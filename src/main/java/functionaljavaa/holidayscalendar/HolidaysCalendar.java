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
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        if (fldNames==null){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsApp.HolidaysCalendar.FLD_CODE.getName(), TblsApp.HolidaysCalendar.FLD_ACTIVE.getName(),
            TblsApp.HolidaysCalendar.FLD_CREATED_ON.getName(), TblsApp.HolidaysCalendar.FLD_CREATED_BY.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{name, true, LPDate.getCurrentTimeStamp(), token.getPersonName()});
        Object[] instCreationDiagn = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.HolidaysCalendar.TBL.getName(), 
                fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        //instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.CREATION.toString(), name, TblsApp.HolidaysCalendar.TBL.getName(), name,
        //                fldNames, fldValues);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        messages.addMainForSuccess("configInstruments", CalendarAPIactionsEndpoints.NEW_CALENDAR.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, CalendarAPIactionsEndpoints.NEW_CALENDAR.getSuccessMessageCode(), new Object[]{name}, name);
    }

public static InternalMessage addDateToCalendar(String code, Date newDate, String dayName, String[] fldNames, Object[] fldValues){   
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        if (fldNames==null){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsApp.HolidaysCalendarDate.FLD_CALENDAR_CODE.getName(), TblsApp.HolidaysCalendarDate.FLD_DATE.getName(),
            TblsApp.HolidaysCalendarDate.FLD_DAY_NAME.getName(),
            TblsApp.HolidaysCalendarDate.FLD_CREATED_ON.getName(), TblsApp.HolidaysCalendarDate.FLD_CREATED_BY.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{code, newDate, dayName, 
            LPDate.getCurrentTimeStamp(), token.getPersonName()});
        Object[] instCreationDiagn = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.HolidaysCalendarDate.TBL.getName(), 
                fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{code, newDate}, null);
        //instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.CREATION.toString(), name, TblsApp.HolidaysCalendar.TBL.getName(), name,
        //                fldNames, fldValues);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        messages.addMainForSuccess("configInstruments", CalendarAPIactionsEndpoints.NEW_CALENDAR.getSuccessMessageCode(), new Object[]{code, newDate});
        return new InternalMessage(LPPlatform.LAB_TRUE, CalendarAPIactionsEndpoints.NEW_CALENDAR.getSuccessMessageCode(), new Object[]{code, newDate}, newDate);
    }


}
