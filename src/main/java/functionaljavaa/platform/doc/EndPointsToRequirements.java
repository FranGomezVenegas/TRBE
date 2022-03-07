/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platform.doc;
import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsTrazitDocTrazit.EndpointsDeclaration;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.parameter.Parameter.PropertyFilesType;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lbplanet.utilities.LPFrontEnd;
import java.util.List;
import java.util.ResourceBundle;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.Languages;
/**
 *
 * @author User
 */
public final class EndPointsToRequirements {
    String[] fldNames;
    Object[][] endpointsFromDatabase;
    String[] endpointsApiAndEndpointNamesKey;
    Object[] apiName1d;
    Object[] endpointName1d;
    
    
public EndPointsToRequirements(HttpServletRequest request, HttpServletResponse response){
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
        Rdbms.getRdbms().startRdbms(dbTrazitModules);
        getEndPointsFromDatabase();
        if (this.fldNames==null) return;
        JSONArray enumsCompleteSuccess = new JSONArray();
        Integer classesImplementingInt=-999;
        Integer totalEndpointsVisitedInt=0;
            try (       io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo()//.acceptPackages("com.xyz")
            .scan()) {    
                ClassInfoList classesImplementing = scanResult.getClassesImplementing("trazit.enums.EnumIntEndpoints");
                ClassInfoList allEnums = scanResult.getAllEnums();
                classesImplementingInt=classesImplementing.size();
                for (int i=0;i<classesImplementing.size();i++){
                    ClassInfo getMine = classesImplementing.get(i); 

                    String st="";
                    if ("SampleAuditErrorTrapping".equalsIgnoreCase(getMine.getName().toString())){
                        st="e";
                    }
                    List<Object> enumConstantObjects = getMine.getEnumConstantObjects();
                    JSONArray enumsIncomplete = new JSONArray();
                    totalEndpointsVisitedInt=totalEndpointsVisitedInt+enumConstantObjects.size();
                    for (int j=0;j<enumConstantObjects.size();j++) {
                        EnumIntEndpoints curEndpoint = (EnumIntEndpoints) enumConstantObjects.get(j);
                        
                        
                        String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
                        Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curEndpoint.getClass().getSimpleName(), curEndpoint.getName(), curEndpoint.getSuccessMessageCode()});
                        fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
                        fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curEndpoint.getArguments())});                
                        
                        
                        //String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
                        //Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curEndpoint.getClass().getSimpleName(), curEndpoint.getErrorCode()});
                        try{
                            declareInDatabase(curEndpoint.getClass().getSimpleName(), curEndpoint.getName().toString(), 
                                    fieldNames, fieldValues, curEndpoint.getOutputObjectTypes(), enumConstantObjects.size());
                        //declareMessageInDatabase(curBusRul.getClass().getSimpleName(), 
                          //  curBusRul.getErrorCode(), fieldNames, fieldValues);

                        }catch(Exception e){
                            JSONObject jObj=new JSONObject();
                            jObj.put("enum",getMine.getName().toString());
                            jObj.put("endpoint_code",curEndpoint.toString());
                            jObj.put("error",e.getMessage());
                            enumsIncomplete.add(jObj);
                        }
                    }
                    if (enumsIncomplete.size()>0){
                        LPFrontEnd.servletReturnSuccess(request, response, enumsIncomplete);
                        return;
                    }else{
                        JSONObject jObj=new JSONObject();
                        jObj.put("enum",getMine.getName().toString());
                        jObj.put("messages",enumConstantObjects.size());
                        enumsCompleteSuccess.add(jObj);
                    }
                }
            }catch(Exception e){
                ScanResult.closeAll();
                JSONArray errorJArr = new JSONArray();
                errorJArr.add(e.getMessage());
                LPFrontEnd.servletReturnSuccess(request, response, errorJArr);
                return;
            }
        // Rdbms.closeRdbms();
        ScanResult.closeAll();        
        JSONObject jMainObj=new JSONObject();
        jMainObj.put("00_total_in_db_before_running", this.endpointsFromDatabase.length);
        jMainObj.put("01_total_apis_in_db_before_running", this.apiName1d.length);
        jMainObj.put("02_total_enums",classesImplementingInt.toString());
        jMainObj.put("03_total_visited_enums",enumsCompleteSuccess.size());
        jMainObj.put("04_enums_visited_list", enumsCompleteSuccess);
        jMainObj.put("05_total_number_of_messages_visited", totalEndpointsVisitedInt);
        
        
        LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
        return;
    }    
    
    public EndPointsToRequirements() {
        }
    
    public static JsonArray endpointWithNoOutputObjects=Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", "no output for testing")
                    .add("table", "no output for testing").build()).build();
    // Endpoints 'antiguos': AppHeaderAPIEndpoints, IncidentAPIfrontendEndpoints, BatchAPIEndpoints, GenomaVariableAPIEndPoints y todos los de Genoma!
/*
    public Object[] endpointDefinition(){
        getEndPointsFromDatabase();
        Object[] logMsg=new Object[]{};
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        logMsg=LPArray.addValueToArray1D(logMsg, "Begin");
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
        Boolean startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
        // *** Falta encontrar la manera de tomar la url de un servlet!
        //"api_url",

        startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
                

        TestingLimitAndResult[] valuesDBSpecLimitAndResult = TestingLimitAndResult.values();
        for (TestingLimitAndResult curApi: valuesDBSpecLimitAndResult){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), TestingLimitAndResult.values().length);
        }

        AuthenticationAPIEndpoints[] valuesAuth = AuthenticationAPIEndpoints.values();
        for (AuthenticationAPIEndpoints curApi: valuesAuth){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), AuthenticationAPIEndpoints.values().length);
        }
        
        AppHeaderAPIfrontendEndpoints[] valuesHeaderFrontend = AppHeaderAPIfrontendEndpoints.values();
        for (AppHeaderAPIfrontendEndpoints curApi: valuesHeaderFrontend){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), AppHeaderAPIfrontendEndpoints.values().length);
        }
        ProcedureDefinitionAPIEndpoints[] valuesProcedureDefinition = ProcedureDefinitionAPIEndpoints.values();
        for (ProcedureDefinitionAPIEndpoints curApi: valuesProcedureDefinition){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), ProcedureDefinitionAPIEndpoints.values().length);
        }          
        ProcedureDefinitionAPIfrontendEndpoints[] valuesProcedureDefinitionfrontend = ProcedureDefinitionAPIfrontendEndpoints.values();
        for (ProcedureDefinitionAPIfrontendEndpoints curApi: valuesProcedureDefinitionfrontend){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), ProcedureDefinitionAPIfrontendEndpoints.values().length);
        }  
        IncidentAPIEndpoints[] valuesInc = IncidentAPIEndpoints.values();
        for (IncidentAPIEndpoints curApi: valuesInc){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), IncidentAPIEndpoints.values().length);
        }
        IncidentAPIfrontendEndpoints[] valuesIncFrontEnd = IncidentAPIfrontendEndpoints.values();
        for (IncidentAPIfrontendEndpoints curApi: valuesIncFrontEnd){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), IncidentAPIfrontendEndpoints.values().length);
        }
        InvestigationAPIEndpoints[] valuesInvest = InvestigationAPIEndpoints.values();
        for (InvestigationAPIEndpoints curApi: valuesInvest){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), InvestigationAPIEndpoints.values().length);
        }
        InvestigationAPIfrontendEndpoints[] valuesInvestFrontEnd = InvestigationAPIfrontendEndpoints.values();
        for (InvestigationAPIfrontendEndpoints curApi: valuesInvestFrontEnd){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), InvestigationAPIfrontendEndpoints.values().length);
        }

        SopUserAPIEndpoints[] valuesSop = SopUserAPIEndpoints.values();
        for (SopUserAPIEndpoints curApi: valuesSop){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), SopUserAPIEndpoints.values().length);
        }
        CertifyAnalysisMethodAPIEndpoints[] certifAnaMeth = CertifyAnalysisMethodAPIEndpoints.values();
        for (CertifyAnalysisMethodAPIEndpoints curApi: certifAnaMeth){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), CertifyAnalysisMethodAPIEndpoints.values().length);
        }
        CertifyAPIfrontendEndpoints[] certifEndpoints = CertifyAPIfrontendEndpoints.values();
        for (CertifyAPIfrontendEndpoints curApi: certifEndpoints){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), CertifyAPIfrontendEndpoints.values().length);
        }
        SopUserAPIfrontendEndpoints[] valuesSopFrontend = SopUserAPIfrontendEndpoints.values();
        for (SopUserAPIfrontendEndpoints curApi: valuesSopFrontend){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), SopUserAPIfrontendEndpoints.values().length);
        }
        ConfigMasterDataAPIEndpoints[] valuesConfigMasterData = ConfigMasterDataAPIEndpoints.values();
        for (ConfigMasterDataAPIEndpoints curApi: valuesConfigMasterData){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), ConfigMasterDataAPIEndpoints.values().length);
        }
        EnvMonAPIEndpoints[] valuesEnvMon = EnvMonAPIEndpoints.values();
        for (EnvMonAPIEndpoints curApi: valuesEnvMon){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), EnvMonAPIEndpoints.values().length);
        }        
        EnvMonAPIfrontendEndpoints[] valuesEnvMonFrontend = EnvMonAPIfrontendEndpoints.values();
        for (EnvMonAPIfrontendEndpoints curApi: valuesEnvMonFrontend){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), EnvMonAPIfrontendEndpoints.values().length);
        }
        EnvMonQueriesAPIEndpoints[] valuesEnvMonQueries = EnvMonQueriesAPIEndpoints.values();
        for (EnvMonQueriesAPIEndpoints curApi: valuesEnvMonQueries){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), EnvMonQueriesAPIEndpoints.values().length);
        }
        EnvMonAPIstatsEndpoints[] valuesEnvMonStats = EnvMonAPIstatsEndpoints.values();
        for (EnvMonAPIstatsEndpoints curApi: valuesEnvMonStats){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), EnvMonAPIstatsEndpoints.values().length);
        }
        EnvMonIncubBatchAPIfrontendEndpoints[] valuesEnvMonIncubFrontend = EnvMonIncubBatchAPIfrontendEndpoints.values();
        for (EnvMonIncubBatchAPIfrontendEndpoints curApi: valuesEnvMonIncubFrontend){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), EnvMonIncubBatchAPIfrontendEndpoints.values().length);
        }        
        EnvMonIncubationAPIEndpoints[] valuesEnvMonIncub = EnvMonIncubationAPIEndpoints.values();
        for (EnvMonIncubationAPIEndpoints curApi: valuesEnvMonIncub){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), EnvMonIncubationAPIEndpoints.values().length);
        }            
        EnvMonIncubationAPIfrontendEndpoints[] valuesEnvMonIncubFE = EnvMonIncubationAPIfrontendEndpoints.values();
        for (EnvMonIncubationAPIfrontendEndpoints curApi: valuesEnvMonIncubFE){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), EnvMonIncubationAPIfrontendEndpoints.values().length);
        }    
        EnvMonProdLotAPIEndpoints[] valuesEnvMonProdLot = EnvMonProdLotAPIEndpoints.values();
        for (EnvMonProdLotAPIEndpoints curApi: valuesEnvMonProdLot){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), EnvMonProdLotAPIEndpoints.values().length);
        }
        EnvMonSampleAPIEndpoints[] valuesEnvMonSample = EnvMonSampleAPIEndpoints.values();
        for (EnvMonSampleAPIEndpoints curApi: valuesEnvMonSample){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), EnvMonSampleAPIEndpoints.values().length);
        }
        EnvMonSampleAPIFrontendEndpoints[] valuesEnvMonSampleFE = EnvMonSampleAPIFrontendEndpoints.values();
        for (EnvMonSampleAPIFrontendEndpoints curApi: valuesEnvMonSampleFE){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), EnvMonSampleAPIFrontendEndpoints.values().length);
        }
        InspLotRMAPIEndpoints[] valuesInspLotRM = InspLotRMAPIEndpoints.values();
        for (InspLotRMAPIEndpoints curApi: valuesInspLotRM){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), InspLotRMAPIEndpoints.values().length);
        }
        InspLotRMQueriesAPIEndpoints[] valuesInspLotRMQueries = InspLotRMQueriesAPIEndpoints.values();
        for (InspLotRMQueriesAPIEndpoints curApi: valuesInspLotRMQueries){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), InspLotRMQueriesAPIEndpoints.values().length);
        }        
        EndpointsDocAPIqueriesEndpoints[] endpointsDocAPIqueriesEndpoints = EndpointsDocAPIqueriesEndpoints.values();
        for (EndpointsDocAPIqueriesEndpoints curApi: endpointsDocAPIqueriesEndpoints){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), EndpointsDocAPIqueriesEndpoints.values().length);
        }
        SampleAPIfrontendEndpoints[] valuesSampleFE = SampleAPIfrontendEndpoints.values();
        for (SampleAPIfrontendEndpoints curApi: valuesSampleFE){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), SampleAPIfrontendEndpoints.values().length);
        }
        SampleAPIEndpoints[] valuesSample = SampleAPIEndpoints.values();
        for (SampleAPIEndpoints curApi: valuesSample){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), SampleAPIEndpoints.values().length);
        }
        UserSessionAPIfrontendEndpoints[] userSessionAPIfrontendEndpoints = UserSessionAPIfrontendEndpoints.values();
        for (UserSessionAPIfrontendEndpoints curApi: userSessionAPIfrontendEndpoints){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), SampleAPIEndpoints.values().length);
        }        
        CalendarAPIactionsEndpoints[] calendarAPIactionsEndpoints = CalendarAPIactionsEndpoints.values();
        for (CalendarAPIactionsEndpoints curApi: calendarAPIactionsEndpoints){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), SampleAPIEndpoints.values().length);
        }
        CalendarAPIqueriesEndpoints[] calendarAPIqueriesEndpoints = CalendarAPIqueriesEndpoints.values();
        for (CalendarAPIqueriesEndpoints curApi: calendarAPIqueriesEndpoints){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName(),  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName(), curApi.getSuccessMessageCode()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues, curApi.getOutputObjectTypes(), SampleAPIEndpoints.values().length);
        }
        logMsg=LPArray.addValueToArray1D(logMsg, "End");        
        Rdbms.closeRdbms();
        return logMsg;
}
  */  
private static JSONArray getEndPointArguments(LPAPIArguments[] arguments){
    String[] argHeader=new String[]{"name", "type", "is_mandatory?","testing arg posic"};
    JSONArray argsJsonArr = new JSONArray();
    for (LPAPIArguments curArg: arguments){
        JSONObject argsJson = LPJson.convertArrayRowToJSONObject(argHeader, new Object[]{curArg.getName(), curArg.getType(), curArg.getMandatory(), curArg.getTestingArgPosic()});
        argsJsonArr.add(argsJson);
    }
    return argsJsonArr;
}
//private static void declareInDatabase(String apiName, String endpointName, String[] fieldNames, Object[] fieldValues){
//     declareInDatabase(apiName, endpointName, fieldNames, fieldValues, null);
//}

private void getEndPointsFromDatabase(){
    Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), EndpointsDeclaration.TBL.getName(), 
            new String[]{EndpointsDeclaration.API_NAME.getName()+SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL.getSqlClause()},
            new Object[]{"zzz"}, EndpointsDeclaration.getAllFieldNames());
    this.fldNames=EndpointsDeclaration.getAllFieldNames();
    this.endpointsFromDatabase=reqEndpointInfo;
    Integer apiNamePosic=LPArray.valuePosicInArray(this.fldNames, EndpointsDeclaration.API_NAME.getName());
    Integer endpointNamePosic=LPArray.valuePosicInArray(this.fldNames, EndpointsDeclaration.ENDPOINT_NAME.getName());
    Object[] apiName1d = LPArray.array2dTo1d(this.endpointsFromDatabase, apiNamePosic);
    Object[] endpointName1d = LPArray.array2dTo1d(this.endpointsFromDatabase, endpointNamePosic);
    
    this.endpointsApiAndEndpointNamesKey=LPArray.joinTwo1DArraysInOneOf1DString(apiName1d, endpointName1d, "-");
}

private Object[] existsEndPointInDatabase(String apiName, String endpointName){
    Integer valuePosicInArray = LPArray.valuePosicInArray(this.endpointsApiAndEndpointNamesKey, apiName+"-"+endpointName);
    if (valuePosicInArray==-1)return new Object[]{LPPlatform.LAB_FALSE};
    return this.endpointsFromDatabase[valuePosicInArray];    
}
public void declareInDatabase(String apiName, String endpointName, String[] fieldNames, Object[] fieldValues, JsonArray outputObjectTypes, Integer numEndpointsInApi){
//if (1==1)return;
//    Rdbms.getRecordFieldsByFilter(apiName, apiName, fieldNames, fieldValues, fieldNames)
    try{
    Object[] reqEndpointInfo=existsEndPointInDatabase(apiName, endpointName);
//    Object[] docInfoForEndPoint = getDocInfoForEndPoint(apiName, endpointName);
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0].toString())){
        String newArgumentsArray=fieldValues[LPArray.valuePosicInArray(fieldNames, EndpointsDeclaration.ARGUMENTS_ARRAY.getName())].toString();
        if (!newArgumentsArray.equalsIgnoreCase(reqEndpointInfo[1].toString())){
            Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), EndpointsDeclaration.TBL.getName(),
                    new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName(), EndpointsDeclaration.LAST_UPDATE.getName()},
                    new Object[]{newArgumentsArray, LPDate.getCurrentTimeStamp()},
                    new String[]{EndpointsDeclaration.ID.getName()}, new Object[]{reqEndpointInfo[0]});
            return;
        }else{
            //String[] flds=(String[]) docInfoForEndPoint[0];
            String[] fldNames=new String[]{};
            Object[] fldValues=new Object[]{};
/*            if (flds.length>0){
                fldNames=(String[]) docInfoForEndPoint[0];
                fldValues=(Object[]) docInfoForEndPoint[1];
            }*/
            fldNames=LPArray.addValueToArray1D(fldNames, EndpointsDeclaration.OUTPUT_OBJECT_TYPES.getName());
            if (outputObjectTypes==null) fldValues=LPArray.addValueToArray1D(fldValues, "TBD");
            else
                fldValues=LPArray.addValueToArray1D(fldValues, outputObjectTypes.toString());                
            fldNames=LPArray.addValueToArray1D(fldNames, EndpointsDeclaration.NUM_ENDPOINTS_IN_API.getName());
                fldValues=LPArray.addValueToArray1D(fldValues, numEndpointsInApi);                
            Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), EndpointsDeclaration.TBL.getName(),
                    fldNames, fldValues,
                    new String[]{EndpointsDeclaration.ID.getName()}, new Object[]{reqEndpointInfo[0]});            
            return;
        }
    }else{
        fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.CREATION_DATE.getName(), EndpointsDeclaration.NUM_ENDPOINTS_IN_API.getName()});
        fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{LPDate.getCurrentTimeStamp(), numEndpointsInApi});
        //fieldNames=LPArray.addValueToArray1D(fieldNames, (String[]) docInfoForEndPoint[0]);
        //fieldValues=LPArray.addValueToArray1D(fieldValues, (Object[]) docInfoForEndPoint[1]);
        fieldNames=LPArray.addValueToArray1D(fieldNames, EndpointsDeclaration.OUTPUT_OBJECT_TYPES.getName());
        if (outputObjectTypes==null) fieldValues=LPArray.addValueToArray1D(fieldValues, "TBD");
        else
            fieldValues=LPArray.addValueToArray1D(fieldValues, outputObjectTypes.toString());
        Object[] insertRecordInTable = Rdbms.insertRecordInTable(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), EndpointsDeclaration.TBL.getName(), fieldNames, fieldValues);    
        this.endpointsFromDatabase=LPArray.joinTwo2DArrays(endpointsFromDatabase, LPArray.array1dTo2d(fieldValues,1));
        return;
    }
    }catch(Exception e){
      return;      
    }
}

public static Object[] getDocInfoForEndPoint(String apiName, String endpointName){
    Parameter parm=new Parameter();
if ("RESULT_CHANGE_UOM".equalsIgnoreCase(endpointName))    
    System.out.print(endpointName);
    try{
        String[] fldNames=new String[]{EndpointsDeclaration.BRIEF_SUMMARY_EN.getName(), EndpointsDeclaration.DOCUMENT_NAME_EN.getName(),
            EndpointsDeclaration.DOC_CHAPTER_ID_EN.getName(), EndpointsDeclaration.DOC_CHAPTER_NAME_EN.getName()};
        Object[] data=new Object[2];
        String[] fldsToRetrieve=new String[]{};
        String[] fldsValuesToRetrieve=new String[]{};
        for (String curFld: fldNames){
            for (Languages curLang: GlobalVariables.Languages.values()){            
                String propName=endpointName+"_"+curFld.replace("_en", ""); //"GET_METHOD_CERTIFIED_USERS_LIST_brief_summary"
                 String propValue = Parameter.getMessageCodeValue(PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName, null, propName, curLang.getName(), false);
                if (propValue.length()>0){
                    fldsToRetrieve=LPArray.addValueToArray1D(fldsToRetrieve, curFld.replace("_en", "_"+curLang.getName()));
                    fldsValuesToRetrieve=LPArray.addValueToArray1D(fldsValuesToRetrieve, propValue);
                }else{
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Parameter.parameterBundleExists(PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName, null, propName, curLang.getName(), false))){                
                        parm.createPropertiesFile(PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName+"_"+curLang.getName());  
                        parm.addTagInPropertiesFile(PropertyFilesType.ENDPOINTDOCUMENTATION.toString(),  apiName+"_"+curLang.getName(), propName, propValue);
                    }
                }
            }
        }    
        if (fldsToRetrieve.length==0) data[0]=LPPlatform.LAB_FALSE;
        data[0]=fldsToRetrieve;
        data[1]=fldsValuesToRetrieve;
        return data;
    }finally{
        parm=null;
    }
}

}
