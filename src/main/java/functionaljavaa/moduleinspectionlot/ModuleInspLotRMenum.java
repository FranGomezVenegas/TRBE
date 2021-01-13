/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleinspectionlot;

/**
 *
 * @author User
 */
public class ModuleInspLotRMenum {
    public enum DataLotProperties{ 
        SUFFIX_STATUS_FIRST ("_statusFirst", "First status, to be concatenated to the entity name, example: sample_statusFirst, program_statusFirst etc...", "One of the given statuses"),
        SUFFIX_LOTSTRUCTURE ("_lotStructure", "TBD", "TBD"),
/*        SAMPLE_STATUS_FIRST ("sample_statusFirst","", "One of the given statuses"),
        SAMPLE_STATUS_RECEIVED ("sample_statusReceived", "", "One of the given statuses"),
        SAMPLE_STATUS_INCOMPLETE ("sample_statusIncomplete", "", "One of the given statuses"),
        SAMPLE_STATUS_COMPLETE ("sample_statusComplete", "", "One of the given statuses"),
        SAMPLEALIQUOTING_VOLUME_REQUIRED ("sampleAliquot_volumeRequired", "TBD", "TBD"),
        SAMPLEASUBLIQUOTING_VOLUME_REQUIRED ("sampleSubAliquot_volumeRequired", "TBD", "TBD"), */       
        ;
        private DataLotProperties(String pName, String descr, String possValues){
            this.propertyName=pName;
            this.description=descr;
            this.possibleValues=possValues;
        }
        public String getPropertyName(){return this.propertyName;}
        public String getDescription(){return this.description;}
        public String getPossibleValues(){return this.possibleValues;}
    
        private final String propertyName;
        private final String description;
        private final String possibleValues;
    }
    public enum DataInspLotErrorTrapping{ 
        SAMPLE_NOT_FOUND ("SampleNotFound", "", ""),
        ERROR_INSERTING_INSPLOT_RECORD("errorInsertingInspLotRecord", "", ""),
        MISSING_MANDATORY_FIELDS("MissingMandatoryFields", "", ""),
        MISSING_CONFIG_CODE("MissingConfigCode", "", ""),        
        MISSING_SPEC_CONFIG_CODE("MissingSpecConfigCode", "Spec Config code <*1*> version <*2*> Not found for the procedure <*3*>", ""),        
        SAMPLE_ALREADY_RECEIVED("SampleAlreadyReceived", "", ""),
        SAMPLE_NOT_REVIEWABLE("SampleNotReviewable", "", ""),
        VOLUME_SHOULD_BE_GREATER_THAN_ZERO("sampleAliquoting_volumeCannotBeNegativeorZero", "", ""),
        ALIQUOT_CREATED_BUT_ID_NOT_GOT("AliquotCreatedButIdNotGotToContinueApplyingAutomatisms", "Object created but aliquot id cannot be get back to continue with the logic", ""),
        SAMPLEASUBLIQUOTING_VOLUME_AND_UOM_REQUIRED ("sampleSubAliquoting_volumeAndUomMandatory", "", ""),        
        ;
        private DataInspLotErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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

    public enum DataInspLotCertificateStatuses{NEW, DRAFT, READY_FOR_APPROVAL, UNDER_APPROVAL, APPROVED, SENT}
    
    public enum DataInspLotCertificateTrackActions{PRINT, OPEN, CHANGE_STATUS, SENT}
}
