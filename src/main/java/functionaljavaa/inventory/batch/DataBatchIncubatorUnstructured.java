/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.inventory.batch;

import com.labplanet.servicios.moduleenvmonit.EnvMonAPI;
import module.monitoring.definition.TblsEnvMonitData;
import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import functionaljavaa.audit.IncubBatchAudit;
import functionaljavaa.inventory.batch.DataBatchIncubator.IncubatorBatchErrorTrapping;
import functionaljavaa.inventory.batch.DataBatchIncubator.IncubatorBatchSuccess;
import functionaljavaa.samplestructure.DataSampleIncubation;
import functionaljavaa.samplestructure.DataSampleStages;
import java.math.BigDecimal;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
/**
 *
 * @author User
 */
public final class DataBatchIncubatorUnstructured {
    private DataBatchIncubatorUnstructured() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}

    static Boolean batchIsEmptyUnstructured(String batchName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, 
                new String[]{TblsEnvMonitData.IncubBatch.UNSTRUCT_CONTENT.getName()});
        return (!LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(batchInfo[0][0]).toString())) && 
                (LPNulls.replaceNull(batchInfo[0][0]).toString().length()==0);
    }
    static InternalMessage batchAddSampleUnstructured(String batchName, Integer sampleId, Integer incubStage) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String separator = "*";
        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.UNSTRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), 
                new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{batchName});
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        if (batchSamples.length() > 0) {
            batchSamples = batchSamples + "|";
        }
        batchSamples = batchSamples + sampleId.toString() + separator + incubStage.toString();
        String[] updFieldName = new String[]{TblsEnvMonitData.IncubBatch.UNSTRUCT_CONTENT.getName()};
        Object[] updFieldValue = new Object[]{batchSamples};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsEnvMonitData.IncubBatch.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{batchName}, "");
	RdbmsObject updateBatchSamples=Rdbms.updateTableRecordFieldsByFilter(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH,
		EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH, updFieldName), updFieldValue, sqlWhere, null);        
        
        if (Boolean.FALSE.equals(updateBatchSamples.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateBatchSamples.getErrorMessageCode(), updateBatchSamples.getErrorMessageVariables());
        }
        if (Boolean.TRUE.equals(updateBatchSamples.getRunSuccess())) {
            IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.DataBatchAuditEvents.BATCH_SAMPLE_ADDED.toString(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        }
        String batchFldName = "";
        if (incubStage == 1) {
            batchFldName = TblsEnvMonitData.Sample.INCUBATION_BATCH.getName();
        } else if (incubStage == 2) {
            batchFldName = TblsEnvMonitData.Sample.INCUBATION2_BATCH.getName();
        } else {
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STAGE_NOT_RECOGNIZED, new Object[]{incubStage, procInstanceName});
        }
	sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsEnvMonitData.Sample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
        RdbmsObject updateTableRecordFieldsByFilter = Rdbms.updateTableRecordFieldsByFilter(TblsEnvMonitData.TablesEnvMonitData.SAMPLE,
                EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.SAMPLE, new String[]{batchFldName}), new Object[]{batchName}, sqlWhere, null);
        return new InternalMessage(updateTableRecordFieldsByFilter.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, updateTableRecordFieldsByFilter.getErrorMessageCode(), updateTableRecordFieldsByFilter.getErrorMessageVariables());        
    }

    static InternalMessage batchRemoveSampleUnstructured(String batchName, Integer sampleId) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.UNSTRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{batchName});
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        Integer samplePosic = batchSamples.indexOf(sampleId.toString());
        if (samplePosic == -1) {
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.SAMPLE_NOTFOUND_IN_BATCH, new Object[]{sampleId, batchName, procInstanceName});
        }
        String samplePosicInfo = batchSamples.substring(samplePosic, samplePosic + sampleId.toString().length() + 2);
        String[] samplePosicInfoArr = samplePosicInfo.split("\\*");
        if (samplePosicInfoArr.length != 2) {
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.PARSE_ERROR_STRUCTUREDBATCH, new Object[]{"Removing Sample", samplePosicInfo, batchSamples, procInstanceName});
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
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsEnvMonitData.IncubBatch.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{batchName}, "");
	RdbmsObject updateBatchSamples=Rdbms.updateTableRecordFieldsByFilter(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH,
		EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH, updFieldName), updFieldValue, sqlWhere, null);
        if (Boolean.FALSE.equals(updateBatchSamples.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateBatchSamples.getErrorMessageCode(), updateBatchSamples.getErrorMessageVariables());
        }
        if (Boolean.TRUE.equals(updateBatchSamples.getRunSuccess())) {
            IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.DataBatchAuditEvents.BATCH_SAMPLE_REMOVED.toString(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        }
        String batchFldName = "";
        if (incubStage == 1) {
            batchFldName = TblsEnvMonitData.Sample.INCUBATION_BATCH.getName();
        } else if (incubStage == 2) {
            batchFldName = TblsEnvMonitData.Sample.INCUBATION2_BATCH.getName();
        } else {
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.STAGE_NOT_RECOGNIZED, new Object[]{incubStage, procInstanceName});
        }
	sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsEnvMonitData.Sample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
        RdbmsObject updateRecordFieldsByFilter = Rdbms.updateTableRecordFieldsByFilter(TblsEnvMonitData.TablesEnvMonitData.SAMPLE,
                EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.SAMPLE, new String[]{batchFldName}), new Object[]{batchName}, sqlWhere, null);
        return new InternalMessage(updateRecordFieldsByFilter.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, updateRecordFieldsByFilter.getErrorMessageCode(), updateRecordFieldsByFilter.getErrorMessageVariables());        
    }
    static InternalMessage batchSampleIncubStartedUnstructured(String batchName, String incubName) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.UNSTRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{batchName});
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        if (batchSamples.length() == 0) {
            return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.EMPTY_BATCH, new Object[]{batchName, procInstanceName});
        }
        String[] batchSamplesArr = batchSamples.split("\\|");
        for (String currSample : batchSamplesArr) {
            String[] currSampleArr = currSample.split("\\*");
            if (currSampleArr.length != 2) {
                return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.PARSE_ERROR_STRUCTUREDBATCH, new Object[]{"Starting Batch", currSample, batchSamples, procInstanceName});
            }
            Integer sampleId = Integer.valueOf(currSampleArr[0]);
            Integer incubStage = Integer.valueOf(currSampleArr[1]);
            BigDecimal tempReading = null;
            InternalMessage setSampleIncubStarted = DataSampleIncubation.setSampleStartIncubationDateTime(sampleId, incubStage, incubName, tempReading, batchName);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(setSampleIncubStarted.getDiagnostic())) {
                return setSampleIncubStarted;
            }else{
                //setSampleIncubStarted=(Object[]) setSampleIncubStarted[0];
                DataSampleStages smpStage=new DataSampleStages();
                if (Boolean.TRUE.equals(smpStage.isSampleStagesEnable()) && (sampleId!=null))
                    smpStage.dataSampleActionAutoMoveToNext(EnvMonAPI.EnvMonAPIactionsEndpoints.EM_BATCH_INCUB_START.getName(), sampleId);
            }
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, IncubatorBatchSuccess.SAMPLES_IN_BATCH_SET_AS_BATCHSTARTED, new Object[]{batchName});
    }
    
    static InternalMessage batchSampleIncubEndedUnstructured(String batchName, String incubName) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.UNSTRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), new String[]{TblsEnvMonitData.IncubBatch.NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{batchName});
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        String[] batchSamplesArr = batchSamples.split("\\|");
        for (String currSample : batchSamplesArr) {
            String[] currSampleArr = currSample.split("\\*");
            if (currSampleArr.length != 2) {
                return new InternalMessage(LPPlatform.LAB_FALSE, IncubatorBatchErrorTrapping.PARSE_ERROR_STRUCTUREDBATCH, new Object[]{"Ending Batch", currSample, batchSamples, procInstanceName});
            }
            Integer sampleId = Integer.valueOf(currSampleArr[0]);
            Integer incubStage = Integer.valueOf(currSampleArr[1]);
            BigDecimal tempReading = null;
            InternalMessage setSampleIncubEnded = DataSampleIncubation.setSampleEndIncubationDateTime(sampleId, incubStage, incubName, tempReading, batchName);
            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(setSampleIncubEnded.getDiagnostic())) {
                return setSampleIncubEnded;
            }else{
                DataSampleStages smpStage=new DataSampleStages();
                if (Boolean.TRUE.equals(smpStage.isSampleStagesEnable()) && (sampleId!=null))
                    smpStage.dataSampleActionAutoMoveToNext(EnvMonAPI.EnvMonAPIactionsEndpoints.EM_BATCH_INCUB_END.getName(), sampleId);
            }
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, IncubatorBatchSuccess.SAMPLES_IN_BATCH_SET_AS_BATCHENDED, new Object[]{batchName});
    }

    
    static InternalMessage createBatchUnstructured(String bName, Integer bTemplateId, Integer bTemplateVersion, String[] fldName, Object[] fldValue) {        
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
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH, fldName, fldValue);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess()))
            IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.DataBatchAuditEvents.BATCH_CREATED.toString(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), bName, LPArray.joinTwo1DArraysInOneOf1DString(fldName, fldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);        
        return new InternalMessage(insertRecordInTable.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());
    }

    
}
