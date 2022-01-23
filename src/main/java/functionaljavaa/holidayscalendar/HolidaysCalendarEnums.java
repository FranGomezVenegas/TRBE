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
import databases.TblsAppProcData;
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
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class HolidaysCalendarEnums {
    
    public enum CalendarEvents{ 
        NEW_CALENDAR, CLONE_CALENDAR, REMOVE_CALENDAR, UPDATE_CALENDAR_PROPERTIES, ADD_DATE_TO_CALENDAR, ADD_DATE_FROM_CALENDAR
    }    
    public enum CalendarAPIactionsEndpoints {
        NEW_CALENDAR("NEW_CALENDAR", "name", "", "instrumentNewInstrumentCreated_success",  
            new LPAPIArguments[]{ new LPAPIArguments("name", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcData.Instruments.TBL.getName()).build()).build()
        ),        
        ADD_DATE_TO_CALENDAR("ADD_DATE_TO_CALENDAR", "name", "", "instrumentUpdated_success",  
            new LPAPIArguments[]{ new LPAPIArguments("name", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_NEW_DATE, LPAPIArguments.ArgumentType.DATE.toString(), true, 7 ),
                new LPAPIArguments("dayName", LPAPIArguments.ArgumentType.STRING.toString(), false, 8 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 9 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 10 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcData.Instruments.TBL.getName()).build()).build()
        ),
        DECOMMISSION_INSTRUMENT("DECOMMISSION_INSTRUMENT", "name", "", "instrumentDecommissioned_success",  
            new LPAPIArguments[]{ new LPAPIArguments("name", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsApp.HolidaysCalendar.TBL.getName()).build()).build()
        ),
        UNDECOMMISSION_INSTRUMENT("UNDECOMMISSION_INSTRUMENT", "instrumentName", "", "instrumentUndecommissioned_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcData.Instruments.TBL.getName()).build()).build()
        ),
        TURN_ON_LINE("TURN_ON_LINE", "instrumentName", "", "instrumentTurnedONLine_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcData.Instruments.TBL.getName()).build()).build()
        ),
        TURN_OFF_LINE("TURN_OFF_LINE", "instrumentName", "", "instrumentTurnedOFFLine_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcData.Instruments.TBL.getName()).build()).build()
        ),
        START_CALIBRATION("START_CALIBRATION", "instrumentName", "", "instrumentCalibrationStarted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcData.Instruments.TBL.getName()).build()).build()
        ),
        COMPLETE_CALIBRATION("COMPLETE_CALIBRATION", "instrumentName", "", "instrumentCalibrationCompleted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),
                new LPAPIArguments("decision", LPAPIArguments.ArgumentType.STRING.toString(), true, 9 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcData.Instruments.TBL.getName()).build()).build()
        ),     
        START_PREV_MAINT("START_PREV_MAINT", "instrumentName", "", "instrumentPrevMaintStarted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcData.Instruments.TBL.getName()).build()).build()
        ),
        COMPLETE_PREV_MAINT("COMPLETE_PREV_MAINT", "instrumentName", "", "instrumentPrevMaintCompleted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),
                new LPAPIArguments("decision", LPAPIArguments.ArgumentType.STRING.toString(), true, 9 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcData.Instruments.TBL.getName()).build()).build()
        ),             
        START_VERIFICATION("START_VERIFICATION", "instrumentName", "", "instrumentVerificationStarted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcData.Instruments.TBL.getName()).build()).build()
        ),
        COMPLETE_VERIFICATION("COMPLETE_VERIFICATION", "instrumentName", "", "instrumentVerificationCompleted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),
                new LPAPIArguments("decision", LPAPIArguments.ArgumentType.STRING.toString(), true, 9 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcData.Instruments.TBL.getName()).build()).build()
        ),        
        ENTER_EVENT_RESULT("ENTER_EVENT_RESULT", "instrumentName", "", "eventValueEntered_success", 
                new LPAPIArguments[]{new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments("variableName", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments("newValue", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcData.Instruments.TBL.getName()).build()).build()
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
        public String getName(){return this.name;}
        public String getMandatoryParams(){return this.mandatoryParams;}
        public String getSuccessMessageCode(){return this.successMessageCode;}           
        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     

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
    
    static final String COMMON_PARAMS="incidentId|note";

    
    public enum CalendarAPIqueriesEndpoints {
        ACTIVE_INSTRUMENTS_LIST("ACTIVE_INSTRUMENTS_LIST", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        INSTRUMENT_AUDIT_FOR_GIVEN_INSTRUMENT("INSTRUMENT_AUDIT_FOR_GIVEN_INSTRUMENT", "",new LPAPIArguments[]{new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects),
        INSTRUMENT_EVENTS_FOR_GIVEN_INSTRUMENT("INSTRUMENT_EVENTS_FOR_GIVEN_INSTRUMENT", "",new LPAPIArguments[]{new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects),
        INSTRUMENT_EVENTS_INPROGRESS("INSTRUMENT_EVENTS_INPROGRESS","", new LPAPIArguments[]{
            new LPAPIArguments("fieldName", LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
            new LPAPIArguments("fielValue", LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects),
        INSTRUMENT_EVENT_VARIABLES("INSTRUMENT_EVENTS_VARIABLES", "",new LPAPIArguments[]{            
            new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6), }, EndPointsToRequirements.endpointWithNoOutputObjects),
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
        public String getName(){return this.name;}
        public String getSuccessMessageCode(){return this.successMessageCode;}           
        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        public LPAPIArguments[] getArguments() {return arguments;}
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
        xSTART_MULTIPLE_BATCH_IN_PARALLEL("incubationBatch_startMultipleInParallelPerIncubator", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        xSTART_FOR_LOCKED_INCUBATOR_MODE("incubationBatch_startForLockedIncubatorMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|')        
        ;
        private CalendarBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        public JSONArray getValuesList(){return this.valuesList;}
        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        

        @Override
        public Boolean getIsOptional() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ArrayList<String[]> getPreReqs() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }  
    public enum CalendarErrorTrapping{ 
        NOT_ONLINE("instrumentNotOnline","The instrument <*1*> is not currently on line","El instrumento <*1*> no está actualmente en línea"),
        NOT_DECOMMISSIONED("instrumentNotDecommissioned","The instrument <*1*> is not currently decommissioned","El instrumento <*1*> no está actualmente retirado"),
        ALREADY_ONLINE("instrumentAlreadyOnline", "The instrument <*1*> is currently on line","El instrumento <*1*> está actualmente en línea"),
        NO_PENDING_CALIBRATION("instrumentHasNoPendingCalibration", "The instrument <*1*> has no pending calibration in progress in this moment","El instrumento <*1*> no tiene ninguna calibración en curso en este momento"),
        NO_PENDING_VERIFICATION("instrumentHasNoPendingVerification", "The instrument <*1*> has no pending verification in progress in this moment","El instrumento <*1*> no tiene ninguna verificación en curso en este momento"),
        NO_PENDING_PREV_MAINT("instrumentHasNoPendingPrevMaint", "The instrument <*1*> has no pending preventive maintenance in progress in this moment","El instrumento <*1*> no tiene ningun mantenimiento preventivo en curso en este momento"),
        ALREADY_HAS_PENDING_CALIBRATION("instrumentAlreadyHasPendingCalibration", "The instrument <*1*> already has one pending calibration in progress in this moment","El instrumento <*1*> tiene actualmente una calibración en curso en este momento"),
        ALREADY_HAS_PENDING_VERIFICATION("instrumentAlreadyHasPendingVerification", "The instrument <*1*> already has one pending verification in progress in this moment","El instrumento <*1*> tiene actualmente una verificación en curso en este momento"),
        ALREADY_HAS_PENDING_PREV_MAINT("instrumentAlreadyHasPendingPrevMaint", "The instrument <*1*> already has one pending preventive maintenance in progress in this moment","El instrumento <*1*> tiene actualmente un mantenimiento preventivo en curso en este momento"),
        IS_LOCKED("instrumentIsLocked", "The instrument <*1*> is locked, the reason is <*2*>","El instrumento <*1*> está actualmente bloqueado, la razón es <*2*>"),
        TRYINGUPDATE_RESERVED_FIELD("instrumentTryingToUpdateReservedField", "Not allowed to update the reserved field <*1*>","No permitido modificar el campo reservado <*1*>"),
        ALREADY_DECOMMISSIONED("instrumentAlreadyDecommissioned", "Instrument <*1*> already decommissioned","Instrumento <*1*> ya fue retirado"),
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
