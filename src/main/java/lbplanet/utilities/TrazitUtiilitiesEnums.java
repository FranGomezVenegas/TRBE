/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import trazit.enums.EnumIntMessages;

/**
 *
 * @author User
 */
public class TrazitUtiilitiesEnums {
    public enum TrazitUtilitiesErrorTrapping implements EnumIntMessages{ 
        FIELDS_DUPLICATED("fieldsDuplicated", "There are duplicated fields", "Hay campos por duplicado"),
        ERRORTRAPPING_EXCEPTION("LabPLANETPlatform_SpecialFunctionReturnedEXCEPTION", "", ""),
        ARRAYS_DIFFERENT_SIZE("DataSample_FieldArraysDifferentSize", "", ""),
        OBJECTOFARRAY_WRONGFORMAT("objectOfArray_wrongFormat", "", ""),
        GETCOLUMNFROM2D_COLNOTFOUND("LabPLANETArray_getColumnFromArray2D_ColNotFound", "", ""),
        NOT_IMPLEMENTED_YET("notImplementedYet", "", ""),
        VOLUME_CANNOTBE_NULL("volume_cannot_be_null", "", ""),
        VOLUME_NOT_ZERO_OR_NEGATIVE("volume_cannot_be_zero_or_negative", "", ""),
        PORTION_NOT_ZERO_OR_NEGATIVE("portion_cannot_be_zero_or_negative", "", ""),
        VALUE_EMPTY("valueEmpty", "", ""),
        VALUE_NOT_NUMERIC("valueNotNumeric", "", ""),
        COMMA_IS_DECIMAL_SEPARATOR("commaIsTheDecimalsSeparator", "", ""),
        DATERANGE_WRONG_INTERVAL("dateRange_wrongInterval", "", ""),
        SPECIAL_FUNCTION_RETURNED_ERROR("SpecialFunctionReturnedERROR", "", ""),
        CORRECT("correct", "", "")
        ;
        private TrazitUtilitiesErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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

