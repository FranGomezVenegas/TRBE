/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.definition.logic;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import static trazit.procedureinstance.definition.logic.ClassReqProcedUserAndActionsForQueries.actionsByRoles;
import static trazit.procedureinstance.definition.logic.ClassReqProcedUserAndActionsForQueries.viewsByRoles;
import databases.Rdbms;
import databases.SqlWhere;
import trazit.procedureinstance.definition.definition.TblsReqs;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.globalvariables.GlobalVariables;
import static trazit.procedureinstance.definition.logic.ClassReqProcedUserAndActionsForQueries.usersByRoles;

/**
 *
 * @author User
 */
public class ClassReqProcedureQueries {

    private ClassReqProcedureQueries() {
        throw new IllegalStateException("Utility class");
    }

    static final String NO_DATA = "No Data";

    public static Object[][] procAccessBlockInRequirements(String procInstanceName) {
        Integer iObjsInArray=0;
        String[] fldsArr = new String[]{TblsReqs.ProcedureUsers.USER_NAME.getName()};
        Object[][] procUsers = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_USERS.getTableName(),
                new String[]{TblsReqs.ProcedureUsers.PROC_INSTANCE_NAME.getName()},
                new Object[]{procInstanceName}, fldsArr,
                new String[]{TblsReqs.ProcedureUserRoles.USER_NAME.getName()});
        Object[] allContentArr1D = new Object[]{};
        
        JSONArray jBlockArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procUsers[0][0].toString()))) {
            for (Object[] curRow : procUsers) {
                jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsArr, curRow));
            }
        }
        allContentArr1D=LPArray.addValueToArray1D(allContentArr1D, TblsReqs.TablesReqs.PROC_USERS.getTableName());
        allContentArr1D=LPArray.addValueToArray1D(allContentArr1D, jBlockArr);
        //jBlockArr.put("users", jBlockArr);

        fldsArr = new String[]{TblsReqs.ProcedureRoles.ROLE_NAME.getName()};
        Object[][] procRoles = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_ROLES.getTableName(),
                new String[]{TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName()},
                new Object[]{procInstanceName}, fldsArr,
                new String[]{TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()});

        jBlockArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procRoles[0][0].toString()))) {
            for (Object[] curRow : procRoles) {
                jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsArr, curRow));
            }
        }
        allContentArr1D=LPArray.addValueToArray1D(allContentArr1D, TblsReqs.TablesReqs.PROCEDURE_ROLES.getTableName());
        allContentArr1D=LPArray.addValueToArray1D(allContentArr1D, jBlockArr);
//        jBlockArr.put("roles", jBlockArr);

        fldsArr = new String[]{TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()};
        Object[][] procUserRoles = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_USER_ROLES.getTableName(),
                new String[]{TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName()},
                new Object[]{procInstanceName}, fldsArr,
                new String[]{TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()});
        jBlockArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserRoles[0][0].toString()))) {
            for (Object[] curRow : procUserRoles) {
                jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsArr, curRow));
            }
        }
        allContentArr1D=LPArray.addValueToArray1D(allContentArr1D, TblsReqs.TablesReqs.PROC_USER_ROLES.getTableName());
        allContentArr1D=LPArray.addValueToArray1D(allContentArr1D, jBlockArr);
//        jBlockArr.put("user_role", jBlockArr);

        allContentArr1D=LPArray.addValueToArray1D(allContentArr1D, "access_roles_actions");
        allContentArr1D=LPArray.addValueToArray1D(allContentArr1D, actionsByRoles(procInstanceName, procRoles));
//        jBlockArr.put("roles_actions", actionsByRoles(procInstanceName, procRoles));
        allContentArr1D=LPArray.addValueToArray1D(allContentArr1D, "access_users_per_roles");
        allContentArr1D=LPArray.addValueToArray1D(allContentArr1D, usersByRoles(procInstanceName, procRoles));
//        jBlockArr.put("users_per_roles", usersByRoles(procInstanceName, procRoles));
        
        return LPArray.array1dTo2d(allContentArr1D, 2);
        //return jBlockArr;
    }

    public static org.json.JSONArray procViewsBlockInRequirements(String procInstanceName) {
        JSONObject jBlockObj = new JSONObject();
        String[] fldsArr = new String[]{TblsReqs.ProcedureRoles.ROLE_NAME.getName()};
        Object[][] procRoles = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_ROLES.getTableName(),
                new String[]{TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName()},
                new Object[]{procInstanceName}, fldsArr,
                new String[]{TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()});
        if (Boolean.TRUE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procRoles[0][0].toString()))) {
            return new org.json.JSONArray();
            //jBlockObj.put("roles_views", jBlockArr);
        } else {
            return viewsByRoles(procInstanceName, procRoles);
            //jBlockObj.put("roles_views", viewsByRoles(procInstanceName, procRoles));
        }
//        return jBlockArr;
    }

    public static JSONArray feProcModel(String procInstanceName) {
        JSONArray jArr = new JSONArray();
        Object[][] ruleValue = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_FE_MODEL.getTableName(),
                new String[]{TblsReqs.ProcedureFEModel.PROC_INSTANCE_NAME.getName()},
                new Object[]{procInstanceName},
                new String[]{TblsReqs.ProcedureFEModel.MODEL_JSON.getName(), TblsReqs.ProcedureFEModel.MODEL_JSON_MOBILE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ruleValue[0][0].toString())) {
            JSONObject jObj = new JSONObject();
            jObj.put(NO_DATA, NO_DATA);
            jArr.add(jObj);
        } else {
            JSONObject jObj = new JSONObject();
            jObj.put("laptop_mode", JsonParser.parseString(ruleValue[0][0].toString()).getAsJsonObject());
            jArr.add(jObj);
            jObj = new JSONObject();
            if (ruleValue[0][1] == null || ruleValue[0][1].toString().length() == 0) {
                jObj.put("mobile_mode", "no mobile version");
            } else {
                jObj.put("mobile_mode", JsonParser.parseString(ruleValue[0][1].toString()).getAsJsonObject());
            }
            jArr.add(jObj);

        }
        return jArr;
    }

    public static JSONObject dbSingleRowToJsonObj(String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue) {
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), tblName,
                whereFldName, whereFldValue, fldsToGet);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procTblRows[0][0].toString())) {
            JSONObject jObj = new JSONObject();
            jObj.put(NO_DATA, NO_DATA);
            return jObj;
        } else {
            return LPJson.convertArrayRowToJSONObject(fldsToGet, procTblRows[0]);
        }
    }

    public static JSONArray dbSingleRowToJsonFldNameAndValueArr(String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue) {
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), tblName,
                whereFldName, whereFldValue, fldsToGet);
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
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaName, tblObj,
                wObj, fldsToGet, sortFlds, false);
        return convertArray2DtoJArr(procTblRows, EnumIntTableFields.getAllFieldNames(fldsToGet), fldsToExclude, emptyWhenNoData);
    }
    public static JSONArray dbRowsToJsonArr(String procInstanceName, String schemaName, String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue, String[] sortFlds, String[] fldsToExclude, Boolean emptyWhenNoData, Boolean inforceDistinct) {
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaName, tblName,
                whereFldName, whereFldValue, fldsToGet, sortFlds, inforceDistinct);
        return convertArray2DtoJArr(procTblRows, fldsToGet, fldsToExclude, emptyWhenNoData);
    }
    
    private static JSONArray convertArray2DtoJArr(Object[][] procTblRows, String[] fldsToGet, String[] jsonFlds, Boolean emptyWhenNoData){
        JSONArray jBlockArr = new JSONArray();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procTblRows[0][0].toString())) {
            if (Boolean.TRUE.equals(emptyWhenNoData))
                return jBlockArr;
            JSONObject jObj = new JSONObject();
            jObj.put(NO_DATA, NO_DATA);
            jBlockArr.add(jObj);
        } else {
            try {
                for (Object[] curRow : procTblRows) {
                    if (jsonFlds == null) {
                        jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsToGet, curRow));
                    } else {
                        JSONObject jObj = (LPJson.convertArrayRowToJSONObject(fldsToGet, curRow, jsonFlds));
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

    public static JSONObject dbRowsGroupedToJsonArr(String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue, String[] sortFlds) {
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), tblName,
                whereFldName, whereFldValue, fldsToGet, sortFlds);
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
    
}
