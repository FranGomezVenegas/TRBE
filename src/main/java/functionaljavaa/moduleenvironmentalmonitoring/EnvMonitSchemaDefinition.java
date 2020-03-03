/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitProcedure;
import databases.Rdbms;
import static functionaljavaa.requirement.ProcedureDefinitionToInstance.JSON_LABEL_FOR_NUM_RECORDS_IN_DEFINITION;
import static functionaljavaa.requirement.ProcedureDefinitionToInstance.SCHEMA_AUTHORIZATION_ROLE;
import static functionaljavaa.requirement.RequirementLogFile.requirementsLogEntry;
import javax.sql.rowset.CachedRowSet;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Administrator
 */
public class EnvMonitSchemaDefinition {

    /**
     *
     * @return
     */
    public static JSONObject createPlatformSchemas(){
    
        Rdbms.stablishDBConection();
        JSONObject jsonObj = new JSONObject();
        
        String methodName = "createDataBaseSchemas";       
        String newEntry = "";
        String[] schemaNames = new String[]{LPPlatform.SCHEMA_APP, LPPlatform.SCHEMA_REQUIREMENTS, LPPlatform.SCHEMA_CONFIG};
         jsonObj.put(JSON_LABEL_FOR_NUM_RECORDS_IN_DEFINITION, schemaNames.length);     
        for (String configSchemaName:schemaNames){
            JSONArray jsSchemaArr = new JSONArray();
            jsSchemaArr.add(configSchemaName);
            requirementsLogEntry("", methodName, configSchemaName,2);
            
            String configSchemaScript = "CREATE SCHEMA "+configSchemaName+"  AUTHORIZATION "+SCHEMA_AUTHORIZATION_ROLE+";"+
                    " GRANT ALL ON SCHEMA "+configSchemaName+" TO "+SCHEMA_AUTHORIZATION_ROLE+ ";";     
            CachedRowSet prepRdQuery = Rdbms.prepRdQuery(configSchemaScript, new Object[]{});
            
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
     * @param schemaPrefix
     * @param fieldsName
     * @return
     */
    public static final  JSONObject createDBTables(String schemaPrefix, String[] fieldsName){
        JSONObject jsonObj = new JSONObject();        
        String tblCreateScript="";
        
        tblCreateScript=TblsEnvMonitProcedure.ProgramCorrectiveAction.createTableScript(schemaPrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsEnvMonitProcedure.ProgramCorrectiveAction", tblCreateScript);
        
        return jsonObj;    }
}
