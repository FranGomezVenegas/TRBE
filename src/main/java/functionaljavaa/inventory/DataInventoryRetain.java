/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.inventory;

import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMData;
import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
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
        Object[] isRetAvailable=isRetainAvailable(lotName, id, extraFldName, new Object[]{""});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isRetAvailable[0].toString())) return isRetAvailable;
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_RECEPTION_BY.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause(), TblsInspLotRMData.InventoryRetain.FLD_LOT_NAME.getName()};
        Object[] fldValue=new Object[]{"", lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.FLD_ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_RECEPTION_BY.getName(), TblsInspLotRMData.InventoryRetain.FLD_RECEPTION_ON.getName()};
        Object[] updFldValue=new Object[]{procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        return Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.InventoryRetain.TBL.getName(),
                updFldName, updFldValue, fldName, fldValue);
    }    
    public static Object[] retainMovement(String lotName, Integer id, String newLocation){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] isRetAvailable=isRetainAvailable(lotName, id, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isRetAvailable[0].toString())) return isRetAvailable;
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_RECEPTION_BY.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause(), TblsInspLotRMData.InventoryRetain.FLD_LOT_NAME.getName()};
        Object[] fldValue=new Object[]{"", lotName};
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.FLD_ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }        
        String[] updFldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_STORAGE_ID.getName()};
        Object[] updFldValue=new Object[]{newLocation};
        return Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.InventoryRetain.TBL.getName(),
                updFldName, updFldValue, fldName, fldValue);
    }    
    private static Object[] isRetainAvailable(String lotName, Integer id, String[] extraFldName, Object[] extraFldValue){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String[] fldName=new String[]{TblsInspLotRMData.InventoryRetain.FLD_LOT_NAME.getName()};
        Object[] fldValue=new Object[]{lotName};
        String[] fldNameToGet=LPArray.addValueToArray1D(fldName, new String[]{TblsInspLotRMData.InventoryRetain.FLD_LOCKED.getName()});
        if (id!=null){
            fldName=LPArray.addValueToArray1D(fldName, TblsInspLotRMData.InventoryRetain.FLD_ID.getName());
            fldValue=LPArray.addValueToArray1D(fldValue, id);
        }
        Object[][] retainRowsInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.InventoryRetain.TBL.getName(), 
                fldName, fldValue, fldNameToGet);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(retainRowsInfo[0][0].toString())) return LPArray.array2dTo1d(retainRowsInfo);
        if (retainRowsInfo.length>1) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "There are more than one row pending of reception and this action should be performed one by one", null);
        String retRowLocked=retainRowsInfo[0][LPArray.valuePosicInArray(fldNameToGet, TblsInspLotRMData.InventoryRetain.FLD_LOCKED.getName())].toString();
        if (retRowLocked!=null && Boolean.valueOf(retRowLocked))            
            return retainRowsInfo[0];
        else
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "retainRowIsLocked", null);
    }
}
