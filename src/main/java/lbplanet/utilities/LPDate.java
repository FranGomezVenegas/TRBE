/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import functionaljavaa.investigation.Investigation.InvestigationErrorTrapping;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import lbplanet.utilities.LPPlatform.LpPlatformSuccess;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
/**
 *
 * @author Administrator
 */
public class LPDate {
    public enum IntervalTypes{DAYS, MONTHS, YEARS}

    private LPDate(){    throw new IllegalStateException("Utility class");}    
/**
 * Add some days to this given date
 * @param date Date - The date to add the days
 * @param days int - The number of days to be added
 * @return Date - Returns the new date in a Date format
 */
    
    
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

/**
 * Add some Months(simply considering all months of 30 days) to this given date
 * @param date Date - The date to add the days
 * @param months int - The number of months to be added
 * @return Date - Returns the new date in a Date format
 */    
    public static Date addMonths(Date date, int months)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, months*30); //minus number would decrement the days
        return cal.getTime();
    }    

/**
 * Add some Years(simply considering all years of 365 days) to this given date
 * @param date Date - The date to add the days
 * @param years int - The number of years to be added
 * @return Date - Returns the new date in a Date format
 */      
    public static Date addYears(Date date, int years)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, years*365); //minus number would decrement the days
        return cal.getTime();
    } 
    public static Object[] getDaysInRangeByIntervals(Calendar startDate, Calendar endDate, String dayOfWeek, String inverval, Integer numIterations){
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
    }
    
    /**
     *
     * @param startDate
     * @param endDate
     * @param dayOfWeek
     * @return
     */
    public static Object[] getDaysInRange(Calendar startDate, Calendar endDate, String dayOfWeek){
        Object[] dt = new Object[0];
        
        int dayOfWeekNum=-1;        
        switch (dayOfWeek.toUpperCase()){
            case "SUNDAYS": dayOfWeekNum=0; break;
            case "MONDAYS": dayOfWeekNum=1; break;
            case "TUESDAYS":dayOfWeekNum=2;break;
            case "WEDNESDAYS":dayOfWeekNum=3;break;
            case "THURSDAYS":dayOfWeekNum=4;break;
            case "FRIDAYS":dayOfWeekNum=5;break;
            case "SATURDAYS":dayOfWeekNum=6;break;
            default:
                return new Object[0];
        }
        int startDateDayOfWeek = startDate.get(Calendar.DAY_OF_WEEK); 
        
        int daysToAdd=0;
        if (startDateDayOfWeek < dayOfWeekNum){
            daysToAdd = dayOfWeekNum-startDateDayOfWeek+1;    
        }else{
            daysToAdd = dayOfWeekNum-startDateDayOfWeek+1+7;            
        }  
        startDate.add(Calendar.DAY_OF_MONTH, daysToAdd);
        
        while (startDate.compareTo(endDate)<=0){
            dt = LPArray.addValueToArray1D(dt, startDate.getTime());
            startDate.add(Calendar.DAY_OF_MONTH, 7);
        }
        return  dt;                       
    }
    
    /**
     *
     * @return
     */
    public static Date getTimeStampLocalDate(){    
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    /**
     *
     * @return
     */
    public static LocalDate getDateTimeLocalDate(){
        LocalDate date = LocalDate.now();
        String text = date.format(ISO_LOCAL_DATE_TIME);
        return LocalDate.parse(text, ISO_LOCAL_DATE_TIME);
    }

    public static Date getCurrentDateWithNoTime(){
        return new Date();
    }

    /**
     *
     * @return
     */
    public static LocalDateTime getCurrentTimeStamp() {
        return LocalDateTime.now();
    }    
    
    public static LocalDateTime dateStringFormatToLocalDateTime(String dateStr){
        try{
            return LocalDateTime.parse(dateStr);
        }catch(DateTimeParseException e){
            try{
                return LocalDateTime.parse(dateStr+"T00:00:00");
            }catch (DateTimeParseException r){
                return null;
            }
        }
    }
    
    public static Date stringFormatToDate(String dateStr){ 
        if (Boolean.FALSE.equals(dateStr.toUpperCase().contains("T"))) dateStr=dateStr+"T00:00:00";
        Calendar cal = Calendar.getInstance();    
        Integer tposic=dateStr.indexOf(":");
        tposic=tposic-3;
        char tposicValue=dateStr.charAt(tposic);
        
        if (" ".equalsIgnoreCase(String.valueOf(tposicValue))){
            String dateStr1=dateStr.substring(0, tposic)+"T";
            String dateStr2=dateStr.substring(tposic+1, dateStr.length()-tposic);        
            dateStr=dateStr1+dateStr2;
        }
        int y=LocalDateTime.parse(dateStr).getYear();
        int m=LocalDateTime.parse(dateStr).getMonthValue();
        int d=LocalDateTime.parse(dateStr).getDayOfMonth();
        cal.set(y, 
                m-1,
                d);
        return cal.getTime();
    }
    
    public static LocalDateTime stringFormatToLocalDateTime(String dateStr){ 
        try{                            
        Integer tposic=dateStr.indexOf(":");
        if (tposic==-1){
            dateStr=dateStr+"T00:00:00";
            tposic=dateStr.indexOf(":");
        }
        tposic=tposic-3;
        char tposicaVlue=dateStr.charAt(tposic);
        
        if (" ".equalsIgnoreCase(String.valueOf(tposicaVlue))){
            String dateStr1=dateStr.substring(0, tposic)+"T";
            String dateStr2=dateStr.substring(tposic+1);        
            dateStr=dateStr1+dateStr2;
        }            
        if (dateStr.length()>0) 
            return LocalDateTime.parse(dateStr, ISO_LOCAL_DATE_TIME);
        return null;
        }catch(DateTimeParseException e){
            return null;
        }
    }
    public static InternalMessage isIntervalTypeOneRecognized(String itemsMeasurement){    
            IntervalTypes iTypes=null;
        try{
            iTypes = IntervalTypes.valueOf(itemsMeasurement.toUpperCase());
            return new InternalMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.ALL_FINE, null);
        }catch(Exception e){
            return new InternalMessage(LPPlatform.LAB_FALSE, InvestigationErrorTrapping.OBJECT_NOT_RECOGNIZED, new Object[]{itemsMeasurement, IntervalTypes.values().toString()});
        }
    }
    public static Boolean isDateBiggerThanTimeStamp(Calendar dateToCompare){
        return (Calendar.getInstance().before(dateToCompare));        
    }
    public static Date addIntervalToGivenDate(Date startDay, String itemsMeasurement, int scheduleSize){    
        InternalMessage intervalTypeOneRecognized = isIntervalTypeOneRecognized(itemsMeasurement.toUpperCase());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(intervalTypeOneRecognized.getDiagnostic()))
            return null;
        IntervalTypes iTypes = IntervalTypes.valueOf(itemsMeasurement.toUpperCase());
        switch (iTypes){
            case DAYS:
                return LPDate.addDays(startDay, scheduleSize);
            case MONTHS:
                return LPDate.addMonths(startDay, scheduleSize);
            case YEARS:
                return LPDate.addYears(startDay, scheduleSize);
            default:                
                return null;
        }
    }
    public static long secondsInDateRange(LocalDateTime startDate, LocalDateTime endDate){
        Duration duration = Duration.between(startDate, endDate);
        return duration.getSeconds();
    }
    public static BigDecimal secondsInDateRange(LocalDateTime startDate, LocalDateTime endDate, Boolean includeMilis){
        BigDecimal durSecs = new BigDecimal(Duration.between(startDate, endDate).getSeconds());
        if (Boolean.FALSE.equals(includeMilis)) return durSecs;
        BigDecimal durMillis=new BigDecimal(Duration.between(startDate, endDate).getNano());
        durMillis=durMillis.divide(new BigDecimal(1000000));
        durMillis=durMillis.divide(new BigDecimal(1000));
        durMillis=durMillis.add(durSecs);
        return durMillis;
                
    }
    public static Date resetTimeToZero(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Set hours, minutes, and seconds to zero
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }    
}
