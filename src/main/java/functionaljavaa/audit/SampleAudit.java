/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.SqlWhere;
import databases.TblsData;
import databases.TblsDataAudit;
import databases.features.Token;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.isTagValueOneOfEnableOnes;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import java.util.Arrays;
import functionaljavaa.requirement.Requirement;
import functionaljavaa.samplestructure.DataSampleStages;
import java.util.ArrayList;
import lbplanet.utilities.LPDate;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;
/**
 * 
 * @author Fran Gomez
 * @version 0.1
 */
public class SampleAudit {       
    
    public enum SampleAuditErrorTrapping implements EnumIntMessages{ 
        AUDIT_RECORDS_PENDING_REVISION("sampleAuditRecordsPendingRevision", "The sample <*1*> has pending sign audit records.", "La muestra <*1*> tiene registros de auditoría sin firmar"),
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
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }    
    public enum DataSampleAuditEvents implements EnumIntAuditEvents{SAMPLE_LOGGED, SAMPLE_RECEIVED, SET_SAMPLING_DATE, SAMPLE_CHANGE_SAMPLING_DATE,
        SET_SAMPLING_DATE_END, SAMPLE_CHANGE_SAMPLING_DATE_END,
        SAMPLE_RECEPTION_COMMENT_ADD, SAMPLE_RECEPTION_COMMENT_REMOVE, SAMPLE_EVALUATE_STATUS, SAMPLE_TESTINGGROUP_REVIEWED, SAMPLE_TESTINGGROUP_SET_AUTOAPPROVE, SAMPLE_TESTINGGROUP_SET_READY_REVISION, SAMPLE_REVIEWED,
        LOG_SAMPLE_ALIQUOT, LOG_SAMPLE_SUBALIQUOT, SAMPLESTAGE_MOVETONEXT, SAMPLESTAGE_MOVETOPREVIOUS,
        UPDATE_LAST_ANALYSIS_USER_METHOD, CHAIN_OF_CUSTODY_STARTED, CHAIN_OF_CUSTODY_ABORTED, CHAIN_OF_CUSTODY_COMPLETED, MICROORGANISM_ADDED, MICROORGANISM_REMOVED, 
        SAMPLE_SET_INCUBATION_STARTED, SAMPLE_SET_INCUBATION_ENDED, SAMPLE_SET_INCUBATION_1_STARTED, SAMPLE_SET_INCUBATION_1_ENDED, SAMPLE_SET_INCUBATION_2_STARTED, SAMPLE_SET_INCUBATION_2_ENDED,
        SAMPLE_CANCELED, SAMPLE_UNCANCELED, SAMPLE_UNREVIEWED, SAMPLE_SET_READY_FOR_REVISION, REVIEWED_AUDIT_ID,
        BATCH_SAMPLE_ADDED, BATCH_SAMPLE_REMOVED, BATCH_SAMPLE_MOVED_FROM, BATCH_SAMPLE_MOVED_TO, SAMPLE_AUTOAPPROVE, ADDED_TO_INVESTIGATION, INVESTIGATION_CLOSED}  

    public enum DataSampleAnalysisAuditEvents implements EnumIntAuditEvents{ SAMPLE_ANALYSIS_REVIEWED, SAMPLE_ANALYSIS_EVALUATE_STATUS, SAMPLE_ANALYSIS_ANALYST_ASSIGNMENT, 
        SAMPLE_ANALYSIS_ADDED, SAMPLE_ANALYSIS_CANCELED, SAMPLE_ANALYSIS_UNCANCELED, SAMPLE_ANALYSIS_UNREVIEWED, SAMPLE_ANALYSIS_SET_READY_FOR_REVISION, SAMPLE_ANALYSIS_AUTOAPPROVE}
    
    public enum DataSampleAnalysisResultAuditEvents implements EnumIntAuditEvents{BACK_FROM_CANCEL, SAMPLE_ANALYSIS_RESULT_ENTERED, UOM_CHANGED, SAMPLE_ANALYSIS_RESULT_REENTERED, SAMPLE_ANALYSIS_RESULT_ENTERED_SECONDENTRY, SAMPLE_ANALYSIS_RESULT_REENTERED_SECONDENTRY,
        SAMPLE_ANALYSIS_RESULT_ADDED, SAMPLE_ANALYSIS_RESULT_CANCELED, SAMPLE_ANALYSIS_RESULT_UNCANCELED, SAMPLE_ANALYSIS_RESULT_REVIEWED, SAMPLE_ANALYSIS_RESULT_UNREVIEWED,
        SAMPLE_ANALYSIS_RESULT_SECONDENTRY_ADDED}
      
    public enum SampleAuditBusinessRules implements EnumIntBusinessRules{
        REVISION_MODE("sampleAuditRevisionMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        AUTHOR_CAN_REVIEW_AUDIT_TOO("sampleAuditAuthorCanBeReviewerToo", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        CHILD_REVISION_REQUIRED("sampleAuditChildRevisionRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null)
        ;
        private SampleAuditBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator
        , Boolean isOpt, ArrayList<String[]> preReqs){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
            this.isOptional=isOpt;
            this.preReqs=preReqs;
        }       
        @Override        public String getTagName(){return this.tagName;}
        @Override        public String getAreaName(){return this.areaName;}
        @Override        public JSONArray getValuesList(){return this.valuesList;}
        @Override        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        @Override        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        @Override        public Boolean getIsOptional() {return isOptional;}
        @Override        public ArrayList<String[]> getPreReqs() {return this.preReqs;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        
        private final Boolean isOptional;
        private final ArrayList<String[]> preReqs;
    }
/**
 * Add one record in the audit table when altering any of the levels belonging to the sample structure when not linked to any other statement.
 *      @param action String - Action being performed
 *      @param tableName String - table where the action was performed into the Sample structure
 *      @param tableId Integer - Id for the object where the action was performed.
 *      @param sampleId
 *      @param testId Integer - testId
 *      @param resultId Integer - resultId
     * @param fldNames
     * @param fldValues
 * @return  
 */    
    public Object[] sampleAuditAdd(EnumIntAuditEvents action, String tableName, Integer tableId, 
                        Integer sampleId, Integer testId, Integer resultId, String[] fldNames, Object[] fldValues) {
        return sampleAuditAdd(action, tableName, tableId, 
            sampleId, testId, resultId, fldNames, fldValues, null, null);
    }
    public Object[] sampleAuditAdd(EnumIntAuditEvents action2, String tableName, Integer tableId, 
                        Integer sampleId, Integer testId, Integer resultId, String[] fldNames, Object[] fldValues, String alternativeAuditEntry, String alternativeAuditClass) {
/*        String fileName="dataSampleAuditEvents";
        if (testId!=null)
            fileName="dataDataSampleConfigAnalysisAuditEvents";
        if (resultId!=null)
            fileName="dataDataSampleAnalysisResultAuditEvents";
        if (alternativeAuditClass!=null){
            fileName=alternativeAuditClass;
            action=alternativeAuditEntry;
        }*/
        GenericAuditFields gAuditFlds=new GenericAuditFields(action2, TblsDataAudit.TablesDataAudit.SAMPLE, fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();
        
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        fieldNames = LPArray.addValueToArray1D(fieldNames, new String[]{ 
            TblsDataAudit.Sample.TABLE_NAME.getName(), TblsDataAudit.Sample.TABLE_ID.getName()});
        fieldValues = LPArray.addValueToArray1D(fieldValues, new Object[]{tableName, tableId});

/*        for (GlobalVariables.Languages curLang: GlobalVariables.Languages.values()){            
            Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), 
                    TblsDataAudit.TablesDataAudit.SAMPLE.getTableName(), TblsDataAudit.Sample.ACTION_PRETTY_EN.getName().replace("en", curLang.getName()));
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())){
                String propValue = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
                    fileName, null, action, curLang.getName(), false);
                if (propValue==null || propValue.length()==0)propValue=action;
                fieldNames = LPArray.addValueToArray1D(fieldNames, 
                        TblsDataAudit.Sample.ACTION_PRETTY_EN.getName().replace("en", curLang.getName()));
                fieldValues = LPArray.addValueToArray1D(fieldValues, propValue);
            }
        }
        SessionAuditActions auditActions = ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditActions();        
        String actionPrettyEn=action;
        Integer actionPrettyEnPosic = LPArray.valuePosicInArray(fieldNames, TblsDataAudit.Sample.ACTION_PRETTY_EN.getName());
        if (actionPrettyEnPosic>-1)
            actionPrettyEn=LPNulls.replaceNull(fieldValues[actionPrettyEnPosic]).toString();
        String actionPrettyEs=action;
        Integer actionPrettyEsPosic = LPArray.valuePosicInArray(fieldNames, TblsDataAudit.Sample.ACTION_PRETTY_ES.getName());
        if (actionPrettyEsPosic>-1)
            actionPrettyEs=LPNulls.replaceNull(fieldValues[actionPrettyEsPosic]).toString();
                if (auditActions!=null && auditActions.getLastAuditAction()!=null){
            action=auditActions.getLastAuditAction().getActionName()+" > "+action;
            actionPrettyEn=auditActions.getLastAuditAction().getActionPrettyEn()+" > "+actionPrettyEn;
            actionPrettyEs=auditActions.getLastAuditAction().getActionPrettyEs()+" > "+actionPrettyEs;
        }
        if (actionPrettyEnPosic==-1){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.ACTION_PRETTY_EN.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, actionPrettyEn);
        }else
            fieldValues[actionPrettyEnPosic]=actionPrettyEn;
        
        if (actionPrettyEsPosic==-1){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.ACTION_PRETTY_ES.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, actionPrettyEs);
        }else
            fieldValues[actionPrettyEsPosic]=actionPrettyEs;

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action);
*/        
        if (sampleId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.SAMPLE_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, sampleId);
        }    
        if (testId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.TEST_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, testId);
        }    
        if (resultId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.RESULT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, resultId);
        }    
/*        if (auditActions!=null && auditActions.getMainParentAuditAction()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditActions.getMainParentAuditAction().getAuditId());
        }    */
        Object[] insertRecordInfo = AuditUtilities.applyTheInsert(gAuditFlds, TblsDataAudit.TablesDataAudit.SAMPLE, fieldNames, fieldValues);
        return insertRecordInfo;
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
     * @return 
     */
    public Object[] sampleAliquotingAuditAdd(EnumIntAuditEvents action, String tableName, Integer tableId, Integer subaliquotId, Integer aliquotId, Integer sampleId, Integer testId, Integer resultId, Object[] auditlog) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        
        String[] fldNames = new String[]{TblsDataAudit.Sample.DATE.getName(), TblsDataAudit.Sample.ACTION_NAME.getName(), TblsDataAudit.Sample.TABLE_NAME.getName(),
          TblsDataAudit.Sample.TABLE_ID.getName(), TblsDataAudit.Sample.FIELDS_UPDATED.getName(), TblsDataAudit.Sample.USER_ROLE.getName(),
          TblsDataAudit.Sample.PERSON.getName(), TblsDataAudit.Sample.TRANSACTION_ID.getName()};

        Object[] fldValues = new Object[]{LPDate.getCurrentTimeStamp(), action, tableName, tableId, Arrays.toString(auditlog), token.getUserRole(), token.getPersonName(), Rdbms.getTransactionId()};
        GenericAuditFields gAuditFlds=new GenericAuditFields(action, TblsDataAudit.TablesDataAudit.SAMPLE, fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();

        Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.PROCEDURE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.PROCEDURE_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
        }                
        if (token.getAppSessionId()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.APP_SESSION_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, token.getAppSessionId());
        }    
        if (subaliquotId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.SUBALIQUOT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, subaliquotId);
        }    
        if (aliquotId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.ALIQUOT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, aliquotId);
        }    
        if (sampleId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.SAMPLE_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, sampleId);
        }    
        if (testId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.TEST_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, testId);
        }    
        if (resultId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.RESULT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, resultId);
        }                   
        AuditAndUserValidation auditAndUsrValid=ProcedureRequestSession.getInstanceForActions(null, null, null).getAuditAndUsrValid();
        if (auditAndUsrValid!=null && auditAndUsrValid.getAuditReasonPhrase()!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsDataAudit.Sample.REASON.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, auditAndUsrValid.getAuditReasonPhrase());
        }  
        return AuditUtilities.applyTheInsert(gAuditFlds, TblsDataAudit.TablesDataAudit.SAMPLE, fieldNames, fieldValues);        
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
        Object[][] auditInfo=QueryUtilitiesEnums.getTableData(TblsDataAudit.TablesDataAudit.SAMPLE, 
            EnumIntTableFields.getTableFieldsFromString(TblsDataAudit.TablesDataAudit.SAMPLE, new String[]{TblsDataAudit.Sample.PERSON.getName(), TblsDataAudit.Sample.REVIEWED.getName()}),                
            new String[]{TblsDataAudit.Sample.AUDIT_ID.getName()}, new Object[]{auditId}, 
            new String[]{TblsDataAudit.Sample.AUDIT_ID.getName()});
        if (Boolean.FALSE.equals(isTagValueOneOfEnableOnes(auditAuthorCanBeReviewerMode))){
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(auditInfo[0][0].toString())) return LPArray.array2dTo1d(auditInfo);
            if (personName.equalsIgnoreCase(auditInfo[0][0].toString())) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.AUTHOR_CANNOT_BE_REVIEWER, new Object[]{});
        }
        if (Boolean.TRUE.equals(Boolean.valueOf(auditInfo[0][1].toString()))){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.AUDIT_RECORD_ALREADY_REVIEWED, new Object[]{auditId});              
        }
        String[] updFieldName=new String[]{TblsDataAudit.Sample.REVIEWED.getName(), TblsDataAudit.Sample.REVIEWED_BY.getName(), TblsDataAudit.Sample.REVIEWED_ON.getName()};
        Object[] updFieldValue=new Object[]{true, personName, LPDate.getCurrentTimeStamp()};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsDataAudit.Sample.AUDIT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{auditId}, "");
	return Rdbms.updateRecordFieldsByFilter(TblsDataAudit.TablesDataAudit.SAMPLE,
		EnumIntTableFields.getTableFieldsFromString(TblsDataAudit.TablesDataAudit.SAMPLE, updFieldName), updFieldValue, sqlWhere, null);
        
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
        if (auditRevisionMode==null || auditRevisionMode.length()==0) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.PARAMETER_MISSING, 
                  new Object[]{SampleAuditBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName(), procInstanceName});
        String[] auditRevisionModeArr= auditRevisionMode.split("\\|");
        Boolean auditRevisionModeRecognized=false;
        for (String curModeRequired: auditRevisionModesRequired){
          if (LPArray.valuePosicInArray(auditRevisionModeArr, curModeRequired)>-1) auditRevisionModeRecognized= true; 
        }
        if (Boolean.FALSE.equals(auditRevisionModeRecognized))return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.PARAMETER_MISSING,
                  new Object[]{SampleAuditBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName(), procInstanceName});
        if (LPArray.valuePosicInArray(auditRevisionModeArr, "DISABLE")>-1)return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, SampleAuditErrorTrapping.DISABLED, 
                  new Object[]{SampleAuditBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName(), procInstanceName});
        if (LPArray.valuePosicInArray(auditRevisionModeArr, "STAGES")>-1){
            DataSampleStages smpStages = new DataSampleStages();
            if (Boolean.FALSE.equals(smpStages.isSampleStagesEnable()))return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.STAGESDETECTED_BUT_SAMPLESTAGES_NOT_ENABLED, 
                new Object[]{SampleAuditBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName(), procInstanceName});
            Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.CURRENT_STAGE.getName()});
            String sampleCurrentStage=sampleInfo[0][0].toString();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleCurrentStage)) return LPArray.array2dTo1d(sampleInfo);
            if (LPArray.valuePosicInArray(auditRevisionModeArr, sampleInfo[0][0].toString())==-1) return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, SampleAuditErrorTrapping.CURRENTSAMPLESTAGE_NOTREQUIRES_SAMPLEAUDITREVISION, 
                new Object[]{sampleCurrentStage, sampleId, SampleAuditBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName(), procInstanceName});
        }
        if (LPArray.valuePosicInArray(auditRevisionModeArr, "ACTIONS")>-1){

        }
        String[] whereFieldName=new String[]{TblsDataAudit.Sample.SAMPLE_ID.getName()};
        Object[] whereFieldValue=new Object[]{sampleId, false};

        if ("FALSE".equalsIgnoreCase(auditRevisionChildRequired))
            whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsDataAudit.Sample.PARENT_AUDIT_ID.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause());
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsDataAudit.TablesDataAudit.SAMPLE.getTableName(), 
                whereFieldName, whereFieldValue, 
                new String[]{TblsDataAudit.Sample.AUDIT_ID.getName(), TblsDataAudit.Sample.REVIEWED.getName()});
        for (Object[] curSampleInfo: sampleInfo){
          if (!"true".equalsIgnoreCase(curSampleInfo[1].toString())) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.AUDIT_RECORDS_PENDING_REVISION, 
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
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, SampleAuditErrorTrapping.ACTION_HAS_NO_SAMPLE_TEST_RESULT_LINKED, new Object[]{actionName});
        String[] auditRevisionModesRequired=new String[]{"ENABLE", "DISABLE"};
        String auditRevisionMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleAuditBusinessRules.REVISION_MODE.getAreaName(), SampleAuditBusinessRules.REVISION_MODE.getTagName());  
        String auditChildRevisionMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleAuditBusinessRules.CHILD_REVISION_REQUIRED.getAreaName(), SampleAuditBusinessRules.CHILD_REVISION_REQUIRED.getTagName());  
        if (auditRevisionMode==null || auditRevisionMode.length()==0) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.PARAMETER_MISSING, 
                  new Object[]{SampleAuditBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName(), procInstanceName});
        if (auditRevisionMode.equalsIgnoreCase("DISABLE"))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "auditRevisionModeDisabled", null);
        String[] auditRevisionModeArr= auditRevisionMode.split("\\|");
        Boolean auditRevisionModeRecognized=false;
        for (String curModeRequired: auditRevisionModesRequired){
          if (LPArray.valuePosicInArray(auditRevisionModeArr, curModeRequired)>-1) auditRevisionModeRecognized= true; 
        }
        if (Boolean.FALSE.equals(auditRevisionModeRecognized))return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.PARAMETER_MISSING, 
                  new Object[]{SampleAuditBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName(), procInstanceName});
        Integer isActions=LPArray.valuePosicInArray(auditRevisionModeArr, "ACTIONS");
        Integer actionIsThere=LPArray.valuePosicInArray(auditRevisionModeArr, actionName);
        if ((LPArray.valuePosicInArray(auditRevisionModeArr, "ACTIONS")>-1) && (LPArray.valuePosicInArray(auditRevisionModeArr, actionName)==-1)) {
            
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, SampleAuditErrorTrapping.AUDIT_RECORDS_PENDING_REVISION, new Object[]{sampleId, procInstanceName});
        }
        String[] whereFieldName=new String[]{TblsDataAudit.Sample.REVIEWED.getName()};
        Object[] whereFieldValue=new Object[]{false};
        if (sampleId!=null && sampleId!=0){
            whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsDataAudit.Sample.SAMPLE_ID.getName());
            whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, sampleId);
        }
        if (testId!=null && testId!=0){
            whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsDataAudit.Sample.TEST_ID.getName());
            whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, testId);
        }
        if (resultId!=null && resultId!=0){
            whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsDataAudit.Sample.RESULT_ID.getName());
            whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, resultId);
        }
        
        if ("FALSE".equalsIgnoreCase(auditChildRevisionMode))
            whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsDataAudit.Sample.PARENT_AUDIT_ID.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause());
        Object[][] sampleAuditInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsDataAudit.TablesDataAudit.SAMPLE.getTableName(), 
                whereFieldName, whereFieldValue, 
                new String[]{TblsDataAudit.Sample.AUDIT_ID.getName(), TblsDataAudit.Sample.REVIEWED.getName()});
        for (Object[] curSampleInfo: sampleAuditInfo){
          if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(curSampleInfo[0].toString())) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, SampleAuditErrorTrapping.AUDIT_RECORDS_PENDING_REVISION, 
            new Object[]{sampleId, procInstanceName});
          }
        }      
    //      Object[] sampleAuditReviewedValues=LPArray.getUniquesArray(sampleInfoReviewed1D);
    //      if ( (sampleAuditReviewedValues.length!=1) || ( (sampleAuditReviewedValues.length==1) && !("true".equalsIgnoreCase(sampleAuditReviewedValues[0].toString())) ) )
        return new Object[]{LPPlatform.LAB_TRUE, "All reviewed"};
    }  
}
