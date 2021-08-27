/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class DataSampleEnums {
    public enum DataSampleErrorTrapping{ 
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
        SAMPLE_ALREADY_REVIEWED("sampleAlreadyReviewed", "", "")
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
    public enum sampleReviewReviewerModeValues{
        DISABLERULE("DISABLERULE", "Disable this rule", "Deshabilitar esta regla"),
        NOT_AUTHOR("NOT_AUTHOR", "Sample reviewer cannot be any result author", "El revisor de muestra no puede ser ningún autor de resultados"),
        NOT_TEST_REVIEWER("NOT_TEST_REVIEWER", "Sample reviewer cannot be any test reviewer", "El revisor de muestra no puede ser ningún revisor de ensayos"),
        ;
        private sampleReviewReviewerModeValues(String valor, String descEn, String descEs){
            this.value=valor;
            this.descriptionEn=descEn;
            this.descriptionEs=descEs;
        }       
        public String getValue(){return this.value;}
        public String getDescriptionEn(){return this.descriptionEn;}
        public String getDescriptionEs(){return this.descriptionEs;}
        public static JSONArray getValuesInOne(){
            JSONArray jArr=new JSONArray();
            for (sampleReviewReviewerModeValues obj: sampleReviewReviewerModeValues.values()){
                JSONObject jObj=new JSONObject();
                jObj.put("value", obj.value);
                jObj.put("description_en", obj.descriptionEn);
                jObj.put("description_es", obj.descriptionEs);
                jArr.add(jObj);
            }           
            return jArr;
        }
        private final String value;
        private final String descriptionEn;
        private final String descriptionEs;
    }    
}
