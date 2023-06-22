/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class DataSampleStructureEnums {

    public enum DataSampleStructureSuccess implements EnumIntMessages {
        READY_FOR_REVISION("Sample_readyForRevision", "", ""),
        SAMPLEANALYSISASSIGNED_SUCCESS("SampleAnalysisAssignment_Successfully", "", ""),
        SAMPLEANALYSIS_CANCELED("SampleAnalysisCanceled", "", ""),
        SAMPLE_ANALYSIS_ADDED_SUCCESS("SampleAnalysisAddedSuccessfully", "", ""),
        ACTIONNOTDECLARED_TOPERFORMAUTOMOVETONEXT("actionNotDeclaredToPerformAutoMoveToNext", "The action <*1*> is not declared as to perform auto move to next in procedure <*2*>", ""),
        QUANTITATIVE_IN("IN", "", ""),
        QUANTITATIVE_IN_ALERT_MAX("inAlertMax", "", ""), QUANTITATIVE_IN_ALERT_MAX_STRICT("inAlertMaxStrict", "", ""),
        QUANTITATIVE_IN_ALERT_MIN("inAlertMin", "", ""), QUANTITATIVE_IN_ALERT_MIN_STRICT("inAlertMinStrict", "", ""),
        OUT_SPEC_MIN("outOfSpecMin", "", ""), OUT_SPEC_MAX("outOfSpecMax", "", ""),
        OUT_SPEC_MIN_STRICT("outOfSpecMinStrict", "", ""), OUT_SPEC_MAX_STRICT("outOfSpecMaxStrict", "", ""),
        QUALITATIVE_IN("IN", "", ""),
        QUALITATIVE_OUT_EQUAL_TO("outEqualTo", "", ""),
        QUALITATIVE_OUT_NOT_EQUAL_TO("outNotEqualTo", "", ""),
        QUALITATIVE_OUT_CONTAINS("outContains", "", ""),
        QUALITATIVE_OUT_NOT_CONTAINS("outNotContains", "", ""),
        QUALITATIVE_OUT_IS_ONE_OF("outIsOneOf", "", ""),
        QUALITATIVE_OUT_IS_NOT_ONE_OF("outIsNotOneOf", "", ""),
        EVALUATION_IN("IN", "", ""),
        EVALUATION_OUT("OUT", "", ""),
        EVALUATION_UPON_CONTROL_MIN("inAlertMin", "", ""),
        EVALUATION_UPON_CONTROL_MAX("inAlertMax", "", ""),;

        DataSampleStructureSuccess(String cl, String msgEn, String msgEs) {
            this.errorCode = cl;
            this.defaultTextWhenNotInPropertiesFileEn = msgEn;
            this.defaultTextWhenNotInPropertiesFileEs = msgEs;
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

    public enum DataSampleErrorTrapping implements EnumIntMessages {
        SETSAMPLINGDATE_NOT_ALLOW_CHANGE_PREVIOUS_VALUE("SetSamplingDate_notAllowChangePreviousValue", "", ""),
        CHANGESAMPLINGDATE_NOT_ALLOW_WHEN_NOT_PREVIOUSDATE("changeSamplingDate_cannotBeAppliedForNullValue", "", ""),
        CHANGESAMPLINGDATE_NOT_ALLOW_WHEN_SAME_PREVIOUSDATE("changeSamplingDate_sameSamplingDate", "", ""),
        CHANGESAMPLINGDATEEND_NOT_ALLOW_WHEN_NOT_PREVIOUSDATE("changeSamplingDateEnd_cannotBeAppliedForNullValue", "", ""),
        CHANGESAMPLINGDATEEND_NOT_ALLOW_WHEN_SAME_PREVIOUSDATE("changeSamplingDateEnd_sameSamplingDate", "", ""),
        SAMPLINGDATE_REQUIRED_FOR_SAMPLINGDATEEND("SamplingDateRequiredForSampleDateEnd", "", ""),
        SAMPLINGDATEEND_NOTREQUIRED_ASTOPERFORMTHEACTION("SamplingDateEndNotRequiredAsToPerformTheAction", "", ""),
        ERROR_INSERTING_SAMPLE_RECORD("errorInsertingSampleRecord", "", ""),
        SAMPLE_STATUS_MANDATORY("SampleStatusMandatory", "", ""),
        MISSING_MANDATORY_FIELDS("MissingMandatoryFields", "", ""),
        MISSING_CONFIG_CODE("MissingConfigCode", "", ""),
        MISSING_SPEC_CONFIG_CODE("MissingSpecConfigCode", "Spec Config code <*1*> version <*2*> Not found for the procedure <*3*>", ""),
        SAMPLE_ALREADY_RECEIVED("SampleAlreadyReceived", "", ""),
        SAMPLE_NOT_REVIEWABLE("SampleNotReviewable", "", ""),
        VOLUME_SHOULD_BE_GREATER_THAN_ZERO("sampleAliquoting_volumeCannotBeNegativeorZero", "", ""),
        ALIQUOT_CREATED_BUT_ID_NOT_GOT("AliquotCreatedButIdNotGotToContinueApplyingAutomatisms", "Object created but aliquot id cannot be get back to continue with the logic", ""),
        SAMPLEASUBLIQUOTING_VOLUME_AND_UOM_REQUIRED("sampleSubAliquoting_volumeAndUomMandatory", "", ""),
        SAMPLE_NOT_FOUND("SampleNotFound", "", ""),
        SAMPLE_FIELDNOTFOUND("SampleFieldNotFound", "", ""),
        SAMPLE_RULES_NOT_FOUND("SampleRulesNotFound", "", ""),
        NOT_IMPLEMENTED("notImplementedWhenSetReadyForRevisionNotSetToTrue", "NOT IMPLEMENTED YET WHEN SET READY FOR REVISION NOT TRUE YET", ""),
        SAMPLE_ALREADY_REVIEWED("sampleAlreadyReviewed", "", ""),
        SAMPLE_ALREADY_READY_FOR_REVISION("alreadyReadyForRevision", "", ""),
        SAMPLE_CANNOT_BE_UNCANCELLED("DataSample_sampleCannotBeUncanceled", "", ""),;

        private DataSampleErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
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

    public enum DataSampleBusinessRules implements EnumIntBusinessRules {
        STATUSES("sample_statuses", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleStatusesByBusinessRules"),
        STATUSES_LABEL_EN("sample_statuses_label_en", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleStatusesByBusinessRules"),
        STATUSES_LABEL_ES("sample_statuses_label_es", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleStatusesByBusinessRules"),
        SUFFIX_STATUS_FIRST("_statusFirst", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleStatusesByBusinessRules"),
        STATUS_REVIEWED("sample_statusReviewed", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleStatusesByBusinessRules"),
        STATUS_CANCELED("sample_statusCanceled", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleStatusesByBusinessRules"),
        SUFFIX_SAMPLESTRUCTURE("_sampleStructure", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleStatusesByBusinessRules"),
        SAMPLE_STATUS_FIRST("sample_statusFirst", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleStatusesByBusinessRules"),
        SAMPLE_STATUS_RECEIVED("sample_statusReceived", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleStatusesByBusinessRules"),
        SAMPLE_STATUS_INCOMPLETE("sample_statusIncomplete", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleStatusesByBusinessRules"),
        SAMPLE_STATUS_COMPLETE("sample_statusComplete", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleStatusesByBusinessRules"),
        SAMPLE_STATUS_REVIEWED("sample_statusReviewed", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleStatusesByBusinessRules"),
        SAMPLE_STATUS_CANCELED("sample_statusCanceled", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleStatusesByBusinessRules"),
        SAMPLEALIQUOTING_VOLUME_REQUIRED("sampleAliquot_volumeRequired", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, null),
        SAMPLEASUBLIQUOTING_VOLUME_REQUIRED("sampleSubAliquot_volumeRequired", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, null),
        SAMPLE_GENERICAUTOAPPROVEENABLED("sampleGenericAutoApproveEnabled", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', true, null),;

        private DataSampleBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator, Boolean isOptional, String preReqs) {
            this.tagName = tgName;
            this.areaName = areaNm;
            this.valuesList = valuesList;
            this.allowMultiValue = allowMulti;
            this.multiValueSeparator = separator;
            this.isOptional = isOptional;
            this.preReqsBusRules = preReqs;
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

        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;
        private final Boolean isOptional;
        private final String preReqsBusRules;

        @Override
        public Boolean getIsOptional() {
            return this.isOptional;
        }

        @Override
        public ArrayList<String[]> getPreReqs() {
            ArrayList<String[]> d = new ArrayList<String[]>();
            if (preReqsBusRules != null && preReqsBusRules.length() > 0) {
                String[] rulesArr = preReqsBusRules.split("\\|");
                for (String curRule : rulesArr) {
                    String[] curRuleArr = curRule.split("\\*");
                    if (curRuleArr.length == 2) {
                        d.add(curRuleArr);
                    }
                }
            }
            return d;
        }
    }

    public enum DataSampleAnalysisBusinessRules implements EnumIntBusinessRules {
        SAMPLEANALYSIS_STATUSFIRST("sampleAnalysis_statusFirst", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleAnalysisStatusesByBusinessRules"),
        SAMPLEANALYSIS_STATUSINCOMPLETE("sampleAnalysis_statusIncomplete", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleAnalysisStatusesByBusinessRules"),
        SAMPLEANALYSIS_STATUSCOMPLETE("sampleAnalysis_statusComplete", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleAnalysisStatusesByBusinessRules"),
        SAMPLEANALYSIS_STATUSCANCELED("sampleAnalysis_statusCanceled", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleAnalysisStatusesByBusinessRules"),
        SAMPLEANALYSIS_STATUSREVIEWED("sampleAnalysis_statusReviewed", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleAnalysisStatusesByBusinessRules"),
        MARK_READYFORREVISION_WHENALLRESULTSENTERED("revisionSampleAnalysis_markAsReadyForRevisionWhenAllResultsEntered", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', false, null),
        SAMPLEANALYSIS_ANALYSTASSIGNMENTMODE("sampleAnalysis_analystAssigmentMode", GlobalVariables.Schemas.DATA.getName(), null, null, '|', false, null),
        SAMPLEANALYSIS_ANALYSTASSIGNMENTMODES("sampleAnalysis_analystAssigmentModes", GlobalVariables.Schemas.DATA.getName(), null, null, '|', false, null),
        REVISION_SAMPLEANALYSIS_REQUIRED("revisionSampleAnalysisRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', false, null),
        REVISION_TESTINGGROUP_REQUIRED("revisionTestinGroupRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', false, null),
        SAMPLEANALYSIS_GENERICAUTOAPPROVEENABLED("sampleAnalysisGenericAutoApproveEnabled", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', false, null),;

        private DataSampleAnalysisBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator, Boolean isOptional, String preReqs) {
            this.tagName = tgName;
            this.areaName = areaNm;
            this.valuesList = valuesList;
            this.allowMultiValue = allowMulti;
            this.multiValueSeparator = separator;
            this.isOptional = isOptional;
            this.preReqsBusRules = preReqs;
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
            return this.isOptional;
        }

        @Override
        public ArrayList<String[]> getPreReqs() {
            ArrayList<String[]> d = new ArrayList<String[]>();
            if (preReqsBusRules != null && preReqsBusRules.length() > 0) {
                String[] rulesArr = preReqsBusRules.split("\\|");
                for (String curRule : rulesArr) {
                    String[] curRuleArr = curRule.split("\\*");
                    if (curRuleArr.length == 2) {
                        d.add(curRuleArr);
                    }
                }
            }
            return d;
        }
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;
        private final Boolean isOptional;
        private final String preReqsBusRules;

    }

    public enum DataSampleAnalysisErrorTrapping implements EnumIntMessages {
        MARK_READYFORREVISION_WHENALLRESULTSENTERED_NOTACTIVE_SUCCESS("DataSample_markAsReadyForRevisionWhenAllResultsEnteredNotActive", "", ""),
        ALREADY_READYFORREVISION("DataSample_alreadyReadyForRevision", "", ""),
        ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS("DataSample_sampleAnalaysisAddToSample_MissingMandatoryFields", "", ""),
        ANALYSISMETHOD_PARAMSNOTFOUND("DataSample_AnalysisMethodParamsNotFound", "", ""),
        PENDING_REVISION("DataSample_SampleAnalysisPendingRevision", "", ""),
        SPECLIMIT_NOTFOUND("DataSample_SpecLimitNotFound", "", ""),
        SPECRULE_NOTFOUND("DataSample_SpecRuleNotFound", "", ""),
        SAMPLEANALYSISASSIGNED_MODENOTRECOGNIZED("DataSample_SampleAnalysisAssignment_AssignmentModeNotRecognized", "", ""),
        SAMPLEANALYSISASSIGNED_MODENOTIMPLEMENTED("DataSample_SampleAnalysisAssignment_AssignmentModeNotImplemented", "", ""),
        DB_RETURNEDERROR("DataSample_SampleAnalysisAssignment_databaseReturnedError", "", ""),
        ESCAPE_UNHANDLEDEXCEPTION("DataSample_SampleAnalysisAssignment_EscapeByUnhandledException", "", ""),
        SAMPLEANALYSIS_NOTFOUND("DataSample_SampleAnalysisNotFound", "", ""),
        SAME_ANALYST("DataSample_SampleAnalysisAssignment_SameAnalyst", "", ""),
        SAMPLEANALYSIS_LOCKED("DataSample_SampleAnalysisAssignment_SampleAnalysisLocked", "", ""),
        READY_FOR_REVISION("DataSample_readyForRevision", "", ""),
        NOT_IMPLEMENTED("DataSample_notImplementedWhenSetReadyForRevisionNotSetToTrue", "NOT IMPLEMENTED YET WHEN SET READY FOR REVISION NOT TRUE YET", ""),
        RESULT_NOT_REVIEWABLE("DataSample_SampleAnalysisResultNotReviewable", "", ""),
        RULE_ANALYST_NOT_ASSIGNED("DataSample_SampleAnalysisRuleAnalystNotAssigned", "", ""),
        RULE_OTHERANALYSIS_ENTER_RESULT("DataSample_SampleAnalysisRuleOtherAnalystEnterResult", "", ""),
        ALREADY_REVIEWED("DataSample_SampleAnalysisAlreadyReviewed", "", ""),
        SAMPLE_ANALYSIS_CANNOT_BE_UNCANCELLED("DataSample_sampleAnalysisCannotBeUncanceled", "", ""),
        SAMPLE_ANALYSIS_CANNOT_BE_REMOVED("DataSample_sampleAnalysisCannotBeRemoved", "", "");

        private DataSampleAnalysisErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
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

    public enum DataSampleAnalysisResultBusinessRules implements EnumIntBusinessRules {
        STATUS_FIRST("sampleAnalysisResult_statusFirst", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleAnalysisResultStatusesByBusinessRules"),
        STATUS_ENTERED("sampleAnalysisResult_statusEntered", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleAnalysisResultStatusesByBusinessRules"),
        STATUS_REENTERED("sampleAnalysisResult_statusReEntered", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleAnalysisResultStatusesByBusinessRules"),
        STATUS_REVIEWED("sampleAnalysisResult_statusReviewed", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleAnalysisResultStatusesByBusinessRules"),
        STATUS_CANCELED("sampleAnalysisResult_statusCanceled", GlobalVariables.Schemas.DATA.getName(), null, null, '|', true, "data*sampleAnalysisResultStatusesByBusinessRules"),
        STATUS_SPEC_EVAL_NOSPEC("sampleAnalysisResult_statusSpecEvalNoSpec", GlobalVariables.Schemas.DATA.getName(), null, null, '|', false, null),
        STATUS_EVAL_NOSPECPARAMLIMIT("sampleAnalysisResult_statusSpecEvalNoSpecParamLimit", GlobalVariables.Schemas.DATA.getName(), null, null, '|', false, null),;

        private DataSampleAnalysisResultBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator, Boolean isOptional, String preReqs) {
            this.tagName = tgName;
            this.areaName = areaNm;
            this.valuesList = valuesList;
            this.allowMultiValue = allowMulti;
            this.multiValueSeparator = separator;
            this.isOptional = isOptional;
            this.preReqsBusRules = preReqs;
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
            return this.isOptional;
        }

        @Override
        public ArrayList<String[]> getPreReqs() {
            ArrayList<String[]> d = new ArrayList<String[]>();
            if (preReqsBusRules != null && preReqsBusRules.length() > 0) {
                String[] rulesArr = preReqsBusRules.split("\\|");
                for (String curRule : rulesArr) {
                    String[] curRuleArr = curRule.split("\\*");
                    if (curRuleArr.length == 2) {
                        d.add(curRuleArr);
                    }
                }
            }
            return d;
        }

        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;
        private final Boolean isOptional;
        private final String preReqsBusRules;

    }

    public enum DataSampleAnalysisResultErrorTrapping implements EnumIntMessages {
        NOT_FOUND("DataSampleAnalysisResult_SampleAnalysisResultNotFound", "", ""),
        RESULT_CANNOT_BE_CANCELLED("DataSampleAnalysisResult_resultCannotBeCanceled", "", ""),
        ANALYSIS_HAS_SOME_PARAMETERS("DataSampleAnalysisResult_analysisWithManyParameters", "", ""),
        RESULT_LOCKED("DataSampleAnalysisResult_SampleAnalysisResultLocked", "", ""),
        SAME_RESULT_VALUE("DataSampleAnalysisResult_SampleAnalysisResultSameValue", "", ""),
        CONVERSION_NOT_ALLOWED("DataSample_SampleAnalysisResult_ConversionNotAllowed", "", ""),
        CURRENTRESULT_ISEMPTY("DataSample_SampleAnalysisResult_CurrentResultIsEmpty", "", ""),
        NOT_NUMERIC_VALUE("DataSampleAnalysisResult_ValueNotNumericForQuantitativeParam", "", ""),
        SPECRULE_NOTIMPLEMENTED("DataSample_SampleAnalysisResult_SpecRuleNotImplemented", "", ""),
        FORRESULTUNCANCEL_STATUS_NOT_EXPECTED("DataSample_SampleUnCancel_StatusNotExpected", "", ""),
        FORRESULTUNREVIEW_STATUS_NOT_EXPECTED("DataSample_SampleUnReviewed_StatusNotExpected", "", ""),
        FORRESULTCANCELATION_STATUS_NOT_EXPECTED("DataSample_SampleAnalysisResultCancelation_StatusNotExpected", "", ""),;

        private DataSampleAnalysisResultErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
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

}
