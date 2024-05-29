/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.formulation.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.isTagValueOneOfEnableOnes;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import module.formulation.definition.FormulationEnums;
import module.formulation.definition.TblsFormulationData;
import module.formulation.definition.TblsFormulationData.TablesFormulationData;
import module.formulation.definition.TblsFormulationDataAudit;
import static module.formulation.logic.AppFormulaAudit.AppFormulaAudit;
import trazit.enums.EnumIntTableFields;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ProcedureRequestSession;
import trazit.session.InternalMessage;
import trazit.session.ResponseMessages;

/**
 *
 * @author Administrator
 */
public class DataFormulaIngredient {

    private DataFormulaIngredient() {
        throw new IllegalStateException("Utility class");
    }


    public static InternalMessage removeFormulaIngredient(DataFormulation formula, String ingredient) {
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        String[] fldNames = new String[]{TblsFormulationData.FormulaIngredients.ACTIVE.getName(), TblsFormulationData.FormulaIngredients.REMOVED.getName()};        
        Object[] fldValues = new Object[]{false, true};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsFormulationData.FormulaIngredients.FORMULA, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{formula.getFormulaName()}, "");
        sqlWhere.addConstraint(TblsFormulationData.FormulaIngredients.INGREDIENT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{ingredient}, "");
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(TablesFormulationData.FORMULA_INGREDIENTS,
                EnumIntTableFields.getTableFieldsFromString(TablesFormulationData.FORMULA_INGREDIENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length - 1].toString(), new Object[]{formula.getFormulaName()}, null);
        }
        AppFormulaAudit(FormulationEnums.AppFormulationAuditEvents.REMOVED_INGREDIENT,
                formula.getFormulaName(), TablesFormulationData.FORMULA_INGREDIENTS.getTableName(),
                ingredient, fldNames, fldValues, formula.getProjectName());
        return new InternalMessage(LPPlatform.LAB_TRUE, FormulationEnums.FormulationAPIactionsEndpoints.FORMULA_REMOVE_INGREDIENT, new Object[]{ingredient, formula.getFormulaName()}, formula.getFormulaName());
    }

    public static InternalMessage updateFormulaIngredient(DataFormulation formula, String ingredient, String[] fldNames, Object[] fldValues) {
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsFormulationData.FormulaIngredients.FORMULA, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{formula.getFormulaName()}, "");
        sqlWhere.addConstraint(TblsFormulationData.FormulaIngredients.INGREDIENT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{ingredient}, "");
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(TablesFormulationData.FORMULA_INGREDIENTS,
                EnumIntTableFields.getTableFieldsFromString(TablesFormulationData.FORMULA_INGREDIENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length - 1].toString(), new Object[]{formula.getFormulaName()}, null);
        }
        AppFormulaAudit(FormulationEnums.AppFormulationAuditEvents.UPDATED_INGREDIENT,
                formula.getFormulaName(), TablesFormulationData.FORMULA_INGREDIENTS.getTableName(),
                ingredient, fldNames, fldValues, formula.getProjectName());
        return new InternalMessage(LPPlatform.LAB_TRUE, FormulationEnums.FormulationAPIactionsEndpoints.FORMULA_REMOVE_INGREDIENT, new Object[]{ingredient, formula.getFormulaName()}, formula.getFormulaName());
    }
    
    public static InternalMessage addFormulaIngredient(DataFormulation formula, String ingredient, String[] fldNames, Object[] fldValues) {
        String[] fieldsName = new String[]{TblsFormulationData.FormulaIngredients.FORMULA.getName(), TblsFormulationData.FormulaIngredients.INGREDIENT.getName()};
        Object[] fieldsValue = new Object[]{formula.getFormulaName(), ingredient};

        Object[] existMethod = Rdbms.existsRecord(TblsFormulationData.TablesFormulationData.FORMULA_INGREDIENTS, fieldsName, fieldsValue, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existMethod[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationEnums.FormulationErrorTrapping.INGREDIENT_ALREADY_EXISTS, new Object[]{ingredient, formula.getFormulaName()}, formula.getFormulaName());
        }

        fieldsName = LPArray.addValueToArray1D(fieldsName, fldNames);
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, fldValues);

        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsFormulationData.TablesFormulationData.FORMULA_INGREDIENTS, fieldsName, fieldsValue);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
            AppFormulaAudit(FormulationEnums.AppFormulationAuditEvents.ADDED_INGREDIENT, formula.getFormulaName(), TblsFormulationData.TablesFormulationData.FORMULA_INGREDIENTS.getTableName(), ingredient,
                    fieldsName, fieldsValue, formula.getProjectName());
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, FormulationEnums.FormulationAPIactionsEndpoints.FORMULA_ADD_INGREDIENT, new Object[]{ingredient, formula.getFormulaName()}, formula.getFormulaName());    
    }

    public static InternalMessage formulaAuditSetAuditRecordAsReviewed(Integer auditId, String personName) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = procReqSession.getMessages();
        String auditReviewMode = Parameter.getBusinessRuleProcedureFile(procReqSession.getProcedureInstance(), FormulationEnums.FormulationBusinessRules.REVISION_MODE.getAreaName(), FormulationEnums.FormulationBusinessRules.REVISION_MODE.getTagName());
        if (Boolean.FALSE.equals(isTagValueOneOfEnableOnes(auditReviewMode))) {
            messages.addMainForError(FormulationEnums.FormulationErrorTrapping.DISABLED, new Object[]{});
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationEnums.FormulationErrorTrapping.DISABLED, new Object[]{});
        }
        String auditAuthorCanBeReviewerMode = Parameter.getBusinessRuleProcedureFile(procReqSession.getProcedureInstance(), FormulationEnums.FormulationBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getAreaName(), FormulationEnums.FormulationBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName());
        Object[][] auditInfo = QueryUtilitiesEnums.getTableData(TblsFormulationDataAudit.TablesFormulationDataAudit.FORMULA,
                EnumIntTableFields.getTableFieldsFromString(TblsFormulationDataAudit.TablesFormulationDataAudit.FORMULA, new String[]{TblsFormulationDataAudit.Formula.PERSON.getName(), TblsFormulationDataAudit.Formula.REVIEWED.getName()}),
                new String[]{TblsFormulationDataAudit.Formula.AUDIT_ID.getName()}, new Object[]{auditId},
                new String[]{TblsFormulationDataAudit.Formula.AUDIT_ID.getName()});
        if (Boolean.FALSE.equals(isTagValueOneOfEnableOnes(auditAuthorCanBeReviewerMode))) {
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(auditInfo[0][0].toString())) {
                messages.addMainForError(FormulationEnums.FormulationErrorTrapping.DISABLED, new Object[]{});
                return new InternalMessage(LPPlatform.LAB_FALSE, FormulationEnums.FormulationErrorTrapping.DISABLED, new Object[]{});
            }
            if (personName.equalsIgnoreCase(auditInfo[0][0].toString())) {
                messages.addMainForError(FormulationEnums.FormulationErrorTrapping.AUTHOR_CANNOT_BE_REVIEWER, new Object[]{});
                return new InternalMessage(LPPlatform.LAB_FALSE, FormulationEnums.FormulationErrorTrapping.AUTHOR_CANNOT_BE_REVIEWER, new Object[]{});
            }
        }
        if (Boolean.TRUE.equals(Boolean.valueOf(auditInfo[0][1].toString()))) {
            messages.addMainForError(FormulationEnums.FormulationErrorTrapping.AUDIT_RECORD_ALREADY_REVIEWED, new Object[]{auditId});
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationEnums.FormulationErrorTrapping.AUDIT_RECORD_ALREADY_REVIEWED, new Object[]{auditId});
        }
        String[] updFieldsName = new String[]{TblsFormulationDataAudit.Formula.REVIEWED.getName(), TblsFormulationDataAudit.Formula.REVIEWED_BY.getName(), TblsFormulationDataAudit.Formula.REVIEWED_ON.getName()};
        Object[] updFieldsValue = new Object[]{true, personName, LPDate.getCurrentTimeStamp()};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsFormulationDataAudit.Formula.AUDIT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{auditId}, "");
        Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(TblsFormulationDataAudit.TablesFormulationDataAudit.FORMULA,
                EnumIntTableFields.getTableFieldsFromString(TblsFormulationDataAudit.TablesFormulationDataAudit.FORMULA, updFieldsName), updFieldsValue, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(updateRecordFieldsByFilter[0].toString())) {
            return new InternalMessage(updateRecordFieldsByFilter[0].toString(), FormulationEnums.FormulationAPIactionsEndpoints.FORMULAAUDIT_SET_AUDIT_ID_REVIEWED, new Object[]{auditId});
        } else {
            return new InternalMessage(updateRecordFieldsByFilter[0].toString(), FormulationEnums.FormulationErrorTrapping.AUDIT_RECORD_NOT_FOUND, new Object[]{auditId});
        }
    }

}
