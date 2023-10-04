/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.analysis;

import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.materialspec.DataSpec;
import functionaljavaa.samplestructure.DataSampleAnalysis;
import functionaljavaa.samplestructure.DataSampleAnalysisResultStrategy;
import functionaljavaa.samplestructure.DataSampleStructureStatuses;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class AnalysisCalculations {

    public enum AnalysisCalculationsCollection {
        LOD, SULFATES, SULFURIC_ASHES
    };

    //LOD|Weight after-Weight before
    public static void fireCalcController(Integer resultId, Integer testId, Integer sampleId, String[] resultFlds, Object[] resultVls,
            DataSampleAnalysisResultStrategy sar, String alternativeAuditEntry, String alternativeAuditClass) {
        Integer calcLinkedFldPosicInArray = LPArray.valuePosicInArray(resultFlds, TblsData.SampleAnalysisResult.CALC_LINKED.getName());
        if (calcLinkedFldPosicInArray == -1) {
            return;
        }
        try {
            AnalysisCalculationsCollection selCalcName = AnalysisCalculationsCollection.valueOf(resultVls[calcLinkedFldPosicInArray].toString());
            switch (selCalcName) {
                case LOD:
                    calcLossOnDrying(resultId, testId, sampleId, resultFlds, resultVls, sar, alternativeAuditEntry, alternativeAuditClass);
                    break;
                case SULFATES:
                    calcSulfase(resultId, testId, sampleId, resultFlds, resultVls, sar, alternativeAuditEntry, alternativeAuditClass);
                    break;
                case SULFURIC_ASHES:
                    calcSulfuricAshes(resultId, testId, sampleId, resultFlds, resultVls, sar, alternativeAuditEntry, alternativeAuditClass);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            return;
        }
    }

    public static void calcLossOnDrying(Integer resultId, Integer testId, Integer sampleId, String[] resultFlds, Object[] resultVls,
            DataSampleAnalysisResultStrategy sar, String alternativeAuditEntry, String alternativeAuditClass) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String[] resultFieldsArr = new String[]{TblsData.SampleAnalysisResult.PARAM_NAME.getName(), TblsData.SampleAnalysisResult.RAW_VALUE.getName(),
            TblsData.SampleAnalysisResult.PARAM_TYPE.getName(), TblsData.SampleAnalysisResult.RESULT_ID.getName(),
            TblsData.SampleAnalysisResult.LIMIT_ID.getName(), TblsData.SampleAnalysisResult.STATUS.getName()};
        Object[][] resultData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{testId},
                resultFieldsArr);
        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) {
            return;
        }

        String wBefore = null;
        String wAfter = null;
        Integer calcResultId = null;
        Integer calcResultSpecLimitId = null;
        String currResultStatus = null;
        for (Object[] curRes : resultData) {
            if ((curRes[0].toString().toUpperCase().contains("BEFORE")) || (curRes[0].toString().toUpperCase().contains("WET"))
                    || (curRes[0].toString().toUpperCase().contains("ANTES")) || (curRes[0].toString().toUpperCase().contains("MOJADO"))) {
                wBefore = LPNulls.replaceNull(curRes[1]).toString();
            }
            if ((curRes[0].toString().toUpperCase().contains("AFTER")) || (curRes[0].toString().toUpperCase().contains("DRY"))
                    || (curRes[0].toString().toUpperCase().contains("SECO")) || (curRes[0].toString().toUpperCase().contains("DESPUES"))) {
                wAfter = LPNulls.replaceNull(curRes[1]).toString();
            }
            if (curRes[2].toString().toUpperCase().contains("CALC")) {
                calcResultId = Integer.valueOf(LPNulls.replaceNull(curRes[3]).toString());
                calcResultSpecLimitId = LPNulls.replaceNull(curRes[4]).toString().length() == 0 ? null : Integer.valueOf(LPNulls.replaceNull(curRes[4]).toString());
                currResultStatus = LPNulls.replaceNull(curRes[5]).toString();
            }
        }
        if (LPNulls.replaceNull(wBefore).toString().length() > 0 && LPNulls.replaceNull(wAfter).toString().length() > 0 && LPNulls.replaceNull(calcResultId).toString().length() > 0) {
            BigDecimal initialWeight = BigDecimal.valueOf(Double.valueOf(wBefore));
            BigDecimal finalWeight = BigDecimal.valueOf(Double.valueOf(wAfter));
            BigDecimal loss = initialWeight.subtract(finalWeight);
            BigDecimal resultValue = loss.divide(finalWeight, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

            String newResultStatus = currResultStatus;
            String resultStatusDefault = DataSampleStructureStatuses.SampleAnalysisResultStatuses.getStatusFirstCode();
            String resultStatusCanceled = DataSampleStructureStatuses.SampleAnalysisResultStatuses.CANCELED.getStatusCode("");
            String resultStatusReviewed = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REVIEWED.getStatusCode("");
            String resultStatusEntered = DataSampleStructureStatuses.SampleAnalysisResultStatuses.ENTERED.getStatusCode("");
            String resultStatusReEntered = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REENTERED.getStatusCode("");
            if (resultStatusReviewed.equalsIgnoreCase(currResultStatus) || resultStatusCanceled.equalsIgnoreCase(currResultStatus)) {
                return;
            }

            if (currResultStatus == null) {
                newResultStatus = resultStatusDefault;
            }
            if ((newResultStatus.equalsIgnoreCase(DataSampleStructureStatuses.SampleAnalysisResultStatuses.BLANK.getStatusCode(""))) || (newResultStatus.equalsIgnoreCase(resultStatusDefault))) {
                newResultStatus = resultStatusEntered;
            } else {
                newResultStatus = resultStatusReEntered;
            }
            String[] fieldsName = new String[]{TblsData.SampleAnalysisResult.RAW_VALUE.getName()};
            Object[] fieldsValue = new Object[]{resultValue};
            if (calcResultSpecLimitId != null) {
                String specEvalNoSpec = DataSampleStructureStatuses.SampleAnalysisResultSpecEvalStatuses.NO_SPEC.getStatusCode("");
                String specEvalNoSpecParamLimit = DataSampleStructureStatuses.SampleAnalysisResultSpecEvalStatuses.NO_SPEC_LIMIT.getStatusCode("");
                DataSpec resChkSpec = new DataSpec();
                Object[] resSpecEvaluation = null;
                ConfigSpecRule specRule = new ConfigSpecRule();
                specRule.specLimitsRule(calcResultSpecLimitId, null);
                String specEval = "";
                if (Boolean.TRUE.equals(specRule.getRuleIsQualitative())) {
                    resSpecEvaluation = resChkSpec.resultCheck((String) resultValue.toString(), specRule.getQualitativeRule(),
                            specRule.getQualitativeRuleValues(), specRule.getQualitativeRuleSeparator(), specRule.getQualitativeRuleListName());
                    EnumIntMessages checkMsgCode = (EnumIntMessages) resSpecEvaluation[resSpecEvaluation.length - 1];
                    specEval = checkMsgCode.getErrorCode();

                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation[0].toString())) {
                        return;
                    }
                    fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.SPEC_EVAL_DETAIL.getName(),
                        TblsData.SampleAnalysisResult.ENTERED_BY.getName(), TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName()});
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEval, resSpecEvaluation[resSpecEvaluation.length - 2],
                        instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus});
                    if (calcResultSpecLimitId == null || Boolean.FALSE.equals(Objects.equals(calcResultSpecLimitId, calcResultSpecLimitId))) {
                        fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
                        fieldsValue = LPArray.addValueToArray1D(fieldsValue, calcResultSpecLimitId);
                    }
                    SqlWhere sqlWhere = new SqlWhere();
                    sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
                    Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                            EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                                resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
                    }
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
                    }
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE.getTableName(),
                                new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId},
                                new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()});
                        if (LPPlatform.LAB_FALSE.equals(sampleData[0][0].toString())) {
                            return;
                        }
                        String[] sampleFieldName = new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()};
                        String sampleConfigCode = (String) sampleData[0][1];
                        Integer sampleConfigCodeVersion = Integer.valueOf(LPNulls.replaceNull(sampleData[0][2]).toString());

                        Object[] sampleFieldValue = new Object[]{sampleId, sampleConfigCode, sampleConfigCodeVersion};

                        if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_UPON_CONTROL)) {
                            sar.sarControlAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                        }
                        if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_OOS)) {
                            sar.sarOOSAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                        }
                    }
                    return;
                }
                if (calcResultSpecLimitId == null || Boolean.FALSE.equals(Objects.equals(calcResultSpecLimitId, calcResultSpecLimitId))) {
                    fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.SPEC_EVAL_DETAIL.getName(), TblsData.SampleAnalysisResult.LIMIT_ID.getName()});
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEval, resSpecEvaluation[resSpecEvaluation.length - 2], calcResultSpecLimitId});
                }

            }
            fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{
                TblsData.SampleAnalysisResult.ENTERED_BY.getName(), TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName()});
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{
                instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus});
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{calcResultId}, "");
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED_CALC, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                        resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
            }

        }
    }

    public static void calcSulfase(Integer resultId, Integer testId, Integer sampleId, String[] resultFlds, Object[] resultVls,
            DataSampleAnalysisResultStrategy sar, String alternativeAuditEntry, String alternativeAuditClass) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String[] resultFieldsArr = new String[]{TblsData.SampleAnalysisResult.PARAM_NAME.getName(), TblsData.SampleAnalysisResult.RAW_VALUE.getName(),
            TblsData.SampleAnalysisResult.PARAM_TYPE.getName(), TblsData.SampleAnalysisResult.RESULT_ID.getName(),
            TblsData.SampleAnalysisResult.LIMIT_ID.getName(), TblsData.SampleAnalysisResult.STATUS.getName()};
        Object[][] resultData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{testId},
                resultFieldsArr);
        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) {
            return;
        }

        String volume = null;
        String factor = null;
        String weight = null;
        Integer calcResultId = null;
        Integer calcResultSpecLimitId = null;
        String currResultStatus = null;
        for (Object[] curRes : resultData) {
            if (curRes[0].toString().toUpperCase().contains("VOLUME")) {
                volume = LPNulls.replaceNull(curRes[1]).toString();
            }
            if (curRes[0].toString().toUpperCase().contains("FACTOR")) {
                factor = LPNulls.replaceNull(curRes[1]).toString();
            }
            if (curRes[0].toString().toUpperCase().contains("WEIGHT")||curRes[0].toString().toUpperCase().contains("PESO")) {
                weight = LPNulls.replaceNull(curRes[1]).toString();
            }
            if (curRes[2].toString().toUpperCase().contains("CALC")) {
                calcResultId = Integer.valueOf(LPNulls.replaceNull(curRes[3]).toString());
                calcResultSpecLimitId = LPNulls.replaceNull(curRes[4]).toString().length() == 0 ? null : Integer.valueOf(LPNulls.replaceNull(curRes[4]).toString());
                currResultStatus = LPNulls.replaceNull(curRes[5]).toString();
            }
        }
        if (LPNulls.replaceNull(volume).toString().length() > 0 && LPNulls.replaceNull(factor).toString().length() > 0 && LPNulls.replaceNull(calcResultId).toString().length() > 0) {
            BigDecimal volumeNum = BigDecimal.valueOf(Double.valueOf(volume));
            BigDecimal factorNum = BigDecimal.valueOf(Double.valueOf(factor));
            BigDecimal weightNum = BigDecimal.valueOf(Double.valueOf(weight));
            BigDecimal valueCalc = volumeNum.multiply(BigDecimal.valueOf(0.032)).multiply(factorNum);
            BigDecimal resultValue = valueCalc.divide(weightNum, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

            String newResultStatus = currResultStatus;
            String resultStatusDefault = DataSampleStructureStatuses.SampleAnalysisResultStatuses.getStatusFirstCode();
            String resultStatusCanceled = DataSampleStructureStatuses.SampleAnalysisResultStatuses.CANCELED.getStatusCode("");
            String resultStatusReviewed = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REVIEWED.getStatusCode("");
            String resultStatusEntered = DataSampleStructureStatuses.SampleAnalysisResultStatuses.ENTERED.getStatusCode("");
            String resultStatusReEntered = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REENTERED.getStatusCode("");
            if (resultStatusReviewed.equalsIgnoreCase(currResultStatus) || resultStatusCanceled.equalsIgnoreCase(currResultStatus)) {
                return;
            }

            if (currResultStatus == null) {
                newResultStatus = resultStatusDefault;
            }
            if ((newResultStatus.equalsIgnoreCase(DataSampleStructureStatuses.SampleAnalysisResultStatuses.BLANK.getStatusCode(""))) || (newResultStatus.equalsIgnoreCase(resultStatusDefault))) {
                newResultStatus = resultStatusEntered;
            } else {
                newResultStatus = resultStatusReEntered;
            }
            String[] fieldsName = new String[]{TblsData.SampleAnalysisResult.RAW_VALUE.getName()};
            Object[] fieldsValue = new Object[]{resultValue};
            if (calcResultSpecLimitId != null) {
                String specEvalNoSpec = DataSampleStructureStatuses.SampleAnalysisResultSpecEvalStatuses.NO_SPEC.getStatusCode("");
                String specEvalNoSpecParamLimit = DataSampleStructureStatuses.SampleAnalysisResultSpecEvalStatuses.NO_SPEC_LIMIT.getStatusCode("");
                DataSpec resChkSpec = new DataSpec();
                Object[] resSpecEvaluation = null;
                ConfigSpecRule specRule = new ConfigSpecRule();
                specRule.specLimitsRule(calcResultSpecLimitId, null);
                String specEval = "";
                if (Boolean.TRUE.equals(specRule.getRuleIsQualitative())) {
                    resSpecEvaluation = resChkSpec.resultCheck((String) resultValue.toString(), specRule.getQualitativeRule(),
                            specRule.getQualitativeRuleValues(), specRule.getQualitativeRuleSeparator(), specRule.getQualitativeRuleListName());
                    EnumIntMessages checkMsgCode = (EnumIntMessages) resSpecEvaluation[resSpecEvaluation.length - 1];
                    specEval = checkMsgCode.getErrorCode();

                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation[0].toString())) {
                        return;
                    }
                    fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.SPEC_EVAL_DETAIL.getName(),
                        TblsData.SampleAnalysisResult.ENTERED_BY.getName(), TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName()});
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEval, resSpecEvaluation[resSpecEvaluation.length - 2],
                        instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus});
                    if (calcResultSpecLimitId == null || Boolean.FALSE.equals(Objects.equals(calcResultSpecLimitId, calcResultSpecLimitId))) {
                        fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
                        fieldsValue = LPArray.addValueToArray1D(fieldsValue, calcResultSpecLimitId);
                    }
                    SqlWhere sqlWhere = new SqlWhere();
                    sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
                    Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                            EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                                resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
                    }
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
                    }
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE.getTableName(),
                                new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId},
                                new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()});
                        if (LPPlatform.LAB_FALSE.equals(sampleData[0][0].toString())) {
                            return;
                        }
                        String[] sampleFieldName = new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()};
                        String sampleConfigCode = (String) sampleData[0][1];
                        Integer sampleConfigCodeVersion = Integer.valueOf(LPNulls.replaceNull(sampleData[0][2]).toString());

                        Object[] sampleFieldValue = new Object[]{sampleId, sampleConfigCode, sampleConfigCodeVersion};

                        if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_UPON_CONTROL)) {
                            sar.sarControlAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                        }
                        if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_OOS)) {
                            sar.sarOOSAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                        }
                    }
                    return;
                }
                if (calcResultSpecLimitId == null || Boolean.FALSE.equals(Objects.equals(calcResultSpecLimitId, calcResultSpecLimitId))) {
                    fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.SPEC_EVAL_DETAIL.getName(), TblsData.SampleAnalysisResult.LIMIT_ID.getName()});
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEval, resSpecEvaluation[resSpecEvaluation.length - 2], calcResultSpecLimitId});
                }

            }
            fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{
                TblsData.SampleAnalysisResult.ENTERED_BY.getName(), TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName()});
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{
                instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus});
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{calcResultId}, "");
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED_CALC, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                        resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
            }

        }
    }

    public static void calcSulfuricAshes(Integer resultId, Integer testId, Integer sampleId, String[] resultFlds, Object[] resultVls,
            DataSampleAnalysisResultStrategy sar, String alternativeAuditEntry, String alternativeAuditClass) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String[] resultFieldsArr = new String[]{TblsData.SampleAnalysisResult.PARAM_NAME.getName(), TblsData.SampleAnalysisResult.RAW_VALUE.getName(),
            TblsData.SampleAnalysisResult.PARAM_TYPE.getName(), TblsData.SampleAnalysisResult.RESULT_ID.getName(),
            TblsData.SampleAnalysisResult.LIMIT_ID.getName(), TblsData.SampleAnalysisResult.STATUS.getName()};
        Object[][] resultData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{testId},
                resultFieldsArr);
        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) {
            return;
        }

        String after = null;
        String empty = null;
        String weight = null;
        Integer calcResultId = null;
        Integer calcResultSpecLimitId = null;
        String currResultStatus = null;
        for (Object[] curRes : resultData) {
            if (curRes[0].toString().toUpperCase().contains("AFTER")||curRes[0].toString().toUpperCase().contains("DESPU")) {
                after = LPNulls.replaceNull(curRes[1]).toString();
            }
            if (curRes[0].toString().toUpperCase().contains("ANTES")||curRes[0].toString().toUpperCase().contains("EMPTY")||curRes[0].toString().toUpperCase().contains("VACIO")) {
                empty = LPNulls.replaceNull(curRes[1]).toString();
            }
            if (curRes[0].toString().toUpperCase().contains("WEIGHT")||curRes[0].toString().toUpperCase().contains("PESO")) {
                weight = LPNulls.replaceNull(curRes[1]).toString();
            }
            if (curRes[2].toString().toUpperCase().contains("CALC")) {
                calcResultId = Integer.valueOf(LPNulls.replaceNull(curRes[3]).toString());
                calcResultSpecLimitId = LPNulls.replaceNull(curRes[4]).toString().length() == 0 ? null : Integer.valueOf(LPNulls.replaceNull(curRes[4]).toString());
                currResultStatus = LPNulls.replaceNull(curRes[5]).toString();
            }
        }
        if (LPNulls.replaceNull(after).toString().length() > 0 && LPNulls.replaceNull(empty).toString().length() > 0 && LPNulls.replaceNull(calcResultId).toString().length() > 0) {
            BigDecimal afterNum = BigDecimal.valueOf(Double.valueOf(after));
            BigDecimal emptyNum = BigDecimal.valueOf(Double.valueOf(empty));
            BigDecimal weightNum = BigDecimal.valueOf(Double.valueOf(weight));
            BigDecimal valueCalc = afterNum.subtract(emptyNum);
            BigDecimal resultValue = valueCalc.divide(weightNum, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

            String newResultStatus = currResultStatus;
            String resultStatusDefault = DataSampleStructureStatuses.SampleAnalysisResultStatuses.getStatusFirstCode();
            String resultStatusCanceled = DataSampleStructureStatuses.SampleAnalysisResultStatuses.CANCELED.getStatusCode("");
            String resultStatusReviewed = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REVIEWED.getStatusCode("");
            String resultStatusEntered = DataSampleStructureStatuses.SampleAnalysisResultStatuses.ENTERED.getStatusCode("");
            String resultStatusReEntered = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REENTERED.getStatusCode("");
            if (resultStatusReviewed.equalsIgnoreCase(currResultStatus) || resultStatusCanceled.equalsIgnoreCase(currResultStatus)) {
                return;
            }

            if (currResultStatus == null) {
                newResultStatus = resultStatusDefault;
            }
            if ((newResultStatus.equalsIgnoreCase(DataSampleStructureStatuses.SampleAnalysisResultStatuses.BLANK.getStatusCode(""))) || (newResultStatus.equalsIgnoreCase(resultStatusDefault))) {
                newResultStatus = resultStatusEntered;
            } else {
                newResultStatus = resultStatusReEntered;
            }
            String[] fieldsName = new String[]{TblsData.SampleAnalysisResult.RAW_VALUE.getName()};
            Object[] fieldsValue = new Object[]{resultValue};
            if (calcResultSpecLimitId != null) {
                String specEvalNoSpec = DataSampleStructureStatuses.SampleAnalysisResultSpecEvalStatuses.NO_SPEC.getStatusCode("");
                String specEvalNoSpecParamLimit = DataSampleStructureStatuses.SampleAnalysisResultSpecEvalStatuses.NO_SPEC_LIMIT.getStatusCode("");
                DataSpec resChkSpec = new DataSpec();
                Object[] resSpecEvaluation = null;
                ConfigSpecRule specRule = new ConfigSpecRule();
                specRule.specLimitsRule(calcResultSpecLimitId, null);
                String specEval = "";
                if (Boolean.TRUE.equals(specRule.getRuleIsQualitative())) {
                    resSpecEvaluation = resChkSpec.resultCheck((String) resultValue.toString(), specRule.getQualitativeRule(),
                            specRule.getQualitativeRuleValues(), specRule.getQualitativeRuleSeparator(), specRule.getQualitativeRuleListName());
                    EnumIntMessages checkMsgCode = (EnumIntMessages) resSpecEvaluation[resSpecEvaluation.length - 1];
                    specEval = checkMsgCode.getErrorCode();

                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation[0].toString())) {
                        return;
                    }
                    fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.SPEC_EVAL_DETAIL.getName(),
                        TblsData.SampleAnalysisResult.ENTERED_BY.getName(), TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName()});
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEval, resSpecEvaluation[resSpecEvaluation.length - 2],
                        instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus});
                    if (calcResultSpecLimitId == null || Boolean.FALSE.equals(Objects.equals(calcResultSpecLimitId, calcResultSpecLimitId))) {
                        fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
                        fieldsValue = LPArray.addValueToArray1D(fieldsValue, calcResultSpecLimitId);
                    }
                    SqlWhere sqlWhere = new SqlWhere();
                    sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
                    Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                            EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                                resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
                    }
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
                    }
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                        Object[][] sampleData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE.getTableName(),
                                new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId},
                                new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()});
                        if (LPPlatform.LAB_FALSE.equals(sampleData[0][0].toString())) {
                            return;
                        }
                        String[] sampleFieldName = new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()};
                        String sampleConfigCode = (String) sampleData[0][1];
                        Integer sampleConfigCodeVersion = Integer.valueOf(LPNulls.replaceNull(sampleData[0][2]).toString());

                        Object[] sampleFieldValue = new Object[]{sampleId, sampleConfigCode, sampleConfigCodeVersion};

                        if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_UPON_CONTROL)) {
                            sar.sarControlAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                        }
                        if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_OOS)) {
                            sar.sarOOSAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                        }
                    }
                    return;
                }
                if (calcResultSpecLimitId == null || Boolean.FALSE.equals(Objects.equals(calcResultSpecLimitId, calcResultSpecLimitId))) {
                    fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.SPEC_EVAL_DETAIL.getName(), TblsData.SampleAnalysisResult.LIMIT_ID.getName()});
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEval, resSpecEvaluation[resSpecEvaluation.length - 2], calcResultSpecLimitId});
                }

            }
            fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{
                TblsData.SampleAnalysisResult.ENTERED_BY.getName(), TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName()});
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{
                instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus});
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{calcResultId}, "");
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED_CALC, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                        resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
            }

        }
    }
    
}
