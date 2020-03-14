/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.instruments.incubator;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import databases.Rdbms;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author User
 */
public class DataIncubatorNoteBook {
    
    private enum EventType{TEMPERATURE_READING, ACTIVATE, DEACTIVATE}
    
    /**
     *
     * @param schemaPrefix
     * @param instName
     * @param temperature
     * @param personName
     * @return
     */
    public static Object[] temperatureReading(String schemaPrefix, String instName, BigDecimal temperature, String personName){
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "INCUBATOR_NOT_EXIST", new Object[]{instName, schemaPrefix});
        if (!Boolean.valueOf(instrInfo[0][1].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "INCUBATOR_NOT_ACTIVE", new Object[]{instName, schemaPrefix});
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), 
                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_TEMPERATURE.getName()}, 
                new Object[]{instName, EventType.TEMPERATURE_READING.toString(), personName, LPDate.getCurrentTimeStamp(), temperature});
        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "NOT_IMPLEMENTED", new Object[0]);
    }
    
    /**
     *
     * @param schemaPrefix
     * @param instName
     * @param personName
     * @return
     */
    public static Object[] activation(String schemaPrefix, String instName, String personName){
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "INCUBATOR_NOT_EXIST", new Object[]{instName, schemaPrefix});
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), 
                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName()}, 
                new Object[]{instName, EventType.ACTIVATE.toString(), personName, LPDate.getCurrentTimeStamp()});
        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "NOT_IMPLEMENTED", new Object[0]);
    }
    
    /**
     *
     * @param schemaPrefix
     * @param instName
     * @param personName
     * @return
     */
    public static Object[] deactivation(String schemaPrefix, String instName, String personName){
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "INCUBATOR_NOT_EXIST", new Object[]{instName, schemaPrefix});
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), 
                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName()}, 
                new Object[]{instName, EventType.DEACTIVATE.toString(), personName, LPDate.getCurrentTimeStamp()});
        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "NOT_IMPLEMENTED", new Object[0]);
    }

    /**
     *
     * @param schemaPrefix
     * @param instName
     * @param personName
     * @param temperature
     * @return
     */
    public static Object[] newTemperatureReading(String schemaPrefix, String instName, String personName, BigDecimal temperature){
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "INCUBATOR_NOT_EXIST", new Object[]{instName, schemaPrefix});
        if (!Boolean.valueOf(instrInfo[0][1].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "INCUBATOR_NOT_ACTIVE", new Object[]{instName, schemaPrefix});
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), 
                new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_NAME.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName(),
                    TblsEnvMonitData.InstrIncubatorNoteBook.FLD_TEMPERATURE.getName()}, 
                new Object[]{instName, EventType.TEMPERATURE_READING.toString(), personName, LPDate.getCurrentTimeStamp(), temperature});
        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "NOT_IMPLEMENTED", new Object[0]);
    }
    
    /**
     * This method returns the last temperature reading for the latest activate session, in other words, when invoked after having two temperature readings after
     * being activated from a deactivation period then it will return just those two readings even not being enough to covers the requested number of points.
     * @param schemaPrefix
     * @param instName
     * @param points
     * @return
     */
    public static Object[][] getLastTemperatureReading(String schemaPrefix, String instName, Integer points){
        return getLastTemperatureReading(schemaPrefix, instName, points, null, null);
    }

    public static Object[][] getLastTemperatureReading(String schemaPrefix, String instName, Integer points, LocalDateTime date){
        return getLastTemperatureReading(schemaPrefix, instName, points, date, null);
    }
    
    public static Object[][] getLastTemperatureReading(String schemaPrefix, String instName, Integer points, LocalDateTime startDate, LocalDateTime endDate){   
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
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
        Object[][] instrNotebook=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), 
                whereFieldName, whereFieldValue, 
                fieldsToRetrieve, new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_ID.getName()+ " desc"});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrNotebook[0][0].toString())) return instrNotebook;
        Object[] pointsFromLatestActivation= new Object[0];        
        Integer pointsAdded=0;
        for (Object[] currReading: instrNotebook){
            String currEventType = currReading[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName())].toString();
            if ( (EventType.ACTIVATE.toString().equalsIgnoreCase(currEventType)) || (EventType.DEACTIVATE.toString().equalsIgnoreCase(currEventType)) ) break;
            if (EventType.TEMPERATURE_READING.toString().equalsIgnoreCase(currEventType)){                
                pointsAdded++;
                pointsFromLatestActivation=LPArray.addValueToArray1D(pointsFromLatestActivation, currReading);
                if ((endDate==null) && (points!=null) && (points<=pointsAdded)) break;
            }
        }
        if (pointsAdded==0){
            Object[] errDiagn=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "NO_READINGS_SINCE_LATEST_ACTIVATION", new Object[0]);
            return LPArray.array1dTo2d(errDiagn, errDiagn.length-1);            
        } 
        return LPArray.array1dTo2d(pointsFromLatestActivation, instrNotebook[0].length);
    }
    
}
