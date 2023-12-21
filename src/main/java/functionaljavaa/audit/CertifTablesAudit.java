package functionaljavaa.audit;

import databases.TblsDataAudit;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTables;
import trazit.session.ProcedureRequestSession;
/** 
 *
 * @author User
 */
public final class CertifTablesAudit {
    private CertifTablesAudit() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] certifTablsAudit(EnumIntTables tblObj, Integer certifId, String action, String objectFldName, String objectName, String objectVersionFldName, Integer objectVersion, Object[] auditlog, Integer trainingId, Integer parentAuditId, String note) {
        GenericAuditFields gAuditFlds=new GenericAuditFields(auditlog);

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, new String[]{TblsDataAudit.CertifUserAnalysisMethod.TABLE_ID.getName(), TblsDataAudit.CertifUserAnalysisMethod.CERTIF_ID.getName()});
        fieldValues = LPArray.addValueToArray1D(fieldValues, new Object[]{certifId, certifId});

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
        if (objectVersion!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, objectVersionFldName);
            fieldValues = LPArray.addValueToArray1D(fieldValues, objectVersion);            
        }
        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.CertifUserAnalysisMethod.PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }    
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        return AuditUtilities.applyTheInsert(gAuditFlds, tblObj, fieldNames, fieldValues, procInstanceName);
    }    
}
