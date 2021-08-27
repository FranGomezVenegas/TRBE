package functionaljavaa.samplestructure;

import org.json.simple.JSONArray;
import trazit.globalvariables.GlobalVariables;

public class DataSampleAnalysisEnums {
    
/*    static String SAMPLEANALYSIS_STATUS_FIRST_WHEN_NO_PROPERTY="NOT_STARTED";
    static String SAMPLEANALYSIS_STATUS_INCOMPLETE_WHEN_NO_PROPERTY="INCOMPLETE";
    static String SAMPLEANALYSIS_STATUS_COMPLETE_WHEN_NO_PROPERTY="COMPLETE";
    static String SAMPLEANALYSIS_STATUS_CANCELED_WHEN_NO_PROPERTY="CANCELED";
    static String SAMPLEANALYSIS_STATUS_REVIEWED_WHEN_NO_PROPERTY="REVIEWED";
*/    

    
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
    
}
