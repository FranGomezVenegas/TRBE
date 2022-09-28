/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.inventory;

import com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.InspLotRMAPIEndpoints;
import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMData;
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
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public final class DataInventoryRetain {
    private DataInventoryRetain() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] createRetain(String lotName, String materialName, InventoryPlanEntryItem invEntryItem){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String[] fieldName=new String[]{TblsInspLotRMData.InventoryRetain.LOT_NAME.getName(), TblsInspLotRMData.InventoryRetain.MATERIAL_NAME.getName(),
            TblsInspLotRMData.InventoryRetain.AMOUNT.getName(), TblsInspLotRMData.InventoryRetain.AMOUNT_UOM.getName(), TblsInspLotRMData.InventoryRetain.RECEPTION_REQUIRED.getName(),
            TblsInspLotRMData.InventoryRetain.CREATED_BY.getName(), TblsInspLotRMData.InventoryRetain.CREATED_ON.getName()};
        Object[] fieldValue=new Object[]{lotName, materialName, invEntryItem.getQuantity(), invEntryItem.getQuantityUom(),
            invEntryItem.getReceptionRequired(), procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN, fieldName, fieldValue);        
        return insertRecordInTable.getApiMessage();
    }
    

    
    public static Object[] retainReception(String lotName, Integer id){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String[] extraFldName=new String[]{TblsInspLotRMData.InventoryRetain.RECEPTION_BY.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause()};
        Object[] isRetAvailable=isRetainAvailable(lotName, id, extraFldName, new Object[]{""}, new String[]{TblsInspLotRMData.InventoryRetain.RECEPTION_BY.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isRetAvailable[0].toString())) return isRetAvailable;
        Integer recepFieldPosic=LPArray.valuePosicInArray(isRetAvailable, TblsInspLotRMData.InventoryRetain.RECEPTION_BY.getName());
        if (recepFieldPosic==-1) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.RECEPTION_FIELD_NOT_RETRIEVED, new Object[]{TblsInspLotRMData.InventoryRetain.RECEPTION_BY.getName()});
        recepFieldPosic=recepFieldPosic+(isRetAvailable.length/2);
        if (isRetAvailable[recepFieldPosic]!=null && isRetAvailable[recepFieldPosic].toString().length()>0)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.ITEM_ALREADY_RECEIVED, null);
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.RECEPTION_BY.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause(), TblsInspLotRMData.InventoryRetain.LOT_NAME.getName()};
        Object[] fldValue=new Object[]{"", lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.RECEPTION_BY.getName(), TblsInspLotRMData.InventoryRetain.RECEPTION_ON.getName()};
        Object[] updFldValue=new Object[]{procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIEndpoints.LOT_RETAIN_RECEPTION.getAuditActionName());
    }    
    public static Object[] retainMovement(String lotName, Integer id, String newLocation){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isRetAvailable[0].toString())) return isRetAvailable;
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.STORAGE_NAME.getName()};
        Object[] updFldValue=new Object[]{newLocation};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIEndpoints.LOT_RETAIN_MOVEMENT.getAuditActionName());
    }    
    public static Object[] retainMovement(String lotName, Integer id, Integer newLocationId){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isRetAvailable[0].toString())) return isRetAvailable;
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.STORAGE_ID.getName()};
        Object[] updFldValue=new Object[]{newLocationId};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIEndpoints.LOT_RETAIN_MOVEMENT.getAuditActionName());
    }    
    public static Object[] retainUnlock(String lotName, Integer id){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null, null);
        if (! (LPPlatform.LAB_FALSE.equalsIgnoreCase(isRetAvailable[0].toString())) && isRetAvailable[isRetAvailable.length-2].toString().equalsIgnoreCase("retainRowIsLocked"))  return isRetAvailable;
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
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIEndpoints.LOT_RETAIN_UNLOCK.getAuditActionName());
    }
    public static Object[] retainLock(String lotName, Integer id){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isRetAvailable[0].toString())) return isRetAvailable;
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.LOCKED.getName(), TblsInspLotRMData.InventoryRetain.LOCKED_BY.getName(), TblsInspLotRMData.InventoryRetain.LOCKED_ON.getName()};
        Object[] updFldValue=new Object[]{true, procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIEndpoints.LOT_RETAIN_LOCK.getAuditActionName());
    }    
    public static Object[] retainExtract(String lotName, Integer id, BigDecimal q, String qUom){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String[] extraFieldToGet=new String[]{TblsInspLotRMData.InventoryRetain.AMOUNT.getName(), TblsInspLotRMData.InventoryRetain.AMOUNT_UOM.getName(), TblsInspLotRMData.InventoryRetain.UOM_CONVERSION_MODE.getName() };
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null, extraFieldToGet);        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isRetAvailable[0].toString())) return isRetAvailable;
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
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.CONVERSION_NOT_ALLOWED, new Object[]{conversionMode, newAmountUom, qUom,  id.toString(), procReqSession.getProcedureInstance()});            
            //Boolean requiresUnitsConversion = true;
            uom.convertValue(newAmountUom);
            if (!uom.getConvertedFine()) 
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.CONVERTER_FALSE, new Object[]{id.toString(), uom.getConversionErrorDetail()[3].toString(), procReqSession.getProcedureInstance()});
            resultConverted = uom.getConvertedQuantity();
        }        
        newAmount=newAmount.subtract(resultConverted);
        Integer isNegative=newAmount.compareTo(BigDecimal.ZERO);
        if (newAmount.compareTo(BigDecimal.ZERO)<0)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.NOT_ENOUGH_QUANTITY, new Object[]{newAmount, newAmountUom, resultConverted, procReqSession.getProcedureInstance()});
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.AMOUNT.getName(), TblsInspLotRMData.InventoryRetain.AMOUNT_UOM.getName()};
        Object[] updFldValue=new Object[]{newAmount, newAmountUom};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIEndpoints.LOT_RETAIN_EXTRACT.getAuditActionName());
    }    
    private static Object[] updateRetainRecordWithAuditInsert(String lotName, String[] updFldName, Object[] updFldValue, String[] whereFldName, Object[] whereFldValue, String auditActionName){
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
        return updateRecordFieldsByFilter;                
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
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(retainRowsInfo[0][0].toString())) return LPArray.array2dTo1d(retainRowsInfo);
        if (retainRowsInfo.length>1) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.MULTI_ITEMS_NOT_ALLOWED, null);
        String retRowLocked=retainRowsInfo[0][LPArray.valuePosicInArray(fldNameToGet, TblsInspLotRMData.InventoryRetain.LOCKED.getName())].toString();
        if (retRowLocked==null || (retRowLocked.length()>0 && Boolean.valueOf(retRowLocked)))            
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.ITEM_IS_LOCKED, null);
        else
            //return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "retainRowAvailable", null);
            return LPArray.addValueToArray1D(LPArray.addValueToArray1D(new Object[]{}, fldNameToGet), retainRowsInfo[0]);
    }
}
