/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitDataAudit;
import databases.Rdbms;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public final class IncubBatchAudit {
    private IncubBatchAudit() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}

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
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        GenericAuditFields gAuditFlds=new GenericAuditFields(null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();
        
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
        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsEnvMonitDataAudit.IncubBatch.FLD_PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }    
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsEnvMonitDataAudit.TablesEnvMonitDataAudit.INCUB_BATCH.getTableName(), 
                fieldNames, fieldValues);
    }    
}
