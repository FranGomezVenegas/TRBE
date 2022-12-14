/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platformadmin;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.SqlStatement;
import databases.TblsApp;
import databases.TblsProcedure;
import databases.features.Token;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.queries.QueryUtilitiesEnums;

/**
 *
 * @author User
 */
public class AppBusinessRules {
    public static JSONObject AllAppBusinessRules(HttpServletRequest request, HttpServletResponse response){
    try{
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String language = LPFrontEnd.setLanguage(request); 
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);        
        if (finalToken==null || finalToken.length()==0)
            finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
        Token token = new Token(finalToken);
        
        Object[][] appBusRulesInfo = QueryUtilitiesEnums.getTableData(TblsApp.TablesApp.APP_BUSINESS_RULES, 
            EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.APP_BUSINESS_RULES, 
                new String[]{TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName(), TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()}), 
            new String[]{TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
            new Object[]{""}, null, "app");
/*        Object[][] appBusRulesInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_BUSINESS_RULES.getName(), "business_rules", 
            new String[]{"rule_value"+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{""}, 
            new String[]{"rule_name", "rule_value"}, new String[]{"area", "order_number"});
*/
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(appBusRulesInfo[0][0]).toString())){
            return new JSONObject();
        }
        JSONObject appBusinessRulesJArr = new JSONObject(); 
        for (Object[] curBusRule: appBusRulesInfo){
            appBusinessRulesJArr.put(curBusRule[0].toString(), curBusRule[1].toString());
        }    
        return appBusinessRulesJArr;
    }catch(Exception e){
        JSONObject proceduresList = new JSONObject();
        proceduresList.put("Error",e.getMessage());
        return proceduresList;            
    }
    }
    
}
