/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

import lbplanet.utilities.LPArray;
import static lbplanet.utilities.LPPlatform.TRAP_MESSAGE_CODE_POSIC;
import static lbplanet.utilities.LPPlatform.TRAP_MESSAGE_EVALUATION_POSIC;
import static lbplanet.utilities.LPPlatform.TRAP_MESSAGE_MESSAGE_POSIC;

/**
 *
 * @author Administrator
 */
public class TestingAssert {
    
    private String evalSyntaxis="";
    private String evalCode="";    
    private String evalSyntaxisDiagnostic="";  
    private String evalCodeDiagnostic="";  
    
    public enum EvalCodes{MATCH, UNMATCH, UNDEFINED, WITH_NO_DIAGNOSTIC}
    /**
     *
     * @param line
     * @param numArgs
     */
    public TestingAssert(Object[] line, Integer numArgs){
        switch (numArgs.toString()){                    
            case "1":
                this.evalSyntaxis=(String) line[0];
                break;
            case "2":
                this.evalSyntaxis=(String) line[0];
                this.evalCode=(String) line[1];
                break;
            default:                
        }        
    }    
    
    /**
     *
     * @param numEvaluationArguments
     * @param tstAssertSummary
     * @param diagnoses
     * @return
     */
    public Object[] evaluate(Integer numEvaluationArguments, TestingAssertSummary tstAssertSummary, Object[] diagnoses){
        String sintaxisIcon = ""; 
        String codeIcon = "";
        if (diagnoses==null || diagnoses.length==0){
            this.evalSyntaxisDiagnostic=EvalCodes.WITH_NO_DIAGNOSTIC.toString();
            this.evalCodeDiagnostic=EvalCodes.WITH_NO_DIAGNOSTIC.toString();
        }else{
            if (numEvaluationArguments>=1){
                if ( (this.getEvalSyntaxis()==null) || (this.getEvalSyntaxis().length()==0) ||("".equals(this.getEvalSyntaxis())) ){
                    tstAssertSummary.increasetotalLabPlanetBooleanUndefined();
                    sintaxisIcon=LPTestingOutFormat.TST_BOOLEANUNDEFINED;
                    this.evalSyntaxisDiagnostic=EvalCodes.UNDEFINED.toString();
                }else{
                    if (this.getEvalSyntaxis().equalsIgnoreCase(diagnoses[0].toString())){
                        tstAssertSummary.increasetotalLabPlanetBooleanMatch(); 
                        sintaxisIcon=LPTestingOutFormat.TST_BOOLEANMATCH;
                        this.evalSyntaxisDiagnostic=EvalCodes.MATCH.toString();
                    }else{
                        tstAssertSummary.increasetotalLabPlanetBooleanUnMatch(); 
                        sintaxisIcon=LPTestingOutFormat.TST_BOOLEANUNMATCH;
                        this.evalSyntaxisDiagnostic=EvalCodes.UNMATCH.toString();
                    }
                }
            }else{
                tstAssertSummary.increasetotalLabPlanetBooleanUndefined();sintaxisIcon=LPTestingOutFormat.TST_BOOLEANUNDEFINED;            
            }
            if (numEvaluationArguments>=2){
                if ( (this.getEvalCode()==null) || (this.getEvalCode().length()==0) ||("".equals(this.getEvalCode())) ){
                    tstAssertSummary.increasetotalLabPlanetErrorCodeUndefined();
                    codeIcon=LPTestingOutFormat.TST_ERRORCODEUNDEFINED;
                    this.evalCodeDiagnostic=EvalCodes.UNDEFINED.toString();
                }else{
                    if (this.getEvalCode().equalsIgnoreCase(diagnoses[4].toString())){
                        tstAssertSummary.increasetotalLabPlanetErrorCodeMatch(); 
                        codeIcon=LPTestingOutFormat.TST_ERRORCODEMATCH;
                        this.evalCodeDiagnostic=EvalCodes.MATCH.toString();
                    }else{
                        tstAssertSummary.increasetotalLabPlanetErrorCodeUnMatch(); 
                        codeIcon=LPTestingOutFormat.TST_ERRORCODEUNMATCH;
                        this.evalCodeDiagnostic=EvalCodes.UNMATCH.toString();
                    }
                }    
            }else{
                tstAssertSummary.increasetotalLabPlanetErrorCodeUndefined();
                codeIcon=LPTestingOutFormat.TST_ERRORCODEUNDEFINED;            
            }
        }
        Object[] diagnostic=new Object[] {sintaxisIcon + " ("+this.getEvalSyntaxis()+") "};
        String message="";
        if (diagnoses.length>TRAP_MESSAGE_EVALUATION_POSIC) message=message+"Syntaxis:"+diagnoses[TRAP_MESSAGE_EVALUATION_POSIC]+". ";
        if (numEvaluationArguments>=2){
            if (diagnoses.length>TRAP_MESSAGE_CODE_POSIC) message=message+"Code:"+diagnoses[TRAP_MESSAGE_CODE_POSIC]+". ";
            diagnostic=LPArray.addValueToArray1D(diagnostic, codeIcon + "<h8>("+this.getEvalCode()+")</h8> ");
        }
        if (diagnoses.length>TRAP_MESSAGE_MESSAGE_POSIC) message=message+"Message:"+diagnoses[TRAP_MESSAGE_MESSAGE_POSIC]+". ";  
        diagnostic=LPArray.addValueToArray1D(diagnostic, message);
        return diagnostic;
    }

    /**
     * @return the evalSyntaxis
     */
    public String getEvalSyntaxis() {
        return evalSyntaxis;
    }

    /**
     * @return the evalCode
     */
    public String getEvalCode() {
        return evalCode;
    }

    /**
     * @return the evalSyntaxisDiagnostic
     */
    public String getEvalSyntaxisDiagnostic() {
        return evalSyntaxisDiagnostic;
    }

    /**
     * @return the evalCodeDiagnostic
     */
    public String getEvalCodeDiagnostic() {
        return evalCodeDiagnostic;
    }
    
}
