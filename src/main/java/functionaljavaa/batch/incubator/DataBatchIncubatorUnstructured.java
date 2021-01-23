/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.batch.incubator;

import com.labplanet.servicios.moduleenvmonit.EnvMonAPI.EnvMonAPIEndpoints;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import databases.Rdbms;
import functionaljavaa.audit.IncubBatchAudit;
import functionaljavaa.samplestructure.DataSampleIncubation;
import functionaljavaa.samplestructure.DataSampleStages;
import java.math.BigDecimal;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class DataBatchIncubatorUnstructured {

    static Boolean batchIsEmptyUnstructured(String batchName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_UNSTRUCT_CONTENT.getName()});
        return (!LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(batchInfo[0][0]).toString())) && 
                (LPNulls.replaceNull(batchInfo[0][0]).toString().length()==0);
    }
    static Object[] batchAddSampleUnstructured(String batchName, Integer sampleId, Integer incubStage) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String separator = "*";
        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.FLD_UNSTRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return LPArray.array2dTo1d(sampleInfo);
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        if (batchSamples.length() > 0) {
            batchSamples = batchSamples + "|";
        }
        batchSamples = batchSamples + sampleId.toString() + separator + incubStage.toString();
        String[] updFieldName = new String[]{TblsEnvMonitData.IncubBatch.FLD_UNSTRUCT_CONTENT.getName()};
        Object[] updFieldValue = new Object[]{batchSamples};
        Object[] updateBatchSamples = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), updFieldName, updFieldValue, new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateBatchSamples[0].toString())) {
            return updateBatchSamples;
        }
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateBatchSamples[0].toString())) {
            IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.BatchAuditEvents.BATCH_SAMPLE_ADDED.toString(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null);
        }
        String batchFldName = "";
        if (incubStage == 1) {
            batchFldName = TblsEnvMonitData.Sample.FLD_INCUBATION_BATCH.getName();
        } else if (incubStage == 2) {
            batchFldName = TblsEnvMonitData.Sample.FLD_INCUBATION2_BATCH.getName();
        } else {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>.", new Object[]{incubStage, procInstanceName});
        }
        return Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.Sample.TBL.getName(), new String[]{batchFldName}, new Object[]{batchName}, new String[]{TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
    }

    static Object[] batchRemoveSampleUnstructured(String batchName, Integer sampleId) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.FLD_UNSTRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return LPArray.array2dTo1d(sampleInfo);
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        Integer samplePosic = batchSamples.indexOf(sampleId.toString());
        if (samplePosic == -1) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " Sample <*1*> not found in batch <*2*> for procedure <*3*>.", new Object[]{sampleId, batchName, procInstanceName});
        }
        String samplePosicInfo = batchSamples.substring(samplePosic, samplePosic + sampleId.toString().length() + 2);
        String[] samplePosicInfoArr = samplePosicInfo.split("\\*");
        if (samplePosicInfoArr.length != 2) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " batchRemoveSampleUnstructured cannot parse the info for the Sample <*1*> when there are more than 2 pieces of info. Batch Samples info is <*2*> for procedure <*3*>.", new Object[]{samplePosicInfo, batchSamples, procInstanceName});
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
        String[] updFieldName = new String[]{TblsEnvMonitData.IncubBatch.FLD_UNSTRUCT_CONTENT.getName()};
        Object[] updFieldValue = new Object[]{batchSamples};
        Object[] updateBatchSamples = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), updFieldName, updFieldValue, new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateBatchSamples[0].toString())) {
            return updateBatchSamples;
        }
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateBatchSamples[0].toString())) {
            IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.BatchAuditEvents.BATCH_SAMPLE_REMOVED.toString(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null);
        }
        String batchFldName = "";
        if (incubStage == 1) {
            batchFldName = TblsEnvMonitData.Sample.FLD_INCUBATION_BATCH.getName();
        } else if (incubStage == 2) {
            batchFldName = TblsEnvMonitData.Sample.FLD_INCUBATION2_BATCH.getName();
        } else {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>.", new Object[]{incubStage, procInstanceName});
        }
        return Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.Sample.TBL.getName(), new String[]{batchFldName}, new Object[]{null}, new String[]{TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});        
    }
    static Object[] batchSampleIncubStartedUnstructured(String batchName, String incubName) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.FLD_UNSTRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return LPArray.array2dTo1d(sampleInfo);
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        if (batchSamples.length() == 0) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The batch <*1*> has no samples therefore cannot be started yet, procedure <*2*>", new Object[]{batchName, procInstanceName});
        }
        String[] batchSamplesArr = batchSamples.split("\\|");
        for (String currSample : batchSamplesArr) {
            String[] currSampleArr = currSample.split("\\*");
            if (currSampleArr.length != 2) {
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " batchSampleIncubStartedUnstructured cannot parse the info for the Sample <*1*> when there are more than 2 pieces of info. Batch Samples info is <*2*> for procedure <*3*>.", new Object[]{currSample, batchSamples, procInstanceName});
            }
            Integer sampleId = Integer.valueOf(currSampleArr[0]);
            Integer incubStage = Integer.valueOf(currSampleArr[1]);
            BigDecimal tempReading = null;
            Object[] setSampleIncubStarted = DataSampleIncubation.setSampleStartIncubationDateTime(sampleId, incubStage, incubName, tempReading);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(setSampleIncubStarted[0].toString())) {
                return setSampleIncubStarted;
            }else{
                DataSampleStages smpStage=new DataSampleStages();
                if (smpStage.isSampleStagesEnable() && (sampleId!=null))
                    smpStage.dataSampleActionAutoMoveToNext(EnvMonAPIEndpoints.EM_BATCH_INCUB_START.getName(), sampleId);                
            }
        }
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "All sample set as incubation started", null);
    }
    
    static Object[] batchSampleIncubEndedUnstructured(String batchName, String incubName) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.FLD_UNSTRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return LPArray.array2dTo1d(sampleInfo);
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        String[] batchSamplesArr = batchSamples.split("\\|");
        for (String currSample : batchSamplesArr) {
            String[] currSampleArr = currSample.split("\\*");
            if (currSampleArr.length != 2) {
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " batchSampleIncubEndedUnstructured cannot parse the info for the Sample <*1*> when there are more than 2 pieces of info. Batch Samples info is <*2*> for procedure <*3*>.", new Object[]{currSample, batchSamples, procInstanceName});
            }
            Integer sampleId = Integer.valueOf(currSampleArr[0]);
            Integer incubStage = Integer.valueOf(currSampleArr[1]);
            BigDecimal tempReading = null;
            Object[] setSampleIncubEnded = DataSampleIncubation.setSampleEndIncubationDateTime(sampleId, incubStage, incubName, tempReading);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(setSampleIncubEnded[0].toString())) {
                return setSampleIncubEnded;
            }else{
                DataSampleStages smpStage=new DataSampleStages();
                if (smpStage.isSampleStagesEnable() && (sampleId!=null))
                    smpStage.dataSampleActionAutoMoveToNext(EnvMonAPIEndpoints.EM_BATCH_INCUB_END.getName(), sampleId);                                                
            }
        }
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "All sample set as incubation ended", null);
    }

    
    static Object[] createBatchUnstructured(String bName, Integer bTemplateId, Integer bTemplateVersion, String[] fldName, Object[] fldValue) {        
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, bTemplateId);
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName())] = bTemplateId;
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_VERSION.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_VERSION.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, bTemplateVersion);
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_VERSION.getName())] = bTemplateVersion;
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_NAME.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.FLD_NAME.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, bName);
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_NAME.getName())] = bName;
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_TYPE.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.FLD_TYPE.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, DataBatchIncubator.BatchIncubatorType.UNSTRUCTURED.toString());
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_TYPE.getName())] = DataBatchIncubator.BatchIncubatorType.STRUCTURED.toString();
        } 
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_ACTIVE.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.FLD_ACTIVE.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, true);
        }         
        Object[] createBatchDiagn = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), fldName, fldValue);
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(createBatchDiagn[0].toString())) {
            IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.BatchAuditEvents.BATCH_CREATED.toString(), TblsEnvMonitData.IncubBatch.TBL.getName(), bName, LPArray.joinTwo1DArraysInOneOf1DString(fldName, fldValue, ":"), null);
        }
        return createBatchDiagn;
    }

    
}
