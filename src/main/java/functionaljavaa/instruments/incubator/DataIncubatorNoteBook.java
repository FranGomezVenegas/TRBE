/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.instruments.incubator;

import com.labplanet.servicios.moduleenvmonit.EnvMonIncubatorAPIactions.EnvMonIncubatorAPIactionsEndpoints;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import functionaljavaa.instruments.incubator.ConfigIncubator.ConfigIncubatorBusinessRules;
import functionaljavaa.instruments.incubator.ConfigIncubator.ConfigIncubatorErrorTrapping;
import functionaljavaa.instruments.incubator.ConfigIncubator.ConfigIncubatorLockingReason;
import functionaljavaa.materialspec.DataSpec;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleStructureSuccess;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
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
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
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
        Object[][] instrInfo=QueryUtilitiesEnums.getTableData(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
            EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()}),
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.NOT_EXISTS, new Object[]{instName, procInstanceName});
        if (!Boolean.valueOf(instrInfo[0][1].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.CURRENTLY_DEACTIVE, new Object[]{instName, procInstanceName});
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK, 
                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_BY.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.TEMPERATURE.getName()}, 
                new Object[]{instName, EventType.TEMPERATURE_READING.toString(), personName, LPDate.getCurrentTimeStamp(), temperature});
        return insertRecordInTable.getApiMessage();
    }
    
    /**
     *
     * @param instName
     * @param personName
     * @return
     */
    public static Object[] activation(String instName, String personName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[][] instrInfo=QueryUtilitiesEnums.getTableData(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
            EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()}),
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.NOT_EXISTS, new Object[]{instName, procInstanceName});
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK, 
                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_BY.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName()}, 
                new Object[]{instName, EventType.ACTIVATE.toString(), personName, LPDate.getCurrentTimeStamp()});
        return insertRecordInTable.getApiMessage();
    }
    
    /**
     *
     * @param instName
     * @param personName
     * @return
     */
    public static Object[] deactivation(String instName, String personName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[][] instrInfo=QueryUtilitiesEnums.getTableData(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
            EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()}),
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.NOT_EXISTS, new Object[]{instName, procInstanceName});
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK, 
                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_BY.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName()}, 
                new Object[]{instName, EventType.DEACTIVATE.toString(), personName, LPDate.getCurrentTimeStamp()});
        return insertRecordInTable.getApiMessage();
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
        Object[] dbMaxFldExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), TblsEnvMonitConfig.InstrIncubator.MAX.getName());
        String[] fieldsToRetrieve=new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()};
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), TblsEnvMonitConfig.InstrIncubator.LOCKED.getName())[0].toString()))
            fieldsToRetrieve=LPArray.addValueToArray1D(fieldsToRetrieve, new String[]{TblsEnvMonitConfig.InstrIncubator.LOCKED.getName(), TblsEnvMonitConfig.InstrIncubator.LOCKED_REASON.getName()});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbMaxFldExists[0].toString()))
            fieldsToRetrieve=LPArray.addValueToArray1D(fieldsToRetrieve, new String[]{TblsEnvMonitConfig.InstrIncubator.MIN.getName(), TblsEnvMonitConfig.InstrIncubator.IS_MIN_STRICT.getName(), TblsEnvMonitConfig.InstrIncubator.MAX.getName(), TblsEnvMonitConfig.InstrIncubator.IS_MAX_STRICT.getName()});

        Object[][] instrInfo=QueryUtilitiesEnums.getTableData(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
            EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
                fieldsToRetrieve),
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.NOT_EXISTS, new Object[]{instName, procInstanceName});
        if (!Boolean.valueOf(instrInfo[0][1].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.CURRENTLY_DEACTIVE, new Object[]{instName, procInstanceName});
        String[] insFldsName=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_BY.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.TEMPERATURE.getName()};
        Object[] insFldsValue=new Object[]{instName, EventType.TEMPERATURE_READING.toString(), personName, LPDate.getCurrentTimeStamp(), temperature};
        String specEval = "";
        String specEvalDetail = "";
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbMaxFldExists[0].toString())){
            DataSpec dtSpec=new DataSpec();
            BigDecimal minVal=null;
            if (LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.MIN.getName())]).toString().length()>0)
                minVal = BigDecimal.valueOf(Double.valueOf(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.MIN.getName())].toString()));
            Boolean minIsStrict = null;
            if (LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.IS_MIN_STRICT.getName())]).toString().length()>0)
                minIsStrict=Boolean.valueOf(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.IS_MIN_STRICT.getName())].toString());
            BigDecimal maxVal = null;
            if (LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.MAX.getName())]).toString().length()>0)
                maxVal = BigDecimal.valueOf(Double.valueOf(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.MAX.getName())].toString()));
            Boolean maxIsStrict = null;
            if (LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.IS_MAX_STRICT.getName())]).toString().length()>0)
                maxIsStrict = Boolean.valueOf(instrInfo[0][LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.InstrIncubator.IS_MAX_STRICT.getName())].toString());
            Object[] resultCheck = dtSpec.resultCheck(temperature, minVal, maxVal, minIsStrict, maxIsStrict, null, null);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultCheck[0].toString())) return resultCheck;
            EnumIntMessages checkMsgCode=(EnumIntMessages) resultCheck[resultCheck.length - 1];
            specEval = checkMsgCode.getErrorCode();
            specEvalDetail = (String) resultCheck[resultCheck.length - 2];
            insFldsName=LPArray.addValueToArray1D(insFldsName, new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.SPEC_EVAL.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.SPEC_EVAL_DETAIL.getName()});
            insFldsValue=LPArray.addValueToArray1D(insFldsValue, new Object[]{specEval, specEvalDetail});
        }        
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK, insFldsName, insFldsValue);
        if (!insertRecordInTable.getRunSuccess()) return insertRecordInTable.getApiMessage();
        
        incubatorLocking(instName, new Object[]{specEval, specEvalDetail}, fieldsToRetrieve, instrInfo[0]);
        
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, EnvMonIncubatorAPIactionsEndpoints.EM_INCUBATOR_ADD_TEMP_READING, new Object[]{instName, procInstanceName});
    }
    
    /**
     * This method returns the last temperature reading for the latest activate session, in other words, when invoked after having two temperature readings after
     * being activated from a deactivation period then it will return just those two readings even not being enough to covers the requested number of points.
     * @param instName
     * @param points
     * @return
     */
    
    public static Object[][] getLastTemperatureReadingNoMask(String instName, Integer points){
        return getLastTemperatureReading(instName, points, null, null, false);
    }
    public static Object[][] getLastTemperatureReading(String instName, Integer points){
        return getLastTemperatureReading(instName, points, null, null, true);
    }

    public static Object[][] getLastTemperatureReading(String instName, Integer points, LocalDateTime date){
        return getLastTemperatureReading(instName, points, date, null, true);
    }
    public static Object[][] getLastTemperatureReading(String instName, Integer points, String[] fieldsToRetrieve){
        return getLastTemperatureReading(instName, points, null, null, fieldsToRetrieve, true);
    }
    
    public static Object[][] getLastTemperatureReading(String instName, Integer points, LocalDateTime startDate, LocalDateTime endDate, Boolean withMask){   
        String[] fieldsToRetrieve=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.ID.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_BY.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.TEMPERATURE.getName()};
        return getLastTemperatureReading(instName, points, startDate, endDate, fieldsToRetrieve, withMask);
    }
    public static Object[][] getLastTemperatureReading(String instName, Integer points, LocalDateTime startDate, LocalDateTime endDate, String[] fieldsToRetrieve, Boolean withMask){   
        EnumIntTableFields[] fieldsToRetrieveObj=EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
            new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()});
        String procInstanceName=ProcedureRequestSession.getInstanceForQueries(null, null, null).getProcedureInstance();
        if (procInstanceName==null) return LPArray.array1dTo2d(new String[]{LPPlatform.LAB_FALSE},1);
        Object[][] instrInfo=null;
        if (withMask)
            instrInfo=QueryUtilitiesEnums.getTableData(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
                fieldsToRetrieveObj,
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()}, new Object[]{instName},
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()});
        else
            instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())) return instrInfo;
        
        Integer activeFldPosic=EnumIntTableFields.getFldPosicInArray(fieldsToRetrieveObj, TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName());
        if (!Boolean.valueOf(instrInfo[0][activeFldPosic].toString())){
            Object[] errDiagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, " ", new Object[0]);
            return LPArray.array1dTo2d(errDiagn, errDiagn.length);
        }
        String[] whereFieldName=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.NAME.getName()};
        Object[] whereFieldValue=new Object[]{instName};
        if (startDate!=null){
            if (endDate!=null){
                whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName()+" BETWEEN ");
                whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, startDate);
                whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, endDate);
            }else{
                whereFieldName=LPArray.addValueToArray1D(whereFieldName, TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName()+" <= ");
                whereFieldValue=LPArray.addValueToArray1D(whereFieldValue, startDate);
            }
        }
        fieldsToRetrieveObj=EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK, 
            fieldsToRetrieve);        
        Object[][] instrNotebook=null;
        if (withMask)
            instrNotebook=QueryUtilitiesEnums.getTableData(TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK, 
                fieldsToRetrieveObj, whereFieldName, whereFieldValue, 
                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.ID.getName()+ " desc"});
        else
            instrNotebook=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK.getTableName(), 
                whereFieldName, whereFieldValue, 
                fieldsToRetrieve, new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.ID.getName()+ " desc"});         
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrNotebook[0][0].toString())){
            Object[] errDiagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataIncubatorNoteBookErrorTrapping.NO_READINGS_LOGGED_YET, new Object[0]);
            return LPArray.array1dTo2d(errDiagn, errDiagn.length);            
        }
        
        Object[] pointsFromLatestActivation= new Object[0];        
        Integer pointsAdded=0;
        Integer eventTypeFldPosic=EnumIntTableFields.getFldPosicInArray(fieldsToRetrieveObj, TblsEnvMonitData.InstrIncubatorNoteBook.EVENT_TYPE.getName());
        for (Object[] currReading: instrNotebook){
            String currEventType = currReading[eventTypeFldPosic].toString();
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
            return new Object[][]{errDiagn};            
        } 
        return LPArray.array1dTo2d(pointsFromLatestActivation, instrNotebook[0].length);
    }
    public static Object[] incubatorLocking(String instName, Object[] specEvalInfo, String[] incubFldNames, Object[] incubFldValues){
        String procInstanceName=ProcedureRequestSession.getInstanceForQueries(null, null, null).getProcedureInstance();     
        Object[] procedureBusinessRuleEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, ConfigIncubatorBusinessRules.LOCK_WHEN_TEMP_OUT_OF_RANGE.getAreaName(), ConfigIncubatorBusinessRules.LOCK_WHEN_TEMP_OUT_OF_RANGE.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureBusinessRuleEnable[0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "incubationLockingNotEnabled", null);
        if (!LPArray.valueInArray(incubFldNames, TblsEnvMonitConfig.InstrIncubator.LOCKED.getName())){
            if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), TblsEnvMonitConfig.InstrIncubator.LOCKED.getName())[0].toString())) 
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "lockedFieldNotPresentInIncubatorForProcedure", new Object[]{procInstanceName});
            incubFldNames=new String[]{TblsEnvMonitConfig.InstrIncubator.LOCKED.getName(), TblsEnvMonitConfig.InstrIncubator.LOCKED_REASON.getName()};        

            Object[][] instrInfo=QueryUtilitiesEnums.getTableData(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
                EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR,incubFldNames),
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())) return instrInfo[0];
            incubFldValues=instrInfo[0];        
        }
        Object[] updateRecordFieldsByFilter=null;
        String[] updFldName=null;
        Object[] updFldValue=null;
        if (DataSampleStructureSuccess.EVALUATION_IN.getErrorCode().equalsIgnoreCase(specEvalInfo[0].toString())){
            if ("TRUE".equalsIgnoreCase(LPNulls.replaceNull(incubFldValues[LPArray.valuePosicInArray(incubFldNames, TblsEnvMonitConfig.InstrIncubator.LOCKED.getName())].toString())) ){
                updFldName=new String[]{TblsEnvMonitConfig.InstrIncubator.LOCKED.getName(), TblsEnvMonitConfig.InstrIncubator.LOCKED_REASON.getName()};
                updFldValue=new Object[]{false,""};
                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsEnvMonitConfig.InstrIncubator.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{instName}, "");
                updateRecordFieldsByFilter=Rdbms.updateRecordFieldsByFilter(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR,
                        EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, updFldName), updFldValue, sqlWhere, null);
            }
        }else{
            if (specEvalInfo[0].toString().toUpperCase().contains("SPEC")){
                if (!"TRUE".equalsIgnoreCase(LPNulls.replaceNull(incubFldValues[LPArray.valuePosicInArray(incubFldNames, TblsEnvMonitConfig.InstrIncubator.LOCKED.getName())].toString())) ){
                    updFldName=new String[]{TblsEnvMonitConfig.InstrIncubator.LOCKED.getName(), TblsEnvMonitConfig.InstrIncubator.LOCKED_REASON.getName()};
                    updFldValue=new Object[]{true,ConfigIncubatorLockingReason.TEMP_READING_OUT_OF_RANGE.getTagName()};
                    SqlWhere sqlWhere = new SqlWhere();
                    sqlWhere.addConstraint(TblsEnvMonitConfig.InstrIncubator.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{instName}, "");
                    updateRecordFieldsByFilter=Rdbms.updateRecordFieldsByFilter(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR,
                            EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, updFldName), updFldValue, sqlWhere, null);
                }                
            }
        }
        if (updateRecordFieldsByFilter==null) return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "noChangesRequired", null);
        return updateRecordFieldsByFilter;            
        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "notImplementedYet", null);
    }
}
