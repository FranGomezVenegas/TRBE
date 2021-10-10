/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.inventory.batch;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitProcedure;
import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import functionaljavaa.audit.IncubBatchAudit;
import functionaljavaa.instruments.incubator.ConfigIncubator.ConfigIncubatorBusinessRules;
import functionaljavaa.moduleenvironmentalmonitoring.ProcedureDeviationIncubator;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.responsemessages.ResponseMessages;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class DataBatchIncubator {
    
    public enum BatchBusinessRules{
        START_MULTIPLE_BATCH_IN_PARALLEL("incubationBatch_startMultipleInParallelPerIncubator", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        START_FOR_LOCKED_INCUBATOR_MODE("incubationBatch_startForLockedIncubatorMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|')
        
        ;
        private BatchBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
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
    public enum IncubatorBatchErrorTrapping{ 
        INCUBATORBATCH_NOT_STARTED("IncubatorBatchNotStartedYet", "The batch <*1*> was not started yet for procedure <*2*>", "La tanda <*1*> no está iniciada todavía para el proceso <*2*>"),
        INCUBATORBATCH_ALREADY_STARTED("IncubatorBatchAlreadyStarted", "The batch <*1*> was already started and cannot be started twice for procedure <*2*>", "La tanda <*1*> no está iniciada todavía para el proceso <*2*>"),
        INCUBATORBATCH_ALREADY_IN_PROCESS("IncubatorBatchAlreadyInProcess", "The batch <*1*> is already in process for incubator <*2*> and start multiples batches per incubator is not allowed for the procedure <*3*>", ""),
        INCUBATORBATCH_ALREADY_EXIST("incubatorBatchExist", "One incubator batch called <*1*> already exist in procedure <*2*>", "Una tanda con el nombre <*1*> ya existe en el proceso <*2*>"),
        INCUBATORBATCH_NOT_FOUND("incubatorBatchNotFound", "One incubator batch called <*1*> does not exist in procedure <*2*>", "Una tanda con el nombre <*1*> no existe en el proceso <*2*>"),        
        INCUBATORBATCH_NOT_ACTIVE("incubatorBatchNotActive","The Batch <*1*> is not active","The Batch <*1*> is not active"),
        INCUBATORBATCH_TEMPLATE_NOT_ACTIVE("incubatorBatchTemplateNotActive","The Batch template <*1*> and version <*2*> is not active","The Batch template <*1*> and version <*2*> is not active"),
        BATCH_AVAILABLEFORCHANGES("batchAvailableForChanges", "The Batch <*1*> is available to alter its content", "The Batch <*1*> is available to alter its content"),
        INCUB_BATCH_NOT_ACTIVE_FOR_CHANGES("incubationBatchStart_StoppedByNotActiveForChanges", "", ""), 
        INCUB_BATCH_START_STOPPED_BY_BUSINESSRULEMODE("incubationBatchStart_StoppedByIncubationLockedBusinessRuleMode", "", ""),
        
        EMPTY_BATCH("incubBatch_emptyBatch", "", ""),
        INCUB_BATCH_STARTED_CHANGEITSCONTENT("IncubatorBatchStartedToChangeItsContent", "", ""),
        SAMPLE_NOTFOUND_IN_BATCH("incubBatch_sampleNotFoundInBatch"," Sample <*1*> not found in batch <*2*> for procedure <*3*>.", ""),
        SAMPLES_IN_BATCH_SET_AS_BATCHSTARTED("allSamplesInBatchSetAsBatchStarted", "", ""),
        SAMPLES_IN_BATCH_SET_AS_BATCHENDED("allSamplesInBatchSetAsBatchEnded", "", ""),
        CREATEBATCH_TYPECHECKER_SUCCESS("createBatchTypeCheckerSuccess", "", ""),
        INCUBATORBATCH_NOTEMPTY_TOBEREMOVED("IncubatorBatchNotEmptyToRemove", "", ""),
        BATCHTYPE_NOT_RECOGNIZED("incubatorBatchType_notRecognized", "batchType <*1*> Not recognized", "batchType <*1*> Not recognized"),
        SAMPLE_HAS_NOPENDING_INCUBATION("sampleWithNoPendingIncubation", "There is no pending incubation for sample <*1*> in procedure <*2*>", "There is no pending incubation for sample <*1*> in procedure <*2*>"), 
        MOMENT_NOTDECLARED_IN_BATCHMOMENTSLIST("incubBatch_momentNotInBatchMomentsList","The moment <*1*> is not declared in BatchIncubatorMoments", "The moment <*1*> is not declared in BatchIncubatorMoments"),
        STAGE_NOT_RECOGNIZED("incubBatch_stageNotRecognized", " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>.", " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>."),
        SAMPLE_ALREADYBATCHED("incubBatch_sampleAlreadyBatched", "The pending incubation stage is <*1*> and the sample <*2*>  is already batched in batch <*3*> for procedure <*3*>", "The pending incubation stage is <*1*> and the sample <*2*>  is already batched in batch <*3*> for procedure <*3*>"), 
        FIELD_NOT_FOUND("incubBatch_fieldNotFound","Field <*1*> not found in table <*2*> for procedure <*3*>", "Field <*1*> not found in table <*2*> for procedure <*3*>"),
        STRUCTURED_BATCH_WRONGDIMENSION("incubBatchStructured_wrongDimensions", "", ""),
        STRUCTURED_BATCH_POSITIONOCCUPIED("incubBatchStructured_positionOccupied", "", ""),
        STRUCTURED_POSITION_OVER_DIMENSION("incubBatchStructured_positionOverDimenrsion", "", ""),
        PARSE_ERROR_STRUCTUREDBATCH("incubBatchStructured_parseError", "", "")
        
        ;
        private IncubatorBatchErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
    enum BatchAuditEvents{BATCH_CREATED, BATCH_UPDATED, BATCH_STARTED, BATCH_ENDED, BATCH_SAMPLE_ADDED, BATCH_SAMPLE_MOVED, BATCH_SAMPLE_REMOVED, BATCH_SAMPLE_REMOVED_BY_OVERRIDE, BATCH_ASSIGN_INCUBATOR}
//    enum BatchIncubatorUpdateFieldsNotAllowed{a("f"), b("f")};//    enum BatchIncubatorUpdateFieldsNotAllowed{a("f"), b("f")};

    /**
     *
     * @param bName
     * @param bTemplateId
     * @param bTemplateVersion
     * @param fldName
     * @param fldValue
     * @return
     */

    public static Object[] createBatch(String bName, Integer bTemplateId, Integer bTemplateVersion, String[] fldName, Object[] fldValue){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] batchExists=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{bName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(batchExists[0].toString())){
            Object[] trapMessage = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_ALREADY_EXIST.getErrorCode(), new Object[]{bName, procInstanceName});
            return LPArray.addValueToArray1D(trapMessage, new Object[]{bName, procInstanceName});
        }
        Object[][] templateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_VERSION.getName()}, 
                new Object[]{bTemplateId, bTemplateVersion}, new String[]{TblsEnvMonitConfig.IncubBatch.FLD_ACTIVE.getName(), TblsEnvMonitConfig.IncubBatch.FLD_TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(templateInfo[0][0].toString()))
            return LPArray.array2dTo1d(templateInfo);
        if (!Boolean.valueOf(templateInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_TEMPLATE_NOT_ACTIVE.getErrorCode(), new Object[]{bTemplateId, bTemplateVersion});

        String batchType=templateInfo[0][1].toString();
        Object[] batchTypeCheckerDiagn= createBatchTypeChecker(batchType, bName, bTemplateId, bTemplateVersion, fldName, fldValue);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchTypeCheckerDiagn[0].toString())) return batchTypeCheckerDiagn;
        
        if (batchType.equalsIgnoreCase(BatchIncubatorType.UNSTRUCTURED.toString())){ 
            return DataBatchIncubatorUnstructured.createBatchUnstructured(bName, bTemplateId, bTemplateVersion, fldName, fldValue);
        }else if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) return DataBatchIncubatorStructured.createBatchStructured(bName, bTemplateId, bTemplateVersion, fldName, fldValue);
        else
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED.getErrorCode(), new Object[]{batchType});         
    }
    
    public static Object[] removeBatch(String bName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{bName},
                new String[]{TblsEnvMonitData.IncubBatch.FLD_TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())){
            Object[] trapMessage = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_NOT_FOUND.getErrorCode(), new Object[]{bName, procInstanceName});
            return LPArray.addValueToArray1D(trapMessage, new Object[]{bName, procInstanceName});
        } 
        String batchType=batchInfo[0][0].toString();
        Boolean isBatchEmpty=false;
        if (batchType.equalsIgnoreCase(BatchIncubatorType.UNSTRUCTURED.toString())){ 
            isBatchEmpty=DataBatchIncubatorUnstructured.batchIsEmptyUnstructured(bName);
        }else if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) 
            isBatchEmpty=DataBatchIncubatorStructured.batchIsEmptyStructured(bName);
        else
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED.getErrorCode(), new Object[]{batchType});   
        if (isBatchEmpty){
            return Rdbms.removeRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitConfig.IncubBatch.TBL.getName(),
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{bName});
        }else{
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_NOTEMPTY_TOBEREMOVED.getErrorCode(), new Object[]{bName, procInstanceName});        
        }
    }
    private static Object[] createBatchTypeChecker(String batchType, String bName, Integer bTemplateId, Integer bTemplateVersion, String[] fldName, Object[] fldValue){
        Object[] batchTypeExist=batchTypeExists(batchType);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchTypeExist[0].toString())) return batchTypeExist;
        
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, IncubatorBatchErrorTrapping.CREATEBATCH_TYPECHECKER_SUCCESS.getErrorCode(), null);        
    }
    
    /**
     *
     * @param bName
     * @param bTemplateId
     * @param bTemplateVersion
     * @param sampleId
     * @return
     */
    public static Object[] batchAddSample(String bName, Integer bTemplateId, Integer bTemplateVersion, Integer sampleId){
        return batchAddSample(bName, bTemplateId, bTemplateVersion, sampleId, null, null, null);
    }

    public static Object[] batchAddSample(String bName, Integer bTemplateId, Integer bTemplateVersion, Integer sampleId, Integer row, Integer col, Boolean override){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] templateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_VERSION.getName()}, 
                new Object[]{bTemplateId, bTemplateVersion}, new String[]{TblsEnvMonitConfig.IncubBatch.FLD_ACTIVE.getName(), TblsEnvMonitConfig.IncubBatch.FLD_TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(templateInfo[0][0].toString()))
            return LPArray.array2dTo1d(templateInfo);
        if (!Boolean.valueOf(templateInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_TEMPLATE_NOT_ACTIVE.getErrorCode(), new Object[]{bTemplateId, bTemplateVersion});

        Object[] batchIsAvailableForChangingContent = batchIsAvailableForChangingContent(bName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchIsAvailableForChangingContent[0].toString())) return batchIsAvailableForChangingContent;
        
        String batchType=templateInfo[0][1].toString();
        Object[] batchTypeExist=batchTypeExists(batchType);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchTypeExist[0].toString())) return batchTypeExist;
        String[] sampleInfoFieldsToRetrieve=new String[]{TblsEnvMonitData.Sample.FLD_INCUBATION_PASSED.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION2_PASSED.getName(),                    
                    TblsEnvMonitData.Sample.FLD_INCUBATION_BATCH.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION2_BATCH.getName()};
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.Sample.TBL.getName(), 
                new String[]{TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
            return LPArray.array2dTo1d(sampleInfo);
        Integer pendingIncubationStage=samplePendingBatchStage(sampleInfoFieldsToRetrieve, sampleInfo[0]);
        if (pendingIncubationStage==-1) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.SAMPLE_HAS_NOPENDING_INCUBATION.getErrorCode(), new Object[]{sampleId, procInstanceName});
        Object[] smpIsBatchable=sampleIncubStageIsBatchable(sampleId, pendingIncubationStage, sampleInfoFieldsToRetrieve, sampleInfo[0]);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(smpIsBatchable[0].toString())) return smpIsBatchable;        
        
        if (batchType.equalsIgnoreCase(BatchIncubatorType.UNSTRUCTURED.toString())){ 
            return DataBatchIncubatorUnstructured.batchAddSampleUnstructured(bName, sampleId, pendingIncubationStage);
        }else if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) return DataBatchIncubatorStructured.batchAddSampleStructured(bName, sampleId, pendingIncubationStage, row, col, override);
        else
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED.getErrorCode(), new Object[]{batchType}); 
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
    public static Object[] batchMoveSample(String bName, Integer bTemplateId, Integer bTemplateVersion, Integer sampleId, Integer newRow, Integer newCol, Boolean override){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] templateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_VERSION.getName()}, 
                new Object[]{bTemplateId, bTemplateVersion}, new String[]{TblsEnvMonitConfig.IncubBatch.FLD_ACTIVE.getName(), TblsEnvMonitConfig.IncubBatch.FLD_TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(templateInfo[0][0].toString()))
            return LPArray.array2dTo1d(templateInfo);
        if (!Boolean.valueOf(templateInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_TEMPLATE_NOT_ACTIVE.getErrorCode(), new Object[]{bTemplateId, bTemplateVersion});

        Object[] batchIsAvailableForChangingContent = batchIsAvailableForChangingContent(bName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchIsAvailableForChangingContent[0].toString())) return batchIsAvailableForChangingContent;

        String[] sampleInfoFieldsToRetrieve=new String[]{TblsEnvMonitData.Sample.FLD_INCUBATION_PASSED.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION2_PASSED.getName(),                    
                    TblsEnvMonitData.Sample.FLD_INCUBATION_BATCH.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION2_BATCH.getName()};
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.Sample.TBL.getName(), 
                new String[]{TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
            return LPArray.array2dTo1d(sampleInfo);
        Integer pendingIncubationStage=samplePendingBatchStage(sampleInfoFieldsToRetrieve, sampleInfo[0]);
        if (pendingIncubationStage==-1) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.SAMPLE_HAS_NOPENDING_INCUBATION.getErrorCode(), new Object[]{sampleId, procInstanceName});

        String batchType=templateInfo[0][1].toString();
        Object[] batchTypeExist=batchTypeExists(batchType);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchTypeExist[0].toString())) return batchTypeExist;
        if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) return DataBatchIncubatorStructured.batchMoveSampleStructured(bName, sampleId, pendingIncubationStage, newRow, newCol, override);
                else
                    return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED.getErrorCode(), new Object[]{batchType});         
    }
    
    public static Object[] batchRemoveSample(String bName, Integer sampleId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitConfig.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{bName}, 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_VERSION.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString()))
            return LPArray.array2dTo1d(batchInfo);        
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
    public static Object[] batchRemoveSample(String bName, Integer bTemplateId, Integer bTemplateVersion, Integer sampleId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] templateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_VERSION.getName()}, 
                new Object[]{bTemplateId, bTemplateVersion}, new String[]{TblsEnvMonitConfig.IncubBatch.FLD_ACTIVE.getName(), TblsEnvMonitConfig.IncubBatch.FLD_TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(templateInfo[0][0].toString()))
            return LPArray.array2dTo1d(templateInfo);
        if (!Boolean.valueOf(templateInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_TEMPLATE_NOT_ACTIVE.getErrorCode(), new Object[]{bTemplateId, bTemplateVersion});

        String batchType=templateInfo[0][1].toString();
        Object[] batchTypeExist=batchTypeExists(batchType);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchTypeExist[0].toString())) return batchTypeExist;

        Object[] batchIsAvailableForChangingContent = batchIsAvailableForChangingContent(bName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchIsAvailableForChangingContent[0].toString())) return batchIsAvailableForChangingContent;

        String[] sampleInfoFieldsToRetrieve=new String[]{TblsEnvMonitData.Sample.FLD_INCUBATION_PASSED.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION2_PASSED.getName(),                    
                    TblsEnvMonitData.Sample.FLD_INCUBATION_BATCH.getName(), TblsEnvMonitData.Sample.FLD_INCUBATION2_BATCH.getName()};
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.Sample.TBL.getName(), 
                new String[]{TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
            return LPArray.array2dTo1d(sampleInfo);        
        Integer pendingIncubationStage=samplePendingBatchStage(sampleInfoFieldsToRetrieve, sampleInfo[0]);
        if (pendingIncubationStage==-1) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.SAMPLE_HAS_NOPENDING_INCUBATION.getErrorCode(), new Object[]{sampleId, procInstanceName});

        if (batchType.equalsIgnoreCase(BatchIncubatorType.UNSTRUCTURED.toString())) 
            return DataBatchIncubatorUnstructured.batchRemoveSampleUnstructured(bName, sampleId);
        else if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) return DataBatchIncubatorStructured.batchRemoveSampleStructured(bName, sampleId, pendingIncubationStage);
        else
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED.getErrorCode(), new Object[]{batchType}); 
        
    }
    
    /**
     *
     * @param bName
     * @param bTemplateId
     * @param bTemplateVersion
     * @return 
     */
    public static Object[] batchStarted(String bName, Integer bTemplateId, Integer bTemplateVersion){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{bName}, new String[]{TblsEnvMonitData.IncubBatch.FLD_INCUBATION_START.getName(), TblsEnvMonitData.IncubBatch.FLD_INCUBATION_INCUBATOR.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())) return LPArray.array2dTo1d(batchInfo);
        String batchIncubName=batchInfo[0][1].toString();
        if ( (batchInfo[0][0]!=null) && (batchInfo[0][0].toString().trim().length()>0) ) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_ALREADY_STARTED.getErrorCode(), new Object[]{bName, procInstanceName});        
        String allowMultipleStartBatch=Parameter.getBusinessRuleProcedureFile(procInstanceName, BatchBusinessRules.START_MULTIPLE_BATCH_IN_PARALLEL.getAreaName(), BatchBusinessRules.START_MULTIPLE_BATCH_IN_PARALLEL.getTagName());
        if (!"YES".equalsIgnoreCase(allowMultipleStartBatch)){
            Object[][] batchInProcess = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                    new String[]{TblsEnvMonitData.IncubBatch.FLD_INCUBATION_INCUBATOR.getName(), TblsEnvMonitData.IncubBatch.FLD_INCUBATION_START.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause(), TblsEnvMonitData.IncubBatch.FLD_INCUBATION_END.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, new Object[]{batchIncubName, "", ""},
                    new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()});            
            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInProcess[0][0].toString())) {
                Object[] diagn=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_ALREADY_IN_PROCESS.getErrorCode(), new Object[]{batchInProcess[0][0], batchIncubName, procInstanceName});
                diagn=LPArray.addValueToArray1D(diagn, batchInProcess[0][0].toString());
                return LPArray.addValueToArray1D(diagn, batchIncubName);
            }                    
        }
        Object[] incubIsLocked=incubatorIsLocked(bName, batchIncubName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubIsLocked[0].toString())) return incubIsLocked;
        return batchMomentMarked(bName, bTemplateId, bTemplateVersion, BatchIncubatorMoments.START.toString());
    }
    
    /**
     *
     * @param bName
     * @param bTemplateId
     * @param bTemplateVersion
     * @return
     */
    public static Object[] batchEnded(String bName, Integer bTemplateId, Integer bTemplateVersion){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{bName}, new String[]{TblsEnvMonitData.IncubBatch.FLD_INCUBATION_START.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())) return LPArray.array2dTo1d(batchInfo);
        if ( (batchInfo[0][0]==null) || (batchInfo[0][0].toString().trim().length()<1) ) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_NOT_STARTED.getErrorCode(), new Object[]{bName, procInstanceName});
        return batchMomentMarked(bName, bTemplateId, bTemplateVersion, BatchIncubatorMoments.END.toString());
    }
    
    private static Object[] batchMomentMarked(String bName, Integer bTemplateId, Integer bTemplateVersion, String moment){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] templateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_VERSION.getName()}, 
                new Object[]{bTemplateId, bTemplateVersion}, new String[]{TblsEnvMonitConfig.IncubBatch.FLD_ACTIVE.getName(), TblsEnvMonitConfig.IncubBatch.FLD_TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(templateInfo[0][0].toString()))
            return LPArray.array2dTo1d(templateInfo);
        if (!Boolean.valueOf(templateInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_TEMPLATE_NOT_ACTIVE.getErrorCode(), new Object[]{bTemplateId, bTemplateVersion});
        Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{bName}, 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_INCUBATION_INCUBATOR.getName()});            
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString()))
            return LPArray.array2dTo1d(batchInfo);
        String incubName=batchInfo[0][0].toString();
        String batchType=templateInfo[0][1].toString();
        Object[] batchTypeExist=batchTypeExists(batchType);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchTypeExist[0].toString())) return batchTypeExist;
        Object[] batchSampleIncubationMomentMarkedDiagn= new Object[]{};

        if (BatchIncubatorMoments.START.toString().equalsIgnoreCase(moment)){
            if (batchType.equalsIgnoreCase(BatchIncubatorType.UNSTRUCTURED.toString())) 
                batchSampleIncubationMomentMarkedDiagn=DataBatchIncubatorUnstructured.batchSampleIncubStartedUnstructured(bName, incubName);
            else if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) 
                batchSampleIncubationMomentMarkedDiagn=DataBatchIncubatorStructured.batchSampleIncubStartedStructured();
            else
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED.getErrorCode(), new Object[]{batchType}); 
        }else if (BatchIncubatorMoments.END.toString().equalsIgnoreCase(moment)){
            if (batchType.equalsIgnoreCase(BatchIncubatorType.UNSTRUCTURED.toString())) 
                batchSampleIncubationMomentMarkedDiagn=DataBatchIncubatorUnstructured.batchSampleIncubEndedUnstructured(bName, incubName);
            else if (batchType.equalsIgnoreCase(BatchIncubatorType.STRUCTURED.toString())) 
                batchSampleIncubationMomentMarkedDiagn=DataBatchIncubatorStructured.batchSampleIncubEndedStructured();
            else
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED.getErrorCode(), new Object[]{batchType}); 
        } else return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.MOMENT_NOTDECLARED_IN_BATCHMOMENTSLIST.getErrorCode(), new Object[]{moment});
        

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchSampleIncubationMomentMarkedDiagn[0].toString())) return batchSampleIncubationMomentMarkedDiagn;
        String[] requiredFields = new String[0];
        Object[] requiredFieldsValue= new Object[0];
        String batchAuditEvent="";
        if (BatchIncubatorMoments.START.toString().equalsIgnoreCase(moment)){
            requiredFields = new String[]{TblsEnvMonitData.IncubBatch.FLD_INCUBATION_INCUBATOR.getName(), TblsEnvMonitData.IncubBatch.FLD_INCUBATION_START.getName()};
            requiredFieldsValue= new Object[]{incubName, LPDate.getCurrentTimeStamp()}; 
            batchAuditEvent=BatchAuditEvents.BATCH_STARTED.toString();
        }else if (BatchIncubatorMoments.END.toString().equalsIgnoreCase(moment)){
            requiredFields = new String[]{TblsEnvMonitData.IncubBatch.FLD_INCUBATION_END.getName(), TblsEnvMonitData.IncubBatch.FLD_ACTIVE.getName(), TblsEnvMonitData.IncubBatch.FLD_COMPLETED.getName()};
            requiredFieldsValue= new Object[]{LPDate.getCurrentTimeStamp(), false, true};                
            batchAuditEvent=BatchAuditEvents.BATCH_ENDED.toString();
        } else return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.MOMENT_NOTDECLARED_IN_BATCHMOMENTSLIST.getErrorCode(), new Object[]{moment});
        
        Object[] updateDiagnostic=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                requiredFields, requiredFieldsValue, 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{bName});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateDiagnostic[0].toString()))
            IncubBatchAudit.incubBatchAuditAdd(batchAuditEvent, TblsEnvMonitData.IncubBatch.TBL.getName(), bName,
                LPArray.joinTwo1DArraysInOneOf1DString(requiredFields, requiredFieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        return updateDiagnostic;
    }
    
    private static Object[] batchTypeExists(String batchType){
        Boolean typeExists = false;
        BatchIncubatorType[] arr = BatchIncubatorType.values();
        for (BatchIncubatorType curType: arr){
            if (batchType.equalsIgnoreCase(curType.toString())){
                typeExists=true;
                break;
            }
        }
        if (typeExists)return new Object[]{LPPlatform.LAB_TRUE};
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.BATCHTYPE_NOT_RECOGNIZED.getErrorCode(), new Object[]{batchType});        
    }
    
    private static Integer samplePendingBatchStage(String[] fieldsName, Object[] fieldsValue){
        Integer posic = LPArray.valuePosicInArray(fieldsName, TblsEnvMonitData.Sample.FLD_INCUBATION_PASSED.getName());
        if (posic==-1) return posic;
        if ((fieldsValue[posic]==null) || (!Boolean.valueOf(fieldsValue[posic].toString())) ) return 1;

        posic = LPArray.valuePosicInArray(fieldsName, TblsEnvMonitData.Sample.FLD_INCUBATION2_PASSED.getName());
        if (posic==-1) return posic;
        if ((fieldsValue[posic]==null) || (!Boolean.valueOf(fieldsValue[posic].toString())) ) return 2;
        return -1;
    }
    
    private static Object[] sampleIncubStageIsBatchable(Integer sampleId, Integer incubStage, String[] fieldsName, Object[] fieldsValue){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String batchFldName="";
        if (null==incubStage)
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STAGE_NOT_RECOGNIZED.getErrorCode(),
                    new Object[]{incubStage, procInstanceName});         
        else switch (incubStage) {
            case 1:
                batchFldName=TblsEnvMonitData.Sample.FLD_INCUBATION_BATCH.getName();
                break;
            case 2:
                batchFldName=TblsEnvMonitData.Sample.FLD_INCUBATION2_BATCH.getName();
                break;
            default:
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STAGE_NOT_RECOGNIZED.getErrorCode(),
                        new Object[]{incubStage, procInstanceName});
        }

        Integer posic = LPArray.valuePosicInArray(fieldsName, batchFldName);
        if (posic==-1) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.FIELD_NOT_FOUND.getErrorCode(),
                new Object[]{batchFldName, TblsEnvMonitData.Sample.TBL.getName(), procInstanceName});
        if ( (fieldsValue[posic]==null) || (fieldsValue[posic].toString().length()==0) ) return new Object[]{LPPlatform.LAB_TRUE};
        else return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.SAMPLE_ALREADYBATCHED.getErrorCode(),
                new Object[]{incubStage, sampleId, fieldsValue[posic], procInstanceName});
    }
    
        
    /**
     *
     * @param batchName
     * @param incubName
     * @return
     */
    public static Object[] batchAssignIncubator(String batchName, String incubName, String incubStage){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitConfig.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, 
                new String[]{TblsEnvMonitConfig.IncubBatch.FLD_ACTIVE.getName(), TblsEnvMonitConfig.IncubBatch.FLD_TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString()))
            return LPArray.array2dTo1d(batchInfo);
        if (!Boolean.valueOf(batchInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_NOT_ACTIVE.getErrorCode(), new Object[]{batchName});
        String[] updFieldName=new String[]{TblsEnvMonitData.IncubBatch.FLD_INCUBATION_INCUBATOR.getName(), TblsEnvMonitData.IncubBatch.FLD_INCUB_STAGE.getName()};
        Object[] updFieldValue=new Object[]{incubName, incubStage};
        Object[] updateDiagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                updFieldName, updFieldValue, 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName});        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateDiagn[0].toString()))
            IncubBatchAudit.incubBatchAuditAdd(BatchAuditEvents.BATCH_ASSIGN_INCUBATOR.toString(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName,  
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        return updateDiagn;
    }
    
    /**
     *
     * @param batchName
     * @param fieldsName
     * @param fieldsValue
     * @return
     */
    public static Object[] batchUpdateInfo(String batchName, String[] fieldsName, Object[] fieldsValue){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitConfig.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, 
                new String[]{TblsEnvMonitConfig.IncubBatch.FLD_ACTIVE.getName(), TblsEnvMonitConfig.IncubBatch.FLD_TYPE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString()))
            return LPArray.array2dTo1d(batchInfo);
        if (!Boolean.valueOf(batchInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUBATORBATCH_NOT_ACTIVE.getErrorCode(), new Object[]{batchName});        
        Object[] updateDiagnostic=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                fieldsName, fieldsValue, 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateDiagnostic[0].toString()))
            IncubBatchAudit.incubBatchAuditAdd(BatchAuditEvents.BATCH_UPDATED.toString(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName,
                LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        return updateDiagnostic;
    }
    
    public static Object[] batchIsAvailableForChangingContent(String batchName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        Object[][] batchInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_ACTIVE.getName(), TblsEnvMonitData.IncubBatch.FLD_INCUBATION_START.getName()});
        if (batchInfo[0][0]==null || !Boolean.valueOf(batchInfo[0][0].toString())) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUB_BATCH_NOT_ACTIVE_FOR_CHANGES.getErrorCode(), new Object[]{batchName}); 
        if (batchInfo[0][1]!=null && batchInfo[0][1].toString().length()>0) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUB_BATCH_STARTED_CHANGEITSCONTENT.getErrorCode(), new Object[]{batchName}); 
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, IncubatorBatchErrorTrapping.BATCH_AVAILABLEFORCHANGES.getErrorCode(), new Object[]{batchName}); 
    }
    public static Object[] incubatorIsLocked(String batchName, String instName){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=instanceForActions.getProcedureInstance();        
        Object[] procedureBusinessRuleEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, ConfigIncubatorBusinessRules.LOCK_WHEN_TEMP_OUT_OF_RANGE.getAreaName(), ConfigIncubatorBusinessRules.LOCK_WHEN_TEMP_OUT_OF_RANGE.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureBusinessRuleEnable[0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "incubationLockingNotEnabled", null);
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED.getName())[0].toString())) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "lockedFieldNotPresentInIncubatorForProcedure", new Object[]{procInstanceName});
        String[] incubFldNames=new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED_REASON.getName()};        
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
            new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
            incubFldNames);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())) return instrInfo[0];
        if (!Boolean.valueOf(LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(incubFldNames, TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED.getName())]).toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "incubationNotLocked", null);
        String ruleValue=Parameter.getBusinessRuleProcedureFile(procInstanceName, BatchBusinessRules.START_FOR_LOCKED_INCUBATOR_MODE.getAreaName(), BatchBusinessRules.START_FOR_LOCKED_INCUBATOR_MODE.getTagName());
        if (ruleValue.length()==0)
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "incubationLockingEnabledButModeNotDefined", null);
        if (ruleValue.toUpperCase().contains("DEVIATION")){
            Object[] createNew = ProcedureDeviationIncubator.createNew(instName, new String[]{TblsEnvMonitProcedure.ProcedureDeviationIncubator.FLD_BATCH_NAME.getName()}, new Object[]{batchName});
        }
        if (ruleValue.toUpperCase().contains("STOP")){
            ResponseMessages messages = instanceForActions.getMessages();
            messages.addMainForError(IncubatorBatchErrorTrapping.INCUB_BATCH_START_STOPPED_BY_BUSINESSRULEMODE.getErrorCode(), new Object[]{instrInfo[0][1].toString(), ruleValue});
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.INCUB_BATCH_START_STOPPED_BY_BUSINESSRULEMODE.getErrorCode(), new Object[]{instrInfo[0][1].toString(), ruleValue});
        }
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "ByPassedByIncubationLockedBusinessRuleMode Rule:<*1*>, Value:<*2*>", new Object[]{BatchBusinessRules.START_FOR_LOCKED_INCUBATOR_MODE.getTagName(), ruleValue});
    }
    
}
