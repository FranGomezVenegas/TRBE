/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app.platformadmin;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.IncidentAPI.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsApp;
import databases.Token;
import static functionaljavaa.platformadmin.AppBusinessRules.AllAppBusinessRules;
import functionaljavaa.platformadmin.PlatformAdminEnums.PlatformAdminAPIqueriesEndpoints;
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
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntTables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class PlatformAdminQueries extends HttpServlet {

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
        try{
            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                return;          
            }             
            String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   

            Token token = new Token(finalToken);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                    LPFrontEnd.servletReturnResponseError(request, response, 
                            LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode(), null, language);              
                    return;                             
            }
            PlatformAdminAPIqueriesEndpoints endPoint = null;
            try{
                endPoint = PlatformAdminAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            ProcedureRequestSession.getInstanceForActions(request, response, false);
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}          

            switch (endPoint){
                case GET_PLATFORM_ADMIN_ALL_INFO:
                case GET_API_LISTS:
                    JSONObject jMainObj=new JSONObject();
                    EnumIntTables[] tblsIP=new EnumIntTables[]{TblsApp.TablesApp.IP_BLACK_LIST, TblsApp.TablesApp.IP_WHITE_LIST};
                    for (EnumIntTables curTbl: tblsIP){
                        String[] fieldsToRetrieve=getAllFieldNames(curTbl.getTableFields());
                        Object[][] ipBlackLists=QueryUtilitiesEnums.getTableData(curTbl,
                                EnumIntTableFields.getTableFieldsFromString(curTbl, "ALL"),
                                new String[]{TblsApp.IPBlackList.IP_VALUE1.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, 
                                new Object[]{""}, 
                                new String[]{TblsApp.IPBlackList.ID.getName()+" desc"});
                        JSONArray jArr = new JSONArray();
                        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(ipBlackLists[0][0].toString())){
                            for (Object[] currInstr: ipBlackLists){
                                JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstr);
                                jArr.add(jObj);
                            }
                        }
                        jMainObj.put(curTbl.getTableName(), jArr);
                    }
                    if ("GET_PLATFORM_ADMIN_ALL_INFO".equalsIgnoreCase(endPoint.getName())) 
                        jMainObj.put("business_rules", AllAppBusinessRules(request, response));
                    Rdbms.closeRdbms();  
                    LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
            default: 
            }
        }finally {
            // release database resources
            try {           
                ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
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
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
