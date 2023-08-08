/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.queries;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import databases.SqlStatement;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPKPIs;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public final class QueryUtilities {

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
        return Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), schema),
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
