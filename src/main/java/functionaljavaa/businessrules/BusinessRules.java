/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.businessrules;

import databases.Rdbms;
import databases.TblsProcedure;
import databases.TblsTesting;
import java.util.ArrayList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class BusinessRules {
    static String startsMark=" *** ";
    String procedureInstanceName;
    ArrayList<RuleInfo> procedure;
    ArrayList<RuleInfo> data;
    ArrayList<RuleInfo> config;
    Integer totalBusinessRules;

    public BusinessRules(String procedureInstanceName, Integer scriptId, JsonArray busRulesList){
        this.procedureInstanceName=procedureInstanceName;
        this.procedure=new ArrayList();
        this.data=new ArrayList();
        this.config=new ArrayList();
        for (int i = 0; i < busRulesList.size(); i++) {
            JsonObject object = (JsonObject) busRulesList.get(i);
            String suffix=object.get("suffix").getAsString();
            String ruleName=object.get("ruleName").getAsString();
            String ruleValue=object.get("ruleName").getAsString();            
            if ("PROCEDURE".equalsIgnoreCase(suffix))
                this.procedure.add(new RuleInfo(ruleName, ruleValue));
            if ("DATA".equalsIgnoreCase(suffix))
                this.data.add(new RuleInfo(ruleName, ruleValue));
            if ("CONFIG".equalsIgnoreCase(suffix))
                this.config.add(new RuleInfo(ruleName, ruleValue));
        this.totalBusinessRules=this.procedure.size()+this.config.size()+this.data.size();            
        }
    }
    public BusinessRules(String procedureInstanceName, Integer scriptId){
        this.procedureInstanceName=procedureInstanceName;
        this.procedure=new ArrayList();
        this.data=new ArrayList();
        this.config=new ArrayList();
//        if(1==1) return;
        Object[][] testingBusRulsInfo = null;
        if (scriptId!=null && scriptId>0)
            testingBusRulsInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procedureInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT_BUS_RULES.getTableName(), 
                new String[]{TblsTesting.ScriptBusinessRules.SCRIPT_ID.getName(), TblsTesting.ScriptBusinessRules.ACTIVE.getName()}, new Object[]{scriptId, true}, 
                new String[]{TblsTesting.ScriptBusinessRules.REPOSITORY.getName(), TblsTesting.ScriptBusinessRules.RULE_NAME.getName(), TblsTesting.ScriptBusinessRules.RULE_VALUE.getName()});
        else
            if (procedureInstanceName!=null && procedureInstanceName.length()>0)
                testingBusRulsInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procedureInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), 
                    new String[]{TblsProcedure.ProcedureBusinessRules.DISABLED.getName()}, new Object[]{false}, 
                    new String[]{TblsProcedure.ProcedureBusinessRules.AREA.getName(), TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName(), TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()});
            else
                testingBusRulsInfo=new Object[][]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "BusinessRulesNotCompatibleYetForPlatformRules", null)};
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testingBusRulsInfo[0][0].toString())) return;
        for (Object[] curObj: testingBusRulsInfo){
            if ("PROCEDURE".equalsIgnoreCase(curObj[0].toString()))
                this.procedure.add(new RuleInfo(curObj[1].toString(), curObj[2].toString()));
            if ("DATA".equalsIgnoreCase(curObj[0].toString()))
                this.data.add(new RuleInfo(curObj[1].toString(), curObj[2].toString()));
            if ("CONFIG".equalsIgnoreCase(curObj[0].toString()))
                this.config.add(new RuleInfo(curObj[1].toString(), curObj[2].toString()));
        }
        this.totalBusinessRules=this.procedure.size()+this.config.size()+this.data.size();
    }
    public Integer getTotalBusinessRules(){
        return this.totalBusinessRules;}
    public ArrayList<RuleInfo> getProcedureBusinessRules(){return this.procedure;}    
    public String getProcedureBusinessRule(String ruleName){
        if (this.procedure!=null){
            for (RuleInfo curElement : this.procedure) {
                if (ruleName.equalsIgnoreCase(curElement.getRuleName())) 
    //                LPPlatform.saveParameterPropertyInDbErrorLog("", this.procedureInstanceName+"-"+"procedure", new Object[]{}, ruleName);                
                    return curElement.getRuleValue();
            }
        }
        return "";
    }
    public ArrayList<RuleInfo> getConfigBusinessRules(){return this.config;}
    public String getConfigBusinessRule(String ruleName){
        if (this.config!=null){        
            for (RuleInfo curElement : this.config) {
                if (ruleName.equalsIgnoreCase(curElement.getRuleName())) 
    //                LPPlatform.saveParameterPropertyInDbErrorLog("", this.procedureInstanceName+"-"+"config", new Object[]{}, ruleName);                
                    return curElement.getRuleValue();
            }
        }
        return "";
    }
    public ArrayList<RuleInfo> getDataBusinessRules(){return this.data;}    
    public String getDataBusinessRule(String ruleName){
        if (this.data!=null){
            for (RuleInfo curElement : this.data) {
                if (ruleName.equalsIgnoreCase(curElement.getRuleName())) 
    //                LPPlatform.saveParameterPropertyInDbErrorLog("", this.procedureInstanceName+"-"+"data", new Object[]{}, ruleName);                
                    return curElement.getRuleValue();
            }
        }
        return "";
    }
    public static Object[][] sessionBusinessRulesList(){
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, false);
        BusinessRules bRProcInstance = procReqInstance.getBusinessRulesProcInstance();
        BusinessRules bRTesting = procReqInstance.getBusinessRulesTesting();
        if (bRProcInstance!=null){
            Integer totalRules=bRProcInstance.config.size()+bRProcInstance.data.size()+bRProcInstance.procedure.size();
        }        
        Object[] ruleNameArr=new Object[]{};
        if (bRTesting!=null){
            for (int i=0;i<bRTesting.config.size();i++){
                String brValue=bRProcInstance.getProcedureBusinessRule(bRTesting.config.get(i).getRuleName());
                if (brValue.length()>0){
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, startsMark+bRTesting.config.get(i).getRuleName());
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRTesting.config.get(i).getRuleValue()+" ("+brValue+")");
                }else{                    
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRTesting.config.get(i).getRuleName());
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRTesting.config.get(i).getRuleValue());
                }
            }
            for (int i=0;i<bRTesting.data.size();i++){
                String brValue=bRProcInstance.getProcedureBusinessRule(bRTesting.data.get(i).getRuleName());
                if (brValue.length()>0){
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, startsMark+bRTesting.data.get(i).getRuleName());
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRTesting.data.get(i).getRuleValue()+" ("+brValue+")");
                }else{              
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRTesting.data.get(i).getRuleName());
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRTesting.data.get(i).getRuleValue());                    
                }
            }
            for (int i=0;i<bRTesting.procedure.size();i++){
                String brValue="";
                if (bRProcInstance!=null)
                    brValue=bRProcInstance.getProcedureBusinessRule(bRTesting.procedure.get(i).getRuleName());
                if (brValue.length()>0){
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, startsMark+bRTesting.procedure.get(i).getRuleName());
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRTesting.procedure.get(i).getRuleValue()+" ("+brValue+")");                    
                }else{                    
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRTesting.procedure.get(i).getRuleName());
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRTesting.procedure.get(i).getRuleValue());                    
                }
            }
        }
        if (bRProcInstance!=null){
            for (int i=0;i<bRProcInstance.config.size();i++){
                String brValue="";
                if (bRTesting!=null)
                    brValue=bRTesting.getConfigBusinessRule(bRProcInstance.config.get(i).getRuleName());
                if (brValue.length()==0){   
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRProcInstance.config.get(i).getRuleName());
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRProcInstance.config.get(i).getRuleValue());                                        
                }
            }
            for (int i=0;i<bRProcInstance.data.size();i++){
                String brValue="";
                if (bRTesting!=null)
                    brValue=bRTesting.getProcedureBusinessRule(bRProcInstance.data.get(i).getRuleName());
                if (brValue.length()==0){ 
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRProcInstance.data.get(i).getRuleName());
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRProcInstance.data.get(i).getRuleValue());                                                            
                }
            }
            for (int i=0;i<bRProcInstance.procedure.size();i++){
                String brValue="";
                if (bRTesting!=null)
                    brValue=bRTesting.getProcedureBusinessRule(bRProcInstance.procedure.get(i).getRuleName());
                if (brValue.length()==0){                
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRProcInstance.procedure.get(i).getRuleName());
                    ruleNameArr=LPArray.addValueToArray1D(ruleNameArr, bRProcInstance.procedure.get(i).getRuleValue());                                        
                }
            }
        }
        return LPArray.array1dTo2d(ruleNameArr, 2);
    }
}
