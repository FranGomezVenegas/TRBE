/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.definition;

import com.labplanet.servicios.app.GlobalAPIsParams;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class InspLotRMEnums {
    
    public enum InspectionLotRMClousureTypes {
        BULK_INSPECTION_REJECTED
    } 
    public enum InspectionLotRMAuditEvents implements EnumIntAuditEvents{ 
        LOT_CREATION, LOT_BULK_ADDED, LOT_BULK_ADHOC_ADDED, LOT_BULK_QUANTITY_ADJUSTED, LOT_BULK_SAMPLE_QUANTITY_ADJUSTED, LOT_CERTIFICATE_UPDATED, LOT_CERTIFICATE_INSERTED,
        LOT_DECISION_TAKEN, LOT_BULK_DECISION_TAKEN, LOT_ALL_BULKS_DECISION_TAKEN, INVENTORY_RETAIN_UPDATED, LOT_QUANTITY_REDUCED_BY_BULK_REJECTION
    }   
    
    public enum DataLotProperties{ 
        SUFFIX_STATUS_FIRST ("_statusFirst", "First status, to be concatenated to the entity name, example: sample_statusFirst, program_statusFirst etc...", "One of the given statuses"),
        SUFFIX_LOTSTRUCTURE ("_lotStructure", "TBD", "TBD"),
/*        SAMPLE_STATUS_FIRST ("sample_statusFirst","", "One of the given statuses"),
        SAMPLE_STATUS_RECEIVED ("sample_statusReceived", "", "One of the given statuses"),
        SAMPLE_STATUS_INCOMPLETE ("sample_statusIncomplete", "", "One of the given statuses"),
        SAMPLE_STATUS_COMPLETE ("sample_statusComplete", "", "One of the given statuses"),
        SAMPLEALIQUOTING_VOLUME_REQUIRED ("sampleAliquot_volumeRequired", "TBD", "TBD"),
        SAMPLEASUBLIQUOTING_VOLUME_REQUIRED ("sampleSubAliquot_volumeRequired", "TBD", "TBD"), */       
        ;
        private DataLotProperties(String pName, String descr, String possValues){
            this.propertyName=pName;
            this.description=descr;
            this.possibleValues=possValues;
        }
        public String getPropertyName(){return this.propertyName;}
        public String getDescription(){return this.description;}
        public String getPossibleValues(){return this.possibleValues;}
    
        private final String propertyName;
        private final String description;
        private final String possibleValues;
    }
    public enum DataInspLotErrorTrapping implements EnumIntMessages{ 
        SAMPLE_NOT_FOUND ("SampleNotFound", "", ""),
        ERROR_INSERTING_INSPLOT_RECORD("errorInsertingInspLotRecord", "", ""),
        MISSING_MANDATORY_FIELDS("MissingMandatoryFields", "", ""),
        MISSING_CONFIG_CODE("MissingConfigCode", "", ""),        
        MISSING_SPEC_CONFIG_CODE("MissingSpecConfigCode", "Spec Config code <*1*> version <*2*> Not found for the procedure <*3*>", ""),        
        LOT_ALREADY_EXISTS("LotAlreadyExists", "", ""),
        SAMPLE_ALREADY_RECEIVED("SampleAlreadyReceived", "", ""),
        SAMPLE_NOT_REVIEWABLE("SampleNotReviewable", "", ""),
        VOLUME_SHOULD_BE_GREATER_THAN_ZERO("sampleAliquoting_volumeCannotBeNegativeorZero", "", ""),
        ALIQUOT_CREATED_BUT_ID_NOT_GOT("AliquotCreatedButIdNotGotToContinueApplyingAutomatisms", "Object created but aliquot id cannot be get back to continue with the logic", ""),
        SAMPLEASUBLIQUOTING_VOLUME_AND_UOM_REQUIRED ("sampleSubAliquoting_volumeAndUomMandatory", "", ""),                
        INVENTORYPLAN_CHECKER_ERROR("InventoryPlanCheckerReturnedErrors", "", ""),
        SAMPLEPLAN_CHECKER_ERROR("SamplePlanCheckerReturnedErrors", "", ""),
        NO_DECISION_LIST_DEFINED("NoDecisionsListDefined", "", ""),
        LOT_DECISION_NOT_ACCEPTED_VALUE("lotDecision_notAcceptedValue", "", ""),
        LOT_HAS_ONE_SAMPLE_ANALYSIS_WITH_NO_STATUS("lotHasOneSampleAnalysisWithNoStatus", "", ""),
        LOT_HAS_NOTREVIEWED_SAMPLEANALYSIS("lotHasNotReviewedSampleAnalysis", "", ""),
        LOT_HAS_NOTREVIEWED_SAMPLE("lotHasNotReviewedSample", "", ""),

        WRONG_ALGORITHM_DEFINITION("wrongAlgorithmDefinition", "", ""),
        NO_NUMBER_OF_BULKS_SPECIFIED("noNumberOfBulksSpecified", "", ""),
        LOT_WITH_NO_BULKS("lotWithNoBulks", "", ""),
        LOT_BULKS_WITH_NO_DECISION("lotBulksWithNoDecision", "", ""),
        ADD_ADHOC_BULKS_NOT_ALLOWED("addAdhocBulksNotAllowed", "The material <*1*> does not allow adding adhoc bulks to the lot <*2*>", "El material <*1*> no permite añadir bultos ad-hoc para el lote <*2*>"),
        
        
        ;
        private DataInspLotErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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

    public enum DataInspLotCertificateStatuses{NEW, DRAFT, READY_FOR_APPROVAL, UNDER_APPROVAL, APPROVED, SENT}
    
    public enum DataInspLotCertificateTrackActions{PRINT, OPEN, CHANGE_STATUS, SENT}
    
    public enum InspLotRMAPIactionsEndpoints implements EnumIntEndpoints{
        NEW_LOT("NEW_LOT", "createNewLot_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MATERIAL_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_TEMPLATE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_QUANTITY, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_QUANTITY_UOM, LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NUM_CONTAINERS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NUM_BULKS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 13),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 14),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 15),
            }, "NEW_LOT_CREATED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        LOT_ADD_ADHOC_BULKS("LOT_ADD_ADHOC_BULKS", "createLotCertificate_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NUM_ADHOC_BULKS, LPAPIArguments.ArgumentType.STRING.toString(), true, 7) 
        }, "LOT_ADHOC_BULKS_ADDED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        CREATE_LOT_CERTIFICATE("CREATE_LOT_CERTIFICATE", "createLotCertificate_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MATERIAL_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_TEMPLATE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9), 
        }, "LOT_CERTIFICATE_CREATED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        LOT_TAKE_DECISION("LOT_TAKE_DECISION", "lotTakeDecision_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_DECISION, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),
            },"LOT_DECISION_TAKEN", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        LOT_ALL_BULKS_TAKE_DECISION("LOT_ALL_BULKS_TAKE_DECISION", "lotAllBulksTakeDecision_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_BULK_DECISION, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),
            },"LOT_ALL_BULKS_DECISION_TAKEN", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        LOT_BULK_ADJUST_QUANTITY("LOT_BULK_ADJUST_QUANTITY", "LotBulkQuantityAdjusted_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BULK_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_QUANTITY, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8),                
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_QUANTITY_UOM, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 11)
        }, "LOT_BULK_QUANTITY_ADJUSTED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        LOT_BULK_ADJUST_SAMPLE_QUANTITY("LOT_BULK_ADJUST_SAMPLE_QUANTITY", "LotBulkSampleQuantityAdjusted_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BULK_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_QUANTITY, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8),                
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_QUANTITY_UOM, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 11)
        }, "LOT_BULK_SAMPLE_QUANTITY_ADJUSTED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),

        LOT_BULK_TAKE_DECISION("LOT_BULK_TAKE_DECISION", "LotBulkTakeDecision_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BULK_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_BULK_DECISION, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 11)                
        }, "LOT_BULK_DECISION_TAKEN", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        LOT_RETAIN_UNLOCK("LOT_RETAIN_UNLOCK", "lotRetainUnlock_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RETAIN_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
            }, "LOT_RETAIN_UNLOCKED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        LOT_RETAIN_LOCK("LOT_RETAIN_LOCK", "lotRetainLock_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RETAIN_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
            }, "LOT_RETAIN_LOCKED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        LOT_RETAIN_RECEPTION("LOT_RETAIN_RECEPTION", "lotRetainReception_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RETAIN_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
            }, "LOT_RETAIN_RECEIVED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),        
        LOT_RETAIN_MOVEMENT("LOT_RETAIN_MOVEMENT", "lotRetainMoved_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RETAIN_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NEW_LOCATION_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NEW_LOCATION_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 9),
            }, "LOT_RETAIN_MOVED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        LOT_RETAIN_EXTRACT("LOT_RETAIN_EXTRACT", "lotRetainMoved_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RETAIN_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_QUANTITY, LPAPIArguments.ArgumentType.BIGDECIMAL.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_QUANTITY_UOM, LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
            }, "LOT_RETAIN_EXTRACTED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        ;
        private InspLotRMAPIactionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, String actNameForAudit, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums; 
            this.auditActionName=actNameForAudit;
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
        @Override        public String getApiUrl(){return GlobalVariables.ApiUrls.INSPLOT_RM_ACTIONS.getUrl();}
        public String getAuditActionName(){return this.auditActionName;}           
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final String auditActionName;
        private final JsonArray outputObjectTypes;
    }
    
    public enum InspLotRMQueriesAPIEndpoints implements EnumIntEndpoints{
        GET_LOT_INFO("GET_LOT_INFO", "get_lot_info_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_SAMPLES, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_MATERIAL, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 9)},
                EndPointsToRequirements.endpointWithNoOutputObjects),
        GET_LOT_SAMPLES_INFO("GET_LOT_SAMPLES_INFO", "get_lot_samples_info_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_SAMPLE_ANALYSIS, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_SAMPLE_ANALYSIS_RESULTS, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 9)},
                EndPointsToRequirements.endpointWithNoOutputObjects),
        GET_LOT_AUDIT("GET_LOT_AUDIT", "",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_AUDIT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7)}, null),        
        ;
        private InspLotRMQueriesAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
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
        @Override        public String getApiUrl(){return GlobalVariables.ApiUrls.INSPLOT_RM_ACTIONS.getUrl();}
        private final String name;
        private final String successMessageCode; 
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }    
    
}
