/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inventorytrack.logic;

import module.inventorytrack.definition.InvTrackingEnums;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.features.Token;
import functionaljavaa.modulegenoma.DataStudyObjectsVariableValues;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.isTagValueOneOfEnableOnes;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import static lbplanet.utilities.LPMath.isNumeric;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.inventorytrack.definition.TblsInvTrackingConfig;
import module.inventorytrack.definition.TblsInvTrackingData;
import module.inventorytrack.definition.TblsInvTrackingData.TablesInvTrackingData;
import module.inventorytrack.definition.InvTrackingEnums.InventoryTrackingErrorTrapping;
import module.inventorytrack.definition.TblsInvTrackingDataAudit;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;
import trazit.session.InternalMessage;
import trazit.session.ResponseMessages;
import static module.inventorytrack.logic.AppInventoryLotAudit.inventoryLotAuditAdd;

/**
 *
 * @author Administrator
 */
public class DataInventoryQualif {
private DataInventoryQualif() {throw new IllegalStateException("Utility class");}
    public static InternalMessage createInventoryLotQualif(String lotName, String category, String reference, Boolean requiresConfigChecks) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = procReqSession.getMessages();
        Token token = procReqSession.getToken();
        String[] fldNames = new String[]{};
        Object[] fldValues = new Object[]{};
        Object[][] referenceInfo = null;
        String[] allFieldNames = getAllFieldNames(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields());
        if (Boolean.TRUE.equals(requiresConfigChecks)
                && (reference != null && reference.length() > 0)) {
            referenceInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableName(),
                    new String[]{TblsInvTrackingConfig.Reference.NAME.getName(), TblsInvTrackingConfig.Reference.CATEGORY.getName()}, new Object[]{reference, category},
                    allFieldNames);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceInfo[0][0].toString())) {
                messages.addMainForError(InvTrackingEnums.InventoryTrackingErrorTrapping.REFERENCE_NOT_FOUND, new Object[]{reference, category});
                return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.REFERENCE_NOT_FOUND, new Object[]{reference, category}, null);
            }
        }
        fldNames = LPArray.addValueToArray1D(fldNames, TblsInvTrackingData.LotCertification.REFERENCE.getName());
        fldValues = LPArray.addValueToArray1D(fldValues, reference);
        fldNames = LPArray.addValueToArray1D(fldNames, TblsInvTrackingData.LotCertification.CATEGORY.getName());
        fldValues = LPArray.addValueToArray1D(fldValues, category);
        fldNames = LPArray.addValueToArray1D(fldNames, new String[]{TblsInvTrackingData.LotCertification.LOT_NAME.getName(),
            TblsInvTrackingData.LotCertification.CREATED_ON.getName(), TblsInvTrackingData.LotCertification.CREATED_BY.getName()});
        fldValues = LPArray.addValueToArray1D(fldValues, new Object[]{lotName, LPDate.getCurrentTimeStamp(), token.getPersonName()});

        RdbmsObject invLotQualifCreationDiagn = Rdbms.insertRecordInTable(TablesInvTrackingData.LOT_CERTIFICATION, fldNames, fldValues);
        if (Boolean.FALSE.equals(invLotQualifCreationDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotQualifCreationDiagn.getErrorMessageCode(), new Object[]{lotName}, null);
        }
        inventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.CREATED_QUALIFICATION, lotName, reference, category, TablesInvTrackingData.LOT.getTableName(), lotName,
                fldNames, fldValues);
        if (referenceInfo != null) {
            String variableSetName = LPNulls.replaceNull(referenceInfo[0][LPArray.valuePosicInArray(allFieldNames, TblsInvTrackingConfig.Reference.QUALIF_VARIABLES_SET.getName())]).toString();
            addVariableSetToObject(lotName, Integer.valueOf(invLotQualifCreationDiagn.getNewRowId().toString()), variableSetName, token.getPersonName());
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.NEW_INVENTORY_LOT, new Object[]{lotName}, lotName);
    }

    public static InternalMessage completeInventoryLotQualif(DataInventory invLot, String category, String reference, String decision, Boolean turnLotAvailable) {
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) {
            return decisionValueIsCorrect;
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (invLot.getIsRetired() != null && invLot.getIsRetired()) {
            messages.addMainForError(InventoryTrackingErrorTrapping.ALREADY_RETIRED, new Object[]{invLot.getLotName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.ALREADY_RETIRED, new Object[]{invLot.getLotName()}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        String eventIdStr = LPNulls.replaceNull(invLot.getQualificationFieldValues()[LPArray.valuePosicInArray(invLot.getQualificationFieldNames(), TblsInvTrackingData.LotCertification.CERTIF_ID.getName())]).toString();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(invLot.getQualificationFieldValues()[0].toString()) || eventIdStr.length() == 0) {
            messages.addMainForError(InvTrackingEnums.InventoryTrackingErrorTrapping.NO_PENDING_QUALIFICATION, new Object[]{invLot.getLotName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.NO_PENDING_QUALIFICATION, new Object[]{invLot.getLotName()}, invLot.getLotName());
        }
        Integer eventId = Integer.valueOf(eventIdStr);
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInvTrackingData.LOT_CERTIFICATION.getTableName(), eventId);

        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(invLot.getLotName(), eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())) {
            messages.addMainForError(eventHasNotEnteredVariables.getMessageCodeObj(), eventHasNotEnteredVariables.getMessageCodeVariables());
            return eventHasNotEnteredVariables;
        }

        String[] fldNames = new String[]{TblsInvTrackingData.LotCertification.COMPLETED_DECISION.getName(), TblsInvTrackingData.LotCertification.COMPLETED_ON.getName(), TblsInvTrackingData.LotCertification.COMPLETED_BY.getName()};
        Object[] fldValues = new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInvTrackingData.LotCertification.CERTIF_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(TablesInvTrackingData.LOT_CERTIFICATION,
                EnumIntTableFields.getTableFieldsFromString(TablesInvTrackingData.LOT_CERTIFICATION, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length - 1].toString(), new Object[]{invLot.getLotName()}, null);
        }
        inventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_QUALIFICATION,
                invLot.getLotName(), invLot.getReference(), invLot.getCategory(), TablesInvTrackingData.LOT.getTableName(),
                invLot.getLotName(), fldNames, fldValues);
        fldNames = new String[]{TblsInvTrackingData.Lot.IS_LOCKED.getName(), TblsInvTrackingData.Lot.LOCKED_REASON.getName(), TblsInvTrackingData.Lot.STATUS.getName(), TblsInvTrackingData.Lot.STATUS_PREVIOUS.getName()};
        fldValues = new Object[]{false, "", "QUARANTINE_"+decision.toUpperCase(), "QUARANTINE"};
        invLot.updateInventoryLot(fldNames, fldValues, InvTrackingEnums.AppInventoryTrackingAuditEvents.UNLOCK_LOT_ONCE_QUALIFIED.toString());
        inventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.UNLOCK_LOT_ONCE_QUALIFIED,
                invLot.getLotName(), invLot.getReference(), invLot.getCategory(), TablesInvTrackingData.LOT.getTableName(),
                invLot.getLotName(), fldNames, fldValues);
        if (turnLotAvailable != null && turnLotAvailable && decision.toUpperCase().contains("ACCEPT")) {
            invLot.turnAvailable(null, null);
        }
//        if (!invLot.availableForUse && decisionAndFamilyRuleToTurnOn(decision, TblsAppProcConfig.InstrumentsFamily.CALIB_TURN_ON_WHEN_COMPLETED.getName())){
//            turnAvailable(fldNames, fldValues); //, InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_CALIBRATION.toString());
//        }else{
//            updateInventoryLot(fldNames, fldValues, InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_QUALIFICATION.toString());            
//        }
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.COMPLETE_QUALIFICATION, new Object[]{invLot.getLotName(), decision});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.COMPLETE_QUALIFICATION, new Object[]{invLot.getLotName(), decision}, invLot.getLotName());
    }

    public static InternalMessage reopenInventoryLotQualif(DataInventory invLot) {
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        String eventIdStr = LPNulls.replaceNull(invLot.getQualificationFieldValues()[LPArray.valuePosicInArray(invLot.getQualificationFieldNames(), TblsInvTrackingData.LotCertification.CERTIF_ID.getName())]).toString();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(invLot.getQualificationFieldValues()[0].toString()) || eventIdStr.length() == 0) {
            messages.addMainForError(InvTrackingEnums.InventoryTrackingErrorTrapping.NO_PENDING_QUALIFICATION, new Object[]{invLot.getLotName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.NO_PENDING_QUALIFICATION, new Object[]{invLot.getLotName()}, invLot.getLotName());
        }
        String qualifDecision = LPNulls.replaceNull(invLot.getQualificationFieldValues()[LPArray.valuePosicInArray(invLot.getQualificationFieldNames(), TblsInvTrackingData.LotCertification.COMPLETED_DECISION.getName())]).toString();
        if (qualifDecision.length() == 0) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.QUALIFICATION_NOT_CLOSED, new Object[]{invLot.getLotName()}, invLot.getLotName());
        }
        Integer eventId = Integer.valueOf(eventIdStr);
        String[] fldNames = new String[]{TblsInvTrackingData.LotCertification.COMPLETED_DECISION.getName(), TblsInvTrackingData.LotCertification.COMPLETED_BY.getName()};
        //TblsInvTrackingData.LotCertification.COMPLETED_ON.getName(), 
        Object[] fldValues = new Object[]{"", ""}; //"null", 
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInvTrackingData.LotCertification.CERTIF_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(TablesInvTrackingData.LOT_CERTIFICATION,
                EnumIntTableFields.getTableFieldsFromString(TablesInvTrackingData.LOT_CERTIFICATION, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length - 1].toString(), new Object[]{invLot.getLotName()}, null);
        }
        inventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.REOPEN_QUALIFICATION,
                invLot.getLotName(), invLot.getReference(), invLot.getCategory(), TablesInvTrackingData.LOT.getTableName(),
                invLot.getLotName(), fldNames, fldValues);
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.REOPEN_QUALIFICATION, new Object[]{invLot.getLotName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.REOPEN_QUALIFICATION, new Object[]{invLot.getLotName()}, invLot.getLotName());
    }

    private static InternalMessage decisionValueIsCorrect(String decision) {
        try {
            DataInventory.Decisions.valueOf(decision);
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.CORRECT, null, null);
        } catch (Exception e) {
            ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
            messages.addMainForError(InventoryTrackingErrorTrapping.WRONG_DECISION, new Object[]{decision, Arrays.toString(DataInventory.Decisions.values())});
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.WRONG_DECISION, new Object[]{decision, Arrays.toString(DataInventory.Decisions.values())}, null);
        }
    }

    public static Object[][] getVariableSetVariablesProperties(String variableSetName) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String appProcInstance = LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName());

        Object[][] variableSetInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.CONFIG.getName()), TblsInvTrackingConfig.TablesInvTrackingConfig.VARIABLES_SET.getTableName(),
                new String[]{TblsInvTrackingConfig.VariablesSet.NAME.getName()}, new Object[]{variableSetName},
                new String[]{TblsInvTrackingConfig.VariablesSet.VARIABLES_LIST.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variableSetInfo[0][0].toString())) {
            return variableSetInfo;
        }
        String variableSetContent = LPNulls.replaceNull(variableSetInfo[0][0]).toString();
        String[] fieldsToRetrieve = new String[]{TblsInvTrackingConfig.Variables.PARAM_NAME.getName(), TblsInvTrackingConfig.Variables.PARAM_TYPE.getName(), TblsInvTrackingConfig.Variables.REQUIRED.getName(),
            TblsInvTrackingConfig.Variables.ALLOWED_VALUES.getName()};
        Object[][] variablesProperties2D = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.CONFIG.getName()), TblsInvTrackingConfig.TablesInvTrackingConfig.VARIABLES.getTableName(),
                new String[]{TblsInvTrackingConfig.Variables.PARAM_NAME.getName() + " IN"}, new Object[]{variableSetContent},
                fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variablesProperties2D[0][0].toString())) {
            return variablesProperties2D;
        }
        Object[] variablesProperties1D = LPArray.array2dTo1d(variablesProperties2D);
        variablesProperties1D = LPArray.addValueToArray1D(fieldsToRetrieve, variablesProperties1D);
        return LPArray.array1dTo2d(variablesProperties1D, fieldsToRetrieve.length);
    }

    public static Object[] isCertificationOpenToChanges(Integer lotCertifId, String lotName, String category, String ref) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String[] wFldName = new String[]{};
        Object[] wFldValue = new Object[]{};
        if (lotCertifId != null) {
            wFldName = new String[]{TblsInvTrackingData.LotCertification.CERTIF_ID.getName()};
            wFldValue = new Object[]{lotCertifId};
        } else {
            wFldName = new String[]{TblsInvTrackingData.LotCertification.LOT_NAME.getName(), TblsInvTrackingData.LotCertification.CATEGORY.getName(), TblsInvTrackingData.LotCertification.REFERENCE.getName()};
            wFldValue = new Object[]{lotName, category, ref};
        }
        String appProcInstance = LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName());
        Object[][] eventInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION.getTableName(),
                wFldName, wFldValue, new String[]{TblsInvTrackingData.LotCertification.COMPLETED_BY.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventInfo[0][0].toString())) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.NOT_FOUND, new Object[]{lotCertifId, appProcInstance});
        }
        if (LPNulls.replaceNull(eventInfo[0][0]).toString().length() > 0) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.NO_PENDING_QUALIFICATION, new Object[]{lotCertifId, appProcInstance});
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "<*1*> is open to changes in procedure <*2*>", new Object[]{lotCertifId, appProcInstance});
    }

    public static Object[] addVariableSetToObject(String lotName, Integer lotCertifId, String variableSetName, String ownerId) {
        Object[] diagn = new Object[0];
        Object[] isStudyOpenToChanges = isCertificationOpenToChanges(lotCertifId, null, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())) {
            return isStudyOpenToChanges;
        }
        if (variableSetName == null) {
            return new Object[]{};
        }
        Object[][] variableSetContent = getVariableSetVariablesProperties(variableSetName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variableSetContent[0][0].toString())) {
            return variableSetContent[0];
        }
        String[] fieldHeaders = new String[0];
        for (Object[] currVar : variableSetContent) {
            if (fieldHeaders.length == 0) {
                for (Object currVar1 : currVar) {
                    fieldHeaders = LPArray.addValueToArray1D(fieldHeaders, currVar1.toString());
                }
            } else {
                Object[] fieldVarProperties = new Object[0];
                for (Object currVar1 : currVar) {
                    fieldVarProperties = LPArray.addValueToArray1D(fieldVarProperties, currVar1);
                }
                String[] fieldsName = new String[]{TblsInvTrackingData.LotCertificationVariableValues.LOT_NAME.getName(), TblsInvTrackingData.LotCertificationVariableValues.CERTIF_ID.getName(), TblsInvTrackingData.LotCertificationVariableValues.OWNER_ID.getName(),
                    TblsInvTrackingData.LotCertificationVariableValues.VARIABLE_SET.getName()};
                fieldsName = LPArray.addValueToArray1D(fieldsName, fieldHeaders);
                Object[] fieldsValue = new Object[]{lotName, lotCertifId, ownerId, variableSetName};
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, fieldVarProperties);
                /*                Object[][] extraFields=objectFieldExtraFields(insEventId, variableSetName, ownerTable, ownerId);
                if (extraFields!=null && extraFields.length>0){
                for (Object[] curFld: extraFields){
                fieldsName=LPArray.addValueToArray1D(fieldsName, curFld[0].toString());
                fieldsValue=LPArray.addValueToArray1D(fieldsValue, curFld[1]);
                }
                }*/
                RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION_VARIABLE_VALUES, fieldsName, fieldsValue);
                if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
                    inventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.ADDED_VARIABLE, lotName, null, null, TblsInvTrackingData.TablesInvTrackingData.LOT.getTableName(), lotCertifId.toString(),
                            fieldsName, fieldsValue);
                }
            }
        }
        return diagn;
    }

    public static InternalMessage objectVariableSetValue(String lotName, String category, String reference, Integer lotCertifId, String variableName, String newValue) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String appProcInstance = LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName());

        if (lotCertifId == null) {
            DataInventory invLot = new DataInventory(lotName, reference, category, null);
            if ((Boolean.TRUE.equals(invLot.getHasError())) || (Boolean.FALSE.equals(invLot.getRequiresQualification()))) {
                return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.NO_PENDING_QUALIFICATION, null, null);
            }
            String lotCertifIdStr = LPNulls.replaceNull(
                    invLot.getQualificationFieldValues()[LPArray.valuePosicInArray(
                    invLot.getQualificationFieldNames(), TblsInvTrackingData.LotCertificationVariableValues.CERTIF_ID.getName())]).toString();
            lotCertifId = Integer.valueOf(lotCertifIdStr);
        }

        Object[] isStudyOpenToChanges = isCertificationOpenToChanges(lotCertifId, lotName, category, reference);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.NO_PENDING_QUALIFICATION, null, null);
        }

        String[] fieldsToRetrieve = new String[]{TblsInvTrackingData.LotCertificationVariableValues.ID.getName(),
            TblsInvTrackingData.LotCertificationVariableValues.PARAM_NAME.getName(), TblsInvTrackingData.LotCertificationVariableValues.PARAM_TYPE.getName(),
            TblsInvTrackingData.LotCertificationVariableValues.REQUIRED.getName(),
            TblsInvTrackingData.LotCertificationVariableValues.ALLOWED_VALUES.getName(), TblsInvTrackingData.LotCertificationVariableValues.VALUE.getName()};

        String[] fieldsName = new String[]{TblsInvTrackingData.LotCertificationVariableValues.CERTIF_ID.getName(),
            TblsInvTrackingData.LotCertificationVariableValues.PARAM_NAME.getName()};
        Object[] fieldsValue = new Object[]{lotCertifId, variableName};
        Object[][] objectVariablePropInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION_VARIABLE_VALUES.getTableName(),
                fieldsName, fieldsValue, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectVariablePropInfo[0][0].toString())) {
            Object[][] instEvVariables = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION_VARIABLE_VALUES.getTableName(),
                    new String[]{TblsInvTrackingData.LotCertificationVariableValues.CERTIF_ID.getName()}, new Object[]{lotCertifId}, fieldsToRetrieve);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instEvVariables[0][0].toString())) {
                return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.VARIABLE_NOT_EXISTS_EVENT_WITHNOVARIABLES, null);
            }
            /*else{
                return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.VARIABLE_NOT_EXISTS, 
                new Object[]{Arrays.toString(LPArray.getColumnFromArray2D(instEvVariables, 1))});
            }*/
        }
        if (objectVariablePropInfo.length != 1) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.MORE_THAN_ONE_VARIABLE,
                    new Object[]{objectVariablePropInfo.length, Arrays.toString(fieldsName), appProcInstance});
        }
        String currentValue = LPNulls.replaceNull(objectVariablePropInfo[0][5]).toString();
        if (currentValue.length() > 0) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.USE_REENTER_WHEN_PARAM_ALREADY_HAS_VALUE,
                    new Object[]{});
        }
        String fieldType = objectVariablePropInfo[0][2].toString().toUpperCase();
        switch (DataStudyObjectsVariableValues.VariableTypes.valueOf(fieldType)) {
            case LIST:
                String[] allowedValuesArr = LPNulls.replaceNull(objectVariablePropInfo[0][4]).toString().split("\\|");
                if (Boolean.FALSE.equals(LPArray.valueInArray(allowedValuesArr, newValue))) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.VARIABLE_VALUE_NOTONEOFTHEEXPECTED,
                            new Object[]{newValue, Arrays.toString(allowedValuesArr), variableName, appProcInstance});
                }
                break;
            case REAL:
            case INTEGER:
                Object[] isNumeric = isNumeric(newValue);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric[0].toString())) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.NOT_NUMERIC_VALUE, null, null);
                }
                break;
            case TEXT:
                break;
            default:
                return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.VARIABLE_TYPE_NOT_RECOGNIZED, new Object[]{fieldType}, null);
        }
        String[] updFieldsName = new String[]{TblsInvTrackingData.LotCertificationVariableValues.VALUE.getName()};
        Object[] updFieldsValue = new Object[]{newValue};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInvTrackingData.LotCertificationVariableValues.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{Integer.valueOf(objectVariablePropInfo[0][0].toString())}, "");
        Object[] diagnostic = Rdbms.updateRecordFieldsByFilter(TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION_VARIABLE_VALUES,
                EnumIntTableFields.getTableFieldsFromString(TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION_VARIABLE_VALUES, updFieldsName), updFieldsValue, sqlWhere, null);
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString()))) {
            inventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.VALUE_ENTERED, lotName, null, null, TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION.getTableName(), lotCertifId.toString(),
                    updFieldsName, updFieldsValue);
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.ENTER_EVENT_RESULT, new Object[]{lotName, lotCertifId, variableName, newValue}, null);
    }

    public static InternalMessage objectVariableChangeValue(String lotName, String category, String reference, Integer lotCertifId, String variableName, String newValue) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String appProcInstance = LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName());
        Object[] isStudyOpenToChanges = isCertificationOpenToChanges(lotCertifId, lotName, category, reference);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES, null, null);
        }

        String[] fieldsToRetrieve = new String[]{TblsInvTrackingData.LotCertificationVariableValues.ID.getName(), TblsInvTrackingData.LotCertificationVariableValues.PARAM_NAME.getName(), TblsInvTrackingData.LotCertificationVariableValues.PARAM_TYPE.getName(), TblsInvTrackingData.LotCertificationVariableValues.REQUIRED.getName(),
            TblsInvTrackingData.LotCertificationVariableValues.ALLOWED_VALUES.getName(), TblsInvTrackingData.LotCertificationVariableValues.VALUE.getName()};

        String[] fieldsName = new String[]{TblsInvTrackingData.LotCertificationVariableValues.CERTIF_ID.getName(),
            TblsInvTrackingData.LotCertificationVariableValues.PARAM_NAME.getName()};
        Object[] fieldsValue = new Object[]{lotCertifId, variableName};
        Object[][] objectVariablePropInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION_VARIABLE_VALUES.getTableName(),
                fieldsName, fieldsValue, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectVariablePropInfo[0][0].toString())) {
            Object[][] instEvVariables = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION_VARIABLE_VALUES.getTableName(),
                    new String[]{TblsInvTrackingData.LotCertificationVariableValues.CERTIF_ID.getName()}, new Object[]{lotCertifId}, fieldsToRetrieve);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instEvVariables[0][0].toString())) {
                return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.VARIABLE_NOT_EXISTS_EVENT_WITHNOVARIABLES, null);
            } else {
                return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.VARIABLE_NOT_EXISTS,
                        new Object[]{Arrays.toString(LPArray.getColumnFromArray2D(instEvVariables, 1))});
            }
        }
        if (objectVariablePropInfo.length != 1) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.MORE_THAN_ONE_VARIABLE,
                    new Object[]{objectVariablePropInfo.length, Arrays.toString(fieldsName), appProcInstance});
        }
        String currentValue = LPNulls.replaceNull(objectVariablePropInfo[0][5]).toString();
        if (currentValue.length() == 0) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.USE_ENTER_WHEN_PARAM_HAS_NO_VALUE,
                    new Object[]{});
        }
        if (currentValue.equalsIgnoreCase(newValue)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InvTrackingEventsErrorTrapping.SAME_RESULT_VALUE,
                    new Object[]{variableName, appProcInstance, newValue});
        }
        String fieldType = objectVariablePropInfo[0][2].toString().toUpperCase();
        switch (DataStudyObjectsVariableValues.VariableTypes.valueOf(fieldType)) {
            case LIST:
                String[] allowedValuesArr = LPNulls.replaceNull(objectVariablePropInfo[0][4]).toString().split("\\|");
                if (Boolean.FALSE.equals(LPArray.valueInArray(allowedValuesArr, newValue))) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.MORE_THAN_ONE_VARIABLE,
                            new Object[]{newValue, Arrays.toString(allowedValuesArr), variableName, appProcInstance});
                }
                break;
            case REAL:
            case INTEGER:
                Object[] isNumeric = isNumeric(newValue);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric[0].toString())) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.NOT_NUMERIC_VALUE, null, null);
                }
                break;
            case TEXT:
                break;
            default:
                return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.VARIABLE_TYPE_NOT_RECOGNIZED, new Object[]{fieldType}, null);
        }
        String[] updFieldsName = new String[]{TblsInvTrackingData.LotCertificationVariableValues.VALUE.getName()};
        Object[] updFieldsValue = new Object[]{newValue};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInvTrackingData.LotCertificationVariableValues.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{Integer.valueOf(objectVariablePropInfo[0][0].toString())}, "");
        Object[] diagnostic = Rdbms.updateRecordFieldsByFilter(TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION_VARIABLE_VALUES,
                EnumIntTableFields.getTableFieldsFromString(TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION_VARIABLE_VALUES, updFieldsName), updFieldsValue, sqlWhere, null);
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString()))) {
            inventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.VALUE_REENTERED, lotName, null, null, TblsInvTrackingData.TablesInvTrackingData.LOT.getTableName(), lotCertifId.toString(),
                    updFieldsName, updFieldsValue);
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.ENTER_EVENT_RESULT, new Object[]{lotName, lotCertifId, variableName, newValue}, null);
    }

    public static InternalMessage eventHasNotEnteredVariables(String lotName, Integer lotCertifId) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String appProcInstance = LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName());
        Object[] isStudyOpenToChanges = isCertificationOpenToChanges(lotCertifId, null, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())) {
            ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
            messages.addMainForError(InvTrackingEnums.InventoryTrackingErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES, new Object[]{lotCertifId});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES, new Object[]{lotCertifId}, null);
        }

        Object[][] diagn = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION_VARIABLE_VALUES.getTableName(),
                new String[]{TblsInvTrackingData.LotCertificationVariableValues.LOT_NAME.getName(),
                    TblsInvTrackingData.LotCertificationVariableValues.CERTIF_ID.getName(), TblsInvTrackingData.LotCertificationVariableValues.REQUIRED.getName(), TblsInvTrackingData.LotCertificationVariableValues.VALUE.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
                new Object[]{lotName, lotCertifId, "Y"}, new String[]{TblsInvTrackingData.LotCertificationVariableValues.ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackingErrorTrapping.EVENT_NOTHING_PENDING, null, null);
        } else {
            ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
            messages.addMainForError(InvTrackingEnums.InventoryTrackingErrorTrapping.EVENT_HAS_PENDING_RESULTS, new Object[]{diagn.length});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.EVENT_HAS_PENDING_RESULTS, new Object[]{diagn.length}, null);
        }
    }

    public static InternalMessage invTrackingAuditSetAuditRecordAsReviewed(Integer auditId, String personName) {
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
        String appProcInstance = GlobalVariables.Schemas.APP_PROC_DATA_AUDIT.getName();
        String auditReviewMode = Parameter.getBusinessRuleProcedureFile(appProcInstance, InvTrackingEnums.InventoryTrackBusinessRules.REVISION_MODE.getAreaName(), InvTrackingEnums.InventoryTrackBusinessRules.REVISION_MODE.getTagName());
        if (Boolean.FALSE.equals(isTagValueOneOfEnableOnes(auditReviewMode))) {
            messages.addMainForError(InvTrackingEnums.InventoryTrackingErrorTrapping.DISABLED, new Object[]{});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.DISABLED, new Object[]{});
        }
        String auditAuthorCanBeReviewerMode = Parameter.getBusinessRuleProcedureFile(appProcInstance, InvTrackingEnums.InventoryTrackBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getAreaName(), InvTrackingEnums.InventoryTrackBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName());
        Object[][] auditInfo = QueryUtilitiesEnums.getTableData(TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT,
                EnumIntTableFields.getTableFieldsFromString(TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT, new String[]{TblsInvTrackingDataAudit.Lot.PERSON.getName(), TblsInvTrackingDataAudit.Lot.REVIEWED.getName()}),
                new String[]{TblsInvTrackingDataAudit.Lot.AUDIT_ID.getName()}, new Object[]{auditId},
                new String[]{TblsInvTrackingDataAudit.Lot.AUDIT_ID.getName()});
        if (Boolean.FALSE.equals(isTagValueOneOfEnableOnes(auditAuthorCanBeReviewerMode))) {
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(auditInfo[0][0].toString())) {
                messages.addMainForError(InvTrackingEnums.InventoryTrackingErrorTrapping.DISABLED, new Object[]{});
                return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.DISABLED, new Object[]{});
            }
            if (personName.equalsIgnoreCase(auditInfo[0][0].toString())) {
                messages.addMainForError(InvTrackingEnums.InventoryTrackingErrorTrapping.AUTHOR_CANNOT_BE_REVIEWER, new Object[]{});
                return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.AUTHOR_CANNOT_BE_REVIEWER, new Object[]{});
            }
        }
        if (Boolean.TRUE.equals(Boolean.valueOf(auditInfo[0][1].toString()))) {
            messages.addMainForError(InvTrackingEnums.InventoryTrackingErrorTrapping.AUDIT_RECORD_ALREADY_REVIEWED, new Object[]{auditId});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.AUDIT_RECORD_ALREADY_REVIEWED, new Object[]{auditId});
        }
        String[] updFieldsName = new String[]{TblsInvTrackingDataAudit.Lot.REVIEWED.getName(), TblsInvTrackingDataAudit.Lot.REVIEWED_BY.getName(), TblsInvTrackingDataAudit.Lot.REVIEWED_ON.getName()};
        Object[] updFieldsValue = new Object[]{true, personName, LPDate.getCurrentTimeStamp()};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInvTrackingDataAudit.Lot.AUDIT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{auditId}, "");
        Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT,
                EnumIntTableFields.getTableFieldsFromString(TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT, updFieldsName), updFieldsValue, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(updateRecordFieldsByFilter[0].toString())) {
            return new InternalMessage(updateRecordFieldsByFilter[0].toString(), InvTrackingEnums.InventoryTrackAPIactionsEndpoints.LOTAUDIT_SET_AUDIT_ID_REVIEWED, new Object[]{auditId});
        } else {
            return new InternalMessage(updateRecordFieldsByFilter[0].toString(), InvTrackingEnums.InventoryTrackingErrorTrapping.AUDIT_RECORD_NOT_FOUND, new Object[]{auditId});
        }
    }

}
