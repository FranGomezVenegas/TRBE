/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulebatch;

import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPHttp;
import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.features.Token;
import functionaljavaa.businessrules.ActionsControl;
import functionaljavaa.businessrules.BusinessRules;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
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
import lbplanet.utilities.LPNulls;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import trazit.session.InternalMessage;

/**
 *
 * @author Administrator
 */
public class BatchAPI extends HttpServlet {

    static final String COMMON_PARAMS = "incidentId|note";

    public enum BatchAPIactionsEndpoints implements EnumIntEndpoints {
        CREATE_BATCH_ARRAY("CREATE_BATCH_ARRAY", "createBatchArray_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)},
                null, null),
        LOAD_BATCH_ARRAY("LOAD_BATCH_ARRAY", "loadBatchArray_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)},
                null, null),;

        private BatchAPIactionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, String devComment, String devCommentTag) {
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
        @Override public String getEntity() {return "batch";}
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
            return ApiUrls.BATCH_ARRAY_ACTIONS.getUrl();
        }
        private final String name;
        private final String successMessageCode;
        private final LPAPIArguments[] arguments;

        @Override
        public JsonArray getOutputObjectTypes() {
            return EndPointsToRequirements.endpointWithNoOutputObjects;
        }

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
    }

    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME + "|" + GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN + "|" + GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME + "|" + GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

    /**
     *
     */
    public static final String ERRORMSG_ERROR_STATUS_CODE = "Error Status Code";

    /**
     *
     */
    public static final String ERRORMSG_MANDATORY_PARAMS_MISSING = "API Error Message: There are mandatory params for this API method not being passed";

    /**
     *
     */
    public static final String MANDATORY_PARAMS_CREATEBATCHARRAY = "batchName|batchTemplate|batchTemplateVersion|numRows|numCols";

    /**
     *
     */
    public static final String MANDATORY_PARAMS_LOADBATCHARRAY = "batchName";

    /**
     *
     */
    public static final String PARAMS_BATCH_NAME = "batchName";

    /**
     *
     */
    public static final String PARAMS_BATCH_TEMPLATE = "batchTemplate";

    /**
     *
     */
    public static final String PARAMS_BATCH_TEMPLATE_VERSION = "batchTemplateVersion";

    /**
     *
     */
    public static final String PARAMS_BATCH_NUM_ROWS = "numRows";

    /**
     *
     */
    public static final String PARAMS_BATCH_NUM_COLS = "numCols";

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

        String language = LPFrontEnd.setLanguage(request);

        Connection con = null;

        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }
        String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME);
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);

        Token token = new Token(finalToken);
        BatchAPIactionsEndpoints endPoint = null;
        try {
            endPoint = BatchAPIactionsEndpoints.valueOf(actionName.toUpperCase());
        } catch (Exception e) {
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }
        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
            return;
        }
        try (PrintWriter out = response.getWriter()) {
            BusinessRules bi = new BusinessRules(procInstanceName, null);
            InternalMessage actionEnabled = ActionsControl.procActionEnabled(procInstanceName, token, actionName, bi, false);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled.getDiagnostic())) {
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, actionEnabled.getMessageCodeObj(), actionEnabled.getMessageCodeVariables());
                return;
            }
            actionEnabled = ActionsControl.procUserRoleActionEnabled(procInstanceName, token.getUserRole(), actionName, bi);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled.getDiagnostic())) {
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, actionEnabled.getMessageCodeObj(), actionEnabled.getMessageCodeVariables());
                return;
            }
            switch (endPoint) {
                case CREATE_BATCH_ARRAY:
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_CREATEBATCHARRAY.split("\\|"));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
                        LPFrontEnd.servletReturnResponseError(request, response,
                                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                    }
                    break;
                case LOAD_BATCH_ARRAY:
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_LOADBATCHARRAY.split("\\|"));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
                        LPFrontEnd.servletReturnResponseError(request, response,
                                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                    }
                    break;
                default:
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            }
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BatchAPI.class.getName()).log(Level.SEVERE, null, ex);
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
