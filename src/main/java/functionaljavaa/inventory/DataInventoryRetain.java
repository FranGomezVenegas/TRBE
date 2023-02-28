/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.inventory;

import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.InspLotRMAPIactionsEndpoints;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.SqlWhere;
import functionaljavaa.audit.LotAudit;
import functionaljavaa.materialspec.InventoryPlanEntryItem;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement;
import java.math.BigDecimal;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public final class DataInventoryRetain {
    private DataInventoryRetain() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static InternalMessage createRetain(String lotName, String materialName, InventoryPlanEntryItem invEntryItem){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String[] fieldName=new String[]{TblsInspLotRMData.InventoryRetain.LOT_NAME.getName(), TblsInspLotRMData.InventoryRetain.MATERIAL_NAME.getName(),
            TblsInspLotRMData.InventoryRetain.AMOUNT.getName(), TblsInspLotRMData.InventoryRetain.AMOUNT_UOM.getName(), TblsInspLotRMData.InventoryRetain.RECEPTION_REQUIRED.getName(),
            TblsInspLotRMData.InventoryRetain.CREATED_BY.getName(), TblsInspLotRMData.InventoryRetain.CREATED_ON.getName()};
        Object[] fieldValue=new Object[]{lotName, materialName, invEntryItem.getQuantity(), invEntryItem.getQuantityUom(),
            invEntryItem.getReceptionRequired(), procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN, fieldName, fieldValue);        
        return new InternalMessage(LPPlatform.LAB_TRUE, 
            InspLotRMEnums.InspLotRMAPIactionsEndpoints.NEW_LOT, new Object[]{lotName}, insertRecordInTable.getNewRowId());
    }
    
    public static InternalMessage retainReception(String lotName, Integer id){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String[] extraFldName=new String[]{TblsInspLotRMData.InventoryRetain.RECEPTION_BY.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause()};
        Object[] isRetAvailable=isRetainAvailable(lotName, id, extraFldName, new Object[]{""}, new String[]{TblsInspLotRMData.InventoryRetain.RECEPTION_BY.getName()});
        InternalMessage retAvDiagn=(InternalMessage) isRetAvailable[0];
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(retAvDiagn.getDiagnostic())) return (InternalMessage)isRetAvailable[0];
        Integer recepFieldPosic=LPArray.valuePosicInArray(isRetAvailable, TblsInspLotRMData.InventoryRetain.RECEPTION_BY.getName());
        if (recepFieldPosic==-1) return new InternalMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.RECEPTION_FIELD_NOT_RETRIEVED, new Object[]{TblsInspLotRMData.InventoryRetain.RECEPTION_BY.getName()});
        recepFieldPosic=recepFieldPosic+(isRetAvailable.length/2);
        if (isRetAvailable[recepFieldPosic]!=null && isRetAvailable[recepFieldPosic].toString().length()>0)
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.ITEM_ALREADY_RECEIVED, null);
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.RECEPTION_BY.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause(), TblsInspLotRMData.InventoryRetain.LOT_NAME.getName()};
        Object[] fldValue=new Object[]{"", lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.RECEPTION_BY.getName(), TblsInspLotRMData.InventoryRetain.RECEPTION_ON.getName()};
        Object[] updFldValue=new Object[]{procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIactionsEndpoints.LOT_RETAIN_RECEPTION.getAuditActionName());
    }    
    public static InternalMessage retainMovement(String lotName, Integer id, String newLocation){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null, null);
        InternalMessage retAvDiagn=(InternalMessage) isRetAvailable[0];
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(retAvDiagn.getDiagnostic())) return (InternalMessage)isRetAvailable[0];
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.STORAGE_NAME.getName()};
        Object[] updFldValue=new Object[]{newLocation};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIactionsEndpoints.LOT_RETAIN_MOVEMENT.getAuditActionName());
    }    
    public static InternalMessage retainMovement(String lotName, Integer id, Integer newLocationId){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null, null);
        InternalMessage retAvDiagn=(InternalMessage) isRetAvailable[0];
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(retAvDiagn.getDiagnostic())) return (InternalMessage)isRetAvailable[0];
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.STORAGE_ID.getName()};
        Object[] updFldValue=new Object[]{newLocationId};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIactionsEndpoints.LOT_RETAIN_MOVEMENT.getAuditActionName());
    }    
    public static InternalMessage retainUnlock(String lotName, Integer id){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null, null); 
        InternalMessage retAvDiagn=(InternalMessage) isRetAvailable[0];
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(retAvDiagn.getDiagnostic())) return (InternalMessage)isRetAvailable[0];
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.LOCKED.getName(), TblsInspLotRMData.InventoryRetain.LOCKED_BY.getName(), TblsInspLotRMData.InventoryRetain.LOCKED_ON.getName()};
        Object[] updFldValue=new Object[]{false, procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
//        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.LOCKED.getName(), TblsInspLotRMData.InventoryRetain.LOCKED_BY.getName(), TblsInspLotRMData.InventoryRetain.LOCKED_ON.getName()};
//        Object[] updFldValue=new Object[]{false, "NULL>>>STRING", "NULL>>>DATE"};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIactionsEndpoints.LOT_RETAIN_UNLOCK.getAuditActionName());
    }
    public static InternalMessage retainLock(String lotName, Integer id){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null, null);
        InternalMessage retAvDiagn=(InternalMessage) isRetAvailable[0];
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(retAvDiagn.getDiagnostic())) return (InternalMessage)isRetAvailable[0];
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.LOCKED.getName(), TblsInspLotRMData.InventoryRetain.LOCKED_BY.getName(), TblsInspLotRMData.InventoryRetain.LOCKED_ON.getName()};
        Object[] updFldValue=new Object[]{true, procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIactionsEndpoints.LOT_RETAIN_LOCK.getAuditActionName());
    }    
    public static InternalMessage retainExtract(String lotName, Integer id, BigDecimal q, String qUom){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String[] extraFieldToGet=new String[]{TblsInspLotRMData.InventoryRetain.AMOUNT.getName(), TblsInspLotRMData.InventoryRetain.AMOUNT_UOM.getName(), TblsInspLotRMData.InventoryRetain.UOM_CONVERSION_MODE.getName() };
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null, extraFieldToGet);        
        InternalMessage retAvDiagn=(InternalMessage) isRetAvailable[0];
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(retAvDiagn.getDiagnostic())) return (InternalMessage)isRetAvailable[0];
        
        BigDecimal newAmount=BigDecimal.valueOf(Double.valueOf(isRetAvailable[(isRetAvailable.length/2)+LPArray.valuePosicInArray(isRetAvailable, TblsInspLotRMData.InventoryRetain.AMOUNT.getName())].toString()));
        String newAmountUom=isRetAvailable[(isRetAvailable.length/2)+LPArray.valuePosicInArray(isRetAvailable, TblsInspLotRMData.InventoryRetain.AMOUNT_UOM.getName())].toString();
        String conversionMode=isRetAvailable[(isRetAvailable.length/2)+LPArray.valuePosicInArray(isRetAvailable, TblsInspLotRMData.InventoryRetain.UOM_CONVERSION_MODE.getName())].toString();
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }else  
            id=Integer.valueOf(isRetAvailable[(isRetAvailable.length/2)+LPArray.valuePosicInArray(isRetAvailable, TblsInspLotRMData.InventoryRetain.ID.getName())].toString());
        UnitsOfMeasurement uom = new UnitsOfMeasurement(new BigDecimal(q.toString()), qUom);
        BigDecimal resultConverted = q;
        if ((newAmountUom.length()>0) && (!newAmountUom.equalsIgnoreCase(qUom)) ) {
            if ((!qUom.equalsIgnoreCase(newAmountUom)) && (conversionMode == null || conversionMode.equalsIgnoreCase("DISABLED") || ((!conversionMode.contains(qUom)) && !conversionMode.equalsIgnoreCase("ALL")))) 
                return new InternalMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.CONVERSION_NOT_ALLOWED, new Object[]{conversionMode, newAmountUom, qUom,  id.toString(), procReqSession.getProcedureInstance()});            
            //Boolean requiresUnitsConversion = true;
            uom.convertValue(newAmountUom);
            if (!uom.getConvertedFine()) 
                return new InternalMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.CONVERTER_FALSE, new Object[]{id.toString(), uom.getConversionErrorDetail()[3].toString(), procReqSession.getProcedureInstance()});
            resultConverted = uom.getConvertedQuantity();
        }        
        newAmount=newAmount.subtract(resultConverted);
        Integer isNegative=newAmount.compareTo(BigDecimal.ZERO);
        if (newAmount.compareTo(BigDecimal.ZERO)<0)
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.NOT_ENOUGH_QUANTITY, new Object[]{newAmount, newAmountUom, resultConverted, procReqSession.getProcedureInstance()});
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.AMOUNT.getName(), TblsInspLotRMData.InventoryRetain.AMOUNT_UOM.getName()};
        Object[] updFldValue=new Object[]{newAmount, newAmountUom};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIactionsEndpoints.LOT_RETAIN_EXTRACT.getAuditActionName());
    }    
    private static InternalMessage updateRetainRecordWithAuditInsert(String lotName, String[] updFldName, Object[] updFldValue, String[] whereFldName, Object[] whereFldValue, String auditActionName){
        SqlWhere sqlWhere = new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN, whereFldName, whereFldValue);
	Object[] updateRecordFieldsByFilter=Rdbms.updateRecordFieldsByFilter(TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN,
		EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN, updFldName), updFldValue, sqlWhere, null);
        Integer recIdPosic = LPArray.valuePosicInArray(whereFldName, TblsInspLotRMData.InventoryRetain.ID.getName());
        String recId=lotName;
        if (recIdPosic>-1) recId=whereFldValue[recIdPosic].toString();
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(updateRecordFieldsByFilter[0].toString())){
            LotAudit lotAudit = new LotAudit();            
            lotAudit.lotAuditAdd(auditActionName, TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableName(), recId, lotName, 
                    LPArray.joinTwo1DArraysInOneOf1DString(updFldName, updFldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
            lotAudit=null;
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_RETAIN_MOVEMENT, null);                
    }
    private static Object[] isRetainAvailable(String lotName, Integer id, String[] extraFldName, Object[] extraFldValue, String[] extraFldNameToGet){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        String[] fldNameToGet=LPArray.addValueToArray1D(fldName, new String[]{TblsInspLotRMData.InventoryRetain.ID.getName(), TblsInspLotRMData.InventoryRetain.LOCKED.getName()});
        if (extraFldNameToGet!=null)fldNameToGet=LPArray.addValueToArray1D(fldNameToGet, extraFldNameToGet);
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }
        Object[][] retainRowsInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableName(), 
                fldName, fldValue, fldNameToGet);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(retainRowsInfo[0][0].toString())) 
            return new Object[]{new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())}, lotName)};
        if (retainRowsInfo.length>1) 
            return new Object[]{new InternalMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.MULTI_ITEMS_NOT_ALLOWED, null)};
        String retRowLocked=retainRowsInfo[0][LPArray.valuePosicInArray(fldNameToGet, TblsInspLotRMData.InventoryRetain.LOCKED.getName())].toString();
        if (retRowLocked==null || (retRowLocked.length()>0 && Boolean.valueOf(retRowLocked)))            
            return new Object[]{new InternalMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.ITEM_IS_LOCKED, null)};
        else
            //return LPArray.addValueToArray1D(LPArray.addValueToArray1D(new Object[]{}, fldNameToGet), retainRowsInfo[0]);
            //return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "retainRowAvailable", null);
            return new Object[]{new InternalMessage(LPPlatform.LAB_TRUE, Rdbms.RdbmsSuccess.RDBMS_TABLE_FOUND, null), 
                new Object[]{fldNameToGet}, retainRowsInfo[0]};
    }
}
