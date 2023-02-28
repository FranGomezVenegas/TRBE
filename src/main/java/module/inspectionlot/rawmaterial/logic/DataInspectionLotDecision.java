/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.logic;

import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.InspLotRMAPIactionsEndpoints;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMConfig;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.features.Token;
import functionaljavaa.audit.LotAudit;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.DataInspLotErrorTrapping;
import functionaljavaa.samplestructure.DataSampleStructureStatuses;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
/**
 *
 * @author User 
 */
public class DataInspectionLotDecision {
    public InternalMessage lotTakeDecision(String lotName, String decision, String[] fieldName, Object[] fieldValue) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String[] dataLotFlds=new String[]{TblsInspLotRMData.Lot.LOT_CONFIG_NAME.getName(), TblsInspLotRMData.Lot.LOT_CONFIG_VERSION.getName()};
        String[] configLotDecisionFlds=EnumIntTableFields.getAllFieldNames(TblsInspLotRMConfig.TablesInspLotRMConfig.LOT_DECISION_RULES.getTableFields());
        
        Object[][] lotInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), 
                new String[]{TblsInspLotRMData.Lot.NAME.getName()}, new Object[]{lotName}, 
                dataLotFlds);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) 
            new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        
        String templateName=lotInfo[0][LPArray.valuePosicInArray(dataLotFlds, TblsInspLotRMData.Lot.LOT_CONFIG_NAME.getName())].toString();
        Integer templateVersion=Integer.valueOf(lotInfo[0][LPArray.valuePosicInArray(dataLotFlds, TblsInspLotRMData.Lot.LOT_CONFIG_VERSION.getName())].toString());
        
        Object[][] configLotDecisionInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsInspLotRMConfig.TablesInspLotRMConfig.LOT_DECISION_RULES.getTableName(), 
                new String[]{TblsInspLotRMConfig.LotDecisionRules.CODE.getName(), TblsInspLotRMConfig.LotDecisionRules.CODE_VERSION.getName()}, new Object[]{templateName, templateVersion}, 
                configLotDecisionFlds);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(configLotDecisionInfo[0][0].toString())) 
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);

        // , String template, Integer templateVersion
        InternalMessage diagn=decisionTypePasses(lotName, decision, dataLotFlds, lotInfo[0], configLotDecisionFlds, configLotDecisionInfo[0]);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn.getDiagnostic())) return diagn;

        diagn=sampleAndTestCheck(lotName, templateName, templateVersion, decision, dataLotFlds, lotInfo[0], configLotDecisionFlds, configLotDecisionInfo[0]);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn.getDiagnostic())) return diagn;
        
        return lotDecisionRecordCreateOrUpdate(lotName, decision);
        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "NotImplementedYet", null);
    } 
    
    public InternalMessage decisionTypePasses(String lotName, String decision, String[] dataLotFlds, Object[] lotInfo, String[] configLotDecFlds, Object[] configLotDecInfo){
        String decisionsList=configLotDecInfo[LPArray.valuePosicInArray(configLotDecFlds, TblsInspLotRMConfig.LotDecisionRules.DECISIONS_LIST.getName())].toString();
        if (decisionsList==null || decisionsList.length()==0) return 
                new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.DataInspLotErrorTrapping.NO_DECISION_LIST_DEFINED, null);
        if (LPArray.valueInArray(decisionsList.split("\\|"), decision)) 
            return new InternalMessage(LPPlatform.LAB_TRUE, Rdbms.RdbmsSuccess.RDBMS_TABLE_FOUND, null);
        else
            return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_DECISION_NOT_ACCEPTED_VALUE, new Object[]{decision, Arrays.toString(decisionsList.split("\\|")), lotName});
    }
    
    public InternalMessage sampleAndTestCheck(String lotName, String templateName, Integer templateVersion, String decision, String[] dataLotFlds, Object[] lotInfo, String[] configLotDecFlds, Object[] configLotDecInfo){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String testRevisionRequired=configLotDecInfo[LPArray.valuePosicInArray(configLotDecFlds, TblsInspLotRMConfig.LotDecisionRules.SAMPLE_ANALYSIS_REVISION_REQUIRED.getName())].toString();
        String sampleRevisionRequired=configLotDecInfo[LPArray.valuePosicInArray(configLotDecFlds, TblsInspLotRMConfig.LotDecisionRules.SAMPLE_REVISION_REQUIRED.getName())].toString();
        if ((testRevisionRequired==null || !Boolean.valueOf(testRevisionRequired)) && (sampleRevisionRequired==null || !Boolean.valueOf(sampleRevisionRequired)) ) 
            return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.DataInspLotErrorTrapping.NO_DECISION_LIST_DEFINED, null);
        else{
            String[] sampleAndSampleAnalysisFlds=new String[]{TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_ID.getName(), TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_STATUS.getName(), TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.TEST_ID.getName(), TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.TEST_STATUS.getName()};
            String sampleStatusReviewed = sampleStatusReviewed=DataSampleStructureStatuses.SampleStatuses.REVIEWED.getStatusCode(""); //Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.SAMPLE_STATUS_REVIEWED.getAreaName(), DataSampleBusinessRules.SAMPLE_STATUS_REVIEWED.getTagName());
            
            Object[][] sampleAndSampleAnalysisInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.ViewsInspLotRMData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW.getViewName(), 
                    new String[]{TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.LOT_NAME.getName()}, new Object[]{lotName}, 
                    sampleAndSampleAnalysisFlds);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAndSampleAnalysisInfo[0][0].toString())) 
                return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
            if (Boolean.valueOf(testRevisionRequired)){            
                Object[] sampleAnalysisStatuses = LPArray.getColumnFromArray2D(sampleAndSampleAnalysisInfo, LPArray.valuePosicInArray(sampleAndSampleAnalysisFlds, TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.TEST_STATUS.getName()));
                for (Object curSmpAnaStatus: sampleAnalysisStatuses){
                    if (curSmpAnaStatus==null || curSmpAnaStatus.toString().length()==0) return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_HAS_ONE_SAMPLE_ANALYSIS_WITH_NO_STATUS, null);
                    if (!sampleStatusReviewed.equalsIgnoreCase(curSmpAnaStatus.toString())) return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_HAS_NOTREVIEWED_SAMPLEANALYSIS, new Object[]{lotName, procInstanceName});
                }
            }
            if (Boolean.valueOf(sampleRevisionRequired)){            
                Object[] sampleStatuses = LPArray.getColumnFromArray2D(sampleAndSampleAnalysisInfo, LPArray.valuePosicInArray(sampleAndSampleAnalysisFlds, TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_STATUS.getName()));
                for (Object curSmpStatus: sampleStatuses){
                    if (curSmpStatus==null || curSmpStatus.toString().length()==0) return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_HAS_ONE_SAMPLE_ANALYSIS_WITH_NO_STATUS, null);
                    if (!sampleStatusReviewed.equalsIgnoreCase(curSmpStatus.toString())) return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_HAS_NOTREVIEWED_SAMPLE, null);
                }
            }
            return new InternalMessage(LPPlatform.LAB_TRUE, Rdbms.RdbmsSuccess.RDBMS_TABLE_FOUND, null);
        }
        //return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "", null);
    }
    public InternalMessage lotDecisionRecordCreateOrUpdate(String lotName, String decision){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String[] lotFieldName=new String[]{};
        Object[] lotFieldValue=new Object[]{};
        Object[] errorDetailVariables=new Object[]{};
        Object[] diagnoses=new Object[]{};

        if (decision!=null && decision.length()>0){
            lotFieldName = LPArray.addValueToArray1D(lotFieldName, new String[]{TblsInspLotRMData.LotDecision.DECISION.getName(), TblsInspLotRMData.LotDecision.DECISION_TAKEN_BY.getName(), TblsInspLotRMData.LotDecision.DECISION_TAKEN_ON.getName()});    
            lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, new Object[]{decision, token.getPersonName(), LPDate.getCurrentTimeStamp()});                                         
        }

        Object[] lotExists=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT_DECISION.getTableName(), 
                new String[]{TblsInspLotRMData.LotDecision.LOT_NAME.getName()}, new Object[]{lotName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(lotExists[0].toString())){      
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsInspLotRMData.LotDecision.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
            diagnoses=Rdbms.updateRecordFieldsByFilter(TblsInspLotRMData.TablesInspLotRMData.LOT_DECISION,
                EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMData.TablesInspLotRMData.LOT_DECISION, lotFieldName), lotFieldValue, sqlWhere, null);
        }else{
            lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.LotDecision.LOT_NAME.getName());    
            lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, lotName);                         
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMData.TablesInspLotRMData.LOT_DECISION, lotFieldName, lotFieldValue);
            if (!insertRecordInTable.getRunSuccess()){
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, insertRecordInTable.getNewRowId());
                return new InternalMessage(LPPlatform.LAB_FALSE, DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD, errorDetailVariables);
            }                                
        }
        Object[] fieldsOnLogLot = LPArray.joinTwo1DArraysInOneOf1DString(lotFieldName, lotFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR);
//        diagnoses = LPArray.addValueToArray1D(diagnoses, diagnoses[diagnoses.length-1]);

//        if (Rdbms.TBL_NO_KEY.equalsIgnoreCase(diagnoses[diagnoses.length-1].toString())){return diagnoses;}
        if (decision!=null && decision.length()>0){
            LotAudit lotAudit = new LotAudit();            
            lotAudit.lotAuditAdd(InspLotRMAPIactionsEndpoints.LOT_TAKE_DECISION.getAuditActionName(), 
                    TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName, lotName, fieldsOnLogLot, null);
            return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_TAKE_DECISION, new Object[]{lotName, decision, procInstanceName});
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, Rdbms.RdbmsSuccess.RDBMS_TABLE_FOUND, null);
    }
}
