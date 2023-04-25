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
public interface TestingReport {
    void createSummaryTable(String scriptSummaryPhrase);
    void createStepsTable(TestingScript ts, Integer numEvaluationArguments, Integer numArgsInUse);
    void createBusinessRulesTable(BusinessRules busRulesProcInstance, BusinessRules busRulesTesting);
    void createLogsTable(Integer scriptId);
    void publishReport(HttpServletRequest request, HttpServletResponse response);
}
