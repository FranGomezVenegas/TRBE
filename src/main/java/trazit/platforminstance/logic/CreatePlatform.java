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
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import static databases.DbObjects.createSchemas;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntViews;
import trazit.globalvariables.GlobalVariables;
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
    private Boolean isError(EnumIntViews curView,  Object[] prepUpQuery, String tblCreateScript){
        if (tblCreateScript.toUpperCase().contains("ALREADY")&&tblCreateScript.toUpperCase().contains("EXIST")){
            return false;
        }            
        if (Boolean.FALSE.equals(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE)) && Boolean.FALSE.equals(tblCreateScript.toLowerCase().contains("already"))){
            this.errorsList.add(infoToReport(curView, tblCreateScript));
            return true;
        }
        if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains(GlobalAPIsParams.LBL_ERROR)){
            this.errorsList.add(infoToReport(curView, tblCreateScript));
            return true;
        }
        return false;        
    }
    private String infoToReport(EnumIntTables curTbl, String tblCreateScript){
        return curTbl.getRepositoryName()+"."+curTbl.getTableName()+" Error: "+tblCreateScript;
    }
    private String infoToReport(EnumIntViews curView, String tblCreateScript){
        return curView.getRepositoryName()+"."+curView.getViewName()+" Error: "+tblCreateScript;
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
        String viewCreateScript="";
        JSONObject mainBlockReport=new JSONObject();
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
        
        String[] schemaNames = new String[]{GlobalVariables.Schemas.APP_AUDIT.getName(),
            GlobalVariables.Schemas.CONFIG.getName(), GlobalVariables.Schemas.REQUIREMENTS.getName(),
            GlobalVariables.Schemas.APP.getName(),
            //GlobalVariables.Schemas.APP_BUSINESS_RULES.getName(),
            GlobalVariables.Schemas.APP_PROCEDURE.getName()};
        JSONArray createSchemas = createSchemas(schemaNames, platformName);
        mainBlockReport.put("base_platform_schemas", createSchemas);
        
        
        JSONObject allSchemasObj=new JSONObject();
        TblsApp.TablesApp[] tblsApp = TblsApp.TablesApp.values();
        for (EnumIntTables[] enumArray : enumArrays) {
            org.json.JSONObject curSchemaObj=new org.json.JSONObject();
            curSchemaObj.put("repository", enumArray[0].getRepositoryName());
            JSONArray curSchemaArr=new JSONArray();
            for (EnumIntTables curTbl: enumArray){
                org.json.JSONObject curTblObj=new org.json.JSONObject();
                viewCreateScript = createTableScript(curTbl, null, false, true, null);
                Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), viewCreateScript, new Object[]{});
                curTblObj.put("table", curTbl.getTableName());
                Boolean hadError=isError(curTbl, prepUpQuery, viewCreateScript);
                curTblObj.put("Errors_found?", hadError);
                if (hadError){
                    curTblObj.put("error", viewCreateScript);
                }else{
                    curTblObj.put("script", viewCreateScript);            
                }
                curSchemaArr.add(curTblObj);
            }
            curSchemaObj.put("tables", curSchemaArr);
            allSchemasObj.put(enumArray[0].getRepositoryName(), curSchemaObj);
        }
//        org.json.JSONObject curSchemaObj=new org.json.JSONObject();
        JSONArray allViewsArr=new JSONArray();
        for (TblsReqs.ViewsReqs curView : TblsReqs.ViewsReqs.values()) {
//            curSchemaObj.put("repository", curView.getRepositoryName());
            
//            for (EnumIntTables curTbl: enumArray){
                org.json.JSONObject curViewObj=new org.json.JSONObject();
                viewCreateScript = EnumIntViews.getViewScriptCreation(curView, "", false, false, false, null);
                Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curView.getRepositoryName(), curView.getViewName(), viewCreateScript, new Object[]{});
                curViewObj.put("view", curView.getViewName());
                Boolean hadError=isError(curView, prepUpQuery, viewCreateScript);
                curViewObj.put("Errors_found?", hadError);
                if (hadError){
                    curViewObj.put("error", viewCreateScript);
                }else{
                    curViewObj.put("script", viewCreateScript);            
                }
                curViewObj.put("repository", curView.getRepositoryName());
                allViewsArr.add(curViewObj);
//            }
        }        
        mainBlockReport.put("views", allViewsArr);
//        allSchemasObj.put(enumArray[0].getRepositoryName(), curSchemaObj);
        mainBlockReport.put("schemas_and_tables", allSchemasObj);
        JSONObject allTablesLogObj=new JSONObject();
        for (EnumIntTables curTable: tablesToTransferData){
            org.json.JSONObject curTableLogObj=new org.json.JSONObject();
            Object[] tableContent = FromInstanceToInstance.tableContent(curTable, dbTrazitModules, platformName);
            String detail="Transfer Data for "+curTable.getRepositoryName()+"."+curTable.getTableName()+" from "+dbTrazitModules;
            viewCreateScript=tableContent[tableContent.length-2].toString();
            org.json.JSONObject curTblObj=new org.json.JSONObject();
            curTableLogObj.put("script", viewCreateScript); 
            curTableLogObj.put("detail", detail);
            Boolean hadError=isError(curTable, tableContent, viewCreateScript);
            curTblObj.put("Errors_found?", hadError);
            if (hadError){
                curTableLogObj.put("error", viewCreateScript);
            }else{
                curTableLogObj.put("script", viewCreateScript);            
            }            
            allTablesLogObj.put(curTable.getTableName(), curTableLogObj);
        }
        
        mainBlockReport.put("tables_records", allTablesLogObj);
        addToLogSummary("main_platform_structure", mainBlockReport);
    }

    private void createAppProcTables(String platformName){        
        JSONObject allLog=new JSONObject();
        EnumIntTables[] appProcTables=new EnumIntTables[]{TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, TblsProcedure.TablesProcedure.PROCEDURE_INFO, TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, TblsProcedure.TablesProcedure.PERSON_PROFILE};
        JSONObject curSchemaObj=new JSONObject();
        JSONObject prcDeplSectionLog=new JSONObject();
        JSONObject prcReqsSectionLog=new JSONObject();
        JSONObject recordsLogObj=new JSONObject();
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
        String procNameInReqs="platform-settings";

        ClassLoader classLoader = DbObjects.class.getClassLoader();
        String[][] platfModels= new String[][]{
            {procNameInReqs, "PlatformModels/model_platform-settings.txt", "JSONObject"},
            {"app", "PlatformModels/model_app.txt", "JSONObject"},
            {"proc_management", "PlatformModels/model_proc_management.txt", "JSONArray"}
        };
        for (String[] curModel: platfModels){
            String filePath = curModel[1]; //"JavaScript/model_platform-settings.txt";
            StringBuilder jsonDataModel = new StringBuilder();    
            try (InputStream inputStream = classLoader.getResourceAsStream(filePath)) {
                if (inputStream != null) {
                    // Read the content of the text file
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));                    
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
            Object[] fieldValues=null;
            if ("JSONObject".equalsIgnoreCase(curModel[2])){
                JSONObject jObjModel = new JSONObject(jsonDataModel.toString());       
                fieldValues = new Object[]{curModel[0], 1, curModel[0], jObjModel, true};
            }else{
                //JSONArray jObjModel = new JSONArray(jsonDataModel.toString());       
                JsonArray jObjModel = JsonParser.parseString(jsonDataModel.toString()).getAsJsonArray();
                fieldValues = new Object[]{curModel[0], 1, curModel[0], jObjModel, true};
            }                        
            String[] fields = new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName(),
                TblsReqs.ProcedureInfo.ACTIVE.getName(), TblsReqs.ProcedureInfo.LOCKED_FOR_ACTIONS.getName(),
                TblsReqs.ProcedureInfo.MODULE_NAME.getName(), TblsReqs.ProcedureInfo.DESCRIPTION.getName(), TblsReqs.ProcedureInfo.LABEL_EN.getName(), TblsReqs.ProcedureInfo.LABEL_ES.getName(), TblsReqs.ProcedureInfo.PROCEDURE_HASH_CODE.getName()};
            LocalDateTime currentTimeStamp = LPDate.getCurrentTimeStamp();
            int hashCode = currentTimeStamp.hashCode();        
            Object[] values1D = new Object[]{curModel[0], 1, curModel[0], true, false, "APP", "Platform settings", "Platform settings", "Configuración de Plataforma", hashCode};
            RdbmsObject insertRecord2 = Rdbms.insertRecord(TblsReqs.TablesReqs.PROCEDURE_INFO, fields, values1D, TblsReqs.TablesReqs.PROCEDURE_INFO.getRepositoryName());            
            Boolean errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.PROCEDURE_INFO, insertRecord2, curModel[0]);
            prcReqsSectionLog.put(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName()+"_"+curModel[0], 
                    infoToReportForInsertRecord(TblsReqs.TablesReqs.PROCEDURE_INFO, insertRecord2, curModel[0], errorForInsertRecord));

            String[] fieldNames = new String[]{TblsReqs.ProcedureFEModel.PROCEDURE_NAME.getName(), TblsReqs.ProcedureFEModel.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureFEModel.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureFEModel.MODEL_JSON.getName(), TblsReqs.ProcedureFEModel.ACTIVE.getName()};            
            insertRecord2 = Rdbms.insertRecord(TblsReqs.TablesReqs.PROC_FE_MODEL, fieldNames, fieldValues, TblsReqs.TablesReqs.PROC_FE_MODEL.getRepositoryName());
            errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.PROC_FE_MODEL, insertRecord2, curModel[0]);
            prcReqsSectionLog.put(TblsReqs.TablesReqs.PROC_FE_MODEL.getTableName()+"_"+curModel[0], 
                    infoToReportForInsertRecord(TblsReqs.TablesReqs.PROC_FE_MODEL, insertRecord2, curModel[0], errorForInsertRecord));
        }        
                
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
            Boolean errorForInsertRecord = isErrorForInsertRecord(TblsApp.TablesApp.APP_BUSINESS_RULES, insertRecord, curRule[1].toString());
            jArr.add(infoToReportForInsertRecord(TblsApp.TablesApp.APP_BUSINESS_RULES, insertRecord, curRule[1].toString(), errorForInsertRecord));                        
        }        
        
            RdbmsObject insertRecord2 = Rdbms.insertRecord(TblsProcedure.TablesProcedure.PROCEDURE_INFO, 
                    new String[]{TblsProcedure.ProcedureInfo.NAME.getName(), TblsProcedure.ProcedureInfo.VERSION.getName(), TblsProcedure.ProcedureInfo.PROCEDURE_HASH_CODE.getName(),
                        TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName(), TblsProcedure.ProcedureInfo.MODULE_NAME.getName(), 
                        TblsProcedure.ProcedureInfo.INCLUDE_CONFIG_CHANGES.getName(), TblsProcedure.ProcedureInfo.ENABLE_CHANGE_TRACKING.getName(), TblsProcedure.ProcedureInfo.CREATE_PICT_ONGCHNGE.getName()}, 
                    new Object[]{procNameInReqs, 1, -1, procNameInReqs, procNameInReqs, true, true, false}, TblsApp.TablesApp.APP_PERSON_PROFILE.getRepositoryName());
            Boolean errorForInsertRecord = isErrorForInsertRecord(TblsProcedure.TablesProcedure.PROCEDURE_INFO, insertRecord2, procNameInReqs);
            prcDeplSectionLog.put(TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(), 
                    infoToReportForInsertRecord(TblsProcedure.TablesProcedure.PROCEDURE_INFO, insertRecord2, procNameInReqs, errorForInsertRecord));
        
        
        prcDeplSectionLog.put(TblsApp.TablesApp.APP_BUSINESS_RULES.getTableName(), jArr);
        fields=new String[]{TblsProcedure.ProcedureViews.NAME.getName(), TblsProcedure.ProcedureViews.ROLE_NAME.getName(),
            TblsProcedure.ProcedureViews.MODE.getName(), TblsProcedure.ProcedureViews.TYPE.getName(),
            TblsProcedure.ProcedureViews.LABEL_EN.getName(), TblsProcedure.ProcedureViews.LABEL_ES.getName(), 
            TblsProcedure.ProcedureViews.ORDER_NUMBER.getName(), TblsProcedure.ProcedureViews.LP_FRONTEND_PAGE_NAME.getName()};
        values=new Object[][]{{"BlackIpList", "superuser", "edit", "simple", "Black IP Lists", "Lista IPs denegadas", 1, "BlackIpList" },
            {"PlatformBusRules", "superuser", "edit", "simple", "Business Rules", "Reglas de Negocio", 3, "PlatformBusRules"},
            {"WhiteIpList", "superuser", "edit", "simple", "White IP Lists", "Lista IPs autorizadas", 2, "WhiteIpList"}};
        jArr=new JSONArray();
        for (Object[] curRule: values){            
            RdbmsObject insertRecord = Rdbms.insertRecord(TblsApp.TablesApp.APP_PROCEDURE_VIEWS, fields, curRule,
TblsApp.TablesApp.APP_BUSINESS_RULES.getRepositoryName());
            errorForInsertRecord = isErrorForInsertRecord(TblsApp.TablesApp.APP_PROCEDURE_VIEWS, insertRecord, curRule[0].toString());
            jArr.add(infoToReportForInsertRecord(TblsApp.TablesApp.APP_PROCEDURE_VIEWS, insertRecord, curRule[0].toString(), errorForInsertRecord));                        
        }        
        prcDeplSectionLog.put(TblsApp.TablesApp.APP_PROCEDURE_VIEWS.getTableName(), jArr);
        
        String fakeEsingn="firmademo";
        String defaultMail="info@trazit.net";
        Object[] encryptValue=DbEncryption.encryptValue(fakeEsingn);        
        String fakeEsingnEncrypted = encryptValue[encryptValue.length-1].toString();
        Object[] encryptPa=DbEncryption.encryptValue("trazit4ever");        
        String paEncrypted = encryptPa[encryptPa.length-1].toString();
        Object[] encryptPers=DbEncryption.encryptValue("adminz");        
        String persEncrypted = encryptPers[encryptPers.length-1].toString();
        insertRecord2 = Rdbms.insertRecord(TblsApp.TablesApp.USERS, 
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
        

        fields=new String[]{TblsReqs.ProcedureModuleTables.PROCEDURE_NAME.getName(), TblsReqs.ProcedureModuleTables.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureModuleTables.PROC_INSTANCE_NAME.getName(),
                    TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.TABLE_NAME.getName(), TblsReqs.ProcedureModuleTables.IS_VIEW.getName()};
        values1D=new Object[]{procNameInReqs, 1, procNameInReqs, TblsApp.TablesApp.IP_BLACK_LIST.getRepositoryName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), false};
        insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.PROC_MODULE_TABLES, fields, values, 
                TblsReqs.TablesReqs.PROC_MODULE_TABLES.getRepositoryName());            
        errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.PROC_MODULE_TABLES, insertRecord2, procNameInReqs);
        prcReqsSectionLog.put(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableName(), 
                infoToReportForInsertRecord(TblsReqs.TablesReqs.PROC_MODULE_TABLES, insertRecord2, procNameInReqs, errorForInsertRecord));
        

        recordsLogObj.put("app_procedure_deployment", prcDeplSectionLog);
        recordsLogObj.put("requirements", prcReqsSectionLog);             
        
        addToLogSummary("add_platform_admin", recordsLogObj);
    }

    private void createModules(String platformName){
        String directoryPath = "ModulesInfo"; // Update with the actual path
        JSONArray allModuleFilesInfo=new JSONArray();
        ClassInfoList classesImplementingEndPoints = null;
        ClassInfoList classesImplementingBusinessRules = null;
        ClassInfoList classesImplementingErrorMessages = null;
        try (io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {        
            classesImplementingEndPoints = scanResult.getClassesImplementing("trazit.enums.EnumIntEndpoints"); 
            classesImplementingBusinessRules = scanResult.getClassesImplementing("trazit.enums.EnumIntBusinessRules"); 
            classesImplementingErrorMessages = scanResult.getClassesImplementing("trazit.enums.EnumIntMessages"); 
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

                        //try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
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
                            String picture=jFileContentObjModel.getString("picture");
                            jFileContentObjModel = new JSONObject(jsonDataModel.toString());       
                            String[] fieldNames = new String[]{TblsReqs.Modules.MODULE_NAME.getName(), TblsReqs.Modules.MODULE_VERSION.getName(),
                                TblsReqs.Modules.DESCRIPTION_EN.getName(), TblsReqs.Modules.DESCRIPTION_ES.getName(),
                                TblsReqs.Modules.INFO_JSON.getName(), TblsReqs.Modules.ACTIVE.getName(), TblsReqs.Modules.MODULE_SETTINGS.getName(),
                                TblsReqs.Modules.PICTURE.getName()};
                            Object[] fieldValues = new Object[]{moduleName, moduleVersion, descEn, descEs, jFileContentObjModel, true, moduleSettings, picture};
                            RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.MODULES, fieldNames, fieldValues, TblsReqs.TablesReqs.MODULES.getRepositoryName());
                            Boolean errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.MODULES, insertRecord, moduleName);
                            prcReqsSectionLog.put(TblsReqs.TablesReqs.MODULES.getTableName(), 
                            infoToReportForInsertRecord(TblsReqs.TablesReqs.MODULES, insertRecord, moduleName, errorForInsertRecord));
                            JSONObject curModuleInfo = new JSONObject();
                            curModuleInfo.put("moduleName", moduleName);
                            curModuleInfo.put("file_content", jFileContentObjModel);
                            curModuleInfo.put("detail", infoToReportForInsertRecord(TblsReqs.TablesReqs.MODULES, insertRecord, moduleName, errorForInsertRecord));
                            curModuleInfo.put("create_module_actions_and_queries", createModuleActionsAndQueries(moduleName, moduleVersion, jFileContentObjModel, classesImplementingEndPoints));
                            org.json.JSONArray busRules=jFileContentObjModel.getJSONArray("business_rules");
                            org.json.JSONArray manuals=jFileContentObjModel.getJSONArray("manuals");
                            org.json.JSONArray specialViews=jFileContentObjModel.getJSONArray("special_views");
                            org.json.JSONArray errorNotif=jFileContentObjModel.getJSONArray("error_notifications");
                            curModuleInfo.put("create_business_rules",createBusinessRules(moduleName, moduleVersion, busRules, classesImplementingBusinessRules));
                            curModuleInfo.put("create_special_views",createSpecialViews(moduleName, moduleVersion, specialViews));
                            curModuleInfo.put("create_error_notifications",createErrorNotifications(moduleName, moduleVersion, errorNotif, classesImplementingErrorMessages));
                            curModuleInfo.put("manuals",createManuals(moduleName, moduleVersion, manuals));
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

    public JSONArray createBusinessRules(String moduleName, Integer moduleVersion, org.json.JSONArray jArr, ClassInfoList classInfo){
        JSONObject jMainObj=new JSONObject();        
        JSONArray allBusRulesArr=new JSONArray();
        if (jArr.isEmpty())return allBusRulesArr;
        
        for (int i = 0; i < jArr.length(); i++) {
            // Get the current element as a JSONObject
            JSONObject currentApi = jArr.getJSONObject(i);
            // You can now work with the elements in the currentApi object
            // For example, you can access specific fields within the JSON object:
            String apiName = currentApi.getString("api_name");
            String ruleName = currentApi.getString("name");
            String type = currentApi.getString("type");
            Boolean mandatory = currentApi.getBoolean("mandatory");
            JSONObject curApiObj=new JSONObject();
            curApiObj.put("api_name", apiName);

            List<Object> enumConstantObjects = null; // getMine.getEnumConstantObjects();
            ClassInfo selectedEnum=null;
            for (int j = 0; j < classInfo.size(); j++) {
                ClassInfo enumObject = classInfo.get(j);
                String curApi=enumObject.getName().toUpperCase();
                // Check if the enumObject's class name matches the enumNameToFind
                if (curApi.contains(apiName.toUpperCase())) {
                    // Add the enumObject to your JSONArray or perform any other desired action
                    //apiEndpointsArr.put(enumObject);
//                    getMine=enumObject;
                    selectedEnum= enumObject;
                    enumConstantObjects = enumObject.getEnumConstantObjects();
                    continue;
                }
            }            
            if (enumConstantObjects==null){
                Boolean errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.MODULE_BUSINESS_RULES, null, apiName);
                //curApiObj.put("error", curApi+" not found.");
            }else{
                for (int j = 0; j < enumConstantObjects.size(); j++) {
                    EnumIntBusinessRules curBusRuleObj = (EnumIntBusinessRules) enumConstantObjects.get(j);
                    if ("one_business_rule".equalsIgnoreCase(type)){
                        if (ruleName.equalsIgnoreCase(curBusRuleObj.getTagName())){
                            String[] fieldNames = new String[]{TblsReqs.ModuleBusinessRules.MODULE_NAME.getName(), TblsReqs.ModuleBusinessRules.MODULE_VERSION.getName(),
                                TblsReqs.ModuleBusinessRules.API_NAME.getName(), TblsReqs.ModuleBusinessRules.AREA.getName(),
                                TblsReqs.ModuleBusinessRules.RULE_NAME.getName(), TblsReqs.ModuleBusinessRules.IS_MANDATORY.getName(), TblsReqs.ModuleBusinessRules.PREREQUISITE.getName(),
                                TblsReqs.ModuleBusinessRules.VALUES_LIST.getName()};
                            Object[] fieldValues = new Object[]{moduleName, moduleVersion, apiName, curBusRuleObj.getAreaName(), curBusRuleObj.getTagName(), 
                                Boolean.FALSE.equals(curBusRuleObj.getIsOptional()), curBusRuleObj.getPreReqs(), curBusRuleObj.getValuesList()};
                            RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.MODULE_BUSINESS_RULES, fieldNames, fieldValues, TblsReqs.TablesReqs.MODULE_BUSINESS_RULES.getRepositoryName());                                    
                            Boolean errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.MODULE_BUSINESS_RULES, insertRecord, apiName+"."+curBusRuleObj.getAreaName()+"."+curBusRuleObj.getTagName());
                            jMainObj.put(TblsReqs.TablesReqs.MODULE_BUSINESS_RULES.getTableName()+"."+apiName+"."+curBusRuleObj.getAreaName()+"."+curBusRuleObj.getTagName(), 
                                    infoToReportForInsertRecord(TblsReqs.TablesReqs.MODULE_BUSINESS_RULES, insertRecord, apiName+"."+curBusRuleObj.getAreaName()+"."+curBusRuleObj.getTagName(), errorForInsertRecord));
//                            return allBusRulesArr;
                        }
                    }else{
                        String[] fieldNames = new String[]{TblsReqs.ModuleBusinessRules.MODULE_NAME.getName(), TblsReqs.ModuleBusinessRules.MODULE_VERSION.getName(),
                            TblsReqs.ModuleBusinessRules.API_NAME.getName(), TblsReqs.ModuleBusinessRules.AREA.getName(),
                            TblsReqs.ModuleBusinessRules.RULE_NAME.getName(), TblsReqs.ModuleBusinessRules.IS_MANDATORY.getName(), TblsReqs.ModuleBusinessRules.PREREQUISITE.getName(),
                            TblsReqs.ModuleBusinessRules.VALUES_LIST.getName()};
                        Object[] fieldValues = new Object[]{moduleName, moduleVersion, apiName, curBusRuleObj.getAreaName(), curBusRuleObj.getTagName(), 
                            Boolean.FALSE.equals(curBusRuleObj.getIsOptional()), curBusRuleObj.getPreReqs(), curBusRuleObj.getValuesList()};
                        RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.MODULE_BUSINESS_RULES, fieldNames, fieldValues, TblsReqs.TablesReqs.MODULE_BUSINESS_RULES.getRepositoryName());                                
                        Boolean errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.MODULE_BUSINESS_RULES, insertRecord, apiName+"."+curBusRuleObj.getAreaName()+"."+curBusRuleObj.getTagName());
                        jMainObj.put(TblsReqs.TablesReqs.MODULE_BUSINESS_RULES.getTableName()+"."+apiName+"."+curBusRuleObj.getAreaName()+"."+curBusRuleObj.getTagName(), 
                                infoToReportForInsertRecord(TblsReqs.TablesReqs.MODULE_BUSINESS_RULES, insertRecord, apiName+"."+curBusRuleObj.getAreaName()+"."+curBusRuleObj.getTagName(), errorForInsertRecord));
                    }
                }
                curApiObj.put("inserts_log", jMainObj);
            }
            allBusRulesArr.add(curApiObj);
        }
        return allBusRulesArr;
    }
    public JSONArray createSpecialViews(String moduleName, Integer moduleVersion, org.json.JSONArray jArr){
        JSONArray allSpecialViewsArr=new JSONArray();
        if (jArr.isEmpty())return allSpecialViewsArr;
        
        return allSpecialViewsArr;
    }
    public JSONArray createErrorNotifications(String moduleName, Integer moduleVersion, org.json.JSONArray jArr, ClassInfoList classInfo){
        JSONObject jMainObj=new JSONObject();        
        JSONArray allErrorNotifArr=new JSONArray();
        if (jArr.isEmpty())return allErrorNotifArr;
        for (int i = 0; i < jArr.length(); i++) {
            // Get the current element as a JSONObject
            JSONObject currentApi = jArr.getJSONObject(i);
            JSONObject curApiObj=new JSONObject();
            curApiObj.put("api_name", currentApi);
            // You can now work with the elements in the currentApi object
            // For example, you can access specific fields within the JSON object:
            String apiName = currentApi.getString("api_name");

            List<Object> enumConstantObjects = null; // getMine.getEnumConstantObjects();
            ClassInfo selectedEnum=null;
            for (int j = 0; j < classInfo.size(); j++) {
                ClassInfo enumObject = classInfo.get(j);
                String curEndpointName=enumObject.getName().toUpperCase();
                // Check if the enumObject's class name matches the enumNameToFind
                if (curEndpointName.contains(apiName.toUpperCase())) {
                    // Add the enumObject to your JSONArray or perform any other desired action
                    //apiEndpointsArr.put(enumObject);
//                    getMine=enumObject;
                    selectedEnum= enumObject;
                    enumConstantObjects = enumObject.getEnumConstantObjects();
                    continue;
                }
            }            
            if (enumConstantObjects==null){
                Boolean errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.MODULE_ERROR_NOTIFICATIONS, null, apiName);
                //curApiObj.put("error", curApi+" not found.");
            }else{
                for (int j = 0; j < enumConstantObjects.size(); j++) {
                    EnumIntMessages curErrorNotifObj = (EnumIntMessages) enumConstantObjects.get(j);
                        String[] fieldNames = new String[]{TblsReqs.ModuleBusinessRules.MODULE_NAME.getName(), TblsReqs.ModuleErrorNotifications.MODULE_VERSION.getName(),
                            TblsReqs.ModuleErrorNotifications.API_NAME.getName(), TblsReqs.ModuleErrorNotifications.ERROR_CODE.getName()};
                        Object[] fieldValues = new Object[]{moduleName, moduleVersion, apiName, curErrorNotifObj.getErrorCode()};
                        RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.MODULE_ERROR_NOTIFICATIONS, fieldNames, fieldValues, TblsReqs.TablesReqs.MODULE_ERROR_NOTIFICATIONS.getRepositoryName());                                
                        Boolean errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.MODULE_ERROR_NOTIFICATIONS, insertRecord, apiName+"."+curErrorNotifObj.getErrorCode());
                        jMainObj.put(TblsReqs.TablesReqs.MODULE_ERROR_NOTIFICATIONS.getTableName()+"."+apiName+"."+curErrorNotifObj.getErrorCode().toString(), 
                                infoToReportForInsertRecord(TblsReqs.TablesReqs.MODULE_ERROR_NOTIFICATIONS, insertRecord, apiName+"."+curErrorNotifObj.getErrorCode().toString(), errorForInsertRecord));
                }
                curApiObj.put("inserts_log", jMainObj);
            }  
            allErrorNotifArr.add(curApiObj);
        } 
        return allErrorNotifArr;
    }
    
    public JSONArray createModuleActionsAndQueries(String moduleName, Integer moduleVersion, JSONObject jFileContentObjModel, ClassInfoList classesImplementingEndPoints){
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
    public JSONArray createManuals(String moduleName, Integer moduleVersion, org.json.JSONArray jArr){
        JSONArray allManualsArr=new JSONArray();
        if (jArr.isEmpty())return allManualsArr;        
        EnumIntTableFields[] tableFlds = TblsReqs.TablesReqs.MODULE_MANUALS.getTableFields();
        for (int i = 0; i < jArr.length(); i++) {
            String[] fldsInTable=new String[]{};
            Object[] fieldValues=new Object[]{};
            JSONObject curRowObj=new JSONObject();

            JSONObject currenRowOfData = jArr.getJSONObject(i);
            String manualName = currenRowOfData.getString("manual_name");

            curRowObj.put("manual_name", manualName);

            Iterator<String> keys = currenRowOfData.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                if (EnumIntTableFields.getFldPosicInArray(tableFlds, key) > -1){
                //if (LPArray.valueInArray(tableFlds, key)) {
                    Object value = currenRowOfData.get(key);
                    String valueType = value.getClass().getSimpleName();                    
                    fldsInTable = LPArray.addValueToArray1D(fldsInTable, key);
                    switch (valueType){
                        case "String":
                            fieldValues = LPArray.addValueToArray1D(fieldValues, value.toString());
                            break;
                        case "Integer":
                            fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(value.toString()));
                            break;
                        case "Boolean":
                            fieldValues = LPArray.addValueToArray1D(fieldValues, Boolean.valueOf(value.toString()));
                            break;
                        default:
                            break;
                    }                    
                }
            }    
            fldsInTable = LPArray.addValueToArray1D(fldsInTable, new String[]{TblsReqs.ModuleManuals.MODULE_NAME.getName(), TblsReqs.ModuleManuals.MODULE_VERSION.getName()});
            fieldValues = LPArray.addValueToArray1D(fieldValues, new Object[]{moduleName, moduleVersion});
            RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.MODULE_MANUALS, fldsInTable, fieldValues, TblsReqs.TablesReqs.MODULE_BUSINESS_RULES.getRepositoryName());                                
            Boolean errorForInsertRecord = isErrorForInsertRecord(TblsReqs.TablesReqs.MODULE_MANUALS, insertRecord, manualName);
            curRowObj.put("inserts_log", infoToReportForInsertRecord(TblsReqs.TablesReqs.MODULE_MANUALS, insertRecord, manualName, errorForInsertRecord));
            allManualsArr.add(curRowObj);
        }
        return allManualsArr;    
    }
}                
           