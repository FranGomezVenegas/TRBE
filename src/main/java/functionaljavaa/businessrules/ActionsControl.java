/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package functionaljavaa.businessrules;

import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsProcedure;
import databases.features.Token;
import functionaljavaa.testingscripts.TestingBusinessRulesVisited;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ActionsControl {

    public static JSONObject auditSignMode(String procInstanceName) {
        JSONObject jObj = new JSONObject();
        Object[][] rulesValues = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), new String[]{TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause()}, new Object[]{"sampleAuditChildRevisionRequired|sampleAuditRevisionMode"}, new String[]{TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName(), TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(rulesValues[0][0].toString())) {
            return jObj;
        }
        for (Object[] curRule : rulesValues) {
            jObj.put(curRule[0], curRule[1]);
        }
        return jObj;
    }

    public static JSONArray procActionsWithJustifReason(String procInstanceName) {
        Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, procInstanceName + "-procedure", TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString())) {
            return procActionsWithJustifReasonInBusRules(procInstanceName);
        }
        return procActionsWithJustifReasonInTable(procInstanceName);
    }
    private static JSONArray procActionsWithJustifReasonInBusRules(String procInstanceName) {
        JSONArray jArr = new JSONArray();
        Object[][] ruleValue = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), new String[]{TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName()}, new Object[]{LPPlatform.LpPlatformBusinessRules.AUDIT_JUSTIF_REASON_REQUIRED.getTagName()}, new String[]{TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ruleValue[0][0].toString())) {
            return jArr;
        }
        String[] justifReasonRequired = LPNulls.replaceNull(ruleValue[0][0]).toString().split("\\|");
        for (String curAction : justifReasonRequired) {
            JSONObject jActionObj = new JSONObject();
            jActionObj.put(curAction, actionDetailInBusRules(procInstanceName, curAction));
            jArr.add(jActionObj);
            jArr.add(curAction);
        }
        return jArr;
    }
    private static JSONArray procActionsWithJustifReasonInTable(String procInstanceName) {
        JSONArray jArr = new JSONArray();
        Object[][] ruleValue = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName(), new String[]{TblsProcedure.ProcedureActions.JUSTIF_REASON_REQUIRED.getName()}, new Object[]{true}, new String[]{TblsProcedure.ProcedureActions.ACTION_NAME.getName(), TblsProcedure.ProcedureActions.AUDIT_REASON_TYPE.getName(), TblsProcedure.ProcedureActions.AUDIT_LIST_EN.getName(), TblsProcedure.ProcedureActions.AUDIT_LIST_ES.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ruleValue[0][0].toString())) {
            return jArr;
        }
        for (Object[] curAction : ruleValue) {
            JSONObject jActionObj = new JSONObject();
            jActionObj.put(curAction[0], actionDetailInTable(curAction[0], curAction[1], curAction[2], curAction[3]));
            jArr.add(jActionObj);
            jArr.add(curAction[0]);
        }
        return jArr;
    }

    public static JSONArray procActionsWithActionConfirm(String procInstanceName) {
        Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, procInstanceName + "-procedure", TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString())) {
            return procActionsWithActionConfirmInBusRules(procInstanceName);
        }
        return procActionsWithActionConfirmInTable(procInstanceName);
    }
    private static JSONArray procActionsWithActionConfirmInBusRules(String procInstanceName) {
        JSONArray jArr = new JSONArray();
        Object[][] ruleValue = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), new String[]{TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName()}, new Object[]{LPPlatform.LpPlatformBusinessRules.ACTIONCONFIRM_REQUIRED.getTagName()}, new String[]{TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ruleValue[0][0].toString())) {
            return jArr;
        }
        String[] justifReasonRequired = LPNulls.replaceNull(ruleValue[0][0]).toString().split("\\|");
        jArr.addAll(Arrays.asList(justifReasonRequired));
        return jArr;
    }
    private static JSONArray procActionsWithActionConfirmInTable(String procInstanceName) {
        JSONArray jArr = new JSONArray();
        Object[][] ruleValue = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName(), new String[]{TblsProcedure.ProcedureActions.ARE_YOU_SURE_REQUIRED.getName()}, new Object[]{true}, new String[]{TblsProcedure.ProcedureActions.ACTION_NAME.getName(), TblsProcedure.ProcedureActions.AUDIT_REASON_TYPE.getName(), TblsProcedure.ProcedureActions.AUDIT_LIST_EN.getName(), TblsProcedure.ProcedureActions.AUDIT_LIST_ES.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ruleValue[0][0].toString())) {
            return jArr;
        }
        for (Object[] curAction : ruleValue) {
            JSONObject jActionObj = new JSONObject();
            jActionObj.put(curAction[0], actionDetailInTable(curAction[0], curAction[1], curAction[2], curAction[3]));
            jArr.add(jActionObj);
            jArr.add(curAction[0]);
        }
        return jArr;
    }

    public static JSONArray procActionsWithESign(String procInstanceName) {
        Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, procInstanceName + "-procedure", TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString())) {
            return procActionsWithESignInBusRules(procInstanceName);
        }
        return procActionsWithESignInTable(procInstanceName);
    }
    private static JSONArray procActionsWithESignInBusRules(String procInstanceName) {
        JSONArray jArr = new JSONArray();
        Object[][] ruleValue = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), new String[]{TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName()}, new Object[]{LPPlatform.LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName()}, new String[]{TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ruleValue[0][0].toString())) {
            return jArr;
        }
        String[] eSignRequired = LPNulls.replaceNull(ruleValue[0][0]).toString().split("\\|");
        for (String curAction : eSignRequired) {
            JSONObject jActionObj = new JSONObject();
            jActionObj.put(curAction, actionDetailInBusRules(procInstanceName, curAction));
            jArr.add(jActionObj);
            jArr.add(curAction);
        }
        return jArr;
    }
    private static JSONArray procActionsWithESignInTable(String procInstanceName) {
        JSONArray jArr = new JSONArray();
        Object[][] ruleValue = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName(), new String[]{TblsProcedure.ProcedureActions.ESIGN_REQUIRED.getName()}, new Object[]{true}, new String[]{TblsProcedure.ProcedureActions.ACTION_NAME.getName(), TblsProcedure.ProcedureActions.AUDIT_REASON_TYPE.getName(), TblsProcedure.ProcedureActions.AUDIT_LIST_EN.getName(), TblsProcedure.ProcedureActions.AUDIT_LIST_ES.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ruleValue[0][0].toString())) {
            return jArr;
        }
        for (Object[] curAction : ruleValue) {
            JSONObject jActionObj = new JSONObject();
            jActionObj.put(curAction[0], actionDetailInTable(curAction[0], curAction[1], curAction[2], curAction[3]));
            jArr.add(jActionObj);
            jArr.add(curAction[0]);
        }
        return jArr;
    }

    public static JSONArray procActionsWithConfirmUser(String procInstanceName) {
        Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, procInstanceName + "-procedure", TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString())) {
            return procActionsWithConfirmUserInBusRules(procInstanceName);
        }
        return procActionsWithConfirmUserInTable(procInstanceName);
    }
    private static JSONArray procActionsWithConfirmUserInBusRules(String procInstanceName) {
        JSONArray jArr = new JSONArray();
        Object[][] ruleValue = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), new String[]{TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName()}, new Object[]{LPPlatform.LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getTagName()}, new String[]{TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ruleValue[0][0].toString())) {
            return jArr;
        }
        String[] verifyUserRequired = LPNulls.replaceNull(ruleValue[0][0]).toString().split("\\|");
        for (String curAction : verifyUserRequired) {
            JSONObject jActionObj = new JSONObject();
            jActionObj.put(curAction, actionDetailInBusRules(procInstanceName, curAction));
            jArr.add(jActionObj);
            jArr.add(curAction);
        }
        return jArr;
    }
    private static JSONArray procActionsWithConfirmUserInTable(String procInstanceName) {
        JSONArray jArr = new JSONArray();
        Object[][] ruleValue = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName(), new String[]{TblsProcedure.ProcedureActions.USER_CREDENTIAL_REQUIRED.getName()}, new Object[]{true}, new String[]{TblsProcedure.ProcedureActions.ACTION_NAME.getName(), TblsProcedure.ProcedureActions.AUDIT_REASON_TYPE.getName(), TblsProcedure.ProcedureActions.AUDIT_LIST_EN.getName(), TblsProcedure.ProcedureActions.AUDIT_LIST_ES.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ruleValue[0][0].toString())) {
            return jArr;
        }
        for (Object[] curAction : ruleValue) {
            JSONObject jActionObj = new JSONObject();
            jActionObj.put(curAction[0], actionDetailInTable(curAction[0], curAction[1], curAction[2], curAction[3]));
            jArr.add(jActionObj);
            jArr.add(curAction[0]);
        }
        return jArr;
    }
 
    private static JSONObject actionDetailInBusRules(String procInstanceName, String actionName) {
        JSONObject jObj = new JSONObject();
        actionName = actionName + "AuditReasonPhrase";
        Object[][] ruleValue = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), new String[]{TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName()}, new Object[]{actionName}, new String[]{TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()});
        String[] actionDetail = LPNulls.replaceNull(ruleValue[0][0]).toString().split("\\|");
        jObj.put("name", actionName);
        jObj.put("type", actionDetail[0]);
        if (actionDetail[0].toUpperCase().contains("LIST")) {
            JSONArray jObjListEntries = new JSONArray();
            jObjListEntries.addAll(Arrays.asList(actionDetail));
            jObj.put("list_entries", jObjListEntries);
        }
        return jObj;
    }
    private static JSONObject actionDetailInTable(Object actionName, Object auditReasonType, Object listEn, Object listEs) {
        JSONObject jObj = new JSONObject();
        String[] listEnArr = LPNulls.replaceNull(listEn).toString().split("\\|");
        String[] listEsArr = LPNulls.replaceNull(listEs).toString().split("\\|");
        jObj.put("name", actionName);
        jObj.put("type", auditReasonType);
        if (LPNulls.replaceNull(auditReasonType).toString().toUpperCase().contains("LIST")) {
            JSONArray jObjListEntries = new JSONArray();
            jObjListEntries.addAll(Arrays.asList(listEnArr));
            jObj.put("list_entries", jObjListEntries);
            jObjListEntries = new JSONArray();
            jObjListEntries.addAll(Arrays.asList(listEnArr));
            jObj.put("list_entries_en", jObjListEntries);
            jObjListEntries = new JSONArray();
            jObjListEntries.addAll(Arrays.asList(listEsArr));
            jObj.put("list_entries_es", jObjListEntries);
        }
        return jObj;
    }

    public static InternalMessage isTheProcActionEnabled(Token tokn, String procInstanceName, String actionNm, BusinessRules procBusinessRules) {
        return isTheProcActionEnabled(tokn, procInstanceName, actionNm, procBusinessRules, false);
    }
    public static InternalMessage isTheProcActionEnabled(Token tokn, String procInstanceName, String actionNm, BusinessRules procBusinessRules, Boolean isProcManagement) {
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(tokn.getUserName())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.INVALID_TOKEN, null);
        }
        InternalMessage actionEnabled = procActionEnabled(procInstanceName, tokn, actionNm, procBusinessRules, isProcManagement);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled.getDiagnostic())) {
            return actionEnabled;
        }
        if (Boolean.FALSE.equals(isProcManagement)) {
            actionEnabled = ActionsControl.procUserRoleActionEnabled(procInstanceName, tokn.getUserRole(), actionNm, procBusinessRules);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled.getDiagnostic())) {
                return actionEnabled;
            }
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
    }
    public static InternalMessage procUserRoleActionEnabled(String procInstanceName, String userRole, String actionName, BusinessRules procBusinessRules) {
        Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, procInstanceName + "-procedure", TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName());
        //return (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString()))? 
        //    procUserRoleActionEnabledInBusRules(procInstanceName, userRole, actionName, procBusinessRules):
        return procUserRoleActionEnabledInTable(procInstanceName, userRole, actionName, procBusinessRules);
    }
    private static Object[] xprocUserRoleActionEnabledInBusRules(String procInstanceName, String userRole, String actionName, BusinessRules procBusinessRules) {
        String[] procedureActionsUserRoles = procBusinessRules.getProcedureBusinessRule(LPPlatform.LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName() + actionName).split("\\|");
        if (Boolean.TRUE.equals(ProcedureRequestSession.getInstanceForQueries(null, null, null).getIsForTesting())) {
            TestingBusinessRulesVisited testingBusinessRulesVisitedObj = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingBusinessRulesVisitedObj();
            if (testingBusinessRulesVisitedObj != null) {
                testingBusinessRulesVisitedObj.addObject(procInstanceName, "procedure", "TestingRegresssionUAT", LPPlatform.LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName() + actionName, Arrays.toString(procedureActionsUserRoles));
            }
        }
        if (LPArray.valueInArray(procedureActionsUserRoles, "ALL")) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.USRROLACTIONENABLED_ENABLED_BYALL, new Object[]{procInstanceName});
        }
        if (procedureActionsUserRoles.length == 1 && "".equals(procedureActionsUserRoles[0])) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.USRROLACTIONENABLED_MISSEDPARAMETER, new Object[]{actionName, procInstanceName});
        } else if (Boolean.FALSE.equals(LPArray.valueInArray(procedureActionsUserRoles, userRole))) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.USRROLACTIONENABLED_ROLENOTINCLUDED, new Object[]{procInstanceName, actionName, userRole, Arrays.toString(procedureActionsUserRoles)});
        } else {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.USRROLACTIONENABLED_ENABLED, new Object[]{procInstanceName, actionName});
        }
    }
    private static InternalMessage procUserRoleActionEnabledInTable(String procInstanceName, String userRole, String actionName, BusinessRules procBusinessRules) {
        ActionInfo actionDefinition=null;
        actionDefinition = procBusinessRules.getActionDefinition(actionName);
        if (actionDefinition==null)
            actionDefinition = procBusinessRules.getActionDefinitionMasterData(actionName);

        
        String[] procedureActionsUserRoles = actionDefinition.getActionRoles().split("\\|");
        if (Boolean.TRUE.equals(ProcedureRequestSession.getInstanceForQueries(null, null, null).getIsForTesting())) {
            TestingBusinessRulesVisited testingBusinessRulesVisitedObj = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingBusinessRulesVisitedObj();
            if (testingBusinessRulesVisitedObj != null) {
                testingBusinessRulesVisitedObj.addObject(procInstanceName, "procedure", "TestingRegresssionUAT", LPPlatform.LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName() + actionName, Arrays.toString(procedureActionsUserRoles));
            }
        }
        if (LPArray.valueInArray(procedureActionsUserRoles, "ALL")) {
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.USRROLACTIONENABLED_ENABLED_BYALL, new Object[]{procInstanceName});
        }
        if (procedureActionsUserRoles.length == 1 && "".equals(procedureActionsUserRoles[0])) {
            return new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.USRROLACTIONENABLED_MISSEDPARAMETER, new Object[]{actionName, procInstanceName});
        } else if (Boolean.FALSE.equals(LPArray.valueInArray(procedureActionsUserRoles, userRole))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.USRROLACTIONENABLED_ROLENOTINCLUDED, new Object[]{procInstanceName, actionName, userRole, Arrays.toString(procedureActionsUserRoles)});
        } else {
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.USRROLACTIONENABLED_ENABLED, new Object[]{procInstanceName, actionName});
        }
    }
    
    public static InternalMessage procActionEnabled(String procInstanceName, Token token, String actionName, BusinessRules procBusinessRules, Boolean isProcManagement) {
        Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, procInstanceName + "-procedure", TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName());
        return    procActionEnabledInTable(procInstanceName, token, actionName, procBusinessRules, isProcManagement);
    }
    private static InternalMessage procActionEnabledInTable(String procInstanceName, Token token, String actionName, BusinessRules procBusinessRules, Boolean isProcManagement) {
        if (Boolean.FALSE.equals(isProcManagement)) {
            String userProceduresList = token.getUserProcedures();
            userProceduresList = userProceduresList.replace("[", "");
            userProceduresList = userProceduresList.replace("]", "");
            if (Boolean.FALSE.equals(LPArray.valueInArray(userProceduresList.split(", "), procInstanceName))) {
                return new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.USER_NOTASSIGNED_TOPROCEDURE, new String[]{token.getUserName(), procInstanceName, userProceduresList});
            }
        }
        actionName = actionName.toUpperCase();
        
        ActionInfo actionDefinition = procBusinessRules.getActionDefinition(actionName);
        if (actionDefinition==null) {            
            actionDefinition = procBusinessRules.getActionDefinitionMasterData(actionName);
        }
        if (actionDefinition==null) {            

            procBusinessRules.getActionsList();
            return new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.ACTION_NOTFOUND, new String[]{procInstanceName, actionName});
        }
        if (Boolean.TRUE.equals(isProcManagement)) {
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.USRROLACTIONENABLED_ENABLED, new String[]{procInstanceName, actionName});
        }
        String procedureActionRoles = actionDefinition.getActionRoles();
        
        if (Boolean.TRUE.equals(ProcedureRequestSession.getInstanceForQueries(null, null, null).getIsForTesting())) {
            TestingBusinessRulesVisited testingBusinessRulesVisitedObj = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingBusinessRulesVisitedObj();
            if (testingBusinessRulesVisitedObj != null) {
                testingBusinessRulesVisitedObj.addObject(procInstanceName, "procedure", "TestingRegresssionUAT", LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName(), procedureActionRoles);
            }
        }
        if (procedureActionRoles.length() == 0) {
            return new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.USRROLACTIONENABLED_DENIED_RULESNOTFOUND, new String[]{procInstanceName, procedureActionRoles});
        }
        if ("ALL".equalsIgnoreCase(procedureActionRoles)) {
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.USRROLACTIONENABLED_ENABLED_BYALL, new String[]{procInstanceName, actionName});
        }
        if (Boolean.FALSE.equals(LPArray.valueInArray(procedureActionRoles.split("\\|"), token.getUserRole()))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.USRROLACTIONENABLED_DENIED, new String[]{actionName, procInstanceName, procedureActionRoles});
        } else {
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.USRROLACTIONENABLED_ENABLED, new String[]{procInstanceName, actionName});
        }
    }

    public static Object[] procActionRequiresJustificationPhrase(String procInstanceName, String actionName, BusinessRules procBusinessRules) {
        Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, procInstanceName + "-procedure", TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName());
        return    procActionRequiresJustificationPhraseInTable(procInstanceName, actionName, procBusinessRules);                
    }
    private static Object[] procActionRequiresJustificationPhraseInTable(String procInstanceName, String actionName, BusinessRules procBusinessRules) {
        actionName = actionName.toUpperCase();
        ActionInfo actionDefinition = procBusinessRules.getActionDefinition(actionName);
        if (actionDefinition==null) 
            actionDefinition = procBusinessRules.getActionDefinitionMasterData(actionName);
        
        if (actionDefinition==null) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.JUSTIFPHRASEREQUIRED_DENIED_RULENOTFOUND, new Object[]{procInstanceName, actionDefinition.getActionName()});
        } else if (Boolean.FALSE.equals(actionDefinition.getJustifReasonReqd())) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.JUSTIFPHRASEREQUIRED_DENIED, new Object[]{actionName, procInstanceName, actionDefinition.getActionName()});
        } else {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE + auditReasonType(procInstanceName, actionName, procBusinessRules), LPPlatform.LpPlatformSuccess.JUSTIFPHRASEREQUIRED_ENABLED, new Object[]{procInstanceName, actionName});
        }
    }

    public static Object[] procActionRequiresEsignConfirmation(String procInstanceName, String actionName, BusinessRules procBusinessRules) {
        Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, procInstanceName + "-procedure", TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName());
        return  procActionRequiresEsignConfirmationInTable(procInstanceName, actionName, procBusinessRules);                        
    }
    private static Object[] procActionRequiresEsignConfirmationInTable(String procInstanceName, String actionName, BusinessRules procBusinessRules) {
        actionName = actionName.toUpperCase();
        ActionInfo actionDefinition = procBusinessRules.getActionDefinition(actionName);
        if (actionDefinition==null) 
            actionDefinition = procBusinessRules.getActionDefinitionMasterData(actionName);

        if (actionDefinition==null) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.ESIGNREQUIRED_DENIED_RULENOTFOUND, new Object[]{procInstanceName, actionDefinition.getActionName()});
        } else if (Boolean.FALSE.equals(actionDefinition.getEsignReqd())) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.ESIGNREQUIRED_DENIED, new Object[]{actionName, procInstanceName, actionDefinition.getActionName()});
        } else {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE + auditReasonType(procInstanceName, actionName, procBusinessRules), LPPlatform.LpPlatformSuccess.ESIGNREQUIRED_ENABLED, new Object[]{procInstanceName, actionName});
        }
    }

    public static Object[] procActionRequiresUserConfirmation(String procInstanceName, String actionName, BusinessRules procBusinessRules) {
        Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, procInstanceName + "-procedure", TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName());
        return    procActionRequiresUserConfirmationInTable(procInstanceName, actionName, procBusinessRules);
        
    }
    public static Object[] procActionRequiresUserConfirmationInTable(String procInstanceName, String actionName, BusinessRules procBusinessRules) {
        actionName = actionName.toUpperCase();
        ActionInfo actionDefinition = procBusinessRules.getActionDefinition(actionName);
        if (actionDefinition==null) 
            actionDefinition = procBusinessRules.getActionDefinitionMasterData(actionName);
        if (actionDefinition==null) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.VERIFYUSERREQUIRED_DENIED_RULENOTFOUND, new Object[]{procInstanceName, actionDefinition.getActionName()});
        } else if (Boolean.FALSE.equals(actionDefinition.getUserConfirmReqd())) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.VERIFYUSERREQUIRED_DENIED, new Object[]{actionName, procInstanceName, actionDefinition.getActionName()});
        } else {
            String diagnStr = LPPlatform.LAB_TRUE;
            diagnStr = diagnStr + LPNulls.replaceNull(auditReasonType(procInstanceName, actionName, procBusinessRules));
            return ApiMessageReturn.trapMessage(diagnStr, LPPlatform.LpPlatformSuccess.VERIFYUSERREQUIRED_ENABLED, new Object[]{procInstanceName, actionName});
        }
    }

    private static String auditReasonType(String procInstanceName, String actionName) {
        return auditReasonType(procInstanceName, actionName, null);
    }
    private static String auditReasonType(String procInstanceName, String actionName, BusinessRules procBusinessRules) {
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        BusinessRules businessRulesProcInstance = procReqInstance.getBusinessRulesProcInstance();
        String auditReasonType = null;
        if (businessRulesProcInstance != null) {
            auditReasonType = businessRulesProcInstance.getProcedureBusinessRule(actionName + LPPlatform.LpPlatformBusinessRules.AUDITREASON_PHRASE.getTagName());
        } else {
            auditReasonType = procBusinessRules.getProcedureBusinessRule(actionName + LPPlatform.LpPlatformBusinessRules.AUDITREASON_PHRASE.getTagName());
        }
        if (auditReasonType.length() == 0) {
            return "TEXT";
        }
        if (auditReasonType.length() > 0 && auditReasonType.equalsIgnoreCase("DISABLE")) {
            return "";
        }
        if (auditReasonType.length() > 0 && auditReasonType.equalsIgnoreCase("NO")) {
            return "";
        }
        return auditReasonType;
    }
    
}
