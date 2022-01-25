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
import trazit.enums.EnumIntEndpoints;

/**
 *
 * @author Administrator
 */
public class AuthenticationAPIParams extends HttpServlet {

    public enum AuthenticationAPIEndpoints implements EnumIntEndpoints{
        USER_CHANGE_PSWD("USER_CHANGE_PASSWORD", "userChangePswd_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_NEW, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects ),
        USER_CHANGE_PSWD_SEND_MAIL("USER_CHANGE_PSWD_SEND_MAIL", "userChangePswd_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_NEW, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),}, EndPointsToRequirements.endpointWithNoOutputObjects ),
        AUTHENTICATE("AUTHENTICATE", "userAuthentication_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DB_USERNAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DB_PSSWD, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects),
        GETUSERROLE("GETUSERROLE", "getUserRoles_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MY_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, EndPointsToRequirements.endpointWithNoOutputObjects ),
        FINALTOKEN("FINALTOKEN", "finalToken_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MY_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_ROLE, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects ),
        TOKEN_VALIDATE_USER_CREDENTIALS("TOKEN_VALIDATE_USER_CREDENTIALS", "tokenValidateUserCredentials_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, EndPointsToRequirements.endpointWithNoOutputObjects ),
        TOKEN_VALIDATE_ESIGN_PHRASE("TOKEN_VALIDATE_ESIGN_PHRASE", "tokenValdidateEsignPhrase_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ESIGN_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects ),
        USER_CHANGE_PSWD_BY_MAIL("USER_CHANGE_PSWD_BY_MAIL", "userChangePswd_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_NEW, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects ),
        USER_CHANGE_ESIGN("USER_CHANGE_ESIGN", "userChangeEsign_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ESIGN_NEW, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects),
        SET_DEFAULT_TABS_ON_LOGIN("SET_DEFAULT_TABS_ON_LOGIN", "defaultTabsOnLogin_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABS_STRING, LPAPIArguments.ArgumentType.STRING.toString(), false, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects),
        ;      
        private AuthenticationAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
            this.outputObjectTypes=outputObjectTypes;                        
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
        public String getName(){return this.name;}
        public String getSuccessMessageCode(){return this.successMessageCode;}           
        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        public LPAPIArguments[] getArguments() {return arguments;}
        private final String name;
        private final String successMessageCode;  
        private final  LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }
public enum AuthenticationErrorTrapping{ 
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
        public String getErrorCode(){return this.errorCode;}
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
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
    public static final String RESPONSE_JSON_TAG_APP_USER_TABS_ON_LOGIN = "userTabsOnLogin";

    
    
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
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
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
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
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
