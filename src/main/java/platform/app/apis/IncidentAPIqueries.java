/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.app.apis;

import platform.app.apis.IncidentAPIactions.IncidentAPIqueriesEndpoints;
import static platform.app.apis.IncidentAPIactions.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlStatementEnums;
import databases.SqlWhere;
import databases.TblsApp;
import databases.TblsAppAudit;
import databases.TblsDataAudit;
import databases.features.Token;
import functionaljavaa.incident.AppIncident;
import functionaljavaa.incident.AppIncident.DataIncidentAuditEvents;
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
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class IncidentAPIqueries extends HttpServlet {

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
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false, true);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        
        try{
        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;          
        }             
        String actionName = procReqInstance.getActionName();
        String finalToken = procReqInstance.getTokenString();
        
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode(), null, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;                             
        }
        IncidentAPIqueriesEndpoints endPoint = null;
        try{
            endPoint = IncidentAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
            return;                   
        }
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))){return;}          

        switch (endPoint){
            case USER_OPEN_INCIDENTS:              
                String[] fieldsToRetrieve=getAllFieldNames(TblsApp.TablesApp.INCIDENT.getTableFields());
                Object[][] incidentsClosedLastDays=QueryUtilitiesEnums.getTableData(TblsApp.TablesApp.INCIDENT, 
                        EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.INCIDENT, "ALL"),
                        new String[]{TblsApp.Incident.STATUS.getName()+"<>", TblsApp.Incident.PERSON_CREATION.getName()}, 
                        new Object[]{AppIncident.IncidentStatuses.CLOSED.toString(), token.getPersonName()}, 
                        new String[]{TblsApp.Incident.ID.getName()+SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
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

                fieldsToRetrieve=getAllFieldNames(TblsAppAudit.TablesAppAudit.INCIDENT.getTableFields());
                incidentsClosedLastDays=QueryUtilitiesEnums.getTableData(TblsAppAudit.TablesAppAudit.INCIDENT, 
                    EnumIntTableFields.getTableFieldsFromString(TblsAppAudit.TablesAppAudit.INCIDENT, "ALL"),
                    new String[]{TblsAppAudit.Incident.INCIDENT_ID.getName()}, new Object[]{incId}, 
                    new String[]{TblsAppAudit.Incident.DATE.getName()+SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                jArr = new JSONArray();
                Integer actionPosic=LPArray.valuePosicInArray(fieldsToRetrieve, TblsAppAudit.Incident.ACTION_NAME.getName());
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsClosedLastDays[0][0].toString())){
                    for (Object[] currIncident: incidentsClosedLastDays){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                        if (actionPosic>-1){
                            String action=LPNulls.replaceNull(currIncident[actionPosic]).toString();
                            String propValue = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
                                DataIncidentAuditEvents.class.getSimpleName(), null, action, "en", false, null);
                            if (propValue.length()==0) propValue=action;
                            jObj.put(TblsDataAudit.Sample.ACTION_PRETTY_EN.getName(), propValue);
                            propValue = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
                                DataIncidentAuditEvents.class.getSimpleName(), null, action, "es", false, null);
                            if (propValue.length()==0) propValue=action;
                            jObj.put(TblsDataAudit.Sample.ACTION_PRETTY_ES.getName(), propValue);
                            if (DataIncidentAuditEvents.NEW_INCIDENT_CREATED.name().equalsIgnoreCase(action)){
                                SqlWhere where=new SqlWhere();
                                where.addConstraint(TblsApp.Incident.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{incId}, null);
                                Object[][] incidentsInfo=QueryUtilitiesEnums.getTableData(TblsApp.TablesApp.INCIDENT, 
                                        EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.INCIDENT, 
                                            new String[]{TblsApp.Incident.TITLE.getName(), TblsApp.Incident.DETAIL.getName()}),
                                        where,                                        
                                        new String[]{TblsApp.Incident.ID.getName()+SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                                jObj.put("note", incidentsInfo[0][0].toString() +": "+incidentsInfo[0][1].toString());
                            }
                        }
                        jArr.add(jObj);
                    }
                }
                Rdbms.closeRdbms();                  
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                return;
            case CLOSED_INCIDENTS_LAST_N_DAYS:
                String numDays = LPNulls.replaceNull(argValues[0]).toString();
                if (numDays.length()==0) numDays=String.valueOf(7);
                int numDaysInt=0-Integer.valueOf(numDays);               
                fieldsToRetrieve=getAllFieldNames(TblsApp.TablesApp.INCIDENT.getTableFields());
                incidentsClosedLastDays=QueryUtilitiesEnums.getTableData(TblsApp.TablesApp.INCIDENT, 
                    EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.INCIDENT, "ALL"),
                    new String[]{TblsApp.Incident.STATUS.getName(), TblsApp.Incident.DATE_RESOLUTION.getName()+SqlStatement.WHERECLAUSE_TYPES.GREATER_THAN.getSqlClause()}, 
                    new Object[]{AppIncident.IncidentStatuses.CLOSED.toString(), LPDate.addDays(LPDate.getCurrentDateWithNoTime(), numDaysInt)}, 
                    new String[]{TblsApp.Incident.DATE_RESOLUTION.getName()+SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
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
        }finally {
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
