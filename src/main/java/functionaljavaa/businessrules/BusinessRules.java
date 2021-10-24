/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.businessrules;

import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsProcedure;
import databases.TblsTesting;
import java.util.ArrayList;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

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
        //if (scriptId==null || procedureInstanceName==null) return;
        Object[][] testingBusRulsInfo = null;
        if (scriptId!=null && scriptId>0)
            testingBusRulsInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procedureInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.ScriptBusinessRules.TBL.getName(), 
                new String[]{TblsTesting.ScriptBusinessRules.FLD_SCRIPT_ID.getName(), TblsTesting.ScriptBusinessRules.FLD_ACTIVE.getName()}, new Object[]{scriptId, true}, 
                new String[]{TblsTesting.ScriptBusinessRules.FLD_REPOSITORY.getName(), TblsTesting.ScriptBusinessRules.FLD_RULE_NAME.getName(), TblsTesting.ScriptBusinessRules.FLD_RULE_VALUE.getName()});
        else
            testingBusRulsInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procedureInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.ProcedureBusinessRules.TBL.getName(), 
                new String[]{TblsProcedure.ProcedureBusinessRules.FLD_AREA.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{}, 
                new String[]{TblsProcedure.ProcedureBusinessRules.FLD_AREA.getName(), TblsProcedure.ProcedureBusinessRules.FLD_RULE_NAME.getName(), TblsProcedure.ProcedureBusinessRules.FLD_RULE_VALUE.getName()});
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
    public String getConfigBusinessRule(String ruleName){
        for (RuleInfo curElement : this.config) {
            if (ruleName.equalsIgnoreCase(curElement.getRuleName())) 
                return curElement.getRuleValue();
        }
        return "";
    }
    public String getDataBusinessRule(String ruleName){
        for (RuleInfo curElement : this.data) {
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
    public static Object[][] SessionBusinessRulesList(){
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, false);
        BusinessRules bRProcInstance = procReqInstance.getBusinessRulesProcInstance();
        BusinessRules bRTesting = procReqInstance.getBusinessRulesTesting();
        Object[][] valsArr=new Object[][]{{}};
        if (bRProcInstance!=null){
            Integer totalRules=bRProcInstance.config.size()+bRProcInstance.data.size()+bRProcInstance.procedure.size();
            valsArr=new Object[totalRules][2];                        
        }        
        int curBusRul=0;
        if (bRTesting!=null){
            for (int i=0;i<bRTesting.config.size();i++){
                String brValue=bRProcInstance.getProcedureBusinessRule(bRTesting.config.get(i).getRuleName());
                if (brValue.length()>0){
                    valsArr[curBusRul][0]=" *** "+bRTesting.config.get(i).getRuleName();
                    valsArr[curBusRul][1]=bRTesting.config.get(i).getRuleValue()+" ("+brValue+")";
                }else{                    
                    valsArr[curBusRul][0]=bRTesting.config.get(i).getRuleName();
                    valsArr[curBusRul][1]=bRTesting.config.get(i).getRuleValue();
                }
                curBusRul++;
            }
            for (int i=0;i<bRTesting.data.size();i++){
                String brValue=bRProcInstance.getProcedureBusinessRule(bRTesting.data.get(i).getRuleName());
                if (brValue.length()>0){
                    valsArr[curBusRul][0]=" *** "+bRTesting.data.get(i).getRuleName();
                    valsArr[curBusRul][1]=bRTesting.data.get(i).getRuleValue()+" ("+brValue+")";
                }else{                    
                    valsArr[curBusRul][0]=bRTesting.data.get(i).getRuleName();
                    valsArr[curBusRul][1]=bRTesting.data.get(i).getRuleValue();
                }
                curBusRul++;
            }
            for (int i=0;i<bRTesting.procedure.size();i++){
                String brValue=bRProcInstance.getProcedureBusinessRule(bRTesting.procedure.get(i).getRuleName());
                if (brValue.length()>0){
                    valsArr[curBusRul][0]=" *** "+bRTesting.procedure.get(i).getRuleName();
                    valsArr[curBusRul][1]=bRTesting.procedure.get(i).getRuleValue()+" ("+brValue+")";
                }else{                    
                    valsArr[curBusRul][0]=bRTesting.procedure.get(i).getRuleName();
                    valsArr[curBusRul][1]=bRTesting.procedure.get(i).getRuleValue();
                }
                curBusRul++;
            }
           //for (RuleInfo curR: bRTesting.config.)
        }
        if (bRProcInstance!=null){
            for (int i=0;i<bRProcInstance.config.size();i++){
                String brValue=bRTesting.getProcedureBusinessRule(bRProcInstance.config.get(i).getRuleName());
                if (brValue.length()==0){                
                    valsArr[curBusRul][0]=bRProcInstance.config.get(i).getRuleName();
                    valsArr[curBusRul][1]=bRProcInstance.config.get(i).getRuleValue();
                    curBusRul++;
                }
            }
            for (int i=0;i<bRProcInstance.data.size();i++){
                String brValue=bRTesting.getProcedureBusinessRule(bRProcInstance.data.get(i).getRuleName());
                if (brValue.length()==0){                
                    valsArr[curBusRul][0]=bRProcInstance.data.get(i).getRuleName();
                    valsArr[curBusRul][1]=bRProcInstance.data.get(i).getRuleValue();
                    curBusRul++;
                }
            }
            for (int i=0;i<bRProcInstance.procedure.size();i++){
                String brValue=bRTesting.getProcedureBusinessRule(bRProcInstance.procedure.get(i).getRuleName());
                if (brValue.length()==0){                
                    valsArr[curBusRul][0]=bRProcInstance.procedure.get(i).getRuleName();
                    valsArr[curBusRul][1]=bRProcInstance.procedure.get(i).getRuleValue();
                    curBusRul++;
                }
            }
        }
        return valsArr;
    }
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
