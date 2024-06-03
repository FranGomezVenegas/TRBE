/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.projectrnd.definition;

import databases.TblsAppConfig;
import databases.TblsData;
import lbplanet.utilities.LPDatabase;
import module.instrumentsmanagement.definition.TblsInstrumentsData;
import module.methodvalidation.definition.TblsMethodValidationData;
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
public class TblsProjectRnDData {
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesProjectRnDData implements EnumIntTables{
        PROJECT(null, "project", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Project.values(), 
            null, new String[]{Project.NAME.getName()}, null, "project"),
        PROJECT_ATTACHMENT(null, "project_attachment", SCHEMA_NAME, true, ProjectAttachments.values(), ProjectAttachments.ID.getName(),
            new String[]{ProjectAttachments.ID.getName()}, null, "ProjectAttachments"),        
        PROJECT_NOTES(null, "project_notes", SCHEMA_NAME, true, ProjectNotes.values(), ProjectNotes.ID.getName(),
            new String[]{ProjectNotes.ID.getName()}, null, "ProjectNotes"),        
        RD_DAILY_ENTRY(null, "rd_daily_entry", SCHEMA_NAME, true, RdDailyEntry.values(), null,
            new String[]{RdDailyEntry.NAME.getName()}, null, "rd_daily_entry"),        
        SAMPLE(null, "sample", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsMethodValidationData.Sample.values(), TblsMethodValidationData.Sample.SAMPLE_ID.getName()
            , new String[]{TblsMethodValidationData.Sample.SAMPLE_ID.getName()}, null, "sample table"),
        METHOD_DEVELOPMENT_SEQUENCE(null, "method_development_sequence", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, MethodDevelopmentSequence.values(), 
            null, new String[]{MethodDevelopmentSequence.NAME.getName()}, null, ""),
        ;
        private TablesProjectRnDData(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
        ;
        private ViewsInstrumentsData(String viewScript, FldBusinessRules[] fldBusRules, String dbVwName, String repositoryName, Boolean isProcedure, EnumIntViewFields[] vwFlds, 
                String comment, EnumIntTablesJoin[] tablesInView, String extraFilters, Boolean useFixViewScript){
            this.getTblBusinessRules=fldBusRules;
            this.viewName=dbVwName;
            this.viewFields=vwFlds;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.viewComment=comment;
            this.viewScript=viewScript;
            this.tablesInTheView=tablesInView;
            this.extraFilters=extraFilters;
            this.useFixViewScript=useFixViewScript;
        }
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public String getViewCreatecript() {return this.viewScript;}
        @Override        public String getViewName() {return this.viewName;}
        @Override        public EnumIntViewFields[] getViewFields() {return this.viewFields;}
        @Override        public String getViewComment() {return this.viewComment;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        @Override        public Boolean getUsesFixScriptView() {return this.useFixViewScript;}
        private final Boolean useFixViewScript;
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
    
    public enum Project implements EnumIntTableFields{
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null, true),
        TYPE("type", LPDatabase.stringNotNull(), null, null, null, null, true),
        PURPOSE("purpose", LPDatabase.string(), null, null, null, null, false),
        CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null, true),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null, true),
        IS_OPEN("is_open", LPDatabase.booleanFld(true), null, null, null, null, true),
        IS_LOCKED("is_locked", LPDatabase.booleanFld(false), null, null, null, null, true),
        LOCKED_REASON(TblsInstrumentsData.Instruments.LOCKED_REASON.getName(),TblsInstrumentsData.Instruments.LOCKED_REASON.getFieldType(), null, null, null, null, true),
        RESPONSIBLE("responsible", LPDatabase.string(), null, null, null, null, true)
        ;
        private Project(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules, Boolean isSystFld){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
            this.isSystemFld=isSystFld;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;
        private final Boolean isSystemFld;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
        @Override        public Boolean isSystemField(){return this.isSystemFld;}
    }        

    public enum ProjectAttachments implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        PROJECT_NAME("project_name", LPDatabase.string(), null, null, null, null),
        DAILY_ENTRY_NAME("daily_entry_name", LPDatabase.string(), null, null, null, null),
        FORMULA_NAME("formula_name", LPDatabase.string(), null, null, null, null),
        PARAMETER_NAME("parameter_name", LPDatabase.string(), null, null, null, null),        
        SAMPLE_ID("sample_id", LPDatabase.integer(), null, null, null, null),        
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        FILE_LINK("file_link", LPDatabase.string(), null, null, null, null),
        DB_FILE("db_file", LPDatabase.embeddedFile(), null, null, null, null),
        AWS_FILE("aws_file", LPDatabase.string(), null, null, null, null),
        ORIGINAL_FILE_NAME("original_file_name", LPDatabase.string(), null, null, null, null),
        BRIEF_SUMMARY("brief_summary", LPDatabase.string(), null, null, null, null),
        REMOVED("removed", LPDatabase.booleanFld(false), null, null, null, null),
        ;
        private ProjectAttachments(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum ProjectNotes implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        PROJECT_NAME("project_name", LPDatabase.string(), null, null, null, null),
        DAILY_ENTRY_NAME("daily_entry_name", LPDatabase.string(), null, null, null, null),
        FORMULA_NAME("formula_name", LPDatabase.string(), null, null, null, null),
        PARAMETER_NAME("parameter_name", LPDatabase.string(), null, null, null, null),        
        SAMPLE_ID("sample_id", LPDatabase.integer(), null, null, null, null),        
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        FILE_LINK("file_link", LPDatabase.string(), null, null, null, null),
        NOTES("notes", LPDatabase.string(), null, null, null, null),
        REMOVED("removed", LPDatabase.booleanFld(false), null, null, null, null),
        ;
        private ProjectNotes(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum RdDailyEntry implements EnumIntTableFields{
        PROJECT("project", LPDatabase.stringNotNull(), null, null, null, null, true),
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null, true),
        PURPOSE("purpose", LPDatabase.string(), null, null, null, null, false),
        CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null, true),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null, true),
        IS_OPEN("is_open", LPDatabase.booleanFld(true), null, null, null, null, true),
        RESPONSIBLE("responsible", LPDatabase.string(), null, null, null, null, true),
        IS_LOCKED(TblsInstrumentsData.Instruments.IS_LOCKED.getName(),TblsInstrumentsData.Instruments.IS_LOCKED.getFieldType(), null, null, null, null, true),
        LOCKED_REASON(TblsInstrumentsData.Instruments.LOCKED_REASON.getName(),TblsInstrumentsData.Instruments.LOCKED_REASON.getFieldType(), null, null, null, null, true),
        LINKED_SEQUENCE_ANALYSIS_LIST("linked_sequence_analysis_list", LPDatabase.string(), null, null, null, null, true),
        LINKED_FORMULATION_LIST("linked_formulation_list", LPDatabase.string(), null, null, null, null, true)
        ;
        private RdDailyEntry(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules, Boolean isSystFld){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
            this.isSystemFld=isSystFld;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;
        private final Boolean isSystemFld;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
        @Override        public Boolean isSystemField(){return this.isSystemFld;}
    }        
    public enum MethodDevelopmentSequence implements EnumIntTableFields{
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null, true),
        ANALYTICAL_PURPOSE("analytical_purpose", LPDatabase.string(), null, null, null, null, true),
        ANALYTICAL_PARAMETER("analytical_parameter", LPDatabase.string(), null, null, null, null, true),
        PROJECT("project", LPDatabase.stringNotNull(), null, null, null, null, true),
        PURPOSE("purpose", LPDatabase.string(), null, null, null, null, true),
        CONDITIONS("conditions", LPDatabase.string(), null, null, null, null, true),
        CONCLUSIONS("conclusions", LPDatabase.string(), null, null, null, null, true),

        CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null, true),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null, true),
        IS_OPEN("is_open", LPDatabase.booleanFld(true), null, null, null, null, true),
        IS_LOCKED("is_locked", LPDatabase.booleanFld(false), null, null, null, null, true),
        LOCKED_REASON(TblsInstrumentsData.Instruments.LOCKED_REASON.getName(),TblsInstrumentsData.Instruments.LOCKED_REASON.getFieldType(), null, null, null, null, true),
        RESPONSIBLE("responsible", LPDatabase.string(), null, null, null, null, true),
        LINKED_SEQUENCE_ANALYSIS_LIST("linked_sequence_analysis_list", LPDatabase.string(), null, null, null, null, true),
        LINKED_FORMULATION_LIST("linked_formulation_list", LPDatabase.string(), null, null, null, null, true),
        JSON_MODEL("json_model", LPDatabase.json(), null, null, null, null, true)
        ;
        private MethodDevelopmentSequence(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules, Boolean isSystFld){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
            this.isSystemFld=isSystFld;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;
        private final Boolean isSystemFld;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
        @Override        public Boolean isSystemField(){return this.isSystemFld;}
    }        

    public enum Sample implements EnumIntTableFields{
        SAMPLE_ID(TblsData.Sample.SAMPLE_ID.getName(), LPDatabase.integerNotNull(), null, null, null, null, true),
        CONFIG_CODE("sample_config_code", LPDatabase.string(), null, null, null, null, true),
        CONFIG_CODE_VERSION("sample_config_code_version", LPDatabase.integer(), null, null, null, null, true),
        STATUS("status",LPDatabase.stringNotNull(), null, null, null, null, true),
        STATUS_PREVIOUS("status_previous",LPDatabase.string(), null, null, null, null, true),
        LOGGED_ON("logged_on", LPDatabase.date(), null, null, null, null, true),
        LOGGED_BY("logged_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null, true),
        RECEIVED_ON("received_on", LPDatabase.date(), null, null, null, null, true),
        RECEIVED_BY("received_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null, true),
        VOLUME(LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.real(), null, null, null, null, true),
        VOLUME_UOM(LPDatabase.FIELDS_NAMES_VOLUME_UOM,LPDatabase.string(), null, null, null, null, true),
        ALIQUOTED("aliquoted", LPDatabase.booleanFld(false), null, null, null, null, true),
        ALIQUOT_STATUS("aliq_status",LPDatabase.string(), null, null, null, null, true),
        VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.real(), null, null, null, null, true),
        VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom",LPDatabase.string(), null, null, null, null, true),
        SPEC_CODE("spec_code",LPDatabase.stringNotNull(), null, null, null, null, false),
        SPEC_CODE_VERSION("spec_code_version", LPDatabase.integer(), null, null, null, null, false),
        SPEC_VARIATION_NAME("spec_variation_name",LPDatabase.stringNotNull(), null, null, null, null, false),
        SPEC_ANALYSIS_VARIATION("spec_analysis_variation", LPDatabase.stringNotNull(), null, null, null, null, false),
        SPEC_EVAL("spec_eval",  LPDatabase.string(), null, null, null, null, true),
        CUSTODIAN("custodian",  LPDatabase.string(2), null, null, null, null, true),
        CUSTODIAN_CANDIDATE("custodian_candidate",  LPDatabase.string(2), null, null, null, null, true),
        COC_REQUESTED_ON("coc_requested_on", LPDatabase.date(), null, null, null, null, true),
        COC_CONFIRMED_ON("coc_confirmed_on", LPDatabase.date()
            , null, null, null, null, true),
        PROJECT("project", LPDatabase.stringNotNull(), null, null, null, null, true),
        RD_DAILY_ENTRY("rd_daily_entry",  LPDatabase.string(), null, null, null, null, true),
        REVIEWER("reviewer",LPDatabase.string(), null, null, null, null, true),
        REVIEWED("reviewed", LPDatabase.booleanFld(), null, null, null, null, true), 
        REVIEWED_BY("reviewed_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null, true), 
        REVIEWED_ON("reviewed_on", LPDatabase.dateTime(), null, null, null, null, true),
        ;
        private Sample(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules, Boolean isSystFld){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
            this.isSystemFld=isSystFld;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;
        private final Boolean isSystemFld;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
        @Override        public Boolean isSystemField(){return this.isSystemFld;}
    }            
    
}
