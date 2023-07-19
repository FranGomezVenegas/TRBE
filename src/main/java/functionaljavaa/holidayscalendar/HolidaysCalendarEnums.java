/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.holidayscalendar;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_NEW_DATE;
import databases.TblsApp;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class HolidaysCalendarEnums {

    public enum CalendarEvents {
        NEW_CALENDAR, CLONE_CALENDAR, REMOVE_CALENDAR, UPDATE_CALENDAR_PROPERTIES, ADD_DATE_TO_CALENDAR, ADD_DATE_FROM_CALENDAR
    }

    public enum CalendarAPIactionsEndpoints implements EnumIntEndpoints {
        NEW_CALENDAR("NEW_CALENDAR", "name", "", "calendarNewCalendarCreated_success",
                new LPAPIArguments[]{new LPAPIArguments("name", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName()).build()).build(),
                null, null),
        ADD_DATE_TO_CALENDAR("ADD_DATE_TO_CALENDAR", "name", "", "calendarDateAdded_success",
                new LPAPIArguments[]{new LPAPIArguments("name", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_NEW_DATE, LPAPIArguments.ArgumentType.DATE.toString(), true, 7),
                    new LPAPIArguments("dayName", LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName()).build()).build(),
                null, null),
        DELETE_DATE_FROM_GIVEN_CALENDAR("DELETE_DATE_FROM_GIVEN_CALENDAR", "", "", "calendarDateDeleted_success",
                new LPAPIArguments[]{new LPAPIArguments("calendar", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("date_id", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE.getTableName()).build()).build(),
                null, null),
        DEACTIVATE_CALENDAR("DEACTIVATE_CALENDAR", "name", "", "calendarDeactivatedCalendarCreated_success",
                new LPAPIArguments[]{new LPAPIArguments("name", LPAPIArguments.ArgumentType.STRING.toString(), true, 6)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName()).build()).build(),
                 null, null),        
        REACTIVATE_CALENDAR("REACTIVATE_CALENDAR", "name", "", "calendarReactivatedCalendarCreated_success",
                new LPAPIArguments[]{new LPAPIArguments("name", LPAPIArguments.ArgumentType.STRING.toString(), true, 6)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName()).build()).build(),
                 null, null),        
    ;
    private CalendarAPIactionsEndpoints(String name, String mandatoryParams, String optionalParams, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
        this.name = name;
        this.mandatoryParams = mandatoryParams;
        this.optionalParams = optionalParams;
        this.successMessageCode = successMessageCode;
        this.arguments = argums;
        this.outputObjectTypes = outputObjectTypes;
        this.devComment = LPNulls.replaceNull(devComment);
        this.devCommentTag = LPNulls.replaceNull(devCommentTag);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getMandatoryParams() {
        return this.mandatoryParams;
    }

    @Override
    public String getSuccessMessageCode() {
        return this.successMessageCode;
    }

    @Override
    public JsonArray getOutputObjectTypes() {
        return outputObjectTypes;
    }

    @Override
    public String getApiUrl() {
        return GlobalVariables.ApiUrls.APP_CALENDAR_ACTIONS.getUrl();
    }

    public HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex) {
        HashMap<HttpServletRequest, Object[]> hm = new HashMap<>();
        Object[] argValues = new Object[0];
        for (LPAPIArguments curArg : this.arguments) {
            argValues = LPArray.addValueToArray1D(argValues, curArg.getName() + ":" + getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
        }
        hm.put(request, argValues);
        return hm;
    }

    /**
     * @return the arguments
     */
    @Override
    public LPAPIArguments[] getArguments() {
        return arguments;
    }
    private final String name;
    private final String mandatoryParams;
    private final String optionalParams;
    private final String successMessageCode;
    private final LPAPIArguments[] arguments;
    private final JsonArray outputObjectTypes;

    @Override
    public String getDeveloperComment() {
        return this.devComment;
    }

    @Override
    public String getDeveloperCommentTag() {
        return this.devCommentTag;
    }
    private final String devComment;
    private final String devCommentTag;
}
public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME + "|" + GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN + "|" + GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

public enum CalendarAPIqueriesEndpoints implements EnumIntEndpoints {
    GET_ALL_HOLIDAY_DATES_LIST_ALL_CALENDARS("GET_ALL_HOLIDAY_DATES_LIST_ALL_CALENDARS", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects,
            null, null),;

    private CalendarAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
        this.name = name;
        this.successMessageCode = successMessageCode;
        this.arguments = argums;
        this.outputObjectTypes = outputObjectTypes;
        this.devComment = LPNulls.replaceNull(devComment);
        this.devCommentTag = LPNulls.replaceNull(devCommentTag);
    }

    public HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex) {
        HashMap<HttpServletRequest, Object[]> hm = new HashMap<>();
        Object[] argValues = new Object[0];
        for (LPAPIArguments curArg : this.arguments) {
            argValues = LPArray.addValueToArray1D(argValues, curArg.getName() + ":" + getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
        }
        hm.put(request, argValues);
        return hm;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getSuccessMessageCode() {
        return this.successMessageCode;
    }

    @Override
    public JsonArray getOutputObjectTypes() {
        return outputObjectTypes;
    }

    @Override
    public LPAPIArguments[] getArguments() {
        return arguments;
    }

    @Override
    public String getApiUrl() {
        return GlobalVariables.ApiUrls.APP_CALENDAR_QUERIES.getUrl();
    }
    private final String name;
    private final String successMessageCode;
    private final LPAPIArguments[] arguments;
    private final JsonArray outputObjectTypes;

    @Override
    public String getDeveloperComment() {
        return this.devComment;
    }

    @Override
    public String getDeveloperCommentTag() {
        return this.devCommentTag;
    }
    private final String devComment;
    private final String devCommentTag;
}

public enum ParamsList {
    INCIDENT_ID("incidentId"), INCIDENT_TITLE("incidentTitle"), INCIDENT_DETAIL("incidentDetail"),
    NOTE("note"), NEW_STATUS("newStatus"),;

    private ParamsList(String requestName) {
        this.requestName = requestName;
    }

    public String getParamName() {
        return this.requestName;
    }
    private final String requestName;
}

public enum CalendarErrorTrapping implements EnumIntMessages {
    CALENDAR_NOT_EXISTS("calendar_calendarNotExists", "", ""),
    CALENDAR_ALREADY_EXISTS("calendar_calendarAlreadyExists", "", ""),
    CALENDAR_DATE_NOT_EXISTS("calendar_calendarDateNotExists", "", ""),
    CALENDAR_ALREADY_ACTIVE("calendar_calendarAlreadyActive", "", ""),
    CALENDAR_ALREADY_INACTIVE("calendar_calendarAlreadyInactive", "", ""),
    DATE_ALREADY_EXISTS_IN_CALENDAR("calendar_dateAlreadyExistsInCalendar", "", ""),
    
    ;
    private CalendarErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
        this.errorCode = errCode;
        this.defaultTextWhenNotInPropertiesFileEn = defaultTextEn;
        this.defaultTextWhenNotInPropertiesFileEs = defaultTextEs;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getDefaultTextEn() {
        return this.defaultTextWhenNotInPropertiesFileEn;
    }

    @Override
    public String getDefaultTextEs() {
        return this.defaultTextWhenNotInPropertiesFileEs;
    }

    private final String errorCode;
    private final String defaultTextWhenNotInPropertiesFileEn;
    private final String defaultTextWhenNotInPropertiesFileEs;
}

}
