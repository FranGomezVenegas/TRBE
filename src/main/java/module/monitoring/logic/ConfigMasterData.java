/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.logic;

import com.google.gson.JsonArray;
import functionaljavaa.businessrules.BusinessRules;
import functionaljavaa.parameter.Parameter;
import lbplanet.utilities.LPJson;
import org.json.simple.JSONObject;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ConfigMasterData {
    private ConfigMasterData() {throw new IllegalStateException("Utility class");}
    public static JSONObject getMasterData(String procInstanceName, BusinessRules bi){
        if (procInstanceName==null){
            ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false);
            procInstanceName=procReqInstance.getProcedureInstance();
        }        
        JSONObject masterDataObj = new JSONObject();
        String ruleName="samplerPersonalAreas";
        String objStr;
        if (bi==null){
            objStr=Parameter.getBusinessRuleProcedureFile(procInstanceName, "config", ruleName);
        }else{
            objStr=bi.getConfigBusinessRule(ruleName);
        }
        JsonArray objArr = LPJson.convertToJsonArrayStringedObject(objStr);
        masterDataObj.put(ruleName, objArr);
        return masterDataObj;
    }
}
