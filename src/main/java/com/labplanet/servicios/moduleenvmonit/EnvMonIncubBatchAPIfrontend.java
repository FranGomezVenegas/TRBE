/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import functionaljavaa.inventory.batch.DataBatchIncubator.*;
import functionaljavaa.inventory.batch.DataBatchIncubatorStructured;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import static functionaljavaa.inventory.batch.DataBatchIncubatorStructured.BATCHCONTENTSEPARATORSTRUCTUREDBATCH;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPAPIArguments;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class EnvMonIncubBatchAPIfrontend extends HttpServlet {
    
    public enum EnvMonIncubBatchAPIfrontendEndpoints{
        ACTIVE_BATCH_LIST("ACTIVE_BATCH_LIST", "", new LPAPIArguments[]{}),
        ;
        private EnvMonIncubBatchAPIfrontendEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN;

    /**
     *
     * @param request the request info
     * @param response the response to the request
     * @throws ServletException in case something not handled happen
     * @throws IOException issues with the message
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);        
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }
        String actionName=procReqInstance.getActionName();        
        String language=procReqInstance.getLanguage();
        String procInstanceName = procReqInstance.getProcedureInstance();

        try (PrintWriter out = response.getWriter()) {

            EnvMonIncubBatchAPIfrontendEndpoints endPoint = null;
            try{
                endPoint = EnvMonIncubBatchAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            switch (endPoint){
            case ACTIVE_BATCH_LIST: 
                String[] fieldsToRetrieve=new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName(), TblsEnvMonitData.IncubBatch.FLD_TYPE.getName()
                    , TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_VERSION.getName()
                    , TblsEnvMonitData.IncubBatch.FLD_INCUBATION_INCUBATOR.getName(), TblsEnvMonitData.IncubBatch.FLD_UNSTRUCT_CONTENT.getName()
                    , TblsEnvMonitData.IncubBatch.FLD_INCUBATION_START.getName(), TblsEnvMonitData.IncubBatch.FLD_INCUBATION_END.getName()
                    , TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_ROWS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_COLS.getName()
                    , TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_POSITIONS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName()
                    , TblsEnvMonitData.IncubBatch.FLD_STRUCT_ROWS_NAME.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_COLS_NAME.getName() 
                    , TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
                Object[][] activeBatchesList=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                        new String[]{TblsEnvMonitData.IncubBatch.FLD_ACTIVE.getName()}, new Object[]{true}, 
                        fieldsToRetrieve, new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()});
                JSONArray jArr = new JSONArray();
                for (Object[] currBatch: activeBatchesList){
                    JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currBatch);

                    Object[] incubBatchContentInfo=incubBatchContentJson(fieldsToRetrieve, currBatch);
                    jObj.put("SAMPLES_ARRAY", incubBatchContentInfo[0]);
                    jObj.put("NUM_SAMPLES", incubBatchContentInfo[1]);                 
                    jArr.add(jObj);
                }
                procReqInstance.killIt();              
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                break;        
            default:      
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);                                                                  
        }
    }catch(Exception e){      
        procReqInstance.killIt();
        String exceptionMessage =e.getMessage();
        if (exceptionMessage==null){exceptionMessage="null exception";}
        response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
        LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null);      
    } finally {
       // release database resources
       try {
            procReqInstance.killIt();
        } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
       }
    }              
    }
    
    public static Object[] incubBatchContentJson(String[] batchFields, Object[] batchValues){
        if (BatchIncubatorType.UNSTRUCTURED.toString().equalsIgnoreCase(batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.FLD_TYPE.getName())].toString())){
            String unstructuredContent=LPNulls.replaceNull((String)batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.FLD_UNSTRUCT_CONTENT.getName())]);
            if (unstructuredContent!=null && unstructuredContent.length()>0){ 
                String fieldsSeparator="\\*";
                String[] fieldsTag = new String[]{"sample_id", "incubation_moment"};
                String[] samplesArr = unstructuredContent.split("\\|");
                JSONArray jbatchSamplesArr = new JSONArray();
                for (String currSample: samplesArr){
                    String[] currSampleArr=currSample.split(fieldsSeparator);
                    JSONObject jReadingsObj=LPJson.convertArrayRowToJSONObject(fieldsTag, currSampleArr);
                    jbatchSamplesArr.add(jReadingsObj);
                }
                return new Object[]{jbatchSamplesArr, samplesArr.length};
                
            }
        }else if (BatchIncubatorType.STRUCTURED.toString().equalsIgnoreCase(batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.FLD_TYPE.getName())].toString())){
            Integer totalRows=(Integer)batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_ROWS.getName())]; 
            Integer totalCols=(Integer)batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_COLS.getName())]; 
            String[] rowsName=batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.FLD_STRUCT_ROWS_NAME.getName())].toString().split(DataBatchIncubatorStructured.BATCHCONTENTSEPARATORSTRUCTUREDBATCH);
            String[] colsName=batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.FLD_STRUCT_COLS_NAME.getName())].toString().split(DataBatchIncubatorStructured.BATCHCONTENTSEPARATORSTRUCTUREDBATCH);
            String[] batchContent1D=batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName())].toString().split(BATCHCONTENTSEPARATORSTRUCTUREDBATCH);
            String[][] batchContent2D=LPArray.array1dTo2d(batchContent1D, totalCols);

            if (batchContent2D.length==0) return new Object[]{new JSONArray(), ""};  
            JSONArray jbatchSamplesArr = new JSONArray();
            for (int x=0;x<totalRows;x++){
                for (int y=0;y<totalCols;y++){
                    JSONObject posicObj=new JSONObject();
                    posicObj.put("x", x+1);
                    posicObj.put("y", y+1);
                    posicObj.put("posic name", rowsName[x]+colsName[y]);
                    posicObj.put("content", batchContent2D[x][y]);
                    jbatchSamplesArr.add(posicObj);
                }
            }
                return new Object[]{jbatchSamplesArr, ""};            
        }       
        return new Object[]{new JSONArray(), ""};        
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
