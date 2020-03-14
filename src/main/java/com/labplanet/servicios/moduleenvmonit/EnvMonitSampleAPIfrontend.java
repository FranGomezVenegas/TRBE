/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.JSON_TAG_NAME_SAMPLE_RESULTS;
import static com.labplanet.servicios.moduleenvmonit.EnvMonitAPIParams.*;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData.ViewSampleMicroorganismList;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import databases.Rdbms;
import databases.TblsCnfg;
import databases.TblsData;
import databases.Token;
import functionaljavaa.instruments.incubator.DataIncubatorNoteBook;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import functionaljavaa.samplestructure.DataSampleStages;
import static lbplanet.utilities.LPDate.dateStringFormatToLocalDateTime;
import lbplanet.utilities.LPNulls;

/**
 *
 * @author Administrator
 */
public class EnvMonitSampleAPIfrontend extends HttpServlet {

    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET="actionName|finalToken";

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
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        String language = LPFrontEnd.setLanguage(request); 


        try (PrintWriter out = response.getWriter()) {
            
            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                return;          
            }             
            String schemaPrefix = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX);            
            String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
           
           // Token token = new Token(finalToken);

            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}              
            
            switch (actionName.toUpperCase()){
                case API_ENDPOINT_GET_MICROORGANISM_LIST:
                  String[] fieldsToRetrieve=new String[]{TblsEnvMonitConfig.MicroOrganism.FLD_NAME.getName()};
                  Object[][] list = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.MicroOrganism.TBL.getName(), 
                          new String[]{TblsEnvMonitConfig.MicroOrganism.FLD_NAME.getName()+" is not null"}, new Object[]{}
                          , fieldsToRetrieve, fieldsToRetrieve);
                  JSONArray jArr=new JSONArray();
                  for (Object[] curRec: list){
                    JSONObject jObj= LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRec);
                    jArr.add(jObj);
                  }
                  LPFrontEnd.servletReturnSuccess(request, response, jArr);
                  return;  
                case API_ENDPOINT_GET_SAMPLE_MICROORGANISM_VIEW:
                    String whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME); 
                    if (whereFieldsName==null){whereFieldsName="";}
                    String whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE); 
                    String[] whereFieldsNameArr=new String[0];
                    Object[] whereFieldsValueArr=new Object[0];
                    if ( (whereFieldsName!=null) && (whereFieldsValue!=null) ){
                        whereFieldsNameArr=LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
                        whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|")));                                          
                        for (int iFields=0; iFields<whereFieldsNameArr.length; iFields++){
                            if (LPPlatform.isEncryptedField(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsData.Sample.TBL.getName(), whereFieldsNameArr[iFields])){                
                                HashMap<String, String> hm = LPPlatform.encryptEncryptableFieldsAddBoth(whereFieldsNameArr[iFields], whereFieldsValueArr[iFields].toString());
                                whereFieldsNameArr[iFields]= hm.keySet().iterator().next();    
                                if ( hm.get(whereFieldsNameArr[iFields]).length()!=whereFieldsNameArr[iFields].length()){
                                    String newWhereFieldValues = hm.get(whereFieldsNameArr[iFields]);
                                    whereFieldsValueArr[iFields]=newWhereFieldValues;
                                }
                            }
                            String[] tokenFieldValue = Token.getTokenFieldValue(whereFieldsValueArr[iFields].toString(), finalToken);
                            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(tokenFieldValue[0])) 
                                whereFieldsValueArr[iFields]=tokenFieldValue[1];                                                    
                        }                                    
                    }            
                    whereFieldsNameArr=LPArray.addValueToArray1D(whereFieldsNameArr, new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.FLD_CURRENT_STAGE.getName(), TblsEnvMonitData.ViewSampleMicroorganismList.FLD_RAW_VALUE.getName()+" is not null"});
                    whereFieldsValueArr=LPArray.addValueToArray1D(whereFieldsValueArr, new Object[]{"MicroorganismIdentification"});
                    ViewSampleMicroorganismList[] fieldsList = TblsEnvMonitData.ViewSampleMicroorganismList.values();
                    fieldsToRetrieve = new String[0];
                    for (ViewSampleMicroorganismList fieldsList1 : fieldsList) {
                      if (!"TBL".equalsIgnoreCase(fieldsList1.name())) {
                        fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fieldsList1.getName());
                      }                  
                    }
                    list = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.ViewSampleMicroorganismList.TBL.getName(), 
                            whereFieldsNameArr, whereFieldsValueArr
                            , fieldsToRetrieve
                            , new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.FLD_SAMPLE_ID.getName()} );
                    jArr=new JSONArray();
                    for (Object[] curRec: list){
                      JSONObject jObj= LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRec);
                      jArr.add(jObj);
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return; 
                case API_ENDPOINT_GET_SAMPLE_STAGES_SUMMARY_REPORT:
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, EnvMonitAPIParams.MANDATORY_PARAMS_GET_SAMPLE_STAGES_SUMMARY_REPORT.split("\\|"));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                        LPFrontEnd.servletReturnResponseError(request, response, 
                                LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                        return;                  
                    }                                                              
                    String sampleIdStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID);
                    String sampleToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE);
                    String[] sampleToRetrieveArr=new String[0];
                    if ((sampleToRetrieve!=null) && (sampleToRetrieve.length()>0))
                        if ("ALL".equalsIgnoreCase(sampleToRetrieve)) sampleToRetrieveArr=TblsEnvMonitData.Sample.getAllFieldNames();
                        else sampleToRetrieveArr=sampleToRetrieve.split("\\|");
                    sampleToRetrieveArr=LPArray.addValueToArray1D(sampleToRetrieveArr, TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName());
                    String sampleToDisplay = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_DISPLAY);
                    String[] sampleToDisplayArr=new String[0];
                    if ((sampleToDisplay!=null) && (sampleToDisplay.length()>0))
                        if ("ALL".equalsIgnoreCase(sampleToDisplay)) sampleToDisplayArr=TblsEnvMonitData.Sample.getAllFieldNames();
                        else sampleToDisplayArr=sampleToDisplay.split("\\|");

                    String[] sampleTblAllFields=TblsEnvMonitData.Sample.getAllFieldNames();
                    Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.Sample.TBL.getName(), 
                            new String[]{TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{Integer.valueOf(sampleIdStr)}, 
                            sampleTblAllFields);                    
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())){
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Error on getting sample <*1*> in procedure <*2*>", new Object[]{Arrays.toString(sampleInfo[0]), schemaPrefix}));              
                        return;}  
                    JSONObject jObjSampleInfo=new JSONObject();
                    JSONObject jObjMainObject=new JSONObject();
                    JSONObject jObjPieceOfInfo=new JSONObject();
                    JSONArray jArrPieceOfInfo=new JSONArray();
                    JSONArray jArrMainPieceOfInfo=new JSONArray();
                    for (int iFlds=0;iFlds<sampleInfo[0].length;iFlds++){                      
                        if (LPArray.valueInArray(sampleToRetrieveArr, sampleTblAllFields[iFlds]))
                            jObjSampleInfo.put(sampleTblAllFields[iFlds], sampleInfo[0][iFlds].toString());
                    }
                    for (int iFlds=0;iFlds<sampleToDisplayArr.length;iFlds++){                      
                        if (LPArray.valueInArray(sampleTblAllFields, sampleToDisplayArr[iFlds])){
                            jObjPieceOfInfo=new JSONObject();
                            jObjPieceOfInfo.put("field_name", sampleToDisplayArr[iFlds]);
                            jObjPieceOfInfo.put("field_value", sampleInfo[0][LPArray.valuePosicInArray(sampleTblAllFields, sampleToDisplayArr[iFlds])].toString());
                            jArrPieceOfInfo.add(jObjPieceOfInfo);
                        }
                    }

                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, jObjSampleInfo);
                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_DISPLAY, jArrPieceOfInfo);
                    
                    JSONArray jArrMainObj=new JSONArray();
                    jObjPieceOfInfo=new JSONObject();
                    DataSampleStages smpStage= new DataSampleStages(schemaPrefix);
                    String[] sampleStageTimingCaptureAllFlds=TblsEnvMonitProcedure.SampleStageTimingCapture.getAllFieldNames();
                    JSONObject jObjMainObject2=new JSONObject();                    
                    
                    if (smpStage.isSampleStagesEnable()){
                        Object[][] sampleStageInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_PROCEDURE), TblsEnvMonitProcedure.SampleStageTimingCapture.TBL.getName(), 
                                new String[]{TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_SAMPLE_ID.getName()}, new Object[]{Integer.valueOf(sampleIdStr)}, 
                                sampleStageTimingCaptureAllFlds, new String[]{TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_ID.getName()});                    
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleStageInfo[0][0].toString())){
                            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Error on getting sample <*1*> in procedure <*2*>", new Object[]{Arrays.toString(sampleInfo[0]), schemaPrefix}));              
                            return;}  
                        for (Object[] curRec: sampleStageInfo){
                            JSONObject jObj= LPJson.convertArrayRowToJSONObject(sampleStageTimingCaptureAllFlds, curRec);
                            JSONArray jArrMainObj2=new JSONArray();
                            jArrMainObj2=sampleStageDataJsonArr(schemaPrefix, Integer.valueOf(sampleIdStr), sampleTblAllFields, sampleInfo[0], sampleStageTimingCaptureAllFlds, curRec);
                            jObj.put("data", jArrMainObj2);
                            jArrMainObj.add(jObj);
                        }
                    }
                    jObjMainObject.put("stages", jArrMainObj);                    
                    LPFrontEnd.servletReturnSuccess(request, response, jObjMainObject);
                    return;
                case API_ENDPOINT_GET_BATCH_REPORT:
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, EnvMonitAPIParams.MANDATORY_PARAMS_GET_BATCH_REPORT.split("\\|"));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                        LPFrontEnd.servletReturnResponseError(request, response, 
                                LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                        return;                  
                    }                                                              
                    String batchName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME);
                    String fieldsToRetrieveStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_BATCH_FIELD_TO_RETRIEVE);
                    String fieldsToDisplayStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_BATCH_FIELD_TO_DISPLAY);
                    String[] fieldToRetrieveArr=new String[0];
                    if ((fieldsToRetrieveStr!=null) && (fieldsToRetrieveStr.length()>0))
                        if ("ALL".equalsIgnoreCase(fieldsToRetrieveStr)) fieldToRetrieveArr=TblsEnvMonitData.IncubBatch.getAllFieldNames();
                        else fieldToRetrieveArr=fieldsToRetrieveStr.split("\\|");
                    fieldToRetrieveArr=LPArray.addValueToArray1D(fieldToRetrieveArr, TblsEnvMonitData.IncubBatch.FLD_NAME.getName());
                    String[] fieldToDisplayArr=new String[0];
                    if ((fieldsToDisplayStr!=null) && (fieldsToDisplayStr.length()>0))
                        if ("ALL".equalsIgnoreCase(fieldsToDisplayStr)) fieldToDisplayArr=TblsEnvMonitData.IncubBatch.getAllFieldNames();                        
                        else fieldToDisplayArr=fieldsToDisplayStr.split("\\|");
                    String[] batchTblAllFields=TblsEnvMonitData.IncubBatch.getAllFieldNames();
                    Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                            new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, 
                            batchTblAllFields);                    
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())){
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Error on getting sample <*1*> in procedure <*2*>", new Object[]{Arrays.toString(batchInfo[0]), schemaPrefix}));              
                        return;}  
                    JSONObject jObjBatchInfo=new JSONObject();
                    jObjMainObject=new JSONObject();
                    jObjPieceOfInfo=new JSONObject();
                    jArrPieceOfInfo=new JSONArray();
                    for (int iFlds=0;iFlds<batchInfo[0].length;iFlds++){                      
                        if (LPArray.valueInArray(fieldToRetrieveArr, batchTblAllFields[iFlds]))
                            jObjBatchInfo.put(batchTblAllFields[iFlds], batchInfo[0][iFlds].toString());
                    }
                    for (int iFlds=0;iFlds<fieldToDisplayArr.length;iFlds++){                      
                        if (LPArray.valueInArray(batchTblAllFields, fieldToDisplayArr[iFlds])){
                            jObjPieceOfInfo=new JSONObject();
                            jObjPieceOfInfo.put("field_name", fieldToDisplayArr[iFlds]);
                            jObjPieceOfInfo.put("field_value", batchInfo[0][LPArray.valuePosicInArray(batchTblAllFields, fieldToDisplayArr[iFlds])].toString());
                            jArrPieceOfInfo.add(jObjPieceOfInfo);
                        }
                    }
                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_BATCH_FIELD_TO_RETRIEVE, jObjBatchInfo);
                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_BATCH_FIELD_TO_DISPLAY, jArrPieceOfInfo);

                    Object[] incubBatchContentInfo=EnvMonIncubBatchAPIfrontend.incubBatchContentJson(batchTblAllFields, batchInfo[0]);
                    jObjMainObject.put("SAMPLES_ARRAY", incubBatchContentInfo[0]);
                    jObjMainObject.put("NUM_SAMPLES", incubBatchContentInfo[1]);     
                    
                    String incubName= batchInfo[0][LPArray.valuePosicInArray(TblsEnvMonitData.IncubBatch.getAllFieldNames(), TblsEnvMonitData.IncubBatch.FLD_INCUBATION_INCUBATOR.getName())].toString();
                    Object incubStart= batchInfo[0][LPArray.valuePosicInArray(TblsEnvMonitData.IncubBatch.getAllFieldNames(), TblsEnvMonitData.IncubBatch.FLD_INCUBATION_START.getName())]; 
                    Object incubEnd= batchInfo[0][LPArray.valuePosicInArray(TblsEnvMonitData.IncubBatch.getAllFieldNames(), TblsEnvMonitData.IncubBatch.FLD_INCUBATION_END.getName())];                     
                    JSONArray jArrLastTempReadings = new JSONArray();
                    if (incubName==null || incubStart==null || incubEnd==null){
                        JSONObject jObj= new JSONObject();
                        jObj.put("error", "This is not a completed batch so temperature readings cannot be");
                        jArrLastTempReadings.add(jObj);
                    }else{
                        fieldsToRetrieve=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_ID.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(),
                                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_TEMPERATURE.getName()};   
                        Object[][] instrReadings=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), 
                                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName()+" BETWEEN "}, 
                                new Object[]{incubName, incubStart, incubEnd}, 
                                fieldsToRetrieve, new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName()});
                        if ("LABPLANET_FALSE".equalsIgnoreCase(instrReadings[0][0].toString())){
                            JSONObject jObj= new JSONObject();
                            jObj.put("error", "No temperature readings found");
                            jArrLastTempReadings.add(jObj);                            
                        }else{
                            for (Object[] currReading: instrReadings){
                                JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currReading);
                                jArrLastTempReadings.add(jObj);
                            }
                        }
                    }
                    jObjMainObject.put(GlobalAPIsParams.BATCH_REPORT_JSON_TAG_NAME_TEMP_READINGS, jArrLastTempReadings);
                    
                    LPFrontEnd.servletReturnSuccess(request, response, jObjMainObject);
                    break;                    
                case API_ENDPOINT_GET_INCUBATOR_REPORT:
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, EnvMonitAPIParams.MANDATORY_PARAMS_GET_INCUBATOR_REPORT.split("\\|"));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                        LPFrontEnd.servletReturnResponseError(request, response, 
                                LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                        return;                  
                    }                                                              
                    incubName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_INCUBATOR_NAME);
                    fieldsToRetrieveStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_INCUBATOR_FIELD_TO_RETRIEVE);
                    fieldsToDisplayStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_INCUBATOR_FIELD_TO_DISPLAY);
                    
                    String startDateStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_DATE_START);
                    String endDateStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_DATE_END);                    
                    
                    fieldToRetrieveArr=new String[0];
                    if ((fieldsToRetrieveStr!=null) && (fieldsToRetrieveStr.length()>0))
                        if ("ALL".equalsIgnoreCase(fieldsToRetrieveStr)) fieldToRetrieveArr=TblsEnvMonitConfig.InstrIncubator.getAllFieldNames();
                        else fieldToRetrieveArr=fieldsToRetrieveStr.split("\\|");
                    fieldToRetrieveArr=LPArray.addValueToArray1D(fieldToRetrieveArr, TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName());
                    fieldToDisplayArr=new String[0];
                    if ((fieldsToDisplayStr!=null) && (fieldsToDisplayStr.length()>0))
                        if ("ALL".equalsIgnoreCase(fieldsToDisplayStr)) fieldToDisplayArr=TblsEnvMonitConfig.InstrIncubator.getAllFieldNames();                        
                        else fieldToDisplayArr=fieldsToDisplayStr.split("\\|");
                    String[] incubTblAllFields=TblsEnvMonitConfig.InstrIncubator.getAllFieldNames();
                    Object[][] incubInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                            new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{incubName}, 
                            incubTblAllFields);                    
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubInfo[0][0].toString())){
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Error on getting sample <*1*> in procedure <*2*>", new Object[]{Arrays.toString(incubInfo[0]), schemaPrefix}));              
                        return;}  
                    jObjBatchInfo=new JSONObject();
                    jObjMainObject=new JSONObject();
                    jObjPieceOfInfo=new JSONObject();
                    jArrPieceOfInfo=new JSONArray();
                    for (int iFlds=0;iFlds<incubInfo[0].length;iFlds++){                      
                        if (LPArray.valueInArray(fieldToRetrieveArr, incubTblAllFields[iFlds]))
                            jObjBatchInfo.put(incubTblAllFields[iFlds], incubInfo[0][iFlds].toString());
                    }
                    for (int iFlds=0;iFlds<fieldToDisplayArr.length;iFlds++){                      
                        if (LPArray.valueInArray(incubTblAllFields, fieldToDisplayArr[iFlds])){
                            jObjPieceOfInfo=new JSONObject();
                            jObjPieceOfInfo.put("field_name", fieldToDisplayArr[iFlds]);
                            jObjPieceOfInfo.put("field_value", incubInfo[0][LPArray.valuePosicInArray(incubTblAllFields, fieldToDisplayArr[iFlds])].toString());
                            jArrPieceOfInfo.add(jObjPieceOfInfo);
                        }
                    }
                    jObjMainObject.put(GlobalAPIsParams.INCUBATION_REPORT_JSON_TAG_NAME_FIELD_TO_RETRIEVE, jObjBatchInfo);
                    jObjMainObject.put(GlobalAPIsParams.INCUBATION_REPORT_JSON_TAG_NAME_FIELD_TO_DISPLAY, jArrPieceOfInfo);
    
                    String numPoints=request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_INCUBATOR_NUM_POINTS);                     
                    Integer numPointsInt=null;
                    fieldsToRetrieve=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_ID.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                                TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(),
                                TblsEnvMonitData.InstrIncubatorNoteBook.FLD_TEMPERATURE.getName()};            
                    if (numPoints!=null) numPointsInt=Integer.valueOf(numPoints); 
                    else numPointsInt=20;
                    Object[][] instrReadings =new Object[0][0]; 
                    if (startDateStr==null && endDateStr==null) 
                        instrReadings = DataIncubatorNoteBook.getLastTemperatureReading(schemaPrefix, incubName, numPointsInt);                    
                    if (startDateStr!=null && endDateStr==null){ 
                        
                        startDateStr=startDateStr.replace ( " " , "T" );
                        instrReadings = DataIncubatorNoteBook.getLastTemperatureReading(schemaPrefix, incubName, numPointsInt, dateStringFormatToLocalDateTime(startDateStr));
                    }
                    if (startDateStr!=null && endDateStr!=null){
                        startDateStr=startDateStr.replace ( " " , "T" );
                        endDateStr=endDateStr.replace (" " , "T" );
                        instrReadings = DataIncubatorNoteBook.getLastTemperatureReading(schemaPrefix, incubName, numPointsInt, dateStringFormatToLocalDateTime(startDateStr), dateStringFormatToLocalDateTime(endDateStr));
                    }
                    jArrLastTempReadings = new JSONArray();
                    for (Object[] currReading: instrReadings){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currReading);
                        jArrLastTempReadings.add(jObj);
                    }
                    jObjMainObject.put(GlobalAPIsParams.INCUBATION_REPORT_JSON_TAG_NAME_LAST_N_TEMP_READINGS, jArrLastTempReadings);
                    
                    LPFrontEnd.servletReturnSuccess(request, response, jObjMainObject);
                    break;                    
                    
                case API_ENDPOINT_STATS_SAMPLES_PER_STAGE:
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, EnvMonitAPIParams.MANDATORY_PARAMS_STATS_SAMPLES_PER_STAGE.split("\\|"));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                        LPFrontEnd.servletReturnResponseError(request, response, 
                                LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                        return;                  
                    }      
                    String[] whereFieldNames = new String[0];
                    Object[] whereFieldValues = new Object[0];
                    fieldToRetrieveArr=new String[]{TblsEnvMonitData.Sample.FLD_CURRENT_STAGE.getName()};
                    String programName=request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME);  
                    if (programName!=null){
                        whereFieldNames=new String[]{TblsEnvMonitData.Sample.FLD_PROGRAM_NAME.getName()}; 
                        whereFieldValues=new Object[]{programName};
                    }
                    String stagesToInclude=request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_STAGES_TO_INCLUDE);  
                    if (stagesToInclude!=null){
                        whereFieldNames=LPArray.addValueToArray1D(whereFieldNames, TblsEnvMonitData.Sample.FLD_CURRENT_STAGE.getName()+" in|"); 
                        whereFieldValues=LPArray.addValueToArray1D(whereFieldValues, stagesToInclude);
                    }                   
                    String stagesToExclude=request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_STAGES_TO_EXCLUDE);  
                    if (stagesToExclude!=null){
                        whereFieldNames=LPArray.addValueToArray1D(whereFieldNames, TblsEnvMonitData.Sample.FLD_CURRENT_STAGE.getName()+" not in|"); 
                        whereFieldValues=LPArray.addValueToArray1D(whereFieldValues, stagesToExclude);
                    }

                    if (whereFieldNames.length==0){
                        whereFieldNames=new String[]{TblsEnvMonitData.Sample.FLD_PROGRAM_NAME.getName()+" not in"}; 
                        whereFieldValues=new Object[]{"<<"};                        
                    }
                    Object[][] samplesCounterPerStage=Rdbms.getGrouper(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.Sample.TBL.getName(), 
                            fieldToRetrieveArr, 
                            whereFieldNames, whereFieldValues,
                            new String[]{"COUNTER desc"});  
                    fieldToRetrieveArr=LPArray.addValueToArray1D(fieldToRetrieveArr, "COUNTER");
                    jArr=new JSONArray();
                    for (Object[] curRec: samplesCounterPerStage){
                      JSONObject jObj= LPJson.convertArrayRowToJSONObject(fieldToRetrieveArr, curRec);
                      jArr.add(jObj);
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;                     
                case API_ENDPOINT_STATS_PROGRAM_LAST_RESULTS:
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, EnvMonitAPIParams.MANDATORY_PARAMS_STATS_PROGRAM_LAST_RESULTS.split("\\|"));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                        LPFrontEnd.servletReturnResponseError(request, response, 
                                LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                        return;                  
                    }    
                    String grouped=request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_GROUPED);
                    fieldsToRetrieveStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE);
                    fieldToRetrieveArr=new String[0];
                    Integer numTotalRecords=50;
                    String numTotalRecordsStr=request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_TOTAL_OBJECTS);
                    if (numTotalRecordsStr==null) numTotalRecords=50;
                    else
                        numTotalRecords=Integer.valueOf(LPNulls.replaceNull(request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_TOTAL_OBJECTS)));
                    if ((fieldsToRetrieveStr!=null) && (fieldsToRetrieveStr.length()>0)){
                        if ("ALL".equalsIgnoreCase(fieldsToRetrieveStr)) fieldToRetrieveArr=TblsData.ViewSampleAnalysisResultWithSpecLimits.getAllFieldNames();
                        else fieldToRetrieveArr=fieldsToRetrieveStr.split("\\|");
                    }else fieldToRetrieveArr=TblsData.ViewSampleAnalysisResultWithSpecLimits.getAllFieldNames();
                    if (grouped==null || !Boolean.valueOf(grouped)){
                        whereFieldNames = new String[0];
                        whereFieldValues = new Object[0];                    
                        programName=request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME);
                        if (programName!=null){
                            whereFieldNames=LPArray.addValueToArray1D(whereFieldNames, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_PROGRAM_NAME.getName());
                            whereFieldValues=LPArray.addValueToArray1D(whereFieldValues, programName);
                            //whereLimitsFieldNames=LPArray.addValueToArray1D(whereFieldNames, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_PROGRAM_NAME.getName());
                            //whereLimitsFieldValues=LPArray.addValueToArray1D(whereFieldValues, programName);                        
                        }
                        whereFieldNames = LPArray.addValueToArray1D(whereFieldNames, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_RAW_VALUE.getName()+ " is not null");

                        Object[][] programLastResults=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(), 
                                whereFieldNames, whereFieldValues, 
                                fieldToRetrieveArr, new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_ENTERED_ON.getName()+" desc"});  
                        if (numTotalRecords>programLastResults.length) numTotalRecords=programLastResults.length;
                        jArr=new JSONArray();
                        for (int i=0;i<numTotalRecords;i++){
                          JSONObject jObj= LPJson.convertArrayRowToJSONObject(fieldToRetrieveArr, programLastResults[i]);
                          jArr.add(jObj);
                        }
                    }else{
                        String[] whereLimitsFieldNames = new String[0];
                        Object[] whereLimitsFieldValues = new Object[0];                    

                        if (whereLimitsFieldNames==null || whereLimitsFieldNames.length==0)
                            whereLimitsFieldNames=new String[]{TblsCnfg.SpecLimits.FLD_LIMIT_ID.getName()+" is not null"};
//                        
                        String[] fieldToRetrieveLimitsArr=new String[]{TblsCnfg.SpecLimits.FLD_CODE.getName(), TblsCnfg.SpecLimits.FLD_VARIATION_NAME.getName(), TblsCnfg.SpecLimits.FLD_ANALYSIS.getName(), TblsCnfg.SpecLimits.FLD_METHOD_NAME.getName(), TblsCnfg.SpecLimits.FLD_PARAMETER.getName(), TblsCnfg.SpecLimits.FLD_RULE_TYPE.getName(),
                            TblsCnfg.SpecLimits.FLD_RULE_VARIABLES.getName(), TblsCnfg.SpecLimits.FLD_UOM.getName()};
                        String[] limitsFieldNamesToFilter=new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SPEC_CODE.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SPEC_VARIATION_NAME.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_ANALYSIS.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_METHOD_NAME.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_PARAMETER.getName()};
                        String[] fieldToRetrieveGroupedArr = new String[0];                        
                        if ((fieldsToRetrieveStr==null) || ("ALL".equalsIgnoreCase(fieldsToRetrieveStr)) ) fieldToRetrieveGroupedArr=TblsData.ViewSampleAnalysisResultWithSpecLimits.getAllFieldNames();
                        else fieldToRetrieveGroupedArr=fieldsToRetrieveStr.split("\\|");
                        Object[][] specLimits=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsCnfg.SpecLimits.TBL.getName(), 
                                whereLimitsFieldNames, whereLimitsFieldValues, fieldToRetrieveLimitsArr, new String[]{TblsCnfg.SpecLimits.FLD_LIMIT_ID.getName()});
                        jArr=new JSONArray();
                        for (Object[] currLimit: specLimits){
                            numTotalRecords=50;
                            whereFieldNames = new String[0];
                            whereFieldValues = new Object[0];                    
                            programName=request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME);
                            if (programName!=null){
                                whereFieldNames=LPArray.addValueToArray1D(whereFieldNames, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_PROGRAM_NAME.getName());
                                whereFieldValues=LPArray.addValueToArray1D(whereFieldValues, programName);
                                //whereLimitsFieldNames=LPArray.addValueToArray1D(whereFieldNames, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_PROGRAM_NAME.getName());
                                //whereLimitsFieldValues=LPArray.addValueToArray1D(whereFieldValues, programName);                        
                            }                            
                            JSONObject jObj= LPJson.convertArrayRowToJSONObject(fieldToRetrieveLimitsArr, currLimit);
                            for (int i=0;i<limitsFieldNamesToFilter.length;i++){
                                whereFieldNames=LPArray.addValueToArray1D(whereFieldNames, limitsFieldNamesToFilter[i]);                                
                                whereFieldValues=LPArray.addValueToArray1D(whereFieldValues, currLimit[i]);
                            }
                            whereFieldNames = LPArray.addValueToArray1D(whereFieldNames, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_RAW_VALUE.getName()+ " is not null");                            
                            whereFieldValues=LPArray.addValueToArray1D(whereFieldValues, "");                            
                            Object[][] programLastResults=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(), 
                                    whereFieldNames, whereFieldValues, 
                                    fieldToRetrieveArr, new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_ENTERED_ON.getName()+" desc"});
                            JSONArray jArrSampleResults=new JSONArray();
                            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(programLastResults[0][0].toString())){
                                if (numTotalRecords>programLastResults.length) numTotalRecords=programLastResults.length;
                                for (int i=0;i<numTotalRecords;i++){
                                  JSONObject jResultsObj= LPJson.convertArrayRowToJSONObject(fieldToRetrieveGroupedArr, programLastResults[i]);
                                  jArrSampleResults.add(jResultsObj);
                                }
                            }
                            jObj.put(JSON_TAG_NAME_SAMPLE_RESULTS, jArrSampleResults); 
                            jArr.add(jObj);
                        }                        
                        
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;                     
                default:      
                  RequestDispatcher rd = request.getRequestDispatcher(SampleAPIParams.SERVLET_FRONTEND_URL);
                  rd.forward(request,response);   
            }
            }catch(Exception e){      
              String exceptionMessage =e.getMessage();
              if (exceptionMessage==null){exceptionMessage="null exception";}
              response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
              LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null);      
        } finally {
            // release database resources
            try {
                Rdbms.closeRdbms();   
            } catch (Exception ignore) {
            }
        }                
}

private Object[][] sampleStageDataArr(String schemaPrefix, Integer sampleId, String[] sampleFldName, Object[] sampleFldValue, String[] sampleStageFldName, Object[] sampleStageFldValue){
    if (sampleStageFldValue==null) return null; //new Object[][]{{}};
    if (!LPArray.valueInArray(sampleStageFldName, TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_STAGE_CURRENT.getName())) return null; //new Object[][]{{}};
    String currentStage=sampleStageFldValue[LPArray.valuePosicInArray(sampleStageFldName, TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_STAGE_CURRENT.getName())].toString();
    
    switch (currentStage.toUpperCase()){
        case "SAMPLING":
            return new Object[][]{{TblsEnvMonitData.Sample.FLD_SAMPLING_DATE.getName(), sampleFldValue[LPArray.valuePosicInArray(sampleFldName, TblsEnvMonitData.Sample.FLD_SAMPLING_DATE.getName())].toString()}};
        case "INCUBATION":
            String[] incub1Flds=new String[]{TblsEnvMonitData.Sample.FLD_INCUBATION_PASSED.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION_INCUBATOR.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION_BATCH.getName(), 
                TblsEnvMonitData.Sample.FLD_INCUBATION_START.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION_START_TEMP_EVENT_ID.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION_START_TEMPERATURE.getName(),
                TblsEnvMonitData.Sample.FLD_INCUBATION_END.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION_END_TEMP_EVENT_ID.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION_END_TEMPERATURE.getName()};
            Object[] curFldArr=new Object[0];
            for (String curFld: incub1Flds){
                Integer fldPosic=LPArray.valuePosicInArray(sampleFldName, curFld);
                if (fldPosic==-1){
                    curFldArr=LPArray.addValueToArray1D(curFldArr, "");
                    curFldArr=LPArray.addValueToArray1D(curFldArr, "");
                }else{
                    curFldArr=LPArray.addValueToArray1D(curFldArr, curFld);
                    curFldArr=LPArray.addValueToArray1D(curFldArr, sampleFldValue[fldPosic].toString());
                }
                curFld.replace("incubation", "incubation2");
                fldPosic=LPArray.valuePosicInArray(sampleFldName, curFld);
                if (fldPosic==-1){
                    curFldArr=LPArray.addValueToArray1D(curFldArr, "");
                    curFldArr=LPArray.addValueToArray1D(curFldArr, "");
                }else{
                    curFldArr=LPArray.addValueToArray1D(curFldArr, curFld);
                    curFldArr=LPArray.addValueToArray1D(curFldArr, sampleFldValue[fldPosic].toString());
                }                
            }
            return LPArray.array1dTo2d(curFldArr, 4);
        case "PLATEREADING":
            return null;
        default: 
            return null; //new Object[][]{{}};
    }
    
    //return new Object[][]{{"hola", "adios"}};
}

private JSONArray sampleStageDataJsonArr(String schemaPrefix, Integer sampleId, String[] sampleFldName, Object[] sampleFldValue, String[] sampleStageFldName, Object[] sampleStageFldValue){
    if (sampleStageFldValue==null) return null; //new Object[][]{{}};
    if (!LPArray.valueInArray(sampleStageFldName, TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_STAGE_CURRENT.getName())) return null; //new Object[][]{{}};
    String currentStage=sampleStageFldValue[LPArray.valuePosicInArray(sampleStageFldName, TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_STAGE_CURRENT.getName())].toString();
    JSONObject jObj= new JSONObject();
    JSONArray jArrMainObj=new JSONArray();
    JSONArray jArrMainObj2=new JSONArray();
    switch (currentStage.toUpperCase()){
        case "SAMPLING":
            jObj.put(TblsEnvMonitData.Sample.FLD_SAMPLING_DATE.getName(), sampleFldValue[LPArray.valuePosicInArray(sampleFldName, TblsEnvMonitData.Sample.FLD_SAMPLING_DATE.getName())].toString());
            jObj.put("field_name", TblsEnvMonitData.Sample.FLD_SAMPLING_DATE.getName());
            jObj.put("field_value", sampleFldValue[LPArray.valuePosicInArray(sampleFldName, TblsEnvMonitData.Sample.FLD_SAMPLING_DATE.getName())].toString());
            jArrMainObj.add(jObj);
            return jArrMainObj; 
            //return new Object[][]{{TblsEnvMonitData.Sample.FLD_SAMPLING_DATE.getName(), sampleFldValue[LPArray.valuePosicInArray(sampleFldName, TblsEnvMonitData.Sample.FLD_SAMPLING_DATE.getName())].toString()}};
        case "INCUBATION":
            String[] incub1Flds=new String[]{TblsEnvMonitData.Sample.FLD_INCUBATION_PASSED.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION_INCUBATOR.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION_BATCH.getName(), 
                TblsEnvMonitData.Sample.FLD_INCUBATION_START.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION_START_TEMP_EVENT_ID.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION_START_TEMPERATURE.getName(),
                TblsEnvMonitData.Sample.FLD_INCUBATION_END.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION_END_TEMP_EVENT_ID.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION_END_TEMPERATURE.getName()};
            for (String curFld: incub1Flds){
                Integer fldPosic=LPArray.valuePosicInArray(sampleFldName, curFld);
                if (fldPosic>-1){
                    jObj= new JSONObject();
                    jObj.put(curFld, sampleFldValue[fldPosic].toString());
                    jArrMainObj.add(jObj);
                    JSONObject jObjSampleStageInfo=new JSONObject();
                    jObjSampleStageInfo.put("field_name", curFld);
                    jObjSampleStageInfo.put("field_value", sampleFldValue[fldPosic].toString());
                    jArrMainObj.add(jObjSampleStageInfo);
                }               
                curFld=curFld.replace("incubation", "incubation2");
                fldPosic=LPArray.valuePosicInArray(sampleFldName, curFld);
                if (fldPosic>-1){
                    jObj= new JSONObject();
                    jObj.put(curFld, sampleFldValue[fldPosic].toString());
                    jArrMainObj2.add(jObj);
                    JSONObject jObjSampleStageInfo=new JSONObject();
                    jObjSampleStageInfo.put("field_name", curFld);
                    jObjSampleStageInfo.put("field_value", sampleFldValue[fldPosic].toString());
                    jArrMainObj2.add(jObjSampleStageInfo);
                }                
            }
            JSONObject jObj2= new JSONObject();
            jObj2.put("incubation_1", jArrMainObj);
            jObj2.put("incubation_2", jArrMainObj2);
            jArrMainObj=new JSONArray();
            jArrMainObj.add(jObj2);
            return jArrMainObj;
        case "PLATEREADING":
        case "MICROORGANISMIDENTIFICATION":
            String[] tblAllFlds=TblsEnvMonitData.ViewSampleMicroorganismList.getAllFieldNames();
            Object[][] sampleStageInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.ViewSampleMicroorganismList.TBL.getName(), 
                    new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.FLD_SAMPLE_ID.getName()}, new Object[]{Integer.valueOf(sampleId)}, 
                    tblAllFlds, new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.FLD_TEST_ID.getName(), TblsEnvMonitData.ViewSampleMicroorganismList.FLD_RESULT_ID.getName()});                    
            jObj= new JSONObject();
            for (Object[] curRow: sampleStageInfo){
                jObj2= new JSONObject();
                for (int iFlds=0;iFlds<sampleStageInfo[0].length;iFlds++){ //Object[] curRec: sampleInfo){   
                    jObj2.put(tblAllFlds[iFlds], sampleStageInfo[0][iFlds].toString());
                    //jArrMainObj.add(jObj);
                    JSONObject jObjSampleStageInfo=new JSONObject();
                    jObjSampleStageInfo.put("field_name", tblAllFlds[iFlds]);
                    jObjSampleStageInfo.put("field_value", sampleStageInfo[0][iFlds].toString());
                    //jArrSampleStageInfo.add(jObjSampleStageInfo);
                    //jArrMainObj.add(jObj2);
                    jArrMainObj.add(jObjSampleStageInfo);
                }
                jObj.put("counting", jObj2);
                //jObj.put("fields", jArrSampleStageInfo);
            }
            //jArrMainObj=new JSONArray();
            jArrMainObj.add(jObj);
           // jArrMainObj.add(jArrSampleStageInfo);
            return jArrMainObj;
            
            
            
/*            jObj.put(TblsEnvMonitData.Sample.FLD_SAMPLING_DATE.getName(), sampleFldValue[LPArray.valuePosicInArray(sampleFldName, TblsEnvMonitData.Sample.FLD_SAMPLING_DATE.getName())].toString());
            jArrMainObj.add(jObj);
            return jArrMainObj; */
        default: 
            return jArrMainObj; //new Object[][]{{}};
    }
    
    //return new Object[][]{{"hola", "adios"}};
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
