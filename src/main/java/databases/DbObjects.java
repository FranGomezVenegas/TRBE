/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import trazit.procedureinstance.definition.definition.TblsReqs;
import com.labplanet.servicios.app.GlobalAPIsParams;
import trazit.procedureinstance.deployment.definition.ProcDeployEnums;
import static databases.Rdbms.insertRecordInTableFromTable;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import functionaljavaa.datatransfer.FromInstanceToInstance;
import functionaljavaa.parameter.Parameter;
import java.net.MalformedURLException;
import static trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstance.SCHEMA_AUTHORIZATION_ROLE;
import java.util.Arrays;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.JSONObject;
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
    enum SchemaActions{CREATE, DELETE}
    /**
     *
     */
    public static final String POSTGRES_DB_TABLESPACE="pg_default";
    
    /**
     *
     * @param platformName
     * @return one Json Object with the log built after running the script for the platform instance creation.
     */
    public static JSONObject createPlatformSchemasAndBaseTables(String platformName) throws MalformedURLException{        
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());        
        EnumIntTables[] tablesToTransferData=new EnumIntTables[]{
            TblsCnfg.TablesConfig.UOM
        };
        String tblCreateScript="";
        JSONObject jsonObj=new JSONObject();
        
        JSONObject errorsOnlyObj=new JSONObject();
        JSONObject schemasObj=new JSONObject();
/*  Moved        
        TablesApp[] tblsApp = TablesApp.values();
        for (TablesApp curTbl: tblsApp){
            tblCreateScript = createTableScript(curTbl, null, false, true, null);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});
            
            JSONObject scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            if (Boolean.FALSE.equals(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE)) && Boolean.FALSE.equals(tblCreateScript.toLowerCase().contains("already")))
                scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains(GlobalAPIsParams.LBL_ERROR))
                errorsOnlyObj.put("app."+curTbl.getTableName(), scriptLog);
            jsonObj.put(curTbl.getTableName(), scriptLog);
        }
        schemasObj.put("app", jsonObj);
        
        jsonObj=new JSONObject();
        TablesAppAudit[] tblsAppAudit = TablesAppAudit.values();
        for (TablesAppAudit curTbl: tblsAppAudit){
            tblCreateScript = createTableScript(curTbl, null, false, true, null);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});
            JSONObject scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            if (Boolean.FALSE.equals(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE)) && Boolean.FALSE.equals(tblCreateScript.toLowerCase().contains("already")) )
                scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains(GlobalAPIsParams.LBL_ERROR))
                errorsOnlyObj.put("app_audit."+curTbl.getTableName(), scriptLog);
            jsonObj.put(curTbl.getTableName(), scriptLog);
        }
        schemasObj.put("app_audit", jsonObj);

        jsonObj=new JSONObject();
        TablesAppConfig[] tblsAppCnfg = TablesAppConfig.values();
        for (TablesAppConfig curTbl: tblsAppCnfg){
            tblCreateScript = createTableScript(curTbl, null, false, true, null);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});
            JSONObject scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            if (Boolean.FALSE.equals(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE)) && Boolean.FALSE.equals(tblCreateScript.toLowerCase().contains("already")) )        
                scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains(GlobalAPIsParams.LBL_ERROR))
                errorsOnlyObj.put("config."+curTbl.getTableName(), scriptLog);
            jsonObj.put(curTbl.getTableName(), scriptLog);
        }
        schemasObj.put("config", jsonObj);

        EnumIntTables[] appProcTables=new EnumIntTables[]{TablesProcedure.PROCEDURE_BUSINESS_RULE, TablesProcedure.PROCEDURE_INFO, TablesProcedure.PROCEDURE_EVENTS, TablesProcedure.PERSON_PROFILE};
        for (EnumIntTables curTbl: appProcTables){
            tblCreateScript = createTableScript(curTbl, "app", false, true, null);
            tblCreateScript=tblCreateScript.replace(GlobalVariables.Schemas.APP_PROCEDURE.getName(), curTbl.toString());
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});

            JSONObject scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            if (Boolean.FALSE.equals(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE)) && Boolean.FALSE.equals(tblCreateScript.toLowerCase().contains("already")) )            
                scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains(GlobalAPIsParams.LBL_ERROR))
                errorsOnlyObj.put("app."+curTbl.getTableName(), scriptLog);
            jsonObj.put(curTbl.getTableName(), scriptLog); prcDeplSectionLog prcReqsSectionLog
        }
*/
/*        RdbmsObject insertRecord2 = Rdbms.insertRecord(TblsProcedure.TablesProcedure.PROCEDURE_INFO, 
                new String[]{TblsProcedure.ProcedureInfo.NAME.getName(), TblsProcedure.ProcedureInfo.VERSION.getName(), TblsProcedure.ProcedureInfo.PROCEDURE_HASH_CODE.getName(),
                    TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName(), TblsProcedure.ProcedureInfo.MODULE_NAME.getName(), 
                    TblsProcedure.ProcedureInfo.INCLUDE_CONFIG_CHANGES.getName(), TblsProcedure.ProcedureInfo.ENABLE_CHANGE_TRACKING.getName(), TblsProcedure.ProcedureInfo.CREATE_PICT_ONGCHNGE.getName()}, 
                new Object[]{"app", 1, -1, "app", "app", true, true, false}, TblsApp.TablesApp.APP_PERSON_PROFILE.getRepositoryName());
        if (Boolean.TRUE.equals(insertRecord2.getRunSuccess()))
            schemasObj.put("inserting_procedure_info_"+"app",insertRecord2.getRunSuccess());
        else
            schemasObj.put("inserting_procedure_info_"+"app",insertRecord2.getErrorMessageCode());
        
        String[] fields=new String[]{TblsProcedure.ProcedureBusinessRules.AREA.getName(), TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName(),
            TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName(), TblsProcedure.ProcedureBusinessRules.DISABLED.getName()};
        Object[][] values=new Object[][]{{"frontend_locksession", "enableLockSession", "true", false},
            {"frontend_locksession", "enableLogoutSession", "true", false},
            {"frontend_locksession", "minsLockSession", "2", false},
            {"frontend_locksession", "minsLogoutSession", "5", false},
            {"frontend_locksession", "secondsNextTimeChecker", "60", false},
            {"procedure", "windowOpenableWhenNotSopCertifiedUserSopCertification", "NO", false}};
        for (Object[] curRule: values){
            
            RdbmsObject insertRecord = Rdbms.insertRecord(TablesApp.APP_BUSINESS_RULES, fields, curRule, TblsApp.TablesApp.APP_BUSINESS_RULES.getRepositoryName());
            
            if (Boolean.TRUE.equals(insertRecord.getRunSuccess()))
                jsonObj.put("inserting_business_rule_diagn", curRule[1]+" "+insertRecord.getRunSuccess());
            else
                jsonObj.put("inserting_business_rule_diagn", curRule[1]+" "+insertRecord.getErrorMessageCode());
        }
        schemasObj.put(TablesApp.APP_BUSINESS_RULES.toString(), jsonObj);

        String fakeEsingn="firmademo";
        String defaultMail="info@trazit.net";
        Object[] encryptValue=DbEncryption.encryptValue(fakeEsingn);        
        String fakeEsingnEncrypted = encryptValue[encryptValue.length-1].toString();
        Object[] encryptPa=DbEncryption.encryptValue("trazit4ever");        
        String paEncrypted = encryptPa[encryptPa.length-1].toString();
        Object[] encryptPers=DbEncryption.encryptValue("adminz");        
        String persEncrypted = encryptPers[encryptPers.length-1].toString();
        RdbmsObject insertRecordInTable = Rdbms.insertRecord(TblsApp.TablesApp.USERS, 
                new String[]{TblsApp.Users.USER_NAME.getName(), TblsApp.Users.EMAIL.getName(), TblsApp.Users.ESIGN.getName(),
                    TblsApp.Users.PASSWORD.getName(), TblsApp.Users.PERSON_NAME.getName()},
                new Object[]{"admin", defaultMail, fakeEsingnEncrypted, paEncrypted, persEncrypted}, null);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess()))
            jsonObj.put("insert_user_admin", insertRecordInTable.getRunSuccess());
        else{
            jsonObj.put("insert_user_admin", insertRecordInTable.getErrorMessageCode());
        }
        
            
        insertRecord2 = Rdbms.insertRecord(TablesApp.APP_PERSON_PROFILE, 
                new String[]{TblsProcedure.PersonProfile.PERSON_NAME.getName(), TblsProcedure.PersonProfile.ROLE_NAME.getName(), TblsProcedure.PersonProfile.ACTIVE.getName()}, 
                new Object[]{persEncrypted, "superuser", true}, TblsApp.TablesApp.APP_PERSON_PROFILE.getRepositoryName());
        if (Boolean.TRUE.equals(insertRecord2.getRunSuccess()))
            schemasObj.put("inserting_person_profile_"+"superuser",insertRecord2.getRunSuccess());
        else
            schemasObj.put("inserting_person_profile_"+"superuser",insertRecord2.getErrorMessageCode());
        

        insertRecord2 = Rdbms.insertRecord(TablesApp.APP_PERSON_PROFILE, 
                new String[]{TblsProcedure.PersonProfile.PERSON_NAME.getName(), TblsProcedure.PersonProfile.ROLE_NAME.getName(), TblsProcedure.PersonProfile.ACTIVE.getName()}, 
                new Object[]{persEncrypted, PROC_MANAGEMENT_SPECIAL_ROLE, true}, TblsApp.TablesApp.APP_PERSON_PROFILE.getRepositoryName());
        if (Boolean.TRUE.equals(insertRecord2.getRunSuccess()))
            schemasObj.put("inserting_person_profile_"+PROC_MANAGEMENT_SPECIAL_ROLE,insertRecord2.getRunSuccess());
        else
            schemasObj.put("inserting_person_profile_"+PROC_MANAGEMENT_SPECIAL_ROLE,insertRecord2.getErrorMessageCode());

        insertRecord2 = Rdbms.insertRecord(TablesApp.USER_PROCESS, 
                new String[]{TblsApp.UserProcess.USER_NAME.getName(), TblsApp.UserProcess.PROC_NAME.getName(), TblsApp.UserProcess.ACTIVE.getName()}, 
                new Object[]{"admin", "app", true}, TblsApp.TablesApp.APP_PERSON_PROFILE.getRepositoryName());
        if (Boolean.TRUE.equals(insertRecord2.getRunSuccess()))
            schemasObj.put("inserting_user_process_"+"admin",insertRecord2.getRunSuccess());
        else
            schemasObj.put("inserting_user_process_"+"admin",insertRecord2.getErrorMessageCode());

        
        fields=new String[]{TblsProcedure.ProcedureEvents.NAME.getName(), TblsProcedure.ProcedureEvents.ROLE_NAME.getName(),
            TblsProcedure.ProcedureEvents.MODE.getName(), TblsProcedure.ProcedureEvents.TYPE.getName(),
            TblsProcedure.ProcedureEvents.LABEL_EN.getName(), TblsProcedure.ProcedureEvents.LABEL_ES.getName(), 
            TblsProcedure.ProcedureEvents.ORDER_NUMBER.getName(), TblsProcedure.ProcedureEvents.LP_FRONTEND_PAGE_NAME.getName()};
        values=new Object[][]{{"BlackIpList", "superuser", "edit", "simple", "Black IP Lists", "Lista IPs denegadas", 1, "BlackIpList" },
            {"PlatformBusRules", "superuser", "edit", "simple", "Business Rules", "Reglas de Negocio", 3, "PlatformBusRules"},
            {"WhiteIpList", "superuser", "edit", "simple", "White IP Lists", "Lista IPs autorizadas", 2, "WhiteIpList"}};
        for (Object[] curRule: values){
            
            RdbmsObject insertRecord = Rdbms.insertRecord(TablesApp.APP_PROCEDURE_EVENTS, fields, curRule, TblsApp.TablesApp.APP_PROCEDURE_EVENTS.getRepositoryName());
            
            if (Boolean.TRUE.equals(insertRecord.getRunSuccess()))
                jsonObj.put("inserting_diagn", curRule[1]+" "+insertRecord.getRunSuccess());
            else
                jsonObj.put("inserting_diagn", curRule[1]+" "+insertRecord.getErrorMessageCode());
        }
        schemasObj.put(TablesApp.APP_PROCEDURE_EVENTS.toString(), jsonObj);
*/        
/*  Moved      
        jsonObj=new JSONObject();
        TablesReqs[] tblsReqs = TablesReqs.values();
        for (TablesReqs curTbl: tblsReqs){
            tblCreateScript = createTableScript(curTbl, null, false, true, null);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});
            JSONObject scriptLog = new JSONObject();
            scriptLog.put("script", tblCreateScript);
            if (Boolean.FALSE.equals(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE)) && Boolean.FALSE.equals(tblCreateScript.toLowerCase().contains("already")) )
                scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains(GlobalAPIsParams.LBL_ERROR))
                errorsOnlyObj.put("requirements."+curTbl.getTableName(), scriptLog);
            jsonObj.put(curTbl.getTableName(), scriptLog);
        }
        schemasObj.put("requirements", jsonObj);
        
        
        String procNameInReqs="platform-settings";
        
        fields=new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName(),
                    TblsReqs.ProcedureInfo.ACTIVE.getName(), TblsReqs.ProcedureInfo.LOCKED_FOR_ACTIONS.getName(),
                    TblsReqs.ProcedureInfo.MODULE_NAME.getName(), TblsReqs.ProcedureInfo.DESCRIPTION.getName(), TblsReqs.ProcedureInfo.LABEL_EN.getName(), TblsReqs.ProcedureInfo.LABEL_ES.getName(), TblsReqs.ProcedureInfo.PROCEDURE_HASH_CODE.getName()};
        LocalDateTime currentTimeStamp = LPDate.getCurrentTimeStamp();
        int hashCode = currentTimeStamp.hashCode();        
        values=new Object[][]{{procNameInReqs, 1, procNameInReqs, true, false, "APP", "Platform settings", "Platform settings", "Configuración de Plataforma", hashCode}};
        for (Object[] curRule: values){            
            RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.PROCEDURE_INFO, fields, curRule, TblsReqs.TablesReqs.PROCEDURE_INFO.getRepositoryName());            
            if (Boolean.TRUE.equals(insertRecord.getRunSuccess()))
                jsonObj.put(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName()+"_inserting_diagn", curRule[1]+" "+insertRecord.getRunSuccess());
            else
                jsonObj.put(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName()+"_inserting_diagn", curRule[1]+" "+insertRecord.getErrorMessageCode());
        }
        schemasObj.put(TblsReqs.TablesReqs.PROCEDURE_INFO.toString(), jsonObj);

        ClassLoader classLoader = DbObjects.class.getClassLoader();
        String filePath = "JavaScript/platform-settingsModel.txt";
        StringBuilder jsonDataModel = new StringBuilder();    
        try (InputStream inputStream = classLoader.getResourceAsStream(filePath)) {
            if (inputStream != null) {
                // Read the content of the text file
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonDataModel.append(line).append("\n");
                }
            } else {
                System.err.println("File not found: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }  
        JSONObject jObjModel = new JSONObject(jsonDataModel.toString());       
        String[] fieldNames = new String[]{TblsReqs.ProcedureFEModel.PROCEDURE_NAME.getName(), TblsReqs.ProcedureFEModel.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureFEModel.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureFEModel.MODEL_JSON.getName(), TblsReqs.ProcedureFEModel.ACTIVE.getName()};
        Object[] fieldValues = new Object[]{procNameInReqs, 1, procNameInReqs, jObjModel, true};
        insertRecord2 = Rdbms.insertRecord(TblsReqs.TablesReqs.PROC_FE_MODEL, fieldNames, fieldValues, TblsReqs.TablesReqs.PROC_FE_MODEL.getRepositoryName());
        if (Boolean.TRUE.equals(insertRecord2.getRunSuccess()))
            jsonObj.put(TblsReqs.TablesReqs.PROC_FE_MODEL.getTableName()+"_inserting_diagn", insertRecord2.getRunSuccess());
        else
            jsonObj.put(TblsReqs.TablesReqs.PROC_FE_MODEL.getTableName()+"_inserting_diagn", insertRecord2.getErrorMessageCode());
        schemasObj.put(TblsReqs.TablesReqs.PROC_FE_MODEL.toString(), jsonObj);
        
        
        String directoryPath = "ModulesInfo"; // Update with the actual path
        
        // Initialize the JSON object
        JSONObject jsonObject = new JSONObject();
        URL directoryUrl = classLoader.getResource(directoryPath);
        if (directoryUrl != null) {
            // Get the file path from the URL
            String directoryFilePath = directoryUrl.getPath();

            // Create a File object representing the directory
            File directory = new File(directoryFilePath);

            // List files in the directory
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        // Read the content of each text file
                        jsonDataModel = new StringBuilder();

                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                jsonDataModel.append(line).append("\n");
                            }
                            jObjModel = new JSONObject(jsonDataModel.toString());   
                            // Wrap the JSON data in a new JSON object under the file name
                            JSONObject fileJsonObject = new JSONObject();
                            fileJsonObject.put("information", new JSONObject(jsonDataModel.toString()));

                            // Add the file's JSON object to the main JSON object
                            jsonObject.put(file.getName(), fileJsonObject);

                            jObjModel = new JSONObject(jsonDataModel.toString());       
                            fieldNames = new String[]{TblsReqs.Modules.MODULE_NAME.getName(), TblsReqs.Modules.MODULE_VERSION.getName(),
                                TblsReqs.Modules.INFO_JSON.getName(), TblsReqs.Modules.ACTIVE.getName(),
                            };
                            fieldValues = new Object[]{procNameInReqs, 1, jObjModel, true};
                            insertRecord2 = Rdbms.insertRecord(TblsReqs.TablesReqs.MODULES, fieldNames, fieldValues, TblsReqs.TablesReqs.MODULES.getRepositoryName());
                            if (Boolean.TRUE.equals(insertRecord2.getRunSuccess()))
                                jsonObj.put(TblsReqs.TablesReqs.MODULES.getTableName()+"_inserting_diagn", insertRecord2.getRunSuccess());
                            else
                                jsonObj.put(TblsReqs.TablesReqs.MODULES.getTableName()+"_inserting_diagn", insertRecord2.getErrorMessageCode());
                            schemasObj.put(TblsReqs.TablesReqs.MODULES.toString(), jsonObj);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        
*/        
/*        
        fields=new String[]{TblsReqs.ProcedureUsers.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUsers.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUsers.PROC_INSTANCE_NAME.getName(),
                    TblsReqs.ProcedureUsers.USER_NAME.getName(), TblsReqs.ProcedureUsers.FULL_NAME.getName()};
        values=new Object[][]{{procNameInReqs, 1, procNameInReqs, "admin", "admin"}};
        for (Object[] curRule: values){            
            RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.PROC_USERS, fields, curRule, 
                    TblsReqs.TablesReqs.PROC_USERS.getRepositoryName());            
            if (Boolean.TRUE.equals(insertRecord.getRunSuccess()))
                jsonObj.put("inserting_diagn", curRule[1]+" "+insertRecord.getRunSuccess());
            else
                jsonObj.put("inserting_diagn", curRule[1]+" "+insertRecord.getErrorMessageCode());
        }
        schemasObj.put(TblsReqs.TablesReqs.PROC_USERS.toString(), jsonObj);

        fields=new String[]{TblsReqs.ProcedureRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRoles.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName(),
                    TblsReqs.ProcedureRoles.ROLE_NAME.getName(), TblsReqs.ProcedureRoles.DESCRIPTION.getName()};
        values=new Object[][]{{procNameInReqs, 1, procNameInReqs, "superuser", "superuser can do everything as admin role"}};
        for (Object[] curRule: values){            
            RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.PROCEDURE_ROLES, fields, curRule, 
                    TblsReqs.TablesReqs.PROCEDURE_ROLES.getRepositoryName());            
            if (Boolean.TRUE.equals(insertRecord.getRunSuccess()))
                jsonObj.put("inserting_diagn", curRule[1]+" "+insertRecord.getRunSuccess());
            else
                jsonObj.put("inserting_diagn", curRule[1]+" "+insertRecord.getErrorMessageCode());
        }
        schemasObj.put(TblsReqs.TablesReqs.PROCEDURE_ROLES.toString(), jsonObj);

        fields=new String[]{TblsReqs.ProcedureUserRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRoles.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName(),
                    TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()};
        values=new Object[][]{{procNameInReqs, 1, procNameInReqs, "admin", "superuser"}};
        for (Object[] curRule: values){            
            RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.PROC_USER_ROLES, fields, curRule, 
                    TblsReqs.TablesReqs.PROC_USER_ROLES.getRepositoryName());            
            if (Boolean.TRUE.equals(insertRecord.getRunSuccess()))
                jsonObj.put("inserting_diagn", curRule[1]+" "+insertRecord.getRunSuccess());
            else
                jsonObj.put("inserting_diagn", curRule[1]+" "+insertRecord.getErrorMessageCode());
        }
        schemasObj.put(TblsReqs.TablesReqs.PROC_USER_ROLES.toString(), jsonObj);

        fields=new String[]{TblsReqs.ProcedureModuleTables.PROCEDURE_NAME.getName(), TblsReqs.ProcedureModuleTables.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureModuleTables.PROC_INSTANCE_NAME.getName(),
                    TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.TABLE_NAME.getName(), TblsReqs.ProcedureModuleTables.IS_VIEW.getName()};
        values=new Object[][]{{procNameInReqs, 1, procNameInReqs, TblsApp.TablesApp.IP_BLACK_LIST.getRepositoryName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), false}};
        for (Object[] curRule: values){            
            RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.PROC_MODULE_TABLES, fields, curRule, 
                    TblsReqs.TablesReqs.PROC_MODULE_TABLES.getRepositoryName());            
            if (Boolean.TRUE.equals(insertRecord.getRunSuccess()))
                jsonObj.put("inserting_diagn", curRule[1]+" "+insertRecord.getRunSuccess());
            else
                jsonObj.put("inserting_diagn", curRule[1]+" "+insertRecord.getErrorMessageCode());
        }
        schemasObj.put(TblsReqs.TablesReqs.PROC_MODULE_TABLES.toString(), jsonObj);

        fields=new String[]{TblsReqs.ProcedureModuleTables.PROCEDURE_NAME.getName(), TblsReqs.ProcedureModuleTables.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureModuleTables.PROC_INSTANCE_NAME.getName(),
                    TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.TABLE_NAME.getName(), TblsReqs.ProcedureModuleTables.IS_VIEW.getName()};
        values=new Object[][]{{procNameInReqs, 1, procNameInReqs, TblsApp.TablesApp.IP_WHITE_LIST.getRepositoryName(), TblsApp.TablesApp.IP_WHITE_LIST.getTableName(), false}};
        for (Object[] curRule: values){            
            RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.PROC_MODULE_TABLES, fields, curRule, 
                    TblsReqs.TablesReqs.PROC_MODULE_TABLES.getRepositoryName());            
            if (Boolean.TRUE.equals(insertRecord.getRunSuccess()))
                jsonObj.put("inserting_diagn", curRule[1]+" "+insertRecord.getRunSuccess());
            else
                jsonObj.put("inserting_diagn", curRule[1]+" "+insertRecord.getErrorMessageCode());
        }
        schemasObj.put(TblsReqs.TablesReqs.PROC_MODULE_TABLES.toString(), jsonObj);
*/        
/*
        fields=new String[]{TblsProcedure.ProcedureEvents.NAME.getName(), TblsProcedure.ProcedureEvents.ROLE_NAME.getName(),
            TblsProcedure.ProcedureEvents.MODE.getName(), TblsProcedure.ProcedureEvents.TYPE.getName(),
            TblsProcedure.ProcedureEvents.LABEL_EN.getName(), TblsProcedure.ProcedureEvents.LABEL_ES.getName(), 
            TblsProcedure.ProcedureEvents.ORDER_NUMBER.getName(), TblsProcedure.ProcedureEvents.LP_FRONTEND_PAGE_NAME.getName()};
        values=new Object[][]{{"BlackIpList", "superuser", "edit", "simple", "Black IP Lists", "Lista IPs denegadas", 1, "BlackIpList" },
            {"PlatformBusRules", "superuser", "edit", "simple", "Business Rules", "Reglas de Negocio", 3, "PlatformBusRules"},
            {"WhiteIpList", "superuser", "edit", "simple", "White IP Lists", "Lista IPs autorizadas", 2, "WhiteIpList"}};
        for (Object[] curRule: values){
            
            RdbmsObject insertRecord = Rdbms.insertRecord(TablesApp.APP_PROCEDURE_EVENTS, fields, curRule, TblsApp.TablesApp.APP_PROCEDURE_EVENTS.getRepositoryName());
            
            if (Boolean.TRUE.equals(insertRecord.getRunSuccess()))
                jsonObj.put("inserting_business_rule_diagn", curRule[1]+" "+insertRecord.getRunSuccess());
            else
                jsonObj.put("inserting_business_rule_diagn", curRule[1]+" "+insertRecord.getErrorMessageCode());
        }
        schemasObj.put(TablesApp.APP_PROCEDURE_EVENTS.toString(), jsonObj);
*/
        


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
            tblCreateScript = createTableScript(curTbl, procInstanceName, false, true, null);
            
            tblCreateScript = createTableScript(curTbl, LPPlatform.buildSchemaName(procInstanceName, curTbl.getRepositoryName()), false, true, null);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});
            String schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curTbl.getRepositoryName()), curTbl.getTableName());
            if (Boolean.FALSE.equals(schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curTbl.getRepositoryName())))){
                String tblCreateScriptTesting = createTableScript(curTbl, schemaForTesting, false, true, null);
                prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScriptTesting, new Object[]{});
            }
            //Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});
            JSONObject scriptLog=new JSONObject();
            scriptLog.put("script", tblCreateScript);
            if (Boolean.FALSE.equals(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE)) && Boolean.FALSE.equals(tblCreateScript.toLowerCase().contains("already")) )            
                scriptLog.put("creator_diagn", prepUpQuery[prepUpQuery.length-1]);
            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains(GlobalAPIsParams.LBL_ERROR))
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
            if (configSchemaName.contains("-") && (Boolean.FALSE.equals(configSchemaName.startsWith("\"")))){            
                configSchemaName = "\""+configSchemaName+"\"";}
            Object[] dbSchemaExists = Rdbms.dbSchemaExists(configSchemaName);
            SchemaActions schemaAction = SchemaActions.valueOf(actionToPerform);
            switch (schemaAction){
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
        sw.addConstraint(TblsProcedure.ProcedureViews.NAME, WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, "");
        Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, sw, procInstanceName);
        Object[] insertRecordInTableFromTable = insertRecordInTableFromTable(true, 
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableFields()),
                    GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(), 
                new String[]{TblsReqs.ProcedureReqSolution.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolution.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName()},
                new Object[]{procedure, procVersion, procInstanceName},
                LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), 
                    TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableName(), getAllFieldNames(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableFields()));
        JSONObject jsonObj = new JSONObject();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTableFromTable[0].toString())){
            jsonObj.put("error_cloning_from_requirements_to_procedure", Arrays.toString(insertRecordInTableFromTable));
            return jsonObj;
        }     
        jsonObj.put("success_cloning_from_requirements_to_procedure", insertRecordInTableFromTable[insertRecordInTableFromTable.length-2]+":"+insertRecordInTableFromTable[insertRecordInTableFromTable.length-1]);
//        jsonObj.put("Diagnostic from createDBProcedureEvents", insertRecordInTableFromTable[0].toString());
        String[] procEventFldNamesToGet=getAllFieldNames(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableFields());
        Object[][] procEventRows = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableName(), 
                //new String[]{TblsProcedure.ProcedureEvents.ROLE_NAME.getName(), WHERECLAUSE_TYPES.OR.getSqlClause()+" "+TblsProcedure.ProcedureEvents.ROLE_NAME.getName()+" "+WHERECLAUSE_TYPES.LIKE}, 
                //new Object[]{"ALL", "%|%"}, 
                new String[]{TblsProcedure.ProcedureViews.ROLE_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{},
                procEventFldNamesToGet);
        JSONArray multiRolejArr=new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventRows[0][0].toString()))){
            Object[][] procRoles = new Object[][]{{}};
                Object[][] procRolesAllRoles = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_ROLES.getTableName(), 
                    new String[]{TblsReqs.ProcedureRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRoles.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName()},
                    new Object[]{procedure, procVersion, procInstanceName},
                    new String[]{TblsReqs.ProcedureRoles.ROLE_NAME.getName()});
            
            for (Object[] curProcEvent: procEventRows){
                JSONObject multiRolCurEvent=new JSONObject();
                multiRolCurEvent.put("event_name", 
                    curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.NAME.getName())]);
                String multiRolesLog="";
                if ("ALL".equalsIgnoreCase(curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.ROLE_NAME.getName())].toString())){
                    procRoles=procRolesAllRoles;                    
                    multiRolesLog=multiRolesLog+" as for all roles, trying addition for "+LPArray.convertArrayToString(LPArray.getColumnFromArray2D(procRoles, 0),", ", "", true);
                }else{
                    procRoles=LPArray.array1dTo2d(curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.ROLE_NAME.getName())].toString().split("\\|"), 1);
                    multiRolesLog=multiRolesLog+" as for multiple roles, trying addition for "+LPArray.convertArrayToString(LPArray.getColumnFromArray2D(procRoles, 0),", ", "", true);                    
                }
                multiRolCurEvent.put("multirole_type", multiRolesLog);
                for (int i=0;i<procRoles.length;i++){
                    if (i==0){
                        SqlWhere sqlWhere = new SqlWhere();
                        sqlWhere.addConstraint(TblsProcedure.ProcedureViews.ROLE_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.ROLE_NAME.getName())].toString()}, "");
                        sqlWhere.addConstraint(TblsProcedure.ProcedureViews.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.NAME.getName())]}, "");
                        Object[] diagnoses=Rdbms.updateRecordFieldsByFilter(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS,
                            EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, new String[]{TblsProcedure.ProcedureViews.ROLE_NAME.getName()}), new Object[]{procRoles[0][0].toString()}, sqlWhere, procInstanceName);
                        multiRolCurEvent.put("updated?", Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())));
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString()))
                            multiRolCurEvent.put("update error log", Arrays.toString(diagnoses));
                    }else{
                    
                        curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.ROLE_NAME.getName())]=procRoles[i][0].toString();
                        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, procEventFldNamesToGet, curProcEvent);
                        multiRolCurEvent.put("inserted?", insertRecordInTable.getRunSuccess());
                        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess()))
                            multiRolCurEvent.put("insert error log", 
                                insertRecordInTable.getErrorMessageCode()+" "+Arrays.toString(insertRecordInTable.getErrorMessageVariables()));
                    }
                }
                if (procRoles.length>1)
                    multiRolejArr.add(multiRolCurEvent);
            }
        }
        if (Boolean.FALSE.equals(multiRolejArr.isEmpty()))
            jsonObj.put("multiroles_addition_log", multiRolejArr);
        return jsonObj;
    }
    
    /**
     *
     * @return Json object log built after running the script for the db tables creation.
     */
    
}
