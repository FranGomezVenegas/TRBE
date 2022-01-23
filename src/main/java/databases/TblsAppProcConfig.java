/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import static databases.TblsCnfg.FIELDSTAG;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPPlatform;
import static databases.TblsCnfg.OWNERTAG;
import static databases.TblsCnfg.SCHEMATAG;
import static databases.TblsCnfg.TABLESPACETAG;
import static databases.TblsCnfg.TABLETAG;
import static lbplanet.utilities.LPDatabase.dateTime;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class TblsAppProcConfig {
    public enum InstrumentsFamily{

        /**
         *
         */
        TBL("instruments_family",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_NAME) ) " +
                LPDatabase.POSTGRESQL_OIDS+"  TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;"),        
        FLD_NAME("name", LPDatabase.stringNotNull()),
        FLD_DESCRIPTION("description", LPDatabase.string()),
        FLD_CALIB_REQUIRED("calibration_required", LPDatabase.booleanFld()),
        FLD_CALIB_INTERVAL("calibration_interval", LPDatabase.string()),
        FLD_CALIB_TURN_OFF_WHEN_STARTED("calibration_turn_off_when_started", LPDatabase.booleanFld()),
        FLD_CALIB_TURN_ON_WHEN_COMPLETED("calibration_turn_on_when_completed", LPDatabase.booleanFld()),
        FLD_CALIB_VARIABLES_SET("calib_variables_set", LPDatabase.string()),
        FLD_PM_REQUIRED("pm_required", LPDatabase.booleanFld()),
        FLD_PM_INTERVAL("pm_interval", LPDatabase.string()),
        FLD_PM_TURN_OFF_WHEN_STARTED("pm_turn_off_when_started", LPDatabase.booleanFld()),
        FLD_PM_TURN_ON_WHEN_COMPLETED("pm_turn_on_when_completed", LPDatabase.booleanFld()),
        FLD_PM_VARIABLES_SET("pm_variables_set", LPDatabase.string()),
        FLD_VERIF_SAME_DAY_REQUIRED("verif_same_day_required", LPDatabase.booleanFld()),
        FLD_VERIF_SAME_DAY_VARIABLES_SET("verif_same_day_variables_set", LPDatabase.string()),
        ;
        private InstrumentsFamily(String dbObjName, String dbObjType){
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
            String[] tblObj = InstrumentsFamily.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(GlobalVariables.Schemas.APP_PROC_CONFIG.getName(), ""));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (InstrumentsFamily obj: InstrumentsFamily.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(currField[1]);
                        currFieldDefBuilder=LPPlatform.replaceStringBuilderByStringAllReferences(currFieldDefBuilder, SCHEMATAG, LPPlatform.buildSchemaName(GlobalVariables.Schemas.APP_PROC_CONFIG.getName(), ""));
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
            for (InstrumentsFamily obj: InstrumentsFamily.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }            
        
    }        

    public enum Variables{
        TBL("variables",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;"),
        FLD_PARAM_NAME("param_name",  LPDatabase.stringNotNull(100)),
        FLD_DESCRIPTION("description", LPDatabase.string()),
        FLD_ACTIVE("active", LPDatabase.booleanFld()),
        FLD_PARAM_TYPE("param_type", LPDatabase.string()),
        FLD_REQUIRED("required", LPDatabase.string()),
        FLD_ALLOWED_VALUES("allowed_values", LPDatabase.string()),
        FLD_CREATED_ON("created_on", dateTime()),
        FLD_CREATED_BY("created_by", LPDatabase.string()),
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
         * @param procInstanceName Procedure Prefix
         * @param fields fiels to apply
         * @return the Create-Table script for this given table for all or the fields array fields
         */
        public static String createTableScript(String procInstanceName, String[] fields){
            return createTableScriptPostgres(procInstanceName, fields);
        }
        private static String createTableScriptPostgres(String procInstanceName, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = Variables.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (Variables obj: Variables.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
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
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }          
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }

    public enum VariablesSet{
        TBL("variables_set",  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+" TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + "    OWNER to #OWNER;"),
        FLD_NAME("name",  LPDatabase.stringNotNull(100)),
        FLD_DESCRIPTION("description", LPDatabase.string()),
        FLD_ACTIVE("active", LPDatabase.booleanFld()),
        FLD_VARIABLES_LIST("variables_list", LPDatabase.string()),
        FLD_CREATED_ON("created_on", dateTime()),
        FLD_CREATED_BY("created_by", LPDatabase.string()),
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
         * @param procInstanceName procedure prefix
         * @param fields fields , ALL when this is null
         * @return One Create-Table script for this given table, for this given procedure and for ALL or the given fields.
         */
        public static String createTableScript(String procInstanceName, String[] fields){
            return createTableScriptPostgres(procInstanceName, fields);
        }
        private static String createTableScriptPostgres(String procInstanceName, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = VariablesSet.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (VariablesSet obj: VariablesSet.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
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
