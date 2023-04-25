/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import lbplanet.utilities.LPFrontEnd;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Administrator
 */
public class GlobalAPIsParams extends HttpServlet {

    public static final String REQUEST_PARAM_IGNORE_ARGUMENT_WORD = "IGNORE";

    // Por favor, agregar ordenado alfabeticamente para evitar duplicados
    /**
     *
     */
    public static final String REQUEST_PARAM_DB_NAME = "dbName";
    public static final String REQUEST_PARAM_IS_TESTING = "isForTesting";
    public static final String REQUEST_PARAM_VIEW_NAME = "viewName";
    public static final String REQUEST_PARAM_ACTION_NAME = "actionName";
    public static final String REQUEST_PARAM_SIZE_VALUE = "sizeValue";
    public static final String REQUEST_PARAM_TESTING_OUTPUT_FORMAT = "outputFormat";

    /**
     *
     */
    public static final String REQUEST_PARAM_AUDIT_ID = "auditId";

    /**
     *
     */
    public static final String REQUEST_PARAM_ADD_SAMPLE_ANALYSIS = "addSampleAnalysis";

    /**
     *
     */
    public static final String REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE = "addSampleAnalysisFieldToRetrieve";

    /**
     *
     */
    public static final String REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT = "addSampleAnalysisResult";

    /**
     *
     */
    public static final String REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE = "addSampleAnalysisResultFieldToRetrieve";
    public static final String REQUEST_PARAM_ANALYSIS_NAME = "analysisName";
    public static final String REQUEST_PARAM_ANALYSIS = "analysis";

    /**
     *
     */
    public static final String REQUEST_PARAM_ALIQUOT_ID = "aliquotId";

    /**
     *
     */
    public static final String REQUEST_PARAM_BATCH_NAME = "batchName";

    /**
     *
     */
    public static final String REQUEST_PARAM_BATCH_TEMPLATE_ID = "batchTemplateId";

    /**
     *
     */
    public static final String REQUEST_PARAM_BATCH_TEMPLATE_VERSION = "batchTemplateVersion";
    public static final String REQUEST_PARAM_BATCH_POSITION_ROW = "positionRow";
    public static final String REQUEST_PARAM_BATCH_POSITION_COL = "positionCol";
    public static final String REQUEST_PARAM_BATCH_POSITION_OVERRIDE = "positionOverride";
    /**
     *
     */
    public static final String REQUEST_PARAM_CANCEL_CHANGE_COMMENT = "cancelChangeComment";

    /**
     *
     */
    public static final String REQUEST_PARAM_CONFIG_VERSION = "config_version";
    public static final String REQUEST_PARAM_SPEC_FIELD_NAME = "specFieldName";
    public static final String REQUEST_PARAM_SPEC_FIELD_VALUE = "specFieldValue";

    public static final String REQUEST_PARAM_CONFIRM_CHANGE_COMMENT = "confirmChangeComment";

    /**
     *
     */
    public static final String REQUEST_PARAM_CUSTODIAN_CANDIDATE = "custodianCandidate";

    /**
     *
     */
    public static final String REQUEST_PARAM_DB_USERNAME = "dbUserName";

    /**
     *
     */
    public static final String REQUEST_PARAM_DB_PSSWD = "dbUserPassword";

    public static final String REQUEST_PARAM_ESIGN_TO_CHECK = "esignPhraseToCheck";
    public static final String REQUEST_PARAM_AUDIT_REASON_PHRASE = "auditReasonPhrase";
    public static final String REQUEST_PARAM_FIELD_NAME = "fieldName";
    public static final String REQUEST_PARAM_FIELD_VALUE = "fieldValue";
    public static final String REQUEST_PARAM_FINAL_TOKEN = "finalToken";
    public static final String REQUEST_PARAM_INCLUDE_PROC_MODEL_INFO = "includeProcModelInfo";

    /**
     *
     */
    public static final String REQUEST_PARAM_INCUBATOR_NAME = "incubatorName";

    public static final String REQUEST_PARAM_CATEGORY = "category";
    public static final String REQUEST_PARAM_REFERENCE = "reference";
    public static final String REQUEST_PARAM_VOLUME= "volume";
    public static final String REQUEST_PARAM_VOLUME_UOM = "volumeUom";
    
    public static final String REQUEST_PARAM_INCLUDE_MATERIAL = "includesMaterial";
    public static final String REQUEST_PARAM_LOT_NAME = "lotName";
    public static final String REQUEST_PARAM_LOT_DECISION = "lotDecision";
    public static final String REQUEST_PARAM_RETAIN_ID = "retainId";
    public static final String REQUEST_PARAM_NEW_LOCATION_NAME = "newLocationName";
    public static final String REQUEST_PARAM_NEW_LOCATION_ID = "newLocationId";
    public static final String REQUEST_PARAM_NUM_ADHOC_BULKS = "numAdhocBulks";
    /**
     *
     */
    public static final String REQUEST_PARAM_MATERIAL_NAME = "materialName";
    public static final String REQUEST_PARAM_BULK_ID = "bulkId";
    public static final String REQUEST_PARAM_LOT_BULK_DECISION = "lotBulkDecision";

    public static final String REQUEST_PARAM_MY_TOKEN = "myToken";

    /**
     *
     */
    public static final String REQUEST_PARAM_MICROORGANISM_NAME = "microorganismName";

    /**
     *
     */
    public static final String REQUEST_PARAM_NEW_ANALYST = "newAnalyst";

    /**
     *
     */
    public static final String REQUEST_PARAM_NEW_DATE = "newDate";
    public static final String REQUEST_PARAM_NEW_DATETIME = "newDateTime";

    /**
     *
     */
    public static final String REQUEST_PARAM_NUM_SAMPLES_TO_LOG = "numSamplesToLog";

    /**
     *
     */
    public static final String REQUEST_PARAM_OBJECT_ID = "objectId";

    /**
     *
     */
    public static final String REQUEST_PARAM_OBJECT_LEVEL = "objectLevel";

    /**
     *
     */
    public static final String REQUEST_PARAM_OBJECT_LEVEL_RESULT = "RESULT";

    /**
     *
     */
    public static final String REQUEST_PARAM_OBJECT_LEVEL_SAMPLE = "SAMPLE";

    /**
     *
     */
    public static final String REQUEST_PARAM_OBJECT_LEVEL_TEST = "TEST";

    /**
     *
     */
    public static final String REQUEST_PARAM_PSWD_TO_CHECK = "passwordToCheck";
    public static final String REQUEST_PARAM_PSWD_NEW = "newPassword";
    public static final String REQUEST_PARAM_ESIGN_NEW = "newEsign";

    /**
     *
     */
    public static final String REQUEST_PARAM_PARAMETER = "parameter";
    public static final String REQUEST_PARAM_PERSON_FIELDS_NAME = "passwdToCheck";

    /**
     *
     */
    public static final String REQUEST_PARAM_RAW_VALUE_RESULT = "rawValueResult";
    public static final String REQUEST_PARAM_RESULT_NEW_UOM = "newResultUom";

    /**
     *
     */
    public static final String REQUEST_PARAM_RESULT_ID = "resultId";

    public static final String REQUEST_PARAM_LOT_AUDIT_FIELD_TO_RETRIEVE = "lotAuditFieldToRetrieve";
    public static final String REQUEST_PARAM_SAMPLE_AUDIT_FIELD_TO_RETRIEVE = "sampleAuditFieldToRetrieve";

    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_AUDIT_FIELD_TO_SORT = "sampleAuditFieldToSort";

    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_COMMENT = "sampleComment";

    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_ID = "sampleId";

    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_FIELD_NAME = GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME;
    public static final String REQUEST_PARAM_SAMPLE_FIELD_VALUE = GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE;

    /**
     *
     */
    public static final String REQUEST_PARAM_ANA_METH_CERTIF_FIELD_TO_DISPLAY = "analysisMethodCertifFieldsToDisplay";
    public static final String REQUEST_PARAM_CERTIF_OBJECTS_LEVEL = "certification_level";

    public static final String REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE = "sampleFieldToRetrieve";
    public static final String REQUEST_PARAM_SAMPLE_FIELD_TO_DISPLAY = "sampleFieldsToDisplay";
    public static final String REQUEST_PARAM_SAMPLE_WHERE_FIELDS_NAME = "sampleWhereFieldsName";
    public static final String REQUEST_PARAM_SAMPLE_WHERE_FIELDS_VALUE = "sampleWhereFieldsValue";
    public static final String REQUEST_PARAM_INVESTIGATION_GROUPS = "investigationGroups";
    public static final String REQUEST_PARAM_SAMPLE_GROUPS = "sampleGroups";
    public static final String REQUEST_PARAM_INCLUDE_SAMPLES = "includeSamples";
    public static final String REQUEST_PARAM_INCLUDE_SAMPLE_ANALYSIS = "includeSampleAnalysis";
    public static final String REQUEST_PARAM_INCLUDE_SAMPLE_ANALYSIS_RESULTS = "includeSampleAnalysisResults";
    public static final String REQUEST_PARAM_INCLUDE_SAMPLER_SAMPLES = "includeSamplerSamples";
    public static final String REQUEST_PARAM_EXCLUDE_SAMPLER_SAMPLES = "excludeSamplerSamples";
    public static final String REQUEST_PARAM_EXCLUDE_READING_NOT_ENTERED = "excludeReadingNotEntered";
    public static final String REQUEST_PARAM_READING_EQUAL = "readingEqual";
    public static final String REQUEST_PARAM_READING_MIN = "readingMin";
    public static final String REQUEST_PARAM_READING_MAX = "readingMax";
    public static final String REQUEST_PARAM_INCLUDE_MICROORGANISMS = "includeMicroorganisms";
    public static final String REQUEST_PARAM_METHOD_NAME = "methodName";
    public static final String REQUEST_PARAM_METHOD_VERSION = "methodVersion";
    public static final String REQUEST_PARAM_TRAINING_ID = "trainingId";
    public static final String REQUEST_PARAM_MICROORGANISMS_TO_FIND = "MicroorganismsToFind";
    public static final String REQUEST_PARAM_CREATION_DAY_START = "creationDayStart";
    public static final String REQUEST_PARAM_CREATION_DAY_END = "creationDayEnd";
    public static final String REQUEST_PARAM_CLOSURE_DAY_START = "closureDayStart";
    public static final String REQUEST_PARAM_CLOSURE_DAY_END = "closureDayEnd";
    public static final String REQUEST_PARAM_EXCLUDE_NOT_CLOSED_YET = "excludeNotClosedYet";

    public static final String REQUEST_PARAM_SAMPLING_DAY_START = "samplingDayStart";
    public static final String REQUEST_PARAM_SAMPLING_DAY_END = "samplingDayEnd";
    public static final String REQUEST_PARAM_SAMPLER = "samplerName";
    public static final String REQUEST_PARAM_SAMPLER_AREA = "samplerArea";

    public static final String REQUEST_PARAM_LOGIN_DAY_START = "loginDayStart";
    public static final String REQUEST_PARAM_LOGIN_DAY_END = "loginDayEnd";

    public static final String REQUEST_PARAM_BATCH_FIELD_TO_RETRIEVE = "batchFieldToRetrieve";
    public static final String REQUEST_PARAM_BATCH_FIELD_TO_DISPLAY = "batchFieldsToDisplay";
    public static final String REQUEST_PARAM_INCUBATOR_FIELD_TO_RETRIEVE = "incubatorFieldToRetrieve";
    public static final String REQUEST_PARAM_INCUBATOR_FIELD_TO_DISPLAY = "incubatorFieldsToDisplay";
    public static final String REQUEST_PARAM_NAME = "name";
    public static final String REQUEST_PARAM_OUTPUT_IS_FILE = "outputIsFile";

    public static final String REQUEST_PARAM_PRODLOT_FIELD_TO_RETRIEVE = "prodLotFieldToRetrieve";
    public static final String REQUEST_PARAM_PRODLOT_FIELD_TO_SORT = "prodLotFieldToSort";
    public static final String REQUEST_PARAM_PRODLOT_FIELD_TO_DISPLAY = "prodLotFieldsToDisplay";

    public static final String REQUEST_PARAM_QUANTITY = "quantity";
    public static final String REQUEST_PARAM_QUANTITY_UOM = "quantityUom";
    public static final String REQUEST_PARAM_NUM_CONTAINERS = "numContainers";
    public static final String REQUEST_PARAM_NUM_BULKS = "numBulks";
    public static final String REQUEST_PARAM_NUM_DAYS = "numDays";

    public static final String INCUBATION_REPORT_JSON_TAG_NAME_FIELD_TO_RETRIEVE = "incubatorFieldToRetrieve";
    public static final String INCUBATION_REPORT_JSON_TAG_NAME_FIELD_TO_DISPLAY = "incubatorFieldsToDisplay";
    public static final String INCUBATION_REPORT_JSON_TAG_NAME_LAST_N_TEMP_READINGS = "lastTemperatureReadings";
    public static final String JSON_TAG_NAME_SAMPLE_RESULTS = "sample_results";

    public static final String BATCH_REPORT_JSON_TAG_NAME_TEMP_READINGS = "lastTemperatureReadings";

    public static final String REQUEST_PARAM_DATE_START = "startDate";
    public static final String REQUEST_PARAM_DATE_END = "endDate";

    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_LAST_LEVEL = "sampleLastLevel";

    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_STAGE = "sampleStage";

    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_STAGE_NEXT = "sampleStageNext";

    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_STATUS = "sampleStatus";

    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_TEMPLATE = "sampleTemplate";
    public static final String REQUEST_PARAM_SAMPLE_TEMPLATE_VERSION = "sampleTemplateVersion";

    public static final String REQUEST_PARAM_LOT_TEMPLATE = "lotTemplate";
    public static final String REQUEST_PARAM_LOT_TEMPLATE_VERSION = "lotTemplateVersion";
    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE = "sampleAnalysisFieldToRetrieve";

    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_SORT = "sampleAnalysisFieldToSort";

    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE = "sampleAnalysisResultFieldToRetrieve";

    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_FIELD_TO_SORT = "sampleAnalysisResultFieldToSort";

    /**
     *
     */
    public static final String REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE = "sopFieldsToRetrieve";

    /**
     *
     */
    public static final String REQUEST_PARAM_SORT_FIELDS_NAME = "sortFieldsName";

    /**
     *
     */
    public static final String REQUEST_PARAM_SOP_NAME = "sopName";

    /**
     *
     */
    public static final String REQUEST_PARAM_PROCINSTANCENAME = "procInstanceName";

    /**
     *
     */
    public static final String REQUEST_PARAM_TEST_ID = "testId";

    /**
     *
     */
    public static final String REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE = "testFieldToRetrieve";
    public static final String REQUEST_PARAM_TESTING_GROUP = "testingGroup";
    /**
     *
     */
    public static final String REQUEST_PARAM_USER_NAME = "userName";
    public static final String REQUEST_PARAM_USER_ROLE = "userRole";
    public static final String REQUEST_PARAM_USER_SESSION_ID = "userSessionId";
    public static final String REQUEST_PARAM_PERSON = "person";

    /**
     *
     */
    public static final String REQUEST_PARAM_USER_TO_CHECK = "userToCheck";
    public static final String REQUEST_PARAM_TABS_STRING = "tabsString";

    public static final String REQUEST_PARAM_USER_INFO = "userInfoId";

    public static final String REQUEST_PARAM_OBJ_GROUP_NAME = "objGroupName";
    public static final String REQUEST_PARAM_TABLE_CATEGORY = "tableCategory";
    public static final String REQUEST_PARAM_TABLE_NAME = "tableName";

    public static final String REQUEST_PARAM_FIELD_TO_RETRIEVE = "fieldToRetrieve";
    public static final String REQUEST_PARAM_UPDATE_FIELDS_NAME = "updateFieldsName";
    public static final String REQUEST_PARAM_UPDATE_FIELDS_VALUE = "updateFieldsValue";
    public static final String REQUEST_PARAM_WHERE_FIELDS_NAME = "whereFieldsName";
    public static final String REQUEST_PARAM_WHERE_FIELDS_VALUE = "whereFieldsValue";
    public static final String REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME = "sampleAnalysisWhereFieldsName";
    public static final String REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE = "sampleAnalysisWhereFieldsValue";
    public static final String REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME = "sampleAnalysisResultWhereFieldsName";
    public static final String REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE = "sampleAnalysisResultWhereFieldsValue";

    public static final String REQUEST_PARAM_GROUPED = "dataGrouped";
    public static final String REQUEST_PARAM_FIELDS_TO_RETRIEVE_OR_GROUPING = "fieldsToRetrieveOrGrouping";

    
    public static final String LBL_MESSAGE_EN = "message_en";
    public static final String LBL_MESSAGE_ES = "message_es";
    public static final String LBL_DIAGNOSTIC = "diagnostic";
    public static final String LBL_TABLE = "table";
    public static final String LBL_ERROR = "error";
    public static final String LBL_FIELD_NAME = "field_name";
    public static final String LBL_FIELD_VALUE = "field_value";
   
    public static final String LBL_REPOSITORY = "repository";
    public static final String LBL_RPT_INFO = "report_information";
    public static final String LBL_VALUES = "values";
    public static final String LBL_OBJECT_TYPE = "object_type";
    public static final String LBL_USERS_ASSIGNMENT = "users_assignment";
    public static final String LBL_PREFIX_INCUB1 = "incub1_";
    public static final String LBL_PREFIX_INCUB2 = "incub2_";
    public static final String LBL_PREFIX_ALLPENDINGANYINCUB = "allpendinganyincub_";

    public static final String LBL_DATA_DEPLOYED_TABLE="data_deployed_table_";
    public static final String LBL_DATA_IN_DEFINITION_TABLE="data_in_definition_table_";
    public static final String REQUEST_PARAM_VALUE_UNDEFINED = "undefined";

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet globalAPIsParams</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet globalAPIsParams at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
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
