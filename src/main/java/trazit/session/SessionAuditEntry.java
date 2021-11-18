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
    public SessionAuditEntry(Integer auditId, String actionName){
        this.auditId=auditId;
        this.actionName=actionName;
    }
    public Integer getAuditId()   {return this.auditId;}
    public String  getActionName(){return this.actionName;}
}
    
