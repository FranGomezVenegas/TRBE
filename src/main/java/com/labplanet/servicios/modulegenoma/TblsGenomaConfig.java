/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulegenoma;

import databases.DbObjects;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import static lbplanet.utilities.LPDatabase.dateTime;
import lbplanet.utilities.LPPlatform;
import static databases.TblsCnfg.SCHEMATAG;
import static databases.TblsCnfg.TABLETAG;
import static databases.TblsCnfg.OWNERTAG;
import static databases.TblsCnfg.TABLESPACETAG;
import static databases.TblsCnfg.FIELDSTAG;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class TblsGenomaConfig {
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.CONFIG.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesGenomaConfig implements EnumIntTables{        
        VARIABLES(null, "variables", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Variables.values(), null, new String[]{Variables.NAME.getName()}, null, "Variables table"),
        VARIABLES_SET(null, "variables_set", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, VariablesSet.values(), null, new String[]{VariablesSet.NAME.getName()}, null, "Variables Set table"),
        ;
        private TablesGenomaConfig(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
                String seqName, String[] primaryK, Object[] foreignK, String comment){
            this.getTblBusinessRules=fldBusRules;
            this.tableName=dbTblName;
            this.tableFields=tblFlds;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.sequence=seqName;
            this.primarykey=primaryK;
            this.foreignkey=foreignK;
            this.tableComment=comment;
        }
        @Override        public String getTableName() {return this.tableName;}
        @Override        public String getTableComment() {return this.tableComment;}
        @Override        public EnumIntTableFields[] getTableFields() {return this.tableFields;}
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public String getSeqName() {return this.sequence;}
        @Override        public String[] getPrimaryKey() {return this.primarykey;}
        @Override        public Object[] getForeignKey() {return this.foreignkey;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String tableName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final String sequence;
        private final EnumIntTableFields[] tableFields;
        private final String[] primarykey;
        private final Object[] foreignkey;
        private final String tableComment;
    }

    
    public enum Variables implements EnumIntTableFields{
        NAME("name",  LPDatabase.stringNotNull(100), null, null, null, null), 
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null), 
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null), 
        PARAM_TYPE("param_type", LPDatabase.string(), null, null, null, null), 
        REQUIRED("required", LPDatabase.string(), null, null, null, null), 
        ALLOWED_VALUES("allowed_values", LPDatabase.string(), null, null, null, null), 
        CREATED_ON("created_on", dateTime(), null, null, null, null), 
        CREATED_BY("created_by", LPDatabase.string(), null, null, null, null), 
        STARTED_ON("started_on", dateTime(), null, null, null, null), 
        ENDED_ON("ended_on", dateTime(), null, null, null, null), 
        ;
        private Variables(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }

    public enum VariablesSet implements EnumIntTableFields{
        NAME("name",  LPDatabase.stringNotNull(100), null, null, null, null), 
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null), 
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null), 
        VARIABLES_LIST("variables_list", LPDatabase.string(), null, null, null, null), 
        CREATED_ON("created_on", dateTime(), null, null, null, null), 
        CREATED_BY("created_by", LPDatabase.string(), null, null, null, null), 
        STARTED_ON("started_on", dateTime(), null, null, null, null), 
        ENDED_ON("ended_on", dateTime(), null, null, null, null), 
        ;
        private VariablesSet(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    
    public enum xVariables{
        TBL("variables",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;"),
        NAME("name",  LPDatabase.stringNotNull(100)),
        DESCRIPTION("description", LPDatabase.string()),
        ACTIVE("active", LPDatabase.booleanFld()),
        TYPE("type", LPDatabase.string()),
        REQUIRED("required", LPDatabase.string()),
        ALLOWED_VALUES("allowed_values", LPDatabase.string()),
        CREATED_ON("created_on", dateTime()),
        CREATED_BY("created_by", LPDatabase.string()),
        STARTED_ON("started_on", dateTime()),
        ENDED_ON("ended_on", dateTime()),        
        ;
        
        private xVariables(String dbObjName, String dbObjType){
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
         * @param procInstanceName Procedure Prefix
         * @param fields fiels to apply
         * @return the Create-Table script for this given table for all or the fields array fields
         */
        public static String createTableScript(String procInstanceName, String[] fields){
            return createTableScriptPostgres(procInstanceName, fields);
        }
        private static String createTableScriptPostgres(String procInstanceName, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = xVariables.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (xVariables obj: xVariables.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (Boolean.FALSE.equals("TBL".equalsIgnoreCase(objName))) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()));
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
            for (Variables obj: Variables.values()){
                String objName = obj.name();
                if (Boolean.FALSE.equals("TBL".equalsIgnoreCase(objName))){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }          
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }

    public enum xVariablesSet{
        TBL("variables_set",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;"),
        NAME("name",  LPDatabase.stringNotNull(100)),
        DESCRIPTION("description", LPDatabase.string()),
        ACTIVE("active", LPDatabase.booleanFld()),
        VARIABLES_LIST("variables_list", LPDatabase.string()),
        CREATED_ON("created_on", dateTime()),
        CREATED_BY("created_by", LPDatabase.string()),
        STARTED_ON("started_on", dateTime()),
        ENDED_ON("ended_on", dateTime()),        
        ;
        
        private xVariablesSet(String dbObjName, String dbObjType){
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
         * @param procInstanceName procedure prefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String procInstanceName, String[] fields){
            return createTableScriptPostgres(procInstanceName, fields);
        }
        private static String createTableScriptPostgres(String procInstanceName, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = xVariablesSet.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (xVariablesSet obj: xVariablesSet.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (Boolean.FALSE.equals("TBL".equalsIgnoreCase(objName))) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()));
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
            for (VariablesSet obj: VariablesSet.values()){
                String objName = obj.name();
                if (Boolean.FALSE.equals("TBL".equalsIgnoreCase(objName))){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }  
        
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }
    
}
