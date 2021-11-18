/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import databases.Rdbms;
import databases.SqlStatement;
import databases.Token;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class AppBusinessRules {
    public static JSONArray AllAppBusinessRules(HttpServletRequest request, HttpServletResponse response){
    try{
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String language = LPFrontEnd.setLanguage(request); 
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);        
        if (finalToken==null || finalToken.length()==0)
            finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
        Token token = new Token(finalToken);
        
        Object[][] appBusRulesInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_BUSINESS_RULES.getName(), "business_rules", 
            new String[]{"rule_value"+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{""}, 
            new String[]{"rule_name", "rule_value"}, new String[]{"area", "order_number"});

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(appBusRulesInfo[0][0]).toString())){
            return new JSONArray();
        }
        JSONArray appBusinessRulesJArr = new JSONArray(); 
        for (Object[] curBusRule: appBusRulesInfo){
            JSONObject brJObj = new JSONObject();
            brJObj.put(curBusRule[0].toString(), curBusRule[1].toString());
            appBusinessRulesJArr.add(brJObj);
        }    
        return appBusinessRulesJArr;
    }catch(Exception e){
        JSONArray proceduresList = new JSONArray();
        proceduresList.add("Error:"+e.getMessage());
        return proceduresList;            
    }
    }
    
}
