/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.TblsAppConfig;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import databases.Token;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONObject;

/**
 *
 * @author Administrator
 */
public class AppHeaderAPI extends HttpServlet {
    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN;
    public static final String MANDATORY_PARAMS_FRONTEND_GETAPPHEADER_PERSONFIELDSNAME_DEFAULT_VALUE="first_name|last_name|photo";
    public enum AppHeaderAPIfrontendEndpoints{
        /**
         *
         */
        GETAPPHEADER("GETAPPHEADER", "",new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PERSON_FIELDS_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),}),
        ;
        private AppHeaderAPIfrontendEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){                
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
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
        private final LPAPIArguments[] arguments;
    }
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {
        
        response=LPHttp.responsePreparation(response);
        
        request=LPHttp.requestPreparation(request);
        String language = LPFrontEnd.setLanguage(request); 
   
        try (PrintWriter out = response.getWriter()) {            

            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                return;          
            }                  
            String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);

            JSONObject personInfoJsonObj = new JSONObject();
            AppHeaderAPIfrontendEndpoints endPoint = null;
            try{
                endPoint = AppHeaderAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());     
            if (actionName.toUpperCase().equalsIgnoreCase(AppHeaderAPIfrontendEndpoints.GETAPPHEADER.getName())){
                String personFieldsName = LPNulls.replaceNull(argValues[0]).toString();
                String[] personFieldsNameArr = new String[0];
                if ( personFieldsName==null || personFieldsName.length()==0){
                    personFieldsNameArr = MANDATORY_PARAMS_FRONTEND_GETAPPHEADER_PERSONFIELDSNAME_DEFAULT_VALUE.split("\\|");                            
                }else{
                    personFieldsNameArr = personFieldsName.split("\\|");
                }    
                if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}   
                Token token = new Token(finalToken);
                Object[][] personInfoArr = Rdbms.getRecordFieldsByFilter(LPPlatform.SCHEMA_CONFIG, TblsAppConfig.Person.TBL.getName(), 
                     new String[]{TblsAppConfig.Person.FLD_PERSON_ID.getName()}, new String[]{token.getPersonName()}, personFieldsNameArr);             
                if (LPPlatform.LAB_FALSE.equals(personInfoArr[0][0].toString())){                                                                                                                                                   
                    Object[] errMsg = LPFrontEnd.responseError(LPArray.array2dTo1d(personInfoArr), language, null);
                    response.sendError((int) errMsg[0], (String) errMsg[1]);   
                    Rdbms.closeRdbms();    
                    return;
                }
                for (int iFields=0; iFields<personFieldsNameArr.length; iFields++ ){
                    personInfoJsonObj.put(personFieldsNameArr[iFields], personInfoArr[0][iFields]);
                }                                 
                token=null;
                LPFrontEnd.servletReturnSuccess(request, response, personInfoJsonObj);
            }            
        }catch(Exception e){            
            String exceptionMessage = e.getMessage();           
            Object[] errMsg = LPFrontEnd.responseError(new String[]{exceptionMessage}, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]); 
            Rdbms.closeRdbms(); 
        } finally {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
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
        }catch(ServletException|IOException e){
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