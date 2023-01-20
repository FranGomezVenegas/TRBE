/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inventorytrack.definition;

import lbplanet.utilities.LPDatabase;
import static lbplanet.utilities.LPDatabase.dateTime;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class TblsInvTrackingProcedure {
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.PROCEDURE.getName();
    public enum TablesEnvMonitProcedure implements EnumIntTables{        
        DEVIATION_INCUBATOR(null, "deviation_incubator", SCHEMA_NAME, true, ProcedureDeviationIncubator.values(), 
            ProcedureDeviationIncubator.ID.getName(), new String[]{ProcedureDeviationIncubator.ID.getName()}, null, "ProcedureDeviationIncubator table"),
        INCUB_TEMP_READING_VIOLATIONS(null, "incubator_temp_reading_violations", SCHEMA_NAME, true, IncubatorTempReadingViolations.values(), IncubatorTempReadingViolations.ID.getName(), 
            new String[]{IncubatorTempReadingViolations.ID.getName()}, null, "IncubatorTempReadingViolations table"),
        ;
        private TablesEnvMonitProcedure(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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

    public enum IncubatorTempReadingViolations implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null), 
        INCUBATOR("incubator", LPDatabase.string(), null, null, null, null), 
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, dateTime(), null, null, null, null), 
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null), 
        STARTED_ON("started_on", dateTime(), null, null, null, null), 
        ENDED_ON("ended_on", dateTime(), null, null, null, null), 
        REASON("reason", LPDatabase.string(), null, null, null, null), 
        STAGE_CURRENT("current_stage", LPDatabase.stringNotNull(), null, null, null, null), 
        STAGE_PREVIOUS("stage_previous", LPDatabase.string(), null, null, null, null), 
        ;
        private IncubatorTempReadingViolations(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    
    /**
     *
     */

    public enum ProcedureDeviationIncubator implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null), 
        STATUS("status", LPDatabase.stringNotNull(), null, null, null, null), 
        STATUS_PREVIOUS("status_previous", LPDatabase.stringNotNull(), null, null, null, null), 
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, dateTime(), null, null, null, null), 
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null), 
        INCUB_NAME("incubator_name", LPDatabase.string(), null, null, null, null), 
        INCUB_NOTEBOOK_ID("notebook_id", LPDatabase.integer(), null, null, null, null), 
        BATCH_NAME("batch_name", LPDatabase.string(), null, null, null, null), 
        REASON("reason", LPDatabase.string(), null, null, null, null), 
        ;
        private ProcedureDeviationIncubator(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
