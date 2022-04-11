/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import lbplanet.utilities.LPDatabase;
import static lbplanet.utilities.LPDatabase.dateTime;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class TblsAppProcConfig {
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.APP_PROC_CONFIG.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = false;
    public enum TablesAppProcConfig implements EnumIntTables{
        
        INSTRUMENTS_FAMILY(null, "instruments_family", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, InstrumentsFamily.values(), null,
            new String[]{InstrumentsFamily.NAME.getName()}, null, ""),
        VARIABLES(null, "variables", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Variables.values(), null,
            new String[]{Variables.PARAM_NAME.getName()}, null, ""),
        VARIABLES_SET(null, "variables_set", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, VariablesSet.values(), null,
            new String[]{VariablesSet.NAME.getName()}, null, ""),
        ;
        private TablesAppProcConfig(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    
    public enum InstrumentsFamily implements EnumIntTableFields{
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        CALIB_REQUIRED("calibration_required", LPDatabase.booleanFld(), null, null, null, null),
        CALIB_INTERVAL("calibration_interval", LPDatabase.string(), null, null, null, null),
        CALIB_TURN_OFF_WHEN_STARTED("calibration_turn_off_when_started", LPDatabase.booleanFld(), null, null, null, null),
        CALIB_TURN_ON_WHEN_COMPLETED("calibration_turn_on_when_completed", LPDatabase.booleanFld(), null, null, null, null),
        CALIB_VARIABLES_SET("calib_variables_set", LPDatabase.string(), null, null, null, null),
        PM_REQUIRED("pm_required", LPDatabase.booleanFld(), null, null, null, null),
        PM_INTERVAL("pm_interval", LPDatabase.string(), null, null, null, null),
        PM_TURN_OFF_WHEN_STARTED("pm_turn_off_when_started", LPDatabase.booleanFld(), null, null, null, null),
        PM_TURN_ON_WHEN_COMPLETED("pm_turn_on_when_completed", LPDatabase.booleanFld(), null, null, null, null),
        PM_VARIABLES_SET("pm_variables_set", LPDatabase.string(), null, null, null, null),
        VERIF_SAME_DAY_REQUIRED("verif_same_day_required", LPDatabase.booleanFld(), null, null, null, null),
        VERIF_SAME_DAY_VARIABLES_SET("verif_same_day_variables_set", LPDatabase.string(), null, null, null, null),
        SERVICE_REQUIRED("service_required", LPDatabase.booleanFld(), null, null, null, null),
        SERVICE_VARIABLES_SET("service_variables_set", LPDatabase.string(), null, null, null, null),
        
        ;
        private InstrumentsFamily(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
