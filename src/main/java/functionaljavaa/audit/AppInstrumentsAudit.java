/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import databases.Rdbms;
import databases.TblsAppProcDataAudit;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntAuditEvents;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;
import trazit.session.SessionAuditActions;

/**
 *
 * @author User
 */
public final class AppInstrumentsAudit {
    private AppInstrumentsAudit() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] instrumentsAuditAdd(EnumIntAuditEvents action, String instrName, String tableName, String tableId,
                        String[] fldNames, Object[] fldValues) {
        String procInstanceName="app-proc";
        GenericAuditFields gAuditFlds=new GenericAuditFields(action, fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();
        

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.INSTRUMENT_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, instrName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
       
        Object[] insertRecordInfo = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP_PROC_DATA_AUDIT.getName(), TblsAppProcDataAudit.TablesAppProcDataAudit.INSTRUMENTS.getTableName(), 
                fieldNames, fieldValues);
        SessionAuditActions auditActions = ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditActions();
        auditActions.addAuditAction(Integer.valueOf(insertRecordInfo[insertRecordInfo.length-1].toString()), gAuditFlds.getActionName(), gAuditFlds.getActionPrettyNameEn(), gAuditFlds.getActionPrettyNameEs());
        return insertRecordInfo;
    }    
    
}
