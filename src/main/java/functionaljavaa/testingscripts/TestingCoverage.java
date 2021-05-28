/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

import com.google.gson.JsonArray;
import databases.Rdbms;
import databases.TblsReqs;
import databases.TblsTesting;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class TestingCoverage {
    
    private String moduleName;
    private final String procInstanceName;
    private final Integer coverageId;
    private final String scriptIds;
    private Integer totalBusRules;
    private Integer totalMessages;
    private Object[][] procBusRules;
    private JsonArray scriptsBusRules;
    
    public TestingCoverage(String procInstanceName, Integer coverageId){
        this.procInstanceName=procInstanceName;
        this.coverageId=coverageId;
        this.scriptIds="2";

        Object[][] procBusRul = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureBusinessRules.TBL.getName(), 
                new String[]{TblsReqs.ProcedureBusinessRules.FLD_INSTANCE_NAME.getName()}, new Object[]{procInstanceName}, 
                new String[]{TblsReqs.ProcedureBusinessRules.FLD_MODULE_NAME.getName(), TblsReqs.ProcedureBusinessRules.FLD_FILE_SUFFIX.getName(),
                    TblsReqs.ProcedureBusinessRules.FLD_RULE_NAME.getName()});
        this.procBusRules=procBusRul;
        Object[][] scriptsBusRul = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.Script.TBL.getName(), 
                new String[]{TblsTesting.Script.FLD_SCRIPT_ID.getName()}, new Object[]{Integer.valueOf(this.scriptIds)}, 
                new String[]{TblsTesting.Script.FLD_BUSINESS_RULES_VISITED.getName(), TblsTesting.Script.FLD_MESSAGES_VISITED.getName()});
        this.scriptsBusRules=LPJson.convertToJsonArrayStringedObject(scriptsBusRul[0][0].toString());
     
    }
    
    public void calculateCoverage(){
        Integer currentBusRulesVisits=0;
        JSONArray jArr=new JSONArray();
        JSONObject jObj=new JSONObject();
        jObj.put("total procedure business rules", this.procBusRules.length);
        jArr.add(jObj);
        jObj=new JSONObject();
        jObj.put("total scripts business rules visited", currentBusRulesVisits);
        jArr.add(jObj);
        
        //scriptsBusRules.forEach(action);
        Object[] updateCoverageRow = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.ScriptsCoverage.TBL.getName(), 
                new String[]{TblsTesting.ScriptsCoverage.FLD_BUS_RULES_COVERAGE.getName(), TblsTesting.ScriptsCoverage.FLD_BUS_RULES_COVERAGE_DETAIL.getName()}, 
                new Object[]{currentBusRulesVisits, jArr.toJSONString()}, 
                new String[]{TblsTesting.ScriptsCoverage.FLD_COVERAGE_ID.getName()}, new Object[]{coverageId});
        
        
    }
    
}
