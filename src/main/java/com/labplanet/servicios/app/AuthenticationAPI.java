/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import static com.labplanet.servicios.app.AppHeaderAPI.appHeaderApi;
import static com.labplanet.servicios.app.AppProcedureListAPI.SIZE_WHEN_CONSIDERED_MOBILE;
import static com.labplanet.servicios.app.AppProcedureListAPI.procedureListInfo;
import com.labplanet.servicios.app.AuthenticationAPIParams.AuthenticationAPIactionsEndpoints;
import com.labplanet.servicios.app.AuthenticationAPIParams.AuthenticationErrorTrapping;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPSession;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.TblsApp;
import databases.features.Token;
import databases.TblsApp.Users;
import databases.features.DbEncryption;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import functionaljavaa.user.UserAndRolesViews;
import static functionaljavaa.user.UserAndRolesViews.setUserDefaultTabsOnLogin;
import static functionaljavaa.user.UserAndRolesViews.setUserNewEsign;
import static functionaljavaa.user.UserAndRolesViews.setUserNewPassword;
import functionaljavaa.user.UserProfile;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPAPIArguments;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import static lbplanet.utilities.LPSession.frontEndIpChecker;
import lbplanet.utilities.Mailing;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;
import trazit.session.ResponseMessages;
import static trazit.codedocumentation.logic.AppBusinessRules.allAppBusinessRules;

/**
 *
 * @author Administrator
 */
public class AuthenticationAPI extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request = LPHttp.requestPreparation(request);
        response = LPHttp.responsePreparation(response);

        String language = LPFrontEnd.setLanguage(request);
        String dbName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_DB_NAME);
        if (dbName == null || dbName.length() == 0) {
            Rdbms.stablishDBConection();
        } else {
            Rdbms.stablishDBConection(dbName);
        }

        try (PrintWriter out = response.getWriter()) {

            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
                return;
            }
            ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);

            String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            AuthenticationAPIactionsEndpoints endPoint = null;
            try {
                endPoint = AuthenticationAPIactionsEndpoints.valueOf(actionName.toUpperCase());
            } catch (Exception e) {
                Rdbms.closeRdbms();
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }
            Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
                Rdbms.closeRdbms();
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            switch (endPoint) {
                case AUTHENTICATE:
                    Object[] ipCheck = frontEndIpChecker(request.getRemoteAddr());
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ipCheck[0].toString())) {
                        Rdbms.closeRdbms();
                        LPFrontEnd.servletReturnResponseError(request, response, ipCheck[ipCheck.length - 1].toString(), null, language, AuthenticationErrorTrapping.class.getSimpleName());
                        return;
                    }
                    String dbUserName = argValues[0].toString();
                    String dbUserPassword = argValues[1].toString();
                    String userIsCaseSensitive = prop.getString(UserAndRolesViews.UserAndRolesErrorTrapping.BUNDLEPARAM_CREDNTUSR_IS_CASESENSIT.getErrorCode());
                    if (Boolean.FALSE.equals(Boolean.valueOf(userIsCaseSensitive))) {
                        dbUserName = dbUserName.toLowerCase();
                    }

                    Object[] personNameObj = UserAndRolesViews.getPersonByUser(dbUserName);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personNameObj[0].toString())) {
                        Rdbms.closeRdbms();
                        LPFrontEnd.servletReturnResponseError(request, response, AuthenticationErrorTrapping.PERSON_NOT_FOUND.getErrorCode(), null, language, AuthenticationErrorTrapping.class.getSimpleName());
                        return;
                    }
                    String personName = personNameObj[0].toString();
                    Object[] validUserPassword = UserAndRolesViews.isValidUserPassword(dbUserName, dbUserPassword);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(validUserPassword[0].toString())) {
                        validUserPassword = UserAndRolesViews.isValidUserPassword(dbUserName, dbUserPassword);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(validUserPassword[0].toString())) {
                            Rdbms.closeRdbms();
                            LPFrontEnd.servletReturnResponseError(request, response, AuthenticationErrorTrapping.INVALID_USER_PWD.getErrorCode(), null, language, AuthenticationErrorTrapping.class.getSimpleName());
                            return;
                        }
                    }
                    Token token = new Token("");
                    String userMail = "";
                    Object[][] userInfoArr = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(),
                            new String[]{TblsApp.Users.USER_NAME.getName()}, new String[]{dbUserName}, new String[]{TblsApp.Users.EMAIL.getName()});
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equals(userInfoArr[0][0].toString()))) {
                        userMail = userInfoArr[0][0].toString();
                    }

                    String myToken = token.createToken(dbUserName, dbUserPassword, personName, "Admin", "", "", "", dbName, userMail);

                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put(AuthenticationAPIParams.RESPONSE_JSON_TAG_USER_INFO_ID, personName);
                    jsonObj.put(AuthenticationAPIParams.RESPONSE_JSON_TAG_MY_TOKEN, myToken);
                    Rdbms.closeRdbms();

                    LPFrontEnd.servletReturnSuccess(request, response, jsonObj);
                    return;
                case GETUSERROLE:
                    String firstToken = argValues[0].toString();
                    token = new Token(firstToken);

                    UserProfile usProf = new UserProfile();
                    Object[] allUserProcedurePrefix = usProf.getAllUserProcedurePrefix(token.getUserName());
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0].toString())) {
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, allUserProcedurePrefix);
                        return;
                    }
                    Object[] allUserProcedureRoles = usProf.getProcedureUserProfileFieldValues(allUserProcedurePrefix, token.getPersonName());
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedureRoles[0].toString())) {
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, allUserProcedureRoles);
                        return;
                    }
                    JSONArray jArray = new JSONArray();
                    jArray.addAll(Arrays.asList(allUserProcedureRoles));
                    Rdbms.closeRdbms();
                    response.getWriter().write(jArray.toString());
                    return;
                case FINALTOKEN:
                    try{
                    if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
                        return;
                    }
                    firstToken = argValues[0].toString();
                    String userRole = argValues[1].toString();

                    token = new Token(firstToken);

                    String[] fieldsName = new String[]{TblsApp.AppSession.PERSON.getName(), TblsApp.AppSession.ROLE_NAME.getName()};
                    Object[] fieldsValue = new Object[]{token.getPersonName(), userRole};
                    RdbmsObject newAppSession = LPSession.newAppSession(fieldsName, fieldsValue, request.getRemoteAddr());
                    if (Boolean.FALSE.equals(newAppSession.getRunSuccess())) {
                        LPFrontEnd.servletReturnResponseError(request, response, AuthenticationErrorTrapping.SESSION_ID_NOTGENERATED.getErrorCode(), null, language, AuthenticationErrorTrapping.class.getSimpleName());
                        return;
                    }
                    Integer sessionId = Integer.parseInt(newAppSession.getNewRowId().toString());
                    String sessionIdStr = sessionId.toString();

                    Date nowLocalDate = LPDate.getTimeStampLocalDate();
                    Object[][] userInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(),
                            new String[]{Users.USER_NAME.getName()}, new Object[]{token.getUserName()},
                            new String[]{Users.ESIGN.getName(), TblsApp.Users.TABS_ON_LOGIN.getName()});

                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userInfo[0][0].toString())) {
                        LPFrontEnd.servletReturnResponseError(request, response, AuthenticationErrorTrapping.ESGININFO_NOTAVAILABLE.getErrorCode(), null, language, AuthenticationErrorTrapping.class.getSimpleName());
                        return;
                    }
                    Object[] decryptValue = DbEncryption.decryptValue(userInfo[0][0].toString());
                    String eSignUncrypted = decryptValue[decryptValue.length - 1].toString();

                    String myFinalToken = token.createToken(token.getUserName(), token.getUsrPw(), token.getPersonName(),
                            userRole, sessionIdStr, nowLocalDate.toString(), eSignUncrypted, token.getDbName(), token.getUserMailAddress());
                    request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, myFinalToken);

                    jsonObj = new JSONObject();
                    jsonObj.put(AuthenticationAPIParams.RESPONSE_JSON_TAG_FINAL_TOKEN, myFinalToken);
                    jsonObj.put(AuthenticationAPIParams.RESPONSE_JSON_TAG_APP_SESSION_ID, sessionIdStr);
                    jsonObj.put(AuthenticationAPIParams.RESPONSE_JSON_TAG_APP_SESSION_DATE, nowLocalDate.toString());
                    if (userInfo[0][0].toString().length() == 0) {
                        jsonObj.put("warning", "no esign phrase");
                    }
                    String tabsStr = userInfo[0][1].toString();
                    String[] tabs = tabsStr.split("\\|");
                    JSONArray jArr = new JSONArray();
                    for (String curTab : tabs) {
                        String[] tabAttrArr = curTab.split("\\*");
                        JSONObject jObj = new JSONObject();
                        for (String curTabAttr : tabAttrArr) {
                            String[] curAttr = curTabAttr.split("\\:");
                            if (curAttr.length >= 2) {
                                jObj.put(curAttr[0], curAttr[1]);
                            }
                        }
                        jArr.add(jObj);
                    }
                    jsonObj.put(AuthenticationAPIParams.RESPNS_JSON_TAG_APPUSERTBS_ONLOGIN, jArr);
                    request.setAttribute(AuthenticationAPIParams.RESPONSE_JSON_TAG_FINAL_TOKEN, myFinalToken);
                    jsonObj.put("header_info", appHeaderApi(request, response));
                    jsonObj.put("procedures_list", procedureListInfo(request, response));
                    if (Boolean.FALSE.equals(GlobalVariables.PROC_MANAGEMENT_SPECIAL_ROLE.equalsIgnoreCase(userRole))) {
                        jsonObj.put("all_my_sops", SopUserAPIqueries.AllMySops(request, response));
                        jsonObj.put("all_my_pending_certif_approvals", SopUserAPIqueries.AllMyPendingSignSops(request, response));
                        jsonObj.put("procedures_sops", SopUserAPIqueries.ProceduresSops(request, response));
                        jsonObj.put("sop_tree_list_element", SopUserAPIqueries.SopTreeListElements(request, response));
                        jsonObj.put("all_my_analysis_methods", CertifyAnalysisMethodAPIfrontend.allMyAnalysisMethodCertif(request, response));
                        jsonObj.put("platform_business_rules", allAppBusinessRules());
                    }
                    Integer sizeValue = SIZE_WHEN_CONSIDERED_MOBILE + 1;
                    String sizeValueStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SIZE_VALUE);
                    if (sizeValueStr != null && sizeValueStr.length() > 0) {
                        sizeValue = Integer.valueOf(sizeValueStr);
                    }
                    jsonObj.put("platform_settings", AppProcedureListAPI.procModel("platform-settings", sizeValue, null));
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jsonObj);
                    return;
                    }catch(Exception e){
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, new Object[]{e.getMessage()});
                        return;                        
                    }
                case TOKEN_VALIDATE_ESIGN_PHRASE:
                    myToken = argValues[0].toString();
                    String esignPhraseToCheck = argValues[1].toString();

                    token = new Token(myToken);
                    if (token.geteSign().length() == 0) {
                        LPFrontEnd.servletReturnResponseError(request, response, AuthenticationErrorTrapping.TOKEN_ESIGN_ISNULL.getErrorCode(), new Object[]{esignPhraseToCheck}, language, AuthenticationErrorTrapping.class.getSimpleName());
                        return;
                    }
                    if (esignPhraseToCheck.equals(token.geteSign())) {
                        JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, null, null);

                        LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
                        return;
                    } else {
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, AuthenticationErrorTrapping.ESIGN_TOCHECK_INVALID, new Object[]{esignPhraseToCheck});
                        return;
                    }
                case TOKEN_VALIDATE_USER_CREDENTIALS:
                case TOKEN_VALIDATE_USER_CREDENTIALS_UNLOCKSESSION:
                    myToken = argValues[0].toString();
                    String userToCheck = argValues[1].toString();
                    String passwordToCheck = argValues[2].toString();

                    token = new Token(myToken);
                    String tokenUserName = token.getUserName();
                    userIsCaseSensitive = prop.getString(UserAndRolesViews.UserAndRolesErrorTrapping.BUNDLEPARAM_CREDNTUSR_IS_CASESENSIT.getErrorCode());
                    if (Boolean.FALSE.equals(Boolean.valueOf(userIsCaseSensitive))) {
                        userToCheck = userToCheck.toLowerCase();
                        tokenUserName = token.getUserName().toLowerCase();
                    }
                    if ((userToCheck.equals(tokenUserName)) && (passwordToCheck.equals(token.getUsrPw()))) {
                        JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, null, null);
                        LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
                    } else {
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, AuthenticationErrorTrapping.USRPWD_TOCHECK_INVALID, new Object[]{userToCheck});
                    }
                    break;
                case USER_CHANGE_PSWD:
                    String finalToken = argValues[0].toString();
                    String newPassword = argValues[1].toString();
                    userToCheck = argValues[2].toString();
                    passwordToCheck = argValues[3].toString();
                    token = new Token(finalToken);
                    Object[] newPwDiagn = setUserNewPassword(token.getUserName(), newPassword);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newPwDiagn[0].toString())) {
                        LPFrontEnd.servletReturnResponseError(request, response, AuthenticationErrorTrapping.USR_NEWPWD_NOT_SET.getErrorCode(), new Object[]{token.getUserName()}, language, AuthenticationErrorTrapping.class.getSimpleName());
                    }
                    String appStartedDate = null;
                    if (token.getAppSessionStartedDate() != null) {
                        appStartedDate = token.getAppSessionStartedDate().toString();
                    }
                    Token newToken = new Token("");
                    String myNewToken = newToken.createToken(token.getUserName(),
                            newPassword,
                            token.getPersonName(),
                            token.getUserRole(),
                            token.getAppSessionId(),
                            appStartedDate,
                            token.geteSign(), token.getDbName(), token.getUserMailAddress());
                    Rdbms.closeRdbms();
                    RelatedObjects rObj = RelatedObjects.getInstanceForActions();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), token.getUserName());
                    jsonObj = new JSONObject();
                    ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
                    messages.killInstance();
                    jsonObj = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, newPwDiagn, rObj.getRelatedObject());
                    jsonObj.put(AuthenticationAPIParams.RESPONSE_JSON_TAG_FINAL_TOKEN, myNewToken);
                    rObj.killInstance();
                    LPFrontEnd.servletReturnSuccess(request, response, jsonObj);
                    return;
                case USER_CHANGE_PSWD_SEND_MAIL:
                    Mailing mail=new Mailing();
                    mail.sendMailViaSSL("prueba SSL", "SSL esto es una prueba", new String[]{"info.fran.gomez@gmail.com"},
                            null, null, new String[]{"d:/FE Refactoring LP.xlsx"}, null);
                    Rdbms.closeRdbms();
                    return;
                case USER_CHANGE_PSWD_BY_MAIL:
                    finalToken = argValues[0].toString();
                    newPassword = argValues[1].toString();
                    userToCheck = argValues[2].toString();
                    passwordToCheck = argValues[3].toString();
                    token = new Token(finalToken);
                    newPwDiagn = setUserNewPassword(token.getUserName(), newPassword);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newPwDiagn[0].toString())) {
                        LPFrontEnd.servletReturnResponseError(request, response, AuthenticationErrorTrapping.USR_NEWPWD_NOT_SET.getErrorCode(), new Object[]{token.getUserName()}, language, AuthenticationErrorTrapping.class.getSimpleName());
                    }
                    appStartedDate = null;
                    if (token.getAppSessionStartedDate() != null) {
                        appStartedDate = token.getAppSessionStartedDate().toString();
                    }
                    newToken = new Token("");
                    myNewToken = newToken.createToken(token.getUserName(),
                            newPassword,
                            token.getPersonName(),
                            token.getUserRole(),
                            token.getAppSessionId(),
                            appStartedDate,
                            token.geteSign(), token.getDbName(), token.getUserMailAddress());
                    Rdbms.closeRdbms();
                    rObj = RelatedObjects.getInstanceForActions();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), token.getUserName());
                    jsonObj = new JSONObject();
                    jsonObj = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, newPwDiagn, rObj.getRelatedObject());
                    jsonObj.put(AuthenticationAPIParams.RESPONSE_JSON_TAG_FINAL_TOKEN, myNewToken);
                    rObj.killInstance();
                    LPFrontEnd.servletReturnSuccess(request, response, jsonObj);
                    return;
                case USER_CHANGE_ESIGN:
                    finalToken = argValues[0].toString();
                    String newEsign = argValues[1].toString();
                    userToCheck = argValues[2].toString();
                    passwordToCheck = argValues[3].toString();
                    token = new Token(finalToken);
                    Object[] newEsignDiagn = setUserNewEsign(token.getUserName(), newEsign);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newEsignDiagn[0].toString())) {
                        LPFrontEnd.servletReturnResponseError(request, response, AuthenticationErrorTrapping.USR_NEWPWD_NOT_SET.getErrorCode(), new Object[]{token.getUserName()}, language, AuthenticationErrorTrapping.class.getSimpleName());
                    }
                    appStartedDate = null;
                    if (token.getAppSessionStartedDate() != null) {
                        appStartedDate = token.getAppSessionStartedDate().toString();
                    }
                    newToken = new Token("");
                    myNewToken = newToken.createToken(token.getUserName(),
                            token.getUsrPw(),
                            token.getPersonName(),
                            token.getUserRole(),
                            token.getAppSessionId(),
                            appStartedDate,
                            newEsign, token.getDbName(), token.getUserMailAddress());

                    jsonObj = new JSONObject();
                    rObj = RelatedObjects.getInstanceForActions();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), token.getUserName());
                    jsonObj = new JSONObject();
                    messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
                    messages.killInstance();
                    jsonObj = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, newEsignDiagn, rObj.getRelatedObject());
                    jsonObj.put(AuthenticationAPIParams.RESPONSE_JSON_TAG_FINAL_TOKEN, myNewToken);
                    rObj.killInstance();
                    LPFrontEnd.servletReturnSuccess(request, response, jsonObj);
                    return;
                case SET_DEFAULT_TABS_ON_LOGIN:
                    finalToken = argValues[0].toString();
                    String tabsString = argValues[1].toString();
                    token = new Token(finalToken);
                    Object[] diagn = setUserDefaultTabsOnLogin(token, tabsString);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) {
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, diagn);
                    } else {
                        rObj = RelatedObjects.getInstanceForActions();
                        messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
                        messages.killInstance();
                        jsonObj = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, diagn, rObj.getRelatedObject());
                        rObj.killInstance();
                        LPFrontEnd.servletReturnSuccess(request, response, jsonObj);
                    }
                    return;
                default:
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                    break;
            }
        } catch (@SuppressWarnings("FieldNameHidesFieldInSuperclass") Exception e) {
            String exceptionMessage = e.getMessage();
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);
        } finally {
            Rdbms.closeRdbms();
            // release database resources
            try {
                Rdbms.closeRdbms();

            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (ServletException | IOException e) {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (ServletException | IOException e) {
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
