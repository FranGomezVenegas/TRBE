/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.session;

import functionaljavaa.parameter.Parameter;
import java.util.Arrays;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class ApiMessageReturn {
    public static final String JAVADOC_CLASS_FLDNAME = "class";
    public static final String JAVADOC_METHOD_FLDNAME = "method";
    public static final String JAVADOC_LINE_FLDNAME = "line";
    
    private static final String JSON_TAG_ERR_MSG_EVALUATION = "evaluation";
    private static final String JSON_TAG_ERR_MSG_CLSS = JAVADOC_CLASS_FLDNAME;
    private static final String JSON_TAG_ERR_MSG_CLSS_VERSION = "classVersion";
    private static final String JSON_TAG_ERR_MSG_CLSS_LINE = "line";
    private static final String JSON_TAG_ERR_MSG_CLSS_ERR_CODE = "errorCode";
    private static final String JSON_TAG_ERR_MSG_CLSS_ERR_CODE_TEXT = "errorCodeText";
    private static final String JSON_TAG_ERR_MSG_CLSS_ERR_DETAIL = "errorDetail";
    public static final String CONFIG_OTRONOMBRE_FILE_NAME = "-otronombre";
    private static final int CLIENT_CODE_STACK_INDEX;
    
    private static final String javaExtension=".java";
    
    static{
        int i = 0;
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()){
            i++;
            if (ste.getClassName().equals(LPPlatform.class.getName())){
                break;
            }
        }
        CLIENT_CODE_STACK_INDEX = i;
    }
    public static Object[] trapMessage(String evaluation, EnumIntEndpoints endpoint, Object[] msgVariables) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int length = Thread.currentThread().getStackTrace().length;
        if (length>CLIENT_CODE_STACK_INDEX) length=CLIENT_CODE_STACK_INDEX;
        else length=3;
        String className = Thread.currentThread().getStackTrace()[length-1].getFileName();
        String classFullName = Thread.currentThread().getStackTrace()[length-1].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[length-1].getMethodName();
        Integer lineNumber = Thread.currentThread().getStackTrace()[length-1].getLineNumber();
        className = endpoint.getClass().getSimpleName(); //className.replace(javaExtension, "");
        Object[] callerInfo = new Object[]{className, classFullName, methodName, lineNumber};
        
        return trapMessage(evaluation, endpoint.getSuccessMessageCode(), msgVariables, null, callerInfo, false);
    }
    public static Object[] trapMessage(String evaluation, EnumIntMessages endpoint, Object[] msgVariables, String language) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int length = Thread.currentThread().getStackTrace().length;
        if (length>CLIENT_CODE_STACK_INDEX) length=CLIENT_CODE_STACK_INDEX;
        else length=3;
        String className = Thread.currentThread().getStackTrace()[length-1].getFileName();
        String classFullName = Thread.currentThread().getStackTrace()[length-1].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[length-1].getMethodName();
        Integer lineNumber = Thread.currentThread().getStackTrace()[length-1].getLineNumber();
        className = endpoint.getClass().getSimpleName(); //className.replace(javaExtension, "");
        Object[] callerInfo = new Object[]{className, classFullName, methodName, lineNumber};
        
        return trapMessage(evaluation, endpoint.getErrorCode(), msgVariables, language, callerInfo, false);
    }
    public static Object[] trapMessage(String evaluation, EnumIntMessages endpoint, Object[] msgVariables) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int length = Thread.currentThread().getStackTrace().length;
        if (length>CLIENT_CODE_STACK_INDEX) length=CLIENT_CODE_STACK_INDEX;
        else length=3;
        String className = Thread.currentThread().getStackTrace()[length-1].getFileName();
        String classFullName = Thread.currentThread().getStackTrace()[length-1].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[length-1].getMethodName();
        Integer lineNumber = Thread.currentThread().getStackTrace()[length-1].getLineNumber();
        className = endpoint.getClass().getSimpleName(); //className.replace(javaExtension, "");
        Object[] callerInfo = new Object[]{className, classFullName, methodName, lineNumber};
        
        return trapMessage(evaluation, endpoint.getErrorCode(), msgVariables, null, callerInfo, false);
    }

    
    public static Object[] trapMessage(String evaluation, String msgCode, Object[] msgVariables) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int length = Thread.currentThread().getStackTrace().length;
        if (length>CLIENT_CODE_STACK_INDEX) length=CLIENT_CODE_STACK_INDEX;
        else length=3;
        String className = Thread.currentThread().getStackTrace()[length-1].getFileName();
        String classFullName = Thread.currentThread().getStackTrace()[length-1].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[length-1].getMethodName();
        Integer lineNumber = Thread.currentThread().getStackTrace()[length-1].getLineNumber();
        className = className.replace(javaExtension, "");
        Object[] callerInfo = new Object[]{className, classFullName, methodName, lineNumber};
        return trapMessage(evaluation, msgCode, msgVariables, null, callerInfo, false);
    }
    
    public static Object[] trapMessage(String evaluation, String msgCode, Object[] msgVariables, Boolean isOptional) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int length = Thread.currentThread().getStackTrace().length;
        if (length>CLIENT_CODE_STACK_INDEX) length=CLIENT_CODE_STACK_INDEX;
        else length=3;
        String className = Thread.currentThread().getStackTrace()[length-1].getFileName();
        String classFullName = Thread.currentThread().getStackTrace()[length-1].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[length-1].getMethodName();
        Integer lineNumber = Thread.currentThread().getStackTrace()[length-1].getLineNumber();
        className = className.replace(javaExtension, "");
        Object[] callerInfo = new Object[]{className, classFullName, methodName, lineNumber};
        return trapMessage(evaluation, msgCode, msgVariables, null, callerInfo, isOptional);
    }

    public static Object[] trapMessage(String evaluation, String msgCode, Object[] msgVariables, String language) {
        return trapMessage(null, evaluation, msgCode, msgVariables, language);
    }

    public static Object[] trapMessage(String className, String evaluation, String msgCode, Object[] msgVariables, String language) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int length = Thread.currentThread().getStackTrace().length;
        if (length>CLIENT_CODE_STACK_INDEX) length=CLIENT_CODE_STACK_INDEX;
        else length=3;
        if (className == null) {
            className = Thread.currentThread().getStackTrace()[length-1].getFileName();
        }
        String classFullName = Thread.currentThread().getStackTrace()[length-1].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[length-1].getMethodName();
        Integer lineNumber = Thread.currentThread().getStackTrace()[length-1].getLineNumber();
        className = className.replace(javaExtension, "");
        Object[] callerInfo = new Object[]{className, classFullName, methodName, lineNumber};
        return trapMessage(evaluation, msgCode, msgVariables, language, callerInfo, false);
    }

    public static Object[] trapMessage(String evaluation, String msgCode, Object[] msgVariables, String language, Object[] callerInfo, Boolean isOptional) {
        if (callerInfo == null) {
            String className = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName();
            String classFullName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName();
            String methodName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName();
            Integer lineNumber = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber();
            className = className.replace(javaExtension, "");
            callerInfo = new Object[]{className, classFullName, methodName, lineNumber};
        }
        String errorDetail = "";
        Object[] fldValue = new Object[7];
        Boolean errorCodeFromBundle = true;
        String errorCodeText = "";
        String className = callerInfo[0].toString();
        String classFullName = callerInfo[1].toString();
        String methodName = callerInfo[2].toString();
        Integer lineNumber = -999;
        if (callerInfo.length > 3) {
            lineNumber = Integer.valueOf(callerInfo[3].toString());
        }
        String propertiesFilePrefix = "";
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(evaluation)) {
            propertiesFilePrefix = LPPlatform.CONFIG_FILES_API_SUCCESSMESSAGE + className;
        } else {
            propertiesFilePrefix = LPPlatform.CONFIG_FILES_ERRORTRAPING;
        }
        if (language == null) {
            language = GlobalVariables.DEFAULTLANGUAGE;
        }
        errorCodeText = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, propertiesFilePrefix, null, className + "_" + msgCode, language, callerInfo, isOptional, className);
        if (errorCodeText.length() == 0) {
            errorCodeText = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, propertiesFilePrefix, null, msgCode, language, callerInfo, isOptional, className);
        }
        if (errorCodeText.length() == 0) {
            errorCodeText = msgCode;
            errorCodeFromBundle = false;
        }
        if (!errorCodeFromBundle) {
            errorDetail = errorCodeText + " (*** This errorCode has no entry defined in messages property file, class=" + className + " msgCode=" + msgCode + ") ";
            if (msgVariables != null) {
                errorDetail = errorDetail + Arrays.toString(msgVariables);
            }
            if ((msgVariables != null) && msgVariables.length > 0) {
                for (int iVarValue = 1; iVarValue <= msgVariables.length; iVarValue++) {
                    errorDetail = errorDetail.replace("<*" + iVarValue + "*>", LPNulls.replaceNull(msgVariables[iVarValue - 1]).toString());
                }
            }
        } else {
            errorDetail = errorCodeText;
            //errorDetail = Parameter.getMessageCodeValue(CONFIG_FILES_FOLDER, CONFIG_FILES_ERRORTRAPING, null, className+"_"+msgCode, language, callerInfo, false);
            if (errorDetail.length() == 0) {
                errorDetail = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_ERRORTRAPING, null, msgCode, language, callerInfo, true, className);
            }
            if (errorDetail == null || errorDetail.length() == 0) {
                if (msgVariables.length > 0) {
                    errorDetail = msgVariables[0].toString();
                } else {
                    errorDetail = "";
                }
            } else {
                if (msgVariables != null) {
                    for (int iVarValue = 1; iVarValue <= msgVariables.length; iVarValue++) {
                        errorDetail = errorDetail.replace("<*" + iVarValue + "*>", LPNulls.replaceNull(msgVariables[iVarValue - 1]).toString());
                    }
                }
            }
        }
        fldValue[LPPlatform.TRAP_MESSAGE_EVALUATION_POSIC] = evaluation;
        fldValue[1] = classFullName + "." + methodName;
        fldValue[2] = "-999";
        fldValue[3] = "Code line " + lineNumber.toString();
        fldValue[LPPlatform.TRAP_MESSAGE_CODE_POSIC] = msgCode;
        fldValue[5] = errorCodeText;
        fldValue[LPPlatform.TRAP_MESSAGE_MESSAGE_POSIC] = errorDetail;
        return fldValue;
    }

    public static JSONObject trapErrorMessageJSON(Object[] errorArray) {
        JSONObject errorJson = new JSONObject();
        errorJson.put(JSON_TAG_ERR_MSG_EVALUATION, errorArray[0]);
        errorJson.put(JSON_TAG_ERR_MSG_CLSS, errorArray[1]);
        errorJson.put(JSON_TAG_ERR_MSG_CLSS_VERSION, errorArray[2]);
        errorJson.put(JSON_TAG_ERR_MSG_CLSS_LINE, errorArray[3]);
        errorJson.put(JSON_TAG_ERR_MSG_CLSS_ERR_CODE, errorArray[4]);
        errorJson.put(JSON_TAG_ERR_MSG_CLSS_ERR_CODE_TEXT, errorArray[5]);
        errorJson.put(JSON_TAG_ERR_MSG_CLSS_ERR_DETAIL, errorArray[6]);
        return errorJson;
    }
/**
 * Get Class Method Name dynamically for the method that call this method.
 * @return String - Class method name
 */
    public static String getClassMethodName(){
        return Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName();
    }
    
/*    private static final int CLIENT_CODE_STACK_INDEX;
    
    static{
        int i = 0;
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()){
            i++;
            if (ste.getClassName().equals(LPPlatform.class.getName())){
                break;
            }
        }
        CLIENT_CODE_STACK_INDEX = i;
    }
*/    
}
