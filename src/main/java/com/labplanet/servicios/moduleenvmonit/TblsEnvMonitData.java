/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

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
import databases.TblsData;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class TblsEnvMonitData {
    public static final String getTableCreationScriptFromDataTableEnvMonit(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "INCUB_BATCH": return IncubBatch.createTableScript(schemaNamePrefix, fields);
            case "INSTRUMENT_INCUBATOR_NOTEBOOK": return InstrIncubatorNoteBook.createTableScript(schemaNamePrefix, fields);
            case "PRODUCTION_LOT": return ProductionLot.createTableScript(schemaNamePrefix, fields);
            case "PROGRAM": return Program.createTableScript(schemaNamePrefix, fields);
            case "PROGRAM_CALENDAR_DATE": return ProgramCalendarDate.createTableScript(schemaNamePrefix, fields);
            case "PROGRAM_DAY": return ProgramDay.createTableScript(schemaNamePrefix, fields);
            case "PROGRAM_LOCATION": return ProgramLocation.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE": return Sample.createTableScript(schemaNamePrefix, fields);
            case "SAVED_QUERIES": return TblsData.SavedQueries.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE_MICROORGANISM": return SampleMicroorganism.createTableScript(schemaNamePrefix, fields);
            case "PR_SCHEDULED_LOCATIONS_VIEW": return ViewProgramScheduledLocations.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE_MICROORGANISM_LIST_VIEW": return ViewSampleMicroorganismList.createTableScript(schemaNamePrefix, fields);
            case "SAMPLE_REVISION_TESTING_GROUP": return TblsData.SampleRevisionTestingGroup.createTableScript(schemaNamePrefix, fields);
            default: return "TABLE "+tableName+" NOT IN ENVMONIT_TBLSDATAENVMONIT"+LPPlatform.LAB_FALSE;
        }        
    }    
    /**
     *
     */
    public enum Program{

        /**
         *
         */
        TBL("program",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_NAME) )" +
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
        FLD_PROGRAM_CONFIG_ID("program_config_id", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_PROGRAM_CONFIG_VERSION("program_config_version", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_SPEC_CODE("spec_code", LPDatabase.string()),        

        /**
         *
         */
        FLD_SPEC_CONFIG_VERSION("spec_config_version", LPDatabase.integer()),

        /**
         *
         */
        FLD_SAMPLE_CONFIG_CODE("sample_config_code", LPDatabase.string()),        
        FLD_MAP_IMAGE("map_image", LPDatabase.string()),        
        
        /**
         *
         */
        FLD_SAMPLE_CONFIG_CODE_VERSION("sample_config_code_version", LPDatabase.integer()),        
        FLD_DESCRIPTION_EN("description_en", LPDatabase.string()),        
        FLD_DESCRIPTION_ES("description_es", LPDatabase.string()),        

                
        FLD_ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld())
        // ...
        ;
        
        private Program(String dbObjName, String dbObjType){
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
            String[] tblObj = Program.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (Program obj: Program.values()){
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
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }

    /**
     *
     */
    public enum ProgramLocation{

        /**
         *
         */
        TBL("program_location",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_PROGRAM_NAME, #FLD_LOCATION_NAME, #FLD_AREA) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_PROGRAM_NAME(FIELDS_NAMES_PROGRAM_NAME, LPDatabase.stringNotNull(100))
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
        
//        , FLD_PROGRAM_CONFIG_VERSION("program_config_version", LPDatabase.String())
        // ...
        ;        
        private ProgramLocation(String dbObjName, String dbObjType){
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
            String[] tblObj = ProgramLocation.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProgramLocation obj: ProgramLocation.values()){
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
            for (ProgramLocation obj: ProgramLocation.values()){
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

    public enum ProgramCalendarDate{

        /**
         *
         */
        FLD_ID("id", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_id_seq'::regclass)")
        ,        
        TBL("program_calendar_date", LPDatabase.createSequence(FLD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_PROGRAM_ID, #FLD_CALENDAR_ID, #FLD_ID) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */


        /**
         *
         */
        FLD_CALENDAR_ID("calendar_id", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_PROGRAM_ID(TblsEnvMonitConfig.FIELDS_NAMES_PROGRAM_ID,LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_RECURSIVE_ID("recursive_id", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_IS_HOLIDAYS("is_holidays", LPDatabase.booleanFld(false))
        ,

        /**
         *
         */
        FLD_DATE("date", LPDatabase.date())
        ,

        /**
         *
         */
        FLD_CONFLICT("conflict", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_CONFLICT_DETAIL("conflict_detail", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_LOCATION_NAME("location_name", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_SPEC("spec", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_VARIATION_NAME("variation_name", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_ANALYSIS_VARIATION("analysis_variation", LPDatabase.string())
        ;        
        private ProgramCalendarDate(String dbObjName, String dbObjType){
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
         * @param schemaNamePrefix procedure prefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProgramCalendarDate.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProgramCalendarDate obj: ProgramCalendarDate.values()){
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
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }
    
    public enum ProgramDay{

        /**
         *
         */
        TBL("program_day",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_PROGRAM_CONFIG_ID, #FLD_PROGRAM_CONFIG_VERSION) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_PROGRAM_CONFIG_ID("program_config_id", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_PROGRAM_CONFIG_VERSION("program_config_version", LPDatabase.integerNotNull())
        ,
        /**
         *
         */
        FLD_DAY_ID("day_id", LPDatabase.integer())        
        ,

        /**
         *
         */
        FLD_DATE("date", LPDatabase.date())        
        // ...
        ;
        
        private ProgramDay(String dbObjName, String dbObjType){
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
            String[] tblObj = ProgramDay.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProgramDay obj: ProgramDay.values()){
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
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }
    
    private static final String FIELDS_NAMES_LOCATION_NAME = "location_name";
    private static final String FIELDS_NAMES_PROGRAM_NAME = "program_name";

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
        FLD_SPEC_ANALYSIS_VARIATION("spec_analysis_variation", LPDatabase.stringNotNull()),
        /**
         *
         */
        FLD_SPEC_EVAL("spec_eval",  LPDatabase.string())
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

        /**
         *
         */
        FLD_COC_REQUESTED_ON("coc_requested_on", LPDatabase.date())
        ,

        /**
         *
         */
        FLD_COC_CONFIRMED_ON("coc_confirmed_on", LPDatabase.date())
        ,

        /**
         *
         */
        FLD_PROGRAM_NAME(FIELDS_NAMES_PROGRAM_NAME,  LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_LOCATION_NAME(FIELDS_NAMES_LOCATION_NAME,  LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_PRODUCTION_LOT("production_lot",  LPDatabase.string()),
        /**
         *
         */
        FLD_SAMPLER_AREA("sampler_area",LPDatabase.string()),
        FLD_SAMPLER("sampler",LPDatabase.string()),
        FLD_SAMPLE_ID_RELATED("sample_id_related",LPDatabase.integer()),        
        FLD_SAMPLING_DATE("sampling_date", dateTime())
        ,

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

        FLD_INCUBATION_BATCH("incubation_batch", LPDatabase.string())
        ,
        /**
         *
         */
        FLD_INCUBATION_START(FIELDS_NAMES_INCUBATION_START, dateTime())
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
        FLD_INCUBATION_END(FIELDS_NAMES_INCUBATION_END, dateTime())
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
        FLD_INCUBATION2_BATCH("incubation2_batch", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_INCUBATION2_START(FIELDS_NAMES_INCUBATION2_START, dateTime())
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
        FLD_INCUBATION2_END(FIELDS_NAMES_INCUBATION2_END, dateTime())
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
        FLD_CURRENT_STAGE("current_stage",LPDatabase.string())
        ,

        /**
         *
         */
        FLD_PREVIOUS_STAGE("previous_stage",LPDatabase.string()),
        FLD_AREA("area",LPDatabase.string())
        
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
    private static final String FIELDS_NAMES_INCUBATION2_END = "incubation2_end";
    private static final String FIELDS_NAMES_INCUBATION2_START = "incubation2_start";
    private static final String FIELDS_NAMES_INCUBATION_START = "incubation_start";
    private static final String FIELDS_NAMES_INCUBATION_END = "incubation_end";

    /**
     *
     */
    public enum SampleMicroorganism{

        /**
         *
         */
        FLD_ID("id", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_id_seq'::regclass)")
        ,
        TBL("sample_microorganism", LPDatabase.createSequence(FLD_ID.getName())
                
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
        //, FLD_TEST_ID("test_id", LPDatabase.Integer())
        //, FLD_RESULT_ID("result_id", LPDatabase.Integer())    
        ,

        /**
         *
         */
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, dateTimeWithDefaultNow())
        ,

        /**
         *
         */
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_MICROORG_NAME("microorganism_name", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_NOTE("note", LPDatabase.string())
        ;
        private SampleMicroorganism(String dbObjName, String dbObjType){
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
            String[] tblObj = SampleMicroorganism.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (SampleMicroorganism obj: SampleMicroorganism.values()){
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
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }         
    
    /**
     *
     */
    public enum ProductionLot{

        /**
         *
         */
        TBL("production_lot",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_LOT_NAME) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_LOT_NAME("lot_name",LPDatabase.stringNotNull())
        //, FLD_TEST_ID("test_id", LPDatabase.Integer())
        //, FLD_RESULT_ID("result_id", LPDatabase.Integer())    
        ,

        /**
         *
         */
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, dateTimeWithDefaultNow())
        ,

        /**
         *
         */
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_CLOSED_ON("closed_on", dateTimeWithDefaultNow())
        ,

        /**
         *
         */
        FLD_CLOSED_BY("closed_by", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld())
        ;
        private ProductionLot(String dbObjName, String dbObjType){
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
            String[] tblObj = ProductionLot.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProductionLot obj: ProductionLot.values()){
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
            for (ProductionLot obj: ProductionLot.values()){
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

    /*CREATE OR REPLACE VIEW "em-demo-a-data".sample_microorganism_list_vw AS
select s.sample_id, s.current_stage, s.program_name, s.location_name, s.incubation_start, s.incubation_end, s.incubation2_start, s.incubation2_end, sar.raw_value,
array_to_string(array_agg(distinct sorg.microorganism_name), ', ') as microorganism_list
-- array_to_string(ARRAY(sorg.microorganism_name), ', ') as microorganism_list
from "em-demo-a-data".sample_analysis_result as sar 
 inner join "em-demo-a-data".sample as s on sar.sample_id=s.sample_id
 left outer join "em-demo-a-data".sample_microorganism as sorg on sorg.sample_id=sar.sample_id
where sar.param_name='Recuento'       
group by s.sample_id, s.current_stage, s.program_name, s.location_name, s.incubation_start, s.incubation_end, s.incubation2_start, s.incubation2_end, sar.raw_value    
*/

    /**
     *
     */

    public enum ViewSampleMicroorganismList{

        /**
         *
         */
        TBL("sample_microorganism_list_vw", " CREATE OR REPLACE VIEW #SCHEMA.#TBL AS " +
               " SELECT distinct s.sample_id, s.sample_config_code, s.status, s.sampling_date, s.current_stage, s.program_name, s.location_name, s.incubation_start, s.incubation_end, s.incubation2_start, s.incubation2_end, "
               + "sar.raw_value, sar.result_id, sar.test_id, count(distinct sorg.id) as microorganism_count,"
               + "  array_to_string(array_agg(distinct sorg.microorganism_name), ', ') as microorganism_list" 
               + "   FROM #SCHEMA.sample_analysis_result as sar " +
                  "    inner join #SCHEMA.sample as s on sar.sample_id=s.sample_id " +
                  "       left outer join #SCHEMA.sample_microorganism as sorg on sorg.sample_id=sar.sample_id " +
                   "  where sar.param_name='Recuento' "+
                   " group by s.sample_id, s.status, s.sampling_date, s.current_stage, s.program_name, s.location_name, s.incubation_start, s.incubation_end, s.incubation2_start, s.incubation2_end, sar.raw_value, sar.result_id, sar.test_id;"+
                   "ALTER TABLE  #SCHEMA.#TBL  OWNER TO #OWNER;" +
                   "GRANT ALL ON TABLE  #SCHEMA.#TBL TO #OWNER;")
       ,
        FLD_SAMPLE_ID(LPDatabase.FIELDS_NAMES_SAMPLE_ID, ""),
        FLD_SAMPLE_TEMPLATE("sample_config_code", ""),
        FLD_STATUS("status", ""),
        FLD_CURRENT_STAGE("current_stage", ""),
        FLD_SAMPLING_DATE("sampling_date", "")
       ,

        /**
         *
         */
        FLD_PROGRAM_NAME(FIELDS_NAMES_PROGRAM_NAME, ""),

        /**
         *
         */
        FLD_LOCATION_NAME(FIELDS_NAMES_LOCATION_NAME, "")
       ,

        /**
         *
         */
        FLD_INCUBATION_START(FIELDS_NAMES_INCUBATION_START, ""),

        /**
         *
         */
        FLD_INCUBATION_END(FIELDS_NAMES_INCUBATION_END, "")
       ,

        /**
         *
         */
        FLD_INCUBATION2_START(FIELDS_NAMES_INCUBATION2_START, ""),

        /**
         *
         */
        FLD_INCUBATION2_END(FIELDS_NAMES_INCUBATION2_END, "")
       ,

        /**
         *
         */
        FLD_RESULT_ID(FIELDS_NAMES_INCUBATION2_START, ""),

        /**
         *
         */
        FLD_TEST_ID(FIELDS_NAMES_INCUBATION2_END, "")
       ,

        /**
         *
         */
        FLD_RAW_VALUE("raw_value",""),

        /**
         *
         */
        FLD_MICROORGANISM_COUNT("microorganism_count", ""),

        /**
         *
         */
        FLD_MICROORGANISM_LIST("microorganism_list", "")
       ;
       private ViewSampleMicroorganismList(String dbObjName, String dbObjType){
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
         * @param schemaNamePrefix procedure prefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
           return createTableScriptPostgres(schemaNamePrefix, fields);
       }
       private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
           StringBuilder tblCreateScript=new StringBuilder(0);
           String[] tblObj = ViewSampleMicroorganismList.TBL.getDbFieldDefinitionPostgres();
           tblCreateScript.append(tblObj[1]);
           tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_APP", GlobalVariables.Schemas.APP.getName());
           tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
           tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
           tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
           tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
           StringBuilder fieldsScript=new StringBuilder(0);
           for (ViewSampleMicroorganismList obj: ViewSampleMicroorganismList.values()){
               String[] currField = obj.getDbFieldDefinitionPostgres();
               String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                       if (fieldsScript.length()>0)fieldsScript.append(", ");
                       StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                       tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_APP", GlobalVariables.Schemas.APP.getName());
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
            for (ViewSampleMicroorganismList obj: ViewSampleMicroorganismList.values()){
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
    public enum InstrIncubatorNoteBook{

        /**
         *
         */
        FLD_ID("id", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_id_seq'::regclass)")
        ,
        TBL("instrument_incubator_notebook", LPDatabase.createSequence(FLD_ID.getName())
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
        FLD_NAME("name",  LPDatabase.stringNotNull(100))
        ,

        /**
         *
         */
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY,  LPDatabase.string(200))
        ,

        /**
         *
         */
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, lbplanet.utilities.LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_EVENT_TYPE("event_type",  LPDatabase.string(200))
        ,

        /**
         *
         */
        FLD_TEMPERATURE("temperature", LPDatabase.real())
        ;        
        private InstrIncubatorNoteBook(String dbObjName, String dbObjType){
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
         * @param schemaNamePrefix procedure prefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = InstrIncubatorNoteBook.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (InstrIncubatorNoteBook obj: InstrIncubatorNoteBook.values()){
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
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }

    /**
     *
     */
    public enum IncubBatch{

        /**
         *
         */
        TBL("incub_batch",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_NAME) )" +
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
        FLD_INCUB_BATCH_CONFIG_ID("incub_batch_config_id", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_INCUB_BATCH_CONFIG_VERSION("incub_batch_config_version", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_TYPE("type", LPDatabase.string())
        ,        

        /**
         *
         */
        FLD_INCUBATION_INCUBATOR("incubation_incubator", LPDatabase.string())        
        ,

        /**
         *
         */
        FLD_INCUBATION_START(FIELDS_NAMES_INCUBATION_START, LPDatabase.dateTime())
        ,        

        /**
         *
         */
        FLD_INCUBATION_END(FIELDS_NAMES_INCUBATION_END, LPDatabase.dateTime())        
        ,

        /**
         *
         */
        FLD_ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(false))
        ,

        /**
         *
         */
        FLD_COMPLETED("completed", LPDatabase.booleanFld(false)),
        FLD_UNSTRUCT_CONTENT("unstruct_content", LPDatabase.string()),
        FLD_STRUCT_NUM_ROWS("struct_num_rows", LPDatabase.integer()),
        FLD_STRUCT_NUM_COLS("struct_num_cols", LPDatabase.integer()),
        FLD_STRUCT_TOTAL_POSITIONS("struct_total_positions", LPDatabase.integer()),
        FLD_STRUCT_TOTAL_OBJECTS("struct_total_objects", LPDatabase.integer()),
        FLD_STRUCT_CONTENT("struct_content", LPDatabase.string()),
        FLD_STRUCT_ROWS_NAME("struct_rows_name", LPDatabase.string()),
        FLD_STRUCT_COLS_NAME("struct_cols_name", LPDatabase.string()),        
        // ...
        ;
        
        private IncubBatch(String dbObjName, String dbObjType){
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
         * @param schemaNamePrefix procedure prefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix,String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix,String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = IncubBatch.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (IncubBatch obj: IncubBatch.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, GlobalVariables.Schemas.DATA.getName());
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
            for (IncubBatch obj: IncubBatch.values()){
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
    public enum ViewProgramScheduledLocations{
        /**
         *
         */
        TBL("pr_scheduled_locations",  LPDatabase.createView() +
                " select  dpr.sample_config_code, dpr.sample_config_code_version, "+
                "         cnfpcd.*, dpl.area, dpl.spec_code, dpl.spec_variation_name, dpl.spec_analysis_variation, dpl.spec_code_version, dpl.requires_person_ana, dpl.person_ana_definition "+
                "   from #SCHEMA_DATA.program_calendar_date  cnfpcd"+
                "  inner join #SCHEMA_DATA.program  dpr on dpr.name=cnfpcd.program_id "+
                "  inner join #SCHEMA_DATA.program_location dpl on dpl.program_name=cnfpcd.program_id and dpl.location_name=cnfpcd.location_name;"+
                "ALTER VIEW  #SCHEMA.#TBL  OWNER TO #OWNER;")
        ,

        /**
         *
         */
        FLD_SAMPLE_CONFIG_CODE("sample_config_code", "dpr.sample_config_code"),
        FLD_SAMPLE_CONFIG_CODE_VERSION("sample_config_code_version", "dpr.sample_config_code_version"),
        FLD_PROGRAM_NAME("program_name", "cnfpcd.program_id as program_name"),
        FLD_PROGRAM_DAY_ID("program_day_id", "cnfpcd.id as program_day_id"),
        FLD_PROGRAM_DAY_DATE("program_day_date", "cnfpcd.date as program_day_date"),
        FLD_AREA("area", "dpl.area"),
        FLD_SPEC_CODE("spec_code", "dpl.spec_code"),
        FLD_SPEC_CODE_VERSION("spec_code_version", "dpl.spec_code_version"),
        FLD_SPEC_VARIATION_NAME("spec_variation_name", "dpl.spec_variation_name"),
        FLD_SPEC_ANALYSIS_VARIATION("spec_analysis_variation", "dpl.spec_analysis_variation"),
        FLD_REQUIRES_PERSON_ANA("requires_person_ana", "dpl.requires_person_ana"),
        FLD_PERSON_ANA_DEFINITION("person_ana_definition", "dpl.person_ana_definition"),
        FLD_LOCATION_NAME("location_name", "cnfpcd.location_name"),
        FLD_ID("id", "cnfpcd.id"),
        FLD_PROGRAM_ID("program_id", "cnfpcd.program_id"),
        FLD_DATE("date", "cnfpcd.date"),
        ;
        private ViewProgramScheduledLocations(String dbObjName, String dbObjType){
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
         * @param schemaNamePrefix procedure prefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ViewProgramScheduledLocations.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_CONFIG", LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_DATA", LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ViewProgramScheduledLocations obj: ViewProgramScheduledLocations.values()){
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
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }        

}
