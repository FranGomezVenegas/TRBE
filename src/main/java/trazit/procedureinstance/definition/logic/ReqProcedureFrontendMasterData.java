/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.definition.logic;

import databases.Rdbms;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.enums.FrontendMasterData;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.definition.definition.TblsReqs;

/**
 *
 * @author User
 */
public class ReqProcedureFrontendMasterData implements FrontendMasterData{

    @Override
    public JSONObject getMasterDataJsonObject(String alternativeProcInstanceName) {
        JSONObject jSummaryObj=new JSONObject();        
        jSummaryObj.put(TblsReqs.TablesReqs.MODULES.getTableName(), getActiveModulesJSON(alternativeProcInstanceName, null));
        return jSummaryObj;
    }
    
    public static Object[][] getActiveModules(String procInstanceName, String[] fieldsToGet){
        if (fieldsToGet==null){
            fieldsToGet = EnumIntTableFields.getAllFieldNames(TblsReqs.TablesReqs.MODULES.getTableFields());    
        }
        Object[][] procAndInstanceArr = Rdbms.getRecordFieldsByFilter( "", 
                TblsReqs.TablesReqs.MODULES.getRepositoryName(), TblsReqs.TablesReqs.MODULES.getTableName(),
                new String[]{TblsReqs.Modules.ACTIVE.getName()}, 
                new Object[]{true},
                fieldsToGet, new String[]{TblsReqs.Modules.MODULE_NAME.getName()});
        return procAndInstanceArr;
    }
    public static JSONArray getActiveModulesJSON(String procInstanceName, String[] fieldsToGet){     
        if (fieldsToGet==null){
            fieldsToGet = EnumIntTableFields.getAllFieldNames(TblsReqs.TablesReqs.MODULES.getTableFields());  
        }
        Object[][] procAndInstanceArr=getActiveModules(procInstanceName, fieldsToGet);
        JSONArray proceduresList = new JSONArray();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procAndInstanceArr[0][0].toString())) {
            return proceduresList;
        } else {
            for (Object[] curProc : procAndInstanceArr) {
                JSONObject curProcObj = LPJson.convertArrayRowToJSONObject(fieldsToGet, curProc);

                String moduleName=curProcObj.get("module_name").toString();
                Integer moduleVersion=curProcObj.get("module_version").toString().length()>0?Integer.valueOf(curProcObj.get("module_version").toString()):-1;        
                                
                JSONArray dbRowsToJsonArr = new JSONArray();        
                dbRowsToJsonArr = ClassReqProcedureQueries.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.BUSINESS_RULES_IN_SOLUTION.getViewName(),
                        new String[]{TblsReqs.viewBusinessRulesInSolution.AREA.getName(),
                    TblsReqs.viewBusinessRulesInSolution.RULE_NAME.getName(), TblsReqs.viewBusinessRulesInSolution.PRESENT.getName(), TblsReqs.viewBusinessRulesInSolution.REQUIREMENTS_LIST.getName(),
                    TblsReqs.viewBusinessRulesInSolution.PREREQUISITE.getName(), TblsReqs.viewBusinessRulesInSolution.IS_MANDATORY.getName()},
                        new String[]{TblsReqs.ModuleBusinessRules.MODULE_NAME.getName(), TblsReqs.ModuleBusinessRules.MODULE_VERSION.getName()},
                        new Object[]{moduleName, moduleVersion},
                        new String[]{TblsReqs.viewBusinessRulesInSolution.PRESENT.getName()+" desc",  TblsReqs.viewBusinessRulesInSolution.AREA.getName(), TblsReqs.viewBusinessRulesInSolution.RULE_NAME.getName()},
                        new String[]{},
                        true);
                curProcObj.put("module_in_solution_business_rules", dbRowsToJsonArr);
                dbRowsToJsonArr = new JSONArray();        
                dbRowsToJsonArr = ClassReqProcedureQueries.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.ACTIONS_IN_SOLUTION.getViewName(),
                        new String[]{TblsReqs.viewActionsInSolution.PRETTY_EN.getName(), TblsReqs.viewActionsInSolution.PRETTY_ES.getName(),
                            TblsReqs.viewActionsInSolution.ENTITY.getName(), TblsReqs.viewActionsInSolution.PRESENT.getName(), TblsReqs.viewActionsInSolution.REQUIREMENTS_LIST.getName(),
                            TblsReqs.viewActionsInSolution.ENDPOINT_NAME.getName(), TblsReqs.viewActionsInSolution.API_NAME.getName()},
                        new String[]{TblsReqs.viewActionsInSolution.MODULE_NAME.getName(), TblsReqs.viewActionsInSolution.MODULE_VERSION.getName()},
                        new Object[]{moduleName, moduleVersion},
                        new String[]{TblsReqs.viewActionsInSolution.PRESENT.getName()+" desc", TblsReqs.viewQueriesInSolution.ENTITY.getName(), TblsReqs.viewActionsInSolution.API_NAME.getName(), TblsReqs.viewActionsInSolution.ENDPOINT_NAME.getName()},
                        new String[]{},
                        true);
                curProcObj.put("module_in_solution_actions", dbRowsToJsonArr);
                dbRowsToJsonArr = ClassReqProcedureQueries.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.QUERIES_IN_SOLUTION.getViewName(),
                        new String[]{TblsReqs.viewActionsInSolution.PRETTY_EN.getName(), TblsReqs.viewActionsInSolution.PRETTY_ES.getName(),
                            TblsReqs.viewQueriesInSolution.ENTITY.getName(), TblsReqs.viewQueriesInSolution.PRESENT.getName(), TblsReqs.viewQueriesInSolution.REQUIREMENTS_LIST.getName(),
                            TblsReqs.viewQueriesInSolution.ENDPOINT_NAME.getName(), TblsReqs.viewQueriesInSolution.API_NAME.getName()},
                        new String[]{TblsReqs.ModuleBusinessRules.MODULE_NAME.getName(), TblsReqs.ModuleBusinessRules.MODULE_VERSION.getName()},
                        new Object[]{moduleName, moduleVersion},
                        new String[]{TblsReqs.viewQueriesInSolution.PRESENT.getName()+" desc",  TblsReqs.viewQueriesInSolution.ENTITY.getName(), TblsReqs.viewActionsInSolution.API_NAME.getName(), TblsReqs.viewQueriesInSolution.ENDPOINT_NAME.getName()},
                        new String[]{},
                        true);
                curProcObj.put("module_in_solution_queries", dbRowsToJsonArr);                
                
                proceduresList.add(curProcObj);                            
            }
            return proceduresList;

        }
    }
    
}
