/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import com.labplanet.servicios.app.GlobalAPIsParams;
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
public class TblsReqs {
    public static final String FIELDS_NAMES_SCHEMA_PREFIX="schema_prefix";
    public static final String FIELDS_NAMES_DESCRIPTION="description";
    public static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.REQUIREMENTS.getName();
    public enum TablesReqs implements EnumIntTables{
        PROCEDURE_INFO(null, "procedure_info", SCHEMA_NAME, false, ProcedureInfo.values(), ProcedureInfo.PROCEDURE_NAME.getName()+"_"+ProcedureInfo.PROCEDURE_VERSION.getName()+"_"+ProcedureInfo.PROC_INSTANCE_NAME.getName(),
            new String[]{ProcedureInfo.PROCEDURE_NAME.getName(), ProcedureInfo.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName()}, null, "This table provides the general info about the process instances"),
        PROCEDURE_ROLES(null, "procedure_roles", SCHEMA_NAME, false, ProcedureRoles.values(), null,
            new String[]{ProcedureRoles.PROCEDURE_NAME.getName(), ProcedureRoles.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureRoles.ROLE_NAME.getName()},
            new Object[]{new ForeignkeyFld(ProcedureRoles.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                new ForeignkeyFld(ProcedureRoles.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                new ForeignkeyFld(ProcedureRoles.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
            },
            "Roles for a given process instance"),
        PROC_USERS(null, "procedure_users", SCHEMA_NAME, false, ProcedureUsers.values(), 
            ProcedureUsers.PROCEDURE_NAME.getName()+"_"+ProcedureUsers.PROCEDURE_VERSION.getName()+"_"+ProcedureUsers.USER_NAME.getName(),
            new String[]{ProcedureUsers.PROCEDURE_NAME.getName(), ProcedureUsers.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureUsers.USER_NAME.getName()},
            new Object[]{new ForeignkeyFld(ProcedureUsers.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                new ForeignkeyFld(ProcedureUsers.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                new ForeignkeyFld(ProcedureUsers.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
            },
            "Users for a given process instance"),
        PROC_USER_ROLES(null, "procedure_user_role", SCHEMA_NAME, false, ProcedureUserRoles.values(), 
            ProcedureUserRoles.PROCEDURE_NAME.getName()+"_"+ProcedureUserRoles.PROCEDURE_VERSION.getName()+"_"+ProcedureUserRoles.ROLE_NAME.getName()+"_"+ProcedureUserRoles.USER_NAME.getName(),
            new String[]{ProcedureUserRoles.PROCEDURE_NAME.getName(), ProcedureUserRoles.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureUserRoles.ROLE_NAME.getName(), ProcedureUserRoles.USER_NAME.getName()},
            new Object[]{new ForeignkeyFld(ProcedureUserRoles.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                new ForeignkeyFld(ProcedureUserRoles.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                new ForeignkeyFld(ProcedureUserRoles.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
            },
            "User Roles for a given process instance"),
        PROCEDURE_SOP_META_DATA(null, "procedure_sop_meta_data", SCHEMA_NAME, false, ProcedureSopMetaData.values(), 
            ProcedureSopMetaData.PROCEDURE_NAME.getName()+"_"+ProcedureSopMetaData.PROCEDURE_VERSION.getName()+"_"+ProcedureSopMetaData.SOP_ID.getName(),
            new String[]{ProcedureSopMetaData.PROCEDURE_NAME.getName(), ProcedureSopMetaData.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureSopMetaData.SOP_ID.getName()},
            new Object[]{new ForeignkeyFld(ProcedureSopMetaData.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                new ForeignkeyFld(ProcedureSopMetaData.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                new ForeignkeyFld(ProcedureSopMetaData.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
            },
            "SOPs for a given process instance"),
        PROCEDURE_USER_REQS(null, "procedure_user_requirements", SCHEMA_NAME, false, ProcedureUserRequirements.values(), 
            ProcedureUserRequirements.PROCEDURE_NAME.getName()+"_"+ProcedureUserRequirements.PROCEDURE_VERSION.getName()+"_"+ProcedureUserRequirements.ID.getName(),
            new String[]{ProcedureUserRequirements.PROCEDURE_NAME.getName(), ProcedureUserRequirements.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureUserRequirements.ID.getName()},
            new Object[]{new ForeignkeyFld(ProcedureUserRequirements.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                new ForeignkeyFld(ProcedureUserRequirements.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                new ForeignkeyFld(ProcedureUserRequirements.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
            },
            "URS for a given process instance"),
        PROCEDURE_USER_REQS_EVENTS(null, "procedure_user_requirements_events", SCHEMA_NAME, false, ProcedureUserRequirementsEvents.values(), 
            ProcedureUserRequirementsEvents.PROCEDURE_NAME.getName()+"_"+ProcedureUserRequirementsEvents.PROCEDURE_VERSION.getName()+"_"+ProcedureUserRequirementsEvents.ID.getName(),
            new String[]{ProcedureUserRequirementsEvents.PROCEDURE_NAME.getName(), ProcedureUserRequirementsEvents.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureUserRequirementsEvents.ID.getName()},
            new Object[]{new ForeignkeyFld(ProcedureUserRequirementsEvents.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                new ForeignkeyFld(ProcedureUserRequirementsEvents.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                new ForeignkeyFld(ProcedureUserRequirementsEvents.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
            },
            "URS events for a given process instance"),
        PROC_MODULE_TABLES(null, "procedure_module_tables", SCHEMA_NAME, false, ProcedureModuleTables.values(), null,
            new String[]{ProcedureModuleTables.PROCEDURE_NAME.getName(), ProcedureModuleTables.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureModuleTables.SCHEMA_NAME.getName(), ProcedureModuleTables.TABLE_NAME.getName()},
            new Object[]{new ForeignkeyFld(ProcedureModuleTables.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                new ForeignkeyFld(ProcedureModuleTables.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                new ForeignkeyFld(ProcedureModuleTables.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
            },
            "Roles for a given process instance"),
        PROC_BUS_RULES(null, "procedure_business_rules", SCHEMA_NAME, false, ProcedureBusinessRules.values(), null,
            new String[]{ProcedureBusinessRules.PROCEDURE_NAME.getName(), ProcedureBusinessRules.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureBusinessRules.FILE_SUFFIX.getName(), ProcedureBusinessRules.RULE_NAME.getName()},
            new Object[]{new ForeignkeyFld(ProcedureBusinessRules.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                new ForeignkeyFld(ProcedureBusinessRules.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                new ForeignkeyFld(ProcedureBusinessRules.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())},
            "Roles for a given process instance"),
        PROC_MASTER_DATA(null, "procedure_master_data", SCHEMA_NAME, false, ProcedureMasterData.values(), null,
            new String[]{ProcedureMasterData.PROCEDURE_NAME.getName(), ProcedureMasterData.PROCEDURE_VERSION.getName(), ProcedureFEModel.PROC_INSTANCE_NAME.getName(), ProcedureMasterData.OBJECT_TYPE.getName()},
            new Object[]{new ForeignkeyFld(ProcedureMasterData.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                new ForeignkeyFld(ProcedureMasterData.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                new ForeignkeyFld(ProcedureMasterData.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
            },
            "Master Data for a given process instance"),
        PROC_FE_MODEL(null, "fe_proc_model", SCHEMA_NAME, false, ProcedureFEModel.values(), null,
            new String[]{ProcedureFEModel.PROCEDURE_NAME.getName(), ProcedureFEModel.PROCEDURE_VERSION.getName(), ProcedureFEModel.PROC_INSTANCE_NAME.getName()},
            new Object[]{new ForeignkeyFld(ProcedureFEModel.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                new ForeignkeyFld(ProcedureFEModel.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                new ForeignkeyFld(ProcedureFEModel.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())},
            "Frontend model definition for a given process instance"),
        JAVA_CLASS_DOC(null, "java_class_doc", SCHEMA_NAME, false, ProcedureFEModel.values(), "id",
            new String[]{"id"},
            null,
            "java_class_doc"),

        ;
        private TablesReqs(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
                String seqName, String[] primaryK, Object[] foreignK, String comment){
            this.getTblBusinessRules=fldBusRules;
            this.tableName=dbTblName;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.tableFields=tblFlds;
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

    public enum ProcedureInfo implements EnumIntTableFields{
        PROCEDURE_NAME("procedure_name", LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION("procedure_version", LPDatabase.integerNotNull(), null, null, null, null),
        PROCEDURE_HASH_CODE("procedure_hash_code", LPDatabase.stringNotNull(), null, null, null, null),
        DESCRIPTION(FIELDS_NAMES_DESCRIPTION, LPDatabase.stringNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_NAME("module_name", LPDatabase.stringNotNull(), null, null, null, null),
        LABEL_EN("label_en", LPDatabase.stringNotNull(), null, null, null, null),
        LABEL_ES("label_es", LPDatabase.stringNotNull(), null, null, null, null),
        LOCKED_FOR_ACTIONS("locked_for_actions", LPDatabase.booleanFld(false), null, null, null, null),
        NAVIGATION_ICON_NAME("navigation_icon_name", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null),
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
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }   
    public enum ProcedureRoles implements EnumIntTableFields{
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        DESCRIPTION(FIELDS_NAMES_DESCRIPTION, LPDatabase.string(), null, null, null, null),
//        SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
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
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }

    public enum ProcedureSopMetaData implements EnumIntTableFields{
        SOP_ID("sop_id", LPDatabase.integerNotNull(), null, null, null, null),
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
//        SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
        SOP_NAME("sop_name", LPDatabase.stringNotNull(), null, null, null, null),
        SOP_VERSION("sop_version", LPDatabase.integerNotNull(), null, null, null, null),
        SOP_REVISION("sop_revision", LPDatabase.integerNotNull(), null, null, null, null),
        CURRENT_STATUS("current_status", LPDatabase.string(), null, null, null, null),
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
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }

    public enum ProcedureUserRequirements implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, null, null),
        CODE("code", LPDatabase.string(), null, null, null, null),
        PARENT_CODE("parent_code", LPDatabase.string(), null, null, null, null),
        BRANCH_NEED("branch_need", LPDatabase.string(), null, null, null, null),
        NAME("name", LPDatabase.string(), null, null, null, null),
        WINDOW_ELEMENT_TYPE("window_element_type", LPDatabase.string(), null, null, null, null),
        WINDOW_NAME("window_name", LPDatabase.string(), null, null, null, null),
        WINDOW_ACTION("window_action", LPDatabase.string(), null, null, null, null),
        BUSINESS_RULE("business_rule", LPDatabase.string(), null, null, null, null),
        BUSINESS_RULE_VALUE("business_rule_value", LPDatabase.string(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),
        IN_SCOPE("in_scope", LPDatabase.booleanFld(), null, null, null, null),
        IN_SYSTEM("in_system", LPDatabase.booleanFld(), null, null, null, null),
        CONFIRM_DIALOG("confirmation_dialog", LPDatabase.booleanFld(), null, null, null, null),
        CONFIRM_DIALOG_DETAIL("confirmation_dialog_detail", LPDatabase.booleanFld(), null, null, null, null),
        SOLUTION_TYPE("solution_type", LPDatabase.string(), null, null, null, null),
        ROLES("roles", LPDatabase.string(), null, null, null, null),
        SOP_NAME("sop_name", LPDatabase.string(), null, null, null, null),
        TRAINING_REQ("training_req", LPDatabase.booleanFld(), null, null, null, null),
        TRAINING_NAME("training_name", LPDatabase.string(), null, null, null, null),
        UAT_REQ("uat_req", LPDatabase.booleanFld(), null, null, null, null),
        UAT_NAME("uat_name", LPDatabase.string(), null, null, null, null),
/*        WIDGET("widget", LPDatabase.string(), null, null, null, null),
        WIDGET_VERSION("widget_version", LPDatabase.integer(), null, null, null, null),
        WIDGET_ACTION("widget_action", LPDatabase.string(), null, null, null, null),
        WIDGET_ACCESS_MODE("widget_access_mode", LPDatabase.string(), null, null, null, null),
        WIDGET_TYPE("widget_type", LPDatabase.string(), null, null, null, null),
        WIDGET_LABEL_EN("widget_label_en", LPDatabase.string(), null, null, null, null),
        WIDGET_LABEL_ES("widget_label_es", LPDatabase.string(), null, null, null, null),*/
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
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }

    public enum ProcedureUserRequirementsEvents implements EnumIntTableFields{
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
//        SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, null, null),
        NAME("name", LPDatabase.string(), null, null, null, null),
        LP_FRONTEND_PAGE_NAME("lp_frontend_page_name", LPDatabase.string(), null, null, null, null),
        LP_FRONTEND_PAGE_FILTER("lp_frontend_page_filter", LPDatabase.string(), null, null, null, null),
        //PARENT_NAME("parent_name", LPDatabase.string(), null, null, null, null),
        POSITION("position", LPDatabase.string(), null, null, null, null),
        ROLE_NAME("role_name", LPDatabase.string(), null, null, null, null),
        MODE("mode", LPDatabase.string(), null, null, null, null),
        TYPE("type", LPDatabase.string(), null, null, null, null),
        LABEL_EN("label_en", LPDatabase.string(), null, null, null, null),
        LABEL_ES("label_es", LPDatabase.string(), null, null, null, null),
        SOP("sop", LPDatabase.string(), null, null, null, null),
        ESIGN_REQUIRED("esign_required", LPDatabase.booleanFld(), null, null, null, null),
        USERCONFIRM_REQUIRED("userconfirm_required", LPDatabase.booleanFld(), null, null, null, null),
        ICON_NAME("icon_name", LPDatabase.string(), null, null, null, null),
        ICON_NAME_WHEN_NOT_CERTIFIED("icon_name_when_not_certified", LPDatabase.string(), null, null, null, null)
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
    public enum ProcedureUserRoles implements EnumIntTableFields{
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
//        SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
        USER_NAME("user_name", LPDatabase.stringNotNull(), null, null, null, null),
        ROLE_NAME("role_name", LPDatabase.stringNotNull(), null, null, null, null),
        // ....
        ;
        private ProcedureUserRoles(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum ProcedureUsers implements EnumIntTableFields{
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        USER_NAME("user_name", LPDatabase.stringNotNull(), null, null, null, null),
        FULL_NAME("full_name", LPDatabase.stringNotNull(), null, null, null, null)
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
    public enum ProcedureModuleTables implements EnumIntTableFields{
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
//        SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
        SCHEMA_NAME("schema_name", LPDatabase.stringNotNull(), null, null, null, null),
        TABLE_NAME("table_name", LPDatabase.string(), null, null, null, null),
        IS_VIEW("is_view", LPDatabase.booleanNotNull(false), null, null, null, null),
        FIELD_NAME(GlobalAPIsParams.LBL_FIELD_NAME, LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, null, null),        
        ;
        private ProcedureModuleTables(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum ProcedureBusinessRules implements EnumIntTableFields{
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
//        PROC_INSTANCE_NAME(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull(), null, null, null, null),
//        INSTANCE_NAME("instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        CATEGORY("category", LPDatabase.string(), null, null, null, null),
        EXPLANATION("explanation", LPDatabase.string(), null, null, null, null),
        VALUES_ALLOWED("values_allowed", LPDatabase.string(), null, null, null, null),
        MODULE_NAME("module_name", LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_VERSION("module_version", LPDatabase.integerNotNull(), null, null, null, null),
        FILE_SUFFIX("file_suffix", LPDatabase.string(), null, null, null, null),
        RULE_NAME("rule_name", LPDatabase.string(), null, null, null, null),
        RULE_VALUE("rule_value", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, null, null),
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
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    
    public enum ProcedureMasterData implements EnumIntTableFields{
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),        
        ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, null, null),
        DESCRIPTION(LPDatabase.FIELDS_NAMES_DESCRIPTION, LPDatabase.string(), null, null, null, null),
        OBJECT_TYPE("object_type", LPDatabase.string(), null, null, null, null),
        JSON_OBJ("json_obj", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),
        ;
        private ProcedureMasterData(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum ProcedureFEModel implements EnumIntTableFields{
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        DESCRIPTION(LPDatabase.FIELDS_NAMES_DESCRIPTION, LPDatabase.string(), null, null, null, null),
        MODEL_JSON("model_json", LPDatabase.json(), null, null, null, null),
        MODEL_JSON_MOBILE("model_json_mobile", LPDatabase.json(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),
        ;
        private ProcedureFEModel(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
