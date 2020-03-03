/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulegenoma;

import databases.DbObjects;
import static databases.TblsCnfg.fieldsTag;
import static databases.TblsCnfg.ownerTag;
import static databases.TblsCnfg.schemaTag;
import static databases.TblsCnfg.tableTag;
import static databases.TblsCnfg.tablespaceTag;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import static lbplanet.utilities.LPDatabase.dateTime;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author User
 */
public class TblsGenomaConfig {
    public enum Variables{
        TBL("variables",  LPDatabase.CreateTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;"),
        FLD_NAME("name",  LPDatabase.StringNotNull(100)),
        FLD_DESCRIPTION("description", LPDatabase.String()),
        FLD_ACTIVE("active", LPDatabase.Boolean()),
        FLD_TYPE("type", LPDatabase.String()),
        FLD_REQUIRED("required", LPDatabase.String()),
        FLD_ALLOWED_VALUES("allowed_values", LPDatabase.String()),
        FLD_CREATED_ON("created_on", dateTime()),
        FLD_CREATED_BY("created_by", LPDatabase.String()),
        FLD_STARTED_ON("started_on", dateTime()),
        FLD_ENDED_ON("ended_on", dateTime()),        
        ;
        
        private Variables(String dbObjName, String dbObjType){
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
         * @param schemaPrefix Procedure Prefix
         * @param fields fiels to apply
         * @return the Create-Table script for this given table for all or the fields array fields
         */
        public static String createTableScript(String schemaPrefix, String[] fields){
            return createTableScriptPostgres(schemaPrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaPrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder();
            String[] tblObj = Variables.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, schemaTag, LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, tableTag, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, ownerTag, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, tablespaceTag, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder();
            for (Variables obj: Variables.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, schemaTag, LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG));
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, tableTag, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, fieldsTag, fieldsScript.toString());
            return tblCreateScript.toString();
        }                
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }

    public enum VariablesSet{
        TBL("variables_set",  LPDatabase.CreateTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;"),
        FLD_NAME("name",  LPDatabase.StringNotNull(100)),
        FLD_DESCRIPTION("description", LPDatabase.String()),
        FLD_ACTIVE("active", LPDatabase.Boolean()),
        FLD_VARIABLES_LIST("variables_list", LPDatabase.String()),
        FLD_CREATED_ON("created_on", dateTime()),
        FLD_CREATED_BY("created_by", LPDatabase.String()),
        FLD_STARTED_ON("started_on", dateTime()),
        FLD_ENDED_ON("ended_on", dateTime()),        
        ;
        
        private VariablesSet(String dbObjName, String dbObjType){
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
         * @param schemaPrefix procedure prefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String schemaPrefix, String[] fields){
            return createTableScriptPostgres(schemaPrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaPrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder();
            String[] tblObj = VariablesSet.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, schemaTag, LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, tableTag, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, ownerTag, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, tablespaceTag, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder();
            for (VariablesSet obj: VariablesSet.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, schemaTag, LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG));
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, tableTag, tblObj[0]);                        
                        fieldsScript.append(currField[0]).append(" ").append(currFieldDefBuilder);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, fieldsTag, fieldsScript.toString());
            return tblCreateScript.toString();
        }                
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }
    
}
