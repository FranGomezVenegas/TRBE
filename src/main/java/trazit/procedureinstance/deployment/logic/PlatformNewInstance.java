/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.deployment.logic;

import static databases.DbObjects.createSchemas;
import static databases.DbObjects.removeSchemas;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlWhere;
import databases.TblsApp;
import databases.TblsAppConfig;
import databases.TblsProcedure;
import databases.features.DbEncryption;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lbplanet.utilities.LPDate;
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
    private PlatformNewInstance() {throw new IllegalStateException("Utility class");}
    public static String decrypt(String encryptedValue, SecretKey secretKey)  {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
            byte[] decryptedBytes = cipher.doFinal(decodedValue);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (InvalidKeyException|IllegalBlockSizeException|BadPaddingException|NoSuchAlgorithmException|NoSuchPaddingException ex) {
            Logger.getLogger(PlatformNewInstance.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static JSONObject createCheckPlatformProcedure(String platfName){
        ResourceBundle propConfig = ResourceBundle.getBundle(BUNDLE_TAG_PARAMETER_CONFIG_CONF);  
        String fileDir = propConfig.getString(Parameter.PropertyFilesType.UNDECODE_FOR_TESTING.getAppConfigParamName());
        byte[] decodedKey = Base64.getDecoder().decode(fileDir);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        String fakeProcUserName2 = decrypt("bMB9/9Rg5v7WOKdG/SkqCw==", secretKey);
        if (fakeProcUserName2==null){
            JSONObject jObj=new JSONObject();
            jObj.put("error", jObj);
        }
        String fakeProcName = "check-platform";
        String fakeProcUserName = "demo";
        String personId="d1m2";
        String fakeEsingn="firmademo";
        String defaultMail="info@trazit.net";
        Object[] encryptValue=DbEncryption.encryptValue(fakeEsingn);        
        String fakeEsingnEncrypted = encryptValue[encryptValue.length-1].toString();
        String[] schemaNames = new String[]{LPPlatform.buildSchemaName(fakeProcName, GlobalVariables.Schemas.PROCEDURE.getName())};
        String tblCreateScript="";
        JSONObject jsonObj=new JSONObject();
        JSONArray createSchemas = createSchemas(schemaNames, LPPlatform.buildSchemaName(fakeProcName, fakeProcName));     
        jsonObj.put("create_schemas", createSchemas);
        tblCreateScript=createTableScript(TblsProcedure.TablesProcedure.PERSON_PROFILE, fakeProcName, false, true, null);        
        Rdbms.prepUpQueryWithDiagn(TblsProcedure.TablesProcedure.PERSON_PROFILE.getRepositoryName(), TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(), tblCreateScript, new Object[]{});
        tblCreateScript=createTableScript(TblsProcedure.TablesProcedure.PROCEDURE_INFO, fakeProcName, false, true, null
        );        
        Rdbms.prepUpQueryWithDiagn(TblsProcedure.TablesProcedure.PROCEDURE_INFO.getRepositoryName(), TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(), tblCreateScript, new Object[]{});
        String actionLog="";
        
        RdbmsObject insertRecordInTable=Rdbms.insertRecord(TblsProcedure.TablesProcedure.PERSON_PROFILE, 
            new String[]{TblsProcedure.PersonProfile.PERSON_NAME.getName(), TblsProcedure.PersonProfile.ROLE_NAME.getName(), 
                TblsProcedure.PersonProfile.ACTIVE.getName(), TblsProcedure.PersonProfile.USER_TITLE.getName()}, 
            new Object[]{personId, "testing", true, "Testing user access / Testeo acceso usuario"}, fakeProcName);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess()))
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("insert_person_profile_record", actionLog);
        LocalDateTime currentTimeStamp = LPDate.getCurrentTimeStamp();
        int hashCode=currentTimeStamp.hashCode();
        insertRecordInTable=Rdbms.insertRecord(TblsProcedure.TablesProcedure.PROCEDURE_INFO, 
            new String[]{TblsProcedure.ProcedureInfo.NAME.getName(), TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName(), TblsProcedure.ProcedureInfo.VERSION.getName(), TblsProcedure.ProcedureInfo.LABEL_EN.getName(), 
                TblsProcedure.ProcedureInfo.LABEL_ES.getName(), TblsProcedure.ProcedureInfo.PROCEDURE_HASH_CODE.getName()}, 
            new Object[]{fakeProcName, fakeProcName, 1, fakeProcName, fakeProcName, String.valueOf(hashCode)}, fakeProcName);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess()))
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("insert_procedure_info_record", actionLog);

        insertRecordInTable = Rdbms.insertRecord(TblsApp.TablesApp.USERS, 
                new String[]{TblsApp.Users.USER_NAME.getName(), TblsApp.Users.EMAIL.getName(), TblsApp.Users.ESIGN.getName(),
                    TblsApp.Users.PASSWORD.getName(), TblsApp.Users.PERSON_NAME.getName()},
                new Object[]{fakeProcUserName, defaultMail, fakeEsingnEncrypted, fakeProcUserName+fakeProcUserName, personId}, null);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess()))
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("insert_user_record", actionLog);

        insertRecordInTable=Rdbms.insertRecord(TblsApp.TablesApp.USER_PROCESS, 
            new String[]{TblsApp.UserProcess.USER_NAME.getName(), TblsApp.UserProcess.PROC_NAME.getName(), TblsApp.UserProcess.ACTIVE.getName()}, 
            new Object[]{fakeProcUserName, fakeProcName, true}, null);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess()))
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("insert_user_process_record", actionLog);

        insertRecordInTable=Rdbms.insertRecord(TblsAppConfig.TablesAppConfig.PERSON, 
            new String[]{TblsAppConfig.Person.PERSON_ID.getName(), TblsAppConfig.Person.FIRST_NAME.getName(), 
                TblsAppConfig.Person.LAST_NAME.getName(), TblsAppConfig.Person.PHOTO.getName()}, 
            new Object[]{personId, "I'm a user demo", "for demos "+platfName, "https://hasta-pronto.ru/wp-content/uploads/2014/09/chibcha.jpg"}, null);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess()))
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
        JSONObject jsonObj=new JSONObject();
        String actionLog="";
        RdbmsObject removeRecord = Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PERSON_PROFILE, 
            new SqlWhere(TblsProcedure.TablesProcedure.PERSON_PROFILE, new String[]{TblsProcedure.PersonProfile.PERSON_NAME.getName(), TblsProcedure.PersonProfile.ROLE_NAME.getName()},
            new Object[]{personId, "testing"}), fakeProcName);        
        if (Boolean.TRUE.equals(removeRecord.getRunSuccess()))
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", removeRecord.getErrorMessageCode(), removeRecord.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("remove_person_profile_record", actionLog);        
        
        removeRecord = Rdbms.removeRecordInTable(TblsApp.TablesApp.USERS, 
            new SqlWhere(TblsApp.TablesApp.USERS, new String[]{TblsApp.Users.USER_NAME.getName()}, new Object[]{fakeProcUserName}), null);
        if (Boolean.TRUE.equals(removeRecord.getRunSuccess()))
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", removeRecord.getErrorMessageCode(), removeRecord.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("remove_user_record", actionLog);        
        
        removeRecord=Rdbms.removeRecordInTable(TblsApp.TablesApp.USER_PROCESS, 
            new SqlWhere(TblsApp.TablesApp.USER_PROCESS, new String[]{TblsApp.UserProcess.USER_NAME.getName(), TblsApp.UserProcess.PROC_NAME.getName()}, 
            new Object[]{fakeProcUserName, fakeProcName}), null);
        if (Boolean.TRUE.equals(removeRecord.getRunSuccess()))
            actionLog="success";
        else{
            Object[] trapMessage = ApiMessageReturn.trapMessage("", removeRecord.getErrorMessageCode(), removeRecord.getErrorMessageVariables());            
            actionLog=trapMessage[trapMessage.length-1].toString();
        }
        jsonObj.put("remove_user_process_record", actionLog);        

        removeRecord=Rdbms.removeRecordInTable(TblsAppConfig.TablesAppConfig.PERSON, 
            new SqlWhere(TblsAppConfig.TablesAppConfig.PERSON, new String[]{TblsAppConfig.Person.PERSON_ID.getName()}, 
            new Object[]{personId}), null);
        if (Boolean.TRUE.equals(removeRecord.getRunSuccess()))
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
