/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import module.monitoring.definition.TblsEnvMonitData;
import module.monitoring.definition.TblsEnvMonitConfig;
import lbplanet.utilities.LPArray;
import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_NUM_DAYS;
import databases.TblsData;
import databases.TblsProcedure;
import functionaljavaa.platform.doc.EndPointsToRequirements;
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
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.monitoring.definition.ClassEnvMon;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import static trazit.session.ActionsServletCommons.publishResult;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class EnvMonAPI extends HttpServlet {

    public static class EnvMonSampleAPIactionsEndpoints {

    }

    public enum EnvMonAPIactionsEndpoints implements EnumIntEndpoints{ //, EndpointHandler  {
        /**
         *
         */
        CORRECTIVE_ACTION_COMPLETE("CORRECTIVE_ACTION_COMPLETE", "programCompleteCorrectiveAction_success",
                new LPAPIArguments[]{new LPAPIArguments("programName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("programCorrectiveActionId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM.getTableName()).build()).
                        add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                                .add(GlobalAPIsParams.LBL_TABLE, TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName()).build()).build()
                , null, null),
        EM_BATCH_INCUB_CREATE("EM_BATCH_INCUB_CREATE", "incubatorBatch_create_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 10),
                    new LPAPIArguments("incubStage", LPAPIArguments.ArgumentType.STRING.toString(), false, 11),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName()).build()).build()
                , null, null),
        EM_BATCH_INCUB_REMOVE("EM_BATCH_INCUB_REMOVE", "incubatorBatch_remove_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName()).build()).build()
                , null, null),
        EM_BATCH_ASSIGN_INCUB("EM_BATCH_ASSIGN_INCUB", "incubatorBatch_assignIncubator_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCUBATOR_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments("incubStage", LPAPIArguments.ArgumentType.STRING.toString(), true, 8)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName()).build())
                        .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                                .add(GlobalAPIsParams.LBL_TABLE, TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName()).build()).build()
                , null, null),
        EM_BATCH_UPDATE_INFO("EM_BATCH_UPDATE_INFO", "incubatorBatch_updateInfo_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName()).build()).build()
                , null, null),
        EM_BATCH_INCUB_START("EM_BATCH_INCUB_START", "incubatorBatch_incubationStart_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName()).build()).build()
                , null, null),
        EM_BATCH_INCUB_END("EM_BATCH_INCUB_END", "incubatorBatch_incubationEnd_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName()).build()).build()
                , null, null),
        EM_LOGSAMPLE_SCHEDULER("EM_LOGSAMPLE_SCHEDULER", "programScheduler_logScheduledSamples",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_START, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_END, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                    new LPAPIArguments("programName", LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8)}, null
                , null, null),
        EM_MD_ADD_ADHOC_MICROORGANISM("EM_MD_ADD_ADHOC_MICROORGANISM", "masterData_AddAdhocMicroorganism",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_START, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, null
                , null, null)
        ;
        private EnvMonAPIactionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
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
        @Override public String getEntity() {return "batch_incub";}
        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getApiUrl() {
            return GlobalVariables.ApiUrls.ENVMON_ACTIONS.getUrl();
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
        public String getDeveloperComment() {
            return this.devComment;
        }

        @Override
        public String getDeveloperCommentTag() {
            return this.devCommentTag;
        }
        private final String name;
        private final String successMessageCode;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
        private final String devComment;
        private final String devCommentTag;
/*
        @Override
        public boolean endpointExists() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Object[] getDiagnostic() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public JSONObject createResponse() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
*/        
    }
    public enum EnvMonQueriesAPIEndpoints implements EnumIntEndpoints {
        GET_MASTER_DATA("GET_MASTER_DATA", "",
                new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        PROGRAMS_LIST("PROGRAMS_LIST", "",
                new LPAPIArguments[]{new LPAPIArguments("programFldNameList", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 6),
                    new LPAPIArguments("programFldSortList", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments("programLocationFldNameList", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                    new LPAPIArguments("programLocationFldSortList", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                    new LPAPIArguments("programLocationCardInfoFldNameList", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                    new LPAPIArguments("programLocationCardInfoFldSortList", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OBJ_GROUP_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABLE_CATEGORY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABLE_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 14),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 15),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 16),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELDS_TO_RETRIEVE_OR_GROUPING, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 17),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_GROUPED, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 18),}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        PROGRAMS_CORRECTIVE_ACTION_LIST("PROGRAMS_CORRECTIVE_ACTION_LIST", "",
                new LPAPIArguments[]{new LPAPIArguments("programName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("programCorrectiveActionFldNameList", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments("programCorrectiveActionFldSortList", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        GET_ALL_PRODUCTION_LOTS("GET_ALL_PRODUCTION_LOTS", "",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PRODLOT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PRODLOT_FIELD_TO_SORT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        GET_ACTIVE_PRODUCTION_LOTS("GET_ACTIVE_PRODUCTION_LOTS", "",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PRODLOT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PRODLOT_FIELD_TO_SORT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        DEACTIVATED_PRODUCTION_LOTS_LAST_N_DAYS("DEACTIVATED_PRODUCTION_LOTS_LAST_N_DAYS", "", new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_NUM_DAYS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        GET_SCHEDULED_SAMPLES("GET_SCHEDULED_SAMPLES", "",
                new LPAPIArguments[]{
                    new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOGIN_DAY_START, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOGIN_DAY_END, LPAPIArguments.ArgumentType.STRING.toString(), false, 8)},
                    EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        GET_STAGES_TIMING_CAPTURE_DATA("GET_STAGES_TIMING_CAPTURE_DATA", "",
                new LPAPIArguments[]{
                    new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOGIN_DAY_START, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOGIN_DAY_END, LPAPIArguments.ArgumentType.STRING.toString(), false, 8)},
                    EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        GET_SAMPLE_INFO("GET_SAMPLE_INFO", "get_sample_info_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
        , null, null),
        GET_SAMPLE_RESULTS("GET_SAMPLE_RESULTS", "get_sample_results_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 8)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build()
        , null, null),
        GET_SAMPLE_RESULTS_SECONDENTRY("GET_SAMPLE_RESULTS_SECONDENTRY", "get_sample_results_secondentry_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 8)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build()
        , null, null)        
        ;
        private EnvMonQueriesAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name = name;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.outputObjectTypes = outputObjectTypes;
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);
        }

        public HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex) {
            HashMap<HttpServletRequest, Object[]> hm = new HashMap<>();
            ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, null);
            if (Boolean.FALSE.equals(procReqInstance.getIsForTesting())){            
                return hm;
            }
            Object[] argValues = new Object[0];
            for (LPAPIArguments curArg : this.arguments) {
                argValues = LPArray.addValueToArray1D(argValues, curArg.getName() + ":" + getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }
            hm.put(request, argValues);
            return hm;
        }
        
        @Override public String getEntity() {return "program";}
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
            return ApiUrls.ENVMON_QUERIES.getUrl();
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

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())) {
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);
            return;
        }
        String actionName = procReqInstance.getActionName();
        String language = procReqInstance.getLanguage();

        EnvMonAPIactionsEndpoints endPoint = null;
        try {
            endPoint = EnvMonAPIactionsEndpoints.valueOf(actionName.toUpperCase());
        } catch (Exception e) {
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }
        Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }
        try (PrintWriter out = response.getWriter()) {
            ClassEnvMon clss = new ClassEnvMon(request, endPoint);
            if (clss.getFunctionFound()){
                publishResult(request, response, procReqInstance, clss.getEndpointObj(), 
                    clss.getDiagnostic(), clss.getDiagnosticObj(), 
                    clss.getMessageDynamicData(), clss.getRelatedObj());
            }
        } catch (Exception e) {
            procReqInstance.killIt();
            LPFrontEnd.responseError(new String[]{"Servlet " + this.getClass().getSimpleName() + "Error: " + e.getMessage()});
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
