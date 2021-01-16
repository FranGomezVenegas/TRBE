/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import com.labplanet.servicios.app.IncidentAPI.IncidentAPIfrontendEndpoints;
import static com.labplanet.servicios.app.IncidentAPI.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.TblsApp;
import databases.TblsAppAudit;
import databases.Token;
import functionaljavaa.incident.AppIncident;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class IncidentAPIfrontend extends HttpServlet {

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

        String language = LPFrontEnd.setLanguage(request); 

        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
            return;          
        }             
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.API_ERRORTRAPING_INVALID_TOKEN, null, language);              
                return;                             
        }
        IncidentAPIfrontendEndpoints endPoint = null;
        try{
            endPoint = IncidentAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);              
            return;                   
        }
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}          

        switch (endPoint){
            case USER_OPEN_INCIDENTS:              
                String[] fieldsToRetrieve=TblsApp.Incident.getAllFieldNames();
                Object[][] incidentsNotClosed=Rdbms.getRecordFieldsByFilter(LPPlatform.SCHEMA_APP,TblsApp.Incident.TBL.getName(), 
                        new String[]{TblsApp.Incident.FLD_STATUS.getName()+"<>", TblsApp.Incident.FLD_PERSON_CREATION.getName()}, 
                        new Object[]{AppIncident.IncidentStatuses.CLOSED.toString(), token.getPersonName()}, 
                        fieldsToRetrieve, new String[]{TblsApp.Incident.FLD_ID.getName()+" desc"});
                JSONArray jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsNotClosed[0][0].toString())){
                    for (Object[] currIncident: incidentsNotClosed){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                        jArr.add(jObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                return;  
            case INCIDENT_DETAIL_FOR_GIVEN_INCIDENT:
                Integer incId=null;
                String incIdStr=LPNulls.replaceNull(argValues[0]).toString();
                if (incIdStr!=null && incIdStr.length()>0) incId=Integer.valueOf(incIdStr);

                fieldsToRetrieve=TblsAppAudit.Incident.getAllFieldNames();
                incidentsNotClosed=Rdbms.getRecordFieldsByFilter(LPPlatform.SCHEMA_APP_AUDIT,TblsAppAudit.Incident.TBL.getName(), 
                        new String[]{TblsAppAudit.Incident.FLD_INCIDENT_ID.getName()}, 
                        new Object[]{incId}, 
                        fieldsToRetrieve, new String[]{TblsAppAudit.Incident.FLD_DATE.getName()+" desc"});
                jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsNotClosed[0][0].toString())){
                    for (Object[] currIncident: incidentsNotClosed){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                        jArr.add(jObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
        default: 
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
