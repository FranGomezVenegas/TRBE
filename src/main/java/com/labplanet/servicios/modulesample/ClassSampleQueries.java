/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import com.labplanet.servicios.app.GlobalAPIsParams;
import module.monitoring.definition.TblsEnvMonitData;
import com.labplanet.servicios.modulesample.SampleAPIParams.SampleAPIqueriesEndpoints;
import static com.labplanet.servicios.modulesample.SampleAPIfrontend.sampleAnalysisResultView;
import static com.labplanet.servicios.modulesample.SampleAPIfrontend.sampleAnalysisView;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlStatementEnums;
import databases.SqlWhere;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsProcedure;
import databases.features.Token;
import functionaljavaa.analysis.ConfigAnalysisStructure;
import functionaljavaa.materialspec.ConfigSpecRule;
import module.monitoring.logic.DataProgramCorrectiveAction;
import static module.monitoring.logic.DataProgramCorrectiveAction.isProgramCorrectiveActionEnable;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.samplestructure.DataSampleStructureStatuses.SampleStatuses;
import java.sql.Timestamp;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntQueriesObj;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntViewFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import static trazit.globalvariables.GlobalVariables.DEFAULTLANGUAGE;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;

/**
 *
 * @author User
 */
public class ClassSampleQueries implements EnumIntQueriesObj {

    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    private Object[] diagnostic = new Object[0];
    private Boolean functionFound = false;
    private Boolean isSuccess = false;
    private JSONObject responseSuccessJObj = null;
    private JSONArray responseSuccessJArr = null;
    private Object[] responseError = null;

    private static final String[] SAMPLEANALYSISRESULTLOCKDATA_RETRIEVEDATA_PROGRAMCORRECTIVEACTION = new String[]{TblsProcedure.ProgramCorrectiveAction.RESULT_ID.getName(), TblsProcedure.ProgramCorrectiveAction.STATUS.getName()};
    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME + "|" + GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN + "|" + GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

    public ClassSampleQueries(HttpServletRequest request, HttpServletResponse response, SampleAPIqueriesEndpoints endPoint) {
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        Object[] actionDiagnoses = null;
        this.functionFound = true;
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        this.functionFound = true;
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
            this.isSuccess = false;
            this.responseError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE,
                    ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.messageDynamicData = new Object[]{argValues[2].toString()};
            this.diagnostic = this.responseError;
            return;
        }
        switch (endPoint) {
            case GET_SAMPLE_ANALYSIS_RESULT_LIST:
                Integer sampleId = Integer.valueOf(LPNulls.replaceNull(argValues[0]).toString());
                String[] resultFieldToRetrieveArr = EnumIntViewFields.getAllFieldNames(EnumIntViewFields.getViewFieldsFromString(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, "ALL"));
                EnumIntViewFields[] fldsToGet = EnumIntViewFields.getAllFieldNamesFromDatabase(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, null);
                //fieldsToGet = EnumIntViewFields.(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, sampleAnalysisFieldToRetrieve.split("\\|"));
                resultFieldToRetrieveArr = LPArray.getUniquesArray(LPArray.addValueToArray1D(resultFieldToRetrieveArr, SampleAPIParams.MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|")));

                String[] sampleAnalysisWhereFieldsNameArr = new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName()};
                Object[] sampleAnalysisWhereFieldsValueArr = new Object[]{sampleId};

                String sampleAnalysisWhereFieldsName = LPNulls.replaceNull(argValues[2]).toString();
                if ((sampleAnalysisWhereFieldsName != null) && (sampleAnalysisWhereFieldsName.length() > 0)) {
                    sampleAnalysisWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                }
                String sampleAnalysisWhereFieldsValue = LPNulls.replaceNull(argValues[3]).toString();
                if ((sampleAnalysisWhereFieldsValue != null) && (sampleAnalysisWhereFieldsValue.length() > 0)) {
                    sampleAnalysisWhereFieldsValueArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")));
                }

                String sarWhereFieldsName = LPNulls.replaceNull(argValues[4]).toString();
                if ((sarWhereFieldsName != null) && (sarWhereFieldsName.length() > 0)) {
                    sampleAnalysisWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sarWhereFieldsName.split("\\|"));
                }
                String sarWhereFieldsValue = LPNulls.replaceNull(argValues[5]).toString();
                if ((sarWhereFieldsValue != null) && (sarWhereFieldsValue.length() > 0)) {
                    sampleAnalysisWhereFieldsValueArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsValueArr, (sampleAnalysisWhereFieldsValue != null ? LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")) : new Object[]{}));
                }

                String[] sortFieldsNameArr = null;
                String sortFieldsName = LPNulls.replaceNull(argValues[6]).toString();
                if ((sortFieldsName != null) && (sortFieldsName.length() > 0)) {
                    sortFieldsNameArr = sortFieldsName.split("\\|");
                } else {
                    sortFieldsNameArr = LPArray.getUniquesArray(SampleAPIParams.MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|"));
                }

                Integer posicRawValueFld = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE.getName());
                if (posicRawValueFld == -1) {
                    resultFieldToRetrieveArr = LPArray.addValueToArray1D(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE.getName());
                    posicRawValueFld = resultFieldToRetrieveArr.length;
                }
                Integer posicLimitIdFld = EnumIntViewFields.getFldPosicInArray(fldsToGet, TblsData.ViewSampleAnalysisResultWithSpecLimits.LIMIT_ID.getName());

                Object[][] analysisResultList = QueryUtilitiesEnums.getViewData(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW,
                        fldsToGet,
                        new SqlWhere(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsValueArr),
                        sortFieldsNameArr);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisResultList[0][0].toString())) {

                    this.isSuccess = true;
                    this.responseSuccessJArr = new JSONArray();
                } else {
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    Object[] objectsIds = getObjectsId(EnumIntViewFields.getAllFieldNames(fldsToGet), analysisResultList, "-");
                    for (Object curObj : objectsIds) {
                        String[] curObjDet = curObj.toString().split("-");
                        if (TblsData.SampleAnalysisResult.TEST_ID.getName().equalsIgnoreCase(curObjDet[0])) {
                            rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), curObjDet[1]);
                        }
                        if (TblsData.SampleAnalysisResult.RESULT_ID.getName().equalsIgnoreCase(curObjDet[0])) {
                            rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), curObjDet[1]);
                        }
                    }
                    JSONArray jArr = new JSONArray();
                    for (Object[] curRow : analysisResultList) {
                        ConfigSpecRule specRule = new ConfigSpecRule();
                        String currRowRawValue = curRow[posicRawValueFld].toString();
                        String currRowLimitId = curRow[posicLimitIdFld].toString();
                        JSONObject row = new JSONObject();

                        Object[] resultLockData = sampleAnalysisResultLockData(EnumIntViewFields.getAllFieldNames(fldsToGet), curRow);
                        if (resultLockData != null && resultLockData[0] != null) {
                            if (resultLockData.length > 2) {
                                row = LPJson.convertArrayRowToJSONObject(LPArray.addValueToArray1D(LPArray.addValueToArray1D(EnumIntViewFields.getAllFieldNames(fldsToGet), (String) resultLockData[2]), (String[]) resultLockData[0]),
                                        LPArray.addValueToArray1D(LPArray.addValueToArray1D(curRow, resultLockData[3]), (Object[]) resultLockData[1]));
                            } else {
                                row = LPJson.convertArrayRowToJSONObject(LPArray.addValueToArray1D(EnumIntViewFields.getAllFieldNames(fldsToGet), (String[]) resultLockData[0]), LPArray.addValueToArray1D(curRow, (Object[]) resultLockData[1]));
                            }
                        } else {
                            row = LPJson.convertArrayRowToJSONObject(EnumIntViewFields.getAllFieldNames(fldsToGet), curRow);
                        }
                        if ((currRowLimitId != null) && (currRowLimitId.length() > 0)) {
                            specRule.specLimitsRule(Integer.valueOf(currRowLimitId), null);
                            row.put(ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_DETAILED, LPNulls.replaceNull(specRule.getRuleRepresentation()).replace(("R"), "R (" + currRowRawValue + ")"));
                            Object[][] specRuleDetail = specRule.getRuleData();
                            JSONArray specRuleDetailjArr = new JSONArray();
                            JSONObject specRuleDetailjObj = new JSONObject();
                            for (Object[] curSpcRlDet : specRuleDetail) {
                                specRuleDetailjObj.put(curSpcRlDet[0], curSpcRlDet[1]);
                            }
                            specRuleDetailjArr.add(specRuleDetailjObj);
                            row.put(ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_INFO, specRuleDetailjArr);
                        }
                        jArr.add(row);
                    }
                    Rdbms.closeRdbms();
                    this.isSuccess = true;
                    this.responseSuccessJArr = jArr;
                }
                return;
            case SAMPLES_VIEW:
                EnumIntTableFields[] fieldToRetrieveArr;
                String whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME);
                if (whereFieldsName == null) {
                    whereFieldsName = "";
                }
                String whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE);

                String sampleFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE);
                if (sampleFieldToRetrieve == null || sampleFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleFieldToRetrieve)) {
                    fieldToRetrieveArr = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsData.TablesData.SAMPLE);
                } else {
                    fieldToRetrieveArr = EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE, sampleFieldToRetrieve.split("\\|"));
                }

                String sampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE);
                String sampleLastLevel = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL);

                String addSampleAnalysis = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS);
                if (addSampleAnalysis == null) {
                    addSampleAnalysis = "false";
                }
                String sampleAnalysisFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE);
                String[] sampleAnalysisFieldToRetrieveArr = null;
                if (sampleAnalysisFieldToRetrieve == null || sampleAnalysisFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleAnalysisFieldToRetrieve)) {
                    sampleAnalysisFieldToRetrieveArr = getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS.getTableFields());
                } else {
                    sampleAnalysisFieldToRetrieveArr = sampleAnalysisFieldToRetrieve.split("\\|");
                }
                sampleAnalysisWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME);
                sampleAnalysisWhereFieldsNameArr = new String[0];
                if ((sampleAnalysisWhereFieldsName != null) && (sampleAnalysisWhereFieldsName.length() > 0)) {
                    sampleAnalysisWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                }
                sampleAnalysisWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE);

                String addSampleAnalysisResult = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT);
                if (addSampleAnalysisResult == null) {
                    addSampleAnalysisResult = "false";
                }
                sampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE);
                String[] sampleAnalysisResultFieldToRetrieveArr = null;
                if (sampleAnalysisResultFieldToRetrieve == null || sampleAnalysisResultFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleAnalysisResultFieldToRetrieve)) {
                    sampleAnalysisResultFieldToRetrieveArr = getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableFields());
                } else {
                    sampleAnalysisResultFieldToRetrieveArr = sampleAnalysisResultFieldToRetrieve.split("\\|");
                }
                String sampleAnalysisResultWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME);
                String[] sampleAnalysisResultWhereFieldsNameArr = new String[0];
                if ((sampleAnalysisResultWhereFieldsName != null) && (sampleAnalysisResultWhereFieldsName.length() > 0)) {
                    sampleAnalysisResultWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisResultWhereFieldsNameArr, sampleAnalysisResultWhereFieldsName.split("\\|"));
                }
                String sampleAnalysisResultWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE);
                Boolean includeOnlyWhenResultsInProgress = Boolean.valueOf(LPNulls.replaceNull(argValues[14]).toString());

                sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME);
                JSONArray samplesArray = samplesByStageData(sampleLastLevel, fieldToRetrieveArr, whereFieldsName,
                        whereFieldsValue, sortFieldsName,
                        addSampleAnalysis, sampleAnalysisFieldToRetrieveArr, sampleAnalysisWhereFieldsName, sampleAnalysisWhereFieldsValue,
                        addSampleAnalysisResult,
                        sampleAnalysisResultFieldToRetrieveArr, sampleAnalysisResultWhereFieldsName, sampleAnalysisResultWhereFieldsValue,
                        includeOnlyWhenResultsInProgress);
                this.isSuccess = true;
                this.responseSuccessJArr = samplesArray;
                return;
            case SAMPLES_ANALYSIS_VIEW:
                whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME);
                if (whereFieldsName == null) {
                    whereFieldsName = "";
                }
                whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE);

                String fieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE);
                if (fieldToRetrieve == null || fieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(fieldToRetrieve)) {
                    fieldToRetrieveArr = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsData.TablesData.SAMPLE_ANALYSIS);
                } else {
                    fieldToRetrieveArr = EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS, fieldToRetrieve.split("\\|"));
                }

                sampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE);
                addSampleAnalysisResult = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT);
                if (addSampleAnalysisResult == null) {
                    addSampleAnalysisResult = "false";
                }
                sampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE);
                sampleAnalysisResultFieldToRetrieveArr = null;
                if (sampleAnalysisResultFieldToRetrieve == null || sampleAnalysisResultFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleAnalysisResultFieldToRetrieve)) {
                    sampleAnalysisResultFieldToRetrieveArr = getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableFields());
                } else {
                    sampleAnalysisResultFieldToRetrieveArr = sampleAnalysisResultFieldToRetrieve.split("\\|");
                }
                sampleAnalysisResultWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME);
                sampleAnalysisResultWhereFieldsNameArr = new String[0];
                if ((sampleAnalysisResultWhereFieldsName != null) && (sampleAnalysisResultWhereFieldsName.length() > 0)) {
                    sampleAnalysisResultWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisResultWhereFieldsNameArr, sampleAnalysisResultWhereFieldsName.split("\\|"));
                }
                sampleAnalysisResultWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE);
                includeOnlyWhenResultsInProgress = Boolean.valueOf(LPNulls.replaceNull(argValues[14]).toString());

                sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME);
                JSONArray samplesAnalysisArray = sampleAnalysisView(EnumIntTableFields.getAllFieldNames(fieldToRetrieveArr), whereFieldsName,
                        whereFieldsValue, sortFieldsName,
                        addSampleAnalysisResult, sampleAnalysisResultFieldToRetrieveArr, sampleAnalysisResultWhereFieldsName, sampleAnalysisResultWhereFieldsValue,
                        includeOnlyWhenResultsInProgress);
                this.isSuccess = true;
                this.responseSuccessJArr = samplesAnalysisArray;
                return;
            case SAMPLEANALYSIS_PENDING_REVISION:
                String[] whereFieldsNameArr = new String[]{};
                Object[] whereFieldsValueArr = new Object[]{};
                sampleAnalysisFieldToRetrieveArr = new String[]{};
                String[] sampleAnalysisSortFieldArr = new String[]{};
                whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME);
                if (whereFieldsName == null) {
                    whereFieldsName = "";
                }
                whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE);

                if (whereFieldsValue == null || whereFieldsValue.length() == 0) {
                    whereFieldsValue = "|BLANK|true*Boolean";
                } else {
                    whereFieldsValue = whereFieldsValue + "|BLANK|true*Boolean";
                }
                if (whereFieldsName != null && whereFieldsName.length() > 0) {
                    whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
                }
                whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.STATUS.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.NOT_IN.getSqlClause(),
                    TblsData.ViewSampleAnalysisResultWithSpecLimits.TEST_REVIEWER.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()});

                whereFieldsValueArr = LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|"));
                EnumIntViewFields[] fieldsToGet = null;
                sampleAnalysisFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE);
                if (sampleAnalysisFieldToRetrieve != null && !"ALL".equalsIgnoreCase(sampleAnalysisFieldToRetrieve) && sampleAnalysisFieldToRetrieve.length() > 0) {
                    fieldsToGet = EnumIntViewFields.getViewFieldsFromString(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, sampleAnalysisFieldToRetrieve.split("\\|"));
                } else {
                    fieldsToGet = EnumIntViewFields.getAllFieldNamesFromDatabase(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, null);
                }
                String sampleAnalysisSortField = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME);
                if (sampleAnalysisSortField != null && sampleAnalysisSortField.length() > 0) {
                    sampleAnalysisSortFieldArr = sampleAnalysisSortField.split("\\|");
                }

                Object[][] smplsAnaData = QueryUtilitiesEnums.getViewData(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW,
                        fieldsToGet,
                        new SqlWhere(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, whereFieldsNameArr, whereFieldsValueArr), sampleAnalysisSortFieldArr);
                JSONArray smplAnaJsArr = new JSONArray();
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(smplsAnaData[0][0].toString())) {
                    this.isSuccess = true;
                    this.responseSuccessJArr = smplAnaJsArr;
                    return;
                }
                for (Object[] curSmpAna : smplsAnaData) {
                    smplAnaJsArr.add(LPJson.convertArrayRowToJSONObject(EnumIntViewFields.getAllFieldNames(fieldsToGet), curSmpAna));
                }
                this.isSuccess = true;
                this.responseSuccessJArr = smplAnaJsArr;
                return;

            case SAMPLES_ANALYSIS_RESULTS_VIEW:
                whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME);
                if (whereFieldsName == null) {
                    whereFieldsName = "";
                }
                whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE);

                fieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE);
                if (fieldToRetrieve == null || fieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(fieldToRetrieve)) {
                    fieldToRetrieveArr = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT);
                } else {
                    fieldToRetrieveArr = EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldToRetrieve.split("\\|"));
                }

                sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME);
                JSONArray samplesAnalysisResultArray = sampleAnalysisResultView(EnumIntTableFields.getAllFieldNames(fieldToRetrieveArr), whereFieldsName,
                        whereFieldsValue, sortFieldsName);
                this.isSuccess = true;
                this.responseSuccessJArr = samplesAnalysisResultArray;
                return;
            case SAMPLES_INPROGRESS_LIST:
                whereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME);
                if (whereFieldsName == null) {
                    whereFieldsName = "";
                }
                whereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE);
                EnumIntTableFields[] sampleFieldToRetrieveArr = null;
                sampleFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE);
                if (sampleFieldToRetrieve == null || sampleFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleFieldToRetrieve)) {
                    sampleFieldToRetrieveArr = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsData.TablesData.SAMPLE);
                } else {
                    sampleFieldToRetrieveArr = EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE, sampleFieldToRetrieve.split("\\|"));
                }
                sampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE);
                sampleLastLevel = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL);

                addSampleAnalysis = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS);
                if (addSampleAnalysis == null) {
                    addSampleAnalysis = "false";
                }
                sampleAnalysisFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE);
                sampleAnalysisFieldToRetrieveArr = null;
                if (sampleAnalysisFieldToRetrieve == null || sampleAnalysisFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleAnalysisFieldToRetrieve)) {
                    sampleAnalysisFieldToRetrieveArr = getAllFieldNames(EnumIntTableFields.getAllFieldNamesFromDatabase(TblsData.TablesData.SAMPLE_ANALYSIS));
                } else {
                    sampleAnalysisFieldToRetrieveArr = sampleAnalysisFieldToRetrieve.split("\\|");
                }
                sampleAnalysisWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME);
                sampleAnalysisWhereFieldsNameArr = new String[0];
                if ((sampleAnalysisWhereFieldsName != null) && (sampleAnalysisWhereFieldsName.length() > 0)) {
                    sampleAnalysisWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                }
                sampleAnalysisWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE);

                addSampleAnalysisResult = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT);
                if (addSampleAnalysisResult == null) {
                    addSampleAnalysisResult = "false";
                }
                sampleAnalysisResultFieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE);
                sampleAnalysisResultFieldToRetrieveArr = null;
                if (sampleAnalysisResultFieldToRetrieve == null || sampleAnalysisResultFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleAnalysisResultFieldToRetrieve)) {
                    sampleAnalysisResultFieldToRetrieveArr = getAllFieldNames(EnumIntTableFields.getAllFieldNamesFromDatabase(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT));
                } else {
                    sampleAnalysisResultFieldToRetrieveArr = sampleAnalysisResultFieldToRetrieve.split("\\|");
                }
                sampleAnalysisResultWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME);
                sampleAnalysisResultWhereFieldsNameArr = new String[0];
                if ((sampleAnalysisResultWhereFieldsName != null) && (sampleAnalysisResultWhereFieldsName.length() > 0)) {
                    sampleAnalysisResultWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisResultWhereFieldsNameArr, sampleAnalysisResultWhereFieldsName.split("\\|"));
                }
                sampleAnalysisResultWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE);
                includeOnlyWhenResultsInProgress = false;
                if (argValues.length > 14) {
                    includeOnlyWhenResultsInProgress = Boolean.valueOf(LPNulls.replaceNull(argValues[14]).toString());
                }

                sortFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME);
                samplesArray = samplesByStageData(sampleLastLevel, sampleFieldToRetrieveArr, whereFieldsName,
                        whereFieldsValue, sortFieldsName,
                        addSampleAnalysis, sampleAnalysisFieldToRetrieveArr, sampleAnalysisWhereFieldsName, sampleAnalysisWhereFieldsValue,
                        addSampleAnalysisResult,
                        sampleAnalysisResultFieldToRetrieveArr, sampleAnalysisResultWhereFieldsName, sampleAnalysisResultWhereFieldsValue,
                        includeOnlyWhenResultsInProgress);
                this.isSuccess = true;
                this.responseSuccessJArr = samplesArray;
                return;
            case SAMPLES_PENDING_TESTINGGROUP_REVISION:
                String testingGroup = argValues[0].toString();
                fieldToRetrieve = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE);
                String[] fieldToRetrieveViewArr = null;
                if (fieldToRetrieve == null || fieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(fieldToRetrieve)) {
                    EnumIntViewFields[] viewFieldsFromString = EnumIntViewFields.getAllFieldNamesFromDatabase(TblsData.ViewsData.SAMPLE_TESTING_GROUP_VIEW, null);
                    fieldToRetrieveViewArr = EnumIntViewFields.getAllFieldNames(viewFieldsFromString);
                } else {
                    fieldToRetrieveViewArr = fieldToRetrieve.split("\\|");
                }
                String myData = Rdbms.getRecordFieldsByFilterJSON(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsData.ViewsData.SAMPLE_TESTING_GROUP_VIEW.getViewName(),
                        new String[]{TblsData.ViewSampleTestingGroup.READY_FOR_REVISION.getName(), TblsData.ViewSampleTestingGroup.REVIEWED.getName(), TblsData.ViewSampleTestingGroup.TESTING_GROUP.getName()},
                        new Object[]{true, false, testingGroup},
                        fieldToRetrieveViewArr,
                        new String[]{TblsData.ViewSampleTestingGroup.SAMPLE_ID.getName(), TblsData.ViewSampleTestingGroup.TESTING_GROUP.getName()});
                if (myData == null || myData.contains(LPPlatform.LAB_FALSE)) {
                    this.isSuccess = true;
                    this.responseSuccessJArr = new JSONArray();
                } else {
                    this.isSuccess = true;
                    this.responseSuccessJArr = LPJson.convertArrayJsonToJSON(LPJson.convertToJsonArrayStringedObject(myData));
                }
                return;

            case SAMPLES_PENDING_SAMPLE_REVISION:
                sampleFieldToRetrieveArr = null;
                sampleFieldToRetrieve = argValues[0].toString();
                if (sampleFieldToRetrieve == null || sampleFieldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(sampleFieldToRetrieve)) {
                    sampleFieldToRetrieveArr = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsData.TablesData.SAMPLE);
                } else {
                    sampleFieldToRetrieveArr = EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE, sampleFieldToRetrieve.split("\\|"));
                }

                myData = Rdbms.getRecordFieldsByFilterJSON(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(),
                        new String[]{TblsData.Sample.READY_FOR_REVISION.getName(), "(" + TblsData.Sample.REVIEWED.getName(), SqlStatement.WHERECLAUSE_TYPES.OR.getSqlClause() + " " + TblsData.Sample.REVIEWED.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause() + ")"},
                        new Object[]{true, false, null},
                        EnumIntTableFields.getAllFieldNames(sampleFieldToRetrieveArr),
                        new String[]{TblsData.Sample.SAMPLE_ID.getName()});
                if (myData == null || myData.contains(LPPlatform.LAB_FALSE)) {
                    this.isSuccess = true;
                    this.responseSuccessJArr = new JSONArray();
                } else {
                    this.isSuccess = true;
                    this.responseSuccessJArr = LPJson.convertArrayJsonToJSON(LPJson.convertToJsonArrayStringedObject(myData));
                }
                return;
            case GET_METHOD_CERTIFIED_USERS_LIST:
                return;
            case GET_SAMPLE_ANALYSIS_LIST:
                sampleId = Integer.valueOf(LPNulls.replaceNull(argValues[0]).toString());
                EnumIntTableFields[] tblFldsToGet = EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS, "ALL");

                sampleAnalysisWhereFieldsNameArr = new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName()};
                sampleAnalysisWhereFieldsValueArr = new Object[]{sampleId};

                sampleAnalysisWhereFieldsName = LPNulls.replaceNull(argValues[2]).toString();
                if ((sampleAnalysisWhereFieldsName != null) && (sampleAnalysisWhereFieldsName.length() > 0)) {
                    sampleAnalysisWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                }
                sampleAnalysisWhereFieldsValue = LPNulls.replaceNull(argValues[3]).toString();
                if ((sampleAnalysisWhereFieldsValue != null) && (sampleAnalysisWhereFieldsValue.length() > 0)) {
                    sampleAnalysisWhereFieldsValueArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")));
                }
                sortFieldsNameArr = null;
                sortFieldsName = LPNulls.replaceNull(argValues[4]).toString();
                if ((sortFieldsName != null) && (sortFieldsName.length() > 0)) {
                    sortFieldsNameArr = sortFieldsName.split("\\|");
                } else {
                    sortFieldsNameArr = LPArray.getUniquesArray(SampleAPIParams.MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_GET_SAMPLE_ANALYSIS_LIST.split("\\|"));
                }

                Object[][] analysisList = QueryUtilitiesEnums.getTableData(TblsData.TablesData.SAMPLE_ANALYSIS,
                        tblFldsToGet,
                        new SqlWhere(TblsData.TablesData.SAMPLE_ANALYSIS, sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsValueArr),
                        sortFieldsNameArr);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisList[0][0].toString())) {
                    this.isSuccess = true;
                    this.responseSuccessJArr = new JSONArray();
                } else {
                    JSONArray jArr = new JSONArray();
                    for (Object[] curRow : analysisList) {
                        JSONObject row = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(tblFldsToGet), curRow);
                        jArr.add(row);
                    }
                    Rdbms.closeRdbms();
                    this.isSuccess = true;
                    this.responseSuccessJArr = jArr;
                }
                return;
            default:
                break;
        }
        this.diagnostic = actionDiagnoses;
        this.relatedObj = rObj;
        rObj.killInstance();
    }

    private static JSONArray samplesByStageData(String sampleLastLevel, EnumIntTableFields[] sampleFieldToRetrieveArr, String whereFieldsName, String whereFieldsValue, String sortFieldsName,
            String addSampleAnalysis, String[] sampleAnalysisFieldToRetrieveArr, String sampleAnalysisWhereFieldsName, String sampleAnalysisWhereFieldsValue,
            String addSampleAnalysisResult, String[] sampleAnalysisResultFieldToRetrieveArr, String sampleAnalysisResultWhereFieldsName, String sampleAnalysisResultWhereFieldsValue, Boolean includeOnlyWhenResultsInProgress) {
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        String procInstanceName = procReqInstance.getProcedureInstance();
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());

        if (sampleLastLevel == null) {
            sampleLastLevel = TblsData.TablesData.SAMPLE.getTableName();
        }
        if (sampleFieldToRetrieveArr == null || sampleFieldToRetrieveArr.length == 0) {
            sampleFieldToRetrieveArr = new EnumIntTableFields[]{TblsData.Sample.SAMPLE_ID};
        }

        if (sampleAnalysisFieldToRetrieveArr == null || sampleAnalysisFieldToRetrieveArr[0].length() == 0) {
            sampleAnalysisFieldToRetrieveArr = new String[]{TblsData.SampleAnalysis.TEST_ID.getName()};
        }

        if (sampleAnalysisFieldToRetrieveArr == null || sampleAnalysisFieldToRetrieveArr[0].length() == 0) {
            sampleAnalysisFieldToRetrieveArr = new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()};
        }

        String[] whereFieldsNameArr = null;
        Object[] whereFieldsValueArr = null;

        if ((whereFieldsName != null) && (whereFieldsValue != null) && whereFieldsName.length() > 0) {
            whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
            whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|")));
            for (int iFields = 0; iFields < whereFieldsNameArr.length; iFields++) {
                if (Boolean.TRUE.equals(LPPlatform.isEncryptedField(procInstanceName, GlobalVariables.Schemas.DATA.getName(), TblsData.TablesData.SAMPLE.getTableName(), whereFieldsNameArr[iFields]))) {
                    Map<String, String> hm = LPPlatform.encryptEncryptableFieldsAddBoth(whereFieldsNameArr[iFields], whereFieldsValueArr[iFields].toString());
                    whereFieldsNameArr[iFields] = hm.keySet().iterator().next();
                    if (hm.get(whereFieldsNameArr[iFields]).length() != whereFieldsNameArr[iFields].length()) {
                        String newWhereFieldValues = hm.get(whereFieldsNameArr[iFields]);
                        whereFieldsValueArr[iFields] = newWhereFieldValues;
                    }
                }
                if (whereFieldsValueArr.length > iFields) {
                    String[] tokenFieldValue = Token.getTokenFieldValue(whereFieldsValueArr[iFields].toString(), procReqInstance.getTokenString());
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(tokenFieldValue[0])) {
                        whereFieldsValueArr[iFields] = tokenFieldValue[1];
                    }
                }
            }
        }
        String[] sortFieldsNameArr = null;
        if (!((sortFieldsName == null || sortFieldsName.length() == 0) || (sortFieldsName.contains(GlobalAPIsParams.REQUEST_PARAM_VALUE_UNDEFINED)))) {
            sortFieldsNameArr = sortFieldsName.split("\\|");
        } else {
            sortFieldsNameArr = null;
        }
        if (whereFieldsValueArr != null) {
            for (int iFldV = 0; iFldV < whereFieldsValueArr.length; iFldV++) {
                if (whereFieldsValueArr[iFldV].toString().equalsIgnoreCase("false")) {
                    whereFieldsValueArr[iFldV] = Boolean.valueOf(whereFieldsValueArr[iFldV].toString());
                }
                if (whereFieldsValueArr[iFldV].toString().equalsIgnoreCase("true")) {
                    whereFieldsValueArr[iFldV] = Boolean.valueOf(whereFieldsValueArr[iFldV].toString());
                }
            }
        }
        if (TblsData.TablesData.SAMPLE.getTableName().equals(sampleLastLevel)) {

            //EnumIntTableFields[] tableFieldsFromString = EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE, sampleFieldToRetrieveArr);
            //sampleFieldToRetrieveArr = getAllFieldNames(tableFieldsFromString);
            Object[][] mySamples = QueryUtilitiesEnums.getTableData(TblsData.TablesData.SAMPLE,
                    sampleFieldToRetrieveArr,
                    whereFieldsNameArr, whereFieldsValueArr, sortFieldsNameArr, null, false);
            if (mySamples == null) {
                return new JSONArray();
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(mySamples[0][0].toString())) {
                return new JSONArray();
            } else {
                JSONArray mySamplesJSArr = new JSONArray();
                for (Object[] mySample : mySamples) {
                    JSONObject mySampleJSObj = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(sampleFieldToRetrieveArr), mySample);
                    if (Boolean.TRUE.equals(includeOnlyWhenResultsInProgress) && Boolean.FALSE.equals(isThereResultsInProgress(EnumIntTableFields.getAllFieldNames(sampleFieldToRetrieveArr), mySample))) {
                        continue;
                    }

                    if ("TRUE".equalsIgnoreCase(addSampleAnalysis)) {
                        String[] testWhereFieldsNameArr = new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName()};
                        testWhereFieldsNameArr = LPArray.addValueToArray1D(testWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));

                        testWhereFieldsNameArr = LPArray.getUniquesArray(testWhereFieldsNameArr);

                        Integer sampleIdPosicInArray = LPArray.valuePosicInArray(sampleFieldToRetrieveArr, TblsData.SampleAnalysis.SAMPLE_ID.getName());
                        Object[] testWhereFieldsValueArr = new Object[]{Integer.parseInt(mySample[sampleIdPosicInArray].toString())};
                        testWhereFieldsValueArr = LPArray.addValueToArray1D(testWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")));
                        if ("TRUE".equalsIgnoreCase(addSampleAnalysisResult)) {
                            sampleAnalysisFieldToRetrieveArr = LPArray.addValueToArray1D(sampleAnalysisFieldToRetrieveArr, TblsData.SampleAnalysis.TEST_ID.getName());
                        }
                        Object[][] mySampleAnalysis = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                                testWhereFieldsNameArr, testWhereFieldsValueArr, sampleAnalysisFieldToRetrieveArr);
                        JSONArray mySamplesAnaJSArr = new JSONArray();
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(mySampleAnalysis[0][0].toString())) {
                            mySampleJSObj.put(TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), mySamplesAnaJSArr);
                        } else {
                            for (Object[] mySampleAnalysi : mySampleAnalysis) {
                                JSONObject mySampleAnaJSObj = LPJson.convertArrayRowToJSONObject(sampleAnalysisFieldToRetrieveArr, mySampleAnalysi);
                                if ("TRUE".equalsIgnoreCase(addSampleAnalysisResult)) {
                                    String[] sarWhereFieldsNameArr = new String[]{TblsData.SampleAnalysis.TEST_ID.getName()};
                                    if (sampleAnalysisResultWhereFieldsName != null) {
                                        sarWhereFieldsNameArr = LPArray.addValueToArray1D(sarWhereFieldsNameArr, sampleAnalysisResultWhereFieldsName);
                                    }
                                    Integer testIdPosicInArray = LPArray.valuePosicInArray(sampleAnalysisFieldToRetrieveArr, TblsData.SampleAnalysis.TEST_ID.getName());
                                    Object[] sarWhereFieldsValueArr = new Object[]{Integer.parseInt(mySampleAnalysi[testIdPosicInArray].toString())};
                                    if (sampleAnalysisResultWhereFieldsValue != null) {
                                        sarWhereFieldsValueArr = LPArray.addValueToArray1D(sarWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(LPNulls.replaceNull(sampleAnalysisResultWhereFieldsValue).split("\\|")));
                                    }

                                    sarWhereFieldsValueArr = LPArray.getUniquesArray(sarWhereFieldsValueArr);
                                    Object[][] mySampleAnalysisResults = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                                            sarWhereFieldsNameArr, sarWhereFieldsValueArr, sampleAnalysisResultFieldToRetrieveArr);
                                    JSONArray mySamplesAnaResJSArr = new JSONArray();
                                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(mySampleAnalysisResults[0][0].toString())) {
                                        mySampleAnaJSObj.put(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), mySamplesAnaResJSArr);
                                    }
                                    JSONObject mySampleAnaResJSObj = new JSONObject();
                                    for (Object[] mySampleAnalysisResult : mySampleAnalysisResults) {
                                        mySampleAnaResJSObj = LPJson.convertArrayRowToJSONObject(sampleAnalysisResultFieldToRetrieveArr, mySampleAnalysisResult);
                                        mySamplesAnaResJSArr.add(mySampleAnaResJSObj);
                                    }
                                    mySampleAnaJSObj.put(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), mySamplesAnaResJSArr);
                                }
                                mySamplesAnaJSArr.add(mySampleAnaJSObj);
                            }
                            mySampleJSObj.put(TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), mySamplesAnaJSArr);
                        }
                    }
                    mySamplesJSArr.add(mySampleJSObj);
                }
                return mySamplesJSArr;
            }
        } else {
            whereFieldsNameArr = LPArray.addValueToArray1D(whereFieldsNameArr, "sample_id is not null");
            whereFieldsValueArr = LPArray.addValueToArray1D(whereFieldsValueArr, "");
            JSONArray samplesArray = new JSONArray();
            JSONArray sampleArray = new JSONArray();
            Object[][] mySamples = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(),
                    whereFieldsNameArr, whereFieldsValueArr, EnumIntTableFields.getAllFieldNames(sampleFieldToRetrieveArr));
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(mySamples[0][0].toString())) {
                return new JSONArray();
            }
            for (Object[] currSample : mySamples) {
                Integer sampleId = Integer.valueOf(currSample[0].toString());
                JSONObject sampleObj = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(sampleFieldToRetrieveArr), currSample);
                if (("TEST".equals(sampleLastLevel)) || ("RESULT".equals(sampleLastLevel))) {
                    String[] testWhereFieldsNameArr = new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName()};
                    Object[] testWhereFieldsValueArr = new Object[]{sampleId};
                    Object[][] mySampleAnalysis = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                            testWhereFieldsNameArr, testWhereFieldsValueArr, sampleAnalysisFieldToRetrieveArr);
                    for (Object[] mySampleAnalysi : mySampleAnalysis) {
                        JSONObject testObj = new JSONObject();
                        for (int ySmpAna = 0; ySmpAna < mySampleAnalysis[0].length; ySmpAna++) {
                            if (mySampleAnalysi[ySmpAna] instanceof Timestamp) {
                                testObj.put(sampleAnalysisFieldToRetrieveArr[ySmpAna], mySampleAnalysi[ySmpAna].toString());
                            } else {
                                testObj.put(sampleAnalysisFieldToRetrieveArr[ySmpAna], mySampleAnalysi[ySmpAna]);
                            }
                        }
                        sampleArray.add(testObj);
                    }
                    sampleObj.put(TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), sampleArray);
                }
                sampleArray.add(sampleObj);
            }
            samplesArray.add(sampleArray);
            return samplesArray;
        }

    }

    private JSONArray sampleStageDataJsonArr(Integer sampleId, String[] sampleFldName, Object[] sampleFldValue, String[] sampleStageFldName, Object[] sampleStageFldValue) {
        if (sampleStageFldValue == null) {
            return new JSONArray();
        }
        if (Boolean.FALSE.equals(LPArray.valueInArray(sampleStageFldName, TblsProcedure.SampleStageTimingCapture.STAGE_CURRENT.getName()))) {
            return new JSONArray();
        }
        String currentStage = sampleStageFldValue[LPArray.valuePosicInArray(sampleStageFldName, TblsProcedure.SampleStageTimingCapture.STAGE_CURRENT.getName())].toString();
        JSONObject jObj = new JSONObject();
        JSONArray jArrMainObj = new JSONArray();
        JSONArray jArrMainObj2 = new JSONArray();
        switch (currentStage.toUpperCase()) {
            case "SAMPLING":
                jObj.put(TblsEnvMonitData.Sample.SAMPLING_DATE.getName(), sampleFldValue[LPArray.valuePosicInArray(sampleFldName, TblsEnvMonitData.Sample.SAMPLING_DATE.getName())].toString());
                jObj.put(GlobalAPIsParams.LBL_FIELD_NAME, TblsEnvMonitData.Sample.SAMPLING_DATE.getName());
                jObj.put(GlobalAPIsParams.LBL_FIELD_VALUE, sampleFldValue[LPArray.valuePosicInArray(sampleFldName, TblsEnvMonitData.Sample.SAMPLING_DATE.getName())].toString());
                jArrMainObj.add(jObj);
                return jArrMainObj;
            case "INCUBATION":
                String[] incub1Flds = new String[]{TblsEnvMonitData.Sample.INCUBATION_PASSED.getName(), TblsEnvMonitData.Sample.INCUBATION_INCUBATOR.getName(), TblsEnvMonitData.Sample.INCUBATION_BATCH.getName(),
                    TblsEnvMonitData.Sample.INCUBATION_START.getName(), TblsEnvMonitData.Sample.INCUBATION_START_TEMP_EVENT_ID.getName(), TblsEnvMonitData.Sample.INCUBATION_START_TEMPERATURE.getName(),
                    TblsEnvMonitData.Sample.INCUBATION_END.getName(), TblsEnvMonitData.Sample.INCUBATION_END_TEMP_EVENT_ID.getName(), TblsEnvMonitData.Sample.INCUBATION_END_TEMPERATURE.getName()};
                for (String curFld : incub1Flds) {
                    Integer fldPosic = LPArray.valuePosicInArray(sampleFldName, curFld);
                    if (fldPosic > -1) {
                        jObj = new JSONObject();
                        jObj.put(curFld, sampleFldValue[fldPosic].toString());
                        jArrMainObj.add(jObj);
                        JSONObject jObjSampleStageInfo = new JSONObject();
                        jObjSampleStageInfo.put(GlobalAPIsParams.LBL_FIELD_NAME, curFld);
                        jObjSampleStageInfo.put(GlobalAPIsParams.LBL_FIELD_VALUE, sampleFldValue[fldPosic].toString());
                        jArrMainObj.add(jObjSampleStageInfo);
                    }
                    curFld = curFld.replace("incubation", "incubation2");
                    fldPosic = LPArray.valuePosicInArray(sampleFldName, curFld);
                    if (fldPosic > -1) {
                        jObj = new JSONObject();
                        jObj.put(curFld, sampleFldValue[fldPosic].toString());
                        jArrMainObj2.add(jObj);
                        JSONObject jObjSampleStageInfo = new JSONObject();
                        jObjSampleStageInfo.put(GlobalAPIsParams.LBL_FIELD_NAME, curFld);
                        jObjSampleStageInfo.put(GlobalAPIsParams.LBL_FIELD_VALUE, sampleFldValue[fldPosic].toString());
                        jArrMainObj2.add(jObjSampleStageInfo);
                    }
                }
                JSONObject jObj2 = new JSONObject();
                jObj2.put("incubation_1", jArrMainObj);
                jObj2.put("incubation_2", jArrMainObj2);
                jArrMainObj = new JSONArray();
                jArrMainObj.add(jObj2);
                return jArrMainObj;
            case "PLATEREADING":
            case "MICROORGANISMIDENTIFICATION":
                String[] tblAllFlds = EnumIntViewFields.getAllFieldNames(TblsEnvMonitData.ViewSampleMicroorganismList.values());
                Object[][] sampleStageInfo = QueryUtilitiesEnums.getViewData(TblsEnvMonitData.ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW,
                        EnumIntViewFields.getViewFieldsFromString(TblsEnvMonitData.ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW, tblAllFlds),
                        new SqlWhere(TblsEnvMonitData.ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW, new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.SAMPLE_ID.getName()}, new Object[]{sampleId}),
                        new String[]{TblsEnvMonitData.ViewSampleMicroorganismList.TEST_ID.getName(), TblsEnvMonitData.ViewSampleMicroorganismList.RESULT_ID.getName()});
                jObj = new JSONObject();
                jObj2 = new JSONObject();
                for (int iFlds = 0; iFlds < sampleStageInfo[0].length; iFlds++) {
                    jObj2.put(tblAllFlds[iFlds], sampleStageInfo[0][iFlds].toString());
                    JSONObject jObjSampleStageInfo = new JSONObject();
                    jObjSampleStageInfo.put(GlobalAPIsParams.LBL_FIELD_NAME, tblAllFlds[iFlds]);
                    jObjSampleStageInfo.put(GlobalAPIsParams.LBL_FIELD_VALUE, sampleStageInfo[0][iFlds].toString());
                    jArrMainObj.add(jObjSampleStageInfo);
                }
                jObj.put("counting", jObj2);
                jArrMainObj.add(jObj);
                return jArrMainObj;
            default:
                return jArrMainObj;
        }
    }

    static Object[] sampleAnalysisResultLockData(String[] resultFieldToRetrieveArr, Object[] curRow) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] fldNameArr = new String[0];
        Object[] fldValueArr = new Object[0];
        Integer resultFldPosic = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.SampleAnalysisResult.RESULT_ID.getName());
        Integer resultId = Integer.valueOf(curRow[resultFldPosic].toString());

        if (Boolean.FALSE.equals(isProgramCorrectiveActionEnable(procInstanceName))) {
            return new Object[]{fldNameArr, fldValueArr};
        }
        Object[][] notClosedProgramCorrreciveAction = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(),
                new String[]{TblsProcedure.ProgramCorrectiveAction.RESULT_ID.getName(), TblsProcedure.ProgramCorrectiveAction.STATUS.getName() + "<>"},
                new Object[]{resultId, DataProgramCorrectiveAction.ProgramCorrectiveStatus.CLOSED.toString()},
                SAMPLEANALYSISRESULTLOCKDATA_RETRIEVEDATA_PROGRAMCORRECTIVEACTION);
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(notClosedProgramCorrreciveAction[0][0].toString()))) {
            String notifMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataProgramCorrectiveAction.DataProgramCorrectiveActionBusinessRules.STILLOPEN_NOTIFMODE.getAreaName(), DataProgramCorrectiveAction.DataProgramCorrectiveActionBusinessRules.STILLOPEN_NOTIFMODE.getTagName());
            switch (notifMode.toLowerCase()) {
                case "silent":
                    return new Object[]{fldNameArr, fldValueArr};
                case "warning":
                    fldNameArr = LPArray.addValueToArray1D(fldNameArr, "has_warning");
                    fldValueArr = LPArray.addValueToArray1D(fldValueArr, true);
                    fldNameArr = LPArray.addValueToArray1D(fldNameArr, "warning_object");
                    fldValueArr = LPArray.addValueToArray1D(fldValueArr, TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName());

                    String msgCode = "resultLockedByProgramCorrectiveActionInProgress";
                    fldValueArr = LPArray.addValueToArray1D(fldValueArr, msgCode);
                    String errorTextEn = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_WARNING_REASONS, null, msgCode, DEFAULTLANGUAGE, null, true, null);
                    String errorTextEs = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_WARNING_REASONS, null, msgCode, "es", null, false, null);
                    JSONObject reasonInfo = new JSONObject();
                    reasonInfo.put(GlobalAPIsParams.LBL_MESSAGE_EN, errorTextEn);
                    reasonInfo.put(GlobalAPIsParams.LBL_MESSAGE_ES, errorTextEs);
                    return new Object[]{fldNameArr, fldValueArr, "warning_reason", reasonInfo};
                case "locking":
                default:
                    fldNameArr = LPArray.addValueToArray1D(fldNameArr, "is_locked");
                    fldValueArr = LPArray.addValueToArray1D(fldValueArr, true);
                    fldNameArr = LPArray.addValueToArray1D(fldNameArr, "locking_object");
                    fldValueArr = LPArray.addValueToArray1D(fldValueArr, TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName());
                    msgCode = "resultLockedByProgramCorrectiveActionInProgress";
                    fldValueArr = LPArray.addValueToArray1D(fldValueArr, msgCode);
                    errorTextEn = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_LOCKING_REASONS, null, msgCode, DEFAULTLANGUAGE, null, true, null);
                    errorTextEs = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_LOCKING_REASONS, null, msgCode, "es", null, false, null);
                    reasonInfo = new JSONObject();
                    reasonInfo.put(GlobalAPIsParams.LBL_MESSAGE_EN, errorTextEn);
                    reasonInfo.put(GlobalAPIsParams.LBL_MESSAGE_ES, errorTextEs);
                    return new Object[]{fldNameArr, fldValueArr, "locking_reason", reasonInfo};
            }
        }
        return new Object[]{fldNameArr, fldValueArr};
    }

    private static Boolean isThereResultsInProgress(String[] fldsName, Object[] fldsValue) {
        Integer smFldPosic = LPArray.valuePosicInArray(fldsName, TblsData.Sample.SAMPLE_ID.getName());
        if (smFldPosic == -1) {
            return false;
        }
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] groupedInfo = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                new String[]{TblsData.SampleAnalysis.STATUS.getName()},
                new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName(), TblsData.SampleAnalysis.STATUS.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.NOT_IN.getSqlClause()},
                new Object[]{fldsValue[smFldPosic],
                    SampleStatuses.REVIEWED.getStatusCode("") + "|" + SampleStatuses.CANCELED.getStatusCode("")},
                null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(groupedInfo[0][0].toString())) {
            return false;
        }
        return (groupedInfo[0][0].toString().length() > 0);
    }

    static Object[] getObjectsId(String[] headerFlds, Object[][] analysisResultList, String separator) {
        if (analysisResultList == null || analysisResultList.length == 0) {
            return new Object[]{};
        }
        Object[] objIds = new Object[]{};
        for (Object[] curRow : analysisResultList) {
            String curTest = TblsData.SampleAnalysisResult.TEST_ID.getName() + separator + curRow[LPArray.valuePosicInArray(headerFlds, TblsData.SampleAnalysisResult.TEST_ID.getName())].toString();
            if (Boolean.FALSE.equals(LPArray.valueInArray(objIds, curTest))) {
                objIds = LPArray.addValueToArray1D(objIds, curTest);
            }
            String curResult = TblsData.SampleAnalysisResult.RESULT_ID.getName() + separator + curRow[LPArray.valuePosicInArray(headerFlds, TblsData.SampleAnalysisResult.RESULT_ID.getName())].toString();
            if (Boolean.FALSE.equals(LPArray.valueInArray(objIds, curResult))) {
                objIds = LPArray.addValueToArray1D(objIds, curResult);
            }
        }
        return objIds;
    }

    /**
     * @return the messageDynamicData
     */
    @Override
    public Object[] getMessageDynamicData() {
        return messageDynamicData;
    }

    /**
     * @return the relatedObj
     */
    @Override
    public RelatedObjects getRelatedObj() {
        return relatedObj;
    }

    /**
     * @return the endpointExists
     */
    @Override
    public Boolean getEndpointExists() {
        return endpointExists;
    }

    /**
     * @return the diagnostic
     */
    @Override
    public Object[] getDiagnostic() {
        return diagnostic;
    }

    /**
     * @return the functionFound
     */
    @Override
    public Boolean getFunctionFound() {
        return functionFound;
    }

    /**
     * @return the isSuccess
     */
    @Override
    public Boolean getIsSuccess() {
        return isSuccess;
    }

    /**
     * @return the contentSuccessResponse
     */
    public Object getResponseContentJArr() {
        return getResponseSuccessJArr();
    }

    public Object getResponseContentJObj() {
        return responseSuccessJObj;
    }

    @Override
    public Object[] getResponseError() {
        return responseError;
    }

    /**
     * @return the responseSuccessJArr
     */
    @Override
    public JSONArray getResponseSuccessJArr() {
        return responseSuccessJArr;
    }

    @Override
    public JSONObject getResponseSuccessJObj() {
        return this.responseSuccessJObj;
    }

    public static JSONArray configAnalysisList(String alternativeProcInstanceName) {
        String[] fieldsToRetrieve = getAllFieldNames(TblsCnfg.TablesConfig.ANALYSIS_METHOD, alternativeProcInstanceName);
        Object[][] analysisMethodsList = QueryUtilitiesEnums.getTableData(TblsCnfg.TablesConfig.ANALYSIS_METHOD,
                EnumIntTableFields.getAllFieldNamesFromDatabase(TblsCnfg.TablesConfig.ANALYSIS_METHOD, alternativeProcInstanceName),
                new String[]{TblsCnfg.AnalysisMethod.ANALYSIS.getName() + "<>"},
                new Object[]{">>>"},
                new String[]{TblsCnfg.AnalysisMethod.ANALYSIS.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()}, alternativeProcInstanceName);
        JSONArray jArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisMethodsList[0][0].toString()))) {
            for (Object[] currAnalysisMeth : analysisMethodsList) {
                JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currAnalysisMeth);
                jArr.add(jObj);
            }
        }
        return jArr;
    }

}
