/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import databases.DbObjects;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
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
public class TblsEnvMonitConfig {

    /**
     *
     */
    public enum Program{

        /**
         *
         */
        TBL("program",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_PROGRAM_CONFIG_ID, FLD_PROGRAM_VERSION) )" +
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
        FLD_PROGRAM_VERSION("program_version", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_NAME("name", LPDatabase.stringNotNull(100))
        ,

        /**
         *
         */
        FLD_DESCRIPTION(LPDatabase.FIELDS_NAMES_DESCRIPTION, LPDatabase.stringNotNull(200))
        ,

        /**
         *
         */
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.stringNotNull(200))
        ,

        /**
         *
         */
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime())
        //, FLD_SENT_FOR_APPROVAL("sent_for_approval", LPDatabase.Boolean())
        // ...
        ,

        /**
         *
         */
        FLD_AREAS("areas", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_SPEC("spec", LPDatabase.string())
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
         * @param schemaNamePrefix procedure prefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder();
            String[] tblObj = Program.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder();
            for (Program obj: Program.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                    if (fieldsScript.length()>0)fieldsScript.append(", ");
                    StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                    currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
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
        FLD_PROGRAM_LOC_CONFIG_ID("program_loc_config_id", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_program_loc_config_id_seq'::regclass)"),
        TBL("program", LPDatabase.createSequence(FLD_PROGRAM_LOC_CONFIG_ID.getName())  
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_PROGRAM_LOC_CONFIG_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_PROGRAM_LOC_CONFIG_ID) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        /**
         *
         */
        ,

        /**
         *
         */
        FLD_PROGRAM_ID(FIELDS_NAMES_PROGRAM_ID, LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_NAME("name",LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_LOCATION_NAME("location_name",LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_SPEC("spec", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_VARIATION_NAME("variation_name",LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_ANALYSIS_VARIATION("analysis_variation",LPDatabase.stringNotNull())
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
         * @param schemaNamePrefix procedure prefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder();
            String[] tblObj = Program.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder();
            for (Program obj: Program.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
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
    private static final String FIELDS_NAMES_PROGRAM_ID = "program_id";

    /**
     *
     */
    public enum ProgramCalendar{

        /**
         *
         */
        FLD_CALENDAR_ID("calendar_id", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_calendar_id_seq'::regclass)")
        ,
        TBL("program_calendar", LPDatabase.createSequence(FLD_CALENDAR_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_CALENDAR_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_CALENDAR_ID) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */

        /**
         *
         */
        FLD_PROGRAM_ID(FIELDS_NAMES_PROGRAM_ID,LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_SCHEDULE_SIZE_UNIT("schedule_size_unit",LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_SCHEDULE_SIZE("schedule_size", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_START_DATE("start_date", LPDatabase.date())
        ,

        /**
         *
         */
        FLD_END_DATE("end_date", LPDatabase.date())
        ,                

        /**
         *
         */
        FLD_DAY_OF_WEEK("day_of_week",LPDatabase.stringNotNull())                
        ;        
        private ProgramCalendar(String dbObjName, String dbObjType){
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
            StringBuilder tblCreateScript=new StringBuilder();
            String[] tblObj = ProgramCalendar.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder();
            for (ProgramCalendar obj: ProgramCalendar.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
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
    public enum ProgramCalendarRecursiveEntries{

        /**
         *
         */
        FLD_ID("id", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_id_seq'::regclass)")
        ,
        TBL("program_calendar_recursive_entry", LPDatabase.createSequence(FLD_ID.getName())
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
        FLD_PROGRAM_ID(FIELDS_NAMES_PROGRAM_ID,LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_RULE("rule",LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_START_DATE("start_date", LPDatabase.date())
        ,

        /**
         *
         */
        FLD_END_DATE("end_date", LPDatabase.date())
        ,

        /**
         *
         */
        FLD_IS_HOLIDAYS("is_holidays", LPDatabase.booleanFld(false))
        ;        
        private ProgramCalendarRecursiveEntries(String dbObjName, String dbObjType){
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
            StringBuilder tblCreateScript=new StringBuilder();
            String[] tblObj = ProgramCalendarRecursiveEntries.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder();
            for (ProgramCalendarRecursiveEntries obj: ProgramCalendarRecursiveEntries.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
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
        FLD_PROGRAM_ID(FIELDS_NAMES_PROGRAM_ID,LPDatabase.stringNotNull())
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
            StringBuilder tblCreateScript=new StringBuilder();
            String[] tblObj = ProgramCalendarDate.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder();
            for (ProgramCalendarDate obj: ProgramCalendarDate.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
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
    public enum MicroOrganism{

        /**
         *
         */
        TBL("microorganism",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_NAME("name",LPDatabase.stringNotNull())
        ;        
        private MicroOrganism(String dbObjName, String dbObjType){
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
            StringBuilder tblCreateScript=new StringBuilder();
            String[] tblObj = MicroOrganism.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder();
            for (MicroOrganism obj: MicroOrganism.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
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
    public enum InstrIncubator{

        /**
         *
         */
        TBL("instrument_incubator",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_NAME("name",  LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_DESCRIPTION(LPDatabase.FIELDS_NAMES_DESCRIPTION, LPDatabase.string(200))
        ,

        /**
         *
         */
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY,  LPDatabase.string(200))
        ,

        /**
         *
         */
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, "date NOT NULL")
        ,

        /**
         *
         */
        FLD_ACTIVE("active", LPDatabase.booleanNotNull(Boolean.TRUE))
        ;        
        private InstrIncubator(String dbObjName, String dbObjType){
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
            StringBuilder tblCreateScript=new StringBuilder();
            String[] tblObj = InstrIncubator.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder();
            for (InstrIncubator obj: InstrIncubator.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
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
            for (InstrIncubator obj: InstrIncubator.values()){
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
    public enum IncubBatch{

        /**
         *
         */
        TBL("incub_batch",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_INCUB_BATCH_CONFIG_ID, #FLD_INCUB_BATCH_VERSION) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_INCUB_BATCH_CONFIG_ID("incub_batch_config_id", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_INCUB_BATCH_VERSION("incub_batch_version", LPDatabase.integerNotNull())
        ,

        /**
         *
         */
        FLD_NAME("name",  LPDatabase.stringNotNull(100))
        ,

        /**
         *
         */
        FLD_DESCRIPTION(LPDatabase.FIELDS_NAMES_DESCRIPTION,  LPDatabase.string(200))
        ,

        /**
         *
         */
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY,  LPDatabase.string(200))
        ,

        /**
         *
         */
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.date())
        ,

        /**
         *
         */
        FLD_TYPE("type",  LPDatabase.stringNotNull(100))
        ,

        /**
         *
         */
        FLD_ACTIVE("active", LPDatabase.booleanFld(false)),
        FLD_STRUCT_NUM_ROWS("struct_num_rows", LPDatabase.integer()),
        FLD_STRUCT_NUM_COLS("struct_num_cols", LPDatabase.integer()),
        FLD_STRUCT_TOTAL_POSITIONS("struct_total_positions", LPDatabase.integer()),
        FLD_STRUCT_ROWS_NAME("struct_rows_name", "character varying[] COLLATE pg_catalog.\"default\""),
        FLD_STRUCT_COLS_NAME("struct_cols_name", "character varying[] COLLATE pg_catalog.\"default\""),        

        //, FLD_SENT_FOR_APPROVAL("sent_for_approval", LPDatabase.Boolean())
        // ...
        
        ;        
        private IncubBatch(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return the entry name
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
            StringBuilder tblCreateScript=new StringBuilder();
            String[] tblObj = IncubBatch.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder();
            for (IncubBatch obj: IncubBatch.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, LPPlatform.SCHEMA_CONFIG));
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
