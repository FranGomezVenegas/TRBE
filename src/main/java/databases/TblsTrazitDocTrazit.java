package databases;

import lbplanet.utilities.LPDatabase;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ReferenceFld;
/**
 *
 * @author Administrator
 */
public class TblsTrazitDocTrazit {
    public static final String FIELDS_NAMES_SCHEMA_PREFIX="schema_prefix";
    public static final String FIELDS_NAMES_DESCRIPTION="description";
    private static final java.lang.String SCHEMA_NAME = "trazit";
    public enum TablesTrazitDocTrazit implements EnumIntTables{
        ENDPOINTS_DECLARATION(null, "endpoints_declaration", SCHEMA_NAME, true, EndpointsDeclaration.values(), EndpointsDeclaration.ID.getName(),
            new String[]{EndpointsDeclaration.ID.getName()}, null, "EndpointsDeclaration"),
        BUSINESS_RULES_DECLARATION(null, "business_rules_declaration", SCHEMA_NAME, true, BusinessRulesDeclaration.values(), BusinessRulesDeclaration.ID.getName(),
            new String[]{EndpointsDeclaration.ID.getName()}, null, "EndpointsDeclaration"),
        MESSAGE_CODES_DECLARATION(null, "message_codes_declaration", SCHEMA_NAME, true, MessageCodeDeclaration.values(), MessageCodeDeclaration.ID.getName(),
            new String[]{EndpointsDeclaration.ID.getName()}, null, "EndpointsDeclaration"),
        AUDIT_EVENTS_DECLARATION(null, "audit_events_declaration", SCHEMA_NAME, true, AuditEventsDeclaration.values(), AuditEventsDeclaration.ID.getName(),
            new String[]{EndpointsDeclaration.ID.getName()}, null, "EndpointsDeclaration"),
        ;
        private TablesTrazitDocTrazit(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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

    public enum EndpointsDeclaration implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        API_NAME("api_name", LPDatabase.stringNotNull(), null, null, null, null),
        API_VERSION("api_version", LPDatabase.integer(), null, null, null, null),
        API_URL("api_url", LPDatabase.string(), null, null, null, null),
        ENDPOINT_NAME("endpoint_name", LPDatabase.stringNotNull(), null, null, null, null),
        ENDPOINT_VERSION("endpoint_version", LPDatabase.integer(), null, null, null, null),
        ARGUMENTS("arguments", LPDatabase.stringNotNull(), null, null, null, null),
        ARGUMENTS_ARRAY("arguments_array", LPDatabase.string(), null, null, null, null),
        NUM_ARGUMENTS("num_arguments", LPDatabase.integer(), null, null, null, null),
        OUTPUT_OBJECT_TYPES("output_object_types", LPDatabase.string(), null, null, null, null),
        CREATION_DATE("creation_date", LPDatabase.dateTimeWithDefaultNow(), null, null, null, null),
        LAST_UPDATE("last_update", LPDatabase.dateTime(), null, null, null, null),
        BRIEF_SUMMARY_EN("brief_summary_en", LPDatabase.string(), null, null, null, null),
        DOCUMENT_NAME_EN("document_name_en", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_NAME_EN("doc_chapter_name_en", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_ID_EN("doc_chapter_id_en", LPDatabase.string(), null, null, null, null),
        BRIEF_SUMMARY_ES("brief_summary_es", LPDatabase.string(), null, null, null, null),
        DOCUMENT_NAME_ES("document_name_es", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_NAME_ES("doc_chapter_name_es", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_ID_ES("doc_chapter_id_es", LPDatabase.string(), null, null, null, null),
        NUM_ENDPOINTS_IN_API("num_endpoints_in_api", LPDatabase.integer(), null, null, null, null),
        SUCCESS_MESSAGE_CODE("success_message_code", LPDatabase.string(), null, null, null, null),
        
        ;
        private EndpointsDeclaration(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum BusinessRulesDeclaration implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        API_NAME("api_name", LPDatabase.stringNotNull(), null, null, null, null),
        API_VERSION("api_version", LPDatabase.integer(), null, null, null, null),
        PROPERTY_NAME("property_name", LPDatabase.stringNotNull(), null, null, null, null),
        VALUES_LIST("values_list", LPDatabase.string(), null, null, null, null),
        ALLOW_MULTI_VALUES("allow_multi_values", LPDatabase.booleanFld(), null, null, null, null),
        VALUES_SEPARATOR("values_separator", LPDatabase.string(1), null, null, null, null),
        FILE_AREA("file_area", LPDatabase.stringNotNull(), null, null, null, null),
//        ENDPOINT_VERSION("endpoint_version", LPDatabase.integer()),
//        ARGUMENTS("arguments", LPDatabase.stringNotNull()),
//        ARGUMENTS_ARRAY("arguments_array", LPDatabase.string()),
        CREATION_DATE("creation_date", LPDatabase.dateTimeWithDefaultNow(), null, null, null, null),
        LAST_UPDATE("last_update", LPDatabase.dateTime(), null, null, null, null),
        BRIEF_SUMMARY_EN("brief_summary_en", LPDatabase.string(), null, null, null, null),
        DOCUMENT_NAME_EN("document_name_en", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_NAME_EN("doc_chapter_name_en", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_ID_EN("doc_chapter_id_en", LPDatabase.string(), null, null, null, null),
        BRIEF_SUMMARY_ES("brief_summary_es", LPDatabase.string(), null, null, null, null),
        DOCUMENT_NAME_ES("document_name_es", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_NAME_ES("doc_chapter_name_es", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_ID_ES("doc_chapter_id_es", LPDatabase.string(), null, null, null, null),
        ;
        private BusinessRulesDeclaration(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum MessageCodeDeclaration implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        API_NAME("api_name", LPDatabase.stringNotNull(), null, null, null, null),
        API_VERSION("api_version", LPDatabase.integer(), null, null, null, null),
        PROPERTY_NAME("property_name", LPDatabase.stringNotNull(), null, null, null, null),
//        ENDPOINT_VERSION("endpoint_version", LPDatabase.integer()),
//        ARGUMENTS("arguments", LPDatabase.stringNotNull()),
        MSG_VARS_ARRAY("msg_vars_array", LPDatabase.string(), null, null, null, null),
        TRANSLATIONS_ARRAY("translations_array", LPDatabase.string(), null, null, null, null),
        CREATION_DATE("creation_date", LPDatabase.dateTimeWithDefaultNow(), null, null, null, null),
        LAST_UPDATE("last_update", LPDatabase.dateTime(), null, null, null, null),
        BRIEF_SUMMARY_EN("brief_summary_en", LPDatabase.string(), null, null, null, null),
        DOCUMENT_NAME_EN("document_name_en", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_NAME_EN("doc_chapter_name_en", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_ID_EN("doc_chapter_id_en", LPDatabase.string(), null, null, null, null),
        BRIEF_SUMMARY_ES("brief_summary_es", LPDatabase.string(), null, null, null, null),
        DOCUMENT_NAME_ES("document_name_es", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_NAME_ES("doc_chapter_name_es", LPDatabase.string(), null, null, null, null),
        DOC_CHAPTER_ID_ES("doc_chapter_id_es", LPDatabase.string(), null, null, null, null),
        ;
        private MessageCodeDeclaration(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum AuditEventsDeclaration implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        AUDIT_OBJECT("audit_object", LPDatabase.stringNotNull(), null, null, null, null),
        EVENT_NAME("event_name", LPDatabase.integer(), null, null, null, null),
        EVENT_PRETTY_EN("event_pretty_en", LPDatabase.stringNotNull(), null, null, null, null),
        EVENT_PRETTY_ES("event_pretty_es", LPDatabase.stringNotNull(), null, null, null, null),
        CREATION_DATE("creation_date", LPDatabase.dateTimeWithDefaultNow(), null, null, null, null),
        LAST_UPDATE("last_update", LPDatabase.dateTime(), null, null, null, null),
        ;
        private AuditEventsDeclaration(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
