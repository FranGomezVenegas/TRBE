/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import com.labplanet.servicios.app.GlobalAPIsParams;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import org.json.simple.JSONObject;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class EnvMonAPI extends HttpServlet {  
    public enum EnvMonAPIEndpoints{
        /**
         *
         */
        CORRECTIVE_ACTION_COMPLETE("CORRECTIVE_ACTION_COMPLETE", "programCompleteCorrectiveAction_success", 
                new LPAPIArguments[]{new LPAPIArguments(PARAMETER_PROGRAM_SAMPLE_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(PARAMETER_PROGRAM_SAMPLE_CORRECITVE_ACTION_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7)}),
        EM_BATCH_INCUB_CREATE("EM_BATCH_INCUB_CREATE", "incubatorBatch_create_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 10)}),
        EM_BATCH_INCUB_REMOVE("EM_BATCH_INCUB_REMOVE", "incubatorBatch_remove_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}),
        EM_BATCH_ASSIGN_INCUB("EM_BATCH_ASSIGN_INCUB", "incubatorBatch_assignIncubator_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCUBATOR_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
        EM_BATCH_UPDATE_INFO("EM_BATCH_UPDATE_INFO", "incubatorBatch_updateInfo_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8)}),
        EM_BATCH_INCUB_START("EM_BATCH_INCUB_START", "incubatorBatch_incubationStart_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8)}),
        EM_BATCH_INCUB_END("EM_BATCH_INCUB_END", "incubatorBatch_incubationEnd_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8)}),
        EM_LOGSAMPLE_SCHEDULER("EM_LOGSAMPLE_SCHEDULER", "programScheduler_logScheduledSamples", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_START, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_END, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(PARAMETER_PROGRAM_SAMPLE_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8)}),
        ;
        private EnvMonAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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
    
    public enum EnvMonQueriesAPIEndpoints{
        /**
         *
         */
        GET_SAMPLE_INFO("GET_SAMPLE_INFO", "get_sample_info_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7)}),
        GET_SAMPLE_RESULTS("GET_SAMPLE_RESULTS", "get_sample_results_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 8)}),
        ;
        private EnvMonQueriesAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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
     *
     */
    
    /**
     *
     */
    public static final String PARAMETER_PROGRAM_SAMPLE_TEMPLATE="sampleTemplate";

    /**
     *
     */
    public static final String PARAMETER_PROGRAM_SAMPLE_TEMPLATE_VERSION="sampleTemplateVersion";       

    /**
     *
     */
    public static final String PARAMETER_NUM_SAMPLES_TO_LOG="numSamplesToLog";

    /**
     *
     */
    public static final String PARAMETER_PROGRAM_FIELD_NAME="fieldName";

    /**
     *
     */
    public static final String PARAMETER_PROGRAM_FIELD_VALUE="fieldValue";    

    /**
     *
     */
    public static final String PARAMETER_PROGRAM_SAMPLE_PROGRAM_NAME="programName"; 

    /**
     *
     */
    public static final String PARAMETER_PROGRAM_SAMPLE_CORRECITVE_ACTION_ID="programCorrectiveActionId"; 
    
    /**
     *
     */
    public static final String TABLE_SAMPLE_PROGRAM_FIELD="program"; 
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();

        String[] errObject = new String[]{"Servlet programAPI at " + request.getServletPath()};   

//        Connection con = Rdbms.createTransactionWithSavePoint();        

        //Rdbms.setTransactionId(schemaConfigName);
        EnvMonAPIEndpoints endPoint = null;
        try{
            endPoint = EnvMonAPIEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
            return;                   
        }
        Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);
            return;
        }                
        try (PrintWriter out = response.getWriter()) {
            ClassEnvMon clss=new ClassEnvMon(request, endPoint);
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
            procReqInstance.killIt();
            errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
        } finally {
            // release database resources
            try {
                //con.close();
                procReqInstance.killIt();
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
