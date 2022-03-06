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
import functionaljavaa.modulesample.DataModuleSampleAnalysisResult;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleAnalysisBusinessRules;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleAnalysisErrorTrapping;
import java.lang.reflect.Method;
import java.util.Arrays;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleErrorTrapping;
import functionaljavaa.samplestructure.DataSampleStructureStatuses.SampleAnalysisResultStatuses;
import functionaljavaa.samplestructure.DataSampleStructureStatuses.SampleAnalysisStatuses;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author Administrator
 */
public class DataSampleAnalysis{// implements DataSampleAnalysisStrategy{

    
    public enum DataSampleAnalyisAutoAddLevel{    
        DISABLE("DISABLE"), SPEC("SPEC"), SPEC_VARIATION("SPEC_VARIATION")
        ; 
        private final String name;
        DataSampleAnalyisAutoAddLevel(String name) {this.name = name;}
        public String getName() {return name;}
    }
    /**
     *
     * @param sampleId
     * @param testId
     * @return diagnoses
     */
    public static Object[] sampleAnalysisReview(Integer sampleId, Integer testId) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());

        String sampleAnalysisStatusCanceled = SampleAnalysisStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisStatusReviewed = SampleAnalysisStatuses.REVIEWED.getStatusCode("");
        Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId}, 
                new String[]{TblsData.SampleAnalysis.STATUS.getName(), TblsData.SampleAnalysis.STATUS_PREVIOUS.getName(), TblsData.SampleAnalysis.TEST_ID.getName(), TblsData.SampleAnalysis.SAMPLE_ID.getName()});
        String currStatus = (String) objectInfo[0][0];
        if (sampleId==null)
            sampleId = (Integer) objectInfo[0][3];        
        if ((!(sampleAnalysisStatusCanceled.equalsIgnoreCase(currStatus))) && (!(sampleAnalysisStatusReviewed.equalsIgnoreCase(currStatus))) && (testId != null)) {
            String[] updateFldNames=new String[]{TblsData.SampleAnalysis.STATUS.getName(), TblsData.SampleAnalysis.STATUS_PREVIOUS.getName()};
            Object[] updateFldValues=new Object[]{sampleAnalysisStatusReviewed, currStatus};
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                    updateFldNames, updateFldValues, new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId});
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                SampleAudit smpAudit = new SampleAudit();
                Object[] sampleAuditAdd = smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_REVIEWED.toString(), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), testId, sampleId, testId, null, updateFldNames, updateFldValues);
                sampleAnalysisEvaluateStatusAutomatismForReview(sampleId, testId, SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_REVIEWED.toString(), (Integer) sampleAuditAdd[sampleAuditAdd.length-1]);
            }
            return diagnoses;
        } else {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.RESULT_NOT_REVIEWABLE, new Object[]{LPNulls.replaceNull(testId), schemaDataName, currStatus});
        }
    }
    public static Object[] sampleAnalysisEvaluateStatusAutomatismForReview(Integer sampleId, Integer testId, String parentAuditAction, Integer parentAuditId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] whereFieldName=new String[]{};
        Object[] whereFieldValue=new Object[]{};
        Object[][] testInfo=new Object[][]{{}};
        Object[] isRevisionTestinGroupRequired = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleAnalysisBusinessRules.REVISION_TESTINGGROUP_REQUIRED.getAreaName(), DataSampleAnalysisBusinessRules.REVISION_TESTINGGROUP_REQUIRED.getTagName());
        Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP.getTableName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())){
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionTestinGroupRequired[0].toString())){
                testInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                    new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId}, 
                    new String[]{TblsData.SampleAnalysis.TESTING_GROUP.getName(), TblsData.SampleAnalysis.SAMPLE_ID.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testInfo[0][0].toString())) return LPArray.array2dTo1d(testInfo);
                whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsData.SampleAnalysis.TESTING_GROUP.getName());
                whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, testInfo[0][0].toString());
            }
            if (sampleId==null)
                sampleId= (Integer) testInfo[0][1];
            Object[] areAllsampleAnalysisReviewed = isAllsampleAnalysisReviewed(sampleId, whereFieldName, whereFieldValue);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areAllsampleAnalysisReviewed[0].toString())) return areAllsampleAnalysisReviewed;            
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionTestinGroupRequired[0].toString()))
                return DataSampleRevisionTestingGroup.setReadyForRevision(sampleId, testInfo[0][0].toString());
            else
                return DataSample.setReadyForRevision(sampleId, parentAuditAction,parentAuditId);
        }
        return DataSample.setReadyForRevision(sampleId, parentAuditAction,parentAuditId);        
    }
    
    /**
     *
     * @param sampleId
     * @param testId
     * @param parentAuditAction
     * @param parentAuditId
     * @return
     */
    public static Object[] setReadyForRevision(Integer sampleId, Integer testId, String parentAuditAction, Integer parentAuditId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), TblsData.SampleAnalysis.READY_FOR_REVISION.getName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString())){
            return dbTableExists;
        }
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        String auditActionName = SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_SET_READY_FOR_REVISION.toString();
        if (parentAuditAction != null) {
            auditActionName = parentAuditAction +LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR+ auditActionName;
        }
        String[] sampleAnalysisFieldName=new String[]{TblsData.SampleAnalysis.READY_FOR_REVISION.getName()};
        Object[] sampleAnalysisFieldValue=new Object[]{true};
        Object[][] sampleAnalysisInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),  
                new String[] {TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId}, sampleAnalysisFieldName);
        if ("TRUE".equalsIgnoreCase(sampleAnalysisInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ALREADY_READYFORREVISION, new Object[]{testId, sampleId, procInstanceName});
        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), sampleAnalysisFieldName, sampleAnalysisFieldValue, 
                                                new String[] {TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
            SampleAudit smpAudit = new SampleAudit();       
            smpAudit.sampleAuditAdd(auditActionName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), testId, sampleId, null, null, sampleAnalysisFieldName, sampleAnalysisFieldValue);
        }    
        return diagnoses;
    }

    public static Object[] isReadyForRevision(Integer testId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String sampleAnalysisStatusCanceled = SampleAnalysisStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisStatusReviewed = SampleAnalysisStatuses.REVIEWED.getStatusCode("");

        Object[][] sampleAnalysisInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),  
                new String[] {TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId}, 
                new String[] {TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.STATUS.getName()});
        String sampleIdStr=sampleAnalysisInfo[0][0].toString();
        String currStatus=sampleAnalysisInfo[0][1].toString();
        if (sampleAnalysisStatusCanceled.equalsIgnoreCase(currStatus))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSIS_CANCELED, new Object[]{testId, Integer.valueOf(sampleIdStr), procInstanceName});            
        if (sampleAnalysisStatusReviewed.equalsIgnoreCase(currStatus)) 
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ALREADY_REVIEWED, new Object[]{testId, Integer.valueOf(sampleIdStr), procInstanceName});            
             
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAnalysisInfo[0][0].toString()))
            return LPArray.array2dTo1d(sampleAnalysisInfo);
        return isReadyForRevision((Integer) sampleAnalysisInfo[0][0], testId);
    }
    /**
     *
     * @param sampleId
     * @param testId
     * @return
     */
    public static Object[] isReadyForRevision(Integer sampleId, Integer testId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), TblsData.SampleAnalysis.READY_FOR_REVISION.getName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString())){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleAnalysisErrorTrapping.NOT_IMPLEMENTED, new Object[]{testId, sampleId, procInstanceName});
        }
        String[] sampleAnalysisFieldToRetrieve=new String[]{TblsData.SampleAnalysis.READY_FOR_REVISION.getName()};
        Object[][] sampleAnalysisInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),  
                new String[] {TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId}, sampleAnalysisFieldToRetrieve);
        if ("TRUE".equalsIgnoreCase(sampleAnalysisInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleAnalysisErrorTrapping.READY_FOR_REVISION, new Object[]{testId, sampleId, procInstanceName});
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleAnalysisErrorTrapping.NOT_IMPLEMENTED, new Object[]{testId, sampleId, procInstanceName});
        //return diagnoses;
    }
        
    

    /**
     *
     * @param sampleId
     * @param testId
     * @param parentAuditId
     * @param parentAuditAction
     * @return
     */
    public static Object[] sampleAnalysisEvaluateStatus(Integer sampleId, Integer testId, String parentAuditAction, Integer parentAuditId) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String auditActionName = SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_EVALUATE_STATUS.toString();
        if (parentAuditAction != null) {
            auditActionName = parentAuditAction +LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR+ auditActionName;
        }
        String sampleAnalysisStatusIncomplete = SampleAnalysisStatuses.INCOMPLETE.getStatusCode("");
        String sampleAnalysisStatusComplete = SampleAnalysisStatuses.COMPLETE.getStatusCode("");
        String smpAnaNewStatus = "";
        Object[] diagnoses = Rdbms.existsRecord(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.MANDATORY.getName()}, 
                new Object[]{testId, SampleAnalysisResultStatuses.BLANK.getStatusCode(""), true});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
            smpAnaNewStatus = sampleAnalysisStatusIncomplete;
        } else {
            smpAnaNewStatus = sampleAnalysisStatusComplete;
        }
        
        Object[][] sampleAnalysisInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), new String[]{TblsData.SampleAnalysis.TEST_ID.getName()},
                new Object[]{testId}, new String[]{TblsData.SampleAnalysis.STATUS.getName()});
        if (sampleAnalysisInfo[0][0].toString().equalsIgnoreCase(smpAnaNewStatus))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "noChangeRequired", null);

        diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), new String[]{TblsData.SampleAnalysis.STATUS.getName()}, 
                new Object[]{smpAnaNewStatus}, new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {            
            if (sampleAnalysisStatusComplete.equalsIgnoreCase(smpAnaNewStatus))                
                sampleAnalysisEvaluateStatusAutomatismForComplete(sampleId, testId, parentAuditAction, parentAuditId);
            String[] fieldsForAudit = new String[0];
            SampleAudit smpAudit = new SampleAudit();
            smpAudit.sampleAuditAdd(auditActionName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                testId, sampleId, testId, null, new String[]{TblsData.SampleAnalysis.STATUS.getName()}, new Object[]{smpAnaNewStatus});
            if (sampleAnalysisStatusComplete.equalsIgnoreCase(smpAnaNewStatus)){
                sampleAnalysisEvaluateStatusAutomatismForAutoApprove(sampleId, testId, parentAuditAction, parentAuditId);
                return diagnoses;
            }
        }        
        DataSample.sampleEvaluateStatus(sampleId, parentAuditAction, parentAuditId);
        return diagnoses;
    }
    public static Object[] sampleAnalysisEvaluateStatusAutomatismForComplete(Integer sampleId, Integer testId,String parentAuditAction,Integer parentAuditId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] isRevisionSampleAnalysisMarkAsReadyForRevisionWhenAllResultsEntered = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleAnalysisBusinessRules.MARK_READYFORREVISION_WHENALLRESULTSENTERED.getAreaName(), DataSampleAnalysisBusinessRules.MARK_READYFORREVISION_WHENALLRESULTSENTERED.getTagName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionSampleAnalysisMarkAsReadyForRevisionWhenAllResultsEntered[0].toString()))
            return setReadyForRevision(sampleId, testId, parentAuditAction, parentAuditId);
        Object[] isRevisionSampleAnalysisMarkAsReviewedWhenAllResultsEntered = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleAnalysisBusinessRules.MARK_READYFORREVISION_WHENALLRESULTSENTERED.getAreaName(), DataSampleAnalysisBusinessRules.MARK_READYFORREVISION_WHENALLRESULTSENTERED.getTagName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionSampleAnalysisMarkAsReviewedWhenAllResultsEntered[0].toString()))
            return sampleAnalysisReview(sampleId, testId);
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "markAsReadyForRevisionWhenAllResultsEnteredNotActive", null);
    }
    
    public static void sampleAnalysisEvaluateStatusAutomatismForAutoApprove(Integer sampleId, Integer testId,String parentAuditAction,Integer parentAuditId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String auditActionName = SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_AUTOAPPROVE.toString();
        if (parentAuditAction != null) {
            auditActionName = parentAuditAction +LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR+ auditActionName;
        }
        Object[] isSampleAnalysisGenericAutoApproveEnabled = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_GENERICAUTOAPPROVEENABLED.getAreaName(), DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_GENERICAUTOAPPROVEENABLED.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isSampleAnalysisGenericAutoApproveEnabled[0].toString()))
            return;
        DataModuleSampleAnalysisResult moduleSmpAnaRes = new DataModuleSampleAnalysisResult();   
        DataSampleAnalysisResult smpAnaRes = new functionaljavaa.samplestructure.DataSampleAnalysisResult(moduleSmpAnaRes);   
        smpAnaRes.sampleAnalysisResultReview(null, testId, null, DataSample.AUTO_APPROVE_USER);
/*        String sampleAnalysisStatusReviewed = SampleAnalysisStatuses.REVIEWED.getStatusCode("");
        String[] updFldsNames=new String[]{TblsData.SampleAnalysis.FLD_STATUS.getName()};
        Object[] updFldsValues=new Object[]{sampleAnalysisStatusReviewed};
        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
        updFldsNames, updFldsValues,
        new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()}, new Object[]{testId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
        String[] fieldsForAudit = new String[0];
        fieldsForAudit = LPArray.addValueToArray1D(fieldsForAudit, TblsData.SampleAnalysis.FLD_STATUS.getName() +LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR+ sampleAnalysisStatusReviewed);
        SampleAudit smpAudit = new SampleAudit();
        smpAudit.sampleAuditAdd(auditActionName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), testId, sampleId, testId, null, fieldsForAudit, parentAuditId);
        }        */
    }
    /**
     *
     * @param testId
     * @param newAnalyst
     * @param dataSample
     * @return
     */
    public static Object[] sampleAnalysisAssignAnalyst(Integer testId, String newAnalyst, DataSample dataSample) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        Boolean assignTestAnalyst = false;
        String testStatusReviewed = SampleAnalysisStatuses.REVIEWED.getStatusCode("");
        String testStatusCanceled = SampleAnalysisStatuses.CANCELED.getStatusCode("");
        String assignmentModes = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_ANALYSTASSIGNMENTMODES.getAreaName(), DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_ANALYSTASSIGNMENTMODES.getAreaName());
        Object[][] testData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, 
                new Object[]{testId}, new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.STATUS.getName(), TblsData.SampleAnalysis.ANALYST.getName(), 
                    TblsData.SampleAnalysis.ANALYSIS.getName(), TblsData.SampleAnalysis.METHOD_NAME.getName(), TblsData.SampleAnalysis.METHOD_VERSION.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testData[0][0].toString())) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSIS_NOTFOUND, new Object[]{testId, procInstanceName});
        }
        Integer sampleId = (Integer) testData[0][0];
        String testStatus = (String) testData[0][1];
        String testCurrAnalyst = (String) testData[0][2];
        String testAnalysis = (String) testData[0][3];
        String testMethodName = (String) testData[0][4];
        Integer testMethodVersion = (Integer) testData[0][5]; 
        if (testCurrAnalyst == null ? newAnalyst == null : testCurrAnalyst.equals(newAnalyst)) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAME_ANALYST, new Object[]{testCurrAnalyst, testId, procInstanceName});
        }
        // the test status cannot be reviewed or canceled, should be checked
        if ((testCurrAnalyst != null) && (testStatus.equalsIgnoreCase(testStatusReviewed) || testStatus.equalsIgnoreCase(testStatusCanceled))) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE,DataSampleAnalysisErrorTrapping.SAMPLEANALYSIS_LOCKED, new Object[]{testStatus, testId, newAnalyst, procInstanceName});
        }
        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), 
                new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()});
        String sampleConfigCode = (String) sampleData[0][0];
        Integer sampleConfigCodeVersion = (Integer) sampleData[0][1];
        Object[][] sampleRulesData = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.TablesConfig.SAMPLE_RULES.getTableName(), 
                new String[]{TblsCnfg.SampleRules.CODE.getName(), TblsCnfg.SampleRules.CODE_VERSION.getName()}, 
                new Object[]{sampleConfigCode, sampleConfigCodeVersion}, 
                new String[]{TblsCnfg.SampleRules.CODE.getName(), TblsCnfg.SampleRules.CODE_VERSION.getName(), TblsCnfg.SampleRules.ANALYST_ASSIGNMENT_MODE.getName()});
        String testAssignmentMode = (String) sampleRulesData[0][2];
        if (testAssignmentMode == null) {
            testAssignmentMode = "null";
        }
        if (!assignmentModes.contains(testAssignmentMode)) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSISASSIGNED_MODENOTRECOGNIZED, 
                    new Object[]{TblsCnfg.SampleRules.ANALYST_ASSIGNMENT_MODE.getName(), sampleConfigCode, sampleConfigCodeVersion, testAssignmentMode, assignmentModes, procInstanceName,
                    testId, newAnalyst});
        }
        
        if (testAssignmentMode.equalsIgnoreCase("DISABLE")) {
            assignTestAnalyst = true;
        } else {
            UserMethod ana = new UserMethod();
            String userMethodCertificationMode = ana.userMethodCertificationLevel(procInstanceName, testAnalysis, testMethodName, testMethodVersion, newAnalyst);
            String userCertifiedModes = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_ANALYSTASSIGNMENTMODE.getAreaName(), DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_ANALYSTASSIGNMENTMODE.getTagName()+testAssignmentMode);
            String[] userMethodModesArr = userCertifiedModes.split("\\|");
            assignTestAnalyst = LPArray.valueInArray(userMethodModesArr, userMethodCertificationMode);
            if (!assignTestAnalyst) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSISASSIGNED_MODENOTIMPLEMENTED, new Object[]{testAssignmentMode, Arrays.toString(userMethodModesArr), userMethodCertificationMode, schemaDataName});
            }
        }
        if (assignTestAnalyst) {
            String[] updateFieldName = new String[]{TblsData.SampleAnalysis.ANALYST.getName(), TblsData.SampleAnalysis.ANALYST_ASSIGNED_ON.getName(), TblsData.SampleAnalysis.ANALYST_ASSIGNED_BY.getName()};
            Object[] updateFieldValue = new Object[]{newAnalyst, LPDate.getCurrentTimeStamp(), token.getUserName()};
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), updateFieldName, updateFieldValue, 
                    new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId});
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSISASSIGNED_SUCCESS, new Object[]{testId, newAnalyst, schemaDataName});
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_ANALYST_ASSIGNMENT.toString(), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                        testId, sampleId, testId, null, updateFieldName, updateFieldValue);
                return diagnoses;
            }
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.DB_RETURNEDERROR, new Object[]{testId, newAnalyst, schemaDataName});
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ESCAPE_UNHANDLEDEXCEPTION, new Object[]{procInstanceName, token.getUserName(), testId, newAnalyst, token.getUserRole()});
    }
        
    /**
     *
     * @param procInstanceName
     * @param dataSample
     * @return
     */
/*    public String specialFieldCheckSampleAnalysisMethod(String procInstanceName, DataSample dataSample) {
        String myDiagnoses = "";
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
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
        String[] fieldNames = new String[]{TblsCnfg.AnalysisMethod.ANALYSIS.getName(), TblsCnfg.AnalysisMethod.METHOD_NAME.getName(), TblsCnfg.AnalysisMethod.METHOD_VERSION.getName()};
        Object[] fieldValues = new Object[]{analysis, methodName, methodVersion};
        Object[] diagnosis = Rdbms.existsRecord(schemaConfigName, TblsCnfg.TablesConfig.ANALYSIS_METHOD.getTableName(), fieldNames, fieldValues);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())) {
            myDiagnoses = DataSample.DIAGNOSES_SUCCESS;
        } else {
            diagnosis = Rdbms.existsRecord(schemaConfigName, TblsData.Sample.FIELDNAME_ANALYSIS, new String[]{TblsData.Sample.FIELDNAME_CODE}, new Object[]{analysis});
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())) {
                myDiagnoses = "ERROR: The analysis " + analysis + " exists but the method " + methodName + " with version " + methodVersion + " was not found in the schema " + procInstanceName;
            } else {
                myDiagnoses = "ERROR: The analysis " + analysis + " is not found in the schema " + procInstanceName;
            }
        }
        return myDiagnoses;
    }
*/    
    /**
     *
     * @param sampleId
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public static Object[] sampleAnalysisAddtoSample(Integer sampleId, String[] fieldName, Object[] fieldValue) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] mandatoryFields = null;
        Object[] mandatoryFieldsValue = null;
        DataDataIntegrity labIntChecker = new DataDataIntegrity(); 
    
        String tableName = TblsData.TablesData.SAMPLE_ANALYSIS.getTableName();
        String actionName = "Insert";
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        String sampleLevel = TblsData.TablesData.SAMPLE.getTableName();
        mandatoryFields = labIntChecker.getTableMandatoryFields(sampleLevel + tableName, actionName);
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
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS, 
                    new String[]{mandatoryFieldsMissingBuilder.toString(), Arrays.toString(fieldName), schemaConfigName});
        }
        // set first status. Begin
        String firstStatus = SampleAnalysisStatuses.getStatusFirstCode();
        Integer specialFieldIndex = Arrays.asList(fieldName).indexOf(TblsData.Sample.STATUS.getName());
        if (specialFieldIndex == -1) {
            fieldName = LPArray.addValueToArray1D(fieldName, TblsData.Sample.STATUS.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, firstStatus);
        } else {
            fieldValue[specialFieldIndex] = firstStatus;
        }
        // set first status. End
        // Spec Business Rule. Allow other analyses. Begin
        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.STATUS.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleData[0][0].toString())) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_NOT_FOUND, new Object[]{sampleId, schemaDataName});
        }
        String sampleSpecCode = "";
        Integer sampleSpecCodeVersion = null;
        String sampleSpecVariationName = "";
        String specAnalysisTestingGroup="";
        Object[][] sampleSpecData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.SPEC_CODE.getName(), TblsData.Sample.SPEC_CODE_VERSION.getName(), 
                    TblsData.Sample.SPEC_VARIATION_NAME.getName(), TblsData.Sample.STATUS.getName()});
        if ((sampleSpecData[0][0] == null) || (!LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleSpecData[0][0].toString()))) {
            sampleSpecCode = (String) sampleSpecData[0][1];
            sampleSpecCodeVersion = (Integer) sampleSpecData[0][2];
            sampleSpecVariationName = (String) sampleSpecData[0][3];
            if (sampleSpecCode != null) {
                Object[][] specRules = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.TablesConfig.SPEC_RULES.getTableName(), 
                        new String[]{TblsCnfg.SpecRules.CODE.getName(), TblsCnfg.SpecRules.CONFIG_VERSION.getName()}, 
                        new Object[]{sampleSpecCode, sampleSpecCodeVersion}, new String[]{TblsCnfg.SpecRules.ALLOW_OTHER_ANALYSIS.getName(), TblsCnfg.SpecRules.ALLOW_MULTI_SPEC.getName(), 
                            TblsCnfg.SpecRules.CODE.getName(), TblsCnfg.SpecRules.CONFIG_VERSION.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(specRules[0][0].toString())) {
                    return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SPECRULE_NOTFOUND, new Object[]{sampleSpecCode, sampleSpecCodeVersion, schemaDataName});
                }
                if (!Boolean.valueOf(specRules[0][0].toString())) {
                    String[] specAnalysisFieldName = new String[]{TblsCnfg.SpecLimits.ANALYSIS.getName(), TblsCnfg.SpecLimits.METHOD_NAME.getName(), TblsCnfg.SpecLimits.METHOD_VERSION.getName()};
                    Object[] specAnalysisFieldValue = new Object[0];
                    for (String iFieldN : specAnalysisFieldName) {
                        specialFieldIndex = Arrays.asList(fieldName).indexOf(iFieldN);
                        if (specialFieldIndex == -1) 
                            specAnalysisFieldValue = LPArray.addValueToArray1D(specAnalysisFieldValue, "is null");
                        else 
                            specAnalysisFieldValue = LPArray.addValueToArray1D(specAnalysisFieldValue, fieldValue[specialFieldIndex]);                        
                    }
                    specAnalysisFieldName = LPArray.addValueToArray1D(specAnalysisFieldName, 
                        new String[]{TblsCnfg.SpecLimits.CODE.getName(), TblsCnfg.SpecLimits.CONFIG_VERSION.getName(), TblsCnfg.SpecLimits.VARIATION_NAME.getName()});
                    specAnalysisFieldValue = LPArray.addValueToArray1D(specAnalysisFieldValue, 
                        new Object[]{sampleSpecCode, sampleSpecCodeVersion, sampleSpecVariationName});

                    Object[] isReviewByTestingGroupEnable=LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleRevisionTestingGroup.DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP.getTagName());
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString())){
                        Object[][] analysisTestingGroup = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName(), 
                            specAnalysisFieldName, specAnalysisFieldValue, 
                            new String[]{TblsCnfg.SpecLimits.TESTING_GROUP.getName()});
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisTestingGroup[0][0].toString())) {
                            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SPECLIMIT_NOTFOUND, new Object[]{Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(specAnalysisFieldName, specAnalysisFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR)), schemaDataName});
                        }                    
                        specAnalysisTestingGroup=analysisTestingGroup[0][0].toString();
                    }
                }
            }
        }
        // Spec Business Rule. Allow other analyses. End
        String[] specialFields = labIntChecker.getStructureSpecialFields(sampleLevel + "Structure");
        String[] specialFieldsFunction = labIntChecker.getStructureSpecialFieldsFunction(sampleLevel + "Structure");
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
                    errorDetailVariablesLocal = LPArray.addValueToArray1D(errorDetailVariablesLocal, currField);
                    errorDetailVariablesLocal = LPArray.addValueToArray1D(errorDetailVariablesLocal, aMethod);
                    errorDetailVariablesLocal = LPArray.addValueToArray1D(errorDetailVariablesLocal, ex.getMessage());
                    return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping, errorDetailVariablesLocal);
                }
                Object specialFunctionReturn = null;
                try {
                    if (method != null) {
                        specialFunctionReturn = method.invoke(this, procInstanceName);
                    }
                } catch (IllegalAccessException | NullPointerException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(DataSample.class.getName()).log(Level.SEVERE, null, ex);
                }
                if ((specialFunctionReturn == null) || (specialFunctionReturn != null && specialFunctionReturn.toString().contains("ERROR"))) {
                    errorCode = "DataSample_SpecialFunctionReturnedERROR";
                    errorDetailVariablesLocal = LPArray.addValueToArray1D(errorDetailVariablesLocal, currField);
                    errorDetailVariablesLocal = LPArray.addValueToArray1D(errorDetailVariablesLocal, aMethod);
                    errorDetailVariablesLocal = LPArray.addValueToArray1D(errorDetailVariablesLocal, LPNulls.replaceNull(specialFunctionReturn));
                    return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping, errorDetailVariablesLocal);
                }*/
            }
        }
        Object value = null;
        Object[] whereResultFieldValue = new Object[0];
        String[] whereResultFieldName = new String[0];
        String fieldNeed = TblsCnfg.AnalysisMethodParams.ANALYSIS.getName();
        whereResultFieldName = LPArray.addValueToArray1D(whereResultFieldName, fieldNeed);
        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(fieldNeed);
        if (specialFieldIndex == -1) {
            specialFieldIndex = Arrays.asList(fieldName).indexOf(fieldNeed);
            if (specialFieldIndex == -1) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS, 
                    new String[]{fieldNeed, Arrays.toString(mandatoryFields), schemaDataName});
            }
            value = fieldValue[specialFieldIndex];
        } else {
            value = mandatoryFieldsValue[specialFieldIndex];
        }
        whereResultFieldValue = LPArray.addValueToArray1D(whereResultFieldValue, value);
        fieldNeed = TblsCnfg.AnalysisMethodParams.METHOD_NAME.getName();
        whereResultFieldName = LPArray.addValueToArray1D(whereResultFieldName, fieldNeed);
        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(fieldNeed);
        if (specialFieldIndex == -1) {
            specialFieldIndex = Arrays.asList(fieldName).indexOf(fieldNeed);
            if (specialFieldIndex == -1) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS, 
                    new String[]{fieldNeed, Arrays.toString(mandatoryFields), schemaDataName});
            }
            value = fieldValue[specialFieldIndex];
        } else {
            value = mandatoryFieldsValue[specialFieldIndex];
        }
        whereResultFieldValue = LPArray.addValueToArray1D(whereResultFieldValue, value);
        fieldNeed = TblsCnfg.AnalysisMethodParams.METHOD_VERSION.getName();
        whereResultFieldName = LPArray.addValueToArray1D(whereResultFieldName, fieldNeed);
        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(fieldNeed);
        if (specialFieldIndex == -1) {
            specialFieldIndex = Arrays.asList(fieldName).indexOf(fieldNeed);
            if (specialFieldIndex == -1) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS, 
                    new String[]{fieldNeed, Arrays.toString(mandatoryFields), schemaDataName});
            }
            value = fieldValue[specialFieldIndex];
        } else {
            value = mandatoryFieldsValue[specialFieldIndex];
        }
        whereResultFieldValue = LPArray.addValueToArray1D(whereResultFieldValue, value);
        String[] getResultFields = new String[]{TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName(), TblsCnfg.AnalysisMethodParams.MANDATORY.getName(), TblsCnfg.AnalysisMethodParams.ANALYSIS.getName(),
            TblsCnfg.AnalysisMethodParams.PARAM_TYPE.getName(), TblsCnfg.AnalysisMethodParams.NUM_REPLICAS.getName(), TblsCnfg.AnalysisMethodParams.UOM.getName(), TblsCnfg.AnalysisMethodParams.UOM_CONVERSION_MODE.getName()};
        Object[][] resultFieldRecords = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS.getTableName(), 
                whereResultFieldName, whereResultFieldValue, getResultFields);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultFieldRecords[0][0].toString())) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ANALYSISMETHOD_PARAMSNOTFOUND, new Object[]{Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(whereResultFieldName, whereResultFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR)), schemaDataName});
        }
        resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, sampleId);
        getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.SAMPLE_ID.getName());
        resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, 0);
        getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.TEST_ID.getName());
        // This is temporary !!!! ***************************************************************
        specialFieldIndex = Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.STATUS.getName());
        String firstStatusSampleAnalysisResult = SampleAnalysisResultStatuses.getStatusFirstCode();
        if (specialFieldIndex == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, firstStatusSampleAnalysisResult);
            getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.STATUS.getName());
        }
        // This is temporary !!!! ***************************************************************
        String[] resultMandatoryFields = labIntChecker.getTableMandatoryFields(sampleLevel, actionName);
        String[] resultDefaulFields = labIntChecker.getTableFieldsDefaulValues(tableName, actionName);
        Object[] resultDefaulFieldValue = labIntChecker.getTableFieldsDefaulValues(tableName, actionName);
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
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{resultMandatoryFieldsMissingBuilder, schemaDataName});
        }
        fieldName = LPArray.addValueToArray1D(fieldName, new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.ADDED_ON.getName(), TblsData.SampleAnalysis.ADDED_BY.getName()});
        fieldValue = LPArray.addValueToArray1D(fieldValue, new Object[]{sampleId, Rdbms.getCurrentDate(), token.getUserName()});
        Object[] isReviewByTestingGroupEnable=LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleRevisionTestingGroup.DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP.getTagName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString()) &&                
                !LPArray.valueInArray(fieldName, TblsData.SampleAnalysis.TESTING_GROUP.getName())){
            fieldName = LPArray.addValueToArray1D(fieldName, TblsData.SampleAnalysis.TESTING_GROUP.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, specAnalysisTestingGroup);
        }
        String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(fieldName, fieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR);
        Object[] diagnoses = Rdbms.insertRecordInTable(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), fieldName, fieldValue);
        Integer testId = Integer.parseInt(diagnoses[diagnoses.length - 1].toString());
        SampleAudit smpAudit = new SampleAudit();
        smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisAuditEvents.SAMPLE_ANALYSIS_ADDED.toString(), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                testId, sampleId, testId, null, fieldName, fieldValue);
        Integer valuePosic = Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.TEST_ID.getName());
        if (valuePosic == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, testId);
            getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.TEST_ID.getName());
        }else
            resultFieldRecords = LPArray.setColumnValueToArray2D(resultFieldRecords, valuePosic, testId);        
        valuePosic = Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.METHOD_NAME.getName());
        if (valuePosic == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.METHOD_NAME.getName())]);
            getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.METHOD_NAME.getName());
        }
        valuePosic = Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.METHOD_VERSION.getName());
        if (valuePosic == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.METHOD_VERSION.getName())]);
            getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.METHOD_VERSION.getName());
        }
        for (Object[] resultFieldRecord : resultFieldRecords) {
            Object[] fieldVal = new Object[0];
            for (int col = 0; col < resultFieldRecords[0].length; col++) {
                fieldVal = LPArray.addValueToArray1D(fieldVal, resultFieldRecord[col]);
            }
            valuePosic = Arrays.asList(getResultFields).indexOf(TblsCnfg.AnalysisMethodParams.NUM_REPLICAS.getName());
            Integer numReplicas = 1;
            String resultReplicaFieldName = TblsData.SampleAnalysisResult.REPLICA.getName();
            if (valuePosic == -1) {
                valuePosic = Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.REPLICA.getName());
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
            Object[][] specLimits = ConfigSpecRule.getSpecLimitLimitIdFromSpecVariables(sampleSpecCode, sampleSpecCodeVersion, 
                    sampleSpecVariationName, 
                    fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.ANALYSIS.getName())].toString(), 
                    fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.METHOD_NAME.getName())].toString(), 
                    (Integer) fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.METHOD_VERSION.getName())], 
                    fieldVal[Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.PARAM_NAME.getName())].toString(), 
                    new String[]{TblsCnfg.SpecLimits.LIMIT_ID.getName()});
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimits[0][0].toString())){
                      getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.LIMIT_ID.getName());     
                      fieldVal = LPArray.addValueToArray1D(fieldVal, specLimits[0][0]);                           
                    }
            }
            for (Integer iNumReps = 1; iNumReps <= numReplicas; iNumReps++) {
                fieldVal[valuePosic] = iNumReps;
                Integer statusFieldPosic=LPArray.valuePosicInArray(getResultFields, TblsData.SampleAnalysisResult.STATUS.getName());
                if (statusFieldPosic==-1){
                    getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.STATUS.getName());
                    fieldVal = LPArray.addValueToArray1D(fieldVal, firstStatusSampleAnalysisResult);
                }else
                    fieldVal[statusFieldPosic]=firstStatusSampleAnalysisResult;
                
                diagnoses = Rdbms.insertRecordInTable(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                        getResultFields, fieldVal);
                Integer resultId = Integer.parseInt(diagnoses[diagnoses.length - 1].toString());
                smpAudit.sampleAuditAdd(SampleAudit.SampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ADDED.toString(), sampleLevel + TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                    resultId, sampleId, testId, resultId, getResultFields, fieldVal);
            }
        }
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString())){
            Object[] addSampleRevisionByTestingGroup = DataSampleRevisionTestingGroup.addSampleRevisionByTestingGroup(sampleId, testId, specAnalysisTestingGroup);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(addSampleRevisionByTestingGroup[0].toString())) return addSampleRevisionByTestingGroup;
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleAnalysisErrorTrapping.SAMPLE_ANALYSIS_ADDED_SUCCESS, new Object[]{"", testId, schemaDataName});
    }
    public static Object[] isAllsampleAnalysisReviewed(Integer sampleId, String[] whereFieldName, Object[] whereFieldValue) {    
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String sampleAnalysisStatusReviewed = SampleAnalysisStatuses.REVIEWED.getStatusCode("");
        if (whereFieldName==null) whereFieldName=new String[0];
        if (whereFieldValue==null) whereFieldValue=new String[0];
        whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsData.SampleAnalysis.SAMPLE_ID.getName());
        whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, sampleId);
        Object[][] grouper = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                new String[]{TblsData.SampleAnalysis.STATUS.getName()}, whereFieldName, whereFieldValue, null);
        if (grouper.length!=1) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.PENDING_REVISION, null);
        if (!grouper[0][0].toString().equalsIgnoreCase(sampleAnalysisStatusReviewed))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.PENDING_REVISION, null);
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "", null);
    }
    
}
