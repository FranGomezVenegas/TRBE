/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import databases.DataDataIntegrity;
import databases.Rdbms;
import databases.TblsCnfg;
import databases.TblsData;
import databases.Token;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.certification.AnalysisMethodCertif;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.materialspec.DataSpec;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.samplestructure.DataSample.DataSampleErrorTrapping;
import static functionaljavaa.samplestructure.DataSample.PROCEDURE_REVISIONSAMPLEANALYSISREQUIRED;
import static functionaljavaa.samplestructure.DataSample.PROCEDURE_SAMPLEANALYSIS_AUTHORCANBEREVIEWERTOO;
import static functionaljavaa.samplestructure.DataSampleAnalysis.SAMPLEANALYSIS_STATUS_REVIEWED_WHEN_NO_PROPERTY;
import static functionaljavaa.samplestructure.DataSampleAnalysis.isReadyForRevision;
import static functionaljavaa.samplestructure.DataSampleAnalysis.sampleAnalysisEvaluateStatusAutomatismForReview;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement;
import java.math.BigDecimal;
import java.util.Arrays;
import lbplanet.utilities.LPDate;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class DataSampleAnalysisResult {
    
    String SAMPLEANALYSISRESULT_STATUS_FIRST_WHEN_NO_PROPERTY="BLANK";
    String SAMPLEANALYSISRESULT_STATUS_CANCELED_WHEN_NO_PROPERTY="CANCELED";
    String SAMPLEANALYSISRESULT_STATUS_REVIEWED_WHEN_NO_PROPERTY="REVIEWED";
    String SAMPLEANALYSISRESULT_STATUS_SPEC_EVAL_NOSPEC_WHEN_NO_PROPERTY="NO SPEC";
    String SAMPLEANALYSISRESULT_STATUS_EVAL_NOSPECPARAMLIMIT_WHEN_NO_PROPERTY="NO SPEC LIMIT";
    
    public enum DataSampleAnalysisResultBusinessRules{        
        STATUS_FIRST("sampleAnalysisResult_statusFirst", GlobalVariables.Schemas.DATA.getName()),
        STATUS_REVIEWED("sampleAnalysisResult_statusReviewed", GlobalVariables.Schemas.DATA.getName()),
        STATUS_CANCELED("sampleAnalysisResult_statusCanceled", GlobalVariables.Schemas.DATA.getName()),
        STATUS_SPEC_EVAL_NOSPEC("sampleAnalysisResult_statusSpecEvalNoSpec", GlobalVariables.Schemas.DATA.getName()),
        STATUS_EVAL_NOSPECPARAMLIMIT("sampleAnalysisResult_statusSpecEvalNoSpecParamLimit", GlobalVariables.Schemas.DATA.getName()),
         
        ;
        private DataSampleAnalysisResultBusinessRules(String tgName, String areaNm){
            this.tagName=tgName;
            this.areaName=areaNm;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        
        private final String tagName;
        private final String areaName;
    }
    
    String[] mandatoryFields = null;
    Object[] mandatoryFieldsValue = null;
    DataDataIntegrity labIntChecker = new DataDataIntegrity(); 
    String errorCode ="";
    Object[] errorDetailVariables= new Object[0];
    DataSampleAnalysisResultStrategy sar;
        
    
    /**
     *
     * @param sar
     */
    public DataSampleAnalysisResult(DataSampleAnalysisResultStrategy sar){
      this.sar=sar;
    }    
    /**
     *
     * @param sampleId
     * @param testId
     * @param resultId
     * @param dataSample
     * @return
     */
    
    public Object[] sampleAnalysisResultCancelBack(Integer sampleId, Integer testId, Integer resultId, DataSample dataSample) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String[] diagnoses = new String[6];
        String sampleAnalysisResultStatusCanceled = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultBusinessRules.STATUS_CANCELED.getAreaName(), DataSampleAnalysisResultBusinessRules.STATUS_CANCELED.getTagName());
        if (sampleAnalysisResultStatusCanceled.length()==0)sampleAnalysisResultStatusCanceled=SAMPLEANALYSISRESULT_STATUS_CANCELED_WHEN_NO_PROPERTY;
        String sampleAnalysisResultStatusReviewed = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultBusinessRules.STATUS_REVIEWED.getAreaName(), DataSampleAnalysisResultBusinessRules.STATUS_REVIEWED.getTagName());
        if (sampleAnalysisResultStatusReviewed.length()==0)sampleAnalysisResultStatusReviewed=SAMPLEANALYSISRESULT_STATUS_REVIEWED_WHEN_NO_PROPERTY;
        
        String sampleAnalysisStatusCanceled = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysis.DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSCANCELED.getAreaName(), DataSampleAnalysis.DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSCANCELED.getTagName());
        if (sampleAnalysisStatusCanceled.length()==0)sampleAnalysisStatusCanceled=SAMPLEANALYSISRESULT_STATUS_CANCELED_WHEN_NO_PROPERTY;
        String sampleAnalysisStatusReviewed = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysis.DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSCOMPLETE.getAreaName(), DataSampleAnalysis.DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSREVIEWED.getTagName());
        if (sampleAnalysisStatusReviewed.length()==0)sampleAnalysisStatusReviewed=SAMPLEANALYSIS_STATUS_REVIEWED_WHEN_NO_PROPERTY;

        String sampleStatusCanceled = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSample.DataSampleBusinessRules.SAMPLE_STATUS_CANCELED.getAreaName(), DataSample.DataSampleBusinessRules.SAMPLE_STATUS_CANCELED.getTagName());
        if (sampleStatusCanceled.length()==0)sampleStatusCanceled=DataSample.SAMPLE_STATUS_CANCELED_WHEN_NO_PROPERTY;
        String sampleStatusReviewed = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSample.DataSampleBusinessRules.SAMPLE_STATUS_REVIEWED.getAreaName(), DataSample.DataSampleBusinessRules.SAMPLE_STATUS_REVIEWED.getTagName());
        if (sampleStatusReviewed.length()==0)sampleStatusReviewed=DataSample.SAMPLE_STATUS_COMPLETE_WHEN_NO_PROPERTY;
        Object[] samplesToCancel = new Object[0];
        Object[] testsToCancel = new Object[0];
        Object[] testsSampleToCancel = new Object[0];
        String cancelScope = "";
        Integer cancelScopeId = 0;
        if (sampleId != null) {
            cancelScope = TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName();
            cancelScopeId = sampleId;}        
        if (testId != null) {
            cancelScope = TblsData.SampleAnalysisResult.FLD_TEST_ID.getName();
            cancelScopeId = testId;}
        if (resultId != null) {
            cancelScope = TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName();
            cancelScopeId = resultId;}
        Object[][] objectInfo = null;
        objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), new String[]{cancelScope}, new Object[]{cancelScopeId}, 
                new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()});
        if (objectInfo.length == 0) {
            String[] filter = new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName() + ":" + sampleId.toString() + 
                    TblsData.SampleAnalysisResult.FLD_TEST_ID.getName()+":" + testId.toString() + TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()+":" + resultId.toString()};
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResultNotFound", new Object[]{Arrays.toString(filter), schemaDataName});
//        } else if (LPArray.valueInArray(, objectInfo[0][0].toString()))
//            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResultNotFound", new Object[]{Arrays.toString(filter), schemaDataName});            
        } else {
            for (Integer iResToCancel = 0; iResToCancel < objectInfo.length; iResToCancel++) {
                String currStatus = (String) objectInfo[iResToCancel][0];
                if (!(sampleAnalysisResultStatusCanceled.equalsIgnoreCase(currStatus))) {
                    resultId = (Integer) objectInfo[iResToCancel][1];
                    testId = (Integer) objectInfo[iResToCancel][2];
                    sampleId = (Integer) objectInfo[iResToCancel][3];
                    if (!(sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currStatus))) {
                        diagnoses = (String[]) Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                                new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName()}, new Object[]{sampleAnalysisResultStatusCanceled, currStatus}, 
                                new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0])) {
                            String[] fieldsForAudit = new String[0];
                            fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysisResult.FLD_STATUS + ":" + sampleAnalysisResultStatusCanceled);
                            fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS + ":" + currStatus);
                            SampleAudit smpAudit = new SampleAudit();
                            smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.BACK_FROM_CANCEL.toString(), TblsData.SampleAnalysisResult.TBL.getName(), resultId, sampleId, testId, resultId, fieldsForAudit, null);
                        }
                    } else {
                        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The <*1*> <*2*> has status <*3*> then cannot be canceled in schema <*4*>", 
                            new Object[]{TblsData.SampleAnalysisResult.TBL.getName(), resultId, currStatus,schemaDataName});
                    }
                }
                if ((cancelScope.equalsIgnoreCase(TblsData.Sample.FLD_SAMPLE_ID.getName())) && (!LPArray.valueInArray(samplesToCancel, sampleId))) {
                    samplesToCancel = LPArray.addValueToArray1D(samplesToCancel, sampleId);
                }
                if ((cancelScope.equalsIgnoreCase(TblsData.Sample.FLD_SAMPLE_ID.getName()) || cancelScope.equalsIgnoreCase(TblsData.SampleAnalysis.FLD_TEST_ID.getName())) && (!LPArray.valueInArray(testsToCancel, testId))) {
                    testsToCancel = LPArray.addValueToArray1D(testsToCancel, testId);
                    testsSampleToCancel = LPArray.addValueToArray1D(testsSampleToCancel, sampleId);
                }
            }
        }
        for (Integer iTstToCancel = 0; iTstToCancel < testsToCancel.length; iTstToCancel++) {
            Integer currTest = (Integer) testsToCancel[iTstToCancel];
            if (currTest != null) {
                objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{currTest}, 
                        new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName(), TblsData.SampleAnalysis.FLD_TEST_ID.getName(), 
                            TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName()});
                String currStatus = (String) objectInfo[0][0];
                if ((!(sampleAnalysisStatusCanceled.equalsIgnoreCase(currStatus))) && (!(sampleAnalysisStatusReviewed.equalsIgnoreCase(currStatus))) && (currTest != null)) {
                    diagnoses = (String[]) Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                            new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName()}, new Object[]{sampleAnalysisStatusCanceled, currStatus}, 
                            new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{currTest});
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0])) {
                        String[] fieldsForAudit = new String[0];
                        fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysis.FLD_STATUS.getName() + ":" + sampleAnalysisStatusCanceled);
                        fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName() + ":" + currStatus);
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.BACK_FROM_CANCEL.toString(), TblsData.SampleAnalysis.TBL.getName(), currTest, sampleId, currTest, null, fieldsForAudit, null);
                    }
                } else 
                    return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The <*1*> <*2*> has status <*3*> then cannot be canceled in schema <*4*>", 
                        new Object[]{TblsData.SampleAnalysisResult.TBL.getName(), resultId, currStatus,schemaDataName});
            }
        }
        for (Integer iSmpToCancel = 0; iSmpToCancel < samplesToCancel.length; iSmpToCancel++) {
            Integer currSample = (Integer) samplesToCancel[iSmpToCancel];
            objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{currSample}, 
                    new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName(), TblsData.Sample.FLD_SAMPLE_ID.getName(), TblsData.Sample.FLD_SAMPLE_ID.getName()});
            String currStatus = (String) objectInfo[0][0];
            if ((!(sampleStatusCanceled.equalsIgnoreCase(currStatus))) && (!(sampleStatusReviewed.equalsIgnoreCase(currStatus))) && (currSample != null)) {
                diagnoses = (String[]) Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), 
                        new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName()}, new Object[]{sampleStatusCanceled, currStatus}, 
                        new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{currSample});
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0])) {
                    String[] fieldsForAudit = new String[0];
                    fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.Sample.FLD_STATUS.getName() + ":" + sampleStatusCanceled);
                    fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.Sample.FLD_STATUS_PREVIOUS.getName() + ":" + currStatus);
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.BACK_FROM_CANCEL.toString(), TblsData.Sample.TBL.getName(), currSample, currSample, null, null, fieldsForAudit, null);
                }
            } else {
                diagnoses[5] = "The "+TblsData.Sample.TBL.getName()+" "+currSample+" has status "+currStatus+" then cannot be canceled in schema "+schemaDataName; 
            }
        }
        return diagnoses;
    }
    public Object[] sampleAnalysisResultEntryByAnalysisName(Integer sampleId, String analysisName, Object resultValue, DataSample dataSample) {        
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] analysisNameArr=analysisName.split("\\|");
        Object[] resultValueArr=resultValue.toString().split("\\|");
        Object[] diagn=new Object[]{};
        for (int i=0;i<analysisNameArr.length;i++){
            Object[][] resultInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysisResult.TBL.getName(), 
                new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.FLD_ANALYSIS.getName()}, 
                new Object[]{sampleId, analysisNameArr[i]}, 
                new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString())) return LPArray.array2dTo1d(resultInfo);
            if (resultInfo.length>1) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "This analysis has more than one parameter to enter result", null);
            diagn=sampleAnalysisResultEntry(Integer.valueOf(resultInfo[0][0].toString()), resultValueArr[i],dataSample);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        }
        return diagn;
    }
    
    
    /**
     *
     * @param resultId
     * @param resultValue
     * @param dataSample
     * @return
     */
    public Object[] sampleAnalysisResultEntry(Integer resultId, Object resultValue, DataSample dataSample) {           
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String[] sampleFieldName=new String[0];
        Object[] sampleFieldValue=new Object[0];
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        String specEvalNoSpec = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultBusinessRules.STATUS_SPEC_EVAL_NOSPEC.getAreaName(), DataSampleAnalysisResultBusinessRules.STATUS_SPEC_EVAL_NOSPEC.getTagName());
        if(specEvalNoSpec.length()==0) specEvalNoSpec=SAMPLEANALYSISRESULT_STATUS_SPEC_EVAL_NOSPEC_WHEN_NO_PROPERTY;
        String specEvalNoSpecParamLimit = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultBusinessRules.STATUS_EVAL_NOSPECPARAMLIMIT.getAreaName(), DataSampleAnalysisResultBusinessRules.STATUS_EVAL_NOSPECPARAMLIMIT.getTagName());
        if(specEvalNoSpecParamLimit.length()==0) specEvalNoSpecParamLimit=SAMPLEANALYSISRESULT_STATUS_EVAL_NOSPECPARAMLIMIT_WHEN_NO_PROPERTY;

        String resultStatusDefault = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultBusinessRules.STATUS_FIRST.getAreaName(), DataSampleAnalysisResultBusinessRules.STATUS_FIRST.getTagName());
        if (resultStatusDefault.length()==0)resultStatusDefault=SAMPLEANALYSISRESULT_STATUS_FIRST_WHEN_NO_PROPERTY;
        String resultStatusCanceled = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultBusinessRules.STATUS_CANCELED.getAreaName(), DataSampleAnalysisResultBusinessRules.STATUS_CANCELED.getTagName());
        if (resultStatusCanceled.length()==0)resultStatusCanceled=SAMPLEANALYSISRESULT_STATUS_CANCELED_WHEN_NO_PROPERTY;
        String resultStatusReviewed = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultBusinessRules.STATUS_REVIEWED.getAreaName(), DataSampleAnalysisResultBusinessRules.STATUS_REVIEWED.getTagName());
        if (resultStatusReviewed.length()==0)resultStatusReviewed=SAMPLEANALYSISRESULT_STATUS_REVIEWED_WHEN_NO_PROPERTY;

        String[] fieldsName = new String[0];
        Object[] fieldsValue = new Object[0];
        fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysisResult.FLD_RAW_VALUE.getName());
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, resultValue);
        Object[][] resultData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId}, 
                new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), TblsData.SampleAnalysisResult.FLD_ANALYSIS.getName(), 
                    TblsData.SampleAnalysisResult.FLD_METHOD_NAME.getName(), TblsData.SampleAnalysisResult.FLD_METHOD_VERSION.getName(), TblsData.SampleAnalysisResult.FLD_PARAM_NAME.getName(), 
                    TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_RAW_VALUE.getName(), TblsData.SampleAnalysisResult.FLD_UOM.getName(), 
                    TblsData.SampleAnalysisResult.FLD_UOM_CONVERSION_MODE.getName()});
        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, this.getClass().getSimpleName()+"_SampleAnalysisResultNotFound", new Object[]{resultId.toString(), schemaDataName});
        Integer sampleId = (Integer) resultData[0][0];
        Integer testId = (Integer) resultData[0][1];
        String analysis = (String) resultData[0][2];
        String methodName = (String) resultData[0][3];
        Integer methodVersion = (Integer) resultData[0][4];
        String paramName = (String) resultData[0][5];
        String currResultStatus = (String) resultData[0][6];
        String currRawValue = (String) resultData[0][7];
        String resultUomName = (String) resultData[0][8];
        Object[] userCertified = AnalysisMethodCertif.isUserCertified(methodName, token.getUserName());
        if (!Boolean.valueOf(userCertified[0].toString())) return (Object[]) userCertified[1];
        if (resultStatusReviewed.equalsIgnoreCase(currResultStatus) || resultStatusCanceled.equalsIgnoreCase(currResultStatus)) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, this.getClass().getSimpleName()+"_SampleAnalysisResultLocked", new Object[]{currResultStatus, resultId.toString(), schemaConfigName});
        if ((currRawValue != null) && (currRawValue.equalsIgnoreCase(resultValue.toString()))) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, this.getClass().getSimpleName()+"_SampleAnalysisResultSampleValue", new Object[]{resultId.toString(), schemaDataName, currRawValue});
        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), 
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName(), TblsData.Sample.FLD_CONFIG_CODE.getName(), TblsData.Sample.FLD_CONFIG_CODE_VERSION.getName()});
        if (LPPlatform.LAB_FALSE.equals(sampleData[0][0].toString())) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_NOT_FOUND.getErrorCode(), new Object[]{sampleId.toString(), schemaDataName});
        String sampleConfigCode = (String) sampleData[0][1];
        Integer sampleConfigCodeVersion = (Integer) sampleData[0][2];
sampleFieldName=LPArray.addValueToArray1D(sampleFieldName, TblsData.Sample.FLD_SAMPLE_ID.getName());
sampleFieldValue=LPArray.addValueToArray1D(sampleFieldValue, sampleId);
sampleFieldName=LPArray.addValueToArray1D(sampleFieldName, TblsData.Sample.FLD_CONFIG_CODE.getName());
sampleFieldValue=LPArray.addValueToArray1D(sampleFieldValue, sampleConfigCode);
sampleFieldName=LPArray.addValueToArray1D(sampleFieldName, TblsData.Sample.FLD_CONFIG_CODE_VERSION.getName());
sampleFieldValue=LPArray.addValueToArray1D(sampleFieldValue, sampleConfigCodeVersion);

        Object[][] sampleSpecData = Rdbms.getRecordFieldsByFilter(schemaDataName,  TblsData.Sample.TBL.getName(), 
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.FLD_SPEC_CODE.getName(), TblsData.Sample.FLD_SPEC_CODE_VERSION.getName(), TblsData.Sample.FLD_SPEC_VARIATION_NAME.getName(), 
                    TblsData.Sample.FLD_STATUS.getName()});
        String sampleSpecCode = null;
        Integer sampleSpecCodeVersion = null;
        String sampleSpecVariationName = null;
        if ((sampleSpecData[0][0] != null) && (!LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleSpecData[0][0].toString()))) {
            sampleSpecCode = sampleSpecData[0][0].toString();
            sampleSpecCodeVersion = Integer.valueOf(sampleSpecData[0][1].toString());
            sampleSpecVariationName = sampleSpecData[0][2].toString();
sampleFieldName=LPArray.addValueToArray1D(sampleFieldName, TblsData.Sample.FLD_SPEC_CODE.getName());
sampleFieldValue=LPArray.addValueToArray1D(sampleFieldValue, sampleSpecCode);
sampleFieldName=LPArray.addValueToArray1D(sampleFieldName, TblsData.Sample.FLD_SPEC_CODE_VERSION.getName());
sampleFieldValue=LPArray.addValueToArray1D(sampleFieldValue, sampleSpecCodeVersion);
sampleFieldName=LPArray.addValueToArray1D(sampleFieldName, TblsData.Sample.FLD_SPEC_VARIATION_NAME.getName());
sampleFieldValue=LPArray.addValueToArray1D(sampleFieldValue, sampleSpecVariationName);            
        }
        Object[][] sampleRulesData = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.SampleRules.TBL.getName(), 
                new String[]{TblsCnfg.SampleRules.FLD_CODE.getName(), TblsCnfg.SampleRules.FLD_CODE_VERSION.getName()}, 
                new Object[]{sampleConfigCode, sampleConfigCodeVersion}, new String[]{TblsCnfg.SampleRules.FLD_TEST_ANALYST_REQUIRED.getName()});        
        if ( (sampleRulesData[0][0]!=null) && (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleRulesData[0][0].toString())) ) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleRulesNotFound", 
                new Object[]{TblsCnfg.SampleRules.FLD_ANALYST_ASSIGNMENT_MODE.getName(), sampleConfigCode, sampleConfigCodeVersion, schemaConfigName});
        Boolean analystRequired=false;
        if (sampleRulesData[0][0]!=null){analystRequired = Boolean.valueOf(sampleRulesData[0][0].toString());}
        if (analystRequired) {
            Object[][] testData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                    new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{testId}, 
                    new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName(), TblsData.SampleAnalysis.FLD_ANALYST.getName(), TblsData.SampleAnalysis.FLD_ANALYST_ASSIGNED_ON.getName()});
            if ( (sampleRulesData[0][0]!=null) && (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleRulesData[0][0].toString())) ) {
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisNotFound", new Object[]{testId.toString(), schemaDataName});
            }
            String testAnalyst = (String) testData[0][1];
            if (testAnalyst == null) 
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisRuleAnalystNotAssigned", new Object[]{testId.toString(), sampleConfigCode, sampleConfigCodeVersion.toString(), schemaDataName});
            if (!testAnalyst.equalsIgnoreCase(token.getPersonName())) 
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisRuleOtherAnalystEnterResult", new Object[]{testId.toString(), testAnalyst, token.getPersonName(), schemaDataName});
        }
        String newResultStatus = currResultStatus;
        if (currResultStatus == null) {
            newResultStatus = resultStatusDefault;
        }
        if (newResultStatus.equalsIgnoreCase(resultStatusDefault)) {
            newResultStatus = "ENTERED";
        } else {
            newResultStatus = "RE-ENTERED";
        }
        if (sampleSpecCode == null) {
            Object[] prettyValue = sarRawToPrettyResult(resultValue);
            fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.FLD_SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.FLD_ENTERED_BY.getName()
                , TblsData.SampleAnalysisResult.FLD_ENTERED_ON.getName(), TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_PRETTY_VALUE.getName()});
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEvalNoSpec, token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus, prettyValue[1]});
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                    fieldsName, fieldsValue, new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
            Object[] sampleAuditAdd=new Object[0];
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, ":");
                SampleAudit smpAudit = new SampleAudit();
                sampleAuditAdd = smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), resultId, sampleId, testId, resultId, fieldsForAudit, null);
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId, SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString()));
            }
        }
        Object[][] specLimits = ConfigSpecRule.getSpecLimitLimitIdFromSpecVariables(sampleSpecCode, sampleSpecCodeVersion, sampleSpecVariationName, analysis, methodName, methodVersion, paramName, 
                new String[]{TblsCnfg.SpecLimits.FLD_LIMIT_ID.getName(), TblsCnfg.SpecLimits.FLD_RULE_TYPE.getName(), TblsCnfg.SpecLimits.FLD_RULE_VARIABLES.getName(), TblsCnfg.SpecLimits.FLD_LIMIT_ID.getName(), 
                    TblsCnfg.SpecLimits.FLD_UOM.getName(), TblsCnfg.SpecLimits.FLD_UOM_CONVERSION_MODE.getName()});
        if ((LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimits[0][0].toString())) && (!"Rdbms_NoRecordsFound".equalsIgnoreCase(specLimits[0][4].toString()))) {
            return LPArray.array2dTo1d(specLimits);
        }
        if ((LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimits[0][0].toString())) && ("Rdbms_NoRecordsFound".equalsIgnoreCase(specLimits[0][4].toString()))) {
            Object[] prettyValue = sarRawToPrettyResult(resultValue);
            fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.FLD_SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.FLD_ENTERED_BY.getName()
                , TblsData.SampleAnalysisResult.FLD_ENTERED_ON.getName(), TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_PRETTY_VALUE.getName()});
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEvalNoSpecParamLimit, token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus, prettyValue[1]});
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), fieldsName, fieldsValue, 
                    new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
            Object[] sampleAuditAdd=new Object[0];
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, ":");
                SampleAudit smpAudit = new SampleAudit();
                sampleAuditAdd=smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), resultId, sampleId, testId, resultId, fieldsForAudit, null);
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId, SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString()));
            }
            return diagnoses;
        }
        Integer limitId = (Integer) specLimits[0][0];
        String ruleType = (String) specLimits[0][1];
        String specUomName = (String) specLimits[0][4];
        String specUomConversionMode = (String) specLimits[0][5];
        Boolean requiresUnitsConversion = false;
        BigDecimal resultConverted = null;
        resultUomName = LPNulls.replaceNull(resultUomName);
        if (resultUomName.length()>0) {
            if ((!resultUomName.equalsIgnoreCase(specUomName)) && (specUomConversionMode == null || specUomConversionMode.equalsIgnoreCase("DISABLED") || ((!specUomConversionMode.contains(resultUomName)) && !specUomConversionMode.equalsIgnoreCase("ALL")))) 
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResult_ConversionNotAllowed", new Object[]{specUomConversionMode, specUomName, resultUomName,  limitId.toString(), schemaDataName});            
            if (resultUomName.equalsIgnoreCase(specUomName)){
                requiresUnitsConversion = false;
                resultConverted=new BigDecimal(resultValue.toString());
            }else{                
                requiresUnitsConversion = true;
                UnitsOfMeasurement uom = new UnitsOfMeasurement(new BigDecimal(resultValue.toString()), resultUomName);
                uom.convertValue(specUomName);
                if (!uom.getConvertedFine()) 
                    return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResult_ConverterFALSE", new Object[]{resultId.toString(), uom.getConversionErrorDetail()[3].toString(), schemaDataName});
                resultConverted = uom.getConvertedQuantity();
            }
        }
        DataSpec resChkSpec = new DataSpec();
        Object[] resSpecEvaluation = null;
        ConfigSpecRule specRule = new ConfigSpecRule();
        specRule.specLimitsRule(limitId, null);
        if (specRule.getRuleIsQualitative()){        
                resSpecEvaluation = resChkSpec.resultCheck((String) resultValue, specRule.getQualitativeRule(), 
                        specRule.getQualitativeRuleValues(), specRule.getQualitativeRuleSeparator(), specRule.getQualitativeRuleListName());
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation[0].toString())) {
                    return resSpecEvaluation;
                }      
                fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.FLD_SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.FLD_SPEC_EVAL_DETAIL.getName()
                    , TblsData.SampleAnalysisResult.FLD_ENTERED_BY.getName(), TblsData.SampleAnalysisResult.FLD_ENTERED_ON.getName(), TblsData.SampleAnalysisResult.FLD_STATUS.getName(), 
                    TblsData.SampleAnalysisResult.FLD_LIMIT_ID.getName()});
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{resSpecEvaluation[resSpecEvaluation.length - 1], resSpecEvaluation[resSpecEvaluation.length - 2]
                    , token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus, limitId});
                
                Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                        fieldsName, fieldsValue, new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
                Object[] sampleAuditAdd=new Object[0];
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, ":");
                    SampleAudit smpAudit = new SampleAudit();
                    sampleAuditAdd=smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), resultId, sampleId, testId, resultId, fieldsForAudit, null);
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId, SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString()));
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                  if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_UPON_CONTROL))
                    this.sar.sarControlAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                  if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_OOS))
                    this.sar.sarOOSAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                }                    
                return diagnoses;
        }
        if (specRule.getRuleIsQuantitative()){
                try{
                    resultValue= new BigDecimal(resultValue.toString());
                }catch(Exception e){
                    return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSampleAnalysisResult_ValueNotNumericForQuantitativeParam", new Object[]{resultValue, specRule.getRuleRepresentation(), limitId.toString(), schemaDataName});            
                }
                if (specRule.getQuantitativeHasControl()){
                    if (requiresUnitsConversion) {
                        resSpecEvaluation = resChkSpec.resultCheck(resultConverted, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinControl(), specRule.getMaxControl(), specRule.getMinControlIsStrict(), specRule.getMaxControlIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                    } else {
                        resSpecEvaluation = resChkSpec.resultCheck((BigDecimal) resultValue, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinControl(), specRule.getMaxControl(), specRule.getMinControlIsStrict(), specRule.getMaxControlIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                    }
                } else {
                    if (requiresUnitsConversion) {
                        resSpecEvaluation = resChkSpec.resultCheck(resultConverted, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                    } else {
                        resSpecEvaluation = resChkSpec.resultCheck((BigDecimal) resultValue, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                    }
                }
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation[0].toString())) {
                    return resSpecEvaluation;
                }
                String specEval = (String) resSpecEvaluation[resSpecEvaluation.length - 1];
                String specEvalDetail = (String) resSpecEvaluation[resSpecEvaluation.length - 2];
                if (requiresUnitsConversion) specEvalDetail = specEvalDetail + " in " + specUomName;

                fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.FLD_SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.FLD_SPEC_EVAL_DETAIL.getName()
                    , TblsData.SampleAnalysisResult.FLD_ENTERED_BY.getName(), TblsData.SampleAnalysisResult.FLD_ENTERED_ON.getName(), TblsData.SampleAnalysisResult.FLD_STATUS.getName(), 
                    TblsData.SampleAnalysisResult.FLD_LIMIT_ID.getName()});
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEval, specEvalDetail, token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus, limitId});
                
                Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                        fieldsName, fieldsValue, new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
                Object[] sampleAuditAdd=new Object[0];
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, ":");
                    SampleAudit smpAudit = new SampleAudit();
                    sampleAuditAdd=smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), resultId, sampleId, testId, resultId, fieldsForAudit, null);
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) 
                    DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId, SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString()));
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                  if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_UPON_CONTROL))
                    this.sar.sarControlAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                  if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_OOS))
                    this.sar.sarOOSAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                }                    
//                UserMethod.newUserMethodEntry(procInstanceName, userName, userRole, analysis, methodName, methodVersion, sampleId, testId, appSessionId);
                return diagnoses;
        }
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResult_SpecRuleNotImplemented", new Object[]{resultId.toString(), schemaDataName, ruleType});
    }
    
    /**
     *
     * @param resultId
     * @param newuom
     * @param dataSample
     * @return
     */
    public Object[] sarChangeUom(Integer resultId, String newuom, DataSample dataSample) {       
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        Object[][] resultInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId}, 
                new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), TblsData.SampleAnalysisResult.FLD_PARAM_NAME.getName(), TblsData.SampleAnalysisResult.FLD_UOM.getName(), 
                    TblsData.SampleAnalysisResult.FLD_RAW_VALUE.getName(), TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName(),
                    TblsData.SampleAnalysisResult.FLD_UOM_CONVERSION_MODE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString())) return LPArray.array2dTo1d(resultInfo);
        String paramName = resultInfo[0][1].toString();
        String curruom = resultInfo[0][2].toString();
        String currValue = resultInfo[0][3].toString();
        Integer testId = Integer.valueOf(resultInfo[0][4].toString());
        Integer sampleId = Integer.valueOf(resultInfo[0][5].toString());
        String specUomConversionMode = resultInfo[0][6].toString();
        if (specUomConversionMode == null || specUomConversionMode.equalsIgnoreCase("DISABLED") || ((!specUomConversionMode.contains(newuom)) && !specUomConversionMode.equalsIgnoreCase("ALL"))) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResult_ConversionNotAllowed", new Object[]{specUomConversionMode, newuom, curruom, resultId.toString(), schemaDataName});
        UnitsOfMeasurement uom = new UnitsOfMeasurement(new BigDecimal(currValue), curruom);
        uom.convertValue(newuom);
        if (!uom.getConvertedFine()) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResult_ConverterFALSE", new Object[]{resultId.toString(), uom.getConversionErrorDetail()[3].toString(), schemaDataName});
        BigDecimal resultConverted = uom.getConvertedQuantity();
        String[] updFieldNames = new String[]{TblsData.SampleAnalysisResult.FLD_RAW_VALUE.getName(), TblsData.SampleAnalysisResult.FLD_UOM.getName()};
        Object[] updFieldValues = new Object[]{resultConverted.toString(), newuom};
        Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                updFieldNames, updFieldValues, new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateRecordFieldsByFilter[0].toString())) return updateRecordFieldsByFilter;
        SampleAudit smpAudit = new SampleAudit();
        String auditActionName = SampleAudit.SampleAnalysisResultAuditEvents.UOM_CHANGED.toString() + " FOR " + paramName;
        Object[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(updFieldNames, updFieldValues, ":");
        smpAudit.sampleAuditAdd(auditActionName, TblsData.SampleAnalysisResult.TBL.getName(), resultId, sampleId, testId, resultId, fieldsForAudit, null);
        return updateRecordFieldsByFilter;
    }

    /**
     *
     * @param rawValue
     * @return
     */
    public Object[] sarRawToPrettyResult(Object rawValue) {
        return new Object[]{LPPlatform.LAB_TRUE, rawValue};
    }

    /**
     *
     * @param sampleId
     * @param testId
     * @param resultId
     * @return
     */
    public Object[] sampleAnalysisResultUnCancel(Integer sampleId, Integer testId, Integer resultId) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        Object[] diagnoses = new Object[7];
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());

        String sampleAnalysisResultStatusCanceled = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultBusinessRules.STATUS_CANCELED.getAreaName(), DataSampleAnalysisResultBusinessRules.STATUS_CANCELED.getTagName());
        if (sampleAnalysisResultStatusCanceled.length()==0)sampleAnalysisResultStatusCanceled=SAMPLEANALYSISRESULT_STATUS_CANCELED_WHEN_NO_PROPERTY;
        String sampleAnalysisStatusCanceled = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysis.DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSCANCELED.getAreaName(), DataSampleAnalysis.DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSCANCELED.getTagName());
        if (sampleAnalysisStatusCanceled.length()==0)sampleAnalysisStatusCanceled=SAMPLEANALYSISRESULT_STATUS_CANCELED_WHEN_NO_PROPERTY;
        String sampleStatusCanceled = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSample.DataSampleBusinessRules.SAMPLE_STATUS_CANCELED.getAreaName(), DataSample.DataSampleBusinessRules.SAMPLE_STATUS_CANCELED.getTagName());
        if (sampleStatusCanceled.length()==0)sampleStatusCanceled=DataSample.SAMPLE_STATUS_CANCELED_WHEN_NO_PROPERTY;


        String cancelScope = "";
        Integer cancelScopeId = 0;
        if (sampleId != null) {
            cancelScope = TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName();
            cancelScopeId = sampleId;
        }
        if (testId != null) {
            cancelScope = TblsData.SampleAnalysisResult.FLD_TEST_ID.getName();
            cancelScopeId = testId;
        }
        if (resultId != null) {
            cancelScope = TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName();
            cancelScopeId = resultId;
        }
        Object[] samplesToUnCancel = new Object[0];
        Object[] testsToUnCancel = new Object[0];
        String[] diagPerResult = new String[0];

        Object[][] resultInfo = null;
        resultInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                new String[]{cancelScope}, new Object[]{cancelScopeId}, 
                new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName(), TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), 
                    TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString())) {
            String[] filter = new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName() + ":" + LPNulls.replaceNull(sampleId).toString() + " " + TblsData.SampleAnalysisResult.FLD_TEST_ID.getName() + ":" + LPNulls.replaceNull(testId).toString() +
                    " " + TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName() + ":" + LPNulls.replaceNull(resultId).toString()};
            errorCode = DataSampleErrorTrapping.SAMPLE_NOT_FOUND.getErrorCode();
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, new Object[]{Arrays.toString(filter), schemaDataName});
            //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, errorCode, errorDetailVariables);
        }else{
            for (Integer iResToCancel = 0; iResToCancel < resultInfo.length; iResToCancel++) {
                String currResultStatus = (String) resultInfo[iResToCancel][0];
                String statusPrevious = (String) resultInfo[iResToCancel][1];
                resultId = (Integer) resultInfo[iResToCancel][2];
                testId = (Integer) resultInfo[iResToCancel][3];
                sampleId = (Integer) resultInfo[iResToCancel][4];
                if (!(sampleAnalysisResultStatusCanceled.equalsIgnoreCase(currResultStatus))) {
                    diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleUnCancel_StatusNotExpected", new Object[]{resultInfo[0][0].toString(), sampleAnalysisResultStatusCanceled, schemaDataName});
                    diagPerResult = LPArray.addValueToArray1D(diagPerResult, TblsData.SampleAnalysisResult.TBL.getName()+" " + resultId.toString() + " not uncanceled because current status is " + currResultStatus);
                } else {
                    resultId = (Integer) resultInfo[iResToCancel][2];
                    diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                            new String[]{TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName(), TblsData.SampleAnalysisResult.FLD_STATUS.getName()}, new Object[]{sampleAnalysisResultStatusCanceled, statusPrevious}, 
                            new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        String[] fieldsForAudit = new String[0];
                        fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName() + ":" + sampleAnalysisResultStatusCanceled);
                        fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysisResult.FLD_STATUS.getName() + ":" + statusPrevious);
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_UNCANCELED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), resultId, sampleId, testId, resultId, fieldsForAudit, null);
                    }
                    diagPerResult = LPArray.addValueToArray1D(diagPerResult, "Result " + resultId.toString() + " UNCANCELED ");
                }
                if ((cancelScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName())) && (!LPArray.valueInArray(samplesToUnCancel, sampleId))) {
                    samplesToUnCancel = LPArray.addValueToArray1D(samplesToUnCancel, sampleId);
                }
                if ((cancelScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()) || cancelScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.FLD_TEST_ID.getName())) && (!LPArray.valueInArray(testsToUnCancel, testId))) {
                    testsToUnCancel = LPArray.addValueToArray1D(testsToUnCancel, testId);
                }
            }
        }
        if (testsToUnCancel.length==0 && cancelScope.equalsIgnoreCase(TblsData.SampleAnalysis.FLD_TEST_ID.getName()))
            testsToUnCancel = LPArray.addValueToArray1D(testsToUnCancel, cancelScopeId);                
        for (Integer iTstToUnCancel = 0; iTstToUnCancel < testsToUnCancel.length; iTstToUnCancel++) {
            Integer currTest = (Integer) testsToUnCancel[iTstToUnCancel];
            Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                    new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{currTest}, 
                    new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName(), TblsData.SampleAnalysis.FLD_TEST_ID.getName(), 
                        TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName()});
            String currStatus = (String) objectInfo[0][0];
            String currPrevStatus = (String) objectInfo[0][1];
            if ((sampleAnalysisStatusCanceled.equalsIgnoreCase(currStatus)) && (currTest != null)) {
                diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                        new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName()}, 
                        new Object[]{currPrevStatus, sampleAnalysisResultStatusCanceled}, new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{currTest});
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    String[] fieldsForAudit = new String[0];
                    fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName() + ":" + sampleAnalysisResultStatusCanceled);
                    fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysis.FLD_STATUS.getName() + ":" + currPrevStatus);
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_UNCANCELED.toString(), TblsData.SampleAnalysis.TBL.getName(), currTest, sampleId, currTest, null, fieldsForAudit, null);
                }
            } else {
                diagnoses[5] = "The "+TblsData.SampleAnalysis.TBL.getName()+" "+currTest+" has status "+currStatus+" then cannot be canceled in schema "+schemaDataName;                 
            }
        }
        if (samplesToUnCancel.length==0 && cancelScope.equalsIgnoreCase(TblsData.Sample.FLD_SAMPLE_ID.getName()))
            samplesToUnCancel = LPArray.addValueToArray1D(samplesToUnCancel, cancelScopeId);        
        for (Integer iSmpToUnCancel = 0; iSmpToUnCancel < samplesToUnCancel.length; iSmpToUnCancel++) {
            Integer currSample = (Integer) samplesToUnCancel[iSmpToUnCancel];
            Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{currSample}, 
                    new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName()});
            String currStatus = (String) objectInfo[0][0];
            String currPrevStatus = (String) objectInfo[0][1];
            if ((sampleStatusCanceled.equalsIgnoreCase(currStatus)) && (currSample != null)) {
                diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), 
                        new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName()}, new Object[]{currPrevStatus, sampleAnalysisResultStatusCanceled}, 
                        new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{currSample});
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    String[] fieldsForAudit = new String[0];
                    fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.Sample.FLD_STATUS_PREVIOUS.getName() + ":" + sampleAnalysisResultStatusCanceled);
                    fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.Sample.FLD_STATUS.getName() + ":" + currPrevStatus);
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_UNCANCELED.toString(), TblsData.Sample.TBL.getName(), currSample, currSample, null, null, fieldsForAudit, null);
                }
            } else {
                diagnoses[5] = "The "+TblsData.Sample.TBL.getName()+" "+currSample+" has status "+currStatus+" then cannot be canceled in schema "+schemaDataName;
            }
        }
        diagnoses[5] = Arrays.toString(diagPerResult);
        return diagnoses;
    }

    /**
     *
     * @param sampleId
     * @param testId
     * @param resultId
     * @return
     */
    public Object[] sampleAnalysisResultCancel(Integer sampleId, Integer testId, Integer resultId) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        Object[] diagnoses = new Object[7];
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());

        String sampleAnalysisResultStatusCanceled = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultBusinessRules.STATUS_CANCELED.getAreaName(), DataSampleAnalysisResultBusinessRules.STATUS_CANCELED.getTagName());
        if (sampleAnalysisResultStatusCanceled.length()==0)sampleAnalysisResultStatusCanceled=SAMPLEANALYSISRESULT_STATUS_CANCELED_WHEN_NO_PROPERTY;
        String sampleAnalysisResultStatusReviewed = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultBusinessRules.STATUS_REVIEWED.getAreaName(), DataSampleAnalysisResultBusinessRules.STATUS_REVIEWED.getTagName());
        if (sampleAnalysisResultStatusReviewed.length()==0)sampleAnalysisResultStatusReviewed=SAMPLEANALYSISRESULT_STATUS_REVIEWED_WHEN_NO_PROPERTY;
        
        String sampleAnalysisStatusCanceled = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysis.DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSCANCELED.getAreaName(), DataSampleAnalysis.DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSCANCELED.getTagName());
        if (sampleAnalysisStatusCanceled.length()==0)sampleAnalysisStatusCanceled=SAMPLEANALYSISRESULT_STATUS_CANCELED_WHEN_NO_PROPERTY;
        String sampleAnalysisStatusReviewed = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysis.DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSCOMPLETE.getAreaName(), DataSampleAnalysis.DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSREVIEWED.getTagName());
        if (sampleAnalysisStatusReviewed.length()==0)sampleAnalysisStatusReviewed=SAMPLEANALYSIS_STATUS_REVIEWED_WHEN_NO_PROPERTY;

        String sampleStatusCanceled = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSample.DataSampleBusinessRules.SAMPLE_STATUS_CANCELED.getAreaName(), DataSample.DataSampleBusinessRules.SAMPLE_STATUS_CANCELED.getTagName());
        if (sampleStatusCanceled.length()==0)sampleStatusCanceled=DataSample.SAMPLE_STATUS_CANCELED_WHEN_NO_PROPERTY;
        String sampleStatusReviewed = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSample.DataSampleBusinessRules.SAMPLE_STATUS_REVIEWED.getAreaName(), DataSample.DataSampleBusinessRules.SAMPLE_STATUS_REVIEWED.getTagName());
        if (sampleStatusReviewed.length()==0)sampleStatusReviewed=DataSample.SAMPLE_STATUS_COMPLETE_WHEN_NO_PROPERTY;

        Object[] samplesToCancel = new Object[0];
        Object[] testsToCancel = new Object[0];
        Object[] testsSampleToCancel = new Object[0];
        String cancelScope = "";
        String cancelScopeTable = "";
        Integer cancelScopeId = 0;
        if (sampleId != null) {
            cancelScope = TblsData.Sample.FLD_SAMPLE_ID.getName();
            cancelScopeTable = TblsData.Sample.TBL.getName();
            cancelScopeId = sampleId;
        }
        if (testId != null) {
            cancelScope = TblsData.SampleAnalysis.FLD_TEST_ID.getName();
            cancelScopeTable = TblsData.SampleAnalysis.TBL.getName();
            cancelScopeId = testId;
        }
        if (resultId != null) {
            cancelScope = TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName();
            cancelScopeTable = TblsData.SampleAnalysisResult.TBL.getName();
            cancelScopeId = resultId;
        }
        Object[][] objectInfo = null;
        objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, cancelScopeTable, 
                new String[]{cancelScope}, new Object[]{cancelScopeId}, 
                new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectInfo[0][0].toString())) {
            String[] filter = new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName() + ":" + LPNulls.replaceNull(sampleId).toString() + TblsData.SampleAnalysisResult.FLD_TEST_ID.getName() + ":" + LPNulls.replaceNull(testId).toString() 
                    + TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName() + ":" + LPNulls.replaceNull(resultId).toString()};
            //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_NOT_FOUND.getErrorCode(), new Object[]{Arrays.toString(filter), schemaDataName});
        } else {
            objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, cancelScopeTable, 
                    new String[]{cancelScope}, new Object[]{cancelScopeId}, 
                    new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()});
            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(objectInfo[0][0].toString())) {
                for (Integer iResToCancel = 0; iResToCancel < objectInfo.length; iResToCancel++) {
                    String currStatus = (String) objectInfo[iResToCancel][0];
                    if (!(sampleAnalysisResultStatusCanceled.equalsIgnoreCase(currStatus))) {
                        resultId = (Integer) objectInfo[iResToCancel][1];
                        testId = (Integer) objectInfo[iResToCancel][2];
                        sampleId = (Integer) objectInfo[iResToCancel][3];
                        if (!(sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currStatus))) {
                            diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                                    new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName()}, new Object[]{sampleAnalysisResultStatusCanceled, currStatus}, 
                                    new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
                            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                                String[] fieldsForAudit = new String[0];
                                fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysisResult.FLD_STATUS.getName() + ":" + sampleAnalysisResultStatusCanceled);
                                fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName() + ":" + currStatus);
                                SampleAudit smpAudit = new SampleAudit();
                                smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_CANCELED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), resultId, sampleId, testId, resultId, fieldsForAudit, null);
                            }
                        } else 
                            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResultCancelation_StatusNotExpected", new Object[]{resultId.toString(), currStatus, schemaDataName});
                    }
                    if ((cancelScope.equalsIgnoreCase(TblsData.Sample.FLD_SAMPLE_ID.getName())) && (!LPArray.valueInArray(samplesToCancel, sampleId)))
                        samplesToCancel = LPArray.addValueToArray1D(samplesToCancel, sampleId);
                    if ((cancelScope.equalsIgnoreCase(TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName()) || cancelScope.equalsIgnoreCase(TblsData.SampleAnalysis.FLD_TEST_ID.getName())) && (!LPArray.valueInArray(testsToCancel, testId))) {
                        testsToCancel = LPArray.addValueToArray1D(testsToCancel, testId);
                        testsSampleToCancel = LPArray.addValueToArray1D(testsSampleToCancel, sampleId);
                    }
                }
            }
        }
        if (testsToCancel.length==0 && cancelScope.equalsIgnoreCase(TblsData.SampleAnalysis.FLD_TEST_ID.getName()))
            testsToCancel = LPArray.addValueToArray1D(testsToCancel, cancelScopeId);        
        for (Integer iTstToCancel = 0; iTstToCancel < testsToCancel.length; iTstToCancel++) {
            Integer currTest = (Integer) testsToCancel[iTstToCancel];
            objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                    new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{currTest}, new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName()});
            String currStatus = (String) objectInfo[0][0];
            if ((!(sampleAnalysisStatusCanceled.equalsIgnoreCase(currStatus))) && (!(sampleAnalysisStatusReviewed.equalsIgnoreCase(currStatus))) && (currTest != null)) {
                diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                        new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName()}, new Object[]{sampleAnalysisStatusCanceled, currStatus}, 
                        new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{currTest});
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    String[] fieldsForAudit = new String[0];
                    fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysis.FLD_STATUS.getName() + ":" + sampleAnalysisStatusCanceled);
                    fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName() + ":" + currStatus);
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_CANCELED.toString(), TblsData.SampleAnalysis.TBL.getName(), currTest, sampleId, currTest, null, fieldsForAudit, null);
                }
            } else 
                diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisCancelation_StatusNotExpected", new Object[]{LPNulls.replaceNull(currTest), currStatus, schemaDataName});            
        }
        if (samplesToCancel.length==0 && cancelScope.equalsIgnoreCase(TblsData.Sample.FLD_SAMPLE_ID.getName()))
            samplesToCancel = LPArray.addValueToArray1D(samplesToCancel, cancelScopeId);
        for (Integer iSmpToCancel = 0; iSmpToCancel < samplesToCancel.length; iSmpToCancel++) {
            Integer currSample = (Integer) samplesToCancel[iSmpToCancel];
            objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), 
                    new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{currSample}, 
                    new String[]{TblsData.Sample.FLD_STATUS.getName()});
            String currStatus = (String) objectInfo[0][0];
            if ((!(sampleStatusCanceled.equalsIgnoreCase(currStatus))) && (!(sampleStatusReviewed.equalsIgnoreCase(currStatus))) && (currSample != null)) {
                diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), 
                        new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName()}, new Object[]{sampleStatusCanceled, currStatus}, 
                        new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{currSample});
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    String[] fieldsForAudit = new String[0];
                    fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.Sample.FLD_STATUS.getName() + ":" + sampleStatusCanceled);
                    fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.Sample.FLD_STATUS_PREVIOUS.getName() + ":" + currStatus);
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_CANCELED.toString(), TblsData.Sample.TBL.getName(), currSample, currSample, null, null, fieldsForAudit, null);
                }
            }else 
                diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisCancelation_StatusNotExpected", new Object[]{LPNulls.replaceNull(currSample), currStatus, schemaDataName});
        }
        return diagnoses;
    }
    /**
     *
     * @param sampleId
     * @param analysisName
     * @return
     */
    public Object[] sampleResultReviewBySampleAndAnalysis(Integer sampleId, String analysisName) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] analysisNameArr=analysisName.split("\\|");
        Object[] diagn=new Object[]{};
        for (String analysisNameArr1 : analysisNameArr) {
            Object[][] testInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysis.TBL.getName(), new String[]{TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName(), TblsData.SampleAnalysis.FLD_ANALYSIS.getName()}, new Object[]{sampleId, analysisNameArr1}, new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testInfo[0][0].toString())) return LPArray.array2dTo1d(testInfo);
            if (testInfo.length>1) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "This analysis has more than one parameter to enter result", null);
            diagn=sampleResultReview(null, Integer.valueOf(testInfo[0][0].toString()), null);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) 
                return diagn;            
        }
        return diagn;
    }
    public Object[] sampleResultReview(Integer sampleId, Integer testId, Integer resultId) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        Object[] diagnoses = new Object[7];
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String sampleAnalysisResultStatusCanceled = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultBusinessRules.STATUS_CANCELED.getAreaName(), DataSampleAnalysisResultBusinessRules.STATUS_CANCELED.getTagName());
        if (sampleAnalysisResultStatusCanceled.length()==0)sampleAnalysisResultStatusCanceled=SAMPLEANALYSISRESULT_STATUS_CANCELED_WHEN_NO_PROPERTY;
        String sampleAnalysisResultStatusReviewed = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultBusinessRules.STATUS_REVIEWED.getAreaName(), DataSampleAnalysisResultBusinessRules.STATUS_REVIEWED.getTagName());
        if (sampleAnalysisResultStatusReviewed.length()==0)sampleAnalysisResultStatusReviewed=SAMPLEANALYSISRESULT_STATUS_REVIEWED_WHEN_NO_PROPERTY;
        
        Object[] samplesToReview = new Object[0];
        Object[] testsToReview = new Object[0];
        Object[] testsSampleToReview = new Object[0];
        String[] fieldsToRetrieve = new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName(),
            TblsData.SampleAnalysisResult.FLD_ENTERED_BY.getName()};
        String reviewScope = "";
        String reviewScopeTable="";
        Integer reviewScopeId = 0;
        if (sampleId != null) {
            reviewScope = TblsData.Sample.FLD_SAMPLE_ID.getName();
            reviewScopeTable = TblsData.Sample.TBL.getName();
            reviewScopeId = sampleId;
            Object[] sampleRevisionByTestingGroupReviewed = DataSampleRevisionTestingGroup.isSampleRevisionByTestingGroupReviewed(sampleId);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleRevisionByTestingGroupReviewed[0].toString())) return sampleRevisionByTestingGroupReviewed;            
        }
        if (testId != null) {
            reviewScope = TblsData.SampleAnalysisResult.FLD_TEST_ID.getName();
            reviewScopeTable = TblsData.SampleAnalysis.TBL.getName();
            reviewScopeId = testId;
            Object[] readyForRevision = isReadyForRevision(testId);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(readyForRevision[0].toString())) return readyForRevision;
        }
        if (resultId != null) {
            reviewScope = TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName();
            reviewScopeTable = TblsData.SampleAnalysisResult.TBL.getName();
            reviewScopeId = resultId;
        }
reviewScopeTable = TblsData.SampleAnalysisResult.TBL.getName();
        Object[][] objectInfoForRevisionCheck = Rdbms.getRecordFieldsByFilter(schemaDataName, reviewScopeTable, 
                new String[]{reviewScope}, new Object[]{reviewScopeId}, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectInfoForRevisionCheck[0][0].toString()) || objectInfoForRevisionCheck.length == 0)             
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResultNotFound", new Object[]{resultId.toString(), schemaDataName});
if (reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName())){
        if (sampleAnalysisResultStatusReviewed.equalsIgnoreCase(objectInfoForRevisionCheck[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_AlreadyReviewed", new Object[]{reviewScope, reviewScopeId, schemaDataName});
}
        Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                new String[]{reviewScope}, new Object[]{reviewScopeId}, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectInfo[0][0].toString()) || objectInfo.length == 0)             
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResultNotFound", new Object[]{resultId.toString(), schemaDataName});            
        Object[] isSampleAnalysisAuthorCanReviewEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, "procedure", PROCEDURE_SAMPLEANALYSIS_AUTHORCANBEREVIEWERTOO);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isSampleAnalysisAuthorCanReviewEnable[0].toString())){
            if (LPArray.valueInArray(LPArray.getColumnFromArray2D(objectInfo, 4), token.getPersonName()))
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "SampleAnalysisAuthorCannotBeReviewer", null);
        }     
            for (Integer iResToCancel = 0; iResToCancel < objectInfo.length; iResToCancel++) {
                String currStatus = (String) objectInfo[iResToCancel][0];
                if (!(sampleAnalysisResultStatusCanceled.equalsIgnoreCase(currStatus))) {
                    resultId = Integer.valueOf(objectInfo[iResToCancel][1].toString());
                    testId = Integer.valueOf(objectInfo[iResToCancel][2].toString());
                    sampleId = Integer.valueOf(objectInfo[iResToCancel][3].toString());
                    if (!(sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currStatus))) {
                        diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                                new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName()}, 
                                new Object[]{sampleAnalysisResultStatusReviewed, currStatus}, 
                                new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), TblsData.SampleAnalysisResult.FLD_STATUS.getName()+" not in-"}, new Object[]{resultId, sampleAnalysisResultStatusCanceled+"-"+sampleAnalysisResultStatusReviewed});
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                            String[] fieldsForAudit = new String[0];
                            fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysisResult.FLD_STATUS.getName() + ":" + sampleAnalysisResultStatusReviewed);
                            fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName() + ":" + currStatus);
                            SampleAudit smpAudit = new SampleAudit();
                            smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_REVIEWED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), resultId, sampleId, testId, resultId, fieldsForAudit, null);
                        }
    // Parece que no tiene sentido ¿Si no hay nada pendiente entonces no sigue?
    //                         else 
    //                            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResultNothingPending", 
    //                                    new Object[]{resultId.toString(), schemaDataName});
                    } 
    // Si ya está revisado entonces no volver a revisarlo ... pero ... ¿debe hacer return o continuar?
    //                        else 
    //                        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResultNotReviable", new Object[]{resultId.toString(), schemaDataName, sampleAnalysisResultStatusReviewed});
                }
            }
 
            if ((reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName())) && (!LPArray.valueInArray(samplesToReview, sampleId))) {
                samplesToReview = LPArray.addValueToArray1D(samplesToReview, sampleId);
            }
            if ((reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()) || reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.FLD_TEST_ID.getName())) && (!LPArray.valueInArray(testsToReview, testId))) {
                testsToReview = LPArray.addValueToArray1D(testsToReview, testId);
                testsSampleToReview = LPArray.addValueToArray1D(testsSampleToReview, sampleId);
            }
        //}
//        if ((reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName())) || (reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.FLD_TEST_ID.getName()))){
            for (Integer itestsToReview = 0; itestsToReview < testsToReview.length; itestsToReview++) {
                testId = Integer.valueOf(testsToReview[itestsToReview].toString());
                Object[][] testInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                        new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{testId}, 
                        new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName()});
                if (testInfo.length == 0) {            
                    return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisNotFound", new Object[]{testId.toString(), schemaDataName});
                } else {
                    String currStatus=testInfo[0][0].toString();                
                    if (!(sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currStatus))) {
                        diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                                new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName(), TblsData.SampleAnalysis.FLD_READY_FOR_REVISION.getName()}, 
                                new Object[]{sampleAnalysisResultStatusReviewed, currStatus, false}, 
                                new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName(), TblsData.SampleAnalysis.FLD_STATUS.getName()+" not in-"}, new Object[]{testId, sampleAnalysisResultStatusCanceled+"-"+sampleAnalysisResultStatusReviewed});
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                            String[] fieldsForAudit = new String[0];
                            fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysis.FLD_STATUS.getName() + ":" + sampleAnalysisResultStatusReviewed);
                            fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName() + ":" + currStatus);
                            fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysis.FLD_READY_FOR_REVISION.getName() + ":" + "false");                     
                            SampleAudit smpAudit = new SampleAudit();
                            smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_REVIEWED.toString(), TblsData.SampleAnalysis.TBL.getName(), testId, sampleId, testId, null, fieldsForAudit, null);
                            sampleAnalysisEvaluateStatusAutomatismForReview(sampleId, testId);
                        }
                    }
                }
            }
//        }            
//        if (reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName())){
            for (Integer isamplesToReview = 0; isamplesToReview < samplesToReview.length; isamplesToReview++) {
                sampleId = Integer.valueOf(samplesToReview[isamplesToReview].toString());
                Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), 
                        new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                        new String[]{TblsData.Sample.FLD_STATUS.getName()});
                if (sampleInfo.length == 0) 
                    return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleNotFound", new Object[]{sampleId.toString(), schemaDataName});

                Object[] isRevisionSampleAnalysisRequired=LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, "procedure", PROCEDURE_REVISIONSAMPLEANALYSISREQUIRED);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionSampleAnalysisRequired[0].toString())){            
                    Object[] isallsampleAnalysisReviewed = DataSampleAnalysis.isAllsampleAnalysisReviewed(sampleId, new String[]{}, new Object[]{});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isallsampleAnalysisReviewed[0].toString())) return isallsampleAnalysisReviewed;
                }

                String currStatus=sampleInfo[0][0].toString();                
                if (!(sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currStatus))) {
                    diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), 
                            new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName(), TblsData.Sample.FLD_READY_FOR_REVISION.getName()}, 
                            new Object[]{sampleAnalysisResultStatusReviewed, currStatus, false}, 
                            new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName(), TblsData.Sample.FLD_STATUS.getName()+" not in-"}, new Object[]{sampleId, sampleAnalysisResultStatusCanceled+"-"+sampleAnalysisResultStatusReviewed});
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        String[] fieldsForAudit = new String[0];
                        fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.Sample.FLD_STATUS.getName() + ":" + sampleAnalysisResultStatusReviewed);
                        fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.Sample.FLD_STATUS_PREVIOUS.getName() + ":" + currStatus);
                        fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.Sample.FLD_READY_FOR_REVISION.getName() + ":" + "false");
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_REVIEWED.toString(), TblsData.Sample.TBL.getName(), sampleId, sampleId, null, null, fieldsForAudit, null);
                    }
                }
            }                        
//        }
        if (diagnoses[0]==null) diagnoses=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "NothingDone", null);
        return diagnoses;
    }    
}
