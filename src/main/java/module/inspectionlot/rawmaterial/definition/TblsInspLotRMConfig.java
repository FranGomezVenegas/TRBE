/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.definition;

import lbplanet.utilities.LPDatabase;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class TblsInspLotRMConfig {

    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.CONFIG.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;

    public static class TablesInspLotRMConig {

        public TablesInspLotRMConig() {
        }
    }
    public enum TablesInspLotRMConfig implements EnumIntTables{        
        MATERIAL(null, "material", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Material.values(), null, new String[]{Material.NAME.getName()}, null, "Material table"),
        MATERIAL_INVENTORY_PLAN(null, "material_inventory_plan", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, MaterialInventoryPlan.values(), null, new String[]{MaterialInventoryPlan.MATERIAL.getName(), MaterialInventoryPlan.ENTRY_NAME.getName()}, null, "MaterialInventoryPlan table"),
        MATERIAL_SAMPLING_PLAN(null, "material_sampling_plan", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, MaterialSamplingPlan.values(), null, new String[]{MaterialSamplingPlan.MATERIAL.getName(), MaterialSamplingPlan.ENTRY_NAME.getName()}, null, "MaterialSamplingPlan table"),
        MATERIAL_CERTIFICATE(null, "material_certificate", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, MaterialCertificate.values(), null, new String[]{MaterialCertificate.MATERIAL.getName(), MaterialCertificate.CONFIG_NAME.getName()}, null, "MaterialCertificate table"),

        LOT(null, "lot", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Lot.values(), null, new String[]{Lot.CODE.getName(), Lot.CODE_VERSION.getName()}, null, "Lot table"),
        LOT_RULES(null, "lot_rules", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, LotRules.values(), null, new String[]{LotRules.CODE.getName(), LotRules.CODE_VERSION.getName()}, null, "LotRules table"),
        LOT_DECISION_RULES(null, "lot_decision_rules", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, LotDecisionRules.values(), null, new String[]{LotDecisionRules.CODE.getName(), LotDecisionRules.CODE_VERSION.getName()}, null, "LotDecisionRules table"),
        ;
        private TablesInspLotRMConfig(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    public enum Material implements EnumIntTableFields{
        NAME("name",  LPDatabase.stringNotNull(100), null, null, null, null),
        SPEC_CODE("spec_code", LPDatabase.string(), null, null, null, null),
        SPEC_CODE_VERSION("spec_code_version", LPDatabase.integer(), null, null, null, null),
        INVENTORY_MANAGEMENT("inventory_management", LPDatabase.booleanFld(), null, null, null, null),
        // ...
        
        ;
        private Material(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum MaterialInventoryPlan implements EnumIntTableFields{
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
        LABEL_ON_STORAGE_FORMAT("label_on_storage_format", LPDatabase.string(), null, null, null, null),
        ;
        private MaterialInventoryPlan(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum MaterialSamplingPlan implements EnumIntTableFields{
        MATERIAL("material", LPDatabase.stringNotNull(), null, null, null, null),
        ENTRY_NAME("entry_name", LPDatabase.stringNotNull(), null, null, null, null),
        ANALYSIS_VARIATION("analysis_variation", LPDatabase.string(), null, null, null, null),
        ALGORITHM("algorithm", LPDatabase.string(), null, null, null, null),
        FIX_SAMPLES_NUM("fix_samples_num", LPDatabase.integer(), null, null, null, null),
        ;
        private MaterialSamplingPlan(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum MaterialCertificate implements EnumIntTableFields{
        MATERIAL("material", LPDatabase.stringNotNull(), null, null, null, null),
        CONFIG_NAME("config_name", LPDatabase.stringNotNull(), null, null, null, null),
        ANALYSIS_LIST("analysis_list", LPDatabase.stringNotNull(), null, null, null, null),
        PRINTABLE("printable", LPDatabase.booleanFld(), null, null, null, null),
        PARTIAL_ANALYSIS_LIST("partial_analysis_list", LPDatabase.stringNotNull(), null, null, null, null),
        PARTIAL_PRINTABLE("partial_printable", LPDatabase.booleanFld(), null, null, null, null),
        TRACK_ACCESS("track_access", LPDatabase.booleanFld(), null, null, null, null),
        TRACK_PRINT("track_print", LPDatabase.booleanFld(), null, null, null, null),
        ;
        private MaterialCertificate(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum Lot implements EnumIntTableFields{
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        CODE_VERSION("code_version", LPDatabase.integer(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        JSON_DEFINITION("json_definition", LPDatabase.json(), null, null, null, null),
        JSON_DEFINITION_STR("json_definition_str", LPDatabase.string(), null, null, null, null),
        CAMPO1("campo1", LPDatabase.integer(), null, null, null, null),
        CAMPO2("campo2", LPDatabase.integer(), null, null, null, null),
        CAMPO3("campo3", LPDatabase.integer(), null, null, null, null),
        ;
        private Lot(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum LotRules implements EnumIntTableFields{
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        CODE_VERSION("code_version", LPDatabase.integer(), null, null, null, null),
        STATUSES("statuses", LPDatabase.string(), null, null, null, null),
        DEFAULT_STATUS("default_status", LPDatabase.string(), null, null, null, null),
/*        TEST_ANALYST_REQUIRED("test_analyst_required", LPDatabase.booleanFld())
        ,

        ANALYST_ASSIGNMENT_MODE("analyst_assignment_mode", LPDatabase.string())
        ,

        FIELD_DEFAULT_VALUES("field_default_values", LPDatabase.string())
        ,        

        AUTO_ADD_SAMPLE_ANALYSIS_LEVEL("auto_add_sample_analysis_lvl", LPDatabase.string())        */
        ;
        private LotRules(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum LotDecisionRules implements EnumIntTableFields{
        CODE("code", LPDatabase.stringNotNull(), null, null, null, null),
        CODE_VERSION("code_version", LPDatabase.integer(), null, null, null, null),
        DECISIONS_LIST("decisions_list", LPDatabase.string(), null, null, null, null),
        MINIMUM_ANALYSIS_REQUIRED_LIST("minimum_analysis_required_list", LPDatabase.string(), null, null, null, null),
        SAMPLE_ANALYSIS_REVISION_REQUIRED("sample_analysis_revision_required", LPDatabase.booleanFld(true), null, null, null, null),
        SAMPLE_REVISION_REQUIRED("sample_revision_required", LPDatabase.booleanFld(true), null, null, null, null),
        ALLOW_AUTO_DECISION("allow_auto_decision", LPDatabase.booleanFld(false), null, null, null, null),
        ALLOW_DECISION_PARTIAL_RESULTS("allow_decision_partial_results", LPDatabase.booleanFld(false), null, null, null, null),
        ALLOW_GENERATE_COA_PARTIAL_RESULTS("allow__generate_coa_partial_results", LPDatabase.booleanFld(false), null, null, null, null),
/*        TEST_ANALYST_REQUIRED("test_analyst_required", LPDatabase.booleanFld())
        ,

        ANALYST_ASSIGNMENT_MODE("analyst_assignment_mode", LPDatabase.string())
        ,

        FIELD_DEFAULT_VALUES("field_default_values", LPDatabase.string())
        ,        

        AUTO_ADD_SAMPLE_ANALYSIS_LEVEL("auto_add_sample_analysis_lvl", LPDatabase.string())        */
        ;
        private LotDecisionRules(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

}
