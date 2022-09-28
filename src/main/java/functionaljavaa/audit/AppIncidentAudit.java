/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import databases.TblsAppAudit;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;

public final class AppIncidentAudit {
    private AppIncidentAudit() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] incidentAuditAdd(String action, String tableName, Integer incidentId,
                        Object[] auditlog, Integer parentAuditId, String note) {

        GenericAuditFields gAuditFlds=new GenericAuditFields(auditlog);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.INCIDENT_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, incidentId);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, incidentId);
        if (note!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.NOTE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, note);
        }
        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }    
        return AuditUtilities.applyTheInsert(gAuditFlds, TblsAppAudit.TablesAppAudit.INCIDENT, fieldNames, fieldValues);
    }    
    
}
