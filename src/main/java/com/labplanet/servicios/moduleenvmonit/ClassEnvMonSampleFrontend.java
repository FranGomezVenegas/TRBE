/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.JSON_TAG_NAME_SAMPLE_RESULTS;
import static com.labplanet.servicios.moduleenvmonit.EnvMonIncubBatchAPIfrontend.getActiveBatchData;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData.ViewSampleMicroorganismList;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import static com.labplanet.servicios.modulesample.SampleAPIfrontend.samplesByStageData;
import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsProcedure;
import databases.Token;
import functionaljavaa.certification.AnalysisMethodCertif;
import functionaljavaa.instruments.incubator.DataIncubatorNoteBook;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction;
import static functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction.isProgramCorrectiveActionEnable;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.samplestructure.DataSampleStages;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.Arrays;
import java.util.HashMap;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import static lbplanet.utilities.LPDate.dateStringFormatToLocalDateTime;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import static lbplanet.utilities.LPFrontEnd.noRecordsInTableMessage;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import static trazit.queries.QueryUtilities.getFieldsListToRetrieve;
import static trazit.queries.QueryUtilities.getTableData;

/**
 *
 * @author User
 */
public class ClassEnvMonSampleFrontend {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;
    private Boolean isSuccess=false;
    private JSONObject responseSuccessJObj=null;
    private JSONArray responseSuccessJArr=null;
    private Object[] responseError=null;

    private static final String[] SAMPLEANALYSISRESULTLOCKDATA_RETRIEVEDATA_PROGRAMCORRECTIVEACTION=new String[]{TblsProcedure.ProgramCorrectiveAction.FLD_RESULT_ID.getName(), TblsProcedure.ProgramCorrectiveAction.FLD_STATUS.getName()};
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

    public enum EnvMonSampleAPIFrontendEndpoints{
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
                }, EndPointsToRequirements.endpointWithNoOutputObjects),
        GET_MICROORGANISM_LIST("GET_MICROORGANISM_LIST", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            }, EndPointsToRequirements.endpointWithNoOutputObjects),
        GET_SAMPLE_MICROORGANISM_VIEW("GET_SAMPLE_MICROORGANISM_VIEW", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8),
            }, EndPointsToRequirements.endpointWithNoOutputObjects),
        GET_SAMPLE_STAGES_SUMMARY_REPORT("GET_SAMPLE_STAGES_SUMMARY_REPORT", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            }, EndPointsToRequirements.endpointWithNoOutputObjects),
        GET_SAMPLE_BY_TESTINGGROUP_SUMMARY_REPORT("GET_SAMPLE_BY_TESTINGGROUP_SUMMARY_REPORT", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            }, EndPointsToRequirements.endpointWithNoOutputObjects),
        GET_BATCH_REPORT("GET_BATCH_REPORT", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_FIELD_TO_DISPLAY, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8),
            }, EndPointsToRequirements.endpointWithNoOutputObjects),
        GET_PRODLOT_REPORT("GET_PRODLOT_REPORT", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PRODLOT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PRODLOT_FIELD_TO_DISPLAY, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8),
            }, EndPointsToRequirements.endpointWithNoOutputObjects),
        GET_INCUBATOR_REPORT("GET_INCUBATOR_REPORT", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCUBATOR_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCUBATOR_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCUBATOR_FIELD_TO_DISPLAY, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_START, LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_END, LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
            }, EndPointsToRequirements.endpointWithNoOutputObjects),
        STATS_SAMPLES_PER_STAGE("STATS_SAMPLES_PER_STAGE", new LPAPIArguments[]{
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_STAGES_TO_INCLUDE, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_STAGES_TO_EXCLUDE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
            }, EndPointsToRequirements.endpointWithNoOutputObjects),
        STATS_PROGRAM_LAST_RESULTS("STATS_PROGRAM_LAST_RESULTS", new LPAPIArguments[]{
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_GROUPED, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_TOTAL_OBJECTS, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            }, EndPointsToRequirements.endpointWithNoOutputObjects),
        KPIS("KPIS", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OBJ_GROUP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABLE_CATEGORY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABLE_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELDS_TO_RETRIEVE_OR_GROUPING, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_GROUPED, LPAPIArguments.ArgumentType.BOOLEANARR.toString(), true, 11),
                }, EndPointsToRequirements.endpointWithNoOutputObjects),        
        GET_PENDING_INCUBATION_SAMPLES_AND_ACTIVE_BATCHES("GET_PENDING_INCUBATION_SAMPLES_AND_ACTIVE_BATCHES", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8),
new LPAPIArguments("incub1_"+GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 9),
                new LPAPIArguments("incub1_"+GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                new LPAPIArguments("incub1_"+GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                new LPAPIArguments("incub1_"+GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
                new LPAPIArguments("incub1_"+GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13),
                new LPAPIArguments("incub1_"+GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 14),
                new LPAPIArguments("incub1_"+GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 15),
                new LPAPIArguments("incub1_"+GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 16),
                new LPAPIArguments("incub1_"+GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 17),
                new LPAPIArguments("incub1_"+GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 18),
                new LPAPIArguments("incub1_"+GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 19),
                new LPAPIArguments("incub1_"+GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 20),
                new LPAPIArguments("incub1_"+GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 21),
                new LPAPIArguments("incub1_"+GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 22),            
new LPAPIArguments("incub2_"+GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 23),
                new LPAPIArguments("incub2_"+GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 24),
                new LPAPIArguments("incub2_"+GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 25),
                new LPAPIArguments("incub2_"+GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 26),
                new LPAPIArguments("incub2_"+GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 27),
                new LPAPIArguments("incub2_"+GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 28),
                new LPAPIArguments("incub2_"+GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 29),
                new LPAPIArguments("incub2_"+GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 30),
                new LPAPIArguments("incub2_"+GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 31),
                new LPAPIArguments("incub2_"+GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 32),
                new LPAPIArguments("incub2_"+GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 33),
                new LPAPIArguments("incub2_"+GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 34),
                new LPAPIArguments("incub2_"+GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 35),
                new LPAPIArguments("incub2_"+GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 36)}, EndPointsToRequirements.endpointWithNoOutputObjects)
            
        
        ;
        private EnvMonSampleAPIFrontendEndpoints(String name, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
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
        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        public LPAPIArguments[] getArguments() {return arguments;}
        private final String name;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }
    
    public ClassEnvMonSampleFrontend(HttpServletRequest request, EnvMonSampleAPIFrontendEndpoints endPoint){
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        RelatedObjects rObj=RelatedObjects.getInstanceForActions();

        String batchName = "";
        Object[] actionDiagnoses = null;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());        
        this.functionFound=true;
            switch (endPoint){
                case GET_SAMPLE_ANALYSIS_RESULT_LIST:
                    Integer sampleId = (Integer) argValues[0];                        
                    String[] resultFieldToRetrieveArr=getFieldsListToRetrieve(argValues[1].toString(), TblsData.ViewSampleAnalysisResultWithSpecLimits.getAllFieldNames());

                    resultFieldToRetrieveArr = LPArray.addValueToArray1D(resultFieldToRetrieveArr, SampleAPIParams.MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|"));
                    
                    String[] sampleAnalysisWhereFieldsNameArr = new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName()};
                    Object[] sampleAnalysisWhereFieldsValueArr = new Object[]{sampleId};

                    String sampleAnalysisWhereFieldsName = argValues[2].toString();
                    if ( (sampleAnalysisWhereFieldsName!=null ) && (sampleAnalysisWhereFieldsName.length()>0) ) 
                        sampleAnalysisWhereFieldsNameArr=LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                    String sampleAnalysisWhereFieldsValue = argValues[3].toString();
                    if ( (sampleAnalysisWhereFieldsValue!=null) && (sampleAnalysisWhereFieldsValue.length()>0) )
                        sampleAnalysisWhereFieldsValueArr=LPArray.addValueToArray1D(sampleAnalysisWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")));
                    
                    String[] sortFieldsNameArr = null;
                    String sortFieldsName = argValues[4].toString();
                    if ( (sortFieldsName!=null) && (sortFieldsName.length()>0) ) 
                        sortFieldsNameArr = sortFieldsName.split("\\|");                                    
                    else
                        sortFieldsNameArr = SampleAPIParams.MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|");     
                    
                    Integer posicRawValueFld=LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_RAW_VALUE.getName());
                    if (posicRawValueFld==-1){
                        resultFieldToRetrieveArr=LPArray.addValueToArray1D(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_RAW_VALUE.getName());
                        posicRawValueFld=resultFieldToRetrieveArr.length;
                    }
                    Integer posicLimitIdFld=LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_LIMIT_ID.getName());
                    if (posicLimitIdFld==-1){
                        resultFieldToRetrieveArr=LPArray.addValueToArray1D(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_LIMIT_ID.getName());
                        posicLimitIdFld=resultFieldToRetrieveArr.length;
                    }
                    Object[][] analysisResultList=getTableData(GlobalVariables.Schemas.DATA.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(), 
                        argValues[1].toString(), resultFieldToRetrieveArr, 
                        sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsValueArr,
                        sortFieldsNameArr);     
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisResultList[0][0].toString())){  
                        // Rdbms.closeRdbms();   
                        this.isSuccess=false;
                        this.responseError=LPArray.array2dTo1d(analysisResultList);
                        //response.sendError((int) errMsg[0], (String) errMsg[1]);                            
                    }else{                           
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.Sample.TBL.getName(), TblsEnvMonitData.Sample.TBL.getName(), sampleId);
                        Object[] objectsIds=getObjectsId(resultFieldToRetrieveArr, analysisResultList, "-");
                        for (Object curObj: objectsIds){
                            String[] curObjDet=curObj.toString().split("-");
                            if (TblsData.SampleAnalysisResult.FLD_TEST_ID.getName().equalsIgnoreCase(curObjDet[0]))
                                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.SampleAnalysis.TBL.getName(), TblsData.SampleAnalysis.TBL.getName(), curObjDet[1]);
                            if (TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName().equalsIgnoreCase(curObjDet[0]))
                                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.SampleAnalysisResult.TBL.getName(), TblsData.SampleAnalysisResult.TBL.getName(), curObjDet[1]);
                        }
                        JSONArray jArr=new JSONArray();
                        for (Object[] curRow: analysisResultList){
                            ConfigSpecRule specRule = new ConfigSpecRule();
                            String currRowRawValue=curRow[posicRawValueFld].toString();
                            String currRowLimitId=curRow[posicLimitIdFld].toString();
                            Object[] resultLockData=sampleAnalysisResultLockData(procInstanceName, resultFieldToRetrieveArr, curRow);
                            JSONObject row=new JSONObject();
                            if (resultLockData!=null && resultLockData[0]!=null)
                                row=LPJson.convertArrayRowToJSONObject(LPArray.addValueToArray1D(resultFieldToRetrieveArr, (String[]) resultLockData[0]), LPArray.addValueToArray1D(curRow, (Object[]) resultLockData[1]));
                            else        
                                row=LPJson.convertArrayRowToJSONObject(resultFieldToRetrieveArr, curRow);
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
                case GET_MICROORGANISM_LIST:
                    String[] fieldsToRetrieve=getFieldsListToRetrieve("", TblsEnvMonitConfig.MicroOrganism.getAllFieldNames());                    
                    Object[][] list=getTableData(GlobalVariables.Schemas.CONFIG.getName(), TblsEnvMonitConfig.MicroOrganism.TBL.getName(), 
                        "", TblsEnvMonitConfig.MicroOrganism.getAllFieldNames(), 
                        new String[]{TblsEnvMonitConfig.MicroOrganism.FLD_NAME.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{}, 
                        new String[]{TblsEnvMonitConfig.MicroOrganism.FLD_NAME.getName()});     
                    JSONArray jArr=new JSONArray();
                    for (Object[] curRec: list){
                        JSONObject jObj= LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRec);
                        jArr.add(jObj);
                    }
                    this.isSuccess=true;
                    this.responseSuccessJArr=jArr;
                    return;  
                case GET_SAMPLE_MICROORGANISM_VIEW:
                    String fieldsNameToRetrieve = argValues[0].toString(); 
                    String whereFieldsName = argValues[1].toString(); 
                    if (whereFieldsName==null){whereFieldsName="";}
                    String whereFieldsValue = argValues[2].toString(); 
                    String[] whereFieldsNameArr=new String[0];
                    Object[] whereFieldsValueArr=new Object[0];
                    if ( (whereFieldsName!=null && whereFieldsName.length()>0) && (whereFieldsValue!=null && whereFieldsValue.length()>0) ){
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
                            String[] tokenFieldValue = Token.getTokenFieldValue(whereFieldsValueArr[iFields].toString(),  ProcedureRequestSession.getInstanceForActions(null, null, null).getTokenString());
                            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(tokenFieldValue[0])) 
                                whereFieldsValueArr[iFields]=tokenFieldValue[1];                                                    
                        }                                    
                    }            
                    whereFieldsNameArr=LPArray.addValueToArray1D(whereFieldsNameArr, new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.FLD_CURRENT_STAGE.getName(), TblsEnvMonitData.ViewSampleMicroorganismList.FLD_RAW_VALUE.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()});
                    whereFieldsValueArr=LPArray.addValueToArray1D(whereFieldsValueArr, new Object[]{"MicroorganismIdentification"});
                    ViewSampleMicroorganismList[] fieldsList = TblsEnvMonitData.ViewSampleMicroorganismList.values();
                    if (fieldsNameToRetrieve.length()==0 || "ALL".equalsIgnoreCase(fieldsNameToRetrieve))
                        fieldsToRetrieve = TblsEnvMonitData.ViewSampleMicroorganismList.getAllFieldNames();
                    else 
                        fieldsToRetrieve = fieldsNameToRetrieve.split("\\|");
                    list = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.ViewSampleMicroorganismList.TBL.getName(), 
                            whereFieldsNameArr, whereFieldsValueArr
                            , fieldsToRetrieve
                            , new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.FLD_SAMPLE_ID.getName()} );
                    jArr=new JSONArray();
                    for (Object[] curRec: list){
                      JSONObject jObj= LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRec);
                      Integer fldPosic=LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitData.ViewSampleMicroorganismList.FLD_MICROORGANISM_LIST.getName());
                      if (fldPosic>-1){
                          JSONArray jMicArr=new JSONArray();
                          for (String curMic:curRec[fldPosic].toString().split(", ")){
                              //jMicArr.add(curMic);
                              JSONObject jmicObj=new JSONObject();
                              jmicObj.put("name", curMic);
                              jMicArr.add(jmicObj);
                          }
                          jObj.put("microorganism_list_array", jMicArr);
                      }                      
                      jArr.add(jObj);
                    }
                    this.isSuccess=true;
                    this.responseSuccessJArr=jArr;                    
                    return; 
                case GET_SAMPLE_STAGES_SUMMARY_REPORT:
                    sampleId = (Integer) argValues[0];
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
                    Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.Sample.TBL.getName(), 
                            new String[]{TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                            sampleTblAllFields);                    
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())){
                        this.isSuccess=false;
                        this.responseError=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Error on getting sample <*1*> in procedure <*2*>", 
                            new Object[]{Arrays.toString(sampleInfo[0]), procInstanceName});                        
                        //LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Error on getting sample <*1*> in procedure <*2*>", new Object[]{Arrays.toString(sampleInfo[0]), procInstanceName}));              
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
                    for (String sampleToDisplayArr1 : sampleToDisplayArr) {
                        if (LPArray.valueInArray(sampleTblAllFields, sampleToDisplayArr1)) {
                            jObjPieceOfInfo=new JSONObject();
                            jObjPieceOfInfo.put("field_name", sampleToDisplayArr1);
                            jObjPieceOfInfo.put("field_value", sampleInfo[0][LPArray.valuePosicInArray(sampleTblAllFields, sampleToDisplayArr1)].toString());
                            jArrPieceOfInfo.add(jObjPieceOfInfo);
                    }
            }

                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, jObjSampleInfo);
                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_DISPLAY, jArrPieceOfInfo);
                    
                    JSONArray jArrMainObj=new JSONArray();
                    jObjPieceOfInfo=new JSONObject();
                    DataSampleStages smpStage= new DataSampleStages();
                    String[] sampleStageTimingCaptureAllFlds=TblsProcedure.SampleStageTimingCapture.getAllFieldNames();
                    JSONObject jObjMainObject2=new JSONObject();                    
                    
                    if (smpStage.isSampleStagesEnable()){
                        Object[][] sampleStageInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.SampleStageTimingCapture.TBL.getName(), 
                                new String[]{TblsProcedure.SampleStageTimingCapture.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                                sampleStageTimingCaptureAllFlds, new String[]{TblsProcedure.SampleStageTimingCapture.FLD_ID.getName()});                    
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleStageInfo[0][0].toString())){
                            this.isSuccess=false;
                            this.responseError=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Error on getting sample <*1*> in procedure <*2*>", new Object[]{Arrays.toString(sampleInfo[0]), procInstanceName});              
                            return;}  
                        for (Object[] curRec: sampleStageInfo){
                            JSONObject jObj= LPJson.convertArrayRowToJSONObject(sampleStageTimingCaptureAllFlds, curRec);
                            JSONArray jArrMainObj2=new JSONArray();
                            jArrMainObj2=sampleStageDataJsonArr(procInstanceName, sampleId, sampleTblAllFields, sampleInfo[0], sampleStageTimingCaptureAllFlds, curRec);
                            jObj.put("data", jArrMainObj2);
                            jArrMainObj.add(jObj);
                        }
                    }
                    jObjMainObject.put("stages", jArrMainObj);  
                    this.isSuccess=true;
                    this.responseSuccessJObj=jObjMainObject;
//                    LPFrontEnd.servletReturnSuccess(request, response, jObjMainObject);
                    return;
                case GET_SAMPLE_BY_TESTINGGROUP_SUMMARY_REPORT:
                    sampleId = (Integer) argValues[0];
                    sampleToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE);
                    sampleToRetrieveArr=new String[0];
                    if ((sampleToRetrieve!=null) && (sampleToRetrieve.length()>0))
                        if ("ALL".equalsIgnoreCase(sampleToRetrieve)) sampleToRetrieveArr=TblsEnvMonitData.Sample.getAllFieldNames();
                        else sampleToRetrieveArr=sampleToRetrieve.split("\\|");
                    sampleToRetrieveArr=LPArray.addValueToArray1D(sampleToRetrieveArr, TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName());
                    sampleToDisplay = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_DISPLAY);
                    sampleToDisplayArr=new String[0];
                    if ((sampleToDisplay!=null) && (sampleToDisplay.length()>0))
                        if ("ALL".equalsIgnoreCase(sampleToDisplay)) sampleToDisplayArr=TblsEnvMonitData.Sample.getAllFieldNames();
                        else sampleToDisplayArr=sampleToDisplay.split("\\|");

                    sampleTblAllFields=TblsEnvMonitData.Sample.getAllFieldNames();
                    sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.Sample.TBL.getName(), 
                            new String[]{TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                            sampleTblAllFields);                    
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())){
                        this.isSuccess=false;
                        this.responseError=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Error on getting sample <*1*> in procedure <*2*>", 
                            new Object[]{Arrays.toString(sampleInfo[0]), procInstanceName});                        
                        return;}  
                    jObjMainObject=new JSONObject();                    
                    jObjSampleInfo=new JSONObject();
                    jObjSampleInfo=LPJson.convertArrayRowToJSONObject(sampleTblAllFields, sampleInfo[0]);
                    String[] testingGroupFldsArr=TblsData.SampleRevisionTestingGroup.getAllFieldNames();
                    Object[][] testingGroupInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleRevisionTestingGroup.TBL.getName(), 
                            new String[]{TblsData.SampleRevisionTestingGroup.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                            testingGroupFldsArr);                    
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(testingGroupInfo[0][0].toString())){
                        JSONArray tstGrpJsArr=new JSONArray();
                        for (Object[] curTstGrp: testingGroupInfo){
                            JSONObject curTstGrpJObj=LPJson.convertArrayRowToJSONObject(testingGroupFldsArr, curTstGrp);
                            String curTstGrpName=LPNulls.replaceNull(curTstGrp[LPArray.valuePosicInArray(testingGroupFldsArr, TblsData.SampleRevisionTestingGroup.FLD_TESTING_GROUP.getName())]).toString();
                            String[] testFldsArr=TblsData.SampleAnalysis.getAllFieldNames();
                            Object[][] testInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysis.TBL.getName(), 
                                    new String[]{TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName(), TblsData.SampleAnalysis.FLD_TESTING_GROUP.getName()}, new Object[]{sampleId, curTstGrpName}, 
                                    testFldsArr);                    
                            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(testInfo[0][0].toString())){
                                JSONArray testJsArr=new JSONArray();
                                for (Object[] curTest: testInfo){
                                    JSONObject curTestJsObj=LPJson.convertArrayRowToJSONObject(testFldsArr, curTest);
                                    Integer curTestId=Integer.valueOf(LPNulls.replaceNull(curTest[LPArray.valuePosicInArray(testFldsArr, TblsData.SampleAnalysis.FLD_TEST_ID.getName())]).toString());
                                    String[] resultFldsArr=TblsData.SampleAnalysisResult.getAllFieldNames();
                                    Object[][] resultInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysisResult.TBL.getName(), 
                                            new String[]{TblsData.SampleAnalysisResult.FLD_SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.FLD_TEST_ID.getName()}, new Object[]{sampleId, curTestId}, 
                                            resultFldsArr);                    
                                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString())){
                                        JSONArray resultJsArr=new JSONArray();
                                        for (Object[] curResult: resultInfo){
                                            JSONObject curResultJsObj=LPJson.convertArrayRowToJSONObject(resultFldsArr, curResult);
                                            resultJsArr.add(curResultJsObj);
                                        }
                                        curTestJsObj.put(TblsData.SampleAnalysisResult.TBL.getName(), resultJsArr);
                                    }
                                    
                                    testJsArr.add(curTestJsObj);
                                }
                                curTstGrpJObj.put(TblsData.SampleAnalysis.TBL.getName(), testJsArr);
                            }
                            tstGrpJsArr.add(curTstGrpJObj);
                        }
                        jObjSampleInfo.put(TblsData.SampleRevisionTestingGroup.TBL.getName(), tstGrpJsArr);
                    }
                    
                    this.isSuccess=true;
                    jObjMainObject.put("sample_id", sampleId.toString());
                    jObjMainObject.put("sample", jObjSampleInfo);
                    this.responseSuccessJObj=jObjMainObject;                    
                    return;
                case GET_BATCH_REPORT:
                    batchName = argValues[0].toString();
                    String fieldsToRetrieveStr = argValues[1].toString();
                    String prodLotfieldsToDisplayStr = argValues[2].toString();
                    String[] fieldToRetrieveArr=new String[0];
                    if ((fieldsToRetrieveStr!=null) && (fieldsToRetrieveStr.length()>0))
                        if ("ALL".equalsIgnoreCase(fieldsToRetrieveStr)) fieldToRetrieveArr=TblsEnvMonitData.IncubBatch.getAllFieldNames();
                        else fieldToRetrieveArr=fieldsToRetrieveStr.split("\\|");
                    fieldToRetrieveArr=LPArray.addValueToArray1D(fieldToRetrieveArr, TblsEnvMonitData.IncubBatch.FLD_NAME.getName());
                    String[] fieldToDisplayArr=new String[0];
                    if ((prodLotfieldsToDisplayStr!=null) && (prodLotfieldsToDisplayStr.length()>0))
                        if ("ALL".equalsIgnoreCase(prodLotfieldsToDisplayStr)) fieldToDisplayArr=TblsEnvMonitData.IncubBatch.getAllFieldNames();                        
                        else fieldToDisplayArr=prodLotfieldsToDisplayStr.split("\\|");
                    String[] batchTblAllFields=TblsEnvMonitData.IncubBatch.getAllFieldNames();
                    Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                            new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, 
                            batchTblAllFields);                    
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())){
                        this.isSuccess=false;
                        this.responseError=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Error on getting sample <*1*> in procedure <*2*>", new Object[]{Arrays.toString(batchInfo[0]), procInstanceName});
                        //LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Error on getting sample <*1*> in procedure <*2*>", new Object[]{Arrays.toString(batchInfo[0]), procInstanceName}));              
                        return;}  
                    JSONObject jObjBatchInfo=new JSONObject();
                    jObjMainObject=new JSONObject();
                    jObjPieceOfInfo=new JSONObject();
                    jArrPieceOfInfo=new JSONArray();
                    for (int iFlds=0;iFlds<batchInfo[0].length;iFlds++){                      
                        if (LPArray.valueInArray(fieldToRetrieveArr, batchTblAllFields[iFlds]))
                            jObjBatchInfo.put(batchTblAllFields[iFlds], batchInfo[0][iFlds].toString());
                    }
                    for (String fieldToDisplayArr1 : fieldToDisplayArr) {
                        if (LPArray.valueInArray(batchTblAllFields, fieldToDisplayArr1)) {
                            jObjPieceOfInfo=new JSONObject();
                            jObjPieceOfInfo.put("field_name", fieldToDisplayArr1);
                            jObjPieceOfInfo.put("field_value", batchInfo[0][LPArray.valuePosicInArray(batchTblAllFields, fieldToDisplayArr1)].toString());
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
                        Object[][] instrReadings=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), 
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
                    this.isSuccess=true;
                    this.responseSuccessJObj=jObjMainObject;
                    break;                                        
                case GET_PRODLOT_REPORT:
                    String lotName = argValues[0].toString();
                    String prodLotfieldsToRetrieveStr = argValues[1].toString();
                    prodLotfieldsToDisplayStr = argValues[2].toString();
                    String[] prodLotfieldToRetrieveArr=null;
                    if ((prodLotfieldsToRetrieveStr!=null) && (prodLotfieldsToRetrieveStr.length()>0))
                        if ("ALL".equalsIgnoreCase(prodLotfieldsToRetrieveStr)) prodLotfieldToRetrieveArr=TblsEnvMonitData.ProductionLot.getAllFieldNames();
                        else prodLotfieldToRetrieveArr=prodLotfieldsToRetrieveStr.split("\\|");
                    if (prodLotfieldToRetrieveArr==null) prodLotfieldToRetrieveArr=TblsEnvMonitData.ProductionLot.getAllFieldNames();
                    prodLotfieldToRetrieveArr=LPArray.addValueToArray1D(prodLotfieldToRetrieveArr, TblsEnvMonitData.ProductionLot.FLD_LOT_NAME.getName());
                    String[] prodLotfieldToDisplayArr=null;
                    if ((prodLotfieldsToDisplayStr!=null) && (prodLotfieldsToDisplayStr.length()>0))
                        if ("ALL".equalsIgnoreCase(prodLotfieldsToDisplayStr)) prodLotfieldToDisplayArr=TblsEnvMonitData.ProductionLot.getAllFieldNames();                        
                        else prodLotfieldToDisplayArr=prodLotfieldsToDisplayStr.split("\\|");
                    if (prodLotfieldToDisplayArr==null) prodLotfieldToDisplayArr=TblsEnvMonitData.ProductionLot.getAllFieldNames();
                    String[] prodLotTblAllFields=TblsEnvMonitData.ProductionLot.getAllFieldNames();
                    Object[][] prodLotInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.ProductionLot.TBL.getName(), 
                            new String[]{TblsEnvMonitData.ProductionLot.FLD_LOT_NAME.getName()}, new Object[]{lotName}, 
                            prodLotTblAllFields);                    
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(prodLotInfo[0][0].toString())){
                        this.isSuccess=false;
                        this.responseError=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Error on getting sample <*1*> in procedure <*2*>", new Object[]{Arrays.toString(prodLotInfo[0]), procInstanceName});
                        return;}  
                    JSONObject jObjProdLotInfo=new JSONObject();
                    jObjMainObject=new JSONObject();
                    jObjPieceOfInfo=new JSONObject();
                    jArrPieceOfInfo=new JSONArray();
                    for (int iFlds=0;iFlds<prodLotInfo[0].length;iFlds++){                      
                        if (LPArray.valueInArray(prodLotfieldToRetrieveArr, prodLotTblAllFields[iFlds]))
                            jObjProdLotInfo.put(prodLotTblAllFields[iFlds], prodLotInfo[0][iFlds].toString());
                    }
                    for (String fieldToDisplayArr1 : prodLotfieldToDisplayArr) {
                        if (LPArray.valueInArray(prodLotTblAllFields, fieldToDisplayArr1)) {
                            jObjPieceOfInfo=new JSONObject();
                            jObjPieceOfInfo.put("field_name", fieldToDisplayArr1);
                            jObjPieceOfInfo.put("field_value", prodLotInfo[0][LPArray.valuePosicInArray(prodLotTblAllFields, fieldToDisplayArr1)].toString());
                            jArrPieceOfInfo.add(jObjPieceOfInfo);
                        }
                    }
                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_PRODLOT_FIELD_TO_RETRIEVE, jObjProdLotInfo);
                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_PRODLOT_FIELD_TO_DISPLAY, jArrPieceOfInfo);

                    String prodLotFieldToRetrieve = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_PROD_LOT_FIELD_TO_RETRIEVE);
                    String[] prodLotFieldToRetrieveArr=new String[0];
                    if ((prodLotFieldToRetrieve!=null) && (prodLotFieldToRetrieve.length()>0))
                        if ("ALL".equalsIgnoreCase(prodLotFieldToRetrieve)) prodLotFieldToRetrieveArr=TblsEnvMonitData.ProductionLot.getAllFieldNames();
                        else prodLotFieldToRetrieveArr=prodLotFieldToRetrieve.split("\\|");
                    if (prodLotFieldToRetrieve==null)
                        prodLotFieldToRetrieveArr=TblsEnvMonitData.ProductionLot.getAllFieldNames();
                    String sampleFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE);                    
                    String[] sampleFieldToRetrieveArr=new String[0];
                    if ((sampleFieldToRetrieve!=null) && (sampleFieldToRetrieve.length()>0))
                        if ("ALL".equalsIgnoreCase(sampleFieldToRetrieve)) sampleFieldToRetrieveArr=TblsEnvMonitData.Sample.getAllFieldNames();
                        else sampleFieldToRetrieveArr=sampleFieldToRetrieve.split("\\|");
                    if (sampleFieldToRetrieve==null)
                        sampleFieldToRetrieveArr=TblsEnvMonitData.Sample.getAllFieldNames();
                    String sampleWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_WHERE_FIELDS_NAME); 
                    String[] sampleWhereFieldsNameArr=new String[0];
                    if (sampleWhereFieldsName!=null && sampleWhereFieldsName.length()>0)
                        sampleWhereFieldsNameArr=sampleWhereFieldsName.split("\\|");
                    String sampleWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_WHERE_FIELDS_VALUE); 
                    Object[] sampleWhereFieldsValueArr=new Object[0];
                    if (sampleWhereFieldsValue!=null && sampleWhereFieldsValue.length()>0)
                        sampleWhereFieldsValueArr=LPArray.convertStringWithDataTypeToObjectArray(sampleWhereFieldsValue.split("\\|"));
                    //String[] sampleWhereFieldsNameArr=sampleWhereFieldsName.split("\\|");
                    if (!LPArray.valueInArray(sampleWhereFieldsNameArr, TblsEnvMonitData.Sample.FLD_PRODUCTION_LOT.getName())){
                        sampleWhereFieldsNameArr=LPArray.addValueToArray1D(sampleWhereFieldsNameArr, TblsEnvMonitData.Sample.FLD_PRODUCTION_LOT.getName());
                        sampleWhereFieldsValueArr=LPArray.addValueToArray1D(sampleWhereFieldsValueArr, lotName);
                    }
/*                    Object[][] prodLotInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.ProductionLot.TBL.getName(), 
                            new String[]{TblsEnvMonitData.ProductionLot.FLD_LOT_NAME.getName()}, new Object[]{lotName}
                            , prodLotFieldToRetrieveArr, new String[]{TblsEnvMonitData.ProductionLot.FLD_CREATED_ON.getName()+" desc"} ); 
                    JSONObject jObj=new JSONObject();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(prodLotInfo[0][0].toString())){
                         jObj= noRecordsInTableMessage();                    
                    }else{
                       for (Object[] curRec: prodLotInfo){
                         jObj= LPJson.convertArrayRowToJSONObject(prodLotFieldToRetrieveArr, curRec);
                       }
                    }
                    jObjMainObject.put(TblsEnvMonitData.ProductionLot.TBL.getName(), jObj);*/
                    
                    sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.Sample.TBL.getName(), 
                            sampleWhereFieldsNameArr, sampleWhereFieldsValueArr
                            , sampleFieldToRetrieveArr , new String[]{TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName()+" desc"} ); 
                    JSONObject jObj=new JSONObject();
                    JSONArray sampleJsonArr = new JSONArray();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())){
                         jObj= noRecordsInTableMessage();                    
                    }else{     
                        sampleJsonArr.add(jObj);
                        for (Object[] curRec: sampleInfo){
                            jObj= LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, curRec);
                            sampleJsonArr.add(jObj);
                        }
                    }    
                    jObjMainObject.put(TblsEnvMonitData.Sample.TBL.getName(), sampleJsonArr);

                    sampleJsonArr = new JSONArray();
                    for (String fieldToDisplayArr1 : sampleFieldToRetrieveArr) {
                        jObjPieceOfInfo=new JSONObject();
                        jObjPieceOfInfo.put("field_name", fieldToDisplayArr1);
                        sampleJsonArr.add(jObjPieceOfInfo);
                    }                    
                    jObjMainObject.put(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_DISPLAY, sampleJsonArr);
                    
                    String sampleGroups=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_GROUPS);
                    if (sampleGroups!=null){
                        String[] sampleGroupsArr=sampleGroups.split("\\|");
                        for (String currGroup: sampleGroupsArr){
                            JSONArray sampleGrouperJsonArr = new JSONArray();
                            String[] groupInfo = currGroup.split("\\*");
                            String[] smpGroupFldsArr=groupInfo[0].split(",");
                            Object[][] groupedInfo = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.Sample.TBL.getName(), 
                                    smpGroupFldsArr, new String[]{TblsEnvMonitData.Sample.FLD_PRODUCTION_LOT.getName()}, new Object[]{lotName}, 
                                    null);
                            smpGroupFldsArr=LPArray.addValueToArray1D(smpGroupFldsArr, "count");
                            smpGroupFldsArr=LPArray.addValueToArray1D(smpGroupFldsArr, "grouper");
                            jObj=new JSONObject();
                            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())){
                                jObj= noRecordsInTableMessage();                    
                            }else{                       
                                for (Object[] curRec: groupedInfo){
                                    jObj= LPJson.convertArrayRowToJSONObject(smpGroupFldsArr, curRec);
                                    sampleGrouperJsonArr.add(jObj);
                                }
                            } 
                            jObjMainObject.put(groupInfo[1], sampleGrouperJsonArr);
                        }
                    }              
                    this.isSuccess=true;
                    this.responseSuccessJObj=jObjMainObject;                    
                    break;                                        
                case GET_INCUBATOR_REPORT:
                    lotName = argValues[0].toString();
                    prodLotfieldsToRetrieveStr = argValues[1].toString();
                    prodLotfieldsToDisplayStr = argValues[2].toString();
                    
                    String startDateStr = argValues[3].toString();
                    String endDateStr = argValues[4].toString();
                    
                    prodLotfieldToRetrieveArr=new String[0];
                    if ((prodLotfieldsToRetrieveStr!=null) && (prodLotfieldsToRetrieveStr.length()>0))
                        if ("ALL".equalsIgnoreCase(prodLotfieldsToRetrieveStr)) prodLotfieldToRetrieveArr=TblsEnvMonitConfig.InstrIncubator.getAllFieldNames();
                        else prodLotfieldToRetrieveArr=prodLotfieldsToRetrieveStr.split("\\|");
                    prodLotfieldToRetrieveArr=LPArray.addValueToArray1D(prodLotfieldToRetrieveArr, TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName());
                    prodLotfieldToDisplayArr=new String[0];
                    if ((prodLotfieldsToDisplayStr!=null) && (prodLotfieldsToDisplayStr.length()>0))
                        if ("ALL".equalsIgnoreCase(prodLotfieldsToDisplayStr)) prodLotfieldToDisplayArr=TblsEnvMonitConfig.InstrIncubator.getAllFieldNames();                        
                        else prodLotfieldToDisplayArr=prodLotfieldsToDisplayStr.split("\\|");
                    String[] incubTblAllFields=TblsEnvMonitConfig.InstrIncubator.getAllFieldNames();
                    Object[][] incubInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                            new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{lotName}, 
                            incubTblAllFields);                    
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubInfo[0][0].toString())){
                        this.isSuccess=false;
                        this.responseError= LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Error on getting sample <*1*> in procedure <*2*>", new Object[]{Arrays.toString(incubInfo[0]), procInstanceName});
                        return;}  
                    jObjProdLotInfo=new JSONObject();
                    jObjMainObject=new JSONObject();
                    jObjPieceOfInfo=new JSONObject();
                    jArrPieceOfInfo=new JSONArray();
                    for (int iFlds=0;iFlds<incubInfo[0].length;iFlds++){                      
                        if (LPArray.valueInArray(prodLotfieldToRetrieveArr, incubTblAllFields[iFlds]))
                            jObjProdLotInfo.put(incubTblAllFields[iFlds], incubInfo[0][iFlds].toString());
                    }
                    for (String fieldToDisplayArr1 : prodLotfieldToDisplayArr) {
                        if (LPArray.valueInArray(incubTblAllFields, fieldToDisplayArr1)) {
                            jObjPieceOfInfo=new JSONObject();
                            jObjPieceOfInfo.put("field_name", fieldToDisplayArr1);
                            jObjPieceOfInfo.put("field_value", incubInfo[0][LPArray.valuePosicInArray(incubTblAllFields, fieldToDisplayArr1)].toString());
                            jArrPieceOfInfo.add(jObjPieceOfInfo);
                        }
                    }
                    jObjMainObject.put(GlobalAPIsParams.INCUBATION_REPORT_JSON_TAG_NAME_FIELD_TO_RETRIEVE, jObjProdLotInfo);
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
                        instrReadings = DataIncubatorNoteBook.getLastTemperatureReading(lotName, numPointsInt);
                    if (startDateStr!=null && endDateStr==null){ 
                        
                        startDateStr=startDateStr.replace ( " " , "T" );
                        instrReadings = DataIncubatorNoteBook.getLastTemperatureReading(lotName, numPointsInt, dateStringFormatToLocalDateTime(startDateStr));
                    }
                    if (startDateStr!=null && endDateStr!=null){
                        startDateStr=startDateStr.replace ( " " , "T" );
                        endDateStr=endDateStr.replace (" " , "T" );
                        instrReadings = DataIncubatorNoteBook.getLastTemperatureReading(lotName, numPointsInt, dateStringFormatToLocalDateTime(startDateStr), dateStringFormatToLocalDateTime(endDateStr));
                    }
                    jArrLastTempReadings = new JSONArray();
                    for (Object[] currReading: instrReadings){
                        jObj= new JSONObject();
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(currReading[0].toString())){
                            jObj.put("error", "No temperature readings found");
                        }else{
                            jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currReading);}
                        
                        jArrLastTempReadings.add(jObj);
                    }
                    jObjMainObject.put(GlobalAPIsParams.INCUBATION_REPORT_JSON_TAG_NAME_LAST_N_TEMP_READINGS, jArrLastTempReadings);
                    this.isSuccess=true;
                    this.responseSuccessJObj=jObjMainObject;                          
                    break;                                        
                    
                case STATS_SAMPLES_PER_STAGE:
                    String[] whereFieldNames = new String[0];
                    Object[] whereFieldValues = new Object[0];
                    prodLotfieldToRetrieveArr=new String[]{TblsEnvMonitData.Sample.FLD_CURRENT_STAGE.getName()};
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
                    Object[][] samplesCounterPerStage=Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.Sample.TBL.getName(), 
                            prodLotfieldToRetrieveArr, 
                            whereFieldNames, whereFieldValues,
                            new String[]{"COUNTER desc"});  
                    prodLotfieldToRetrieveArr=LPArray.addValueToArray1D(prodLotfieldToRetrieveArr, "COUNTER");
                    jArr=new JSONArray();
                    for (Object[] curRec: samplesCounterPerStage){
                      jObj= LPJson.convertArrayRowToJSONObject(prodLotfieldToRetrieveArr, curRec);
                      jArr.add(jObj);
                    }
                    this.isSuccess=true;
                    this.responseSuccessJArr=jArr;                          
                    return;                     
                case STATS_PROGRAM_LAST_RESULTS:
                    String grouped=argValues[0].toString();
                    prodLotfieldsToRetrieveStr = argValues[1].toString();
                    prodLotfieldToRetrieveArr=new String[0];
                    Integer numTotalRecords=50;
                    String numTotalRecordsStr=LPNulls.replaceNull(argValues[2]).toString();
                    if (numTotalRecordsStr==null || numTotalRecordsStr.length()==0) 
                        numTotalRecords=50;
                    else
                        numTotalRecords=Integer.valueOf(LPNulls.replaceNull(argValues[2].toString()));
                    if ((prodLotfieldsToRetrieveStr!=null) && (prodLotfieldsToRetrieveStr.length()>0)){
                        if ("ALL".equalsIgnoreCase(prodLotfieldsToRetrieveStr)) prodLotfieldToRetrieveArr=TblsData.ViewSampleAnalysisResultWithSpecLimits.getAllFieldNames();
                        else prodLotfieldToRetrieveArr=prodLotfieldsToRetrieveStr.split("\\|");
                    }else prodLotfieldToRetrieveArr=TblsData.ViewSampleAnalysisResultWithSpecLimits.getAllFieldNames();
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
                        whereFieldNames = LPArray.addValueToArray1D(whereFieldNames, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_RAW_VALUE.getName()+ WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause());

                        Object[][] programLastResults=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(), 
                                whereFieldNames, whereFieldValues, 
                                prodLotfieldToRetrieveArr, new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_ENTERED_ON.getName()+" desc"});  
                        if (numTotalRecords>programLastResults.length) numTotalRecords=programLastResults.length;
                        jArr=new JSONArray();
                        for (int i=0;i<numTotalRecords;i++){
                          jObj= LPJson.convertArrayRowToJSONObject(prodLotfieldToRetrieveArr, programLastResults[i]);
                          jArr.add(jObj);
                        }
                    }else{
                        String[] whereLimitsFieldNames = new String[0];
                        Object[] whereLimitsFieldValues = new Object[0];                    

                        if (whereLimitsFieldNames==null || whereLimitsFieldNames.length==0)
                            whereLimitsFieldNames=new String[]{TblsCnfg.SpecLimits.FLD_LIMIT_ID.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()};
//                        
                        String[] fieldToRetrieveLimitsArr=new String[]{TblsCnfg.SpecLimits.FLD_CODE.getName(), TblsCnfg.SpecLimits.FLD_VARIATION_NAME.getName(), TblsCnfg.SpecLimits.FLD_ANALYSIS.getName(), TblsCnfg.SpecLimits.FLD_METHOD_NAME.getName(), TblsCnfg.SpecLimits.FLD_PARAMETER.getName(), TblsCnfg.SpecLimits.FLD_RULE_TYPE.getName(),
                            TblsCnfg.SpecLimits.FLD_RULE_VARIABLES.getName(), TblsCnfg.SpecLimits.FLD_UOM.getName()};
                        String[] limitsFieldNamesToFilter=new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SPEC_CODE.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SPEC_VARIATION_NAME.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_ANALYSIS.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_METHOD_NAME.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_PARAMETER.getName()};
                        String[] fieldToRetrieveGroupedArr = new String[0];                        
                        if ((prodLotfieldsToRetrieveStr==null) || ("ALL".equalsIgnoreCase(prodLotfieldsToRetrieveStr)) ) fieldToRetrieveGroupedArr=TblsData.ViewSampleAnalysisResultWithSpecLimits.getAllFieldNames();
                        else fieldToRetrieveGroupedArr=prodLotfieldsToRetrieveStr.split("\\|");
                        Object[][] specLimits=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.SpecLimits.TBL.getName(), 
                                whereLimitsFieldNames, whereLimitsFieldValues, fieldToRetrieveLimitsArr, new String[]{TblsCnfg.SpecLimits.FLD_LIMIT_ID.getName()});
                        jArr=new JSONArray();
                        for (Object[] currLimit: specLimits){
                            numTotalRecords=50;
                            whereFieldNames = new String[0];
                            whereFieldValues = new Object[0];                    
                            programName=argValues[3].toString();
                            if (programName!=null){
                                whereFieldNames=LPArray.addValueToArray1D(whereFieldNames, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_PROGRAM_NAME.getName());
                                whereFieldValues=LPArray.addValueToArray1D(whereFieldValues, programName);
                            }                            
                            jObj= LPJson.convertArrayRowToJSONObject(fieldToRetrieveLimitsArr, currLimit);
                            for (int i=0;i<limitsFieldNamesToFilter.length;i++){
                                whereFieldNames=LPArray.addValueToArray1D(whereFieldNames, limitsFieldNamesToFilter[i]);                                
                                whereFieldValues=LPArray.addValueToArray1D(whereFieldValues, currLimit[i]);
                            }
                            whereFieldNames = LPArray.addValueToArray1D(whereFieldNames, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_RAW_VALUE.getName()+ WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause());                            
                            whereFieldValues=LPArray.addValueToArray1D(whereFieldValues, "");                            
                            Object[][] programLastResults=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(), 
                                    whereFieldNames, whereFieldValues, 
                                    prodLotfieldToRetrieveArr, new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_ENTERED_ON.getName()+" desc"});
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
                    this.isSuccess=true;
                    this.responseSuccessJArr=jArr;  
//                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return; 
                case GET_PENDING_INCUBATION_SAMPLES_AND_ACTIVE_BATCHES:                    
                    fieldsToRetrieve=new String[]{};
                    String fieldsRetrieveStr = argValues[0].toString(); 
                    if (fieldsRetrieveStr.length()==0 || "ALL".equalsIgnoreCase(fieldsRetrieveStr))
                        fieldsToRetrieve=TblsEnvMonitData.IncubBatch.getAllFieldNames();
                    else
                        fieldsToRetrieve=fieldsRetrieveStr.split("\\|");
                    whereFieldsNameArr = null;
                    whereFieldsValueArr = null;
                    whereFieldsName = argValues[1].toString(); 
                    if (whereFieldsName==null){whereFieldsName="";}
                    whereFieldsValue = argValues[2].toString();
                    if (whereFieldsValue==null){whereFieldsValue="";}

                    if (whereFieldsName.length()>0)
                        whereFieldsNameArr=LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
                    else
                        whereFieldsNameArr=new String[]{TblsEnvMonitData.IncubBatch.FLD_ACTIVE.getName()};
                    if (whereFieldsValue.length()>0)
                        whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|")));                                          
                    else
                        whereFieldsValueArr=new Object[]{true};
                    for (int iFields=0; iFields<whereFieldsNameArr.length; iFields++){
                        if (LPPlatform.isEncryptedField(procInstanceName, GlobalVariables.Schemas.DATA.getName(), TblsData.Sample.TBL.getName(), whereFieldsNameArr[iFields])){                
                            HashMap<String, String> hm = LPPlatform.encryptEncryptableFieldsAddBoth(whereFieldsNameArr[iFields], whereFieldsNameArr[iFields]);
                            whereFieldsNameArr[iFields]= hm.keySet().iterator().next();    
                            if ( hm.get(whereFieldsNameArr[iFields]).length()!=whereFieldsNameArr[iFields].length()){
                                String newWhereFieldValues = hm.get(whereFieldsNameArr[iFields]);
                                whereFieldsValueArr[iFields]=newWhereFieldValues;
                            }
                        }
                        procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, null);
                        String[] tokenFieldValue = Token.getTokenFieldValue(whereFieldsValueArr[iFields].toString(), procReqInstance.getTokenString());
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(tokenFieldValue[0])) 
                            whereFieldsValueArr[iFields]=tokenFieldValue[1];                                                    
                    } 
                    if (!LPArray.valueInArray(whereFieldsNameArr, TblsEnvMonitData.IncubBatch.FLD_ACTIVE.getName())){
                        whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, TblsEnvMonitData.IncubBatch.FLD_ACTIVE.getName());
                        whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, true);
                    }
                    jArr=getActiveBatchData(fieldsToRetrieve, whereFieldsNameArr, whereFieldsValueArr);
                    jObj=new JSONObject();
                    jObj.put("active_batches", jArr);
                    int j=3;
                    for (int i=1;i<3;i++){
                        whereFieldsName=argValues[j].toString();j++;
                        whereFieldsValue=argValues[j].toString();j++;
                        sampleFieldToRetrieve=argValues[j].toString();j++;
                        if (sampleFieldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(sampleFieldToRetrieve))
                            sampleFieldToRetrieveArr=TblsEnvMonitData.Sample.getAllFieldNames();
                        else 
                            sampleFieldToRetrieveArr=sampleFieldToRetrieve.split("\\|");
                        String sampleAnalysisFieldToRetrieve=argValues[j].toString();j++;
                        String sampleLastLevel=argValues[j].toString();j++;
                        String addSampleAnalysis=argValues[j].toString();j++;
                        sampleAnalysisFieldToRetrieve=argValues[j].toString();j++;
                        String[] sampleAnalysisFieldToRetrieveArr=null;
                        if (sampleAnalysisFieldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(sampleAnalysisFieldToRetrieve))
                            sampleAnalysisFieldToRetrieveArr=TblsEnvMonitData.Sample.getAllFieldNames();
                        else 
                            sampleAnalysisFieldToRetrieveArr=sampleAnalysisFieldToRetrieve.split("\\|");
                        sampleAnalysisWhereFieldsName=argValues[j].toString();j++;
                        sampleAnalysisWhereFieldsValue=argValues[j].toString();j++;
                        String addSampleAnalysisResult=argValues[j].toString();j++;
                        String sampleAnalysisResultFieldToRetrieve=argValues[j].toString();j++;
                        String[] sampleAnalysisResultFieldToRetrieveArr=null;
                        if (sampleAnalysisResultFieldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(sampleAnalysisResultFieldToRetrieve))
                            sampleAnalysisResultFieldToRetrieveArr=TblsEnvMonitData.Sample.getAllFieldNames();
                        else 
                            sampleAnalysisResultFieldToRetrieveArr=sampleAnalysisResultFieldToRetrieve.split("\\|");
                        String sampleAnalysisResultWhereFieldsName=argValues[j].toString();j++;
                        String sampleAnalysisResultWhereFieldsValue=argValues[j].toString();j++;
                        sortFieldsName=argValues[j].toString();j++;
                        JSONArray samplesArray = samplesByStageData(sampleLastLevel, sampleFieldToRetrieveArr, whereFieldsName, 
                                whereFieldsValue, sortFieldsName,
                                addSampleAnalysis, sampleAnalysisFieldToRetrieveArr, sampleAnalysisWhereFieldsName, sampleAnalysisWhereFieldsValue,
                                addSampleAnalysisResult, sampleAnalysisResultFieldToRetrieveArr, sampleAnalysisResultWhereFieldsName, sampleAnalysisResultWhereFieldsValue);
                        jObj.put("incub_"+String.valueOf(i), samplesArray);                    
                    } 
                    this.isSuccess=true;
                    this.responseSuccessJObj=jObj;
                    return; 
                default:      
//                  RequestDispatcher rd = request.getRequestDispatcher(SampleAPIParams.SERVLET_FRONTEND_URL);
//                  rd.forward(request, null);                                   
            }    
        this.diagnostic=actionDiagnoses;
        this.relatedObj=rObj;
        rObj.killInstance();
    }

private JSONArray sampleStageDataJsonArr(String procInstanceName, Integer sampleId, String[] sampleFldName, Object[] sampleFldValue, String[] sampleStageFldName, Object[] sampleStageFldValue){
    if (sampleStageFldValue==null) return null;
    if (!LPArray.valueInArray(sampleStageFldName, TblsProcedure.SampleStageTimingCapture.FLD_STAGE_CURRENT.getName())) return null; //new Object[][]{{}};
    String currentStage=sampleStageFldValue[LPArray.valuePosicInArray(sampleStageFldName, TblsProcedure.SampleStageTimingCapture.FLD_STAGE_CURRENT.getName())].toString();
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
            Object[][] sampleStageInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.ViewSampleMicroorganismList.TBL.getName(), 
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
    
    static Object[] sampleAnalysisResultLockData(String procInstanceName, String[] resultFieldToRetrieveArr, Object[] curRow){
        String[] fldNameArr=null;
        Object[] fldValueArr=null;
        Integer resultFldPosic = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName());
        Integer resultId=Integer.valueOf(curRow[resultFldPosic].toString());
        
        Object[] lockedByCorrectiveAction = isLockedByCorrectiveAction(procInstanceName, resultFieldToRetrieveArr, curRow);
        if (lockedByCorrectiveAction[0]!=null) return lockedByCorrectiveAction;

        Object[] isLockedByUserCertification = isLockedByUserCertification(procInstanceName, resultFieldToRetrieveArr, curRow);
        if (isLockedByUserCertification[0]!=null) return isLockedByUserCertification;
        
        return new Object[]{fldNameArr, fldValueArr};
    }

    static Object[] isLockedByCorrectiveAction(String procInstanceName, String[] resultFieldToRetrieveArr, Object[] curRow){
        String[] fldNameArr=null;
        Object[] fldValueArr=null;
        Integer resultFldPosic = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName());
        Integer resultId=Integer.valueOf(curRow[resultFldPosic].toString());
        if (!isProgramCorrectiveActionEnable(procInstanceName)) return new Object[]{null, null};
        Object[][] notClosedProgramCorrreciveAction=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.ProgramCorrectiveAction.TBL.getName(), 
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
            return new Object[]{fldNameArr, fldValueArr};
        }
        return new Object[]{null, null};
    }
    
    static Object[] isLockedByUserCertification(String procInstanceName, String[] resultFieldToRetrieveArr, Object[] curRow){
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String[] fldNameArr=null;
        Object[] fldValueArr=null;
        Integer fldPosic = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.SampleAnalysisResult.FLD_METHOD_NAME.getName());
        String methodName=curRow[fldPosic].toString();
        fldPosic = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.SampleAnalysisResult.FLD_METHOD_VERSION.getName());
        Integer methodVersion=Integer.valueOf(curRow[fldPosic].toString());

        Object[] ifUserCertificationEnabled = AnalysisMethodCertif.isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ifUserCertificationEnabled[0].toString())) return new Object[]{null, null};
        
        Object[] userCertified = AnalysisMethodCertif.isUserCertified(methodName, token.getUserName());
        if (Boolean.valueOf(userCertified[0].toString())) return new Object[]{null, null};
                

        fldNameArr=LPArray.addValueToArray1D(fldNameArr, "is_locked");
        fldValueArr=LPArray.addValueToArray1D(fldValueArr, true);
        fldNameArr=LPArray.addValueToArray1D(fldNameArr, "locking_object");
        fldValueArr=LPArray.addValueToArray1D(fldValueArr, TblsCnfg.Methods.TBL.getName());
        fldNameArr=LPArray.addValueToArray1D(fldNameArr, "locking_reason");
            
        JSONObject lockReasonJSONObj = LPFrontEnd.responseJSONDiagnosticLPFalse(                
                AnalysisMethodCertif.CertificationAnalysisMethodErrorTrapping.USER_NOT_CERTIFIED.getErrorCode(), new Object[]{methodName});
        fldValueArr=LPArray.addValueToArray1D(fldValueArr, lockReasonJSONObj);
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
