/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.globalvariables;

import databases.TblsCnfg.TablesConfig;
import databases.TblsCnfgAudit;
import databases.TblsData;
import databases.TblsDataAudit;
import databases.TblsProcedure;
import databases.TblsProcedureAudit.TablesProcedureAudit;
import module.clinicalstudies.apis.GenomaProjectAPI.GenomaProjectAPIactionsEndPoints;
import module.clinicalstudies.apis.GenomaStudyAPI.GenomaStudyAPIactionsEndPoints;
import module.clinicalstudies.definition.TblsGenomaConfig;
import module.clinicalstudies.definition.TblsGenomaData;
import module.clinicalstudies.definition.TblsGenomaDataAudit;
import module.clinicalstudies.logic.ClassProject;
import module.clinicalstudies.logic.ClassStudy;
import module.clinicalstudies.logic.ClinicalStudiesFrontendMasterData;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMConfig;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMDataAudit;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMProcedure;
import module.inspectionlot.rawmaterial.logic.InspLotRawMaterialMasterData;
import module.instrumentsmanagement.definition.TblsInstrumentsConfig.TablesInstrumentsConfig;
import module.instrumentsmanagement.definition.TblsInstrumentsConfigAudit;
import module.instrumentsmanagement.definition.TblsInstrumentsData;
import module.instrumentsmanagement.definition.TblsInstrumentsDataAudit;
import module.instrumentsmanagement.definition.TblsInstrumentsProcedure;
import module.instrumentsmanagement.logic.InstrumentsFrontendMasterData;
import module.inventorytrack.definition.TblsInvTrackingConfig;
import module.inventorytrack.definition.TblsInvTrackingData;
import module.inventorytrack.definition.TblsInvTrackingDataAudit;
import module.inventorytrack.definition.TblsInvTrackingProcedure;
import module.inventorytrack.logic.InvTrackingFrontendMasterData;
import module.monitoring.definition.TblsEnvMonitConfig;
import module.monitoring.definition.TblsEnvMonitConfigAudit;
import module.monitoring.definition.TblsEnvMonitData;
import module.monitoring.definition.TblsEnvMonitDataAudit;
import module.monitoring.definition.TblsEnvMonitProcedure;
import trazit.enums.ActionsEndpointPair;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntViews;
import trazit.enums.FrontendMasterData;

/**
 *
 * @author User
 */
public class GlobalVariables {
    
    public enum Schemas{APP("app"), APP_AUDIT("app-audit"),
        APP_CONFIG("config"), APP_TESTING("app-testing"),
        APP_PROCEDURE("app-procedure"),
        APP_PROC_DATA("app-proc-data"), APP_PROC_DATA_AUDIT("app-proc-data-audit"),
        CONFIG("config"), CONFIG_AUDIT("config-audit"), REQUIREMENTS("requirements"),
        DATA("data"), DATA_AUDIT("data-audit"), TESTING("testing"), 
        DATA_TESTING(VALIDATION_MODE_REPO+"data"), DATA_AUDIT_TESTING(VALIDATION_MODE_REPO+"data-audit"),
        PROCEDURE("procedure"), PROCEDURE_CONFIG("procedure-config"), 
        PROCEDURE_TESTING(VALIDATION_MODE_REPO+"procedure"), PROCEDURE_AUDIT("procedure-audit"), 
        PROCEDURE_AUDIT_TESTING(VALIDATION_MODE_REPO+"procedure-audit"), MODULES_TRAZIT_TRAZIT("trazit"), MODULES_TRAZIT_MODULES("modules")
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
    public static final String VALIDATION_MODE_REPO="valid-";
    
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
    
    public enum TrazitModules{
        STOCKS(null,TblsInvTrackingConfig.TablesInvTrackingConfig.values(),
            TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.values(), TblsInvTrackingData.TablesInvTrackingData.values(),
            null, TblsInvTrackingProcedure.TablesInvTrackingProcedure.values(),
            null,TblsInvTrackingConfig.ViewsInvTrackingConfig.values(),
            null, TblsInvTrackingData.ViewsInvTrackingData.values(),
            null, null, InvTrackingFrontendMasterData.class,
              null
        ), 
        INSTRUMENTS(TblsInstrumentsConfigAudit.TablesInstrumentsConfigAudit.values(), TablesInstrumentsConfig.values(),
            TblsInstrumentsDataAudit.TablesInstrumentsDataAudit.values(), TblsInstrumentsData.TablesInstrumentsData.values(),
              null, TblsInstrumentsProcedure.TablesInstrumentsProcedure.values(),
            null, null,
            null, TblsInstrumentsData.ViewsInstrumentsData.values(),
              null, null, InstrumentsFrontendMasterData.class,
              null
        ), 
        MONITORING(TblsEnvMonitConfigAudit.TablesEnvMonitConfigAudit.values(), TblsEnvMonitConfig.TablesEnvMonitConfig.values(),
                TblsEnvMonitDataAudit.TablesEnvMonitDataAudit.values(), TblsEnvMonitData.TablesEnvMonitData.values(),
                null, TblsEnvMonitProcedure.TablesEnvMonitProcedure.values(),
                null, TblsEnvMonitConfig.ViewsEnvMonConfig.values(),
                null, TblsEnvMonitData.ViewsEnvMonData.values(),
                null, null, null,
                null
        ), 
        CLINICAL_STUDIES(null, TblsGenomaConfig.TablesGenomaConfig.values(), 
                TblsGenomaDataAudit.TablesGenomaDataAudit.values(), TblsGenomaData.TablesGenomaData.values(),
                null, null,
                null, null,
                null, null, null, null, ClinicalStudiesFrontendMasterData.class,
                new ActionsEndpointPair[]{
                    new ActionsEndpointPair(GenomaProjectAPIactionsEndPoints.class.getName(), ClassProject.class.getName()),
                    new ActionsEndpointPair(GenomaStudyAPIactionsEndPoints.class.getName(), ClassStudy.class.getName())}        
        ), 
        PLATFORM_ADMIN(null, null, null, null, null, null, null, null, null, null, null, null, null, null), 
        SAMPLES_MANAGEMENT(TblsCnfgAudit.TablesCfgAudit.values(), TablesConfig.values(),
            TblsDataAudit.TablesDataAudit.values(), TblsData.TablesData.values(), 
            TablesProcedureAudit.values(), TblsProcedure.TablesProcedure.values(),
            null, null,
            null, TblsData.ViewsData.values(), 
            null, TblsProcedure.ViewsProcedure.values(), null,
            null
        ), 
        INSPECTION_LOTS(null, TblsInspLotRMConfig.TablesInspLotRMConfig.values(),
                TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.values(), TblsInspLotRMData.TablesInspLotRMData.values(),
                null, TblsInspLotRMProcedure.TablesInspLotRMProcedure.values(),
                null, null,
                null, TblsInspLotRMData.ViewsInspLotRMData.values(),
                null, null, InspLotRawMaterialMasterData.class,
              null                
        )
        ;
        TrazitModules(EnumIntTables[] configAudit, EnumIntTables[] config, EnumIntTables[] dataAudit, EnumIntTables[] data, EnumIntTables[] procAudit, EnumIntTables[] proc,
            EnumIntViews[] configAuditVw, EnumIntViews[] configVw, EnumIntViews[] dataAuditVw, EnumIntViews[] dataVw, EnumIntViews[] procAuditVw, EnumIntViews[] procVw,
            Class<? extends FrontendMasterData> masterDataClass, ActionsEndpointPair[] actionsEndpointPair){
                this.configAuditTbls=configAudit;
                this.configTbls=config;
                this.dataAuditTbls=dataAudit;
                this.dataTbls=data;
                this.procedureAuditTbls=procAudit;
                this.procedureTbls=proc;
                this.configAuditVws=configAuditVw;
                this.configVws=configVw;
                this.dataAuditVws=dataAuditVw;
                this.dataVws=dataVw;
                this.procedureAuditVws=procAuditVw;
                this.procedureVws=procVw;
                this.masterDataClass=masterDataClass;
                this.actionsEndpointsPair=actionsEndpointPair;
        }
        private final EnumIntTables[] configAuditTbls;
        public EnumIntTables[] getConfigAuditTbls(){return configAuditTbls;}
        private final EnumIntTables[] configTbls;
        public EnumIntTables[] getConfigTbls(){return configTbls;}
        private final EnumIntTables[] dataAuditTbls;
        public EnumIntTables[] getDataAuditTbls(){return dataAuditTbls;}
        private final EnumIntTables[] dataTbls;
        public EnumIntTables[] getDataTbls(){return dataTbls;}
        private final EnumIntTables[] procedureAuditTbls;
        public EnumIntTables[] getProcedureAuditTbls(){return procedureAuditTbls;}
        private final EnumIntTables[] procedureTbls;
        public EnumIntTables[] getProcedureTbls(){return procedureTbls;}

        private final EnumIntViews[] configAuditVws;
        public EnumIntViews[] getConfigAuditVws(){return configAuditVws;}
        private final EnumIntViews[] configVws;
        public EnumIntViews[] getConfigVws(){return configVws;}
        private final EnumIntViews[] dataAuditVws;
        public EnumIntViews[] getDataAuditVws(){return dataAuditVws;}
        private final EnumIntViews[] dataVws;
        public EnumIntViews[] getDataVws(){return dataVws;}
        private final EnumIntViews[] procedureAuditVws;
        public EnumIntViews[] getProcedureAuditVws(){return procedureAuditVws;}
        private final EnumIntViews[] procedureVws;
        public EnumIntViews[] getProcedureVws(){return procedureVws;}
        private final Class<? extends FrontendMasterData> masterDataClass;
        public Class<? extends FrontendMasterData> getModuleMasterDataClass(){return masterDataClass;}
        private final ActionsEndpointPair[] actionsEndpointsPair;
        public ActionsEndpointPair[] getActionsEndpointPair(){return actionsEndpointsPair;}
    }
    
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
