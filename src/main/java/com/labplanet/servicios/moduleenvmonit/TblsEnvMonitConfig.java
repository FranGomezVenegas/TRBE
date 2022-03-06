package com.labplanet.servicios.moduleenvmonit;

import lbplanet.utilities.LPDatabase;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ForeignkeyFld;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;

public class TblsEnvMonitConfig {
/*    public static final String getTableCreationScriptFromConfigTableEnvMonit(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "INCUB_BATCH": return IncubBatch.createTableScript(schemaNamePrefix, fields);
            case "INSTRUMENT_INCUBATOR": return InstrIncubator.createTableScript(schemaNamePrefix, fields);
            case "MICROORGANISM": return MicroOrganism.createTableScript(schemaNamePrefix, fields);
            case "PROGRAM": return Program.createTableScript(schemaNamePrefix, fields);
            // ??? case "PROGRAM_RULES": return ProgramRules.createTableScript(schemaNamePrefix, fields);
            case "PROGRAM_DAY": return ProgramDay.createTableScript(schemaNamePrefix, fields);
            case "PROGRAM_CALENDAR": return ProgramCalendar.createTableScript(schemaNamePrefix, fields);
            case "PROGRAM_CALENDAR_DATE": return ProgramCalendarDate.createTableScript(schemaNamePrefix, fields);
            case "PROGRAM_CALENDAR_RECURSIVE_ENTRY": return ProgramCalendarRecursiveEntries.createTableScript(schemaNamePrefix, fields);
            case "PROGRAM_LOCATION": return ProgramLocation.createTableScript(schemaNamePrefix, fields);
//            case "ANALYSIS_METHODS_VIEW": return TblsCnfg.ViewAnalysisMethodsView.createTableScript(schemaNamePrefix, fields);
            default: return "TABLE "+tableName+" NOT IN ENVMONIT_TBLSCNFGENVMONIT"+LPPlatform.LAB_FALSE;
        }        
    }
*/   
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA.getName();

    public enum TablesEnvMonitConfig implements EnumIntTables{        
        PROGRAM(null, "program", SCHEMA_NAME, true, Program.values(), null, new String[]{Program.NAME.getName()}, null, "program table"),
        PROGRAM_LOCATION(null, "program_location", SCHEMA_NAME, true, ProgramLocation.values(), null, 
            new String[]{ProgramLocation.PROGRAM_NAME.getName(), ProgramLocation.LOCATION_NAME.getName(), ProgramLocation.AREA.getName()}, 
            new Object[]{new ForeignkeyFld(ProgramLocation.PROGRAM_NAME.getName(), 
                    SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName()
            )}, "program locations table"),
        PROGRAM_DAY(null, "program_day", SCHEMA_NAME, true, ProgramDay.values(), null, 
            new String[]{ProgramDay.PROGRAM_CONFIG_ID.getName(), ProgramDay.PROGRAM_CONFIG_VERSION.getName()}, 
            new Object[]{new ForeignkeyFld(TblsEnvMonitConfig.ProgramDay.PROGRAM_CONFIG_ID.getName(), 
                    SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName()
            )}, "program_day table"),
        PROGRAM_CALENDAR(null, "program_calendar", SCHEMA_NAME, true, ProgramCalendar.values(), ProgramCalendar.CALENDAR_ID.getName(), 
            new String[]{ProgramCalendar.CALENDAR_ID.getName()}, 
            new Object[]{new ForeignkeyFld(TblsEnvMonitConfig.ProgramCalendar.PROGRAM_ID.getName(), 
                    SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName()
            )}, "program_calendar table"),
        PROGRAM_CALENDAR_DATE(null, "program_calendar_date", SCHEMA_NAME, true, ProgramCalendarDate.values(), ProgramCalendarDate.CALENDAR_ID.getName(), 
            new String[]{ProgramCalendarDate.CALENDAR_ID.getName()}, 
            new Object[]{new ForeignkeyFld(TblsEnvMonitConfig.ProgramCalendarDate.PROGRAM_ID.getName(), 
                    SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName()
            )}, "ProgramCalendarDate table"),
        PROGRAM_CALENDAR_RECURSIVE_ENTRY(null, "program_calendar_recursive_entry", SCHEMA_NAME, true, ProgramCalendarRecursiveEntries.values(), ProgramCalendarRecursiveEntries.ID.getName(), 
            new String[]{ProgramCalendarRecursiveEntries.ID.getName()}, 
            new Object[]{new ForeignkeyFld(TblsEnvMonitConfig.ProgramCalendarRecursiveEntries.PROGRAM_ID.getName(), 
                    SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName()
            )}, "program_calendar_recursive_entry table"),
        MICROORGANISM(null, "microorganism", SCHEMA_NAME, true, MicroOrganism.values(), null, new String[]{MicroOrganism.NAME.getName()}, null, "program table"),
        MICROORGANISM_ADHOC(null, "microorganism_adhoc", SCHEMA_NAME, true, MicroOrganismAdhoc.values(), null, new String[]{MicroOrganismAdhoc.NAME.getName()}, null, "program table"),
        INSTRUMENT_INCUBATOR(null, "instrument_incubator", SCHEMA_NAME, true, InstrIncubator.values(), null, new String[]{InstrIncubator.NAME.getName()}, null, "instrument_incubator table"),
        INCUB_BATCH(null, "incub_batch", SCHEMA_NAME, true, IncubBatch.values(), null, new String[]{IncubBatch.INCUB_BATCH_CONFIG_ID.getName(), IncubBatch.INCUB_BATCH_VERSION.getName()}, null, "incub_batch table"),
        ;
        private TablesEnvMonitConfig(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    public enum Program implements EnumIntTableFields{
        NAME("name",  LPDatabase.stringNotNull(100), null, null, null, null), 
        PROGRAM_CONFIG_ID("program_config_id", LPDatabase.integerNotNull(), null, null, null, null), 
        PROGRAM_CONFIG_VERSION("program_config_version", LPDatabase.integerNotNull(), null, null, null, null), 
        SPEC_CODE("spec_code", LPDatabase.stringNotNull(), null, null, null, null), 
        SPEC_CONFIG_VERSION("spec_config_version", LPDatabase.integerNotNull(), null, null, null, null), 
        SAMPLE_CONFIG_CODE("sample_config_code", LPDatabase.stringNotNull(), null, null, null, null), 
        SAMPLE_CONFIG_CODE_VERSION("sample_config_code_version", LPDatabase.integerNotNull(), null, null, null, null), 
        MAP_IMAGE("map_image", LPDatabase.string(), null, null, null, null), 
        DESCRIPTION_EN("description_en", LPDatabase.string(), null, null, null, null), 
        DESCRIPTION_ES("description_es", LPDatabase.string(), null, null, null, null), 
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null), 
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.stringNotNull(200), null, null, null, null), 
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null), 
        ;
        private Program(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum ProgramLocation implements EnumIntTableFields{
        PROGRAM_NAME("program_name", LPDatabase.stringNotNull(100), null, null, null, null), 
        PROGRAM_ID(FIELDS_NAMES_PROGRAM_ID, LPDatabase.integerNotNull(), null, null, null, null), 
        LOCATION_NAME("location_name",  LPDatabase.string(200), null, null, null, null), 
        AREA("area",  LPDatabase.string(), null, null, null, null), 
        ORDER_NUMBER("order_number",  LPDatabase.integer(), null, null, null, null), 
        DESCRIPTION_EN("description_en",  LPDatabase.string(), null, null, null, null), 
        DESCRIPTION_ES("description_es",  LPDatabase.string(), null, null, null, null), 
        REQUIRES_PERSON_ANA("requires_person_ana", LPDatabase.booleanFld(), null, null, null, null), 
        PERSON_ANA_DEFINITION("person_ana_definition",LPDatabase.string(), null, null, null, null), 
        SPEC_CODE("spec_code",  LPDatabase.string(), null, null, null, null), 
        SPEC_CODE_VERSION("spec_code_version",  LPDatabase.integer(), null, null, null, null), 
        SPEC_VARIATION_NAME("spec_variation_name",  LPDatabase.string(), null, null, null, null), 
        SPEC_ANALYSIS_VARIATION("spec_analysis_variation",  LPDatabase.string(), null, null, null, null), 
        TESTING_GROUP("testing_group",  LPDatabase.string(), null, null, null, null), 
        MAP_ICON("map_icon",  LPDatabase.string(), null, null, null, null), 
        MAP_ICON_H("map_icon_h",  LPDatabase.string(), null, null, null, null), 
        MAP_ICON_W("map_icon_w",  LPDatabase.string(), null, null, null, null), 
        MAP_ICON_TOP("map_icon_top",  LPDatabase.string(), null, null, null, null), 
        MAP_ICON_LEFT("map_icon_left",  LPDatabase.string(), null, null, null, null), 
        
//        , PROGRAM_CONFIG_VERSION("program_config_version", LPDatabase.String())
        // ...
        ;        
        private ProgramLocation(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    
    static final String FIELDS_NAMES_PROGRAM_ID = "program_id";
    public enum ProgramDay implements EnumIntTableFields{
        PROGRAM_CONFIG_ID("program_config_id", LPDatabase.integerNotNull(), null, null, null, null), 
        PROGRAM_CONFIG_VERSION("program_config_version", LPDatabase.integerNotNull(), null, null, null, null), 
        DAY_ID("day_id", LPDatabase.integer(), null, null, null, null), 
        DATE("date", LPDatabase.date(), null, null, null, null), 
        // ...
        ;
        private ProgramDay(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum ProgramCalendar implements EnumIntTableFields{
        CALENDAR_ID("calendar_id", LPDatabase.integer(), null, null, null, null), 
        PROGRAM_ID(FIELDS_NAMES_PROGRAM_ID,LPDatabase.stringNotNull(), null, null, null, null), 
        SCHEDULE_SIZE_UNIT("schedule_size_unit",LPDatabase.stringNotNull(), null, null, null, null), 
        SCHEDULE_SIZE("schedule_size", LPDatabase.integerNotNull(), null, null, null, null), 
        START_DATE("start_date", LPDatabase.date(), null, null, null, null), 
        END_DATE("end_date", LPDatabase.date(), null, null, null, null), 
        DAY_OF_WEEK("day_of_week",LPDatabase.stringNotNull(), null, null, null, null),
        ;        
        private ProgramCalendar(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum ProgramCalendarRecursiveEntries implements EnumIntTableFields{
        ID("id", LPDatabase.integer(), null, null, null, null),
        CALENDAR_ID("calendar_id", LPDatabase.integerNotNull(), null, null, null, null),
        PROGRAM_ID(FIELDS_NAMES_PROGRAM_ID,LPDatabase.stringNotNull(), null, null, null, null),
        RULE("rule",LPDatabase.stringNotNull(), null, null, null, null),
        START_DATE("start_date", LPDatabase.date(), null, null, null, null),
        END_DATE("end_date", LPDatabase.date(), null, null, null, null),
        IS_HOLIDAYS("is_holidays", LPDatabase.booleanFld(false), null, null, null, null),
        ;        
        private ProgramCalendarRecursiveEntries(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum ProgramCalendarDate implements EnumIntTableFields{
        CALENDAR_ID("calendar_id", LPDatabase.integerNotNull(), null, null, null, null),
        PROGRAM_ID(FIELDS_NAMES_PROGRAM_ID,LPDatabase.stringNotNull(), null, null, null, null),
        RECURSIVE_ID("recursive_id", LPDatabase.integerNotNull(), null, null, null, null),
        IS_HOLIDAYS("is_holidays", LPDatabase.booleanFld(false), null, null, null, null),
        DATE("date", LPDatabase.date(), null, null, null, null),
        CONFLICT("conflict", LPDatabase.string(), null, null, null, null),
        CONFLICT_DETAIL("conflict_detail", LPDatabase.string(), null, null, null, null),
        LOCATION_NAME("location_name", LPDatabase.string(), null, null, null, null),
        SPEC("spec", LPDatabase.string(), null, null, null, null),
        VARIATION_NAME("variation_name", LPDatabase.string(), null, null, null, null),
        ANALYSIS_VARIATION("analysis_variation", LPDatabase.string(), null, null, null, null),
        ;        
        private ProgramCalendarDate(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum MicroOrganism implements EnumIntTableFields{
        NAME("name",LPDatabase.stringNotNull(), null, null, null, null),
        ;        
        private MicroOrganism(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum MicroOrganismAdhoc implements EnumIntTableFields{
/*        TBL("microorganism_adhoc",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")*/
        NAME("name",LPDatabase.stringNotNull(), null, null, null, null),
        ADDED_BY("added_by",LPDatabase.stringNotNull(), null, null, null, null),
        ADDED_ON("added_on",LPDatabase.stringNotNull(), null, null, null, null),
        ;        
        private MicroOrganismAdhoc(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum InstrIncubator implements EnumIntTableFields{
        NAME("name",  LPDatabase.stringNotNull(), null, null, null, null),
        DESCRIPTION(LPDatabase.FIELDS_NAMES_DESCRIPTION, LPDatabase.string(200), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY,  LPDatabase.string(200), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, "date NOT NULL", null, null, null, null),
        ACTIVE("active", LPDatabase.booleanNotNull(Boolean.TRUE), null, null, null, null),
        STAGE("stage", LPDatabase.stringNotNull(), null, null, null, null),
        MIN("min", LPDatabase.real(), null, null, null, null),
        IS_MIN_STRICT("is_min_strict", LPDatabase.booleanNotNull(Boolean.TRUE), null, null, null, null),
        MAX("max", LPDatabase.real(), null, null, null, null),
        IS_MAX_STRICT("is_max_strict", LPDatabase.booleanNotNull(Boolean.TRUE), null, null, null, null),
        LOCKED("locked", LPDatabase.booleanFld(), null, null, null, null),
        LOCKED_REASON("locked_reason", LPDatabase.booleanFld(), null, null, null, null),
        ;        
        private InstrIncubator(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum IncubBatch implements EnumIntTableFields{
        INCUB_BATCH_CONFIG_ID("incub_batch_config_id", LPDatabase.integerNotNull(), null, null, null, null),
        INCUB_BATCH_VERSION("incub_batch_version", LPDatabase.integerNotNull(), null, null, null, null),
        NAME("name",  LPDatabase.stringNotNull(100), null, null, null, null),
        DESCRIPTION(LPDatabase.FIELDS_NAMES_DESCRIPTION,  LPDatabase.string(200), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY,  LPDatabase.string(200), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date(), null, null, null, null),
        TYPE("type",  LPDatabase.stringNotNull(100), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(false), null, null, null, null),
        STRUCT_NUM_ROWS("struct_num_rows", LPDatabase.integer(), null, null, null, null),
        STRUCT_NUM_COLS("struct_num_cols", LPDatabase.integer(), null, null, null, null),
        STRUCT_TOTAL_POSITIONS("struct_total_positions", LPDatabase.integer(), null, null, null, null),
        STRUCT_ROWS_NAME("struct_rows_name", "character varying[] COLLATE pg_catalog.\"default\"", null, null, null, null),
        STRUCT_COLS_NAME("struct_cols_name", "character varying[] COLLATE pg_catalog.\"default\"", null, null, null, null),
        STAGE("stage", LPDatabase.stringNotNull(), null, null, null, null),
        //, SENT_FOR_APPROVAL("sent_for_approval", LPDatabase.Boolean())
        // ...
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
