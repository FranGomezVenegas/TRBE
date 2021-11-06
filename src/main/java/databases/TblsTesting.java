/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import static databases.TblsCnfg.SCHEMATAG;
import static databases.TblsCnfg.TABLETAG;
import static databases.TblsCnfg.OWNERTAG;
import static databases.TblsCnfg.TABLESPACETAG;
import static databases.TblsCnfg.FIELDSTAG;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class TblsTesting {
    public static final String getTableCreationScriptFromTestingTable(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "SCRIPT": return Script.createTableScript(schemaNamePrefix, fields);
            case "SCRIPT_STEPS": return ScriptSteps.createTableScript(schemaNamePrefix, fields);
            default: return "TABLE "+tableName+" NOT IN ENVMONIT_TBLSCNFGENVMONIT"+LPPlatform.LAB_FALSE;
        }        
    }    
    public enum Script{

        /**
         *
         */
        FLD_SCRIPT_ID("script_id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_script_id_seq'::regclass)"),
        TBL("script", LPDatabase.createSequence(FLD_SCRIPT_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_SCRIPT_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #FLD_SCRIPT_ID_pkey PRIMARY KEY (#FLD_SCRIPT_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;")
        ,
        FLD_ACTIVE("active", LPDatabase.booleanFld()),
        FLD_DATE_CREATION("date_creation", LPDatabase.dateTimeWithDefaultNow()),
        FLD_DATE_EXECUTION("date_execution", LPDatabase.dateTime()),
        FLD_TIME_STARTED("time_started", LPDatabase.dateTime()),           
        FLD_TIME_COMPLETED("time_completed", LPDatabase.dateTime()),           
        FLD_TIME_CONSUME("time_consume", LPDatabase.real()),                   
        FLD_PURPOSE("purpose", LPDatabase.string()),
        FLD_TESTER_PROGRESS_PERCENTAGE("tester_progress_percentage", LPDatabase.integer()),
        FLD_INCLUDE_IN_SCHED("include_in_run_scheduled", LPDatabase.booleanFld()),
        FLD_TESTER_NAME("tester_name", LPDatabase.string()),
        FLD_EVAL_NUM_ARGS("num_eval_args", LPDatabase.integer())
        ,
        FLD_EVAL_TOTAL_TESTS("eval_total_tests", LPDatabase.integer())
        ,
        FLD_EVAL_SYNTAXIS_MATCH("eval_syntaxis_match", LPDatabase.integer()),
        FLD_EVAL_SYNTAXIS_UNMATCH("eval_syntaxis_unmatch", LPDatabase.integer()),
        FLD_EVAL_SYNTAXIS_UNDEFINED("eval_syntaxis_undefined", LPDatabase.integer()),
        FLD_EVAL_CODE_MATCH("eval_code_match", LPDatabase.integer()),
        FLD_EVAL_CODE_UNMATCH("eval_code_unmatch", LPDatabase.integer()),
        FLD_EVAL_CODE_UNDEFINED("eval_code_undefined", LPDatabase.integer()),
        
        FLD_AUDIT_IDS_TO_GET("audit_ids_to_get", LPDatabase.string()),
        FLD_AUDIT_IDS_VALUES("audit_ids_values", LPDatabase.string()),
        FLD_RUN_SUMMARY("run_summary", LPDatabase.string()),
        FLD_GET_DB_ERRORS("get_db_errors", LPDatabase.booleanFld()),
        FLD_DB_ERRORS_IDS_VALUES("db_error_ids_values", LPDatabase.string()),
        FLD_GET_MSG_ERRORS("get_msg_errors", LPDatabase.booleanFld()),
        FLD_MSG_ERRORS_IDS_VALUES("msg_error_ids_values", LPDatabase.string()),

        FLD_BUSINESS_RULES_VISITED("business_rules_visited", LPDatabase.string()),
        FLD_MESSAGES_VISITED("messages_visited", LPDatabase.string()),
        
        ;
        private Script(String dbObjName, String dbObjType){
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
         * @return One Create-Table ScriptSteps for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = Script.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.TESTING.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (Script obj: Script.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.TESTING.getName()));
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
            for (ScriptSteps obj: ScriptSteps.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }              
    }

    public enum ScriptSteps{

        /**
         *
         */
        FLD_SCRIPT_ID("script_id", LPDatabase.integerNotNull()),
        FLD_STEP_ID("step_id", LPDatabase.integerNotNull()),
        TBL("script_steps", LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_#FLD_STEP_ID_pkey PRIMARY KEY (#FLD_SCRIPT_ID, #FLD_STEP_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;")
        ,
        FLD_ACTIVE("active", LPDatabase.booleanFld(true)), 
        FLD_TESTER_NOTES("tester_notes", LPDatabase.string()),   
        FLD_EXPECTED_SYNTAXIS("expected_syntaxis", LPDatabase.string()),
        FLD_EXPECTED_CODE("expected_code", LPDatabase.string()),
        FLD_TIME_STARTED("time_started", LPDatabase.dateTime()),           
        FLD_TIME_COMPLETED("time_completed", LPDatabase.dateTime()),           
        FLD_TIME_CONSUME("time_consume", LPDatabase.real()),                   
        FLD_ARGUMENT_01("argument_01", LPDatabase.string()),
        FLD_ARGUMENT_02("argument_02", LPDatabase.string()),
        FLD_ARGUMENT_03("argument_03", LPDatabase.string()),
        FLD_ARGUMENT_04("argument_04", LPDatabase.string()),
        FLD_ARGUMENT_05("argument_05", LPDatabase.string()),
        FLD_ARGUMENT_06("argument_06", LPDatabase.string()),
        FLD_ARGUMENT_07("argument_07", LPDatabase.string()),
        FLD_ARGUMENT_08("argument_08", LPDatabase.string()),
        FLD_ARGUMENT_09("argument_09", LPDatabase.string()),
        FLD_ARGUMENT_10("argument_10", LPDatabase.string()),
        FLD_EVAL_SYNTAXIS("eval_syntaxis", LPDatabase.string()),
        FLD_EVAL_CODE("eval_code", LPDatabase.string()),
        FLD_FUNCTION_RETURN("function_return", LPDatabase.string()),
        FLD_FUNCTION_SYNTAXIS("function_syntaxis", LPDatabase.string()),
        FLD_FUNCTION_CODE("function_code", LPDatabase.string()),
        FLD_DYNAMIC_DATA("dynamic_data", LPDatabase.string()),        
        FLD_DATE_EXECUTION("date_execution", LPDatabase.dateTime()),   
        FLD_ESIGN_TO_CHECK("esign_to_check", LPDatabase.string()),   
        FLD_CONFIRMUSER_USER_TO_CHECK("confirmuser_user_to_check", LPDatabase.string()),   
        FLD_CONFIRMUSER_PW_TO_CHECK("confirmuser_pw_to_check", LPDatabase.string()),   
        FLD_AUDIT_REASON("audit_reason", LPDatabase.string()),   
        FLD_STOP_WHEN_SYNTAXIS_UNMATCH("stop_when_syntaxis_unmatch", LPDatabase.booleanFld()),   
        FLD_STOP_WHEN_SYNTAXIS_FALSE("stop_when_function_syntaxis_returns_false", LPDatabase.booleanFld()),   
        FLD_ALTERNATIVE_TOKEN("alternative_token", LPDatabase.string()),   
        ;
        private ScriptSteps(String dbObjName, String dbObjType){
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
         * @return One Create-Table ScriptSteps for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ScriptSteps.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.TESTING.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ScriptSteps obj: ScriptSteps.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.TESTING.getName()));
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
            for (ScriptSteps obj: ScriptSteps.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }              
    }

    public enum ScriptBusinessRules{

        /**
         *
         */
        FLD_SCRIPT_ID("script_id", LPDatabase.integerNotNull()),
        FLD_ID("id", LPDatabase.integerNotNull()),
        TBL("script_business_rules", LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_#FLD_STEP_ID_pkey PRIMARY KEY (#FLD_SCRIPT_ID, #FLD_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;")
        ,
        FLD_REPOSITORY("repository", LPDatabase.string()),   
        FLD_RULE_NAME("rule_name", LPDatabase.string()),
        FLD_RULE_VALUE("rule_value", LPDatabase.string()),
        FLD_ACTIVE("active", LPDatabase.booleanFld(true)), 
        ;
        private ScriptBusinessRules(String dbObjName, String dbObjType){
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
         * @return One Create-Table ScriptSteps for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ScriptBusinessRules.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.TESTING.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ScriptBusinessRules obj: ScriptBusinessRules.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.TESTING.getName()));
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
            for (ScriptBusinessRules obj: ScriptBusinessRules.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }              
    }
    
    public enum ScriptsCoverage{
        FLD_COVERAGE_ID("coverage_id", LPDatabase.integerNotNull()),
        TBL("scripts_coverage", LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_#FLD_COVERAGE_ID_pkey PRIMARY KEY (#FLD_COVERAGE_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;")
        ,
        FLD_ACTIVE("active", LPDatabase.booleanFld()),
        FLD_DATE_CREATION("date_creation", LPDatabase.dateTimeWithDefaultNow()),
        FLD_DATE_EXECUTION("date_execution", LPDatabase.dateTime()),
        FLD_PURPOSE("purpose", LPDatabase.string()),
        FLD_SCRIPT_IDS_LIST("script_ids_list", LPDatabase.string()),
        FLD_ENDPOINTS_COVERAGE("endpoints_coverage", LPDatabase.real()),
        FLD_ENDPOINTS_EXCLUDE_LIST("endpoints_exclude_list", LPDatabase.string()),
        FLD_ENDPOINTS_COVERAGE_DETAIL("endpoints_coverage_detail", LPDatabase.string()),
        FLD_BUS_RULES_COVERAGE("bus_rule_coverage", LPDatabase.real()),
        FLD_BUS_RULES_EXCLUDE_LIST("bus_rule_exclude_list", LPDatabase.string()),        
        FLD_BUS_RULES_COVERAGE_DETAIL("bus_rule_coverage_detail", LPDatabase.string()),        
        FLD_MSG_COVERAGE("msg_coverage", LPDatabase.real()),
        FLD_MSG_COVERAGE_DETAIL("msg_coverage_detail", LPDatabase.string()),
        
        
        ;
        private ScriptsCoverage(String dbObjName, String dbObjType){
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
         * @return One Create-Table ScriptSteps for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ScriptsCoverage.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.TESTING.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ScriptsCoverage obj: ScriptsCoverage.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.TESTING.getName()));
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
            for (ScriptsCoverage obj: ScriptsCoverage.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }              
    }
    
}
