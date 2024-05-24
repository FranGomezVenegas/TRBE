/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.inventory.batch;

import module.monitoring.definition.TblsEnvMonitConfig;
import module.monitoring.definition.TblsEnvMonitData;
import module.monitoring.definition.TblsEnvMonitProcedure;
import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.SqlWhere;
import functionaljavaa.audit.IncubBatchAudit;
import functionaljavaa.instruments.incubator.ConfigIncubator.ConfigIncubatorBusinessRules;
import module.monitoring.logic.ProcedureDeviationIncubator;
import functionaljavaa.parameter.Parameter;
import java.util.ArrayList;
import trazit.session.ResponseMessages;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.JSONArray;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public class DataBatchIncubator {
    
    public enum BatchBusinessRules implements EnumIntBusinessRules{
        START_MULTIPLE_BATCH_IN_PARALLEL("incubationBatch_startMultipleInParallelPerIncubator", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        START_FOR_LOCKED_INCUBATOR_MODE("incubationBatch_startForLockedIncubatorMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null)
        
        ;
        private BatchBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator, Boolean isOptional, ArrayList<String[]> preReqs){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
            this.isOptional=isOptional;
            this.preReq=preReqs;
        }       
        @Override        public String getTagName(){return this.tagName;}
        @Override        public String getAreaName(){return this.areaName;}
        @Override        public JSONArray getValuesList(){return this.valuesList;}
        @Override        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        @Override        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        @Override        public Boolean getIsOptional() {return this.isOptional;}
        @Override        public ArrayList<String[]> getPreReqs() {return this.preReq;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;   
        private final Boolean isOptional;
        private final ArrayList<String[]> preReq;

    }  
public enum IncubatorBatchSuccess implements EnumIntMessages{  
        BATCH_AVAILABLEFORCHANGES("batchAvailableForChanges", "The Batch <*1*> is available to alter its content", "The Batch <*1*> is available to alter its content"),
        CREATEBATCH_TYPECHECKER_SUCCESS("createBatchTypeCheckerSuccess", "", ""),
        SAMPLES_IN_BATCH_SET_AS_BATCHSTARTED("allSamplesInBatchSetAsBatchStarted", "", ""),
        SAMPLES_IN_BATCH_SET_AS_BATCHENDED("allSamplesInBatchSetAsBatchEnded", "", ""),
        ; 
        private IncubatorBatchSuccess(String errCode, String defaultTextEn, String defaultTextEs){
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
    
    public enum IncubatorBatchErrorTrapping implements EnumIntMessages{ 
        INCUBATORBATCH_NOT_STARTED("IncubatorBatchNotStartedYet", "The batch <*1*> was not started yet for procedure <*2*>", "La tanda <*1*> no está iniciada todavía para el proceso <*2*>"),
        INCUBATORBATCH_ALREADY_STARTED("IncubatorBatchAlreadyStarted", "The batch <*1*> was already started and cannot be started twice for procedure <*2*>", "La tanda <*1*> no está iniciada todavía para el proceso <*2*>"),
        INCUBATORBATCH_ALREADY_IN_PROCESS("IncubatorBatchAlreadyInProcess", "The batch <*1*> is already in process for incubator <*2*> and start multiples batches per incubator is not allowed for the procedure <*3*>", ""),
        INCUBATORBATCH_ALREADY_EXIST("incubatorBatchExist", "One incubator batch called <*1*> already exist in procedure <*2*>", "Una tanda con el nombre <*1*> ya existe en el proceso <*2*>"),
        INCUBATORBATCH_NOT_FOUND("incubatorBatchNotFound", "One incubator batch called <*1*> does not exist in procedure <*2*>", "Una tanda con el nombre <*1*> no existe en el proceso <*2*>"),        
        INCUBATORBATCH_NOT_ACTIVE("incubatorBatchNotActive","The Batch <*1*> is not active","The Batch <*1*> is not active"),
        INCUBATORBATCH_TEMPLATE_NOT_ACTIVE("incubatorBatchTemplateNotActive","The Batch template <*1*> and version <*2*> is not active","The Batch template <*1*> and version <*2*> is not active"),
        INCUBATORBATCH_WITH_NO_INCUBATOR("incubatorBatch_withNoIncubator", "", ""),
        INCUB_BATCH_NOT_ACTIVE_FOR_CHANGES("incubationBatchStart_StoppedByNotActiveForChanges", "", ""), 
        INCUB_BATCH_START_STOPPED_BY_BUSINESSRULEMODE("incubationBatchStart_StoppedByIncubationLockedBusinessRuleMode", "", ""),        
        EMPTY_BATCH("incubBatch_emptyBatch", "", ""),
        INCUB_BATCH_STARTED_CHANGEITSCONTENT("IncubatorBatchStartedToChangeItsContent", "", ""),
        SAMPLE_NOTFOUND_IN_BATCH("incubBatch_sampleNotFoundInBatch"," Sample <*1*> not found in batch <*2*> for procedure <*3*>.", ""),
        INCUBATORBATCH_NOTEMPTY_TOBEREMOVED("IncubatorBatchNotEmptyToRemove", "", ""),
        INCUBATORBATCH_NOTEMPTY_TOCHANGEINCUBATOR("IncubatorBatchNotEmptyToChangeIncubator", "", ""),
        BATCHTYPE_NOT_RECOGNIZED("incubatorBatchType_notRecognized", "batchType <*1*> Not recognized", "batchType <*1*> Not recognized"),
        SAMPLE_HAS_NOPENDING_INCUBATION("sampleWithNoPendingIncubation", "There is no pending incubation for sample <*1*> in procedure <*2*>", "There is no pending incubation for sample <*1*> in procedure <*2*>"), 
        MOMENT_NOTDECLARED_IN_BATCHMOMENTSLIST("incubBatch_momentNotInBatchMomentsList","The moment <*1*> is not declared in BatchIncubatorMoments", "The moment <*1*> is not declared in BatchIncubatorMoments"),
        STAGE_NOT_RECOGNIZED("incubBatch_stageNotRecognized", " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>.", " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>."),
        SAMPLE_ALREADYBATCHED("incubBatch_sampleAlreadyBatched", "The pending incubation stage is <*1*> and the sample <*2*>  is already batched in batch <*3*> for procedure <*3*>", "The pending incubation stage is <*1*> and the sample <*2*>  is already batched in batch <*3*> for procedure <*3*>"), 
        FIELD_NOT_FOUND("incubBatch_fieldNotFound","Field <*1*> not found in table <*2*> for procedure <*3*>", "Field <*1*> not found in table <*2*> for procedure <*3*>"),
        STRUCTURED_BATCH_WRONGDIMENSION("incubBatchStructured_wrongDimensions", "", ""),
        STRUCTURED_BATCH_POSITIONOCCUPIED("incubBatchStructured_positionOccupied", "", ""),
        STRUCTURED_POSITION_OVER_DIMENSION("incubBatchStructured_positionOverDimenrsion", "", ""),
        PARSE_ERROR_STRUCTUREDBATCH("incubBatchStructured_parseError", "", ""),
        LOCKING_NOT_ENABLED("incubationLockingNotEnabled", "", ""),
        LOCKED_FIELD_NOT_PRESENT("lockedFieldNotPresentInIncubatorForProcedure", "", ""),
        LOCKED_ENABLED_BUT_MODE_NOT_DEFINED("incubationLockingEnabledButModeNotDefined", "", ""),
        BYPASSED_BY_BUSINESS_RULE("ByPassedByIncubationLockedBusinessRuleMode", "ByPassedByIncubationLockedBusinessRuleMode, Rule:<*1*>, Value:<*2*>", ""),
        
        ;
        private IncubatorBatchErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
    
    /**
     *
     */
    public enum BatchIncubatorType{

        /**
         *
         */
        STRUCTURED, 

        /**
         *
         */
        UNSTRUCTURED}
    enum BatchIncubatorMoments{START, END}
    public enum DataBatchAuditEvents implements EnumIntAuditEvents{BATCH_CREATED, BATCH_UPDATED, BATCH_STARTED, BATCH_ENDED, BATCH_SAMPLE_ADDED, BATCH_SAMPLE_MOVED, BATCH_SAMPLE_REMOVED, BATCH_SAMPLE_REMOVED_BY_OVERRIDE, BATCH_ASSIGN_INCUBATOR}

    /**
     *
     * @param bName
     * @param bTemplateId
     * @param bTemplateVersion
     * @param fldName
     * @param fldValue
     * @return
     */

    public static InternalMessage createBatch(String bName, Integer bTemplateId, Integer bTemplateVersion, String[] fldName, Object[] fldValue){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] batchExists=Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{bName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(batchExists[0].toString())){
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_ALREADY_EXIST, new Object[]{bName, procInstanceName});
        }
        Object[][] templateInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_VERSION.getName()}, 
                new Object[]{bTemplateId, bTemplateVersion}, new String[]{TblsEnvMonitConfig.IncubBatch.ACTIVE.getName(), TblsEnvMonitConfig.IncubBatch.TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(templateInfo[0][0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{bTemplateId, bTemplateVersion});
        if (Boolean.FALSE.equals(Boolean.valueOf(templateInfo[0][0].toString())))
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_TEMPLATE_NOT_ACTIVE, new Object[]{bTemplateId, bTemplateVersion});

        String batchType=templateInfo[0][1].toString();
        InternalMessage batchTypeCheckerDiagn= createBatchTypeChecker(batchType, bTemplateVersion, fldName, fldValue);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchTypeCheckerDiagn.getDiagnostic())) return batchTypeCheckerDiagn;
        
        if (batchType.equalsIgnoreCase(BatchIncubatorType.UNSTRUCTURED.toString())){ 
            return DataBatchIncubatorUnstructured.createBatchUnstructured(bName, bTemplateId, bTemplateVersion, fldName, fldValue);
        }else if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) return DataBatchIncubatorStructured.createBatchStructured(bName, bTemplateId, bTemplateVersion, fldName, fldValue);
        else
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED, new Object[]{batchType});         
    }
    
    public static InternalMessage removeBatch(String bName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{bName},
                new String[]{TblsEnvMonitData.IncubBatch.TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())){
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_NOT_FOUND, new Object[]{bName, procInstanceName});            
        } 
        String batchType=batchInfo[0][0].toString();
        Boolean isBatchEmpty=false;
        if (batchType.equalsIgnoreCase(BatchIncubatorType.UNSTRUCTURED.toString())){ 
            isBatchEmpty=DataBatchIncubatorUnstructured.batchIsEmptyUnstructured(bName);
        }else if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) 
            isBatchEmpty=DataBatchIncubatorStructured.batchIsEmptyStructured(bName);
        else
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED, new Object[]{batchType});   
        if (Boolean.TRUE.equals(isBatchEmpty)){
            SqlWhere where =new SqlWhere();
            where.addConstraint(TblsEnvMonitData.IncubBatch.NAME, null, new Object[]{bName}, null);
            RdbmsObject removeDiagn = Rdbms.removeRecordInTable(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH, where, null); 
            if (Boolean.TRUE.equals(removeDiagn.getRunSuccess()))
                return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
            else
                return new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
        }else{
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_NOTEMPTY_TOBEREMOVED, new Object[]{bName, procInstanceName});        
        }
    }
    private static InternalMessage createBatchTypeChecker(String batchType, Integer bTemplateVersion, String[] fldName, Object[] fldValue){
        InternalMessage batchTypeExist=batchTypeExists(batchType);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchTypeExist.getDiagnostic())) return batchTypeExist;
        
        return new InternalMessage(LPPlatform.LAB_TRUE, IncubatorBatchSuccess.CREATEBATCH_TYPECHECKER_SUCCESS, null);        
    }
    
    /**
     *
     * @param bName
     * @param bTemplateId
     * @param bTemplateVersion
     * @param sampleId
     * @return
     */
    public static InternalMessage batchAddSample(String bName, Integer bTemplateId, Integer bTemplateVersion, Integer sampleId){
        return batchAddSample(bName, bTemplateId, bTemplateVersion, sampleId, null, null, null);
    }

    public static InternalMessage batchAddSample(String bName, Integer bTemplateId, Integer bTemplateVersion, Integer sampleId, Integer row, Integer col, Boolean override){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] templateInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_VERSION.getName()}, 
                new Object[]{bTemplateId, bTemplateVersion}, 
                new String[]{TblsEnvMonitConfig.IncubBatch.ACTIVE.getName(), TblsEnvMonitConfig.IncubBatch.TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(templateInfo[0][0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{bTemplateId, bTemplateVersion});
        if (Boolean.FALSE.equals(Boolean.valueOf(templateInfo[0][0].toString())))
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_TEMPLATE_NOT_ACTIVE, new Object[]{bTemplateId, bTemplateVersion});

        Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, 
                new Object[]{bName}, 
                new String[]{TblsEnvMonitData.IncubBatch.INCUBATION_INCUBATOR.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{bName});
        if (batchInfo[0][0]==null || batchInfo[0][0].toString().length()==0)
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_WITH_NO_INCUBATOR, new Object[]{bName});
        InternalMessage batchIsAvailableForChangingContent = batchIsAvailableForChangingContent(bName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchIsAvailableForChangingContent.getDiagnostic())) return batchIsAvailableForChangingContent;
        
        String batchType=templateInfo[0][1].toString();
        InternalMessage batchTypeExist=batchTypeExists(batchType);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchTypeExist.getDiagnostic())) return batchTypeExist;
        String[] sampleInfoFieldsToRetrieve=new String[]{TblsEnvMonitData.Sample.INCUBATION_PASSED.getName(), TblsEnvMonitData.Sample.INCUBATION2_PASSED.getName(),                    
                    TblsEnvMonitData.Sample.INCUBATION_BATCH.getName(), TblsEnvMonitData.Sample.INCUBATION2_BATCH.getName()};
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), 
                new String[]{TblsEnvMonitData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{sampleId});
        Integer pendingIncubationStage=samplePendingBatchStage(sampleInfoFieldsToRetrieve, sampleInfo[0]);
        if (pendingIncubationStage==-1) return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.SAMPLE_HAS_NOPENDING_INCUBATION, new Object[]{sampleId, procInstanceName});
        InternalMessage smpIsBatchable=sampleIncubStageIsBatchable(sampleId, pendingIncubationStage, sampleInfoFieldsToRetrieve, sampleInfo[0]);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(smpIsBatchable.getDiagnostic())) return smpIsBatchable;        
        
        if (batchType.equalsIgnoreCase(BatchIncubatorType.UNSTRUCTURED.toString())){ 
            return DataBatchIncubatorUnstructured.batchAddSampleUnstructured(bName, sampleId, pendingIncubationStage);
        }else if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) return DataBatchIncubatorStructured.batchAddSampleStructured(bName, sampleId, pendingIncubationStage, row, col, override);
        else
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED, new Object[]{batchType}); 
    }

    /**
     *
     * @param bName
     * @param bTemplateId
     * @param bTemplateVersion
     * @param sampleId
     * @param newRow
     * @param newCol
     * @param override
     * @return
     */
    public static InternalMessage batchMoveSample(String bName, Integer bTemplateId, Integer bTemplateVersion, Integer sampleId, Integer newRow, Integer newCol, Boolean override){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] templateInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_VERSION.getName()}, 
                new Object[]{bTemplateId, bTemplateVersion}, new String[]{TblsEnvMonitConfig.IncubBatch.ACTIVE.getName(), TblsEnvMonitConfig.IncubBatch.TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(templateInfo[0][0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{bTemplateId, bTemplateVersion});
        if (Boolean.FALSE.equals(Boolean.valueOf(templateInfo[0][0].toString())))
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_TEMPLATE_NOT_ACTIVE, new Object[]{bTemplateId, bTemplateVersion});

        InternalMessage batchIsAvailableForChangingContent = batchIsAvailableForChangingContent(bName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchIsAvailableForChangingContent.getDiagnostic())) return batchIsAvailableForChangingContent;

        String[] sampleInfoFieldsToRetrieve=new String[]{TblsEnvMonitData.Sample.INCUBATION_PASSED.getName(), TblsEnvMonitData.Sample.INCUBATION2_PASSED.getName(),                    
                    TblsEnvMonitData.Sample.INCUBATION_BATCH.getName(), TblsEnvMonitData.Sample.INCUBATION2_BATCH.getName()};
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), 
                new String[]{TblsEnvMonitData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{sampleId});
        Integer pendingIncubationStage=samplePendingBatchStage(sampleInfoFieldsToRetrieve, sampleInfo[0]);
        if (pendingIncubationStage==-1) return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.SAMPLE_HAS_NOPENDING_INCUBATION, new Object[]{sampleId, procInstanceName});

        String batchType=templateInfo[0][1].toString();
        InternalMessage batchTypeExist=batchTypeExists(batchType);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchTypeExist.getDiagnostic())) return batchTypeExist;
        if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) 
            return DataBatchIncubatorStructured.batchMoveSampleStructured(bName, sampleId, pendingIncubationStage, newRow, newCol, override);
        else
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED, new Object[]{batchType});         
    }
    
    public static InternalMessage batchRemoveSample(String bName, Integer sampleId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{bName}, 
                new String[]{TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_VERSION.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{bName});       
        return batchRemoveSample(bName, (Integer) batchInfo[0][0], (Integer) batchInfo[0][1], sampleId);
    }
    /**
     *
     * @param bName
     * @param bTemplateId
     * @param bTemplateVersion
     * @param sampleId
     * @return
     */
    public static InternalMessage batchRemoveSample(String bName, Integer bTemplateId, Integer bTemplateVersion, Integer sampleId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] templateInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_VERSION.getName()}, 
                new Object[]{bTemplateId, bTemplateVersion}, new String[]{TblsEnvMonitConfig.IncubBatch.ACTIVE.getName(), TblsEnvMonitConfig.IncubBatch.TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(templateInfo[0][0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{bTemplateId, bTemplateVersion});
        if (Boolean.FALSE.equals(Boolean.valueOf(templateInfo[0][0].toString())))
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_TEMPLATE_NOT_ACTIVE, new Object[]{bTemplateId, bTemplateVersion});

        String batchType=templateInfo[0][1].toString();
        InternalMessage batchTypeExist=batchTypeExists(batchType);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchTypeExist.getDiagnostic())) return batchTypeExist;

        InternalMessage batchIsAvailableForChangingContent = batchIsAvailableForChangingContent(bName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchIsAvailableForChangingContent.getDiagnostic())) return batchIsAvailableForChangingContent;

        String[] sampleInfoFieldsToRetrieve=new String[]{TblsEnvMonitData.Sample.INCUBATION_PASSED.getName(), TblsEnvMonitData.Sample.INCUBATION2_PASSED.getName(),                    
                    TblsEnvMonitData.Sample.INCUBATION_BATCH.getName(), TblsEnvMonitData.Sample.INCUBATION2_BATCH.getName()};
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), 
                new String[]{TblsEnvMonitData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{sampleId});
        Integer pendingIncubationStage=samplePendingBatchStage(sampleInfoFieldsToRetrieve, sampleInfo[0]);
        if (pendingIncubationStage==-1) return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.SAMPLE_HAS_NOPENDING_INCUBATION, new Object[]{sampleId, procInstanceName});

        if (batchType.equalsIgnoreCase(BatchIncubatorType.UNSTRUCTURED.toString())) 
            return DataBatchIncubatorUnstructured.batchRemoveSampleUnstructured(bName, sampleId);
        else if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) return DataBatchIncubatorStructured.batchRemoveSampleStructured(bName, sampleId, pendingIncubationStage);
        else
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED, new Object[]{batchType}); 
        
    }
    
    /**
     *
     * @param bName
     * @param bTemplateId
     * @param bTemplateVersion
     * @return 
     */
    public static InternalMessage batchStarted(String bName, Integer bTemplateId, Integer bTemplateVersion){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{bName}, new String[]{TblsEnvMonitData.IncubBatch.INCUBATION_START.getName(), TblsEnvMonitData.IncubBatch.INCUBATION_INCUBATOR.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())) 
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{bName});
        String batchIncubName=batchInfo[0][1].toString();
        if ( (batchInfo[0][0]!=null) && (batchInfo[0][0].toString().trim().length()>0) ) return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_ALREADY_STARTED, new Object[]{bName, procInstanceName});        
        String allowMultipleStartBatch=Parameter.getBusinessRuleProcedureFile(procInstanceName, BatchBusinessRules.START_MULTIPLE_BATCH_IN_PARALLEL.getAreaName(), BatchBusinessRules.START_MULTIPLE_BATCH_IN_PARALLEL.getTagName());
        if (Boolean.FALSE.equals("YES".equalsIgnoreCase(allowMultipleStartBatch))){
            Object[][] batchInProcess = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), 
                    new String[]{TblsEnvMonitData.IncubBatch.INCUBATION_INCUBATOR.getName(), TblsEnvMonitData.IncubBatch.INCUBATION_START.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause(), TblsEnvMonitData.IncubBatch.INCUBATION_END.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, new Object[]{batchIncubName, "", ""},
                    new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()});            
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInProcess[0][0].toString()))) {
                return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_ALREADY_IN_PROCESS, new Object[]{batchInProcess[0][0], batchIncubName, procInstanceName}, batchIncubName);
//                diagn=LPArray.addValueToArray1D(diagn, batchInProcess[0][0].toString()); InternalMessage
//                return LPArray.addValueToArray1D(diagn, batchIncubName); InternalMessage
            }                    
        }
        InternalMessage incubIsLocked=incubatorIsLocked(bName, batchIncubName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubIsLocked.getDiagnostic())) return incubIsLocked;
        return batchMomentMarked(bName, bTemplateId, bTemplateVersion, BatchIncubatorMoments.START.toString());
    }
    
    /**
     *
     * @param bName
     * @param bTemplateId
     * @param bTemplateVersion
     * @return
     */
    public static InternalMessage batchEnded(String bName, Integer bTemplateId, Integer bTemplateVersion){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{bName}, new String[]{TblsEnvMonitData.IncubBatch.INCUBATION_START.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())) 
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{bName});
        if ( (batchInfo[0][0]==null) || (batchInfo[0][0].toString().trim().length()<1) ) return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_NOT_STARTED, new Object[]{bName, procInstanceName});
        return batchMomentMarked(bName, bTemplateId, bTemplateVersion, BatchIncubatorMoments.END.toString());
    }
    
    private static InternalMessage batchMomentMarked(String bName, Integer bTemplateId, Integer bTemplateVersion, String moment){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] templateInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_VERSION.getName()}, 
                new Object[]{bTemplateId, bTemplateVersion}, new String[]{TblsEnvMonitConfig.IncubBatch.ACTIVE.getName(), TblsEnvMonitConfig.IncubBatch.TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(templateInfo[0][0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{bTemplateId, bTemplateVersion});
        if (Boolean.FALSE.equals(Boolean.valueOf(templateInfo[0][0].toString())))
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_TEMPLATE_NOT_ACTIVE, new Object[]{bTemplateId, bTemplateVersion});
        Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{bName}, 
                new String[]{TblsEnvMonitData.IncubBatch.INCUBATION_INCUBATOR.getName()});            
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{bName});
        String incubName=batchInfo[0][0].toString();
        String batchType=templateInfo[0][1].toString();
        InternalMessage batchTypeExist=batchTypeExists(batchType);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchTypeExist.getDiagnostic())) return batchTypeExist;
        InternalMessage batchSampleIncubationMomentMarkedDiagn= null;

        if (BatchIncubatorMoments.START.toString().equalsIgnoreCase(moment)){
            if (batchType.equalsIgnoreCase(BatchIncubatorType.UNSTRUCTURED.toString())) 
                batchSampleIncubationMomentMarkedDiagn=DataBatchIncubatorUnstructured.batchSampleIncubStartedUnstructured(bName, incubName);
            else if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) 
                batchSampleIncubationMomentMarkedDiagn=DataBatchIncubatorStructured.batchSampleIncubStartedStructured();
            else
                return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED, new Object[]{batchType}); 
        }else if (BatchIncubatorMoments.END.toString().equalsIgnoreCase(moment)){
            if (batchType.equalsIgnoreCase(BatchIncubatorType.UNSTRUCTURED.toString())) 
                batchSampleIncubationMomentMarkedDiagn=DataBatchIncubatorUnstructured.batchSampleIncubEndedUnstructured(bName, incubName);
            else if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) 
                batchSampleIncubationMomentMarkedDiagn=DataBatchIncubatorStructured.batchSampleIncubEndedStructured();
            else
                return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED, new Object[]{batchType}); 
        } else return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.MOMENT_NOTDECLARED_IN_BATCHMOMENTSLIST, new Object[]{moment});
        

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchSampleIncubationMomentMarkedDiagn.getDiagnostic())) return batchSampleIncubationMomentMarkedDiagn;
        String[] requiredFields = new String[0];
        Object[] requiredFieldsValue= new Object[0];
        String batchAuditEvent="";
        if (BatchIncubatorMoments.START.toString().equalsIgnoreCase(moment)){
            requiredFields = new String[]{TblsEnvMonitData.IncubBatch.INCUBATION_INCUBATOR.getName(), TblsEnvMonitData.IncubBatch.INCUBATION_START.getName()};
            requiredFieldsValue= new Object[]{incubName, LPDate.getCurrentTimeStamp()}; 
            batchAuditEvent=DataBatchAuditEvents.BATCH_STARTED.toString();
        }else if (BatchIncubatorMoments.END.toString().equalsIgnoreCase(moment)){
            requiredFields = new String[]{TblsEnvMonitData.IncubBatch.INCUBATION_END.getName(), TblsEnvMonitData.IncubBatch.ACTIVE.getName(), TblsEnvMonitData.IncubBatch.COMPLETED.getName()};
            requiredFieldsValue= new Object[]{LPDate.getCurrentTimeStamp(), false, true};                
            batchAuditEvent=DataBatchAuditEvents.BATCH_ENDED.toString();
        } else return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.MOMENT_NOTDECLARED_IN_BATCHMOMENTSLIST, new Object[]{moment});
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsEnvMonitData.IncubBatch.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{bName}, "");
	RdbmsObject updateDiagnostic=Rdbms.updateTableRecordFieldsByFilter(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH,
            EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH, requiredFields), requiredFieldsValue, sqlWhere, null);
        if (Boolean.TRUE.equals(updateDiagnostic.getRunSuccess())) {
            IncubBatchAudit.incubBatchAuditAdd(batchAuditEvent, TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), bName,
                LPArray.joinTwo1DArraysInOneOf1DString(requiredFields, requiredFieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        }
        return new InternalMessage(updateDiagnostic.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, updateDiagnostic.getErrorMessageCode(), updateDiagnostic.getErrorMessageVariables());
    }
    
    private static InternalMessage batchTypeExists(String batchType){
        Boolean typeExists = false;
        BatchIncubatorType[] arr = BatchIncubatorType.values();
        for (BatchIncubatorType curType: arr){
            if (batchType.equalsIgnoreCase(curType.toString())){
                typeExists=true;
                break;
            }
        }
        if (Boolean.TRUE.equals(typeExists))
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
        return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED, new Object[]{batchType});        
    }
    
    private static Integer samplePendingBatchStage(String[] fieldsName, Object[] fieldsValue){
        Integer posic = LPArray.valuePosicInArray(fieldsName, TblsEnvMonitData.Sample.INCUBATION_PASSED.getName());
        if (posic==-1) return posic;
        if ((fieldsValue[posic]==null) || (Boolean.FALSE.equals(Boolean.valueOf(fieldsValue[posic].toString()))) ) return 1;

        posic = LPArray.valuePosicInArray(fieldsName, TblsEnvMonitData.Sample.INCUBATION2_PASSED.getName());
        if (posic==-1) return posic;
        if ((fieldsValue[posic]==null) || (Boolean.FALSE.equals(Boolean.valueOf(fieldsValue[posic].toString()))) ) return 2;
        return -1;
    }
    
    private static InternalMessage sampleIncubStageIsBatchable(Integer sampleId, Integer incubStage, String[] fieldsName, Object[] fieldsValue){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String batchFldName="";
        if (null==incubStage)
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STAGE_NOT_RECOGNIZED,
                    new Object[]{incubStage, procInstanceName});         
        else switch (incubStage) {
            case 1:
                batchFldName=TblsEnvMonitData.Sample.INCUBATION_BATCH.getName();
                break;
            case 2:
                batchFldName=TblsEnvMonitData.Sample.INCUBATION2_BATCH.getName();
                break;
            default:
                return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STAGE_NOT_RECOGNIZED,
                        new Object[]{incubStage, procInstanceName});
        }

        Integer posic = LPArray.valuePosicInArray(fieldsName, batchFldName);
        if (posic==-1) return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.FIELD_NOT_FOUND,
                new Object[]{batchFldName, TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), procInstanceName});
        if ( (fieldsValue[posic]==null) || (fieldsValue[posic].toString().length()==0) ) 
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
        else return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.SAMPLE_ALREADYBATCHED,
                new Object[]{incubStage, sampleId, fieldsValue[posic], procInstanceName});
    }
    
        
    /**
     *
     * @param batchName
     * @param incubName
     * @param incubStage
     * @return
     */
    public static InternalMessage batchAssignIncubator(String batchName, String incubName, String incubStage){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, 
                new String[]{TblsEnvMonitConfig.IncubBatch.ACTIVE.getName(), TblsEnvMonitConfig.IncubBatch.TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{batchName});
        if (Boolean.FALSE.equals(Boolean.valueOf(batchInfo[0][0].toString())))
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_NOT_ACTIVE, new Object[]{batchName});
        Boolean isBatchEmpty=false;
        String batchType=batchInfo[0][1].toString();        
        if (batchType.equalsIgnoreCase(BatchIncubatorType.UNSTRUCTURED.toString())){ 
            isBatchEmpty=DataBatchIncubatorUnstructured.batchIsEmptyUnstructured(batchName);
        }else if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) 
            isBatchEmpty=DataBatchIncubatorStructured.batchIsEmptyStructured(batchName);
        else
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED, new Object[]{batchType});   
        if (Boolean.FALSE.equals(isBatchEmpty))
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_NOTEMPTY_TOCHANGEINCUBATOR, new Object[]{batchName, procInstanceName});        
        
        String[] updFieldName=new String[]{TblsEnvMonitData.IncubBatch.INCUBATION_INCUBATOR.getName(), TblsEnvMonitData.IncubBatch.INCUB_STAGE.getName()};
        Object[] updFieldValue=new Object[]{incubName, incubStage};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsEnvMonitData.IncubBatch.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{batchName}, "");
	RdbmsObject updateDiagnostic=Rdbms.updateTableRecordFieldsByFilter(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH,
            EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH, updFieldName), updFieldValue, sqlWhere, null);
        if (Boolean.TRUE.equals(updateDiagnostic.getRunSuccess())) {
            IncubBatchAudit.incubBatchAuditAdd(DataBatchAuditEvents.BATCH_ASSIGN_INCUBATOR.toString(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName,  
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        }
        return new InternalMessage(updateDiagnostic.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, updateDiagnostic.getErrorMessageCode(), updateDiagnostic.getErrorMessageVariables());
    }
    
    /**
     *
     * @param batchName
     * @param fieldsName
     * @param fieldsValue
     * @return
     */
    public static InternalMessage batchUpdateInfo(String batchName, String[] fieldsName, Object[] fieldsValue){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, 
                new String[]{TblsEnvMonitConfig.IncubBatch.ACTIVE.getName(), TblsEnvMonitConfig.IncubBatch.TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{batchName});
        if (Boolean.FALSE.equals(Boolean.valueOf(batchInfo[0][0].toString())))
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_NOT_ACTIVE, new Object[]{batchName});        
	SqlWhere sqlWhere = new SqlWhere();
        EnumIntTableFields[] updateFldNames = EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH, fieldsName);
	sqlWhere.addConstraint(TblsEnvMonitData.IncubBatch.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{batchName}, "");
	RdbmsObject updateDiagnostic=Rdbms.updateTableRecordFieldsByFilter(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH,
            updateFldNames, fieldsValue, sqlWhere, null);
        if (Boolean.TRUE.equals(updateDiagnostic.getRunSuccess())) {
            IncubBatchAudit.incubBatchAuditAdd(DataBatchAuditEvents.BATCH_UPDATED.toString(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName,
                LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        }
        return new InternalMessage(updateDiagnostic.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, updateDiagnostic.getErrorMessageCode(), updateDiagnostic.getErrorMessageVariables());
    }
    
    public static InternalMessage batchIsAvailableForChangingContent(String batchName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, 
                new String[]{TblsEnvMonitData.IncubBatch.ACTIVE.getName(), TblsEnvMonitData.IncubBatch.INCUBATION_START.getName()});
        if (batchInfo[0][0]==null || Boolean.FALSE.equals(Boolean.valueOf(batchInfo[0][0].toString()))) 
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUB_BATCH_NOT_ACTIVE_FOR_CHANGES, new Object[]{batchName}); 
        if (batchInfo[0][1]!=null && batchInfo[0][1].toString().length()>0) 
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUB_BATCH_STARTED_CHANGEITSCONTENT, new Object[]{batchName}); 
        return new InternalMessage(LPPlatform.LAB_TRUE, IncubatorBatchSuccess.BATCH_AVAILABLEFORCHANGES, new Object[]{batchName}); 
    }

    public static InternalMessage incubatorIsLocked(String batchName, String instName){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=instanceForActions.getProcedureInstance();        
        Object[] procedureBusinessRuleEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, ConfigIncubatorBusinessRules.LOCK_WHEN_TEMP_OUT_OF_RANGE.getAreaName(), ConfigIncubatorBusinessRules.LOCK_WHEN_TEMP_OUT_OF_RANGE.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureBusinessRuleEnable[0].toString()))
            return new InternalMessage(LPPlatform.LAB_TRUE, IncubatorBatchErrorTrapping.LOCKING_NOT_ENABLED, null);
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(Rdbms.dbTableExists(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), TblsEnvMonitConfig.InstrIncubator.LOCKED.getName())[0].toString()))) 
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.LOCKED_FIELD_NOT_PRESENT, new Object[]{procInstanceName});
        String[] incubFldNames=new String[]{TblsEnvMonitConfig.InstrIncubator.LOCKED.getName(), TblsEnvMonitConfig.InstrIncubator.LOCKED_REASON.getName()};        
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), 
            new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()}, new Object[]{instName}, 
            incubFldNames);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())) 
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{instName});
        if (Boolean.FALSE.equals(Boolean.valueOf(LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(incubFldNames, TblsEnvMonitConfig.InstrIncubator.LOCKED.getName())]).toString())))
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
        String ruleValue=Parameter.getBusinessRuleProcedureFile(procInstanceName, BatchBusinessRules.START_FOR_LOCKED_INCUBATOR_MODE.getAreaName(), BatchBusinessRules.START_FOR_LOCKED_INCUBATOR_MODE.getTagName());
        if (ruleValue.length()==0)
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.LOCKED_ENABLED_BUT_MODE_NOT_DEFINED, null);
        if (ruleValue.toUpperCase().contains("DEVIATION")){
            ProcedureDeviationIncubator.createNew(instName, new String[]{TblsEnvMonitProcedure.ProcedureDeviationIncubator.BATCH_NAME.getName()}, new Object[]{batchName});
        }
        if (ruleValue.toUpperCase().contains("STOP")){
            ResponseMessages messages = instanceForActions.getMessages();
            messages.addMainForError(IncubatorBatchErrorTrapping.INCUB_BATCH_START_STOPPED_BY_BUSINESSRULEMODE, new Object[]{instrInfo[0][1].toString(), ruleValue});
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUB_BATCH_START_STOPPED_BY_BUSINESSRULEMODE, new Object[]{instrInfo[0][1].toString(), ruleValue});
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, IncubatorBatchErrorTrapping.BYPASSED_BY_BUSINESS_RULE, new Object[]{BatchBusinessRules.START_FOR_LOCKED_INCUBATOR_MODE.getTagName(), ruleValue});
    }
    
}
