/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.definition.logic;

import com.google.gson.JsonParser;
import static trazit.procedureinstance.definition.logic.ClassReqProcedUserAndActionsForQueries.actionsByRoles;
import static trazit.procedureinstance.definition.logic.ClassReqProcedUserAndActionsForQueries.viewsByRoles;
import databases.Rdbms;
import trazit.procedureinstance.definition.definition.TblsReqs;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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

    public static final String NO_DATA = "No Data";

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

    
}
