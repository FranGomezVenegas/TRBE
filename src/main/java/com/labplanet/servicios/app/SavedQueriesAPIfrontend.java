/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import com.labplanet.servicios.app.SavedQueriesAPI.SavedQueriesAPIfrontendEndpoints;
import static com.labplanet.servicios.app.InvestigationAPI.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.TblsData;
import databases.features.Token;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class SavedQueriesAPIfrontend extends HttpServlet {

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
        ProcedureRequestSession.getInstanceForQueries(request, response, Boolean.FALSE);
        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;          
        }             
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode(), null, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;                             
        }
        SavedQueriesAPIfrontendEndpoints endPoint = null;
        try{
            endPoint = SavedQueriesAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
            return;                   
        }
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}          

        switch (endPoint){
            case ALL_SAVED_QUERIES:              
                String[] fieldsToRetrieve=getAllFieldNames(TblsData.TablesData.SAVED_QUERIES.getTableFields());
                Object[][] savedQueriesInfo=QueryUtilitiesEnums.getTableData(TblsData.TablesData.SAVED_QUERIES, 
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAVED_QUERIES, "ALL"),
                    new String[]{TblsData.SavedQueries.ID.getName()+">"}, 
                    new Object[]{0}, 
                    new String[]{TblsData.SavedQueries.ID.getName()+" desc"});
                JSONArray savedQryJArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(savedQueriesInfo[0][0].toString())){
                    for (Object[] currSavedQry: savedQueriesInfo){
                        
                        JSONObject savedQryObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currSavedQry);
                        JSONObject json=new JSONObject();
                        if (LPArray.valueInArray(fieldsToRetrieve, TblsData.SavedQueries.DEFINITION.getName())){
                            try {
                                Object qryDefinition=currSavedQry[LPArray.valuePosicInArray(fieldsToRetrieve, TblsData.SavedQueries.DEFINITION.getName())];
                                JSONParser parser = new JSONParser(); 
                                json = (JSONObject) parser.parse(qryDefinition.toString());
                            } catch (ParseException ex) {
                                Logger.getLogger(SavedQueriesAPIfrontend.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        savedQryObj.put("definition_json", json);
                        savedQryJArr.add(savedQryObj);
                    }                    
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, savedQryJArr);
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
