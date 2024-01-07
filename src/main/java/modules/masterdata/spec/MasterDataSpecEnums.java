/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules.masterdata.spec;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_CODE;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_CONFIG_VERSION;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_SPEC_FIELD_NAME;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_SPEC_FIELD_VALUE;
import databases.TblsCnfg;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class MasterDataSpecEnums {


    public enum MasterDataSpecAuditEvents implements EnumIntAuditEvents {
        SPEC_CREATION, SPEC_UPDATED, SPEC_LIMIT_ADDED, SPEC_RULES_UPDATED
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

    public enum MasterDataSpecErrorTrapping implements EnumIntMessages {
        METHOD_NOT_FOUND("methodNotFound", "", ""),
        ANALYSIS_METHOD_NOT_FOUND("analysisMethodNotFound", "", ""),
        
        ;
        private MasterDataSpecErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
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

    public enum MasterDataSpecActionsEndpoints implements EnumIntEndpoints {
        SPEC_NEW("SPEC_NEW", "specNew_success",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                    
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 8)}, MasterDataSpecAuditEvents.SPEC_CREATION,
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                    .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.SPEC.getTableName()).build()).build(),
            null, null),
        UPDATE_SPEC_RULES("UPDATE_SPEC_RULES", "specRulesUpdated_success",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments("methodName", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments("methodVersion", LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments("expiryIntervalInfo", LPAPIArguments.ArgumentType.STRING.toString(), false, 10),}, MasterDataSpecAuditEvents.SPEC_RULES_UPDATED,
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                    .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.SPEC.getTableName()).build()).build(),
            null, null),
        SPEC_ADD_ANALYSIS("SPEC_ADD_ANALYSIS", "specAddAnalysis_success",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                    
                new LPAPIArguments("analysisName", LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, MasterDataSpecAuditEvents.SPEC_CREATION,
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                    .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.SPEC.getTableName()).build()).build(),
            null, null),
        SPEC_REMOVE_ANALYSIS("SPEC_REMOVE_ANALYSIS", "specAddAnalysis_success",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                    
                new LPAPIArguments("analysisName", LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, MasterDataSpecAuditEvents.SPEC_CREATION,
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                    .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.SPEC.getTableName()).build()).build(),
            null, null),
        SPEC_ADD_VARIATION_NAME("SPEC_ADD_VARIATION_NAME", "specAddAnalysis_success",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                    
                new LPAPIArguments("variationName", LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, MasterDataSpecAuditEvents.SPEC_CREATION,
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                    .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.SPEC.getTableName()).build()).build(),
            null, null),
        SPEC_REMOVE_VARIATION_NAME("SPEC_REMOVE_VARIATION_NAME", "specAddAnalysis_success",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                    
                new LPAPIArguments("variationName", LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, MasterDataSpecAuditEvents.SPEC_CREATION,
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                    .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.SPEC.getTableName()).build()).build(),
            null, null),
        
        SPEC_LIMIT_NEW("SPEC_LIMIT_NEW", "specLimitAdded_success",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments("methodName", LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, MasterDataSpecAuditEvents.SPEC_LIMIT_ADDED,
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                    .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.SPEC.getTableName()).build()).build(),
            null, null),
/*        ANALYSIS_ADD_PARAM("ANALYSIS_ADD_PARAM", "analysisParamAdded_success",
            new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments("methodName", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments("analysis", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                new LPAPIArguments("paramName", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                new LPAPIArguments("paramType", LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                new LPAPIArguments("numReplicas", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 11),
                new LPAPIArguments("uom", LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                new LPAPIArguments("uomConversionMode", LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                new LPAPIArguments("calcLinked", LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                new LPAPIArguments("listEntry", LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 13),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 14)}, MasterDataSpecAuditEvents.ANALYSIS_METHOD_REMOVED,
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                    .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.SPEC.getTableName()).build()).build(),
            null, null),*/
        SPEC_UPDATE("SPEC_UPDATE", "specUpdated_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CODE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                    new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 9)}, MasterDataSpecAuditEvents.SPEC_UPDATED,
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.CONFIG.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsCnfg.TablesConfig.SPEC.getTableName()).build()).build(),
                null, null),
        ;

        private MasterDataSpecActionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, EnumIntAuditEvents actNameForAudit, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
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


}
