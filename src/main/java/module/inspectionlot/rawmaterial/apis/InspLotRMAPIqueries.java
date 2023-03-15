/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.apis;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.moduleenvmonit.EnvMonAPIqueries.JSON_TAG_SPEC_DEFINITION;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import static module.inspectionlot.rawmaterial.definition.InspLotQueries.configMaterialStructure;
import static module.inspectionlot.rawmaterial.definition.InspLotQueries.dataSampleStructure;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.InspLotRMQueriesAPIEndpoints;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMConfig;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsCnfg;
import databases.TblsData;
import functionaljavaa.materialspec.SpecFrontEndUtilities;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMDataAudit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import trazit.queries.QueryUtilitiesEnums;
/**
 *
 * @author User
 */
public class InspLotRMAPIqueries extends HttpServlet {

    public enum InspLotRMAPIqueriesEndpoints implements EnumIntEndpoints{
        ACTIVE_BATCH_LIST("ACTIVE_BATCH_LIST", "", new LPAPIArguments[]{}),
        ;
        private InspLotRMAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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
        @Override        public String getName(){return this.name;}
        @Override        public String getSuccessMessageCode(){return this.successMessageCode;}           
        @Override        public LPAPIArguments[] getArguments() {return arguments;} 
        @Override        public String getApiUrl(){return ApiUrls.INSPLOT_RM_QUERIES.getUrl();}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;

        @Override        public JsonArray getOutputObjectTypes() {return EndPointsToRequirements.endpointWithNoOutputObjects;}
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
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        String procInstanceName = procReqInstance.getProcedureInstance();
        

        try (PrintWriter out = response.getWriter()) {

        InspLotRMQueriesAPIEndpoints endPoint = null;
        try{
            endPoint = InspLotRMQueriesAPIEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;                   
        }
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                             

        if (!LPFrontEnd.servletStablishDBConection(request, response))return;

        switch (endPoint){            
        case GET_LOT_INFO: 
            String lotName=LPNulls.replaceNull(argValues[0]).toString();
            String fieldsToRetrieveStr=LPNulls.replaceNull(argValues[1].toString());
            Boolean includesSamplesInfo=Boolean.valueOf(LPNulls.replaceNull(argValues[2]).toString());
            Boolean includesMaterialInfo=Boolean.valueOf(LPNulls.replaceNull(argValues[3]).toString());
            if (includesMaterialInfo && fieldsToRetrieveStr.length()>0 && !fieldsToRetrieveStr.contains(TblsInspLotRMData.Lot.MATERIAL_NAME.getName()))
                fieldsToRetrieveStr=fieldsToRetrieveStr + "|"+TblsInspLotRMData.Lot.MATERIAL_NAME.getName();

            EnumIntTableFields[] tableFieldsLot = TblsInspLotRMData.TablesInspLotRMData.LOT.getTableFields();
            String[] fieldsToRetrieveLot = EnumIntTableFields.getAllFieldNames(tableFieldsLot);                
            Object[][] lotInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.LOT, 
                    tableFieldsLot, new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.LOT, new String[]{TblsInspLotRMData.Lot.NAME.getName()}, new Object[]{lotName}),
                    new String[]{TblsInspLotRMData.Lot.NAME.getName()}, null);        

            JSONArray lotsJsonArr = new JSONArray();            
            for (Object[] currLot: lotInfo){
                JSONObject lotJsonObj = new JSONObject();  
                JSONObject jLotInfoObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieveLot, currLot);
                
                if (LPArray.valueInArray(fieldsToRetrieveLot, TblsInspLotRMData.Lot.MATERIAL_NAME.getName())){
                    String currMaterial=currLot[LPArray.valuePosicInArray(fieldsToRetrieveLot, TblsInspLotRMData.Lot.MATERIAL_NAME.getName())].toString();
                    if (includesSamplesInfo && currMaterial!=null && currMaterial.length()>0)
                        jLotInfoObj.put(TblsData.TablesData.SAMPLE.getTableName(), dataSampleStructure(lotName, null, null, new String[]{TblsInspLotRMData.Sample.SAMPLE_ID.getName()}, true, true));
                    if (includesMaterialInfo && currMaterial!=null && currMaterial.length()>0)
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
                            JSONObject SampleAnaSectionInfoObj = new JSONObject();
                            jLotSampleSectionInfoObj.put(TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), SampleAnaSectionInfoObj);
                        }else{
                            JSONArray sampleAnaJsonArr = new JSONArray();
                            for (Object[] curRow2:sampleAnaInfo){
                                JSONObject SampleAnaSectionInfoObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveSmpAna, curRow2);                               
                                Object testId=SampleAnaSectionInfoObj.get(TblsData.SampleAnalysis.TEST_ID.getName());              
                                EnumIntTableFields[] tableFieldsSmpAnaRes = TblsInspLotRMData.TablesInspLotRMData.SAMPLE_ANALYSIS_RESULT.getTableFields();
                                String[] fieldsToRetrieveSmpAnaRes = EnumIntTableFields.getAllFieldNames(tableFieldsSmpAnaRes);                                                
                                Object[][] sampleAnaResInfo=QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.SAMPLE_ANALYSIS_RESULT, 
                                    tableFieldsSmpAnaRes, 
                                    new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.SAMPLE_ANALYSIS_RESULT, 
                                        new String[]{TblsInspLotRMData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{Integer.valueOf(testId.toString())}), 
                                    new String[]{TblsInspLotRMData.SampleAnalysisResult.RESULT_ID.getName()}, null);        
                                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAnaResInfo[0][0].toString())){
                                    JSONObject SampleAnaResSectionInfoObj = new JSONObject();
                                    jLotSampleSectionInfoObj.put(TblsInspLotRMData.TablesInspLotRMData.SAMPLE_ANALYSIS_RESULT.getTableName(), SampleAnaResSectionInfoObj);
                                }else{
                                    JSONArray sampleAnaResJsonArr = new JSONArray();
                                    for (Object[] curRow3:sampleAnaResInfo){
                                        JSONObject SampleAnaResSectionInfoObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveSmpAnaRes, curRow3);
                                        sampleAnaResJsonArr.add(SampleAnaResSectionInfoObj);
                                    }
                                    SampleAnaSectionInfoObj.put(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), sampleAnaResJsonArr);
                                
                                sampleAnaJsonArr.add(SampleAnaSectionInfoObj);
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
                if (!(specCode==null || specCode=="" || specConfigVersion==null || "".equals(specConfigVersion.toString()))){
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
            {
                if (LPNulls.replaceNull(fieldsToRetrieveStr).length()==0)
                    fieldsToRetrieve=EnumIntTableFields.getAllFieldNames(TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.LOT.getTableFields());
                else
                    fieldsToRetrieve=fieldsToRetrieveStr.split("\\|");
            }
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
            Rdbms.closeRdbms(); 
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
    }
    }catch(Exception e){      
        String exceptionMessage =e.getMessage();
        if (exceptionMessage==null){exceptionMessage="null exception";}
        response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
        procReqInstance.killIt();
        LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);      
    } finally {
       // release database resources
       try {
           procReqInstance.killIt();
        } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
       }
    }              
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
         try {
        processRequest(request, response);
         }catch(ServletException|IOException e){Logger.getLogger(e.getMessage());}
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
