/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.definition.logic;

import databases.Rdbms;
import databases.SqlStatement;
import trazit.procedureinstance.definition.definition.TblsReqs;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class ClassReqProcedUserAndActionsForQueries {

    public static JSONArray usersByRoles(String procInstanceName, Object[][] procRoles) {
        String[] roleActionsFldsArr = new String[]{TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName(),
        //    TblsReqs.ProcedureUserRoles.MOD_ORDER_NUMBER.getName(), TblsReqs.ProcedureUserRoles.WINDOW_ACTION.getName()
        };
        Object[][] roleActions2d = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_USER_ROLES.getTableName(),
                new String[]{TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName()},
                new Object[]{procInstanceName}, roleActionsFldsArr,
                new String[]{TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()}, true);
        JSONArray rolesActionsOutput = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(roleActions2d[0][0].toString()))) {
            String[] procRoles1D = LPArray.getUniquesArray(LPArray.array2dTo1d(procRoles));
            JSONArray header = new JSONArray();
            JSONObject fldDef = new JSONObject();
            fldDef.put("label", "User / Roles");
            fldDef.put("is_translation", true);
            fldDef.put("name", TblsReqs.ProcedureUserRoles.USER_NAME.getName()+"_en");
            header.put(fldDef);
            fldDef = new JSONObject();
            fldDef.put("label", "Usuario / Roles");
            fldDef.put("is_translation", true);
            fldDef.put("name", TblsReqs.ProcedureUserRoles.USER_NAME.getName()+"_es");
            header.put(fldDef);
            for (String curRole : procRoles1D) {
                header.put(curRole);
            }
            rolesActionsOutput.put(header);
//            String prevExecuted = "";
            for (Object[] curActRow : roleActions2d) {
                JSONArray curActionRow = new JSONArray();
                curActionRow.put(curActRow[0]);
                curActionRow.put(curActRow[1]);
//                if (Boolean.FALSE.equals(prevExecuted.matches(curActRow[0] + ": " + curActRow[0]))) {
                    String[] allActionRoles = LPNulls.replaceNull(curActRow[2]).toString().split("\\|");
                    for (String curRole : procRoles1D) {
                        if (LPArray.valueInArray(allActionRoles, "ALL")) {
                            curActionRow.put("ALL");
                        } else if (LPArray.valueInArray(allActionRoles, curRole)) {
                            curActionRow.put("X");
                        } else {
                            curActionRow.put("");
                        }
                    }
                    rolesActionsOutput.put(curActionRow);
//                    prevExecuted = curActRow[0] + ": " + curActRow[1];
//                }
            }
        }
        return rolesActionsOutput;
    }

    public static JSONArray actionsByRoles(String procInstanceName, Object[][] procRoles) {
        String[] roleActionsFldsArr = new String[]{TblsReqs.viewProcReqSolutionActions.ENTITY.getName(), TblsReqs.viewProcReqSolutionActions.PRETTY_EN.getName(), TblsReqs.viewProcReqSolutionActions.PRETTY_ES.getName(), TblsReqs.viewProcReqSolutionActions.ROLES.getName(),
            TblsReqs.viewProcReqSolutionActions.MOD_ORDER_NUMBER.getName(), TblsReqs.viewProcReqSolutionActions.WINDOW_ACTION.getName()};
        Object[][] roleActions2d = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_ACTIONS.getViewName(),
                new String[]{TblsReqs.viewProcReqSolutionActions.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionActions.TYPE.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.LIKE.getSqlClause()},
                new Object[]{procInstanceName, "%ction%"}, roleActionsFldsArr,
                new String[]{TblsReqs.viewProcReqSolutionActions.ENTITY.getName(), TblsReqs.viewProcReqSolutionActions.MOD_ORDER_NUMBER.getName(), TblsReqs.viewProcReqSolutionActions.WINDOW_ACTION.getName()}, true);
        JSONArray rolesActionsOutput = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(roleActions2d[0][0].toString()))) {
            String[] procRoles1D = LPArray.getUniquesArray(LPArray.array2dTo1d(procRoles));
            JSONArray header = new JSONArray();
            JSONObject fldDef = new JSONObject();
            fldDef.put("label", "Entity: Action / Roles");
            fldDef.put("is_translation", true);
            fldDef.put("name", TblsReqs.viewProcReqSolutionActions.PRETTY_EN.getName());
            header.put(fldDef);
            fldDef = new JSONObject();
            fldDef.put("label", "Entidad: Acción / Roles");
            fldDef.put("is_translation", true);
            fldDef.put("name", TblsReqs.viewProcReqSolutionActions.PRETTY_ES.getName());
            header.put(fldDef);
            for (String curRole : procRoles1D) {
                header.put(curRole);
            }
            rolesActionsOutput.put(header);
            String prevExecuted = "";
            for (Object[] curActRow : roleActions2d) {
                JSONArray curActionRow = new JSONArray();
                curActionRow.put(curActRow[0] + ": " + curActRow[1]);
                curActionRow.put(curActRow[0] + ": " + curActRow[2]);
                if (Boolean.FALSE.equals(prevExecuted.matches(curActRow[0] + ": " + curActRow[1]))) {
                    String[] allActionRoles = LPNulls.replaceNull(curActRow[3]).toString().split("\\|");
                    for (String curRole : procRoles1D) {
                        if (LPArray.valueInArray(allActionRoles, "ALL")) {
                            curActionRow.put("ALL");
                        } else if (LPArray.valueInArray(allActionRoles, curRole)) {
                            curActionRow.put("X");
                        } else {
                            curActionRow.put("");
                        }
                    }
                    rolesActionsOutput.put(curActionRow);
                    prevExecuted = curActRow[0] + ": " + curActRow[1];
                }
            }
        }
        return rolesActionsOutput;
    }

    static JSONArray viewsByRoles(String procInstanceName, Object[][] procViewRoles) {
        String[] roleActionsFldsArr = new String[]{TblsReqs.ProcedureReqSolution.WINDOW_NAME.getName(), TblsReqs.ProcedureReqSolution.LABEL_EN.getName(), TblsReqs.ProcedureReqSolution.LABEL_ES.getName(), TblsReqs.ProcedureReqSolution.ROLES.getName()};
        Object[][] roleActions2d = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(),
                new String[]{TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.TYPE.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.LIKE.getSqlClause()},
                new Object[]{procInstanceName, "%indow"}, roleActionsFldsArr,
                new String[]{TblsReqs.ProcedureReqSolution.ORDER_NUMBER.getName(), TblsReqs.ProcedureReqSolution.WINDOW_NAME.getName()});
        JSONArray rolesActionsOutput = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(roleActions2d[0][0].toString()))) {
            String[] procRoles1D = LPArray.getUniquesArray(LPArray.array2dTo1d(procViewRoles));
            JSONArray header = new JSONArray();
            JSONObject fldDef = new JSONObject();
            fldDef.put("label", "Views / Roles");
            fldDef.put("is_translation", true);
            fldDef.put("name", TblsReqs.ProcedureReqSolution.LABEL_EN.getName());
            header.put(fldDef);
            fldDef = new JSONObject();
            fldDef.put("label", "Pantallas / Roles");
            fldDef.put("is_translation", true);
            fldDef.put("name", TblsReqs.ProcedureReqSolution.LABEL_ES.getName());
            header.put(fldDef);

            for (String curRole : procRoles1D) {
                header.put(curRole);
            }
            rolesActionsOutput.put(header);
            for (Object[] curActRow : roleActions2d) {
                JSONArray curActionRow = new JSONArray();
                curActionRow.put(((LPNulls.replaceNull(curActRow[1]).toString().length()) > 0) ? curActRow[1] : curActRow[0]);
                curActionRow.put(((LPNulls.replaceNull(curActRow[2]).toString().length()) > 0) ? curActRow[2] : curActRow[0]);
                String[] allActionRoles = LPNulls.replaceNull(curActRow[3]).toString().split("\\|");
                for (String curRole : procRoles1D) {
                    if (LPArray.valueInArray(allActionRoles, "ALL")){
                        curActionRow.put("ALL");
                    }else if ((LPArray.valueInArray(allActionRoles, curRole))) {
                        curActionRow.put("X");
                    } else {
                        curActionRow.put("");
                    }
                }
                rolesActionsOutput.put(curActionRow);
            }
        }
        return rolesActionsOutput;
    }

    public static JSONArray viewsBySops(String procInstanceName) {
        JSONArray viewSopsOutput = new JSONArray();

        String[] fldsArr = new String[]{TblsReqs.ProcedureSopMetaData.SOP_NAME.getName()};
        Object[][] procSops = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableName(),
                new String[]{TblsReqs.ProcedureSopMetaData.PROC_INSTANCE_NAME.getName()},
                new Object[]{procInstanceName}, fldsArr,
                new String[]{TblsReqs.ProcedureSopMetaData.SOP_NAME.getName()});
        if (Boolean.TRUE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procSops[0][0].toString()))) {
            //viewSopsOutput.put(NO_DATA, NO_DATA);
            return viewSopsOutput;
        }
        String[] viewFldsArr = new String[]{TblsReqs.viewProcReqSolutionActions.WINDOW_NAME.getName(), TblsReqs.viewProcReqSolutionActions.WINDOW_LABEL_EN.getName(), TblsReqs.viewProcReqSolutionActions.WINDOW_LABEL_ES.getName(), TblsReqs.viewProcReqSolutionActions.SOP_NAME.getName()};
        Object[][] views2d = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_ACTIONS.getViewName(),
                new String[]{TblsReqs.viewProcReqSolutionActions.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionActions.TYPE.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.LIKE.getSqlClause()},
                new Object[]{procInstanceName, "%indow"}, viewFldsArr,
                new String[]{TblsReqs.viewProcReqSolutionActions.ORDER_NUMBER.getName(), TblsReqs.viewProcReqSolutionActions.WINDOW_NAME.getName()});
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(views2d[0][0].toString()))) {
            String[] procSops1D = LPArray.getUniquesArray(LPArray.array2dTo1d(procSops));
            JSONArray header = new JSONArray();
            JSONObject fldDef = new JSONObject();
            fldDef.put("label", "Views / SOPs");
            fldDef.put("is_translation", true);
            fldDef.put("name", TblsReqs.viewProcReqSolutionActions.WINDOW_LABEL_EN.getName());
            header.put(fldDef);
            fldDef = new JSONObject();
            fldDef.put("label", "Pantallas / PNTs");
            fldDef.put("is_translation", true);
            fldDef.put("name", TblsReqs.viewProcReqSolutionActions.WINDOW_LABEL_ES.getName());
            header.put(fldDef);

            for (String curRole : procSops1D) {
                header.put(curRole);
            }
            viewSopsOutput.put(header);
            for (Object[] curActRow : views2d) {
                JSONArray curActionRow = new JSONArray();
                curActionRow.put(((LPNulls.replaceNull(curActRow[1]).toString().length()) > 0) ? curActRow[1] : curActRow[0]);
                curActionRow.put(((LPNulls.replaceNull(curActRow[2]).toString().length()) > 0) ? curActRow[2] : curActRow[0]);
                String[] allActionRoles = LPNulls.replaceNull(curActRow[3]).toString().split("\\|");
                for (String curRole : procSops1D) {
                    if (LPArray.valueInArray(allActionRoles, "ALL")) {
                        curActionRow.put("ALL");
                    } else if (LPArray.valueInArray(allActionRoles, curRole)) {
                        curActionRow.put("X");
                    } else {
                        curActionRow.put("");
                    }
                }
                viewSopsOutput.put(curActionRow);
            }
        }
        return viewSopsOutput;
    }

}