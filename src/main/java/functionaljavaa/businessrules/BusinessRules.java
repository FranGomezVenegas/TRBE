/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.businessrules;

import databases.Rdbms;
import databases.TblsProcedure;
import java.util.ArrayList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import databases.SqlStatement;
import databases.TblsTesting;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class BusinessRules {

    static String startsMark = " *** ";
    String procedureInstanceName;
    ArrayList<ActionInfo> actions;
    ArrayList<ActionInfo> actionsMasterData;
    String actionsListStr;
    String actionsListMasterDataStr;
    ArrayList<RuleInfo> procedure;
    ArrayList<RuleInfo> data;
    ArrayList<RuleInfo> config;
    Integer totalBusinessRules;

    public BusinessRules(String procedureInstanceName, Integer scriptId, JsonArray busRulesList) {
        this.procedureInstanceName = procedureInstanceName;
        this.actions = new ArrayList<>();
        this.procedure = new ArrayList<>();
        this.data = new ArrayList<>();
        this.config = new ArrayList<>();
        for (int i = 0; i < busRulesList.size(); i++) {
            JsonObject object = (JsonObject) busRulesList.get(i);
            String suffix = object.get("suffix").getAsString();
            String ruleName = object.get("ruleName").getAsString();
            String ruleValue = object.get("ruleName").getAsString();
            if ("PROCEDURE".equalsIgnoreCase(suffix)) {
                this.procedure.add(new RuleInfo(ruleName, ruleValue));
            }
            if ("DATA".equalsIgnoreCase(suffix)) {
                this.data.add(new RuleInfo(ruleName, ruleValue));
            }
            if ("CONFIG".equalsIgnoreCase(suffix)) {
                this.config.add(new RuleInfo(ruleName, ruleValue));
            }
            this.totalBusinessRules = this.procedure.size() + this.config.size() + this.data.size();
            setActions(procedureInstanceName);
        }
    }

    public static Object[] getProcInstanceActionsInfo(String procedureInstanceName){
        String[] fldNames=new String[]{TblsProcedure.ProcedureActions.ACTION_NAME.getName(), TblsProcedure.ProcedureActions.ROLES_NAME.getName(),
            TblsProcedure.ProcedureActions.ARE_YOU_SURE_REQUIRED.getName(), TblsProcedure.ProcedureActions.JUSTIF_REASON_REQUIRED.getName(), TblsProcedure.ProcedureActions.ESIGN_REQUIRED.getName(),
            TblsProcedure.ProcedureActions.USER_CREDENTIAL_REQUIRED.getName(), TblsProcedure.ProcedureActions.AUDIT_REASON_TYPE.getName(),
            TblsProcedure.ProcedureActions.AUDIT_LIST_EN.getName(), TblsProcedure.ProcedureActions.AUDIT_LIST_ES.getName()};
        Object[][] actionsInfo = Rdbms.getRecordFieldsByFilter(procedureInstanceName, LPPlatform.buildSchemaName(procedureInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName(),
                new String[]{TblsProcedure.ProcedureActions.ACTION_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{},fldNames);        
        return new Object[]{fldNames, actionsInfo};
    }
    private void setActions(String procedureInstanceName){
        Object[] dbTableExists = Rdbms.dbTableExists(procedureInstanceName, procedureInstanceName + "-procedure", 
                TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString())) {return;}
        JSONArray jArr = new JSONArray();
        Object[] procInstanceActionsListAll=getProcInstanceActionsInfo(procedureInstanceName);
        Object[][] procInstanceActionsList=(Object[][])procInstanceActionsListAll[1];
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procInstanceActionsList[0][0].toString())) {
            return;
        }        
        for (Object[] curAction : procInstanceActionsList) {
            this.actions.add(new ActionInfo(curAction[0].toString(), curAction[1].toString(), Boolean.valueOf(curAction[2].toString()), Boolean.valueOf(curAction[3].toString()), 
                Boolean.valueOf(curAction[4].toString()), Boolean.valueOf(curAction[5].toString()),
                curAction[6].toString(), curAction[7].toString(), curAction[8].toString()));
        }
        this.actionsListStr=Arrays.toString(LPArray.getColumnFromArray2D(procInstanceActionsList, 0));
    }    

    public static Object[] getProcInstanceActionsMasterDataInfo(String procedureInstanceName){
        String[] fldNames=new String[]{TblsProcedure.ProcedureActions.ACTION_NAME.getName(), TblsProcedure.ProcedureActions.ROLES_NAME.getName(),
            TblsProcedure.ProcedureActions.ARE_YOU_SURE_REQUIRED.getName(), TblsProcedure.ProcedureActions.JUSTIF_REASON_REQUIRED.getName(), TblsProcedure.ProcedureActions.ESIGN_REQUIRED.getName(),
            TblsProcedure.ProcedureActions.USER_CREDENTIAL_REQUIRED.getName(), TblsProcedure.ProcedureActions.AUDIT_REASON_TYPE.getName(),
            TblsProcedure.ProcedureActions.AUDIT_LIST_EN.getName(), TblsProcedure.ProcedureActions.AUDIT_LIST_ES.getName()};
        Object[][] actionsInfo = Rdbms.getRecordFieldsByFilter(procedureInstanceName, LPPlatform.buildSchemaName(procedureInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS_MASTER_DATA.getTableName(),
                new String[]{TblsProcedure.ProcedureActions.ACTION_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{},fldNames);        
        return new Object[]{fldNames, actionsInfo};
    }
    private void setActionsMasterData(String procedureInstanceName){
        Object[] dbTableExists = Rdbms.dbTableExists(procedureInstanceName, procedureInstanceName + "-procedure", 
                TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString())) {return;}
        JSONArray jArr = new JSONArray();
        Object[] procInstanceActionsListAll=getProcInstanceActionsMasterDataInfo(procedureInstanceName);
        Object[][] procInstanceActionsList=(Object[][])procInstanceActionsListAll[1];
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procInstanceActionsList[0][0].toString())) {
            return;
        }        
        for (Object[] curAction : procInstanceActionsList) {
            this.actionsMasterData.add(new ActionInfo(curAction[0].toString(), curAction[1].toString(), Boolean.valueOf(curAction[2].toString()), Boolean.valueOf(curAction[3].toString()), 
                Boolean.valueOf(curAction[4].toString()), Boolean.valueOf(curAction[5].toString()),
                curAction[6].toString(), curAction[7].toString(), curAction[8].toString()));
        }
        this.actionsListMasterDataStr=Arrays.toString(LPArray.getColumnFromArray2D(procInstanceActionsList, 0));
    }    
    
            
    public BusinessRules(String procedureInstanceName, Integer scriptId) {
        this.procedureInstanceName = procedureInstanceName;
        this.actions = new ArrayList<>();
        this.actionsMasterData = new ArrayList<>();
        this.procedure = new ArrayList<>();
        this.data = new ArrayList<>();
        this.config = new ArrayList<>();
//        if(1==1) return;
        Object[][] testingBusRulsInfo = null;

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, false);
        if (procReqInstance != null) {
            String procedureInstance = procReqInstance.getProcedureInstance();
            if (procedureInstance != null) {
                procedureInstanceName = procedureInstance;
            }
        }
        if (scriptId != null && scriptId > 0) {
            testingBusRulsInfo = Rdbms.getRecordFieldsByFilter(procedureInstanceName, LPPlatform.buildSchemaName(procedureInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT_BUS_RULES.getTableName(),
                    new String[]{TblsTesting.ScriptBusinessRules.SCRIPT_ID.getName(), TblsTesting.ScriptBusinessRules.ACTIVE.getName()}, new Object[]{scriptId, true},
                    new String[]{TblsTesting.ScriptBusinessRules.REPOSITORY.getName(), TblsTesting.ScriptBusinessRules.RULE_NAME.getName(), TblsTesting.ScriptBusinessRules.RULE_VALUE.getName()});
        } else if (procedureInstanceName != null && procedureInstanceName.length() > 0) {
            testingBusRulsInfo = Rdbms.getRecordFieldsByFilter(procedureInstanceName, LPPlatform.buildSchemaName(procedureInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(),
                    new String[]{TblsProcedure.ProcedureBusinessRules.DISABLED.getName()}, new Object[]{false},
                    new String[]{TblsProcedure.ProcedureBusinessRules.AREA.getName(), TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName(), TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()});
        } else {
            testingBusRulsInfo = new Object[][]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "BusinessRulesNotCompatibleYetForPlatformRules", null)};
        }
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(testingBusRulsInfo[0][0].toString()))) {
            for (Object[] curObj : testingBusRulsInfo) {
                if ("PROCEDURE".equalsIgnoreCase(curObj[0].toString())) {
                    this.procedure.add(new RuleInfo(curObj[1].toString(), curObj[2].toString()));
                }
                if ("DATA".equalsIgnoreCase(curObj[0].toString())) {
                    this.data.add(new RuleInfo(curObj[1].toString(), curObj[2].toString()));
                }
                if ("CONFIG".equalsIgnoreCase(curObj[0].toString())) {
                    this.config.add(new RuleInfo(curObj[1].toString(), curObj[2].toString()));
                }
            }
        }
        this.totalBusinessRules = this.procedure.size() + this.config.size() + this.data.size();
        setActions(procedureInstanceName);
        setActionsMasterData(procedureInstanceName);
    }

    public Integer getTotalBusinessRules() {
        return this.totalBusinessRules;
    }

    public List<RuleInfo> getProcedureBusinessRules() {
        return Collections.unmodifiableList(this.procedure);
    }

    public String getActionsList(){
        return actionsListStr;
    }
    public ActionInfo getActionDefinition(String actionName) {
        if (this.actions != null) {
            for (ActionInfo curElement : this.actions) {
                if (actionName.equalsIgnoreCase(curElement.getActionName())) 
                {
                    return curElement;
                }
            }            
        }
        return null;
    }  
    public String getActionsMasterDataList(){
        return actionsListMasterDataStr;
    }
    public ActionInfo getActionDefinitionMasterData(String actionName) {
        if (this.actionsMasterData != null) {
            for (ActionInfo curElement : this.actionsMasterData) {
                if (actionName.equalsIgnoreCase(curElement.getActionName())) 
                {
                    return curElement;
                }
            }            
        }
        return null;
    }  

    public String getProcedureBusinessRule(String ruleName) {
        if (this.procedure != null) {
            for (RuleInfo curElement : this.procedure) {
                if (ruleName.equalsIgnoreCase(curElement.getRuleName())) 
                {
                    return curElement.getRuleValue();
                }
            }
        }
        return "";
    }

    public List<RuleInfo> getConfigBusinessRules() {
        return Collections.unmodifiableList(this.config);
    }

    public String getConfigBusinessRule(String ruleName) {
        if (this.config != null) {
            for (RuleInfo curElement : this.config) {
                if (ruleName.equalsIgnoreCase(curElement.getRuleName())) //                LPPlatform.saveParameterPropertyInDbErrorLog("", this.procedureInstanceName+"-"+"config", new Object[]{}, ruleName);                
                {
                    return curElement.getRuleValue();
                }
            }
        }
        return "";
    }

    public List<RuleInfo> getDataBusinessRules() {
        return Collections.unmodifiableList(this.data);
    }

    public String getDataBusinessRule(String ruleName) {
        if (this.data != null) {
            for (RuleInfo curElement : this.data) {
                if (ruleName.equalsIgnoreCase(curElement.getRuleName())) //                LPPlatform.saveParameterPropertyInDbErrorLog("", this.procedureInstanceName+"-"+"data", new Object[]{}, ruleName);                
                {
                    return curElement.getRuleValue();
                }
            }
        }
        return "";
    }

    public static Object[][] sessionBusinessRulesList() {
        return sessionBusinessRulesList(null, null);
    }

    public static Object[][] sessionBusinessRulesList(BusinessRules busRulesProcInstance, BusinessRules busRulesTesting) {
        ProcedureRequestSession procReqInstance = null;
        BusinessRules bRProcInstance = null;
        BusinessRules bRTesting = null;
        if (busRulesProcInstance == null) {
            procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, false);
            bRProcInstance = procReqInstance.getBusinessRulesProcInstance();
            bRTesting = procReqInstance.getBusinessRulesTesting();
        } else {
            bRProcInstance = busRulesProcInstance;
            bRTesting = busRulesTesting;
        }
        Object[] ruleNameArr = new Object[]{};
        if (bRTesting != null) {
            for (int i = 0; i < bRTesting.config.size(); i++) {
                String brValue = bRProcInstance.getProcedureBusinessRule(bRTesting.config.get(i).getRuleName());
                if (brValue.length() > 0) {
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, startsMark + bRTesting.config.get(i).getRuleName());
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRTesting.config.get(i).getRuleValue() + " (" + brValue + ")");
                } else {
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRTesting.config.get(i).getRuleName());
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRTesting.config.get(i).getRuleValue());
                }
            }
            for (int i = 0; i < bRTesting.data.size(); i++) {
                String brValue = bRProcInstance.getProcedureBusinessRule(bRTesting.data.get(i).getRuleName());
                if (brValue.length() > 0) {
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, startsMark + bRTesting.data.get(i).getRuleName());
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRTesting.data.get(i).getRuleValue() + " (" + brValue + ")");
                } else {
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRTesting.data.get(i).getRuleName());
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRTesting.data.get(i).getRuleValue());
                }
            }
            for (int i = 0; i < bRTesting.procedure.size(); i++) {
                String brValue = "";
                if (bRProcInstance != null) {
                    brValue = bRProcInstance.getProcedureBusinessRule(bRTesting.procedure.get(i).getRuleName());
                }
                if (brValue.length() > 0) {
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, startsMark + bRTesting.procedure.get(i).getRuleName());
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRTesting.procedure.get(i).getRuleValue() + " (" + brValue + ")");
                } else {
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRTesting.procedure.get(i).getRuleName());
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRTesting.procedure.get(i).getRuleValue());
                }
            }
        }
        if (bRProcInstance != null) {
            for (int i = 0; i < bRProcInstance.config.size(); i++) {
                String brValue = "";
                if (bRTesting != null) {
                    brValue = bRTesting.getConfigBusinessRule(bRProcInstance.config.get(i).getRuleName());
                }
                if (brValue.length() == 0) {
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRProcInstance.config.get(i).getRuleName());
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRProcInstance.config.get(i).getRuleValue());
                }
            }
            for (int i = 0; i < bRProcInstance.data.size(); i++) {
                String brValue = "";
                if (bRTesting != null) {
                    brValue = bRTesting.getProcedureBusinessRule(bRProcInstance.data.get(i).getRuleName());
                }
                if (brValue.length() == 0) {
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRProcInstance.data.get(i).getRuleName());
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRProcInstance.data.get(i).getRuleValue());
                }
            }
            for (int i = 0; i < bRProcInstance.procedure.size(); i++) {
                String brValue = "";
                if (bRTesting != null) {
                    brValue = bRTesting.getProcedureBusinessRule(bRProcInstance.procedure.get(i).getRuleName());
                }
                if (brValue.length() == 0) {
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRProcInstance.procedure.get(i).getRuleName());
                    ruleNameArr = LPArray.addValueToArray1D(ruleNameArr, bRProcInstance.procedure.get(i).getRuleValue());
                }
            }
        }
        return LPArray.array1dTo2d(ruleNameArr, 2);
    }
}
