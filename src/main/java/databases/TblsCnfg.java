package databases;

import static functionaljavaa.intervals.IntervalsUtilities.DBFIELDNAME_EXPIRY_INTERVAL_INFO;
import lbplanet.utilities.LPDatabase;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ForeignkeyFld;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class TblsCnfg {    
    public static final String  SCHEMATAG = "#SCHEMA";
    public static final String TABLETAG = "#TBL";
    public static final String OWNERTAG = "#OWNER";
    public static final String TABLESPACETAG = "#TABLESPACE";
    public static final String FIELDSTAG = "#FLDS";
         
    
/*    public static final String getTableCreationScriptFromCnfgTable(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "METHODS": return Methods.createTableScript(schemaNamePrefix, fields);
            case "ANALYSIS": return Analysis.createTableScript(schemaNamePrefix, fields);
            case "ANALYSIS_METHOD": return AnalysisMethod.createTableScript(schemaNamePrefix, fields);
            case "ANALYSIS_METHOD_PARAMS": return AnalysisMethodParams.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE": return Sample.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE_RULES": return SampleRules.createTableScript(schemaNamePrefix, fields);
            case "SOP_META_DATA": return SopMetaData.createTableScript(schemaNamePrefix, fields);
            case "SPEC": return Spec.createTableScript(schemaNamePrefix, fields);
            case "SPEC_LIMITS": return SpecLimits.createTableScript(schemaNamePrefix, fields);
            case "SPEC_RULES": return SpecRules.createTableScript(schemaNamePrefix, fields);
            case "UNITS_OF_MEASUREMENT": return UnitsOfMeasurement.createTableScript(schemaNamePrefix, fields);
            case "ANALYSIS_METHODS_VIEW": return ViewAnalysisMethodsView.createTableScript(schemaNamePrefix, fields);
            case "ZZZ_DB_ERROR_LOG": return zzzDbErrorLog.createTableScript(schemaNamePrefix, fields);
            case "ZZZ_PROPERTIES_ERROR": return zzzPropertiesMissing.createTableScript(schemaNamePrefix, fields);
            default: return "TABLE "+tableName+" NOT IN TBLSCNFG"+LPPlatform.LAB_FALSE;
        }        
    }*/
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.CONFIG.getName();
    public enum TablesConfig implements EnumIntTables{
        METHODS(null, "methods", SCHEMA_NAME, true, Methods.values(), null,
            new String[]{Methods.FLD_CODE.getName()}, null, "Analysis methods"),
        ANALYSIS(null, "analysis", SCHEMA_NAME, true, Analysis.values(), null,
            new String[]{Analysis.FLD_CODE.getName()}, null, "Analysis"),
        ANALYSIS_METHOD(null, "analysis_method", SCHEMA_NAME, true, AnalysisMethod.values(), null,
            new String[]{AnalysisMethod.FLD_ANALYSIS.getName(), AnalysisMethod.FLD_METHOD_NAME.getName(), AnalysisMethod.FLD_METHOD_VERSION.getName()}, 
            new Object[]{new ForeignkeyFld(AnalysisMethod.FLD_METHOD_NAME.getName(), SCHEMA_NAME, TablesConfig.METHODS.getTableName(), Methods.FLD_CODE.getName())
            }, "Analysis Method"),
        ANALYSIS_METHOD_PARAMS(null, "analysis_method_params", SCHEMA_NAME, true, AnalysisMethodParams.values(), null,
            new String[]{AnalysisMethodParams.FLD_PARAM_NAME.getName(), AnalysisMethodParams.FLD_ANALYSIS.getName(), AnalysisMethodParams.FLD_METHOD_NAME.getName(), AnalysisMethodParams.FLD_METHOD_VERSION.getName()}, 
            new Object[]{new ForeignkeyFld(AnalysisMethodParams.FLD_METHOD_NAME.getName(), SCHEMA_NAME, TablesConfig.ANALYSIS.getTableName(), Analysis.FLD_CODE.getName())}, "Analysis Method Params"),
        SAMPLE(null, "sample", SCHEMA_NAME, true, Sample.values(), null,
            new String[]{Sample.FLD_CODE.getName(), Sample.FLD_CODE_VERSION.getName()}, null, "Sample config"),
        SAMPLE_RULES(null, "sample_rules", SCHEMA_NAME, true, SampleRules.values(), null,
            new String[]{SampleRules.FLD_CODE.getName(), SampleRules.FLD_CODE_VERSION.getName()}, 
            new Object[]{new ForeignkeyFld(SampleRules.FLD_CODE.getName(), SCHEMA_NAME, TablesConfig.SAMPLE.getTableName(), Sample.FLD_CODE.getName()),
                new ForeignkeyFld(SampleRules.FLD_CODE_VERSION.getName(), SCHEMA_NAME, TablesConfig.SAMPLE.getTableName(), Sample.FLD_CODE_VERSION.getName())}
            , "Sample Rules config"),
        SOP_META_DATA(null, "sop_meta_data", SCHEMA_NAME, true, SopMetaData.values(), SopMetaData.FLD_SOP_ID.getName(),
            new String[]{SopMetaData.FLD_SOP_ID.getName()}, null, "SopMetaData"),
        SPEC(null, "spec", SCHEMA_NAME, true, Spec.values(), null,
            new String[]{Spec.FLD_CODE.getName()}, null, "Spec"),
        SPEC_LIMITS(null, "spec_limits", SCHEMA_NAME, true, SpecLimits.values(), SpecLimits.FLD_LIMIT_ID.getName(),
            new String[]{SpecLimits.FLD_LIMIT_ID.getName()}, 
            new Object[]{new ForeignkeyFld(SpecLimits.FLD_CODE.getName(), SCHEMA_NAME, TablesConfig.SPEC.getTableName(), Spec.FLD_CODE.getName())}, "spec_limits"),
        SPEC_RULES(null, "spec_rules", SCHEMA_NAME, true, SpecRules.values(), null,
            new String[]{SpecRules.FLD_CODE.getName()}, 
            new Object[]{new ForeignkeyFld(SpecRules.FLD_CODE.getName(), SCHEMA_NAME, TablesConfig.SPEC.getTableName(), Spec.FLD_CODE.getName())}, "spec_rules"),
        UOM(null, "units_of_measurement", SCHEMA_NAME, true, UnitsOfMeasurement.values(), UnitsOfMeasurement.FLD_NAME.getName(),
            new String[]{UnitsOfMeasurement.FLD_NAME.getName()}, null, "UnitsOfMeasurement"),
        ZZZ_DB_ERROR(null, "zzz_db_error_log", SCHEMA_NAME, true, zzzDbErrorLog.values(), zzzDbErrorLog.FLD_ID.getName(),
            new String[]{zzzDbErrorLog.FLD_ID.getName()}, null, "zzzDbErrorLog"),
        ZZZ_PROPERTIES_ERROR(null, "zzz_properties_error", SCHEMA_NAME, true, zzzPropertiesMissing.values(), zzzPropertiesMissing.FLD_ID.getName(),
            new String[]{zzzPropertiesMissing.FLD_ID.getName()}, null, "zzzPropertiesMissing"),
        /*
        TBL("sop_meta_data", LPDatabase.createSequence(FLD_SOP_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_SOP_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_SOP_ID, #FLD_SOP_VERSION, #FLD_SOP_REVISION) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")*/
        ;
        private TablesConfig(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    public enum ViewsConfig implements EnumIntTables{
        ANALYSIS_METHODS(null, "analysis_methods_view", SCHEMA_NAME, true, ViewAnalysisMethodsView.values(), null,
            null, null, "ViewAnalysisMethodsView"),        
/*        
        TBL("analysis_methods_view", " CREATE OR REPLACE VIEW #SCHEMA.#TBL AS " +
                " SELECT a.code, " +
                "    meth.method_name, " +
                "    meth.method_version " +
                "   FROM #SCHEMA.analysis a, " +
                "    #SCHEMA.analysis_method meth " +
                "  WHERE meth.analysis::text = a.code::text AND a.active = true;" +
                "ALTER TABLE  #SCHEMA.#TBL  OWNER TO #OWNER;" +
                "GRANT ALL ON TABLE  #SCHEMA.#TBL TO #OWNER;")
*/        
        ;
        private ViewsConfig(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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

    
    public enum Methods implements EnumIntTableFields{
        FLD_CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        FLD_CONFIG_VERSION("config_version", LPDatabase.integer(), null, null, null, null),
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        FLD_ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        FLD_EXPIRES("expires", LPDatabase.booleanFld(), null, null, null, null),
        FLD_EXPIRY_INTERVAL_INFO(DBFIELDNAME_EXPIRY_INTERVAL_INFO, LPDatabase.string(), null, null, null, null),
        ;
        private Methods(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum Analysis implements EnumIntTableFields{
        FLD_CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        FLD_CONFIG_VERSION("config_version", LPDatabase.integer(), null, null, null, null),
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        FLD_ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        FLD_TESTING_GROUP( LPDatabase.FIELDS_NAMES_TESTING_GROUP, LPDatabase.booleanFld(), null, null, null, null),
        ;
        private Analysis(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }

    /**
     *
     */
    public enum AnalysisMethod implements EnumIntTableFields{
        FLD_ANALYSIS(TablesConfig.ANALYSIS.getTableName(), LPDatabase.stringNotNull(), null, null, null, null),
        FLD_METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer(), null, null, null, null),
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        FLD_EXPIRY_INTERVAL_INFO(DBFIELDNAME_EXPIRY_INTERVAL_INFO, LPDatabase.string(), null, null, null, null),
        ;
        private AnalysisMethod(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }    

    /**
     *
     */
    public enum AnalysisMethodParams implements EnumIntTableFields{
        FLD_PARAM_NAME("param_name", LPDatabase.stringNotNull(), null, null, null, null),
        FLD_ANALYSIS(TablesConfig.ANALYSIS.getTableName(), LPDatabase.stringNotNull(), null, null, null, null),
        FLD_METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        FLD_METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        FLD_MANDATORY("mandatory", LPDatabase.booleanFld(), null, null, null, null),
        FLD_PARAM_TYPE("param_type", LPDatabase.string(), null, null, null, null),
        FLD_NUM_REPLICAS("num_replicas", LPDatabase.integer(), null, null, null, null),
        FLD_UOM("uom", LPDatabase.string(), null, null, null, null),
        FLD_UOM_CONVERSION_MODE("uom_conversion_mode", LPDatabase.string(), null, null, null, null),
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        ;
        private AnalysisMethodParams(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }        

    public enum Sample implements EnumIntTableFields{
        FLD_CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        FLD_CODE_VERSION("code_version", LPDatabase.integer(), null, null, null, null),
        FLD_DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        FLD_JSON_DEFINITION("json_definition", "json", null, null, null, null),
        FLD_JSON_DEFINITION_STR("json_definition_str", LPDatabase.string(), null, null, null, null),
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
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }    

    /**
     *
     */
    public enum SampleRules implements EnumIntTableFields{
        FLD_CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        FLD_CODE_VERSION("code_version", LPDatabase.integer(), null, null, null, null),
        FLD_STATUSES("statuses", LPDatabase.string(), null, null, null, null),
        FLD_DEFAULT_STATUS("default_status", LPDatabase.string(), null, null, null, null),
        FLD_TEST_ANALYST_REQUIRED("test_analyst_required", LPDatabase.booleanFld(), null, null, null, null),
        FLD_ANALYST_ASSIGNMENT_MODE("analyst_assignment_mode", LPDatabase.string(), null, null, null, null),
        FLD_FIELD_DEFAULT_VALUES("field_default_values", LPDatabase.string(), null, null, null, null),
        FLD_AUTO_ADD_SAMPLE_ANALYSIS_LEVEL("auto_add_sample_analysis_lvl", LPDatabase.string(), null, null, null, null),
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        ;
        private SampleRules(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }        

    /**
     *
     */
    public enum SopMetaData implements EnumIntTableFields{
        FLD_SOP_ID("sop_id", LPDatabase.integerNotNull(), null, null, null, null),
        FLD_SOP_NAME("sop_name", LPDatabase.stringNotNull(), null, null, null, null),
        FLD_SOP_VERSION("sop_version", LPDatabase.integer(), null, null, null, null),
        FLD_SOP_REVISION("sop_revision", LPDatabase.integer(), null, null, null, null),
        FLD_CURRENT_STATUS("current_status", LPDatabase.string(), null, null, null, null),
        FLD_ADDED_BY("added_by", LPDatabase.string(), null, null, null, null),
        FLD_FILE_LINK("file_link", LPDatabase.string(), null, null, null, null),
        FLD_BRIEF_SUMMARY("brief_summary", LPDatabase.string(), null, null, null, null),
        FLD_EXPIRES("expires", LPDatabase.booleanFld(), null, null, null, null),
        FLD_HAS_CHILD("has_child", LPDatabase.booleanFld(), null, null, null, null),
        FLD_AUTHOR("author", LPDatabase.string(), null, null, null, null),
        FLD_EXPIRY_INTERVAL_INFO(DBFIELDNAME_EXPIRY_INTERVAL_INFO, LPDatabase.string(), null, null, null, null),
        FLD_CERTIFICATION_MODE("certification_mode", LPDatabase.string(), null, null, null, null),
/*        , FLD_ACTIVE_DATE("active_date", "Date")
        , FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.String())
        , FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.Date())
        , FLD_ANALYSES("analyses", LPDatabase.String())
        , FLD_VARIATION_NAMES("variation_names", LPDatabase.String())*/
        ;
        private SopMetaData(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }                

    /**
     *
     */
    public enum Spec implements EnumIntTableFields{
        FLD_CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        FLD_CONFIG_VERSION("config_version", LPDatabase.integer(), null, null, null, null),
        FLD_CATEGORY("category", LPDatabase.string(), null, null, null, null),
        FLD_ACTIVE_DATE("active_date", "Date", null, null, null, null),
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        FLD_ANALYSES("analyses", LPDatabase.string(), null, null, null, null),
        FLD_VARIATION_NAMES("variation_names", LPDatabase.string(), null, null, null, null),
        FLD_REPORTING_ACCEPTANCE_CRITERIA("reporting_acceptance_criteria", LPDatabase.string(), null, null, null, null),
        ;
        private Spec(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }        

    public enum SpecLimits implements EnumIntTableFields{
        FLD_LIMIT_ID("limit_id", LPDatabase.integerNotNull(), null, null, null, null),
        FLD_CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        FLD_CONFIG_VERSION("config_version", LPDatabase.integerNotNull(), null, null, null, null),
        FLD_VARIATION_NAME("variation_name", LPDatabase.string(), null, null, null, null),
        FLD_ANALYSIS(TablesConfig.ANALYSIS.getTableName(), LPDatabase.stringNotNull(), null, null, null, null),
        FLD_METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.string(), null, null, null, null),
        FLD_METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer(), null, null, null, null),
        FLD_PARAMETER("parameter", LPDatabase.string(), null, null, null, null),
        FLD_RULE_TYPE("rule_type", LPDatabase.string(), null, null, null, null),
        FLD_RULE_VARIABLES("rule_variables", LPDatabase.string(), null, null, null, null),
        FLD_UOM("uom", LPDatabase.string(), null, null, null, null),
        FLD_UOM_CONVERSION_MODE("uom_conversion_mode", LPDatabase.string(), null, null, null, null),
        FLD_MIN_VAL_ALLOWED("min_val_allowed", LPDatabase.real(), null, null, null, null),
        FLD_MAX_VAL_ALLOWED("max_val_allowed", LPDatabase.real(), null, null, null, null),
        FLD_MIN_VAL_ALLOWED_IS_STRICT("min_allowed_strict", LPDatabase.booleanFld(false), null, null, null, null),
        FLD_MAX_VAL_ALLOWED_IS_STRICT("max_allowed_strict", LPDatabase.booleanFld(false), null, null, null, null),
        FLD_MIN_VAL_FOR_UNDETERMINED("min_undetermined", LPDatabase.real(), null, null, null, null),
        FLD_MAX_VAL_FOR_UNDETERMINED("max_undetermined", LPDatabase.real(), null, null, null, null),
        FLD_MIN_VAL_UNDETERMINED_IS_STRICT("min_undet_strict", LPDatabase.booleanFld(false), null, null, null, null),
        FLD_MAX_VAL_UNDETERMINED_IS_STRICT("max_undet_strict", LPDatabase.booleanFld(false), null, null, null, null),
        FLD_TESTING_GROUP("testing_group",  LPDatabase.string(), null, null, null, null),
        FLD_SPEC_TEXT_EN("spec_text_en",  LPDatabase.string(), null, null, null, null),
        FLD_SPEC_TEXT_RED_AREA_EN("spec_text_red_area_en",  LPDatabase.string(), null, null, null, null),
        FLD_SPEC_TEXT_YELLOW_AREA_EN("spec_text_yellow_area_en",  LPDatabase.string(), null, null, null, null),
        FLD_SPEC_TEXT_GREEN_AREA_EN("spec_text_green_area_en",  LPDatabase.string(), null, null, null, null),
        FLD_SPEC_TEXT_ES("spec_text_es",  LPDatabase.string(), null, null, null, null),
        FLD_SPEC_TEXT_RED_AREA_ES("spec_text_red_area_es",  LPDatabase.string(), null, null, null, null),
        FLD_SPEC_TEXT_YELLOW_AREA_ES("spec_text_yellow_area_es",  LPDatabase.string(), null, null, null, null),
        FLD_SPEC_TEXT_GREEN_AREA_ES("spec_text_green_area_es",  LPDatabase.string(), null, null, null, null),
        ;
        private SpecLimits(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }        

    /**
     *
     */
    public enum SpecRules implements EnumIntTableFields{
        FLD_CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        FLD_CONFIG_VERSION("config_version", LPDatabase.integer(), null, null, null, null),
        FLD_ALLOW_OTHER_ANALYSIS("allow_other_analysis", LPDatabase.booleanFld(), null, null, null, null),
        FLD_ALLOW_MULTI_SPEC("allow_multi_spec", LPDatabase.booleanFld(), null, null, null, null),
        FLD_ANALYSIS_NOT_DECLARED_LEVEL("analysis_not_declared_level", LPDatabase.string(), null, null, null, null),
        ;
        private SpecRules(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }                

    /**
     *
     */
    public enum UnitsOfMeasurement implements EnumIntTableFields{
        FLD_NAME("name", LPDatabase.stringNotNull(), null, null, null, null),
        FLD_PRETTY_NAME("pretty_name", LPDatabase.stringNotNull(), null, null, null, null),
        FLD_MEASUREMENT_FAMILY("measurement_family", LPDatabase.string(), null, null, null, null),
        FLD_IS_BASE("is_base", LPDatabase.booleanFld(), null, null, null, null),
        FLD_FACTOR_VALUE("factor_value", LPDatabase.real(), null, null, null, null),
        FLD_OFFSET_VALUE("offset_value", LPDatabase.real(), null, null, null, null),
        FLD_DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        ;
        private UnitsOfMeasurement(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }        

    /**
     *
     */
    public enum ViewAnalysisMethodsView implements EnumIntTableFields{
        FLD_CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        FLD_METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, "", null, null, null, null),
        FLD_METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, "", null, null, null, null),
        ;
        private ViewAnalysisMethodsView(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }        

    /**
     *
     */
    public enum zzzDbErrorLog implements EnumIntTableFields{
        FLD_ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        FLD_CREATION_DATE("creation_date", "timestamp NOT NULL DEFAULT NOW()", null, null, null, null),
        FLD_ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null),
        FLD_QUERY("query", LPDatabase.string(), null, null, null, null),
        FLD_QUERY_PARAMETERS("query_parameters", LPDatabase.string(), null, null, null, null),
        FLD_ERROR_MESSAGE("error_message", LPDatabase.stringNotNull(), null, null, null, null),
        FLD_CLASS_CALLER("class_caller_info", LPDatabase.string(), null, null, null, null),
        FLD_RESOLVED("resolved", LPDatabase.booleanFld(false), null, null, null, null),
        FLD_RESOLUTION_NOTES("resolution_notes", LPDatabase.real(), null, null, null, null),
        ;
        private zzzDbErrorLog(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }    

    public enum zzzPropertiesMissing implements EnumIntTableFields{
        FLD_ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        FLD_CREATION_DATE("creation_date", "date NOT NULL DEFAULT NOW()", null, null, null, null),
        FLD_AREA("area", LPDatabase.string(), null, null, null, null),
        FLD_RULE_NAME("rule_name", LPDatabase.string(), null, null, null, null),
        FLD_PROCEDURE("procedure", LPDatabase.string(), null, null, null, null),
        FLD_ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null),
        FLD_CLASS_CALLER("class_caller_info", LPDatabase.string(), null, null, null, null),
        FLD_RESOLVED("resolved", LPDatabase.booleanFld(false), null, null, null, null),
        FLD_RESOLUTION_NOTES("resolution_notes", LPDatabase.real(), null, null, null, null),
        ;
        private zzzPropertiesMissing(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }        
}
