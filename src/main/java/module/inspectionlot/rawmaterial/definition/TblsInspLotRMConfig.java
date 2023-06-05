/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.definition;

import databases.TblsAppConfig;
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
public class TblsInspLotRMConfig {

    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.CONFIG.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;

    public enum TablesInspLotRMConfig implements EnumIntTables {
        MATERIAL(null, "material", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Material.values(), null, new String[]{Material.NAME.getName()}, null, "Material table"),
        MATERIAL_INVENTORY_PLAN(null, "material_inventory_plan", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, MaterialInventoryPlan.values(), null, new String[]{MaterialInventoryPlan.MATERIAL.getName(), MaterialInventoryPlan.ENTRY_NAME.getName()},
                new Object[]{new ForeignkeyFld(MaterialInventoryPlan.MATERIAL.getName(), SCHEMA_NAME, MATERIAL.getTableName(), Material.NAME.getName())}, "MaterialInventoryPlan table"),
        MATERIAL_SAMPLING_PLAN(null, "material_sampling_plan", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, MaterialSamplingPlan.values(), null, new String[]{MaterialSamplingPlan.MATERIAL.getName(), MaterialSamplingPlan.ENTRY_NAME.getName()},
                new Object[]{new ForeignkeyFld(MaterialSamplingPlan.MATERIAL.getName(), SCHEMA_NAME, MATERIAL.getTableName(), Material.NAME.getName())}, "MaterialSamplingPlan table"),
        MATERIAL_CERTIFICATE(null, "material_certificate", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, MaterialCertificate.values(), null, new String[]{MaterialCertificate.MATERIAL.getName(), MaterialCertificate.CONFIG_NAME.getName()},
                new Object[]{new ForeignkeyFld(MaterialCertificate.MATERIAL.getName(), SCHEMA_NAME, MATERIAL.getTableName(), Material.NAME.getName())}, "MaterialCertificate table"),
        LOT(null, "lot", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Lot.values(), null, new String[]{Lot.CODE.getName(), Lot.CODE_VERSION.getName()}, null, "Lot table"),
        LOT_RULES(null, "lot_rules", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, LotRules.values(), null, new String[]{LotRules.CODE.getName(), LotRules.CODE_VERSION.getName()},
                new Object[]{new ForeignkeyFld(LotRules.CODE.getName(), SCHEMA_NAME, LOT.getTableName(), Lot.CODE.getName()),
                    new ForeignkeyFld(LotRules.CODE_VERSION.getName(), SCHEMA_NAME, LOT.getTableName(), Lot.CODE_VERSION.getName())}, "LotRules table"),
        LOT_DECISION_RULES(null, "lot_decision_rules", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, LotDecisionRules.values(), null, new String[]{LotDecisionRules.CODE.getName(), LotDecisionRules.CODE_VERSION.getName()}, null, "LotDecisionRules table"),
        SPEC(null, "spec", SCHEMA_NAME, true, Spec.values(), null,
                new String[]{Spec.CODE.getName()}, null, "Spec"),
        SPEC_LIMITS(null, "spec_limits", SCHEMA_NAME, true, SpecLimits.values(), SpecLimits.LIMIT_ID.getName(),
                new String[]{SpecLimits.LIMIT_ID.getName()},
                new Object[]{new ForeignkeyFld(SpecLimits.CODE.getName(), SCHEMA_NAME, TablesInspLotRMConfig.SPEC.getTableName(), Spec.CODE.getName())}, "spec_limits"),
        COA_DEFINITION(null, "coa_definition", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, CoaDefinition.values(), null, new String[]{CoaDefinition.NAME.getName()}, null, "Coa Definition table"),
        COA_HEADER_COLUMNS(null, "coa_header_columns", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, CoaHeaderColumns.values(), null, new String[]{CoaHeaderColumns.COA_NAME.getName(), CoaHeaderColumns.COL_ID.getName(), CoaHeaderColumns.ORDER_NUMBER.getName()},
                new Object[]{new ForeignkeyFld(CoaHeaderColumns.COA_NAME.getName(), SCHEMA_NAME, COA_DEFINITION.getTableName(), CoaDefinition.NAME.getName())}, "Coa Header Columns table"),
        COA_RESULTS_COLUMNS(null, "coa_results_columns", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, CoaResultsColumns.values(), null, new String[]{CoaResultsColumns.COA_NAME.getName(), CoaResultsColumns.FIELD_NAME.getName(), CoaHeaderColumns.ORDER_NUMBER.getName()},
                new Object[]{new ForeignkeyFld(CoaResultsColumns.COA_NAME.getName(), SCHEMA_NAME, COA_DEFINITION.getTableName(), CoaDefinition.NAME.getName())}, "Coa Results Columns table"),
        COA_USAGE_DECISION(null, "coa_usage_decision", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, CoaUsageDecision.values(), null, new String[]{CoaUsageDecision.COA_NAME.getName()},
                new Object[]{new ForeignkeyFld(CoaUsageDecision.COA_NAME.getName(), SCHEMA_NAME, COA_DEFINITION.getTableName(), CoaDefinition.NAME.getName())}, "Coa Usage Decision table"),
        COA_SIGNATURES(null, "coa_signatures", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, CoaSignatures.values(), null, new String[]{CoaSignatures.COA_NAME.getName(), CoaSignatures.ORDER_NUMBER.getName()},
                new Object[]{new ForeignkeyFld(CoaSignatures.COA_NAME.getName(), SCHEMA_NAME, COA_DEFINITION.getTableName(), CoaDefinition.NAME.getName())}, "Coa Usage Decision table"),;

        private TablesInspLotRMConfig(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds,
                String seqName, String[] primaryK, Object[] foreignK, String comment) {
            this.getTblBusinessRules = fldBusRules;
            this.tableName = dbTblName;
            this.tableFields = tblFlds;
            this.repositoryName = repositoryName;
            this.isProcedure = isProcedure;
            this.sequence = seqName;
            this.primarykey = primaryK;
            this.foreignkey = foreignK;
            this.tableComment = comment;
        }

        @Override
        public String getTableName() {
            return this.tableName;
        }

        @Override
        public String getTableComment() {
            return this.tableComment;
        }

        @Override
        public EnumIntTableFields[] getTableFields() {
            return this.tableFields;
        }

        @Override
        public String getRepositoryName() {
            return this.repositoryName;
        }

        @Override
        public String getSeqName() {
            return this.sequence;
        }

        @Override
        public String[] getPrimaryKey() {
            return this.primarykey;
        }

        @Override
        public Object[] getForeignKey() {
            return this.foreignkey;
        }

        @Override
        public Boolean getIsProcedureInstance() {
            return this.isProcedure;
        }

        @Override
        public FldBusinessRules[] getTblBusinessRules() {
            return this.getTblBusinessRules;
        }
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

    public enum Material implements EnumIntTableFields {
        NAME("name", LPDatabase.stringNotNull(100), null, null, null, null),
        SPEC_CODE("spec_code", LPDatabase.string(), null, null, null, null),
        SPEC_CODE_VERSION("spec_code_version", LPDatabase.integer(), null, null, null, null),
        ANALYSIS_VARIATION_NAME("analysis_variation_name", LPDatabase.string(), null, null, null, null),
        INVENTORY_MANAGEMENT("inventory_management", LPDatabase.booleanFld(), null, null, null, null),
        BULK_SAMPLING_DEFAULT_ALGORITHM("bulk_sampling_default_algorithm", LPDatabase.stringNotNull(), null, null, null, null),
        PERFORM_BULK_CONTROL("perform_bulk_control", LPDatabase.booleanFld(), null, null, null, null),
        ADD_ADHOC_BULK_ADDITION("allow_adhoc_bulk_addition", LPDatabase.booleanFld(), null, null, null, null),
        SAMPLING_ALGORITHM("sampling_algorithm", LPDatabase.stringNotNull(), null, null, null, null), // ...
        ;

        private Material(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum MaterialInventoryPlan implements EnumIntTableFields {
        MATERIAL("material", LPDatabase.stringNotNull(), null, null, null, null),
        ENTRY_TYPE("entry_type", LPDatabase.stringNotNull(), null, null, null, null),
        ENTRY_NAME("entry_name", LPDatabase.stringNotNull(), null, null, null, null),
        TRANSIT_LOCATION("transit_location", LPDatabase.string(), null, null, null, null),
        QUANTITY("quantity", LPDatabase.integer(), null, null, null, null),
        QUANTITY_UOM("quantity_uom", LPDatabase.string(), null, null, null, null),
        LABEL_ON_CREATION_PRINT("label_on_creation_print", LPDatabase.booleanFld(), null, null, null, null),
        LABEL_ON_CREATION_FORMAT("label_on_creation_format", LPDatabase.string(), null, null, null, null),
        REQUIRES_RECEPTION("requires_reception", LPDatabase.booleanFld(), null, null, null, null),
        LABEL_ON_RECEPTION_PRINT("label_on_reception_print", LPDatabase.booleanFld(), null, null, null, null),
        LABEL_ON_RECEPTION_FORMAT("label_on_reception_format", LPDatabase.string(), null, null, null, null),
        LABEL_ON_STORAGE_FORMAT("label_on_storage_format", LPDatabase.string(), null, null, null, null),;

        private MaterialInventoryPlan(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum MaterialSamplingPlan implements EnumIntTableFields {
        MATERIAL("material", LPDatabase.stringNotNull(), null, null, null, null),
        ENTRY_NAME("entry_name", LPDatabase.stringNotNull(), null, null, null, null),
        ANALYSIS_VARIATION("analysis_variation", LPDatabase.string(), null, null, null, null),
        ALGORITHM("algorithm", LPDatabase.string(), null, null, null, null),
        FIX_SAMPLES_NUM("fix_samples_num", LPDatabase.integer(), null, null, null, null),;

        private MaterialSamplingPlan(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum MaterialCertificate implements EnumIntTableFields {
        MATERIAL("material", LPDatabase.stringNotNull(), null, null, null, null),
        CONFIG_NAME("config_name", LPDatabase.stringNotNull(), null, null, null, null),
        ANALYSIS_LIST("analysis_list", LPDatabase.stringNotNull(), null, null, null, null),
        PRINTABLE("printable", LPDatabase.booleanFld(), null, null, null, null),
        PARTIAL_ANALYSIS_LIST("partial_analysis_list", LPDatabase.stringNotNull(), null, null, null, null),
        PARTIAL_PRINTABLE("partial_printable", LPDatabase.booleanFld(), null, null, null, null),
        TRACK_ACCESS("track_access", LPDatabase.booleanFld(), null, null, null, null),
        TRACK_PRINT("track_print", LPDatabase.booleanFld(), null, null, null, null),;

        private MaterialCertificate(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum Lot implements EnumIntTableFields {
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        CODE_VERSION("code_version", LPDatabase.integer(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        JSON_DEFINITION("json_definition", LPDatabase.json(), null, null, null, null),
        JSON_DEFINITION_STR("json_definition_str", LPDatabase.string(), null, null, null, null),
        CAMPO1("campo1", LPDatabase.integer(), null, null, null, null),
        CAMPO2("campo2", LPDatabase.integer(), null, null, null, null),
        CAMPO3("campo3", LPDatabase.integer(), null, null, null, null),;

        private Lot(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum LotRules implements EnumIntTableFields {
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        CODE_VERSION("code_version", LPDatabase.integer(), null, null, null, null),
        STATUSES("statuses", LPDatabase.string(), null, null, null, null),
        DEFAULT_STATUS("default_status", LPDatabase.string(), null, null, null, null), /*        TEST_ANALYST_REQUIRED("test_analyst_required", LPDatabase.booleanFld())
        ,

        ANALYST_ASSIGNMENT_MODE("analyst_assignment_mode", LPDatabase.string())
        ,

        FIELD_DEFAULT_VALUES("field_default_values", LPDatabase.string())
        ,        

        AUTO_ADD_SAMPLE_ANALYSIS_LEVEL("auto_add_sample_analysis_lvl", LPDatabase.string())        */;

        private LotRules(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum LotDecisionRules implements EnumIntTableFields {
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        CODE_VERSION("code_version", LPDatabase.integer(), null, null, null, null),
        BULK_DECISIONS_LIST("bulk_decisions_list", LPDatabase.string(), null, null, null, null),
        DECISIONS_LIST("decisions_list", LPDatabase.string(), null, null, null, null),
        MINIMUM_ANALYSIS_REQUIRED_LIST("minimum_analysis_required_list", LPDatabase.string(), null, null, null, null),
        SAMPLE_ANALYSIS_REVISION_REQUIRED("sample_analysis_revision_required", LPDatabase.booleanFld(true), null, null, null, null),
        SAMPLE_REVISION_REQUIRED("sample_revision_required", LPDatabase.booleanFld(true), null, null, null, null),
        ALLOW_AUTO_DECISION("allow_auto_decision", LPDatabase.booleanFld(false), null, null, null, null),
        ALLOW_DECISION_PARTIAL_RESULTS("allow_decision_partial_results", LPDatabase.booleanFld(false), null, null, null, null),
        ALLOW_GENERATE_COA_PARTIAL_RESULTS("allow__generate_coa_partial_results", LPDatabase.booleanFld(false), null, null, null, null), /*        TEST_ANALYST_REQUIRED("test_analyst_required", LPDatabase.booleanFld())
        ,

        ANALYST_ASSIGNMENT_MODE("analyst_assignment_mode", LPDatabase.string())
        ,

        FIELD_DEFAULT_VALUES("field_default_values", LPDatabase.string())
        ,        

        AUTO_ADD_SAMPLE_ANALYSIS_LEVEL("auto_add_sample_analysis_lvl", LPDatabase.string())        */;

        private LotDecisionRules(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum Spec implements EnumIntTableFields {
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        CONFIG_VERSION("config_version", LPDatabase.integer(), null, null, null, null),
        CATEGORY("category", LPDatabase.string(), null, null, null, null),
        ACTIVE_DATE("active_date", "Date", null, null, null, null),
        CREATED_BY(LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        CREATED_ON(LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        ANALYSES("analyses", LPDatabase.string(), null, null, null, null),
        VARIATION_NAMES("variation_names", LPDatabase.string(), null, null, null, null),
        REPORTING_ACCEPTANCE_CRITERIA("reporting_acceptance_criteria", LPDatabase.string(), null, null, null, null),
        TOTAL_SAMPLE_REQ_Q("total_sample_required_quantity", LPDatabase.realNotNull(), null, null, null, null),
        TOTAL_SAMPLE_REQ_Q_UOM("total_sample_required_quantity_uom", LPDatabase.stringNotNull(), null, null, null, null),
        TESTING_SCRIPTS("testing_scripts", LPDatabase.string(), null, null, null, null)
        ;
        private Spec(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum SpecLimits implements EnumIntTableFields {
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
        TESTING_GROUP("testing_group", LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_EN("spec_text_en", LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_RED_AREA_EN("spec_text_red_area_en", LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_YELLOW_AREA_EN("spec_text_yellow_area_en", LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_GREEN_AREA_EN("spec_text_green_area_en", LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_ES("spec_text_es", LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_RED_AREA_ES("spec_text_red_area_es", LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_YELLOW_AREA_ES("spec_text_yellow_area_es", LPDatabase.string(), null, null, null, null),
        SPEC_TEXT_GREEN_AREA_ES("spec_text_green_area_es", LPDatabase.string(), null, null, null, null),
        MAX_DP("max_dp", LPDatabase.integer(), null, null, null, null),
        LIST_ENTRY("list_entry", LPDatabase.string(), null, null, null, null),
        ADD_IN_COA("add_in_coa", LPDatabase.booleanFld(true), null, null, null, null),
        COA_ORDER("coa_order", LPDatabase.booleanFld(true), null, null, null, null);

        private SpecLimits(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum CoaDefinition implements EnumIntTableFields {
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null),
        REPORT_INFORMATION_EN("report_information_en", LPDatabase.string(), null, null, null, null),
        REPORT_INFORMATION_ES("report_information_es", LPDatabase.string(), null, null, null, null),
        PROVISIONAL_COPY_EN("provisional_copy_en", LPDatabase.string(), null, null, null, null),
        PROVISIONAL_COPY_ES("provisional_copy_es", LPDatabase.string(), null, null, null, null),
        LOGO("logo", LPDatabase.string(), null, null, null, null),
        TITLE_EN("title_en", LPDatabase.string(), null, null, null, null),
        TITLE_ES("title_es", LPDatabase.string(), null, null, null, null),
        TITLE2_EN("title2_en", LPDatabase.string(), null, null, null, null),
        TITLE2_ES("title2_es", LPDatabase.string(), null, null, null, null),;

        private CoaDefinition(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum CoaHeaderColumns implements EnumIntTableFields {
        COA_NAME("coa_name", LPDatabase.stringNotNull(), null, null, null, null),
        COL_ID("col_id", LPDatabase.integer(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, null, null),
        FIELD_NAME("field_name", LPDatabase.string(), null, null, null, null),
        LABEL_EN("label_en", LPDatabase.string(), null, null, null, null),
        LABEL_ES("label_es", LPDatabase.string(), null, null, null, null),
        FIELD2_NAME("field2_name", LPDatabase.string(), null, null, null, null);

        private CoaHeaderColumns(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum CoaResultsColumns implements EnumIntTableFields {
        COA_NAME("coa_name", LPDatabase.stringNotNull(), null, null, null, null),
        FIELD_NAME("field_name", LPDatabase.string(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, null, null),
        LABEL_EN("label_en", LPDatabase.string(), null, null, null, null),
        LABEL_ES("label_es", LPDatabase.string(), null, null, null, null);

        private CoaResultsColumns(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum CoaUsageDecision implements EnumIntTableFields {
        COA_NAME("coa_name", LPDatabase.stringNotNull(), null, null, null, null),
        LABEL_EN("label_en", LPDatabase.string(), null, null, null, null),
        LABEL_ES("label_es", LPDatabase.string(), null, null, null, null),
        LABEL_WHEN_NO_DECISION_EN("label_when_no_decision_en", LPDatabase.string(), null, null, null, null),
        LABEL_WHEN_NO_DECISION_ES("label_when_no_decision_es", LPDatabase.string(), null, null, null, null),
        VALUE_ACCEPTED_EN("value_accepted_en", LPDatabase.string(), null, null, null, null),
        VALUE_ACCEPTED_ES("value_accepted_es", LPDatabase.string(), null, null, null, null),
        VALUE_REJECTED_EN("value_rejected_en", LPDatabase.string(), null, null, null, null),
        VALUE_REJECTED_ES("value_rejected_es", LPDatabase.string(), null, null, null, null);

        private CoaUsageDecision(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum CoaSignatures implements EnumIntTableFields {
        COA_NAME("coa_name", LPDatabase.stringNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, null, null),
        SIGN_LEVEL("sign_level", LPDatabase.string(), null, null, null, null),
        TITLE_EN("label_en", LPDatabase.string(), null, null, null, null),
        TITLE_ES("label_es", LPDatabase.string(), null, null, null, null),
        LABEL_WHEN_NOT_SIGNED_EN("label_when_not_signed_en", LPDatabase.string(), null, null, null, null),
        LABEL_WHEN_NOT_SIGNED_ES("label_when_not_signed_es", LPDatabase.string(), null, null, null, null),
        MANUAL_SIGN("manual_sign", LPDatabase.booleanFld(false), null, null, null, null),
        SIGN_ELECTRONICALLY_EN("sign_electronically_en", LPDatabase.string(), null, null, null, null),
        SIGN_ELECTRONICALLY_ES("sign_electronically_es", LPDatabase.string(), null, null, null, null),
        AUTHOR_EN("author_en", LPDatabase.string(), null, null, null, null),
        AUTHOR_ES("author_es", LPDatabase.string(), null, null, null, null),
        DATE_EN("date_en", LPDatabase.string(), null, null, null, null),
        DATE_ES("date_es", LPDatabase.string(), null, null, null, null);

        private CoaSignatures(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

}
