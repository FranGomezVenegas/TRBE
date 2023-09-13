package com.labplanet.servicios.moduleenvmonit;

import module.monitoring.definition.ClassEnvMonSampleFrontend;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.modulesample.ClassSampleQueries;
import module.monitoring.definition.ClassEnvMonSampleFrontend.EnvMonSampleAPIqueriesEndpoints;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import com.labplanet.servicios.modulesample.SampleAPIParams.SampleAPIqueriesEndpoints;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import trazit.enums.EnumIntEndpoints;
import trazit.session.ProcedureRequestSession;

public class EnvMonitSampleAPIfrontend extends HttpServlet {
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        try (PrintWriter out = response.getWriter()) {
            EnumIntEndpoints endPoint = null;            
            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))){return;}
            
            try{ 
                endPoint = EnvMonSampleAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
                ClassEnvMonSampleFrontend clss=new ClassEnvMonSampleFrontend(request, (EnvMonSampleAPIqueriesEndpoints) endPoint);                    
                if (Boolean.TRUE.equals(clss.getIsSuccess())){
                    if (clss.getResponseContentJArr()!=null)//&&Boolean.FALSE.equals(clss.getResponseSuccessJArr().isEmpty()))
                        LPFrontEnd.servletReturnSuccess(request, response, clss.getResponseContentJArr());
                    if (clss.getResponseContentJObj()!=null)//&&Boolean.FALSE.equals(clss.getResponseSuccessJObj().isEmpty()))
                        LPFrontEnd.servletReturnSuccess(request, response, clss.getResponseContentJObj());
                }            
            }catch(Exception e){
                try{
                    SampleAPIqueriesEndpoints endPoint2 = SampleAPIParams.SampleAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
                    ClassSampleQueries clss=new ClassSampleQueries(request, response, endPoint2);
                    if (Boolean.TRUE.equals(clss.getIsSuccess())){
                        if (clss.getResponseSuccessJArr()!=null)//&&Boolean.FALSE.equals(clss.getResponseSuccessJArr().isEmpty()))
                            LPFrontEnd.servletReturnSuccess(request, response, clss.getResponseSuccessJArr());
                        if (clss.getResponseSuccessJObj()!=null)//&&Boolean.FALSE.equals(clss.getResponseSuccessJObj().isEmpty()))
                            LPFrontEnd.servletReturnSuccess(request, response, clss.getResponseSuccessJObj());
                    }            
                }catch(Exception e2){               
                    procReqInstance.killIt();
                    RequestDispatcher rd = request.getRequestDispatcher(SampleAPIParams.SERVLET_FRONTEND_URL);
                    rd.forward(request, response);                                   
                    return;                   
                }                
            }
        }catch(Exception e){      
            String exceptionMessage =e.getMessage();
            procReqInstance.killIt();
            if (exceptionMessage==null){exceptionMessage="null exception";}
            response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);      
        } finally {
            procReqInstance.killIt();
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
