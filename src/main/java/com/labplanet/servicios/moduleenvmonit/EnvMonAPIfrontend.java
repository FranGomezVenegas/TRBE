/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import databases.Rdbms;
import functionaljavaa.samplestructure.DataSampleUtilities;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import databases.TblsProcedure.ProgramCorrectiveAction;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsCnfg;
import databases.TblsProcedure;
import functionaljavaa.materialspec.SpecFrontEndUtilities;
import static functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction.isProgramCorrectiveActionEnable;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPAPIArguments;
import static lbplanet.utilities.LPFrontEnd.noRecordsInTableMessage;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPKPIs;
import trazit.session.ProcedureRequestSession;
/**
 *
 * @author Administrator
 */
public class EnvMonAPIfrontend extends HttpServlet {
  
    /**
     *
     */
    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN;

    /**
     *
     */
    public static final String MANDATORY_PARAMS_PROGRAM_CORRECTIVE_ACTION_LIST="programName";
    
    /**
     *
     */
    public static final String DEFAULT_PARAMS_PROGRAMS_LIST_PROGRAM_TO_GET="name|program_config_id|program_config_version|description_en|description_es"
                                + "|sample_config_code|sample_config_code_version|map_image";     

    /**
     *
     */
    public static final String DEFAULT_PARAMS_PROGRAMS_LIST_PROGRAM_SORT_FLDS="name";

    /**
     *
     */
    public static final String DEFAULT_PARAMS_PROGRAMS_LIST_PROGRAM_LOCATION_TO_GET="program_name|location_name|description_en|description_es|map_icon|map_icon_h|map_icon_w|map_icon_top|map_icon_left|area|spec_code|spec_variation_name|spec_analysis_variation|person_ana_definition|requires_person_ana";

    /**
     *
     */
    public static final String DEFAULT_PARAMS_PROGRAMS_LIST_PROGRAM_LOCATION_SORT_FLDS="order_number|location_name";
    
    /**
     *
     */
    public static final String DEFAULT_PARAMS_PROGRAMS_LIST_CARD_FIELDS="program_name|location_name|area|spec_code|spec_code_version|spec_variation_name|spec_analysis_variation";

    /**
     *
     */
    public static final String DEFAULT_PARAMS_PROGRAMS_LIST_CARD_SORT_FLDS="order_number|location_name";

    /**
     *
     */
    public String[] programLocationCardFieldsInteger=new String[]{"spec_code_version"};

    /**
     *
     */
    public String[] programLocationCardFieldsNoDbType=new String[]{"description_en"};
    
    /**
     *
     */
    public static final String DEFAULT_PARAMS_PROGRAM_CORRECTIVE_ACTION_LIST_FLDS_TO_GET="id|status|status_previous|created_on|created_by|program_name|location_name|area|sample_id|test_id|result_id|limit_id|spec_eval|spec_eval_detail|analysis|method_name|method_version|param_name|spec_rule_with_detail";

    /**
     *
     */
    public static final String DEFAULT_PARAMS_PROGRAM_CORRECTIVE_ACTION_LIST_FLDS_TO_SORT="program_name|created_on desc";

    /**
     *
     */
    public static final String JSON_TAG_NAME_NAME="name";

    /**
     *
     */
    public static final String JSON_TAG_NAME_LABEL_EN="label_en";

    /**
     *
     */
    public static final String JSON_TAG_NAME_LABEL_ES="label_es";

    /**
     *
     */
    public static final String JSON_TAG_NAME_PSWD="password";

    /**
     *
     */
    public static final String JSON_TAG_NAME_PSWD_VALUE_FALSE="false";

    /**
     *
     */
    public static final String JSON_TAG_NAME_TYPE="type";

    /**
     *
     */
    public static final String JSON_TAG_NAME_TYPE_VALUE_TREE_LIST="tree-list";
      
    /**
     *
     */
    public static final String JSON_TAG_NAME_TYPE_VALUE_TEXT="text";      

    /**
     *
     */
    public static final String JSON_TAG_NAME_DB_TYPE="dbType";

    /**
     *
     */
    public static final String JSON_TAG_NAME_DB_TYPE_VALUE_INTEGER="Integer";

    /**
     *
     */
    public static final String JSON_TAG_NAME_DB_TYPE_VALUE_STRING="String";
    
    /**
     *
     */
    public static final String JSON_TAG_NAME_VALUE="value";

    /**
     *
     */
    public static final String JSON_TAG_NAME_TOTAL="total";

    /**
     *
     */
    public static final String JSON_TAG_GROUP_NAME_CARD_PROGRAMS_LIST="programsList";

    /**
     *
     */
    public static final String JSON_TAG_GROUP_NAME_CARD_INFO="card_info";

    /**
     *
     */
    public static final String JSON_TAG_GROUP_NAME_SAMPLES_SUMMARY="samples_summary";

    public static final String JSON_TAG_GROUP_NAME_SAMPLES_SUMMARY_BY_STAGE="samples_summary_by_stage";
    public static final String JSON_TAG_GROUP_NAME_CONFIG_CALENDAR="config_scheduled_calendar";
    
    /**
     *
     */
    public static final String JSON_TAG_GROUP_NAME_SAMPLE_POINTS="sample_points";

    /**
     *
     */
    public static final String JSON_TAG_PROGRAM_DATA_TEMPLATE_DEFINITION="program_data_template_definition";

    /**
     *
     */
    public static final String JSON_TAG_SPEC_DEFINITION="spec_definition";
/*
        
   
 GlobalAPIsParams. GlobalAPIsParams.
GlobalAPIsParams. GlobalAPIsParams. GlobalAPIsParams.  
GlobalAPIsParams.
*/    
    public enum EnvMonAPIfrontendEndpoints{
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
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_GROUPED, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 18),
                }),
        PROGRAMS_CORRECTIVE_ACTION_LIST("PROGRAMS_CORRECTIVE_ACTION_LIST", "", 
                new LPAPIArguments[]{new LPAPIArguments("programName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("programCorrectiveActionFldNameList", LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                    new LPAPIArguments("programCorrectiveActionFldSortList", LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8),}),
        GET_ACTIVE_PRODUCTION_LOTS("GET_ACTIVE_PRODUCTION_LOTS", "", 
                new LPAPIArguments[]{}),
        ;
        private EnvMonAPIfrontendEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){                
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }  
            hm.put(request, argValues);            
            return hm;
        }        
        public String getName(){
            return this.name;
        }
        public String getSuccessMessageCode(){
            return this.successMessageCode;
        }           

        /**
         * @return the arguments
         */
        public LPAPIArguments[] getArguments() {
            return arguments;
        }     
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        if (procReqInstance.getHasErrors()) return;
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        String procInstanceName = procReqInstance.getProcedureInstance();

        try (PrintWriter out = response.getWriter()) {
            
            EnvMonAPIfrontendEndpoints endPoint = null;
            try{
                endPoint = EnvMonAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                             
                   
            switch (endPoint){
                case PROGRAMS_LIST: 

                    String schemaName=LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA);
                    StringBuilder programFldNameList = new StringBuilder();
                    programFldNameList.append(request.getParameter("programFldNameList"));   
                      if (programFldNameList==null || "null".equalsIgnoreCase(programFldNameList.toString())) {
                          programFldNameList = new StringBuilder();
                          programFldNameList.append(DEFAULT_PARAMS_PROGRAMS_LIST_PROGRAM_TO_GET);}
                    String[] programFldNameArray = LPTestingOutFormat.csvExtractFieldValueStringArr(programFldNameList);

                    String programFldSortList = request.getParameter("programFldSortList");   
                      if (programFldSortList==null) programFldSortList = DEFAULT_PARAMS_PROGRAMS_LIST_PROGRAM_SORT_FLDS;                     
                    String[] programFldSortArray = LPTestingOutFormat.csvExtractFieldValueStringArr(programFldSortList);

                    String programLocationFldNameList = request.getParameter("programLocationFldNameList");   
                      if (programLocationFldNameList==null) programLocationFldNameList = DEFAULT_PARAMS_PROGRAMS_LIST_PROGRAM_LOCATION_TO_GET;                     
                    String[] programLocationFldNameArray = LPTestingOutFormat.csvExtractFieldValueStringArr(programLocationFldNameList);

                    String programLocationFldSortList = request.getParameter("programLocationFldSortList");   
                    if (programLocationFldSortList==null)programLocationFldSortList = DEFAULT_PARAMS_PROGRAMS_LIST_PROGRAM_LOCATION_SORT_FLDS;                     
                    String[] programLocationFldSortArray = LPTestingOutFormat.csvExtractFieldValueStringArr(programLocationFldSortList);

                    if (LPArray.valuePosicInArray(programLocationFldNameArray, TblsEnvMonitData.ProgramLocation.FLD_PROGRAM_NAME.getName())==-1){
                        programLocationFldNameArray = LPArray.addValueToArray1D(programLocationFldNameArray, TblsEnvMonitData.ProgramLocation.FLD_PROGRAM_NAME.getName());
                    }                    
                    if (LPArray.valuePosicInArray(programLocationFldNameArray, TblsEnvMonitData.ProgramLocation.FLD_LOCATION_NAME.getName())==-1){
                        programLocationFldNameArray = LPArray.addValueToArray1D(programLocationFldNameArray, TblsEnvMonitData.ProgramLocation.FLD_LOCATION_NAME.getName());
                    }
                    String programLocationCardInfoFldNameList = request.getParameter("programLocationCardInfoFldNameList");   
                      if (programLocationCardInfoFldNameList==null) programLocationCardInfoFldNameList = DEFAULT_PARAMS_PROGRAMS_LIST_CARD_FIELDS;
                    String[] programLocationCardInfoFldNameArray = LPTestingOutFormat.csvExtractFieldValueStringArr(programLocationCardInfoFldNameList);

                    String programLocationCardInfoFldSortList = request.getParameter("programLocationCardInfoFldSortList");   
                      if (programLocationCardInfoFldSortList==null) programLocationCardInfoFldSortList = DEFAULT_PARAMS_PROGRAMS_LIST_CARD_SORT_FLDS;                     
                    String[] programLocationCardInfoFldSortArray = LPTestingOutFormat.csvExtractFieldValueStringArr(programLocationCardInfoFldSortList);

                    String[] programKPIGroupNameArr = new String[0];
                    String programKPIGroupName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_OBJ_GROUP_NAME);   
                    if (programKPIGroupName!=null) 
                        programKPIGroupNameArr = programKPIGroupName.split("\\/");
                    String[] programKPITableCategoryArr = new String[0];
                    String programKPITableCategory = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TABLE_CATEGORY);   
                    if (programKPITableCategory!=null) 
                        programKPITableCategoryArr = programKPITableCategory.split("\\/");
                    String[] programKPITableNameArr = new String[0];
                    String programKPITableName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TABLE_NAME);   
                    if (programKPITableName!=null) 
                        programKPITableNameArr = programKPITableName.split("\\/");
                    String[] programKPIWhereFieldsNameArr = new String[0];
                    String programKPIWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME);   
                    if (programKPIWhereFieldsName!=null) 
                        programKPIWhereFieldsNameArr = programKPIWhereFieldsName.split("\\/");
                    String[] programKPIWhereFieldsValueArr = new String[0];
                    String programKPIWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE);   
                    if (programKPIWhereFieldsValue!=null) 
                        programKPIWhereFieldsValueArr = programKPIWhereFieldsValue.split("\\/");
                    String[] programKPIRetrieveOrGroupingArr = new String[0];
                    String programKPIRetrieveOrGrouping = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FIELDS_TO_RETRIEVE_OR_GROUPING);   
                    if (programKPIRetrieveOrGrouping!=null) 
                        programKPIRetrieveOrGroupingArr = programKPIRetrieveOrGrouping.split("\\/");
                    String[] programKPIGroupedArr = new String[0];
                    String programKPIGrouped = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_GROUPED);   
                    if (programKPIGrouped!=null) 
                        programKPIGroupedArr = programKPIGrouped.split("\\/");

                    if (LPArray.valuePosicInArray(programLocationCardInfoFldNameArray, TblsEnvMonitData.ProgramLocation.FLD_PROGRAM_NAME.getName())==-1){
                        programLocationCardInfoFldNameArray = LPArray.addValueToArray1D(programLocationCardInfoFldNameArray, TblsEnvMonitData.ProgramLocation.FLD_PROGRAM_NAME.getName());
                    }                    
                    if (LPArray.valuePosicInArray(programLocationCardInfoFldNameArray, TblsEnvMonitData.ProgramLocation.FLD_LOCATION_NAME.getName())==-1){
                        programLocationCardInfoFldNameArray = LPArray.addValueToArray1D(programLocationCardInfoFldNameArray, TblsEnvMonitData.ProgramLocation.FLD_LOCATION_NAME.getName());
                    }
                    Object[] statusList = DataSampleUtilities.getSchemaSampleStatusList();
                    Object[] statusListEn = DataSampleUtilities.getSchemaSampleStatusList(LPPlatform.REQUEST_PARAM_LANGUAGE_ENGLISH);
                    Object[] statusListEs = DataSampleUtilities.getSchemaSampleStatusList(LPPlatform.REQUEST_PARAM_LANGUAGE_SPANISH);

                    if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}                    
                    Object[][] programInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsEnvMonitData.Program.TBL.getName(), 
                        new String[]{TblsEnvMonitData.Program.FLD_ACTIVE.getName()}, new Object[]{true}, programFldNameArray, programFldSortArray);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programInfo[0][0].toString())){
                        Rdbms.closeRdbms();                                           
                        Object[] errMsg = LPFrontEnd.responseError(programInfo, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);    
                        return;
                    }
                    JSONArray programsJsonArr = new JSONArray();     
                    for (Object[] curProgram: programInfo){
                        JSONObject programJsonObj = new JSONObject();  
                        String currProgram = curProgram[0].toString();

                        String[] programSampleSummaryFldNameArray = new String[]{TblsEnvMonitData.Sample.FLD_STATUS.getName(), TblsEnvMonitData.Sample.FLD_LOCATION_NAME.getName()};
                        String[] programSampleSummaryFldSortArray = new String[]{TblsEnvMonitData.Sample.FLD_STATUS.getName()};
                        Object[][] programSampleSummary = Rdbms.getRecordFieldsByFilter(schemaName, TblsEnvMonitData.Sample.TBL.getName(), 
                                new String[]{TblsEnvMonitData.ProgramLocation.FLD_PROGRAM_NAME.getName(), }, new String[]{currProgram}, programSampleSummaryFldNameArray, programSampleSummaryFldSortArray);

                        for (int yProc=0; yProc<programInfo[0].length; yProc++){              
                            programJsonObj.put(programFldNameArray[yProc], curProgram[yProc]);
                        }
                        programJsonObj.put(JSON_TAG_NAME_TYPE, JSON_TAG_NAME_TYPE_VALUE_TREE_LIST); 
                        programJsonObj.put(JSON_TAG_NAME_TOTAL, programSampleSummary.length); 
                        JSONObject programkpIsObj = new JSONObject();
                        if (programKPIWhereFieldsName!=null && programKPIWhereFieldsValue!=null){
                            String[] curProgramKPIWhereFieldsNameArr = programKPIWhereFieldsName.split("\\/");
                            String[] curProgramKPIWhereFieldsValueArr = programKPIWhereFieldsValue.split("\\/");
                            for (int i=0;i<curProgramKPIWhereFieldsNameArr.length;i++){
                                curProgramKPIWhereFieldsNameArr[i]=curProgramKPIWhereFieldsNameArr[i]+"|"+TblsEnvMonitData.Sample.FLD_PROGRAM_NAME.getName();
                                curProgramKPIWhereFieldsValueArr[i]=curProgramKPIWhereFieldsValueArr[i]+"|"+currProgram;
                            }
                            programkpIsObj = LPKPIs.getKPIs(procInstanceName, programKPIGroupNameArr, programKPITableCategoryArr, programKPITableNameArr, 
                                    curProgramKPIWhereFieldsNameArr, curProgramKPIWhereFieldsValueArr, programKPIRetrieveOrGroupingArr, programKPIGroupedArr);
                        }
                        programJsonObj.put("KPI", programkpIsObj);   
                        // Program Location subStructure. Begin
                        Object[][] programLocations = Rdbms.getRecordFieldsByFilter(schemaName, TblsEnvMonitData.ProgramLocation.TBL.getName(), 
                                new String[]{TblsEnvMonitData.ProgramLocation.FLD_PROGRAM_NAME.getName()}, new String[]{currProgram}, 
                                programLocationFldNameArray, programLocationFldSortArray);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programLocations[0][0].toString())){
                            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}                           
                            programLocations = Rdbms.getRecordFieldsByFilter(schemaName, TblsEnvMonitData.ProgramLocation.TBL.getName(), 
                                                           new String[]{TblsEnvMonitData.ProgramLocation.FLD_PROGRAM_NAME.getName()}, new String[]{currProgram}, 
                                                           programLocationFldNameArray, programLocationFldSortArray);                            
                        }
                        String[] fieldToRetrieveArr=new String[]{TblsEnvMonitData.Sample.FLD_CURRENT_STAGE.getName()};
                        String[] whereFieldNames=new String[]{TblsEnvMonitData.Sample.FLD_PROGRAM_NAME.getName()}; 
                        Object[] whereFieldValues=new Object[]{currProgram};
                        Object[][] samplesCounterPerStage=Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.Sample.TBL.getName(), 
                                fieldToRetrieveArr, 
                                whereFieldNames, whereFieldValues,
                                new String[]{"COUNTER desc"}); 
                        fieldToRetrieveArr=LPArray.addValueToArray1D(fieldToRetrieveArr, "COUNTER");                            
                        JSONArray programSampleSummaryByStageJsonArray=new JSONArray();
                        for (Object[] curRec: samplesCounterPerStage){
                          JSONObject jObj= LPJson.convertArrayRowToJSONObject(fieldToRetrieveArr, curRec);
                          programSampleSummaryByStageJsonArray.add(jObj);
                        }    
                        programJsonObj.put(JSON_TAG_GROUP_NAME_SAMPLES_SUMMARY_BY_STAGE, programSampleSummaryByStageJsonArray); 

                        JSONObject jObj= new JSONObject();
                        String[] fieldsToRetrieve = new String[]{TblsEnvMonitData.ViewProgramScheduledLocations.FLD_PROGRAM_NAME.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_DATE.getName(),
                            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_PROGRAM_DAY_ID.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_PROGRAM_DAY_DATE.getName(),
                            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SAMPLE_CONFIG_CODE.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SAMPLE_CONFIG_CODE_VERSION.getName(),
                            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_LOCATION_NAME.getName(),            
                            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_AREA.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SPEC_CODE.getName(), 
                            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SPEC_CODE_VERSION.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_AREA.getName(), 
                            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SPEC_VARIATION_NAME.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SPEC_ANALYSIS_VARIATION.getName() 
                        };                            
                        Object[][] programCalendarDatePending=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.ViewProgramScheduledLocations.TBL.getName(), 
                                new String[]{TblsEnvMonitData.ViewProgramScheduledLocations.FLD_PROGRAM_NAME.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{}, 
                                fieldsToRetrieve, new String[]{TblsEnvMonitData.ViewProgramScheduledLocations.FLD_PROGRAM_DAY_DATE.getName()});
                        JSONArray programConfigScheduledPointsJsonArray=new JSONArray();
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programCalendarDatePending[0][0].toString())){
                            jObj.put("message", "Nothing pending in procedure "+procInstanceName+" for the filter "+LPNulls.replaceNull(programCalendarDatePending[6][0]).toString());
                            programConfigScheduledPointsJsonArray.add(jObj);
                        }else{
                            for (Object[] curRecord: programCalendarDatePending){
                                jObj= new JSONObject();
                                for (int i=0;i<curRecord.length;i++){ jObj.put(fieldsToRetrieve[i], curRecord[i].toString());}
                                jObj.put("title", curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitData.ViewProgramScheduledLocations.FLD_LOCATION_NAME.getName())].toString());
                                jObj.put("content", curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitData.ViewProgramScheduledLocations.FLD_LOCATION_NAME.getName())].toString());
                                jObj.put("date",curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitData.ViewProgramScheduledLocations.FLD_DATE.getName())].toString());
                                jObj.put("category","orange");
                                jObj.put("color","#000");
                                programConfigScheduledPointsJsonArray.add(jObj);
                            }
                        }
                        programJsonObj.put(JSON_TAG_GROUP_NAME_CONFIG_CALENDAR, programConfigScheduledPointsJsonArray); 


                        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(programLocations[0][0].toString())){     
                            JSONArray programLocationsJsonArray = new JSONArray();                              
                            for (Object[] programLocations1 : programLocations) {
                                String locationName = programLocations1[LPArray.valuePosicInArray(programLocationFldNameArray, TblsEnvMonitData.ProgramLocation.FLD_LOCATION_NAME.getName())].toString();

                                JSONObject programLocationJsonObj = new JSONObject();     
                                for (int yProcEv = 0; yProcEv<programLocations[0].length; yProcEv++) {
                                    programLocationJsonObj.put(programLocationFldNameArray[yProcEv], programLocations1[yProcEv]);
                                }
                                Object[][] programLocationCardInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsEnvMonitData.ProgramLocation.TBL.getName(), 
                                        new String[]{TblsEnvMonitData.ProgramLocation.FLD_PROGRAM_NAME.getName(), TblsEnvMonitData.ProgramLocation.FLD_LOCATION_NAME.getName()}, new String[]{currProgram, locationName}, 
                                        programLocationCardInfoFldNameArray, programLocationCardInfoFldSortArray);
                                JSONArray programLocationCardInfoJsonArr = new JSONArray(); 

                                JSONObject programLocationCardInfoJsonObj = new JSONObject();  
                                for (int xProc=0; xProc<programLocationCardInfo.length; xProc++){   
                                    for (int yProc=0; yProc<programLocationCardInfo[0].length; yProc++){              
                                        programLocationCardInfoJsonObj = new JSONObject();
                                        programLocationCardInfoJsonObj.put(JSON_TAG_NAME_NAME, programLocationCardInfoFldNameArray[yProc]);
                                        programLocationCardInfoJsonObj.put(JSON_TAG_NAME_LABEL_EN, programLocationCardInfoFldNameArray[yProc]);
                                        programLocationCardInfoJsonObj.put(JSON_TAG_NAME_LABEL_ES, programLocationCardInfoFldNameArray[yProc]);
                                        programLocationCardInfoJsonObj.put(JSON_TAG_NAME_VALUE, programLocationCardInfo[xProc][yProc]);
                                        programLocationCardInfoJsonObj.put(JSON_TAG_NAME_TYPE, JSON_TAG_NAME_TYPE_VALUE_TEXT);
                                        String fieldName=programLocationCardInfoFldNameArray[yProc];
                                        Integer posicInArray=LPArray.valuePosicInArray(programLocationCardFieldsInteger, fieldName);
                                        if (posicInArray>-1){
                                            programLocationCardInfoJsonObj.put(JSON_TAG_NAME_DB_TYPE, JSON_TAG_NAME_DB_TYPE_VALUE_INTEGER);
                                        }else{ 
                                            posicInArray=LPArray.valuePosicInArray(programLocationCardFieldsNoDbType, fieldName);
                                            if (posicInArray==-1){
                                                programLocationCardInfoJsonObj.put(JSON_TAG_NAME_DB_TYPE, JSON_TAG_NAME_DB_TYPE_VALUE_STRING);
                                            }else{
                                                programLocationCardInfoJsonObj.put(JSON_TAG_NAME_DB_TYPE, "");
                                            }
                                        }
                                        programLocationCardInfoJsonObj.put(JSON_TAG_NAME_PSWD, JSON_TAG_NAME_PSWD_VALUE_FALSE);
                                        programLocationCardInfoJsonArr.add(programLocationCardInfoJsonObj);                                    
                                    }    
                                }
                                programLocationJsonObj.put(JSON_TAG_GROUP_NAME_CARD_INFO, programLocationCardInfoJsonArr);  
                                Object[] samplesStatusCounter = new Object[0];
                                for (Object statusList1 : statusList) {
                                    String currStatus = statusList1.toString();
                                    Integer contSmpStatus=0;
                                    for (Object[] smpStatus: programSampleSummary){
                                        if (currStatus.equalsIgnoreCase(smpStatus[0].toString()) && 
                                                ( smpStatus[1]!=null) && locationName.equalsIgnoreCase(smpStatus[1].toString()) ){contSmpStatus++;}
                                    }
                                    samplesStatusCounter = LPArray.addValueToArray1D(samplesStatusCounter, contSmpStatus);
                                }


                                JSONArray programSampleSummaryJsonArray = new JSONArray();  
                                for (int iStatuses=0; iStatuses < statusList.length; iStatuses++){
                                    JSONObject programSampleSummaryJsonObj = new JSONObject();  
                                    programSampleSummaryJsonObj.put(JSON_TAG_NAME_NAME, statusList[iStatuses]);
                                    programSampleSummaryJsonObj.put(JSON_TAG_NAME_LABEL_EN, statusListEn[iStatuses]);
                                    programSampleSummaryJsonObj.put(JSON_TAG_NAME_LABEL_ES, statusListEs[iStatuses]);
                                    programSampleSummaryJsonObj.put(JSON_TAG_NAME_VALUE, samplesStatusCounter[iStatuses]);
                                    programSampleSummaryJsonObj.put(JSON_TAG_NAME_TYPE, JSON_TAG_NAME_TYPE_VALUE_TEXT);
                                    programSampleSummaryJsonObj.put(JSON_TAG_NAME_PSWD, JSON_TAG_NAME_PSWD_VALUE_FALSE);
                                    programSampleSummaryJsonArray.add(programSampleSummaryJsonObj);
                                }
                                programLocationJsonObj.put(JSON_TAG_GROUP_NAME_SAMPLES_SUMMARY, programSampleSummaryJsonArray); 
                                fieldToRetrieveArr=new String[]{TblsEnvMonitData.Sample.FLD_CURRENT_STAGE.getName()};
                                whereFieldNames=new String[]{TblsEnvMonitData.Sample.FLD_PROGRAM_NAME.getName(), TblsEnvMonitData.Sample.FLD_LOCATION_NAME.getName()}; 
                                whereFieldValues=new Object[]{currProgram, locationName};
                                samplesCounterPerStage=Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.Sample.TBL.getName(), 
                                        fieldToRetrieveArr, 
                                        whereFieldNames, whereFieldValues,
                                        new String[]{"COUNTER desc"}); 
                                fieldToRetrieveArr=LPArray.addValueToArray1D(fieldToRetrieveArr, "COUNTER");                            
                                programSampleSummaryByStageJsonArray=new JSONArray();
                                for (Object[] curRec: samplesCounterPerStage){
                                  jObj= LPJson.convertArrayRowToJSONObject(fieldToRetrieveArr, curRec);
                                  programSampleSummaryByStageJsonArray.add(jObj);
                                }                                
                                programLocationJsonObj.put(JSON_TAG_GROUP_NAME_SAMPLES_SUMMARY_BY_STAGE, programSampleSummaryByStageJsonArray);

                                programLocationsJsonArray.add(programLocationJsonObj);
                            }                    
                            programJsonObj.put(JSON_TAG_GROUP_NAME_SAMPLE_POINTS, programLocationsJsonArray);   
                        }
                        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(programLocations[0][0].toString())){     
                            JSONArray programSampleSummaryJsonArray = new JSONArray();   
                            Object[] samplesStatusCounter = new Object[0];
                            for (Object statusList1 : statusList) {
                                String currStatus = statusList1.toString();
                                Integer contSmpStatus=0;
                                for (Object[] smpStatus: programSampleSummary){
                                    if (currStatus.equalsIgnoreCase(smpStatus[0].toString())){contSmpStatus++;}
                                }
                                samplesStatusCounter = LPArray.addValueToArray1D(samplesStatusCounter, contSmpStatus);
                            }
                            for (int iStatuses=0; iStatuses < statusList.length; iStatuses++){
                                JSONObject programSampleSummaryJsonObj = new JSONObject();  
                                programSampleSummaryJsonObj.put(JSON_TAG_NAME_NAME, LPNulls.replaceNull(statusList[iStatuses]));
                                programSampleSummaryJsonObj.put(JSON_TAG_NAME_LABEL_EN, LPNulls.replaceNull(statusListEn[iStatuses]));
                                programSampleSummaryJsonObj.put(JSON_TAG_NAME_LABEL_ES, LPNulls.replaceNull(statusListEs[iStatuses]));
                                programSampleSummaryJsonObj.put(JSON_TAG_NAME_VALUE, LPNulls.replaceNull(samplesStatusCounter[iStatuses]));
                                programSampleSummaryJsonObj.put(JSON_TAG_NAME_TYPE, JSON_TAG_NAME_TYPE_VALUE_TEXT);
                                programSampleSummaryJsonObj.put(JSON_TAG_NAME_PSWD, JSON_TAG_NAME_PSWD_VALUE_FALSE);
                                programSampleSummaryJsonArray.add(programSampleSummaryJsonObj);
                            }
                            programJsonObj.put(JSON_TAG_GROUP_NAME_SAMPLES_SUMMARY, programSampleSummaryJsonArray);                             
                        }
                        programsJsonArr.add(programJsonObj);
                        JSONObject programDataTemplateDefinition = new JSONObject();
                        JSONObject templateProgramInfo=EnvMonFrontEndUtilities.dataProgramInfo(procInstanceName, currProgram, null, null);
                        programDataTemplateDefinition.put(TblsEnvMonitData.Program.TBL.getName(), templateProgramInfo);
                        JSONArray templateProgramLocationInfo=EnvMonFrontEndUtilities.dataProgramLocationInfo(procInstanceName, currProgram, null, null);
                        programDataTemplateDefinition.put(TblsEnvMonitData.ProgramLocation.TBL.getName(), templateProgramLocationInfo);
                        programJsonObj.put(JSON_TAG_PROGRAM_DATA_TEMPLATE_DEFINITION, programDataTemplateDefinition); 
                        Object specCode = templateProgramInfo.get(TblsEnvMonitData.Program.FLD_SPEC_CODE.getName());
                        Object specConfigVersion = templateProgramInfo.get(TblsEnvMonitData.Program.FLD_SPEC_CONFIG_VERSION.getName());                    
                        JSONObject specDefinition = new JSONObject();
                        if (!(specCode==null || specCode=="" || specConfigVersion==null || "".equals(specConfigVersion.toString()))){
                          JSONObject specInfo=SpecFrontEndUtilities.configSpecInfo((String) specCode, (Integer) specConfigVersion, 
                                  null, null);
                          specDefinition.put(TblsCnfg.Spec.TBL.getName(), specInfo);
                          JSONArray specLimitsInfo=SpecFrontEndUtilities.configSpecLimitsInfo((String) specCode, (Integer) specConfigVersion, 
                                  null, new String[]{TblsCnfg.SpecLimits.FLD_VARIATION_NAME.getName(), TblsCnfg.SpecLimits.FLD_ANALYSIS.getName(), 
                                  TblsCnfg.SpecLimits.FLD_METHOD_NAME.getName(), TblsCnfg.SpecLimits.FLD_LIMIT_ID.getName()});
                          specDefinition.put(TblsCnfg.SpecLimits.TBL.getName(), specLimitsInfo);
                        }
                        programJsonObj.put(JSON_TAG_SPEC_DEFINITION, specDefinition); 
                    }          
                    JSONObject programsListObj = new JSONObject();
                    programsListObj.put(JSON_TAG_GROUP_NAME_CARD_PROGRAMS_LIST, programsJsonArr);
                    response.getWriter().write(programsListObj.toString());
                    Rdbms.closeRdbms();                    
                    Response.ok().build();
                    return;  
                case PROGRAMS_CORRECTIVE_ACTION_LIST:    
                  Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_PROGRAM_CORRECTIVE_ACTION_LIST.split("\\|"));                       
                  if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                      LPFrontEnd.servletReturnResponseError(request, response, 
                          LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                      return;          
                  }                       
                  String programName = request.getParameter("programName");   
                  programFldNameList = new StringBuilder();
                  programFldNameList.append(request.getParameter("programCorrectiveActionFldNameList"));   
                  if ("null".equalsIgnoreCase(programFldNameList.toString())){
                      programFldNameList = new StringBuilder();
                      programFldNameList.append(DEFAULT_PARAMS_PROGRAM_CORRECTIVE_ACTION_LIST_FLDS_TO_GET);
                  }
                  if ("null".equalsIgnoreCase(programFldNameList.toString()) || programFldNameList.length()==0) {
                    programFldNameList = new StringBuilder();
                    programFldNameList.append("");

                    for (TblsProcedure.ProgramCorrectiveAction obj: TblsProcedure.ProgramCorrectiveAction.values()){
                        String objName = obj.name();
                        if ( (!"TBL".equalsIgnoreCase(objName)) && (programFldNameList.length()>0) ) programFldNameList.append("|");
                          programFldNameList.append(obj.getName());
                    }      
                  }                  
                  programFldNameArray = LPTestingOutFormat.csvExtractFieldValueStringArr(programFldNameList);
                  if (!LPArray.valueInArray(programFldNameArray, TblsProcedure.ProgramCorrectiveAction.FLD_ID.getName()))
                    programFldNameArray=LPArray.addValueToArray1D(programFldNameArray, TblsProcedure.ProgramCorrectiveAction.FLD_ID.getName());

                  programFldSortList = request.getParameter("programCorrectiveActionFldSortList");   
                  if (programFldSortList==null) programFldSortList = DEFAULT_PARAMS_PROGRAM_CORRECTIVE_ACTION_LIST_FLDS_TO_SORT;                     
                  programFldSortArray = LPTestingOutFormat.csvExtractFieldValueStringArr(programFldSortList);

                  String statusClosed=Parameter.getParameterBundle(procInstanceName+"-"+LPPlatform.SCHEMA_DATA, "programCorrectiveAction_statusClosed");
                  JSONArray jArray = new JSONArray(); 
                  if (!isProgramCorrectiveActionEnable(procInstanceName)){
                    JSONObject jObj=new JSONObject();
                    jArray.add(jObj.put(programFldNameArray, "program corrective action not active!"));
                  }
                  else{
                    Object[][] programCorrectiveAction = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_PROCEDURE), ProgramCorrectiveAction.TBL.getName(), 
                            new String[]{TblsProcedure.ProgramCorrectiveAction.FLD_PROGRAM_NAME.getName(), TblsProcedure.ProgramCorrectiveAction.FLD_STATUS.getName()+"<>"}, 
                            new String[]{programName, statusClosed}, 
                            programFldNameArray, programFldSortArray);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programCorrectiveAction[0][0].toString()))LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());


                    for (Object[] curRow: programCorrectiveAction){
                      JSONObject jObj=LPJson.convertArrayRowToJSONObject(programFldNameArray, curRow);
                      jArray.add(jObj);
                    }
                  }
                  Rdbms.closeRdbms();                    
                  LPFrontEnd.servletReturnSuccess(request, response, jArray);
                  break;
                case GET_ACTIVE_PRODUCTION_LOTS:
                    String[] fieldsToRetrieve = new String[0];
                     TblsEnvMonitData.ProductionLot[] fieldsListPrLot = TblsEnvMonitData.ProductionLot.values();                 
                     for (TblsEnvMonitData.ProductionLot fieldsList1 : fieldsListPrLot) {
                       if (!"TBL".equalsIgnoreCase(fieldsList1.name())) {
                         fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fieldsList1.getName());
                       }                  
                     }
                     Object[][] list = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.ProductionLot.TBL.getName(), 
                             new String[]{TblsEnvMonitData.ProductionLot.FLD_ACTIVE.getName()}, 
                             new Object[]{true}
                             , fieldsToRetrieve
                             , new String[]{TblsEnvMonitData.ProductionLot.FLD_CREATED_ON.getName()+" desc"} );
                     JSONArray jArr=new JSONArray();
                     if (LPPlatform.LAB_FALSE.equalsIgnoreCase(list[0][0].toString())){
                          JSONObject jObj= noRecordsInTableMessage();                    
                          jArr.add(jObj);
                     }else{
                        for (Object[] curRec: list){
                          JSONObject jObj= LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRec);
                          jArr.add(jObj);
                        }
                     }
                     LPFrontEnd.servletReturnSuccess(request, response, jArr);
                     return;
                default:      
                    Rdbms.closeRdbms(); 
                    RequestDispatcher rd = request.getRequestDispatcher(SampleAPIParams.SERVLET_FRONTEND_URL);
                    rd.forward(request,response);   
            }
        }catch(Exception e){      
            procReqInstance.killIt();
            String[] errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, errMsg);
        } finally {
            // release database resources
            try {
                procReqInstance.killIt();
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }      }

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
