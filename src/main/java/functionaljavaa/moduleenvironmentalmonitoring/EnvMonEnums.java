/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import trazit.enums.EnumIntMessages;

/**
 *
 * @author User
 */
public class EnvMonEnums {
    public enum EnvMonitErrorTrapping implements EnumIntMessages{ 
        LOGSAMPLE_PROGRAM_OR_LOCATION_NOTFOUND("EnvMonSampleProgramOrLocationNotFound", "Program <*1*> or location <*2*> not found", ""),
        PERSONAL_ANALYSIS_REQUIRED_NOT_DEFINED("EnvMonSamplePersonalAnalysisRequiredNotDefined", "", ""),
        MICROORGANISM_FOUND("EnvMonSampleMicroorganismNotFound", "",""),
        CULTURE_MEDIA_ALREADY_ASSIGNED("EnvMonSampleCultureMediaAlreadyAssigned", "",""),  
        NOT_AVAILABLEFORUSE_CULTURE_MEDIA_LOT("EnvMonNotAvailableForUseCultureMediaLot", "","")
        ;
        private EnvMonitErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    
}
