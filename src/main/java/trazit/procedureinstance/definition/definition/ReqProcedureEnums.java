/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.definition.definition;

import com.labplanet.servicios.app.GlobalAPIsParams;
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
        PROCEDURE_NAME("procedureName"), PROCEDURE_VERSION("procedureVersion"), PROC_INSTANCENAME("procInstanceName"), DB_NAME("dbName"),
        MODULE_NAME("moduleName"), MODULE_VERSION("moduleVersion"), USER_NAME("userName"), ROLE_NAME("roleName"),
        REQ_PARENT_CODE("requirementParentCode"), REQ_CODE("requirementCode"),
        REQUIREMENT_ID("requirementId"), RISK_ID("riskId"), SOLUTION_ID("solutionId"),
        COVERAGE_ID("coverageId"), SCRIPT_ID("scriptId"), 
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
        DEFINITION_CHECKER("DEFINITION_CHECKER", "checkedRequirements_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        SUGGEST_SPEC_LIMITS_TESTING("SUGGEST_SPEC_LIMITS_TESTING", "deployRequirements_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("spec", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("specVersion", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 10),
                    new LPAPIArguments("saveScript", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 11)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        ASSIGN_SCRIPT_TO_SPEC("ASSIGN_SCRIPT_TO_SPEC", "scriptAssignToSpec_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("spec", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("specVersion", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 10),
                    new LPAPIArguments("scriptId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 11)}, EndPointsToRequirements.endpointWithNoOutputObjects,
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
        NEW_PROCEDURE("NEW_PROCEDURE","newProcedureInstance_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments("new"+ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("moduleName", LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments("moduleVersion", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8),
                    new LPAPIArguments("labelEn", LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                    new LPAPIArguments("labelEs", LPAPIArguments.ArgumentType.STRING.toString(), false, 10),}, EndPointsToRequirements.endpointWithNoOutputObjects,
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
        REMOVE_USER("REMOVE_USER", "removeUserToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.USER_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        RENAME_USER("RENAME_USER", "renameUserToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.USER_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9), 
                    new LPAPIArguments("new"+ProcedureDefinitionpParametersEndpoints.USER_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        ADD_ROLE("ADD_ROLE", "addRoleToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.ROLE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        REMOVE_ROLE("REMOVE_ROLE", "removeRoleToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.ROLE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        RENAME_ROLE("RENAME_ROLE", "renameRoleToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.ROLE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("new"+ProcedureDefinitionpParametersEndpoints.ROLE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                }, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        CLONE_ROLE("CLONE_ROLE", "cloneRoleToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.ROLE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("new"+ProcedureDefinitionpParametersEndpoints.ROLE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                }, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        ADD_ROLE_TO_USER("ADD_ROLE_TO_USER", "addRoleToUser_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.USER_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.ROLE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        REMOVE_ROLE_TO_USER("REMOVE_ROLE_TO_USER", "removeRoleToUser_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.USER_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.ROLE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        ADD_SOP("ADD_SOP", "addSOP_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("sopName", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("sopVersion", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 10),
                    new LPAPIArguments("fileLink", LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 13)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        REMOVE_SOP("REMOVE_SOP", "removeSOP_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("sopName", LPAPIArguments.ArgumentType.STRING.toString(), true, 9)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        RENAME_SOP("RENAME_SOP", "renameSOP_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("sopName", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("new"+"sopName", LPAPIArguments.ArgumentType.STRING.toString(), true, 10)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        NEW_PARENT_USER_REQUIREMENT("NEW_PARENT_USER_REQUIREMENT", "newParentUserRequirementToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQ_PARENT_CODE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("description", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments("order_number", LPAPIArguments.ArgumentType.BIGDECIMAL.toString(), true, 11),
                    new LPAPIArguments("active", LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 12),
                    new LPAPIArguments("in_scope", LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 13),
                    new LPAPIArguments("in_system", LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 14)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,null, null),
        NEW_USER_REQUIREMENT("NEW_USER_REQUIREMENT", "newUserRequirementToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQ_PARENT_CODE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQ_CODE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments("description", LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                    new LPAPIArguments("order_number", LPAPIArguments.ArgumentType.BIGDECIMAL.toString(), true, 12),
                    new LPAPIArguments("active", LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 13),
                    new LPAPIArguments("in_scope", LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 14),
                    new LPAPIArguments("in_system", LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 15)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,null, null),
        UPDATE_INFO_USER_REQUIREMENT("UPDATE_INFO_USER_REQUIREMENT", "updateUserRequirementToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQ_PARENT_CODE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9), 
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQ_CODE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments("description", LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                    new LPAPIArguments("order_number", LPAPIArguments.ArgumentType.BIGDECIMAL.toString(), false, 12),
                    new LPAPIArguments("active", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 13),
                    new LPAPIArguments("in_scope", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 14),
                    new LPAPIArguments("in_system", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 15),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 16),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 17)
                    
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        UPDATE_INFO_PARENT_USER_REQUIREMENT("UPDATE_INFO_PARENT_USER_REQUIREMENT", "updateParentUserRequirementToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQ_PARENT_CODE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9), 
                    new LPAPIArguments("description", LPAPIArguments.ArgumentType.STRING.toString(), false, 10),
                    new LPAPIArguments("order_number", LPAPIArguments.ArgumentType.BIGDECIMAL.toString(), false, 11),
                    new LPAPIArguments("active", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 12),
                    new LPAPIArguments("in_scope", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 13),
                    new LPAPIArguments("in_system", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 14),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 15),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 16)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        REMOVE_PARENT_USER_REQUIREMENT("REMOVE_PARENT_USER_REQUIREMENT", "removeParentUserRequirementToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQ_PARENT_CODE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        REMOVE_USER_REQUIREMENT("REMOVE_USER_REQUIREMENT", "removeUserRequirementToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQ_PARENT_CODE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQ_CODE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        MOVE_USER_REQUIREMENT("MOVE_USER_REQUIREMENT", "moveUserRequirementToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQ_PARENT_CODE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9), 
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQ_CODE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments("new"+ProcedureDefinitionpParametersEndpoints.REQ_PARENT_CODE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                    new LPAPIArguments("new"+ProcedureDefinitionpParametersEndpoints.REQ_CODE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                    new LPAPIArguments("neworderNumber", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 11),
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        NEW_RISK("NEW_RISK", "newRiskToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQUIREMENT_ID.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9),
                    new LPAPIArguments("level", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments("comments", LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                    new LPAPIArguments("partOfTesting", LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 12),
                    new LPAPIArguments("expectedTestNames", LPAPIArguments.ArgumentType.STRING.toString(), true, 13)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        REMOVE_RISK("REMOVE_RISK", "removeRiskToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQUIREMENT_ID.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.RISK_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        UPDATE_RISK("UPDATE_RISK", "updateRiskToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQUIREMENT_ID.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.RISK_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10), 
                    new LPAPIArguments("level", LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                    new LPAPIArguments("comments", LPAPIArguments.ArgumentType.STRING.toString(), false, 12),
                    new LPAPIArguments("partOfTesting", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 13),
                    new LPAPIArguments("expectedTestNames", LPAPIArguments.ArgumentType.STRING.toString(), false, 14),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 15),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 16)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        SET_RISK_READY_FOR_REVISION("SET_RISK_READY_FOR_REVISION", "setRiskReadyForRevisionToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQUIREMENT_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.RISK_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        SIGN_RISK("SIGN_RISK", "signRiskToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQUIREMENT_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.RISK_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        NEW_REQUIREMENT_SOLUTION("NEW_REQUIREMENT_SOLUTION", "newRequirementSolutionToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQUIREMENT_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("type", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 12)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        ADD_BUSINESS_RULE_REQ_SOLUTION("ADD_BUSINESS_RULE_REQ_SOLUTION", "addBusinessRuleRequirementSolutionToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQUIREMENT_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("businessRuleArea", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments("businessRuleName", LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                    new LPAPIArguments("businessRuleValue", LPAPIArguments.ArgumentType.STRING.toString(), true, 12),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 14)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        ADD_WINDOW_BUTTON_REQ_SOLUTION("ADD_WINDOW_BUTTON_REQ_SOLUTION", "addWindowButtonRequirementSolutionToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQUIREMENT_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("actionType", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments("windowActionName", LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                    new LPAPIArguments("confirmDialog", LPAPIArguments.ArgumentType.STRING.toString(), false, 12),
                    new LPAPIArguments("confirmDialogDetail", LPAPIArguments.ArgumentType.STRING.toString(), false, 13),
                    new LPAPIArguments("roleName", LPAPIArguments.ArgumentType.STRING.toString(), false, 14),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 15),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 16)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        ADD_SPECIAL_VIEW_REQ_SOLUTION("ADD_SPECIAL_VIEW_REQ_SOLUTION", "addSpecialViewnRequirementSolutionToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQUIREMENT_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("specialViewName", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments("windowName", LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                    new LPAPIArguments("roleName", LPAPIArguments.ArgumentType.STRING.toString(), false, 12),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 14)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        ADD_WINDOW_REQ_SOLUTION("ADD_WINDOW_REQ_SOLUTION", "addWindowRequirementSolutionToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQUIREMENT_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("windowName", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments("windowQuery", LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                    new LPAPIArguments("windowType", LPAPIArguments.ArgumentType.STRING.toString(), true, 12),
                    new LPAPIArguments("windowMode", LPAPIArguments.ArgumentType.STRING.toString(), true, 13),
                    new LPAPIArguments("roleName", LPAPIArguments.ArgumentType.STRING.toString(), true, 14),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 15),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 16)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        ADD_SPECIAL_WINDOW_REQ_SOLUTION("ADD_SPECIAL_WINDOW_REQ_SOLUTION", "addSpecialWindowRequirementSolutionToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQUIREMENT_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("specialWindowName", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments("windowMode", LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                    new LPAPIArguments("roleName", LPAPIArguments.ArgumentType.STRING.toString(), true, 12),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 14)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        REMOVE_REQ_SOLUTION("REMOVE_REQ_SOLUTION", "removeRequirementSolutionToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.SOLUTION_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        UPDATE_REQUIREMENT_SOLUTION("UPDATE_REQUIREMENT_SOLUTION", "updateRequirementSolutionToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.REQUIREMENT_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.SOLUTION_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 12)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),
        NEW_SCRIPT_TESTING("NEW_SCRIPT_TESTING", "newScriptTesting_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),                    
                    new LPAPIArguments("purpose", LPAPIArguments.ArgumentType.STRING.toString(), false, 9)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),        
        SCRIPT_SAVE_POINT("SCRIPT_SAVE_POINT", "newScriptTesting_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),                    
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.SCRIPT_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("tester", LPAPIArguments.ArgumentType.STRING.toString(), false, 10),
                    new LPAPIArguments("purpose", LPAPIArguments.ArgumentType.STRING.toString(), false, 11),                    
                    new LPAPIArguments("reviewer", LPAPIArguments.ArgumentType.STRING.toString(), false, 12),
                    new LPAPIArguments("conclusion", LPAPIArguments.ArgumentType.STRING.toString(), false, 13),
                    new LPAPIArguments("signed", LPAPIArguments.ArgumentType.STRING.toString(), false, 14),
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),        
        DELETE_SCRIPT_TESTING("DELETE_SCRIPT_TESTING", "deletedScriptTesting_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.SCRIPT_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),        
        SCRIPT_ADD_STEP("SCRIPT_ADD_STEP", "addedScriptStepTesting_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.SCRIPT_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("action", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 12),
                    new LPAPIArguments("expectedSyntaxis", LPAPIArguments.ArgumentType.STRING.toString(), false, 13),
                    new LPAPIArguments("expectedNotification", LPAPIArguments.ArgumentType.STRING.toString(), false, 14),                        
                    new LPAPIArguments("alternativeToken", LPAPIArguments.ArgumentType.STRING.toString(), false, 15)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),        
        SCRIPT_REMOVE_STEP("SCRIPT_REMOVE_STEP", "removedScriptStepTesting_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.SCRIPT_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("stepId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 10)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),        
        NEW_COVERAGE_TESTING("NEW_COVERAGE_TESTING", "newCoverageTesting_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("scriptIdsList", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("purpose", LPAPIArguments.ArgumentType.STRING.toString(), false, 10)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),        
        DELETE_COVERAGE_TESTING("DELETE_COVERAGE_TESTING", "deletedCoverageTesting_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.COVERAGE_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),        
        COVERAGE_REMOVE_SCRIPT("COVERAGE_REMOVE_SCRIPT", "removedScriptToCoverageTesting_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.COVERAGE_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.SCRIPT_ID.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 10)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),        
        COVERAGE_ADD_SCRIPT("COVERAGE_ADD_SCRIPT", "addedScriptToCoverageTesting_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.COVERAGE_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.SCRIPT_ID.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 10)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),        
        COVERAGE_EXCLUDE_ACTION("COVERAGE_EXCLUDE_ACTION", "coverageExcludeAction_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.COVERAGE_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("action", LPAPIArguments.ArgumentType.STRING.toString(), true, 10)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),        
        COVERAGE_UNEXCLUDE_ACTION("COVERAGE_UNEXCLUDE_ACTION", "coverageUnExcludeAction_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.COVERAGE_ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("action", LPAPIArguments.ArgumentType.STRING.toString(), true, 10)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,                null, null),        
        GET_UOM("GET_UOM", "addRoleToUser_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.UOM_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.UOM_IMPORT_TYPE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null);

        private ProcedureDefinitionAPIActionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
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
        @Override public String getEntity() {return "procedure_deployment";}
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
            return ApiUrls.PROCEDURE_DEFINITION_ACTIONS.getUrl();
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
                    new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DB_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        GET_ALL_ACTIVE_MODULES("GET_ALL_ACTIVE_MODULES", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        ;

        private ReqProcedureDefinitionAPIQueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
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
        @Override public String getEntity() {return "procedure_deployment";}
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
        MODULE_NOT_FOUND("procDefinition_moduleNotFound", "", ""),
        PROCEDURE_INSTANCE_ALREADY_EXISTS("procDefinition_procedureInstanceAlreadyExists", "", ""),
        INSTANCE_LOCKED_FOR_ACTIONS("procDefinition_instanceLockedForActions", "", ""),
        USER_NOT_FOUND("procDefinition_userNotFound", "", ""),
        USER_ALREADY_EXISTS("procDefinition_userAlreadyExists", "", ""),
        ROLE_NOT_FOUND("procDefinition_roleNotFound", "", ""),
        ROLE_ALREADY_EXISTS("procDefinition_roleAlreadyExists", "", ""),
        SOP_ALREADY_EXISTS("procDefinition_sopAlreadyExists", "", ""),
        SOP_NOT_FOUND("procDefinition_sopNotFound", "", ""),
        USER_REQUIREMENT_NOT_FOUND("procDefinition_userRequirementNotFound", "", ""),
        PARENT_USER_REQUIREMENT_NOT_FOUND("procDefinition_parentUserRequirementNotFound", "", ""),
        PARENT_USER_REQUIREMENT_HAS_CHILD("procDefinition_parentUserRequirementHasChild", "", ""),
        USER_REQUIREMENT_ALREADY_EXISTS("procDefinition_userRequirementAlreadyExists", "", ""),
        RISK_REQUIREMENT_NOT_FOUND("procDefinition_riskNotFound", "", ""),
        RISK_REQUIREMENT_ALREADY_EXISTS("procDefinition_riskAlreadyExists", "", ""),
        SOLUTION_REQUIREMENT_NOT_FOUND("procDefinition_solutionNotFound", "", ""),
        SOLUTION_REQUIREMENT_ALREADY_EXISTS("procDefinition_solutionAlreadyExists", "", ""),
        MODULE_BUSINESS_RULE_NOT_FOUND("procDefinition_moduleBusinessRuleNotFound", "", ""),
        MODULE_BUSINESS_ALREADY_PRESENT("procDefinition_moduleBusinessRuleAlreadyPresent", "", ""),
        MODULE_BUSINESS_VALUE_NOT_ALLOWED("procDefinition_moduleBusinessRuleValueNotAllowed", "", ""),
        BUSINESS_RULE_ALREADY_PART_OF_PROCEDURE("procDefinition_businessRuleAlreadyPartOfProcedure", "", ""),
        MODULE_VIEW_QUERY_NOT_FOUND("procDefinition_moduleViewQueryNotFound", "", ""),
        MODULE_WINDOW_BUTTON_NOT_FOUND("procDefinition_moduleWindowButtonNotFound", "", ""),
        MODULE_WINDOW_ACTION_TYPE_NOT_RECOGNIZED("procDefinition_moduleWindowActionTypeNotRecognized", "", ""),
        COVERAGE_NOT_FOUND("procDefinition_coverageNotFound", "", ""),
        COVERAGE_NOT_ACTIVE("procDefinition_coverageNotActive", "", ""),
        COVERAGE_ACTION_ALREADY_PRESENT_IN_EXCLUDED_LIST("procDefinition_coverageActionAlreadyPresentInExcludedList", "", ""),
        COVERAGE_ACTION_NOT_PRESENT_IN_EXCLUDED_LIST("procDefinition_coverageActionNotPresentInExcludedList", "", ""),
        COVERAGE_ACTION_ALREADY_ACTION_UPON_RISK_ASSESSMENT("procDefinition_coverageActionUponRiskAsessment", "", ""),

        ;
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
