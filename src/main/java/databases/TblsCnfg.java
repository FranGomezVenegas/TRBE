/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import static functionaljavaa.intervals.IntervalsUtilities.DBFIELDNAME_EXPIRY_INTERVAL_INFO;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class TblsCnfg {    
    
    public static final String getTableCreationScriptFromCnfgTable(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "METHODS": return Methods.createTableScript(schemaNamePrefix, fields);
            case "ANALYSIS": return Analysis.createTableScript(schemaNamePrefix, fields);
            case "ANALYSIS_METHOD": return AnalysisMethod.createTableScript(schemaNamePrefix, fields);
            case "ANALYSIS_METHOD_PARAMS": return AnalysisMethodParams.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE": return Sample.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE_RULES": return SampleRules.createTableScript(schemaNamePrefix, fields);
            case "SOP_META_DATA": return SopMetaData.createTableScript(schemaNamePrefix, fields);
            case "SPEC": return Spec.createTableScript(schemaNamePrefix, fields);
            case "SPEC_LIMITS": return SpecLimits.createTableScript(schemaNamePrefix, fields);
            case "SPEC_RULES": return SpecRules.createTableScript(schemaNamePrefix, fields);
            case "UNITS_OF_MEASUREMENT": return UnitsOfMeasurement.createTableScript(schemaNamePrefix, fields);
            case "ANALYSIS_METHODS_VIEW": return ViewAnalysisMethodsView.createTableScript(schemaNamePrefix, fields);
            case "ZZZ_DB_ERROR_LOG": return zzzDbErrorLog.createTableScript(schemaNamePrefix, fields);
            case "ZZZ_PROPERTIES_ERROR": return zzzPropertiesMissing.createTableScript(schemaNamePrefix, fields);
            default: return "TABLE "+tableName+" NOT IN TBLSCNFG"+LPPlatform.LAB_FALSE;
        }        
    }

    /**
     *
     */
    public static final String  SCHEMATAG = "#SCHEMA";

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
        
    /**
     *
     */
    public enum Methods{
        TBL("methods",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_CODE) )" +
            LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")        ,
        FLD_CODE("code", LPDatabase.stringNotNull())        ,
        FLD_CONFIG_VERSION("config_version", LPDatabase.integer())        ,
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string())        ,
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date())        ,        
        FLD_ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld())  ,
        FLD_EXPIRES("expires", LPDatabase.booleanFld()),
        FLD_EXPIRY_INTERVAL_INFO(DBFIELDNAME_EXPIRY_INTERVAL_INFO, LPDatabase.string()),        

        ;
        private Methods(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }
        public String getName(){            return this.dbObjName;        }
        private String[] getDbFieldDefinitionPostgres(){            return new String[]{this.dbObjName, this.dbObjTypePostgres};        }

        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = Methods.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsCnfg.Methods obj: TblsCnfg.Methods.values()){
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
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }
    public enum Analysis{
        TBL("analysis",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_CODE) )" +
            LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")        ,
        FLD_CODE("code", LPDatabase.stringNotNull())        ,
        FLD_CONFIG_VERSION("config_version", LPDatabase.integer())        ,
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string())        ,
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date())        ,        
        FLD_ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld())  ,
        FLD_TESTING_GROUP( LPDatabase.FIELDS_NAMES_TESTING_GROUP, LPDatabase.booleanFld())  ,
        ;
        private Analysis(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }
        public String getName(){            return this.dbObjName;        }
        private String[] getDbFieldDefinitionPostgres(){            return new String[]{this.dbObjName, this.dbObjTypePostgres};        }

        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = Analysis.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsCnfg.Analysis obj: TblsCnfg.Analysis.values()){
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
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }

    /**
     *
     */
    public enum AnalysisMethod{

        /**
         *
         */
        TBL("analysis_method",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_ANALYSIS, #FLD_METHOD_NAME, #FLD_METHOD_VERSION) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_ANALYSIS(Analysis.TBL.getName(), LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string()),
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date()),
        FLD_EXPIRY_INTERVAL_INFO(DBFIELDNAME_EXPIRY_INTERVAL_INFO, LPDatabase.string()),
        ;
        private AnalysisMethod(String dbObjName, String dbObjType){
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
            String[] tblObj = AnalysisMethod.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsCnfg.AnalysisMethod obj: TblsCnfg.AnalysisMethod.values()){
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
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }    

    /**
     *
     */
    public enum AnalysisMethodParams{

        /**
         *
         */
        TBL("analysis_method_params",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_PARAM_NAME, #FLD_ANALYSIS, #FLD_METHOD_NAME, #FLD_METHOD_VERSION) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_PARAM_NAME("param_name", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_ANALYSIS(Analysis.TBL.getName(), LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.stringNotNull())
         ,

        /**
         *
         */
        FLD_METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_MANDATORY("mandatory", LPDatabase.booleanFld())
        ,

        /**
         *
         */
        FLD_PARAM_TYPE("param_type", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_NUM_REPLICAS("num_replicas", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_UOM("uom", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_UOM_CONVERSION_MODE("uom_conversion_mode", LPDatabase.string()),
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string()),
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date())        
        ;
        private AnalysisMethodParams(String dbObjName, String dbObjType){
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
            String[] tblObj = AnalysisMethodParams.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsCnfg.AnalysisMethodParams obj: TblsCnfg.AnalysisMethodParams.values()){
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
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }        

    /**
     *
     */
    public enum Sample{

        /**
         *
         */
        TBL("sample",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_CODE, #FLD_CODE_VERSION)) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_CODE("code", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_CODE_VERSION("code_version", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_DESCRIPTION("description", LPDatabase.string())
        ,
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string()),
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date()),
        /**
         *
         */
        FLD_JSON_DEFINITION("json_definition", "json")
        ,

        /**
         *
         */
        FLD_JSON_DEFINITION_STR("json_definition_str", LPDatabase.string())
        ;
        private Sample(String dbObjName, String dbObjType){
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
        String[] getDbFieldDefinitionPostgres(){
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
            String[] tblObj = Sample.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsCnfg.Sample obj: TblsCnfg.Sample.values()){
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
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }    

    /**
     *
     */
    public enum SampleRules{

        /**
         *
         */
        TBL("sample_rules",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_CODE, #FLD_CODE_VERSION) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_CODE("code", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_CODE_VERSION("code_version", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_STATUSES("statuses", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_DEFAULT_STATUS("default_status", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_TEST_ANALYST_REQUIRED("test_analyst_required", LPDatabase.booleanFld())
        ,

        /**
         *
         */
        FLD_ANALYST_ASSIGNMENT_MODE("analyst_assignment_mode", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_FIELD_DEFAULT_VALUES("field_default_values", LPDatabase.string())
        ,        

        /**
         *
         */
        FLD_AUTO_ADD_SAMPLE_ANALYSIS_LEVEL("auto_add_sample_analysis_lvl", LPDatabase.string()),
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string()),
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date())        
        ;
        private SampleRules(String dbObjName, String dbObjType){
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
            String[] tblObj = SampleRules.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsCnfg.SampleRules obj: TblsCnfg.SampleRules.values()){
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
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }        

    /**
     *
     */
    public enum SopMetaData{
        FLD_SOP_ID("sop_id", "integer NOT NULL DEFAULT nextval('#SCHEMA.#TBL_sop_id_seq'::regclass)")
        ,        
        TBL("sop_meta_data", LPDatabase.createSequence(FLD_SOP_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_SOP_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_SOP_ID, #FLD_SOP_VERSION, #FLD_SOP_REVISION) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
        FLD_SOP_NAME("sop_name", LPDatabase.stringNotNull()),
        FLD_SOP_VERSION("sop_version", LPDatabase.integer()),
        FLD_SOP_REVISION("sop_revision", LPDatabase.integer()),
        FLD_CURRENT_STATUS("current_status", LPDatabase.string()),
        FLD_ADDED_BY("added_by", LPDatabase.string()),
        FLD_FILE_LINK("file_link", LPDatabase.string()),
        FLD_BRIEF_SUMMARY("brief_summary", LPDatabase.string()),
        FLD_EXPIRES("expires", LPDatabase.booleanFld()),
        FLD_HAS_CHILD("has_child", LPDatabase.booleanFld()),
        FLD_AUTHOR("author", LPDatabase.string()),
        FLD_EXPIRY_INTERVAL_INFO(DBFIELDNAME_EXPIRY_INTERVAL_INFO, LPDatabase.string()),        
        FLD_CERTIFICATION_MODE("certification_mode", LPDatabase.string()),
/*        , FLD_ACTIVE_DATE("active_date", "Date")
        , FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.String())
        , FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.Date())
        , FLD_ANALYSES("analyses", LPDatabase.String())
        , FLD_VARIATION_NAMES("variation_names", LPDatabase.String())*/
        ;
        private SopMetaData(String dbObjName, String dbObjType){
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
            String[] tblObj = SopMetaData.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsCnfg.SopMetaData obj: TblsCnfg.SopMetaData.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
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
    }                

    /**
     *
     */
    public enum Spec{

        /**
         *
         */
        TBL("spec",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_CODE, #FLD_CONFIG_VERSION) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_CODE("code", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_CONFIG_VERSION("config_version", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_CATEGORY("category", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_ACTIVE_DATE("active_date", "Date")
        ,

        /**
         *
         */
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date())
        ,

        /**
         *
         */
        FLD_ANALYSES("analyses", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_VARIATION_NAMES("variation_names", LPDatabase.string()),
        FLD_REPORTING_ACCEPTANCE_CRITERIA("reporting_acceptance_criteria", LPDatabase.string())
        
        ;
        private Spec(String dbObjName, String dbObjType){
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
            String[] tblObj = Spec.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsCnfg.Spec obj: TblsCnfg.Spec.values()){
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
            for (TblsCnfg.Spec obj: TblsCnfg.Spec.values()){
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
    public enum SpecLimits{

        /**
         *
         */
        FLD_LIMIT_ID("limit_id", "integer NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_limit_id_seq'::regclass) ")            
        ,
        TBL("spec_limits", LPDatabase.createSequence(FLD_LIMIT_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_LIMIT_ID_seq OWNER TO #OWNER;"
                + " CREATE TABLE #SCHEMA.#TBL (#FLDS , CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_LIMIT_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,            

        /**
         *
         */


        /**
         *
         */
        FLD_CODE("code", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_CONFIG_VERSION("config_version", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_VARIATION_NAME("variation_name", LPDatabase.string())
        ,            

        /**
         *
         */
        FLD_ANALYSIS(Analysis.TBL.getName(), LPDatabase.stringNotNull())            
        ,

        /**
         *
         */
        FLD_METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_PARAMETER("parameter", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_RULE_TYPE("rule_type", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_RULE_VARIABLES("rule_variables", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_UOM("uom", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_UOM_CONVERSION_MODE("uom_conversion_mode", LPDatabase.string()),
        FLD_MIN_VAL_ALLOWED("min_val_allowed", LPDatabase.real()),
        FLD_MAX_VAL_ALLOWED("max_val_allowed", LPDatabase.real()),
        FLD_MIN_VAL_ALLOWED_IS_STRICT("min_allowed_strict", LPDatabase.booleanFld(false)),
        FLD_MAX_VAL_ALLOWED_IS_STRICT("max_allowed_strict", LPDatabase.booleanFld(false)),        
        FLD_MIN_VAL_FOR_UNDETERMINED("min_undetermined", LPDatabase.real()),
        FLD_MAX_VAL_FOR_UNDETERMINED("max_undetermined", LPDatabase.real()),
        FLD_MIN_VAL_UNDETERMINED_IS_STRICT("min_undet_strict", LPDatabase.booleanFld(false)),
        FLD_MAX_VAL_UNDETERMINED_IS_STRICT("max_undet_strict", LPDatabase.booleanFld(false)),
        FLD_TESTING_GROUP("testing_group",  LPDatabase.string()),    
        FLD_SPEC_TEXT_EN("spec_text_en",  LPDatabase.string()),    
        FLD_SPEC_TEXT_RED_AREA_EN("spec_text_red_area_en",  LPDatabase.string()),    
        FLD_SPEC_TEXT_YELLOW_AREA_EN("spec_text_yellow_area_en",  LPDatabase.string()),    
        FLD_SPEC_TEXT_GREEN_AREA_EN("spec_text_green_area_en",  LPDatabase.string()),    
        FLD_SPEC_TEXT_ES("spec_text_es",  LPDatabase.string()),    
        FLD_SPEC_TEXT_RED_AREA_ES("spec_text_red_area_es",  LPDatabase.string()),    
        FLD_SPEC_TEXT_YELLOW_AREA_ES("spec_text_yellow_area_es",  LPDatabase.string()),    
        FLD_SPEC_TEXT_GREEN_AREA_ES("spec_text_green_area_es",  LPDatabase.string()),    
        
        ;
        private SpecLimits(String dbObjName, String dbObjType){
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
            String[] tblObj = SpecLimits.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsCnfg.SpecLimits obj: TblsCnfg.SpecLimits.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
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
    }        

    /**
     *
     */
    public enum SpecRules{

        /**
         *
         */
        TBL("spec_rules",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_CODE, #FLD_CONFIG_VERSION) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";"
                + "COMMENT ON COLUMN #SCHEMA.#TBL.#FLD_ANALYSIS_NOT_DECLARED_LEVEL IS 'OPEN|ANALYSES_SPEC_LIST|SPEC_LIMIT_DEFINITION';")
        ,

        /**
         *
         */
        FLD_CODE("code", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_CONFIG_VERSION("config_version", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_ALLOW_OTHER_ANALYSIS("allow_other_analysis", LPDatabase.booleanFld())
        ,

        /**
         *
         */
        FLD_ALLOW_MULTI_SPEC("allow_multi_spec", LPDatabase.booleanFld())
        ,

        /**
         *
         */
        FLD_ANALYSIS_NOT_DECLARED_LEVEL("analysis_not_declared_level", LPDatabase.string())
        ;
        private SpecRules(String dbObjName, String dbObjType){
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
            String[] tblObj = SpecRules.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsCnfg.SpecRules obj: TblsCnfg.SpecRules.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
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
    }                

    /**
     *
     */
    public enum UnitsOfMeasurement{

        /**
         *
         */
        TBL("units_of_measurement",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_NAME) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_NAME("name", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_PRETTY_NAME("pretty_name", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_MEASUREMENT_FAMILY("measurement_family", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_IS_BASE("is_base", LPDatabase.booleanFld())
        ,

        /**
         *
         */
        FLD_FACTOR_VALUE("factor_value", LPDatabase.real())
        ,

        /**
         *
         */
        FLD_OFFSET_VALUE("offset_value", LPDatabase.real())
        ,

        /**
         *
         */
        FLD_DESCRIPTION("description", LPDatabase.string())
        ;
        private UnitsOfMeasurement(String dbObjName, String dbObjType){
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
            String[] tblObj = UnitsOfMeasurement.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsCnfg.UnitsOfMeasurement obj: TblsCnfg.UnitsOfMeasurement.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
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
            for (UnitsOfMeasurement obj: UnitsOfMeasurement.values()){
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
    public enum ViewAnalysisMethodsView{

        /**
         *
         */
        TBL("analysis_methods_view", " CREATE OR REPLACE VIEW #SCHEMA.#TBL AS " +
                " SELECT a.code, " +
                "    meth.method_name, " +
                "    meth.method_version " +
                "   FROM #SCHEMA.analysis a, " +
                "    #SCHEMA.analysis_method meth " +
                "  WHERE meth.analysis::text = a.code::text AND a.active = true;" +
                "ALTER TABLE  #SCHEMA.#TBL  OWNER TO #OWNER;" +
                "GRANT ALL ON TABLE  #SCHEMA.#TBL TO #OWNER;")
        ,

        /**
         *
         */
        FLD_CODE("code", "")
        ,

        /**
         *
         */
        FLD_METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, "")
        ,

        /**
         *
         */
        FLD_METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, "")
        ;
        private ViewAnalysisMethodsView(String dbObjName, String dbObjType){
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
            String[] tblObj = ViewAnalysisMethodsView.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsCnfg.ViewAnalysisMethodsView obj: TblsCnfg.ViewAnalysisMethodsView.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
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
    }        

    /**
     *
     */
    public enum zzzDbErrorLog{

        /**
         *
         */
        FLD_ID("id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_id_seq'::regclass)")
        ,
        TBL("zzz_db_error_log", LPDatabase.createSequence(FLD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
        FLD_CREATION_DATE("creation_date", "timestamp NOT NULL DEFAULT NOW()")
        ,
        FLD_ACTION_NAME("action_name", LPDatabase.string())        ,
        FLD_QUERY("query", LPDatabase.string())
        ,
        FLD_QUERY_PARAMETERS("query_parameters", LPDatabase.string())
        ,
        FLD_ERROR_MESSAGE("error_message", LPDatabase.stringNotNull())
        ,
        FLD_CLASS_CALLER("class_caller_info", LPDatabase.string())
        ,
        FLD_RESOLVED("resolved", LPDatabase.booleanFld(false))
        ,
        FLD_RESOLUTION_NOTES("resolution_notes", LPDatabase.real())
        ;
        private zzzDbErrorLog(String dbObjName, String dbObjType){
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
            String[] tblObj = zzzDbErrorLog.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsCnfg.zzzDbErrorLog obj: TblsCnfg.zzzDbErrorLog.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
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
    }        

    /**
     *
     */
    public enum zzzPropertiesMissing{

        /**
         *
         */
        FLD_ID("id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_id_seq'::regclass)")
        ,        
        TBL("zzz_properties_error", LPDatabase.createSequence(FLD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
        FLD_CREATION_DATE("creation_date", "date NOT NULL DEFAULT NOW()")
        ,
        FLD_AREA("area", LPDatabase.string())
        ,
        FLD_RULE_NAME("rule_name", LPDatabase.string())
        ,
        FLD_PROCEDURE("procedure", LPDatabase.string())
        ,
        FLD_ACTION_NAME("action_name", LPDatabase.string())        ,
        FLD_CLASS_CALLER("class_caller_info", LPDatabase.string())
        ,
        FLD_RESOLVED("resolved", LPDatabase.booleanFld(false))
        ,
        FLD_RESOLUTION_NOTES("resolution_notes", LPDatabase.real())
        ;
        private zzzPropertiesMissing(String dbObjName, String dbObjType){
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
            String[] tblObj = zzzPropertiesMissing.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsCnfg.zzzPropertiesMissing obj: TblsCnfg.zzzPropertiesMissing.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
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
    }        
}
