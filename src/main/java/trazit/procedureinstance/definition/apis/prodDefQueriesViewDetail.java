package trazit.procedureinstance.definition.apis;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import java.util.Arrays;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntViewFields;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.definition.definition.TblsReqs;
import trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections.ReqSolutionTypes;
import trazit.queries.QueryUtilities;
import com.google.gson.JsonObject;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import static trazit.procedureinstance.deployment.logic.ProcDefToInstanceCreateProcViewsDeployed.viewTableWithButtonsDeployed;
import static trazit.procedureinstance.deployment.logic.ProcDefToInstanceCreateProcViewsDeployed.viewTabsDeployed;

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
            TblsReqs.viewProcReqSolutionViews.ORDER_NUMBER, TblsReqs.viewProcReqSolutionViews.SOLUTION_ID, TblsReqs.viewProcReqSolutionViews.TWOICONS_DETAIL, TblsReqs.viewProcReqSolutionViews.SOP_NAME, TblsReqs.viewProcReqSolutionViews.REQ_ID};
        Object[][] procViewsArr = Rdbms.getRecordFieldsByFilterForViews(null, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_WINDOWS, new SqlWhere(TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_WINDOWS, new String[]{TblsReqs.viewProcReqSolutionViews.PROCEDURE_NAME.getName(), TblsReqs.viewProcReqSolutionViews.PROCEDURE_VERSION.getName(), TblsReqs.viewProcReqSolutionViews.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionViews.ACTIVE.getName()}, 
                new Object[]{procedure, procVersion, procInstanceName, true}), 
            vwProcReqSolFlds, null, false);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procViewsArr[0][0].toString())) {
            JSONObject curViewLog = new JSONObject();
            curViewLog.put("error", "cannot get the window data");
            curViewLog.put("error_detail", Arrays.toString(procViewsArr[0]));
            mainLog.put("windows_error", curViewLog);
            return mainLogArr;            
        } else {
            for (Object[] curView : procViewsArr) {
                JSONObject viewRowAndContentInfoToJsonObject = viewRowAndContentInfoToJsonObject(procedure, procVersion, procInstanceName, vwProcReqSolFlds, curView);
                String curContentType=viewRowAndContentInfoToJsonObject.get(TblsReqs.viewProcReqSolutionViews.CONTENT_TYPE.getName()).toString();                
                String curSolutionId=viewRowAndContentInfoToJsonObject.get(TblsReqs.viewProcReqSolutionViews.SOLUTION_ID.getName()).toString();
                
                switch (curContentType) {
                    case "TABLE_WITH_BUTTONS":
                        EnumIntTableFields[] vwProcReqSolTableFlds = new EnumIntTableFields[]
                            {TblsReqs.ProcedureReqSolutionViewTableWithButtons.TABLE_ID};
                        Object[][] procTblArr = Rdbms.getRecordFieldsByFilter(null, 
                                GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOL_VIEW_TBL_BUTTONS, new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_REQ_SOL_VIEW_TBL_BUTTONS, new String[]{TblsReqs.ProcedureReqSolutionViewTableWithButtons.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolutionViewTableWithButtons.SOLUTION_ID.getName()}, 
                                new Object[]{procedure, procVersion, procInstanceName, Integer.valueOf(curSolutionId)}), 
                            vwProcReqSolTableFlds, null, false);
                        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procTblArr[0][0].toString()))) {
                            viewRowAndContentInfoToJsonObject.put(TblsReqs.ProcedureReqSolutionViewTableWithButtons.TABLE_ID.getName(), Integer.valueOf(procTblArr[0][0].toString()));
                        }
                        
                        break;
                    default:
                        break;
                }
                
                
                mainLogArr.add(viewRowAndContentInfoToJsonObject);
            }
            //return mainLogArr;
        }    
        vwProcReqSolFlds = new EnumIntViewFields[]{TblsReqs.viewProcReqSolutionSpecialViews.WINDOW_NAME, TblsReqs.viewProcReqSolutionSpecialViews.ROLES, TblsReqs.viewProcReqSolutionViews.PARENT_CODE, TblsReqs.viewProcReqSolutionViews.CODE, 
            TblsReqs.viewProcReqSolutionSpecialViews.WINDOW_QUERY, TblsReqs.viewProcReqSolutionSpecialViews.CONTENT_TYPE, TblsReqs.viewProcReqSolutionSpecialViews.WINDOW_MODE, 
            TblsReqs.viewProcReqSolutionSpecialViews.WINDOW_TYPE, TblsReqs.viewProcReqSolutionSpecialViews.WINDOW_LABEL_EN, TblsReqs.viewProcReqSolutionSpecialViews.WINDOW_LABEL_ES, 
            TblsReqs.viewProcReqSolutionSpecialViews.ORDER_NUMBER, TblsReqs.viewProcReqSolutionSpecialViews.SOLUTION_ID, TblsReqs.viewProcReqSolutionSpecialViews.TWOICONS_DETAIL, TblsReqs.viewProcReqSolutionSpecialViews.SOP_NAME};
        Object[][] procSpecialViewsArr = Rdbms.getRecordFieldsByFilterForViews(null, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_SPECIAL_VIEWS, new SqlWhere(TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_SPECIAL_VIEWS, new String[]{TblsReqs.viewProcReqSolutionSpecialViews.PROCEDURE_NAME.getName(), TblsReqs.viewProcReqSolutionSpecialViews.PROCEDURE_VERSION.getName(), TblsReqs.viewProcReqSolutionSpecialViews.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionSpecialViews.ACTIVE.getName()}, 
                new Object[]{procedure, procVersion, procInstanceName, true}), 
            vwProcReqSolFlds, null, false);
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procViewsArr[0][0].toString()))) {
            for (Object[] curView : procSpecialViewsArr) {
                mainLogArr.add(viewRowAndContentInfoToJsonObject(procedure, procVersion, procInstanceName, vwProcReqSolFlds, curView));
            }
        }
        return mainLogArr;

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
    private static JSONObject viewRowAndContentInfoToJsonObject(String procedure, Integer procVersion, String procInstanceName, EnumIntViewFields[] vwProcReqSolFlds, Object[] curView){
        JSONObject curViewObj =new JSONObject();
        curViewObj = LPJson.convertArrayRowToJSONObject(EnumIntViewFields.getAllFieldNames(vwProcReqSolFlds), curView);

        String currentParentCode = LPNulls.replaceNull(curView[2]).toString();
        String currentCode = LPNulls.replaceNull(curView[3]).toString();
        String windowType = LPNulls.replaceNull(curView[7]).toString();  
        String sopName = LPNulls.replaceNull(curView[13]).toString();
        JsonObject jObjDeployedModel= new JsonObject();
        JsonObject jObjModel= new JsonObject();
        switch (LPNulls.replaceNull(curView[5]).toString().toUpperCase()){
            case "TABS":
                jObjDeployedModel=viewTabsDeployed(Integer.valueOf(curView[11].toString()), curView, currentParentCode, currentCode, windowType, procedure, procVersion, procInstanceName);
                break;
            case "TABLE_WITH_BUTTONS":
               // jObjModel=viewTableWithButtonsDesigner(Integer.valueOf(curView[11].toString()), null, curView, currentParentCode, currentCode, windowType, procedure, procVersion, procInstanceName);
                jObjDeployedModel=viewTableWithButtonsDeployed(Integer.valueOf(curView[11].toString()), null, curView, currentParentCode, currentCode, windowType, procedure, procVersion, procInstanceName);
                break;
            default:
                break;                        
        }
        if (jObjDeployedModel.has("error")){
            curViewObj.put(curView[0], jObjDeployedModel);     
            return curViewObj;
        }  
       // curViewObj.put("design_designer", jObjModel);
        curViewObj.put("design", jObjDeployedModel);

        return curViewObj;
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
