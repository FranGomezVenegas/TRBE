package functionaljavaa.audit;

import databases.Rdbms;
import databases.TblsApp;
import databases.TblsDataAudit;
import databases.Token;
import functionaljavaa.requirement.Requirement;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPSession;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/** 
 *
 * @author User
 */
public final class CertifTablesAudit {
    private CertifTablesAudit() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] CertifTablesAudit(String dbTableName, Integer certifId, String action, String userId, String userName, String objectFldName, String objectName, String objectVersionFldName, Integer ObjectVersion, 
                        Object[] auditlog, Integer trainingId, Integer parentAuditId, String note) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String[] fieldNames = new String[]{TblsDataAudit.CertifUserAnalysisMethod.DATE.getName(), 
            TblsDataAudit.CertifUserAnalysisMethod.TABLE_ID.getName(), TblsDataAudit.CertifUserAnalysisMethod.CERTIF_ID.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp(), certifId, certifId};
        if (procInstanceName!=null){
            Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
            if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.CertifUserAnalysisMethod.PROCEDURE.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.CertifUserAnalysisMethod.PROCEDURE_VERSION.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
            }        
        }
        if (note!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.CertifUserAnalysisMethod.NOTE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, note);
        }
        if (trainingId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.CertifUserAnalysisMethod.TRAINING_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, trainingId);
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.CertifUserAnalysisMethod.ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, objectFldName);
        fieldValues = LPArray.addValueToArray1D(fieldValues, objectName);
        if (ObjectVersion!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, objectVersionFldName);
            fieldValues = LPArray.addValueToArray1D(fieldValues, ObjectVersion);            
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.CertifUserAnalysisMethod.FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.CertifUserAnalysisMethod.USER_ROLE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.CertifUserAnalysisMethod.PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        if (token.getAppSessionId()!=null){
            Object[] appSession = LPSession.addAppSession( Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.DATE_STARTED.getName()});
       
    //        Object[] appSession = labSession.getAppSession(appSessionId, new String[]{"date_started"});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())){
                return appSession;
            }else{

                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.CertifUserAnalysisMethod.APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));            
            }
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.CertifUserAnalysisMethod.TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.CertifUserAnalysisMethod.PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }    
        AuditAndUserValidation auditAndUsrValid=ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditAndUsrValid();
        if (auditAndUsrValid!=null && auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.CertifUserAnalysisMethod.REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }    
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), dbTableName, 
                fieldNames, fieldValues);
    }    
}
