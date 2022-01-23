/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import com.labplanet.servicios.app.IncidentAPI.IncidentAPIfrontendEndpoints;
import static com.labplanet.servicios.app.IncidentAPI.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsApp;
import databases.TblsAppAudit;
import databases.TblsDataAudit;
import databases.Token;
import functionaljavaa.incident.AppIncident;
import functionaljavaa.parameter.Parameter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class HolidayCalendarAPIqueries extends HttpServlet {

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
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
            return;          
        }             
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.ApiErrorTraping.INVALID_TOKEN.getName(), null, language);              
                return;                             
        }
        IncidentAPIfrontendEndpoints endPoint = null;
        try{
            endPoint = IncidentAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
            return;                   
        }
        ProcedureRequestSession.getInstanceForActions(request, response, false);
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}          

        switch (endPoint){
            case USER_OPEN_INCIDENTS:              
                String[] fieldsToRetrieve=TblsApp.Incident.getAllFieldNames();
                Object[][] incidentsClosedLastDays=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(),TblsApp.Incident.TBL.getName(), 
                        new String[]{TblsApp.Incident.FLD_STATUS.getName()+"<>", TblsApp.Incident.FLD_PERSON_CREATION.getName()}, 
                        new Object[]{AppIncident.IncidentStatuses.CLOSED.toString(), token.getPersonName()}, 
                        fieldsToRetrieve, new String[]{TblsApp.Incident.FLD_ID.getName()+" desc"});
                JSONArray jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsClosedLastDays[0][0].toString())){
                    for (Object[] currIncident: incidentsClosedLastDays){
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
                incidentsClosedLastDays=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_AUDIT.getName(),TblsAppAudit.Incident.TBL.getName(), 
                        new String[]{TblsAppAudit.Incident.FLD_INCIDENT_ID.getName()}, 
                        new Object[]{incId}, 
                        fieldsToRetrieve, new String[]{TblsAppAudit.Incident.FLD_DATE.getName()+" desc"});
                jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsClosedLastDays[0][0].toString())){
                    for (Object[] currIncident: incidentsClosedLastDays){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                        Integer actionPosic=LPArray.valuePosicInArray(fieldsToRetrieve, TblsAppAudit.Incident.FLD_ACTION_NAME.getName());
                        if (actionPosic>-1){
                            String action=LPNulls.replaceNull(currIncident[actionPosic]).toString();
                            String propValue = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
                                "dataIncidentAuditEvents", null, action, "en", false);
                            if (propValue.length()==0) propValue=action;
                            jObj.put(TblsDataAudit.Sample.FLD_ACTION_PRETTY_EN.getName(), propValue);
                            propValue = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
                                "dataIncidentAuditEvents", null, action, "es", false);
                            if (propValue.length()==0) propValue=action;
                            jObj.put(TblsDataAudit.Sample.FLD_ACTION_PRETTY_ES.getName(), propValue);
                        }
                        jArr.add(jObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
            case CLOSED_INCIDENTS_LAST_N_DAYS:
                String numDays = LPNulls.replaceNull(argValues[0]).toString();
                if (numDays.length()==0) numDays=String.valueOf(7);
                int numDaysInt=0-Integer.valueOf(numDays);               
                fieldsToRetrieve=TblsApp.Incident.getAllFieldNames();
                incidentsClosedLastDays=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(),TblsApp.Incident.TBL.getName(), 
                        new String[]{TblsApp.Incident.FLD_STATUS.getName(), TblsApp.Incident.FLD_DATE_RESOLUTION.getName()+SqlStatement.WHERECLAUSE_TYPES.GREATER_THAN.getSqlClause()}, 
                        new Object[]{AppIncident.IncidentStatuses.CLOSED.toString(), LPDate.addDays(LPDate.getCurrentDateWithNoTime(), numDaysInt)}, 
                        fieldsToRetrieve, new String[]{TblsApp.Incident.FLD_DATE_RESOLUTION.getName()+" desc"});
                jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsClosedLastDays[0][0].toString())){
                    for (Object[] currIncident: incidentsClosedLastDays){
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