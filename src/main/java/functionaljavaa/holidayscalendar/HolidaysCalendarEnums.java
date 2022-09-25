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
import java.util.ArrayList;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class HolidaysCalendarEnums {
    
    public enum CalendarEvents{ 
        NEW_CALENDAR, CLONE_CALENDAR, REMOVE_CALENDAR, UPDATE_CALENDAR_PROPERTIES, ADD_DATE_TO_CALENDAR, ADD_DATE_FROM_CALENDAR
    }    
    public enum CalendarAPIactionsEndpoints implements EnumIntEndpoints{
        NEW_CALENDAR("NEW_CALENDAR", "name", "", "calendarNewCalendarCreated_success",  
            new LPAPIArguments[]{ new LPAPIArguments("name", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName()).build()).build()
        ),        
        ADD_DATE_TO_CALENDAR("ADD_DATE_TO_CALENDAR", "name", "", "calendarDateAdded_success",  
            new LPAPIArguments[]{ new LPAPIArguments("name", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_NEW_DATE, LPAPIArguments.ArgumentType.DATE.toString(), true, 7 ),
                new LPAPIArguments("dayName", LPAPIArguments.ArgumentType.STRING.toString(), false, 8 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 10 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsApp.TablesApp.HOLIDAYS_CALENDAR.getTableName()).build()).build()
        ),
        DELETE_DATE_FROM_GIVEN_CALENDAR("DELETE_DATE_FROM_GIVEN_CALENDAR", "", "", "calendarDateDeleted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("calendar", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("date_id", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE.getTableName()).build()).build()
        )
        ;
        private CalendarAPIactionsEndpoints(String name, String mandatoryParams, String optionalParams, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.mandatoryParams=mandatoryParams;
            this.optionalParams=optionalParams;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;
            this.outputObjectTypes=outputObjectTypes;
        } 
        @Override        public String getName(){return this.name;}
        public String getMandatoryParams(){return this.mandatoryParams;}
        @Override        public String getSuccessMessageCode(){return this.successMessageCode;}           
        @Override   public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     

        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){                
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
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
    }
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    
    public enum CalendarAPIqueriesEndpoints implements EnumIntEndpoints{
        GET_ALL_HOLIDAY_DATES_LIST_ALL_CALENDARS("GET_ALL_HOLIDAY_DATES_LIST_ALL_CALENDARS", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        
        ;
        private CalendarAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
            this.outputObjectTypes=outputObjectTypes;            
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){                
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }  
            hm.put(request, argValues);            
            return hm;
        }        
        @Override        public String getName(){return this.name;}
        @Override        public String getSuccessMessageCode(){return this.successMessageCode;}           
        @Override        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        @Override        public LPAPIArguments[] getArguments() {return arguments;}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }

    public enum ParamsList{INCIDENT_ID("incidentId"),INCIDENT_TITLE("incidentTitle"),INCIDENT_DETAIL("incidentDetail"),
        NOTE("note"),NEW_STATUS("newStatus"),
        ;
        private ParamsList(String requestName){
            this.requestName=requestName;
        } 
        public String getParamName(){
            return this.requestName;
        }        
        private final String requestName;
    }    
    public enum CalendarBusinessRules  implements EnumIntBusinessRules{
        xSTART_MULTIPLE_BATCH_IN_PARALLEL("incubationBatch_startMultipleInParallelPerIncubator", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        xSTART_FOR_LOCKED_INCUBATOR_MODE("incubationBatch_startForLockedIncubatorMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null)
        ;
        private CalendarBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator
        , Boolean isOpt, ArrayList<String[]> preReqs){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
            this.isOptional=isOpt;
            this.preReqs=preReqs;
        }       
        @Override        public String getTagName(){return this.tagName;}
        @Override        public String getAreaName(){return this.areaName;}
        @Override        public JSONArray getValuesList(){return this.valuesList;}
        @Override        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        @Override        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        @Override        public Boolean getIsOptional() {return isOptional;}
        @Override        public ArrayList<String[]> getPreReqs() {return this.preReqs;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        
        private final Boolean isOptional;
        private final ArrayList<String[]> preReqs;
    }  
    public enum CalendarErrorTrapping implements EnumIntMessages{         
        CALENDAR_NOT_EXISTS("calendar_calendarNotExists", "", ""),
        CALENDAR_ALREADY_EXISTS("calendar_calendarAlreadyExists", "", ""),
        CALENDAR_DATE_NOT_EXISTS("calendar_calendarDateNotExists", "", ""),
        DATE_ALREADY_EXISTS_IN_CALENDAR("calendar_dateAlreadyExistsInCalendar", "", ""),
        ;
        private CalendarErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
    
}
