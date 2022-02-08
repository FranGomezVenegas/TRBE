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
import trazit.globalvariables.GlobalVariables;
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
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "ruleDisabled", null);
        Object[][] sampleStageTimingCaptureInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.SampleStageTimingCapture.TBL.getName(), 
            new String[]{TblsProcedure.SampleStageTimingCapture.FLD_SAMPLE_ID.getName(), TblsProcedure.SampleStageTimingCapture.FLD_STAGE_CURRENT.getName()},    
            new Object[]{sampleId, stage},
            new String[]{TblsProcedure.SampleStageTimingCapture.FLD_STARTED_ON.getName(), TblsProcedure.SampleStageTimingCapture.FLD_ENDED_ON.getName()},
            new String[]{TblsProcedure.SampleStageTimingCapture.FLD_ID.getName()});
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
        Object[][] sampleStageProcInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE_CONFIG.getName()), TblsProcedureConfig.StageTimingInterval.TBL.getName(), 
                new String[]{TblsProcedureConfig.StageTimingInterval.FLD_SAMPLE_CONFIG_CODE.getName(), TblsProcedureConfig.StageTimingInterval.FLD_SAMPLE_CONFIG_VERSION.getName(), TblsProcedureConfig.StageTimingInterval.FLD_STAGE.getName()},
                new Object[]{configCode, configVersion, stage}, 
                TblsProcedureConfig.StageTimingInterval.getAllFieldNames());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleStageProcInfo[0][0].toString()))
            return sampleStageProcInfo;
        String isStageMarkedAsEnabled=LPNulls.replaceNull(sampleStageProcInfo[0][LPArray.valuePosicInArray(TblsProcedureConfig.StageTimingInterval.getAllFieldNames(), TblsProcedureConfig.StageTimingInterval.FLD_ENABLED.getName())]).toString();
        if (!Boolean.valueOf(isStageMarkedAsEnabled)) 
           return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "stageMarkedAsDisabled", null);
        String intvValue=LPNulls.replaceNull(sampleStageProcInfo[0][LPArray.valuePosicInArray(TblsProcedureConfig.StageTimingInterval.getAllFieldNames(), TblsProcedureConfig.StageTimingInterval.FLD_INTERVAL_SECONDS.getName())]).toString();
        stageProcedureDeviationShouldBeCreated(sampleId, stageStartDate, LocalDateTime.now(), configCode, configVersion, stage, Integer.valueOf(intvValue));
        return new Object[]{LPPlatform.LAB_TRUE, Integer.valueOf(intvValue)};
    }
    private static void stageProcedureDeviationShouldBeCreated(Integer sampleId, LocalDateTime stageStartDate, LocalDateTime stageEndDate, String configCode, Integer configVersion, String stage, Integer interval){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] isTheIntervalIntoTheDatesRange = IntervalsUtilities.isTheIntervalIntoTheDatesRange(interval, stageStartDate, stageEndDate);        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isTheIntervalIntoTheDatesRange[0].toString())){
            String[] insFldNames=new String[]{TblsProcedure.SampleStageTimingIntervalDeviation.FLD_SAMPLE_ID.getName(), TblsProcedure.SampleStageTimingIntervalDeviation.FLD_STAGE.getName(),
                TblsProcedure.SampleStageTimingIntervalDeviation.FLD_STARTED_ON.getName(), TblsProcedure.SampleStageTimingIntervalDeviation.FLD_ENDED_ON.getName(),
                TblsProcedure.SampleStageTimingIntervalDeviation.FLD_SAMPLE_CONFIG_CODE.getName(), TblsProcedure.SampleStageTimingIntervalDeviation.FLD_SAMPLE_CONFIG_VERSION.getName(),
                TblsProcedure.SampleStageTimingIntervalDeviation.FLD_EXPECTED_INTERVAL_SECONDS.getName()};
            Object[] insFldValues=new Object[]{sampleId, stage, stageStartDate, stageEndDate, configCode, configVersion, interval};
            Object datesDiffSeconds = isTheIntervalIntoTheDatesRange[isTheIntervalIntoTheDatesRange.length-1];
            if (datesDiffSeconds!=null){
               insFldNames=LPArray.addValueToArray1D(insFldNames, TblsProcedure.SampleStageTimingIntervalDeviation.FLD_DATERANGE_INTERVAL_SECONDS.getName());
               insFldValues=LPArray.addValueToArray1D(insFldValues, datesDiffSeconds);
            }
            Object[] insertRecordInTable = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.SampleStageTimingIntervalDeviation.TBL.getName(), 
                    insFldNames, insFldValues);            
            return;            
        }
        return;
    }
}
