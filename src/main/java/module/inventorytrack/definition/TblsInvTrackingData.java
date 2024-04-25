/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inventorytrack.definition;

import databases.SqlStatementEnums.JOIN_TYPES;
import databases.TblsAppConfig;
import databases.TblsCnfg;
import databases.TblsCnfg.TablesConfig;
import lbplanet.utilities.LPDatabase;
import module.instrumentsmanagement.definition.TblsInstrumentsData.Instruments;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntTablesJoin;
import trazit.enums.EnumIntViewFields;
import trazit.enums.EnumIntViews;
import trazit.enums.FldBusinessRules;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class TblsInvTrackingData {
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesInvTrackingData implements EnumIntTables{
        LOT(null, "lot", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsInvTrackingData.Lot.values(), null,
            new String[]{Lot.LOT_NAME.getName(), Lot.REFERENCE.getName(), Lot.CATEGORY.getName()}, null, "lot table"),
        LOT_QUALIFICATION(null, "lot_qualification", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsInvTrackingData.LotQualification.values(), 
            TblsInvTrackingData.LotQualification.QUALIF_ID.getName(),new String[]{TblsInvTrackingData.LotQualification.QUALIF_ID.getName()}, null, ""),
        LOT_QUALIFICATION_VARIABLE_VALUES(null, "lot_qualification_variable_values", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, LotQualificationVariableValues.values(), 
            LotQualificationVariableValues.ID.getName(), new String[]{LotQualificationVariableValues.ID.getName()}, null, ""),
        LOT_ATTACHMENT(null, "lot_attachment", SCHEMA_NAME, true, LotAttachments.values(), LotAttachments.ID.getName(),
            new String[]{LotAttachments.ID.getName()}, null, "LotAttachments"),

        ;
        ;private TablesInvTrackingData(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    public enum ViewsInvTrackingData implements EnumIntViews{
        LOTS_EXPIRED("",
            null, "lots_expired", SCHEMA_NAME, true, TblsInvTrackingData.ViewExpiredLots.values(), "Lots which expiry_date or expiry_date_in_use is over", 
        new EnumIntTablesJoin[]{
            new EnumIntTablesJoin(TblsInvTrackingData.TablesInvTrackingData.LOT, "l", TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE, "s", true,
                new EnumIntTableFields[][]{{TblsInvTrackingData.Lot.REFERENCE, TblsInvTrackingConfig.Reference.NAME}, 
                {TblsInvTrackingData.Lot.CATEGORY, TblsInvTrackingConfig.Reference.CATEGORY}}, "", JOIN_TYPES.INNER),
        }, "where (expiry_date is not null or expiry_date_in_use is not null) " +
              "and ( " +
                "(expiry_date_in_use is null and expiry_date < now()) " +
                  "or (expiry_date_in_use is not null and expiry_date_in_use < now()) " +
                  "or (expiry_date_in_use<expiry_date and expiry_date_in_use < now()) " +
                ")", false),
        REFERENCES_STOCK_UNDER_MIN("",
            null, "references_stock_under_min", SCHEMA_NAME, true, TblsInvTrackingData.ViewReferencesStockUnderMin.values(), "Lots which expiry_date or expiry_date_in_use is over", 
        new EnumIntTablesJoin[]{
            new EnumIntTablesJoin(TblsInvTrackingData.TablesInvTrackingData.LOT, "l", TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE, "s", true,
                new EnumIntTableFields[][]{{TblsInvTrackingData.Lot.REFERENCE, TblsInvTrackingConfig.Reference.NAME}, 
                {TblsInvTrackingData.Lot.CATEGORY, TblsInvTrackingConfig.Reference.CATEGORY}}, "", JOIN_TYPES.INNER),
        }, "where (expiry_date is not null or expiry_date_in_use is not null) " +
              "and ( " +
                "(expiry_date_in_use is null and expiry_date < now()) " +
                  "or (expiry_date_in_use is not null and expiry_date_in_use < now()) " +
                  "or (expiry_date_in_use<expiry_date and expiry_date_in_use < now()) " +
                ") GROUP BY s.category, s.name", false),
        REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN("",
            null, "references_available_for_use_under_min", SCHEMA_NAME, true, TblsInvTrackingData.ViewReferencesAvailableForUseUnderMin.values(), "Lots which expiry_date or expiry_date_in_use is over", 
        new EnumIntTablesJoin[]{
            new EnumIntTablesJoin(TblsInvTrackingData.TablesInvTrackingData.LOT, "l", TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE, "s", true,
                new EnumIntTableFields[][]{{TblsInvTrackingData.Lot.REFERENCE, TblsInvTrackingConfig.Reference.NAME}, 
                {TblsInvTrackingData.Lot.CATEGORY, TblsInvTrackingConfig.Reference.CATEGORY}}, "", JOIN_TYPES.INNER),
        }, "where (expiry_date is not null or expiry_date_in_use is not null) " +
              "and ( " +
                "(expiry_date_in_use is null and expiry_date < now()) " +
                  "or (expiry_date_in_use is not null and expiry_date_in_use < now()) " +
                  "or (expiry_date_in_use<expiry_date and expiry_date_in_use < now()) " +
                ") ", false),
        AVAILABLE_LOTS_PER_REFERENCE("",
            null, "available_lots_per_reference", SCHEMA_NAME, true, TblsInvTrackingData.ViewAvailableLotsPerReference.values(), "Lots which expiry_date or expiry_date_in_use is over", 
        new EnumIntTablesJoin[]{
            new EnumIntTablesJoin(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_CATEGORY, "cat", TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE, "ref", true,
                new EnumIntTableFields[][]{{TblsInvTrackingConfig.Category.NAME, TblsInvTrackingConfig.Reference.CATEGORY}}, "", JOIN_TYPES.INNER),
        }, "", false),
        ;
        private ViewsInvTrackingData(String viewScript, FldBusinessRules[] fldBusRules, String dbVwName, String repositoryName, Boolean isProcedure, EnumIntViewFields[] vwFlds, 
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
        @Override        public Boolean getUsesFixScriptView() {return this.useFixViewScript;}
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public String getViewCreatecript() {return this.viewScript;}
        @Override        public String getViewName() {return this.viewName;}
        @Override        public EnumIntViewFields[] getViewFields() {return this.viewFields;}
        @Override        public String getViewComment() {return this.viewComment;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        @Override        public String getExtraFilters() {return this.extraFilters;}
        
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
        private final String extraFilters;
    }
    
    public enum Lot implements EnumIntTableFields{
        LOT_NAME("lot_name", LPDatabase.stringNotNull(), null, null, null, null),
        REFERENCE("reference", LPDatabase.string(), null, new ReferenceFld(SCHEMA_NAME, TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableName() , TblsInvTrackingConfig.Reference.NAME.getName()), null, null),
        CATEGORY("category", LPDatabase.string(), null, new ReferenceFld(SCHEMA_NAME, TblsInvTrackingConfig.TablesInvTrackingConfig.INV_CATEGORY.getTableName() , TblsInvTrackingConfig.Category.NAME.getName()), null, null),
        STATUS("status",LPDatabase.stringNotNull(), null, null, null, null),
        STATUS_PREVIOUS("status_previous",LPDatabase.string(), null, null, null, null),
        LOGGED_ON("logged_on", LPDatabase.dateTime(), null, null, null, null),
        LOGGED_BY("logged_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null), 
        EXPIRY_DATE("expiry_date", LPDatabase.date(), null, null, null, null),
        EXPIRY_DATE_IN_USE("expiry_date_in_use", LPDatabase.date(), null, null, null, null),
        RETEST_DATE("retest_date", LPDatabase.date(), null, null, null, null),
        VENDOR("vendor", LPDatabase.string(), null, null, null, null),
        VENDOR_LOT("vendor_lot", LPDatabase.string(), null, null, null, null),        
        VENDOR_REFERENCE("vendor_reference", LPDatabase.string(), null, null, null, null),
        VENDOR_COA_VERIFIED("vendor_coa_verified", LPDatabase.booleanFld(false), null, null, null, null),
        CONSERV_CONDITION("conservation_condition", LPDatabase.string(), null, null, null, null),
        PURITY("purity", LPDatabase.string(), null, null, null, null),
        QUANTITY("quantity",LPDatabase.real(), null, null, null, null),
        QUANTITY_UOM("quantity_uom",LPDatabase.string(), null, new ReferenceFld(SCHEMA_NAME, TablesConfig.UOM.getTableName() , TblsCnfg.UnitsOfMeasurement.NAME.getName()), null, null),        
        IS_LOCKED(Instruments.IS_LOCKED.getName(),Instruments.IS_LOCKED.getFieldType(), null, null, null, null),
        LOCKED_REASON(Instruments.LOCKED_REASON.getName(),Instruments.LOCKED_REASON.getFieldType(), null, null, null, null),
        RETIRED("retired", LPDatabase.booleanFld(false), null, null, null, null),
        RETIRED_ON("retired_on", LPDatabase.dateTime(), null, null, null, null),
        RETIRED_BY("retired_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null), 
        ;
        private Lot(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum LotQualification implements EnumIntTableFields{
        QUALIF_ID("qualif_id", LPDatabase.integerNotNull(), null, null, null, null),
        LOT_NAME(Lot.LOT_NAME.getName(), LPDatabase.string(), null, null, null, null),//, null, new ReferenceFld(GlobalVariables.Schemas.DATA.getName(), TablesInvTrackingData.LOT.getTableName(), Lot.LOT_NAME.getName()), null, null),
        CATEGORY(Lot.CATEGORY.getName(), LPDatabase.stringNotNull(), null, null, null, null),         
        REFERENCE(Lot.REFERENCE.getName(), LPDatabase.stringNotNull(), null, null, null, null),         
        EVENT_TYPE("event_type", LPDatabase.string(), null, null, null, null),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),//, null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        COMPLETED_ON("completed_on", LPDatabase.dateTime(), null, null, null, null),
        COMPLETED_BY("completed_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),//, null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        COMPLETED_DECISION("completed_decision", LPDatabase.string(), null, null, null, null),
        ATTACHMENT("attachment", LPDatabase.string(), null, null, null, null),         
        VARIABLES_SET("variables_set", LPDatabase.string(), null, null, null, null)//, null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsInvTrackingConfig.TablesInvTrackingConfig.VARIABLES_SET.getTableName(), TblsInvTrackingConfig.VariablesSet.NAME.getName()), null, null),
        ;
        private LotQualification(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum LotQualificationVariableValues implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        LOT_NAME(Lot.LOT_NAME.getName(), LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.DATA.getName(), TablesInvTrackingData.LOT.getTableName(), Lot.LOT_NAME.getName()), null, null),
        QUALIF_ID(LotQualification.QUALIF_ID.getName(), LPDatabase.integerNotNull(), null, new ReferenceFld(GlobalVariables.Schemas.DATA.getName(), TablesInvTrackingData.LOT_QUALIFICATION.getTableName(), LotQualification.QUALIF_ID.getName()), null, null),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        VARIABLE_SET("variable_set", LPDatabase.stringNotNull(), null, null, null, null),
        PARAM_NAME("param_name", LPDatabase.stringNotNull(), null, null, null, null),
        VALUE("value", LPDatabase.string(), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        PARAM_TYPE("param_type", LPDatabase.string(), null, null, null, null),
        REQUIRED("required", LPDatabase.string(), null, null, null, null),
        ALLOWED_VALUES("allowed_values", LPDatabase.string(), null, null, null, null),
        OWNER_ID("owner_id", LPDatabase.stringNotNull(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        ENTERED_ON("entered_on", LPDatabase.dateTime(), null, null, null, null),
        ENTERED_BY("entered_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        REENTERED("reentered", LPDatabase.booleanFld(false), null, null, null, null),        
        ;
        private LotQualificationVariableValues(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum ViewExpiredLots implements EnumIntViewFields{
        LOT_NAME("l", Lot.LOT_NAME.getName(), "l."+"lot_name", Lot.LOT_NAME, null, null, null),
        REFERENCE("l", Lot.REFERENCE.getName(), "l."+Lot.REFERENCE.getName(), Lot.REFERENCE, null, null, null),
        CATEGORY("l", Lot.CATEGORY.getName(), "l."+Lot.CATEGORY.getName(), Lot.CATEGORY, null, null, null),
        STATUS("l", Lot.STATUS.getName(), "l."+Lot.STATUS.getName(), Lot.STATUS, null, null, null),
        STATUS_PREVIOUS("l", Lot.STATUS_PREVIOUS.getName(), "l."+Lot.STATUS_PREVIOUS.getName(), Lot.STATUS_PREVIOUS, null, null, null),
        LOGGED_ON("l", Lot.LOGGED_ON.getName(), "l."+Lot.LOGGED_ON.getName(), Lot.LOGGED_ON, null, null, null),
        LOGGED_BY("l", Lot.LOGGED_BY.getName(), "l."+Lot.LOGGED_BY.getName(), Lot.LOGGED_BY, null, null, null),
        EXPIRY_DATE("l", Lot.EXPIRY_DATE.getName(), "l."+Lot.EXPIRY_DATE.getName(), Lot.EXPIRY_DATE, null, null, null),
        EXPIRY_DATE_IN_USE("l", Lot.EXPIRY_DATE_IN_USE.getName(), "l."+Lot.EXPIRY_DATE_IN_USE.getName(), Lot.EXPIRY_DATE_IN_USE, null, null, null),
        RETEST_DATE("l", Lot.RETEST_DATE.getName(), "l."+Lot.RETEST_DATE.getName(), Lot.RETEST_DATE, null, null, null),
        VENDOR("l", Lot.VENDOR.getName(), "l."+Lot.VENDOR.getName(), Lot.VENDOR, null, null, null),
        VENDOR_LOT("l", Lot.VENDOR_LOT.getName(), "l."+Lot.VENDOR_LOT.getName(), Lot.VENDOR_LOT, null, null, null),
        VENDOR_REFERENCE("l", Lot.VENDOR_REFERENCE.getName(), "l."+Lot.VENDOR_REFERENCE.getName(), Lot.VENDOR_REFERENCE, null, null, null),
        VENDOR_COA_VERIFIED("l", Lot.VENDOR_COA_VERIFIED.getName(), "l."+Lot.VENDOR_COA_VERIFIED.getName(), Lot.VENDOR_COA_VERIFIED, null, null, null),
        CONSERV_CONDITION("l", Lot.CONSERV_CONDITION.getName(), "l."+Lot.CONSERV_CONDITION.getName(), Lot.CONSERV_CONDITION, null, null, null),
        PURITY("l", Lot.PURITY.getName(), "l."+Lot.PURITY.getName(), Lot.PURITY, null, null, null),
        VOLUME("l", Lot.QUANTITY.getName(), "l."+Lot.QUANTITY.getName(), Lot.QUANTITY, null, null, null),
        VOLUME_UOM("l", Lot.QUANTITY_UOM.getName(), "l."+Lot.QUANTITY_UOM.getName(), Lot.QUANTITY_UOM, null, null, null),
        IS_LOCKED("l", Lot.IS_LOCKED.getName(), "l."+Lot.IS_LOCKED.getName(), Lot.IS_LOCKED, null, null, null),
        LOCKED_REASON("l", Lot.LOCKED_REASON.getName(), "l."+Lot.LOCKED_REASON.getName(), Lot.LOCKED_REASON, null, null, null),
        RETIRED("l", Lot.RETIRED.getName(), "l."+Lot.RETIRED.getName(), Lot.RETIRED, null, null, null),
        RETIRED_ON("l", Lot.RETIRED_ON.getName(), "l."+Lot.RETIRED_ON.getName(), Lot.RETIRED_ON, null, null, null),
        RETIRED_BY("l", Lot.RETIRED_BY.getName(), "l."+Lot.RETIRED_BY.getName(), Lot.RETIRED_BY, null, null, null),        
        
        EXPIRY_REASON("l", "expiry_reason", "CASE " +
            " WHEN expiry_date_in_use is not null and expiry_date is not null then 'expiry_date_in_use passed ( Expiry date in use: '||expiry_date_in_use::text||' , expiry date: '||expiry_date::text||' )' \n" +
            " when expiry_date_in_use is null and expiry_date is not null then 'expiry_date passed ( Expiry date: '||expiry_date::text||' )' \n" +
            " when expiry_date_in_use is not null and expiry_date is null then 'expiry_date_in_use passed ( Expiry date in use: '||expiry_date_in_use::text||' )' \n" +
            " else 'not recognized this case, please report it to Trazit' " +
            "end as expiry_reason", Lot.LOCKED_REASON, null, null, null),
        ;
        private ViewExpiredLots(String tblAliasInView, String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
//            try{
//            this.fldName="";
            this.fldName=name;
            this.fldAliasInView=vwAliasName;
            this.fldMask=fldMask;
            this.fldComment=comment;
            this.fldBusinessRules=busRules;
            this.fldObj=fldObj;
            this.tblAliasInView=tblAliasInView;
/*            }catch(Exception e){
                String s= e.getMessage();
                //String s2=name;
                this.fldName="";
            }*/
        }
        private final String fldName;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;   
        private final String tblAliasInView;
        @Override public String getName() {return fldName;}
        @Override public String getFldViewAliasName() {return this.fldAliasInView;}
        @Override public String getFieldMask() {return this.fldMask;}
        @Override public String getFieldComment() {return this.fldComment;}
        @Override public FldBusinessRules[] getFldBusinessRules() {return this.fldBusinessRules;}
        @Override public EnumIntTableFields getTableField() {return this.fldObj;}
        @Override public String getTblAliasInView() {return this.tblAliasInView;}
    }        

    public enum ViewReferencesStockUnderMin implements EnumIntViewFields{
        NAME("s", "name", "s."+"name", TblsInvTrackingConfig.Reference.NAME, null, null, null),
        MIN_STOCK_TYPE("s", TblsInvTrackingConfig.Reference.MIN_STOCK_TYPE.getName(), "s."+TblsInvTrackingConfig.Reference.MIN_STOCK_TYPE.getName(), TblsInvTrackingConfig.Reference.MIN_STOCK_TYPE, null, null, null),
        CATEGORY("s", TblsInvTrackingConfig.Reference.CATEGORY.getName(), "s."+TblsInvTrackingConfig.Reference.CATEGORY.getName(), TblsInvTrackingConfig.Reference.CATEGORY, null, null, null),
        MIN_STOCK("s", TblsInvTrackingConfig.Reference.MIN_STOCK.getName(), "s."+TblsInvTrackingConfig.Reference.MIN_STOCK.getName(), TblsInvTrackingConfig.Reference.MIN_STOCK_TYPE, null, null, null),
        CURRENT_STOCK("s", "current_stock", "case when s.min_stock_type='ITEMS' then count(l.*)::real " +
            "when s.min_stock_type='VOLUME' then sum(l.quantity) " +
            "else null::real end " +
            "as current_stock ", Lot.LOCKED_REASON, null, null, null)
        ;
        private ViewReferencesStockUnderMin(String tblAliasInView, String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
            this.fldName=name;
            this.fldAliasInView=vwAliasName;
            this.fldMask=fldMask;
            this.fldComment=comment;
            this.fldBusinessRules=busRules;
            this.fldObj=fldObj;
            this.tblAliasInView=tblAliasInView;
        }
        private final String fldName;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;
        private final String tblAliasInView;        
        @Override public String getName() {return fldName;}
        @Override public String getFldViewAliasName() {return this.fldAliasInView;}
        @Override public String getFieldMask() {return this.fldMask;}
        @Override public String getFieldComment() {return this.fldComment;}
        @Override public FldBusinessRules[] getFldBusinessRules() {return this.fldBusinessRules;}
        @Override public EnumIntTableFields getTableField() {return this.fldObj;}
        @Override public String getTblAliasInView() {return this.tblAliasInView;}
    }        

    public enum ViewReferencesAvailableForUseUnderMin implements EnumIntViewFields{
        CATEGORY("s", TblsInvTrackingConfig.Reference.CATEGORY.getName(), "s."+TblsInvTrackingConfig.Reference.CATEGORY.getName(), TblsInvTrackingConfig.Reference.CATEGORY, null, null, null),
        NAME("s", TblsInvTrackingConfig.Reference.NAME.getName(), "s."+TblsInvTrackingConfig.Reference.NAME.getName(), TblsInvTrackingConfig.Reference.NAME, null, null, null),
        MIN_STOCK_AVAILABLE_FOR_USE("s", TblsInvTrackingConfig.Reference.MIN_AVAILABLES_FOR_USE.getName(), "s."+TblsInvTrackingConfig.Reference.MIN_AVAILABLES_FOR_USE.getName(), TblsInvTrackingConfig.Reference.MIN_STOCK_TYPE, null, null, null),
        MIN_AVAILABLES_FOR_USE_TYPE("s", TblsInvTrackingConfig.Reference.MIN_AVAILABLES_FOR_USE_TYPE.getName(), "s.min_availables_for_use_type", TblsInvTrackingConfig.Reference.MIN_AVAILABLES_FOR_USE_TYPE, null, null, null),
        ALLOW_OPENING_SOME_AT_A_TIME("s", TblsInvTrackingConfig.Reference.ALLOW_OPENING_SOME_AT_A_TIME.getName(), "s."+TblsInvTrackingConfig.Reference.ALLOW_OPENING_SOME_AT_A_TIME.getName(), TblsInvTrackingConfig.Reference.MIN_STOCK_TYPE, null, null, null),
        CURRENT_STOCK_AVAILABLE_FOR_USE("ref", "current_stock_available_for_use", "case when ref.min_availables_for_use_type='ITEMS' then count(l.*) " +
            "when ref.min_availables_for_use_type='VOLUME' then sum(l.volume) " +
            "else null end " +
            "as current_stock_available_for_use ", Lot.LOCKED_REASON, null, null, null),
        ;
        private ViewReferencesAvailableForUseUnderMin(String tblAliasInView, String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
            this.fldName=name;
            this.fldAliasInView=vwAliasName;
            this.fldMask=fldMask;
            this.fldComment=comment;
            this.fldBusinessRules=busRules;
            this.fldObj=fldObj;
            this.tblAliasInView = tblAliasInView;
        }
        private final String fldName;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;
private final String tblAliasInView;        
        @Override public String getName() {return fldName;}
        @Override public String getFldViewAliasName() {return this.fldAliasInView;}
        @Override public String getFieldMask() {return this.fldMask;}
        @Override public String getFieldComment() {return this.fldComment;}
        @Override public FldBusinessRules[] getFldBusinessRules() {return this.fldBusinessRules;}
        @Override public EnumIntTableFields getTableField() {return this.fldObj;}
        @Override public String getTblAliasInView() {return this.tblAliasInView;}
    }        
    
    
    public enum ViewAvailableLotsPerReference implements EnumIntViewFields{
        NAME("ref", TblsInvTrackingConfig.Reference.NAME.getName(), "ref."+TblsInvTrackingConfig.Reference.NAME.getName(), TblsInvTrackingConfig.Reference.NAME, null, null, null),
        CATEGORY("ref", TblsInvTrackingConfig.Reference.CATEGORY.getName(), "ref."+TblsInvTrackingConfig.Reference.CATEGORY.getName(), TblsInvTrackingConfig.Reference.CATEGORY, null, null, null),
        COUNT("l", "count", "(select count(*) from #PROC_INSTANCE_NAME-#SCHEMA_DATA.lot l where l.category=ref.category and l.reference=ref.name and l.status='AVAILABLE_FOR_USE') as count ", Lot.LOCKED_REASON, null, null, null),
        ;
        private ViewAvailableLotsPerReference(String tblAliasInView, String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
            this.fldName=name;
            this.fldAliasInView=vwAliasName;
            this.fldMask=fldMask;
            this.fldComment=comment;
            this.fldBusinessRules=busRules;
            this.fldObj=fldObj;
            this.tblAliasInView=tblAliasInView;
        }
        private final String fldName;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;      
        private final String tblAliasInView;
        @Override public String getName() {return fldName;}
        @Override public String getFldViewAliasName() {return this.fldAliasInView;}
        @Override public String getFieldMask() {return this.fldMask;}
        @Override public String getFieldComment() {return this.fldComment;}
        @Override public FldBusinessRules[] getFldBusinessRules() {return this.fldBusinessRules;}
        @Override public EnumIntTableFields getTableField() {return this.fldObj;}
        @Override public String getTblAliasInView() {return this.tblAliasInView;}
    }        

    public enum LotAttachments implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        LOT_NAME("lot_name", LPDatabase.stringNotNull(), null, null, null, null),
        QUALIF_ID("qualif_id", LPDatabase.integer(), null, null, null, null),        
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        FILE_LINK("file_link", LPDatabase.string(), null, null, null, null),
        DB_FILE("db_file", LPDatabase.embeddedFile(), null, null, null, null),
        BRIEF_SUMMARY("brief_summary", LPDatabase.string(), null, null, null, null),
        REMOVED("removed", LPDatabase.booleanFld(false), null, null, null, null),
        ;
        private LotAttachments(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
