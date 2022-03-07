/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.inventory.batch;

import com.labplanet.servicios.moduleenvmonit.EnvMonAPI;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import databases.Rdbms;
import functionaljavaa.audit.IncubBatchAudit;
import functionaljavaa.inventory.batch.DataBatchIncubator.IncubatorBatchErrorTrapping;
import functionaljavaa.samplestructure.DataSampleIncubation;
import functionaljavaa.samplestructure.DataSampleStages;
import java.math.BigDecimal;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author User
 */
public final class DataBatchIncubatorUnstructured {
    private DataBatchIncubatorUnstructured() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}

    static Boolean batchIsEmptyUnstructured(String batchName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, 
                new String[]{TblsEnvMonitData.IncubBatch.UNSTRUCT_CONTENT.getName()});
        return (!LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(batchInfo[0][0]).toString())) && 
                (LPNulls.replaceNull(batchInfo[0][0]).toString().length()==0);
    }
    static Object[] batchAddSampleUnstructured(String batchName, Integer sampleId, Integer incubStage) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String separator = "*";
        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.UNSTRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return LPArray.array2dTo1d(sampleInfo);
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        if (batchSamples.length() > 0) {
            batchSamples = batchSamples + "|";
        }
        batchSamples = batchSamples + sampleId.toString() + separator + incubStage.toString();
        String[] updFieldName = new String[]{TblsEnvMonitData.IncubBatch.UNSTRUCT_CONTENT.getName()};
        Object[] updFieldValue = new Object[]{batchSamples};
        Object[] updateBatchSamples = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), updFieldName, updFieldValue, new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateBatchSamples[0].toString())) {
            return updateBatchSamples;
        }
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateBatchSamples[0].toString())) {
            IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.DataBatchAuditEvents.BATCH_SAMPLE_ADDED.toString(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        }
        String batchFldName = "";
        if (incubStage == 1) {
            batchFldName = TblsEnvMonitData.Sample.INCUBATION_BATCH.getName();
        } else if (incubStage == 2) {
            batchFldName = TblsEnvMonitData.Sample.INCUBATION2_BATCH.getName();
        } else {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STAGE_NOT_RECOGNIZED, new Object[]{incubStage, procInstanceName});
        }
        return Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), new String[]{batchFldName}, new Object[]{batchName}, new String[]{TblsEnvMonitData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId});
    }

    static Object[] batchRemoveSampleUnstructured(String batchName, Integer sampleId) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.UNSTRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return LPArray.array2dTo1d(sampleInfo);
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        Integer samplePosic = batchSamples.indexOf(sampleId.toString());
        if (samplePosic == -1) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.SAMPLE_NOTFOUND_IN_BATCH, new Object[]{sampleId, batchName, procInstanceName});
        }
        String samplePosicInfo = batchSamples.substring(samplePosic, samplePosic + sampleId.toString().length() + 2);
        String[] samplePosicInfoArr = samplePosicInfo.split("\\*");
        if (samplePosicInfoArr.length != 2) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.PARSE_ERROR_STRUCTUREDBATCH, new Object[]{"Removing Sample", samplePosicInfo, batchSamples, procInstanceName});
        }
        Integer incubStage = Integer.valueOf(samplePosicInfoArr[1]);
        if (samplePosic == 0) {
            if (batchSamples.length() == samplePosicInfo.length()) {
                batchSamples = batchSamples.substring(samplePosic + samplePosicInfo.length());
            } else {
                batchSamples = batchSamples.substring(samplePosic + samplePosicInfo.length() + 1);
            }
        } else {
            batchSamples = batchSamples.substring(0, samplePosic - 1) + batchSamples.substring(samplePosic + samplePosicInfo.length());
        }
        String[] updFieldName = new String[]{TblsEnvMonitData.IncubBatch.UNSTRUCT_CONTENT.getName()};
        Object[] updFieldValue = new Object[]{batchSamples};
        Object[] updateBatchSamples = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), updFieldName, updFieldValue, new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateBatchSamples[0].toString())) {
            return updateBatchSamples;
        }
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateBatchSamples[0].toString())) {
            IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.DataBatchAuditEvents.BATCH_SAMPLE_REMOVED.toString(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        }
        String batchFldName = "";
        if (incubStage == 1) {
            batchFldName = TblsEnvMonitData.Sample.INCUBATION_BATCH.getName();
        } else if (incubStage == 2) {
            batchFldName = TblsEnvMonitData.Sample.INCUBATION2_BATCH.getName();
        } else {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STAGE_NOT_RECOGNIZED, new Object[]{incubStage, procInstanceName});
        }
        return Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), new String[]{batchFldName}, new Object[]{null}, new String[]{TblsEnvMonitData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId});        
    }
    static Object[] batchSampleIncubStartedUnstructured(String batchName, String incubName) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.UNSTRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return LPArray.array2dTo1d(sampleInfo);
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        if (batchSamples.length() == 0) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.EMPTY_BATCH, new Object[]{batchName, procInstanceName});
        }
        String[] batchSamplesArr = batchSamples.split("\\|");
        for (String currSample : batchSamplesArr) {
            String[] currSampleArr = currSample.split("\\*");
            if (currSampleArr.length != 2) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.PARSE_ERROR_STRUCTUREDBATCH, new Object[]{"Starting Batch", currSample, batchSamples, procInstanceName});
            }
            Integer sampleId = Integer.valueOf(currSampleArr[0]);
            Integer incubStage = Integer.valueOf(currSampleArr[1]);
            BigDecimal tempReading = null;
            Object[] setSampleIncubStarted = DataSampleIncubation.setSampleStartIncubationDateTime(sampleId, incubStage, incubName, tempReading, batchName);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(setSampleIncubStarted[0].toString())) {
                return setSampleIncubStarted;
            }else{
                Object[] auditInfo=(Object[]) setSampleIncubStarted[1];
                setSampleIncubStarted=(Object[]) setSampleIncubStarted[0];
                DataSampleStages smpStage=new DataSampleStages();
                if (smpStage.isSampleStagesEnable() && (sampleId!=null))
                    smpStage.dataSampleActionAutoMoveToNext(EnvMonAPI.EnvMonAPIEndpoints.EM_BATCH_INCUB_START.getName(), sampleId);
            }
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataBatchIncubator.IncubatorBatchErrorTrapping.SAMPLES_IN_BATCH_SET_AS_BATCHSTARTED, new Object[]{batchName});
    }
    
    static Object[] batchSampleIncubEndedUnstructured(String batchName, String incubName) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.UNSTRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return LPArray.array2dTo1d(sampleInfo);
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        String[] batchSamplesArr = batchSamples.split("\\|");
        for (String currSample : batchSamplesArr) {
            String[] currSampleArr = currSample.split("\\*");
            if (currSampleArr.length != 2) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.PARSE_ERROR_STRUCTUREDBATCH, new Object[]{"Ending Batch", currSample, batchSamples, procInstanceName});
            }
            Integer sampleId = Integer.valueOf(currSampleArr[0]);
            Integer incubStage = Integer.valueOf(currSampleArr[1]);
            BigDecimal tempReading = null;
            Object[] setSampleIncubEnded = DataSampleIncubation.setSampleEndIncubationDateTime(sampleId, incubStage, incubName, tempReading, batchName);
            Object[] auditInfo=(Object[]) setSampleIncubEnded[1];
            setSampleIncubEnded=(Object[])setSampleIncubEnded[0];
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(setSampleIncubEnded[0].toString())) {
                return setSampleIncubEnded;
            }else{
                DataSampleStages smpStage=new DataSampleStages();
                if (smpStage.isSampleStagesEnable() && (sampleId!=null))
                    smpStage.dataSampleActionAutoMoveToNext(EnvMonAPI.EnvMonAPIEndpoints.EM_BATCH_INCUB_END.getName(), sampleId);
            }
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, IncubatorBatchErrorTrapping.SAMPLES_IN_BATCH_SET_AS_BATCHENDED, new Object[]{batchName});
    }

    
    static Object[] createBatchUnstructured(String bName, Integer bTemplateId, Integer bTemplateVersion, String[] fldName, Object[] fldValue) {        
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_ID.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_ID.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, bTemplateId);
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_ID.getName())] = bTemplateId;
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_VERSION.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_VERSION.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, bTemplateVersion);
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.INCUB_BATCH_CONFIG_VERSION.getName())] = bTemplateVersion;
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.NAME.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.NAME.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, bName);
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.NAME.getName())] = bName;
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.TYPE.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.TYPE.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, DataBatchIncubator.BatchIncubatorType.UNSTRUCTURED.toString());
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.TYPE.getName())] = DataBatchIncubator.BatchIncubatorType.STRUCTURED.toString();
        } 
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.ACTIVE.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.ACTIVE.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, true);
        }         
        Object[] createBatchDiagn = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), fldName, fldValue);
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(createBatchDiagn[0].toString())) {
            IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.DataBatchAuditEvents.BATCH_CREATED.toString(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), bName, LPArray.joinTwo1DArraysInOneOf1DString(fldName, fldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        }
        return createBatchDiagn;
    }

    
}
