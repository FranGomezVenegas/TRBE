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
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsCnfg;
import databases.TblsData;
import databases.features.Token;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.certification.AnalysisMethodCertif;
import functionaljavaa.inventory.InventoryGlobalVariables.DataInvRetErrorTrapping;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.materialspec.DataSpec;
import functionaljavaa.modulesample.DataModuleSampleAnalysis;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleErrorTrapping;
import static functionaljavaa.samplestructure.DataSampleAnalysis.isReadyForRevision;
import static functionaljavaa.samplestructure.DataSampleAnalysis.sampleAnalysisEvaluateStatusAutomatismForReview;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleAnalysisErrorTrapping;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleAnalysisResultErrorTrapping;
import static functionaljavaa.samplestructure.DataSampleStructureRevisionRules.reviewSampleAnalysisRulesAllowed;
import functionaljavaa.samplestructure.DataSampleStructureStatuses.SampleStatuses;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import lbplanet.utilities.LPDate;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ResponseMessages;
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
        Integer cancelScopeId =Integer.valueOf(LPNulls.replaceNull(scopeInfo[1]).toString());
        Object[][] objectInfo = null;
        objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), new String[]{cancelScope}, new Object[]{cancelScopeId}, 
                new String[]{TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.RESULT_ID.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.SAMPLE_ID.getName()});
        if (objectInfo.length == 0) {
            String[] filter = new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + sampleId.toString() + 
                    TblsData.SampleAnalysisResult.TEST_ID.getName()+LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + testId.toString() + TblsData.SampleAnalysisResult.RESULT_ID.getName()+LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + resultId.toString()};
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.NOT_FOUND, new Object[]{Arrays.toString(filter), schemaDataName});
        } else {
            for (Integer iResToCancel = 0; iResToCancel < objectInfo.length; iResToCancel++) {
                String currStatus = (String) objectInfo[iResToCancel][0];
                if (Boolean.FALSE.equals((sampleAnalysisResultStatusCanceled.equalsIgnoreCase(currStatus)))) {
                    String rsltIdStr=objectInfo[iResToCancel][1].toString();
                    resultId = Integer.valueOf(rsltIdStr);                
                    testId = Integer.valueOf(LPNulls.replaceNull(objectInfo[iResToCancel][2]).toString());
                    sampleId = Integer.valueOf(LPNulls.replaceNull(objectInfo[iResToCancel][3]).toString());
                    if (Boolean.FALSE.equals((sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currStatus)))) {
                        String[] updFldName=new String[]{TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.STATUS_PREVIOUS.getName()};
                        Object[] updFldValue=new Object[]{sampleAnalysisResultStatusCanceled, currStatus};
                        SqlWhere sqlWhere = new SqlWhere();
                        sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
                        diagnoses = (String[]) Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                                EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, updFldName), updFldValue, sqlWhere, null);        
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0])) {
                            String[] fieldsForAudit = new String[0];
                            fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysisResult.STATUS + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + sampleAnalysisResultStatusCanceled);
                            fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysisResult.STATUS_PREVIOUS + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + currStatus);
                            SampleAudit smpAudit = new SampleAudit();
                            smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.BACK_FROM_CANCEL, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                                resultId, sampleId, testId, resultId, fieldsForAudit, null);
                        }
                    } else {
                        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.RESULT_CANNOT_BE_CANCELLED, 
                            new Object[]{TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), resultId, currStatus,schemaDataName});
                    }
                }
                if ((cancelScope.equalsIgnoreCase(TblsData.Sample.SAMPLE_ID.getName())) && (Boolean.FALSE.equals(LPArray.valueInArray(samplesToCancel, sampleId)))) {
                    samplesToCancel = LPArray.addValueToArray1D(samplesToCancel, sampleId);
                }
                if ((cancelScope.equalsIgnoreCase(TblsData.Sample.SAMPLE_ID.getName()) || cancelScope.equalsIgnoreCase(TblsData.SampleAnalysis.TEST_ID.getName())) && (Boolean.FALSE.equals(LPArray.valueInArray(testsToCancel, testId)))) {
                    testsToCancel = LPArray.addValueToArray1D(testsToCancel, testId);
                    testsSampleToCancel = LPArray.addValueToArray1D(testsSampleToCancel, sampleId);
                }
            }
        }
        for (Integer iTstToCancel = 0; iTstToCancel < testsToCancel.length; iTstToCancel++) {
            Integer currTest = Integer.valueOf(LPNulls.replaceNull(testsToCancel[iTstToCancel]).toString());
            if (currTest != null) {
                objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{currTest}, 
                        new String[]{TblsData.SampleAnalysis.STATUS.getName(), TblsData.SampleAnalysis.STATUS_PREVIOUS.getName(), TblsData.SampleAnalysis.TEST_ID.getName(), 
                            TblsData.SampleAnalysis.SAMPLE_ID.getName()});
                String currStatus = (String) objectInfo[0][0];
                if ((Boolean.FALSE.equals((sampleAnalysisStatusCanceled.equalsIgnoreCase(currStatus)))) && (Boolean.FALSE.equals((sampleAnalysisStatusReviewed.equalsIgnoreCase(currStatus)))) && (currTest != null)) {
                    String[] updFldName=new String[]{TblsData.SampleAnalysis.STATUS.getName(), TblsData.Sample.STATUS_PREVIOUS.getName()};
                    Object[] updFldValue=new Object[]{sampleAnalysisStatusCanceled, currStatus};
                    SqlWhere sqlWhere = new SqlWhere();
                    sqlWhere.addConstraint(TblsData.SampleAnalysis.TEST_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{currTest}, "");
                    diagnoses = (String[])Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS,
                            EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS, updFldName), updFldValue, sqlWhere, null);        
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0])) {
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.BACK_FROM_CANCEL, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                            currTest, sampleId, currTest, null, updFldName, updFldValue);
                    }
                } else 
                    return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.RESULT_CANNOT_BE_CANCELLED, 
                        new Object[]{TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), resultId, currStatus,schemaDataName});
            }
        }
        for (Integer iSmpToCancel = 0; iSmpToCancel < samplesToCancel.length; iSmpToCancel++) {
            Integer currSample = Integer.valueOf(LPNulls.replaceNull(samplesToCancel[iSmpToCancel]).toString());
            objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{currSample}, 
                    new String[]{TblsData.Sample.STATUS.getName(), TblsData.Sample.STATUS_PREVIOUS.getName(), TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.SAMPLE_ID.getName()});
            String currStatus = (String) objectInfo[0][0];
            if ((Boolean.FALSE.equals((sampleStatusCanceled.equalsIgnoreCase(currStatus)))) && (Boolean.FALSE.equals((sampleStatusReviewed.equalsIgnoreCase(currStatus)))) && (currSample != null)) {
                String[] updFldName=new String[]{TblsData.Sample.STATUS.getName(), TblsData.Sample.STATUS_PREVIOUS.getName()};
                Object[] updFldValue=new Object[]{sampleStatusCanceled, currStatus};
                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsData.Sample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{currSample}, "");
                diagnoses = (String[])Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE,
                        EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE, updFldName), updFldValue, sqlWhere, null);        
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0])) {
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.BACK_FROM_CANCEL, TblsData.TablesData.SAMPLE.getTableName(), 
                        currSample, currSample, null, null, updFldName, updFldValue);
                }
            } else {
                diagnoses[5] = "The "+TblsData.TablesData.SAMPLE.getTableName()+" "+currSample+" has status "+currStatus+" then cannot be canceled in schema "+schemaDataName; 
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
            Object[][] resultInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.ANALYSIS.getName()}, 
                new Object[]{sampleId, analysisNameArr[i]}, 
                new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString())) return LPArray.array2dTo1d(resultInfo);
            if (resultInfo.length>1) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.ANALYSIS_HAS_SOME_PARAMETERS, null);
            Object[] actionDiagnoses=sampleAnalysisResultEntry(Integer.valueOf(resultInfo[0][0].toString()), resultValueArr[i],dataSample);
            diagn=(Object[]) actionDiagnoses[0];   
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        }
        return diagn;
    }
    public Object[] sampleAnalysisResultEntry(Integer resultId, Object resultValue, DataSample dataSample){
        return sampleAnalysisResultEntry(resultId, resultValue, dataSample, null, null);
    }
    public Object[] sampleAnalysisResultEntry(Integer resultId, Object resultValue, DataSample dataSample, String alternativeAuditEntry, String alternativeAuditClass) {           
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Token token=instanceForActions.getToken();
        String procInstanceName=instanceForActions.getProcedureInstance();
        ResponseMessages messages = instanceForActions.getMessages();
        
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
        fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysisResult.RAW_VALUE.getName());
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, resultValue);
        Object[][] resultData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()}, new Object[]{resultId}, 
                new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.ANALYSIS.getName(), 
                    TblsData.SampleAnalysisResult.METHOD_NAME.getName(), TblsData.SampleAnalysisResult.METHOD_VERSION.getName(), TblsData.SampleAnalysisResult.PARAM_NAME.getName(), 
                    TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.RAW_VALUE.getName(), TblsData.SampleAnalysisResult.UOM.getName(), 
                    TblsData.SampleAnalysisResult.UOM_CONVERSION_MODE.getName(), TblsData.SampleAnalysisResult.LIMIT_ID.getName()});
        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) 
            return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.NOT_FOUND, new Object[]{resultId.toString(), schemaDataName})};
        Integer sampleId = Integer.valueOf(LPNulls.replaceNull(resultData[0][0]).toString());
        Integer testId = Integer.valueOf(LPNulls.replaceNull(resultData[0][1]).toString());
        String analysis = LPNulls.replaceNull(resultData[0][2]).toString();
        String methodName = LPNulls.replaceNull(resultData[0][3]).toString();
        Integer methodVersion = Integer.valueOf(LPNulls.replaceNull(resultData[0][4]).toString());
        String paramName = LPNulls.replaceNull(resultData[0][5]).toString();
        String currResultStatus = LPNulls.replaceNull(resultData[0][6]).toString();
        String currRawValue = LPNulls.replaceNull(resultData[0][7]).toString();
        String resultUomName = LPNulls.replaceNull(resultData[0][8]).toString();
        Integer limitId =-999;
        if (resultData[0][10]!=null && resultData[0][10].toString().length()>0)
            limitId = Integer.valueOf(LPNulls.replaceNull(resultData[0][10]).toString());
        
        Object[] ifUserCertificationEnabled = AnalysisMethodCertif.isUserCertificationEnabled();
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(ifUserCertificationEnabled[0].toString())){
            Object[] userCertified = AnalysisMethodCertif.isUserCertified(methodName, token.getUserName());
            if (Boolean.FALSE.equals(Boolean.valueOf(userCertified[0].toString()))) return new Object[]{userCertified[1]};
        }        
        if (resultStatusReviewed.equalsIgnoreCase(currResultStatus) || resultStatusCanceled.equalsIgnoreCase(currResultStatus)){
            messages.addMainForError(DataSampleAnalysisResultErrorTrapping.RESULT_LOCKED, new Object[]{currResultStatus, resultId.toString(), schemaConfigName});
            return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.RESULT_LOCKED, new Object[]{currResultStatus, resultId.toString(), schemaConfigName})};
        }            
        if ((currRawValue != null) && (currRawValue.equalsIgnoreCase(resultValue.toString()))){ 
            messages.addMainForError(DataSampleAnalysisResultErrorTrapping.SAME_RESULT_VALUE, new Object[]{resultId.toString(), schemaDataName, currRawValue});
            return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.SAME_RESULT_VALUE, new Object[]{resultId.toString(), schemaDataName, currRawValue})};
        }
        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), 
                new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()});
        if (LPPlatform.LAB_FALSE.equals(sampleData[0][0].toString())){ 
            messages.addMainForError(DataSampleErrorTrapping.SAMPLE_NOT_FOUND, new Object[]{sampleId.toString(), schemaDataName});
            return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_NOT_FOUND, new Object[]{sampleId.toString(), schemaDataName})};
        }
        String sampleConfigCode = (String) sampleData[0][1];
        Integer sampleConfigCodeVersion = Integer.valueOf(LPNulls.replaceNull(sampleData[0][2]).toString());
        sampleFieldName=LPArray.addValueToArray1D(sampleFieldName, new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()});
        sampleFieldValue=LPArray.addValueToArray1D(sampleFieldValue, new Object[]{sampleId, sampleConfigCode, sampleConfigCodeVersion});

        Object[][] sampleSpecData = Rdbms.getRecordFieldsByFilter(schemaDataName,  TblsData.TablesData.SAMPLE.getTableName(), 
                new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.SPEC_CODE.getName(), TblsData.Sample.SPEC_CODE_VERSION.getName(), TblsData.Sample.SPEC_VARIATION_NAME.getName(), 
                    TblsData.Sample.STATUS.getName()});
        String sampleSpecCode = null;
        Integer sampleSpecCodeVersion = null;
        String sampleSpecVariationName = null;
        if ((sampleSpecData[0][0] != null) && (!LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleSpecData[0][0].toString()))) {
            sampleSpecCode = sampleSpecData[0][0].toString();
            sampleSpecCodeVersion = Integer.valueOf(sampleSpecData[0][1].toString());
            sampleSpecVariationName = sampleSpecData[0][2].toString();
            sampleFieldName=LPArray.addValueToArray1D(sampleFieldName, new String[]{TblsData.Sample.SPEC_CODE.getName(), TblsData.Sample.SPEC_CODE_VERSION.getName(), TblsData.Sample.SPEC_VARIATION_NAME.getName()});
            sampleFieldValue=LPArray.addValueToArray1D(sampleFieldValue, new Object[]{sampleSpecCode, sampleSpecCodeVersion, sampleSpecVariationName});
        }
        Object[][] sampleRulesData = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.TablesConfig.SAMPLE_RULES.getTableName(), 
                new String[]{TblsCnfg.SampleRules.CODE.getName(), TblsCnfg.SampleRules.CODE_VERSION.getName()}, 
                new Object[]{sampleConfigCode, sampleConfigCodeVersion}, new String[]{TblsCnfg.SampleRules.TEST_ANALYST_REQUIRED.getName()});        
        if ( (sampleRulesData[0][0]!=null) && (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleRulesData[0][0].toString())) ) 
            return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_RULES_NOT_FOUND, 
                new Object[]{TblsCnfg.SampleRules.ANALYST_ASSIGNMENT_MODE.getName(), sampleConfigCode, sampleConfigCodeVersion, schemaConfigName})};
        Boolean analystRequired=false;
        if (sampleRulesData[0][0]!=null){analystRequired = Boolean.valueOf(sampleRulesData[0][0].toString());}
        if (Boolean.TRUE.equals(analystRequired)) {
            Object[][] testData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                    new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId}, 
                    new String[]{TblsData.SampleAnalysis.TEST_ID.getName(), TblsData.SampleAnalysis.ANALYST.getName(), TblsData.SampleAnalysis.ANALYST_ASSIGNED_ON.getName()});
            if ( (sampleRulesData[0][0]!=null) && (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleRulesData[0][0].toString())) ) {
                return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSIS_NOTFOUND, new Object[]{testId.toString(), schemaDataName})};
            }
            String testAnalyst = (String) testData[0][1];
            if (testAnalyst == null) 
                return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.RULE_ANALYST_NOT_ASSIGNED, new Object[]{testId.toString(), sampleConfigCode, sampleConfigCodeVersion.toString(), schemaDataName})};
            if (Boolean.FALSE.equals(testAnalyst.equalsIgnoreCase(token.getPersonName()))) 
                return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.RULE_OTHERANALYSIS_ENTER_RESULT, new Object[]{testId.toString(), testAnalyst, token.getPersonName(), schemaDataName})};
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
            fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.ENTERED_BY.getName()
                , TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.PRETTY_VALUE.getName()});
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEvalNoSpec, token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus, prettyValue[1]});
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);        
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                    resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
            }
        }
        Object[][] specLimits = ConfigSpecRule.getSpecLimitLimitIdFromSpecVariables(sampleSpecCode, sampleSpecCodeVersion, sampleSpecVariationName, analysis, methodName, methodVersion, paramName, 
                new String[]{TblsCnfg.SpecLimits.LIMIT_ID.getName(), TblsCnfg.SpecLimits.RULE_TYPE.getName(), TblsCnfg.SpecLimits.RULE_VARIABLES.getName(), TblsCnfg.SpecLimits.LIMIT_ID.getName(), 
                    TblsCnfg.SpecLimits.UOM.getName(), TblsCnfg.SpecLimits.UOM_CONVERSION_MODE.getName()});
        if ((LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimits[0][0].toString())) && (Boolean.FALSE.equals(Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND.getErrorCode().equalsIgnoreCase(specLimits[0][4].toString()))) ) {
            return new Object[]{LPArray.array2dTo1d(specLimits)};
        }
        if ((LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimits[0][0].toString())) && (Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND.getErrorCode().equalsIgnoreCase(specLimits[0][4].toString()))) {
            Object[] prettyValue = sarRawToPrettyResult(resultValue);
            fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.ENTERED_BY.getName()
                , TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.PRETTY_VALUE.getName()});
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEvalNoSpecParamLimit, token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus, prettyValue[1]});
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);                    
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                        resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId); 
            }
            return new Object[]{diagnoses};
        }
        Integer specLimitId = Integer.valueOf(LPNulls.replaceNull(specLimits[0][0]).toString());
        String ruleType = (String) specLimits[0][1];
        String specUomName = (String) specLimits[0][4];
        String specUomConversionMode = (String) specLimits[0][5];
        Boolean requiresUnitsConversion = false;
        BigDecimal resultConverted = null;
        resultUomName = LPNulls.replaceNull(resultUomName);
        if (resultUomName.length()>0) {
            if ((Boolean.FALSE.equals(resultUomName.equalsIgnoreCase(specUomName))) && (specUomConversionMode == null || specUomConversionMode.equalsIgnoreCase("DISABLED") || ((!specUomConversionMode.contains(resultUomName)) && !specUomConversionMode.equalsIgnoreCase("ALL")))) 
                return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.CONVERSION_NOT_ALLOWED, new Object[]{specUomConversionMode, specUomName, resultUomName,  specLimitId.toString(), schemaDataName})};            
            if (resultUomName.equalsIgnoreCase(specUomName)){
                requiresUnitsConversion = false;
                resultConverted=new BigDecimal(resultValue.toString());
            }else{                
                requiresUnitsConversion = true;
                UnitsOfMeasurement uom = new UnitsOfMeasurement(new BigDecimal(resultValue.toString()), resultUomName);
                uom.convertValue(specUomName);
                if (Boolean.FALSE.equals(uom.getConvertedFine())) 
                    return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataInvRetErrorTrapping.CONVERTER_FALSE, new Object[]{resultId.toString(), uom.getConversionErrorDetail()[3].toString(), schemaDataName})};
                resultConverted = uom.getConvertedQuantity();
            }
        }
        DataSpec resChkSpec = new DataSpec();
        Object[] resSpecEvaluation = null;
        ConfigSpecRule specRule = new ConfigSpecRule();
        specRule.specLimitsRule(specLimitId, null);
        if (Boolean.TRUE.equals(specRule.getRuleIsQualitative())){
                resSpecEvaluation = resChkSpec.resultCheck((String) resultValue, specRule.getQualitativeRule(), 
                        specRule.getQualitativeRuleValues(), specRule.getQualitativeRuleSeparator(), specRule.getQualitativeRuleListName());
                EnumIntMessages checkMsgCode=(EnumIntMessages) resSpecEvaluation[resSpecEvaluation.length - 1];
                String specEval = checkMsgCode.getErrorCode();
                
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation[0].toString())) {
                    return new Object[]{resSpecEvaluation};
                }      
                fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.SPEC_EVAL_DETAIL.getName()
                    , TblsData.SampleAnalysisResult.ENTERED_BY.getName(), TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName()});
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEval, resSpecEvaluation[resSpecEvaluation.length - 2]
                    , token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus});
                if (limitId==null || Boolean.FALSE.equals(Objects.equals(limitId, specLimitId))){
                    fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, specLimitId);
                }                
                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
                Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);        
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                        resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                  if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_UPON_CONTROL))
                    this.sar.sarControlAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                  if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_OOS))
                    this.sar.sarOOSAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                }                    
                return new Object[]{diagnoses};
        }
        if (Boolean.TRUE.equals(specRule.getRuleIsQuantitative())){
                try{
                    resultValue= new BigDecimal(resultValue.toString());
                }catch(Exception e){
                    return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.NOT_NUMERIC_VALUE, new Object[]{resultValue, specRule.getRuleRepresentation(), specLimitId.toString(), schemaDataName})};            
                }
                if (Boolean.TRUE.equals(specRule.getQuantitativeHasControl())){
                    if (Boolean.TRUE.equals(requiresUnitsConversion)){
                        resSpecEvaluation = resChkSpec.resultCheck(resultConverted, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinControl(), specRule.getMaxControl(), specRule.getMinControlIsStrict(), specRule.getMaxControlIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                    } else {
                        resSpecEvaluation = resChkSpec.resultCheck((BigDecimal) resultValue, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinControl(), specRule.getMaxControl(), specRule.getMinControlIsStrict(), specRule.getMaxControlIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                    }
                } else {
                    if (Boolean.TRUE.equals(requiresUnitsConversion)) {
                        resSpecEvaluation = resChkSpec.resultCheck(resultConverted, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                    } else {
                        resSpecEvaluation = resChkSpec.resultCheck((BigDecimal) resultValue, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                    }
                }
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation[0].toString())) {
                    return new Object[]{resSpecEvaluation};
                }
                EnumIntMessages checkMsgCode=(EnumIntMessages) resSpecEvaluation[resSpecEvaluation.length - 1];
                String specEval = checkMsgCode.getErrorCode();
                String specEvalDetail = (String) resSpecEvaluation[resSpecEvaluation.length - 2];
                if (Boolean.TRUE.equals(requiresUnitsConversion)) specEvalDetail = specEvalDetail + " in " + specUomName;

                fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.SPEC_EVAL_DETAIL.getName()
                    , TblsData.SampleAnalysisResult.ENTERED_BY.getName(), TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName()});
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEval, specEvalDetail, token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus});
                if (limitId==null || Boolean.FALSE.equals(Objects.equals(limitId, specLimitId))){
                    fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, specLimitId);
                }                                
                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
                Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);        
                Object[] sampleAuditAdd=new Object[0];
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    SampleAudit smpAudit = new SampleAudit();
                    sampleAuditAdd=smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                            resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
                }                
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) 
                    DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    checkMsgCode=(EnumIntMessages) resSpecEvaluation[resSpecEvaluation.length - 1];
                    specEval = checkMsgCode.getErrorCode();
                  if (specEval.toUpperCase().contains(ConfigSpecRule.SPEC_WORD_FOR_UPON_CONTROL))
                    this.sar.sarControlAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                  if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().toUpperCase().contains(ConfigSpecRule.SPEC_WORD_FOR_OOS))
                    this.sar.sarOOSAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                }                    
                return new Object[]{diagnoses, sampleAuditAdd};
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.SPECRULE_NOTIMPLEMENTED, new Object[]{resultId.toString(), schemaDataName, ruleType});
    }
    public Object[] sampleAnalysisResultEntrySecondEntry(Integer resultId, Object resultValue, DataSample dataSample, String alternativeAuditEntry, String alternativeAuditClass) {           
    try{
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Token token=instanceForActions.getToken();
        String procInstanceName=instanceForActions.getProcedureInstance();
        ResponseMessages messages = instanceForActions.getMessages();
        
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
        fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysisResult.RAW_VALUE.getName());
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, resultValue);
        Object[][] resultData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY.getTableName(), 
                new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()}, new Object[]{resultId}, 
                new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.ANALYSIS.getName(), 
                    TblsData.SampleAnalysisResult.METHOD_NAME.getName(), TblsData.SampleAnalysisResult.METHOD_VERSION.getName(), TblsData.SampleAnalysisResult.PARAM_NAME.getName(), 
                    TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.RAW_VALUE.getName(), TblsData.SampleAnalysisResult.UOM.getName(), 
                    TblsData.SampleAnalysisResult.UOM_CONVERSION_MODE.getName(), TblsData.SampleAnalysisResult.LIMIT_ID.getName()});
        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) 
            return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.NOT_FOUND, new Object[]{resultId.toString(), schemaDataName})};
        Integer sampleId = Integer.valueOf(LPNulls.replaceNull(resultData[0][0]).toString());
        Integer testId = Integer.valueOf(LPNulls.replaceNull(resultData[0][1]).toString());
        String analysis = (String) resultData[0][2];
        String methodName = (String) resultData[0][3];
        Integer methodVersion = Integer.valueOf(LPNulls.replaceNull(resultData[0][4]).toString());
        String paramName = (String) resultData[0][5];
        String currResultStatus = (String) resultData[0][6];
        String currRawValue = (String) resultData[0][7];
        String resultUomName = (String) resultData[0][8];
        Integer limitId =-999;
        if (resultData[0][10]!=null && resultData[0][10].toString().length()>0)
            limitId = Integer.valueOf(LPNulls.replaceNull(resultData[0][10]).toString());
        
        Object[] ifUserCertificationEnabled = AnalysisMethodCertif.isUserCertificationEnabled();
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(ifUserCertificationEnabled[0].toString())){
            Object[] userCertified = AnalysisMethodCertif.isUserCertified(methodName, token.getUserName());
            if (Boolean.FALSE.equals(Boolean.valueOf(userCertified[0].toString()))) return new Object[]{userCertified[1]};
        }        
        if (resultStatusReviewed.equalsIgnoreCase(currResultStatus) || resultStatusCanceled.equalsIgnoreCase(currResultStatus)){
            messages.addMainForError(DataSampleAnalysisResultErrorTrapping.RESULT_LOCKED, new Object[]{currResultStatus, resultId.toString(), schemaConfigName});
            return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.RESULT_LOCKED, new Object[]{currResultStatus, resultId.toString(), schemaConfigName})};
        }            
        if ((currRawValue != null) && (currRawValue.equalsIgnoreCase(resultValue.toString()))){ 
            messages.addMainForError(DataSampleAnalysisResultErrorTrapping.SAME_RESULT_VALUE, new Object[]{resultId.toString(), schemaDataName, currRawValue});
            return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.SAME_RESULT_VALUE, new Object[]{resultId.toString(), schemaDataName, currRawValue})};
        }
        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), 
                new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()});
        if (LPPlatform.LAB_FALSE.equals(sampleData[0][0].toString())){ 
            messages.addMainForError(DataSampleErrorTrapping.SAMPLE_NOT_FOUND, new Object[]{sampleId.toString(), schemaDataName});
            return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_NOT_FOUND, new Object[]{sampleId.toString(), schemaDataName})};
        }
        String sampleConfigCode = (String) sampleData[0][1];
        Integer sampleConfigCodeVersion = Integer.valueOf(LPNulls.replaceNull(sampleData[0][2]).toString());
        sampleFieldName=LPArray.addValueToArray1D(sampleFieldName, new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()});
        sampleFieldValue=LPArray.addValueToArray1D(sampleFieldValue, new Object[]{sampleId, sampleConfigCode, sampleConfigCodeVersion});

        Object[][] sampleSpecData = Rdbms.getRecordFieldsByFilter(schemaDataName,  TblsData.TablesData.SAMPLE.getTableName(), 
                new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.SPEC_CODE.getName(), TblsData.Sample.SPEC_CODE_VERSION.getName(), TblsData.Sample.SPEC_VARIATION_NAME.getName(), 
                    TblsData.Sample.STATUS.getName()});
        String sampleSpecCode = null;
        Integer sampleSpecCodeVersion = null;
        String sampleSpecVariationName = null;
        if ((sampleSpecData[0][0] != null) && (!LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleSpecData[0][0].toString()))) {
            sampleSpecCode = sampleSpecData[0][0].toString();
            sampleSpecCodeVersion = Integer.valueOf(sampleSpecData[0][1].toString());
            sampleSpecVariationName = sampleSpecData[0][2].toString();
            sampleFieldName=LPArray.addValueToArray1D(sampleFieldName, new String[]{TblsData.Sample.SPEC_CODE.getName(), TblsData.Sample.SPEC_CODE_VERSION.getName(), TblsData.Sample.SPEC_VARIATION_NAME.getName()});
            sampleFieldValue=LPArray.addValueToArray1D(sampleFieldValue, new Object[]{sampleSpecCode, sampleSpecCodeVersion, sampleSpecVariationName});
        }
        Object[][] sampleRulesData = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.TablesConfig.SAMPLE_RULES.getTableName(), 
                new String[]{TblsCnfg.SampleRules.CODE.getName(), TblsCnfg.SampleRules.CODE_VERSION.getName()}, 
                new Object[]{sampleConfigCode, sampleConfigCodeVersion}, new String[]{TblsCnfg.SampleRules.TEST_ANALYST_REQUIRED.getName()});        
        if ( (sampleRulesData[0][0]!=null) && (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleRulesData[0][0].toString())) ) 
            return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_RULES_NOT_FOUND, 
                new Object[]{TblsCnfg.SampleRules.ANALYST_ASSIGNMENT_MODE.getName(), sampleConfigCode, sampleConfigCodeVersion, schemaConfigName})};
        Boolean analystRequired=false;
        if (sampleRulesData[0][0]!=null){analystRequired = Boolean.valueOf(sampleRulesData[0][0].toString());}
        if (Boolean.TRUE.equals(analystRequired)) {
            Object[][] testData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                    new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId}, 
                    new String[]{TblsData.SampleAnalysis.TEST_ID.getName(), TblsData.SampleAnalysis.ANALYST.getName(), TblsData.SampleAnalysis.ANALYST_ASSIGNED_ON.getName()});
            if ( (sampleRulesData[0][0]!=null) && (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleRulesData[0][0].toString())) ) {
                return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSIS_NOTFOUND, new Object[]{testId.toString(), schemaDataName})};
            }
            String testAnalyst = (String) testData[0][1];
            if (testAnalyst == null) 
                return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.RULE_ANALYST_NOT_ASSIGNED, new Object[]{testId.toString(), sampleConfigCode, sampleConfigCodeVersion.toString(), schemaDataName})};
            if (Boolean.FALSE.equals(testAnalyst.equalsIgnoreCase(token.getPersonName())))
                return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.RULE_OTHERANALYSIS_ENTER_RESULT, new Object[]{testId.toString(), testAnalyst, token.getPersonName(), schemaDataName})};
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
            fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.ENTERED_BY.getName()
                , TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.PRETTY_VALUE.getName()});
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEvalNoSpec, token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus, prettyValue[1]});
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);                    
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED_SECONDENTRY, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY.getTableName(), 
                    resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
            }
        }
        Object[][] specLimits = ConfigSpecRule.getSpecLimitLimitIdFromSpecVariables(sampleSpecCode, sampleSpecCodeVersion, sampleSpecVariationName, analysis, methodName, methodVersion, paramName, 
                new String[]{TblsCnfg.SpecLimits.LIMIT_ID.getName(), TblsCnfg.SpecLimits.RULE_TYPE.getName(), TblsCnfg.SpecLimits.RULE_VARIABLES.getName(), TblsCnfg.SpecLimits.LIMIT_ID.getName(), 
                    TblsCnfg.SpecLimits.UOM.getName(), TblsCnfg.SpecLimits.UOM_CONVERSION_MODE.getName()});
        if ((LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimits[0][0].toString())) && (Boolean.FALSE.equals(Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND.getErrorCode().equalsIgnoreCase(specLimits[0][4].toString())))) {
            return new Object[]{LPArray.array2dTo1d(specLimits)};
        }
        if ((LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimits[0][0].toString())) && (Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND.getErrorCode().equalsIgnoreCase(specLimits[0][4].toString()))) {
            Object[] prettyValue = sarRawToPrettyResult(resultValue);
            fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.ENTERED_BY.getName()
                , TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.PRETTY_VALUE.getName()});
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEvalNoSpecParamLimit, token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus, prettyValue[1]});
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);        
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED_SECONDENTRY, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY.getTableName(), 
                        resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
            }
            return new Object[]{diagnoses};
        }
        Integer specLimitId = Integer.valueOf(LPNulls.replaceNull(specLimits[0][0]).toString());
        String ruleType = (String) specLimits[0][1];
        String specUomName = (String) specLimits[0][4];
        String specUomConversionMode = (String) specLimits[0][5];
        Boolean requiresUnitsConversion = false;
        BigDecimal resultConverted = null;
        resultUomName = LPNulls.replaceNull(resultUomName);
        if (resultUomName.length()>0) {
            if ((Boolean.FALSE.equals(resultUomName.equalsIgnoreCase(specUomName))) && (specUomConversionMode == null || specUomConversionMode.equalsIgnoreCase("DISABLED") || ((!specUomConversionMode.contains(resultUomName)) && !specUomConversionMode.equalsIgnoreCase("ALL")))) 
                return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.CONVERSION_NOT_ALLOWED, new Object[]{specUomConversionMode, specUomName, resultUomName,  specLimitId.toString(), schemaDataName})};            
            if (resultUomName.equalsIgnoreCase(specUomName)){
                requiresUnitsConversion = false;
                resultConverted=new BigDecimal(resultValue.toString());
            }else{                
                requiresUnitsConversion = true;
                UnitsOfMeasurement uom = new UnitsOfMeasurement(new BigDecimal(resultValue.toString()), resultUomName);
                uom.convertValue(specUomName);
                if (Boolean.FALSE.equals(uom.getConvertedFine())) 
                    return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataInvRetErrorTrapping.CONVERTER_FALSE, new Object[]{resultId.toString(), uom.getConversionErrorDetail()[3].toString(), schemaDataName})};
                resultConverted = uom.getConvertedQuantity();
            }
        }
        DataSpec resChkSpec = new DataSpec();
        Object[] resSpecEvaluation = null;
        ConfigSpecRule specRule = new ConfigSpecRule();
        specRule.specLimitsRule(specLimitId, null);
        if (Boolean.TRUE.equals(specRule.getRuleIsQualitative())){
                resSpecEvaluation = resChkSpec.resultCheck((String) resultValue, specRule.getQualitativeRule(), 
                        specRule.getQualitativeRuleValues(), specRule.getQualitativeRuleSeparator(), specRule.getQualitativeRuleListName());
                EnumIntMessages checkMsgCode=(EnumIntMessages) resSpecEvaluation[resSpecEvaluation.length - 1];
                String specEval = checkMsgCode.getErrorCode();
                
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation[0].toString())) {
                    return new Object[]{resSpecEvaluation};
                }      
                fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.SPEC_EVAL_DETAIL.getName()
                    , TblsData.SampleAnalysisResult.ENTERED_BY.getName(), TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName()});
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEval, resSpecEvaluation[resSpecEvaluation.length - 2]
                    , token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus});
                if (limitId==null || Boolean.FALSE.equals(Objects.equals(limitId, specLimitId))){
                    fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, specLimitId);
                }                
                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
                Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);        
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED_SECONDENTRY, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY.getTableName(), 
                        resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                  if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_UPON_CONTROL))
                    this.sar.sarControlAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                  if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_OOS))
                    this.sar.sarOOSAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                }                    
                return new Object[]{diagnoses};
        }
        if (Boolean.TRUE.equals(specRule.getRuleIsQuantitative())){
                try{
                    resultValue= new BigDecimal(resultValue.toString());
                }catch(Exception e){
                    return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.NOT_NUMERIC_VALUE, new Object[]{resultValue, specRule.getRuleRepresentation(), specLimitId.toString(), schemaDataName})};            
                }
                if (Boolean.TRUE.equals(specRule.getQuantitativeHasControl())){
                    if (Boolean.TRUE.equals(requiresUnitsConversion)) {
                        resSpecEvaluation = resChkSpec.resultCheck(resultConverted, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinControl(), specRule.getMaxControl(), specRule.getMinControlIsStrict(), specRule.getMaxControlIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                    } else {
                        resSpecEvaluation = resChkSpec.resultCheck((BigDecimal) resultValue, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinControl(), specRule.getMaxControl(), specRule.getMinControlIsStrict(), specRule.getMaxControlIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                    }
                } else {
                    if (Boolean.TRUE.equals(requiresUnitsConversion)) {
                        resSpecEvaluation = resChkSpec.resultCheck(resultConverted, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                    } else {
                        resSpecEvaluation = resChkSpec.resultCheck((BigDecimal) resultValue, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                    }
                }
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation[0].toString())) {
                    return new Object[]{resSpecEvaluation};
                }
                EnumIntMessages checkMsgCode=(EnumIntMessages) resSpecEvaluation[resSpecEvaluation.length - 1];
                String specEval = checkMsgCode.getErrorCode();
                String specEvalDetail = (String) resSpecEvaluation[resSpecEvaluation.length - 2];
                if (Boolean.TRUE.equals(requiresUnitsConversion)) specEvalDetail = specEvalDetail + " in " + specUomName;

                fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.SPEC_EVAL_DETAIL.getName()
                    , TblsData.SampleAnalysisResult.ENTERED_BY.getName(), TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName()});
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEval, specEvalDetail, token.getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus});
                if (limitId==null || Boolean.FALSE.equals(Objects.equals(limitId, specLimitId))){
                    fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, specLimitId);
                }                                
                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
                Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);        
                Object[] sampleAuditAdd=new Object[0];
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    SampleAudit smpAudit = new SampleAudit();
                    sampleAuditAdd=smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED_SECONDENTRY, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY.getTableName(), 
                            resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
                }                
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) 
                    DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    checkMsgCode=(EnumIntMessages) resSpecEvaluation[resSpecEvaluation.length - 1];
                    specEval = checkMsgCode.getErrorCode();
                  if (specEval.toUpperCase().contains(ConfigSpecRule.SPEC_WORD_FOR_UPON_CONTROL))
                    this.sar.sarControlAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                  if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().toUpperCase().contains(ConfigSpecRule.SPEC_WORD_FOR_OOS))
                    this.sar.sarOOSAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                }                    
                return new Object[]{diagnoses, sampleAuditAdd};
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.SPECRULE_NOTIMPLEMENTED, new Object[]{resultId.toString(), schemaDataName, ruleType});
    }catch(NumberFormatException e){
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, e.getMessage(), null);
    }
    }
    public Object[] sarChangeUom(Integer resultId, String newuom, DataSample dataSample) {       
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        Object[][] resultInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()}, new Object[]{resultId}, 
                new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName(), TblsData.SampleAnalysisResult.PARAM_NAME.getName(), TblsData.SampleAnalysisResult.UOM.getName(), 
                    TblsData.SampleAnalysisResult.RAW_VALUE.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.SAMPLE_ID.getName(),
                    TblsData.SampleAnalysisResult.UOM_CONVERSION_MODE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString())) return LPArray.array2dTo1d(resultInfo);
        String paramName = resultInfo[0][1].toString();
        String curruom = resultInfo[0][2].toString();
        String currValue = resultInfo[0][3].toString();        
        Integer testId = Integer.valueOf(resultInfo[0][4].toString());
        Integer sampleId = Integer.valueOf(resultInfo[0][5].toString());
        String specUomConversionMode = resultInfo[0][6].toString();
        if (LPNulls.replaceNull(currValue).length()==0)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.CURRENTRESULT_ISEMPTY, new Object[]{paramName, sampleId});
        if (specUomConversionMode == null || specUomConversionMode.equalsIgnoreCase("DISABLED") || ((Boolean.FALSE.equals(specUomConversionMode.contains(newuom))) && Boolean.FALSE.equals(specUomConversionMode.equalsIgnoreCase("ALL"))) ) 
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.CONVERSION_NOT_ALLOWED, new Object[]{specUomConversionMode, newuom, curruom, resultId.toString(), schemaDataName});
        UnitsOfMeasurement uom = new UnitsOfMeasurement(new BigDecimal(currValue), curruom);
        uom.convertValue(newuom);
        if (Boolean.FALSE.equals(uom.getConvertedFine())) 
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataInvRetErrorTrapping.CONVERTER_FALSE, new Object[]{resultId.toString(), uom.getConversionErrorDetail()[3].toString(), schemaDataName});
        BigDecimal resultConverted = uom.getConvertedQuantity();
        String[] updFieldNames = new String[]{TblsData.SampleAnalysisResult.RAW_VALUE.getName(), TblsData.SampleAnalysisResult.UOM.getName()};
        Object[] updFieldValues = new Object[]{resultConverted.toString(), newuom};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
        Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, updFieldNames), updFieldValues, sqlWhere, null);        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateRecordFieldsByFilter[0].toString())) return updateRecordFieldsByFilter;
        SampleAudit smpAudit = new SampleAudit();
        EnumIntAuditEvents auditActionName = SampleAudit.DataSampleAnalysisResultAuditEvents.UOM_CHANGED; 
        smpAudit.sampleAuditAdd(auditActionName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
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
        Integer cancelScopeId=Integer.valueOf(LPNulls.replaceNull(scopeInfo[1]).toString());
        String cancelScopeTable=scopeInfo[2].toString();
        
        Object[] samplesToUnCancel = new Object[0];
        Object[] testsToUnCancel = new Object[0];
        String[] diagPerResult = new String[0];
       
        Object[][] resultInfo = null;
        resultInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                new String[]{cancelScope}, new Object[]{cancelScopeId}, 
                new String[]{TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.STATUS_PREVIOUS.getName(), TblsData.SampleAnalysisResult.RESULT_ID.getName(), 
                    TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.SAMPLE_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString())) {
            String[] filter = new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(sampleId).toString() + " " + TblsData.SampleAnalysisResult.TEST_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(testId).toString() +
                    " " + TblsData.SampleAnalysisResult.RESULT_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(resultId).toString()};
            errorCode = DataSampleErrorTrapping.SAMPLE_NOT_FOUND.getErrorCode();
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, new Object[]{Arrays.toString(filter), schemaDataName});
        }else{
            if (TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName().equalsIgnoreCase(cancelScopeTable)){
                for (Integer iResToCancel = 0; iResToCancel < resultInfo.length; iResToCancel++) {
                    String currResultStatus = (String) resultInfo[iResToCancel][0];
                    String statusPrevious = (String) resultInfo[iResToCancel][1];
                    String rsltIdStr=resultInfo[iResToCancel][2].toString();
                    resultId = Integer.valueOf(rsltIdStr);                
                    testId = Integer.valueOf(LPNulls.replaceNull(resultInfo[iResToCancel][3]).toString());
                    sampleId = Integer.valueOf(LPNulls.replaceNull(resultInfo[iResToCancel][4]).toString());
                    if (Boolean.FALSE.equals((sampleAnalysisResultStatusCanceled.equalsIgnoreCase(currResultStatus)))) {
                        diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.FORRESULTUNCANCEL_STATUS_NOT_EXPECTED, new Object[]{resultInfo[0][0].toString(), sampleAnalysisResultStatusCanceled, schemaDataName});
                        diagPerResult = LPArray.addValueToArray1D(diagPerResult, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()+" " + resultId.toString() + " not uncanceled because current status is " + currResultStatus);
                    } else {
                        resultId = Integer.valueOf(LPNulls.replaceNull(resultInfo[iResToCancel][2]).toString());
                        String[] updFldNames=new String[]{TblsData.SampleAnalysisResult.STATUS_PREVIOUS.getName(),TblsData.SampleAnalysisResult.STATUS.getName()};
                        Object[] updFldValues=new Object[]{sampleAnalysisResultStatusCanceled, statusPrevious};
                        SqlWhere sqlWhere = new SqlWhere();
                        sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
                        diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                                EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, updFldNames), updFldValues, sqlWhere, null);        
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                            SampleAudit smpAudit = new SampleAudit();
                            smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_UNCANCELED, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), +
                                    resultId, sampleId, testId, resultId, updFldNames, updFldValues);
                        }
                        diagPerResult = LPArray.addValueToArray1D(diagPerResult, "Result " + resultId.toString() + " UNCANCELED ");
                    }
                    if ((cancelScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.SAMPLE_ID.getName())) && (Boolean.FALSE.equals(LPArray.valueInArray(samplesToUnCancel, sampleId)))) {
                        samplesToUnCancel = LPArray.addValueToArray1D(samplesToUnCancel, sampleId);
                    }
                    if ((cancelScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.SAMPLE_ID.getName()) || cancelScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.TEST_ID.getName())) && (Boolean.FALSE.equals(LPArray.valueInArray(testsToUnCancel, testId)))) {
                        testsToUnCancel = LPArray.addValueToArray1D(testsToUnCancel, testId);
                    }
                }
            }
        }
        if (TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName().equalsIgnoreCase(cancelScopeTable) || TblsData.TablesData.SAMPLE_ANALYSIS.getTableName().equalsIgnoreCase(cancelScopeTable)){        
            if (testsToUnCancel.length==0 && cancelScope.equalsIgnoreCase(TblsData.SampleAnalysis.TEST_ID.getName()))
                testsToUnCancel = LPArray.addValueToArray1D(testsToUnCancel, cancelScopeId);                
            for (Integer iTstToUnCancel = 0; iTstToUnCancel < testsToUnCancel.length; iTstToUnCancel++) {
                Integer currTest = Integer.valueOf(LPNulls.replaceNull(testsToUnCancel[iTstToUnCancel]).toString()); 
                Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                        new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{currTest}, 
                        new String[]{TblsData.SampleAnalysis.STATUS.getName(), TblsData.SampleAnalysis.STATUS_PREVIOUS.getName(), TblsData.SampleAnalysis.TEST_ID.getName(), 
                            TblsData.SampleAnalysis.SAMPLE_ID.getName()});
                String currStatus = (String) objectInfo[0][0];
                String currPrevStatus = (String) objectInfo[0][1];
                if ((sampleAnalysisStatusCanceled.equalsIgnoreCase(currStatus)) && (currTest != null)) {
                    String[] updFldNames=new String[]{TblsData.SampleAnalysis.STATUS.getName(), TblsData.SampleAnalysis.STATUS_PREVIOUS.getName()};
                    Object[] updFldValues=new Object[]{currPrevStatus, sampleAnalysisResultStatusCanceled};
                    SqlWhere sqlWhere = new SqlWhere();
                    sqlWhere.addConstraint(TblsData.SampleAnalysis.TEST_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{currTest}, "");
                    diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS,
                        EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS, updFldNames), updFldValues, sqlWhere, null);                                            
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_UNCANCELED, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                            currTest, sampleId, currTest, null, updFldNames, updFldValues);
                    }
                } else {
                    return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLE_ANALYSIS_CANNOT_BE_UNCANCELLED, new Object[]{currTest, currStatus});
                }
            }
        }
        if (samplesToUnCancel.length==0 && cancelScope.equalsIgnoreCase(TblsData.Sample.SAMPLE_ID.getName()))
            samplesToUnCancel = LPArray.addValueToArray1D(samplesToUnCancel, cancelScopeId);        
        for (Integer iSmpToUnCancel = 0; iSmpToUnCancel < samplesToUnCancel.length; iSmpToUnCancel++) {
            Integer currSample = Integer.valueOf(LPNulls.replaceNull(samplesToUnCancel[iSmpToUnCancel]).toString());
            Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{currSample}, 
                    new String[]{TblsData.Sample.STATUS.getName(), TblsData.Sample.STATUS_PREVIOUS.getName()});
            String currStatus = (String) objectInfo[0][0];
            String currPrevStatus = (String) objectInfo[0][1];
            if ((SampleStatuses.CANCELED.getStatusCode("").equalsIgnoreCase(currStatus)) && (currSample != null)) {
                String[] updFldNames=new String[]{TblsData.Sample.STATUS.getName(), TblsData.Sample.STATUS_PREVIOUS.getName()};
                Object[] updFldValues=new Object[]{currPrevStatus, sampleAnalysisResultStatusCanceled};
                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsData.Sample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{currSample}, "");
                diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE,
                        EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE, updFldNames), updFldValues, sqlWhere, null);                        
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.SAMPLE_UNCANCELED, TblsData.TablesData.SAMPLE.getTableName(), 
                            currSample, currSample, null, null, updFldNames, updFldValues);
                }
            } else {                
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_CANNOT_BE_UNCANCELLED, new Object[]{currSample, currStatus});
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
        Integer reviewScopeId=Integer.valueOf(LPNulls.replaceNull(scopeInfo[1]).toString());
        String cancelScopeTable=scopeInfo[2].toString();
        
        Object[] samplesToUnReview = new Object[0];
        Object[] testsToUnReview = new Object[0];
        String[] diagPerResult = new String[0];

        Object[][] resultInfo = null;
        resultInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                new String[]{reviewScope}, new Object[]{reviewScopeId}, 
                new String[]{TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.STATUS_PREVIOUS.getName(), TblsData.SampleAnalysisResult.RESULT_ID.getName(), 
                    TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.SAMPLE_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString())) {
            String[] filter = new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(sampleId).toString() + " " + TblsData.SampleAnalysisResult.TEST_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(testId).toString() +
                    " " + TblsData.SampleAnalysisResult.RESULT_ID.getName() + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + LPNulls.replaceNull(resultId).toString()};
            errorCode = DataSampleErrorTrapping.SAMPLE_NOT_FOUND.getErrorCode();
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, new Object[]{Arrays.toString(filter), schemaDataName});
        }else{
            if (TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName().equalsIgnoreCase(cancelScopeTable)){            
                for (Integer iResToReview = 0; iResToReview < resultInfo.length; iResToReview++) {
                    String currResultStatus = (String) resultInfo[iResToReview][0];
                    String statusPrevious = (String) resultInfo[iResToReview][1];
                    String rsltIdStr=resultInfo[iResToReview][2].toString();
                    resultId = Integer.valueOf(rsltIdStr);                
                    testId = Integer.valueOf(LPNulls.replaceNull(resultInfo[iResToReview][3]).toString());
                    sampleId = Integer.valueOf(LPNulls.replaceNull(resultInfo[iResToReview][4]).toString());
                    if (Boolean.FALSE.equals((sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currResultStatus)))){
                        diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.FORRESULTUNREVIEW_STATUS_NOT_EXPECTED, new Object[]{resultInfo[0][0].toString(), sampleAnalysisResultStatusReviewed, schemaDataName});
                        diagPerResult = LPArray.addValueToArray1D(diagPerResult, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()+" " + resultId.toString() + " not unreviewed because current status is " + currResultStatus);
                    } else {
                        String[] updFldNames=new String[]{TblsData.SampleAnalysisResult.STATUS_PREVIOUS.getName(), TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.REVIEWED.getName(), TblsData.SampleAnalysisResult.REVIEWED_ON.getName(), TblsData.SampleAnalysisResult.REVIEWED_BY.getName()};
                        Object[] updFldValues=new Object[]{sampleAnalysisResultStatusReviewed, statusPrevious, false, "NULL>>>DATE", "NULL>>>STRING"};
                        SqlWhere sqlWhere = new SqlWhere();
                        sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
                        diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                                EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, updFldNames), updFldValues, sqlWhere, null);        
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                            SampleAudit smpAudit = new SampleAudit();
                            smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_UNCANCELED, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                                resultId, sampleId, testId, resultId, updFldNames, updFldValues);
                        }
                        diagPerResult = LPArray.addValueToArray1D(diagPerResult, "Result " + resultId.toString() + " UNREVIEWED ");
                    }
                    if ((reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.SAMPLE_ID.getName())) && (Boolean.FALSE.equals(LPArray.valueInArray(samplesToUnReview, sampleId)))) {
                        samplesToUnReview = LPArray.addValueToArray1D(samplesToUnReview, sampleId);
                    }
                    if ((reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.SAMPLE_ID.getName()) || reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.TEST_ID.getName())) && (Boolean.FALSE.equals(LPArray.valueInArray(testsToUnReview, testId)))) {
                        testsToUnReview = LPArray.addValueToArray1D(testsToUnReview, testId);
                    }
                }
            }
        }
        if (TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName().equalsIgnoreCase(cancelScopeTable) || TblsData.TablesData.SAMPLE_ANALYSIS.getTableName().equalsIgnoreCase(cancelScopeTable)){                
            if (testsToUnReview.length==0 && reviewScope.equalsIgnoreCase(TblsData.SampleAnalysis.TEST_ID.getName()))
                testsToUnReview = LPArray.addValueToArray1D(testsToUnReview, reviewScopeId);                
            for (Integer iTstToUnreview = 0; iTstToUnreview < testsToUnReview.length; iTstToUnreview++) {
                Integer currTest = Integer.valueOf(LPNulls.replaceNull(testsToUnReview[iTstToUnreview]).toString()); 
                Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                        new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{currTest}, 
                        new String[]{TblsData.SampleAnalysis.STATUS.getName(), TblsData.SampleAnalysis.STATUS_PREVIOUS.getName(), TblsData.SampleAnalysis.TEST_ID.getName(), 
                            TblsData.SampleAnalysis.SAMPLE_ID.getName()});
                String currStatus = (String) objectInfo[0][0];
                String currPrevStatus = (String) objectInfo[0][1];
                if ((sampleAnalysisStatusReviewed.equalsIgnoreCase(currStatus)) && (currTest != null)) {
                    String[] updFldNames=new String[]{TblsData.SampleAnalysis.STATUS.getName(), TblsData.SampleAnalysis.STATUS_PREVIOUS.getName(), TblsData.SampleAnalysis.REVIEWED.getName(), TblsData.SampleAnalysis.REVIEWED_ON.getName(), TblsData.SampleAnalysis.REVIEWED_BY.getName()};
                    Object[] updFldValues=new Object[]{currPrevStatus, sampleAnalysisResultStatusReviewed, false, "NULL>>>DATE", "NULL>>>STRING"};
                    SqlWhere sqlWhere = new SqlWhere();
                    sqlWhere.addConstraint(TblsData.SampleAnalysis.TEST_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{currTest}, "");
                    diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS,
                            EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS, updFldNames), updFldValues, sqlWhere, null);        
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_UNREVIEWED, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                            currTest, sampleId, currTest, null, updFldNames, updFldValues);
                    }
                } else {
                    diagnoses[5] = "The "+TblsData.TablesData.SAMPLE_ANALYSIS.getTableName()+" "+currTest+" has status "+currStatus+" then cannot be unreviewed in schema "+schemaDataName;                 
                }
            }
        }
        if (samplesToUnReview.length==0 && reviewScope.equalsIgnoreCase(TblsData.Sample.SAMPLE_ID.getName()))
            samplesToUnReview = LPArray.addValueToArray1D(samplesToUnReview, reviewScopeId);        
        for (Integer iSmpToUnReview = 0; iSmpToUnReview < samplesToUnReview.length; iSmpToUnReview++) {
            Integer currSample = Integer.valueOf(LPNulls.replaceNull(samplesToUnReview[iSmpToUnReview]).toString());
            Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{currSample}, 
                    new String[]{TblsData.Sample.STATUS.getName(), TblsData.Sample.STATUS_PREVIOUS.getName()});
            String currStatus = (String) objectInfo[0][0];
            String currPrevStatus = (String) objectInfo[0][1];
            if ((sampleStatusReviewed.equalsIgnoreCase(currStatus)) && (currSample != null)) {
                String[] updFldNames=new String[]{TblsData.Sample.STATUS.getName(), TblsData.Sample.STATUS_PREVIOUS.getName(), TblsData.Sample.REVIEWED.getName(), TblsData.Sample.REVIEWED_ON.getName(), TblsData.Sample.REVIEWED_BY.getName()};
                Object[] updFldValues=new Object[]{currPrevStatus, sampleAnalysisResultStatusReviewed, false, "NULL>>>DATE", "NULL>>>STRING"};
                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsData.Sample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{currSample}, "");
                diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE, updFldNames), updFldValues, sqlWhere, null);        
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {                    
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.SAMPLE_UNREVIEWED, TblsData.TablesData.SAMPLE.getTableName(), 
                        currSample, currSample, null, null, updFldNames, updFldValues);
                }
            } else {
                diagnoses[5] = "The "+TblsData.TablesData.SAMPLE.getTableName()+" "+currSample+" has status "+currStatus+" then cannot be unreviewed in schema "+schemaDataName;
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
        Integer cancelScopeId=Integer.valueOf(LPNulls.replaceNull(scopeInfo[1]).toString());
        String cancelScopeTable=scopeInfo[2].toString();
        Object[][] objectInfo = null;
        objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, cancelScopeTable, 
                new String[]{cancelScope}, new Object[]{cancelScopeId}, 
                new String[]{TblsData.SampleAnalysisResult.STATUS.getName(), cancelScope});
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(objectInfo[0][0].toString()))) {
            objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                    new String[]{cancelScope}, new Object[]{cancelScopeId}, 
                    new String[]{TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.RESULT_ID.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.SAMPLE_ID.getName()});
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(objectInfo[0][0].toString()))) {
                for (Integer iResToCancel = 0; iResToCancel < objectInfo.length; iResToCancel++) {
                    String currStatus = (String) objectInfo[iResToCancel][0];
                    if (Boolean.FALSE.equals((sampleAnalysisResultStatusCanceled.equalsIgnoreCase(currStatus)))) {
                        String rsltIdStr=objectInfo[iResToCancel][2].toString();
                        resultId = Integer.valueOf(rsltIdStr);                
                        testId = Integer.valueOf(LPNulls.replaceNull(objectInfo[iResToCancel][2]).toString()); 
                        sampleId = Integer.valueOf(LPNulls.replaceNull(objectInfo[iResToCancel][3]).toString()); 
                        if (Boolean.FALSE.equals((sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currStatus)))) {
                            String[] updFldNames=new String[]{TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.STATUS_PREVIOUS.getName()};
                            Object[] updFldValues=new Object[]{sampleAnalysisResultStatusCanceled, currStatus};
                            SqlWhere sqlWhere = new SqlWhere();
                            sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
                            diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, updFldNames), updFldValues, sqlWhere, null);        
                            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                                SampleAudit smpAudit = new SampleAudit();
                                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_CANCELED, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                                    resultId, sampleId, testId, resultId, updFldNames, updFldValues);
                            }
                        } else 
                            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.FORRESULTCANCELATION_STATUS_NOT_EXPECTED, new Object[]{resultId.toString(), currStatus, schemaDataName});
                    }
                    if ((cancelScope.equalsIgnoreCase(TblsData.Sample.SAMPLE_ID.getName())) && (Boolean.FALSE.equals(LPArray.valueInArray(samplesToCancel, sampleId))))
                        samplesToCancel = LPArray.addValueToArray1D(samplesToCancel, sampleId);
                    if ((cancelScope.equalsIgnoreCase(TblsData.SampleAnalysis.SAMPLE_ID.getName()) || cancelScope.equalsIgnoreCase(TblsData.SampleAnalysis.TEST_ID.getName())) && (Boolean.FALSE.equals(LPArray.valueInArray(testsToCancel, testId)))) {
                        testsToCancel = LPArray.addValueToArray1D(testsToCancel, testId);
                        testsSampleToCancel = LPArray.addValueToArray1D(testsSampleToCancel, sampleId);
                    }
                }
            }
        }
        if (testsToCancel.length==0 && cancelScope.equalsIgnoreCase(TblsData.SampleAnalysis.TEST_ID.getName()))
            testsToCancel = LPArray.addValueToArray1D(testsToCancel, cancelScopeId);        
        for (Integer iTstToCancel = 0; iTstToCancel < testsToCancel.length; iTstToCancel++) {
            Integer currTest = Integer.valueOf(LPNulls.replaceNull(testsToCancel[iTstToCancel]).toString());
            objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                    new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{currTest}, new String[]{TblsData.SampleAnalysis.STATUS.getName()});
            String currStatus = (String) objectInfo[0][0];
            if ((Boolean.FALSE.equals((sampleAnalysisStatusCanceled.equalsIgnoreCase(currStatus)))) && (Boolean.FALSE.equals((sampleAnalysisStatusReviewed.equalsIgnoreCase(currStatus)))) && (currTest != null)) {
                    String[] updFldNames=new String[]{TblsData.SampleAnalysis.STATUS.getName(), TblsData.SampleAnalysis.STATUS_PREVIOUS.getName()}; 
                    Object[] updFldValues=new Object[]{sampleAnalysisStatusCanceled, currStatus};
                    SqlWhere sqlWhere = new SqlWhere();
                    sqlWhere.addConstraint(TblsData.SampleAnalysis.TEST_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{currTest}, "");
                    diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS,
                        EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS, updFldNames), updFldValues, sqlWhere, null);        
                    
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_CANCELED, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                        currTest, sampleId, currTest, null, updFldNames, updFldValues);
                }
            } else 
                diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.FORRESULTCANCELATION_STATUS_NOT_EXPECTED, new Object[]{LPNulls.replaceNull(currTest), currStatus, schemaDataName});            
        }
        if (samplesToCancel.length==0 && cancelScope.equalsIgnoreCase(TblsData.Sample.SAMPLE_ID.getName()))
            samplesToCancel = LPArray.addValueToArray1D(samplesToCancel, cancelScopeId);
        for (Integer iSmpToCancel = 0; iSmpToCancel < samplesToCancel.length; iSmpToCancel++) {
            Integer currSample = Integer.valueOf(LPNulls.replaceNull(samplesToCancel[iSmpToCancel]).toString());
            objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), 
                    new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{currSample}, 
                    new String[]{TblsData.Sample.STATUS.getName()});
            String currStatus = (String) objectInfo[0][0];
            if ((Boolean.FALSE.equals((sampleStatusCanceled.equalsIgnoreCase(currStatus)))) && (Boolean.FALSE.equals((sampleStatusReviewed.equalsIgnoreCase(currStatus)))) && (currSample != null)) {
                String[] updFldNames=new String[]{TblsData.Sample.STATUS.getName(), TblsData.Sample.STATUS_PREVIOUS.getName()};
                Object[] updFldValues=new Object[]{sampleStatusCanceled, currStatus};
                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsData.Sample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{currSample}, "");
                diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE,
                        EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE, updFldNames), updFldValues, sqlWhere, null);        
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.SAMPLE_CANCELED, TblsData.TablesData.SAMPLE.getTableName(), 
                            currSample, currSample, null, null, updFldNames, updFldValues);
                }
            }else 
                diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.FORRESULTCANCELATION_STATUS_NOT_EXPECTED, new Object[]{LPNulls.replaceNull(currSample), currStatus, schemaDataName});
        }
        return diagnoses;
    }
    public Object[] sampleAnalysisResultReviewBySampleAndAnalysis(Integer sampleId, String analysisName) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] analysisNameArr=analysisName.split("\\|");
        Object[] diagn=new Object[]{};
        for (String analysisNameArr1 : analysisNameArr) {
            Object[][] testInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.ANALYSIS.getName()}, new Object[]{sampleId, analysisNameArr1}, new String[]{TblsData.SampleAnalysis.TEST_ID.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testInfo[0][0].toString())) return LPArray.array2dTo1d(testInfo);
            if (testInfo.length>1) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.ANALYSIS_HAS_SOME_PARAMETERS, null);
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
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String sampleAnalysisResultStatusReviewed = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REVIEWED.getStatusCode("");        
        String[] fieldsToRetrieve = new String[]{TblsData.SampleAnalysisResult.STATUS.getName(), 
            TblsData.SampleAnalysisResult.RESULT_ID.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName(), 
            TblsData.SampleAnalysisResult.SAMPLE_ID.getName(),
            TblsData.SampleAnalysisResult.ENTERED_BY.getName()};        
        Object[] scopeInfo=getScope(sampleId, testId, resultId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scopeInfo[0].toString())) return scopeInfo;
        String reviewScope=scopeInfo[0].toString();
        Integer reviewScopeId=Integer.valueOf(LPNulls.replaceNull(scopeInfo[1]).toString());
        String reviewScopeTable=scopeInfo[2].toString();        
        if (sampleId != null) {
            Object[] sampleReviewable=checkIfSampleIsReadyForRevision(sampleId);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleReviewable[0].toString())) return sampleReviewable;
            reviewScopeTable = TblsData.TablesData.SAMPLE.getTableName();            
            DataModuleSampleAnalysis smpAna = new DataModuleSampleAnalysis();
            DataSample smp=new DataSample(smpAna);
            return smp.sampleReview(sampleId);
        }
        if (testId != null) {
            reviewScopeTable = TblsData.TablesData.SAMPLE_ANALYSIS.getTableName();
            Object[] readyForRevision = isReadyForRevision(testId);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(readyForRevision[0].toString())) return readyForRevision;
        }
        reviewScopeTable = TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName();
        Object[][] objectInfoForRevisionCheck = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), reviewScopeTable, 
                new String[]{reviewScope}, new Object[]{reviewScopeId}, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectInfoForRevisionCheck[0][0].toString()) || objectInfoForRevisionCheck.length == 0)             
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.NOT_FOUND, new Object[]{LPNulls.replaceNull(resultId).toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
        if (reviewScope.equalsIgnoreCase(TblsData.SampleAnalysisResult.RESULT_ID.getName())
            && (sampleAnalysisResultStatusReviewed.equalsIgnoreCase(objectInfoForRevisionCheck[0][0].toString())) ){
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_ALREADY_REVIEWED, new Object[]{reviewScope, reviewScopeId, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
        }
        Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                new String[]{reviewScope}, new Object[]{reviewScopeId}, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectInfo[0][0].toString()) || objectInfo.length == 0)             
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisResultErrorTrapping.NOT_FOUND, new Object[]{LPNulls.replaceNull(resultId).toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});            
        Object[] reviewSampleAnalysisRulesAllowed = reviewSampleAnalysisRulesAllowed(testId, fieldsToRetrieve, objectInfo);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reviewSampleAnalysisRulesAllowed[0].toString())) return reviewSampleAnalysisRulesAllowed;
        Object[] testsToReview = reviewSamplesAnalysisResultToReview(objectInfo, reviewer);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testsToReview[0].toString()))
                return testsToReview;
        Object[] sampleToReview = reviewSamplesAnalysisFromSampleToReview(sampleId, new Object[]{testId});
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(sampleToReview[0].toString())))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "testNotReviewed", new Object[]{testId});
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "testReviewed", new Object[]{testId});
    }    
    public static Object[] checkIfSampleIsReadyForRevision(Integer sampleId) {    
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] sampleReadyForRevisionFldExists=Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                 TblsData.Sample.READY_FOR_REVISION.getName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(sampleReadyForRevisionFldExists[0].toString())){
            Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.READY_FOR_REVISION.getName()});
            if (Boolean.FALSE.equals(Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString()))) 
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "sampleNotSetAsReadyForRevision", new Object[]{sampleId});
        }            
        Object[] allsampleAnalysisReviewed = DataSampleAnalysis.isAllsampleAnalysisReviewed(sampleId, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allsampleAnalysisReviewed[0].toString())) return allsampleAnalysisReviewed;
        Object[] allsampleTestGroupReviewed = DataSampleRevisionTestingGroup.isAllsampleTestingGroupReviewed(sampleId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allsampleTestGroupReviewed[0].toString())) return allsampleTestGroupReviewed;
        
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "OK", new Object[]{sampleId});
    }
    private Object[] getScope(Integer sampleId, Integer testId, Integer resultId){
        if (sampleId != null) return new Object[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(), sampleId, TblsData.TablesData.SAMPLE.getTableName()};
        if (testId != null) return new Object[]{TblsData.SampleAnalysisResult.TEST_ID.getName(), testId, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName()};   
        if (resultId != null) return new Object[]{TblsData.SampleAnalysisResult.RESULT_ID.getName(), resultId, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()};
        return new Object[]{LPPlatform.LAB_FALSE, "notRecognizedLevel <*1*>", null};
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
            Object[][] testInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                    new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId}, 
                    new String[]{TblsData.SampleAnalysis.STATUS.getName()});
            if (testInfo.length == 0) {            
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSIS_NOTFOUND, new Object[]{testId.toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
            } else {
                String currStatus=testInfo[0][0].toString();                
                if (Boolean.FALSE.equals((sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currStatus)))) {
                    String[] updFieldName=new String[]{TblsData.SampleAnalysis.STATUS.getName(), TblsData.SampleAnalysis.STATUS_PREVIOUS.getName(),
                        TblsData.SampleAnalysis.REVIEWED_ON.getName(), TblsData.SampleAnalysis.REVIEWED_BY.getName()}; 
                    
                    Object[] updFieldValue=new Object[]{sampleAnalysisResultStatusReviewed, currStatus, LPDate.getCurrentTimeStamp()};
                    if (reviewer==null)
                        updFieldValue=LPArray.addValueToArray1D(updFieldValue, token.getPersonName());
                    else
                        updFieldValue=LPArray.addValueToArray1D(updFieldValue, reviewer);
                    Object[] fieldExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), TblsData.Sample.READY_FOR_REVISION.getName());
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldExists[0].toString())){
                        updFieldName=LPArray.addValueToArray1D(updFieldName, TblsData.Sample.READY_FOR_REVISION.getName());
                        updFieldValue=LPArray.addValueToArray1D(updFieldValue, false);
                    }
                    SqlWhere sqlWhere = new SqlWhere();
                    sqlWhere.addConstraint(TblsData.SampleAnalysis.TEST_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{testId}, "");
                    sqlWhere.addConstraint(TblsData.SampleAnalysis.STATUS, SqlStatement.WHERECLAUSE_TYPES.NOT_IN, new Object[]{sampleAnalysisResultStatusCanceled+"-"+sampleAnalysisResultStatusReviewed}, "-");
                    Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS,
                        EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS, updFieldName), updFieldValue, sqlWhere, null);        
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        sampleAnalysisFinallyReviewed=new Object[]{sampleId};
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_REVIEWED, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                            testId, sampleId, testId, null, updFieldName, updFieldValue);
                        sampleAnalysisEvaluateStatusAutomatismForReview(sampleId, testId);
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
        String sampleAnalysisResultStatusCanceled = DataSampleStructureStatuses.SampleAnalysisResultStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisResultStatusReviewed = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REVIEWED.getStatusCode("");
        Object[] sampleAnalysisResultFinallyReviewed=null;
        Integer sampleId = null;
        for (Integer iResToCancel = 0; iResToCancel < objectInfo.length; iResToCancel++) {
            String currStatus = (String) objectInfo[iResToCancel][0];
            if (Boolean.FALSE.equals((sampleAnalysisResultStatusCanceled.equalsIgnoreCase(currStatus)))) {
                String rsltIdStr=objectInfo[iResToCancel][1].toString();
                Integer resultId = Integer.valueOf(rsltIdStr);                
                Integer testId = Integer.valueOf(objectInfo[iResToCancel][2].toString());
                sampleId = Integer.valueOf(objectInfo[iResToCancel][3].toString());
                if (Boolean.FALSE.equals((sampleAnalysisResultStatusReviewed.equalsIgnoreCase(currStatus)))) {
                    String[] updFldName=new String[]{TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.STATUS_PREVIOUS.getName(), 
                        TblsData.SampleAnalysisResult.REVIEWED_ON.getName(), TblsData.SampleAnalysisResult.REVIEWED_BY.getName()}; 
                    Object[] updFldValue=new Object[]{sampleAnalysisResultStatusReviewed, currStatus, LPDate.getCurrentTimeStamp()};
                    if (reviewer==null)
                        updFldValue=LPArray.addValueToArray1D(updFldValue, instanceForActions.getToken().getPersonName());
                    else
                        updFldValue=LPArray.addValueToArray1D(updFldValue, reviewer);
                    SqlWhere sqlWhere = new SqlWhere();
                    sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
                    sqlWhere.addConstraint(TblsData.SampleAnalysisResult.STATUS, SqlStatement.WHERECLAUSE_TYPES.NOT_IN, new Object[]{sampleAnalysisResultStatusCanceled+"-"+sampleAnalysisResultStatusReviewed}, "-");
                    Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                        EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, updFldName), updFldValue, sqlWhere, null);        
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        sampleAnalysisResultFinallyReviewed=LPArray.addValueToArray1D(sampleAnalysisResultFinallyReviewed, resultId);
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_REVIEWED, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                            resultId, sampleId, testId, resultId, updFldName, updFldValue);
                    }
                } 
            }
        }
        return new Object[]{LPPlatform.LAB_TRUE, sampleAnalysisResultFinallyReviewed, sampleId};        
    }
}