/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.methodvalidation.definition;

import databases.SqlStatementEnums.JOIN_TYPES;
import databases.TblsAppConfig;
import databases.TblsData;
import static databases.TblsData.FIELDS_NAMES_ALIQUOT_ID;
import static databases.TblsData.FIELDS_NAMES_ANALYSIS;
import static databases.TblsData.FIELDS_NAMES_REPLICA;
import static databases.TblsData.FIELDS_NAMES_SPEC_EVAL;
import static databases.TblsData.FIELDS_NAMES_STATUS;
import static databases.TblsData.FIELDS_NAMES_STATUS_PREVIOUS;
import static databases.TblsData.FIELDS_NAMES_SUBALIQUOT_ID;
import databases.TblsData.SampleAnalysisResult;
import databases.TblsData.TablesData;
import lbplanet.utilities.LPDatabase;
import module.instrumentsmanagement.definition.TblsInstrumentsData;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntTablesJoin;
import trazit.enums.EnumIntViewFields;
import trazit.enums.EnumIntViews;
import trazit.enums.FldBusinessRules;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class TblsMethodValidationData {
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesMethodValidationData implements EnumIntTables{
        SAMPLE(null, "sample", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsMethodValidationData.Sample.values(), Sample.SAMPLE_ID.getName()
            , new String[]{Sample.SAMPLE_ID.getName()}, null, "sample table"),
        SAMPLE_ANALYSIS(null, "sample_analysis", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsMethodValidationData.SampleAnalysis.values(), TblsMethodValidationData.SampleAnalysis.SAMPLE_ID.getName()
            , new String[]{TblsMethodValidationData.SampleAnalysis.SAMPLE_ID.getName()}, null, "sample table"),
        VALIDATION_METHOD_PARAMS(null, "validation_method_params", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ValidationMethodParams.values(), 
            null, new String[]{ValidationMethodParams.NAME.getName()}, null, ""),
        VALIDATION_METHOD_PARAMS_CALCS(null, "validation_method_params_calcs", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ValidationMethodParamsCalcs.values(), 
            null, new String[]{ValidationMethodParamsCalcs.VAL_PARAM_NAME.getName()}, null, ""),
        PROJECT_ATTACHMENT(null, "project_attachment", SCHEMA_NAME, true, ProjectAttachments.values(), ProjectAttachments.ID.getName(),
            new String[]{ProjectAttachments.ID.getName()}, null, "ProjectAttachments"),        
        PROJECT_NOTES(null, "project_notes", SCHEMA_NAME, true, ProjectNotes.values(), ProjectNotes.ID.getName(),
            new String[]{ProjectNotes.ID.getName()}, null, "ProjectAttachments"),        
        ;
        private TablesMethodValidationData(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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

    public enum ViewsMethodValidationData implements EnumIntViews {
        SAMPLE_AND_RESULTS_VIEW(" SELECT #FLDS from #SCHEMA.sample_analysis_result sar "
                + "   JOIN #SCHEMA.sample_analysis_result_secondentry sar2 ON sar2.result_id=sar.result_id AND sar2.test_id = sar.test_id AND sar2.sample_id = sar.sample_id"
                + "   INNER JOIN #SCHEMA.sample_analysis sa on sa.test_id = sar.test_id "
                + "   INNER JOIN #SCHEMA.sample s on s.sample_id = sar.sample_id "
                + "    left outer join #SCHEMA_CONFIG.spec_limits spcLim on sar.limit_id=spcLim.limit_id "
                + "    left outer join #SCHEMA_PROCEDURE.program_corrective_action pca on pca.result_id=sar.result_id "
                + "    left outer join #SCHEMA_PROCEDURE.invest_objects io on io.object_id=sar.result_id and io.object_type='sample_analysis_result' ;"
                + "ALTER VIEW  #SCHEMA.#TBL  OWNER TO #OWNER;",
                null, "sample_and_results", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ViewSampleAndResult.values(), "sample_and_results view",
                new EnumIntTablesJoin[]{
                    new EnumIntTablesJoin(TablesData.SAMPLE_ANALYSIS_RESULT, "sar", TablesData.SAMPLE, "s", true,
                            new EnumIntTableFields[][]{{TblsData.SampleAnalysisResult.SAMPLE_ID, TblsData.Sample.SAMPLE_ID}}, "", JOIN_TYPES.INNER),
                }, " and io.object_type='sample_analysis_result'", false
        ),
        ;

        private ViewsMethodValidationData(String viewScript, FldBusinessRules[] fldBusRules, String dbVwName, String repositoryName, Boolean isProcedure, EnumIntViewFields[] vwFlds,
                String comment, EnumIntTablesJoin[] tablesInView, String extraFilters, Boolean useFixViewScript) {
            this.getTblBusinessRules = fldBusRules;
            this.viewName = dbVwName;
            this.viewFields = vwFlds;
            this.repositoryName = repositoryName;
            this.isProcedure = isProcedure;
            this.viewComment = comment;
            this.viewScript = viewScript;
            this.tablesInTheView = tablesInView;
            this.extraFilters = extraFilters;
            this.useFixViewScript = useFixViewScript;
        }

        @Override
        public String getRepositoryName() {
            return this.repositoryName;
        }

        @Override
        public Boolean getIsProcedureInstance() {
            return this.isProcedure;
        }

        @Override
        public String getViewCreatecript() {
            return this.viewScript;
        }

        @Override
        public String getViewName() {
            return this.viewName;
        }

        @Override
        public EnumIntViewFields[] getViewFields() {
            return this.viewFields;
        }

        @Override
        public String getViewComment() {
            return this.viewComment;
        }

        @Override
        public FldBusinessRules[] getTblBusinessRules() {
            return this.getTblBusinessRules;
        }

        @Override
        public String getExtraFilters() {
            return this.extraFilters;
        }

        @Override
        public Boolean getUsesFixScriptView() {
            return this.useFixViewScript;
        }
        private final FldBusinessRules[] getTblBusinessRules;
        private final String viewName;
        private final String repositoryName;
        private final Boolean isProcedure;
        private final EnumIntViewFields[] viewFields;
        private final String viewComment;
        private final String viewScript;
        private final EnumIntTablesJoin[] tablesInTheView;
        private final String extraFilters;
        private final Boolean useFixViewScript;

        @Override
        public EnumIntTablesJoin[] getTablesRequiredInView() {
            return this.tablesInTheView;
        }
    }

    public enum Sample implements EnumIntTableFields{
        SAMPLE_ID(TblsData.Sample.SAMPLE_ID.getName(), LPDatabase.integerNotNull(), null, null, null, null, true),
        CONFIG_CODE("sample_config_code", LPDatabase.string(), null, null, null, null, true),
        CONFIG_CODE_VERSION("sample_config_code_version", LPDatabase.integer(), null, null, null, null, true),
        STATUS("status",LPDatabase.stringNotNull(), null, null, null, null, true),
        STATUS_PREVIOUS("status_previous",LPDatabase.string(), null, null, null, null, true),
        LOGGED_ON("logged_on", LPDatabase.date(), null, null, null, null, true),
        LOGGED_BY("logged_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null, true),
        RECEIVED_ON("received_on", LPDatabase.date(), null, null, null, null, true),
        RECEIVED_BY("received_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null, true),
        VOLUME(LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.real(), null, null, null, null, true),
        VOLUME_UOM(LPDatabase.FIELDS_NAMES_VOLUME_UOM,LPDatabase.string(), null, null, null, null, true),
        ALIQUOTED("aliquoted", LPDatabase.booleanFld(false), null, null, null, null, true),
        ALIQUOT_STATUS("aliq_status",LPDatabase.string(), null, null, null, null, true),
        VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.real(), null, null, null, null, true),
        VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom",LPDatabase.string(), null, null, null, null, true),
        SPEC_CODE("spec_code",LPDatabase.stringNotNull(), null, null, null, null, false),
        SPEC_CODE_VERSION("spec_code_version", LPDatabase.integer(), null, null, null, null, false),
        SPEC_VARIATION_NAME("spec_variation_name",LPDatabase.stringNotNull(), null, null, null, null, false),
        SPEC_ANALYSIS_VARIATION("spec_analysis_variation", LPDatabase.stringNotNull(), null, null, null, null, false),
        SPEC_EVAL("spec_eval",  LPDatabase.string(), null, null, null, null, true),
        CUSTODIAN("custodian",  LPDatabase.string(2), null, null, null, null, true),
        CUSTODIAN_CANDIDATE("custodian_candidate",  LPDatabase.string(2), null, null, null, null, true),
        COC_REQUESTED_ON("coc_requested_on", LPDatabase.date(), null, null, null, null, true),
        COC_CONFIRMED_ON("coc_confirmed_on", LPDatabase.date()
            , null, null, null, null, true),
        PROJECT("project", LPDatabase.stringNotNull(), null, null, null, null, true),
        PARAMETER_NAME("parameter_name",  LPDatabase.stringNotNull(), null, null, null, null, true),
        ANALYTICAL_SEQUENCE_NAME("analytical_sequence_name",  LPDatabase.stringNotNull(), null, null, null, null, true),
        
        ANALYTICAL_PARAMETER("analytical_parameter",  LPDatabase.string(), null, null, null, null, true),
        REVIEWER("reviewer",LPDatabase.string(), null, null, null, null, true),
        REVIEWED("reviewed", LPDatabase.booleanFld(), null, null, null, null, true), 
        REVIEWED_BY("reviewed_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null, true), 
        REVIEWED_ON("reviewed_on", LPDatabase.dateTime(), null, null, null, null, true),
        ;
        private Sample(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules, Boolean isSystFld){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
            this.isSystemFld=isSystFld;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;
        private final Boolean isSystemFld;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
        @Override        public Boolean isSystemField(){return this.isSystemFld;}
    }            

    public enum SampleAnalysis implements EnumIntTableFields {
        TEST_ID("test_id", LPDatabase.integerNotNull(), null, null, null, null),
        SAMPLE_ID(TblsData.Sample.SAMPLE_ID.getName(), LPDatabase.integer(), null, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull(), null, null, null, null),
        STATUS_PREVIOUS(FIELDS_NAMES_STATUS_PREVIOUS, LPDatabase.string(), null, null, null, null),
        ANALYSIS(FIELDS_NAMES_ANALYSIS, LPDatabase.stringNotNull(), null, null, null, null),
        METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.string(), null, null, null, null),
        METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer(), null, null, null, null),
        REPLICA(FIELDS_NAMES_REPLICA, LPDatabase.integer(), null, null, null, null),
        ADDED_ON("added_on", LPDatabase.dateTime(), "to_char(" + "added_on" + ",'YYYY-MM-DD HH:MI')", null, null, null),
        ADDED_BY("added_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        SPEC_EVAL(FIELDS_NAMES_SPEC_EVAL, LPDatabase.string(2), null, null, null, null),
        REVIEWER("reviewer", LPDatabase.string(), null, null, null, null),
        REVIEWER_ASSIGNED_ON("reviewer_assigned_on", LPDatabase.dateTime(), "to_char(" + "reviewer_assigned_on" + ",'YYYY-MM-DD HH:MI')", null, null, null),
        REVIEWER_ASSIGNED_BY("reviewer_assigned_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        ANALYST("analyst", LPDatabase.string(), null, null, null, null),
        ANALYST_ASSIGNED_ON("analyst_assigned_on", LPDatabase.dateTime(), "to_char(" + "analyst_assigned_on" + ",'YYYY-MM-DD HH:MI')", null, null, null),
        ANALYST_ASSIGNED_BY("analyst_assigned_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        ANALYST_CERTIFICATION_MODE("analyst_certification_mode", LPDatabase.string(), null, null, null, null),
        ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        TESTING_GROUP("testing_group", LPDatabase.string(), null, null, null, null),
        READY_FOR_REVISION("ready_for_revision", LPDatabase.booleanFld(), null, null, null, null),
        SUBALIQUOT_ID(FIELDS_NAMES_SUBALIQUOT_ID, LPDatabase.integer(), null, null, null, null),
        REVIEWED("reviewed", LPDatabase.booleanFld(), null, null, null, null),
        REVIEWED_ON("reviewed_on", LPDatabase.dateTime(), "to_char(" + "reviewed_on" + ",'YYYY-MM-DD HH:MI')", null, null, null),
        REVIEWED_BY("reviewed_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        PARSING("parsing", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        THEORETICAL_VALUE("theoretical_value", LPDatabase.integer(), null, null, null, null),
        Q_VALUE("q_value", LPDatabase.integer(), null, null, null, null)
        ;

        private SampleAnalysis(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public String getName() {
            return this.fieldName;
        }

        @Override
        public String getFieldType() {
            return this.fieldType;
        }

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }
    
    public enum ValidationMethodParams implements EnumIntTableFields{
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null, true),
        ANALYTICAL_PARAMETER("analytical_parameter", LPDatabase.stringNotNull(), null, null, null, null, true),
        PROJECT("project", LPDatabase.stringNotNull(), null, null, null, null, true),

        ANALYSIS_LIST("analysis_list", LPDatabase.string(), null, null, null, null, true),
        ADD_ANALYSIS_ON_LOG("add_analysis_on_log", LPDatabase.booleanFld(true), null, null, null, null, true),
        NUM_SAMPLES("num_samples", LPDatabase.integer(), null, null, null, null, true),
        JSON_MODEL("json_model", LPDatabase.json(), null, null, null, null, true),

        PURPOSE("purpose", LPDatabase.string(), null, null, null, null, false),
        CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null, true),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null, true),
        IS_OPEN("is_open", LPDatabase.booleanFld(true), null, null, null, null, true),
        IS_LOCKED("is_locked", LPDatabase.booleanFld(false), null, null, null, null, true),
        LOCKED_REASON(TblsInstrumentsData.Instruments.LOCKED_REASON.getName(),TblsInstrumentsData.Instruments.LOCKED_REASON.getFieldType(), null, null, null, null, true),
        RESPONSIBLE("responsible", LPDatabase.string(), null, null, null, null, true)
        ;
        private ValidationMethodParams(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules, Boolean isSystFld){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
            this.isSystemFld=isSystFld;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;
        private final Boolean isSystemFld;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
        @Override        public Boolean isSystemField(){return this.isSystemFld;}
    }        

    public enum ValidationMethodParamsCalcs implements EnumIntTableFields{
        PROJECT("project", LPDatabase.stringNotNull(), null, null, null, null, true),
        VAL_PARAM_NAME("val_param_name", LPDatabase.stringNotNull(), null, null, null, null, true),
        ANALYTICAL_PARAMETER("analytical_parameter", LPDatabase.stringNotNull(), null, null, null, null, true),
        PARAM_REPORTED_NAME("param_reported_name", LPDatabase.stringNotNull(), null, null, null, null, true),
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null, true),
        CALC_NAME("calc_name", LPDatabase.string(), null, null, null, null, true),
        DECIMAL_PLACES("decimal_places", LPDatabase.integer(), null, null, null, null, true),
        VALUE("value", LPDatabase.real(), null, null, null, null, true),
        PRETTY_VALUE("pretty_value", LPDatabase.string(), null, null, null, null, true),
        ENTERED_BY("entered_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null, true),
        ENTERED_ON("entered_on", LPDatabase.dateTime(), null, null, null, null, true),
        ;
        private ValidationMethodParamsCalcs(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules, Boolean isSystFld){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
            this.isSystemFld=isSystFld;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;
        private final Boolean isSystemFld;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
        @Override        public Boolean isSystemField(){return this.isSystemFld;}
    }        

    public enum ProjectAttachments implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        PROJECT_NAME("project_name", LPDatabase.string(), null, null, null, null),
        FORMULA_NAME("formula_name", LPDatabase.string(), null, null, null, null),
        PARAMETER_NAME("parameter_name", LPDatabase.string(), null, null, null, null),
        RD_DAILY_ENTRY_NAME("rd_daily_entry", LPDatabase.string(), null, null, null, null),
        SAMPLE_ID("sample_id", LPDatabase.integer(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        FILE_LINK("file_link", LPDatabase.string(), null, null, null, null),
        DB_FILE("db_file", LPDatabase.embeddedFile(), null, null, null, null),
        BRIEF_SUMMARY("brief_summary", LPDatabase.string(), null, null, null, null),
        REMOVED("removed", LPDatabase.booleanFld(false), null, null, null, null),
        ;
        private ProjectAttachments(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    
    public enum ProjectNotes implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        PROJECT_NAME("project_name", LPDatabase.string(), null, null, null, null),
        FORMULA_NAME("formula_name", LPDatabase.string(), null, null, null, null),
        PARAMETER_NAME("parameter_name", LPDatabase.string(), null, null, null, null),
        RD_DAILY_ENTRY_NAME("rd_daily_entry", LPDatabase.string(), null, null, null, null),
        SAMPLE_ID("sample_id", LPDatabase.integer(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        FILE_LINK("file_link", LPDatabase.string(), null, null, null, null),
        DB_FILE("db_file", LPDatabase.embeddedFile(), null, null, null, null),
        BRIEF_SUMMARY("brief_summary", LPDatabase.string(), null, null, null, null),
        REMOVED("removed", LPDatabase.booleanFld(false), null, null, null, null),
        ;
        private ProjectNotes(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum ViewSampleAndResult implements EnumIntViewFields {
        RESULT_ID("sar", SampleAnalysisResult.RESULT_ID.getName(), "sar.result_id", SampleAnalysisResult.RESULT_ID, null, null, null),
        TEST_ID("sar", SampleAnalysis.TEST_ID.getName(), "sar.test_id", SampleAnalysisResult.TEST_ID, null, null, null),
        SAMPLE_ID("sar", Sample.SAMPLE_ID.getName(), "sar.sample_id", SampleAnalysisResult.SAMPLE_ID, null, null, null),
        STATUS("sar", FIELDS_NAMES_STATUS, "sar.status", SampleAnalysisResult.STATUS, null, null, null),
        ANALYSIS("sar", FIELDS_NAMES_ANALYSIS, "sar.analysis", SampleAnalysisResult.ANALYSIS, null, null, null),
        PARAM_NAME("sar", "param_name", "sar.param_name", SampleAnalysisResult.PARAM_NAME, null, null, null),
        PARAM_TYPE("sar", "param_type", "sar.param_type", SampleAnalysisResult.PARAM_TYPE, null, null, null),
        RAW_VALUE("sar", "raw_value", "sar.raw_value", SampleAnalysisResult.RAW_VALUE, null, null, null),
        RAW_VALUE_NUM("sar", "raw_value_num", "CASE "
                + "            WHEN isnumeric(sar.raw_value::text) THEN to_number(sar.raw_value::text, '9999'::text) "
                + "            ELSE NULL::numeric END AS raw_value_num",
                SampleAnalysisResult.REPLICA, null, null, null),
        PRETTY_VALUE("sar", "pretty_value", "sar.pretty_value", SampleAnalysisResult.PRETTY_VALUE, null, null, null),
        SAMPLE_STATUS("s", "sample_status", "s.status AS sample_status ", Sample.STATUS, null, null, null),
        PROJECT("s", "project", "s.project", Sample.PROJECT, null, null, null),
        ANALYTICAL_PARAMETER("s", "analytical_parameter", "s.analytical_parameter", Sample.ANALYTICAL_PARAMETER, null, null, null),
        PARAMETER_NAME("s", "parameter_name", "s.parameter_name", Sample.PARAMETER_NAME, null, null, null),
        ANALYTICAL_SEQUENCE_NAME("s", "analytical_sequence_name", "s.analytical_sequence_name", Sample.ANALYTICAL_SEQUENCE_NAME, null, null, null),
        ;
        private ViewSampleAndResult(String tblAliasInView, String name, String vwFldAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules) {
            this.fldName = name;
            this.fldAliasInView = vwFldAliasName;
            this.fldMask = fldMask;
            this.fldComment = comment;
            this.fldBusinessRules = busRules;
            this.fldObj = fldObj;
            this.tblAliasInView = tblAliasInView;
        }
        private final String fldName;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;
        private final String tblAliasInView;

        @Override
        public String getName() {
            return fldName;
        }

        @Override
        public String getFldViewAliasName() {
            return this.fldAliasInView;
        }

        @Override
        public String getFieldMask() {
            return this.fldMask;
        }

        @Override
        public String getFieldComment() {
            return this.fldComment;
        }

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }

        @Override
        public EnumIntTableFields getTableField() {
            return this.fldObj;
        }

        @Override
        public String getTblAliasInView() {
            return this.tblAliasInView;
        }
    }
    
}
