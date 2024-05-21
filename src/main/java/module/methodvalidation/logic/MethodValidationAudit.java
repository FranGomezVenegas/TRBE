/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.methodvalidation.logic;

import functionaljavaa.audit.AuditUtilities;
import functionaljavaa.audit.GenericAuditFields;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import module.methodvalidation.definition.TblsMethodValidationDataAudit;
import trazit.enums.EnumIntAuditEvents;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public final class MethodValidationAudit {
    private MethodValidationAudit() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] MethodValidationAudit(EnumIntAuditEvents action, String formulaName, String tableName, String tableId,
                        String[] fldNames, Object[] fldValues,String projectName) {
        return MethodValidationAudit(action, formulaName, tableName, tableId,
            fldNames, fldValues, null, projectName);
    }        
    public static Object[] MethodValidationAudit(EnumIntAuditEvents action, String parameterName, String tableName, String tableId,
                        String[] fldNames, Object[] fldValues, String externalProcInstanceName,String projectName) {
        GenericAuditFields gAuditFlds=new GenericAuditFields(action, TblsMethodValidationDataAudit.TablesMethodValidationDataAudit.PARAMETER, fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsMethodValidationDataAudit.Parameter.PARAMETER_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, parameterName);

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsMethodValidationDataAudit.Parameter.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsMethodValidationDataAudit.Parameter.TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);

        if (projectName!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsMethodValidationDataAudit.Parameter.PROJECT_NAME.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, projectName);
        }

        if (externalProcInstanceName!=null){
            ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);  
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsMethodValidationDataAudit.Parameter.EXTERNAL_PROCESS.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procReqSession.getProcedureInstance());            
            return AuditUtilities.applyTheInsert(gAuditFlds, TblsMethodValidationDataAudit.TablesMethodValidationDataAudit.PARAMETER, fieldNames, fieldValues, externalProcInstanceName);            
        }else        
            return AuditUtilities.applyTheInsert(gAuditFlds, TblsMethodValidationDataAudit.TablesMethodValidationDataAudit.PARAMETER, fieldNames, fieldValues);        
    }
    /*
    public static Object[] inventoryLotConfigAuditAdd(EnumIntAuditEvents action, EnumIntTables tableObj, String tableKey, String category,
                        String[] fldNames, Object[] fldValues) {
        GenericAuditFields gAuditFlds=new GenericAuditFields(action, tableObj, fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingConfigAudit.Reference.NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableKey);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingConfigAudit.Reference.CATEGORY.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, category);
        return AuditUtilities.applyTheInsert(gAuditFlds, tableObj, fieldNames, fieldValues);
    }  */  
    
    
 }
