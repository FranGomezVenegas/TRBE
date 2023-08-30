/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import static com.labplanet.servicios.app.AuthenticationAPIParams.RESPONSE_JSON_DATATABLE;
import module.monitoring.definition.TblsEnvMonitData;
import module.monitoring.definition.TblsEnvMonitConfig;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_NUM_DAYS;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import com.labplanet.servicios.app.GlobalAPIsParams;
import module.monitoring.definition.TblsEnvMonitConfig.ViewsEnvMonConfig;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import databases.Rdbms;
import databases.SqlStatement;
import functionaljavaa.samplestructure.DataSampleUtilities;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.SqlStatementEnums;
import databases.SqlWhere;
import databases.TblsCnfg;
import databases.TblsProcedure;
import functionaljavaa.materialspec.SpecFrontEndUtilities;
import module.monitoring.logic.ConfigMasterData;
import module.monitoring.logic.DataProgramCorrectiveAction.ProgramCorrectiveActionStatuses;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPJson;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntViewFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import static trazit.queries.QueryUtilities.getFieldsListToRetrieve;
import static trazit.queries.QueryUtilities.getKPIInfoFromRequest;
import static trazit.queries.QueryUtilities.getNdaysArray;
import static trazit.queries.QueryUtilities.getTableData;
import trazit.queries.QueryUtilitiesEnums;

/**
 *
 * @author Administrator
 */
public class EnvMonAPIqueries extends HttpServlet {

    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME + "|" + GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN + "|" + GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    public static final String MANDATORY_PARAMS_PROGRAM_CORRECTIVE_ACTION_LIST = "programName";
    public static final String DEFAULT_PARAMS_PROGRAMS_LIST_PROGRAM_TO_GET = "name|program_config_id|program_config_version|description_en|description_es"
            + "|sample_config_code|sample_config_code_version|map_image";
    public static final String DEFAULT_PARAMS_PROGRAMS_LIST_PROGRAM_SORT_FLDS = "name";
    public static final String DEFAULT_PARAMS_PROGRAMS_LIST_PROGRAM_LOCATION_TO_GET = "program_name|location_name|description_en|description_es|map_icon|map_icon_h|map_icon_w|map_icon_top|map_icon_left|area|spec_code|spec_variation_name|spec_analysis_variation|person_ana_definition|requires_person_ana";
    public static final String DEFAULT_PARAMS_PROGRAMS_LIST_PROGRAM_LOCATION_SORT_FLDS = "order_number|location_name";
    public static final String DEFAULT_PARAMS_PROGRAMS_LIST_CARD_FIELDS = "program_name|location_name|area|spec_code|spec_code_version|spec_variation_name|spec_analysis_variation";
    public static final String DEFAULT_PARAMS_PROGRAMS_LIST_CARD_SORT_FLDS = "order_number|location_name";
    public static final String[] DEFAULT_PARAMS_SPEC_CODE_VERSION = new String[]{"spec_code_version"};
    public static final String[] DEFAULT_PARAMS_DESCRIPTION_EN = new String[]{"description_en"};
    public static final String DEFAULT_PARAMS_PROGRAM_CORRECTIVE_ACTION_LIST_FLDS_TO_GET = "id|status|status_previous|created_on|created_by|program_name|location_name|area|sample_id|test_id|result_id|limit_id|spec_eval|spec_eval_detail|analysis|method_name|method_version|param_name|spec_rule_with_detail";
    public static final String DEFAULT_PARAMS_PROGRAM_CORRECTIVE_ACTION_LIST_FLDS_TO_SORT = "program_name|created_on desc";
    public static final String JSON_TAG_NAME_NAME = "name";
    public static final String JSON_TAG_NAME_LABEL_EN = "label_en";
    public static final String JSON_TAG_NAME_LABEL_ES = "label_es";
    public static final String JSON_TAG_NAME_PASS = "password";
    public static final String JSON_TAG_NAME_PASS_VALUE_FALSE = "false";
    public static final String JSON_TAG_NAME_TYPE = "type";
    public static final String JSON_TAG_NAME_TYPE_VALUE_TREE_LIST = "tree-list";
    public static final String JSON_TAG_NAME_TYPE_VALUE_TEXT = "text";
    public static final String JSON_TAG_NAME_DB_TYPE = "dbType";
    public static final String JSON_TAG_NAME_DB_TYPE_VALUE_INTEGER = "Integer";
    public static final String JSON_TAG_NAME_DB_TYPE_VALUE_STRING = "String";
    public static final String JSON_TAG_NAME_VALUE = "value";
    public static final String JSON_TAG_NAME_TOTAL = "total";
    public static final String JSON_TAG_GROUP_NAME_CARD_PROGRAMS_LIST = "programsList";
    public static final String JSON_TAG_GROUP_NAME_CARD_INFO = "card_info";
    public static final String JSON_TAG_GROUP_NAME_SAMPLES_SUMMARY = "samples_summary";
    public static final String JSON_TAG_GROUP_NAME_SAMPLES_SUMMARY_BY_STAGE = "samples_summary_by_stage";
    public static final String JSON_TAG_GROUP_NAME_CONFIG_CALENDAR = "config_scheduled_calendar";
    public static final String JSON_TAG_GROUP_NAME_SAMPLE_POINTS = "sample_points";
    public static final String JSON_TAG_PROGRAM_DATA_TEMPLATE_DEFINITION = "program_data_template_definition";
    public static final String JSON_TAG_SPEC_DEFINITION = "spec_definition";

    /*
        
   
 GlobalAPIsParams. GlobalAPIsParams.
GlobalAPIsParams. GlobalAPIsParams. GlobalAPIsParams.  
GlobalAPIsParams.
     */
    public enum EnvMonAPIqueriesEndpoints implements EnumIntEndpoints {
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
                    EndPointsToRequirements.endpointWithNoOutputObjects, null, null)        
        ;
        private EnvMonAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
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

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())) {
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);
            return;
        }
        String actionName = procReqInstance.getActionName();
        String language = procReqInstance.getLanguage();
        String procInstanceName = procReqInstance.getProcedureInstance();

        try (PrintWriter out = response.getWriter()) {
            EnvMonAPIqueriesEndpoints endPoint = null;
            try {
                endPoint = EnvMonAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
            } catch (Exception e) {
                //procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            if (endPoint.getArguments().length > 0 && argValues.length > 0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
                //this.diagnostic=argValues;
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
//                LPFrontEnd.servletReturnResponseError(request, response, argValues[1].toString(), new Object[]{argValues[2].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                return;
            }
            switch (endPoint) {
                case GET_MASTER_DATA:
                    LPFrontEnd.servletReturnSuccess(request, response, ConfigMasterData.getMasterData(procInstanceName, null));
                    return;
                case PROGRAMS_LIST:
                    String[] programFldNameArray = getFieldsListToRetrieve(argValues[0].toString(), EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM.getTableFields()));
                    String[] programFldSortArray = getFieldsListToRetrieve(argValues[1].toString(), new String[]{});
                    String[] programLocationFldNameArray = getFieldsListToRetrieve(argValues[2].toString(), EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getTableFields()));
                    String[] programLocationFldSortArray = getFieldsListToRetrieve(argValues[3].toString(), new String[]{});
                    String[] programLocationCardInfoFldNameArray = getFieldsListToRetrieve(argValues[4].toString(), EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getTableFields()));
                    String[] programLocationCardInfoFldSortArray = getFieldsListToRetrieve(argValues[5].toString(), new String[]{});

                    if (LPArray.valuePosicInArray(programLocationFldNameArray, TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName()) == -1) {
                        programLocationFldNameArray = LPArray.addValueToArray1D(programLocationFldNameArray, TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName());
                        programLocationCardInfoFldNameArray = LPArray.addValueToArray1D(programLocationCardInfoFldNameArray, TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName());
                    }
                    if (LPArray.valuePosicInArray(programLocationFldNameArray, TblsEnvMonitConfig.ProgramLocation.LOCATION_NAME.getName()) == -1) {
                        programLocationFldNameArray = LPArray.addValueToArray1D(programLocationFldNameArray, TblsEnvMonitConfig.ProgramLocation.LOCATION_NAME.getName());
                        programLocationCardInfoFldNameArray = LPArray.addValueToArray1D(programLocationCardInfoFldNameArray, TblsEnvMonitConfig.ProgramLocation.LOCATION_NAME.getName());
                    }
                    Object[] statusList = DataSampleUtilities.getSchemaSampleStatusList();
                    Object[] statusListEn = DataSampleUtilities.getSchemaSampleStatusList(GlobalVariables.Languages.EN.getName());
                    Object[] statusListEs = DataSampleUtilities.getSchemaSampleStatusList(GlobalVariables.Languages.ES.getName());

                    Object[][] programInfo = getTableData(procReqInstance, TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM.getRepositoryName(), TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM.getTableName(),
                            argValues[0].toString(), EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM.getTableFields()),
                            new String[]{TblsEnvMonitConfig.Program.ACTIVE.getName()}, new Object[]{true}, programFldSortArray);
                    JSONArray programsJsonArr = new JSONArray();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programInfo[0][0].toString())) {
                        LPFrontEnd.servletReturnSuccess(request, response, programsJsonArr);
                        return;
                    }
                    for (Object[] curProgram : programInfo) {
                        JSONObject programJsonObj = new JSONObject();
                        String curProgramName = curProgram[0].toString();
                        programJsonObj = LPJson.convertArrayRowToJSONObject(programFldNameArray, curProgram);

                        String[] programSampleSummaryFldNameArray = new String[]{TblsEnvMonitData.Sample.STATUS.getName(), TblsEnvMonitData.Sample.LOCATION_NAME.getName()};
                        String[] programSampleSummaryFldSortArray = new String[]{TblsEnvMonitData.Sample.STATUS.getName()};
                        Object[][] programSampleSummary = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getRepositoryName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(),
                                new String[]{TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName(),}, new String[]{curProgramName}, programSampleSummaryFldNameArray, programSampleSummaryFldSortArray);
                        programJsonObj.put(JSON_TAG_NAME_TYPE, JSON_TAG_NAME_TYPE_VALUE_TREE_LIST);
                        programJsonObj.put(JSON_TAG_NAME_TOTAL, programSampleSummary.length);
                        programJsonObj.put("KPI", getKPIInfoFromRequest(request, TblsEnvMonitData.Sample.PROGRAM_NAME.getName(), curProgramName));

                        Object[][] programLocations = getTableData(procReqInstance, TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getRepositoryName(), TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getTableName(),
                                argValues[2].toString(), EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getTableFields()),
                                new String[]{TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName()}, new Object[]{curProgramName}, programLocationFldSortArray);
                        /**/
                        if (procReqInstance.getProcedureInstance() == null || procReqInstance.getProcedureInstance().length() == 0) {
                            procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
                        }

                        String[] fieldToRetrieveArr = new String[]{TblsEnvMonitData.Sample.CURRENT_STAGE.getName()};
                        Object[][] samplesCounterPerStage = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getRepositoryName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(),
                                fieldToRetrieveArr,
                                new String[]{TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName()}, new Object[]{curProgramName},
                                new String[]{"COUNTER desc"});
                        fieldToRetrieveArr = LPArray.addValueToArray1D(fieldToRetrieveArr, "COUNTER");
                        JSONArray programSampleSummaryByStageJsonArray = new JSONArray();
                        for (Object[] curRec : samplesCounterPerStage) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldToRetrieveArr, curRec);
                            programSampleSummaryByStageJsonArray.add(jObj);
                        }
                        programJsonObj.put(JSON_TAG_GROUP_NAME_SAMPLES_SUMMARY_BY_STAGE, programSampleSummaryByStageJsonArray);

                        JSONObject jObj = new JSONObject();
                        String[] fieldsToRetrieve = new String[]{TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_NAME.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.DATE.getName(),
                            TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_DAY_ID.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_DAY_DATE.getName(),
                            TblsEnvMonitConfig.ViewProgramScheduledLocations.SAMPLE_CONFIG_CODE.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.SAMPLE_CONFIG_CODE_VERSION.getName(),
                            TblsEnvMonitConfig.ViewProgramScheduledLocations.LOCATION_NAME.getName(),
                            TblsEnvMonitConfig.ViewProgramScheduledLocations.AREA.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.SPEC_CODE.getName(),
                            TblsEnvMonitConfig.ViewProgramScheduledLocations.SPEC_CODE_VERSION.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.AREA.getName(),
                            TblsEnvMonitConfig.ViewProgramScheduledLocations.SPEC_VARIATION_NAME.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.SPEC_ANALYSIS_VARIATION.getName(),
                            TblsEnvMonitConfig.ViewProgramScheduledLocations.SAMPLE_ID.getName()
                        };
                        Object[][] programCalendarDatePending = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, TblsEnvMonitConfig.ViewsEnvMonConfig.PROG_SCHED_LOCATIONS_VIEW.getRepositoryName()), ViewsEnvMonConfig.PROG_SCHED_LOCATIONS_VIEW.getViewName(),
                                new String[]{TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_NAME.getName() + WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{},
                                fieldsToRetrieve, new String[]{TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_DAY_DATE.getName()});
                        JSONArray programConfigScheduledPointsJsonArray = new JSONArray();
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programCalendarDatePending[0][0].toString())) {
                            jObj.put("message", "Nothing pending in procedure " + procInstanceName + " for the filter " + LPNulls.replaceNull(programCalendarDatePending[0][programCalendarDatePending.length - 1]).toString());
                            programConfigScheduledPointsJsonArray.add(jObj);
                        } else {
                            for (Object[] curRecord : programCalendarDatePending) {
                                jObj = new JSONObject();
                                for (int i = 0; i < curRecord.length; i++) {
                                    jObj.put(fieldsToRetrieve[i], curRecord[i].toString());
                                }
                                jObj.put("title", curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.ViewProgramScheduledLocations.LOCATION_NAME.getName())].toString());
                                jObj.put("content", curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.ViewProgramScheduledLocations.LOCATION_NAME.getName())].toString());
                                jObj.put("date", curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.ViewProgramScheduledLocations.DATE.getName())].toString());
                                jObj.put("category", "orange");
                                jObj.put("color", "#000");
                                programConfigScheduledPointsJsonArray.add(jObj);
                            }
                        }
                        programJsonObj.put(JSON_TAG_GROUP_NAME_CONFIG_CALENDAR, programConfigScheduledPointsJsonArray);
                        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(programLocations[0][0].toString()))) {
                            JSONArray programLocationsJsonArray = new JSONArray();
                            for (Object[] programLocations1 : programLocations) {
                                String locationName = programLocations1[LPArray.valuePosicInArray(programLocationFldNameArray, TblsEnvMonitConfig.ProgramLocation.LOCATION_NAME.getName())].toString();

                                JSONObject programLocationJsonObj = new JSONObject();
                                for (int yProcEv = 0; yProcEv < programLocations[0].length; yProcEv++) {
                                    programLocationJsonObj.put(programLocationFldNameArray[yProcEv], programLocations1[yProcEv]);
                                }
                                Object[][] programLocationCardInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getRepositoryName()), TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getTableName(),
                                        new String[]{TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName(), TblsEnvMonitConfig.ProgramLocation.LOCATION_NAME.getName()}, new String[]{curProgramName, locationName},
                                        programLocationCardInfoFldNameArray, programLocationCardInfoFldSortArray);
                                JSONArray programLocationCardInfoJsonArr = new JSONArray();

                                JSONObject programLocationCardInfoJsonObj = new JSONObject();
                                for (int xProc = 0; xProc < programLocationCardInfo.length; xProc++) {
                                    for (int yProc = 0; yProc < programLocationCardInfo[0].length; yProc++) {
                                        programLocationCardInfoJsonObj = new JSONObject();
                                        programLocationCardInfoJsonObj.putIfAbsent(JSON_TAG_NAME_NAME, programLocationCardInfoFldNameArray[yProc]);
                                        programLocationCardInfoJsonObj.putIfAbsent(JSON_TAG_NAME_LABEL_EN, programLocationCardInfoFldNameArray[yProc]);
                                        programLocationCardInfoJsonObj.putIfAbsent(JSON_TAG_NAME_LABEL_ES, programLocationCardInfoFldNameArray[yProc]);
                                        programLocationCardInfoJsonObj.putIfAbsent(JSON_TAG_NAME_VALUE, programLocationCardInfo[xProc][yProc]);
                                        programLocationCardInfoJsonObj.putIfAbsent(JSON_TAG_NAME_TYPE, JSON_TAG_NAME_TYPE_VALUE_TEXT);
                                        String fieldName = programLocationCardInfoFldNameArray[yProc];
                                        Integer posicInArray = LPArray.valuePosicInArray(DEFAULT_PARAMS_SPEC_CODE_VERSION, fieldName);
                                        if (posicInArray > -1) {
                                            programLocationCardInfoJsonObj.putIfAbsent(JSON_TAG_NAME_DB_TYPE, JSON_TAG_NAME_DB_TYPE_VALUE_INTEGER);
                                        } else {
                                            posicInArray = LPArray.valuePosicInArray(DEFAULT_PARAMS_DESCRIPTION_EN, fieldName);
                                            if (posicInArray == -1) {
                                                programLocationCardInfoJsonObj.putIfAbsent(JSON_TAG_NAME_DB_TYPE, JSON_TAG_NAME_DB_TYPE_VALUE_STRING);
                                            } else {
                                                programLocationCardInfoJsonObj.putIfAbsent(JSON_TAG_NAME_DB_TYPE, "");
                                            }
                                        }
                                        programLocationCardInfoJsonObj.putIfAbsent(JSON_TAG_NAME_PASS, JSON_TAG_NAME_PASS_VALUE_FALSE);
                                        programLocationCardInfoJsonArr.add(programLocationCardInfoJsonObj);
                                    }
                                }
                                programLocationJsonObj.put(JSON_TAG_GROUP_NAME_CARD_INFO, programLocationCardInfoJsonArr);
                                Object[] samplesStatusCounter = new Object[0];
                                for (Object statusList1 : statusList) {
                                    String currStatus = statusList1.toString();
                                    Integer contSmpStatus = 0;
                                    for (Object[] smpStatus : programSampleSummary) {
                                        if (currStatus.equalsIgnoreCase(smpStatus[0].toString())
                                                && (smpStatus[1] != null) && locationName.equalsIgnoreCase(smpStatus[1].toString())) {
                                            contSmpStatus++;
                                        }
                                    }
                                    samplesStatusCounter = LPArray.addValueToArray1D(samplesStatusCounter, contSmpStatus);
                                }
                                JSONArray programSampleSummaryJsonArray = new JSONArray();
                                for (int iStatuses = 0; iStatuses < statusList.length; iStatuses++) {
                                    JSONObject programSampleSummaryJsonObj = new JSONObject();
                                    programSampleSummaryJsonObj.put(JSON_TAG_NAME_NAME, statusList[iStatuses]);
                                    programSampleSummaryJsonObj.put(JSON_TAG_NAME_LABEL_EN, statusListEn[iStatuses]);
                                    programSampleSummaryJsonObj.put(JSON_TAG_NAME_LABEL_ES, statusListEs[iStatuses]);
                                    programSampleSummaryJsonObj.put(JSON_TAG_NAME_VALUE, samplesStatusCounter[iStatuses]);
                                    programSampleSummaryJsonObj.put(JSON_TAG_NAME_TYPE, JSON_TAG_NAME_TYPE_VALUE_TEXT);
                                    programSampleSummaryJsonObj.put(JSON_TAG_NAME_PASS, JSON_TAG_NAME_PASS_VALUE_FALSE);
                                    programSampleSummaryJsonArray.add(programSampleSummaryJsonObj);
                                }
                                programLocationJsonObj.put(JSON_TAG_GROUP_NAME_SAMPLES_SUMMARY, programSampleSummaryJsonArray);
                                fieldToRetrieveArr = new String[]{TblsEnvMonitData.Sample.CURRENT_STAGE.getName()};
                                String[] whereFieldNames = new String[]{TblsEnvMonitData.Sample.PROGRAM_NAME.getName(), TblsEnvMonitData.Sample.LOCATION_NAME.getName()};
                                Object[] whereFieldValues = new Object[]{curProgramName, locationName};
                                samplesCounterPerStage = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getRepositoryName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(),
                                        fieldToRetrieveArr,
                                        whereFieldNames, whereFieldValues,
                                        new String[]{"COUNTER desc"});
                                fieldToRetrieveArr = LPArray.addValueToArray1D(fieldToRetrieveArr, "COUNTER");
                                programSampleSummaryByStageJsonArray = new JSONArray();
                                for (Object[] curRec : samplesCounterPerStage) {
                                    jObj = LPJson.convertArrayRowToJSONObject(fieldToRetrieveArr, curRec);
                                    programSampleSummaryByStageJsonArray.add(jObj);
                                }
                                programLocationJsonObj.put(JSON_TAG_GROUP_NAME_SAMPLES_SUMMARY_BY_STAGE, programSampleSummaryByStageJsonArray);

                                programLocationsJsonArray.add(programLocationJsonObj);
                            }
                            programJsonObj.put(JSON_TAG_GROUP_NAME_SAMPLE_POINTS, programLocationsJsonArray);
                        }
                        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(programLocations[0][0].toString()))) {
                            JSONArray programSampleSummaryJsonArray = new JSONArray();
                            Object[] samplesStatusCounter = new Object[0];
                            for (Object statusList1 : statusList) {
                                String currStatus = statusList1.toString();
                                Integer contSmpStatus = 0;
                                for (Object[] smpStatus : programSampleSummary) {
                                    if (currStatus.equalsIgnoreCase(smpStatus[0].toString())) {
                                        contSmpStatus++;
                                    }
                                }
                                samplesStatusCounter = LPArray.addValueToArray1D(samplesStatusCounter, contSmpStatus);
                            }
                            for (int iStatuses = 0; iStatuses < statusList.length; iStatuses++) {
                                JSONObject programSampleSummaryJsonObj = new JSONObject();
                                programSampleSummaryJsonObj.put(JSON_TAG_NAME_NAME, LPNulls.replaceNull(statusList[iStatuses]));
                                programSampleSummaryJsonObj.put(JSON_TAG_NAME_LABEL_EN, LPNulls.replaceNull(statusListEn[iStatuses]));
                                programSampleSummaryJsonObj.put(JSON_TAG_NAME_LABEL_ES, LPNulls.replaceNull(statusListEs[iStatuses]));
                                programSampleSummaryJsonObj.put(JSON_TAG_NAME_VALUE, LPNulls.replaceNull(samplesStatusCounter[iStatuses]));
                                programSampleSummaryJsonObj.put(JSON_TAG_NAME_TYPE, JSON_TAG_NAME_TYPE_VALUE_TEXT);
                                programSampleSummaryJsonObj.put(JSON_TAG_NAME_PASS, JSON_TAG_NAME_PASS_VALUE_FALSE);
                                programSampleSummaryJsonArray.add(programSampleSummaryJsonObj);
                            }
                            programJsonObj.put(JSON_TAG_GROUP_NAME_SAMPLES_SUMMARY, programSampleSummaryJsonArray);
                        }
                        programsJsonArr.add(programJsonObj);
                        JSONObject programDataTemplateDefinition = new JSONObject();
                        JSONObject templateProgramInfo = EnvMonFrontEndUtilities.dataProgramInfo(curProgramName, null, null);
                        programDataTemplateDefinition.put(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM.getTableName(), templateProgramInfo);
                        JSONArray templateProgramLocationInfo = EnvMonFrontEndUtilities.dataProgramLocationInfo(curProgramName, null, null);
                        programDataTemplateDefinition.put(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getTableName(), templateProgramLocationInfo);
                        programJsonObj.put(JSON_TAG_PROGRAM_DATA_TEMPLATE_DEFINITION, programDataTemplateDefinition);
                        Object specCode = templateProgramInfo.get(TblsEnvMonitConfig.Program.SPEC_CODE.getName());
                        Object specConfigVersion = templateProgramInfo.get(TblsEnvMonitConfig.Program.SPEC_CONFIG_VERSION.getName());
                        JSONObject specDefinition = new JSONObject();
                        if (Boolean.FALSE.equals((specCode == null || specCode == "" || specConfigVersion == null || "".equals(specConfigVersion.toString())))) {
                            JSONObject specInfo = SpecFrontEndUtilities.configSpecInfo(procReqInstance, TblsCnfg.TablesConfig.SPEC, (String) specCode, (Integer) specConfigVersion,
                                    null, null);
                            specDefinition.put(TblsCnfg.TablesConfig.SPEC.getTableName(), specInfo);
                            JSONArray specLimitsInfo = SpecFrontEndUtilities.configSpecLimitsInfo(procReqInstance, TblsCnfg.TablesConfig.SPEC_LIMITS, (String) specCode, (Integer) specConfigVersion, null,
                                    null, new String[]{TblsCnfg.SpecLimits.VARIATION_NAME.getName(), TblsCnfg.SpecLimits.ANALYSIS.getName(),
                                        TblsCnfg.SpecLimits.METHOD_NAME.getName(), TblsCnfg.SpecLimits.LIMIT_ID.getName(),
                                        TblsCnfg.SpecLimits.SPEC_TEXT_EN.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_RED_AREA_EN.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_YELLOW_AREA_EN.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_GREEN_AREA_EN.getName(),
                                        TblsCnfg.SpecLimits.SPEC_TEXT_ES.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_RED_AREA_ES.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_YELLOW_AREA_ES.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_GREEN_AREA_ES.getName()});
                            specDefinition.put(TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName(), specLimitsInfo);
                        }
                        programJsonObj.put(JSON_TAG_SPEC_DEFINITION, specDefinition);
                    }
                    JSONObject programsListObj = new JSONObject();
                    programsListObj.put(JSON_TAG_GROUP_NAME_CARD_PROGRAMS_LIST, programsJsonArr);
                    LPFrontEnd.servletReturnSuccess(request, response, programsListObj);
                    return;
                case PROGRAMS_CORRECTIVE_ACTION_LIST:
                    String statusClosed = ProgramCorrectiveActionStatuses.STATUS_CLOSED.getStatusCode();
                    String programName = argValues[0].toString();
                    String[] progCorrFldNameList = getFieldsListToRetrieve(argValues[1].toString(), getAllFieldNames(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableFields()));
                    String[] progCorrFldSortArray = null;
                    if (argValues[2].toString().length() > 0) {
                        progCorrFldSortArray = argValues[2].toString().split("\\|");
                    }
                    Object[][] progCorrInfo = getTableData(procReqInstance, TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getRepositoryName(), TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(),
                            argValues[1].toString(), getAllFieldNames(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableFields()),
                            new String[]{TblsProcedure.ProgramCorrectiveAction.PROGRAM_NAME.getName(), TblsProcedure.ProgramCorrectiveAction.STATUS.getName() + "<>"},
                            new String[]{programName, statusClosed}, progCorrFldSortArray);
                    JSONArray jArr = new JSONArray();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(progCorrInfo[0][0].toString())) {
                        LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    }
                    for (Object[] curProgCorr : progCorrInfo) {
                        JSONObject jObj = LPJson.convertArrayRowToJSONObject(progCorrFldNameList, curProgCorr);
                        jArr.add(jObj);
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                case GET_ACTIVE_PRODUCTION_LOTS:
                case GET_ALL_PRODUCTION_LOTS:
                    String[] whereFldName = new String[]{TblsEnvMonitData.ProductionLot.LOT_NAME.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()};
                    Object[] whereFldValue = new Object[]{"s"};
                    if ("GET_ACTIVE_PRODUCTION_LOTS".equalsIgnoreCase(endPoint.getName())) {
                        whereFldName = new String[]{TblsEnvMonitData.ProductionLot.ACTIVE.getName()};
                        whereFldValue = new Object[]{true};
                    }
                    String[] prodLotFldToRetrieve = getFieldsListToRetrieve(argValues[0].toString(), EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableFields()));
                    String[] prodLotFldToSort = getFieldsListToRetrieve(argValues[1].toString(), new String[]{});
                    programInfo = QueryUtilitiesEnums.getTableData(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT,
                            EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT, argValues[0]),
                            whereFldName, whereFldValue, prodLotFldToSort);
                    jArr = new JSONArray();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programInfo[0][0].toString())) {
                        LPFrontEnd.servletReturnSuccess(request, response, jArr);
                        return;
                    }
                    for (Object[] curProgram : programInfo) {
                        JSONObject jObj = LPJson.convertArrayRowToJSONObject(prodLotFldToRetrieve, curProgram);
                        jArr.add(jObj);
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                case DEACTIVATED_PRODUCTION_LOTS_LAST_N_DAYS:
                    String numDays = LPNulls.replaceNull(argValues[0]).toString();
                    if (numDays.length() == 0) {
                        numDays = String.valueOf(7);
                    }
                    int numDaysInt = 0 - Integer.valueOf(numDays);
                    jArr=getNdaysArray(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT, numDays, TblsEnvMonitData.ProductionLot.CLOSED_ON, 
                            new String[]{TblsEnvMonitData.ProductionLot.ACTIVE.getName()}, 
                            new Object[]{false}, 
                            new String[]{TblsEnvMonitData.ProductionLot.CLOSED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                case GET_SCHEDULED_SAMPLES:
                    SqlWhere wObj = new SqlWhere();
                    programName = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME);
                    if (programName != null && programName.length() > 0) {
                        wObj.addConstraint(TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_NAME,
                                programName.contains("*") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{programName}, null);
                    }

                    String loginDayStart = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_LOGIN_DAY_START);
                    String loginDayEnd = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_LOGIN_DAY_END);

                    Object[] buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsEnvMonitConfig.ViewProgramScheduledLocations.DATE.getName(), loginDayStart, loginDayEnd);
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString()))) {
                        if (buildDateRangeFromStrings.length == 4) {
                            wObj.addConstraint(TblsEnvMonitConfig.ViewProgramScheduledLocations.DATE, SqlStatement.WHERECLAUSE_TYPES.BETWEEN, new Object[]{buildDateRangeFromStrings[2], buildDateRangeFromStrings[3]}, null);
                        } else {
                            wObj.addConstraint(TblsEnvMonitConfig.ViewProgramScheduledLocations.DATE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{buildDateRangeFromStrings[2]}, null);
                        }
                    }
                    JSONObject jObj = new JSONObject();
                    JSONArray sampleJsonArr = new JSONArray();
                    if (Boolean.FALSE.equals(wObj.getAllWhereEntries().isEmpty())) {
                        EnumIntViewFields[] fieldsToGet = EnumIntViewFields.getViewFieldsFromString(TblsEnvMonitConfig.ViewsEnvMonConfig.PROG_SCHED_LOCATIONS_VIEW, "ALL");
                        Object[][] programSchedEntries = QueryUtilitiesEnums.getViewData(TblsEnvMonitConfig.ViewsEnvMonConfig.PROG_SCHED_LOCATIONS_VIEW,
                        fieldsToGet,
                        wObj, //new SqlWhere(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, filterFieldName, filterFieldValue),
                        new String[]{TblsEnvMonitConfig.ViewProgramScheduledLocations.SAMPLE_ID.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()}, false);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programSchedEntries[0][0].toString())) {
                            jObj = LPFrontEnd.responseJSONDiagnosticLPFalse(Rdbms.RdbmsErrorTrapping.TABLE_WITH_NO_RECORDS, new Object[0]);
                            sampleJsonArr.add(jObj);
                        } else {
                            for (Object[] curRec : programSchedEntries) {
                                jObj = LPJson.convertArrayRowToJSONObject(EnumIntViewFields.getAllFieldNames(fieldsToGet), curRec);
                                sampleJsonArr.add(jObj);
                            }
                        }
                    }
                    JSONObject jObjMainObject = new JSONObject();
                    jObjMainObject.put(RESPONSE_JSON_DATATABLE, sampleJsonArr);
                    jObjMainObject.put(GlobalAPIsParams.LBL_TABLE, "GET_SCHEDULED_SAMPLES v1");
                    LPFrontEnd.servletReturnSuccess(request, response, jObjMainObject);
                    break;                            
                default:
                    RequestDispatcher rd = request.getRequestDispatcher(SampleAPIParams.SERVLET_FRONTEND_URL);
                    rd.forward(request, response);
            }
        } catch (Exception e) {
            procReqInstance.killIt();
            String[] errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject);
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, errMsg);
        } finally {
            // release database resources
            procReqInstance.killIt();
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
