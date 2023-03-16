/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import com.labplanet.servicios.moduleenvmonit.*;
import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.moduleenvmonit.ClassEnvMonSampleFrontend.samplesByStageData;
import com.labplanet.servicios.modulesample.SampleAPIParams.SampleAPIqueriesEndpoints;
import static com.labplanet.servicios.modulesample.SampleAPIfrontend.sampleAnalysisResultView;
import static com.labplanet.servicios.modulesample.SampleAPIfrontend.sampleAnalysisView;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import databases.TblsProcedure;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction;
import static functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction.isProgramCorrectiveActionEnable;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.samplestructure.DataSampleStructureStatuses.SampleStatuses;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntMessages;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntViewFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;

/**
 *
 * @author User
 */
public class ClassSampleQueries {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;
    private Boolean isSuccess=false;
    private JSONObject responseSuccessJObj=null;
    private JSONArray responseSuccessJArr=null;
    private Object[] responseError=null;

    private static final String[] SAMPLEANALYSISRESULTLOCKDATA_RETRIEVEDATA_PROGRAMCORRECTIVEACTION=new String[]{TblsProcedure.ProgramCorrectiveAction.RESULT_ID.getName(), TblsProcedure.ProgramCorrectiveAction.STATUS.getName()};
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

    public ClassSampleQueries(HttpServletRequest request, SampleAPIqueriesEndpoints endPoint){
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();

        Object[] actionDiagnoses = null;
            this.functionFound=true;
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            this.functionFound=true;
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
                this.isSuccess=false;           
                this.responseError=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, 
                        (EnumIntMessages)argValues[1] , new Object[]{argValues[2].toString()});
                this.messageDynamicData=new Object[]{argValues[2].toString()};
                this.diagnostic=this.responseError;
                return;                        
            }            
            switch (endPoint){
                case GET_SAMPLE_ANALYSIS_RESULT_LIST:
                    Integer sampleId = Integer.valueOf(LPNulls.replaceNull(argValues[0]).toString());
                    String[] resultFieldToRetrieveArr=EnumIntViewFields.getAllFieldNames(EnumIntViewFields.getViewFieldsFromString(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, "ALL"));
                    EnumIntViewFields[] fldsToGet= EnumIntViewFields.getViewFieldsFromString(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, "ALL");
                    resultFieldToRetrieveArr = LPArray.getUniquesArray(LPArray.addValueToArray1D(resultFieldToRetrieveArr, SampleAPIParams.MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|")));
                    
                    String[] sampleAnalysisWhereFieldsNameArr = new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName()};
                    Object[] sampleAnalysisWhereFieldsValueArr = new Object[]{sampleId};

                    String sampleAnalysisWhereFieldsName = LPNulls.replaceNull(argValues[2]).toString();
                    if ( (sampleAnalysisWhereFieldsName!=null ) && (sampleAnalysisWhereFieldsName.length()>0) ) 
                        sampleAnalysisWhereFieldsNameArr=LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                    String sampleAnalysisWhereFieldsValue = LPNulls.replaceNull(argValues[3]).toString();
                    if ( (sampleAnalysisWhereFieldsValue!=null) && (sampleAnalysisWhereFieldsValue.length()>0) )
                        sampleAnalysisWhereFieldsValueArr=LPArray.addValueToArray1D(sampleAnalysisWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")));

                    String sarWhereFieldsName = LPNulls.replaceNull(argValues[4]).toString();
                    if ( (sarWhereFieldsName!=null ) && (sarWhereFieldsName.length()>0) ) 
                        sampleAnalysisWhereFieldsNameArr=LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sarWhereFieldsName.split("\\|"));
                    String sarWhereFieldsValue = LPNulls.replaceNull(argValues[5]).toString();
                    if ( (sarWhereFieldsValue!=null) && (sarWhereFieldsValue.length()>0) )
                        sampleAnalysisWhereFieldsValueArr=LPArray.addValueToArray1D(sampleAnalysisWhereFieldsValueArr, (
                                sampleAnalysisWhereFieldsValue!=null ?LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")) : new Object[]{}));
                    
                    String[] sortFieldsNameArr = null;
                    String sortFieldsName = LPNulls.replaceNull(argValues[6]).toString();
                    if ( (sortFieldsName!=null) && (sortFieldsName.length()>0) ) 
                        sortFieldsNameArr = sortFieldsName.split("\\|");                                    
                    else
                        sortFieldsNameArr = LPArray.getUniquesArray(SampleAPIParams.MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|"));
                    
                    Integer posicRawValueFld=LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE.getName());
                    if (posicRawValueFld==-1){
                        resultFieldToRetrieveArr=LPArray.addValueToArray1D(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE.getName());
                        posicRawValueFld=resultFieldToRetrieveArr.length;
                    }
                    Integer posicLimitIdFld=EnumIntViewFields.getFldPosicInArray(fldsToGet, TblsData.ViewSampleAnalysisResultWithSpecLimits.LIMIT_ID.getName());
                    
                    Object[][] analysisResultList=QueryUtilitiesEnums.getViewData(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, 
                        fldsToGet,
                        new SqlWhere(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsValueArr),
                        sortFieldsNameArr);     
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisResultList[0][0].toString())){  
                        // Rdbms.closeRdbms();   
                        this.isSuccess=true;
                        this.responseSuccessJArr=new JSONArray();                       
                    }else{    
                       rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), sampleId);
                        Object[] objectsIds=getObjectsId(EnumIntViewFields.getAllFieldNames(fldsToGet), analysisResultList, "-");
                        for (Object curObj: objectsIds){
                            String[] curObjDet=curObj.toString().split("-");
                            if (TblsData.SampleAnalysisResult.TEST_ID.getName().equalsIgnoreCase(curObjDet[0]))
                                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), curObjDet[1]);
                            if (TblsData.SampleAnalysisResult.RESULT_ID.getName().equalsIgnoreCase(curObjDet[0]))
                                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), curObjDet[1]);
                        }
                        JSONArray jArr=new JSONArray();
                        for (Object[] curRow: analysisResultList){
                            ConfigSpecRule specRule = new ConfigSpecRule();
                            String currRowRawValue=curRow[posicRawValueFld].toString();
                            String currRowLimitId=curRow[posicLimitIdFld].toString();
                            JSONObject row=new JSONObject();

                            Object[] resultLockData=sampleAnalysisResultLockData(EnumIntViewFields.getAllFieldNames(fldsToGet), curRow);
                            if (resultLockData!=null && resultLockData[0]!=null){
                                if (resultLockData.length>2){
                                    row=LPJson.convertArrayRowToJSONObject(LPArray.addValueToArray1D(LPArray.addValueToArray1D(EnumIntViewFields.getAllFieldNames(fldsToGet), (String)resultLockData[2]), (String[]) resultLockData[0]), 
                                            LPArray.addValueToArray1D(LPArray.addValueToArray1D(curRow, resultLockData[3]), (Object[]) resultLockData[1]));
                                }else{
                                    row=LPJson.convertArrayRowToJSONObject(LPArray.addValueToArray1D(EnumIntViewFields.getAllFieldNames(fldsToGet), (String[]) resultLockData[0]), LPArray.addValueToArray1D(curRow, (Object[]) resultLockData[1]));
                                }
                            }else{
                                row=LPJson.convertArrayRowToJSONObject(EnumIntViewFields.getAllFieldNames(fldsToGet), curRow);
                            }
                            if ((currRowLimitId!=null) && (currRowLimitId.length()>0) ){
                            specRule.specLimitsRule(Integer.valueOf(currRowLimitId) , null);                        
                            row.put(ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_DETAILED, LPNulls.replaceNull(specRule.getRuleRepresentation()).replace(("R"), "R ("+currRowRawValue+")"));
                            Object[][] specRuleDetail=specRule.getRuleData();
                            JSONArray specRuleDetailjArr=new JSONArray();
                            JSONObject specRuleDetailjObj=new JSONObject();
                            for (Object[] curSpcRlDet: specRuleDetail){
                                specRuleDetailjObj.put(curSpcRlDet[0], curSpcRlDet[1]);                              
                            }
                            specRuleDetailjArr.add(specRuleDetailjObj);
                            row.put(ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_INFO, specRuleDetailjArr);
                        }
                        jArr.add(row);
                      }                        
                    Rdbms.closeRdbms(); 
                    this.isSuccess=true;
                    this.responseSuccessJArr=jArr;                                        
                    }                    
                    return;    
            case SAMPLES_VIEW:  
                String[] fieldToRetrieveArr;
                String whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME); 
                if (whereFieldsName==null){whereFieldsName="";}
                String whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE); 

                String sampleFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE); 
                if (sampleFieldToRetrieve==null || sampleFieldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(sampleFieldToRetrieve))
                    fieldToRetrieveArr=getAllFieldNames(TblsData.TablesData.SAMPLE.getTableFields());
                else 
                    fieldToRetrieveArr=sampleFieldToRetrieve.split("\\|");
                
                String sampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE); 
                String sampleLastLevel = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL);                 
                
                String addSampleAnalysis = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS); 
                if (addSampleAnalysis==null){addSampleAnalysis="false";}
                String sampleAnalysisFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE);                
                String[] sampleAnalysisFieldToRetrieveArr=null;
                if (sampleAnalysisFieldToRetrieve==null || sampleAnalysisFieldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(sampleAnalysisFieldToRetrieve))
                    sampleAnalysisFieldToRetrieveArr=getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS.getTableFields());
                else 
                    sampleAnalysisFieldToRetrieveArr=sampleAnalysisFieldToRetrieve.split("\\|");
                sampleAnalysisWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME); 
                sampleAnalysisWhereFieldsNameArr = new String[0];
                if ( (sampleAnalysisWhereFieldsName!=null) && (sampleAnalysisWhereFieldsName.length()>0) ) {
                    sampleAnalysisWhereFieldsNameArr=LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                }                                
                sampleAnalysisWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE); 

                String addSampleAnalysisResult = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT); 
                if (addSampleAnalysisResult==null){addSampleAnalysisResult="false";}
                sampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE); 
                String[] sampleAnalysisResultFieldToRetrieveArr=null;
                if (sampleAnalysisResultFieldToRetrieve==null || sampleAnalysisResultFieldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(sampleAnalysisResultFieldToRetrieve))
                    sampleAnalysisResultFieldToRetrieveArr=getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableFields());
                else 
                    sampleAnalysisResultFieldToRetrieveArr=sampleAnalysisResultFieldToRetrieve.split("\\|");
                String sampleAnalysisResultWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME); 
                String[] sampleAnalysisResultWhereFieldsNameArr = new String[0];
                if ( (sampleAnalysisResultWhereFieldsName!=null) && (sampleAnalysisResultWhereFieldsName.length()>0) ) {
                    sampleAnalysisResultWhereFieldsNameArr=LPArray.addValueToArray1D(sampleAnalysisResultWhereFieldsNameArr, sampleAnalysisResultWhereFieldsName.split("\\|"));
                }                                
                String sampleAnalysisResultWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE); 
                Boolean includeOnlyWhenResultsInProgress = Boolean.valueOf(LPNulls.replaceNull(argValues[14]).toString());

                sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME);  
                JSONArray samplesArray = samplesByStageData(sampleLastLevel, fieldToRetrieveArr, whereFieldsName, 
                        whereFieldsValue, sortFieldsName,
                        addSampleAnalysis, sampleAnalysisFieldToRetrieveArr, sampleAnalysisWhereFieldsName, sampleAnalysisWhereFieldsValue,
                        addSampleAnalysisResult,
                        sampleAnalysisResultFieldToRetrieveArr, sampleAnalysisResultWhereFieldsName, sampleAnalysisResultWhereFieldsValue,
                        includeOnlyWhenResultsInProgress);
                this.isSuccess=true;
                this.responseSuccessJArr=samplesArray;
                return;                         
            case SAMPLES_ANALYSIS_VIEW:  
                whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME); 
                if (whereFieldsName==null){whereFieldsName="";}
                whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE); 

                String fieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE); 
                if (fieldToRetrieve==null || fieldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(fieldToRetrieve))
                    fieldToRetrieveArr=getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS.getTableFields());
                else 
                    fieldToRetrieveArr=fieldToRetrieve.split("\\|");
                
                sampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE); 
                addSampleAnalysisResult = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT); 
                if (addSampleAnalysisResult==null){addSampleAnalysisResult="false";}
                sampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE); 
                sampleAnalysisResultFieldToRetrieveArr=null;
                if (sampleAnalysisResultFieldToRetrieve==null || sampleAnalysisResultFieldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(sampleAnalysisResultFieldToRetrieve))
                    sampleAnalysisResultFieldToRetrieveArr=getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableFields());
                else 
                    sampleAnalysisResultFieldToRetrieveArr=sampleAnalysisResultFieldToRetrieve.split("\\|");
                sampleAnalysisResultWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME); 
                sampleAnalysisResultWhereFieldsNameArr = new String[0];
                if ( (sampleAnalysisResultWhereFieldsName!=null) && (sampleAnalysisResultWhereFieldsName.length()>0) ) {
                    sampleAnalysisResultWhereFieldsNameArr=LPArray.addValueToArray1D(sampleAnalysisResultWhereFieldsNameArr, sampleAnalysisResultWhereFieldsName.split("\\|"));
                }                                
                sampleAnalysisResultWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE); 
                includeOnlyWhenResultsInProgress = Boolean.valueOf(LPNulls.replaceNull(argValues[14]).toString());

                sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME);  
                JSONArray samplesAnalysisArray = sampleAnalysisView(fieldToRetrieveArr, whereFieldsName, 
                        whereFieldsValue, sortFieldsName,                        
                        addSampleAnalysisResult, sampleAnalysisResultFieldToRetrieveArr, sampleAnalysisResultWhereFieldsName, sampleAnalysisResultWhereFieldsValue,
                        includeOnlyWhenResultsInProgress);
                this.isSuccess=true;
                this.responseSuccessJArr=samplesAnalysisArray;
                return;                         
            case SAMPLES_ANALYSIS_RESULTS_VIEW:  
                whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME); 
                if (whereFieldsName==null){whereFieldsName="";}
                whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE); 

                fieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE); 
                if (fieldToRetrieve==null || fieldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(fieldToRetrieve))
                    fieldToRetrieveArr=getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableFields());
                else 
                    fieldToRetrieveArr=fieldToRetrieve.split("\\|");

                sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME);  
                JSONArray samplesAnalysisResultArray = sampleAnalysisResultView(fieldToRetrieveArr, whereFieldsName, 
                        whereFieldsValue, sortFieldsName);
                this.isSuccess=true;
                this.responseSuccessJArr=samplesAnalysisResultArray;
                return;                         
            case GET_METHOD_CERTIFIED_USERS_LIST:
                return;
            default:      
                break;
            }    
        this.diagnostic=actionDiagnoses;
        this.relatedObj=rObj;
        rObj.killInstance();
    }

    private JSONArray sampleStageDataJsonArr(String procInstanceName, Integer sampleId, String[] sampleFldName, Object[] sampleFldValue, String[] sampleStageFldName, Object[] sampleStageFldValue){
    if (sampleStageFldValue==null) return null;
    if (!LPArray.valueInArray(sampleStageFldName, TblsProcedure.SampleStageTimingCapture.STAGE_CURRENT.getName())) return null; //new Object[][]{{}};
    String currentStage=sampleStageFldValue[LPArray.valuePosicInArray(sampleStageFldName, TblsProcedure.SampleStageTimingCapture.STAGE_CURRENT.getName())].toString();
    JSONObject jObj= new JSONObject();
    JSONArray jArrMainObj=new JSONArray();
    JSONArray jArrMainObj2=new JSONArray();
    switch (currentStage.toUpperCase()){
        case "SAMPLING":
            jObj.put(TblsEnvMonitData.Sample.SAMPLING_DATE.getName(), sampleFldValue[LPArray.valuePosicInArray(sampleFldName, TblsEnvMonitData.Sample.SAMPLING_DATE.getName())].toString());
            jObj.put("field_name", TblsEnvMonitData.Sample.SAMPLING_DATE.getName());
            jObj.put("field_value", sampleFldValue[LPArray.valuePosicInArray(sampleFldName, TblsEnvMonitData.Sample.SAMPLING_DATE.getName())].toString());
            jArrMainObj.add(jObj);
            return jArrMainObj; 
        case "INCUBATION":
            String[] incub1Flds=new String[]{TblsEnvMonitData.Sample.INCUBATION_PASSED.getName(), TblsEnvMonitData.Sample.INCUBATION_INCUBATOR.getName(), TblsEnvMonitData.Sample.INCUBATION_BATCH.getName(), 
                TblsEnvMonitData.Sample.INCUBATION_START.getName(), TblsEnvMonitData.Sample.INCUBATION_START_TEMP_EVENT_ID.getName(), TblsEnvMonitData.Sample.INCUBATION_START_TEMPERATURE.getName(),
                TblsEnvMonitData.Sample.INCUBATION_END.getName(), TblsEnvMonitData.Sample.INCUBATION_END_TEMP_EVENT_ID.getName(), TblsEnvMonitData.Sample.INCUBATION_END_TEMPERATURE.getName()};
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
            String[] tblAllFlds=EnumIntViewFields.getAllFieldNames(TblsEnvMonitData.ViewSampleMicroorganismList.values());
            Object[][] sampleStageInfo=QueryUtilitiesEnums.getViewData(TblsEnvMonitData.ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW, 
                EnumIntViewFields.getViewFieldsFromString(TblsEnvMonitData.ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW, tblAllFlds),
                new SqlWhere(TblsEnvMonitData.ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW, new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.SAMPLE_ID.getName()}, new Object[]{sampleId}), 
                new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.TEST_ID.getName(), TblsEnvMonitData.ViewSampleMicroorganismList.RESULT_ID.getName()});                    
            jObj= new JSONObject();
            jObj2= new JSONObject();
            for (int iFlds=0;iFlds<sampleStageInfo[0].length;iFlds++){ 
                jObj2.put(tblAllFlds[iFlds], sampleStageInfo[0][iFlds].toString());
                JSONObject jObjSampleStageInfo=new JSONObject();
                jObjSampleStageInfo.put("field_name", tblAllFlds[iFlds]);
                jObjSampleStageInfo.put("field_value", sampleStageInfo[0][iFlds].toString());
                jArrMainObj.add(jObjSampleStageInfo);
            }
            jObj.put("counting", jObj2);
            jArrMainObj.add(jObj);
            return jArrMainObj;
        default: 
            return jArrMainObj; 
    }
}
    
    static Object[] sampleAnalysisResultLockData(String[] resultFieldToRetrieveArr, Object[] curRow){
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] fldNameArr=new String[0];
        Object[] fldValueArr=new Object[0];
        Integer resultFldPosic = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.SampleAnalysisResult.RESULT_ID.getName());
        Integer resultId=Integer.valueOf(curRow[resultFldPosic].toString());
        
        if (!isProgramCorrectiveActionEnable(procInstanceName)) return new Object[]{fldNameArr, fldValueArr};
        Object[][] notClosedProgramCorrreciveAction=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), 
                new String[]{TblsProcedure.ProgramCorrectiveAction.RESULT_ID.getName(), TblsProcedure.ProgramCorrectiveAction.STATUS.getName()+"<>"}, 
                new Object[]{resultId,DataProgramCorrectiveAction.ProgramCorrectiveStatus.CLOSED.toString()}, 
                SAMPLEANALYSISRESULTLOCKDATA_RETRIEVEDATA_PROGRAMCORRECTIVEACTION);
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(notClosedProgramCorrreciveAction[0][0].toString())){
            String notifMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataProgramCorrectiveAction.DataProgramCorrectiveActionBusinessRules.STILLOPEN_NOTIFMODE.getAreaName(), DataProgramCorrectiveAction.DataProgramCorrectiveActionBusinessRules.STILLOPEN_NOTIFMODE.getTagName());
            switch(notifMode.toLowerCase()){
            case "silent":
                return new Object[]{fldNameArr, fldValueArr};
            case "warning":
                fldNameArr=LPArray.addValueToArray1D(fldNameArr, "has_warning");
                fldValueArr=LPArray.addValueToArray1D(fldValueArr, true);
                fldNameArr=LPArray.addValueToArray1D(fldNameArr, "warning_object");                
                fldValueArr=LPArray.addValueToArray1D(fldValueArr, TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName());
                
                String msgCode="resultLockedByProgramCorrectiveActionInProgress";
                fldValueArr=LPArray.addValueToArray1D(fldValueArr, msgCode);
                String errorTextEn = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_WARNING_REASONS, null, msgCode, "en", null, true, null);
                String errorTextEs = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_WARNING_REASONS, null, msgCode, "es", null, false, null);
                JSONObject reasonInfo=new JSONObject();
                reasonInfo.put("message_en", errorTextEn);
                reasonInfo.put("message_es", errorTextEs);
                return new Object[]{fldNameArr, fldValueArr, "warning_reason", reasonInfo};
            case "locking":
            default:
                fldNameArr=LPArray.addValueToArray1D(fldNameArr, "is_locked");
                fldValueArr=LPArray.addValueToArray1D(fldValueArr, true);
                fldNameArr=LPArray.addValueToArray1D(fldNameArr, "locking_object");
                fldValueArr=LPArray.addValueToArray1D(fldValueArr, TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName());
                msgCode="resultLockedByProgramCorrectiveActionInProgress";
                fldValueArr=LPArray.addValueToArray1D(fldValueArr, msgCode);
                errorTextEn = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_LOCKING_REASONS, null, msgCode, "en", null, true, null);
                errorTextEs = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_LOCKING_REASONS, null, msgCode, "es", null, false, null);
                reasonInfo=new JSONObject();
                reasonInfo.put("message_en", errorTextEn);
                reasonInfo.put("message_es", errorTextEs);
                return new Object[]{fldNameArr, fldValueArr, "locking_reason", reasonInfo};
            }
        }
        return new Object[]{fldNameArr, fldValueArr};
    }
    private static Boolean isThereResultsInProgress(String[] fldsName, Object[] fldsValue){
        Integer smFldPosic=LPArray.valuePosicInArray(fldsName, TblsData.Sample.SAMPLE_ID.getName());
        if (smFldPosic==-1) return false;
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] groupedInfo = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
        new String[]{TblsData.SampleAnalysis.STATUS.getName()}, 
        new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.STATUS.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.NOT_IN.getSqlClause()}, 
        new Object[]{fldsValue[smFldPosic], 
            SampleStatuses.REVIEWED.getStatusCode("")+"|"+SampleStatuses.CANCELED.getStatusCode("")                            }, 
        null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(groupedInfo[0][0].toString())) 
            return false;
        return (groupedInfo[0][0].toString().length()>0);        
    }
    
    static Object[] getObjectsId(String[] headerFlds, Object[][] analysisResultList, String separator){
        if (analysisResultList==null || analysisResultList.length==0)
            return new Object[]{};
        Object[] objIds=new Object[]{};
        for (Object[] curRow: analysisResultList){
            String curTest=TblsData.SampleAnalysisResult.TEST_ID.getName()+separator+curRow[LPArray.valuePosicInArray(headerFlds, TblsData.SampleAnalysisResult.TEST_ID.getName())].toString();
            if (!LPArray.valueInArray(objIds, curTest)) objIds=LPArray.addValueToArray1D(objIds, curTest);
            String curResult=TblsData.SampleAnalysisResult.RESULT_ID.getName()+separator+curRow[LPArray.valuePosicInArray(headerFlds, TblsData.SampleAnalysisResult.RESULT_ID.getName())].toString();
            if (!LPArray.valueInArray(objIds, curResult)) objIds=LPArray.addValueToArray1D(objIds, curResult);
        }
        return objIds;
    }
    
    /**
     * @return the messageDynamicData
     */
    public Object[] getMessageDynamicData() {
        return messageDynamicData;
    }

    /**
     * @return the relatedObj
     */
    public RelatedObjects getRelatedObj() {
        return relatedObj;
    }

    /**
     * @return the endpointExists
     */
    public Boolean getEndpointExists() {
        return endpointExists;
    }

    /**
     * @return the diagnostic
     */
    public Object[] getDiagnostic() {
        return diagnostic;
    }

    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
    }

    /**
     * @return the isSuccess
     */
    public Boolean getIsSuccess() {
        return isSuccess;
    }

    /**
     * @return the contentSuccessResponse
     */
    public Object getResponseContentJArr() {
        return responseSuccessJArr;
    }
    public Object getResponseContentJObj() {
        return responseSuccessJObj;
    }
    public Object getResponseError() {
        return responseError;
    }
    
}
