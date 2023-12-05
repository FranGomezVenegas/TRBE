/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlWhere;
import databases.TblsCnfg;
import databases.TblsTesting;
import functionaljavaa.businessrules.BusinessRules;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
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
        tstOut = new LPTestingOutFormat(request, LPTestingParams.TestingServletsConfig.GENOMICS.name(), testerFileName);
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
    
    public static InternalMessage newSpecScript(String testerName, Boolean suggestedByTrazit, String specCode, Integer specVersion, String[] fields, Object[][] rows){
        //String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String scriptPurpose = "Script created by TRAZIT";
        Integer codeFldPosic=LPArray.valuePosicInArray(fields, "evaluation_pretty_en");
        if (codeFldPosic==-1)
            codeFldPosic=LPArray.valuePosicInArray(fields, TblsCnfg.SpecLimits.CODE.getName());
        
        Integer codeVersionFldPosic=LPArray.valuePosicInArray(fields, TblsCnfg.SpecLimits.CONFIG_VERSION.getName());
        
        if (codeFldPosic==-1||codeVersionFldPosic==-1){
            return new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{});
        }                
        RdbmsObject insertRecord = Rdbms.insertRecord(TblsTesting.TablesTesting.SPEC_SCRIPT, 
            new String[]{TblsTesting.SpecScript.SPEC_CODE.getName(), TblsTesting.SpecScript.SPEC_VERSION.getName(),
                TblsTesting.SpecScript.TESTER_NAME.getName(), TblsTesting.SpecScript.PURPOSE.getName(),
                TblsTesting.SpecScript.EVAL_TOTAL_TESTS.getName(), TblsTesting.SpecScript.EVAL_SYNTAXIS_MATCH.getName(), TblsTesting.SpecScript.EVAL_CODE_MATCH.getName(),
                TblsTesting.SpecScript.EVAL_SYNTAXIS_UNMATCH.getName(), TblsTesting.SpecScript.EVAL_CODE_UNMATCH.getName(), TblsTesting.SpecScript.EVAL_SYNTAXIS_UNDEFINED.getName(), TblsTesting.SpecScript.EVAL_CODE_UNDEFINED.getName(),
                TblsTesting.SpecScript.EVAL_NUM_ARGS.getName(), TblsTesting.SpecScript.RUN_SUMMARY.getName(), 
                TblsTesting.SpecScript.ACTIVE.getName(), TblsTesting.SpecScript.DATE_EXECUTION.getName()},
            new Object[]{specCode, specVersion, testerName, scriptPurpose, rows.length, rows.length, rows.length, 0, 0, 0, 0, 2, "COMPLETED SUCCESSFULLY", 
                true, LPDate.getCurrentTimeStamp()}, null);
        if (insertRecord.getRunSuccess()){            
            InternalMessage newScriptForUAT = newSpecScriptForUAT(Integer.valueOf(insertRecord.getNewRowId().toString()), fields, rows);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(newScriptForUAT.getDiagnostic())){
                assignScriptToSpec(Integer.valueOf(insertRecord.getNewRowId().toString()), fields, rows);
            }
            return newScriptForUAT;
        }else{
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables());
        }
        
    }
    public static InternalMessage newSpecScriptForUAT(Integer scriptId, String[] fields, Object[][] rows){
        //ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        //String procInstanceName = instanceForActions.getProcedureInstance();        
        int stepId=1;
        RdbmsObject insertRecord = null;
        for (Object[] curRow: rows){
            insertRecord = Rdbms.insertRecord(TblsTesting.TablesTesting.SPEC_SCRIPT_STEPS, 
                new String[]{TblsTesting.SpecScriptSteps.SCRIPT_ID.getName(), TblsTesting.SpecScriptSteps.STEP_ID.getName(), 
                    TblsTesting.SpecScriptSteps.EXPECTED_SYNTAXIS.getName(), TblsTesting.SpecScriptSteps.FUNCTION_SYNTAXIS.getName(), 
                    TblsTesting.SpecScriptSteps.EXPECTED_CODE.getName(), TblsTesting.SpecScriptSteps.FUNCTION_CODE.getName(), TblsTesting.SpecScriptSteps.TESTER_NOTES.getName(),
                    TblsTesting.SpecScriptSteps.VARIATION_NAME.getName(), TblsTesting.SpecScriptSteps.ANALYSIS.getName(),
                    TblsTesting.SpecScriptSteps.METHOD_NAME.getName(), TblsTesting.SpecScriptSteps.METHOD_VERSION.getName(), TblsTesting.SpecScriptSteps.PARAMETER.getName(), TblsTesting.SpecScriptSteps.VALUE.getName()},
                new Object[]{scriptId, stepId, 
                    LPPlatform.LAB_TRUE.equalsIgnoreCase(curRow[LPArray.valuePosicInArray(fields,"syntaxis")].toString()), LPPlatform.LAB_TRUE.equalsIgnoreCase(curRow[LPArray.valuePosicInArray(fields,"syntaxis")].toString()),
                    curRow[LPArray.valuePosicInArray(fields,"evaluation")], curRow[LPArray.valuePosicInArray(fields,"evaluation")],
                    curRow[LPArray.valuePosicInArray(fields,"reason")],
                    curRow[LPArray.valuePosicInArray(fields, TblsCnfg.SpecLimits.VARIATION_NAME.getName())], curRow[LPArray.valuePosicInArray(fields, TblsCnfg.SpecLimits.ANALYSIS.getName())],
                    curRow[LPArray.valuePosicInArray(fields, TblsCnfg.SpecLimits.METHOD_NAME.getName())], curRow[LPArray.valuePosicInArray(fields, TblsCnfg.SpecLimits.METHOD_VERSION.getName())],
                    curRow[LPArray.valuePosicInArray(fields, TblsCnfg.SpecLimits.PARAMETER.getName())], curRow[LPArray.valuePosicInArray(fields,"suggested_value")]
                    }, null);
            if (Boolean.FALSE.equals(insertRecord.getRunSuccess())){
                return new InternalMessage(LPPlatform.LAB_FALSE, insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables());
            }        
            stepId++;
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, insertRecord.getErrorMessageCode(), new Object[]{});        
    }
    
    public static InternalMessage assignScriptToSpec(Integer scriptId, String[] fields, Object[][] rows){
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Integer codeFldPosic=LPArray.valuePosicInArray(fields, TblsCnfg.SpecLimits.CODE.getName());
        Integer codeVersionFldPosic=LPArray.valuePosicInArray(fields, TblsCnfg.SpecLimits.CONFIG_VERSION.getName());
        
        if (codeFldPosic==-1||codeVersionFldPosic==-1){
            return new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{});
        }
        Object[][] spec = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.SPEC.getTableName(),
            new String[]{TblsCnfg.Spec.CODE.getName(), TblsCnfg.Spec.CONFIG_VERSION.getName()}, 
            new Object[]{rows[0][codeFldPosic], rows[0][codeVersionFldPosic]},
            new String[]{TblsCnfg.Spec.TESTING_SCRIPTS.getName()});
        if ((LPPlatform.LAB_FALSE.equalsIgnoreCase(spec[0][0].toString()))){
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{rows[0][codeFldPosic], rows[0][codeVersionFldPosic]});
        }
        //&& (Boolean.FALSE.equals(Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND.getErrorCode().equalsIgnoreCase(specLimits[0][4].toString())))) {
        String curTestingScripts=LPNulls.replaceNull(spec[0][0]).toString();
        if (curTestingScripts.length()>0)
            curTestingScripts=curTestingScripts+"|";
        curTestingScripts=curTestingScripts+scriptId.toString();
        RdbmsObject updateTable = Rdbms.updateTableRecordFieldsByFilter(TblsCnfg.TablesConfig.SPEC, 
                new EnumIntTableFields[]{TblsCnfg.Spec.TESTING_SCRIPTS}, new Object[]{curTestingScripts},
                new SqlWhere(TblsCnfg.TablesConfig.SPEC, new String[]{TblsCnfg.Spec.CODE.getName(), TblsCnfg.Spec.CONFIG_VERSION.getName()},
                        new Object[]{rows[0][codeFldPosic], rows[0][codeVersionFldPosic]})
                , procInstanceName);
        
        return new InternalMessage(updateTable.getApiMessage()[0].toString(), updateTable.getErrorMessageCode(), updateTable.getErrorMessageVariables());
    }
}
