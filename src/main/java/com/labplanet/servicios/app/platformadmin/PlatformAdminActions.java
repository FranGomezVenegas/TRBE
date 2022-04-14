/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app.platformadmin;

import static com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.MANDATORY_PARAMS_MAIN_SERVLET_PROCEDURE;
import functionaljavaa.platformadmin.PlatformAdminEnums;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class PlatformAdminActions extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);     
        
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false, true);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        InternalMessage actionDiagnoses=null;

        PlatformAdminEnums.PlatformAdminAPIActionsEndpoints endPoint = null;
        try{
            endPoint = PlatformAdminEnums.PlatformAdminAPIActionsEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
            return;                   
        }
        JsonObject jsonObject=null;
        String[] argList=new String[]{};
        LPAPIArguments[] arguments = endPoint.getArguments();
        for (LPAPIArguments curArg: arguments){
            argList=LPArray.addValueToArray1D(argList, curArg.getName());
        }
        argList=LPArray.addValueToArray1D(argList, MANDATORY_PARAMS_MAIN_SERVLET_PROCEDURE.split("\\|"));
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());  
        try (PrintWriter out = response.getWriter()) {
            ClassPlatformAdmin clss = new ClassPlatformAdmin(request, endPoint);
            Object[] diagnostic=clss.getDiagnostic();            
            if (diagnostic!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){ 
                InternalMessage diagnosticObj=clss.getDiagnosticObj();
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnosticObj.getMessageCodeObj(), diagnosticObj.getMessageCodeVariables());   
            }else{
                RelatedObjects rObj=RelatedObjects.getInstanceForActions();
//                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsAppProcData.TablesAppProcData.INSTRUMENTS.getTableName(), null);                
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, new Object[]{""}, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
            }           
        }catch(Exception e){   
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, LPPlatform.ApiErrorTraping.EXCEPTION_RAISED, new Object[]{e.getMessage()});   
            // Rdbms.closeRdbms();                   
            procReqInstance.killIt();
            String[] errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
        } finally {
            procReqInstance.killIt();
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
