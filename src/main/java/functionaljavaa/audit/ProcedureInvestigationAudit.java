/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import databases.Rdbms;
import databases.TblsProcedureAudit;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public final class ProcedureInvestigationAudit {
    private ProcedureInvestigationAudit() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] investigationAuditAdd(String action, String tableName, Integer investigationId, String tableId,
                        Object[] auditlog, Integer parentAuditId, String note) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        GenericAuditFields gAuditFlds=new GenericAuditFields(auditlog);

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();

        if (note!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.NOTE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, note);
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.INVESTIGATION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, investigationId);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
//        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FIELDS_UPDATED.getName());
//        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));

        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }    
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE_AUDIT.getName()), TblsProcedureAudit.TablesProcedureAudit.INVESTIGATION.getTableName(), 
                fieldNames, fieldValues);
    }    
    
}

