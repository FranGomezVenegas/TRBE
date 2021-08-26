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
import org.json.simple.JSONArray;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class DataSampleIncubation {

    public enum DataSampleIncubationBusinessRules{
        SAMPLE_INCUBATION_MODE("sampleIncubationMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        SAMPLE_INCUB_TEMP_READING_BUSRULE("sampleIncubationTempReadingBusinessRule", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|')
        
        ;
        private DataSampleIncubationBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
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
     *
     * @param procInstanceName
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
        ;
    }    

    /**
     *
     * @param sampleId
     * @param incubationStage
     * @param incubName
     * @param tempReading
     * @return
     */
    public static Object[] setSampleEndIncubationDateTime(Integer sampleId, Integer incubationStage, String incubName, BigDecimal tempReading) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[] sampleIncubatorModeCheckerInfo=sampleIncubatorModeChecker(incubationStage, SampleIncubationMoment.END.toString(), incubName, tempReading);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleIncubatorModeCheckerInfo[0].toString())) return sampleIncubatorModeCheckerInfo;
        if ((incubationStage < 1) || (incubationStage > 2)) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUBATION_STAGE_NOTRECOGNIZED.getErrorCode(), null);
        }
        String[] sampleFieldName = (String[]) sampleIncubatorModeCheckerInfo[1];
        Object[] sampleFieldValue = (Object[]) sampleIncubatorModeCheckerInfo[2];
        
        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), sampleFieldName, sampleFieldValue, new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
            diagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, DataSampleIncubationErrorTrapping.SAMPLEINCUBATION_ENDED_SUCCESS.getErrorCode(), 
                    new Object[]{sampleId, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ", "))});
            String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, token.getPersonName());
            SampleAudit smpAudit = new SampleAudit();
            smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_SET_INCUBATION_ENDED.toString(), TblsData.Sample.TBL.getName(), sampleId, sampleId, null, null, fieldsForAudit, null);
        }
        return diagnoses;
    }

    /**
     *
     * @param sampleId
     * @param incubationStage
     * @param incubName
     * @param tempReading
     * @return
     */
    public static Object[] setSampleStartIncubationDateTime(Integer sampleId, Integer incubationStage, String incubName, BigDecimal tempReading) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] sampleIncubatorModeCheckerInfo=sampleIncubatorModeChecker(incubationStage, SampleIncubationMoment.START.toString(), incubName, tempReading);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleIncubatorModeCheckerInfo[0].toString())) return sampleIncubatorModeCheckerInfo;
        if ((incubationStage < 1) || (incubationStage > 2)) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUBATION_STAGE_NOTRECOGNIZED.getErrorCode(), null);
        }
        String[] sampleFieldName = (String[]) sampleIncubatorModeCheckerInfo[1];
        Object[] sampleFieldValue = (Object[]) sampleIncubatorModeCheckerInfo[2];

        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), sampleFieldName, sampleFieldValue, new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {            
            diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, DataSampleIncubationErrorTrapping.SAMPLEINCUBATION_STARTED_SUCCESS.getErrorCode(), 
                    new Object[]{sampleId, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ", "))});
            String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, token.getPersonName());
            SampleAudit smpAudit = new SampleAudit();
            smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_SET_INCUBATION_STARTED.toString(), TblsData.Sample.TBL.getName(), sampleId, sampleId, null, null, fieldsForAudit, null);
        }
        return diagnoses;
    }
    
    private static Object[] sampleIncubatorModeChecker(Integer incubationStage, String moment, String incubName, BigDecimal tempReading){        
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String sampleIncubationMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleIncubationBusinessRules.SAMPLE_INCUBATION_MODE.getAreaName(), DataSampleIncubationBusinessRules.SAMPLE_INCUBATION_MODE.getTagName());
        if (sampleIncubationMode.length()==0) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "SampleIncubatorModeBusinessRuleNotDefined", new Object[]{procInstanceName});
        if (!SampleIncubationModes.contains(sampleIncubationMode)) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "SampleIncubatorModeValueNotRrecognized", new Object[]{sampleIncubationMode});        
        
        String[] requiredFields=new String[0];
        Object[] requiredFieldsValue=new Object[0];
        
        if (sampleIncubationMode.contains(SampleIncubationObjects.SAMPLE.toString())){}
        else if (sampleIncubationMode.contains(SampleIncubationObjects.BATCH.toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.NOT_IMPLEMENTED.getErrorCode(), null);
        else
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUBATION_OBJECT_NOTRECOGNIZED.getErrorCode(), new Object[]{sampleIncubationMode});
        
        if (!SampleIncubationMoment.contains(moment))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUBATION_STAGE_NOTRECOGNIZED.getErrorCode(), new Object[]{moment});        
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
            if (incubName==null) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUBATOR_NOT_ASSIGNED.getErrorCode(), null);
            Object[] incubInfo=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                    new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{incubName});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubInfo[0].toString()))
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUBATOR_NOT_ASSIGNED.getErrorCode(), new Object[]{incubName, procInstanceName});
            Integer tempReadingEvId=null;
            if (tempReading==null){
                Object[][] incubLastTempReading=DataIncubatorNoteBook.getLastTemperatureReading(incubName, 1);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubLastTempReading[0][0].toString())) return LPArray.array2dTo1d(incubLastTempReading);
                tempReadingEvId= Integer.valueOf(incubLastTempReading[0][0].toString());
                tempReading= BigDecimal.valueOf(Double.valueOf(incubLastTempReading[0][4].toString()));                
                Object[] tempReadingChecker=tempReadingBusinessRule(incubName, incubLastTempReading[0][2]);
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
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUBATION_OBJECT_NOTRECOGNIZED.getErrorCode(), new Object[]{sampleIncubationMode});
                
        return new Object[]{LPPlatform.LAB_TRUE, requiredFields, requiredFieldsValue};
    }
    
    private static Object[] tempReadingBusinessRule(String incubName, Object tempReadingDate){   
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String sampleIncubationTempReadingBusinessRulevalue = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleIncubationBusinessRules.SAMPLE_INCUB_TEMP_READING_BUSRULE.getAreaName(), DataSampleIncubationBusinessRules.SAMPLE_INCUB_TEMP_READING_BUSRULE.getTagName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime tempReadingDateDateTime = LocalDateTime.parse(tempReadingDate.toString().substring(0, 19), formatter);
        if (sampleIncubationTempReadingBusinessRulevalue.length()==0)
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleIncubationTempReadingBusinessRule procedure property not found for procedure <*1*>.", new Object[]{procInstanceName} );
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
                    currDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.TEMPERATUREREADYDAY_ISNOTTODAY.getErrorCode(), new Object[]{tempReadingDate.toString(), procInstanceName} );                
            }else if (TempReadingBusinessRules.HOURS.toString().equalsIgnoreCase(currSampleIncubationTempReadingBusinessRulevalueArr[0])){
                long hours = ChronoUnit.HOURS.between(tempReadingDateDateTime, LPDate.getCurrentTimeStamp());
                if (hours>Long.valueOf(currSampleIncubationTempReadingBusinessRulevalueArr[1])){
                    currDiagn=false;
                    currDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUB_TEMP_READING_INTVL_EXPIRED.getErrorCode(), new Object[]{tempReadingDate.toString(), hours, procInstanceName} );                                    
                }else{
                    currDiagn=true;
                }
            }else
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "sampleIncubationTempReadingBusinessRule procedure property value is <*1*> and is not a recognized value for procedure <*2*>", new Object[]{sampleIncubationTempReadingBusinessRulevalue, procInstanceName} );
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
            Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsEnvMonitProcedure.IncubatorTempReadingViolations.TBL.getName(), 
                    new String[]{TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_CREATED_ON.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_CREATED_BY.getName(), 
                        TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_STARTED_ON.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_REASON.getName(), 
                        TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_INCUBATOR.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.FLD_STAGE_CURRENT.getName()}, 
                    new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName(), LPDate.getCurrentTimeStamp(), deviationAndStopDiagn[deviationAndStopDiagn.length-1],
                        incubName, "CREATED"});
            return deviationAndStopDiagn;            
        }
    
        if (stoppables>0) return stoppablesDiagn;
        
        if (deviations>0){
            Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsEnvMonitProcedure.IncubatorTempReadingViolations.TBL.getName(), 
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
    
    public enum DataSampleIncubationErrorTrapping{ 
        INCUBATORBATCH_NOT_STARTED("IncubatorBatchNotStartedYet", "The batch <*1*> was not started yet for procedure <*2*>", "La tanda <*1*> no está iniciada todavía para el proceso <*2*>"),
        SAMPLEINCUBATION_STARTED_SUCCESS("SampleIncubationStartedSuccessfully", "", ""),
        SAMPLEINCUBATION_ENDED_SUCCESS("SampleIncubationEndedSuccessfully", "", ""),
        TEMPERATUREREADYDAY_ISNOTTODAY("TemperatureReadingDayIsNotToday", "", ""),
        INCUBATION_STAGE_NOTRECOGNIZED("incubationStageNotRecognized", "", ""),
        INCUBATION_OBJECT_NOTRECOGNIZED("incubationObjectNotRecognized", "", ""),
        NOT_IMPLEMENTED("incubationModuleCheckerLogicNotImplementedYet", "", ""),
        INCUB_TEMP_READING_INTVL_EXPIRED("incubatorTempReadingIntervalExpired", "", ""),
        INCUBATOR_NOT_ASSIGNED("incubatorNotAssignedToBatch", "", "")
        ;
        private DataSampleIncubationErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
    
}
    
