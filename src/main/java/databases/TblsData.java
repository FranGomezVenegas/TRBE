/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import databases.TblsCnfg.Methods;
import databases.TblsCnfg.SpecLimits;
import databases.TblsProcedure.InvestObjects;
import databases.TblsProcedure.ProgramCorrectiveAction;
import lbplanet.utilities.LPDatabase;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntViewFields;
import trazit.enums.EnumIntViews;
import trazit.enums.FldBusinessRules;
import trazit.enums.ForeignkeyFld;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class TblsData {
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesData implements EnumIntTables{        
        CERTIF_USER_ANALYSIS_METHOD(null, "certif_user_analysis_method", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, CertifUserAnalysisMethod.values(), CertifUserAnalysisMethod.ID.getName(),
            new String[]{CertifUserAnalysisMethod.ID.getName()}, null, "certif_user_analysis_method table, manage user analysis method certification tracking"),
        TRAINING(null, "training", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Training.values(), Training.ID.getName(),
            new String[]{Training.ID.getName()}, null, "Training table"),
        USER_SOP(null, "user_sop", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, UserSop.values(), UserSop.SOP_ID.getName(),
            new String[]{UserSop.SOP_ID.getName()}, null, "Training table"),
        USER_ANALYSIS_METHOD(null, "user_analysis_method", SCHEMA_NAME, true, UserAnalysisMethod.values(), UserAnalysisMethod.USER_ANALYSIS_METHOD_ID.getName(),
            new String[]{UserAnalysisMethod.USER_ANALYSIS_METHOD_ID.getName()}, null, "Training table"),
        SAVED_QUERIES(null, "saved_queries", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, SavedQueries.values(), SavedQueries.ID.getName(),
            new String[]{SavedQueries.ID.getName()}, null, "Training table"),
        SAMPLE(null, "sample", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Sample.values(), Sample.SAMPLE_ID.getName(),
            new String[]{Sample.SAMPLE_ID.getName()}, null, "sample table"),
        SAMPLE_ANALYSIS(null, "sample_analysis", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, SampleAnalysis.values(), Sample.SAMPLE_ID.getName(),
            new String[]{SampleAnalysis.TEST_ID.getName()}, 
            new Object[]{new ForeignkeyFld(SampleAnalysis.SAMPLE_ID.getName(), SCHEMA_NAME, SAMPLE.getTableName(), Sample.SAMPLE_ID.getName())}, "sample analysis table"),
        SAMPLE_ANALYSIS_RESULT(null, "sample_analysis_result", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, SampleAnalysisResult.values(), Sample.SAMPLE_ID.getName(),
            new String[]{SampleAnalysisResult.RESULT_ID.getName()}, 
            new Object[]{new ForeignkeyFld(SampleAnalysisResult.TEST_ID.getName(), SCHEMA_NAME, SAMPLE_ANALYSIS.getTableName(), SampleAnalysis.TEST_ID.getName())}, "sample analysis results table"),
        SAMPLE_ALIQ(null, "sample_aliq", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, SampleAliq.values(), SampleAliq.ALIQUOT_ID.getName(),
            new String[]{SampleAliq.ALIQUOT_ID.getName()}, 
            new Object[]{new ForeignkeyFld(SampleAliq.SAMPLE_ID.getName(), SCHEMA_NAME, SAMPLE.getTableName(), Sample.SAMPLE_ID.getName())}, "sample aliquot table"),
        SAMPLE_ALIQ_SUB(null, "sample_aliq_sub", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, SampleAliqSub.values(), SampleAliqSub.SUBALIQUOT_ID.getName(),
            new String[]{SampleAliqSub.SUBALIQUOT_ID.getName()}, 
            new Object[]{new ForeignkeyFld(SampleAliqSub.ALIQUOT_ID.getName(), SCHEMA_NAME, SAMPLE_ALIQ.getTableName(), SampleAliq.ALIQUOT_ID.getName())}, "sample sub aliquot table"),
        PRODUCT(null, "product", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Sample.values(), null, 
            new String[]{Sample.SAMPLE_ID.getName()}, 
            new Object[]{new ForeignkeyFld(Sample.SAMPLE_ID_RELATED.getName(), SCHEMA_NAME, "sample", Sample.SAMPLE_ID.getName())}, "product table comment"),
        SAMPLE_COC(new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleChangeOfCustody", false, false, new String[]{"ENABLED"}, null)},
            "sample_coc", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, SampleCoc.values(), SampleCoc.ID.getName(),
            new String[]{SampleCoc.ID.getName()}, null, ""),
        SAMPLE_REVISION_TESTING_GROUP(new FldBusinessRules[]{new FldBusinessRules("procedure", "revisionTestinGroupRequired", false, false, new String[]{"ENABLED"}, null)},
            "sample_revision_testing_group", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, SampleRevisionTestingGroup.values(), null,
            new String[]{SampleRevisionTestingGroup.SAMPLE_ID.getName(), SampleRevisionTestingGroup.TESTING_GROUP.getName()}, null, ""),
//        USER_METHOD(null, "user_method", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, UserMethod.values(), UserSop.SOP_ID.getName(),
//            new String[]{UserSop.SOP_ID.getName()}, null, "user_method table, log for methods and its last performed one"),
        ;
        private TablesData(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
                String seqName, String[] primaryK, Object[] foreignK, String comment){
            this.getTblBusinessRules=fldBusRules;
            this.tableName=dbTblName;
            this.tableFields=tblFlds;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.sequence=seqName;
            this.primarykey=primaryK;
            this.foreignkey=foreignK;
            this.tableComment=comment;
        }
        @Override        public String getTableName() {return this.tableName;}
        @Override        public String getTableComment() {return this.tableComment;}
        @Override        public EnumIntTableFields[] getTableFields() {return this.tableFields;}
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public String getSeqName() {return this.sequence;}
        @Override        public String[] getPrimaryKey() {return this.primarykey;}
        @Override        public Object[] getForeignKey() {return this.foreignkey;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String tableName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final String sequence;
        private final EnumIntTableFields[] tableFields;
        private final String[] primarykey;
        private final Object[] foreignkey;
        private final String tableComment;
    }
    public enum ViewsData implements EnumIntViews{
        SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW(" SELECT #FLDS from #SCHEMA.sample_analysis_result sar " +
                "   INNER JOIN #SCHEMA.sample_analysis sa on sa.test_id = sar.test_id "+
                "   INNER JOIN #SCHEMA.sample s on s.sample_id = sar.sample_id "+
                "    left outer join #SCHEMA_CONFIG.spec_limits spcLim on sar.limit_id=spcLim.limit_id " +
                "    left outer join #SCHEMA_PROCEDURE.program_corrective_action pca on pca.result_id=sar.result_id " +
                "    left outer join #SCHEMA_PROCEDURE.invest_objects io on io.object_id=sar.result_id and io.object_type='sample_analysis_result' ;" +                        
                "ALTER VIEW  #SCHEMA.#TBL  OWNER TO #OWNER;",
            null, "sample_analysis_result_with_spec_limits", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ViewSampleAnalysisResultWithSpecLimits.values(), "ViewSampleAnalysisResultWithSpecLimits"),        
        SAMPLE_COC_NAMES_VIEW(" SELECT smp_coc.sample_id, smp_coc.custodian, smp_coc.custodian_candidate, smp_coc.coc_started_on, smp_coc.coc_confirmed_on, smp_coc.coc_custodian_notes, "
                + "          smp_coc.coc_new_custodian_notes, smp_coc.sample_picture, smp_coc.id, smp_coc.status, usr_custodian.user_name AS custodian_name," +
                    "         usr_candidate.user_name AS candidate_name " +
                    "   FROM #SCHEMA.sample_coc smp_coc," +
                                "    #SCHEMA_APP.users usr_custodian," +
                                "    #SCHEMA_APP.users usr_candidate" +
                    "  WHERE smp_coc.custodian::text = usr_custodian.person_name::text AND smp_coc.custodian_candidate::text = usr_candidate.person_name::text; "+
                    "ALTER TABLE  #SCHEMA.#TBL  OWNER TO #OWNER;" +
                    "GRANT ALL ON TABLE  #SCHEMA.#TBL TO #OWNER;"
        ,
            null, "sample_coc_names", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ViewSampleCocNames.values(), "ViewSampleCocNames"),        
        USER_AND_META_DATA_SOP_VIEW(" SELECT '#SCHEMA_CONFIG'::text AS procedure, usr.user_sop_id, usr.user_id, usr.sop_id, usr.sop_list_id, usr.assigned_on, usr.assigned_by, usr.status, usr.mandatory_level," +
                "            usr.read_started, usr.read_completed, usr.understood, usr.expiration_date, usr.sop_name, usr.user_name, usr.light, metadata.brief_summary, metadata.file_link, metadata.author " +
                "   FROM #SCHEMA.user_sop usr," +
                "    #SCHEMA_CONFIG.sop_meta_data metadata" +
                "  WHERE usr.sop_name::text = metadata.sop_name::text; "+
                    "ALTER TABLE  #SCHEMA.#TBL  OWNER TO #OWNER;" +
                    "GRANT ALL ON TABLE  #SCHEMA.#TBL TO #OWNER;",
            null, "user_and_meta_data_sop_vw", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ViewUserAndMetaDataSopView.values(), "ViewUserAndMetaDataSopView"),        
        USER_AND_ANALYSISMETHOD_CERTIF_VIEW(" SELECT '#SCHEMA_CONFIG'::text AS procedure, usr.id, usr.user_id, metadata.code, usr.method_name, usr.method_version, "+
                "   usr.assigned_on, usr.assigned_by, usr.status, usr.certification_date, usr.certif_expiry_date," +
                "   usr.certif_started, usr.certif_completed, usr.sop_name, usr.user_name, usr.light, , usr.training_id,"+
                "   metadata.active, metadata.expires, metadata.expiry_interval_info, metadata.certification_mode " +
                "   FROM #SCHEMA.certif_user_analysis_method usr," +
                "    #SCHEMA_CONFIG.methods metadata" +
                "  WHERE usr.method_name::text = metadata.code::text "+
                "    and usr.method_version::integer =metadata.config_version::integer;"+
                    "ALTER TABLE  #SCHEMA.#TBL  OWNER TO #OWNER;" +
                    "GRANT ALL ON TABLE  #SCHEMA.#TBL TO #OWNER;",
            null, "user_and_analysis_method_certification_vw", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ViewUserAndAnalysisMethodCertificationView.values(), "ViewUserAndMetaDataSopView"),
        SAMPLE_TESTING_GROUP_VIEW(" SELECT #FLDS from #SCHEMA_CONFIG.sample s " +
                "   INNER JOIN #SCHEMA_CONFIG.sample_revision_testing_group stg on stg.sample_id = s.sample_id; "+
                "ALTER VIEW  #SCHEMA_CONFIG.#TBL  OWNER TO #OWNER;",
            null, "sample_testing_group_view", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ViewSampleTestingGroup.values(), "ViewUserAndMetaDataSopView"),        
        ;
        private ViewsData(String viewScript, FldBusinessRules[] fldBusRules, String dbVwName, String repositoryName, Boolean isProcedure, EnumIntViewFields[] vwFlds, 
                String comment){
            this.getTblBusinessRules=fldBusRules;
            this.viewName=dbVwName;
            this.viewFields=vwFlds;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.viewComment=comment;
            this.viewScript=viewScript;
        }
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public String getViewCreatecript() {return this.viewScript;}
        @Override        public String getViewName() {return this.viewName;}
        @Override        public EnumIntViewFields[] getViewFields() {return this.viewFields;}
        @Override        public String getViewComment() {return this.viewComment;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String viewName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final EnumIntViewFields[] viewFields;
        private final String viewComment;
        private final String viewScript;
    }
/*    public static final String getTableCreationScriptFromDataTable(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "SAMPLE": return createTableScript(TablesData.SAMPLE, schemaNamePrefix);
            case "SAMPLE_ALIQ": return createTableScript(TablesData.SAMPLE_ALIQ, schemaNamePrefix);
            case "SAMPLE_ALIQ_SUB": return createTableScript(TablesData.SAMPLE_ALIQ_SUB, schemaNamePrefix);
            case "SAMPLE_ANALYSIS": return createTableScript(TablesData.SAMPLE_ANALYSIS, schemaNamePrefix);
            case "SAMPLE_ANALYSIS_RESULT": return createTableScript(TablesData.SAMPLE_ANALYSIS_RESULT, schemaNamePrefix);
            case "SAMPLE_COC": return createTableScript(TablesData.SAMPLE_COC, schemaNamePrefix);
            case "CERTIF_USER_ANALYSIS_METHOD": return createTableScript(TablesData.CERTIF_USER_ANALYSIS_METHOD, schemaNamePrefix);
            case "USER_ANALYSIS_METHOD": return createTableScript(TablesData.USER_ANALYSIS_METHOD, schemaNamePrefix);
            case "USER_SOP": return createTableScript(TablesData.USER_SOP, schemaNamePrefix);
            case "SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW": return ViewSampleAnalysisResultWithSpecLimits.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE_COC_NAMES_VIEW": return ViewSampleCocNames.createTableScript(schemaNamePrefix, fields);
            case "USER_AND_META_DATA_SOP_VIEW": return ViewUserAndMetaDataSopView.createTableScript(schemaNamePrefix, fields);
            default: return "TABLE "+tableName+" NOT IN TBLDATA "+LPPlatform.LAB_FALSE;
        }        
    }*/
    private static final java.lang.String FIELDS_NAMES_LIGHT = "light";
    public static final String FIELDS_NAMES_USER_ID="user_id";
    public static final String FIELDS_NAMES_USER_NAME="user_name";
    public static final java.lang.String FIELDS_NAMES_STATUS_PREVIOUS = "status_previous";
    public static final java.lang.String FIELDS_NAMES_STATUS = "status";
    public static final java.lang.String FIELDS_NAMES_SPEC_EVAL = "spec_eval";
    private static final java.lang.String FIELDS_NAMES_CUSTODIAN_CANDIDATE = "custodian_candidate";
    private static final java.lang.String FIELDS_NAMES_CUSTODIAN = "custodian";
    private static final java.lang.String FIELDS_NAMES_COC_CONFIRMED_ON = "coc_confirmed_on";
    
    public static final java.lang.String FIELDS_NAMES_ANALYSIS = "analysis";
    public static final java.lang.String FIELDS_NAMES_REPLICA = "replica";
    public static final java.lang.String FIELDS_NAMES_SUBALIQUOT_ID = "subaliquot_id";
    public static final java.lang.String FIELDS_NAMES_ALIQUOT_ID = "aliquot_id";
    private static final java.lang.String FIELDS_NAMES_ASSIGNED_ON = "assigned_on";
    private static final java.lang.String FIELDS_NAMES_ASSIGNED_BY = "assigned_by";
    private static final java.lang.String FIELDS_NAMES_MANDATORY_LEVEL = "mandatory_level";
    private static final java.lang.String FIELDS_NAMES_EXPIRATION_DATE = "expiration_date";
    private static final java.lang.String FIELDS_NAMES_SOP_NAME = "sop_name";

    public enum Sample implements EnumIntTableFields{
        SAMPLE_ID("sample_id", LPDatabase.integerNotNull(), null, null, null, null),
        CONFIG_CODE("sample_config_code", LPDatabase.stringNotNull(),null, null, "sample config code comment in field", null),
        CONFIG_CODE_VERSION("sample_config_code_version", LPDatabase.integer(),null, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(),null, null, null, null),
        STATUS_PREVIOUS(FIELDS_NAMES_STATUS_PREVIOUS, LPDatabase.stringNotNull(),null, null, null, null),
        LOGGED_ON("logged_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        LOGGED_BY("logged_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        RECEIVED_ON("received_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        RECEIVED_BY("received_by", LPDatabase.string(),null, null, null, null),
        VOLUME( LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.real(),null, null, null, null),
        VOLUME_UOM( LPDatabase.FIELDS_NAMES_VOLUME_UOM, LPDatabase.stringNotNull(),null, null, null, null),
        ALIQUOTED("aliquoted", LPDatabase.booleanFld(false),null, null, null, null),
        ALIQUOT_STATUS("aliq_status", LPDatabase.stringNotNull(),null, null, null, null),
        VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.real(),null, null, null, null),
        VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom", LPDatabase.stringNotNull(),null, null, null, null),
        SAMPLING_DATE("sampling_date", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        REQS_TRACKING_SAMPLING_END("requires_tracking_sampling_end", LPDatabase.booleanFld(),null, null, null, null),
        SAMPLING_DATE_END("sampling_date_end", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        SAMPLER("sampler",LPDatabase.string(),null, null, null, null),
        SAMPLE_ID_RELATED("sample_id_related",LPDatabase.integer(),null, null, null, null),
        SAMPLING_COMMENT("sampling_comment", LPDatabase.string(),null, null, null, null),
        INCUBATION_BATCH("incubation_batch", LPDatabase.string(),null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION_INCUBATOR("incubation_incubator", LPDatabase.string(),null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION_START("incubation_start", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION_START_TEMPERATURE("incubation_start_temperature", LPDatabase.real(), null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION_START_TEMP_EVENT_ID("incubation_start_temp_event_id", LPDatabase.integer(), null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION_END("incubation_end", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION_END_TEMPERATURE("incubation_end_temperature", LPDatabase.real(), null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION_END_TEMP_EVENT_ID("incubation_end_temp_event_id", LPDatabase.integer(), null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION_PASSED("incubation_passed", LPDatabase.booleanFld(false), null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION2_BATCH("incubation2_batch", LPDatabase.string(), null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION2_INCUBATOR("incubation2_incubator", LPDatabase.string(),null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION2_START("incubation2_start", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION2_START_TEMPERATURE("incubation2_start_temperature", LPDatabase.real(),null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION2_START_TEMP_EVENT_ID("incubation2_start_temp_event_id", LPDatabase.integer(), null,null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION2_END("incubation2_end", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION2_END_TEMPERATURE("incubation2_end_temperature", LPDatabase.real(),null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION2_END_TEMP_EVENT_ID("incubation2_end_temp_event_id", LPDatabase.integer(), null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        INCUBATION2_PASSED("incubation2_passed", LPDatabase.booleanFld(),null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        SPEC_CODE("spec_code", LPDatabase.stringNotNull(),null, null, null, null),
        SPEC_CODE_VERSION("spec_code_version", LPDatabase.integer(),null, null, null, null),
        SPEC_VARIATION_NAME("spec_variation_name", LPDatabase.stringNotNull(),null, null, null, null),
        SPEC_ANALYSIS_VARIATION("spec_analysis_variation", LPDatabase.stringNotNull(),null, null, null, null),
        SPEC_EVAL(FIELDS_NAMES_SPEC_EVAL,  LPDatabase.stringNotNull(2),null, null, null, null),
        CUSTODIAN(FIELDS_NAMES_CUSTODIAN,  LPDatabase.stringNotNull(2), null, null, null, null),
        CUSTODIAN_CANDIDATE(FIELDS_NAMES_CUSTODIAN_CANDIDATE,  LPDatabase.stringNotNull(2), null, null, null, null),
        COC_REQUESTED_ON("coc_requested_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        CURRENT_STAGE("current_stage", LPDatabase.stringNotNull(), null, null, null, null),
        PREVIOUS_STAGE("previous_stage", LPDatabase.string(), null, null, null, null),
        READY_FOR_REVISION("ready_for_revision", LPDatabase.booleanFld(), null, null, null, null),
        REVIEWER("reviewer", LPDatabase.string(), null, null, null, null),
        REVIEWED("reviewed", LPDatabase.booleanFld(), null, null, null, null), 
        REVIEWED_BY("reviewed_by", LPDatabase.string(), null, null, null, null), 
        REVIEWED_ON("reviewed_on", LPDatabase.dateTime(), null, null, null, null)
        ;
        private Sample(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }            
    
    public enum SampleCoc implements EnumIntTableFields{
        ID("id", LPDatabase.integer(), null, null, null, null),
        SAMPLE_ID(Sample.SAMPLE_ID.getName(), LPDatabase.integer(), null, null, null, null),
        CUSTODIAN(FIELDS_NAMES_CUSTODIAN, LPDatabase.string(), null, null, null, null),
        CUSTODIAN_CANDIDATE(FIELDS_NAMES_CUSTODIAN_CANDIDATE, LPDatabase.string(), null, null, null, null),
        STARTED_ON("coc_started_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        CONFIRMED_ON(FIELDS_NAMES_COC_CONFIRMED_ON, LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        CUSTODIAN_NOTES("coc_custodian_notes", LPDatabase.string(), null, null, null, null),
        NEW_CUSTODIAN_NOTES("coc_new_custodian_notes", LPDatabase.string(), null, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        SAMPLE_PICTURE("sample_picture", "json", null, null, null, null),
        ;
        private SampleCoc(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum SampleRevisionTestingGroup implements EnumIntTableFields{    
        SAMPLE_ID(Sample.SAMPLE_ID.getName(), LPDatabase.integer(), null, null, null, null),
        TESTING_GROUP("testing_group", LPDatabase.string(), null, null, null, null),
        READY_FOR_REVISION("ready_for_revision", LPDatabase.booleanFld(), null, null, null, null),
        REVIEWED("reviewed", LPDatabase.booleanFld(), null, null, null, null),
        REVISION_ON("revision_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        REVISION_BY("revision_by", LPDatabase.string(), null, null, null, null)
        ;
        private SampleRevisionTestingGroup(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum SampleAnalysis  implements EnumIntTableFields{
        TEST_ID("test_id", LPDatabase.integerNotNull(), null, null, null, null),
        SAMPLE_ID(Sample.SAMPLE_ID.getName(), LPDatabase.integer(), null, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        STATUS_PREVIOUS(FIELDS_NAMES_STATUS_PREVIOUS, LPDatabase.string(), null, null, null, null),
        ANALYSIS(FIELDS_NAMES_ANALYSIS, LPDatabase.stringNotNull(), null, null, null, null),
        METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.string(), null, null, null, null),
        METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer(), null, null, null, null),
        REPLICA(FIELDS_NAMES_REPLICA, LPDatabase.integer(), null, null, null, null),
        ADDED_ON("added_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        ADDED_BY("added_by", LPDatabase.string(), null, null, null, null),
        SPEC_EVAL(FIELDS_NAMES_SPEC_EVAL,  LPDatabase.string(2), null, null, null, null),
        REVIEWER("reviewer", LPDatabase.string(), null, null, null, null),
        REVIEWER_ASSIGNED_ON("reviewer_assigned_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        REVIEWER_ASSIGNED_BY("reviewer_assigned_by", LPDatabase.string(), null, null, null, null),
        ANALYST("analyst", LPDatabase.string(), null, null, null, null),
        ANALYST_ASSIGNED_ON("analyst_assigned_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        ANALYST_ASSIGNED_BY("analyst_assigned_by", LPDatabase.string(), null, null, null, null),
        ANALYST_CERTIFICATION_MODE("analyst_certification_mode", LPDatabase.string(), null, null, null, null),
        ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        TESTING_GROUP("testing_group",  LPDatabase.string(), null, null, null, null),
        READY_FOR_REVISION("ready_for_revision", LPDatabase.booleanFld(), null, null, null, null),     
        SUBALIQUOT_ID(FIELDS_NAMES_SUBALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        REVIEWED("reviewed", LPDatabase.booleanFld(), null, null, null, null),
        REVIEWED_ON("reviewed_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        REVIEWED_BY("reviewed_by", LPDatabase.string(), null, null, null, null),
        ;
        private SampleAnalysis(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }            
    public enum SampleAnalysisResult implements EnumIntTableFields{
        RESULT_ID("result_id", LPDatabase.integer(), null, null, null, null),
        TEST_ID(SampleAnalysis.TEST_ID.getName(), LPDatabase.integer(), null, null, null, null),
        SAMPLE_ID(Sample.SAMPLE_ID.getName(), LPDatabase.integer(), null, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        STATUS_PREVIOUS(FIELDS_NAMES_STATUS_PREVIOUS, LPDatabase.string(), null, null, null, null),
        ANALYSIS(FIELDS_NAMES_ANALYSIS, LPDatabase.stringNotNull(), null, null, null, null),
        METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.string(), null, null, null, null),
        METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer(), null, null, null, null),
        REPLICA(FIELDS_NAMES_REPLICA, LPDatabase.integer(), null, null, null, null),
        PARAM_NAME("param_name", LPDatabase.string(), null, null, null, null),
        PARAM_TYPE("param_type", LPDatabase.string(), null, null, null, null),
        MANDATORY("mandatory", LPDatabase.booleanFld(false), null, null, null, null),
        REQUIRES_LIMIT("requires_limit", LPDatabase.booleanFld(false), null, null, null, null),
        RAW_VALUE("raw_value", LPDatabase.string(), null, null, null, null),
        PRETTY_VALUE("pretty_value", LPDatabase.string(), null, null, null, null),
        ENTERED_ON("entered_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        ENTERED_BY("entered_by", LPDatabase.string(), null, null, null, null),
        REENTERED("reentered", LPDatabase.booleanFld(), null, null, null, null),
        SPEC_EVAL(FIELDS_NAMES_SPEC_EVAL, LPDatabase.string(200), null, null, null, null),
        SPEC_EVAL_DETAIL("spec_eval_detail",  LPDatabase.string(200), null, null, null, null),
        UOM("uom", LPDatabase.string(), null, null, null, null),
        UOM_CONVERSION_MODE("uom_conversion_mode", LPDatabase.string(), null, null, null, null),
        ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        SUBALIQUOT_ID(FIELDS_NAMES_SUBALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        LIMIT_ID("limit_id", LPDatabase.integer(), null, null, null, null),
        REVIEWED("reviewed", LPDatabase.booleanFld(), null, null, null, null),
        REVIEWED_ON("reviewed_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        REVIEWED_BY("reviewed_by", LPDatabase.string(), null, null, null, null),
        MAX_DP("max_dp", LPDatabase.integer(), null, null, null, null),
        MIN_ALLOWED("min_allowed", LPDatabase.real(), null, null, null, null),
        MAX_ALLOWED("max_allowed", LPDatabase.real(), null, null, null, null),
        LIST_ENTRY("list_entry", LPDatabase.string(), null, null, null, null),
        
        /* Este bloque de campos está a nivel de SampleAnalysis, es posible que pueda ser interesante tb en sample_analysis_result
        , REVIEWER("reviewer", LPDatabase.String())
        , REVIEWER_ASSIGNED_ON("reviewer_assigned_on", LPDatabase.dateTime())        
        , REVIEWER_ASSIGNED_BY("reviewer_assigned_by", LPDatabase.String())        
        , ANALYST("analyst", LPDatabase.String())
        , ANALYST_ASSIGNED_ON("analyst_assigned_on", LPDatabase.dateTime())        
        , ANALYST_ASSIGNED_BY("analyst_assigned_by", LPDatabase.String())        
        , ANALYST_CERTIFICATION_MODE("analyst_certification_mode", LPDatabase.String()) */
        //, UNDER_DEVIATION("under_deviation", LPDatabase.Boolean()) Desviaciones aún no implementadas
/*     Este bloque de campos está a nivel de Sample, es posible que pueda ser interesante tb en sample_analysis   
        , VOLUME( LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.Real())
        , VOLUME_UOM( LPDatabase.FIELDS_NAMES_VOLUME_UOM, LPDatabase.StringNotNull())
        , ALIQUOTED("aliquoted", LPDatabase.Boolean(false))
        , ALIQUOT_STATUS("aliq_status", LPDatabase.StringNotNull())
        , VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.Real())
        , VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom", LPDatabase.StringNotNull())
        , SAMPLING_DATE("sampling_date", LPDatabase.dateTime())
        , SAMPLING_COMMENT("sampling_comment", LPDatabase.String())
        , INCUBATION_START("incubation_start", LPDatabase.dateTime())
        , INCUBATION_END("incubation_end", LPDatabase.dateTime())
        , INCUBATION_PASSED("incubation_passed", LPDatabase.Boolean())*/ 
        ;
        private SampleAnalysisResult(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }            
    
    public enum SampleAliq implements EnumIntTableFields{
        ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        SAMPLE_ID(Sample.SAMPLE_ID.getName(), LPDatabase.integer(), null, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, null, null, null),
        SUBALIQ_STATUS("subaliq_status", LPDatabase.string(), null, null, null, null),
        VOLUME( LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.real(), null, null, null, null),
        VOLUME_UOM( LPDatabase.FIELDS_NAMES_VOLUME_UOM, LPDatabase.string(), null, null, null, null),
        VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.real(), null, null, null, null),
        VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom", LPDatabase.string(), null, null, null, null),
        ;
        private SampleAliq(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }        
    public enum SampleAliqSub implements EnumIntTableFields{
        SUBALIQUOT_ID(FIELDS_NAMES_SUBALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        SAMPLE_ID(Sample.SAMPLE_ID.getName(), LPDatabase.integer(), null, null, null, null),
        ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, null, null, null),
        VOLUME( LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.real(), null, null, null, null),
        VOLUME_UOM( LPDatabase.FIELDS_NAMES_VOLUME_UOM, LPDatabase.string(), null, null, null, null),
        ;
        private SampleAliqSub(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }        
    public enum CertifUserAnalysisMethod implements EnumIntTableFields{
        ID("id", LPDatabase.integer(), null, null, null, null),
        METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.string(), null, null, null, null),
        METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer(), null, null, null, null),
        USER_ID(FIELDS_NAMES_USER_ID, LPDatabase.string(), null, null, null, null),
        ASSIGNED_ON(FIELDS_NAMES_ASSIGNED_ON, LPDatabase.dateTime(), null, null, null, null),
        ASSIGNED_BY("assigned_by", LPDatabase.string(), null, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        CERTIFICATION_DATE("certification_date", LPDatabase.dateTime(), null, null, null, null),
        CERTIF_EXPIRY_DATE("certif_expiry_date", LPDatabase.dateTime(), null, null, null, null),
        CERTIF_STARTED("certif_started", LPDatabase.booleanFld(), null, null, null, null),
        CERTIF_COMPLETED("certif_completed", LPDatabase.booleanFld(), null, null, null, null),
        SOP_NAME(FIELDS_NAMES_SOP_NAME, LPDatabase.string(), null, null, null, null),
        //user_name is mandatory due to its involved in the analysis method certification evaluation instead of the user_id one.
        USER_NAME(FIELDS_NAMES_USER_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        LIGHT(FIELDS_NAMES_LIGHT, LPDatabase.stringNotNull(), null, null, null, null),
        TRAINING_ID("training_id", LPDatabase.integer(), null, null, null, null),
        ;
        private CertifUserAnalysisMethod(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }           

    public enum Training implements EnumIntTableFields{
        ID("id", LPDatabase.integer(), null, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        LIGHT(FIELDS_NAMES_LIGHT, LPDatabase.stringNotNull(), null, null, null, null),
        ;
        private Training(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }           
    public enum UserAnalysisMethod implements EnumIntTableFields{
        USER_ANALYSIS_METHOD_ID("user_analysis_method_id", LPDatabase.integer(), null, null, null, null),
        USER_ID(FIELDS_NAMES_USER_ID, LPDatabase.string(), null, null, null, null),
        ANALYSIS(FIELDS_NAMES_ANALYSIS, LPDatabase.string(), null, null, null, null),
        METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.string(), null, null, null, null),
        METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer(), null, null, null, null),
        ASSIGNED_ON(FIELDS_NAMES_ASSIGNED_ON, LPDatabase.dateTime(), null, null, null, null),
        ASSIGNED_BY("assigned_by", LPDatabase.string(), null, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        MANDATORY_LEVEL(FIELDS_NAMES_MANDATORY_LEVEL, LPDatabase.stringNotNull(), null, null, null, null),
        STARTED("started", LPDatabase.booleanFld(), null, null, null, null),
        COMPLETED("completed", LPDatabase.booleanFld(), null, null, null, null),
        CERTIF_EXPIRY_DATE("certif_expiry_date", LPDatabase.dateTime(), null, null, null, null),
        SOP_NAME(FIELDS_NAMES_SOP_NAME, LPDatabase.string(), null, null, null, null),
        USER_NAME(FIELDS_NAMES_USER_NAME, LPDatabase.string(), null, null, null, null),
        LIGHT(FIELDS_NAMES_LIGHT, LPDatabase.stringNotNull(), null, null, null, null),
        ;
        private UserAnalysisMethod(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }           
    public enum UserSop implements EnumIntTableFields{
        USER_SOP_ID("user_sop_id", LPDatabase.integer(), null, null, null, null),
        USER_ID(FIELDS_NAMES_USER_ID, LPDatabase.string(), null, null, null, null),
        SOP_ID("sop_id", LPDatabase.string(), null, null, null, null),
        SOP_LIST_ID("sop_list_id", LPDatabase.string(), null, null, null, null),
        ASSIGNED_ON(FIELDS_NAMES_ASSIGNED_ON, LPDatabase.dateTime(), null, null, null, null),
        ASSIGNED_BY("assigned_by", LPDatabase.string(), null, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        MANDATORY_LEVEL(FIELDS_NAMES_MANDATORY_LEVEL, LPDatabase.string(), null, null, null, null),
        READ_STARTED("read_started", LPDatabase.booleanFld(), null, null, null, null),
        READ_COMPLETED("read_completed", LPDatabase.booleanFld(), null, null, null, null),
        UNDERSTOOD("understood", LPDatabase.booleanFld(), null, null, null, null),
        EXPIRATION_DATE(FIELDS_NAMES_EXPIRATION_DATE, LPDatabase.dateTime(), null, null, null, null),
        SOP_NAME(FIELDS_NAMES_SOP_NAME, LPDatabase.string(), null, null, null, null),
        USER_NAME(FIELDS_NAMES_USER_NAME, LPDatabase.string(), null, null, null, null),
        LIGHT(FIELDS_NAMES_LIGHT, LPDatabase.stringNotNull(), null, null, null, null),
        ;
        private UserSop(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }        
    public enum SavedQueries implements EnumIntTableFields{
        ID("id", LPDatabase.integer(), null, null, null, null),
        NAME("name", LPDatabase.string(), null, null, null, null),
        OWNER("owner", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        PRIVATE("private", LPDatabase.booleanFld(), null, null, null, null),
        READABLE_BY("readable_by", LPDatabase.string(), null, null, null, null),
        DEFINITION("definition", LPDatabase.string(), null, null, null, null),
        ;
        private SavedQueries(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }        
    public enum ViewSampleCocNames implements EnumIntViewFields{
        SAMPLE_ID(Sample.SAMPLE_ID.getName(), "", Sample.SAMPLE_ID, null, null, null),
        CUSTODIAN(FIELDS_NAMES_CUSTODIAN, "", Sample.CUSTODIAN, null, null, null),
        CUSTODIAN_CANDIDATE(FIELDS_NAMES_CUSTODIAN_CANDIDATE, "", Sample.CUSTODIAN_CANDIDATE, null, null, null),
        COC_STARTED_ON("coc_started_on", "", SampleCoc.STARTED_ON, null, null, null),
        COC_CONFIRMED_ON(FIELDS_NAMES_COC_CONFIRMED_ON, "", SampleCoc.CONFIRMED_ON, null, null, null),
        COC_CUSTODIAN_NOTES("coc_custodian_notes", "", SampleCoc.CUSTODIAN_NOTES, null, null, null),
        NEW_CUSTODIAN_NOTES("coc_new_custodian_notes","", SampleCoc.NEW_CUSTODIAN_NOTES, null, null, null),
        SAMPLE_PICTURE("sample_picture", "", SampleCoc.SAMPLE_PICTURE, null, null, null),
        ID("id", "", SampleCoc.ID, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, "", SampleCoc.STATUS, null, null, null),
        CUSTODIAN_NAME("custodian_name", "", SampleCoc.CUSTODIAN, null, null, null),
        CANDIDATE_NAME("candidate_name", "", SampleCoc.CUSTODIAN_CANDIDATE, null, null, null),
        ;
        private ViewSampleCocNames(String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
            this.fldName=name;
            this.fldAliasInView=vwAliasName;
            this.fldMask=fldMask;
            this.fldComment=comment;
            this.fldBusinessRules=busRules;
            this.fldObj=fldObj;
        }
        private final String fldName;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;        
        @Override public String getName() {return fldName;}
        @Override public String getViewAliasName() {return this.fldAliasInView;}
        @Override public String getFieldMask() {return this.fldMask;}
        @Override public String getFieldComment() {return this.fldComment;}
        @Override public FldBusinessRules[] getFldBusinessRules() {return this.fldBusinessRules;}
        @Override public EnumIntTableFields getTableField() {return this.fldObj;}
    }        
    public enum ViewUserAndMetaDataSopView implements EnumIntViewFields{
        PROCEDURE("procedure", "", UserSop.USER_NAME, null, null, null),
        USER_SOP_ID("user_sop_id", "", UserSop.USER_SOP_ID, null, null, null),
        USER_ID(FIELDS_NAMES_USER_ID, "", UserSop.USER_ID, null, null, null),
        SOP_LIST_ID("sop_list_id", "", UserSop.SOP_LIST_ID, null, null, null),
        ASSIGNED_ON(FIELDS_NAMES_ASSIGNED_ON, "", UserSop.ASSIGNED_ON, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, "", UserSop.STATUS, null, null, null),
        MANDATORY_LEVEL(FIELDS_NAMES_MANDATORY_LEVEL, "", UserSop.MANDATORY_LEVEL, null, null, null),
        READ_STARTED("read_started","", UserSop.READ_STARTED, null, null, null),
        READ_COMPLETED("read_completed", "", UserSop.READ_COMPLETED, null, null, null),
        UNDERSTOOD("understood", "", UserSop.UNDERSTOOD, null, null, null),
        EXPIRATION_DATE(FIELDS_NAMES_EXPIRATION_DATE, "", UserSop.EXPIRATION_DATE, null, null, null),
        SOP_NAME(FIELDS_NAMES_SOP_NAME, "", UserSop.SOP_NAME, null, null, null),
        USER_NAME(FIELDS_NAMES_USER_NAME, "", UserSop.USER_NAME, null, null, null),
        LIGHT(FIELDS_NAMES_LIGHT, "", UserSop.LIGHT, null, null, null),
        BRIEF_SUMMARY("brief_summary", "", TblsCnfg.SopMetaData.BRIEF_SUMMARY, null, null, null),
        FILE_LINK("file_link", "", TblsCnfg.SopMetaData.FILE_LINK, null, null, null),
        AUTHOR("author", "", TblsCnfg.SopMetaData.AUTHOR, null, null, null),
        CERTIFICATION_MODE("certification_mode", "", TblsCnfg.SopMetaData.CERTIFICATION_MODE, null, null, null),        
        ;
        private ViewUserAndMetaDataSopView(String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
            this.fldName=name;
            this.fldAliasInView=vwAliasName;
            this.fldMask=fldMask;
            this.fldComment=comment;
            this.fldBusinessRules=busRules;
            this.fldObj=fldObj;
        }
        private final String fldName;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;        
        @Override public String getName() {return fldName;}
        @Override public String getViewAliasName() {return this.fldAliasInView;}
        @Override public String getFieldMask() {return this.fldMask;}
        @Override public String getFieldComment() {return this.fldComment;}
        @Override public FldBusinessRules[] getFldBusinessRules() {return this.fldBusinessRules;}
        @Override public EnumIntTableFields getTableField() {return this.fldObj;}
    }        

    public enum ViewUserAndAnalysisMethodCertificationView implements EnumIntViewFields{
        PROCEDURE("procedure", "", CertifUserAnalysisMethod.USER_NAME, null, null, null),
        ID("id", "", CertifUserAnalysisMethod.ID, null, null, null),
        USER_ID(FIELDS_NAMES_USER_ID, "", CertifUserAnalysisMethod.USER_ID, null, null, null),
        METHOD_CODE("code", "", Methods.CODE, null, null, null),
        METHOD_NAME("method_name", "", CertifUserAnalysisMethod.METHOD_NAME, null, null, null),
        METHOD_VERSION("method_version", "", CertifUserAnalysisMethod.METHOD_VERSION, null, null, null),
        ASSIGNED_ON(FIELDS_NAMES_ASSIGNED_ON, "", CertifUserAnalysisMethod.ASSIGNED_ON, null, null, null),
        ASSIGNED_BY(FIELDS_NAMES_ASSIGNED_BY, "", CertifUserAnalysisMethod.ASSIGNED_BY, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, "", CertifUserAnalysisMethod.STATUS, null, null, null),
        CERTIFICATION_DATE("certification_date", "", CertifUserAnalysisMethod.CERTIFICATION_DATE, null, null, null),
        CERTIF_EXPIRY_DATE("certif_expiry_date", "", CertifUserAnalysisMethod.CERTIF_EXPIRY_DATE, null, null, null),
        CERTIF_STARTED("certif_started","", CertifUserAnalysisMethod.CERTIF_STARTED, null, null, null),
        CERTIF_COMPLETED("certif_completed", "", CertifUserAnalysisMethod.CERTIF_COMPLETED, null, null, null),
        SOP_NAME(FIELDS_NAMES_SOP_NAME, "", CertifUserAnalysisMethod.SOP_NAME, null, null, null),
        USER_NAME(FIELDS_NAMES_USER_NAME, "", CertifUserAnalysisMethod.USER_NAME, null, null, null),
        LIGHT(FIELDS_NAMES_LIGHT, "", CertifUserAnalysisMethod.LIGHT, null, null, null),
        TRAINING_ID("training_id", "", CertifUserAnalysisMethod.TRAINING_ID, null, null, null),
        METHOD_IS_ACTIVE("active", "method_is_active", Methods.ACTIVE, null, null, null),
        METHOD_EXPIRES("expires", "method_expires", Methods.EXPIRES, null, null, null),
        METHOD_EXPIRY_INTERVAL_INFO("expiry_interval_info", "method_expiry_interval_info", Methods.EXPIRY_INTERVAL_INFO, null, null, null),
        CERTIFICATION_MODE("certification_mode", "method_certification_mode", Methods.CERTIFICATION_MODE, null, null, null),
        ;
        private ViewUserAndAnalysisMethodCertificationView(String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
            this.fldName=name;
            this.fldAliasInView=vwAliasName;
            this.fldMask=fldMask;
            this.fldComment=comment;
            this.fldBusinessRules=busRules;
            this.fldObj=fldObj;
        }
        private final String fldName;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;        
        @Override public String getName() {return fldName;}
        @Override public String getViewAliasName() {return this.fldAliasInView;}
        @Override public String getFieldMask() {return this.fldMask;}
        @Override public String getFieldComment() {return this.fldComment;}
        @Override public FldBusinessRules[] getFldBusinessRules() {return this.fldBusinessRules;}
        @Override public EnumIntTableFields getTableField() {return this.fldObj;}
    }        

    public enum ViewSampleAnalysisResultWithSpecLimits implements EnumIntViewFields{
        RESULT_ID(SampleAnalysisResult.RESULT_ID.getName(), "sar.result_id", SampleAnalysisResult.RESULT_ID, null, null, null),
        TEST_ID(SampleAnalysis.TEST_ID.getName(), "sar.test_id", SampleAnalysisResult.TEST_ID, null, null, null),
        SAMPLE_ID(Sample.SAMPLE_ID.getName(), "sar.sample_id", SampleAnalysisResult.SAMPLE_ID, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, "sar.status", SampleAnalysisResult.STATUS, null, null, null),
        STATUS_PREVIOUS(FIELDS_NAMES_STATUS_PREVIOUS, "sar.status_previous", SampleAnalysisResult.STATUS_PREVIOUS, null, null, null),
        ANALYSIS(FIELDS_NAMES_ANALYSIS, "sar.analysis", SampleAnalysisResult.ANALYSIS, null, null, null),
        METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, "sar.method_name", SampleAnalysisResult.METHOD_NAME, null, null, null),
        METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, "sar.method_version", SampleAnalysisResult.METHOD_VERSION, null, null, null),
        REPLICA(FIELDS_NAMES_REPLICA, "sar.replica", SampleAnalysisResult.REPLICA, null, null, null),
        PARAM_NAME("param_name", "sar.param_name", SampleAnalysisResult.PARAM_NAME, null, null, null),
        PARAM_TYPE("param_type", "sar.param_type", SampleAnalysisResult.PARAM_TYPE, null, null, null),
        MANDATORY("mandatory", "sar.mandatory", SampleAnalysisResult.MANDATORY, null, null, null),
        REQUIRES_LIMIT("requires_limit", "sar.requires_limit", SampleAnalysisResult.REQUIRES_LIMIT, null, null, null),
        RAW_VALUE("raw_value", "sar.raw_value", SampleAnalysisResult.RAW_VALUE, null, null, null),
        RAW_VALUE_NUM("raw_value_num", "case when isnumeric(sar.raw_value) then to_number(sar.raw_value::text, '9999'::text) else null end", SampleAnalysisResult.REPLICA, null, null, null),
        PRETTY_VALUE("pretty_value", "sar.pretty_value", SampleAnalysisResult.PRETTY_VALUE, null, null, null),
        ENTERED_ON("entered_on", "sar.entered_on", SampleAnalysisResult.ENTERED_ON, null, null, null),
        ENTERED_BY("entered_by", "sar.entered_by", SampleAnalysisResult.ENTERED_BY, null, null, null),
        REENTERED("reentered", "sar.reentered", SampleAnalysisResult.REENTERED, null, null, null),
        SPEC_EVAL(FIELDS_NAMES_SPEC_EVAL, "sar.spec_eval", SampleAnalysisResult.SPEC_EVAL, null, null, null),
        SPEC_EVAL_DETAIL("spec_eval_detail", "sar.spec_eval_detail", SampleAnalysisResult.SPEC_EVAL_DETAIL, null, null, null),
        UOM("uom", "sar.uom", SampleAnalysisResult.UOM, null, null, null),
        UOM_CONVERSION_MODE("uom_conversion_mode", "sar.uom_conversion_mode", SampleAnalysisResult.UOM_CONVERSION_MODE, null, null, null),
        ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, "sar.aliquot_id", SampleAnalysisResult.ALIQUOT_ID, null, null, null),
        SUBALIQUOT_ID(FIELDS_NAMES_SUBALIQUOT_ID, "sar.subaliquot_id", SampleAnalysisResult.SUBALIQUOT_ID, null, null, null),
        MAX_DP("max_dp", "sar.max_dp", SampleAnalysisResult.MAX_DP, null, null, null),
        MIN_ALLOWED("min_allowed", "sar.min_allowed", SampleAnalysisResult.MIN_ALLOWED, null, null, null),
        MAX_ALLOWED("max_allowed", "sar.max_allowed", SampleAnalysisResult.MAX_ALLOWED, null, null, null),
        LIST_ENTRY("list_entry", "sar.list_entry", SampleAnalysisResult.LIST_ENTRY, null, null, null),
        SAMPLE_CONFIG_CODE("sample_config_code", "s."+TblsData.Sample.CONFIG_CODE.getName(), Sample.CONFIG_CODE, null, null, null),
        SAMPLE_STATUS("sample_status", "s.status", Sample.STATUS, null, null, null),
        CURRENT_STAGE("current_stage", "s.current_stage", Sample.CURRENT_STAGE, null, null, null),
        PROGRAM_NAME("program_name", "s.program_name", TblsEnvMonitData.Sample.PROGRAM_NAME, null, null, null),
        SAMPLING_DATE("sampling_date", "s.sampling_date", Sample.SAMPLING_DATE, null, null, null),
        SHIFT("shift", "s.shift", TblsEnvMonitData.Sample.SHIFT, null, null, null),
        AREA("area", "s.area", TblsEnvMonitData.Sample.AREA, null, null, null),
        LOCATION_NAME("location_name", "s.location_name", TblsEnvMonitData.Sample.LOCATION_NAME, null, null, null),
        PRODUCTION_LOT("production_lot", "s.production_lot", TblsEnvMonitData.Sample.PRODUCTION_LOT, null, null, null),
        PROGRAM_DAY_ID("program_day_id", "s.program_day_id", TblsEnvMonitData.Sample.PROG_DAY_ID, null, null, null),
        PROGRAM_DAY_DATE("program_day_date", "s.program_day_date", TblsEnvMonitData.Sample.PROG_DAY_DATE, null, null, null),
        SAMPLE_ANALYSIS_STATUS("sample_analysis_status", "sa.status", SampleAnalysis.STATUS, null, null, null),
        SAMPLE_ANALYSIS_READY_FOR_REVISION("sample_analysis_"+TblsData.SampleAnalysis.READY_FOR_REVISION.getName(), "sa."+TblsData.Sample.READY_FOR_REVISION.getName(), SampleAnalysis.READY_FOR_REVISION, null, null, null),
        TESTING_GROUP("testing_group", "sa.testing_group", SampleAnalysis.TESTING_GROUP, null, null, null),
        LOGGED_ON("logged_on", "s.logged_on", Sample.LOGGED_ON, null, null, null),
        SAMPLER("sampler", "s.sampler", Sample.SAMPLER, null, null, null),
        SAMPLER_AREA("sampler_area", "s.sampler_area", TblsEnvMonitData.Sample.SAMPLER_AREA, null, null, null),
        READY_FOR_REVISION(TblsData.Sample.READY_FOR_REVISION.getName(), "s."+TblsData.Sample.READY_FOR_REVISION.getName(), TblsData.Sample.READY_FOR_REVISION, null, null, null),
        LIMIT_ID("limit_id", "spcLim.limit_id", SpecLimits.LIMIT_ID, null, null, null),
        SPEC_CODE("spec_code", "spcLim.code", SpecLimits.CODE, null, null, null),
        SPEC_CONFIG_VERSION("spec_config_version", "spcLim.config_version", SpecLimits.CONFIG_VERSION, null, null, null),
        SPEC_VARIATION_NAME("spec_variation_name", "spcLim.variation_name", SpecLimits.VARIATION_NAME, null, null, null),
        ANALYSIS_SPEC_LIMITS("analysis_spec_limits", "spcLim.analysis", SpecLimits.ANALYSIS, null, null, null),
        METHOD_NAME_SPEC_LIMITS("method_name_spec_limits", "spcLim.method_name", SpecLimits.METHOD_NAME, null, null, null),
        METHOD_VERSION_SPEC_LIMITS("method_version_spec_limits", "spcLim.method_version", SpecLimits.METHOD_VERSION, null, null, null),
        PARAMETER("parameter", "spcLim.parameter", SpecLimits.PARAMETER, null, null, null),
        RULE_TYPE("rule_type", "spcLim.rule_type", SpecLimits.RULE_TYPE, null, null, null),
        RULE_VARIABLES("rule_variables", "spcLim.rule_variables", SpecLimits.RULE_VARIABLES, null, null, null),
        UOM_SPEC_LIMITS("uom_spec_limits", "spcLim.uom", SpecLimits.UOM, null, null, null),
        UOM_CONVERSION_MODE_SPEC_LIMITS("uom_conversion_mode_spec_limits", "spcLim.uom_conversion_mode", SpecLimits.UOM_CONVERSION_MODE, null, null, null),
        MIN_VAL_ALLOWED("min_val_allowed", "spcLim.min_val_allowed", SpecLimits.MIN_VAL_ALLOWED, null, null, null),
        MAX_VAL_ALLOWED("max_val_allowed", "spcLim.max_val_allowed", SpecLimits.MAX_VAL_ALLOWED, null, null, null),
        MIN_VAL_ALLOWED_IS_STRICT("min_allowed_strict", "spcLim.min_allowed_strict", SpecLimits.MIN_VAL_ALLOWED_IS_STRICT, null, null, null),
        MAX_VAL_ALLOWED_IS_STRICT("max_allowed_strict", "spcLim.max_allowed_strict", SpecLimits.MAX_VAL_ALLOWED_IS_STRICT, null, null, null),
        MIN_VAL_FOR_UNDETERMINED("min_undetermined", "spcLim.min_undetermined", SpecLimits.MIN_VAL_FOR_UNDETERMINED, null, null, null),
        MAX_VAL_FOR_UNDETERMINED("max_undetermined", "spcLim.max_undetermined", SpecLimits.MAX_VAL_FOR_UNDETERMINED, null, null, null),
        MIN_VAL_UNDETERMINED_IS_STRICT("min_undet_strict", "spcLim.min_undet_strict", SpecLimits.MIN_VAL_UNDETERMINED_IS_STRICT, null, null, null),
        MAX_VAL_UNDETERMINED_IS_STRICT("max_undet_strict", "spcLim.max_undet_strict", SpecLimits.MAX_VAL_UNDETERMINED_IS_STRICT, null, null, null),
        HAS_PREINVEST("has_pre_invest", "CASE WHEN pca.id IS NULL THEN 'NO' ELSE 'YES' END", SpecLimits.MAX_VAL_ALLOWED_IS_STRICT, null, null, null),
        PREINVEST_ID("pre_invest_id", "pca.id", ProgramCorrectiveAction.ID, null, null, null),
        HAS_INVEST("has_invest", "CASE WHEN io.id IS NULL THEN 'NO' ELSE 'YES' END", SpecLimits.MAX_VAL_ALLOWED_IS_STRICT, null, null, null),
        INVEST_ID("invest_id", "io.invest_id", InvestObjects.INVEST_ID, null, null, null),
        INVEST_OBJECT_ID("invest_object_id", "io.id", InvestObjects.OBJECT_ID, null, null, null),
        SAMPLE_REVIEWER("sample_reviewer", "s.reviewer", Sample.REVIEWER, null, null, null),
        TEST_REVIEWER("test_reviewer", "sa.reviewer", SampleAnalysis.REVIEWER, null, null, null),        
        ;
        private ViewSampleAnalysisResultWithSpecLimits(String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
//            try{
//            this.fldName="";
            this.fldName=name;
            this.fldAliasInView=vwAliasName;
            this.fldMask=fldMask;
            this.fldComment=comment;
            this.fldBusinessRules=busRules;
            this.fldObj=fldObj;
/*            }catch(Exception e){
                String s= e.getMessage();
                //String s2=name;
                this.fldName="";
            }*/
        }
        private final String fldName;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;        
        @Override public String getName() {return fldName;}
        @Override public String getViewAliasName() {return this.fldAliasInView;}
        @Override public String getFieldMask() {return this.fldMask;}
        @Override public String getFieldComment() {return this.fldComment;}
        @Override public FldBusinessRules[] getFldBusinessRules() {return this.fldBusinessRules;}
        @Override public EnumIntTableFields getTableField() {return this.fldObj;}
    }        

    public enum ViewSampleTestingGroup implements EnumIntViewFields{
        SAMPLE_ID(Sample.SAMPLE_ID.getName(), "s.sample_id", Sample.SAMPLE_ID, null, null, null),
        SAMPLE_CONFIG_CODE("sample_config_code", "s."+TblsData.Sample.CONFIG_CODE.getName(), Sample.CONFIG_CODE, null, null, null),
        SAMPLE_STATUS("sample_status", "s.status", Sample.STATUS, null, null, null),
        CURRENT_STAGE("current_stage", "s.current_stage", Sample.CURRENT_STAGE, null, null, null),
        PROGRAM_NAME("program_name", "s.program_name", TblsEnvMonitData.Sample.PROGRAM_NAME, null, null, null),
        SAMPLING_DATE("sampling_date", "s.sampling_date", Sample.SAMPLING_DATE, null, null, null),
        SHIFT("shift", "s.shift", TblsEnvMonitData.Sample.SHIFT, null, null, null),
        AREA("area", "s.area", TblsEnvMonitData.Sample.AREA, null, null, null),
        LOCATION_NAME("location_name", "s.location_name", TblsEnvMonitData.Sample.LOCATION_NAME, null, null, null),
        PRODUCTION_LOT("production_lot", "s.production_lot", TblsEnvMonitData.Sample.PRODUCTION_LOT, null, null, null),
        PROGRAM_DAY_ID("program_day_id", "s.program_day_id", TblsEnvMonitData.Sample.PROG_DAY_ID, null, null, null),
        PROGRAM_DAY_DATE("program_day_date", "s.program_day_date", TblsEnvMonitData.Sample.PROG_DAY_DATE, null, null, null),
        TESTING_GROUP("testing_group", "stg.testing_group", TblsData.SampleRevisionTestingGroup.TESTING_GROUP, null, null, null),
        READY_FOR_REVISION("ready_for_revision", "stg.ready_for_revision", TblsData.SampleRevisionTestingGroup.READY_FOR_REVISION, null, null, null),
        REVIEWED("reviewed", "stg.reviewed", TblsData.SampleRevisionTestingGroup.REVIEWED, null, null, null),
        REVISION_ON("revision_on", "stg.revision_on", TblsData.SampleRevisionTestingGroup.REVISION_ON, null, null, null),
        REVISION_BY("revision_by", "stg.revision_by", TblsData.SampleRevisionTestingGroup.REVISION_BY, null, null, null)
        ;
        private ViewSampleTestingGroup(String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
            this.fldName=name;
            this.fldAliasInView=vwAliasName;
            this.fldMask=fldMask;
            this.fldComment=comment;
            this.fldBusinessRules=busRules;
            this.fldObj=fldObj;
        }
        private final String fldName;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;        
        @Override public String getName() {return fldName;}
        @Override public String getViewAliasName() {return this.fldAliasInView;}
        @Override public String getFieldMask() {return this.fldMask;}
        @Override public String getFieldComment() {return this.fldComment;}
        @Override public FldBusinessRules[] getFldBusinessRules() {return this.fldBusinessRules;}
        @Override public EnumIntTableFields getTableField() {return this.fldObj;}
    } 
    
}
