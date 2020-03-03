/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

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
public class DbObjects {
    private DbObjects() {    throw new IllegalStateException("Utility class");  }
    /**
     *
     */
    public static final String POSTGRES_DB_OWNER="labplanet";

    /**
     *
     */
    public static final String POSTGRES_DB_TABLESPACE="pg_default";
    
    /**
     *
     * @return
     */
    public static JSONObject createPlatformSchemas(){
        String[] schemaNames = new String[]{LPPlatform.SCHEMA_APP, LPPlatform.SCHEMA_REQUIREMENTS, LPPlatform.SCHEMA_CONFIG};
        return createSchemas(schemaNames);
     }    

    public static JSONObject createModuleSchemas(String schemaPrefix){
        JSONObject jsonObj = new JSONObject();
        String tblCreateScript="";
        String[] schemaNames = new String[]{LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), 
            LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), 
            LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG_AUDIT), LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA_AUDIT), 
            LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_PROCEDURE)};        
        jsonObj=createSchemas(schemaNames);

        tblCreateScript=TblsProcedure.PersonProfile.createTableScript(schemaPrefix, new String[]{""});
        //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsProcedure.PersonProfile", tblCreateScript);

        tblCreateScript=TblsProcedure.ProcedureInfo.createTableScript(schemaPrefix, new String[]{""});
        //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsTblsProcedureReqs.ProcedureInfo", tblCreateScript);

        tblCreateScript=TblsProcedure.ProcedureEvents.createTableScript(schemaPrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsProcedure.ProcedureEvents", tblCreateScript);
                
        return jsonObj;
     }    

    private static JSONObject createSchemas(String[] schemasNames){
    
        Rdbms.stablishDBConection();
        JSONObject jsonObj = new JSONObject();
        
        String methodName = "createDataBaseSchemas";       
        jsonObj.put(JSON_LABEL_FOR_NUM_RECORDS_IN_DEFINITION, schemasNames.length);     
        for (String configSchemaName:schemasNames){
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
     * @param tableName
     * @param fieldsName
     * @return
     */
    public static final  JSONObject createDBTables(String tableName, String[] fieldsName){
        JSONObject jsonObj = new JSONObject();        
        
        String tblCreateScript=TblsApp.AppSession.createTableScript(new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsApp.AppSession", tblCreateScript);
        
        tblCreateScript=TblsApp.UserProcess.createTableScript(new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsApp.UserProcess", tblCreateScript);

        tblCreateScript=TblsApp.Users.createTableScript(new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsApp.Users", tblCreateScript);

        tblCreateScript=TblsApp.HolidaysCalendar.createTableScript(new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsApp.HolidaysCalendar", tblCreateScript);

        tblCreateScript=TblsApp.HolidaysCalendarDate.createTableScript(new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsApp.HolidaysCalendarDate", tblCreateScript);
        
        tblCreateScript=TblsAppConfig.Person.createTableScript(new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsAppConfig.Person", tblCreateScript);
        
        tblCreateScript=TblsReqs.ProcedureInfo.createTableScript(new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureInfo", tblCreateScript);

        tblCreateScript=TblsReqs.ProcedureRoles.createTableScript(new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureRoles", tblCreateScript);

        tblCreateScript=TblsReqs.ProcedureSopMetaData.createTableScript(new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureSopMetaData", tblCreateScript);
        
        tblCreateScript=TblsReqs.ProcedureUserRequirements.createTableScript(new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureUserRequirements", tblCreateScript);

        tblCreateScript=TblsReqs.ProcedureUserRole.createTableScript(new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureUserRole", tblCreateScript);

        tblCreateScript=TblsReqs.ProcedureUsers.createTableScript(new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureUsers", tblCreateScript);
        
        return jsonObj;
     }    
    
}
