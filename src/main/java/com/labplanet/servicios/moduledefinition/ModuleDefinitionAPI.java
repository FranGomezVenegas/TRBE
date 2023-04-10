/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduledefinition;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.features.Token;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ModuleDefinitionAPI extends HttpServlet {

    public enum ModuleDefinitionpParametersEndpoints {
        PROCEDURE_NAME("procedureName"), PROCEDURE_VERSION("procedureVersion"), SCHEMA_PREFIX("procInstanceName"), MODULE_NAME("moduleName"), USER_NAME("userName"), ROLE_NAME("roleName"),
        UOM_NAME("uomName"), UOM_IMPORT_TYPE("importType");
        private ModuleDefinitionpParametersEndpoints(String name) {this.name = name;}
        public String getName() {return this.name;}
        private final String name;
        public LPAPIArguments[] getArguments() {return new LPAPIArguments[]{};}
    }

    public enum ModuleDefinitionAPIactionsEndpoints implements EnumIntEndpoints {
        DOC_API_ENDPOINTS_IN_DB("DOC_API_ENDPOINTS_IN_DB", "documentedApiEndpointsInDb_success: <*1*>", new LPAPIArguments[]{},
                null, null),
        DOC_API_BUSINESS_RULES_IN_DB("DOC_API_BUSINESS_RULES_IN_DB", "documentedApiBusinessRulesInDb_success", new LPAPIArguments[]{},
                null, null),
        DOC_API_ERROR_MESSAGE_CODES_IN_DB("DOC_API_ERROR_MESSAGE_CODES_IN_DB", "documentedApiMessageCodesInDb_success", new LPAPIArguments[]{},
                null, null),
        DOC_API_AUDIT_EVENTS_IN_DB("DOC_API_AUDIT_EVENTS_IN_DB", "documentedApiMessageCodesInDb_success", new LPAPIArguments[]{},
                null, null),
        DOC_API_ALL_IN_ONE("DOC_API_ALL_IN_ONE", "documentedApiMessageCodesInDb_success", new LPAPIArguments[]{},
                null, null);

        private ModuleDefinitionAPIactionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, String devComment, String devCommentTag) {
            this.name = name;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);
        }

        public HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex) {
            HashMap<HttpServletRequest, Object[]> hm = new HashMap<>();
            Object[] argValues = new Object[0];
            for (LPAPIArguments curArg : this.arguments) {
                argValues = LPArray.addValueToArray1D(argValues, curArg.getName() + ":" + getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }
            hm.put(request, argValues);
            return hm;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getSuccessMessageCode() {
            return this.successMessageCode;
        }

        @Override
        public LPAPIArguments[] getArguments() {
            return arguments;
        }

        @Override
        public String getApiUrl() {
            return GlobalVariables.ApiUrls.MODULE_DEFINITION_ACTIONS.getUrl();
        }
        private final String name;
        private final String successMessageCode;
        private final LPAPIArguments[] arguments;

        @Override
        public String getDeveloperComment() {
            return this.devComment;
        }

        @Override
        public String getDeveloperCommentTag() {
            return this.devCommentTag;
        }
        private final String devComment;
        private final String devCommentTag;

        @Override
        public JsonArray getOutputObjectTypes() {
            return EndPointsToRequirements.endpointWithNoOutputObjects;
        }
    }
    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME + "|" + GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN;

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
        request = LPHttp.requestPreparation(request);
        response = LPHttp.responsePreparation(response);
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForDocumentation(request, response);

        String language = LPFrontEnd.setLanguage(request);

        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }
        String actionName = procReqInstance.getActionName();
        String finalToken = procReqInstance.getTokenString();

        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())) {
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode(), null, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }
        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
            return;
        }
//        Connection con = Rdbms.createTransactionWithSavePoint();        
        //Rdbms.setTransactionId(schemaConfigName);
        ModuleDefinitionAPIactionsEndpoints endPoint = null;
        try {
            endPoint = ModuleDefinitionAPIactionsEndpoints.valueOf(actionName.toUpperCase());
        } catch (Exception e) {
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }
        areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }
        try (PrintWriter out = response.getWriter()) {
            ClassTrazitCodeDoc clss = new ClassTrazitCodeDoc(request, response, endPoint);
            Object[] diagnostic = clss.getDiagnostic();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) {
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clss.getMessageDynamicData());
            } else {
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, clss.getMessageDynamicData(), clss.getRelatedObj().getRelatedObject());
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
            }

        } catch (Exception e) {
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, LPPlatform.ApiErrorTraping.EXCEPTION_RAISED, new Object[]{e.getMessage()});
        } finally {
            // release database resources
            try {
                procReqInstance.killIt();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (ServletException | IOException e) {
            Logger.getLogger(e.getMessage());
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
