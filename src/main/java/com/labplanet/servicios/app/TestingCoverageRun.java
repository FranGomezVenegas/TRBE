/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import databases.Token;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import functionaljavaa.testingscripts.TestingCoverage;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class TestingCoverageRun extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProcedureRequestSession procReqInstance = null;
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);        
        StringBuilder fileContentBuilder = new StringBuilder(0);        
        String language = LPFrontEnd.setLanguage(request); 

        try (PrintWriter out = response.getWriter()) {               
            procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, true);
            if (procReqInstance==null){
                LPFrontEnd.servletReturnResponseError(request, response, 
                    "Error", null, procReqInstance.getLanguage());              
                return;
            }
            String sessionLang=procReqInstance.getLanguage();
            String errMsg=procReqInstance.getErrorMessage();
            if (procReqInstance.getHasErrors()){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, errMsg, null, sessionLang);              
                return;
            }
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   

            Token token = new Token(finalToken);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, "Argument scriptId not found in the call", null, sessionLang);                              
                return;
            }                
            String dbName=request.getParameter("dbName");
            if (dbName==null){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, "Argument dbName not found in the call", null, sessionLang);                              
                return;
            }            
            String procInstanceName=request.getParameter("procInstanceName");
            if (procInstanceName==null){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, "Argument procInstanceName not found in the call", null, sessionLang);                              
                return;
            }            
            String coverageId=request.getParameter("coverageId");
            if (coverageId==null){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, "Argument coverageId not found in the call", null, sessionLang);                              
                return;
            }            
            //out.println("Running Testing Coverage for id "+coverageId);
            TestingCoverage tstCov=null;
            tstCov=new TestingCoverage(procInstanceName, Integer.valueOf(coverageId));  
            //out.println(tstCov.getJsonSummary());
            LPFrontEnd.servletReturnSuccess(request, response, tstCov.getJsonSummary());
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
