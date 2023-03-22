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
import com.labplanet.servicios.modulesample.ClassSample;
import com.labplanet.servicios.modulesample.SampleAPIParams.SampleAPIactionsEndpoints;
import databases.TblsData;
import static functionaljavaa.audit.SampleAudit.sampleAuditRevisionPassByAction;
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
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class EnvMonSampleAPI extends HttpServlet {
    
    
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    
    public enum EnvMonSampleAPIactionsEndpoints implements EnumIntEndpoints{
        LOGSAMPLE("LOGSAMPLE", "sampleLogged_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_TEMPLATE, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_LOCATION_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NUM_SAMPLES_TO_LOG, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 12)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
        ),
        ENTERRESULT("ENTERRESULT", "enterResult_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build()
        ),
        REENTERRESULT("REENTERRESULT", "reEnterResult_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build()
        ),        
        ENTER_PLATE_READING("ENTER_PLATE_READING", "enterPlateReading_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build()
        ),
        REENTER_PLATE_READING("REENTER_PLATE_READING", "reEnterPlateReading_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build()
        ),
        ENTER_PLATE_READING_SECONDENTRY("ENTER_PLATE_READING_SECONDENTRY", "enterPlateReadingSecondEntry_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build()
        ),
        REENTER_PLATE_READING_SECONDENTRY("REENTER_PLATE_READING_SECONDENTRY", "reEnterPlateReadingSecondEntry_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build()
        ),                
        ADD_SAMPLE_MICROORGANISM("ADD_SAMPLE_MICROORGANISM", "MigroorganismAdded_success",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISM_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments("numItems", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 8)},                
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
        ),
        ADD_ADHOC_SAMPLE_MICROORGANISM("ADD_ADHOC_SAMPLE_MICROORGANISM", "MigroorganismAdded_success",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISM_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments("numItems", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 8)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
        ),
        REMOVE_SAMPLE_MICROORGANISM("REMOVE_SAMPLE_MICROORGANISM", "MigroorganismRemoved_success",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISM_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments("numItems", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 8)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
        ),
        EM_BATCH_INCUB_ADD_SMP("EM_BATCH_INCUB_ADD_SMP", "batchIncubator_sampleAdded_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_ROW, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_COL, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_OVERRIDE, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 12)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName()).build()).build()
        ),
        EM_BATCH_INCUB_MOVE_SMP("EM_BATCH_INCUB_MOVE_SMP", "batchIncubator_sampleMoved_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_ROW, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_COL, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_OVERRIDE, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 12)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName()).build()).build()
        ),
        EM_BATCH_INCUB_REMOVE_SMP("EM_BATCH_INCUB_REMOVE_SMP", "batchIncubator_sampleRemoved_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName()).build()).build()
        ),
        SINGLE_SAMPLE_INCUB_START("SINGLE_SAMPLE_INCUB_START", "SampleIncubationStartedSuccessfully",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
        ),
        SINGLE_SAMPLE_INCUB_END("SINGLE_SAMPLE_INCUB_END", "SampleIncubationEndedSuccessfully",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
        ),        
        ASSIGN_SAMPLE_CULTURE_MEDIA("ASSIGN_SAMPLE_CULTURE_MEDIA", "CultureMediaAssigned_success",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments("inventoryTrackinProcInstanceName", LPAPIArguments.ArgumentType.STRING.toString(), true, 7 ),
                new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 8 ),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), true, 9 ),
                new LPAPIArguments("referenceLot", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 10),
                new LPAPIArguments("useOpenReferenceLot", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 11)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
        ),
        
        ;      
        private EnvMonSampleAPIactionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums; 
            this.outputObjectTypes=outputObjectTypes;            
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex, Integer auditReasonPosic){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }  
            if (auditReasonPosic!=-1)
                request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE, getAttributeValue(contentLine[lineIndex][auditReasonPosic], contentLine));
            hm.put(request, argValues);            
            return hm;
        }
        @Override        public String getName(){return this.name;}
        @Override        public String getSuccessMessageCode(){return this.successMessageCode;}           
        @Override        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        @Override        public LPAPIArguments[] getArguments() {return arguments;}
        @Override        public String getApiUrl(){return ApiUrls.SAMPLES_ACTIONS.getUrl();}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }

    public static final String PARAMETER_PROGRAM_SAMPLE_TEMPLATE="sampleTemplate";
    public static final String PARAMETER_PROGRAM_SMP_TMP_VERSION="sampleTemplateVersion";       
    public static final String PARAMETER_NUM_SAMPLES_TO_LOG="numSamplesToLog";
    public static final String PARAMETER_PROGRAM_FIELD_NAME="fieldName";
    public static final String PARAMETER_PROGRAM_FIELD_VALUE="fieldValue";    
    public static final String PARAMTR_PROGRM_SMP_PROGRM_FLD="programName"; 
    public static final String TABLE_SAMPLE_PROGRAM_FIELD="program"; 
  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        String actionName = (String) request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        if (actionName==null || actionName.length()==0)
            actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String language = LPFrontEnd.setLanguage(request); 
        EnvMonSampleAPIactionsEndpoints endPoint = null;
        try{
            endPoint = EnvMonSampleAPIactionsEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            SampleAPIactionsEndpoints endPointSmp = null;
            try{
                endPointSmp = SampleAPIactionsEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception er){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                return;                   
            }                
            ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActionsWithEndpoint(request, response, endPoint, false);
            if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
                return;
            }
            ClassSample clssSmp=new ClassSample(request, endPointSmp);
            if (Boolean.TRUE.equals(clssSmp.getEndpointExists())){
                Object[] diagnostic=clssSmp.getDiagnostic();
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clssSmp.getMessageDynamicData());           
                }else{
                    JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPointSmp, clssSmp.getMessageDynamicData(), clssSmp.getRelatedObj().getRelatedObject());                
                    LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
                } 
            }                
        }        
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActionsWithEndpoint(request, response, endPoint, false);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        actionName=procReqInstance.getActionName();
        language=procReqInstance.getLanguage();
        String procInstanceName = procReqInstance.getProcedureInstance();
        
        String[] errObject = new String[]{"Servlet programAPI at " + request.getServletPath()};   

        String sampleIdStr=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID);
        Integer sampleId=0;
        if (sampleIdStr!=null && sampleIdStr.length()>0) sampleId=Integer.valueOf(sampleIdStr);
        String testIdStr=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TEST_ID);
        Integer testId=0;
        if (testIdStr!=null && testIdStr.length()>0) testId=Integer.valueOf(testIdStr);
        String resultIdStr=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID);
        Integer resultId=0;
        if (resultIdStr!=null && resultIdStr.length()>0) sampleId=Integer.valueOf(resultIdStr);

        Object[] sampleAuditRevision=sampleAuditRevisionPassByAction(procInstanceName, actionName, sampleId, testId, resultId);     
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAuditRevision[0].toString())){  
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, sampleAuditRevision);
            return;                             
        }  
        try (PrintWriter out = response.getWriter()) {

            Object[] areMandatoryParamsInResponse=new Object[]{};
            if (endPoint!=null && endPoint.getArguments()!=null)
                areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }                            
            ClassEnvMonSample clss=new ClassEnvMonSample(request, endPoint);
            if (Boolean.TRUE.equals(clss.getEndpointExists())){
                Object[] diagnostic=clss.getDiagnostic();
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                    String errorCode =diagnostic[4].toString();
                    Object[] msgVariables=clss.getMessageDynamicData();
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, errorCode, msgVariables);               
//                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, diagnostic);   
                }else{
                    JSONObject dataSampleJSONMsg =new JSONObject();
                    if (endPoint!=null)
                        dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, clss.getMessageDynamicData(), clss.getRelatedObj().getRelatedObject());                
                    
                    LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
                }            
            }else{
                SampleAPIactionsEndpoints endPointSmp = null;
                try{
                    endPointSmp = SampleAPIactionsEndpoints.valueOf(actionName.toUpperCase());
                }catch(Exception e){
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                    return;                   
                }                
                ClassSample clssSmp=new ClassSample(request, endPointSmp);
                if (Boolean.TRUE.equals(clssSmp.getEndpointExists())){
                    Object[] diagnostic=clssSmp.getDiagnostic();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, diagnostic);   
                    }else{
                        JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, clssSmp.getMessageDynamicData(), clssSmp.getRelatedObj().getRelatedObject());                
                        LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
                    } 
                }
            }
        }catch(Exception e){   
            procReqInstance.killIt();
            errObject = new String[]{e.getMessage()};
            LPFrontEnd.responseError(errObject, language, null);
        } finally {
            // release database resources
            try {
                procReqInstance.killIt();
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
        } catch (IOException | ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
        } catch (IOException | ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
