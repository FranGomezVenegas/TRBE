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
import functionaljavaa.modulesample.DataModuleSampleAnalysis;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleErrorTrapping;
import static functionaljavaa.samplestructure.DataSampleAnalysis.isReadyForRevision;
import static functionaljavaa.samplestructure.DataSampleAnalysis.sampleAnalysisEvaluateStatusAutomatismForReview;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleAnalysisErrorTrapping;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleAnalysisResultErrorTrapping;
import functionaljavaa.samplestructure.DataSampleStructureRevisionRules.DataSampleStructureRevisionRls;
import static functionaljavaa.samplestructure.DataSampleStructureRevisionRules.reviewSampleAnalysisRulesAllowed;
import functionaljavaa.samplestructure.DataSampleStructureStatuses.SampleStatuses;
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
        
        String sampleStatusCanceled = SampleStatuses.CANCELED.getStatusCode("");
        String sampleStatusReviewed = SampleStatuses.REVIEWED.getStatusCode("");
        String sampleAnalysisStatusCanceled = DataSampleStructureStatuses.SampleAnalysisStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisStatusReviewed = DataSampleStructureStatuses.SampleAnalysisStatuses.REVIEWED.getStatusCode("");

        String sampleAnalysisResultStatusCanceled = DataSampleStructureStatuses.SampleAnalysisResultStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisResultStatusReviewed = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REVIEWED.getStatusCode("");

        Object[] samplesToCancel = new Object[0];
        Object[] testsToCancel = new Object[0];
        Object[] testsSampleToCancel = new Object[0];
        Object[] scopeInfo = getScope(sampleId, testId, resultId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scopeInfo[0].toString())) return scopeInfo;

        String cancelScope = scopeInfo[0].toString();
        Integer cancelScopeId = (Integer) scopeInfo[1];
        Object[][] objectInfo = null;
        objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), new String[]{cancelScope}, new Object[]{cancelScopeId}, 
                new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()});
        if (objectInfo.length == 0) {
            String[] filter = new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + sampleId.toString() + 
                    TblsData.SampleAnalysisResult.FLD_TEST_ID.getName()+LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + testId.toString() + TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()+LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + resultId.toString()};
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.NOT_FOUND.getErrorCode(), new Object[]{Arrays.toString(filter), schemaDataName});
//        } else if (LPArray.valueInArray(, objectInfo[0][0].toString()))
//            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.NOT_FOUND.getErrorCode(), new Object[]{Arrays.toString(filter), schemaDataName});            
        } else {
            for (Integer iResToCancel = 0; iResToCancel < objectInfo.length; iResToCancel++) {
                String currStatus = (String) objectInfo[iResToCancel][0];
                if (!(sampleAnalysisResultStatusCanceled.equalsIgnoreCase(currStatus))) {
                    String rsltIdStr=objectInfo[iResToCancel][1].toString();
                    resultId = Integer.valueOf(rsltIdStr);                
                    testId = (Integer) objectInfo[iResToCancel][2];
                    sampleId = (Integer) objectInfo[iResToCancel][3];
                    if (!(sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currStatus))) {
                        String[] updFldName=new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName()};
                        Object[] updFldValue=new Object[]{sampleAnalysisResultStatusCanceled, currStatus};
                        diagnoses = (String[]) Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                                updFldName, updFldValue,
                                new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0])) {
                            String[] fieldsForAudit = new String[0];
                            fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysisResult.FLD_STATUS + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + sampleAnalysisResultStatusCanceled);
                            fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + currStatus);
                            SampleAudit smpAudit = new SampleAudit();
                            smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.BACK_FROM_CANCEL.toString(), TblsData.SampleAnalysisResult.TBL.getName(), 
                                resultId, sampleId, testId, resultId, fieldsForAudit, null);
                        }
                    } else {
                        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.RESULT_CANNOT_BE_CANCELLED.getErrorCode(), 
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
                    String[] updFldName=new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName()};
                    Object[] updFldValue=new Object[]{sampleAnalysisStatusCanceled, currStatus};
                    diagnoses = (String[]) Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                            updFldName, updFldValue, 
                            new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{currTest});
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0])) {
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.BACK_FROM_CANCEL.toString(), TblsData.SampleAnalysis.TBL.getName(), 
                            currTest, sampleId, currTest, null, updFldName, updFldValue);
                    }
                } else 
                    return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.RESULT_CANNOT_BE_CANCELLED.getErrorCode(), 
                        new Object[]{TblsData.SampleAnalysisResult.TBL.getName(), resultId, currStatus,schemaDataName});
            }
        }
        for (Integer iSmpToCancel = 0; iSmpToCancel < samplesToCancel.length; iSmpToCancel++) {
            Integer currSample = (Integer) samplesToCancel[iSmpToCancel];
            objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{currSample}, 
                    new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName(), TblsData.Sample.FLD_SAMPLE_ID.getName(), TblsData.Sample.FLD_SAMPLE_ID.getName()});
            String currStatus = (String) objectInfo[0][0];
            if ((!(sampleStatusCanceled.equalsIgnoreCase(currStatus))) && (!(sampleStatusReviewed.equalsIgnoreCase(currStatus))) && (currSample != null)) {
                String[] updFldName=new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName()};
                Object[] updFldValue=new Object[]{sampleStatusCanceled, currStatus};
                diagnoses = (String[]) Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), 
                        updFldName, updFldValue, 
                        new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{currSample});
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0])) {
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.BACK_FROM_CANCEL.toString(), TblsData.Sample.TBL.getName(), 
                        currSample, currSample, null, null, updFldName, updFldValue);
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
            if (resultInfo.length>1) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.ANALYSIS_HAS_SOME_PARAMETERS.getErrorCode(), null);
            Object[] actionDiagnoses=sampleAnalysisResultEntry(Integer.valueOf(resultInfo[0][0].toString()), resultValueArr[i],dataSample);
            diagn=(Object[]) actionDiagnoses[0];   
            if (actionDiagnoses.length>1){
                Object[] auditDiagn=(Object[]) actionDiagnoses[1];
                String pAuditId=(String)auditDiagn[auditDiagn.length-1];
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        }
        return diagn;
    }
    
    public Object[] sampleAnalysisResultEntry(Integer resultId, Object resultValue, DataSample dataSample) {           
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String[] sampleFieldName=new String[0];
        Object[] sampleFieldValue=new Object[0];
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());

        String specEvalNoSpec = DataSampleStructureStatuses.SampleAnalysisResultSpecEvalStatuses.NO_SPEC.getStatusCode("");
        String specEvalNoSpecParamLimit = DataSampleStructureStatuses.SampleAnalysisResultSpecEvalStatuses.NO_SPEC_LIMIT.getStatusCode("");

        String resultStatusDefault = DataSampleStructureStatuses.SampleAnalysisResultStatuses.getStatusFirstCode();
        String resultStatusCanceled = DataSampleStructureStatuses.SampleAnalysisResultStatuses.CANCELED.getStatusCode("");
        String resultStatusReviewed = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REVIEWED.getStatusCode("");
        String resultStatusEntered = DataSampleStructureStatuses.SampleAnalysisResultStatuses.ENTERED.getStatusCode("");
        String resultStatusReEntered = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REENTERED.getStatusCode("");

        String[] fieldsName = new String[0];
        Object[] fieldsValue = new Object[0];
        fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysisResult.FLD_RAW_VALUE.getName());
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, resultValue);
        Object[][] resultData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId}, 
                new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), TblsData.SampleAnalysisResult.FLD_ANALYSIS.getName(), 
                    TblsData.SampleAnalysisResult.FLD_METHOD_NAME.getName(), TblsData.SampleAnalysisResult.FLD_METHOD_VERSION.getName(), TblsData.SampleAnalysisResult.FLD_PARAM_NAME.getName(), 
                    TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_RAW_VALUE.getName(), TblsData.SampleAnalysisResult.FLD_UOM.getName(), 
                    TblsData.SampleAnalysisResult.FLD_UOM_CONVERSION_MODE.getName(), TblsData.SampleAnalysisResult.FLD_LIMIT_ID.getName()});
        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) 
            return new Object[]{LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.NOT_FOUND.getErrorCode(), new Object[]{resultId.toString(), schemaDataName})};
        Integer sampleId = (Integer) resultData[0][0];
        Integer testId = (Integer) resultData[0][1];
        String analysis = (String) resultData[0][2];
        String methodName = (String) resultData[0][3];
        Integer methodVersion = (Integer) resultData[0][4];
        String paramName = (String) resultData[0][5];
        String currResultStatus = (String) resultData[0][6];
        String currRawValue = (String) resultData[0][7];
        String resultUomName = (String) resultData[0][8];
        Integer limitId =-999;
        if (resultData[0][9]!=null && resultData[0][9].toString().length()>0)
            limitId = (Integer) resultData[0][9];
        
        Object[] ifUserCertificationEnabled = AnalysisMethodCertif.isUserCertificationEnabled();
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(ifUserCertificationEnabled[0].toString())){
            Object[] userCertified = AnalysisMethodCertif.isUserCertified(methodName, token.getUserName());
            if (!Boolean.valueOf(userCertified[0].toString())) return (Object[]) new Object[]{userCertified[1]};
        }        
        if (resultStatusReviewed.equalsIgnoreCase(currResultStatus) || resultStatusCanceled.equalsIgnoreCase(currResultStatus)) 
            return new Object[]{LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.RESULT_LOCKED.getErrorCode(), new Object[]{currResultStatus, resultId.toString(), schemaConfigName})};
        if ((currRawValue != null) && (currRawValue.equalsIgnoreCase(resultValue.toString()))) 
            return new Object[]{LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.SAME_RESULT_VALUE.getErrorCode(), new Object[]{resultId.toString(), schemaDataName, currRawValue})};
        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), 
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName(), TblsData.Sample.FLD_CONFIG_CODE.getName(), TblsData.Sample.FLD_CONFIG_CODE_VERSION.getName()});
        if (LPPlatform.LAB_FALSE.equals(sampleData[0][0].toString())) 
            return new Object[]{LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_NOT_FOUND.getErrorCode(), new Object[]{sampleId.toString(), schemaDataName})};
        String sampleConfigCode = (String) sampleData[0][1];
        Integer sampleConfigCodeVersion = (Integer) sampleData[0][2];
        sampleFieldName=LPArray.addValueToArray1D(sampleFieldName, new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName(), TblsData.Sample.FLD_CONFIG_CODE.getName(), TblsData.Sample.FLD_CONFIG_CODE_VERSION.getName()});
        sampleFieldValue=LPArray.addValueToArray1D(sampleFieldValue, new Object[]{sampleId, sampleConfigCode, sampleConfigCodeVersion});

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
            sampleFieldName=LPArray.addValueToArray1D(sampleFieldName, new String[]{TblsData.Sample.FLD_SPEC_CODE.getName(), TblsData.Sample.FLD_SPEC_CODE_VERSION.getName(), TblsData.Sample.FLD_SPEC_VARIATION_NAME.getName()});
            sampleFieldValue=LPArray.addValueToArray1D(sampleFieldValue, new Object[]{sampleSpecCode, sampleSpecCodeVersion, sampleSpecVariationName});
        }
        Object[][] sampleRulesData = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.SampleRules.TBL.getName(), 
                new String[]{TblsCnfg.SampleRules.FLD_CODE.getName(), TblsCnfg.SampleRules.FLD_CODE_VERSION.getName()}, 
                new Object[]{sampleConfigCode, sampleConfigCodeVersion}, new String[]{TblsCnfg.SampleRules.FLD_TEST_ANALYST_REQUIRED.getName()});        
        if ( (sampleRulesData[0][0]!=null) && (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleRulesData[0][0].toString())) ) 
            return new Object[]{LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_RULES_NOT_FOUND.getErrorCode(), 
                new Object[]{TblsCnfg.SampleRules.FLD_ANALYST_ASSIGNMENT_MODE.getName(), sampleConfigCode, sampleConfigCodeVersion, schemaConfigName})};
        Boolean analystRequired=false;
        if (sampleRulesData[0][0]!=null){analystRequired = Boolean.valueOf(sampleRulesData[0][0].toString());}
        if (analystRequired) {
            Object[][] testData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                    new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{testId}, 
                    new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName(), TblsData.SampleAnalysis.FLD_ANALYST.getName(), TblsData.SampleAnalysis.FLD_ANALYST_ASSIGNED_ON.getName()});
            if ( (sampleRulesData[0][0]!=null) && (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleRulesData[0][0].toString())) ) {
                return new Object[]{LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSIS_NOTFOUND.getErrorCode(), new Object[]{testId.toString(), schemaDataName})};
            }
            String testAnalyst = (String) testData[0][1];
            if (testAnalyst == null) 
                return new Object[]{LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.RULE_ANALYST_NOT_ASSIGNED.getErrorCode(), new Object[]{testId.toString(), sampleConfigCode, sampleConfigCodeVersion.toString(), schemaDataName})};
            if (!testAnalyst.equalsIgnoreCase(token.getPersonName())) 
                return new Object[]{LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.RULE_OTHERANALYSIS_ENTER_RESULT.getErrorCode(), new Object[]{testId.toString(), testAnalyst, token.getPersonName(), schemaDataName})};
        }
        String newResultStatus = currResultStatus;
        if (currResultStatus == null) {
            newResultStatus = resultStatusDefault;
        }
        if ((newResultStatus.equalsIgnoreCase(DataSampleStructureStatuses.SampleAnalysisResultStatuses.BLANK.getStatusCode(""))) || (newResultStatus.equalsIgnoreCase(resultStatusDefault))){
            newResultStatus=resultStatusEntered;
        } else {
            newResultStatus=resultStatusReEntered;
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
                SampleAudit smpAudit = new SampleAudit();
                sampleAuditAdd = smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), 
                    resultId, sampleId, testId, resultId, fieldsName, fieldsValue);
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId, SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString()));
            }
        }
        Object[][] specLimits = ConfigSpecRule.getSpecLimitLimitIdFromSpecVariables(sampleSpecCode, sampleSpecCodeVersion, sampleSpecVariationName, analysis, methodName, methodVersion, paramName, 
                new String[]{TblsCnfg.SpecLimits.FLD_LIMIT_ID.getName(), TblsCnfg.SpecLimits.FLD_RULE_TYPE.getName(), TblsCnfg.SpecLimits.FLD_RULE_VARIABLES.getName(), TblsCnfg.SpecLimits.FLD_LIMIT_ID.getName(), 
                    TblsCnfg.SpecLimits.FLD_UOM.getName(), TblsCnfg.SpecLimits.FLD_UOM_CONVERSION_MODE.getName()});
        if ((LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimits[0][0].toString())) && (!Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND.getErrorCode().equalsIgnoreCase(specLimits[0][4].toString()))) {
            return new Object[]{LPArray.array2dTo1d(specLimits)};
        }
        if ((LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimits[0][0].toString())) && (Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND.getErrorCode().equalsIgnoreCase(specLimits[0][4].toString()))) {
            Object[] prettyValue = sarRawToPrettyResult(resultValue);
            fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.FLD_SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.FLD_ENTERED_BY.getName()
                , TblsData.SampleAnalysisResult.FLD_ENTERED_ON.getName(), TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_PRETTY_VALUE.getName()});
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEvalNoSpecParamLimit, token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus, prettyValue[1]});
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), fieldsName, fieldsValue, 
                    new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
            Object[] sampleAuditAdd=new Object[0];
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                SampleAudit smpAudit = new SampleAudit();
                sampleAuditAdd=smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), 
                        resultId, sampleId, testId, resultId, fieldsName, fieldsValue);
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId, SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString()));
            }
            return new Object[]{diagnoses};
        }
        Integer specLimitId = (Integer) specLimits[0][0];
        String ruleType = (String) specLimits[0][1];
        String specUomName = (String) specLimits[0][4];
        String specUomConversionMode = (String) specLimits[0][5];
        Boolean requiresUnitsConversion = false;
        BigDecimal resultConverted = null;
        resultUomName = LPNulls.replaceNull(resultUomName);
        if (resultUomName.length()>0) {
            if ((!resultUomName.equalsIgnoreCase(specUomName)) && (specUomConversionMode == null || specUomConversionMode.equalsIgnoreCase("DISABLED") || ((!specUomConversionMode.contains(resultUomName)) && !specUomConversionMode.equalsIgnoreCase("ALL")))) 
                return new Object[]{LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.CONVERSION_NOT_ALLOWED.getErrorCode(), new Object[]{specUomConversionMode, specUomName, resultUomName,  specLimitId.toString(), schemaDataName})};            
            if (resultUomName.equalsIgnoreCase(specUomName)){
                requiresUnitsConversion = false;
                resultConverted=new BigDecimal(resultValue.toString());
            }else{                
                requiresUnitsConversion = true;
                UnitsOfMeasurement uom = new UnitsOfMeasurement(new BigDecimal(resultValue.toString()), resultUomName);
                uom.convertValue(specUomName);
                if (!uom.getConvertedFine()) 
                    return new Object[]{LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.CONVERTER_RETURNED_FALSE.getErrorCode(), new Object[]{resultId.toString(), uom.getConversionErrorDetail()[3].toString(), schemaDataName})};
                resultConverted = uom.getConvertedQuantity();
            }
        }
        DataSpec resChkSpec = new DataSpec();
        Object[] resSpecEvaluation = null;
        ConfigSpecRule specRule = new ConfigSpecRule();
        specRule.specLimitsRule(specLimitId, null);
        if (specRule.getRuleIsQualitative()){        
                resSpecEvaluation = resChkSpec.resultCheck((String) resultValue, specRule.getQualitativeRule(), 
                        specRule.getQualitativeRuleValues(), specRule.getQualitativeRuleSeparator(), specRule.getQualitativeRuleListName());
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation[0].toString())) {
                    return new Object[]{resSpecEvaluation};
                }      
                fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.FLD_SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.FLD_SPEC_EVAL_DETAIL.getName()
                    , TblsData.SampleAnalysisResult.FLD_ENTERED_BY.getName(), TblsData.SampleAnalysisResult.FLD_ENTERED_ON.getName(), TblsData.SampleAnalysisResult.FLD_STATUS.getName()});
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{resSpecEvaluation[resSpecEvaluation.length - 1], resSpecEvaluation[resSpecEvaluation.length - 2]
                    , token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus});
                if (limitId==null || limitId!=specLimitId){
                    fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysisResult.FLD_LIMIT_ID.getName());
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, specLimitId);
                }                
                Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                        fieldsName, fieldsValue, new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
                Object[] sampleAuditAdd=new Object[0];
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    SampleAudit smpAudit = new SampleAudit();
                    sampleAuditAdd=smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), 
                        resultId, sampleId, testId, resultId, fieldsName, fieldsValue);
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
                return new Object[]{diagnoses};
        }
        if (specRule.getRuleIsQuantitative()){
                try{
                    resultValue= new BigDecimal(resultValue.toString());
                }catch(Exception e){
                    return new Object[]{LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.NOT_NUMERIC_VALUE.getErrorCode(), new Object[]{resultValue, specRule.getRuleRepresentation(), specLimitId.toString(), schemaDataName})};            
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
                    return new Object[]{resSpecEvaluation};
                }
                String specEval = (String) resSpecEvaluation[resSpecEvaluation.length - 1];
                String specEvalDetail = (String) resSpecEvaluation[resSpecEvaluation.length - 2];
                if (requiresUnitsConversion) specEvalDetail = specEvalDetail + " in " + specUomName;

                fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.FLD_SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.FLD_SPEC_EVAL_DETAIL.getName()
                    , TblsData.SampleAnalysisResult.FLD_ENTERED_BY.getName(), TblsData.SampleAnalysisResult.FLD_ENTERED_ON.getName(), TblsData.SampleAnalysisResult.FLD_STATUS.getName()});
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEval, specEvalDetail, token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus});
                if (limitId==null || limitId!=specLimitId){
                    fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysisResult.FLD_LIMIT_ID.getName());
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, specLimitId);
                }                                
                Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                        fieldsName, fieldsValue, new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
                Object[] sampleAuditAdd=new Object[0];
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    SampleAudit smpAudit = new SampleAudit();
                    sampleAuditAdd=smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), 
                            resultId, sampleId, testId, resultId, fieldsName, fieldsValue);
                }                
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) 
                    DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId, SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED.toString(), Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString()));
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                  if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().toUpperCase().contains(ConfigSpecRule.SPEC_WORD_FOR_UPON_CONTROL))
                    this.sar.sarControlAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                  if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().toUpperCase().contains(ConfigSpecRule.SPEC_WORD_FOR_OOS))
                    this.sar.sarOOSAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                }                    
//                UserMethod.newUserMethodEntry(procInstanceName, userName, userRole, analysis, methodName, methodVersion, sampleId, testId, appSessionId);
                return new Object[]{diagnoses, sampleAuditAdd};
        }
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.SPECRULE_NOTIMPLEMENTED.getErrorCode(), new Object[]{resultId.toString(), schemaDataName, ruleType});
    }
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
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.CONVERSION_NOT_ALLOWED.getErrorCode(), new Object[]{specUomConversionMode, newuom, curruom, resultId.toString(), schemaDataName});
        UnitsOfMeasurement uom = new UnitsOfMeasurement(new BigDecimal(currValue), curruom);
        uom.convertValue(newuom);
        if (!uom.getConvertedFine()) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.CONVERTER_RETURNED_FALSE.getErrorCode(), new Object[]{resultId.toString(), uom.getConversionErrorDetail()[3].toString(), schemaDataName});
        BigDecimal resultConverted = uom.getConvertedQuantity();
        String[] updFieldNames = new String[]{TblsData.SampleAnalysisResult.FLD_RAW_VALUE.getName(), TblsData.SampleAnalysisResult.FLD_UOM.getName()};
        Object[] updFieldValues = new Object[]{resultConverted.toString(), newuom};
        Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                updFieldNames, updFieldValues, new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateRecordFieldsByFilter[0].toString())) return updateRecordFieldsByFilter;
        SampleAudit smpAudit = new SampleAudit();
        String auditActionName = SampleAudit.SampleAnalysisResultAuditEvents.UOM_CHANGED.toString() + " FOR " + paramName;
        smpAudit.sampleAuditAdd(auditActionName, TblsData.SampleAnalysisResult.TBL.getName(), 
            resultId, sampleId, testId, resultId, updFieldNames, updFieldValues);
        return updateRecordFieldsByFilter;
    }

    public Object[] sarRawToPrettyResult(Object rawValue) {
        return new Object[]{LPPlatform.LAB_TRUE, rawValue};
    }

    public Object[] sampleAnalysisResultUnCancel(Integer sampleId, Integer testId, Integer resultId) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        Object[] diagnoses = new Object[7];
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());

        String sampleAnalysisStatusCanceled = DataSampleStructureStatuses.SampleAnalysisStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisResultStatusCanceled = DataSampleStructureStatuses.SampleAnalysisResultStatuses.CANCELED.getStatusCode("");

        Object[] scopeInfo=getScope(sampleId, testId, resultId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scopeInfo[0].toString())) return scopeInfo;
        String cancelScope=scopeInfo[0].toString();
        Integer cancelScopeId=(Integer) scopeInfo[1];
        String cancelScopeTable=scopeInfo[2].toString();
        
        Object[] samplesToUnCancel = new Object[0];
        Object[] testsToUnCancel = new Object[0];
        String[] diagPerResult = new String[0];
       
        Object[][] resultInfo = null;
        resultInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                new String[]{cancelScope}, new Object[]{cancelScopeId}, 
                new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName(), TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), 
                    TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString())) {
            String[] filter = new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(sampleId).toString() + " " + TblsData.SampleAnalysisResult.FLD_TEST_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(testId).toString() +
                    " " + TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(resultId).toString()};
            errorCode = DataSampleErrorTrapping.SAMPLE_NOT_FOUND.getErrorCode();
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, new Object[]{Arrays.toString(filter), schemaDataName});
            //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, errorCode, errorDetailVariables);
        }else{
            if (TblsData.SampleAnalysisResult.TBL.getName().equalsIgnoreCase(cancelScopeTable)){
                for (Integer iResToCancel = 0; iResToCancel < resultInfo.length; iResToCancel++) {
                    String currResultStatus = (String) resultInfo[iResToCancel][0];
                    String statusPrevious = (String) resultInfo[iResToCancel][1];
                    String rsltIdStr=resultInfo[iResToCancel][2].toString();
                    resultId = Integer.valueOf(rsltIdStr);                
                    testId = (Integer) resultInfo[iResToCancel][3];
                    sampleId = (Integer) resultInfo[iResToCancel][4];
                    if (!(sampleAnalysisResultStatusCanceled.equalsIgnoreCase(currResultStatus))) {
                        diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.FORRESULTUNCANCEL_STATUS_NOT_EXPECTED.getErrorCode(), new Object[]{resultInfo[0][0].toString(), sampleAnalysisResultStatusCanceled, schemaDataName});
                        diagPerResult = LPArray.addValueToArray1D(diagPerResult, TblsData.SampleAnalysisResult.TBL.getName()+" " + resultId.toString() + " not uncanceled because current status is " + currResultStatus);
                    } else {
                        resultId = (Integer) resultInfo[iResToCancel][2];
                        String[] updFldNames=new String[]{TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName(),TblsData.SampleAnalysisResult.FLD_STATUS.getName()};
                        Object[] updFldValues=new Object[]{sampleAnalysisResultStatusCanceled, statusPrevious};
                        diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                                updFldNames, updFldValues, 
                                new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                            SampleAudit smpAudit = new SampleAudit();
                            smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_UNCANCELED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), +
                                    resultId, sampleId, testId, resultId, updFldNames, updFldValues);
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
        }
        if (TblsData.SampleAnalysisResult.TBL.getName().equalsIgnoreCase(cancelScopeTable) || TblsData.SampleAnalysis.TBL.getName().equalsIgnoreCase(cancelScopeTable)){        
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
                    String[] updFldNames=new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName()};
                    Object[] updFldValues=new Object[]{currPrevStatus, sampleAnalysisResultStatusCanceled};
                    diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                            updFldNames, updFldValues,
                            new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{currTest});
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_UNCANCELED.toString(), TblsData.SampleAnalysis.TBL.getName(), 
                            currTest, sampleId, currTest, null, updFldNames, updFldValues);
                    }
                } else {
                    diagnoses[5] = "The "+TblsData.SampleAnalysis.TBL.getName()+" "+currTest+" has status "+currStatus+" then cannot be canceled in schema "+schemaDataName;                 
                }
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
            if ((SampleStatuses.CANCELED.getStatusCode("").equalsIgnoreCase(currStatus)) && (currSample != null)) {
                String[] updFldNames=new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName()};
                Object[] updFldValues=new Object[]{currPrevStatus, sampleAnalysisResultStatusCanceled};
                diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), 
                        updFldNames, updFldValues, 
                        new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{currSample});
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_UNCANCELED.toString(), TblsData.Sample.TBL.getName(), 
                            currSample, currSample, null, null, updFldNames, updFldValues);
                }
            } else {
                diagnoses[5] = "The "+TblsData.Sample.TBL.getName()+" "+currSample+" has status "+currStatus+" then cannot be canceled in schema "+schemaDataName;
            }
        }
        diagnoses[5] = Arrays.toString(diagPerResult);
        return diagnoses;
    }

    public Object[] sampleAnalysisResultUnReview(Integer sampleId, Integer testId, Integer resultId) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        Object[] diagnoses = new Object[7];
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());

        String sampleStatusReviewed = DataSampleStructureStatuses.SampleStatuses.REVIEWED.getStatusCode("");
        String sampleAnalysisStatusReviewed = DataSampleStructureStatuses.SampleAnalysisStatuses.REVIEWED.getStatusCode("");
        String sampleAnalysisResultStatusReviewed = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REVIEWED.getStatusCode("");

        Object[] scopeInfo=getScope(sampleId, testId, resultId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scopeInfo[0].toString())) return scopeInfo;
        String reviewScope=scopeInfo[0].toString();
        Integer reviewScopeId=(Integer) scopeInfo[1];
        String cancelScopeTable=scopeInfo[2].toString();
        
        Object[] samplesToUnReview = new Object[0];
        Object[] testsToUnReview = new Object[0];
        String[] diagPerResult = new String[0];

        Object[][] resultInfo = null;
        resultInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                new String[]{reviewScope}, new Object[]{reviewScopeId}, 
                new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName(), TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), 
                    TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString())) {
            String[] filter = new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(sampleId).toString() + " " + TblsData.SampleAnalysisResult.FLD_TEST_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(testId).toString() +
                    " " + TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(resultId).toString()};
            errorCode = DataSampleErrorTrapping.SAMPLE_NOT_FOUND.getErrorCode();
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, new Object[]{Arrays.toString(filter), schemaDataName});
            //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, errorCode, errorDetailVariables);
        }else{
            if (TblsData.SampleAnalysisResult.TBL.getName().equalsIgnoreCase(cancelScopeTable)){            
                for (Integer iResToReview = 0; iResToReview < resultInfo.length; iResToReview++) {
                    String currResultStatus = (String) resultInfo[iResToReview][0];
                    String statusPrevious = (String) resultInfo[iResToReview][1];
                    String rsltIdStr=resultInfo[iResToReview][2].toString();
                    resultId = Integer.valueOf(rsltIdStr);                
                    testId = (Integer) resultInfo[iResToReview][3];
                    sampleId = (Integer) resultInfo[iResToReview][4];
                    if (!(sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currResultStatus))) {
                        diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.FORRESULTUNREVIEW_STATUS_NOT_EXPECTED.getErrorCode(), new Object[]{resultInfo[0][0].toString(), sampleAnalysisResultStatusReviewed, schemaDataName});
                        diagPerResult = LPArray.addValueToArray1D(diagPerResult, TblsData.SampleAnalysisResult.TBL.getName()+" " + resultId.toString() + " not unreviewed because current status is " + currResultStatus);
                    } else {
                        String[] updFldNames=new String[]{TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName(), TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_REVIEWED.getName(), TblsData.SampleAnalysisResult.FLD_REVIEWED_ON.getName(), TblsData.SampleAnalysisResult.FLD_REVIEWED_BY.getName()};
                        Object[] updFldValues=new Object[]{sampleAnalysisResultStatusReviewed, statusPrevious, false, "NULL>>>DATE", "NULL>>>STRING"};
                        diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                            updFldNames, updFldValues, 
                            new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                            SampleAudit smpAudit = new SampleAudit();
                            smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_UNCANCELED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), 
                                resultId, sampleId, testId, resultId, updFldNames, updFldValues);
                        }
                        diagPerResult = LPArray.addValueToArray1D(diagPerResult, "Result " + resultId.toString() + " UNREVIEWED ");
                    }
                    if ((reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName())) && (!LPArray.valueInArray(samplesToUnReview, sampleId))) {
                        samplesToUnReview = LPArray.addValueToArray1D(samplesToUnReview, sampleId);
                    }
                    if ((reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()) || reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.FLD_TEST_ID.getName())) && (!LPArray.valueInArray(testsToUnReview, testId))) {
                        testsToUnReview = LPArray.addValueToArray1D(testsToUnReview, testId);
                    }
                }
            }
        }
        if (TblsData.SampleAnalysisResult.TBL.getName().equalsIgnoreCase(cancelScopeTable) || TblsData.SampleAnalysis.TBL.getName().equalsIgnoreCase(cancelScopeTable)){                
            if (testsToUnReview.length==0 && reviewScope.equalsIgnoreCase(TblsData.SampleAnalysis.FLD_TEST_ID.getName()))
                testsToUnReview = LPArray.addValueToArray1D(testsToUnReview, reviewScopeId);                
            for (Integer iTstToUnreview = 0; iTstToUnreview < testsToUnReview.length; iTstToUnreview++) {
                Integer currTest = (Integer) testsToUnReview[iTstToUnreview];
                Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                        new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{currTest}, 
                        new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName(), TblsData.SampleAnalysis.FLD_TEST_ID.getName(), 
                            TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName()});
                String currStatus = (String) objectInfo[0][0];
                String currPrevStatus = (String) objectInfo[0][1];
                if ((sampleAnalysisStatusReviewed.equalsIgnoreCase(currStatus)) && (currTest != null)) {
                    String[] updFldNames=new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName(), TblsData.SampleAnalysis.FLD_REVIEWED.getName(), TblsData.SampleAnalysis.FLD_REVIEWED_ON.getName(), TblsData.SampleAnalysis.FLD_REVIEWED_BY.getName()};
                    Object[] updFldValues=new Object[]{currPrevStatus, sampleAnalysisResultStatusReviewed, false, "NULL>>>DATE", "NULL>>>STRING"};
                    diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                        updFldNames, updFldValues,
                        new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{currTest});
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_UNREVIEWED.toString(), TblsData.SampleAnalysis.TBL.getName(), 
                            currTest, sampleId, currTest, null, updFldNames, updFldValues);
                    }
                } else {
                    diagnoses[5] = "The "+TblsData.SampleAnalysis.TBL.getName()+" "+currTest+" has status "+currStatus+" then cannot be unreviewed in schema "+schemaDataName;                 
                }
            }
        }
        if (samplesToUnReview.length==0 && reviewScope.equalsIgnoreCase(TblsData.Sample.FLD_SAMPLE_ID.getName()))
            samplesToUnReview = LPArray.addValueToArray1D(samplesToUnReview, reviewScopeId);        
        for (Integer iSmpToUnReview = 0; iSmpToUnReview < samplesToUnReview.length; iSmpToUnReview++) {
            Integer currSample = (Integer) samplesToUnReview[iSmpToUnReview];
            Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{currSample}, 
                    new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName()});
            String currStatus = (String) objectInfo[0][0];
            String currPrevStatus = (String) objectInfo[0][1];
            if ((sampleStatusReviewed.equalsIgnoreCase(currStatus)) && (currSample != null)) {
                String[] updFldNames=new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName(), TblsData.Sample.FLD_REVIEWED.getName(), TblsData.Sample.FLD_REVIEWED_ON.getName(), TblsData.Sample.FLD_REVIEWED_BY.getName()};
                Object[] updFldValues=new Object[]{currPrevStatus, sampleAnalysisResultStatusReviewed, false, "NULL>>>DATE", "NULL>>>STRING"};
                diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), 
                    updFldNames, updFldValues,
                    new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{currSample});
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {                    
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_UNREVIEWED.toString(), TblsData.Sample.TBL.getName(), 
                        currSample, currSample, null, null, updFldNames, updFldValues);
                }
            } else {
                diagnoses[5] = "The "+TblsData.Sample.TBL.getName()+" "+currSample+" has status "+currStatus+" then cannot be unreviewed in schema "+schemaDataName;
            }
        }
        diagnoses[5] = Arrays.toString(diagPerResult);
        return diagnoses;
    }

    public Object[] sampleAnalysisResultCancel(Integer sampleId, Integer testId, Integer resultId) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] diagnoses = new Object[7];
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String sampleStatusCanceled = SampleStatuses.CANCELED.getStatusCode("");
        String sampleStatusReviewed = SampleStatuses.REVIEWED.getStatusCode("");
        String sampleAnalysisStatusReviewed = DataSampleStructureStatuses.SampleAnalysisStatuses.REVIEWED.getStatusCode("");
        String sampleAnalysisStatusCanceled = DataSampleStructureStatuses.SampleAnalysisStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisResultStatusCanceled = DataSampleStructureStatuses.SampleAnalysisResultStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisResultStatusReviewed = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REVIEWED.getStatusCode("");
        Object[] samplesToCancel = new Object[0];
        Object[] testsToCancel = new Object[0];
        Object[] testsSampleToCancel = new Object[0];
        Object[] scopeInfo=getScope(sampleId, testId, resultId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scopeInfo[0].toString())) return scopeInfo;
        String cancelScope=scopeInfo[0].toString();
        Integer cancelScopeId=(Integer) scopeInfo[1];
        String cancelScopeTable=scopeInfo[2].toString();
        Object[][] objectInfo = null;
        objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, cancelScopeTable, 
                new String[]{cancelScope}, new Object[]{cancelScopeId}, 
                new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), cancelScope});// TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectInfo[0][0].toString())) {
            String[] filter = new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(sampleId).toString() + TblsData.SampleAnalysisResult.FLD_TEST_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(testId).toString() 
                    + TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(resultId).toString()};
            //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_NOT_FOUND.getErrorCode(), new Object[]{Arrays.toString(filter), schemaDataName});
        } else {
            objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                    new String[]{cancelScope}, new Object[]{cancelScopeId}, 
                    new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()});
            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(objectInfo[0][0].toString())) {
                for (Integer iResToCancel = 0; iResToCancel < objectInfo.length; iResToCancel++) {
                    String currStatus = (String) objectInfo[iResToCancel][0];
                    if (!(sampleAnalysisResultStatusCanceled.equalsIgnoreCase(currStatus))) {
                        String rsltIdStr=objectInfo[iResToCancel][2].toString();
                        resultId = Integer.valueOf(rsltIdStr);                
                        testId = (Integer) objectInfo[iResToCancel][2];
                        sampleId = (Integer) objectInfo[iResToCancel][3];
                        if (!(sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currStatus))) {
                            String[] updFldNames=new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName()};
                            Object[] updFldValues=new Object[]{sampleAnalysisResultStatusCanceled, currStatus};
                            diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                                    updFldNames, updFldValues,
                                    new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId});
                            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                                SampleAudit smpAudit = new SampleAudit();
                                smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_CANCELED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), 
                                    resultId, sampleId, testId, resultId, updFldNames, updFldValues);
                            }
                        } else 
                            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.FORRESULTCANCELATION_STATUS_NOT_EXPECTED.getErrorCode(), new Object[]{resultId.toString(), currStatus, schemaDataName});
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
                    String[] updFldNames=new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName()}; 
                    Object[] updFldValues=new Object[]{sampleAnalysisStatusCanceled, currStatus};
                    diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                        updFldNames, updFldValues,
                        new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{currTest});
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_CANCELED.toString(), TblsData.SampleAnalysis.TBL.getName(), 
                        currTest, sampleId, currTest, null, updFldNames, updFldValues);
                }
            } else 
                diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.FORRESULTCANCELATION_STATUS_NOT_EXPECTED.getErrorCode(), new Object[]{LPNulls.replaceNull(currTest), currStatus, schemaDataName});            
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
                String[] updFldNames=new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName()};
                Object[] updFldValues=new Object[]{sampleStatusCanceled, currStatus};
                diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), 
                        updFldNames, updFldValues,
                        new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{currSample});
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_CANCELED.toString(), TblsData.Sample.TBL.getName(), 
                            currSample, currSample, null, null, updFldNames, updFldValues);
                }
            }else 
                diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.FORRESULTCANCELATION_STATUS_NOT_EXPECTED.getErrorCode(), new Object[]{LPNulls.replaceNull(currSample), currStatus, schemaDataName});
        }
        return diagnoses;
    }
    public Object[] sampleAnalysisResultReviewBySampleAndAnalysis(Integer sampleId, String analysisName) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] analysisNameArr=analysisName.split("\\|");
        Object[] diagn=new Object[]{};
        for (String analysisNameArr1 : analysisNameArr) {
            Object[][] testInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysis.TBL.getName(), new String[]{TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName(), TblsData.SampleAnalysis.FLD_ANALYSIS.getName()}, new Object[]{sampleId, analysisNameArr1}, new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testInfo[0][0].toString())) return LPArray.array2dTo1d(testInfo);
            if (testInfo.length>1) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.ANALYSIS_HAS_SOME_PARAMETERS.getErrorCode(), null);
            diagn=sampleAnalysisResultReview(null, Integer.valueOf(testInfo[0][0].toString()), null);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) 
                return diagn;            
        }
        return diagn;
    }
    public Object[] sampleAnalysisResultReview(Integer sampleId, Integer testId, Integer resultId) {
        return sampleAnalysisResultReview(sampleId, testId, resultId, null);
    }

    public Object[] sampleAnalysisResultReview(Integer sampleId, Integer testId, Integer resultId, String reviewer) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String sampleAnalysisResultStatusCanceled = DataSampleStructureStatuses.SampleAnalysisResultStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisResultStatusReviewed = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REVIEWED.getStatusCode("");        
        String[] fieldsToRetrieve = new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), 
            TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), 
            TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName(),
            TblsData.SampleAnalysisResult.FLD_ENTERED_BY.getName()};        
        Object[] scopeInfo=getScope(sampleId, testId, resultId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scopeInfo[0].toString())) return scopeInfo;
        String reviewScope=scopeInfo[0].toString();
        Integer reviewScopeId=(Integer) scopeInfo[1];
        String reviewScopeTable=scopeInfo[2].toString();        
        if (sampleId != null) {
            Object[] sampleReviewable=checkIfSampleIsReadyForRevision(sampleId);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleReviewable[0].toString())) return sampleReviewable;
            reviewScopeTable = TblsData.Sample.TBL.getName();            
//            Object[] sampleRevisionByTestingGroupReviewed = DataSampleRevisionTestingGroup.isSampleRevisionByTestingGroupReviewed(sampleId);
//            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleRevisionByTestingGroupReviewed[0].toString())) return sampleRevisionByTestingGroupReviewed;            
            DataModuleSampleAnalysis smpAna = new DataModuleSampleAnalysis();
            DataSample smp=new DataSample(smpAna);
            return smp.sampleReview(sampleId);
        }
        if (testId != null) {
            reviewScopeTable = TblsData.SampleAnalysis.TBL.getName();
            Object[] readyForRevision = isReadyForRevision(testId);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(readyForRevision[0].toString())) return readyForRevision;
        }
        //if (resultId != null) 
        reviewScopeTable = TblsData.SampleAnalysisResult.TBL.getName();
        Object[][] objectInfoForRevisionCheck = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), reviewScopeTable, 
                new String[]{reviewScope}, new Object[]{reviewScopeId}, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectInfoForRevisionCheck[0][0].toString()) || objectInfoForRevisionCheck.length == 0)             
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.NOT_FOUND.getErrorCode(), new Object[]{LPNulls.replaceNull(resultId).toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
        if (reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName())){
            if (sampleAnalysisResultStatusReviewed.equalsIgnoreCase(objectInfoForRevisionCheck[0][0].toString()))
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_ALREADY_REVIEWED.getErrorCode(), new Object[]{reviewScope, reviewScopeId, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
        }
        Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysisResult.TBL.getName(), 
                new String[]{reviewScope}, new Object[]{reviewScopeId}, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectInfo[0][0].toString()) || objectInfo.length == 0)             
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.NOT_FOUND.getErrorCode(), new Object[]{LPNulls.replaceNull(resultId).toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});            
        Object[] reviewSampleAnalysisRulesAllowed = reviewSampleAnalysisRulesAllowed(testId, fieldsToRetrieve, objectInfo);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reviewSampleAnalysisRulesAllowed[0].toString())) return reviewSampleAnalysisRulesAllowed;
        Object[] testsToReview = reviewSamplesAnalysisResultToReview(objectInfo, reviewer);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testsToReview[0].toString()))
                return testsToReview;
        Object[] sampleToReview = reviewSamplesAnalysisFromSampleToReview(sampleId, new Object[]{testId});
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(sampleToReview[0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "testNotReviewed", new Object[]{testId});
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "testReviewed", new Object[]{testId});
    }    
    
/*    private Object[] nextForSampleAnalysisReview(Integer sampleId, Integer testId, String parentAuditAction, Integer parentAuditId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleRevisionTestingGroup.TBL.getName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())){
            isReadyForRevision(Integer sampleId, String testingGroup);
            setReadyForRevision(Integer sampleId, String testingGroup);
        }else{
            setReadyForRevision(Integer sampleId, parentAuditAction, parentAuditId);
        }
    }
  */  
    public static Object[] checkIfSampleIsReadyForRevision(Integer sampleId) {    
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] sampleReadyForRevisionFldExists=Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), 
                 TblsData.Sample.FLD_READY_FOR_REVISION.getName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(sampleReadyForRevisionFldExists[0].toString())){
            Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), 
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.FLD_READY_FOR_REVISION.getName()});
            if (!Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString())) 
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleNotSetAsReadyForRevision", new Object[]{sampleId});
        }            
        Object[] allsampleAnalysisReviewed = DataSampleAnalysis.isAllsampleAnalysisReviewed(sampleId, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allsampleAnalysisReviewed[0].toString())) return allsampleAnalysisReviewed;
        Object[] allsampleTestGroupReviewed = DataSampleRevisionTestingGroup.isAllsampleTestingGroupReviewed(sampleId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allsampleTestGroupReviewed[0].toString())) return allsampleTestGroupReviewed;
        
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "OK", new Object[]{sampleId});
    }
    private Object[] getScope(Integer sampleId, Integer testId, Integer resultId){
        if (sampleId != null) return new Object[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName(), sampleId, TblsData.Sample.TBL.getName()};
        if (testId != null) return new Object[]{TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), testId, TblsData.SampleAnalysis.TBL.getName()};   
        if (resultId != null) return new Object[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), resultId, TblsData.SampleAnalysisResult.TBL.getName()};
        return new Object[]{LPPlatform.LAB_FALSE, "notRecognizedLevel <*1*>", null};
    }

    private Object[] reviewSampleToReview(Object[] samplesToReview){
        if (samplesToReview==null) return new Object[]{LPPlatform.LAB_TRUE,null};
        Object[] samplesFinallyReviewed=null;
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String sampleAnalysisResultStatusCanceled = DataSampleStructureStatuses.SampleAnalysisResultStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisResultStatusReviewed = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REVIEWED.getStatusCode("");
        for (Integer isamplesToReview = 0; isamplesToReview < samplesToReview.length; isamplesToReview++) {
            Integer sampleId = Integer.valueOf(samplesToReview[isamplesToReview].toString());
            Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), 
                    new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                    new String[]{TblsData.Sample.FLD_STATUS.getName()});
            if (sampleInfo.length == 0) 
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_NOT_FOUND.getErrorCode(), new Object[]{sampleId.toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
            Object[] isRevisionSampleAnalysisRequired=LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleStructureRevisionRls.REVISION_SAMPLEANALYSIS_REQUIRED.getAreaName(), DataSampleStructureRevisionRls.REVISION_SAMPLEANALYSIS_REQUIRED.getTagName());
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionSampleAnalysisRequired[0].toString())){            
                Object[] isallsampleAnalysisReviewed = DataSampleAnalysis.isAllsampleAnalysisReviewed(sampleId, new String[]{}, new Object[]{});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isallsampleAnalysisReviewed[0].toString())) return isallsampleAnalysisReviewed;
            }
            String currStatus=sampleInfo[0][0].toString();                
            if (!(sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currStatus))) {
                String[] updFieldName=new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName()};
                Object[] updFieldValue=new Object[]{sampleAnalysisResultStatusReviewed, currStatus};
                Object[] fieldExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.FLD_READY_FOR_REVISION.getName());
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldExists[0].toString())){
                    updFieldName=LPArray.addValueToArray1D(updFieldName, TblsData.Sample.FLD_READY_FOR_REVISION.getName());
                    updFieldValue=LPArray.addValueToArray1D(updFieldValue, false);
                }
                Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), 
                        updFieldName, updFieldValue,
                        new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName(), TblsData.Sample.FLD_STATUS.getName()+" not in-"}, new Object[]{sampleId, sampleAnalysisResultStatusCanceled+"-"+sampleAnalysisResultStatusReviewed});                    
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {    
                    samplesFinallyReviewed=new Object[]{sampleId};
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldExists[0].toString())){
                        updFieldName = LPArray.addValueToArray1D(updFieldName, TblsData.Sample.FLD_READY_FOR_REVISION.getName());                        
                        updFieldValue = LPArray.addValueToArray1D(updFieldValue, "false");                        
                    }
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_REVIEWED.toString(), TblsData.Sample.TBL.getName(), 
                        sampleId, sampleId, null, null, updFieldName, updFieldValue);
                }
            }
        }       
        return new Object[]{LPPlatform.LAB_TRUE, samplesFinallyReviewed};
    }
    private Object[] reviewSamplesAnalysisFromSampleToReview(Integer sampleId, Object[] testsToReview){
        return reviewSamplesAnalysisFromSampleToReview(sampleId, testsToReview, null);
    }
    private Object[] reviewSamplesAnalysisFromSampleToReview(Integer sampleId, Object[] testsToReview, String reviewer){
        if (testsToReview==null) return new Object[]{LPPlatform.LAB_TRUE,null};
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=instanceForActions.getProcedureInstance();  
        Token token = instanceForActions.getToken();
        Object[] sampleAnalysisFinallyReviewed=null;
        String sampleAnalysisResultStatusCanceled = DataSampleStructureStatuses.SampleAnalysisResultStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisResultStatusReviewed = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REVIEWED.getStatusCode("");
        for (Integer itestsToReview = 0; itestsToReview < testsToReview.length; itestsToReview++) {
            Integer testId = Integer.valueOf(testsToReview[itestsToReview].toString());
            Object[][] testInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysis.TBL.getName(), 
                    new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{testId}, 
                    new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName()});
            if (testInfo.length == 0) {            
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSIS_NOTFOUND.getErrorCode(), new Object[]{testId.toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
            } else {
                String currStatus=testInfo[0][0].toString();                
                if (!(sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currStatus))) {
                    String[] updFieldName=new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName(),
                        TblsData.SampleAnalysis.FLD_REVIEWED_ON.getName(), TblsData.SampleAnalysis.FLD_REVIEWED_BY.getName()}; 
                    
                    Object[] updFieldValue=new Object[]{sampleAnalysisResultStatusReviewed, currStatus, LPDate.getCurrentTimeStamp()};
                    if (reviewer==null)
                        updFieldValue=LPArray.addValueToArray1D(updFieldValue, token.getPersonName());
                    else
                        updFieldValue=LPArray.addValueToArray1D(updFieldValue, reviewer);
                    Object[] fieldExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.FLD_READY_FOR_REVISION.getName());
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldExists[0].toString())){
                        updFieldName=LPArray.addValueToArray1D(updFieldName, TblsData.Sample.FLD_READY_FOR_REVISION.getName());
                        updFieldValue=LPArray.addValueToArray1D(updFieldValue, false);
                    }
                    Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysis.TBL.getName(), 
                            updFieldName, updFieldValue,
                            new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName(), TblsData.SampleAnalysis.FLD_STATUS.getName()+" not in-"}, new Object[]{testId, sampleAnalysisResultStatusCanceled+"-"+sampleAnalysisResultStatusReviewed});
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        sampleAnalysisFinallyReviewed=new Object[]{sampleId};
                        SampleAudit smpAudit = new SampleAudit();
                        Object[] sampleAuditAdd = smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_REVIEWED.toString(), TblsData.SampleAnalysis.TBL.getName(), 
                            testId, sampleId, testId, null, updFieldName, updFieldValue);
                        sampleAnalysisEvaluateStatusAutomatismForReview(sampleId, testId, 
                                SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_REVIEWED.toString(), Integer.valueOf(LPNulls.replaceNull(sampleAuditAdd[sampleAuditAdd.length-1]).toString()));
                    }
                }
            }
        }
        return new Object[]{LPPlatform.LAB_TRUE, sampleAnalysisFinallyReviewed};        
    }
    private Object[] reviewSamplesAnalysisResultToReview(Object[][] objectInfo){
        return reviewSamplesAnalysisResultToReview(objectInfo, null);
    }

    private Object[] reviewSamplesAnalysisResultToReview(Object[][] objectInfo, String reviewer){
        if (objectInfo==null) return new Object[]{LPPlatform.LAB_TRUE,null};
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=instanceForActions.getProcedureInstance();  
        Token token = instanceForActions.getToken();
        
        String sampleAnalysisResultStatusCanceled = DataSampleStructureStatuses.SampleAnalysisResultStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisResultStatusReviewed = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REVIEWED.getStatusCode("");
        Object[] sampleAnalysisResultFinallyReviewed=null;
        Integer sampleId = null;
        for (Integer iResToCancel = 0; iResToCancel < objectInfo.length; iResToCancel++) {
            String currStatus = (String) objectInfo[iResToCancel][0];
            if (!(sampleAnalysisResultStatusCanceled.equalsIgnoreCase(currStatus))) {
                String rsltIdStr=objectInfo[iResToCancel][1].toString();
                Integer resultId = Integer.valueOf(rsltIdStr);                
                Integer testId = Integer.valueOf(objectInfo[iResToCancel][2].toString());
                sampleId = Integer.valueOf(objectInfo[iResToCancel][3].toString());
                if (!(sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currStatus))) {
                    String[] updFldName=new String[]{TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_STATUS_PREVIOUS.getName(), 
                        TblsData.SampleAnalysisResult.FLD_REVIEWED_ON.getName(), TblsData.SampleAnalysisResult.FLD_REVIEWED_BY.getName()}; 
                    Object[] updFldValue=new Object[]{sampleAnalysisResultStatusReviewed, currStatus, LPDate.getCurrentTimeStamp()};
                    if (reviewer==null)
                        updFldValue=LPArray.addValueToArray1D(updFldValue, token.getPersonName());
                    else
                        updFldValue=LPArray.addValueToArray1D(updFldValue, reviewer);
                    Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysisResult.TBL.getName(), 
                        updFldName, updFldValue,
                        new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), TblsData.SampleAnalysisResult.FLD_STATUS.getName()+" not in-"}, new Object[]{resultId, sampleAnalysisResultStatusCanceled+"-"+sampleAnalysisResultStatusReviewed});
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        sampleAnalysisResultFinallyReviewed=LPArray.addValueToArray1D(sampleAnalysisResultFinallyReviewed, resultId);
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_REVIEWED.toString(), TblsData.SampleAnalysisResult.TBL.getName(), 
                            resultId, sampleId, testId, resultId, updFldName, updFldValue);
                    }
                } 
            }
        }
        return new Object[]{LPPlatform.LAB_TRUE, sampleAnalysisResultFinallyReviewed, sampleId};        
    }
}