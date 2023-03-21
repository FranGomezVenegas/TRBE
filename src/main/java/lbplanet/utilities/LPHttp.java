/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;



/**
 *
 * @author Administrator
 */
public class LPHttp {
    private LPHttp(){    throw new IllegalStateException("Utility class");}    


    /**
     *
     * @param request
     * @return
     */
    
    public static HttpServletRequest requestPreparation(HttpServletRequest request){
        try {            
            request.setCharacterEncoding(LPPlatform.LAB_ENCODER_UTF8);    
            return request;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LPHttp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return request;
    }

    /**
     *
     * @param response
     * @return
     */
    public static HttpServletResponse responsePreparation(HttpServletResponse response){
        response.setContentType("application/json");
        response.setCharacterEncoding(LPPlatform.LAB_ENCODER_UTF8);
        
        
        
        response.setHeader("CORS_ORIGIN_ALLOW_ALL", "True");                
        response.setHeader("CORS_ALLOW_CREDENTIALS", "true");                 //False
        response.setHeader("Access-Control-Allow-Methods", "GET");        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "True"); 
        response.setHeader("Set-Cookie", "key=value; HttpOnly; SameSite=strict");
        addSameSiteCookieAttribute(response); 
        return response;
    }    
    private static void addSameSiteCookieAttribute(HttpServletResponse response) {
        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        boolean firstHeader = true;
        for (String header : headers) { // there can be multiple Set-Cookie attributes
            if (firstHeader) {
                response.setHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=Strict"));
                firstHeader = false;
                continue;
            }
            response.addHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=Strict"));
        }
    }    

    /**
     *
     * @param request
     * @param paramNames
     * @return
     */
    public static Object[] areMandatoryParamsInApiRequest(HttpServletRequest request, String[] paramNames){        
        Object [] diagnoses = null;        
        StringBuilder paramsNotPresent = new StringBuilder(0); 
        if ( (paramNames!=null) && (paramNames.length>1 || (paramNames.length==1 && (!"".equals(paramNames[0])))) ){
            for (String curParam: paramNames){
                Boolean notPresent = false;
                String curParamValue = request.getParameter(curParam);
                if (curParamValue==null)curParamValue = LPNulls.replaceNull(request.getAttribute(curParam)).toString();
                if (curParamValue==null){notPresent=true;}
                if ("undefined".equals(curParamValue)){notPresent=true;}
                if ("".equals(curParamValue)){notPresent=true;}
                if (notPresent){
                    paramsNotPresent.append(curParam).append(", ");
                }
            }
        }
        if (paramsNotPresent.length()>0){
            diagnoses = LPArray.addValueToArray1D(diagnoses, LPPlatform.LAB_FALSE);
            diagnoses = LPArray.addValueToArray1D(diagnoses, paramsNotPresent);
            return diagnoses;
        }else{
            return new Object[]{LPPlatform.LAB_TRUE};           
        }
    }
    
    /**
     *
     * @param request
     * @param paramNames
     * @return
     */
    public static Object[] areEndPointMandatoryParamsInApiRequest(HttpServletRequest request, LPAPIArguments[] paramNames){        
        Object [] diagnoses = null;        
        StringBuilder paramsNotPresent = new StringBuilder(0); 
        if ( (paramNames!=null) && (paramNames.length>1 || (paramNames.length==1 && (!"".equals(paramNames[0].toString())))) ){
            for (LPAPIArguments curParam: paramNames){
                if (curParam.getMandatory()){
                    Boolean notPresent = false;
                    String curParamValue = request.getParameter(curParam.getName());
                    if (curParamValue==null){notPresent=true;}
                    if ("undefined".equals(curParamValue)){notPresent=true;}
                    if ("".equals(curParamValue)){notPresent=true;}
                    if (notPresent)
                        paramsNotPresent.append(curParam.getName()).append(", ");
                }
            }
        }
        if (paramsNotPresent.length()>0){
            diagnoses = LPArray.addValueToArray1D(diagnoses, LPPlatform.LAB_FALSE);
            diagnoses = LPArray.addValueToArray1D(diagnoses, paramsNotPresent);
            return diagnoses;
        }else{
            return new Object[]{LPPlatform.LAB_TRUE};           
        }
    }
    
    /**
     *
     */
    public static void sendResponseMissingMandatories(){
        // Not implemented yet
    }
}
