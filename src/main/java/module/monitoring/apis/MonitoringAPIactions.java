/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.apis;

import com.labplanet.servicios.app.InvestigationAPI;
import com.labplanet.servicios.moduleenvmonit.EnvMonAPI.EnvMonAPIactionsEndpoints;
import com.labplanet.servicios.moduleenvmonit.EnvMonIncubatorAPIactions.EnvMonIncubatorAPIactionsEndpoints;
import com.labplanet.servicios.moduleenvmonit.EnvMonProdLotAPI.EnvMonProdLotAPIactionsEndpoints;
import com.labplanet.servicios.moduleenvmonit.EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.labplanet.servicios.modulesample.ClassSample;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import functionaljavaa.investigation.ClassInvestigation;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import static lbplanet.utilities.LPHttp.moduleActionsSingleAPI;
import lbplanet.utilities.LPPlatform;
import module.monitoring.definition.ClassEnvMon;
import module.monitoring.definition.ClassEnvMonIncubator;
import module.monitoring.definition.ClassEnvMonProdLot;
import module.monitoring.definition.ClassEnvMonSample;
import org.json.simple.JSONObject;
import trazit.enums.ActionsEndpointPair;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class MonitoringAPIactions extends HttpServlet {
/*
    public interface EndpointHandler {
        boolean endpointExists();
        Object[] getDiagnostic();
        JSONObject createResponse();
    }

public class EndpointHandlerFactory {
    public static EndpointHandler getHandler(Enum<?> endpointEnum, HttpServletRequest request) {
        // Implement logic to return the appropriate handler instance
        // Example:
        switch(endpointEnum.getClass().getSimpleName()) {
            case "EnvMonAPIactionsEndpoints":
                return new ClassEnvMon(request, (EnvMonAPIactionsEndpoints) endpointEnum);
            // Add cases for other endpoint enums
        }
        return null;
    }
}
*/    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response){        
        ActionsEndpointPair[] actionEndpointArr = GlobalVariables.TrazitModules.MONITORING.getActionsEndpointPair(); //implements ActionsClass
        moduleActionsSingleAPI(request, response, actionEndpointArr, this.getServletName());
    }
    
    protected void processRequest2(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request = LPHttp.requestPreparation(request);
        response = LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())) {
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);
            return;
        }
        try ( PrintWriter out = response.getWriter()) {
            EnvMonAPIactionsEndpoints endPoint = null;
            try {
                endPoint = EnvMonAPIactionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase());
            } catch (Exception e) {
                EnvMonSampleAPIactionsEndpoints endPointEnvMonSmp=null;
                try{
                    endPointEnvMonSmp = EnvMonSampleAPIactionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase());
                } catch (Exception ev) {
                    EnvMonProdLotAPIactionsEndpoints endPointEnvMonProdLot = null;
                    try{
                        endPointEnvMonProdLot = EnvMonProdLotAPIactionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase());
                    } catch (Exception epr) {      
                        EnvMonIncubatorAPIactionsEndpoints endPointEnvMonIncub = null;
                        try{
                            endPointEnvMonIncub = EnvMonIncubatorAPIactionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase());
                        } catch (Exception eincub) {                    
                                SampleAPIParams.SampleAPIactionsEndpoints endPointSmp = null;
                                try {
                                    endPointSmp = SampleAPIParams.SampleAPIactionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase());
                                } catch (Exception er) {
                                    try {
                                        InvestigationAPI.InvestigationAPIactionsEndpoints endPoint2 = InvestigationAPI.InvestigationAPIactionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase());
                                        Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint2.getArguments());
                                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
                                            procReqInstance.killIt();
                                            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{procReqInstance.getActionName(), this.getServletName()}, procReqInstance.getLanguage(), LPPlatform.ApiErrorTraping.class.getSimpleName());
                                            return;
                                        }
                                        ClassInvestigation clssInv = new ClassInvestigation(request, InvestigationAPI.InvestigationAPIactionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase()));
                                        if (Boolean.TRUE.equals(clssInv.getEndpointExists())) {
                                            Object[] diagnostic = clssInv.getDiagnostic();
                                            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) {
                                                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clssInv.getMessageDynamicData());
                                            } else {
                                                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(InvestigationAPI.InvestigationAPIactionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase()), clssInv.getMessageDynamicData(), clssInv.getRelatedObj().getRelatedObject());
                                                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
                                            }
                                        }
                                    } catch (Exception er2) {
                                        procReqInstance.killIt();
                                        LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{procReqInstance.getActionName(), this.getServletName()}, procReqInstance.getLanguage(), LPPlatform.ApiErrorTraping.class.getSimpleName());
                                        return;                        
                                    }                    
                                }
                                ClassSample clssSmp = new ClassSample(request, endPointSmp);
                                if (Boolean.TRUE.equals(clssSmp.getEndpointExists())) {
                                    Object[] diagnostic = clssSmp.getDiagnostic();
                                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) {
                                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clssSmp.getMessageDynamicData());
                                    } else {
                                        JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPointSmp, clssSmp.getMessageDynamicData(), clssSmp.getRelatedObj().getRelatedObject());
                                        LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
                                    }
                                }
                        }
                        ClassEnvMonIncubator clssIncub = new ClassEnvMonIncubator(request, endPointEnvMonIncub);
                        if (Boolean.TRUE.equals(clssIncub.getEndpointExists())) {
                            Object[] diagnostic = clssIncub.getDiagnostic();
                            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) {
                                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clssIncub.getMessageDynamicData());
                            } else {
                                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPointEnvMonIncub, clssIncub.getMessageDynamicData(), clssIncub.getRelatedObj().getRelatedObject());
                                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
                            }
                    }                    }
                    ClassEnvMonProdLot clssProdLot = new ClassEnvMonProdLot(request, endPointEnvMonProdLot);
                    if (Boolean.TRUE.equals(clssProdLot.getEndpointExists())) {
                        Object[] diagnostic = clssProdLot.getDiagnostic();
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) {
                            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clssProdLot.getMessageDynamicData());
                        } else {
                            JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPointEnvMonProdLot, clssProdLot.getMessageDynamicData(), clssProdLot.getRelatedObj().getRelatedObject());
                            LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
                        }
                    }                
                }
                ClassEnvMonSample clssSmp = new ClassEnvMonSample(request, endPointEnvMonSmp);
                if (Boolean.TRUE.equals(clssSmp.getEndpointExists())) {
                    Object[] diagnostic = clssSmp.getDiagnostic();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) {
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clssSmp.getMessageDynamicData());
                    } else {
                        JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPointEnvMonSmp, clssSmp.getMessageDynamicData(), clssSmp.getRelatedObj().getRelatedObject());
                        LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
                    }
                }
            }
            if (endPoint == null) {
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{procReqInstance.getActionName(), this.getServletName()}, procReqInstance.getLanguage(), LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }
            Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, procReqInstance.getLanguage(), LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }

            ClassEnvMon clss = new ClassEnvMon(request, endPoint);

            InternalMessage diagnosticObj = null; //clss.getDiagnosticObj();
            Object[] diagnostic = clss.getDiagnostic();

            if (diagnosticObj != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosticObj.getDiagnostic())) {
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnosticObj.getMessageCodeObj(), diagnosticObj.getMessageCodeVariables());
            } else if (diagnosticObj == null && LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) {
                procReqInstance.killIt();
                LPFrontEnd.responseError(diagnostic);
            } else {
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, clss.getMessageDynamicData(), clss.getRelatedObj().getRelatedObject());
                procReqInstance.killIt();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
            }
        } catch (Exception e) {
            procReqInstance.killIt();
            String[] errObject = new String[]{e.getMessage()};
            LPFrontEnd.responseError(errObject);
        } finally {
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
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
            processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
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
