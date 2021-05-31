/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.proceduredefinition;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static functionaljavaa.requirement.ProcedureDefinitionQueries.*;
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
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class ProcedureDefinitionfrontend extends HttpServlet {
    public enum ProcedureDefinitionAPIfrontendEndpoints{
        /**
         *
         */
        ALL_PROCEDURE_DEFINITION("ALL_PROCEDURE_DEFINITION", "",new LPAPIArguments[]{}),
        ENABLE_ACTIONS_AND_ROLES("ENABLE_ACTIONS_AND_ROLES", "",new LPAPIArguments[]{}),
        ;
        private ProcedureDefinitionAPIfrontendEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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
    
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    public static final String ERRORMSG_ERROR_STATUS_CODE="Error Status Code";
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);
        String language = LPFrontEnd.setLanguage(request); 
        try (PrintWriter out = response.getWriter()) {
            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                return;          
            }                  
            String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            
            ProcedureDefinitionAPIfrontendEndpoints endPoint = null;
            try{
                endPoint = ProcedureDefinitionAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME);                        
            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}   
            JSONArray mainArr = new JSONArray(); 
            JSONArray mainContentArr = new JSONArray(); 
            switch (endPoint){
            case ALL_PROCEDURE_DEFINITION:
                JSONObject schemaContentObj = new JSONObject(); 
                String[] sectionsArr=new String[]{ProcBusinessRulesQueries.PROCEDURE_MAIN_INFO.toString(), ProcBusinessRulesQueries.PROCEDURE_ACTIONS_AND_ROLES.toString(),
                    ProcBusinessRulesQueries.PROCEDURE_SAMPLE_AUDIT_LEVEL.toString(),
                    ProcBusinessRulesQueries.PROCEDURE_USER_SOP_CERTIFICATION_LEVEL.toString(), ProcBusinessRulesQueries.PROGRAM_CORRECTIVE_ACTION.toString(),
                    ProcBusinessRulesQueries.CHANGE_OF_CUSTODY.toString(), ProcBusinessRulesQueries.SAMPLE_STAGES_TIMING_CAPTURE.toString(),
                    ProcBusinessRulesQueries.SAMPLE_INCUBATION.toString(),ProcBusinessRulesQueries.PROCEDURE_ALL_PROC_USERS_ROLES.toString(),
                    ProcBusinessRulesQueries.PROCEDURE_SAMPLE_STAGES.toString(),ProcBusinessRulesQueries.PROCEDURE_ENCRYPTION_TABLES_AND_FIELDS.toString()};
                for (String currSection: sectionsArr)
                    mainArr.add(getProcBusinessRulesQueriesInfo(procInstanceName, currSection));
                
                schemaContentObj.put("definition", mainArr);
                mainContentArr.add(schemaContentObj);
                LPFrontEnd.servletReturnSuccess(request, response, schemaContentObj);
                return;                                
            case ENABLE_ACTIONS_AND_ROLES:  
                LPFrontEnd.servletReturnSuccess(request, response, 
                        getProcBusinessRulesQueriesInfo(procInstanceName, ProcBusinessRulesQueries.PROCEDURE_ACTIONS_AND_ROLES.toString()));
                return;
            default:                
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
            }
        }catch(Exception e){
            String errMessage = e.getMessage();
            String[] errObject = new String[0];
            errObject = LPArray.addValueToArray1D(errObject, ERRORMSG_ERROR_STATUS_CODE+": "+HttpServletResponse.SC_BAD_REQUEST);
            errObject = LPArray.addValueToArray1D(errObject, "This call raised one unhandled exception. Error:"+errMessage);     
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);    
        } finally {
            // release database resources
            try {
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }                                       
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
         try {
        processRequest(request, response);
         }catch(ServletException|IOException e){Logger.getLogger(e.getMessage());}
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
