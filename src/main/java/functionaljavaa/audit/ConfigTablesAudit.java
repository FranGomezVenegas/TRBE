package functionaljavaa.audit;

import databases.TblsCnfgAudit;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntTables;
import trazit.session.ProcedureRequestSession;
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
     * @param tblObj
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
    public static Object[] analysisAuditAdd(String action, EnumIntTables tblObj, String tableId,
            String specCode, Integer specConfigVersion, Object[] auditlog, Integer parentAuditId) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        GenericAuditFields gAuditFlds=new GenericAuditFields(auditlog);

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tblObj.getTableName());
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
        return AuditUtilities.applyTheInsert(gAuditFlds, tblObj, fieldNames, fieldValues);       
    }

    /**
     * Add one record in the audit table when altering any of the levels
     * belonging to the sample structure when not linked to any other statement.
     *
     * @param action String - Action being performed
     * @param tblObj
     * @param tableId Integer - Id for the object where the action was
     * performed.
     * @param auditlog Object[] - All data that should be stored in the audit as
     * part of the action being performed
     * @param specCode
     * @param specConfigVersion
     * @param parentAuditId
     * @return
     */
    public static Object[] specAuditAdd(String action, EnumIntTables tblObj, String tableId,
            String specCode, Integer specConfigVersion, Object[] auditlog, Integer parentAuditId) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        GenericAuditFields gAuditFlds=new GenericAuditFields(auditlog);

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tblObj.getTableName());
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
        return AuditUtilities.applyTheInsert(gAuditFlds, tblObj, fieldNames, fieldValues);        
    }

}
