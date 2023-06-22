/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.JSON_TAG_NAME_SAMPLE_RESULTS;
import static com.labplanet.servicios.moduleenvmonit.EnvMonIncubBatchAPIfrontend.getActiveBatchData;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import databases.SqlStatement;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.SqlStatementEnums;
import databases.SqlWhere;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsProcedure;
import databases.features.Token;
import functionaljavaa.certification.AnalysisMethodCertif;
import functionaljavaa.instruments.incubator.DataIncubatorNoteBook;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction;
import static functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction.isProgramCorrectiveActionEnable;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.samplestructure.DataSampleStages;
import functionaljavaa.samplestructure.DataSampleStructureStatuses;
import functionaljavaa.samplestructure.DataSampleStructureStatuses.SampleStatuses;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import static lbplanet.utilities.LPDate.dateStringFormatToLocalDateTime;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntViewFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import static trazit.globalvariables.GlobalVariables.DEFAULTLANGUAGE;
import static trazit.queries.QueryUtilities.getTableData;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;

/**
 *
 * @author User
 */
public class ClassEnvMonSampleFrontend {

    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    private Object[] diagnostic = new Object[0];
    private Boolean functionFound = false;
    private Boolean isSuccess = false;
    private JSONObject responseSuccessJObj = null;
    private JSONArray responseSuccessJArr = null;
    private Object[] responseError = null;

    private static final String[] SAMPLEANALYSISRESULTLOCKDATA_RETRIEVEDATA_PROGRAMCORRECTIVEACTION = new String[]{TblsProcedure.ProgramCorrectiveAction.RESULT_ID.getName(), TblsProcedure.ProgramCorrectiveAction.STATUS.getName()};
    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME + "|" + GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN + "|" + GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

    static final String IS_LOCKED = "is_locked";
    static final String LOCKING_OBJECT = "locking_object";
    static final String LOCKING_REASON = "locking_reason";

    public enum EnvMonSampleAPIqueriesEndpoints implements EnumIntEndpoints {
        GET_SAMPLE_ANALYSIS_LIST("GET_SAMPLE_ANALYSIS_LIST", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        GET_SAMPLE_ANALYSIS_RESULT_LIST("GET_SAMPLE_ANALYSIS_RESULT_LIST", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 11),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        GET_SAMPLE_ANALYSIS_RESULT_LIST_SECONDENTRY("GET_SAMPLE_ANALYSIS_RESULT_LIST_SECONDENTRY", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 11),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        GET_MICROORGANISM_LIST("GET_MICROORGANISM_LIST", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        GET_SAMPLE_MICROORGANISM_VIEW("GET_SAMPLE_MICROORGANISM_VIEW", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8),}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        GET_SAMPLE_STAGES_SUMMARY_REPORT("GET_SAMPLE_STAGES_SUMMARY_REPORT", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects,
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_RPT_INFO, "Name: SAMPLE STAGES SUMMARY REPORT v1.0").build()).build(),
                null, null),
        GET_SAMPLE_BY_TESTINGGROUP_SUMMARY_REPORT("GET_SAMPLE_BY_TESTINGGROUP_SUMMARY_REPORT", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects,
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_RPT_INFO, "Name: SAMPLE BY TESTING GROUP SUMMARY REPORT v1.0").build()).build(),
                null, null),
        GET_BATCH_REPORT("GET_BATCH_REPORT", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_FIELD_TO_DISPLAY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),}, EndPointsToRequirements.endpointWithNoOutputObjects,
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_RPT_INFO, "Name: BATCH REPORT v1.0").build()).build(),
                null, null),
        GET_PRODLOT_REPORT("GET_PRODLOT_REPORT", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PRODLOT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PRODLOT_FIELD_TO_DISPLAY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),}, EndPointsToRequirements.endpointWithNoOutputObjects,
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_RPT_INFO, "Name: PRODUCTION LOT REPORT v1.0").build()).build(),
                null, null),
        GET_INCUBATOR_REPORT("GET_INCUBATOR_REPORT", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCUBATOR_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCUBATOR_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCUBATOR_FIELD_TO_DISPLAY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_START, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_END, LPAPIArguments.ArgumentType.STRING.toString(), false, 10),}, EndPointsToRequirements.endpointWithNoOutputObjects,
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_RPT_INFO, "Name: INCUBATOR REPORT v1.0").build()).build(),
                null, null),
        STATS_SAMPLES_PER_STAGE("STATS_SAMPLES_PER_STAGE", "", new LPAPIArguments[]{
            new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_STAGES_TO_INCLUDE, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
            new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_STAGES_TO_EXCLUDE, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        STATS_PROGRAM_LAST_RESULTS("STATS_PROGRAM_LAST_RESULTS", "", new LPAPIArguments[]{
            new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_GROUPED, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
            new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_TOTAL_OBJECTS, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        KPIS("KPIS", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OBJ_GROUP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABLE_CATEGORY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABLE_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 10),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELDS_TO_RETRIEVE_OR_GROUPING, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_GROUPED, LPAPIArguments.ArgumentType.BOOLEANARR.toString(), true, 11),}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        GET_PENDING_INCUBATION_SAMPLES_AND_ACTIVE_BATCHES("GET_PENDING_INCUBATION_SAMPLES_AND_ACTIVE_BATCHES", "", new LPAPIArguments[]{
            new LPAPIArguments("includeSplittedByIncubNumber", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 6),
            new LPAPIArguments("includeAllWithAnyPendingIncubation", LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 10),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB1 + GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 11),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB1 + GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB1 + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB1 + GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 14),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB1 + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 15),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB1 + GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 16),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB1 + GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 17),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB1 + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 18),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB1 + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 19),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB1 + GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 20),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB1 + GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 21),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB1 + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 22),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB1 + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 23),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB1 + GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 24),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB2 + GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 25),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB2 + GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 26),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB2 + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 27),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB2 + GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 28),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB2 + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 29),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB2 + GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 30),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB2 + GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 31),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB2 + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 32),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB2 + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 33),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB2 + GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 34),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB2 + GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 35),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB2 + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 36),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB2 + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 37),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_INCUB2 + GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 38),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 39),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 40),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 41),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 42),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 43),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 44),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 45),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 46),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 47),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 48),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 49),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 50),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 51),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 52),
            new LPAPIArguments(GlobalAPIsParams.LBL_PREFIX_ALLPENDINGANYINCUB + GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 53),}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        SAMPLES_BY_STAGE("SAMPLES_BY_STAGE", "",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 14),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 15),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 16),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 17),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 18),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 19),
                    new LPAPIArguments("includeOnlyIfResultsInProgress", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 20),}, null, null,
                null, null),
        SAMPLES_INPROGRESS_LIST("SAMPLES_INPROGRESS_LIST", "",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 14),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 15),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 16),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 17),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 18),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 19),
                    new LPAPIArguments("includeOnlyIfResultsInProgress", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 20),}, null, null,
                null, null);

        private EnvMonSampleAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, JsonArray reportInfo, String devComment, String devCommentTag) {
            this.name = name;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.outputObjectTypes = outputObjectTypes;
            this.reportInfo = reportInfo;
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);
        }

        public Map<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex) {
            Map<HttpServletRequest, Object[]> hm = new HashMap<>();
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
        public LPAPIArguments[] getArguments() {
            return arguments;
        }

        @Override
        public JsonArray getOutputObjectTypes() {
            return outputObjectTypes;
        }

        @Override
        public String getApiUrl() {
            return GlobalVariables.ApiUrls.ENVMON_SAMPLE_QUERIES.getUrl();
        }

        public JsonArray getReportInfo() {
            return reportInfo;
        }
        private final String name;
        private final String successMessageCode;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
        private final JsonArray reportInfo;

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
        public String getSuccessMessageCode() {
            return this.successMessageCode;
        }
    }

    public ClassEnvMonSampleFrontend(HttpServletRequest request, EnvMonSampleAPIqueriesEndpoints endPoint) {
        String reportInfoTagNAme = "report_info";
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())) {
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, null, procReqInstance.getErrorMessage(),
                    new Object[]{procReqInstance.getErrorMessage(), this.getClass().getSimpleName()}, procReqInstance.getLanguage(), null);
            return;
        }
        try {
            RelatedObjects rObj = RelatedObjects.getInstanceForActions();

            String batchName = "";
            Object[] actionDiagnoses = null;
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
                procReqInstance.killIt();
                this.isSuccess = false;
                this.diagnostic = (Object[]) argValues[1];
                this.messageDynamicData = new Object[]{argValues[2].toString()};
                return;
            }

            this.functionFound = true;
            switch (endPoint) {
                case GET_SAMPLE_ANALYSIS_LIST:
                    Integer sampleId = Integer.valueOf(LPNulls.replaceNull(argValues[0]).toString());
                    EnumIntTableFields[] tblFldsToGet = EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS, "ALL");

                    String[] sampleAnalysisWhereFieldsNameArr = new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName()};
                    Object[] sampleAnalysisWhereFieldsValueArr = new Object[]{sampleId};

                    String sampleAnalysisWhereFieldsName = LPNulls.replaceNull(argValues[2]).toString();
                    if ((sampleAnalysisWhereFieldsName != null) && (sampleAnalysisWhereFieldsName.length() > 0)) {
                        sampleAnalysisWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                    }
                    String sampleAnalysisWhereFieldsValue = LPNulls.replaceNull(argValues[3]).toString();
                    if ((sampleAnalysisWhereFieldsValue != null) && (sampleAnalysisWhereFieldsValue.length() > 0)) {
                        sampleAnalysisWhereFieldsValueArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")));
                    }
                    String[] sortFieldsNameArr = null;
                    String sortFieldsName = LPNulls.replaceNull(argValues[4]).toString();
                    if ((sortFieldsName != null) && (sortFieldsName.length() > 0)) {
                        sortFieldsNameArr = sortFieldsName.split("\\|");
                    } else {
                        sortFieldsNameArr = LPArray.getUniquesArray(SampleAPIParams.MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_GET_SAMPLE_ANALYSIS_LIST.split("\\|"));
                    }

                    Object[][] analysisList = QueryUtilitiesEnums.getTableData(TblsData.TablesData.SAMPLE_ANALYSIS,
                            tblFldsToGet,
                            new SqlWhere(TblsData.TablesData.SAMPLE_ANALYSIS, sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsValueArr),
                            sortFieldsNameArr);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisList[0][0].toString())) {
                        this.isSuccess = true;
                        this.responseSuccessJArr = new JSONArray();
                    } else {
                        JSONArray jArr = new JSONArray();
                        for (Object[] curRow : analysisList) {
                            JSONObject row = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(tblFldsToGet), curRow);
                            jArr.add(row);
                        }
                        Rdbms.closeRdbms();
                        this.isSuccess = true;
                        this.responseSuccessJArr = jArr;
                    }
                    return;                
                case GET_SAMPLE_ANALYSIS_RESULT_LIST:
                case GET_SAMPLE_ANALYSIS_RESULT_LIST_SECONDENTRY:
                    sampleId = Integer.valueOf(LPNulls.replaceNull(argValues[0]).toString());
                    String[] resultFieldToRetrieveArr = EnumIntViewFields.getAllFieldNames(EnumIntViewFields.getViewFieldsFromString(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, "ALL"));
                    EnumIntViewFields[] fldsToGet = EnumIntViewFields.getViewFieldsFromString(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, "ALL");
                    resultFieldToRetrieveArr = LPArray.getUniquesArray(LPArray.addValueToArray1D(resultFieldToRetrieveArr, SampleAPIParams.MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|")));

                    sampleAnalysisWhereFieldsNameArr = new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName()};
                    sampleAnalysisWhereFieldsValueArr = new Object[]{sampleId};

                    sampleAnalysisWhereFieldsName = LPNulls.replaceNull(argValues[2]).toString();
                    if ((sampleAnalysisWhereFieldsName != null) && (sampleAnalysisWhereFieldsName.length() > 0)) {
                        sampleAnalysisWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                    }
                    sampleAnalysisWhereFieldsValue = LPNulls.replaceNull(argValues[3]).toString();
                    if ((sampleAnalysisWhereFieldsValue != null) && (sampleAnalysisWhereFieldsValue.length() > 0)) {
                        sampleAnalysisWhereFieldsValueArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")));
                    }

                    String sarWhereFieldsName = LPNulls.replaceNull(argValues[4]).toString();
                    if ((sarWhereFieldsName != null) && (sarWhereFieldsName.length() > 0)) {
                        sampleAnalysisWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sarWhereFieldsName.split("\\|"));
                    }
                    String sarWhereFieldsValue = LPNulls.replaceNull(argValues[5]).toString();
                    if ((sarWhereFieldsValue != null) && (sarWhereFieldsValue.length() > 0)) {
                        sampleAnalysisWhereFieldsValueArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")));
                    }

                    sortFieldsNameArr = null;
                    sortFieldsName = LPNulls.replaceNull(argValues[6]).toString();
                    if ((sortFieldsName != null) && (sortFieldsName.length() > 0)) {
                        sortFieldsNameArr = sortFieldsName.split("\\|");
                    } else {
                        sortFieldsNameArr = LPArray.getUniquesArray(SampleAPIParams.MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|"));
                    }

                    Integer posicRawValueFld = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE.getName());
                    if (posicRawValueFld == -1) {
                        resultFieldToRetrieveArr = LPArray.addValueToArray1D(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE.getName());
                        posicRawValueFld = resultFieldToRetrieveArr.length;
                    }
                    Integer posicLimitIdFld = EnumIntViewFields.getFldPosicInArray(fldsToGet, TblsData.ViewSampleAnalysisResultWithSpecLimits.LIMIT_ID.getName());

                    Object[][] analysisResultList = QueryUtilitiesEnums.getViewData(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW,
                            fldsToGet,
                            new SqlWhere(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsValueArr),
                            sortFieldsNameArr);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisResultList[0][0].toString())) {
                        this.isSuccess = true;
                        this.responseSuccessJArr = new JSONArray();
                    } else {
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), sampleId);
                        Object[] objectsIds = getObjectsId(EnumIntViewFields.getAllFieldNames(fldsToGet), analysisResultList, "-");
                        for (Object curObj : objectsIds) {
                            String[] curObjDet = curObj.toString().split("-");
                            if (TblsData.SampleAnalysisResult.TEST_ID.getName().equalsIgnoreCase(curObjDet[0])) {
                                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), curObjDet[1]);
                            }
                            if (TblsData.SampleAnalysisResult.RESULT_ID.getName().equalsIgnoreCase(curObjDet[0])) {
                                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), curObjDet[1]);
                            }
                        }
                        JSONArray jArr = new JSONArray();
                        for (Object[] curRow : analysisResultList) {
                            ConfigSpecRule specRule = new ConfigSpecRule();
                            String currRowRawValue = curRow[posicRawValueFld].toString();
                            String currRowLimitId = curRow[posicLimitIdFld].toString();
                            JSONObject row = new JSONObject();

                            String[] allFieldNames = EnumIntViewFields.getAllFieldNames(fldsToGet);
                            Object[] resultWarningData = warningByMinOrMaxAllowed(EnumIntViewFields.getAllFieldNames(fldsToGet), curRow);

                            if (resultWarningData != null && resultWarningData[0] != null) {
                                allFieldNames = LPArray.addValueToArray1D(allFieldNames, (String[]) resultWarningData[0]);
                                curRow = LPArray.addValueToArray1D(curRow, (Object[]) resultWarningData[1]);
                                if (resultWarningData.length > 2) {
                                }
                            }

                            Object[] resultLockData = sampleAnalysisResultLockData(procInstanceName, EnumIntViewFields.getAllFieldNames(fldsToGet), curRow);
                            if (resultLockData != null && resultLockData[0] != null) {
                                allFieldNames = LPArray.addValueToArray1D(allFieldNames, (String[]) resultLockData[0]);
                                curRow = LPArray.addValueToArray1D(curRow, (Object[]) resultLockData[1]);
                            }
                            row = LPJson.convertArrayRowToJSONObject(EnumIntViewFields.getAllFieldNames(fldsToGet), curRow);
                            if (resultWarningData != null && resultWarningData[0] != null && resultWarningData.length > 2) {
                                row.put(resultWarningData[2], resultWarningData[3]);
                            }
                            if (resultLockData != null && resultLockData[0] != null && resultLockData.length > 2) {
                                row.put(resultLockData[2], resultLockData[3]);
                            }

                            if ((currRowLimitId != null) && (currRowLimitId.length() > 0)) {
                                specRule.specLimitsRule(Integer.valueOf(currRowLimitId), null);
                                row.put(ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_DETAILED, LPNulls.replaceNull(specRule.getRuleRepresentation()).replace(("R"), "R (" + currRowRawValue + ")"));
                                Object[][] specRuleDetail = specRule.getRuleData();
                                JSONArray specRuleDetailjArr = new JSONArray();
                                JSONObject specRuleDetailjObj = new JSONObject();
                                for (Object[] curSpcRlDet : specRuleDetail) {
                                    specRuleDetailjObj.put(curSpcRlDet[0], curSpcRlDet[1]);
                                }
                                specRuleDetailjArr.add(specRuleDetailjObj);
                                row.put(ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_INFO, specRuleDetailjArr);
                            }
                            jArr.add(row);
                        }
                        Rdbms.closeRdbms();
                        this.isSuccess = true;
                        this.responseSuccessJArr = jArr;
                    }
                    return;
                case GET_MICROORGANISM_LIST:
                    String[] fieldsToRetrieve = EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM.getTableFields());
                    Object[][] list = getTableData(procReqInstance, GlobalVariables.Schemas.CONFIG.getName(), TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM.getTableName(),
                            "", EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM.getTableFields()),
                            new String[]{TblsEnvMonitConfig.MicroOrganism.NAME.getName() + WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{},
                            new String[]{TblsEnvMonitConfig.MicroOrganism.NAME.getName()});
                    JSONArray jArr = new JSONArray();
                    for (Object[] curRec : list) {
                        JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRec);
                        jArr.add(jObj);
                    }
                    this.isSuccess = true;
                    this.responseSuccessJArr = jArr;
                    return;
                case GET_SAMPLE_MICROORGANISM_VIEW:
                    String fieldsNameToRetrieve = argValues[0].toString();
                    String whereFieldsName = argValues[1].toString();
                    if (whereFieldsName == null) {
                        whereFieldsName = "";
                    }
                    String whereFieldsValue = argValues[2].toString();
                    String[] whereFieldsNameArr = new String[0];
                    Object[] whereFieldsValueArr = new Object[0];
                    if ((whereFieldsName != null && whereFieldsName.length() > 0) && (whereFieldsValue != null && whereFieldsValue.length() > 0)) {
                        whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
                        whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|")));
                        for (int iFields = 0; iFields < whereFieldsNameArr.length; iFields++) {
                            if (Boolean.TRUE.equals(LPPlatform.isEncryptedField(procInstanceName, GlobalVariables.Schemas.DATA.getName(), TblsData.TablesData.SAMPLE.getTableName(), whereFieldsNameArr[iFields]))) {
                                Map<String, String> hm = LPPlatform.encryptEncryptableFieldsAddBoth(whereFieldsNameArr[iFields], whereFieldsValueArr[iFields].toString());
                                whereFieldsNameArr[iFields] = hm.keySet().iterator().next();
                                if (hm.get(whereFieldsNameArr[iFields]).length() != whereFieldsNameArr[iFields].length()) {
                                    String newWhereFieldValues = hm.get(whereFieldsNameArr[iFields]);
                                    whereFieldsValueArr[iFields] = newWhereFieldValues;
                                }
                            }
                            String[] tokenFieldValue = Token.getTokenFieldValue(whereFieldsValueArr[iFields].toString(), ProcedureRequestSession.getInstanceForActions(null, null, null).getTokenString());
                            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(tokenFieldValue[0])) {
                                whereFieldsValueArr[iFields] = tokenFieldValue[1];
                            }
                        }
                    }
                    whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.CURRENT_STAGE.getName(), TblsEnvMonitData.ViewSampleMicroorganismList.RAW_VALUE.getName() + WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()});
                    whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, new Object[]{"MicroorganismIdentification"});
                    if (fieldsNameToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(fieldsNameToRetrieve)) {
                        fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsEnvMonitData.ViewSampleMicroorganismList.values());
                    } else {
                        fieldsToRetrieve = fieldsNameToRetrieve.split("\\|");
                    }
                    fieldsToRetrieve = LPArray.getUniquesArray(fieldsToRetrieve);
                    SqlWhere wh = new SqlWhere(TblsEnvMonitData.ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW, whereFieldsNameArr, whereFieldsValueArr);
                    list = QueryUtilitiesEnums.getViewData(TblsEnvMonitData.ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW,
                            EnumIntViewFields.getViewFieldsFromString(TblsEnvMonitData.ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW, fieldsToRetrieve),
                            wh,
                            new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.SAMPLE_ID.getName()});
                    jArr = new JSONArray();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(list[0][0].toString())) {
                        this.isSuccess = true;
                        this.responseSuccessJArr = jArr;
                        return;
                    }
                    for (Object[] curRec : list) {
                        JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRec);
                        Integer fldSampleIdPosic = LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitData.ViewSampleMicroorganismList.SAMPLE_ID.getName());
                        if (fldSampleIdPosic > -1) {
                            JSONArray jMicArr = new JSONArray();
                            Integer curSmpId = Integer.valueOf(curRec[fldSampleIdPosic].toString());
                            Object[][] grouper = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE_MICROORGANISM.getTableName(),
                                    new String[]{TblsEnvMonitData.SampleMicroorganism.MICROORG_NAME.getName()},
                                    new String[]{TblsEnvMonitData.SampleMicroorganism.SAMPLE_ID.getName()},
                                    new Object[]{curSmpId}, new String[]{TblsEnvMonitData.SampleMicroorganism.MICROORG_NAME.getName()});
                            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(grouper[0][0].toString()))) {
                                for (Object[] curMic : grouper) {
                                    JSONObject jmicObj = new JSONObject();
                                    jmicObj.put("name", curMic[0]);
                                    jmicObj.put("items", curMic[1]);
                                    jMicArr.add(jmicObj);
                                }
                            }
                            jObj.put("microorganism_list_array", jMicArr);
                        }
                        jArr.add(jObj);
                    }
                    this.isSuccess = true;
                    this.responseSuccessJArr = jArr;
                    return;
                case GET_SAMPLE_STAGES_SUMMARY_REPORT:
                    sampleId = (Integer) argValues[0];
                    String sampleToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE);
                    String[] sampleToRetrieveArr = new String[0];
                    if ((sampleToRetrieve != null) && (sampleToRetrieve.length() > 0)) {
                        if ("ALL".equalsIgnoreCase(sampleToRetrieve)) {
                            sampleToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableFields());
                        } else {
                            sampleToRetrieveArr = sampleToRetrieve.split("\\|");
                        }
                    }
                    sampleToRetrieveArr = LPArray.addValueToArray1D(sampleToRetrieveArr, TblsEnvMonitData.Sample.SAMPLE_ID.getName());
                    String sampleToDisplay = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_DISPLAY);
                    String[] sampleToDisplayArr = new String[0];
                    if ((sampleToDisplay != null) && (sampleToDisplay.length() > 0)) {
                        if ("ALL".equalsIgnoreCase(sampleToDisplay)) {
                            sampleToDisplayArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableFields());
                        } else {
                            sampleToDisplayArr = sampleToDisplay.split("\\|");
                        }
                    }
                    String[] sampleTblAllFields = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.SAMPLE);
                    Object[][] sampleInfo = QueryUtilitiesEnums.getTableData(TblsEnvMonitData.TablesEnvMonitData.SAMPLE,
                            EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.SAMPLE, "ALL"),
                            new String[]{TblsEnvMonitData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId},
                            sampleTblAllFields);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
                        this.isSuccess = false;
                        this.responseError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND,
                                new Object[]{sampleId, TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName()});
                        return;
                    }
                    JSONObject jObjSampleInfo = new JSONObject();
                    JSONObject jObjMainObject = new JSONObject();
                    JSONObject jObjPieceOfInfo = new JSONObject();
                    JSONArray jArrPieceOfInfo = new JSONArray();
                    for (int iFlds = 0; iFlds < sampleInfo[0].length; iFlds++) {
                        if (LPArray.valueInArray(sampleToRetrieveArr, sampleTblAllFields[iFlds])) {
                            jObjSampleInfo.put(sampleTblAllFields[iFlds], sampleInfo[0][iFlds].toString());
                        }
                    }
                    for (String sampleToDisplayArr1 : sampleToDisplayArr) {
                        if (LPArray.valueInArray(sampleTblAllFields, sampleToDisplayArr1)) {
                            jObjPieceOfInfo = new JSONObject();
                            jObjPieceOfInfo.put(GlobalAPIsParams.LBL_FIELD_NAME, sampleToDisplayArr1);
                            jObjPieceOfInfo.put(GlobalAPIsParams.LBL_FIELD_VALUE, sampleInfo[0][LPArray.valuePosicInArray(sampleTblAllFields, sampleToDisplayArr1)].toString());
                            jArrPieceOfInfo.add(jObjPieceOfInfo);
                        }
                    }

                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, jObjSampleInfo);
                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_DISPLAY, jArrPieceOfInfo);
                    jObjMainObject.put(reportInfoTagNAme, endPoint.getReportInfo());
                    jObjMainObject.put("buttonActionInfo", buttonActionInfo(sampleId, sampleTblAllFields));

                    JSONArray jArrMainObj = new JSONArray();
                    jObjPieceOfInfo = new JSONObject();
                    DataSampleStages smpStage = new DataSampleStages();
                    String[] sampleStageTimingCaptureAllFlds = getAllFieldNames(TblsProcedure.TablesProcedure.SAMPLE_STAGE_TIMING_CAPTURE.getTableFields());

                    if (Boolean.TRUE.equals(smpStage.isSampleStagesEnable())) {
                        Object[][] sampleStageInfo = QueryUtilitiesEnums.getTableData(TblsProcedure.TablesProcedure.SAMPLE_STAGE_TIMING_CAPTURE,
                                EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.SAMPLE_STAGE_TIMING_CAPTURE, sampleStageTimingCaptureAllFlds),
                                new String[]{TblsProcedure.SampleStageTimingCapture.SAMPLE_ID.getName()}, new Object[]{sampleId},
                                new String[]{TblsProcedure.SampleStageTimingCapture.ID.getName()}, null, false);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleStageInfo[0][0].toString())) {
                            this.isSuccess = false;
                            this.responseError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{sampleId, TblsProcedure.TablesProcedure.SAMPLE_STAGE_TIMING_CAPTURE.getTableName()});
                            return;
                        }
                        for (Object[] curRec : sampleStageInfo) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(sampleStageTimingCaptureAllFlds, curRec);
                            JSONArray jArrMainObj2 = new JSONArray();
                            jArrMainObj2 = sampleStageDataJsonArr(sampleId, sampleTblAllFields, sampleInfo[0], sampleStageTimingCaptureAllFlds, curRec);
                            jObj.put("data", jArrMainObj2);
                            jArrMainObj.add(jObj);
                        }
                    }
                    jObjMainObject.put("stages", jArrMainObj);

                    this.isSuccess = true;
                    this.responseSuccessJObj = jObjMainObject;
                    return;
                case GET_SAMPLE_BY_TESTINGGROUP_SUMMARY_REPORT:
                    sampleId = (Integer) argValues[0];
                    sampleToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE);
                    sampleToRetrieveArr = new String[0];
                    if ((sampleToRetrieve != null) && (sampleToRetrieve.length() > 0)) {
                        if ("ALL".equalsIgnoreCase(sampleToRetrieve)) {
                            sampleToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.SAMPLE);
                        } else {
                            sampleToRetrieveArr = sampleToRetrieve.split("\\|");
                        }
                    }
                    sampleToRetrieveArr = LPArray.addValueToArray1D(sampleToRetrieveArr, TblsEnvMonitData.Sample.SAMPLE_ID.getName());
                    sampleToRetrieveArr = LPArray.getUniquesArray(sampleToRetrieveArr);
                    sampleToDisplay = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_DISPLAY);
                    sampleToDisplayArr = new String[0];
                    if ((sampleToDisplay != null) && (sampleToDisplay.length() > 0)) {
                        if ("ALL".equalsIgnoreCase(sampleToDisplay)) {
                            sampleToDisplayArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.SAMPLE);
                        } else {
                            sampleToDisplayArr = sampleToDisplay.split("\\|");
                        }
                    }

                    sampleInfo = QueryUtilitiesEnums.getTableData(TblsEnvMonitData.TablesEnvMonitData.SAMPLE,
                            EnumIntTableFields.getAllFieldNamesFromDatabase(TblsEnvMonitData.TablesEnvMonitData.SAMPLE),
                            new String[]{TblsEnvMonitData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, null);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
                        this.isSuccess = false;
                        this.responseError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND,
                                new Object[]{sampleId,
                                    TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName()});
                        return;
                    }
                    jObjMainObject = new JSONObject();
                    jObjSampleInfo = new JSONObject();
                    jObjSampleInfo = LPJson.convertArrayRowToJSONObject(sampleToRetrieveArr, sampleInfo[0]);
                    String[] testingGroupFldsArr = getAllFieldNames(TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP);
                    Object[][] testingGroupInfo = QueryUtilitiesEnums.getTableData(TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP,
                            EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP, testingGroupFldsArr),
                            new String[]{TblsData.SampleRevisionTestingGroup.SAMPLE_ID.getName()}, new Object[]{sampleId}, null);
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(testingGroupInfo[0][0].toString()))) {
                        JSONArray tstGrpJsArr = new JSONArray();
                        for (Object[] curTstGrp : testingGroupInfo) {
                            JSONObject curTstGrpJObj = LPJson.convertArrayRowToJSONObject(testingGroupFldsArr, curTstGrp);
                            String curTstGrpName = LPNulls.replaceNull(curTstGrp[LPArray.valuePosicInArray(testingGroupFldsArr, TblsData.SampleRevisionTestingGroup.TESTING_GROUP.getName())]).toString();
                            String[] testFldsArr = getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS.getTableFields());
                            Object[][] testInfo = QueryUtilitiesEnums.getTableData(TblsData.TablesData.SAMPLE_ANALYSIS,
                                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS, testFldsArr),
                                    new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.TESTING_GROUP.getName()}, new Object[]{sampleId, curTstGrpName},
                                    null);
                            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(testInfo[0][0].toString()))) {
                                JSONArray testJsArr = new JSONArray();
                                for (Object[] curTest : testInfo) {
                                    JSONObject curTestJsObj = LPJson.convertArrayRowToJSONObject(testFldsArr, curTest);
                                    Integer curTestId = Integer.valueOf(LPNulls.replaceNull(curTest[LPArray.valuePosicInArray(testFldsArr, TblsData.SampleAnalysis.TEST_ID.getName())]).toString());
                                    String[] resultFldsArr = getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableFields());
                                    Object[][] resultInfo = QueryUtilitiesEnums.getTableData(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                                            EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, resultFldsArr),
                                            new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{sampleId, curTestId},
                                            null);
                                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString()))) {
                                        JSONArray resultJsArr = new JSONArray();
                                        for (Object[] curResult : resultInfo) {
                                            JSONObject curResultJsObj = LPJson.convertArrayRowToJSONObject(resultFldsArr, curResult);
                                            resultJsArr.add(curResultJsObj);
                                        }
                                        curTestJsObj.put(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), resultJsArr);
                                    }

                                    testJsArr.add(curTestJsObj);
                                }
                                curTstGrpJObj.put(TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), testJsArr);
                            }
                            Object[][] testWithSpecsInfo = QueryUtilitiesEnums.getViewData(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW,
                                    EnumIntViewFields.getViewFieldsFromString(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, "ALL"),
                                    new SqlWhere(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_ID.getName(),
                                TblsData.ViewSampleAnalysisResultWithSpecLimits.TESTING_GROUP.getName()}, new Object[]{sampleId, curTstGrpName}),
                                    null);
                            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(testWithSpecsInfo[0][0].toString()))) {
                                JSONArray testJsArr = new JSONArray();
                                for (Object[] curTestWSpec : testWithSpecsInfo) {
                                    JSONObject curTestJsObj = LPJson.convertArrayRowToJSONObject(testFldsArr, curTestWSpec);
                                    testJsArr.add(curTestJsObj);
                                }
                                curTstGrpJObj.put(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW.getViewName(), testJsArr);
                            }
                            tstGrpJsArr.add(curTstGrpJObj);
                        }
                        jObjSampleInfo.put(TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP.getTableName(), tstGrpJsArr);
                    }

                    this.isSuccess = true;
                    jObjMainObject.put("sample_id", sampleId.toString());
                    jObjMainObject.put("sample", jObjSampleInfo);
                    jObjMainObject.put(reportInfoTagNAme, endPoint.getReportInfo());
                    this.responseSuccessJObj = jObjMainObject;
                    return;
                case GET_BATCH_REPORT:
                    batchName = argValues[0].toString();
                    String fieldsToRetrieveStr = argValues[1].toString();
                    String prodLotfieldsToDisplayStr = argValues[2].toString();
                    String[] fieldToRetrieveArr = new String[0];
                    if ((fieldsToRetrieveStr != null) && (fieldsToRetrieveStr.length() > 0)) {
                        if ("ALL".equalsIgnoreCase(fieldsToRetrieveStr)) {
                            fieldToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableFields());
                        } else {
                            fieldToRetrieveArr = fieldsToRetrieveStr.split("\\|");
                        }
                    }
                    fieldToRetrieveArr = LPArray.addValueToArray1D(fieldToRetrieveArr, TblsEnvMonitData.IncubBatch.NAME.getName());
                    String[] fieldToDisplayArr = new String[0];
                    if ((prodLotfieldsToDisplayStr != null) && (prodLotfieldsToDisplayStr.length() > 0)) {
                        if ("ALL".equalsIgnoreCase(prodLotfieldsToDisplayStr)) {
                            fieldToDisplayArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableFields());
                        } else {
                            fieldToDisplayArr = prodLotfieldsToDisplayStr.split("\\|");
                        }
                    }
                    String[] batchTblAllFields = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableFields());
                    Object[][] batchInfo = QueryUtilitiesEnums.getTableData(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH,
                            EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH, batchTblAllFields),
                            new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, null, null, true);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())) {
                        this.isSuccess = false;
                        this.responseError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{batchName, TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName()});
                        return;
                    }
                    JSONObject jObjBatchInfo = new JSONObject();
                    jObjMainObject = new JSONObject();
                    jObjPieceOfInfo = new JSONObject();
                    jArrPieceOfInfo = new JSONArray();
                    for (int iFlds = 0; iFlds < batchInfo[0].length; iFlds++) {
                        if (LPArray.valueInArray(fieldToRetrieveArr, batchTblAllFields[iFlds])) {
                            jObjBatchInfo.put(batchTblAllFields[iFlds], batchInfo[0][iFlds].toString());
                        }
                    }
                    for (String fieldToDisplayArr1 : fieldToDisplayArr) {
                        if (LPArray.valueInArray(batchTblAllFields, fieldToDisplayArr1)) {
                            jObjPieceOfInfo = new JSONObject();
                            jObjPieceOfInfo.put(GlobalAPIsParams.LBL_FIELD_NAME, fieldToDisplayArr1);
                            jObjPieceOfInfo.put(GlobalAPIsParams.LBL_FIELD_VALUE, batchInfo[0][LPArray.valuePosicInArray(batchTblAllFields, fieldToDisplayArr1)].toString());
                            jArrPieceOfInfo.add(jObjPieceOfInfo);
                        }
                    }
                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_BATCH_FIELD_TO_RETRIEVE, jObjBatchInfo);
                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_BATCH_FIELD_TO_DISPLAY, jArrPieceOfInfo);

                    Object[] incubBatchContentInfo = EnvMonIncubBatchAPIfrontend.incubBatchContentJson(batchTblAllFields, batchInfo[0]);
                    jObjMainObject.put("SAMPLES_ARRAY", incubBatchContentInfo[0]);
                    jObjMainObject.put("NUM_SAMPLES", incubBatchContentInfo[1]);

                    String incubName = batchInfo[0][LPArray.valuePosicInArray(EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableFields()), TblsEnvMonitData.IncubBatch.INCUBATION_INCUBATOR.getName())].toString();
                    Object incubStart = batchInfo[0][LPArray.valuePosicInArray(EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableFields()), TblsEnvMonitData.IncubBatch.INCUBATION_START.getName())];
                    Object incubEnd = batchInfo[0][LPArray.valuePosicInArray(EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableFields()), TblsEnvMonitData.IncubBatch.INCUBATION_END.getName())];
                    JSONArray jArrLastTempReadings = new JSONArray();
                    if (LPNulls.replaceNull(incubName).length() == 0 || LPNulls.replaceNull(incubStart.toString()).length() == 0 || LPNulls.replaceNull(incubEnd.toString()).length() == 0) {
                        JSONObject jObj = new JSONObject();
                        jObj.put(GlobalAPIsParams.LBL_ERROR, "This is not a completed batch so temperature readings cannot be"
                                + ". IncubName:" + incubName + ". incubStart:" + incubStart + ". incubEnd:" + incubEnd);
                        jArrLastTempReadings.add(jObj);
                    } else {
                        fieldsToRetrieve = new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.ID.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.EVENT_TYPE.getName(),
                            TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_BY.getName(),
                            TblsEnvMonitData.InstrIncubatorNoteBook.TEMPERATURE.getName()};
                        Object[][] instrReadings = QueryUtilitiesEnums.getTableData(TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK,
                                EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK, fieldsToRetrieve),
                                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName() + " BETWEEN "},
                                new Object[]{incubName, incubStart, incubEnd},
                                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName()});
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrReadings[0][0].toString())) {
                            JSONObject jObj = new JSONObject();
                            jObj.put(GlobalAPIsParams.LBL_ERROR, "No temperature readings found");
                            jArrLastTempReadings.add(jObj);
                        } else {
                            for (Object[] currReading : instrReadings) {
                                JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currReading);
                                jArrLastTempReadings.add(jObj);
                            }
                        }
                    }
                    jObjMainObject.put(GlobalAPIsParams.BATCH_REPORT_JSON_TAG_NAME_TEMP_READINGS, jArrLastTempReadings);
                    jObjMainObject.put(reportInfoTagNAme, endPoint.getReportInfo());
                    this.isSuccess = true;
                    this.responseSuccessJObj = jObjMainObject;
                    break;
                case GET_PRODLOT_REPORT:
                    String lotName = argValues[0].toString();
                    String prodLotfieldsToRetrieveStr = argValues[1].toString();
                    prodLotfieldsToDisplayStr = argValues[2].toString();
                    String[] prodLotfieldToRetrieveArr = null;
                    if ((prodLotfieldsToRetrieveStr != null) && (prodLotfieldsToRetrieveStr.length() > 0)) {
                        if ("ALL".equalsIgnoreCase(prodLotfieldsToRetrieveStr)) {
                            prodLotfieldToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableFields());
                        } else {
                            prodLotfieldToRetrieveArr = prodLotfieldsToRetrieveStr.split("\\|");
                        }
                    }
                    if (prodLotfieldToRetrieveArr == null) {
                        prodLotfieldToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableFields());
                    }
                    prodLotfieldToRetrieveArr = LPArray.addValueToArray1D(prodLotfieldToRetrieveArr, TblsEnvMonitData.ProductionLot.LOT_NAME.getName());
                    String[] prodLotfieldToDisplayArr = null;
                    if ((prodLotfieldsToDisplayStr != null) && (prodLotfieldsToDisplayStr.length() > 0)) {
                        if ("ALL".equalsIgnoreCase(prodLotfieldsToDisplayStr)) {
                            prodLotfieldToDisplayArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableFields());
                        } else {
                            prodLotfieldToDisplayArr = prodLotfieldsToDisplayStr.split("\\|");
                        }
                    }
                    if (prodLotfieldToDisplayArr == null) {
                        prodLotfieldToDisplayArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableFields());
                    }
                    String[] prodLotTblAllFields = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableFields());
                    Object[][] prodLotInfo = QueryUtilitiesEnums.getTableData(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT,
                            EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT, prodLotTblAllFields),
                            new String[]{TblsEnvMonitData.ProductionLot.LOT_NAME.getName()}, new Object[]{lotName}, null);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(prodLotInfo[0][0].toString())) {
                        this.isSuccess = false;
                        this.responseError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableName()});
                        return;
                    }
                    JSONObject jObjProdLotInfo = new JSONObject();
                    jObjMainObject = new JSONObject();
                    jObjPieceOfInfo = new JSONObject();
                    jArrPieceOfInfo = new JSONArray();
                    for (int iFlds = 0; iFlds < prodLotInfo[0].length; iFlds++) {
                        if (LPArray.valueInArray(prodLotfieldToRetrieveArr, prodLotTblAllFields[iFlds])) {
                            jObjProdLotInfo.put(prodLotTblAllFields[iFlds], prodLotInfo[0][iFlds].toString());
                        }
                    }
                    for (String fieldToDisplayArr1 : prodLotfieldToDisplayArr) {
                        if (LPArray.valueInArray(prodLotTblAllFields, fieldToDisplayArr1)) {
                            jObjPieceOfInfo = new JSONObject();
                            jObjPieceOfInfo.put(GlobalAPIsParams.LBL_FIELD_NAME, fieldToDisplayArr1);
                            jObjPieceOfInfo.put(GlobalAPIsParams.LBL_FIELD_VALUE, prodLotInfo[0][LPArray.valuePosicInArray(prodLotTblAllFields, fieldToDisplayArr1)].toString());
                            jArrPieceOfInfo.add(jObjPieceOfInfo);
                        }
                    }
                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_PRODLOT_FIELD_TO_RETRIEVE, jObjProdLotInfo);
                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_PRODLOT_FIELD_TO_DISPLAY, jArrPieceOfInfo);

                    String sampleFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE);
                    String[] sampleFieldToRetrieveArr = new String[0];
                    if ((sampleFieldToRetrieve != null) && (sampleFieldToRetrieve.length() > 0)) {
                        if ("ALL".equalsIgnoreCase(sampleFieldToRetrieve)) {
                            sampleFieldToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableFields());
                        } else {
                            sampleFieldToRetrieveArr = sampleFieldToRetrieve.split("\\|");
                        }
                    }
                    if (sampleFieldToRetrieve == null) {
                        sampleFieldToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableFields());
                    }
                    String sampleWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_WHERE_FIELDS_NAME);
                    String[] sampleWhereFieldsNameArr = new String[0];
                    if (sampleWhereFieldsName != null && sampleWhereFieldsName.length() > 0) {
                        sampleWhereFieldsNameArr = sampleWhereFieldsName.split("\\|");
                    }
                    String sampleWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_WHERE_FIELDS_VALUE);
                    Object[] sampleWhereFieldsValueArr = new Object[0];
                    if (sampleWhereFieldsValue != null && sampleWhereFieldsValue.length() > 0) {
                        sampleWhereFieldsValueArr = LPArray.convertStringWithDataTypeToObjectArray(sampleWhereFieldsValue.split("\\|"));
                    }
                    if (Boolean.FALSE.equals(LPArray.valueInArray(sampleWhereFieldsNameArr, TblsEnvMonitData.Sample.PRODUCTION_LOT.getName()))) {
                        sampleWhereFieldsNameArr = LPArray.addValueToArray1D(sampleWhereFieldsNameArr, TblsEnvMonitData.Sample.PRODUCTION_LOT.getName());
                        sampleWhereFieldsValueArr = LPArray.addValueToArray1D(sampleWhereFieldsValueArr, lotName);
                    }
                    sampleInfo = QueryUtilitiesEnums.getTableData(TblsEnvMonitData.TablesEnvMonitData.SAMPLE,
                            EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.SAMPLE, sampleFieldToRetrieveArr),
                            sampleWhereFieldsNameArr, sampleWhereFieldsValueArr, new String[]{TblsEnvMonitData.Sample.SAMPLE_ID.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    JSONObject jObj = new JSONObject();
                    JSONArray sampleJsonArr = new JSONArray();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
                        jObj = LPFrontEnd.responseJSONDiagnosticLPFalse(Rdbms.RdbmsErrorTrapping.TABLE_WITH_NO_RECORDS, new Object[0]);
                    } else {
                        sampleJsonArr.add(jObj);
                        for (Object[] curRec : sampleInfo) {
                            jObj = LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, curRec);
                            sampleJsonArr.add(jObj);
                        }
                    }
                    jObjMainObject.put(TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), sampleJsonArr);

                    sampleJsonArr = new JSONArray();
                    for (String fieldToDisplayArr1 : sampleFieldToRetrieveArr) {
                        jObjPieceOfInfo = new JSONObject();
                        jObjPieceOfInfo.put(GlobalAPIsParams.LBL_FIELD_NAME, fieldToDisplayArr1);
                        sampleJsonArr.add(jObjPieceOfInfo);
                    }
                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_DISPLAY, sampleJsonArr);

                    String sampleGroups = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_GROUPS);
                    if (sampleGroups != null) {
                        String[] sampleGroupsArr = sampleGroups.split("\\|");
                        for (String currGroup : sampleGroupsArr) {
                            JSONArray sampleGrouperJsonArr = new JSONArray();
                            String[] groupInfo = currGroup.split("\\*");
                            String[] smpGroupFldsArr = groupInfo[0].split(",");
                            Object[][] groupedInfo = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(),
                                    smpGroupFldsArr, new String[]{TblsEnvMonitData.Sample.PRODUCTION_LOT.getName()}, new Object[]{lotName},
                                    null);
                            smpGroupFldsArr = LPArray.addValueToArray1D(smpGroupFldsArr, "count");
                            smpGroupFldsArr = LPArray.addValueToArray1D(smpGroupFldsArr, "grouper");
                            jObj = new JSONObject();
                            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
                                jObj = LPFrontEnd.responseJSONDiagnosticLPFalse(Rdbms.RdbmsErrorTrapping.TABLE_WITH_NO_RECORDS, new Object[0]);
                            } else {
                                for (Object[] curRec : groupedInfo) {
                                    jObj = LPJson.convertArrayRowToJSONObject(smpGroupFldsArr, curRec);
                                    sampleGrouperJsonArr.add(jObj);
                                }
                            }
                            jObjMainObject.put(groupInfo[1], sampleGrouperJsonArr);
                        }
                    }
                    jObjMainObject.put(reportInfoTagNAme, endPoint.getReportInfo());
                    this.isSuccess = true;
                    this.responseSuccessJObj = jObjMainObject;
                    break;
                case GET_INCUBATOR_REPORT:
                    lotName = argValues[0].toString();
                    prodLotfieldsToRetrieveStr = argValues[1].toString();
                    prodLotfieldsToDisplayStr = argValues[2].toString();

                    String startDateStr = argValues[3].toString();
                    String endDateStr = argValues[4].toString();

                    prodLotfieldToRetrieveArr = new String[0];
                    if ((prodLotfieldsToRetrieveStr != null) && (prodLotfieldsToRetrieveStr.length() > 0)) {
                        if ("ALL".equalsIgnoreCase(prodLotfieldsToRetrieveStr)) {
                            prodLotfieldToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableFields());
                        } else {
                            prodLotfieldToRetrieveArr = prodLotfieldsToRetrieveStr.split("\\|");
                        }
                    }
                    prodLotfieldToRetrieveArr = LPArray.addValueToArray1D(prodLotfieldToRetrieveArr, TblsEnvMonitConfig.InstrIncubator.NAME.getName());
                    prodLotfieldToDisplayArr = new String[0];
                    if ((prodLotfieldsToDisplayStr != null) && (prodLotfieldsToDisplayStr.length() > 0)) {
                        if ("ALL".equalsIgnoreCase(prodLotfieldsToDisplayStr)) {
                            prodLotfieldToDisplayArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableFields());
                        } else {
                            prodLotfieldToDisplayArr = prodLotfieldsToDisplayStr.split("\\|");
                        }
                    }
                    String[] incubTblAllFields = EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableFields());
                    Object[][] incubInfo = QueryUtilitiesEnums.getTableData(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR,
                            EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, incubTblAllFields),
                            new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()}, new Object[]{lotName}, null);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubInfo[0][0].toString())) {
                        this.isSuccess = false;
                        this.responseError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName()});
                        return;
                    }
                    jObjProdLotInfo = new JSONObject();
                    jObjMainObject = new JSONObject();
                    jObjPieceOfInfo = new JSONObject();
                    jArrPieceOfInfo = new JSONArray();
                    for (int iFlds = 0; iFlds < incubInfo[0].length; iFlds++) {
                        if (LPArray.valueInArray(prodLotfieldToRetrieveArr, incubTblAllFields[iFlds])) {
                            jObjProdLotInfo.put(incubTblAllFields[iFlds], incubInfo[0][iFlds].toString());
                        }
                    }
                    for (String fieldToDisplayArr1 : prodLotfieldToDisplayArr) {
                        if (LPArray.valueInArray(incubTblAllFields, fieldToDisplayArr1)) {
                            jObjPieceOfInfo = new JSONObject();
                            jObjPieceOfInfo.put(GlobalAPIsParams.LBL_FIELD_NAME, fieldToDisplayArr1);
                            jObjPieceOfInfo.put(GlobalAPIsParams.LBL_FIELD_VALUE, incubInfo[0][LPArray.valuePosicInArray(incubTblAllFields, fieldToDisplayArr1)].toString());
                            jArrPieceOfInfo.add(jObjPieceOfInfo);
                        }
                    }
                    jObjMainObject.put(GlobalAPIsParams.INCUBATION_REPORT_JSON_TAG_NAME_FIELD_TO_RETRIEVE, jObjProdLotInfo);
                    jObjMainObject.put(GlobalAPIsParams.INCUBATION_REPORT_JSON_TAG_NAME_FIELD_TO_DISPLAY, jArrPieceOfInfo);

                    String numPoints = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_INCUBATOR_NUM_POINTS);
                    Integer numPointsInt = null;
                    fieldsToRetrieve = new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.ID.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.EVENT_TYPE.getName(),
                        TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_BY.getName(),
                        TblsEnvMonitData.InstrIncubatorNoteBook.TEMPERATURE.getName()};
                    if (numPoints != null) {
                        numPointsInt = Integer.valueOf(numPoints);
                    } else {
                        numPointsInt = 20;
                    }
                    Object[][] instrReadings = new Object[0][0];
                    if (startDateStr == null && endDateStr == null) {
                        instrReadings = DataIncubatorNoteBook.getLastTemperatureReading(lotName, numPointsInt);
                    }
                    if (startDateStr != null && endDateStr == null) {

                        startDateStr = startDateStr.replace(" ", "T");
                        instrReadings = DataIncubatorNoteBook.getLastTemperatureReading(lotName, numPointsInt, dateStringFormatToLocalDateTime(startDateStr));
                    }
                    if (startDateStr != null && endDateStr != null) {
                        startDateStr = startDateStr.replace(" ", "T");
                        endDateStr = endDateStr.replace(" ", "T");
                        instrReadings = DataIncubatorNoteBook.getLastTemperatureReading(lotName, numPointsInt, dateStringFormatToLocalDateTime(startDateStr), dateStringFormatToLocalDateTime(endDateStr), true);
                    }
                    jArrLastTempReadings = new JSONArray();
                    for (Object[] currReading : instrReadings) {
                        jObj = new JSONObject();
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(currReading[0].toString())) {
                            jObj.put(GlobalAPIsParams.LBL_ERROR, "No temperature readings found");
                        } else {
                            jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currReading);
                        }

                        jArrLastTempReadings.add(jObj);
                    }
                    jObjMainObject.put(GlobalAPIsParams.INCUBATION_REPORT_JSON_TAG_NAME_LAST_N_TEMP_READINGS, jArrLastTempReadings);
                    jObjMainObject.put(reportInfoTagNAme, endPoint.getReportInfo());
                    this.isSuccess = true;
                    this.responseSuccessJObj = jObjMainObject;
                    break;

                case STATS_SAMPLES_PER_STAGE:
                    String[] whereFieldNames = new String[0];
                    Object[] whereFieldValues = new Object[0];
                    prodLotfieldToRetrieveArr = new String[]{TblsEnvMonitData.Sample.CURRENT_STAGE.getName()};
                    String programName = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME);
                    if (programName != null) {
                        whereFieldNames = new String[]{TblsEnvMonitData.Sample.PROGRAM_NAME.getName()};
                        whereFieldValues = new Object[]{programName};
                    }
                    String stagesToInclude = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_STAGES_TO_INCLUDE);
                    if (stagesToInclude != null) {
                        whereFieldNames = LPArray.addValueToArray1D(whereFieldNames, TblsEnvMonitData.Sample.CURRENT_STAGE.getName() + " in|");
                        whereFieldValues = LPArray.addValueToArray1D(whereFieldValues, stagesToInclude);
                    }
                    String stagesToExclude = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_STAGES_TO_EXCLUDE);
                    if (stagesToExclude != null) {
                        whereFieldNames = LPArray.addValueToArray1D(whereFieldNames, TblsEnvMonitData.Sample.CURRENT_STAGE.getName() + " not in|");
                        whereFieldValues = LPArray.addValueToArray1D(whereFieldValues, stagesToExclude);
                    }

                    if (whereFieldNames.length == 0) {
                        whereFieldNames = new String[]{TblsEnvMonitData.Sample.PROGRAM_NAME.getName() + " not in"};
                        whereFieldValues = new Object[]{"<<"};
                    }
                    Object[][] samplesCounterPerStage = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(),
                            prodLotfieldToRetrieveArr,
                            whereFieldNames, whereFieldValues,
                            new String[]{"COUNTER desc"});
                    prodLotfieldToRetrieveArr = LPArray.addValueToArray1D(prodLotfieldToRetrieveArr, "COUNTER");
                    jArr = new JSONArray();
                    for (Object[] curRec : samplesCounterPerStage) {
                        jObj = LPJson.convertArrayRowToJSONObject(prodLotfieldToRetrieveArr, curRec);
                        jArr.add(jObj);
                    }
                    this.isSuccess = true;
                    this.responseSuccessJArr = jArr;
                    return;
                case STATS_PROGRAM_LAST_RESULTS:
                    String grouped = argValues[0].toString();
                    prodLotfieldsToRetrieveStr = argValues[1].toString();
                    prodLotfieldToRetrieveArr = new String[0];
                    Integer numTotalRecords = 50;
                    String numTotalRecordsStr = LPNulls.replaceNull(argValues[2]).toString();
                    if (numTotalRecordsStr == null || numTotalRecordsStr.length() == 0) {
                        numTotalRecords = 50;
                    } else {
                        numTotalRecords = Integer.valueOf(LPNulls.replaceNull(argValues[2].toString()));
                    }
                    if ((prodLotfieldsToRetrieveStr != null) && (prodLotfieldsToRetrieveStr.length() > 0)) {
                        if ("ALL".equalsIgnoreCase(prodLotfieldsToRetrieveStr)) {
                            prodLotfieldToRetrieveArr = EnumIntViewFields.getAllFieldNames(TblsData.ViewSampleAnalysisResultWithSpecLimits.values());
                        } else {
                            prodLotfieldToRetrieveArr = prodLotfieldsToRetrieveStr.split("\\|");
                        }
                    } else {
                        prodLotfieldToRetrieveArr = EnumIntViewFields.getAllFieldNames(TblsData.ViewSampleAnalysisResultWithSpecLimits.values());
                    }
                    if (grouped == null || !Boolean.valueOf(grouped)) {
                        whereFieldNames = new String[0];
                        whereFieldValues = new Object[0];
                        programName = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME);
                        if (programName != null) {
                            whereFieldNames = LPArray.addValueToArray1D(whereFieldNames, TblsData.ViewSampleAnalysisResultWithSpecLimits.PROGRAM_NAME.getName());
                            whereFieldValues = LPArray.addValueToArray1D(whereFieldValues, programName);
                        }
                        whereFieldNames = LPArray.addValueToArray1D(whereFieldNames, TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE.getName() + WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause());

                        Object[][] programLastResults = QueryUtilitiesEnums.getViewData(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW,
                                EnumIntViewFields.getViewFieldsFromString(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, prodLotfieldToRetrieveArr),
                                new SqlWhere(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, whereFieldNames, whereFieldValues),
                                new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.ENTERED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                        if (numTotalRecords > programLastResults.length) {
                            numTotalRecords = programLastResults.length;
                        }
                        jArr = new JSONArray();
                        for (int i = 0; i < numTotalRecords; i++) {
                            jObj = LPJson.convertArrayRowToJSONObject(prodLotfieldToRetrieveArr, programLastResults[i]);
                            jArr.add(jObj);
                        }
                    } else {
                        String[] whereLimitsFieldNames = new String[0];
                        Object[] whereLimitsFieldValues = new Object[0];

                        if (whereLimitsFieldNames == null || whereLimitsFieldNames.length == 0) {
                            whereLimitsFieldNames = new String[]{TblsCnfg.SpecLimits.LIMIT_ID.getName() + WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()};
                        }
                        String[] fieldToRetrieveLimitsArr = new String[]{TblsCnfg.SpecLimits.CODE.getName(), TblsCnfg.SpecLimits.VARIATION_NAME.getName(), TblsCnfg.SpecLimits.ANALYSIS.getName(), TblsCnfg.SpecLimits.METHOD_NAME.getName(), TblsCnfg.SpecLimits.PARAMETER.getName(), TblsCnfg.SpecLimits.RULE_TYPE.getName(),
                            TblsCnfg.SpecLimits.RULE_VARIABLES.getName(), TblsCnfg.SpecLimits.UOM.getName()};
                        String[] limitsFieldNamesToFilter = new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.SPEC_CODE.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.SPEC_VARIATION_NAME.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.ANALYSIS.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.METHOD_NAME.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.PARAMETER.getName()};
                        String[] fieldToRetrieveGroupedArr = new String[0];
                        if ((prodLotfieldsToRetrieveStr == null) || ("ALL".equalsIgnoreCase(prodLotfieldsToRetrieveStr))) {
                            fieldToRetrieveGroupedArr = EnumIntViewFields.getAllFieldNames(TblsData.ViewSampleAnalysisResultWithSpecLimits.values());
                        } else {
                            fieldToRetrieveGroupedArr = prodLotfieldsToRetrieveStr.split("\\|");
                        }
                        Object[][] specLimits = QueryUtilitiesEnums.getTableData(TblsCnfg.TablesConfig.SPEC_LIMITS,
                                EnumIntTableFields.getTableFieldsFromString(TblsCnfg.TablesConfig.SPEC_LIMITS, fieldToRetrieveLimitsArr),
                                whereLimitsFieldNames, whereLimitsFieldValues, new String[]{TblsCnfg.SpecLimits.LIMIT_ID.getName()});
                        jArr = new JSONArray();
                        for (Object[] currLimit : specLimits) {
                            numTotalRecords = 50;
                            whereFieldNames = new String[0];
                            whereFieldValues = new Object[0];
                            programName = argValues[3].toString();
                            if (programName != null) {
                                whereFieldNames = LPArray.addValueToArray1D(whereFieldNames, TblsData.ViewSampleAnalysisResultWithSpecLimits.PROGRAM_NAME.getName());
                                whereFieldValues = LPArray.addValueToArray1D(whereFieldValues, programName);
                            }
                            jObj = LPJson.convertArrayRowToJSONObject(fieldToRetrieveLimitsArr, currLimit);
                            for (int i = 0; i < limitsFieldNamesToFilter.length; i++) {
                                whereFieldNames = LPArray.addValueToArray1D(whereFieldNames, limitsFieldNamesToFilter[i]);
                                whereFieldValues = LPArray.addValueToArray1D(whereFieldValues, currLimit[i]);
                            }
                            whereFieldNames = LPArray.addValueToArray1D(whereFieldNames, TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE.getName() + WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause());
                            whereFieldValues = LPArray.addValueToArray1D(whereFieldValues, "");
                            Object[][] programLastResults = QueryUtilitiesEnums.getViewData(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW,
                                    EnumIntViewFields.getViewFieldsFromString(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, prodLotfieldToRetrieveArr),
                                    new SqlWhere(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, whereFieldNames, whereFieldValues),
                                    new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.ENTERED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                            JSONArray jArrSampleResults = new JSONArray();
                            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(programLastResults[0][0].toString()))) {
                                if (numTotalRecords > programLastResults.length) {
                                    numTotalRecords = programLastResults.length;
                                }
                                for (int i = 0; i < numTotalRecords; i++) {
                                    JSONObject jResultsObj = LPJson.convertArrayRowToJSONObject(fieldToRetrieveGroupedArr, programLastResults[i]);
                                    jArrSampleResults.add(jResultsObj);
                                }
                            }
                            jObj.put(JSON_TAG_NAME_SAMPLE_RESULTS, jArrSampleResults);
                            jArr.add(jObj);
                        }

                    }
                    this.isSuccess = true;
                    this.responseSuccessJArr = jArr;
                    return;
                case GET_PENDING_INCUBATION_SAMPLES_AND_ACTIVE_BATCHES:
                    fieldsToRetrieve = new String[]{};
                    String fieldsRetrieveStr = argValues[2].toString();
                    if (fieldsRetrieveStr.length() == 0 || "ALL".equalsIgnoreCase(fieldsRetrieveStr)) {
                        fieldsToRetrieve = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableFields());
                    } else {
                        fieldsToRetrieve = fieldsRetrieveStr.split("\\|");
                    }
                    whereFieldsNameArr = null;
                    whereFieldsValueArr = null;
                    whereFieldsName = argValues[3].toString();
                    if (whereFieldsName == null) {
                        whereFieldsName = "";
                    }
                    whereFieldsValue = argValues[4].toString();
                    if (whereFieldsValue == null) {
                        whereFieldsValue = "";
                    }

                    if (whereFieldsName.length() > 0) {
                        whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
                    } else {
                        whereFieldsNameArr = new String[]{TblsEnvMonitData.IncubBatch.ACTIVE.getName()};
                    }
                    if (whereFieldsValue.length() > 0) {
                        whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|")));
                    } else {
                        whereFieldsValueArr = new Object[]{true};
                    }
                    for (int iFields = 0; iFields < whereFieldsNameArr.length; iFields++) {
                        if (Boolean.TRUE.equals(LPPlatform.isEncryptedField(procInstanceName, GlobalVariables.Schemas.DATA.getName(), TblsData.TablesData.SAMPLE.getTableName(), whereFieldsNameArr[iFields]))) {
                            Map<String, String> hm = LPPlatform.encryptEncryptableFieldsAddBoth(whereFieldsNameArr[iFields], whereFieldsNameArr[iFields]);
                            whereFieldsNameArr[iFields] = hm.keySet().iterator().next();
                            if (hm.get(whereFieldsNameArr[iFields]).length() != whereFieldsNameArr[iFields].length()) {
                                String newWhereFieldValues = hm.get(whereFieldsNameArr[iFields]);
                                whereFieldsValueArr[iFields] = newWhereFieldValues;
                            }
                        }
                        procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, null);
                        String[] tokenFieldValue = Token.getTokenFieldValue(whereFieldsValueArr[iFields].toString(), procReqInstance.getTokenString());
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(tokenFieldValue[0])) {
                            whereFieldsValueArr[iFields] = tokenFieldValue[1];
                        }
                    }
                    if (Boolean.FALSE.equals(LPArray.valueInArray(whereFieldsNameArr, TblsEnvMonitData.IncubBatch.ACTIVE.getName()))) {
                        whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, TblsEnvMonitData.IncubBatch.ACTIVE.getName());
                        whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, true);
                    }
                    jArr = getActiveBatchData(fieldsToRetrieve, whereFieldsNameArr, whereFieldsValueArr);
                    jObj = new JSONObject();
                    jObj.put("active_batches", jArr);

                    sampleFieldToRetrieve = argValues[5].toString();
                    if (sampleFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleFieldToRetrieve)) {
                        sampleFieldToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableFields());
                    } else {
                        sampleFieldToRetrieveArr = sampleFieldToRetrieve.split("\\|");
                    }
                    if (Boolean.FALSE.equals(LPArray.valueInArray(sampleFieldToRetrieveArr, TblsData.Sample.SAMPLE_ID.getName()))) {
                        sampleFieldToRetrieveArr = LPArray.addValueToArray1D(sampleFieldToRetrieveArr, TblsData.Sample.SAMPLE_ID.getName());
                    }
                    jArr = new JSONArray();
                    Object[][] smpArr = QueryUtilitiesEnums.getTableData(TblsData.TablesData.SAMPLE,
                            EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE, sampleFieldToRetrieveArr),
                            new String[]{TblsData.Sample.CURRENT_STAGE.getName(), TblsData.Sample.INCUBATION_PASSED.getName(), TblsData.Sample.INCUBATION2_PASSED.getName()},
                            new Object[]{"Incubation", true, true}, null);
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(smpArr[0][0].toString()))) {
                        for (Object[] curSmp : smpArr) {
                            JSONObject curRecordJObj = LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, curSmp);
                            jArr.add(curRecordJObj);
                        }
                    }
                    jObj.put("samples_stillIncubationStageAndBothIncubCompleted", jArr);

                    String includSplittedByIncubNumber = LPNulls.replaceNull(argValues[0]).toString();
                    if (Boolean.TRUE.equals(Boolean.valueOf(includSplittedByIncubNumber))) {
                        int j = 5;
                        for (int i = 1; i < 3; i++) {
                            whereFieldsName = argValues[j].toString();
                            j++;
                            whereFieldsValue = argValues[j].toString();
                            j++;
                            sampleFieldToRetrieve = argValues[j].toString();
                            j++;
                            if (sampleFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleFieldToRetrieve)) {
                                sampleFieldToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableFields());
                            } else {
                                sampleFieldToRetrieveArr = sampleFieldToRetrieve.split("\\|");
                            }
                            String sampleAnalysisFieldToRetrieve = argValues[j].toString();
                            j++;
                            String sampleLastLevel = argValues[j].toString();
                            j++;
                            String addSampleAnalysis = argValues[j].toString();
                            j++;
                            sampleAnalysisFieldToRetrieve = argValues[j].toString();
                            j++;
                            String[] sampleAnalysisFieldToRetrieveArr = null;
                            if (sampleAnalysisFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleAnalysisFieldToRetrieve)) {
                                sampleAnalysisFieldToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableFields());
                            } else {
                                sampleAnalysisFieldToRetrieveArr = sampleAnalysisFieldToRetrieve.split("\\|");
                            }
                            sampleAnalysisWhereFieldsName = argValues[j].toString();
                            j++;
                            sampleAnalysisWhereFieldsValue = argValues[j].toString();
                            j++;
                            String addSampleAnalysisResult = argValues[j].toString();
                            j++;
                            String sampleAnalysisResultFieldToRetrieve = argValues[j].toString();
                            j++;
                            String[] sampleAnalysisResultFieldToRetrieveArr = null;
                            if (sampleAnalysisResultFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleAnalysisResultFieldToRetrieve)) {
                                sampleAnalysisResultFieldToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableFields());
                            } else {
                                sampleAnalysisResultFieldToRetrieveArr = sampleAnalysisResultFieldToRetrieve.split("\\|");
                            }
                            String sampleAnalysisResultWhereFieldsName = argValues[j].toString();
                            j++;
                            String sampleAnalysisResultWhereFieldsValue = argValues[j].toString();
                            j++;
                            sortFieldsName = argValues[j].toString();
                            j++;
                            JSONArray samplesArray = samplesByStageData(sampleLastLevel, sampleFieldToRetrieveArr, whereFieldsName,
                                    whereFieldsValue, sortFieldsName,
                                    addSampleAnalysis, sampleAnalysisFieldToRetrieveArr, sampleAnalysisWhereFieldsName, sampleAnalysisWhereFieldsValue,
                                    addSampleAnalysisResult, sampleAnalysisResultFieldToRetrieveArr, sampleAnalysisResultWhereFieldsName, sampleAnalysisResultWhereFieldsValue, false);
                            jObj.put("incub_" + i, samplesArray);
                        }
                    }
                    String includeAllWithAnyPendingIncubation = LPNulls.replaceNull(argValues[1]).toString();
                    if (Boolean.TRUE.equals(Boolean.valueOf(includeAllWithAnyPendingIncubation))) {
                        int j = 33;
                        whereFieldsName = argValues[j].toString();
                        j++;
                        whereFieldsValue = argValues[j].toString();
                        j++;
                        sampleFieldToRetrieve = argValues[j].toString();
                        j++;
                        if (sampleFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleFieldToRetrieve)) {
                            sampleFieldToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableFields());
                        } else {
                            sampleFieldToRetrieveArr = sampleFieldToRetrieve.split("\\|");
                        }
                        String sampleAnalysisFieldToRetrieve = argValues[j].toString();
                        j++;
                        sampleAnalysisFieldToRetrieve = argValues[j].toString();
                        j++;
                        String[] sampleAnalysisFieldToRetrieveArr = null;
                        if (sampleAnalysisFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleAnalysisFieldToRetrieve)) {
                            sampleAnalysisFieldToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableFields());
                        } else {
                            sampleAnalysisFieldToRetrieveArr = sampleAnalysisFieldToRetrieve.split("\\|");
                        }
                        sampleAnalysisWhereFieldsName = argValues[j].toString();
                        j++;
                        sampleAnalysisWhereFieldsValue = argValues[j].toString();
                        j++;
                        sortFieldsName = argValues[j].toString();
                        j++;
                        jArr = new JSONArray();
                        smpArr = QueryUtilitiesEnums.getTableData(TblsEnvMonitData.TablesEnvMonitData.SAMPLE,
                                EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.SAMPLE, sampleFieldToRetrieveArr),
                                new String[]{TblsEnvMonitData.Sample.CURRENT_STAGE.getName(), TblsEnvMonitData.Sample.INCUBATION2_PASSED.getName(),
                                    SqlStatement.WHERECLAUSE_TYPES.OR.getSqlClause() + " " + TblsEnvMonitData.Sample.INCUBATION2_PASSED.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
                                new Object[]{"Incubation", false, ""}, null);
                        for (Object[] curSmp : smpArr) {
                            JSONObject incubRow = LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, curSmp);
                            Integer incub1Passed = LPArray.valuePosicInArray(sampleFieldToRetrieveArr, TblsEnvMonitData.Sample.INCUBATION_PASSED.getName());
                            if (incub1Passed > -1) {
                                String currPendingIncubBatch = "";
                                String incub1PassedStr = LPNulls.replaceNull(curSmp[incub1Passed]).toString();
                                if (Boolean.TRUE.equals(Boolean.valueOf(incub1PassedStr))) {
                                    incubRow.put("pending_incub", 2);
                                } else {
                                    incubRow.put("pending_incub", 1);
                                }
                                currPendingIncubBatch = curSmp[LPArray.valuePosicInArray(sampleFieldToRetrieveArr, TblsData.Sample.INCUBATION2_BATCH.getName())].toString();
                                incubRow.put("current_pending_incub_batch", currPendingIncubBatch);
                            }
                            jArr.add(incubRow);
                        }
                        jObj.put("samplesWithAnyPendingIncubation", jArr);
                    }

                    this.isSuccess = true;
                    this.responseSuccessJObj = jObj;
                    return;
                case SAMPLES_BY_STAGE:
                case SAMPLES_INPROGRESS_LIST:
                    whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME);
                    if (whereFieldsName == null) {
                        whereFieldsName = "";
                    }
                    whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE);

                    sampleFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE);
                    if (sampleFieldToRetrieve == null || sampleFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleFieldToRetrieve)) {
                        sampleFieldToRetrieveArr = getAllFieldNames(TblsData.TablesData.SAMPLE.getTableFields());
                    } else {
                        sampleFieldToRetrieveArr = sampleFieldToRetrieve.split("\\|");
                    }

                    String sampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE);
                    String sampleLastLevel = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL);

                    String addSampleAnalysis = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS);
                    if (addSampleAnalysis == null) {
                        addSampleAnalysis = Boolean.FALSE.toString().toLowerCase();
                    }
                    String sampleAnalysisFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE);
                    String[] sampleAnalysisFieldToRetrieveArr = null;
                    if (sampleAnalysisFieldToRetrieve == null || sampleAnalysisFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleAnalysisFieldToRetrieve)) {
                        sampleAnalysisFieldToRetrieveArr = getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS.getTableFields());
                    } else {
                        sampleAnalysisFieldToRetrieveArr = sampleAnalysisFieldToRetrieve.split("\\|");
                    }
                    sampleAnalysisWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME);
                    sampleAnalysisWhereFieldsNameArr = new String[0];
                    if ((sampleAnalysisWhereFieldsName != null) && (sampleAnalysisWhereFieldsName.length() > 0)) {
                        sampleAnalysisWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                    }
                    sampleAnalysisWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE);

                    String addSampleAnalysisResult = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT);
                    if (addSampleAnalysisResult == null) {
                        addSampleAnalysisResult = Boolean.FALSE.toString().toLowerCase();
                    }
                    sampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE);
                    String[] sampleAnalysisResultFieldToRetrieveArr = null;
                    if (sampleAnalysisResultFieldToRetrieve == null || sampleAnalysisResultFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleAnalysisResultFieldToRetrieve)) {
                        sampleAnalysisResultFieldToRetrieveArr = getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableFields());
                    } else {
                        sampleAnalysisResultFieldToRetrieveArr = sampleAnalysisResultFieldToRetrieve.split("\\|");
                    }
                    String sampleAnalysisResultWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME);
                    String[] sampleAnalysisResultWhereFieldsNameArr = new String[0];
                    if ((sampleAnalysisResultWhereFieldsName != null) && (sampleAnalysisResultWhereFieldsName.length() > 0)) {
                        sampleAnalysisResultWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisResultWhereFieldsNameArr, sampleAnalysisResultWhereFieldsName.split("\\|"));
                    }
                    String sampleAnalysisResultWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE);
                    Boolean includeOnlyWhenResultsInProgress = Boolean.valueOf(LPNulls.replaceNull(argValues[14]).toString());

                    sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME);
                    JSONArray samplesArray = samplesByStageData(sampleLastLevel, sampleFieldToRetrieveArr, whereFieldsName,
                            whereFieldsValue, sortFieldsName,
                            addSampleAnalysis, sampleAnalysisFieldToRetrieveArr, sampleAnalysisWhereFieldsName, sampleAnalysisWhereFieldsValue,
                            addSampleAnalysisResult,
                            sampleAnalysisResultFieldToRetrieveArr, sampleAnalysisResultWhereFieldsName, sampleAnalysisResultWhereFieldsValue,
                            includeOnlyWhenResultsInProgress);
                    this.isSuccess = true;
                    this.responseSuccessJArr = samplesArray;
                    return;

                default:
            }
            this.diagnostic = actionDiagnoses;
            this.relatedObj = rObj;
            rObj.killInstance();
        } catch (NumberFormatException e) {
            this.diagnostic = new Object[]{e.getMessage()};
        }
    }

    public static JSONArray samplesByStageData(String sampleLastLevel, String[] sampleFieldToRetrieveArr, String whereFieldsName, String whereFieldsValue, String sortFieldsName,
            String addSampleAnalysis, String[] sampleAnalysisFieldToRetrieveArr, String sampleAnalysisWhereFieldsName, String sampleAnalysisWhereFieldsValue,
            String addSampleAnalysisResult, String[] sampleAnalysisResultFieldToRetrieveArr, String sampleAnalysisResultWhereFieldsName, String sampleAnalysisResultWhereFieldsValue, Boolean includeOnlyWhenResultsInProgress) {
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        String procInstanceName = procReqInstance.getProcedureInstance();
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());

        if (sampleLastLevel == null) {
            sampleLastLevel = TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName();
        }
        if (sampleFieldToRetrieveArr == null || sampleFieldToRetrieveArr[0].length() == 0) {
            sampleFieldToRetrieveArr = new String[]{TblsEnvMonitData.Sample.SAMPLE_ID.getName()};
        }

        if (sampleAnalysisFieldToRetrieveArr == null || sampleAnalysisFieldToRetrieveArr[0].length() == 0) {
            sampleAnalysisFieldToRetrieveArr = new String[]{TblsData.SampleAnalysis.TEST_ID.getName()};
        }

        if (sampleAnalysisFieldToRetrieveArr == null || sampleAnalysisFieldToRetrieveArr[0].length() == 0) {
            sampleAnalysisFieldToRetrieveArr = new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()};
        }

        String[] whereFieldsNameArr = null;
        Object[] whereFieldsValueArr = null;

        if ((whereFieldsName != null) && (whereFieldsValue != null) && whereFieldsName.length() > 0) {
            whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
            whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|")));
            for (int iFields = 0; iFields < whereFieldsNameArr.length; iFields++) {
                if (Boolean.TRUE.equals(LPPlatform.isEncryptedField(procInstanceName, GlobalVariables.Schemas.DATA.getName(), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), whereFieldsNameArr[iFields]))) {
                    Map<String, String> hm = LPPlatform.encryptEncryptableFieldsAddBoth(whereFieldsNameArr[iFields], whereFieldsValueArr[iFields].toString());
                    whereFieldsNameArr[iFields] = hm.keySet().iterator().next();
                    if (hm.get(whereFieldsNameArr[iFields]).length() != whereFieldsNameArr[iFields].length()) {
                        String newWhereFieldValues = hm.get(whereFieldsNameArr[iFields]);
                        whereFieldsValueArr[iFields] = newWhereFieldValues;
                    }
                }
                String[] tokenFieldValue = Token.getTokenFieldValue(whereFieldsValueArr[iFields].toString(), procReqInstance.getTokenString());
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(tokenFieldValue[0])) {
                    whereFieldsValueArr[iFields] = tokenFieldValue[1];
                }
            }
        }
        String[] sortFieldsNameArr = null;
        if (!((sortFieldsName == null || sortFieldsName.length() == 0) || (sortFieldsName.contains(GlobalAPIsParams.REQUEST_PARAM_VALUE_UNDEFINED)))) {
            sortFieldsNameArr = sortFieldsName.split("\\|");
        } else {
            sortFieldsNameArr = null;
        }
        if (whereFieldsValueArr != null) {
            for (int iFldV = 0; iFldV < whereFieldsValueArr.length; iFldV++) {
                if (whereFieldsValueArr[iFldV].toString().equalsIgnoreCase(Boolean.FALSE.toString().toLowerCase())) {
                    whereFieldsValueArr[iFldV] = Boolean.valueOf(whereFieldsValueArr[iFldV].toString());
                }
                if (whereFieldsValueArr[iFldV].toString().equalsIgnoreCase("true")) {
                    whereFieldsValueArr[iFldV] = Boolean.valueOf(whereFieldsValueArr[iFldV].toString());
                }
            }
        }
        if (TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName().equals(sampleLastLevel)) {
            Object[][] mySamples = QueryUtilitiesEnums.getTableData(TblsEnvMonitData.TablesEnvMonitData.SAMPLE,
                    EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.SAMPLE, sampleFieldToRetrieveArr),
                    whereFieldsNameArr, whereFieldsValueArr, sortFieldsNameArr);
            if (mySamples == null) {
                return new JSONArray();
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(mySamples[0][0].toString())) {
                return new JSONArray();
            } else {
                JSONArray mySamplesJSArr = new JSONArray();
                for (Object[] mySample : mySamples) {
                    JSONObject mySampleJSObj = LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, mySample);
                    if (Boolean.TRUE.equals(includeOnlyWhenResultsInProgress) && Boolean.FALSE.equals(isThereResultsInProgress(sampleFieldToRetrieveArr, mySample))) {
                        continue;
                    }

                    if ("TRUE".equalsIgnoreCase(addSampleAnalysis)) {
                        String[] testWhereFieldsNameArr = new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName()};
                        testWhereFieldsNameArr = LPArray.addValueToArray1D(testWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                        Integer sampleIdPosicInArray = LPArray.valuePosicInArray(sampleFieldToRetrieveArr, TblsData.SampleAnalysis.SAMPLE_ID.getName());
                        Object[] testWhereFieldsValueArr = new Object[]{Integer.parseInt(mySample[sampleIdPosicInArray].toString())};
                        testWhereFieldsValueArr = LPArray.addValueToArray1D(testWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")));
                        if ("TRUE".equalsIgnoreCase(addSampleAnalysisResult)) {
                            sampleAnalysisFieldToRetrieveArr = LPArray.addValueToArray1D(sampleAnalysisFieldToRetrieveArr, TblsData.SampleAnalysis.TEST_ID.getName());
                        }
                        Object[][] mySampleAnalysis = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                                testWhereFieldsNameArr, testWhereFieldsValueArr, sampleAnalysisFieldToRetrieveArr);
                        JSONArray mySamplesAnaJSArr = new JSONArray();
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(mySampleAnalysis[0][0].toString())) {
                            mySampleJSObj.put(TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), mySamplesAnaJSArr);
                        } else {
                            for (Object[] mySampleAnalysi : mySampleAnalysis) {
                                JSONObject mySampleAnaJSObj = LPJson.convertArrayRowToJSONObject(sampleAnalysisFieldToRetrieveArr, mySampleAnalysi);
                                if ("TRUE".equalsIgnoreCase(addSampleAnalysisResult)) {
                                    String[] sarWhereFieldsNameArr = new String[]{TblsData.SampleAnalysis.TEST_ID.getName()};
                                    if (sampleAnalysisResultWhereFieldsName != null) {
                                        sarWhereFieldsNameArr = LPArray.addValueToArray1D(sarWhereFieldsNameArr, sampleAnalysisResultWhereFieldsName);
                                    }
                                    Integer testIdPosicInArray = LPArray.valuePosicInArray(sampleAnalysisFieldToRetrieveArr, TblsData.SampleAnalysis.TEST_ID.getName());
                                    Object[] sarWhereFieldsValueArr = new Object[]{Integer.parseInt(mySampleAnalysi[testIdPosicInArray].toString())};
                                    if (sampleAnalysisResultWhereFieldsValue != null) {
                                        sarWhereFieldsValueArr = LPArray.addValueToArray1D(sarWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(LPNulls.replaceNull(sampleAnalysisResultWhereFieldsValue).split("\\|")));
                                    }

                                    Object[][] mySampleAnalysisResults = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                                            sarWhereFieldsNameArr, sarWhereFieldsValueArr, sampleAnalysisResultFieldToRetrieveArr);
                                    JSONArray mySamplesAnaResJSArr = new JSONArray();
                                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(mySampleAnalysisResults[0][0].toString())) {
                                        mySampleAnaJSObj.put(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), mySamplesAnaResJSArr);
                                    }
                                    JSONObject mySampleAnaResJSObj = new JSONObject();
                                    for (Object[] mySampleAnalysisResult : mySampleAnalysisResults) {
                                        mySampleAnaResJSObj = LPJson.convertArrayRowToJSONObject(sampleAnalysisResultFieldToRetrieveArr, mySampleAnalysisResult);
                                        mySamplesAnaResJSArr.add(mySampleAnaResJSObj);
                                    }
                                    mySampleAnaJSObj.put(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), mySamplesAnaResJSArr);
                                }
                                mySamplesAnaJSArr.add(mySampleAnaJSObj);
                            }
                            mySampleJSObj.put(TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), mySamplesAnaJSArr);
                        }
                    }
                    mySamplesJSArr.add(mySampleJSObj);
                }
                return mySamplesJSArr;
            }
        } else {
            whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, "sample_id is not null");
            whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, "");
            JSONArray samplesArray = new JSONArray();
            JSONArray sampleArray = new JSONArray();
            Object[][] mySamples = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(),
                    whereFieldsNameArr, whereFieldsValueArr, sampleFieldToRetrieveArr);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(mySamples[0][0].toString())) {
                return new JSONArray();
            }
            for (Object[] currSample : mySamples) {
                Integer sampleId = Integer.valueOf(currSample[0].toString());
                JSONObject sampleObj = LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, currSample);
                if (("TEST".equals(sampleLastLevel)) || ("RESULT".equals(sampleLastLevel))) {
                    String[] testWhereFieldsNameArr = new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName()};
                    Object[] testWhereFieldsValueArr = new Object[]{sampleId};
                    Object[][] mySampleAnalysis = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                            testWhereFieldsNameArr, testWhereFieldsValueArr, sampleAnalysisFieldToRetrieveArr);
                    for (Object[] mySampleAnalysi : mySampleAnalysis) {
                        JSONObject testObj = new JSONObject();
                        for (int ySmpAna = 0; ySmpAna < mySampleAnalysis[0].length; ySmpAna++) {
                            if (mySampleAnalysi[ySmpAna] instanceof Timestamp) {
                                testObj.put(sampleAnalysisFieldToRetrieveArr[ySmpAna], mySampleAnalysi[ySmpAna].toString());
                            } else {
                                testObj.put(sampleAnalysisFieldToRetrieveArr[ySmpAna], mySampleAnalysi[ySmpAna]);
                            }
                        }
                        sampleArray.add(testObj);
                    }
                    sampleObj.put(TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), sampleArray);
                }
                sampleArray.add(sampleObj);
            }
            samplesArray.add(sampleArray);
            return samplesArray;
        }

    }

    private JSONArray sampleStageDataJsonArr(Integer sampleId, String[] sampleFldName, Object[] sampleFldValue, String[] sampleStageFldName, Object[] sampleStageFldValue) {
        if (sampleStageFldValue == null) {
            return new JSONArray();
        }
        if (Boolean.FALSE.equals(LPArray.valueInArray(sampleStageFldName, TblsProcedure.SampleStageTimingCapture.STAGE_CURRENT.getName()))) {
            return new JSONArray();
        }
        String currentStage = sampleStageFldValue[LPArray.valuePosicInArray(sampleStageFldName, TblsProcedure.SampleStageTimingCapture.STAGE_CURRENT.getName())].toString();
        JSONObject jObj = new JSONObject();
        JSONArray jArrMainObj = new JSONArray();
        JSONArray jArrMainObj2 = new JSONArray();
        switch (currentStage.toUpperCase()) {
            case "SAMPLING":
                jObj.put(GlobalAPIsParams.LBL_FIELD_NAME, TblsEnvMonitData.Sample.SAMPLING_DATE.getName());
                jObj.put(GlobalAPIsParams.LBL_FIELD_VALUE, sampleFldValue[LPArray.valuePosicInArray(sampleFldName, TblsEnvMonitData.Sample.SAMPLING_DATE.getName())].toString());
                jArrMainObj.add(jObj);
                return jArrMainObj;
            case "INCUBATION":
                String[] incub1Flds = new String[]{TblsEnvMonitData.Sample.INCUBATION_PASSED.getName(), TblsEnvMonitData.Sample.INCUBATION_INCUBATOR.getName(), TblsEnvMonitData.Sample.INCUBATION_BATCH.getName(),
                    TblsEnvMonitData.Sample.INCUBATION_START.getName(), TblsEnvMonitData.Sample.INCUBATION_START_TEMP_EVENT_ID.getName(), TblsEnvMonitData.Sample.INCUBATION_START_TEMPERATURE.getName(),
                    TblsEnvMonitData.Sample.INCUBATION_END.getName(), TblsEnvMonitData.Sample.INCUBATION_END_TEMP_EVENT_ID.getName(), TblsEnvMonitData.Sample.INCUBATION_END_TEMPERATURE.getName()};
                for (String curFld : incub1Flds) {
                    Integer fldPosic = LPArray.valuePosicInArray(sampleFldName, curFld);
                    if (fldPosic > -1) {
                        jObj = new JSONObject();
                        JSONObject jObjSampleStageInfo = new JSONObject();
                        jObjSampleStageInfo.put(GlobalAPIsParams.LBL_FIELD_NAME, curFld);
                        jObjSampleStageInfo.put(GlobalAPIsParams.LBL_FIELD_VALUE, sampleFldValue[fldPosic].toString());
                        jArrMainObj.add(jObjSampleStageInfo);
                    }
                    curFld = curFld.replace("incubation", "incubation2");
                    fldPosic = LPArray.valuePosicInArray(sampleFldName, curFld);
                    if (fldPosic > -1) {
                        jObj = new JSONObject();
                        JSONObject jObjSampleStageInfo = new JSONObject();
                        jObjSampleStageInfo.put(GlobalAPIsParams.LBL_FIELD_NAME, curFld);
                        jObjSampleStageInfo.put(GlobalAPIsParams.LBL_FIELD_VALUE, sampleFldValue[fldPosic].toString());
                        jArrMainObj2.add(jObjSampleStageInfo);
                    }
                }
                JSONObject jObj2 = new JSONObject();
                jObj2.put("incubation_1", jArrMainObj);
                jObj2.put("incubation_2", jArrMainObj2);
                jArrMainObj = new JSONArray();
                jArrMainObj.add(jObj2);
                return jArrMainObj;
            case "PLATEREADING":
            case "MICROORGANISMIDENTIFICATION":
                EnumIntViewFields[] tblAllFldsObj = EnumIntViewFields.getViewFieldsFromString(TblsEnvMonitData.ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW, "ALL");
                String[] tblAllFlds = EnumIntViewFields.getAllFieldNames(tblAllFldsObj);
                Object[][] sampleStageInfo = QueryUtilitiesEnums.getViewData(TblsEnvMonitData.ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW,
                        tblAllFldsObj,
                        new SqlWhere(TblsEnvMonitData.ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW, new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.SAMPLE_ID.getName()}, new Object[]{sampleId}),
                        new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.TEST_ID.getName(), TblsEnvMonitData.ViewSampleMicroorganismList.RESULT_ID.getName()});
                jObj = new JSONObject();
                for (int iFlds = 0; iFlds < sampleStageInfo[0].length; iFlds++) {
                    JSONObject jObjSampleStageInfo = new JSONObject();
                    jObjSampleStageInfo.put(GlobalAPIsParams.LBL_FIELD_NAME, tblAllFlds[iFlds]);
                    jObjSampleStageInfo.put(GlobalAPIsParams.LBL_FIELD_VALUE, sampleStageInfo[0][iFlds].toString());
                    jArrMainObj.add(jObjSampleStageInfo);
                }
                jArrMainObj.add(jObj);
                return jArrMainObj;
            default:
                return jArrMainObj;
        }
    }

    static Object[] sampleAnalysisResultLockData(String procInstanceName, String[] resultFieldToRetrieveArr, Object[] curRow) {
        try {
            String[] fldNameArr = null;
            Object[] fldValueArr = null;

            Object[] lockedByStatus = isLockedByStatus(resultFieldToRetrieveArr, curRow);
            if (lockedByStatus[0] != null) {
                return lockedByStatus;
            }

            Object[] lockedByCorrectiveAction = isLockedByCorrectiveAction(procInstanceName, resultFieldToRetrieveArr, curRow);
            if (lockedByCorrectiveAction[0] != null) {
                return lockedByCorrectiveAction;
            }

            Object[] isLockedByUserCertification = isLockedByUserCertification(resultFieldToRetrieveArr, curRow);
            if (isLockedByUserCertification[0] != null) {
                return isLockedByUserCertification;
            }

            return new Object[]{fldNameArr, fldValueArr};
        } catch (NumberFormatException e) {
            return new Object[]{new String[]{"ERROR"}, e.getMessage()};
        }
    }

    static Object[] isLockedByCorrectiveAction(String procInstanceName, String[] resultFieldToRetrieveArr, Object[] curRow) {
        String[] fldNameArr = null;
        Object[] fldValueArr = null;
        try {
            Integer resultFldPosic = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.SampleAnalysisResult.RESULT_ID.getName());
            Integer resultId = Integer.valueOf(curRow[resultFldPosic].toString());
            if (Boolean.FALSE.equals(isProgramCorrectiveActionEnable(procInstanceName))) {
                return new Object[]{null, null};
            }
            Object[][] notClosedProgramCorrreciveAction = QueryUtilitiesEnums.getTableData(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION,
                    EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION, SAMPLEANALYSISRESULTLOCKDATA_RETRIEVEDATA_PROGRAMCORRECTIVEACTION),
                    new String[]{TblsProcedure.ProgramCorrectiveAction.RESULT_ID.getName(), TblsProcedure.ProgramCorrectiveAction.STATUS.getName() + "<>"},
                    new Object[]{resultId, DataProgramCorrectiveAction.ProgramCorrectiveStatus.CLOSED.toString()}, null);
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(notClosedProgramCorrreciveAction[0][0].toString()))) {
                String notifMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataProgramCorrectiveAction.DataProgramCorrectiveActionBusinessRules.STILLOPEN_NOTIFMODE.getAreaName(), DataProgramCorrectiveAction.DataProgramCorrectiveActionBusinessRules.STILLOPEN_NOTIFMODE.getTagName());
                switch (notifMode.toLowerCase()) {
                    case "silent":
                        return new Object[]{fldNameArr, fldValueArr};
                    case "warning":
                        fldNameArr = LPArray.addValueToArray1D(fldNameArr, "has_warning");
                        fldValueArr = LPArray.addValueToArray1D(fldValueArr, true);
                        fldNameArr = LPArray.addValueToArray1D(fldNameArr, "warning_object");
                        fldValueArr = LPArray.addValueToArray1D(fldValueArr, TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName());
                        String msgCode = "resultLockedByProgramCorrectiveActionInProgress";
                        fldValueArr = LPArray.addValueToArray1D(fldValueArr, msgCode);
                        String errorTextEn = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_WARNING_REASONS, null, msgCode, DEFAULTLANGUAGE, null, true, null);
                        String errorTextEs = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_WARNING_REASONS, null, msgCode, "es", null, false, null);
                        JSONObject reasonInfo = new JSONObject();
                        reasonInfo.put(GlobalAPIsParams.LBL_MESSAGE_EN, errorTextEn);
                        reasonInfo.put(GlobalAPIsParams.LBL_MESSAGE_ES, errorTextEs);
                        return new Object[]{fldNameArr, fldValueArr, "warning_reason", reasonInfo};
                    case "locking":
                    default:
                        fldNameArr = LPArray.addValueToArray1D(fldNameArr, IS_LOCKED);
                        fldValueArr = LPArray.addValueToArray1D(fldValueArr, true);
                        fldNameArr = LPArray.addValueToArray1D(fldNameArr, LOCKING_OBJECT);
                        fldValueArr = LPArray.addValueToArray1D(fldValueArr, TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName());
                        msgCode = "resultLockedByProgramCorrectiveActionInProgress";
                        fldValueArr = LPArray.addValueToArray1D(fldValueArr, msgCode);
                        errorTextEn = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_LOCKING_REASONS, null, msgCode, DEFAULTLANGUAGE, null, true, null);
                        errorTextEs = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_LOCKING_REASONS, null, msgCode, "es", null, false, null);
                        reasonInfo = new JSONObject();
                        reasonInfo.put(GlobalAPIsParams.LBL_MESSAGE_EN, errorTextEn);
                        reasonInfo.put(GlobalAPIsParams.LBL_MESSAGE_ES, errorTextEs);
                        return new Object[]{fldNameArr, fldValueArr, LOCKING_REASON, reasonInfo};
                }
            }
            return new Object[]{null, null};
        } catch (NumberFormatException e) {
            return new Object[]{"ERROR", e.getMessage()};
        }
    }

    static Object[] isLockedByStatus(String[] resultFieldToRetrieveArr, Object[] curRow) {
        String[] fldNameArr = null;
        Object[] fldValueArr = null;
        Integer resultFldPosic = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.SampleAnalysisResult.STATUS.getName());
        String resultStatus = curRow[resultFldPosic].toString();
        String sampleAnalysisStatusCanceled = DataSampleStructureStatuses.SampleAnalysisStatuses.CANCELED.getStatusCode("");
        String sampleAnalysisStatusReviewed = DataSampleStructureStatuses.SampleAnalysisStatuses.REVIEWED.getStatusCode("");
        if ((resultStatus.equalsIgnoreCase(sampleAnalysisStatusReviewed)) || (resultStatus.equalsIgnoreCase(sampleAnalysisStatusCanceled))) {
            fldNameArr = LPArray.addValueToArray1D(fldNameArr, IS_LOCKED);
            fldValueArr = LPArray.addValueToArray1D(fldValueArr, true);
            fldNameArr = LPArray.addValueToArray1D(fldNameArr, LOCKING_OBJECT);
            fldValueArr = LPArray.addValueToArray1D(fldValueArr, TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName());
            String msgCode = "resultLockedByStatus";
            fldValueArr = LPArray.addValueToArray1D(fldValueArr, msgCode);
            JSONObject reasonInfo = new JSONObject();
            reasonInfo.put(GlobalAPIsParams.LBL_MESSAGE_EN, resultStatus);
            reasonInfo.put(GlobalAPIsParams.LBL_MESSAGE_ES, resultStatus);
            return new Object[]{fldNameArr, fldValueArr, LOCKING_REASON, reasonInfo};
        }
        return new Object[]{null, null};
    }

    static Object[] isLockedByUserCertification(String[] resultFieldToRetrieveArr, Object[] curRow) {
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String[] fldNameArr = null;
        Object[] fldValueArr = null;
        Integer fldPosic = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.SampleAnalysisResult.METHOD_NAME.getName());
        String methodName = curRow[fldPosic].toString();
        fldPosic = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.SampleAnalysisResult.METHOD_VERSION.getName());

        Object[] ifUserCertificationEnabled = AnalysisMethodCertif.isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ifUserCertificationEnabled[0].toString())) {
            return new Object[]{null, null};
        }

        Object[] userCertified = AnalysisMethodCertif.isUserCertified(methodName, token.getUserName());
        if (Boolean.TRUE.equals(Boolean.valueOf(userCertified[0].toString()))) {
            return new Object[]{null, null};
        }

        fldNameArr = LPArray.addValueToArray1D(fldNameArr, IS_LOCKED);
        fldValueArr = LPArray.addValueToArray1D(fldValueArr, true);
        fldNameArr = LPArray.addValueToArray1D(fldNameArr, LOCKING_OBJECT);
        fldValueArr = LPArray.addValueToArray1D(fldValueArr, TblsCnfg.TablesConfig.METHODS.getTableName());
        Object[] errorMsgEn = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, AnalysisMethodCertif.CertificationAnalysisMethodErrorTrapping.USER_NOT_CERTIFIED, new Object[]{methodName}, DEFAULTLANGUAGE);
        Object[] errorMsgEs = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, AnalysisMethodCertif.CertificationAnalysisMethodErrorTrapping.USER_NOT_CERTIFIED, new Object[]{methodName}, "es");
        JSONObject reasonInfo = new JSONObject();
        reasonInfo.put(GlobalAPIsParams.LBL_MESSAGE_EN, errorMsgEn[errorMsgEs.length - 1]);
        reasonInfo.put(GlobalAPIsParams.LBL_MESSAGE_ES, errorMsgEs[errorMsgEs.length - 1]);
        return new Object[]{fldNameArr, fldValueArr, LOCKING_REASON, reasonInfo};
    }

    static Object[] getObjectsId(String[] headerFlds, Object[][] analysisResultList, String separator) {
        if (analysisResultList == null || analysisResultList.length == 0) {
            return new Object[]{};
        }
        Object[] objIds = new Object[]{};
        for (Object[] curRow : analysisResultList) {
            String curTest = TblsData.SampleAnalysisResult.TEST_ID.getName() + separator + curRow[LPArray.valuePosicInArray(headerFlds, TblsData.SampleAnalysisResult.TEST_ID.getName())].toString();
            if (Boolean.FALSE.equals(LPArray.valueInArray(objIds, curTest))) {
                objIds = LPArray.addValueToArray1D(objIds, curTest);
            }
            String curResult = TblsData.SampleAnalysisResult.RESULT_ID.getName() + separator + curRow[LPArray.valuePosicInArray(headerFlds, TblsData.SampleAnalysisResult.RESULT_ID.getName())].toString();
            if (Boolean.FALSE.equals(LPArray.valueInArray(objIds, curResult))) {
                objIds = LPArray.addValueToArray1D(objIds, curResult);
            }
        }
        return objIds;
    }

    static Object[] warningByMinOrMaxAllowed(String[] resultFieldToRetrieveArr, Object[] curResult) {
        Integer minFldPosic = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.SampleAnalysisResult.MIN_ALLOWED.getName());
        Integer maxFldPosic = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.SampleAnalysisResult.MAX_ALLOWED.getName());
        if (minFldPosic == -1 && maxFldPosic == -1) {
            return null;
        }
        String minValue = LPNulls.replaceNull(curResult[minFldPosic]).toString();
        String maxValue = LPNulls.replaceNull(curResult[maxFldPosic]).toString();
        if (minValue.length() == 0 && maxValue.length() == 0) {
            return null;
        }
        StringBuilder wEn = new StringBuilder(0);
        StringBuilder wEs = new StringBuilder(0);
        if (minValue.length() > 0 && maxValue.length() > 0) {
            wEn.append("The result is limited to the range ").append(minValue).append(" and ").append(maxValue);
            wEs.append("El resultado está limitado al rango ").append(minValue).append(" y ").append(maxValue);
        } else if (minValue.length() == 0 && maxValue.length() > 0) {
            wEn.append("The result is limited to not accept values less than ").append(minValue);
            wEs.append("El resultado está limitado para no aceptar valores menores de ").append(minValue);
        } else {
            wEn.append("The result is limited to not accept values greater than ").append(maxValue);
            wEs.append("El resultado está limitado para no aceptar valores mayores de ").append(maxValue);
        }
        JSONObject reasonInfo = new JSONObject();
        reasonInfo.put(GlobalAPIsParams.LBL_MESSAGE_EN, wEn.toString());
        reasonInfo.put(GlobalAPIsParams.LBL_MESSAGE_ES, wEs.toString());
        String[] fldNameArr = new String[]{"has_warning", "warning_object"};
        Object[] fldValueArr = new Object[]{true, TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName()};
        return new Object[]{fldNameArr, fldValueArr, "warning_reason", reasonInfo};
    }

    static JSONObject buttonActionInfo(Integer sampleId, String[] fields) {
        JSONObject jObj = new JSONObject();
        String curStage = LPNulls.replaceNull(LPArray.valuePosicInArray(fields, TblsEnvMonitData.Sample.CURRENT_STAGE.getName())).toString();
        String configCode = LPNulls.replaceNull(LPArray.valuePosicInArray(fields, TblsEnvMonitData.Sample.CONFIG_CODE.getName())).toString();
        Boolean reqSamplingEnd = Boolean.valueOf(LPNulls.replaceNull(LPArray.valuePosicInArray(fields, TblsEnvMonitData.Sample.REQS_TRACKING_SAMPLING_END.getName())).toString());
        String filterName = curStage;
        if (configCode.toUpperCase().contains("PERS")) {
            filterName = filterName + "PERS";
        } else {
            filterName = filterName + "SMP";
        }
        String viewName = "";
        switch (curStage.toUpperCase()) {
            case "SAMPLING":
                if (Boolean.TRUE.equals(reqSamplingEnd)) {
                    viewName = "SamplePendingSamplingInterval";
                } else {
                    viewName = "SamplePendingSampling";
                }
                break;
            case "INCUBATION":
                viewName = "SampleIncubation";
                break;
            case "PLATEREADING":
                viewName = "SamplePlateReading";
                break;
            case "PLATEREADINGSECONDENTRY":
                viewName = "SamplePlateSecondEntryReading";
                break;
            case "MICROORGANISMIDENTIFICATION":
                viewName = "SampleMicroorganism";
                break;
            default:
                viewName = "";
        }
        jObj.put("viewName", viewName);
        jObj.put("filterName", filterName);
        jObj.put("objectId", sampleId);
        return jObj;
    }

    public Object[] getMessageDynamicData() {
        return messageDynamicData;
    }

    /**
     * @return the relatedObj
     */
    public RelatedObjects getRelatedObj() {
        return relatedObj;
    }

    /**
     * @return the endpointExists
     */
    public Boolean getEndpointExists() {
        return endpointExists;
    }

    /**
     * @return the diagnostic
     */
    public Object[] getDiagnostic() {
        return diagnostic;
    }

    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
    }

    /**
     * @return the isSuccess
     */
    public Boolean getIsSuccess() {
        return isSuccess;
    }

    /**
     * @return the contentSuccessResponse
     */
    public Object getResponseContentJArr() {
        return responseSuccessJArr;
    }

    public Object getResponseContentJObj() {
        return responseSuccessJObj;
    }

    public Object getResponseError() {
        return responseError;
    }

    private static Boolean isThereResultsInProgress(String[] fldsName, Object[] fldsValue) {
        Integer smFldPosic = LPArray.valuePosicInArray(fldsName, TblsData.Sample.SAMPLE_ID.getName());
        if (smFldPosic == -1) {
            return false;
        }
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] groupedInfo = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                new String[]{TblsData.SampleAnalysis.STATUS.getName()},
                new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.STATUS.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.NOT_IN.getSqlClause()},
                new Object[]{fldsValue[smFldPosic],
                    SampleStatuses.REVIEWED.getStatusCode("") + "|" + SampleStatuses.CANCELED.getStatusCode("")},
                null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(groupedInfo[0][0].toString())) {
            return false;
        }
        return (groupedInfo.length > 0);
    }
}
