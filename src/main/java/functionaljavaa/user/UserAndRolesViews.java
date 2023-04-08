/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.user;

import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsApp;
import databases.TblsApp.TablesApp;
import databases.TblsApp.Users;
import databases.TblsAppConfig;
import databases.TblsAppConfig.TablesAppConfig;
import databases.features.DbEncryption;
import databases.features.Token;
import functionaljavaa.parameter.Parameter;
import java.util.ResourceBundle;
import lbplanet.utilities.LPNulls;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class UserAndRolesViews {
    private UserAndRolesViews(){    throw new IllegalStateException("Utility class");}             

    public enum UserAndRolesErrorTrapping implements EnumIntMessages{ 
        BUNDLEPARAM_CREDNTUSR_IS_CASESENSIT("credentials_userIsCaseSensitive", "", ""),
        USER_NOT_EXISTS("userNotExists", "", ""),
        ;
        private UserAndRolesErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    /**
     *
     * @param person
     * @return
     */
    public static final String getUserByPerson(String person){
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String userIsCaseSensitive = prop.getString(UserAndRolesErrorTrapping.BUNDLEPARAM_CREDNTUSR_IS_CASESENSIT.getErrorCode());
        if (Boolean.FALSE.equals(Boolean.valueOf(userIsCaseSensitive))) person=person.toLowerCase();        
        Object[][] userByPerson = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), 
                new String[]{Users.PERSON_NAME.getName()}, new String[]{person}, new String[]{TblsApp.Users.USER_NAME.getName()}, new String[]{TblsApp.Users.USER_NAME.getName()});
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
        String userIsCaseSensitive = prop.getString(UserAndRolesErrorTrapping.BUNDLEPARAM_CREDNTUSR_IS_CASESENSIT.getErrorCode());
        if (Boolean.FALSE.equals(Boolean.valueOf(userIsCaseSensitive))) userName=userName.toLowerCase();
        Object[][] personByUser = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), 
                new String[]{TblsApp.Users.USER_NAME.getName()}, new String[]{userName}, new String[]{Users.PERSON_NAME.getName()}, new String[]{Users.PERSON_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUser[0][0].toString()))
            ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, UserAndRolesErrorTrapping.USER_NOT_EXISTS.getErrorCode(), new Object[]{userName});
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
        String userIsCaseSensitive = prop.getString(UserAndRolesErrorTrapping.BUNDLEPARAM_CREDNTUSR_IS_CASESENSIT.getErrorCode());
        if (Boolean.FALSE.equals(Boolean.valueOf(userIsCaseSensitive))) user=user.toLowerCase();
        
        SqlWhere sW=new SqlWhere();
        sW.addConstraint(Users.USER_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{user}, null);        
        EnumIntTableFields[] fieldsToRetrieve = EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.USERS, 
            Users.PASSWORD);
        Object[][] tableData = QueryUtilitiesEnums.getTableData(TblsApp.TablesApp.USERS,
            fieldsToRetrieve, sW, null);         
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(tableData[0][0].toString()))
            return tableData[0];
        Object[] decryptValue = DbEncryption.decryptValue(tableData[0][0].toString());
        String dbPass = decryptValue[decryptValue.length-1].toString();
        if (pass.equalsIgnoreCase(dbPass))
            return new Object[]{LPPlatform.LAB_TRUE};
        else
            return new Object[]{LPPlatform.LAB_FALSE};
    }

    public static final Object[] setUserNewPassword(String user, String newPass) {
        return setUserProperty(user, Users.PASSWORD.getName(), newPass, true);
    }
    public static final Object[] setUserNewEsign(String user, String newEsign) {
        return setUserProperty(user, Users.ESIGN.getName(), newEsign, true);        
    }
    public static final Object[] setUserProperty(String user, String fieldName, String newValue, Boolean isEnctrypted) {
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String userIsCaseSensitive = prop.getString(UserAndRolesErrorTrapping.BUNDLEPARAM_CREDNTUSR_IS_CASESENSIT.getErrorCode());
        if (Boolean.FALSE.equals(Boolean.valueOf(userIsCaseSensitive))) user=user.toLowerCase();
	SqlWhere sqlWhere = new SqlWhere();
        if (Boolean.TRUE.equals(isEnctrypted)){
            Object[] encryptValue=DbEncryption.encryptValue(newValue);        
            newValue=encryptValue[encryptValue.length-1].toString();
        }
	sqlWhere.addConstraint(Users.USER_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{user}, "");
	return Rdbms.updateRecordFieldsByFilter(TblsApp.TablesApp.USERS,
            EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.USERS, new String[]{fieldName}), new Object[]{newValue}, sqlWhere, null);
        
    }
    
    public static final Object[] setUserDefaultTabsOnLogin(Token token, String tabs){
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(Users.USER_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{token.getUserName()}, "");
	return Rdbms.updateRecordFieldsByFilter(TblsApp.TablesApp.USERS,
            EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.USERS, new String[]{TblsApp.Users.TABS_ON_LOGIN.getName()}), new Object[]{tabs}, sqlWhere, null);
    }
    
    public static final Object[] createAppUser(String uName, String[] fldNames, Object[] fldValues){
        String pasEsingn="trazit4ever";
        Object[] encryptValue=DbEncryption.encryptValue(pasEsingn);        
        String pasEncrypted = encryptValue[encryptValue.length-1].toString();
        
        Object[] personByUserObj = getPersonByUser(uName);        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUserObj[0].toString())) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "UserAlreadyExists", new Object[]{uName});        
        Object[] personIdDiagn = getNextAppPersonId();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personIdDiagn[0].toString())) return personIdDiagn;
        String personId=personIdDiagn[1].toString();        
        RdbmsObject personCreatedDiagn = Rdbms.insertRecordInTable(TblsAppConfig.TablesAppConfig.PERSON, 
                new String[]{TblsAppConfig.Person.PERSON_ID.getName(), TblsAppConfig.Person.FIRST_NAME.getName()}, new Object[]{personId, uName});
        if (!personCreatedDiagn.getRunSuccess()) return personCreatedDiagn.getApiMessage();
        RdbmsObject userCreatedDiagn = Rdbms.insertRecordInTable(TblsApp.TablesApp.USERS, 
                new String[]{Users.USER_NAME.getName(), Users.PASSWORD.getName(), Users.PERSON_NAME.getName()}, new Object[]{uName, pasEncrypted, personId});        
        return userCreatedDiagn.getApiMessage();
        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "not implemented yet", null);        
    }
    private static Object[] getNextAppPersonId(){
        return new Object[]{LPPlatform.LAB_TRUE, "14"};
        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "not implemented yet", null);
    }
 /*   
    public static InternalMessage updateUserShift(String newShift, String userName){
        String personId="";
        if (LPNulls.replaceNull(userName).length()==0){
            ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
            personId=instanceForActions.getToken().getPersonName();
        }else{
            Object[] personByUser = getPersonByUser(userName);            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUser[0].toString()))
                return new InternalMessage(LPPlatform.LAB_FALSE, personByUser[personByUser.length-1].toString(), new Object[]{}, null);
            personId=personByUser[0].toString();
        }
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsAppConfig.Person.PERSON_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{personId}, "");
	Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(TablesAppConfig.PERSON,
            EnumIntTableFields.getTableFieldsFromString(TablesAppConfig.PERSON, new String[]{TblsAppConfig.Person.SHIFT.getName()}), new Object[]{newShift}, sqlWhere, null);        
        return new InternalMessage(updateRecordFieldsByFilter[0].toString(), updateRecordFieldsByFilter[updateRecordFieldsByFilter.length-1].toString(), new Object[]{}, null);
    }   
    
    public static InternalMessage updateUserMail(String newMail, String userName){
        String personId="";
        if (LPNulls.replaceNull(userName).length()==0){
            ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
            personId=instanceForActions.getToken().getPersonName();
        }else{
            Object[] personByUser = getPersonByUser(userName);            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUser[0].toString()))
                return new InternalMessage(LPPlatform.LAB_FALSE, personByUser[personByUser.length-1].toString(), new Object[]{}, null);
            personId=personByUser[0].toString();
        }
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsAppConfig.Person.PERSON_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{personId}, "");
	Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(TablesAppConfig.PERSON,
            EnumIntTableFields.getTableFieldsFromString(TablesAppConfig.PERSON, new String[]{TblsAppConfig.Person.SHIFT.getName()}), new Object[]{newMail}, sqlWhere, null);        
        return new InternalMessage(updateRecordFieldsByFilter[0].toString(), updateRecordFieldsByFilter[updateRecordFieldsByFilter.length-1].toString(), new Object[]{}, null);
    }   
*/
    public static InternalMessage updateUsersFields(String userName, EnumIntTableFields[] updateFldsN, Object[] updateFldV){
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsApp.Users.USER_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, "");
	Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(TablesApp.USERS,
            updateFldsN, updateFldV, sqlWhere, null);        
        return new InternalMessage(updateRecordFieldsByFilter[0].toString(), updateRecordFieldsByFilter[updateRecordFieldsByFilter.length-1].toString(), new Object[]{}, null);
    }   

    public static InternalMessage updateUserPersonFields(String userName, EnumIntTableFields[] updateFldsN, Object[] updateFldV){
        String personId="";
        if (LPNulls.replaceNull(userName).length()==0){
            ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
            personId=instanceForActions.getToken().getPersonName();
        }else{
            Object[] personByUser = getPersonByUser(userName);            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUser[0].toString()))
                return new InternalMessage(LPPlatform.LAB_FALSE, personByUser[personByUser.length-1].toString(), new Object[]{}, null);
            personId=personByUser[0].toString();
        }
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsAppConfig.Person.PERSON_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{personId}, "");
	Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(TablesAppConfig.PERSON,
            updateFldsN, updateFldV, sqlWhere, null);        
        return new InternalMessage(updateRecordFieldsByFilter[0].toString(), updateRecordFieldsByFilter[updateRecordFieldsByFilter.length-1].toString(), new Object[]{}, null);
    }   
    
}
