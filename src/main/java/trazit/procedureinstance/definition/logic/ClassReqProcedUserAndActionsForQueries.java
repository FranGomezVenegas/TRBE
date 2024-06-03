/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.definition.logic;

import databases.Rdbms;
import databases.SqlStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lbplanet.utilities.LPArray;
import trazit.procedureinstance.definition.definition.TblsReqs;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections.ReqSolutionTypes;

/**
 *
 * @author User
 */
public class ClassReqProcedUserAndActionsForQueries {

    

    public static JSONArray actionsByRoles(String procInstanceName, Object[][] procRoles) {
String[] roleActionsFldsArr = new String[]{TblsReqs.ProcedureReqSolution.WINDOW_ACTION.getName(), TblsReqs.ProcedureReqSolution.WINDOW_ACTION.getName(), TblsReqs.ProcedureReqSolution.WINDOW_ACTION.getName(), TblsReqs.ProcedureReqSolution.ROLES.getName()};
        Object[][] roleActions2d = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(),
                new String[]{TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.TYPE.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause()},
                new Object[]{procInstanceName, ReqSolutionTypes.TABLE_ROW_BUTTON.getTagValue() + "|" + ReqSolutionTypes.WINDOW_BUTTON.getTagValue()}, 
                roleActionsFldsArr,
                new String[]{TblsReqs.ProcedureReqSolution.ORDER_NUMBER.getName(), TblsReqs.ProcedureReqSolution.WINDOW_ACTION.getName()});
        return LPJson.pivotTable(roleActions2d, 0, 3, procRoles, "Actions / Roles", "Acciones / Roles", "name");        
    }

    public static JSONArray viewsByRoles(String procInstanceName, Object[][] procRoles) {
        String[] roleActionsFldsArr = new String[]{TblsReqs.ProcedureReqSolution.WINDOW_NAME.getName(), TblsReqs.ProcedureReqSolution.WINDOW_NAME.getName(), TblsReqs.ProcedureReqSolution.WINDOW_NAME.getName(), TblsReqs.ProcedureReqSolution.ROLES.getName()};
        Object[][] roleActions2d = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(),
                new String[]{TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.TYPE.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.LIKE.getSqlClause()},
                new Object[]{procInstanceName, "%indow"}, roleActionsFldsArr,
                new String[]{TblsReqs.ProcedureReqSolution.ORDER_NUMBER.getName(), TblsReqs.ProcedureReqSolution.WINDOW_NAME.getName()});

        return LPJson.pivotTable(roleActions2d, 0, 3, procRoles, "Views / Roles", "Pantallas / Roles", "name");
    }

    public static JSONArray sopsByRoles(String procInstanceName, Object[][] procRoles) {
        String[] roleSopsFldsArr = new String[]{TblsReqs.ProcedureReqSolution.SOP_NAME.getName(), TblsReqs.ProcedureReqSolution.SOP_NAME.getName(), TblsReqs.ProcedureReqSolution.SOP_NAME.getName(), TblsReqs.ProcedureReqSolution.ROLES.getName(),
            TblsReqs.ProcedureReqSolution.ORDER_NUMBER.getName(), TblsReqs.ProcedureReqSolution.SOP_NAME.getName()};
        Object[][] roleActions2d = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(),
                new String[]{TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.SOP_NAME.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{procInstanceName, ""}, roleSopsFldsArr,
                new String[]{TblsReqs.ProcedureReqSolution.SOP_NAME.getName(), TblsReqs.ProcedureReqSolution.ORDER_NUMBER.getName()}, true);

        return LPJson.pivotTable(roleActions2d, 0, 3, procRoles, "SOPs / Roles", "PNTs / Roles", "name");
    }
    
    public static JSONArray viewsBySops(String procInstanceName) {
        JSONArray viewSopsOutput = new JSONArray();

        String[] fldsArr = new String[]{TblsReqs.ProcedureSopMetaData.SOP_NAME.getName()};
        Object[][] procSops = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableName(),
                new String[]{TblsReqs.ProcedureSopMetaData.PROC_INSTANCE_NAME.getName()},
                new Object[]{procInstanceName}, fldsArr,
                new String[]{TblsReqs.ProcedureSopMetaData.SOP_NAME.getName()});
        if (Boolean.TRUE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procSops[0][0].toString()))) {
            return viewSopsOutput;
        }
        String[] viewFldsArr = new String[]{TblsReqs.ProcedureReqSolution.WINDOW_NAME.getName(), TblsReqs.ProcedureReqSolution.WINDOW_NAME.getName(), TblsReqs.ProcedureReqSolution.WINDOW_NAME.getName(), TblsReqs.ProcedureReqSolution.SOP_NAME.getName()};
        Object[][] views2d = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(),
                new String[]{TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.TYPE.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.LIKE.getSqlClause()},
                new Object[]{procInstanceName, "%indow"}, viewFldsArr,
                new String[]{TblsReqs.ProcedureReqSolution.ORDER_NUMBER.getName(), TblsReqs.ProcedureReqSolution.WINDOW_NAME.getName()});
        return LPJson.pivotTable(procSops, 0, 0, views2d, "Views / SOPs", "Pantallas / PNTs", "name");
    }

    public static JSONArray usersByRoles(String procInstanceName, Object[][] procRoles) {
        Map<String, Set<String>> userRolesMap = new HashMap<>();

        String[] procRoles1D = LPArray.getUniquesArray(LPArray.array2dTo1d(procRoles));
        String[] roleActionsFldsArr = new String[]{TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName(),
        };

        Object[][] roleActions2d = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_USER_ROLES.getTableName(),
                new String[]{TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName()},
                new Object[]{procInstanceName}, roleActionsFldsArr,
                new String[]{TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()}, true);
        return LPJson.pivotTable(roleActions2d, 0, 2, procRoles, "Entity: Action / Roles", "Entidad: Acción / Roles", "name");
    }

    
    
}
