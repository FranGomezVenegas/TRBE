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
 * @author Administrator
 */
public class TblsApp {

    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.APP.getName();
    public enum TablesApp implements EnumIntTables{
        
        USERS(null, "users", SCHEMA_NAME, true, Users.values(), null,
            new String[]{Users.USER_NAME.getName()}, null, "instance users declaration"),
        USER_PROCESS(null, "user_process", SCHEMA_NAME, true, UserProcess.values(), null,
            new String[]{UserProcess.USER_NAME.getName(), UserProcess.PROC_NAME.getName()}, null, "Processes assigned to the users"),
        APP_SESSION(null, "app_session", SCHEMA_NAME, true, AppSession.values(), AppSession.SESSION_ID.getName(),
            new String[]{AppSession.SESSION_ID.getName()}, null, "Id for any user session"),
        IP_WHITE_LIST(null, "ip_white_list", SCHEMA_NAME, true, IPWhiteList.values(), IPWhiteList.ID.getName(),
            new String[]{IPWhiteList.ID.getName()}, null, "White List, when at least one IP added then the access will be limited to those IPs (except if added to blacklist)"),
        IP_BLACK_LIST(null, "ip_black_list", SCHEMA_NAME, true, IPBlackList.values(), IPBlackList.ID.getName(),
            new String[]{IPBlackList.ID.getName()}, null, "Black List, when one IP is added to this table then it is banned, independently of be in the white list too"),
        HOLIDAYS_CALENDAR(null, "holidays_calendar", SCHEMA_NAME, true, HolidaysCalendar.values(), null,
            new String[]{HolidaysCalendar.FLD_CODE.getName()}, null, "Holiday Calendars"),
        HOLIDAYS_CALENDAR_DATE(null, "holidays_calendar_date", SCHEMA_NAME, true, HolidaysCalendarDate.values(), HolidaysCalendarDate.ID.getName(),
            new String[]{HolidaysCalendarDate.ID.getName()}, null, "Holiday Calendars dates added"),
        INCIDENT(null, "incident", SCHEMA_NAME, true, Incident.values(), Incident.ID.getName(),
            new String[]{Incident.ID.getName()}, null, "Incidents table"),
        VIDEO_TUTORIAL(null, "video_tutorial", SCHEMA_NAME, true, VideoTutorial.values(), VideoTutorial.ID.getName(),
            new String[]{VideoTutorial.ID.getName()}, null, "Video Tutorial entries table"),
        ;
        private TablesApp(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    public enum IPWhiteList implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null), 
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null), 
        ADDED_ON("added_on", LPDatabase.dateTime(), null, null, null, null),
        ADDED_BY("added_by", LPDatabase.string(), null, null, null, null), 
        IP_VALUE1("ip_value1", LPDatabase.stringNotNull(),null, null, "", null),
        IP_VALUE2("ip_value2", LPDatabase.stringNotNull(),null, null, "", null),
        IP_VALUE3("ip_value3", LPDatabase.stringNotNull(),null, null, "", null),
        IP_VALUE4("ip_value4", LPDatabase.stringNotNull(),null, null, "", null),
        IP_ENDRANGE_VALUE1("ip_endrange_value1", LPDatabase.string(),null, null, "", null),
        IP_ENDRANGE_VALUE2("ip_endrange_value2", LPDatabase.string(),null, null, "", null),
        IP_ENDRANGE_VALUE3("ip_endrange_value3", LPDatabase.string(),null, null, "", null),
        IP_ENDRANGE_VALUE4("ip_endrange_value4", LPDatabase.string(),null, null, "", null),
        ;
        private IPWhiteList(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum IPBlackList implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null), 
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null), 
        ADDED_ON("added_on", LPDatabase.dateTime(), null, null, null, null),
        ADDED_BY("added_by", LPDatabase.string(), null, null, null, null), 
        IP_VALUE1("ip_value1", LPDatabase.stringNotNull(),null, null, "", null),
        IP_VALUE2("ip_value2", LPDatabase.stringNotNull(),null, null, "", null),
        IP_VALUE3("ip_value3", LPDatabase.stringNotNull(),null, null, "", null),
        IP_VALUE4("ip_value4", LPDatabase.stringNotNull(),null, null, "", null),
        IP_ENDRANGE_VALUE1("ip_endrange_value1", LPDatabase.string(),null, null, "", null),
        IP_ENDRANGE_VALUE2("ip_endrange_value2", LPDatabase.string(),null, null, "", null),
        IP_ENDRANGE_VALUE3("ip_endrange_value3", LPDatabase.string(),null, null, "", null),
        IP_ENDRANGE_VALUE4("ip_endrange_value4", LPDatabase.string(),null, null, "", null),
        ;
        private IPBlackList(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum AppSession implements EnumIntTableFields{
        SESSION_ID("session_id", LPDatabase.integerNotNull(),null, null, "", null),
        DATE_STARTED("date_started", LPDatabase.date(),null, null, "", null),
        PERSON("person", LPDatabase.string(),null, null, "", null),
        ROLE_NAME("role_name", LPDatabase.string(),null, null, "", null),
        IP_ADDRESS("ip_address", LPDatabase.string(),null, null, "", null),
        PROCEDURES("procedures", LPDatabase.string(),null, null, "", null);
        private AppSession(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum UserProcess implements EnumIntTableFields{
        USER_NAME("user_name", LPDatabase.stringNotNull(),null, null, "", null),
        PROC_NAME("proc_name", LPDatabase.stringNotNull(),null, null, "", null),
        ACTIVE("active", LPDatabase.booleanFld(),null, null, "", null)
        ;
        private UserProcess(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum Users implements EnumIntTableFields{
        USER_NAME("user_name", LPDatabase.string(),null, null, "", null),
        EMAIL("email", LPDatabase.string(),null, null, "", null),
        ESIGN("e_sign", LPDatabase.string(),null, null, "", null),
        PASSWORD("password", LPDatabase.string(),null, null, "", null),
        PERSON_NAME("person_name", LPDatabase.string(),null, null, "", null),
        TABS_ON_LOGIN("tabs_on_login", LPDatabase.string(),null, null, "", null)
        //, FLD_PROCEDURES("procedures", "character varying[] COLLATE pg_catalog.\"default\"")
        ;
        private Users(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum HolidaysCalendar implements EnumIntTableFields{
        FLD_CODE("code", LPDatabase.string(),null, null, "", null),
        FLD_ACTIVE("active", LPDatabase.booleanFld(),null, null, "", null),
        FLD_CREATED_ON("created_on", LPDatabase.dateTime(),null, null, "", null),
        FLD_CREATED_BY("created_by", LPDatabase.string(),null, null, "", null),
        FLD_DESCRIPTION("description", LPDatabase.string(),null, null, "", null)
        ;
        private HolidaysCalendar(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum HolidaysCalendarDate implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, "", null),
        CALENDAR_CODE("calendar_code", LPDatabase.string(),null, null, "", null),
        DATE("date", LPDatabase.date(),null, null, "", null),
        DAY_NAME("day_name", LPDatabase.string(),null, null, "", null),
        CREATED_ON("created_on", LPDatabase.dateTime(),null, null, "", null),
        CREATED_BY("created_by", LPDatabase.string(),null, null, "", null)
        ;
        private HolidaysCalendarDate(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum Incident implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, "", null),
        DATE_CREATION("date_creation", LPDatabase.dateTime(), null, null, "", null),
        PERSON_CREATION("person_creation", LPDatabase.string(), null, null, "", null),
        DATE_CONFIRMED("date_confirmed", LPDatabase.dateTime(), null, null, "", null),
        PERSON_CONFIRMED("person_confirmed", LPDatabase.string(), null, null, "", null),
        DATE_RESOLUTION("date_resolution", LPDatabase.dateTime(), null, null, "", null),
        PERSON_RESOLUTION("person_resolution", LPDatabase.string(), null, null, "", null),
        DATE_LAST_UPDATE("date_last_update", LPDatabase.dateTime(), null, null, "", null),
        PERSON_LAST_UPDATE("person_last_update", LPDatabase.string(), null, null, "", null),
        STATUS("status", LPDatabase.string(), null, null, "", null),
        STATUS_PREVIOUS("status_previous", LPDatabase.string(), null, null, "", null),    
        USER_NAME("user_name", LPDatabase.string(), null, null, "", null),
        PERSON_NAME("person_name", LPDatabase.string(), null, null, "", null),
        USER_ROLE("user_role", LPDatabase.string(), null, null, "", null),
        TITLE("item_title", LPDatabase.string(), null, null, "", null),
        DETAIL("item_detail", LPDatabase.string(), null, null, "", null),
        SESSION_INFO("session_info", LPDatabase.string(), null, null, "", null),
        ;
        private Incident(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    
    public enum VideoTutorial implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, "", null),
        DATE_CREATION("date_creation", LPDatabase.dateTime(), null, null, "", null),
        SOURCE("source", LPDatabase.string(), null, null, "", null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, "", null),
        INDEX_LEVEL("index_level", LPDatabase.string(), null, null, "", null),
        ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, "", null),
        PARENT_ID("parent_id", LPDatabase.integer(), null, null, "", null),
        LABEL_EN("label_en", LPDatabase.string(), null, null, "", null),
        LABEL_ES("label_es", LPDatabase.string(), null, null, "", null),
        SUMMARY_EN("summary_en", LPDatabase.string(), null, null, "", null),
        SUMMARY_ES("summary_es", LPDatabase.string(), null, null, "", null),
        ENTITY_TYPE("entity_type", LPDatabase.string(), null, null, "", null),
        ENTITY_NAME("entity_name", LPDatabase.string(), null, null, "", null),
        ;
        private VideoTutorial(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
