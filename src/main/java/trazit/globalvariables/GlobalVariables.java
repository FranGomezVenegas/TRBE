/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.globalvariables;

/**
 *
 * @author User
 */
public class GlobalVariables {
    
    public enum Schemas{APP("app"), APP_AUDIT("app-audit"),APP_BUSINESS_RULES("app-business-rules"),
        APP_CONFIG("config"), APP_TESTING("app-testing"), 
        APP_PROC_DATA("app-proc-data"), APP_PROC_DATA_AUDIT("app-proc-data-audit"),
        CONFIG("config"), CONFIG_AUDIT("config-audit"), REQUIREMENTS("requirements"),
        DATA("data"), DATA_AUDIT("data-audit"), TESTING("testing"), DATA_TESTING("data_testing"), DATA_AUDIT_TESTING("data-audit_testing"),
        PROCEDURE("procedure"), PROCEDURE_CONFIG("procedure-config"), PROCEDURE_TESTING("procedure_testing"), PROCEDURE_AUDIT("procedure-audit"), PROCEDURE_AUDIT_TESTING("procedure-audit_testing"), MODULES_TRAZIT_TRAZIT("trazit")
        ;
        Schemas(String nm){
            this.name=nm;
        }
        public String getName() {
            return name;
        }
        private final String name;        
    }
    public static final String DEFAULTLANGUAGE="en";
    public static final String TRAZIT_SCHEDULER="TRAZIT_SCHEDULER";
    public static final String PROC_MANAGEMENT_SPECIAL_ROLE="proc_management";
    
    public static final String LANGUAGE_ALL_LANGUAGES="ALL";
    public enum Languages{EN("en"), ES("es")
        ;
        Languages(String nm){
            this.name=nm;
        }
        public String getName() {
            return name;
        }
        private final String name;        
    }    
    
    public enum TrazitModules{INVENTORY_TRACKING, INSTRUMENTS, ENVIRONMENTAL_MONITORING, GENOMICS,
        PLATFORM_ADMIN, SAMPLES_MANAGEMENT, INSPECTION_LOTS_RAW_MAT}
    
    public enum ServletsResponse{SUCCESS("/ResponseSuccess", "response"), ERROR("/ResponseError", "errorDetail");
        ServletsResponse(String svlt, String attr){
            this.attributeName=attr;
            this.servletName=svlt;
        }
        public String getAttributeName() {return attributeName;}
        public String getServletName() {return servletName;}     
        private final String servletName;        
        private final String attributeName;        
    }
    
public enum ApiUrls{

        APP_AUTHENTICATION_ACTIONS("/app/AuthenticationAPIactions"),
        APP_USER_SESSIONS_QUERIES("/app/UserSessionAPIqueries"),
        APP_INCIDENTS_ACTIONS("/app/IncidentAPIactions"),
        APP_INCIDENTS_QUERIES("/app/IncidentAPIqueries"),
        APP_CALENDAR_ACTIONS("/app/HolidayCalendarAPIactions"),
        APP_CALENDAR_QUERIES("/app/HolidayCalendarAPIqueries"),
        PLATFORM_ADMIN_ACTIONS("/app/PlatformAdminAPIactions"),
        PLATFORM_ADMIN_QUERIES("/app/PlatformAdminAPIqueries"),
        // /AppTrazitInitSession
        // /app/VideoTutorialAPIqueries
        PLATFORM_DEFINITION_ACTIONS("/PlatformDefinitionToInstance"),
        MODULE_DEFINITION_ACTIONS("/app/ModuleDefinitionAPI"),
        PROCEDURE_DEFINITION_ACTIONS("/appProcMgr/RequirementsProcedureDefinitionAPIActions"),
        PROCEDURE_DEFINITION_QUERIES("/appProcMgr/RequirementsProcedureDefinitionAPIQueries"),
        
        CONFIG_MASTERDATA_ACTIONS("/modules/ConfigMasterDataAPI"),
        
        TESTING_ACTIONS(""),        
        TESTING_LIMIT_AND_RESULT("/testing/config/db/DbTestingLimitAndResult"),
        DOC_ENDPOINTS_QUERIES("/Doc/EndpointsDocAPIqueries"),
        // /testing/platform/TestingCoverageRun
        // /testing/platform/TestingRegressionUAT
        
        SAVEDQUERIES_ACTIONS("/app/SavedQueriesAPIactions"),
        SAVEDQUERIES_QUERIES("/app/SavedQueriesAPIqueries"),

        CERTIFY_QUERIES("/app/CertifyAPIqueries"),
        CERTIFY_ANALYSISMETHODS_ACTIONS("/app/CertifyAnalysisMethodAPIactions"),
        SOPS_QUERIES("/app/SopUserAPIqueries"),
        SOPS_ACTIONS("/app/SopUserAPIactions"),
        // /app/AnalysisMethodCertifUserAPIqueries 
        
        BATCH_ARRAY_ACTIONS("/modulebatch/BatchAPI"),
        
        INVESTIGATIONS_ACTIONS("/app/InvestigationAPIactions"),
        INVESTIGATIONS_QUERIES("/app/InvestigationAPIqueries"),
        
        SAMPLES_ACTIONS("/modulesample/SampleAPIactions"),
        SAMPLES_QUERIES("/modulesample/SampleAPIqueries"),
        
        ENVMON_ACTIONS("/moduleenvmon/EnvMonAPIactions"),        
        ENVMON_QUERIES("EnvMonAPIqueries"), //2???
        ENVMON_STATS_QUERIES("/moduleenvmon/EnvMonAPIstats"),
        ENVMON_SAMPLE_ACTIONS("/moduleenvmon/EnvMonSampleAPIactions"),
        ENVMON_SAMPLE_QUERIES("/moduleenvmon/EnvMonSampleAPIqueries"),
        ENVMON_INCUB_BATCH_QUERIES("/moduleenvmon/EnvMonIncubBatchAPIqueries"),
        
        ENVMON_INCUBATOR_ACTIONS("/moduleenvmon/EnvMonIncubatorAPIactions"),
        ENVMON_INCUBATOR_QUERIES("/moduleenvmon/EnvMonIncubatorAPIqueries"),
        
        ENVMON_PRODLOT_ACTIONS(""),
        
        INSTRUMENTS_ACTIONS("/app/procs/InstrumentsAPIactions"),
        INSTRUMENTS_QUERIES("/app/procs/InstrumentsAPIqueries"),
        // /testing/app/TestingPlatformInstruments
        
        INVENTORY_TRACKING_ACTIONS("/app/procs/InvTrackingAPIactions"),
        INVENTORY_TRACKING_QUERIES("/app/procs/InvTrackingAPIqueries"),
        // /testing/app/TestingPlatformInstruments
        
        
        GENOMA_PROJECT_ACTIONS("/modulegenoma/GenomaProjectAPIactions"),
        GENOMA_PROJECT_QUERIES(""),
        GENOMA_STUDY_ACTIONS("/modulegenoma/GenomaStudyAPIactions"),
        GENOMA_STUDY_QUERIES("/modulegenoma/GenomaStudyAPIqueries"),
        GENOMA_VARIABLE_ACTIONS("/modulegenoma/GenomaConfigVariableAPIactions"),
        GENOMA_VARIABLE_QUERIES("/modulegenoma/GenomaConfigVariableAPIqueries"),
        // /modulegenome/TablesDeployment
        // /modulegenoma/GenomaStudyObjectsVariablesAPI
        // /testing/moduleGenoma/TestingGenoma
        
        INSPLOT_RM_ACTIONS("/moduleinsplotrm/InspLotRMAPIactions"),
        INSPLOT_RM_QUERIES("/moduleinsplotrm/InspLotRMAPIqueries"),
        // /testing/moduleInspLotRM/TestingInspLotRM
        
        ;
        private ApiUrls(String url){
            this.url=url;
        }
        public String getUrl() {return url;}     
        private final String url;    
    }    
}
