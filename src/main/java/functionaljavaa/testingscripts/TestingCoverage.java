/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

import com.google.gson.JsonArray;
import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsTesting;
import functionaljavaa.businessrules.BusinessRules;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lbplanet.utilities.LPArray;
import static lbplanet.utilities.LPArray.convertStringedPipedNumbersInArray;
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
    private JsonArray endpointsCoverageDetail;   
    private String[] procActionsArr;
    
    private BusinessRules procBusRules;
    private BusinessRules scriptsBusRules;
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
    
    public TestingCoverage(String procInstanceName, Integer coverageId){
        this.procInstanceName=procInstanceName;
        this.coverageId=coverageId;

        Object[][] coverageInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.ScriptsCoverage.TBL.getName(),
            new String[]{TblsTesting.ScriptsCoverage.FLD_COVERAGE_ID.getName()},
            new Object[]{coverageId},
            TblsTesting.ScriptsCoverage.getAllFieldNames());        
        //if (LPPlatform.LAB_FALSE.equalsIgnoreCase(coverageInfo[0][0].toString())){return;}
        
        String scrId=LPNulls.replaceNull(coverageInfo[0][LPArray.valuePosicInArray(TblsTesting.ScriptsCoverage.getAllFieldNames(), TblsTesting.ScriptsCoverage.FLD_SCRIPT_IDS_LIST.getName())]).toString();
        this.scriptIds = convertStringedPipedNumbersInArray(scrId);
        scrId=scrId.replace("\\|", "\\|INTEGER*");
        scrId="INTEGER*".concat(scrId);        
        BusinessRules bR=new BusinessRules(procInstanceName, 0);
        this.procBusRules=bR;
/*        Object[][] procBusRul = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureBusinessRules.TBL.getName(), 
                new String[]{TblsReqs.ProcedureBusinessRules.FLD_INSTANCE_NAME.getName()}, new Object[]{procInstanceName}, 
                new String[]{TblsReqs.ProcedureBusinessRules.FLD_MODULE_NAME.getName(), TblsReqs.ProcedureBusinessRules.FLD_FILE_SUFFIX.getName(),
                    TblsReqs.ProcedureBusinessRules.FLD_RULE_NAME.getName()});
        this.procBusRules=procBusRul;*/
        initializeCounters();
        Object[][] scriptsBusRul = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.Script.TBL.getName(), 
                new String[]{TblsTesting.Script.FLD_SCRIPT_ID.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause()+"|"}, new Object[]{scrId}, 
                new String[]{TblsTesting.Script.FLD_SCRIPT_ID.getName(), TblsTesting.Script.FLD_BUSINESS_RULES_VISITED.getName(), TblsTesting.Script.FLD_MESSAGES_VISITED.getName()});
        List<String> calcProcedureBusRules=  new ArrayList<String>();
        List<String> calcDataBusRules=  new ArrayList<String>();
        List<String> calcConfigBusRules =   new ArrayList<String>();        
        List<String> calcEndpoints=  new ArrayList<String>();
//        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptsBusRul[0][0].toString())){
            for (Object[] curScript: scriptsBusRul){
                this.scriptsBusRules=new BusinessRules(procInstanceName, 0, LPJson.convertToJsonArrayStringedObject(curScript[1].toString()));     
                calculateCoverageEndpoints(scrId, calcEndpoints);            
                calculateCoverageBusRules(calcProcedureBusRules, calcDataBusRules, calcConfigBusRules);            
            }
//        }
        saveCoverage();
    }
    public void calculateCoverageEndpoints(String scrId, List<String> calcProcedureActions){       
        Object[] whereFldValue=new Object[]{scrId, true};
        Object[][] scriptsEndpoints = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.ScriptSteps.TBL.getName(), 
                new String[]{TblsTesting.ScriptSteps.FLD_SCRIPT_ID.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause()+"|", TblsTesting.ScriptSteps.FLD_ACTIVE.getName()}, whereFldValue, 
                new String[]{TblsTesting.ScriptSteps.FLD_SCRIPT_ID.getName(), TblsTesting.ScriptSteps.FLD_ARGUMENT_01.getName()});
        this.endpointsCoverageDetail=new JsonArray();
        JsonArray visitedjObj=new JsonArray();
        JsonArray missingjObj=new JsonArray();
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
        
        this.endpointsCoverageDetail.addAll(visitedjObj);
        this.endpointsCoverageDetail.addAll(missingjObj);
    }                
    public void calculateCoverageBusRules(List<String> calcProcedure, List<String> calcData, List<String> calcConfig){       
        this.busRuleCoverageDetail=new JSONObject();
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
        this.busRuleCoverageDetail.put("visited", visitedRulesJArr);
        this.busRuleCoverageDetail.put("missing", missingRulesJArr);
    }            
    public void saveCoverage(){   
        BigDecimal endpointsCovPerc = new BigDecimal(((double)this.endpointsVisitedTotal/
                (double)this.procActionsArr.length)*100);
        BigDecimal busRuleCovPerc = new BigDecimal(((double)this.busRuleVisitedTotal/(double)this.procBusRules.getTotalBusinessRules())*100);
        Object[] updateCoverageRow = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.ScriptsCoverage.TBL.getName(), 
            new String[]{TblsTesting.ScriptsCoverage.FLD_ENDPOINTS_COVERAGE.getName(), TblsTesting.ScriptsCoverage.FLD_ENDPOINTS_MISSING.getName(), TblsTesting.ScriptsCoverage.FLD_ENDPOINTS_COVERAGE_DETAIL.getName(), 
                TblsTesting.ScriptsCoverage.FLD_BUS_RULES_COVERAGE.getName(), TblsTesting.ScriptsCoverage.FLD_BUS_RULES_COVERAGE_DETAIL.getName()}, 
            new Object[]{endpointsCovPerc, this.endpointsMissingTotal, LPNulls.replaceNull(this.endpointsCoverageDetail).toString(), 
                busRuleCovPerc, LPNulls.replaceNull(this.busRuleCoverageDetail).toString()}, 
            new String[]{TblsTesting.ScriptsCoverage.FLD_COVERAGE_ID.getName()}, new Object[]{this.coverageId});        
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
        //this.busRuleCoverageDetail;
        this.endpointsVisitedTotal=0;
        this.endpointsMissingTotal=0;
        this.procActionsArr = this.procBusRules.getProcedureBusinessRule(LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName()).split("\\|");

    }
            
}
