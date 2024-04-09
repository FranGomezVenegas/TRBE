/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.TblsApp;
import databases.TblsDataAudit;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntTables;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.session.SessionAuditActions;

/**
 *
 * @author User
 */
public final class AuditUtilities {

    private AuditUtilities() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Object[] applyTheInsert(GenericAuditFields gAuditFlds, EnumIntTables tblObj, String[] fldN, Object[] fldV) {
        return applyTheInsert(gAuditFlds, tblObj, fldN, fldV, null);
    }

    public static Object[] applyTheInsert(GenericAuditFields gAuditFlds, EnumIntTables tblObj, String[] fldN, Object[] fldV, String externalProcInstanceName) {
        RdbmsObject insertDiagn = Rdbms.insertRecord(tblObj, fldN, fldV, externalProcInstanceName);
        if (Boolean.TRUE.equals(insertDiagn.getRunSuccess())) {
            SessionAuditActions auditActions = ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditActions();
            auditActions.addAuditAction(Integer.valueOf(insertDiagn.getNewRowId().toString()),
                    gAuditFlds.getActionName(), gAuditFlds.getActionPrettyNameEn(), gAuditFlds.getActionPrettyNameEs());
            Object[] trapMessage = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
            return LPArray.addValueToArray1D(trapMessage, insertDiagn);
        } else {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
        }
    }
    public static InternalMessage applyTheInsertInternalMessage(GenericAuditFields gAuditFlds, EnumIntTables tblObj, String[] fldN, Object[] fldV) {
        return applyTheInsertInternalMessage(gAuditFlds, tblObj, fldN, fldV, null);
    }

    public static InternalMessage applyTheInsertInternalMessage(GenericAuditFields gAuditFlds, EnumIntTables tblObj, String[] fldN, Object[] fldV, String externalProcInstanceName) {
        RdbmsObject insertDiagn = Rdbms.insertRecord(tblObj, fldN, fldV, externalProcInstanceName);
        if (Boolean.TRUE.equals(insertDiagn.getRunSuccess())) {
            SessionAuditActions auditActions = ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditActions();
            auditActions.addAuditAction(Integer.valueOf(insertDiagn.getNewRowId().toString()),
                    gAuditFlds.getActionName(), gAuditFlds.getActionPrettyNameEn(), gAuditFlds.getActionPrettyNameEs());
            Object[] trapMessage = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
            return new InternalMessage(insertDiagn.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), 
                insertDiagn.getErrorMessageVariables());
        } else {
            return new InternalMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
        }
    }

    public static String[] getUserSessionProceduresList(String[] tblFlds, Object[] fldVls) {
        char procsSeparator = (char) 34;
        if (LPArray.valueInArray(tblFlds, TblsApp.AppSession.PROCEDURES.getName())) {
            String usSessProcs = LPNulls.replaceNull(fldVls[LPArray.valuePosicInArray(tblFlds, TblsApp.AppSession.PROCEDURES.getName())]).toString();
            if (usSessProcs.length() > 0) {
                usSessProcs = usSessProcs.replace(String.valueOf(procsSeparator), "");
                return LPArray.getUniquesArray(usSessProcs.split("\\|"));
            }
        }
        return new String[]{};
    }

    public static String[] getAuditTableAllFields(String repository, String tableName) {
        if (GlobalVariables.Schemas.DATA.getName().equalsIgnoreCase(repository)) {
            switch (tableName.toLowerCase()) {
                case "sample":
                    return getAllFieldNames(TblsDataAudit.TablesDataAudit.SAMPLE.getTableFields());
                default:
                    return new String[]{TblsDataAudit.Sample.PERSON.getName(), TblsDataAudit.Sample.APP_SESSION_ID.getName(), TblsDataAudit.Sample.AUDIT_ID.getName(), TblsDataAudit.Sample.ACTION_NAME.getName()};
            }
        }
        return new String[]{TblsDataAudit.Sample.APP_SESSION_ID.getName(), TblsDataAudit.Sample.AUDIT_ID.getName(), TblsDataAudit.Sample.ACTION_NAME.getName()};
    }

    public static Boolean userSessionExistAtProcLevel(String procInstanceName, Integer sessionId) {

        Object[] existsRecord = Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsDataAudit.TablesDataAudit.SESSION.getTableName(),
                new String[]{TblsDataAudit.Session.SESSION_ID.getName()}, new Object[]{sessionId});
        return (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString()));
    }

}
