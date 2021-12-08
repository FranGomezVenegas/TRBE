/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app.procs;

import com.labplanet.servicios.app.*;
import static com.labplanet.servicios.app.IncidentAPI.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.TblsAppProcData;
import databases.TblsAppProcDataAudit;
import databases.Token;
import functionaljavaa.instruments.InstrumentsEnums.InstrumentsAPIqueriesEndpoints;
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
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class InstrumentsAPIqueries extends HttpServlet {

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
        InstrumentsAPIqueriesEndpoints endPoint = null;
        try{
            endPoint = InstrumentsAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
            return;                   
        }
        ProcedureRequestSession.getInstanceForActions(request, response, false);
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}          

        switch (endPoint){
            case ACTIVE_INSTRUMENTS_LIST:              
                String[] fieldsToRetrieve=TblsAppProcData.Instruments.getAllFieldNames();
                Object[][] instrumentAudit=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(),TblsAppProcData.Instruments.TBL.getName(), 
                        new String[]{TblsAppProcData.Instruments.FLD_DECOMMISSIONED.getName()+"<>"}, 
                        new Object[]{true}, 
                        fieldsToRetrieve, new String[]{TblsAppProcData.Instruments.FLD_NAME.getName()+" desc"});
                JSONArray jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentAudit[0][0].toString())){
                    for (Object[] currInstr: instrumentAudit){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstr);
                        jArr.add(jObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                return;  
            case INSTRUMENT_AUDIT_FOR_GIVEN_INSTRUMENT:
                String instrName=LPNulls.replaceNull(argValues[0]).toString();
                fieldsToRetrieve=TblsAppProcDataAudit.Instruments.getAllFieldNames();
                instrumentAudit=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA_AUDIT.getName(),TblsAppProcDataAudit.Instruments.TBL.getName(), 
                        new String[]{TblsAppProcDataAudit.Instruments.FLD_INSTRUMENT_NAME.getName()}, 
                        new Object[]{instrName}, 
                        fieldsToRetrieve, new String[]{TblsAppProcDataAudit.Instruments.FLD_INSTRUMENT_NAME.getName(), TblsAppProcDataAudit.Instruments.FLD_DATE.getName()+" desc"});
                jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentAudit[0][0].toString())){
                    for (Object[] currInstrAudit: instrumentAudit){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrAudit);
                        jArr.add(jObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
            case INSTRUMENT_EVENTS_FOR_GIVEN_INSTRUMENT:
                instrName=LPNulls.replaceNull(argValues[0]).toString();
                fieldsToRetrieve=TblsAppProcData.InstrumentEvent.getAllFieldNames();
                Object[][] instrumentEvents = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(),TblsAppProcData.InstrumentEvent.TBL.getName(), 
                    new String[]{TblsAppProcData.InstrumentEvent.FLD_INSTRUMENT.getName()},
                    new Object[]{instrName},
                    fieldsToRetrieve, new String[]{TblsAppProcData.InstrumentEvent.FLD_INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.FLD_CREATED_ON.getName()+" desc"});
                jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentEvents[0][0].toString())){
                    for (Object[] currInstrEv: instrumentEvents){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrEv);
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
