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
import com.labplanet.servicios.modulesample.SampleAPI;
import com.labplanet.servicios.modulesample.SampleAPIParams.SampleAPIEndpoints;
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
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class EnvMonSampleAPI extends HttpServlet {
    
    
    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    
    public enum EnvMonSampleAPIEndpoints{
        /**
         *
         */
        LOGSAMPLE("LOGSAMPLE", "sampleLogged_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_TEMPLATE, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_LOCATION_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NUM_SAMPLES_TO_LOG, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 12)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.Sample.TBL.getName()).build()).build()
        ),
        ENTERRESULT("ENTERRESULT", "enterResult_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.Sample.TBL.getName()).build())
                .add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.SampleAnalysisResult.TBL.getName()).build()).build()
        ),
        REENTERRESULT("REENTERRESULT", "reEnterResult_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.Sample.TBL.getName()).build())
                .add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.SampleAnalysisResult.TBL.getName()).build()).build()
        ),
/*        PLATE_READING_NUMBER("PLATE_READING_NUMBER", "plateReadingNumber_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.Sample.TBL.getName()).build())
                .add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.SampleAnalysisResult.TBL.getName()).build()).build()
        ),
*/        
        ADD_SAMPLE_MICROORGANISM("ADD_SAMPLE_MICROORGANISM", "MigroorganismAdded_success",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISM_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.Sample.TBL.getName()).build()).build()
        ),
        ADD_ADHOC_SAMPLE_MICROORGANISM("ADD_ADHOC_SAMPLE_MICROORGANISM", "MigroorganismAdded_success",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISM_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.Sample.TBL.getName()).build()).build()
        ),
        REMOVE_SAMPLE_MICROORGANISM("REMOVE_SAMPLE_MICROORGANISM", "MigroorganismRemoved_success",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISM_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.Sample.TBL.getName()).build()).build()
        ),
        EM_BATCH_INCUB_ADD_SMP("EM_BATCH_INCUB_ADD_SMP", "batchIncubator_sampleAdded_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_ROW, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_COL, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_OVERRIDE, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 12)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.Sample.TBL.getName()).build())
                .add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsEnvMonitData.IncubBatch.TBL.getName()).build()).build()
        ),
        EM_BATCH_INCUB_MOVE_SMP("EM_BATCH_INCUB_MOVE_SMP", "batchIncubator_sampleMoved_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_ROW, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_COL, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_OVERRIDE, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 12)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.Sample.TBL.getName()).build())
                .add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsEnvMonitData.IncubBatch.TBL.getName()).build()).build()
        ),
        EM_BATCH_INCUB_REMOVE_SMP("EM_BATCH_INCUB_REMOVE_SMP", "batchIncubator_sampleRemoved_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.Sample.TBL.getName()).build())
                .add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsEnvMonitData.IncubBatch.TBL.getName()).build()).build()
        )
        ;      
        private EnvMonSampleAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
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
        public String getName(){return this.name;}
        public String getSuccessMessageCode(){return this.successMessageCode;}           
        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        public LPAPIArguments[] getArguments() {return arguments;}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }

    /**
     *
     */
    public static final String PARAMETER_PROGRAM_SAMPLE_TEMPLATE="sampleTemplate";

    /**
     *
     */
    public static final String PARAMETER_PROGRAM_SAMPLE_TEMPLATE_VERSION="sampleTemplateVersion";       

    /**
     *
     */
    public static final String PARAMETER_NUM_SAMPLES_TO_LOG="numSamplesToLog";

    /**
     *
     */
    public static final String PARAMETER_PROGRAM_FIELD_NAME="fieldName";

    /**
     *
     */
    public static final String PARAMETER_PROGRAM_FIELD_VALUE="fieldValue";    

    /**
     *
     */
    public static final String PARAMETER_PROGRAM_SAMPLE_PROGRAM_FIELD="programName"; 
    
    /**
     *
     */
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

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
        if (procReqInstance.getHasErrors()){
//            procReqInstance.killIt();
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
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

//        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}
        
        Object[] sampleAuditRevision=sampleAuditRevisionPassByAction(procInstanceName, actionName, sampleId, testId, resultId);     
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAuditRevision[0].toString())){  
//            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, sampleAuditRevision);
            //LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.INVALID_TOKEN.getName(), null, language);              
            return;                             
        }  
//        Connection con = Rdbms.createTransactionWithSavePoint();        
 /*       if (con==null){
             response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The Transaction cannot be created, the action should be aborted");
             return;
        }
*/        
/*        try {
            con.rollback();
            con.setAutoCommit(true);    
        } catch (SQLException ex) {
            Logger.getLogger(EnvMonAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
*/                    
/*        try {
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(sampleAPI.class.getName()).log(Level.SEVERE, null, ex);
        }*/

//        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());    
//        Rdbms.setTransactionId(schemaConfigName);
        try (PrintWriter out = response.getWriter()) {
            EnvMonSampleAPIEndpoints endPoint = null;
            try{
                endPoint = EnvMonSampleAPIEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                SampleAPIEndpoints endPointSmp = null;
                try{
                    endPointSmp = SampleAPIEndpoints.valueOf(actionName.toUpperCase());
                }catch(Exception er){
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
                    return;                   
                }                
                ClassSample clssSmp=new ClassSample(request, endPointSmp);
                if (clssSmp.getEndpointExists()){
                    Object[] diagnostic=clssSmp.getDiagnostic();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
        /*                Rdbms.rollbackWithSavePoint();
                        if (!con.getAutoCommit()){
                            con.rollback();
                            con.setAutoCommit(true);}                */
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clssSmp.getMessageDynamicData());           
                    }else{
                        JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(SampleAPI.class.getSimpleName(), endPointSmp.getSuccessMessageCode(), clssSmp.getMessageDynamicData(), clssSmp.getRelatedObj().getRelatedObject());                
                        LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
                    } 
                }                
            }
            Object[] areMandatoryParamsInResponse=new Object[]{};
            if (endPoint!=null && endPoint.getArguments()!=null)
                areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);
                return;
            }                            
            ClassEnvMonSample clss=new ClassEnvMonSample(request, endPoint);
            if (clss.getEndpointExists()){
                Object[] diagnostic=clss.getDiagnostic();
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
    /*                Rdbms.rollbackWithSavePoint();
                    if (!con.getAutoCommit()){
                        con.rollback();
                        con.setAutoCommit(true);}                */     
                    String errorCode =diagnostic[4].toString();
                    Object[] msgVariables=clss.getMessageDynamicData();
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, errorCode, msgVariables);               
//                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, diagnostic);   
                }else{
                    JSONObject dataSampleJSONMsg =new JSONObject();
                    if (endPoint!=null)
                        dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), clss.getMessageDynamicData(), clss.getRelatedObj().getRelatedObject());                
                    
                    LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
                }            
            }else{
                SampleAPIEndpoints endPointSmp = null;
                try{
                    endPointSmp = SampleAPIEndpoints.valueOf(actionName.toUpperCase());
                }catch(Exception e){
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
                    return;                   
                }                
                ClassSample clssSmp=new ClassSample(request, endPointSmp);
                if (clssSmp.getEndpointExists()){
                    Object[] diagnostic=clssSmp.getDiagnostic();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
        //                Rdbms.rollbackWithSavePoint();
        //                if (!con.getAutoCommit()){
        //                    con.rollback();
        //                    con.setAutoCommit(true);}                
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, diagnostic);   
                    }else{
                        JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), clssSmp.getMessageDynamicData(), clssSmp.getRelatedObj().getRelatedObject());                
                        LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
                    } 
                }
            }
        }catch(Exception e){   
 /*           try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(sampleAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
*/            
            procReqInstance.killIt();
            errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
        } finally {
            // release database resources
            try {
//                con.close();
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
