package trazit.platforminstance.logic;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.DbObjects;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.TblsApp;
import databases.TblsApp.TablesApp;
import databases.TblsAppAudit;
import databases.TblsAppConfig;
import databases.TblsCnfg;
import databases.TblsProcedure;
import databases.features.DbEncryption;
import functionaljavaa.datatransfer.FromInstanceToInstance;
import functionaljavaa.parameter.Parameter;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTables;
import static trazit.enums.deployrepository.DeployTables.createTableScript;
import static trazit.globalvariables.GlobalVariables.PROC_MANAGEMENT_SPECIAL_ROLE;
import trazit.procedureinstance.definition.definition.TblsReqs;

/**
 *
 * @author User
 */
public class CreatePlatform {
    private String diagnSummary="";
    private JSONObject executionLog = new JSONObject();
    private JSONArray errorsList = new JSONArray();
    
    public CreatePlatform(String platformName){
        diagnSummary="";
        executionLog = new JSONObject();
        errorsList = new JSONArray();        
        createBasicSchemasAndTablesStructure(platformName);
        createAppProcTables(platformName);
        createModules(platformName);
    }

    public JSONObject publishReport(){
        JSONObject summary = new JSONObject();
        String conclusionStr="";
        if (this.errorsList.isEmpty()){
            conclusionStr="All correct, no problems";
        }else{
            conclusionStr="Found "+this.errorsList.size()+" errors";
            summary.put("errors_found", errorsList);
        }        
        this.executionLog.put("conclusion", conclusionStr);
        if (this.errorsList.size()>0)
            this.executionLog.put("errors_detected", summary);
        return executionLog;
    }

    private void addToLogSummary(String entryName, JSONArray jArr){
        this.executionLog.put(entryName, jArr);
    }
    private void addToLogSummary(String entryName, JSONObject jObj){
        this.executionLog.put(entryName, jObj);
    }
    private void addToLogSummary(String entryName, String jObj){
        this.executionLog.put(entryName, jObj);
    }

    private Boolean isError(EnumIntTables curTbl,  Object[] prepUpQuery, String tblCreateScript){
        if (tblCreateScript.toUpperCase().contains("ALREADY")&&tblCreateScript.toUpperCase().contains("EXIST")){
            return false;
        }            
        if (Boolean.FALSE.equals(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE)) && Boolean.FALSE.equals(tblCreateScript.toLowerCase().contains("already"))){
            this.errorsList.add(infoToReport(curTbl, tblCreateScript));
            return true;
        }
        if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains(GlobalAPIsParams.LBL_ERROR)){
            this.errorsList.add(infoToReport(curTbl, tblCreateScript));
            return true;
        }
        return false;        
    }
    private String infoToReport(EnumIntTables curTbl, String tblCreateScript){
        return curTbl.getRepositoryName()+"."+curTbl.getTableName()+" Error: "+tblCreateScript;
    }
    Boolean isErrorForInsertRecord(EnumIntTables curTbl,  RdbmsObject insertRecord, String keyValue){
        return isErrorForInsertRecord(curTbl, insertRecord, keyValue, null);
    }   
    Boolean isErrorForInsertRecord(EnumIntTables curTbl,  RdbmsObject insertRecord, String keyValue, String errorDetailStr){
        if (insertRecord!=null&&Boolean.TRUE.equals(insertRecord.getRunSuccess())){
            return false;  
        }else{
            JSONObject log= new JSONObject();
            if (insertRecord!=null){
                Object[] errorDetail=insertRecord.getErrorMessageVariables();
                errorDetailStr=errorDetail[errorDetail.length-1].toString();
            }
            if (errorDetailStr.toUpperCase().contains("ALREADY")&&errorDetailStr.toUpperCase().contains("EXIST")){
                return false;  
            }
            if (insertRecord!=null&&"RDBMS_RECORD_CREATED".equalsIgnoreCase(insertRecord.getErrorMessageCode().getErrorCode())){
                return false;
            }
            log.put("inserting_procedure_info_"+"app",insertRecord!=null?insertRecord.getErrorMessageCode():errorDetailStr);
            this.errorsList.add(log);
            return true;
        }
    }
    String infoToReportForInsertRecord(EnumIntTables curTbl, RdbmsObject insertRecord, String keyValue, Boolean isError){
        if (Boolean.FALSE.equals(isError)){
            Object[] errorDetail=insertRecord.getErrorMessageVariables();
            if (errorDetail!=null){
                String errorDetailStr=errorDetail[errorDetail.length-1].toString();
                if (errorDetailStr.toUpperCase().contains("ALREADY")&&errorDetailStr.toUpperCase().contains("EXIST")){
                    return "Record "+keyValue+" already exists";
                }else{            
                    return "Added "+keyValue+" properly";
                }
            }else
                return "Added "+keyValue+" properly";
        }
        else{
            return "Error adding "+keyValue+". Error: "+insertRecord.getErrorMessageCode();
        }
    }

    
    private void createBasicSchemasAndTablesStructure(String platformName){
        String tblCreateScript="";
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());        
        EnumIntTables[] tablesToTransferData=new EnumIntTables[]{
            TblsCnfg.TablesConfig.UOM
        };
        List<EnumIntTables[]> enumArrays = new ArrayList<>();
        enumArrays.add(TblsApp.TablesApp.values());
        enumArrays.add(TblsAppAudit.TablesAppAudit.values());
        enumArrays.add(TblsAppConfig.TablesAppConfig.values());
        enumArrays.add(TblsReqs.TablesReqs.values());        
        
        JSONObject mainBlockReport=new JSONObject();
        
        JSONObject allSchemasObj=new JSONObject();
        TblsApp.TablesApp[] tblsApp = TblsApp.TablesApp.values();
        for (EnumIntTables[] enumArray : enumArrays) {
            org.json.JSONObject curSchemaObj=new org.json.JSONObject();
            curSchemaObj.put("repository", enumArray[0].getRepositoryName());
            JSONArray curSchemaArr=new JSONArray();
            for (EnumIntTables curTbl: enumArray){
                org.json.JSONObject curTblObj=new org.json.JSONObject();
                tblCreateScript = createTableScript(curTbl, null, false, true, null);
                Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});
                curTblObj.put("table", curTbl.getTableName());
                Boolean hadError=isError(curTbl, prepUpQuery, tblCreateScript);
                curTblObj.put("Errors_found?", hadError);
                if (hadError){
                    curTblObj.put("error", tblCreateScript);
                }else{
                    curTblObj.put("script", tblCreateScript);            
                }
                curSchemaArr.add(curTblObj);
            }
            curSchemaObj.put("tables", curSchemaArr);
            allSchemasObj.put(enumArray[0].getRepositoryName(), curSchemaObj);
        }
        
        mainBlockReport.put("schemas_and_tables", allSchemasObj);
        JSONObject allTablesLogObj=new JSONObject();
        for (EnumIntTables curTable: tablesToTransferData){
            org.json.JSONObject curTableLogObj=new org.json.JSONObject();
            Object[] tableContent = FromInstanceToInstance.tableContent(curTable, dbTrazitModules, platformName);
            String detail="Transfer Data for "+curTable.getRepositoryName()+"."+curTable.getTableName()+" from "+dbTrazitModules;
            tblCreateScript=tableContent[tableContent.length-2].toString();
            org.json.JSONObject curTblObj=new org.json.JSONObject();
            curTableLogObj.put("script", tblCreateScript); 
            curTableLogObj.put("detail", detail);
            Boolean hadError=isError(curTable, tableContent, tblCreateScript);
            curTblObj.put("Errors_found?", hadError);
            if (hadError){
                curTableLogObj.put("error", tblCreateScript);
            }else{
                curTableLogObj.put("script", tblCreateScript);            
            }            
            allTablesLogObj.put(curTable.getTableName(), curTableLogObj);
        }
        
        mainBlockReport.put("tables_records", allTablesLogObj);
        addToLogSummary("main_platform_structure", mainBlockReport);
    }

    private void createAppProcTables(String platformName){        
        JSONObject allLog=new JSONObject();
        EnumIntTables[] appProcTables=new EnumIntTables[]{TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, TblsProcedure.TablesProcedure.PROCEDURE_INFO, TblsProcedure.TablesProcedure.PROCEDURE_EVENTS, TblsProcedure.TablesProcedure.PERSON_PROFILE};
        JSONObject curSchemaObj=new JSONObject();
        JSONObject prcDeplSectionLog=new JSONObject();
        JSONObject prcReqsSectionLog=new JSONObject();
        for (EnumIntTables curTbl: appProcTables){
            org.json.JSONObject curTblObj=new org.json.JSONObject();
            String tblCreateScript = createTableScript(curTbl, "app", false, true, null);
            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript, new Object[]{});
            curTblObj.put("table", curTbl.getTableName());
            Boolean hadError=isError(curTbl, prepUpQuery, tblCreateScript);
            curTblObj.put("Errors_found?", hadError);
            if (hadError){
                curTblObj.put("error", tblCreateScript);
            }else{
                curTblObj.put("script", tblCreateScript);            
            }
            curSchemaObj.put(curTbl.getTableName(), curTblObj);
        }
        allLog.put("app_procedure_tables", curSchemaObj);

        
        JSONObject recordsLogObj=new JSONObject();
        RdbmsObject insertRecord2 = Rdbms.insertRecord(TblsProcedure.TablesProcedure.PROCEDURE_INFO, 
                new String[]{TblsProcedure.ProcedureInfo.NAME.getName(), TblsProcedure.ProcedureInfo.VERSION.getName(), TblsProcedure.ProcedureInfo.PROCEDURE_HASH_CODE.getName(),
                    TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName(), TblsProcedure.ProcedureInfo.MODULE_NAME.getName(), 
                    TblsProcedure.ProcedureInfo.INCLUDE_CONFIG_CHANGES.getName(), TblsProcedure.ProcedureInfo.ENABLE_CHANGE_TRACKING.getName(), TblsProcedure.ProcedureInfo.CREATE_PICT_ONGCHNGE.getName()}, 
                new Object[]{"app", 1, -1, "app", "app", true, true, false}, TblsApp.TablesApp.APP_PERSON_PROFILE.getRepositoryName());
        Boolean errorForInsertRecord = isErrorForInsertRecord(TblsProcedure.TablesProcedure.PROCEDURE_INFO, insertRecord2, "app");
        prcDeplSectionLog.put(TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(), 
                infoToReportForInsertRecord(TblsProcedure.TablesProcedure.PROCEDURE_INFO, insertRecord2, "app", errorForInsertRecord));
                
        String[] fields=new String[]{TblsProcedure.ProcedureBusinessRules.AREA.getName(), TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName(),
            TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName(), TblsProcedure.ProcedureBusinessRules.DISABLED.getName()};
        Object[][] values=new Object[][]{{"frontend_locksession", "enableLockSession", "true", false},
            {"frontend_locksession", "enableLogoutSession", "true", false},
            {"frontend_locksession", "minsLockSession", "2", false},
            {"frontend_locksession", "minsLogoutSession", "5", false},
            {"frontend_locksession", "secondsNextTimeChecker", "60", false},
            {"procedure", "windowOpenableWhenNotSopCertifiedUserSopCertification", "NO", false}};
        JSONArray jArr=new JSONArray();
        for (Object[] curRule: values){            
            RdbmsObject insertRecord = Rdbms.insertRecord(TblsApp.TablesApp.APP_BUSINESS_RULES, fields, curRule,
TblsApp.TablesApp.APP_BUSINESS_RULES.getRepositoryName());
            errorForInsertRecord = isErrorForInsertRecord(TblsApp.TablesApp.APP_BUSINESS_RULES, insertRecord, curRule[1].toString());
            jArr.add(infoToReportForInsertRecord(TblsApp.TablesApp.APP_BUSINESS_RULES, insertRecord, curRule[1].toString(), errorForInsertRecord));                        
        }        
        prcDeplSectionLog.put(TblsApp.TablesApp.APP_BUSINESS_RULES.getTableName(), jArr);
        fields=new String[]{TblsProcedure.ProcedureEvents.NAME.getName(), TblsProcedure.ProcedureEvents.ROLE_NAME.getName(),
            TblsProcedure.ProcedureEvents.MODE.getName(), TblsProcedure.ProcedureEvents.TYPE.getName(),
            TblsProcedure.ProcedureEvents.LABEL_EN.getName(), TblsProcedure.ProcedureEvents.LABEL_ES.getName(), 
            TblsProcedure.ProcedureEvents.ORDER_NUMBER.getName(), TblsProcedure.ProcedureEvents.LP_FRONTEND_PAGE_NAME.getName()};
        values=new Object[][]{{"BlackIpList", "superuser", "edit", "simple", "Black IP Lists", "Lista IPs denegadas", 1, "BlackIpList" },
            {"PlatformBusRules", "superuser", "edit", "simple", "Business Rules", "Reglas de Negocio", 3, "PlatformBusRules"},
            {"WhiteIpList", "superuser", "edit", "simple", "White IP Lists", "Lista IPs autorizadas", 2, "WhiteIpList"}};
        jArr=new JSONArray();
        for (Object[] curRule: values){            
            RdbmsObject insertRecord = Rdbms.insertRecord(TblsApp.TablesApp.APP_PROCEDURE_EVENTS, fields, curRule,
TblsApp.TablesApp.APP_BUSINESS_RULES.getRepositoryName());
            errorForInsertRecord = isErrorForInsertRecord(TblsApp.TablesApp.APP_PROCEDURE_EVENTS, insertRecord, curRule[0].toString());
            jArr.add(infoToReportForInsertRecord(TblsApp.TablesApp.APP_PROCEDURE_EVENTS, insertRecord, curRule[0].toString(), errorForInsertRecord));                        
        }        
        prcDeplSectionLog.put(TblsApp.TablesApp.APP_PROCEDURE_EVENTS.getTableName(), jArr);
        
        String procNameInReqs="platform-settings";
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
        errorForInsertRecord = isErrorForInsertRecord(TblsApp.TablesApp.USERS, insertRecord2, "admin");
        prcDeplSectionLog.put(TblsApp.TablesApp.USERS.getTableName(), 
                infoToReportForInsertRecord(TblsApp.TablesApp.USERS, insertRecord2, "admin", errorForInsertRecord));

        insertRecord2 = Rdbms.insertRecord(TablesApp.APP_PERSON_PROFILE, 
                new String[]{TblsProcedure.PersonProfile.PERSON_NAME.getName(), TblsProcedure.PersonProfile.ROLE_NAME.getName(), TblsProcedure.PersonProfile.ACTIVE.getName()}, 
                new Object[]{persEncrypted, "superuser", true}, TblsApp.TablesApp.APP_PERSON_PROFILE.getRepositoryName());
        errorForInsertRecord = isErrorForInsertRecord(TblsApp.TablesApp.APP_PERSON_PROFILE, insertRecord2, "admin-superuser");
        prcDeplSectionLog.put(TblsApp.TablesApp.APP_PERSON_PROFILE.getTableName(), 
                infoToReportForInsertRecord(TblsApp.TablesApp.APP_PERSON_PROFILE, insertRecord2, "admin-superuser", errorForInsertRecord));
        

        insertRecord2 = Rdbms.insertRecord(TablesApp.APP_PERSON_PROFILE, 
                new String[]{TblsProcedure.PersonProfile.PERSON_NAME.getName(), TblsProcedure.PersonProfile.ROLE_NAME.getName(), TblsProcedure.PersonProfile.ACTIVE.getName()}, 
                new Object[]{persEncrypted, PROC_MANAGEMENT_SPECIAL_ROLE, true}, TblsApp.TablesApp.APP_PERSON_PROFILE.getRepositoryName());
        errorForInsertRecord = isErrorForInsertRecord(TblsApp.TablesApp.APP_PERSON_PROFILE, insertRecord2, "admin-"+PROC_MANAGEMENT_SPECIAL_ROLE);
        prcDeplSectionLog.put(TblsApp.TablesApp.APP_PERSON_PROFILE.getTableName(), 
                infoToReportForInsertRecord(TblsApp.TablesApp.APP_PERSON_PROFILE, insertRecord2, "admin-"+PROC_MANAGEMENT_SPECIAL_ROLE, errorForInsertRecord));

        insertRecord2 = Rdbms.insertRecord(TblsApp.TablesApp.USER_PROCESS, 
                new String[]{TblsApp.UserProcess.USER_NAME.getName(), TblsApp.UserProcess.PROC_NAME.getName(), TblsApp.UserProcess.ACTIVE.getName()}, 
                new Object[]{"admin", "app", true}, TblsApp.TablesApp.APP_PERSON_PROFILE.getRepositoryName());
        errorForInsertRecord = isErrorForInsertRecord(TblsApp.TablesApp.USER_PROCESS, insertRecord2, "admin-app");
        prcDeplSectionLog.put(TblsApp.TablesApp.USER_PROCESS.getTableName(), 
                infoToReportForInsertRecord(TblsApp.TablesApp.USER_PROCESS, insertRecord2, "admin-app", errorForInsertRecord));

//SIGUE DE AQUI PARA ABAJO, LAS TABLAS NO ESTAN BIEN EN LOS LOGS, MIRA BIEN
        
        fields=new String[]{TblsReqs.ProcedureUsers.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUsers.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUsers.PROC_INSTANCE_NAME.getName(),
                    TblsReqs.ProcedureUsers.USER_NAME.getName(), TblsReqs.ProcedureUsers.FULL_NAME.getName()};
        Object[] values1D=new Object[]{procNameInReqs, 1, procNameInReqs, "admin", "admin"};        
        RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.PROC_USERS, fields, values1D, 
                TblsReqs.TablesReqs.PROC_USERS.getRepositoryName());            
        errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.PROC_USERS, insertRecord2, "admin");
        prcReqsSectionLog.put(TblsReqs.TablesReqs.PROC_USERS.getTableName(), 
                infoToReportForInsertRecord(TblsReqs.TablesReqs.PROC_USERS, insertRecord2, "admin", errorForInsertRecord));

        fields=new String[]{TblsReqs.ProcedureRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRoles.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName(),
                    TblsReqs.ProcedureRoles.ROLE_NAME.getName(), TblsReqs.ProcedureRoles.DESCRIPTION.getName()};
        values1D=new Object[]{procNameInReqs, 1, procNameInReqs, "superuser", "superuser can do everything as admin role"};
        insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.PROCEDURE_ROLES, fields, values1D, 
                TblsReqs.TablesReqs.PROCEDURE_ROLES.getRepositoryName());            
        errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.PROCEDURE_ROLES, insertRecord2, "admin-superuser");
        prcReqsSectionLog.put(TblsReqs.TablesReqs.PROCEDURE_ROLES.getTableName(), 
                infoToReportForInsertRecord(TblsReqs.TablesReqs.PROCEDURE_ROLES, insertRecord2, "adminsuperuser- ", errorForInsertRecord));
            
        fields=new String[]{TblsReqs.ProcedureUserRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRoles.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName(),
                    TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()};
        values1D=new Object[]{procNameInReqs, 1, procNameInReqs, "admin", "superuser"};
        insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.PROC_USER_ROLES, fields, values1D, 
                TblsReqs.TablesReqs.PROC_USER_ROLES.getRepositoryName());            
        errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.PROC_USER_ROLES, insertRecord2, "admin-superuser");
        prcReqsSectionLog.put(TblsReqs.TablesReqs.PROC_USER_ROLES.getTableName(), 
                infoToReportForInsertRecord(TblsReqs.TablesReqs.PROC_USER_ROLES, insertRecord2, "admin-superuser", errorForInsertRecord));
        
        fields=new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName(),
                    TblsReqs.ProcedureInfo.ACTIVE.getName(), TblsReqs.ProcedureInfo.LOCKED_FOR_ACTIONS.getName(),
                    TblsReqs.ProcedureInfo.MODULE_NAME.getName(), TblsReqs.ProcedureInfo.DESCRIPTION.getName(), TblsReqs.ProcedureInfo.LABEL_EN.getName(), TblsReqs.ProcedureInfo.LABEL_ES.getName(), TblsReqs.ProcedureInfo.PROCEDURE_HASH_CODE.getName()};
        LocalDateTime currentTimeStamp = LPDate.getCurrentTimeStamp();
        int hashCode = currentTimeStamp.hashCode();        
        values1D=new Object[]{procNameInReqs, 1, procNameInReqs, true, false, "APP", "Platform settings", "Platform settings", "Configuración de Plataforma", hashCode};
        insertRecord2 = Rdbms.insertRecord(TblsReqs.TablesReqs.PROCEDURE_INFO, fields, values1D, TblsReqs.TablesReqs.PROCEDURE_INFO.getRepositoryName());            
        errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.PROCEDURE_INFO, insertRecord2, procNameInReqs);
        prcReqsSectionLog.put(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(), 
                infoToReportForInsertRecord(TblsReqs.TablesReqs.PROCEDURE_INFO, insertRecord2, "admin-superuser", errorForInsertRecord));


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
        errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.PROC_FE_MODEL, insertRecord2, procNameInReqs);
        prcReqsSectionLog.put(TblsReqs.TablesReqs.PROC_FE_MODEL.getTableName(), 
                infoToReportForInsertRecord(TblsReqs.TablesReqs.PROC_FE_MODEL, insertRecord2, "admin-superuser", errorForInsertRecord));
        
        fields=new String[]{TblsReqs.ProcedureModuleTables.PROCEDURE_NAME.getName(), TblsReqs.ProcedureModuleTables.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureModuleTables.PROC_INSTANCE_NAME.getName(),
                    TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.TABLE_NAME.getName(), TblsReqs.ProcedureModuleTables.IS_VIEW.getName()};
        values1D=new Object[]{procNameInReqs, 1, procNameInReqs, TblsApp.TablesApp.IP_BLACK_LIST.getRepositoryName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), false};
        insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.PROC_MODULE_TABLES, fields, values, 
                TblsReqs.TablesReqs.PROC_MODULE_TABLES.getRepositoryName());            
        errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.PROC_MODULE_TABLES, insertRecord2, "admin-superuser");
        prcReqsSectionLog.put(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableName(), 
                infoToReportForInsertRecord(TblsReqs.TablesReqs.PROC_MODULE_TABLES, insertRecord2, "admin-superuser", errorForInsertRecord));


        recordsLogObj.put("app_procedure_deployment", prcDeplSectionLog);
        recordsLogObj.put("requirements", prcReqsSectionLog);             
        
        addToLogSummary("add_platform_admin", recordsLogObj);
    }

    private void createModules(String platformName){
        String directoryPath = "ModulesInfo"; // Update with the actual path
        JSONArray allModuleFilesInfo=new JSONArray();
        ClassInfoList classesImplementingEndPoints = null;
        try (io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {        
            classesImplementingEndPoints = scanResult.getClassesImplementing("trazit.enums.EnumIntEndpoints"); 
        
        // Initialize the JSON object
        JSONObject prcReqsSectionLog = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        
        ClassLoader classLoader = CreatePlatform.class.getClassLoader();
        URL directoryUrl = classLoader.getResource(directoryPath);
        if (directoryUrl != null) {
            // Get the file path from the URL
            String directoryFilePath = directoryUrl.getPath();

            // Create a File object representing the directory
            File directory = new File(directoryFilePath);

            // List files in the directory
            File[] files = directory.listFiles();
            if (files == null) {
                addToLogSummary("requirement_modules", "no modules in directory to load");
                return;
            }
            for (File file : files) {
                org.json.JSONObject curSchemaObj=new org.json.JSONObject();
                String moduleName="Still not specified";
                JSONObject jFileContentObjModel = new JSONObject(); 
                try{
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        // Read the content of each text file
                        StringBuilder jsonDataModel = new StringBuilder();

                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                jsonDataModel.append(line).append("\n");
                            }
                            jFileContentObjModel = new JSONObject(jsonDataModel.toString());   
                            // Wrap the JSON data in a new JSON object under the file name
                            JSONObject fileJsonObject = new JSONObject();
                            fileJsonObject.put("information", new JSONObject(jsonDataModel.toString()));

                            // Add the file's JSON object to the main JSON object
                            jsonObject.put(file.getName(), fileJsonObject);
                            moduleName=jFileContentObjModel.getString("name");
                            Integer moduleVersion=jFileContentObjModel.getInt("version");
                            String releaseDate=jFileContentObjModel.getString("releaseDate");
                            JSONObject moduleSettings=jFileContentObjModel.getJSONObject("ModuleSettings");                            
                            String descEn=jFileContentObjModel.getString("description_en");
                            String descEs=jFileContentObjModel.getString("description_es");
                            jFileContentObjModel = new JSONObject(jsonDataModel.toString());       
                            String[] fieldNames = new String[]{TblsReqs.Modules.MODULE_NAME.getName(), TblsReqs.Modules.MODULE_VERSION.getName(),
                                TblsReqs.Modules.DESCRIPTION_EN.getName(), TblsReqs.Modules.DESCRIPTION_ES.getName(),
                                TblsReqs.Modules.INFO_JSON.getName(), TblsReqs.Modules.ACTIVE.getName(), TblsReqs.Modules.MODULE_SETTINGS.getName()
                            };
                            Object[] fieldValues = new Object[]{moduleName, moduleVersion, descEn, descEs, jFileContentObjModel, true, moduleSettings};
                            RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.MODULES, fieldNames, fieldValues, TblsReqs.TablesReqs.MODULES.getRepositoryName());
                            Boolean errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.MODULES, insertRecord, moduleName);
                            prcReqsSectionLog.put(TblsReqs.TablesReqs.MODULES.getTableName(), 
                            infoToReportForInsertRecord(TblsReqs.TablesReqs.MODULES, insertRecord, moduleName, errorForInsertRecord));
                            JSONObject curModuleInfo = new JSONObject();
                            curModuleInfo.put("moduleName", moduleName);
                            curModuleInfo.put("file_content", jFileContentObjModel);
                            curModuleInfo.put("detail", infoToReportForInsertRecord(TblsReqs.TablesReqs.MODULES, insertRecord, moduleName, errorForInsertRecord));
                            curModuleInfo.put("getModuleActionsAndQueries", getModuleActionsAndQueries(moduleName, moduleVersion, jFileContentObjModel, classesImplementingEndPoints));
                            allModuleFilesInfo.add(curModuleInfo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }catch(Exception e){
                    JSONObject curModuleInfo = new JSONObject();
                    curModuleInfo.put("moduleName", moduleName);
                    curModuleInfo.put("file_content", jFileContentObjModel);
                    curModuleInfo.put("error", e.getMessage());
                    allModuleFilesInfo.add(curModuleInfo);
                }
            }                            
        }
        }
        addToLogSummary("requirement_modules", allModuleFilesInfo);
    }                

    public JSONArray getModuleActionsAndQueries(String moduleName, Integer moduleVersion, JSONObject jFileContentObjModel, ClassInfoList classesImplementingEndPoints){
        JSONObject jMainObj=new JSONObject();
        
        JSONArray allApisArr=new JSONArray();
        org.json.JSONArray moduleApis=jFileContentObjModel.getJSONArray("apis");
        for (int i = 0; i < moduleApis.length(); i++) {
            // Get the current element as a JSONObject
            Object get = moduleApis.get(i);
            String curApi=get.toString();
            JSONObject curApiObj=new JSONObject();
            curApiObj.put("api_name", curApi);
            ClassInfo getMine = null; // classesImplementingEndPoints.get(curApi);
            List<Object> enumConstantObjects = null; // getMine.getEnumConstantObjects();
            ClassInfo selectedEnum=null;
            for (int j = 0; j < classesImplementingEndPoints.size(); j++) {
                ClassInfo enumObject = classesImplementingEndPoints.get(j);
                String curEndpointName=enumObject.getName().toUpperCase();
                // Check if the enumObject's class name matches the enumNameToFind
                if (curEndpointName.contains(curApi.toUpperCase())) {
                    // Add the enumObject to your JSONArray or perform any other desired action
                    //apiEndpointsArr.put(enumObject);
//                    getMine=enumObject;
                    selectedEnum= enumObject;
                    enumConstantObjects = enumObject.getEnumConstantObjects();
                    continue;
                }
            }            
            if (enumConstantObjects==null){
                Boolean errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.MODULE_ACTIONS_N_QUERIES, null, curApi);
                curApiObj.put("error", curApi+" not found.");
            }else{
                JSONArray apiEndpointsArr=new JSONArray();
                Object[][] values=new Object[][]{{}};
                String[] apiNameArr=selectedEnum.getName().split("\\$");
                
                String[] fields=new String[]{TblsReqs.ModuleActionsAndQueries.MODULE_NAME.getName(), TblsReqs.ModuleActionsAndQueries.MODULE_VERSION.getName(), TblsReqs.ModuleActionsAndQueries.API_NAME.getName(),
                    TblsReqs.ModuleActionsAndQueries.ENDPOINT_NAME.getName(), TblsReqs.ModuleActionsAndQueries.ENTITY.getName(), TblsReqs.ModuleActionsAndQueries.ACTIVE.getName()};
                for (int j = 0; j < enumConstantObjects.size(); j++) {
                    EnumIntEndpoints curEndpoint = (EnumIntEndpoints) enumConstantObjects.get(j);
                    apiEndpointsArr.add(curEndpoint.getName());
                    Object[] values1D=new Object[]{moduleName, moduleVersion, apiNameArr[1], curEndpoint.getName(), curEndpoint.getEntity(), true};
                    values=LPArray.array1dTo2d(LPArray.addValueToArray1D(LPArray.array2dTo1d(values),values1D), fields.length); 
                }                
                for (Object[]  curRow: values){
                    RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.MODULE_ACTIONS_N_QUERIES, fields, curRow, 
                            TblsReqs.TablesReqs.PROC_MODULE_TABLES.getRepositoryName());
                    Boolean errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.MODULE_ACTIONS_N_QUERIES, insertRecord, apiNameArr[1]+"."+curRow[3]);
                    jMainObj.put(TblsReqs.TablesReqs.MODULE_ACTIONS_N_QUERIES.getTableName()+"."+apiNameArr[1]+"."+curRow[3].toString(), 
                            infoToReportForInsertRecord(TblsReqs.TablesReqs.MODULE_ACTIONS_N_QUERIES, insertRecord, apiNameArr[1]+"."+curRow[3].toString(), errorForInsertRecord));
                }
                curApiObj.put("endpoints", apiEndpointsArr);
                curApiObj.put("inserts_log", jMainObj);
            }
            allApisArr.add(curApiObj);
        }
        return allApisArr;
    }
    
}