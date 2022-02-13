/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import databases.Rdbms;
import databases.TblsApp;
import databases.TblsAppAudit;
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
public final class AppIncidentAudit {
    private AppIncidentAudit() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] incidentAuditAdd(String action, String tableName, Integer incidentId,
                        Object[] auditlog, Integer parentAuditId, String note) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
//if (1==1) return new Object[]{LPPlatform.LAB_FALSE};

        String[] fieldNames = new String[]{TblsAppAudit.Incident.DATE.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};
        if (procInstanceName!=null){
            Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
            if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.PROCEDURE.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.PROCEDURE_VERSION.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
            }        
        }
        if (note!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.NOTE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, note);
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.INCIDENT_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, incidentId);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, incidentId);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.USER_ROLE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        if (token.getAppSessionId()!=null){
            Object[] appSession = LPSession.addAppSession( Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.DATE_STARTED.getName()});
       
    //        Object[] appSession = labSession.getAppSession(appSessionId, new String[]{"date_started"});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())){
                return appSession;
            }else{

                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));            
            }
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }    
        AuditAndUserValidation auditAndUsrValid=ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditAndUsrValid();
        if (auditAndUsrValid!=null && auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }    
        Object[] insertRecordInTable = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP_AUDIT.getName(), TblsAppAudit.TablesAppAudit.INCIDENT.getTableName(), 
                fieldNames, fieldValues);
        return insertRecordInTable;
    }    
    
}
