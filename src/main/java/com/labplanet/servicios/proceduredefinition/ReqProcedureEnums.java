/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.proceduredefinition;

import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables.ApiUrls;

/**
 *
 * @author User
 */
public class ReqProcedureEnums {

    public enum ProcedureDefinitionpParametersEndpoints {
        /**
         *
         */
        PROCEDURE_NAME("procedureName"), PROCEDURE_VERSION("procedureVersion"), PROC_INSTANCENAME("procInstanceName"), DB_NAME("dbName"),
        MODULE_NAME("moduleName"), MODULE_VERSION("moduleVersion"), USER_NAME("userName"), ROLE_NAME("roleName"),
        UOM_NAME("uomName"), UOM_IMPORT_TYPE("importType"),
        CREATE_DATABASE("createDatabase"), CREATE_FILES("createFiles"), MAIN_PATH("mainPath"),
        NEW_FILE_CAMEL_LOWER("newFileCamelLower"), NEW_FILE_CAMEL("newFileCamel"),
        NEW_FILE_PROC_NAME("newFileProcName"), NEW_FILE_ALIAS_UNDERSCORE("newAliasUnderscore"),
        CREATE_CHECKPLATFORM_PROCEDURE("createCheckPlatformProcedure"),
        REMOVE_CHECKPLATFORM_PROCEDURE("removeCheckPlatformProcedure"),
        CREATE_REPOSITORIES_AND_PROC_TBLS("deployRepositoriesAndProcTbls"), DEPLOY_PROC_INFO("deployProcInfo"), DEPLOY_PROC_USER_ROLES("deployProcUserRoles"),
        DEPLOY_PROC_SOP_META_DATA("deployProcSopMetaData"), DEPLOY_PROC_SOPS_TO_USERS("deployProcSopsToUsers"), DEPLOY_PROC_EVENTS("deployProcEvents"),
        DEPLOY_PROC_BUSINESS_RULES_PROP_FILES("deployProcBusinessRulesPropFiles"), DEPLOY_MODULE_TABLES_AND_FIELDS("deployModuleTablesAndFields"),
        DEPLOY_PROC_MASTER_DATA("deployMasterData"),;

        private ProcedureDefinitionpParametersEndpoints(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
        private final String name;
    }

    public enum ProcedureDefinitionAPIActionsEndpoints implements EnumIntEndpoints {
        DEPLOY_REQUIREMENTS("DEPLOY_REQUIREMENTS", "deployRequirements_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DB_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.MODULE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.CREATE_REPOSITORIES_AND_PROC_TBLS.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_PROC_INFO.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 12),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_PROC_USER_ROLES.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 13),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_PROC_SOP_META_DATA.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 14),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_PROC_SOPS_TO_USERS.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 15),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_PROC_EVENTS.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 16),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_PROC_BUSINESS_RULES_PROP_FILES.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 17),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_MODULE_TABLES_AND_FIELDS.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 18),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_PROC_MASTER_DATA.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 19)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        PROC_DEPLOY_CHECKER("PROC_DEPLOY_CHECKER", "deployRequirements_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DB_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        DEPLOY_REQUIREMENTS_CLONE_SPRINT("DEPLOY_REQUIREMENTS_CLONE_SPRINT", "sprintRequirementsCloned_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("NEW" + ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("continueIfExistsNew", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 10)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        SET_PROCEDURE_BUSINESS_RULES("SET_PROCEDURE_BUSINESS_RULES", "setProcedureBusinessRules_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("suffixName", LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments("propName", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("propValue", LPAPIArguments.ArgumentType.STRING.toString(), false, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        ADD_USER("ADD_USER", "addUserToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.USER_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        ADD_ROLE_TO_USER("ADD_ROLE_TO_USER", "addRoleToUser_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.ROLE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.USER_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        GET_UOM("GET_UOM", "addRoleToUser_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.UOM_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.UOM_IMPORT_TYPE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10)}, EndPointsToRequirements.endpointWithNoOutputObjects
, null, null);
        private ProcedureDefinitionAPIActionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name = name;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.outputObjectTypes = outputObjectTypes;
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);
        }

        public HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex) {
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues = new Object[0];
            for (LPAPIArguments curArg : this.arguments) {
                argValues = LPArray.addValueToArray1D(argValues, curArg.getName() + ":" + getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }
            hm.put(request, argValues);
            return hm;
        }

        @Override        public String getName() {            return this.name;        }
        @Override        public String getSuccessMessageCode() {           return this.successMessageCode;        }
        @Override        public JsonArray getOutputObjectTypes() {            return outputObjectTypes;        }
        @Override        public LPAPIArguments[] getArguments() {            return arguments;        }
        @Override        public String getApiUrl() {            return ApiUrls.PROCEDURE_DEFINITION_ACTIONS.getUrl();        }
        @Override        public String getDeveloperComment() {return this.devComment;        }
        @Override public String getDeveloperCommentTag() {return this.devCommentTag;}
        private final String name;
        private final String successMessageCode;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
        private final String devComment;
        private final String devCommentTag;
    }

    public enum ReqProcedureDefinitionAPIQueriesEndpoints implements EnumIntEndpoints {
        ALL_PROCEDURES_AND_INSTANCE_LIST("ALL_PROCEDURES_AND_INSTANCE_LIST", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        ALL_USER_PROCEDURES_DEFINITION("ALL_USER_PROCEDURES_DEFINITION", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        ALL_PROCEDURES_DEFINITION("ALL_PROCEDURES_DEFINITION", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        ALL_PROCEDURE_DEFINITION("ALL_PROCEDURE_DEFINITION", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        ONE_PROCEDURE_DEFINITION("ONE_PROCEDURE_DEFINITION", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        ENABLE_ACTIONS_AND_ROLES("ENABLE_ACTIONS_AND_ROLES", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        ALL_PROCEDURE_TESTING_SCRIPT("ALL_PROCEDURE_TESTING_SCRIPT", "deployRequirements_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DB_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        PROC_DEPLOY_TESTING_COVERAGE_SUMMARY("PROC_DEPLOY_TESTING_COVERAGE_SUMMARY", "deployRequirements_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DB_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null);

        private ReqProcedureDefinitionAPIQueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name = name;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.outputObjectTypes = outputObjectTypes;
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);
        }

        public HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex) {
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
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
            return ApiUrls.PROCEDURE_DEFINITION_QUERIES.getUrl();
        }

        @Override
        public String getDeveloperComment() {
            return this.devComment;
        }

        @Override
        public String getDeveloperCommentTag() {
            return this.devCommentTag;
        }
        private final String name;
        private final String successMessageCode;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
        private final String devComment;
        private final String devCommentTag;

    }

    public enum ReqProcedureDefinitionErrorTraping implements EnumIntMessages {
        INSTANCE_LOCKED_FOR_ACTIONS("instanceLockedForActions", "", ""),;

        private ReqProcedureDefinitionErrorTraping(String errCode, String defaultTextEn, String defaultTextEs) {
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
