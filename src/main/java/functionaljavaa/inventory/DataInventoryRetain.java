/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.inventory;

import com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.InspLotRMAPIEndpoints;
import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMData;
import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import functionaljavaa.audit.LotAudit;
import functionaljavaa.materialspec.InventoryPlanEntryItem;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class DataInventoryRetain {
    
    public static Object[] createRetain(String lotName, String materialName, InventoryPlanEntryItem invEntryItem){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String[] fieldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_LOT_NAME.getName(), TblsInspLotRMData.InventoryRetain.FLD_MATERIAL_NAME.getName(),
            TblsInspLotRMData.InventoryRetain.FLD_AMOUNT.getName(), TblsInspLotRMData.InventoryRetain.FLD_AMOUNT_UOM.getName(), TblsInspLotRMData.InventoryRetain.FLD_RECEPTION_REQUIRED.getName(),
            TblsInspLotRMData.InventoryRetain.FLD_CREATED_BY.getName(), TblsInspLotRMData.InventoryRetain.FLD_CREATED_ON.getName()};
        Object[] fieldValue=new Object[]{lotName, materialName, invEntryItem.getQuantity(), invEntryItem.getQuantityUom(),
            invEntryItem.getReceptionRequired(), procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        Object[] newInvRec=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.InventoryRetain.TBL.getName(), 
            fieldName, fieldValue);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newInvRec[0].toString())) return newInvRec;
        return newInvRec;
    }
    
    public static Object[] retainReception(String lotName, Integer id){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String[] extraFldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_RECEPTION_BY.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause()};
        Object[] isRetAvailable=isRetainAvailable(lotName, id, extraFldName, new Object[]{""}, new String[]{TblsInspLotRMData.InventoryRetain.FLD_RECEPTION_BY.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isRetAvailable[0].toString())) return isRetAvailable;
        Integer recepFieldPosic=LPArray.valuePosicInArray(isRetAvailable, TblsInspLotRMData.InventoryRetain.FLD_RECEPTION_BY.getName());
        if (recepFieldPosic==-1) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "errorInCode, receptionFieldNotRetrieved", null);
        recepFieldPosic=recepFieldPosic+(isRetAvailable.length/2);
        if (isRetAvailable[recepFieldPosic]!=null && isRetAvailable[recepFieldPosic].toString().length()>0)
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "retainRowAlreadyReceived", null);
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_RECEPTION_BY.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause(), TblsInspLotRMData.InventoryRetain.FLD_LOT_NAME.getName()};
        Object[] fldValue=new Object[]{"", lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.FLD_ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_RECEPTION_BY.getName(), TblsInspLotRMData.InventoryRetain.FLD_RECEPTION_ON.getName()};
        Object[] updFldValue=new Object[]{procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIEndpoints.LOT_RETAIN_RECEPTION.getAuditActionName());
    }    
    public static Object[] retainMovement(String lotName, Integer id, String newLocation){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isRetAvailable[0].toString())) return isRetAvailable;
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.FLD_ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_STORAGE_NAME.getName()};
        Object[] updFldValue=new Object[]{newLocation};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIEndpoints.LOT_RETAIN_MOVEMENT.getAuditActionName());
    }    
    public static Object[] retainMovement(String lotName, Integer id, Integer newLocationId){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isRetAvailable[0].toString())) return isRetAvailable;
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.FLD_ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_STORAGE_ID.getName()};
        Object[] updFldValue=new Object[]{newLocationId};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIEndpoints.LOT_RETAIN_MOVEMENT.getAuditActionName());
    }    
    public static Object[] retainUnlock(String lotName, Integer id){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null, null);
        if (! (LPPlatform.LAB_FALSE.equalsIgnoreCase(isRetAvailable[0].toString())) && isRetAvailable[isRetAvailable.length-2].toString().equalsIgnoreCase("retainRowIsLocked"))  return isRetAvailable;
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.FLD_ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_LOCKED.getName(), TblsInspLotRMData.InventoryRetain.FLD_LOCKED_BY.getName(), TblsInspLotRMData.InventoryRetain.FLD_LOCKED_ON.getName()};
        Object[] updFldValue=new Object[]{false, "NULL>>>STRING", "NULL>>>DATE"};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIEndpoints.LOT_RETAIN_UNLOCK.getAuditActionName());
    }
    public static Object[] retainLock(String lotName, Integer id){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isRetAvailable[0].toString())) return isRetAvailable;
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.FLD_ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_LOCKED.getName(), TblsInspLotRMData.InventoryRetain.FLD_LOCKED_BY.getName(), TblsInspLotRMData.InventoryRetain.FLD_LOCKED_ON.getName()};
        Object[] updFldValue=new Object[]{true, procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        return updateRetainRecordWithAuditInsert(lotName, updFldName, updFldValue, fldName, fldValue, InspLotRMAPIEndpoints.LOT_RETAIN_LOCK.getAuditActionName());
    }    
    private static Object[] updateRetainRecordWithAuditInsert(String lotName, String[] updFldName, Object[] updFldValue, String[] whereFldName, Object[] whereFldValue, String auditActionName){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.InventoryRetain.TBL.getName(),
                updFldName, updFldValue, whereFldName, whereFldValue);        
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(updateRecordFieldsByFilter[0].toString())){
            LotAudit lotAudit = new LotAudit();            
            lotAudit.lotAuditAdd(auditActionName, 
                TblsInspLotRMData.Lot.TBL.getName(), lotName, lotName, LPArray.joinTwo1DArraysInOneOf1DString(updFldName, updFldValue, ":"), null);
            lotAudit=null;
        }
        return updateRecordFieldsByFilter;                
    }
    private static Object[] isRetainAvailable(String lotName, Integer id, String[] extraFldName, Object[] extraFldValue, String[] extraFldNameToGet){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        String[] fldNameToGet=LPArray.addValueToArray1D(fldName, new String[]{TblsInspLotRMData.InventoryRetain.FLD_LOCKED.getName()});
        if (extraFldNameToGet!=null)fldNameToGet=LPArray.addValueToArray1D(fldNameToGet, extraFldNameToGet);
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.FLD_ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }
        Object[][] retainRowsInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.InventoryRetain.TBL.getName(), 
                fldName, fldValue, fldNameToGet);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(retainRowsInfo[0][0].toString())) return LPArray.array2dTo1d(retainRowsInfo);
        if (retainRowsInfo.length>1) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "There are more than one row pending of reception and this action should be performed one by one", null);
        String retRowLocked=retainRowsInfo[0][LPArray.valuePosicInArray(fldNameToGet, TblsInspLotRMData.InventoryRetain.FLD_LOCKED.getName())].toString();
        if (retRowLocked==null || (retRowLocked.length()>0 && Boolean.valueOf(retRowLocked)))            
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "retainRowIsLocked", null);
        else
            //return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "retainRowAvailable", null);
            return LPArray.addValueToArray1D(LPArray.addValueToArray1D(new Object[]{}, fldNameToGet), retainRowsInfo[0]);
    }
}
