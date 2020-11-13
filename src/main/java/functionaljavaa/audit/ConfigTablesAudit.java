/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitDataAudit;
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

/**
 *
 * @author User
 */
public class ConfigTablesAudit {
    
    public enum AnalysisAuditEvents{
        ANALYSIS_NEW, ANALYSIS_UPDATE, ANALYSIS_METHOD_NEW, ANALYSIS_METHOD_UPDATE, ANALYSIS_METHOD_DELETE,
        ANALYSIS_METHOD_PARAM_NEW, ANALYSIS_METHOD_PARAM_UPDATE, ANALYSIS_METHOD_PARAM_DELETE
    }    
    public enum SpecAuditEvents{
        SPEC_NEW, SPEC_UPDATE, SPEC_LIMIT_NEW
    }    
/**
 * Add one record in the audit table when altering any of the levels belonging to the sample structure when not linked to any other statement.
 * @param schemaPrefix String - Procedure Name
 * @param action String - Action being performed
 * @param tableName String - table where the action was performed into the Sample structure
 * @param tableId Integer - Id for the object where the action was performed.
 * @param auditlog Object[] - All data that should be stored in the audit as part of the action being performed
     * @param specCode
     * @param specConfigVersion
     * @param token
     * @param parentAuditId 
     * @return  
 */    
    public static Object[] analysisAuditAdd(String schemaPrefix, Token token, String action, String tableName, String tableId, 
                        String specCode, Integer specConfigVersion, Object[] auditlog, Integer parentAuditId) {
        String[] fieldNames = new String[]{TblsCnfgAudit.Analysis.FLD_DATE.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};
        
        Object[][] procedureInfo = Requirement.getProcedureBySchemaPrefix(schemaPrefix);
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FLD_PROCEDURE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FLD_PROCEDURE_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
        }        
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FLD_ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FLD_TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FLD_TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        if (specCode!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FLD_CODE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, specCode);
        }    
        if (specConfigVersion!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FLD_CONFIG_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, specConfigVersion);
        }    
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FLD_FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FLD_USER_ROLE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FLD_PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        if (token.getAppSessionId()!=null){
            Object[] appSession = LPSession.addProcessSession( LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA_AUDIT), Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.FLD_DATE_STARTED.getName()});
       
    //        Object[] appSession = labSession.getAppSession(appSessionId, new String[]{"date_started"});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())){
                return appSession;
            }else{

                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FLD_APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));            
            }
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FLD_TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FLD_PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }    
        AuditAndUserValidation auditAndUsrValid=AuditAndUserValidation.getInstance(null, null, null);
        if (auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Analysis.FLD_REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }    
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG_AUDIT), TblsCnfgAudit.Analysis.TBL.getName(), 
                fieldNames, fieldValues);
    }
    
/**
 * Add one record in the audit table when altering any of the levels belonging to the sample structure when not linked to any other statement.
 * @param schemaPrefix String - Procedure Name
 * @param action String - Action being performed
 * @param tableName String - table where the action was performed into the Sample structure
 * @param tableId Integer - Id for the object where the action was performed.
 * @param auditlog Object[] - All data that should be stored in the audit as part of the action being performed
     * @param specCode
     * @param specConfigVersion
     * @param token
     * @param parentAuditId 
     * @return  
 */    
    public static Object[] specAuditAdd(String schemaPrefix, Token token, String action, String tableName, String tableId, 
                        String specCode, Integer specConfigVersion, Object[] auditlog, Integer parentAuditId) {
        String[] fieldNames = new String[]{TblsCnfgAudit.Spec.FLD_DATE.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};
        
        Object[][] procedureInfo = Requirement.getProcedureBySchemaPrefix(schemaPrefix);
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FLD_PROCEDURE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FLD_PROCEDURE_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
        }        
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FLD_ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FLD_TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FLD_TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        if (specCode!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FLD_SPEC_CODE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, specCode);
        }    
        if (specConfigVersion!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FLD_SPEC_CONFIG_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, specConfigVersion);
        }    
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FLD_FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FLD_USER_ROLE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FLD_PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        if (token.getAppSessionId()!=null){
            Object[] appSession = LPSession.addProcessSession( LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA_AUDIT), Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.FLD_DATE_STARTED.getName()});
       
    //        Object[] appSession = labSession.getAppSession(appSessionId, new String[]{"date_started"});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())){
                return appSession;
            }else{

                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FLD_APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));            
            }
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FLD_TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FLD_PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }    
        AuditAndUserValidation auditAndUsrValid=AuditAndUserValidation.getInstance(null, null, null);
        if (auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsCnfgAudit.Spec.FLD_REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }    
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG_AUDIT), TblsCnfgAudit.Spec.TBL.getName(), 
                fieldNames, fieldValues);
    }
    
}
