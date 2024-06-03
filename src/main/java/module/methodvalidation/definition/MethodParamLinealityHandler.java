/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package module.methodvalidation.definition;

import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import functionaljavaa.samplestructure.DataSample;
import java.math.BigDecimal;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPMath;
import lbplanet.utilities.LPPlatform;
import module.methodvalidation.definition.TblsMethodValidationData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntViewFields;
import trazit.queries.QueryUtilities;
import trazit.session.ProcedureRequestSession;
import lbplanet.utilities.LPDate;
import databases.RdbmsObject;

public class MethodParamLinealityHandler implements MethodParamsHandler {

    @Override
    public JSONObject paramDataForQuery(String curProjName, String parameterName, String sequenceName, String analyticalParameter, String procInstanceName) {
        JSONObject jObj = new JSONObject();
        Object[] resultsAndChartResults = resultsAndChartResults(curProjName, parameterName, sequenceName, analyticalParameter, procInstanceName);
        jObj.put("title_en", "Lineality Assay");
        jObj.put("title_es", "Ensayo linealidad");
        if (resultsAndChartResults.length>0)
            jObj.put("total_samples", (Integer) resultsAndChartResults[0]);
        if (resultsAndChartResults.length>1)
            jObj.put("results", (JSONArray) resultsAndChartResults[1]);
        if (resultsAndChartResults.length>2)
        jObj.put("chart_results", (JSONArray) resultsAndChartResults[2]);
        jObj.put("final_results", (JSONObject) finalResults(curProjName, parameterName, sequenceName, analyticalParameter, procInstanceName));
        return jObj;
    }
    private Object[] resultsAndChartResults(String curProjName, String parameterName, String sequenceName, String analyticalParameter, String procInstanceName) {
        Object[] mainObjArr = new Object[]{};
     //   if (1==1) return jObj;

        // Fetch samples information
        Object[][] samplesInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsData.TablesData.SAMPLE.getRepositoryName()), TblsData.TablesData.SAMPLE.getTableName(),
            new String[]{TblsMethodValidationData.Sample.PROJECT.getName(), TblsMethodValidationData.Sample.ANALYTICAL_PARAMETER.getName(), 
                    parameterName!=null?TblsMethodValidationData.Sample.PARAMETER_NAME.getName():TblsMethodValidationData.Sample.ANALYTICAL_SEQUENCE_NAME.getName()},
            new Object[]{curProjName, analyticalParameter, parameterName!=null?parameterName:sequenceName}, 
            new String[]{TblsData.Sample.SAMPLE_ID.getName()},
            new String[]{TblsData.Sample.SAMPLE_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(samplesInfo[0][0].toString())) {
            return mainObjArr;
        }
        JSONArray resultsJArr = new JSONArray();
        JSONArray testChartJArr = new JSONArray();
        String finalResult = "";
        int i = 0;
        Integer totalSamples=null;
        for (Object[] curSmp : samplesInfo) {
            JSONObject sampleJObj = new JSONObject();
            String[] fldsToGetTests = new String[]{TblsMethodValidationData.SampleAnalysis.TEST_ID.getName(),
                TblsMethodValidationData.SampleAnalysis.THEORETICAL_VALUE.getName()};
            Object[][] testsInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsMethodValidationData.TablesMethodValidationData.SAMPLE_ANALYSIS.getRepositoryName()), TblsMethodValidationData.TablesMethodValidationData.SAMPLE_ANALYSIS.getTableName(),
                new String[]{TblsMethodValidationData.SampleAnalysis.SAMPLE_ID.getName()}, new Object[]{Integer.valueOf(curSmp[0].toString())},
                fldsToGetTests, new String[]{TblsMethodValidationData.SampleAnalysis.SAMPLE_ID.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testsInfo[0][0].toString())) {
                return mainObjArr;
            }
            i++;
            JSONObject testChartJObj = new JSONObject();
            for (Object[] curTst : testsInfo) {
                testChartJObj.put(TblsMethodValidationData.SampleAnalysis.THEORETICAL_VALUE.getName(),
                    curTst[LPArray.valuePosicInArray(fldsToGetTests, TblsMethodValidationData.SampleAnalysis.THEORETICAL_VALUE.getName())]);
                int iInj = 0;
                String[] fldsToGetSmpRslt = new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.RESULT_ID.getName(), TblsData.SampleAnalysisResult.PARAM_NAME.getName(), TblsData.SampleAnalysisResult.PARAM_TYPE.getName(), TblsData.SampleAnalysisResult.PRETTY_VALUE.getName(), TblsData.SampleAnalysisResult.UOM.getName()};
                Object[][] resultsInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getRepositoryName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                    new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()}, 
                    new Object[]{Integer.valueOf(curTst[0].toString())}, fldsToGetSmpRslt, 
                    new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultsInfo[0][0].toString())) {
                    return mainObjArr;
                }
                totalSamples=samplesInfo.length;
                for (Object[] curRslt : resultsInfo) {
                    if ("CALC".equalsIgnoreCase(curRslt[4].toString())) {
                        finalResult = curRslt[5].toString();
                    } else {
                        iInj++;
                        sampleJObj = LPJson.convertArrayRowToJSONObject(fldsToGetSmpRslt, curRslt);
                        sampleJObj.put("order_number", i);
                        sampleJObj.put("name", "sample " + i);
                        sampleJObj.put("injection", "Inj " + iInj);
                        sampleJObj.put("analytical_parameter", analyticalParameter);
                        sampleJObj.put("parameter_name", parameterName);
                        sampleJObj.put("result", curRslt[5]);
                        sampleJObj.put("sample_id", Integer.valueOf(curRslt[0].toString()));
                        sampleJObj.put("test_id", Integer.valueOf(curRslt[1].toString()));
                        sampleJObj.put("result_id", Integer.valueOf(curRslt[2].toString()));
                        sampleJObj.put("final_result", "");
                        sampleJObj.put(TblsMethodValidationData.SampleAnalysis.THEORETICAL_VALUE.getName(),
                            curTst[LPArray.valuePosicInArray(fldsToGetTests, TblsMethodValidationData.SampleAnalysis.THEORETICAL_VALUE.getName())]);
                        sampleJObj.put(TblsMethodValidationData.SampleAnalysis.THEORETICAL_VALUE.getName(), curTst[LPArray.valuePosicInArray(fldsToGetTests, TblsMethodValidationData.SampleAnalysis.THEORETICAL_VALUE.getName())]);
                        resultsJArr.add(sampleJObj);
                    }
                }
            }
            JSONObject objToModify = (JSONObject) resultsJArr.get(resultsJArr.size() - 1); // Last added object
            objToModify.put("final_result", finalResult); // Update the final result
            testChartJObj.put("value", finalResult);
            testChartJArr.add(testChartJObj);
            resultsJArr.add(resultsJArr.size() - 1, objToModify);
        }
        return new Object[]{totalSamples, resultsJArr, testChartJArr};
    }

    private JSONObject finalResults(String curProjName, String parameterName, String sequenceName, String analyticalParameter, String procInstanceName){
        JSONArray myData=new JSONArray();
        if (parameterName!=null){
            myData= QueryUtilities.dbRowsToJsonArrSimpleJson(procInstanceName,
            LPPlatform.buildSchemaName(procInstanceName, TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS_CALCS.getRepositoryName()),
            TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS_CALCS.getTableName(),
                EnumIntTableFields.getAllFieldNames(TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS_CALCS.getTableFields(), new String[]{}),
                new String[]{TblsMethodValidationData.ValidationMethodParamsCalcs.ANALYTICAL_PARAMETER.getName(), TblsMethodValidationData.ValidationMethodParamsCalcs.VAL_PARAM_NAME.getName()},
                new Object[]{analyticalParameter, parameterName},
                new String[]{TblsMethodValidationData.ValidationMethodParamsCalcs.VAL_PARAM_NAME.getName()},
                new String[]{}, true, true);
        }else{
            return new JSONObject();
/*            myData= QueryUtilities.dbRowsToJsonArrSimpleJson(procInstanceName,
            LPPlatform.buildSchemaName(procInstanceName, TblsProjectRnDData.TablesProjectRnDData.METHOD_DEVELOPMENT_SEQUENCE.getRepositoryName()),
            TblsProjectRnDData.TablesProjectRnDData.VALIDATION_METHOD_PARAMS_CALCS.getTableName(),
                EnumIntTableFields.getAllFieldNames(TblsProjectRnDData.TablesProjectRnDData.VALIDATION_METHOD_PARAMS_CALCS.getTableFields(), new String[]{}),
                new String[]{TblsProjectRnDData.ValidationMethodParamsCalcs.ANALYTICAL_PARAMETER.getName(), TblsMethodValidationData.ValidationMethodParamsCalcs.VAL_PARAM_NAME.getName()},
                new Object[]{analyticalParameter, sequenceName},
                new String[]{TblsProjectRnDData.ValidationMethodParamsCalcs.VAL_PARAM_NAME.getName()},
                new String[]{}, true, true);    */
        }
        JSONObject result = new JSONObject();
        //if (1==1) return result;
        for (int i = 0; i < myData.size(); i++) {
            JSONObject row = (JSONObject) myData.get(i);
            String key = row.get(TblsMethodValidationData.ValidationMethodParamsCalcs.PARAM_REPORTED_NAME.getName()).toString();
            String value = row.get(TblsMethodValidationData.ValidationMethodParamsCalcs.PRETTY_VALUE.getName()).toString();
            result.put(key, value);
        }
        return result;
    }

    @Override
    public void calcParamResults(Integer resultId, Integer testId, Integer sampleId, DataSample dataSample, String analyticalParameter, String parameterName, String sequenceName, String project){
        ProcedureRequestSession procReqSession=ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=procReqSession.getProcedureInstance();

        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsMethodValidationData.ViewSampleAndResult.PROJECT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{project}, "");
        if (parameterName!=null)
            sqlWhere.addConstraint(TblsMethodValidationData.ViewSampleAndResult.PARAMETER_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{parameterName}, "");
        if (sequenceName!=null)
            sqlWhere.addConstraint(TblsMethodValidationData.ViewSampleAndResult.ANALYTICAL_SEQUENCE_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sequenceName}, "");
        sqlWhere.addConstraint(TblsMethodValidationData.ViewSampleAndResult.ANALYTICAL_PARAMETER, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{analyticalParameter}, "");
        sqlWhere.addConstraint(TblsMethodValidationData.ViewSampleAndResult.PARAM_TYPE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{"CALC"}, "");
        sqlWhere.addConstraint(TblsMethodValidationData.ViewSampleAndResult.ANALYSIS, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{"lineality"}, "");
        sqlWhere.addConstraint(TblsMethodValidationData.ViewSampleAndResult.RAW_VALUE, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, "");
        Object[][] samplesCalcResultsInfo = Rdbms.getRecordFieldsByFilterForViews(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsMethodValidationData.ViewsMethodValidationData.SAMPLE_AND_RESULTS_VIEW.getRepositoryName()),
            TblsMethodValidationData.ViewsMethodValidationData.SAMPLE_AND_RESULTS_VIEW,
        sqlWhere,
        new EnumIntViewFields[]{TblsMethodValidationData.ViewSampleAndResult.PARAMETER_NAME, TblsMethodValidationData.ViewSampleAndResult.RAW_VALUE,
            TblsMethodValidationData.ViewSampleAndResult.PROJECT}, null, true);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(samplesCalcResultsInfo[0][0].toString())){
            return;
        }

        Object[][] valMethodParamCalcs = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS_CALCS.getRepositoryName()),
            TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS_CALCS,
        new SqlWhere(TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS_CALCS,
            new String[]{TblsMethodValidationData.ValidationMethodParamsCalcs.PROJECT.getName(), TblsMethodValidationData.ValidationMethodParamsCalcs.VAL_PARAM_NAME.getName(), TblsMethodValidationData.ValidationMethodParamsCalcs.ANALYTICAL_PARAMETER.getName()},
        new Object[]{project, parameterName, analyticalParameter}),
        new EnumIntTableFields[]{TblsMethodValidationData.ValidationMethodParamsCalcs.CALC_NAME, TblsMethodValidationData.ValidationMethodParamsCalcs.VALUE,
            TblsMethodValidationData.ValidationMethodParamsCalcs.PRETTY_VALUE, TblsMethodValidationData.ValidationMethodParamsCalcs.DECIMAL_PLACES,
            TblsMethodValidationData.ValidationMethodParamsCalcs.PROJECT}, null, true);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(valMethodParamCalcs[0][0].toString())){
            return;
        }
        String[] finalResultsToCalc=new String[]{"MIN", "MAX", "AVERAGE", "STD_DEV"};
        Integer decPlaces=3;
        String[] calcValues=null;
        for (String curCalc: finalResultsToCalc){
            RdbmsObject diagnoseObj = null;
            switch(curCalc.toUpperCase()){
                case "MIN":
                    calcValues=LPMath.calculateMin(LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(samplesCalcResultsInfo, 1)), decPlaces);
                    break;
                case "MAX":
                    calcValues=LPMath.calculateMax(LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(samplesCalcResultsInfo, 1)), decPlaces);
                    break;
                case "AVERAGE":
                    calcValues=LPMath.calculateAverage(LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(samplesCalcResultsInfo, 1)), decPlaces);
                    break;
                case "STD_DEV":
                    calcValues=LPMath.calculateStandardDeviation(LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(samplesCalcResultsInfo, 1)), decPlaces);
                    break;
            }
            Integer calcAlreadyInValParam=LPArray.valuePosicInArray2D(valMethodParamCalcs, curCalc.toUpperCase(), 0);
            if (calcAlreadyInValParam==-1){
                String[] insertFldsN=new String[]{TblsMethodValidationData.ValidationMethodParamsCalcs.VAL_PARAM_NAME.getName(), TblsMethodValidationData.ValidationMethodParamsCalcs.ANALYTICAL_PARAMETER.getName(),
                    TblsMethodValidationData.ValidationMethodParamsCalcs.VALUE.getName(), TblsMethodValidationData.ValidationMethodParamsCalcs.PRETTY_VALUE.getName(),
                    TblsMethodValidationData.ValidationMethodParamsCalcs.ENTERED_BY.getName(), TblsMethodValidationData.ValidationMethodParamsCalcs.ENTERED_ON.getName(),
                    TblsMethodValidationData.ValidationMethodParamsCalcs.CALC_NAME.getName(), TblsMethodValidationData.ValidationMethodParamsCalcs.PARAM_REPORTED_NAME.getName(),
                    TblsMethodValidationData.ValidationMethodParamsCalcs.PROJECT.getName(), TblsMethodValidationData.ValidationMethodParamsCalcs.ORDER_NUMBER.getName(),};
                Object[] insertFldsV=new Object[]{parameterName, analyticalParameter, BigDecimal.valueOf(Double.valueOf(calcValues[0].toString())), calcValues[1].toString(), procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp(),
                    curCalc, curCalc, project, valMethodParamCalcs.length+1};
                diagnoseObj = Rdbms.insertRecordInTable(TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS_CALCS, 
                    insertFldsN, insertFldsV);
                
            }else{
                String[] updFieldNames=new String[]{TblsMethodValidationData.ValidationMethodParamsCalcs.VAL_PARAM_NAME.getName(), TblsMethodValidationData.ValidationMethodParamsCalcs.ANALYTICAL_PARAMETER.getName(),                        
                    TblsMethodValidationData.ValidationMethodParamsCalcs.VALUE.getName(), TblsMethodValidationData.ValidationMethodParamsCalcs.PRETTY_VALUE.getName(),
                    TblsMethodValidationData.ValidationMethodParamsCalcs.ENTERED_BY.getName(), TblsMethodValidationData.ValidationMethodParamsCalcs.ENTERED_ON.getName()};
                Object[] updFieldValues=new Object[]{parameterName, analyticalParameter, BigDecimal.valueOf(Double.valueOf(calcValues[0].toString())), calcValues[1].toString(),
                    procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};

                sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsMethodValidationData.ValidationMethodParamsCalcs.PROJECT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{project}, "");
                sqlWhere.addConstraint(TblsMethodValidationData.ValidationMethodParamsCalcs.VAL_PARAM_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{parameterName}, "");
                sqlWhere.addConstraint(TblsMethodValidationData.ValidationMethodParamsCalcs.ANALYTICAL_PARAMETER, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{analyticalParameter}, "");
                sqlWhere.addConstraint(TblsMethodValidationData.ValidationMethodParamsCalcs.CALC_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curCalc.toUpperCase()}, "");
                diagnoseObj = Rdbms.updateTableRecordFieldsByFilter(TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS_CALCS,
                        EnumIntTableFields.getTableFieldsFromString(TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS_CALCS, updFieldNames), updFieldValues, sqlWhere, null);                
            }
            Boolean diagn=diagnoseObj.getRunSuccess();
        }


    }
}

