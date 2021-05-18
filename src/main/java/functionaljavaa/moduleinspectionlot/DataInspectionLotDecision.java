/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleinspectionlot;

import com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.InspLotRMAPIEndpoints;
import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMConfig;
import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMData;
import databases.Rdbms;
import databases.Token;
import functionaljavaa.audit.LotAudit;
import functionaljavaa.moduleinspectionlot.ModuleInspLotRMenum.DataInspLotErrorTrapping;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.samplestructure.DataSample.DataSampleBusinessRules;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User 
 */
public class DataInspectionLotDecision {
    public Object[] lotTakeDecision(String lotName, String decision, String[] fieldName, Object[] fieldValue) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String[] dataLotFlds=new String[]{TblsInspLotRMData.Lot.FLD_LOT_CONFIG_NAME.getName(), TblsInspLotRMData.Lot.FLD_LOT_CONFIG_VERSION.getName()};
        String[] configLotDecisionFlds=TblsInspLotRMConfig.LotDecisionRules.getAllFieldNames();
        
        Object[][] lotInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.Lot.TBL.getName(), 
                new String[]{TblsInspLotRMData.Lot.FLD_NAME.getName()}, new Object[]{lotName}, 
                dataLotFlds);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) return lotInfo;
        
        String templateName=lotInfo[0][LPArray.valuePosicInArray(dataLotFlds, TblsInspLotRMData.Lot.FLD_LOT_CONFIG_NAME.getName())].toString();
        Integer templateVersion=Integer.valueOf(lotInfo[0][LPArray.valuePosicInArray(dataLotFlds, TblsInspLotRMData.Lot.FLD_LOT_CONFIG_VERSION.getName())].toString());
        
        Object[][] configLotDecisionInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsInspLotRMConfig.LotDecisionRules.TBL.getName(), 
                new String[]{TblsInspLotRMConfig.LotDecisionRules.FLD_CODE.getName(), TblsInspLotRMConfig.LotDecisionRules.FLD_CODE_VERSION.getName()}, new Object[]{templateName, templateVersion}, 
                configLotDecisionFlds);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(configLotDecisionInfo[0][0].toString())) return configLotDecisionInfo;

        // , String template, Integer templateVersion
        Object[] diagn=decisionTypePasses(lotName, decision, dataLotFlds, lotInfo[0], configLotDecisionFlds, configLotDecisionInfo[0]);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;

        diagn=sampleAndTestCheck(lotName, templateName, templateVersion, decision, dataLotFlds, lotInfo[0], configLotDecisionFlds, configLotDecisionInfo[0]);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        
        return lotDecisionRecordCreateOrUpdate(lotName, decision);
        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "NotImplementedYet", null);
    } 
    
    public Object[] decisionTypePasses(String lotName, String decision, String[] dataLotFlds, Object[] lotInfo, String[] configLotDecFlds, Object[] configLotDecInfo){
        String decisionsList=configLotDecInfo[LPArray.valuePosicInArray(configLotDecFlds, TblsInspLotRMConfig.LotDecisionRules.FLD_DECISIONS_LIST.getName())].toString();
        if (decisionsList==null || decisionsList.length()==0) return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "NoDecisionsListDefined", null);
        if (LPArray.valueInArray(decisionsList.split("\\|"), decision)) 
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "", null);
        else
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "lotDecision_notAcceptedValue", new Object[]{decision, Arrays.toString(decisionsList.split("\\|")), lotName});
    }
    
    public Object[] sampleAndTestCheck(String lotName, String templateName, Integer templateVersion, String decision, String[] dataLotFlds, Object[] lotInfo, String[] configLotDecFlds, Object[] configLotDecInfo){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String testRevisionRequired=configLotDecInfo[LPArray.valuePosicInArray(configLotDecFlds, TblsInspLotRMConfig.LotDecisionRules.FLD_SAMPLE_ANALYSIS_REVISION_REQUIRED.getName())].toString();
        String sampleRevisionRequired=configLotDecInfo[LPArray.valuePosicInArray(configLotDecFlds, TblsInspLotRMConfig.LotDecisionRules.FLD_SAMPLE_REVISION_REQUIRED.getName())].toString();
        if ((testRevisionRequired==null || !Boolean.valueOf(testRevisionRequired)) && (sampleRevisionRequired==null || !Boolean.valueOf(sampleRevisionRequired)) ) 
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "NoDecisionsListDefined", null);
        else{
            String[] sampleAndSampleAnalysisFlds=new String[]{TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLE_ID.getName(), TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLE_STATUS.getName(), TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.FLD_TEST_ID.getName(), TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.FLD_TEST_STATUS.getName()};
            String sampleStatusReviewed = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.SAMPLE_STATUS_REVIEWED.getAreaName(), DataSampleBusinessRules.SAMPLE_STATUS_REVIEWED.getTagName());
            
            Object[][] sampleAndSampleAnalysisInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(), 
                    new String[]{TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.FLD_LOT_NAME.getName()}, new Object[]{lotName}, 
                    sampleAndSampleAnalysisFlds);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAndSampleAnalysisInfo[0][0].toString())) return sampleAndSampleAnalysisInfo;
            if (Boolean.valueOf(testRevisionRequired)){            
                Object[] sampleAnalysisStatuses = LPArray.getColumnFromArray2D(sampleAndSampleAnalysisInfo, LPArray.valuePosicInArray(sampleAndSampleAnalysisFlds, TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.FLD_TEST_STATUS.getName()));
                for (Object curSmpAnaStatus: sampleAnalysisStatuses){
                    if (curSmpAnaStatus==null || curSmpAnaStatus.toString().length()==0) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "lotHasOneSampleAnalysisWithNoStatus", null);
                    if (!sampleStatusReviewed.equalsIgnoreCase(curSmpAnaStatus.toString())) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "lotHasNotReviewedSampleAnalysis", new Object[]{lotName, procInstanceName});
                }
            }
            if (Boolean.valueOf(sampleRevisionRequired)){            
                Object[] sampleStatuses = LPArray.getColumnFromArray2D(sampleAndSampleAnalysisInfo, LPArray.valuePosicInArray(sampleAndSampleAnalysisFlds, TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLE_STATUS.getName()));
                for (Object curSmpStatus: sampleStatuses){
                    if (curSmpStatus==null || curSmpStatus.toString().length()==0) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "lotHasOneSampleWithNoStatus", null);
                    if (!sampleStatusReviewed.equalsIgnoreCase(curSmpStatus.toString())) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "lotHasNotReviewedSamples", null);
                }
            }
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "", null);
        }
        //return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "", null);
    }
    public Object[] lotDecisionRecordCreateOrUpdate(String lotName, String decision){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String[] lotFieldName=new String[]{};
        Object[] lotFieldValue=new Object[]{};
        Object[] errorDetailVariables=new Object[]{};
        Object[] diagnoses=new Object[]{};

        if (decision!=null && decision.length()>0){
            lotFieldName = LPArray.addValueToArray1D(lotFieldName, new String[]{TblsInspLotRMData.LotDecision.FLD_DECISION.getName(), TblsInspLotRMData.LotDecision.FLD_DECISION_TAKEN_BY.getName(), TblsInspLotRMData.LotDecision.FLD_DECISION_TAKEN_ON.getName()});    
            lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, new Object[]{decision, token.getPersonName(), LPDate.getCurrentTimeStamp()});                                         
        }

        Object[] lotExists=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.LotDecision.TBL.getName(), 
                new String[]{TblsInspLotRMData.LotDecision.FLD_LOT_NAME.getName()}, new Object[]{lotName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(lotExists[0].toString())){      
            diagnoses=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.LotDecision.TBL.getName(), 
                lotFieldName, lotFieldValue, 
                new String[]{TblsInspLotRMData.LotDecision.FLD_LOT_NAME.getName()}, new Object[]{lotName});
        }else{
            lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.LotDecision.FLD_LOT_NAME.getName());    
            lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, lotName);                         
            diagnoses = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.LotDecision.TBL.getName(), 
                lotFieldName, lotFieldValue);
            if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, diagnoses[diagnoses.length-2]);
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD.getErrorCode(), errorDetailVariables);
            }                                
        }
        Object[] fieldsOnLogLot = LPArray.joinTwo1DArraysInOneOf1DString(lotFieldName, lotFieldValue, ":");
        diagnoses = LPArray.addValueToArray1D(diagnoses, diagnoses[diagnoses.length-1]);

//        if (Rdbms.TBL_NO_KEY.equalsIgnoreCase(diagnoses[diagnoses.length-1].toString())){return diagnoses;}
        if (decision!=null && decision.length()>0){
            LotAudit lotAudit = new LotAudit();            
            lotAudit.lotAuditAdd(InspLotRMAPIEndpoints.LOT_TAKE_DECISION.getAuditActionName(), 
                    TblsInspLotRMData.Lot.TBL.getName(), lotName, lotName, fieldsOnLogLot, null);
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "LotDecisionTaken", new Object[]{lotName, decision, procInstanceName});
        }
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "", null);
    }
}
