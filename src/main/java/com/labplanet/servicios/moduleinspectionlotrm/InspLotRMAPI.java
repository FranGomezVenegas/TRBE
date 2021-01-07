/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleinspectionlotrm;

import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.modulesample.ClassSample;
import com.labplanet.servicios.modulesample.SampleAPI;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import databases.Rdbms;
import databases.Token;
import functionaljavaa.audit.AuditAndUserValidation;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */ 
public class InspLotRMAPI extends HttpServlet {
    public enum InspLotRMAPIEndpoints{
        NEW_LOT("NEW_LOT", "createNewLot", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MATERIAL_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_TEMPLATE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_QUANTITY, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_QUANTITY_UOM, LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NUM_CONTAINERS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 14),
            }),
        ZZENTERRESULT("ZZZZENTERRESULT", "enterResult_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )} ),        
        LOT_TAKE_DECISION("LOT_TAKE_DECISION", "lotTakeDecision_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_DECISION, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),
            }),
        ;
        private InspLotRMAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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
    
    public enum InspLotRMQueriesAPIEndpoints{
        /**
         *
         */
        GET_LOT_INFO("GET_LOT_INFO", "get_lot_info_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7)}),
        GET_LOT_SAMPLES_INFO("GET_LOT_SAMPLES_INFO", "get_lot_samples_info_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7)}),
        ;
        private InspLotRMQueriesAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        String language = LPFrontEnd.setLanguage(request); 
        String[] errObject = new String[]{"Servlet programAPI at " + request.getServletPath()};   

        String[] mandatoryParams = new String[]{""};
        Object[] areMandatoryParamsInResponse = LPHttp.areAPIMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
            return;          
        }             
        String schemaPrefix = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX);            
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.API_ERRORTRAPING_INVALID_TOKEN, null, language);              
                return;                             
        }
        Object[] actionEnabled = LPPlatform.procActionEnabled(schemaPrefix, token, actionName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, actionEnabled);
            return ;                           
        }            
        actionEnabled = LPPlatform.procUserRoleActionEnabled(schemaPrefix, token.getUserRole(), actionName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){            
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, actionEnabled);
            return ;                           
        }                        
        AuditAndUserValidation auditAndUsrValid=AuditAndUserValidation.getInstance(request, response, language);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditAndUsrValid.getCheckUserValidationPassesDiag()[0].toString())){
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, auditAndUsrValid.getCheckUserValidationPassesDiag());              
            return;          
        }             

        if (!LPFrontEnd.servletStablishDBConection(request, response, false)){return;}
//        Connection con = Rdbms.createTransactionWithSavePoint();        
        //Rdbms.setTransactionId(schemaConfigName);
        InspLotRMAPIEndpoints endPoint = null;
        try{
            endPoint = InspLotRMAPIEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            SampleAPIParams.SampleAPIEndpoints endPointSmp = null;
            try{
                endPointSmp = SampleAPIParams.SampleAPIEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception er){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }                
            ClassSample clssSmp=new ClassSample(request, token, schemaPrefix, endPointSmp);
            if (clssSmp.getEndpointExists()){
                Object[] diagnostic=clssSmp.getDiagnostic();
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
    /*                Rdbms.rollbackWithSavePoint();
                    if (!con.getAutoCommit()){
                        con.rollback();
                        con.setAutoCommit(true);}                */                        
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clssSmp.getMessageDynamicData());           
                }else{
                    JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(SampleAPI.class.getSimpleName(), endPointSmp.getSuccessMessageCode(), clssSmp.getMessageDynamicData(), clssSmp.getRelatedObj().getRelatedObject());                
                    LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
                } 
            }                
        }
        areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);
            return;
        }                
        try (PrintWriter out = response.getWriter()) {
            ClassInspLotRM clss=new ClassInspLotRM(request, token, schemaPrefix, endPoint, auditAndUsrValid);
            Object[] diagnostic=clss.getDiagnostic();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
/*                Rdbms.rollbackWithSavePoint();
                if (!con.getAutoCommit()){
                    con.rollback();
                    con.setAutoCommit(true);}                */
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clss.getMessageDynamicData());   
            }else{
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), clss.getMessageDynamicData(), clss.getRelatedObj().getRelatedObject());                
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
            }   
        }catch(Exception e){   
 /*           try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(sampleAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
*/                        
            auditAndUsrValid.killInstance();
            Rdbms.closeRdbms();                   
            errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
        } finally {            
            // release database resources
            try {                
                //con.close();
                auditAndUsrValid.killInstance();
                Rdbms.closeRdbms();   
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }      }

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