/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.instrumentsmanagement.logic;

import module.instrumentsmanagement.definition.TblsInstrumentsDataAudit;
import functionaljavaa.audit.AuditUtilities;
import functionaljavaa.audit.GenericAuditFields;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import module.instrumentsmanagement.definition.TblsInstrumentsConfigAudit;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntTables;

/**
 *
 * @author User
 */
public final class AppInstrumentsAudit {
    private AppInstrumentsAudit() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] instrumentsConfigAuditAdd(EnumIntAuditEvents action, EnumIntTables tableObj, String tableKey,
                        String[] fldNames, Object[] fldValues) {
        GenericAuditFields gAuditFlds=new GenericAuditFields(action, tableObj, fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsConfigAudit.InstrumentsFamily.NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableKey);
        return AuditUtilities.applyTheInsert(gAuditFlds, tableObj, fieldNames, fieldValues);
    }    

    public static Object[] instrumentsAuditAdd(EnumIntAuditEvents action, String instrName, String tableName, String tableId,
                        String[] fldNames, Object[] fldValues) {
        GenericAuditFields gAuditFlds=new GenericAuditFields(action, TblsInstrumentsDataAudit.TablesInstrumentsDataAudit.INSTRUMENTS, fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsDataAudit.Instruments.INSTRUMENT_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, instrName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsDataAudit.Instruments.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsDataAudit.Instruments.TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        return AuditUtilities.applyTheInsert(gAuditFlds, TblsInstrumentsDataAudit.TablesInstrumentsDataAudit.INSTRUMENTS, fieldNames, fieldValues);
    }    
    
 }
