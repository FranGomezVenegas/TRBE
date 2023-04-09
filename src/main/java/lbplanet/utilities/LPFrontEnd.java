package lbplanet.utilities;

import com.github.opendevl.JFlat;
import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import databases.features.Token;
import functionaljavaa.parameter.Parameter;
import trazit.session.ResponseMessages;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;
import static trazit.globalvariables.GlobalVariables.LANGUAGE_ALL_LANGUAGES;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class LPFrontEnd {

    public enum ResponseTags {
        DIAGNOSTIC(GlobalAPIsParams.LBL_DIAGNOSTIC), CATEGORY("category"), MESSAGE_CODE("messageCode"), MESSAGE("message"), RELATED_OBJECTS("related_objects"), IS_ERROR("is_error");

        private ResponseTags(String labelName) {
            this.labelName = labelName;
        }

        public String getLabelName() {
            return this.labelName;
        }
        private final String labelName;
    }

    private LPFrontEnd() {
        throw new IllegalStateException("Utility class");
    }

    public static String setLanguage(HttpServletRequest request) {
        String language = request.getParameter(LPPlatform.REQUEST_PARAM_LANGUAGE);
        if (language == null) {
            language = LANGUAGE_ALL_LANGUAGES;
        }
        return language;
    }

    /**
     * Mucho uso
     *
     * @param request
     * @param response
     * @return
     */
    public static final Boolean servletStablishDBConection(HttpServletRequest request, HttpServletResponse response) {
        String dbName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_DB_NAME);

        boolean isConnected = false;
        if (dbName == null || dbName.length() == 0) {
            String theToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_MY_TOKEN);
            if (theToken == null || theToken.length() == 0) {
                theToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            }
            if (theToken == null || theToken.length() == 0) {
                isConnected = Rdbms.getRdbms().startRdbms();
            } else {
                Token token = new Token(theToken);
                dbName = token.getDbName();
                if (dbName == null || dbName.length() == 0) {
                    isConnected = Rdbms.getRdbms().startRdbms();
                } else {
                    isConnected = Rdbms.getRdbms().startRdbms(dbName);
                }
            }
        } else {
            isConnected = Rdbms.getRdbms().startRdbms(dbName);
        }
        if (!isConnected) {
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPPlatform.ApiErrorTraping.PROPERTY_DATABASE_NOT_CONNECTED.getErrorCode(), null, null, LPPlatform.ApiErrorTraping.class.getSimpleName());
        }
        return isConnected;
    }

    /**
     * En uso, no tocar
     *
     * @param request
     * @param dbUserName
     * @param dbUserPassword
     * @return
     */
    public static final Boolean servletUserToVerify(HttpServletRequest request, String dbUserName, String dbUserPassword) {
        String userToVerify = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK);
        if (userToVerify == null) {
            userToVerify = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK)).toString();
        }
        String passwordToVerify = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK);
        if (passwordToVerify == null) {
            passwordToVerify = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK)).toString();
        }
        return Boolean.FALSE.equals((Boolean.FALSE.equals(userToVerify.equalsIgnoreCase(dbUserName))) || (Boolean.FALSE.equals(passwordToVerify.equalsIgnoreCase(dbUserPassword))));
    }

    /**
     * En uso, no tocar
     *
     * @param request
     * @param eSign
     * @return
     */
    public static final Boolean servletEsignToVerify(HttpServletRequest request, String eSign) {
        String eSignToVerify = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ESIGN_TO_CHECK);
        if (eSignToVerify == null) {
            eSignToVerify = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_ESIGN_TO_CHECK)).toString();
        }
        return eSignToVerify.equalsIgnoreCase(eSign);
    }

    /**
     * En uso
     *
     * @param errorPropertyName
     * @param errorPropertyValue
     * @return
     */
    private static JSONObject responseJSONError(String errorPropertyName, Object[] errorPropertyValue, String className) {
        JSONObject errJsObj = new JSONObject();
        errJsObj.put(ResponseTags.MESSAGE.getLabelName(), errorPropertyName);
        String errorTextEn = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_ERRORTRAPING, null, errorPropertyName, null, true, className);
        if (errorTextEn == null || errorTextEn.length() == 0) {
            errorTextEn = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, "api-platform", null, errorPropertyName, "en", true, null);
        }
        if (errorPropertyValue != null) {
            for (int iVarValue = 1; iVarValue <= errorPropertyValue.length; iVarValue++) {
                errorTextEn = errorTextEn.replace("<*" + iVarValue + "*>", LPNulls.replaceNull(errorPropertyValue[iVarValue - 1]).toString());
            }
        }
        errJsObj.put(ResponseTags.MESSAGE.getLabelName() + "_en", errorTextEn);
        String errorTextEs = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_ERRORTRAPING, null, errorPropertyName, "es", false, className);
        if (errorTextEs == null || errorTextEs.length() == 0) {
            errorTextEs = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, "api-platform", null, errorPropertyName, "es", false, null);
        }
        if (errorPropertyValue != null) {
            for (int iVarValue = 1; iVarValue <= errorPropertyValue.length; iVarValue++) {
                errorTextEs = errorTextEs.replace("<*" + iVarValue + "*>", LPNulls.replaceNull(errorPropertyValue[iVarValue - 1]).toString());
            }
        }
        errJsObj.put(ResponseTags.MESSAGE_CODE.getLabelName(), errorPropertyName);
        errJsObj.put(ResponseTags.MESSAGE.getLabelName() + "_es", errorTextEs);
        errJsObj.put(ResponseTags.DIAGNOSTIC.getLabelName(), LPPlatform.LAB_FALSE);
        errJsObj.put(ResponseTags.IS_ERROR.getLabelName(), true);
        return errJsObj;
    }

    /**
     * En mucho uso
     *
     * @param errorStructure
     * @return
     */
    public static Object[] responseError(Object[] errorStructure) {
        Object[] responseObj = new Object[0];
        responseObj = LPArray.addValueToArray1D(responseObj, HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);
        if (errorStructure.length > 0) {
            responseObj = LPArray.addValueToArray1D(responseObj, errorStructure[errorStructure.length - 1].toString());
        } else {
            responseObj = LPArray.addValueToArray1D(responseObj, Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName());
        }
        return responseObj;
    }
    private static final int CLIENT_CODE_STACK_INDEX;

    static {
        int i = 0;
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            i++;
            if (ste.getClassName().equals(LPPlatform.class.getName())) {
                break;
            }
        }
        CLIENT_CODE_STACK_INDEX = i;
    }

    private static void servetInvokeResponseErrorServlet(HttpServletRequest request, HttpServletResponse response) {
        RequestDispatcher rd = request.getRequestDispatcher(GlobalVariables.ServletsResponse.ERROR.getServletName());
        try {
            rd.forward(request, response);
        } catch (ServletException | IOException ex) {
            Logger.getLogger(LPFrontEnd.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void servetInvokeResponseSuccessServlet(HttpServletRequest request, HttpServletResponse response) {
        RequestDispatcher rd = request.getRequestDispatcher(GlobalVariables.ServletsResponse.SUCCESS.getServletName());
        try {
            rd.forward(request, response);
        } catch (IOException | ServletException ex) {
            Logger.getLogger(LPFrontEnd.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * En mucho uso
     *
     * @param request
     * @param response
     * @param errorCode
     * @param errorCodeVars
     * @param language
     * @param className
     */
    public static final void servletReturnResponseError(HttpServletRequest request, HttpServletResponse response, String errorCode, Object[] errorCodeVars, String language, String className) {

        JSONObject errJSONMsg = LPFrontEnd.responseJSONError(errorCode, errorCodeVars, className);
        request.setAttribute(GlobalVariables.ServletsResponse.ERROR.getAttributeName(), errJSONMsg.toString());
        servetInvokeResponseErrorServlet(request, response);
    }

    /**
     * En mucho uso
     *
     * @param request
     * @param response
     * @param myStr
     */
    public static final void servletReturnSuccess(HttpServletRequest request, HttpServletResponse response, String myStr) {
        if (myStr == null) {
            request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(), "");
        } else {
            request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(), myStr);
        }
        servetInvokeResponseSuccessServlet(request, response);
    }

    /**
     * En mucho uso
     *
     * @param request
     * @param response
     * @param jsonObj
     */
    public static final void servletReturnSuccess(HttpServletRequest request, HttpServletResponse response, JSONObject jsonObj) {
        if (jsonObj == null) {
            request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(), "");
        } else {
            request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(), jsonObj.toString());
        }
        servetInvokeResponseSuccessServlet(request, response);
    }

    public static final void servletReturnSuccessFile(HttpServletRequest request, HttpServletResponse response, JSONObject jsonObj, HttpServlet srv, String filePath, String fileName) {
        if (jsonObj == null) {
            request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(), "");
        } else {
            try {
                if (filePath == null || filePath.length() == 0) {
                    filePath = "fake";
                }
                if (fileName == null || fileName.length() == 0) {
                    fileName = "mycsv.csv";
                }
                String fileWithPath = filePath + fileName; 
                String str = jsonObj.toJSONString(); 

                JFlat flatMe = new JFlat(str);

                //directly write the JSON document to CSV
                flatMe.json2Sheet().write2csv(fileWithPath);

                //directly write the JSON document to CSV but with delimiter
                flatMe.json2Sheet().write2csv(fileWithPath, '|');

                File downloadFile = new File(fileWithPath);
                OutputStream outStream;
                // if you want to use a relative path to context root:
                try (FileInputStream inStream = new FileInputStream(downloadFile)) {
                    // if you want to use a relative path to context root:
                    String relativePath = srv.getServletContext().getRealPath("");
                    System.out.println("relativePath = " + relativePath);
                    // obtains ServletContext
                    ServletContext context = srv.getServletContext();
                    // gets MIME type of the file
                    String mimeType = context.getMimeType(fileWithPath);
                    if (mimeType == null) {
                        // set to binary type if MIME mapping not found
                        mimeType = "application/octet-stream";
                    }
                    System.out.println("MIME type: " + mimeType);
                    // modifies response
                    response.setContentType(mimeType);
                    response.setContentLength((int) downloadFile.length());
                    // forces download
                    String headerKey = "Content-Disposition";
                    String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
                    response.setHeader(headerKey, headerValue);
                    // obtains response's output stream
                    outStream = response.getOutputStream();
                    byte[] buffer = new byte[4096];
                    int bytesRead = -1;
                    while ((bytesRead = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                }
                outStream.close();
            } catch (IOException ex) {
                Logger.getLogger(LPFrontEnd.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        servetInvokeResponseSuccessServlet(request, response);
    }

    /**
     * En mucho uso
     *
     * @param request
     * @param response
     * @param jsonArr
     */
    public static final void servletReturnSuccess(HttpServletRequest request, HttpServletResponse response, JSONArray jsonArr) {
        if (jsonArr == null) {
            request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(), "");
        } else {
            request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(), jsonArr.toString());
        }
        servetInvokeResponseSuccessServlet(request, response);
    }

    /**
     * En mucho uso
     *
     * @param request
     * @param response
     * @param lPFalseObject
     */
    public static final void servletReturnResponseErrorLPFalseDiagnostic(HttpServletRequest request, HttpServletResponse response, Object[] lPFalseObject) {
        JSONObject errJSONMsg = LPFrontEnd.responseJSONDiagnosticLPFalse(lPFalseObject);
        request.setAttribute(GlobalVariables.ServletsResponse.ERROR.getAttributeName(), errJSONMsg.toString());
        servetInvokeResponseErrorServlet(request, response);
    }

    /**
     * En mucho uso
     *
     * @param request
     * @param response
     * @param errorCode
     * @param msgVariables
     */
    public static final void servletReturnResponseErrorLPFalseDiagnosticBilingue(HttpServletRequest request, HttpServletResponse response, String errorCode, Object[] msgVariables) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[][] mainMessage = procReqSession.getMessages().getMainMessage();
        if (mainMessage != null && mainMessage.length > 0 && mainMessage[0].length >= 2) {
            errorCode = (String) mainMessage[0][0];
            msgVariables = (Object[]) mainMessage[0][1];
        }
        JSONObject errJSONMsg = LPFrontEnd.responseJSONDiagnosticLPFalse(errorCode, msgVariables);
        request.setAttribute(GlobalVariables.ServletsResponse.ERROR.getAttributeName(), errJSONMsg.toString());
        servetInvokeResponseErrorServlet(request, response);
    }

    public static JSONObject responseJSONDiagnosticLPFalse(String errorCode, Object[] msgVariables) {
        JSONObject errJsObj = new JSONObject();
        errJsObj.put(ResponseTags.DIAGNOSTIC.getLabelName(), LPPlatform.LAB_FALSE);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
        Object[][] mainMessage = messages.getMainMessage();
        Object[] errorMsgEn = null;
        Object[] errorMsgEs = null;
        String errorCodeStr = "";
        if (mainMessage != null && messages.getMainMessageCode() != null && mainMessage.length > 0 && mainMessage[0].length > 1) {
            errorMsgEn = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, messages.getMainMessageCode(), messages.getMainMessageVariables(), "en");
            errorMsgEs = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, messages.getMainMessageCode(), messages.getMainMessageVariables(), "es");
            errorCodeStr = messages.getMainMessageCode().getErrorCode();
        } else {
            errorMsgEn = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, errorCode, msgVariables, "en");
            errorMsgEs = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, errorCode, msgVariables, "es");
            errorCodeStr = errorCode;
        }
        String errorTextEn = errorMsgEn[errorMsgEn.length - 1].toString();
        String errorTextEs = errorMsgEs[errorMsgEs.length - 1].toString();
        errJsObj.put(ResponseTags.MESSAGE_CODE.getLabelName(), errorCodeStr);

        errJsObj.put(ResponseTags.MESSAGE.getLabelName() + "_es", errorTextEs);
        errJsObj.put(ResponseTags.MESSAGE.getLabelName() + "_en", errorTextEn);
        errJsObj.put(ResponseTags.IS_ERROR.getLabelName(), true);
        return errJsObj;
    }

    private static JSONObject responseJSONDiagnosticLPFalse(Object[] lpFalseStructure) {
        JSONObject errJsObj = new JSONObject();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
        Object[][] mainMessage = messages.getMainMessage();
        Object[] errorMsgEn = null;
        Object[] errorMsgEs = null;
        if (mainMessage != null && mainMessage.length > 0 && mainMessage[0].length > 1) {
            errorMsgEn = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, messages.getMainMessageCode(), messages.getMainMessageVariables(), "en");
            errorMsgEs = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, messages.getMainMessageCode(), messages.getMainMessageVariables(), "es");
            String errorTextEn = errorMsgEn[errorMsgEn.length - 1].toString();
            String errorTextEs = errorMsgEs[errorMsgEs.length - 1].toString();
            errJsObj.put(ResponseTags.MESSAGE_CODE.getLabelName(), messages.getMainMessageCode().getErrorCode());

            errJsObj.put(ResponseTags.MESSAGE.getLabelName() + "_en", errorTextEn);
            errJsObj.put(ResponseTags.MESSAGE.getLabelName() + "_es", errorTextEs);
        } else {
            errJsObj.put(ResponseTags.MESSAGE_CODE.getLabelName(), lpFalseStructure[lpFalseStructure.length - 2]);

            errJsObj.put(ResponseTags.MESSAGE.getLabelName() + "_es", lpFalseStructure[lpFalseStructure.length - 1]);
            errJsObj.put(ResponseTags.MESSAGE.getLabelName() + "_en", lpFalseStructure[lpFalseStructure.length - 1]);
        }

        errJsObj.put(ResponseTags.DIAGNOSTIC.getLabelName(), lpFalseStructure[0]);
        errJsObj.put(ResponseTags.IS_ERROR.getLabelName(), true);
        return errJsObj;
    }

    public static JSONObject responseJSONDiagnosticPositiveEndpoint(EnumIntEndpoints endpoint, Object[] msgDynamicValues, JSONArray relatedObjects) {

        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
        Object[][] mainMessage = messages.getMainMessage();
        Object[] errorMsgEn = null;
        Object[] errorMsgEs = null;
        String errorTextEn = "";
        String errorTextEs = "";
        String errorCode = "";
        if (mainMessage != null && mainMessage.length > 0 && mainMessage[0].length > 1 && !mainMessage[0][0].toString().toUpperCase().contains("NULL")) {
            Object[] msgArg3 = new Object[]{};
            if (mainMessage[0].length > 2) {
                msgArg3 = (Object[]) mainMessage[0][2];
            }
            errorCode = mainMessage[0][1].toString();
            errorMsgEn = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, mainMessage[0][1].toString(), msgArg3, "en", mainMessage[0], true);
            errorMsgEs = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, mainMessage[0][1].toString(), msgArg3, "es", mainMessage[0], false);
            errorTextEn = errorMsgEn[errorMsgEn.length - 1].toString();
            errorTextEs = errorMsgEs[errorMsgEs.length - 1].toString();
        } else {
            errorCode = endpoint.getSuccessMessageCode();
            errorTextEn = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_API_SUCCESSMESSAGE + endpoint.getClass().getSimpleName(), null, endpoint.getSuccessMessageCode(), "en", null, true, endpoint.getClass().getSimpleName());
            errorTextEs = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_API_SUCCESSMESSAGE + endpoint.getClass().getSimpleName(), null, endpoint.getSuccessMessageCode(), "es", null, false, endpoint.getClass().getSimpleName());
            if (endpoint != null) {
                if (msgDynamicValues != null) {
                    for (int iVarValue = 1; iVarValue <= msgDynamicValues.length; iVarValue++) {
                        errorTextEn = errorTextEn.replace("<*" + iVarValue + "*>", LPNulls.replaceNull(msgDynamicValues[iVarValue - 1]).toString());
                        errorTextEs = errorTextEs.replace("<*" + iVarValue + "*>", LPNulls.replaceNull(msgDynamicValues[iVarValue - 1]).toString());
                    }
                }
                if (errorTextEn.length() == 0) {
                    errorTextEn = endpoint + " (*** This MessageCode, " + endpoint + ", has no entry defined in messages property file) ";
                    if (msgDynamicValues != null) {
                        errorTextEn = errorTextEn + Arrays.toString(msgDynamicValues);
                    }
                }
                if (errorTextEs.length() == 0) {
                    errorTextEs = endpoint + " (*** Este CódigoDeMensaje, " + endpoint + ", no está definido en los archivos de mensajes) ";
                    if (msgDynamicValues != null) {
                        errorTextEs = errorTextEs + Arrays.toString(msgDynamicValues);
                    }
                }
            }
        }
        JSONObject errJsObj = new JSONObject();
        errJsObj.put(ResponseTags.DIAGNOSTIC.getLabelName(), LPPlatform.LAB_TRUE);
        errJsObj.put(ResponseTags.CATEGORY.getLabelName(), endpoint.getClass().getSimpleName().toUpperCase().replace("API", ""));
        errJsObj.put(ResponseTags.MESSAGE_CODE.getLabelName(), errorCode);
        errJsObj.put(ResponseTags.MESSAGE.getLabelName() + "_es", errorTextEs);
        errJsObj.put(ResponseTags.MESSAGE.getLabelName() + "_en", errorTextEn);
        errJsObj.put(ResponseTags.RELATED_OBJECTS.getLabelName(), relatedObjects);
        errJsObj.put(ResponseTags.IS_ERROR.getLabelName(), false);
        return errJsObj;
    }

    public static JSONObject responseJSONDiagnosticLPFalse(EnumIntMessages errorCode, Object[] msgVariables) {
        JSONObject errJsObj = new JSONObject();
        errJsObj.put(ResponseTags.DIAGNOSTIC.getLabelName(), LPPlatform.LAB_FALSE);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
        Object[][] mainMessage = messages.getMainMessage();
        Object[] errorMsgEn = null;
        Object[] errorMsgEs = null;
        String errorCodeStr = "";
        if (mainMessage != null && mainMessage.length > 0 && mainMessage[0].length > 1) {
            errorCodeStr = messages.getMainMessageCode().getErrorCode();
            errorMsgEn = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, messages.getMainMessageCode(), messages.getMainMessageVariables(), "en");
            errorMsgEs = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, messages.getMainMessageCode(), messages.getMainMessageVariables(), "es");
        } else {
            errorCodeStr = errorCode.getErrorCode();
            errorMsgEn = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, errorCode, msgVariables, "en");
            errorMsgEs = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, errorCode, msgVariables, "es");
        }
        String errorTextEn = errorMsgEn[errorMsgEn.length - 1].toString();
        String errorTextEs = errorMsgEs[errorMsgEs.length - 1].toString();
        errJsObj.put(ResponseTags.MESSAGE_CODE.getLabelName(), errorCodeStr);
        errJsObj.put(ResponseTags.MESSAGE.getLabelName() + "_es", errorTextEs);
        errJsObj.put(ResponseTags.MESSAGE.getLabelName() + "_en", errorTextEn);
        errJsObj.put(ResponseTags.IS_ERROR.getLabelName(), true);
        return errJsObj;
    }

    public static final void servletReturnResponseErrorLPFalseDiagnosticBilingue(HttpServletRequest request, HttpServletResponse response, EnumIntMessages errorCode, Object[] msgVariables) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages mainMessage = procReqSession.getMessages();
        Object[][] mainMessageObj = mainMessage.getMainMessage();
        if (mainMessageObj != null && mainMessageObj.length > 0 && mainMessageObj[0].length >= 2) {
            errorCode = mainMessage.getMainMessageCode();
            msgVariables = mainMessage.getMainMessageVariables();
        }
        JSONObject errJSONMsg = LPFrontEnd.responseJSONDiagnosticLPFalse(errorCode, msgVariables);
        request.setAttribute(GlobalVariables.ServletsResponse.ERROR.getAttributeName(), errJSONMsg.toString());
        servetInvokeResponseErrorServlet(request, response);
    }

}
