/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.certification;

import databases.TblsDataAudit;
import functionaljavaa.audit.AuditUtilities;
import functionaljavaa.audit.GenericAuditFields;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntAuditEvents;

public final class UserCertificationTracking {
    private UserCertificationTracking() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] userCertificationTrackingAuditAdd(EnumIntAuditEvents action, String objectType, String userName, String objectName, Integer objectId,
                        String[] fldNames, Object[] fldValues) {
        GenericAuditFields gAuditFlds=new GenericAuditFields(action, fldNames, fldValues, userName, false);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.UserCertifTrack.OBJECT_TYPE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, objectType);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.UserCertifTrack.OBJECT_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, objectName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.UserCertifTrack.OBJECT_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, objectId);
        Object[] insertRecordInfo=AuditUtilities.applyTheInsert(gAuditFlds, TblsDataAudit.TablesDataAudit.USER_CERTIF_TRACK, fieldNames, fieldValues);
        return insertRecordInfo;
    }       
}
