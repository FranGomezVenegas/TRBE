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
import databases.Token;
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
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;

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

        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
            return;          
        }             
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME); 
        
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode(), null, language);              
                return;                             
        }
        SavedQueriesAPIfrontendEndpoints endPoint = null;
        try{
            endPoint = SavedQueriesAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language);              
            return;                   
        }
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}          

        switch (endPoint){
            case ALL_SAVED_QUERIES:              
                String[] fieldsToRetrieve=getAllFieldNames(TblsData.TablesData.SAVED_QUERIES.getTableFields());
                Object[][] savedQueriesInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()),TblsData.TablesData.SAVED_QUERIES.getTableName(), 
                        new String[]{TblsData.SavedQueries.FLD_ID.getName()+">"}, 
                        new Object[]{0}, 
                        fieldsToRetrieve, new String[]{TblsData.SavedQueries.FLD_ID.getName()+" desc"});
                JSONArray savedQryJArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(savedQueriesInfo[0][0].toString())){
                    for (Object[] currSavedQry: savedQueriesInfo){
                        
                        JSONObject savedQryObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currSavedQry);
                        JSONObject json=new JSONObject();
                        if (LPArray.valueInArray(fieldsToRetrieve, TblsData.SavedQueries.FLD_DEFINITION.getName())){
                            try {
                                Object qryDefinition=currSavedQry[LPArray.valuePosicInArray(fieldsToRetrieve, TblsData.SavedQueries.FLD_DEFINITION.getName())];
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
/*            case INVESTIGATION_RESULTS_PENDING_DECISION:
                String statusClosed=Parameter.getBusinessRuleProcedureFile(procInstanceName, DataProgramCorrectiveActionBusinessRules.STATUS_CLOSED.getAreaName(), DataProgramCorrectiveActionBusinessRules.STATUS_CLOSED.getTagName());
                JSONArray jArray = new JSONArray(); 
                if (!isProgramCorrectiveActionEnable(procInstanceName)){
                  JSONObject jObj=new JSONObject();
                  jArray.add(jObj.put(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), "program corrective action not active!"));
                }
                else{
                  Object[][] investigationResultsPendingDecision = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), 
                          new String[]{TblsProcedure.ProgramCorrectiveAction.STATUS.getName()+"<>"}, 
                          new String[]{statusClosed}, 
                          getAllFieldNames(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableFields()), new String[]{TblsProcedure.ProgramCorrectiveAction.PROGRAM_NAME.getName()});
                  if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationResultsPendingDecision[0][0].toString()))LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());


                  for (Object[] curRow: investigationResultsPendingDecision){
                    JSONObject jObj=LPJson.convertArrayRowToJSONObject(getAllFieldNames(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableFields()), curRow);
                    jArray.add(jObj);
                  }
                }
                // Rdbms.closeRdbms();                    
                LPFrontEnd.servletReturnSuccess(request, response, jArray);
                break;                
            case INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION:
                Integer investigationId=null;
                String investigationIdStr=LPNulls.replaceNull(argValues[0]).toString();
                if (investigationIdStr!=null && investigationIdStr.length()>0) investigationId=Integer.valueOf(investigationIdStr);

                fieldsToRetrieve=TblsData.SavedQueries.getAllFieldNames();
                incidentsNotClosed=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()),TblsData.TablesData.SAVED_QUERIES.getTableName(), 
                        new String[]{TblsData.SavedQueries.FLD_ID.getName()}, 
                        new Object[]{investigationId}, 
                        fieldsToRetrieve, new String[]{TblsData.SavedQueries.FLD_ID.getName()+" desc"});
                investigationJArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsNotClosed[0][0].toString())){
                    for (Object[] currInvestigation: incidentsNotClosed){
                        JSONObject investigationJObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInvestigation);
                        
                        fieldsToRetrieve=TblsProcedure.InvestObjects.getAllFieldNames();
                        incidentsNotClosed=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()),TblsProcedure.TablesProcedure.INVEST_OBJECTS.getTableName(), 
                                new String[]{TblsProcedure.InvestObjects.INVEST_ID.getName()}, 
                                new Object[]{investigationId}, 
                                fieldsToRetrieve, new String[]{TblsProcedure.InvestObjects.ID.getName()});
                        JSONArray investObjectsJArr = new JSONArray();
                        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsNotClosed[0][0].toString())){
                            for (Object[] currInvestObject: incidentsNotClosed){
                                JSONObject investObjectsJObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInvestObject);
                                investObjectsJArr.add(investObjectsJObj);
                            }
                        }
                        investigationJObj.put(TblsProcedure.TablesProcedure.INVEST_OBJECTS.getTableName(), investObjectsJArr);
                        investigationJArr.add(investigationJObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, investigationJArr);
                return;*/
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
