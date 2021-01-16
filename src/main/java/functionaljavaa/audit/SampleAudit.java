/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsApp;
import databases.TblsData;
import databases.TblsDataAudit;
import databases.Token;
import functionaljavaa.parameter.Parameter;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPSession;
import java.util.Arrays;
import functionaljavaa.requirement.Requirement;
import functionaljavaa.samplestructure.DataSampleStages;
import lbplanet.utilities.LPDate;
import trazit.session.ProcedureRequestSession;

/**
 * 
 * @author Fran Gomez
 * @version 0.1
 */
public class SampleAudit {
    
    /**
     *
     */
 
    public enum SampleAuditErrorTrapping{ 
        AUDIT_RECORDS_PENDING_REVISION("auditRecordsPendingRevision", "The sample <*1*> has pending sign audit records.", "La muestra <*1*> tiene registros de auditoría sin firmar"),
        AUDIT_RECORD_NOT_FOUND("AuditRecordNotFound", "The audit record <*1*> for sample does not exist", "No encontrado un registro de audit para muestra con id <*1*>"),
        AUDIT_RECORD_ALREADY_REVIEWED("AuditRecordAlreadyReviewed", "The audit record <*1*> was reviewed therefore cannot be reviewed twice.", "El registro de audit para muestra con id <*1*> ya fue revisado, no se puede volver a revisar."),
        AUTHOR_CANNOT_BE_REVIEWER("AuditSamePersonCannotBeAuthorAndReviewer", "Same person cannot review its own actions", "La misma persona no puede revisar sus propias acciones")
        //INCUBATORBATCH_ALREADY_STARTED("IncubatorBatchAlreadyStarted", "The batch <*1*> was already started and cannot be started twice for procedure <*2*>", "La tanda <*1*> no está iniciada todavía para el proceso <*2*>"),
        //INCUBATORBATCH_ALREADY_IN_PROCESS("IncubatorBatchAlreadyInProcess", "The batch <*1*> is already in process for incubator <*2*> and start multiples batches per incubator is not allowed for the procedure <*3*>", "")
        ;
        private SampleAuditErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        public String getErrorCode(){return this.errorCode;}
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }    
    public enum SampleAuditEvents{SAMPLE_LOGGED, SAMPLE_RECEIVED, SET_SAMPLING_DATE, SAMPLE_CHANGE_SAMPLING_DATE,
        SAMPLE_RECEPTION_COMMENT_ADD, SAMPLE_RECEPTION_COMMENT_REMOVE, SAMPLE_EVALUATE_STATUS, SAMPLE_TESTINGGROUP_REVIEWED, SAMPLE_TESTINGGROUP_SET_READY_REVISION, SAMPLE_REVIEWED,
        LOG_SAMPLE_ALIQUOT, LOG_SAMPLE_SUBALIQUOT, SAMPLESTAGE_MOVETONEXT, SAMPLESTAGE_MOVETOPREVIOUS,
        UPDATE_LAST_ANALYSIS_USER_METHOD, CHAIN_OF_CUSTODY_STARTED, CHAIN_OF_CUSTODY_COMPLETED, MICROORGANISM_ADDED, 
        SAMPLE_SET_INCUBATION_STARTED, SAMPLE_SET_INCUBATION_ENDED, SAMPLE_CANCELED, SAMPLE_UNCANCELED, SAMPLE_SET_READY_FOR_REVISION,
        BATCH_SAMPLE_ADDED, BATCH_SAMPLE_REMOVED, BATCH_SAMPLE_MOVED_FROM, BATCH_SAMPLE_MOVED_TO}  

    /**
     *
     */
    public enum SampleAnalysisAuditEvents{ SAMPLE_ANALYSIS_REVIEWED, SAMPLE_ANALYSIS_EVALUATE_STATUS, SAMPLE_ANALYSIS_ANALYST_ASSIGNMENT, 
        SAMPLE_ANALYSIS_ADDED, SAMPLE_ANALYSIS_CANCELED, SAMPLE_ANALYSIS_UNCANCELED, SAMPLE_ANALYSIS_SET_READY_fOR_REVISION}
    
    /**
     *
     */
    public enum SampleAnalysisResultAuditEvents{ 

        /**
         *
         */
        BACK_FROM_CANCEL, 

        /**
         *
         */
        SAMPLE_ANALYSIS_RESULT_ENTERED, 

        /**
         *
         */
        UOM_CHANGED, 

        /**
         *
         */
        SAMPLE_ANALYSIS_RESULT_CANCELED, 

        /**
         *
         */
        SAMPLE_ANALYSIS_RESULT_UNCANCELED, 

        /**
         *
         */
        SAMPLE_ANALYSIS_RESULT_REVIEWED}
    String classVersion = "0.1";
    Integer auditId=0;
      
    /**
     *
     */
    public static final String PARAMETER_BUNDLE_SAMPLE_AUDIT_REVISION_MODE= "sampleAuditRevisionMode";
    public static final String PARAMETER_BUNDLE_SAMPLE_AUDIT_AUTHOR_CAN_REVIEW_TOO= "sampleAuditAuthorCanBeReviewerToo";
    public static final String PARAMETER_BUNDLE_SAMPLE_AUDIT_CHILD_REVISION_REQUIRED= "sampleAuditChildRevisionRequired";
    
    

/**
 * Add one record in the audit table when altering any of the levels belonging to the sample structure when not linked to any other statement.
 * @param action String - Action being performed
 * @param tableName String - table where the action was performed into the Sample structure
 * @param tableId Integer - Id for the object where the action was performed.
     * @param sampleId
 * @param testId Integer - testId
 * @param resultId Integer - resultId
 * @param auditlog Object[] - All data that should be stored in the audit as part of the action being performed
@param parentAuditId paranet audit id when creating a child-record
     * @return  
 */    
    public Object[] sampleAuditAdd(String action, String tableName, Integer tableId, 
                        Integer sampleId, Integer testId, Integer resultId, Object[] auditlog, Integer parentAuditId) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String[] fieldNames = new String[]{TblsDataAudit.Sample.FLD_DATE.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};
        
        Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_PROCEDURE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_PROCEDURE_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
        }        
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        if (sampleId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_SAMPLE_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, sampleId);
        }    
        if (testId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_TEST_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, testId);
        }    
        if (resultId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_RESULT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, resultId);
        }    
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_USER_ROLE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        if (token.getAppSessionId()!=null){
            Object[] appSession = LPSession.addProcessSession( LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA_AUDIT), Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.FLD_DATE_STARTED.getName()});
       
    //        Object[] appSession = labSession.getAppSession(appSessionId, new String[]{"date_started"});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())){
                return appSession;
            }else{

                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));            
            }
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }    
        AuditAndUserValidation auditAndUsrValid=AuditAndUserValidation.getInstanceForActions(null, null, null);
        if (auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }    
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA_AUDIT), TblsDataAudit.Sample.TBL.getName(), 
                fieldNames, fieldValues);
        
    }

    /**
     *
     * @param procInstanceName
     * @param action
     * @param tableName
     * @param tableId
     * @param aliquotId
     * @param sampleId
     * @param testId
     * @param resultId
     * @param auditlog
     * @param userName
     * @param userRole
     * @param sessionId
     */
    public void sampleAuditAddObsolete(String procInstanceName, String action, String tableName, Integer tableId, Integer aliquotId, Integer sampleId, Integer testId, Integer resultId, Object[] auditlog, String userName, String userRole, Integer sessionId) {
        
        String[] fieldNames = new String[0];
        Object[] fieldValues = new Object[0];
        
        Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_PROCEDURE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_PROCEDURE_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
        }        
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        if (sessionId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, "session_id");
            fieldValues = LPArray.addValueToArray1D(fieldValues, sessionId);
        }    
        if (aliquotId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, "aliquot_id");
            fieldValues = LPArray.addValueToArray1D(fieldValues, aliquotId);
        }    
        if (sampleId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_SAMPLE_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, sampleId);
        }    
        if (testId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_TEST_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, testId);
        }    
        if (resultId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_RESULT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, resultId);
        }    
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_USER_ROLE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, userRole);

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, userName);
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
                
/*        String jsonString = null;
        jsonString = sampleJsonString(procInstanceName+"-data", sampleId);
        if ((jsonString!=null)){
        //if (!jsonString.isEmpty()){
            fieldNames = LPArray.addValueToArray1D(fieldNames, "picture_after");
            fieldValues = LPArray.addValueToArray1D(fieldValues, jsonString);            
        }
*/        
//        fieldNames = LPArray.addValueToArray1D(fieldNames, "user");
//        fieldValues = LPArray.addValueToArray1D(fieldValues, userName);        
           Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA_AUDIT), TblsDataAudit.Sample.TBL.getName(),
                   fieldNames, fieldValues);
    }

    /**
     *
     * @param procInstanceName
     * @param token
     * @param action
     * @param tableName
     * @param tableId
     * @param subaliquotId
     * @param aliquotId
     * @param sampleId
     * @param testId
     * @param resultId
     * @param auditlog
     */
    public void sampleAliquotingAuditAdd( String procInstanceName, Token token, String action, String tableName, Integer tableId, Integer subaliquotId, Integer aliquotId, Integer sampleId, Integer testId, Integer resultId, Object[] auditlog) {
        
        String[] fieldNames = new String[]{TblsDataAudit.Sample.FLD_DATE.getName(), TblsDataAudit.Sample.FLD_ACTION_NAME.getName(), TblsDataAudit.Sample.FLD_TABLE_NAME.getName(),
          TblsDataAudit.Sample.FLD_TABLE_ID.getName(), TblsDataAudit.Sample.FLD_FIELDS_UPDATED.getName(), TblsDataAudit.Sample.FLD_USER_ROLE.getName(),
          TblsDataAudit.Sample.FLD_PERSON.getName(), TblsDataAudit.Sample.FLD_TRANSACTION_ID.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp(), action, tableName, tableId, Arrays.toString(auditlog), token.getUserRole(), token.getPersonName(), Rdbms.getTransactionId()};

        Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_PROCEDURE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_PROCEDURE_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
        }                
        if (token.getAppSessionId()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, "session_id");
            fieldValues = LPArray.addValueToArray1D(fieldValues, token.getAppSessionId());
        }    
        if (subaliquotId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, "subaliquot_id");
            fieldValues = LPArray.addValueToArray1D(fieldValues, subaliquotId);
        }    
        if (aliquotId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, "aliquot_id");
            fieldValues = LPArray.addValueToArray1D(fieldValues, aliquotId);
        }    
        if (sampleId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_SAMPLE_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, sampleId);
        }    
        if (testId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_TEST_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, testId);
        }    
        if (resultId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_RESULT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, resultId);
        }                   
        AuditAndUserValidation auditAndUsrValid=AuditAndUserValidation.getInstanceForActions(null, null, null);
        if (auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }    
        Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA_AUDIT), TblsDataAudit.Sample.TBL.getName(),
            fieldNames, fieldValues);
    }
    
    
/*    public void sampleAuditAdd( String procInstanceName, String action, String tableName, Integer tableId, Integer sampleId, Integer testId, Integer resultId, Object[] auditlog, String userName, String userRole) {
        String auditTableName = TABLE_NAME_DATA_AUDIT_SAMPLE;
        String schemaName = LPPlatform.SCHEMA_DATA_AUDIT;                
        schemaName = LPPlatform.buildSchemaName(procInstanceName, schemaName);                
        
        String[] fieldNames = new String[0];
        Object[] fieldValues = new Object[0];
        
        Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
            fieldNames = LPArray.addValueToArray1D(fieldNames, FIELD_NAME_DATA_AUDIT_SAMPLE_PROCEDURE);
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
            fieldNames = LPArray.addValueToArray1D(fieldNames, FIELD_NAME_DATA_AUDIT_SAMPLE_PROCEDURE_VERSION);
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
        }        
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, FIELD_NAME_DATA_AUDIT_SAMPLE_ACTION_NAME);
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
        fieldNames = LPArray.addValueToArray1D(fieldNames, FIELD_NAME_DATA_AUDIT_SAMPLE_TABLE_NAME);
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, FIELD_NAME_DATA_AUDIT_SAMPLE_TABLE_ID);
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        if (sampleId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, FIELD_NAME_DATA_AUDIT_SAMPLE_SAMPLE_ID);
            fieldValues = LPArray.addValueToArray1D(fieldValues, sampleId);
        }    
        if (testId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, FIELD_NAME_DATA_AUDIT_SAMPLE_TEST_ID);
            fieldValues = LPArray.addValueToArray1D(fieldValues, testId);
        }    
        if (resultId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, FIELD_NAME_DATA_AUDIT_SAMPLE_RESULT_ID);
            fieldValues = LPArray.addValueToArray1D(fieldValues, resultId);
        }    
        fieldNames = LPArray.addValueToArray1D(fieldNames, FIELD_NAME_DATA_AUDIT_SAMPLE_FIELDS_UPDATED);
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));
        fieldNames = LPArray.addValueToArray1D(fieldNames, FIELD_NAME_DATA_AUDIT_SAMPLE_USER_ROLE);
        fieldValues = LPArray.addValueToArray1D(fieldValues, userRole);

        fieldNames = LPArray.addValueToArray1D(fieldNames, FIELD_NAME_DATA_AUDIT_SAMPLE_PERSON);
        fieldValues = LPArray.addValueToArray1D(fieldValues, userName);
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, FIELD_NAME_DATA_AUDIT_SAMPLE_TRANSACTION_ID);
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
                
//        String jsonString = null;
//        jsonString = sampleJsonString(procInstanceName+"-data", sampleId);
//        if ((jsonString!=null)){
//        //if (!jsonString.isEmpty()){
//            fieldNames = LPArray.addValueToArray1D(fieldNames, "picture_after");
//            fieldValues = LPArray.addValueToArray1D(fieldValues, jsonString);            
//        }
        
//        fieldNames = LPArray.addValueToArray1D(fieldNames, "user");
//        fieldValues = LPArray.addValueToArray1D(fieldValues, userName);        
           Rdbms.insertRecordInTable(schemaName, auditTableName, fieldNames, fieldValues);
    }
*/
 /**
 * Not recommended. reduced version of
 * @param procInstanceName String - Procedure Name
 * @param action String - Action being performed
 * @param tableName String - table where the action was performed into the Sample structure
 * @param tableId Integer - Id for the object where the action was performed.
 * @param auditlog Object[] - All data that should be stored in the audit as part of the action being performed
 * @param userName String - User who performed the action.
 */    
    public void sampleAuditAddObsolete( String procInstanceName, String action, String tableName, Integer tableId, Object[] auditlog, String userName){
        
        String[] fieldNames = new String[]{TblsDataAudit.Sample.FLD_ACTION_NAME.getName(), TblsDataAudit.Sample.FLD_TABLE_NAME.getName(),
          TblsDataAudit.Sample.FLD_TABLE_ID.getName(), TblsDataAudit.Sample.FLD_SAMPLE_ID.getName()};
        Object[] fieldValues = new Object[]{action, tableName, tableId, tableId};
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
//        fieldNames = LPArray.addValueToArray1D(fieldNames, "user");
//        fieldValues = LPArray.addValueToArray1D(fieldValues, userName);        
           Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA_AUDIT), TblsDataAudit.Sample.TBL.getName(),
                   fieldNames, fieldValues);
    }

    /**
     *
     * @param procInstanceName
     * @param auditId
     * @param personName
     * @return
     */
    public static Object[] sampleAuditSetAuditRecordAsReviewed(String procInstanceName, Integer auditId, String personName){
        String auditAuthorCanBeReviewerMode = Parameter.getParameterBundle("config", procInstanceName, "procedure", PARAMETER_BUNDLE_SAMPLE_AUDIT_AUTHOR_CAN_REVIEW_TOO, null);  
        Object[][] auditInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA_AUDIT), TblsDataAudit.Sample.TBL.getName(), 
            new String[]{TblsDataAudit.Sample.FLD_AUDIT_ID.getName()}, new Object[]{auditId}, 
            new String[]{TblsDataAudit.Sample.FLD_PERSON.getName(), TblsDataAudit.Sample.FLD_REVIEWED.getName()}, new String[]{TblsDataAudit.Sample.FLD_AUDIT_ID.getName()});
        if (!"TRUE".equalsIgnoreCase(auditAuthorCanBeReviewerMode)){
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(auditInfo[0][0].toString())) return LPArray.array2dTo1d(auditInfo);
            if (personName.equalsIgnoreCase(auditInfo[0][0].toString())) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.AUTHOR_CANNOT_BE_REVIEWER.getErrorCode(), new Object[]{});
        }
        if (Boolean.valueOf(auditInfo[0][1].toString())){
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.AUDIT_RECORD_ALREADY_REVIEWED.getErrorCode(), new Object[]{auditId});              
        }
        return Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA_AUDIT), TblsDataAudit.Sample.TBL.getName(), 
            new String[]{TblsDataAudit.Sample.FLD_REVIEWED.getName(), TblsDataAudit.Sample.FLD_REVIEWED_BY.getName(), TblsDataAudit.Sample.FLD_REVIEWED_ON.getName()}, 
            new Object[]{true, personName, LPDate.getCurrentTimeStamp()}, 
            new String[]{TblsDataAudit.Sample.FLD_AUDIT_ID.getName()}, new Object[]{auditId});
    }
    
    /**
     *
     * @param sampleId
     * @return
     */
    public static Object[] sampleAuditRevisionPass(Integer sampleId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] auditRevisionModesRequired=new String[]{"ENABLE", "DISABLE"};
        String auditRevisionMode = Parameter.getParameterBundle("config", procInstanceName, "procedure", PARAMETER_BUNDLE_SAMPLE_AUDIT_REVISION_MODE, null);  
        String auditRevisionChildRequired = Parameter.getParameterBundle("config", procInstanceName, "procedure", PARAMETER_BUNDLE_SAMPLE_AUDIT_CHILD_REVISION_REQUIRED, null);   
        if (auditRevisionMode==null || auditRevisionMode.length()==0) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleAuditRevisionMode_ParameterMissing", 
                  new Object[]{PARAMETER_BUNDLE_SAMPLE_AUDIT_REVISION_MODE, procInstanceName});
        String[] auditRevisionModeArr= auditRevisionMode.split("\\|");
        Boolean auditRevisionModeRecognized=false;
        for (String curModeRequired: auditRevisionModesRequired){
          if (LPArray.valuePosicInArray(auditRevisionModeArr, curModeRequired)>-1) auditRevisionModeRecognized= true; 
        }
        if (!auditRevisionModeRecognized)return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleAuditRevisionMode_ParameterMissing", 
                  new Object[]{PARAMETER_BUNDLE_SAMPLE_AUDIT_REVISION_MODE, procInstanceName});
        if (LPArray.valuePosicInArray(auditRevisionModeArr, "DISABLE")>-1)return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "sampleAuditRevisionMode_Disable", 
                  new Object[]{PARAMETER_BUNDLE_SAMPLE_AUDIT_REVISION_MODE, procInstanceName});
        if (LPArray.valuePosicInArray(auditRevisionModeArr, "STAGES")>-1){
          DataSampleStages smpStages = new DataSampleStages();
          if (!smpStages.isSampleStagesEnable())return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleAuditRevisionMode_StagesDetectedButSampleStagesNotEnable", 
                  new Object[]{PARAMETER_BUNDLE_SAMPLE_AUDIT_REVISION_MODE, procInstanceName});
          Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsData.Sample.TBL.getName(), 
                  new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                  new String[]{TblsData.Sample.FLD_CURRENT_STAGE.getName()});
          String sampleCurrentStage=sampleInfo[0][0].toString();
          if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleCurrentStage)) return LPArray.array2dTo1d(sampleInfo);
          if (LPArray.valuePosicInArray(auditRevisionModeArr, sampleInfo[0][0].toString())==-1) return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "currentSampleStageNotRequiresSampleAuditRevision", 
                  new Object[]{sampleCurrentStage, sampleId, PARAMETER_BUNDLE_SAMPLE_AUDIT_REVISION_MODE, procInstanceName});
        }
        if (LPArray.valuePosicInArray(auditRevisionModeArr, "ACTIONS")>-1){

        }
        String[] whereFieldName=new String[]{TblsDataAudit.Sample.FLD_SAMPLE_ID.getName()};
        Object[] whereFieldValue=new Object[]{sampleId, false};

        if ("FALSE".equalsIgnoreCase(auditRevisionChildRequired))
            whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsDataAudit.Sample.FLD_PARENT_AUDIT_ID.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause());
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA_AUDIT), TblsDataAudit.Sample.TBL.getName(), 
                whereFieldName, whereFieldValue, 
                new String[]{TblsDataAudit.Sample.FLD_AUDIT_ID.getName(), TblsDataAudit.Sample.FLD_REVIEWED.getName()});
        for (Object[] curSampleInfo: sampleInfo){
          if (!"true".equalsIgnoreCase(curSampleInfo[1].toString())) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.AUDIT_RECORDS_PENDING_REVISION.getErrorCode(), 
            new Object[]{sampleId, procInstanceName});
          }
        }      
    //      Object[] sampleAuditReviewedValues=LPArray.getUniquesArray(sampleInfoReviewed1D);
    //      if ( (sampleAuditReviewedValues.length!=1) || ( (sampleAuditReviewedValues.length==1) && !("true".equalsIgnoreCase(sampleAuditReviewedValues[0].toString())) ) )
        return new Object[]{LPPlatform.LAB_TRUE, "All reviewed"};
    }  
    
    /**
     *
     * @param procInstanceName
     * @param sampleId
     * @param actionName
     * @param testId
     * @param resultId
     * @return
     */
    public static Object[] sampleAuditRevisionPassByAction(String procInstanceName, String actionName, Integer sampleId, Integer testId, Integer resultId){
        
        
//if (1==1) return new Object[]{LPPlatform.LAB_TRUE, "All reviewed"};        
        
        if ( (sampleId==null || sampleId==0) && (testId==null || testId==0) && (resultId==null || resultId==0) )
                return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "The action "+actionName+" has no sampleId, testId or resultId linked with so this method returns true doing nothing", null);
        String[] auditRevisionModesRequired=new String[]{"ENABLE", "DISABLE"};
        String auditRevisionMode = Parameter.getParameterBundle("config", procInstanceName, "procedure", PARAMETER_BUNDLE_SAMPLE_AUDIT_REVISION_MODE, null);  
        String auditRevisionChildRequired = Parameter.getParameterBundle("config", procInstanceName, "procedure", PARAMETER_BUNDLE_SAMPLE_AUDIT_CHILD_REVISION_REQUIRED, null);   
        if (auditRevisionMode==null || auditRevisionMode.length()==0) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleAuditRevisionMode_ParameterMissing", 
                  new Object[]{PARAMETER_BUNDLE_SAMPLE_AUDIT_REVISION_MODE, procInstanceName});
        String[] auditRevisionModeArr= auditRevisionMode.split("\\|");
        Boolean auditRevisionModeRecognized=false;
        for (String curModeRequired: auditRevisionModesRequired){
          if (LPArray.valuePosicInArray(auditRevisionModeArr, curModeRequired)>-1) auditRevisionModeRecognized= true; 
        }
        if (!auditRevisionModeRecognized)return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleAuditRevisionMode_ParameterMissing", 
                  new Object[]{PARAMETER_BUNDLE_SAMPLE_AUDIT_REVISION_MODE, procInstanceName});
        if (LPArray.valuePosicInArray(auditRevisionModeArr, "ACTIONS")==-1){
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, SampleAuditErrorTrapping.AUDIT_RECORDS_PENDING_REVISION.getErrorCode(), new Object[]{sampleId, procInstanceName});

        }
        String[] whereFieldName=new String[]{TblsDataAudit.Sample.FLD_REVIEWED.getName()};
        Object[] whereFieldValue=new Object[]{false};
        if (sampleId!=null && sampleId!=0){
            whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsDataAudit.Sample.FLD_SAMPLE_ID.getName());
            whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, sampleId);
        }
        if (testId!=null && testId!=0){
            whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsDataAudit.Sample.FLD_TEST_ID.getName());
            whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, testId);
        }
        if (resultId!=null && resultId!=0){
            whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsDataAudit.Sample.FLD_RESULT_ID.getName());
            whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, resultId);
        }
//        if ("FALSE".equalsIgnoreCase(auditRevisionChildRequired))
//            whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsDataAudit.Sample.FLD_PARENT_AUDIT_ID.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause());
        Object[][] sampleAuditInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA_AUDIT), TblsDataAudit.Sample.TBL.getName(), 
                whereFieldName, whereFieldValue, 
                new String[]{TblsDataAudit.Sample.FLD_AUDIT_ID.getName(), TblsDataAudit.Sample.FLD_REVIEWED.getName()});
        for (Object[] curSampleInfo: sampleAuditInfo){
          if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(curSampleInfo[0].toString())) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.AUDIT_RECORDS_PENDING_REVISION.getErrorCode(), 
            new Object[]{sampleId, procInstanceName});
          }
        }      
    //      Object[] sampleAuditReviewedValues=LPArray.getUniquesArray(sampleInfoReviewed1D);
    //      if ( (sampleAuditReviewedValues.length!=1) || ( (sampleAuditReviewedValues.length==1) && !("true".equalsIgnoreCase(sampleAuditReviewedValues[0].toString())) ) )
        return new Object[]{LPPlatform.LAB_TRUE, "All reviewed"};
    }  
    
    
    
}
