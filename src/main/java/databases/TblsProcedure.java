/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import static lbplanet.utilities.LPDatabase.dateTime;
import static lbplanet.utilities.LPDatabase.dateTimeWithDefaultNow;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ForeignkeyFld;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class TblsProcedure {

    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.PROCEDURE.getName();
    public enum TablesProcedure implements EnumIntTables{
        INVESTIGATION(null, "investigation", SCHEMA_NAME, true, TblsProcedure.Investigation.values(), TblsProcedure.Investigation.ID.getName(),
            new String[]{TblsProcedure.Investigation.ID.getName()}, null, "Investigation objects"),
        INVEST_OBJECTS(null, "invest_objects", SCHEMA_NAME, true, TblsProcedure.InvestObjects.values(), TblsProcedure.InvestObjects.ID.getName(),
            new String[]{TblsProcedure.InvestObjects.ID.getName()}, null, "Objects added to one given investigation"),
        ;
        private TablesProcedure(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
                String seqName, String[] primaryK, ForeignkeyFld foreignK, String comment){
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
        @Override        public ForeignkeyFld getForeignKey() {return this.foreignkey;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String tableName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final String sequence;
        private final EnumIntTableFields[] tableFields;
        private final String[] primarykey;
        private final ForeignkeyFld foreignkey;
        private final String tableComment;
    }
    
    /**
     *
     */
    public static final String SCHEMATAG = "#SCHEMA";

    /**
     *
     */
    public static final String TABLETAG = "#TBL";

    /**
     *
     */
    public static final String OWNERTAG = "#OWNER";

    /**
     *
     */
    public static final String TABLESPACETAG = "#TABLESPACE";

    /**
     *
     */
    public static final String FIELDSTAG = "#FLDS";
    
    /**
     *
     */
    public enum PersonProfile{

        /**
         *
         */
        TBL("person_profile",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_PERSON_NAME, #FLD_ROLE_NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+"  TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_PERSON_NAME("person_name", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_ROLE_NAME("role_name", LPDatabase.stringNotNull()),        
        FLD_ACTIVE("active", LPDatabase.booleanFld()),
        FLD_USER_TITLE("user_title", LPDatabase.string()),
        
/*        , FLD_ANALYSIS("analysis", LPDatabase.StringNotNull())
         , FLD_METHOD_VERSION("method_version", LPDatabase.IntegerNotNull())
        , FLD_MANDATORY("mandatory", LPDatabase.Boolean())
        , FLD_PARAM_TYPE("param_type", LPDatabase.String())
        , FLD_NUM_REPLICAS("num_replicas", LPDatabase.Integer())
        , FLD_UOM("uom", LPDatabase.String())
        , FLD_UOM_CONVERSION_MODE("uom_conversion_mode", LPDatabase.String())*/
        ;
        private PersonProfile(String dbObjName, String dbObjType){
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
         * @param schemaNamePrefix - Procedure Instance where it applies
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = PersonProfile.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (PersonProfile obj: PersonProfile.values()){
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

    /**
     *
     */
    public enum ProcedureEvents{

        /**
         *
         */
        TBL("procedure_events",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_NAME, #FLD_ROLE_NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+"  TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,

        /**
         *
         */
        FLD_NAME("name", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_ROLE_NAME("role_name", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_MODE("mode", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_TYPE("type", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_BRANCH_LEVEL("branch_level", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_LABEL_EN("label_en", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_LABEL_ES("label_es", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_ORDER_NUMBER("order_number", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_SOP("sop", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_ESIGN_REQUIRED("esign_required", LPDatabase.booleanFld())
        ,
        FLD_USERCONFIRM_REQUIRED("userconfirm_required", LPDatabase.booleanFld())
        ,

        /**
         *
         */
        FLD_LP_FRONTEND_PAGE_NAME("lp_frontend_page_name", LPDatabase.string()),
        FLD_ICON_NAME("icon_name", LPDatabase.string()),
        FLD_LP_NEW_FRONTEND_PAGE_NAME("lp_new_frontend_page_name", LPDatabase.string()),
        FLD_NEW_ICON_NAME("new_icon_name", LPDatabase.string()),
        FLD_NEW_ICON_NAME_WHEN_NOT_CERTIFIED("new_icon_name_when_not_certified", LPDatabase.string()),
        FLD_NEW_NAME("new_name", LPDatabase.stringNotNull()),
        FLD_POSITION("position", LPDatabase.string()),
        FLD_PARENT_NAME("parent_name", LPDatabase.string())
        ;
        private ProcedureEvents(String dbObjName, String dbObjType){
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
         * @param schemaNamePrefix - Procedure Instance where it applies
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureEvents.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureEvents obj: ProcedureEvents.values()){
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
            for (ProcedureEvents obj: ProcedureEvents.values()){
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
    public enum ProcedureInfo{
        TBL("procedure_info",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+"  TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")        ,
        FLD_NAME("name", LPDatabase.stringNotNull()),
        FLD_VERSION("version", LPDatabase.integerNotNull())        ,
        FLD_SCHEMA_PREFIX("schema_prefix", LPDatabase.stringNotNull())        ,
        FLD_LABEL_EN("label_en", LPDatabase.string())        ,
        FLD_LABEL_ES("label_es", LPDatabase.string()),
        FLD_DESCRIPTION("description", LPDatabase.string())        
        ;
        private ProcedureInfo(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }
        public String getName(){
            return this.dbObjName;
        }
        public String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        /**
         *
         * @param schemaNamePrefix - Procedure Instance where it applies
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureInfo.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureInfo obj: ProcedureInfo.values()){
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
            for (ProcedureInfo obj: ProcedureInfo.values()){
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

    public enum ProcedureBusinessRules{
        TBL("procedure_business_rules",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_AREA, #FLD_RULE_NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+"  TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")        ,
        FLD_AREA("area", LPDatabase.stringNotNull())        ,
        FLD_RULE_NAME("rule_name", LPDatabase.stringNotNull())        ,
        FLD_RULE_VALUE("rule_value", LPDatabase.string())        ,
        FLD_DISABLED("disabled", LPDatabase.booleanFld(false))        ,
        ;
        private ProcedureBusinessRules(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }
        public String getName(){
            return this.dbObjName;
        }

        /**
         *
         * @return
         */
        public String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        /**
         *
         * @param schemaNamePrefix - Procedure Instance where it applies
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureInfo.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureInfo obj: ProcedureInfo.values()){
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

    // Esta no tiene sentido a este nivel porque business rules a nivel de proceso son archivos, no tabla.
    //  La que tiene sentido está en TblsReqs porque es la declaración de cuales son las reglas de negocio del proceso
    //      según se definen en el Excel.
/*    
    public enum ProcedureBusinessRules {

        FLD_ID("id", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_#FLD_ID_seq'::regclass)"),        
        TBL("procedure_business_rules",  LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey PRIMARY KEY (#FLD_PROCEDURE_NAME) )" +
                LPDatabase.POSTGRESQL_OIDS+"  TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
        FLD_PROCEDURE_NAME("procedure_name", LPDatabase.stringNotNull()),
        FLD_PROCEDURE_VERSION("procedure_version", LPDatabase.integerNotNull()),
        FLD_INSTANCE_NAME("instance_name", LPDatabase.stringNotNull()),
        FLD_MODULE_NAME("module_name", LPDatabase.stringNotNull()),
        FLD_MODULE_VERSION("module_version", LPDatabase.integerNotNull()),
        FLD_FILE_SUFFIX("file_suffix", LPDatabase.string()),
        FLD_RULE_NAME("rule_name", LPDatabase.string()),
        FLD_RULE_VALUE("rule_value", LPDatabase.string()),
        FLD_ACTIVE("active", LPDatabase.booleanFld())
        ;
        private ProcedureBusinessRules(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjTypePostgres=dbObjType;
        }

        public String getName(){
            return this.dbObjName;
        }

        public String[] getDbFieldDefinitionPostgres(){
            return new String[]{this.dbObjName, this.dbObjTypePostgres};
        }

        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ProcedureBusinessRules.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProcedureBusinessRules obj: ProcedureBusinessRules.values()){
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
            for (ProcedureBusinessRules obj: ProcedureBusinessRules.values()){
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
*/
    public enum ViewProcUserAndRoles{

        /**
         *
         */
        TBL("proc_user_and_roles",  LPDatabase.createView() +
                " SELECT #FLDS from #SCHEMA.person_profile persprof " +
                "   INNER JOIN \"app\".users usr on usr.person_name=persprof.person_name; "+
                "ALTER VIEW  #SCHEMA.#TBL  OWNER TO #OWNER;")
        ,

        /**
         *
         */
        FLD_USER_NAME("user_name", "usr.user_name")
        ,

        /**
         *
         */
        FLD_EMAIL("email", "usr.email")
        ,

        /**
         *
         */
        FLD_ROLE_NAME("role_name", "persprof.role_name")
        ,
        /**
         *
         */
        FLD_USER_TITLE("user_title", "persprof.user_title")
        ,

        /**
         *
         */
        FLD_ACTIVE("active", "persprof.active")
        ;
        private ViewProcUserAndRoles(String dbObjName, String dbObjType){
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
         * @param schemaNamePrefix - Procedure Instance where it applies
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = ViewProcUserAndRoles.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            //tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#SCHEMA_CONFIG", LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ViewProcUserAndRoles obj: ViewProcUserAndRoles.values()){
                String[] currField = obj.getDbFieldDefinitionPostgres();
                String objName = obj.name();
                if ( (!"TBL".equalsIgnoreCase(objName)) && (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, currField[0]))) ) ){
                        if (fieldsScript.length()>0)fieldsScript.append(", ");
                        fieldsScript.append(currField[1]).append(" AS ").append(currField[0]);
                        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+obj.name(), currField[0]);
                }
            }
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
            return tblCreateScript.toString();
        }      
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ViewProcUserAndRoles obj: ViewProcUserAndRoles.values()){
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
    
    public enum Investigation implements EnumIntTableFields{
        ID("id", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_id_seq'::regclass)", null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, null, null, null),
        CLOSED("closed", LPDatabase.booleanFld(), null, null, null, null),
        CLOSED_ON("closed_on", LPDatabase.dateTime(), null, null, null, null),
        CLOSED_BY("closed_by", LPDatabase.string(), null, null, null, null),
        EXTERNAL_SYSTEM_ID("external_system_id", LPDatabase.string(), null, null, null, null),
        EXTERNAL_SYSTEM_NAME("external_system_name", LPDatabase.string(), null, null, null, null),
        CAPA_REQUIRED("capa_required", LPDatabase.booleanFld(), null, null, null, null),
        CAPA_DECISION_ON("capa_decision_on", LPDatabase.dateTime(), null, null, null, null),
        CAPA_DECISION_BY("capa_decision_by", LPDatabase.string(), null, null, null, null),
        CAPA_OBSERVATION("capa_observation", LPDatabase.string(), null, null, null, null),
        CAPA_EXTERNAL_SYSTEM_ID("capa_external_system_id", LPDatabase.string(), null, null, null, null),
        CAPA_EXTERNAL_SYSTEM_NAME("capa_external_system_name", LPDatabase.string(), null, null, null, null),
        ;
        private Investigation(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum InvestObjects implements EnumIntTableFields{
        ID("id", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_id_seq'::regclass)", null, null, null, null),
        INVEST_ID("invest_id", LPDatabase.integer(), null, null, null, null),
        OBJECT_TYPE("object_type", LPDatabase.string(), null, null, null, null),
        OBJECT_NAME("object_name", LPDatabase.string(), null, null, null, null),
        OBJECT_ID("object_id", LPDatabase.integer(), null, null, null, null),
        ADDED_ON("added_on", LPDatabase.dateTime(), null, null, null, null),
        ADDED_BY("added_BY", LPDatabase.string(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        NOTES("notes", LPDatabase.string(), null, null, null, null),
        ;
        private InvestObjects(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum ProgramCorrectiveAction{

        /**
         *
         */
        FLD_ID("id", "bigint NOT NULL DEFAULT nextval(' #SCHEMA.#TBL_id_seq'::regclass)"),

        /**
         *
         */
        TBL("program_corrective_action", LPDatabase.createSequence(FLD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_ID_seq OWNER TO #OWNER;"
                +  LPDatabase.createTable() + " (#FLDS ,  CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_ID) ) " +
                LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace()+"  ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")        
        /**
         *
         */
        ,

        /**
         *
         */
        FLD_STATUS("status", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_STATUS_PREVIOUS("status_previous", LPDatabase.stringNotNull())
        ,

        /**
         *
         */
        FLD_CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, dateTime())
        ,

        /**
         *
         */
        FLD_CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string())
        ,

        /**
         *
         */
        FLD_PROGRAM_NAME("program_name", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_LOCATION_NAME("location_name", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_AREA("area", LPDatabase.string())
        ,

        /**
         *
         */
        FLD_SAMPLE_ID("sample_id", LPDatabase.integer())
        ,

        /**
         *
         */
        FLD_TEST_ID("test_id", LPDatabase.integer())
        ,    

        /**
         *
         */
        FLD_RESULT_ID("result_id", LPDatabase.integer())    
        ,    		

        /**
         *
         */
        FLD_LIMIT_ID("limit_id", LPDatabase.integer())    		
        ,

        /**
         *
         */
        FLD_ANALYSIS("analysis", LPDatabase.stringNotNull(10))
        ,

        /**
         *
         */
        FLD_METHOD_NAME("method_name", LPDatabase.stringNotNull(10))
        ,    		

        /**
         *
         */
        FLD_METHOD_VERSION("method_version", LPDatabase.integer())    		
        ,

        /**
         *
         */
        FLD_PARAM_NAME("param_name", LPDatabase.stringNotNull(10))
        ,        

        /**
         *
         */
        FLD_SPEC_RULE_WITH_DETAIL("spec_rule_with_detail", LPDatabase.stringNotNull(10))        
        ,

        /**
         *
         */
        FLD_SPEC_EVAL("spec_eval", LPDatabase.stringNotNull(10))
        ,

        /**
         *
         */
        FLD_SPEC_EVAL_DETAIL("spec_eval_detail", LPDatabase.stringNotNull()),
        FLD_INVEST_ID("invest_id", LPDatabase.integer())    		

        ;
        private ProgramCorrectiveAction(String dbObjName, String dbObjType){
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
            String[] tblObj = ProgramCorrectiveAction.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (ProgramCorrectiveAction obj: ProgramCorrectiveAction.values()){
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
         * @return get all table fields
         */
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (ProgramCorrectiveAction obj: ProgramCorrectiveAction.values()){
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

    public enum SampleStageTimingCapture{

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
        private SampleStageTimingCapture(String dbObjName, String dbObjType){
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
            String[] tblObj = SampleStageTimingCapture.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (SampleStageTimingCapture obj: SampleStageTimingCapture.values()){
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
            for (SampleStageTimingCapture obj: SampleStageTimingCapture.values()){
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
    public enum SampleStageTimingIntervalDeviation{
        FLD_ID("id", "bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_id_seq'::regclass)"),
        TBL("sample_stage_timing_interval_deviation",  LPDatabase.createSequence(FLD_ID.getName())
                + "ALTER SEQUENCE #SCHEMA.#TBL_#FLD_ID_seq OWNER TO #OWNER;"
                + LPDatabase.createTable() + " (#FLDS , CONSTRAINT #TBL_pkey1 PRIMARY KEY (#FLD_ID) )" +
                LPDatabase.POSTGRESQL_OIDS+"  TABLESPACE #TABLESPACE; ALTER TABLE  #SCHEMA.#TBL" + LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";")
        ,
        FLD_SAMPLE_ID("sample_id", LPDatabase.integerNotNull())        ,        
        FLD_SAMPLE_CONFIG_CODE("sample_config_code", LPDatabase.stringNotNull()),
        FLD_SAMPLE_CONFIG_VERSION("sample_config_version", LPDatabase.integerNotNull())        ,        
        FLD_STAGE("stage", LPDatabase.stringNotNull()),
        FLD_STARTED_ON("started_on", dateTime()),
        FLD_ENDED_ON("ended_on", dateTime()),
        FLD_DATERANGE_INTERVAL_SECONDS("daterange_interval_seconds", LPDatabase.integer()),
        FLD_EXPECTED_INTERVAL_SECONDS("expected_interval_seconds", LPDatabase.integer())
        ;
        private SampleStageTimingIntervalDeviation(String dbObjName, String dbObjType){
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
         * @param schemaNamePrefix - Procedure Instance where it applies
         * @param fields
         * @return
         */
        public static String createTableScript(String schemaNamePrefix, String[] fields){
            return createTableScriptPostgres(schemaNamePrefix, fields);
        }
        private static String createTableScriptPostgres(String schemaNamePrefix, String[] fields){
            StringBuilder tblCreateScript=new StringBuilder(0);
            String[] tblObj = SampleStageTimingIntervalDeviation.TBL.getDbFieldDefinitionPostgres();
            tblCreateScript.append(tblObj[1]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(schemaNamePrefix, GlobalVariables.Schemas.PROCEDURE.getName()));
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tblObj[0]);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
            tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
            StringBuilder fieldsScript=new StringBuilder(0);
            for (SampleStageTimingIntervalDeviation obj: SampleStageTimingIntervalDeviation.values()){
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
        public static String[] getAllFieldNames(){
            String[] tableFields=new String[0];
            for (SampleStageTimingIntervalDeviation obj: SampleStageTimingIntervalDeviation.values()){
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
