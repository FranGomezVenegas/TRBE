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
import org.json.simple.JSONArray;
import trazit.session.ProcedureRequestSession;
import static trazit.session.ProcedureRequestSession.getInstanceForActions;
import trazit.globalvariables.GlobalVariables;
/**
 * 
 * @author Fran Gomez
 * @version 0.1
 */
public class SampleAudit {       
    
    public enum SampleAuditErrorTrapping{ 
        AUDIT_RECORDS_PENDING_REVISION("auditRecordsPendingRevision", "The sample <*1*> has pending sign audit records.", "La muestra <*1*> tiene registros de auditoría sin firmar"),
        AUDIT_RECORD_NOT_FOUND("AuditRecordNotFound", "The audit record <*1*> for sample does not exist", "No encontrado un registro de audit para muestra con id <*1*>"),
        AUDIT_RECORD_ALREADY_REVIEWED("AuditRecordAlreadyReviewed", "The audit record <*1*> was reviewed therefore cannot be reviewed twice.", "El registro de audit para muestra con id <*1*> ya fue revisado, no se puede volver a revisar."),
        AUTHOR_CANNOT_BE_REVIEWER("AuditSamePersonCannotBeAuthorAndReviewer", "Same person cannot review its own actions", "La misma persona no puede revisar sus propias acciones"),
        PARAMETER_MISSING("sampleAuditRevisionMode_ParameterMissing", "", ""),
        DISABLED("sampleAuditRevisionMode_Disable", "", ""),
        STAGESDETECTED_BUT_SAMPLESTAGES_NOT_ENABLED("sampleAuditRevisionMode_StagesDetectedButSampleStagesNotEnable", "", ""),
        CURRENTSAMPLESTAGE_NOTREQUIRES_SAMPLEAUDITREVISION("currentSampleStageNotRequiresSampleAuditRevision", "", ""),
        ACTION_HAS_NO_SAMPLE_TEST_RESULT_LINKED("actionHasNoSampleIdTestIdResultIdLinked", "The action <*1*> has no sampleId, testId or resultId linked with so this method returns true doing nothing", ""),
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
        SET_SAMPLING_DATE_END, SAMPLE_CHANGE_SAMPLING_DATE_END,
        SAMPLE_RECEPTION_COMMENT_ADD, SAMPLE_RECEPTION_COMMENT_REMOVE, SAMPLE_EVALUATE_STATUS, SAMPLE_TESTINGGROUP_REVIEWED, SAMPLE_TESTINGGROUP_SET_AUTOAPPROVE, SAMPLE_TESTINGGROUP_SET_READY_REVISION, SAMPLE_REVIEWED,
        LOG_SAMPLE_ALIQUOT, LOG_SAMPLE_SUBALIQUOT, SAMPLESTAGE_MOVETONEXT, SAMPLESTAGE_MOVETOPREVIOUS,
        UPDATE_LAST_ANALYSIS_USER_METHOD, CHAIN_OF_CUSTODY_STARTED, CHAIN_OF_CUSTODY_COMPLETED, MICROORGANISM_ADDED, MICROORGANISM_REMOVED, 
        SAMPLE_SET_INCUBATION_STARTED, SAMPLE_SET_INCUBATION_ENDED, SAMPLE_CANCELED, SAMPLE_UNCANCELED, SAMPLE_UNREVIEWED, SAMPLE_SET_READY_FOR_REVISION,
        BATCH_SAMPLE_ADDED, BATCH_SAMPLE_REMOVED, BATCH_SAMPLE_MOVED_FROM, BATCH_SAMPLE_MOVED_TO, SAMPLE_AUTOAPPROVE, ADDED_TO_INVESTIGATION, INVESTIGATION_CLOSED}  

    public enum SampleAnalysisAuditEvents{ SAMPLE_ANALYSIS_REVIEWED, SAMPLE_ANALYSIS_EVALUATE_STATUS, SAMPLE_ANALYSIS_ANALYST_ASSIGNMENT, 
        SAMPLE_ANALYSIS_ADDED, SAMPLE_ANALYSIS_CANCELED, SAMPLE_ANALYSIS_UNCANCELED, SAMPLE_ANALYSIS_UNREVIEWED, SAMPLE_ANALYSIS_SET_READY_FOR_REVISION, SAMPLE_ANALYSIS_AUTOAPPROVE}
    
    public enum SampleAnalysisResultAuditEvents{BACK_FROM_CANCEL, SAMPLE_ANALYSIS_RESULT_ENTERED, UOM_CHANGED, 
        SAMPLE_ANALYSIS_RESULT_ADDED, SAMPLE_ANALYSIS_RESULT_CANCELED, SAMPLE_ANALYSIS_RESULT_UNCANCELED, SAMPLE_ANALYSIS_RESULT_REVIEWED}
      
    public enum SampleAuditBusinessRules{
        REVISION_MODE("sampleAuditRevisionMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        AUTHOR_CAN_REVIEW_AUDIT_TOO("sampleAuditAuthorCanBeReviewerToo", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        CHILD_REVISION_REQUIRED("sampleAuditChildRevisionRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|')
        ;
        private SampleAuditBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        public JSONArray getValuesList(){return this.valuesList;}
        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        
    }
/**
 * Add one record in the audit table when altering any of the levels belonging to the sample structure when not linked to any other statement.
 *      @param action String - Action being performed
 *      @param tableName String - table where the action was performed into the Sample structure
 *      @param tableId Integer - Id for the object where the action was performed.
 *      @param sampleId
 *      @param testId Integer - testId
 *      @param resultId Integer - resultId
 *      @param auditlog Object[] - All data that should be stored in the audit as part of the action being performed
 *      @param parentAuditId paranet audit id when creating a child-record
 * @return  
 */    
    public Object[] sampleAuditAdd(String action, String tableName, Integer tableId, 
                        Integer sampleId, Integer testId, Integer resultId, Object[] auditlog, Integer parentAuditId) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);

        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String[] fieldNames = new String[]{TblsDataAudit.Sample.FLD_DATE.getName(), TblsDataAudit.Sample.FLD_ACTION_NAME.getName(), 
            TblsDataAudit.Sample.FLD_TABLE_NAME.getName(), TblsDataAudit.Sample.FLD_TABLE_ID.getName(),
            TblsDataAudit.Sample.FLD_FIELDS_UPDATED.getName(), TblsDataAudit.Sample.FLD_USER_ROLE.getName(),
            TblsDataAudit.Sample.FLD_PERSON.getName(), TblsDataAudit.Sample.FLD_TRANSACTION_ID.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp(), action, tableName, tableId,
            Arrays.toString(auditlog), token.getUserRole(), token.getPersonName(), Rdbms.getTransactionId()};

        Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
            fieldNames = LPArray.addValueToArray1D(fieldNames, new String[]{TblsDataAudit.Sample.FLD_PROCEDURE.getName(), TblsDataAudit.Sample.FLD_PROCEDURE_VERSION.getName()});
            fieldValues = LPArray.addValueToArray1D(fieldValues, new Object[]{procedureInfo[0][0], procedureInfo[0][1]});
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
        if (token.getAppSessionId()!=null){
            Object[] appSession = LPSession.addProcessSession( LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.FLD_DATE_STARTED.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())){
                return appSession;
            }else{
                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));            
            }
        }
        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }  
        for (GlobalVariables.Languages curLang: GlobalVariables.Languages.values()){            
            Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), 
                    TblsDataAudit.Sample.TBL.getName(), TblsDataAudit.Sample.FLD_ACTION_PRETTY_EN.getName().replace("en", curLang.getName()));
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())){
                String propValue = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
                    "dataSampleAuditEvents", null, action, curLang.getName(), false);
                if (propValue==null || propValue.length()==0)propValue=action;
                fieldNames = LPArray.addValueToArray1D(fieldNames, 
                        TblsDataAudit.Sample.FLD_ACTION_PRETTY_EN.getName().replace("en", curLang.getName()));
                fieldValues = LPArray.addValueToArray1D(fieldValues, propValue);            
            }
        }
        AuditAndUserValidation auditAndUsrValid=getInstanceForActions(null, null, null).getAuditAndUsrValid();
        if (auditAndUsrValid!=null && auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }    
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsDataAudit.Sample.TBL.getName(), 
                fieldNames, fieldValues);
    }
    /**
     *
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
    public Object[] sampleAliquotingAuditAdd(String action, String tableName, Integer tableId, Integer subaliquotId, Integer aliquotId, Integer sampleId, Integer testId, Integer resultId, Object[] auditlog) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        
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
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_APP_SESSION_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, token.getAppSessionId());
        }    
        if (subaliquotId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_SUBALIQUOT_ID.getName());
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
        AuditAndUserValidation auditAndUsrValid=ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditAndUsrValid();
        if (auditAndUsrValid!=null && auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.FLD_REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }    
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsDataAudit.Sample.TBL.getName(),
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
        String auditAuthorCanBeReviewerMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleAuditBusinessRules.REVISION_MODE.getAreaName(), SampleAuditBusinessRules.REVISION_MODE.getTagName());  
        Object[][] auditInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsDataAudit.Sample.TBL.getName(), 
            new String[]{TblsDataAudit.Sample.FLD_AUDIT_ID.getName()}, new Object[]{auditId}, 
            new String[]{TblsDataAudit.Sample.FLD_PERSON.getName(), TblsDataAudit.Sample.FLD_REVIEWED.getName()}, new String[]{TblsDataAudit.Sample.FLD_AUDIT_ID.getName()});
        if (!"TRUE".equalsIgnoreCase(auditAuthorCanBeReviewerMode)){
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(auditInfo[0][0].toString())) return LPArray.array2dTo1d(auditInfo);
            if (personName.equalsIgnoreCase(auditInfo[0][0].toString())) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.AUTHOR_CANNOT_BE_REVIEWER.getErrorCode(), new Object[]{});
        }
        if (Boolean.valueOf(auditInfo[0][1].toString())){
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.AUDIT_RECORD_ALREADY_REVIEWED.getErrorCode(), new Object[]{auditId});              
        }
        return Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsDataAudit.Sample.TBL.getName(), 
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
        String auditRevisionMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleAuditBusinessRules.REVISION_MODE.getAreaName(), SampleAuditBusinessRules.REVISION_MODE.getTagName());  
        String auditRevisionChildRequired = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleAuditBusinessRules.CHILD_REVISION_REQUIRED.getAreaName(), SampleAuditBusinessRules.CHILD_REVISION_REQUIRED.getTagName());   
        if (auditRevisionMode==null || auditRevisionMode.length()==0) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.PARAMETER_MISSING.getErrorCode(), 
                  new Object[]{SampleAuditBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName(), procInstanceName});
        String[] auditRevisionModeArr= auditRevisionMode.split("\\|");
        Boolean auditRevisionModeRecognized=false;
        for (String curModeRequired: auditRevisionModesRequired){
          if (LPArray.valuePosicInArray(auditRevisionModeArr, curModeRequired)>-1) auditRevisionModeRecognized= true; 
        }
        if (!auditRevisionModeRecognized)return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.PARAMETER_MISSING.getErrorCode(),
                  new Object[]{SampleAuditBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName(), procInstanceName});
        if (LPArray.valuePosicInArray(auditRevisionModeArr, "DISABLE")>-1)return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, SampleAuditErrorTrapping.DISABLED.getErrorCode(), 
                  new Object[]{SampleAuditBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName(), procInstanceName});
        if (LPArray.valuePosicInArray(auditRevisionModeArr, "STAGES")>-1){
          DataSampleStages smpStages = new DataSampleStages();
          if (!smpStages.isSampleStagesEnable())return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.STAGESDETECTED_BUT_SAMPLESTAGES_NOT_ENABLED.getErrorCode(), 
                  new Object[]{SampleAuditBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName(), procInstanceName});
          Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), 
                  new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                  new String[]{TblsData.Sample.FLD_CURRENT_STAGE.getName()});
          String sampleCurrentStage=sampleInfo[0][0].toString();
          if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleCurrentStage)) return LPArray.array2dTo1d(sampleInfo);
          if (LPArray.valuePosicInArray(auditRevisionModeArr, sampleInfo[0][0].toString())==-1) return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, SampleAuditErrorTrapping.CURRENTSAMPLESTAGE_NOTREQUIRES_SAMPLEAUDITREVISION.getErrorCode(), 
                  new Object[]{sampleCurrentStage, sampleId, SampleAuditBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName(), procInstanceName});
        }
        if (LPArray.valuePosicInArray(auditRevisionModeArr, "ACTIONS")>-1){

        }
        String[] whereFieldName=new String[]{TblsDataAudit.Sample.FLD_SAMPLE_ID.getName()};
        Object[] whereFieldValue=new Object[]{sampleId, false};

        if ("FALSE".equalsIgnoreCase(auditRevisionChildRequired))
            whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsDataAudit.Sample.FLD_PARENT_AUDIT_ID.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause());
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsDataAudit.Sample.TBL.getName(), 
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
        if ( (sampleId==null || sampleId==0) && (testId==null || testId==0) && (resultId==null || resultId==0) )
                return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, SampleAuditErrorTrapping.ACTION_HAS_NO_SAMPLE_TEST_RESULT_LINKED.getErrorCode(), new Object[]{actionName});
        String[] auditRevisionModesRequired=new String[]{"ENABLE", "DISABLE"};
        String auditRevisionMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleAuditBusinessRules.REVISION_MODE.getAreaName(), SampleAuditBusinessRules.REVISION_MODE.getTagName());  
        String auditChildRevisionMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleAuditBusinessRules.CHILD_REVISION_REQUIRED.getAreaName(), SampleAuditBusinessRules.CHILD_REVISION_REQUIRED.getTagName());  
        if (auditRevisionMode==null || auditRevisionMode.length()==0) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.PARAMETER_MISSING.getErrorCode(), 
                  new Object[]{SampleAuditBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName(), procInstanceName});
        if (auditRevisionMode.equalsIgnoreCase("DISABLE"))
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "auditRevisionModeDisabled", null);
        String[] auditRevisionModeArr= auditRevisionMode.split("\\|");
        Boolean auditRevisionModeRecognized=false;
        for (String curModeRequired: auditRevisionModesRequired){
          if (LPArray.valuePosicInArray(auditRevisionModeArr, curModeRequired)>-1) auditRevisionModeRecognized= true; 
        }
        if (!auditRevisionModeRecognized)return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.PARAMETER_MISSING.getErrorCode(), 
                  new Object[]{SampleAuditBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName(), procInstanceName});
        Integer isActions=LPArray.valuePosicInArray(auditRevisionModeArr, "ACTIONS");
        Integer actionIsThere=LPArray.valuePosicInArray(auditRevisionModeArr, actionName);
        if ((LPArray.valuePosicInArray(auditRevisionModeArr, "ACTIONS")>-1) && (LPArray.valuePosicInArray(auditRevisionModeArr, actionName)==-1)) {
            
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
        
        if ("FALSE".equalsIgnoreCase(auditChildRevisionMode))
            whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsDataAudit.Sample.FLD_PARENT_AUDIT_ID.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause());
        Object[][] sampleAuditInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsDataAudit.Sample.TBL.getName(), 
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
