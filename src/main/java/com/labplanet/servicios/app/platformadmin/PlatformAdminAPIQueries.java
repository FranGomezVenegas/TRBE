/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app.platformadmin;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.IncidentAPIactions.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsApp;
import databases.TblsProcedure;
import databases.features.Token;
import static functionaljavaa.platformadmin.AppBusinessRules.AllAppBusinessRules;
import functionaljavaa.platformadmin.PlatformAdminEnums.PlatformAdminAPIqueriesEndpoints;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class PlatformAdminAPIQueries extends HttpServlet {

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
            PlatformAdminAPIqueriesEndpoints endPoint = null;
            try{
                endPoint = PlatformAdminAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                return;                   
            }
            ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false, true);
            if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
                return;
            }
                
            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))){return;}          

            JSONObject jMainObj=new JSONObject();
            EnumIntTables[] tblsIP=null;
            String alternativeProcedureInstanceName=null;
            switch (endPoint){
                case GET_BLACK_IP_LIST:
                    tblsIP=new EnumIntTables[]{TblsApp.TablesApp.IP_BLACK_LIST};
                    break;
                case GET_WHITE_IP_LIST:
                    tblsIP=new EnumIntTables[]{TblsApp.TablesApp.IP_WHITE_LIST};
                    break;
                case GET_PLATFORM_BUSINESS_RULES:
                    alternativeProcedureInstanceName="app";
                    tblsIP=new EnumIntTables[]{TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE};
                    break;
                case GET_PLATFORM_ADMIN_ALL_INFO:
                    tblsIP=new EnumIntTables[]{TblsApp.TablesApp.IP_BLACK_LIST, TblsApp.TablesApp.IP_WHITE_LIST};
                    jMainObj.put("business_rules", AllAppBusinessRules(request, response));
                    break;
                case GET_API_LISTS:
                    tblsIP=new EnumIntTables[]{TblsApp.TablesApp.IP_BLACK_LIST, TblsApp.TablesApp.IP_WHITE_LIST};
                    break;
                default: 
                LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                    return;
            }
            if (tblsIP!=null){
                for (EnumIntTables curTbl: tblsIP){
                    SqlWhere w=new SqlWhere();
                    w.addConstraint(curTbl.getTableFields()[0], SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{""}, null);
                    String[] fieldsToRetrieve=getAllFieldNames(curTbl.getTableFields());
                    Object[][] ipBlackLists=QueryUtilitiesEnums.getTableData(curTbl,
                            EnumIntTableFields.getTableFieldsFromString(curTbl, "ALL"),
                            w, new String[]{curTbl.getPrimaryKey()[0]+" desc"}, alternativeProcedureInstanceName);
                    JSONArray jArr = new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(ipBlackLists[0][0].toString())){
                        for (Object[] currInstr: ipBlackLists){
                            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstr);
                            jArr.add(jObj);
                        }
                    }
                    if (tblsIP.length==1){
                        Rdbms.closeRdbms();  
                        LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    }else
                        jMainObj.put(curTbl.getTableName(), jArr);
                }
            }
            Rdbms.closeRdbms();  
            LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
        }catch(Exception e){
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }finally {
            ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
            procReqInstance.killIt();
//             Rdbms.closeRdbms(); 
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
        }
    }
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
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
