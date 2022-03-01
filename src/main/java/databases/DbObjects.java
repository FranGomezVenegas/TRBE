/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import static databases.DbObjects.SchemaActions.CREATE;
import static databases.DbObjects.SchemaActions.DELETE;
import databases.TblsApp.TablesApp;
import databases.TblsAppAudit.TablesAppAudit;
import databases.TblsAppConfig.TablesAppConfig;
import databases.TblsCnfg.TablesConfig;
import databases.TblsData.TablesData;
import databases.TblsReqs.TablesReqs;
import functionaljavaa.datatransfer.FromInstanceToInstance;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.requirement.ProcedureDefinitionToInstance.SCHEMA_AUTHORIZATION_ROLE;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import java.util.ResourceBundle;
import trazit.enums.EnumIntTables;
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
            {GlobalVariables.Schemas.CONFIG.getName(), TblsCnfg.TablesConfig.UOM.getTableName(), dbTrazitModules}
        };
        String[] schemaNames = new String[]{GlobalVariables.Schemas.APP_AUDIT.getName(),
            GlobalVariables.Schemas.CONFIG.getName(), GlobalVariables.Schemas.REQUIREMENTS.getName(), 
            GlobalVariables.Schemas.APP.getName()};
        String tblCreateScript="";
        JSONObject jsonObj=new JSONObject();
        
        JSONObject errorsOnlyObj=new JSONObject();
        JSONObject schemasObj=new JSONObject();
        TablesApp[] tblsApp = TablesApp.values();
        for (TablesApp curTbl: tblsApp){
            tblCreateScript = createTableScript(curTbl);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(tblCreateScript, new Object[]{});
            JSONObject scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains("error"))
                errorsOnlyObj.put("app."+curTbl.getTableName(), scriptLog);
            jsonObj.put(curTbl.getTableName(), scriptLog);
        }
        schemasObj.put("app", jsonObj);
        
        jsonObj=new JSONObject();
        TablesAppAudit[] tblsAppAudit = TablesAppAudit.values();
        for (TablesAppAudit curTbl: tblsAppAudit){
            tblCreateScript = createTableScript(curTbl);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(tblCreateScript, new Object[]{});
            JSONObject scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains("error"))
                errorsOnlyObj.put("app_audit."+curTbl.getTableName(), scriptLog);
            jsonObj.put(curTbl.getTableName(), scriptLog);
        }
        schemasObj.put("app_audit", jsonObj);

        jsonObj=new JSONObject();
        TablesAppConfig[] tblsAppCnfg = TablesAppConfig.values();
        for (TablesAppConfig curTbl: tblsAppCnfg){
            tblCreateScript = createTableScript(curTbl);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(tblCreateScript, new Object[]{});
            JSONObject scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains("error"))
                errorsOnlyObj.put("config."+curTbl.getTableName(), scriptLog);
            jsonObj.put(curTbl.getTableName(), scriptLog);
        }
        schemasObj.put("config", jsonObj);
        
        jsonObj=new JSONObject();
        TablesReqs[] tblsReqs = TablesReqs.values();
        for (TablesReqs curTbl: tblsReqs){
            tblCreateScript = createTableScript(curTbl);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(tblCreateScript, new Object[]{});
            JSONObject scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains("error"))
                errorsOnlyObj.put("requirements."+curTbl.getTableName(), scriptLog);
            jsonObj.put(curTbl.getTableName(), scriptLog);
        }
        schemasObj.put("requirements", jsonObj);
        
        if (errorsOnlyObj.isEmpty())
            schemasObj.put("summary", "all fine");
        else
            schemasObj.put("summary_run_with_errors", errorsOnlyObj);
        
        for (String[] curTable: tablesToTransferData){
            Object[] tableContent = FromInstanceToInstance.tableContent(curTable[0], curTable[1], curTable[2], platformName);
            schemasObj.put("Transfer Data for "+curTable[0]+"."+curTable[1]+" from "+curTable[2], tableContent[tableContent.length-2]);
        }
        return schemasObj;
     }         
    
    public static JSONObject createModuleSchemasAndBaseTables(String procInstanceName, String dbName){
        String tblCreateScript="";
        String[] schemaNames = new String[]{
            LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG_AUDIT.getName()), 
            LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), 
            LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_TESTING.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT_TESTING.getName()), 
            LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE_CONFIG.getName()),
            LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE_AUDIT.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE_AUDIT_TESTING.getName()),
            LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()),        
            LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE_TESTING.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName())};        
        JSONObject schemasObj=new JSONObject();
        JSONObject jsonObj=new JSONObject();
        JSONObject errorsOnlyObj=new JSONObject();

        JSONArray createSchemas = createSchemas(schemaNames, procInstanceName);
        schemasObj.put("create_schemas", createSchemas);

        jsonObj=new JSONObject();
        EnumIntTables[] tblsTesting = new EnumIntTables[]{TblsProcedure.TablesProcedure.PERSON_PROFILE, TblsProcedure.TablesProcedure.PROCEDURE_INFO,
            TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, TblsProcedure.TablesProcedure.PROCEDURE_EVENTS,
            TblsTesting.TablesTesting.SCRIPT, TblsTesting.TablesTesting.SCRIPT_STEPS,
            TblsTesting.TablesTesting.SCRIPT_BUS_RULES, TblsTesting.TablesTesting.SCRIPTS_COVERAGE, TblsTesting.TablesTesting.SCRIPT_SAVE_POINT,
            TablesConfig.SOP_META_DATA, TablesConfig.ZZZ_DB_ERROR, TablesConfig.ZZZ_PROPERTIES_ERROR,
            TablesData.USER_SOP};
        for (EnumIntTables curTbl: tblsTesting){
            tblCreateScript = createTableScript(curTbl, procInstanceName);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(tblCreateScript, new Object[]{});
            JSONObject scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains("error"))
                errorsOnlyObj.put(curTbl.getRepositoryName()+"."+curTbl.getTableName(), scriptLog);
            jsonObj.put(curTbl.getRepositoryName()+"."+curTbl.getTableName(), scriptLog);
        }
        if (errorsOnlyObj.isEmpty())
            jsonObj.put("summary", "all fine");
        else
            jsonObj.put("summary_run_with_errors", errorsOnlyObj);
        return jsonObj;
    }    
    public static JSONArray createSchemas(String[] schemasNames, String dbName){
        return schemasActions(schemasNames, dbName, SchemaActions.CREATE.name());
    }
    public static JSONArray removeSchemas(String[] schemasNames, String dbName){
        return schemasActions(schemasNames, dbName, SchemaActions.DELETE.name());
    }
    private static JSONArray schemasActions(String[] schemasNames, String dbName, String actionToPerform){
        String schemaAuthRole=SCHEMA_AUTHORIZATION_ROLE;
        Rdbms.stablishDBConection(dbName);
        JSONArray mainLogArr = new JSONArray();
        for (String configSchemaName:schemasNames){
            JSONObject jsonObj = new JSONObject();
            JSONArray jsSchemaArr = new JSONArray();
            jsSchemaArr.add(configSchemaName);
            if (configSchemaName.contains("-") && (!configSchemaName.startsWith("\""))){            
                configSchemaName = "\""+configSchemaName+"\"";}
            Object[] dbSchemaExists = Rdbms.dbSchemaExists(configSchemaName);
            SchemaActions SchemaAction = SchemaActions.valueOf(actionToPerform);
            switch (SchemaAction){
            case CREATE:
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbSchemaExists[0].toString()) && actionToPerform.equalsIgnoreCase(SchemaActions.CREATE.name())){
                    String configSchemaScript = "CREATE SCHEMA "+configSchemaName+"  AUTHORIZATION "+schemaAuthRole+";"+
                            " GRANT ALL ON SCHEMA "+configSchemaName+" TO "+schemaAuthRole+ ";";     
                    Integer prepUpQuery = Rdbms.prepUpQuery(configSchemaScript, new Object[]{});
                    if (prepUpQuery==-999)
                        jsonObj.put(configSchemaName.replace("\"", ""), "schema creation failed");
                    else
                        jsonObj.put(configSchemaName.replace("\"", ""), "schema created");
                }else
                    jsonObj.put(configSchemaName.replace("\"", ""), "schema exists");
                
                break;
            case DELETE:
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbSchemaExists[0].toString()) && actionToPerform.equalsIgnoreCase(SchemaActions.CREATE.name()))
                    jsonObj.put(configSchemaName.replace("\"", ""), "schema not exists");
                else{
                    String configSchemaScript = "DROP SCHEMA "+configSchemaName+" CASCADE";     
                    Integer prepUpQuery = Rdbms.prepUpQuery(configSchemaScript, new Object[]{});            
                    if (prepUpQuery==-999)
                        jsonObj.put(configSchemaName.replace("\"", ""), "schema deletion failed");
                    else
                        jsonObj.put(configSchemaName.replace("\"", ""), "schema deleted");
                }
                break;
            }
            mainLogArr.add(jsonObj);
        }
        return mainLogArr;
     }    
    
    /**
     *
     * @return Json object log built after running the script for the db tables creation.
     */
    
}
