package module.inventorytrack.definition;


import databases.SqlStatementEnums.JOIN_TYPES;
import databases.TblsCnfg;
import databases.TblsCnfg.TablesConfig;
import lbplanet.utilities.LPDatabase;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntTablesJoin;
import trazit.enums.EnumIntViewFields;
import trazit.enums.EnumIntViews;
import trazit.enums.FldBusinessRules;
import trazit.enums.ForeignkeyFld;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;

public class TblsInvTrackingConfig {
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.CONFIG.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesInvTrackingConfig implements EnumIntTables{        
        INV_CATEGORY(null, "inv_category", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Category.values(), null, new String[]{Category.NAME.getName()}, null, "Category table"),
        INV_REFERENCE(null, "inv_reference", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Reference.values(), null, new String[]{Reference.NAME.getName(), Reference.CATEGORY.getName()}, null, "Reference table"),

        INSTRUMENT_INCUBATOR(null, "instrument_incubator", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, InstrIncubator.values(), null, new String[]{InstrIncubator.NAME.getName()}, null, "instrument_incubator table"),
        INCUB_BATCH(null, "incub_batch", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, IncubBatch.values(), null, new String[]{IncubBatch.INCUB_BATCH_CONFIG_ID.getName(), IncubBatch.INCUB_BATCH_VERSION.getName()}, null, "incub_batch table"),
        PROGRAM(null, "program", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Program.values(), null, new String[]{Program.PROGRAM_CONFIG_ID.getName(), Program.PROGRAM_CONFIG_VERSION.getName()}, null, "program table"),
        PROGRAM_LOCATION(null, "program_location", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProgramLocation.values(), null, 
            new String[]{ProgramLocation.PROGRAM_NAME.getName(), ProgramLocation.LOCATION_NAME.getName(), ProgramLocation.AREA.getName()}, 
            //new Object[]{new ForeignkeyFld(ProgramLocation.PROGRAM_NAME.getName(), 
            //        SCHEMA_NAME, TablesInvTrackingConfig.PROGRAM.getTableName(), TblsInvTrackingConfig.Program.NAME.getName()
            //)}
            new Object[]{new ForeignkeyFld(TblsInvTrackingConfig.ProgramLocation.PROGRAM_CONFIG_ID.getName(), SCHEMA_NAME, TablesInvTrackingConfig.PROGRAM.getTableName(), TblsInvTrackingConfig.Program.PROGRAM_CONFIG_ID.getName()),
                new ForeignkeyFld(TblsInvTrackingConfig.ProgramLocation.PROGRAM_CONFIG_VERSION.getName(), SCHEMA_NAME, TablesInvTrackingConfig.PROGRAM.getTableName(), TblsInvTrackingConfig.Program.PROGRAM_CONFIG_VERSION.getName())
            },            
            "program locations table"),
        PROGRAM_DAY(null, "program_day", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProgramDay.values(), null, 
            new String[]{ProgramDay.PROGRAM_CONFIG_ID.getName(), ProgramDay.PROGRAM_CONFIG_VERSION.getName()}, 
            new Object[]{new ForeignkeyFld(TblsInvTrackingConfig.ProgramDay.PROGRAM_CONFIG_ID.getName(), SCHEMA_NAME, TablesInvTrackingConfig.PROGRAM.getTableName(), TblsInvTrackingConfig.Program.PROGRAM_CONFIG_ID.getName()),
                new ForeignkeyFld(TblsInvTrackingConfig.ProgramDay.PROGRAM_CONFIG_VERSION.getName(), SCHEMA_NAME, TablesInvTrackingConfig.PROGRAM.getTableName(), TblsInvTrackingConfig.Program.PROGRAM_CONFIG_VERSION.getName())
            }, "program_day table"),
        PROGRAM_CALENDAR(null, "program_calendar", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProgramCalendar.values(), ProgramCalendar.CALENDAR_ID.getName(), 
            new String[]{ProgramCalendar.CALENDAR_ID.getName()}, 
            new Object[]{new ForeignkeyFld(TblsInvTrackingConfig.ProgramCalendar.PROGRAM_CONFIG_ID.getName(), SCHEMA_NAME, TablesInvTrackingConfig.PROGRAM.getTableName(), TblsInvTrackingConfig.Program.PROGRAM_CONFIG_ID.getName()),
                new ForeignkeyFld(TblsInvTrackingConfig.ProgramCalendar.PROGRAM_CONFIG_VERSION.getName(), SCHEMA_NAME, TablesInvTrackingConfig.PROGRAM.getTableName(), TblsInvTrackingConfig.Program.PROGRAM_CONFIG_VERSION.getName())
            }, "program_calendar table"),
        PROGRAM_CALENDAR_DATE(null, "program_calendar_date", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProgramCalendarDate.values(), ProgramCalendarDate.CALENDAR_ID.getName(), 
            new String[]{ProgramCalendarDate.CALENDAR_ID.getName()}, 
            new Object[]{new ForeignkeyFld(TblsInvTrackingConfig.ProgramCalendarDate.PROGRAM_CONFIG_ID.getName(), SCHEMA_NAME, TablesInvTrackingConfig.PROGRAM.getTableName(), TblsInvTrackingConfig.Program.PROGRAM_CONFIG_ID.getName()),
                new ForeignkeyFld(TblsInvTrackingConfig.ProgramCalendarDate.PROGRAM_CONFIG_VERSION.getName(), SCHEMA_NAME, TablesInvTrackingConfig.PROGRAM.getTableName(), TblsInvTrackingConfig.Program.PROGRAM_CONFIG_VERSION.getName())
            }, "ProgramCalendarDate table"),
        PROGRAM_CALENDAR_RECURSIVE_ENTRY(null, "program_calendar_recursive_entry", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProgramCalendarRecursiveEntries.values(), ProgramCalendarRecursiveEntries.ID.getName(), 
            new String[]{ProgramCalendarRecursiveEntries.ID.getName()}, 
            new Object[]{new ForeignkeyFld(TblsInvTrackingConfig.ProgramCalendarRecursiveEntries.PROGRAM_CONFIG_ID.getName(), SCHEMA_NAME, TablesInvTrackingConfig.PROGRAM.getTableName(), TblsInvTrackingConfig.Program.PROGRAM_CONFIG_ID.getName()),
                new ForeignkeyFld(TblsInvTrackingConfig.ProgramCalendarRecursiveEntries.PROGRAM_CONFIG_VERSION.getName(), SCHEMA_NAME, TablesInvTrackingConfig.PROGRAM.getTableName(), TblsInvTrackingConfig.Program.PROGRAM_CONFIG_VERSION.getName())
            }, "program_calendar_recursive_entry table"),
        ;
        private TablesInvTrackingConfig(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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

    
    public enum ViewsInvTrackingConfig implements EnumIntViews{
        PROG_SCHED_LOCATIONS_VIEW(" select  dpr.sample_config_code, dpr.sample_config_code_version, "+
                "         cnfpcd.*, dpl.area, dpl.spec_code, dpl.spec_variation_name, dpl.spec_analysis_variation, dpl.spec_code_version, dpl.requires_person_ana, dpl.person_ana_definition "+
                "   from #SCHEMA_CONFIG.program_calendar_date  cnfpcd"+
                "  inner join #SCHEMA_CONFIG.program  dpr on dpr.name=cnfpcd.program_id "+
                "  inner join #SCHEMA_CONFIG.program_location dpl on dpl.program_name=cnfpcd.program_id and dpl.location_name=cnfpcd.location_name;"+
                "ALTER VIEW  #SCHEMA.#TBL  OWNER TO #OWNER;",
            null, "pr_scheduled_locations", SCHEMA_NAME, true, TblsInvTrackingConfig.ViewProgramScheduledLocations.values(), "pr_scheduled_locations", 
        new EnumIntTablesJoin[]{
            new EnumIntTablesJoin(TablesInvTrackingConfig.PROGRAM, "dpr", TablesInvTrackingConfig.PROGRAM_CALENDAR_DATE, "cnfpcd", true,
                new EnumIntTableFields[][]{{TblsInvTrackingConfig.Program.NAME, TblsInvTrackingConfig.ProgramCalendarDate.PROGRAM_CONFIG_ID}
                },"", JOIN_TYPES.INNER),
            new EnumIntTablesJoin(TablesInvTrackingConfig.PROGRAM_CALENDAR_DATE, "cnfpcd", TablesInvTrackingConfig.PROGRAM_LOCATION, "dpl", true,
                new EnumIntTableFields[][]{{TblsInvTrackingConfig.ProgramCalendarDate.PROGRAM_CONFIG_ID, TblsInvTrackingConfig.ProgramLocation.PROGRAM_NAME},
                        {TblsInvTrackingConfig.ProgramCalendarDate.LOCATION_NAME, TblsInvTrackingConfig.ProgramLocation.LOCATION_NAME}
                }, "", JOIN_TYPES.INNER),
        }
                , null),
        ;
        private ViewsInvTrackingConfig(String viewScript, FldBusinessRules[] fldBusRules, String dbVwName, String repositoryName, Boolean isProcedure, EnumIntViewFields[] vwFlds, 
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
        @Override        public String getExtraFilters() {return this.extraFilters;}

        
        private final EnumIntTablesJoin[] tablesInTheView;
        @Override  public EnumIntTablesJoin[] getTablesRequiredInView() {return this.tablesInTheView;}        
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String viewName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final EnumIntViewFields[] viewFields;
        private final String viewComment;
        private final String viewScript;
        private final String extraFilters;
    }

    public enum Category implements EnumIntTableFields{
        NAME("name",LPDatabase.stringNotNull(), null, null, null, null),
        ;        
        private Category(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum Reference implements EnumIntTableFields{
        NAME("name",LPDatabase.stringNotNull(), null, null, null, null),
        ACTIVE("active",LPDatabase.booleanFld(false), null, null, null, null),
        CATEGORY("category",LPDatabase.stringNotNull(), null, new ReferenceFld(SCHEMA_NAME, TablesInvTrackingConfig.INV_CATEGORY.getTableName() , Category.NAME.getName()), null, null),
        LOT_REQUIRES_QUALIF("lot_requires_qualif",LPDatabase.booleanFld(false), null, null, null, null),
        MIN_STOCK("min_stock",LPDatabase.real(), null, null, null, null),
        MIN_STOCK_UOM("min_stock_uom",LPDatabase.string(), null, new ReferenceFld(SCHEMA_NAME, TablesConfig.UOM.getTableName() , TblsCnfg.UnitsOfMeasurement.NAME.getName()), null, null),                
        ALLOWED_UOMS("allowed_uoms",LPDatabase.string(), null, null, "para limitar las unidades, el sistema hace conversiones siempre entre unidades de una misma familia, ALL para permitir cualquier unidad de medida", null),        
        MIN_STOCK_TYPE("min_stock_type",LPDatabase.string(), null, new ReferenceFld("ITEMS|VOLUME"), "ITEMS/VOLUME. ITEMS means number of available lots, VOLUME means the total quantity from all available lots", null),
        ALLOW_PROC_DISCOUNTS("allow_proc_discounts",LPDatabase.booleanFld(false), null, null, null, null),
        PROCESSES_LIST("processes_list",LPDatabase.string(), null, null, null, null),
        REQUIRES_AVAILABLES_FOR_USE("requires_availables_for_use",LPDatabase.booleanFld(false), null, null, null, null),
        MIN_AVAILABLES_FOR_USE("min_availables_for_use",LPDatabase.real(), null, null, null, null),
        MIN_AVAILABLES_FOR_USE_TYPE("min_availables_for_use_type",LPDatabase.string(), null, new ReferenceFld("ITEMS|VOLUME"), "ITEMS/VOLUME. ITEMS means number of available lots, VOLUME means the total quantity from all available lots", null),
        ALLOW_OPENING_SOME_AT_A_TIME("allow_opening_some_at_a_time",LPDatabase.booleanFld(true), null, null, null, null),
        
        CREATED_BY("created_by",LPDatabase.stringNotNull(), null, null, null, null),
        CREATED_ON("created_on",LPDatabase.stringNotNull(), null, null, null, null),
        ;        
        private Reference(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum InstrIncubator implements EnumIntTableFields{
        NAME("name",  LPDatabase.stringNotNull(), null, null, null, null),
        DESCRIPTION(LPDatabase.FIELDS_NAMES_DESCRIPTION, LPDatabase.string(200), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY,  LPDatabase.string(200), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanNotNull(Boolean.TRUE), null, null, null, null),
        LAST_DEACTIVATION_ON("last_deactivation_on", LPDatabase.dateTime(), null, null, null, null),
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
        // ** Not in use, it is present in the data repository, not in config
            //STAGE("stage", LPDatabase.stringNotNull(), null, null, null, null),
        // ** Not in use, it is present in the data repository, not in config
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
        PROGRAM_DAY_ID("program_day_id", "cnfpcd.id as program_day_id", TblsInvTrackingConfig.ProgramCalendarDate.CALENDAR_ID, null, null, null),
        PROGRAM_DAY_DATE("program_day_date", "cnfpcd.date as program_day_date", TblsInvTrackingConfig.ProgramCalendarDate.DATE, null, null, null),
        AREA("area", "dpl.area", TblsInvTrackingConfig.ProgramLocation.AREA, null, null, null),
        SPEC_CODE("spec_code", "dpl.spec_code", TblsInvTrackingConfig.ProgramLocation.SPEC_CODE, null, null, null),
        SPEC_CODE_VERSION("spec_code_version", "dpl.spec_code_version", TblsInvTrackingConfig.ProgramLocation.SPEC_CODE_VERSION, null, null, null),
        SPEC_VARIATION_NAME("spec_variation_name", "dpl.spec_variation_name", TblsInvTrackingConfig.ProgramLocation.SPEC_VARIATION_NAME, null, null, null),
        SPEC_ANALYSIS_VARIATION("spec_analysis_variation", "dpl.spec_analysis_variation", TblsInvTrackingConfig.ProgramLocation.SPEC_ANALYSIS_VARIATION, null, null, null),
        REQUIRES_PERSON_ANA("requires_person_ana", "dpl.requires_person_ana", TblsInvTrackingConfig.ProgramLocation.REQUIRES_PERSON_ANA, null, null, null),
        PERSON_ANA_DEFINITION("person_ana_definition", "dpl.person_ana_definition", TblsInvTrackingConfig.ProgramLocation.PERSON_ANA_DEFINITION, null, null, null),
        LOCATION_NAME("location_name", "cnfpcd.location_name", TblsInvTrackingConfig.ProgramLocation.LOCATION_NAME, null, null, null),
        ID("id", "cnfpcd.id", TblsInvTrackingConfig.ProgramCalendarDate.CALENDAR_ID, null, null, null),
        PROGRAM_CONFIG_ID("program_config_id", "cnfpcd.program_config_id", TblsInvTrackingConfig.ProgramCalendarDate.PROGRAM_CONFIG_ID, null, null, null),
        DATE("date", "cnfpcd.date", TblsInvTrackingConfig.ProgramCalendarDate.DATE, null, null, null),
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
