package com.labplanet.servicios.app;

import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import databases.TblsData;
import modules.masterdata.analysis.ConfigAnalysisStructure;
import functionaljavaa.certification.AnalysisMethodCertif;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
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
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.session.ResponseMessages;

/**
 *
 * @author Administrator
 */
public class CertifyAnalysisMethodAPI extends HttpServlet {

    public enum CertifyAnalysisMethodAPIactionsEndpoints implements EnumIntEndpoints {
        CERTIFY_ASSIGN_METHOD_TO_USER("CERTIFY_ASSIGN_METHOD_TO_USER", "certificationAnalysisMethodAssigned_success", "CERTIF_ADDED_TO_USER", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TRAINING_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName()).build()).build(),
                 null, null),
        CERTIFY_START_USER_METHOD("CERTIFY_START_USER_METHOD", "startCertification_success", "CERTIF_STARTED", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 8)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName()).build()).build(),
                 null, null),
        CERTIFY_COMPLETE_CERTIFIED_USER_METHOD("CERTIFY_COMPLETE_CERTIFIED_USER_METHOD", "completeCertifiedCertification_success", "CERTIF_COMPLETED", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 8)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName()).build()).build(),
                 null, null),
        CERTIFY_COMPLETE_NOT_CERTIFIED_USER_METHOD("CERTIFY_COMPLETE_NOT_CERTIFIED_USER_METHOD", "completeNotCertifiedCertification_success", "CERTIF_COMPLETED", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 8)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName()).build()).build(),
                 null, null),
        CERTIFY_REVOKE_USER_METHOD("CERTIFY_REVOKE_USER_METHOD", "revokeCertification_success", "CERTIF_REVOKED", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 8)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName()).build()).build(),
                 null, null),
        USER_MARKIT_AS_COMPLETED("USER_MARKIT_AS_COMPLETED", "appAnaMethCertifUser_markAsCompleted_success", "",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6), //    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7)
            }, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        CERTIFUSER_READ_AND_UNDERSTOOD("CERTIFUSER_READ_AND_UNDERSTOOD", "certifUser_markAsReadAndUnderstood_success", "",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)},
                EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        CERTIFUSER_UNDERSTOOD_AND_SENDTOREVIEWER("CERTIFUSER_UNDERSTOOD_AND_SENDTOREVIEWER", "certifUser_sendToReviewer_success", "",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        CERTIFUSER_TRAINING_REQUIRED("CERTIFUSER_TRAINING_REQUIRED", "certifUser_trainingRequired_succes", "",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),;

        private CertifyAnalysisMethodAPIactionsEndpoints(String name, String successMessageCode, String audtEv, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name = name;
            this.successMessageCode = successMessageCode;
            this.auditEvent = audtEv;
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

        public String getAuditEvent() {
            return this.auditEvent;
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
        public JsonArray getOutputObjectTypes() {
            return outputObjectTypes;
        }

        @Override
        public LPAPIArguments[] getArguments() {
            return arguments;
        }

        @Override
        public String getApiUrl() {
            return ApiUrls.CERTIFY_ANALYSISMETHODS_ACTIONS.getUrl();
        }
        @Override public String getEntity() {return "certification";}
        private final String name;
        private final String successMessageCode;
        private final String auditEvent;
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
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false, false);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            if (procReqInstance.getErrorMessageCodeObj()!=null)
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, procReqInstance.getErrorMessageCodeObj(), procReqInstance.getErrorMessageVariables());                   
            else
                LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        String actionName = procReqInstance.getActionName();
        String language = procReqInstance.getLanguage();
        CertifyAnalysisMethodAPIactionsEndpoints endPoint = null;
        try {
            endPoint = CertifyAnalysisMethodAPIactionsEndpoints.valueOf(actionName.toUpperCase());
        } catch (Exception e) {
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
            ResponseMessages mainMessage = procReqInstance.getMessages();
            mainMessage.addMainForError(ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            return;
        }
        Object[] messageDynamicData = new Object[]{};
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        InternalMessage diagnostic = null;
        try (PrintWriter out = response.getWriter()) {
            String sopName = "";
            Integer trainingId = null;
            switch (endPoint) {
                case CERTIFY_ASSIGN_METHOD_TO_USER:
                    sopName = argValues[3].toString();
                    if (LPNulls.replaceNull(argValues[4]).toString().length()>0){
                        trainingId = (Integer) argValues[4];
                    }
                case CERTIFY_START_USER_METHOD:
                case CERTIFY_COMPLETE_CERTIFIED_USER_METHOD:
                case CERTIFY_COMPLETE_NOT_CERTIFIED_USER_METHOD:
                case CERTIFY_REVOKE_USER_METHOD:
                    String methodName = argValues[0].toString();
                    Integer methodVersion = (Integer) argValues[1];
                    String userName = argValues[2].toString();
                    if (actionName.equalsIgnoreCase(CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_ASSIGN_METHOD_TO_USER.getName())) {
                        diagnostic = AnalysisMethodCertif.newRecord(methodName, methodVersion, userName, sopName, trainingId);
                    }
                    if (actionName.equalsIgnoreCase(CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_START_USER_METHOD.getName())) {
                        diagnostic = AnalysisMethodCertif.startCertification(methodName, userName);
                    }
                    if (actionName.equalsIgnoreCase(CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_COMPLETE_CERTIFIED_USER_METHOD.getName())) {
                        diagnostic = AnalysisMethodCertif.completeCertificationCertified(methodName, userName);
                    }
                    if (actionName.equalsIgnoreCase(CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_COMPLETE_NOT_CERTIFIED_USER_METHOD.getName())) {
                        diagnostic = AnalysisMethodCertif.completeCertificationNotCertified(methodName, userName);
                    }
                    if (actionName.equalsIgnoreCase(CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_REVOKE_USER_METHOD.getName())) {
                        diagnostic = AnalysisMethodCertif.revokeCertification(methodName, userName);
                    }

                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic.getDiagnostic())) {
                        messageDynamicData = new Object[]{sopName, userName, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{methodName, userName, procReqInstance.getProcedureInstance()};
                    }
                    if (actionName.equalsIgnoreCase(CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_ASSIGN_METHOD_TO_USER.getName())) {
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), diagnostic.getNewObjectId());
                    }

                    break;
                case CERTIFUSER_READ_AND_UNDERSTOOD:
                case USER_MARKIT_AS_COMPLETED:
                    methodName = argValues[0].toString();
                    userName = procReqInstance.getToken().getUserName();
                    diagnostic = AnalysisMethodCertif.userMarkItAsCompleted(methodName);
                    messageDynamicData = new Object[]{methodName, userName, procReqInstance.getProcedureInstance()};
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic.getDiagnostic())) {
                        messageDynamicData = new Object[]{sopName, userName, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{methodName, userName, procReqInstance.getProcedureInstance()};
                    }
                    break;
                case CERTIFUSER_UNDERSTOOD_AND_SENDTOREVIEWER:
                case CERTIFUSER_TRAINING_REQUIRED:
                    diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
                    break;
                default:
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                    return;
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic.getDiagnostic())) {
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic.getMessageCodeObj(), diagnostic.getMessageCodeVariables());
            } else {
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, messageDynamicData, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
            }
        } catch (Exception e) {
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.EXCEPTION_RAISED.getErrorCode(), new Object[]{e.getMessage(), this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
        } finally {
            // release database resources
            try {
                rObj.killInstance();
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
