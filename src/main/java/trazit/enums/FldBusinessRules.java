/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.enums;

/**
 *
 * @author User
 */
public class FldBusinessRules {
    private final String busRuleRepository;
    private final String businessRule;
    private final Boolean addIfBusinessRuleAbsent;
    private final Boolean stopTableCreationIfBusinessRuleAbsent;
    private final String[] expectedValues;
    private final String[] notExpectedValues;
    
    public FldBusinessRules(String brRepository, String businessRule, Boolean createIfAbsent, Boolean stopTableCreationIfBusinessRuleAbsent, String[] expectedValues, String[] notExpectedValues){
        this.busRuleRepository=brRepository;
        this.businessRule=businessRule;
        this.addIfBusinessRuleAbsent=createIfAbsent;
        this.stopTableCreationIfBusinessRuleAbsent=stopTableCreationIfBusinessRuleAbsent;
        this.expectedValues=expectedValues;
        this.notExpectedValues=notExpectedValues;
    }
    public String getBusRuleRepository(){return busRuleRepository;}
    public String getBusinessRule(){return businessRule;}
    public String[] getExpectedValues(){return expectedValues;}
    public String[] getNotExpectedValues(){return notExpectedValues;}
    public Boolean getAddIfBusinessRuleAbsent(){return addIfBusinessRuleAbsent;}
    public Boolean getStopTableCreationIfBusinessRuleAbsent(){return stopTableCreationIfBusinessRuleAbsent;}
    
}
