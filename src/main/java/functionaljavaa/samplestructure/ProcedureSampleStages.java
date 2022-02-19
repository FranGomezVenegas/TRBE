/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import databases.Rdbms;
import databases.TblsData;
import databases.TblsProcedure;
import databases.TblsProcedureConfig;
import functionaljavaa.intervals.IntervalsUtilities;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.samplestructure.DataSampleStages.SampleStageBusinessRules;
import java.time.LocalDateTime;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ProcedureSampleStages {
    public static Object[] procedureSampleStagesTimingEvaluateDeviation(Integer sampleId, String stage){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String tagValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, 
                SampleStageBusinessRules.SAMPLE_STAGE_TIMING_PROCEDURE_CONFIG_ENABLED.getAreaName(), 
                SampleStageBusinessRules.SAMPLE_STAGE_TIMING_PROCEDURE_CONFIG_ENABLED.getTagName());        
        Boolean businessRuleIsEnable = Parameter.isTagValueOneOfEnableOnes(tagValue);
        if (!businessRuleIsEnable)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "ruleDisabled", null);
        Object[][] sampleStageTimingCaptureInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.SAMPLE_STAGE_TIMING_CAPTURE.getTableName(), 
            new String[]{TblsProcedure.SampleStageTimingCapture.SAMPLE_ID.getName(), TblsProcedure.SampleStageTimingCapture.STAGE_CURRENT.getName()},    
            new Object[]{sampleId, stage},
            new String[]{TblsProcedure.SampleStageTimingCapture.STARTED_ON.getName(), TblsProcedure.SampleStageTimingCapture.ENDED_ON.getName()},
            new String[]{TblsProcedure.SampleStageTimingCapture.ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleStageTimingCaptureInfo[0][0].toString()))
            return sampleStageTimingCaptureInfo;
        String stageStartDateStr=sampleStageTimingCaptureInfo[sampleStageTimingCaptureInfo.length-1][0].toString();        
        LocalDateTime stageStartDate=LPDate.stringFormatToLocalDateTime(stageStartDateStr);
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
            new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()},    
            new Object[]{sampleId},
            new String[]{TblsData.Sample.FLD_CONFIG_CODE.getName(), TblsData.Sample.FLD_CONFIG_CODE_VERSION.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
            return sampleInfo;
        String configCode=sampleInfo[0][0].toString();
        Integer configVersion=(Integer)sampleInfo[0][1];
        Object[][] sampleStageProcInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE_CONFIG.getName()), TblsProcedureConfig.TablesProcedureConfig.STAGE_TIMING_INTERVAL.getTableName(), 
                new String[]{TblsProcedureConfig.StageTimingInterval.SAMPLE_CONFIG_CODE.getName(), TblsProcedureConfig.StageTimingInterval.SAMPLE_CONFIG_VERSION.getName(), TblsProcedureConfig.StageTimingInterval.STAGE.getName()},
                new Object[]{configCode, configVersion, stage}, 
                getAllFieldNames(TblsProcedureConfig.TablesProcedureConfig.STAGE_TIMING_INTERVAL.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleStageProcInfo[0][0].toString()))
            return sampleStageProcInfo;
        String isStageMarkedAsEnabled=LPNulls.replaceNull(sampleStageProcInfo[0][LPArray.valuePosicInArray(getAllFieldNames(TblsProcedureConfig.TablesProcedureConfig.STAGE_TIMING_INTERVAL.getTableFields()), TblsProcedureConfig.StageTimingInterval.ENABLED.getName())]).toString();
        if (!Boolean.valueOf(isStageMarkedAsEnabled)) 
           return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "stageMarkedAsDisabled", null);
        String intvValue=LPNulls.replaceNull(sampleStageProcInfo[0][LPArray.valuePosicInArray(getAllFieldNames(TblsProcedureConfig.TablesProcedureConfig.STAGE_TIMING_INTERVAL.getTableFields()), TblsProcedureConfig.StageTimingInterval.INTERVAL_SECONDS.getName())]).toString();
        stageProcedureDeviationShouldBeCreated(sampleId, stageStartDate, LocalDateTime.now(), configCode, configVersion, stage, Integer.valueOf(intvValue));
        return new Object[]{LPPlatform.LAB_TRUE, Integer.valueOf(intvValue)};
    }
    private static void stageProcedureDeviationShouldBeCreated(Integer sampleId, LocalDateTime stageStartDate, LocalDateTime stageEndDate, String configCode, Integer configVersion, String stage, Integer interval){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] isTheIntervalIntoTheDatesRange = IntervalsUtilities.isTheIntervalIntoTheDatesRange(interval, stageStartDate, stageEndDate);        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isTheIntervalIntoTheDatesRange[0].toString())){
            String[] insFldNames=new String[]{TblsProcedure.SampleStageTimingIntervalDeviation.SAMPLE_ID.getName(), TblsProcedure.SampleStageTimingIntervalDeviation.STAGE.getName(),
                TblsProcedure.SampleStageTimingIntervalDeviation.STARTED_ON.getName(), TblsProcedure.SampleStageTimingIntervalDeviation.ENDED_ON.getName(),
                TblsProcedure.SampleStageTimingIntervalDeviation.SAMPLE_CONFIG_CODE.getName(), TblsProcedure.SampleStageTimingIntervalDeviation.SAMPLE_CONFIG_VERSION.getName(),
                TblsProcedure.SampleStageTimingIntervalDeviation.EXPECTED_INTERVAL_SECONDS.getName()};
            Object[] insFldValues=new Object[]{sampleId, stage, stageStartDate, stageEndDate, configCode, configVersion, interval};
            Object datesDiffSeconds = isTheIntervalIntoTheDatesRange[isTheIntervalIntoTheDatesRange.length-1];
            if (datesDiffSeconds!=null){
               insFldNames=LPArray.addValueToArray1D(insFldNames, TblsProcedure.SampleStageTimingIntervalDeviation.DATERANGE_INTERVAL_SECONDS.getName());
               insFldValues=LPArray.addValueToArray1D(insFldValues, datesDiffSeconds);
            }
            Object[] insertRecordInTable = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.SAMPLE_STAGE_TIMING_INTERVAL_DEVIATION.getTableName(), 
                    insFldNames, insFldValues);            
            return;            
        }
        return;
    }
}
