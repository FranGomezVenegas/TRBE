/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement;

import static databases.DbObjects.createSchemas;
import static databases.DbObjects.removeSchemas;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.TblsApp;
import databases.TblsAppConfig;
import databases.TblsProcedure;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import static trazit.enums.deployrepository.DeployTables.createTableScript;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;

/**
 *
 * @author User
 */
public class PlatformNewInstance {
    public static JSONObject createCheckPlatformProcedure(String platfName){
        String fakeProcName = "check-platform";
        String fakeProcUserName = "demo";
        String personId="d1m2";
        String[] schemaNames = new String[]{LPPlatform.buildSchemaName(fakeProcName, GlobalVariables.Schemas.PROCEDURE.getName())};
        String tblCreateScript="";
        JSONObject jsonObj=new JSONObject();
        JSONArray createSchemas = createSchemas(schemaNames, LPPlatform.buildSchemaName(fakeProcName, fakeProcName));     
        jsonObj.put("create_schemas", createSchemas);
        tblCreateScript=createTableScript(TblsProcedure.TablesProcedure.PERSON_PROFILE, fakeProcName);        
        Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(TblsProcedure.TablesProcedure.PERSON_PROFILE.getRepositoryName(), TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(), tblCreateScript, new Object[]{});
        String actionLog="";
        
        RdbmsObject insertRecordInTable=Rdbms.insertRecord(LPPlatform.buildSchemaName(fakeProcName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(), 
            new String[]{TblsProcedure.PersonProfile.PERSON_NAME.getName(), TblsProcedure.PersonProfile.ROLE_NAME.getName(), 
                TblsProcedure.PersonProfile.ACTIVE.getName(), TblsProcedure.PersonProfile.USER_TITLE.getName()}, 
            new Object[]{personId, "testing", true, "Testing user access / Testeo acceso usuario"});
        if (insertRecordInTable.getRunSuccess())
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("insert_person_profile_record", actionLog);

        insertRecordInTable = Rdbms.insertRecord(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), 
                new String[]{TblsApp.Users.USER_NAME.getName(), TblsApp.Users.EMAIL.getName(), TblsApp.Users.ESIGN.getName(),
                    TblsApp.Users.PASSWORD.getName(), TblsApp.Users.PERSON_NAME.getName()},
                new Object[]{fakeProcUserName, "trazit.info@gmail.com", "firmademo", fakeProcUserName+fakeProcUserName, personId});
        if (insertRecordInTable.getRunSuccess())
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("insert_user_record", actionLog);

        insertRecordInTable=Rdbms.insertRecord(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USER_PROCESS.getTableName(), 
            new String[]{TblsApp.UserProcess.USER_NAME.getName(), TblsApp.UserProcess.PROC_NAME.getName(), TblsApp.UserProcess.ACTIVE.getName()}, 
            new Object[]{fakeProcUserName, fakeProcName, true});
        if (insertRecordInTable.getRunSuccess())
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("insert_user_process_record", actionLog);

        insertRecordInTable=Rdbms.insertRecord(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), 
            new String[]{TblsAppConfig.Person.PERSON_ID.getName(), TblsAppConfig.Person.FIRST_NAME.getName(), 
                TblsAppConfig.Person.LAST_NAME.getName(), TblsAppConfig.Person.PHOTO.getName()}, 
            new Object[]{personId, "I'm a user demo", "for demos "+platfName, "https://hasta-pronto.ru/wp-content/uploads/2014/09/chibcha.jpg"});
        if (insertRecordInTable.getRunSuccess())
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("insert_person_record", actionLog);
        
        return jsonObj;
    }

    public static JSONObject removeCheckPlatformProcedure(String platfName){
        String fakeProcName = "check-platform";
        String fakeProcUserName = "demo";
        String personId="d1m2";
        String[] schemaNames = new String[]{LPPlatform.buildSchemaName(fakeProcName, GlobalVariables.Schemas.PROCEDURE.getName())};
        String tblCreateScript="";
        JSONObject jsonObj=new JSONObject();
        String actionLog="";
        
        RdbmsObject removeRecord = Rdbms.removeRecord(LPPlatform.buildSchemaName(fakeProcName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(), 
                new String[]{TblsProcedure.PersonProfile.PERSON_NAME.getName(), TblsProcedure.PersonProfile.ROLE_NAME.getName()},
                new Object[]{personId, "testing"});        
        if (removeRecord.getRunSuccess())
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", removeRecord.getErrorMessageCode(), removeRecord.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("remove_person_profile_record", actionLog);        
        
        removeRecord = Rdbms.removeRecord(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), 
                new String[]{TblsApp.Users.USER_NAME.getName()},
                new Object[]{fakeProcUserName});
        if (removeRecord.getRunSuccess())
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", removeRecord.getErrorMessageCode(), removeRecord.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("remove_user_record", actionLog);        
        
        removeRecord=Rdbms.removeRecord(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USER_PROCESS.getTableName(), 
            new String[]{TblsApp.UserProcess.USER_NAME.getName(), TblsApp.UserProcess.PROC_NAME.getName()}, 
            new Object[]{fakeProcUserName, fakeProcName});
        if (removeRecord.getRunSuccess())
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", removeRecord.getErrorMessageCode(), removeRecord.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("remove_user_process_record", actionLog);        

        removeRecord=Rdbms.removeRecord(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), 
            new String[]{TblsAppConfig.Person.PERSON_ID.getName()}, 
            new Object[]{personId});
        if (removeRecord.getRunSuccess())
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", removeRecord.getErrorMessageCode(), removeRecord.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("remove_person_record", actionLog);        

        JSONArray removeSchemas = removeSchemas(schemaNames, LPPlatform.buildSchemaName(fakeProcName, platfName));  
        jsonObj.put("remove_schemas", removeSchemas);

        return jsonObj;
    }
}
