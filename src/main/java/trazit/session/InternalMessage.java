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
public class InternalMessage {
    private String diagnostic;
    private String messageCode;
    private Object[] messageCodeVariables;
    private Object newObjectId;
    
    public InternalMessage(String diag, String msgCode, Object[] msgCodeVars){
        this.diagnostic=diag;
        this.messageCode=msgCode;
        this.messageCodeVariables=msgCodeVars;
    }
    public InternalMessage(String diag, String msgCode, Object[] msgCodeVars, Object theNew){
        this.diagnostic=diag;
        this.messageCode=msgCode;
        this.messageCodeVariables=msgCodeVars;
        this.newObjectId=theNew;
    }

    public String getDiagnostic() {return diagnostic;}
    public String getMessageCode() {return messageCode;}
    public Object[] getMessageCodeVariables() {return messageCodeVariables;}
    public Object getNewObjectId() {return newObjectId;}
}
