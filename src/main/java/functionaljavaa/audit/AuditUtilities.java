/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import databases.Rdbms;
import static databases.Rdbms.dbSchemaTablesList;
import databases.TblsApp;
import databases.TblsDataAudit;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author User
 */
public class AuditUtilities {
    public static String[] getUserSessionProceduresList(String[] tblFlds, Object[] fldVls){
        char procsSeparator = (char)34;
        if (LPArray.valueInArray(tblFlds, TblsApp.AppSession.FLD_PROCEDURES.getName())){
            String usSessProcs=LPNulls.replaceNull(fldVls[LPArray.valuePosicInArray(tblFlds, TblsApp.AppSession.FLD_PROCEDURES.getName())]).toString();
            if (usSessProcs.length()>0){
                usSessProcs=usSessProcs.replace(String.valueOf(procsSeparator), "");
                return LPArray.getUniquesArray(usSessProcs.split("\\|"));
            }
        }
        return new String[]{};
    }
    public static Object[] getProcAuditTablesList(String schemaPrefix){
        if (schemaPrefix.length()>0)
            return dbSchemaTablesList(schemaPrefix);
        return new Object[]{};
    }
    public static String[] getAuditTableAllFields(String repository, String tableName){
        switch (repository.toLowerCase()){
            case LPPlatform.SCHEMA_DATA:   
                switch (tableName.toLowerCase()){
                    case "sample":
                        return TblsDataAudit.Sample.getAllFieldNames();
                    default:
                        return new String[]{TblsDataAudit.Sample.FLD_PERSON.getName(), TblsDataAudit.Sample.FLD_APP_SESSION_ID.getName(), TblsDataAudit.Sample.FLD_AUDIT_ID.getName(), TblsDataAudit.Sample.FLD_ACTION_NAME.getName()};                    
                }
                    
            default:
                return new String[]{TblsDataAudit.Sample.FLD_APP_SESSION_ID.getName(), TblsDataAudit.Sample.FLD_AUDIT_ID.getName(), TblsDataAudit.Sample.FLD_ACTION_NAME.getName()};
        }
    }
    
    
    public static Boolean userSessionExistAtProcLevel(String schemaPrefix, Integer sessionId){
        Object[] existsRecord = Rdbms.existsRecord(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA_AUDIT), TblsDataAudit.Session.TBL.getName(),
                new String[]{TblsDataAudit.Session.FLD_SESSION_ID.getName()}, new Object[]{sessionId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())) return true;
        return false;
    }
    
}
