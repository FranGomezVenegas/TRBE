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
    
    // Por favor, agregar ordenado alfabeticamente para evitar duplicados

    /**
     *
     */
    public static final String REQUEST_PARAM_ACTION_NAME = "actionName";

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

    /**
     *
     */
    public static final String REQUEST_PARAM_ESIGN_TO_CHECK = "esignPhraseToCheck"; 

    /**
     *
     */
    public static final String REQUEST_PARAM_FIELD_TO_RETRIEVE = "fieldToRetrieve"; 

    /**
     *
     */
    public static final String REQUEST_PARAM_FIELD_NAME = "fieldName";

    /**
     *
     */
    public static final String REQUEST_PARAM_FIELD_VALUE = "fieldValue";

    /**
     *
     */
    public static final String REQUEST_PARAM_FINAL_TOKEN = "finalToken";    

    /**
     *
     */
    public static final String REQUEST_PARAM_INCUBATOR_NAME = "incubatorName";        

    /**
     *
     */
    public static final String REQUEST_PARAM_LOT_NAME = "lotName";    

    /**
     *
     */
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

    /**
     *
     */
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
    public static final String REQUEST_PARAM_SAMPLE_FIELD_NAME = "fieldName";

    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_FIELD_VALUE = "fieldValue";

    /**
     *
     */
    public static final String REQUEST_PARAM_SOP_FIELD_TO_DISPLAY = "sopFieldsToDisplay";
    
    public static final String REQUEST_PARAM_GROUPER_NAME = "grouperName";
    
    public static final String REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE = "sampleFieldToRetrieve";
    public static final String REQUEST_PARAM_SAMPLE_FIELD_TO_DISPLAY = "sampleFieldsToDisplay";
    public static final String REQUEST_PARAM_SAMPLE_WHERE_FIELDS_NAME = "sampleWhereFieldsName"; 
    public static final String REQUEST_PARAM_SAMPLE_WHERE_FIELDS_VALUE = "sampleWhereFieldsValue";   
    public static final String REQUEST_PARAM_SAMPLE_GROUPS = "sampleGroups";

    public static final String REQUEST_PARAM_BATCH_FIELD_TO_RETRIEVE = "batchFieldToRetrieve";
    public static final String REQUEST_PARAM_BATCH_FIELD_TO_DISPLAY = "batchFieldsToDisplay";
    public static final String REQUEST_PARAM_INCUBATOR_FIELD_TO_RETRIEVE = "incubatorFieldToRetrieve";
    public static final String REQUEST_PARAM_INCUBATOR_FIELD_TO_DISPLAY = "incubatorFieldsToDisplay";
    public static final String REQUEST_PARAM_PRODLOT_FIELD_TO_RETRIEVE = "prodLotFieldToRetrieve";
    public static final String REQUEST_PARAM_PRODLOT_FIELD_TO_DISPLAY = "prodLotFieldsToDisplay";

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

    /**
     *
     */
    public static final String REQUEST_PARAM_SAMPLE_TEMPLATE_VERSION = "sampleTemplateVersion";

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
    public static final String REQUEST_PARAM_SCHEMA_PREFIX = "schemaPrefix";    

    /**
     *
     */
    public static final String REQUEST_PARAM_TEST_ID = "testId";

    /**
     *
     */
    public static final String REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE = "testFieldToRetrieve";

    /**
     *
     */
    public static final String REQUEST_PARAM_USER_ROLE = "userRole";

    /**
     *
     */
    public static final String REQUEST_PARAM_USER_TO_CHECK = "userToCheck";
    public static final String REQUEST_PARAM_TABS_STRING = "tabsString";

    /**
     *
     */
    public static final String REQUEST_PARAM_USER_INFO = "userInfoId";

    /**
     *
     */
    public static final String REQUEST_PARAM_WHERE_FIELDS_NAME = "whereFieldsName"; 
    public static final String REQUEST_PARAM_WHERE_FIELDS_VALUE = "whereFieldsValue";
    
    /**
     *
     */
    public static final String REQUEST_PARAM_VALUE_UNDEFINED="undefined";
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
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
