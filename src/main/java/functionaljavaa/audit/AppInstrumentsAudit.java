/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import databases.Rdbms;
import databases.TblsApp;
import databases.TblsAppProcDataAudit;
import databases.TblsDataAudit;
import databases.Token;
import functionaljavaa.parameter.Parameter;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
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
        String fileName="AppInstrumentsAuditEvents";
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        SessionAuditActions auditActions = ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditActions();
        String procInstanceName="app-proc";
        String[] fieldNames = new String[]{TblsAppProcDataAudit.Instruments.FLD_DATE.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};
        for (GlobalVariables.Languages curLang: GlobalVariables.Languages.values()){            
            Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), 
                    TblsDataAudit.TablesDataAudit.SAMPLE.getTableName(), TblsAppProcDataAudit.Instruments.FLD_ACTION_PRETTY_EN.getName().replace("en", curLang.getName()));
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())){
                String propValue = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
                    fileName, null, action, curLang.getName(), false);
                if (propValue==null || propValue.length()==0)propValue=action;
                fieldNames = LPArray.addValueToArray1D(fieldNames, 
                        TblsAppProcDataAudit.Instruments.FLD_ACTION_PRETTY_EN.getName().replace("en", curLang.getName()));
                fieldValues = LPArray.addValueToArray1D(fieldValues, propValue);            
            }
        }
        String actionPrettyEn=action;
        Integer actionPrettyEnPosic = LPArray.valuePosicInArray(fieldNames, TblsAppProcDataAudit.Instruments.FLD_ACTION_PRETTY_EN.getName());
        if (actionPrettyEnPosic>-1)
            actionPrettyEn=LPNulls.replaceNull(fieldValues[actionPrettyEnPosic]).toString();
        String actionPrettyEs=action;
        Integer actionPrettyEsPosic = LPArray.valuePosicInArray(fieldNames, TblsAppProcDataAudit.Instruments.FLD_ACTION_PRETTY_ES.getName());
        if (actionPrettyEsPosic>-1)
            actionPrettyEs=LPNulls.replaceNull(fieldValues[actionPrettyEsPosic]).toString();
        if (auditActions!=null && auditActions.getLastAuditAction()!=null){
            action=auditActions.getLastAuditAction().getActionName()+" > "+action;
            actionPrettyEn=auditActions.getLastAuditAction().getActionPrettyEn()+" > "+actionPrettyEn;
            actionPrettyEs=auditActions.getLastAuditAction().getActionPrettyEs()+" > "+actionPrettyEs;
        }
        if (actionPrettyEnPosic==-1){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_ACTION_PRETTY_EN.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, actionPrettyEn);
        }else
            fieldValues[actionPrettyEnPosic]=actionPrettyEn;
        
        if (actionPrettyEsPosic==-1){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_ACTION_PRETTY_ES.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, actionPrettyEs);
        }else
            fieldValues[actionPrettyEsPosic]=actionPrettyEs;

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);

        String note="";
        if (note!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, note);
        }
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
            Object[] appSession = LPSession.addAppSession( Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.DATE_STARTED.getName()});
       
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
        
        
        if (auditActions.getMainParentAuditAction()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditActions.getMainParentAuditAction().getAuditId());
        }    
        AuditAndUserValidation auditAndUsrValid=ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditAndUsrValid();
        if (auditAndUsrValid!=null && auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FLD_REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }    
        Object[] insertRecordInfo = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP_PROC_DATA_AUDIT.getName(), TblsAppProcDataAudit.TablesAppProcDataAudit.INSTRUMENTS.getTableName(), 
                fieldNames, fieldValues);
        auditActions.addAuditAction(Integer.valueOf(insertRecordInfo[insertRecordInfo.length-1].toString()), action, actionPrettyEn, actionPrettyEs);
/*        parentAuditId=ProcedureRequestSession.getInstanceForActions(null, null, null).getParentAuditId();       
        if (parentAuditId==null)
            ProcedureRequestSession.getInstanceForActions(null, null, null).setParentAuditId();
*/
        return insertRecordInfo;
    }    
    
}
