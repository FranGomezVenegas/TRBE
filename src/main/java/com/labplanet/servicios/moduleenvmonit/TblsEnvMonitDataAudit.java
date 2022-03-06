/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import lbplanet.utilities.LPArray;
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
public class TblsEnvMonitDataAudit {
/*    public static final String getTableCreationScriptFromDataAuditTableEnvMonit(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "INCUB_BATCH": return IncubBatch.createTableScript(schemaNamePrefix, fields);
            default: return "TABLE "+tableName+" NOT IN ENVMONIT_TBLSDATAAUDITENVMONIT"+LPPlatform.LAB_FALSE;
        }        
    }    */
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA_AUDIT.getName();    
    public enum TablesEnvMonitDataAudit implements EnumIntTables{        
        INCUB_BATCH(null, "incub_batch", SCHEMA_NAME, true, IncubBatch.values(), IncubBatch.FLD_AUDIT_ID.getName(), 
            new String[]{IncubBatch.FLD_AUDIT_ID.getName()}, null, "IncubBatch table"),
        ;
        private TablesEnvMonitDataAudit(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    
    public enum IncubBatch implements EnumIntTableFields{
        FLD_AUDIT_ID("audit_id", LPDatabase.integerNotNull(), null, null, null, null), 
        FLD_TABLE_NAME("table_name", " character varying COLLATE pg_catalog.\"default\"", null, null, null, null), 
        FLD_TRANSACTION_ID("transaction_id", LPDatabase.integer(), null, null, null, null), 
        FLD_TABLE_ID("table_id", LPDatabase.string(), null, null, null, null), 
        FLD_DATE("date", LPDatabase.dateTime(), null, null, null, null), 
        FLD_PERSON("person", LPDatabase.string(), null, null, null, null), 
        FLD_ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null), 
        FLD_FIELDS_UPDATED("fields_updated", LPDatabase.string(), null, null, null, null), 
        FLD_BATCH_NAME("batch", LPDatabase.string(), null, null, null, null), 
        FLD_USER_ROLE("user_role", LPDatabase.string(), null, null, null, null), 
        FLD_PROCEDURE("procedure", LPDatabase.string(), null, null, null, null), 
        FLD_PROCEDURE_VERSION("procedure_version", LPDatabase.integer(), null, null, null, null), 
        FLD_APP_SESSION_ID("app_session_id", LPDatabase.integer(), null, null, null, null), 
        FLD_PICTURE_BEFORE("picture_before", "json", null, null, null, null), 
        FLD_PICTURE_AFTER("picture_after", "json", null, null, null, null), 
        FLD_REVIEWED("reviewed", LPDatabase.booleanFld(false), null, null, null, null), 
        FLD_REVIEWED_BY("reviewed_by", LPDatabase.string(), null, null, null, null), 
        FLD_REVIEWED_ON("reviewed_on", LPDatabase.dateTime(), null, null, null, null), 
        FLD_REVISION_NOTE("revision_note", LPDatabase.string(), null, null, null, null), 
        FLD_PARENT_AUDIT_ID("parent_audit_id", LPDatabase.integer(), null, null, null, null), 
        FLD_REASON("reason", LPDatabase.string(), null, null, null, null), 
        ;
        private IncubBatch(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
