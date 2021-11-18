/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import databases.Rdbms;
import databases.TblsApp;
import databases.TblsAppProcDataAudit;
import databases.Token;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPSession;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.SessionAuditActions;

/**
 *
 * @author User
 */
public final class AppInstrumentsAudit {
    private AppInstrumentsAudit() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] instrumentsAuditAdd(String action, String instrName, String tableName, String tableId,
                        String[] fldNames, Object[] fldValues) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        SessionAuditActions auditActions = ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditActions();
        if (auditActions.getLastAuditAction()!=null){
            action=auditActions.getLastAuditAction().getActionName()+">"+action;
        }
//if (1==1) return new Object[]{LPPlatform.LAB_FALSE};
        String note="";
        String[] fieldNames = new String[]{TblsAppProcDataAudit.Instruments.FLD_DATE.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};
        if (note!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, note);
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_INSTRUMENT_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, instrName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, LPJson.convertArrayRowToJSONObject(fldNames, fldValues).toJSONString());
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_USER_ROLE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        if (token.getAppSessionId()!=null){
            Object[] appSession = LPSession.addAppSession( Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.FLD_DATE_STARTED.getName()});
       
    //        Object[] appSession = labSession.getAppSession(appSessionId, new String[]{"date_started"});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())){
                return appSession;
            }else{

                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));            
            }
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());      
        
        
        if (auditActions.getLastAuditAction()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditActions.getLastAuditAction().getAuditId());
        }    
        AuditAndUserValidation auditAndUsrValid=ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditAndUsrValid();
        if (auditAndUsrValid!=null && auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }    
        Object[] insertRecordInfo = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP_PROC_DATA_AUDIT.getName(), TblsAppProcDataAudit.Instruments.TBL.getName(), 
                fieldNames, fieldValues);
        auditActions.addAuditAction(Integer.valueOf(insertRecordInfo[insertRecordInfo.length-1].toString()), action);
/*        parentAuditId=ProcedureRequestSession.getInstanceForActions(null, null, null).getParentAuditId();       
        if (parentAuditId==null)
            ProcedureRequestSession.getInstanceForActions(null, null, null).setParentAuditId();
*/
        return insertRecordInfo;
    }    
    
}
