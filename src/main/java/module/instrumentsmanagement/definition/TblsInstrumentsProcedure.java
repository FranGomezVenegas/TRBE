/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.instrumentsmanagement.definition;

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
public class TblsInstrumentsProcedure {
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.PROCEDURE.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesInstrumentsProcedure implements EnumIntTables{        
        INSTRUMENT_CORRECTIVE_ACTION(null, "instrument_corrective_action", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsInstrumentsProcedure.InstrumentsCorrectiveAction.values(), TblsInstrumentsProcedure.InstrumentsCorrectiveAction.ID.getName(),
            new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.ID.getName()}, null, "Instrument Corrective Action for results OOS and/or OOC Info"),
        ;
        private TablesInstrumentsProcedure(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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

    public enum InstrumentsCorrectiveAction implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        STATUS("status", LPDatabase.stringNotNull(), null, null, null, null),
        STATUS_PREVIOUS("status_previous", LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, dateTime(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        INVEST_ID("invest_id", LPDatabase.integer(), null, null, null, null),
        OBJECT_TYPE("object_type", LPDatabase.string(), null, null, null, null),
        INSTRUMENT("instrument", LPDatabase.string(), null, null, null, null),
        EVENT_ID("event_id", LPDatabase.integerNotNull(), null, null, null, null),
        ;
        private InstrumentsCorrectiveAction(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
