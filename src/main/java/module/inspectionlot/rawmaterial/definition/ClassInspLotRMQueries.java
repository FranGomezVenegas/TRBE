/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.definition;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.moduleenvmonit.EnvMonAPIqueries.JSON_TAG_SPEC_DEFINITION;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsCnfg;
import databases.TblsData;
import functionaljavaa.materialspec.SpecFrontEndUtilities;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import static module.inspectionlot.rawmaterial.definition.InspLotQueries.configMaterialStructure;
import static module.inspectionlot.rawmaterial.definition.InspLotQueries.dataSampleStructure;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntQueriesObj;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.queries.QueryUtilitiesEnums;
/**
 *
 * @author User
 */
public class ClassInspLotRMQueries  implements EnumIntQueriesObj{
    
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;
    
    private Boolean isSuccess=false;
    private JSONObject responseSuccessJObj=null;
    private JSONArray responseSuccessJArr=null;

    public ClassInspLotRMQueries(HttpServletRequest request, HttpServletResponse response, InspLotRMEnums.InspLotRMQueriesAPIEndpoints endPoint){
        request=LPHttp.requestPreparation(request);
        response = LPHttp.responsePreparation(response);
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        try{
            if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, null, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), ClassInspLotRMQueries.class.getSimpleName()}, procReqInstance.getLanguage(), null);                   
                return;
            }
            this.functionFound=true;
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            this.functionFound=true;
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
                this.diagnostic=(Object[]) argValues[1];
                this.messageDynamicData=new Object[]{argValues[2].toString()};
                return;                        
            }            
            switch (endPoint){            
            case GET_LOT_INFO: 
                String lotName=LPNulls.replaceNull(argValues[0]).toString();
                String fieldsToRetrieveStr=LPNulls.replaceNull(argValues[1].toString());
                Boolean includesSamplesInfo=Boolean.valueOf(LPNulls.replaceNull(argValues[2]).toString());
                Boolean includesMaterialInfo=Boolean.valueOf(LPNulls.replaceNull(argValues[3]).toString());
                if (Boolean.TRUE.equals(includesMaterialInfo) && fieldsToRetrieveStr.length()>0 && Boolean.FALSE.equals(fieldsToRetrieveStr.contains(TblsInspLotRMData.Lot.MATERIAL_NAME.getName())) ) 
                    fieldsToRetrieveStr=fieldsToRetrieveStr + "|"+TblsInspLotRMData.Lot.MATERIAL_NAME.getName();

                JSONObject lotJsonObj = new JSONObject();
                JSONArray lotsJsonArr = new JSONArray();

                EnumIntTableFields[] tableFieldsLot = TblsInspLotRMData.TablesInspLotRMData.LOT.getTableFields();
                String[] fieldsToRetrieveLot = EnumIntTableFields.getAllFieldNames(tableFieldsLot);                
                Object[][] lotInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.LOT, 
                        tableFieldsLot, new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.LOT, new String[]{TblsInspLotRMData.Lot.NAME.getName()}, new Object[]{lotName}),
                        new String[]{TblsInspLotRMData.Lot.NAME.getName()}, null);        
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())){
                    lotJsonObj.put(GlobalAPIsParams.LBL_ERROR, Arrays.toString(lotInfo[0])); 
                    lotsJsonArr.add(lotJsonObj);
                    Rdbms.closeRdbms();  
                    LPFrontEnd.servletReturnSuccess(request, response, lotsJsonArr);                
                    return;
                }
                for (Object[] currLot: lotInfo){

                    JSONObject jLotInfoObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieveLot, currLot);

                    if (LPArray.valueInArray(fieldsToRetrieveLot, TblsInspLotRMData.Lot.MATERIAL_NAME.getName())){
                        String currMaterial=currLot[LPArray.valuePosicInArray(fieldsToRetrieveLot, TblsInspLotRMData.Lot.MATERIAL_NAME.getName())].toString();
                        if (Boolean.TRUE.equals(includesSamplesInfo) && currMaterial!=null && currMaterial.length()>0)
                            jLotInfoObj.put(TblsData.TablesData.SAMPLE.getTableName(), dataSampleStructure(lotName, null, null, new String[]{TblsInspLotRMData.Sample.SAMPLE_ID.getName()}, true, true));
                        if (Boolean.TRUE.equals(includesMaterialInfo) && currMaterial!=null && currMaterial.length()>0)
                            jLotInfoObj.put(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL.getTableName(), configMaterialStructure(currMaterial, null, new String[]{TblsInspLotRMConfig.Material.NAME.getName()}, true, true, true));
                    }
                    lotJsonObj.put("lot_info", jLotInfoObj);
                    lotJsonObj.put("lot_name", jLotInfoObj.get(TblsInspLotRMData.Lot.NAME.getName())); 

                    EnumIntTableFields[] tableFieldsBulk = TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableFields();
                    String[] fieldsToRetrieveBulk = EnumIntTableFields.getAllFieldNames(tableFieldsBulk);                
                    lotInfo=QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK, 
                        tableFieldsBulk, new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK, new String[]{TblsInspLotRMData.LotBulk.LOT_NAME.getName()}, new Object[]{lotName}), 
                        new String[]{TblsInspLotRMData.LotBulk.LOT_NAME.getName()}, null);        
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())){
                        JSONObject jLotSectionInfoObj=new JSONObject();
                        lotJsonObj.put(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(), jLotSectionInfoObj); 
                    }else{
                        JSONArray lotInfoJsonArr = new JSONArray();
                        for (Object[] curRow:lotInfo){
                            JSONObject jLotSectionInfoObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveBulk, curRow);
                            lotInfoJsonArr.add(jLotSectionInfoObj);
                        }
                        lotJsonObj.put(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(), lotInfoJsonArr); 
                    }

                EnumIntTableFields[] tableFieldsSample = TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableFields();
                String[] fieldsToRetrieveSample = EnumIntTableFields.getAllFieldNames(tableFieldsSample);                
                    Object[][] lotSampleInfo=QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.SAMPLE, 
                        tableFieldsSample, new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.SAMPLE, new String[]{TblsInspLotRMData.Sample.LOT_NAME.getName()}, new Object[]{lotName}), 
                        new String[]{TblsInspLotRMData.Sample.SAMPLE_ID.getName()}, null);        
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotSampleInfo[0][0].toString())){
                        JSONObject jLotSampleSectionInfoObj = new JSONObject();
                        lotJsonObj.put(TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableName(), jLotSampleSectionInfoObj); 
                    }else{
                        JSONArray jLotSampleSectionInfoArr = new JSONArray();
                        for (Object[] curRow:lotSampleInfo){
                            JSONObject jLotSampleSectionInfoObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveSample, curRow);
                            Object sampleId=jLotSampleSectionInfoObj.get(TblsInspLotRMData.Sample.SAMPLE_ID.getName());
                            EnumIntTableFields[] tableFieldsSmpAna = TblsData.TablesData.SAMPLE_ANALYSIS.getTableFields();
                            String[] fieldsToRetrieveSmpAna = EnumIntTableFields.getAllFieldNames(tableFieldsSmpAna);                
                            Object[][] sampleAnaInfo=QueryUtilitiesEnums.getTableData(TblsData.TablesData.SAMPLE_ANALYSIS, 
                                tableFieldsSmpAna, 
                                new SqlWhere(TblsData.TablesData.SAMPLE_ANALYSIS, new String[]{TblsInspLotRMData.Sample.SAMPLE_ID.getName()}, new Object[]{Integer.valueOf(sampleId.toString())}), 
                                new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName()}, null);        
                            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAnaInfo[0][0].toString())){
                                JSONObject sampleAnaSectionInfoObj = new JSONObject();
                                jLotSampleSectionInfoObj.put(TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), sampleAnaSectionInfoObj);
                            }else{
                                JSONArray sampleAnaJsonArr = new JSONArray();
                                for (Object[] curRow2:sampleAnaInfo){
                                    JSONObject sampleAnaSectionInfoObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveSmpAna, curRow2);                               
                                    Object testId=sampleAnaSectionInfoObj.get(TblsData.SampleAnalysis.TEST_ID.getName());              
                                    EnumIntTableFields[] tableFieldsSmpAnaRes = TblsInspLotRMData.TablesInspLotRMData.SAMPLE_ANALYSIS_RESULT.getTableFields();
                                    String[] fieldsToRetrieveSmpAnaRes = EnumIntTableFields.getAllFieldNames(tableFieldsSmpAnaRes);                                                
                                    Object[][] sampleAnaResInfo=QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.SAMPLE_ANALYSIS_RESULT, 
                                        tableFieldsSmpAnaRes, 
                                        new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.SAMPLE_ANALYSIS_RESULT, 
                                            new String[]{TblsInspLotRMData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{Integer.valueOf(testId.toString())}), 
                                        new String[]{TblsInspLotRMData.SampleAnalysisResult.RESULT_ID.getName()}, null);        
                                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAnaResInfo[0][0].toString())){
                                        JSONObject sampleAnaResSectionInfoObj = new JSONObject();
                                        jLotSampleSectionInfoObj.put(TblsInspLotRMData.TablesInspLotRMData.SAMPLE_ANALYSIS_RESULT.getTableName(), sampleAnaResSectionInfoObj);
                                    }else{
                                        JSONArray sampleAnaResJsonArr = new JSONArray();
                                        for (Object[] curRow3:sampleAnaResInfo){
                                            JSONObject sampleAnaResSectionInfoObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveSmpAnaRes, curRow3);
                                            sampleAnaResJsonArr.add(sampleAnaResSectionInfoObj);
                                        }
                                        sampleAnaSectionInfoObj.put(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), sampleAnaResJsonArr);

                                    sampleAnaJsonArr.add(sampleAnaSectionInfoObj);
                                }
                                jLotSampleSectionInfoObj.put(TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), sampleAnaJsonArr);
                                }
                            }

                            jLotSampleSectionInfoArr.add(jLotSampleSectionInfoObj);
                        }
                        lotJsonObj.put(TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableName(), jLotSampleSectionInfoArr); 
                    }

                    Object specCode = jLotInfoObj.get(TblsInspLotRMData.Lot.SPEC_CODE.getName());
                    Object specConfigVersion = jLotInfoObj.get(TblsEnvMonitConfig.Program.SPEC_CONFIG_VERSION.getName());                    
                    JSONObject specDefinition = new JSONObject();
                    if (Boolean.FALSE.equals(specCode==null || specCode=="" || specConfigVersion==null || "".equals(specConfigVersion.toString()))){
                        JSONObject specInfo=SpecFrontEndUtilities.configSpecInfo(procReqInstance, (String) specCode, (Integer) specConfigVersion, 
                            null, null);
                        specDefinition.put(TblsCnfg.TablesConfig.SPEC.getTableName(), specInfo);
                        JSONArray specLimitsInfo=SpecFrontEndUtilities.configSpecLimitsInfo(procReqInstance, (String) specCode, (Integer) specConfigVersion, 
                            null, new String[]{TblsCnfg.SpecLimits.VARIATION_NAME.getName(), TblsCnfg.SpecLimits.ANALYSIS.getName(), 
                            TblsCnfg.SpecLimits.METHOD_NAME.getName(), TblsCnfg.SpecLimits.LIMIT_ID.getName(),
                            TblsCnfg.SpecLimits.SPEC_TEXT_EN.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_RED_AREA_EN.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_YELLOW_AREA_EN.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_GREEN_AREA_EN.getName(),
                            TblsCnfg.SpecLimits.SPEC_TEXT_ES.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_RED_AREA_ES.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_YELLOW_AREA_ES.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_GREEN_AREA_ES.getName()});
                        specDefinition.put(TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName(), specLimitsInfo);
                        lotJsonObj.put(JSON_TAG_SPEC_DEFINITION, specDefinition);                               
                    }
                    lotsJsonArr.add(lotJsonObj);
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, lotsJsonArr);
                break;        


            case GET_LOT_SAMPLES_INFO: 
                lotName=LPNulls.replaceNull(argValues[0]).toString();
                fieldsToRetrieveStr=LPNulls.replaceNull(argValues[1].toString());
                Boolean includesSampleAnalysisInfo=Boolean.valueOf(LPNulls.replaceNull(argValues[2]).toString());
                Boolean includesSampleAnalysisResultInfo=Boolean.valueOf(LPNulls.replaceNull(argValues[3]).toString());            
                JSONArray jArr = new JSONArray();
                jArr.add(dataSampleStructure(lotName, null, fieldsToRetrieveStr, new String[]{TblsInspLotRMData.Sample.SAMPLE_ID.getName()}, includesSampleAnalysisInfo, includesSampleAnalysisResultInfo));
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                break;        
            case GET_LOT_AUDIT:
                String[] fieldsToRetrieve;
                lotName=LPNulls.replaceNull(argValues[0]).toString();
                fieldsToRetrieveStr=LPNulls.replaceNull(argValues[1]).toString();
                    if (LPNulls.replaceNull(fieldsToRetrieveStr).length()==0)
                        fieldsToRetrieve=EnumIntTableFields.getAllFieldNames(TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.LOT.getTableFields());
                    else
                        fieldsToRetrieve=fieldsToRetrieveStr.split("\\|");
                fieldsToRetrieve = new String[]{TblsInspLotRMDataAudit.Lot.LOT_NAME.getName(), TblsInspLotRMDataAudit.Lot.AUDIT_ID.getName(), TblsInspLotRMDataAudit.Lot.ACTION_NAME.getName(), TblsInspLotRMDataAudit.Lot.FIELDS_UPDATED.getName()
                        , TblsInspLotRMDataAudit.Lot.REVIEWED.getName(), TblsInspLotRMDataAudit.Lot.REVIEWED_ON.getName(), TblsInspLotRMDataAudit.Lot.DATE.getName(), TblsInspLotRMDataAudit.Lot.PERSON.getName(), TblsInspLotRMDataAudit.Lot.REASON.getName(), TblsInspLotRMDataAudit.Lot.ACTION_PRETTY_EN.getName(), TblsInspLotRMDataAudit.Lot.ACTION_PRETTY_ES.getName()};            
                Object[][] sampleAuditInfo=QueryUtilitiesEnums.getTableData(TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.LOT,
                    EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.LOT, fieldsToRetrieve),
                    new String[]{TblsInspLotRMDataAudit.Lot.LOT_NAME.getName(), TblsInspLotRMDataAudit.Lot.PARENT_AUDIT_ID.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, new Object[]{lotName}, 
                    new String[]{TblsInspLotRMDataAudit.Lot.AUDIT_ID.getName()});
               jArr = new JSONArray();
               if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAuditInfo[0][0].toString())){
                    //jArr.add(sampleAuditInfo[0]);
                    //LPFrontEnd.responseError(sampleAuditInfo, language, procInstanceName);
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;                       
               }
               for (Object[] curRow: sampleAuditInfo){
                JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRow,
                        new String[]{TblsInspLotRMDataAudit.Lot.FIELDS_UPDATED.getName()});
                Object[] convertToJsonObjectStringedObject = LPJson.convertToJsonObjectStringedObject(curRow[LPArray.valuePosicInArray(fieldsToRetrieve, TblsInspLotRMDataAudit.Lot.FIELDS_UPDATED.getName())].toString());
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(convertToJsonObjectStringedObject[0].toString()))
                    jObj.put(TblsInspLotRMDataAudit.Lot.FIELDS_UPDATED.getName(), convertToJsonObjectStringedObject[1]);            


                Integer curAuditId=Integer.valueOf(jObj.get(TblsInspLotRMDataAudit.Lot.AUDIT_ID.getName()).toString());
                    Object[][] sampleAuditInfoLvl2=QueryUtilitiesEnums.getTableData(TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.LOT,
                        EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.LOT, fieldsToRetrieve),
                        new String[]{TblsInspLotRMDataAudit.Lot.PARENT_AUDIT_ID.getName()}, new Object[]{curAuditId}, 
                        new String[]{TblsInspLotRMDataAudit.Lot.AUDIT_ID.getName()});
                    JSONArray jArrLvl2 = new JSONArray();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAuditInfoLvl2[0][0].toString())){
                        //Object[] childJObj=new Object[]{null, null, "No child", "", "", "", null, "", "", null, null};
                        //for (int iChild=childJObj.length;iChild<fieldsToRetrieve.length;iChild++)
                        //    childJObj=LPArray.addValueToArray1D(childJObj, null);      
                        Object[] childJObj=new Object[fieldsToRetrieve.length];
                        childJObj[2] = "No child";
                        JSONObject jObjLvl2=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, childJObj); 
                        jArrLvl2.add(jObjLvl2);
                    }else{
                        for (Object[] curRowLvl2: sampleAuditInfoLvl2){
                            JSONObject jObjLvl2=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRowLvl2,
                                new String[]{TblsInspLotRMDataAudit.Lot.FIELDS_UPDATED.getName()});  
                            Object[] convertToJsonObjectStringedObjectLvl2 = LPJson.convertToJsonObjectStringedObject(curRowLvl2[LPArray.valuePosicInArray(fieldsToRetrieve, TblsInspLotRMDataAudit.Lot.FIELDS_UPDATED.getName())].toString());
                            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(convertToJsonObjectStringedObjectLvl2[0].toString()))
                                jObjLvl2.put(TblsInspLotRMDataAudit.Lot.FIELDS_UPDATED.getName(), convertToJsonObjectStringedObjectLvl2[1]);            
                            jArrLvl2.add(jObjLvl2);
                        }
                    }
                    jObj.put("sublevel", jArrLvl2);
                jArr.add(jObj);
               }
               Rdbms.closeRdbms();
               LPFrontEnd.servletReturnSuccess(request, response, jArr);
               return;



            default:   
                procReqInstance.killIt();
                    LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), 
                        new Object[]{actionName, this.getClass().getSimpleName()}, language, 
                        ClassInspLotRMQueries.class.getSimpleName());
        }
        }finally{
            procReqInstance.killIt();
        }
    }
    @Override    public Object[] getMessageDynamicData() {
        return messageDynamicData;
    }
    @Override    public RelatedObjects getRelatedObj() {
        return relatedObj;
    }
    @Override    public Boolean getEndpointExists() {
        return endpointExists;
    }
    @Override    public Object[] getDiagnostic() {
        return diagnostic;
    }
    @Override    public Boolean getFunctionFound() {
        return this.functionFound;
    }
    @Override    public Boolean getIsSuccess() {
        return this.isSuccess;
    }
    @Override    public JSONObject getResponseSuccessJObj() {
        return this.responseSuccessJObj;
    }
    @Override    public JSONArray getResponseSuccessJArr() {
        return this.responseSuccessJArr;
    }
    @Override    public Object[] getResponseError() {
        return this.diagnostic;
    }
}