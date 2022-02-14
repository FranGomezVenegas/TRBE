/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import static databases.DbObjects.SchemaActions.CREATE;
import static databases.DbObjects.SchemaActions.DELETE;
import databases.TblsData.TablesData;
import functionaljavaa.datatransfer.FromInstanceToInstance;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.requirement.ProcedureDefinitionToInstance.JsonTags;
import static functionaljavaa.requirement.ProcedureDefinitionToInstance.SCHEMA_AUTHORIZATION_ROLE;
import static functionaljavaa.requirement.RequirementLogFile.requirementsLogEntry;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import java.util.ResourceBundle;
import static trazit.enums.deployrepository.DeployTables.createTableScript;


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
    enum SchemaActions{CREATE, DELETE};
    /**
     *
     */
    public static final String POSTGRES_DB_TABLESPACE="pg_default";
    
    /**
     *
     * @return one Json Object with the log built after running the script for the platform instance creation.
     */
    public static JSONObject createPlatformSchemasAndBaseTables(String platformName){
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());        
        String[][] tablesToTransferData=new String[][]{
            {GlobalVariables.Schemas.CONFIG.getName(), TblsCnfg.UnitsOfMeasurement.TBL.getName(), dbTrazitModules}
        };
        
        String[] schemaNames = new String[]{GlobalVariables.Schemas.APP_AUDIT.getName(),
            GlobalVariables.Schemas.CONFIG.getName(), GlobalVariables.Schemas.REQUIREMENTS.getName(), 
            GlobalVariables.Schemas.APP.getName()};
        String tblCreateScript="";
        JSONObject jsonObj=new JSONObject();
        
        tblCreateScript = TblsCnfg.zzzDbErrorLog.createTableScript("", new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        //jsonObj.put("TblsCnfg.zzzDbErrorLog", tblCreateScript);

        tblCreateScript=TblsCnfg.zzzPropertiesMissing.createTableScript("", new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        //jsonObj.put("TblsCnfg.zzzPropertiesMissing", tblCreateScript);
        
        jsonObj=createSchemas(schemaNames, platformName);
        tblCreateScript=createTableScript(TblsApp.TablesApp.APP_SESSION);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsApp.AppSession", tblCreateScript);

        tblCreateScript=createTableScript(TblsApp.TablesApp.HOLIDAYS_CALENDAR);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsApp.HolidaysCalendar", tblCreateScript);
        
        tblCreateScript=createTableScript(TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsApp.HolidaysCalendarDate", tblCreateScript);

        tblCreateScript=createTableScript(TblsApp.TablesApp.INCIDENT);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsApp.Incident", tblCreateScript);

        tblCreateScript=createTableScript(TblsApp.TablesApp.USER_PROCESS);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsApp.UserProcess", tblCreateScript);

        tblCreateScript=createTableScript(TblsApp.TablesApp.USERS);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsApp.Users", tblCreateScript);

        tblCreateScript=createTableScript(TblsApp.TablesApp.VIDEO_TUTORIAL);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsApp.VideoTutorial", tblCreateScript);

        tblCreateScript=createTableScript(TblsAppAudit.TablesAppAudit.INCIDENT);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsAppAudit.Incident", tblCreateScript);
        
        tblCreateScript=createTableScript(TblsAppAudit.TablesAppAudit.SESSION);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsAppAudit.Session", tblCreateScript);

        tblCreateScript=TblsCnfg.UnitsOfMeasurement.createTableScript("", new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsCnfg.UnitsOfMeasurement", tblCreateScript);
// Comentadas porque se han movido para que se creen justo las primeras por tema de que si no existen 'paran la ejecución'
/*        tblCreateScript=TblsCnfg.zzzDbErrorLog.createTableScript("", new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsCnfg.zzzDbErrorLog", tblCreateScript);
        
        tblCreateScript=TblsCnfg.zzzPropertiesMissing.createTableScript("", new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsCnfg.zzzPropertiesMissing", tblCreateScript);
*/
        tblCreateScript=createTableScript(TblsAppConfig.TablesAppConfig.PERSON);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsAppConfig.Person", tblCreateScript);
        
        tblCreateScript=createTableScript(TblsReqs.TablesReqs.PROCEDURE_INFO);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureInfo", tblCreateScript);

        tblCreateScript=createTableScript(TblsReqs.TablesReqs.PROC_MODULE_TABLES);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureModuleTables.", tblCreateScript);
        
        tblCreateScript=createTableScript(TblsReqs.TablesReqs.PROCEDURE_ROLES);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureRoles", tblCreateScript);

        tblCreateScript=createTableScript(TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureSopMetaData", tblCreateScript);

        tblCreateScript=createTableScript(TblsReqs.TablesReqs.PROCEDURE_USER_REQS);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureUserRequirements", tblCreateScript);

        tblCreateScript=createTableScript(TblsReqs.TablesReqs.PROCEDURE_USER_REQS_EVENTS);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureUserRequirementsEvents", tblCreateScript);
        
        tblCreateScript=createTableScript(TblsReqs.TablesReqs.PROC_USER_ROLES);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureUserRole", tblCreateScript);
        
        tblCreateScript=createTableScript(TblsReqs.TablesReqs.PROC_USERS);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureUsers", tblCreateScript);

        tblCreateScript=createTableScript(TblsReqs.TablesReqs.PROC_BUS_RULES);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureBusinessRules", tblCreateScript);

        tblCreateScript=createTableScript(TblsReqs.TablesReqs.PROC_MASTER_DATA);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsReqs.ProcedureMasterData", tblCreateScript);

        for (String[] curTable: tablesToTransferData){
            Object[] tableContent = FromInstanceToInstance.tableContent(curTable[0], curTable[1], curTable[2], platformName);
            jsonObj.put("Transfer Data for "+curTable[0]+"."+curTable[1]+" from "+curTable[2], tableContent[tableContent.length-2]);
        }
        return jsonObj;
     }    

    public static JSONObject createModuleSchemasAndBaseTables(String procInstanceName, String dbName){
        String tblCreateScript="";
        String[] schemaNames = new String[]{
            LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG_AUDIT.getName()), 
            LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), 
            LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_TESTING.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT_TESTING.getName()), 
            LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()),        
            LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE_TESTING.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName())};        
        JSONObject jsonObj=createSchemas(schemaNames, dbName);

        tblCreateScript=createTableScript(TblsProcedure.TablesProcedure.PERSON_PROFILE);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsProcedure.PersonProfile", tblCreateScript);

        tblCreateScript=createTableScript(TblsProcedure.TablesProcedure.PROCEDURE_INFO);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsTblsProcedureReqs.ProcedureInfo", tblCreateScript);

        tblCreateScript=createTableScript(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsProcedure.ProcedureEvents", tblCreateScript);

        tblCreateScript=TblsCnfg.SopMetaData.createTableScript(procInstanceName, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsCnfg.SopMetaData", tblCreateScript);
        
        tblCreateScript=createTableScript(TablesData.USER_SOP, procInstanceName);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsData.UserSop", tblCreateScript);

        tblCreateScript=TblsData.ViewUserAndMetaDataSopView.createTableScript(procInstanceName, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsData.ViewUserAndMetaDataSopView", tblCreateScript);
        
        tblCreateScript=createTableScript(TblsTesting.TablesTesting.SCRIPT);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsTesting.Script", tblCreateScript);
        
        tblCreateScript=createTableScript(TblsTesting.TablesTesting.SCRIPT_STEPS);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsTesting.ScriptSteps", tblCreateScript);

        tblCreateScript=createTableScript(TblsTesting.TablesTesting.SCRIPT_BUS_RULES);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsTesting.ScriptBusinessRules", tblCreateScript);

        tblCreateScript=createTableScript(TblsTesting.TablesTesting.SCRIPTS_COVERAGE);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsTesting.ScriptsCoverage", tblCreateScript);

        tblCreateScript=createTableScript(TblsTesting.TablesTesting.SCRIPT_SAVE_POINT);
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsTesting.ScriptSavePoint", tblCreateScript);

        return jsonObj;
    }    
    public static JSONObject createSchemas(String[] schemasNames, String dbName){
        return schemasActions(schemasNames, dbName, SchemaActions.CREATE.name());
    }
    public static JSONObject removeSchemas(String[] schemasNames, String dbName){
        return schemasActions(schemasNames, dbName, SchemaActions.DELETE.name());
    }
    private static JSONObject schemasActions(String[] schemasNames, String dbName, String actionToPerform){
        String schemaAuthRole=SCHEMA_AUTHORIZATION_ROLE;
        Rdbms.stablishDBConection(dbName);
        JSONObject jsonObj = new JSONObject();
        
        String methodName = "createDataBaseSchemas";       
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), schemasNames.length);     
        for (String configSchemaName:schemasNames){
            JSONArray jsSchemaArr = new JSONArray();
            jsSchemaArr.add(configSchemaName);
            requirementsLogEntry("", methodName, configSchemaName,2);
            if (configSchemaName.contains("-") && (!configSchemaName.startsWith("\""))){            
                configSchemaName = "\""+configSchemaName+"\"";}
            Object[] dbSchemaExists = Rdbms.dbSchemaExists(configSchemaName);
            SchemaActions SchemaAction = SchemaActions.valueOf(actionToPerform);
            switch (SchemaAction){
            case CREATE:
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbSchemaExists[0].toString()) && actionToPerform.equalsIgnoreCase(SchemaActions.CREATE.name())){
                    String configSchemaScript = "CREATE SCHEMA "+configSchemaName+"  AUTHORIZATION "+schemaAuthRole+";"+
                            " GRANT ALL ON SCHEMA "+configSchemaName+" TO "+schemaAuthRole+ ";";     
                    Rdbms.prepUpQuery(configSchemaScript, new Object[]{});            
                }else
                    jsonObj.put(configSchemaName, "schema exists");
                
                break;
            case DELETE:
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbSchemaExists[0].toString()) && actionToPerform.equalsIgnoreCase(SchemaActions.CREATE.name()))
                    jsonObj.put(configSchemaName, "schema not exists");
                else{
                    String configSchemaScript = "DROP SCHEMA "+configSchemaName+" CASCADE";     
                    Rdbms.prepUpQuery(configSchemaScript, new Object[]{});            
                }
                break;
            }
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
     * @return Json object log built after running the script for the db tables creation.
     */
    
}
