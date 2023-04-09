/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import databases.features.Token;
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
import static trazit.globalvariables.GlobalVariables.DEFAULTLANGUAGE;
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

        procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, true);
        try (PrintWriter out = response.getWriter()) {               
            if (procReqInstance==null){
                LPFrontEnd.servletReturnResponseError(request, response, 
                    "Error", null, DEFAULTLANGUAGE, null);              
                return;
            }
            String sessionLang=procReqInstance.getLanguage();
            String errMsg=procReqInstance.getErrorMessage();
            if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, errMsg, null, sessionLang,null);
                return;
            }
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   

            Token token = new Token(finalToken);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, "Argument scriptId not found in the call", null, sessionLang, null);
                return;
            }                
            String dbName=request.getParameter("dbName");
            if (dbName==null){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, "Argument dbName not found in the call", null, sessionLang, null);
                return;
            }            
            String procInstanceName=request.getParameter("procInstanceName");
            if (procInstanceName==null){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, "Argument procInstanceName not found in the call", null, sessionLang, null);
                return;
            }            
            String coverageId=request.getParameter("coverageId");
            if (coverageId==null){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, "Argument coverageId not found in the call", null, sessionLang, null);
                return;
            }            
            //out.println("Running Testing Coverage for id "+coverageId);
            TestingCoverage tstCov=null;
            tstCov=new TestingCoverage(procInstanceName, Integer.valueOf(coverageId));  
            //out.println(tstCov.getJsonSummary());
            LPFrontEnd.servletReturnSuccess(request, response, tstCov.getJsonSummary());
        }
        finally{
            procReqInstance.killIt();
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
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
