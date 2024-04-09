/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.apis;

import com.labplanet.servicios.modulesample.ClassSampleQueriesController;
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
import module.monitoring.definition.ClassEnvMonQueriesController;
import module.monitoring.definition.ClassEnvMonSampleFrontendController;
import trazit.session.ProcedureRequestSession;
/**
 *
 * @author User
 */
public class MonitoringAPIqueries extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        String language = LPFrontEnd.setLanguage(request); 
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        try (PrintWriter out = response.getWriter()) {
            String actionName=procReqInstance.getActionName();           
            ClassEnvMonSampleFrontendController clssEnvMonSampleQueries=new ClassEnvMonSampleFrontendController(request, response, actionName.toUpperCase(), null, null, null, null);
            if (Boolean.FALSE.equals(clssEnvMonSampleQueries.getFunctionFound())){
                ClassSampleQueriesController clssSampleQueriesController=new ClassSampleQueriesController(request, response, actionName, null, null, null, null);
                if (Boolean.FALSE.equals(clssSampleQueriesController.getFunctionFound())){
                    ClassEnvMonQueriesController clssEnvMonQueriesController=new ClassEnvMonQueriesController(request, response, actionName, null, null, null);
                    if (Boolean.FALSE.equals(clssEnvMonQueriesController.getFunctionFound())){
                        procReqInstance.killIt();
                        LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());                                        
                    }
                    clssEnvMonQueriesController=null;
                }
                clssSampleQueriesController=null;
            }
            clssEnvMonSampleQueries=null;
    }catch(Exception e){      
        String exceptionMessage =e.getMessage();
        if (exceptionMessage==null){exceptionMessage="null exception";}
        response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
        procReqInstance.killIt();
        LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);      
    } finally {
       // release database resources
       try {
           procReqInstance.killIt();
        } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
       }
    }              
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
         try {
        processRequest(request, response);
         }catch(ServletException|IOException e){Logger.getLogger(e.getMessage());}
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
