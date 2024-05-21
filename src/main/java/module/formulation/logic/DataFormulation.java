package module.formulation.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.features.Token;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import trazit.session.ResponseMessages;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.formulation.definition.FormulationEnums;
import module.formulation.definition.FormulationEnums.FormulationErrorTrapping;
import module.formulation.definition.TblsFormulationData;
import module.formulation.definition.TblsFormulationData.TablesFormulationData;
import static module.formulation.logic.AppFormulaAudit.AppFormulaAudit;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class DataFormulation {

    /**
     * @return the formulaName
     */
    public String getFormulaName() {
        return formulaName;
    }
    public String getProjectName() {
        return projectName;
    }

    /**
     * @return the reference
     */

    /**
     * @return the category
     */
    private final String formulaName;
    private String projectName;
    private Boolean isLocked;
    private Boolean isRetired;
    private String lockedReason;
    private String[] formulaFieldNames;
    private Object[] formulaFieldValues;
    private Boolean hasError;
    private InternalMessage errorDetail;
    private String[] ingredientsFieldNames;
    private Object[] ingredientsFieldValues;

    public DataFormulation(String formula) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[][] formulaInfo = null;
        formulaInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(),  LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsFormulationData.TablesFormulationData.FORMULA.getTableName(),
                new String[]{TblsFormulationData.Formula.NAME.getName()},
                new Object[]{formula}, getAllFieldNames(TblsFormulationData.TablesFormulationData.FORMULA.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(formulaInfo[0][0].toString())) {
            this.formulaName = null;
            this.hasError = true;
            this.errorDetail = new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{formula, TablesFormulationData.FORMULA.getTableName(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())}, formula);
        } else {
            this.hasError = false;
            this.formulaFieldNames = getAllFieldNames(TblsFormulationData.TablesFormulationData.FORMULA.getTableFields());
            this.formulaFieldValues = formulaInfo[0];
            this.formulaName = LPNulls.replaceNull(formulaInfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsFormulationData.Formula.NAME.getName())]).toString();
            this.isLocked = Boolean.valueOf(LPNulls.replaceNull(formulaInfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsFormulationData.Formula.IS_LOCKED.getName())]).toString());
            if (this.isLocked == null) {
                this.isLocked = false;
            }
            this.lockedReason = LPNulls.replaceNull(formulaInfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsFormulationData.Formula.LOCKED_REASON.getName())]).toString();
        }
    }

    public static InternalMessage createNewFormula(String formulaName, String projectName, String[] fldNames, Object[] fldValues, String ingredientsList) {
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = procReqSession.getMessages();
        Token token = procReqSession.getToken();
        if (fldNames == null) {
            fldNames = new String[]{};
            fldValues = new Object[]{};
        }
        Object[][] referenceInfo = null;
        
        fldNames = LPArray.addValueToArray1D(fldNames, TblsFormulationData.Formula.NAME.getName());
        fldValues = LPArray.addValueToArray1D(fldValues, formulaName);

        if (projectName !=null&&projectName.length()>0){
            fldNames = LPArray.addValueToArray1D(fldNames, TblsFormulationData.Formula.PROJECT.getName());
            fldValues = LPArray.addValueToArray1D(fldValues, projectName);
        }
        fldNames = LPArray.addValueToArray1D(fldNames, new String[]{TblsFormulationData.Formula.CREATED_ON.getName(), TblsFormulationData.Formula.CREATED_BY.getName()});
        fldValues = LPArray.addValueToArray1D(fldValues, new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()});

        RdbmsObject invLotCreationDiagn = Rdbms.insertRecordInTable(TablesFormulationData.FORMULA, fldNames, fldValues);
        if (Boolean.FALSE.equals(invLotCreationDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotCreationDiagn.getErrorMessageCode(), new Object[]{formulaName}, null);
        }
        AppFormulaAudit(FormulationEnums.AppFormulationAuditEvents.FORMULA_CREATION, formulaName, TablesFormulationData.FORMULA.getTableName(), formulaName,
                fldNames, fldValues, projectName);
        if (ingredientsList!=null&&ingredientsList.length()>0){
            int iIngredients=0;
            for (String curIngredient: ingredientsList.split("\\|")) {                                                
                fldNames = new String[]{TblsFormulationData.FormulaIngredients.FORMULA.getName(), TblsFormulationData.FormulaIngredients.INGREDIENT.getName(), TblsFormulationData.FormulaIngredients.ORDER_NUMBER.getName()};
                fldValues = new Object[]{formulaName, curIngredient, iIngredients++};
                RdbmsObject formulaIngredientCreation = Rdbms.insertRecordInTable(TablesFormulationData.FORMULA_INGREDIENTS, fldNames, fldValues);
                if (Boolean.FALSE.equals(formulaIngredientCreation.getRunSuccess())) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, formulaIngredientCreation.getErrorMessageCode(), new Object[]{formulaName}, null);
                }
                AppFormulaAudit(FormulationEnums.AppFormulationAuditEvents.FORMULA_CREATION, formulaName, TablesFormulationData.FORMULA.getTableName(), formulaName,
                        fldNames, fldValues, projectName);
                messages.addMinorForSuccess(FormulationEnums.FormulationAPIactionsEndpoints.NEW_FORMULA, new Object[]{curIngredient});                
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesFormulationData.FORMULA_INGREDIENTS.getTableName(), curIngredient);
            }            
        }
        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesFormulationData.FORMULA.getTableName(), formulaName);
        messages.addMainForSuccess(FormulationEnums.FormulationAPIactionsEndpoints.NEW_FORMULA, new Object[]{formulaName});
        return new InternalMessage(LPPlatform.LAB_TRUE, FormulationEnums.FormulationAPIactionsEndpoints.NEW_FORMULA, new Object[]{formulaName}, formulaName);
    }

    private InternalMessage updateLotTransaction(EnumIntEndpoints actionObj, EnumIntAuditEvents auditEventObj, String[] extraFldNames, Object[] extraFldValues) {

        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = procReqSession.getMessages();
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsFormulationData.Formula.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.getFormulaName()}, "");        
        RdbmsObject invLotTurnAvailableDiagn = Rdbms.updateTableRecordFieldsByFilter(TablesFormulationData.FORMULA,
                EnumIntTableFields.getTableFieldsFromString(TablesFormulationData.FORMULA, extraFldNames),
                extraFldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(invLotTurnAvailableDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotTurnAvailableDiagn.getErrorMessageCode(), new Object[]{this.getFormulaName()}, null);
        }
        AppFormulaAudit(auditEventObj, this.getFormulaName(), TablesFormulationData.FORMULA.getTableName(), this.getFormulaName(),
        extraFldNames, extraFldValues, projectName);
        messages.addMainForSuccess(actionObj, new Object[]{this.getFormulaName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, actionObj, new Object[]{this.getFormulaName()}, this.getFormulaName());
    }
/*
    public InternalMessage turnAvailable(String[] fldNames, Object[] fldValues) {
        if (Boolean.TRUE.equals(this.getRequiresQualification()) && Boolean.FALSE.equals(this.getIsQualified())){
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationErrorTrapping.FORMULA_NOTQUALIFIED_YET, new Object[]{this.getFormulaName()}, null);
        }
        
        return updateLotTransaction(InvLotStatuses.AVAILABLE_FOR_USE.toString(), FormulationEnums.FormulationAPIactionsEndpoints.TURN_LOT_AVAILABLE,
                FormulationEnums.AppFormulationAuditEvents.TURN_AVAILABLE, null, null, FormulationErrorTrapping.ALREADY_AVAILABLE);
    }

    public InternalMessage turnUnAvailable(String[] fldNames, Object[] fldValues) {
        if (Boolean.TRUE.equals(this.getRequiresQualification()) && Boolean.FALSE.equals(this.getIsQualified())){
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationErrorTrapping.FORMULA_NOTQUALIFIED_YET, new Object[]{this.getFormulaName()}, null);
        }
        return updateLotTransaction(InvLotStatuses.NOT_AVAILABLEFOR_USE.toString(), FormulationEnums.FormulationAPIactionsEndpoints.TURN_LOT_UNAVAILABLE,
                FormulationEnums.AppFormulationAuditEvents.TURN_UNAVAILABLE, null, null, FormulationErrorTrapping.ALREADY_UNAVAILABLE);
    }
*/
    public InternalMessage updateFormula(String[] fldNames, Object[] fldValues) {
        if (Boolean.TRUE.equals(this.getIsRetired())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationErrorTrapping.ALREADY_RETIRED, new Object[]{this.getFormulaName()}, null);
        }
        String[] reservedFldsNotUpdatable = new String[]{TblsFormulationData.Formula.NAME.getName()};
        String[] reservedFldsNotUpdatableFromActions = new String[]{TblsFormulationData.Formula.NAME.getName()};
        for (String curFld : fldNames) {
            if (LPArray.valueInArray(reservedFldsNotUpdatable, curFld)) {
                return new InternalMessage(LPPlatform.LAB_FALSE, FormulationErrorTrapping.TRYINGUPDATE_RESERVED_FIELD, new Object[]{curFld}, null);
            }
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames == null || fldNames[0].length() == 0) {
            fldNames = new String[]{};
            fldValues = new Object[]{};
        }
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsFormulationData.Formula.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{getFormulaName()}, "");
        RdbmsObject instUpdateDiagn = Rdbms.updateTableRecordFieldsByFilter(TablesFormulationData.FORMULA,
                EnumIntTableFields.getTableFieldsFromString(TablesFormulationData.FORMULA, fldNames), fldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(instUpdateDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn.getErrorMessageCode(), instUpdateDiagn.getErrorMessageVariables());
        }
        AppFormulaAudit(FormulationEnums.AppFormulationAuditEvents.UPDATED_FORMULA, this.getFormulaName(), TablesFormulationData.FORMULA.getTableName(), this.getFormulaName(),
                fldNames, fldValues, this.projectName);
        messages.addMainForSuccess(FormulationEnums.FormulationAPIactionsEndpoints.UPDATE_FORMULA, new Object[]{getFormulaName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, FormulationEnums.FormulationAPIactionsEndpoints.UPDATE_FORMULA, new Object[]{getFormulaName()}, getFormulaName());
    }

    public InternalMessage closeFormula(String[] fldNames, Object[] fldValues) {
        return updateLotTransaction(FormulationEnums.FormulationAPIactionsEndpoints.CLOSE_FORMULA,
    FormulationEnums.AppFormulationAuditEvents.CLOSED_FORMULA, new String[]{TblsFormulationData.Formula.IS_OPEN.getName()}, new Object[]{false});
    }

    public InternalMessage addFormulaIngredient(String ingredient, String[] fldNames, Object[] fldValues) {
        return DataFormulaIngredient.addFormulaIngredient(this, ingredient, fldNames, fldValues);
    }
    public InternalMessage removeFormulaIngredient(String ingredient) {
        return DataFormulaIngredient.removeFormulaIngredient(this, ingredient);
    }
    public InternalMessage addAttachment(String attachUrl, String briefSummary) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isLocked)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationEnums.FormulationErrorTrapping.ALREADY_RETIRED, new Object[]{this.getFormulaName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        String[] fldNames = new String[]{TblsFormulationData.FormulaAttachments.FORMULA_NAME.getName(), TblsFormulationData.FormulaAttachments.FILE_LINK.getName(), 
            TblsFormulationData.FormulaAttachments.CREATED_ON.getName(), TblsFormulationData.FormulaAttachments.CREATED_BY.getName()};        
        Object[] fldValues = new Object[]{this.getFormulaName(), attachUrl, LPDate.getCurrentTimeStamp(), procReqSession.getToken().getPersonName()};
        if (briefSummary != null) {
            fldNames=LPArray.addValueToArray1D(fldNames, TblsFormulationData.FormulaAttachments.BRIEF_SUMMARY.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, briefSummary);
        }
        RdbmsObject insertRecordInTable = Rdbms.insertRecord(TblsFormulationData.TablesFormulationData.FORMULA_ATTACHMENT, 
                fldNames, fldValues, procReqSession.getProcedureInstance());
        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
        }
        AppFormulaAudit.AppFormulaAudit(FormulationEnums.AppFormulationAuditEvents.ADDED_ATTACHMENT, getFormulaName(), 
                TblsFormulationData.TablesFormulationData.FORMULA.getTableName(), getFormulaName(), fldNames, fldValues, this.projectName);
        messages.addMainForSuccess(FormulationEnums.FormulationAPIactionsEndpoints.ADD_ATTACHMENT, new Object[]{getFormulaName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, FormulationEnums.FormulationAPIactionsEndpoints.ADD_ATTACHMENT, new Object[]{getFormulaName()}, getFormulaName());
    }
    public InternalMessage removeAttachment(Integer attachmentId) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isLocked)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationEnums.FormulationErrorTrapping.ALREADY_RETIRED, new Object[]{this.getFormulaName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        EnumIntTableFields[] fldNamesObj = new EnumIntTableFields[]{TblsFormulationData.FormulaAttachments.REMOVED};
        Object[] fldValues = new Object[]{true};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsFormulationData.FormulaAttachments.FORMULA_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.getFormulaName()}, "");
        sqlWhere.addConstraint(TblsFormulationData.FormulaAttachments.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{attachmentId}, "");
        
        RdbmsObject updateRecordInTable = Rdbms.updateTableRecordFieldsByFilter(TblsFormulationData.TablesFormulationData.FORMULA_ATTACHMENT, 
                fldNamesObj, fldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(updateRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordInTable.getErrorMessageCode(), updateRecordInTable.getErrorMessageVariables(), null);
        }
        AppFormulaAudit.AppFormulaAudit(FormulationEnums.AppFormulationAuditEvents.REMOVED_ATTACHMENT, getFormulaName(), 
                TblsFormulationData.TablesFormulationData.FORMULA.getTableName(), getFormulaName(),EnumIntTableFields.getAllFieldNames(fldNamesObj), fldValues, this.projectName);
        messages.addMainForSuccess(FormulationEnums.FormulationAPIactionsEndpoints.REMOVE_ATTACHMENT, new Object[]{getFormulaName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, FormulationEnums.FormulationAPIactionsEndpoints.REMOVE_ATTACHMENT, new Object[]{getFormulaName()}, getFormulaName());
    }
    public InternalMessage reactivateAttachment(Integer attachmentId) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isLocked)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationEnums.FormulationErrorTrapping.ALREADY_RETIRED, new Object[]{this.getFormulaName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        EnumIntTableFields[] fldNamesObj = new EnumIntTableFields[]{TblsFormulationData.FormulaAttachments.REMOVED};
        Object[] fldValues = new Object[]{false};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsFormulationData.FormulaAttachments.FORMULA_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.getFormulaName()}, "");
        sqlWhere.addConstraint(TblsFormulationData.FormulaAttachments.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{attachmentId}, "");
        
            RdbmsObject updateRecordInTable = Rdbms.updateTableRecordFieldsByFilter(TblsFormulationData.TablesFormulationData.FORMULA_ATTACHMENT, 
                fldNamesObj, fldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(updateRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordInTable.getErrorMessageCode(), updateRecordInTable.getErrorMessageVariables(), null);
        }
        AppFormulaAudit.AppFormulaAudit(FormulationEnums.AppFormulationAuditEvents.REACTIVATED_ATTACHMENT, getFormulaName(), TblsFormulationData.TablesFormulationData.FORMULA.getTableName(), getFormulaName(),
        EnumIntTableFields.getAllFieldNames(fldNamesObj), fldValues, this.projectName);
        messages.addMainForSuccess(FormulationEnums.FormulationAPIactionsEndpoints.REACTIVATE_ATTACHMENT, new Object[]{getFormulaName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, FormulationEnums.FormulationAPIactionsEndpoints.REACTIVATE_ATTACHMENT, new Object[]{getFormulaName()}, getFormulaName());
    }

    public Boolean getHasError() {
        return hasError;
    }

    public InternalMessage getErrorDetail() {
        return errorDetail;
    }

    public Boolean getIsRetired() {
        return isRetired;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public String getLockedReason() {
        return lockedReason;
    }

    public String[] getQualificationFieldNames() {
        return ingredientsFieldNames;
    }

    public Object[] getQualificationFieldValues() {
        return ingredientsFieldValues;
    }

    public String[] getLotFieldNames() {
        return formulaFieldNames;
    }

    public Object[] getLotFieldValues() {
        return formulaFieldValues;
    }

}
