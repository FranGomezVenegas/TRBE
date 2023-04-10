/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import functionaljavaa.platform.doc.EndPointsToRequirements;
import lbplanet.utilities.LPFrontEnd;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.json.JsonArray;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables.ApiUrls;

/**
 *
 * @author Administrator
 */
public class AuthenticationAPIParams extends HttpServlet {
final static String USER_CHANGE_PSWD_SUCCESS="userChangePswd_success";
    public enum AuthenticationAPIactionsEndpoints implements EnumIntEndpoints{
        USER_CHANGE_PSWD("USER_CHANGE_PASSWORD", USER_CHANGE_PSWD_SUCCESS, 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_NEW, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects 
                , null, null),
        USER_CHANGE_PSWD_SEND_MAIL("USER_CHANGE_PSWD_SEND_MAIL", USER_CHANGE_PSWD_SUCCESS, 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_NEW, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),}, EndPointsToRequirements.endpointWithNoOutputObjects 
                , null, null),
        AUTHENTICATE("AUTHENTICATE", "userAuthentication_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DB_USERNAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DB_PSSWD, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects
                , null, null),
        GETUSERROLE("GETUSERROLE", "getUserRoles_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MY_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, EndPointsToRequirements.endpointWithNoOutputObjects 
                , null, null),
        FINALTOKEN("FINALTOKEN", "finalToken_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MY_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_ROLE, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects 
                , null, null),
        TOKEN_VALIDATE_USER_CREDENTIALS("TOKEN_VALIDATE_USER_CREDENTIALS", "tokenValidateUserCredentials_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, EndPointsToRequirements.endpointWithNoOutputObjects 
                , null, null),
        TOKEN_VALIDATE_USER_CREDENTIALS_UNLOCKSESSION("TOKEN_VALIDATE_USER_CREDENTIALS_UNLOCKSESSION", "tokenValidateUserCredentialsUnlockedSession_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, EndPointsToRequirements.endpointWithNoOutputObjects 
                , null, null),
        TOKEN_VALIDATE_ESIGN_PHRASE("TOKEN_VALIDATE_ESIGN_PHRASE", "tokenValdidateEsignPhrase_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ESIGN_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects 
                , null, null),
        USER_CHANGE_PSWD_BY_MAIL("USER_CHANGE_PSWD_BY_MAIL", USER_CHANGE_PSWD_SUCCESS, 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_NEW, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects 
                , null, null),
        USER_CHANGE_ESIGN("USER_CHANGE_ESIGN", "userChangeEsign_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ESIGN_NEW, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects
                , null, null),
        SET_DEFAULT_TABS_ON_LOGIN("SET_DEFAULT_TABS_ON_LOGIN", "defaultTabsOnLogin_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABS_STRING, LPAPIArguments.ArgumentType.STRING.toString(), false, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects
                , null, null)
        ;      
        private AuthenticationAPIactionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
            this.outputObjectTypes=outputObjectTypes;
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);            
        } 

        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+contentLine[lineIndex][curArg.getTestingArgPosic()]);
                request.setAttribute(curArg.getName(), contentLine[lineIndex][curArg.getTestingArgPosic()]);
            }  
            hm.put(request, argValues);            
            return hm;
        }
        @Override        public String getName(){return this.name;}
        @Override        public String getSuccessMessageCode(){return this.successMessageCode;}           
        @Override        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        @Override        public LPAPIArguments[] getArguments() {return arguments;}
        @Override        public String getApiUrl(){return ApiUrls.APP_AUTHENTICATION_ACTIONS.getUrl();}
        private final String name;
        private final String successMessageCode;  
        private final  LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
        @Override public String getDeveloperComment() { return this.devComment;}
        @Override        public String getDeveloperCommentTag() {            return this.devCommentTag;        }
        private final String devComment;
        private final String devCommentTag;        
    }
public enum AuthenticationErrorTrapping  implements EnumIntMessages{ 
        IP_IN_BLACK_LIST("IPinBlackList", "", ""),
        IP_NOTIN_WHITE_LIST("IPnotInWhiteList", "", ""),
        WRONG_IP("wrongIP", "", ""),
        INVALID_USER_PWD("authenticationAPI_invalidUserPsswd", "", ""),
        SESSION_ID_NULLNOTALLOWED("authenticationAPI_sessionIdNullNotAllowed", "", ""),
        SESSION_ID_NOTGENERATED("authenticationAPI_sessionIdNotGenerated", "", ""),

        ESGININFO_NOTAVAILABLE("authenticationAPI_esignInfoNotAvailable", "", ""),
        PERSON_NOT_FOUND("authenticationAPI_personNotFound", "", ""),
        USRPWD_TOCHECK_INVALID("authenticationAPI_userPsswdToCheckInvalid", "", ""),
        ESIGN_TOCHECK_INVALID("authenticationAPI_esignToCheckInvalid", "", ""),
        TOKEN_ESIGN_ISNULL("authenticationAPI_tokenEsignValueNull", "", ""),
        
        USR_NEWPWD_NOT_SET("authenticationAPI_userNewPsswdNotSet", "", ""),
        ;
        private AuthenticationErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
     */
    public static final String RESPONSE_JSON_TAG_USER_INFO_ID = "userInfoId";

    /**
     *
     */
    public static final String RESPONSE_JSON_TAG_FINAL_TOKEN = "finalToken";

    /**
     *
     */
    public static final String RESPONSE_JSON_TAG_MY_TOKEN = "myToken";

    /**
     *
     */
    public static final String RESPONSE_JSON_TAG_APP_SESSION_ID = "appSessionId";

    /**
     *
     */
    public static final String RESPONSE_JSON_TAG_APP_SESSION_DATE = "appSessionStartDate";
    public static final String RESPNS_JSON_TAG_APPUSERTBS_ONLOGIN = "userTabsOnLogin";

    
    
    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

    /**
     *
     */
   
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws  IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet authenticationAPIParams</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet authenticationAPIParams at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
