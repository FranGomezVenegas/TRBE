/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import databases.Rdbms;
import databases.TblsApp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class ConfigProgramCalendar {

    /**
     *
     */
    public enum ConflictDetail{

        /**
         *
         */
        DAY_CONVERTED_ON_HOLIDAYS("This day was converted on holidays"),

        /**
         *
         */
        DAY_IS_MARKED_AS_HOLIDAYS("This day is marked as holidays")
    ;    
    private ConflictDetail(String description){
        this.description=description;
    }    
    String description;

      /**
       *
       * @return
       */
      public String getDescription(){
        return this.description;
    }    
  }
  //public static final String CONFLICT_DETAIL_= ;
  
    /**
     *
     */
    public static final String ERROR_TRAPING_HOLIDAY_CALENDAR_EMPTY="holidayCalendarEmpty";

    /**
     *
     */
    public static final String ERROR_TRAPING_START_DATE_CANNOT_BE_NULL="Start date cannot be null";

    /**
     *
     */
    public static final String ERROR_TRAPING_END_DATE_CANNOT_BE_NULL="End date cannot be null";

    /**
     *
     */
    public static final String ERROR_TRAPING_NO_DAYS_IN_RANGE="noDaysInRange";

    /**
     *
     */
    public static final String HOLIDAY_CALENDAR_ADDED="Holiday calendar added";
  

  String project;
  int scheduleId;
  int scheduleSize;
  String itemsMeasurement; 
  Date firstDay;
  Date endDay;
    
    /**
     *
     */
    public enum ScheduleSizeUnits{

        /**
         *
         */
        DAYS,

        /**
         *
         */
        MONTHS,

      /**
       *
       */
      YEARS;
    }

    /**
     *
     */
    public enum recursiveRules{

        /**
         *
         */
        MONDAYS,

        /**
         *
         */
        TUESDAYS,

        /**
         *
         */
        WEDNESDAYS,

      /**
       *
       */
      THURSDAYS,

        /**
         *
         */
        FRIDAYS,

        /**
         *
         */
        SATURDAYS,

        /**
         *
         */
        SUNDAYS;
    }
        

    /**
     *
     * @param scheduleSize
     * @param itemsMeasurement
     * @param startDay
     */
    public void dataProjectSchedule (int scheduleSize, String itemsMeasurement, Date startDay){
        //EnumUtils.isValidEnum(itemsMeasurementType.class, itemsMeasurement);
        Date endDayLocal = new Date();
        this.itemsMeasurement =itemsMeasurement;
        this.scheduleSize=scheduleSize;
        this.firstDay=startDay;
        endDayLocal=LPDate.addIntervalToGivenDate(startDay, itemsMeasurement, scheduleSize);
        this.endDay=endDayLocal;                        
    }

        /**
     *
     * @param pName
     * @param programCalendarId
   * @param fieldsToRetrieve
     * @return
     */
    public static Object[][] getConfigProgramCalendar(String pName, int programCalendarId, String[] fieldsToRetrieve) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        if (fieldsToRetrieve==null) fieldsToRetrieve=new String[]{TblsEnvMonitConfig.ProgramCalendar.FLD_PROGRAM_ID.getName(), TblsEnvMonitConfig.ProgramCalendar.FLD_CALENDAR_ID.getName(), 
        TblsEnvMonitConfig.ProgramCalendar.FLD_SCHEDULE_SIZE_UNIT.getName(), TblsEnvMonitConfig.ProgramCalendar.FLD_SCHEDULE_SIZE.getName(), 
        TblsEnvMonitConfig.ProgramCalendar.FLD_START_DATE.getName(), TblsEnvMonitConfig.ProgramCalendar.FLD_END_DATE.getName()};
        return Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.ProgramCalendarDate.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.ProgramCalendarDate.FLD_PROGRAM_ID.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_CALENDAR_ID.getName()}, 
                new Object[]{pName, programCalendarId}, 
                fieldsToRetrieve);
    }   

    /**
     *
     * @param holidaysCalendarCode
     * @param pName
     * @param programCalendarId
     * @return
     */
    @SuppressWarnings("empty-statement")
    public static Object[] importHolidaysCalendarSchedule(String pName, Integer programCalendarId, String holidaysCalendarCode) {                
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] existsRecord = Rdbms.existsRecord(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName(),  
              new String[]{TblsApp.HolidaysCalendar.FLD_CODE.getName(),TblsApp.HolidaysCalendar.FLD_ACTIVE.getName()}, 
              new Object[]{holidaysCalendarCode, true});
      if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsRecord[0].toString())){ return existsRecord;}     

      Object[][] holidaysCalendarDates = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE.getTableName(), 
              new String[]{TblsApp.HolidaysCalendarDate.CALENDAR_CODE.getName()}, 
              new Object[]{holidaysCalendarCode}, new String[]{TblsApp.HolidaysCalendarDate.ID.getName(), TblsApp.HolidaysCalendarDate.DATE.getName()});
      if (LPPlatform.LAB_FALSE.equalsIgnoreCase(holidaysCalendarDates[0][0].toString())){return LPArray.array2dTo1d(holidaysCalendarDates);}
      if (holidaysCalendarDates.length==0)
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPING_HOLIDAY_CALENDAR_EMPTY, new Object[]{holidaysCalendarCode});

      existsRecord = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.ProgramCalendar.TBL.getName(), 
              new String[]{TblsEnvMonitConfig.ProgramCalendar.FLD_PROGRAM_ID.getName(), TblsEnvMonitConfig.ProgramCalendar.FLD_CALENDAR_ID.getName()}, 
              new Object[]{pName, programCalendarId});
      if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsRecord[0].toString())){ return existsRecord;}
          Object[] newProjSchedRecursive = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.ProgramCalendarRecursiveEntries.TBL.getName(), 
          new String[]{TblsEnvMonitConfig.ProgramCalendarRecursiveEntries.FLD_PROGRAM_ID.getName(), TblsEnvMonitConfig.ProgramCalendar.FLD_CALENDAR_ID.getName(), TblsEnvMonitConfig.ProgramCalendarRecursiveEntries.FLD_RULE.getName(), TblsEnvMonitConfig.ProgramCalendarRecursiveEntries.FLD_IS_HOLIDAYS.getName()},
          new Object[]{pName, programCalendarId, holidaysCalendarCode, true});
      int projRecursiveId = Integer.parseInt(newProjSchedRecursive[newProjSchedRecursive.length-1].toString());
      StringBuilder datesStr =new StringBuilder();
      for (Object[] holidaysCalendarDate : holidaysCalendarDates) {
          SimpleDateFormat format1 = new SimpleDateFormat("yyyy MMM dd HH:mm:ss"); //yyyy-MM-dd
          String s;
          Date calDate = (Date) holidaysCalendarDate[1]; //String s = cal.getTime().toString();
          s = format1.format(calDate.getTime());            
          datesStr.append(s).append("|");
          Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.ProgramCalendarDate.TBL.getName(), 
                  new String[]{TblsEnvMonitConfig.ProgramCalendarDate.FLD_PROGRAM_ID.getName(), 
                    TblsEnvMonitConfig.ProgramCalendarDate.FLD_CALENDAR_ID.getName()
                    , TblsEnvMonitConfig.ProgramCalendarDate.FLD_RECURSIVE_ID.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_DATE.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_IS_HOLIDAYS.getName()},
                  new Object[]{pName, programCalendarId, projRecursiveId, calDate, true});
          Object[][] itemsSameDay = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.ProgramCalendarDate.TBL.getName(), 
                  new String[]{TblsEnvMonitConfig.ProgramCalendarDate.FLD_PROGRAM_ID.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_CALENDAR_ID.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_DATE.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_IS_HOLIDAYS.getName()},
                  new Object[]{pName, programCalendarId, calDate, false}, 
                  new String[]{TblsEnvMonitConfig.ProgramCalendarDate.FLD_ID.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_PROGRAM_ID.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_CALENDAR_ID.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_RECURSIVE_ID.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_DATE.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_IS_HOLIDAYS.getName()});
          if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(itemsSameDay[0][0].toString())){
              for (Object[] itemsSameDay1 : itemsSameDay) {
                  Long itemId = (Long) itemsSameDay1[0];
                  Object[] updateResult = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.ProgramCalendarDate.TBL.getName(),
                          new String[]{TblsEnvMonitConfig.ProgramCalendarDate.FLD_CONFLICT.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_CONFLICT_DETAIL.getName()}, new Object[]{true, ConflictDetail.DAY_CONVERTED_ON_HOLIDAYS.getDescription()},
                          new String[]{TblsEnvMonitConfig.ProgramCalendarDate.FLD_ID.getName()}, new Object[]{itemId.intValue()});
                  if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateResult[0].toString())){return updateResult;}                    
              }
          }                        
      }
      return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, HOLIDAY_CALENDAR_ADDED, new Object[]{datesStr});
    }
 
    /**
     *
     * @param pName
     * @param programCalendarId
     * @param locationName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public static Object[] addRecursiveScheduleForLocation(String pName, Integer programCalendarId, String locationName, String[] fieldName, Object[] fieldValue){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

      Object[] existsRecord = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.ProgramCalendar.TBL.getName(),  
              new String[]{TblsEnvMonitConfig.ProgramCalendar.FLD_PROGRAM_ID.getName(), TblsEnvMonitConfig.ProgramCalendar.FLD_CALENDAR_ID.getName()}, new Object[]{pName, programCalendarId});
      if (LPPlatform.LAB_FALSE.equals(existsRecord[0].toString())){ return existsRecord;}

      Calendar startDate = null; 
      Calendar endDate = null;

      if (LPArray.valueInArray(fieldName, TblsEnvMonitConfig.ProgramCalendar.FLD_START_DATE.getName())){
          startDate = (Calendar) fieldValue[LPArray.valuePosicInArray(fieldName, TblsEnvMonitConfig.ProgramCalendar.FLD_START_DATE.getName())];
      }
      if (LPArray.valueInArray(fieldName, TblsEnvMonitConfig.ProgramCalendar.FLD_END_DATE.getName())){
          endDate = (Calendar) fieldValue[LPArray.valuePosicInArray(fieldName, TblsEnvMonitConfig.ProgramCalendar.FLD_END_DATE.getName())];
      }      
      Object[][] projectInfo = new Object[0][0];
      if (startDate==null || endDate==null){
          projectInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.ProgramCalendar.TBL.getName(), 
                  new String[]{TblsEnvMonitConfig.ProgramCalendar.FLD_PROGRAM_ID.getName(), TblsEnvMonitConfig.ProgramCalendar.FLD_CALENDAR_ID.getName()}, new Object[]{pName, programCalendarId}, new String[]{TblsEnvMonitConfig.ProgramCalendar.FLD_PROGRAM_ID.getName(), 
                    TblsEnvMonitConfig.ProgramCalendar.FLD_START_DATE.getName(), TblsEnvMonitConfig.ProgramCalendar.FLD_END_DATE.getName()});
          if (startDate==null){
              Date currDate = (Date) projectInfo[0][1]; 
              if (currDate!=null){
                  startDate = Calendar.getInstance();
                  startDate.setTime(currDate);
              }else{
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPING_START_DATE_CANNOT_BE_NULL, new Object[]{});
              }
          }
          if (endDate==null){
              Date currDate = (Date) projectInfo[0][2]; 
              if (currDate!=null){
                  endDate = Calendar.getInstance();
                  endDate.setTime(currDate);                
              }else{
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPING_END_DATE_CANNOT_BE_NULL, new Object[]{});
              }                
          }            
      }
      String daysOfWeek ="";
      if (LPArray.valueInArray(fieldName, TblsEnvMonitConfig.ProgramCalendar.FLD_DAY_OF_WEEK.getName())){
          daysOfWeek = (String) fieldValue[LPArray.valuePosicInArray(fieldName, TblsEnvMonitConfig.ProgramCalendar.FLD_DAY_OF_WEEK.getName())];
          //if ( daysOfWeek!=null){daysOfWeekArr = (String[]) daysOfWeek.split("\\*");}
      }
      StringBuilder datesStr = new StringBuilder();
      Object[] daysInRange = LPDate.getDaysInRange(startDate, endDate, daysOfWeek);  
      if (daysInRange.length==0){
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPING_NO_DAYS_IN_RANGE, new Object[]{daysOfWeek, startDate, endDate});
      }
      Object[] newProjSchedRecursive = null;
      newProjSchedRecursive = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.ProgramCalendarRecursiveEntries.TBL.getName(), 
              new String[]{TblsEnvMonitConfig.ProgramCalendarRecursiveEntries.FLD_PROGRAM_ID.getName(), TblsEnvMonitConfig.ProgramCalendarRecursiveEntries.FLD_CALENDAR_ID.getName(), TblsEnvMonitConfig.ProgramCalendarRecursiveEntries.FLD_RULE.getName(), TblsEnvMonitConfig.ProgramCalendarRecursiveEntries.FLD_START_DATE.getName(), TblsEnvMonitConfig.ProgramCalendarRecursiveEntries.FLD_END_DATE.getName()},
              new Object[]{pName, programCalendarId, daysOfWeek, (Date) projectInfo[0][1], (Date) projectInfo[0][2]});
      int projRecursiveId = Integer.parseInt(newProjSchedRecursive[newProjSchedRecursive.length-1].toString());
      for (Object daysInRange1 : daysInRange) {
          SimpleDateFormat format1 = new SimpleDateFormat("yyyy MMM dd HH:mm:ss"); //yyyy-MM-dd
          String s;
          Date cale = (Date) daysInRange1; //String s = cal.getTime().toString();
          s = format1.format(cale.getTime());            
          datesStr.append(s).append("|");
          Object[] isHolidays = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.ProgramCalendarDate.TBL.getName(), 
                  new String[]{TblsEnvMonitConfig.ProgramCalendarDate.FLD_PROGRAM_ID.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_CALENDAR_ID.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_DATE.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_IS_HOLIDAYS.getName()}, 
                  new Object[]{pName, programCalendarId, daysInRange1, true});             
          String[] fieldNames = new String[]{TblsEnvMonitConfig.ProgramCalendarDate.FLD_PROGRAM_ID.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_CALENDAR_ID.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_RECURSIVE_ID.getName(), TblsEnvMonitConfig.ProgramCalendarDate.FLD_DATE.getName()};
          Object[] fieldValues = new Object[]{pName, programCalendarId, projRecursiveId, daysInRange1};
          fieldNames=LPArray.addValueToArray1D(fieldNames, TblsEnvMonitConfig.ProgramCalendarDate.FLD_LOCATION_NAME.getName());
          fieldValues=LPArray.addValueToArray1D(fieldValues, locationName);
          
          if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isHolidays[0].toString())){
              fieldNames=LPArray.addValueToArray1D(fieldNames, TblsEnvMonitConfig.ProgramCalendarDate.FLD_CONFLICT.getName());
              fieldNames=LPArray.addValueToArray1D(fieldNames, TblsEnvMonitConfig.ProgramCalendarDate.FLD_CONFLICT_DETAIL.getName());
              fieldValues=LPArray.addValueToArray1D(fieldValues, true);
              fieldValues=LPArray.addValueToArray1D(fieldValues, ConflictDetail.DAY_IS_MARKED_AS_HOLIDAYS.getDescription());
          }         
          Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.ProgramCalendarDate.TBL.getName(), 
            fieldNames, fieldValues);            
      }
      return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, HOLIDAY_CALENDAR_ADDED, new Object[]{datesStr});
    }
           

}
