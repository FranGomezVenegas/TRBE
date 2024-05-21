/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.projectrnd.logic;

import functionaljavaa.audit.AuditUtilities;
import functionaljavaa.audit.GenericAuditFields;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import module.formulation.definition.TblsFormulationDataAudit;
import trazit.enums.EnumIntAuditEvents;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public final class AppProjectRnDAudit {
    private AppProjectRnDAudit() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] AppProjectRnDAudit(EnumIntAuditEvents action, String formulaName, String tableName, String tableId,
                        String[] fldNames, Object[] fldValues) {
        return AppProjectRnDAudit(action, formulaName, tableName, tableId,
            fldNames, fldValues, null);
    }        
    public static Object[] AppProjectRnDAudit(EnumIntAuditEvents action, String projectName, String tableName, String tableId,
                        String[] fldNames, Object[] fldValues, String externalProcInstanceName) {
        GenericAuditFields gAuditFlds=new GenericAuditFields(action, TblsFormulationDataAudit.TablesFormulationDataAudit.FORMULA, fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsFormulationDataAudit.Formula.FORMULA_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, projectName);

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsFormulationDataAudit.Formula.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsFormulationDataAudit.Formula.TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        if (externalProcInstanceName!=null){
            ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);  
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsFormulationDataAudit.Formula.EXTERNAL_PROCESS.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procReqSession.getProcedureInstance());            
            return AuditUtilities.applyTheInsert(gAuditFlds, TblsFormulationDataAudit.TablesFormulationDataAudit.FORMULA, fieldNames, fieldValues, externalProcInstanceName);            
        }else        
            return AuditUtilities.applyTheInsert(gAuditFlds, TblsFormulationDataAudit.TablesFormulationDataAudit.FORMULA, fieldNames, fieldValues);        
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
