/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package functionaljavaa.businessrules;

/**
 *
 * @author User
 */
public class ActionInfo {
    public ActionInfo(String actionName, String actionRoles, Boolean areYouUserReqd, Boolean justifReasonReqd, Boolean esignReqd, Boolean userConfirmReqd,
            String auditReasonType, String auditReasonListEn, String auditReasonListEs){
        this.actionName=actionName;
        this.actionRoles=actionRoles;        
        this.areYouUserReqd=areYouUserReqd;
        this.justifReasonReqd=justifReasonReqd;
        this.esignReqd=esignReqd;
        this.userConfirmReqd=userConfirmReqd;
        this.auditReasonType=auditReasonType;
        this.auditReasonListEn=auditReasonListEn;
        this.auditReasonListEs=auditReasonListEs;
    }
    public String getActionName(){return this.actionName;}
    public String getActionRoles(){return this.actionRoles;}
    private final String actionName;
    private final String actionRoles;       
    private final Boolean areYouUserReqd;
    private final Boolean justifReasonReqd;
    private final Boolean esignReqd;
    private final Boolean userConfirmReqd;
    private final String auditReasonType;
    private final String auditReasonListEn;
    private final String auditReasonListEs;

    /**
     * @return the areYouUserReqd
     */
    public Boolean getAreYouUserReqd() {
        return areYouUserReqd;
    }

    /**
     * @return the justifReasonReqd
     */
    public Boolean getJustifReasonReqd() {
        return justifReasonReqd;
    }

    /**
     * @return the esignReqd
     */
    public Boolean getEsignReqd() {
        return esignReqd;
    }

    /**
     * @return the userConfirmReqd
     */
    public Boolean getUserConfirmReqd() {
        return userConfirmReqd;
    }

    /**
     * @return the auditReasonType
     */
    public String getAuditReasonType() {
        return auditReasonType;
    }

    /**
     * @return the auditReasonListEn
     */
    public String getAuditReasonListEn() {
        return auditReasonListEn;
    }

    /**
     * @return the auditReasonListEs
     */
    public String getAuditReasonListEs() {
        return auditReasonListEs;
    }

}
