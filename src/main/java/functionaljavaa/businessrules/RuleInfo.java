/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.businessrules;

/**
 *
 * @author User
 */
public class RuleInfo {
    public RuleInfo(String ruleName, String ruleValue){
        this.ruleName=ruleName;
        this.ruleValue=ruleValue;
    }
    public String getRuleValue(){return this.ruleValue;}
    public String getRuleName(){return this.ruleName;}
    String ruleName;
    String ruleValue;    
}
