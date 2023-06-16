/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import databases.SqlStatementEnums.JOIN_TYPES;
import databases.TblsApp.TablesApp;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import static lbplanet.utilities.LPDatabase.dateTime;
import static lbplanet.utilities.LPDatabase.dateTimeWithDefaultNow;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntTablesJoin;
import trazit.enums.EnumIntViewFields;
import trazit.enums.EnumIntViews;
import trazit.enums.FldBusinessRules;
import trazit.enums.ForeignkeyFld;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class TblsProcedure {

    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.PROCEDURE.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesProcedure implements EnumIntTables{
        INVESTIGATION(null, "investigation", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Investigation.values(), TblsProcedure.Investigation.ID.getName(),
            new String[]{TblsProcedure.Investigation.ID.getName()}, null, "Investigation objects"),
        INVEST_OBJECTS(null, "invest_objects", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, InvestObjects.values(), TblsProcedure.InvestObjects.ID.getName(),
            new String[]{TblsProcedure.InvestObjects.ID.getName()}, null, "Objects added to one given investigation"),
        PERSON_PROFILE(null, "person_profile", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, PersonProfile.values(), null,
            new String[]{TblsProcedure.PersonProfile.PERSON_NAME.getName(), TblsProcedure.PersonProfile.ROLE_NAME.getName()}, null, "Objects added to one given investigation"),
        PROCEDURE_INFO(null, "procedure_info", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProcedureInfo.values(), null,
            new String[]{TblsProcedure.ProcedureInfo.NAME.getName()}, null, "Procedure Info"),
        PROCEDURE_BUSINESS_RULE(null, "procedure_business_rules", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProcedureBusinessRules.values(), null,
            new String[]{TblsProcedure.ProcedureBusinessRules.AREA.getName(), ProcedureBusinessRules.RULE_NAME.getName()}, null, "Procedure Business Rules Info"),
        PROCEDURE_EVENTS(null, "procedure_events", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProcedureEvents.values(), null,
            new String[]{TblsProcedure.ProcedureEvents.NAME.getName(), ProcedureEvents.ROLE_NAME.getName()}, null, "Procedure Events Info"),
        PROCEDURE_EVENTS_HELP_CONTENT(null, "procedure_events_help_content", SCHEMA_NAME, false, ProcedureEventsHelpContent.values(), null,
                new String[]{ProcedureEventsHelpContent.LP_FRONTEND_PAGE_NAME.getName(), ProcedureEventsHelpContent.LP_FRONTEND_PAGE_FILTER.getName(), ProcedureEventsHelpContent.ORDER_NUMBER.getName()},
                new Object[]{new ForeignkeyFld(ProcedureEventsHelpContent.LP_FRONTEND_PAGE_NAME.getName(), SCHEMA_NAME, TablesProcedure.PROCEDURE_EVENTS.getTableName(), ProcedureEvents.LP_FRONTEND_PAGE_NAME.getName()),
                    new ForeignkeyFld(ProcedureEventsHelpContent.LP_FRONTEND_PAGE_FILTER.getName(), SCHEMA_NAME, TablesProcedure.PROCEDURE_EVENTS.getTableName(), ProcedureEvents.LP_FRONTEND_PAGE_FILTER.getName())
                },"procedure_events_help_content"),
        PROGRAM_CORRECTIVE_ACTION(null, "program_corrective_action", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsProcedure.ProgramCorrectiveAction.values(), TblsProcedure.ProgramCorrectiveAction.ID.getName(),
            new String[]{TblsProcedure.ProgramCorrectiveAction.ID.getName()}, null, "Program Corrective Action for results OOS and/or OOC Info"),
        SAMPLE_STAGE_TIMING_CAPTURE(null, "sample_stage_timing_capture", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, SampleStageTimingCapture.values(), TblsProcedure.SampleStageTimingCapture.ID.getName(),
            new String[]{TblsProcedure.SampleStageTimingCapture.ID.getName()}, null, "Sample Stage Timing Capture Info"),
        SAMPLE_STAGE_TIMING_INTERVAL_DEVIATION(null, "sample_stage_timing_interval_deviation", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, SampleStageTimingIntervalDeviation.values(), TblsProcedure.SampleStageTimingIntervalDeviation.ID.getName(),
            new String[]{TblsProcedure.SampleStageTimingIntervalDeviation.ID.getName()}, null, "SampleStageTimingIntervalDeviation Info"),
        ;
        private TablesProcedure(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    
    public enum ViewsProcedure implements EnumIntViews{
        PROC_USER_AND_ROLES(" SELECT #FLDS from #SCHEMA_CONFIG.sample s " +
                "   INNER JOIN #SCHEMA_CONFIG.sample_revision_testing_group stg on stg.sample_id = s.sample_id; "+
                "ALTER VIEW  #SCHEMA_CONFIG.#TBL  OWNER TO #OWNER;",
            null, "sample_testing_group_view", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ViewProcUserAndRolesNewDef.values(), "ProcUserAndRoles", 
        new EnumIntTablesJoin[]{
            new EnumIntTablesJoin(TablesProcedure.PERSON_PROFILE, "persprof", TablesApp.USERS, "usr", true,
                new EnumIntTableFields[][]{{TblsProcedure.PersonProfile.PERSON_NAME, TblsApp.Users.PERSON_NAME}}
                ,"", JOIN_TYPES.INNER)}
                , null),        
        ;
        private ViewsProcedure(String viewScript, FldBusinessRules[] fldBusRules, String dbVwName, String repositoryName, Boolean isProcedure, EnumIntViewFields[] vwFlds, 
                String comment, EnumIntTablesJoin[] tablesInView, String extraFilters){
            this.getTblBusinessRules=fldBusRules;
            this.viewName=dbVwName;
            this.viewFields=vwFlds;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.viewComment=comment;
            this.viewScript=viewScript;
            this.tablesInTheView=tablesInView;
            this.extraFilters=extraFilters;
        }
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public String getViewCreatecript() {return this.viewScript;}
        @Override        public String getViewName() {return this.viewName;}
        @Override        public EnumIntViewFields[] getViewFields() {return this.viewFields;}
        @Override        public String getViewComment() {return this.viewComment;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        public String getExtraFilters() {return this.extraFilters;}
        
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String viewName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final EnumIntViewFields[] viewFields;
        private final String viewComment;
        private final String viewScript;
        private final EnumIntTablesJoin[] tablesInTheView;
        private final String extraFilters;
        @Override  public EnumIntTablesJoin[] getTablesRequiredInView() {return this.tablesInTheView;}
    }
    
    
    /**
     *
     */
    public static final String SCHEMATAG = "#SCHEMA";

    /**
     *
     */
    public static final String TABLETAG = "#TBL";

    /**
     *
     */
    public static final String OWNERTAG = "#OWNER";

    /**
     *
     */
    public static final String TABLESPACETAG = "#TABLESPACE";

    /**
     *
     */
    public static final String FIELDSTAG = "#FLDS";
    
    public enum PersonProfile implements EnumIntTableFields{
        PERSON_NAME("person_name", LPDatabase.stringNotNull(), null, null, null, null),
        ROLE_NAME("role_name", LPDatabase.stringNotNull(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),
        USER_TITLE("user_title", LPDatabase.string(), null, null, null, null),
        ;
        private PersonProfile(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum ProcedureEvents implements EnumIntTableFields{
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null),
        ROLE_NAME("role_name", LPDatabase.stringNotNull(), null, null, null, null),
        MODE("mode", LPDatabase.string(), null, null, null, null),
        TYPE("type", LPDatabase.string(), null, null, null, null),
        LABEL_EN("label_en", LPDatabase.string(), null, null, null, null),
        LABEL_ES("label_es", LPDatabase.string(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, null, null),
        LP_FRONTEND_PAGE_NAME("lp_frontend_page_name", LPDatabase.string(), null, null, null, null),
        LP_FRONTEND_PAGE_FILTER("lp_frontend_page_filter", LPDatabase.string(), null, null, null, null),
        //PARENT_NAME("parent_name", LPDatabase.string(), null, null, null, null),        
        POSITION("position", LPDatabase.string(), null, null, null, null),
        SOP("sop", LPDatabase.string(), null, null, null, null),
        ESIGN_REQUIRED("esign_required", LPDatabase.booleanFld(), null, null, null, null),
        USERCONFIRM_REQUIRED("userconfirm_required", LPDatabase.booleanFld(), null, null, null, null),
        ICON_NAME("icon_name", LPDatabase.string(), null, null, null, null),
        ICON_NAME_WHENNOTCERTIF("icon_name_when_not_certified", LPDatabase.string(), null, null, null, null)
        ;
        private ProcedureEvents(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

public enum ProcedureEventsHelpContent implements EnumIntTableFields {
        LP_FRONTEND_PAGE_NAME("lp_frontend_page_name", LPDatabase.string(), null, null, null, null),
        LP_FRONTEND_PAGE_FILTER("lp_frontend_page_filter", LPDatabase.string(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, null, null),
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        FILE_LINK("file_link", LPDatabase.string(), null, null, null, null),
        IS_VIDEO("is_video", LPDatabase.booleanFld(false), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null)
        ;
        private ProcedureEventsHelpContent(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }
    
    public enum ProcedureInfo implements EnumIntTableFields{
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null),
        VERSION("version", LPDatabase.integerNotNull(), null, null, null, null),
        PROCEDURE_HASH_CODE("procedure_hash_code", LPDatabase.stringNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_NAME("module_name", LPDatabase.string(), null, null, null, null),
        LABEL_EN("label_en", LPDatabase.string(), null, null, null, null),
        LABEL_ES("label_es", LPDatabase.string(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        ENABLE_CHANGE_TRACKING("enable_change_tracking", LPDatabase.booleanFld(false), null, null, null, null),
        INCLUDE_CONFIG_CHANGES("include_config_changes", LPDatabase.booleanFld(true), null, null, null, null),
        CREATE_PICT_ONGCHNGE("create_picture_on_change", LPDatabase.booleanFld(false), null, null, null, null),
        ;
        private ProcedureInfo(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum ProcedureBusinessRules implements EnumIntTableFields{
        AREA("area", LPDatabase.stringNotNull(), null, null, null, null),
        RULE_NAME("rule_name", LPDatabase.stringNotNull(), null, null, null, null),
        RULE_VALUE("rule_value", LPDatabase.string(), null, null, null, null),
        DISABLED("disabled", LPDatabase.booleanFld(false), null, null, null, null),
        ;
        private ProcedureBusinessRules(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    // Esta no tiene sentido a este nivel porque business rules a nivel de proceso son archivos, no tabla.
    //  La que tiene sentido está en TblsReqs porque es la declaración de cuales son las reglas de negocio del proceso
    //      según se definen en el Excel.
/*    
    public enum ProcedureBusinessRules {

        ID("id", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_#ID_seq'::regclass)"),        
        TBL("procedure_business_rules",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#PROCEDURE_NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+"  TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
        PROCEDURE_NAME(FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull()),
        PROCEDURE_VERSION("procedure_version", LPDatabase.integerNotNull()),
        INSTANCE_NAME("instance_name", LPDatabase.stringNotNull()),
        MODULE_NAME("module_name", LPDatabase.stringNotNull()),
        MODULE_VERSION("module_version", LPDatabase.integerNotNull()),
        FILE_SUFFIX("file_suffix", LPDatabase.string()),
        RULE_NAME("rule_name", LPDatabase.string()),
        RULE_VALUE("rule_value", LPDatabase.string()),
        ACTIVE("active", LPDatabase.booleanFld())
        ;
        private ProcedureBusinessRules(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        public String getName(){
            return this.dbObjName;
        }

        public String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureBusinessRules.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureBusinessRules obj: ProcedureBusinessRules.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        fieldsScript.append(currField[0]).append(" ").append(currField[1]);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ProcedureBusinessRules obj: ProcedureBusinessRules.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }                
        private final String dbObjName;             
        private final String dbObjTypePostgres;           
    } 
*/
    public enum ViewProcUserAndRolesNewDef implements EnumIntViewFields{
        USER_NAME(TblsApp.Users.USER_NAME.getName(), "usr.user_name", TblsApp.Users.USER_NAME, null, null, null),

        EMAIL(TblsApp.Users.EMAIL.getName(), "usr.email", TblsApp.Users.EMAIL, null, null, null),
        ROLE_NAME(TblsProcedure.PersonProfile.ROLE_NAME.getName(), "persprof.role_name", TblsProcedure.PersonProfile.ROLE_NAME, null, null, null),
        USER_TITLE(TblsProcedure.PersonProfile.USER_TITLE.getName(), "persprof.user_title", TblsProcedure.PersonProfile.USER_TITLE, null, null, null),
        ACTIVE(TblsProcedure.PersonProfile.ACTIVE.getName(), "persprof.active", TblsProcedure.PersonProfile.ACTIVE, null, null, null)
        
        ;
        private ViewProcUserAndRolesNewDef(String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
//            try{
//            this.fldName="";
            this.fldName=name;
            this.fldAliasInView=vwAliasName;
            this.fldMask=fldMask;
            this.fldComment=comment;
            this.fldBusinessRules=busRules;
            this.fldObj=fldObj;
/*            }catch(Exception e){
                String s= e.getMessage();
                //String s2=name;
                this.fldName="";
            }*/
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
    
    public enum ViewProcUserAndRoles{

        /**
         *
         */
        TBL("proc_user_and_roles",  LPDatabase.createView() +
                " SELECT #FLDS from #SCHEMA.person_profile persprof " +
                "   INNER JOIN \"app\".users usr on usr.person_name=persprof.person_name; "+
                "ALTER VIEW  #SCHEMA.#TBL  OWNER TO #OWNER;")
        ,

        /**
         *
         */
        USER_NAME("user_name", "usr.user_name")
        ,

        /**
         *
         */
        EMAIL("email", "usr.email")
        ,

        /**
         *
         */
        ROLE_NAME("role_name", "persprof.role_name")
        ,
        /**
         *
         */
        USER_TITLE("user_title", "persprof.user_title")
        ,

        /**
         *
         */
        ACTIVE("active", "persprof.active")
        ;
        private ViewProcUserAndRoles(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return
         */
        public String getName(){
            return this.dbObjName;
        }
        private String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        /**
         *
         * @param schemaNamePrefix - Procedure Instance where it applies
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ViewProcUserAndRoles.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            //tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_CONFIG", LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ViewProcUserAndRoles obj: ViewProcUserAndRoles.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (Boolean.FALSE.equals("TBL".equalsIgnoreCase(objName))) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        fieldsScript.append(currField[1]).append(" AS ").append(currField[0]);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }      
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ViewProcUserAndRoles obj: ViewProcUserAndRoles.values()){
                String objName = obj.name();
                if (Boolean.FALSE.equals("TBL".equalsIgnoreCase(objName)))
{                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }             
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }        
    
    public enum Investigation implements EnumIntTableFields{
        ID("id", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_id_seq'::regclass)", null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, null, null, null),
        CLOSED("closed", LPDatabase.booleanFld(), null, null, null, null),
        CLOSED_ON("closed_on", LPDatabase.dateTime(), null, null, null, null),
        CLOSED_BY("closed_by", LPDatabase.string(), null, null, null, null),
        EXTERNAL_SYSTEM_ID("external_system_id", LPDatabase.string(), null, null, null, null),
        EXTERNAL_SYSTEM_NAME("external_system_name", LPDatabase.string(), null, null, null, null),
        CAPA_REQUIRED("capa_required", LPDatabase.booleanFld(), null, null, null, null),
        CAPA_DECISION_ON("capa_decision_on", LPDatabase.dateTime(), null, null, null, null),
        CAPA_DECISION_BY("capa_decision_by", LPDatabase.string(), null, null, null, null),
        CAPA_OBSERVATION("capa_observation", LPDatabase.string(), null, null, null, null),
        CAPA_EXTERNAL_SYSTEM_ID("capa_external_system_id", LPDatabase.string(), null, null, null, null),
        CAPA_EXTERNAL_SYSTEM_NAME("capa_external_system_name", LPDatabase.string(), null, null, null, null),
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

    public enum InvestObjects implements EnumIntTableFields{
        ID("id", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_id_seq'::regclass)", null, null, null, null),
        INVEST_ID("invest_id", LPDatabase.integer(), null, null, null, null),
        OBJECT_TYPE("object_type", LPDatabase.string(), null, null, null, null),
        OBJECT_NAME("object_name", LPDatabase.string(), null, null, null, null),
        OBJECT_ID("object_id", LPDatabase.integer(), null, null, null, null),
        ADDED_ON("added_on", LPDatabase.dateTime(), null, null, null, null),
        ADDED_BY("added_BY", LPDatabase.string(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        NOTES("notes", LPDatabase.string(), null, null, null, null),
        ;
        private InvestObjects(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum ProgramCorrectiveAction implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        STATUS("status", LPDatabase.stringNotNull(), null, null, null, null),
        STATUS_PREVIOUS("status_previous", LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, dateTime(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        PROGRAM_NAME("program_name", LPDatabase.string(), null, null, null, null),
        LOCATION_NAME("location_name", LPDatabase.string(), null, null, null, null),
        AREA("area", LPDatabase.string(), null, null, null, null),
        SAMPLE_ID("sample_id", LPDatabase.integer(), null, null, null, null),
        TEST_ID("test_id", LPDatabase.integer(), null, null, null, null),
        RESULT_ID("result_id", LPDatabase.integer(), null, null, null, null),
        LIMIT_ID("limit_id", LPDatabase.integer(), null, null, null, null),
        ANALYSIS("analysis", LPDatabase.stringNotNull(), null, null, null, null),
        METHOD_NAME("method_name", LPDatabase.stringNotNull(), null, null, null, null),
        METHOD_VERSION("method_version", LPDatabase.integer(), null, null, null, null),
        PARAM_NAME("param_name", LPDatabase.stringNotNull(), null, null, null, null),
        SPEC_RULE_WITH_DETAIL("spec_rule_with_detail", LPDatabase.stringNotNull(), null, null, null, null),
        SPEC_EVAL("spec_eval", LPDatabase.stringNotNull(), null, null, null, null),
        SPEC_EVAL_DETAIL("spec_eval_detail", LPDatabase.stringNotNull(), null, null, null, null),
        INVEST_ID("invest_id", LPDatabase.integer(), null, null, null, null),
        ;
        private ProgramCorrectiveAction(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum SampleStageTimingCapture implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        SAMPLE_ID("sample_id", LPDatabase.integer(), null, null, null, null),
        STAGE_CURRENT("current_stage", LPDatabase.stringNotNull(), null, null, null, null),
        STAGE_PREVIOUS("stage_previous", LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, dateTimeWithDefaultNow(), "to_char("+LPDatabase.FIELDS_NAMES_CREATED_ON+",'YYYY-MM-DD HH:MI')", null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        STARTED_ON("started_on", dateTime(), "to_char("+"started_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        ENDED_ON("ended_on", dateTime(), "to_char("+"ended_on"+",'YYYY-MM-DD HH:MI')", null, null, null),
        ;
        private SampleStageTimingCapture(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum SampleStageTimingIntervalDeviation implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        SAMPLE_ID("sample_id", LPDatabase.integerNotNull(), null, null, null, null),
        SAMPLE_CONFIG_CODE("sample_config_code", LPDatabase.stringNotNull(), null, null, null, null),
        SAMPLE_CONFIG_VERSION("sample_config_version", LPDatabase.integerNotNull(), null, null, null, null),
        STAGE("stage", LPDatabase.stringNotNull(), null, null, null, null),
        STARTED_ON("started_on", dateTime(), null, null, null, null),
        ENDED_ON("ended_on", dateTime(), null, null, null, null),
        DATERANGE_INTERVAL_SECONDS("daterange_interval_seconds", LPDatabase.integer(), null, null, null, null),
        EXPECTED_INTERVAL_SECONDS("expected_interval_seconds", LPDatabase.integer(), null, null, null, null),
        ;
        private SampleStageTimingIntervalDeviation(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
