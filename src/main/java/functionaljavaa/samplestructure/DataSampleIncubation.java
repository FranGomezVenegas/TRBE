/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitProcedure;
import databases.Rdbms;
import databases.TblsData;
import databases.Token;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.instruments.incubator.DataIncubatorNoteBook;
import functionaljavaa.parameter.Parameter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author User
 */
public class DataSampleIncubation {

    /**
     *
     * @param schemaPrefix
     * @param userName
     * @param sampleId
     * @param userRole
     * @param appSessionId
     * @return
     */
    enum SampleIncubationObjects{
        SAMPLE, BATCH}
    enum SampleIncubationLevel{
        DATE, INCUBATOR}
    enum SampleIncubationMoment{ START, END;
        private static final Set<String> _values = new HashSet<>();
        // O(n) - runs once
        static{
            for (SampleIncubationMoment choice : SampleIncubationMoment.values()) {
                _values.add(choice.name());
            }
        }        
        public static boolean contains(String value){
            return _values.contains(value);        
    }
    }
    enum SampleIncubationModes{
        SAMPLE_AND_DATE,SAMPLE_AND_INCUBATOR,;
        private static final Set<String> _values = new HashSet<>();
        // O(n) - runs once
        static{
            for (SampleIncubationModes choice : SampleIncubationModes.values()) {
                _values.add(choice.name());
            }
        }        
        public static boolean contains(String value){
            return _values.contains(value);        
        }
    }   
    enum TempReadingBusinessRules{
        DISABLE,
        SAME_DAY,
        HOURS
    }
    enum TempReadingBusinessRulesLevel{
        DEVIATION,
        STOP,
        DEVIATION_AND_STOP
    }    

    /**
     *
     * @param schemaPrefix
     * @param token
     * @param sampleId
     * @param incubationStage
     * @param incubName
     * @param tempReading
     * @return
     */
    public static Object[] setSampleEndIncubationDateTime(String schemaPrefix, Token token, Integer sampleId, Integer incubationStage, String incubName, BigDecimal tempReading) {
        Object[] sampleIncubatorModeCheckerInfo=sampleIncubatorModeChecker(schemaPrefix, token, incubationStage, SampleIncubationMoment.END.toString(), incubName, tempReading);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleIncubatorModeCheckerInfo[0].toString())) return sampleIncubatorModeCheckerInfo;
        if ((incubationStage < 1) || (incubationStage > 2)) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "INCUBATION STAGE SHOULD BE SET TO 1 OR 2", null);
        }
        String[] sampleFieldName = (String[]) sampleIncubatorModeCheckerInfo[1];
        Object[] sampleFieldValue = (Object[]) sampleIncubatorModeCheckerInfo[2];
        
        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsData.Sample.TBL.getName(), sampleFieldName, sampleFieldValue, new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
            diagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "DataSample_SampleIncubationEndedSuccessfully", 
                    new Object[]{sampleId, LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ", "))});
            String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, token.getPersonName());
            SampleAudit smpAudit = new SampleAudit();
            smpAudit.sampleAuditAdd(schemaPrefix, SampleAudit.SampleAuditEvents.SAMPLE_SET_INCUBATION_ENDED.toString(), TblsData.Sample.TBL.getName(), sampleId, sampleId, null, null, fieldsForAudit, token, null);
        }
        return diagnoses;
    }

    /**
     *
     * @param schemaPrefix
     * @param token
     * @param sampleId
     * @param incubationStage
     * @param incubName
     * @param tempReading
     * @return
     */
    public static Object[] setSampleStartIncubationDateTime(String schemaPrefix, Token token, Integer sampleId, Integer incubationStage, String incubName, BigDecimal tempReading) {
        Object[] sampleIncubatorModeCheckerInfo=sampleIncubatorModeChecker(schemaPrefix, token, incubationStage, SampleIncubationMoment.START.toString(), incubName, tempReading);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleIncubatorModeCheckerInfo[0].toString())) return sampleIncubatorModeCheckerInfo;
        if ((incubationStage < 1) || (incubationStage > 2)) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "INCUBATION STAGE SHOULD BE SET TO 1 OR 2", null);
        }
        String[] sampleFieldName = (String[]) sampleIncubatorModeCheckerInfo[1];
        Object[] sampleFieldValue = (Object[]) sampleIncubatorModeCheckerInfo[2];

        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsData.Sample.TBL.getName(), sampleFieldName, sampleFieldValue, new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {            
            diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "DataSample_SampleIncubationStartedSuccessfully", 
                    new Object[]{sampleId, LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ", "))});
            String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, token.getPersonName());
            SampleAudit smpAudit = new SampleAudit();
            smpAudit.sampleAuditAdd(schemaPrefix, SampleAudit.SampleAuditEvents.SAMPLE_SET_INCUBATION_STARTED.toString(), TblsData.Sample.TBL.getName(), sampleId, sampleId, null, null, fieldsForAudit, token, null);
        }
        return diagnoses;
    }
    
    private static Object[] sampleIncubatorModeChecker(String schemaPrefix, Token token, Integer incubationStage, String moment, String incubName, BigDecimal tempReading){        
        String sampleIncubationMode = Parameter.getParameterBundle("config", schemaPrefix, "procedure", "sampleIncubationMode", null);
        if (sampleIncubationMode.length()==0) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleIncubatorMode Business Rule not defined for "+schemaPrefix, null);
        if (!SampleIncubationModes.contains(sampleIncubationMode)) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleIncubatorMode <*1*> not recognized as one of the expected values", new Object[]{sampleIncubationMode});        
        
        String[] requiredFields=new String[0];
        Object[] requiredFieldsValue=new Object[0];
        
        if (sampleIncubationMode.contains(SampleIncubationObjects.SAMPLE.toString())){}
        else if (sampleIncubationMode.contains(SampleIncubationObjects.BATCH.toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleIncubatorModeChecker NOT IMPLEMENTED YET", null);
        else
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleIncubatorModeChecker Object <*1*> NOT RECOGNIZED", new Object[]{sampleIncubationMode});
        
        if (!SampleIncubationMoment.contains(moment))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleIncubatorModeChecker Moment <*1*> NOT RECOGNIZED", new Object[]{moment});        
        if (sampleIncubationMode.contains(SampleIncubationLevel.DATE.toString())){
            if (incubationStage == 2) {
                if (moment.contains(SampleIncubationMoment.START.toString())){
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.FLD_INCUBATION2_START.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp()});                
                }else{
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.FLD_INCUBATION2_END.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp()});                                
                }
            }else{
                if (moment.contains(SampleIncubationMoment.START.toString())){
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.FLD_INCUBATION_START.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp()});                
                }else{
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.FLD_INCUBATION_END.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp()});                                
                }                
            }
        }else if (sampleIncubationMode.contains(SampleIncubationLevel.INCUBATOR.toString())){
            if (incubName==null) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleIncubatorModeChecker Incubator should be specified", null);
            Object[] incubInfo=Rdbms.existsRecord(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                    new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{incubName});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubInfo[0].toString()))
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleIncubatorModeChecker Incubator <*1*> not found for schema <*2*>", new Object[]{incubName, schemaPrefix});
            Integer tempReadingEvId=null;
            if (tempReading==null){
                Object[][] incubLastTempReading=DataIncubatorNoteBook.getLastTemperatureReading(schemaPrefix, incubName, 1);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubLastTempReading[0][0].toString())) return LPArray.array2dTo1d(incubLastTempReading);
                tempReadingEvId= Integer.valueOf(incubLastTempReading[0][0].toString());
                tempReading= BigDecimal.valueOf(Double.valueOf(incubLastTempReading[0][4].toString()));                
                Object[] tempReadingChecker=tempReadingBusinessRule(schemaPrefix, token, incubName, incubLastTempReading[0][2]);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(tempReadingChecker[0].toString())) return tempReadingChecker;
            }
            if (incubationStage == 2) {
                if (moment.contains(SampleIncubationMoment.START.toString())){
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.FLD_INCUBATION2_START.getName(), 
                        TblsData.Sample.FLD_INCUBATION2_INCUBATOR.getName(), TblsData.Sample.FLD_INCUBATION2_START_TEMP_EVENT_ID.getName(), TblsData.Sample.FLD_INCUBATION2_START_TEMPERATURE.getName(), TblsData.Sample.FLD_INCUBATION2_PASSED.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp(), incubName, tempReadingEvId, tempReading, false});
                }else if (moment.contains(SampleIncubationMoment.END.toString())){
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.FLD_INCUBATION2_END.getName(), 
                        TblsData.Sample.FLD_INCUBATION2_INCUBATOR.getName(), TblsData.Sample.FLD_INCUBATION2_END_TEMP_EVENT_ID.getName(), TblsData.Sample.FLD_INCUBATION2_END_TEMPERATURE.getName(), TblsData.Sample.FLD_INCUBATION2_PASSED.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp(), incubName, tempReadingEvId, tempReading, true});
                    }
            } else {
                if (moment.contains(SampleIncubationMoment.START.toString())){
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.FLD_INCUBATION_START.getName(), 
                        TblsData.Sample.FLD_INCUBATION_INCUBATOR.getName(), TblsData.Sample.FLD_INCUBATION_START_TEMP_EVENT_ID.getName(), TblsData.Sample.FLD_INCUBATION_START_TEMPERATURE.getName(), TblsData.Sample.FLD_INCUBATION_PASSED.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp(), incubName, tempReadingEvId, tempReading, false});
                }else if (moment.contains(SampleIncubationMoment.END.toString())){
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.FLD_INCUBATION_END.getName(), 
                        TblsData.Sample.FLD_INCUBATION_INCUBATOR.getName(), TblsData.Sample.FLD_INCUBATION_END_TEMP_EVENT_ID.getName(), TblsData.Sample.FLD_INCUBATION_END_TEMPERATURE.getName(), TblsData.Sample.FLD_INCUBATION_PASSED.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp(), incubName, tempReadingEvId, tempReading, true});                
                }
            }
        }else
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleIncubatorModeChecker Object <*1*> NOT RECOGNIZED", new Object[]{sampleIncubationMode});
                
        return new Object[]{LPPlatform.LAB_TRUE, requiredFields, requiredFieldsValue};
    }
    
    private static Object[] tempReadingBusinessRule(String schemaPrefix, Token token, String incubName, Object tempReadingDate){   
        String sampleIncubationTempReadingBusinessRulevalue = Parameter.getParameterBundle("config", schemaPrefix, "procedure", "sampleIncubationTempReadingBusinessRule", null);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime tempReadingDateDateTime = LocalDateTime.parse(tempReadingDate.toString().substring(0, 19), formatter);
        if (sampleIncubationTempReadingBusinessRulevalue.length()==0)
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleIncubationTempReadingBusinessRule procedure property not found for procedure <*1*>.", new Object[]{schemaPrefix} );
        if (TempReadingBusinessRules.DISABLE.toString().equalsIgnoreCase(sampleIncubationTempReadingBusinessRulevalue))
                return new Object[]{LPPlatform.LAB_TRUE};
        String[] sampleIncubationTempReadingBusinessRulevalueArr=sampleIncubationTempReadingBusinessRulevalue.split("\\|");
        Integer stoppables=0;
        Object[] stoppablesDiagn = new Object[0];        
        Integer deviations=0;
        Object[] deviationsDiagn = new Object[0];        
        Integer deviationAndStop=0;
        Object[] deviationAndStopDiagn = new Object[0];        
        Boolean finalDiagn=true;
        for (String currSampleIncubationTempReadingBusinessRulevalue: sampleIncubationTempReadingBusinessRulevalueArr){
            Boolean currDiagn=false;
            Object[] currDiagnoses = new Object[0];
            String[] currSampleIncubationTempReadingBusinessRulevalueArr=currSampleIncubationTempReadingBusinessRulevalue.split("\\*");
            if (TempReadingBusinessRules.SAME_DAY.toString().equalsIgnoreCase(currSampleIncubationTempReadingBusinessRulevalueArr[0])){                
                currDiagn = tempReadingDateDateTime.getDayOfYear()==LPDate.getCurrentTimeStamp().getDayOfYear();
                    currDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The temperature reading day is <*1*> and is not from today as set for procedure <*2*>", new Object[]{tempReadingDate.toString(), schemaPrefix} );                
            }else if (TempReadingBusinessRules.HOURS.toString().equalsIgnoreCase(currSampleIncubationTempReadingBusinessRulevalueArr[0])){
                long hours = ChronoUnit.HOURS.between(tempReadingDateDateTime, LPDate.getCurrentTimeStamp());
                if (hours>Long.valueOf(currSampleIncubationTempReadingBusinessRulevalueArr[1])){
                    currDiagn=false;
                    currDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The temperature reading is <*1*> and there are <*2*> hours what is greater than the set for procedure <*3*>", new Object[]{tempReadingDate.toString(), hours, schemaPrefix} );                                    
                }else{
                    currDiagn=true;
                }
            }else
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleIncubationTempReadingBusinessRule procedure property value is <*1*> and is not a recognized value for procedure <*2*>", new Object[]{sampleIncubationTempReadingBusinessRulevalue, schemaPrefix} );
            if (!currDiagn){
                String curLevel=currSampleIncubationTempReadingBusinessRulevalueArr[currSampleIncubationTempReadingBusinessRulevalueArr.length-1];
                Boolean currLevelExists=false;
                for (TempReadingBusinessRulesLevel currBusRuleLvl: TempReadingBusinessRulesLevel.values()){
                    if (curLevel.equalsIgnoreCase(currBusRuleLvl.toString())) currLevelExists=true;
                }
                if (!currLevelExists) curLevel=TempReadingBusinessRulesLevel.STOP.toString();
                if (TempReadingBusinessRulesLevel.STOP.toString().equalsIgnoreCase(curLevel)){
                    stoppables++;
                    stoppablesDiagn=currDiagnoses;
                }
                if (TempReadingBusinessRulesLevel.DEVIATION.toString().equalsIgnoreCase(curLevel)){
                    deviations++;
                    deviationsDiagn=currDiagnoses;
                }
                if (TempReadingBusinessRulesLevel.DEVIATION_AND_STOP.toString().equalsIgnoreCase(curLevel)){
                    deviationAndStop++;
                    deviationAndStopDiagn=currDiagnoses;
                }
                finalDiagn=false;                
            }
        }
        if (finalDiagn) return new Object[]{LPPlatform.LAB_TRUE};  

        if (deviationAndStop>0){
            Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_PROCEDURE), TblsEnvMonitProcedure.IncubatorTempReadingViolations.TBL.getName(), 
                    new String[]{TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_CREATED_ON.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_CREATED_BY.getName(), 
                        TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_STARTED_ON.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_REASON.getName(), 
                        TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_INCUBATOR.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_STAGE_CURRENT.getName()}, 
                    new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName(), LPDate.getCurrentTimeStamp(), deviationAndStopDiagn[deviationAndStopDiagn.length-1],
                        incubName, "CREATED"});
            return deviationAndStopDiagn;            
        }
    
        if (stoppables>0) return stoppablesDiagn;
        
        if (deviations>0){
            Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_PROCEDURE), TblsEnvMonitProcedure.IncubatorTempReadingViolations.TBL.getName(), 
                    new String[]{TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_CREATED_ON.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_CREATED_BY.getName(), 
                        TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_STARTED_ON.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_REASON.getName(), 
                        TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_INCUBATOR.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_STAGE_CURRENT.getName()}, 
                    new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName(), LPDate.getCurrentTimeStamp(), deviationsDiagn[deviationsDiagn.length-1],
                        incubName, "CREATED"});
            deviationsDiagn[0]=LPPlatform.LAB_TRUE;
            return deviationsDiagn;
        }
        
        return new Object[]{LPPlatform.LAB_FALSE}; 
    }
    
}
    
