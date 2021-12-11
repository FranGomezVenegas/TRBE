/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.session;

import java.util.ArrayList;

/**
 *
 * @author User
 */
public class SessionAuditActions {
    
    ArrayList<SessionAuditEntry> allActions;

    public SessionAuditActions() {
        this.allActions = new ArrayList<SessionAuditEntry>();
    }
    
    public void addAuditAction(Integer auditId, String actionName){
        this.allActions.add(new SessionAuditEntry(auditId, actionName));
    }
    public SessionAuditEntry getMainParentAuditAction(){
        if (this.allActions.isEmpty())return null;
        else return this.allActions.get(0);
    }
    public SessionAuditEntry getLastAuditAction(){
        if (this.allActions.isEmpty())return null;
        else return this.allActions.get(this.allActions.size()-1);
    }
    
}
