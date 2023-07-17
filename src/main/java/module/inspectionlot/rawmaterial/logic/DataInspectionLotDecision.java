/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.logic;

import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.InspLotRMAPIactionsEndpoints;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMConfig;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.features.Token;
import functionaljavaa.inventory.InventoryGlobalVariables;
import functionaljavaa.samplestructure.DataSampleStructureEnums;
import module.inspectionlot.rawmaterial.definition.LotAudit;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.DataInspLotErrorTrapping;
import functionaljavaa.samplestructure.DataSampleStructureStatuses;
import functionaljavaa.samplestructure.DataSampleStructureStatuses.SampleStatuses;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement;
import java.math.BigDecimal;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.InspectionLotRMAuditEvents;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.InspectionLotRMClousureTypes;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public class DataInspectionLotDecision {

    public InternalMessage lotAllBulksTakeDecision(DataInspectionLot insplot, String lotName, String decision, String[] fieldName, Object[] fieldValue) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] dataLotFlds = new String[]{TblsInspLotRMData.Lot.LOT_CONFIG_NAME.getName(), TblsInspLotRMData.Lot.LOT_CONFIG_VERSION.getName(), TblsInspLotRMData.Lot.MATERIAL_NAME.getName(), TblsInspLotRMData.Lot.BULK_DECISION_BY.getName(),
            TblsInspLotRMData.Lot.QUANTITY.getName(), TblsInspLotRMData.Lot.QUANTITY_UOM.getName()};
        String[] configLotDecisionFlds = EnumIntTableFields.getAllFieldNames(TblsInspLotRMConfig.TablesInspLotRMConfig.LOT_DECISION_RULES.getTableFields());

        Object[][] lotInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(),
                new String[]{TblsInspLotRMData.Lot.NAME.getName()}, new Object[]{lotName},
                dataLotFlds);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }
        if (LPNulls.replaceNull(lotInfo[0][3].toString()).length() > 0) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_ALL_BULKS_ALREADY_HAS_DECISION, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }
        String materialName = LPNulls.replaceNull(lotInfo[0][2]).toString();
        String[] dataLotBulkFlds = new String[]{TblsInspLotRMData.LotBulk.BULK_ID.getName(), TblsInspLotRMData.LotBulk.DECISION.getName(),
            TblsInspLotRMData.LotBulk.QUANTITY.getName(), TblsInspLotRMData.LotBulk.QUANTITY_UOM.getName()};
        Object[][] lotBulksInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(),
                new String[]{TblsInspLotRMData.LotBulk.LOT_NAME.getName()}, new Object[]{lotName},
                dataLotBulkFlds);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotBulksInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_WITH_NO_BULKS, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }
        if (Boolean.FALSE.equals(decision.toUpperCase().contains("REJECT"))) {
            Integer bulksNoDecision = 0;

            for (Object[] curBulk : lotBulksInfo) {
                if (LPNulls.replaceNull(curBulk[1]).toString().length() == 0) {
                    bulksNoDecision++;
                }
            }
            if (bulksNoDecision > 0) {
                return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_BULKS_WITH_NO_DECISION, new Object[]{bulksNoDecision, lotBulksInfo.length, lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, null);
            }
            InternalMessage lotAndBulksQuantityMatch = lotAndBulksQuantityMatch(lotName, materialName, Double.valueOf(LPNulls.replaceNull(lotInfo[0][4]).toString()), LPNulls.replaceNull(lotInfo[0][5]).toString(), dataLotBulkFlds, lotBulksInfo);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotAndBulksQuantityMatch.getDiagnostic())) {
                return lotAndBulksQuantityMatch;
            }
            String templateName = lotInfo[0][LPArray.valuePosicInArray(dataLotFlds, TblsInspLotRMData.Lot.LOT_CONFIG_NAME.getName())].toString();
            Integer templateVersion = Integer.valueOf(lotInfo[0][LPArray.valuePosicInArray(dataLotFlds, TblsInspLotRMData.Lot.LOT_CONFIG_VERSION.getName())].toString());

            Object[][] configLotDecisionInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsInspLotRMConfig.TablesInspLotRMConfig.LOT_DECISION_RULES.getTableName(),
                    new String[]{TblsInspLotRMConfig.LotDecisionRules.CODE.getName(), TblsInspLotRMConfig.LotDecisionRules.CODE_VERSION.getName()}, new Object[]{templateName, templateVersion},
                    configLotDecisionFlds);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(configLotDecisionInfo[0][0].toString())) {
                return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
            }

            // , String template, Integer templateVersion
            InternalMessage diagn = decisionTypePasses(lotName, decision, dataLotFlds, lotInfo[0], configLotDecisionFlds, configLotDecisionInfo[0], true);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn.getDiagnostic())) {
                return diagn;
            }
            /*
            if (decision.toString().toUpperCase().contains("ACCEPT")){
                SamplingPlanEntry spEntry=new SamplingPlanEntry(lotInfo[0][0].toString(), lotInfo[0][1].toString(), Integer.valueOf(lotInfo[0][2].toString()), 
                    lotQuantity, Integer.valueOf(lotInfo[0][4].toString()));
                if (spEntry.getHasErrors())
                    return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.SAMPLEPLAN_CHECKER_ERROR, new Object[]{Arrays.toString(spEntry.getErrorsArr())});
                applySamplingPlan(lotName, lotInfo[0][0].toString(), lotInfo[0][1].toString(), Integer.valueOf(lotInfo[0][2].toString()), 
                    lotQuantity, Integer.valueOf(lotInfo[0][4].toString()),
                        null, null, spEntry, bulkId);
            }
             */
//            diagn=sampleAndTestCheck(lotName, templateName, templateVersion, decision, dataLotFlds, lotInfo[0], configLotDecisionFlds, configLotDecisionInfo[0]);
//            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn.getDiagnostic())) return diagn;
            diagn = DataBulk.applyBulkSamplesSamplingPlan(lotName, materialName, decision);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn.getDiagnostic())) {
                return diagn;
            }
        }
        InternalMessage lotDecisionRecordCreateOrUpdate = lotDecisionRecordCreateOrUpdate(lotName, decision, true);
        if (decision.toUpperCase().contains("REJECT")) {
            InternalMessage lotClousureDiagn = DataInspectionLot.lotClousure(lotName, InspectionLotRMClousureTypes.BULK_INSPECTION_REJECTED);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotClousureDiagn.getDiagnostic())) {
                return lotClousureDiagn;
            }
        }
        return lotDecisionRecordCreateOrUpdate;
    }

    public InternalMessage lotTakeDecision(String lotName, String decision, String[] fieldName, Object[] fieldValue) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] dataLotFlds = new String[]{TblsInspLotRMData.Lot.LOT_CONFIG_NAME.getName(), TblsInspLotRMData.Lot.LOT_CONFIG_VERSION.getName()};
        String[] configLotDecisionFlds = EnumIntTableFields.getAllFieldNames(TblsInspLotRMConfig.TablesInspLotRMConfig.LOT_DECISION_RULES.getTableFields());

        Object[][] lotInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(),
                new String[]{TblsInspLotRMData.Lot.NAME.getName()}, new Object[]{lotName},
                dataLotFlds);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }

        String templateName = lotInfo[0][LPArray.valuePosicInArray(dataLotFlds, TblsInspLotRMData.Lot.LOT_CONFIG_NAME.getName())].toString();
        Integer templateVersion = Integer.valueOf(lotInfo[0][LPArray.valuePosicInArray(dataLotFlds, TblsInspLotRMData.Lot.LOT_CONFIG_VERSION.getName())].toString());

        Object[][] configLotDecisionInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsInspLotRMConfig.TablesInspLotRMConfig.LOT_DECISION_RULES.getTableName(),
                new String[]{TblsInspLotRMConfig.LotDecisionRules.CODE.getName(), TblsInspLotRMConfig.LotDecisionRules.CODE_VERSION.getName()}, new Object[]{templateName, templateVersion},
                configLotDecisionFlds);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(configLotDecisionInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }

        // , String template, Integer templateVersion
        InternalMessage diagn = decisionTypePasses(lotName, decision, dataLotFlds, lotInfo[0], configLotDecisionFlds, configLotDecisionInfo[0], false);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn.getDiagnostic())) {
            return diagn;
        }

        diagn = sampleAndTestCheck(lotName, templateName, templateVersion, decision, dataLotFlds, lotInfo[0], configLotDecisionFlds, configLotDecisionInfo[0]);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn.getDiagnostic())) {
            return diagn;
        }

        return lotDecisionRecordCreateOrUpdate(lotName, decision, false);
    }

    public InternalMessage decisionTypePasses(String lotName, String decision, String[] dataLotFlds, Object[] lotInfo, String[] configLotDecFlds, Object[] configLotDecInfo, Boolean isBulk) {
        String decisionsList = null;
        if (Boolean.TRUE.equals(isBulk)) {
            decisionsList = configLotDecInfo[LPArray.valuePosicInArray(configLotDecFlds, TblsInspLotRMConfig.LotDecisionRules.BULK_DECISIONS_LIST.getName())].toString();
        } else {
            decisionsList = configLotDecInfo[LPArray.valuePosicInArray(configLotDecFlds, TblsInspLotRMConfig.LotDecisionRules.DECISIONS_LIST.getName())].toString();
        }
        if (decisionsList == null || decisionsList.length() == 0) {
            return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.DataInspLotErrorTrapping.NO_DECISION_LIST_DEFINED, null);
        }
        if (LPArray.valueInArray(decisionsList.split("\\|"), decision)) {
            return new InternalMessage(LPPlatform.LAB_TRUE, Rdbms.RdbmsSuccess.RDBMS_TABLE_FOUND, null);
        } else {
            return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_DECISION_NOT_ACCEPTED_VALUE, new Object[]{decision, Arrays.toString(decisionsList.split("\\|")), lotName});
        }
    }

    public InternalMessage sampleAndTestCheck(String lotName, String templateName, Integer templateVersion, String decision, String[] dataLotFlds, Object[] lotInfo, String[] configLotDecFlds, Object[] configLotDecInfo) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String testRevisionRequired = configLotDecInfo[LPArray.valuePosicInArray(configLotDecFlds, TblsInspLotRMConfig.LotDecisionRules.SAMPLE_ANALYSIS_REVISION_REQUIRED.getName())].toString();
        String sampleRevisionRequired = configLotDecInfo[LPArray.valuePosicInArray(configLotDecFlds, TblsInspLotRMConfig.LotDecisionRules.SAMPLE_REVISION_REQUIRED.getName())].toString();
        if ((testRevisionRequired == null || Boolean.FALSE.equals(Boolean.valueOf(testRevisionRequired))) && (sampleRevisionRequired == null || Boolean.FALSE.equals(Boolean.valueOf(sampleRevisionRequired)))) {
            return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.DataInspLotErrorTrapping.NO_DECISION_LIST_DEFINED, null);
        } else {
            String[] sampleAndSampleAnalysisFlds = new String[]{TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_ID.getName(), TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_STATUS.getName(), TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.TEST_ID.getName(), TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_ANALYSIS_STATUS.getName()};
            String sampleStatusReviewed = sampleStatusReviewed = DataSampleStructureStatuses.SampleStatuses.REVIEWED.getStatusCode("");

            Object[][] sampleAndSampleAnalysisInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.ViewsInspLotRMData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW.getViewName(),
                    new String[]{TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.LOT_NAME.getName()}, new Object[]{lotName},
                    sampleAndSampleAnalysisFlds);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAndSampleAnalysisInfo[0][0].toString())) {
                return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
            }
            if (Boolean.TRUE.equals(Boolean.valueOf(testRevisionRequired))) {
                Object[] sampleAnalysisStatuses = LPArray.getColumnFromArray2D(sampleAndSampleAnalysisInfo, LPArray.valuePosicInArray(sampleAndSampleAnalysisFlds, TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_ANALYSIS_STATUS.getName()));
                for (Object curSmpAnaStatus : sampleAnalysisStatuses) {
                    if (curSmpAnaStatus == null || curSmpAnaStatus.toString().length() == 0) {
                        return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_HAS_ONE_SAMPLE_ANALYSIS_WITH_NO_STATUS, null);
                    }
                    if (Boolean.FALSE.equals(sampleStatusReviewed.equalsIgnoreCase(curSmpAnaStatus.toString()))) {
                        return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_HAS_NOTREVIEWED_SAMPLEANALYSIS, new Object[]{lotName, procInstanceName});
                    }
                }
            }
            if (Boolean.TRUE.equals(Boolean.valueOf(sampleRevisionRequired))) {
                Object[] sampleStatuses = LPArray.getColumnFromArray2D(sampleAndSampleAnalysisInfo, LPArray.valuePosicInArray(sampleAndSampleAnalysisFlds, TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.SAMPLE_STATUS.getName()));
                for (Object curSmpStatus : sampleStatuses) {
                    if (curSmpStatus == null || curSmpStatus.toString().length() == 0) {
                        return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_HAS_ONE_SAMPLE_ANALYSIS_WITH_NO_STATUS, null);
                    }
                    if (Boolean.FALSE.equals(sampleStatusReviewed.equalsIgnoreCase(curSmpStatus.toString()))) {
                        return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_HAS_NOTREVIEWED_SAMPLE, null);
                    }
                }
            }
            return new InternalMessage(LPPlatform.LAB_TRUE, Rdbms.RdbmsSuccess.RDBMS_TABLE_FOUND, null);
        }
        //return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "", null);
    }

    public static InternalMessage setLotReadyForRevision(String lotName) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        InternalMessage readyForRevision = isReadyForRevision(lotName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(readyForRevision.getDiagnostic())) {
            return readyForRevision;
        }

        String[] lotFieldName = new String[]{TblsInspLotRMData.Lot.READY_FOR_REVISION.getName()};
        Object[] lotFieldValue = new Object[]{true};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInspLotRMData.Lot.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");

        RdbmsObject diagnoses = Rdbms.updateTableRecordFieldsByFilter(TblsInspLotRMData.TablesInspLotRMData.LOT,
                EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMData.TablesInspLotRMData.LOT, lotFieldName), lotFieldValue, sqlWhere, null);
        if (Boolean.FALSE.equals(diagnoses.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnoses.getErrorMessageCode(), diagnoses.getErrorMessageVariables());
        }

        LotAudit lotAudit = new LotAudit();
        lotAudit.lotAuditAdd(InspectionLotRMAuditEvents.LOT_SET_READY_FOR_REVISION,
                TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName, lotName, lotFieldName, lotFieldValue);

        return new InternalMessage(LPPlatform.LAB_TRUE, Rdbms.RdbmsSuccess.RDBMS_TABLE_FOUND, null);
    }

    public static InternalMessage isReadyForRevision(String lotName) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] whereFieldName = new String[]{TblsInspLotRMData.Sample.LOT_NAME.getName()};
        Object[] whereFieldValue = new Object[]{lotName};
        Object[][] grouper = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableName(),
                new String[]{TblsInspLotRMData.Sample.STATUS.getName()}, whereFieldName, whereFieldValue, null);
        if (grouper.length != 1) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleStructureEnums.DataSampleAnalysisErrorTrapping.PENDING_REVISION, null);
        }
        if (Boolean.FALSE.equals(grouper[0][0].toString().equalsIgnoreCase(SampleStatuses.REVIEWED.getStatusCode("")))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleStructureEnums.DataSampleAnalysisErrorTrapping.PENDING_REVISION, null);
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, "", null, null);
    }

    public InternalMessage lotDecisionRecordCreateOrUpdate(String lotName, String decision, Boolean forBulks) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String[] lotFieldName = new String[]{};
        Object[] lotFieldValue = new Object[]{};
        Object[] errorDetailVariables = new Object[]{};
        EnumIntTables tblObj = null;
        EnumIntEndpoints endpointObj = null;
        EnumIntAuditEvents auditEvObj = null;
        SqlWhere sqlWhere = new SqlWhere();
        if (Boolean.FALSE.equals(forBulks)) {
            sqlWhere.addConstraint(TblsInspLotRMData.LotDecision.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
            tblObj = TblsInspLotRMData.TablesInspLotRMData.LOT_DECISION;
            endpointObj = InspLotRMAPIactionsEndpoints.LOT_TAKE_USAGE_DECISION;
            if (decision != null && decision.length() > 0) {
                lotFieldName = LPArray.addValueToArray1D(lotFieldName, new String[]{TblsInspLotRMData.LotDecision.DECISION.getName(), TblsInspLotRMData.LotDecision.DECISION_TAKEN_BY.getName(), TblsInspLotRMData.LotDecision.DECISION_TAKEN_ON.getName()});
                lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, new Object[]{decision, token.getPersonName(), LPDate.getCurrentTimeStamp()});
            }
            auditEvObj = InspectionLotRMAuditEvents.LOT_USAGE_DECISION_TAKEN;
        } else {
            tblObj = TblsInspLotRMData.TablesInspLotRMData.LOT;
            endpointObj = InspLotRMAPIactionsEndpoints.LOT_ALL_BULKS_TAKE_DECISION;
            if (decision != null && decision.length() > 0) {
                lotFieldName = LPArray.addValueToArray1D(lotFieldName, new String[]{TblsInspLotRMData.Lot.BULK_DECISION.getName(), TblsInspLotRMData.Lot.BULK_DECISION_BY.getName(), TblsInspLotRMData.Lot.BULK_DECISION_ON.getName()});
                lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, new Object[]{decision, token.getPersonName(), LPDate.getCurrentTimeStamp()});
            }
            auditEvObj = InspectionLotRMAuditEvents.LOT_ALL_BULKS_DECISION_TAKEN;
            sqlWhere.addConstraint(TblsInspLotRMData.Lot.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
        }

        Object[] lotExists = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT_DECISION.getTableName(),
                new String[]{TblsInspLotRMData.LotDecision.LOT_NAME.getName()}, new Object[]{lotName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(lotExists[0].toString())) {
            RdbmsObject diagnoses = Rdbms.updateTableRecordFieldsByFilter(tblObj,
                    EnumIntTableFields.getTableFieldsFromString(tblObj, lotFieldName), lotFieldValue, sqlWhere, null);
            if (Boolean.FALSE.equals(diagnoses.getRunSuccess())) {
                return new InternalMessage(LPPlatform.LAB_FALSE, diagnoses.getErrorMessageCode(), diagnoses.getErrorMessageVariables());
            }

        } else {
            lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.LotDecision.LOT_NAME.getName());
            lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, lotName);
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(tblObj, lotFieldName, lotFieldValue);
            if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, insertRecordInTable.getNewRowId());
                return new InternalMessage(LPPlatform.LAB_FALSE, DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD, errorDetailVariables);
            }
        }
        if (decision != null && decision.length() > 0) {
            LotAudit lotAudit = new LotAudit();
            lotAudit.lotAuditAdd(auditEvObj,
                    tblObj.getTableName(), lotName, lotName, lotFieldName, lotFieldValue);
            return new InternalMessage(LPPlatform.LAB_TRUE, endpointObj, new Object[]{lotName, decision, procInstanceName});
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, Rdbms.RdbmsSuccess.RDBMS_TABLE_FOUND, null);
    }

    public InternalMessage lotAndBulksQuantityMatch(String lotName, String materialName, Double lotQuantity, String lotQuantityUom, String[] dataLotBulkFlds, Object[][] lotBulksInfo) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = procReqSession.getProcedureInstance();
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());

        Object[][] materialInfo = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL.getTableName(),
                new String[]{TblsInspLotRMConfig.Material.NAME.getName()}, new Object[]{materialName},
                new String[]{TblsInspLotRMConfig.Material.NAME.getName(), TblsInspLotRMConfig.Material.CHECK_LOT_AND_BULKS_QUANT_MATCHES.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }
        if (Boolean.FALSE.equals(Boolean.valueOf(LPNulls.replaceNull(materialInfo[0][1]).toString()))) {
            return new InternalMessage(LPPlatform.LAB_TRUE, DataInspLotErrorTrapping.DISABLED, null);
        }
        Double bulksTotalQuantity = Double.valueOf("0");
        for (Object[] curBulk : lotBulksInfo) {
            String decision = LPNulls.replaceNull(curBulk[LPArray.valuePosicInArray(dataLotBulkFlds, TblsInspLotRMData.LotBulk.DECISION.getName())]).toString();
            if (Boolean.FALSE.equals(decision.toUpperCase().contains("REJECT"))) {
                Double bulkQ = Double.valueOf(LPNulls.replaceNull(curBulk[LPArray.valuePosicInArray(dataLotBulkFlds, TblsInspLotRMData.LotBulk.QUANTITY.getName())]).toString());
                String bulkUom = LPNulls.replaceNull(curBulk[LPArray.valuePosicInArray(dataLotBulkFlds, TblsInspLotRMData.LotBulk.QUANTITY_UOM.getName())]).toString();
                if (lotQuantityUom.equalsIgnoreCase(bulkUom)) {
                    bulksTotalQuantity = bulksTotalQuantity + bulkQ;
                } else {
                    UnitsOfMeasurement uom = new UnitsOfMeasurement(BigDecimal.valueOf(bulkQ), bulkUom);
                    uom.convertValue(lotQuantityUom);
                    if (Boolean.FALSE.equals(uom.getConvertedFine())) {
                        return new InternalMessage(LPPlatform.LAB_FALSE, InventoryGlobalVariables.DataInvRetErrorTrapping.CONVERTER_FALSE, new Object[]{lotName, uom.getConversionErrorDetail()[3].toString(), procReqSession.getProcedureInstance()});
                    }
                    bulksTotalQuantity = bulksTotalQuantity + uom.getConvertedQuantity().doubleValue();
                }
            }
        }
        if (lotQuantity.equals(bulksTotalQuantity)) {
            return new InternalMessage(LPPlatform.LAB_TRUE, Rdbms.RdbmsSuccess.RDBMS_TABLE_FOUND, null);
        } else {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataInspLotErrorTrapping.LOTQUANTITY_AND_ACCEPTEDBULKSQUANTITY_NOTMATCHING, new Object[]{lotName, lotQuantity, bulksTotalQuantity});
        }
    }
}
