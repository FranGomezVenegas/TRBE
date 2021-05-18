/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import functionaljavaa.parameter.Parameter;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public final class GenomaBusinessRules {
    private GenomaBusinessRules() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public enum GenomaBusnessRules{
        SUFFIX_ACTIVEONCREATION("_activeOnCreation", GlobalVariables.Schemas.DATA.getName()),
        SUFFIX_SPECIALFIELDS_LOCKEDFORPROJECTUPDATEENDPOINT("_specialFieldsLockedForProjectUpdateEndPoint", GlobalVariables.Schemas.DATA.getName()),
        
        
        ;
        private GenomaBusnessRules(String tgName, String areaNm){
            this.tagName=tgName;
            this.areaName=areaNm;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        
        private final String tagName;
        private final String areaName;
    }
    
    public static Boolean activateOnCreation(String schemaSuffix, String tableName){
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

        if (propertyEntryValue.length()==0) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "", null);
        String[] propertyEntryValueArr=propertyEntryValue.split("\\|");
        StringBuilder specialFieldsPresent=new StringBuilder();
        for (String curFldToCheck: fieldsToCheck){
            if ( LPArray.valueInArray(propertyEntryValueArr, curFldToCheck) ) {
                if (specialFieldsPresent.length()>0) specialFieldsPresent.append(", ");
                specialFieldsPresent.append(curFldToCheck);
            }                  
        }
        if (specialFieldsPresent.length()>0) return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "Special fields (<*1*>) are present and they are not allowed by the generic update action.", new Object[]{specialFieldsPresent});
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "", null);
    }
    
}
