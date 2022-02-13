/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement;

import static databases.DbObjects.createSchemas;
import static databases.DbObjects.removeSchemas;
import databases.Rdbms;
import databases.TblsApp;
import databases.TblsAppConfig;
import databases.TblsProcedure;
import javax.sql.rowset.CachedRowSet;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;

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
        jsonObj=createSchemas(schemaNames, LPPlatform.buildSchemaName(fakeProcName, platfName));  
        
        tblCreateScript=TblsProcedure.PersonProfile.createTableScript(fakeProcName, new String[]{""});
        CachedRowSet prepRdQuery = Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsProcedure.PersonProfile", tblCreateScript);
        Object[] insertRecordInTable=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(fakeProcName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.PersonProfile.TBL.getName(), 
            new String[]{TblsProcedure.PersonProfile.FLD_PERSON_NAME.getName(), TblsProcedure.PersonProfile.FLD_ROLE_NAME.getName(), 
                TblsProcedure.PersonProfile.FLD_ACTIVE.getName(), TblsProcedure.PersonProfile.FLD_USER_TITLE.getName()}, 
            new Object[]{personId, "testing", true, "Testing user access / Testeo acceso usuario"});
        
        insertRecordInTable = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), 
                new String[]{TblsApp.Users.USER_NAME.getName(), TblsApp.Users.EMAIL.getName(), TblsApp.Users.ESIGN.getName(),
                    TblsApp.Users.PASSWORD.getName(), TblsApp.Users.PERSON_NAME.getName()},
                new Object[]{fakeProcUserName, "trazit.info@gmail.com", "firmademo", fakeProcUserName+fakeProcUserName, personId});
        insertRecordInTable=Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USER_PROCESS.getTableName(), 
            new String[]{TblsApp.UserProcess.USER_NAME.getName(), TblsApp.UserProcess.PROC_NAME.getName(), TblsApp.UserProcess.ACTIVE.getName()}, 
            new Object[]{fakeProcUserName, fakeProcName, true});
        insertRecordInTable=Rdbms.insertRecordInTable(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.Person.TBL.getName(), 
            new String[]{TblsAppConfig.Person.FLD_PERSON_ID.getName(), TblsAppConfig.Person.FLD_FIRST_NAME.getName(), 
                TblsAppConfig.Person.FLD_LAST_NAME.getName(), TblsAppConfig.Person.FLD_PHOTO.getName()}, 
            new Object[]{personId, "I'm a user demo", "for demos "+platfName, "https://hasta-pronto.ru/wp-content/uploads/2014/09/chibcha.jpg"});
        
        return new JSONObject();
    }

    public static JSONObject removeCheckPlatformProcedure(String platfName){
        String fakeProcName = "check-platform";
        String fakeProcUserName = "demo";
        String personId="d1m2";
        String[] schemaNames = new String[]{LPPlatform.buildSchemaName(fakeProcName, GlobalVariables.Schemas.PROCEDURE.getName())};
        String tblCreateScript="";
        JSONObject jsonObj=new JSONObject();
        jsonObj=removeSchemas(schemaNames, LPPlatform.buildSchemaName(fakeProcName, platfName));  
        
        Object[] removeRecordInTable=Rdbms.removeRecordInTable(LPPlatform.buildSchemaName(fakeProcName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.PersonProfile.TBL.getName(), 
            new String[]{TblsProcedure.PersonProfile.FLD_PERSON_NAME.getName(), TblsProcedure.PersonProfile.FLD_ROLE_NAME.getName()}, 
            new Object[]{personId, "testing"});        
        removeRecordInTable = Rdbms.removeRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), 
                new String[]{TblsApp.Users.USER_NAME.getName()},
                new Object[]{fakeProcUserName});
        removeRecordInTable=Rdbms.removeRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USER_PROCESS.getTableName(), 
            new String[]{TblsApp.UserProcess.USER_NAME.getName(), TblsApp.UserProcess.PROC_NAME.getName()}, 
            new Object[]{fakeProcUserName, fakeProcName});
        removeRecordInTable=Rdbms.removeRecordInTable(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.Person.TBL.getName(), 
            new String[]{TblsAppConfig.Person.FLD_PERSON_ID.getName()}, 
            new Object[]{personId});
        return new JSONObject();
    }
}
