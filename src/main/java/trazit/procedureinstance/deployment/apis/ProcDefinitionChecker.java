package trazit.procedureinstance.deployment.apis;

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
import functionaljavaa.parameter.Parameter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import lbplanet.utilities.LPDate;
import org.json.JSONObject;
import org.json.JSONArray;
import trazit.enums.EnumIntTables;
import static trazit.enums.deployrepository.DeployTables.createTableScript;
import static trazit.globalvariables.GlobalVariables.PROC_MANAGEMENT_SPECIAL_ROLE;
import trazit.procedureinstance.definition.definition.TblsReqs;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import trazit.enums.EnumIntViews;
/**
 *
 * @author User
 */
public class ProcDefinitionChecker {
    private String diagnSummary="";
    private JSONObject executionLog = new JSONObject();
    private JSONArray errorsList = new JSONArray();
    
    public ProcDefinitionChecker(String procName, Integer procVersion, String procInstanceName){
        diagnSummary="";
        executionLog = new JSONObject();
        errorsList = new JSONArray();        
        checkBusinessRules(procName, procVersion, procInstanceName);
    }

    public JSONObject publishReport(){
        JSONObject summary = new JSONObject();
        String conclusionStr="";
        if (this.errorsList.isEmpty()){
            conclusionStr="All correct, no problems";
        }else{
            conclusionStr="Found "+this.errorsList.length()+" errors";
            summary.put("errors_found", errorsList);
        }        
        this.executionLog.put("conclusion", conclusionStr);
        if (this.errorsList.length()>0)
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
            this.errorsList.put(infoToReport(curTbl, tblCreateScript));
            return true;
        }
        if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains(GlobalAPIsParams.LBL_ERROR)){
            this.errorsList.put(infoToReport(curTbl, tblCreateScript));
            return true;
        }
        return false;        
    }
    private Boolean isError(EnumIntViews curView,  Object[] prepUpQuery, String tblCreateScript){
        if (tblCreateScript.toUpperCase().contains("ALREADY")&&tblCreateScript.toUpperCase().contains("EXIST")){
            return false;
        }            
        if (Boolean.FALSE.equals(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE)) && Boolean.FALSE.equals(tblCreateScript.toLowerCase().contains("already"))){
            this.errorsList.put(infoToReport(curView, tblCreateScript));
            return true;
        }
        if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains(GlobalAPIsParams.LBL_ERROR)){
            this.errorsList.put(infoToReport(curView, tblCreateScript));
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
            this.errorsList.put(log);
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

    
    private void checkBusinessRules(String procName, Integer procVersion, String procInstanceName){
        JSONObject mainBlockReport=new JSONObject();
        if (1==1){
            mainBlockReport.put("procInstanceName", procInstanceName);
            addToLogSummary("main_platform_structure", mainBlockReport);
            return;
        }
        String viewCreateScript="";
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
                curSchemaArr.put(curTblObj);
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
                allViewsArr.put(curViewObj);
//            }
        }        
        mainBlockReport.put("views", allViewsArr);

        
        addToLogSummary("main_platform_structure", mainBlockReport);
    }

    private void xcreateAppProcTables(String platformName){        
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
            jArr.put(infoToReportForInsertRecord(TblsApp.TablesApp.APP_BUSINESS_RULES, insertRecord, curRule[1].toString(), errorForInsertRecord));                        
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
            jArr.put(infoToReportForInsertRecord(TblsApp.TablesApp.APP_PROCEDURE_VIEWS, insertRecord, curRule[0].toString(), errorForInsertRecord));                        
        }        
        prcDeplSectionLog.put(TblsApp.TablesApp.APP_PROCEDURE_VIEWS.getTableName(), jArr);
        
        String fakeEsingn="firmademo";
        String defaultMail="info@trazit.net";
        Object[] encryptValue=DbEncryption.encryptValue(fakeEsingn);        
        String fakeEsingnEncrypted = encryptValue[encryptValue.length-1].toString();
        Object[] encryptPa=DbEncryption.encryptValue("trazit4ever");        
        String paEncrypted = encryptPa[encryptPa.length-1].toString();
       // Object[] encryptPers=DbEncryption.encryptValue("adminz");        
        String persEncrypted = String.valueOf("adminz".hashCode()); //encryptPers[encryptPers.length-1].toString();
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

}                
           