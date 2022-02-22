/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import lbplanet.utilities.LPDatabase;
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
public class TblsProcedureConfig {
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.PROCEDURE_CONFIG.getName();
    public enum TablesProcedureConfig implements EnumIntTables{
        STAGE_TIMING_INTERVAL(null, "stage_timing_interval", SCHEMA_NAME, true, StageTimingInterval.values(), null,
            new String[]{StageTimingInterval.SAMPLE_CONFIG_CODE.getName(), StageTimingInterval.SAMPLE_CONFIG_VERSION.getName(), StageTimingInterval.STAGE.getName()}, null, ""),
        ;
        private TablesProcedureConfig(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    
    
    public static final String SCHEMATAG = "#SCHEMA";
    public static final String TABLETAG = "#TBL";
    public static final String OWNERTAG = "#OWNER";
    public static final String TABLESPACETAG = "#TABLESPACE";
    public static final String FIELDSTAG = "#FLDS";

    public enum StageTimingInterval implements EnumIntTableFields{
        SAMPLE_CONFIG_CODE("sample_config_code", LPDatabase.stringNotNull(), null, null, null, null),
        SAMPLE_CONFIG_VERSION("sample_config_version", LPDatabase.stringNotNull(), null, null, null, null),
        STAGE("stage", LPDatabase.stringNotNull(), null, null, null, null),
        ENABLED("enabled", LPDatabase.booleanFld(), null, null, null, null),
        INTERVAL_SECONDS("interval_seconds", LPDatabase.integer(), null, null, null, null),
        ;
        private StageTimingInterval(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
