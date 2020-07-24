/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import lbplanet.utilities.LPFrontEnd;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;

/**
 *
 * @author Administrator
 */
public class AuthenticationAPIParams extends HttpServlet {

    public enum AuthenticationAPIEndpoints{
        AUTHENTICATE("AUTHENTICATE", "userAuthentication_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DB_USERNAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DB_PSSWD, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
        GETUSERROLE("GETUSERROLE", "getUserRoles_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MY_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)} ),
        FINALTOKEN("FINALTOKEN", "finalToken_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MY_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_ROLE, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)} ),
        TOKEN_VALIDATE_USER_CREDENTIALS("TOKEN_VALIDATE_USER_CREDENTIALS", "tokenValidateUserCredentials_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), true, 8)} ),
        TOKEN_VALIDATE_ESIGN_PHRASE("TOKEN_VALIDATE_ESIGN_PHRASE", "tokenValdidateEsignPhrase_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ESIGN_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)} ),
        USER_CHANGE_PSWD("USER_CHANGE_PASSWORD", "userChangePswd_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_NEW, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 9)} ),
        USER_CHANGE_ESIGN("USER_CHANGE_ESIGN", "userChangeEsign_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ESIGN_NEW, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK, LPAPIArguments.ArgumentType.STRING.toString(), false, 9)} ),
        SET_DEFAULT_TABS_ON_LOGIN("SET_DEFAULT_TABS_ON_LOGIN", "defaultTabsOnLogin_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABS_STRING, LPAPIArguments.ArgumentType.STRING.toString(), false, 7)}),
        ;      
        private AuthenticationAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;            
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
        public String getName(){
            return this.name;
        }
        public String getSuccessMessageCode(){
            return this.successMessageCode;
        }           

        /**
         * @return the arguments
         */
        public LPAPIArguments[] getArguments() {
            return arguments;
        }
     
        private final String name;
        private final String successMessageCode;  
        private final  LPAPIArguments[] arguments;

    }
    
    /**
     *
     */

    /**
     *
     */

        
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
    public static final String ERROR_PROPERTY_INVALID_USER_PSSWD = "authenticationAPI_invalidUserPsswd";

    /**
     *
     */
    public static final String ERROR_PROPERTY_SESSION_ID_NULL_NOT_ALLOWED = "authenticationAPI_sessionIdNullNotAllowed";

    /**
     *
     */
    public static final String ERROR_PROPERTY_ESIGN_INFO_NOT_AVAILABLE = "authenticationAPI_esignInfoNotAvailable";

    /**
     *
     */
    public static final String ERROR_PROPERTY_PERSON_NOT_FOUND = "authenticationAPI_personNotFound";

    /**
     *
     */
    public static final String ERROR_PROPERTY_SESSION_ID_NOT_GENERATED = "authenticationAPI_sessionIdNotGenerated";

    /**
     *
     */
    public static final String ERROR_API_ERRORTRAPING_PROPERTY_USER_PSSWD_TO_CHECK_INVALID = "authenticationAPI_userPsswdToCheckInvalid";
    public static final String ERROR_API_ERRORTRAPING_PROPERTY_USER_NEW_PSSWD_NOT_SET = "authenticationAPI_userNewPsswdNotSet";

    /**
     *
     */
    public static final String ERROR_API_ERRORTRAPING_PROPERTY_ALL_USER_PROCEDURE_PREFIX = "allUserProcedurePrefixReturnedFalse";

    /**
     *
     */
    public static final String ERROR_API_ERRORTRAPING_PROPERTY_ESIGN_TO_CHECK_INVALID = "authenticationAPI_esignToCheckInvalid";
    
    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME;

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
