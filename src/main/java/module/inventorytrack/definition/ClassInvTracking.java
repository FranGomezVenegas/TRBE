/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inventorytrack.definition;

import databases.TblsAppProcData;
import databases.TblsAppProcData.TablesAppProcData;
import databases.TblsAppProcDataAudit;
import functionaljavaa.audit.SampleAudit;
import static functionaljavaa.instruments.DataInstrumentsEvents.instrumentAuditSetAuditRecordAsReviewed;
import static functionaljavaa.instruments.DataInstrumentsEvents.objectVariableChangeValue;
import static functionaljavaa.instruments.DataInstrumentsEvents.objectVariableSetValue;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.ApiErrorTraping;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import module.inventorytrack.definition.TblsInvTrackingData.TablesInvTrackingData;
import module.inventorytrack.logic.DataInventory;
import module.inventorytrack.logic.InvTrackingEnums.InventoryTrackAPIactionsEndpoints;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ClassInvTracking {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    InternalMessage actionDiagnosesObj=null;
    private Boolean functionFound=false;

    public ClassInvTracking(HttpServletRequest request, InventoryTrackAPIactionsEndpoints endPoint){
        
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = procReqSession.getProcedureInstance();
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        InternalMessage actionDiagnoses = null;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
        //procReqSession.killIt();
            String language = ProcedureRequestSession.getInstanceForActions(null, null, null).getLanguage();
            this.diagnostic=(Object[]) argValues[1];
            this.messageDynamicData=new Object[]{argValues[2].toString()};
            return; 
        }
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
            //this.diagnostic=argValues;
            this.diagnostic=ApiMessageReturn.trapMessage(argValues[0].toString(), argValues[1].toString(), new Object[]{argValues[2].toString()});
            this.relatedObj=rObj;
            rObj.killInstance();
            return;
        }
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
            //procReqSession.killIt();
            String language=procReqSession.getLanguage();
            this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, 
                    argValues[1].toString(), new Object[]{argValues[2].toString()});
            this.messageDynamicData=new Object[]{argValues[2].toString()};
            return;                        
        }        
        DataInventory invLot=null;
        String lotName=argValues[0].toString();
        String reference=argValues[1].toString();
        String category=argValues[2].toString();
        if (!"NEW_INVENTORY_LOT".equalsIgnoreCase(endPoint.getName())){
            invLot=new DataInventory(lotName, reference, category);
            if (invLot.getHasError()){
                this.actionDiagnosesObj=invLot.getErrorDetail();
                this.diagnostic=ApiMessageReturn.trapMessage(invLot.getErrorDetail().getDiagnostic(),invLot.getErrorDetail().getMessageCodeObj(), invLot.getErrorDetail().getMessageCodeVariables());
                this.relatedObj=rObj;
                rObj.killInstance();
                return;
            }
        }
        this.functionFound=true;
            switch (endPoint){
                case NEW_INVENTORY_LOT:
                    String expiryDate=argValues[3].toString();
                    String expiryDateInUse=argValues[4].toString();
                    String retestDate=argValues[5].toString();
                    String vendor=argValues[6].toString();
                    String vendorLot=argValues[7].toString();
                    String vendorReference=argValues[8].toString();
                    String purity=argValues[9].toString();
                    String conservCondition=argValues[10].toString();
                    BigDecimal volume=(BigDecimal) argValues[11];
                    String volumeUom=argValues[12].toString();
                    String fldNamesStr=argValues[13].toString();
                    String fldValuesStr=argValues[14].toString();
                    String[] fieldNames=null;
                    Object[] fieldValues=null;
                    if (vendorReference!=null&&vendorReference.length()>0) fieldNames = vendorReference.split("\\|");
                    if (fldValuesStr!=null&&fldValuesStr.length()>0){
                        fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fldValuesStr.split("\\|"));
                        fieldNames=fldNamesStr.split("\\|");
                    }
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        if (LPNulls.replaceNull(expiryDate).length()>0){
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.EXPIRY_DATE.getName());
                            fieldValues=LPArray.addValueToArray1D(fieldValues, LPDate.stringFormatToDate(expiryDate));
                        }
                        if (LPNulls.replaceNull(expiryDateInUse).length()>0){
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.EXPIRY_DATE_IN_USE.getName());
                            fieldValues=LPArray.addValueToArray1D(fieldValues, LPDate.stringFormatToDate(expiryDateInUse));
                        }
                        if (LPNulls.replaceNull(retestDate).length()>0){
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.RETEST_DATE.getName());
                            fieldValues=LPArray.addValueToArray1D(fieldValues, LPDate.stringFormatToDate(retestDate));
                        }
                        if (LPNulls.replaceNull(vendor).length()>0){
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.VENDOR.getName());
                            fieldValues=LPArray.addValueToArray1D(fieldValues, (vendor));
                        }
                        if (LPNulls.replaceNull(vendorLot).length()>0){
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.VENDOR_LOT.getName());
                            fieldValues=LPArray.addValueToArray1D(fieldValues, vendorLot);
                        }
                        if (LPNulls.replaceNull(vendorReference).length()>0){
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.VENDOR_REFERENCE.getName());
                            fieldValues=LPArray.addValueToArray1D(fieldValues, (vendorReference));
                        }
                        if (LPNulls.replaceNull(conservCondition).length()>0){
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.CONSERV_CONDITION.getName());
                            fieldValues=LPArray.addValueToArray1D(fieldValues, conservCondition);
                        }     
                        if (LPNulls.replaceNull(volume).toString().length()>0){
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.VOLUME.getName());
                            fieldValues=LPArray.addValueToArray1D(fieldValues, volume);
                        }     
                        if (LPNulls.replaceNull(volumeUom).length()>0){
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsInvTrackingData.Lot.VOLUME_UOM.getName());
                            fieldValues=LPArray.addValueToArray1D(fieldValues, volumeUom);
                        }     
                        
                    actionDiagnoses=DataInventory.createNewInventoryLot(lotName, reference, category, volume, volumeUom, fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), lotName);                
                    break;
                case TURN_LOT_AVAILABLE:
                    fldNamesStr=argValues[3].toString();
                    fldValuesStr=argValues[4].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fldValuesStr!=null&&fldValuesStr.length()>0){
                        fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fldValuesStr.split("\\|"));
                        fieldNames=fldNamesStr.split("\\|");
                    }
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else{
                        actionDiagnoses=DataInventory.turnAvailable(lotName, reference, category, fieldNames, fieldValues);
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), lotName);                
                    }
                    break;
                case TURN_LOT_UNAVAILABLE:
                    fldNamesStr=argValues[3].toString();
                    fldValuesStr=argValues[4].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fldValuesStr!=null&&fldValuesStr.length()>0){
                        fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fldValuesStr.split("\\|"));
                        fieldNames=fldNamesStr.split("\\|");
                    }
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else{
                        actionDiagnoses=DataInventory.turnUnAvailable(lotName, reference, category, fieldNames, fieldValues);
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), lotName);                
                    }
                    break;
                case CHANGE_INSTRUMENT_FAMILY:
                    //this.actionDiagnosesObj=instr.getErrorDetail();
                    diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
                    this.relatedObj=rObj;
                    rObj.killInstance();
                    return;                    
                case UPDATE_INSTRUMENT:
                    category=argValues[1].toString();
                    expiryDate=argValues[2].toString();
                    expiryDateInUse=argValues[3].toString();
                    retestDate=argValues[4].toString();
                    vendorReference=argValues[5].toString();
                    purity=argValues[6].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (vendorReference!=null) fieldNames = vendorReference.split("\\|");
                    if (purity!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(purity.split("\\|"));
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        if (LPNulls.replaceNull(category).length()>0){
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsAppProcData.Instruments.MODEL_NUMBER.getName());
                            fieldValues=LPArray.addValueToArray1D(fieldValues, category);
                        }
                        if (LPNulls.replaceNull(expiryDate).length()>0){
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsAppProcData.Instruments.SERIAL_NUMBER.getName());
                            fieldValues=LPArray.addValueToArray1D(fieldValues, expiryDate);
                        }
                        if (LPNulls.replaceNull(expiryDateInUse).length()>0){
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsAppProcData.Instruments.SUPPLIER.getName());
                            fieldValues=LPArray.addValueToArray1D(fieldValues, expiryDateInUse);
                        }
                        if (LPNulls.replaceNull(retestDate).length()>0){
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsAppProcData.Instruments.MANUFACTURER.getName());
                            fieldValues=LPArray.addValueToArray1D(fieldValues, retestDate);
                        }
                        actionDiagnoses=invLot.updateInstrument(fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                    break;
                case DECOMMISSION_INSTRUMENT:
                    vendorReference=argValues[1].toString();
                    purity=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (purity!=null && purity.length()>0){
                        if (vendorReference!=null) fieldNames = vendorReference.split("\\|");
                        fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(purity.split("\\|"));
                    }
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        actionDiagnoses=invLot.decommissionInstrument(fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                    break;
                case UNDECOMMISSION_INSTRUMENT:
                    vendorReference=argValues[1].toString();
                    purity=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (purity!=null && purity.length()>0){
                        if (vendorReference!=null) fieldNames = vendorReference.split("\\|");
                        fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(purity.split("\\|"));
                    }
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        actionDiagnoses=invLot.unDecommissionInstrument(fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                    break;
                case TURN_ON_LINE:
                    vendorReference=argValues[1].toString();
                    purity=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (purity!=null && purity.length()>0){
                        if (vendorReference!=null) fieldNames = vendorReference.split("\\|");
                        fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(purity.split("\\|"));
                    }
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        actionDiagnoses=invLot.turnOnLine(fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                    break;
                case TURN_OFF_LINE:
                    vendorReference=argValues[1].toString();
                    purity=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (purity!=null && purity.length()>0){
                        if (vendorReference!=null) fieldNames = vendorReference.split("\\|");
                        fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(purity.split("\\|"));
                    }
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        //invLot=new DataInventory(lotName);
                        actionDiagnoses=invLot.turnOffLine(fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                    break;
                case START_CALIBRATION:
                    //invLot=new DataInventory(lotName);
                    actionDiagnoses=invLot.startCalibration();
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())){
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), actionDiagnoses.getNewObjectId());                
                    }
                    break;
                case COMPLETE_CALIBRATION:
                    //invLot=new DataInventory(lotName);
                    String decision=argValues[3].toString();
                    actionDiagnoses=invLot.completeCalibration(decision);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())){
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);
                    }
                    break;
                case START_PREVENTIVE_MAINTENANCE:
                    //invLot=new DataInventory(lotName);
                    actionDiagnoses=invLot.startPrevMaint();
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())){
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), actionDiagnoses.getNewObjectId());                
                    }
                    break;
                case COMPLETE_PREVENTIVE_MAINTENANCE:
                    //invLot=new DataInventory(lotName);
                    decision=argValues[3].toString();
                    actionDiagnoses=invLot.completePrevMaint(decision);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())){
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                    }
                    break;
                case START_VERIFICATION:
                    //invLot=new DataInventory(lotName);
                    actionDiagnoses=invLot.startVerification();
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())){
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), actionDiagnoses.getNewObjectId());                
                    }
                    break;
                case COMPLETE_VERIFICATION:
                    //invLot=new DataInventory(lotName);
                    decision=argValues[3].toString();
                    actionDiagnoses=invLot.completeVerification(decision);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                    break;
                case START_SERVICE:
                    //invLot=new DataInventory(lotName);
                    actionDiagnoses=invLot.startSevice();
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())){
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), actionDiagnoses.getNewObjectId());                
                    }
                    break;
                case COMPLETE_SERVICE:
                    //invLot=new DataInventory(lotName);
                    decision=argValues[3].toString();
                    actionDiagnoses=invLot.completeService(decision);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                    break;
                case REOPEN_EVENT:
                    //invLot=new DataInventory(lotName);
                    Integer instrEventId=(Integer)argValues[1];
                    actionDiagnoses=invLot.reopenEvent(instrEventId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                    break;                    
                case ENTER_EVENT_RESULT:
                    instrEventId=(Integer)argValues[1];
                    String variableName=argValues[2].toString();
                    String newValue=argValues[3].toString();
                    //instr=new DataInstruments(instrName);
                    //actionDiagnoses=instr.startCalibration();
                    actionDiagnoses=objectVariableSetValue(lotName, instrEventId, variableName, newValue);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                    break;
                case REENTER_EVENT_RESULT:
                    instrEventId=(Integer)argValues[1];
                    variableName=argValues[2].toString();
                    newValue=argValues[3].toString();
                    //instr=new DataInstruments(instrName);
                    //actionDiagnoses=instr.startCalibration();
                    actionDiagnoses=objectVariableChangeValue(lotName, instrEventId, variableName, newValue);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENTS.getTableName(), lotName);                
                    break;
                case INSTRUMENTAUDIT_SET_AUDIT_ID_REVIEWED:
//                    ResponseMessages message=ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
//                    message.addMainForError(TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
//                    actionDiagnoses=new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null,null);
//if (1==2){
                    String instrumentName=LPNulls.replaceNull(argValues[0]).toString();
                    Integer auditId = Integer.valueOf(LPNulls.replaceNull(argValues[1]).toString());
                    Object[][] auditInfo=QueryUtilitiesEnums.getTableData(TblsAppProcDataAudit.TablesAppProcDataAudit.INSTRUMENTS,
                        EnumIntTableFields.getTableFieldsFromString(TblsAppProcDataAudit.TablesAppProcDataAudit.INSTRUMENTS, new String[]{TblsAppProcDataAudit.Instruments.INSTRUMENT_NAME.getName()}),
                        new String[]{TblsAppProcDataAudit.Instruments.AUDIT_ID.getName()}, new Object[]{auditId}, 
                        new String[]{TblsAppProcDataAudit.Instruments.AUDIT_ID.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditInfo[0][0].toString())){
                        actionDiagnoses=new InternalMessage(auditInfo[0][0].toString(), SampleAudit.SampleAuditErrorTrapping.AUDIT_RECORD_NOT_FOUND, new Object[]{auditId});
                        instrumentName=null;
                    }else{
                        actionDiagnoses=instrumentAuditSetAuditRecordAsReviewed(auditId, ProcedureRequestSession.getInstanceForActions(null, null, null).getToken().getPersonName());
//                    }
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsAppProcDataAudit.TablesAppProcDataAudit.INSTRUMENTS.getTableName(), auditId);
                    this.messageDynamicData=new Object[]{auditId, instrumentName};
                    break;
                    }
                    break;
                default:
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, null, ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND, null);   
                    return;
            }     
        this.actionDiagnosesObj=actionDiagnoses;
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))
            this.diagnostic=ApiMessageReturn.trapMessage(actionDiagnoses.getDiagnostic(),actionDiagnoses.getMessageCodeObj(), actionDiagnoses.getMessageCodeVariables());
        else            
            this.diagnostic=ApiMessageReturn.trapMessage(actionDiagnoses.getDiagnostic(),endPoint, actionDiagnoses.getMessageCodeVariables());
        this.relatedObj=rObj;
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
