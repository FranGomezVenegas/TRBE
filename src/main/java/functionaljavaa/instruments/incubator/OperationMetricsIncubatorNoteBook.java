/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package functionaljavaa.instruments.incubator;

import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import java.time.LocalDateTime;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPMath;
import lbplanet.utilities.LPPlatform;
import module.monitoring.definition.TblsEnvMonitData;
import module.monitoring.definition.TblsEnvMonitProcedure;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import static trazit.procedureinstance.definition.logic.ClassReqProcedureQueries.dbRowsToJsonArr;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class OperationMetricsIncubatorNoteBook {

    public static JSONObject incubTempReadingStatistics(String incubName, String startDate, String endDate) {
        //case GET_STAGES_TIMING_CAPTURE_DATA:
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        String procInstanceName = procReqInstance.getProcedureInstance();

        JSONObject jObjMainObject = new JSONObject();
        SqlWhere wObj = new SqlWhere();
        SqlWhere wObj2 = new SqlWhere();
        Object[] whereForPercentagesView = new Object[]{};
        Object[] buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName(), 
                startDate, endDate);
        
        //Object[] buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName(), 
        //        LPDate.(startDate.toString()), LPDate.stringFormatToDate(endDate.toString()));
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString()))) {
            if (buildDateRangeFromStrings.length == 4) {
                wObj.addConstraint(TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON, SqlStatement.WHERECLAUSE_TYPES.BETWEEN, new Object[]{buildDateRangeFromStrings[2], buildDateRangeFromStrings[3]}, null);
                wObj2.addConstraint(TblsEnvMonitProcedure.IncubatorTempReadingViolations.CREATED_ON, SqlStatement.WHERECLAUSE_TYPES.BETWEEN, new Object[]{buildDateRangeFromStrings[2], buildDateRangeFromStrings[3]}, null);
                whereForPercentagesView = LPArray.addValueToArray1D(whereForPercentagesView, new Object[]{buildDateRangeFromStrings[2], buildDateRangeFromStrings[3]});
            } else {
                wObj.addConstraint(TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{buildDateRangeFromStrings[2]}, null);
                wObj2.addConstraint(TblsEnvMonitProcedure.IncubatorTempReadingViolations.CREATED_ON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{buildDateRangeFromStrings[2]}, null);
                whereForPercentagesView = LPArray.addValueToArray1D(whereForPercentagesView, buildDateRangeFromStrings[2]);
            }
        }
        JSONArray qryJsonArr = dbRowsToJsonArr(procInstanceName, TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK,
                EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK, "ALL"),
                wObj, new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName()},
                null, true);
        jObjMainObject.put(TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK.getTableName(), qryJsonArr);
/*
        wObj.addConstraint(TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, null);
        Object[][] programInfo = QueryUtilitiesEnums.getTableData(TblsEnvMonitProcedure.TablesEnvMonitProcedure.INCUB_TEMP_READING_VIOLATIONS,
                new EnumIntTableFields[]{TblsEnvMonitProcedure.IncubatorTempReadingViolations.INCUBATOR, TblsEnvMonitProcedure.IncubatorTempReadingViolations.STARTED_ON,
                    TblsEnvMonitProcedure.IncubatorTempReadingViolations.ENDED_ON},
                wObj, null);
        Object[] durArr = new Double[]{};
        String[] allStages = LPArray.getUniquesArray(LPArray.getColumnFromArray2D(programInfo, 0));
        JSONArray statAnalysisArr = new JSONArray();
        for (String curStage : allStages) {
            Integer[] valueAllPosicInArray2D = LPArray.valueAllPosicInArray2D(programInfo, curStage, 0);
            Object[][] programInfoFiltered = new Object[valueAllPosicInArray2D.length][];
            for (int i = 0; i < valueAllPosicInArray2D.length; i++) {
                int rowIndex = valueAllPosicInArray2D[i];

                if (rowIndex >= 0 && rowIndex < programInfo.length) {
                    programInfoFiltered[i] = programInfo[rowIndex];
                } else {
                    // Handle out-of-bounds index if needed
                }
            }
            int iF = 0;
            for (Object[] curRow : programInfoFiltered) {
                LocalDateTime dStart = LPDate.stringFormatToLocalDateTime(curRow[1].toString() + ":00");
                LocalDateTime dEnd = LPDate.stringFormatToLocalDateTime(curRow[2].toString() + ":00");
                if (dStart != null && dEnd != null) {
                    long secondsInDateRange = LPDate.secondsInDateRange(dStart, dEnd);
                    if (!Double.isNaN(secondsInDateRange) && secondsInDateRange > 0) {
                        durArr = LPArray.addValueToArray1D(durArr, Double.valueOf(secondsInDateRange));
                    }
                }
                iF++;
            }
            double[] doubleArray = new double[durArr.length];
            for (int i = 0; i < durArr.length; i++) {
                if (durArr[i] instanceof Double) {
                    doubleArray[i] = ((Double) durArr[i]).doubleValue();
                } else {
                    // Handle the case where the element is not a Double
                    // You might want to throw an exception or provide a default value
                }
            }
            JSONObject statAnalysis = LPMath.statAnalysis((double[]) doubleArray, durArr, "Hours");
            statAnalysis.put("stage", curStage);
            statAnalysisArr.add(statAnalysis);
        }
        jObjMainObject.put("statistics_per_stage", statAnalysisArr);
*/
        String tblCreateScript = "select sample_id, min(started_on), max(ended_on) "
                + " FROM "
                + "    \"" + procInstanceName + "-data\"."+TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK.getTableName()+" st "
                + " WHERE    st.started_on >= ? AND st.started_on <= ? "
                + "GROUP BY st.sample_id;";
        Object[] durArr = new Object[]{};
        JSONArray sampleJsonArr = new JSONArray();
        Object[][] data = Rdbms.runQueryByString(tblCreateScript, 3, whereForPercentagesView);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(data[0][0].toString())) {
            JSONObject jObj = LPFrontEnd.responseJSONDiagnosticLPFalse(Rdbms.RdbmsErrorTrapping.TABLE_WITH_NO_RECORDS, new Object[0]);
            sampleJsonArr.add(jObj);
        } else {
            for (Object[] curRow : data) {
                if (curRow[1] != null && curRow[2] != null && curRow[1].toString().length() > 0 && curRow[2].toString().length() > 0) {
                    LocalDateTime dStart = LPDate.stringFormatToLocalDateTime(curRow[1].toString());
                    LocalDateTime dEnd = LPDate.stringFormatToLocalDateTime(curRow[2].toString());
                    if (dStart != null && dEnd != null) {
                        long secondsInDateRange = LPDate.secondsInDateRange(dStart, dEnd);
                        if (!Double.isNaN(secondsInDateRange) && secondsInDateRange > 0) {
                            durArr = LPArray.addValueToArray1D(durArr, Double.valueOf(secondsInDateRange));
                        }
                    }
                }
            }
            double[] doubleArray = new double[durArr.length];
            for (int i = 0; i < durArr.length; i++) {
                if (durArr[i] instanceof Double) {
                    doubleArray[i] = ((Double) durArr[i]).doubleValue();
                } else {
                    // Handle the case where the element is not a Double
                    // You might want to throw an exception or provide a default value
                }
            }
            JSONObject statAnalysis = LPMath.statAnalysis((double[]) doubleArray, durArr, "Days");
            jObjMainObject.put("statistics_per_end_to_end_sample_process", statAnalysis);
        }

        qryJsonArr = dbRowsToJsonArr(procInstanceName, TblsEnvMonitProcedure.TablesEnvMonitProcedure.INCUB_TEMP_READING_VIOLATIONS,
                EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitProcedure.TablesEnvMonitProcedure.INCUB_TEMP_READING_VIOLATIONS, "ALL"),
                wObj2, new String[]{TblsEnvMonitProcedure.IncubatorTempReadingViolations.CREATED_BY.getName()},
                null, true);
        jObjMainObject.put(TblsEnvMonitProcedure.TablesEnvMonitProcedure.INCUB_TEMP_READING_VIOLATIONS.getTableName(), qryJsonArr);

        tblCreateScript = "SELECT st.current_stage, "
                + " COUNT(DISTINCT sst.sample_id) AS violated_samples,"
                + " COUNT(DISTINCT st.sample_id) AS total_samples,"
                + " ROUND((COUNT(DISTINCT sst.sample_id) * 100.0 / COUNT(DISTINCT st.sample_id)), 4) AS percentage_violated"
                + " FROM \"" + procInstanceName + "-data\"."+TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK.getTableName()+" st "
                + " left outer JOIN \"" + procInstanceName + "-procedure\"."+TblsEnvMonitProcedure.TablesEnvMonitProcedure.INCUB_TEMP_READING_VIOLATIONS.getTableName()+" sst ON st.sample_id = sst.sample_id "
                + " WHERE    st.started_on >= ? AND st.started_on <= ? "
                + " GROUP BY st.current_stage;";
        sampleJsonArr = new JSONArray();
        data = Rdbms.runQueryByString(tblCreateScript, 4, whereForPercentagesView);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(data[0][0].toString())) {
            JSONObject jObj = LPFrontEnd.responseJSONDiagnosticLPFalse(Rdbms.RdbmsErrorTrapping.TABLE_WITH_NO_RECORDS, new Object[0]);
            sampleJsonArr.add(jObj);
        } else {
            for (Object[] curRec : data) {
                JSONObject jObj = LPJson.convertArrayRowToJSONObject(new String[]{"stage", "violated_sample_stages", "total_sample_stages", "percentage"},
                        curRec);
                sampleJsonArr.add(jObj);
            }
        }
        jObjMainObject.put("violations_percentage", sampleJsonArr);
        return jObjMainObject;
        //jObjMainObject.put(GlobalAPIsParams.LBL_TABLE, "GET_SCHEDULED_SAMPLES v1"); 
        //LPFrontEnd.servletReturnSuccess(request, response, jObjMainObject);
        //break;    
    }
}
