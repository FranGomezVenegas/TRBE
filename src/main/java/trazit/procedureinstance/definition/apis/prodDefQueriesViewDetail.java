/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.procedureinstance.definition.apis;
import com.google.gson.JsonObject;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import java.util.Arrays;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntViewFields;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.definition.definition.TblsReqs;
import static trazit.procedureinstance.deployment.logic.ProcDefToInstanceCreateProcViews.viewTableWithButtons;
import static trazit.procedureinstance.deployment.logic.ProcDefToInstanceCreateProcViews.viewTabs;
import trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections;
import trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections.ReqSolutionTypes;
import trazit.queries.QueryUtilities;

/**
 *
 * @author User
 */
public class prodDefQueriesViewDetail {
    
    
public static final JSONArray getProcedureViews(String procedure, Integer procVersion, String procInstanceName) {
    String currentParentCode = "";
    String currentCode = "";
    String windowType = "";
    JSONObject mainLog = new JSONObject();    
    JSONArray mainLogArr = new JSONArray();  
    try{        
        EnumIntViewFields[] vwProcReqSolFlds = new EnumIntViewFields[]{TblsReqs.viewProcReqSolutionViews.WINDOW_NAME, TblsReqs.viewProcReqSolutionViews.ROLES, TblsReqs.viewProcReqSolutionViews.PARENT_CODE, TblsReqs.viewProcReqSolutionViews.CODE, 
            TblsReqs.viewProcReqSolutionViews.WINDOW_QUERY, TblsReqs.viewProcReqSolutionViews.CONTENT_TYPE, TblsReqs.viewProcReqSolutionViews.WINDOW_MODE, 
            TblsReqs.viewProcReqSolutionViews.WINDOW_TYPE, TblsReqs.viewProcReqSolutionViews.WINDOW_LABEL_EN, TblsReqs.viewProcReqSolutionViews.WINDOW_LABEL_ES, 
            TblsReqs.viewProcReqSolutionViews.ORDER_NUMBER, TblsReqs.viewProcReqSolutionViews.SOLUTION_ID, TblsReqs.viewProcReqSolutionViews.TWOICONS_DETAIL, TblsReqs.viewProcReqSolutionViews.SOP_NAME};
        Object[][] procViewsArr = Rdbms.getRecordFieldsByFilterForViews(null, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_WINDOWS, new SqlWhere(TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_WINDOWS, new String[]{TblsReqs.viewProcReqSolutionViews.PROCEDURE_NAME.getName(), TblsReqs.viewProcReqSolutionViews.PROCEDURE_VERSION.getName(), TblsReqs.viewProcReqSolutionViews.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionViews.ACTIVE.getName(), TblsReqs.viewProcReqSolutionViews.TYPE.getName()}, new Object[]{procedure, procVersion, procInstanceName, true, ProcedureDefinitionToInstanceSections.ReqSolutionTypes.WINDOW.getTagValue()}), 
            vwProcReqSolFlds, null, false);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procViewsArr[0][0].toString())) {
            JSONObject curViewLog = new JSONObject();
            curViewLog.put("error", "cannot get the window data");
            curViewLog.put("error_detail", Arrays.toString(procViewsArr[0]));
            mainLog.put("windows_error", curViewLog);
            return mainLogArr;            
        } else {
            
            for (Object[] curView : procViewsArr) {
                JSONObject curViewObj =new JSONObject();
                curViewObj = LPJson.convertArrayRowToJSONObject(EnumIntViewFields.getAllFieldNames(vwProcReqSolFlds), curView);
                
                currentParentCode = LPNulls.replaceNull(curView[2]).toString();
                currentCode = LPNulls.replaceNull(curView[3]).toString();
                windowType = LPNulls.replaceNull(curView[7]).toString();  
                String sopName = LPNulls.replaceNull(curView[13]).toString();
                JsonObject jObjModel= new JsonObject();
                switch (LPNulls.replaceNull(curView[5]).toString().toUpperCase()){
                    case "TABS":
                        jObjModel=viewTabs(Integer.valueOf(curView[11].toString()), curView, currentParentCode, currentCode, windowType, procedure, procVersion, procInstanceName);
                        break;
                    case "TABLE_WITH_BUTTONS":
                        jObjModel=viewTableWithButtons(Integer.valueOf(curView[11].toString()), null, curView, currentParentCode, currentCode, windowType, procedure, procVersion, procInstanceName);
                        break;
                    default:
                        break;                        
                }
                if (jObjModel.has("error")){
                    mainLog.put(curView[0], jObjModel);     
                    continue;
                }  
                curViewObj.put("design", jObjModel);
                mainLogArr.add(curViewObj);
            }
            return mainLogArr;
        }    
    } catch (Exception e) {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentParentCode", currentParentCode);
        jsonObj.put("currentCode", currentCode);
        jsonObj.put("windowType", windowType);
        jsonObj.put("error_exception", e.getMessage());
        //mainLog.put(currentParentCode, jsonObj);
        mainLogArr.add(jsonObj);
        return mainLogArr;
    }
    }    
    
    public static JSONArray xprocInstanceViewDesignInRequirements(String procInstanceName) {
        JSONArray reqSolJsonExtendedArr = new JSONArray();
        SqlWhere wObj = new SqlWhere();
        wObj.addConstraint(TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{procInstanceName}, null);        
        wObj.addConstraint(TblsReqs.ProcedureReqSolution.TYPE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{ReqSolutionTypes.WINDOW.getTagValue()}, null);        
        JSONArray reqSolJsonArr = QueryUtilities.dbRowsToJsonArr(procInstanceName, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, TblsReqs.ProcedureReqSolution.values(), 
            wObj, null, null, false);        
        for (int i=0;i<reqSolJsonArr.size();i++){
            JSONObject curView=(JSONObject) reqSolJsonArr.get(i);
            String curType=curView.get("type").toString();
           // ReqWindowTypes curTypeObj = ReqWindowTypes.getByTagValue(curType);
/*           
            switch (curTypeObj){
                
                //viewTableWithButtons(Integer solId, Integer tabId, Object[] curView, String currentParentCode, String currentCode, String windowType,
    //String procedure, Integer procVersion, String procInstanceName)
                case BUSINESS_RULE:
                    String busRule=LPNulls.replaceNull(curView.get("business_rule")).toString();
                    String busRuleValue=LPNulls.replaceNull(curView.get("business_rule_value")).toString();
                    curView.put("relevant_info_1", busRule);
                    curView.put("relevant_info_2", busRuleValue);
                    break;
                case TABS:
    //                viewTabs(Integer solId, Object[] curView, String currentParentCode, String currentCode, String windowType,
    //String procedure, Integer procVersion, String procInstanceName)
                    String wName=LPNulls.replaceNull(curView.get("window_name")).toString();
                    curView.put("relevant_info_1", wName);
                    String wQuery=LPNulls.replaceNull(curView.get("window_query")).toString();
                    curView.put("relevant_info_2", wName);
                    break;
                case WINDOW_BUTTON:
                    String wAction=LPNulls.replaceNull(curView.get("window_action")).toString();
                    curView.put("relevant_info_1", wAction);
                    curView.put("relevant_info_2", "");
                    break;
                case SPECIAL_VIEW:
                    String specialViewName=LPNulls.replaceNull(curView.get("special_view_name")).toString();
                    curView.put("relevant_info_1", specialViewName);
                    curView.put("relevant_info_2", "");
                    break;
                default:
                    break;
            }
*/
        }
        return reqSolJsonArr;
        
    }
    
}
