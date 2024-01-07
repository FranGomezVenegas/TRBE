/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.apis;

import com.labplanet.servicios.app.CertifyAnalysisMethodAPI;
import com.labplanet.servicios.app.InvestigationAPI;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import module.inspectionlot.rawmaterial.definition.ClassInspLotRMactions;
import com.labplanet.servicios.modulesample.ClassSample;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import functionaljavaa.certification.ClassAnalysisMehtodCertif;
import functionaljavaa.investigation.ClassInvestigation;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.InspLotRMAPIactionsEndpoints;
import modules.masterdata.analysis.ClassAnalysis;
import modules.masterdata.analysis.MasterDataAnalysisEnums;
import modules.masterdata.spec.ClassSpec;
import modules.masterdata.spec.MasterDataSpecEnums;
import org.json.simple.JSONObject;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class InspLotRMAPIactions extends HttpServlet {

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
        request = LPHttp.requestPreparation(request);
        response = LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())) {
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);
            return;
        }
        try ( PrintWriter out = response.getWriter()) {
            InspLotRMAPIactionsEndpoints endPoint = null;
            try {
                endPoint = InspLotRMAPIactionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase());
            } catch (Exception e) {
                SampleAPIParams.SampleAPIactionsEndpoints endPointSmp = null;
                try {
                    endPointSmp = SampleAPIParams.SampleAPIactionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase());
                } catch (Exception er) {
                    CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints endPointMethodCertif = null;
                    try {
                        endPointMethodCertif = CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase());
                    } catch (Exception er2) {                    
                        MasterDataSpecEnums.MasterDataSpecActionsEndpoints endPointMasterDataSpec = null;
                        try {
                            endPointMasterDataSpec = MasterDataSpecEnums.MasterDataSpecActionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase());
                        } catch (Exception er3) {
                            MasterDataAnalysisEnums.MasterDataAnalysisActionsEndpoints endPointMasterDataAna = null;
                            try {
                                endPointMasterDataAna = MasterDataAnalysisEnums.MasterDataAnalysisActionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase());
                            } catch (Exception er4) {
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
                                } catch (Exception er5) {
                                    procReqInstance.killIt();
                                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{procReqInstance.getActionName(), this.getServletName()}, procReqInstance.getLanguage(), LPPlatform.ApiErrorTraping.class.getSimpleName());
                                    return;                        
                                }
                            }
                            ClassAnalysis clssMasterDataAna = new ClassAnalysis(request, response, endPointMasterDataAna);
                            if (Boolean.TRUE.equals(clssMasterDataAna.getEndpointExists())) {
                                InternalMessage diagnostic = clssMasterDataAna.getDiagnosticObj();
                                if (diagnostic!=null&&LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic.getDiagnostic())) {
                                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic.getMessageCodeObj(), diagnostic.getMessageCodeVariables());
                                } else {
                                    JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPointMasterDataAna, clssMasterDataAna.getMessageDynamicData(), clssMasterDataAna.getRelatedObj().getRelatedObject());
                                    LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
                                }
                            }
                        }
                        ClassSpec clssMasterDataSpec = new ClassSpec(request, response, endPointMasterDataSpec);
                        if (Boolean.TRUE.equals(clssMasterDataSpec.getEndpointExists())) {
                            InternalMessage diagnostic = clssMasterDataSpec.getDiagnosticObj();
                            if (diagnostic!=null&&LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic.getDiagnostic())) {
                                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic.getMessageCodeObj(), diagnostic.getMessageCodeVariables());
                            } else {
                                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPointMasterDataSpec, clssMasterDataSpec.getMessageDynamicData(), clssMasterDataSpec.getRelatedObj().getRelatedObject());
                                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
                            }
                        }
                    }
                    ClassAnalysisMehtodCertif clssMethodCertif = new ClassAnalysisMehtodCertif(request, response, endPointMethodCertif);
                    if (Boolean.TRUE.equals(clssMethodCertif.getEndpointExists())) {
                        Object[] diagnostic = clssMethodCertif.getDiagnostic();
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) {
                            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clssMethodCertif.getMessageDynamicData());
                        } else {
                            JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPointSmp, clssMethodCertif.getMessageDynamicData(), clssMethodCertif.getRelatedObj().getRelatedObject());
                            LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
                        }
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

            ClassInspLotRMactions clss = new ClassInspLotRMactions(request, endPoint);

            InternalMessage diagnosticObj = clss.getDiagnosticObj();
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
        try {
            processRequest(request, response);
        } catch (ServletException | IOException e) {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (ServletException | IOException e) {
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
