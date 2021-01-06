/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.LPPlatform;
import databases.DataDataIntegrity;
import databases.TblsCnfg;
import databases.Rdbms;
import databases.TblsData;
import databases.Token;
import functionaljavaa.analysis.UserMethod;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.parameter.Parameter;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 *
 * @author Administrator
 */
public class DataSampleAnalysis{// implements DataSampleAnalysisStrategy{
/*
  @Override
  public Object[] autoSampleAnalysisAdd(String schemaPrefix, Integer sampleId, Token token, String[] sampleFieldName, Object[] sampleFieldValue, String eventName, Integer transactionId) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
*/

    /**
     *
     */

    
    public enum DataSampleAnalyisAutoAddLevel{    
        DISABLE("DISABLE"), SPEC("SPEC"), SPEC_VARIATION("SPEC_VARIATION")
        ; 
        private final String name;
        DataSampleAnalyisAutoAddLevel(String name) {this.name = name;}
        public String getName() {return name;}
    }
    
    /**
     *
     */
    public static final String CONFIG_SAMPLEANALYSIS_STATUSCANCELED = "sampleAnalysis_statusCanceled";

    /**
     *
     */
    public static final String CONFIG_SAMPLEANALYSIS_STATUSREVIEWED = "sampleAnalysis_statusReviewed";

    private static final String ERROR_TRAPPING_DATA_SAMPLE_ANALYSIS_ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS= "DataSample_sampleAnalaysisAddToSample_MissingMandatoryFields";

    /**
     *
     */
    /**
     *  Automate the sample analysis assignment as to be triggered by any sample action.<br>
     *      Assigned to the actions: LOGSAMPLE.
     * @param schemaPrefix
     * @param sampleId
     * @param userName
     * @param sampleFieldName
     * @param userRole
     * @param sampleFieldValue
     * @param eventName
     * @param appSessionId
     * @param transactionId
     */

    /**
     *
     * @param schemaPrefix
     * @param token
     * @param testId
     * @return diagnoses
     */
    public static Object[] sampleAnalysisReview(String schemaPrefix, Token token, Integer sampleId, Integer testId) {
        String schemaDataName = LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA);
        String sampleAnalysisStatusCanceled = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), CONFIG_SAMPLEANALYSIS_STATUSCANCELED);
        String sampleAnalysisStatusReviewed = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), CONFIG_SAMPLEANALYSIS_STATUSREVIEWED);
        Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{testId}, 
                new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName(), TblsData.SampleAnalysis.FLD_TEST_ID.getName(), TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName()});
        String currStatus = (String) objectInfo[0][0];
        sampleId = (Integer) objectInfo[0][3];
        if ((!(sampleAnalysisStatusCanceled.equalsIgnoreCase(currStatus))) && (!(sampleAnalysisStatusReviewed.equalsIgnoreCase(currStatus))) && (testId != null)) {
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                    new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName()}, 
                    new Object[]{sampleAnalysisStatusReviewed, currStatus}, new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{testId});
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                String[] fieldsForAudit = new String[0];
                fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysis.FLD_STATUS.getName() + ":" + sampleAnalysisStatusCanceled);
                fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysis.FLD_STATUS_PREVIOUS.getName() + ":" + currStatus);
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(schemaPrefix, SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_REVIEWED.toString(), TblsData.SampleAnalysis.TBL.getName(), testId, sampleId, testId, null, fieldsForAudit, token, null);
                sampleAnalysisEvaluateStatus_automatismForReview(schemaPrefix, token, sampleId, testId);
            }
            return diagnoses;
        } else {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResultNotReviewable", new Object[]{LPNulls.replaceNull(testId), schemaDataName, currStatus});
        }
    }
    public static Object[] sampleAnalysisEvaluateStatus_automatismForReview(String schemaPrefix, Token token, Integer sampleId, Integer testId){
        String[] whereFieldName=new String[]{};
        Object[] whereFieldValue=new Object[]{};
        Object[][] testInfo=new Object[][]{{}};
        Object[] isRevisionTestinGroupRequired = LPPlatform.isProcedureBusinessRuleEnable(schemaPrefix, "procedure", "revisionTestinGroupRequired");
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionTestinGroupRequired[0].toString())){
            testInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsData.SampleAnalysis.TBL.getName(),
                new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{testId}, new String[]{TblsData.SampleAnalysis.FLD_TESTING_GROUP.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testInfo[0][0].toString())) return LPArray.array2dTo1d(testInfo);
            whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsData.SampleAnalysis.FLD_TESTING_GROUP.getName());
            whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, testInfo[0][0].toString());
        }
        
        Object[] areAllsampleAnalysisReviewed = isAllsampleAnalysisReviewed(schemaPrefix, token, sampleId, whereFieldName, whereFieldValue);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areAllsampleAnalysisReviewed[0].toString())) return areAllsampleAnalysisReviewed;            
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionTestinGroupRequired[0].toString()))
            return DataSampleRevisionTestingGroup.setReadyForRevision(schemaPrefix, token, sampleId, testInfo[0][0].toString());
        else
            return DataSample.setReadyForRevision(schemaPrefix, token, sampleId);
    }
    
    /**
     *
     * @param schemaPrefix
     * @param token
     * @param sampleId
     * @param testId
     * @return
     */
    public static Object[] setReadyForRevision( String schemaPrefix, Token token, Integer sampleId, Integer testId, String parentAuditAction, Integer parentAuditId){
        String auditActionName = SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_SET_READY_fOR_REVISION.toString();
        if (parentAuditAction != null) {
            auditActionName = parentAuditAction + ":" + auditActionName;
        }
        String[] sampleAnalysisFieldName=new String[]{TblsData.SampleAnalysis.FLD_READY_FOR_REVISION.getName()};
        Object[] sampleAnalysisFieldValue=new Object[]{true};
        Object[][] sampleAnalysisInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsData.SampleAnalysis.TBL.getName(),  
                new String[] {TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{testId}, sampleAnalysisFieldName);
        if ("TRUE".equalsIgnoreCase(sampleAnalysisInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "alreadyReadyForRevision", new Object[]{testId, sampleId, schemaPrefix});
        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsData.SampleAnalysis.TBL.getName(), sampleAnalysisFieldName, sampleAnalysisFieldValue, 
                                                new String[] {TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{testId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
            String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(sampleAnalysisFieldName, sampleAnalysisFieldValue, token.getPersonName());

            SampleAudit smpAudit = new SampleAudit();       
            smpAudit.sampleAuditAdd(schemaPrefix, auditActionName, TblsData.SampleAnalysis.TBL.getName(), testId, sampleId, null, null, fieldsForAudit, token, parentAuditId);
        }    
        return diagnoses;
    }

    public static Object[] isReadyForRevision( String schemaPrefix, Token token, Integer testId){
        Object[][] sampleAnalysisInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsData.SampleAnalysis.TBL.getName(),  
                new String[] {TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{testId}, 
                new String[] {TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAnalysisInfo[0][0].toString()))
            return LPArray.array2dTo1d(sampleAnalysisInfo);
        return isReadyForRevision(schemaPrefix, token, (Integer) sampleAnalysisInfo[0][0], testId);
    }
    /**
     *
     * @param schemaPrefix
     * @param token
     * @param sampleId
     * @param testId
     * @return
     */
    public static Object[] isReadyForRevision( String schemaPrefix, Token token, Integer sampleId, Integer testId){
        String[] sampleAnalysisFieldName=new String[]{TblsData.SampleAnalysis.FLD_READY_FOR_REVISION.getName()};
        Object[] sampleAnalysisFieldValue=new Object[]{true};
        Object[][] sampleAnalysisInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsData.SampleAnalysis.TBL.getName(),  
                new String[] {TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{testId}, sampleAnalysisFieldName);
        if ("TRUE".equalsIgnoreCase(sampleAnalysisInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "readyForRevision", new Object[]{testId, sampleId, schemaPrefix});
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "NOT IMPLEMENTED YET WHEN SET READY FOR REVISION NOT TRUE YET", new Object[]{testId, sampleId, schemaPrefix});
        //return diagnoses;
    }
        
    

    /**
     *
     * @param schemaPrefix
     * @param token
     * @param sampleId
     * @param testId
     * @param parentAuditId
     * @param parentAuditAction
     * @return
     */
    public static Object[] sampleAnalysisEvaluateStatus(String schemaPrefix, Token token, Integer sampleId, Integer testId, String parentAuditAction, Integer parentAuditId) {
        String schemaDataName = LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA);
        String auditActionName = SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_EVALUATE_STATUS.toString();
        if (parentAuditAction != null) {
            auditActionName = parentAuditAction + ":" + auditActionName;
        }
        String sampleAnalysisStatusIncomplete = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), "sampleAnalysis_statusIncomplete");
        String sampleAnalysisStatusComplete = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), "sampleAnalysis_statusComplete");
        String smpAnaNewStatus = "";
        Object[] diagnoses = Rdbms.existsRecord(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), 
                new String[]{TblsData.SampleAnalysisResult.FLD_TEST_ID.getName(), TblsData.SampleAnalysisResult.FLD_STATUS.getName(), TblsData.SampleAnalysisResult.FLD_MANDATORY.getName()}, 
                new Object[]{testId, "BLANK", true});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
            smpAnaNewStatus = sampleAnalysisStatusIncomplete;
        } else {
            smpAnaNewStatus = sampleAnalysisStatusComplete;
        }
        diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName()}, 
                new Object[]{smpAnaNewStatus}, new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{testId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
            if (sampleAnalysisStatusComplete.equalsIgnoreCase(smpAnaNewStatus))                
                sampleAnalysisEvaluateStatus_automatismForComplete(schemaPrefix, token, sampleId, testId, parentAuditAction, parentAuditId);
            String[] fieldsForAudit = new String[0];
            fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysis.FLD_STATUS.getName() + ":" + smpAnaNewStatus);
            SampleAudit smpAudit = new SampleAudit();
            smpAudit.sampleAuditAdd(schemaPrefix, auditActionName, TblsData.SampleAnalysis.TBL.getName(), testId, sampleId, testId, null, fieldsForAudit, token, parentAuditId);
        }        
        DataSample.sampleEvaluateStatus(schemaPrefix, token, sampleId, parentAuditAction, parentAuditId);
        return diagnoses;
    }
    public static Object[] sampleAnalysisEvaluateStatus_automatismForComplete(String schemaPrefix, Token token, Integer sampleId, Integer testId,String parentAuditAction,Integer parentAuditId){
        Object[] isRevisionSampleAnalysisMarkAsReadyForRevisionWhenAllResultsEntered = LPPlatform.isProcedureBusinessRuleEnable(schemaPrefix, "procedure", "revisionSampleAnalysis_markAsReadyForRevisionWhenAllResultsEntered");
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionSampleAnalysisMarkAsReadyForRevisionWhenAllResultsEntered[0].toString()))
            return setReadyForRevision(schemaPrefix, token, sampleId, testId, parentAuditAction, parentAuditId);
        Object[] isRevisionSampleAnalysisMarkAsReviewedWhenAllResultsEntered = LPPlatform.isProcedureBusinessRuleEnable(schemaPrefix, "procedure", "revisionSampleAnalysis_markAsReviewedWhenAllResultsEntered");
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionSampleAnalysisMarkAsReviewedWhenAllResultsEntered[0].toString()))
            return sampleAnalysisReview(schemaPrefix, token, sampleId, testId);
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "revisionSampleAnalysis_markAsReadyForRevisionWhenAllResultsEntered not active", null);
    }
    /**
     *
     * @param schemaPrefix
     * @param token
     * @param testId
     * @param newAnalyst
     * @param dataSample
     * @return
     */
    public static Object[] sampleAnalysisAssignAnalyst(String schemaPrefix, Token token, Integer testId, String newAnalyst, DataSample dataSample) {
        String schemaDataName = LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA);
        String schemaConfigName = LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG);
        Boolean assignTestAnalyst = false;
        String testStatusReviewed = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), CONFIG_SAMPLEANALYSIS_STATUSREVIEWED);
        String testStatusCanceled = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), CONFIG_SAMPLEANALYSIS_STATUSCANCELED);
        String assignmentModes = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), "sampleAnalysis_analystAssigmentModes");
        Object[][] testData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), 
                new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, 
                new Object[]{testId}, new String[]{TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName(), TblsData.SampleAnalysis.FLD_STATUS.getName(), TblsData.SampleAnalysis.FLD_ANALYST.getName(), 
                    TblsData.SampleAnalysis.FLD_ANALYSIS.getName(), TblsData.SampleAnalysis.FLD_METHOD_NAME.getName(), TblsData.SampleAnalysis.FLD_METHOD_VERSION.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testData[0][0].toString())) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisNotFound", new Object[]{testId, schemaPrefix});
        }
        Integer sampleId = (Integer) testData[0][0];
        String testStatus = (String) testData[0][1];
        String testCurrAnalyst = (String) testData[0][2];
        String testAnalysis = (String) testData[0][3];
        String testMethodName = (String) testData[0][4];
        Integer testMethodVersion = (Integer) testData[0][5];
        if (testCurrAnalyst == null ? newAnalyst == null : testCurrAnalyst.equals(newAnalyst)) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisAssignment_SameAnalyst", new Object[]{testCurrAnalyst, testId, schemaPrefix});
        }
        // the test status cannot be reviewed or canceled, should be checked
        if ((testCurrAnalyst != null) && (testStatus.equalsIgnoreCase(testStatusReviewed) || testStatus.equalsIgnoreCase(testStatusCanceled))) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisAssignment_SampleAnalysisLocked", new Object[]{testStatus, testId, newAnalyst, schemaPrefix});
        }
        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), 
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.FLD_CONFIG_CODE.getName(), TblsData.Sample.FLD_CONFIG_CODE_VERSION.getName()});
        String sampleConfigCode = (String) sampleData[0][0];
        Integer sampleConfigCodeVersion = (Integer) sampleData[0][1];
        Object[][] sampleRulesData = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.SampleRules.TBL.getName(), 
                new String[]{TblsCnfg.SampleRules.FLD_CODE.getName(), TblsCnfg.SampleRules.FLD_CODE_VERSION.getName()}, 
                new Object[]{sampleConfigCode, sampleConfigCodeVersion}, 
                new String[]{TblsCnfg.SampleRules.FLD_CODE.getName(), TblsCnfg.SampleRules.FLD_CODE_VERSION.getName(), TblsCnfg.SampleRules.FLD_ANALYST_ASSIGNMENT_MODE.getName()});
        String testAssignmentMode = (String) sampleRulesData[0][2];
        if (testAssignmentMode == null) {
            testAssignmentMode = "null";
        }
        if (!assignmentModes.contains(testAssignmentMode)) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisAssignment_AssignmentModeNotRecognized", 
                    new Object[]{TblsCnfg.SampleRules.FLD_ANALYST_ASSIGNMENT_MODE.getName(), sampleConfigCode, sampleConfigCodeVersion, testAssignmentMode, assignmentModes, schemaPrefix,
                    testId, newAnalyst});
        }
        if (testAssignmentMode.equalsIgnoreCase("DISABLE")) {
            assignTestAnalyst = true;
        } else {
            UserMethod ana = new UserMethod();
            String userMethodCertificationMode = ana.userMethodCertificationLevel(schemaPrefix, testAnalysis, testMethodName, testMethodVersion, newAnalyst);
            String userCertifiedModes = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), "sampleAnalysis_analystAssigmentMode" + testAssignmentMode);
            String[] userMethodModesArr = userCertifiedModes.split("\\|");
            assignTestAnalyst = LPArray.valueInArray(userMethodModesArr, userMethodCertificationMode);
            if (!assignTestAnalyst) {
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisAssignment_AssignmentModeNotImplemented", new Object[]{testAssignmentMode, Arrays.toString(userMethodModesArr), userMethodCertificationMode, schemaDataName});
            }
        }
        if (assignTestAnalyst) {
            String[] updateFieldName = new String[]{TblsData.SampleAnalysis.FLD_ANALYST.getName(), TblsData.SampleAnalysis.FLD_ANALYST_ASSIGNED_ON.getName(), TblsData.SampleAnalysis.FLD_ANALYST_ASSIGNED_BY.getName()};
            Object[] updateFieldValue = new Object[]{newAnalyst, LPDate.getCurrentTimeStamp(), token.getUserName()};
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), updateFieldName, updateFieldValue, 
                    new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{testId});
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "DataSample_SampleAnalysisAssignment_Successfully", new Object[]{testId, newAnalyst, schemaDataName});
                String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(updateFieldName, updateFieldValue, ":");
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(schemaPrefix, SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_ANALYST_ASSIGNMENT.toString(), TblsData.SampleAnalysis.TBL.getName(), testId, sampleId, testId, null, fieldsForAudit, token, null);
                return diagnoses;
            }
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisAssignment_databaseReturnedError", new Object[]{testId, newAnalyst, schemaDataName});
        }
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisAssignment_EscapeByUnhandledException", new Object[]{schemaPrefix, token.getUserName(), testId, newAnalyst, token.getUserRole()});
    }

    /**
     *
     * @param schemaPrefix
     * @param dataSample
     * @return
     */
/*    public String specialFieldCheckSampleAnalysisMethod(String schemaPrefix, DataSample dataSample) {
        String myDiagnoses = "";
        String schemaConfigName = LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG);
        Integer specialFieldIndex = Arrays.asList(dataSample.mandatoryFields).indexOf(TblsData.Sample.FIELDNAME_ANALYSIS);
        String analysis = (String) dataSample.mandatoryFieldsValue[specialFieldIndex];
        if (analysis.length() == 0) {
            myDiagnoses = "ERROR: The parameter analysis cannot be null";
            return myDiagnoses;
        }
        specialFieldIndex = Arrays.asList(dataSample.mandatoryFields).indexOf(FIELDNAME_SAMPLE_ANALYSIS_METHOD_NAME);
        String methodName = (String) dataSample.mandatoryFieldsValue[specialFieldIndex];
        if (methodName.length() == 0) {
            myDiagnoses = "ERROR: The parameter method_name cannot be null";
            return myDiagnoses;
        }
        specialFieldIndex = Arrays.asList(dataSample.mandatoryFields).indexOf(FIELDNAME_SAMPLE_ANALYSIS_METHOD_VERSION);
        Integer methodVersion = (Integer) dataSample.mandatoryFieldsValue[specialFieldIndex];
        if (methodVersion == null) {
            myDiagnoses = "ERROR: The parameter method_version cannot be null";
            return myDiagnoses;
        }
        String[] fieldNames = new String[]{TblsCnfg.AnalysisMethod.FLD_ANALYSIS.getName(), TblsCnfg.AnalysisMethod.FLD_METHOD_NAME.getName(), TblsCnfg.AnalysisMethod.FLD_METHOD_VERSION.getName()};
        Object[] fieldValues = new Object[]{analysis, methodName, methodVersion};
        Object[] diagnosis = Rdbms.existsRecord(schemaConfigName, TblsCnfg.AnalysisMethod.TBL.getName(), fieldNames, fieldValues);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())) {
            myDiagnoses = DataSample.DIAGNOSES_SUCCESS;
        } else {
            diagnosis = Rdbms.existsRecord(schemaConfigName, TblsData.Sample.FIELDNAME_ANALYSIS, new String[]{TblsData.Sample.FIELDNAME_CODE}, new Object[]{analysis});
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())) {
                myDiagnoses = "ERROR: The analysis " + analysis + " exists but the method " + methodName + " with version " + methodVersion + " was not found in the schema " + schemaPrefix;
            } else {
                myDiagnoses = "ERROR: The analysis " + analysis + " is not found in the schema " + schemaPrefix;
            }
        }
        return myDiagnoses;
    }
*/    
    /**
     *
     * @param schemaPrefix
     * @param token
     * @param sampleId
     * @param fieldName
     * @param fieldValue
     * @param parentAuditId
     * @return
     */
    public static Object[] sampleAnalysisAddtoSample(String schemaPrefix, Token token, Integer sampleId, String[] fieldName, Object[] fieldValue, Integer parentAuditId) {
        String[] mandatoryFields = null;
        Object[] mandatoryFieldsValue = null;
        DataDataIntegrity labIntChecker = new DataDataIntegrity(); 
    
        String tableName = TblsData.SampleAnalysis.TBL.getName();
        String actionName = "Insert";
        String schemaDataName = LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA);
        String schemaConfigName = LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG);
        String sampleLevel = TblsData.Sample.TBL.getName();
        mandatoryFields = labIntChecker.getTableMandatoryFields(schemaDataName, sampleLevel + tableName, actionName);
        Object[] fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(fieldName, fieldValue);
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker[0].toString())) {
            return fieldNameValueArrayChecker;
        }
        mandatoryFieldsValue = new Object[mandatoryFields.length];
        StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);
        for (Integer inumLines = 0; inumLines < mandatoryFields.length; inumLines++) {
            String currField = mandatoryFields[inumLines];
            boolean contains = Arrays.asList(fieldName).contains(currField.toLowerCase());
            if (!contains) {                
                if (mandatoryFieldsMissingBuilder.length()>0){mandatoryFieldsMissingBuilder.append(",");}
                
                mandatoryFieldsMissingBuilder.append(currField);
            } else {
                Integer valuePosic = Arrays.asList(fieldName).indexOf(currField);
                mandatoryFieldsValue[inumLines] = fieldValue[valuePosic];
            }
        }
        if (mandatoryFieldsMissingBuilder.length() > 0) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_DATA_SAMPLE_ANALYSIS_ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS, 
                    new String[]{mandatoryFieldsMissingBuilder.toString(), Arrays.toString(fieldName), schemaConfigName});
        }
        // set first status. Begin
        String firstStatus = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), "sampleAnalysis_statusFirst");
        Integer specialFieldIndex = Arrays.asList(fieldName).indexOf(TblsData.Sample.FLD_STATUS.getName());
        if (specialFieldIndex == -1) {
            fieldName = LPArray.addValueToArray1D(fieldName, TblsData.Sample.FLD_STATUS.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, firstStatus);
        } else {
            fieldValue[specialFieldIndex] = firstStatus;
        }
        // set first status. End
        // Spec Business Rule. Allow other analyses. Begin
        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName(), TblsData.Sample.FLD_STATUS.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleData[0][0].toString())) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSample.DataSampleErrorTrapping.SAMPLE_NOT_FOUND.getErrorCode(), new Object[]{sampleId, schemaDataName});
        }
        String sampleSpecCode = "";
        Integer sampleSpecCodeVersion = null;
        String sampleSpecVariationName = "";
        String specAnalysisTestingGroup="";
        Object[][] sampleSpecData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(), new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName(), TblsData.Sample.FLD_SPEC_CODE.getName(), TblsData.Sample.FLD_SPEC_CODE_VERSION.getName(), 
                    TblsData.Sample.FLD_SPEC_VARIATION_NAME.getName(), TblsData.Sample.FLD_STATUS.getName()});
        if ((sampleSpecData[0][0] == null) || (!LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleSpecData[0][0].toString()))) {
            sampleSpecCode = (String) sampleSpecData[0][1];
            sampleSpecCodeVersion = (Integer) sampleSpecData[0][2];
            sampleSpecVariationName = (String) sampleSpecData[0][3];
            if (sampleSpecCode != null) {
                Object[][] specRules = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.SpecRules.TBL.getName(), 
                        new String[]{TblsCnfg.SpecRules.FLD_CODE.getName(), TblsCnfg.SpecRules.FLD_CONFIG_VERSION.getName()}, 
                        new Object[]{sampleSpecCode, sampleSpecCodeVersion}, new String[]{TblsCnfg.SpecRules.FLD_ALLOW_OTHER_ANALYSIS.getName(), TblsCnfg.SpecRules.FLD_ALLOW_MULTI_SPEC.getName(), 
                            TblsCnfg.SpecRules.FLD_CODE.getName(), TblsCnfg.SpecRules.FLD_CONFIG_VERSION.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(specRules[0][0].toString())) {
                    return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SpecRuleNotFound", new Object[]{sampleSpecCode, sampleSpecCodeVersion, schemaDataName});
                }
                if (!Boolean.valueOf(specRules[0][0].toString())) {
                    String[] specAnalysisFieldName = new String[]{TblsCnfg.SpecLimits.FLD_ANALYSIS.getName(), TblsCnfg.SpecLimits.FLD_METHOD_NAME.getName(), TblsCnfg.SpecLimits.FLD_METHOD_VERSION.getName()};
                    Object[] specAnalysisFieldValue = new Object[0];
                    for (String iFieldN : specAnalysisFieldName) {
                        specialFieldIndex = Arrays.asList(fieldName).indexOf(iFieldN);
                        if (specialFieldIndex == -1) {
                            specAnalysisFieldValue = LPArray.addValueToArray1D(specAnalysisFieldValue, "is null");
                        } else {
                            specAnalysisFieldValue = LPArray.addValueToArray1D(specAnalysisFieldValue, fieldValue[specialFieldIndex]);
                        }
                    }
                    specAnalysisFieldName = LPArray.addValueToArray1D(specAnalysisFieldName, TblsCnfg.SpecLimits.FLD_CODE.getName());
                    specAnalysisFieldValue = LPArray.addValueToArray1D(specAnalysisFieldValue, sampleSpecCode);
                    specAnalysisFieldName = LPArray.addValueToArray1D(specAnalysisFieldName, TblsCnfg.SpecLimits.FLD_CONFIG_VERSION.getName());
                    specAnalysisFieldValue = LPArray.addValueToArray1D(specAnalysisFieldValue, sampleSpecCodeVersion);
                    specAnalysisFieldName = LPArray.addValueToArray1D(specAnalysisFieldName, TblsCnfg.SpecLimits.FLD_VARIATION_NAME.getName());
                    specAnalysisFieldValue = LPArray.addValueToArray1D(specAnalysisFieldValue, sampleSpecVariationName);
                    //Object[] analysisInSpec = Rdbms.existsRecord(schemaConfigName, TblsCnfg.SpecLimits.TBL.getName(), specAnalysisFieldName, specAnalysisFieldValue);
                    Object[][] analysisInSpec = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.SpecLimits.TBL.getName(), 
                        specAnalysisFieldName, specAnalysisFieldValue, 
                        new String[]{TblsCnfg.SpecLimits.FLD_TESTING_GROUP.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisInSpec[0][0].toString())) {
                        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SpecLimitNotFound", new Object[]{Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(specAnalysisFieldName, specAnalysisFieldValue, ":")), schemaDataName});
                    }
                    specAnalysisTestingGroup=analysisInSpec[0][0].toString();
                }
            }
        }
        // Spec Business Rule. Allow other analyses. End
        String[] specialFields = labIntChecker.getStructureSpecialFields(schemaDataName, sampleLevel + "Structure");
        String[] specialFieldsFunction = labIntChecker.getStructureSpecialFieldsFunction(schemaDataName, sampleLevel + "Structure");
        for (Integer inumLines = 0; inumLines < fieldName.length; inumLines++) {
            String currField = tableName + "." + fieldName[inumLines];
            boolean contains = Arrays.asList(specialFields).contains(currField);
            if (contains) {
                specialFieldIndex = Arrays.asList(specialFields).indexOf(currField);
                Method method = null;
/*                try {
                    String aMethod = specialFieldsFunction[specialFieldIndex];
                    Class<?>[] paramTypes = {Rdbms.class, String.class};
                    method = getClass().getDeclaredMethod(aMethod, paramTypes);
                } catch (NoSuchMethodException | SecurityException ex) {
                    errorCode = "DataSample_SpecialFunctionReturnedERROR";
                    errorDetailVariablesLocal = LPArray.addValueToArray1D(errorDetailVariablesLocal, currField);
                    errorDetailVariablesLocal = LPArray.addValueToArray1D(errorDetailVariablesLocal, aMethod);
                    errorDetailVariablesLocal = LPArray.addValueToArray1D(errorDetailVariablesLocal, ex.getMessage());
                    return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, errorCode, errorDetailVariablesLocal);
                }
                Object specialFunctionReturn = null;
                try {
                    if (method != null) {
                        specialFunctionReturn = method.invoke(this, schemaPrefix);
                    }
                } catch (IllegalAccessException | NullPointerException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(DataSample.class.getName()).log(Level.SEVERE, null, ex);
                }
                if ((specialFunctionReturn == null) || (specialFunctionReturn != null && specialFunctionReturn.toString().contains("ERROR"))) {
                    errorCode = "DataSample_SpecialFunctionReturnedERROR";
                    errorDetailVariablesLocal = LPArray.addValueToArray1D(errorDetailVariablesLocal, currField);
                    errorDetailVariablesLocal = LPArray.addValueToArray1D(errorDetailVariablesLocal, aMethod);
                    errorDetailVariablesLocal = LPArray.addValueToArray1D(errorDetailVariablesLocal, LPNulls.replaceNull(specialFunctionReturn));
                    return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, errorCode, errorDetailVariablesLocal);
                }*/
            }
        }
        Object value = null;
        Object[] whereResultFieldValue = new Object[0];
        String[] whereResultFieldName = new String[0];
        String fieldNeed = TblsCnfg.AnalysisMethodParams.FLD_ANALYSIS.getName();
        whereResultFieldName = LPArray.addValueToArray1D(whereResultFieldName, fieldNeed);
        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(fieldNeed);
        if (specialFieldIndex == -1) {
            specialFieldIndex = Arrays.asList(fieldName).indexOf(fieldNeed);
            if (specialFieldIndex == -1) {
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_DATA_SAMPLE_ANALYSIS_ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS, 
                    new String[]{fieldNeed, Arrays.toString(mandatoryFields), schemaDataName});
            }
            value = fieldValue[specialFieldIndex];
        } else {
            value = mandatoryFieldsValue[specialFieldIndex];
        }
        whereResultFieldValue = LPArray.addValueToArray1D(whereResultFieldValue, value);
        fieldNeed = TblsCnfg.AnalysisMethodParams.FLD_METHOD_NAME.getName();
        whereResultFieldName = LPArray.addValueToArray1D(whereResultFieldName, fieldNeed);
        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(fieldNeed);
        if (specialFieldIndex == -1) {
            specialFieldIndex = Arrays.asList(fieldName).indexOf(fieldNeed);
            if (specialFieldIndex == -1) {
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_DATA_SAMPLE_ANALYSIS_ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS, 
                    new String[]{fieldNeed, Arrays.toString(mandatoryFields), schemaDataName});
            }
            value = fieldValue[specialFieldIndex];
        } else {
            value = mandatoryFieldsValue[specialFieldIndex];
        }
        whereResultFieldValue = LPArray.addValueToArray1D(whereResultFieldValue, value);
        fieldNeed = TblsCnfg.AnalysisMethodParams.FLD_METHOD_VERSION.getName();
        whereResultFieldName = LPArray.addValueToArray1D(whereResultFieldName, fieldNeed);
        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(fieldNeed);
        if (specialFieldIndex == -1) {
            specialFieldIndex = Arrays.asList(fieldName).indexOf(fieldNeed);
            if (specialFieldIndex == -1) {
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_DATA_SAMPLE_ANALYSIS_ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS, 
                    new String[]{fieldNeed, Arrays.toString(mandatoryFields), schemaDataName});
            }
            value = fieldValue[specialFieldIndex];
        } else {
            value = mandatoryFieldsValue[specialFieldIndex];
        }
        whereResultFieldValue = LPArray.addValueToArray1D(whereResultFieldValue, value);
        String[] getResultFields = new String[]{TblsCnfg.AnalysisMethodParams.FLD_PARAM_NAME.getName(), TblsCnfg.AnalysisMethodParams.FLD_MANDATORY.getName(), TblsCnfg.AnalysisMethodParams.FLD_ANALYSIS.getName(),
            TblsCnfg.AnalysisMethodParams.FLD_PARAM_TYPE.getName(), TblsCnfg.AnalysisMethodParams.FLD_NUM_REPLICAS.getName(), TblsCnfg.AnalysisMethodParams.FLD_UOM.getName(), TblsCnfg.AnalysisMethodParams.FLD_UOM_CONVERSION_MODE.getName()};
        Object[][] resultFieldRecords = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.AnalysisMethodParams.TBL.getName(), 
                whereResultFieldName, whereResultFieldValue, getResultFields);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultFieldRecords[0][0].toString())) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_AnalysisMethodParamsNotFound", new Object[]{Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(whereResultFieldName, whereResultFieldValue, ":")), schemaDataName});
        }
        resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, sampleId);
        getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName());
        resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, 0);
        getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.FLD_TEST_ID.getName());
        // This is temporary !!!! ***************************************************************
        specialFieldIndex = Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.FLD_STATUS.getName());
        if (specialFieldIndex == -1) {
            firstStatus = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), "sampleAnalysisResult_statusFirst");
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, firstStatus);
            getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.FLD_STATUS.getName());
        }
        // This is temporary !!!! ***************************************************************
        String[] resultMandatoryFields = labIntChecker.getTableMandatoryFields(schemaDataName, sampleLevel, actionName);
        String[] resultDefaulFields = labIntChecker.getTableFieldsDefaulValues(schemaDataName, tableName, actionName);
        Object[] resultDefaulFieldValue = labIntChecker.getTableFieldsDefaulValues(schemaDataName, tableName, actionName);
        Object[] resultMandatoryFieldsValue = new Object[resultMandatoryFields.length];
        StringBuilder resultMandatoryFieldsMissingBuilder = new StringBuilder(0);
        for (Integer inumLines = 0; inumLines < resultMandatoryFieldsValue.length; inumLines++) {
            String currField = resultMandatoryFields[inumLines];
            boolean contains = Arrays.asList(getResultFields).contains(currField.toLowerCase());
            if (!contains) {
                Integer valuePosic = Arrays.asList(resultDefaulFields).indexOf(currField.toLowerCase());
                if (valuePosic == -1) {
                    if (resultMandatoryFieldsMissingBuilder.length()>0){resultMandatoryFieldsMissingBuilder.append(",");}
                
                    resultMandatoryFieldsMissingBuilder.append(currField);                        
                } else {
                    Object currFieldValue = resultDefaulFieldValue[valuePosic];
                    resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, currFieldValue);
                    getResultFields = LPArray.addValueToArray1D(getResultFields, currField);
                }
            }
        }
        if (resultMandatoryFieldsMissingBuilder.length() > 0) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSample.DataSampleErrorTrapping.MISSING_MANDATORY_FIELDS.getErrorCode(), new Object[]{resultMandatoryFieldsMissingBuilder, schemaDataName});
        }
        fieldName = LPArray.addValueToArray1D(fieldName, new String[]{TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName(), TblsData.SampleAnalysis.FLD_ADDED_ON.getName(), TblsData.SampleAnalysis.FLD_ADDED_BY.getName()});
        fieldValue = LPArray.addValueToArray1D(fieldValue, new Object[]{sampleId, Rdbms.getCurrentDate(), token.getUserName()});
        if (!LPArray.valueInArray(fieldName, TblsData.SampleAnalysis.FLD_TESTING_GROUP.getName())){
            fieldName = LPArray.addValueToArray1D(fieldName, TblsData.SampleAnalysis.FLD_TESTING_GROUP.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, specAnalysisTestingGroup);
        }
        String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(fieldName, fieldValue, ":");
        Object[] diagnoses = Rdbms.insertRecordInTable(schemaDataName, TblsData.SampleAnalysis.TBL.getName(), fieldName, fieldValue);
        Integer testId = Integer.parseInt(diagnoses[diagnoses.length - 1].toString());
        SampleAudit smpAudit = new SampleAudit();
        smpAudit.sampleAuditAdd(schemaPrefix, SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_ADDED.toString(), TblsData.SampleAnalysis.TBL.getName(), testId, sampleId, testId, null, fieldsForAudit, token, parentAuditId);
        Integer valuePosic = Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.FLD_TEST_ID.getName());
        if (valuePosic == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, testId);
            getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.FLD_TEST_ID.getName());
        }else
            resultFieldRecords = LPArray.setColumnValueToArray2D(resultFieldRecords, valuePosic, testId);        
        valuePosic = Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.FLD_METHOD_NAME.getName());
        if (valuePosic == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.FLD_METHOD_NAME.getName())]);
            getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.FLD_METHOD_NAME.getName());
        }
        valuePosic = Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.FLD_METHOD_VERSION.getName());
        if (valuePosic == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.FLD_METHOD_VERSION.getName())]);
            getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.FLD_METHOD_VERSION.getName());
        }
        for (Object[] resultFieldRecord : resultFieldRecords) {
            Object[] fieldVal = new Object[0];
            for (int col = 0; col < resultFieldRecords[0].length; col++) {
                fieldVal = LPArray.addValueToArray1D(fieldVal, resultFieldRecord[col]);
            }
            valuePosic = Arrays.asList(getResultFields).indexOf(TblsCnfg.AnalysisMethodParams.FLD_NUM_REPLICAS.getName());
            Integer numReplicas = 1;
            String resultReplicaFieldName = TblsData.SampleAnalysisResult.FLD_REPLICA.getName();
            if (valuePosic == -1) {
                valuePosic = Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.FLD_REPLICA.getName());
                if (valuePosic == -1) {
                    getResultFields = LPArray.addValueToArray1D(getResultFields, resultReplicaFieldName);
                    fieldVal = LPArray.addValueToArray1D(fieldVal, numReplicas);
                    valuePosic = fieldVal.length - 1;
                }
            } else {
                numReplicas = (Integer) fieldVal[valuePosic];
                getResultFields[valuePosic] = resultReplicaFieldName;
                if ((numReplicas == null) || (numReplicas == 0)) {
                    numReplicas = 1;
                    fieldVal[valuePosic] = 1;
                }
            }
            if (sampleSpecCode.length()>0){
            Object[][] specLimits = ConfigSpecRule.getSpecLimitLimitIdFromSpecVariables(schemaPrefix, sampleSpecCode, sampleSpecCodeVersion, 
                    sampleSpecVariationName, 
                    fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.FLD_ANALYSIS.getName())].toString(), 
                    fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.FLD_METHOD_NAME.getName())].toString(), 
                    (Integer) fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.FLD_METHOD_VERSION.getName())], 
                    fieldVal[Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.FLD_PARAM_NAME.getName())].toString(), 
                    new String[]{TblsCnfg.SpecLimits.FLD_LIMIT_ID.getName()});
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimits[0][0].toString())){
                      getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.FLD_LIMIT_ID.getName());     
                      fieldVal = LPArray.addValueToArray1D(fieldVal, specLimits[0][0]);                           
                    }
            }
            for (Integer iNumReps = 1; iNumReps <= numReplicas; iNumReps++) {
                fieldVal[valuePosic] = iNumReps;
                diagnoses = Rdbms.insertRecordInTable(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(), getResultFields, fieldVal);
                fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(getResultFields, fieldVal, ":");
                Integer resultId = Integer.parseInt(diagnoses[diagnoses.length - 1].toString());
                smpAudit.sampleAuditAdd(schemaPrefix, SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_ADDED.toString(), sampleLevel + TblsData.SampleAnalysisResult.TBL.getName(), resultId, sampleId, testId, resultId, fieldsForAudit, token, parentAuditId);
            }
        }
        Object[] diagnoses3 = DataSample.sampleEvaluateStatus(schemaPrefix, token, sampleId, SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_ADDED.toString(), parentAuditId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses3[0].toString())) {
            return diagnoses3;
        }
        Object[] addSampleRevisionByTestingGroup = DataSampleRevisionTestingGroup.addSampleRevisionByTestingGroup(schemaPrefix, token, sampleId, testId, specAnalysisTestingGroup);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(addSampleRevisionByTestingGroup[0].toString())) return addSampleRevisionByTestingGroup;
        
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "DataSample_SampleAnalysisAddedSuccessfully", new Object[]{"", testId, schemaDataName});
    }
    public static Object[] isAllsampleAnalysisReviewed(String schemaPrefix, Token token, Integer sampleId, String[] whereFieldName, Object[] whereFieldValue) {    
        String sampleAnalysisStatusReviewed = Parameter.getParameterBundle(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA).replace("\"", ""), "sampleAnalysis_statusReviewed");        
        if (whereFieldName==null) whereFieldName=new String[0];
        if (whereFieldValue==null) whereFieldValue=new String[0];
        whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName());
        whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, sampleId);
        Object[][] grouper = Rdbms.getGrouper(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsData.SampleAnalysis.TBL.getName(), 
                new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName()}, whereFieldName, whereFieldValue, null);
        if (grouper.length!=1) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "SampleAnalysisPendingRevision", null);
        if (!grouper[0][0].toString().equalsIgnoreCase(sampleAnalysisStatusReviewed))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "SampleAnalysisPendingRevision", null);
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "", null);
    }
    
}
