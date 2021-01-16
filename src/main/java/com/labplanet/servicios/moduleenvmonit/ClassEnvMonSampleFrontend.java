/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.JSON_TAG_NAME_SAMPLE_RESULTS;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData.ViewSampleMicroorganismList;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsProcedure;
import databases.Token;
import functionaljavaa.instruments.incubator.DataIncubatorNoteBook;
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
import static lbplanet.utilities.LPDate.dateStringFormatToLocalDateTime;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import static lbplanet.utilities.LPFrontEnd.noRecordsInTableMessage;
import trazit.session.ProcedureRequestSession;
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
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN;

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
                }),
        GET_MICROORGANISM_LIST("GET_MICROORGANISM_LIST", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            }),
        GET_SAMPLE_MICROORGANISM_VIEW("GET_SAMPLE_MICROORGANISM_VIEW", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 7),
            }),
        GET_SAMPLE_STAGES_SUMMARY_REPORT("GET_SAMPLE_STAGES_SUMMARY_REPORT", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            }),
        GET_BATCH_REPORT("GET_BATCH_REPORT", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_FIELD_TO_DISPLAY, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8),
            }),
        GET_PRODLOT_REPORT("GET_PRODLOT_REPORT", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PRODLOT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PRODLOT_FIELD_TO_DISPLAY, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8),
            }),
        GET_INCUBATOR_REPORT("GET_INCUBATOR_REPORT", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCUBATOR_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCUBATOR_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCUBATOR_FIELD_TO_DISPLAY, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_START, LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_END, LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
            }),
        STATS_SAMPLES_PER_STAGE("STATS_SAMPLES_PER_STAGE", new LPAPIArguments[]{
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_STAGES_TO_INCLUDE, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_STAGES_TO_EXCLUDE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
            }),
        STATS_PROGRAM_LAST_RESULTS("STATS_PROGRAM_LAST_RESULTS", new LPAPIArguments[]{
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_GROUPED, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_TOTAL_OBJECTS, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
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
        private EnvMonSampleAPIFrontendEndpoints(String name, LPAPIArguments[] argums){
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
    
    public ClassEnvMonSampleFrontend(HttpServletRequest request, EnvMonSampleAPIFrontendEndpoints endPoint){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        RelatedObjects rObj=RelatedObjects.getInstanceForActions();

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
                    actionDiagnoses = DataProgramCorrectiveAction.markAsCompleted(procInstanceName, token, correctiveActionId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){                        
                        Object[][] correctiveActionInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_PROCEDURE), TblsEnvMonitProcedure.ProgramCorrectiveAction.TBL.getName(), 
                            new String[]{TblsEnvMonitProcedure.ProgramCorrectiveAction.FLD_ID.getName()}, new Object[]{correctiveActionId},
                            new String[]{TblsEnvMonitProcedure.ProgramCorrectiveAction.FLD_SAMPLE_ID.getName()});
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{correctiveActionId, correctiveActionInfo[0][0], procInstanceName}); 
                        this.messageDynamicData=new Object[]{correctiveActionId, correctiveActionInfo[0][0], procInstanceName};   
                    }else{
                        this.messageDynamicData=new Object[]{correctiveActionId, procInstanceName};                           
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
                    Object[][] analysisResultList = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(),
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
                        Object[] resultLockData=sampleAnalysisResultLockData(procInstanceName, resultFieldToRetrieveArr, curRow);
                        JSONObject row=new JSONObject();
                        if (resultLockData!=null)
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
                  String[] fieldsToRetrieve=new String[]{TblsEnvMonitConfig.MicroOrganism.FLD_NAME.getName()};
                  Object[][] list = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.MicroOrganism.TBL.getName(), 
                          new String[]{TblsEnvMonitConfig.MicroOrganism.FLD_NAME.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{}
                          , fieldsToRetrieve, fieldsToRetrieve);
                  JSONArray jArr=new JSONArray();
                  for (Object[] curRec: list){
                    JSONObject jObj= LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRec);
                    jArr.add(jObj);
                  }
                  this.isSuccess=true;
                  this.responseSuccessJArr=jArr;
                  return;  
                case GET_SAMPLE_MICROORGANISM_VIEW:
                    String whereFieldsName = argValues[0].toString(); 
                    if (whereFieldsName==null){whereFieldsName="";}
                    String whereFieldsValue = argValues[1].toString(); 
                    String[] whereFieldsNameArr=new String[0];
                    Object[] whereFieldsValueArr=new Object[0];
                    if ( (whereFieldsName!=null && whereFieldsName.length()>0) && (whereFieldsValue!=null && whereFieldsValue.length()>0) ){
                        whereFieldsNameArr=LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
                        whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|")));                                          
                        for (int iFields=0; iFields<whereFieldsNameArr.length; iFields++){
                            if (LPPlatform.isEncryptedField(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsData.Sample.TBL.getName(), whereFieldsNameArr[iFields])){                
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
                    fieldsToRetrieve = new String[0];
                    for (ViewSampleMicroorganismList fieldsList1 : fieldsList) {
                      if (!"TBL".equalsIgnoreCase(fieldsList1.name())) {
                        fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fieldsList1.getName());
                      }                  
                    }
                    list = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.ViewSampleMicroorganismList.TBL.getName(), 
                            whereFieldsNameArr, whereFieldsValueArr
                            , fieldsToRetrieve
                            , new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.FLD_SAMPLE_ID.getName()} );
                    jArr=new JSONArray();
                    for (Object[] curRec: list){
                      JSONObject jObj= LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRec);
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
                    Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.Sample.TBL.getName(), 
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
                    String[] sampleStageTimingCaptureAllFlds=TblsEnvMonitProcedure.SampleStageTimingCapture.getAllFieldNames();
                    JSONObject jObjMainObject2=new JSONObject();                    
                    
                    if (smpStage.isSampleStagesEnable()){
                        Object[][] sampleStageInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_PROCEDURE), TblsEnvMonitProcedure.SampleStageTimingCapture.TBL.getName(), 
                                new String[]{TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                                sampleStageTimingCaptureAllFlds, new String[]{TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_ID.getName()});                    
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
                    Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.IncubBatch.TBL.getName(), 
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
                        Object[][] instrReadings=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), 
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
                    Object[][] prodLotInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.ProductionLot.TBL.getName(), 
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
/*                    Object[][] prodLotInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.ProductionLot.TBL.getName(), 
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
                    
                    sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.Sample.TBL.getName(), 
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
                            Object[][] groupedInfo = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.Sample.TBL.getName(), 
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
                    Object[][] incubInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
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
                    Object[][] samplesCounterPerStage=Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.Sample.TBL.getName(), 
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

                        Object[][] programLastResults=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(), 
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
                        Object[][] specLimits=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_CONFIG), TblsCnfg.SpecLimits.TBL.getName(), 
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
                            Object[][] programLastResults=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(), 
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
            Object[][] sampleStageInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.ViewSampleMicroorganismList.TBL.getName(), 
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
        String[] fldNameArr=new String[0];
        Object[] fldValueArr=new Object[0];
        Integer resultFldPosic = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName());
        Integer resultId=Integer.valueOf(curRow[resultFldPosic].toString());
        
        if (!isProgramCorrectiveActionEnable(procInstanceName)) return new Object[]{fldNameArr, fldValueArr};
        Object[][] notClosedProgramCorrreciveAction=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_PROCEDURE), TblsProcedure.ProgramCorrectiveAction.TBL.getName(), 
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
