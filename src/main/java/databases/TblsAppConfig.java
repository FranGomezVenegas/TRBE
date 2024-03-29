/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import com.labplanet.servicios.app.GlobalAPIsParams;
import lbplanet.utilities.LPDatabase;

import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntTableFields;
import trazit.enums.FldBusinessRules;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class TblsAppConfig {
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.APP_CONFIG.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = false;
    public enum TablesAppConfig implements EnumIntTables{
        
        PERSON(null, "person", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsAppConfig.Person.values(), null,
            new String[]{TblsAppConfig.Person.PERSON_ID.getName()}, null, ""),
        TBL_FLD_ENCRYPT(null, "table_field_encrypt", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsAppConfig.TblFldsEncrypt.values(), null,
            new String[]{TblsAppConfig.TblFldsEncrypt.SCHEMA_NAME.getName(), TblsAppConfig.TblFldsEncrypt.TABLE_NAME.getName(), TblsAppConfig.TblFldsEncrypt.FIELD_NAME.getName()}, null, ""),
        TBL_FLD_DATE_FORMAT(null, "table_field_date_format", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsAppConfig.TblFldsDateFormat.values(), null,
            new String[]{TblsAppConfig.TblFldsDateFormat.SCHEMA_NAME.getName(), TblsAppConfig.TblFldsDateFormat.TABLE_NAME.getName(), TblsAppConfig.TblFldsDateFormat.FIELD_NAME.getName()}, null, ""),
        UOM(null, "units_of_measurement", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, 
                TblsCnfg.UnitsOfMeasurement.values(), null,
            new String[]{TblsCnfg.UnitsOfMeasurement.NAME.getName()}, 
                null, "UnitsOfMeasurement"), 
        ;
        private TablesAppConfig(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    
    public enum Person implements EnumIntTableFields{
        PERSON_ID("person_id", LPDatabase.stringNotNull(), null, null, null, null),
        FIRST_NAME("first_name", LPDatabase.stringNotNull(), null, null, null, null),
        LAST_NAME("last_name", LPDatabase.string(), null, null, null, null),
        BIRTH_DATE("birth_date", LPDatabase.date(), null, null, null, null),
        PHOTO("photo", LPDatabase.string(), null, null, null, null),
        ALIAS("alias", LPDatabase.string(), null, null, null, null),
        SHIFT("shift", LPDatabase.string(), null, null, null, null),
        ;
        private Person(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum TblFldsEncrypt implements EnumIntTableFields{
        SCHEMA_NAME("schema_name", LPDatabase.stringNotNull(), null, null, null, null),
        TABLE_NAME("table_name", LPDatabase.stringNotNull(), null, null, null, null),
        FIELD_NAME(GlobalAPIsParams.LBL_FIELD_NAME, LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null),
        ;
        private TblFldsEncrypt(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    
    public enum TblFldsDateFormat implements EnumIntTableFields{
        SCHEMA_NAME("schema_name", LPDatabase.stringNotNull(), null, null, null, null),
        TABLE_NAME("table_name", LPDatabase.stringNotNull(), null, null, null, null),
        FIELD_NAME(GlobalAPIsParams.LBL_FIELD_NAME, LPDatabase.string(), null, null, null, null),
        MASK("mask", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null),
        ;
        private TblFldsDateFormat(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
