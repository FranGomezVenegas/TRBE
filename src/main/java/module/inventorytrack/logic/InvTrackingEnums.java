/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inventorytrack.logic;

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
import module.inventorytrack.definition.TblsInvTrackingConfig;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.ApiUrls;

/**
 *
 * @author User
 */
public class InvTrackingEnums {
    public enum InvReferenceStockControlTypes{VOLUME, ITEMS}
    public enum AppInventoryTrackingAuditEvents implements EnumIntAuditEvents{ 
        CREATION, UOM_CONVERSION_ON_CREATION, TURN_AVAILABLE, TURN_UNAVAILABLE, 
        RETIRED, UNRETIRED,
        CREATED_QUALIFICATION, COMPLETE_QUALIFICATION, REOPEN_QUALIFICATION, UNLOCK_LOT_ONCE_QUALIFIED, TURN_AVAILABLE_ONCE_QUALIFIED,
        LOT_VOLUME_ADJUSTED, LOT_VOLUME_CONSUMED, LOT_VOLUME_ADDITION,
        UPDATE_INVENTORY_LOT,
        VALUE_ENTERED, VALUE_REENTERED
    }   
    public enum InvLotStatuses{NEW,        QUARANTINE,         RETIRED, 
        NOT_AVAILABLEFOR_USE,         AVAILABLE_FOR_USE,         CANCELED
        ;
        public static String getStatusFirstCode(EnumIntTableFields[] invReferenceFlds, Object[] invReferenceVls){
            ArrayList<String[]> preReqs = new ArrayList<String[]>();
            preReqs.add(0, new String[]{"data","sampleStatusesByBusinessRules"});                
            if (Boolean.valueOf(
                    invReferenceVls[EnumIntTableFields.getFldPosicInArray(invReferenceFlds, TblsInvTrackingConfig.Reference.LOT_REQUIRES_QUALIF.getName())].toString()))
                return QUARANTINE.toString();        
            else
                return NEW.toString();
        }       
    }
      
    public enum InventoryTrackAPIactionsEndpoints implements EnumIntEndpoints{
        NEW_INVENTORY_LOT("NEW_INVENTORY_LOT", "inventoryLot", "", "invTrackingNewLotCreated_success",  
            new LPAPIArguments[]{ new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 7 ),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), true, 8 ),
                new LPAPIArguments("expiryDate", LPAPIArguments.ArgumentType.STRING.toString(), false, 9 ),
                new LPAPIArguments("expiryDateInUse", LPAPIArguments.ArgumentType.STRING.toString(), false, 10 ),
                new LPAPIArguments("retestDate", LPAPIArguments.ArgumentType.STRING.toString(), false, 11 ),
                new LPAPIArguments("volume", LPAPIArguments.ArgumentType.BIGDECIMAL.toString(), false, 12 ),
                new LPAPIArguments("volumeUom", LPAPIArguments.ArgumentType.STRING.toString(), false, 13 ),
                new LPAPIArguments("vendor", LPAPIArguments.ArgumentType.STRING.toString(), false, 14 ),
                new LPAPIArguments("vendorLot", LPAPIArguments.ArgumentType.STRING.toString(), false, 15 ),
                new LPAPIArguments("vendorReference", LPAPIArguments.ArgumentType.STRING.toString(), false, 16 ),
                new LPAPIArguments("purity", LPAPIArguments.ArgumentType.STRING.toString(), false, 17 ),
                new LPAPIArguments("conservationCondition", LPAPIArguments.ArgumentType.STRING.toString(), false, 18 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 19 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 20 ),
                new LPAPIArguments("numEntries", LPAPIArguments.ArgumentType.STRING.toString(), false, 21 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),        
        TURN_LOT_AVAILABLE("TURN_LOT_AVAILABLE", "inventoryLot", "", "invTrackingLotTurnAvailable_success",  
            new LPAPIArguments[]{ new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 7 ),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), true, 8 ),                
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 10 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),                
        TURN_LOT_UNAVAILABLE("TURN_LOT_UNAVAILABLE", "inventoryLot", "", "invTrackingLotTurnUnavailable_success",  
            new LPAPIArguments[]{ new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 7 ),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), true, 8 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 19 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 20 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),                
        UPDATE_LOT("UPDATE_LOT", "lotName", "", "inventoryLotUpdated_success",  
            new LPAPIArguments[]{ new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("modelNumber", LPAPIArguments.ArgumentType.STRING.toString(), false, 7 ),
                new LPAPIArguments("serialNumber", LPAPIArguments.ArgumentType.STRING.toString(), false, 8 ),
                new LPAPIArguments("supplierName", LPAPIArguments.ArgumentType.STRING.toString(), false, 9 ),
                new LPAPIArguments("manufacturerName", LPAPIArguments.ArgumentType.STRING.toString(), false, 10 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 11 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        RETIRE_LOT("RETIRE_LOT", "lotName", "", "inventoryLotRetired_success",  
            new LPAPIArguments[]{ new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 7 ),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), true, 8 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 19 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 20 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ), 
        UNRETIRE_LOT("UNRETIRE_LOT", "lotName", "", "inventoryLotUnRetired_success",  
            new LPAPIArguments[]{ new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 7 ),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), true, 8 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 10 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        START_QUALIFICATION("START_QUALIFICATION", "lotName", "", "inventoryLotQualificationStarted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 7 ),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), true, 8 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 10 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        COMPLETE_QUALIFICATION("COMPLETE_QUALIFICATION", "lotName", "", "inventoryLotQualificationCompleted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 7 ),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), true, 8 ),
                new LPAPIArguments("decision", LPAPIArguments.ArgumentType.STRING.toString(), true, 9 ),
                new LPAPIArguments("turn_available_lot", LPAPIArguments.ArgumentType.STRING.toString(), false, 10 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11 ),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 12 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),     
        REOPEN_QUALIFICATION("REOPEN_QUALIFICATION", "lotName", "", "inventoryLotQualificationReopened_success",  
            new LPAPIArguments[]{ new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 7 ),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), true, 8 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        CONSUME_INV_LOT_VOLUME("CONSUME_INV_LOT", "lotName", "", "inventoryLotConsumed_success",  
            new LPAPIArguments[]{ new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 7 ),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), true, 8 ),
                new LPAPIArguments("volume", LPAPIArguments.ArgumentType.BIGDECIMAL.toString(), false, 9 ),
                new LPAPIArguments("volumeUom", LPAPIArguments.ArgumentType.STRING.toString(), false, 10 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        ADD_INV_LOT_VOLUME("ADD_INV_LOT_VOLUME", "lotName", "", "inventoryLotadded_success",  
            new LPAPIArguments[]{ new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 7 ),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), true, 8 ),
                new LPAPIArguments("volume", LPAPIArguments.ArgumentType.BIGDECIMAL.toString(), false, 9 ),
                new LPAPIArguments("volumeUom", LPAPIArguments.ArgumentType.STRING.toString(), false, 10 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        ADJUST_INV_LOT_VOLUME("ADJUST_INV_LOT", "lotName", "", "inventoryLotAdjusted_success",  
            new LPAPIArguments[]{ new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 7 ),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), true, 8 ),
                new LPAPIArguments("volume", LPAPIArguments.ArgumentType.BIGDECIMAL.toString(), false, 9 ),
                new LPAPIArguments("volumeUom", LPAPIArguments.ArgumentType.STRING.toString(), false, 10 )},
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
        LOTAUDIT_SET_AUDIT_ID_REVIEWED("LOTAUDIT_SET_AUDIT_ID_REVIEWED", "lotName", "", "lotAuditIdReviewed_success", 
                new LPAPIArguments[]{new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),                
                new LPAPIArguments("auditId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.APP.getName())
                .add("table", TablesAppProcData.INSTRUMENTS.getTableName()).build()).build()
        ),
        ;
        private InventoryTrackAPIactionsEndpoints(String name, String mandatoryParams, String optionalParams, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
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
        @Override        public String getApiUrl(){return ApiUrls.INVENTORY_TRACKING_ACTIONS.getUrl();}
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
        @Override        public LPAPIArguments[] getArguments() {
            return arguments;
        }     
        private final String name;
        private final String mandatoryParams; 
        private final String optionalParams; 
        private final String successMessageCode;       
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }
    
    public enum InventoryTrackAPIqueriesEndpoints implements EnumIntEndpoints{
        ALL_INVENTORY_LOTS("ALL_INVENTORY_LOTS", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        RETIRED_INVENTORY_LOTS_LAST_N_DAYS("RETIRED_INVENTORY_LOTS_LAST_N_DAYS","",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_NUM_DAYS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 6),
                new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), false, 8)},
            EndPointsToRequirements.endpointWithNoOutputObjects),
        AUDIT_FOR_GIVEN_INVENTORY_LOT("AUDIT_FOR_GIVEN_INVENTORY_LOT", "",
            new LPAPIArguments[]{new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects),
        INV_LOT_QUALIF_INPROGRESS("INV_LOT_QUALIF_INPROGRESS","", new LPAPIArguments[]{
            new LPAPIArguments("fieldName", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 6),
            new LPAPIArguments("fielValue", LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects),
        EXPIRED_LOTS("EXPIRED_LOTS","", 
            new LPAPIArguments[]{new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(REQUEST_PARAM_NUM_DAYS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 9),}, EndPointsToRequirements.endpointWithNoOutputObjects),
        REFERENCES_UNDER_MIN_STOCK("REFERENCES_UNDER_MIN_STOCK","",
            new LPAPIArguments[]{new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), false, 8)}, EndPointsToRequirements.endpointWithNoOutputObjects),
        REFERENCE_WITH_CONTROL_ISSUES("REFERENCE_WITH_CONTROL_ISSUES","",
            new LPAPIArguments[]{new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), false, 8)}, EndPointsToRequirements.endpointWithNoOutputObjects),
        LOT_PRINT_LABEL("LOT_PRINT_LABEL", "",
            new LPAPIArguments[]{new LPAPIArguments("lotName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects),

        GET_INSTRUMENT_FAMILY_LIST("GET_INSTRUMENT_FAMILY_LIST", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        INSTRUMENT_EVENTS_FOR_GIVEN_INSTRUMENT("INSTRUMENT_EVENTS_FOR_GIVEN_INSTRUMENT", "",new LPAPIArguments[]{new LPAPIArguments("instrumentName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects),
        INSTRUMENT_EVENT_VARIABLES("INSTRUMENT_EVENT_VARIABLES", "",new LPAPIArguments[]{            
            new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6), }, EndPointsToRequirements.endpointWithNoOutputObjects),
        COMPLETED_EVENTS_LAST_N_DAYS("COMPLETED_EVENTS_LAST_N_DAYS","",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_NUM_DAYS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 6)}, 
            EndPointsToRequirements.endpointWithNoOutputObjects),
        ;
        private InventoryTrackAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
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
        @Override        public String getApiUrl(){return ApiUrls.INVESTIGATIONS_QUERIES.getUrl();}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }

    public enum InventoryTrackBusinessRules  implements EnumIntBusinessRules{
        XREVISION_MODE("instrumentAuditRevisionMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        XAUTHOR_CAN_REVIEW_AUDIT_TOO("instrumentAuditAuthorCanBeReviewerToo", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        XCHILD_REVISION_REQUIRED("instrumentAuditChildRevisionRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null)
        ;
        private InventoryTrackBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator
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
        
    public enum InventoryTrackingErrorTrapping implements EnumIntMessages{ 
        REFERENCE_NOT_FOUND("inventoryTrackingReferenceNotFound","The inventory reference <*1*> is not found in procedure <*2*>","La referencia de inventario <*1*> no se ha encontrado para el proceso <*2*>"),
        UOM_NOT_INTHELIST("InventoryTracking_UnitNotPartOfAllowedList","", ""),
        ALREADY_HAS_PENDING_QUALIFICATION("InventoryLotAlreadyHasPendingQualification", "The lot <*1*> already has one pending qualification in progress in this moment","El lote <*1*> tiene actualmente una cualificación en curso en este momento"),
        NO_PENDING_QUALIFICATION("InventoryLotHasNoPendingQualification", "The lot <*1*> has no pending qualification in progress in this moment","El lote <*1*> no tiene ninguna cualificación en curso en este momento"),
        QUALIFICATION_NOT_CLOSED("inventoryLotQualificationNotClosed", "", ""),
        ALREADY_AVAILABLE("InventoryLotAlreadyAvailable", "The Lot <*1*> is currently available","El Lote <*1*> está actualmente disponible"),
        ALREADY_UNAVAILABLE("InventoryLotAlreadyUnAvailable", "The Lot <*1*> is currently available","El Lote <*1*> está actualmente disponible"),
        ALREADY_EXISTS("InventoryLotAlreadyExists", "", ""),
        ALREADY_RETIRED("instrumentAlreadyRetired", "Inventory lot <*1*> already retired","Lote <*1*> ya fue retirado"),
        NO_LOT_VOLUME_SPECIFIED_AND_REQUIRED("inventoryTrackingNoLotVolumeSpecifiedAndRequired", "", ""),
        INV_LOT_HAS_NOT_ENOUGH_VOLUME("InventoryLotHasNotEnoughVolume", "The lot <*1*> has not enough volume, <*2*>, for a consume of <*3*> <*4*>","El lote <*1*> no tiene suficiente volumen, <*2*>, para consumir <*3*> de <*4*>"),
        NOT_FOUND("InventoryLotNotFound","The Lot <*1*> is not found in procedure <*2*>","El Lote <*1*> no se ha encontrado para el proceso <*2*>"),
        NOT_AVAILABLE("InventoryLotNotAvailable","The Lot <*1*> is not currently available","El Lote <*1*> no está actualmente disponible"),
        NOT_RETIRED("InventoryLotNotRetired","The Lot <*1*> is not currently retired","El Lote <*1*> no está actualmente no retirado"),
        LOT_NOTQUALIFIED_YET("InventoryLotNotQualifiedYet", "", ""),
        INV_LOT_HAS_NO_VOLUME_SET("InventoryLotHasNoVolumeSet", "The lot <*1*> has no volume set","El lote <*1*> no tiene volumen asignado"),
        IS_LOCKED("InventoryLotIsLocked", "The lot <*1*> is locked, the reason is <*2*>","El lote <*1*> está actualmente bloqueado, la razón es <*2*>"),
        WRONG_DECISION("InventoryLotWrongDecision", "Wrong Decision <*1*>, it is not one of the accepted values(<*2*>)", "wrongDecision <*1*> is not one of the accepted values(<*2*>)"),
        TRYINGUPDATE_RESERVED_FIELD("InventoryLotTryingToUpdateReservedField", "Not allowed to update the reserved field <*1*>","No permitido modificar el campo reservado <*1*>"),
        
/*        ALREADY_INPROGRESS("instrumentEventAlreadyInprogress", "The instrument event <*1*> is currently in progress","El evento de instrumento <*1*> está actualmente en progreso"),
        AUDIT_RECORDS_PENDING_REVISION("instrumentAuditRecordsPendingRevision", "The sample <*1*> has pending sign audit records.", "La muestra <*1*> tiene registros de auditoría sin firmar"),
        AUDIT_RECORD_NOT_FOUND("AuditRecordNotFound", "The audit record <*1*> for sample does not exist", "No encontrado un registro de audit para muestra con id <*1*>"),
        AUDIT_RECORD_ALREADY_REVIEWED("AuditRecordAlreadyReviewed", "The audit record <*1*> was reviewed therefore cannot be reviewed twice.", "El registro de audit para muestra con id <*1*> ya fue revisado, no se puede volver a revisar."),
        AUTHOR_CANNOT_BE_REVIEWER("AuditSamePersonCannotBeAuthorAndReviewer", "Same person cannot review its own actions", "La misma persona no puede revisar sus propias acciones"),
        PARAMETER_MISSING("sampleAuditRevisionMode_ParameterMissing", "", ""),
        DISABLED("instrumentAuditRevisionMode_Disable", "", ""),
        VARIABLE_TYPE_NOT_RECOGNIZED("variableTypeNotRecognized", "", "")*/
        ;
        private InventoryTrackingErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
    
    public enum InvTrackingEventsErrorTrapping implements EnumIntMessages{ 
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
        private InvTrackingEventsErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
    public enum InventoryLotLockingReasons{
        UNDER_CALIBRATION_EVENT("instrLockedByCalibrationInProgress"),
        UNDER_MAINTENANCE_EVENT("instrLockedByMaintenanceInProgress"),
        UNDER_SERVICE_EVENT("instrLockedByServiceInProgress"),
        UNDER_DAILY_VERIF_EVENT("instrLockedByDailyVerificationInProgress"),
        
        ;
        InventoryLotLockingReasons(String propName){
            this.propName=propName;
        }
        public String getPropertyName(){return this.propName;}
        private final String propName;
    }
}
