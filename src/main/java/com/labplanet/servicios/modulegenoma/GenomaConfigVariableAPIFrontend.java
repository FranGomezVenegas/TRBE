/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulegenoma;

import functionaljavaa.modulegenoma.GenomaConfigVariablesQueries;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class GenomaConfigVariableAPIFrontend extends HttpServlet {
    
            
    public enum  GenomaVariableAPIFrontEndEndPoints{
            GET_VARIABLE_SET_VARIABLES_ID("GET_VARIABLE_SET_VARIABLES_ID", "variableSetName"),
            GET_ACTIVE_CONFIG_VARIABLE_SET("GET_ACTIVE_CONFIG_VARIABLE_SET", ""),
//          PROJECT_NEW("PROJECT_NEW", "projectName"), PROJECT_UPDATE("PROJECT_UPDATE", "projectName|fieldsNames|fieldsValues"),
//          PROJECT_ACTIVATE("PROJECT_ACTIVATE", "projectName"), PROJECT_DEACTIVATE("PROJECT_DEACTIVATE", "projectName"),
//          VARIABLE_SET_ADD_VARIABLE("VARIABLE_SET_ADD_VARIABLE", "variableSetName|variableName"), VARIABLE_SET_REMOVE_VARIABLE("VARIABLE_SET_REMOVE_VARIABLE", "variableSetName|variableName"),
//          PROJECT_CHANGE_USER_ROLE("PROJECT_CHANGE_USER_ROLE", "projectName|userName|userRole"), PROJECT_USER_ACTIVATE("PROJECT_USER_ACTIVATE", "projectName|userName|userRole"),
//          PROJECT_USER_DEACTIVATE("PROJECT_USER_DEACTIVATE", "projectName|userName|userRole"),
          ;
        private GenomaVariableAPIFrontEndEndPoints(String name, String mandatoryFields){
            this.endPointName=name;
            this.endPointMandatoryFields=mandatoryFields;
        }
        public String getName(){
            return this.endPointName;
        }
        public String getMandatoryFields(){
            return this.endPointMandatoryFields;
        }
      String endPointName="";
      String endPointMandatoryFields="";
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        if (procReqInstance.getHasErrors()) return;
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        
        try (PrintWriter out = response.getWriter()) {

        GenomaVariableAPIFrontEndEndPoints endPoint = null;
        try{
            endPoint = GenomaVariableAPIFrontEndEndPoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
            return;                   
        }        

        if (!LPFrontEnd.servletStablishDBConection(request, response))return;

        switch (endPoint){
        case GET_ACTIVE_CONFIG_VARIABLE_SET:
            break;
        case GET_VARIABLE_SET_VARIABLES_ID: 
            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, GenomaConfigVariableAPIFrontend.GenomaVariableAPIFrontEndEndPoints.GET_VARIABLE_SET_VARIABLES_ID.getMandatoryFields().split("\\|"));
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                return;                  
            }                                 
            String variableSetName=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.VARIABLE_SET_NAME.getParamName());  
            Object[] varSetVariables=GenomaConfigVariablesQueries.getVariableSetVariablesId(variableSetName);            
            JSONArray jsonArr=new JSONArray();
            jsonArr.add(LPJson.convertToJSON(varSetVariables, "Variable Name"));
            LPFrontEnd.servletReturnSuccess(request, response, jsonArr);
            break;        
        default:      
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);                                                                  
    }
    }catch(Exception e){      
        String exceptionMessage =e.getMessage();
        if (exceptionMessage==null){exceptionMessage="null exception";}
        response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
        LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null);      
    } finally {
       // release database resources
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
        } catch (IOException ex) {
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
        } catch (IOException ex) {
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
