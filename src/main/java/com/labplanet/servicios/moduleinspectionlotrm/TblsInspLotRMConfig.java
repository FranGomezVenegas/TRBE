/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleinspectionlotrm;

import databases.DbObjects;
import static databases.Rdbms.dbTableGetFieldDefinition;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPPlatform;
import static databases.TblsCnfg.SCHEMATAG;
import static databases.TblsCnfg.TABLETAG;
import static databases.TblsCnfg.OWNERTAG;
import static databases.TblsCnfg.TABLESPACETAG;
import static databases.TblsCnfg.FIELDSTAG;
import java.util.HashMap;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class TblsInspLotRMConfig {
    public static final String getTableCreationScriptFromConfigTableInspLotRM(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "LOT": return Lot.createTableScript(schemaNamePrefix, fields);            
            case "LOT_RULES": return LotRules.createTableScript(schemaNamePrefix, fields);           
            case "LOT_DECISION_RULES": return LotDecisionRules.createTableScript(schemaNamePrefix, fields);           
            case "MATERIAL": return Material.createTableScript(schemaNamePrefix, fields);           
            case "MATERIAL_CERTIFICATE": return MaterialCertificate.createTableScript(schemaNamePrefix, fields);           
            case "MATERIAL_SAMPLING_PLAN": return MaterialSamplingPlan.createTableScript(schemaNamePrefix, fields);                       
            default: return "TABLE "+tableName+" NOT IN INSPLOT_RM_TBLSCNFGENVMONIT"+LPPlatform.LAB_FALSE;
        }        
    }
    public static final String getTableUpdateScriptFromConfigTableInspLotRM(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "LOT": return Lot.updateTableScript(schemaNamePrefix, fields);            
            default: return "TABLE "+tableName+" NOT IN INSPLOT_RM_TBLSCNFGENVMONIT"+LPPlatform.LAB_FALSE;
        }        
    }

    public enum Material{

        /**
         *
         */
        TBL("material", true,  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_NAME)) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
        FLD_NAME("name", true, LPDatabase.stringNotNull())        ,
        FLD_SPEC_CODE("spec_code", true, LPDatabase.string()),
        FLD_SPEC_CODE_VERSION("spec_code_version", true, LPDatabase.integer()),
        ;
        private Material(String dbObjName, Boolean fldMandatory, String dbObjType){
            this.dbObjName=dbObjName;
            this.fieldIsMandatory=fldMandatory;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return
         */
        public String getName(){
            return this.dbObjName;
        }
        public Boolean getFieldIsMandatory(){
            return this.fieldIsMandatory;
        }
        String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        /**
         *
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = Material.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (Material obj: Material.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && ( (obj.getFieldIsMandatory()) || (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) )){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        fieldsScript.append(currField[0]).append(" ").append(currField[1]);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }
        public static String updateTableScript(String schemaNamePrefix, String[] fields){
            return updateTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String updateTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblAlterScript=new StringBuilder(0);
            HashMap<String[], Object[][]> dbTableGetFieldDefinition = dbTableGetFieldDefinition(schemaNamePrefix, Lot.TBL.getName());

            String[] fldDefinitionColName= dbTableGetFieldDefinition.keySet().iterator().next();    
            Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
            //if ( dbTableGetFieldDefinition1.get(FldDefinitionColName).length()!=whereFieldsNameArr[iFields].length()){
            Object[] tableFldsInfoColumns = LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name"));
            if (fields==null || (fields.length==1 && fields[0].length()==0)) fields=getAllFieldNames();

            for (String curFld: fields){
                if (!LPArray.valueInArray(tableFldsInfoColumns, curFld)){
                    String[] currField = getFldDefBydbFieldName(curFld);
                    if (tblAlterScript.length()>0)tblAlterScript.append(", ");
                    tblAlterScript.append(LPDatabase.addColumn()).append(" ").append(currField[0]).append(" ").append(currField[1]);                            
                }
                
            }
/*            
            for (TblsInspLotRMConfig.Lot obj: TblsInspLotRMConfig.Lot.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( !"TBL".equalsIgnoreCase(objName)) {
                    if (!LPArray.valueInArray(tableFldsInfoColumns, currField[0])){
                        if (tblAlterScript.length()>0)tblAlterScript.append(", ");
                        tblAlterScript.append(LPDatabase.addColumn()).append(" ").append(currField[0]).append(" ").append(currField[1]);                            
                    }
                }
            }
*/            
            if (tblAlterScript.toString().length()>0)
                return LPDatabase.alterTable()+" "+LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName())+"."+Lot.TBL.getName()+" "+tblAlterScript.toString()+";";
            else
                return tblAlterScript.toString();
/*            for (String curFld: fields){
                if (!LPArray.valueInArray(tableFldsInfoColumns, curFld))
                    tblAlterScript.append(addColumn)+
            }*/
            //tblAlterScript.append(LPDatabase.alterTableAddColumn());
        }
        private final String dbObjName;             
        private final Boolean fieldIsMandatory;             
        private final String dbObjTypePostgres;     
        
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (TblsInspLotRMConfig.Lot obj: TblsInspLotRMConfig.Lot.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }  
        public static String[] getFldDefBydbFieldName(String fldName){
            String[] tableFields=new String[0];
            for (TblsInspLotRMConfig.Lot obj: TblsInspLotRMConfig.Lot.values()){
                String objName = obj.getName();
                if (fldName.equalsIgnoreCase(objName)){
                    //tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getDbFieldDefinitionPostgres());
                    return tableFields;
                }
            }           
            return tableFields;            
        }
    }    
    
    public enum MaterialSamplingPlan{

        /**
         *
         */
        TBL("material_sampling_plan", true,  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_MATERIAL, #FLD_ENTRY_NAME)) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
        FLD_MATERIAL("material", true, LPDatabase.stringNotNull())        ,
        FLD_ENTRY_NAME("entry_name", true, LPDatabase.stringNotNull())        ,
        FLD_ANALYSIS_VARIATION("analysis_variation", true, LPDatabase.string()),
        FLD_ALGORITHM("algorithm", true, LPDatabase.string()),
        FLD_FIX_SAMPLES_NUM("fix_samples_num", true, LPDatabase.integer()),
        ;
        private MaterialSamplingPlan(String dbObjName, Boolean fldMandatory, String dbObjType){
            this.dbObjName=dbObjName;
            this.fieldIsMandatory=fldMandatory;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return
         */
        public String getName(){
            return this.dbObjName;
        }
        public Boolean getFieldIsMandatory(){
            return this.fieldIsMandatory;
        }
        String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        /**
         *
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = MaterialSamplingPlan.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (MaterialSamplingPlan obj: MaterialSamplingPlan.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && ( (obj.getFieldIsMandatory()) || (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) )){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        fieldsScript.append(currField[0]).append(" ").append(currField[1]);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }
        public static String updateTableScript(String schemaNamePrefix, String[] fields){
            return updateTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String updateTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblAlterScript=new StringBuilder(0);
            HashMap<String[], Object[][]> dbTableGetFieldDefinition = dbTableGetFieldDefinition(schemaNamePrefix, Lot.TBL.getName());

            String[] fldDefinitionColName= dbTableGetFieldDefinition.keySet().iterator().next();    
            Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
            //if ( dbTableGetFieldDefinition1.get(FldDefinitionColName).length()!=whereFieldsNameArr[iFields].length()){
            Object[] tableFldsInfoColumns = LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name"));
            if (fields==null || (fields.length==1 && fields[0].length()==0)) fields=getAllFieldNames();

            for (String curFld: fields){
                if (!LPArray.valueInArray(tableFldsInfoColumns, curFld)){
                    String[] currField = getFldDefBydbFieldName(curFld);
                    if (tblAlterScript.length()>0)tblAlterScript.append(", ");
                    tblAlterScript.append(LPDatabase.addColumn()).append(" ").append(currField[0]).append(" ").append(currField[1]);                            
                }
                
            }
/*            
            for (TblsInspLotRMConfig.Lot obj: TblsInspLotRMConfig.Lot.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( !"TBL".equalsIgnoreCase(objName)) {
                    if (!LPArray.valueInArray(tableFldsInfoColumns, currField[0])){
                        if (tblAlterScript.length()>0)tblAlterScript.append(", ");
                        tblAlterScript.append(LPDatabase.addColumn()).append(" ").append(currField[0]).append(" ").append(currField[1]);                            
                    }
                }
            }
*/            
            if (tblAlterScript.toString().length()>0)
                return LPDatabase.alterTable()+" "+LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName())+"."+Lot.TBL.getName()+" "+tblAlterScript.toString()+";";
            else
                return tblAlterScript.toString();
/*            for (String curFld: fields){
                if (!LPArray.valueInArray(tableFldsInfoColumns, curFld))
                    tblAlterScript.append(addColumn)+
            }*/
            //tblAlterScript.append(LPDatabase.alterTableAddColumn());
        }
        private final String dbObjName;             
        private final Boolean fieldIsMandatory;             
        private final String dbObjTypePostgres;     
        
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (TblsInspLotRMConfig.Lot obj: TblsInspLotRMConfig.Lot.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }  
        public static String[] getFldDefBydbFieldName(String fldName){
            String[] tableFields=new String[0];
            for (TblsInspLotRMConfig.Lot obj: TblsInspLotRMConfig.Lot.values()){
                String objName = obj.getName();
                if (fldName.equalsIgnoreCase(objName)){
                    //tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getDbFieldDefinitionPostgres());
                    return tableFields;
                }
            }           
            return tableFields;            
        }
    }    

    public enum MaterialCertificate{

        /**
         *
         */
        TBL("material_certificate", true,  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_MATERIAL, #FLD_CONFIG_NAME)) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
        FLD_MATERIAL("material", true, LPDatabase.stringNotNull())        ,
        FLD_CONFIG_NAME("config_name", true, LPDatabase.stringNotNull())        ,
        FLD_ANALYSIS_LIST("analysis_list", true, LPDatabase.stringNotNull())        ,
        FLD_PRINTABLE("printable", true, LPDatabase.booleanFld()),
        FLD_PARTIAL_ANALYSIS_LIST("partial_analysis_list", true, LPDatabase.stringNotNull())        ,
        FLD_PARTIAL_PRINTABLE("partial_printable", true, LPDatabase.booleanFld()),
        FLD_TRACK_ACCESS("track_access", true, LPDatabase.booleanFld()),
        FLD_TRACK_PRINT("track_print", true, LPDatabase.booleanFld()),
        ;
        private MaterialCertificate(String dbObjName, Boolean fldMandatory, String dbObjType){
            this.dbObjName=dbObjName;
            this.fieldIsMandatory=fldMandatory;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return
         */
        public String getName(){
            return this.dbObjName;
        }
        public Boolean getFieldIsMandatory(){
            return this.fieldIsMandatory;
        }
        String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        /**
         *
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = MaterialCertificate.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (MaterialCertificate obj: MaterialCertificate.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && ( (obj.getFieldIsMandatory()) || (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) )){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        fieldsScript.append(currField[0]).append(" ").append(currField[1]);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }
        public static String updateTableScript(String schemaNamePrefix, String[] fields){
            return updateTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String updateTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblAlterScript=new StringBuilder(0);
            HashMap<String[], Object[][]> dbTableGetFieldDefinition = dbTableGetFieldDefinition(schemaNamePrefix, Lot.TBL.getName());

            String[] fldDefinitionColName= dbTableGetFieldDefinition.keySet().iterator().next();    
            Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
            //if ( dbTableGetFieldDefinition1.get(FldDefinitionColName).length()!=whereFieldsNameArr[iFields].length()){
            Object[] tableFldsInfoColumns = LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name"));
            if (fields==null || (fields.length==1 && fields[0].length()==0)) fields=getAllFieldNames();

            for (String curFld: fields){
                if (!LPArray.valueInArray(tableFldsInfoColumns, curFld)){
                    String[] currField = getFldDefBydbFieldName(curFld);
                    if (tblAlterScript.length()>0)tblAlterScript.append(", ");
                    tblAlterScript.append(LPDatabase.addColumn()).append(" ").append(currField[0]).append(" ").append(currField[1]);                            
                }
                
            }
/*            
            for (TblsInspLotRMConfig.Lot obj: TblsInspLotRMConfig.Lot.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( !"TBL".equalsIgnoreCase(objName)) {
                    if (!LPArray.valueInArray(tableFldsInfoColumns, currField[0])){
                        if (tblAlterScript.length()>0)tblAlterScript.append(", ");
                        tblAlterScript.append(LPDatabase.addColumn()).append(" ").append(currField[0]).append(" ").append(currField[1]);                            
                    }
                }
            }
*/            
            if (tblAlterScript.toString().length()>0)
                return LPDatabase.alterTable()+" "+LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName())+"."+Lot.TBL.getName()+" "+tblAlterScript.toString()+";";
            else
                return tblAlterScript.toString();
/*            for (String curFld: fields){
                if (!LPArray.valueInArray(tableFldsInfoColumns, curFld))
                    tblAlterScript.append(addColumn)+
            }*/
            //tblAlterScript.append(LPDatabase.alterTableAddColumn());
        }
        private final String dbObjName;             
        private final Boolean fieldIsMandatory;             
        private final String dbObjTypePostgres;     
        
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (TblsInspLotRMConfig.Lot obj: TblsInspLotRMConfig.Lot.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }  
        public static String[] getFldDefBydbFieldName(String fldName){
            String[] tableFields=new String[0];
            for (TblsInspLotRMConfig.Lot obj: TblsInspLotRMConfig.Lot.values()){
                String objName = obj.getName();
                if (fldName.equalsIgnoreCase(objName)){
                    //tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getDbFieldDefinitionPostgres());
                    return tableFields;
                }
            }           
            return tableFields;            
        }
    }    
    
    public enum Lot{

        /**
         *
         */
        TBL("lot", true,  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_CODE, #FLD_CODE_VERSION)) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_CODE("code", true, LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_CODE_VERSION("code_version", true, LPDatabase.integer()),
        FLD_DESCRIPTION("description", true, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_JSON_DEFINITION("json_definition", false, "json")
        ,

        /**
         *
         */
        FLD_JSON_DEFINITION_STR("json_definition_str", false, LPDatabase.string()),
        FLD_CAMPO1("campo1", false, LPDatabase.integer()),
        FLD_CAMPO2("campo2", false, LPDatabase.integer()),
        FLD_CAMPO3("campo3", false, LPDatabase.integer()),
        ;
        private Lot(String dbObjName, Boolean fldMandatory, String dbObjType){
            this.dbObjName=dbObjName;
            this.fieldIsMandatory=fldMandatory;
            this.dbObjTypePostgres=dbObjType;
        }

        /**
         *
         * @return
         */
        public String getName(){
            return this.dbObjName;
        }
        public Boolean getFieldIsMandatory(){
            return this.fieldIsMandatory;
        }
        String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        /**
         *
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = Lot.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsInspLotRMConfig.Lot obj: TblsInspLotRMConfig.Lot.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && ( (obj.getFieldIsMandatory()) || (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) )){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        fieldsScript.append(currField[0]).append(" ").append(currField[1]);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }
        public static String updateTableScript(String schemaNamePrefix, String[] fields){
            return updateTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String updateTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblAlterScript=new StringBuilder(0);
            HashMap<String[], Object[][]> dbTableGetFieldDefinition = dbTableGetFieldDefinition(schemaNamePrefix, Lot.TBL.getName());

            String[] fldDefinitionColName= dbTableGetFieldDefinition.keySet().iterator().next();    
            Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
            //if ( dbTableGetFieldDefinition1.get(FldDefinitionColName).length()!=whereFieldsNameArr[iFields].length()){
            Object[] tableFldsInfoColumns = LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name"));
            if (fields==null || (fields.length==1 && fields[0].length()==0)) fields=getAllFieldNames();

            for (String curFld: fields){
                if (!LPArray.valueInArray(tableFldsInfoColumns, curFld)){
                    String[] currField = getFldDefBydbFieldName(curFld);
                    if (tblAlterScript.length()>0)tblAlterScript.append(", ");
                    tblAlterScript.append(LPDatabase.addColumn()).append(" ").append(currField[0]).append(" ").append(currField[1]);                            
                }
                
            }
/*            
            for (TblsInspLotRMConfig.Lot obj: TblsInspLotRMConfig.Lot.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( !"TBL".equalsIgnoreCase(objName)) {
                    if (!LPArray.valueInArray(tableFldsInfoColumns, currField[0])){
                        if (tblAlterScript.length()>0)tblAlterScript.append(", ");
                        tblAlterScript.append(LPDatabase.addColumn()).append(" ").append(currField[0]).append(" ").append(currField[1]);                            
                    }
                }
            }
*/            
            if (tblAlterScript.toString().length()>0)
                return LPDatabase.alterTable()+" "+LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName())+"."+Lot.TBL.getName()+" "+tblAlterScript.toString()+";";
            else
                return tblAlterScript.toString();
/*            for (String curFld: fields){
                if (!LPArray.valueInArray(tableFldsInfoColumns, curFld))
                    tblAlterScript.append(addColumn)+
            }*/
            //tblAlterScript.append(LPDatabase.alterTableAddColumn());
        }
        private final String dbObjName;             
        private final Boolean fieldIsMandatory;             
        private final String dbObjTypePostgres;     
        
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (TblsInspLotRMConfig.Lot obj: TblsInspLotRMConfig.Lot.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                }
            }           
            return tableFields;
        }  
        public static String[] getFldDefBydbFieldName(String fldName){
            String[] tableFields=new String[0];
            for (TblsInspLotRMConfig.Lot obj: TblsInspLotRMConfig.Lot.values()){
                String objName = obj.getName();
                if (fldName.equalsIgnoreCase(objName)){
                    //tableFields=LPArray.addValueToArray1D(tableFields, obj.getName());
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getDbFieldDefinitionPostgres());
                    return tableFields;
                }
            }           
            return tableFields;            
        }
    }    
    
    public enum LotRules{

        /**
         *
         */
        TBL("lot_rules",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_CODE, #FLD_CODE_VERSION) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_CODE("code", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_CODE_VERSION("code_version", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_STATUSES("statuses", LPDatabase.string())
        ,

        FLD_DEFAULT_STATUS("default_status", LPDatabase.string())
        ,

/*        FLD_TEST_ANALYST_REQUIRED("test_analyst_required", LPDatabase.booleanFld())
        ,

        FLD_ANALYST_ASSIGNMENT_MODE("analyst_assignment_mode", LPDatabase.string())
        ,

        FLD_FIELD_DEFAULT_VALUES("field_default_values", LPDatabase.string())
        ,        

        FLD_AUTO_ADD_SAMPLE_ANALYSIS_LEVEL("auto_add_sample_analysis_lvl", LPDatabase.string())        */
        ;
        private LotRules(String dbObjName, String dbObjType){
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
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = LotRules.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsInspLotRMConfig.LotRules obj: TblsInspLotRMConfig.LotRules.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        fieldsScript.append(currField[0]).append(" ").append(currField[1]);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }        
        private final String dbObjName;             
        private final String dbObjTypePostgres;                     
    }        

    public enum LotDecisionRules{

        TBL("lot_decision_rules",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_CODE, #FLD_CODE_VERSION) )" +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")        ,
        FLD_CODE("code", LPDatabase.stringNotNull())        ,
        FLD_CODE_VERSION("code_version", LPDatabase.integer())        ,
        FLD_DECISIONS_LIST("decisions_list", LPDatabase.string())        ,        
        FLD_MINIMUM_ANALYSIS_REQUIRED_LIST("minimum_analysis_required_list", LPDatabase.string())        ,        
        FLD_SAMPLE_ANALYSIS_REVISION_REQUIRED("sample_analysis_revision_required", LPDatabase.booleanFld(true))        ,        
        FLD_SAMPLE_REVISION_REQUIRED("sample_revision_required", LPDatabase.booleanFld(true))        ,        
        FLD_ALLOW_AUTO_DECISION("allow_auto_decision", LPDatabase.booleanFld(false))        ,
        FLD_ALLOW_DECISION_PARTIAL_RESULTS("allow_decision_partial_results", LPDatabase.booleanFld(false))        ,
        FLD_ALLOW_GENERATE_COA_PARTIAL_RESULTS("allow__generate_coa_partial_results", LPDatabase.booleanFld(false))        ,


/*        FLD_TEST_ANALYST_REQUIRED("test_analyst_required", LPDatabase.booleanFld())
        ,

        FLD_ANALYST_ASSIGNMENT_MODE("analyst_assignment_mode", LPDatabase.string())
        ,

        FLD_FIELD_DEFAULT_VALUES("field_default_values", LPDatabase.string())
        ,        

        FLD_AUTO_ADD_SAMPLE_ANALYSIS_LEVEL("auto_add_sample_analysis_lvl", LPDatabase.string())        */
        ;
        private LotDecisionRules(String dbObjName, String dbObjType){
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
         * @param schemaNamePrefix
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = LotDecisionRules.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.CONFIG.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (TblsInspLotRMConfig.LotDecisionRules obj: TblsInspLotRMConfig.LotDecisionRules.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        fieldsScript.append(currField[0]).append(" ").append(currField[1]);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (TblsInspLotRMConfig.LotDecisionRules obj: TblsInspLotRMConfig.LotDecisionRules.values()){
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
