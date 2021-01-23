/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class RequirementDeployment {
/**
     *
     * @param procedure
     * @param pVersion
     * @return
     */
    @SuppressWarnings("ConvertToTryWithResources")
    public String procedureDeployment ( String procedure, Integer pVersion)  {    
        
        String schemaNamePrefix = "genoma-1";
        
        String[] schemaNames = new String[0];        
        schemaNames = LPArray.addValueToArray1D(schemaNames, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
        schemaNames = LPArray.addValueToArray1D(schemaNames, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));        
        schemaNames = LPArray.addValueToArray1D(schemaNames, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA_AUDIT.getName())); 
        schemaNames = LPArray.addValueToArray1D(schemaNames, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName())); 

        //createDBSchemas(schemaNamePrefix, schemaNames);
        //createDBSchemasTable(schemaNamePrefix, schemaNames);
       
        return "";
    }
}
