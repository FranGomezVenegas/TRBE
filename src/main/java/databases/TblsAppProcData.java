/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ReferenceFld;
import static trazit.enums.deployrepository.DeployTables.createTableScript;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class TblsAppProcData {
    public static final String getTableCreationScriptFromDataTable(String tableName, String[] fields){
        switch (tableName.toUpperCase()){
            case "INSTRUMENT_EVENT": return createTableScript(TablesAppProcData.INSTRUMENT_EVENT);
            default: return "TABLE "+tableName+" NOT IN TBLDATA "+LPPlatform.LAB_FALSE;
        }        
    }

    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.APP_PROC_DATA.getName();
    public enum TablesAppProcData implements EnumIntTables{
        INSTRUMENTS(null, "instruments", SCHEMA_NAME, true, Instruments.values(), Instruments.NAME.getName(),
            new String[]{Instruments.NAME.getName()}, null, ""),
        INSTRUMENT_EVENT(null, "instrument_event", SCHEMA_NAME, true, TblsAppProcData.InstrumentEvent.values(), TblsAppProcData.InstrumentEvent.ID.getName(),
            new String[]{TblsAppProcData.InstrumentEvent.ID.getName()}, null, ""),
        INSTR_EVENT_VARIABLE_VALUES(null, "instr_event_variable_values", SCHEMA_NAME, true, TblsAppProcData.InstrEventVariableValues.values(), TblsAppProcData.InstrumentEvent.ID.getName(),
            new String[]{TblsAppProcData.InstrEventVariableValues.ID.getName()}, null, ""),
        ;
        private TablesAppProcData(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, null, null, null),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        FAMILY("family", LPDatabase.string(), null, null, null, null),
        DECOMMISSIONED("decommissioned", LPDatabase.booleanFld(), null, null, null, null),
        DECOMMISSIONED_BY("decommissioned_by", LPDatabase.string(), null, null, null, null),
        DECOMMISSIONED_ON("decommissioned_on", LPDatabase.dateTime(), null, null, null, null),
        UNDECOMMISSIONED_BY("undecommissioned_by", LPDatabase.string(), null, null, null, null),
        UNDECOMMISSIONED_ON("undecommissioned_on", LPDatabase.dateTime(), null, null, null, null),
        ON_LINE("on_line", LPDatabase.booleanFld(), null, null, null, null),
        IS_LOCKED("is_locked", LPDatabase.booleanFld(), null, null, null, null),
        LOCKED_REASON("locked_reason", LPDatabase.string(), null, null, null, null),
        LAST_CALIBRATION("last_calibration",LPDatabase.dateTime(), null, null, null, null),
        NEXT_CALIBRATION("next_calibration",LPDatabase.dateTime(), null, null, null, null),
        LAST_PM("last_prev_maint",LPDatabase.dateTime(), null, null, null, null),
        NEXT_PM("next_prev_maint",LPDatabase.dateTime(), null, null, null, null),
        LAST_VERIF("last_verification",LPDatabase.dateTime(), null, null, null, null),
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
    
    public enum InstrumentEvent implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        INSTRUMENT("instrument", LPDatabase.string(), null, null, null, null),
        EVENT_TYPE("event_type", LPDatabase.string(), null, null, null, null),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, null, null, null),
        COMPLETED_ON("completed_on", LPDatabase.dateTime(), null, null, null, null),
        COMPLETED_BY("completed_by", LPDatabase.string(), null, null, null, null),
        DECISION("decision", LPDatabase.string(), null, null, null, null),
        ATTACHMENT("attachment", LPDatabase.string(), null, null, null, null),         
        ;
        private InstrumentEvent(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum InstrEventVariableValues implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        INSTRUMENT("instrument", LPDatabase.string(), null, null, null, null),
        EVENT_ID("event_id", LPDatabase.string(), null, null, null, null),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        VARIABLE_SET("variable_set", LPDatabase.stringNotNull(), null, null, null, null),
        PARAM_NAME("param_name", LPDatabase.stringNotNull(), null, null, null, null),
        VALUE("value", LPDatabase.string(), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        PARAM_TYPE("param_type", LPDatabase.string(), null, null, null, null),
        REQUIRED("required", LPDatabase.string(), null, null, null, null),
        ALLOWED_VALUES("allowed_values", LPDatabase.string(), null, null, null, null),
        OWNER_ID("owner_id", LPDatabase.stringNotNull(), null, null, null, null),
        ENTERED_ON("entered_on", LPDatabase.dateTime(), null, null, null, null),
        ENTERED_BY("entered_by", LPDatabase.string(), null, null, null, null),
        REENTERED("reentered", LPDatabase.booleanFld(false), null, null, null, null),
        
        ;
        private InstrEventVariableValues(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
