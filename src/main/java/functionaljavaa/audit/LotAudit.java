/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMDataAudit;
import databases.Rdbms;
import databases.TblsApp;
import databases.TblsDataAudit;
import databases.Token;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPSession;
import java.util.Arrays;
import functionaljavaa.requirement.Requirement;
import lbplanet.utilities.LPDate;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 * 
 * @author Fran Gomez
 * @version 0.1
 */
public class LotAudit {
    
    /**
     *
     */
 
    public enum LotAuditErrorTrapping{ 
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
        public String getErrorCode(){return this.errorCode;}
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }    
    public enum LotAuditEvents{
        LOT_CREATED, LOT_DECISION_TAKEN
        
        /*SAMPLE_LOGGED, SAMPLE_RECEIVED, SET_SAMPLING_DATE, SAMPLE_CHANGE_SAMPLING_DATE,
        SAMPLE_RECEPTION_COMMENT_ADD, SAMPLE_RECEPTION_COMMENT_REMOVE, SAMPLE_EVALUATE_STATUS, SAMPLE_TESTINGGROUP_REVIEWED, SAMPLE_TESTINGGROUP_SET_READY_REVISION, SAMPLE_REVIEWED,
        LOG_SAMPLE_ALIQUOT, LOG_SAMPLE_SUBALIQUOT, SAMPLESTAGE_MOVETONEXT, SAMPLESTAGE_MOVETOPREVIOUS,
        UPDATE_LAST_ANALYSIS_USER_METHOD, CHAIN_OF_CUSTODY_STARTED, CHAIN_OF_CUSTODY_COMPLETED, MICROORGANISM_ADDED, 
        SAMPLE_SET_INCUBATION_STARTED, SAMPLE_SET_INCUBATION_ENDED, SAMPLE_CANCELED, SAMPLE_UNCANCELED, SAMPLE_SET_READY_FOR_REVISION,
        BATCH_SAMPLE_ADDED, BATCH_SAMPLE_REMOVED, BATCH_SAMPLE_MOVED_FROM, BATCH_SAMPLE_MOVED_TO
        */}  

    /**
     *
     */
    public enum LotDecisionAuditEvents{ 
        ZZZ
        //SAMPLE_ANALYSIS_REVIEWED, SAMPLE_ANALYSIS_EVALUATE_STATUS, SAMPLE_ANALYSIS_ANALYST_ASSIGNMENT, 
    //    SAMPLE_ANALYSIS_ADDED, SAMPLE_ANALYSIS_CANCELED, SAMPLE_ANALYSIS_UNCANCELED, SAMPLE_ANALYSIS_SET_READY_fOR_REVISION
    }
/**
 * Add one record in the audit table when altering any of the levels belonging to the sample structure when not linked to any other statement.
 * @param action String - Action being performed
 * @param tableName String - table where the action was performed into the Sample structure
 * @param tableId Integer - Id for the object where the action was performed.
 * @param lotName
 * @param auditlog Object[] - All data that should be stored in the audit as part of the action being performed
 * @param parentAuditId paranet audit id when creating a child-record
 * @return  
 */    
    public Object[] lotAuditAdd(String action, String tableName, String tableId, 
                        String lotName, Object[] auditlog, Integer parentAuditId) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] fieldNames = new String[]{TblsDataAudit.Sample.FLD_DATE.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};
        
        Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInspLotRMDataAudit.Lot.FLD_PROCEDURE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
            fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.FLD_PROCEDURE_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
        }        
        
        fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.FLD_ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.FLD_TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.FLD_TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        if (lotName!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.FLD_LOT_NAME.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, lotName);
        }    
        fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.FLD_FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));
        fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.FLD_USER_ROLE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());

        fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.FLD_PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        if (token.getAppSessionId()!=null){
            Object[] appSession = LPSession.addProcessSession( LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.FLD_DATE_STARTED.getName()});
       
    //        Object[] appSession = labSession.getAppSession(appSessionId, new String[]{"date_started"});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())){
                return appSession;
            }else{

                fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.FLD_APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));            
            }
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.FLD_TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.FLD_PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }    
        AuditAndUserValidation auditAndUsrValid=ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditAndUsrValid();
        if (auditAndUsrValid!=null && auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames,  TblsInspLotRMDataAudit.Lot.FLD_REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }    
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()),  TblsInspLotRMDataAudit.Lot.TBL.getName(), 
                fieldNames, fieldValues);
        
    }
}
