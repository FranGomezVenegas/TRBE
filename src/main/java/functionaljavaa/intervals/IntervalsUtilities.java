/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.intervals;

import databases.Rdbms;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import static lbplanet.utilities.LPDate.SecondsInDateRange;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class IntervalsUtilities {
    
    public static String DBFIELDNAME_EXPIRY_INTERVAL_INFO="expiry_interval_info";        
    
    
    public static Object[] applyExpiryInterval(String objectWithIntervalTableName, String[] whereFieldNames, Object[] whereFieldValues){        
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] fieldsToRetrieve=new String[]{DBFIELDNAME_EXPIRY_INTERVAL_INFO};
        Object[][] intervalInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), objectWithIntervalTableName, 
                whereFieldNames, whereFieldValues,
                fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(intervalInfo[0][0].toString())) return intervalInfo;
        Object[] intervalChecker = intervalChecker(intervalInfo[0][0].toString());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(intervalChecker[0].toString())) return intervalInfo;
        String[] intervalInfoArr=intervalInfo[0][0].toString().split("\\|");
        Date addIntervalToGivenDate = LPDate.addIntervalToGivenDate(LPDate.getCurrentDateWithNoTime(), intervalInfoArr[0], Integer.parseInt(intervalInfoArr[1]));        
        if (addIntervalToGivenDate==null)
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "IntervalNotPossible <*1*>", new Object[]{intervalInfo[0][0].toString()});
        return new Object[]{LPPlatform.LAB_TRUE, addIntervalToGivenDate};
    }
    
    private static Object[] intervalChecker(String val){
        if (val==null) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "formatIsNull", null);
        String[] valArr=val.split("\\|");
        if (valArr.length!=2) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "wrongFormat", null);
        LPDate.IntervalTypes iTypes=null;
        try{
            iTypes = LPDate.IntervalTypes.valueOf(valArr[0].toUpperCase());
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "", null);
        }catch(Exception e){
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "wrongIntervalType <*1*>, Accepted values: <*2*>", 
                new Object[]{LPPlatform.LAB_FALSE,valArr[0], Arrays.toString(LPDate.IntervalTypes.values())});
        }        
    }
    public static Object[] isTheIntervalIntoTheDatesRange(long interval, LocalDateTime startDate, LocalDateTime endDate){
        if (interval<=0) return LPArray.addValueToArray1D(LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "intervalValueIsNegativeOrZero", null), -1);
        long seconds = SecondsInDateRange(startDate, endDate);               
        int compareTo = Integer.valueOf((int) seconds).compareTo((int) interval);
        //if (secondsInDateRange.interval);
        if (compareTo==1)
            return LPArray.addValueToArray1D(LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "not", null), seconds);
        else
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "yes", null);
    }
}
