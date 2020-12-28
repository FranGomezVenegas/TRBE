/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPPlatform;
import static lbplanet.utilities.LPDatabase.*;
import static databases.TblsCnfg.SCHEMATAG;
import static databases.TblsCnfg.TABLETAG;
import static databases.TblsCnfg.OWNERTAG;
import static databases.TblsCnfg.TABLESPACETAG;
import static databases.TblsCnfg.FIELDSTAG;

/**
 *
 * @author Administrator
 */
public class TblsData {
    private static final java.lang.String FIELDS_NAMES_LIGHT = "light";
    public static final String FIELDS_NAMES_USER_ID="user_id";
    public static final String FIELDS_NAMES_USER_NAME="user_name";

    public static final String getTableCreationScriptFromDataTable(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "SAMPLE": return Sample.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE_ALIQ": return SampleAliq.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE_ALIQ_SUB": return SampleAliqSub.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE_ANALYSIS": return SampleAnalysis.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE_ANALYSIS_RESULT": return SampleAnalysisResult.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE_COC": return SampleCoc.createTableScript(schemaNamePrefix, fields);
            case "USER_ANALYSIS_METHOD": return UserAnalysisMethod.createTableScript(schemaNamePrefix, fields);
            case "USER_SOP": return UserSop.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW": return ViewSampleAnalysisResultWithSpecLimits.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE_COC_NAMES_VIEW": return ViewSampleCocNames.createTableScript(schemaNamePrefix, fields);
            case "USER_AND_META_DATA_SOP_VIEW": return ViewUserAndMetaDataSopView.createTableScript(schemaNamePrefix, fields);
            default: return "TABLE "+tableName+" NOT IN TBLDATA "+LPPlatform.LAB_FALSE;
        }        
    }
    private static final java.lang.String FIELDS_NAMES_STATUS_PREVIOUS = "status_previous";
    private static final java.lang.String FIELDS_NAMES_STATUS = "status";
    private static final java.lang.String FIELDS_NAMES_SPEC_EVAL = "spec_eval";
    private static final java.lang.String FIELDS_NAMES_CUSTODIAN_CANDIDATE = "custodian_candidate";
    private static final java.lang.String FIELDS_NAMES_CUSTODIAN = "custodian";
    private static final java.lang.String FIELDS_NAMES_COC_CONFIRMED_ON = "coc_confirmed_on";
    
    /**
     *
     */
    public enum Sample{

        /**
         *
         */
        FLD_SAMPLE_ID(LPDatabase.FIELDS_NAMES_SAMPLE_ID, "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_sample_id_seq'::regclass)")
        ,        
        TBL("sample", LPDatabase.createSequence(FLD_SAMPLE_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_SAMPLE_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_SAMPLE_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_CONFIG_CODE("sample_config_code", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_CONFIG_CODE_VERSION("sample_config_code_version", LPDatabase.integer())
        ,

        /**
         *
         */


        /**
         *
         */
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_STATUS_PREVIOUS(FIELDS_NAMES_STATUS_PREVIOUS, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_LOGGED_ON("logged_on", LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_LOGGED_BY("logged_by", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_RECEIVED_ON("received_on", LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_RECEIVED_BY("received_by", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_VOLUME( LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.real())
        ,

        /**
         *
         */
        FLD_VOLUME_UOM( LPDatabase.FIELDS_NAMES_VOLUME_UOM, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_ALIQUOTED("aliquoted", LPDatabase.booleanFld(false))
        ,

        /**
         *
         */
        FLD_ALIQUOT_STATUS("aliq_status", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.real())
        ,

        /**
         *
         */
        FLD_VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_SAMPLING_DATE("sampling_date", LPDatabase.dateTime())
        ,
        FLD_SAMPLER("sampler",LPDatabase.string()),
        FLD_SAMPLE_ID_RELATED("sample_id_related",LPDatabase.integer()),        

        /**
         *
         */
        FLD_SAMPLING_COMMENT("sampling_comment", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_INCUBATION_INCUBATOR("incubation_incubator", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_INCUBATION_START("incubation_start", LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_INCUBATION_START_TEMPERATURE("incubation_start_temperature", LPDatabase.real())
        ,

        /**
         *
         */
        FLD_INCUBATION_START_TEMP_EVENT_ID("incubation_start_temp_event_id", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_INCUBATION_END("incubation_end", LPDatabase.dateTime())
        ,        

        /**
         *
         */
        FLD_INCUBATION_END_TEMPERATURE("incubation_end_temperature", LPDatabase.real())        
        ,

        /**
         *
         */
        FLD_INCUBATION_END_TEMP_EVENT_ID("incubation_end_temp_event_id", LPDatabase.integer())
        ,        

        /**
         *
         */
        FLD_INCUBATION_PASSED("incubation_passed", LPDatabase.booleanFld(false))        
        ,

        /**
         *
         */
        FLD_INCUBATION2_INCUBATOR("incubation2_incubator", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_INCUBATION2_START("incubation2_start", LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_INCUBATION2_START_TEMPERATURE("incubation2_start_temperature", LPDatabase.real())
        ,

        /**
         *
         */
        FLD_INCUBATION2_START_TEMP_EVENT_ID("incubation2_start_temp_event_id", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_INCUBATION2_END("incubation2_end", LPDatabase.dateTime())
        ,        

        /**
         *
         */
        FLD_INCUBATION2_END_TEMPERATURE("incubation2_end_temperature", LPDatabase.real())        
        ,

        /**
         *
         */
        FLD_INCUBATION2_END_TEMP_EVENT_ID("incubation2_end_temp_event_id", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_INCUBATION2_PASSED("incubation2_passed", LPDatabase.booleanFld())
        ,

        /**
         *
         */
        FLD_SPEC_CODE("spec_code", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_SPEC_CODE_VERSION("spec_code_version", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_SPEC_VARIATION_NAME("spec_variation_name", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_SPEC_ANALYSIS_VARIATION("spec_analysis_variation", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_SPEC_EVAL(FIELDS_NAMES_SPEC_EVAL,  LPDatabase.stringNotNull(2))
        ,

        /**
         *
         */
        FLD_CUSTODIAN(FIELDS_NAMES_CUSTODIAN,  LPDatabase.stringNotNull(2))
        ,

        /**
         *
         */
        FLD_CUSTODIAN_CANDIDATE(FIELDS_NAMES_CUSTODIAN_CANDIDATE,  LPDatabase.stringNotNull(2))
        ,

        /**
         *
         */
        FLD_COC_REQUESTED_ON("coc_requested_on", LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_COC_CONFIRMED_ON(FIELDS_NAMES_COC_CONFIRMED_ON, LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_CURRENT_STAGE("current_stage", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_PREVIOUS_STAGE("previous_stage", LPDatabase.string()),
        FLD_READY_FOR_REVISION("ready_for_revision", LPDatabase.booleanFld()),        
        ;
        private Sample(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return
         */
        public String getName(){return this.dbObjName;}
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix
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
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (Sample obj: Sample.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
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

    public enum SampleRevisionTestingGroup{

        /**
         *
         */
        FLD_SAMPLE_ID(LPDatabase.FIELDS_NAMES_SAMPLE_ID, LPDatabase.integer()),
        FLD_TESTING_GROUP("testing_group", LPDatabase.string()),                
        TBL("sample_revision_testing_group",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_SAMPLE_ID, #FLD_TESTING_GROUP) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,        
        FLD_READY_FOR_REVISION("ready_for_revision", LPDatabase.booleanFld())
        ,
        FLD_REVIEWED("reviewed", LPDatabase.booleanFld())
        ,
        FLD_REVISION_ON("revision_on", LPDatabase.dateTime())
        ,
        FLD_REVISION_BY("revision_by", LPDatabase.string())
        ;
        private SampleRevisionTestingGroup(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return
         */
        public String getName(){return this.dbObjName;}
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = SampleRevisionTestingGroup.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (SampleRevisionTestingGroup obj: SampleRevisionTestingGroup.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
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
    public enum SampleAnalysis{

        /**
         *
         */
        FLD_TEST_ID(FIELDS_NAMES_TEST_ID, "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_test_id_seq'::regclass)")
        ,        
        TBL("sample_analysis", LPDatabase.createSequence(FLD_TEST_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_TEST_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_TEST_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */


        /**
         *
         */
        FLD_SAMPLE_ID(LPDatabase.FIELDS_NAMES_SAMPLE_ID, LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_STATUS_PREVIOUS(FIELDS_NAMES_STATUS_PREVIOUS, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_ANALYSIS(FIELDS_NAMES_ANALYSIS, LPDatabase.stringNotNull())
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
        FLD_REPLICA(FIELDS_NAMES_REPLICA, LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_ADDED_ON("added_on", LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_ADDED_BY("added_by", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_SPEC_EVAL(FIELDS_NAMES_SPEC_EVAL,  LPDatabase.string(2))
        ,

        /**
         *
         */
        FLD_REVIEWER("reviewer", LPDatabase.string())
        ,        

        /**
         *
         */
        FLD_REVIEWER_ASSIGNED_ON("reviewer_assigned_on", LPDatabase.dateTime())        
        ,        

        /**
         *
         */
        FLD_REVIEWER_ASSIGNED_BY("reviewer_assigned_by", LPDatabase.string())        
        ,

        /**
         *
         */
        FLD_ANALYST("analyst", LPDatabase.string())
        ,        

        /**
         *
         */
        FLD_ANALYST_ASSIGNED_ON("analyst_assigned_on", LPDatabase.dateTime())        
        ,        

        /**
         *
         */
        FLD_ANALYST_ASSIGNED_BY("analyst_assigned_by", LPDatabase.string())        
        ,

        /**
         *
         */
        FLD_ANALYST_CERTIFICATION_MODE("analyst_certification_mode", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, LPDatabase.integer())
        ,
        FLD_TESTING_GROUP("testing_group",  LPDatabase.string()),
        FLD_READY_FOR_REVISION("ready_for_revision", LPDatabase.booleanFld()),                
        /**
         *
         */
        FLD_SUBALIQUOT_ID(FIELDS_NAMES_SUBALIQUOT_ID, LPDatabase.integer())
        //, FLD_UNDER_DEVIATION("under_deviation", LPDatabase.Boolean()) Desviaciones aún no implementadas
/*     Este bloque de campos está a nivel de Sample, es posible que pueda ser interesante tb en sample_analysis   
        , FLD_VOLUME( LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.Real())
        , FLD_VOLUME_UOM( LPDatabase.FIELDS_NAMES_VOLUME_UOM, LPDatabase.StringNotNull())
        , FLD_ALIQUOTED("aliquoted", LPDatabase.Boolean(false))
        , FLD_ALIQUOT_STATUS("aliq_status", LPDatabase.StringNotNull())
        , FLD_VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.Real())
        , FLD_VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom", LPDatabase.StringNotNull())
        , FLD_SAMPLING_DATE("sampling_date", LPDatabase.dateTime())
        , FLD_SAMPLING_COMMENT("sampling_comment", LPDatabase.String())
        , FLD_INCUBATION_START("incubation_start", LPDatabase.dateTime())
        , FLD_INCUBATION_END("incubation_end", LPDatabase.dateTime())
        , FLD_INCUBATION_PASSED("incubation_passed", LPDatabase.Boolean())*/ 
        ;
        private SampleAnalysis(String dbObjName, String dbObjType){
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
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = SampleAnalysis.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (SampleAnalysis obj: SampleAnalysis.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
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
            for (SampleAnalysis obj: SampleAnalysis.values()){
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
    private static final java.lang.String FIELDS_NAMES_TEST_ID = "test_id";
    private static final java.lang.String FIELDS_NAMES_ANALYSIS = "analysis";
    private static final java.lang.String FIELDS_NAMES_REPLICA = "replica";
    private static final java.lang.String FIELDS_NAMES_SUBALIQUOT_ID = "subaliquot_id";
    private static final java.lang.String FIELDS_NAMES_ALIQUOT_ID = "aliquot_id";

    /**
     *
     */
    public enum SampleAnalysisResult{

        /**
         *
         */
        FLD_RESULT_ID("result_id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_result_id_seq'::regclass)")
        ,        
        TBL("sample_analysis_result", LPDatabase.createSequence(FLD_RESULT_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_RESULT_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_RESULT_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */


        /**
         *
         */
        FLD_TEST_ID(FIELDS_NAMES_TEST_ID, LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_SAMPLE_ID(LPDatabase.FIELDS_NAMES_SAMPLE_ID, LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_STATUS_PREVIOUS(FIELDS_NAMES_STATUS_PREVIOUS, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_ANALYSIS(FIELDS_NAMES_ANALYSIS, LPDatabase.stringNotNull())
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
        FLD_REPLICA(FIELDS_NAMES_REPLICA, LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_PARAM_NAME("param_name", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_PARAM_TYPE("param_type", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_MANDATORY("mandatory", LPDatabase.booleanFld(false))
        ,

        /**
         *
         */
        FLD_REQUIRES_LIMIT("requires_limit", LPDatabase.booleanFld(false))
        ,

        /**
         *
         */
        FLD_RAW_VALUE("raw_value", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_PRETTY_VALUE("pretty_value", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_ENTERED_ON("entered_on", LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_ENTERED_BY("entered_by", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_REENTERED("reentered", LPDatabase.booleanFld())
        ,

        /**
         *
         */
        FLD_SPEC_EVAL(FIELDS_NAMES_SPEC_EVAL, LPDatabase.string(200))
        ,

        /**
         *
         */
        FLD_SPEC_EVAL_DETAIL("spec_eval_detail",  LPDatabase.string(200))
        ,        

        /**
         *
         */
        FLD_UOM("uom", LPDatabase.string())        
        ,        

        /**
         *
         */
        FLD_UOM_CONVERSION_MODE("uom_conversion_mode", LPDatabase.string())        
        ,

        /**
         *
         */
        FLD_ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_SUBALIQUOT_ID(FIELDS_NAMES_SUBALIQUOT_ID, LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_LIMIT_ID("limit_id", LPDatabase.integer())
        
        /* Este bloque de campos está a nivel de SampleAnalysis, es posible que pueda ser interesante tb en sample_analysis_result
        , FLD_REVIEWER("reviewer", LPDatabase.String())
        , FLD_REVIEWER_ASSIGNED_ON("reviewer_assigned_on", LPDatabase.dateTime())        
        , FLD_REVIEWER_ASSIGNED_BY("reviewer_assigned_by", LPDatabase.String())        
        , FLD_ANALYST("analyst", LPDatabase.String())
        , FLD_ANALYST_ASSIGNED_ON("analyst_assigned_on", LPDatabase.dateTime())        
        , FLD_ANALYST_ASSIGNED_BY("analyst_assigned_by", LPDatabase.String())        
        , FLD_ANALYST_CERTIFICATION_MODE("analyst_certification_mode", LPDatabase.String()) */
        //, FLD_UNDER_DEVIATION("under_deviation", LPDatabase.Boolean()) Desviaciones aún no implementadas
/*     Este bloque de campos está a nivel de Sample, es posible que pueda ser interesante tb en sample_analysis   
        , FLD_VOLUME( LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.Real())
        , FLD_VOLUME_UOM( LPDatabase.FIELDS_NAMES_VOLUME_UOM, LPDatabase.StringNotNull())
        , FLD_ALIQUOTED("aliquoted", LPDatabase.Boolean(false))
        , FLD_ALIQUOT_STATUS("aliq_status", LPDatabase.StringNotNull())
        , FLD_VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.Real())
        , FLD_VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom", LPDatabase.StringNotNull())
        , FLD_SAMPLING_DATE("sampling_date", LPDatabase.dateTime())
        , FLD_SAMPLING_COMMENT("sampling_comment", LPDatabase.String())
        , FLD_INCUBATION_START("incubation_start", LPDatabase.dateTime())
        , FLD_INCUBATION_END("incubation_end", LPDatabase.dateTime())
        , FLD_INCUBATION_PASSED("incubation_passed", LPDatabase.Boolean())*/ 
        ;
        private SampleAnalysisResult(String dbObjName, String dbObjType){
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
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = SampleAnalysisResult.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (SampleAnalysisResult obj: SampleAnalysisResult.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
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
    public enum SampleAliq{

        /**
         *
         */
        FLD_ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_aliquot_id_seq'::regclass)")
        ,        
        TBL("sample_aliq", LPDatabase.createSequence(FLD_ALIQUOT_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_ALIQUOT_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_ALIQUOT_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */


        /**
         *
         */
        FLD_SAMPLE_ID(LPDatabase.FIELDS_NAMES_SAMPLE_ID, LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_CREATED_ON("created_on", LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_CREATED_BY("created_by", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_SUBALIQ_STATUS("subaliq_status", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_VOLUME( LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.real())
        ,

        /**
         *
         */
        FLD_VOLUME_UOM( LPDatabase.FIELDS_NAMES_VOLUME_UOM, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.real())
        ,

        /**
         *
         */
        FLD_VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom", LPDatabase.string())
        ;
        private SampleAliq(String dbObjName, String dbObjType){
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
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = SampleAliq.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (SampleAliq obj: SampleAliq.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
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
    public enum SampleAliqSub{

        /**
         *
         */
        FLD_SUBALIQUOT_ID(FIELDS_NAMES_SUBALIQUOT_ID, "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_subaliquot_id_seq'::regclass)")
        ,        
        TBL("sample_aliq_sub", LPDatabase.createSequence(FLD_SUBALIQUOT_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_SUBALIQUOT_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_SUBALIQUOT_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */


        /**
         *
         */
        FLD_SAMPLE_ID(LPDatabase.FIELDS_NAMES_SAMPLE_ID, LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_CREATED_ON("created_on", LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_CREATED_BY("created_by", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_VOLUME( LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.real())
        ,

        /**
         *
         */
        FLD_VOLUME_UOM( LPDatabase.FIELDS_NAMES_VOLUME_UOM, LPDatabase.string())
        ;
        private SampleAliqSub(String dbObjName, String dbObjType){
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
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = SampleAliqSub.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (SampleAliqSub obj: SampleAliqSub.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
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
    public enum SampleCoc{

        /**
         *
         */
        FLD_ID("id", "integer NOT NULL DEFAULT nextval('#SCHEMA.#TBL_id_seq'::regclass)")
        ,        
        TBL("sample_coc", LPDatabase.createSequence(FLD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */


        /**
         *
         */
        FLD_SAMPLE_ID(LPDatabase.FIELDS_NAMES_SAMPLE_ID, LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_CUSTODIAN(FIELDS_NAMES_CUSTODIAN, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_CUSTODIAN_CANDIDATE(FIELDS_NAMES_CUSTODIAN_CANDIDATE, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_STARTED_ON("coc_started_on", LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_CONFIRMED_ON(FIELDS_NAMES_COC_CONFIRMED_ON, LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_CUSTODIAN_NOTES("coc_custodian_notes", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_NEW_CUSTODIAN_NOTES("coc_new_custodian_notes", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_SAMPLE_PICTURE("sample_picture", "json")
        ;
        private SampleCoc(String dbObjName, String dbObjType){
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
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = SampleCoc.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (SampleCoc obj: SampleCoc.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
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
    public enum UserAnalysisMethod{

        /**
         *
         */
        FLD_USER_ANALYSIS_METHOD_ID("user_analysis_method_id", "integer NOT NULL DEFAULT nextval('#SCHEMA.#TBL_user_analysis_method_id_seq'::regclass)")
        ,        
        TBL("user_analysis_method", LPDatabase.createSequence(FLD_USER_ANALYSIS_METHOD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_USER_ANALYSIS_METHOD_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_USER_ANALYSIS_METHOD_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */


        /**
         *
         */
        FLD_USER_ID(FIELDS_NAMES_USER_ID, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_ANALYSIS(FIELDS_NAMES_ANALYSIS, LPDatabase.string())
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
        FLD_ASSIGNED_ON(FIELDS_NAMES_ASSIGNED_ON, LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_ASSIGNED_BY("assigned_by", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_MANDATORY_LEVEL(FIELDS_NAMES_MANDATORY_LEVEL, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_STARTED("started", LPDatabase.booleanFld())
        ,

        /**
         *
         */
        FLD_COMPLETED("completed", LPDatabase.booleanFld())
        ,

        /**
         *
         */
        FLD_EXPIRATION_DATE(FIELDS_NAMES_EXPIRATION_DATE, LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_SOP_NAME(FIELDS_NAMES_SOP_NAME, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_USER_NAME(FIELDS_NAMES_USER_NAME, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_LIGHT(FIELDS_NAMES_LIGHT, LPDatabase.stringNotNull())
        ;
        private UserAnalysisMethod(String dbObjName, String dbObjType){
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
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = UserAnalysisMethod.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (UserAnalysisMethod obj: UserAnalysisMethod.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
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
    private static final java.lang.String FIELDS_NAMES_ASSIGNED_ON = "assigned_on";
    private static final java.lang.String FIELDS_NAMES_MANDATORY_LEVEL = "mandatory_level";
    private static final java.lang.String FIELDS_NAMES_EXPIRATION_DATE = "expiration_date";
    private static final java.lang.String FIELDS_NAMES_SOP_NAME = "sop_name";

    /**
     *
     */
    public enum UserSop{

        /**
         *
         */
        FLD_USER_SOP_ID("user_sop_id", "integer NOT NULL DEFAULT nextval('#SCHEMA.#TBL_user_sop_id_seq'::regclass)")
        ,
        TBL("user_sop", LPDatabase.createSequence(FLD_USER_SOP_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_USER_SOP_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_USER_SOP_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */

        /**
         *
         */
        FLD_USER_ID(FIELDS_NAMES_USER_ID, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_SOP_ID("sop_id", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_SOP_LIST_ID("sop_list_id", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_ASSIGNED_ON(FIELDS_NAMES_ASSIGNED_ON, LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_ASSIGNED_BY("assigned_by", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_STATUS(FIELDS_NAMES_STATUS, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_MANDATORY_LEVEL(FIELDS_NAMES_MANDATORY_LEVEL, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_READ_STARTED("read_started", LPDatabase.booleanFld())
        ,

        /**
         *
         */
        FLD_READ_COMPLETED("read_completed", LPDatabase.booleanFld())
        ,

        /**
         *
         */
        FLD_UNDERSTOOD("understood", LPDatabase.booleanFld())
        ,

        /**
         *
         */
        FLD_EXPIRATION_DATE(FIELDS_NAMES_EXPIRATION_DATE, LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_SOP_NAME(FIELDS_NAMES_SOP_NAME, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_USER_NAME(FIELDS_NAMES_USER_NAME, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_LIGHT(FIELDS_NAMES_LIGHT, LPDatabase.stringNotNull())
        ;
        private UserSop(String dbObjName, String dbObjType){
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
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = UserSop.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (UserSop obj: UserSop.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
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
            for (UserSop obj: UserSop.values()){
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
    public enum ViewSampleCocNames{

        /**
         *
         */
        TBL("sample_coc_names", createView() +
                " SELECT smp_coc.sample_id, smp_coc.custodian, smp_coc.custodian_candidate, smp_coc.coc_started_on, smp_coc.coc_confirmed_on, smp_coc.coc_custodian_notes, "
                + "          smp_coc.coc_new_custodian_notes, smp_coc.sample_picture, smp_coc.id, smp_coc.status, usr_custodian.user_name AS custodian_name," +
                    "         usr_candidate.user_name AS candidate_name " +
                    "   FROM #SCHEMA.sample_coc smp_coc," +
                                "    #SCHEMA_APP.users usr_custodian," +
                                "    #SCHEMA_APP.users usr_candidate" +
                    "  WHERE smp_coc.custodian::text = usr_custodian.person_name::text AND smp_coc.custodian_candidate::text = usr_candidate.person_name::text; "+
                    "ALTER TABLE  #SCHEMA.#TBL  OWNER TO #OWNER;" +
                    "GRANT ALL ON TABLE  #SCHEMA.#TBL TO #OWNER;")
        ,

        /**
         *
         */
        FLD_SAMPLE_ID(LPDatabase.FIELDS_NAMES_SAMPLE_ID, ""),

        /**
         *
         */
        FLD_CUSTODIAN(FIELDS_NAMES_CUSTODIAN, ""),

        /**
         *
         */
        FLD_CUSTODIAN_CANDIDATE(FIELDS_NAMES_CUSTODIAN_CANDIDATE, "")
        ,

        /**
         *
         */
        FLD_COC_STARTED_ON("coc_started_on", ""),

        /**
         *
         */
        FLD_COC_CONFIRMED_ON(FIELDS_NAMES_COC_CONFIRMED_ON, ""),

        /**
         *
         */
        FLD_COC_CUSTODIAN_NOTES("coc_custodian_notes", "")
        ,

        /**
         *
         */
        FLD_NEW_CUSTODIAN_NOTES("coc_new_custodian_notes",""),

        /**
         *
         */
        FLD_SAMPLE_PICTURE("sample_picture", ""),

        /**
         *
         */
        FLD_ID("id", "")
        ,

        /**
         *
         */
        FLD_STATUS(FIELDS_NAMES_STATUS, ""),

        /**
         *
         */
        FLD_CUSTODIAN_NAME("custodian_name", ""),

        /**
         *
         */
        FLD_CANDIDATE_NAME("candidate_name", "")
        ;
        private ViewSampleCocNames(String dbObjName, String dbObjType){
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
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ViewSampleCocNames.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_APP", LPPlatform.SCHEMA_APP);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ViewSampleCocNames obj: ViewSampleCocNames.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_APP", LPPlatform.SCHEMA_APP);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
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
    public enum ViewUserAndMetaDataSopView{

        /**
         *
         */
        TBL("user_and_meta_data_sop_vw",  LPDatabase.createView() +
                " SELECT '#SCHEMA_CONFIG'::text AS procedure, usr.user_sop_id, usr.user_id, usr.sop_id, usr.sop_list_id, usr.assigned_on, usr.assigned_by, usr.status, usr.mandatory_level," +
                "            usr.read_started, usr.read_completed, usr.understood, usr.expiration_date, usr.sop_name, usr.user_name, usr.light, metadata.brief_summary, metadata.file_link, metadata.author " +
                "   FROM #SCHEMA.user_sop usr," +
                "    #SCHEMA_CONFIG.sop_meta_data metadata" +
                "  WHERE usr.sop_name::text = metadata.sop_name::text; "+
                    "ALTER TABLE  #SCHEMA.#TBL  OWNER TO #OWNER;" +
                    "GRANT ALL ON TABLE  #SCHEMA.#TBL TO #OWNER;")
        ,

        /**
         *
         */
        FLD_PROCEDURE("procedure", ""),

        /**
         *
         */
        FLD_USER_SOP_ID("user_sop_id", ""),

        /**
         *
         */
        FLD_USER_ID(FIELDS_NAMES_USER_ID, "")
        ,

        /**
         *
         */
        FLD_SOP_LIST_ID("sop_list_id", ""),

        /**
         *
         */
        FLD_ASSIGNED_ON(FIELDS_NAMES_ASSIGNED_ON, ""),

        /**
         *
         */
        FLD_STATUS(FIELDS_NAMES_STATUS, ""),

        /**
         *
         */
        FLD_MANDATORY_LEVEL(FIELDS_NAMES_MANDATORY_LEVEL, "")
        ,

        /**
         *
         */
        FLD_READ_STARTED("read_started",""),

        /**
         *
         */
        FLD_READ_COMPLETED("read_completed", ""),

        /**
         *
         */
        FLD_UNDERSTOOD("understood", "")
        ,

        /**
         *
         */
        FLD_EXPIRATION_DATE(FIELDS_NAMES_EXPIRATION_DATE, ""),

        /**
         *
         */
        FLD_SOP_NAME(FIELDS_NAMES_SOP_NAME, ""),

        /**
         *
         */
        FLD_USER_NAME(FIELDS_NAMES_USER_NAME, ""),

        /**
         *
         */
        FLD_LIGHT(FIELDS_NAMES_LIGHT, ""),

        /**
         *
         */
        FLD_BRIEF_SUMMARY("brief_summary", "")
        ,

        /**
         *
         */
        FLD_FILE_LINK("file_link", ""),

        /**
         *
         */
        FLD_AUTHOR("author", "")
        ;
        private ViewUserAndMetaDataSopView(String dbObjName, String dbObjType){
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
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ViewUserAndMetaDataSopView.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_CONFIG", LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ViewUserAndMetaDataSopView obj: ViewUserAndMetaDataSopView.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
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
    public enum ViewSampleAnalysisResultWithSpecLimits{

        /**
         *
         */
        TBL("sample_analysis_result_with_spec_limits",  LPDatabase.createView() +
                " SELECT #FLDS from #SCHEMA.sample_analysis_result sar " +
                "   INNER JOIN \"em-demo-a-data\".sample_analysis sa on sa.test_id = sar.test_id "+
                "   INNER JOIN \"em-demo-a-data\".sample s on s.sample_id = sar.sample_id "+
                "    left outer join #SCHEMA_CONFIG.spec_limits spcLim on sar.limit_id=spcLim.limit_id " +
                "    left outer join #SCHEMA_PROCEDURE.program_corrective_action pca on pca.result_id=rsl.result_id " +
                "    left outer join #SCHEMA_PROCEDURE.invest_objects io on io.object_id=rsl.result_id and io.object_type='sample_analysis_result' ;" +
                        
                "ALTER VIEW  #SCHEMA.#TBL  OWNER TO #OWNER;")
        ,

        /**
         *
         */
        FLD_RESULT_ID("result_id", "sar.result_id")
        ,

        /**
         *
         */
        FLD_TEST_ID(FIELDS_NAMES_TEST_ID, "sar.test_id")
        ,

        /**
         *
         */
        FLD_SAMPLE_ID(LPDatabase.FIELDS_NAMES_SAMPLE_ID, "sar.sample_id")
        ,

        /**
         *
         */
        FLD_STATUS(FIELDS_NAMES_STATUS, "sar.status")
        ,

        /**
         *
         */
        FLD_STATUS_PREVIOUS(FIELDS_NAMES_STATUS_PREVIOUS, "sar.status_previous")
        ,

        /**
         *
         */
        FLD_ANALYSIS(FIELDS_NAMES_ANALYSIS, "sar.analysis")
        ,

        /**
         *
         */
        FLD_METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, "sar.method_name")
        ,

        /**
         *
         */
        FLD_METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, "sar.method_version")
        ,

        /**
         *
         */
        FLD_REPLICA(FIELDS_NAMES_REPLICA, "sar.replica")
        ,

        /**
         *
         */
        FLD_PARAM_NAME("param_name", "sar.param_name")
        ,

        /**
         *
         */
        FLD_PARAM_TYPE("param_type", "sar.param_type")
        ,

        /**
         *
         */
        FLD_MANDATORY("mandatory", "sar.mandatory")
        ,

        /**
         *
         */
        FLD_REQUIRES_LIMIT("requires_limit", "sar.requires_limit")
        ,

        /**
         *
         */
        FLD_RAW_VALUE("raw_value", "sar.raw_value"),
        FLD_RAW_VALUE_NUM("raw_value_num", "TO_NUMBER(sar.raw_value, '9999')"),         

        /**
         *
         */
        FLD_PRETTY_VALUE("pretty_value", "sar.pretty_value")
        ,

        /**
         *
         */
        FLD_ENTERED_ON("entered_on", "sar.entered_on")
        ,

        /**
         *
         */
        FLD_ENTERED_BY("entered_by", "sar.entered_by")
        ,

        /**
         *
         */
        FLD_REENTERED("reentered", "sar.reentered")
        ,

        /**
         *
         */
        FLD_SPEC_EVAL(FIELDS_NAMES_SPEC_EVAL, "sar.spec_eval")
        ,

        /**
         *
         */
        FLD_SPEC_EVAL_DETAIL("spec_eval_detail", "sar.spec_eval_detail")
        ,        

        /**
         *
         */
        FLD_UOM("uom", "sar.uom")        
        ,        

        /**
         *
         */
        FLD_UOM_CONVERSION_MODE("uom_conversion_mode", "sar.uom_conversion_mode")        
        ,

        /**
         *
         */
        FLD_ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, "sar.aliquot_id")
        ,

        /**
         *
         */
        FLD_SUBALIQUOT_ID(FIELDS_NAMES_SUBALIQUOT_ID, "sar.subaliquot_id")
        ,        

        /**
         *
         */
        FLD_SAMPLE_CONFIG_CODE("sample_config_code", "s.config_code"),
        FLD_SAMPLE_STATUS("sample_status", "s.status"),
        FLD_CURRENT_STAGE("current_stage", "s.current_stage"),
        FLD_PROGRAM_NAME("program_name", "s.program_name"),
        FLD_SAMPLING_DATE("sampling_date", "s.sampling_date"),
        FLD_SHIFT("shift", "s.shift"),
        FLD_AREA("area", "s.area"),
        FLD_LOCATION_NAME("location_name", "s.location_name"),
        FLD_PRODUCTION_LOT("production_lot", "s.production_lot"),
        FLD_PROGRAM_DAY_ID("program_day_id", "s.program_day_id"),
        FLD_PROGRAM_DAY_DATE("program_day_date", "s.program_day_date"),
        FLD_TESTING_GROUP("testing_group", "sa.testing_group"),
        FLD_LOGGED_ON("logged_on", "s.logged_on"),
        FLD_LIMIT_ID("limit_id", "spcLim.limit_id"),
        FLD_SAMPLER("sampler", "s.sampler"),
        FLD_SAMPLER_AREA("sampler_area", "s.sampler_area"),
        /**
         *
         */
        FLD_SPEC_CODE("spec_code", "spcLim.code")
        ,

        /**
         *
         */
        FLD_SPEC_CONFIG_VERSION("spec_config_version", "spcLim.config_version")
        ,

        /**
         *
         */
        FLD_SPEC_VARIATION_NAME("spec_variation_name", "spcLim.variation_name")
        ,            

        /**
         *
         */
        FLD_ANALYSIS_SPEC_LIMITS("analysis_spec_limits", "spcLim.analysis")            
        ,

        /**
         *
         */
        FLD_METHOD_NAME_SPEC_LIMITS("method_name_spec_limits", "spcLim.method_name")
        ,

        /**
         *
         */
        FLD_METHOD_VERSION_SPEC_LIMITS("method_version_spec_limits", "spcLim.method_version")
        ,

        /**
         *
         */
        FLD_PARAMETER("parameter", "spcLim.parameter")
        ,

        /**
         *
         */
        FLD_RULE_TYPE("rule_type", "spcLim.rule_type")
        ,

        /**
         *
         */
        FLD_RULE_VARIABLES("rule_variables", "spcLim.rule_variables")
        ,

        /**
         *
         */
        FLD_UOM_SPEC_LIMITS("uom_spec_limits", "spcLim.uom")
        ,        

        /**
         *
         */
        FLD_UOM_CONVERSION_MODE_SPEC_LIMITS("uom_conversion_mode_spec_limits", "spcLim.uom_conversion_mode") ,
        FLD_MIN_VAL_ALLOWED("min_val_allowed", "spcLim.min_val_allowed"),
        FLD_MAX_VAL_ALLOWED("max_val_allowed", "spcLim.max_val_allowed"),
        FLD_MIN_VAL_ALLOWED_IS_STRICT("min_allowed_strict", "spcLim.min_allowed_strict"),
        FLD_MAX_VAL_ALLOWED_IS_STRICT("max_allowed_strict", "spcLim.max_allowed_strict"),        
        FLD_MIN_VAL_FOR_UNDETERMINED("min_undetermined", "spcLim.min_undetermined"),
        FLD_MAX_VAL_FOR_UNDETERMINED("max_undetermined", "spcLim.max_undetermined"),
        FLD_MIN_VAL_UNDETERMINED_IS_STRICT("min_undet_strict", "spcLim.min_undet_strict"),
        FLD_MAX_VAL_UNDETERMINED_IS_STRICT("max_undet_strict", "spcLim.max_undet_strict"),
        FLD_HAS_PREINVEST("has_pre_invest", "CASE WHEN pca.id IS NULL THEN 'NO' ELSE 'YES' END"),
        FLD_PREINVEST_ID("pre_invest_id", "pca.id"),
        FLD_HAS_INVEST("has_invest", "CASE WHEN io.id IS NULL THEN 'NO' ELSE 'YES' END"),
        FLD_INVEST_ID("invest_id", "io.invest_id"),
        FLD_INVEST_OBJECT_ID("invest_object_id", "io.id"),
        ;
        private ViewSampleAnalysisResultWithSpecLimits(String dbObjName, String dbObjType){
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
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ViewSampleAnalysisResultWithSpecLimits.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_CONFIG", LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ViewSampleAnalysisResultWithSpecLimits obj: ViewSampleAnalysisResultWithSpecLimits.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
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
            for (ViewSampleAnalysisResultWithSpecLimits obj: ViewSampleAnalysisResultWithSpecLimits.values()){
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
    

    public enum SavedQueries{

        /**
         *
         */
        FLD_ID("id", "integer NOT NULL DEFAULT nextval('#SCHEMA.#TBL_id_seq'::regclass)")
        ,        
        TBL("saved_queries", LPDatabase.createSequence(FLD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */


        /**
         *
         */
        FLD_NAME("name", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_OWNER("owner", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_PRIVATE("private", LPDatabase.booleanFld())
        ,

        /**
         *
         */
        FLD_READABLE_BY("readable_by", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_DEFINITION("definition", LPDatabase.string())
        ;
        private SavedQueries(String dbObjName, String dbObjType){
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
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = SavedQueries.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (SavedQueries obj: SavedQueries.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
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
            for (SavedQueries obj: SavedQueries.values()){
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
