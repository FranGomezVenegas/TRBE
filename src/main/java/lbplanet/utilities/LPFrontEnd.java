/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import com.github.opendevl.JFlat;
import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import databases.Token;
import functionaljavaa.parameter.Parameter;
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
import trazit.globalvariables.GlobalVariables;
import static trazit.globalvariables.GlobalVariables.LANGUAGE_ALL_LANGUAGES;

/**
 *
 * @author Administrator
 */
public class LPFrontEnd {
    public static final String ERROR_TRAPPING_TABLE_NO_RECORDS="tableWithNoRecords";

    public enum ResponseTags{
        DIAGNOSTIC("diagnostic"), CATEGORY("category"), MESSAGE("message"), RELATED_OBJECTS("related_objects"), IS_ERROR("is_error");
        private ResponseTags(String labelName){
            this.labelName=labelName;            
        }    
        public String getLabelName(){
            return this.labelName;
        }           
        private final String labelName;
    }

    private LPFrontEnd(){    throw new IllegalStateException("Utility class");}    

    /**
     *
     * @param request
     * @return
     */
    public static String setLanguage(HttpServletRequest request){
        String language = request.getParameter(LPPlatform.REQUEST_PARAM_LANGUAGE);
        if (language == null)language = LANGUAGE_ALL_LANGUAGES;
        return language;
    }
    
    /**
     *
     * @param request
     * @param response
     * @return
     */
    public static final Boolean servletStablishDBConection(HttpServletRequest request, HttpServletResponse response){
        String dbName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_DB_NAME);

        boolean isConnected = false;    
        if (dbName==null || dbName.length()==0){
            String theToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_MY_TOKEN);
            if (theToken==null || theToken.length()==0)
                theToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            if (theToken==null || theToken.length()==0)
                isConnected = Rdbms.getRdbms().startRdbms();      
            else{
                Token token = new Token(theToken);  
                dbName=token.getDbName();
                if (dbName==null || dbName.length()==0)
                    isConnected = Rdbms.getRdbms().startRdbms();      
                else
                isConnected = Rdbms.getRdbms().startRdbms(dbName);      
            }
        }
        else
            isConnected = Rdbms.getRdbms().startRdbms(dbName);      
        if (!isConnected){      
            LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.ApiErrorTraping.PROPERTY_DATABASE_NOT_CONNECTED.getName(), null, null);                                                                
        }  
        return isConnected;
    }

    /**
     *
     * @param request
     * @param response
     * @param dbUserName
     * @param dbUserPassword
     * @return
     */
    public static final Boolean servletUserToVerify(HttpServletRequest request, HttpServletResponse response, String dbUserName, String dbUserPassword){    
        String userToVerify = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK); 
            if (userToVerify==null) userToVerify=LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK)).toString();
        String passwordToVerify = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK);    
            if (passwordToVerify==null) passwordToVerify=LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK)).toString();
        if ( (!userToVerify.equalsIgnoreCase(dbUserName)) || (!passwordToVerify.equalsIgnoreCase(dbUserPassword)) ){
            servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.INVALID_USER_VERIFICATION.getName(), null, null);           
            return false;                                
        }            
        return true;
    }

    /**
     *
     * @param request
     * @param response
     * @param eSign
     * @return
     */
    public static final Boolean servletEsignToVerify(HttpServletRequest request, HttpServletResponse response, String eSign){    
        String eSignToVerify = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ESIGN_TO_CHECK);      
            if (eSignToVerify==null) eSignToVerify=LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_ESIGN_TO_CHECK)).toString();
        if (!eSignToVerify.equalsIgnoreCase(eSign)) {  
            servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.INVALID_ESIGN.getName(), null, null);           
            return false;                                
        }            
        return true;
    }
    /**
     *
     * @param errorStructure
     * @return
     */
    public static Object[] responseError(Object[] errorStructure){
        Object[] responseObj = new Object[0];
        responseObj = LPArray.addValueToArray1D(responseObj, HttpServletResponse.SC_UNAUTHORIZED);
        responseObj = LPArray.addValueToArray1D(responseObj, errorStructure[errorStructure.length-1].toString());        
        return responseObj;
    }

    /**
     *
     * @param lpFalseStructure
     * @return
     */
    public static JSONObject responseJSONDiagnosticLPFalse(Object[] lpFalseStructure){
        JSONObject errJsObj = new JSONObject();
        errJsObj.put(ResponseTags.DIAGNOSTIC.getLabelName(), lpFalseStructure[0]);
        errJsObj.put(ResponseTags.MESSAGE.getLabelName()+"_es", lpFalseStructure[lpFalseStructure.length-1]);
        errJsObj.put(ResponseTags.MESSAGE.getLabelName()+"_en", lpFalseStructure[lpFalseStructure.length-1]);
        errJsObj.put(ResponseTags.IS_ERROR.getLabelName(), true);
        return errJsObj;
    }
    public static JSONObject responseJSONDiagnosticLPFalse(String errorCode, Object[] msgVariables){
        JSONObject errJsObj = new JSONObject();
        errJsObj.put(ResponseTags.DIAGNOSTIC.getLabelName(), LPPlatform.LAB_FALSE);
        Object [] errorMsgEn=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, errorCode, msgVariables, "en");
        Object [] errorMsgEs=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, errorCode, msgVariables, "es");
        String errorTextEn = errorMsgEn[errorMsgEn.length-1].toString(); 
        String errorTextEs = errorMsgEs[errorMsgEs.length-1].toString(); 
        errJsObj.put(ResponseTags.MESSAGE.getLabelName()+"_es", errorTextEs);
        errJsObj.put(ResponseTags.MESSAGE.getLabelName()+"_en", errorTextEn);
        errJsObj.put(ResponseTags.IS_ERROR.getLabelName(), true);
        return errJsObj;
    }
    

    /**
     *
     * @param lpTrueStructure
     * @return
     */
    public static JSONObject responseJSONDiagnosticLPTrue(Object[] lpTrueStructure){
        JSONObject errJsObj = new JSONObject();
        errJsObj.put(ResponseTags.DIAGNOSTIC.getLabelName(), lpTrueStructure[0]);
        errJsObj.put(ResponseTags.MESSAGE.getLabelName()+"_es", lpTrueStructure[lpTrueStructure.length-1]);
        errJsObj.put(ResponseTags.MESSAGE.getLabelName()+"_en", lpTrueStructure[lpTrueStructure.length-1]);
        errJsObj.put(ResponseTags.IS_ERROR.getLabelName(), false);
        return errJsObj;
    }    

    /**
     *
     * @param apiName
     * @param msgCode
     * @param msgDynamicValues
     * @param relatedObjects
     * @return
     */
    public static JSONObject responseJSONDiagnosticLPTrue(String apiName, String msgCode, Object[] msgDynamicValues, JSONArray relatedObjects){
        String errorTextEn = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_API_SUCCESSMESSAGE+apiName, null, msgCode, "en");
        String errorTextEs = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_API_SUCCESSMESSAGE+apiName, null, msgCode, "es");
        if (msgCode!=null){
            for (int iVarValue=1; iVarValue<=msgDynamicValues.length; iVarValue++){
                errorTextEn = errorTextEn.replace("<*"+iVarValue+"*>", msgDynamicValues[iVarValue-1].toString());
                errorTextEs = errorTextEs.replace("<*"+iVarValue+"*>", msgDynamicValues[iVarValue-1].toString());
            }        
            if (errorTextEn.length()==0){
                errorTextEn=msgCode+ " (*** This MessageCode, "+msgCode+", has no entry defined in messages property file) ";
                if (msgDynamicValues!=null)
                    errorTextEn=errorTextEn+Arrays.toString(msgDynamicValues);
            }
            if (errorTextEs.length()==0){
                errorTextEs=msgCode+ " (*** Este CódigoDeMensaje, "+msgCode+", no está definido en los archivos de mensajes) ";
                if (msgDynamicValues!=null)
                    errorTextEs=errorTextEs+Arrays.toString(msgDynamicValues);
            }
        }      
        JSONObject errJsObj = new JSONObject();
        errJsObj.put(ResponseTags.DIAGNOSTIC.getLabelName(), LPPlatform.LAB_TRUE);
        errJsObj.put(ResponseTags.CATEGORY.getLabelName(), apiName.toUpperCase().replace("API", ""));
        errJsObj.put(ResponseTags.MESSAGE.getLabelName()+"_es", errorTextEs);
        errJsObj.put(ResponseTags.MESSAGE.getLabelName()+"_en", errorTextEn);
        errJsObj.put(ResponseTags.RELATED_OBJECTS.getLabelName(), relatedObjects);        
        errJsObj.put(ResponseTags.IS_ERROR.getLabelName(), false);
        return errJsObj;
    }    
    
    /**
     *
     * @param errorPropertyName
     * @param errorPropertyValue
     * @return
     */
    public static JSONObject responseJSONError(String errorPropertyName, Object[] errorPropertyValue){
        JSONObject errJsObj = new JSONObject();
        errJsObj.put(ResponseTags.MESSAGE.getLabelName(), errorPropertyName);
        String errorTextEn = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_ERRORTRAPING, null, errorPropertyName+"_detail", null);
        if (errorPropertyValue!=null){
            for (int iVarValue=1; iVarValue<=errorPropertyValue.length; iVarValue++){
                errorTextEn = errorTextEn.replace("<*"+iVarValue+"*>", errorPropertyValue[iVarValue-1].toString());
            }        
        }
        errJsObj.put(ResponseTags.MESSAGE.getLabelName()+"_en", errorTextEn);
        String errorTextEs = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_ERRORTRAPING, null, errorPropertyName+"_detail", "es");
        if (errorPropertyValue!=null){
            for (int iVarValue=1; iVarValue<=errorPropertyValue.length; iVarValue++){
                errorTextEs = errorTextEs.replace("<*"+iVarValue+"*>", errorPropertyValue[iVarValue-1].toString());
            }         
        }
        errJsObj.put(ResponseTags.MESSAGE.getLabelName()+"_es", errorTextEs);
        errJsObj.put(ResponseTags.DIAGNOSTIC.getLabelName(), LPPlatform.LAB_FALSE); 
        errJsObj.put(ResponseTags.IS_ERROR.getLabelName(), true);
        return errJsObj;
    }
    /**
     *
     * @param errorStructure
     * @param language
     * @param schemaPrefix
     * @return
     */
    public static Object[] responseError(Object[] errorStructure, String language, String schemaPrefix){
        Object[] responseObj = new Object[0];
        responseObj = LPArray.addValueToArray1D(responseObj, HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);
        if (errorStructure.length>0){
            responseObj = LPArray.addValueToArray1D(responseObj, errorStructure[errorStructure.length-1].toString());        
        }else{
            responseObj = LPArray.addValueToArray1D(responseObj, Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName());        
        }
        return responseObj;
    }
    private static final int CLIENT_CODE_STACK_INDEX;    
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
  
    
    private static void servetInvokeResponseErrorServlet(HttpServletRequest request, HttpServletResponse response){
        Rdbms.closeRdbms();      
        RequestDispatcher rd = request.getRequestDispatcher(GlobalVariables.ServletsResponse.ERROR.getServletName());
        try {   
            rd.forward(request,response);
        } catch (ServletException | IOException ex) {
            Logger.getLogger(LPFrontEnd.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private static void servetInvokeResponseSuccessServlet(HttpServletRequest request, HttpServletResponse response){
        Rdbms.closeRdbms();      
        
        RequestDispatcher rd = request.getRequestDispatcher(GlobalVariables.ServletsResponse.SUCCESS.getServletName());
        try {           
            rd.forward(request,response);
        } catch (IOException | ServletException ex) {
            Logger.getLogger(LPFrontEnd.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param request
     * @param response
     * @param errorCode
     * @param errorCodeVars
     * @param language
     */
    public static final void servletReturnResponseError(HttpServletRequest request, HttpServletResponse response, String errorCode, Object[] errorCodeVars, String language){  
        JSONObject errJSONMsg = LPFrontEnd.responseJSONError(errorCode,errorCodeVars);
        request.setAttribute(GlobalVariables.ServletsResponse.ERROR.getAttributeName(), errJSONMsg.toString());
        servetInvokeResponseErrorServlet(request, response);
    }

    /**
     *
     * @param request
     * @param response
     */
    public static final void servletReturnSuccess(HttpServletRequest request, HttpServletResponse response){  
        request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(),"");
        servetInvokeResponseSuccessServlet(request, response);
    }    

    /**
     *
     * @param request
     * @param response
     * @param myStr
     */
    public static final void servletReturnSuccess(HttpServletRequest request, HttpServletResponse response, String myStr){  
        if (myStr==null){request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(),"");}
        else{request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(), myStr);}
        servetInvokeResponseSuccessServlet(request, response);
    }       

    /**
     *
     * @param request
     * @param response
     * @param jsonObj
     */
    public static final void servletReturnSuccess(HttpServletRequest request, HttpServletResponse response, JSONObject jsonObj){  
        if (jsonObj==null){request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(),"");}
        else{request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(), jsonObj.toString());}
        servetInvokeResponseSuccessServlet(request, response);
    }   

    public static final void servletReturnSuccessFile(HttpServletRequest request, HttpServletResponse response, JSONObject jsonObj, HttpServlet srv, String filePath, String fileName){  
        if (jsonObj==null){request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(),"");}
        else{
            try { 
                if (filePath==null || filePath.length()==0) filePath="D:\\\\LP\\\\";
                if (fileName==null || fileName.length()==0) fileName="mycsv.csv";
                String fileWithPath=filePath+fileName; //"D:\\\\LP\\\\mycsv.csv";
                String str = jsonObj.toJSONString(); //new String(Files.readAllBytes(Paths.get(fileWithPath)));

                JFlat flatMe = new JFlat(str);

                //directly write the JSON document to CSV
                flatMe.json2Sheet().write2csv(fileWithPath);

                //directly write the JSON document to CSV but with delimiter
                flatMe.json2Sheet().write2csv(fileWithPath, '|');

                //String fileWithPath = "E:/Test/Download/MYPIC.JPG";
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
                    }   System.out.println("MIME type: " + mimeType);
                    // modifies response
                    response.setContentType(mimeType);
                    response.setContentLength((int) downloadFile.length());
                    // forces download
                    String headerKey = "Content-Disposition";
                    String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
                    response.setHeader(headerKey, headerValue);
                    //response.getContext().responseComplete();
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
     *
     * @param request
     * @param response
     * @param jsonArr
     */
    public static final void servletReturnSuccess(HttpServletRequest request, HttpServletResponse response, JSONArray jsonArr){  
        if (jsonArr==null){request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(),"");}
        else{request.setAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName(), jsonArr.toString());}
        servetInvokeResponseSuccessServlet(request, response);
    }  
    
    /**
     *
     * @param request
     * @param response
     * @param lPFalseObject
     */
    public static final void servletReturnResponseErrorLPFalseDiagnostic(HttpServletRequest request, HttpServletResponse response, Object[] lPFalseObject){       
        JSONObject errJSONMsg = LPFrontEnd.responseJSONDiagnosticLPFalse(lPFalseObject);
        request.setAttribute(GlobalVariables.ServletsResponse.ERROR.getAttributeName(), errJSONMsg.toString());        
        servetInvokeResponseErrorServlet(request, response);
    }    
    public static final void servletReturnResponseErrorLPFalseDiagnosticBilingue(HttpServletRequest request, HttpServletResponse response, String errorCode, Object[] msgVariables){       
        JSONObject errJSONMsg = LPFrontEnd.responseJSONDiagnosticLPFalse(errorCode, msgVariables);
        request.setAttribute(GlobalVariables.ServletsResponse.ERROR.getAttributeName(), errJSONMsg.toString());          
        servetInvokeResponseErrorServlet(request, response);
    }    
    /**
     *
     * @param request
     * @param response
     * @param lPTrueObject
     */
    public static final void servletReturnResponseErrorLPTrueDiagnostic(HttpServletRequest request, HttpServletResponse response, Object[] lPTrueObject){       
        JSONObject successJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(lPTrueObject);
        request.setAttribute(GlobalVariables.ServletsResponse.ERROR.getAttributeName(), successJSONMsg.toString());        
        servetInvokeResponseErrorServlet(request, response);
    }  
    public static final JSONObject noRecordsInTableMessage(){
        return LPFrontEnd.responseJSONDiagnosticLPFalse(ERROR_TRAPPING_TABLE_NO_RECORDS, new Object[0]);
    }
}
