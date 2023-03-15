/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import com.labplanet.servicios.requirements.ProcDeployEnums;
import static databases.Rdbms.insertRecordInTableFromTable;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsApp.TablesApp;
import databases.TblsAppAudit.TablesAppAudit;
import databases.TblsAppConfig.TablesAppConfig;
import databases.TblsProcedure.TablesProcedure;
import databases.TblsReqs.TablesReqs;
import functionaljavaa.datatransfer.FromInstanceToInstance;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.requirement.ProcedureDefinitionToInstance.SCHEMA_AUTHORIZATION_ROLE;
import java.util.Arrays;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import java.util.ResourceBundle;
import lbplanet.utilities.LPArray;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
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
     * @param platformName
     * @return one Json Object with the log built after running the script for the platform instance creation.
     */
    public static JSONObject createPlatformSchemasAndBaseTables(String platformName){        
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());        
        EnumIntTables[] tablesToTransferData=new EnumIntTables[]{
            TblsCnfg.TablesConfig.UOM
        };
        String tblCreateScript="";
        JSONObject jsonObj=new JSONObject();
        
        JSONObject errorsOnlyObj=new JSONObject();
        JSONObject schemasObj=new JSONObject();
        TablesApp[] tblsApp = TablesApp.values();
        for (TablesApp curTbl: tblsApp){
            tblCreateScript = createTableScript(curTbl, null, false, true);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});
            
            JSONObject scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            if (!tblCreateScript.toLowerCase().startsWith("table") && !tblCreateScript.toLowerCase().contains("already"))            
                scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains("error"))
                errorsOnlyObj.put("app."+curTbl.getTableName(), scriptLog);
            jsonObj.put(curTbl.getTableName(), scriptLog);
        }
        schemasObj.put("app", jsonObj);
        
        jsonObj=new JSONObject();
        TablesAppAudit[] tblsAppAudit = TablesAppAudit.values();
        for (TablesAppAudit curTbl: tblsAppAudit){
            tblCreateScript = createTableScript(curTbl, null, false, true);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});
            JSONObject scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            if (!tblCreateScript.toLowerCase().startsWith("table") && !tblCreateScript.toLowerCase().contains("already"))            
                scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains("error"))
                errorsOnlyObj.put("app_audit."+curTbl.getTableName(), scriptLog);
            jsonObj.put(curTbl.getTableName(), scriptLog);
        }
        schemasObj.put("app_audit", jsonObj);

        jsonObj=new JSONObject();
        TablesAppConfig[] tblsAppCnfg = TablesAppConfig.values();
        for (TablesAppConfig curTbl: tblsAppCnfg){
            tblCreateScript = createTableScript(curTbl, null, false, true);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});
            JSONObject scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            if (!tblCreateScript.toLowerCase().startsWith("table") && !tblCreateScript.toLowerCase().contains("already"))        
                scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains("error"))
                errorsOnlyObj.put("config."+curTbl.getTableName(), scriptLog);
            jsonObj.put(curTbl.getTableName(), scriptLog);
        }
        schemasObj.put("config", jsonObj);

        tblCreateScript = createTableScript(TablesProcedure.PROCEDURE_BUSINESS_RULE, "app", false, true);
        tblCreateScript=tblCreateScript.replace("app-procedure", "app-business-rules");
        Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(TablesProcedure.PROCEDURE_BUSINESS_RULE.getRepositoryName(), TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), tblCreateScript, new Object[]{});

        JSONObject scriptLog=new JSONObject();
        scriptLog.put("script", tblCreateScript);
        if (!tblCreateScript.toLowerCase().startsWith("table") && !tblCreateScript.toLowerCase().contains("already"))            
            scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
        if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains("error"))
            errorsOnlyObj.put("app."+TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), scriptLog);
        jsonObj.put(TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), scriptLog);
        
        
        String[] fields=new String[]{"area", "rule_name", "rule_value", "disabled"};
        Object[][] values=new Object[][]{{"frontend_locksession", "enableLockSession", "true", false},
            {"frontend_locksession", "enableLogoutSession", "true", false},
            {"frontend_locksession", "minsLockSession", "2", false},
            {"frontend_locksession", "minsLogoutSession", "5", false},
            {"frontend_locksession", "secondsNextTimeChecker", "60", false},
            {"procedure", "windowOpenableWhenNotSopCertifiedUserSopCertification", "NO", false}};
        for (Object[] curRule: values){
            RdbmsObject insertRecord = Rdbms.insertRecord(TablesApp.APP_BUSINESS_RULES, fields, curRule, "app-business_rules");
            if (insertRecord.getRunSuccess())
                jsonObj.put("inserting_business_rule_diagn", curRule[1]+" "+insertRecord.getRunSuccess());
            else
                jsonObj.put("inserting_business_rule_diagn", curRule[1]+" "+insertRecord.getErrorMessageCode());
        }
        schemasObj.put("app-business-rules", jsonObj);
        
        jsonObj=new JSONObject();
        TablesReqs[] tblsReqs = TablesReqs.values();
        for (TablesReqs curTbl: tblsReqs){
            tblCreateScript = createTableScript(curTbl, null, false, true);
            prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});
            scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            if (!tblCreateScript.toLowerCase().startsWith("table") && !tblCreateScript.toLowerCase().contains("already"))        
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
        
        for (EnumIntTables curTable: tablesToTransferData){
            Object[] tableContent = FromInstanceToInstance.tableContent(curTable, dbTrazitModules, platformName);
            schemasObj.put("Transfer Data for "+curTable.getRepositoryName()+"."+curTable.getTableName()+" from "+dbTrazitModules, tableContent[tableContent.length-2]);
        }
        return schemasObj;
     }         
    
    public static JSONObject createModuleSchemasAndBaseTables(String procInstanceName){
        String tblCreateScript="";
        JSONObject schemasObj=new JSONObject();
        JSONObject jsonObj=new JSONObject();
        JSONObject errorsOnlyObj=new JSONObject();
        String[] schemaNames=ProcDeployEnums.moduleBaseSchemas(procInstanceName);
        JSONArray createSchemas = createSchemas(schemaNames, procInstanceName);
        schemasObj.put("create_schemas", createSchemas);
        EnumIntTables[] moduleBaseTables = ProcDeployEnums.moduleBaseTables();
        jsonObj=new JSONObject();
        for (EnumIntTables curTbl: moduleBaseTables){
            tblCreateScript = createTableScript(curTbl, procInstanceName, false, true);
            
            tblCreateScript = createTableScript(curTbl, LPPlatform.buildSchemaName(procInstanceName, curTbl.getRepositoryName()), false, true);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});
            String schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curTbl.getRepositoryName()), curTbl.getTableName());
            if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curTbl.getRepositoryName()))){
                String tblCreateScriptTesting = createTableScript(curTbl, schemaForTesting, false, true);
                prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScriptTesting, new Object[]{});
            }
            //Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});
            JSONObject scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            if (!tblCreateScript.toLowerCase().startsWith("table") && !tblCreateScript.toLowerCase().contains("already"))            
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
    
    
    public static final  JSONObject cloneProcModel(String procedure,  Integer procVersion, String procInstanceName){
        SqlWhere sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureEvents.NAME, WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, "");
        RdbmsObject removeRecordInTable = Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS, sw, procInstanceName);
        Object[] insertRecordInTableFromTable = insertRecordInTableFromTable(true, 
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_USER_REQS_EVENTS.getTableFields()),
                    GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS_EVENTS.getTableName(), 
                new String[]{TblsReqs.ProcedureUserRequirementsEvents.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirementsEvents.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUserRequirementsEvents.PROC_INSTANCE_NAME.getName()},
                new Object[]{procedure, procVersion, procInstanceName},
                LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), 
                    TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableName(), getAllFieldNames(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableFields()));
        JSONObject jsonObj = new JSONObject();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTableFromTable[0].toString())){
            jsonObj.put("error_cloning_from_requirements_to_procedure", Arrays.toString(insertRecordInTableFromTable));
            return jsonObj;
        }     
        jsonObj.put("success_cloning_from_requirements_to_procedure", insertRecordInTableFromTable[insertRecordInTableFromTable.length-2]+":"+insertRecordInTableFromTable[insertRecordInTableFromTable.length-1]);
//        jsonObj.put("Diagnostic from createDBProcedureEvents", insertRecordInTableFromTable[0].toString());
        String[] procEventFldNamesToGet=getAllFieldNames(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableFields());
        Object[][] procEventRows = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableName(), 
                //new String[]{TblsProcedure.ProcedureEvents.ROLE_NAME.getName(), WHERECLAUSE_TYPES.OR.getSqlClause()+" "+TblsProcedure.ProcedureEvents.ROLE_NAME.getName()+" "+WHERECLAUSE_TYPES.LIKE}, 
                //new Object[]{"ALL", "%|%"}, 
                new String[]{TblsProcedure.ProcedureEvents.ROLE_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{},
                procEventFldNamesToGet);
        JSONArray multiRolejArr=new JSONArray();
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventRows[0][0].toString())){
            Object[][] procRoles = new Object[][]{{}};
                Object[][] procRolesAllRoles = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_ROLES.getTableName(), 
                    new String[]{TblsReqs.ProcedureRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRoles.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName()},
                    new Object[]{procedure, procVersion, procInstanceName},
                    new String[]{TblsReqs.ProcedureRoles.ROLE_NAME.getName()});
            
            for (Object[] curProcEvent: procEventRows){
                JSONObject multiRolCurEvent=new JSONObject();
                multiRolCurEvent.put("event_name", 
                    curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.NAME.getName())]);
                String multiRolesLog="";
                if ("ALL".equalsIgnoreCase(curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.ROLE_NAME.getName())].toString())){
                    procRoles=procRolesAllRoles;                    
                    multiRolesLog=multiRolesLog+" as for all roles, trying addition for "+LPArray.convertArrayToString(LPArray.getColumnFromArray2D(procRoles, 0),", ", "", true);
                }else{
                    procRoles=LPArray.array1dTo2d(curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.ROLE_NAME.getName())].toString().split("\\|"), 1);
                    multiRolesLog=multiRolesLog+" as for multiple roles, trying addition for "+LPArray.convertArrayToString(LPArray.getColumnFromArray2D(procRoles, 0),", ", "", true);                    
                }
                multiRolCurEvent.put("multirole_type", multiRolesLog);
                for (int i=0;i<procRoles.length;i++){
                    if (i==0){
                        SqlWhere sqlWhere = new SqlWhere();
                        sqlWhere.addConstraint(TblsProcedure.ProcedureEvents.ROLE_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.ROLE_NAME.getName())].toString()}, "");
                        sqlWhere.addConstraint(TblsProcedure.ProcedureEvents.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.NAME.getName())]}, "");
                        Object[] diagnoses=Rdbms.updateRecordFieldsByFilter(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS,
                            EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS, new String[]{TblsProcedure.ProcedureEvents.ROLE_NAME.getName()}), new Object[]{procRoles[0][0].toString()}, sqlWhere, procInstanceName);
                        multiRolCurEvent.put("updated?", !LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString()));
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString()))
                            multiRolCurEvent.put("update error log", Arrays.toString(diagnoses));
                    }else{
                    
                        curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.ROLE_NAME.getName())]=procRoles[i][0].toString();
                        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS, procEventFldNamesToGet, curProcEvent);
                        multiRolCurEvent.put("inserted?", insertRecordInTable.getRunSuccess());
                        if (!insertRecordInTable.getRunSuccess())
                            multiRolCurEvent.put("insert error log", 
                                insertRecordInTable.getErrorMessageCode()+" "+Arrays.toString(insertRecordInTable.getErrorMessageVariables()));
                    }
                }
                if (procRoles.length>1)
                    multiRolejArr.add(multiRolCurEvent);
            }
        }
        if (!multiRolejArr.isEmpty())
            jsonObj.put("multiroles_addition_log", multiRolejArr);
        return jsonObj;
    }
    
    /**
     *
     * @return Json object log built after running the script for the db tables creation.
     */
    
}
