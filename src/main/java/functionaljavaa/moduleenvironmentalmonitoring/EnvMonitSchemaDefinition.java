/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import databases.TblsProcedure;
import databases.Rdbms;
import static functionaljavaa.requirement.ProcedureDefinitionToInstance.JSON_LABEL_FOR_NUM_RECORDS_IN_DEFINITION;
import static functionaljavaa.requirement.ProcedureDefinitionToInstance.SCHEMA_AUTHORIZATION_ROLE;
import static functionaljavaa.requirement.RequirementLogFile.requirementsLogEntry;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public final class EnvMonitSchemaDefinition {
    private EnvMonitSchemaDefinition() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}

    /**
     *
     * @return
     */
    public static JSONObject createPlatformSchemas(){
    
        Rdbms.stablishDBConection();
        JSONObject jsonObj = new JSONObject();
        
        String methodName = "createDataBaseSchemas";       
        String[] schemaNames = new String[]{GlobalVariables.Schemas.APP.getName(), GlobalVariables.Schemas.REQUIREMENTS.getName(), GlobalVariables.Schemas.CONFIG.getName()};
         jsonObj.put(JSON_LABEL_FOR_NUM_RECORDS_IN_DEFINITION, schemaNames.length);     
        for (String configSchemaName:schemaNames){
            JSONArray jsSchemaArr = new JSONArray();
            jsSchemaArr.add(configSchemaName);
            requirementsLogEntry("", methodName, configSchemaName,2);
            
            String configSchemaScript = "CREATE SCHEMA "+configSchemaName+"  AUTHORIZATION "+SCHEMA_AUTHORIZATION_ROLE+";"+
                    " GRANT ALL ON SCHEMA "+configSchemaName+" TO "+SCHEMA_AUTHORIZATION_ROLE+ ";";     
            Rdbms.prepRdQuery(configSchemaScript, new Object[]{});
            
            // La idea es no permitir ejecutar prepUpQuery directamente, por eso es privada y no publica.            
                //Integer prepUpQuery = Rdbms.prepUpQuery(configSchemaScript, new Object[0]);
                //String diagnosesForLog = (prepUpQuery==-1) ? JSON_LABEL_FOR_NO : JSON_LABEL_FOR_YES;
                //jsonObj.put("Schema Created?", diagnosesForLog);            
            jsonObj.put(configSchemaName, jsSchemaArr);
        }
        return jsonObj;
     }    

    /**
     *
     * @param fieldsName
     * @return
     */
    public static final  JSONObject createDBTables(String[] fieldsName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        JSONObject jsonObj = new JSONObject();        
        String tblCreateScript="";
        
        tblCreateScript=TblsProcedure.ProgramCorrectiveAction.createTableScript(procInstanceName, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsProcedure.ProgramCorrectiveAction", tblCreateScript);
        
        return jsonObj;    }
}
