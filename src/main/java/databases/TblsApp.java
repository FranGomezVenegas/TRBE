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
import trazit.enums.ForeignkeyFld;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class TblsApp {

    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.APP.getName();
    public enum TablesApp implements EnumIntTables{
        
        IP_WHITE_LIST(null, "ip_white_list", SCHEMA_NAME, true, IPWhiteList.values(), IPWhiteList.ID.getName(),
            new String[]{IPWhiteList.ID.getName()}, null, "White List, when at least one IP added then the access will be limited to those IPs (except if added to blacklist)"),
        IP_BLACK_LIST(null, "ip_black_list", SCHEMA_NAME, true, IPBlackList.values(), IPBlackList.ID.getName(),
            new String[]{IPBlackList.ID.getName()}, null, "Black List, when one IP is added to this table then it is banned, independently of be in the white list too"),
        APP_SESSION(null, "app_session", SCHEMA_NAME, true, AppSession.values(), AppSession.FLD_SESSION_ID.getName(),
            new String[]{AppSession.FLD_SESSION_ID.getName()}, null, "Id for any user session"),
        USER_PROCESS(null, "user_process", SCHEMA_NAME, true, UserProcess.values(), null,
            new String[]{UserProcess.FLD_USER_NAME.getName(), UserProcess.FLD_USER_NAME.getName()}, null, "Processes assigned to the users"),
        USERS(null, "users", SCHEMA_NAME, true, Users.values(), null,
            new String[]{Users.FLD_USER_NAME.getName()}, null, "instance users declaration"),
        HOLIDAYS_CALENDAR(null, "holidays_calendar", SCHEMA_NAME, true, HolidaysCalendar.values(), null,
            new String[]{HolidaysCalendar.FLD_CODE.getName()}, null, "Holiday Calendars"),
        HOLIDAYS_CALENDAR_DATE(null, "holidays_calendar_date", SCHEMA_NAME, true, HolidaysCalendarDate.values(), HolidaysCalendarDate.FLD_ID.getName(),
            new String[]{HolidaysCalendarDate.FLD_ID.getName()}, null, "Holiday Calendars"),
        INCIDENT(null, "incident", SCHEMA_NAME, true, Incident.values(), Incident.FLD_ID.getName(),
            new String[]{Incident.FLD_ID.getName()}, null, "Holiday Calendars"),
        VIDEO_TUTORIAL(null, "video_tutorial", SCHEMA_NAME, true, VideoTutorial.values(), VideoTutorial.FLD_ID.getName(),
            new String[]{VideoTutorial.FLD_ID.getName()}, null, "Holiday Calendars"),
        ;
        private TablesApp(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
                String seqName, String[] primaryK, ForeignkeyFld foreignK, String comment){
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
        @Override        public ForeignkeyFld getForeignKey() {return this.foreignkey;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String tableName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final String sequence;
        private final EnumIntTableFields[] tableFields;
        private final String[] primarykey;
        private final ForeignkeyFld foreignkey;
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
        FLD_SESSION_ID("session_id", LPDatabase.integerNotNull(),null, null, "", null),
        FLD_DATE_STARTED("date_started", LPDatabase.date(),null, null, "", null),
        FLD_PERSON("person", LPDatabase.string(),null, null, "", null),
        FLD_ROLE_NAME("role_name", LPDatabase.string(),null, null, "", null),
        FLD_IP_ADDRESS("ip_address", LPDatabase.string(),null, null, "", null),
        FLD_PROCEDURES("procedures", LPDatabase.string(),null, null, "", null);
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
        FLD_USER_NAME("user_name", LPDatabase.stringNotNull(),null, null, "", null),
        FLD_PROC_NAME("proc_name", LPDatabase.stringNotNull(),null, null, "", null),
        FLD_ACTIVE("active", LPDatabase.booleanFld(),null, null, "", null)
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
        FLD_USER_NAME("user_name", LPDatabase.string(),null, null, "", null),
        FLD_EMAIL("email", LPDatabase.string(),null, null, "", null),
        FLD_ESIGN("e_sign", LPDatabase.string(),null, null, "", null),
        FLD_PASSWORD("password", LPDatabase.string(),null, null, "", null),
        FLD_PERSON_NAME("person_name", LPDatabase.string(),null, null, "", null),
        FLD_TABS_ON_LOGIN("tabs_on_login", LPDatabase.string(),null, null, "", null)
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
        FLD_ID("id", LPDatabase.integerNotNull(), null, null, "", null),
        FLD_CALENDAR_CODE("calendar_code", LPDatabase.string(),null, null, "", null),
        FLD_DATE("date", LPDatabase.date(),null, null, "", null),
        FLD_DAY_NAME("day_name", LPDatabase.string(),null, null, "", null),
        FLD_CREATED_ON("created_on", LPDatabase.dateTime(),null, null, "", null),
        FLD_CREATED_BY("created_by", LPDatabase.string(),null, null, "", null)
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
        FLD_ID("id", LPDatabase.integerNotNull(), null, null, "", null),
        FLD_DATE_CREATION("date_creation", LPDatabase.dateTime(), null, null, "", null),
        FLD_PERSON_CREATION("person_creation", LPDatabase.string(), null, null, "", null),
        FLD_DATE_CONFIRMED("date_confirmed", LPDatabase.dateTime(), null, null, "", null),
        FLD_PERSON_CONFIRMED("person_confirmed", LPDatabase.string(), null, null, "", null),
        FLD_DATE_RESOLUTION("date_resolution", LPDatabase.dateTime(), null, null, "", null),
        FLD_PERSON_RESOLUTION("person_resolution", LPDatabase.string(), null, null, "", null),
        FLD_DATE_LAST_UPDATE("date_last_update", LPDatabase.dateTime(), null, null, "", null),
        FLD_PERSON_LAST_UPDATE("person_last_update", LPDatabase.string(), null, null, "", null),
        FLD_STATUS("status", LPDatabase.string(), null, null, "", null),
        FLD_STATUS_PREVIOUS("status_previous", LPDatabase.string(), null, null, "", null),    
        FLD_USER_NAME("user_name", LPDatabase.string(), null, null, "", null),
        FLD_PERSON_NAME("person_name", LPDatabase.string(), null, null, "", null),
        FLD_USER_ROLE("user_role", LPDatabase.string(), null, null, "", null),
        FLD_TITLE("item_title", LPDatabase.string(), null, null, "", null),
        FLD_DETAIL("item_detail", LPDatabase.string(), null, null, "", null),
        FLD_SESSION_INFO("session_info", LPDatabase.string(), null, null, "", null),
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
        FLD_ID("id", LPDatabase.integerNotNull(), null, null, "", null),
        FLD_DATE_CREATION("date_creation", LPDatabase.dateTime(), null, null, "", null),
        FLD_SOURCE("source", LPDatabase.string(), null, null, "", null),
        FLD_ACTIVE("active", LPDatabase.booleanFld(), null, null, "", null),
        FLD_INDEX_LEVEL("index_level", LPDatabase.string(), null, null, "", null),
        FLD_ORDER_NUMBER("order_number", LPDatabase.integer(), null, null, "", null),
        FLD_PARENT_ID("parent_id", LPDatabase.integer(), null, null, "", null),
        FLD_LABEL_EN("label_en", LPDatabase.string(), null, null, "", null),
        FLD_LABEL_ES("label_es", LPDatabase.string(), null, null, "", null),
        FLD_SUMMARY_EN("summary_en", LPDatabase.string(), null, null, "", null),
        FLD_SUMMARY_ES("summary_es", LPDatabase.string(), null, null, "", null),
        FLD_ENTITY_TYPE("entity_type", LPDatabase.string(), null, null, "", null),
        FLD_ENTITY_NAME("entity_name", LPDatabase.string(), null, null, "", null),
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
