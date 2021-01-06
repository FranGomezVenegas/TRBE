/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import com.labplanet.servicios.moduleenvmonit.*;
import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.modulesample.SampleAPIParams.SampleAPIfrontendEndpoints;
import databases.Rdbms;
import databases.TblsData;
import databases.TblsProcedure;
import databases.Token;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction;
import static functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction.isProgramCorrectiveActionEnable;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.samplestructure.DataSampleStages;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 *
 * @author User
 */
public class ClassSampleQueries {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstance();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;
    private Boolean isSuccess=false;
    private JSONObject responseSuccessJObj=null;
    private JSONArray responseSuccessJArr=null;
    private Object[] responseError=null;

    private static final String[] SAMPLEANALYSISRESULTLOCKDATA_RETRIEVEDATA_PROGRAMCORRECTIVEACTION=new String[]{TblsProcedure.ProgramCorrectiveAction.FLD_RESULT_ID.getName(), TblsProcedure.ProgramCorrectiveAction.FLD_STATUS.getName()};
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN;

    public enum SampleAPIFrontendEndpoints{
        /**
         *
         */                
        GET_SAMPLE_ANALYSIS_RESULT_LIST("GET_SAMPLE_ANALYSIS_RESULT_LIST", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                //new LPAPIArguments(EnvMonitAPIParams., LPAPIArguments.ArgumentType.STRING.toString(), false, 7)
                }),
        GET_SAMPLE_STAGES_SUMMARY_REPORT("GET_SAMPLE_STAGES_SUMMARY_REPORT", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            }),
        KPIS("KPIS", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OBJ_GROUP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABLE_CATEGORY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABLE_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELDS_TO_RETRIEVE_OR_GROUPING, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_GROUPED, LPAPIArguments.ArgumentType.BOOLEANARR.toString(), true, 11),
                }),        
        ;
        private SampleAPIFrontendEndpoints(String name, LPAPIArguments[] argums){
            this.name=name;
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
        /**
         * @return the arguments
         */
        public LPAPIArguments[] getArguments() {
            return arguments;
        }     
        private final String name;
        private final LPAPIArguments[] arguments;
    }
    
    public ClassSampleQueries(HttpServletRequest request, Token finalToken, String schemaPrefix, SampleAPIfrontendEndpoints endPoint){
        RelatedObjects rObj=RelatedObjects.getInstance();

        String batchName = "";
        String incubationName = "";
        String language="";
        Object[] actionDiagnoses = null;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());        
        this.functionFound=true;
            switch (endPoint){
/*                case CORRECTIVE_ACTION_COMPLETE:
                    String programName=argValues[0].toString();
                    Integer correctiveActionId = (Integer) argValues[1];                    
                    actionDiagnoses = DataProgramCorrectiveAction.markAsCompleted(schemaPrefix, token, correctiveActionId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){                        
                        Object[][] correctiveActionInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_PROCEDURE), TblsEnvMonitProcedure.ProgramCorrectiveAction.TBL.getName(), 
                            new String[]{TblsEnvMonitProcedure.ProgramCorrectiveAction.FLD_ID.getName()}, new Object[]{correctiveActionId},
                            new String[]{TblsEnvMonitProcedure.ProgramCorrectiveAction.FLD_SAMPLE_ID.getName()});
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{correctiveActionId, correctiveActionInfo[0][0], schemaPrefix}); 
                        this.messageDynamicData=new Object[]{correctiveActionId, correctiveActionInfo[0][0], schemaPrefix};   
                    }else{
                        this.messageDynamicData=new Object[]{correctiveActionId, schemaPrefix};                           
                    }                    
                    break;*/
                case GET_SAMPLE_ANALYSIS_RESULT_LIST:
                    Integer sampleId = (Integer) argValues[0];                        
                    String resultFieldToRetrieve = argValues[1].toString();
                    String[] resultFieldToRetrieveArr=null;
                    if (resultFieldToRetrieve!=null && resultFieldToRetrieve.length()>0){resultFieldToRetrieveArr=  resultFieldToRetrieve.split("\\|");}
                    resultFieldToRetrieveArr = LPArray.addValueToArray1D(resultFieldToRetrieveArr, SampleAPIParams.MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|"));
                    
                    String sampleAnalysisWhereFieldsName = argValues[2].toString();
                    String[] sampleAnalysisWhereFieldsNameArr = new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()};
                    if ( (sampleAnalysisWhereFieldsName!=null ) && (sampleAnalysisWhereFieldsName.length()>0) ) {
                        sampleAnalysisWhereFieldsNameArr=LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                    }     
                    Object[] sampleAnalysisWhereFieldsValueArr = new Object[]{sampleId};
                    String sampleAnalysisWhereFieldsValue = argValues[3].toString();
                    if ( (sampleAnalysisWhereFieldsValue!=null) && (sampleAnalysisWhereFieldsValue.length()>0) ) 
                        sampleAnalysisWhereFieldsValueArr=LPArray.addValueToArray1D(sampleAnalysisWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")));
                    
                    String[] sortFieldsNameArr = null;
                    String sortFieldsName = argValues[4].toString();
                    if ( (sortFieldsName!=null) && (sortFieldsName.length()>0) ) 
                        sortFieldsNameArr = sortFieldsName.split("\\|");                                    
                    else
                        sortFieldsNameArr = SampleAPIParams.MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|");     
                    
                    resultFieldToRetrieveArr=LPArray.addValueToArray1D(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_RAW_VALUE.getName());
                    Integer posicRawValueFld=resultFieldToRetrieveArr.length;
                    resultFieldToRetrieveArr=LPArray.addValueToArray1D(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_LIMIT_ID.getName());
                    Integer posicLimitIdFld=resultFieldToRetrieveArr.length;
                    Object[][] analysisResultList = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(),
                            sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsValueArr,
                            //new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()},new Object[]{sampleId}, 
                            resultFieldToRetrieveArr, sortFieldsNameArr);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisResultList[0][0].toString())){  
                        Rdbms.closeRdbms();   
                        this.isSuccess=false;
                        this.responseError=LPArray.array2dTo1d(analysisResultList);
                        //response.sendError((int) errMsg[0], (String) errMsg[1]);                            
                    }else{                           
                        rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsEnvMonitData.Sample.TBL.getName(), TblsEnvMonitData.Sample.TBL.getName(), sampleId);
                        Object[] objectsIds=getObjectsId(resultFieldToRetrieveArr, analysisResultList, "-");
                        for (Object curObj: objectsIds){
                            String[] curObjDet=curObj.toString().split("-");
                            if (TblsData.SampleAnalysisResult.FLD_TEST_ID.getName().equalsIgnoreCase(curObjDet[0]))
                                rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsData.SampleAnalysis.TBL.getName(), TblsData.SampleAnalysis.TBL.getName(), curObjDet[1]);
                            if (TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName().equalsIgnoreCase(curObjDet[0]))
                                rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsData.SampleAnalysisResult.TBL.getName(), TblsData.SampleAnalysisResult.TBL.getName(), curObjDet[1]);
                        }
                      JSONArray jArr=new JSONArray();
                      for (Object[] curRow: analysisResultList){
                        ConfigSpecRule specRule = new ConfigSpecRule();
                        String currRowRawValue=curRow[posicRawValueFld-1].toString();
                        String currRowLimitId=curRow[posicLimitIdFld-1].toString();
                        Object[] resultLockData=sampleAnalysisResultLockData(schemaPrefix, resultFieldToRetrieveArr, curRow);
                        JSONObject row=new JSONObject();
                        if (resultLockData!=null)
                            row=LPJson.convertArrayRowToJSONObject(LPArray.addValueToArray1D(resultFieldToRetrieveArr, (String[]) resultLockData[0]), LPArray.addValueToArray1D(curRow, (Object[]) resultLockData[1]));
                        else        
                            row=LPJson.convertArrayRowToJSONObject(resultFieldToRetrieveArr, curRow);
                        if ((currRowLimitId!=null) && (currRowLimitId.length()>0) ){
                          specRule.specLimitsRule(schemaPrefix, Integer.valueOf(currRowLimitId) , null);                        
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
                               
                default:      
//                  RequestDispatcher rd = request.getRequestDispatcher(SampleAPIParams.SERVLET_FRONTEND_URL);
//                  rd.forward(request, null);                                   
            }    
        this.diagnostic=actionDiagnoses;
        this.relatedObj=rObj;
        rObj.killInstance();
    }

private JSONArray sampleStageDataJsonArr(String schemaPrefix, Integer sampleId, String[] sampleFldName, Object[] sampleFldValue, String[] sampleStageFldName, Object[] sampleStageFldValue){
    if (sampleStageFldValue==null) return null;
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
                    new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                    tblAllFlds, new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.FLD_TEST_ID.getName(), TblsEnvMonitData.ViewSampleMicroorganismList.FLD_RESULT_ID.getName()});                    
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
    
    static Object[] sampleAnalysisResultLockData(String schemaPrefix, String[] resultFieldToRetrieveArr, Object[] curRow){
        String[] fldNameArr=new String[0];
        Object[] fldValueArr=new Object[0];
        Integer resultFldPosic = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName());
        Integer resultId=Integer.valueOf(curRow[resultFldPosic].toString());
        
        if (!isProgramCorrectiveActionEnable(schemaPrefix)) return new Object[]{fldNameArr, fldValueArr};
        Object[][] notClosedProgramCorrreciveAction=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_PROCEDURE), TblsProcedure.ProgramCorrectiveAction.TBL.getName(), 
                new String[]{TblsProcedure.ProgramCorrectiveAction.FLD_RESULT_ID.getName(), TblsProcedure.ProgramCorrectiveAction.FLD_STATUS.getName()+"<>"}, 
                new Object[]{resultId,DataProgramCorrectiveAction.ProgramCorrectiveStatus.CLOSED.toString()}, 
                SAMPLEANALYSISRESULTLOCKDATA_RETRIEVEDATA_PROGRAMCORRECTIVEACTION);
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(notClosedProgramCorrreciveAction[0][0].toString())){
            fldNameArr=LPArray.addValueToArray1D(fldNameArr, "is_locked");
            fldValueArr=LPArray.addValueToArray1D(fldValueArr, true);
            fldNameArr=LPArray.addValueToArray1D(fldNameArr, "locking_object");
            fldValueArr=LPArray.addValueToArray1D(fldValueArr, TblsProcedure.ProgramCorrectiveAction.TBL.getName());
            fldNameArr=LPArray.addValueToArray1D(fldNameArr, "locking_reason");
            
            JSONObject lockReasonJSONObj = LPFrontEnd.responseJSONDiagnosticLPTrue(
                    EnvMonSampleAPI.class.getSimpleName(),
                    "resultLockedByProgramCorrectiveAction", notClosedProgramCorrreciveAction[0], null);                                
            fldValueArr=LPArray.addValueToArray1D(fldValueArr, lockReasonJSONObj);
        }
        return new Object[]{fldNameArr, fldValueArr};
    }
    static Object[] getObjectsId(String[] headerFlds, Object[][] analysisResultList, String separator){
        if (analysisResultList==null || analysisResultList.length==0)
            return new Object[]{};
        Object[] objIds=new Object[]{};
        for (Object[] curRow: analysisResultList){
            String curTest=TblsData.SampleAnalysisResult.FLD_TEST_ID.getName()+separator+curRow[LPArray.valuePosicInArray(headerFlds, TblsData.SampleAnalysisResult.FLD_TEST_ID.getName())].toString();
            if (!LPArray.valueInArray(objIds, curTest)) objIds=LPArray.addValueToArray1D(objIds, curTest);
            String curResult=TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()+separator+curRow[LPArray.valuePosicInArray(headerFlds, TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName())].toString();
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
