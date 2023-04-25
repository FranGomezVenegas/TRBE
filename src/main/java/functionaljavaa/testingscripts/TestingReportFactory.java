/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

import functionaljavaa.businessrules.BusinessRules;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author User
 */
public class TestingReportFactory {

    public static TestingReport createReport(TestingScript ts, HttpServletRequest request, HttpServletResponse response
    , String scriptSummaryPhrase, BusinessRules busRulesProcInstance, BusinessRules busRulesTesting, Integer numEvaluationArguments) {
        response = LPTestingOutFormat.responsePreparation(response);                
        if (Boolean.TRUE.equals("JSON".equalsIgnoreCase(ts.getSessionRequest().getTestingOutputFormat()))) {
            return new TestingReportJson(ts, request, response, scriptSummaryPhrase, busRulesProcInstance, busRulesTesting, numEvaluationArguments);
        } else {
            return new TestingReportHtml(ts, request, response, scriptSummaryPhrase, busRulesProcInstance, busRulesTesting, numEvaluationArguments);
        }
    }
}
