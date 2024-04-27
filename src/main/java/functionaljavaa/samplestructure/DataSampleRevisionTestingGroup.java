/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import databases.features.Token;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.samplestructure.DataSampleStructureRevisionRules.DataSampleStructureRevisionRls;
import static functionaljavaa.samplestructure.DataSampleStructureRevisionRules.reviewTestingGroupRulesAllowed;
import java.util.ArrayList;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public class DataSampleRevisionTestingGroup {

    public enum DataSampleRevisionTestingGroupBusinessRules implements EnumIntBusinessRules {
        SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP("sampleTestingByGroup_ReviewByTestingGroup", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        SAMPLETESTINGBYGROUP_GENERICAUTOAPPROVEENABLED("sampleTestingGroupGenericAutoApproveEnabled", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),;

        private DataSampleRevisionTestingGroupBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator,
                 Boolean isOpt, ArrayList<String[]> preReqs) {
            this.tagName = tgName;
            this.areaName = areaNm;
            this.valuesList = valuesList;
            this.allowMultiValue = allowMulti;
            this.multiValueSeparator = separator;
            this.isOptional = isOpt;
            this.preReqs = preReqs;
        }

        @Override
        public String getTagName() {
            return this.tagName;
        }

        @Override
        public String getAreaName() {
            return this.areaName;
        }

        @Override
        public JSONArray getValuesList() {
            return this.valuesList;
        }

        @Override
        public Boolean getAllowMultiValue() {
            return this.allowMultiValue;
        }

        @Override
        public char getMultiValueSeparator() {
            return this.multiValueSeparator;
        }

        @Override
        public Boolean getIsOptional() {
            return isOptional;
        }

        @Override
        public ArrayList<String[]> getPreReqs() {
            return this.preReqs;
        }

        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;
        private final Boolean isOptional;
        private final ArrayList<String[]> preReqs;
    }

    public enum DataSampleRevisionTestingGroupErrorTrapping implements EnumIntMessages {
        SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP_NOT_FOUND("sampleTestingByGroup_ReviewByTestingGroupNotFound", "sampleTestingByGroup_ReviewByTestingGroup Not Active", "sampleTestingByGroup_ReviewByTestingGroup No Activo"),
        SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP_NOT_ACTIVE("sampleTestingByGroup_ReviewByTestingGroupNotActive", "sampleTestingByGroup_ReviewByTestingGroup Not Active", "sampleTestingByGroup_ReviewByTestingGroup No Activo"),
        SAMPLETESTINGBYGROUP_PENDING_TESTINGGROUPREVISION("DataSampleRevision_PendingTestingGroupRevision", "There are pending testing group, <*1*>, for the sample <*2*> in procedure <*3*>", "There are pending testing group, <*1*>, for the sample <*2*> in procedure <*3*>"),
        SAMPLETESTINGBYGROUP_NOPENDING_TESTINGGROUPREVISION("DataSampleRevision_NoPendingTestingGroupRevision", "No testing group revision pending for sample <*1*> in procedure <*2*>", "No testing group revision pending for sample <*1*> in procedure <*2*>"),
        SAMPLETESTINGBYGROUP_ALREADY_READYFORREVISION("DataSampleRevision_alreadyReadyForRevision", "Already ready for revision", "Ya está marcado para revisión"),
        SAMPLETESTINGBYGROUP_ALREADY_REVIEWED("DataSampleRevision_alreadyReviewer", "Already reviewed", "Ya está revisado"),
        NOT_READY_FOR_REVISION("DataSampleRevision_notReadyForRevision", "Not ready for revision", "No listo para la revisión"),
        SAMPLETESTINGBYGROUP_PENDINGRESULTSINTESTINGGROUP("DataSampleRevision_PendingResultsInTestingGroup", "There are pending results for the testing group <*1*> for the sample <*2*> in procedure <*3*>", "There are pending results for the testing group <*1*> for the sample <*2*> in procedure <*3*>");

        private DataSampleRevisionTestingGroupErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
            this.errorCode = errCode;
            this.defaultTextWhenNotInPropertiesFileEn = defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs = defaultTextEs;
        }

        @Override
        public String getErrorCode() {
            return this.errorCode;
        }

        @Override
        public String getDefaultTextEn() {
            return this.defaultTextWhenNotInPropertiesFileEn;
        }

        @Override
        public String getDefaultTextEs() {
            return this.defaultTextWhenNotInPropertiesFileEs;
        }

        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }

    public static InternalMessage addSampleRevisionByTestingGroup(Integer sampleId, Integer testId, String specAnalysisTestingGroup) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[] isReviewByTestingGroupEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP_NOT_ACTIVE, null);
        }
        if (specAnalysisTestingGroup == null || specAnalysisTestingGroup.length() == 0) {
            Object[][] testInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                    new String[]{TblsData.SampleAnalysis.TEST_ID.getName()},
                    new Object[]{testId}, new String[]{TblsData.SampleAnalysis.TESTING_GROUP.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testInfo[0][0].toString())) {
                return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP_NOT_FOUND, null);
            }
            if (LPNulls.replaceNull(testInfo[0][0]).toString().length() == 0) {

                return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{testId});
            }
            specAnalysisTestingGroup = testInfo[0][0].toString();
        }
        Object[] existsSampleRevisionTestingGroupRecord = Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP.getTableName(),
                new String[]{TblsData.SampleRevisionTestingGroup.SAMPLE_ID.getName(), TblsData.SampleRevisionTestingGroup.TESTING_GROUP.getName()},
                new Object[]{sampleId, specAnalysisTestingGroup});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsSampleRevisionTestingGroupRecord[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, new Object[]{sampleId, specAnalysisTestingGroup});
        }
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP,
                new String[]{TblsData.SampleRevisionTestingGroup.SAMPLE_ID.getName(), TblsData.SampleRevisionTestingGroup.TESTING_GROUP.getName(), TblsData.SampleRevisionTestingGroup.READY_FOR_REVISION.getName(), TblsData.SampleRevisionTestingGroup.REVIEWED.getName()},
                new Object[]{sampleId, specAnalysisTestingGroup, false, false});
        return new InternalMessage(insertRecordInTable.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());
    }

    public static InternalMessage isSampleRevisionByTestingGroupReviewed(Integer sampleId) {
        return isSampleRevisionByTestingGroupReviewed(sampleId, null);
    }

    public static InternalMessage isSampleRevisionByTestingGroupReviewed(Integer sampleId, String testingGroup) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[] isReviewByTestingGroupEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP_NOT_ACTIVE, null);
        }
        String[] fieldNames = new String[]{TblsData.SampleRevisionTestingGroup.SAMPLE_ID.getName(), TblsData.SampleRevisionTestingGroup.REVIEWED.getName()};
        Object[] fieldValues = new Object[]{sampleId, false};
        if (testingGroup != null && testingGroup.length() > 0) {
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsData.SampleRevisionTestingGroup.TESTING_GROUP.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, testingGroup);
        }
        Object[][] existsPendingRevisionRecord = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP.getTableName(),
                fieldNames, fieldValues, new String[]{TblsData.SampleRevisionTestingGroup.TESTING_GROUP.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsPendingRevisionRecord[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_NOPENDING_TESTINGGROUPREVISION, new Object[]{sampleId, procInstanceName});
        } else {
            String pendingTestingGroupStr = Arrays.toString(LPArray.getColumnFromArray2D(existsPendingRevisionRecord, 0));
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_PENDING_TESTINGGROUPREVISION,
                    new Object[]{pendingTestingGroupStr, sampleId, procInstanceName});
        }
    }

    public static InternalMessage isReadyForRevision(Integer sampleId, String testingGroup) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] sampleAnalysisFieldName = new String[]{TblsData.SampleRevisionTestingGroup.READY_FOR_REVISION.getName()};
        Object[][] sampleAnalysisInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP.getTableName(),
                new String[]{TblsData.SampleRevisionTestingGroup.SAMPLE_ID.getName(), TblsData.SampleRevisionTestingGroup.TESTING_GROUP.getName()}, new Object[]{sampleId, testingGroup}, sampleAnalysisFieldName);
        if ("TRUE".equalsIgnoreCase(sampleAnalysisInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleStructureEnums.DataSampleStructureSuccess.READY_FOR_REVISION, new Object[]{sampleId, procInstanceName});
        }
        return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleRevisionTestingGroupErrorTrapping.NOT_READY_FOR_REVISION, new Object[]{sampleId, procInstanceName});        
    }

    public static InternalMessage isAllsampleTestingGroupReviewed(Integer sampleId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] fieldExists = Rdbms.dbTableExists(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP.getTableName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldExists[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
        }
        String[] whereFieldName = new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName()};
        Object[] whereFieldValue = new Object[]{sampleId};
        Object[][] grouper = Rdbms.getGrouper(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP.getTableName(),
                new String[]{TblsData.SampleRevisionTestingGroup.REVIEWED.getName()}, whereFieldName, whereFieldValue, null);
        if (grouper.length != 1) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleStructureEnums.DataSampleAnalysisErrorTrapping.PENDING_REVISION, null);
        }
        if (Boolean.FALSE.equals(grouper[0][0].toString().equalsIgnoreCase("TRUE"))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleStructureEnums.DataSampleAnalysisErrorTrapping.PENDING_REVISION, null);
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
    }

    public static InternalMessage reviewSampleTestingGroup(Integer sampleId, String testingGroup) {
        return reviewSampleTestingGroup(sampleId, testingGroup, null);
    }

    public static InternalMessage reviewSampleTestingGroup(Integer sampleId, String testingGroup, String reviewer) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        if (reviewer == null) {
            reviewer = token.getPersonName();
        }

        InternalMessage isReviewByTestingGroupEnable = LPPlatform.isProcedureBusinessRuleDisableInternalMessage(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP.getTagName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isReviewByTestingGroupEnable.getDiagnostic())) {
            return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP_NOT_ACTIVE, null);
        }
        InternalMessage sampleRevisionByTestingGroupReviewed = isSampleRevisionByTestingGroupReviewed(sampleId, testingGroup);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(sampleRevisionByTestingGroupReviewed.getDiagnostic())) {
            return new InternalMessage(LPPlatform.LAB_TRUE, sampleRevisionByTestingGroupReviewed.getMessageCodeObj(), sampleRevisionByTestingGroupReviewed.getMessageCodeVariables());
        }
        
        Object[] existsPendingAnalysis = Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW.getViewName(),
                new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_ID.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.TESTING_GROUP.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.MANDATORY.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE.getName() + " is null"},
                new Object[]{sampleId, testingGroup, true});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsPendingAnalysis[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_PENDINGRESULTSINTESTINGGROUP, new Object[]{testingGroup, sampleId, procInstanceName});
        }
        Object[] isRevisionSampleAnalysisRequired = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleStructureRevisionRls.REVISION_SAMPLEANALYSIS_REQUIRED.getAreaName(), DataSampleStructureRevisionRls.REVISION_SAMPLEANALYSIS_REQUIRED.getTagName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionSampleAnalysisRequired[0].toString())) {
            InternalMessage isallsampleAnalysisReviewed = DataSampleAnalysis.isAllsampleAnalysisReviewed(sampleId, new String[]{TblsData.SampleAnalysis.TESTING_GROUP.getName()}, new Object[]{testingGroup});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isallsampleAnalysisReviewed.getDiagnostic())) {
                return new InternalMessage(LPPlatform.LAB_FALSE, isallsampleAnalysisReviewed.getMessageCodeObj(),
                        isallsampleAnalysisReviewed.getMessageCodeVariables());
            }
        }
        InternalMessage readyForRevision2 = isReadyForRevision(sampleId, testingGroup);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(readyForRevision2.getDiagnostic())) {
            return readyForRevision2;
        }

        InternalMessage reviewTstGgpRules = reviewTestingGroupRulesAllowed(sampleId, testingGroup);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reviewTstGgpRules.getDiagnostic())) {
            return reviewTstGgpRules;
        }
        String[] updFldNames = new String[]{TblsData.SampleRevisionTestingGroup.READY_FOR_REVISION.getName(), TblsData.SampleRevisionTestingGroup.REVIEWED.getName(), TblsData.SampleRevisionTestingGroup.REVISION_BY.getName(), TblsData.SampleRevisionTestingGroup.REVISION_ON.getName()};
        Object[] updFldValues = new Object[]{false, true, reviewer, LPDate.getCurrentTimeStamp()};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsData.SampleRevisionTestingGroup.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
        sqlWhere.addConstraint(TblsData.SampleRevisionTestingGroup.TESTING_GROUP, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{testingGroup}, "");
        RdbmsObject updateReviewSampleTestingGroup = Rdbms.updateTableRecordFieldsByFilter(TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP,
                EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP, updFldNames), updFldValues, sqlWhere, null);
        if (Boolean.TRUE.equals(updateReviewSampleTestingGroup.getRunSuccess())) {
            SampleAudit smpAudit = new SampleAudit();
            smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.SAMPLE_TESTINGGROUP_REVIEWED, TblsData.TablesData.SAMPLE.getTableName(),
                    sampleId, sampleId, null, null, updFldNames, updFldValues);
            markSampleAsReadyForRevision(sampleId);
        }
        return new InternalMessage(updateReviewSampleTestingGroup.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, updateReviewSampleTestingGroup.getErrorMessageCode(), updateReviewSampleTestingGroup.getErrorMessageVariables());
    }

    public static InternalMessage markSampleAsReadyForRevision(Integer sampleId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[][] pendingTestingGroupByRevisionValue = Rdbms.getGrouper(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP.getTableName(),
                new String[]{TblsData.SampleRevisionTestingGroup.REVIEWED.getName()},
                new String[]{TblsData.SampleRevisionTestingGroup.SAMPLE_ID.getName()},
                new Object[]{sampleId}, null);
        if (pendingTestingGroupByRevisionValue.length == 1 && pendingTestingGroupByRevisionValue[0][0].toString().equalsIgnoreCase("TRUE")) {
            DataSample.setReadyForRevision(sampleId);
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
        }
        return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_PENDING_TESTINGGROUPREVISION, new Object[]{sampleId, procInstanceName});
    }

    /**
     *
     * @param sampleId
     * @param testingGroup
     * @return
     */
    public static InternalMessage setReadyForRevision(Integer sampleId, String testingGroup) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] sampleFieldName = new String[]{TblsData.SampleRevisionTestingGroup.READY_FOR_REVISION.getName()};
        Object[] sampleFieldValue = new Object[]{true};
        Object[][] sampleRevisionTestingGroupInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP.getTableName(),
                new String[]{TblsData.SampleRevisionTestingGroup.SAMPLE_ID.getName(), TblsData.SampleRevisionTestingGroup.TESTING_GROUP.getName()}, new Object[]{sampleId, testingGroup}, sampleFieldName);
        if ("TRUE".equalsIgnoreCase(sampleRevisionTestingGroupInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_ALREADY_READYFORREVISION, new Object[]{sampleId, procInstanceName});
        }
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsData.SampleRevisionTestingGroup.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
        sqlWhere.addConstraint(TblsData.SampleRevisionTestingGroup.TESTING_GROUP, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{testingGroup}, "");
        RdbmsObject diagnoses = Rdbms.updateTableRecordFieldsByFilter(TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP,
                EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP, sampleFieldName), sampleFieldValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnoses.getRunSuccess())) {
            SampleAudit smpAudit = new SampleAudit();
            Object[] isSampleTestingGroupGenericAutoApproveEnabled = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_GENERICAUTOAPPROVEENABLED.getAreaName(), DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_GENERICAUTOAPPROVEENABLED.getTagName());
//            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isSampleTestingGroupGenericAutoApproveEnabled[0].toString()))
//                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.SAMPLE_TESTINGGROUP_SET_AUTOAPPROVE.toString(), TblsData.SampleRevisionTestingGroup.TBL.getName(), sampleId, sampleId, null, null, fieldsForAudit, null);
//            else
            smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.SAMPLE_TESTINGGROUP_SET_READY_REVISION, TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP.getTableName(),
                    sampleId, sampleId, null, null, sampleFieldName, sampleFieldValue);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isSampleTestingGroupGenericAutoApproveEnabled[0].toString())) {
                return reviewSampleTestingGroup(sampleId, testingGroup, DataSample.AUTO_APPROVE_USER);
            }
        }
        return new InternalMessage(diagnoses.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, diagnoses.getErrorMessageCode(), diagnoses.getErrorMessageVariables());
    }
}
