/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsTesting;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import static lbplanet.utilities.LPPlatform.TRAP_MESSAGE_CODE_POSIC;
import static lbplanet.utilities.LPPlatform.TRAP_MESSAGE_EVALUATION_POSIC;
import org.json.JSONArray;
import trazit.enums.EnumIntTableFields;

/**
 *
 * @author User
 */
public class StepExecDetail {

    /**
     * @return the interruptExecution
     */
    public Boolean getInterruptExecution() {
        return interruptExecution;
    }

    /**
     * @return the interruptExecutionReason
     */
    public String getInterruptExecutionReason() {
        return interruptExecutionReason;
    }
    private final LocalDateTime timeStarted;
    private LocalDateTime timeCompleted;
    private BigDecimal timeConsume;
    private final Integer stepId;
    private final String actionName;
    private final Integer contentIndex;
    private final Object[] originalSteps;
    private final TestingAssert tstAssert;
    private final Integer numEvaluationArguments;
    private final Integer scriptId;
    private Object[] functionEvaluation;
    private Boolean interruptExecution;
    private String interruptExecutionReason;
    private Object[] argsWithNamesAndValues;

    public StepExecDetail(Integer contentI, Integer stpId, Object[] origStps, Integer numEvalArgs, Integer scrId, String actName) {
        this.timeStarted = LPDate.getCurrentTimeStamp();
        stepId = stpId;
        contentIndex = contentI;
        originalSteps = origStps;
        tstAssert = new TestingAssert(origStps, numEvalArgs, false);
        numEvaluationArguments = numEvalArgs;
        scriptId = scrId;
        actionName = actName;
        interruptExecution = false;
        interruptExecutionReason = "";
        functionEvaluation = new Object[0];
    }

    /**
     * @return the timeStarted
     */
    public LocalDateTime getTimeStarted() {
        return timeStarted;
    }

    public void evaluationIfInterrupts(LPTestingOutFormat tstOut, Integer totalSteps) {
        if (Boolean.FALSE.equals(tstAssert.getEvalSyntaxis().equalsIgnoreCase(tstAssert.getEvalSyntaxisDiagnostic()))) {

//                tstOut.getStopSyntaxisUnmatchPosic() > -1 && 
//                Boolean.TRUE.equals(Boolean.valueOf(LPNulls.replaceNull(testingContent[contentIndex][tstOut.getStopSyntaxisUnmatchPosic()]).toString()))
//                && Boolean.FALSE.equals(TestingAssert.EvalCodes.MATCH.toString().equalsIgnoreCase(tstAssert.getEvalSyntaxisDiagnostic()))) {
            interruptExecution = true;
            interruptExecutionReason = "Interrupted by evaluation not matching in step " + contentIndex + " of " + totalSteps;
            return;
        }
        if (Boolean.TRUE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(tstAssert.getEvalSyntaxisDiagnostic()))) {
            /*        if (tstOut.getStopSyntaxisFalsePosic() > -1 && 
                Boolean.TRUE.equals(Boolean.valueOf(LPNulls.replaceNull(testingContent[contentIndex][tstOut.getStopSyntaxisFalsePosic()]).toString()))
                && LPPlatform.LAB_FALSE.equalsIgnoreCase(functionEvaluation[0].toString())) {*/
            interruptExecution = true;
            interruptExecutionReason = "Interrupted by evaluation returning false in step " + contentIndex + " of " + totalSteps;
            return;
        }

    }

    public void saveInDbStepEval(Object[] evaluate, JSONArray functionRelatedObjects, TestingAssert tstAssert, Object inputMode) {
//    public StringBuilder publishEvalStep(HttpServletRequest request, Integer stepId, Object[] evaluate, JSONArray functionRelatedObjects, TestingAssert tstAssert, LocalDateTime timeStarted) {
        //StringBuilder fileContentBuilder = new StringBuilder(0);
        LocalDateTime timeCompleted = LPDate.getCurrentTimeStamp();
        String[] updFldNames = new String[]{TblsTesting.ScriptSteps.DATE_EXECUTION.getName(), TblsTesting.ScriptSteps.TIME_COMPLETED.getName()};
        Object[] updFldValues = new Object[]{timeCompleted, timeCompleted};
        if (timeStarted != null) {
            updFldNames = LPArray.addValueToArray1D(updFldNames, TblsTesting.ScriptSteps.TIME_STARTED.getName());
            updFldValues = LPArray.addValueToArray1D(updFldValues, timeStarted);
            BigDecimal secondsInDateRange = LPDate.secondsInDateRange(timeStarted, timeCompleted, true);
            updFldNames = LPArray.addValueToArray1D(updFldNames, TblsTesting.ScriptSteps.TIME_CONSUME.getName());
            updFldValues = LPArray.addValueToArray1D(updFldValues, secondsInDateRange);
        }
        if (numEvaluationArguments > 0 && ("DB".equals(inputMode))) {
            if (evaluate == null || evaluate.length == 0) {
                updFldNames = LPArray.addValueToArray1D(updFldNames, new String[]{TblsTesting.ScriptSteps.FUNCTION_SYNTAXIS.getName(), TblsTesting.ScriptSteps.EVAL_SYNTAXIS.getName()});
                updFldValues = LPArray.addValueToArray1D(updFldValues, new Object[]{"EvaluateEmpty", tstAssert.getEvalSyntaxisDiagnostic()});
            } else {
                updFldNames = LPArray.addValueToArray1D(updFldNames, new String[]{TblsTesting.ScriptSteps.FUNCTION_SYNTAXIS.getName(), TblsTesting.ScriptSteps.EVAL_SYNTAXIS.getName()});
                updFldValues = LPArray.addValueToArray1D(updFldValues, new Object[]{evaluate[TRAP_MESSAGE_EVALUATION_POSIC], tstAssert.getEvalSyntaxisDiagnostic()});
                if (numEvaluationArguments > 1) {
                    updFldNames = LPArray.addValueToArray1D(updFldNames, new String[]{TblsTesting.ScriptSteps.FUNCTION_CODE.getName(), TblsTesting.ScriptSteps.EVAL_CODE.getName(),
                        TblsTesting.ScriptSteps.DYNAMIC_DATA.getName()});
                    updFldValues = LPArray.addValueToArray1D(updFldValues, new Object[]{evaluate[TRAP_MESSAGE_CODE_POSIC], tstAssert.getEvalCodeDiagnostic(),
                        functionRelatedObjects.toString()});
                }
            }
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsTesting.ScriptSteps.SCRIPT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{scriptId}, "");
            sqlWhere.addConstraint(TblsTesting.ScriptSteps.STEP_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{stepId}, "");
            Rdbms.updateRecordFieldsByFilter(TblsTesting.TablesTesting.SCRIPT_STEPS,
                    EnumIntTableFields.getTableFieldsFromString(TblsTesting.TablesTesting.SCRIPT_STEPS, updFldNames), updFldValues, sqlWhere, null);
        }
        return;
    }

    /**
     * @return the tstAssert
     */
    public TestingAssert getTstAssert() {
        return tstAssert;
    }

    public Object[] getObjectInArrays() {
        String[] fldsToGet = new String[]{"Start"};
        Object[] fldsValues = new Object[]{timeStarted};

        return new Object[]{fldsToGet, fldsValues, stepId + 1, tstAssert};
    }

    public void setFunctionEvaluation(Object[] fEval, Object[] args) {
        argsWithNamesAndValues=args;
        BigDecimal secondsInDateRange = null;
        if (timeStarted != null) {
            timeCompleted=LPDate.getCurrentTimeStamp();
            secondsInDateRange = LPDate.secondsInDateRange(timeStarted, getTimeCompleted(), true);
        }   
        timeConsume=secondsInDateRange;
        this.functionEvaluation = fEval;
    }

    public Object[] getFunctionEvaluation() {
        return functionEvaluation;
    }

    /**
     * @return the actionName
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * @return the timeCompleted
     */
    public LocalDateTime getTimeCompleted() {
        return timeCompleted;
    }

    /**
     * @return the timeConsume
     */
    public BigDecimal getTimeConsume() {
        return timeConsume;
    }

    /**
     * @return the argsWithNamesAndValues
     */
    public Object[] getArgsWithNamesAndValues() {
        return argsWithNamesAndValues;
    }
}
