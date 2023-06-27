/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.logic;

import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lbplanet.utilities.LPArray;
import static lbplanet.utilities.LPArray.areAllValuesSame;
import static lbplanet.utilities.LPArray.getColumnValuesFromSomeRows;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMConfig;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntViewFields;
import trazit.queries.QueryUtilitiesEnums;

/**
 *
 * @author User
 */
public class DataInspLotRMCertificate {

    public enum CertificateSignatureLevels {
        BULK_DECISION, SAMPLE_REVIEWER, LOT_USAGE_DECISION
    };

    public static JSONObject getLotCoAInfo(String lotName, String coaDef, JSONObject jLotInfoObj, String[] fieldsToRetrieveSample, Object[][] lotSampleInfo, String[] specFlds, JSONArray specLimitsInfo) {
        JSONObject jMainObj = new JSONObject();

        EnumIntTableFields[] tableFieldsLotDecisionObj = TblsInspLotRMData.TablesInspLotRMData.LOT_DECISION.getTableFields();
        String[] tableFieldsLotDecision = EnumIntTableFields.getAllFieldNames(tableFieldsLotDecisionObj);
        Object[][] lotDecisionInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.LOT_DECISION,
                tableFieldsLotDecisionObj, new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.LOT_DECISION, new String[]{TblsInspLotRMData.LotDecision.LOT_NAME.getName()}, new Object[]{lotName}),
                new String[]{TblsInspLotRMData.LotDecision.LOT_NAME.getName()}, null);

        SqlWhere whereObj = new SqlWhere();
        whereObj.addConstraint(TblsInspLotRMConfig.CoaDefinition.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{coaDef}, null);
        EnumIntTableFields[] flds = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsInspLotRMConfig.TablesInspLotRMConfig.COA_DEFINITION);
        Object[][] materialInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMConfig.TablesInspLotRMConfig.COA_DEFINITION,
                flds, whereObj,
                new String[]{TblsInspLotRMConfig.CoaDefinition.NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) {
            jMainObj.put("error", Arrays.toString(materialInfo[0]));
            return jMainObj;
        }
        jMainObj.put("report_info", LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(flds), materialInfo[0]));

        JSONObject jSectionObj = new JSONObject();
        EnumIntTables tblObj = TblsInspLotRMConfig.TablesInspLotRMConfig.COA_HEADER_COLUMNS;
        whereObj = new SqlWhere();
        whereObj.addConstraint(TblsInspLotRMConfig.CoaHeaderColumns.COA_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{coaDef}, null);
        flds = new EnumIntTableFields[]{TblsInspLotRMConfig.CoaHeaderColumns.COL_ID, TblsInspLotRMConfig.CoaHeaderColumns.FIELD_NAME,
            TblsInspLotRMConfig.CoaHeaderColumns.FIELD2_NAME,
            TblsInspLotRMConfig.CoaHeaderColumns.LABEL_EN, TblsInspLotRMConfig.CoaHeaderColumns.LABEL_ES};
        Object[][] coaHeaderColumnsDef = QueryUtilitiesEnums.getTableData(TblsInspLotRMConfig.TablesInspLotRMConfig.COA_HEADER_COLUMNS,
                flds, whereObj,
                new String[]{TblsInspLotRMConfig.CoaHeaderColumns.COL_ID.getName(), TblsInspLotRMConfig.CoaHeaderColumns.ORDER_NUMBER.getName()});
        JSONObject jSubSectionObj = new JSONObject();
        JSONArray jColArr = new JSONArray();
        JSONArray jCol2Arr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString()))) {
            for (Object[] curFld : coaHeaderColumnsDef) {
                JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(flds), curFld);
                String fldValue = LPNulls.replaceNull(jLotInfoObj.get(curFld[1])).toString();
                convertArrayRowToJSONObject.put("value_en", fldValue);
                convertArrayRowToJSONObject.put("value_es", fldValue);
                if ("2".equalsIgnoreCase(curFld[0].toString())) {
                    jCol2Arr.add(convertArrayRowToJSONObject);
                } else {
                    jColArr.add(convertArrayRowToJSONObject);
                }
            }
        }
        jSubSectionObj.put("column", jColArr);
        jSubSectionObj.put("column2", jCol2Arr);
        jMainObj.put("header", jSubSectionObj);

        jSectionObj = new JSONObject();
        tblObj = TblsInspLotRMConfig.TablesInspLotRMConfig.COA_RESULTS_COLUMNS;
        whereObj = new SqlWhere();
        whereObj.addConstraint(TblsInspLotRMConfig.CoaResultsColumns.COA_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{coaDef}, null);
        flds = new EnumIntTableFields[]{TblsInspLotRMConfig.CoaResultsColumns.LABEL_EN, TblsInspLotRMConfig.CoaResultsColumns.LABEL_ES,
            TblsInspLotRMConfig.CoaResultsColumns.FIELD_NAME, TblsInspLotRMConfig.CoaResultsColumns.ORDER_NUMBER};
        Object[][] coaResultsTableDef = QueryUtilitiesEnums.getTableData(TblsInspLotRMConfig.TablesInspLotRMConfig.COA_RESULTS_COLUMNS,
                flds, whereObj, new String[]{TblsInspLotRMConfig.CoaResultsColumns.ORDER_NUMBER.getName()});
        JSONArray jhdrArr = new JSONArray();
        JSONArray jvlsArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString()))) {

            tblObj = TblsInspLotRMData.TablesInspLotRMData.LOT_NOT_ANALYZED_RESULT;
            whereObj = new SqlWhere();
            whereObj.addConstraint(TblsInspLotRMData.LotNotAnalyzedResult.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, null);
            EnumIntTableFields[] fldsForNotAnalyzed = new EnumIntTableFields[]{TblsInspLotRMData.LotNotAnalyzedResult.ANALYSIS, TblsInspLotRMData.LotNotAnalyzedResult.VALUE};
            Object[][] LotNotAnalyzedResultInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.LOT_NOT_ANALYZED_RESULT,
                    fldsForNotAnalyzed, whereObj,
                    new String[]{TblsInspLotRMData.LotNotAnalyzedResult.LOT_NAME.getName()});
            if (Boolean.TRUE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(LotNotAnalyzedResultInfo[0][0].toString()))) {
                LotNotAnalyzedResultInfo = null;
            }

            TblsInspLotRMData.ViewsInspLotRMData vwObj = TblsInspLotRMData.ViewsInspLotRMData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW;
            whereObj = new SqlWhere();
            whereObj.addConstraint(TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, null);
            EnumIntViewFields[] fldsForSampleResults = TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.values();
            Object[][] lotSampleResultInfo = QueryUtilitiesEnums.getViewData(TblsInspLotRMData.ViewsInspLotRMData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW,
                    fldsForSampleResults, whereObj,
                    new String[]{TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.LOT_NAME.getName()});
            if (Boolean.TRUE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(lotSampleResultInfo[0][0].toString()))) {
                lotSampleResultInfo = null;
            }

            for (Object[] curFld : coaResultsTableDef) {
                jSubSectionObj = new JSONObject();
                jSubSectionObj.put(flds[0].toString().toLowerCase(), curFld[0]);
                jSubSectionObj.put(flds[1].toString().toLowerCase(), curFld[1]);
                jSubSectionObj.put(flds[3].toString().toLowerCase(), curFld[3]);
                jhdrArr.add(jSubSectionObj);
            }
            for (int i = 0; i < specLimitsInfo.size(); i++) {
                JSONObject jsonObject = (JSONObject) specLimitsInfo.get(i);
                String addInCoa = LPNulls.replaceNull(jsonObject.get(TblsInspLotRMConfig.SpecLimits.ADD_IN_COA.getName())).toString();
                if (Boolean.valueOf(addInCoa)) {
                    JSONArray jSubSectionArr = new JSONArray();
                    for (Object[] curFld : coaResultsTableDef) {
                        if ("result".equalsIgnoreCase(curFld[2].toString())) {
                            jSubSectionObj = new JSONObject();
                            jSubSectionObj.put("value_en", getResult("en",
                                    LPNulls.replaceNull(jsonObject.get(TblsInspLotRMConfig.SpecLimits.ANALYSIS.getName())).toString(),
                                    LPNulls.replaceNull(jsonObject.get(TblsInspLotRMConfig.SpecLimits.PARAMETER.getName())).toString(),
                                    LPNulls.replaceNull(jsonObject.get(TblsInspLotRMConfig.SpecLimits.METHOD_NAME.getName())).toString(),
                                    LPNulls.replaceNull(jsonObject.get(TblsInspLotRMConfig.SpecLimits.VARIATION_NAME.getName())).toString(),
                                    fldsForSampleResults, lotSampleResultInfo, LotNotAnalyzedResultInfo));
                            jSubSectionObj.put("value_es", getResult("es",
                                    LPNulls.replaceNull(jsonObject.get(TblsInspLotRMConfig.SpecLimits.ANALYSIS.getName())).toString(),
                                    LPNulls.replaceNull(jsonObject.get(TblsInspLotRMConfig.SpecLimits.PARAMETER.getName())).toString(),
                                    LPNulls.replaceNull(jsonObject.get(TblsInspLotRMConfig.SpecLimits.METHOD_NAME.getName())).toString(),
                                    LPNulls.replaceNull(jsonObject.get(TblsInspLotRMConfig.SpecLimits.VARIATION_NAME.getName())).toString(),
                                    fldsForSampleResults, lotSampleResultInfo, LotNotAnalyzedResultInfo));
                            jSubSectionArr.add(jSubSectionObj);
                        } else {
                            jSubSectionObj = new JSONObject();
                            jSubSectionObj.put("value_en", LPNulls.replaceNull(jsonObject.get(curFld[2])));
                            jSubSectionObj.put("value_es", LPNulls.replaceNull(jsonObject.get(curFld[2])));
                            jSubSectionArr.add(jSubSectionObj);
                        }
                    }
                    jvlsArr.add(jSubSectionArr);
                }
            }
            jSectionObj.put("header", jhdrArr);
            jSectionObj.put("values", jvlsArr);
        }
        jMainObj.put("resultsTable", jSectionObj);

        jSectionObj = new JSONObject();
        tblObj = TblsInspLotRMConfig.TablesInspLotRMConfig.COA_USAGE_DECISION;
        whereObj = new SqlWhere();
        whereObj.addConstraint(TblsInspLotRMConfig.CoaUsageDecision.COA_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{coaDef}, null);
        flds = new EnumIntTableFields[]{TblsInspLotRMConfig.CoaUsageDecision.LABEL_EN, TblsInspLotRMConfig.CoaUsageDecision.LABEL_ES,
            TblsInspLotRMConfig.CoaUsageDecision.LABEL_WHEN_NO_DECISION_EN, TblsInspLotRMConfig.CoaUsageDecision.LABEL_WHEN_NO_DECISION_ES,
            TblsInspLotRMConfig.CoaUsageDecision.VALUE_ACCEPTED_EN, TblsInspLotRMConfig.CoaUsageDecision.VALUE_ACCEPTED_ES,
            TblsInspLotRMConfig.CoaUsageDecision.VALUE_REJECTED_EN, TblsInspLotRMConfig.CoaUsageDecision.VALUE_REJECTED_ES};
        Object[][] coaUsageDecisionInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMConfig.TablesInspLotRMConfig.COA_USAGE_DECISION,
                flds, whereObj,
                new String[]{TblsInspLotRMConfig.CoaUsageDecision.COA_NAME.getName()});
        jSectionObj = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(flds), coaUsageDecisionInfo[0],
                new String[]{TblsInspLotRMConfig.CoaUsageDecision.VALUE_ACCEPTED_EN.getName(), TblsInspLotRMConfig.CoaUsageDecision.VALUE_ACCEPTED_ES.getName(),
                    TblsInspLotRMConfig.CoaUsageDecision.VALUE_REJECTED_EN.getName(), TblsInspLotRMConfig.CoaUsageDecision.VALUE_REJECTED_ES.getName()});
        String usageDecision = LPNulls.replaceNull(lotDecisionInfo[0][LPArray.valuePosicInArray(tableFieldsLotDecision, TblsInspLotRMData.LotDecision.DECISION.getName())]).toString();
        jSectionObj.put("decided", usageDecision.length() > 0);
        if (usageDecision.toUpperCase().contains("ACC")) {
            jSectionObj.put("value_en", coaUsageDecisionInfo[0][EnumIntTableFields.getFldPosicInArray(flds, TblsInspLotRMConfig.CoaUsageDecision.VALUE_ACCEPTED_EN.getName())]);
            jSectionObj.put("value_es", coaUsageDecisionInfo[0][EnumIntTableFields.getFldPosicInArray(flds, TblsInspLotRMConfig.CoaUsageDecision.VALUE_ACCEPTED_ES.getName())]);
        } else {
            jSectionObj.put("value_en", coaUsageDecisionInfo[0][EnumIntTableFields.getFldPosicInArray(flds, TblsInspLotRMConfig.CoaUsageDecision.VALUE_REJECTED_EN.getName())]);
            jSectionObj.put("value_es", coaUsageDecisionInfo[0][EnumIntTableFields.getFldPosicInArray(flds, TblsInspLotRMConfig.CoaUsageDecision.VALUE_REJECTED_ES.getName())]);
        }
        jMainObj.put("usageDecision", jSectionObj);
        jSectionObj = new JSONObject();
        tblObj = TblsInspLotRMConfig.TablesInspLotRMConfig.COA_SIGNATURES;
        whereObj = new SqlWhere();
        whereObj.addConstraint(TblsInspLotRMConfig.CoaSignatures.COA_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{coaDef}, null);
        flds = new EnumIntTableFields[]{TblsInspLotRMConfig.CoaSignatures.MANUAL_SIGN, TblsInspLotRMConfig.CoaSignatures.SIGN_LEVEL,
            TblsInspLotRMConfig.CoaSignatures.SIGN_ELECTRONICALLY_EN, TblsInspLotRMConfig.CoaSignatures.SIGN_ELECTRONICALLY_ES,
            TblsInspLotRMConfig.CoaSignatures.TITLE_EN, TblsInspLotRMConfig.CoaSignatures.TITLE_ES,
            TblsInspLotRMConfig.CoaSignatures.LABEL_WHEN_NOT_SIGNED_EN, TblsInspLotRMConfig.CoaSignatures.LABEL_WHEN_NOT_SIGNED_ES,
            TblsInspLotRMConfig.CoaSignatures.AUTHOR_EN, TblsInspLotRMConfig.CoaSignatures.AUTHOR_ES,
            TblsInspLotRMConfig.CoaSignatures.DATE_EN, TblsInspLotRMConfig.CoaSignatures.DATE_ES};
        Object[][] coaSignaturesInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMConfig.TablesInspLotRMConfig.COA_SIGNATURES,
                flds, whereObj,
                new String[]{TblsInspLotRMConfig.CoaUsageDecision.COA_NAME.getName()});
        JSONArray jSignsArr = new JSONArray();
        for (Object[] curRow : coaSignaturesInfo) {
            jSectionObj = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(flds), curRow);
            Map<String, Object> signaturesData = getSignaturesData(lotName, curRow[1].toString(), coaDef, jLotInfoObj, fieldsToRetrieveSample, lotSampleInfo, specFlds, specLimitsInfo, tableFieldsLotDecision, lotDecisionInfo);
            for (Map.Entry<String, Object> entry : signaturesData.entrySet()) {
                jSectionObj.put(entry.getKey(), entry.getValue());
            }
            jSectionObj.put("sign_electronically_en", LPNulls.replaceNull(curRow[2]).toString());
            jSectionObj.put("sign_electronically_es", LPNulls.replaceNull(curRow[3]).toString());
            jSignsArr.add(jSectionObj);
        }
        jMainObj.put("signatures", jSignsArr);

        return jMainObj;
    }

    public static Map<String, Object> getSignaturesData(String lotName, String signLevel, String coaDef, JSONObject jLotInfoObj, String[] fieldsToRetrieveSample, Object[][] lotSampleInfo, String[] specFlds, JSONArray specLimitsInfo, String[] tableFieldsLotDecision, Object[][] lotDecisionInfo) {
        Map< String, Object> etiquetasValores = new HashMap<>();

        try {
            CertificateSignatureLevels signLevelObj = CertificateSignatureLevels.valueOf(signLevel);
            switch (signLevelObj) {
                case BULK_DECISION:
                    String lotBulkDecision = LPNulls.replaceNull(jLotInfoObj.get(TblsInspLotRMData.Lot.BULK_DECISION.getName())).toString();
                    if (lotBulkDecision.length() == 0) {
                        etiquetasValores.put("author_value_en", "");
                        etiquetasValores.put("author_value_es", "");
                        etiquetasValores.put("date_value_en", "");
                        etiquetasValores.put("date_value_es", "");
                        etiquetasValores.put("signed", false);
                    } else {
                        etiquetasValores.put("signed", true);
                        etiquetasValores.put("author_value_en", LPNulls.replaceNull(jLotInfoObj.get(TblsInspLotRMData.Lot.BULK_DECISION_BY.getName())).toString());
                        etiquetasValores.put("author_value_es", LPNulls.replaceNull(jLotInfoObj.get(TblsInspLotRMData.Lot.BULK_DECISION_BY.getName())).toString());
                        etiquetasValores.put("date_value_en", LPNulls.replaceNull(jLotInfoObj.get(TblsInspLotRMData.Lot.BULK_DECISION_ON.getName())).toString());
                        etiquetasValores.put("date_value_es", LPNulls.replaceNull(jLotInfoObj.get(TblsInspLotRMData.Lot.BULK_DECISION_ON.getName())).toString());
                    }
                    return etiquetasValores;
                case SAMPLE_REVIEWER:
                    Integer revwdFldPosic = LPArray.valuePosicInArray(fieldsToRetrieveSample, TblsData.Sample.REVIEWED.getName());
                    if (revwdFldPosic == -1) {
                        etiquetasValores.put("author_value_en", "Sample Review info not found " + signLevel);
                        etiquetasValores.put("author_value_es", "Info de Revisión de muestra no encontrada " + signLevel);
                        etiquetasValores.put("date_value_en", "");
                        etiquetasValores.put("date_value_es", "");
                        etiquetasValores.put("signed", true);
                    } else {
                        etiquetasValores.put("signed", Boolean.valueOf(LPNulls.replaceNull(lotSampleInfo[0][revwdFldPosic]).toString()));
                        etiquetasValores.put("author_value_en", LPNulls.replaceNull(lotSampleInfo[0][LPArray.valuePosicInArray(fieldsToRetrieveSample, TblsData.Sample.REVIEWED_BY.getName())]));
                        etiquetasValores.put("author_value_es", LPNulls.replaceNull(lotSampleInfo[0][LPArray.valuePosicInArray(fieldsToRetrieveSample, TblsData.Sample.REVIEWED_BY.getName())]));
                        etiquetasValores.put("date_value_en", LPNulls.replaceNull(lotSampleInfo[0][LPArray.valuePosicInArray(fieldsToRetrieveSample, TblsData.Sample.REVIEWED_ON.getName())]).toString());
                        etiquetasValores.put("date_value_es", LPNulls.replaceNull(lotSampleInfo[0][LPArray.valuePosicInArray(fieldsToRetrieveSample, TblsData.Sample.REVIEWED_ON.getName())]).toString());
                    }
                    return etiquetasValores;
                case LOT_USAGE_DECISION:
                    String usageDecision = LPNulls.replaceNull(lotDecisionInfo[0][LPArray.valuePosicInArray(tableFieldsLotDecision, TblsInspLotRMData.LotDecision.DECISION.getName())]).toString();
                    String usageDecisionBy = LPNulls.replaceNull(lotDecisionInfo[0][LPArray.valuePosicInArray(tableFieldsLotDecision, TblsInspLotRMData.LotDecision.DECISION_TAKEN_BY.getName())]).toString();
                    String usageDecisionOn = LPNulls.replaceNull(lotDecisionInfo[0][LPArray.valuePosicInArray(tableFieldsLotDecision, TblsInspLotRMData.LotDecision.DECISION_TAKEN_ON.getName())]).toString();
                    etiquetasValores.put("author_value_en", usageDecisionBy);
                    etiquetasValores.put("author_value_es", usageDecisionBy);
                    etiquetasValores.put("date_value_en", usageDecisionOn);
                    etiquetasValores.put("date_value_es", usageDecisionOn);
                    etiquetasValores.put("signed", usageDecision.length() > 0);
                    return etiquetasValores;
                default:
                    etiquetasValores.put("author_value_en", "Not implemented " + signLevel);
                    etiquetasValores.put("author_value_es", "No implementado " + signLevel);
                    etiquetasValores.put("date_value_en", "");
                    etiquetasValores.put("date_value_es", "");
                    etiquetasValores.put("signed", true);
                    return etiquetasValores;
            }
        } catch (Exception e) {
            etiquetasValores.put("author_value_en", e.getMessage());
            etiquetasValores.put("author_value_es", e.getMessage());
            etiquetasValores.put("date_value_en", "");
            etiquetasValores.put("date_value_es", "");
            etiquetasValores.put("signed", true);
            return etiquetasValores;
        }

    }

    public static String getResult(String language, String analysis, String paramName, String methodName, String variationName, EnumIntViewFields[] fldsForSampleResults, Object[][] lotSampleResultInfo,
            Object[][] LotResultButNotAnalyzedResultInfo) {
        try {
            if (lotSampleResultInfo==null){
                return ("en".equalsIgnoreCase(language)) ? "Not performed" : "No realizado";
            }
            if (LotResultButNotAnalyzedResultInfo != null) {
                Object[] analysisList = LPArray.getColumnFromArray2D(LotResultButNotAnalyzedResultInfo, 0);
                Integer analysisPosic = LPArray.valuePosicInArray(analysisList, analysis);
                if (analysisPosic > -1) {
                    return LotResultButNotAnalyzedResultInfo[analysisPosic][1].toString();
                }
            }
            Integer rawValueFldPosic = EnumIntViewFields.getFldPosicInArray(fldsForSampleResults, TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE.getName());
            if (rawValueFldPosic == -1) {
                return ("en".equalsIgnoreCase(language)) ? "Error in COA building, raw value column not found" : "Error generando COA: Columna raw value no encontrada";
            }
            Integer prettyValueFldPosic = EnumIntViewFields.getFldPosicInArray(fldsForSampleResults, TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.PRETTY_VALUE.getName());
            if (prettyValueFldPosic == -1) {
                return ("en".equalsIgnoreCase(language)) ? "Error in COA building, pretty value column not found" : "Error generando COA: Columna pretty value no encontrada";
            }
            Integer analysisFldPosic = EnumIntViewFields.getFldPosicInArray(fldsForSampleResults, TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.ANALYSIS.getName());
            if (analysisFldPosic == -1) {
                return ("en".equalsIgnoreCase(language)) ? "Error in COA building, analysis column not found" : "Error generando COA: Columna analysis no encontrada";
            }
            Integer paramNameFldPosic = EnumIntViewFields.getFldPosicInArray(fldsForSampleResults, TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.PARAM_NAME.getName());
            if (paramNameFldPosic == -1) {
                return ("en".equalsIgnoreCase(language)) ? "Error in COA building, method name column not found" : "Error generando COA: Columna parameter name no encontrada";
            }
            Integer methodFldPosic = EnumIntViewFields.getFldPosicInArray(fldsForSampleResults, TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.METHOD_NAME.getName());
            if (methodFldPosic == -1) {
                return ("en".equalsIgnoreCase(language)) ? "Error in COA building, method name column not found" : "Error generando COA: Columna method name no encontrada";
            }
            Object[][] criteria = {{methodFldPosic, methodName}, {analysisFldPosic, analysis}, {paramNameFldPosic, paramName}};
            int[] allResultsForThisAnalysisArr = LPArray.valuePosicArray2D(lotSampleResultInfo, criteria);
//        Integer[] allResultsForThisAnalysisArr = LPArray.valueAllPosicInArray2D(lotSampleResultInfo, analysis, analysisFldPosic);
            if (allResultsForThisAnalysisArr.length == 0 || allResultsForThisAnalysisArr[0] == -1) {
                return ("en".equalsIgnoreCase(language)) ? "Not performed" : "No realizado";
            }
//        Integer prettyValueFldPosic = EnumIntViewFields.getFldPosicInArray(fldsForSampleResults, TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.PRETTY_VALUE.getName());
//        Integer rawValueFldPosic = EnumIntViewFields.getFldPosicInArray(fldsForSampleResults, TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE.getName());
            Integer uomFldPosic = EnumIntViewFields.getFldPosicInArray(fldsForSampleResults, TblsInspLotRMData.ViewSampleAnalysisResultWithSpecLimits.UOM.getName());
            if (allResultsForThisAnalysisArr.length == 1) {
                String rsltVal = "";
                String rsltRawVal = ("en".equalsIgnoreCase(language)) ? lotSampleResultInfo[allResultsForThisAnalysisArr[0]][rawValueFldPosic].toString() : lotSampleResultInfo[allResultsForThisAnalysisArr[0]][rawValueFldPosic].toString();
                String rsltPrettyVal = ("en".equalsIgnoreCase(language)) ? lotSampleResultInfo[allResultsForThisAnalysisArr[0]][prettyValueFldPosic].toString() : lotSampleResultInfo[allResultsForThisAnalysisArr[0]][prettyValueFldPosic].toString();
                if (rsltPrettyVal.length() == 0 && rsltRawVal.length() == 0) {
                    return ("en".equalsIgnoreCase(language)) ? "Not performed" : "No realizado";
                }
                rsltVal = rsltPrettyVal.length() == 0 ? rsltRawVal : rsltPrettyVal;
                String rsltUom = LPNulls.replaceNull(lotSampleResultInfo[allResultsForThisAnalysisArr[0]][uomFldPosic]).toString();
                rsltVal = rsltVal + " " + rsltUom;
                return rsltVal;
            }
            if (allResultsForThisAnalysisArr.length > 1) {
                Object[] columnValuesFromSomeRowsRaw = getColumnValuesFromSomeRows(lotSampleResultInfo, allResultsForThisAnalysisArr, rawValueFldPosic);
                Object[] columnValuesFromSomeRowsPretty = getColumnValuesFromSomeRows(lotSampleResultInfo, allResultsForThisAnalysisArr, prettyValueFldPosic);

                if (areAllValuesSame(columnValuesFromSomeRowsPretty)) {
                    String rsltVal = LPNulls.replaceNull(columnValuesFromSomeRowsPretty[0]).toString().length() == 0 ? LPNulls.replaceNull(columnValuesFromSomeRowsRaw[0]).toString() : LPNulls.replaceNull(columnValuesFromSomeRowsPretty[0]).toString();
                    String rsltUom = LPNulls.replaceNull(lotSampleResultInfo[allResultsForThisAnalysisArr[0]][uomFldPosic]).toString();
                    rsltVal = rsltVal + " " + rsltUom;
                    return rsltVal;
                } else {
                    return ("en".equalsIgnoreCase(language)) ? "Some" : "Varios";
                }
            }

            return ("en".equalsIgnoreCase(language)) ? "Not performed" : "No realizado";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
