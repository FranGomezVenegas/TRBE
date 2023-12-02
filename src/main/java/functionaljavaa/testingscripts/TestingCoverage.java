/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.labplanet.servicios.ResponseError;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsProcedure;
import databases.TblsTesting;
import functionaljavaa.businessrules.BusinessRules;
import static functionaljavaa.businessrules.BusinessRules.getProcInstanceActionsInfo;
import functionaljavaa.businessrules.RuleInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPArray;
import static lbplanet.utilities.LPArray.convertStringedPipedNumbersInArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.definition.definition.ReqProcedureEnums.ProcedureDefinitionAPIActionsEndpoints;
import trazit.procedureinstance.definition.definition.TblsReqs;

/**
 *
 * @author User
 */
public final class TestingCoverage {

    private String moduleName;
    private Integer moduleVersion;
    private final String procInstanceName;
    private final Integer coverageId;
    private final Object[] scriptIds;

    private Integer totalMessages;
    private final JSONArray msgCodeVisited;
    private JSONArray msgCodeVisitedObj;

    private BigDecimal msgCodeCovPerc;
    private final String[] coverageMsgCodeExcludeList;
    private JSONObject msgCodeCoverageDetail;

    private Integer endpointsVisitedTotal;
    private Integer endpointsMissingTotal;
    private JSONObject endpointsCoverageDetail;
    private String[] procActionsArr;
    private String[] coverageEndpointsExcludeList;
    private BigDecimal endpointsCovPerc;

    private Integer totalBusRules;
    private final BusinessRules procBusRules;
    private BusinessRules scriptsBusRules;
    private String[] coverageBusRulesExcludeList;
    private Integer busRuleVisitedTotal;
    private Integer busRuleVisitedConfigRules;
    private Integer busRuleVisitedDataRules;
    private Integer busRuleVisitedProcedureRules;
    private Integer busRuleVisitedMissingInProcTotal;
    private Integer busRuleVisitedMissingInProcConfig;
    private Integer busRuleVisitedMissingInProcData;
    private Integer busRuleVisitedMissingInProcProcedure;
    private String[] busRuleExcludedByExcludeEndpoint;
    private BusinessRules busRuleVisitedMissingInProcRules;
    private JSONObject busRuleCoverageDetail;
    private BigDecimal busRuleCovPerc;
    private final Object[][] scriptsInfoArr;
    private final String[] scriptsFldNameArr;

    public TestingCoverage(String procInstanceName, Integer coverageId) {
        this.msgCodeVisited = new JSONArray();
        this.procInstanceName = procInstanceName;

        Object[][] moduleInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(),
                new String[]{TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName()},
                new Object[]{procInstanceName}, new String[]{TblsProcedure.ProcedureInfo.MODULE_NAME.getName(), TblsProcedure.ProcedureInfo.VERSION.getName()});
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(moduleInfo[0][0].toString()))){
            this.moduleName=moduleInfo[0][0].toString();
            this.moduleVersion=Integer.valueOf(moduleInfo[0][1].toString());
        }
        
        this.coverageId = coverageId;
        this.busRuleExcludedByExcludeEndpoint = new String[]{};
        String[] covFldNameArr = getAllFieldNames(TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableFields());
        Object[][] coverageInfoArr = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableName(),
                new String[]{TblsTesting.ScriptsCoverage.COVERAGE_ID.getName()},
                new Object[]{coverageId}, covFldNameArr);
        //if (LPPlatform.LAB_FALSE.equalsIgnoreCase(coverageInfo[0][0].toString())){return;}

        String scrId = LPNulls.replaceNull(coverageInfoArr[0][LPArray.valuePosicInArray(getAllFieldNames(TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableFields()), TblsTesting.ScriptsCoverage.SCRIPT_IDS_LIST.getName())]).toString();
        this.scriptIds = convertStringedPipedNumbersInArray(scrId);
        scrId = scrId.replace("\\|", "\\|INTEGER*");
        scrId = "INTEGER*".concat(scrId);
        BusinessRules bR = new BusinessRules(procInstanceName, 0);
        this.coverageEndpointsExcludeList = LPNulls.replaceNull(coverageInfoArr[0][LPArray.valuePosicInArray(getAllFieldNames(TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableFields()), TblsTesting.ScriptsCoverage.ENDPOINTS_EXCLUDE_LIST.getName())]).toString().split("\\|");
        if (this.coverageEndpointsExcludeList != null && this.coverageEndpointsExcludeList[0].length() == 0) {
            this.coverageEndpointsExcludeList = new String[]{};
        }else{
            this.coverageEndpointsExcludeList=LPArray.getUniquesArray(this.coverageEndpointsExcludeList);
        }
        this.coverageBusRulesExcludeList = LPNulls.replaceNull(coverageInfoArr[0][LPArray.valuePosicInArray(getAllFieldNames(TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableFields()), TblsTesting.ScriptsCoverage.BUS_RULES_EXCLUDE_LIST.getName())]).toString().split("\\|");
        if (this.coverageBusRulesExcludeList != null && this.coverageBusRulesExcludeList[0].length() == 0) {
            this.coverageBusRulesExcludeList = new String[]{};
        }else{
            this.coverageBusRulesExcludeList=LPArray.getUniquesArray(this.coverageBusRulesExcludeList);
        }

        this.coverageMsgCodeExcludeList = LPNulls.replaceNull(coverageInfoArr[0][LPArray.valuePosicInArray(getAllFieldNames(TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableFields()), TblsTesting.ScriptsCoverage.MSG_CODE_EXCLUDE_LIST.getName())]).toString().split("\\|");

        this.procBusRules = bR;
        initializeCounters();
        this.scriptsFldNameArr = new String[]{TblsTesting.Script.SCRIPT_ID.getName(), TblsTesting.Script.BUSINESS_RULES_VISITED.getName(), TblsTesting.Script.MESSAGES_VISITED.getName(),
            TblsTesting.Script.DATE_EXECUTION.getName(), TblsTesting.Script.PURPOSE.getName(), TblsTesting.Script.RUN_SUMMARY.getName()};
        this.scriptsInfoArr = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT.getTableName(),
                new String[]{TblsTesting.Script.SCRIPT_ID.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause() + "|"}, new Object[]{scrId},
                this.scriptsFldNameArr);
        List<String> calcProcedureBusRules = new ArrayList<>();
        List<String> calcDataBusRules = new ArrayList<>();
        List<String> calcConfigBusRules = new ArrayList<>();
        List<String> calcEndpoints = new ArrayList<>();
//        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptsBusRul[0][0].toString())){
        for (Object[] curScript : this.scriptsInfoArr) {
            if (curScript[1] != null && curScript[1].toString().length() > 0) {
                this.scriptsBusRules = new BusinessRules(procInstanceName, 0, LPJson.convertToJsonArrayStringedObject(curScript[1].toString()));
            } else {
                this.scriptsBusRules = new BusinessRules(procInstanceName, 0, new JsonArray());
            }
            calculateCoverageEndpoints(scrId, calcEndpoints);
            calculateCoverageBusRules(calcProcedureBusRules, calcDataBusRules, calcConfigBusRules);
            calculateCoverageMessageCodes(LPJson.convertToJsonArrayStringedObject(curScript[2].toString()));
        }
//        }
        generateSummaryForBusinessRules();
        generateSummaryForEndpoint();
        generateSummaryForMessageCodes();
        saveCoverage();
    }

    public void calculateCoverageEndpoints(String scrId, List<String> calcProcedureActions) {
        Object[] whereFldValue = new Object[]{scrId, true};
        Object[][] scriptsEndpoints = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT_STEPS.getTableName(),
                new String[]{TblsTesting.ScriptSteps.SCRIPT_ID.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause() + "|", TblsTesting.ScriptSteps.ACTIVE.getName()}, whereFldValue,
                new String[]{TblsTesting.ScriptSteps.SCRIPT_ID.getName(), TblsTesting.ScriptSteps.ACTION_NAME.getName()});
        JSONArray visitedjObj = new JSONArray();
        JSONArray missingjObj = new JSONArray();
        for (Object[] curStep : scriptsEndpoints) {
            String curScriptEndpoint = curStep[1].toString();
            if ((curScriptEndpoint != null && curScriptEndpoint.length() > 0)
                    && (calcProcedureActions == null || calcProcedureActions.isEmpty() || !calcProcedureActions.contains(curScriptEndpoint))) {
                if (curScriptEndpoint != null) {
                    calcProcedureActions.add(curScriptEndpoint);
                }
                if (LPArray.valueInArray(this.procActionsArr, curScriptEndpoint)) {
                    this.endpointsVisitedTotal++;
                    visitedjObj.add(curScriptEndpoint);
                } else {
                    this.endpointsMissingTotal++;
                    missingjObj.add(curScriptEndpoint);
                }
            }
        }
        JSONArray accVisited = (JSONArray) this.endpointsCoverageDetail.get("visited");
        if (accVisited == null) {
            accVisited = visitedjObj;
        } else {
            accVisited.addAll(visitedjObj);
        }
        JSONArray accMissing = (JSONArray) this.endpointsCoverageDetail.get("missing");
        if (accMissing == null) {
            accMissing = missingjObj;
        } else {
            accMissing.addAll(missingjObj);
        }
        this.endpointsCoverageDetail.put("visited", accVisited);
        this.endpointsCoverageDetail.put("missing", accMissing);
    }

    public void calculateCoverageBusRules(List<String> calcProcedure, List<String> calcData, List<String> calcConfig) {
        //this.busRuleCoverageDetail=new JSONObject();
        JSONArray visitedRulesJArr = new JSONArray();
        JSONArray missingRulesJArr = new JSONArray();
        for (int i = 0; i < this.scriptsBusRules.getConfigBusinessRules().size(); i++) {
            String curScriptConfigRule = this.scriptsBusRules.getConfigBusinessRules().get(i).getRuleName();
            if (calcConfig == null || calcConfig.isEmpty() || !calcConfig.contains(curScriptConfigRule)) {
                calcConfig.add(curScriptConfigRule);
                JSONObject jObj = new JSONObject();
                jObj.put("area", "config");
                jObj.put("rule_name", curScriptConfigRule);
                if (this.procBusRules.getConfigBusinessRule(curScriptConfigRule).length() == 0) {
                    this.busRuleVisitedMissingInProcConfig++;
                    this.busRuleVisitedMissingInProcTotal++;
                    missingRulesJArr.add(jObj);
                } else {
                    this.busRuleVisitedTotal++;
                    this.busRuleVisitedConfigRules++;
                    visitedRulesJArr.add(jObj);
                }
            }
        }
        for (int i = 0; i < this.scriptsBusRules.getDataBusinessRules().size(); i++) {
            String curScriptDataRule = this.scriptsBusRules.getDataBusinessRules().get(i).getRuleName();
            if ((calcData == null || calcData.isEmpty() || !calcData.contains(curScriptDataRule))
                    && (curScriptDataRule != null)) {
                calcData.add(curScriptDataRule);
                JSONObject jObj = new JSONObject();
                jObj.put("area", "data");
                jObj.put("rule_name", curScriptDataRule);
                if (this.procBusRules.getDataBusinessRule(curScriptDataRule).length() == 0) {
                    this.busRuleVisitedMissingInProcData++;
                    this.busRuleVisitedMissingInProcTotal++;
                    missingRulesJArr.add(jObj);
                } else {
                    this.busRuleVisitedTotal++;
                    this.busRuleVisitedDataRules++;
                    visitedRulesJArr.add(jObj);
                }
            }
        }
        for (int i = 0; i < this.scriptsBusRules.getProcedureBusinessRules().size(); i++) {
            String curScriptProcedureRule = this.scriptsBusRules.getProcedureBusinessRules().get(i).getRuleName();
/*            if ((calcProcedure == null || calcProcedure.isEmpty() || !calcProcedure.contains(curScriptProcedureRule))
                    && Boolean.FALSE.equals(curScriptProcedureRule.contains("AuditReasonPhrase"))) {
                if (curScriptProcedureRule != null) {
                    calcProcedure.add(curScriptProcedureRule);
                    JSONObject jObj = new JSONObject();
                    jObj.put("area", "procedure");
                    jObj.put("rule_name", curScriptProcedureRule);
                    if (this.procBusRules.getProcedureBusinessRule(curScriptProcedureRule).length() == 0) {
                        this.busRuleVisitedMissingInProcProcedure++;
                        this.busRuleVisitedMissingInProcTotal++;
                        missingRulesJArr.add(jObj);
                    } else {
                        this.busRuleVisitedTotal++;
                        this.busRuleVisitedProcedureRules++;
                        visitedRulesJArr.add(jObj);
                    }
                }
            }
*/            
        }
        JSONArray accVisited = (JSONArray) this.busRuleCoverageDetail.get("visited");
        if (accVisited == null) {
            accVisited = visitedRulesJArr;
        } else {
            accVisited.addAll(visitedRulesJArr);
        }
        JSONArray accMissing = (JSONArray) this.busRuleCoverageDetail.get("missing");
        if (accMissing == null) {
            accMissing = missingRulesJArr;
        } else {
            accMissing.addAll(missingRulesJArr);
        }
        this.busRuleCoverageDetail.put("visited", accVisited);
        this.busRuleCoverageDetail.put("missing", accMissing);
    }

    public void calculateCoverageMessageCodes(JsonArray currScriptMessages) {
        try {
            this.msgCodeVisitedObj = new JSONArray();
            for (int i = 0; i < currScriptMessages.size(); i++) {
                JsonElement msgCodeObj = currScriptMessages.get(i);
                String curMsgCode = currScriptMessages.get(i).getAsJsonObject().get("messageCode").getAsString();
                if (this.msgCodeVisitedObj == null || !this.msgCodeVisitedObj.contains(msgCodeObj)) {
                    this.msgCodeVisitedObj.add(msgCodeObj);
                }
                if (Boolean.FALSE.equals(this.msgCodeVisited.contains(curMsgCode))) {
                    this.msgCodeVisited.add(curMsgCode);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ResponseError.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void saveCoverage() {
        String[] updFldNames = new String[]{TblsTesting.ScriptsCoverage.DATE_EXECUTION.getName(), TblsTesting.ScriptsCoverage.ENDPOINTS_COVERAGE.getName(),
            TblsTesting.ScriptsCoverage.ENDPOINTS_COVERAGE_DETAIL.getName(),
            TblsTesting.ScriptsCoverage.BUS_RULES_COVERAGE.getName(), TblsTesting.ScriptsCoverage.BUS_RULES_COVERAGE_DETAIL.getName(),};
        Object[] updFldValues = new Object[]{LPDate.getCurrentTimeStamp(), this.endpointsCovPerc,
            LPNulls.replaceNull(this.endpointsCoverageDetail).toString(),
            this.busRuleCovPerc, LPNulls.replaceNull(this.busRuleCoverageDetail).toString()};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsTesting.ScriptsCoverage.COVERAGE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.coverageId}, "");
        Rdbms.updateRecordFieldsByFilter(TblsTesting.TablesTesting.SCRIPTS_COVERAGE,
                EnumIntTableFields.getTableFieldsFromString(TblsTesting.TablesTesting.SCRIPTS_COVERAGE, updFldNames), updFldValues, sqlWhere, null);

    }

    void generateSummaryForBusinessRules() {
//        removeProcBusRulesByEndpointExclusion();
        final int DECIMAL_PLACES = 2;
        String percExplPatternStr = "The <*1*> is <*2*> div <*3*> ";

        double procBusRulesTotal = this.procBusRules.getTotalBusinessRules();
        double divisor = procBusRulesTotal - this.coverageBusRulesExcludeList.length;
        double divident = (double) this.busRuleVisitedTotal + (double) this.busRuleVisitedMissingInProcTotal;
        double operatedVal = (divident / divisor) * 100;
        this.busRuleCovPerc = new BigDecimal(String.valueOf(operatedVal));
        String busRulesPercExplStr = percExplPatternStr.replace("<*1*>", (this.busRuleCovPerc).setScale(DECIMAL_PLACES, RoundingMode.UP).toString())
                .replace("<*2*>", "Visited=" + this.busRuleVisitedTotal.toString() + " + Missing=" + this.busRuleVisitedMissingInProcTotal.toString())
                .replace("<*3*>", String.valueOf(divisor));
        if (this.coverageBusRulesExcludeList.length > 0) {
            busRulesPercExplStr = busRulesPercExplStr + " have on mind that the exclusions are " + this.coverageBusRulesExcludeList.length
                    + " what means that the divider is the total (" + procBusRulesTotal + ") minus the excluded (" + this.coverageBusRulesExcludeList.length + ")";
        }
        JSONObject busRulesSummaryJObj = new JSONObject();
        busRulesSummaryJObj.put("percentage_explanation", busRulesPercExplStr);
        busRulesSummaryJObj.put("procedure_total", procBusRulesTotal);
        busRulesSummaryJObj.put("visited_total", this.busRuleVisitedTotal);

        JSONObject unCoveredBusRules = new JSONObject();
        JSONArray excludedBusRules = new JSONArray();
        excludedBusRules.addAll(Arrays.asList(this.coverageBusRulesExcludeList));
        unCoveredBusRules.put("excluded_list", excludedBusRules);

        JSONArray notCoveredBusRules = new JSONArray();

        String[] accVisitedArr = new String[]{};
        JSONArray accVisited = (JSONArray) this.busRuleCoverageDetail.get("visited");
        for (int iVisited = 0; iVisited < accVisited.size(); iVisited++) {
            JSONObject curVisited = (JSONObject) accVisited.get(iVisited);
            accVisitedArr = LPArray.addValueToArray1D(accVisitedArr, curVisited.get("area") + "_" + curVisited.get("rule_name"));
        }

        JSONArray procBusRulesJArr = new JSONArray();
        JSONObject procBusRulesJObj = new JSONObject();
        List<RuleInfo> configBusinessRules = this.procBusRules.getConfigBusinessRules();
        JSONArray procBusRulesAreaJArr = new JSONArray();
        for (RuleInfo curRule : configBusinessRules) {
            JSONObject ruleJObj = new JSONObject();
            ruleJObj.put(curRule.getRuleName(), curRule.getRuleValue());
            procBusRulesAreaJArr.add(ruleJObj);
            String curRuleStr = "config_" + curRule.getRuleName();//+"="+curRule.getRuleValue();
            boolean inExclList = LPArray.valueInArray(this.coverageBusRulesExcludeList, curRuleStr);
            boolean valueInArray = LPArray.valueInArray(accVisitedArr, curRuleStr);
            if (Boolean.FALSE.equals(valueInArray) && Boolean.FALSE.equals(inExclList) && Boolean.FALSE.equals(notCoveredBusRules.contains(curRuleStr))) {
                notCoveredBusRules.add(curRuleStr);
            }
        }
        procBusRulesJObj.put("config", procBusRulesAreaJArr);

        List<RuleInfo> dataBusinessRules = this.procBusRules.getDataBusinessRules();
        procBusRulesAreaJArr = new JSONArray();
        for (RuleInfo curRule : dataBusinessRules) {
            JSONObject ruleJObj = new JSONObject();
            ruleJObj.put(curRule.getRuleName(), curRule.getRuleValue());
            procBusRulesAreaJArr.add(ruleJObj);
            String curRuleStr = "data_" + curRule.getRuleName();//+"="+curRule.getRuleValue();
            boolean inExclList = LPArray.valueInArray(this.coverageBusRulesExcludeList, curRuleStr);
            boolean valueInArray = LPArray.valueInArray(accVisitedArr, curRuleStr);
            if (Boolean.FALSE.equals(valueInArray) && Boolean.FALSE.equals(inExclList) && Boolean.FALSE.equals(notCoveredBusRules.contains(curRuleStr))) {
                notCoveredBusRules.add(curRuleStr);
            }
        }
        procBusRulesJObj.put("data", procBusRulesAreaJArr);

        List<RuleInfo> procedureBusinessRules = this.procBusRules.getProcedureBusinessRules();

        procBusRulesAreaJArr = new JSONArray();
        for (RuleInfo curRule : procedureBusinessRules) {
            JSONObject ruleJObj = new JSONObject();
            ruleJObj.put(curRule.getRuleName(), curRule.getRuleValue());
            procBusRulesAreaJArr.add(ruleJObj);
            String curRuleStr = "procedure_" + curRule.getRuleName();
            boolean inExclList = LPArray.valueInArray(this.coverageBusRulesExcludeList, curRuleStr);
            boolean valueInArray = LPArray.valueInArray(accVisitedArr, curRuleStr);
            if ((Boolean.FALSE.equals(valueInArray) && Boolean.FALSE.equals(notCoveredBusRules.contains(curRuleStr)) && Boolean.FALSE.equals(procedureRulesNotToPutInPlace(curRuleStr)))
                    && (Boolean.FALSE.equals(inExclList) && Boolean.FALSE.equals(LPArray.valueInArray(this.busRuleExcludedByExcludeEndpoint, curRuleStr)))) {
                notCoveredBusRules.add(curRuleStr);
            }
        }
        if (this.busRuleExcludedByExcludeEndpoint.length > 0) {
            JSONArray busRuleExcludedByExcludeEndpointJArr = new JSONArray();
            busRuleExcludedByExcludeEndpointJArr.addAll(Arrays.asList(busRuleExcludedByExcludeEndpoint));
            unCoveredBusRules.put("business_rules_excluded_by_exclude_the_endpoint", busRuleExcludedByExcludeEndpointJArr);
        }

        procBusRulesJObj.put("procedure", procBusRulesAreaJArr);
        procBusRulesJArr.add(procBusRulesJObj);

        this.busRuleCoverageDetail.put("procedure_rules", procBusRulesJArr);
        unCoveredBusRules.put("uncoverage_list", notCoveredBusRules);

        this.busRuleCoverageDetail.put("uncoverage_summary", unCoveredBusRules);
        this.busRuleCoverageDetail.put("summary", busRulesSummaryJObj);
    }

    void generateSummaryForEndpoint() {
        final int DECIMAL_PLACES = 2;
        String percExplPatternStr = "The <*1*> is <*2*> div <*3*> ";

        JSONArray procActionsJArr = new JSONArray();
        procActionsJArr.addAll(Arrays.asList(this.procActionsArr));

        JSONObject endpointsSummaryJObj = new JSONObject();
        double divisor = this.procActionsArr.length;
        divisor = divisor - this.coverageEndpointsExcludeList.length;
        double operatorVal = ((double) this.endpointsVisitedTotal / divisor) * 100;
        this.endpointsCovPerc = new BigDecimal(String.valueOf(operatorVal));
        String endpointsPercExplStr = percExplPatternStr.replace("<*1*>", this.endpointsCovPerc.setScale(DECIMAL_PLACES, RoundingMode.UP).toString())
                .replace("<*2*>", this.endpointsVisitedTotal.toString())
                .replace("<*3*>", String.valueOf(divisor));
        if (this.coverageEndpointsExcludeList.length > 0) {
            endpointsPercExplStr = endpointsPercExplStr + " take care that the exclusions are " + this.coverageEndpointsExcludeList.length
                    + " what means that the divider is the total (" + this.procActionsArr.length + ") minus the excluded (" + this.coverageEndpointsExcludeList.length + ")";
        }
        endpointsSummaryJObj.put("percentage_explanation", endpointsPercExplStr);
        endpointsSummaryJObj.put("procedure_total", this.procActionsArr.length);
        endpointsSummaryJObj.put("visited_total", this.endpointsVisitedTotal);
        this.endpointsCoverageDetail.put("summary", endpointsSummaryJObj);
        this.endpointsCoverageDetail.put("procedure_endpoints", procActionsJArr);

        JSONObject unCoveredEndPoints = new JSONObject();
        JSONArray excludedEndPoints = new JSONArray();
        excludedEndPoints.addAll(Arrays.asList(this.coverageEndpointsExcludeList));
        unCoveredEndPoints.put("excluded_list", excludedEndPoints);
        JSONArray notCoveredEndPoints = new JSONArray();
        for (String curEnd : this.procActionsArr) {
            boolean inExclList = LPArray.valueInArray(this.coverageEndpointsExcludeList, curEnd);
            JSONArray accVisited = (JSONArray) this.endpointsCoverageDetail.get("visited");
            boolean inVisitedList = accVisited.contains(curEnd);
            if (Boolean.FALSE.equals(inExclList) && Boolean.FALSE.equals(inVisitedList) && curEnd.length() > 0 && Boolean.FALSE.equals(LPArray.valueInArray(this.busRuleExcludedByExcludeEndpoint, curEnd))) {
                notCoveredEndPoints.add(curEnd);
            }
        }
        unCoveredEndPoints.put("uncoverage_list", notCoveredEndPoints);
        this.endpointsCoverageDetail.put("uncoverage_summary", unCoveredEndPoints);

    }

    void generateSummaryForMessageCodes() {
        final int DECIMAL_PLACES = 2;
        String percExplPatternStr = "The <*1*> is <*2*> div <*3*> ";
        String[] msgClasses = new String[]{};
        JSONArray msgClassAllMessagesJArr = new JSONArray();
        int totalNotifications=0;
        int totalVisitedNotif=0;
        int totalNotVisitedNotif=0;        
        try {
            
            Object[][] moduleAllNotifications = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.MODULE_ERROR_NOTIFICATIONS,
                new SqlWhere(TblsReqs.TablesReqs.MODULE_ERROR_NOTIFICATIONS, new String[]{TblsReqs.ModuleErrorNotifications.MODULE_NAME.getName(), TblsReqs.ModuleErrorNotifications.MODULE_VERSION.getName()},
                new Object[]{this.moduleName, this.moduleVersion}),
                new EnumIntTableFields[]{TblsReqs.ModuleErrorNotifications.API_NAME, TblsReqs.ModuleErrorNotifications.ERROR_CODE}, null, false);        
            Object[] moduleAllAPIs=LPArray.getColumnFromArray2D(moduleAllNotifications, 0);
            
            for (int iMsgs = 0; iMsgs < this.msgCodeVisitedObj.size(); iMsgs++) {
                JsonObject curVisited = (JsonObject) this.msgCodeVisitedObj.get(iMsgs);
                if (Boolean.TRUE.equals(LPArray.valueInArray(moduleAllAPIs, curVisited.get("className")))&&
                        Boolean.FALSE.equals(LPArray.valueInArray(msgClasses, curVisited.get("className")))) {
                    msgClasses = LPArray.addValueToArray1D(msgClasses, curVisited.get("className").toString());
                }
            }
            msgClasses = LPArray.getUniquesArray(msgClasses);

            try (io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo()//.acceptPackages("com.xyz")
                    .scan()) {
                ClassInfoList classesImplementing = scanResult.getClassesImplementing("trazit.enums.EnumIntMessages");
                String clssObjName = "";
                for (int iClss = 0; iClss < msgClasses.length; iClss++) {
                    JSONObject jObj = new JSONObject();
                    String curClassName = msgClasses[iClss].replace("\"", "");
                    jObj.put("className", curClassName);
                    int totalClassNotifications=0;
                    int totalClassVisitedNotif=0;
                    int totalClassNotVisitedNotif=0;                        

                    for (int i = 0; i < classesImplementing.size(); i++) {
                        totalNotifications++;
                        totalClassNotifications++;
                        ClassInfo getMine = classesImplementing.get(i);
                        clssObjName = getMine.getSimpleName();
                        if (clssObjName.equalsIgnoreCase(curClassName)) {
                            List<Object> enumConstantObjects = getMine.getEnumConstantObjects();
                            JSONArray enumsIncomplete = new JSONArray();
                            for (int j = 0; j < enumConstantObjects.size(); j++) {
                                EnumIntMessages curBusRul = (EnumIntMessages) enumConstantObjects.get(j);
                                String evName = curBusRul.getErrorCode();
                                if (this.msgCodeVisited.contains(evName)) {
                                    evName = evName + " visited";
                                    totalVisitedNotif++;
                                    totalClassVisitedNotif++;
                                }else{
                                    totalNotVisitedNotif++;
                                    totalClassNotVisitedNotif++;
                                }                                
                                enumsIncomplete.add(evName);
                            }
                            jObj.put("total_notifications", totalClassNotifications);
                            jObj.put("visited_notifications", totalClassVisitedNotif);
                            jObj.put("not_visited_notifications", totalClassNotVisitedNotif);
                            jObj.put("percentage", (totalClassVisitedNotif/totalClassNotifications)*100);
                            jObj.put("messages", enumsIncomplete);
                        }
                    }
                    msgClassAllMessagesJArr.add(jObj);
                }
            }
        } catch (Exception e) {
            return;
        }

        JSONObject msgCodesSummaryJObj = new JSONObject();
/*        this.msgCodeCovPerc = new BigDecimal(this.msgCodeVisited.size());
        String msgCodesPercExplStr = percExplPatternStr.replace("<*1*>", this.msgCodeCovPerc.setScale(DECIMAL_PLACES, RoundingMode.UP).toString())
                .replace("<*2*>", String.valueOf(this.msgCodeVisited.size()))
                .replace("<*3*>", String.valueOf("divisor (replace string by variable when so)"));
        if (this.coverageMsgCodeExcludeList.length > 0) {
            msgCodesPercExplStr = msgCodesPercExplStr + " take care that the exclusions are " + this.coverageMsgCodeExcludeList.length
                    + " what means that the divider is the total (" + "this.procActionsArr.length!!!!!" + ") minus the excluded (" + this.coverageMsgCodeExcludeList.length + ")";
        }
*/        
/*        
        jObj.put("total_notifications", totalClassNotifications);
        jObj.put("visited_notifications", totalClassVisitedNotif);
        jObj.put("not_visited_notifications", totalClassNotVisitedNotif);
        jObj.put("percentage", (totalClassVisitedNotif/totalClassNotifications)*100);
*/
        double divisor = totalNotifications;
        double operatorVal = divisor==0? 0:(totalVisitedNotif / divisor) * 100;
        this.msgCodeCovPerc = new BigDecimal(String.valueOf(operatorVal));
        String msgCodesPercExplStr = percExplPatternStr.replace("<*1*>", this.msgCodeCovPerc.setScale(DECIMAL_PLACES, RoundingMode.UP).toString())
                .replace("<*2*>",  String.valueOf(totalVisitedNotif))
                .replace("<*3*>", String.valueOf(divisor));
        if (this.coverageMsgCodeExcludeList.length > 0) {
            msgCodesPercExplStr = msgCodesPercExplStr + " take care that the exclusions are " + this.coverageMsgCodeExcludeList.length
                    + " what means that the divider is the total (" + totalNotifications + ") minus the excluded (" + this.coverageMsgCodeExcludeList.length + ")";
        }
        
        msgCodesSummaryJObj.put("percentage_explanation", msgCodesPercExplStr);
        msgCodesSummaryJObj.put("visited_total", totalVisitedNotif);
        msgCodesSummaryJObj.put("message_collections_visited", msgClassAllMessagesJArr);
        this.msgCodeCoverageDetail.put("summary", msgCodesSummaryJObj);

        JSONObject unCoveredMsgCodes = new JSONObject();
        JSONArray excludedMsgCodes = new JSONArray();
        excludedMsgCodes.addAll(Arrays.asList(this.coverageMsgCodeExcludeList));
        unCoveredMsgCodes.put("excluded_list", excludedMsgCodes);
    }

    void initializeCounters() {
        this.busRuleVisitedTotal = 0;
        this.busRuleVisitedConfigRules = 0;
        this.busRuleVisitedDataRules = 0;
        this.busRuleVisitedProcedureRules = 0;
        this.busRuleVisitedMissingInProcTotal = 0;
        this.busRuleVisitedMissingInProcConfig = 0;
        this.busRuleVisitedMissingInProcData = 0;
        this.busRuleVisitedMissingInProcProcedure = 0;
        this.busRuleCoverageDetail = new JSONObject();
        this.endpointsVisitedTotal = 0;
        this.endpointsMissingTotal = 0;
        this.endpointsCoverageDetail = new JSONObject();
        
        Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, procInstanceName + "-procedure", TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString())){
            this.procActionsArr = this.procBusRules.getProcedureBusinessRule(LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName()).replaceAll("\\|\\|", "\\|").split("\\|");
            this.procActionsArr = LPArray.getUniquesArray(this.procActionsArr);                
        }else{
            Object[] procInstanceActionsInfo = getProcInstanceActionsInfo(procInstanceName);
            Object[][] procInstanceActionsList=(Object[][])procInstanceActionsInfo[1];
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procInstanceActionsList[0][0].toString()))) {
                this.procActionsArr = LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(procInstanceActionsList, 0));
            }                            
        }
         
        
        
        this.msgCodeCoverageDetail = new JSONObject();
    }

    public JSONObject getJsonSummary() {
        //JSONObject mainObj = new JSONObject();
        JSONObject mainObj=LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(ProcedureDefinitionAPIActionsEndpoints.TESTING_COVERAGE_RUN, new Object[]{this.coverageId}, null);
        JSONObject coverageObj = new JSONObject();
        coverageObj.put("coverageId", this.coverageId);
        coverageObj.put("ScriptsIncluded", Arrays.toString(this.scriptIds));
        mainObj.put("Coverage_Record_Info", coverageObj);

        JSONObject endpointsObj = new JSONObject();
        endpointsObj.put("actions_coverage_percentage", this.endpointsCovPerc);
        endpointsObj.put("actions_coverage_detail", this.endpointsCoverageDetail);
        endpointsObj.put("actions_visited", this.endpointsVisitedTotal);
        mainObj.put("Actions_Info", endpointsObj);

        JSONObject busRulesObj = new JSONObject();
        busRulesObj.put("bus_rules_coverage_percentage", this.busRuleCovPerc);
        busRulesObj.put("bus_rules_coverage_detail", this.busRuleCoverageDetail);
        busRulesObj.put("bus_rules_visited", this.busRuleVisitedTotal);
        mainObj.put("Business_Rules_Info", busRulesObj);

        JSONObject msgCodeObj = new JSONObject();
        msgCodeObj.put("msg_codes_coverage_percentage", "This block cannot provide one coverage percentage, it will provide how many visited messages are: " + this.msgCodeCovPerc);
        msgCodeObj.put("msg_codes_coverage_detail", this.msgCodeCoverageDetail);
        msgCodeObj.put("msg_codes_visited", this.msgCodeVisited);
        mainObj.put("Message_Codes_Info", msgCodeObj);

        JSONArray scriptsInfoJArr = new JSONArray();
        String[] fldsToGet = new String[]{TblsTesting.Script.SCRIPT_ID.getName(), TblsTesting.Script.DATE_EXECUTION.getName(), TblsTesting.Script.PURPOSE.getName(), TblsTesting.Script.RUN_SUMMARY.getName()};
        for (Object[] curRec : this.scriptsInfoArr) {
            JSONObject curRecObj = new JSONObject();
            for (String curFld : fldsToGet) {
                curRecObj.put(curFld, curRec[LPArray.valuePosicInArray(this.scriptsFldNameArr, curFld)].toString());
            }
            scriptsInfoJArr.add(curRecObj);
        }
        mainObj.put("Scripts_Info", scriptsInfoJArr);

        return mainObj;
    }

/*    void removeProcBusRulesByEndpointExclusion() {
        List<RuleInfo> procedureBusinessRules = this.procBusRules.getProcedureBusinessRules();
        if (this.coverageEndpointsExcludeList == null) {
            return;
        }
        for (String curExclEndpoint : this.coverageEndpointsExcludeList) {
            String endPointActionEnabledRule = "procedure_actionEnabled" + curExclEndpoint;
            //boolean remove = this.procBusRules.getProcedureBusinessRules().remove(new RuleInfo(endPointActionEnabledRule, ""));
            this.busRuleExcludedByExcludeEndpoint = LPArray.addValueToArray1D(this.busRuleExcludedByExcludeEndpoint, endPointActionEnabledRule);
        }
        String[] toArray = new String[]{};
        for (RuleInfo curRuleI : procedureBusinessRules) {
            toArray = LPArray.addValueToArray1D(toArray, curRuleI.getRuleName());
        }
        if (LPArray.valueInArray(toArray, "actionConfirmRequired")) {
            this.busRuleExcludedByExcludeEndpoint = LPArray.addValueToArray1D(this.busRuleExcludedByExcludeEndpoint, "actionConfirmRequired");
        }
        if (LPArray.valueInArray(toArray, "verifyUserRequired")) {
            this.busRuleExcludedByExcludeEndpoint = LPArray.addValueToArray1D(this.busRuleExcludedByExcludeEndpoint, "verifyUserRequired");
        }
        if (LPArray.valueInArray(toArray, "auditJustifReasonRequired")) {
            this.busRuleExcludedByExcludeEndpoint = LPArray.addValueToArray1D(this.busRuleExcludedByExcludeEndpoint, "auditJustifReasonRequired");
        }
        if (LPArray.valueInArray(toArray, "eSignRequired")) {
            this.busRuleExcludedByExcludeEndpoint = LPArray.addValueToArray1D(this.busRuleExcludedByExcludeEndpoint, "eSignRequired");
        }
    }
*/
    private static Boolean procedureRulesNotToPutInPlace(String curRuleStr) {
        if (curRuleStr.contains("AuditReasonPhrase")) {
            return true;
        }
        if (curRuleStr.equalsIgnoreCase("procedure_actionConfirmRequired")) {
            return true;
        }
        if (curRuleStr.equalsIgnoreCase("procedure_verifyUserRequired")) {
            return true;
        }
        if (curRuleStr.equalsIgnoreCase("procedure_auditJustifReasonRequired")) {
            return true;
        }
        return (curRuleStr.equalsIgnoreCase("procedure_eSignRequired"));
    }
}
