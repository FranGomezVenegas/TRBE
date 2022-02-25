/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import databases.Rdbms;
import databases.TblsApp;
import databases.TblsAppAudit;
import databases.TblsAppProcDataAudit;
import databases.Token;
import functionaljavaa.requirement.Requirement;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPSession;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class GenericAuditFields {
    private String[] fieldNames;
    private Object[] fieldValues;
    private String evaluation;
    private Object[] errorDetail;
    
    public  GenericAuditFields(String[] fldNames, Object[] fldValues){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        
        this.fieldNames = new String[]{TblsAppAudit.Incident.DATE.getName()};
        this.fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};
        if (fldNames!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppProcDataAudit.Instruments.FIELDS_UPDATED.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, LPJson.convertArrayRowToJSONObject(fldNames, fldValues).toJSONString());
        }
        if (procInstanceName!=null){
            Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
            if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.PROCEDURE.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.PROCEDURE_VERSION.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
            }        
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.USER_ROLE.getName());        
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        if (token.getAppSessionId()!=null){
            Object[] appSession = LPSession.addAppSession( Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.DATE_STARTED.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())){
                errorDetail=appSession;
                return;
            }else{
                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));            
            }
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
        AuditAndUserValidation auditAndUsrValid=ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditAndUsrValid();
        if (auditAndUsrValid!=null && auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }    
        
    }

    /**
     * @return the fieldNames
     */
    public String[] getFieldNames() {
        return fieldNames;
    }

    /**
     * @return the fieldValues
     */
    public Object[] getFieldValues() {
        return fieldValues;
    }

    /**
     * @return the evaluation
     */
    public String getEvaluation() {
        return evaluation;
    }

    /**
     * @return the errorDetail
     */
    public Object[] getErrorDetail() {
        return errorDetail;
    }
    
}
