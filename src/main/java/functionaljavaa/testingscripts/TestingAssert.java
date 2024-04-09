/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import static lbplanet.utilities.LPPlatform.TRAP_MESSAGE_CODE_POSIC;
import static lbplanet.utilities.LPPlatform.TRAP_MESSAGE_EVALUATION_POSIC;
import static lbplanet.utilities.LPPlatform.TRAP_MESSAGE_MESSAGE_POSIC;
import trazit.session.InternalMessage;

/**
 *
 * @author Administrator
 */
public class TestingAssert {
    
    private Boolean evalSyntaxisB=null;
    private String evalSyntaxis="";
    private String evalSyntaxisDiagnostic="";  
    private String evalCode="";    
    private String evalCodeDiagnostic="";  
    
    public enum EvalCodes{MATCH, UNMATCH, UNDEFINED, WITH_NO_DIAGNOSTIC}
    /**
     *
     * @param line
     * @param numArgs
     */
    public TestingAssert(Object[] line, Integer numArgs, Boolean isBoolean){
        switch (numArgs.toString()){                    
            case "1":
                if (isBoolean!=null&&Boolean.TRUE.equals(isBoolean))
                    this.evalSyntaxisB=Boolean.valueOf(line[0].toString());
                else
                    this.evalSyntaxis=line[0].toString();
                break;
            case "2":
                if (isBoolean!=null&&Boolean.TRUE.equals(isBoolean))
                    this.evalSyntaxisB=Boolean.valueOf(line[0].toString());
                else
                    this.evalSyntaxis=line[0].toString();
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
    public Object[] evaluate(Integer numEvaluationArguments, TestingAssertSummary tstAssertSummary, Object[] diagnoses, Integer codeEvalPosic){
        String sintaxisIcon = ""; 
        String codeIcon = "";
        if (diagnoses==null || diagnoses.length==0){
            this.evalSyntaxisDiagnostic=EvalCodes.WITH_NO_DIAGNOSTIC.toString();
            this.evalCodeDiagnostic=EvalCodes.WITH_NO_DIAGNOSTIC.toString();
        }else{
            if (numEvaluationArguments>=1){
                if ( ((this.getEvalSyntaxis()==null) || (this.getEvalSyntaxis().length()==0) ||("".equals(this.getEvalSyntaxis())))
                    &&   (this.getEvalSyntaxisB()==null) ){
                    tstAssertSummary.increasetotalLabPlanetBooleanUndefined();
                    sintaxisIcon=LPTestingOutFormat.TST_BOOLEANUNDEFINED;
                    this.evalSyntaxisDiagnostic=EvalCodes.UNDEFINED.toString();
                }else{
                    if ((this.getEvalSyntaxisB()==null)){
                        if (this.getEvalSyntaxis().equalsIgnoreCase(diagnoses[0].toString())){
                            tstAssertSummary.increasetotalLabPlanetBooleanMatch(); 
                            sintaxisIcon=LPTestingOutFormat.TST_BOOLEANMATCH;
                            this.evalSyntaxisDiagnostic=EvalCodes.MATCH.toString();
                        }else{
                            tstAssertSummary.increasetotalLabPlanetBooleanUnMatch(); 
                            sintaxisIcon=LPTestingOutFormat.TST_BOOLEANUNMATCH;
                            this.evalSyntaxisDiagnostic=EvalCodes.UNMATCH.toString();
                        }
                    }else{
                        Boolean diagB=diagnoses[0].toString()==LPPlatform.LAB_TRUE?true:false;
                        
                        if (this.getEvalSyntaxisB().equals(diagB)){
                            tstAssertSummary.increasetotalLabPlanetBooleanMatch(); 
                            sintaxisIcon=LPTestingOutFormat.TST_BOOLEANMATCH;
                            this.evalSyntaxisDiagnostic=EvalCodes.MATCH.toString();
                        }else{
                            tstAssertSummary.increasetotalLabPlanetBooleanUnMatch(); 
                            sintaxisIcon=LPTestingOutFormat.TST_BOOLEANUNMATCH;
                            this.evalSyntaxisDiagnostic=EvalCodes.UNMATCH.toString();
                        }                        
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
                    if (diagnoses.length<4){
                        tstAssertSummary.increasetotalLabPlanetErrorCodeUndefined();
                        codeIcon=LPTestingOutFormat.TST_ERRORCODEUNDEFINED;
                    }
                    else if (this.getEvalCode().equalsIgnoreCase(diagnoses[codeEvalPosic].toString())){
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
        Object[] diagnostic=new Object[]{};//{sintaxisIcon + " ("+this.getEvalSyntaxisB()!=null?this.getEvalSyntaxisB().toString():this.getEvalSyntaxis().toString()+") "};
        String message="";
        if (diagnoses==null){
            diagnostic=LPArray.addValueToArray1D(diagnostic, sintaxisIcon+ " ("+LPNulls.replaceNull(this.getEvalSyntaxisB()).toString()+") ");
            message=message+LPPlatform.LAB_TRUE.equalsIgnoreCase("diagnoses is null");            
        }
        if (diagnoses!=null&&diagnoses.length>TRAP_MESSAGE_EVALUATION_POSIC){
            message=message+"Syntaxis:";
            if (this.getEvalSyntaxisB()!=null){
                diagnostic=LPArray.addValueToArray1D(diagnostic, sintaxisIcon+ " ("+LPNulls.replaceNull(this.getEvalSyntaxisB()).toString()+") ");
                message=message+LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[TRAP_MESSAGE_EVALUATION_POSIC].toString());
            }else{
                diagnostic=LPArray.addValueToArray1D(diagnostic, sintaxisIcon+ " ("+LPNulls.replaceNull(this.getEvalSyntaxis()).toString()+") ");
                message=message+diagnoses[TRAP_MESSAGE_EVALUATION_POSIC].toString()+". ";
            }
            diagnoses[TRAP_MESSAGE_EVALUATION_POSIC]=message;
            //diagnostic=LPArray.addValueToArray1D(diagnostic, sintaxisIcon + "<h8>("+this.getEvalCode()+")</h8> ");
        }
        if (diagnoses!=null&&numEvaluationArguments>=2){
            if (diagnoses.length>TRAP_MESSAGE_CODE_POSIC) message=message+" Code:"+diagnoses[TRAP_MESSAGE_CODE_POSIC]+". ";
            diagnostic=LPArray.addValueToArray1D(diagnostic, codeIcon + "<h8>("+this.getEvalCode()+")</h8> ");
        }
        if (diagnoses!=null&&diagnoses.length>TRAP_MESSAGE_MESSAGE_POSIC) message=message+"Message:"+diagnoses[TRAP_MESSAGE_MESSAGE_POSIC]+". ";  
        diagnostic=LPArray.addValueToArray1D(diagnostic, message);
        return diagnostic;
    }

    public Object[] evaluate(Integer numEvaluationArguments, TestingAssertSummary tstAssertSummary, InternalMessage diagnosesObj, Integer codeEvalPosic){
        String sintaxisIcon = ""; 
        String codeIcon = "";
        String errorCode="";

        if (diagnosesObj==null){
            this.evalSyntaxisDiagnostic=EvalCodes.WITH_NO_DIAGNOSTIC.toString();
            this.evalCodeDiagnostic=EvalCodes.WITH_NO_DIAGNOSTIC.toString();
        }else{
            if (diagnosesObj.getMessageCodeObj()!=null){
                errorCode=diagnosesObj.getMessageCodeObj().getErrorCode();
            }else{
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesObj.getDiagnostic()))
                    errorCode=diagnosesObj.getMessageCodeEndpoint().getSuccessMessageCode();
            }
            if (numEvaluationArguments>=1){
                if ( ((this.getEvalSyntaxis()==null) || (this.getEvalSyntaxis().length()==0) ||("".equals(this.getEvalSyntaxis())))
                    &&   (this.getEvalSyntaxisB()==null) ){
                    tstAssertSummary.increasetotalLabPlanetBooleanUndefined();
                    sintaxisIcon=LPTestingOutFormat.TST_BOOLEANUNDEFINED;
                    this.evalSyntaxisDiagnostic=EvalCodes.UNDEFINED.toString();
                }else{
                    if ((this.getEvalSyntaxisB()==null)){
                        if (this.getEvalSyntaxis().equalsIgnoreCase(diagnosesObj.getDiagnostic())){
                            tstAssertSummary.increasetotalLabPlanetBooleanMatch(); 
                            sintaxisIcon=LPTestingOutFormat.TST_BOOLEANMATCH;
                            this.evalSyntaxisDiagnostic=EvalCodes.MATCH.toString();
                        }else{
                            tstAssertSummary.increasetotalLabPlanetBooleanUnMatch(); 
                            sintaxisIcon=LPTestingOutFormat.TST_BOOLEANUNMATCH;
                            this.evalSyntaxisDiagnostic=EvalCodes.UNMATCH.toString();
                        }
                    }else{
                        Boolean diagB=diagnosesObj.getDiagnostic()==LPPlatform.LAB_TRUE?true:false;
                        
                        if (this.getEvalSyntaxisB().equals(diagB)){
                            tstAssertSummary.increasetotalLabPlanetBooleanMatch(); 
                            sintaxisIcon=LPTestingOutFormat.TST_BOOLEANMATCH;
                            this.evalSyntaxisDiagnostic=EvalCodes.MATCH.toString();
                        }else{
                            tstAssertSummary.increasetotalLabPlanetBooleanUnMatch(); 
                            sintaxisIcon=LPTestingOutFormat.TST_BOOLEANUNMATCH;
                            this.evalSyntaxisDiagnostic=EvalCodes.UNMATCH.toString();
                        }                        
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
                    if (errorCode.length()==0){
                        tstAssertSummary.increasetotalLabPlanetErrorCodeUndefined();
                        codeIcon=LPTestingOutFormat.TST_ERRORCODEUNDEFINED;
                    }
                    else if (this.getEvalCode().equalsIgnoreCase(errorCode)){
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
        Object[] diagnostic=new Object[]{};//{sintaxisIcon + " ("+this.getEvalSyntaxisB()!=null?this.getEvalSyntaxisB().toString():this.getEvalSyntaxis().toString()+") "};
        String message="";
        if (diagnosesObj==null){
            diagnostic=LPArray.addValueToArray1D(diagnostic, sintaxisIcon+ " ("+LPNulls.replaceNull(this.getEvalSyntaxisB()).toString()+") ");
            message=message+LPPlatform.LAB_TRUE.equalsIgnoreCase("diagnoses is null");            
        }
        if (diagnosesObj!=null&&errorCode!=null){
            message=message+"Syntaxis:";
            if (this.getEvalSyntaxisB()!=null){
                diagnostic=LPArray.addValueToArray1D(diagnostic, sintaxisIcon+ " ("+LPNulls.replaceNull(this.getEvalSyntaxisB()).toString()+") ");
                message=message+LPPlatform.LAB_TRUE.equalsIgnoreCase(errorCode);
            }else{
                diagnostic=LPArray.addValueToArray1D(diagnostic, sintaxisIcon+ " ("+LPNulls.replaceNull(this.getEvalSyntaxis()).toString()+") ");
                message=message+errorCode+". ";
            }
            //diagnoses[TRAP_MESSAGE_EVALUATION_POSIC]=message;
            //diagnostic=LPArray.addValueToArray1D(diagnostic, sintaxisIcon + "<h8>("+this.getEvalCode()+")</h8> ");
        }
        if (diagnosesObj!=null&&numEvaluationArguments>=2){
            if (errorCode!=null) message=message+" Code:"+errorCode+". ";
            diagnostic=LPArray.addValueToArray1D(diagnostic, codeIcon + "<h8>("+this.getEvalCode()+")</h8> ");
        }
        if (diagnosesObj!=null&&errorCode!=null&&diagnosesObj.getMessageCodeObj()!=null){
            message=message+"Message:"+diagnosesObj.getMessageCodeObj().getDefaultTextEn()+". ";  
        }else{
            message=message+"Message:"+errorCode+". ";  
        }
        diagnostic=LPArray.addValueToArray1D(diagnostic, message);
        return diagnostic;
    }

    /**
     * @return the evalSyntaxis
     */
    public String getEvalSyntaxis() {
        return evalSyntaxis;
    }
    public Boolean getEvalSyntaxisB() {
        return evalSyntaxisB;
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
