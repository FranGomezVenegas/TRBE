/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class TblsReqs {
    public static final String FIELDS_NAMES_SCHEMA_PREFIX="schema_prefix";
    public static final String FIELDS_NAMES_DESCRIPTION="description";
    /**
     *
     */
    public enum ProcedureInfo{ // 'the sequence is session_id integer NOT NULL DEFAULT nextval('app.app_session_session_id_seq1'::regclass)'
        TBL("procedure_info",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT user_process_pkey PRIMARY KEY (#FLD_NAME, #FLD_VERSION) ) "
                + LPDatabase.POSTGRESQL_OIDS +  LPDatabase.createTableSpace() + "ALTER TABLE #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")        ,
        FLD_PROCEDURE_NAME("procedure_name", LPDatabase.stringNotNull())        ,
        FLD_PROCEDURE_VERSION("procedure_version", LPDatabase.integerNotNull())        ,
        FLD_DESCRIPTION(FIELDS_NAMES_DESCRIPTION, LPDatabase.stringNotNull()),
        FLD_SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull())        ,
        FLD_LABEL_EN("label_en", LPDatabase.stringNotNull())        ,
        FLD_LABEL_ES("label_es", LPDatabase.stringNotNull())
        ;
        private ProcedureInfo(String dbObjName, String dbObjType){
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
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureInfo.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.REQUIREMENTS.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureInfo obj: ProcedureInfo.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }  
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ProcedureInfo obj: ProcedureInfo.values()){
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

    /**
     *
     */
    public enum ProcedureRoles{ // 'the sequence is session_id integer NOT NULL DEFAULT nextval('app.app_session_session_id_seq1'::regclass)'

        /**
         *
         */
        TBL("procedure_roles",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #SCHEMA_#TBL_pkey PRIMARY KEY (#FLD_PROCEDURE_NAME, #FLD_PROCEDURE_VERSION, #FLD_ROLE_NAME) ) "
                + LPDatabase.POSTGRESQL_OIDS +  LPDatabase.createTableSpace() + "ALTER TABLE #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_DESCRIPTION(FIELDS_NAMES_DESCRIPTION, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_ROLE_NAME("role_name", LPDatabase.stringNotNull())
        ;
        private ProcedureRoles(String dbObjName, String dbObjType){
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
         * @param fields
         * @return
         */
        public static String createTableScript(String[] fields){
            return createTableScriptPostgres(fields);
        }
        private static String createTableScriptPostgres(String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureRoles.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureRoles obj: ProcedureRoles.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }   
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ProcedureRoles obj: ProcedureRoles.values()){
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

    /**
     *
     */
    public enum ProcedureSopMetaData{ // 'the sequence is session_id integer NOT NULL DEFAULT nextval('app.app_session_session_id_seq1'::regclass)'

        /**
         *
         */
        FLD_SOP_ID("sop_id", "integer NOT NULL DEFAULT nextval('#SCHEMA.#TBL_sop_id_seq'::regclass)")
        ,        
        TBL("procedure_sop_meta_data", LPDatabase.createSequence(FLD_SOP_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_SOP_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #SCHEMA_#TBL_pkey PRIMARY KEY (#FLD_PROCEDURE_NAME, #FLD_PROCEDURE_VERSION, #FLD_SCHEMA_PREFIX, #FLD_SOP_ID) ) "
                + LPDatabase.POSTGRESQL_OIDS +  LPDatabase.createTableSpace() + "ALTER TABLE #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */


        /**
         *
         */
        FLD_PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_SOP_NAME("sop_name", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_SOP_VERSION("sop_version", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_SOP_REVISION("sop_revision", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_CURRENT_STATUS("current_status", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_EXPIRES("expires", LPDatabase.booleanFld(false))
        ,        

        /**
         *
         */
        FLD_HAS_CHILD("has_child", LPDatabase.booleanFld(false))        
        ,

        /**
         *
         */
        FLD_FILE_LINK("file_link", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_BRIEF_SUMMARY("brief_summary", LPDatabase.string())
        // ....
        ;
        private ProcedureSopMetaData(String dbObjName, String dbObjType){
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
         * @param fields
         * @return
         */
        public static String createTableScript(String[] fields){
            return createTableScriptPostgres(fields);
        }
        private static String createTableScriptPostgres(String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureSopMetaData.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureSopMetaData obj: ProcedureSopMetaData.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }    
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ProcedureSopMetaData obj: ProcedureSopMetaData.values()){
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

    /**
     *
     */
    public enum ProcedureUserRequirements{ // 'the sequence is session_id integer NOT NULL DEFAULT nextval('app.app_session_session_id_seq1'::regclass)'

        /**
         *
         */
        FLD_ID("id", "integer NOT NULL DEFAULT nextval('#SCHEMA.#TBL_id_seq'::regclass)")
        ,        
        TBL("procedure_user_requirements", LPDatabase.createSequence(FLD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #SCHEMA_#TBL_pkey PRIMARY KEY (#FLD_PROCEDURE_NAME, #FLD_PROCEDURE_VERSION, #FLD_SCHEMA_PREFIX, #FLD_ID) ) "
                + LPDatabase.POSTGRESQL_OIDS +  LPDatabase.createTableSpace() + "ALTER TABLE #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */


        /**
         *
         */
        FLD_PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_ORDER_NUMBER("order_number", LPDatabase.integer()),

        FLD_CODE("code", LPDatabase.string()),
        FLD_NAME("name", LPDatabase.string()),
        FLD_DESCRIPTION("description", LPDatabase.string()),
        FLD_ACTIVE("active", LPDatabase.booleanFld()),
        FLD_IN_SCOPE("in_scope", LPDatabase.booleanFld()),
        FLD_IN_SYSTEM("in_system", LPDatabase.booleanFld()),
        FLD_ROLES("roles", LPDatabase.string()),
        FLD_SOP_NAME("sop_name", LPDatabase.string()),
        FLD_ESIGN_REQ("esign_req", LPDatabase.booleanFld()),
        FLD_USERCONFIRM_REQ("userconfirmation_req", LPDatabase.booleanFld()),
        FLD_WIDGET("widget", LPDatabase.string()),
        FLD_WIDGET_VERSION("widget_version", LPDatabase.integer()),
        FLD_WIDGET_ACTION("widget_action", LPDatabase.string()),
        FLD_WIDGET_ACCESS_MODE("widget_access_mode", LPDatabase.string()),
        FLD_WIDGET_TYPE("widget_type", LPDatabase.string()),
        FLD_WIDGET_LABEL_EN("widget_label_en", LPDatabase.string()),
        FLD_WIDGET_LABEL_ES("widget_label_es", LPDatabase.string()),
        FLD_ROLE_NAME("role_name", LPDatabase.string()),
        FLD_MODE("mode", LPDatabase.string()),
        FLD_TYPE("type", LPDatabase.string()),
        FLD_BRANCH_LEVEL("branch_level", LPDatabase.string()),
        FLD_BUSINESS_RULE_NAME("business_rule_name", LPDatabase.string()),
        FLD_BUSINESS_RULE_VALUE("business_rule_value", LPDatabase.string()),
        
        ;
        private ProcedureUserRequirements(String dbObjName, String dbObjType){
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
         * @param fields
         * @return
         */
        public static String createTableScript(String[] fields){
            return createTableScriptPostgres(fields);
        }
        private static String createTableScriptPostgres(String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureUserRequirements.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureUserRequirements obj: ProcedureUserRequirements.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }    
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ProcedureUserRequirements obj: ProcedureUserRequirements.values()){
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

    public enum ProcedureUserRequirementsEvents{ // 'the sequence is session_id integer NOT NULL DEFAULT nextval('app.app_session_session_id_seq1'::regclass)'
        /**
         *
         */
        FLD_ID("id", "integer NOT NULL DEFAULT nextval('#SCHEMA.#TBL_id_seq'::regclass)")
        ,        
        TBL("procedure_user_requirements_events", LPDatabase.createSequence(FLD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #SCHEMA_#TBL_pkey PRIMARY KEY (#FLD_PROCEDURE_NAME, #FLD_PROCEDURE_VERSION, #FLD_SCHEMA_PREFIX, #FLD_ID) ) "
                + LPDatabase.POSTGRESQL_OIDS +  LPDatabase.createTableSpace() + "ALTER TABLE #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
        /**
         *
         */
        FLD_PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_ORDER_NUMBER("order_number", LPDatabase.integer()),
        FLD_NAME("name", LPDatabase.string()),
        FLD_ROLE_NAME("role_name", LPDatabase.string()),
        FLD_MODE("mode", LPDatabase.string()),
        FLD_TYPE("type", LPDatabase.string()),
        FLD_BRANCH_LEVEL("branch_level", LPDatabase.string()),
        FLD_LABEL_EN("label_en", LPDatabase.string()),
        FLD_LABEL_ES("label_es", LPDatabase.string()),
        FLD_SOP("sop", LPDatabase.string()),
        FLD_ESIGN_REQUIRED("esign_required", LPDatabase.booleanFld()),
        FLD_USERCONFIRM_REQUIRED("userconfirm_required", LPDatabase.booleanFld()),
        FLD_LP_FRONTEND_PAGE_NAME("lp_frontend_page_name", LPDatabase.string()),        
        
        // ....
        ;
        private ProcedureUserRequirementsEvents(String dbObjName, String dbObjType){
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
         * @param fields
         * @return
         */
        public static String createTableScript(String[] fields){
            return createTableScriptPostgres(fields);
        }
        private static String createTableScriptPostgres(String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureUserRequirementsEvents.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureUserRequirementsEvents obj: ProcedureUserRequirementsEvents.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }           
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ProcedureUserRequirementsEvents obj: ProcedureUserRequirementsEvents.values()){
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
    /**
     *
     */
    public enum ProcedureUserRole{ // 'the sequence is session_id integer NOT NULL DEFAULT nextval('app.app_session_session_id_seq1'::regclass)'

        /**
         *
         */
        TBL("procedure_user_role",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #SCHEMA_#TBL_pkey PRIMARY KEY (#FLD_PROCEDURE_NAME, #FLD_PROCEDURE_VERSION, #FLD_SCHEMA_PREFIX, #FLD_USER_NAME, #FLD_ROLE_NAME) ) "
                + LPDatabase.POSTGRESQL_OIDS +  LPDatabase.createTableSpace() + "ALTER TABLE #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_USER_NAME("user_name", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_ROLE_NAME("role_name", LPDatabase.stringNotNull())
        // ....
        ;
        private ProcedureUserRole(String dbObjName, String dbObjType){
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
         * @param fields
         * @return
         */
        public static String createTableScript(String[] fields){
            return createTableScriptPostgres(fields);
        }
        private static String createTableScriptPostgres(String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureUserRole.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureUserRole obj: ProcedureUserRole.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }  
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ProcedureUserRole obj: ProcedureUserRole.values()){
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

    /**
     *
     */
    public enum ProcedureUsers{ // 'the sequence is session_id integer NOT NULL DEFAULT nextval('app.app_session_session_id_seq1'::regclass)'
        TBL("procedure_users",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #SCHEMA_#TBL_pkey PRIMARY KEY (#FLD_PROCEDURE_NAME, #FLD_PROCEDURE_VERSION, #FLD_SCHEMA_PREFIX, #FLD_USER_NAME) ) "
                + LPDatabase.POSTGRESQL_OIDS +  LPDatabase.createTableSpace() + "ALTER TABLE #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")        ,
        FLD_PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull())        ,
        FLD_PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull())        ,
        FLD_SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull())        ,
        FLD_USER_NAME("user_name", LPDatabase.stringNotNull())        ;
        private ProcedureUsers(String dbObjName, String dbObjType){
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
         * @param fields
         * @return
         */
        public static String createTableScript(String[] fields){
            return createTableScriptPostgres(fields);
        }
        private static String createTableScriptPostgres(String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureUsers.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureUsers obj: ProcedureUsers.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }  
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ProcedureUsers obj: ProcedureUsers.values()){
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
    
    /**
     *
     */
    public enum ProcedureModuleTablesAndFields{ // 'the sequence is session_id integer NOT NULL DEFAULT nextval('app.app_session_session_id_seq1'::regclass)'

        /**
         *
         */
        TBL("procedure_module_tables",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #SCHEMA_#TBL_pkey PRIMARY KEY (#FLD_PROCEDURE_NAME, #FLD_PROCEDURE_VERSION, #FLD_SCHEMA_NAME, #FLD_TABLE_NAME) ) "
                + LPDatabase.POSTGRESQL_OIDS +  LPDatabase.createTableSpace() + "ALTER TABLE #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull())
        ,
        /**
         *
         */
        FLD_SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_SCHEMA_NAME("schema_name", LPDatabase.stringNotNull()),
        FLD_TABLE_NAME("table_name", LPDatabase.stringNotNull()),
        FLD_FIELD_NAME("field_name", LPDatabase.string()),
        FLD_ACTIVE("active", LPDatabase.booleanFld()),
        ;
        private ProcedureModuleTablesAndFields(String dbObjName, String dbObjType){
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
         * @param fields
         * @return
         */
        public static String createTableScript(String[] fields){
            return createTableScriptPostgres(fields);
        }
        private static String createTableScriptPostgres(String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureModuleTablesAndFields.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureModuleTablesAndFields obj: ProcedureModuleTablesAndFields.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }   
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ProcedureModuleTablesAndFields obj: ProcedureModuleTablesAndFields.values()){
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
    public enum ProcedureBusinessRules { // 'the sequence is session_id integer NOT NULL DEFAULT nextval('app.app_session_session_id_seq1'::regclass)'
        TBL("procedure_business_rules",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #SCHEMA_#TBL_pkey PRIMARY KEY (#FLD_PROCEDURE_NAME, #FLD_PROCEDURE_VERSION, #FLD_SCHEMA_PREFIX, #FLD_FILE_SUFFIX, #FLD_RULE_NAME) ) "
                + LPDatabase.POSTGRESQL_OIDS +  LPDatabase.createTableSpace() + "ALTER TABLE #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")        ,
        FLD_PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull())        ,
        FLD_PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull())        ,
//        FLD_SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull())        ,
        FLD_INSTANCE_NAME("instance_name", LPDatabase.stringNotNull()),
        FLD_CATEGORY("category", LPDatabase.string()),        
        FLD_EXPLANATION("explanation", LPDatabase.string()),        
        FLD_VALUES_ALLOWED("values_allowed", LPDatabase.string()),        
        FLD_MODULE_NAME("module_name", LPDatabase.stringNotNull()),
        FLD_MODULE_VERSION("module_version", LPDatabase.integerNotNull()),
        FLD_FILE_SUFFIX("file_suffix", LPDatabase.string()),
        FLD_RULE_NAME("rule_name", LPDatabase.string()),
        FLD_RULE_VALUE("rule_value", LPDatabase.string()),
        FLD_ACTIVE("active", LPDatabase.booleanFld()),
        FLD_ORDER_NUMBER("order_number", LPDatabase.integer()),
        ;
        private ProcedureBusinessRules(String dbObjName, String dbObjType){
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
         * @param fields
         * @return
         */
        public static String createTableScript(String[] fields){
            return createTableScriptPostgres(fields);
        }
        private static String createTableScriptPostgres(String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureBusinessRules.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureBusinessRules obj: ProcedureBusinessRules.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
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
    
    public enum ProcedureMasterData{ // 'the sequence is session_id integer NOT NULL DEFAULT nextval('app.app_session_session_id_seq1'::regclass)'

        /**
         *
         */
        TBL("procedure_master_data",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #SCHEMA_#TBL_pkey PRIMARY KEY (#FLD_PROCEDURE_NAME, #FLD_PROCEDURE_VERSION, #FLD_INSTANCE_NAME, #FLD_OBJECT_TYPE) ) "
                + LPDatabase.POSTGRESQL_OIDS +  LPDatabase.createTableSpace() + "ALTER TABLE #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
        /**
         *
         */
        FLD_PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull())
        ,

        /**
         *
         */
//        FLD_SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull())        ,
        FLD_INSTANCE_NAME("instance_name", LPDatabase.stringNotNull()),
        FLD_DESCRIPTION(LPDatabase.FIELDS_NAMES_DESCRIPTION, LPDatabase.string()),
        FLD_OBJECT_TYPE("object_type", LPDatabase.string()),
        FLD_JSON_OBJ("json_obj", LPDatabase.string()),
        
        FLD_ACTIVE("active", LPDatabase.booleanFld())

        ;
        private ProcedureMasterData(String dbObjName, String dbObjType){
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
         * @param fields
         * @return
         */
        public static String createTableScript(String[] fields){
            return createTableScriptPostgres(fields);
        }
        private static String createTableScriptPostgres(String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureMasterData.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureMasterData obj: ProcedureMasterData.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }   
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ProcedureMasterData obj: ProcedureMasterData.values()){
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

    public enum ProcedureFrontEndProcModel{ // 'the sequence is session_id integer NOT NULL DEFAULT nextval('app.app_session_session_id_seq1'::regclass)'

        /**
         *
         */
        TBL("fe_proc_model",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #SCHEMA_#TBL_pkey PRIMARY KEY (#FLD_PROCEDURE_NAME, #FLD_PROCEDURE_VERSION, #FLD_INSTANCE_NAME, #FLD_OBJECT_TYPE) ) "
                + LPDatabase.POSTGRESQL_OIDS +  LPDatabase.createTableSpace() + "ALTER TABLE #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
        /**
         *
         */
        FLD_PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull())
        ,

        /**
         *
         */
//        FLD_SCHEMA_PREFIX(FIELDS_NAMES_SCHEMA_PREFIX, LPDatabase.stringNotNull())        ,
        FLD_INSTANCE_NAME("instance_name", LPDatabase.stringNotNull()),
        FLD_DESCRIPTION(LPDatabase.FIELDS_NAMES_DESCRIPTION, LPDatabase.string()),
        FLD_MODEL_JSON("model_json", LPDatabase.string()),
        
        FLD_ACTIVE("active", LPDatabase.booleanFld())

        ;
        private ProcedureFrontEndProcModel(String dbObjName, String dbObjType){
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
         * @param fields
         * @return
         */
        public static String createTableScript(String[] fields){
            return createTableScriptPostgres(fields);
        }
        private static String createTableScriptPostgres(String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureFrontEndProcModel.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureFrontEndProcModel obj: ProcedureFrontEndProcModel.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, GlobalVariables.Schemas.REQUIREMENTS.getName());
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }   
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ProcedureFrontEndProcModel obj: ProcedureFrontEndProcModel.values()){
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

}
