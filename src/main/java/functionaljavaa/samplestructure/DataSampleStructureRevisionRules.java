/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import databases.Rdbms;
import databases.TblsData;
import databases.features.Token;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleAnalysisErrorTrapping;
import java.util.ArrayList;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class DataSampleStructureRevisionRules {

    public enum DataSampleStructureRevisionRls implements EnumIntBusinessRules {
        //SAMPLE_REVIEW_REVIEWER_MODE("sampleReviewReviewerMode",GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleEnums.sampleReviewReviewerModeValues.getValuesInOne(), null, '|'),
        SAMPLE_REVIEW_CAN_BE_ANY_AUTHOR("sampleReviewer_canBeAnyResultAuthor", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        SAMPLE_REVIEW_CAN_BE_ANY_TEST_REVIEWER("sampleReviewer_canBeAnyTestReviewer", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        SAMPLE_REVIEW_CAN_BE_ANY_TESTING_GROUP_REVIEWER("sampleReviewer_canBeAnyTestingGroupReviewer", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        TESTING_GROUP_REVIEWER_CANBE_TEST_REVIEWER("testingGroupReviewer_canBeTestReviewer", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        SAMPLEANALYSIS_AUTHORCANBEREVIEWERTOO("sampleAnalysisAuthorCanBeReviewerToo", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        REVISION_SAMPLEANALYSIS_REQUIRED("revisionSampleAnalysisRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),;

        private DataSampleStructureRevisionRls(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator,
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

    public enum DataSampleStructureRevisionErrorTrapping implements EnumIntMessages {
        SAMPLEANALYSIS_AUTHOR_CANNOTBE_ITSREVIEWER("SampleAnalysisAuthorCannotBeReviewer", "", ""),
        SAMPLEANALYSIS_AUTHOR_CANNOTBE_SAMPLEREVIEWER("SampleAnalysisAuthorCannotBeSampleReviewer", "", ""),
        SAMPLEANALYSIS_REVIEWER_CANNOTBE_SAMPLEREVIEWER("SampleAnalysisReviewerCannotBeSampleReviewer", "", ""),
        SAMPLEANALYSIS_REVIEWER_CANNOTBE_TESTINGROUPREVIEWER("SampleAnalysisReviewerCannotBeTestingGroupReviewer", "", ""),
        SAMPLEANALYSIS_ALREADYRVIEWED("sampleAnalysisAlreadyReviewed", "", ""),
        SAMPLE_REVIEWER_CANNOTBE_TESTINGROUPREVIEWER("SampleReviewerCannotBeTestingGroupReviewer", "", ""),;

        private DataSampleStructureRevisionErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
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

    public static InternalMessage sampleReviewRulesAllowed(Integer sampleId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        Object[] canBeAnyAuthorIsEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleStructureRevisionRls.SAMPLE_REVIEW_CAN_BE_ANY_AUTHOR.getAreaName(), DataSampleStructureRevisionRls.SAMPLE_REVIEW_CAN_BE_ANY_AUTHOR.getTagName());
        Object[] canBeAnyTestReviewerIsEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleStructureRevisionRls.SAMPLE_REVIEW_CAN_BE_ANY_TEST_REVIEWER.getAreaName(), DataSampleStructureRevisionRls.SAMPLE_REVIEW_CAN_BE_ANY_TEST_REVIEWER.getTagName());
        Object[] canBeAnyTestingGroupReviewerIsEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleStructureRevisionRls.SAMPLE_REVIEW_CAN_BE_ANY_TESTING_GROUP_REVIEWER.getAreaName(), DataSampleStructureRevisionRls.SAMPLE_REVIEW_CAN_BE_ANY_TESTING_GROUP_REVIEWER.getTagName());
        Object[] testingGroupTableExists = Rdbms.dbTableExists(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP.getTableName());

        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(canBeAnyAuthorIsEnable[0].toString())
                && LPPlatform.LAB_TRUE.equalsIgnoreCase(canBeAnyTestReviewerIsEnable[0].toString())&& 
                (LPPlatform.LAB_FALSE.equalsIgnoreCase(testingGroupTableExists[0].toString())
                   || LPPlatform.LAB_TRUE.equalsIgnoreCase(testingGroupTableExists[0].toString()) && LPPlatform.LAB_TRUE.equalsIgnoreCase(canBeAnyTestingGroupReviewerIsEnable[0].toString()))) {
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
        }
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(canBeAnyAuthorIsEnable[0].toString())
                || LPPlatform.LAB_FALSE.equalsIgnoreCase(canBeAnyTestReviewerIsEnable[0].toString())) {
            String[] fieldsToRetrieve = new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.ENTERED_BY.getName(),
                TblsData.ViewSampleAnalysisResultWithSpecLimits.TEST_REVIEWER.getName()};
            Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW.getViewName(),
                    new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_ID.getName()}, new Object[]{sampleId}, fieldsToRetrieve);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(canBeAnyAuthorIsEnable[0].toString())
                    && (LPArray.valueInArray(LPArray.getColumnFromArray2D(sampleInfo, 0), token.getPersonName()))) {
                return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleStructureRevisionErrorTrapping.SAMPLEANALYSIS_AUTHOR_CANNOTBE_SAMPLEREVIEWER, null);
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(canBeAnyTestReviewerIsEnable[0].toString())
                    && (LPArray.valueInArray(LPArray.getColumnFromArray2D(sampleInfo, 1), token.getPersonName()))) {
                return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleStructureRevisionErrorTrapping.SAMPLEANALYSIS_REVIEWER_CANNOTBE_SAMPLEREVIEWER, null);
            }
        }

        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(testingGroupTableExists[0].toString())
                && (LPPlatform.LAB_FALSE.equalsIgnoreCase(canBeAnyTestingGroupReviewerIsEnable[0].toString()))) {
            String[] fieldsToRetrieve = new String[]{TblsData.SampleRevisionTestingGroup.REVISION_BY.getName()};
            Object[][] testingGroupInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP.getTableName(),
                    new String[]{TblsData.SampleRevisionTestingGroup.SAMPLE_ID.getName()}, new Object[]{sampleId}, fieldsToRetrieve);
            Object[] testingGroupInfo1D = LPArray.getColumnFromArray2D(testingGroupInfo, 0);
            if (LPArray.valueInArray(testingGroupInfo1D, token.getPersonName())) {
                return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleStructureRevisionErrorTrapping.SAMPLE_REVIEWER_CANNOTBE_TESTINGROUPREVIEWER, null);
            }
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
    }

    public static InternalMessage reviewSampleAnalysisRulesAllowed(Integer testId, String[] tstFldName, Object[][] tstFldValues) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        Object[] isSampleAnalysisAuthorCanBeReviewerTooEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleStructureRevisionRls.SAMPLEANALYSIS_AUTHORCANBEREVIEWERTOO.getAreaName(), DataSampleStructureRevisionRls.SAMPLEANALYSIS_AUTHORCANBEREVIEWERTOO.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isSampleAnalysisAuthorCanBeReviewerTooEnable[0].toString())
                && (LPArray.valueInArray(LPArray.getColumnFromArray2D(tstFldValues, LPArray.valuePosicInArray(tstFldName, TblsData.SampleAnalysis.ANALYST.getName())), token.getPersonName()))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleStructureRevisionErrorTrapping.SAMPLEANALYSIS_AUTHOR_CANNOTBE_ITSREVIEWER, null);
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
    }

    public static InternalMessage reviewTestingGroupRulesAllowed(Integer sampleId, String testingGroup) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String tokenUserName = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken().getUserName();
        String tokenPersonName = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken().getPersonName();
        InternalMessage procedureBusinessRuleEnable = LPPlatform.isProcedureBusinessRuleEnableInternalMessage(procInstanceName, DataSampleStructureRevisionRls.TESTING_GROUP_REVIEWER_CANBE_TEST_REVIEWER.getAreaName(), DataSampleStructureRevisionRls.TESTING_GROUP_REVIEWER_CANBE_TEST_REVIEWER.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureBusinessRuleEnable.getDiagnostic())) {
            return procedureBusinessRuleEnable;
        }
        String[] whereFieldName = new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.TESTING_GROUP.getName()};
        Object[] whereFieldValue = new Object[]{sampleId, testingGroup};
        Object[][] grouper = Rdbms.getGrouper(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                new String[]{TblsData.SampleAnalysis.REVIEWER.getName()}, whereFieldName, whereFieldValue, null);
        if ((LPArray.valueInArray(LPArray.getColumnFromArray2D(grouper, 0), tokenUserName))
                || (LPArray.valueInArray(LPArray.getColumnFromArray2D(grouper, 0), tokenPersonName))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisErrorTrapping.PENDING_REVISION, null);
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE,
                new Object[]{tokenUserName, Arrays.toString(LPArray.getColumnFromArray2D(grouper, 0))});
    }

}
