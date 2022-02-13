/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.user;

import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.TblsApp;
import databases.TblsApp.Users;
import databases.TblsAppConfig;
import databases.Token;
import functionaljavaa.parameter.Parameter;
import java.util.ResourceBundle;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class UserAndRolesViews {
    
    public static final String BUNDLEPARAM_CREDNTUSR_IS_CASESENSIT="credentials_userIsCaseSensitive";
    
    private UserAndRolesViews(){    throw new IllegalStateException("Utility class");}             
         
    /**
     *
     * @param person
     * @return
     */
    public static final String getUserByPerson(String person){
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String userIsCaseSensitive = prop.getString(BUNDLEPARAM_CREDNTUSR_IS_CASESENSIT);
        if (!Boolean.valueOf(userIsCaseSensitive)) person=person.toLowerCase();        
        Object[][] userByPerson = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), 
                new String[]{Users.FLD_PERSON_NAME.getName()}, new String[]{person}, new String[]{TblsApp.Users.FLD_USER_NAME.getName()}, new String[]{TblsApp.Users.FLD_USER_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userByPerson[0][0].toString())){return LPPlatform.LAB_FALSE;}
        return userByPerson[0][0].toString();
    }

    /**
     *  Returns Object[] LABPLANET_FALSE when the person not found or user name in position 0.
     * @param userName
     * @return
     */
    public static final Object[] getPersonByUser(String userName){ 
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String userIsCaseSensitive = prop.getString(BUNDLEPARAM_CREDNTUSR_IS_CASESENSIT);
        if (!Boolean.valueOf(userIsCaseSensitive)) userName=userName.toLowerCase();
        Object[][] personByUser = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), 
                new String[]{TblsApp.Users.FLD_USER_NAME.getName()}, new String[]{userName}, new String[]{Users.FLD_PERSON_NAME.getName()}, new String[]{Users.FLD_PERSON_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUser[0][0].toString()))
            LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "userNotExists", new Object[]{userName});
        return new Object[]{personByUser[0][0].toString()};
    }
    

    /**
     * This method makes no sense once the Rdbms instance is created once by singleton pattern <br>
     * This method would be replaced by checking user and password against the info in the  token
     * @param user
     * @param pass
     * @return
     */
    public static final Object[] isValidUserPassword(String user, String pass) {
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String userIsCaseSensitive = prop.getString(BUNDLEPARAM_CREDNTUSR_IS_CASESENSIT);
        if (!Boolean.valueOf(userIsCaseSensitive)) user=user.toLowerCase();
        return Rdbms.existsRecord(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), 
                new String[]{Users.FLD_USER_NAME.getName(), Users.FLD_PASSWORD.getName()}, new Object[]{user, pass});
    }

    public static final Object[] setUserNewPassword(String user, String newPass) {
        return setUserProperty(user, Users.FLD_PASSWORD.getName(), newPass);
    }
    public static final Object[] setUserNewEsign(String user, String newEsign) {
        return setUserProperty(user, Users.FLD_ESIGN.getName(), newEsign);        
    }
    public static final Object[] setUserProperty(String user, String fieldName, String newValue) {
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String userIsCaseSensitive = prop.getString(BUNDLEPARAM_CREDNTUSR_IS_CASESENSIT);
        if (!Boolean.valueOf(userIsCaseSensitive)) user=user.toLowerCase();
        return Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), 
                new String[]{fieldName}, new Object[]{newValue}, 
                new String[]{Users.FLD_USER_NAME.getName()}, new Object[]{user});
    }
    
    public static final Object[] setUserDefaultTabsOnLogin(Token token, String tabs){
        return Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), 
                new String[]{TblsApp.Users.FLD_TABS_ON_LOGIN.getName()}, new Object[]{tabs}, 
                new String[]{TblsApp.Users.FLD_USER_NAME.getName()}, new Object[]{token.getUserName()});
    }
    
    public static final Object[] createAppUser(String uName, String[] fldNames, Object[] fldValues){
        Object[] personByUserObj = getPersonByUser(uName);        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUserObj[0].toString())) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "UserAlreadyExists", new Object[]{uName});        
        Object[] personIdDiagn = getNextAppPersonId();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personIdDiagn[0].toString())) return personIdDiagn;
        String personId=personIdDiagn[1].toString();
        Object[] personCreatedDiagn; 
        personCreatedDiagn = Rdbms.insertRecordInTable(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.Person.TBL.getName(), 
                new String[]{TblsAppConfig.Person.FLD_PERSON_ID.getName(), TblsAppConfig.Person.FLD_FIRST_NAME.getName()}, new Object[]{personId, uName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personCreatedDiagn[0].toString())) return personCreatedDiagn;
        Object[] userCreatedDiagn = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), 
                new String[]{Users.FLD_USER_NAME.getName(), Users.FLD_PASSWORD.getName(), Users.FLD_PERSON_NAME.getName()}, new Object[]{uName, "trazit123", personId});        
        return userCreatedDiagn;
        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "not implemented yet", null);        
    }
    private static Object[] getNextAppPersonId(){
        return new Object[]{LPPlatform.LAB_TRUE, "14"};
        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "not implemented yet", null);
    }
    
}
