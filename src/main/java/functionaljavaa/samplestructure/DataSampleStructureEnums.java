/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import org.json.simple.JSONArray;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class DataSampleStructureEnums {
    public enum DataSampleErrorTrapping{ 
        SETSAMPLINGDATE_NOT_ALLOW_CHANGE_PREVIOUS_VALUE ("SetSamplingDate_notAllowChangePreviousValue", "", ""),
        SAMPLING_DATE_CHANGED ("SamplingDateChangedSuccessfully", "", ""),
        SAMPLE_RECEPTION_COMMENT_ADDED ("SampleReceptionCommentAdd", "", ""),
        SAMPLE_RECEPTION_COMMENT_REMOVED ("SampleReceptionCommentRemoved", "", ""),
        SAMPLE_NOT_FOUND ("SampleNotFound", "", ""),
        ERROR_INSERTING_SAMPLE_RECORD("errorInsertingSampleRecord", "", ""),
        SAMPLE_STATUS_MANDATORY("SampleStatusMandatory", "", ""),
        MISSING_MANDATORY_FIELDS("MissingMandatoryFields", "", ""),
        MISSING_CONFIG_CODE("MissingConfigCode", "", ""),        
        MISSING_SPEC_CONFIG_CODE("MissingSpecConfigCode", "Spec Config code <*1*> version <*2*> Not found for the procedure <*3*>", ""),        
        SAMPLE_ALREADY_RECEIVED("SampleAlreadyReceived", "", ""),
        SAMPLE_NOT_REVIEWABLE("SampleNotReviewable", "", ""),
        VOLUME_SHOULD_BE_GREATER_THAN_ZERO("sampleAliquoting_volumeCannotBeNegativeorZero", "", ""),
        ALIQUOT_CREATED_BUT_ID_NOT_GOT("AliquotCreatedButIdNotGotToContinueApplyingAutomatisms", "Object created but aliquot id cannot be get back to continue with the logic", ""),
        SAMPLEASUBLIQUOTING_VOLUME_AND_UOM_REQUIRED ("sampleSubAliquoting_volumeAndUomMandatory", "", ""),        
        SAMPLE_FIELDNOTFOUND("SampleFieldNotFound", "", ""),
        READY_FOR_REVISION("readyForRevision", "", ""),
        NOT_IMPLEMENTED("notImplementedWhenSetReadyForRevisionNotSetToTrue", "NOT IMPLEMENTED YET WHEN SET READY FOR REVISION NOT TRUE YET", ""),
        SAMPLE_ALREADY_REVIEWED("sampleAlreadyReviewed", "", ""),
        SAMPLE_RULES_NOT_FOUND("DataSample_SampleRulesNotFound", "", ""),
        SAMPLE_ALREADY_READY_FOR_REVISION("alreadyReadyForRevision", "", "")
        ;
        private DataSampleErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        public String getErrorCode(){return this.errorCode;}
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }

    public enum DataSampleBusinessRules{ 
        STATUSES ("sample_statuses", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUSES_LABEL_EN ("sample_statuses_label_en", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUSES_LABEL_ES ("sample_statuses_label_es", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SUFFIX_STATUS_FIRST ("_statusFirst", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_REVIEWED ("sample_statusReviewed", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_CANCELED ("sample_statusCanceled", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),        
        SUFFIX_SAMPLESTRUCTURE ("_sampleStructure", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SAMPLE_STATUS_FIRST ("sample_statusFirst", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SAMPLE_STATUS_RECEIVED ("sample_statusReceived", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SAMPLE_STATUS_INCOMPLETE ("sample_statusIncomplete", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SAMPLE_STATUS_COMPLETE ("sample_statusComplete", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SAMPLE_STATUS_REVIEWED ("sample_statusReviewed", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SAMPLE_STATUS_CANCELED ("sample_statusCanceled", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SAMPLEALIQUOTING_VOLUME_REQUIRED ("sampleAliquot_volumeRequired", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SAMPLEASUBLIQUOTING_VOLUME_REQUIRED ("sampleSubAliquot_volumeRequired", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SAMPLE_GENERICAUTOAPPROVEENABLED("sampleGenericAutoApproveEnabled", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        ;
        private DataSampleBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        public JSONArray getValuesList(){return this.valuesList;}
        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        
    }

    public enum DataSampleAnalysisBusinessRules{
        MARK_READYFORREVISION_WHENALLRESULTSENTERED("revisionSampleAnalysis_markAsReadyForRevisionWhenAllResultsEntered", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        SAMPLEANALYSIS_STATUSFIRST("sampleAnalysis_statusFirst", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SAMPLEANALYSIS_STATUSINCOMPLETE("sampleAnalysis_statusIncomplete", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SAMPLEANALYSIS_STATUSCOMPLETE("sampleAnalysis_statusComplete", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),

        SAMPLEANALYSIS_STATUSCANCELED("sampleAnalysis_statusCanceled", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SAMPLEANALYSIS_STATUSREVIEWED("sampleAnalysis_statusReviewed", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SAMPLEANALYSIS_ANALYSTASSIGNMENTMODE("sampleAnalysis_analystAssigmentMode", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        SAMPLEANALYSIS_ANALYSTASSIGNMENTMODES("sampleAnalysis_analystAssigmentModes", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        REVISION_TESTINGGROUP_REQUIRED("revisionTestinGroupRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),

        SAMPLEANALYSIS_GENERICAUTOAPPROVEENABLED("sampleAnalysisGenericAutoApproveEnabled", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        ;
        private DataSampleAnalysisBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
        }             
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        public JSONArray getValuesList(){return this.valuesList;}
        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;       
    }
    public enum DataSampleAnalysisErrorTrapping{ 
        MARK_READYFORREVISION_WHENALLRESULTSENTERED_NOTACTIVE_SUCCESS("markAsReadyForRevisionWhenAllResultsEnteredNotActive", "", ""),
        SAMPLE_ANALYSIS_ADDED_SUCCESS("SampleAnalysisAddedSuccessfully", "", ""),
        ALREADY_READYFORREVISION("alreadyReadyForRevision", "", ""),
        ADD_TO_SAMPLE_MISSING_MANDATORY_FIELDS("DataSample_sampleAnalaysisAddToSample_MissingMandatoryFields", "", ""),
        ANALYSISMETHOD_PARAMSNOTFOUND("DataSample_AnalysisMethodParamsNotFound", "", ""),
        PENDING_REVISION("SampleAnalysisPendingRevision", "", ""),
        SPECLIMIT_NOTFOUND("DataSample_SpecLimitNotFound", "", ""),
        SPECRULE_NOTFOUND("DataSample_SpecRuleNotFound", "", ""),
        SAMPLEANALYSISASSIGNED_MODENOTRECOGNIZED("DataSample_SampleAnalysisAssignment_AssignmentModeNotRecognized", "", ""),
        SAMPLEANALYSISASSIGNED_MODENOTIMPLEMENTED("DataSample_SampleAnalysisAssignment_AssignmentModeNotImplemented", "", ""),
        SAMPLEANALYSISASSIGNED_SUCCESS("DataSample_SampleAnalysisAssignment_Successfully", "", ""),
        DB_RETURNEDERROR("DataSample_SampleAnalysisAssignment_databaseReturnedError", "", ""),
        ESCAPE_UNHANDLEDEXCEPTION("DataSample_SampleAnalysisAssignment_EscapeByUnhandledException", "", ""),
        SAMPLEANALYSIS_NOTFOUND("DataSample_SampleAnalysisNotFound", "", ""),
        SAME_ANALYST("DataSample_SampleAnalysisAssignment_SameAnalyst", "", ""),
        SAMPLEANALYSIS_LOCKED("DataSample_SampleAnalysisAssignment_SampleAnalysisLocked", "", ""),
        READY_FOR_REVISION("readyForRevision", "", ""),
        NOT_IMPLEMENTED("notImplementedWhenSetReadyForRevisionNotSetToTrue", "NOT IMPLEMENTED YET WHEN SET READY FOR REVISION NOT TRUE YET", ""),
        RESULT_NOT_REVIEWABLE("DataSample_SampleAnalysisResultNotReviewable", "", ""),
        RULE_ANALYST_NOT_ASSIGNED("DataSample_SampleAnalysisRuleAnalystNotAssigned", "", ""),
        RULE_OTHERANALYSIS_ENTER_RESULT("DataSample_SampleAnalysisRuleOtherAnalystEnterResult", "", ""),

        ;
        
        private DataSampleAnalysisErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        public String getErrorCode(){return this.errorCode;}
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }

    public enum DataSampleAnalysisResultBusinessRules{        
        STATUS_FIRST("sampleAnalysisResult_statusFirst", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_ENTERED("sampleAnalysisResult_statusEntered", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_REENTERED("sampleAnalysisResult_statusReEntered", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_REVIEWED("sampleAnalysisResult_statusReviewed", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_CANCELED("sampleAnalysisResult_statusCanceled", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_SPEC_EVAL_NOSPEC("sampleAnalysisResult_statusSpecEvalNoSpec", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_EVAL_NOSPECPARAMLIMIT("sampleAnalysisResult_statusSpecEvalNoSpecParamLimit", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
         
        ;
        private DataSampleAnalysisResultBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        public JSONArray getValuesList(){return this.valuesList;}
        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;       
    }
    public enum DataSampleAnalysisResultErrorTrapping{ 
        NOT_FOUND("DataSampleAnalysisResult_SampleAnalysisResultNotFound", "", ""),
        RESULT_CANNOT_BE_CANCELLED("DataSampleAnalysisResult_resultCannotBeCanceled", "", ""),
        ANALYSIS_HAS_SOME_PARAMETERS("DataSampleAnalysisResult_analysisWithManyParameters", "", ""),
        RESULT_LOCKED("DataSampleAnalysisResult_SampleAnalysisResultLocked", "", ""),
        SAME_RESULT_VALUE("DataSampleAnalysisResult_SampleAnalysisResultSameValue", "", ""),
        CONVERSION_NOT_ALLOWED("DataSample_SampleAnalysisResult_ConversionNotAllowed", "", ""),
        CONVERTER_RETURNED_FALSE("DataSample_SampleAnalysisResult_ConverterFALSE", "", ""),
        NOT_NUMERIC_VALUE("DataSampleAnalysisResult_ValueNotNumericForQuantitativeParam", "", ""),
        SPECRULE_NOTIMPLEMENTED("DataSample_SampleAnalysisResult_SpecRuleNotImplemented", "", ""),
        FORRESULTUNCANCEL_STATUS_NOT_EXPECTED("DataSample_SampleUnCancel_StatusNotExpected", "", ""),
        FORRESULTUNREVIEW_STATUS_NOT_EXPECTED("DataSample_SampleUnReviewed_StatusNotExpected", "", ""),
        FORRESULTCANCELATION_STATUS_NOT_EXPECTED("DataSample_SampleAnalysisResultCancelation_StatusNotExpected", "", ""),
        ;
        
        private DataSampleAnalysisResultErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        public String getErrorCode(){return this.errorCode;}
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    
}
