/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.instrumentsmanagement.definition;

import databases.SqlStatementEnums.JOIN_TYPES;
import databases.TblsAppConfig;
import lbplanet.utilities.LPDatabase;
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
public class TblsInstrumentsData {
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesInstrumentsData implements EnumIntTables{
        INSTRUMENTS(null, "instruments", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Instruments.values(), 
            null, new String[]{Instruments.NAME.getName()}, null, ""),
        INSTRUMENT_EVENT(null, "instrument_event", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsInstrumentsData.InstrumentEvent.values(), 
            TblsInstrumentsData.InstrumentEvent.ID.getName(),new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()}, null, ""),
        INSTR_EVENT_VARIABLE_VALUES(null, "instr_event_variable_values", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsInstrumentsData.InstrEventVariableValues.values(), 
            TblsInstrumentsData.InstrumentEvent.ID.getName(), new String[]{TblsInstrumentsData.InstrEventVariableValues.ID.getName()}, null, ""),
        ;
        private TablesInstrumentsData(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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

    public enum ViewsInstrumentsData implements EnumIntViews{
        NOT_DECOM_INSTR_EVENT_DATA_VW(""
                + "select ie.id, ie.instrument, ie.event_type, ie.created_on, ie.completed_on, ie.decision, ie.attachment, " +
                "ie.created_by, ie.completed_by, ie.completed_decision, " +
                "i.on_line, i.decommissioned, i.is_locked, i.locked_reason, i.last_calibration, i.next_calibration, " +
                "i.last_prev_maint, i.next_prev_maint, i.last_verification " +
                "from #SCHEMA_DATA.instruments i, #SCHEMA_DATA.instrument_event ie " +
                "where ie.instrument=i.name and i.decommissioned=false and ie.completed_on is null"+
                "ALTER VIEW  #SCHEMA.#TBL  OWNER TO #OWNER;",
            null, "not_decom_instr_event_data_vw", SCHEMA_NAME, true, TblsInstrumentsData.ViewNotDecommInstrumentAndEventData.values(), "pr_scheduled_locations", 
        new EnumIntTablesJoin[]{
            new EnumIntTablesJoin(TablesInstrumentsData.INSTRUMENTS, "i", TablesInstrumentsData.INSTRUMENT_EVENT, "ie", true,
                new EnumIntTableFields[][]{{TblsInstrumentsData.Instruments.NAME, TblsInstrumentsData.InstrumentEvent.INSTRUMENT}
                }, " and i.decommissioned=false and ie.completed_on is null", JOIN_TYPES.INNER),
        }, ""),
        CALIB_PM_EXPIRED_OR_EXPIRING(""
                + "select 'CALIBRATION' as type, DATE(now()) as now, i.name as name, i.family as family, i.next_calibration as next_date, fam.calib_system_create_new_event_when_expires as system_create_new_event_when_expires, fam.calib_sched_create_offset_days as sched_create_offset_days\n" +
"  ,(select count(*) from \"app-proc-data\".instrument_event ev where event_type='CALIBRATION' and completed_on is null ) as events_in_progress\n" +
"  from \"app-proc-config\".instruments_family fam, \"app-proc-data\".instruments i\n" +
" where i.family=fam.name \n" +
"   and fam.calib_system_create_new_event_when_expires\n" +
"   and (fam.calib_sched_create_offset_days is null and \n" +
"		(i.next_calibration is null or (i.next_calibration is not null and date(i.next_calibration) < date(now())) ) )   		\n" +
"union\n" +
"select 'CALIBRATION_OFFSET' as type, DATE(now()) as now, i.name as name, i.family as family, i.next_calibration as next_date, fam.calib_system_create_new_event_when_expires as system_create_new_event_when_expires, fam.calib_sched_create_offset_days as sched_create_offset_days\n" +
"  ,(select count(*) from \"app-proc-data\".instrument_event ev where event_type='CALIBRATION' and completed_on is null ) as events_in_progress \n" +
"  from \"app-proc-config\".instruments_family fam, \"app-proc-data\".instruments i\n" +
" where i.family=fam.name \n" +
"   and fam.calib_system_create_new_event_when_expires\n" +
"   and (fam.calib_sched_create_offset_days is not null and \n" +
"		(i.next_calibration is null or \n" +
" 	(i.next_calibration is not null and (date(i.next_calibration)-fam.calib_sched_create_offset_days) < date(now()) ))) \n" +
"union\n" +
"select 'PM' as type, DATE(now()) as now, i.name as name, i.family as family, i.next_prev_maint as next_date, fam.pm_system_create_new_event_when_expires as system_create_new_event_when_expires, fam.pm_sched_create_offset_days as sched_create_offset_days\n" +
"  ,(select count(*) from \"app-proc-data\".instrument_event ev where event_type='PREVENTIVE_MAINTENANCE' and completed_on is null ) as events_in_progress\n" +
"  from \"app-proc-config\".instruments_family fam, \"app-proc-data\".instruments i\n" +
" where i.family=fam.name \n" +
"   and fam.pm_system_create_new_event_when_expires\n" +
"   and (fam.pm_sched_create_offset_days is null and \n" +
"		(i.next_prev_maint is null or (i.next_prev_maint is not null and date(i.next_prev_maint) < date(now())) ) )\n" +
"union\n" +
"select 'PM_OFFSET' as type, DATE(now()) as now, i.name as name, i.family as family, i.next_prev_maint as next_date, fam.pm_system_create_new_event_when_expires as system_create_new_event_when_expires, fam.pm_sched_create_offset_days as sched_create_offset_days\n" +
"  ,(select count(*) from \"app-proc-data\".instrument_event ev where event_type='PREVENTIVE_MAINTENANCE' and completed_on is null ) as events_in_progress\n" +
"  from \"app-proc-config\".instruments_family fam, \"app-proc-data\".instruments i\n" +
" where i.family=fam.name \n" +
"   and fam.pm_system_create_new_event_when_expires\n" +
"   and (fam.pm_sched_create_offset_days is not null and \n" +
"		(i.next_prev_maint is null or \n" +
" 	(i.next_prev_maint is not null and (date(i.next_prev_maint)-fam.pm_sched_create_offset_days) < date(now()) ))) ",
            null, "calib_pm_expired_or_expiring", SCHEMA_NAME, true, TblsInstrumentsData.CalibPmExpiredOrExpiring.values(), "pr_scheduled_locations", 
        new EnumIntTablesJoin[]{
            new EnumIntTablesJoin(TablesInstrumentsData.INSTRUMENTS, "i", TablesInstrumentsData.INSTRUMENT_EVENT, "ie", true,
                new EnumIntTableFields[][]{{TblsInstrumentsData.Instruments.NAME, TblsInstrumentsData.InstrumentEvent.INSTRUMENT}
                }, " and i.decommissioned=false and ie.completed_on is null", JOIN_TYPES.INNER),
        }, ""),
        ;
        private ViewsInstrumentsData(String viewScript, FldBusinessRules[] fldBusRules, String dbVwName, String repositoryName, Boolean isProcedure, EnumIntViewFields[] vwFlds, 
                String comment, EnumIntTablesJoin[] TablesInView, String extraFilters){
            this.getTblBusinessRules=fldBusRules;
            this.viewName=dbVwName;
            this.viewFields=vwFlds;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.viewComment=comment;
            this.viewScript=viewScript;
            this.tablesInTheView=TablesInView;
            this.extraFilters=extraFilters;
        }
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public String getViewCreatecript() {return this.viewScript;}
        @Override        public String getViewName() {return this.viewName;}
        @Override        public EnumIntViewFields[] getViewFields() {return this.viewFields;}
        @Override        public String getViewComment() {return this.viewComment;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        
        private final EnumIntTablesJoin[] tablesInTheView;
        @Override  public EnumIntTablesJoin[] getTablesRequiredInView() {return this.tablesInTheView;}
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String viewName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final EnumIntViewFields[] viewFields;
        private final String viewComment;
        private final String viewScript;

        @Override public String getExtraFilters() {return this.extraFilters;}
        private final String extraFilters;
    }
    
    public enum Instruments implements EnumIntTableFields{
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        FAMILY("family", LPDatabase.string(), null, null, null, null),
        SUPPLIER("supplier", LPDatabase.string(), null, null, null, null),
        MANUFACTURER("manufacturer", LPDatabase.string(), null, null, null, null),
        SERIAL_NUMBER("serial_number", LPDatabase.string(), null, null, null, null),
        MODEL_NUMBER("model_number", LPDatabase.string(), null, null, null, null),
        DECOMMISSIONED("decommissioned", LPDatabase.booleanFld(false), null, null, null, null),
        DECOMMISSIONED_BY("decommissioned_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        DECOMMISSIONED_ON("decommissioned_on", LPDatabase.dateTime(), null, null, null, null),
        UNDECOMMISSIONED_BY("undecommissioned_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        UNDECOMMISSIONED_ON("undecommissioned_on", LPDatabase.dateTime(), null, null, null, null),
        ON_LINE("on_line", LPDatabase.booleanFld(false), null, null, null, null),
        IS_LOCKED("is_locked", LPDatabase.booleanFld(false), null, null, null, null),
        LOCKED_REASON("locked_reason", LPDatabase.string(), null, null, null, null),
        LAST_CALIBRATION("last_calibration",LPDatabase.dateTime(), null, null, null, null),
        NEXT_CALIBRATION("next_calibration",LPDatabase.dateTime(), null, null, null, null),
        LAST_PM("last_prev_maint",LPDatabase.dateTime(), null, null, null, null),
        NEXT_PM("next_prev_maint",LPDatabase.dateTime(), null, null, null, null),
        LAST_VERIF("last_verification",LPDatabase.dateTime(), null, null, null, null),
        PO_DATE("po_date",LPDatabase.dateTime(), null, null, null, null),
        INSTALLATION_DATE("installation_date",LPDatabase.dateTime(), null, null, null, null),
        RESPONSIBLE("responsible",LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        RESPONSIBLE_BACKUP("responsible_backup",LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null)
        
        
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
        CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        COMPLETED_ON("completed_on", LPDatabase.dateTime(), null, null, null, null),
        COMPLETED_BY("completed_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        COMPLETED_DECISION("completed_decision", LPDatabase.string(), null, null, null, null),
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
        EVENT_ID("event_id", LPDatabase.integerNotNull(), null, null, null, null),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        VARIABLE_SET("variable_set", LPDatabase.stringNotNull(), null, null, null, null),
        PARAM_NAME("param_name", LPDatabase.stringNotNull(), null, null, null, null),
        VALUE("value", LPDatabase.string(), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        PARAM_TYPE("param_type", LPDatabase.string(), null, null, null, null),
        REQUIRED("required", LPDatabase.string(), null, null, null, null),
        ALLOWED_VALUES("allowed_values", LPDatabase.string(), null, null, null, null),
        OWNER_ID("owner_id", LPDatabase.stringNotNull(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        ENTERED_ON("entered_on", LPDatabase.dateTime(), null, null, null, null),
        ENTERED_BY("entered_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
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
    
    public enum ViewNotDecommInstrumentAndEventData implements EnumIntViewFields{
        ID("id", "ie.id", InstrumentEvent.ID, null, null, null),
        INSTRUMENT("instrument", "ie.instrument", InstrumentEvent.INSTRUMENT, null, null, null),
        INSTRUMENT_FAMILY("instrument_family", "i.family as instrument_family", Instruments.FAMILY, null, null, null),                
        RESPONSIBLE("instrument_responsible", "i.responsible as instrument_responsible", Instruments.RESPONSIBLE, null, null, null),                
        RESPONSIBLE_BACKUP("instrument_responsible_backup", "i.responsible_backup as instrument_responsible_backup", Instruments.RESPONSIBLE_BACKUP, null, null, null),                
        EVENT_TYPE("event_type", "ie.event_type", InstrumentEvent.EVENT_TYPE, null, null, null),
        CREATED_ON("created_on", "ie.created_on", InstrumentEvent.CREATED_ON, null, null, null),
        CREATED_BY("created_by", "ie.created_by", InstrumentEvent.CREATED_BY, null, null, null),
        COMPLETED_ON("completed_on", "ie.completed_on", InstrumentEvent.COMPLETED_ON, null, null, null),
        COMPLETED_BY("completed_by", "ie.completed_by", InstrumentEvent.COMPLETED_BY, null, null, null),
        COMPLETED_DECISION("completed_decision", "ie.completed_decision", InstrumentEvent.COMPLETED_DECISION, null, null, null),
        ATTACHMENT("attachment", "ie.attachment", InstrumentEvent.ATTACHMENT, null, null, null),
        INST_ONLINE("on_line", "i.on_line", Instruments.ON_LINE, null, null, null),
        INST_DECOM("decommissioned", "i.decommissioned", Instruments.DECOMMISSIONED, null, null, null),
        INST_ISLOCKED("is_locked", "i.is_locked", Instruments.IS_LOCKED, null, null, null),
        INST_LOCKED_REASON("locked_reason", "i.locked_reason", Instruments.LOCKED_REASON, null, null, null),
        LAST_CALIBRATION("last_calibration", "i.last_calibration", Instruments.LAST_CALIBRATION, null, null, null),
        NEXT_CALIBRATION("next_calibration", "i.next_calibration", Instruments.NEXT_CALIBRATION, null, null, null),
        LAST_PREV_MAINT("last_prev_maint", "i.last_prev_maint", Instruments.LAST_PM, null, null, null),
        NEXT_PREV_MAINT("next_prev_maint", "i.next_prev_maint", Instruments.NEXT_PM, null, null, null),
        LAST_VERIFICATION("last_verification", "i.last_verification", Instruments.LAST_VERIF, null, null, null),
        TOTAL_PARAMS("total_params", "(select count(*) from \"#PROC_INSTANCE_NAME-#SCHEMA_DATA\".instr_event_variable_values eparam where  eparam.event_id=ie.id) as total_params", null, null, null, null),
        PENDING_PARAMS("pending_params", "(select count(*) from \"#PROC_INSTANCE_NAME-#SCHEMA_DATA\".instr_event_variable_values eparam where  eparam.event_id=ie.id and eparam.value is null) as pending_params", null, null, null, null),
        
        ;
        private ViewNotDecommInstrumentAndEventData(String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
            this.fldName=name;
            this.fldAliasInView=vwAliasName;
            this.fldMask=fldMask;
            this.fldComment=comment;
            this.fldBusinessRules=busRules;
            this.fldObj=fldObj;
        }
        private final String fldName;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;        
        @Override public String getName() {return fldName;}
        @Override public String getViewAliasName() {return this.fldAliasInView;}
        @Override public String getFieldMask() {return this.fldMask;}
        @Override public String getFieldComment() {return this.fldComment;}
        @Override public FldBusinessRules[] getFldBusinessRules() {return this.fldBusinessRules;}
        @Override public EnumIntTableFields getTableField() {return this.fldObj;}
    }        
    
    public enum CalibPmExpiredOrExpiring implements EnumIntViewFields{
        TYPE("type", "type as type", null, null, null, null),
        NOW("now", "now as now", null, null, null, null),
        NAME("name", "name as name", null, null, null, null),                
        FAMILY("family", "family as family", null, null, null, null),                
        NEXT_DATE("next_date", "next_date as next_date", null, null, null, null),                
        SYSTEM_CREATE_NEW_EVENT_WHEN_EXPIRES("system_create_new_event_when_expires", "system_create_new_event_when_expires as system_create_new_event_when_expires", InstrumentEvent.EVENT_TYPE, null, null, null),
        EVENTS_IN_PROGRESS("events_in_progress", "events_in_progress  as events_in_progress", null, null, null, null),
        SCHED_CREATE_OFFSET_DAYS("sched_create_offset_days", "sched_create_offset_days  as sched_create_offset_days", null, null, null, null)
        
        ;
        private CalibPmExpiredOrExpiring(String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
            this.fldName=name;
            this.fldAliasInView=vwAliasName;
            this.fldMask=fldMask;
            this.fldComment=comment;
            this.fldBusinessRules=busRules;
            this.fldObj=fldObj;
        }
        private final String fldName;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;        
        @Override public String getName() {return fldName;}
        @Override public String getViewAliasName() {return this.fldAliasInView;}
        @Override public String getFieldMask() {return this.fldMask;}
        @Override public String getFieldComment() {return this.fldComment;}
        @Override public FldBusinessRules[] getFldBusinessRules() {return this.fldBusinessRules;}
        @Override public EnumIntTableFields getTableField() {return this.fldObj;}
    }        
        
}
