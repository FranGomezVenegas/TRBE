/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases.features;

import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPArray;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsApp;
import databases.TblsProcedure;
import functionaljavaa.user.UserProfile;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static lbplanet.utilities.LPMath.isNumeric;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author Administrator
 */
public final class Token {   
    private static final String KEY = "miclave";
    private static final String ISSUER = "LabPLANETdestrangisInTheNight";
    
    private static final String TOKEN_PARAM_USERDB="userDB";
    private static final String TOKEN_PARAM_USERPW="userDBPassword";
    private static final String TOKEN_PARAM_INTERNAL_USERID="internalUserID";
    private static final String TOKEN_PARAM_USERMAIL="userMail";
    private static final String TOKEN_PARAM_USER_ROLE="userRole";
    private static final String TOKEN_PARAM_USER_ESIGN="eSign";
    private static final String TOKEN_PARAM_APP_SESSION_ID="appSessionId";
    private static final String TOKEN_PARAM_PROCS_MODULE_NAME="procsModuleName";
    private static final String TKNPRM_APP_SESSION_STARTED_DATE="appSessionStartedDate";
    private static final String TOKEN_PARAM_USER_PROCEDURES="user_procedures";
    private static final String TOKEN_PARAM_DB_NAME="dbName";
    private static final String TKNPRM_USR_PROCS_VERSIONS_HASHCODES="user_procedure_hashcodes";
    private static final String TKNPRM_DATETIME_FORMT_AT_PLATFM_LVL="datetimeFormatAtPlatformLevel";
    
    private static final String TOKEN_PARAM_PREFIX = "TOKEN_";
    
    private String userName="";
    private String userMail="";
    private String usrPw="";
    private String personName="";
    private String userRole="";
    private String eSign="";
    private String appSessionId="";
    private Date appSessionStartedDate;
    private String userProcedures="";    
    private String userProceduresVersionsAndHashCodes="";     
    private String dbName="";
    private String procsModuleNames;
    private String datetimeFormatAtPlatformLvl;
    /**
     *
     * @param tokenString
     */
    public Token(String tokenString){
        String[] tokenParams = tokenParamsList();
        String[] tokenParamsValues = getTokenParamValue(tokenString, tokenParams);

        this.userName = tokenParamsValues[LPArray.valuePosicInArray(tokenParams, TOKEN_PARAM_USERDB)];
        this.userMail = tokenParamsValues[LPArray.valuePosicInArray(tokenParams, TOKEN_PARAM_USERMAIL)];
        this.usrPw = tokenParamsValues[LPArray.valuePosicInArray(tokenParams, TOKEN_PARAM_USERPW)];
        this.personName = tokenParamsValues[LPArray.valuePosicInArray(tokenParams, TOKEN_PARAM_INTERNAL_USERID)];         
        this.userRole = tokenParamsValues[LPArray.valuePosicInArray(tokenParams, TOKEN_PARAM_USER_ROLE)];                             
        this.eSign = tokenParamsValues[LPArray.valuePosicInArray(tokenParams, TOKEN_PARAM_USER_ESIGN)];     
        this.appSessionId = tokenParamsValues[LPArray.valuePosicInArray(tokenParams, TOKEN_PARAM_APP_SESSION_ID)];    
        this.userProcedures = tokenParamsValues[LPArray.valuePosicInArray(tokenParams, TOKEN_PARAM_USER_PROCEDURES)]; 
        this.userProceduresVersionsAndHashCodes=tokenParamsValues[LPArray.valuePosicInArray(tokenParams, TKNPRM_USR_PROCS_VERSIONS_HASHCODES)]; 
        this.dbName = tokenParamsValues[LPArray.valuePosicInArray(tokenParams, TOKEN_PARAM_DB_NAME)]; 
        this.procsModuleNames = tokenParamsValues[LPArray.valuePosicInArray(tokenParams, TOKEN_PARAM_PROCS_MODULE_NAME)];
        this.datetimeFormatAtPlatformLvl = tokenParamsValues[LPArray.valuePosicInArray(tokenParams, TKNPRM_DATETIME_FORMT_AT_PLATFM_LVL)];
        
    }

    /**
     *
     * @return
     */
    private String[] tokenParamsList(){
        String[] diagnoses = new String[0];        
        diagnoses = LPArray.addValueToArray1D(diagnoses, TOKEN_PARAM_USERDB);
        diagnoses = LPArray.addValueToArray1D(diagnoses, TOKEN_PARAM_USERPW);
        diagnoses = LPArray.addValueToArray1D(diagnoses, TOKEN_PARAM_INTERNAL_USERID);
        diagnoses = LPArray.addValueToArray1D(diagnoses, TOKEN_PARAM_USER_ROLE);
        diagnoses = LPArray.addValueToArray1D(diagnoses, TOKEN_PARAM_USERMAIL);
        diagnoses = LPArray.addValueToArray1D(diagnoses, TOKEN_PARAM_APP_SESSION_ID);
        diagnoses = LPArray.addValueToArray1D(diagnoses, TKNPRM_APP_SESSION_STARTED_DATE);
        diagnoses = LPArray.addValueToArray1D(diagnoses, TOKEN_PARAM_USER_ESIGN);
        diagnoses = LPArray.addValueToArray1D(diagnoses, TOKEN_PARAM_USER_PROCEDURES);
        diagnoses = LPArray.addValueToArray1D(diagnoses, TKNPRM_USR_PROCS_VERSIONS_HASHCODES);
        diagnoses = LPArray.addValueToArray1D(diagnoses, TOKEN_PARAM_DB_NAME);
        diagnoses = LPArray.addValueToArray1D(diagnoses, TOKEN_PARAM_PROCS_MODULE_NAME);
        diagnoses = LPArray.addValueToArray1D(diagnoses, TKNPRM_DATETIME_FORMT_AT_PLATFM_LVL);
        return diagnoses;
    }  
    
    private Object[] isValidToken(String token){
        if (token.length()==0) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE , "tokenIsEmptyOrInvalid", new Object[]{});
        Object[] diagnoses = new Object[0];
        try {
            
            Algorithm algorithm = Algorithm.HMAC256(KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(new String[]{ISSUER})
                .build(); //Reusable verifier instance
            DecodedJWT decode = JWT.decode(token); // This is need for the check that should be implemented
            DecodedJWT jwt = verifier.verify(token);            
            
            // Check that the fields in the header are present, not just verify that the token construction is ok.
            
            diagnoses = LPArray.addValueToArray1D(diagnoses, true);
            diagnoses = LPArray.addValueToArray1D(diagnoses, jwt);
            return diagnoses;
            
        } catch (JWTVerificationException exception){
            diagnoses = LPArray.addValueToArray1D(diagnoses, false);
            return diagnoses;
        }       
    }
    
    /**
     *
     * @param token
     * @return
     */
    public String validateToken(String token){
        return isValidToken(token)[0].toString();
    }
    
    /**
     *
     * @param token
     * @param paramName
     * @return
     */
    public String[] getTokenParamValue(String token, String[] paramName){
        String[] infoFromToken = new String[0];
        
        for (String pn: paramName){
            String paramValue = getTokenParamValue(token, pn);
            infoFromToken = LPArray.addValueToArray1D(infoFromToken, paramValue);
        }
        return infoFromToken;            
    }    

    /**
     *
     * @param userDBId
     * @param userDBPassword
     * @param userId
     * @param userRole
     * @param appSessionId
     * @param appSessionStartedDate
     * @param eSign
     * @param dbName
     * @param userMail
     * @return
     */
    public String  createToken(String userDBId, String userDBPassword, String userId, String userRole, String appSessionId, String appSessionStartedDate, String eSign, String dbName, String userMail){        
        Algorithm algorithm = Algorithm.HMAC256(KEY); 
        Map <String, Object> myParams = new HashMap<>();
        myParams.put(TOKEN_PARAM_USERDB, userDBId);
        myParams.put(TOKEN_PARAM_USERPW, userDBPassword);
        myParams.put(TOKEN_PARAM_INTERNAL_USERID, userId);
        myParams.put(TOKEN_PARAM_USER_ROLE, userRole);
        myParams.put(TOKEN_PARAM_APP_SESSION_ID, appSessionId);
        myParams.put(TKNPRM_APP_SESSION_STARTED_DATE, appSessionStartedDate);
        myParams.put(TOKEN_PARAM_USER_ESIGN, eSign);
        myParams.put(TOKEN_PARAM_DB_NAME, dbName);
        myParams.put(TOKEN_PARAM_USERMAIL, userMail);
        UserProfile usProf = new UserProfile();
        Object[] allUserProcedurePrefix = usProf.getAllUserProcedurePrefix(userDBId);
        myParams.put(TOKEN_PARAM_USER_PROCEDURES, Arrays.toString(allUserProcedurePrefix));
        String procHashCodes="";  
        String procModulesArr="";
        for (Object curProcPrefix: allUserProcedurePrefix){            
            if (Boolean.FALSE.equals(GlobalVariables.PROC_MANAGEMENT_SPECIAL_ROLE.equalsIgnoreCase(curProcPrefix.toString()))){
                if (procHashCodes.length()>0)procHashCodes=procHashCodes+"|";
                Object[][] procInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(curProcPrefix.toString(), GlobalVariables.Schemas.PROCEDURE.getName()), 
                    TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(), 
                    new String[]{TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{curProcPrefix.toString()}, 
                    new String[]{TblsProcedure.ProcedureInfo.VERSION.getName(), TblsProcedure.ProcedureInfo.PROCEDURE_HASH_CODE.getName(), TblsProcedure.ProcedureInfo.MODULE_NAME.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfo[0][0].toString()))
                    return "ERROR: procedure_info into node procedure not found for instance "+curProcPrefix.toString();  
                else{
                    procHashCodes=procHashCodes+curProcPrefix.toString()+"*"+procInfo[0][0].toString()+"*"+procInfo[0][1].toString();            
                    if (procModulesArr.length()>0)
                        procModulesArr=procModulesArr+"|";
                    procModulesArr=procModulesArr+curProcPrefix.toString()+"*"+procInfo[0][2].toString();
                }
            }
        }   
        SqlWhere sql=new SqlWhere();
        sql.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, 
                SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{"datetimeFormat"}, null);
        Object[][] appBusRulesInfo = QueryUtilitiesEnums.getTableData(TblsApp.TablesApp.APP_BUSINESS_RULES, 
            EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.APP_BUSINESS_RULES, 
                new String[]{TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()}), 
                sql, null, "app");
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appBusRulesInfo[0][0].toString())){
            myParams.put(TKNPRM_DATETIME_FORMT_AT_PLATFM_LVL, "DISABLED");
            this.datetimeFormatAtPlatformLvl="DISABLED";
        }else{
            myParams.put(TKNPRM_DATETIME_FORMT_AT_PLATFM_LVL, appBusRulesInfo[0][0].toString());
            this.datetimeFormatAtPlatformLvl=appBusRulesInfo[0][0].toString();
        }
        myParams.put(TKNPRM_USR_PROCS_VERSIONS_HASHCODES, procHashCodes);
        this.procsModuleNames=procModulesArr;
        myParams.put(TOKEN_PARAM_PROCS_MODULE_NAME, procModulesArr);
        try{
            return JWT.create()
                    .withHeader(myParams)
                    .withIssuer(ISSUER)                    
                    .sign(algorithm);
       } catch (JWTCreationException exception){
            return "ERROR: You need to enable Algorithm.HMAC256";        
        }
    }    
        
    /**
     *
     * @param token
     * @param paramName
     * @return
     */
    public String getTokenParamValue(String token, String paramName){
       Object[] tokenObj = isValidToken(token);
        
       if (Boolean.FALSE.equals(Boolean.valueOf(tokenObj[0].toString()))) return LPPlatform.LAB_FALSE;

       DecodedJWT jwt = (DecodedJWT) tokenObj[1];
       Claim header1 = jwt.getHeaderClaim(paramName);            
       return header1.asString();                    
    }
    /**
     * The fieldName should include one prefix that is "TOKEN_" otherwise it will not be interpreted as a correct param.
     * @param token
     * @param fieldName
     * @return
     */
    public static String[] getTokenFieldValue(String fieldName, String token) {
        if (fieldName == null) {
            return new String[]{LPPlatform.LAB_FALSE, ""};
        }
        if (Boolean.FALSE.equals(fieldName.toUpperCase().contains(TOKEN_PARAM_PREFIX))) {
            return new String[]{LPPlatform.LAB_FALSE, ""};
        }
        Token tokenObj = new Token(token);
        String tokenParamValue = tokenObj.getTokenParamValue(token, fieldName.replace(TOKEN_PARAM_PREFIX, ""));
        if (tokenParamValue != null) {
            return new String[]{LPPlatform.LAB_TRUE, tokenParamValue};
        } else {
            return new String[]{LPPlatform.LAB_FALSE, ""};
        }
    }
    public String getProcedureInstanceName(String procInstanceName){        
        return getInfoFromProcedureInstanceVersionsAndHashCode(procInstanceName, TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME);
    }
    public String getProcedureInstanceHashCode(String procInstanceName){        
        return getInfoFromProcedureInstanceVersionsAndHashCode(procInstanceName, TblsProcedure.ProcedureInfo.PROCEDURE_HASH_CODE);
    }
    public Integer getProcedureInstanceVersion(String procInstanceName){
        String procVersion = getInfoFromProcedureInstanceVersionsAndHashCode(procInstanceName, TblsProcedure.ProcedureInfo.VERSION);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isNumeric(procVersion)[0].toString())) 
            return Integer.valueOf(procVersion);
        return -999;
    }
    private String getInfoFromProcedureInstanceVersionsAndHashCode(String procInstanceName, TblsProcedure.ProcedureInfo field){
        String[] splitted = this.userProceduresVersionsAndHashCodes.split("\\|");
        for (String curVal: splitted){
            if (curVal.toUpperCase().contains(procInstanceName.toUpperCase())){
                String[] splittedEntry = curVal.split("\\*");
                TblsProcedure.ProcedureInfo endPoint = null;  
                switch (field){
                    case PROC_INSTANCE_NAME:
                        if (splittedEntry.length>=1)
                            return splittedEntry[0];
                        else
                            return "ERROR.array has no column [0]";
                    case VERSION:
                        if (splittedEntry.length>=2)
                            return splittedEntry[1];
                        else
                            return "ERROR.array has no column [1]";
                    case PROCEDURE_HASH_CODE:
                        if (splittedEntry.length>=3)
                            return splittedEntry[2];
                        else
                            return "ERROR.array has no column [2]";
                    default:
                        return "";                        
                }
            }
        }
        return "";
    }
    /**
     * @return the userName
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * @return the userDBPassword
     */
    public String getUsrPw() {
        return this.usrPw;
    }

    /**
     * @return the personName
     */
    public String getPersonName() {
        return this.personName;
    }
    public String getUserMailAddress() {
        return this.userMail;
    }
    

    /**
     * @return the userRole
     */
    public String getUserRole() {
        return this.userRole;
    }

    /**
     * @return the eSign
     */
    public String geteSign() {
        return this.eSign;
    }

    /**
     * @return the appSessionId
     */
    public String getAppSessionId() {
        return this.appSessionId;
    }

    /**
     * @return the appSessionStartedDate
     */
    public Date getAppSessionStartedDate() {
        return this.appSessionStartedDate;
    }
    /**
     * @return the userProceduresList in a Arrays.strings format
     */
    public String getUserProcedures() {
        return this.userProcedures;
    }    
    /**
     * @return the userProceduresList in a Arrays.strings format
     */
    public String getDbName() {
        return this.dbName;
    }    
    /**
     * @return the userProceduresList in a Arrays.strings format
     */
    public String getProcsModuleNames() {
        return this.procsModuleNames;
    }    
    
    public String getModuleNameFromProcInstance(String instanceName){
        if (this.procsModuleNames==null||!this.procsModuleNames.contains(instanceName))
            return "notFound";
        
        for (String curFld: this.procsModuleNames.split("\\|")){
            if (curFld.contains(instanceName)){
                String[] split = curFld.split("\\*");
                return split[1];
            }                
        }        
        return "notFound";
        
    }

    /**
     * @return the dateFormatAtPlatformLvl
     */
    public String getDateFormatAtPlatformLvl() {
        return datetimeFormatAtPlatformLvl;
    }
    
}
