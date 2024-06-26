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
 * @author User
 */
public class TblsProcedureAudit {
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.PROCEDURE_AUDIT.getName();
    
    public enum TablesProcedureAudit implements EnumIntTables{
        PROC_HASH_CODES(null, "proc_hashcodes_history", SCHEMA_NAME, true, procHashCodesHistory.values(),procHashCodesHistory.ID.getName(),
            new String[]{procHashCodesHistory.ID.getName()}, null, ""),
        INVESTIGATION(null, "investigation", SCHEMA_NAME, true, Investigation.values(), Investigation.AUDIT_ID.getName(),
            new String[]{Investigation.AUDIT_ID.getName()}, null, "Analysis Audit Trial"),
        ;
        private TablesProcedureAudit(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    
    public enum procHashCodesHistory implements EnumIntTableFields{
        ID("id", LPDatabase.integer(), null, null, null, null),
        DATE("date", LPDatabase.dateTime(), null, null, null, null),
        PERSON("person", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), "", null),
        ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null),
        NEW_HASHCODE("new_hashcode", LPDatabase.string(), null, null, null, null),
        //PREVIOUS_HASHCODE("previous_hashcode", LPDatabase.string(), null, null, null, null),
        PICTURE("picture", LPDatabase.json(),null, null, null, null)
        ;
        private procHashCodesHistory(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
            
    public enum Investigation implements EnumIntTableFields{
        AUDIT_ID("audit_id", LPDatabase.integer(), null, null, null, null),
        TABLE_NAME("table_name", LPDatabase.string(), null, null, null, null),
        TRANSACTION_ID("transaction_id", LPDatabase.integer(), null, null, null, null),
        TABLE_ID("table_id", LPDatabase.string(), null, null, null, null),
        DATE("date", LPDatabase.dateTime(), null, null, null, null),
        PERSON("person", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), "", null),
        ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null),
        FIELDS_UPDATED("fields_updated", LPDatabase.string(), null, null, null, null),
        INVESTIGATION_ID("investigation_id", LPDatabase.integer(), null, null, null, null),
        USER_ROLE("user_role", LPDatabase.string(), null, null, null, null),
        PROCEDURE("procedure", LPDatabase.string(), null, null, null, null),
        PROCEDURE_VERSION("procedure_version", LPDatabase.integer(), null, null, null, null),
        PROCEDURE_HASH_CODE("procedure_hash_code", LPDatabase.stringNotNull(), null, null, null, null),
        APP_SESSION_ID("app_session_id", LPDatabase.integer(), null, null, null, null),
        PICTURE_BEFORE("picture_before", "json", null, null, null, null),
        PICTURE_AFTER("picture_after", "json", null, null, null, null),
        REVIEWED("reviewed", LPDatabase.booleanFld(false), null, null, null, null),
        REVIEWED_BY("reviewed_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        REVIEWED_ON("reviewed_on", LPDatabase.dateTime(), null, null, null, null),
        REVISION_NOTE("revision_note", LPDatabase.string(), null, null, null, null),
        PARENT_AUDIT_ID("parent_audit_id", LPDatabase.integer(), null, null, null, null),      
        NOTE("note", LPDatabase.string(), null, null, null, null),
        STATUS("status", LPDatabase.string(), null, null, null, null),
        REASON("reason", LPDatabase.string(), null, null, null, null),
        ;
        private Investigation(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
