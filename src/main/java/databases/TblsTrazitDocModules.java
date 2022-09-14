/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

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
public class TblsTrazitDocModules {
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName();
    public enum TablesTrazitDocModules implements EnumIntTables{
        PROCEDURE_INFO(null, "procedure_info", SCHEMA_NAME, true, ProcedureInfo.values(), null,
            new String[]{ProcedureInfo.NAME.getName(), ProcedureInfo.VERSION.getName()}, null, ""),
        PROCEDURE_ROLES(null, "procedure_roles", SCHEMA_NAME, true, ProcedureRoles.values(), null,
            new String[]{ProcedureRoles.PROCEDURE_NAME.getName(), ProcedureRoles.PROCEDURE_VERSION.getName(), ProcedureRoles.SCHEMA_PREFIX.getName(), ProcedureRoles.ROLE_NAME.getName()}, null, ""),
        PROCEDURE_USERS(null, "procedure_users", SCHEMA_NAME, true, ProcedureUsers.values(), null,
            new String[]{ProcedureUsers.PROCEDURE_NAME.getName(), ProcedureUsers.PROCEDURE_VERSION.getName(), ProcedureUsers.SCHEMA_PREFIX.getName(), ProcedureUsers.USER_NAME.getName()}, null, ""),
        PROCEDURE_USER_ROLE(null, "procedure_user_role", SCHEMA_NAME, true, ProcedureUserRole.values(), null,
            new String[]{ProcedureUserRole.PROCEDURE_NAME.getName(), ProcedureUserRole.PROCEDURE_VERSION.getName(), ProcedureUserRole.SCHEMA_PREFIX.getName(), ProcedureUserRole.USER_NAME.getName(), ProcedureUserRole.ROLE_NAME.getName()}, null, ""),
        PROCEDURE_SOP_META_DATA(null, "procedure_sop_meta_data", SCHEMA_NAME, true, ProcedureSopMetaData.values(), null,
            new String[]{ProcedureSopMetaData.PROCEDURE_NAME.getName(), ProcedureSopMetaData.PROCEDURE_VERSION.getName(), ProcedureSopMetaData.SCHEMA_PREFIX.getName(), ProcedureSopMetaData.SOP_ID.getName()}, null, ""),
        PROCEDURE_USER_REQUIREMENTS(null, "procedure_user_requirements", SCHEMA_NAME, true, ProcedureUserRequirements.values(), null,
            new String[]{ProcedureUserRequirements.PROCEDURE_NAME.getName(), ProcedureUserRequirements.PROCEDURE_VERSION.getName(), ProcedureUserRequirements.SCHEMA_PREFIX.getName(), ProcedureUserRequirements.ID.getName()}, null, ""),
        PROCEDURE_BUSINESS_RULES(null, "procedure_business_rules", SCHEMA_NAME, true, ProcedureBusinessRules.values(), null,
            new String[]{ProcedureBusinessRules.PROCEDURE_NAME.getName(), ProcedureBusinessRules.PROCEDURE_VERSION.getName(), ProcedureBusinessRules.INSTANCE_NAME.getName(), ProcedureBusinessRules.FILE_SUFFIX.getName(), ProcedureBusinessRules.RULE_NAME.getName()}, null, ""),
        PROCEDURE_USER_REQUIREMENTS_EVENTS(null, "procedure_user_requirements_events", SCHEMA_NAME, true, ProcedureUserRequirementsEvents.values(), null,
            new String[]{ProcedureUserRequirementsEvents.PROCEDURE_NAME.getName(), ProcedureUserRequirementsEvents.PROCEDURE_VERSION.getName(), ProcedureUserRequirementsEvents.SCHEMA_PREFIX.getName(), ProcedureUserRequirementsEvents.ID.getName()}, null, ""),
        PROCEDURE_MODULE_TABLES(null, "procedure_module_tables", SCHEMA_NAME, true, ProcedureModuleTablesAndFields.values(), null,
            new String[]{ProcedureModuleTablesAndFields.PROCEDURE_NAME.getName(), ProcedureModuleTablesAndFields.PROCEDURE_VERSION.getName(), ProcedureModuleTablesAndFields.SCHEMA_NAME.getName(), ProcedureModuleTablesAndFields.TABLE_NAME.getName()}, null, ""),
        ENDPOINTS_DECLARATION(null, "endpoints_declaration", SCHEMA_NAME, true, EndpointsDeclaration.values(), EndpointsDeclaration.ID.getName(),
            new String[]{EndpointsDeclaration.ID.getName()}, null, ""),
        ;
        private TablesTrazitDocModules(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    
    public static final String FIELDS_NAMES_SCHEMA_PREFIX="schema_prefix";
    public static final String FIELDS_NAMES_DESCRIPTION="description";
    /**
     *
     */
    public enum ProcedureInfo implements EnumIntTableFields{
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null),
        VERSION("version", LPDatabase.integerNotNull(), null, null, null, null),
        DESCRIPTION(FIELDS_NAMES_DESCRIPTION, LPDatabase.stringNotNull(), null, null, null, null),
        SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
        LABEL_EN("label_en", LPDatabase.stringNotNull(), null, null, null, null),
        LABEL_ES("label_es", LPDatabase.stringNotNull(), null, null, null, null),
        ;
        private ProcedureInfo(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    /**
     *
     */
    public enum ProcedureRoles implements EnumIntTableFields{
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        DESCRIPTION(FIELDS_NAMES_DESCRIPTION, LPDatabase.stringNotNull(), null, null, null, null),
        SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
        ROLE_NAME("role_name", LPDatabase.stringNotNull(), null, null, null, null),
        ;
        private ProcedureRoles(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    /**
     *
     */
    public enum ProcedureSopMetaData implements EnumIntTableFields{
        SOP_ID("sop_id", LPDatabase.integerNotNull(), null, null, null, null),
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
        SOP_NAME("sop_name", LPDatabase.stringNotNull(), null, null, null, null),
        SOP_VERSION("sop_version", LPDatabase.integerNotNull(), null, null, null, null),
        SOP_REVISION("sop_revision", LPDatabase.integerNotNull(), null, null, null, null),
        CURRENT_STATUS("current_status", LPDatabase.stringNotNull(), null, null, null, null),
        EXPIRES("expires", LPDatabase.booleanFld(false), null, null, null, null),
        HAS_CHILD("has_child", LPDatabase.booleanFld(false), null, null, null, null),
        FILE_LINK("file_link", LPDatabase.string(), null, null, null, null),
        BRIEF_SUMMARY("brief_summary", LPDatabase.string(), null, null, null, null),
        // ....
        ;
        private ProcedureSopMetaData(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    /**
     *
     */
    public enum ProcedureUserRequirements implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, null, null),
        CODE("code", LPDatabase.string(), null, null, null, null),
        NAME("name", LPDatabase.string(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),
        IN_SCOPE("in_scope", LPDatabase.booleanFld(), null, null, null, null),
        IN_SYSTEM("in_system", LPDatabase.booleanFld(), null, null, null, null),
        ROLES("roles", LPDatabase.string(), null, null, null, null),
        SOP_NAME("sop_name", LPDatabase.string(), null, null, null, null),
        ESIGN_REQ("esign_required", LPDatabase.booleanFld(), null, null, null, null),
        USERCONFIRM_REQ("userconfirmation_required", LPDatabase.booleanFld(), null, null, null, null),
        WIDGET("widget", LPDatabase.string(), null, null, null, null),
        WIDGET_VERSION("widget_version", LPDatabase.integer(), null, null, null, null),
        WIDGET_ACTION("widget_action", LPDatabase.string(), null, null, null, null),
        WIDGET_ACCESS_MODE("widget_access_mode", LPDatabase.string(), null, null, null, null),
        WIDGET_TYPE("widget_type", LPDatabase.string(), null, null, null, null),
        WIDGET_LABEL_EN("widget_label_en", LPDatabase.string(), null, null, null, null),
        WIDGET_LABEL_ES("widget_label_es", LPDatabase.string(), null, null, null, null),
        ROLE_NAME("role_name", LPDatabase.string(), null, null, null, null),
        MODE("mode", LPDatabase.string(), null, null, null, null),
        TYPE("type", LPDatabase.string(), null, null, null, null),        
        ;
        private ProcedureUserRequirements(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum ProcedureUserRequirementsEvents implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, null, null),
        NAME("name", LPDatabase.string(), null, null, null, null),
        ROLE_NAME("role_name", LPDatabase.string(), null, null, null, null),
        MODE("mode", LPDatabase.string(), null, null, null, null),
        TYPE("type", LPDatabase.string(), null, null, null, null),
        BRANCH_LEVEL("branch_level", LPDatabase.string(), null, null, null, null),
        LABEL_EN("label_en", LPDatabase.string(), null, null, null, null),
        LABEL_ES("label_es", LPDatabase.string(), null, null, null, null),
        SOP("sop", LPDatabase.string(), null, null, null, null),
        ESIGN_REQUIRED("esign_required", LPDatabase.booleanFld(), null, null, null, null),
        USERCONFIRM_REQUIRED("userconfirm_required", LPDatabase.booleanFld(), null, null, null, null),
        LP_FRONTEND_PAGE_NAME("lp_frontend_page_name", LPDatabase.string(), null, null, null, null),        
        // ....
        ;
        private ProcedureUserRequirementsEvents(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    /**
     *
     */
    public enum ProcedureUserRole implements EnumIntTableFields{
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
        USER_NAME("user_name", LPDatabase.stringNotNull(), null, null, null, null),
        ROLE_NAME("role_name", LPDatabase.stringNotNull(), null, null, null, null),
        // ....
        ;
        private ProcedureUserRole(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum ProcedureUsers implements EnumIntTableFields{
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        FULL_NAME("full_name", LPDatabase.stringNotNull(), null, null, null, null),
        SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
        USER_NAME("user_name", LPDatabase.stringNotNull(), null, null, null, null),
        ;
        private ProcedureUsers(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    
    /**
     *
     */
    public enum ProcedureModuleTablesAndFields implements EnumIntTableFields{
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
        SCHEMA_NAME("schema_name", LPDatabase.stringNotNull(), null, null, null, null),
        TABLE_NAME("table_name", LPDatabase.stringNotNull(), null, null, null, null),
        FIELD_NAME("field_name", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),
        ;
        private ProcedureModuleTablesAndFields(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum EndpointsDeclaration implements EnumIntTableFields{
        ID("id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_audit_id_seq'::regclass)", null, null, null, null),
        API_NAME("api_name", LPDatabase.stringNotNull(), null, null, null, null),
        API_VERSION("api_version", LPDatabase.integer(), null, null, null, null),
        ENDPOINT_NAME("endpoint_name", LPDatabase.stringNotNull(), null, null, null, null),
        ENDPOINT_VERSION("endpoint_version", LPDatabase.integer(), null, null, null, null),
        ARGUMENTS("arguments", LPDatabase.stringNotNull(), null, null, null, null),
        ARGUMENTS_ARRAY("arguments_array", LPDatabase.string(), null, null, null, null),
        CREATION_DATE("creation_date", LPDatabase.dateTimeWithDefaultNow(), null, null, null, null),
        LAST_UPDATE("last_update", LPDatabase.dateTime(), null, null, null, null),
        BRIEF_SUMMARY_EN("brief_summary_en", LPDatabase.string(), null, null, null, null),
        DOCUMENT_NAME_EN("document_name_en", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_NAME_EN("doc_chapter_name_en", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_ID_EN("doc_chapter_id_en", LPDatabase.string(), null, null, null, null),
        BRIEF_SUMMARY_ES("brief_summary_es", LPDatabase.string(), null, null, null, null),
        DOCUMENT_NAME_ES("document_name_es", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_NAME_ES("doc_chapter_name_es", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_ID_ES("doc_chapter_id_es", LPDatabase.string(), null, null, null, null),
        ;
        private EndpointsDeclaration(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum ProcedureBusinessRules implements EnumIntTableFields{
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        INSTANCE_NAME("instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_NAME("module_name", LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_VERSION("module_version", LPDatabase.integerNotNull(), null, null, null, null),
        FILE_SUFFIX("file_suffix", LPDatabase.string(), null, null, null, null),
        RULE_NAME("rule_name", LPDatabase.string(), null, null, null, null),
        RULE_VALUE("rule_value", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),
        VALUES_LIST("values_list", LPDatabase.string(), null, null, null, null),
        ALLOW_MULTI_VALUES("allow_multi_values", LPDatabase.booleanFld(), null, null, null, null),
        VALUES_SEPARATOR("values_separator", LPDatabase.string(1), null, null, null, null),
        ;
        private ProcedureBusinessRules(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
