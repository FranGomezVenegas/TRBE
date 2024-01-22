/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules.masterdata.analysis;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_CODE;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_CONFIG_VERSION;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE;
import com.labplanet.servicios.app.InvestigationAPI;
import databases.TblsCnfg;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.ArrayList;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
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
public class MasterDataAnalysisEnums {

    public enum InspectionLotRMClousureTypes {
        BULK_INSPECTION_REJECTED
    }

    public enum MasterDataAnalysisAuditEvents implements EnumIntAuditEvents {
        ANALYSIS_CREATION, ANALYSIS_UPDATED, ANALYSIS_METHOD_ADDED, ANALYSIS_METHOD_REMOVED, ANALYSIS_REACTIVATED, ANALYSIS_DEACTIVATED, ANALYSIS_APPROVED_FOR_USE
    }

    public enum DataLotProperties {
        SUFFIX_STATUS_FIRST("_statusFirst", "First status, to be concatenated to the entity name, example: sample_statusFirst, program_statusFirst etc...", "One of the given statuses"),
        SUFFIX_LOTSTRUCTURE("_lotStructure", "TBD", "TBD"), /*        SAMPLE_STATUS_FIRST ("sample_statusFirst","", "One of the given statuses"),
        SAMPLE_STATUS_RECEIVED ("sample_statusReceived", "", "One of the given statuses"),
        SAMPLE_STATUS_INCOMPLETE ("sample_statusIncomplete", "", "One of the given statuses"),
        SAMPLE_STATUS_COMPLETE ("sample_statusComplete", "", "One of the given statuses"),
        SAMPLEALIQUOTING_VOLUME_REQUIRED ("sampleAliquot_volumeRequired", "TBD", "TBD"),
        SAMPLEASUBLIQUOTING_VOLUME_REQUIRED ("sampleSubAliquot_volumeRequired", "TBD", "TBD"), */;

        private DataLotProperties(String pName, String descr, String possValues) {
            this.propertyName = pName;
            this.description = descr;
            this.possibleValues = possValues;
        }

        public String getPropertyName() {
            return this.propertyName;
        }

        public String getDescription() {
            return this.description;
        }

        public String getPossibleValues() {
            return this.possibleValues;
        }

        private final String propertyName;
        private final String description;
        private final String possibleValues;
    }

    public enum MasterDataAnalysisErrorTrapping implements EnumIntMessages {
        ANALYSIS_NOT_FOUND("analysisNotFound", "", ""),
        METHOD_NOT_FOUND("methodNotFound", "", ""),
        ANALYSIS_METHOD_NOT_FOUND("analysisMethodNotFound", "", ""),
        
        ;
        private MasterDataAnalysisErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
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

    public enum DataInspLotCertificateStatuses {
        NEW, DRAFT, READY_FOR_APPROVAL, UNDER_APPROVAL, APPROVED, SENT
    }

    public enum DataInspLotCertificateTrackActions {
        PRINT, OPEN, CHANGE_STATUS, SENT
    }

    public enum MasterDataAnalysisActionsEndpoints implements EnumIntEndpoints {
        ANALYSIS_NEW("ANALYSIS_NEW", "analysisNew_success",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                    
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 8)}, MasterDataAnalysisAuditEvents.ANALYSIS_CREATION,
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                    .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.ANALYSIS.getTableName()).build()).build(),
            null, null),
        ANALYSIS_ADD_METHOD("ANALYSIS_ADD_METHOD", "analysisMethodAdded_success",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments("methodName", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments("methodVersion", LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments("expiryIntervalInfo", LPAPIArguments.ArgumentType.STRING.toString(), false, 10),}, MasterDataAnalysisAuditEvents.ANALYSIS_METHOD_ADDED,
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                    .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.ANALYSIS.getTableName()).build()).build(),
            null, null),
        ANALYSIS_REMOVE_METHOD("ANALYSIS_REMOVE_METHOD", "analysisMethodRemoved_success",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments("methodName", LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, MasterDataAnalysisAuditEvents.ANALYSIS_METHOD_REMOVED,
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                    .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.ANALYSIS.getTableName()).build()).build(),
            null, null),
        ANALYSIS_ADD_PARAM("ANALYSIS_ADD_PARAM", "analysisParamAdded_success",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments("methodName", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments("paramName", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                new LPAPIArguments("paramType", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                new LPAPIArguments("numReplicas", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 11),
                new LPAPIArguments("uom", LPAPIArguments.ArgumentType.STRING.toString(), false, 12),
                new LPAPIArguments("uomConversionMode", LPAPIArguments.ArgumentType.STRING.toString(), false, 13),
                new LPAPIArguments("calcLinked", LPAPIArguments.ArgumentType.STRING.toString(), false, 14),
                new LPAPIArguments("listEntry", LPAPIArguments.ArgumentType.STRING.toString(), false, 15),
                new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 16),
                new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 17)}, MasterDataAnalysisAuditEvents.ANALYSIS_METHOD_REMOVED,
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                    .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.ANALYSIS.getTableName()).build()).build(),
            null, null),
        ANALYSIS_UPDATE("ANALYSIS_UPDATE", "analysisNew_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 9)}, MasterDataAnalysisAuditEvents.ANALYSIS_UPDATED,
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.ANALYSIS.getTableName()).build()).build(),
                null, null),
        ANALYSIS_REACTIVATE("ANALYSIS_REACTIVATE", "analysisReactivate_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 9)}, MasterDataAnalysisAuditEvents.ANALYSIS_REACTIVATED,
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.ANALYSIS.getTableName()).build()).build(),
                null, null),
        ANALYSIS_DEACTIVATE("ANALYSIS_DEACTIVATE", "analysisDeactivate_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 9)}, MasterDataAnalysisAuditEvents.ANALYSIS_DEACTIVATED,
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.ANALYSIS.getTableName()).build()).build(),
                null, null),
        ANALYSIS_APPROVAL_FOR_USE("ANALYSIS_APPROVAL_FOR_USE", "analysisApprovalForUse_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 9)}, MasterDataAnalysisAuditEvents.ANALYSIS_APPROVED_FOR_USE,
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.ANALYSIS.getTableName()).build()).build(),
                null, null),
        ;

        private MasterDataAnalysisActionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, EnumIntAuditEvents actNameForAudit, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name = name;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.auditActionName = actNameForAudit;
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
        @Override public String getEntity() {return "inspection_lot";}
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
            return GlobalVariables.ApiUrls.INSPLOT_RM_ACTIONS.getUrl();
        }

        public EnumIntAuditEvents getAuditActionName() {
            return this.auditActionName;
        }
        private final String name;
        private final String successMessageCode;
        private final LPAPIArguments[] arguments;
        private final EnumIntAuditEvents auditActionName;
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

    public enum InspLotRMQueriesAPIEndpoints implements EnumIntEndpoints {
        GET_LOT_INFO("GET_LOT_INFO", "get_lot_info_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_SAMPLES, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 8),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_MATERIAL, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 9)},
                EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        GET_LOTS_PENDING_USAGE_DECISION("GET_LOTS_PENDING_USAGE_DECISION", "get_lots_pending_usage_decision_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),                    
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_MATERIAL, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 8)},
                EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        GET_LOT_SAMPLES_INFO("GET_LOT_SAMPLES_INFO", "get_lot_samples_info_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_SAMPLE_ANALYSIS, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 8),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_SAMPLE_ANALYSIS_RESULTS, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 9)},
                EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        GET_LOT_AUDIT("GET_LOT_AUDIT", "",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_AUDIT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7)}, null,
                null, null),
        OPEN_INVESTIGATIONS("OPEN_INVESTIGATIONS", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        INVESTIGATION_DEVIATION_PENDING_DECISION("INVESTIGATION_DEVIATION_PENDING_DECISION", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION("INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION", "", new LPAPIArguments[]{new LPAPIArguments(InvestigationAPI.ParamsList.INVESTIGATION_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        GET_MATERIALS("GET_MATERIALS", "", new LPAPIArguments[]{}, null, null, null),    
        GET_ANALYSIS("GET_ANALYSIS", "",
            new LPAPIArguments[]{new LPAPIArguments("code", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments("includeMethodsCertification", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 7),}, null, null, null),
        GET_SPECS("GET_SPECS", "",
            new LPAPIArguments[]{new LPAPIArguments("specCode", LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, null, null, null),
        GET_SAMPLE_ANALYSIS_RESULT_LIST("GET_SAMPLE_ANALYSIS_RESULT_LIST", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 11),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12), //new LPAPIArguments(EnvMonitAPIParams., LPAPIArguments.ArgumentType.STRING.toString(), false, 7)
        }, null, null, null),
        ;
        

        private InspLotRMQueriesAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
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
        @Override public String getEntity() {return "inspection_lot";}
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
            return GlobalVariables.ApiUrls.INSPLOT_RM_ACTIONS.getUrl();
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

    public enum InspLotRMBusinessRules implements EnumIntBusinessRules {
        REVISION_MODE("inspLotAuditRevisionMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        AUTHOR_CAN_REVIEW_AUDIT_TOO("inspLotAuditAuthorCanBeReviewerToo", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        CHILD_REVISION_REQUIRED("inspLotAuditChildRevisionRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        CORRECTIVE_ACTION_FOR_REJECTED_BULK("inspLotEventCreateCorrectiveActionForRejectedBulk", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null);

        private InspLotRMBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator,
                Boolean isOpt, ArrayList<String[]> preReqs) {
            this.tagName = tgName;
            this.areaName = areaNm;
            this.valuesList = valuesList;
            this.allowMultiValue = allowMulti;
            this.multiValueSeparator = separator;
            this.isOptional = isOpt;
            this.preReqs = preReqs;
        }

        @Override
        public String getTagName() {
            return this.tagName;
        }

        @Override
        public String getAreaName() {
            return this.areaName;
        }

        @Override
        public JSONArray getValuesList() {
            return this.valuesList;
        }

        @Override
        public Boolean getAllowMultiValue() {
            return this.allowMultiValue;
        }

        @Override
        public char getMultiValueSeparator() {
            return this.multiValueSeparator;
        }

        @Override
        public Boolean getIsOptional() {
            return isOptional;
        }

        @Override
        public ArrayList<String[]> getPreReqs() {
            return this.preReqs;
        }

        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;
        private final Boolean isOptional;
        private final ArrayList<String[]> preReqs;
    }

}
