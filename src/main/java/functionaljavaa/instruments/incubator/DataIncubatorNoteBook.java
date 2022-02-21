/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.instruments.incubator;

import com.labplanet.servicios.moduleenvmonit.EnvMonIncubationAPI.EnvMonIncubationAPIEndpoints;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import databases.Rdbms;
import functionaljavaa.instruments.incubator.ConfigIncubator.ConfigIncubatorBusinessRules;
import functionaljavaa.instruments.incubator.ConfigIncubator.ConfigIncubatorErrorTrapping;
import functionaljavaa.instruments.incubator.ConfigIncubator.ConfigIncubatorLockingReason;
import functionaljavaa.materialspec.DataSpec;
import functionaljavaa.materialspec.DataSpec.ResultCheckSuccessErrorTrapping;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntMessages;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author User
 */
public class DataIncubatorNoteBook {

    public enum DataIncubatorNoteBookErrorTrapping implements EnumIntMessages{ 
//        INCUBATOR_NOT_EXIST("IncubatorNotExists", "The incubator <*1*> does not exist for procedure <*2*>", "La incubadora <*1*> no existe para el proceso <*2*>"),
        NO_READINGS_SINCE_LATEST_ACTIVATION("IncubatorNotebookNoReadingSinceLatestActivation", "", ""),
        NO_READINGS_LOGGED_YET("IncubatorNotebookNoReadingLoggedYet", "", "")
        ;
        private DataIncubatorNoteBookErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
    private enum EventType{TEMPERATURE_READING, ACTIVATE, DEACTIVATE}
    
    /**
     *
     * @param instName
     * @param temperature
     * @param personName
     * @return
     */
    public static Object[] temperatureReading(String instName, BigDecimal temperature, String personName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.NOT_EXISTS, new Object[]{instName, procInstanceName});
        if (!Boolean.valueOf(instrInfo[0][1].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.CURRENTLY_DEACTIVE, new Object[]{instName, procInstanceName});
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), 
                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_TEMPERATURE.getName()}, 
                new Object[]{instName, EventType.TEMPERATURE_READING.toString(), personName, LPDate.getCurrentTimeStamp(), temperature});
    }
    
    /**
     *
     * @param instName
     * @param personName
     * @return
     */
    public static Object[] activation(String instName, String personName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.NOT_EXISTS, new Object[]{instName, procInstanceName});
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), 
                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName()}, 
                new Object[]{instName, EventType.ACTIVATE.toString(), personName, LPDate.getCurrentTimeStamp()});
    }
    
    /**
     *
     * @param instName
     * @param personName
     * @return
     */
    public static Object[] deactivation(String instName, String personName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.NOT_EXISTS, new Object[]{instName, procInstanceName});
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), 
                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName()}, 
                new Object[]{instName, EventType.DEACTIVATE.toString(), personName, LPDate.getCurrentTimeStamp()});
    }

    /**
     *
     * @param instName
     * @param personName
     * @param temperature
     * @return
     */
    public static Object[] newTemperatureReading(String instName, String personName, BigDecimal temperature){        
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] dbMaxFldExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_MAX.getName());
        String[] fieldsToRetrieve=new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()};
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED.getName())[0].toString()))
            fieldsToRetrieve=LPArray.addValueToArray1D(fieldsToRetrieve, new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED_REASON.getName()});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbMaxFldExists[0].toString()))
            fieldsToRetrieve=LPArray.addValueToArray1D(fieldsToRetrieve, new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_MIN.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_IS_MIN_STRICT.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_MAX.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_IS_MAX_STRICT.getName()});
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.NOT_EXISTS, new Object[]{instName, procInstanceName});
        if (!Boolean.valueOf(instrInfo[0][1].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.CURRENTLY_DEACTIVE, new Object[]{instName, procInstanceName});
        String[] insFldsName=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_TEMPERATURE.getName()};
        Object[] insFldsValue=new Object[]{instName, EventType.TEMPERATURE_READING.toString(), personName, LPDate.getCurrentTimeStamp(), temperature};
        String specEval = "";
        String specEvalDetail = "";
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbMaxFldExists[0].toString())){
            DataSpec dtSpec=new DataSpec();
            BigDecimal minVal=null;
            if (LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.FLD_MIN.getName())]).toString().length()>0)
                minVal = BigDecimal.valueOf(Double.valueOf(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.FLD_MIN.getName())].toString()));
            Boolean minIsStrict = null;
            if (LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.FLD_IS_MIN_STRICT.getName())]).toString().length()>0)
                minIsStrict=Boolean.valueOf(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.FLD_IS_MIN_STRICT.getName())].toString());
            BigDecimal maxVal = null;
            if (LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.FLD_MAX.getName())]).toString().length()>0)
                maxVal = BigDecimal.valueOf(Double.valueOf(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.FLD_MAX.getName())].toString()));
            Boolean maxIsStrict = null;
            if (LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.FLD_IS_MAX_STRICT.getName())]).toString().length()>0)
                maxIsStrict = Boolean.valueOf(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.FLD_IS_MAX_STRICT.getName())].toString());
            Object[] resultCheck = dtSpec.resultCheck(temperature, minVal, maxVal, minIsStrict, maxIsStrict, null, null);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultCheck[0].toString())) return resultCheck;
            specEval = (String) resultCheck[resultCheck.length - 1];
            specEvalDetail = (String) resultCheck[resultCheck.length - 2];
            insFldsName=LPArray.addValueToArray1D(insFldsName, new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_SPEC_EVAL.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_SPEC_EVAL_DETAIL.getName()});
            insFldsValue=LPArray.addValueToArray1D(insFldsValue, new Object[]{specEval, specEvalDetail});
        }        
        Object[] diagn=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), 
                insFldsName, insFldsValue);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        
        incubatorLocking(instName, new Object[]{specEval, specEvalDetail}, fieldsToRetrieve, instrInfo[0]);
        
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, EnvMonIncubationAPIEndpoints.EM_INCUBATION_ADD_TEMP_READING, new Object[]{instName, procInstanceName});
    }
    
    /**
     * This method returns the last temperature reading for the latest activate session, in other words, when invoked after having two temperature readings after
     * being activated from a deactivation period then it will return just those two readings even not being enough to covers the requested number of points.
     * @param instName
     * @param points
     * @return
     */
    public static Object[][] getLastTemperatureReading(String instName, Integer points){
        return getLastTemperatureReading(instName, points, null, null);
    }

    public static Object[][] getLastTemperatureReading(String instName, Integer points, LocalDateTime date){
        return getLastTemperatureReading(instName, points, date, null);
    }
    public static Object[][] getLastTemperatureReading(String instName, Integer points, String[] fieldsToRetrieve){
        return getLastTemperatureReading(instName, points, null, null, fieldsToRetrieve);
    }
    
    public static Object[][] getLastTemperatureReading(String instName, Integer points, LocalDateTime startDate, LocalDateTime endDate){   
        String[] fieldsToRetrieve=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_ID.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_TEMPERATURE.getName()};
        return getLastTemperatureReading(instName, points, startDate, endDate, fieldsToRetrieve);
    }
    public static Object[][] getLastTemperatureReading(String instName, Integer points, LocalDateTime startDate, LocalDateTime endDate, String[] fieldsToRetrieve){   
        String procInstanceName=ProcedureRequestSession.getInstanceForQueries(null, null, null).getProcedureInstance();
        if (procInstanceName==null) return LPArray.array1dTo2d(new String[]{LPPlatform.LAB_FALSE},1);
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())) return instrInfo;
        
        if (!Boolean.valueOf(instrInfo[0][1].toString())){
            Object[] errDiagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, " ", new Object[0]);
            return LPArray.array1dTo2d(errDiagn, errDiagn.length);
        }
        String[] whereFieldName=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_NAME.getName()};
        Object[] whereFieldValue=new Object[]{instName};
        if (startDate!=null){
            if (endDate!=null){
                whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName()+" BETWEEN ");
                whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, startDate);
                whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, endDate);
            }else{
                whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName()+" <= ");
                whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, startDate);
            }
        }
        Object[][] instrNotebook=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), 
                whereFieldName, whereFieldValue, 
                fieldsToRetrieve, new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_ID.getName()+ " desc"});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrNotebook[0][0].toString())){
            Object[] errDiagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataIncubatorNoteBookErrorTrapping.NO_READINGS_LOGGED_YET, new Object[0]);
            return LPArray.array1dTo2d(errDiagn, errDiagn.length);            
        }
        
        Object[] pointsFromLatestActivation= new Object[0];        
        Integer pointsAdded=0;
        for (Object[] currReading: instrNotebook){
            String currEventType = currReading[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName())].toString();
            if ( (EventType.ACTIVATE.toString().equalsIgnoreCase(currEventType)) || (EventType.DEACTIVATE.toString().equalsIgnoreCase(currEventType)) 
                    || (EventType.TEMPERATURE_READING.toString().equalsIgnoreCase(currEventType) && (endDate==null) && (points!=null) && (points<=pointsAdded) )) break;
            if (EventType.TEMPERATURE_READING.toString().equalsIgnoreCase(currEventType)){                
                pointsAdded++;
                pointsFromLatestActivation=LPArray.addValueToArray1D(pointsFromLatestActivation, currReading);
               // if ((endDate==null) && (points!=null) && (points<=pointsAdded)) break;
            }
        }
        if (pointsAdded==0){
            Object[] errDiagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataIncubatorNoteBookErrorTrapping.NO_READINGS_SINCE_LATEST_ACTIVATION, new Object[0]);
            return LPArray.array1dTo2d(errDiagn, errDiagn.length-1);            
        } 
        return LPArray.array1dTo2d(pointsFromLatestActivation, instrNotebook[0].length);
    }
    public static Object[] incubatorLocking(String instName, Object[] specEvalInfo, String[] incubFldNames, Object[] incubFldValues){
        String procInstanceName=ProcedureRequestSession.getInstanceForQueries(null, null, null).getProcedureInstance();     
        Object[] procedureBusinessRuleEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, ConfigIncubatorBusinessRules.LOCK_WHEN_TEMP_OUT_OF_RANGE.getAreaName(), ConfigIncubatorBusinessRules.LOCK_WHEN_TEMP_OUT_OF_RANGE.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureBusinessRuleEnable[0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "incubationLockingNotEnabled", null);
        if (!LPArray.valueInArray(incubFldNames, TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED.getName())){
            if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED.getName())[0].toString())) 
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "lockedFieldNotPresentInIncubatorForProcedure", new Object[]{procInstanceName});
            incubFldNames=new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED_REASON.getName()};        
            Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                incubFldNames);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())) return instrInfo[0];
            incubFldValues=instrInfo[0];        
        }
        Object[] updateRecordFieldsByFilter=null;
        String[] updFldName=null;
        Object[] updFldValue=null;
        if (ResultCheckSuccessErrorTrapping.EVALUATION_IN.getErrorCode().equalsIgnoreCase(specEvalInfo[0].toString())){
            if ("TRUE".equalsIgnoreCase(LPNulls.replaceNull(incubFldValues[LPArray.valuePosicInArray(incubFldNames, TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED.getName())].toString())) ){
                updFldName=new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED_REASON.getName()};
                updFldValue=new Object[]{false,""};
                updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(),
                    updFldName, updFldValue, new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName});
            }
        }else{
            if (specEvalInfo[0].toString().toUpperCase().contains("SPEC")){
                if (!"TRUE".equalsIgnoreCase(LPNulls.replaceNull(incubFldValues[LPArray.valuePosicInArray(incubFldNames, TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED.getName())].toString())) ){
                    updFldName=new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_LOCKED_REASON.getName()};
                    updFldValue=new Object[]{true,ConfigIncubatorLockingReason.TEMP_READING_OUT_OF_RANGE.getTagName()};
                    updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(),
                        updFldName, updFldValue, new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName});
                }                
            }
        }
        if (updateRecordFieldsByFilter==null) return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "noChangesRequired", null);
        return updateRecordFieldsByFilter;            
        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "notImplementedYet", null);
    }
}
