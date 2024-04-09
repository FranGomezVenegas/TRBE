/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.inventory.batch;

import module.monitoring.definition.TblsEnvMonitConfig;
import module.monitoring.definition.TblsEnvMonitData;
import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import functionaljavaa.audit.IncubBatchAudit;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.inventory.batch.DataBatchIncubator.IncubatorBatchErrorTrapping;
import functionaljavaa.inventory.batch.DataBatchIncubator.IncubatorBatchSuccess;
import functionaljavaa.samplestructure.DataSampleIncubation;
import java.math.BigDecimal;
import java.util.Objects;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public final class DataBatchIncubatorStructured {

    private DataBatchIncubatorStructured() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String BTCHCNTNT_SEPRTRSTRUCTUREDBATCH = "<>";
    public static final String POSIT_VAL_SEPARTRSTRUCTUREDBATCH = "*";
    public static final String BATCHCONTENTEMPTYPOSITIONVALUE = "-";

    static Boolean batchIsEmptyStructured(String batchName) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(),
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName},
                new String[]{TblsEnvMonitData.IncubBatch.STRUCT_TOTAL_OBJECTS.getName()});
        return (!LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(batchInfo[0][0]).toString())) && ("0".equals(LPNulls.replaceNull(batchInfo[0][0]).toString()));
    }

    public static Object[][] dbGetBatchArray(String batchName) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        return Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(),
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName},
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName(), TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_VERSION.getName(),
                     TblsEnvMonitData.IncubBatch.STRUCT_NUM_ROWS.getName(), TblsEnvMonitData.IncubBatch.STRUCT_NUM_COLS.getName(),
                     TblsEnvMonitData.IncubBatch.STRUCT_ROWS_NAME.getName(), TblsEnvMonitData.IncubBatch.STRUCT_COLS_NAME.getName(),
                     TblsEnvMonitData.IncubBatch.STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.STRUCT_TOTAL_POSITIONS.getName()});
    }

    static InternalMessage createBatchStructured(String bName, Integer bTemplateId, Integer bTemplateVersion, String[] fldName, Object[] fldValue) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        if (fldName == null) {
            fldName = new String[0];
        }
        if (fldValue == null) {
            fldValue = new Object[0];
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_ID.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_ID.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, bTemplateId);
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_ID.getName())] = bTemplateId;
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_VERSION.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_VERSION.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, bTemplateVersion);
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_VERSION.getName())] = bTemplateVersion;
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.NAME.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.NAME.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, bName);
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.NAME.getName())] = bName;
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.TYPE.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.TYPE.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, DataBatchIncubator.BatchIncubatorType.STRUCTURED.toString());
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.TYPE.getName())] = DataBatchIncubator.BatchIncubatorType.STRUCTURED.toString();
        }
        String[] templateFldsToPropagate = new String[]{TblsEnvMonitConfig.IncubBatch.STRUCT_NUM_ROWS.getName(), TblsEnvMonitConfig.IncubBatch.STRUCT_NUM_COLS.getName(),
             TblsEnvMonitConfig.IncubBatch.STRUCT_TOTAL_POSITIONS.getName(), TblsEnvMonitConfig.IncubBatch.STRUCT_ROWS_NAME.getName(), TblsEnvMonitConfig.IncubBatch.STRUCT_COLS_NAME.getName()};
        Object[][] templateDefInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INCUB_BATCH.getTableName(),
                new String[]{TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_VERSION.getName()},
                new Object[]{bTemplateId, bTemplateVersion}, templateFldsToPropagate);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(templateDefInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{bTemplateId, bTemplateVersion});
        }
        for (int i = 0; i < templateFldsToPropagate.length; i++) {
            fldName = LPArray.addValueToArray1D(fldName, templateFldsToPropagate[i]);
            fldValue = LPArray.addValueToArray1D(fldValue, templateDefInfo[0][i]);
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.ACTIVE.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.ACTIVE.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, true);
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.STRUCT_TOTAL_OBJECTS.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.STRUCT_TOTAL_OBJECTS.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, 0);
        }
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH, fldName, fldValue);
        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
            IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.DataBatchAuditEvents.BATCH_CREATED.toString(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), bName, LPArray.joinTwo1DArraysInOneOf1DString(fldName, fldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        }
        return new InternalMessage(insertRecordInTable.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());
    }

    static InternalMessage batchAddSampleStructured(String batchName, Integer sampleId, Integer pendingIncubationStage, Integer row, Integer col, Boolean override) {
        return batchAddSampleStructured(batchName, sampleId, pendingIncubationStage, row, col, override, false);
    }

    static InternalMessage batchAddSampleStructured(String batchName, Integer sampleId, Integer pendingIncubationStage, Integer row, Integer col, Boolean override, Boolean byMovement) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        if ((row == null) || (col == null)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STRUCTURED_BATCH_WRONGDIMENSION, new Object[]{LPNulls.replaceNull(row), LPNulls.replaceNull(col)});
        }
        InternalMessage batchSampleIsAddable = batchSampleIsAddable(batchName, sampleId, pendingIncubationStage, row, col, override, byMovement);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchSampleIsAddable.getDiagnostic())) {
            return batchSampleIsAddable;
        }

        String[] batchFldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.STRUCT_NUM_ROWS.getName(), TblsEnvMonitData.IncubBatch.STRUCT_NUM_COLS.getName(),
             TblsEnvMonitData.IncubBatch.STRUCT_TOTAL_POSITIONS.getName(), TblsEnvMonitData.IncubBatch.STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.STRUCT_CONTENT.getName()};
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(),
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()},
                new Object[]{batchName}, batchFldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{batchName});
        }
        Integer batchNumRows = (Integer) batchInfo[0][0];
        Integer batchNumCols = (Integer) batchInfo[0][1];
        Integer batchTotalObjects = LPNulls.replaceNull(batchInfo[0][3]).toString().length() == 0 ? 0 : Integer.valueOf(batchInfo[0][3].toString());
        String batchContentStr = batchInfo[0][4].toString();

        String[][] batchContent2D = new String[0][0];
        if ((batchContentStr == null) || (batchContentStr.length() == 0)) {
            batchContent2D = new String[batchNumRows][0];
            for (int i = 0; i < batchNumCols; i++) {
                batchContent2D = LPArray.convertObjectArrayToStringArray(LPArray.addColumnToArray2D(batchContent2D, BATCHCONTENTEMPTYPOSITIONVALUE));
            }
        } else {
            String[] batchContent1D = batchContentStr.split(BTCHCNTNT_SEPRTRSTRUCTUREDBATCH);
            batchContent2D = LPArray.array1dTo2d(batchContent1D, batchNumCols);
        }

        batchContent2D[row - 1][col - 1] = buildBatchPositionValue(sampleId, pendingIncubationStage);
        batchContentStr = LPArray.convertArrayToString(LPArray.array2dTo1d(batchContent2D), BTCHCNTNT_SEPRTRSTRUCTUREDBATCH, "");
        if (byMovement != null && !byMovement) {
            batchTotalObjects++;
        }
        String[] updFieldName = new String[]{TblsEnvMonitData.IncubBatch.STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.STRUCT_CONTENT.getName()};
        Object[] updFieldValue = new Object[]{batchTotalObjects, batchContentStr};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsEnvMonitData.IncubBatch.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{batchName}, "");
        Object[] updateBatchContentDiagn = Rdbms.updateRecordFieldsByFilter(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH,
                EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH, updFieldName), updFieldValue, sqlWhere, null);
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(updateBatchContentDiagn[0].toString()))) {
            if (byMovement != null && !byMovement) {
                IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.DataBatchAuditEvents.BATCH_SAMPLE_ADDED.toString(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
            } else {
                IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.DataBatchAuditEvents.BATCH_SAMPLE_MOVED.toString(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
            }
        }
        String batchFldName = "";
        if (null == pendingIncubationStage) {
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STAGE_NOT_RECOGNIZED, new Object[]{pendingIncubationStage, procInstanceName});
        } else {
            switch (pendingIncubationStage) {
                case 1:
                    batchFldName = TblsEnvMonitData.Sample.INCUBATION_BATCH.getName();
                    break;
                case 2:
                    batchFldName = TblsEnvMonitData.Sample.INCUBATION2_BATCH.getName();
                    break;
                default:
                    return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STAGE_NOT_RECOGNIZED, new Object[]{pendingIncubationStage, procInstanceName});
            }
        }
        sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsEnvMonitData.Sample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
        RdbmsObject updateSampleInfo = Rdbms.updateTableRecordFieldsByFilter(TblsEnvMonitData.TablesEnvMonitData.SAMPLE,
                EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.SAMPLE, new String[]{batchFldName}), new Object[]{batchName}, sqlWhere, null);
        if (Boolean.TRUE.equals(updateSampleInfo.getRunSuccess())) {
            SampleAudit smpAudit = new SampleAudit();
            if (byMovement != null && !byMovement) {
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.BATCH_SAMPLE_ADDED, TblsData.TablesData.SAMPLE.getTableName(), sampleId, sampleId, null, null, updFieldName, updFieldValue);
            } else {
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.BATCH_SAMPLE_MOVED_TO, TblsData.TablesData.SAMPLE.getTableName(), sampleId, sampleId, null, null, updFieldName, updFieldValue);
            }
        }
        return new InternalMessage(updateSampleInfo.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, updateSampleInfo.getErrorMessageCode(), updateSampleInfo.getErrorMessageVariables());
    }

    private static InternalMessage batchSampleIsAddable(String batchName, Integer sampleId, Integer pendingIncubationStage, Integer row, Integer col, Boolean override, Boolean byMovement) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] batchFldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.STRUCT_NUM_ROWS.getName(), TblsEnvMonitData.IncubBatch.STRUCT_NUM_COLS.getName(),
             TblsEnvMonitData.IncubBatch.STRUCT_TOTAL_POSITIONS.getName(), TblsEnvMonitData.IncubBatch.STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.STRUCT_CONTENT.getName()};
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(),
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()},
                new Object[]{batchName}, batchFldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{batchName});
        }
        Integer batchNumRows = (Integer) batchInfo[0][0];
        Integer batchNumCols = (Integer) batchInfo[0][1];
        String batchContentStr = batchInfo[0][4].toString();

        String[][] batchContent2D = new String[0][0];
        if (row > batchNumRows) {
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STRUCTURED_POSITION_OVER_DIMENSION,
                    new Object[]{"row", row, "row", batchNumRows, batchName, procInstanceName});
        }
        if (col > batchNumCols) {
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STRUCTURED_POSITION_OVER_DIMENSION,
                    new Object[]{"col", col, "col", batchNumCols, batchName, procInstanceName});
        }
        if ((batchContentStr == null) || (batchContentStr.length() == 0)) {
            batchContent2D = new String[batchNumRows][0];
            for (int i = 0; i < batchNumCols; i++) {
                batchContent2D = LPArray.convertObjectArrayToStringArray(LPArray.addColumnToArray2D(batchContent2D, BATCHCONTENTEMPTYPOSITIONVALUE));
            }
        } else {
            String[] batchContent1D = batchContentStr.split(BTCHCNTNT_SEPRTRSTRUCTUREDBATCH);
            batchContent2D = LPArray.array1dTo2d(batchContent1D, batchNumCols);
        }

        String posicContent = batchContent2D[row - 1][col - 1];
        if ((LPNulls.replaceNull(posicContent).length() > 0) && (!BATCHCONTENTEMPTYPOSITIONVALUE.equalsIgnoreCase(LPNulls.replaceNull(posicContent)))
                && (Boolean.FALSE.equals(override))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STRUCTURED_BATCH_POSITIONOCCUPIED, new Object[]{row, col, batchName, procInstanceName});
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
    }

    static InternalMessage batchMoveSampleStructured(String batchName, Integer sampleId, Integer pendingIncubationStage, Integer newRow, Integer newCol, Boolean override) {
        InternalMessage sampleAddable = batchSampleIsAddable(batchName, sampleId, pendingIncubationStage, newRow, newCol, override, true);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAddable.getDiagnostic())) {
            return sampleAddable;
        }
        InternalMessage moveDiagn = batchRemoveSampleStructured(batchName, sampleId, pendingIncubationStage, true);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(moveDiagn.getDiagnostic())) {
            return moveDiagn;
        }
        return batchAddSampleStructured(batchName, sampleId, pendingIncubationStage, newRow, newCol, override, true);
    }

    static InternalMessage batchRemoveSampleStructured(String batchName, Integer sampleId, Integer pendingIncubationStage) {
        return batchRemoveSampleStructured(batchName, sampleId, pendingIncubationStage, false);
    }

    static InternalMessage batchRemoveSampleStructured(String batchName, Integer sampleId, Integer pendingIncubationStage, Boolean byMovement) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] batchFldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.STRUCT_NUM_COLS.getName(), TblsEnvMonitData.IncubBatch.STRUCT_TOTAL_POSITIONS.getName(),
            TblsEnvMonitData.IncubBatch.STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.STRUCT_CONTENT.getName()};
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(),
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()},
                new Object[]{batchName}, batchFldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{batchName});
        }
        Integer batchNumCols = (Integer) batchInfo[0][0];
        Integer batchTotalObjects = LPNulls.replaceNull(batchInfo[0][2]).toString().length() == 0 ? 0 : Integer.valueOf(batchInfo[0][2].toString());
        String batchContentStr = batchInfo[0][3].toString();
        String positionValueToFind = buildBatchPositionValue(sampleId, pendingIncubationStage);

        if ((batchContentStr == null) || (batchContentStr.length() == 0)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.EMPTY_BATCH, new Object[]{batchName, procInstanceName});
        }

        String[] batchContent1D = batchContentStr.split(BTCHCNTNT_SEPRTRSTRUCTUREDBATCH);
        Integer valuePosition = LPArray.valuePosicInArray(batchContent1D, positionValueToFind);
        if (valuePosition == -1) {
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.SAMPLE_NOTFOUND_IN_BATCH, new Object[]{sampleId, batchName, procInstanceName});
        }
        batchContent1D[valuePosition] = "";
        String[][] batchContent2D = LPArray.array1dTo2d(batchContent1D, batchNumCols);
        batchContentStr = LPArray.convertArrayToString(LPArray.array2dTo1d(batchContent2D), BTCHCNTNT_SEPRTRSTRUCTUREDBATCH, "");
        if (byMovement != null && !byMovement) {
            batchTotalObjects--;
        }
        String[] updFieldName = new String[]{TblsEnvMonitData.IncubBatch.STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.STRUCT_CONTENT.getName()};
        Object[] updFieldValue = new Object[]{batchTotalObjects, batchContentStr};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsEnvMonitData.IncubBatch.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{batchName}, "");
        RdbmsObject updateBatchContentDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH,
                EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH, updFieldName), updFieldValue, sqlWhere, null);
        if (Boolean.FALSE.equals(updateBatchContentDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateBatchContentDiagn.getErrorMessageCode(), updateBatchContentDiagn.getErrorMessageVariables());
        }
        if (byMovement == null || !byMovement) {
            IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.DataBatchAuditEvents.BATCH_SAMPLE_REMOVED.toString(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        }

        String batchFldName = "";
        if (null == pendingIncubationStage) {
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STAGE_NOT_RECOGNIZED, new Object[]{pendingIncubationStage, procInstanceName});
        } else {
            switch (pendingIncubationStage) {
                case 1:
                    batchFldName = TblsEnvMonitData.Sample.INCUBATION_BATCH.getName();
                    break;
                case 2:
                    batchFldName = TblsEnvMonitData.Sample.INCUBATION2_BATCH.getName();
                    break;
                default:
                    return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STAGE_NOT_RECOGNIZED, new Object[]{pendingIncubationStage, procInstanceName});
            }
        }
        sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsEnvMonitData.Sample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
        RdbmsObject updateSampleInfo = Rdbms.updateTableRecordFieldsByFilter(TblsEnvMonitData.TablesEnvMonitData.SAMPLE,
                EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.SAMPLE, new String[]{batchFldName}), new Object[]{null}, sqlWhere, null);

        if (Boolean.TRUE.equals(updateSampleInfo.getRunSuccess())) {
            SampleAudit smpAudit = new SampleAudit();
            if (byMovement != null && !byMovement) {
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.BATCH_SAMPLE_REMOVED, TblsData.TablesData.SAMPLE.getTableName(),
                        sampleId, sampleId, null, null, updFieldName, updFieldValue);
            } else {
                updFieldName = LPArray.addValueToArray1D(updFieldName, "row");
                updFieldName = LPArray.addValueToArray1D(updFieldName, "col");
                updFieldValue = LPArray.addValueToArray1D(updFieldValue, (valuePosition / batchNumCols) + 1);
                updFieldValue = LPArray.addValueToArray1D(updFieldValue, (valuePosition % batchNumCols) + 1);
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.BATCH_SAMPLE_MOVED_FROM, TblsData.TablesData.SAMPLE.getTableName(),
                        sampleId, sampleId, null, null, updFieldName, updFieldValue);
            }
        }
        return new InternalMessage(updateSampleInfo.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, updateSampleInfo.getErrorMessageCode(), updateSampleInfo.getErrorMessageVariables());
    }

    static InternalMessage batchSampleIncubStartedStructured() {
        return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
    }

    static InternalMessage batchSampleIncubStartedStructured(String batchName, String incubName) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.STRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{batchName});
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        if (batchSamples.length() == 0) {
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.EMPTY_BATCH, new Object[]{batchName, procInstanceName});
        }
        String[] batchSamplesArr = batchSamples.split("\\|");
        for (String currSample : batchSamplesArr) {
            String[] currSampleArr = currSample.split("\\*");
            if (currSampleArr.length != 2) {
                return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.PARSE_ERROR_STRUCTUREDBATCH, new Object[]{"Starting Batch", currSample, batchSamples, procInstanceName});
            }
            Integer sampleId = Integer.valueOf(currSampleArr[0]);
            Integer incubStage = Integer.valueOf(currSampleArr[1]);
            BigDecimal tempReading = null;
            InternalMessage setSampleIncubStarted = DataSampleIncubation.setSampleStartIncubationDateTime(sampleId, incubStage, incubName, tempReading, batchName);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(setSampleIncubStarted.getDiagnostic())) {
                return setSampleIncubStarted;
            }
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, IncubatorBatchSuccess.SAMPLES_IN_BATCH_SET_AS_BATCHSTARTED, new Object[]{batchName});
    }

    static InternalMessage batchSampleIncubEndedStructured(String batchName, String incubName) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.STRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{batchName});
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        String[] batchSamplesArr = batchSamples.split("\\|");
        for (String currSample : batchSamplesArr) {
            String[] currSampleArr = currSample.split("\\*");
            if (currSampleArr.length != 2) {
                return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.PARSE_ERROR_STRUCTUREDBATCH, new Object[]{"Ending Batch", currSample, batchSamples, procInstanceName});
            }
            Integer sampleId = Integer.valueOf(currSampleArr[0]);
            Integer incubStage = Integer.valueOf(currSampleArr[1]);
            BigDecimal tempReading = null;
            InternalMessage setSampleIncubEnded = DataSampleIncubation.setSampleEndIncubationDateTime(sampleId, incubStage, incubName, tempReading, batchName);
            //setSampleIncubEnded = (Object[]) setSampleIncubEnded[0];
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(setSampleIncubEnded.getDiagnostic())) {
                return setSampleIncubEnded;
            }
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, IncubatorBatchSuccess.SAMPLES_IN_BATCH_SET_AS_BATCHENDED, new Object[]{batchName});
    }

    static InternalMessage batchSampleIncubEndedStructured() {
        return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
    }

    public static String buildBatchPositionValue(Integer sampleId, Integer pendingIncubationStage) {
        if (pendingIncubationStage == null) {
            return sampleId.toString() + POSIT_VAL_SEPARTRSTRUCTUREDBATCH;
        }
        return sampleId.toString() + POSIT_VAL_SEPARTRSTRUCTUREDBATCH + pendingIncubationStage.toString();
    }

    public static String setLinesNameNOUSADO(String[] names, Integer numRows) {
        String valuesSeparator = BTCHCNTNT_SEPRTRSTRUCTUREDBATCH;
        StringBuilder linesName = new StringBuilder(0);
        if (names == null) {
            char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            //Integer numLet=alphabet.length;
            Integer inumLet = 0;
            Integer inumLetAlphabet = 0;
            StringBuilder currPrefixBuilder = new StringBuilder(0);
            //while (inumLet<linesName.length){
            while (inumLet < numRows) {
                if (Objects.equals(inumLet, alphabet.length)) {
                    currPrefixBuilder.append("A");
                    inumLetAlphabet = 0;
                }
                //linesName[inumLet]=currPrefixBuilder.toString()+alphabet[inumLetAlphabet];
                if (linesName.length() > 0) {
                    linesName.append(valuesSeparator);
                }
                linesName.append(currPrefixBuilder.toString()).append(alphabet[inumLetAlphabet]);
                inumLet++;
                inumLetAlphabet++;
            }
        } else {
            for (String name : names) {
                if (linesName.length() > 0) {
                    linesName.append(valuesSeparator);
                }
                linesName.append(name);
            }
//            if (linesName.length==names.length) linesName=names;
        }
        return linesName.toString(); //names;        
    }

    public static String setColumnsNameNOUSADO(String[] names, Integer numCols) {
        //String[] columnsName=new String[numCols];
        String valuesSeparator = BTCHCNTNT_SEPRTRSTRUCTUREDBATCH;
        StringBuilder columnsName = new StringBuilder(0);

        if (names == null) {
            Integer inumLet = 1;
            //while (inumLet<=columnsName.length){                
            while (inumLet <= numCols) {
                if (columnsName.length() > 0) {
                    columnsName.append(valuesSeparator);
                }
                columnsName.append(inumLet);
                //columnsName[inumLet-1]=inumLet.toString();
                inumLet++;
            }
        } else {
            for (String name : names) {
                if (columnsName.length() > 0) {
                    columnsName.append(valuesSeparator);
                }
                columnsName.append(name);
            }
//            if (columnsName.length==names.length) columnsName=names;            
        }
        return columnsName.toString(); //names;
    }

}
