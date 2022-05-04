/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.instruments;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_NUM_DAYS;
import databases.TblsAppProcData.TablesAppProcData;
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
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class InstrumentsEnums {
    
    public enum AppInstrumentsAuditEvents implements EnumIntAuditEvents{ 
        CREATION, TURN_ON_LINE, TURN_OFF_LINE, PREVIOUS_USAGE_PERF_CHECK, CALIBRATION, START_CALIBRATION, COMPLETE_CALIBRATION, 
        PREVENTIVE_MAINTENANCE, START_PREVENTIVE_MAINTENANCE, COMPLETE_PREVENTIVE_MAINTENANCE,  
        VERIFICATION, START_VERIFICATION, COMPLETE_VERIFICATION,
        SERVICE, START_SERVICE, COMPLETE_SERVICE,
        NON_ROUTINE_EVENT, DECOMMISSION, UNDECOMMISSION, UPDATE_INSTRUMENT,
        VALUE_ENTERED, VALUE_REENTERED, REOPEN_EVENT
    }    
    public enum InstrumentsAPIactionsEndpoints implements EnumIntEndpoints{
        NEW_INSTRUMENT("NEW_INSTRUMENT", "instrumentName", "", "instrumentNewInstrumentCreated_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("familyName", LPAPIArguments.ArgumentType.STRING.toString(), false, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),        
        UPDATE_INSTRUMENT("UPDATE_INSTRUMENT", "instrumentName", "", "instrumentUpdated_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        DECOMMISSION_INSTRUMENT("DECOMMISSION_INSTRUMENT", "instrumentName", "", "instrumentDecommissioned_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        UNDECOMMISSION_INSTRUMENT("UNDECOMMISSION_INSTRUMENT", "instrumentName", "", "instrumentUndecommissioned_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        TURN_ON_LINE("TURN_ON_LINE", "instrumentName", "", "instrumentTurnedONLine_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        TURN_OFF_LINE("TURN_OFF_LINE", "instrumentName", "", "instrumentTurnedOFFLine_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        START_CALIBRATION("START_CALIBRATION", "instrumentName", "", "instrumentCalibrationStarted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        COMPLETE_CALIBRATION("COMPLETE_CALIBRATION", "instrumentName", "", "instrumentCalibrationCompleted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),
                new LPAPIArguments("decision", LPAPIArguments.ArgumentType.STRING.toString(), true, 9 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),     
        START_PREV_MAINT("START_PREV_MAINT", "instrumentName", "", "instrumentPrevMaintStarted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        COMPLETE_PREVENTIVE_MAINTENANCE("COMPLETE_PREVENTIVE_MAINTENANCE", "instrumentName", "", "instrumentPrevMaintCompleted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),
                new LPAPIArguments("decision", LPAPIArguments.ArgumentType.STRING.toString(), true, 9 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),             
        START_VERIFICATION("START_VERIFICATION", "instrumentName", "", "instrumentVerificationStarted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        COMPLETE_VERIFICATION("COMPLETE_VERIFICATION", "instrumentName", "", "instrumentVerificationCompleted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),
                new LPAPIArguments("decision", LPAPIArguments.ArgumentType.STRING.toString(), true, 9 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),   
        START_SERVICE("START_SERVICE", "instrumentName", "", "instrumentServiceStarted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        COMPLETE_SERVICE("COMPLETE_SERVICE", "instrumentName", "", "instrumentServiceCompleted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),
                new LPAPIArguments("decision", LPAPIArguments.ArgumentType.STRING.toString(), true, 9 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),   
        REOPEN_EVENT("REOPEN_EVENT", "instrumentName", "", "instrumentEventReopened_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        ENTER_EVENT_RESULT("ENTER_EVENT_RESULT", "instrumentName", "", "eventValueEntered_success", 
                new LPAPIArguments[]{new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments("variableName", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments("newValue", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ), 
        REENTER_EVENT_RESULT("REENTER_EVENT_RESULT", "instrumentName", "", "eventValueReentered_success", 
                new LPAPIArguments[]{new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments("variableName", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments("newValue", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),                
        INSTRUMENTAUDIT_SET_AUDIT_ID_REVIEWED("INSTRUMENTAUDIT_SET_AUDIT_ID_REVIEWED", "instrumentName", "", "instrumentAuditIdReviewed_success", 
                new LPAPIArguments[]{
                //new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments("auditId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        ;
        private InstrumentsAPIactionsEndpoints(String name, String mandatoryParams, String optionalParams, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
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
        @Override        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     

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
    
    public enum InstrumentsAPIqueriesEndpoints implements EnumIntEndpoints{
        ACTIVE_INSTRUMENTS_LIST("ACTIVE_INSTRUMENTS_LIST", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        DECOMISSIONED_INSTRUMENTS_LAST_N_DAYS("DECOMISSIONED_INSTRUMENTS_LAST_N_DAYS","",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_NUM_DAYS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 6)}, 
            EndPointsToRequirements.endpointWithNoOutputObjects),
        GET_INSTRUMENT_FAMILY_LIST("GET_INSTRUMENT_FAMILY_LIST", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        INSTRUMENT_AUDIT_FOR_GIVEN_INSTRUMENT("INSTRUMENT_AUDIT_FOR_GIVEN_INSTRUMENT", "",new LPAPIArguments[]{new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects),
        INSTRUMENT_EVENTS_FOR_GIVEN_INSTRUMENT("INSTRUMENT_EVENTS_FOR_GIVEN_INSTRUMENT", "",new LPAPIArguments[]{new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects),
        INSTRUMENT_EVENTS_INPROGRESS("INSTRUMENT_EVENTS_INPROGRESS","", new LPAPIArguments[]{
            new LPAPIArguments("fieldName", LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
            new LPAPIArguments("fielValue", LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects),
        INSTRUMENT_EVENT_VARIABLES("INSTRUMENT_EVENTS_VARIABLES", "",new LPAPIArguments[]{            
            new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6), }, EndPointsToRequirements.endpointWithNoOutputObjects)
        ;
        private InstrumentsAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
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
    public enum InstrumentsBusinessRules  implements EnumIntBusinessRules{
        xSTART_MULTIPLE_BATCH_IN_PARALLEL("incubationBatch_startMultipleInParallelPerIncubator", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        xSTART_FOR_LOCKED_INCUBATOR_MODE("incubationBatch_startForLockedIncubatorMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        REVISION_MODE("instrumentAuditRevisionMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        AUTHOR_CAN_REVIEW_AUDIT_TOO("instrumentAuditAuthorCanBeReviewerToo", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        CHILD_REVISION_REQUIRED("instrumentAuditChildRevisionRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null)
        ;
        private InstrumentsBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator
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
        
    public enum InstrumentsErrorTrapping implements EnumIntMessages{ 
        NOT_FOUND("instrumentNotFound","The instrument <*1*> is not found in procedure <*2*>","El instrumento <*1*> no se ha encontrado para el proceso <*2*>"),
        FAMILY_NOT_FOUND("instrumentFamilyNotFound","The instrument family <*1*> is not found in procedure <*2*>","La familia de instrumento <*1*> no se ha encontrado para el proceso <*2*>"),
        NOT_ONLINE("instrumentNotOnline","The instrument <*1*> is not currently on line","El instrumento <*1*> no está actualmente en línea"),
        NOT_DECOMMISSIONED("instrumentNotDecommissioned","The instrument <*1*> is not currently decommissioned","El instrumento <*1*> no está actualmente retirado"),
        ALREADY_ONLINE("instrumentAlreadyOnline", "The instrument <*1*> is currently on line","El instrumento <*1*> está actualmente en línea"),
        NO_PENDING_CALIBRATION("instrumentHasNoPendingCalibration", "The instrument <*1*> has no pending calibration in progress in this moment","El instrumento <*1*> no tiene ninguna calibración en curso en este momento"),
        NO_PENDING_VERIFICATION("instrumentHasNoPendingVerification", "The instrument <*1*> has no pending verification in progress in this moment","El instrumento <*1*> no tiene ninguna verificación en curso en este momento"),
        NO_PENDING_SERVICE("instrumentHasNoPendingService", "The instrument <*1*> has no pending service in progress in this moment","El instrumento <*1*> no tiene ningún servicio en curso en este momento"),        
        NO_PENDING_PREV_MAINT("instrumentHasNoPendingPrevMaint", "The instrument <*1*> has no pending preventive maintenance in progress in this moment","El instrumento <*1*> no tiene ningun mantenimiento preventivo en curso en este momento"),
        ALREADY_HAS_PENDING_CALIBRATION("instrumentAlreadyHasPendingCalibration", "The instrument <*1*> already has one pending calibration in progress in this moment","El instrumento <*1*> tiene actualmente una calibración en curso en este momento"),
        ALREADY_HAS_PENDING_VERIFICATION("instrumentAlreadyHasPendingVerification", "The instrument <*1*> already has one pending verification in progress in this moment","El instrumento <*1*> tiene actualmente una verificación en curso en este momento"),
        ALREADY_HAS_PENDING_PREV_MAINT("instrumentAlreadyHasPendingPrevMaint", "The instrument <*1*> already has one pending preventive maintenance in progress in this moment","El instrumento <*1*> tiene actualmente un mantenimiento preventivo en curso en este momento"),
        ALREADY_HAS_PENDING_SERVICE("instrumentAlreadyHasPendingService", "The instrument <*1*> already has one pending service in progress in this moment","El instrumento <*1*> tiene actualmente un servicio en curso en este momento"),
        ALREADY_INPROGRESS("instrumentEventAlreadyInprogress", "The instrument event <*1*> is currently in progress","El evento de instrumento <*1*> está actualmente en progreso"),
        IS_LOCKED("instrumentIsLocked", "The instrument <*1*> is locked, the reason is <*2*>","El instrumento <*1*> está actualmente bloqueado, la razón es <*2*>"),
        TRYINGUPDATE_RESERVED_FIELD("instrumentTryingToUpdateReservedField", "Not allowed to update the reserved field <*1*>","No permitido modificar el campo reservado <*1*>"),
        ALREADY_DECOMMISSIONED("instrumentAlreadyDecommissioned", "Instrument <*1*> already decommissioned","Instrumento <*1*> ya fue retirado"),
        WRONG_DECISION("instrumentWrongDecision", "wrongDecision <*1*> is not one of the accepted values(<*2*>)", "wrongDecision <*1*> is not one of the accepted values(<*2*>)"),
        AUDIT_RECORDS_PENDING_REVISION("instrumentAuditRecordsPendingRevision", "The sample <*1*> has pending sign audit records.", "La muestra <*1*> tiene registros de auditoría sin firmar"),
        AUDIT_RECORD_NOT_FOUND("AuditRecordNotFound", "The audit record <*1*> for sample does not exist", "No encontrado un registro de audit para muestra con id <*1*>"),
        AUDIT_RECORD_ALREADY_REVIEWED("AuditRecordAlreadyReviewed", "The audit record <*1*> was reviewed therefore cannot be reviewed twice.", "El registro de audit para muestra con id <*1*> ya fue revisado, no se puede volver a revisar."),
        AUTHOR_CANNOT_BE_REVIEWER("AuditSamePersonCannotBeAuthorAndReviewer", "Same person cannot review its own actions", "La misma persona no puede revisar sus propias acciones"),
        PARAMETER_MISSING("sampleAuditRevisionMode_ParameterMissing", "", ""),
        DISABLED("instrumentAuditRevisionMode_Disable", "", ""),
        VARIABLE_TYPE_NOT_RECOGNIZED("variableTypeNotRecognized", "", "")
        ;
        private InstrumentsErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    
    public enum InstrEventsErrorTrapping implements EnumIntMessages{ 
        EVENT_NOT_FOUND("instrEvent_EventNotFound","The instrument event <*1*> is already complete in procedure <*2*>","The instrument event <*1*> is already complete in procedure <*2*>"),
        EVENT_NOT_OPEN_FOR_CHANGES("instrEvent_NotOpenedForChanges","The event is not open for changes","Evento no abierto a cambios"),        
        VARIABLE_NOT_EXISTS_EVENT_WITHNOVARIABLES("instEvent_variableNotExists_eventWithNoVariables","This event has no this variable and the event has no variables","Este evento no contiene esta variable y el evento no tiene variables"),
        VARIABLE_NOT_EXISTS("instEvent_variableNotExists", "The parameter <*1*> is not one of the event parameters <*2*>","El parámetro <*1*> no es uno de los que tiene el evento, <*2*>"),
        MORE_THAN_ONE_VARIABLE("instEvent_moreThanOneVariable", "Found more than one record, <*1*> for the query <*2*> on <*3*>","Found more than one record, <*1*> for the query <*2*> on <*3*>"),
        EVENT_HAS_PENDING_RESULTS("instEvent_eventWithPendingResults", "The event has <*1*> pending results", "El evento tiene <*1*> resultado(s) pendiente(s)"),
        EVENT_NOTHING_PENDING("instEvent_eventHasMothingPending", "The event has nothing pending", "El evento no tiene nada pendiente"),
        VARIABLE_VALUE_NOTONEOFTHEEXPECTED("instEvent_valueNotOneOfExpected", "The value <*1*> is not one of the accepted values <*2*> for variable <*3*> in procedure <*4*>","The value <*1*> is not one of the accepted values <*2*> for variable <*3*> in procedure <*4*>"),
        NOT_NUMERIC_VALUE("DataSampleAnalysisResult_ValueNotNumericForQuantitativeParam", "", ""),
        USE_REENTER_WHEN_PARAM_ALREADY_HAS_VALUE("instEvent_whenParamAlreadyHasValueRequiresToUseReenterAction", "", ""),
        USE_ENTER_WHEN_PARAM_HAS_NO_VALUE("instEvent_whenParamHasNoValueRequiresToUseEnterResultAction", "", ""),
        SAME_RESULT_VALUE("DataSampleAnalysisResult_SampleAnalysisResultSameValue", "", ""),

        ;
        private InstrEventsErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    public enum InstrLockingReasons{
        UNDER_CALIBRATION_EVENT("instrLockedByCalibrationInProgress"),
        UNDER_MAINTENANCE_EVENT("instrLockedByMaintenanceInProgress"),
        UNDER_SERVICE_EVENT("instrLockedByServiceInProgress"),
        UNDER_DAILY_VERIF_EVENT("instrLockedByDailyVerificationInProgress"),
        
        ;
        InstrLockingReasons(String propName){
            this.propName=propName;
        }
        public String getPropertyName(){return this.propName;}
        private final String propName;
    }
}
