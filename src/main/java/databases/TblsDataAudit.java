/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import lbplanet.utilities.LPDatabase;
import static lbplanet.utilities.LPDatabase.dateTime;
import lbplanet.utilities.LPEnums.CellHelper;
import lbplanet.utilities.LPEnums.Indexed;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class TblsDataAudit {

    public static final String FIELDS_NAMES_USER_ID="user_id";
    public static final String FIELDS_NAMES_USER_NAME="user_name";

    
    public enum MyEnum implements Indexed<MyEnum> {ONE, TWO, THREE,;}

    public enum SomeEnumClass {ONE(1), TWO(2), THREE(3);
        SomeEnumClass(int n){}
        // This variable hosts your static data, along with shared behavior
        private static  final CellHelper<SomeEnumClass> helper = new CellHelper(SomeEnumClass.values(), SomeEnumClass.class);
        // Delegate the calls for shared functionality to the helper object
        public static SomeEnumClass getCell(int i) {return helper.getCell(i);}
    }

    public enum OtherEnumClass {MONDAY(1), TUESDAY(2), WEDNESDAY(3), THRUSDAY(4), FRIDAY(5), SATURDAY(6), SUNDAY(7);
        OtherEnumClass(int n){}
        private static  final CellHelper<OtherEnumClass> helper = new CellHelper(OtherEnumClass.values(), OtherEnumClass.class);
        public static OtherEnumClass getCell(int i) {return helper.getCell(i);}
    }

    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA_AUDIT.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesDataAudit implements EnumIntTables{
        SAMPLE(null, "sample", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Sample.values(), Sample.AUDIT_ID.getName(),
            new String[]{Sample.AUDIT_ID.getName()}, null, "Sample Audit Trial"),
        USER_CERTIF_TRACK(null, "user_certification_track", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, UserCertifTrack.values(), UserCertifTrack.AUDIT_ID.getName(),
            new String[]{UserCertifTrack.AUDIT_ID.getName()}, null, "UserCertifTrack Audit Trial"),
        CERTIF_USER_ANALYSIS_METHOD(null, "certif_user_analysis_method", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, CertifUserAnalysisMethod.values(), CertifUserAnalysisMethod.AUDIT_ID.getName(),
            new String[]{CertifUserAnalysisMethod.AUDIT_ID.getName()}, null, "certif_user_analysis_method Audit Trial"),
        SESSION(null, "session", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Session.values(), Session.SESSION_ID.getName(),
            new String[]{Session.SESSION_ID.getName()}, null, "Process Session Audit Trial"),
        ;
        private TablesDataAudit(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    
    public enum Session implements EnumIntTableFields{
        SESSION_ID("session_id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_session_id_seq'::regclass)", null, null, null, null),
        PERSON("person", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), "", null),
        ROLE_NAME("role_name", LPDatabase.string(), null, null, null, null),
        DATE_STARTED("date_started", dateTime(), null, null, null, null),
        DATE_ENDED("date_ended", dateTime(), null, null, null, null),
        USER_SESSION_ID("user_session_id", LPDatabase.integer(), null, null, null, null),
        ;
        private Session(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum Sample implements EnumIntTableFields{
        AUDIT_ID("audit_id", LPDatabase.integer(), null, null, null, null),
        TABLE_NAME("table_name", " character varying COLLATE pg_catalog.\"default\"", null, null, null, null),
        TRANSACTION_ID("transaction_id", LPDatabase.integer(), null, null, null, null),
        TABLE_ID("table_id", LPDatabase.integer(), null, null, null, null),
        DATE("date", LPDatabase.dateTime(), null, null, null, null),
        PERSON("person", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), "", null),
        ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null),
        FIELDS_UPDATED("fields_updated", LPDatabase.string(), null, null, null, null),
        SAMPLE_ID("sample_id", LPDatabase.integer(), null, null, null, null),
        TEST_ID("test_id", LPDatabase.integer(), null, null, null, null),
        RESULT_ID("result_id", LPDatabase.integer(), null, null, null, null),
        USER_ROLE("user_role", LPDatabase.string(), null, null, null, null),
        PROCEDURE("procedure", LPDatabase.string(), null, null, null, null),
        PROCEDURE_VERSION("procedure_version", LPDatabase.integer(), null, null, null, null),
        PROCEDURE_HASH_CODE("procedure_hash_code", LPDatabase.stringNotNull(), null, null, null, null),
        APP_SESSION_ID("app_session_id", LPDatabase.integer(), null, null, null, null),
        ALIQUOT_ID("aliquot_id", LPDatabase.integer(), null, null, null, null),
        SUBALIQUOT_ID("subaliquot_id", LPDatabase.integer(), null, null, null, null),
        PICTURE_BEFORE("picture_before", "json", null, null, null, null),
        PICTURE_AFTER("picture_after", "json", null, null, null, null),
        REVIEWED("reviewed", LPDatabase.booleanFld(false), null, null, null, null),
        REVIEWED_BY("reviewed_by", LPDatabase.string(), null, null, null, null),
        REVIEWED_ON("reviewed_on", dateTime(), null, null, null, null),
        REVISION_NOTE("revision_note", LPDatabase.string(), null, null, null, null),
        PARENT_AUDIT_ID("parent_audit_id", LPDatabase.integer(), null, null, null, null),
        REASON("reason", LPDatabase.string(), null, null, null, null),
        ACTION_PRETTY_EN("action_pretty_en", LPDatabase.string(), null, null, null, null),
        ACTION_PRETTY_ES("action_pretty_es", LPDatabase.string(), null, null, null, null),
        ;
        private Sample(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum UserCertifTrack implements EnumIntTableFields{
        AUDIT_ID("audit_id", LPDatabase.integer(), null, null, null, null),
        TRANSACTION_ID("transaction_id", LPDatabase.integer(), null, null, null, null),
        OBJECT_TYPE("object_type", " character varying COLLATE pg_catalog.\"default\"", null, null, null, null),
        OBJECT_ID("object_id", LPDatabase.integer(), null, null, null, null),
        OBJECT_NAME("object_name", " character varying COLLATE pg_catalog.\"default\"", null, null, null, null),
        DATE("date", LPDatabase.dateTime(), null, null, null, null),
        PERSON("person", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), "", null),
        ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null),
        FIELDS_UPDATED("fields_updated", LPDatabase.string(), null, null, null, null),
        PROCEDURE("procedure", LPDatabase.string(), null, null, null, null),
        PROCEDURE_VERSION("procedure_version", LPDatabase.integer(), null, null, null, null),
        PROCEDURE_HASH_CODE("procedure_hash_code", LPDatabase.stringNotNull(), null, null, null, null),
        APP_SESSION_ID("app_session_id", LPDatabase.integer(), null, null, null, null),
        PICTURE_BEFORE("picture_before", "json", null, null, null, null),
        PICTURE_AFTER("picture_after", "json", null, null, null, null),
        REASON("reason", LPDatabase.string(), null, null, null, null),
        ACTION_PRETTY_EN("action_pretty_en", LPDatabase.string(), null, null, null, null),
        ACTION_PRETTY_ES("action_pretty_es", LPDatabase.string(), null, null, null, null),
        ;
        private UserCertifTrack(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum CertifUserAnalysisMethod implements EnumIntTableFields{
        AUDIT_ID("audit_id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_audit_id_seq'::regclass)", null, null, null, null),
        TRANSACTION_ID("transaction_id", LPDatabase.integer(), null, null, null, null),
        TABLE_ID("table_id", LPDatabase.string(), null, null, null, null),
        DATE("date", LPDatabase.dateTime(), null, null, null, null),
        PERSON("person", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), "", null),
        USER_NAME(FIELDS_NAMES_USER_NAME, LPDatabase.string(), null, null, null, null),
        METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.string(), null, null, null, null),
        METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer(), null, null, null, null),
        TRAINING_ID("training_id", LPDatabase.integer(), null, null, null, null),
        ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null),
        FIELDS_UPDATED("fields_updated", LPDatabase.string(), null, null, null, null),
        CERTIF_ID("certif_id", LPDatabase.integer(), null, null, null, null),
        USER_ROLE("user_role", LPDatabase.string(), null, null, null, null),
        PROCEDURE("procedure", LPDatabase.string(), null, null, null, null),
        PROCEDURE_VERSION("procedure_version", LPDatabase.integer(), null, null, null, null),
        PROCEDURE_HASH_CODE("procedure_hash_code", LPDatabase.stringNotNull(), null, null, null, null),
        APP_SESSION_ID("app_session_id", LPDatabase.integer(), null, null, null, null),
        PICTURE_BEFORE("picture_before", "json", null, null, null, null),
        PICTURE_AFTER("picture_after", "json", null, null, null, null),
        REVIEWED("reviewed", LPDatabase.booleanFld(false), null, null, null, null),
        REVIEWED_BY("reviewed_by", LPDatabase.string(), null, null, null, null),
        REVIEWED_ON("reviewed_on", LPDatabase.dateTime(), null, null, null, null),
        REVISION_NOTE("revision_note", LPDatabase.string(), null, null, null, null),
        PARENT_AUDIT_ID("parent_audit_id", LPDatabase.integer(), null, null, null, null),       
        NOTE("note", LPDatabase.string(), null, null, null, null),
        STATUS("status", LPDatabase.string(), null, null, null, null),
        REASON("reason", LPDatabase.string(), null, null, null, null),
        ;
        private CertifUserAnalysisMethod(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
