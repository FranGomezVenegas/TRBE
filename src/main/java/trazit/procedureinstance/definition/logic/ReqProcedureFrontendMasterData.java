/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.definition.logic;

import databases.Rdbms;
import databases.SqlWhere;
import java.util.HashSet;
import java.util.Set;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.enums.FrontendMasterData;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.definition.definition.TblsReqs;
import trazit.queries.QueryUtilities;

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
                dbRowsToJsonArr = QueryUtilities.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.MODULE_ERROR_NOTIFICATIONS,
                    new EnumIntTableFields[]{TblsReqs.ModuleErrorNotifications.API_NAME,  TblsReqs.ModuleErrorNotifications.ERROR_CODE},
                    new SqlWhere(TblsReqs.TablesReqs.MODULE_ERROR_NOTIFICATIONS, new String[]{TblsReqs.ModuleErrorNotifications.MODULE_NAME.getName(), TblsReqs.ModuleErrorNotifications.MODULE_VERSION.getName()},
                        new Object[]{moduleName, moduleVersion}),
                    new String[]{TblsReqs.ModuleErrorNotifications.API_NAME.getName()},
                    new String[]{}, true);
                curProcObj.put(TblsReqs.TablesReqs.MODULE_ERROR_NOTIFICATIONS.getTableName(), dbRowsToJsonArr);
                dbRowsToJsonArr = QueryUtilities.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.BUSINESS_RULES_IN_SOLUTION.getViewName(),
                        new String[]{TblsReqs.viewBusinessRulesInSolution.AREA.getName(),
                    TblsReqs.viewBusinessRulesInSolution.RULE_NAME.getName(), TblsReqs.viewBusinessRulesInSolution.PRESENT.getName(), TblsReqs.viewBusinessRulesInSolution.REQUIREMENTS_LIST.getName(),
                    TblsReqs.viewBusinessRulesInSolution.PREREQUISITE.getName(), TblsReqs.viewBusinessRulesInSolution.IS_MANDATORY.getName()},
                        new String[]{TblsReqs.ModuleBusinessRules.MODULE_NAME.getName(), TblsReqs.ModuleBusinessRules.MODULE_VERSION.getName()},
                        new Object[]{moduleName, moduleVersion},
                        new String[]{TblsReqs.viewBusinessRulesInSolution.RULE_NAME.getName(),  TblsReqs.viewBusinessRulesInSolution.AREA.getName(), TblsReqs.viewBusinessRulesInSolution.RULE_NAME.getName()},
                        new String[]{}, true, true);
                curProcObj.put("module_in_solution_business_rules", dbRowsToJsonArr);                
                dbRowsToJsonArr = QueryUtilities.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.ACTIONS_IN_SOLUTION.getViewName(),
                    new String[]{TblsReqs.viewActionsInSolution.PRETTY_EN.getName(), TblsReqs.viewActionsInSolution.PRETTY_ES.getName(),
                        TblsReqs.viewActionsInSolution.PURPOSE_EN.getName(), TblsReqs.viewActionsInSolution.PURPOSE_ES.getName(),
                        TblsReqs.viewActionsInSolution.ENTITY.getName(), TblsReqs.viewActionsInSolution.PRESENT.getName(), TblsReqs.viewActionsInSolution.REQUIREMENTS_LIST.getName(),
                        TblsReqs.viewActionsInSolution.ENDPOINT_NAME.getName(), TblsReqs.viewActionsInSolution.API_NAME.getName(),
                        TblsReqs.viewActionsInSolution.ARGUMENTS_ARRAY.getName(), TblsReqs.viewActionsInSolution.OUTPUT_OBJECT_TYPES.getName()},
                    new String[]{TblsReqs.viewActionsInSolution.MODULE_NAME.getName(), TblsReqs.viewActionsInSolution.MODULE_VERSION.getName()},
                    new Object[]{moduleName, moduleVersion},
                    new String[]{TblsReqs.viewActionsInSolution.ENDPOINT_NAME.getName(), TblsReqs.viewQueriesInSolution.ENTITY.getName(), TblsReqs.viewActionsInSolution.API_NAME.getName(), TblsReqs.viewActionsInSolution.ENDPOINT_NAME.getName()},
                    new String[]{}, true, true);
                JSONArray dbRowsToJsonFinalArr = new JSONArray();
                Set<String> uniqueEndpointNames = new HashSet<>();
                for (int i=0;i<dbRowsToJsonArr.size();i++){
                    JSONObject curRow = (JSONObject) dbRowsToJsonArr.get(i);     
                    String curEndpointName=curRow.get(TblsReqs.viewActionsInSolution.ENDPOINT_NAME.getName()).toString();
                    if (!uniqueEndpointNames.contains(curEndpointName)) {
                        uniqueEndpointNames.add(curEndpointName);
                        JSONObject curRowObj = new JSONObject();
                        curRowObj.put(TblsReqs.viewActionsInSolution.ENDPOINT_NAME.getName(), curEndpointName);
                        curRowObj.put(TblsReqs.viewActionsInSolution.ARGUMENTS_ARRAY.getName(), LPJson.convertToJsonArrayStringedObject(curRow.get(TblsReqs.viewActionsInSolution.ARGUMENTS_ARRAY.getName()).toString()));
                        curRowObj.put(TblsReqs.viewActionsInSolution.ENTITY.getName(), curRow.get(TblsReqs.viewActionsInSolution.ENTITY.getName()).toString());
                        curRowObj.put(TblsReqs.viewActionsInSolution.PURPOSE_EN.getName(), curRow.get(TblsReqs.viewActionsInSolution.PURPOSE_EN.getName()).toString());
                        curRowObj.put(TblsReqs.viewActionsInSolution.PURPOSE_ES.getName(), curRow.get(TblsReqs.viewActionsInSolution.PURPOSE_ES.getName()).toString());
                        dbRowsToJsonFinalArr.add(curRowObj);                        
                    }
                }
                curProcObj.put("module_in_solution_actions", dbRowsToJsonFinalArr);
                
                dbRowsToJsonArr = QueryUtilities.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.QUERIES_IN_SOLUTION.getViewName(),
                    new String[]{TblsReqs.viewQueriesInSolution.PRETTY_EN.getName(), TblsReqs.viewQueriesInSolution.PRETTY_ES.getName(),
                        TblsReqs.viewQueriesInSolution.PURPOSE_EN.getName(), TblsReqs.viewQueriesInSolution.PURPOSE_ES.getName(),
                        TblsReqs.viewQueriesInSolution.ENTITY.getName(), TblsReqs.viewQueriesInSolution.PRESENT.getName(), TblsReqs.viewQueriesInSolution.REQUIREMENTS_LIST.getName(),
                        TblsReqs.viewQueriesInSolution.ENDPOINT_NAME.getName(), TblsReqs.viewQueriesInSolution.API_NAME.getName(),
                        TblsReqs.viewQueriesInSolution.ARGUMENTS_ARRAY.getName(), TblsReqs.viewQueriesInSolution.OUTPUT_OBJECT_TYPES.getName()},
                    new String[]{TblsReqs.viewQueriesInSolution.MODULE_NAME.getName(), TblsReqs.viewQueriesInSolution.MODULE_VERSION.getName()},
                    new Object[]{moduleName, moduleVersion},
                    new String[]{TblsReqs.viewQueriesInSolution.ENDPOINT_NAME.getName(),  TblsReqs.viewQueriesInSolution.ENTITY.getName(), TblsReqs.viewQueriesInSolution.API_NAME.getName(), TblsReqs.viewQueriesInSolution.ENDPOINT_NAME.getName()},
                    new String[]{}, true, true);
                dbRowsToJsonFinalArr = new JSONArray();
                uniqueEndpointNames = new HashSet<>();
                for (int i=0;i<dbRowsToJsonArr.size();i++){
                    JSONObject curRow = (JSONObject) dbRowsToJsonArr.get(i);     
                    String curEndpointName=curRow.get(TblsReqs.viewQueriesInSolution.ENDPOINT_NAME.getName()).toString();
                    if (!uniqueEndpointNames.contains(curEndpointName)) {
                        uniqueEndpointNames.add(curEndpointName);
                        JSONObject curRowObj = new JSONObject();
                        curRowObj.put(TblsReqs.viewQueriesInSolution.ENDPOINT_NAME.getName(), curEndpointName);
                        curRowObj.put(TblsReqs.viewQueriesInSolution.ARGUMENTS_ARRAY.getName(), LPJson.convertToJsonArrayStringedObject(curRow.get(TblsReqs.viewQueriesInSolution.ARGUMENTS_ARRAY.getName()).toString()));
                        curRowObj.put(TblsReqs.viewQueriesInSolution.ENTITY.getName(), curRow.get(TblsReqs.viewQueriesInSolution.ENTITY.getName()).toString());
                        curRowObj.put(TblsReqs.viewQueriesInSolution.PURPOSE_EN.getName(), curRow.get(TblsReqs.viewQueriesInSolution.PURPOSE_EN.getName()).toString());
                        curRowObj.put(TblsReqs.viewQueriesInSolution.PURPOSE_ES.getName(), curRow.get(TblsReqs.viewQueriesInSolution.PURPOSE_ES.getName()).toString());
                        dbRowsToJsonFinalArr.add(curRowObj);                        
                    }
                }
                curProcObj.put("module_in_solution_queries", dbRowsToJsonFinalArr);                

                dbRowsToJsonArr=QueryUtilities.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.SPECIAL_VIEWS_IN_SOLUTION.getViewName(),
                    new String[]{TblsReqs.viewSpecialViewsInSolution.PRETTY_EN.getName(), TblsReqs.viewSpecialViewsInSolution.PRETTY_ES.getName(),
                        TblsReqs.viewSpecialViewsInSolution.ENTITY.getName(), TblsReqs.viewSpecialViewsInSolution.PRESENT.getName(), TblsReqs.viewSpecialViewsInSolution.REQUIREMENTS_LIST.getName(),
                        TblsReqs.viewSpecialViewsInSolution.VIEW_NAME.getName()},
                    new String[]{TblsReqs.viewSpecialViewsInSolution.MODULE_NAME.getName(), TblsReqs.viewSpecialViewsInSolution.MODULE_VERSION.getName()},
                    new Object[]{moduleName, moduleVersion},
                    new String[]{TblsReqs.viewSpecialViewsInSolution.VIEW_NAME.getName(),  TblsReqs.viewSpecialViewsInSolution.ENTITY.getName(), TblsReqs.viewSpecialViewsInSolution.VIEW_NAME.getName()},
                    new String[]{}, true, true);        
                dbRowsToJsonFinalArr=new JSONArray();
                for (int i=0;i<dbRowsToJsonArr.size();i++){
                    JSONObject curRow = (JSONObject) dbRowsToJsonArr.get(i);     
                    String curEndpointName=curRow.get(TblsReqs.viewSpecialViewsInSolution.VIEW_NAME.getName()).toString();
                    if (Boolean.FALSE.equals(LPJson.ValueInJsonArray(LPJson.convertJsonArrayToJSONArray(dbRowsToJsonFinalArr), curEndpointName))){
                        JSONObject curRowObj=new JSONObject();
                        curRowObj.put(TblsReqs.viewSpecialViewsInSolution.VIEW_NAME.getName(), curEndpointName);
                        dbRowsToJsonFinalArr.add(curRowObj);
                    }
                    //com.google.gson.JsonArray argArrayToJson = LPJson.convertToJsonArrayStringedObject(
                    //    curRow.get(TblsTrazitDocTrazit.EndpointsDeclaration.ARGUMENTS_ARRAY.getName()).toString());
                    //curRow.put(TblsTrazitDocTrazit.EndpointsDeclaration.ARGUMENTS_ARRAY.getName(), argArrayToJson);
                    //dbRowsToJsonFinalArr.add(curRow);
                }            
                curProcObj.put("module_in_solution_special_views", dbRowsToJsonFinalArr);


                proceduresList.add(curProcObj);                            

            }
            return proceduresList;

        }
    }
    
}
