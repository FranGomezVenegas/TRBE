package functionaljavaa.audit;

import databases.Rdbms;
import databases.TblsApp;
import databases.TblsCnfgAudit;
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
public class ConfigTablesAudit {

    public enum AnalysisAuditEvents {
        ANALYSIS_NEW, ANALYSIS_UPDATE, ANALYSIS_METHOD_NEW, ANALYSIS_METHOD_UPDATE, ANALYSIS_METHOD_DELETE,
        ANALYSIS_METHOD_PARAM_NEW, ANALYSIS_METHOD_PARAM_UPDATE, ANALYSIS_METHOD_PARAM_DELETE
    }

    public enum SpecAuditEvents {
        SPEC_NEW, SPEC_UPDATE, SPEC_LIMIT_NEW
    }

    /**
     * Add one record in the audit table when altering any of the levels
     * belonging to the sample structure when not linked to any other statement.
     *
     * @param action String - Action being performed
     * @param tableName String - table where the action was performed into the
     * Sample structure
     * @param tableId Integer - Id for the object where the action was
     * performed.
     * @param auditlog Object[] - All data that should be stored in the audit as
     * part of the action being performed
     * @param specCode
     * @param specConfigVersion
     * @param parentAuditId
     * @return
     */
    public static Object[] analysisAuditAdd(String action, String tableName, String tableId,
            String specCode, Integer specConfigVersion, Object[] auditlog, Integer parentAuditId) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String[] fieldNames = new String[]{TblsCnfgAudit.Analysis.DATE.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};

        Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))) {
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.PROCEDURE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.PROCEDURE_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);
        }

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        if (specCode != null) {
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.CODE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, specCode);
        }
        if (specConfigVersion != null) {
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.CONFIG_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, specConfigVersion);
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.USER_ROLE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        if (token.getAppSessionId() != null && token.getAppSessionId().length()>0) {
            Object[] appSession = LPSession.addProcessSession(Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.DATE_STARTED.getName()});

            //        Object[] appSession = labSession.getAppSession(appSessionId, new String[]{"date_started"});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())) {
                return appSession;
            } else {

                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));
            }
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());
        if (parentAuditId != null) {
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }
        AuditAndUserValidation auditAndUsrValid = ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditAndUsrValid();
        if (auditAndUsrValid!=null && auditAndUsrValid.getAuditReasonPhrase() != null && auditAndUsrValid.getAuditReasonPhrase().length()>0) {
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG_AUDIT.getName()), TblsCnfgAudit.TablesCfgAudit.ANALYSIS.getTableName(),
                fieldNames, fieldValues);
    }

    /**
     * Add one record in the audit table when altering any of the levels
     * belonging to the sample structure when not linked to any other statement.
     *
     * @param action String - Action being performed
     * @param tableName String - table where the action was performed into the
     * Sample structure
     * @param tableId Integer - Id for the object where the action was
     * performed.
     * @param auditlog Object[] - All data that should be stored in the audit as
     * part of the action being performed
     * @param specCode
     * @param specConfigVersion
     * @param parentAuditId
     * @return
     */
    public static Object[] specAuditAdd(String action, String tableName, String tableId,
            String specCode, Integer specConfigVersion, Object[] auditlog, Integer parentAuditId) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String[] fieldNames = new String[]{TblsCnfgAudit.Spec.DATE.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};

        Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))) {
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.PROCEDURE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.PROCEDURE_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);
        }

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        if (specCode != null) {
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.SPEC_CODE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, specCode);
        }
        if (specConfigVersion != null) {
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.SPEC_CONFIG_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, specConfigVersion);
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.USER_ROLE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        if (token.getAppSessionId() != null && token.getAppSessionId().length()>0) {
            Object[] appSession = LPSession.addProcessSession(Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.DATE_STARTED.getName()});

            //        Object[] appSession = labSession.getAppSession(appSessionId, new String[]{"date_started"});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())) {
                return appSession;
            } else {

                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));
            }
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());
        if (parentAuditId != null) {
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }
        AuditAndUserValidation auditAndUsrValid = ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditAndUsrValid();
        if (auditAndUsrValid!=null && auditAndUsrValid.getAuditReasonPhrase() != null && auditAndUsrValid.getAuditReasonPhrase().length()>0) {
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG_AUDIT.getName()), TblsCnfgAudit.TablesCfgAudit.SPEC.getTableName(),
                fieldNames, fieldValues);
    }

}
