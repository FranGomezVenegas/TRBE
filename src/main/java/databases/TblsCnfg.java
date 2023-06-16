package databases;

import lbplanet.utilities.LPDatabase;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ForeignkeyFld;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
import static functionaljavaa.intervals.IntervalsUtilities.DB_FLDNAME_EXPIRY_INTRVL_INFO;
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

    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.CONFIG.getName();
    public enum TablesConfig implements EnumIntTables{
        UOM(null, "units_of_measurement", SCHEMA_NAME, true, UnitsOfMeasurement.values(), 
            null, new String[]{UnitsOfMeasurement.NAME.getName()}, null, "UnitsOfMeasurement"),
        METHODS(null, "methods", SCHEMA_NAME, true, Methods.values(), null,
            new String[]{Methods.CODE.getName()}, null, "Analysis methods"),
        ANALYSIS(null, "analysis", SCHEMA_NAME, true, Analysis.values(), null,
            new String[]{Analysis.CODE.getName()}, null, "Analysis"),
        ANALYSIS_METHOD(null, "analysis_method", SCHEMA_NAME, true, AnalysisMethod.values(), null,
            new String[]{AnalysisMethod.ANALYSIS.getName(), AnalysisMethod.METHOD_NAME.getName(), AnalysisMethod.METHOD_VERSION.getName()}, 
            new Object[]{new ForeignkeyFld(AnalysisMethod.METHOD_NAME.getName(), SCHEMA_NAME, TablesConfig.METHODS.getTableName(), Methods.CODE.getName())
            }, "Analysis Method"),
        ANALYSIS_METHOD_PARAMS(null, "analysis_method_params", SCHEMA_NAME, true, AnalysisMethodParams.values(), null,
            new String[]{AnalysisMethodParams.PARAM_NAME.getName(), AnalysisMethodParams.ANALYSIS.getName(), AnalysisMethodParams.METHOD_NAME.getName(), AnalysisMethodParams.METHOD_VERSION.getName()}, 
            new Object[]{new ForeignkeyFld(AnalysisMethodParams.METHOD_NAME.getName(), SCHEMA_NAME, TablesConfig.METHODS.getTableName(), Methods.CODE.getName())}
//2022-04-04, multi-foreign key for multi tables not supported yet                //new ForeignkeyFld(AnalysisMethodParams.UOM.getName(), SCHEMA_NAME, TablesConfig.UOM.getTableName(), UnitsOfMeasurement.NAME.getName())}
            , "Analysis Method Params"),
        SAMPLE(null, "sample", SCHEMA_NAME, true, Sample.values(), null,
            new String[]{Sample.CODE.getName(), Sample.CODE_VERSION.getName()}, null, "Sample config"),
        SAMPLE_RULES(null, "sample_rules", SCHEMA_NAME, true, SampleRules.values(), null,
            new String[]{SampleRules.CODE.getName(), SampleRules.CODE_VERSION.getName()}, 
            new Object[]{new ForeignkeyFld(SampleRules.CODE.getName(), SCHEMA_NAME, TablesConfig.SAMPLE.getTableName(), Sample.CODE.getName()),
                new ForeignkeyFld(SampleRules.CODE_VERSION.getName(), SCHEMA_NAME, TablesConfig.SAMPLE.getTableName(), Sample.CODE_VERSION.getName())}
            , "Sample Rules config"),
        SOP_LIST(null, "sop_list", SCHEMA_NAME, true, SopList.values(), SopMetaData.SOP_ID.getName(),
            new String[]{SopMetaData.SOP_ID.getName()}, null, "SopList"),
        SOP_META_DATA(null, "sop_meta_data", SCHEMA_NAME, true, SopMetaData.values(), SopMetaData.SOP_ID.getName(),
            new String[]{SopMetaData.SOP_ID.getName()}, null, "SopMetaData"),
        SPEC(null, "spec", SCHEMA_NAME, true, Spec.values(), null,
            new String[]{Spec.CODE.getName()}, null, "Spec"),
        SPEC_LIMITS(null, "spec_limits", SCHEMA_NAME, true, SpecLimits.values(), SpecLimits.LIMIT_ID.getName(),
            new String[]{SpecLimits.LIMIT_ID.getName()}, 
            new Object[]{new ForeignkeyFld(SpecLimits.CODE.getName(), SCHEMA_NAME, TablesConfig.SPEC.getTableName(), Spec.CODE.getName())}, "spec_limits"),
        SPEC_RULES(null, "spec_rules", SCHEMA_NAME, true, SpecRules.values(), null,
            new String[]{SpecRules.CODE.getName()}, 
            new Object[]{new ForeignkeyFld(SpecRules.CODE.getName(), SCHEMA_NAME, TablesConfig.SPEC.getTableName(), Spec.CODE.getName())}, "spec_rules"),
        ZZZ_DB_ERROR(null, "zzz_db_error_log", SCHEMA_NAME, true, zzzDbErrorLog.values(), zzzDbErrorLog.ID.getName(),
            new String[]{zzzDbErrorLog.ID.getName()}, null, "zzzDbErrorLog"),
        ZZZ_PROPERTIES_ERROR(null, "zzz_properties_error", SCHEMA_NAME, true, zzzPropertiesMissing.values(), zzzPropertiesMissing.ID.getName(),
            new String[]{zzzPropertiesMissing.ID.getName()}, null, "zzzPropertiesMissing"),
        /*
        TBL("sop_meta_data", LPDatabase.createSequence(SOP_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#SOP_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey1 PRIMARY KEY (#SOP_ID, #SOP_VERSION, #SOP_REVISION) )" +
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
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        CONFIG_VERSION("config_version", LPDatabase.integer(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        EXPIRES("expires", LPDatabase.booleanFld(), null, null, null, null),
        EXPIRY_INTERVAL_INFO(DB_FLDNAME_EXPIRY_INTRVL_INFO, LPDatabase.string(), null, null, null, null),
        CERTIFICATION_MODE("certification_mode", LPDatabase.string(), null, null, null, null)
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
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        CONFIG_VERSION("config_version", LPDatabase.integer(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        TESTING_GROUP( LPDatabase.FIELDS_NAMES_TESTING_GROUP, LPDatabase.booleanFld(), null, null, null, null),
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
        ANALYSIS(TablesConfig.ANALYSIS.getTableName(), LPDatabase.stringNotNull(), null, null, null, null),
        METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        EXPIRY_INTERVAL_INFO(DB_FLDNAME_EXPIRY_INTRVL_INFO, LPDatabase.string(), null, null, null, null),
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
        PARAM_NAME("param_name", LPDatabase.stringNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, null, null),
        ANALYSIS(TablesConfig.ANALYSIS.getTableName(), LPDatabase.stringNotNull(), null, null, null, null),
        METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        MANDATORY("mandatory", LPDatabase.booleanFld(), null, null, null, null),
        PARAM_TYPE("param_type", LPDatabase.string(), null, null, null, null),
        NUM_REPLICAS("num_replicas", LPDatabase.integer(), null, null, null, null),
        UOM("uom", LPDatabase.string(), null, null, null, null),
        UOM_CONVERSION_MODE("uom_conversion_mode", LPDatabase.string(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        CALC_LINKED("calc_linked",  LPDatabase.string(), null, null, null, null),
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
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        CODE_VERSION("code_version", LPDatabase.integer(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        JSON_DEFINITION("json_definition", "json", null, null, null, null),
        JSON_DEFINITION_STR("json_definition_str", LPDatabase.string(), null, null, null, null),
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
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        CODE_VERSION("code_version", LPDatabase.integer(), null, null, null, null),
        STATUSES("statuses", LPDatabase.string(), null, null, null, null),
        DEFAULT_STATUS("default_status", LPDatabase.string(), null, null, null, null),
        TEST_ANALYST_REQUIRED("test_analyst_required", LPDatabase.booleanFld(), null, null, null, null),
        ANALYST_ASSIGNMENT_MODE("analyst_assignment_mode", LPDatabase.string(), null, null, null, null),
        FIELD_DEFAULT_VALUES("field_default_values", LPDatabase.string(), null, null, null, null),
        AUTO_ADD_SAMPLE_ANALYSIS_LEVEL("auto_add_sample_analysis_lvl", LPDatabase.string(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
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

    public enum SopList implements EnumIntTableFields{
        SOP_LIST_ID("sop_list_id", LPDatabase.integerNotNull(), null, null, null, null),
        SOP_ASSIGNED("sop_assigned", LPDatabase.stringNotNull(), null, null, null, null),
        // ...
        ;
        private SopList(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum SopMetaData implements EnumIntTableFields{
        SOP_ID("sop_id", LPDatabase.integerNotNull(), null, null, null, null),
        SOP_NAME("sop_name", LPDatabase.stringNotNull(), null, null, null, null),
        SOP_VERSION("sop_version", LPDatabase.integer(), null, null, null, null),
        SOP_REVISION("sop_revision", LPDatabase.integer(), null, null, null, null),
        CURRENT_STATUS("current_status", LPDatabase.string(), null, null, null, null),
        ADDED_BY("added_by", LPDatabase.string(), null, null, null, null),
        FILE_LINK("file_link", LPDatabase.string(), null, null, null, null),
        BRIEF_SUMMARY("brief_summary", LPDatabase.string(), null, null, null, null),
        EXPIRES("expires", LPDatabase.booleanFld(), null, null, null, null),
        HAS_CHILD("has_child", LPDatabase.booleanFld(), null, null, null, null),
        AUTHOR("author", LPDatabase.string(), null, null, null, null),
        EXPIRY_INTERVAL_INFO(DB_FLDNAME_EXPIRY_INTRVL_INFO, LPDatabase.string(), null, null, null, null),
        CERTIFICATION_MODE("certification_mode", LPDatabase.string(), null, null, null, null),
/*        , ACTIVE_DATE("active_date", "Date")
        , CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.String())
        , CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.Date())
        , ANALYSES("analyses", LPDatabase.String())
        , VARIATION_NAMES("variation_names", LPDatabase.String())*/
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

    public enum Spec implements EnumIntTableFields{
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        CONFIG_VERSION("config_version", LPDatabase.integer(), null, null, null, null),
        CATEGORY("category", LPDatabase.string(), null, null, null, null),
        ACTIVE_DATE("active_date", "Date", null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        ANALYSES("analyses", LPDatabase.string(), null, null, null, null),
        VARIATION_NAMES("variation_names", LPDatabase.string(), null, null, null, null),
        REPORTING_ACCEPTANCE_CRITERIA("reporting_acceptance_criteria", LPDatabase.string(), null, null, null, null),
        TESTING_SCRIPTS("testing_scripts", LPDatabase.string(), null, null, null, null),        
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
        LIMIT_ID("limit_id", LPDatabase.integer(), null, null, null, null),
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        CONFIG_VERSION("config_version", LPDatabase.integerNotNull(), null, null, null, null),
        VARIATION_NAME("variation_name", LPDatabase.string(), null, null, null, null),
        ANALYSIS("analysis", LPDatabase.stringNotNull(), null, null, null, null),
        METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.string(), null, null, null, null),
        METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer(), null, null, null, null),
        PARAMETER("parameter", LPDatabase.string(), null, null, null, null),
        RULE_TYPE("rule_type", LPDatabase.string(), null, null, null, null),
        RULE_VARIABLES("rule_variables", LPDatabase.string(), null, null, null, null),
        UOM("uom", LPDatabase.string(), null, null, null, null),
        UOM_CONVERSION_MODE("uom_conversion_mode", LPDatabase.string(), null, null, null, null),
        MIN_VAL_ALLOWED("min_val_allowed", LPDatabase.real(), null, null, null, null),
        MAX_VAL_ALLOWED("max_val_allowed", LPDatabase.real(), null, null, null, null),
        MIN_VAL_ALLOWED_IS_STRICT("min_allowed_strict", LPDatabase.booleanFld(false), null, null, null, null),
        MAX_VAL_ALLOWED_IS_STRICT("max_allowed_strict", LPDatabase.booleanFld(false), null, null, null, null),
        MIN_VAL_FOR_UNDETERMINED("min_undetermined", LPDatabase.real(), null, null, null, null),
        MAX_VAL_FOR_UNDETERMINED("max_undetermined", LPDatabase.real(), null, null, null, null),
        MIN_VAL_UNDETERMINED_IS_STRICT("min_undet_strict", LPDatabase.booleanFld(false), null, null, null, null),
        MAX_VAL_UNDETERMINED_IS_STRICT("max_undet_strict", LPDatabase.booleanFld(false), null, null, null, null),
        TESTING_GROUP("testing_group",  LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_EN("spec_text_en",  LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_RED_AREA_EN("spec_text_red_area_en",  LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_YELLOW_AREA_EN("spec_text_yellow_area_en",  LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_GREEN_AREA_EN("spec_text_green_area_en",  LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_ES("spec_text_es",  LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_RED_AREA_ES("spec_text_red_area_es",  LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_YELLOW_AREA_ES("spec_text_yellow_area_es",  LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_GREEN_AREA_ES("spec_text_green_area_es",  LPDatabase.string(), null, null, null, null),
        MAX_DP("max_dp", LPDatabase.integer(), null, null, null, null),
        LIST_ENTRY("list_entry",  LPDatabase.string(), null, null, null, null),        
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
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        CONFIG_VERSION("config_version", LPDatabase.integer(), null, null, null, null),
        ALLOW_OTHER_ANALYSIS("allow_other_analysis", LPDatabase.booleanFld(), null, null, null, null),
        ALLOW_MULTI_SPEC("allow_multi_spec", LPDatabase.booleanFld(), null, null, null, null),
        ANALYSIS_NOT_DECLARED_LEVEL("analysis_not_declared_level", LPDatabase.string(), null, null, null, null),
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
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null),
        PRETTY_NAME("pretty_name", LPDatabase.stringNotNull(), null, null, null, null),
        MEASUREMENT_FAMILY("measurement_family", LPDatabase.string(), null, null, null, null),
        IS_BASE("is_base", LPDatabase.booleanFld(), null, null, null, null),
        FACTOR_VALUE("factor_value", LPDatabase.real(), null, null, null, null),
        OFFSET_VALUE("offset_value", LPDatabase.real(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
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
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, "", null, null, null, null),
        METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, "", null, null, null, null),
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
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        CREATION_DATE("creation_date", "timestamp NOT NULL DEFAULT NOW()", null, null, null, null),
        ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null),
        QUERY("query", LPDatabase.string(), null, null, null, null),
        QUERY_PARAMETERS("query_parameters", LPDatabase.string(), null, null, null, null),
        ERROR_MESSAGE("error_message", LPDatabase.stringNotNull(), null, null, null, null),
        CLASS_CALLER("class_caller_info", LPDatabase.string(), null, null, null, null),
        RESOLVED("resolved", LPDatabase.booleanFld(false), null, null, null, null),
        RESOLUTION_NOTES("resolution_notes", LPDatabase.real(), null, null, null, null),
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
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        CREATION_DATE("creation_date", "timestamp NOT NULL DEFAULT NOW()", null, null, null, null),
        AREA("area", LPDatabase.string(), null, null, null, null),
        RULE_NAME("rule_name", LPDatabase.string(), null, null, null, null),
        PROCEDURE("procedure", LPDatabase.string(), null, null, null, null),
        ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null),
        CLASS_CALLER("class_caller_info", LPDatabase.string(), null, null, null, null),
        RESOLVED("resolved", LPDatabase.booleanFld(false), null, null, null, null),
        RESOLUTION_NOTES("resolution_notes", LPDatabase.real(), null, null, null, null),
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
