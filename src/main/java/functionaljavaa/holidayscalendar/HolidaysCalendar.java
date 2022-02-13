/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.holidayscalendar;

import databases.Rdbms;
import databases.TblsApp;
import databases.Token;
import functionaljavaa.holidayscalendar.HolidaysCalendarEnums.CalendarAPIactionsEndpoints;
import functionaljavaa.holidayscalendar.HolidaysCalendarEnums.CalendarErrorTrapping;
import java.sql.Date;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.session.ResponseMessages;

public class HolidaysCalendar {
public static InternalMessage createNewCalendar(String name, String[] fldNames, Object[] fldValues){   
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        if (fldNames==null){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }
        Object[] existsRecord = Rdbms.existsRecord(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName(), 
            new String[]{TblsApp.HolidaysCalendar.FLD_CODE.getName()},
            new Object[]{name});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())){
            messages.addMainForError(CalendarErrorTrapping.CALENDAR_ALREADY_EXISTS.getErrorCode(), new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, CalendarErrorTrapping.CALENDAR_ALREADY_EXISTS.getErrorCode(), new Object[]{name}, name);    
        }                
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsApp.HolidaysCalendar.FLD_CODE.getName(), TblsApp.HolidaysCalendar.FLD_ACTIVE.getName(),
            TblsApp.HolidaysCalendar.FLD_CREATED_ON.getName(), TblsApp.HolidaysCalendar.FLD_CREATED_BY.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{name, true, LPDate.getCurrentTimeStamp(), token.getPersonName()});
        Object[] instCreationDiagn = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName(), 
                fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        //instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.CREATION.toString(), name, TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName(), name,
        //                fldNames, fldValues);
        messages.addMainForSuccess("HolidaysCalendar", CalendarAPIactionsEndpoints.NEW_CALENDAR.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, CalendarAPIactionsEndpoints.NEW_CALENDAR.getSuccessMessageCode(), new Object[]{name}, name);
    }

public static InternalMessage addDateToCalendar(String code, Date newDate, String dayName, String[] fldNames, Object[] fldValues){   
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        if (fldNames==null){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }
        Object[] existsRecord = Rdbms.existsRecord(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE.getTableName(), 
            new String[]{TblsApp.HolidaysCalendarDate.FLD_CALENDAR_CODE.getName(), TblsApp.HolidaysCalendarDate.FLD_DATE.getName()},
            new Object[]{code, newDate});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsRecord[0].toString())){
            messages.addMainForError(CalendarErrorTrapping.CALENDAR_NOT_EXISTS.getErrorCode(), new Object[]{code});
            return new InternalMessage(LPPlatform.LAB_FALSE, CalendarErrorTrapping.CALENDAR_NOT_EXISTS.getErrorCode(), new Object[]{code}, code);    
        }
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsApp.HolidaysCalendarDate.FLD_CALENDAR_CODE.getName(), TblsApp.HolidaysCalendarDate.FLD_DATE.getName(),
            TblsApp.HolidaysCalendarDate.FLD_DAY_NAME.getName(),
            TblsApp.HolidaysCalendarDate.FLD_CREATED_ON.getName(), TblsApp.HolidaysCalendarDate.FLD_CREATED_BY.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{code, newDate, dayName, 
            LPDate.getCurrentTimeStamp(), token.getPersonName()});
        Object[] calAddDateCreationDiagn = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE.getTableName(), 
                fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(calAddDateCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, calAddDateCreationDiagn[calAddDateCreationDiagn.length-1].toString(), new Object[]{code, newDate}, null);
        //instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.CREATION.toString(), name, TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName(), name,
        //                fldNames, fldValues);
        messages.addMainForSuccess("HolidaysCalendar", CalendarAPIactionsEndpoints.ADD_DATE_TO_CALENDAR.getSuccessMessageCode(), new Object[]{newDate, code});
        return new InternalMessage(LPPlatform.LAB_TRUE, CalendarAPIactionsEndpoints.ADD_DATE_TO_CALENDAR.getSuccessMessageCode(), new Object[]{newDate, code}, newDate);
    }

public static InternalMessage deleteCalendarDate(String code, Integer dateId){   
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        Object[] existsRecord = Rdbms.existsRecord(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE.getTableName(), 
            new String[]{TblsApp.HolidaysCalendarDate.FLD_CALENDAR_CODE.getName(), TblsApp.HolidaysCalendarDate.FLD_ID.getName()},
            new Object[]{code, dateId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsRecord[0].toString())){
            messages.addMainForError(CalendarErrorTrapping.CALENDAR_DATE_NOT_EXISTS.getErrorCode(), new Object[]{code, dateId});
            return new InternalMessage(LPPlatform.LAB_FALSE, CalendarErrorTrapping.CALENDAR_DATE_NOT_EXISTS.getErrorCode(), new Object[]{code, dateId}, code);    
        }
        Object[] removeRecordInTable = Rdbms.removeRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE.getTableName(), 
            new String[]{TblsApp.HolidaysCalendarDate.FLD_CALENDAR_CODE.getName(), TblsApp.HolidaysCalendarDate.FLD_ID.getName()},
            new Object[]{code, dateId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(removeRecordInTable[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, removeRecordInTable[removeRecordInTable.length-1].toString(), new Object[]{code, dateId}, null);
        //instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.CREATION.toString(), name, TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName(), name,
        //                fldNames, fldValues);
        messages.addMainForSuccess("HolidaysCalendar", CalendarAPIactionsEndpoints.DELETE_DATE_FROM_GIVEN_CALENDAR.getSuccessMessageCode(), new Object[]{dateId, code});
        return new InternalMessage(LPPlatform.LAB_TRUE, CalendarAPIactionsEndpoints.DELETE_DATE_FROM_GIVEN_CALENDAR.getSuccessMessageCode(), new Object[]{dateId, code}, dateId);
    }

}
