/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulegenoma;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.modulegenoma.GenomaConfigVariableAPI.GenomaVariableAPIEndPoints.VARIABLE_SET_ADD_VARIABLE;
import com.labplanet.servicios.modulegenoma.GenomaProjectAPI.GenomaProjectAPIParamsList;
import databases.Rdbms;
import functionaljavaa.modulegenoma.GenomaConfigVariables;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntEndpoints;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class GenomaConfigVariableAPI extends HttpServlet {

    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
            
    public enum GenomaVariableAPIEndPoints implements EnumIntEndpoints{
          VARIABLE_SET_ADD_VARIABLE("VARIABLE_SET_ADD_VARIABLE", "variableSetName|variableName", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects), 
          VARIABLE_SET_REMOVE_VARIABLE("VARIABLE_SET_REMOVE_VARIABLE", "variableSetName|variableName", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects)
          ;
        private GenomaVariableAPIEndPoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
            this.outputObjectTypes=outputObjectTypes;            
        } 
        @Override        public String getName(){return this.name;}
        @Override        public String getSuccessMessageCode(){return this.successMessageCode;}           
        @Override        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        @Override        public LPAPIArguments[] getArguments() {return arguments;}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }
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
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        String[] errObject = new String[]{"Servlet Genoma VariableAPI at " + request.getServletPath()};   
        String schemaConfigName = LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName());    
        Rdbms.setTransactionId(schemaConfigName);
        try (PrintWriter out = response.getWriter()) {
            Object[] dataSample = null;
                    GenomaVariableAPIEndPoints endPoint = null;
                    try{
                        endPoint = GenomaVariableAPIEndPoints.valueOf(actionName.toUpperCase());
                    }catch(Exception e){
                        LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                        return;                   
                    }
                    Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                        procReqInstance.killIt();
                        LPFrontEnd.servletReturnResponseError(request, response,
                                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                        return;
                    }  
            
            switch (endPoint){
                case VARIABLE_SET_ADD_VARIABLE:
                case VARIABLE_SET_REMOVE_VARIABLE:     
                    String variableSetName=request.getParameter(GenomaProjectAPIParamsList.VARIABLE_SET_NAME.getParamName());
                    String variableName=request.getParameter(GenomaProjectAPIParamsList.VARIABLE_NAME.getParamName());
                    if (VARIABLE_SET_ADD_VARIABLE.equals(endPoint))
                        dataSample =GenomaConfigVariables.variableSetAddVariable(variableSetName, variableName);
                    else if ("VARIABLE_SET_REMOVE_VARIABLE".equalsIgnoreCase(actionName))
                        dataSample =GenomaConfigVariables.variableSetRemoveVariable(variableSetName, variableName);
                    else
                        LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                    break;       
                default:      
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                    return;                    
            }    
            if (dataSample!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(dataSample[0].toString())){  
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, dataSample);   
            }else{                
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, dataSample[4].toString(), null);   
            }            
        }catch(Exception e){   
            response.setStatus(401);
            errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
        } finally {
            try {
                procReqInstance.killIt();
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
        } catch (IOException | ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
        } catch (IOException | ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
