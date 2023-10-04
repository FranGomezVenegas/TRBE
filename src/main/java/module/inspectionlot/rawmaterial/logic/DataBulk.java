/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsCnfg;
import databases.features.Token;
import functionaljavaa.materialspec.SamplingPlanEntry;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.isTagValueOneOfEnableOnes;
import module.inspectionlot.rawmaterial.definition.LotAudit;
import java.math.BigDecimal;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.InspLotRMBusinessRules;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.InspectionLotRMAuditEvents;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMConfig;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMProcedure;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.session.ResponseMessages;

/**
 *
 * @author User
 */
public class DataBulk {

    private DataBulk() {
        throw new IllegalStateException("Utility class");
    }

    public static InternalMessage createBulk(String lotName, Double smpQuant, String smpQuantUom, Integer numBulk, Boolean isAdhoc) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String[] fieldName = new String[]{TblsInspLotRMData.LotBulk.LOT_NAME.getName(),
            TblsInspLotRMData.LotBulk.CREATED_BY.getName(), TblsInspLotRMData.LotBulk.CREATED_ON.getName()};
        Object[] fieldValue = new Object[]{lotName,
            procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        if (smpQuant != null) {
            fieldName = LPArray.addValueToArray1D(fieldName, TblsInspLotRMData.LotBulk.SAMPLE_QUANTITY.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, smpQuant);
        }
        if (smpQuantUom != null) {
            fieldName = LPArray.addValueToArray1D(fieldName, TblsInspLotRMData.LotBulk.SAMPLE_QUANTITY_UOM.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, smpQuantUom);
        }
        fieldName = LPArray.addValueToArray1D(fieldName, TblsInspLotRMData.LotBulk.BULK_NAME.getName());
        String numBulkStr = String.valueOf(numBulk);
        if (numBulkStr.length() == 1) {
            numBulkStr = "0" + numBulkStr;
        }
        fieldValue = LPArray.addValueToArray1D(fieldValue, lotName + "-" + numBulkStr);

        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK, fieldName, fieldValue);
        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
        }
        LotAudit lotAudit = new LotAudit();
        EnumIntAuditEvents audEvObj = null;
        if (Boolean.TRUE.equals(isAdhoc)) {
            audEvObj = InspectionLotRMAuditEvents.LOT_BULK_ADHOC_ADDED;
        } else {
            audEvObj = InspectionLotRMAuditEvents.LOT_BULK_ADDED;
        }
        lotAudit.lotAuditAdd(audEvObj,
                TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(), insertRecordInTable.getNewRowId().toString(), lotName, fieldName, fieldValue);
        return new InternalMessage(LPPlatform.LAB_TRUE,
                InspLotRMEnums.InspLotRMAPIactionsEndpoints.NEW_LOT, new Object[]{lotName}, insertRecordInTable.getNewRowId());
    }

    public static InternalMessage addAdhocBulk(String lotName, Integer numAdhocBulks) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] lotInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(),
                new String[]{TblsInspLotRMData.Lot.NAME.getName()}, new Object[]{lotName},
                new String[]{TblsInspLotRMData.Lot.MATERIAL_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }
        String materialName = lotInfo[0][0].toString();
        Object[][] materialInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL.getTableName(),
                new String[]{TblsInspLotRMConfig.Material.NAME.getName()}, new Object[]{materialName},
                new String[]{TblsInspLotRMConfig.Material.ADD_ADHOC_BULK_ADDITION.getName(), TblsInspLotRMConfig.Material.SPEC_CODE.getName(), TblsInspLotRMConfig.Material.SPEC_CODE_VERSION.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }
        if (Boolean.FALSE.equals(Boolean.valueOf(LPNulls.replaceNull(materialInfo[0][0]).toString()))) {
            new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.ADD_ADHOC_BULKS_NOT_ALLOWED, new Object[]{lotName, materialName}, null);
        }
        Object[][] specInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsInspLotRMConfig.TablesInspLotRMConfig.SPEC.getTableName(),
                new String[]{TblsCnfg.Spec.CODE.getName(), TblsCnfg.Spec.CONFIG_VERSION.getName()}, new Object[]{materialInfo[0][1], materialInfo[0][2]},
                new String[]{TblsInspLotRMConfig.Spec.TOTAL_SAMPLE_REQ_Q.getName(), TblsInspLotRMConfig.Spec.TOTAL_SAMPLE_REQ_Q_UOM.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(specInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.MISSING_SPEC_CONFIG_CODE, new Object[]{materialInfo[0][0], materialInfo[0][1], procInstanceName});
        }
        Double smpQuant = Double.valueOf(LPNulls.replaceNull(specInfo[0][0]).toString());
        String smpQuantUom = (LPNulls.replaceNull(specInfo[0][1]).toString());
        Object[][] lotBulksInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(),
                new String[]{TblsInspLotRMData.LotBulk.LOT_NAME.getName()}, new Object[]{lotName},
                new String[]{TblsInspLotRMData.LotBulk.LOT_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotBulksInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_WITH_NO_BULKS, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }
        InternalMessage createBulkDiagn = null;
        for (int i = 0; i < numAdhocBulks; i++) {
            createBulkDiagn = createBulk(lotName, smpQuant, smpQuantUom, lotBulksInfo.length + 1, true);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(createBulkDiagn.getDiagnostic())) {
                return createBulkDiagn;
            }
        }
        return createBulkDiagn;
    }

    public static InternalMessage lotBulkAdjustQuantity(String lotName, Integer bulkId, BigDecimal quantity, String quantityUomStr, String[] fieldName, Object[] fieldValue) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] lotBulkInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(),
                new String[]{TblsInspLotRMData.LotBulk.LOT_NAME.getName(), TblsInspLotRMData.LotBulk.BULK_ID.getName()}, new Object[]{lotName, bulkId},
                new String[]{TblsInspLotRMData.LotBulk.QUANTITY.getName(), TblsInspLotRMData.LotBulk.SAMPLE_QUANTITY_UOM.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotBulkInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }
        if (quantityUomStr == null || quantityUomStr.length() == 0) {
            Object[][] lotInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(),
                    new String[]{TblsInspLotRMData.Lot.NAME.getName()}, new Object[]{lotName},
                    new String[]{TblsInspLotRMData.Lot.QUANTITY_UOM.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) {
                return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
            }
            quantityUomStr =lotInfo[0][0].toString();
        }
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInspLotRMData.LotBulk.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
        sqlWhere.addConstraint(TblsInspLotRMData.LotBulk.BULK_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{bulkId}, "");
        String[] updFieldName = new String[]{TblsInspLotRMData.LotBulk.QUANTITY.getName(), TblsInspLotRMData.LotBulk.QUANTITY_UOM.getName()};
        Object[] updFieldValue = new Object[]{quantity, quantityUomStr};
        RdbmsObject diagnoses = Rdbms.updateTableRecordFieldsByFilter(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK,
            EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK, updFieldName), updFieldValue, sqlWhere, null);
        if (Boolean.FALSE.equals(diagnoses.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnoses.getErrorMessageCode(), diagnoses.getErrorMessageVariables());
        }
        LotAudit lotAudit = new LotAudit();
        lotAudit.lotAuditAdd(InspectionLotRMAuditEvents.LOT_BULK_QUANTITY_ADJUSTED,
                TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(), bulkId.toString(), lotName, updFieldName, updFieldValue);
        return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_BULK_ADJUST_QUANTITY, new Object[]{lotName, quantity, procInstanceName});
    }

    public static InternalMessage lotBulkSampleQuantity(String lotName, Integer bulkId, BigDecimal smpQuantity, String smpQuantityUom, String[] fieldName, Object[] fieldValue) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[][] lotInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(),
                new String[]{TblsInspLotRMData.LotBulk.LOT_NAME.getName(), TblsInspLotRMData.LotBulk.BULK_ID.getName()}, new Object[]{lotName, bulkId},
                new String[]{TblsInspLotRMData.LotBulk.QUANTITY.getName(), TblsInspLotRMData.LotBulk.SAMPLE_QUANTITY_UOM.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInspLotRMData.LotBulk.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
        sqlWhere.addConstraint(TblsInspLotRMData.LotBulk.BULK_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{bulkId}, "");
        String[] updFieldName = new String[]{TblsInspLotRMData.LotBulk.SAMPLE_QUANTITY.getName()};
        Object[] updFieldValue = new Object[]{smpQuantity};
        RdbmsObject diagnoses = Rdbms.updateTableRecordFieldsByFilter(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK,
                EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK, TblsInspLotRMData.LotBulk.SAMPLE_QUANTITY.getName()), updFieldValue, sqlWhere, null);
        if (Boolean.FALSE.equals(diagnoses.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnoses.getErrorMessageCode(), diagnoses.getErrorMessageVariables());
        }
        LotAudit lotAudit = new LotAudit();
        lotAudit.lotAuditAdd(InspectionLotRMAuditEvents.LOT_BULK_SAMPLE_QUANTITY_ADJUSTED,
                TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(), bulkId.toString(), lotName, updFieldName, updFieldValue);
        return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_BULK_ADJUST_SAMPLE_QUANTITY, new Object[]{lotName, smpQuantity, procInstanceName});
    }

    public static InternalMessage lotBulkTakeDecision(String lotName, Integer bulkId, String decision, String[] fieldName, Object[] fieldValue) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[][] lotInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(),
                new String[]{TblsInspLotRMData.Lot.NAME.getName()}, new Object[]{lotName},
                new String[]{TblsInspLotRMData.Lot.MATERIAL_NAME.getName(), TblsInspLotRMData.Lot.SPEC_CODE.getName(),
                    TblsInspLotRMData.Lot.SPEC_CODE_VERSION.getName(), TblsInspLotRMData.Lot.QUANTITY.getName(), TblsInspLotRMData.Lot.QUANTITY_UOM.getName(),
                    TblsInspLotRMData.Lot.NUM_CONTAINERS.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }
        InternalMessage lotContainerDecisionRecordCreateOrUpdate = lotBulkDecisionRecordCreateOrUpdate(lotName, bulkId, decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotContainerDecisionRecordCreateOrUpdate.getDiagnostic())) {
            return lotContainerDecisionRecordCreateOrUpdate;
        }
        if (decision.toUpperCase().contains("REJEC")) {
            createInspLotCorrectiveAction(lotName, bulkId, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_BULK_TAKE_DECISION);
        }

        return new InternalMessage(LPPlatform.LAB_TRUE,
                InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_BULK_TAKE_DECISION, new Object[]{lotName});
    }

    public static InternalMessage createInspLotCorrectiveAction(String lotName, Integer bulkId, EnumIntEndpoints endpoint) {
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String createInvCorrectiveAction = Parameter.getBusinessRuleProcedureFile(procInstanceName, InspLotRMBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_BULK.getAreaName(), InspLotRMBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_BULK.getTagName());
        if (Boolean.FALSE.equals(isTagValueOneOfEnableOnes(createInvCorrectiveAction))) {
            //When disabled it should return TRUE due to it does nothing but is not an error is due to it's disabled.
            // messages.addMainForError(InspLotRMEnums.DataInspLotErrorTrapping.DISABLED, new Object[]{});
            return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.DataInspLotErrorTrapping.DISABLED, new Object[]{});
        }
        return DataInsLotsCorrectiveAction.createNew(bulkId, endpoint,
                new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.LOT_NAME.getName()},
                new Object[]{lotName}, TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION.getTableName());
    }

    public static InternalMessage lotBulkDecisionRecordCreateOrUpdate(String lotName, Integer bulkId, String decision) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String[] lotFieldName = new String[]{};
        Object[] lotFieldValue = new Object[]{};
        Object[] errorDetailVariables = new Object[]{};
        RdbmsObject diagnoses = null;

        if (decision != null && decision.length() > 0) {
            lotFieldName = LPArray.addValueToArray1D(lotFieldName, new String[]{TblsInspLotRMData.LotBulk.DECISION.getName(), TblsInspLotRMData.LotBulk.DECISION_TAKEN_BY.getName(), TblsInspLotRMData.LotBulk.DECISION_TAKEN_ON.getName()});
            lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, new Object[]{decision, token.getPersonName(), LPDate.getCurrentTimeStamp()});
        }
        Object[][] lotBulkExists = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(),
                new String[]{TblsInspLotRMData.LotBulk.LOT_NAME.getName(), TblsInspLotRMData.LotBulk.BULK_ID.getName()}, new Object[]{lotName, bulkId},
                new String[]{TblsInspLotRMData.LotBulk.DECISION.getName(), TblsInspLotRMData.LotBulk.BULK_NAME.getName()});
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(lotBulkExists[0][0]).toString()))) {
            if (LPNulls.replaceNull(lotBulkExists[0][0]).toString().length() > 0) {
                return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_BULK_ALREADY_HAS_DECISION, new Object[]{LPNulls.replaceNull(lotBulkExists[0][1]).toString(), lotName});
            }
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsInspLotRMData.LotBulk.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
            sqlWhere.addConstraint(TblsInspLotRMData.LotBulk.BULK_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{bulkId}, "");
            diagnoses = Rdbms.updateTableRecordFieldsByFilter(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK,
                    EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK, lotFieldName), lotFieldValue, sqlWhere, null);
        } else {
            lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.LotBulk.LOT_NAME.getName());
            lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, lotName);
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK, lotFieldName, lotFieldValue);

            if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, insertRecordInTable.getNewRowId());
                return new InternalMessage(LPPlatform.LAB_FALSE, diagnoses.getErrorMessageCode(), diagnoses.getErrorMessageVariables());
            }
        }
        if (decision != null && decision.length() > 0) {
            LotAudit lotAudit = new LotAudit();
            lotAudit.lotAuditAdd(InspectionLotRMAuditEvents.LOT_BULK_DECISION_TAKEN,
                    TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(), bulkId.toString(), lotName, lotFieldName, lotFieldValue);
            return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_BULK_TAKE_DECISION, new Object[]{lotName, decision, procInstanceName});
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, Rdbms.RdbmsSuccess.RDBMS_TABLE_FOUND, null);
    }

    public static InternalMessage applyBulkSamplesSamplingPlan(String lotName, String materialName, String decision) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] materialInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL.getTableName(),
                new String[]{TblsInspLotRMConfig.Material.NAME.getName()}, new Object[]{materialName},
                new String[]{TblsInspLotRMConfig.Material.ADD_ADHOC_BULK_ADDITION.getName(), TblsInspLotRMConfig.Material.SPEC_CODE.getName(), TblsInspLotRMConfig.Material.SPEC_CODE_VERSION.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }
        Object[][] specInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsInspLotRMConfig.TablesInspLotRMConfig.SPEC.getTableName(),
                new String[]{TblsCnfg.Spec.CODE.getName(), TblsCnfg.Spec.CONFIG_VERSION.getName()}, new Object[]{materialInfo[0][1], materialInfo[0][2]},
                new String[]{TblsInspLotRMConfig.Spec.TOTAL_SAMPLE_REQ_Q.getName(), TblsInspLotRMConfig.Spec.TOTAL_SAMPLE_REQ_Q_UOM.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(specInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.MISSING_SPEC_CONFIG_CODE, new Object[]{materialInfo[0][0], materialInfo[0][1], procInstanceName});
        }
        Object[][] lotBulksInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(),
                new String[]{TblsInspLotRMData.LotBulk.LOT_NAME.getName(), TblsInspLotRMData.LotBulk.DECISION.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.LIKE.getSqlClause()}, new Object[]{lotName, "AC%"},
                new String[]{TblsInspLotRMData.LotBulk.LOT_NAME.getName(), TblsInspLotRMData.LotBulk.DECISION.getName(),
                    TblsInspLotRMData.LotBulk.SAMPLE_QUANTITY.getName(), TblsInspLotRMData.LotBulk.SAMPLE_QUANTITY_UOM.getName(),
                    TblsInspLotRMData.LotBulk.BULK_ID.getName(), TblsInspLotRMData.LotBulk.BULK_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotBulksInfo[0][0].toString())) {
            new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_WITH_NO_BULKS, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }
        SamplingPlanEntry spEntry = new SamplingPlanEntry(materialName, materialInfo[0][1].toString(), Integer.valueOf(materialInfo[0][2].toString()), null, null);
        if (Boolean.TRUE.equals(spEntry.getHasErrors())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.SAMPLEPLAN_CHECKER_ERROR, new Object[]{Arrays.toString(spEntry.getErrorsArr())});
        }

        for (Object[] curBulk : lotBulksInfo) {
            Double smpQuant = Double.valueOf(LPNulls.replaceNull(curBulk[2]).toString());
            String smpQuantUom = (LPNulls.replaceNull(curBulk[3]).toString());
            if (curBulk[1].toString().toUpperCase().contains("ACCEPT")) {
                InternalMessage applySamplingPlan = DataInspectionLot.applySamplesSamplingPlan(lotName, materialName, materialInfo[0][1].toString(), Integer.valueOf(materialInfo[0][2].toString()), null, null, null, null,
                        spEntry, Integer.valueOf(curBulk[4].toString()), curBulk[5].toString(), smpQuant, smpQuantUom, 1);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(applySamplingPlan.getDiagnostic())) {
                    return applySamplingPlan;
                }
            }
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.DataInspLotErrorTrapping.SAMPLEPLAN_CHECKER_ERROR, new Object[]{Arrays.toString(spEntry.getErrorsArr())});
    }
}
