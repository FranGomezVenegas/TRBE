/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inventorytrack.definition;

import functionaljavaa.audit.SampleAudit;
import functionaljavaa.requirement.masterdata.ClassMasterData;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.ApiErrorTraping;
import module.inventorytrack.definition.TblsInvTrackingData.TablesInvTrackingData;
import module.inventorytrack.logic.DataInventory;
import module.inventorytrack.definition.InvTrackingEnums.InventoryTrackAPIactionsEndpoints;
import module.inventorytrack.definition.TblsInvTrackingConfig.TablesInvTrackingConfig;
import static module.inventorytrack.logic.DataInventoryQualif.invTrackingAuditSetAuditRecordAsReviewed;
import static module.inventorytrack.logic.DataInventoryQualif.objectVariableChangeValue;
import static module.inventorytrack.logic.DataInventoryQualif.objectVariableSetValue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.TrazitModules;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

public class ClassInvTracking {

    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    private Object[] diagnostic = new Object[0];
    InternalMessage actionDiagnosesObj = null;
    private Boolean functionFound = false;
    private Boolean isSuccess;

    public ClassInvTracking(HttpServletRequest request, InventoryTrackAPIactionsEndpoints endPoint) {
        this.functionFound = true;

        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        InternalMessage actionDiagnoses = null;
        Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
            LPFrontEnd.servletReturnResponseError(request, null,
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, "en", LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());

        DataInventory invLot = null;
        String lotName = argValues[0].toString();
        String category = argValues[1].toString();
        String reference = argValues[2].toString();
        if (Boolean.FALSE.equals("NEW_INVENTORY_LOT".equalsIgnoreCase(endPoint.getName())) && Boolean.FALSE.equals("CONFIG_ADD_REFERENCE".equalsIgnoreCase(endPoint.getName()))) {
            invLot = new DataInventory(lotName, reference, category, null);
            if (Boolean.TRUE.equals(invLot.getHasError())) {
                this.actionDiagnosesObj = invLot.getErrorDetail();
                this.diagnostic = ApiMessageReturn.trapMessage(invLot.getErrorDetail().getDiagnostic(), invLot.getErrorDetail().getMessageCodeObj(), invLot.getErrorDetail().getMessageCodeVariables());
                this.relatedObj = rObj;
                rObj.killInstance();
                invLot = null;
                return;
            }
        }
        this.functionFound = true;
        switch (endPoint) {
            case NEW_INVENTORY_LOT:
                String expiryDate = argValues[3].toString();
                String expiryDateInUse = argValues[4].toString();
                String retestDate = argValues[5].toString();
                BigDecimal volume = null;
                if (argValues[6] != null && argValues[6].toString().length() > 0) {
                    volume = BigDecimal.valueOf(Double.valueOf(argValues[6].toString()));
                }
                String volumeUom = argValues[7].toString();
                String vendor = argValues[8].toString();
                String vendorLot = argValues[9].toString();
                String vendorReference = argValues[10].toString();
                String purity = argValues[11].toString();
                String conservCondition = argValues[12].toString();
                String fldNamesStr = argValues[13].toString();
                String fldValuesStr = argValues[14].toString();
                Integer numEntries = Integer.valueOf(LPNulls.replaceNull(argValues[15].toString()));//LPPlatform.LAB_FALSE.equalsIgnoreCase(LPMath.isNumeric(LPNulls.replaceNull(argValues[15]).toString())[0].toString())
                //? 1 : Integer.valueOf(LPNulls.replaceNull(argValues[15].toString()));
                String[] fieldNames = null;
                Object[] fieldValues = null;
                if (fldValuesStr != null && fldValuesStr.length() > 0) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fldValuesStr.split("\\|"));
                    fieldNames = fldNamesStr.split("\\|");
                }
                if (fieldValues != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else if (LPNulls.replaceNull(expiryDate).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.EXPIRY_DATE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, LPDate.stringFormatToDate(expiryDate));
                }
                if (LPNulls.replaceNull(expiryDateInUse).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.EXPIRY_DATE_IN_USE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, LPDate.stringFormatToDate(expiryDateInUse));
                }
                if (LPNulls.replaceNull(retestDate).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.RETEST_DATE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, LPDate.stringFormatToDate(retestDate));
                }
                if (LPNulls.replaceNull(vendor).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.VENDOR.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, (vendor));
                }
                if (LPNulls.replaceNull(vendorLot).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.VENDOR_LOT.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, vendorLot);
                }
                if (LPNulls.replaceNull(vendorReference).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.VENDOR_REFERENCE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, (vendorReference));
                }
                if (LPNulls.replaceNull(conservCondition).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.CONSERV_CONDITION.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, conservCondition);
                }
                if (LPNulls.replaceNull(volume).toString().length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.QUANTITY.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, volume);
                }
                if (LPNulls.replaceNull(volumeUom).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.QUANTITY_UOM.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, volumeUom);
                }
                actionDiagnoses = DataInventory.createNewInventoryLot(lotName, reference, category, volume, volumeUom, fieldNames, fieldValues, numEntries, null);
                break;
            case TURN_LOT_AVAILABLE:
                fldNamesStr = argValues[3].toString();
                fldValuesStr = argValues[4].toString();
                fieldNames = null;
                fieldValues = null;
                if (fldValuesStr != null && fldValuesStr.length() > 0) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fldValuesStr.split("\\|"));
                    fieldNames = fldNamesStr.split("\\|");
                }
                if (fieldValues != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else {
                    if (invLot == null) {
                        return;
                    }
                    actionDiagnoses = invLot.turnAvailable(fieldNames, fieldValues);
                }
                break;
            case TURN_LOT_UNAVAILABLE:
                fldNamesStr = argValues[3].toString();
                fldValuesStr = argValues[4].toString();
                fieldNames = null;
                fieldValues = null;
                if (fldValuesStr != null && fldValuesStr.length() > 0) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fldValuesStr.split("\\|"));
                    fieldNames = fldNamesStr.split("\\|");
                }
                if (fieldValues != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else {
                    if (invLot == null) {
                        return;
                    }
                    actionDiagnoses = invLot.turnUnAvailable(fieldNames, fieldValues);
                }
                break;
            case UPDATE_LOT:
                category = argValues[1].toString();
                expiryDate = argValues[2].toString();
                expiryDateInUse = argValues[3].toString();
                retestDate = argValues[4].toString();
                vendorReference = argValues[5].toString();
                purity = argValues[6].toString();
                fieldNames = null;
                fieldValues = null;
                if (vendorReference != null) {
                    fieldNames = vendorReference.split("\\|");
                }
                if (purity != null) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(purity.split("\\|"));
                }
                if (fieldValues != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else if (LPNulls.replaceNull(category).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.CATEGORY.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, category);
                }
                if (LPNulls.replaceNull(expiryDate).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.EXPIRY_DATE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, expiryDate);
                }
                if (LPNulls.replaceNull(expiryDateInUse).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.EXPIRY_DATE_IN_USE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, expiryDateInUse);
                }
                if (LPNulls.replaceNull(retestDate).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.RETEST_DATE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, retestDate);
                }
                if (invLot == null) {
                    return;
                }
                actionDiagnoses = invLot.updateInventoryLot(fieldNames, fieldValues);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), lotName);
                }
                break;
            case RETIRE_LOT:
                fldNamesStr = argValues[3].toString();
                fldValuesStr = argValues[4].toString();
                fieldNames = null;
                fieldValues = null;
                if (fldValuesStr != null && fldValuesStr.length() > 0) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fldValuesStr.split("\\|"));
                    fieldNames = fldNamesStr.split("\\|");
                }
                if (fieldValues != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else {
                    if (invLot == null) {
                        return;
                    }
                    actionDiagnoses = invLot.retireInventoryLot(fieldNames, fieldValues);
                }
                break;
            case UNRETIRE_LOT:
                fldNamesStr = argValues[3].toString();
                fldValuesStr = argValues[4].toString();
                fieldNames = null;
                fieldValues = null;
                if (fldValuesStr != null && fldValuesStr.length() > 0) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fldValuesStr.split("\\|"));
                    fieldNames = fldNamesStr.split("\\|");
                }
                if (fieldValues != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else {
                    if (invLot == null) {
                        return;
                    }
                    actionDiagnoses = invLot.unRetireInventoryLot(fieldNames, fieldValues);
                }
                break;
            case CONSUME_INV_LOT_QUANTITY:
                volume = null;
                if (argValues[3] != null && argValues[3].toString().length() > 0) {
                    volume = BigDecimal.valueOf(Double.valueOf(argValues[3].toString()));
                }
                volumeUom = argValues[4].toString();
                if (invLot == null) {
                    return;
                }
                actionDiagnoses = invLot.consumeInvLotVolume(volume, volumeUom);
                break;
            case ADD_INV_LOT_QUANTITY:
                volume = null;
                if (argValues[3] != null && argValues[3].toString().length() > 0) {
                    volume = BigDecimal.valueOf(Double.valueOf(argValues[3].toString()));
                }
                volumeUom = argValues[4].toString();
                if (invLot == null) {
                    return;
                }
                actionDiagnoses = invLot.addInvLotVolume(volume, volumeUom);
                break;
            case ADJUST_INV_LOT_QUANTITY:
                volume = null;
                if (argValues[3] != null && argValues[3].toString().length() > 0) {
                    volume = BigDecimal.valueOf(Double.valueOf(argValues[3].toString()));
                }
                volumeUom = argValues[4].toString();
                if (invLot == null) {
                    return;
                }
                actionDiagnoses = invLot.adjustInvLotVolume(volume, volumeUom);
                break;
            case COMPLETE_QUALIFICATION:
                category = argValues[1].toString();
                reference = argValues[2].toString();
                String decision = argValues[3].toString();
                String turnAvailable = argValues[4].toString();
                if (invLot == null) {
                    return;
                }
                actionDiagnoses = invLot.completeQualification(decision, category, reference, Boolean.valueOf(turnAvailable));
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), lotName);
                }
                break;
            case REOPEN_QUALIFICATION:
                if (invLot == null) {
                    return;
                }
                actionDiagnoses = invLot.reopenQualification();
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), lotName);
                }
                break;
            case ENTER_EVENT_RESULT:
                category = argValues[1].toString();
                reference = argValues[2].toString();
                Integer lotCertifId = (Integer) argValues[3];
                String variableName = argValues[4].toString();
                String newValue = argValues[5].toString();
                actionDiagnoses = objectVariableSetValue(lotName, category, reference, lotCertifId, variableName, newValue);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), lotName);
                }
                break;

            case REENTER_EVENT_RESULT:
                category = argValues[1].toString();
                reference = argValues[2].toString();
                lotCertifId = (Integer) argValues[3];
                variableName = argValues[4].toString();
                newValue = argValues[5].toString();
                actionDiagnoses = objectVariableChangeValue(lotName, category, reference, lotCertifId, variableName, newValue);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), lotName);
                }
                break;
            case LOTAUDIT_SET_AUDIT_ID_REVIEWED:
                lotName = LPNulls.replaceNull(argValues[0]).toString();
                Integer auditId = Integer.valueOf(LPNulls.replaceNull(argValues[1]).toString());
                Object[][] auditInfo = QueryUtilitiesEnums.getTableData(TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT,
                        EnumIntTableFields.getTableFieldsFromString(TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT, new String[]{TblsInvTrackingDataAudit.Lot.LOT_NAME.getName()}),
                        new String[]{TblsInvTrackingDataAudit.Lot.AUDIT_ID.getName()}, new Object[]{auditId},
                        new String[]{TblsInvTrackingDataAudit.Lot.AUDIT_ID.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditInfo[0][0].toString())) {
                    actionDiagnoses = new InternalMessage(auditInfo[0][0].toString(), SampleAudit.SampleAuditErrorTrapping.AUDIT_RECORD_NOT_FOUND, new Object[]{auditId});
                    lotName = null;
                } else {
                    actionDiagnoses = invTrackingAuditSetAuditRecordAsReviewed(auditId, ProcedureRequestSession.getInstanceForActions(null, null, null).getToken().getPersonName());
                    this.messageDynamicData = new Object[]{auditId, lotName};
                    break;
                }
                break;
            case CONFIG_ADD_REFERENCE: 
                String[] tblFields = new String[]{TblsInvTrackingConfig.Reference.NAME.getName(), TblsInvTrackingConfig.Reference.CATEGORY.getName(), TblsInvTrackingConfig.Reference.LOT_REQUIRES_QUALIF.getName(),
                    TblsInvTrackingConfig.Reference.MIN_STOCK.getName(), TblsInvTrackingConfig.Reference.MIN_STOCK_UOM.getName(), TblsInvTrackingConfig.Reference.ALLOWED_UOMS.getName(),
                    TblsInvTrackingConfig.Reference.MIN_STOCK_TYPE.getName(), TblsInvTrackingConfig.Reference.REQUIRES_AVAILABLES_FOR_USE.getName(), TblsInvTrackingConfig.Reference.MIN_AVAILABLES_FOR_USE.getName(),
                    TblsInvTrackingConfig.Reference.MIN_AVAILABLES_FOR_USE_TYPE.getName(), TblsInvTrackingConfig.Reference.QUALIF_VARIABLES_SET.getName()};

                JSONObject jsonObj = new JSONObject();
                jsonObj.put("parsing_type", "SIMPLE_TABLE");
                jsonObj.put("object_type", TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableName());
                JSONArray jArr = new JSONArray();
                JSONObject jsonValuesObj = new JSONObject();
                for (int i = 0; i < argValues.length; i++) {
                    if (LPNulls.replaceNull(argValues[i]).toString().length() > 0) {
                        jsonValuesObj.put(tblFields[i], argValues[i]);
                    }
                }
                jArr.add(jsonValuesObj);
                jsonObj.put("values", jArr);
                ClassMasterData clss = new ClassMasterData(procReqSession.getProcedureInstance(), TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableName(), jsonObj.toJSONString(), TrazitModules.INVENTORY_TRACKING.toString());
                actionDiagnoses = clss.getDiagnostic();
                this.messageDynamicData = new Object[]{argValues[0].toString()};
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(clss.getDiagnostic().getDiagnostic())) {
                    this.messageDynamicData = clss.getDiagnostic().getMessageCodeVariables();
                }
                break;

            default:
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, null, ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND, null);
                invLot = null;
                return;
        }

        this.actionDiagnosesObj = actionDiagnoses;

        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), lotName);
        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TablesInvTrackingConfig.INV_REFERENCE.getTableName(), reference);
        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TablesInvTrackingConfig.INV_CATEGORY.getTableName(), category);
        if (actionDiagnoses
                != null) {
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                this.diagnostic = ApiMessageReturn.trapMessage(actionDiagnoses.getDiagnostic(), actionDiagnoses.getMessageCodeObj(), actionDiagnoses.getMessageCodeVariables());
            } else {
                this.diagnostic = ApiMessageReturn.trapMessage(actionDiagnoses.getDiagnostic(), endPoint, actionDiagnoses.getMessageCodeVariables());
            }
        }

        this.relatedObj = rObj;
        invLot = null;

        rObj.killInstance();
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
        return this.actionDiagnosesObj;
    }

    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
    }

}
