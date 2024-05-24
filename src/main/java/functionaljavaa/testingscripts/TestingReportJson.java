/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

import functionaljavaa.businessrules.BusinessRules;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class TestingReportJson implements TestingReport {

    HttpServletRequest request;
    HttpServletResponse response;
    TestingScript tstScriptObj;
    JSONObject reportJson = new JSONObject();

    TestingReportJson(TestingScript ts, HttpServletRequest req, HttpServletResponse resp, String scriptSummaryPhrase, BusinessRules busRulesProcInstance, BusinessRules busRulesTesting, Integer numArgEval) {
        request = req;
        response = resp;
        tstScriptObj = ts;
        Integer numArgsInUse = 15;
        createSummaryTable(scriptSummaryPhrase);
        createLogsTable(ts.getScriptId());
        createStepsTable(ts, numArgEval, numArgsInUse);
        createBusinessRulesTable(busRulesProcInstance, busRulesTesting);
        publishReport(req, resp);
    }

    @Override
    public void createSummaryTable(String scriptSummaryPhrase) {
        JSONObject jMainObj = new JSONObject();
        tstScriptObj.getTstOut().publishEvalSummary(request, tstScriptObj.getTstAssertSummary(), scriptSummaryPhrase, tstScriptObj.getTimeStarted());
        jMainObj.put("Execution Summary", tstScriptObj.getTstOut().getExecutionSummaryPhrase());
        jMainObj.put("Time Consume (s)", LPNulls.replaceNull(tstScriptObj.getTstOut().getTotalTimeConsume()).toString());
        jMainObj.put("Total Tests", tstScriptObj.getTstAssertSummary().getTotalTests().toString());
        jMainObj.put("Syntaxis Match", tstScriptObj.getTstAssertSummary().getTotalSyntaxisMatch().toString());
        jMainObj.put("Syntaxis Undefined", tstScriptObj.getTstAssertSummary().getTotalSyntaxisUndefined().toString());
        jMainObj.put("Syntaxis Unmatch", tstScriptObj.getTstAssertSummary().getTotalSyntaxisUnMatch().toString());
        jMainObj.put("Code Match", tstScriptObj.getTstAssertSummary().getTotalCodeMatch().toString());
        jMainObj.put("Code Undefined", tstScriptObj.getTstAssertSummary().getTotalCodeUndefined().toString());
        jMainObj.put("Code Unmatch", tstScriptObj.getTstAssertSummary().getTotalCodeUnMatch().toString());
        this.reportJson.put("summary", jMainObj);
    }

    @Override
    public void createBusinessRulesTable(BusinessRules busRulesProcInstance, BusinessRules busRulesTesting) {
        JSONArray mainArr = new JSONArray();
        String[] fldsToGet = new String[]{"rule_name", "rule value"};
        for (Object[] curRow : BusinessRules.sessionBusinessRulesList(busRulesProcInstance, busRulesTesting)) {
            mainArr.put(LPJson.convertArrayRowToJSONObject(fldsToGet, curRow));
        }
        //this.reportJson.put("business_rules", mainArr);
    }

    @Override
    public void publishReport(HttpServletRequest request, HttpServletResponse response) {
        LPFrontEnd.servletReturnSuccess(request, response, reportJson);
    }

    @Override
    public void createStepsTable(TestingScript ts, Integer numEvaluationArguments, Integer numArgsInUse) {
        JSONArray mainArr = new JSONArray();

        StringBuilder fileContentTable1Builder = new StringBuilder(String.valueOf(ts.getScriptSteps().getOriginalSteps().length));
        fileContentTable1Builder.append(LPTestingOutFormat.tableStart(""));
        Boolean headerTblAdded = false;
        for (StepExecDetail curExec : ts.getScriptSteps().getExecutionData()) {
            JSONObject curRowObj = new JSONObject();
            Object[] objectInArrays = curExec.getObjectInArrays();
            String[] fldNames = (String[]) objectInArrays[0];
            Object[] fldValues = (Object[]) objectInArrays[1];
            Integer tblLineIndex = (Integer) objectInArrays[2];
            TestingAssert rowAssert = (TestingAssert) objectInArrays[3];

            curRowObj.put("index", tblLineIndex);
            curRowObj.put("action_name", curExec.getActionName().toString());
            curRowObj.put("time_consume_secs", curExec.getTimeConsume());

            curRowObj.put("index", tblLineIndex);
            curRowObj.put("action_name", curExec.getActionName().toString());
            curRowObj.put("time_consume_secs", curExec.getTimeConsume());

            /*
            if (numEvaluationArguments == 0) {
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(curExec.getFunctionEvaluation())));
            }
            if (numEvaluationArguments > 0) {
                Object[] evaluate = curExec.getTstAssert().evaluate(numEvaluationArguments, ts.getTstAssertSummary(), curExec.getFunctionEvaluation());
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate));
            }
             */
            JSONObject syntaxisEvalObj = new JSONObject();
            syntaxisEvalObj.put("syntaxis_eval", curExec.getTstAssert().getEvalSyntaxis());
            syntaxisEvalObj.put("syntaxis_diagnostic", curExec.getTstAssert().getEvalSyntaxisDiagnostic());
            syntaxisEvalObj.put("notification_eval", curExec.getTstAssert().getEvalCode());
            syntaxisEvalObj.put("notification_diagnostic", curExec.getTstAssert().getEvalCodeDiagnostic());
            curRowObj.put("evaluation", syntaxisEvalObj);

            Object[] argsWithNamesAndValues = curExec.getArgsWithNamesAndValues();
            JSONArray argsArr = new JSONArray();
            for (int i = 0; i < numArgsInUse; i++) {
                JSONObject argsObj = new JSONObject();
                if (i < argsWithNamesAndValues.length) {
                    if (argsWithNamesAndValues[i].toString().contains("^")) {
                        String[] split = argsWithNamesAndValues[i].toString().split("\\^");
                        if (split.length == 2) {
                            argsObj.put("name", split[0]);
                            argsObj.put("value", split[1]);
                        } else {
                            argsObj.put("name", argsWithNamesAndValues[i].toString().replace("^", ""));
                            argsObj.put("value", "");
                        }
                        //} else {
                        //    argsObj.put("name", "");
                        //    argsObj.put("value", "");
                    }
                }
                curRowObj.put("argument_" + i, argsObj);
            }
            mainArr.put(curRowObj);
        }
        this.reportJson.put("steps", mainArr);
    }

    @Override
    public void createLogsTable(Integer scriptId) {
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, null);
        LPTestingOutFormat.getIdsComplete(procReqInstance.getProcedureInstance(), scriptId);
        JSONObject mainObj = new JSONObject();

        JSONObject syntaxisEvalObj = new JSONObject();
        syntaxisEvalObj.put("first", LPNulls.replaceNull(procReqInstance.getTestingAuditObj().getMinAudit()).toString());
        syntaxisEvalObj.put("last", LPNulls.replaceNull(procReqInstance.getTestingAuditObj().getMaxAudit()).toString());
        mainObj.put("audit_ids", syntaxisEvalObj);

        String diagnoses = "";

        syntaxisEvalObj = new JSONObject();
        if (procReqInstance != null && procReqInstance.getTestingMainInfo() != null) {
            if (procReqInstance.getTestingMainInfo().getDbLogErrorStart() == null) {
                diagnoses = " get DB Errors Ids not activated for this script";
            } else {
                if (procReqInstance.getTestingMainInfo().getDbLogErrorStart().compareTo(procReqInstance.getTestingMainInfo().getDbLogErrorEnd()) == 0) {
                    diagnoses = "MATCH";
                } else {
                    diagnoses = "UNMATCH";
                }
                syntaxisEvalObj.put("first", LPNulls.replaceNull(procReqInstance.getTestingMainInfo().getDbLogErrorStart()).toString());
                syntaxisEvalObj.put("last", LPNulls.replaceNull(procReqInstance.getTestingMainInfo().getDbLogErrorEnd()).toString());
            }
        } else {
            diagnoses = LPTestingOutFormat.TST_ICON_UNDEFINED + " No DB Errors found";
        }
        syntaxisEvalObj.put("diagnoses", diagnoses);
        mainObj.put("db_errors_ids", syntaxisEvalObj);

        syntaxisEvalObj = new JSONObject();
        if (procReqInstance != null && procReqInstance.getTestingMainInfo() != null) {
            if (procReqInstance.getTestingMainInfo().getPropertiesErrorStart() == null) {
                diagnoses = " get Properties Errors Ids not activated for this script";
            } else {
                if (procReqInstance.getTestingMainInfo().getPropertiesErrorStart().compareTo(procReqInstance.getTestingMainInfo().getPropertiesErrorEnd()) == 0) {
                    diagnoses = "MATCH";
                } else {
                    diagnoses = "UNMATCH";
                }
                syntaxisEvalObj.put("first", LPNulls.replaceNull(procReqInstance.getTestingMainInfo().getPropertiesErrorStart()).toString());
                syntaxisEvalObj.put("last", LPNulls.replaceNull(procReqInstance.getTestingMainInfo().getPropertiesErrorEnd()).toString());
            }
        } else {
            diagnoses = LPTestingOutFormat.TST_ICON_UNDEFINED + " No Properties Errors found";
        }
        syntaxisEvalObj.put("diagnoses", diagnoses);
        mainObj.put("properties_errors_ids", syntaxisEvalObj);

        this.reportJson.put("logs", mainObj);
    }
}
