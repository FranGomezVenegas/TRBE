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
        MISSING_FIELDS_IN_TABLE("MissingFieldsInTable", "", ""),
        SPECIAL_FIELDS_IN_TABLE("SpecialFieldsInTable", "", ""),
        MISSING_AND_SPECIAL_FIELDS_IN_TABLE("MissingAndSpecialFieldsInTable", "", ""),        
        OBJECTOFARRAY_WRONGFORMAT("objectOfArray_wrongFormat", "", ""),
        GETCOLUMNFROM2D_COLNOTFOUND("LabPLANETArray_getColumnFromArray2D_ColNotFound", "", ""),
        NOT_IMPLEMENTED_YET("notImplementedYet", "", ""),
        VOLUME_CANNOTBE_NULL("volume_cannot_be_null", "", ""),
        VOLUME_NOT_ZERO_OR_NEGATIVE("volume_cannot_be_zero_or_negative", "", ""),
        PORTION_NOT_ZERO_OR_NEGATIVE("portion_cannot_be_zero_or_negative", "", ""),
        VALUE_EMPTY("valueEmpty", "", ""),
        VALUE_NOT_NUMERIC("valueNotNumeric", "", ""),
        AT_LEAST_ONE_VALUE_IS_NOT_NUMERIC("atleastOneValueIsNotNumeric", "", ""),
        DOT_IS_DECIMAL_SEPARATOR("dotIsTheDecimalsSeparator", "", ""),
        RECORD_ALREADY_EXISTS("recordAlreadyExists", "", ""),
        DATERANGE_WRONG_INTERVAL("dateRange_wrongInterval", "", ""),
        SPECIAL_FUNCTION_RETURNED_ERROR("SpecialFunctionReturnedERROR", "", ""),
        UNHANDLED_EXCEPTION("unHandledException", "<*1*>", "<*1*>"),
        TESTING_CONFIRM_DIALOG_VALIDATION_DISABLED("testingConfirmDialogValidationDisabled", "<*1*>", "<*1*>"),
        TESTING_CONFIRM_DIALOG_JUSTIF_PHRASE_REQUIRED("testingConfirmDialogJustifPhraseRequired", "<*1*>", "<*1*>"),
        TESTING_CONFIRM_DIALOG_WRONG_JUSTIF_PHRASE("testingConfirmDialogWrongJustifPhrase", "<*1*>", "<*1*>"),
        TESTING_CONFIRM_DIALOG_ESIGN_REQUIRED("testingConfirmDialogEsignRequired", "<*1*>", "<*1*>"),
        TESTING_CONFIRM_DIALOG_USER_CREDENTIALS_REQUIRED("testingConfirmDialogUserCredentialsRequired", "<*1*>", "<*1*>"),
        QUERIES_HAVE_NO_MSG_CODE("queriesHaveNoMsgCode", "<*1*>", "<*1*>"),
        
        ;
        private TrazitUtilitiesErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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

