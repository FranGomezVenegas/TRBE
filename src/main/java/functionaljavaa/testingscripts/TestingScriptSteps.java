/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

/**
 *
 * @author User
 */
public class TestingScriptSteps {
    private final Object[][] originalSteps;
    private StepExecDetail[] executionData;
    
    
    Integer numEvaluationArguments;  
    Integer scriptId;
    
    public TestingScriptSteps(Object[][] stps){
        this.originalSteps=stps;
        this.executionData=new StepExecDetail[stps.length];
    }

    public void addStep(Integer index, StepExecDetail stepObj){
        this.executionData[index]=stepObj;
    }

    

    public Object[][] getOriginalSteps() {
        return this.originalSteps;
    }

    StepExecDetail[] getExecutionData() {
        return this.executionData;
    }
}
