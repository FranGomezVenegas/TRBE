/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.formulation.definition;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_FORMULA_NAME;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_NAME;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_PROJECT_NAME;
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
import module.formulation.definition.TblsFormulationData.TablesFormulationData;
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
public class FormulationEnums {

    public enum InvReferenceStockControlTypes {
        QUANTITY, ITEMS
    }
    public enum AppConfigInventoryTrackingAuditEvents implements EnumIntAuditEvents {
        REFERENCE_UPDATED, REFERENCE_CREATED
    }
    public enum AppFormulationAuditEvents implements EnumIntAuditEvents {
        FORMULA_CREATION, CLOSED_FORMULA, REOPENED_FORMULA, UPDATED_FORMULA,
        ADDED_INGREDIENT, UPDATED_INGREDIENT, REMOVED_INGREDIENT,
        ADDED_ATTACHMENT, REMOVED_ATTACHMENT, REACTIVATED_ATTACHMENT   

    }

    public static final String INVENTORY_LOT_CAT = "inventoryLot";

    public enum FormulationAPIactionsEndpoints implements EnumIntEndpoints {
        NEW_FORMULA("NEW_FORMULA", null, "", "formulationNewFormulaCreated_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_FORMULA_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_PROJECT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                    new LPAPIArguments("purpose", LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                    new LPAPIArguments("formula_template", LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                    new LPAPIArguments("ingredientsList", LPAPIArguments.ArgumentType.STRING.toString(), false, 10, null, null, null),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 12)},                    
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
        UPDATE_FORMULA("UPDATE_FORMULA", INVENTORY_LOT_CAT, "", "formulationFormulaUpdateIngredient_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_FORMULA_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8)},                    
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build())
                        .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                            .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA_INGREDIENTS.getTableName()).build())
                        .build(),
                null, null),
        CLOSE_FORMULA("CLOSE_FORMULA", null, "", "formulationNewFormulaClosed_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_FORMULA_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_PROJECT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9)
                },                    
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
        FORMULA_ADD_INGREDIENT("FORMULA_ADD_INGREDIENT", INVENTORY_LOT_CAT, "", "formulationFormulaAddIngredient_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_FORMULA_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("ingredient", LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments("quantity", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("quantityUom", LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 11)                    
                    },
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build())
                        .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                            .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA_INGREDIENTS.getTableName()).build())
                        .build(),
                null, null),
        FORMULA_UPDATE_INGREDIENT("FORMULA_UPDATE_INGREDIENT", INVENTORY_LOT_CAT, "", "formulationFormulaUpdateIngredient_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_FORMULA_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("ingredient", LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments("quantity", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("quantityUom", LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 11)
                    },
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build())
                        .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                            .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA_INGREDIENTS.getTableName()).build())
                        .build(),
                null, null),
        FORMULA_REMOVE_INGREDIENT("FORMULA_REMOVE_INGREDIENT", INVENTORY_LOT_CAT, "", "formulationFormulaRemoveIngredient_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_FORMULA_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("ingredient", LPAPIArguments.ArgumentType.STRING.toString(), true, 7)
                    },
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build())
                        .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                            .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA_INGREDIENTS.getTableName()).build())
                        .build(),
                null, null),

        /*        TURN_LOT_UNAVAILABLE("TURN_LOT_UNAVAILABLE", INVENTORY_LOT_CAT, "", "formulationFormulaTurnUnavailable_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 19),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 20),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
        UPDATE_LOT("UPDATE_LOT", REQUEST_PARAM_LOT_NAME, "", "inventoryLotUpdated_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 11),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
        RETIRE_LOT("RETIRE_LOT", REQUEST_PARAM_LOT_NAME, "", "inventoryLotRetired_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 19),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 20),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
        UNRETIRE_LOT("UNRETIRE_LOT", REQUEST_PARAM_LOT_NAME, "", "inventoryLotUnRetired_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
        COMPLETE_QUALIFICATION("COMPLETE_QUALIFICATION", REQUEST_PARAM_LOT_NAME, "", "inventoryLotQualificationCompleted_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("decision", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("turn_available_lot", LPAPIArguments.ArgumentType.STRING.toString(), false, 10),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 12)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
        REOPEN_QUALIFICATION("REOPEN_QUALIFICATION", REQUEST_PARAM_LOT_NAME, "", "inventoryLotQualificationReopened_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
        CONSUME_INV_LOT_QUANTITY("CONSUME_INV_LOT_QUANTITY", REQUEST_PARAM_LOT_NAME, "", "inventoryLotConsumed_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(REQUEST_PARAM_QUANTITY, LPAPIArguments.ArgumentType.BIGDECIMAL.toString(), false, 9, null, null, LPAPIArgumentsSpecialChecks.specialCheckersList.NONEGATIVEVALUE),
                    new LPAPIArguments(REQUEST_PARAM_QUANTITY_UOM, LPAPIArguments.ArgumentType.STRING.toString(), false, 10)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
        ADD_INV_LOT_QUANTITY("ADD_INV_LOT_QUANTITY", REQUEST_PARAM_LOT_NAME, "", "inventoryLotadded_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(REQUEST_PARAM_QUANTITY, LPAPIArguments.ArgumentType.BIGDECIMAL.toString(), false, 9, null, null, LPAPIArgumentsSpecialChecks.specialCheckersList.NONEGATIVEVALUE),
                    new LPAPIArguments(REQUEST_PARAM_QUANTITY_UOM, LPAPIArguments.ArgumentType.STRING.toString(), false, 10)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
        ADJUST_INV_LOT_QUANTITY("ADJUST_INV_LOT_QUANTITY", REQUEST_PARAM_LOT_NAME, "", "inventoryLotAdjusted_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments(REQUEST_PARAM_QUANTITY, LPAPIArguments.ArgumentType.BIGDECIMAL.toString(), false, 9, null, null, LPAPIArgumentsSpecialChecks.specialCheckersList.NONEGATIVEVALUE),
                    new LPAPIArguments(REQUEST_PARAM_QUANTITY_UOM, LPAPIArguments.ArgumentType.STRING.toString(), false, 10)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
        ENTER_EVENT_RESULT("ENTER_EVENT_RESULT", REQUEST_PARAM_LOT_NAME, "", "eventValueEntered_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                    new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                    new LPAPIArguments("qualifId", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 9),
                    new LPAPIArguments("variableName", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments("newValue", LPAPIArguments.ArgumentType.STRING.toString(), true, 11),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
        REENTER_EVENT_RESULT("REENTER_EVENT_RESULT", REQUEST_PARAM_LOT_NAME, "", "eventValueReentered_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("qualifId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9),
                    new LPAPIArguments("variableName", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments("newValue", LPAPIArguments.ArgumentType.STRING.toString(), true, 11),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
*/        
        FORMULAAUDIT_SET_AUDIT_ID_REVIEWED("FORMULAAUDIT_SET_AUDIT_ID_REVIEWED", REQUEST_PARAM_FORMULA_NAME, "", "formulaAuditIdReviewed_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_FORMULA_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("auditId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
/*        
        CONFIG_ADD_REFERENCE("CONFIG_ADD_REFERENCE", REQUEST_PARAM_LOT_NAME, "", "configAddReference",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments("lotRequiresQualif", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 8),
                    new LPAPIArguments("minStock", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 9),
                    new LPAPIArguments("minStockUom", LPAPIArguments.ArgumentType.STRING.toString(), false, 10),
                    new LPAPIArguments("allowedUoms", LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                    new LPAPIArguments("minStockType", LPAPIArguments.ArgumentType.STRING.toString(), false, 12),
                    new LPAPIArguments("requiresAvailableForUse", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 13),
                    new LPAPIArguments("minAvailablesForUse", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 14),
                    new LPAPIArguments("minAvailablesForUseType", LPAPIArguments.ArgumentType.STRING.toString(), false, 15),
                    new LPAPIArguments("allowedOpeningSomeAtaTime", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 16),
                    new LPAPIArguments("qualificationVariablesSet", LPAPIArguments.ArgumentType.STRING.toString(), false, 17)
                },
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),
        CONFIG_UPDATE_REFERENCE("CONFIG_UPDATE_REFERENCE", "referenceName", "", "configReferenceUpdated_success",
                new LPAPIArguments[]{new LPAPIArguments("referenceName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 9)
                }, 
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                null, null),  
*/        
        ADD_ATTACHMENT("ADD_ATTACHMENT", REQUEST_PARAM_FORMULA_NAME, "", "attachmentAdded_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_FORMULA_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments("fileUrl", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                new LPAPIArguments("briefSummary", LPAPIArguments.ArgumentType.STRING.toString(), false, 11)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                "Provides the ability for adding attachment for a given lot or even for a given event if the event id (optional) is added as part of the request", null),        
        REMOVE_ATTACHMENT("REMOVE_ATTACHMENT", REQUEST_PARAM_FORMULA_NAME, "", "attachmentRemoved_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_FORMULA_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments("attachmentId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 10)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                "Provides the ability for removing attachment for a given lot or even for a given event if the event id (optional) is added as part of the request", null),        
        REACTIVATE_ATTACHMENT("REACTIVATE_ATTACHMENT", REQUEST_PARAM_FORMULA_NAME, "", "attachmentReactivated_success",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_FORMULA_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments("attachmentId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 10)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesFormulationData.FORMULA.getTableName()).build()).build(),
                "Provides the ability for reactivate one previously removed attachment for a given lot or even for a given event if the event id (optional) is added as part of the request", null),        
        
        ;
        private FormulationAPIactionsEndpoints(String name, String mandatoryParams, String optionalParams, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name = name;
            this.mandatoryParams = mandatoryParams;
            this.optionalParams = optionalParams;
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
        @Override public String getEntity() {return "inventory_lot";}
        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getApiUrl() {
            return GlobalVariables.ApiUrls.INVENTORY_TRACKING_ACTIONS.getUrl();
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
        public String getDeveloperComment() {
            return this.devComment;
        }

        @Override
        public String getDeveloperCommentTag() {
            return this.devCommentTag;
        }
        private final String name;
        private final String successMessageCode;
        private final String mandatoryParams;
        private final String optionalParams;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
        private final String devComment;
        private final String devCommentTag;
    }

    public enum FormulationAPIqueriesEndpoints implements EnumIntEndpoints {
        ACTIVE_FORMULAS("ACTIVE_FORMULAS", "",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 6),
                    new LPAPIArguments("project", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
/*        ALL_INVENTORY_REFERENCES("ALL_INVENTORY_REFERENCES", "",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 6),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        RETIRED_INVENTORY_LOTS_LAST_N_DAYS("RETIRED_INVENTORY_LOTS_LAST_N_DAYS", "",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_NUM_DAYS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 6),
                    new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8)},
                EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        AUDIT_FOR_GIVEN_INVENTORY_LOT("AUDIT_FOR_GIVEN_INVENTORY_LOT", "",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        QUALIFICATIONS_INPROGRESS("QUALIFICATIONS_INPROGRESS", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 6),
            new LPAPIArguments("fielValue", LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        QUALIFIFICATION_EVENT_VARIABLES("QUALIFIFICATION_EVENT_VARIABLES", "", new LPAPIArguments[]{
            new LPAPIArguments("lotQualifId", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 6),
            new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
            new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
            new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                "This endpoint requires the object to get its variables, by lot or by qualifification. "
                + "In case of lot_qualif then qualifId is the required argument."
                + "In case of lot then requires lotName, category and reference.", null),
        EXPIRED_LOTS("EXPIRED_LOTS", "",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                    new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                    new LPAPIArguments(REQUEST_PARAM_NUM_DAYS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 10),
                    new LPAPIArguments(TblsInvTrackingData.Lot.EXPIRY_DATE.getName().toLowerCase() + "_start", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                    new LPAPIArguments(TblsInvTrackingData.Lot.EXPIRY_DATE.getName().toLowerCase() + "_end", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12)
                }, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        REFERENCES_UNDER_MIN_STOCK("REFERENCES_UNDER_MIN_STOCK", "",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8)}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN_STOCK("REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN_STOCK", "",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8)}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        REFERENCE_WITH_CONTROL_ISSUES("REFERENCE_WITH_CONTROL_ISSUES", "",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                    new LPAPIArguments(TblsData.CertifUserAnalysisMethod.CERTIFICATION_DATE.getName().toLowerCase() + "_start", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                    new LPAPIArguments(TblsData.CertifUserAnalysisMethod.CERTIFICATION_DATE.getName().toLowerCase() + "_end", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10)
                }, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        LOT_PRINT_LABEL("LOT_PRINT_LABEL", "",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        COMPLETED_EVENTS_LAST_N_DAYS("COMPLETED_EVENTS_LAST_N_DAYS", "",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_NUM_DAYS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 6),
                    new LPAPIArguments(REQUEST_PARAM_CATEGORY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_REFERENCE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8)},
                EndPointsToRequirements.endpointWithNoOutputObjects, null, null),
        OPEN_INVESTIGATIONS("OPEN_INVESTIGATIONS", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        INVESTIGATION_QUALIFICATIONS_PENDING_DECISION("INVESTIGATION_QUALIFICATIONS_PENDING_DECISION", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION("INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION", "", new LPAPIArguments[]{new LPAPIArguments(ParamsList.INVESTIGATION_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        INVESTIGATION_RESULTS_PENDING_DECISION("INVESTIGATION_RESULTS_PENDING_DECISION", "", 
                new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null),
        GET_LOT_ATTACHMENTS("GET_LOT_ATTACHMENTS", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("lotQualifId", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                "Provides the ability to get all attachments for a given instrument or even for a given event if the event id (optional) is added as part of the request", null),                
*/
        ;
        private FormulationAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes,
                String devComment, String devCommentTag) {
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
        @Override public String getEntity() {return "inventory_lot";}
        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getApiUrl() {
            return GlobalVariables.ApiUrls.INVESTIGATIONS_QUERIES.getUrl();
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

    public enum FormulationBusinessRules implements EnumIntBusinessRules {
        REVISION_MODE("inventoryAuditRevisionMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        AUTHOR_CAN_REVIEW_AUDIT_TOO("inventoryAuditAuthorCanBeReviewerToo", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        CHILD_REVISION_REQUIRED("inventoryAuditChildRevisionRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        CORRECTIVE_ACTION_FOR_REJECTED_QUALIFICATION("inventoryCreateCorrectiveActionForRejectedQualification", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null);

        private FormulationBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator,
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

    public enum FormulationErrorTrapping implements EnumIntMessages {
        REFERENCE_NOT_FOUND("inventoryTrackingReferenceNotFound", "The inventory reference <*1*> is not found in procedure <*2*>", "La referencia de inventario <*1*> no se ha encontrado para el proceso <*2*>"),
        UOM_NOT_INTHELIST("InventoryTracking_UnitNotPartOfAllowedList", "", ""),
        ALREADY_HAS_PENDING_QUALIFICATION("InventoryLotAlreadyHasPendingQualification", "The lot <*1*> already has one pending qualification in progress in this moment", "El lote <*1*> tiene actualmente una cualificación en curso en este momento"),
        NO_PENDING_QUALIFICATION("InventoryLotHasNoPendingQualification", "The lot <*1*> has no pending qualification in progress in this moment", "El lote <*1*> no tiene ninguna cualificación en curso en este momento"),
        QUALIFICATION_NOT_CLOSED("inventoryLotQualificationNotClosed", "", ""),
        ALREADY_AVAILABLE("InventoryLotAlreadyAvailable", "The Lot <*1*> is currently available", "El Lote <*1*> está actualmente disponible"),
        ALREADY_UNAVAILABLE("InventoryLotAlreadyUnAvailable", "The Lot <*1*> is currently available", "El Lote <*1*> está actualmente disponible"),
        ALREADY_EXISTS("InventoryLotAlreadyExists", "", ""),
        INGREDIENT_ALREADY_EXISTS("formulationIngredientAlreadyExists", "", ""),
        ALREADY_RETIRED("instrumentAlreadyRetired", "Inventory lot <*1*> already retired", "Lote <*1*> ya fue retirado"),
        NO_LOT_QUANTITY_SPECIFIED_AND_REQUIRED("inventoryTrackingNoLotVolumeSpecifiedAndRequired", "", ""),
        INV_LOT_HAS_NOT_ENOUGH_QUANTITY("InventoryLotHasNotEnoughVolume", "The lot <*1*> has not enough volume, <*2*>, for a consume of <*3*> <*4*>", "El lote <*1*> no tiene suficiente volumen, <*2*>, para consumir <*3*> de <*4*>"),
        NOT_FOUND("InventoryLotNotFound", "The Lot <*1*> is not found in procedure <*2*>", "El Lote <*1*> no se ha encontrado para el proceso <*2*>"),
        NOT_AVAILABLE("InventoryLotNotAvailable", "The Lot <*1*> is not currently available", "El Lote <*1*> no está actualmente disponible"),
        NOT_ALREADY_CLOSED("InventoryLotNotAlreadyClosed", "The Lot <*1*> is not currently retired", "El Lote <*1*> no está actualmente no retirado"),
        LOT_NOTQUALIFIED_YET("InventoryLotNotQualifiedYet", "", ""),
        INV_LOT_HAS_NO_QUANTITY_SET("InventoryLotHasNoVolumeSet", "The lot <*1*> has no volume set", "El lote <*1*> no tiene volumen asignado"),
        IS_LOCKED("InventoryLotIsLocked", "The lot <*1*> is locked, the reason is <*2*>", "El lote <*1*> está actualmente bloqueado, la razón es <*2*>"),
        WRONG_DECISION("InventoryLotWrongDecision", "Wrong Decision <*1*>, it is not one of the accepted values(<*2*>)", "wrongDecision <*1*> is not one of the accepted values(<*2*>)"),
        TRYINGUPDATE_RESERVED_FIELD("InventoryLotTryingToUpdateReservedField", "Not allowed to update the reserved field <*1*>", "No permitido modificar el campo reservado <*1*>"),
        REFERENCE_NOT_ALLOWED_TO_CONSUME_EXTERNALLY("InventoryLotReferenceNotConsumableExternally", "", ""),
        PROCEDURE_NOT_DECLARED_IN_AUTHORIZED_FOR_CONSUME_EXTERNALLY("InventoryLotProcedureNotDeclaredAsReferenceConsumableExternally", "", ""),
        REFERENCE_LOT_OR_USE_OPEN_REFERENCE_LOT_SHOULDBESPECIFIED("InventoryLotReferenceLotNameOrUseOpenReferenceLotShouldBeSpecified", "", ""),
        MORE_THAN_ONE_OPEN_REFERENCE_LOT("InventoryLotMoreThanOneOpenReferenceLots", "", ""),
        QUANTITY_IS_ALREADY_THIS("InventoryLotVolumeIsAlreadyThis", "", ""),
        MORE_THAN_ONE_VARIABLE("inventoryLotEvent_moreThanOneVariable", "Found more than one record, <*1*> for the query <*2*> on <*3*>", "Found more than one record, <*1*> for the query <*2*> on <*3*>"),
        EVENT_NOT_OPEN_FOR_CHANGES("inventoryLotEvent_NotOpenedForChanges", "The event is not open for changes", "Evento no abierto a cambios"),
        VARIABLE_NOT_EXISTS_EVENT_WITHNOVARIABLES("inventoryLotEvent_variableNotExists_eventWithNoVariables", "This event has no this variable and the event has no variables", "Este evento no contiene esta variable y el evento no tiene variables"),
        VARIABLE_NOT_EXISTS("inventoryLotEvent_variableNotExists", "The parameter <*1*> is not one of the event parameters <*2*>", "El parámetro <*1*> no es uno de los que tiene el evento, <*2*>"),
        EVENT_HAS_PENDING_RESULTS("inventoryLotEvent_eventWithPendingResults", "The event has <*1*> pending results", "El evento tiene <*1*> resultado(s) pendiente(s)"),
        EVENT_NOTHING_PENDING("inventoryLotEvent_eventHasMothingPending", "The event has nothing pending", "El evento no tiene nada pendiente"),
        VARIABLE_VALUE_NOTONEOFTHEEXPECTED("inventoryLotEvent_valueNotOneOfExpected", "The value <*1*> is not one of the accepted values <*2*> for variable <*3*> in procedure <*4*>", "The value <*1*> is not one of the accepted values <*2*> for variable <*3*> in procedure <*4*>"),
        VARIABLE_TYPE_NOT_RECOGNIZED("variableTypeNotRecognized", "", ""),
        NOT_NUMERIC_VALUE("DataSampleAnalysisResult_ValueNotNumericForQuantitativeParam", "", ""),
        USE_REENTER_WHEN_PARAM_ALREADY_HAS_VALUE("inventoryLotEvent_whenParamAlreadyHasValueRequiresToUseReenterAction", "", ""),
        USE_ENTER_WHEN_PARAM_HAS_NO_VALUE("inventoryLotEvent_whenParamHasNoValueRequiresToUseEnterResultAction", "", ""),
        AUDIT_RECORDS_PENDING_REVISION("inventoryLotAuditRecordsPendingRevision", "The sample <*1*> has pending sign audit records.", "La muestra <*1*> tiene registros de auditoría sin firmar"),
        AUDIT_RECORD_NOT_FOUND("AuditRecordNotFound", "The audit record <*1*> for sample does not exist", "No encontrado un registro de audit para muestra con id <*1*>"),
        AUDIT_RECORD_ALREADY_REVIEWED("AuditRecordAlreadyReviewed", "The audit record <*1*> was reviewed therefore cannot be reviewed twice.", "El registro de audit para muestra con id <*1*> ya fue revisado, no se puede volver a revisar."),
        AUTHOR_CANNOT_BE_REVIEWER("AuditSamePersonCannotBeAuthorAndReviewer", "Same person cannot review its own actions", "La misma persona no puede revisar sus propias acciones"),
        PARAMETER_MISSING("inventoryLotAuditRevisionMode_ParameterMissing", "", ""),
        DISABLED("inventoryLotAuditRevisionMode_Disable", "", ""),;

        private FormulationErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
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

    public enum FormulationLockingReasons {
        UNDER_CALIBRATION_EVENT("instrLockedByCalibrationInProgress"),
        UNDER_MAINTENANCE_EVENT("instrLockedByMaintenanceInProgress"),
        UNDER_SERVICE_EVENT("instrLockedByServiceInProgress"),
        UNDER_DAILY_VERIF_EVENT("instrLockedByDailyVerificationInProgress"),;

        FormulationLockingReasons(String propName) {
            this.propName = propName;
        }

        public String getPropertyName() {
            return this.propName;
        }
        private final String propName;
    }
}
