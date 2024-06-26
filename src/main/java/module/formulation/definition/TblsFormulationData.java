/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.formulation.definition;

import databases.TblsAppConfig;
import databases.TblsCnfg;
import lbplanet.utilities.LPDatabase;
import module.instrumentsmanagement.definition.TblsInstrumentsData;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntTablesJoin;
import trazit.enums.EnumIntViewFields;
import trazit.enums.EnumIntViews;
import trazit.enums.FldBusinessRules;
import trazit.enums.ForeignkeyFld;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class TblsFormulationData {
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesFormulationData implements EnumIntTables{
        PROJECT(null, "project", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Formula.values(), 
            null, new String[]{Formula.NAME.getName()}, null, ""),
        FORMULA(null, "formula", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Formula.values(), 
            null, new String[]{Formula.NAME.getName()}, new Object[]{new ForeignkeyFld(Formula.PROJECT.getName(), 
                SCHEMA_NAME, TablesFormulationData.PROJECT.getTableName(), Project.NAME.getName())}, ""),
        FORMULA_INGREDIENTS(null, "formula_ingredients", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, FormulaIngredients.values(), 
            null, new String[]{FormulaIngredients.FORMULA.getName(), FormulaIngredients.INGREDIENT.getName()}, 
            new Object[]{new ForeignkeyFld(FormulaIngredients.FORMULA.getName(), 
                    SCHEMA_NAME, TablesFormulationData.FORMULA.getTableName(), Formula.NAME.getName())}, ""),
        FORMULA_ATTACHMENT(null, "formula_attachment", SCHEMA_NAME, true, FormulaAttachments.values(), FormulaAttachments.ID.getName(),
            new String[]{FormulaAttachments.ID.getName()}, null, "FormulaAttachments"),        
        ;
        private TablesFormulationData(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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

    public enum ViewsInstrumentsData implements EnumIntViews{        
        ;
        private ViewsInstrumentsData(String viewScript, FldBusinessRules[] fldBusRules, String dbVwName, String repositoryName, Boolean isProcedure, EnumIntViewFields[] vwFlds, 
                String comment, EnumIntTablesJoin[] tablesInView, String extraFilters, Boolean useFixViewScript){
            this.getTblBusinessRules=fldBusRules;
            this.viewName=dbVwName;
            this.viewFields=vwFlds;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.viewComment=comment;
            this.viewScript=viewScript;
            this.tablesInTheView=tablesInView;
            this.extraFilters=extraFilters;
            this.useFixViewScript=useFixViewScript;
        }
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public String getViewCreatecript() {return this.viewScript;}
        @Override        public String getViewName() {return this.viewName;}
        @Override        public EnumIntViewFields[] getViewFields() {return this.viewFields;}
        @Override        public String getViewComment() {return this.viewComment;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        @Override        public Boolean getUsesFixScriptView() {return this.useFixViewScript;}
        private final Boolean useFixViewScript;
        private final EnumIntTablesJoin[] tablesInTheView;
        @Override  public EnumIntTablesJoin[] getTablesRequiredInView() {return this.tablesInTheView;}
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String viewName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final EnumIntViewFields[] viewFields;
        private final String viewComment;
        private final String viewScript;

        @Override public String getExtraFilters() {return this.extraFilters;}
        private final String extraFilters;
    }
    
    public enum Project implements EnumIntTableFields{
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null, true),
        TYPE("type", LPDatabase.stringNotNull(), null, null, null, null, true),
        PURPOSE("purpose", LPDatabase.string(), null, null, null, null, false),
        CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null, true),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null, true),
        IS_OPEN("is_open", LPDatabase.booleanFld(true), null, null, null, null, true),
        IS_LOCKED("is_locked", LPDatabase.booleanFld(false), null, null, null, null, true),
        RESPONSIBLE("responsible", LPDatabase.string(), null, null, null, null, true)
        ;
        private Project(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules, Boolean isSystFld){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
            this.isSystemFld=isSystFld;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;
        private final Boolean isSystemFld;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
        @Override        public Boolean isSystemField(){return this.isSystemFld;}
    }        

    public enum Formula implements EnumIntTableFields{
        PROJECT("project", LPDatabase.stringNotNull(), null, null, null, null, true),
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null, true),
        PURPOSE("purpose", LPDatabase.string(), null, null, null, null, false),
        CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null, true),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null, true),
        IS_OPEN("is_open", LPDatabase.booleanFld(true), null, null, null, null, true),
        RESPONSIBLE("responsible", LPDatabase.string(), null, null, null, null, true),
        IS_LOCKED(TblsInstrumentsData.Instruments.IS_LOCKED.getName(),TblsInstrumentsData.Instruments.IS_LOCKED.getFieldType(), null, null, null, null, true),
        LOCKED_REASON(TblsInstrumentsData.Instruments.LOCKED_REASON.getName(),TblsInstrumentsData.Instruments.LOCKED_REASON.getFieldType(), null, null, null, null, true)
        ;
        private Formula(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules, Boolean isSystFld){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
            this.isSystemFld=isSystFld;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;
        private final Boolean isSystemFld;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
        @Override        public Boolean isSystemField(){return this.isSystemFld;}
    }        
    
    public enum FormulaIngredients implements EnumIntTableFields{
        FORMULA("formula", LPDatabase.stringNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number",LPDatabase.real(), null, null, null, null),
        INGREDIENT("ingredient", LPDatabase.stringNotNull(), null, null, null, null),
        QUANTITY("quantity",LPDatabase.real(), null, null, null, null),
        QUANTITY_UOM("quantity_uom",LPDatabase.string(), null, new ReferenceFld(SCHEMA_NAME, TblsCnfg.TablesConfig.UOM.getTableName() , TblsCnfg.UnitsOfMeasurement.NAME.getName()), null, null),        
        NOTES("notes", LPDatabase.string(), null, null, null, null),         
        IN_PERCENTAGE("in_percentage", LPDatabase.booleanFld(false), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null),
        REMOVED("removed", LPDatabase.booleanFld(false), null, null, null, null)
        //CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        //CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        //COMPLETED_ON("completed_on", LPDatabase.dateTime(), null, null, null, null),
        //COMPLETED_BY("completed_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        //COMPLETED_DECISION("completed_decision", LPDatabase.string(), null, null, null, null),
        //ATTACHMENT("attachment", LPDatabase.string(), null, null, null, null),         
        ;
        private FormulaIngredients(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum FormulaAttachments implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        FORMULA_NAME("formula_name", LPDatabase.stringNotNull(), null, null, null, null),
        QUALIF_ID("qualif_id", LPDatabase.integer(), null, null, null, null),        
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        FILE_LINK("file_link", LPDatabase.string(), null, null, null, null),
        DB_FILE("db_file", LPDatabase.embeddedFile(), null, null, null, null),
        BRIEF_SUMMARY("brief_summary", LPDatabase.string(), null, null, null, null),
        REMOVED("removed", LPDatabase.booleanFld(false), null, null, null, null),
        ;
        private FormulaAttachments(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }                
    
}
