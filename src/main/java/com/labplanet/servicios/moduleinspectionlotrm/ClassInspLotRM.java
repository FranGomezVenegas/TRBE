/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleinspectionlotrm;

//import com.labplanet.servicios.moduleenvmonit.*;
import com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.InspLotRMAPIactionsEndpoints;
import static functionaljavaa.inventory.DataInventoryRetain.*;
import functionaljavaa.moduleinspectionlot.DataInspectionLot;
import functionaljavaa.moduleinspectionlot.DataInspectionLotDecision;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntMessages;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ClassInspLotRM {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;

    public ClassInspLotRM(HttpServletRequest request, InspLotRMAPIactionsEndpoints endPoint){
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
    //try () {
        DataInspectionLot insplot = new DataInspectionLot();     
        DataInspectionLotDecision insplotDecision = new DataInspectionLotDecision();   
        
    
        Object[] actionDiagnoses = null;
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            this.functionFound=true;
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
                this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, 
                        (EnumIntMessages)argValues[1] , new Object[]{argValues[2].toString()});
                this.messageDynamicData=new Object[]{argValues[2].toString()};
                return;                        
            }            
        switch (endPoint){
            case NEW_LOT:
                String lotName= argValues[0].toString();
                String materialName= argValues[1].toString();
                String template= argValues[2].toString();
                Integer templateVersion = (Integer) argValues[3];

                String q= argValues[4].toString();
                String qUomStr = argValues[5].toString();
                String nContStr = LPNulls.replaceNull(argValues[6]).toString();

                String fieldName=LPNulls.replaceNull(argValues[7]).toString();
                String fieldValue=LPNulls.replaceNull(argValues[8]).toString();
                String[] fieldNameArr=new String[]{};
                Object[] fieldValueArr=new Object[]{};
                if (fieldName.length()>0){
                    fieldNameArr=fieldName.split("\\|");
                    fieldValueArr=LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                if (q.length()>0){
                    fieldNameArr=LPArray.addValueToArray1D(fieldNameArr, TblsInspLotRMData.Lot.QUANTITY.getName());
                    fieldValueArr=LPArray.addValueToArray1D(fieldValueArr, Integer.valueOf(q));
                }
                if (qUomStr.length()>0){
                    fieldNameArr=LPArray.addValueToArray1D(fieldNameArr, TblsInspLotRMData.Lot.QUANTITY_UOM.getName());
                    fieldValueArr=LPArray.addValueToArray1D(fieldValueArr, qUomStr);
                }
                if (nContStr.length()>0){
                    fieldNameArr=LPArray.addValueToArray1D(fieldNameArr, TblsInspLotRMData.Lot.NUM_CONTAINERS.getName());
                    fieldValueArr=LPArray.addValueToArray1D(fieldValueArr, Integer.valueOf(nContStr));
                }
                Integer numLotsToCreate=1;
                if (fieldValueArr!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValueArr[0].toString())){
                    actionDiagnoses=fieldValueArr;
                    break;
                }
                actionDiagnoses=insplot.createLot(lotName, materialName, template, templateVersion, fieldNameArr, fieldValueArr, numLotsToCreate);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                    actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{lotName, template, templateVersion, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});                                        
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                }
                this.messageDynamicData=new Object[]{};
                break;
        case LOT_TAKE_DECISION:                 
            lotName= argValues[0].toString();
            String decision= argValues[1].toString();
            fieldName=LPNulls.replaceNull(argValues[2]).toString();
            fieldValue=LPNulls.replaceNull(argValues[3]).toString();
            fieldNameArr=new String[]{};
            fieldValueArr=new Object[]{};
            if (fieldName.length()>0){
                fieldNameArr=fieldName.split("\\|");
                fieldValueArr=LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
            }
            if (fieldValueArr!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValueArr[0].toString())){
                actionDiagnoses=fieldValueArr;
                break;
            }            
            actionDiagnoses=insplotDecision.lotTakeDecision(lotName, decision, fieldNameArr, fieldValueArr);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{lotName, decision, fieldNameArr, fieldValueArr, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});                                        
                rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
            }
            this.messageDynamicData=new Object[]{};
            break;
        case LOT_RETAIN_RECEPTION:                 
        case LOT_RETAIN_UNLOCK:
        case LOT_RETAIN_LOCK:
            lotName= argValues[0].toString();
            Integer retainId = (Integer) argValues[1];
            if ("LOT_RETAIN_RECEPTION".equalsIgnoreCase(endPoint.getName()))
                actionDiagnoses=retainReception(lotName, retainId);
            if ("LOT_RETAIN_UNLOCK".equalsIgnoreCase(endPoint.getName()))
                actionDiagnoses=retainUnlock(lotName, retainId);
            if ("LOT_RETAIN_LOCK".equalsIgnoreCase(endPoint.getName()))
                actionDiagnoses=retainLock(lotName, retainId);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{lotName, LPNulls.replaceNull(retainId), ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});                                        
                rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableName(), lotName);
            }
            this.messageDynamicData=new Object[]{};
            break;
        case LOT_RETAIN_MOVEMENT:                 
            lotName= argValues[0].toString();
            retainId = (Integer) argValues[1];
            String newLocation = argValues[2].toString();
            Integer newLocationId = (Integer) argValues[3];
            if (newLocation!=null) actionDiagnoses=retainMovement(lotName, retainId, newLocation);
            if (newLocationId!=null) actionDiagnoses=retainMovement(lotName, retainId, newLocationId);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{lotName, LPNulls.replaceNull(retainId), ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});                                        
                rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableName(), lotName);
            }
            this.messageDynamicData=new Object[]{}; 
            break;
        case LOT_RETAIN_EXTRACT:
            lotName= argValues[0].toString();
            retainId = (Integer) argValues[1];
            BigDecimal quantity = (BigDecimal) argValues[2];
            String quantityUom = argValues[3].toString();
            actionDiagnoses=retainExtract(lotName, retainId, quantity, quantityUom);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{lotName, LPNulls.replaceNull(retainId), ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});                                        
                rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableName(), lotName);
            }
            this.messageDynamicData=new Object[]{}; 
            break;
        default:
            this.functionFound=false;
            break;
        }    
        this.diagnostic=actionDiagnoses;
        this.relatedObj=rObj;
        rObj.killInstance();
/*    catch(Exception e){   
    }finally{
        insplot.killInstance();
        inspLotDecision.killInstance();    */
    }
    
    /**
     * @return the messageDynamicData
     */
    public Object[] getMessageDynamicData() {
        return messageDynamicData;
    }

    /**
     * @return the relatedObj
     */
    public RelatedObjects getRelatedObj() {
        return relatedObj;
    }

    /**
     * @return the endpointExists
     */
    public Boolean getEndpointExists() {
        return endpointExists;
    }

    /**
     * @return the diagnostic
     */
    public Object[] getDiagnostic() {
        return diagnostic;
    }

    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
    }
    
}
