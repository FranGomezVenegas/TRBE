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
import javax.servlet.http.HttpServletRequest;
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

}
