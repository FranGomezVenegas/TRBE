/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.moduleenvmonit.ClassEnvMonSampleFrontend.EnvMonSampleAPIFrontendEndpoints;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class EnvMonitSampleAPIfrontend extends HttpServlet {

    /**
     *
     */

    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */                
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        if (procReqInstance.getHasErrors()) return;
        String actionName=procReqInstance.getActionName();

        try (PrintWriter out = response.getWriter()) {
            EnvMonSampleAPIFrontendEndpoints endPoint = null;
            try{
                endPoint = EnvMonSampleAPIFrontendEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                RequestDispatcher rd = request.getRequestDispatcher(SampleAPIParams.SERVLET_FRONTEND_URL);
                rd.forward(request, response);                                   
//                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}
            ClassEnvMonSampleFrontend clss=new ClassEnvMonSampleFrontend(request, endPoint);
            if (clss.getIsSuccess()){
                if (clss.getResponseContentJArr()!=null)
                    LPFrontEnd.servletReturnSuccess(request, response, (JSONArray) clss.getResponseContentJArr());
                if (clss.getResponseContentJObj()!=null)
                    LPFrontEnd.servletReturnSuccess(request, response, (JSONObject) clss.getResponseContentJObj());
            }else
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, (Object[]) clss.getResponseError());              
        }catch(Exception e){      
            String exceptionMessage =e.getMessage();
            if (exceptionMessage==null){exceptionMessage="null exception";}
            response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null);      
        } finally {
            // release database resources
            try {
                procReqInstance.killIt();
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
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
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
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
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
