/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import static com.labplanet.servicios.app.IncidentAPI.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsApp;
import databases.Token;
import functionaljavaa.holidayscalendar.HolidaysCalendarEnums.CalendarAPIqueriesEndpoints;
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
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
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
        CalendarAPIqueriesEndpoints endPoint = null;
        try{
            endPoint = CalendarAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
            return;                   
        }
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}          
        try{
            switch (endPoint){
                case GET_ALL_HOLIDAY_DATES_LIST_ALL_CALENDARS:              
                    String[] fieldsToRetrieve=getAllFieldNames(TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE.getTableFields());
                    Object[][] incidentsClosedLastDays=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(),TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE.getTableName(), 
                            new String[]{TblsApp.HolidaysCalendarDate.FLD_CALENDAR_CODE.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, 
                            new Object[]{}, 
                            fieldsToRetrieve, new String[]{TblsApp.HolidaysCalendarDate.FLD_CALENDAR_CODE.getName(), TblsApp.HolidaysCalendarDate.FLD_ID.getName()});
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
                default: 
            }
        }catch(Exception e){   
            // Rdbms.closeRdbms();                   
            procReqInstance.killIt();
            String[] errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);                   
        } finally {
            // release database resources
            try {           
                procReqInstance.killIt();
                // Rdbms.closeRdbms();   
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
