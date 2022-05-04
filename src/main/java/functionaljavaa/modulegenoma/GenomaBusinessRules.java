/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import functionaljavaa.parameter.Parameter;
import java.util.ArrayList;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public final class GenomaBusinessRules {
    private GenomaBusinessRules() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public enum GenomaBusnessRules implements EnumIntBusinessRules{
        SUFFIX_ACTIVEONCREATION("_activeOnCreation", GlobalVariables.Schemas.DATA.getName(), null, null, '|', null, null),
        SUFFIX_SPECIALFIELDS_LOCKEDFORPROJECTUPDATEENDPOINT("_specialFieldsLockedForProjectUpdateEndPoint", GlobalVariables.Schemas.DATA.getName(), null, null, '|', null, null),
        ;
        private GenomaBusnessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator
        , Boolean isOpt, ArrayList<String[]> preReqs){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
            this.isOptional=isOpt;
            this.preReqs=preReqs;
        }       
        @Override        public String getTagName(){return this.tagName;}
        @Override        public String getAreaName(){return this.areaName;}
        @Override        public JSONArray getValuesList(){return this.valuesList;}
        @Override        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        @Override        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        @Override        public Boolean getIsOptional() {return isOptional;}
        @Override        public ArrayList<String[]> getPreReqs() {return this.preReqs;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        
        private final Boolean isOptional;
        private final ArrayList<String[]> preReqs;
    }    
    public static Boolean activateOnCreation(String schemaSuffix, String tableName){
if (1==1) return true;        
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, schemaSuffix);
        
        String propertyEntryValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, GenomaBusnessRules.SUFFIX_ACTIVEONCREATION.getAreaName(), GenomaBusnessRules.SUFFIX_ACTIVEONCREATION.getTagName());        
        if (propertyEntryValue.length()==0) return false;
        return ("YES".equalsIgnoreCase(propertyEntryValue)) || ("SI".equalsIgnoreCase(propertyEntryValue));
    }

    public static Object[] specialFieldsInUpdateArray(String schemaSuffix, String tableName, String[] fieldsToCheck){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, schemaSuffix);
        
        String propertyEntryValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, GenomaBusnessRules.SUFFIX_SPECIALFIELDS_LOCKEDFORPROJECTUPDATEENDPOINT.getAreaName(), GenomaBusnessRules.SUFFIX_SPECIALFIELDS_LOCKEDFORPROJECTUPDATEENDPOINT.getTagName());        

        if (propertyEntryValue.length()==0) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "", null);
        String[] propertyEntryValueArr=propertyEntryValue.split("\\|");
        StringBuilder specialFieldsPresent=new StringBuilder();
        for (String curFldToCheck: fieldsToCheck){
            if ( LPArray.valueInArray(propertyEntryValueArr, curFldToCheck) ) {
                if (specialFieldsPresent.length()>0) specialFieldsPresent.append(", ");
                specialFieldsPresent.append(curFldToCheck);
            }                  
        }
        if (specialFieldsPresent.length()>0) return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Special fields (<*1*>) are present and they are not allowed by the generic update action.", new Object[]{specialFieldsPresent});
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "", null);
    }
    
}
