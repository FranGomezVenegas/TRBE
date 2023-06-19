/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import databases.Rdbms;
import databases.TblsAppAudit;
import module.instrumentsmanagement.definition.TblsInstrumentsDataAudit;
import databases.features.Token;
import functionaljavaa.parameter.Parameter;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntTables;
import trazit.globalvariables.GlobalVariables;
import static trazit.globalvariables.GlobalVariables.DEFAULTLANGUAGE;
import trazit.session.ProcedureRequestSession;
import trazit.session.SessionAuditActions;

/**
 *
 * @author User
 */
public class GenericAuditFields {
    private String[] fieldNames;
    private Object[] fieldValues;
    private String evaluation;
    private Object[] errorDetail;
    private String actionName;
    private String actionPrettyNameEn;
    private String actionPrettyNameEs;
    
    public GenericAuditFields(Object[] auditlog){
        internalAuditFields(null, true);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));        
    }
    public GenericAuditFields(EnumIntAuditEvents auditEventObj, EnumIntTables tblObj, String[] fldNames, Object[] fldValues){
        internalAuditFields(null, true);
        internalAuditActionField(auditEventObj, tblObj);
        if (fldNames!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsDataAudit.Instruments.FIELDS_UPDATED.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, LPJson.convertArrayRowToJSONObject(fldNames, fldValues).toJSONString());
        }    
    }
    public GenericAuditFields(EnumIntAuditEvents auditEventObj, EnumIntTables tblObj, String[] fldNames, Object[] fldValues, String alternativePerson, Boolean includeUserSessionRole){
        internalAuditFields(alternativePerson, includeUserSessionRole);
        internalAuditActionField(auditEventObj, tblObj);
        if (fldNames!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsDataAudit.Instruments.FIELDS_UPDATED.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, LPJson.convertArrayRowToJSONObject(fldNames, fldValues).toJSONString());
        }        
    }
    private void internalAuditFields(String alternativePerson, Boolean includeUserSessionRole){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        
        this.fieldNames = new String[]{TblsAppAudit.Incident.DATE.getName()};
        this.fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};
        if (procInstanceName!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.PROCEDURE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, token.getProcedureInstanceName(procInstanceName));
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.PROCEDURE_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, token.getProcedureInstanceVersion(procInstanceName));        
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.PROCEDURE_HASH_CODE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, token.getProcedureInstanceHashCode(procInstanceName));        
        }
        if (includeUserSessionRole!=null&&includeUserSessionRole){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.USER_ROLE.getName());        
            fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());
        }

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.PERSON.getName());
        if (alternativePerson==null)
            fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        else
            fieldValues = LPArray.addValueToArray1D(fieldValues, alternativePerson);
        if (token.getAppSessionId()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.APP_SESSION_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));            
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
        AuditAndUserValidation auditAndUsrValid=ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditAndUsrValid();
        if (auditAndUsrValid!=null && auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsAppAudit.Incident.REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }           
    }
    private void internalAuditActionField(EnumIntAuditEvents auditEventObj, EnumIntTables tblObj){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=instanceForActions.getProcedureInstance();        
        String fileName=auditEventObj.getClass().getSimpleName(); //actionObj.toString(); //"AppInstrumentsAuditEvents";
        SessionAuditActions auditActions = ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditActions();
        for (GlobalVariables.Languages curLang: GlobalVariables.Languages.values()){            
            Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), 
                   tblObj.getTableName(), TblsInstrumentsDataAudit.Instruments.ACTION_PRETTY_EN.getName().replace(DEFAULTLANGUAGE, curLang.getName()));
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())){                
                String propValue = "";
                if (instanceForActions.getActionEndpoint()!=null)
                    propValue = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
                        instanceForActions.getActionEndpoint().getClass().getSimpleName(), null, instanceForActions.getActionEndpoint().getName(), curLang.getName(), false, null);
                if (LPNulls.replaceNull(propValue).length()==0)
                    propValue = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
                        fileName, null, auditEventObj.toString(), curLang.getName(), false, null);
                if (propValue==null || propValue.length()==0)propValue=auditEventObj.toString();
                fieldNames = LPArray.addValueToArray1D(fieldNames, 
                        TblsInstrumentsDataAudit.Instruments.ACTION_PRETTY_EN.getName().replace(DEFAULTLANGUAGE, curLang.getName()));
                fieldValues = LPArray.addValueToArray1D(fieldValues, propValue);            
            }
        }
        String actionPrettyEn=auditEventObj.toString();
        Integer actionPrettyEnPosic = LPArray.valuePosicInArray(fieldNames, TblsInstrumentsDataAudit.Instruments.ACTION_PRETTY_EN.getName());
        if (actionPrettyEnPosic>-1)
            actionPrettyEn=LPNulls.replaceNull(fieldValues[actionPrettyEnPosic]).toString();
        String actionPrettyEs=auditEventObj.toString();
        Integer actionPrettyEsPosic = LPArray.valuePosicInArray(fieldNames, TblsInstrumentsDataAudit.Instruments.ACTION_PRETTY_ES.getName());
        if (actionPrettyEsPosic>-1)
            actionPrettyEs=LPNulls.replaceNull(fieldValues[actionPrettyEsPosic]).toString();
        if (auditActions!=null && auditActions.getLastAuditAction()!=null){
            actionPrettyEn=auditActions.getLastAuditAction().getActionPrettyEn()+" > "+actionPrettyEn;
            actionPrettyEs=auditActions.getLastAuditAction().getActionPrettyEs()+" > "+actionPrettyEs;
        }
        if (actionPrettyEnPosic==-1){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsDataAudit.Instruments.ACTION_PRETTY_EN.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, actionPrettyEn);
        }else
            fieldValues[actionPrettyEnPosic]=actionPrettyEn;
        
        if (actionPrettyEsPosic==-1){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsDataAudit.Instruments.ACTION_PRETTY_ES.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, actionPrettyEs);
        }else
            fieldValues[actionPrettyEsPosic]=actionPrettyEs;

        this.actionName=auditEventObj.toString();
        this.actionPrettyNameEn=actionPrettyEn;
        this.actionPrettyNameEs=actionPrettyEs;
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsDataAudit.Instruments.ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, auditEventObj.toString());
        if (auditActions.getMainParentAuditAction()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsDataAudit.Instruments.PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditActions.getMainParentAuditAction().getAuditId());
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

    /**
     * @return the actionName
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * @return the actionPrettyNameEn
     */
    public String getActionPrettyNameEn() {
        return actionPrettyNameEn;
    }

    /**
     * @return the actionPrettyNameEs
     */
    public String getActionPrettyNameEs() {
        return actionPrettyNameEs;
    }
    
}
