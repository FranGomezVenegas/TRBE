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
import databases.TblsProcedure;
import static trazit.enums.deployrepository.DeployTables.createTableScript;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class TblsInspLotRMProcedure {
    public static final String getTableCreationScriptFromDataProcedureTableInspLotRM(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "PROGRAM_CORRECTIVE_ACTIONS": return TblsProcedure.ProgramCorrectiveAction.createTableScript(schemaNamePrefix, fields);
            case "INVESTIGATION": return createTableScript(TblsProcedure.TablesProcedure.INVESTIGATION, schemaNamePrefix);
            case "INVEST_OBJECTS": return createTableScript(TblsProcedure.TablesProcedure.INVEST_OBJECTS, schemaNamePrefix);
            case "SAMPLE_STAGE_TIMING_CAPTURE": return zSampleStageTimingCapture.createTableScript(schemaNamePrefix, fields);
            default: return "TABLE "+tableName+" NOT IN ENVMONIT_TBLSDATAAUDITENVMONIT"+LPPlatform.LAB_FALSE;
        }        
    }    
    public enum zSampleStageTimingCapture{

        /**
         *
         */
        FLD_ID("id", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_id_seq'::regclass)"),

        /**
         *
         */
        TBL("sample_stage_timing_capture", LPDatabase.createSequence(FLD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";"),

        /**
         *
         */
        FLD_SAMPLE_ID("sample_id", LPDatabase.integer()),

        /**
         *
         */
        FLD_STAGE_CURRENT("current_stage", LPDatabase.stringNotNull()),

        /**
         *
         */
        FLD_STAGE_PREVIOUS("stage_previous", LPDatabase.string()),

        /**
         *
         */
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, dateTimeWithDefaultNow()),

        /**
         *
         */
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string()),

        /**
         *
         */
        FLD_STARTED_ON("started_on", dateTime()),

        /**
         *
         */
        FLD_ENDED_ON("ended_on", dateTime()),
        ;
        private zSampleStageTimingCapture(String dbObjName, String dbObjType){
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
            String[] tblObj = zSampleStageTimingCapture.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (zSampleStageTimingCapture obj: zSampleStageTimingCapture.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, TABLETAG, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }    

        /**
         *
         * @return get all Table Fields
         */
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (zSampleStageTimingCapture obj: zSampleStageTimingCapture.values()){
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
