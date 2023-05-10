/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.definition;

import functionaljavaa.analysis.ConfigAnalysisStructure;
import static functionaljavaa.inventory.DataInventoryRetain.*;
import module.inspectionlot.rawmaterial.logic.DataInspectionLot;
import module.inspectionlot.rawmaterial.logic.DataInspectionLotDecision;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.logic.DataBulk;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ClassInspLotRMactions {

    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    private Object[] diagnostic = null;
    InternalMessage actionDiagnosesObj = null;
    private Boolean functionFound = false;

    public ClassInspLotRMactions(HttpServletRequest request, InspLotRMEnums.InspLotRMAPIactionsEndpoints endPoint) {
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        //try () {
        DataInspectionLot insplot = new DataInspectionLot();
        DataInspectionLotDecision insplotDecision = new DataInspectionLotDecision();

        this.actionDiagnosesObj = null;//actionDiagnoses;

        InternalMessage actionDiagnoses = null;
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        this.functionFound = true;
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
            this.diagnostic = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.messageDynamicData = new Object[]{argValues[2].toString()};
            this.relatedObj = rObj;
            rObj.killInstance();
            return;
        }
        switch (endPoint) {
            case NEW_LOT:
                String lotName = argValues[0].toString();
                String materialName = argValues[1].toString();
                String template = argValues[2].toString();
                Integer templateVersion = (Integer) argValues[3];

                String q = argValues[4].toString();
                String qUomStr = argValues[5].toString();
                String nContStr = LPNulls.replaceNull(argValues[6]).toString();
                String numBulksStr = LPNulls.replaceNull(argValues[7]).toString();

                String fieldName = LPNulls.replaceNull(argValues[8]).toString();
                String fieldValue = LPNulls.replaceNull(argValues[9]).toString();
                String[] fieldNameArr = new String[]{};
                Object[] fieldValueArr = new Object[]{};
                if (fieldName.length() > 0) {
                    fieldNameArr = fieldName.split("\\|");
                    fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                if (q.length() > 0) {
                    fieldNameArr = LPArray.addValueToArray1D(fieldNameArr, TblsInspLotRMData.Lot.QUANTITY.getName());
                    fieldValueArr = LPArray.addValueToArray1D(fieldValueArr, Integer.valueOf(q));
                }
                if (qUomStr.length() > 0) {
                    fieldNameArr = LPArray.addValueToArray1D(fieldNameArr, TblsInspLotRMData.Lot.QUANTITY_UOM.getName());
                    fieldValueArr = LPArray.addValueToArray1D(fieldValueArr, qUomStr);
                }
                if (nContStr.length() > 0) {
                    fieldNameArr = LPArray.addValueToArray1D(fieldNameArr, TblsInspLotRMData.Lot.NUM_CONTAINERS.getName());
                    fieldValueArr = LPArray.addValueToArray1D(fieldValueArr, Integer.valueOf(nContStr));
                }
                Integer numBulks = null;
                if (numBulksStr.length() > 0) {
                    numBulks = Integer.valueOf(numBulksStr);
                }
                Integer numLotsToCreate = 1;
                if (fieldValueArr != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValueArr[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValueArr[1];
                    break;
                }
                actionDiagnoses = insplot.createLot(lotName, numBulks, materialName, template, templateVersion, fieldNameArr, fieldValueArr, numLotsToCreate);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.NEW_LOT, new Object[]{lotName, template, templateVersion, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{lotName};
                break;
            case LOT_ADD_ADHOC_BULKS:
                lotName = argValues[0].toString();
                String numAdhocBulksStr = argValues[1].toString();
                Integer numAdhocBulks = 1;
                if (numAdhocBulksStr.length() > 0) {
                    numAdhocBulks = Integer.valueOf(numAdhocBulksStr);
                }
                actionDiagnoses = DataBulk.addAdhocBulk(lotName, numAdhocBulks);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_BULK_ADJUST_QUANTITY, new Object[]{lotName, numAdhocBulks});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{numAdhocBulks, lotName};
                break;
            case LOT_BULK_ADJUST_QUANTITY:
                lotName = argValues[0].toString();
                Integer bulkId = Integer.valueOf(argValues[1].toString());
                String quantityStr = LPNulls.replaceNull(argValues[2]).toString();
                String quantityUomStr = LPNulls.replaceNull(argValues[3]).toString();
                fieldName = LPNulls.replaceNull(argValues[4]).toString();
                fieldValue = LPNulls.replaceNull(argValues[5]).toString();
                fieldNameArr = new String[]{};
                fieldValueArr = new Object[]{};
                if (fieldName.length() > 0) {
                    fieldNameArr = fieldName.split("\\|");
                    fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                if (fieldValue != null && fieldValue.length() > 0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValueArr[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValueArr[1];
                }
                actionDiagnoses = DataBulk.lotBulkAdjustQuantity(lotName, bulkId, BigDecimal.valueOf(Double.valueOf(quantityStr)), fieldNameArr, fieldValueArr);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_BULK_ADJUST_QUANTITY, new Object[]{lotName, quantityStr, fieldNameArr, fieldValueArr, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{quantityStr, lotName};
                break;
            case LOT_BULK_ADJUST_SAMPLE_QUANTITY:
                lotName = argValues[0].toString();
                bulkId = Integer.valueOf(argValues[1].toString());
                quantityStr = LPNulls.replaceNull(argValues[2]).toString();
                quantityUomStr = LPNulls.replaceNull(argValues[3]).toString();
                fieldName = LPNulls.replaceNull(argValues[4]).toString();
                fieldValue = LPNulls.replaceNull(argValues[5]).toString();
                fieldNameArr = new String[]{};
                fieldValueArr = new Object[]{};
                if (fieldName.length() > 0) {
                    fieldNameArr = fieldName.split("\\|");
                    fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                if (fieldValue != null && fieldValue.length() > 0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValueArr[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValueArr[1];
                }
                actionDiagnoses = DataBulk.lotBulkSampleQuantity(lotName, bulkId, BigDecimal.valueOf(Double.valueOf(quantityStr)), quantityUomStr, fieldNameArr, fieldValueArr);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_BULK_ADJUST_SAMPLE_QUANTITY, new Object[]{lotName, quantityStr, fieldNameArr, fieldValueArr, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{quantityStr, lotName};
                break;
            case LOT_BULK_TAKE_DECISION:
                lotName = argValues[0].toString();
                bulkId = Integer.valueOf(argValues[1].toString());
                String decision = argValues[2].toString();
                fieldName = LPNulls.replaceNull(argValues[3]).toString();
                fieldValue = LPNulls.replaceNull(argValues[4]).toString();
                fieldNameArr = new String[]{};
                fieldValueArr = new Object[]{};
                if (fieldName.length() > 0) {
                    fieldNameArr = fieldName.split("\\|");
                    fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                if (fieldValue != null && fieldValue.length() > 0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValueArr[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValueArr[1];
                }
                actionDiagnoses = DataBulk.lotBulkTakeDecision(lotName, bulkId, decision, fieldNameArr, fieldValueArr);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    DataInspectionLot.lotQuantityReduce(lotName, bulkId, decision, fieldNameArr, fieldValueArr);
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_TAKE_DECISION, new Object[]{lotName, decision, fieldNameArr, fieldValueArr, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                } else {
                    this.actionDiagnosesObj = actionDiagnoses;
                }
                this.messageDynamicData = new Object[]{lotName, decision};
                break;

            case LOT_ALL_BULKS_TAKE_DECISION:
                lotName = argValues[0].toString();
                decision = argValues[1].toString();
                fieldName = LPNulls.replaceNull(argValues[2]).toString();
                fieldValue = LPNulls.replaceNull(argValues[3]).toString();
                fieldNameArr = new String[]{};
                fieldValueArr = new Object[]{};
                if (fieldName.length() > 0) {
                    fieldNameArr = fieldName.split("\\|");
                    fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                if (fieldValue != null && fieldValue.length() > 0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValueArr[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValueArr[1];
                }
                actionDiagnoses = insplotDecision.lotAllBulksTakeDecision(lotName, decision, fieldNameArr, fieldValueArr);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_TAKE_DECISION, new Object[]{lotName, decision, fieldNameArr, fieldValueArr, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{decision, lotName};
                break;
            case LOT_RETAIN_RECEPTION:
            case LOT_RETAIN_UNLOCK:
            case LOT_RETAIN_LOCK:
                lotName = argValues[0].toString();
                Integer retainId = (Integer) argValues[1];
                if ("LOT_RETAIN_RECEPTION".equalsIgnoreCase(endPoint.getName())) {
                    actionDiagnoses = retainReception(lotName, retainId);
                }
                if ("LOT_RETAIN_UNLOCK".equalsIgnoreCase(endPoint.getName())) {
                    actionDiagnoses = retainUnlock(lotName, retainId);
                }
                if ("LOT_RETAIN_LOCK".equalsIgnoreCase(endPoint.getName())) {
                    actionDiagnoses = retainLock(lotName, retainId);
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_RETAIN_LOCK, new Object[]{lotName, LPNulls.replaceNull(retainId), ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{lotName};
                break;
            case LOT_RETAIN_MOVEMENT:
                lotName = argValues[0].toString();
                retainId = (Integer) argValues[1];
                String newLocation = argValues[2].toString();
                Integer newLocationId = (Integer) argValues[3];
                if (newLocation != null) {
                    actionDiagnoses = retainMovement(lotName, retainId, newLocation);
                }
                if (newLocationId != null) {
                    actionDiagnoses = retainMovement(lotName, retainId, newLocationId);
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_RETAIN_MOVEMENT, new Object[]{lotName, LPNulls.replaceNull(retainId), ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{lotName};
                break;
            case LOT_RETAIN_EXTRACT:
                lotName = argValues[0].toString();
                retainId = (Integer) argValues[1];
                BigDecimal quantity = (BigDecimal) argValues[2];
                String quantityUom = argValues[3].toString();
                actionDiagnoses = retainExtract(lotName, retainId, quantity, quantityUom);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_RETAIN_EXTRACT, new Object[]{lotName, LPNulls.replaceNull(retainId), ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{lotName};
                break;
            case LOT_TAKE_DECISION:
                lotName = argValues[0].toString();
                decision = argValues[1].toString();
                fieldName = LPNulls.replaceNull(argValues[2]).toString();
                fieldValue = LPNulls.replaceNull(argValues[3]).toString();
                fieldNameArr = new String[]{};
                fieldValueArr = new Object[]{};
                if (fieldName.length() > 0) {
                    fieldNameArr = fieldName.split("\\|");
                    fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                if (fieldValue != null && fieldValue.length() > 0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValueArr[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValueArr[1];
                }
                actionDiagnoses = insplotDecision.lotTakeDecision(lotName, decision, fieldNameArr, fieldValueArr);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_TAKE_DECISION, new Object[]{lotName, decision, fieldNameArr, fieldValueArr, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{decision, lotName};
                break;
            default:
                this.functionFound = false;
                break;
        }
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
            this.diagnostic = ApiMessageReturn.trapMessage(actionDiagnoses.getDiagnostic(), actionDiagnoses.getMessageCodeObj(), actionDiagnoses.getMessageCodeVariables());
        } else {
            this.diagnostic = ApiMessageReturn.trapMessage(actionDiagnoses.getDiagnostic(), endPoint, actionDiagnoses.getMessageCodeVariables());
        }
        this.relatedObj = rObj;
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

    public InternalMessage getDiagnosticObj() {
        return actionDiagnosesObj;
    }

    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
    }

}
