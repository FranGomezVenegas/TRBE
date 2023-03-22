package module.inventorytrack.definition;


import databases.TblsAppConfig;
import databases.TblsCnfg;
import databases.TblsCnfg.TablesConfig;
import lbplanet.utilities.LPDatabase;
import static lbplanet.utilities.LPDatabase.dateTime;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntTablesJoin;
import trazit.enums.EnumIntViewFields;
import trazit.enums.EnumIntViews;
import trazit.enums.FldBusinessRules;

import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;

public class TblsInvTrackingConfig {
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.CONFIG.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesInvTrackingConfig implements EnumIntTables{        
        INV_CATEGORY(null, "inv_category", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Category.values(), null, new String[]{Category.NAME.getName()}, null, "Category table"),
        INV_REFERENCE(null, "inv_reference", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Reference.values(), null, new String[]{Reference.NAME.getName(), Reference.CATEGORY.getName()}, null, "Reference table"),
        VARIABLES(null, "variables", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsInvTrackingConfig.Variables.values(), null, new String[]{TblsInvTrackingConfig.Variables.PARAM_NAME.getName()}, null, "Variables table"),
        VARIABLES_SET(null, "variables_set", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsInvTrackingConfig.VariablesSet.values(), null, new String[]{VariablesSet.NAME.getName()}, null, "Variables Set table"),
        ;
        private TablesInvTrackingConfig(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    
    public enum ViewsInvTrackingConfig implements EnumIntViews{

        ;
        private ViewsInvTrackingConfig(String viewScript, FldBusinessRules[] fldBusRules, String dbVwName, String repositoryName, Boolean isProcedure, EnumIntViewFields[] vwFlds, 
                String comment, EnumIntTablesJoin[] tablesInView, String extraFilters){
            this.getTblBusinessRules=fldBusRules;
            this.viewName=dbVwName;
            this.viewFields=vwFlds;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.viewComment=comment;
            this.viewScript=viewScript;
            this.tablesInTheView=tablesInView;
            this.extraFilters=extraFilters;
        }
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public String getViewCreatecript() {return this.viewScript;}
        @Override        public String getViewName() {return this.viewName;}
        @Override        public EnumIntViewFields[] getViewFields() {return this.viewFields;}
        @Override        public String getViewComment() {return this.viewComment;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        @Override        public String getExtraFilters() {return this.extraFilters;}

        
        private final EnumIntTablesJoin[] tablesInTheView;
        @Override  public EnumIntTablesJoin[] getTablesRequiredInView() {return this.tablesInTheView;}        
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String viewName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final EnumIntViewFields[] viewFields;
        private final String viewComment;
        private final String viewScript;
        private final String extraFilters;
    }

    public enum Category implements EnumIntTableFields{
        NAME("name",LPDatabase.stringNotNull(), null, null, null, null)
        ;        
        private Category(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

        @Override        public String getName(){
            return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }

    public enum Reference implements EnumIntTableFields{
        NAME("name",LPDatabase.stringNotNull(), null, null, null, null),
        ACTIVE("active",LPDatabase.booleanFld(false), null, null, null, null),
//        CATEGORY("category",LPDatabase.stringNotNull(), null, new ReferenceFld(SCHEMA_NAME, TablesInvTrackingConfig.INV_CATEGORY.getTableName() , Category.NAME.getName()), null, null),
        CATEGORY("category",LPDatabase.stringNotNull(), null, null, null, null),
        LOT_REQUIRES_QUALIF("lot_requires_qualif",LPDatabase.booleanFld(false), null, null, null, null),
        QUALIF_VARIABLES_SET("qualif_variables_set", LPDatabase.string(), null, null, null, null),
        MIN_STOCK("min_stock",LPDatabase.real(), null, null, null, null),
        MIN_STOCK_UOM("min_stock_uom",LPDatabase.string(), null, new ReferenceFld(SCHEMA_NAME, TablesConfig.UOM.getTableName() , TblsCnfg.UnitsOfMeasurement.NAME.getName()), null, null),                
        ALLOWED_UOMS("allowed_uoms",LPDatabase.string(), null, null, "para limitar las unidades, el sistema hace conversiones siempre entre unidades de una misma familia, ALL para permitir cualquier unidad de medida", null),        
        MIN_STOCK_TYPE("min_stock_type",LPDatabase.string(), null, new ReferenceFld("ITEMS|VOLUME"), "ITEMS/VOLUME. ITEMS means number of available lots, VOLUME means the total quantity from all available lots", null),
        ALLOW_PROC_DISCOUNTS("allow_proc_discounts",LPDatabase.booleanFld(false), null, null, null, null),
        PROCESSES_LIST("processes_list",LPDatabase.string(), null, null, null, null),
        REQUIRES_AVAILABLES_FOR_USE("requires_availables_for_use",LPDatabase.booleanFld(false), null, null, null, null),
        MIN_AVAILABLES_FOR_USE("min_availables_for_use",LPDatabase.real(), null, null, null, null),
        MIN_AVAILABLES_FOR_USE_TYPE("min_availables_for_use_type",LPDatabase.string(), null, new ReferenceFld("ITEMS|VOLUME"), "ITEMS/VOLUME. ITEMS means number of available lots, VOLUME means the total quantity from all available lots", null),
        ALLOW_OPENING_SOME_AT_A_TIME("allow_opening_some_at_a_time",LPDatabase.booleanFld(true), null, null, null, null),        
        CREATED_BY("created_by",LPDatabase.stringNotNull(), null, null, null, null),
        CREATED_ON("created_on",LPDatabase.stringNotNull(), null, null, null, null)
        ;        
        private Reference(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.ref=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld ref;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){
            return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.ref;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum Variables implements EnumIntTableFields{
        PARAM_NAME("param_name",  LPDatabase.stringNotNull(100), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),
        PARAM_TYPE("param_type", LPDatabase.string(), null, null, null, null),
        REQUIRED("required", LPDatabase.string(), null, null, null, null),
        ALLOWED_VALUES("allowed_values", LPDatabase.string(), null, null, null, null),
        CREATED_ON("created_on", dateTime(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
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
        CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
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
      
}
