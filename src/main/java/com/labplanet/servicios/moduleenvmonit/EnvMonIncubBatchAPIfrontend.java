/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import databases.TblsData;
import databases.Token;
import functionaljavaa.instruments.incubator.DataIncubatorNoteBook;
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
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import lbplanet.utilities.LPAPIArguments;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class EnvMonIncubBatchAPIfrontend extends HttpServlet {
    
    public enum EnvMonIncubBatchAPIfrontendEndpoints implements EnumIntEndpoints{
        ACTIVE_BATCH_LIST("ACTIVE_BATCH_LIST", "", 
            new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8) 
            }, EndPointsToRequirements.endpointWithNoOutputObjects)        
        ;
        private EnvMonIncubBatchAPIfrontendEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums; 
            this.outputObjectTypes=outputObjectTypes;            
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
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

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
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                                         
            switch (endPoint){
            case ACTIVE_BATCH_LIST: 
                String[] fieldsToRetrieve=new String[]{};
                String fieldsRetrieveStr = argValues[0].toString(); 
                if (fieldsRetrieveStr.length()==0 || "ALL".equalsIgnoreCase(fieldsRetrieveStr))
                    fieldsToRetrieve=EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableFields());
                else
                    fieldsToRetrieve=fieldsRetrieveStr.split("\\|");
                String[] whereFieldsNameArr = null;
                Object[] whereFieldsValueArr = null;
                String whereFieldsName = argValues[1].toString(); 
                if (whereFieldsName==null){whereFieldsName="";}
                String whereFieldsValue = argValues[2].toString();
                if (whereFieldsValue==null){whereFieldsValue="";}
                
                if (whereFieldsName.length()>0)
                    whereFieldsNameArr=LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
                else
                    whereFieldsNameArr=new String[]{TblsEnvMonitData.IncubBatch.ACTIVE.getName()};
                if (whereFieldsValue.length()>0)
                    whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|")));                                          
                else
                    whereFieldsValueArr=new Object[]{true};
                for (int iFields=0; iFields<whereFieldsNameArr.length; iFields++){
                    if (LPPlatform.isEncryptedField(procInstanceName, GlobalVariables.Schemas.DATA.getName(), TblsData.TablesData.SAMPLE.getTableName(), whereFieldsNameArr[iFields])){                
                        HashMap<String, String> hm = LPPlatform.encryptEncryptableFieldsAddBoth(whereFieldsNameArr[iFields], whereFieldsNameArr[iFields]);
                        whereFieldsNameArr[iFields]= hm.keySet().iterator().next();    
                        if ( hm.get(whereFieldsNameArr[iFields]).length()!=whereFieldsNameArr[iFields].length()){
                            String newWhereFieldValues = hm.get(whereFieldsNameArr[iFields]);
                            whereFieldsValueArr[iFields]=newWhereFieldValues;
                        }
                    }
                    String[] tokenFieldValue = Token.getTokenFieldValue(whereFieldsValueArr[iFields].toString(), procReqInstance.getTokenString());
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(tokenFieldValue[0])) 
                        whereFieldsValueArr[iFields]=tokenFieldValue[1];                                                    
                } 
                if (!LPArray.valueInArray(whereFieldsNameArr, TblsEnvMonitData.IncubBatch.ACTIVE.getName())){
                    whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, TblsEnvMonitData.IncubBatch.ACTIVE.getName());
                    whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, true);
                }
                JSONArray jArr=getActiveBatchData(fieldsToRetrieve, whereFieldsNameArr, whereFieldsValueArr);
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                procReqInstance.killIt();       
                break;        
            default:      
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language);                                                                  
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
    
    
    public static JSONArray getActiveBatchData(String[] fieldsToRetrieve, String[] whereFieldsNameArr, Object[] whereFieldsValueArr){
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false);  
        String procInstanceName= procReqInstance.getProcedureInstance();
        Object[][] activeBatchesList=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), 
                whereFieldsNameArr, whereFieldsValueArr, 
                fieldsToRetrieve, new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()});
        JSONArray jArr = new JSONArray();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(activeBatchesList[0][0].toString())) return jArr;
        for (Object[] currBatch: activeBatchesList){
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currBatch);
            Integer incubPosic=LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitData.IncubBatch.INCUBATION_INCUBATOR.getName());
            JSONArray instrLast10ReadingsjArr = new JSONArray();
            if (incubPosic>-1 && currBatch[incubPosic].toString().length()>0){
                String[] incubatorFldsToRetrieve=new String[]{TblsEnvMonitConfig.InstrIncubator.LOCKED.getName(), TblsEnvMonitConfig.InstrIncubator.LOCKED_REASON.getName()};
                whereFieldsNameArr=new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()};
                whereFieldsValueArr=new Object[]{currBatch[incubPosic].toString()};
                Object[][] instrIncubatorInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), 
                        whereFieldsNameArr, whereFieldsValueArr, incubatorFldsToRetrieve);
                String[] tempReadingFldsToRetrieve=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.ID.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_BY.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.TEMPERATURE.getName()};
                if (procReqInstance.getProcedureInstance()==null)
                    ProcedureRequestSession.getInstanceForQueries(null, null, false);
                Object[][] instrReadings = DataIncubatorNoteBook.getLastTemperatureReading(currBatch[incubPosic].toString(), 10, tempReadingFldsToRetrieve);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrReadings[0][0].toString())){
                    ProcedureRequestSession.getInstanceForQueries(null, null, false);
                    instrReadings = DataIncubatorNoteBook.getLastTemperatureReading(currBatch[incubPosic].toString(), 10, tempReadingFldsToRetrieve);
                }
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrIncubatorInfo[0][0].toString())){
                    for (int i=0;i<incubatorFldsToRetrieve.length;i++){
                        jObj.put("incubator_info_"+incubatorFldsToRetrieve[i], instrIncubatorInfo[0][i]);                            
                    }
                }
                for (String curTempReadingFld: tempReadingFldsToRetrieve){
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrReadings[0][0].toString())){
                        jObj.put("incubator_info_"+curTempReadingFld, "no_data");                            
                    }else{
                        jObj.put("incubator_info_"+curTempReadingFld, LPNulls.replaceNull(instrReadings[0][LPArray.valuePosicInArray(tempReadingFldsToRetrieve, curTempReadingFld)]).toString());
                    }   
                }                            
                instrLast10ReadingsjArr = new JSONArray();
                for (Object[] curLastReading: instrReadings){
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrReadings[0][0].toString())){
                        JSONObject curLastReadingjObj=LPJson.convertArrayRowToJSONObject(tempReadingFldsToRetrieve, curLastReading);
                        instrLast10ReadingsjArr.add(curLastReadingjObj);
                    }
                }
            }                    
            jObj.put("incubator_last_temp_readings", instrLast10ReadingsjArr);
            Object[] incubBatchContentInfo=incubBatchContentJson(fieldsToRetrieve, currBatch);
            jObj.put("SAMPLES_ARRAY", incubBatchContentInfo[0]);
            jObj.put("NUM_SAMPLES", incubBatchContentInfo[1]);                 
            jArr.add(jObj);
        }
        return jArr;
    }
    
    public static Object[] incubBatchContentJson(String[] batchFields, Object[] batchValues){
        if (BatchIncubatorType.UNSTRUCTURED.toString().equalsIgnoreCase(batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.TYPE.getName())].toString())){
            String unstructuredContent=LPNulls.replaceNull((String)batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.UNSTRUCT_CONTENT.getName())]);
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
        }else if (BatchIncubatorType.STRUCTURED.toString().equalsIgnoreCase(batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.TYPE.getName())].toString())){
            Integer totalRows=(Integer)batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.STRUCT_NUM_ROWS.getName())]; 
            Integer totalCols=(Integer)batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.STRUCT_NUM_COLS.getName())]; 
            String[] rowsName=batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.STRUCT_ROWS_NAME.getName())].toString().split(DataBatchIncubatorStructured.BATCHCONTENTSEPARATORSTRUCTUREDBATCH);
            String[] colsName=batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.STRUCT_COLS_NAME.getName())].toString().split(DataBatchIncubatorStructured.BATCHCONTENTSEPARATORSTRUCTUREDBATCH);
            String[] batchContent1D=batchValues[LPArray.valuePosicInArray(batchFields, TblsEnvMonitData.IncubBatch.STRUCT_CONTENT.getName())].toString().split(BATCHCONTENTSEPARATORSTRUCTUREDBATCH);
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
