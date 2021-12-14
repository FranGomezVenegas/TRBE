/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.session;

/**
 *
 * @author User
 */
public class SessionAuditEntry {

    Integer auditId;
    String actionName;
    String actionPrettyEn;
    String actionPrettyEs;
    public SessionAuditEntry(Integer auditId, String actionName, String actionNameEn, String actionNameEs){
        this.auditId=auditId;
        this.actionName=actionName;
        this.actionPrettyEn=actionNameEn;
        this.actionPrettyEs=actionNameEs;
    }
    public Integer getAuditId()   {return this.auditId;}
    public String  getActionName(){return this.actionName;}
    public String  getActionPrettyEn(){return this.actionPrettyEn;}
    public String  getActionPrettyEs(){return this.actionPrettyEs;}    
}
    
