/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.procedureinstance.definition.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlWhere;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.procedureinstance.definition.definition.TblsReqs;
import static trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceUtility.tableWithButtonsSolutionInfo;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public class ViewDesignerTableWithButtons {
 
    public static JSONArray getGridColumnInfo(String procedureName, Integer procedureVersion, String procInstanceName, Integer reqId, Integer solId, Integer tableId){
        
        JSONArray jMainArr=new JSONArray();
        Object[] tableWithButtonsSolutionInfo = tableWithButtonsSolutionInfo(procedureName, solId, tableId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(tableWithButtonsSolutionInfo[0].toString()))
            return jMainArr; //new InternalMessage(tableWithButtonsSolutionInfo[0].toString(), RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{});
        
        Integer endpointArgsFldPosic=LPArray.valuePosicInArray((String[]) tableWithButtonsSolutionInfo[2], TblsReqs.ProcedureReqSolutionViewTableWithButtons.GRID_COLUMNS_DESIGNER.getName());
        
        Object[] tableWithButtonsSolutionInfoRow=(Object[]) tableWithButtonsSolutionInfo[1];
        
        String endArgsInfo=LPNulls.replaceNull(tableWithButtonsSolutionInfoRow[endpointArgsFldPosic]).toString();
        
        if (endArgsInfo.length()==0){            
        }else{
            jMainArr=LPJson.convertArrayJsonToJSON(LPJson.convertToJsonArrayStringedObject(endArgsInfo));
        }
        return jMainArr;
    }


    public static InternalMessage addGridColumn(String procedureName, Integer procedureVersion, String procInstanceName, Integer reqId, Integer solId, Integer tableId, String argName, String argValue, String[] fieldNames, Object[] fieldValues){
        
        JSONArray jMainArr=getGridColumnInfo(procedureName, procedureVersion, procInstanceName, reqId, solId, tableId);

        JSONObject jObj=new JSONObject();
        jObj.put("argumentName", argName);
        jObj.put("fixValue", argValue);
        jMainArr.add(jObj);
        String[] updFieldNames=new String[]{TblsReqs.ProcedureReqSolutionViewTableWithButtons.GRID_COLUMNS_DESIGNER.getName()};
        Object[] updFieldValues=new Object[]{jMainArr.toJSONString()};
        RdbmsObject removeDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsReqs.TablesReqs.PROCEDURE_REQ_SOL_VIEW_TBL_BUTTONS,
                EnumIntTableFields.getTableFieldsFromString(TblsReqs.TablesReqs.PROCEDURE_REQ_SOL_VIEW_TBL_BUTTONS, updFieldNames), updFieldValues,
                new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_REQ_SOL_VIEW_TBL_BUTTONS,new String[]{TblsReqs.ProcedureReqSolutionViewTableWithButtons.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.PROCEDURE_VERSION.getName(),
                    TblsReqs.ProcedureReqSolutionViewTableWithButtons.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.REQ_ID.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.SOLUTION_ID.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.TABLE_ID.getName()},
                        new Object[]{procedureName, procedureVersion, procInstanceName, Integer.valueOf(reqId), Integer.valueOf(solId), Integer.valueOf(tableId)}), null);                    
        return new InternalMessage(removeDiagn.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, 
                removeDiagn.getErrorMessageCode(), 
                new Object[]{solId});
    }
    
    public static InternalMessage updateGridColumn(String procedureName, Integer procedureVersion, String procInstanceName, Integer reqId, Integer solId, Integer tableId, String argName, String argValue, String[] fieldNames, Object[] fieldValues){
        
        JSONArray jMainArr=getGridColumnInfo(procedureName, procedureVersion, procInstanceName, reqId, solId, tableId);
        jMainArr = LPJson.removeEntry(jMainArr, "argumentName", argName);

        JSONObject jObj=new JSONObject();
        jObj.put("argumentName", argName);
        jObj.put("fixValue", argValue);
        jMainArr.add(jObj);
        
        String[] updFieldNames=new String[]{TblsReqs.ProcedureReqSolutionViewTableWithButtons.GRID_COLUMNS_DESIGNER.getName()};
        Object[] updFieldValues=new Object[]{jMainArr.toJSONString()};
        RdbmsObject removeDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsReqs.TablesReqs.PROCEDURE_REQ_SOL_VIEW_TBL_BUTTONS,
                EnumIntTableFields.getTableFieldsFromString(TblsReqs.TablesReqs.PROCEDURE_REQ_SOL_VIEW_TBL_BUTTONS, updFieldNames), updFieldValues,
                new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_REQ_SOL_VIEW_TBL_BUTTONS,new String[]{TblsReqs.ProcedureReqSolutionViewTableWithButtons.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.PROCEDURE_VERSION.getName(),
                    TblsReqs.ProcedureReqSolutionViewTableWithButtons.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.REQ_ID.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.SOLUTION_ID.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.TABLE_ID.getName()},
                        new Object[]{procedureName, procedureVersion, procInstanceName, Integer.valueOf(reqId), Integer.valueOf(solId), Integer.valueOf(tableId)}), null);                    
        return new InternalMessage(removeDiagn.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, 
                removeDiagn.getErrorMessageCode(), 
                new Object[]{solId});
    }

    public static InternalMessage removeGridColumn(String procedureName, Integer procedureVersion, String procInstanceName, Integer reqId, Integer solId, Integer tableId, String argName, String[] fieldNames, Object[] fieldValues){
        
        JSONArray jMainArr=getGridColumnInfo(procedureName, procedureVersion, procInstanceName, reqId, solId, tableId);
        jMainArr = LPJson.removeEntry(jMainArr, "argumentName", argName);
        String[] updFieldNames=new String[]{TblsReqs.ProcedureReqSolutionViewTableWithButtons.GRID_COLUMNS_DESIGNER.getName()};
        Object[] updFieldValues=new Object[]{jMainArr.toJSONString()};
        RdbmsObject removeDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsReqs.TablesReqs.PROCEDURE_REQ_SOL_VIEW_TBL_BUTTONS,
                EnumIntTableFields.getTableFieldsFromString(TblsReqs.TablesReqs.PROCEDURE_REQ_SOL_VIEW_TBL_BUTTONS, updFieldNames), updFieldValues,
                new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_REQ_SOL_VIEW_TBL_BUTTONS,new String[]{TblsReqs.ProcedureReqSolutionViewTableWithButtons.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.PROCEDURE_VERSION.getName(),
                    TblsReqs.ProcedureReqSolutionViewTableWithButtons.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.REQ_ID.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.SOLUTION_ID.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.TABLE_ID.getName()},
                        new Object[]{procedureName, procedureVersion, procInstanceName, Integer.valueOf(reqId), Integer.valueOf(solId), Integer.valueOf(tableId)}), null);                    
        return new InternalMessage(removeDiagn.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, 
                removeDiagn.getErrorMessageCode(), 
                new Object[]{solId});
    }
  
}
