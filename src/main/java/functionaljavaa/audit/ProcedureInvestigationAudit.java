/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import databases.Rdbms;
import databases.TblsApp;
import databases.TblsProcedureAudit;
import databases.Token;
import functionaljavaa.requirement.Requirement;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPSession;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ProcedureInvestigationAudit {
    
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



    public static Object[] investigationAuditAdd(String action, String tableName, Integer investigationId, String tableId,
                        Object[] auditlog, Integer parentAuditId, String note) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
//if (1==1) return new Object[]{LPPlatform.LAB_FALSE};

        String[] fieldNames = new String[]{TblsProcedureAudit.Investigation.FLD_DATE.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};
        if (procInstanceName!=null){
            Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
            if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FLD_PROCEDURE.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FLD_PROCEDURE_VERSION.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
            }        
        }
        if (note!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FLD_NOTE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, note);
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FLD_ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FLD_INVESTIGATION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, investigationId);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FLD_TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FLD_TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FLD_FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FLD_USER_ROLE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FLD_PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        if (token.getAppSessionId()!=null){
            Object[] appSession = LPSession.addAppSession( Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.FLD_DATE_STARTED.getName()});
       
    //        Object[] appSession = labSession.getAppSession(appSessionId, new String[]{"date_started"});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())){
                return appSession;
            }else{

                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FLD_APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));            
            }
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FLD_TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FLD_PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }    
        AuditAndUserValidation auditAndUsrValid=AuditAndUserValidation.getInstanceForActions(null, null, null);
        if (auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.Investigation.FLD_REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }    
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_PROCEDURE_AUDIT), TblsProcedureAudit.Investigation.TBL.getName(), 
                fieldNames, fieldValues);
    }    
    
}

