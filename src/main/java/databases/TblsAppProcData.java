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
public class TblsAppProcData {

    public static final String getTableCreationScriptFromDataTable(String tableName, String[] fields){
        switch (tableName.toUpperCase()){
            case "INSTRUMENT_EVENT": return InstrumentEvent.createTableScript(fields);
            default: return "TABLE "+tableName+" NOT IN TBLDATA "+LPPlatform.LAB_FALSE;
        }        
    }
    /**
     *
     */
    public enum Instruments{
        TBL("instruments",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_NAME) ) " +
                LPDatabase.POSTGRESQL_OIDS+"  TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;"),        
        FLD_NAME("name", LPDatabase.stringNotNull()),
        FLD_DESCRIPTION("description", LPDatabase.string()),
        FLD_CREATED_BY("created_by", LPDatabase.string()),
        FLD_CREATED_ON("created_on", LPDatabase.dateTime()),
        FLD_FAMILY("family", LPDatabase.string()),
        FLD_DECOMMISSIONED("decommissioned", LPDatabase.booleanFld()),
        FLD_DECOMMISSIONED_BY("decommissioned_by", LPDatabase.string()),
        FLD_DECOMMISSIONED_ON("decommissioned_on", LPDatabase.dateTime()),
        FLD_UNDECOMMISSIONED_BY("undecommissioned_by", LPDatabase.string()),
        FLD_UNDECOMMISSIONED_ON("undecommissioned_on", LPDatabase.dateTime()),
        FLD_ON_LINE("on_line", LPDatabase.booleanFld()),
        FLD_IS_LOCKED("is_locked", LPDatabase.booleanFld()),
        FLD_LOCKED_REASON("locked_reason", LPDatabase.string()),
        FLD_LAST_CALIBRATION("last_calibration",LPDatabase.dateTime()),
        FLD_NEXT_CALIBRATION("next_calibration",LPDatabase.dateTime()),
        FLD_LAST_PM("last_prev_maint",LPDatabase.dateTime()),
        FLD_NEXT_PM("next_prev_maint",LPDatabase.dateTime()),
        FLD_LAST_VERIF("last_verification",LPDatabase.dateTime()),
        ;
        private Instruments(String dbObjName, String dbObjType){
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
            String[] tblObj = Instruments.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(GlobalVariables.Schemas.APP_PROC_DATA.getName(), ""));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (Instruments obj: Instruments.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(GlobalVariables.Schemas.APP_PROC_DATA.getName(), ""));
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
            for (Instruments obj: Instruments.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }                   
    }        
    
    public enum InstrumentEvent{
        FLD_ID("id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_id_seq'::regclass)")        ,        
        TBL("instrument_event", LPDatabase.createSequence(FLD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_SAMPLE_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_SAMPLE_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")        ,
        FLD_INSTRUMENT("instrument", LPDatabase.string()),
        FLD_EVENT_TYPE("event_type", LPDatabase.string()), 
        FLD_CREATED_ON("created_on", LPDatabase.dateTime()),
        FLD_CREATED_BY("created_by", LPDatabase.string()), 
        FLD_COMPLETED_ON("completed_on", LPDatabase.dateTime()),
        FLD_COMPLETED_BY("completed_by", LPDatabase.string()), 
        FLD_DECISION("decision", LPDatabase.string()), 
        FLD_ATTACHMENT("attachment", LPDatabase.string()),         
        ;
        private InstrumentEvent(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }
        public String getName(){return this.dbObjName;}
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix - Procedure Instance where it applies
         * @param fields
         * @return
         */
        public static String createTableScript(String[] fields){
            return createTableScriptPostgres(fields);
        }
        private static String createTableScriptPostgres(String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = InstrumentEvent.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(GlobalVariables.Schemas.APP_PROC_DATA.getName(), ""));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (InstrumentEvent obj: InstrumentEvent.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(GlobalVariables.Schemas.APP_PROC_DATA.getName(), ""));
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
            for (InstrumentEvent obj: InstrumentEvent.values()){
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

    public enum InstrEventVariableValues{
        FLD_ID("id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_id_seq'::regclass)")        ,        
        TBL("instr_event_variable_values", LPDatabase.createSequence(FLD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_SAMPLE_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_SAMPLE_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")        ,
        FLD_INSTRUMENT("instrument", LPDatabase.string()),
        FLD_EVENT_ID("event_id", LPDatabase.string()), 
        FLD_CREATED_ON("created_on", LPDatabase.dateTime()),
        FLD_CREATED_BY("created_by", LPDatabase.string()), 
        FLD_DESCRIPTION("description", LPDatabase.string()),
        FLD_VARIABLE_SET("variable_set", LPDatabase.stringNotNull()),
        FLD_PARAM_NAME("param_name", LPDatabase.stringNotNull()),
        FLD_VALUE("value", LPDatabase.string()),
        FLD_ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld()),
        FLD_PARAM_TYPE("param_type", LPDatabase.string()),
        FLD_REQUIRED("required", LPDatabase.string()),
        FLD_ALLOWED_VALUES("allowed_values", LPDatabase.string()),
        FLD_OWNER_ID("owner_id", LPDatabase.stringNotNull()),
        FLD_ENTERED_ON("entered_on", LPDatabase.dateTime()),
        FLD_ENTERED_BY("entered_by", LPDatabase.string()), 
        FLD_REENTERED("reentered", LPDatabase.booleanFld(false)),
        
        ;
        private InstrEventVariableValues(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }
        public String getName(){return this.dbObjName;}
        private String[] getDbFieldDefinitionPostgres(){return new String[]{this.dbObjName, this.dbObjTypePostgres};}

        /**
         *
         * @param schemaNamePrefix - Procedure Instance where it applies
         * @param fields
         * @return
         */
        public static String createTableScript(String[] fields){
            return createTableScriptPostgres(fields);
        }
        private static String createTableScriptPostgres(String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = InstrEventVariableValues.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(GlobalVariables.Schemas.APP_PROC_DATA.getName(), ""));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (InstrEventVariableValues obj: InstrEventVariableValues.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(GlobalVariables.Schemas.APP_PROC_DATA.getName(), ""));
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
            for (InstrEventVariableValues obj: InstrEventVariableValues.values()){
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
