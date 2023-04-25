/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

import functionaljavaa.businessrules.BusinessRules;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class TestingScript {

    private LocalDateTime timeStarted;
    private TestingAssertSummary tstAssertSummary;
    private Integer numEvaluationArguments;
    private LPTestingOutFormat tstOut;
    private ProcedureRequestSession instanceForActions;
    private BusinessRules busRulesProcInstance;
    private BusinessRules busRulesTesting;
    private TestingScriptSteps scriptSteps;
    private Integer scriptId;
    private Object testingSource;

    public TestingScript(HttpServletRequest request, String testerFileName, ProcedureRequestSession instanceForAct) {
        timeStarted = LPDate.getCurrentTimeStamp();
        tstAssertSummary = new TestingAssertSummary();
        tstOut = new LPTestingOutFormat(request, LPTestingParams.TestingServletsConfig.DB_SCHEMADATA_GENOMA.name(), testerFileName);
        numEvaluationArguments = tstOut.getNumEvaluationArguments();
        instanceForActions = instanceForAct;
        busRulesProcInstance = instanceForAct.getBusinessRulesProcInstance();
        busRulesTesting = instanceForAct.getBusinessRulesTesting();
        scriptSteps = new TestingScriptSteps(tstOut.getTestingContent());
        scriptId = Integer.valueOf(LPNulls.replaceNull(request.getAttribute(LPTestingParams.SCRIPT_ID).toString()));
        testingSource = request.getAttribute(LPTestingParams.TESTING_SOURCE);
    }

    public void increaseTotalTests() {
        tstAssertSummary.increaseTotalTests();
    }

    public void runReport(HttpServletRequest request, HttpServletResponse response, String scriptSummaryPhrase) {
        TestingReportFactory.createReport(this, request, response, scriptSummaryPhrase, busRulesProcInstance, busRulesTesting, numEvaluationArguments);
    }

    /**
     * @return the timeStarted
     */
    public LocalDateTime getTimeStarted() {
        return timeStarted;
    }

    /**
     * @return the tstAssertSummary
     */
    public TestingAssertSummary getTstAssertSummary() {
        return tstAssertSummary;
    }

    /**
     * @return the numEvaluationArguments
     */
    public Integer getNumEvaluationArguments() {
        return numEvaluationArguments;
    }

    /**
     * @return the tstOut
     */
    public LPTestingOutFormat getTstOut() {
        return tstOut;
    }

    /**
     * @return the instanceForActions
     */
    public ProcedureRequestSession getSessionRequest() {
        return instanceForActions;
    }

    /**
     * @return the busRulesProcInstance
     */
    public BusinessRules getBusRulesProcInstance() {
        return busRulesProcInstance;
    }

    /**
     * @return the busRulesTesting
     */
    public BusinessRules getBusRulesTesting() {
        return busRulesTesting;
    }

    /**
     * @return the scriptSteps
     */
    public TestingScriptSteps getScriptSteps() {
        return scriptSteps;
    }

    /**
     * @return the scriptId
     */
    public Integer getScriptId() {
        return scriptId;
    }

    /**
     * @return the testingSource
     */
    public Object getTestingSource() {
        return testingSource;
    }
}
