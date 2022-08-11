/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios;

import lbplanet.utilities.LPHttp;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import trazit.globalvariables.GlobalVariables;
import trazit.session.DbLogSummary;
import trazit.session.ProcedureRequestSession;


/**
 *
 * @author Administrator
 */
public class ResponseSuccess extends HttpServlet {
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)           {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response); 
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        String actionName= procReqInstance.getActionName();
        try (PrintWriter out = response.getWriter()) {
            DbLogSummary dbLogSummary = ProcedureRequestSession.getInstanceForQueries(null, null, null).getDbLogSummary();
            String responseMsg="";
            String toJSONString ="";
            if (actionName!=null && !actionName.toUpperCase().contains("DEPLOY") && dbLogSummary!=null && dbLogSummary.hadAnyFailure()){
                //response.getWriter().write("Transaction failed! "+dbLogSummary.getFailureStatement());
                Object[] addValueToArray1D = LPArray.addValueToArray1D(new Object[]{dbLogSummary.getFailureStatement()}, dbLogSummary.getFailureStatementData());
                if (ProcedureRequestSession.getInstanceForQueries(null, null, null).getIsTransactional()){
                    toJSONString = LPFrontEnd.responseJSONDiagnosticLPFalse("fullTransactionNotPossibleByErrors", addValueToArray1D).toJSONString();
                }else
                    toJSONString=LPFrontEnd.responseJSONDiagnosticLPFalse("error on updating database, not transactional", addValueToArray1D).toJSONString();
                response.getWriter().write(toJSONString);
                request=null;
                response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
            }else
                responseMsg=(String) request.getAttribute(GlobalVariables.ServletsResponse.SUCCESS.getAttributeName());
            response.getWriter().write(responseMsg);
            Response.ok().build();    
        } catch (IOException ex) {
            Logger.getLogger(ResponseSuccess.class.getName()).log(Level.SEVERE, null, ex);
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
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  {
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
