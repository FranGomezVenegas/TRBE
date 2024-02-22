/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.app.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsApp;
import databases.features.Token;
import platform.app.definition.HolidaysCalendarEnums.CalendarAPIactionsEndpoints;
import platform.app.definition.HolidaysCalendarEnums.CalendarErrorTrapping;
import java.sql.Date;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.session.ResponseMessages;

public class HolidaysCalendar {

    private HolidaysCalendar() {
        throw new IllegalStateException("Utility class");
    }

    public static InternalMessage createNewCalendar(String name, String[] fldNames, Object[] fldValues) {
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        if (fldNames == null) {
            fldNames = new String[]{};
            fldValues = new Object[]{};
        }
        Object[] existsRecord = Rdbms.existsRecord("", GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName(),
                new String[]{TblsApp.HolidaysCalendar.CODE.getName()},
                new Object[]{name});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())) {
            messages.addMainForError(CalendarErrorTrapping.CALENDAR_ALREADY_EXISTS, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, CalendarErrorTrapping.CALENDAR_ALREADY_EXISTS, new Object[]{name}, name);
        }
        fldNames = LPArray.addValueToArray1D(fldNames, new String[]{TblsApp.HolidaysCalendar.CODE.getName(), TblsApp.HolidaysCalendar.ACTIVE.getName(),
            TblsApp.HolidaysCalendar.CREATED_ON.getName(), TblsApp.HolidaysCalendar.CREATED_BY.getName()});
        fldValues = LPArray.addValueToArray1D(fldValues, new Object[]{name, true, LPDate.getCurrentTimeStamp(), token.getPersonName()});
        RdbmsObject insertDiagn = Rdbms.insertRecordInTable(TblsApp.TablesApp.HOLIDAYS_CALENDAR, fldNames, fldValues);
        if (Boolean.FALSE.equals(insertDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), new Object[]{name}, null);
        }

        messages.addMainForSuccess(CalendarAPIactionsEndpoints.NEW_CALENDAR, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, CalendarAPIactionsEndpoints.NEW_CALENDAR, new Object[]{name}, name);
    }

    public static InternalMessage addDateToCalendar(String code, Date newDate, String dayName, String[] fldNames, Object[] fldValues) {
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        if (fldNames == null) {
            fldNames = new String[]{};
            fldValues = new Object[]{};
        }
        Object[] existsRecord = Rdbms.existsRecord("", GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName(),
                new String[]{TblsApp.HolidaysCalendar.CODE.getName()},
                new Object[]{code});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsRecord[0].toString())) {
            messages.addMainForError(CalendarErrorTrapping.CALENDAR_NOT_EXISTS, new Object[]{code});
            return new InternalMessage(LPPlatform.LAB_FALSE, CalendarErrorTrapping.CALENDAR_NOT_EXISTS, new Object[]{code}, code);
        }
        fldNames = LPArray.addValueToArray1D(fldNames, new String[]{TblsApp.HolidaysCalendarDate.CALENDAR_CODE.getName(), TblsApp.HolidaysCalendarDate.DATE.getName(),
            TblsApp.HolidaysCalendarDate.DAY_NAME.getName(),
            TblsApp.HolidaysCalendarDate.CREATED_ON.getName(), TblsApp.HolidaysCalendarDate.CREATED_BY.getName()});
        fldValues = LPArray.addValueToArray1D(fldValues, new Object[]{code, newDate, dayName,
            LPDate.getCurrentTimeStamp(), token.getPersonName()});
        RdbmsObject insertDiagn = Rdbms.insertRecordInTable(TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE, fldNames, fldValues);
        if (Boolean.FALSE.equals(insertDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), new Object[]{code, newDate}, null);
        }
        messages.addMainForSuccess(CalendarAPIactionsEndpoints.ADD_DATE_TO_CALENDAR, new Object[]{newDate, code});
        return new InternalMessage(LPPlatform.LAB_TRUE, CalendarAPIactionsEndpoints.ADD_DATE_TO_CALENDAR, new Object[]{newDate, code}, newDate);
    }

    public static InternalMessage deleteCalendarDate(String code, Integer dateId) {
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Object[] existsRecord = Rdbms.existsRecord("", GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE.getTableName(),
                new String[]{TblsApp.HolidaysCalendarDate.CALENDAR_CODE.getName(), TblsApp.HolidaysCalendarDate.ID.getName()},
                new Object[]{code, dateId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsRecord[0].toString())) {
            messages.addMainForError(CalendarErrorTrapping.CALENDAR_DATE_NOT_EXISTS, new Object[]{dateId, code});
            return new InternalMessage(LPPlatform.LAB_FALSE, CalendarErrorTrapping.CALENDAR_DATE_NOT_EXISTS, new Object[]{dateId, code}, code);
        }
        SqlWhere where = new SqlWhere();
        where.addConstraint(TblsApp.HolidaysCalendarDate.CALENDAR_CODE, null, new Object[]{code}, null);
        where.addConstraint(TblsApp.HolidaysCalendarDate.ID, null, new Object[]{dateId}, null);
        RdbmsObject removeRecordInTable = Rdbms.removeRecordInTable(TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE, where, null);
        if (Boolean.FALSE.equals(removeRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, removeRecordInTable.getErrorMessageCode(), removeRecordInTable.getErrorMessageVariables(), null);
        }
        messages.addMainForSuccess(CalendarAPIactionsEndpoints.DELETE_DATE_FROM_GIVEN_CALENDAR, new Object[]{dateId, code});
        return new InternalMessage(LPPlatform.LAB_TRUE, CalendarAPIactionsEndpoints.DELETE_DATE_FROM_GIVEN_CALENDAR, new Object[]{dateId, code}, dateId);
    }

    public static InternalMessage calendarChangeActiveFlag(String code, Boolean fldValue, CalendarAPIactionsEndpoints endP) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Object[][] calendarInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName(),
                new String[]{TblsApp.HolidaysCalendar.CODE.getName()},
                new Object[]{code}, new String[]{TblsApp.HolidaysCalendar.CODE.getName(), TblsApp.HolidaysCalendar.ACTIVE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(calendarInfo[0][0].toString())) {
            messages.addMainForError(CalendarErrorTrapping.CALENDAR_NOT_EXISTS, new Object[]{code});
            return new InternalMessage(LPPlatform.LAB_FALSE, CalendarErrorTrapping.CALENDAR_NOT_EXISTS, new Object[]{code}, code);
        }
        if (Boolean.valueOf(LPNulls.replaceNull(calendarInfo[0][1]).toString()).equals(fldValue)) {
            if (Boolean.TRUE.equals(fldValue)) {
                return new InternalMessage(LPPlatform.LAB_FALSE, CalendarErrorTrapping.CALENDAR_ALREADY_ACTIVE, new Object[]{code}, code);
            } else {
                return new InternalMessage(LPPlatform.LAB_FALSE, CalendarErrorTrapping.CALENDAR_ALREADY_INACTIVE, new Object[]{code}, code);
            }
        }
        SqlWhere sW = new SqlWhere();
        sW.addConstraint(TblsApp.HolidaysCalendar.CODE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{code}, null);
        EnumIntTableFields[] fldNamesObj = null;
        Object[] fldValues = null;
        if (Boolean.FALSE.equals(fldValue)) {
            fldNamesObj = new EnumIntTableFields[]{TblsApp.HolidaysCalendar.ACTIVE, TblsApp.HolidaysCalendar.DEACTIVATED_BY, TblsApp.HolidaysCalendar.DEACTIVATED_ON};
            fldValues = new Object[]{fldValue, instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        } else {
            fldNamesObj = new EnumIntTableFields[]{TblsApp.HolidaysCalendar.ACTIVE};
            fldValues = new Object[]{fldValue};
        }
        RdbmsObject updDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsApp.TablesApp.HOLIDAYS_CALENDAR, fldNamesObj, fldValues,
                sW, null);
        if (Boolean.FALSE.equals(updDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updDiagn.getErrorMessageCode(), new Object[]{code}, null);
        }
        messages.addMainForSuccess(endP, new Object[]{code});
        return new InternalMessage(LPPlatform.LAB_TRUE, endP, new Object[]{code}, null);
    }

}
