package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.SqlWhereEntry;
import databases.TblsData;
import databases.TblsProcedure;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
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
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import static lbplanet.utilities.LPKPIs.getKPIs;
import static lbplanet.utilities.LPKPIs.getRecoveryRate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntViewFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import trazit.queries.QueryUtilitiesEnums;
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
    public enum EnvMonAPIqueriesStatsEndpoints implements EnumIntEndpoints{
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
                }, EndPointsToRequirements.endpointWithNoOutputObjects, "QUERY_SAMPLING_HISTORY"),
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
                }, EndPointsToRequirements.endpointWithNoOutputObjects, "QUERY_SAMPLER_SAMPLING_HISTORY"),        
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
                }, EndPointsToRequirements.endpointWithNoOutputObjects, "QUERY_READING_OUT_OF_RANGE"),
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
                }, EndPointsToRequirements.endpointWithNoOutputObjects, "KPI_PRODUCTION_LOT_SAMPLES"),        
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
                }, EndPointsToRequirements.endpointWithNoOutputObjects, "QUERY_INVESTIGATION"),
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
                }, EndPointsToRequirements.endpointWithNoOutputObjects, "KPIS"),        
        RECOVERY_RATE("RECOVERY_RATE", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELDS_TO_RETRIEVE_OR_GROUPING, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments("showAbsence", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 9),
                new LPAPIArguments("showPresence", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 10),
                new LPAPIArguments("showIN", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 11),
                new LPAPIArguments("showOUT", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 12),
                new LPAPIArguments("percNumDecimals", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 13),                
                }, EndPointsToRequirements.endpointWithNoOutputObjects, "RECOVERY_RATE"),                            
        ;
        private EnvMonAPIqueriesStatsEndpoints(String name, LPAPIArguments[] argums, JsonArray outputObjectTypes, String successMessageCode){
            this.name=name;
            this.arguments=argums;  
            this.outputObjectTypes=outputObjectTypes;  
            this.successMessageCode=successMessageCode;
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
        @Override        public LPAPIArguments[] getArguments() {return arguments;}
        @Override        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        @Override public String getSuccessMessageCode() {return this.successMessageCode;}
        @Override        public String getApiUrl(){return ApiUrls.ENVMON_STATS_QUERIES.getUrl();}
        private final String name;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes; 
        private final String successMessageCode;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response){
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        String procInstanceName = procReqInstance.getProcedureInstance();

            
        EnvMonAPIqueriesStatsEndpoints endPoint = null;
        try{
            endPoint = EnvMonAPIqueriesStatsEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
            return;                   
        }
        Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }        
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
            EnumIntMessages errCode=(EnumIntMessages) argValues[1];
            LPFrontEnd.servletReturnResponseError(request, response,
                    errCode.getErrorCode(), new Object[]{argValues[2].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;            
        }          
        JSONObject jObjMainObject=new JSONObject();
        try { //try (PrintWriter out = response.getWriter()) {
            SqlWhere wObj=new SqlWhere();
            String prodLotName="";
            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))){return;}
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
                    wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.SPEC_EVAL, SqlStatement.WHERECLAUSE_TYPES.NOT_IN, new Object[]{"IN"}, null);
                    break;
                case QUERY_SAMPLER_SAMPLING_HISTORY: 
                    getSampleInfo=true;
                    getInvestigationInfo=false;
                    wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_CONFIG_CODE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{samplerSmpTemplate}, null);
                    break;
                case KPI_PRODUCTION_LOT_SAMPLES: 
                    getSampleInfo=true;
                    getInvestigationInfo=false;
                    prodLotName=argValues[0].toString();
                    JSONArray jArr=new JSONArray();
                    wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.PRODUCTION_LOT, 
                        prodLotName.contains("*")  ? SqlStatement.WHERECLAUSE_TYPES.LIKE: SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{prodLotName}, null);                                    

                    String prodLotFieldToRetrieve = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_PROD_LOT_FIELD_TO_RETRIEVE);
                    String[] prodLotFieldToRetrieveArr=new String[0];
                    if ((prodLotFieldToRetrieve!=null) && (prodLotFieldToRetrieve.length()>0))
                        if ("ALL".equalsIgnoreCase(prodLotFieldToRetrieve)) prodLotFieldToRetrieveArr=EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableFields());
                        else prodLotFieldToRetrieveArr=prodLotFieldToRetrieve.split("\\|");
                    if (prodLotFieldToRetrieve==null)
                        prodLotFieldToRetrieveArr=EnumIntTableFields.getAllFieldNames(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableFields());
                    JSONObject jObj=new JSONObject();
                    if (!prodLotName.contains("rutina")){
                        Object[][] prodLotInfo = QueryUtilitiesEnums.getTableData(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT,
                            EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT, "ALL"),
                            new String[]{TblsEnvMonitData.ProductionLot.LOT_NAME.getName()}, new Object[]{prodLotName},
                            new String[]{TblsEnvMonitData.ProductionLot.CREATED_ON.getName()+" desc"} ); 
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(prodLotInfo[0][0].toString())){
                             jObj= LPFrontEnd.responseJSONDiagnosticLPFalse(Rdbms.RdbmsErrorTrapping.TABLE_WITH_NO_RECORDS, new Object[0]);                    
                             jArr.add(jObj);
                        }else{
                           for (Object[] curRec: prodLotInfo){
                                jObj= LPJson.convertArrayRowToJSONObject(prodLotFieldToRetrieveArr, curRec);
                                JSONObject jObjPieceOfInfo=new JSONObject();
                                for (int i=0;i<prodLotFieldToRetrieveArr.length;i++){
                                       jObjPieceOfInfo=new JSONObject();
                                       jObjPieceOfInfo.put("field_name", prodLotFieldToRetrieveArr[i]);
                                       jObjPieceOfInfo.put("field_value", curRec[i].toString());
                                       jArr.add(jObjPieceOfInfo);
                                }
                           }
                        }
                        jObjMainObject.put(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableName(), jArr);
                    }
                    JSONObject jObjRecoveryData = getRecoveryRate(new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.PRODUCTION_LOT.getName()}, 
                        TblsData.ViewSampleAnalysisResultWithSpecLimits.PRODUCTION_LOT.getName(),
                        prodLotName,
                        true, true, true, true, 2, false);
                    jObjMainObject.put("recovery_rate", jObjRecoveryData);
                    break; 
 
                case QUERY_INVESTIGATION:
                    getSampleInfo=false;
                    getInvestigationInfo=true;                    
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
                        fldToRetrieve, dataGrouped, false);
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
                    getRecoveryRate=true;
                    getSampleInfo=true;
            }
            JSONObject jObj=new JSONObject();
            if (Boolean.TRUE.equals(getRecoveryRate)){
                    JSONObject jObjRecoveryData = getRecoveryRate(RRobjGroupName, RRwhereFieldsName, RRwhereFieldsValue, 
                        showAbsence, showPresence, showIN, showOUT, percNumDecimals, false);
                    jObjMainObject.put("recovery_rate", jObjRecoveryData);
            }
            Object[][] sampleInfo=new Object[0][0];
            if (Boolean.TRUE.equals(getSampleInfo)){
                String programName = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME);
                if (programName!=null && programName.length()>0)
                    wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.PROGRAM_NAME, 
                        programName.contains("*")  ? SqlStatement.WHERECLAUSE_TYPES.LIKE: SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{programName}, null);                                    
                String areaName = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_AREA);
                if (areaName!=null && areaName.length()>0)
                    wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.AREA, 
                        areaName.contains("*")  ? SqlStatement.WHERECLAUSE_TYPES.LIKE: SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{areaName}, null);                                    
                String locName = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_LOCATION_NAME);
                if (locName!=null && locName.length()>0)
                    wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.LOCATION_NAME, 
                        locName.contains("*")  ? SqlStatement.WHERECLAUSE_TYPES.LIKE: SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{locName}, null);                    
                String sampleFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE);                    
                String[] sampleFieldToRetrieveArr=new String[0];
                if ((sampleFieldToRetrieve!=null) && (sampleFieldToRetrieve.length()>0))
                    if ("ALL".equalsIgnoreCase(sampleFieldToRetrieve)) sampleFieldToRetrieveArr=EnumIntViewFields.getAllFieldNames(TblsData.ViewSampleAnalysisResultWithSpecLimits.values());
                    else sampleFieldToRetrieveArr=sampleFieldToRetrieve.split("\\|");
                if (sampleFieldToRetrieve==null)
                    sampleFieldToRetrieveArr=EnumIntViewFields.getAllFieldNames(TblsData.ViewSampleAnalysisResultWithSpecLimits.values());

                String samplingDayStart = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLING_DAY_START);
                String samplingDayEnd = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLING_DAY_END);
                Object[] buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLING_DATE.getName(), samplingDayStart, samplingDayEnd);
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString())){
                    if (buildDateRangeFromStrings.length==4)
                        wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLING_DATE, SqlStatement.WHERECLAUSE_TYPES.BETWEEN, new Object[]{buildDateRangeFromStrings[2], buildDateRangeFromStrings[3]}, null);
                    else
                        wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLING_DATE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{buildDateRangeFromStrings[2]}, null);
                }
                String loginDayStart = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_LOGIN_DAY_START);
                String loginDayEnd = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_LOGIN_DAY_END);
                buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsData.ViewSampleAnalysisResultWithSpecLimits.LOGGED_ON.getName(), loginDayStart, loginDayEnd);
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString())){
                    if (buildDateRangeFromStrings.length==4)
                        wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.LOGGED_ON, SqlStatement.WHERECLAUSE_TYPES.BETWEEN, new Object[]{buildDateRangeFromStrings[2], buildDateRangeFromStrings[3]}, null);                    
                    else
                        wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.LOGGED_ON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{buildDateRangeFromStrings[2]}, null);                    
                }
                String includeSamples = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_SAMPLER_SAMPLES);
                if (includeSamples!=null && includeSamples.length()>0 && Boolean.TRUE.equals(Boolean.valueOf(includeSamples)))
                    wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_CONFIG_CODE, SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{smpTemplate}, null);
                String excludeSamplerSamples = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_SAMPLER_SAMPLES);
                if (excludeSamplerSamples!=null && excludeSamplerSamples.length()>0 && Boolean.TRUE.equals(Boolean.valueOf(excludeSamplerSamples)))
                    wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_CONFIG_CODE, SqlStatement.WHERECLAUSE_TYPES.NOT_IN, new Object[]{samplerSmpTemplate}, null);                    
                String samplerName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLER);
                if (samplerName!=null && samplerName.length()>0)
                    wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLER, 
                        samplerName.contains("*")  ? SqlStatement.WHERECLAUSE_TYPES.LIKE: SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{samplerName}, null);                    
                String samplerArea = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLER_AREA);
                if (samplerArea!=null && samplerArea.length()>0)
                    wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLER_AREA, 
                        samplerArea.contains("*")  ? SqlStatement.WHERECLAUSE_TYPES.LIKE: SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{samplerArea}, null);
                String readingEqual = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_READING_EQUAL);
                if (readingEqual!=null && readingEqual.length()>0)
                    wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{readingEqual}, null);
                String readingMin = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_READING_MIN);
                if (readingMin!=null && readingMin.length()>0)
                    wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE_NUM, SqlStatement.WHERECLAUSE_TYPES.GREATER_THAN, new Object[]{Integer.valueOf(readingMin)}, null);
                String readingMax = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_READING_MAX);
                if (readingMax!=null && readingMax.length()>0)
                    wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE_NUM, SqlStatement.WHERECLAUSE_TYPES.LESS_THAN, new Object[]{Integer.valueOf(readingMax)}, null);
                String excludeReadingNotEntered = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_READING_NOT_ENTERED);
                if (excludeReadingNotEntered!=null && excludeReadingNotEntered.length()>0 && Boolean.TRUE.equals(Boolean.valueOf(excludeReadingNotEntered)))
                    wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{""}, null);
                String includeSamplerSamples = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_SAMPLER_SAMPLES);
                if (includeSamplerSamples!=null && includeSamplerSamples.length()>0 && Boolean.TRUE.equals(Boolean.valueOf(includeSamplerSamples))){
                    if (!(includeSamples!=null && includeSamples.length()>0 && Boolean.valueOf(includeSamples)))
                        wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_CONFIG_CODE, SqlStatement.WHERECLAUSE_TYPES.NOT_IN, new Object[]{Integer.valueOf(samplerSmpTemplate)}, null);
                }            
                String includeMicroOrganisms = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_MICROORGANISMS);
                String microOrganismsToFind = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISMS_TO_FIND);
                if (microOrganismsToFind!=null && microOrganismsToFind.length()>0){
                    includeMicroOrganisms=Boolean.TRUE.toString();
                    if (!(includeSamples!=null && includeSamples.length()>0 && Boolean.valueOf(includeSamples)))
                        wObj.addConstraint(TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_CONFIG_CODE, SqlStatement.WHERECLAUSE_TYPES.NOT_IN, new Object[]{Integer.valueOf(samplerSmpTemplate)}, null);
                }
                jObj=new JSONObject();
                JSONArray sampleJsonArr = new JSONArray();
                if (!wObj.getAllWhereEntries().isEmpty()){
                    EnumIntViewFields[] fieldsToGet = EnumIntViewFields.getViewFieldsFromString(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, "ALL");
                    sampleInfo = QueryUtilitiesEnums.getViewData(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW,
                        fieldsToGet,
                        wObj, //new SqlWhere(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, filterFieldName, filterFieldValue),
                        new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_ID.getName()+" desc"}, false ); 
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())){
                        jObj= LPFrontEnd.responseJSONDiagnosticLPFalse(Rdbms.RdbmsErrorTrapping.TABLE_WITH_NO_RECORDS, new Object[0]);                    
                    }else{                       
                        for (Object[] curRec: sampleInfo){
                            jObj= LPJson.convertArrayRowToJSONObject(EnumIntViewFields.getAllFieldNames(fieldsToGet), curRec);
                            if (Boolean.TRUE.equals(Boolean.valueOf(includeMicroOrganisms))){
                                Integer curSampleId = Integer.valueOf(curRec[LPArray.valuePosicInArray(sampleFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_ID.getName())].toString());
                                Object[][] sampleMicroOrgInfo = QueryUtilitiesEnums.getTableData(TblsEnvMonitData.TablesEnvMonitData.SAMPLE_MICROORGANISM, 
                                    EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.SAMPLE_MICROORGANISM, new String[]{TblsEnvMonitData.SampleMicroorganism.MICROORG_NAME.getName()}), 
                                    new String[]{TblsEnvMonitData.SampleMicroorganism.SAMPLE_ID.getName()}, new Object[]{curSampleId},
                                    new String[]{TblsEnvMonitData.SampleMicroorganism.SAMPLE_ID.getName()+" desc"} ); 
                                String microOrgList="";
                                if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleMicroOrgInfo[0][0].toString()))){
                                    for (Object[] curMicroOrg: sampleMicroOrgInfo){
                                        if (microOrgList.length()>0)microOrgList=microOrgList.concat(", ");
                                        microOrgList=microOrgList.concat(curMicroOrg[0].toString());
                                    }
                                    jObj.put(TblsEnvMonitData.TablesEnvMonitData.SAMPLE_MICROORGANISM.getTableName(), microOrgList);
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
                }
                
                jObjMainObject.put("datatable", sampleJsonArr);
                JSONArray filterJArr = new JSONArray();
                for (SqlWhereEntry curFilterFld: wObj.getAllWhereEntries()){
                    JSONObject fltJObj=new JSONObject(); // +" "+LPNulls.replaceNull(curFilterFld.getSeparator())
                    fltJObj.put(curFilterFld.getVwFldName().getName()+" "+curFilterFld.getSymbol().getSqlClause(), 
                            LPArray.convertArrayToString(curFilterFld.getFldValue(), ", ", "", true));
                    fltJObj.put("filter_name", curFilterFld.getVwFldName().getName()+" "+curFilterFld.getSymbol().getSqlClause());
                    fltJObj.put("value", LPArray.convertArrayToString(curFilterFld.getFldValue(), ", ", "", true));
                            //LPNulls.replaceNull(Arrays.toString(curFilterFld.getFldValue())));
                    filterJArr.add(fltJObj);
                }
                jObjMainObject.put("filter_detail", filterJArr);
            }
            
            if (Boolean.TRUE.equals(getInvestigationInfo)){
                Object[][] investigationInfo=new Object[0][0];
                
                String creationDayStart = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_CREATION_DAY_START);
                String creationDayEnd = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_CREATION_DAY_END);
                Object[] buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsProcedure.Investigation.CREATED_ON.getName(), creationDayStart, creationDayEnd);

                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString())){
                    if (buildDateRangeFromStrings.length==4)
                        wObj.addConstraint(TblsProcedure.Investigation.CREATED_ON, SqlStatement.WHERECLAUSE_TYPES.BETWEEN, new Object[]{buildDateRangeFromStrings[2], buildDateRangeFromStrings[3]}, null);                    
                    else
                        wObj.addConstraint(TblsProcedure.Investigation.CREATED_ON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{buildDateRangeFromStrings[2]}, null);                    
                }
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString())){
                    if (buildDateRangeFromStrings.length==4)
                        wObj.addConstraint(TblsProcedure.Investigation.CLOSED_ON, SqlStatement.WHERECLAUSE_TYPES.BETWEEN, new Object[]{buildDateRangeFromStrings[2], buildDateRangeFromStrings[3]}, null);                    
                    else
                        wObj.addConstraint(TblsProcedure.Investigation.CLOSED_ON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{buildDateRangeFromStrings[2]}, null);                    
                }
                String excludeNotClosedYet = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_EXCLUDE_NOT_CLOSED_YET);
                if (excludeNotClosedYet!=null && excludeNotClosedYet.length()>0 && Boolean.TRUE.equals(Boolean.valueOf(excludeNotClosedYet)))
                    wObj.addConstraint(TblsProcedure.Investigation.CLOSED_ON, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{samplerSmpTemplate}, null);
                if (wObj.getAllWhereEntries().isEmpty())
                    wObj.addConstraint(TblsProcedure.Investigation.ID, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{samplerSmpTemplate}, null);
                EnumIntTableFields[] fieldsToGet = EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.INVESTIGATION, "ALL");
                investigationInfo = QueryUtilitiesEnums.getTableData(TblsProcedure.TablesProcedure.INVESTIGATION, 
                    fieldsToGet,
                    wObj, //filterFieldName, filterFieldValue,
                    new String[]{TblsProcedure.Investigation.ID.getName()+" desc"} ); 
                jObj=new JSONObject();
                JSONArray investigationJsonArr = new JSONArray();
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationInfo[0][0].toString())){
                    jObj= LPFrontEnd.responseJSONDiagnosticLPFalse(Rdbms.RdbmsErrorTrapping.TABLE_WITH_NO_RECORDS, new Object[0]);                    
                }else{                       
                    for (Object[] curRec: investigationInfo){
                        jObj= LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(fieldsToGet), curRec);
                            investigationJsonArr.add(jObj);
                    }
                }    
                jObjMainObject.put("datatable", investigationJsonArr);
                JSONArray filterJArr = new JSONArray();
                for (SqlWhereEntry curFilterFld: wObj.getAllWhereEntries()){
                    JSONObject fltJObj=new JSONObject(); // +" "+LPNulls.replaceNull(curFilterFld.getSeparator())
                    fltJObj.put(curFilterFld.getVwFldName().getName()+" "+curFilterFld.getSymbol().getSqlClause(), 
                            LPArray.convertArrayToString(curFilterFld.getFldValue(), ", ", "", true));
                    fltJObj.put("filter_name", curFilterFld.getVwFldName().getName()+" "+curFilterFld.getSymbol().getSqlClause());
                    fltJObj.put("value", LPArray.convertArrayToString(curFilterFld.getFldValue(), ", ", "", true));
                            //LPNulls.replaceNull(Arrays.toString(curFilterFld.getFldValue())));
                    filterJArr.add(fltJObj);
                }
                jObjMainObject.put("filter_detail", filterJArr);
                
            }
            String sampleGroups=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_GROUPS);
            if (sampleGroups!=null){
                String[] sampleGroupsArr=sampleGroups.split("\\|");
                for (String currGroup: sampleGroupsArr){
                    JSONArray sampleGrouperJsonArr = new JSONArray();
                    String[] groupInfo = currGroup.split("\\*");
                    String[] smpGroupFldsArr=groupInfo[0].split(",");
                    Object[][] groupedInfo = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW.getViewName(), 
                            smpGroupFldsArr, wObj, //filterFieldName, filterFieldValue, 
                            null, false);
                    smpGroupFldsArr=LPArray.addValueToArray1D(smpGroupFldsArr, "count");
                    jObj=new JSONObject();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(groupedInfo[0][0].toString())){
                        jObj= LPFrontEnd.responseJSONDiagnosticLPFalse(Rdbms.RdbmsErrorTrapping.TABLE_WITH_NO_RECORDS, new Object[0]);                    
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
                    Object[][] groupedInfo = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.INVESTIGATION.getTableName(), 
                            invGroupFldsArr, wObj, //filterFieldName, filterFieldValue, 
                            null, false);
                    invGroupFldsArr=LPArray.addValueToArray1D(invGroupFldsArr, "count");
                    jObj=new JSONObject();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(groupedInfo[0][0].toString())){
                        jObj= LPFrontEnd.responseJSONDiagnosticLPFalse(Rdbms.RdbmsErrorTrapping.TABLE_WITH_NO_RECORDS, new Object[0]);                    
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
            LPFrontEnd.responseError(errObject, language, null);
            Logger.getLogger(EnvMonAPIStats.class.getName()).log(Level.SEVERE, null, e);
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
