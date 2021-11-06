/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsTesting;
import functionaljavaa.businessrules.BusinessRules;
import functionaljavaa.businessrules.RuleInfo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lbplanet.utilities.LPArray;
import static lbplanet.utilities.LPArray.convertStringedPipedNumbersInArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
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
    private final Object[] scriptIds;
    private Integer totalBusRules;
    private Integer totalMessages;

    private Integer endpointsVisitedTotal;
    private Integer endpointsMissingTotal;
    private JSONObject endpointsCoverageDetail;   
    private String[] procActionsArr;
    private String[] coverageEndpointsExcludeList;    
    
    private BusinessRules procBusRules;
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
    private BusinessRules busRuleVisitedMissingInProcRules;
    private JSONObject busRuleCoverageDetail;
    private BigDecimal busRuleCovPerc;
    private BigDecimal endpointsCovPerc;
    private Object[][] scriptsInfoArr;
    private String[] scriptsFldNameArr;
    
    public TestingCoverage(String procInstanceName, Integer coverageId){
        this.procInstanceName=procInstanceName;
        this.coverageId=coverageId;
        String[] covFldNameArr=TblsTesting.ScriptsCoverage.getAllFieldNames();
        Object[][] coverageInfoArr=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.ScriptsCoverage.TBL.getName(),
            new String[]{TblsTesting.ScriptsCoverage.FLD_COVERAGE_ID.getName()},
            new Object[]{coverageId},covFldNameArr);        
        //if (LPPlatform.LAB_FALSE.equalsIgnoreCase(coverageInfo[0][0].toString())){return;}
        
        String scrId=LPNulls.replaceNull(coverageInfoArr[0][LPArray.valuePosicInArray(TblsTesting.ScriptsCoverage.getAllFieldNames(), TblsTesting.ScriptsCoverage.FLD_SCRIPT_IDS_LIST.getName())]).toString();
        this.scriptIds = convertStringedPipedNumbersInArray(scrId);
        scrId=scrId.replace("\\|", "\\|INTEGER*");
        scrId="INTEGER*".concat(scrId);        
        BusinessRules bR=new BusinessRules(procInstanceName, 0);
        this.coverageEndpointsExcludeList=LPNulls.replaceNull(coverageInfoArr[0][LPArray.valuePosicInArray(TblsTesting.ScriptsCoverage.getAllFieldNames(), TblsTesting.ScriptsCoverage.FLD_ENDPOINTS_EXCLUDE_LIST.getName())]).toString().split("\\|");
        if (this.coverageEndpointsExcludeList!=null && this.coverageEndpointsExcludeList[0].length()==0) this.coverageEndpointsExcludeList=new String[]{};
        this.coverageBusRulesExcludeList=LPNulls.replaceNull(coverageInfoArr[0][LPArray.valuePosicInArray(TblsTesting.ScriptsCoverage.getAllFieldNames(), TblsTesting.ScriptsCoverage.FLD_BUS_RULES_EXCLUDE_LIST.getName())]).toString().split("\\|");
        if (this.coverageBusRulesExcludeList!=null && this.coverageBusRulesExcludeList[0].length()==0) this.coverageBusRulesExcludeList=new String[]{};
        
        this.procBusRules=bR;
        initializeCounters();
        this.scriptsFldNameArr=new String[]{TblsTesting.Script.FLD_SCRIPT_ID.getName(), TblsTesting.Script.FLD_BUSINESS_RULES_VISITED.getName(), TblsTesting.Script.FLD_MESSAGES_VISITED.getName(),
            TblsTesting.Script.FLD_DATE_EXECUTION.getName(), TblsTesting.Script.FLD_PURPOSE.getName(), TblsTesting.Script.FLD_RUN_SUMMARY.getName()};
        this.scriptsInfoArr = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.Script.TBL.getName(), 
                new String[]{TblsTesting.Script.FLD_SCRIPT_ID.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause()+"|"}, new Object[]{scrId}, 
                this.scriptsFldNameArr);
        List<String> calcProcedureBusRules=  new ArrayList<String>();
        List<String> calcDataBusRules=  new ArrayList<String>();
        List<String> calcConfigBusRules =   new ArrayList<String>();        
        List<String> calcEndpoints=  new ArrayList<String>();
//        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptsBusRul[0][0].toString())){
            for (Object[] curScript: this.scriptsInfoArr){
                this.scriptsBusRules=new BusinessRules(procInstanceName, 0, LPJson.convertToJsonArrayStringedObject(curScript[1].toString()));     
                calculateCoverageEndpoints(scrId, calcEndpoints);            
                calculateCoverageBusRules(calcProcedureBusRules, calcDataBusRules, calcConfigBusRules);            
            }
//        }
        generateSummaries();
        saveCoverage();
    }
    public void calculateCoverageEndpoints(String scrId, List<String> calcProcedureActions){       
        Object[] whereFldValue=new Object[]{scrId, true};
        Object[][] scriptsEndpoints = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.ScriptSteps.TBL.getName(), 
                new String[]{TblsTesting.ScriptSteps.FLD_SCRIPT_ID.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause()+"|", TblsTesting.ScriptSteps.FLD_ACTIVE.getName()}, whereFldValue, 
                new String[]{TblsTesting.ScriptSteps.FLD_SCRIPT_ID.getName(), TblsTesting.ScriptSteps.FLD_ARGUMENT_01.getName()});
        //this.endpointsCoverageDetail=new JsonArray();
        JSONArray visitedjObj=new JSONArray();
        JSONArray missingjObj=new JSONArray();
        for (Object[] curStep: scriptsEndpoints){
            String curScriptEndpoint=curStep[1].toString();
            if (calcProcedureActions==null || calcProcedureActions.isEmpty() || !calcProcedureActions.contains(curScriptEndpoint)){
                calcProcedureActions.add(curScriptEndpoint);
                if (LPArray.valueInArray(this.procActionsArr, curScriptEndpoint)){
                    this.endpointsVisitedTotal++;
                    visitedjObj.add(curScriptEndpoint);
                }else{
                    this.endpointsMissingTotal++;
                    missingjObj.add(curScriptEndpoint);
                }
            }
        }
        JSONArray accVisited = (JSONArray) this.endpointsCoverageDetail.get("visited");
        if (accVisited==null)
            accVisited=visitedjObj;
        else
            accVisited.addAll(visitedjObj);
        JSONArray accMissing = (JSONArray) this.endpointsCoverageDetail.get("missing");
        if (accMissing==null)
            accMissing=missingjObj;
        else
            accMissing.addAll(missingjObj);
        this.endpointsCoverageDetail.put("visited", accVisited);
        this.endpointsCoverageDetail.put("missing", accMissing);
    }                
    public void calculateCoverageBusRules(List<String> calcProcedure, List<String> calcData, List<String> calcConfig){       
        //this.busRuleCoverageDetail=new JSONObject();
        JSONArray visitedRulesJArr=new JSONArray();
        JSONArray missingRulesJArr=new JSONArray();
        for (int i=0;i<this.scriptsBusRules.getConfigBusinessRules().size();i++){
            String curScriptConfigRule=this.scriptsBusRules.getConfigBusinessRules().get(i).getRuleName();
            if (calcConfig==null || calcConfig.isEmpty() || !calcConfig.contains(curScriptConfigRule)){
                calcConfig.add(curScriptConfigRule);
                JSONObject jObj=new JSONObject();
                jObj.put("area", "config");                
                jObj.put("rule_name", curScriptConfigRule);
                if (this.procBusRules.getConfigBusinessRule(curScriptConfigRule).length()==0){
                    this.busRuleVisitedMissingInProcConfig++;
                    this.busRuleVisitedMissingInProcTotal++;                    
                    missingRulesJArr.add(jObj);
                }else{
                    this.busRuleVisitedTotal++;
                    this.busRuleVisitedConfigRules++;
                    visitedRulesJArr.add(jObj);
                }
            }
        }
        for (int i=0;i<this.scriptsBusRules.getDataBusinessRules().size();i++){
            String curScriptDataRule=this.scriptsBusRules.getDataBusinessRules().get(i).getRuleName();
            if (calcData==null || calcData.isEmpty() || !calcData.contains(curScriptDataRule)){
                calcData.add(curScriptDataRule);
                JSONObject jObj=new JSONObject();
                jObj.put("area", "data");                
                jObj.put("rule_name", curScriptDataRule);
                if (this.procBusRules.getDataBusinessRule(curScriptDataRule).length()==0){
                    this.busRuleVisitedMissingInProcData++;
                    this.busRuleVisitedMissingInProcTotal++;
                    missingRulesJArr.add(jObj);
                }else{
                    this.busRuleVisitedTotal++;
                    this.busRuleVisitedDataRules++;                    
                    visitedRulesJArr.add(jObj);
                }
            }
        }
        for (int i=0;i<this.scriptsBusRules.getProcedureBusinessRules().size();i++){
            String curScriptProcedureRule=this.scriptsBusRules.getProcedureBusinessRules().get(i).getRuleName();
            if (calcProcedure==null || calcProcedure.isEmpty() || !calcProcedure.contains(curScriptProcedureRule)){
                calcProcedure.add(curScriptProcedureRule);
                JSONObject jObj=new JSONObject();
                jObj.put("area", "procedure");                
                jObj.put("rule_name", curScriptProcedureRule);
                if (this.procBusRules.getProcedureBusinessRule(curScriptProcedureRule).length()==0){
                    this.busRuleVisitedMissingInProcProcedure++;
                    this.busRuleVisitedMissingInProcTotal++;
                    missingRulesJArr.add(jObj);
                }else{
                    this.busRuleVisitedTotal++;
                    this.busRuleVisitedProcedureRules++;
                    visitedRulesJArr.add(jObj);
                }
            }
        }
        JSONArray accVisited = (JSONArray) this.busRuleCoverageDetail.get("visited");
        if (accVisited==null)
            accVisited=visitedRulesJArr;
        else
            accVisited.addAll(visitedRulesJArr);
        JSONArray accMissing = (JSONArray) this.busRuleCoverageDetail.get("missing");
        if (accMissing==null)
            accMissing=missingRulesJArr;
        else
            accMissing.addAll(visitedRulesJArr);
        this.busRuleCoverageDetail.put("visited", accVisited);
        this.busRuleCoverageDetail.put("missing", accMissing);
    }            
    public void saveCoverage(){    
        Object[] updateCoverageRow = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.ScriptsCoverage.TBL.getName(), 
            new String[]{TblsTesting.ScriptsCoverage.FLD_DATE_EXECUTION.getName(), TblsTesting.ScriptsCoverage.FLD_ENDPOINTS_COVERAGE.getName(), 
                TblsTesting.ScriptsCoverage.FLD_ENDPOINTS_COVERAGE_DETAIL.getName(), 
                TblsTesting.ScriptsCoverage.FLD_BUS_RULES_COVERAGE.getName(), TblsTesting.ScriptsCoverage.FLD_BUS_RULES_COVERAGE_DETAIL.getName(), 
            }, 
            new Object[]{LPDate.getCurrentTimeStamp(), this.endpointsCovPerc, 
                LPNulls.replaceNull(this.endpointsCoverageDetail).toString(), 
                this.busRuleCovPerc, LPNulls.replaceNull(this.busRuleCoverageDetail).toString(), 
            }, 
            new String[]{TblsTesting.ScriptsCoverage.FLD_COVERAGE_ID.getName()}, new Object[]{this.coverageId});        
    }
    void generateSummaries(){
        final int DECIMAL_PLACES = 2;
        String percExplPatternStr="The <*1*> is <*2*> div <*3*> ";
        double divisor=(double)this.procBusRules.getTotalBusinessRules();
        divisor=divisor-this.coverageBusRulesExcludeList.length;
        this.busRuleCovPerc = new BigDecimal(((double)this.busRuleVisitedTotal/divisor)*100);
        JSONArray procActionsJArr = new JSONArray();
        for (String curV: this.procActionsArr){
            procActionsJArr.add(curV);
        }
        String busRulesPercExplStr=percExplPatternStr.replace("<*1*>", (this.busRuleCovPerc).setScale(DECIMAL_PLACES, RoundingMode.UP).toString())
            .replace("<*2*>", this.busRuleVisitedTotal.toString())
            .replace("<*3*>", String.valueOf(divisor));
        if (this.coverageEndpointsExcludeList.length>0)
            busRulesPercExplStr=busRulesPercExplStr+" have on mind that the exclusions are "+this.coverageBusRulesExcludeList.length+
                " what means that the divider is the total ("+this.procBusRules.getTotalBusinessRules()+") minus the excluded ("+this.coverageBusRulesExcludeList.length+")";
        JSONObject busRulesSummaryJObj=new JSONObject();
        busRulesSummaryJObj.put("percentage_explanation", busRulesPercExplStr);        
        busRulesSummaryJObj.put("procedure_total", this.procBusRules.getTotalBusinessRules());
        busRulesSummaryJObj.put("visited_total", this.busRuleVisitedTotal);
        this.busRuleCoverageDetail.put("summary", busRulesSummaryJObj);
        JSONObject endpointsSummaryJObj=new JSONObject();
        busRulesSummaryJObj.put("percentage_explanation", this.procBusRules.getTotalBusinessRules());        
        divisor=(double)this.procActionsArr.length;
        divisor=divisor-this.coverageEndpointsExcludeList.length;
        this.endpointsCovPerc = new BigDecimal(((double)this.endpointsVisitedTotal/
                divisor)*100);
        String endpointsPercExplStr=percExplPatternStr.replace("<*1*>", this.endpointsCovPerc.setScale(DECIMAL_PLACES, RoundingMode.UP).toString())
            .replace("<*2*>", this.endpointsVisitedTotal.toString())
            .replace("<*3*>", String.valueOf(divisor));
        if (this.coverageEndpointsExcludeList.length>0)
            endpointsPercExplStr=endpointsPercExplStr+" take care that the exclusions are "+this.coverageEndpointsExcludeList.length+
                " what means that the divider is the total ("+this.procActionsArr.length+") minus the excluded ("+this.coverageEndpointsExcludeList.length+")";
        endpointsSummaryJObj.put("percentage_explanation", endpointsPercExplStr);        
        endpointsSummaryJObj.put("procedure_total", this.procActionsArr.length);
        endpointsSummaryJObj.put("visited_total", this.endpointsVisitedTotal);
        this.endpointsCoverageDetail.put("summary", endpointsSummaryJObj);
        this.endpointsCoverageDetail.put("procedure_endpoints", procActionsJArr);
        
        JSONArray procBusRulesJArr=new JSONArray();
        JSONObject procBusRulesJObj=new JSONObject();
        ArrayList<RuleInfo> configBusinessRules = this.procBusRules.getConfigBusinessRules();
        JSONArray procBusRulesAreaJArr=new JSONArray();
        for (RuleInfo curRule:configBusinessRules){
            JSONObject ruleJObj=new JSONObject();
            ruleJObj.put(curRule.getRuleName(), curRule.getRuleValue());
            procBusRulesAreaJArr.add(ruleJObj);
        }
        procBusRulesJObj.put("config", procBusRulesAreaJArr);
        ArrayList<RuleInfo> dataBusinessRules = this.procBusRules.getDataBusinessRules();
        procBusRulesAreaJArr=new JSONArray();
        for (RuleInfo curRule:dataBusinessRules){
            JSONObject ruleJObj=new JSONObject();
            ruleJObj.put(curRule.getRuleName(), curRule.getRuleValue());
            procBusRulesAreaJArr.add(ruleJObj);
        }
        procBusRulesJObj.put("data", procBusRulesAreaJArr);        
        ArrayList<RuleInfo> procedureBusinessRules = this.procBusRules.getProcedureBusinessRules();
        procBusRulesAreaJArr=new JSONArray();
        for (RuleInfo curRule:configBusinessRules){
            JSONObject ruleJObj=new JSONObject();
            ruleJObj.put(curRule.getRuleName(), curRule.getRuleValue());
            procBusRulesAreaJArr.add(ruleJObj);
        }
        procBusRulesJObj.put("procedure", procBusRulesAreaJArr);
        procBusRulesJArr.add(procBusRulesJObj);
        this.busRuleCoverageDetail.put("procedure_rules", procBusRulesJArr);        
    }
    void initializeCounters(){
        this.busRuleVisitedTotal=0;
        this.busRuleVisitedConfigRules=0;
        this.busRuleVisitedDataRules=0;
        this.busRuleVisitedProcedureRules=0;
        this.busRuleVisitedMissingInProcTotal=0;
        this.busRuleVisitedMissingInProcConfig=0;
        this.busRuleVisitedMissingInProcData=0;
        this.busRuleVisitedMissingInProcProcedure=0;
        this.busRuleCoverageDetail=new JSONObject();
        this.endpointsVisitedTotal=0;
        this.endpointsMissingTotal=0;
        this.endpointsCoverageDetail=new JSONObject();
        this.procActionsArr = this.procBusRules.getProcedureBusinessRule(LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName()).split("\\|");

    }
    
    public JSONObject getJsonSummary(){
        JSONObject mainObj=new JSONObject();
        
        JSONObject coverageObj=new JSONObject();
        coverageObj.put("coverageId", this.coverageId);
        coverageObj.put("ScriptsIncluded", Arrays.toString(this.scriptIds));
        mainObj.put("Coverage_Record_Info", coverageObj);

        JSONObject endpointsObj=new JSONObject();
        endpointsObj.put("endpoints_coverage_percentage", this.endpointsCovPerc);
        endpointsObj.put("endpoints_coverage_detail", this.endpointsCoverageDetail);
        endpointsObj.put("endpoints_visited", this.endpointsVisitedTotal);
        mainObj.put("Endpoints_Info", endpointsObj);

        JSONObject busRulesObj=new JSONObject();
        busRulesObj.put("endpoints_coverage_percentage", this.busRuleCovPerc);
        busRulesObj.put("endpoints_coverage_detail", this.busRuleCoverageDetail);
        busRulesObj.put("endpoints_visited", this.busRuleVisitedTotal);
        mainObj.put("Business_Rules_Info", busRulesObj);

        JSONArray scriptsInfoArr=new JSONArray();
        String[] fldsToGet=new String[]{TblsTesting.Script.FLD_SCRIPT_ID.getName(), TblsTesting.Script.FLD_DATE_EXECUTION.getName(), TblsTesting.Script.FLD_PURPOSE.getName(), TblsTesting.Script.FLD_RUN_SUMMARY.getName()};
        for (Object[] curRec: this.scriptsInfoArr){
            JSONObject curRecObj=new JSONObject();
            for (String curFld: fldsToGet){
                curRecObj.put(curFld, curRec[LPArray.valuePosicInArray(this.scriptsFldNameArr, curFld)].toString());
            }
            scriptsInfoArr.add(curRecObj);            
        }
        mainObj.put("Scripts_Info", scriptsInfoArr);
        
        return mainObj;
    }
    
}
