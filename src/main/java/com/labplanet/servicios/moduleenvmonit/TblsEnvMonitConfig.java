package com.labplanet.servicios.moduleenvmonit;

import lbplanet.utilities.LPDatabase;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntViewFields;
import trazit.enums.EnumIntViews;
import trazit.enums.FldBusinessRules;
import trazit.enums.ForeignkeyFld;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;

public class TblsEnvMonitConfig {
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.CONFIG.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesEnvMonitConfig implements EnumIntTables{        
        PROGRAM(null, "program", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Program.values(), null, new String[]{Program.PROGRAM_CONFIG_ID.getName(), Program.PROGRAM_CONFIG_VERSION.getName()}, null, "program table"),
        PROGRAM_LOCATION(null, "program_location", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProgramLocation.values(), null, 
            new String[]{ProgramLocation.PROGRAM_NAME.getName(), ProgramLocation.LOCATION_NAME.getName(), ProgramLocation.AREA.getName()}, 
            //new Object[]{new ForeignkeyFld(ProgramLocation.PROGRAM_NAME.getName(), 
            //        SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.NAME.getName()
            //)}
            new Object[]{new ForeignkeyFld(TblsEnvMonitConfig.ProgramLocation.PROGRAM_CONFIG_ID.getName(), SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName()),
                new ForeignkeyFld(TblsEnvMonitConfig.ProgramLocation.PROGRAM_CONFIG_VERSION.getName(), SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_VERSION.getName())
            },            
            "program locations table"),
        PROGRAM_DAY(null, "program_day", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProgramDay.values(), null, 
            new String[]{ProgramDay.PROGRAM_CONFIG_ID.getName(), ProgramDay.PROGRAM_CONFIG_VERSION.getName()}, 
            new Object[]{new ForeignkeyFld(TblsEnvMonitConfig.ProgramDay.PROGRAM_CONFIG_ID.getName(), SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName()),
                new ForeignkeyFld(TblsEnvMonitConfig.ProgramDay.PROGRAM_CONFIG_VERSION.getName(), SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_VERSION.getName())
            }, "program_day table"),
        PROGRAM_CALENDAR(null, "program_calendar", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProgramCalendar.values(), ProgramCalendar.CALENDAR_ID.getName(), 
            new String[]{ProgramCalendar.CALENDAR_ID.getName()}, 
            new Object[]{new ForeignkeyFld(TblsEnvMonitConfig.ProgramCalendar.PROGRAM_CONFIG_ID.getName(), SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName()),
                new ForeignkeyFld(TblsEnvMonitConfig.ProgramCalendar.PROGRAM_CONFIG_VERSION.getName(), SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_VERSION.getName())
            }, "program_calendar table"),
        PROGRAM_CALENDAR_DATE(null, "program_calendar_date", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProgramCalendarDate.values(), ProgramCalendarDate.CALENDAR_ID.getName(), 
            new String[]{ProgramCalendarDate.CALENDAR_ID.getName()}, 
            new Object[]{new ForeignkeyFld(TblsEnvMonitConfig.ProgramCalendarDate.PROGRAM_CONFIG_ID.getName(), SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName()),
                new ForeignkeyFld(TblsEnvMonitConfig.ProgramCalendarDate.PROGRAM_CONFIG_VERSION.getName(), SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_VERSION.getName())
            }, "ProgramCalendarDate table"),
        PROGRAM_CALENDAR_RECURSIVE_ENTRY(null, "program_calendar_recursive_entry", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProgramCalendarRecursiveEntries.values(), ProgramCalendarRecursiveEntries.ID.getName(), 
            new String[]{ProgramCalendarRecursiveEntries.ID.getName()}, 
            new Object[]{new ForeignkeyFld(TblsEnvMonitConfig.ProgramCalendarRecursiveEntries.PROGRAM_CONFIG_ID.getName(), SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName()),
                new ForeignkeyFld(TblsEnvMonitConfig.ProgramCalendarRecursiveEntries.PROGRAM_CONFIG_VERSION.getName(), SCHEMA_NAME, TablesEnvMonitConfig.PROGRAM.getTableName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_VERSION.getName())
            }, "program_calendar_recursive_entry table"),
        MICROORGANISM(null, "microorganism", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, MicroOrganism.values(), null, new String[]{MicroOrganism.NAME.getName()}, null, "program table"),
        MICROORGANISM_ADHOC(null, "microorganism_adhoc", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, MicroOrganismAdhoc.values(), null, new String[]{MicroOrganismAdhoc.NAME.getName()}, null, "program table"),
        INSTRUMENT_INCUBATOR(null, "instrument_incubator", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, InstrIncubator.values(), null, new String[]{InstrIncubator.NAME.getName()}, null, "instrument_incubator table"),
        INCUB_BATCH(null, "incub_batch", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, IncubBatch.values(), null, new String[]{IncubBatch.INCUB_BATCH_CONFIG_ID.getName(), IncubBatch.INCUB_BATCH_VERSION.getName()}, null, "incub_batch table"),
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

    
    public enum ViewsEnvMonConfig implements EnumIntViews{
        PROG_SCHED_LOCATIONS_VIEW(" select  dpr.sample_config_code, dpr.sample_config_code_version, "+
                "         cnfpcd.*, dpl.area, dpl.spec_code, dpl.spec_variation_name, dpl.spec_analysis_variation, dpl.spec_code_version, dpl.requires_person_ana, dpl.person_ana_definition "+
                "   from #SCHEMA_CONFIG.program_calendar_date  cnfpcd"+
                "  inner join #SCHEMA_CONFIG.program  dpr on dpr.name=cnfpcd.program_id "+
                "  inner join #SCHEMA_CONFIG.program_location dpl on dpl.program_name=cnfpcd.program_id and dpl.location_name=cnfpcd.location_name;"+
                "ALTER VIEW  #SCHEMA.#TBL  OWNER TO #OWNER;",
            null, "pr_scheduled_locations", SCHEMA_NAME, true, TblsEnvMonitConfig.ViewProgramScheduledLocations.values(), "pr_scheduled_locations"),
        ;
        private ViewsEnvMonConfig(String viewScript, FldBusinessRules[] fldBusRules, String dbVwName, String repositoryName, Boolean isProcedure, EnumIntViewFields[] vwFlds, 
                String comment){
            this.getTblBusinessRules=fldBusRules;
            this.viewName=dbVwName;
            this.viewFields=vwFlds;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.viewComment=comment;
            this.viewScript=viewScript;
        }
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public String getViewCreatecript() {return this.viewScript;}
        @Override        public String getViewName() {return this.viewName;}
        @Override        public EnumIntViewFields[] getViewFields() {return this.viewFields;}
        @Override        public String getViewComment() {return this.viewComment;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String viewName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final EnumIntViewFields[] viewFields;
        private final String viewComment;
        private final String viewScript;
    }

    public enum Program implements EnumIntTableFields{
        NAME("name",  LPDatabase.stringNotNull(100), null, null, null, null), 
        PROGRAM_CONFIG_ID("program_config_id", LPDatabase.integerNotNull(), null, null, null, null), 
        PROGRAM_CONFIG_VERSION("program_config_version", LPDatabase.integerNotNull(), null, null, null, null), 
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.stringNotNull(200), null, null, null, null), 
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null), 
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null), 
        SPEC_CODE("spec_code", LPDatabase.stringNotNull(), null, null, null, null), 
        SPEC_CONFIG_VERSION("spec_config_version", LPDatabase.integerNotNull(), null, null, null, null), 
        SAMPLE_CONFIG_CODE("sample_config_code", LPDatabase.stringNotNull(), null, null, null, null), 
        SAMPLE_CONFIG_CODE_VERSION("sample_config_code_version", LPDatabase.integerNotNull(), null, null, null, null), 
        MAP_IMAGE("map_image", LPDatabase.string(), null, null, null, null), 
        DESCRIPTION_EN("description_en", LPDatabase.string(), null, null, null, null), 
        DESCRIPTION_ES("description_es", LPDatabase.string(), null, null, null, null), 
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
        PROGRAM_CONFIG_ID("program_config_id", LPDatabase.integerNotNull(), null, null, null, null), 
        PROGRAM_CONFIG_VERSION("program_config_version", LPDatabase.integerNotNull(), null, null, null, null),             
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
        REQ_SAMPLING_END("requires_tracking_sampling_end",  LPDatabase.booleanFld(), null, null, null, null),         
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
        PROGRAM_CONFIG_ID("program_config_id", LPDatabase.integerNotNull(), null, null, null, null), 
        PROGRAM_CONFIG_VERSION("program_config_version", LPDatabase.integerNotNull(), null, null, null, null), 
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
        PROGRAM_CONFIG_ID("program_config_id", LPDatabase.integerNotNull(), null, null, null, null), 
        PROGRAM_CONFIG_VERSION("program_config_version", LPDatabase.integerNotNull(), null, null, null, null), 
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
        PROGRAM_CONFIG_ID("program_config_id", LPDatabase.integerNotNull(), null, null, null, null), 
        PROGRAM_CONFIG_VERSION("program_config_version", LPDatabase.integerNotNull(), null, null, null, null), 
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
    public enum ViewProgramScheduledLocations implements EnumIntViewFields{
        SAMPLE_CONFIG_CODE("sample_config_code", "dpr.sample_config_code", Program.SAMPLE_CONFIG_CODE, null, null, null),
        SAMPLE_CONFIG_CODE_VERSION("sample_config_code_version", "dpr.sample_config_code_version", Program.SAMPLE_CONFIG_CODE_VERSION, null, null, null),
        PROGRAM_NAME("program_name", "cnfpcd.program_name as program_name", Program.NAME, null, null, null),
        PROGRAM_DAY_ID("program_day_id", "cnfpcd.id as program_day_id", TblsEnvMonitConfig.ProgramCalendarDate.CALENDAR_ID, null, null, null),
        PROGRAM_DAY_DATE("program_day_date", "cnfpcd.date as program_day_date", TblsEnvMonitConfig.ProgramCalendarDate.DATE, null, null, null),
        AREA("area", "dpl.area", TblsEnvMonitConfig.ProgramLocation.AREA, null, null, null),
        SPEC_CODE("spec_code", "dpl.spec_code", TblsEnvMonitConfig.ProgramLocation.SPEC_CODE, null, null, null),
        SPEC_CODE_VERSION("spec_code_version", "dpl.spec_code_version", TblsEnvMonitConfig.ProgramLocation.SPEC_CODE_VERSION, null, null, null),
        SPEC_VARIATION_NAME("spec_variation_name", "dpl.spec_variation_name", TblsEnvMonitConfig.ProgramLocation.SPEC_VARIATION_NAME, null, null, null),
        SPEC_ANALYSIS_VARIATION("spec_analysis_variation", "dpl.spec_analysis_variation", TblsEnvMonitConfig.ProgramLocation.SPEC_ANALYSIS_VARIATION, null, null, null),
        REQUIRES_PERSON_ANA("requires_person_ana", "dpl.requires_person_ana", TblsEnvMonitConfig.ProgramLocation.REQUIRES_PERSON_ANA, null, null, null),
        PERSON_ANA_DEFINITION("person_ana_definition", "dpl.person_ana_definition", TblsEnvMonitConfig.ProgramLocation.PERSON_ANA_DEFINITION, null, null, null),
        LOCATION_NAME("location_name", "cnfpcd.location_name", TblsEnvMonitConfig.ProgramLocation.LOCATION_NAME, null, null, null),
        ID("id", "cnfpcd.id", TblsEnvMonitConfig.ProgramCalendarDate.CALENDAR_ID, null, null, null),
        PROGRAM_CONFIG_ID("program_config_id", "cnfpcd.program_config_id", TblsEnvMonitConfig.ProgramCalendarDate.PROGRAM_CONFIG_ID, null, null, null),
        DATE("date", "cnfpcd.date", TblsEnvMonitConfig.ProgramCalendarDate.DATE, null, null, null),
        ;
        private ViewProgramScheduledLocations(String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
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
