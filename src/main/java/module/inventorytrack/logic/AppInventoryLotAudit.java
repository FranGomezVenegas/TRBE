/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inventorytrack.logic;

import functionaljavaa.audit.AuditUtilities;
import functionaljavaa.audit.GenericAuditFields;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import module.inventorytrack.definition.TblsInvTrackingDataAudit;
import module.inventorytrack.definition.TblsInvTrackingConfigAudit;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntTables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public final class AppInventoryLotAudit {
    private AppInventoryLotAudit() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] inventoryLotAuditAdd(EnumIntAuditEvents action, String lotId, String reference, String category, String tableName, String tableId,
                        String[] fldNames, Object[] fldValues) {
        return inventoryLotAuditAdd(action, lotId, reference, category, tableName, tableId,
            fldNames, fldValues, null);
    }
        
    public static Object[] inventoryLotAuditAdd(EnumIntAuditEvents action, String lotId, String reference, String category, String tableName, String tableId,
                        String[] fldNames, Object[] fldValues, String externalProcInstanceName) {
        GenericAuditFields gAuditFlds=new GenericAuditFields(action, TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT, fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingDataAudit.Lot.LOT_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, lotId);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingDataAudit.Lot.REFERENCE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, reference);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingDataAudit.Lot.CATEGORY.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, category);

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingDataAudit.Lot.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingDataAudit.Lot.TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        if (externalProcInstanceName!=null){
            ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);  
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingDataAudit.Lot.EXTERNAL_PROCESS.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procReqSession.getProcedureInstance());            
            return AuditUtilities.applyTheInsert(gAuditFlds, TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT, fieldNames, fieldValues, externalProcInstanceName);            
        }else        
            return AuditUtilities.applyTheInsert(gAuditFlds, TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT, fieldNames, fieldValues);
        
    }
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
    }    
    
    
 }
