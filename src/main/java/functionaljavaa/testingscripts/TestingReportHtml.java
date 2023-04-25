/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

import functionaljavaa.businessrules.BusinessRules;
import static functionaljavaa.testingscripts.LPTestingOutFormat.HEADER_END;
import static functionaljavaa.testingscripts.LPTestingOutFormat.HEADER_START;
import static functionaljavaa.testingscripts.LPTestingOutFormat.ROW_END;
import static functionaljavaa.testingscripts.LPTestingOutFormat.ROW_START;
import static functionaljavaa.testingscripts.LPTestingOutFormat.TABLE_END;
import static functionaljavaa.testingscripts.LPTestingOutFormat.headerAddField;
import static functionaljavaa.testingscripts.LPTestingOutFormat.headerAddFields;
import static functionaljavaa.testingscripts.LPTestingOutFormat.rowAddField;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPNulls;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public final class TestingReportHtml implements TestingReport {

    HttpServletRequest request;
    HttpServletResponse response;
    TestingScript tstScriptObj;
    StringBuilder reportHtml;

    TestingReportHtml(TestingScript ts, HttpServletRequest req, HttpServletResponse resp, String scriptSummaryPhrase, BusinessRules busRulesProcInstance, BusinessRules busRulesTesting, Integer numArgEval) {
        request = req;
        response = resp;
        tstScriptObj = ts;
        reportHtml = ts.getTstOut().getHtmlStyleHeader();

        Integer numArgsInUse = 15;
        createSummaryTable(scriptSummaryPhrase);
        createLogsTable(ts.getScriptId());
        createStepsTable(ts, numArgEval, numArgsInUse);
        createBusinessRulesTable(busRulesProcInstance, busRulesTesting);
        publishReport(req, resp);
    }

    @Override
    public void createSummaryTable(String scriptSummaryPhrase) {
        tstScriptObj.getTstOut().publishEvalSummary(request, tstScriptObj.getTstAssertSummary(), scriptSummaryPhrase, tstScriptObj.getTimeStarted());

        String fileContentHeaderSummary = LPTestingOutFormat.tableStart("summary") + ROW_START;
        String fileContentSummary = ROW_START;
        fileContentHeaderSummary = fileContentHeaderSummary + headerAddField("Execution Summary");
        fileContentSummary = fileContentSummary + rowAddField(LPNulls.replaceNull(tstScriptObj.getTstOut().getExecutionSummaryPhrase()));
        if (tstScriptObj.getNumEvaluationArguments() > 0) {
            fileContentHeaderSummary = fileContentHeaderSummary + headerAddField("Time Consume (s)");
            fileContentSummary = fileContentSummary + rowAddField(LPNulls.replaceNull(tstScriptObj.getTstOut().getTotalTimeConsume()).toString());
            fileContentHeaderSummary = fileContentHeaderSummary + headerAddField("Total Tests");
            fileContentSummary = fileContentSummary + rowAddField(tstScriptObj.getTstAssertSummary().getTotalTests().toString());
            fileContentHeaderSummary = fileContentHeaderSummary + headerAddField("Syntaxis Match " + LPTestingOutFormat.TST_ICON_MATCH);
            fileContentSummary = fileContentSummary + LPTestingOutFormat.rowAddField(tstScriptObj.getTstAssertSummary().getTotalSyntaxisMatch().toString());
            fileContentHeaderSummary = fileContentHeaderSummary + headerAddField("Syntaxis Undefined " + LPTestingOutFormat.TST_ICON_UNDEFINED);
            fileContentSummary = fileContentSummary + LPTestingOutFormat.rowAddField(tstScriptObj.getTstAssertSummary().getTotalSyntaxisUndefined().toString());
            fileContentHeaderSummary = fileContentHeaderSummary + headerAddField("Syntaxis Unmatch " + LPTestingOutFormat.TST_ICON_UNMATCH);
            fileContentSummary = fileContentSummary + LPTestingOutFormat.rowAddField(tstScriptObj.getTstAssertSummary().getTotalSyntaxisUnMatch().toString());
        }
        if (tstScriptObj.getNumEvaluationArguments() > 1) {
            fileContentHeaderSummary = fileContentHeaderSummary + headerAddField("Code Match " + LPTestingOutFormat.TST_ICON_MATCH);
            fileContentSummary = fileContentSummary + LPTestingOutFormat.rowAddField(tstScriptObj.getTstAssertSummary().getTotalCodeMatch().toString());
            fileContentHeaderSummary = fileContentHeaderSummary + headerAddField("Code Undefined " + LPTestingOutFormat.TST_ICON_UNDEFINED);
            fileContentSummary = fileContentSummary + LPTestingOutFormat.rowAddField(tstScriptObj.getTstAssertSummary().getTotalCodeUndefined().toString());
            fileContentHeaderSummary = fileContentHeaderSummary + headerAddField("Total ErrorCode Unmatch " + LPTestingOutFormat.TST_ICON_UNMATCH);
            fileContentSummary = fileContentSummary + LPTestingOutFormat.rowAddField(tstScriptObj.getTstAssertSummary().getTotalCodeUnMatch().toString());
        }
        fileContentSummary = fileContentHeaderSummary + fileContentSummary + ROW_END;
        fileContentSummary = fileContentSummary + TABLE_END;
        this.reportHtml.append(fileContentSummary);
    }

    @Override
    public void createBusinessRulesTable(BusinessRules busRulesProcInstance, BusinessRules busRulesTesting) {
        StringBuilder fileContentTable1Builder = new StringBuilder(0);
        fileContentTable1Builder.append(LPTestingOutFormat.tableStart(""));
        fileContentTable1Builder.append(LPTestingOutFormat.HEADER_START).append("Rule Name").append(LPTestingOutFormat.HEADER_END);
        fileContentTable1Builder.append(LPTestingOutFormat.HEADER_START).append("Rule Value").append(LPTestingOutFormat.HEADER_END);
        for (Object[] curRl : BusinessRules.sessionBusinessRulesList(busRulesProcInstance, busRulesTesting)) {
            fileContentTable1Builder.append(LPTestingOutFormat.ROW_START).append(LPTestingOutFormat.FIELD_START)
                    .append(curRl[0]).append(LPTestingOutFormat.FIELD_END);
            fileContentTable1Builder.append(LPTestingOutFormat.FIELD_START).append(curRl[1]).append(LPTestingOutFormat.FIELD_END)
                    .append(LPTestingOutFormat.ROW_END);
        }
        fileContentTable1Builder.append(LPTestingOutFormat.TABLE_END);
        this.reportHtml.append(fileContentTable1Builder);
    }

    @Override
    public void publishReport(HttpServletRequest request, HttpServletResponse response) {
        try (PrintWriter out = response.getWriter()) {
            out.println(reportHtml);
        } catch (IOException ex) {
            Logger.getLogger(TestingReportHtml.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void createLogsTable(Integer scriptId) {
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, null);
        LPTestingOutFormat.getIdsComplete(procReqInstance.getProcedureInstance(), scriptId);

        String fileContentHeaderSummary = LPTestingOutFormat.tableStart("Logs info") + ROW_START;
        String fileContentSummary = ROW_START;

        fileContentHeaderSummary = fileContentHeaderSummary + headerAddField("Logs detail created by running this script") + HEADER_END;
        String msgStr = "Audit Ids";
        if (procReqInstance != null && procReqInstance.getTestingAuditObj().getMinAudit() != null) {
            msgStr = msgStr + " (First: " + LPNulls.replaceNull(procReqInstance.getTestingAuditObj().getMinAudit()).toString()
                    + ", Last: " + LPNulls.replaceNull(procReqInstance.getTestingAuditObj().getMaxAudit()).toString() + ")";
        } else {
            msgStr = msgStr + " " + LPTestingOutFormat.TST_ICON_UNDEFINED + " No Audit records created by this script";
        }
        fileContentSummary = fileContentSummary + ROW_START + rowAddField(msgStr);

        msgStr = "DB Errors Ids";
        if (procReqInstance != null && procReqInstance.getTestingMainInfo() != null) {
            if (procReqInstance.getTestingMainInfo().getDbLogErrorStart() == null) {
                msgStr = msgStr + " " + LPTestingOutFormat.TST_ICON_UNDEFINED + " get DB Errors Ids not activated for this script";
            } else {
                if (procReqInstance.getTestingMainInfo().getDbLogErrorStart().compareTo(procReqInstance.getTestingMainInfo().getDbLogErrorEnd()) == 0) {
                    msgStr = msgStr + " " + LPTestingOutFormat.TST_ICON_MATCH;
                } else {
                    msgStr = msgStr + " " + LPTestingOutFormat.TST_ICON_UNMATCH;
                }

                msgStr = msgStr + " (Before: " + LPNulls.replaceNull(procReqInstance.getTestingMainInfo().getDbLogErrorStart()).toString()
                        + ", After: " + LPNulls.replaceNull(procReqInstance.getTestingMainInfo().getDbLogErrorEnd()).toString() + ")";
            }
        } else {
            msgStr = msgStr + " " + LPTestingOutFormat.TST_ICON_UNDEFINED + " No DB Errors found";
        }
        fileContentSummary = fileContentSummary + ROW_START + rowAddField(msgStr);

        msgStr = "Properties Errors Ids";
        if (procReqInstance != null && procReqInstance.getTestingMainInfo() != null) {
            if (procReqInstance.getTestingMainInfo().getPropertiesErrorStart() == null) {
                msgStr = msgStr + " " + LPTestingOutFormat.TST_ICON_UNDEFINED + " get Properties Errors Ids not activated for this script";
            } else {
                if (procReqInstance.getTestingMainInfo().getPropertiesErrorStart().compareTo(procReqInstance.getTestingMainInfo().getPropertiesErrorEnd()) == 0) {
                    msgStr = msgStr + " " + LPTestingOutFormat.TST_ICON_MATCH;
                } else {
                    msgStr = msgStr + " " + LPTestingOutFormat.TST_ICON_UNMATCH;
                }

                msgStr = msgStr + " (Before: " + LPNulls.replaceNull(procReqInstance.getTestingMainInfo().getPropertiesErrorStart()).toString()
                        + ", After: " + LPNulls.replaceNull(procReqInstance.getTestingMainInfo().getPropertiesErrorEnd()).toString() + ")";
            }
        } else {
            msgStr = msgStr + " " + LPTestingOutFormat.TST_ICON_UNDEFINED + " No Properties Errors found";
        }
        fileContentSummary = fileContentSummary + ROW_START + rowAddField(msgStr);
        fileContentSummary = fileContentHeaderSummary + fileContentSummary + ROW_END;
        fileContentSummary = fileContentSummary + TABLE_END;
        this.reportHtml.append(fileContentSummary);
    }

    @Override
    public void createStepsTable(TestingScript ts, Integer numEvaluationArguments, Integer numArgsInUse) {
        StringBuilder fileContentTable1Builder = new StringBuilder(String.valueOf(ts.getScriptSteps().getOriginalSteps().length));
        fileContentTable1Builder.append(LPTestingOutFormat.tableStart(""));
        Boolean headerTblAdded = false;
        for (StepExecDetail curExec : ts.getScriptSteps().getExecutionData()) {
            Object[] objectInArrays = curExec.getObjectInArrays();
            String[] fldNames = (String[]) objectInArrays[0];
            Object[] fldValues = (Object[]) objectInArrays[1];
            Integer tblLineIndex = (Integer) objectInArrays[2];
            TestingAssert rowAssert = (TestingAssert) objectInArrays[3];
            if (Boolean.FALSE.equals(headerTblAdded)) {
                fileContentTable1Builder.append(addTableHeader(fldNames, numArgsInUse));
                headerTblAdded = true;
            }

            fileContentTable1Builder.append(ROW_START).append(tblLineIndex.toString());
            fileContentTable1Builder.append(rowAddField(tblLineIndex.toString()));
            fileContentTable1Builder.append(rowAddField(curExec.getActionName().toString()));
            fileContentTable1Builder.append(rowAddField(curExec.getTimeConsume().toString()));
            if (numEvaluationArguments == 0) {
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(curExec.getFunctionEvaluation())));
            }
            if (numEvaluationArguments > 0) {
                Object[] evaluate = curExec.getTstAssert().evaluate(numEvaluationArguments, ts.getTstAssertSummary(), curExec.getFunctionEvaluation());
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate));
            }
            Object[] argsWithNamesAndValues = curExec.getArgsWithNamesAndValues();
            for (int i = 0; i < numArgsInUse; i++) {
                if (i<argsWithNamesAndValues.length) {
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(LPNulls.replaceNull(argsWithNamesAndValues[i]).toString().replace("^", ": ")));
                } else {
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(""));
                }
            }
            fileContentTable1Builder.append(ROW_END);

        }
        fileContentTable1Builder.append(LPTestingOutFormat.TABLE_END);
        this.reportHtml.append(fileContentTable1Builder);
    }

    StringBuilder addTableHeader(String[] fldNames, Integer numArgsInUse) {
        String[] evalFlds = new String[]{"Syntaxis Eval", "Notification Eval", "Full Eval Detail"};

        StringBuilder fileContentTable1Builder = new StringBuilder();
        fileContentTable1Builder.append(HEADER_START).append(headerAddField("Action Name"));
        fileContentTable1Builder.append(headerAddField("Time Consume (s)"));
        fileContentTable1Builder.append(headerAddFields(evalFlds));
        for (int i = 1; i <= numArgsInUse; i++) {
            fileContentTable1Builder.append(headerAddField("Arg "
                    + (String.valueOf(i).length() == 1 ? "0" + i : i)));
        }
//        fileContentTable1Builder.append(headerAddFields(fldNames));
        fileContentTable1Builder.append(HEADER_END);
        return fileContentTable1Builder;
    }
}
