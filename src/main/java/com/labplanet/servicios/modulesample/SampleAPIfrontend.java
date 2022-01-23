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
import databases.SqlStatement;
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

    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    
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
                        if (LPPlatform.isEncryptedField(procInstanceName, GlobalVariables.Schemas.DATA.getName(), TblsData.Sample.TBL.getName(), whereFieldsNameArr[iFields])){                
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

                whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLE_ANALYSIS_READY_FOR_REVISION.getName());
                if (whereFieldsName!=null && whereFieldsName.length()>0)whereFieldsNameArr=LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
                
                if (whereFieldsValue==null || whereFieldsValue.length()==0)whereFieldsValue="true*Boolean";
                else whereFieldsValue="true*Boolean|"+whereFieldsValue;
                whereFieldsValueArr=LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|"));
                
                
                String sampleAnalysisFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE); 
                if (sampleAnalysisFieldToRetrieve!=null && sampleAnalysisFieldToRetrieve.length()>0) sampleAnalysisFieldToRetrieveArr=sampleAnalysisFieldToRetrieve.split("\\|");
                else sampleAnalysisFieldToRetrieveArr=TblsData.ViewSampleAnalysisResultWithSpecLimits.getAllFieldNames();                
                String sampleAnalysisSortField = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME); 
                if (sampleAnalysisSortField!=null && sampleAnalysisSortField.length()>0) sampleAnalysisSortFieldArr=sampleAnalysisSortField.split("\\|");
                
                Object[][] smplsAnaData = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(),
                    whereFieldsNameArr, whereFieldsValueArr, sampleAnalysisFieldToRetrieveArr, sampleAnalysisSortFieldArr);
                JSONArray smplAnaJsArr= new JSONArray();
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(smplsAnaData[0][0].toString()))
                    LPFrontEnd.servletReturnSuccess(request, response, smplAnaJsArr); 
                for (Object[] curSmpAna: smplsAnaData){
                    smplAnaJsArr.add(LPJson.convertArrayRowToJSONObject(sampleAnalysisFieldToRetrieveArr, curSmpAna));
                }
                Rdbms.closeRdbms();
                LPFrontEnd.servletReturnSuccess(request, response, smplAnaJsArr);       
                return;
            case SAMPLES_AND_RESULTS_VIEW:
                whereFieldsNameArr=new String[]{};
                whereFieldsValueArr=new Object[]{};
                String[] fieldToRetrieveArr=new String[]{};
                whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME); 
                if (whereFieldsName==null){whereFieldsName="";}
                whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE); 
                String fieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE); 
                if ( (whereFieldsName!=null) && (whereFieldsValue!=null) ){
                    whereFieldsNameArr=LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
                    whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|")));                                          
                    for (int iFields=0; iFields<whereFieldsNameArr.length; iFields++){
                        if (LPPlatform.isEncryptedField(procInstanceName, GlobalVariables.Schemas.DATA.getName(), TblsData.Sample.TBL.getName(), whereFieldsNameArr[iFields])){                
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
                if (fieldToRetrieve==null || fieldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(fieldToRetrieve))
                    fieldToRetrieveArr=TblsData.ViewSampleAnalysisResultWithSpecLimits.getAllFieldNames();
                else
                    fieldToRetrieveArr=fieldToRetrieve.split("\\|");
                sortFieldsNameArr = null;
                sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME); 
                if (! ((sortFieldsName==null) || (sortFieldsName.contains(GlobalAPIsParams.REQUEST_PARAM_VALUE_UNDEFINED))) ) {
                    sortFieldsNameArr = sortFieldsName.split("\\|");                                    
                }else{   sortFieldsNameArr=null;}  
                for (int iFldV=0;iFldV<whereFieldsValueArr.length; iFldV++){                  
                  if (whereFieldsValueArr[iFldV].toString().equalsIgnoreCase("false")){whereFieldsValueArr[iFldV]=Boolean.valueOf(whereFieldsValueArr[iFldV].toString());}
                  if (whereFieldsValueArr[iFldV].toString().equalsIgnoreCase("true")){whereFieldsValueArr[iFldV]=Boolean.valueOf(whereFieldsValueArr[iFldV].toString());}
                }
                Object[][] mySamples = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(),
                    whereFieldsNameArr, whereFieldsValueArr, fieldToRetrieveArr, sortFieldsNameArr);
                if (mySamples==null){ 
                    LPFrontEnd.servletReturnSuccess(request, response);       
                    return;
                }
                JSONArray myJSArr = new JSONArray();
                Rdbms.closeRdbms();
                if ( LPPlatform.LAB_FALSE.equalsIgnoreCase(mySamples[0][0].toString())) {  
                    LPFrontEnd.servletReturnSuccess(request, response, myJSArr);       
                    return;
                }else{                        
                    for (Object[] mySample : mySamples) {
                        JSONObject myJSObj = LPJson.convertArrayRowToJSONObject(fieldToRetrieveArr, mySample); 
                        myJSArr.add(myJSObj);
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, myJSArr);                    
                    return;
                }                
                //JSONObject jsonObj = new JSONObject();
                //jsonObj.put("samples", samplesArray);   
                //LPFrontEnd.servletReturnSuccess(request, response, myJSArr);                    
                
            case SAMPLES_BY_STAGE:   
            case SAMPLES_INPROGRESS_LIST:   
                whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME); 
                if (whereFieldsName==null){whereFieldsName="";}
                whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE); 

                sampleFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE); 
                if (sampleFieldToRetrieve==null || sampleFieldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(sampleFieldToRetrieve))
                    sampleFieldToRetrieveArr=TblsData.Sample.getAllFieldNames();
                else 
                    sampleFieldToRetrieveArr=sampleFieldToRetrieve.split("\\|");
                
                String sampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE); 
                String sampleLastLevel = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL);                 
                
                String addSampleAnalysis = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS); 
                if (addSampleAnalysis==null){addSampleAnalysis="false";}
                sampleAnalysisFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE);                
                if (sampleAnalysisFieldToRetrieve==null || sampleAnalysisFieldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(sampleAnalysisFieldToRetrieve))
                    sampleAnalysisFieldToRetrieveArr=TblsData.SampleAnalysis.getAllFieldNames();
                else 
                    sampleAnalysisFieldToRetrieveArr=sampleAnalysisFieldToRetrieve.split("\\|");
                String sampleAnalysisWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME); 
                String[] sampleAnalysisWhereFieldsNameArr = new String[0];
                if ( (sampleAnalysisWhereFieldsName!=null) && (sampleAnalysisWhereFieldsName.length()>0) ) {
                    sampleAnalysisWhereFieldsNameArr=LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                }                                
                String sampleAnalysisWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE); 

                String addSampleAnalysisResult = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT); 
                if (addSampleAnalysisResult==null){addSampleAnalysisResult="false";}
                sampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE); 
                String[] sampleAnalysisResultFieldToRetrieveArr=null;
                if (sampleAnalysisResultFieldToRetrieve==null || sampleAnalysisResultFieldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(sampleAnalysisResultFieldToRetrieve))
                    sampleAnalysisResultFieldToRetrieveArr=TblsData.SampleAnalysisResult.getAllFieldNames();
                else 
                    sampleAnalysisResultFieldToRetrieveArr=sampleAnalysisResultFieldToRetrieve.split("\\|");
                String sampleAnalysisResultWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME); 
                String[] sampleAnalysisResultWhereFieldsNameArr = new String[0];
                if ( (sampleAnalysisResultWhereFieldsName!=null) && (sampleAnalysisResultWhereFieldsName.length()>0) ) {
                    sampleAnalysisResultWhereFieldsNameArr=LPArray.addValueToArray1D(sampleAnalysisResultWhereFieldsNameArr, sampleAnalysisResultWhereFieldsName.split("\\|"));
                }                                
                String sampleAnalysisResultWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE); 
                
                sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME);  
                JSONArray samplesArray = samplesByStageData(sampleLastLevel, sampleFieldToRetrieveArr, whereFieldsName, 
                        whereFieldsValue, sortFieldsName,
                        addSampleAnalysis, sampleAnalysisFieldToRetrieveArr, sampleAnalysisWhereFieldsName, sampleAnalysisWhereFieldsValue,
                        addSampleAnalysisResult,
                        sampleAnalysisResultFieldToRetrieveArr, sampleAnalysisResultWhereFieldsName, sampleAnalysisResultWhereFieldsValue);

                LPFrontEnd.servletReturnSuccess(request, response, samplesArray);                    
                return;                                        
            case ANALYSIS_ALL_LIST:          
                    fieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE); 
                    fieldToRetrieveArr = new String[0];
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

                    String myData = Rdbms.getRecordFieldsByFilterJSON(schemaConfigName, TblsCnfg.ViewAnalysisMethodsView.TBL.getName(),
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
                        LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());
//                        Object[] errMsg = LPFrontEnd.responseError(new String[] {Arrays.toString(LPArray.array2dTo1d(analysisResultList))}, language, null);
//                        response.sendError((int) errMsg[0], (String) errMsg[1]);                            
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
                    fieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE); 
                    if (fieldToRetrieve==null || fieldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(fieldToRetrieve))
                        fieldToRetrieveArr=TblsData.ViewSampleTestingGroup.getAllFieldNames();
                    else
                        fieldToRetrieveArr=fieldToRetrieve.split("\\|");
                    
                    myData = Rdbms.getRecordFieldsByFilterJSON(schemaDataName, TblsData.ViewSampleTestingGroup.TBL.getName(),
                        new String[]{TblsData.ViewSampleTestingGroup.FLD_READY_FOR_REVISION.getName(), TblsData.ViewSampleTestingGroup.FLD_REVIEWED.getName(), TblsData.ViewSampleTestingGroup.FLD_TESTING_GROUP.getName()},
                        new Object[]{true, false, testingGroup}, 
                        fieldToRetrieveArr,
                        new String[]{TblsData.ViewSampleTestingGroup.FLD_SAMPLE_ID.getName(), TblsData.ViewSampleTestingGroup.FLD_TESTING_GROUP.getName()});
                    Rdbms.closeRdbms();
                    if (myData.contains(LPPlatform.LAB_FALSE)){  
                        LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());
                        return;
//                        Object[] errMsg = LPFrontEnd.responseError(new String[] {myData}, language, null);
//                        response.sendError((int) errMsg[0], (String) errMsg[1]);                            
                    }else{
                        LPFrontEnd.servletReturnSuccess(request, response, myData);
                    }                             
                    return;
                case SAMPLES_PENDING_SAMPLE_REVISION:   
                    sampleFieldToRetrieve = argValues[0].toString();
                    sampleFieldToRetrieveArr = new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()};
                    
                    if ((sampleFieldToRetrieve==null) || (sampleFieldToRetrieve.length()==0) || ("ALL".equalsIgnoreCase(sampleFieldToRetrieve)) )
                        sampleFieldToRetrieveArr=TblsData.Sample.getAllFieldNames();
                    else
                        sampleFieldToRetrieveArr=LPArray.addValueToArray1D(sampleFieldToRetrieveArr, sampleFieldToRetrieve.split("\\|"));
                      
                    myData = Rdbms.getRecordFieldsByFilterJSON(schemaDataName, TblsData.Sample.TBL.getName(),
                            new String[]{TblsData.Sample.FLD_READY_FOR_REVISION.getName(), "("+TblsData.Sample.FLD_REVIEWED.getName(), SqlStatement.WHERECLAUSE_TYPES.OR.getSqlClause()+" "+TblsData.Sample.FLD_REVIEWED.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()+")"},
                            new Object[]{true, false, null}, 
                            sampleFieldToRetrieveArr, 
                            new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()});
                    Rdbms.closeRdbms();
                    if (myData.contains(LPPlatform.LAB_FALSE)){  
                        LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());
                        return;
//                        Object[] errMsg = LPFrontEnd.responseError(new String[] {myData}, language, null);
//                        response.sendError((int) errMsg[0], (String) errMsg[1]);                            
                    }else{
                        LPFrontEnd.servletReturnSuccess(request, response, myData);
                    }                             
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
                    myData = Rdbms.getRecordFieldsByFilterJSON(schemaDataName, TblsData.ViewSampleCocNames.TBL.getName(),
                            new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()},new Object[]{sampleId}, fieldToRetrieveArr, sortFieldsNameArr);
                    Rdbms.closeRdbms();
                    if (myData.contains(LPPlatform.LAB_FALSE)){  
                        LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());
                        return;
//                        Object[] errMsg = LPFrontEnd.responseError(new String[] {myData}, language, null);
//                        response.sendError((int) errMsg[0], (String) errMsg[1]);                            
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
                        LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());
                        return;
//                        Object[] errMsg = LPFrontEnd.responseError(new String[] {myData}, language, null);
//                        response.sendError((int) errMsg[0], (String) errMsg[1]);                            
                    }else{
                        LPFrontEnd.servletReturnSuccess(request, response, myData);
                    }                             

                    return;                      
                case GET_SAMPLE_ANALYSIS_RESULT_SPEC:
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, SampleAPIParams.MANDATORY_PARAMS_FRONTEND_GET_SAMPLE_ANALYSIS_RESULT_SPEC.split("\\|"));
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                        LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());
                        return;
/*                        LPFrontEnd.servletReturnResponseError(request, response, 
                                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                        return;                  */
                    }                      
                    // No implementado aun, seguramente no tiene sentido porque al final la spec está evaluada y guardada en la tabla sample_analysis_result
                    // Rdbms.closeRdbms();
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
                    Rdbms.closeRdbms();                
                    LPFrontEnd.servletReturnSuccess(request, response, jsonarrayf);
                    return;
                case GET_SAMPLE_AUDIT:
                   sampleIdStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID);                     
                   sampleId=Integer.valueOf(sampleIdStr);
                   sampleFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_AUDIT_FIELD_TO_RETRIEVE);
                   sampleFieldToRetrieveArr=new String[]{TblsDataAudit.Sample.FLD_SAMPLE_ID.getName(), TblsDataAudit.Sample.FLD_AUDIT_ID.getName(), TblsDataAudit.Sample.FLD_ACTION_NAME.getName(), TblsDataAudit.Sample.FLD_FIELDS_UPDATED.getName()
                    , TblsDataAudit.Sample.FLD_REVIEWED.getName(), TblsDataAudit.Sample.FLD_REVIEWED_ON.getName(), TblsDataAudit.Sample.FLD_DATE.getName(), TblsDataAudit.Sample.FLD_PERSON.getName(), TblsDataAudit.Sample.FLD_REASON.getName(), TblsDataAudit.Sample.FLD_ACTION_PRETTY_EN.getName(), TblsDataAudit.Sample.FLD_ACTION_PRETTY_ES.getName()};
                   Object[][] sampleAuditInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsDataAudit.Sample.TBL.getName(), 
                           new String[]{TblsDataAudit.Sample.FLD_SAMPLE_ID.getName(), TblsDataAudit.Sample.FLD_PARENT_AUDIT_ID.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, new Object[]{sampleId}, 
                           sampleFieldToRetrieveArr, new String[]{TblsDataAudit.Sample.FLD_AUDIT_ID.getName()});
                   JSONArray jArr = new JSONArray();
                   for (Object[] curRow: sampleAuditInfo){
                    JSONObject jObj=LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, curRow,
                            new String[]{TblsDataAudit.Sample.FLD_FIELDS_UPDATED.getName()});
                    Object[] convertToJsonObjectStringedObject = LPJson.convertToJsonObjectStringedObject(curRow[LPArray.valuePosicInArray(sampleFieldToRetrieveArr, TblsDataAudit.Sample.FLD_FIELDS_UPDATED.getName())].toString());
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(convertToJsonObjectStringedObject[0].toString()))
                        jObj.put(TblsDataAudit.Sample.FLD_FIELDS_UPDATED.getName(), convertToJsonObjectStringedObject[1]);            
                    Integer curAuditId=Integer.valueOf(curRow[1].toString());
                        Object[][] sampleAuditInfoLvl2=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsDataAudit.Sample.TBL.getName(), 
                                new String[]{TblsDataAudit.Sample.FLD_PARENT_AUDIT_ID.getName()}, new Object[]{curAuditId}, 
                                sampleFieldToRetrieveArr, new String[]{TblsDataAudit.Sample.FLD_AUDIT_ID.getName()});
                        JSONArray jArrLvl2 = new JSONArray();
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAuditInfoLvl2[0][0].toString())){
                            Object[] childJObj=new Object[]{null, null, "No child", "", "", "", null, "", "", null, null};
                            for (int iChild=childJObj.length;iChild<sampleFieldToRetrieveArr.length;iChild++)
                                childJObj=LPArray.addValueToArray1D(childJObj, null);                            
                            JSONObject jObjLvl2=LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, childJObj); 
                            jArrLvl2.add(jObjLvl2);
                        }else{
                            for (Object[] curRowLvl2: sampleAuditInfoLvl2){
                                JSONObject jObjLvl2=LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, curRowLvl2,
                                    new String[]{TblsDataAudit.Sample.FLD_FIELDS_UPDATED.getName()});  
                                Object[] convertToJsonObjectStringedObjectLvl2 = LPJson.convertToJsonObjectStringedObject(curRowLvl2[LPArray.valuePosicInArray(sampleFieldToRetrieveArr, TblsDataAudit.Sample.FLD_FIELDS_UPDATED.getName())].toString());
                                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(convertToJsonObjectStringedObjectLvl2[0].toString()))
                                    jObjLvl2.put(TblsDataAudit.Sample.FLD_FIELDS_UPDATED.getName(), convertToJsonObjectStringedObjectLvl2[1]);            
                                jArrLvl2.add(jObjLvl2);
                            }
                        }
                        jObj.put("sublevel", jArrLvl2);
                    jArr.add(jObj);
                   }
                   Rdbms.closeRdbms();
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
                    Rdbms.closeRdbms();
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

           
    public static JSONArray samplesByStageData(String sampleLastLevel, String[] sampleFieldToRetrieveArr, String whereFieldsName, String whereFieldsValue, String sortFieldsName,
        String addSampleAnalysis, String[] sampleAnalysisFieldToRetrieveArr, String sampleAnalysisWhereFieldsName, String sampleAnalysisWhereFieldsValue,
        String addSampleAnalysisResult, String[] sampleAnalysisResultFieldToRetrieveArr, String sampleAnalysisResultWhereFieldsName, String sampleAnalysisResultWhereFieldsValue){
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false);        
        String procInstanceName = procReqInstance.getProcedureInstance();
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());    

    if (sampleLastLevel==null){
        sampleLastLevel=TblsData.Sample.TBL.getName();
    }                                
    if (sampleFieldToRetrieveArr==null || sampleFieldToRetrieveArr[0].length()==0)
        sampleFieldToRetrieveArr = new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()};

    if (sampleAnalysisFieldToRetrieveArr==null || sampleAnalysisFieldToRetrieveArr[0].length()==0)
        sampleAnalysisFieldToRetrieveArr = new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()};

    if (sampleAnalysisFieldToRetrieveArr==null || sampleAnalysisFieldToRetrieveArr[0].length()==0)
        sampleAnalysisFieldToRetrieveArr = new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()};

    String[] whereFieldsNameArr = null; 
    Object[] whereFieldsValueArr = null; 
    
    if ( (whereFieldsName!=null) && (whereFieldsValue!=null) && whereFieldsName.length()>0 ){
        whereFieldsNameArr=LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
        whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|")));                                          
        for (int iFields=0; iFields<whereFieldsNameArr.length; iFields++){
            if (LPPlatform.isEncryptedField(procInstanceName, GlobalVariables.Schemas.DATA.getName(), TblsData.Sample.TBL.getName(), whereFieldsNameArr[iFields])){                
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
    String[]     sortFieldsNameArr = null;
    if (! ((sortFieldsName==null || sortFieldsName.length()==0) || (sortFieldsName.contains(GlobalAPIsParams.REQUEST_PARAM_VALUE_UNDEFINED))) ) 
        sortFieldsNameArr = sortFieldsName.split("\\|");                                    
    else   
        sortFieldsNameArr=null;    
    if (whereFieldsValueArr!=null){
        for (int iFldV=0;iFldV<whereFieldsValueArr.length; iFldV++){                  
          if (whereFieldsValueArr[iFldV].toString().equalsIgnoreCase("false")){whereFieldsValueArr[iFldV]=Boolean.valueOf(whereFieldsValueArr[iFldV].toString());}
          if (whereFieldsValueArr[iFldV].toString().equalsIgnoreCase("true")){whereFieldsValueArr[iFldV]=Boolean.valueOf(whereFieldsValueArr[iFldV].toString());}
        }
    }
    if (TblsData.Sample.TBL.getName().equals(sampleLastLevel)){ 
        Object[][] mySamples = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(),
            whereFieldsNameArr, whereFieldsValueArr, sampleFieldToRetrieveArr, sortFieldsNameArr);
        if (mySamples==null){ 
            return new JSONArray();
        }
        if ( LPPlatform.LAB_FALSE.equalsIgnoreCase(mySamples[0][0].toString())) {  
            return new JSONArray();
        }else{                        
            JSONArray mySamplesJSArr = new JSONArray();
            for (Object[] mySample : mySamples) {
                JSONObject mySampleJSObj = LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, mySample);                
                if ("TRUE".equalsIgnoreCase(addSampleAnalysis)){
                    String[] testWhereFieldsNameArr = new String[]{TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName()};
                    testWhereFieldsNameArr=LPArray.addValueToArray1D(testWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));                                
                    Integer sampleIdPosicInArray = LPArray.valuePosicInArray(sampleFieldToRetrieveArr, TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName());
                    Object[] testWhereFieldsValueArr = new Object[]{Integer.parseInt(mySample[sampleIdPosicInArray].toString())};
                    testWhereFieldsValueArr=LPArray.addValueToArray1D(testWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")));
                    if ("TRUE".equalsIgnoreCase(addSampleAnalysisResult))
                        sampleAnalysisFieldToRetrieveArr=LPArray.addValueToArray1D(sampleAnalysisFieldToRetrieveArr, TblsData.SampleAnalysis.FLD_TEST_ID.getName());
                    Object[][] mySampleAnalysis = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(),
                            testWhereFieldsNameArr, testWhereFieldsValueArr, sampleAnalysisFieldToRetrieveArr);
                    JSONArray mySamplesAnaJSArr = new JSONArray();
                    if ( LPPlatform.LAB_FALSE.equalsIgnoreCase(mySampleAnalysis[0][0].toString()) ){
                        mySampleJSObj.put(TblsData.SampleAnalysis.TBL.getName(), mySamplesAnaJSArr);
                    }else{                                    
                        for (Object[] mySampleAnalysi : mySampleAnalysis) {
                            JSONObject mySampleAnaJSObj = LPJson.convertArrayRowToJSONObject(sampleAnalysisFieldToRetrieveArr, mySampleAnalysi);
                            if ("TRUE".equalsIgnoreCase(addSampleAnalysisResult)){
                                String[] sarWhereFieldsNameArr = new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()};
                                if (sampleAnalysisResultWhereFieldsName!=null)
                                    sarWhereFieldsNameArr=LPArray.addValueToArray1D(sarWhereFieldsNameArr, sampleAnalysisResultWhereFieldsName);
                                Integer testIdPosicInArray = LPArray.valuePosicInArray(sampleAnalysisFieldToRetrieveArr, TblsData.SampleAnalysis.FLD_TEST_ID.getName());
                                Object[] sarWhereFieldsValueArr = new Object[]{Integer.parseInt(mySampleAnalysi[testIdPosicInArray].toString())};
                                if (sampleAnalysisResultWhereFieldsValue!=null)
                                    sarWhereFieldsValueArr=LPArray.addValueToArray1D(sarWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(LPNulls.replaceNull(sampleAnalysisResultWhereFieldsValue).split("\\|")));                                            

                                Object[][] mySampleAnalysisResults = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysisResult.TBL.getName(),
                                        sarWhereFieldsNameArr, sarWhereFieldsValueArr, sampleAnalysisResultFieldToRetrieveArr);          
                                JSONArray mySamplesAnaResJSArr = new JSONArray();
                                if ( LPPlatform.LAB_FALSE.equalsIgnoreCase(mySampleAnalysisResults[0][0].toString()) ){
                                    mySampleAnaJSObj.put(TblsData.SampleAnalysisResult.TBL.getName(), mySamplesAnaResJSArr);                                        
                                }
                                JSONObject mySampleAnaResJSObj = new JSONObject();
                                for (Object[] mySampleAnalysisResult : mySampleAnalysisResults) {
                                    mySampleAnaResJSObj = LPJson.convertArrayRowToJSONObject(sampleAnalysisResultFieldToRetrieveArr, mySampleAnalysisResult);
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
            return mySamplesJSArr;
        }
    }else{                    
        whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, "sample_id is not null");
        whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, "");
        JSONArray samplesArray = new JSONArray();    
        JSONArray sampleArray = new JSONArray();    
            Object[][] mySamples = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.Sample.TBL.getName(),
                    whereFieldsNameArr, whereFieldsValueArr, sampleFieldToRetrieveArr);
        if ( LPPlatform.LAB_FALSE.equalsIgnoreCase(mySamples[0][0].toString()) ){
            return new JSONArray();
        }
        for (Object[] currSample: mySamples){
            Integer sampleId = Integer.valueOf(currSample[0].toString());
            JSONObject sampleObj = LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, currSample);                            
            if ( ("TEST".equals(sampleLastLevel)) || ("RESULT".equals(sampleLastLevel)) ) {
                String[] testWhereFieldsNameArr = new String[]{TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName()};
                Object[] testWhereFieldsValueArr = new Object[]{sampleId};
                Object[][] mySampleAnalysis = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.SampleAnalysis.TBL.getName(),
                        testWhereFieldsNameArr, testWhereFieldsValueArr, sampleAnalysisFieldToRetrieveArr);          
                for (Object[] mySampleAnalysi : mySampleAnalysis) {
                    JSONObject testObj = new JSONObject();
                    for (int ySmpAna = 0; ySmpAna<mySampleAnalysis[0].length; ySmpAna++) {
                        if (mySampleAnalysi[ySmpAna] instanceof Timestamp) {
                            testObj.put(sampleAnalysisFieldToRetrieveArr[ySmpAna], mySampleAnalysi[ySmpAna].toString());
                        } else {
                            testObj.put(sampleAnalysisFieldToRetrieveArr[ySmpAna], mySampleAnalysi[ySmpAna]);
                        }
                    }      
                    sampleArray.add(testObj);
                }
                sampleObj.put(TblsData.SampleAnalysis.TBL.getName(), sampleArray);
            }
            sampleArray.add(sampleObj);                        
        }
        samplesArray.add(sampleArray);
        return samplesArray;
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