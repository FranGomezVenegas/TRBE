/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.businessrules;

import databases.Rdbms;
import databases.TblsTesting;
import java.util.ArrayList;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class BusinessRules {
    public BusinessRules(String procedureInstanceName, Integer scriptId){
        this.procedureInstanceName=procedureInstanceName;
        this.procedure=new ArrayList<RuleInfo>();
        this.data=new ArrayList<RuleInfo>();
        this.config=new ArrayList<RuleInfo>();
        if (scriptId==null || procedureInstanceName==null) return;
        Object[][] testingBusRulsInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procedureInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.ScriptBusinessRules.TBL.getName(), 
            new String[]{TblsTesting.ScriptBusinessRules.FLD_SCRIPT_ID.getName(), TblsTesting.ScriptBusinessRules.FLD_ACTIVE.getName()}, new Object[]{scriptId, true}, 
            new String[]{TblsTesting.ScriptBusinessRules.FLD_REPOSITORY.getName(), TblsTesting.ScriptBusinessRules.FLD_RULE_NAME.getName(), TblsTesting.ScriptBusinessRules.FLD_RULE_VALUE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testingBusRulsInfo[0][0].toString())) return;
        for (Object[] curObj: testingBusRulsInfo){
            if ("PROCEDURE".equalsIgnoreCase(curObj[0].toString()))
                this.procedure.add(new RuleInfo(curObj[1].toString(), curObj[2].toString()));
            if ("DATA".equalsIgnoreCase(curObj[0].toString()))
                this.data.add(new RuleInfo(curObj[1].toString(), curObj[2].toString()));
            if ("CONFIG".equalsIgnoreCase(curObj[0].toString()))
                this.config.add(new RuleInfo(curObj[1].toString(), curObj[2].toString()));
        }
        
    }
    String procedureInstanceName;
    ArrayList<RuleInfo> procedure;
    ArrayList<RuleInfo> data;
    ArrayList<RuleInfo> config;
    public String getProcedureBusinessRule(String ruleName){
        for (RuleInfo curElement : this.procedure) {
            if (ruleName.equalsIgnoreCase(curElement.getRuleName())) 
                return curElement.getRuleValue();
        }
        return "";
    }
/*        List<RuleInfo> result = ArrayList.stream()
     .filter(item -> item.value3.equals(ruleName))
     .collect(Collectors.toList());
        return result.get(0).getRuleValue();
    }*/
}
class RuleInfo{
    public RuleInfo(String ruleName, String ruleValue){
        this.ruleName=ruleName;
        this.ruleValue=ruleValue;
    }
    public String getRuleValue(){return this.ruleValue;}
    public String getRuleName(){return this.ruleName;}
    String ruleName;
    String ruleValue;
    
}
