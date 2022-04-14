/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import com.labplanet.servicios.app.InvestigationAPI.InvestigationAPIfrontendEndpoints;
import static com.labplanet.servicios.app.InvestigationAPI.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.TblsProcedure;
import databases.Token;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction.DataProgramCorrectiveActionBusinessRules;
import static functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction.isProgramCorrectiveActionEnable;
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
public class InvestigationAPIfrontend extends HttpServlet {

    
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
        ProcedureRequestSession instanceForQueries = ProcedureRequestSession.getInstanceForQueries(request, response, Boolean.FALSE);
        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;          
        }             
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME); 
        
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode(), null, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;                             
        }
        InvestigationAPIfrontendEndpoints endPoint = null;
        try{
            endPoint = InvestigationAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
            return;                   
        }
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}          
        JSONArray jArray = new JSONArray(); 

        switch (endPoint){
            case OPEN_INVESTIGATIONS:              
                String[] fieldsToRetrieve=getAllFieldNames(TblsProcedure.TablesProcedure.INVESTIGATION.getTableFields());
                Object[][] incidentsNotClosed=QueryUtilitiesEnums.getTableData(TblsProcedure.TablesProcedure.INVESTIGATION, 
                    EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.INVESTIGATION, "ALL"),
                    new String[]{TblsProcedure.Investigation.CLOSED.getName()+"<>"}, 
                    new Object[]{true}, 
                    new String[]{TblsProcedure.Investigation.ID.getName()+" desc"});
                JSONArray investigationJArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsNotClosed[0][0].toString())){
                    for (Object[] currInvestigation: incidentsNotClosed){
                        JSONObject investigationJObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInvestigation);
                        Integer investFldPosic=LPArray.valuePosicInArray(fieldsToRetrieve, TblsProcedure.Investigation.ID.getName());
                        if (investFldPosic>-1){
                            Integer investigationId=Integer.valueOf(currInvestigation[investFldPosic].toString());
                            String[] fieldsToRetrieveInvestObj=getAllFieldNames(TblsProcedure.TablesProcedure.INVEST_OBJECTS.getTableFields());
                            incidentsNotClosed=QueryUtilitiesEnums.getTableData(TblsProcedure.TablesProcedure.INVEST_OBJECTS, 
                                EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.INVEST_OBJECTS, "ALL"),
                                new String[]{TblsProcedure.InvestObjects.INVEST_ID.getName()}, 
                                new Object[]{investigationId}, 
                                new String[]{TblsProcedure.InvestObjects.ID.getName()});
                            JSONArray investObjectsJArr = new JSONArray();
                            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsNotClosed[0][0].toString())){
                                for (Object[] currInvestObject: incidentsNotClosed){
                                    JSONObject investObjectsJObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieveInvestObj, currInvestObject);
                                    investObjectsJArr.add(investObjectsJObj);
                                }
                            }
                            investigationJObj.put(TblsProcedure.TablesProcedure.INVEST_OBJECTS.getTableName(), investObjectsJArr);
                        }
                        investigationJArr.add(investigationJObj);
                    }
                }
                //Rdbms.closeRdbms();  
                instanceForQueries.killIt();                
                LPFrontEnd.servletReturnSuccess(request, response, investigationJArr);
                return;                  
            case INVESTIGATION_RESULTS_PENDING_DECISION:
                String statusClosed=Parameter.getBusinessRuleProcedureFile(procInstanceName, DataProgramCorrectiveActionBusinessRules.STATUS_CLOSED.getAreaName(), DataProgramCorrectiveActionBusinessRules.STATUS_CLOSED.getTagName());
                if (!isProgramCorrectiveActionEnable(procInstanceName)){
                  JSONObject jObj=new JSONObject();
                  jObj.put(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), "program corrective action not active!");
                  jArray.add(jObj);
                }
                else{
                    Object[][] investigationResultsPendingDecision = QueryUtilitiesEnums.getTableData(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION, 
                        EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION, "ALL"),
                        new String[]{TblsProcedure.ProgramCorrectiveAction.STATUS.getName()+"<>"}, 
                        new String[]{statusClosed}, 
                        new String[]{TblsProcedure.ProgramCorrectiveAction.PROGRAM_NAME.getName()});
                  if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationResultsPendingDecision[0][0].toString()))LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());


                  for (Object[] curRow: investigationResultsPendingDecision){
                    JSONObject jObj=LPJson.convertArrayRowToJSONObject(getAllFieldNames(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableFields()), curRow);
                    jArray.add(jObj);
                  }
                }
                // Rdbms.closeRdbms();       
                instanceForQueries.killIt();                
                LPFrontEnd.servletReturnSuccess(request, response, jArray);
                break;                
            case INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION:
                if (!isProgramCorrectiveActionEnable(procInstanceName)){
                  JSONObject jObj=new JSONObject();
                  jObj.put(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), "program corrective action not active!");
                  jArray.add(jObj);
                  LPFrontEnd.servletReturnSuccess(request, response, jArray);
                }
                Integer investigationId=null;
                String investigationIdStr=LPNulls.replaceNull(argValues[0]).toString();
                if (investigationIdStr!=null && investigationIdStr.length()>0) investigationId=Integer.valueOf(investigationIdStr);

                fieldsToRetrieve=getAllFieldNames(TblsProcedure.TablesProcedure.INVESTIGATION.getTableFields());
                incidentsNotClosed=QueryUtilitiesEnums.getTableData(TblsProcedure.TablesProcedure.INVESTIGATION, 
                    EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.INVESTIGATION, "ALL"),
                    new String[]{TblsProcedure.Investigation.ID.getName()}, 
                    new Object[]{investigationId}, 
                    new String[]{TblsProcedure.Investigation.ID.getName()+" desc"});
                investigationJArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsNotClosed[0][0].toString())){
                    for (Object[] currInvestigation: incidentsNotClosed){
                        JSONObject investigationJObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInvestigation);
                        fieldsToRetrieve=getAllFieldNames(TblsProcedure.TablesProcedure.INVEST_OBJECTS.getTableFields());
                        incidentsNotClosed=QueryUtilitiesEnums.getTableData(TblsProcedure.TablesProcedure.INVEST_OBJECTS, 
                            EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.INVEST_OBJECTS, "ALL"),
                            new String[]{TblsProcedure.InvestObjects.INVEST_ID.getName()}, 
                            new Object[]{investigationId}, 
                            new String[]{TblsProcedure.InvestObjects.ID.getName()});
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
                instanceForQueries.killIt();
                LPFrontEnd.servletReturnSuccess(request, response, investigationJArr);
                return;
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
