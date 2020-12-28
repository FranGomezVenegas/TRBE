/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleinspectionlotrm;

import databases.DbObjects;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import static lbplanet.utilities.LPDatabase.dateTime;
import static lbplanet.utilities.LPDatabase.dateTimeWithDefaultNow;
import lbplanet.utilities.LPPlatform;
import static databases.TblsCnfg.SCHEMATAG;
import static databases.TblsCnfg.TABLETAG;
import static databases.TblsCnfg.OWNERTAG;
import static databases.TblsCnfg.TABLESPACETAG;
import static databases.TblsCnfg.FIELDSTAG;

/**
 *
 * @author Administrator
 */
public class TblsInspLotRMData {
    public static final String getTableCreationScriptFromDataTableInspLotRM(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "LOT": return Lot.createTableScript(schemaNamePrefix, fields);
            case "LOT_DECISION": return LotDecision.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE": return Sample.createTableScript(schemaNamePrefix, fields);
            default: return "TABLE "+tableName+" NOT IN INSPLOT_RM_TBLSDATAENVMONIT"+LPPlatform.LAB_FALSE;
        }        
    }    
    /**
     *
     */
    public enum Lot{

        /**
         *
         */
        TBL("lot",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_NAME("name",  LPDatabase.stringNotNull(100))
        ,

        /**
         *
         */
        FLD_PROGRAM_CONFIG_ID("lot_config_name", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_PROGRAM_CONFIG_VERSION("lot_config_version", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_SPEC_CODE("spec_code", LPDatabase.string()),        

        /**
         *
         */
        FLD_SPEC_CODE_VERSION("spec_code_version", LPDatabase.integer()),

        /**
         *
         */
//        FLD_CONFIG_CODE("config_code", LPDatabase.string()),        
//        FLD_CONFIG_CODE_VERSION("config_code_version", LPDatabase.integer()),        
        FLD_DESCRIPTION_EN("description_en", LPDatabase.string()),        
        FLD_DESCRIPTION_ES("description_es", LPDatabase.string()),        
        FLD_QUANTITY("quantity", LPDatabase.integer()),        
        FLD_QUANTITY_UOM("quantity_uom", LPDatabase.string()),        
        FLD_NUM_CONTAINERS("num_containers", LPDatabase.integer()),        
        FLD_VENDOR("vendor", LPDatabase.string()),        
        FLD_VENDOR_TRUST_LEVEL("vendor_trust_level", LPDatabase.string()),        
        FLD_SAMPLING_PLAN("sampling_plan", LPDatabase.string()),        
        FLD_ANALYSIS_STATUS("analysis_status", LPDatabase.string()),        
        FLD_CREATED_ON("created_on", LPDatabase.dateTime()),
        FLD_CREATED_BY("created_by", LPDatabase.string()),
                
        FLD_ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld()),
        FLD_CUSTODIAN("custodian", LPDatabase.string()),
        // ...
        ;
        
        private Lot(String dbObjName, String dbObjType){
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
        private String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        /**
         *
         * @param schemaNamePrefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = Lot.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (Lot obj: Lot.values()){
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
    public enum LotDecision{

        /**
         *
         */
        TBL("lot_decision",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_LOT_NAME, #FLD_LOCATION_NAME, #FLD_AREA) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_LOT_NAME(FIELDS_NAMES_LOT_NAME, LPDatabase.stringNotNull(100))
        ,

        /**
         *
         */
        FLD_LOCATION_NAME(FIELDS_NAMES_LOCATION_NAME,  LPDatabase.string(200)),
        FLD_AREA("area",  LPDatabase.string()),
        FLD_ORDER_NUMBER("order_number",  LPDatabase.integer()),
        FLD_DESCRIPTION_EN("description_en",  LPDatabase.string()),
        FLD_DESCRIPTION_ES("description_es",  LPDatabase.string()),
        /**
         *
         */
        FLD_REQUIRES_PERSON_ANA("requires_person_ana", LPDatabase.booleanFld()),
        /**
         *
         */
        FLD_PERSON_ANA_DEFINITION("person_ana_definition",LPDatabase.string()),
        FLD_SPEC_CODE("spec_code",  LPDatabase.string()),
        FLD_SPEC_CODE_VERSION("spec_code_version",  LPDatabase.integer()),
        FLD_SPEC_VARIATION_NAME("spec_variation_name",  LPDatabase.string()),
        FLD_SPEC_ANALYSIS_VARIATION("spec_analysis_variation",  LPDatabase.string()),
        FLD_TESTING_GROUP("testing_group",  LPDatabase.string()),

        FLD_MAP_ICON("map_icon",  LPDatabase.string()),
        FLD_MAP_ICON_H("map_icon_h",  LPDatabase.string()),
        FLD_MAP_ICON_W("map_icon_w",  LPDatabase.string()),
        FLD_MAP_ICON_TOP("map_icon_top",  LPDatabase.string()),
        FLD_MAP_ICON_LEFT("map_icon_left",  LPDatabase.string()),
        
//        , FLD_PROGRAM_CONFIG_VERSION("lot_config_version", LPDatabase.String())
        // ...
        ;        
        private LotDecision(String dbObjName, String dbObjType){
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
        private String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        /**
         *
         * @param schemaNamePrefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = LotDecision.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_DATA));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (LotDecision obj: LotDecision.values()){
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
            for (LotDecision obj: LotDecision.values()){
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

    
    
    private static final String FIELDS_NAMES_LOCATION_NAME = "location_name";
    private static final String FIELDS_NAMES_LOT_NAME = "lot_name";

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
        FLD_CONFIG_CODE("sample_config_code", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_sample_id_seq'::regclass)")
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
        FLD_STATUS("status",LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_STATUS_PREVIOUS("status_previous",LPDatabase.string())
        ,

        /**
         *
         */
        FLD_LOGGED_ON("logged_on", LPDatabase.date())
        ,

        /**
         *
         */
        FLD_LOGGED_BY("logged_by", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_RECEIVED_ON("received_on", LPDatabase.date())
        ,

        /**
         *
         */
        FLD_RECEIVED_BY("received_by", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_VOLUME(LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.real())
        ,

        /**
         *
         */
        FLD_VOLUME_UOM(LPDatabase.FIELDS_NAMES_VOLUME_UOM,LPDatabase.string())
        ,

        /**
         *
         */
        FLD_ALIQUOTED("aliquoted", LPDatabase.booleanFld(false))
        ,

        /**
         *
         */
        FLD_ALIQUOT_STATUS("aliq_status",LPDatabase.string())
        ,

        /**
         *
         */
        FLD_VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.real())
        ,

        /**
         *
         */
        FLD_VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom",LPDatabase.string())
        ,

        /**
         *
         */
        FLD_SPEC_CODE("spec_code",LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_SPEC_CODE_VERSION("spec_code_version", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_SPEC_VARIATION_NAME("spec_variation_name",LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_SPEC_EVAL("spec_eval",  LPDatabase.string(2))
        ,

        /**
         *
         */
        FLD_CUSTODIAN("custodian",  LPDatabase.string(2))
        ,

        /**
         *
         */
        FLD_CUSTODIAN_CANDIDATE("custodian_candidate",  LPDatabase.string(2))
        ,

        FLD_COC_REQUESTED_ON("coc_requested_on", LPDatabase.date())
        ,
        FLD_COC_CONFIRMED_ON("coc_confirmed_on", LPDatabase.date())
        ,
        FLD_LOT_NAME(FIELDS_NAMES_LOT_NAME,  LPDatabase.stringNotNull())
        ,
        FLD_CURRENT_STAGE("current_stage",LPDatabase.string())
        ,
        FLD_PREVIOUS_STAGE("previous_stage",LPDatabase.string())
        ;
        private Sample(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return entry name
         */
        public String getName(){return this.dbObjName;}
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
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (Sample obj: Sample.values()){
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
    private static final String FIELDS_NAMES_INCUBATION2_END = "incubation2_end";
    private static final String FIELDS_NAMES_INCUBATION2_START = "incubation2_start";
    private static final String FIELDS_NAMES_INCUBATION_START = "incubation_start";
    private static final String FIELDS_NAMES_INCUBATION_END = "incubation_end";


}
