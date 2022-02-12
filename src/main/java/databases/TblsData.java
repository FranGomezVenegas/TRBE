/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPPlatform;
import static lbplanet.utilities.LPDatabase.*;
import static databases.TblsCnfg.SCHEMATAG;
import static databases.TblsCnfg.TABLETAG;
import static databases.TblsCnfg.OWNERTAG;
import static databases.TblsCnfg.TABLESPACETAG;
import static databases.TblsCnfg.FIELDSTAG;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ForeignkeyFld;
import trazit.enums.ReferenceFld;
import static trazit.enums.deployrepository.DeployTables.createTableScript;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class TblsData {

    public static final String getTableCreationScriptFromDataTable(String tableName, String schemaNamePrefix, String[] fields){
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
    }
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

    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA.getName();

    public enum TablesData implements EnumIntTables{
        
        CERTIF_USER_ANALYSIS_METHOD(null, "certif_user_analysis_method", SCHEMA_NAME, true, CertifUserAnalysisMethod.values(), CertifUserAnalysisMethod.FLD_ID.getName(),
            new String[]{CertifUserAnalysisMethod.FLD_ID.getName()}, null, "certif_user_analysis_method table, manage user analysis method certification tracking"),
        TRAINING(null, "training", SCHEMA_NAME, true, Training.values(), Training.FLD_ID.getName(),
            new String[]{Training.FLD_ID.getName()}, null, "Training table"),
        USER_SOP(null, "user_sop", SCHEMA_NAME, true, UserSop.values(), UserSop.FLD_SOP_ID.getName(),
            new String[]{UserSop.FLD_SOP_ID.getName()}, null, "Training table"),
        USER_ANALYSIS_METHOD(null, "user_analysis_method", SCHEMA_NAME, true, UserAnalysisMethod.values(), UserAnalysisMethod.FLD_USER_ANALYSIS_METHOD_ID.getName(),
            new String[]{UserAnalysisMethod.FLD_USER_ANALYSIS_METHOD_ID.getName()}, null, "Training table"),
        SAVED_QUERIES(null, "saved_queries", SCHEMA_NAME, true, SavedQueries.values(), SavedQueries.FLD_ID.getName(),
            new String[]{SavedQueries.FLD_ID.getName()}, null, "Training table"),
        SAMPLE(null, "sample", SCHEMA_NAME, true, Sample.values(), Sample.FLD_SAMPLE_ID.getName(),
            new String[]{Sample.FLD_SAMPLE_ID.getName()}, null, "sample table"),
        SAMPLE_ANALYSIS(null, "sample_analysis", SCHEMA_NAME, true, SampleAnalysis.values(), Sample.FLD_SAMPLE_ID.getName(),
            new String[]{SampleAnalysis.FLD_TEST_ID.getName()}, 
            new ForeignkeyFld(SampleAnalysis.FLD_SAMPLE_ID.getName(), SCHEMA_NAME, SAMPLE.getTableName(), Sample.FLD_SAMPLE_ID.getName()), "sample analysis table"),
        SAMPLE_ANALYSIS_RESULT(null, "sample_analysis_result", SCHEMA_NAME, true, SampleAnalysisResult.values(), Sample.FLD_SAMPLE_ID.getName(),
            new String[]{SampleAnalysisResult.FLD_RESULT_ID.getName()}, 
            new ForeignkeyFld(SampleAnalysisResult.FLD_TEST_ID.getName(), SCHEMA_NAME, SAMPLE_ANALYSIS.getTableName(), SampleAnalysis.FLD_TEST_ID.getName()), "sample analysis results table"),
        SAMPLE_ALIQ(null, "sample_aliq", SCHEMA_NAME, true, SampleAliq.values(), SampleAliq.FLD_ALIQUOT_ID.getName(),
            new String[]{SampleAliq.FLD_ALIQUOT_ID.getName()}, 
            new ForeignkeyFld(SampleAliq.FLD_SAMPLE_ID.getName(), SCHEMA_NAME, SAMPLE.getTableName(), Sample.FLD_SAMPLE_ID.getName()), "sample aliquot table"),
        SAMPLE_ALIQ_SUB(null, "sample_aliq_sub", SCHEMA_NAME, true, SampleAliqSub.values(), SampleAliqSub.FLD_SUBALIQUOT_ID.getName(),
            new String[]{SampleAliqSub.FLD_SUBALIQUOT_ID.getName()}, 
            new ForeignkeyFld(SampleAliqSub.FLD_ALIQUOT_ID.getName(), SCHEMA_NAME, SAMPLE_ALIQ.getTableName(), SampleAliq.FLD_ALIQUOT_ID.getName()), "sample sub aliquot table"),
        PRODUCT(null, "product", "data", true, Sample.values(), null, 
            new String[]{Sample.FLD_SAMPLE_ID.getName()}, 
            new ForeignkeyFld(Sample.FLD_SAMPLE_ID_RELATED.getName(), SCHEMA_NAME, "sample", Sample.FLD_SAMPLE_ID.getName()), "product table comment"),
        SAMPLE_COC(new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleChangeOfCustody", false, false, new String[]{"ENABLED"}, null)},
            "sample_coc", SCHEMA_NAME, true, SampleCoc.values(), SampleCoc.FLD_ID.getName(),
            new String[]{SampleCoc.FLD_ID.getName()}, null, ""),
        SAMPLE_REVISION_TESTING_GROUP(new FldBusinessRules[]{new FldBusinessRules("procedure", "revisionTestinGroupRequired", false, false, new String[]{"ENABLED"}, null)},
            "sample_revision_testing_group", SCHEMA_NAME, true, SampleRevisionTestingGroup.values(), null,
            new String[]{SampleRevisionTestingGroup.FLD_SAMPLE_ID.getName(), SampleRevisionTestingGroup.FLD_TESTING_GROUP.getName()}, null, ""),
        
        ;
        private TablesData(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
                String seqName, String[] primaryK, ForeignkeyFld foreignK, String comment){
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
        @Override        public ForeignkeyFld getForeignKey() {return this.foreignkey;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String tableName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final String sequence;
        private final EnumIntTableFields[] tableFields;
        private final String[] primarykey;
        private final ForeignkeyFld foreignkey;
        private final String tableComment;
    }
    public enum Sample implements EnumIntTableFields{
        FLD_SAMPLE_ID("sample_id", LPDatabase.integerNotNull(), null, null, null, null),
        FLD_CONFIG_CODE("sample_config_code", LPDatabase.stringNotNull(),null, null, "sample config code comment in field", null),
        FLD_CONFIG_CODE_VERSION("sample_config_code_version", LPDatabase.integer(),null, null, null, null),
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(),null, null, null, null),
        FLD_STATUS_PREVIOUS(FIELDS_NAMES_STATUS_PREVIOUS, LPDatabase.stringNotNull(),null, null, null, null),
        FLD_LOGGED_ON("logged_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        FLD_LOGGED_BY("logged_by", LPDatabase.string(), null, new ReferenceFld("config", "person", "person_id"), null, null),
        FLD_RECEIVED_ON("received_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        FLD_RECEIVED_BY("received_by", LPDatabase.string(),null, null, null, null),
        FLD_VOLUME( LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.real(),null, null, null, null),
        FLD_VOLUME_UOM( LPDatabase.FIELDS_NAMES_VOLUME_UOM, LPDatabase.stringNotNull(),null, null, null, null),
        FLD_ALIQUOTED("aliquoted", LPDatabase.booleanFld(false),null, null, null, null),
        FLD_ALIQUOT_STATUS("aliq_status", LPDatabase.stringNotNull(),null, null, null, null),
        FLD_VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.real(),null, null, null, null),
        FLD_VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom", LPDatabase.stringNotNull(),null, null, null, null),
        FLD_SAMPLING_DATE("sampling_date", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        FLD_REQS_TRACKING_SAMPLING_END("requires_tracking_sampling_end", LPDatabase.booleanFld(),null, null, null, null),
        FLD_SAMPLING_DATE_END("sampling_date_end", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        FLD_SAMPLER("sampler",LPDatabase.string(),null, null, null, null),
        FLD_SAMPLE_ID_RELATED("sample_id_related",LPDatabase.integer(),null, null, null, null),
        FLD_SAMPLING_COMMENT("sampling_comment", LPDatabase.string(),null, null, null, null),
        FLD_INCUBATION_BATCH("incubation_batch", LPDatabase.string(),null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION_INCUBATOR("incubation_incubator", LPDatabase.string(),null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION_START("incubation_start", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION_START_TEMPERATURE("incubation_start_temperature", LPDatabase.real(), null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION_START_TEMP_EVENT_ID("incubation_start_temp_event_id", LPDatabase.integer(), null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION_END("incubation_end", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION_END_TEMPERATURE("incubation_end_temperature", LPDatabase.real(), null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION_END_TEMP_EVENT_ID("incubation_end_temp_event_id", LPDatabase.integer(), null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION_PASSED("incubation_passed", LPDatabase.booleanFld(false), null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION2_BATCH("incubation2_batch", LPDatabase.string(), null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION2_INCUBATOR("incubation2_incubator", LPDatabase.string(),null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION2_START("incubation2_start", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION2_START_TEMPERATURE("incubation2_start_temperature", LPDatabase.real(),null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION2_START_TEMP_EVENT_ID("incubation2_start_temp_event_id", LPDatabase.integer(), null,null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION2_END("incubation2_end", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION2_END_TEMPERATURE("incubation2_end_temperature", LPDatabase.real(),null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION2_END_TEMP_EVENT_ID("incubation2_end_temp_event_id", LPDatabase.integer(), null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_INCUBATION2_PASSED("incubation2_passed", LPDatabase.booleanFld(),null, null, null, 
            new FldBusinessRules[]{new FldBusinessRules("procedure", "sampleIncubationMode", true, false, null, new String[]{"DISABLED"})}),
        FLD_SPEC_CODE("spec_code", LPDatabase.stringNotNull(),null, null, null, null),
        FLD_SPEC_CODE_VERSION("spec_code_version", LPDatabase.integer(),null, null, null, null),
        FLD_SPEC_VARIATION_NAME("spec_variation_name", LPDatabase.stringNotNull(),null, null, null, null),
        FLD_SPEC_ANALYSIS_VARIATION("spec_analysis_variation", LPDatabase.stringNotNull(),null, null, null, null),
        FLD_SPEC_EVAL(FIELDS_NAMES_SPEC_EVAL,  LPDatabase.stringNotNull(2),null, null, null, null),
        FLD_CUSTODIAN(FIELDS_NAMES_CUSTODIAN,  LPDatabase.stringNotNull(2), null, null, null, null),
        FLD_CUSTODIAN_CANDIDATE(FIELDS_NAMES_CUSTODIAN_CANDIDATE,  LPDatabase.stringNotNull(2), null, null, null, null),
        FLD_COC_REQUESTED_ON("coc_requested_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        FLD_CURRENT_STAGE("current_stage", LPDatabase.stringNotNull(), null, null, null, null),
        FLD_PREVIOUS_STAGE("previous_stage", LPDatabase.string(), null, null, null, null),
        FLD_READY_FOR_REVISION("ready_for_revision", LPDatabase.booleanFld(), null, null, null, null),
        FLD_REVIEWED("reviewed", LPDatabase.booleanFld(), null, null, null, null), 
        FLD_REVIEWED_BY("reviewed_by", LPDatabase.string(), null, null, null, null), 
        FLD_REVIEWED_ON("reviewed_on", LPDatabase.dateTime(), null, null, null, null)
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
        FLD_ID("id", LPDatabase.integer(), null, null, null, null),
        FLD_SAMPLE_ID(Sample.FLD_SAMPLE_ID.getName(), LPDatabase.integer(), null, null, null, null),
        FLD_CUSTODIAN(FIELDS_NAMES_CUSTODIAN, LPDatabase.string(), null, null, null, null),
        FLD_CUSTODIAN_CANDIDATE(FIELDS_NAMES_CUSTODIAN_CANDIDATE, LPDatabase.string(), null, null, null, null),
        FLD_STARTED_ON("coc_started_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        FLD_CONFIRMED_ON(FIELDS_NAMES_COC_CONFIRMED_ON, LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        FLD_CUSTODIAN_NOTES("coc_custodian_notes", LPDatabase.string(), null, null, null, null),
        FLD_NEW_CUSTODIAN_NOTES("coc_new_custodian_notes", LPDatabase.string(), null, null, null, null),
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_SAMPLE_PICTURE("sample_picture", "json", null, null, null, null),
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
        FLD_SAMPLE_ID(Sample.FLD_SAMPLE_ID.getName(), LPDatabase.integer(), null, null, null, null),
        FLD_TESTING_GROUP("testing_group", LPDatabase.string(), null, null, null, null),
        FLD_READY_FOR_REVISION("ready_for_revision", LPDatabase.booleanFld(), null, null, null, null),
        FLD_REVIEWED("reviewed", LPDatabase.booleanFld(), null, null, null, null),
        FLD_REVISION_ON("revision_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        FLD_REVISION_BY("revision_by", LPDatabase.string(), null, null, null, null)
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
        FLD_TEST_ID("test_id", LPDatabase.integerNotNull(), null, null, null, null),
        FLD_SAMPLE_ID(Sample.FLD_SAMPLE_ID.getName(), LPDatabase.integer(), null, null, null, null),
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_STATUS_PREVIOUS(FIELDS_NAMES_STATUS_PREVIOUS, LPDatabase.string(), null, null, null, null),
        FLD_ANALYSIS(FIELDS_NAMES_ANALYSIS, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.string(), null, null, null, null),
        FLD_METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer(), null, null, null, null),
        FLD_REPLICA(FIELDS_NAMES_REPLICA, LPDatabase.integer(), null, null, null, null),
        FLD_ADDED_ON("added_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        FLD_ADDED_BY("added_by", LPDatabase.string(), null, null, null, null),
        FLD_SPEC_EVAL(FIELDS_NAMES_SPEC_EVAL,  LPDatabase.string(2), null, null, null, null),
        FLD_REVIEWER("reviewer", LPDatabase.string(), null, null, null, null),
        FLD_REVIEWER_ASSIGNED_ON("reviewer_assigned_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        FLD_REVIEWER_ASSIGNED_BY("reviewer_assigned_by", LPDatabase.string(), null, null, null, null),
        FLD_ANALYST("analyst", LPDatabase.string(), null, null, null, null),
        FLD_ANALYST_ASSIGNED_ON("analyst_assigned_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        FLD_ANALYST_ASSIGNED_BY("analyst_assigned_by", LPDatabase.string(), null, null, null, null),
        FLD_ANALYST_CERTIFICATION_MODE("analyst_certification_mode", LPDatabase.string(), null, null, null, null),
        FLD_ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        FLD_TESTING_GROUP("testing_group",  LPDatabase.string(), null, null, null, null),
        FLD_READY_FOR_REVISION("ready_for_revision", LPDatabase.booleanFld(), null, null, null, null),     
        FLD_SUBALIQUOT_ID(FIELDS_NAMES_SUBALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        FLD_REVIEWED("reviewed", LPDatabase.booleanFld(), null, null, null, null),
        FLD_REVIEWED_ON("reviewed_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        FLD_REVIEWED_BY("reviewed_by", LPDatabase.string(), null, null, null, null),
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
        FLD_RESULT_ID("result_id", LPDatabase.integer(), null, null, null, null),
        FLD_TEST_ID(SampleAnalysis.FLD_TEST_ID.getName(), LPDatabase.integer(), null, null, null, null),
        FLD_SAMPLE_ID(Sample.FLD_SAMPLE_ID.getName(), LPDatabase.integer(), null, null, null, null),
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_STATUS_PREVIOUS(FIELDS_NAMES_STATUS_PREVIOUS, LPDatabase.string(), null, null, null, null),
        FLD_ANALYSIS(FIELDS_NAMES_ANALYSIS, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.string(), null, null, null, null),
        FLD_METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer(), null, null, null, null),
        FLD_REPLICA(FIELDS_NAMES_REPLICA, LPDatabase.integer(), null, null, null, null),
        FLD_PARAM_NAME("param_name", LPDatabase.string(), null, null, null, null),
        FLD_PARAM_TYPE("param_type", LPDatabase.string(), null, null, null, null),
        FLD_MANDATORY("mandatory", LPDatabase.booleanFld(false), null, null, null, null),
        FLD_REQUIRES_LIMIT("requires_limit", LPDatabase.booleanFld(false), null, null, null, null),
        FLD_RAW_VALUE("raw_value", LPDatabase.string(), null, null, null, null),
        FLD_PRETTY_VALUE("pretty_value", LPDatabase.string(), null, null, null, null),
        FLD_ENTERED_ON("entered_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        FLD_ENTERED_BY("entered_by", LPDatabase.string(), null, null, null, null),
        FLD_REENTERED("reentered", LPDatabase.booleanFld(), null, null, null, null),
        FLD_SPEC_EVAL(FIELDS_NAMES_SPEC_EVAL, LPDatabase.string(200), null, null, null, null),
        FLD_SPEC_EVAL_DETAIL("spec_eval_detail",  LPDatabase.string(200), null, null, null, null),
        FLD_UOM("uom", LPDatabase.string(), null, null, null, null),
        FLD_UOM_CONVERSION_MODE("uom_conversion_mode", LPDatabase.string(), null, null, null, null),
        FLD_ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        FLD_SUBALIQUOT_ID(FIELDS_NAMES_SUBALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        FLD_LIMIT_ID("limit_id", LPDatabase.integer(), null, null, null, null),
        FLD_REVIEWED("reviewed", LPDatabase.booleanFld(), null, null, null, null),
        FLD_REVIEWED_ON("reviewed_on", LPDatabase.dateTime(), "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        FLD_REVIEWED_BY("reviewed_by", LPDatabase.string(), null, null, null, null),
        FLD_MAX_DP("max_dp", LPDatabase.integer(), null, null, null, null),
        FLD_MIN_ALLOWED("min_allowed", LPDatabase.real(), null, null, null, null),
        FLD_MAX_ALLOWED("max_allowed", LPDatabase.real(), null, null, null, null),
        FLD_LIST_ENTRY("list_entry", LPDatabase.string(), null, null, null, null),
        
        /* Este bloque de campos está a nivel de SampleAnalysis, es posible que pueda ser interesante tb en sample_analysis_result
        , FLD_REVIEWER("reviewer", LPDatabase.String())
        , FLD_REVIEWER_ASSIGNED_ON("reviewer_assigned_on", LPDatabase.dateTime())        
        , FLD_REVIEWER_ASSIGNED_BY("reviewer_assigned_by", LPDatabase.String())        
        , FLD_ANALYST("analyst", LPDatabase.String())
        , FLD_ANALYST_ASSIGNED_ON("analyst_assigned_on", LPDatabase.dateTime())        
        , FLD_ANALYST_ASSIGNED_BY("analyst_assigned_by", LPDatabase.String())        
        , FLD_ANALYST_CERTIFICATION_MODE("analyst_certification_mode", LPDatabase.String()) */
        //, FLD_UNDER_DEVIATION("under_deviation", LPDatabase.Boolean()) Desviaciones aún no implementadas
/*     Este bloque de campos está a nivel de Sample, es posible que pueda ser interesante tb en sample_analysis   
        , FLD_VOLUME( LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.Real())
        , FLD_VOLUME_UOM( LPDatabase.FIELDS_NAMES_VOLUME_UOM, LPDatabase.StringNotNull())
        , FLD_ALIQUOTED("aliquoted", LPDatabase.Boolean(false))
        , FLD_ALIQUOT_STATUS("aliq_status", LPDatabase.StringNotNull())
        , FLD_VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.Real())
        , FLD_VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom", LPDatabase.StringNotNull())
        , FLD_SAMPLING_DATE("sampling_date", LPDatabase.dateTime())
        , FLD_SAMPLING_COMMENT("sampling_comment", LPDatabase.String())
        , FLD_INCUBATION_START("incubation_start", LPDatabase.dateTime())
        , FLD_INCUBATION_END("incubation_end", LPDatabase.dateTime())
        , FLD_INCUBATION_PASSED("incubation_passed", LPDatabase.Boolean())*/ 
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
        FLD_ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        FLD_SAMPLE_ID(Sample.FLD_SAMPLE_ID.getName(), LPDatabase.integer(), null, null, null, null),
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        FLD_CREATED_BY("created_by", LPDatabase.string(), null, null, null, null),
        FLD_SUBALIQ_STATUS("subaliq_status", LPDatabase.string(), null, null, null, null),
        FLD_VOLUME( LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.real(), null, null, null, null),
        FLD_VOLUME_UOM( LPDatabase.FIELDS_NAMES_VOLUME_UOM, LPDatabase.string(), null, null, null, null),
        FLD_VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.real(), null, null, null, null),
        FLD_VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom", LPDatabase.string(), null, null, null, null),
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
        FLD_SUBALIQUOT_ID(FIELDS_NAMES_SUBALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        FLD_SAMPLE_ID(Sample.FLD_SAMPLE_ID.getName(), LPDatabase.integer(), null, null, null, null),
        FLD_ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        FLD_CREATED_BY("created_by", LPDatabase.string(), null, null, null, null),
        FLD_VOLUME( LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.real(), null, null, null, null),
        FLD_VOLUME_UOM( LPDatabase.FIELDS_NAMES_VOLUME_UOM, LPDatabase.string(), null, null, null, null),
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
        FLD_ID("id", LPDatabase.integer(), null, null, null, null),
        FLD_METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.string(), null, null, null, null),
        FLD_METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer(), null, null, null, null),
        FLD_USER_ID(FIELDS_NAMES_USER_ID, LPDatabase.string(), null, null, null, null),
        FLD_ASSIGNED_ON(FIELDS_NAMES_ASSIGNED_ON, LPDatabase.dateTime(), null, null, null, null),
        FLD_ASSIGNED_BY("assigned_by", LPDatabase.string(), null, null, null, null),
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_CERTIFICATION_DATE("certification_date", LPDatabase.dateTime(), null, null, null, null),
        FLD_CERTIF_EXPIRY_DATE("certif_expiry_date", LPDatabase.dateTime(), null, null, null, null),
        FLD_CERTIF_STARTED("certif_started", LPDatabase.booleanFld(), null, null, null, null),
        FLD_CERTIF_COMPLETED("certif_completed", LPDatabase.booleanFld(), null, null, null, null),
        FLD_SOP_NAME(FIELDS_NAMES_SOP_NAME, LPDatabase.string(), null, null, null, null),
        //user_name is mandatory due to its involved in the analysis method certification evaluation instead of the user_id one.
        FLD_USER_NAME(FIELDS_NAMES_USER_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_LIGHT(FIELDS_NAMES_LIGHT, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_TRAINING_ID("training_id", LPDatabase.integer(), null, null, null, null),
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
        FLD_ID("id", LPDatabase.integer(), null, null, null, null),
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_LIGHT(FIELDS_NAMES_LIGHT, LPDatabase.stringNotNull(), null, null, null, null),
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
        FLD_USER_ANALYSIS_METHOD_ID("user_analysis_method_id", LPDatabase.integer(), null, null, null, null),
        FLD_USER_ID(FIELDS_NAMES_USER_ID, LPDatabase.string(), null, null, null, null),
        FLD_ANALYSIS(FIELDS_NAMES_ANALYSIS, LPDatabase.string(), null, null, null, null),
        FLD_METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.string(), null, null, null, null),
        FLD_METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer(), null, null, null, null),
        FLD_ASSIGNED_ON(FIELDS_NAMES_ASSIGNED_ON, LPDatabase.dateTime(), null, null, null, null),
        FLD_ASSIGNED_BY("assigned_by", LPDatabase.string(), null, null, null, null),
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_MANDATORY_LEVEL(FIELDS_NAMES_MANDATORY_LEVEL, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_STARTED("started", LPDatabase.booleanFld(), null, null, null, null),
        FLD_COMPLETED("completed", LPDatabase.booleanFld(), null, null, null, null),
        FLD_CERTIF_EXPIRY_DATE("certif_expiry_date", LPDatabase.dateTime(), null, null, null, null),
        FLD_SOP_NAME(FIELDS_NAMES_SOP_NAME, LPDatabase.string(), null, null, null, null),
        FLD_USER_NAME(FIELDS_NAMES_USER_NAME, LPDatabase.string(), null, null, null, null),
        FLD_LIGHT(FIELDS_NAMES_LIGHT, LPDatabase.stringNotNull(), null, null, null, null),
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
        FLD_USER_SOP_ID("user_sop_id", LPDatabase.integer(), null, null, null, null),
        FLD_USER_ID(FIELDS_NAMES_USER_ID, LPDatabase.string(), null, null, null, null),
        FLD_SOP_ID("sop_id", LPDatabase.string(), null, null, null, null),
        FLD_SOP_LIST_ID("sop_list_id", LPDatabase.string(), null, null, null, null),
        FLD_ASSIGNED_ON(FIELDS_NAMES_ASSIGNED_ON, LPDatabase.dateTime(), null, null, null, null),
        FLD_ASSIGNED_BY("assigned_by", LPDatabase.string(), null, null, null, null),
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_MANDATORY_LEVEL(FIELDS_NAMES_MANDATORY_LEVEL, LPDatabase.string(), null, null, null, null),
        FLD_READ_STARTED("read_started", LPDatabase.booleanFld(), null, null, null, null),
        FLD_READ_COMPLETED("read_completed", LPDatabase.booleanFld(), null, null, null, null),
        FLD_UNDERSTOOD("understood", LPDatabase.booleanFld(), null, null, null, null),
        FLD_EXPIRATION_DATE(FIELDS_NAMES_EXPIRATION_DATE, LPDatabase.dateTime(), null, null, null, null),
        FLD_SOP_NAME(FIELDS_NAMES_SOP_NAME, LPDatabase.string(), null, null, null, null),
        FLD_USER_NAME(FIELDS_NAMES_USER_NAME, LPDatabase.string(), null, null, null, null),
        FLD_LIGHT(FIELDS_NAMES_LIGHT, LPDatabase.stringNotNull(), null, null, null, null),
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
        FLD_ID("id", LPDatabase.integer(), null, null, null, null),
        FLD_NAME("name", LPDatabase.string(), null, null, null, null),
        FLD_OWNER("owner", LPDatabase.string(), null, null, null, null),
        FLD_PRIVATE("private", LPDatabase.booleanFld(), null, null, null, null),
        FLD_READABLE_BY("readable_by", LPDatabase.string(), null, null, null, null),
        FLD_DEFINITION("definition", LPDatabase.string(), null, null, null, null),
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
    public enum ViewSampleCocNames{

        /**
         *
         */
        TBL("sample_coc_names", createView() +
                " SELECT smp_coc.sample_id, smp_coc.custodian, smp_coc.custodian_candidate, smp_coc.coc_started_on, smp_coc.coc_confirmed_on, smp_coc.coc_custodian_notes, "
                + "          smp_coc.coc_new_custodian_notes, smp_coc.sample_picture, smp_coc.id, smp_coc.status, usr_custodian.user_name AS custodian_name," +
                    "         usr_candidate.user_name AS candidate_name " +
                    "   FROM #SCHEMA.sample_coc smp_coc," +
                                "    #SCHEMA_APP.users usr_custodian," +
                                "    #SCHEMA_APP.users usr_candidate" +
                    "  WHERE smp_coc.custodian::text = usr_custodian.person_name::text AND smp_coc.custodian_candidate::text = usr_candidate.person_name::text; "+
                    "ALTER TABLE  #SCHEMA.#TBL  OWNER TO #OWNER;" +
                    "GRANT ALL ON TABLE  #SCHEMA.#TBL TO #OWNER;")
        ,

        /**
         *
         */
        FLD_SAMPLE_ID(Sample.FLD_SAMPLE_ID.getName(), ""),

        /**
         *
         */
        FLD_CUSTODIAN(FIELDS_NAMES_CUSTODIAN, ""),

        /**
         *
         */
        FLD_CUSTODIAN_CANDIDATE(FIELDS_NAMES_CUSTODIAN_CANDIDATE, "")
        ,

        /**
         *
         */
        FLD_COC_STARTED_ON("coc_started_on", ""),

        /**
         *
         */
        FLD_COC_CONFIRMED_ON(FIELDS_NAMES_COC_CONFIRMED_ON, ""),

        /**
         *
         */
        FLD_COC_CUSTODIAN_NOTES("coc_custodian_notes", "")
        ,

        /**
         *
         */
        FLD_NEW_CUSTODIAN_NOTES("coc_new_custodian_notes",""),

        /**
         *
         */
        FLD_SAMPLE_PICTURE("sample_picture", ""),

        /**
         *
         */
        FLD_ID("id", "")
        ,

        /**
         *
         */
        FLD_STATUS(FIELDS_NAMES_STATUS, ""),

        /**
         *
         */
        FLD_CUSTODIAN_NAME("custodian_name", ""),

        /**
         *
         */
        FLD_CANDIDATE_NAME("candidate_name", "")
        ;
        private ViewSampleCocNames(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return
         */
        public String getName(){
            return this.dbObjName;
        }
        private String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        /**
         *
         * @param schemaNamePrefix - Procedure Instance where it applies
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ViewSampleCocNames.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_APP", GlobalVariables.Schemas.APP.getName());
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ViewSampleCocNames obj: ViewSampleCocNames.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_APP", GlobalVariables.Schemas.APP.getName());
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }        
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }        
    public enum ViewUserAndMetaDataSopView{

        /**
         *
         */
        TBL("user_and_meta_data_sop_vw",  LPDatabase.createView() +
                " SELECT '#SCHEMA_CONFIG'::text AS procedure, usr.user_sop_id, usr.user_id, usr.sop_id, usr.sop_list_id, usr.assigned_on, usr.assigned_by, usr.status, usr.mandatory_level," +
                "            usr.read_started, usr.read_completed, usr.understood, usr.expiration_date, usr.sop_name, usr.user_name, usr.light, metadata.brief_summary, metadata.file_link, metadata.author " +
                "   FROM #SCHEMA.user_sop usr," +
                "    #SCHEMA_CONFIG.sop_meta_data metadata" +
                "  WHERE usr.sop_name::text = metadata.sop_name::text; "+
                    "ALTER TABLE  #SCHEMA.#TBL  OWNER TO #OWNER;" +
                    "GRANT ALL ON TABLE  #SCHEMA.#TBL TO #OWNER;")
        ,

        /**
         *
         */
        FLD_PROCEDURE("procedure", ""),

        /**
         *
         */
        FLD_USER_SOP_ID("user_sop_id", ""),

        /**
         *
         */
        FLD_USER_ID(FIELDS_NAMES_USER_ID, "")
        ,

        /**
         *
         */
        FLD_SOP_LIST_ID("sop_list_id", ""),

        /**
         *
         */
        FLD_ASSIGNED_ON(FIELDS_NAMES_ASSIGNED_ON, ""),

        /**
         *
         */
        FLD_STATUS(FIELDS_NAMES_STATUS, ""),

        /**
         *
         */
        FLD_MANDATORY_LEVEL(FIELDS_NAMES_MANDATORY_LEVEL, "")
        ,

        /**
         *
         */
        FLD_READ_STARTED("read_started",""),

        /**
         *
         */
        FLD_READ_COMPLETED("read_completed", ""),

        /**
         *
         */
        FLD_UNDERSTOOD("understood", "")
        ,

        /**
         *
         */
        FLD_EXPIRATION_DATE(FIELDS_NAMES_EXPIRATION_DATE, ""),

        /**
         *
         */
        FLD_SOP_NAME(FIELDS_NAMES_SOP_NAME, ""),

        /**
         *
         */
        FLD_USER_NAME(FIELDS_NAMES_USER_NAME, ""),

        /**
         *
         */
        FLD_LIGHT(FIELDS_NAMES_LIGHT, ""),

        /**
         *
         */
        FLD_BRIEF_SUMMARY("brief_summary", "")
        ,

        /**
         *
         */
        FLD_FILE_LINK("file_link", ""),
        FLD_AUTHOR("author", ""),
        FLD_CERTIFICATION_MODE("certification_mode", "")
        
        ;
        private ViewUserAndMetaDataSopView(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return
         */
        public String getName(){
            return this.dbObjName;
        }
        private String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        /**
         *
         * @param schemaNamePrefix - Procedure Instance where it applies
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ViewUserAndMetaDataSopView.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_CONFIG", LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ViewUserAndMetaDataSopView obj: ViewUserAndMetaDataSopView.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }        
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ViewUserAndMetaDataSopView obj: ViewUserAndMetaDataSopView.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }    
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
        
    }        

    public enum ViewUserAndAnalysisMethodCertificationView{

        /**
         *
         */
        TBL("user_and_analysis_method_certification_vw",  LPDatabase.createView() +
                " SELECT '#SCHEMA_CONFIG'::text AS procedure, usr.id, usr.user_id, metadata.code, usr.method_name, usr.method_version, "+
                "   usr.assigned_on, usr.assigned_by, usr.status, usr.certification_date, usr.certif_expiry_date," +
                "   usr.certif_started, usr.certif_completed, usr.sop_name, usr.user_name, usr.light, , usr.training_id,"+
                "   metadata.active, metadata.expires, metadata.expiry_interval_info " +
                "   FROM #SCHEMA.certif_user_analysis_method usr," +
                "    #SCHEMA_CONFIG.methods metadata" +
                "  WHERE usr.method_name::text = metadata.code::text "+
                "    and usr.method_version::integer =metadata.config_version::integer;"+
                    "ALTER TABLE  #SCHEMA.#TBL  OWNER TO #OWNER;" +
                    "GRANT ALL ON TABLE  #SCHEMA.#TBL TO #OWNER;")
        ,

        /**
         *
         */
        FLD_PROCEDURE("procedure", ""),
        FLD_ID("id", ""),
        FLD_USER_ID(FIELDS_NAMES_USER_ID, "")        ,
        FLD_METHOD_CODE("code", ""),
        FLD_METHOD_NAME("method_name", ""),
        FLD_METHOD_VERSION("method_version", ""),
        FLD_ASSIGNED_ON(FIELDS_NAMES_ASSIGNED_ON, ""),
        FLD_ASSIGNED_BY(FIELDS_NAMES_ASSIGNED_BY, ""),
        FLD_STATUS(FIELDS_NAMES_STATUS, ""),
        FLD_CERTIFICATION_DATE("certification_date", "")        ,
        FLD_CERTIF_EXPIRY_DATE("certif_expiry_date", "")        ,
        FLD_CERTIF_STARTED("certif_started",""),
        FLD_CERTIF_COMPLETED("certif_completed", ""),
        FLD_SOP_NAME(FIELDS_NAMES_SOP_NAME, ""),
        FLD_USER_NAME(FIELDS_NAMES_USER_NAME, ""),
        FLD_LIGHT(FIELDS_NAMES_LIGHT, ""),
        FLD_TRAINING_ID("training_id", "")        ,
        FLD_METHOD_IS_ACTIVE("active", "method_is_active"),
        FLD_METHOD_EXPIRES("expires", "method_expires"),
        FLD_METHOD_EXPIRY_INTERVAL_INFO("expiry_interval_info", "method_expiry_interval_info")
        ;
        private ViewUserAndAnalysisMethodCertificationView(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return
         */
        public String getName(){
            return this.dbObjName;
        }
        private String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        /**
         *
         * @param schemaNamePrefix - Procedure Instance where it applies
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ViewUserAndAnalysisMethodCertificationView.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_CONFIG", LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ViewUserAndAnalysisMethodCertificationView obj: ViewUserAndAnalysisMethodCertificationView.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }        
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ViewUserAndAnalysisMethodCertificationView obj: ViewUserAndAnalysisMethodCertificationView.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }    
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
        
    }        

    public enum ViewSampleAnalysisResultWithSpecLimits{

        /**
         *
         */
        TBL("sample_analysis_result_with_spec_limits",  LPDatabase.createView() +
                " SELECT #FLDS from #SCHEMA.sample_analysis_result sar " +
                "   INNER JOIN #SCHEMA.sample_analysis sa on sa.test_id = sar.test_id "+
                "   INNER JOIN #SCHEMA.sample s on s.sample_id = sar.sample_id "+
                "    left outer join #SCHEMA_CONFIG.spec_limits spcLim on sar.limit_id=spcLim.limit_id " +
                "    left outer join #SCHEMA_PROCEDURE.program_corrective_action pca on pca.result_id=sar.result_id " +
                "    left outer join #SCHEMA_PROCEDURE.invest_objects io on io.object_id=sar.result_id and io.object_type='sample_analysis_result' ;" +
                        
                "ALTER VIEW  #SCHEMA.#TBL  OWNER TO #OWNER;")
        ,

        /**
         *
         */
        FLD_RESULT_ID("result_id", "sar.result_id")
        ,

        /**
         *
         */
        FLD_TEST_ID(SampleAnalysis.FLD_TEST_ID.getName(), "sar.test_id")
        ,

        /**
         *
         */
        FLD_SAMPLE_ID(Sample.FLD_SAMPLE_ID.getName(), "sar.sample_id")
        ,

        /**
         *
         */
        FLD_STATUS(FIELDS_NAMES_STATUS, "sar.status")
        ,

        /**
         *
         */
        FLD_STATUS_PREVIOUS(FIELDS_NAMES_STATUS_PREVIOUS, "sar.status_previous")
        ,

        /**
         *
         */
        FLD_ANALYSIS(FIELDS_NAMES_ANALYSIS, "sar.analysis")
        ,

        /**
         *
         */
        FLD_METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, "sar.method_name")
        ,

        /**
         *
         */
        FLD_METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, "sar.method_version")
        ,

        /**
         *
         */
        FLD_REPLICA(FIELDS_NAMES_REPLICA, "sar.replica")
        ,

        /**
         *
         */
        FLD_PARAM_NAME("param_name", "sar.param_name")
        ,

        /**
         *
         */
        FLD_PARAM_TYPE("param_type", "sar.param_type")
        ,

        /**
         *
         */
        FLD_MANDATORY("mandatory", "sar.mandatory")
        ,

        /**
         *
         */
        FLD_REQUIRES_LIMIT("requires_limit", "sar.requires_limit")
        ,

        /**
         *
         */
        FLD_RAW_VALUE("raw_value", "sar.raw_value"),
        FLD_RAW_VALUE_NUM("raw_value_num", "case when isnumeric(sar.raw_value) then to_number(sar.raw_value::text, '9999'::text) else null end"),         

        /**
         *
         */
        FLD_PRETTY_VALUE("pretty_value", "sar.pretty_value")
        ,

        /**
         *
         */
        FLD_ENTERED_ON("entered_on", "sar.entered_on")
        ,

        /**
         *
         */
        FLD_ENTERED_BY("entered_by", "sar.entered_by")
        ,

        /**
         *
         */
        FLD_REENTERED("reentered", "sar.reentered")
        ,

        /**
         *
         */
        FLD_SPEC_EVAL(FIELDS_NAMES_SPEC_EVAL, "sar.spec_eval")
        ,

        /**
         *
         */
        FLD_SPEC_EVAL_DETAIL("spec_eval_detail", "sar.spec_eval_detail")
        ,        

        /**
         *
         */
        FLD_UOM("uom", "sar.uom")        
        ,        

        /**
         *
         */
        FLD_UOM_CONVERSION_MODE("uom_conversion_mode", "sar.uom_conversion_mode")        
        ,

        /**
         *
         */
        FLD_ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, "sar.aliquot_id")
        ,

        /**
         *
         */
        FLD_SUBALIQUOT_ID(FIELDS_NAMES_SUBALIQUOT_ID, "sar.subaliquot_id")
        ,        
        FLD_MAX_DP("max_dp", "sar.max_dp")        ,        
        FLD_MIN_ALLOWED("min_allowed", "sar.min_allowed")        ,        
        FLD_MAX_ALLOWED("max_allowed", "sar.max_allowed")        ,        
        FLD_LIST_ENTRY("list_entry", "sar.list_entry")        ,        

        /**
         *
         */
        FLD_SAMPLE_CONFIG_CODE("sample_config_code", "s."+TblsData.Sample.FLD_CONFIG_CODE.getName()),
        FLD_SAMPLE_STATUS("sample_status", "s.status"),
        FLD_CURRENT_STAGE("current_stage", "s.current_stage"),
        FLD_PROGRAM_NAME("program_name", "s.program_name"),
        FLD_SAMPLING_DATE("sampling_date", "s.sampling_date"),
        FLD_SHIFT("shift", "s.shift"),
        FLD_AREA("area", "s.area"),
        FLD_LOCATION_NAME("location_name", "s.location_name"),
        FLD_PRODUCTION_LOT("production_lot", "s.production_lot"),
        FLD_PROGRAM_DAY_ID("program_day_id", "s.program_day_id"),
        FLD_PROGRAM_DAY_DATE("program_day_date", "s.program_day_date"),
        FLD_SAMPLE_ANALYSIS_STATUS("sample_analysis_status", "sa.status"),
        FLD_SAMPLE_ANALYSIS_READY_FOR_REVISION("sample_analysis_"+TblsData.Sample.FLD_READY_FOR_REVISION.getName(), "sa."+TblsData.Sample.FLD_READY_FOR_REVISION.getName()),
        FLD_TESTING_GROUP("testing_group", "sa.testing_group"),
        FLD_LOGGED_ON("logged_on", "s.logged_on"),
        FLD_LIMIT_ID("limit_id", "spcLim.limit_id"),
        FLD_SAMPLER("sampler", "s.sampler"),
        FLD_SAMPLER_AREA("sampler_area", "s.sampler_area"),
        FLD_READY_FOR_REVISION(TblsData.Sample.FLD_READY_FOR_REVISION.getName(), "s."+TblsData.Sample.FLD_READY_FOR_REVISION.getName()),
        /**
         *
         */
        FLD_SPEC_CODE("spec_code", "spcLim.code")
        ,

        /**
         *
         */
        FLD_SPEC_CONFIG_VERSION("spec_config_version", "spcLim.config_version")
        ,

        /**
         *
         */
        FLD_SPEC_VARIATION_NAME("spec_variation_name", "spcLim.variation_name")
        ,            

        /**
         *
         */
        FLD_ANALYSIS_SPEC_LIMITS("analysis_spec_limits", "spcLim.analysis")            
        ,

        /**
         *
         */
        FLD_METHOD_NAME_SPEC_LIMITS("method_name_spec_limits", "spcLim.method_name")
        ,

        /**
         *
         */
        FLD_METHOD_VERSION_SPEC_LIMITS("method_version_spec_limits", "spcLim.method_version")
        ,

        /**
         *
         */
        FLD_PARAMETER("parameter", "spcLim.parameter")
        ,

        /**
         *
         */
        FLD_RULE_TYPE("rule_type", "spcLim.rule_type")
        ,

        /**
         *
         */
        FLD_RULE_VARIABLES("rule_variables", "spcLim.rule_variables")
        ,

        /**
         *
         */
        FLD_UOM_SPEC_LIMITS("uom_spec_limits", "spcLim.uom")        ,        
        FLD_UOM_CONVERSION_MODE_SPEC_LIMITS("uom_conversion_mode_spec_limits", "spcLim.uom_conversion_mode") ,
        FLD_MIN_VAL_ALLOWED("min_val_allowed", "spcLim.min_val_allowed"),
        FLD_MAX_VAL_ALLOWED("max_val_allowed", "spcLim.max_val_allowed"),
        FLD_MIN_VAL_ALLOWED_IS_STRICT("min_allowed_strict", "spcLim.min_allowed_strict"),
        FLD_MAX_VAL_ALLOWED_IS_STRICT("max_allowed_strict", "spcLim.max_allowed_strict"),        
        FLD_MIN_VAL_FOR_UNDETERMINED("min_undetermined", "spcLim.min_undetermined"),
        FLD_MAX_VAL_FOR_UNDETERMINED("max_undetermined", "spcLim.max_undetermined"),
        FLD_MIN_VAL_UNDETERMINED_IS_STRICT("min_undet_strict", "spcLim.min_undet_strict"),
        FLD_MAX_VAL_UNDETERMINED_IS_STRICT("max_undet_strict", "spcLim.max_undet_strict"),
        FLD_HAS_PREINVEST("has_pre_invest", "CASE WHEN pca.id IS NULL THEN 'NO' ELSE 'YES' END"),
        FLD_PREINVEST_ID("pre_invest_id", "pca.id"),
        FLD_HAS_INVEST("has_invest", "CASE WHEN io.id IS NULL THEN 'NO' ELSE 'YES' END"),
        FLD_INVEST_ID("invest_id", "io.invest_id"),
        FLD_INVEST_OBJECT_ID("invest_object_id", "io.id"),
        FLD_SAMPLE_REVIEWER("sample_reviewer", "s.reviewer"),
        FLD_TEST_REVIEWER("test_reviewer", "sa.reviewer"),
        ;
        private ViewSampleAnalysisResultWithSpecLimits(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return
         */
        public String getName(){
            return this.dbObjName;
        }
        private String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        /**
         *
         * @param schemaNamePrefix - Procedure Instance where it applies
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
//            tblCreateScript.append("CREATE OR REPLACE FUNCTION public.isnumeric(text)  RETURNS boolean  LANGUAGE plpgsql IMMUTABLE STRICT AS $function$ DECLARE x NUMERIC; BEGIN x = $1::NUMERIC; RETURN TRUE; EXCEPTION WHEN others THEN RETURN FALSE; END; $function$");

            String[] tblObj = ViewSampleAnalysisResultWithSpecLimits.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_CONFIG", LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_PROCEDURE", LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ViewSampleAnalysisResultWithSpecLimits obj: ViewSampleAnalysisResultWithSpecLimits.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        fieldsScript.append(currField[1]).append(" AS ").append(currField[0]);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }      
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ViewSampleAnalysisResultWithSpecLimits obj: ViewSampleAnalysisResultWithSpecLimits.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }             
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }        

    public enum ViewSampleTestingGroup{
        TBL("sample_testing_group_view",  LPDatabase.createView() +
                " SELECT #FLDS from #SCHEMA_CONFIG.sample s " +
                "   INNER JOIN #SCHEMA_CONFIG.sample_revision_testing_group stg on stg.sample_id = s.sample_id; "+

                        
                "ALTER VIEW  #SCHEMA_CONFIG.#TBL  OWNER TO #OWNER;")
        ,

        FLD_SAMPLE_ID(Sample.FLD_SAMPLE_ID.getName(), "s.sample_id")        ,
        FLD_SAMPLE_CONFIG_CODE("sample_config_code", "s."+TblsData.Sample.FLD_CONFIG_CODE.getName()),
        FLD_SAMPLE_STATUS("sample_status", "s.status"),
        FLD_CURRENT_STAGE("current_stage", "s.current_stage"),
        FLD_PROGRAM_NAME("program_name", "s.program_name"),
        FLD_SAMPLING_DATE("sampling_date", "s.sampling_date"),
        FLD_SHIFT("shift", "s.shift"),
        FLD_AREA("area", "s.area"),
        FLD_LOCATION_NAME("location_name", "s.location_name"),
        FLD_PRODUCTION_LOT("production_lot", "s.production_lot"),
        FLD_PROGRAM_DAY_ID("program_day_id", "s.program_day_id"),
        FLD_PROGRAM_DAY_DATE("program_day_date", "s.program_day_date"),
        FLD_TESTING_GROUP("testing_group", "stg.testing_group"),
        FLD_READY_FOR_REVISION("ready_for_revision", "stg.ready_for_revision")        ,
        FLD_REVIEWED("reviewed", "stg.reviewed")        ,
        FLD_REVISION_ON("revision_on", "stg.revision_on")        ,
        FLD_REVISION_BY("revision_by", "stg.revision_by")        
        ;
        private ViewSampleTestingGroup(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return
         */
        public String getName(){
            return this.dbObjName;
        }
        private String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        /**
         *
         * @param schemaNamePrefix - Procedure Instance where it applies
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
//            tblCreateScript.append("CREATE OR REPLACE FUNCTION public.isnumeric(text)  RETURNS boolean  LANGUAGE plpgsql IMMUTABLE STRICT AS $function$ DECLARE x NUMERIC; BEGIN x = $1::NUMERIC; RETURN TRUE; EXCEPTION WHEN others THEN RETURN FALSE; END; $function$");

            String[] tblObj = ViewSampleTestingGroup.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_CONFIG", LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_PROCEDURE", LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ViewSampleTestingGroup obj: ViewSampleTestingGroup.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        fieldsScript.append(currField[1]).append(" AS ").append(currField[0]);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }      
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ViewSampleTestingGroup obj: ViewSampleTestingGroup.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }             
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    } 
    
}
