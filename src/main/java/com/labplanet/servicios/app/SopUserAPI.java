package com.labplanet.servicios.app;

import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import databases.TblsData;
import databases.features.Token;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.sop.UserSop;
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
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class SopUserAPI extends HttpServlet {

    public enum SopUserAPIactionsEndpoints implements EnumIntEndpoints {
        CERTIFUSER_SOP_MARK_AS_COMPLETED("CERTIFUSER_SOP_MARK_AS_COMPLETED", "certifUser_markAsCompleted_success", new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        CERTIFUSER_READ_AND_UNDERSTOOD("CERTIFUSER_READ_AND_UNDERSTOOD", "certifUser_markAsReadAndUnderstood_success", new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        CERTIFUSER_UNDERSTOOD_AND_SENDTOREVIEWER("CERTIFUSER_UNDERSTOOD_AND_SENDTOREVIEWER", "certifUser_sendToReviewer_success", new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        CERTIFUSER_REVIEWER_SIGN("CERTIFUSER_REVIEWER_SIGN", "certifUser_reviewerSigned_success", new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        CERTIFUSER_TRAINING_REQUIRED("CERTIFUSER_TRAINING_REQUIRED", "certifUser_trainingRequired_success", new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        ADD_SOP_TO_USER("ADD_SOP_TO_USER", "UserSop_sopAddedToUser", new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),;

        private SopUserAPIactionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name = name;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.outputObjectTypes = outputObjectTypes;
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
        @Override public String getEntity() {return "sop";}
        @Override
        public JsonArray getOutputObjectTypes() {
            return outputObjectTypes;
        }

        @Override
        public String getApiUrl() {
            return ApiUrls.SOPS_ACTIONS.getUrl();
        }

        /**
         * @return the arguments
         */
        @Override
        public LPAPIArguments[] getArguments() {
            return arguments;
        }
        private final String name;
        private final String successMessageCode;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;

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

        String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME);
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false, true);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())) {
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);
            return;
        }
        String actionName = procReqInstance.getActionName();
        String language = procReqInstance.getLanguage();

        try (PrintWriter out = response.getWriter()) {
            SopUserAPIactionsEndpoints endPoint = null;
            try {
                endPoint = SopUserAPIactionsEndpoints.valueOf(actionName.toUpperCase());
            } catch (Exception e) {
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class
                        .getSimpleName());
                return;
            }
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            Token token = procReqInstance.getToken();
            RelatedObjects rObj = RelatedObjects.getInstanceForActions();
            Object[] userSopDiagnostic = new Object[0];
            Object[] messageDynamicData = new Object[]{};
            switch (endPoint) {
                case CERTIFUSER_READ_AND_UNDERSTOOD:
                case CERTIFUSER_SOP_MARK_AS_COMPLETED:
                    String sopName = argValues[0].toString();
                    String userName = token.getUserName();
                    userSopDiagnostic = UserSop.userSopMarkedAsCompletedByUser(procInstanceName, userName, sopName,
                            Boolean.valueOf(LPNulls.replaceNull(request.getParameter(GlobalAPIsParams.REQUEST_PARAM_IS_TESTING))));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userSopDiagnostic[0].toString())) {
                        messageDynamicData = new Object[]{sopName, userName, procInstanceName};
                    } else {
                        messageDynamicData = new Object[]{sopName};
                    }
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.USER_SOP.getTableName(), sopName);
                    break;
                case CERTIFUSER_UNDERSTOOD_AND_SENDTOREVIEWER:
                    sopName = argValues[0].toString();
                    userName = token.getUserName();
                    userSopDiagnostic = UserSop.userSopMarkedAsCompletedByUserAndReviewerSignPending(procInstanceName, userName, sopName,
                            Boolean.valueOf(LPNulls.replaceNull(request.getParameter(GlobalAPIsParams.REQUEST_PARAM_IS_TESTING))));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userSopDiagnostic[0].toString())) {
                        messageDynamicData = new Object[]{sopName, userName, procInstanceName};
                    } else {
                        messageDynamicData = new Object[]{sopName};
                    }
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.USER_SOP.getTableName(), sopName);
                    break;
                case CERTIFUSER_REVIEWER_SIGN:
                    sopName = argValues[0].toString();
                    userName = token.getUserName();
                    userSopDiagnostic = UserSop.userSopSignedByReviewer(procInstanceName, userName, sopName,
                            Boolean.valueOf(LPNulls.replaceNull(request.getParameter(GlobalAPIsParams.REQUEST_PARAM_IS_TESTING))));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userSopDiagnostic[0].toString())) {
                        messageDynamicData = new Object[]{sopName, userName, procInstanceName};
                    } else {
                        messageDynamicData = new Object[]{sopName};
                    }
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.USER_SOP.getTableName(), sopName);
                    break;
                case CERTIFUSER_TRAINING_REQUIRED:
                    userSopDiagnostic = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
                    break;
                default:
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class
                            .getSimpleName());
                    return;
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userSopDiagnostic[0].toString())) {
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, userSopDiagnostic[4].toString(), messageDynamicData);
            } else {
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, messageDynamicData, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);

            }
        } catch (Exception e) {
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.EXCEPTION_RAISED.getErrorCode(), new Object[]{e.getMessage(), this.getServletName()}, language, LPPlatform.ApiErrorTraping.class
                    .getSimpleName());
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
