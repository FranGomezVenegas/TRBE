/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.clinicalstudies.definition;

import databases.DbObjects;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
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
public class TblsGenomaDataAudit {
    
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA_AUDIT.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesGenomaDataAudit implements EnumIntTables{        
        PROJECT(null, "project", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Project.values(), Project.AUDIT_ID.getName(), new String[]{Project.AUDIT_ID.getName()}, null, "Project audit table"),
        STUDY(null, "study", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Study.values(), Study.AUDIT_ID.getName(), new String[]{Study.AUDIT_ID.getName()}, null, "Study audit table"),
        ;
        private TablesGenomaDataAudit(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    public enum Project implements EnumIntTableFields{
        AUDIT_ID("audit_id", LPDatabase.integerNotNull(), null, null, null, null),
        TABLE_NAME("table_name", LPDatabase.string(), null, null, null, null), 
        TRANSACTION_ID("transaction_id", LPDatabase.integer(), null, null, null, null), 
        TABLE_ID("table_id", LPDatabase.string(), null, null, null, null), 
        DATE("date", LPDatabase.dateTime(), null, null, null, null), 
        PERSON("person", LPDatabase.string(), null, null, null, null), 
        ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null), 
        FIELDS_UPDATED("fields_updated", LPDatabase.string(), null, null, null, null), 
        PROJECT("project", LPDatabase.string(), null, null, null, null), 
        STUDY(FIELDS_NAMES_STUDY, LPDatabase.string(), null, null, null, null), 
        USER_ROLE("user_role", LPDatabase.string(), null, null, null, null), 
        PROCEDURE("procedure", LPDatabase.string(), null, null, null, null), 
        PROCEDURE_VERSION("procedure_version", LPDatabase.integer(), null, null, null, null), 
        PROCEDURE_HASH_CODE("procedure_hash_code", LPDatabase.stringNotNull(), null, null, null, null),
        APP_SESSION_ID("app_session_id", LPDatabase.integer(), null, null, null, null), 
        PICTURE_BEFORE("picture_before", LPDatabase.json(), null, null, null, null), 
        PICTURE_AFTER("picture_after", LPDatabase.json(), null, null, null, null), 
        PARENT_AUDIT_ID("parent_audit_id", LPDatabase.integer(), null, null, null, null),       
        ;
        private Project(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum Study implements EnumIntTableFields{
        AUDIT_ID("audit_id", LPDatabase.integerNotNull(), null, null, null, null),
        TABLE_NAME("table_name", LPDatabase.string(), null, null, null, null), 
        TRANSACTION_ID("transaction_id", LPDatabase.integer(), null, null, null, null), 
        TABLE_ID("table_id", LPDatabase.string(), null, null, null, null), 
        DATE("date", LPDatabase.dateTime(), null, null, null, null), 
        PERSON("person", LPDatabase.string(), null, null, null, null), 
        ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null), 
        FIELDS_UPDATED("fields_updated", LPDatabase.string(), null, null, null, null), 
        PROJECT("project", LPDatabase.string(), null, null, null, null), 
        STUDY(FIELDS_NAMES_STUDY, LPDatabase.string(), null, null, null, null), 
        USER_ROLE("user_role", LPDatabase.string(), null, null, null, null), 
        PROCEDURE("procedure", LPDatabase.string(), null, null, null, null), 
        PROCEDURE_VERSION("procedure_version", LPDatabase.integer(), null, null, null, null), 
        PROCEDURE_HASH_CODE("procedure_hash_code", LPDatabase.stringNotNull(), null, null, null, null),
        APP_SESSION_ID("app_session_id", LPDatabase.integer(), null, null, null, null), 
        PICTURE_BEFORE("picture_before", LPDatabase.json(), null, null, null, null), 
        PICTURE_AFTER("picture_after", LPDatabase.json(), null, null, null, null), 
        PARENT_AUDIT_ID("parent_audit_id", LPDatabase.integer(), null, null, null, null),       
        ;
        private Study(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    
    public static final String FIELDS_NAMES_STUDY="study";
    
    public enum xProject{
        /**
         *
         */
        AUDIT_ID("audit_id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_audit_id_seq'::regclass)")
        ,
        TBL("project", LPDatabase.createSequence(AUDIT_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#AUDIT_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#AUDIT_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;")
        ,

        /**
         *
         */

        /**
         *
         */
        TABLE_NAME("table_name", " character varying COLLATE pg_catalog.\"default\"")
        ,

        /**
         *
         */
        TRANSACTION_ID("transaction_id", LPDatabase.integer())
        ,

        /**
         *
         */
        TABLE_ID("table_id", LPDatabase.string())
        ,

        /**
         *
         */
        DATE("date", LPDatabase.dateTime())
        ,

        /**
         *
         */
        PERSON("person", LPDatabase.string())
        ,

        /**
         *
         */
        ACTION_NAME("action_name", LPDatabase.string())
        ,

        /**
         *
         */
        FIELDS_UPDATED("fields_updated", LPDatabase.string())
        ,

        /**
         *
         */
        PROJECT("project", LPDatabase.string())
        ,

        /**
         *
         */
        STUDY(FIELDS_NAMES_STUDY, LPDatabase.string())
        ,

        /**
         *
         */
        USER_ROLE("user_role", LPDatabase.string())
        ,

        /**
         *
         */
        PROCEDURE("procedure", LPDatabase.string())
        ,

        /**
         *
         */
        PROCEDURE_VERSION("procedure_version", LPDatabase.integer())
        ,

        /**
         *
         */
        APP_SESSION_ID("app_session_id", LPDatabase.integer())
        ,

        /**
         *
         */
        PICTURE_BEFORE("picture_before", "json")
        ,

        /**
         *
         */
        PICTURE_AFTER("picture_after", "json")
        ,

        /**
         *
         */
        PARENT_AUDIT_ID("parent_audit_id", LPDatabase.integer())        
        ;
        private xProject(String dbObjName, String dbObjType){
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
            String[] tblObj = xProject.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA_AUDIT.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (xProject obj: xProject.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (Boolean.FALSE.equals("TBL".equalsIgnoreCase(objName))) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
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

    public enum xStudy{
        /**
         *
         */
        AUDIT_ID("audit_id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_audit_id_seq'::regclass)")
        ,
        TBL(FIELDS_NAMES_STUDY, LPDatabase.createSequence(AUDIT_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#AUDIT_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#AUDIT_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;")
        ,

        /**
         *
         */

        /**
         *
         */
        TABLE_NAME("table_name", " character varying COLLATE pg_catalog.\"default\"")
        ,

        /**
         *
         */
        TRANSACTION_ID("transaction_id", LPDatabase.integer())
        ,

        /**
         *
         */
        TABLE_ID("table_id", LPDatabase.string())
        ,

        /**
         *
         */
        DATE("date", LPDatabase.dateTime())
        ,

        /**
         *
         */
        PERSON("person", LPDatabase.string())
        ,

        /**
         *
         */
        ACTION_NAME("action_name", LPDatabase.string())
        ,

        /**
         *
         */
        FIELDS_UPDATED("fields_updated", LPDatabase.string())
        ,

        /**
         *
         */
        PROJECT("project", LPDatabase.string())
        ,

        /**
         *
         */
        STUDY(FIELDS_NAMES_STUDY, LPDatabase.string())
        ,

        /**
         *
         */
        USER_ROLE("user_role", LPDatabase.string())
        ,

        /**
         *
         */
        PROCEDURE("procedure", LPDatabase.string())
        ,

        /**
         *
         */
        PROCEDURE_VERSION("procedure_version", LPDatabase.integer())
        ,

        /**
         *
         */
        APP_SESSION_ID("app_session_id", LPDatabase.integer())
        ,

        /**
         *
         */
        PICTURE_BEFORE("picture_before", "json")
        ,

        /**
         *
         */
        PICTURE_AFTER("picture_after", "json")
        ,

        /**
         *
         */
        PARENT_AUDIT_ID("parent_audit_id", LPDatabase.integer())        
        ;
        private xStudy(String dbObjName, String dbObjType){
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
            String[] tblObj = xStudy.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.DATA_AUDIT.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (xStudy obj: xStudy.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (Boolean.FALSE.equals("TBL".equalsIgnoreCase(objName))) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
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
