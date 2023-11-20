/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.queries;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import databases.TblsDataAudit;
import functionaljavaa.samplestructure.DataSample;
import static functionaljavaa.samplestructure.DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.CachedRowSet;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPKPIs;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.definition.definition.TblsReqs;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public final class QueryUtilities {
    static final String NO_DATA = "No Data";
    private static org.json.JSONArray convertArray2DtoJArrNEXT(Object[][] procTblRows, String[] fldsToGet, String[] jsonFlds, Boolean emptyWhenNoData) {
        org.json.JSONArray jBlockArr = new org.json.JSONArray();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procTblRows[0][0].toString())) {
            if (Boolean.TRUE.equals(emptyWhenNoData)) {
                return jBlockArr;
            }
            JSONObject jObj = new JSONObject();
            jObj.put(NO_DATA, NO_DATA);
            jBlockArr.put(jObj);
        } else {
            try {
                for (Object[] curRow : procTblRows) {
                    if (jsonFlds == null) {
                        jBlockArr.put(LPJson.convertArrayRowToJSONObject(fldsToGet, curRow));
                    } else {
                        JSONObject jObj = LPJson.convertArrayRowToJSONObject(fldsToGet, curRow, jsonFlds);
                        for (String curJsonFld : jsonFlds) {
                            jObj.put(TblsReqs.ProcedureMasterData.JSON_OBJ.getName(), JsonParser.parseString(curRow[LPArray.valuePosicInArray(fldsToGet, curJsonFld)].toString()).getAsJsonObject());
                        }
                        jBlockArr.put(jObj);
                    }
                }
            } catch (JsonSyntaxException e) {
                jBlockArr.put("Errors trying to get the master data records info. " + e.getMessage());
                return jBlockArr;
            }
        }
        return jBlockArr;
    }

    public static JSONArray dbSingleRowToJsonFldNameAndValueArr(String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue) {
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), tblName, whereFldName, whereFldValue, fldsToGet);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procTblRows[0][0].toString())) {
            JSONObject jObj = new JSONObject();
            jObj.put(NO_DATA, NO_DATA);
            JSONArray jArr = new JSONArray();
            jArr.add(jObj);
            return jArr;
        } else {
            return LPJson.convertArrayRowToJSONFieldNameAndValueObject(fldsToGet, procTblRows[0], null);
        }
    }

    public static JSONArray dbRowsToJsonArr(String procInstanceName, String schemaName, EnumIntTables tblObj, EnumIntTableFields[] fldsToGet, SqlWhere wObj, String[] sortFlds, String[] fldsToExclude, Boolean emptyWhenNoData) {
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaName, tblObj, wObj, fldsToGet, sortFlds, false);
        return convertArray2DtoJArr(procTblRows, EnumIntTableFields.getAllFieldNames(fldsToGet), fldsToExclude, emptyWhenNoData);
    }

    public static JSONArray dbRowsToJsonArr(String procInstanceName, String schemaName, String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue, String[] sortFlds, String[] fldsToExclude, Boolean emptyWhenNoData, Boolean inforceDistinct) {
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaName, tblName, whereFldName, whereFldValue, fldsToGet, sortFlds, inforceDistinct);
        return convertArray2DtoJArr(procTblRows, fldsToGet, fldsToExclude, emptyWhenNoData);
    }

    private static JSONArray convertArray2DtoJArr(Object[][] procTblRows, String[] fldsToGet, String[] jsonFlds, Boolean emptyWhenNoData) {
        JSONArray jBlockArr = new JSONArray();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procTblRows[0][0].toString())) {
            if (Boolean.TRUE.equals(emptyWhenNoData)) {
                return jBlockArr;
            }
            JSONObject jObj = new JSONObject();
            jObj.put(NO_DATA, NO_DATA);
            jBlockArr.add(jObj);
        } else {
            try {
                for (Object[] curRow : procTblRows) {
                    if (jsonFlds == null) {
                        jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsToGet, curRow));
                    } else {
                        JSONObject jObj = LPJson.convertArrayRowToJSONObject(fldsToGet, curRow, jsonFlds);
                        for (String curJsonFld : jsonFlds) {
                            jObj.put(TblsReqs.ProcedureMasterData.JSON_OBJ.getName(), JsonParser.parseString(curRow[LPArray.valuePosicInArray(fldsToGet, curJsonFld)].toString()).getAsJsonObject());
                        }
                        jBlockArr.add(jObj);
                    }
                }
            } catch (JsonSyntaxException e) {
                jBlockArr.add("Errors trying to get the master data records info. " + e.getMessage());
                return jBlockArr;
            }
        }
        return jBlockArr;
    }

    public static org.json.JSONArray dbRowsToJsonArrNEXT(String procInstanceName, String schemaName, String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue, String[] sortFlds, String[] fldsToExclude, Boolean emptyWhenNoData, Boolean inforceDistinct) {
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaName, tblName, whereFldName, whereFldValue, fldsToGet, sortFlds, inforceDistinct);
        return convertArray2DtoJArrNEXT(procTblRows, fldsToGet, fldsToExclude, emptyWhenNoData);
    }

    public static JSONObject dbRowsGroupedToJsonArr(String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue, String[] sortFlds) {
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), tblName, whereFldName, whereFldValue, fldsToGet, sortFlds);
        JSONObject jBlockObj = new JSONObject();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procTblRows[0][0].toString())) {
            jBlockObj.put(NO_DATA, NO_DATA);
            return jBlockObj;
        } else {
            String curSchema = "";
            JSONArray jSchemaArr = new JSONArray();
            for (Object[] curRow : procTblRows) {
                if (Boolean.FALSE.equals(curSchema.equalsIgnoreCase(LPNulls.replaceNull(curRow[0]).toString()))) {
                    if (Boolean.FALSE.equals(jSchemaArr.isEmpty())) {
                        if (curSchema.length() == 0) {
                            curSchema = "-";
                        }
                        jBlockObj.put(curSchema, jSchemaArr);
                    }
                    jSchemaArr = new JSONArray();
                    if (fldsToGet.length == 2) {
                        jSchemaArr.add(LPNulls.replaceNull(curRow[1]).toString());
                    } else {
                        JSONObject jObj = new JSONObject();
                        for (int i = 1; i < fldsToGet.length; i++) {
                            jObj.put(fldsToGet[i], curRow[i]);
                        }
                        jSchemaArr.add(jObj);
                    }
                    curSchema = curRow[0].toString();
                } else {
                    if (fldsToGet.length == 2) {
                        jSchemaArr.add(LPNulls.replaceNull(curRow[1]).toString());
                    } else {
                        JSONObject jObj = new JSONObject();
                        for (int i = 1; i < fldsToGet.length; i++) {
                            jObj.put(fldsToGet[i], curRow[i]);
                        }
                        jSchemaArr.add(jObj);
                    }
                }
            }
            if (Boolean.FALSE.equals(jSchemaArr.isEmpty())) {
                if (curSchema.length() == 0) {
                    curSchema = "-";
                }
                jBlockObj.put(curSchema, jSchemaArr);
            }
        }
        return jBlockObj;
    }
    /*
    public static JSONObject riskAssessmentBlockInRequirements(String procInstanceName) {
    String[] fldsArr = new String[]{TblsReqs.ProcedureUsers.USER_NAME.getName()};
    Object[][] procUsers = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_USERS.getTableName(),
    new String[]{TblsReqs.ProcedureUsers.PROC_INSTANCE_NAME.getName()},
    new Object[]{procInstanceName}, fldsArr,
    new String[]{TblsReqs.ProcedureUserRoles.USER_NAME.getName()});
    JSONObject jBlockObj = new JSONObject();
    JSONArray jBlockArr = new JSONArray();
    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procUsers[0][0].toString()))) {
    for (Object[] curRow : procUsers) {
    jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsArr, curRow));
    }
    }
    jBlockObj.put("users", jBlockArr);
    return jBlockObj;
    }*/

    private QueryUtilities() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String[] getFieldsListToRetrieve(String fldToRetrieve, String[] tableAllFields) {
        String[] fieldsToRetrieve = tableAllFields;
        if (Boolean.FALSE.equals(fldToRetrieve == null || fldToRetrieve.length() == 0 || "ALL".equalsIgnoreCase(fldToRetrieve))) {
            fieldsToRetrieve = fldToRetrieve.split("\\|");
        }
        return fieldsToRetrieve;
    }

    public static Object[][] getTableData(String schema, String tableName, String fldToRetrieve, String[] tableAllFields, String[] whereFldName, Object[] whereFldValue, String[] orderBy) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        return getTableData(procReqSession, schema, tableName, fldToRetrieve, tableAllFields, whereFldName, whereFldValue, orderBy);
    }

    public static Object[][] getTableData(ProcedureRequestSession procReqSession, String schema, String tableName, String fldToRetrieve, String[] tableAllFields, String[] whereFldName, Object[] whereFldValue, String[] orderBy) {
        String[] fieldsToRetrieve = getFieldsListToRetrieve(fldToRetrieve, tableAllFields);
        return Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), schema),
                tableName, whereFldName, whereFldValue, fieldsToRetrieve, orderBy);
    }

    public static JSONObject getKPIInfoFromRequest(HttpServletRequest request, String extraGrouperFieldName, String extraGrouperFieldValues) {
        String[] programKPIGroupNameArr = new String[0];
        String programKPIGroupName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_OBJ_GROUP_NAME);
        if (programKPIGroupName != null) {
            programKPIGroupNameArr = programKPIGroupName.split("\\/");
        }
        String[] programKPITableCategoryArr = new String[0];
        String programKPITableCategory = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TABLE_CATEGORY);
        if (programKPITableCategory != null) {
            programKPITableCategoryArr = programKPITableCategory.split("\\/");
        }
        String[] programKPITableNameArr = new String[0];
        String programKPITableName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TABLE_NAME);
        if (programKPITableName != null) {
            programKPITableNameArr = programKPITableName.split("\\/");
        }
        String programKPIWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME);
        String programKPIWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE);
        String[] programKPIRetrieveOrGroupingArr = new String[0];
        String programKPIRetrieveOrGrouping = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FIELDS_TO_RETRIEVE_OR_GROUPING);
        if (programKPIRetrieveOrGrouping != null) {
            programKPIRetrieveOrGroupingArr = programKPIRetrieveOrGrouping.split("\\/");
        }
        String[] programKPIGroupedArr = new String[0];
        String programKPIGrouped = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_GROUPED);
        if (programKPIGrouped != null) {
            programKPIGroupedArr = programKPIGrouped.split("\\/");
        }
        JSONObject programkpIsObj = new JSONObject();
        if (programKPIWhereFieldsName != null && programKPIWhereFieldsValue != null) {
            String[] curProgramKPIWhereFieldsNameArr = programKPIWhereFieldsName.split("\\/");
            String[] curProgramKPIWhereFieldsValueArr = programKPIWhereFieldsValue.split("\\/");
            for (int i = 0; i < curProgramKPIWhereFieldsNameArr.length; i++) {
                curProgramKPIWhereFieldsNameArr[i] = curProgramKPIWhereFieldsNameArr[i] + "|" + extraGrouperFieldName;
                curProgramKPIWhereFieldsValueArr[i] = curProgramKPIWhereFieldsValueArr[i] + "|" + extraGrouperFieldValues;
            }
            programkpIsObj = LPKPIs.getKPIs(programKPIGroupNameArr, programKPITableCategoryArr, programKPITableNameArr,
                    curProgramKPIWhereFieldsNameArr, curProgramKPIWhereFieldsValueArr, programKPIRetrieveOrGroupingArr, programKPIGroupedArr, false);
        }
        return programkpIsObj;
    }
    public static JSONArray getNdaysArray(EnumIntTables tblObj, String numDays, EnumIntTableFields fldForNDaysfilter, String[] extraWhereFlds, Object[] extraWhereVls, String[] sortFlds) {
        return getNdaysArray(tblObj, numDays, fldForNDaysfilter, extraWhereFlds, extraWhereVls, sortFlds, null);
    }
    
    public static JSONArray getNdaysArray(EnumIntTables tblObj, String numDays, EnumIntTableFields fldForNDaysfilter, String[] extraWhereFlds, Object[] extraWhereVls, String[] sortFlds, String alternativeSchema) {
        if (numDays == null) {
            return new JSONArray();
        }
        int numDaysInt = 0 - Integer.valueOf(numDays);
        String[] whereFlds = new String[]{fldForNDaysfilter.getName() + SqlStatement.WHERECLAUSE_TYPES.GREATER_THAN.getSqlClause()};
        Object[] whereVls = new Object[]{LPDate.addDays(LPDate.getCurrentDateWithNoTime(), numDaysInt)};
        if (extraWhereFlds != null && extraWhereVls != null) {
            whereFlds = LPArray.addValueToArray1D(whereFlds, extraWhereFlds);
            whereVls = LPArray.addValueToArray1D(whereVls, extraWhereVls);
        }
        EnumIntTableFields[] allFieldNamesFromDatabase = EnumIntTableFields.getAllFieldNamesFromDatabase(tblObj, alternativeSchema);
        Object[][] prodLotsDeactivatedLastDays = QueryUtilitiesEnums.getTableData(tblObj,
                allFieldNamesFromDatabase,
                whereFlds, whereVls, sortFlds);
        JSONArray jArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(prodLotsDeactivatedLastDays[0][0].toString()))) {
            for (Object[] currIncident : prodLotsDeactivatedLastDays) {
                JSONObject jObj = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(allFieldNamesFromDatabase), currIncident);
                jArr.add(jObj);
            }
        }
        return jArr;
    }

    private static String SampleStructureByQuery(String procInstanceName, Integer sampleId, String sampleFieldToRetrieve, String sampleAnalysisFieldToRetrieve, String sampleAnalysisFieldToSort,
            String sarFieldToRetrieve, String sarFieldToSort, String sampleAuditFieldToRetrieve, String sampleAuditResultFieldToSort) {
        String schemaData = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        schemaData = Rdbms.addSuffixIfItIsForTesting(procInstanceName, schemaData, TblsData.TablesData.SAMPLE.getTableName());
        if (Boolean.FALSE.equals(schemaData.startsWith("\"", 0))){
            schemaData="\""+schemaData+"\"";
        }
        String schemaDataAudit = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName());
        if (Boolean.FALSE.equals(schemaDataAudit.startsWith("\"", 0))){
            schemaDataAudit="\""+schemaDataAudit+"\"";
        }
        schemaDataAudit = Rdbms.addSuffixIfItIsForTesting(procInstanceName, schemaDataAudit, TblsDataAudit.TablesDataAudit.SAMPLE.getTableName());
        String[] sampleFieldToRetrieveArr = new String[0];
        if (SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS.equalsIgnoreCase(sampleFieldToRetrieve)) {
            sampleFieldToRetrieve = "*";
        } else {
            if (sampleFieldToRetrieve != null) {
                sampleFieldToRetrieveArr = sampleFieldToRetrieve.split("\\|");
            } else {
                sampleFieldToRetrieveArr = new String[0];
            }
            sampleFieldToRetrieveArr = LPArray.addValueToArray1D(sampleFieldToRetrieveArr, new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.STATUS.getName()});
            sampleFieldToRetrieve = LPArray.convertArrayToString(sampleFieldToRetrieveArr, ", ", "");
        }
        String[] sampleAnalysisFieldToRetrieveArr = new String[0];
        if (SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS.equalsIgnoreCase(sampleAnalysisFieldToRetrieve)) {
            sampleAnalysisFieldToRetrieve = "*";
        } /*                {                
                for (TblsData.SampleAnalysis obj: TblsData.SampleAnalysis.values()){
                    if (Boolean.FALSE.equals("TBL".equalsIgnoreCase(obj.name())))
                    sampleAnalysisFieldToRetrieveArr=LPArray.addValueToArray1D(sampleAnalysisFieldToRetrieveArr, obj.getName());
                }               } */ else {
            if (sampleAnalysisFieldToRetrieve != null) {
                sampleAnalysisFieldToRetrieveArr = sampleAnalysisFieldToRetrieve.split("\\|");
            } else {
                sampleAnalysisFieldToRetrieveArr = new String[0];
            }
            sampleAnalysisFieldToRetrieveArr = LPArray.addValueToArray1D(sampleAnalysisFieldToRetrieveArr, new String[]{TblsData.SampleAnalysis.TEST_ID.getName(), TblsData.SampleAnalysis.STATUS.getName()});
            sampleAnalysisFieldToRetrieve = LPArray.convertArrayToString(sampleAnalysisFieldToRetrieveArr, ", ", "");
        }
        if (sampleAnalysisFieldToSort == null) {
            sampleAnalysisFieldToSort = TblsData.SampleAnalysis.TEST_ID.getName();
        }
        String[] sarFieldToRetrieveArr = new String[0];
        if (SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS.equalsIgnoreCase(sarFieldToRetrieve)) {
            sarFieldToRetrieve = "*";
        } /*{                
                for (TblsData.SampleAnalysisResult obj: TblsData.SampleAnalysisResult.values()){
                    if (Boolean.FALSE.equals("TBL".equalsIgnoreCase(obj.name())))
                    sarFieldToRetrieveArr=LPArray.addValueToArray1D(sarFieldToRetrieveArr, obj.getName());
                }                
            }*/ else {
            if (sarFieldToRetrieve != null) {
                sarFieldToRetrieveArr = sarFieldToRetrieve.split("\\|");
            } else {
                sarFieldToRetrieveArr = new String[0];
            }
            sarFieldToRetrieveArr = LPArray.addValueToArray1D(sarFieldToRetrieveArr, new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName(), TblsData.SampleAnalysisResult.STATUS.getName()});
            sarFieldToRetrieve = LPArray.convertArrayToString(sarFieldToRetrieveArr, ", ", "");
        }
        if (sarFieldToSort == null) {
            sarFieldToSort = TblsData.SampleAnalysisResult.RESULT_ID.getName();
        }
        String[] sampleAuditFieldToRetrieveArr = new String[0];
        if (sampleAuditFieldToRetrieve != null && SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS.equalsIgnoreCase(sampleAuditFieldToRetrieve)) {
            sampleAuditFieldToRetrieve = "*";
        } /*{                
                for (TblsDataAudit.Sample obj: TblsDataAudit.Sample.values()){
                    if (Boolean.FALSE.equals("TBL".equalsIgnoreCase(obj.name())))
                    sampleAuditFieldToRetrieveArr=LPArray.addValueToArray1D(sampleAuditFieldToRetrieveArr, obj.getName());
                }                
            }*/ else {
            if (sampleAuditFieldToRetrieve != null) {
                sampleAuditFieldToRetrieveArr = sampleAuditFieldToRetrieve.split("\\|");
                sampleAuditFieldToRetrieveArr = LPArray.addValueToArray1D(sampleAuditFieldToRetrieveArr,
                        new String[]{TblsDataAudit.Sample.AUDIT_ID.getName(), TblsDataAudit.Sample.TRANSACTION_ID.getName(),
                            TblsDataAudit.Sample.ACTION_NAME.getName(), TblsDataAudit.Sample.PERSON.getName(), TblsDataAudit.Sample.USER_ROLE.getName()});
                sampleAuditFieldToRetrieve = LPArray.convertArrayToString(sampleAuditFieldToRetrieveArr, ", ", "");
            }
        }
        if (sampleAuditResultFieldToSort == null) {
            sampleAuditResultFieldToSort = TblsDataAudit.Sample.AUDIT_ID.getName();
        }
        try {
            String sqlSelect = " select ";
            String sqlFrom = " from ";
            String sqlOrderBy = " order by ";
            String qry = "";
            qry = qry + "select row_to_json(sQry)from "
                    + " ( " + sqlSelect + " " + sampleFieldToRetrieve + ", "
                    + " ( " + sqlSelect + " COALESCE(array_to_json(array_agg(row_to_json(saQry))),'[]') from  "
                    + "( " + sqlSelect + " " + sampleAnalysisFieldToRetrieve + ", "
                    + "( " + sqlSelect + " COALESCE(array_to_json(array_agg(row_to_json(sarQry))),'[]') from "
                    + "( " + sqlSelect + " " + sarFieldToRetrieve + " from " + schemaData + ".sample_analysis_result_with_spec_limits sar where sar.test_id=sa.test_id "
                    + sqlOrderBy + sarFieldToSort + "     ) sarQry    ) as sample_analysis_result "
                    + sqlFrom + schemaData + ".sample_analysis sa where sa.sample_id=s.sample_id "
                    + sqlOrderBy + sampleAnalysisFieldToSort + "      ) saQry    ) as sample_analysis "
                    + "<audit>"
                    + sqlFrom + schemaData + ".sample s where s.sample_id in (" + "?" + " ) ) sQry   ";
            if (sampleAuditFieldToRetrieve == null) {
                qry = qry.replace("<audit>", "");
            } else {
                qry = qry.replace("<audit>",
                        ", ( " + sqlSelect + " COALESCE(array_to_json(array_agg(row_to_json(sauditQry))),'[]') from  "
                        + "( " + sqlSelect + " " + sampleAuditFieldToRetrieve
                        + sqlFrom + schemaDataAudit + ".sample saudit where saudit.sample_id=s.sample_id "
                        + sqlOrderBy + sampleAuditResultFieldToSort + "      ) sauditQry    ) as sample_audit ");
            }

            CachedRowSet prepRdQuery = Rdbms.prepRdQuery(qry, new Object[]{sampleId});
            prepRdQuery.last();
            if (prepRdQuery.getRow() > 0) {
                return prepRdQuery.getString(1);
            } else {
                ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{"sample", "", procInstanceName});
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataSample.class.getName()).log(Level.SEVERE, null, ex);
            return LPPlatform.LAB_FALSE;
        }
    }
    
}
