/*
 * To change this license header, choose License Headers in Spec Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.instrumentsmanagement.definition;

import databases.TblsAppConfig;
import lbplanet.utilities.LPDatabase;
import static lbplanet.utilities.LPDatabase.dateTime;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class TblsInstrumentsDataAudit {
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA_AUDIT.getName();
    public enum TablesInstrumentsDataAudit implements EnumIntTables{
        INSTRUMENTS(null, "instruments", SCHEMA_NAME, true, Instruments.values(), Instruments.AUDIT_ID.getName(),
            new String[]{Instruments.AUDIT_ID.getName()}, null, "Audit for User Sessions"),
        ;
        private TablesInstrumentsDataAudit(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    
    public enum Instruments implements EnumIntTableFields{
        AUDIT_ID("audit_id", LPDatabase.integerNotNull(), null, null, null, null),
        INSTRUMENT_NAME("instrument_name", LPDatabase.stringNotNull(), null, null, null, null),
        TABLE_NAME("table_name", LPDatabase.string(), null, null, null, null),
        TRANSACTION_ID("transaction_id", LPDatabase.integer(), null, null, null, null),
        TABLE_ID("table_id", LPDatabase.string(), null, null, null, null),
        DATE("date", LPDatabase.dateTime(), null, null, null, null),
        PERSON("person", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), "", null),        ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null),
        FIELDS_UPDATED("fields_updated", LPDatabase.string(), null, null, null, null),
        CODE("code", LPDatabase.string(), null, null, null, null),
        CONFIG_VERSION("config_version", LPDatabase.integer(), null, null, null, null),
        USER_ROLE("user_role", LPDatabase.string(), null, null, null, null),
        PROCEDURE("procedure", LPDatabase.string(), null, null, null, null),
        PROCEDURE_VERSION("procedure_version", LPDatabase.integer(), null, null, null, null),
        PROCEDURE_HASH_CODE("procedure_hash_code", LPDatabase.stringNotNull(), null, null, null, null),
        APP_SESSION_ID("app_session_id", LPDatabase.integer(), null, null, null, null),
        PICTURE_BEFORE("picture_before", "json", null, null, null, null),
        PICTURE_AFTER("picture_after", "json", null, null, null, null),
        REVIEWED("reviewed", LPDatabase.booleanFld(false), null, null, null, null),
        REVIEWED_BY("reviewed_by", LPDatabase.string(), null, null, null, null),
        REVIEWED_ON("reviewed_on", dateTime(), null, null, null, null),        
        PARENT_AUDIT_ID("parent_audit_id", LPDatabase.integer(), null, null, null, null),
        REASON("reason", LPDatabase.string(), null, null, null, null),
        ACTION_PRETTY_EN("action_pretty_en", LPDatabase.string(), null, null, null, null),
        ACTION_PRETTY_ES("action_pretty_es", LPDatabase.string(), null, null, null, null),
        ;
        private Instruments(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
