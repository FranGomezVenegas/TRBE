/*
 * To change this license header, choose License Headers in Spec Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import lbplanet.utilities.LPDatabase;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ForeignkeyFld;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;

public class TblsCnfgAudit {
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.APP_AUDIT.getName();
    public enum TablesCfgAudit implements EnumIntTables{
        ANALYSIS(null, "analysis", SCHEMA_NAME, true, Analysis.values(), Analysis.FLD_AUDIT_ID.getName(),
            new String[]{Analysis.FLD_AUDIT_ID.getName()}, null, "Analysis Audit Trial"),
        SPEC(null, "spec", SCHEMA_NAME, true, Spec.values(), Spec.FLD_AUDIT_ID.getName(),
            new String[]{Spec.FLD_AUDIT_ID.getName()}, null, "Spec Audit Trial"),
        ;
        private TablesCfgAudit(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    
    public enum Analysis implements EnumIntTableFields{
        FLD_AUDIT_ID("audit_id", LPDatabase.integer(), null, null, null, null),
        FLD_TABLE_NAME("table_name", " character varying COLLATE pg_catalog.\"default\"", null, null, null, null),
        FLD_TRANSACTION_ID("transaction_id", LPDatabase.integer(), null, null, null, null),
        FLD_TABLE_ID("table_id", LPDatabase.string(), null, null, null, null),
        FLD_DATE("date", LPDatabase.dateTime(), null, null, null, null),
        FLD_PERSON("person", LPDatabase.string(), null, null, null, null),
        FLD_ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null),
        FLD_FIELDS_UPDATED("fields_updated", LPDatabase.string(), null, null, null, null),
        FLD_CODE("code", LPDatabase.string(), null, null, null, null),
        FLD_CONFIG_VERSION("config_version", LPDatabase.integer(), null, null, null, null),
        FLD_USER_ROLE("user_role", LPDatabase.string(), null, null, null, null),
        FLD_PROCEDURE("procedure", LPDatabase.string(), null, null, null, null),
        FLD_PROCEDURE_VERSION("procedure_version", LPDatabase.integer(), null, null, null, null),
        FLD_APP_SESSION_ID("app_session_id", LPDatabase.integer(), null, null, null, null),
        FLD_PICTURE_BEFORE("picture_before", "json", null, null, null, null),
        FLD_PICTURE_AFTER("picture_after", "json", null, null, null, null),
        FLD_PARENT_AUDIT_ID("parent_audit_id", LPDatabase.integer(), null, null, null, null),
        FLD_REASON("reason", LPDatabase.string(), null, null, null, null),
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

    public enum Spec implements EnumIntTableFields{
        FLD_AUDIT_ID("audit_id", LPDatabase.integer(), null, null, null, null),
        FLD_TABLE_NAME("table_name", LPDatabase.string(), null, null, null, null),
        FLD_TRANSACTION_ID("transaction_id", LPDatabase.integer(), null, null, null, null),
        FLD_TABLE_ID("table_id", LPDatabase.string(), null, null, null, null),
        FLD_DATE("date", LPDatabase.dateTime(), null, null, null, null),
        FLD_PERSON("person", LPDatabase.string(), null, null, null, null),
        FLD_ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null),
        FLD_FIELDS_UPDATED("fields_updated", LPDatabase.string(), null, null, null, null),
        FLD_SPEC_CODE("spec_code", LPDatabase.string(), null, null, null, null),
        FLD_SPEC_CONFIG_VERSION("spec_config_version", LPDatabase.integer(), null, null, null, null),
        FLD_USER_ROLE("user_role", LPDatabase.string(), null, null, null, null),
        FLD_PROCEDURE("procedure", LPDatabase.string(), null, null, null, null),
        FLD_PROCEDURE_VERSION("procedure_version", LPDatabase.integer(), null, null, null, null),
        FLD_APP_SESSION_ID("app_session_id", LPDatabase.integer(), null, null, null, null),
        FLD_PICTURE_BEFORE("picture_before", "json", null, null, null, null),
        FLD_PICTURE_AFTER("picture_after", "json", null, null, null, null),
        FLD_PARENT_AUDIT_ID("parent_audit_id", LPDatabase.integer(), null, null, null, null),
        FLD_REASON("reason", LPDatabase.string(), null, null, null, null),
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
    
}
