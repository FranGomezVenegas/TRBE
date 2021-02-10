/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitDataAudit;
import databases.Rdbms;
import databases.TblsApp;
import databases.Token;
import functionaljavaa.requirement.Requirement;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPSession;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public final class IncubBatchAudit {
    private IncubBatchAudit() {throw new IllegalStateException("Utility class");}

/**
 * Add one record in the audit table when altering any of the levels belonging to the sample structure when not linked to any other statement.
     * @param action String - Action being performed
     * @param tableName String - table where the action was performed into the Sample structure
     * @param batchName given batch
     * @param auditlog audit event log
     * @param parentAuditId when sub-record then the parent audit id
     * @return  
 */    
    public static Object[] incubBatchAuditAdd(String action, String tableName, String batchName, Object[] auditlog, Integer parentAuditId) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
//if (1==1) return new Object[]{LPPlatform.LAB_FALSE};

        String[] fieldNames = new String[]{TblsEnvMonitDataAudit.IncubBatch.FLD_DATE.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};
        
        Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsEnvMonitDataAudit.IncubBatch.FLD_PROCEDURE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsEnvMonitDataAudit.IncubBatch.FLD_PROCEDURE_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
        }        
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsEnvMonitDataAudit.IncubBatch.FLD_ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsEnvMonitDataAudit.IncubBatch.FLD_BATCH_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, batchName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsEnvMonitDataAudit.IncubBatch.FLD_TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsEnvMonitDataAudit.IncubBatch.FLD_TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, batchName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsEnvMonitDataAudit.IncubBatch.FLD_FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsEnvMonitDataAudit.IncubBatch.FLD_USER_ROLE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsEnvMonitDataAudit.IncubBatch.FLD_PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        if (token.getAppSessionId()!=null){
            Object[] appSession = LPSession.addProcessSession( LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.FLD_DATE_STARTED.getName()});
       
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())){
                return appSession;
            }else{

                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsEnvMonitDataAudit.IncubBatch.FLD_APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));            
            }
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsEnvMonitDataAudit.IncubBatch.FLD_TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsEnvMonitDataAudit.IncubBatch.FLD_PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }    
        AuditAndUserValidation auditAndUsrValid=ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditAndUsrValid();
        if (auditAndUsrValid!=null && auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsEnvMonitDataAudit.IncubBatch.FLD_REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }    
        
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsEnvMonitDataAudit.IncubBatch.TBL.getName(), 
                fieldNames, fieldValues);
    }    
}
