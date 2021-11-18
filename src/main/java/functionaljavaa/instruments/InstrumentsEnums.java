/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.instruments;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE;
import databases.TblsAppProcConfig;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import org.json.simple.JSONArray;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class InstrumentsEnums {
    
    public enum InstrumentEvents{ 
        CREATION, TURN_ON_LINE, TURN_OFF_LINE, PREVIOUS_USAGE_PERF_CHECK, CALIBRATION, START_CALIBRATION, COMPLETE_CALIBRATION, PREVENTIVE_MAINTENANCE, NON_ROUTINE_EVENT, DECOMMISSION,
        UNDECOMMISSION, UPDATE_INFO
    }    
    public enum InstrumentsAPIactionsEndpoints{
        NEW_INSTRUMENT("NEW_INSTRUMENT", "instrumentName", "", "instrumentNewInstrumentCreated_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcConfig.Instruments.TBL.getName()).build()).build()
                ),
        TURN_ON_LINE("TURN_ON_LINE", "instrumentName", "", "instrumentTurnedONLine_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcConfig.Instruments.TBL.getName()).build()).build()
                ),
        TURN_OFF_LINE("TURN_OFF_LINE", "instrumentName", "", "instrumentTurnedOFFLine_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcConfig.Instruments.TBL.getName()).build()).build()
                ),
        START_CALIBRATION("START_CALIBRATION", "instrumentName", "", "instrumentCalibrationStarted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcConfig.Instruments.TBL.getName()).build()).build()
                ),
        COMPLETE_CALIBRATION("COMPLETE_CALIBRATION", "instrumentName", "", "instrumentCalibrationCompleted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TblsAppProcConfig.Instruments.TBL.getName()).build()).build()
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
        public String getName(){return this.name;}
        public String getMandatoryParams(){return this.mandatoryParams;}
        public String getSuccessMessageCode(){return this.successMessageCode;}           
        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     

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

    
    public enum InstrumentsAPIqueriesEndpoints{
        /**
         *
         */
        USER_OPEN_INCIDENTS("USER_OPEN_INCIDENTS", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        INCIDENT_DETAIL_FOR_GIVEN_INCIDENT("INCIDENT_DETAIL_FOR_GIVEN_INCIDENT", "",new LPAPIArguments[]{new LPAPIArguments(ParamsList.INCIDENT_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects),
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
    public enum InstrumentsBusinessRules{
        xSTART_MULTIPLE_BATCH_IN_PARALLEL("incubationBatch_startMultipleInParallelPerIncubator", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        xSTART_FOR_LOCKED_INCUBATOR_MODE("incubationBatch_startForLockedIncubatorMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|')        
        ;
        private InstrumentsBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
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
    }  
    public enum InstrumentsErrorTrapping{ 
        NOT_ONLINE("instrumentNotOnline","The instrument <*1*> is not currently on line","El instrumento <*1*> no está actualmente en línea"),
        ALREADY_ONLINE("instrumentAlreadyOnline", "The instrument <*1*> is currently on line","El instrumento <*1*> está actualmente en línea"),
        NO_PENDING_CALIBRATION("instrumentHasNoPendingCalibration", "The instrument <*1*> has no pending calibrations in progress in this moment","El instrumento <*1*> no tiene ninguna calibración en curso en este momento"),
        ALREADY_HAS_PENDING_CALIBRATION("instrumentAlreadyHasPendingCalibration", "The instrument <*1*> already has one pending calibration in progress in this moment","El instrumento <*1*> tiene actualmente una calibración en curso en este momento"),
        INCUBATORBATCH_NOT_STARTED("IncubatorBatchNotStartedYet", "The batch <*1*> was not started yet for procedure <*2*>", "La tanda <*1*> no está iniciada todavía para el proceso <*2*>"),
        INCUBATORBATCH_ALREADY_STARTED("IncubatorBatchAlreadyStarted", "The batch <*1*> was already started and cannot be started twice for procedure <*2*>", "La tanda <*1*> no está iniciada todavía para el proceso <*2*>"),
        INCUBATORBATCH_ALREADY_IN_PROCESS("IncubatorBatchAlreadyInProcess", "The batch <*1*> is already in process for incubator <*2*> and start multiples batches per incubator is not allowed for the procedure <*3*>", ""),
        INCUBATORBATCH_ALREADY_EXIST("incubatorBatchExist", "One incubator batch called <*1*> already exist in procedure <*2*>", "Una tanda con el nombre <*1*> ya existe en el proceso <*2*>"),
        INCUBATORBATCH_NOT_FOUND("incubatorBatchNotFound", "One incubator batch called <*1*> does not exist in procedure <*2*>", "Una tanda con el nombre <*1*> no existe en el proceso <*2*>"),        
        INCUBATORBATCH_TEMPLATE_NOT_ACTIVE("incubatorBatchTemplateNotActive","The Batch template <*1*> and version <*2*> is not active","The Batch template <*1*> and version <*2*> is not active"),
        BATCH_AVAILABLEFORCHANGES("batchAvailableForChanges", "The Batch <*1*> is available to alter its content", "The Batch <*1*> is available to alter its content"),
        INCUB_BATCH_NOT_ACTIVE_FOR_CHANGES("incubationBatchStart_StoppedByNotActiveForChanges", "", ""), 
        INCUB_BATCH_START_STOPPED_BY_BUSINESSRULEMODE("incubationBatchStart_StoppedByIncubationLockedBusinessRuleMode", "", ""),
        
        EMPTY_BATCH("incubBatch_emptyBatch", "", ""),
        INCUB_BATCH_STARTED_CHANGEITSCONTENT("IncubatorBatchStartedToChangeItsContent", "", ""),
        SAMPLE_NOTFOUND_IN_BATCH("incubBatch_sampleNotFoundInBatch"," Sample <*1*> not found in batch <*2*> for procedure <*3*>.", ""),
        SAMPLES_IN_BATCH_SET_AS_BATCHSTARTED("allSamplesInBatchSetAsBatchStarted", "", ""),
        SAMPLES_IN_BATCH_SET_AS_BATCHENDED("allSamplesInBatchSetAsBatchEnded", "", ""),
        CREATEBATCH_TYPECHECKER_SUCCESS("createBatchTypeCheckerSuccess", "", ""),
        INCUBATORBATCH_NOTEMPTY_TOBEREMOVED("IncubatorBatchNotEmptyToRemove", "", ""),
        BATCHTYPE_NOT_RECOGNIZED("incubatorBatchType_notRecognized", "batchType <*1*> Not recognized", "batchType <*1*> Not recognized"),
        SAMPLE_HAS_NOPENDING_INCUBATION("sampleWithNoPendingIncubation", "There is no pending incubation for sample <*1*> in procedure <*2*>", "There is no pending incubation for sample <*1*> in procedure <*2*>"), 
        MOMENT_NOTDECLARED_IN_BATCHMOMENTSLIST("incubBatch_momentNotInBatchMomentsList","The moment <*1*> is not declared in BatchIncubatorMoments", "The moment <*1*> is not declared in BatchIncubatorMoments"),
        STAGE_NOT_RECOGNIZED("incubBatch_stageNotRecognized", " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>.", " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>."),
        SAMPLE_ALREADYBATCHED("incubBatch_sampleAlreadyBatched", "The pending incubation stage is <*1*> and the sample <*2*>  is already batched in batch <*3*> for procedure <*3*>", "The pending incubation stage is <*1*> and the sample <*2*>  is already batched in batch <*3*> for procedure <*3*>"), 
        FIELD_NOT_FOUND("incubBatch_fieldNotFound","Field <*1*> not found in table <*2*> for procedure <*3*>", "Field <*1*> not found in table <*2*> for procedure <*3*>"),
        STRUCTURED_BATCH_WRONGDIMENSION("incubBatchStructured_wrongDimensions", "", ""),
        STRUCTURED_BATCH_POSITIONOCCUPIED("incubBatchStructured_positionOccupied", "", ""),
        STRUCTURED_POSITION_OVER_DIMENSION("incubBatchStructured_positionOverDimenrsion", "", ""),
        PARSE_ERROR_STRUCTUREDBATCH("incubBatchStructured_parseError", "", "")
        
        ;
        private InstrumentsErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
