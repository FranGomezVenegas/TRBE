/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import databases.Rdbms;
import databases.TblsAppProcDataAudit;
import databases.TblsDataAudit;
import functionaljavaa.parameter.Parameter;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
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
        String procInstanceName="app-proc";
        GenericAuditFields gAuditFlds=new GenericAuditFields(fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();
        
        String fileName="AppInstrumentsAuditEvents";
        SessionAuditActions auditActions = ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditActions();
        for (GlobalVariables.Languages curLang: GlobalVariables.Languages.values()){            
            Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), 
                    TblsDataAudit.TablesDataAudit.SAMPLE.getTableName(), TblsAppProcDataAudit.Instruments.ACTION_PRETTY_EN.getName().replace("en", curLang.getName()));
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())){
                String propValue = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
                    fileName, null, action, curLang.getName(), false);
                if (propValue==null || propValue.length()==0)propValue=action;
                fieldNames = LPArray.addValueToArray1D(fieldNames, 
                        TblsAppProcDataAudit.Instruments.ACTION_PRETTY_EN.getName().replace("en", curLang.getName()));
                fieldValues = LPArray.addValueToArray1D(fieldValues, propValue);            
            }
        }
        String actionPrettyEn=action;
        Integer actionPrettyEnPosic = LPArray.valuePosicInArray(fieldNames, TblsAppProcDataAudit.Instruments.ACTION_PRETTY_EN.getName());
        if (actionPrettyEnPosic>-1)
            actionPrettyEn=LPNulls.replaceNull(fieldValues[actionPrettyEnPosic]).toString();
        String actionPrettyEs=action;
        Integer actionPrettyEsPosic = LPArray.valuePosicInArray(fieldNames, TblsAppProcDataAudit.Instruments.ACTION_PRETTY_ES.getName());
        if (actionPrettyEsPosic>-1)
            actionPrettyEs=LPNulls.replaceNull(fieldValues[actionPrettyEsPosic]).toString();
        if (auditActions!=null && auditActions.getLastAuditAction()!=null){
            action=auditActions.getLastAuditAction().getActionName()+" > "+action;
            actionPrettyEn=auditActions.getLastAuditAction().getActionPrettyEn()+" > "+actionPrettyEn;
            actionPrettyEs=auditActions.getLastAuditAction().getActionPrettyEs()+" > "+actionPrettyEs;
        }
        if (actionPrettyEnPosic==-1){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.ACTION_PRETTY_EN.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, actionPrettyEn);
        }else
            fieldValues[actionPrettyEnPosic]=actionPrettyEn;
        
        if (actionPrettyEsPosic==-1){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.ACTION_PRETTY_ES.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, actionPrettyEs);
        }else
            fieldValues[actionPrettyEsPosic]=actionPrettyEs;

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.INSTRUMENT_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, instrName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
       
        if (auditActions.getMainParentAuditAction()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditActions.getMainParentAuditAction().getAuditId());
        }    
        Object[] insertRecordInfo = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP_PROC_DATA_AUDIT.getName(), TblsAppProcDataAudit.TablesAppProcDataAudit.INSTRUMENTS.getTableName(), 
                fieldNames, fieldValues);
        auditActions.addAuditAction(Integer.valueOf(insertRecordInfo[insertRecordInfo.length-1].toString()), action, actionPrettyEn, actionPrettyEs);
        return insertRecordInfo;
    }    
    
}
