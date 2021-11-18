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
    public enum InstrumentEvent{

        /**
         *
         */
        FLD_ID("id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_id_seq'::regclass)")
        ,        
        TBL("instrument_event", LPDatabase.createSequence(FLD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_SAMPLE_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_SAMPLE_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
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

        /**
         *
         * @return
         */
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
    
}