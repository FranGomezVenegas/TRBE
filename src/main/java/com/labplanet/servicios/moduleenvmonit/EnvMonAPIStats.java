/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsData;
import databases.TblsProcedure;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import static lbplanet.utilities.LPFrontEnd.noRecordsInTableMessage;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import static lbplanet.utilities.LPKPIs.getKPIs;
import static lbplanet.utilities.LPKPIs.getRecoveryRate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class EnvMonAPIStats extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     */
    public enum EnvMonAPIstatsEndpoints implements EnumIntEndpoints{
        /**
         *
         */        
        QUERY_SAMPLING_HISTORY("QUERY_SAMPLING_HISTORY", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_AREA, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_LOCATION_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_GROUPS, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_SAMPLES, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_SAMPLER_SAMPLES, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLING_DAY_START, LPAPIArguments.ArgumentType.STRING.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLING_DAY_END, LPAPIArguments.ArgumentType.STRING.toString(), false, 13),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOGIN_DAY_START, LPAPIArguments.ArgumentType.STRING.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOGIN_DAY_END, LPAPIArguments.ArgumentType.STRING.toString(), false, 13),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_READING_NOT_ENTERED, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 14),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_EQUAL, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 15),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_MIN, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 16),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_MAX, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 17),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_MICROORGANISMS, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 18),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISMS_TO_FIND, LPAPIArguments.ArgumentType.STRING.toString(), false, 19),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OUTPUT_IS_FILE, LPAPIArguments.ArgumentType.STRING.toString(), false, 20),
                }, EndPointsToRequirements.endpointWithNoOutputObjects),
        QUERY_SAMPLER_SAMPLING_HISTORY("QUERY_SAMPLER_SAMPLING_HISTORY", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLER, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLER_AREA, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_AREA, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_LOCATION_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRING.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_GROUPS, LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLING_DAY_START, LPAPIArguments.ArgumentType.STRING.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLING_DAY_END, LPAPIArguments.ArgumentType.STRING.toString(), false, 13),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_READING_NOT_ENTERED, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 14),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_EQUAL, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 15),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_MIN, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 16),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_MAX, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 17),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_MICROORGANISMS, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 18),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISMS_TO_FIND, LPAPIArguments.ArgumentType.STRING.toString(), false, 19),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OUTPUT_IS_FILE, LPAPIArguments.ArgumentType.STRING.toString(), false, 20),
                }, EndPointsToRequirements.endpointWithNoOutputObjects),        
        QUERY_READING_OUT_OF_RANGE("QUERY_READING_OUT_OF_RANGE", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLER, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLER_AREA, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_AREA, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_LOCATION_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRING.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_GROUPS, LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLING_DAY_START, LPAPIArguments.ArgumentType.STRING.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLING_DAY_END, LPAPIArguments.ArgumentType.STRING.toString(), false, 13),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_READING_NOT_ENTERED, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 14),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_EQUAL, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 15),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_MIN, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 16),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_MAX, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 17),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_MICROORGANISMS, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 18),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISMS_TO_FIND, LPAPIArguments.ArgumentType.STRING.toString(), false, 19),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OUTPUT_IS_FILE, LPAPIArguments.ArgumentType.STRING.toString(), false, 20),
                }, EndPointsToRequirements.endpointWithNoOutputObjects),
        KPI_PRODUCTION_LOT_SAMPLES("KPI_PRODUCTION_LOT_SAMPLES", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_AREA, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_LOCATION_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROD_LOT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRING.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_GROUPS, LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_SAMPLER_SAMPLES, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLING_DAY_START, LPAPIArguments.ArgumentType.STRING.toString(), false, 13),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLING_DAY_END, LPAPIArguments.ArgumentType.STRING.toString(), false, 14),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_READING_NOT_ENTERED, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 15),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_EQUAL, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 16),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_MIN, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 17),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_MAX, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 18),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_MICROORGANISMS, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 19),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISMS_TO_FIND, LPAPIArguments.ArgumentType.STRING.toString(), false, 20),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OUTPUT_IS_FILE, LPAPIArguments.ArgumentType.STRING.toString(), false, 21),
                }, EndPointsToRequirements.endpointWithNoOutputObjects),        
        QUERY_INVESTIGATION("QUERY_INVESTIGATION", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_CREATION_DAY_START, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_CREATION_DAY_END, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_CLOSURE_DAY_START, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_CLOSURE_DAY_END, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_NOT_CLOSED_YET, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INVESTIGATION_GROUPS, LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
/*                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_GROUPS, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_SAMPLES, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_SAMPLER_SAMPLES, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_READING_NOT_ENTERED, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 14),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_EQUAL, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 15),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_MIN, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 16),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_READING_MAX, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 17),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_MICROORGANISMS, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 18),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISMS_TO_FIND, LPAPIArguments.ArgumentType.STRING.toString(), false, 19),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OUTPUT_IS_FILE, LPAPIArguments.ArgumentType.STRING.toString(), false, 20),
*/ 
                }, EndPointsToRequirements.endpointWithNoOutputObjects),
        KPIS("KPIS", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OBJ_GROUP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABLE_CATEGORY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABLE_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELDS_TO_RETRIEVE_OR_GROUPING, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_GROUPED, LPAPIArguments.ArgumentType.BOOLEANARR.toString(), true, 11),
                new LPAPIArguments("addRecoveryRate", LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 12),
                new LPAPIArguments("rr_"+GlobalAPIsParams.REQUEST_PARAM_FIELDS_TO_RETRIEVE_OR_GROUPING, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 13),
                new LPAPIArguments("showAbsence", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 14),
                new LPAPIArguments("showPresence", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 15),
                new LPAPIArguments("showIN", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 16),
                new LPAPIArguments("showOUT", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 17),
                new LPAPIArguments("percNumDecimals", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 18),
                }, EndPointsToRequirements.endpointWithNoOutputObjects),        
        RECOVERY_RATE("RECOVERY_RATE", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELDS_TO_RETRIEVE_OR_GROUPING, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments("showAbsence", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 9),
                new LPAPIArguments("showPresence", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 10),
                new LPAPIArguments("showIN", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 11),
                new LPAPIArguments("showOUT", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 12),
                new LPAPIArguments("percNumDecimals", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 13),
                
                }, EndPointsToRequirements.endpointWithNoOutputObjects),                            
        ;
        private EnvMonAPIstatsEndpoints(String name, LPAPIArguments[] argums, JsonArray outputObjectTypes){
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
        public LPAPIArguments[] getArguments() {return arguments;}
        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        private final String name;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;        

        @Override
        public String getSuccessMessageCode() {
            return "Not supported yet."; //To change body of generated methods, choose Tools | Templates.
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response){
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

            
        EnvMonAPIstatsEndpoints endPoint = null;
        try{
            endPoint = EnvMonAPIstatsEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
            return;                   
        }
        Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);
            return;
        }        
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        JSONObject jObjMainObject=new JSONObject();
        try { //try (PrintWriter out = response.getWriter()) {
            String[] filterFieldName=new String[]{};
            Object[] filterFieldValue=new Object[]{};
            String prodLotName="";
            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}
            String smpTemplate=Parameter.getBusinessRuleProcedureFile(procInstanceName, "procedure", "SampleTemplate");  
            String samplerSmpTemplate=Parameter.getBusinessRuleProcedureFile(procInstanceName, "procedure", "samplerSampleTemplate");  
            Boolean getSampleInfo=false;
            Boolean getInvestigationInfo=false;
            Boolean getRecoveryRate=false;
            Boolean showAbsence=true;
            Boolean showPresence=true;
            Boolean showIN=true;
            Boolean showOUT=true;
            String[] RRobjGroupName=new String[]{};
            String RRwhereFieldsName="";
            String RRwhereFieldsValue="";
            String[] whereFieldsNameArr=new String[]{};
            String[] whereFieldsValueArr=new String[]{};
            Integer percNumDecimals=null;
            switch (endPoint){
                case QUERY_SAMPLING_HISTORY: 
                    getSampleInfo=true;
                    getInvestigationInfo=false;
                    break;
                case QUERY_READING_OUT_OF_RANGE:
                    getSampleInfo=true;
                    getInvestigationInfo=false;
                    filterFieldName=new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SPEC_EVAL.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.NOT_IN.getSqlClause()};
                    filterFieldValue=new Object[]{"IN"};
                    break;
                case QUERY_SAMPLER_SAMPLING_HISTORY: 
                    getSampleInfo=true;
                    getInvestigationInfo=false;
                    filterFieldName=new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLE_CONFIG_CODE.getName()};
                    filterFieldValue=new Object[]{samplerSmpTemplate};
                    break;
                case KPI_PRODUCTION_LOT_SAMPLES: 
                    getSampleInfo=true;
                    getInvestigationInfo=false;
                    prodLotName=argValues[0].toString();
                    filterFieldName=new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_PRODUCTION_LOT.getName()};
                    filterFieldValue=new Object[]{prodLotName};
                    String prodLotFieldToRetrieve = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_PROD_LOT_FIELD_TO_RETRIEVE);
                    String[] prodLotFieldToRetrieveArr=new String[0];
                    if ((prodLotFieldToRetrieve!=null) && (prodLotFieldToRetrieve.length()>0))
                        if ("ALL".equalsIgnoreCase(prodLotFieldToRetrieve)) prodLotFieldToRetrieveArr=TblsEnvMonitData.ProductionLot.getAllFieldNames();
                        else prodLotFieldToRetrieveArr=prodLotFieldToRetrieve.split("\\|");
                    if (prodLotFieldToRetrieve==null)
                        prodLotFieldToRetrieveArr=TblsEnvMonitData.ProductionLot.getAllFieldNames();
                    JSONObject jObj=new JSONObject();
                    if (!prodLotName.contains("rutina")){
                        Object[][] prodLotInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.ProductionLot.TBL.getName(), 
                                new String[]{TblsEnvMonitData.ProductionLot.FLD_LOT_NAME.getName()}, new Object[]{prodLotName}
                                , prodLotFieldToRetrieveArr, new String[]{TblsEnvMonitData.ProductionLot.FLD_CREATED_ON.getName()+" desc"} ); 
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(prodLotInfo[0][0].toString())){
                             jObj= noRecordsInTableMessage();                    
                        }else{
                           for (Object[] curRec: prodLotInfo){
                             jObj= LPJson.convertArrayRowToJSONObject(prodLotFieldToRetrieveArr, curRec);
                           }
                        }
                        jObjMainObject.put(TblsEnvMonitData.ProductionLot.TBL.getName(), jObj);
                    }
                    JSONObject jObjRecoveryData = getRecoveryRate(new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_PRODUCTION_LOT.getName()}, 
                        TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_PRODUCTION_LOT.getName(),
                        prodLotName,
                        true, true, true, true, 2);
                    jObjMainObject.put("recovery_rate", jObjRecoveryData);
                    break; 
 
                case QUERY_INVESTIGATION:
                    getSampleInfo=false;
                    getInvestigationInfo=true;                    
                    //filterFieldName=new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SPEC_EVAL.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.NOT_IN.getSqlClause()};
                    //filterFieldValue=new Object[]{"IN"};                    
                    break;
                case KPIS: 
                    getSampleInfo=true;                    
                    getInvestigationInfo=false;
                    getRecoveryRate=true;
                    jObjMainObject=new JSONObject();
                    String[] objGroupName = LPNulls.replaceNull(argValues[0]).toString().split("\\/");
                    String[] tblCategory=argValues[1].toString().split("\\/");
                    String[] tblName=argValues[2].toString().split("\\/");
                    whereFieldsNameArr=argValues[3].toString().split("\\/");
                    whereFieldsValueArr=argValues[4].toString().split("\\/");
                    String[] fldToRetrieve=argValues[5].toString().split("\\/");
                    String[] dataGrouped=argValues[6].toString().split("\\/");
                    getRecoveryRate=Boolean.valueOf(argValues[7].toString());
                    RRobjGroupName = LPNulls.replaceNull(argValues[8]).toString().split("\\|");
                    RRwhereFieldsName=argValues[3].toString();
                    RRwhereFieldsValue=argValues[4].toString();
                    showAbsence=Boolean.valueOf(LPNulls.replaceNull(argValues[9]).toString());
                    showPresence=Boolean.valueOf(LPNulls.replaceNull(argValues[10]).toString());
                    showIN=Boolean.valueOf(LPNulls.replaceNull(argValues[11]).toString());
                    showOUT=Boolean.valueOf(LPNulls.replaceNull(argValues[12]).toString());
                    if (LPNulls.replaceNull(argValues[13]).toString().length()>0)
                        percNumDecimals=Integer.valueOf(argValues[13].toString());

                    jObjMainObject=getKPIs(objGroupName, tblCategory, tblName, whereFieldsNameArr, whereFieldsValueArr, 
                        fldToRetrieve, dataGrouped);
                    break;
                    //LPFrontEnd.servletReturnSuccess(request, response, jObjMainObject);
                case RECOVERY_RATE:
                    RRobjGroupName = LPNulls.replaceNull(argValues[0]).toString().split("\\|");
                    RRwhereFieldsName = LPNulls.replaceNull(argValues[1]).toString();
                    RRwhereFieldsValue = LPNulls.replaceNull(argValues[2]).toString();
                    showAbsence=Boolean.valueOf(LPNulls.replaceNull(argValues[3]).toString());
                    showPresence=Boolean.valueOf(LPNulls.replaceNull(argValues[4]).toString());
                    showIN=Boolean.valueOf(LPNulls.replaceNull(argValues[5]).toString());
                    showOUT=Boolean.valueOf(LPNulls.replaceNull(argValues[6]).toString());
                    if (LPNulls.replaceNull(argValues[7]).toString().length()>0)
                        percNumDecimals=Integer.valueOf(argValues[7].toString());
                    /*                    
                String creationDayStart = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_CREATION_DAY_START);
                String creationDayEnd = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_CREATION_DAY_END);
                Object[] buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsProcedure.Investigation.FLD_CREATED_ON.getName(), creationDayStart, creationDayEnd);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString()))
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, buildDateRangeFromStrings);
                if (buildDateRangeFromStrings.length>=2){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, buildDateRangeFromStrings[1].toString());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,buildDateRangeFromStrings[2]);
                }
                if (buildDateRangeFromStrings.length==4)
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,buildDateRangeFromStrings[3]);
*/                    
                    //LPFrontEnd.servletReturnSuccess(request, response, jObjMainObject);
                    getRecoveryRate=true;
                    getSampleInfo=true;




            }
            JSONObject jObj=new JSONObject();
            if (getRecoveryRate){
                    JSONObject jObjRecoveryData = getRecoveryRate(RRobjGroupName, RRwhereFieldsName, RRwhereFieldsValue, 
                        showAbsence, showPresence, showIN, showOUT, percNumDecimals);
                    jObjMainObject.put("recovery_rate", jObjRecoveryData);
            }
            Object[][] sampleInfo=new Object[0][0];
            if (getSampleInfo){
                String areaName = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_AREA);
                if (areaName!=null && areaName.length()>0){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_AREA.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,areaName);
                }
                String locName = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_LOCATION_NAME);
                if (locName!=null && locName.length()>0){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_LOCATION_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,locName);
                }
                String sampleFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE);                    
                String[] sampleFieldToRetrieveArr=new String[0];
                if ((sampleFieldToRetrieve!=null) && (sampleFieldToRetrieve.length()>0))
                    if ("ALL".equalsIgnoreCase(sampleFieldToRetrieve)) sampleFieldToRetrieveArr=TblsData.ViewSampleAnalysisResultWithSpecLimits.getAllFieldNames();
                    else sampleFieldToRetrieveArr=sampleFieldToRetrieve.split("\\|");
                if (sampleFieldToRetrieve==null)
                    sampleFieldToRetrieveArr=TblsData.ViewSampleAnalysisResultWithSpecLimits.getAllFieldNames();

                String samplingDayStart = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLING_DAY_START);
                String samplingDayEnd = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLING_DAY_END);
                Object[] buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLING_DATE.getName(), samplingDayStart, samplingDayEnd);
                //if (LPPlatform.LAB_FALSE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString()))
                    //LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, buildDateRangeFromStrings);
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString()) && (buildDateRangeFromStrings.length>=2)){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, buildDateRangeFromStrings[1].toString());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,buildDateRangeFromStrings[2]);
                }
                if (buildDateRangeFromStrings.length==4)
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,buildDateRangeFromStrings[3]);

                String loginDayStart = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_LOGIN_DAY_START);
                String loginDayEnd = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_LOGIN_DAY_END);
                buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_LOGGED_ON.getName(), loginDayStart, loginDayEnd);
                //if (LPPlatform.LAB_FALSE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString()))
                //    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, buildDateRangeFromStrings);
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString()) && (buildDateRangeFromStrings.length>=2)){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, buildDateRangeFromStrings[1].toString());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,buildDateRangeFromStrings[2]);
                }
                if (buildDateRangeFromStrings.length==4)
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,buildDateRangeFromStrings[3]);

                String includeSamples = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_SAMPLER_SAMPLES);
                if (includeSamples!=null && includeSamples.length()>0 && Boolean.valueOf(includeSamples)){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLE_CONFIG_CODE.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,smpTemplate);
                }
                String excludeSamplerSamples = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_SAMPLER_SAMPLES);
                if (excludeSamplerSamples!=null && excludeSamplerSamples.length()>0 && Boolean.valueOf(excludeSamplerSamples)){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLE_CONFIG_CODE.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.NOT_IN.getSqlClause());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,samplerSmpTemplate);
                }

                String samplerName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLER);
                if (samplerName!=null && samplerName.length()>0){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLER.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,samplerName);
                }
                String samplerArea = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLER_AREA);
                if (samplerArea!=null && samplerArea.length()>0){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLER_AREA.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,samplerArea);
                }
                String readingEqual = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_READING_EQUAL);
                if (readingEqual!=null && readingEqual.length()>0){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_RAW_VALUE.getName());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,readingEqual);
                }
                String readingMin = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_READING_MIN);
                if (readingMin!=null && readingMin.length()>0){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_RAW_VALUE_NUM.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.GREATER_THAN.getSqlClause());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,Integer.valueOf(readingMin));
                }
                String readingMax = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_READING_MAX);
                if (readingMax!=null && readingMax.length()>0){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_RAW_VALUE_NUM.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.LESS_THAN.getSqlClause());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,Integer.valueOf(readingMax));
                }
                String excludeReadingNotEntered = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_READING_NOT_ENTERED);
                if (excludeReadingNotEntered!=null && excludeReadingNotEntered.length()>0 && Boolean.valueOf(excludeReadingNotEntered)){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_RAW_VALUE.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,"");                
                }
                String includeSamplerSamples = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_SAMPLER_SAMPLES);
                if (includeSamplerSamples!=null && includeSamplerSamples.length()>0 && Boolean.valueOf(includeSamplerSamples)){
                    if (!(includeSamples!=null && includeSamples.length()>0 && Boolean.valueOf(includeSamples)))
                        filterFieldName=LPArray.addValueToArray1D(filterFieldName, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLE_CONFIG_CODE.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.NOT_IN.getSqlClause());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,samplerSmpTemplate);
                }            
                String includeMicroOrganisms = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_MICROORGANISMS);
    //            if (includeMicroOrganisms!=null && includeMicroOrganisms.length()>0 && Boolean.valueOf(includeMicroOrganisms)){
    //            }
                String microOrganismsToFind = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISMS_TO_FIND);
                if (microOrganismsToFind!=null && microOrganismsToFind.length()>0){
                    includeMicroOrganisms=Boolean.TRUE.toString();
                    if (!(includeSamples!=null && includeSamples.length()>0 && Boolean.valueOf(includeSamples))){
                        filterFieldName=LPArray.addValueToArray1D(filterFieldName, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLE_CONFIG_CODE.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.NOT_IN.getSqlClause());
                        filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,samplerSmpTemplate);
                    }
                }

                sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(), 
                         filterFieldName, filterFieldValue,
                         sampleFieldToRetrieveArr , new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLE_ID.getName()+" desc"} ); 
                jObj=new JSONObject();
                JSONArray sampleJsonArr = new JSONArray();
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())){
                    jObj= noRecordsInTableMessage();                    
                }else{                       
                    for (Object[] curRec: sampleInfo){
                        jObj= LPJson.convertArrayRowToJSONObject(sampleFieldToRetrieveArr, curRec);
                        if (Boolean.valueOf(includeMicroOrganisms)){
                            Integer curSampleId = Integer.valueOf(curRec[LPArray.valuePosicInArray(sampleFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLE_ID.getName())].toString());
                            Object[][] sampleMicroOrgInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.SampleMicroorganism.TBL.getName(), 
                                new String[]{TblsEnvMonitData.SampleMicroorganism.FLD_SAMPLE_ID.getName()}, new Object[]{curSampleId},
                                new String[]{TblsEnvMonitData.SampleMicroorganism.FLD_MICROORG_NAME.getName()} , new String[]{TblsEnvMonitData.SampleMicroorganism.FLD_SAMPLE_ID.getName()+" desc"} ); 
                            String microOrgList="";
                            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleMicroOrgInfo[0][0].toString())){
                                for (Object[] curMicroOrg: sampleMicroOrgInfo){
                                    if (microOrgList.length()>0)microOrgList=microOrgList.concat(", ");
                                    microOrgList=microOrgList.concat(curMicroOrg[0].toString());
                                }
                                jObj.put(TblsEnvMonitData.SampleMicroorganism.TBL.getName(), microOrgList);
                            }
                            if (microOrganismsToFind!=null && microOrganismsToFind.length()>0 && microOrgList!=null && microOrgList.length()>0){                            
                                Integer findNumber=0;
                                for (String curMicroOrgToFind: microOrganismsToFind.split("\\|")){
                                    if (LPArray.valueInArray(LPArray.getColumnFromArray2D(sampleMicroOrgInfo, 0), curMicroOrgToFind)) findNumber=findNumber+1;
                                }
                                if (findNumber==microOrganismsToFind.split("\\|").length){
                            //    if (microOrgList.toUpperCase().contains(microOrganismsToFind.toUpperCase())){
                                    sampleJsonArr.add(jObj);
                                }
                            }
                        }else
                            sampleJsonArr.add(jObj);
                    }
                }    

                jObjMainObject.put("datatable", sampleJsonArr);
            }
            
            if (getInvestigationInfo){
                Object[][] investigationInfo=new Object[0][0];
                String[] investigationFieldToRetrieveArr = TblsProcedure.Investigation.getAllFieldNames();
                
                        
                String creationDayStart = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_CREATION_DAY_START);
                String creationDayEnd = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_CREATION_DAY_END);
                Object[] buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsProcedure.Investigation.FLD_CREATED_ON.getName(), creationDayStart, creationDayEnd);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString()))
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, buildDateRangeFromStrings);
                if (buildDateRangeFromStrings.length>=2){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, buildDateRangeFromStrings[1].toString());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,buildDateRangeFromStrings[2]);
                }
                if (buildDateRangeFromStrings.length==4)
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,buildDateRangeFromStrings[3]);

                String closureDayStart = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_CLOSURE_DAY_START);
                String closureDayEnd = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_CLOSURE_DAY_END);
                buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsProcedure.Investigation.FLD_CLOSED_ON.getName(), closureDayStart, closureDayEnd);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString()))
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, buildDateRangeFromStrings);
                if (buildDateRangeFromStrings.length>=2){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, buildDateRangeFromStrings[1].toString());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,buildDateRangeFromStrings[2]);
                }
                if (buildDateRangeFromStrings.length==4)
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,buildDateRangeFromStrings[3]);
                
                String excludeNotClosedYet = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_NOT_CLOSED_YET);
                if (excludeNotClosedYet!=null && excludeNotClosedYet.length()>0 && Boolean.valueOf(excludeNotClosedYet)){
                    filterFieldName=LPArray.addValueToArray1D(filterFieldName, TblsProcedure.Investigation.FLD_CLOSED_ON.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause());
                    filterFieldValue=LPArray.addValueToArray1D(filterFieldValue,samplerSmpTemplate);
                }            
                        
                investigationInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.Investigation.TBL.getName(), 
                         filterFieldName, filterFieldValue,
                         investigationFieldToRetrieveArr , new String[]{TblsProcedure.Investigation.FLD_ID.getName()+" desc"} ); 
                jObj=new JSONObject();
                JSONArray investigationJsonArr = new JSONArray();
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationInfo[0][0].toString())){
                    jObj= noRecordsInTableMessage();                    
                }else{                       
                    for (Object[] curRec: investigationInfo){
                        jObj= LPJson.convertArrayRowToJSONObject(investigationFieldToRetrieveArr, curRec);
                            investigationJsonArr.add(jObj);
                    }
                }    
                jObjMainObject.put("datatable", investigationJsonArr);
            }
            String sampleGroups=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_GROUPS);
            if (sampleGroups!=null){
                String[] sampleGroupsArr=sampleGroups.split("\\|");
                for (String currGroup: sampleGroupsArr){
                    JSONArray sampleGrouperJsonArr = new JSONArray();
                    String[] groupInfo = currGroup.split("\\*");
                    String[] smpGroupFldsArr=groupInfo[0].split(",");
                    Object[][] groupedInfo = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(), 
                            smpGroupFldsArr, filterFieldName, filterFieldValue, 
                            null);
                    smpGroupFldsArr=LPArray.addValueToArray1D(smpGroupFldsArr, "count");
                    jObj=new JSONObject();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(groupedInfo[0][0].toString())){
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
            String investigationGroups=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_INVESTIGATION_GROUPS);
            if (investigationGroups!=null){
                String[] investigationGroupsArr=investigationGroups.split("\\|");
                for (String currGroup: investigationGroupsArr){
                    JSONArray investigationGrouperJsonArr = new JSONArray();
                    String[] groupInfo = currGroup.split("\\*");
                    String[] invGroupFldsArr=groupInfo[0].split(",");
                    Object[][] groupedInfo = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.Investigation.TBL.getName(), 
                            invGroupFldsArr, filterFieldName, filterFieldValue, 
                            null);
                    invGroupFldsArr=LPArray.addValueToArray1D(invGroupFldsArr, "count");
                    jObj=new JSONObject();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(groupedInfo[0][0].toString())){
                        jObj= noRecordsInTableMessage();                    
                    }else{                       
                        for (Object[] curRec: groupedInfo){
                            jObj= LPJson.convertArrayRowToJSONObject(invGroupFldsArr, curRec);
                            investigationGrouperJsonArr.add(jObj);
                        }
                    } 
                    jObjMainObject.put(groupInfo[1], investigationGrouperJsonArr);
                }
            }              
            String outputIsFile = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_OUTPUT_IS_FILE);
            if (!(outputIsFile!=null && outputIsFile.length()>0 && Boolean.valueOf(outputIsFile))){
                LPFrontEnd.servletReturnSuccess(request, response, jObjMainObject);
            }else{
                LPFrontEnd.servletReturnSuccessFile(request, response, jObjMainObject, this, 
                        request.getParameter(LPPlatform.REQUEST_PARAM_FILE_PATH), 
                        request.getParameter(LPPlatform.REQUEST_PARAM_FILE_NAME));
            }            
        }catch(NumberFormatException e){   
            procReqInstance.killIt();
            String[] errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            try {           
                response.sendError((int) errMsg[0], (String) errMsg[1]);
            } catch (IOException ex) {
                Logger.getLogger(EnvMonAPIStats.class.getName()).log(Level.SEVERE, null, ex);
            }
        } finally {
            try {
                procReqInstance.killIt();
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        processRequest(request, response);
    }


    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        processRequest(request, response);
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
