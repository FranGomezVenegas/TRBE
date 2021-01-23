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
import functionaljavaa.instruments.incubator.ConfigIncubator.ConfigIncubatorErrorCodes;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class DataIncubatorNoteBook {

    public enum DataIncubatorNoteBookErrorTrapping{ 
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
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorCodes.NOT_EXISTS.getErrorCode(), new Object[]{instName, procInstanceName});
        if (!Boolean.valueOf(instrInfo[0][1].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorCodes.CURRENTLY_DEACTIVE.getErrorCode(), new Object[]{instName, procInstanceName});
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
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorCodes.NOT_EXISTS.getErrorCode(), new Object[]{instName, procInstanceName});
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
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorCodes.NOT_EXISTS.getErrorCode(), new Object[]{instName, procInstanceName});
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
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorCodes.NOT_EXISTS.getErrorCode(), new Object[]{instName, procInstanceName});
        if (!Boolean.valueOf(instrInfo[0][1].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorCodes.CURRENTLY_DEACTIVE.getErrorCode(), new Object[]{instName, procInstanceName});
        Object[] diagn=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), 
                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_TEMPERATURE.getName()}, 
                new Object[]{instName, EventType.TEMPERATURE_READING.toString(), personName, LPDate.getCurrentTimeStamp(), temperature});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, EnvMonIncubationAPIEndpoints.EM_INCUBATION_ADD_TEMP_READING.getSuccessMessageCode(), new Object[]{instName, procInstanceName});
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
    
    public static Object[][] getLastTemperatureReading(String instName, Integer points, LocalDateTime startDate, LocalDateTime endDate){   
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())) return instrInfo;
        
        if (!Boolean.valueOf(instrInfo[0][1].toString())){
            Object[] errDiagn=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " ", new Object[0]);
            return LPArray.array1dTo2d(errDiagn, errDiagn.length);
        }
        String[] fieldsToRetrieve=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_ID.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_TEMPERATURE.getName()};
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
            Object[] errDiagn=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataIncubatorNoteBookErrorTrapping.NO_READINGS_LOGGED_YET.getErrorCode(), new Object[0]);
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
            Object[] errDiagn=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataIncubatorNoteBookErrorTrapping.NO_READINGS_SINCE_LATEST_ACTIVATION.getErrorCode(), new Object[0]);
            return LPArray.array1dTo2d(errDiagn, errDiagn.length-1);            
        } 
        return LPArray.array1dTo2d(pointsFromLatestActivation, instrNotebook[0].length);
    }
    
}
