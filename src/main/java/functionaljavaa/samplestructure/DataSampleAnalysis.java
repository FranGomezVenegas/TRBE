/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import com.labplanet.servicios.modulesample.SampleAPIParams.SampleAPIactionsEndpoints;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.LPPlatform;
import databases.DataDataIntegrity;
import databases.TblsCnfg;
import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import databases.features.Token;
import modules.masterdata.analysis.UserMethod;
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
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleStructureSuccess;
import functionaljavaa.samplestructure.DataSampleStructureStatuses.SampleAnalysisResultStatuses;
import functionaljavaa.samplestructure.DataSampleStructureStatuses.SampleAnalysisStatuses;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntTableFields;
import trazit.session.InternalMessage;
import trazit.session.ResponseMessages;

/**
 *
 * @author Administrator
 */
public class DataSampleAnalysis {// implements DataSampleAnalysisStrategy{

    public enum DataSampleAnalyisAutoAddLevel {
        DISABLE("DISABLE"), SPEC("SPEC"), SPEC_VARIATION("SPEC_VARIATION");
        private final String name;

        DataSampleAnalyisAutoAddLevel(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     *
     * @param sampleId
     * @param testId
     * @return diagnoses
     */
    public static InternalMessage sampleAnalysisReview(Integer sampleId, Integer testId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());

        String sampleAnalysisStatusCanceled = SampleAnalysisStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisStatusReviewed = SampleAnalysisStatuses.REVIEWED.getStatusCode("");
        Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId},
                new String[]{TblsData.SampleAnalysis.STATUS.getName(), TblsData.SampleAnalysis.STATUS_PREVIOUS.getName(), TblsData.SampleAnalysis.TEST_ID.getName(), TblsData.SampleAnalysis.SAMPLE_ID.getName()});
        String currStatus = (String) objectInfo[0][0];
        if (sampleId == null) {
            sampleId = Integer.valueOf(LPNulls.replaceNull(objectInfo[0][3].toString()));
        }
        if ((Boolean.FALSE.equals((sampleAnalysisStatusCanceled.equalsIgnoreCase(currStatus)))) && (Boolean.FALSE.equals((sampleAnalysisStatusReviewed.equalsIgnoreCase(currStatus)))) && (testId != null)) {
            String[] updateFldNames = new String[]{TblsData.SampleAnalysis.STATUS.getName(), TblsData.SampleAnalysis.STATUS_PREVIOUS.getName()};
            Object[] updateFldValues = new Object[]{sampleAnalysisStatusReviewed, currStatus};
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsData.SampleAnalysis.TEST_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{testId}, "");
            RdbmsObject diagnoses = Rdbms.updateTableRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS, updateFldNames), updateFldValues, sqlWhere, null);
            if (Boolean.TRUE.equals(diagnoses.getRunSuccess())) {
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_REVIEWED, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), testId, sampleId, testId, null, updateFldNames, updateFldValues);
                sampleAnalysisEvaluateStatusAutomatismForReview(sampleId, testId);
            }
            return new InternalMessage(diagnoses.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, diagnoses.getErrorMessageCode(), diagnoses.getErrorMessageVariables());
        } else {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.RESULT_NOT_REVIEWABLE, new Object[]{LPNulls.replaceNull(testId), schemaDataName, currStatus});
        }
    }

    public static InternalMessage sampleAnalysisEvaluateStatusAutomatismForReview(Integer sampleId, Integer testId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] whereFieldName = new String[]{};
        Object[] whereFieldValue = new Object[]{};
        Object[][] testInfo = new Object[][]{{}};
        InternalMessage isRevisionTestinGroupRequired = LPPlatform.isProcedureBusinessRuleEnableInternalMessage(procInstanceName, DataSampleAnalysisBusinessRules.REVISION_TESTINGGROUP_REQUIRED.getAreaName(), DataSampleAnalysisBusinessRules.REVISION_TESTINGGROUP_REQUIRED.getTagName());
        Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP.getTableName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())) {
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionTestinGroupRequired.getDiagnostic())) {
                testInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                        new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId},
                        new String[]{TblsData.SampleAnalysis.TESTING_GROUP.getName(), TblsData.SampleAnalysis.SAMPLE_ID.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testInfo[0][0].toString())) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{sampleId, testId});
                }
                whereFieldName = LPArray.addValueToArray1D(whereFieldName, TblsData.SampleAnalysis.TESTING_GROUP.getName());
                whereFieldValue = LPArray.addValueToArray1D(whereFieldValue, testInfo[0][0].toString());
            }
            if (sampleId == null) {
                sampleId = Integer.valueOf(LPNulls.replaceNull(testInfo[0][1].toString()));
            }
            InternalMessage areAllsampleAnalysisReviewed = isAllsampleAnalysisReviewed(sampleId, whereFieldName, whereFieldValue);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areAllsampleAnalysisReviewed.getDiagnostic())) {
                return areAllsampleAnalysisReviewed;
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionTestinGroupRequired.getDiagnostic())) {
                return DataSampleRevisionTestingGroup.setReadyForRevision(sampleId, testInfo[0][0].toString());
            } else {
                return DataSample.setReadyForRevision(sampleId);
            }
        }
        return DataSample.setReadyForRevision(sampleId);
    }

    /**
     *
     * @param sampleId
     * @param testId
     * @return
     */
    public static InternalMessage setReadyForRevision(Integer sampleId, Integer testId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        InternalMessage dbTableExists = Rdbms.dbTableExistsInternalMessage(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), TblsData.SampleAnalysis.READY_FOR_REVISION.getName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists.getDiagnostic())) {
            return dbTableExists;
        }
        EnumIntAuditEvents auditActionName = SampleAudit.DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_SET_READY_FOR_REVISION;
//AuditEvent To Object, commented	        if (parentAuditAction != null) auditActionName = parentAuditAction +LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR+ auditActionName;

        String[] sampleAnalysisFieldName = new String[]{TblsData.SampleAnalysis.READY_FOR_REVISION.getName()};
        Object[] sampleAnalysisFieldValue = new Object[]{true};
        Object[][] sampleAnalysisInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId}, sampleAnalysisFieldName);
        if ("TRUE".equalsIgnoreCase(sampleAnalysisInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ALREADY_READYFORREVISION, new Object[]{testId, sampleId, procInstanceName});
        }
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsData.SampleAnalysis.TEST_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{testId}, "");
        RdbmsObject diagnoses = Rdbms.updateTableRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS,
                EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS, sampleAnalysisFieldName), sampleAnalysisFieldValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnoses.getRunSuccess())) {
            SampleAudit smpAudit = new SampleAudit();
            smpAudit.sampleAuditAdd(auditActionName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), testId, sampleId, null, null, sampleAnalysisFieldName, sampleAnalysisFieldValue);
        }
        return new InternalMessage(diagnoses.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, diagnoses.getErrorMessageCode(), diagnoses.getErrorMessageVariables());
    }

    public static InternalMessage isReadyForRevision(Integer testId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String sampleAnalysisStatusCanceled = SampleAnalysisStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisStatusReviewed = SampleAnalysisStatuses.REVIEWED.getStatusCode("");

        Object[][] sampleAnalysisInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId},
                new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.STATUS.getName()});
        String sampleIdStr = sampleAnalysisInfo[0][0].toString();
        String currStatus = sampleAnalysisInfo[0][1].toString();
        if (sampleAnalysisStatusCanceled.equalsIgnoreCase(currStatus)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleStructureSuccess.SAMPLEANALYSIS_CANCELED,
                    new Object[]{testId, Integer.valueOf(sampleIdStr), procInstanceName});
        }
        if (sampleAnalysisStatusReviewed.equalsIgnoreCase(currStatus)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ALREADY_REVIEWED, new Object[]{testId, Integer.valueOf(sampleIdStr), procInstanceName});
        }

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAnalysisInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{testId});
        }
        return isReadyForRevision(Integer.valueOf(LPNulls.replaceNull(sampleAnalysisInfo[0][0].toString())), testId);
    }

    /**
     *
     * @param sampleId
     * @param testId
     * @return
     */
    public static InternalMessage isReadyForRevision(Integer sampleId, Integer testId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), TblsData.SampleAnalysis.READY_FOR_REVISION.getName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleAnalysisErrorTrapping.NOT_IMPLEMENTED, new Object[]{testId, sampleId, procInstanceName});
        }
        String[] sampleAnalysisFieldToRetrieve = new String[]{TblsData.SampleAnalysis.READY_FOR_REVISION.getName()};
        Object[][] sampleAnalysisInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, new Object[]{testId}, sampleAnalysisFieldToRetrieve);
        if ("TRUE".equalsIgnoreCase(sampleAnalysisInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleAnalysisErrorTrapping.READY_FOR_REVISION, new Object[]{testId, sampleId, procInstanceName});
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleAnalysisErrorTrapping.NOT_IMPLEMENTED, new Object[]{testId, sampleId, procInstanceName});
    }

    /**
     *
     * @param sampleId
     * @param testId
     * @return
     */
    public static InternalMessage sampleAnalysisEvaluateStatus(Integer sampleId, Integer testId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        EnumIntAuditEvents auditActionName = SampleAudit.DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_EVALUATE_STATUS;
        String sampleAnalysisStatusIncomplete = SampleAnalysisStatuses.INCOMPLETE.getStatusCode("");
        String sampleAnalysisStatusComplete = SampleAnalysisStatuses.COMPLETE.getStatusCode("");
        String smpAnaNewStatus = "";
        Object[] diagnoses = Rdbms.existsRecord(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.MANDATORY.getName()},
                new Object[]{testId, SampleAnalysisResultStatuses.BLANK.getStatusCode(""), true});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
            smpAnaNewStatus = sampleAnalysisStatusIncomplete;
        } else {
            smpAnaNewStatus = sampleAnalysisStatusComplete;
        }
        Object[][] sampleAnalysisInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), new String[]{TblsData.SampleAnalysis.TEST_ID.getName()},
                new Object[]{testId}, new String[]{TblsData.SampleAnalysis.STATUS.getName()});
        if (sampleAnalysisInfo[0][0].toString().equalsIgnoreCase(smpAnaNewStatus)) {
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
        }
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsData.SampleAnalysis.TEST_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{testId}, "");
        RdbmsObject diagnoseObj = Rdbms.updateTableRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS,
                EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS, new String[]{TblsData.SampleAnalysis.STATUS.getName()}), new Object[]{smpAnaNewStatus}, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnoseObj.getRunSuccess())) {
            if (sampleAnalysisStatusComplete.equalsIgnoreCase(smpAnaNewStatus)) {
                sampleAnalysisEvaluateStatusAutomatismForComplete(sampleId, testId);
            }
            SampleAudit smpAudit = new SampleAudit();
            smpAudit.sampleAuditAdd(auditActionName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                    testId, sampleId, testId, null, new String[]{TblsData.SampleAnalysis.STATUS.getName()}, new Object[]{smpAnaNewStatus});
            if (sampleAnalysisStatusComplete.equalsIgnoreCase(smpAnaNewStatus)) {
                sampleAnalysisEvaluateStatusAutomatismForAutoApprove(sampleId, testId);
                return new InternalMessage(diagnoseObj.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, diagnoseObj.getErrorMessageCode(), diagnoseObj.getErrorMessageVariables());
            }
        }
        DataSample.sampleEvaluateStatus(sampleId);
        return new InternalMessage(diagnoseObj.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, diagnoseObj.getErrorMessageCode(), diagnoseObj.getErrorMessageVariables());
    }

    public static InternalMessage sampleAnalysisEvaluateStatusAutomatismForComplete(Integer sampleId, Integer testId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] isRevisionSampleAnalysisMarkAsReadyForRevisionWhenAllResultsEntered = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleAnalysisBusinessRules.MARK_READYFORREVISION_WHENALLRESULTSENTERED.getAreaName(), DataSampleAnalysisBusinessRules.MARK_READYFORREVISION_WHENALLRESULTSENTERED.getTagName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionSampleAnalysisMarkAsReadyForRevisionWhenAllResultsEntered[0].toString())) {
            return setReadyForRevision(sampleId, testId);
        }
        Object[] isRevisionSampleAnalysisMarkAsReviewedWhenAllResultsEntered = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleAnalysisBusinessRules.MARK_READYFORREVISION_WHENALLRESULTSENTERED.getAreaName(), DataSampleAnalysisBusinessRules.MARK_READYFORREVISION_WHENALLRESULTSENTERED.getTagName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionSampleAnalysisMarkAsReviewedWhenAllResultsEntered[0].toString())) {
            return sampleAnalysisReview(sampleId, testId);
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
    }

    public static void sampleAnalysisEvaluateStatusAutomatismForAutoApprove(Integer sampleId, Integer testId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] isSampleAnalysisGenericAutoApproveEnabled = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_GENERICAUTOAPPROVEENABLED.getAreaName(), DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_GENERICAUTOAPPROVEENABLED.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isSampleAnalysisGenericAutoApproveEnabled[0].toString())) {
            return;
        }
        DataModuleSampleAnalysisResult moduleSmpAnaRes = new DataModuleSampleAnalysisResult();
        DataSampleAnalysisResult smpAnaRes = new functionaljavaa.samplestructure.DataSampleAnalysisResult(moduleSmpAnaRes);
        smpAnaRes.sampleAnalysisResultReview(null, testId, null, DataSample.AUTO_APPROVE_USER);
    }

    /**
     *
     * @param testId
     * @param newAnalyst
     * @return
     */
    public static InternalMessage sampleAnalysisAssignAnalyst(Integer testId, String newAnalyst) {
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        Boolean assignTestAnalyst = false;
        String testStatusReviewed = SampleAnalysisStatuses.REVIEWED.getStatusCode("");
        String testStatusCanceled = SampleAnalysisStatuses.CANCELED.getStatusCode("");
        String assignmentModes = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_ANALYSTASSIGNMENTMODES.getAreaName(), DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_ANALYSTASSIGNMENTMODES.getAreaName());
        Object[][] testData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                new String[]{TblsData.SampleAnalysis.TEST_ID.getName()},
                new Object[]{testId}, new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.STATUS.getName(), TblsData.SampleAnalysis.ANALYST.getName(),
                    TblsData.SampleAnalysis.ANALYSIS.getName(), TblsData.SampleAnalysis.METHOD_NAME.getName()}); //, TblsData.SampleAnalysis.METHOD_VERSION.getName()
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testData[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSIS_NOTFOUND, new Object[]{testId, procInstanceName});
        }
        Integer sampleId = Integer.valueOf(LPNulls.replaceNull(testData[0][0].toString()));
        String testStatus = (String) testData[0][1];
        String testCurrAnalyst = (String) testData[0][2];
        String testAnalysis = (String) testData[0][3];
        String testMethodName = (String) testData[0][4];
        Integer testMethodVersion = Integer.valueOf(LPNulls.replaceNull(testData[0][5].toString()));
        if (testCurrAnalyst == null ? newAnalyst == null : testCurrAnalyst.equals(newAnalyst)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAME_ANALYST, new Object[]{testCurrAnalyst, testId, procInstanceName});
        }
        // the test status cannot be reviewed or canceled, should be checked
        if ((testCurrAnalyst != null) && (testStatus.equalsIgnoreCase(testStatusReviewed) || testStatus.equalsIgnoreCase(testStatusCanceled))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSIS_LOCKED, new Object[]{testStatus, testId, newAnalyst, procInstanceName});
        }
        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE.getTableName(),
                new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId},
                new String[]{TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()});
        String sampleConfigCode = (String) sampleData[0][0];
        Integer sampleConfigCodeVersion = Integer.valueOf(LPNulls.replaceNull(sampleData[0][1].toString()));
        Object[][] sampleRulesData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.SAMPLE_RULES.getTableName(),
                new String[]{TblsCnfg.SampleRules.CODE.getName(), TblsCnfg.SampleRules.CODE_VERSION.getName()},
                new Object[]{sampleConfigCode, sampleConfigCodeVersion},
                new String[]{TblsCnfg.SampleRules.CODE.getName(), TblsCnfg.SampleRules.CODE_VERSION.getName(), TblsCnfg.SampleRules.ANALYST_ASSIGNMENT_MODE.getName()});
        String testAssignmentMode = (String) sampleRulesData[0][2];
        if (testAssignmentMode == null) {
            testAssignmentMode = "null";
        }
        if (Boolean.FALSE.equals(assignmentModes.contains(testAssignmentMode))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSISASSIGNED_MODENOTRECOGNIZED,
                    new Object[]{TblsCnfg.SampleRules.ANALYST_ASSIGNMENT_MODE.getName(), sampleConfigCode, sampleConfigCodeVersion, testAssignmentMode, assignmentModes, procInstanceName,
                        testId, newAnalyst});
        }

        if (testAssignmentMode.equalsIgnoreCase("DISABLE")) {
            assignTestAnalyst = true;
        } else {
            UserMethod ana = new UserMethod();
            String userMethodCertificationMode = ana.userMethodCertificationLevel(procInstanceName, testAnalysis, testMethodName, testMethodVersion, newAnalyst);
            String userCertifiedModes = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_ANALYSTASSIGNMENTMODE.getAreaName(), DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_ANALYSTASSIGNMENTMODE.getTagName() + testAssignmentMode);
            String[] userMethodModesArr = userCertifiedModes.split("\\|");
            assignTestAnalyst = LPArray.valueInArray(userMethodModesArr, userMethodCertificationMode);
            if (Boolean.FALSE.equals(assignTestAnalyst)) {
                return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSISASSIGNED_MODENOTIMPLEMENTED, new Object[]{testAssignmentMode, Arrays.toString(userMethodModesArr), userMethodCertificationMode, schemaDataName});
            }
        }
        if (Boolean.TRUE.equals(assignTestAnalyst)) {
            String[] updateFieldName = new String[]{TblsData.SampleAnalysis.ANALYST.getName(), TblsData.SampleAnalysis.ANALYST_ASSIGNED_ON.getName(), TblsData.SampleAnalysis.ANALYST_ASSIGNED_BY.getName()};
            Object[] updateFieldValue = new Object[]{newAnalyst, LPDate.getCurrentTimeStamp(), token.getUserName()};
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsData.SampleAnalysis.TEST_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{testId}, "");
            RdbmsObject diagnoses = Rdbms.updateTableRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS, updateFieldName), updateFieldValue, sqlWhere, null);
           if (Boolean.TRUE.equals(diagnoses.getRunSuccess())) {               
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_ANALYST_ASSIGNMENT, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                        testId, sampleId, testId, null, updateFieldName, updateFieldValue);
                return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.SAMPLEANALYSISASSIGNED_SUCCESS, new Object[]{testId, newAnalyst, sampleId});
            }
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.DB_RETURNEDERROR, new Object[]{testId, newAnalyst, schemaDataName});
        }
        return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ESCAPE_UNHANDLEDEXCEPTION, new Object[]{procInstanceName, token.getUserName(), testId, newAnalyst, token.getUserRole()});
    }

    /**
     *
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
        Integer methodVersion = Integer.valueOf(LPNulls.replaceNull(dataSample.mandatoryFieldsValue[specialFieldIndex].toString()));
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
    public static InternalMessage sampleAnalysisRemovetoSample(Integer sampleId, Integer testId, String[] fieldName, Object[] fieldValue) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String[] mandatoryFields = null;
        DataDataIntegrity labIntChecker = new DataDataIntegrity();
        if (sampleId == null) {
            return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
        }
        if (testId == null && fieldName == null) {
            return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
        }
        String[] wFldName = null;
        Object[] wFldValue = null;
        Object value = null;
        if (testId != null) {
            wFldName = new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.TEST_ID.getName()};
            wFldValue = new Object[]{sampleId, testId};
        } else {
            wFldName = new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName()};
            wFldValue = new Object[]{sampleId};
            InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(fieldName, fieldValue);
            if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic()))) {
                return new InternalMessage(LPPlatform.LAB_FALSE, fieldNameValueArrayChecker.getMessageCodeObj(), fieldNameValueArrayChecker.getMessageCodeVariables());
            }
            String[] fieldNeed = new String[]{TblsData.SampleAnalysis.ANALYSIS.getName(), TblsData.SampleAnalysis.METHOD_NAME.getName()}; //, TblsData.SampleAnalysis.METHOD_VERSION.getName()
            for (String curFld : fieldNeed) {
                int specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(curFld);
                if (specialFieldIndex == -1) {
                    specialFieldIndex = Arrays.asList(fieldName).indexOf(curFld);
                    if (specialFieldIndex == -1) {
                        return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS,
                                new String[]{Arrays.toString(fieldNeed), schemaDataName});
                    }
                    value = fieldValue[specialFieldIndex];
                    wFldName = LPArray.addValueToArray1D(wFldName, curFld);
                    wFldValue = LPArray.addValueToArray1D(wFldName, value);
                } else {
                    wFldName = LPArray.addValueToArray1D(wFldName, curFld);
                    wFldValue = LPArray.addValueToArray1D(wFldName, value);
                }
            }
        }
        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                wFldName, wFldValue,
                new String[]{TblsData.SampleAnalysis.STATUS.getName(), TblsData.SampleAnalysis.TEST_ID.getName(), TblsData.SampleAnalysis.ANALYSIS.getName()});
        if (Boolean.TRUE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleData[0][0].toString()))) {
            messages.addMainForError(DataSampleAnalysisErrorTrapping.SAMPLEANALYSIS_NOTFOUND, new Object[]{testId != null ? testId : fieldValue[Arrays.asList(mandatoryFields).indexOf(TblsData.SampleAnalysis.ANALYSIS.getName())], sampleId, schemaDataName});
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLEANALYSIS_NOTFOUND, new Object[]{testId != null ? testId : fieldValue[Arrays.asList(mandatoryFields).indexOf(TblsData.SampleAnalysis.ANALYSIS.getName())], sampleId, schemaDataName});
        }
        String statusCode = DataSampleStructureStatuses.SampleAnalysisStatuses.NOT_STARTED.getStatusCode("");
        if (Boolean.FALSE.equals(LPNulls.replaceNull(sampleData[0][0]).toString().equalsIgnoreCase(statusCode))) {
            messages.addMainForError(DataSampleAnalysisErrorTrapping.SAMPLEANALYSIS_NOTFOUND, new Object[]{LPNulls.replaceNull(sampleData[0][2]).toString(), sampleId, schemaDataName});
            messages.addMainForError(DataSampleAnalysisErrorTrapping.SAMPLE_ANALYSIS_CANNOT_BE_REMOVED, new Object[]{LPNulls.replaceNull(sampleData[0][2]).toString(), sampleData[0][0], sampleId, schemaDataName});
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLE_ANALYSIS_CANNOT_BE_REMOVED, new Object[]{LPNulls.replaceNull(sampleData[0][2]).toString(), sampleData[0][0], sampleId, schemaDataName});
        }
        testId = Integer.valueOf(sampleData[0][1].toString());
        String tableName = TblsData.TablesData.SAMPLE_ANALYSIS.getTableName();
        String actionName = "Delete";
        String sampleLevel = TblsData.TablesData.SAMPLE_ANALYSIS.getTableName();
        mandatoryFields = labIntChecker.getTableMandatoryFields(sampleLevel + tableName, actionName);
        InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(fieldName, fieldValue);
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic()))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, fieldNameValueArrayChecker.getMessageCodeObj(), fieldNameValueArrayChecker.getMessageCodeVariables());
        }
        String[] remFlds = new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.TEST_ID.getName()};
        Object[] remFldsV = new Object[]{sampleId, testId};
        RdbmsObject newResultRdbmsDiagnObj = Rdbms.removeRecordInTable(TblsData.TablesData.SAMPLE_ANALYSIS,
                new SqlWhere(TblsData.TablesData.SAMPLE_ANALYSIS, remFlds, remFldsV), null);
        if (Boolean.FALSE.equals(newResultRdbmsDiagnObj.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, newResultRdbmsDiagnObj.getErrorMessageCode(), newResultRdbmsDiagnObj.getErrorMessageVariables());
        }
        SampleAudit smpAudit = new SampleAudit();
        smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_REMOVED, sampleLevel + TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                testId, sampleId, testId, null, remFlds, remFldsV);

        return new InternalMessage(LPPlatform.LAB_TRUE, SampleAPIactionsEndpoints.SAMPLEANALYSISREMOVE, new Object[]{sampleData[0][2].toString(), sampleId}, testId);
    }

    /**
     *
     * @param sampleId
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public static InternalMessage sampleAnalysisAddtoSample(Integer sampleId, String[] fieldName, Object[] fieldValue) {
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] mandatoryFields = null;
        Object[] mandatoryFieldsValue = null;
        DataDataIntegrity labIntChecker = new DataDataIntegrity();

        String tableName = TblsData.TablesData.SAMPLE_ANALYSIS.getTableName();
        String actionName = "Insert";
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        String sampleLevel = TblsData.TablesData.SAMPLE.getTableName();
        mandatoryFields = labIntChecker.getTableMandatoryFields(sampleLevel + tableName, actionName);
        InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(fieldName, fieldValue);
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic()))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, fieldNameValueArrayChecker.getMessageCodeObj(), fieldNameValueArrayChecker.getMessageCodeVariables());
        }
        mandatoryFieldsValue = new Object[mandatoryFields.length];
        StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);
        for (Integer inumLines = 0; inumLines < mandatoryFields.length; inumLines++) {
            String currField = mandatoryFields[inumLines];
            boolean contains = Arrays.asList(fieldName).contains(currField.toLowerCase());
            if (Boolean.FALSE.equals(contains)) {
                if (mandatoryFieldsMissingBuilder.length() > 0) {
                    mandatoryFieldsMissingBuilder.append(",");
                }

                mandatoryFieldsMissingBuilder.append(currField);
            } else {
                Integer valuePosic = Arrays.asList(fieldName).indexOf(currField);
                mandatoryFieldsValue[inumLines] = fieldValue[valuePosic];
            }
        }
        if (mandatoryFieldsMissingBuilder.length() > 0) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS,
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
        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId},
                new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.STATUS.getName(), TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleData[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_NOT_FOUND, new Object[]{sampleId, schemaDataName});
        }
        String sampleCode=LPNulls.replaceNull(sampleData[0][2]).toString();
        Integer sampleCodeVersion=Integer.valueOf(LPNulls.replaceNull(sampleData[0][3]).toString());        
        Object[][] sampleRules = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.SAMPLE_RULES.getTableName(),
            new String[]{TblsCnfg.SampleRules.CODE.getName(), TblsCnfg.SampleRules.CODE_VERSION.getName()},
            new Object[]{sampleCode, sampleCodeVersion}, new String[]{TblsCnfg.SampleRules.ALLOW_SAMPLE_ANALYSIS_MORE_THAN_ONE.getName(), 
                TblsCnfg.SampleRules.CODE.getName(), TblsCnfg.SampleRules.CODE_VERSION.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleRules[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SPECRULE_NOTFOUND, new Object[]{sampleCode, sampleCodeVersion, schemaDataName});
        }
        String analysisName=LPNulls.replaceNull(fieldValue[LPArray.valuePosicInArray(fieldName, "analysis")]).toString(); 
        if (Boolean.FALSE.equals(sampleRules[0][0])){
            Object[] existsRecord = Rdbms.existsRecord(TblsData.TablesData.SAMPLE_ANALYSIS, 
                new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.ANALYSIS.getName()},
                new Object[]{sampleId, analysisName}, null);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())){
                return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SAMPLE_ANALYSIS_ALREADY_PRESENT, new Object[]{sampleId, analysisName, schemaDataName});                 
            }
        }        
        String sampleSpecCode = "";
        Integer sampleSpecCodeVersion = null;
        String sampleSpecVariationName = "";
        String specAnalysisTestingGroup = "";
        Object[][] sampleSpecData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId},
                new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.SPEC_CODE.getName(), TblsData.Sample.SPEC_CODE_VERSION.getName(),
                    TblsData.Sample.SPEC_VARIATION_NAME.getName(), TblsData.Sample.STATUS.getName()});
        if ((sampleSpecData[0][0] == null) || (!LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleSpecData[0][0].toString()))) {
            sampleSpecCode = (String) sampleSpecData[0][1];
            sampleSpecCodeVersion = Integer.valueOf(LPNulls.replaceNull(sampleSpecData[0][2]).toString());
            sampleSpecVariationName = (String) sampleSpecData[0][3];
            if (sampleSpecCode != null) {
                Object[][] specRules = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.SPEC_RULES.getTableName(),
                        new String[]{TblsCnfg.SpecRules.CODE.getName(), TblsCnfg.SpecRules.CONFIG_VERSION.getName()},
                        new Object[]{sampleSpecCode, sampleSpecCodeVersion}, new String[]{TblsCnfg.SpecRules.ALLOW_OTHER_ANALYSIS.getName(), TblsCnfg.SpecRules.ALLOW_MULTI_SPEC.getName(),
                            TblsCnfg.SpecRules.CODE.getName(), TblsCnfg.SpecRules.CONFIG_VERSION.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(specRules[0][0].toString())) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SPECRULE_NOTFOUND, new Object[]{sampleSpecCode, sampleSpecCodeVersion, schemaDataName});
                }
                //if (Boolean.FALSE.equals(Boolean.valueOf(specRules[0][0].toString()))) {
                    String[] specAnalysisFieldName = new String[]{TblsCnfg.SpecLimits.ANALYSIS.getName(), TblsCnfg.SpecLimits.METHOD_NAME.getName()}; //, TblsCnfg.SpecLimits.METHOD_VERSION.getName()
                    Object[] specAnalysisFieldValue = new Object[0];
                    Boolean nullValues = false;
                    String[] nullValuesArr = new String[]{};
                    for (String iFieldN : specAnalysisFieldName) {
                        specialFieldIndex = Arrays.asList(fieldName).indexOf(iFieldN);
                        if (specialFieldIndex == -1) {
                            specAnalysisFieldValue = LPArray.addValueToArray1D(specAnalysisFieldValue, "is null");
                            nullValuesArr = LPArray.addValueToArray1D(nullValuesArr, iFieldN);
                            nullValues = true;
                        } else {
                            specAnalysisFieldValue = LPArray.addValueToArray1D(specAnalysisFieldValue, fieldValue[specialFieldIndex]);
                        }
                    }
                    if (nullValues) {
                        return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SPECRULE_NOTFOUND, nullValuesArr);
                    }
                    specAnalysisFieldName = LPArray.addValueToArray1D(specAnalysisFieldName,
                            new String[]{TblsCnfg.SpecLimits.CODE.getName(), TblsCnfg.SpecLimits.CONFIG_VERSION.getName()});
                    specAnalysisFieldValue = LPArray.addValueToArray1D(specAnalysisFieldValue,
                            new Object[]{sampleSpecCode, sampleSpecCodeVersion});
                    SqlWhere sW = new SqlWhere(TblsCnfg.TablesConfig.SPEC_LIMITS, specAnalysisFieldName, specAnalysisFieldValue);

                    if (Boolean.FALSE.equals("ALL".equalsIgnoreCase(sampleSpecVariationName))) {
                        sW.addConstraint(TblsCnfg.SpecLimits.VARIATION_NAME, SqlStatement.WHERECLAUSE_TYPES.IN, sampleSpecVariationName.split("\\|"), "|");
//                        specAnalysisFieldName = LPArray.addValueToArray1D(specAnalysisFieldName, TblsCnfg.SpecLimits.VARIATION_NAME.getName());
//                        specAnalysisFieldValue = LPArray.addValueToArray1D(specAnalysisFieldValue, sampleSpecVariationName);
                    }
                    Object[] isReviewByTestingGroupEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleRevisionTestingGroup.DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP.getTagName());
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString())) {
                        Object[][] analysisTestingGroup = Rdbms.getRecordFieldsByFilter(procInstanceName, procInstanceName, TblsCnfg.TablesConfig.SPEC_LIMITS, sW,
                                EnumIntTableFields.getTableFieldsFromString(TblsCnfg.TablesConfig.SPEC_LIMITS, new String[]{TblsCnfg.SpecLimits.TESTING_GROUP.getName()}), new String[]{}, Boolean.FALSE);

//                        Object[][] analysisTestingGroup = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName(),
//                                sW,
//                                new String[]{TblsCnfg.SpecLimits.TESTING_GROUP.getName()});
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisTestingGroup[0][0].toString())) {
                            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.SPECLIMIT_NOTFOUND, new Object[]{sampleSpecCode, Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(specAnalysisFieldName, specAnalysisFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR)), schemaDataName});
                        }
                        specAnalysisTestingGroup = analysisTestingGroup[0][0].toString();
                    }
                //}
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
                return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS,
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
                return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS,
                        new String[]{fieldNeed, Arrays.toString(mandatoryFields), schemaDataName});
            }
            value = fieldValue[specialFieldIndex];
        } else {
            value = mandatoryFieldsValue[specialFieldIndex];
        }
        /*
        whereResultFieldValue = LPArray.addValueToArray1D(whereResultFieldValue, value);
        fieldNeed = TblsCnfg.AnalysisMethodParams.METHOD_VERSION.getName();
        whereResultFieldName = LPArray.addValueToArray1D(whereResultFieldName, fieldNeed);
        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(fieldNeed);
        if (specialFieldIndex == -1) {
            specialFieldIndex = Arrays.asList(fieldName).indexOf(fieldNeed);
            if (specialFieldIndex == -1) {
                return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS,
                        new String[]{fieldNeed, Arrays.toString(mandatoryFields), schemaDataName});
            }
            value = fieldValue[specialFieldIndex];
        } else {
            value = mandatoryFieldsValue[specialFieldIndex];
        }*/
        whereResultFieldValue = LPArray.addValueToArray1D(whereResultFieldValue, value);
        String[] getResultFieldsBeforeEach = new String[]{TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName(), TblsCnfg.AnalysisMethodParams.MANDATORY.getName(), TblsCnfg.AnalysisMethodParams.ANALYSIS.getName(),
            TblsCnfg.AnalysisMethodParams.PARAM_TYPE.getName(), TblsCnfg.AnalysisMethodParams.NUM_REPLICAS.getName(), TblsCnfg.AnalysisMethodParams.UOM.getName(), TblsCnfg.AnalysisMethodParams.UOM_CONVERSION_MODE.getName(), TblsCnfg.AnalysisMethodParams.CALC_LINKED.getName(), TblsCnfg.AnalysisMethodParams.LIST_ENTRY.getName()};
        Object[][] resultFieldRecords = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS.getTableName(),
                whereResultFieldName, whereResultFieldValue, getResultFieldsBeforeEach, new String[]{TblsCnfg.AnalysisMethodParams.ORDER_NUMBER.getName(), TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultFieldRecords[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ANALYSISMETHOD_PARAMSNOTFOUND, new Object[]{Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(whereResultFieldName, whereResultFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR)), schemaDataName});
        }
        resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, sampleId);
        getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.SAMPLE_ID.getName());
        resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, 0);
        getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.TEST_ID.getName());
        // This is temporary !!!! ***************************************************************
        specialFieldIndex = Arrays.asList(getResultFieldsBeforeEach).indexOf(TblsData.SampleAnalysisResult.STATUS.getName());
        String firstStatusSampleAnalysisResult = SampleAnalysisResultStatuses.getStatusFirstCode();
        if (specialFieldIndex == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, firstStatusSampleAnalysisResult);
            getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.STATUS.getName());
        }
        // This is temporary !!!! ***************************************************************
        String[] resultMandatoryFields = labIntChecker.getTableMandatoryFields(sampleLevel, actionName);
        String[] resultDefaulFields = labIntChecker.getTableFieldsDefaulValues(tableName, actionName);
        Object[] resultDefaulFieldValue = labIntChecker.getTableFieldsDefaulValues(tableName, actionName);
        Object[] resultMandatoryFieldsValue = new Object[resultMandatoryFields.length];
        StringBuilder resultMandatoryFieldsMissingBuilder = new StringBuilder(0);
        for (Integer inumLines = 0; inumLines < resultMandatoryFieldsValue.length; inumLines++) {
            String currField = resultMandatoryFields[inumLines];
            boolean contains = Arrays.asList(getResultFieldsBeforeEach).contains(currField.toLowerCase());
            if (Boolean.FALSE.equals(contains)) {
                Integer valuePosic = Arrays.asList(resultDefaulFields).indexOf(currField.toLowerCase());
                if (valuePosic == -1) {
                    if (resultMandatoryFieldsMissingBuilder.length() > 0) {
                        resultMandatoryFieldsMissingBuilder.append(",");
                    }

                    resultMandatoryFieldsMissingBuilder.append(currField);
                } else {
                    Object currFieldValue = resultDefaulFieldValue[valuePosic];
                    resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, currFieldValue);
                    getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, currField);
                }
            }
        }
        if (resultMandatoryFieldsMissingBuilder.length() > 0) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{resultMandatoryFieldsMissingBuilder, schemaDataName});
        }
        fieldName = LPArray.addValueToArray1D(fieldName, new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.ADDED_ON.getName(), TblsData.SampleAnalysis.ADDED_BY.getName()});
        fieldValue = LPArray.addValueToArray1D(fieldValue, new Object[]{sampleId, Rdbms.getCurrentDate(), token.getUserName()});
        Object[] isReviewByTestingGroupEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleRevisionTestingGroup.DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP.getTagName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString())
                && Boolean.FALSE.equals(LPArray.valueInArray(fieldName, TblsData.SampleAnalysis.TESTING_GROUP.getName()))) {
            fieldName = LPArray.addValueToArray1D(fieldName, TblsData.SampleAnalysis.TESTING_GROUP.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, specAnalysisTestingGroup);
        }
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsData.TablesData.SAMPLE_ANALYSIS, fieldName, fieldValue);
        Integer testId = Integer.parseInt(insertRecordInTable.getNewRowId().toString());
        SampleAudit smpAudit = new SampleAudit();
        smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_ADDED, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                testId, sampleId, testId, null, fieldName, fieldValue);
        Integer valuePosic = Arrays.asList(getResultFieldsBeforeEach).indexOf(TblsData.SampleAnalysisResult.TEST_ID.getName());
        if (valuePosic == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, testId);
            getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.TEST_ID.getName());
        } else {
            resultFieldRecords = LPArray.setColumnValueToArray2D(resultFieldRecords, valuePosic, testId);
        }
        valuePosic = Arrays.asList(getResultFieldsBeforeEach).indexOf(TblsData.SampleAnalysisResult.METHOD_NAME.getName());
        if (valuePosic == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.METHOD_NAME.getName())]);
            getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.METHOD_NAME.getName());
        }
        /*
        valuePosic = Arrays.asList(getResultFieldsBeforeEach).indexOf(TblsData.SampleAnalysisResult.METHOD_VERSION.getName());
        if (valuePosic == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.METHOD_VERSION.getName())]);
            getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.METHOD_VERSION.getName());
        }*/
        for (Object[] resultFieldRecord : resultFieldRecords) {
            Object[] fieldVal = new Object[0];
            String[] getResultFields = getResultFieldsBeforeEach;
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
                numReplicas = Integer.valueOf(LPNulls.replaceNull(fieldVal[valuePosic].toString()));
                getResultFields[valuePosic] = resultReplicaFieldName;
                if ((numReplicas == null) || (numReplicas == 0)) {
                    numReplicas = 1;
                    fieldVal[valuePosic] = 1;
                }
            }
//            String[] getResultFieldsWithLimits=getResultFields;
//            Object[] fieldValWithLimits=fieldVal;
            if (sampleSpecCode.length() > 0) {
                Object[][] specLimits = ConfigSpecRule.getSpecLimitLimitIdFromSpecVariables(sampleSpecCode, sampleSpecCodeVersion,
                        sampleSpecVariationName,
                        fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.ANALYSIS.getName())].toString(),
                        fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.METHOD_NAME.getName())].toString(),
                        //Integer.valueOf(fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.METHOD_VERSION.getName())].toString()),
                        fieldVal[Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.PARAM_NAME.getName())].toString(),
                        new String[]{TblsCnfg.SpecLimits.LIMIT_ID.getName(), TblsCnfg.SpecLimits.MAX_DP.getName(), TblsCnfg.SpecLimits.LIST_ENTRY.getName()});
                Integer fldPosic = -1;
                if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimits[0][0].toString()))) {
                    fldPosic = LPArray.valuePosicInArray(getResultFields, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
                    if (fldPosic == -1) {
//                        getResultFieldsWithLimits = LPArray.addValueToArray1D(getResultFieldsWithLimits, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
//                        fieldValWithLimits = LPArray.addValueToArray1D(fieldValWithLimits, specLimits[0][0]);
                        getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
                        fieldVal = LPArray.addValueToArray1D(fieldVal, specLimits[0][0]);
                    } else {
//                        fieldValWithLimits[fldPosic]=specLimits[0][0];
                        fieldVal[fldPosic] = specLimits[0][0];
                    }
                    if (specLimits[0][1] != null && specLimits[0][1].toString().length() > 0) {
                        fldPosic = LPArray.valuePosicInArray(getResultFields, TblsData.SampleAnalysisResult.MAX_DP.getName());
                        if (fldPosic == -1) {
//                            getResultFieldsWithLimits = LPArray.addValueToArray1D(getResultFieldsWithLimits, TblsData.SampleAnalysisResult.MAX_DP.getName());
//                            fieldValWithLimits = LPArray.addValueToArray1D(fieldValWithLimits, specLimits[0][1]);
                            getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.MAX_DP.getName());
                            fieldVal = LPArray.addValueToArray1D(fieldVal, specLimits[0][1]);
                        } else {
//                            fieldValWithLimits[fldPosic]=specLimits[0][1];
                            fieldVal[fldPosic] = specLimits[0][1];
                        }
                    }
                    if (specLimits[0][2] != null && specLimits[0][2].toString().length() > 0) {
                        fldPosic = LPArray.valuePosicInArray(getResultFields, TblsData.SampleAnalysisResult.LIST_ENTRY.getName());
                        if (fldPosic == -1) {
//                            getResultFieldsWithLimits = LPArray.addValueToArray1D(getResultFieldsWithLimits, TblsData.SampleAnalysisResult.LIST_ENTRY.getName());
//                            fieldValWithLimits = LPArray.addValueToArray1D(fieldValWithLimits, specLimits[0][2]);
                            getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.LIST_ENTRY.getName());
                            fieldVal = LPArray.addValueToArray1D(fieldVal, specLimits[0][2]);
                        } else {
//                            fieldValWithLimits[fldPosic]=specLimits[0][2];
                            fieldVal[fldPosic] = specLimits[0][2];
                        }
                    }
                }
            }
            for (Integer iNumReps = 1; iNumReps <= numReplicas; iNumReps++) {
                fieldVal[valuePosic] = iNumReps;
                Integer statusFieldPosic = LPArray.valuePosicInArray(getResultFields, TblsData.SampleAnalysisResult.STATUS.getName());
                if (statusFieldPosic == -1) {
//                    getResultFieldsWithLimits = LPArray.addValueToArray1D(getResultFieldsWithLimits, TblsData.SampleAnalysisResult.STATUS.getName());
//                    fieldValWithLimits = LPArray.addValueToArray1D(fieldValWithLimits, firstStatusSampleAnalysisResult);
                    getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.STATUS.getName());
                    fieldVal = LPArray.addValueToArray1D(fieldVal, firstStatusSampleAnalysisResult);
                } else {
                    fieldVal[statusFieldPosic] = firstStatusSampleAnalysisResult;
                }
                RdbmsObject newResultRdbmsDiagnObj = Rdbms.insertRecordInTable(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                        getResultFields, fieldVal);
                if (Boolean.FALSE.equals(newResultRdbmsDiagnObj.getRunSuccess())) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, newResultRdbmsDiagnObj.getErrorMessageCode(), newResultRdbmsDiagnObj.getErrorMessageVariables());
                }
                Integer resultId = Integer.parseInt(newResultRdbmsDiagnObj.getNewRowId().toString());
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ADDED, sampleLevel + TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                        resultId, sampleId, testId, resultId, getResultFields, fieldVal);
                Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY.getTableName());
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())) {
                    getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResultSecondEntry.FIRST_RESULT_ID.getName());
                    fieldVal = LPArray.addValueToArray1D(fieldVal, resultId);
                    newResultRdbmsDiagnObj = Rdbms.insertRecordInTable(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY,
                            getResultFields, fieldVal);
                    Integer resultIdSecondEntry = Integer.parseInt(newResultRdbmsDiagnObj.getNewRowId().toString());
                    smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_SECONDENTRY_ADDED, sampleLevel + TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                            resultId, sampleId, testId, resultIdSecondEntry, getResultFields, fieldVal);
                }
            }
        }
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString())) {
            InternalMessage addSampleRevisionByTestingGroup = DataSampleRevisionTestingGroup.addSampleRevisionByTestingGroup(sampleId, testId, specAnalysisTestingGroup);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(addSampleRevisionByTestingGroup.getDiagnostic())) {
                return addSampleRevisionByTestingGroup;
            }
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.SAMPLE_ANALYSIS_ADDED_SUCCESS, new Object[]{"", testId, schemaDataName});
    }

    public static InternalMessage sampleAnalysisAddtoSampleNoSpecCheck(Integer sampleId, String[] fieldName, Object[] fieldValue,
            String sampleSpecCode, Integer sampleSpecCodeVersion, String sampleSpecVariationName, String specAnalysisTestingGroup) {
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] mandatoryFields = null;
        Object[] mandatoryFieldsValue = null;
        DataDataIntegrity labIntChecker = new DataDataIntegrity();

        String tableName = TblsData.TablesData.SAMPLE_ANALYSIS.getTableName();
        String actionName = "Insert";
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        String sampleLevel = TblsData.TablesData.SAMPLE.getTableName();
        mandatoryFields = labIntChecker.getTableMandatoryFields(sampleLevel + tableName, actionName);
        InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(fieldName, fieldValue);
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic()))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, fieldNameValueArrayChecker.getMessageCodeObj(), fieldNameValueArrayChecker.getMessageCodeVariables());
        }
        mandatoryFieldsValue = new Object[mandatoryFields.length];
        StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);
        for (Integer inumLines = 0; inumLines < mandatoryFields.length; inumLines++) {
            String currField = mandatoryFields[inumLines];
            boolean contains = Arrays.asList(fieldName).contains(currField.toLowerCase());
            if (Boolean.FALSE.equals(contains)) {
                if (mandatoryFieldsMissingBuilder.length() > 0) {
                    mandatoryFieldsMissingBuilder.append(",");
                }

                mandatoryFieldsMissingBuilder.append(currField);
            } else {
                Integer valuePosic = Arrays.asList(fieldName).indexOf(currField);
                mandatoryFieldsValue[inumLines] = fieldValue[valuePosic];
            }
        }
        if (mandatoryFieldsMissingBuilder.length() > 0) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS,
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
        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId},
                new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.STATUS.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleData[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_NOT_FOUND, new Object[]{sampleId, schemaDataName});
        }
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
                return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS,
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
                return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS,
                        new String[]{fieldNeed, Arrays.toString(mandatoryFields), schemaDataName});
            }
            value = fieldValue[specialFieldIndex];
        } else {
            value = mandatoryFieldsValue[specialFieldIndex];
        }
        /*
        whereResultFieldValue = LPArray.addValueToArray1D(whereResultFieldValue, value);
        fieldNeed = TblsCnfg.AnalysisMethodParams.METHOD_VERSION.getName();
        whereResultFieldName = LPArray.addValueToArray1D(whereResultFieldName, fieldNeed);
        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(fieldNeed);
        if (specialFieldIndex == -1) {
            specialFieldIndex = Arrays.asList(fieldName).indexOf(fieldNeed);
            if (specialFieldIndex == -1) {
                return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS,
                        new String[]{fieldNeed, Arrays.toString(mandatoryFields), schemaDataName});
            }
            value = fieldValue[specialFieldIndex];
        } else {
            value = mandatoryFieldsValue[specialFieldIndex];
        }
        */
        whereResultFieldValue = LPArray.addValueToArray1D(whereResultFieldValue, value);
        String[] getResultFieldsBeforeEach = new String[]{TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName(), TblsCnfg.AnalysisMethodParams.MANDATORY.getName(), TblsCnfg.AnalysisMethodParams.ANALYSIS.getName(),
            TblsCnfg.AnalysisMethodParams.PARAM_TYPE.getName(), TblsCnfg.AnalysisMethodParams.NUM_REPLICAS.getName(), TblsCnfg.AnalysisMethodParams.UOM.getName(), TblsCnfg.AnalysisMethodParams.UOM_CONVERSION_MODE.getName(), TblsCnfg.AnalysisMethodParams.CALC_LINKED.getName(), TblsCnfg.AnalysisMethodParams.LIST_ENTRY.getName()};
        Object[][] resultFieldRecords = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS.getTableName(),
                whereResultFieldName, whereResultFieldValue, getResultFieldsBeforeEach, new String[]{TblsCnfg.AnalysisMethodParams.ORDER_NUMBER.getName(), TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultFieldRecords[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ANALYSISMETHOD_PARAMSNOTFOUND, new Object[]{Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(whereResultFieldName, whereResultFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR)), schemaDataName});
        }
        resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, sampleId);
        getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.SAMPLE_ID.getName());
        resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, 0);
        getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.TEST_ID.getName());
        // This is temporary !!!! ***************************************************************
        specialFieldIndex = Arrays.asList(getResultFieldsBeforeEach).indexOf(TblsData.SampleAnalysisResult.STATUS.getName());
        String firstStatusSampleAnalysisResult = SampleAnalysisResultStatuses.getStatusFirstCode();
        if (specialFieldIndex == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, firstStatusSampleAnalysisResult);
            getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.STATUS.getName());
        }
        // This is temporary !!!! ***************************************************************
        String[] resultMandatoryFields = labIntChecker.getTableMandatoryFields(sampleLevel, actionName);
        String[] resultDefaulFields = labIntChecker.getTableFieldsDefaulValues(tableName, actionName);
        Object[] resultDefaulFieldValue = labIntChecker.getTableFieldsDefaulValues(tableName, actionName);
        Object[] resultMandatoryFieldsValue = new Object[resultMandatoryFields.length];
        StringBuilder resultMandatoryFieldsMissingBuilder = new StringBuilder(0);
        for (Integer inumLines = 0; inumLines < resultMandatoryFieldsValue.length; inumLines++) {
            String currField = resultMandatoryFields[inumLines];
            boolean contains = Arrays.asList(getResultFieldsBeforeEach).contains(currField.toLowerCase());
            if (Boolean.FALSE.equals(contains)) {
                Integer valuePosic = Arrays.asList(resultDefaulFields).indexOf(currField.toLowerCase());
                if (valuePosic == -1) {
                    if (resultMandatoryFieldsMissingBuilder.length() > 0) {
                        resultMandatoryFieldsMissingBuilder.append(",");
                    }

                    resultMandatoryFieldsMissingBuilder.append(currField);
                } else {
                    Object currFieldValue = resultDefaulFieldValue[valuePosic];
                    resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, currFieldValue);
                    getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, currField);
                }
            }
        }
        if (resultMandatoryFieldsMissingBuilder.length() > 0) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{resultMandatoryFieldsMissingBuilder, schemaDataName});
        }
        fieldName = LPArray.addValueToArray1D(fieldName, new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.ADDED_ON.getName(), TblsData.SampleAnalysis.ADDED_BY.getName()});
        fieldValue = LPArray.addValueToArray1D(fieldValue, new Object[]{sampleId, Rdbms.getCurrentDate(), token.getUserName()});
        Object[] isReviewByTestingGroupEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleRevisionTestingGroup.DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP.getTagName());
        /*        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString())
                && Boolean.FALSE.equals(LPArray.valueInArray(fieldName, TblsData.SampleAnalysis.TESTING_GROUP.getName()))) {
            fieldName = LPArray.addValueToArray1D(fieldName, TblsData.SampleAnalysis.TESTING_GROUP.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, specAnalysisTestingGroup);
        }*/
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsData.TablesData.SAMPLE_ANALYSIS, fieldName, fieldValue);
        Integer testId = Integer.parseInt(insertRecordInTable.getNewRowId().toString());
        SampleAudit smpAudit = new SampleAudit();
        smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_ADDED, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                testId, sampleId, testId, null, fieldName, fieldValue);
        Integer valuePosic = Arrays.asList(getResultFieldsBeforeEach).indexOf(TblsData.SampleAnalysisResult.TEST_ID.getName());
        if (valuePosic == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, testId);
            getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.TEST_ID.getName());
        } else {
            resultFieldRecords = LPArray.setColumnValueToArray2D(resultFieldRecords, valuePosic, testId);
        }
        valuePosic = Arrays.asList(getResultFieldsBeforeEach).indexOf(TblsData.SampleAnalysisResult.METHOD_NAME.getName());
        if (valuePosic == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.METHOD_NAME.getName())]);
            getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.METHOD_NAME.getName());
        }
        /*
        valuePosic = Arrays.asList(getResultFieldsBeforeEach).indexOf(TblsData.SampleAnalysisResult.METHOD_VERSION.getName());
        if (valuePosic == -1) {
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.METHOD_VERSION.getName())]);
            getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.METHOD_VERSION.getName());
        }*/
        for (Object[] resultFieldRecord : resultFieldRecords) {
            Object[] fieldVal = new Object[0];
            String[] getResultFields = getResultFieldsBeforeEach;
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
                numReplicas = Integer.valueOf(LPNulls.replaceNull(fieldVal[valuePosic].toString()));
                getResultFields[valuePosic] = resultReplicaFieldName;
                if ((numReplicas == null) || (numReplicas == 0)) {
                    numReplicas = 1;
                    fieldVal[valuePosic] = 1;
                }
            }
//            String[] getResultFieldsWithLimits=getResultFields;
//            Object[] fieldValWithLimits=fieldVal;
            if (sampleSpecCode.length() > 0) {
                Object[][] specLimits = ConfigSpecRule.getSpecLimitLimitIdFromSpecVariables(sampleSpecCode, sampleSpecCodeVersion,
                        sampleSpecVariationName,
                        fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.ANALYSIS.getName())].toString(),
                        fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.METHOD_NAME.getName())].toString(),
                        //Integer.valueOf(fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.METHOD_VERSION.getName())].toString()),
                        fieldVal[Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.PARAM_NAME.getName())].toString(),
                        new String[]{TblsCnfg.SpecLimits.LIMIT_ID.getName(), TblsCnfg.SpecLimits.MAX_DP.getName(), TblsCnfg.SpecLimits.LIST_ENTRY.getName()});
                Integer fldPosic = -1;
                if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimits[0][0].toString()))) {
                    fldPosic = LPArray.valuePosicInArray(getResultFields, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
                    if (fldPosic == -1) {
//                        getResultFieldsWithLimits = LPArray.addValueToArray1D(getResultFieldsWithLimits, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
//                        fieldValWithLimits = LPArray.addValueToArray1D(fieldValWithLimits, specLimits[0][0]);
                        getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
                        fieldVal = LPArray.addValueToArray1D(fieldVal, specLimits[0][0]);
                    } else {
//                        fieldValWithLimits[fldPosic]=specLimits[0][0];
                        fieldVal[fldPosic] = specLimits[0][0];
                    }
                    if (specLimits[0][1] != null && specLimits[0][1].toString().length() > 0) {
                        fldPosic = LPArray.valuePosicInArray(getResultFields, TblsData.SampleAnalysisResult.MAX_DP.getName());
                        if (fldPosic == -1) {
//                            getResultFieldsWithLimits = LPArray.addValueToArray1D(getResultFieldsWithLimits, TblsData.SampleAnalysisResult.MAX_DP.getName());
//                            fieldValWithLimits = LPArray.addValueToArray1D(fieldValWithLimits, specLimits[0][1]);
                            getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.MAX_DP.getName());
                            fieldVal = LPArray.addValueToArray1D(fieldVal, specLimits[0][1]);
                        } else {
//                            fieldValWithLimits[fldPosic]=specLimits[0][1];
                            fieldVal[fldPosic] = specLimits[0][1];
                        }
                    }
                    if (specLimits[0][2] != null && specLimits[0][2].toString().length() > 0) {
                        fldPosic = LPArray.valuePosicInArray(getResultFields, TblsData.SampleAnalysisResult.LIST_ENTRY.getName());
                        if (fldPosic == -1) {
//                            getResultFieldsWithLimits = LPArray.addValueToArray1D(getResultFieldsWithLimits, TblsData.SampleAnalysisResult.LIST_ENTRY.getName());
//                            fieldValWithLimits = LPArray.addValueToArray1D(fieldValWithLimits, specLimits[0][2]);
                            getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.LIST_ENTRY.getName());
                            fieldVal = LPArray.addValueToArray1D(fieldVal, specLimits[0][2]);
                        } else {
//                            fieldValWithLimits[fldPosic]=specLimits[0][2];
                            fieldVal[fldPosic] = specLimits[0][2];
                        }
                    }
                }
            }
            for (Integer iNumReps = 1; iNumReps <= numReplicas; iNumReps++) {
                fieldVal[valuePosic] = iNumReps;
                Integer statusFieldPosic = LPArray.valuePosicInArray(getResultFields, TblsData.SampleAnalysisResult.STATUS.getName());
                if (statusFieldPosic == -1) {
//                    getResultFieldsWithLimits = LPArray.addValueToArray1D(getResultFieldsWithLimits, TblsData.SampleAnalysisResult.STATUS.getName());
//                    fieldValWithLimits = LPArray.addValueToArray1D(fieldValWithLimits, firstStatusSampleAnalysisResult);
                    getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.STATUS.getName());
                    fieldVal = LPArray.addValueToArray1D(fieldVal, firstStatusSampleAnalysisResult);
                } else {
                    fieldVal[statusFieldPosic] = firstStatusSampleAnalysisResult;
                }
                RdbmsObject newResultRdbmsDiagnObj = Rdbms.insertRecordInTable(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                        getResultFields, fieldVal);
                if (Boolean.FALSE.equals(newResultRdbmsDiagnObj.getRunSuccess())) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, newResultRdbmsDiagnObj.getErrorMessageCode(), newResultRdbmsDiagnObj.getErrorMessageVariables());
                }
                Integer resultId = Integer.parseInt(newResultRdbmsDiagnObj.getNewRowId().toString());
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ADDED, sampleLevel + TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                        resultId, sampleId, testId, resultId, getResultFields, fieldVal);
                Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY.getTableName());
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())) {
                    getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResultSecondEntry.FIRST_RESULT_ID.getName());
                    fieldVal = LPArray.addValueToArray1D(fieldVal, resultId);
                    newResultRdbmsDiagnObj = Rdbms.insertRecordInTable(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY,
                            getResultFields, fieldVal);
                    Integer resultIdSecondEntry = Integer.parseInt(newResultRdbmsDiagnObj.getNewRowId().toString());
                    smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_SECONDENTRY_ADDED, sampleLevel + TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                            resultId, sampleId, testId, resultIdSecondEntry, getResultFields, fieldVal);
                }
            }
        }
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString())) {
            InternalMessage addSampleRevisionByTestingGroup = DataSampleRevisionTestingGroup.addSampleRevisionByTestingGroup(sampleId, testId, specAnalysisTestingGroup);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(addSampleRevisionByTestingGroup.getDiagnostic())) {
                return addSampleRevisionByTestingGroup;
            }
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.SAMPLE_ANALYSIS_ADDED_SUCCESS, new Object[]{"", testId, schemaDataName});
    }

    public static InternalMessage isAllsampleAnalysisReviewed(Integer sampleId, String[] whereFieldName, Object[] whereFieldValue) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String sampleAnalysisStatusReviewed = SampleAnalysisStatuses.REVIEWED.getStatusCode("");
        if (whereFieldName == null) {
            whereFieldName = new String[0];
        }
        if (whereFieldValue == null) {
            whereFieldValue = new String[0];
        }
        Object[] isRevisionSampleAnalysisRequired = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleAnalysisBusinessRules.REVISION_SAMPLEANALYSIS_REQUIRED.getAreaName(), DataSampleAnalysisBusinessRules.REVISION_SAMPLEANALYSIS_REQUIRED.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isRevisionSampleAnalysisRequired[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
        }
        whereFieldName = LPArray.addValueToArray1D(whereFieldName, TblsData.SampleAnalysis.SAMPLE_ID.getName());
        whereFieldValue = LPArray.addValueToArray1D(whereFieldValue, sampleId);
        Object[][] grouper = Rdbms.getGrouper(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                new String[]{TblsData.SampleAnalysis.STATUS.getName()}, whereFieldName, whereFieldValue, null);
        if (grouper.length != 1) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.PENDING_REVISION, null);
        }
        if (Boolean.FALSE.equals(grouper[0][0].toString().equalsIgnoreCase(sampleAnalysisStatusReviewed))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.PENDING_REVISION, null);
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
    }

    public static InternalMessage addSampleAnalysisWithResults(Integer sampleId, String[] fieldName, Object[] fieldValue, String specCode, Integer specCodeVersion, String paramName, Integer specLimitId, String specAnalysisTestingGroup) {
        try {
            Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
            String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
            String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
            DataDataIntegrity labIntChecker = new DataDataIntegrity();

            String tableName = TblsData.TablesData.SAMPLE_ANALYSIS.getTableName();
            String actionName = "Insert";
            String sampleLevel = TblsData.TablesData.SAMPLE.getTableName();
            String[] mandatoryFields = labIntChecker.getTableMandatoryFields(sampleLevel + tableName, actionName);

            Object value = null;

            String[] whereResultFieldName = new String[0];
            String[] fieldNeedArr = new String[]{TblsCnfg.AnalysisMethodParams.ANALYSIS.getName(), TblsCnfg.AnalysisMethodParams.METHOD_NAME.getName()};//                TblsCnfg.AnalysisMethodParams.METHOD_VERSION.getName()};
            Object[] whereResultFieldValue = new Object[0];
            Object[] mandatoryFieldsValue = new Object[fieldNeedArr.length];
            for (String fieldNeed : fieldNeedArr) {
                whereResultFieldName = LPArray.addValueToArray1D(whereResultFieldName, fieldNeed);
                Integer specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(fieldNeed);
                if (specialFieldIndex == -1) {
                    specialFieldIndex = Arrays.asList(fieldName).indexOf(fieldNeed);
                    if (specialFieldIndex == -1) {
                        return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS,
                                new String[]{fieldNeed, Arrays.toString(mandatoryFields), schemaDataName});
                    }
                    value = fieldValue[specialFieldIndex];
                    whereResultFieldValue = LPArray.addValueToArray1D(whereResultFieldValue, fieldValue[specialFieldIndex]);
                } else {
                    whereResultFieldValue = LPArray.addValueToArray1D(whereResultFieldValue, mandatoryFieldsValue[specialFieldIndex]);
                }
            }

            Integer numReplicas = 1;

            String[] getResultFieldsBeforeEach = new String[]{TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName(), TblsCnfg.AnalysisMethodParams.MANDATORY.getName(), TblsCnfg.AnalysisMethodParams.ANALYSIS.getName(),
                TblsCnfg.AnalysisMethodParams.PARAM_TYPE.getName(), TblsCnfg.AnalysisMethodParams.NUM_REPLICAS.getName(), TblsCnfg.AnalysisMethodParams.UOM.getName(), TblsCnfg.AnalysisMethodParams.UOM_CONVERSION_MODE.getName(), TblsCnfg.AnalysisMethodParams.CALC_LINKED.getName(), TblsCnfg.AnalysisMethodParams.LIST_ENTRY.getName()};
            Object[][] resultFieldRecords = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS.getTableName(),
                    whereResultFieldName, whereResultFieldValue, getResultFieldsBeforeEach, new String[]{TblsCnfg.AnalysisMethodParams.ORDER_NUMBER.getName(), TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultFieldRecords[0][0].toString())) {
                return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.ANALYSISMETHOD_PARAMSNOTFOUND, new Object[]{Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(whereResultFieldName, whereResultFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR)), schemaDataName});
            }
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, sampleId);
            getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.SAMPLE_ID.getName());
            resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, 0);
            getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.TEST_ID.getName());
            // This is temporary !!!! ***************************************************************
            Integer specialFieldIndex = Arrays.asList(getResultFieldsBeforeEach).indexOf(TblsData.SampleAnalysisResult.STATUS.getName());
            String firstStatusSampleAnalysisResult = SampleAnalysisResultStatuses.getStatusFirstCode();
            if (specialFieldIndex == -1) {
                resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, firstStatusSampleAnalysisResult);
                getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.STATUS.getName());
            }

            String[] resultMandatoryFields = labIntChecker.getTableMandatoryFields(sampleLevel, actionName);
            String[] resultDefaulFields = labIntChecker.getTableFieldsDefaulValues(tableName, actionName);
            Object[] resultDefaulFieldValue = labIntChecker.getTableFieldsDefaulValues(tableName, actionName);
            Object[] resultMandatoryFieldsValue = new Object[resultMandatoryFields.length];
            StringBuilder resultMandatoryFieldsMissingBuilder = new StringBuilder(0);
            for (Integer inumLines = 0; inumLines < resultMandatoryFieldsValue.length; inumLines++) {
                String currField = resultMandatoryFields[inumLines];
                boolean contains = Arrays.asList(getResultFieldsBeforeEach).contains(currField.toLowerCase());
                if (Boolean.FALSE.equals(contains)) {
                    Integer valuePosic = Arrays.asList(resultDefaulFields).indexOf(currField.toLowerCase());
                    if (valuePosic == -1) {
                        if (resultMandatoryFieldsMissingBuilder.length() > 0) {
                            resultMandatoryFieldsMissingBuilder.append(",");
                        }

                        resultMandatoryFieldsMissingBuilder.append(currField);
                    } else {
                        Object currFieldValue = resultDefaulFieldValue[valuePosic];
                        resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, currFieldValue);
                        getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, currField);
                    }
                }
            }

            if (resultMandatoryFieldsMissingBuilder.length() > 0) {
                return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{resultMandatoryFieldsMissingBuilder, schemaDataName});
            }
            fieldName = LPArray.addValueToArray1D(fieldName, new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.ADDED_ON.getName(), TblsData.SampleAnalysis.ADDED_BY.getName()});
            fieldValue = LPArray.addValueToArray1D(fieldValue, new Object[]{sampleId, Rdbms.getCurrentDate(), token.getUserName()});
            InternalMessage isReviewByTestingGroupEnable = LPPlatform.isProcedureBusinessRuleEnableInternalMessage(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleRevisionTestingGroup.DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP.getTagName());
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isReviewByTestingGroupEnable.getDiagnostic())
                    && Boolean.FALSE.equals(LPArray.valueInArray(fieldName, TblsData.SampleAnalysis.TESTING_GROUP.getName()))) {
                fieldName = LPArray.addValueToArray1D(fieldName, TblsData.SampleAnalysis.TESTING_GROUP.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, specAnalysisTestingGroup);
            }
            String firstStatus = SampleAnalysisStatuses.getStatusFirstCode();
            specialFieldIndex = Arrays.asList(fieldName).indexOf(TblsData.Sample.STATUS.getName());
            if (specialFieldIndex == -1) {
                fieldName = LPArray.addValueToArray1D(fieldName, TblsData.Sample.STATUS.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, firstStatus);
            } else {
                fieldValue[specialFieldIndex] = firstStatus;
            }
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsData.TablesData.SAMPLE_ANALYSIS, fieldName, fieldValue);
            Integer testId = Integer.parseInt(insertRecordInTable.getNewRowId().toString());
            SampleAudit smpAudit = new SampleAudit();
            smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_ADDED, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                    testId, sampleId, testId, null, fieldName, fieldValue);
            Integer valuePosic = Arrays.asList(getResultFieldsBeforeEach).indexOf(TblsData.SampleAnalysisResult.TEST_ID.getName());
            if (valuePosic == -1) {
                resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, testId);
                getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.TEST_ID.getName());
            } else {
                resultFieldRecords = LPArray.setColumnValueToArray2D(resultFieldRecords, valuePosic, testId);
            }
            valuePosic = Arrays.asList(getResultFieldsBeforeEach).indexOf(TblsData.SampleAnalysisResult.METHOD_NAME.getName());
            if (valuePosic == -1) {
                resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.METHOD_NAME.getName())]);
                getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.METHOD_NAME.getName());
            }
            /*
            valuePosic = Arrays.asList(getResultFieldsBeforeEach).indexOf(TblsData.SampleAnalysisResult.METHOD_VERSION.getName());
            if (valuePosic == -1) {
                resultFieldRecords = LPArray.addColumnToArray2D(resultFieldRecords, fieldValue[Arrays.asList(fieldName).indexOf(TblsData.SampleAnalysisResult.METHOD_VERSION.getName())]);
                getResultFieldsBeforeEach = LPArray.addValueToArray1D(getResultFieldsBeforeEach, TblsData.SampleAnalysisResult.METHOD_VERSION.getName());
            }
            */
            for (Object[] resultFieldRecord : resultFieldRecords) {
                Object[] fieldVal = new Object[0];
                String[] getResultFields = getResultFieldsBeforeEach;
                for (int col = 0; col < resultFieldRecords[0].length; col++) {
                    fieldVal = LPArray.addValueToArray1D(fieldVal, resultFieldRecord[col]);
                }
                valuePosic = Arrays.asList(getResultFields).indexOf(TblsCnfg.AnalysisMethodParams.NUM_REPLICAS.getName());
                numReplicas = 1;
                String resultReplicaFieldName = TblsData.SampleAnalysisResult.REPLICA.getName();
                if (valuePosic == -1) {
                    valuePosic = Arrays.asList(getResultFields).indexOf(TblsData.SampleAnalysisResult.REPLICA.getName());
                    if (valuePosic == -1) {
                        getResultFields = LPArray.addValueToArray1D(getResultFields, resultReplicaFieldName);
                        fieldVal = LPArray.addValueToArray1D(fieldVal, numReplicas);
                        valuePosic = fieldVal.length - 1;
                    }
                } else {
                    numReplicas = Integer.valueOf(LPNulls.replaceNull(fieldVal[valuePosic].toString()));
                    getResultFields[valuePosic] = resultReplicaFieldName;
                    if ((numReplicas == null) || (numReplicas == 0)) {
                        numReplicas = 1;
                        fieldVal[valuePosic] = 1;
                    }
                }
                for (Integer iNumReps = 1; iNumReps <= numReplicas; iNumReps++) {
                    fieldVal[valuePosic] = iNumReps;
                    Integer statusFieldPosic = LPArray.valuePosicInArray(getResultFields, TblsData.SampleAnalysisResult.STATUS.getName());
                    if (statusFieldPosic == -1) {
                        getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.STATUS.getName());
                        fieldVal = LPArray.addValueToArray1D(fieldVal, firstStatusSampleAnalysisResult);
                    } else {
                        fieldVal[statusFieldPosic] = firstStatusSampleAnalysisResult;
                    }
                    if (specLimitId != null
                            && paramName.equalsIgnoreCase(fieldVal[LPArray.valuePosicInArray(getResultFields, TblsData.SampleAnalysisResult.PARAM_NAME.getName())].toString())) {
                        getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
                        fieldVal = LPArray.addValueToArray1D(fieldVal, specLimitId);
                    }
                    RdbmsObject newResultRdbmsDiagnObj = Rdbms.insertRecordInTable(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                            getResultFields, fieldVal);
                    if (Boolean.FALSE.equals(newResultRdbmsDiagnObj.getRunSuccess())) {
                        return new InternalMessage(LPPlatform.LAB_FALSE, newResultRdbmsDiagnObj.getErrorMessageCode(), newResultRdbmsDiagnObj.getErrorMessageVariables());
                    }
                    Integer resultId = Integer.parseInt(newResultRdbmsDiagnObj.getNewRowId().toString());
                    smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ADDED, sampleLevel + TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                            resultId, sampleId, testId, resultId, getResultFields, fieldVal);
                    Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY.getTableName());
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())) {
                        getResultFields = LPArray.addValueToArray1D(getResultFields, TblsData.SampleAnalysisResultSecondEntry.FIRST_RESULT_ID.getName());
                        fieldVal = LPArray.addValueToArray1D(fieldVal, resultId);
                        newResultRdbmsDiagnObj = Rdbms.insertRecordInTable(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY,
                                getResultFields, fieldVal);
                        Integer resultIdSecondEntry = Integer.parseInt(newResultRdbmsDiagnObj.getNewRowId().toString());
                        smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_SECONDENTRY_ADDED, sampleLevel + TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                                resultId, sampleId, testId, resultIdSecondEntry, getResultFields, fieldVal);
                    }
                }
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isReviewByTestingGroupEnable.getDiagnostic())) {
                InternalMessage addSampleRevisionByTestingGroup = DataSampleRevisionTestingGroup.addSampleRevisionByTestingGroup(sampleId, testId, specAnalysisTestingGroup);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(addSampleRevisionByTestingGroup.getDiagnostic())) {
                    return addSampleRevisionByTestingGroup;
                }
            }

            return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.SAMPLE_ANALYSIS_ADDED_SUCCESS, new Object[]{"", testId, schemaDataName});
        } catch (Exception e) {
            return new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.EXCEPTION_RAISED, new Object[]{e.getMessage()});
        }
    }

}
