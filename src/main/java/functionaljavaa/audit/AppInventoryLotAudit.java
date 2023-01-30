/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import module.inventorytrack.definition.TblsInvTrackingDataAudit;
import trazit.enums.EnumIntAuditEvents;

/**
 *
 * @author User
 */
public final class AppInventoryLotAudit {
    private AppInventoryLotAudit() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] InventoryLotAuditAdd(EnumIntAuditEvents action, String lotId, String reference, String category, String tableName, String tableId,
                        String[] fldNames, Object[] fldValues) {
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
        Object[] insertRecordInfo=AuditUtilities.applyTheInsert(gAuditFlds, TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT, fieldNames, fieldValues);
        return insertRecordInfo;
    }    
    
 }
