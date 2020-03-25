/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import lbplanet.utilities.LPFrontEnd;
import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.moduleenvmonit.EnvMonitAPIParams;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;

/**
 *
 * @author Administrator
 */
public class SampleAPIParams extends HttpServlet {
  
    /**
     *
     */
    public static final String SERVLET_API_URL="/modulesample/SampleAPI";  

    /**
     *
     */
    public static final String SERVLET_FRONTEND_URL="/frontend/SampleAPIfrontEnd";
    
    public enum SampleAPIEndpoints{
        /**
         *
         */
        LOGSAMPLE("LOGSAMPLE", "sampleTemplate|sampleTemplateVersion", "", "sampleLogged_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_TEMPLATE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 9),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_LOCATION_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 11)}),
        RECEIVESAMPLE("RECEIVESAMPLE", "sampleId", "", "sampleReceived_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}),
        SETSAMPLINGDATE("SETSAMPLINGDATE", "sampleId", "", "setSamplingDate_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.DATE.toString(), true, 6)}),
        CHANGESAMPLINGDATE("CHANGESAMPLINGDATE", "sampleId|newDate", "", "changeSamplingDate_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NEW_DATE, LPAPIArguments.ArgumentType.DATE.toString(), true, 7)}),
        SAMPLINGCOMMENTADD("SAMPLINGCOMMENTADD", "sampleId|sampleComment", "", "samplingCommentAdd_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_COMMENT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
        SAMPLINGCOMMENTREMOVE("SAMPLINGCOMMENTREMOVE", "sampleId", "", "samplingCommentRemove_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}),
        INCUBATIONSTART("INCUBATIONSTART", "sampleId", "", "incubationStart_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}),
        INCUBATIONEND("INCUBATIONEND", "sampleId", "", "incubationEnd_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}),
        INCUBATION2START("INCUBATION2START", "sampleId", "", "incubation2Start_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}),
        INCUBATION2END("INCUBATION2END", "sampleId", "", "incubation2End_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}),
        SAMPLEANALYSISADD("SAMPLEANALYSISADD", "sampleId|fieldName|fieldValue", "", "sampleAnalysisAdd_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8)}),
        TESTASSIGNMENT("TESTASSIGNMENT", "testId|newAnalyst", "", "testAssignment_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NEW_ANALYST, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
        ENTERRESULT("ENTERRESULT", "resultId|rawValueResult", "", "enterResult_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
        ENTERRESULT_LOD("ENTERRESULT_LOD", "incidentId|note", "", "enterResultLOD_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
        RESULT_CHANGE_UOM("RESULT_CHANGE_UOM", "incidentId|note", "", "resultChangeUOM_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_NEW_UOM, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
        REVIEWRESULT("REVIEWRESULT", "objectId|objectLevel", "", "reviewResult_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OBJECT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OBJECT_LEVEL, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
        UNREVIEWRESULT("UNREVIEWRESULT", "objectId|objectLevel", "", "unreviewResult_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OBJECT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OBJECT_LEVEL, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
        CANCELRESULT("CANCELRESULT", "objectId|objectLevel", "", "cancelResult_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OBJECT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OBJECT_LEVEL, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
        UNCANCELRESULT("UNCANCELRESULT", "objectId|objectLevel", "", "uncancelResult_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OBJECT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OBJECT_LEVEL, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
        COC_ABORTCHANGE("COC_ABORTCHANGE", "sampleId|cancelChangeComment", "", "cocAbortChange_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_CANCEL_CHANGE_COMMENT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
        COC_STARTCHANGE("COC_STARTCHANGE", "sampleId|custodianCandidate", "", "cocStartChange_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_CUSTODIAN_CANDIDATE, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
        COC_CONFIRMCHANGE("COC_CONFIRMCHANGE", "sampleId|confirmChangeComment", "", "cocConfirmChange_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_CONFIRM_CHANGE_COMMENT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
        GETSAMPLEINFO("GETSAMPLEINFO", "sampleId|sampleFieldToRetrieve", "", "getSampleInfo_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7)}),
        TOKEN_VALIDATE_ESIGN_PHRASE("TOKEN_VALIDATE_ESIGN_PHRASE", "myToken|esignPhraseToCheck", "", "tokenValidateEsignPhrase_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7)}),
        LOGALIQUOT("LOGALIQUOT", "sampleId", "", "logAliquot_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}),
        LOGSUBALIQUOT("LOGSUBALIQUOT", "aliquotId", "", "logSubAliquot_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ALIQUOT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}),
        SAMPLESTAGE_MOVETONEXT("SAMPLESTAGE_MOVETONEXT", "sampleId", "", "sampleStage_moveToNext_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}),
        SAMPLESTAGE_MOVETOPREVIOUS("SAMPLESTAGE_MOVETOPREVIOUS", "sampleId", "", "sampleStage_moveToPrevious_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}),
        SAMPLEAUDIT_SET_AUDIT_ID_REVIEWED("SAMPLEAUDIT_SET_AUDIT_ID_REVIEWED", "auditId", "", "sampleAudit_setAuditIdReviewed_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_AUDIT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}),
        ;      
        private SampleAPIEndpoints(String name, String mandatoryParams, String optionalParams, String successMessageCode, LPAPIArguments[] argums){
            this.name=name;
            this.mandatoryParams=mandatoryParams;
            this.optionalParams=optionalParams;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;            
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+contentLine[lineIndex][curArg.getTestingArgPosic()]);
                request.setAttribute(curArg.getName(), contentLine[lineIndex][curArg.getTestingArgPosic()]);
            }  
            hm.put(request, argValues);            
            return hm;
        }
        
        public String getName(){
            return this.name;
        }
        public String getMandatoryParams(){
            return this.mandatoryParams;
        }
        public String getSuccessMessageCode(){
            return this.successMessageCode;
        }           
        private String[] getEndpointDefinition(){
            return new String[]{this.name, this.mandatoryParams, this.optionalParams, this.successMessageCode};
        }
        /**
         * @return the arguments
         */
        public LPAPIArguments[] getArguments() {
            return arguments;
        }
     
        private final String name;
        private final String mandatoryParams; 
        private final String optionalParams; 
        private final String successMessageCode;    
        public  LPAPIArguments[] arguments;
    }
    
    

    public static final String MANDATORY_PARAMS_MAIN_SERVLET = "actionName|finalToken|schemaPrefix";

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
