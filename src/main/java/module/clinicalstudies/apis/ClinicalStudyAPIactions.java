/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package module.clinicalstudies.apis;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import trazit.enums.ActionsEndpointPair;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables.TrazitModules;
import trazit.session.ActionsServletCommons;
import static trazit.session.ActionsServletCommons.publishResult;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ClinicalStudyAPIactions extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response){
        
/*        ActionsEndpointPair[] actionEndpointArr = new ActionsEndpointPair[]{
            new ActionsEndpointPair("module.clinicalstudies.apis.GenomaProjectAPI$GenomaProjectAPIactionsEndPoints", "module.clinicalstudies.logic.ClassProject"),
            new ActionsEndpointPair("module.clinicalstudies.apis.GenomaStudyAPI$GenomaStudyAPIactionsEndPoints", "module.clinicalstudies.logic.ClassStudy")
        };*/
        ActionsEndpointPair[] actionEndpointArr = TrazitModules.CLINICAL_STUDIES.getActionsEndpointPair();
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);     
        
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false, false);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            if (procReqInstance.getErrorMessageCodeObj()!=null)
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, procReqInstance.getErrorMessageCodeObj(), procReqInstance.getErrorMessageVariables());                   
            else
                LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        EnumIntEndpoints endPoint = null;
        try (PrintWriter out = response.getWriter()) {
            
            ActionsServletCommons clss=new ActionsServletCommons(request, actionEndpointArr, actionName);
            if (clss.getEndpointFound()){
                publishResult(request, response, procReqInstance, clss.getEndpointObj(), 
                    clss.getActionClassRun().getDiagnostic(), clss.getActionClassRun().getDiagnosticObj(), 
                    clss.getActionClassRun().getMessageDynamicData(), 
                    clss.getActionClassRun().getRelatedObj());
            }else{
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;                
            }
        }catch(Exception e){  
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        } finally {
            // release database resources
            try {           
                procReqInstance.killIt();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
             {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
             {
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
