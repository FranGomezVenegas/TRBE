/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import com.labplanet.servicios.moduleenvmonit.EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints;
import module.monitoring.definition.TblsEnvMonitConfig;
import module.monitoring.definition.TblsEnvMonitProcedure;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import databases.features.Token;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.instruments.incubator.DataIncubatorNoteBook;
import functionaljavaa.parameter.Parameter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author User
 */
public class DataSampleIncubation {

    public enum DataSampleIncubationBusinessRules  implements EnumIntBusinessRules{
        SAMPLE_INCUBATION_MODE("sampleIncubationMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        SAMPLE_INCUB_TEMP_READING_BUSRULE("sampleIncubationTempReadingBusinessRule", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null)
        
        ;
        private DataSampleIncubationBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator
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
        return setSampleEndIncubationDateTime(sampleId, incubationStage, incubName, tempReading, null);
    }
    public static Object[] setSampleEndIncubationDateTime(Integer sampleId, Integer incubationStage, String incubName, BigDecimal tempReading, String batchName) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[] sampleIncubatorModeCheckerInfo=sampleIncubatorModeChecker(incubationStage, SampleIncubationMoment.END.toString(), incubName, tempReading, batchName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleIncubatorModeCheckerInfo[0].toString())) return sampleIncubatorModeCheckerInfo;
        if ((incubationStage < 1) || (incubationStage > 2)) {
            return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUBATION_STAGE_NOTRECOGNIZED, null)};
        }
        String[] sampleFieldName = (String[]) sampleIncubatorModeCheckerInfo[1];
        Object[] sampleFieldValue = (Object[]) sampleIncubatorModeCheckerInfo[2];
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsData.Sample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
        Object[] diagnoses=Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE,
            EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE, sampleFieldName), sampleFieldValue, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
            diagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, EnvMonSampleAPIactionsEndpoints.SINGLE_SAMPLE_INCUB_END.getSuccessMessageCode(), 
                    new Object[]{sampleId, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ", "))});
            SampleAudit smpAudit = new SampleAudit();
            EnumIntAuditEvents sampleAuditEvName=null;
            if (incubationStage==1)
                sampleAuditEvName=SampleAudit.DataSampleAuditEvents.SAMPLE_SET_INCUBATION_1_ENDED;
            else
                sampleAuditEvName=SampleAudit.DataSampleAuditEvents.SAMPLE_SET_INCUBATION_2_ENDED;            
            Object[] sampleAuditAdd = smpAudit.sampleAuditAdd(sampleAuditEvName, TblsData.TablesData.SAMPLE.getTableName(), 
                    sampleId, sampleId, null, null, sampleFieldName, sampleFieldValue);
            return new Object[]{diagnoses, sampleAuditAdd};
        }
        return new Object[]{diagnoses};
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
        return setSampleStartIncubationDateTime(sampleId, incubationStage, incubName, tempReading, null);
    }
    public static Object[] setSampleStartIncubationDateTime(Integer sampleId, Integer incubationStage, String incubName, BigDecimal tempReading, String batchName) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] sampleIncubatorModeCheckerInfo=sampleIncubatorModeChecker(incubationStage, SampleIncubationMoment.START.toString(), incubName, tempReading, batchName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleIncubatorModeCheckerInfo[0].toString())) return sampleIncubatorModeCheckerInfo;
        if ((incubationStage < 1) || (incubationStage > 2)) {
            return new Object[]{ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUBATION_STAGE_NOTRECOGNIZED, null)};
        }
        String[] sampleFieldName = (String[]) sampleIncubatorModeCheckerInfo[1];
        Object[] sampleFieldValue = (Object[]) sampleIncubatorModeCheckerInfo[2];
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsData.Sample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
        Object[] diagnoses=Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE,
            EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE, sampleFieldName), sampleFieldValue, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {            
            diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, EnvMonSampleAPIactionsEndpoints.SINGLE_SAMPLE_INCUB_START.getSuccessMessageCode(), 
                    new Object[]{sampleId, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ", "))});           
            SampleAudit smpAudit = new SampleAudit();
            EnumIntAuditEvents sampleAuditEvName=null;
            if (incubationStage==1)
                sampleAuditEvName=SampleAudit.DataSampleAuditEvents.SAMPLE_SET_INCUBATION_1_STARTED;
            else
                sampleAuditEvName=SampleAudit.DataSampleAuditEvents.SAMPLE_SET_INCUBATION_2_STARTED;
            Object[] sampleAuditAdd = smpAudit.sampleAuditAdd(sampleAuditEvName, TblsData.TablesData.SAMPLE.getTableName(), 
                sampleId, sampleId, null, null, sampleFieldName, sampleFieldValue);
            return new Object[]{diagnoses, sampleAuditAdd};            
        }
        return new Object[]{diagnoses};
    }
    
    private static Object[] sampleIncubatorModeChecker(Integer incubationStage, String moment, String incubName, BigDecimal tempReading, String batchName){        
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] incubTempReadingRequiredArr = LPPlatform.isProcedureBusinessRuleDisable(procInstanceName, DataSampleIncubationBusinessRules.SAMPLE_INCUB_TEMP_READING_BUSRULE.getAreaName(), DataSampleIncubationBusinessRules.SAMPLE_INCUB_TEMP_READING_BUSRULE.getTagName());
        String sampleIncubationMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleIncubationBusinessRules.SAMPLE_INCUBATION_MODE.getAreaName(), DataSampleIncubationBusinessRules.SAMPLE_INCUBATION_MODE.getTagName());
        if (sampleIncubationMode.length()==0) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "SampleIncubatorModeBusinessRuleNotDefined", new Object[]{procInstanceName});
        if (Boolean.FALSE.equals(SampleIncubationModes.contains(sampleIncubationMode))) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "SampleIncubatorModeValueNotRrecognized", new Object[]{sampleIncubationMode});        
        
        String[] requiredFields=new String[0];
        Object[] requiredFieldsValue=new Object[0];
        
        if (sampleIncubationMode.contains(SampleIncubationObjects.SAMPLE.toString())){}
        else if (sampleIncubationMode.contains(SampleIncubationObjects.BATCH.toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.NOT_IMPLEMENTED, null);
        else
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUBATION_OBJECT_NOTRECOGNIZED, new Object[]{sampleIncubationMode});
        
        if (Boolean.FALSE.equals(SampleIncubationMoment.contains(moment)))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUBATION_STAGE_NOTRECOGNIZED, new Object[]{moment});        
        if (sampleIncubationMode.contains(SampleIncubationLevel.DATE.toString())){
            if (incubationStage == 2) {
                if (moment.contains(SampleIncubationMoment.START.toString())){
                    if (batchName!=null){
                        requiredFields = LPArray.addValueToArray1D(requiredFields, TblsData.Sample.INCUBATION2_BATCH.getName());
                        requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, batchName);                
                    }
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.INCUBATION2_START.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp()});                
                }else{
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.INCUBATION2_END.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp()});                                
                }
            }else{
                if (moment.contains(SampleIncubationMoment.START.toString())){
                    if (batchName!=null){
                        requiredFields = LPArray.addValueToArray1D(requiredFields, TblsData.Sample.INCUBATION_BATCH.getName());
                        requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, batchName);                
                    }
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.INCUBATION_START.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp()});                
                }else{
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.INCUBATION_END.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp()});                                
                }                
            }
        }else if (sampleIncubationMode.contains(SampleIncubationLevel.INCUBATOR.toString())){
            if (incubName==null) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUBATOR_NOT_ASSIGNED, null);
            Object[] incubInfo=Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), 
                    new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()}, new Object[]{incubName});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubInfo[0].toString()))
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUBATOR_NOT_ASSIGNED, new Object[]{incubName, procInstanceName});
            Integer tempReadingEvId=null;
            
            if (tempReading==null && Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(incubTempReadingRequiredArr[0].toString())) ){
                Object[][] incubLastTempReading=DataIncubatorNoteBook.getLastTemperatureReadingNoMask(incubName, 1);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubLastTempReading[0][0].toString())) return LPArray.array2dTo1d(incubLastTempReading);
                tempReadingEvId= Integer.valueOf(incubLastTempReading[0][0].toString());
                tempReading= BigDecimal.valueOf(Double.valueOf(incubLastTempReading[0][4].toString()));                
                Object[] tempReadingChecker=tempReadingBusinessRule(incubName, incubLastTempReading[0][2]);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(tempReadingChecker[0].toString())) return tempReadingChecker;
            }
            if (incubationStage == 2) {
                if (moment.contains(SampleIncubationMoment.START.toString())){
                    if (batchName!=null){
                        requiredFields = LPArray.addValueToArray1D(requiredFields, TblsData.Sample.INCUBATION2_BATCH.getName());
                        requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, batchName);                
                    }                    
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.INCUBATION2_START.getName(), 
                        TblsData.Sample.INCUBATION2_INCUBATOR.getName(), TblsData.Sample.INCUBATION2_PASSED.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp(), incubName, false});
                    if (tempReading!=null){
                        requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.INCUBATION2_START_TEMP_EVENT_ID.getName(), TblsData.Sample.INCUBATION2_START_TEMPERATURE.getName()});
                        requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{tempReadingEvId, tempReading});
                    }
                }else if (moment.contains(SampleIncubationMoment.END.toString())){
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.INCUBATION2_END.getName(), 
                        TblsData.Sample.INCUBATION2_INCUBATOR.getName(), TblsData.Sample.INCUBATION2_PASSED.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp(), incubName, true});
                    if (tempReading!=null){
                        requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.INCUBATION2_END_TEMP_EVENT_ID.getName(), TblsData.Sample.INCUBATION2_END_TEMPERATURE.getName()});
                        requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{tempReadingEvId, tempReading});
                    }
                }
            } else {
                if (moment.contains(SampleIncubationMoment.START.toString())){
                    if (batchName!=null){
                        requiredFields = LPArray.addValueToArray1D(requiredFields, TblsData.Sample.INCUBATION_BATCH.getName());
                        requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, batchName);                
                    }                    
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.INCUBATION_START.getName(), 
                        TblsData.Sample.INCUBATION_INCUBATOR.getName(), TblsData.Sample.INCUBATION_PASSED.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp(), incubName, false});
                    if (tempReading!=null){
                        requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.INCUBATION_START_TEMP_EVENT_ID.getName(), TblsData.Sample.INCUBATION_START_TEMPERATURE.getName()});
                        requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{tempReadingEvId, tempReading});
                    }                    
                }else if (moment.contains(SampleIncubationMoment.END.toString())){
                    requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.INCUBATION_END.getName(), TblsData.Sample.INCUBATION_PASSED.getName()});
                    requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{LPDate.getCurrentTimeStamp(), true});                
                    if (tempReading!=null){
                        requiredFields = LPArray.addValueToArray1D(requiredFields, new String[]{TblsData.Sample.INCUBATION_END_TEMP_EVENT_ID.getName(), TblsData.Sample.INCUBATION_END_TEMPERATURE.getName()});
                        requiredFieldsValue= LPArray.addValueToArray1D(requiredFieldsValue, new Object[]{tempReadingEvId, tempReading});                                        
                    }
                }
            }
        }else
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUBATION_OBJECT_NOTRECOGNIZED, new Object[]{sampleIncubationMode});
                
        return new Object[]{LPPlatform.LAB_TRUE, requiredFields, requiredFieldsValue};
    }
    
    private static Object[] tempReadingBusinessRule(String incubName, Object tempReadingDate){   
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String sampleIncubationTempReadingBusinessRulevalue = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleIncubationBusinessRules.SAMPLE_INCUB_TEMP_READING_BUSRULE.getAreaName(), DataSampleIncubationBusinessRules.SAMPLE_INCUB_TEMP_READING_BUSRULE.getTagName());
        String formatMask="yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatMask);
        String tmpReadingDateToStr=tempReadingDate.toString().substring(0, formatMask.length());
        LocalDateTime tempReadingDateDateTime = null;
        try{
            tempReadingDateDateTime = LocalDateTime.parse(tmpReadingDateToStr, formatter);
        }catch(DateTimeParseException e){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "error parsing <*1*><*2*>", new Object[]{e.getMessage(), tmpReadingDateToStr} );
        }
        if (sampleIncubationTempReadingBusinessRulevalue.length()==0)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "sampleIncubationTempReadingBusinessRule procedure property not found for procedure <*1*>.", new Object[]{procInstanceName} );
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
                    currDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.TEMPERATUREREADYDAY_ISNOTTODAY, new Object[]{tempReadingDate.toString(), procInstanceName} );                
            }else if (TempReadingBusinessRules.HOURS.toString().equalsIgnoreCase(currSampleIncubationTempReadingBusinessRulevalueArr[0])){
                long hours = ChronoUnit.HOURS.between(tempReadingDateDateTime, LPDate.getCurrentTimeStamp());
                if (hours>Long.valueOf(currSampleIncubationTempReadingBusinessRulevalueArr[1])){
                    currDiagn=false;
                    currDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleIncubationErrorTrapping.INCUB_TEMP_READING_INTVL_EXPIRED, new Object[]{tempReadingDate.toString(), hours, procInstanceName} );                                    
                }else{
                    currDiagn=true;
                }
            }else
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "sampleIncubationTempReadingBusinessRule procedure property value is <*1*> and is not a recognized value for procedure <*2*>", new Object[]{sampleIncubationTempReadingBusinessRulevalue, procInstanceName} );
            if (Boolean.FALSE.equals(currDiagn)){
                String curLevel=currSampleIncubationTempReadingBusinessRulevalueArr[currSampleIncubationTempReadingBusinessRulevalueArr.length-1];
                Boolean currLevelExists=false;
                for (TempReadingBusinessRulesLevel currBusRuleLvl: TempReadingBusinessRulesLevel.values()){
                    if (curLevel.equalsIgnoreCase(currBusRuleLvl.toString())) currLevelExists=true;
                }
                if (Boolean.FALSE.equals(currLevelExists)) curLevel=TempReadingBusinessRulesLevel.STOP.toString();
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
        if (Boolean.TRUE.equals(finalDiagn)) return new Object[]{LPPlatform.LAB_TRUE};  

        if (deviationAndStop>0){
            Rdbms.insertRecordInTable(TblsEnvMonitProcedure.TablesEnvMonitProcedure.INCUB_TEMP_READING_VIOLATIONS, 
                    new String[]{TblsEnvMonitProcedure.IncubatorTempReadingViolations.CREATED_ON.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.CREATED_BY.getName(), 
                        TblsEnvMonitProcedure.IncubatorTempReadingViolations.STARTED_ON.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.REASON.getName(), 
                        TblsEnvMonitProcedure.IncubatorTempReadingViolations.INCUBATOR.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.STAGE_CURRENT.getName()}, 
                    new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName(), LPDate.getCurrentTimeStamp(), deviationAndStopDiagn[deviationAndStopDiagn.length-1],
                        incubName, "CREATED"});
            return deviationAndStopDiagn;            
        }
    
        if (stoppables>0) return stoppablesDiagn;
        
        if (deviations>0){
            Rdbms.insertRecordInTable(TblsEnvMonitProcedure.TablesEnvMonitProcedure.INCUB_TEMP_READING_VIOLATIONS, 
                    new String[]{TblsEnvMonitProcedure.IncubatorTempReadingViolations.CREATED_ON.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.CREATED_BY.getName(), 
                        TblsEnvMonitProcedure.IncubatorTempReadingViolations.STARTED_ON.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.REASON.getName(), 
                        TblsEnvMonitProcedure.IncubatorTempReadingViolations.INCUBATOR.getName(), TblsEnvMonitProcedure.IncubatorTempReadingViolations.STAGE_CURRENT.getName()}, 
                    new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName(), LPDate.getCurrentTimeStamp(), deviationsDiagn[deviationsDiagn.length-1],
                        incubName, "CREATED"});
            deviationsDiagn[0]=LPPlatform.LAB_TRUE;
            return deviationsDiagn;
        }
        
        return new Object[]{LPPlatform.LAB_FALSE}; 
    }    
    
    public enum DataSampleIncubationErrorTrapping implements EnumIntMessages{ 
        INCUBATORBATCH_NOT_STARTED("IncubatorBatchNotStartedYet", "The batch <*1*> was not started yet for procedure <*2*>", "La tanda <*1*> no está iniciada todavía para el proceso <*2*>"),
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
    
