/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import lbplanet.utilities.LPFrontEnd;
import com.labplanet.servicios.app.GlobalAPIsParams;
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
public class EnvMonitAPIParams extends HttpServlet {
    public static final String SERVLET_API_URL="/frontend/SampleAPIfrontEnd";  
    public static final String SERVLET_FRONTEND_URL="/frontend/SampleAPIfrontEnd";
    
    public static final String REQUEST_PARAM_AREA = "area";
    public static final String REQUEST_PARAM_INCUBATOR_NAME = "incubatorName";
    public static final String REQUEST_PARAM_BATCH_NAME = "batchName";
    public static final String REQUEST_PARAM_LOT_NAME = "lotName";
    public static final String REQUEST_PARAM_GROUPED = "grouped";
    public static final String REQUEST_PARAM_INCUBATOR_NUM_POINTS = "numPoints";
    public static final String REQUEST_PARAM_INCUBATOR_TEMPERATURE = "temperature";
    public static final String REQUEST_PARAM_STAGES_TO_INCLUDE = "stagesToInclude";
    public static final String REQUEST_PARAM_STAGES_TO_EXCLUDE = "stagesToExclude";
    public static final String REQUEST_PARAM_PROGRAM_NAME = "programName";
    public static final String REQUEST_PARAM_LOCATION_NAME = "locationName";
    public static final String REQUEST_PARAM_TOTAL_OBJECTS = "totalObjects";        
    
    public static final String REQUEST_PARAM_BATCH_FIELD_TO_RETRIEVE = "batchFieldToRetrieve";
    public static final String REQUEST_PARAM_BATCH_FIELD_TO_DISPLAY = "batchFieldsToDisplay";
    public static final String REQUEST_PARAM_INCUBATOR_FIELD_TO_RETRIEVE = "incubatorFieldToRetrieve";
    public static final String REQUEST_PARAM_INCUBATOR_FIELD_TO_DISPLAY = "incubatorFieldsToDisplay";
    public static final String REQUEST_PARAM_PROD_LOT_FIELD_TO_RETRIEVE = "prodLotFieldToRetrieve";
    public static final String REQUEST_PARAM_PROD_LOT_FIELD_TO_DISPLAY = "prodLotFieldsToDisplay";

    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

    /**
     *
     */
    public static final String MANDATORY_PARAMS_CASE_TOKEN_VALIDATE_ESIGN_PHRASE = "myToken|esignPhraseToCheck";

    /**
     *
     */
    public static final String MANDATORY_PARAMS_LOGSAMPLE="sampleTemplate|sampleTemplateVersion|programName|locationName";

    /**
     *
     */
    public static final String MANDATORY_PARAMS_RECEIVESAMPLE="sampleId";

    /**
     *
     */
    public static final String MANDATORY_PARAMS_CHANGESAMPLINGDATE="sampleId|newDate";  

    /**
     *
     */
    public static final String MANDATORY_PARAMS_SAMPLINGCOMMENTADD="sampleId|sampleComment";  

    /**
     *
     */
    public static final String MANDATORY_PARAMS_SAMPLINGCOMMENTREMOVE="sampleId";  

    /**
     *
     */
    public static final String MANDATORY_PARAMS_INCUBATIONSTART="sampleId";  

    /**
     *
     */
    public static final String MANDATORY_PARAMS_INCUBATIONEND="sampleId";  

    /**
     *
     */
    public static final String MANDATORY_PARAMS_SAMPLEANALYSISADD="sampleId|fieldName|fieldValue";  

    /**
     *
     */
    public static final String MANDATORY_PARAMS_ENTERRESULT="resultId|rawValueResult"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_REVIEWRESULT="objectId|objectLevel"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_CANCELRESULT="objectId|objectLevel"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_UNREVIEWRESULT="objectId|objectLevel"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_UNCANCELRESULT="objectId|objectLevel"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_TESTASSIGNMENT="testId|newAnalyst"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_GETSAMPLEINFO="sampleId|sampleFieldToRetrieve"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_COC_STARTCHANGE="sampleId|custodianCandidate"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_COC_CONFIRMCHANGE="sampleId|confirmChangeComment"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_COC_ABORTCHANGE="sampleId|cancelChangeComment"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_LOGALIQUOT="sampleId"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_LOGSUBALIQUOT="aliquotId"; 
    
    /**
     *
     */
    public static final String MANDATORY_PARAMS_BATCH_INCUB_CREATE="batchName|batchTemplateId|batchTemplateVersion"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_BATCH_INCUB_END="batchName|batchTemplateId|batchTemplateVersion"; 
    
    
    /**
     *
     */
    public static final String MANDATORY_PARAMS_FRONTEND_UNRECEIVESAMPLES_LIST="sortFieldsName|sampleFieldToRetrieve"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_FRONTEND_UNRECEIVESAMPLES_LIST_SORT_FIELDS_NAME_DEFAULT_VALUE="";

    /**
     *
     */
    public static final String MANDATORY_PARAMS_FRONTEND_SAMPLES_INPROGRESS_LIST_SAMPLE_ANALYSIS_FIELD_RETRIEVE_DEFAULT_VALUE="test_id|status|analysis|method_name|method_version";

    /**
     *
     */
    public static final String MANDATORY_PARAMS_FRONTEND_SAMPLES_INPROGRESS_LIST_SAMPLE_ANALYSIS_RESULT_FIELD_RETRIEVE_DEFAULT_VALUE="result_id|status|param_name|raw_value|pretty_value";

    /**
     *
     */
    public static final String MANDATORY_PARAMS_FRONTEND_UNRECEIVESAMPLES_LIST_SAMPLE_FIELD_RETRIEVE_DEFAULT_VALUE="sample_id";

    /**
     *
     */
    public static final String MANDATORY_PARAMS_FRONTEND_GET_SAMPLE_ANALYSIS_LIST=GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_FRONTEND_GET_SAMPLE_ANALYSIS_RESULT_LIST="sampleId"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_FRONTEND_CHANGEOFCUSTODY_SAMPLE_HISTORY="sampleId"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_FRONTEND_GET_SAMPLE_ANALYSIS_RESULT_SPEC="resultId"; 
    
    /**
     *
     */
    public static final String MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_ANALYSIS_ALL_LIST="code|method_name|method_version"; 

    /**
     *
     */
    public static final String MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_GET_SAMPLE_ANALYSIS_LIST="sample_id|test_id"; 

    /**
     *
     */
    public static final String MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_GET_SAMPLE_ANALYSIS_RESULT_LIST="sample_id|test_id|result_id|param_name|limit_id"; 

    /**
     *
     */
    public static final String MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_CHANGEOFCUSTODY_SAMPLE_HISTORY="sample_id|custodian|custodian_name|custodian_candidate|candidate_name|coc_started_on|status|coc_confirmed_on"; 

    /**
     *
     */
    public static final String MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_CHANGEOFCUSTODY_USERS_LIST="user_name|person_name"; 
    
    /**
     *
     */
    public static final String MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_GET_SAMPLE_ANALYSIS_LIST="sample_id|test_id"; 

    /**
     *
     */
    public static final String MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_GET_SAMPLE_ANALYSIS_RESULT_LIST="sample_id|test_id|result_id"; 

    /**
     *
     */
    public static final String MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_CHANGEOFCUSTODY_SAMPLE_HISTORY="sample_id|coc_started_on"; 

    /**
     *
     */
    public static final String MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_CHANGEOFCUSTODY_USERS_LIST="user_name|person_name"; 
    
    /**
     *
     */
    public static final String JSON_TAG_NAME_SPEC_RULE_DETAILED="spec_rule_with_detail";
    
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
            out.println("<title>Servlet sampleAPIParams</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet sampleAPIParams at " + request.getContextPath() + "</h1>");
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
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
