/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.inventory;

/**
 *
 * @author User
 */
public class InventoryGlobalVariables {
        public enum DataInvRetErrorTrapping{ 
        RECEPTION_FIELD_NOT_RETRIEVED("errorInCode, receptionFieldNotRetrieved", "", ""),
        ITEM_ALREADY_RECEIVED("retainRowAlreadyReceived", "", ""),
        NOT_ENOUGH_QUANTITY("NotEnoughQuantity", "", ""),
        CONVERSION_NOT_ALLOWED("_retainExtraction_ConversionNotAllowed", "", ""),
        MULTI_ITEMS_NOT_ALLOWED("MultiItemsNotAllowed", "There are more than one row pending of reception and this action should be performed one by one", "There are more than one row pending of reception and this action should be performed one by one"),
        CONVERTER_FALSE("ConverterFALSE", "", ""),
        ITEM_IS_LOCKED("retainRowIsLocked", "", ""),
        ;
        private DataInvRetErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
