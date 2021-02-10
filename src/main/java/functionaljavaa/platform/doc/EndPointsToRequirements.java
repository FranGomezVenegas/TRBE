/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platform.doc;

import com.labplanet.servicios.app.AppHeaderAPI.AppHeaderAPIfrontendEndpoints;
import com.labplanet.servicios.app.AuthenticationAPIParams.AuthenticationAPIEndpoints;
import com.labplanet.servicios.app.IncidentAPI.IncidentAPIEndpoints;
import com.labplanet.servicios.app.IncidentAPI.IncidentAPIfrontendEndpoints;
import com.labplanet.servicios.app.InvestigationAPI.InvestigationAPIEndpoints;
import com.labplanet.servicios.app.InvestigationAPI.InvestigationAPIfrontendEndpoints;
import com.labplanet.servicios.app.ModulesConfigMasterDataAPI.ConfigMasterDataAPIEndpoints;
import com.labplanet.servicios.app.SopUserAPI.SopUserAPIEndpoints;
import com.labplanet.servicios.app.SopUserAPIfrontend.SopUserAPIfrontendEndpoints;
import com.labplanet.servicios.moduleenvmonit.EnvMonAPI.EnvMonAPIEndpoints;
import com.labplanet.servicios.moduleenvmonit.EnvMonAPI.EnvMonQueriesAPIEndpoints;
import com.labplanet.servicios.moduleenvmonit.EnvMonAPIStats.EnvMonAPIstatsEndpoints;
import com.labplanet.servicios.moduleenvmonit.EnvMonIncubationAPI.EnvMonIncubationAPIEndpoints;
import com.labplanet.servicios.moduleenvmonit.EnvMonProdLotAPI.EnvMonProdLotAPIEndpoints;
import com.labplanet.servicios.moduleenvmonit.EnvMonSampleAPI.EnvMonSampleAPIEndpoints;
import com.labplanet.servicios.moduleenvmonit.ClassEnvMonSampleFrontend.EnvMonSampleAPIFrontendEndpoints;
import com.labplanet.servicios.moduleenvmonit.EnvMonAPIfrontend.EnvMonAPIfrontendEndpoints;
import com.labplanet.servicios.moduleenvmonit.EnvMonIncubBatchAPIfrontend.EnvMonIncubBatchAPIfrontendEndpoints;
import com.labplanet.servicios.moduleenvmonit.EnvMonIncubationAPIfrontend.EnvMonIncubationAPIfrontendEndpoints;
import com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.InspLotRMAPIEndpoints;
import com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.InspLotRMQueriesAPIEndpoints;
import com.labplanet.servicios.modulesample.SampleAPIParams.SampleAPIEndpoints;
import com.labplanet.servicios.modulesample.SampleAPIParams.SampleAPIfrontendEndpoints;
import com.labplanet.servicios.proceduredefinition.ProcedureDefinitionAPI.ProcedureDefinitionAPIEndpoints;
import com.labplanet.servicios.proceduredefinition.ProcedureDefinitionfrontend.ProcedureDefinitionAPIfrontendEndpoints;
import com.labplanet.servicios.testing.config.db.DbTestingLimitAndResult.TestingLimitAndResult;
import databases.Rdbms;
import databases.TblsReqs.EndpointsDeclaration;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public final class EndPointsToRequirements {
    private EndPointsToRequirements() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    // Endpoints 'antiguos': AppHeaderAPIEndpoints, IncidentAPIfrontendEndpoints, BatchAPIEndpoints, GenomaVariableAPIEndPoints y todos los de Genoma!

    public static void endpointDefinition(){
        Rdbms.stablishDBConectionTester();    
        // *** Falta encontrar la manera de tomar la url de un servlet!
        //"api_url",

        TestingLimitAndResult[] valuesDBSpecLimitAndResult = TestingLimitAndResult.values();
        for (TestingLimitAndResult curApi: valuesDBSpecLimitAndResult){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }

        AuthenticationAPIEndpoints[] valuesAuth = AuthenticationAPIEndpoints.values();
        for (AuthenticationAPIEndpoints curApi: valuesAuth){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        
        AppHeaderAPIfrontendEndpoints[] valuesHeaderFrontend = AppHeaderAPIfrontendEndpoints.values();
        for (AppHeaderAPIfrontendEndpoints curApi: valuesHeaderFrontend){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        ProcedureDefinitionAPIEndpoints[] valuesProcedureDefinition = ProcedureDefinitionAPIEndpoints.values();
        for (ProcedureDefinitionAPIEndpoints curApi: valuesProcedureDefinition){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }          
        ProcedureDefinitionAPIfrontendEndpoints[] valuesProcedureDefinitionfrontend = ProcedureDefinitionAPIfrontendEndpoints.values();
        for (ProcedureDefinitionAPIfrontendEndpoints curApi: valuesProcedureDefinitionfrontend){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }  
        IncidentAPIEndpoints[] valuesInc = IncidentAPIEndpoints.values();
        for (IncidentAPIEndpoints curApi: valuesInc){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        IncidentAPIfrontendEndpoints[] valuesIncFrontEnd = IncidentAPIfrontendEndpoints.values();
        for (IncidentAPIfrontendEndpoints curApi: valuesIncFrontEnd){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        InvestigationAPIEndpoints[] valuesInvest = InvestigationAPIEndpoints.values();
        for (InvestigationAPIEndpoints curApi: valuesInvest){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        InvestigationAPIfrontendEndpoints[] valuesInvestFrontEnd = InvestigationAPIfrontendEndpoints.values();
        for (InvestigationAPIfrontendEndpoints curApi: valuesInvestFrontEnd){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }

        SopUserAPIEndpoints[] valuesSop = SopUserAPIEndpoints.values();
        for (SopUserAPIEndpoints curApi: valuesSop){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        SopUserAPIfrontendEndpoints[] valuesSopFrontend = SopUserAPIfrontendEndpoints.values();
        for (SopUserAPIfrontendEndpoints curApi: valuesSopFrontend){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        
        ConfigMasterDataAPIEndpoints[] valuesConfigMasterData = ConfigMasterDataAPIEndpoints.values();
        for (ConfigMasterDataAPIEndpoints curApi: valuesConfigMasterData){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        SampleAPIEndpoints[] valuesSample = SampleAPIEndpoints.values();
        for (SampleAPIEndpoints curApi: valuesSample){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        SampleAPIfrontendEndpoints[] valuesSampleFE = SampleAPIfrontendEndpoints.values();
        for (SampleAPIfrontendEndpoints curApi: valuesSampleFE){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        EnvMonAPIEndpoints[] valuesEnvMon = EnvMonAPIEndpoints.values();
        for (EnvMonAPIEndpoints curApi: valuesEnvMon){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }        
        EnvMonAPIfrontendEndpoints[] valuesEnvMonFrontend = EnvMonAPIfrontendEndpoints.values();
        for (EnvMonAPIfrontendEndpoints curApi: valuesEnvMonFrontend){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        EnvMonQueriesAPIEndpoints[] valuesEnvMonQueries = EnvMonQueriesAPIEndpoints.values();
        for (EnvMonQueriesAPIEndpoints curApi: valuesEnvMonQueries){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        EnvMonAPIstatsEndpoints[] valuesEnvMonStats = EnvMonAPIstatsEndpoints.values();
        for (EnvMonAPIstatsEndpoints curApi: valuesEnvMonStats){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        EnvMonIncubBatchAPIfrontendEndpoints[] valuesEnvMonIncubFrontend = EnvMonIncubBatchAPIfrontendEndpoints.values();
        for (EnvMonIncubBatchAPIfrontendEndpoints curApi: valuesEnvMonIncubFrontend){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }        
        EnvMonIncubationAPIEndpoints[] valuesEnvMonIncub = EnvMonIncubationAPIEndpoints.values();
        for (EnvMonIncubationAPIEndpoints curApi: valuesEnvMonIncub){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }            
        EnvMonIncubationAPIfrontendEndpoints[] valuesEnvMonIncubFE = EnvMonIncubationAPIfrontendEndpoints.values();
        for (EnvMonIncubationAPIfrontendEndpoints curApi: valuesEnvMonIncubFE){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }    
        EnvMonProdLotAPIEndpoints[] valuesEnvMonProdLot = EnvMonProdLotAPIEndpoints.values();
        for (EnvMonProdLotAPIEndpoints curApi: valuesEnvMonProdLot){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        EnvMonSampleAPIEndpoints[] valuesEnvMonSample = EnvMonSampleAPIEndpoints.values();
        for (EnvMonSampleAPIEndpoints curApi: valuesEnvMonSample){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        EnvMonSampleAPIFrontendEndpoints[] valuesEnvMonSampleFE = EnvMonSampleAPIFrontendEndpoints.values();
        for (EnvMonSampleAPIFrontendEndpoints curApi: valuesEnvMonSampleFE){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        InspLotRMAPIEndpoints[] valuesInspLotRM = InspLotRMAPIEndpoints.values();
        for (InspLotRMAPIEndpoints curApi: valuesInspLotRM){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
        InspLotRMQueriesAPIEndpoints[] valuesInspLotRMQueries = InspLotRMQueriesAPIEndpoints.values();
        for (InspLotRMQueriesAPIEndpoints curApi: valuesInspLotRMQueries){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curApi.getClass().getSimpleName(), curApi.getName()});
            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curApi.getArguments())});                
            declareInDatabase(curApi.getClass().getSimpleName(), curApi.getName(), fieldNames, fieldValues);
        }
}
    
private static JSONArray getEndPointArguments(LPAPIArguments[] arguments){
    String[] argHeader=new String[]{"name", "type", "is_mandatory?","testing arg posic"};
    JSONArray argsJsonArr = new JSONArray();
    JSONObject argsJson=new JSONObject();
    for (LPAPIArguments curArg: arguments){
        argsJson = LPJson.convertArrayRowToJSONObject(argHeader, new Object[]{curArg.getName(), curArg.getType(), curArg.getMandatory(), curArg.getTestingArgPosic()});
        argsJsonArr.add(argsJson);
    }
    return argsJsonArr;
}

private static void declareInDatabase(String apiName, String endpointName, String[] fieldNames, Object[] fieldValues){
//    Rdbms.getRecordFieldsByFilter(apiName, apiName, fieldNames, fieldValues, fieldNames)
    Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), EndpointsDeclaration.TBL.getName(), 
            new String[]{EndpointsDeclaration.FLD_API_NAME.getName(),  EndpointsDeclaration.FLD_ENDPOINT_NAME.getName()},
            new Object[]{apiName, endpointName}, new String[]{EndpointsDeclaration.FLD_ID.getName(), EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName()});
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
        String newArgumentsArray=fieldValues[LPArray.valuePosicInArray(fieldNames, EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName())].toString();
        if (!newArgumentsArray.equalsIgnoreCase(reqEndpointInfo[0][1].toString())){
            Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), EndpointsDeclaration.TBL.getName(),
                    new String[]{EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName(), EndpointsDeclaration.FLD_LAST_UPDATE.getName()},
                    new Object[]{newArgumentsArray, LPDate.getCurrentTimeStamp()},
                    new String[]{EndpointsDeclaration.FLD_ID.getName()}, new Object[]{reqEndpointInfo[0][0]});
            return;
        }
        return;
    }else{
        fieldNames=LPArray.addValueToArray1D(fieldNames, EndpointsDeclaration.FLD_CREATION_DATE.getName());
        fieldValues=LPArray.addValueToArray1D(fieldValues, LPDate.getCurrentTimeStamp());                
        Rdbms.insertRecordInTable(GlobalVariables.Schemas.REQUIREMENTS.getName(), EndpointsDeclaration.TBL.getName(), fieldNames, fieldValues);    
    }
}

}
