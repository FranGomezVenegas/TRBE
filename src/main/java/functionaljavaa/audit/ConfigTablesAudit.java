package functionaljavaa.audit;

import databases.Rdbms;
import databases.TblsCnfgAudit;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntAuditEvents;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class ConfigTablesAudit {

    public enum ConfigAnalysisAuditEvents implements EnumIntAuditEvents{
        ANALYSIS_NEW, ANALYSIS_UPDATE, ANALYSIS_METHOD_NEW, ANALYSIS_METHOD_UPDATE, ANALYSIS_METHOD_DELETE,
        ANALYSIS_METHOD_PARAM_NEW, ANALYSIS_METHOD_PARAM_UPDATE, ANALYSIS_METHOD_PARAM_DELETE
    }

    public enum ConfigSpecAuditEvents implements EnumIntAuditEvents{
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
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        GenericAuditFields gAuditFlds=new GenericAuditFields(auditlog);

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();
        
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
        if (parentAuditId != null) {
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
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
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        GenericAuditFields gAuditFlds=new GenericAuditFields(auditlog);

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();
        
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
        if (parentAuditId != null) {
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }
        AuditAndUserValidation auditAndUsrValid = ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditAndUsrValid();
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG_AUDIT.getName()), TblsCnfgAudit.TablesCfgAudit.SPEC.getTableName(),
                fieldNames, fieldValues);
    }

}
