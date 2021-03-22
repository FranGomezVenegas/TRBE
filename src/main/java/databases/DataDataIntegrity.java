/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import lbplanet.utilities.LPPlatform;
import functionaljavaa.parameter.Parameter;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class DataDataIntegrity {
    

    /**
     *
     * @param schemaName
     * @param tableName
     * @param actionName
     * @return Array of strings with the table mandatory fields
     */
    public String[] getTableMandatoryFields(String schemaName, String tableName, String actionName){
        
        String[] myMandatoryFields = new String[0];
        String schemaDataName = LPPlatform.buildSchemaName(schemaName, GlobalVariables.Schemas.DATA.getName());
        
        String propertyEntryName = tableName+"_mandatoryFields"+actionName;        
        String propertyEntryValue = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), propertyEntryName);        
        if (propertyEntryValue.length()>0){
            myMandatoryFields = propertyEntryValue.split("\\|");
        }                  
        return myMandatoryFields;
    }

    /**
     * The system provides the ability to decide which are the default values for certain table fields by action name.
     * To enable it one new propertiy in the way of "tableName+'_fieldsDefaultValues'+actionName" should be added.
     * in procedure field for the given procedure.
     * @param schemaName
     * @param tableName
     * @param actionName
     * @return Array of strings with the table fields default values for a given table and action.
     */
    public String[] getTableFieldsDefaulValues(String schemaName, String tableName, String actionName){
        String[] myMandatoryFields = new String[0];
        String schemaDataName = LPPlatform.buildSchemaName(schemaName, GlobalVariables.Schemas.DATA.getName());
        
        String propertyEntryName = tableName+"_fieldsDefaultValues"+actionName;        
        
        String propertyEntryValue = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), propertyEntryName);        
        if (propertyEntryValue.length()>0){
            myMandatoryFields = propertyEntryValue.split("\\|");
        }                  
        return myMandatoryFields;
    }    

    /**
     *
     * @param schemaName
     * @param tableName
     * @param actionName
     * @return Array of strings with the special fields for a given table.
     */
    public String[] getStructureSpecialFields(String schemaName, String tableName){
        String[] myMandatoryFields = new String[0];
        String schemaDataName = LPPlatform.buildSchemaName(schemaName, GlobalVariables.Schemas.DATA.getName());
        
        String propertyEntryName = tableName+"_specialFields";        
        
        String propertyEntryValue = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), propertyEntryName);        
        if (propertyEntryValue.length()>0){
            myMandatoryFields = propertyEntryValue.split("\\|");
        }                  
        return myMandatoryFields;
    }        
    
    /**
     *
     * @param schemaName
     * @param tableName
     * @return Array of string with the functions to be invoked for the special fields.
     */
    public String[] getStructureSpecialFieldsFunction(String schemaName, String tableName){
        String[] myMandatoryFields = new String[0];
        String schemaDataName = LPPlatform.buildSchemaName(schemaName, GlobalVariables.Schemas.DATA.getName());
        
        String propertyEntryName = tableName+"_specialFieldsFunction";        
        
        String propertyEntryValue = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), propertyEntryName);        
        if (propertyEntryValue.length()>0){
            myMandatoryFields = propertyEntryValue.split("\\|");
        }                  
        return myMandatoryFields;
    }      
}
