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
import databases.TblsDataAudit;
import static trazit.enums.deployrepository.DeployTables.createTableScript;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class TblsInspLotRMDataAudit {
/*    public static final String getTableCreationScriptFromDataAuditTableInspLotRM(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "CERTIF_USER_ANALYSIS_METHOD": return createTableScript(TblsDataAudit.TablesDataAudit.CERTIF_USER_ANALYSIS_METHOD, schemaNamePrefix);
            case "LOT": return Lot.createTableScript(schemaNamePrefix, fields);
            default: return "TABLE "+tableName+" NOT IN INSPLOT_RM_TBLSDATAAUDITENVMONIT"+LPPlatform.LAB_FALSE;
        }        
    }    
*/
    /**
     *
     */
    public enum Lot{

        /**
         *
         */
        FLD_AUDIT_ID("audit_id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_audit_id_seq'::regclass)")
        ,        TBL("lot", LPDatabase.createSequence(FLD_AUDIT_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_AUDIT_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_#FLD_AUDIT_ID_pkey PRIMARY KEY (#FLD_AUDIT_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;")
        ,

        /**
         *
         */


        /**
         *
         */
        FLD_TABLE_NAME("table_name", " character varying COLLATE pg_catalog.\"default\"")
        ,

        /**
         *
         */
        FLD_TRANSACTION_ID("transaction_id", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_TABLE_ID("table_id", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_DATE("date", LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_PERSON("person", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_ACTION_NAME("action_name", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_FIELDS_UPDATED("fields_updated", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_LOT_NAME("lot_name", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_USER_ROLE("user_role", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_PROCEDURE("procedure", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_PROCEDURE_VERSION("procedure_version", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_APP_SESSION_ID("app_session_id", LPDatabase.integer())
        ,
        PROCEDURE_HASH_CODE("procedure_hash_code", LPDatabase.stringNotNull()), //, null, null, null, null),

        /**
         *
         */
        FLD_PICTURE_BEFORE("picture_before", "json")
        ,

        /**
         *
         */
        FLD_PICTURE_AFTER("picture_after", "json")
        ,

        /**
         *
         */
        FLD_REVIEWED("reviewed", LPDatabase.booleanFld(false))
        ,

        /**
         *
         */
        FLD_REVIEWED_BY("reviewed_by", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_REVIEWED_ON("reviewed_on", LPDatabase.dateTime())
        ,

        /**
         *
         */
        FLD_REVISION_NOTE("revision_note", LPDatabase.string())
        ,        

        /**
         *
         */
        FLD_PARENT_AUDIT_ID("parent_audit_id", LPDatabase.integer()),
        FLD_REASON("reason", LPDatabase.string()),
        
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
            String[] tblObj = Lot.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA_AUDIT.getName()));
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
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA_AUDIT.getName()));
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
