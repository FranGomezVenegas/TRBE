/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.definition;

import functionaljavaa.audit.AuditUtilities;
import functionaljavaa.audit.GenericAuditFields;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMDataAudit;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntMessages;
import trazit.session.ProcedureRequestSession;
/**
 * 
 * @author Fran Gomez
 * @version 0.1
 */
public class LotAudit {
    
    /**
     *
     */
 
    public enum LotAuditErrorTrapping implements EnumIntMessages{ 
        AUDIT_RECORDS_PENDING_REVISION("auditRecordsPendingRevision", "The sample <*1*> has pending sign audit records.", "La muestra <*1*> tiene registros de auditoría sin firmar"),
        AUDIT_RECORD_NOT_FOUND("AuditRecordNotFound", "The audit record <*1*> for sample does not exist", "No encontrado un registro de audit para muestra con id <*1*>"),
        AUDIT_RECORD_ALREADY_REVIEWED("AuditRecordAlreadyReviewed", "The audit record <*1*> was reviewed therefore cannot be reviewed twice.", "El registro de audit para muestra con id <*1*> ya fue revisado, no se puede volver a revisar."),
        AUTHOR_CANNOT_BE_REVIEWER("AuditSamePersonCannotBeAuthorAndReviewer", "Same person cannot review its own actions", "La misma persona no puede revisar sus propias acciones")
        //INCUBATORBATCH_ALREADY_STARTED("IncubatorBatchAlreadyStarted", "The batch <*1*> was already started and cannot be started twice for procedure <*2*>", "La tanda <*1*> no está iniciada todavía para el proceso <*2*>"),
        //INCUBATORBATCH_ALREADY_IN_PROCESS("IncubatorBatchAlreadyInProcess", "The batch <*1*> is already in process for incubator <*2*> and start multiples batches per incubator is not allowed for the procedure <*3*>", "")
        ;
        private LotAuditErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override
        public String getErrorCode(){return this.errorCode;}
        @Override
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }    

/**
 * Add one record in the audit table when altering any of the levels belonging to the sample structure when not linked to any other statement.
 * @param action String - Action being performed
 * @param tableName String - table where the action was performed into the Sample structure
 * @param tableId Integer - Id for the object where the action was performed.
 * @param lotName
 * @param fldNames
 * @param fldValues
 * @return  
 */    
    public Object[] lotAuditAdd(EnumIntAuditEvents action, String tableName, String tableId, 
                        String lotName, String[] fldNames, Object[] fldValues) {
        GenericAuditFields gAuditFlds=new GenericAuditFields(action, TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.LOT, fldNames, fldValues);
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();

        fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        if (lotName!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.LOT_NAME.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, lotName);
        }            
        return AuditUtilities.applyTheInsert(gAuditFlds, TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.LOT, fieldNames, fieldValues);       
    }
}
