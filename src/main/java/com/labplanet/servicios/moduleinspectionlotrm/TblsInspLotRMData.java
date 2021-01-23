/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleinspectionlotrm;

import databases.DbObjects;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPPlatform;
import static databases.TblsCnfg.SCHEMATAG;
import static databases.TblsCnfg.TABLETAG;
import static databases.TblsCnfg.OWNERTAG;
import static databases.TblsCnfg.TABLESPACETAG;
import static databases.TblsCnfg.FIELDSTAG;
import databases.TblsData;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class TblsInspLotRMData {
    public static final String getTableCreationScriptFromDataTableInspLotRM(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "LOT": return Lot.createTableScript(schemaNamePrefix, fields);
            case "LOT_DECISION": return LotDecision.createTableScript(schemaNamePrefix, fields);
            case "LOT_CERTIFICATE": return LotCertificate.createTableScript(schemaNamePrefix, fields);
            case "LOT_CERTIFICATE_TRACK": return LotCertificateTrack.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE": return Sample.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW": return ViewSampleAnalysisResultWithSpecLimits.createTableScript(schemaNamePrefix, fields);
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
        FLD_LOT_CONFIG_NAME("lot_config_name", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_LOT_CONFIG_VERSION("lot_config_version", LPDatabase.integerNotNull())        ,
        FLD_MATERIAL_NAME("material_name", LPDatabase.string()),        
        FLD_SPEC_CODE("spec_code", LPDatabase.string()),        
        FLD_SPEC_CODE_VERSION("spec_code_version", LPDatabase.integer()),
        FLD_SAMPLING_PLAN("sampling_plan", LPDatabase.string()),        
//        FLD_CONFIG_CODE("config_code", LPDatabase.string()),        
//        FLD_CONFIG_CODE_VERSION("config_code_version", LPDatabase.integer()),        
        FLD_DESCRIPTION_EN("description_en", LPDatabase.string()),        
        FLD_DESCRIPTION_ES("description_es", LPDatabase.string()),        
        FLD_QUANTITY("quantity", LPDatabase.integer()),        
        FLD_QUANTITY_UOM("quantity_uom", LPDatabase.string()),        
        FLD_NUM_CONTAINERS("num_containers", LPDatabase.integer()),        
        FLD_VENDOR("vendor", LPDatabase.string()),        
        FLD_VENDOR_TRUST_LEVEL("vendor_trust_level", LPDatabase.string()),        
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
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
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
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
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

    /**
     *
     */
    public enum LotDecision{

        /**
         *
         */
        TBL("lot_decision",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_LOT_NAME) )" +
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
        FLD_DECISION("decision",  LPDatabase.string(200)),
        FLD_DECISION_TAKEN_BY("decision_taken_by",  LPDatabase.string()),
        FLD_DECISION_TAKEN_ON("decision_taken_on",  LPDatabase.dateTime()),
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
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
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
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
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

    public enum LotCertificate{

        /**
         *
         */
        FLD_CERTIFICATE_ID("id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_#FLD_CERTIFICATE_ID_seq'::regclass)")
        ,        
        TBL("lot_certificate", LPDatabase.createSequence(FLD_CERTIFICATE_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_CERTIFICATE_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_CERTIFICATE_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
        FLD_LOT_NAME("lot_name", LPDatabase.string())        ,
        FLD_CERTIFICATE_VERSION("certificate_version", LPDatabase.integer())        ,
        FLD_STATUS("status",LPDatabase.stringNotNull())        ,
        FLD_STATUS_PREVIOUS("status_previous",LPDatabase.string())        ,
        FLD_CREATED_ON("created_on", LPDatabase.date())        ,
        FLD_CREATED_BY("created_by", LPDatabase.string())        ,       
        FLD_CERTIFICATE_FORMAT("certificate_format", LPDatabase.string())        ,
        ;
        private LotCertificate(String dbObjName, String dbObjType){
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
            String[] tblObj = LotCertificate.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (LotCertificate obj: LotCertificate.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
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

    public enum LotCertificateTrack{
        FLD_ID("id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_id_seq'::regclass)")
        ,        
        TBL("lot_certificate_track", LPDatabase.createSequence(FLD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
        FLD_LOT_NAME("lot_name", LPDatabase.string())        ,
        FLD_CONFIG_NAME("config_name", LPDatabase.integer()),        
        FLD_EVENT("event",LPDatabase.stringNotNull())        ,
        FLD_CREATED_ON("created_on", LPDatabase.date())        ,
        FLD_CREATED_BY("created_by", LPDatabase.string())        ,       
        ;
        private LotCertificateTrack(String dbObjName, String dbObjType){
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
            String[] tblObj = LotCertificateTrack.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (LotCertificateTrack obj: LotCertificateTrack.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
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
        FLD_CONFIG_CODE("sample_config_code", LPDatabase.string())
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
        FLD_PREVIOUS_STAGE("previous_stage",LPDatabase.string()),
        FLD_READY_FOR_REVISION("ready_for_revision", LPDatabase.booleanFld()),        
        
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
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
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
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
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
    public enum ViewSampleAnalysisResultWithSpecLimits{

        /**
         *
         */
        TBL("sample_analysis_result_with_spec_limits",  LPDatabase.createView() +
                " SELECT #FLDS from #SCHEMA.sample_analysis_result sar " +
                "   INNER JOIN #SCHEMA.sample_analysis sa on sa.test_id = sar.test_id "+
                "   INNER JOIN #SCHEMA.sample s on s.sample_id = sar.sample_id "+
                "   INNER JOIN #SCHEMA.lot l on l.name = s.lot_name "+
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
        FLD_TEST_ID(TblsData.FIELDS_NAMES_TEST_ID, "sar.test_id")
        ,
        FLD_SAMPLE_ID(LPDatabase.FIELDS_NAMES_SAMPLE_ID, "sar.sample_id")        ,
        FLD_LOT_NAME("lot_name", "l.lot_name")        ,
        FLD_STATUS(TblsData.FIELDS_NAMES_STATUS, "sar.status")
        ,

        /**
         *
         */
        FLD_STATUS_PREVIOUS(TblsData.FIELDS_NAMES_STATUS_PREVIOUS, "sar.status_previous")
        ,

        /**
         *
         */
        FLD_ANALYSIS(TblsData.FIELDS_NAMES_ANALYSIS, "sar.analysis")
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
        FLD_REPLICA(TblsData.FIELDS_NAMES_REPLICA, "sar.replica")
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
        FLD_RAW_VALUE_NUM("raw_value_num", "case when isnumeric(sar.raw_value) then to_number(sar.raw_value::text, '9999'::text) else null end AS raw_value_num"),         

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
        FLD_SPEC_EVAL(TblsData.FIELDS_NAMES_SPEC_EVAL, "sar.spec_eval")
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
        FLD_ALIQUOT_ID(TblsData.FIELDS_NAMES_ALIQUOT_ID, "sar.aliquot_id")
        ,

        /**
         *
         */
        FLD_SUBALIQUOT_ID(TblsData.FIELDS_NAMES_SUBALIQUOT_ID, "sar.subaliquot_id")
        ,        

        /**
         *
         */
        FLD_SAMPLE_CONFIG_CODE("sample_config_code", "s.config_code"),
        FLD_SAMPLE_STATUS("sample_status", "s.status"),
        FLD_CURRENT_STAGE("current_stage", "s.current_stage"),
        FLD_TESTING_GROUP("testing_group", "sa.testing_group"),
        FLD_TEST_STATUS("test_status", "sa.status"),
        FLD_LOGGED_ON("logged_on", "s.logged_on"),
        FLD_LIMIT_ID("limit_id", "spcLim.limit_id"),
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
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_CONFIG", LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
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


}
