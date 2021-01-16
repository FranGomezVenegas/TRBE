/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import databases.Token;
import functionaljavaa.parameter.Parameter;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class GenomaBusinessRules {
    
    public static Boolean activateOnCreation(String schemaSuffix, String tableName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, schemaSuffix);
        
        String propertyEntryName = tableName+"_activeOnCreation";        
        String propertyEntryValue = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), propertyEntryName);        
        if (propertyEntryValue.length()==0) return false;
        return ("YES".equalsIgnoreCase(propertyEntryValue)) || ("SI".equalsIgnoreCase(propertyEntryValue));
    }

    public static Object[] specialFieldsInUpdateArray(String schemaSuffix, String tableName, String[] fieldsToCheck){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, schemaSuffix);
        
        String propertyEntryName = tableName+"_specialFieldsLockedForProjectUpdateEndPoint";        
        String propertyEntryValue = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), propertyEntryName);        
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
