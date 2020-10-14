/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class LPDate {
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
    
    public static Date StringFormatToDate(String dateStr){ 
        if (!dateStr.toUpperCase().contains("T")) dateStr=dateStr+"T00:00:00";
        Calendar cal = Calendar.getInstance();    
        int y=LocalDateTime.parse(dateStr).getYear();
        int m=LocalDateTime.parse(dateStr).getMonthValue();
        int d=LocalDateTime.parse(dateStr).getDayOfMonth();
        cal.set(y, 
                m-1,
                d);
        return cal.getTime();
    }

}
