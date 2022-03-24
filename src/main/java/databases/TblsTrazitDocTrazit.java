package databases;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPPlatform;
import static databases.TblsCnfg.SCHEMATAG;
import static databases.TblsCnfg.TABLETAG;
import static databases.TblsCnfg.OWNERTAG;
import static databases.TblsCnfg.TABLESPACETAG;
import static databases.TblsCnfg.FIELDSTAG;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class TblsTrazitDocTrazit {
    public static final String FIELDS_NAMES_SCHEMA_PREFIX="schema_prefix";
    public static final String FIELDS_NAMES_DESCRIPTION="description";

    public enum EndpointsDeclaration{
        ID("id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_audit_id_seq'::regclass)")
        ,        TBL("endpoints_declaration", LPDatabase.createSequence(ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#AUDIT_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_#ID_pkey PRIMARY KEY (#AUDIT_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;")
        ,
        API_NAME("api_name", LPDatabase.stringNotNull()),
        API_VERSION("api_version", LPDatabase.integer()),
        ENDPOINT_NAME("endpoint_name", LPDatabase.stringNotNull()),
        ENDPOINT_VERSION("endpoint_version", LPDatabase.integer()),
        ARGUMENTS("arguments", LPDatabase.stringNotNull()),
        ARGUMENTS_ARRAY("arguments_array", LPDatabase.string()),
        OUTPUT_OBJECT_TYPES("output_object_types", LPDatabase.string()),        
        CREATION_DATE("creation_date", LPDatabase.dateTimeWithDefaultNow()),
        LAST_UPDATE("last_update", LPDatabase.dateTime()),
        BRIEF_SUMMARY_EN("brief_summary_en", LPDatabase.string()),
        DOCUMENT_NAME_EN("document_name_en", LPDatabase.string()),
        DOC_CHAPTER_NAME_EN("doc_chapter_name_en", LPDatabase.string()),
        DOC_CHAPTER_ID_EN("doc_chapter_id_en", LPDatabase.string()),
        BRIEF_SUMMARY_ES("brief_summary_es", LPDatabase.string()),
        DOCUMENT_NAME_ES("document_name_es", LPDatabase.string()),
        DOC_CHAPTER_NAME_ES("doc_chapter_name_es", LPDatabase.string()),
        DOC_CHAPTER_ID_ES("doc_chapter_id_es", LPDatabase.string()),
        NUM_ENDPOINTS_IN_API("num_endpoints_in_api", LPDatabase.integer()),        
        SUCCESS_MESSAGE_CODE("success_message_code", LPDatabase.string()),
        ;
        private EndpointsDeclaration(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return entry name
         */
        public String getName(){
            return this.dbObjName;
        }
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix procedure prefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = EndpointsDeclaration.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.REQUIREMENTS.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (EndpointsDeclaration obj: EndpointsDeclaration.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.REQUIREMENTS.getName()));
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }  
        private final String dbObjName;             
        private final String dbObjTypePostgres;  
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (EndpointsDeclaration obj: EndpointsDeclaration.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }              
    }

    public enum BusinessRulesDeclaration{
        ID("id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_id_seq'::regclass)"),
        TBL("business_rules_declaration", LPDatabase.createSequence(ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#AUDIT_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_#ID_pkey PRIMARY KEY (#AUDIT_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;")
        ,
        API_NAME("api_name", LPDatabase.stringNotNull()),
        API_VERSION("api_version", LPDatabase.integer()),
        PROPERTY_NAME("property_name", LPDatabase.stringNotNull()),
        VALUES_LIST("values_list", LPDatabase.string()),
        ALLOW_MULTI_VALUES("allow_multi_values", LPDatabase.booleanFld()),
        VALUES_SEPARATOR("values_separator", LPDatabase.string(1)),
        FILE_AREA("file_area", LPDatabase.stringNotNull()),
//        ENDPOINT_VERSION("endpoint_version", LPDatabase.integer()),
//        ARGUMENTS("arguments", LPDatabase.stringNotNull()),
//        ARGUMENTS_ARRAY("arguments_array", LPDatabase.string()),
        CREATION_DATE("creation_date", LPDatabase.dateTimeWithDefaultNow()),
        LAST_UPDATE("last_update", LPDatabase.dateTime()),
        BRIEF_SUMMARY_EN("brief_summary_en", LPDatabase.string()),
        DOCUMENT_NAME_EN("document_name_en", LPDatabase.string()),
        DOC_CHAPTER_NAME_EN("doc_chapter_name_en", LPDatabase.string()),
        DOC_CHAPTER_ID_EN("doc_chapter_id_en", LPDatabase.string()),
        BRIEF_SUMMARY_ES("brief_summary_es", LPDatabase.string()),
        DOCUMENT_NAME_ES("document_name_es", LPDatabase.string()),
        DOC_CHAPTER_NAME_ES("doc_chapter_name_es", LPDatabase.string()),
        DOC_CHAPTER_ID_ES("doc_chapter_id_es", LPDatabase.string()),
        ;
        private BusinessRulesDeclaration(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return entry name
         */
        public String getName(){
            return this.dbObjName;
        }
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix procedure prefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = BusinessRulesDeclaration.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.REQUIREMENTS.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (BusinessRulesDeclaration obj: BusinessRulesDeclaration.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.REQUIREMENTS.getName()));
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }  
        private final String dbObjName;             
        private final String dbObjTypePostgres;  
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (BusinessRulesDeclaration obj: BusinessRulesDeclaration.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }              
    }

    public enum MessageCodeDeclaration{
        ID("id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_id_seq'::regclass)"),
        TBL("message_codes_declaration", LPDatabase.createSequence(ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#AUDIT_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_#ID_pkey PRIMARY KEY (#AUDIT_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;")
        ,
        API_NAME("api_name", LPDatabase.stringNotNull()),
        API_VERSION("api_version", LPDatabase.integer()),
        PROPERTY_NAME("property_name", LPDatabase.stringNotNull()),
//        ENDPOINT_VERSION("endpoint_version", LPDatabase.integer()),
//        ARGUMENTS("arguments", LPDatabase.stringNotNull()),
        MSG_VARS_ARRAY("msg_vars_array", LPDatabase.string()),
        TRANSLATIONS_ARRAY("translations_array", LPDatabase.string()),
        CREATION_DATE("creation_date", LPDatabase.dateTimeWithDefaultNow()),
        LAST_UPDATE("last_update", LPDatabase.dateTime()),
        BRIEF_SUMMARY_EN("brief_summary_en", LPDatabase.string()),
        DOCUMENT_NAME_EN("document_name_en", LPDatabase.string()),
        DOC_CHAPTER_NAME_EN("doc_chapter_name_en", LPDatabase.string()),
        DOC_CHAPTER_ID_EN("doc_chapter_id_en", LPDatabase.string()),
        BRIEF_SUMMARY_ES("brief_summary_es", LPDatabase.string()),
        DOCUMENT_NAME_ES("document_name_es", LPDatabase.string()),
        DOC_CHAPTER_NAME_ES("doc_chapter_name_es", LPDatabase.string()),
        DOC_CHAPTER_ID_ES("doc_chapter_id_es", LPDatabase.string()),
        ;
        private MessageCodeDeclaration(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return entry name
         */
        public String getName(){
            return this.dbObjName;
        }
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix procedure prefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = MessageCodeDeclaration.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.REQUIREMENTS.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (MessageCodeDeclaration obj: MessageCodeDeclaration.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.REQUIREMENTS.getName()));
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }  
        private final String dbObjName;             
        private final String dbObjTypePostgres;  
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (MessageCodeDeclaration obj: MessageCodeDeclaration.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }              
    }

    public enum AuditEventsDeclaration{
        ID("id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_audit_id_seq'::regclass)")
        ,        
        TBL("audit_events_declaration", LPDatabase.createSequence(ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#AUDIT_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_#ID_pkey PRIMARY KEY (#AUDIT_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;")
        ,
        AUDIT_OBJECT("audit_object", LPDatabase.stringNotNull()),
        EVENT_NAME("event_name", LPDatabase.integer()),
        EVENT_PRETTY_EN("event_pretty_en", LPDatabase.stringNotNull()),
        EVENT_PRETTY_ES("event_pretty_es", LPDatabase.stringNotNull()),
        CREATION_DATE("creation_date", LPDatabase.dateTimeWithDefaultNow()),
        LAST_UPDATE("last_update", LPDatabase.dateTime()),        
        ;
        private AuditEventsDeclaration(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return entry name
         */
        public String getName(){
            return this.dbObjName;
        }
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix procedure prefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = AuditEventsDeclaration.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.REQUIREMENTS.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (AuditEventsDeclaration obj: AuditEventsDeclaration.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.REQUIREMENTS.getName()));
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }  
        private final String dbObjName;             
        private final String dbObjTypePostgres;  
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (AuditEventsDeclaration obj: AuditEventsDeclaration.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }              
    }
}
