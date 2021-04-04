/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPJson;
import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.modulesample.SampleAPIParams.SampleAPIfrontendEndpoints;
import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsApp;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsDataAudit;
import databases.Token;
import static functionaljavaa.certification.AnalysisMethodCertifQueries.analysisMethodCertifiedUsersList;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.samplestructure.DataSample;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class SampleAPIfrontend extends HttpServlet {

    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX;
    
    /**
     *
     */
    public static final String VIEW_NAME_ANALYSIS_METHOD="analysis_methods_view";

    /**
     *
     */
    public static final String VIEW_NAME_SAMPLE_COC_NAMES="sample_coc_names";
    
    
    
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

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        String procInstanceName=procReqInstance.getProcedureInstance();
        
        try (PrintWriter out = response.getWriter()) {

            String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());    
            String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());  
        
            //Token token = new Token(finalToken);
            SampleAPIfrontendEndpoints endPoint = null;
            try{
                endPoint = SampleAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                             

//            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}   
            
            switch (endPoint){
            case GET_SAMPLETEMPLATES:       
                String[] filterFieldName = new String[]{TblsCnfg.Sample.FLD_JSON_DEFINITION.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()};
                Object[] filterFieldValue = new Object[]{""};
/*                filterFieldName = LPArray.addValueToArray1D(filterFieldName, "code");
                if ("process-us".equalsIgnoreCase(procInstanceName)){
                    filterFieldValue = LPArray.addValueToArray1D(filterFieldValue, "specSamples");
                }else{filterFieldValue = LPArray.addValueToArray1D(filterFieldValue, "sampleTemplate");}    */
                Object[][] datas = Rdbms.getRecordFieldsByFilter(schemaConfigName,TblsCnfg.Sample.TBL.getName(), 
                        filterFieldName, filterFieldValue, new String[] { TblsCnfg.Sample.FLD_JSON_DEFINITION.getName()});
                Rdbms.closeRdbms();
                JSONObject proceduresList = new JSONObject();
                JSONArray jArray = new JSONArray();
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(datas[0][0].toString())){  
                    Object[] errMsg = LPFrontEnd.responseError(LPArray.array2dTo1d(datas), language, null);
                    response.sendError((int) errMsg[0], (String) errMsg[1]);    
                    return;
                }else{                   
                   jArray.addAll(Arrays.asList(LPArray.array2dTo1d(datas)));    
                }           
                LPFrontEnd.servletReturnSuccess(request, response, jArray);
                return;
            case UNRECEIVESAMPLES_LIST:   
                Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, SampleAPIParams.MANDATORY_PARAMS_FRONTEND_UNRECEIVESAMPLES_LIST.split("\\|"));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                    LPFrontEnd.servletReturnResponseError(request, response, 
                            LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                    return;                  
                }                                  
                String[] sortFieldsNameArr = null;
                String[] sampleFieldToRetrieveArr = null;
                String sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME); 
                String sampleFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE); 
                
                if (! ((sortFieldsName==null) || (sortFieldsName.contains(GlobalAPIsParams.REQUEST_PARAM_VALUE_UNDEFINED))) ) {
                    sortFieldsNameArr = sortFieldsName.split("\\|");                                    
                }else{  sortFieldsNameArr = SampleAPIParams.MANDATORY_PARAMS_FRONTEND_UNRECEIVESAMPLES_LIST_SORT_FIELDS_NAME_DEFAULT_VALUE.split("\\|");}             
                if (sampleFieldToRetrieve!=null){
                    sampleFieldToRetrieveArr=LPArray.addValueToArray1D(sampleFieldToRetrieveArr, sampleFieldToRetrieve.split("\\|"));
                }else{
                    sampleFieldToRetrieveArr=SampleAPIParams.MANDATORY_PARAMS_FRONTEND_UNRECEIVESAMPLES_LIST_SAMPLE_FIELD_RETRIEVE_DEFAULT_VALUE.split("\\|");
                }                
                
                String[] whereFieldsNameArr = null;
                Object[] whereFieldsValueArr = null;
                String whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME); 
                if (whereFieldsName==null){whereFieldsName="";}
                String whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE); 
                if (whereFieldsValue==null){whereFieldsValue="";}
                
                if ( ("".equals(whereFieldsName)) && ("".equals(whereFieldsValue)) ){
                    whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, TblsData.Sample.FLD_RECEIVED_BY.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause());
                    whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, "");
                }else{
                    whereFieldsNameArr=LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
                    whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|")));                                          
                    for (int iFields=0; iFields<whereFieldsNameArr.length; iFields++){
                        if (LPPlatform.isEncryptedField(schemaDataName, TblsData.Sample.TBL.getName(), whereFieldsNameArr[iFields])){                
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
                    whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, TblsData.Sample.FLD_RECEIVED_BY.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause());
                    whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, "");
                }  
                Object[][] smplsData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(),
                    whereFieldsNameArr, whereFieldsValueArr, sampleFieldToRetrieveArr, sortFieldsNameArr);
                JSONArray smplsJsArr= new JSONArray();
                for (Object[] curSmp: smplsData){
                    smplsJsArr.add(LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, curSmp));
                }
                Rdbms.closeRdbms();
                LPFrontEnd.servletReturnSuccess(request, response, smplsJsArr);       
                return; 
            case SAMPLEANALYSIS_PENDING_REVISION: 
                whereFieldsNameArr =new String[]{};
                whereFieldsValueArr =new Object[]{};
                String[] sampleAnalysisFieldToRetrieveArr= new String[]{};
                String[] sampleAnalysisSortFieldArr= new String[]{};
                whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME); 
                if (whereFieldsName==null){whereFieldsName="";}
                whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE); 

                whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, TblsData.SampleAnalysis.FLD_READY_FOR_REVISION.getName());
                if (whereFieldsName!=null && whereFieldsName.length()>0)whereFieldsNameArr=LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
                
                if (whereFieldsValue==null || whereFieldsValue.length()==0)whereFieldsValue="true*Boolean";
                else whereFieldsValue="true*Boolean|"+whereFieldsValue;
                whereFieldsValueArr=LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|"));
                
                
                String sampleAnalysisFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE); 
                if (sampleAnalysisFieldToRetrieve!=null && sampleAnalysisFieldToRetrieve.length()>0) sampleAnalysisFieldToRetrieveArr=sampleAnalysisFieldToRetrieve.split("\\|");
                else sampleAnalysisFieldToRetrieveArr=TblsData.SampleAnalysis.getAllFieldNames();                
                String sampleAnalysisSortField = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME); 
                if (sampleAnalysisSortField!=null && sampleAnalysisSortField.length()>0) sampleAnalysisSortFieldArr=sampleAnalysisSortField.split("\\|");
                
                Object[][] smplsAnaData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(),
                    whereFieldsNameArr, whereFieldsValueArr, sampleAnalysisFieldToRetrieveArr, sampleAnalysisSortFieldArr);
                JSONArray smplAnaJsArr= new JSONArray();
                for (Object[] curSmpAna: smplsAnaData){
                    smplAnaJsArr.add(LPJson.convertArrayRowToJSONObject(sampleAnalysisFieldToRetrieveArr, curSmpAna));
                }
                Rdbms.closeRdbms();
                LPFrontEnd.servletReturnSuccess(request, response, smplAnaJsArr);       
                return;
            case SAMPLES_BY_STAGE:   
            case SAMPLES_INPROGRESS_LIST:   
                whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME); 
                if (whereFieldsName==null){whereFieldsName="";}
                whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE); 

                sampleFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE); 
                String testFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE); 
                String sampleLastLevel = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL);                 
                
                String addSampleAnalysis = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS); 
                if (addSampleAnalysis==null){addSampleAnalysis="false";}
                String addSampleAnalysisFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE);                
                String[] addSampleAnalysisFieldToRetrieveArr = SampleAPIParams.MANDATORY_PARAMS_FRONTEND_SAMPLES_INPROGRESS_LIST_SAMPLE_ANALYSIS_FIELD_RETRIEVE_DEFAULT_VALUE.split("\\|");
                if ( (addSampleAnalysisFieldToRetrieve!=null) && (addSampleAnalysisFieldToRetrieve.length()>0) ) {
                    addSampleAnalysisFieldToRetrieveArr=LPArray.addValueToArray1D(addSampleAnalysisFieldToRetrieveArr, addSampleAnalysisFieldToRetrieve.split("\\|"));
                }                                
                String sampleAnalysisWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME); 
                String[] sampleAnalysisWhereFieldsNameArr = new String[0];
                if ( (sampleAnalysisWhereFieldsName!=null) && (sampleAnalysisWhereFieldsName.length()>0) ) {
                    sampleAnalysisWhereFieldsNameArr=LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                }                                
                String sampleAnalysisWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE); 

                String addSampleAnalysisResult = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT); 
                if (addSampleAnalysisResult==null){addSampleAnalysisResult="false";}
                String addSampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE); 
                String[] addSampleAnalysisResultFieldToRetrieveArr = SampleAPIParams.MANDATORY_PARAMS_FRONTEND_SAMPLES_INPROGRESS_LIST_SAMPLE_ANALYSIS_RESULT_FIELD_RETRIEVE_DEFAULT_VALUE.split("\\|");
                if ( (addSampleAnalysisResultFieldToRetrieve!=null) && (addSampleAnalysisResultFieldToRetrieve.length()>0) ) {
                    addSampleAnalysisResultFieldToRetrieveArr=LPArray.addValueToArray1D(addSampleAnalysisResultFieldToRetrieveArr, addSampleAnalysisResultFieldToRetrieve.split("\\|"));
                }                                
                String sampleAnalysisResultWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME); 
                String[] sampleAnalysisResultWhereFieldsNameArr = new String[0];
                if ( (sampleAnalysisResultWhereFieldsName!=null) && (sampleAnalysisResultWhereFieldsName.length()>0) ) {
                    sampleAnalysisResultWhereFieldsNameArr=LPArray.addValueToArray1D(sampleAnalysisResultWhereFieldsNameArr, sampleAnalysisResultWhereFieldsName.split("\\|"));
                }                                
                String sampleAnalysisResultWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE); 
                
                if (sampleLastLevel==null){
                    sampleLastLevel=TblsData.Sample.TBL.getName();
                }                                
                sampleFieldToRetrieveArr = new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()};
                if (sampleFieldToRetrieve!=null){
                    sampleFieldToRetrieveArr=LPArray.addValueToArray1D(sampleFieldToRetrieveArr, sampleFieldToRetrieve.split("\\|"));
                }  
                String[] testFieldToRetrieveArr = new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()};
                if (testFieldToRetrieve!=null){
                    testFieldToRetrieveArr=LPArray.addValueToArray1D(testFieldToRetrieveArr, testFieldToRetrieve.split("\\|"));
                }                
                whereFieldsNameArr = null;
                whereFieldsValueArr = null; 
                if (actionName.toUpperCase().equalsIgnoreCase(SampleAPIfrontendEndpoints.SAMPLES_INPROGRESS_LIST.getName()) && (!whereFieldsName.contains(TblsData.Sample.FLD_STATUS.getName())) ){
                    whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, TblsData.Sample.FLD_RECEIVED_BY.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause());
                    whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, "");
                    Object[] recEncrypted = LPPlatform.encryptString("RECEIVED");
                    whereFieldsNameArr=LPArray.addValueToArray1D(whereFieldsNameArr, TblsData.Sample.FLD_STATUS.getName()+" in|");                
                    whereFieldsValueArr=LPArray.addValueToArray1D(whereFieldsValueArr, "RECEIVED|"+recEncrypted[1]);                                  
                }
                if ( (whereFieldsName!=null) && (whereFieldsValue!=null) ){
                    whereFieldsNameArr=LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
                    whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|")));                                          
                    for (int iFields=0; iFields<whereFieldsNameArr.length; iFields++){
                        if (LPPlatform.isEncryptedField(schemaDataName, TblsData.Sample.TBL.getName(), whereFieldsNameArr[iFields])){                
                            HashMap<String, String> hm = LPPlatform.encryptEncryptableFieldsAddBoth(whereFieldsNameArr[iFields], whereFieldsValueArr[iFields].toString());
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
                }            
                sortFieldsNameArr = null;
                sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME); 
                if (! ((sortFieldsName==null) || (sortFieldsName.contains(GlobalAPIsParams.REQUEST_PARAM_VALUE_UNDEFINED))) ) {
                    sortFieldsNameArr = sortFieldsName.split("\\|");                                    
                }else{   sortFieldsNameArr=null;}  
                for (int iFldV=0;iFldV<whereFieldsValueArr.length; iFldV++){                  
                  if (whereFieldsValueArr[iFldV].toString().equalsIgnoreCase("false")){whereFieldsValueArr[iFldV]=Boolean.valueOf(whereFieldsValueArr[iFldV].toString());}
                  if (whereFieldsValueArr[iFldV].toString().equalsIgnoreCase("true")){whereFieldsValueArr[iFldV]=Boolean.valueOf(whereFieldsValueArr[iFldV].toString());}
                }
                if (TblsData.Sample.TBL.getName().equals(sampleLastLevel)){ 
                    Object[][] mySamples = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(),
                        whereFieldsNameArr, whereFieldsValueArr, sampleFieldToRetrieveArr, sortFieldsNameArr);
                    if (mySamples==null){ 
                        LPFrontEnd.servletReturnSuccess(request, response);       
                        return;
                    }
                    if ( LPPlatform.LAB_FALSE.equalsIgnoreCase(mySamples[0][0].toString())) {  
                        LPFrontEnd.servletReturnSuccess(request, response);       
                        return;
                    }else{                        
                        JSONArray mySamplesJSArr = new JSONArray();
                        for (Object[] mySample : mySamples) {
                            JSONObject mySampleJSObj = LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, mySample);                
                            if ("TRUE".equalsIgnoreCase(addSampleAnalysis)){
                                String[] testWhereFieldsNameArr = new String[]{TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName()};
                                testWhereFieldsNameArr=LPArray.addValueToArray1D(testWhereFieldsNameArr, sampleAnalysisWhereFieldsName);                                
                                Integer sampleIdPosicInArray = LPArray.valuePosicInArray(sampleFieldToRetrieveArr, TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName());
                                Object[] testWhereFieldsValueArr = new Object[]{Integer.parseInt(mySample[sampleIdPosicInArray].toString())};
                                testWhereFieldsValueArr=LPArray.addValueToArray1D(testWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")));
                                Object[][] mySampleAnalysis = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(),
                                        testWhereFieldsNameArr, testWhereFieldsValueArr, addSampleAnalysisFieldToRetrieveArr);          
                                JSONArray mySamplesAnaJSArr = new JSONArray();
                                if ( LPPlatform.LAB_FALSE.equalsIgnoreCase(mySampleAnalysis[0][0].toString()) ){
                                    mySampleJSObj.put(TblsData.SampleAnalysis.TBL.getName(), mySamplesAnaJSArr);
                                }else{                                    
                                    for (Object[] mySampleAnalysi : mySampleAnalysis) {
                                        JSONObject mySampleAnaJSObj = LPJson.convertArrayRowToJSONObject(addSampleAnalysisFieldToRetrieveArr, mySampleAnalysi);

                                        if ("TRUE".equalsIgnoreCase(addSampleAnalysisResult)){
                                            String[] sarWhereFieldsNameArr = new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()};
                                            if (sampleAnalysisResultWhereFieldsName!=null)
                                                sarWhereFieldsNameArr=LPArray.addValueToArray1D(sarWhereFieldsNameArr, sampleAnalysisResultWhereFieldsName);
                                            Integer testIdPosicInArray = LPArray.valuePosicInArray(addSampleAnalysisFieldToRetrieveArr, TblsData.SampleAnalysis.FLD_TEST_ID.getName());
                                            Object[] sarWhereFieldsValueArr = new Object[]{Integer.parseInt(mySampleAnalysi[testIdPosicInArray].toString())};
                                            if (sampleAnalysisResultWhereFieldsValue!=null)
                                                sarWhereFieldsValueArr=LPArray.addValueToArray1D(sarWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(LPNulls.replaceNull(sampleAnalysisResultWhereFieldsValue).split("\\|")));                                            
                                            
                                            Object[][] mySampleAnalysisResults = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(),
                                                    sarWhereFieldsNameArr, sarWhereFieldsValueArr, addSampleAnalysisResultFieldToRetrieveArr);          
                                            JSONArray mySamplesAnaResJSArr = new JSONArray();
                                            if ( LPPlatform.LAB_FALSE.equalsIgnoreCase(mySampleAnalysisResults[0][0].toString()) ){
                                                mySampleAnaJSObj.put(TblsData.SampleAnalysisResult.TBL.getName(), mySamplesAnaResJSArr);                                        
                                            }
                                            JSONObject mySampleAnaResJSObj = new JSONObject();
                                            for (Object[] mySampleAnalysisResult : mySampleAnalysisResults) {
                                                mySampleAnaResJSObj = LPJson.convertArrayRowToJSONObject(addSampleAnalysisResultFieldToRetrieveArr, mySampleAnalysisResult);
                                                mySamplesAnaResJSArr.add(mySampleAnaResJSObj);
                                            }
                                            mySampleAnaJSObj.put(TblsData.SampleAnalysisResult.TBL.getName(), mySamplesAnaResJSArr);  
                                        }
                                        mySamplesAnaJSArr.add(mySampleAnaJSObj);
                                    }        
                                    mySampleJSObj.put(TblsData.SampleAnalysis.TBL.getName(), mySamplesAnaJSArr);
                                }
                            }                            
                            mySamplesJSArr.add(mySampleJSObj);
                        }
                        LPFrontEnd.servletReturnSuccess(request, response, mySamplesJSArr);                                   
                        return;
                    }
                }else{                    
                    whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, "sample_id is not null");
                    whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, "");
                    JSONArray samplesArray = new JSONArray();    
                    JSONArray sampleArray = new JSONArray();    
                    Object[][] mySamples = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(),
                            whereFieldsNameArr, whereFieldsValueArr, sampleFieldToRetrieveArr);
                    if ( LPPlatform.LAB_FALSE.equalsIgnoreCase(mySamples[0][0].toString()) ){
                        Rdbms.closeRdbms(); 
                        Object[] errMsg = LPFrontEnd.responseError(LPArray.array2dTo1d(mySamples), language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);                            
                        return;
                    }
                    for (int xProc=0; xProc<mySamples.length; xProc++){
                        JSONObject sampleObj = new JSONObject();
                        Integer sampleId = Integer.valueOf(mySamples[xProc][0].toString());
                        for (int yProc=0; yProc<mySamples[0].length; yProc++){
                            if (mySamples[xProc][yProc] instanceof Timestamp){
                                sampleObj.put(sampleFieldToRetrieveArr[yProc], mySamples[xProc][yProc].toString());
                            }
                            sampleObj.put(sampleFieldToRetrieveArr[yProc], mySamples[xProc][yProc]);
                        }
                        if ( ("TEST".equals(sampleLastLevel)) || ("RESULT".equals(sampleLastLevel)) ) {
                            String[] testWhereFieldsNameArr = new String[]{TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName()};
                            Object[] testWhereFieldsValueArr = new Object[]{sampleId};
                            Object[][] mySampleAnalysis = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(),
                                    testWhereFieldsNameArr, testWhereFieldsValueArr, testFieldToRetrieveArr);          
                            for (Object[] mySampleAnalysi : mySampleAnalysis) {
                                JSONObject testObj = new JSONObject();
                                for (int ySmpAna = 0; ySmpAna<mySampleAnalysis[0].length; ySmpAna++) {
                                    if (mySampleAnalysi[ySmpAna] instanceof Timestamp) {
                                        testObj.put(testFieldToRetrieveArr[ySmpAna], mySampleAnalysi[ySmpAna].toString());
                                    } else {
                                        testObj.put(testFieldToRetrieveArr[ySmpAna], mySampleAnalysi[ySmpAna]);
                                    }
                                }      
                                sampleArray.add(testObj);
                            }
                            sampleObj.put(TblsData.SampleAnalysis.TBL.getName(), sampleArray);
                        }
                        sampleArray.add(sampleObj);                        
                    }
                    Rdbms.closeRdbms();
                    samplesArray.add(sampleArray);
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("samples", samplesArray);   
                    LPFrontEnd.servletReturnSuccess(request, response, jsonObj);                    
                }
                    Rdbms.closeRdbms(); 
                    return;                                        
                case ANALYSIS_ALL_LIST:          
                    String fieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE); 

                    String[] fieldToRetrieveArr = new String[0];
                    if ( (fieldToRetrieve==null) || (fieldToRetrieve.length()==0) ){
                        fieldToRetrieveArr=LPArray.addValueToArray1D(fieldToRetrieveArr, SampleAPIParams.MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_ANALYSIS_ALL_LIST.split("\\|"));
                    }else{
                        fieldToRetrieveArr=LPArray.addValueToArray1D(fieldToRetrieveArr, fieldToRetrieve.split("\\|"));                        
                            fieldToRetrieveArr=LPArray.addValueToArray1D(fieldToRetrieveArr, SampleAPIParams.MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_ANALYSIS_ALL_LIST.split("\\|"));                       
                    }                
                    sortFieldsNameArr = null;
                    sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME); 
                    if (! ((sortFieldsName==null) || (sortFieldsName.contains(GlobalAPIsParams.REQUEST_PARAM_VALUE_UNDEFINED))) ) {
                        sortFieldsNameArr = sortFieldsName.split("\\|");                                    
                    }else{   sortFieldsNameArr=null;}  

                    String myData = Rdbms.getRecordFieldsByFilterJSON(schemaConfigName, VIEW_NAME_ANALYSIS_METHOD,
                            new String[]{"code is not null"},new Object[]{true}, fieldToRetrieveArr, sortFieldsNameArr);
                    Rdbms.closeRdbms();
                    if (myData.contains(LPPlatform.LAB_FALSE)){  
                        Object[] errMsg = LPFrontEnd.responseError(new String[] {myData}, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);                            
                    }else{
                        LPFrontEnd.servletReturnSuccess(request, response, myData);
                    }
                    return;         
                case GET_SAMPLE_ANALYSIS_LIST:    
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, SampleAPIParams.MANDATORY_PARAMS_FRONTEND_GET_SAMPLE_ANALYSIS_LIST.split("\\|"));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                        LPFrontEnd.servletReturnResponseError(request, response, 
                                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                        return;                  
                    }                      
                    String[] sampleAnalysisFixFieldToRetrieveArr = SampleAPIParams.MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_GET_SAMPLE_ANALYSIS_LIST.split("\\|");                                        
                    String sampleIdStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID);                                                      
                    Integer sampleId = Integer.parseInt(sampleIdStr);       
                    
                    sampleAnalysisFieldToRetrieveArr = new String[0];
                    sampleAnalysisFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE);  
                    if (! ((sampleAnalysisFieldToRetrieve==null) || (sampleAnalysisFieldToRetrieve.contains(GlobalAPIsParams.REQUEST_PARAM_VALUE_UNDEFINED))) ) {
                         sampleAnalysisFieldToRetrieveArr=  sampleAnalysisFieldToRetrieve.split("\\|");                             
                    }    
                    sampleAnalysisFieldToRetrieveArr = LPArray.addValueToArray1D(sampleAnalysisFieldToRetrieveArr, sampleAnalysisFixFieldToRetrieveArr);
                    
                    sortFieldsNameArr = null;
                    sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME); 
                    if (! ((sortFieldsName==null) || (sortFieldsName.contains(GlobalAPIsParams.REQUEST_PARAM_VALUE_UNDEFINED))) ) {
                        sortFieldsNameArr =  sortFieldsName.split("\\|");                                    
                    }else{   
                        sortFieldsNameArr = SampleAPIParams.MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_GET_SAMPLE_ANALYSIS_LIST.split("\\|");                     
                    }  
                    myData = Rdbms.getRecordFieldsByFilterJSON(schemaDataName, TblsData.SampleAnalysis.TBL.getName(),
                            new String[]{TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName()},new Object[]{sampleId}, sampleAnalysisFieldToRetrieveArr, sortFieldsNameArr);
                    Rdbms.closeRdbms();
                    if (myData.contains(LPPlatform.LAB_FALSE)){  
                        Object[] errMsg = LPFrontEnd.responseError(new String[] {myData}, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);                            
                    }else{
                        LPFrontEnd.servletReturnSuccess(request, response, myData);
                    }
                    return;                                            
                case GET_SAMPLE_ANALYSIS_RESULT_LIST:
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, SampleAPIParams.MANDATORY_PARAMS_FRONTEND_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|"));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                        LPFrontEnd.servletReturnResponseError(request, response, 
                                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                        return;                  
                    }                      
                    sampleIdStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID);                                                      
                    sampleId = Integer.parseInt(sampleIdStr);                           
                    String resultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE);
                    String[] resultFieldToRetrieveArr=null;
                    if (resultFieldToRetrieve!=null){resultFieldToRetrieveArr=  resultFieldToRetrieve.split("\\|");}
                    resultFieldToRetrieveArr = LPArray.addValueToArray1D(resultFieldToRetrieveArr, SampleAPIParams.MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|"));
                    sampleAnalysisWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME); 
                    sampleAnalysisWhereFieldsNameArr = new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()};
                    if ( (sampleAnalysisWhereFieldsName!=null) && (sampleAnalysisWhereFieldsName.length()>0) ) {
                        sampleAnalysisWhereFieldsNameArr=LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                    }     
                    Object[] sampleAnalysisWhereFieldsValueArr = new Object[]{sampleId};
                    sampleAnalysisWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE);                    
                    if ( (sampleAnalysisWhereFieldsValue!=null) && (sampleAnalysisWhereFieldsValue.length()>0) ) 
                        sampleAnalysisWhereFieldsValueArr=LPArray.addValueToArray1D(sampleAnalysisWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")));
                  
                    sortFieldsNameArr = null;
                    sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME); 
                    if (! ((sortFieldsName==null) || (sortFieldsName.contains(GlobalAPIsParams.REQUEST_PARAM_VALUE_UNDEFINED))) ) {
                        sortFieldsNameArr = sortFieldsName.split("\\|");                                    
                    }else{   
                        sortFieldsNameArr = SampleAPIParams.MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|");     
                    }  
                    resultFieldToRetrieveArr=LPArray.addValueToArray1D(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_LIMIT_ID.getName());
                    Integer posicLimitIdFld=resultFieldToRetrieveArr.length;
                    Object[][] analysisResultList = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(),
                            sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsValueArr, resultFieldToRetrieveArr, sortFieldsNameArr);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisResultList[0][0].toString())){  
                        Rdbms.closeRdbms();                                          
                        Object[] errMsg = LPFrontEnd.responseError(new String[] {Arrays.toString(LPArray.array2dTo1d(analysisResultList))}, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);                            
                    }else{                
                      JSONArray jArr=new JSONArray();
                      for (Object[] curRow: analysisResultList){
                        ConfigSpecRule specRule = new ConfigSpecRule();
                        String currRowLimitId=curRow[posicLimitIdFld-1].toString();
                        JSONObject row=LPJson.convertArrayRowToJSONObject(resultFieldToRetrieveArr, curRow);
                        if ((currRowLimitId!=null) && (currRowLimitId.length()>0) ){
                          specRule.specLimitsRule(Integer.valueOf(currRowLimitId) , null);                        
                          row.put(ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_DETAILED, specRule.getRuleRepresentation());                          
                        }
                        jArr.add(row);
                      }                        
                    Rdbms.closeRdbms();                    
                      LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    }                    
                    return;  
                case SAMPLES_PENDING_TESTINGGROUP_REVISION:
                    String testingGroup=argValues[0].toString();
                    myData = Rdbms.getRecordFieldsByFilterJSON(schemaDataName, TblsData.SampleRevisionTestingGroup.TBL.getName(),
                            new String[]{TblsData.SampleRevisionTestingGroup.FLD_READY_FOR_REVISION.getName(), TblsData.SampleRevisionTestingGroup.FLD_TESTING_GROUP.getName()},new Object[]{true, testingGroup}, 
                            new String[]{TblsData.SampleRevisionTestingGroup.FLD_SAMPLE_ID.getName(), TblsData.SampleRevisionTestingGroup.FLD_TESTING_GROUP.getName()}, 
                            new String[]{TblsData.SampleRevisionTestingGroup.FLD_SAMPLE_ID.getName(), TblsData.SampleRevisionTestingGroup.FLD_TESTING_GROUP.getName()});
                    Rdbms.closeRdbms();
                    if (myData.contains(LPPlatform.LAB_FALSE)){  
                        Object[] errMsg = LPFrontEnd.responseError(new String[] {myData}, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);                            
                    }else{
                        LPFrontEnd.servletReturnSuccess(request, response, myData);
                    }                             
                    return;
                case SAMPLES_PENDING_SAMPLE_REVISION:   
                    sampleFieldToRetrieve = argValues[0].toString();
                    sampleFieldToRetrieveArr = new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()};
                    if (sampleFieldToRetrieve!=null){
                        sampleFieldToRetrieveArr=LPArray.addValueToArray1D(sampleFieldToRetrieveArr, sampleFieldToRetrieve.split("\\|"));
                    }  
                    myData = Rdbms.getRecordFieldsByFilterJSON(schemaDataName, TblsData.Sample.TBL.getName(),
                            new String[]{TblsData.Sample.FLD_READY_FOR_REVISION.getName()},new Object[]{true}, 
                            new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, 
                            new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()});
                    Rdbms.closeRdbms();
                    if (myData.contains(LPPlatform.LAB_FALSE)){  
                        Object[] errMsg = LPFrontEnd.responseError(new String[] {myData}, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);                            
                    }else{
                        LPFrontEnd.servletReturnSuccess(request, response, myData);
                    }                             
/*
                    Object[][] mySamples = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(),
                        new String[]{TblsData.Sample.FLD_READY_FOR_REVISION.getName()},new Object[]{true},  
                        sampleFieldToRetrieveArr, new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()});
                    Rdbms.closeRdbms();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(mySamples[0][0].toString())){ 
                         Object[] errMsg = LPFrontEnd.responseError(new String[] {Arrays.toString(LPArray.array2dTo1d(mySamples))}, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);                            
                    }else{
                        JSONArray sampleArray=new JSONArray();
                        for (int xProc=0; xProc<mySamples.length; xProc++){
                            JSONObject sampleObj = new JSONObject();
                            for (int yProc=0; yProc<mySamples[0].length; yProc++){
                                if (mySamples[xProc][yProc] instanceof Timestamp){
                                    sampleObj.put(sampleFieldToRetrieveArr[yProc], mySamples[xProc][yProc].toString());
                                }
                                sampleObj.put(sampleFieldToRetrieveArr[yProc], mySamples[xProc][yProc]);
                            }
                            sampleArray.add(sampleObj); 
                        }                          
                        LPFrontEnd.servletReturnSuccess(request, response, sampleArray);                                   
                    }      */                       
                    return;
                case CHANGEOFCUSTODY_SAMPLE_HISTORY:     
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, SampleAPIParams.MANDATORY_PARAMS_FRONTEND_CHANGEOFCUSTODY_SAMPLE_HISTORY.split("\\|"));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                        LPFrontEnd.servletReturnResponseError(request, response, 
                                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                        return;                  
                    }                      
                    sampleIdStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID);                                                         
                    sampleId = Integer.parseInt(sampleIdStr);      

                    fieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE);                    
                    sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME);
                    
                    fieldToRetrieveArr = new String[0];
                    if (!( (fieldToRetrieve==null) || (fieldToRetrieve.length()==0) )){                      
                        fieldToRetrieveArr=LPArray.addValueToArray1D(fieldToRetrieveArr, fieldToRetrieve.split("\\|"));
                    }  
                    fieldToRetrieveArr = LPArray.addValueToArray1D(fieldToRetrieveArr, SampleAPIParams.MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_CHANGEOFCUSTODY_SAMPLE_HISTORY.split("\\|"));
                    
                    sortFieldsNameArr = null;
                    sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME); 
                    if (! ((sortFieldsName==null) || (sortFieldsName.contains(GlobalAPIsParams.REQUEST_PARAM_VALUE_UNDEFINED))) ) {
                        sortFieldsNameArr = sortFieldsName.split("\\|");                                    
                    }else{   
                        sortFieldsNameArr = SampleAPIParams.MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_CHANGEOFCUSTODY_SAMPLE_HISTORY.split("\\|");                    
                    }                      
                    myData = Rdbms.getRecordFieldsByFilterJSON(schemaDataName, VIEW_NAME_SAMPLE_COC_NAMES,
                            new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()},new Object[]{sampleId}, fieldToRetrieveArr, sortFieldsNameArr);
                    Rdbms.closeRdbms();
                    if (myData.contains(LPPlatform.LAB_FALSE)){  
                        Object[] errMsg = LPFrontEnd.responseError(new String[] {myData}, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);                            
                    }else{
                        LPFrontEnd.servletReturnSuccess(request, response, myData);
                    }                             
                    return;                      
                case CHANGEOFCUSTODY_USERS_LIST:

                    fieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE);                    
                    sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME);
                    
                    fieldToRetrieveArr = new String[0];
                    if (!( (fieldToRetrieve==null) || (fieldToRetrieve.length()==0) )){
                        fieldToRetrieveArr=LPArray.addValueToArray1D(fieldToRetrieveArr, fieldToRetrieve.split("\\|"));                
                    }   
                    fieldToRetrieveArr=LPArray.addValueToArray1D(fieldToRetrieveArr, SampleAPIParams.MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_CHANGEOFCUSTODY_USERS_LIST.split("\\|"));
                    sortFieldsNameArr = null;
                    sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME); 
                    if (! ((sortFieldsName==null) || (sortFieldsName.contains(GlobalAPIsParams.REQUEST_PARAM_VALUE_UNDEFINED))) ) {
                        sortFieldsNameArr = sortFieldsName.split("\\|");                                    
                    }else{   
                        sortFieldsNameArr=SampleAPIParams.MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_CHANGEOFCUSTODY_USERS_LIST.split("\\|"); 
                    }  
                    
                    myData = Rdbms.getRecordFieldsByFilterJSON(GlobalVariables.Schemas.APP.getName(), TblsApp.Users.TBL.getName(),
                            new String[]{TblsApp.Users.FLD_USER_NAME.getName()+" NOT IN|"},new Object[]{"0"}, fieldToRetrieveArr, sortFieldsNameArr);
                    Rdbms.closeRdbms();
                    if (myData.contains(LPPlatform.LAB_FALSE)){  
                        Object[] errMsg = LPFrontEnd.responseError(new String[] {myData}, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);                            
                    }else{
                        LPFrontEnd.servletReturnSuccess(request, response, myData);
                    }                             

                    return;                      
                case GET_SAMPLE_ANALYSIS_RESULT_SPEC:
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, SampleAPIParams.MANDATORY_PARAMS_FRONTEND_GET_SAMPLE_ANALYSIS_RESULT_SPEC.split("\\|"));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                        LPFrontEnd.servletReturnResponseError(request, response, 
                                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                        return;                  
                    }                      
                    // No implementado aun, seguramente no tiene sentido porque al final la spec está evaluada y guardada en la tabla sample_analysis_result
                    Rdbms.closeRdbms();
                    return;  
                case SAMPLE_ENTIRE_STRUCTURE:
                   sampleIdStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID);                     
                   String[] sampleIdStrArr=sampleIdStr.split("\\|");  
                   Object[] sampleIdArr=new Object[0];
                   for (String smp: sampleIdStrArr){
                       sampleIdArr=LPArray.addValueToArray1D(sampleIdArr,  Integer.parseInt(smp));
                   }

                   sampleFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE);
                   sampleAnalysisFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE);
                   String sampleAnalysisFieldToSort = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_SORT);
                   String sarFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE);
                   String sarFieldToSort = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_FIELD_TO_SORT);
                   String sampleAuditFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_AUDIT_FIELD_TO_RETRIEVE);
                   String sampleAuditResultFieldToSort = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_AUDIT_FIELD_TO_SORT);
                   

                    String jsonarrayf=DataSample.sampleEntireStructureData(procInstanceName, Integer.parseInt(sampleIdStr), sampleFieldToRetrieve, 
                            sampleAnalysisFieldToRetrieve, sampleAnalysisFieldToSort, sarFieldToRetrieve, sarFieldToSort, 
                            sampleAuditFieldToRetrieve, sampleAuditResultFieldToSort);
                    LPFrontEnd.servletReturnSuccess(request, response, jsonarrayf);
                    return;
                case GET_SAMPLE_AUDIT:
                   sampleIdStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID);                     
                   sampleId=Integer.valueOf(sampleIdStr);
                   sampleFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_AUDIT_FIELD_TO_RETRIEVE);
                   sampleFieldToRetrieveArr=new String[]{TblsDataAudit.Sample.FLD_SAMPLE_ID.getName(), TblsDataAudit.Sample.FLD_AUDIT_ID.getName(), TblsDataAudit.Sample.FLD_ACTION_NAME.getName(), TblsDataAudit.Sample.FLD_FIELDS_UPDATED.getName()
                    , TblsDataAudit.Sample.FLD_REVIEWED.getName(), TblsDataAudit.Sample.FLD_REVIEWED_ON.getName()};
                   Object[][] sampleAuditInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsDataAudit.Sample.TBL.getName(), 
                           new String[]{TblsDataAudit.Sample.FLD_SAMPLE_ID.getName(), TblsDataAudit.Sample.FLD_PARENT_AUDIT_ID.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, new Object[]{sampleId}, 
                           sampleFieldToRetrieveArr, new String[]{TblsDataAudit.Sample.FLD_AUDIT_ID.getName()});
                   JSONArray jArr = new JSONArray();
                   for (Object[] curRow: sampleAuditInfo){
                    JSONObject jObj=LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, curRow);
                    Integer curAuditId=Integer.valueOf(curRow[1].toString());
                        Object[][] sampleAuditInfoLvl2=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsDataAudit.Sample.TBL.getName(), 
                                new String[]{TblsDataAudit.Sample.FLD_PARENT_AUDIT_ID.getName()}, new Object[]{curAuditId}, 
                                sampleFieldToRetrieveArr, new String[]{TblsDataAudit.Sample.FLD_AUDIT_ID.getName()});
                        JSONArray jArrLvl2 = new JSONArray();
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAuditInfoLvl2[0][0].toString())){
                            JSONObject jObjLvl2=LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, new Object[]{null, null, "No child", "", "", ""}); 
                            jArrLvl2.add(jObjLvl2);
                        }else{
                            for (Object[] curRowLvl2: sampleAuditInfoLvl2){
                                JSONObject jObjLvl2=LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, curRowLvl2);  
                                jArrLvl2.add(jObjLvl2);
                            }
                        }
                        jObj.put("sublevel", jArrLvl2);
                    jArr.add(jObj);
                   }
                   LPFrontEnd.servletReturnSuccess(request, response, jArr);
                   return;
                case GET_METHOD_CERTIFIED_USERS_LIST:
                    String methodName=argValues[0].toString();
                    Object[] analysisMethodCertifiedUsersList = analysisMethodCertifiedUsersList(methodName, null, null);
                    jArr = new JSONArray();
                    String[] fldNames=(String[])analysisMethodCertifiedUsersList[0];
                    Object[][] dataValues=(Object[][])analysisMethodCertifiedUsersList[1];
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(dataValues[0][0].toString())){
                        for (Object[] curRow: dataValues){      
                            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fldNames, curRow);
                            jArr.add(jObj);
                        }
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                default:      
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);                              
                }                     
        }catch(Exception e){      
            String exceptionMessage =e.getMessage();
            if (exceptionMessage==null){exceptionMessage="null exception";}
            response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null);      
         } finally {
            // release database resources
            try {
                procReqInstance.killIt();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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