package module.projectrnd.logic;

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
import module.projectrnd.definition.ProjectsRnDEnums;
import module.projectrnd.definition.TblsProjectRnDData;
import module.projectrnd.definition.TblsProjectRnDData.TablesProjectRnDData;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import static module.projectrnd.logic.AppProjectRnDAudit.AppProjectRnDAudit;

/**
 *
 * @author User
 */
public class DataProjectRnD {

    /**
     * @return the formulaName
     */
    public String getFormulaName() {
        return formulaName;
    }

    /**
     * @return the reference
     */

    /**
     * @return the category
     */
    private final String formulaName;
    private Boolean isLocked;
    private Boolean isRetired;
    private String lockedReason;
    private String[] formulaFieldNames;
    private Object[] formulaFieldValues;
    private Boolean hasError;
    private InternalMessage errorDetail;
    private String[] ingredientsFieldNames;
    private Object[] ingredientsFieldValues;

    public DataProjectRnD(String projectName) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = "";
        Object[][] projectRnDinfo = null;
        projectRnDinfo = Rdbms.getRecordFieldsByFilter(procInstanceName,  LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsProjectRnDData.TablesProjectRnDData.PROJECT.getTableName(),
                new String[]{TblsProjectRnDData.Project.NAME.getName()},
                new Object[]{projectName}, getAllFieldNames(TblsProjectRnDData.TablesProjectRnDData.PROJECT.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projectRnDinfo[0][0].toString())) {
            this.formulaName = null;
            this.hasError = true;
            this.errorDetail = new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{projectName, TablesProjectRnDData.PROJECT.getTableName(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())}, projectName);
        } else {
            this.hasError = false;
            this.formulaFieldNames = getAllFieldNames(TblsProjectRnDData.TablesProjectRnDData.PROJECT.getTableFields());
            this.formulaFieldValues = projectRnDinfo[0];
            this.formulaName = LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsProjectRnDData.Project.NAME.getName())]).toString();
            this.isLocked = Boolean.valueOf(LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsProjectRnDData.Project.IS_LOCKED.getName())]).toString());
            if (this.isLocked == null) {
                this.isLocked = false;
            }
            this.lockedReason = LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsProjectRnDData.Project.LOCKED_REASON.getName())]).toString();
        }
    }

    public static InternalMessage createNewProject(String projectName, String[] fldNames, Object[] fldValues) {
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = procReqSession.getMessages();
        Token token = procReqSession.getToken();
        if (fldNames == null) {
            fldNames = new String[]{};
            fldValues = new Object[]{};
        }
        Object[][] referenceInfo = null;
        
        fldNames = LPArray.addValueToArray1D(fldNames, TblsProjectRnDData.Project.NAME.getName());
        fldValues = LPArray.addValueToArray1D(fldValues, projectName);

        fldNames = LPArray.addValueToArray1D(fldNames, new String[]{TblsProjectRnDData.Project.CREATED_ON.getName(), TblsProjectRnDData.Project.CREATED_BY.getName()});
        fldValues = LPArray.addValueToArray1D(fldValues, new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()});

        RdbmsObject invLotCreationDiagn = Rdbms.insertRecordInTable(TablesProjectRnDData.PROJECT, fldNames, fldValues);
        if (Boolean.FALSE.equals(invLotCreationDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotCreationDiagn.getErrorMessageCode(), new Object[]{projectName}, null);
        }
        AppProjectRnDAudit(ProjectsRnDEnums.ProjectRnDAuditEvents.PROJECT_CREATION, projectName, TablesProjectRnDData.PROJECT.getTableName(), projectName,
                fldNames, fldValues);        
        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesProjectRnDData.PROJECT.getTableName(), projectName);
        messages.addMainForSuccess(ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.NEW_PROJECT, new Object[]{projectName});
        return new InternalMessage(LPPlatform.LAB_TRUE, ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.NEW_PROJECT, new Object[]{projectName}, projectName);
    }

    private InternalMessage updateLotTransaction(EnumIntEndpoints actionObj, EnumIntAuditEvents auditEventObj, String[] extraFldNames, Object[] extraFldValues) {

        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = procReqSession.getMessages();
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsProjectRnDData.Project.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.getFormulaName()}, "");        
        RdbmsObject invLotTurnAvailableDiagn = Rdbms.updateTableRecordFieldsByFilter(TablesProjectRnDData.PROJECT,
                EnumIntTableFields.getTableFieldsFromString(TablesProjectRnDData.PROJECT, extraFldNames),
                extraFldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(invLotTurnAvailableDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotTurnAvailableDiagn.getErrorMessageCode(), new Object[]{this.getFormulaName()}, null);
        }
        AppProjectRnDAudit(auditEventObj, this.getFormulaName(), TablesProjectRnDData.PROJECT.getTableName(), this.getFormulaName(),
        extraFldNames, extraFldValues);
        messages.addMainForSuccess(actionObj, new Object[]{this.getFormulaName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, actionObj, new Object[]{this.getFormulaName()}, this.getFormulaName());
    }
/*
    public InternalMessage turnAvailable(String[] fldNames, Object[] fldValues) {
        if (Boolean.TRUE.equals(this.getRequiresQualification()) && Boolean.FALSE.equals(this.getIsQualified())){
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationErrorTrapping.PROJECT_NOTQUALIFIED_YET, new Object[]{this.getFormulaName()}, null);
        }
        
        return updateLotTransaction(InvLotStatuses.AVAILABLE_FOR_USE.toString(), ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.TURN_LOT_AVAILABLE,
                ProjectsRnDEnums.ProjectRnDAuditEvents.TURN_AVAILABLE, null, null, FormulationErrorTrapping.ALREADY_AVAILABLE);
    }

    public InternalMessage turnUnAvailable(String[] fldNames, Object[] fldValues) {
        if (Boolean.TRUE.equals(this.getRequiresQualification()) && Boolean.FALSE.equals(this.getIsQualified())){
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationErrorTrapping.PROJECT_NOTQUALIFIED_YET, new Object[]{this.getFormulaName()}, null);
        }
        return updateLotTransaction(InvLotStatuses.NOT_AVAILABLEFOR_USE.toString(), ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.TURN_LOT_UNAVAILABLE,
                ProjectsRnDEnums.ProjectRnDAuditEvents.TURN_UNAVAILABLE, null, null, FormulationErrorTrapping.ALREADY_UNAVAILABLE);
    }
*/
/*    
    public InternalMessage updateFormula(String[] fldNames, Object[] fldValues) {
        if (Boolean.TRUE.equals(this.getIsRetired())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationErrorTrapping.ALREADY_RETIRED, new Object[]{this.getFormulaName()}, null);
        }
        String[] reservedFldsNotUpdatable = new String[]{TblsProjectRnDData.Project.NAME.getName()};
        String[] reservedFldsNotUpdatableFromActions = new String[]{TblsProjectRnDData.Project.NAME.getName()};
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
        sqlWhere.addConstraint(TblsProjectRnDData.Project.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{getFormulaName()}, "");
        RdbmsObject instUpdateDiagn = Rdbms.updateTableRecordFieldsByFilter(TablesProjectRnDData.PROJECT,
                EnumIntTableFields.getTableFieldsFromString(TablesProjectRnDData.PROJECT, fldNames), fldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(instUpdateDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn.getErrorMessageCode(), instUpdateDiagn.getErrorMessageVariables());
        }
        AppProjectRnDAudit(ProjectsRnDEnums.ProjectRnDAuditEvents.UPDATE_PROJECT, this.getFormulaName(), TablesProjectRnDData.PROJECT.getTableName(), this.getFormulaName(),
                fldNames, fldValues);
        messages.addMainForSuccess(ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.UPDATE_FORMULA, new Object[]{getFormulaName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.UPDATE_FORMULA, new Object[]{getFormulaName()}, getFormulaName());
    }

    public InternalMessage closeFormula(String[] fldNames, Object[] fldValues) {
        return updateLotTransaction(ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.CLOSE_FORMULA,
    ProjectsRnDEnums.ProjectRnDAuditEvents.CLOSED_FORMULA, new String[]{TblsProjectRnDData.Project.OPEN.getName()}, new Object[]{false});
    }

    public InternalMessage addAttachment(String attachUrl, String briefSummary) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isLocked)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ProjectsRnDEnums.FormulationErrorTrapping.ALREADY_RETIRED, new Object[]{this.getFormulaName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        String[] fldNames = new String[]{TblsProjectRnDData.ProjectAttachments.PROJECT_NAME.getName(), TblsProjectRnDData.ProjectAttachments.FILE_LINK.getName(), 
            TblsProjectRnDData.ProjectAttachments.CREATED_ON.getName(), TblsProjectRnDData.ProjectAttachments.CREATED_BY.getName()};        
        Object[] fldValues = new Object[]{this.getFormulaName(), attachUrl, LPDate.getCurrentTimeStamp(), procReqSession.getToken().getPersonName()};
        if (briefSummary != null) {
            fldNames=LPArray.addValueToArray1D(fldNames, TblsProjectRnDData.ProjectAttachments.BRIEF_SUMMARY.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, briefSummary);
        }
        RdbmsObject insertRecordInTable = Rdbms.insertRecord(TblsProjectRnDData.TablesProjectRnDData.PROJECT_ATTACHMENT, 
                fldNames, fldValues, procReqSession.getProcedureInstance());
        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
        }
        AppProjectRnDAudit.AppProjectRnDAudit(ProjectsRnDEnums.ProjectRnDAuditEvents.ADDED_ATTACHMENT, getFormulaName(), 
                TblsProjectRnDData.TablesProjectRnDData.PROJECT.getTableName(), getFormulaName(), fldNames, fldValues);
        messages.addMainForSuccess(ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.ADD_ATTACHMENT, new Object[]{getFormulaName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.ADD_ATTACHMENT, new Object[]{getFormulaName()}, getFormulaName());
    }
    public InternalMessage removeAttachment(Integer attachmentId) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isLocked)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ProjectsRnDEnums.FormulationErrorTrapping.ALREADY_RETIRED, new Object[]{this.getFormulaName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        EnumIntTableFields[] fldNamesObj = new EnumIntTableFields[]{TblsProjectRnDData.ProjectAttachments.REMOVED};
        Object[] fldValues = new Object[]{true};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsProjectRnDData.ProjectAttachments.PROJECT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.getFormulaName()}, "");
        sqlWhere.addConstraint(TblsProjectRnDData.ProjectAttachments.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{attachmentId}, "");
        
        RdbmsObject updateRecordInTable = Rdbms.updateTableRecordFieldsByFilter(TblsProjectRnDData.TablesProjectRnDData.PROJECT_ATTACHMENT, 
                fldNamesObj, fldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(updateRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordInTable.getErrorMessageCode(), updateRecordInTable.getErrorMessageVariables(), null);
        }
        AppProjectRnDAudit.AppProjectRnDAudit(ProjectsRnDEnums.ProjectRnDAuditEvents.REMOVED_ATTACHMENT, getFormulaName(), 
                TblsProjectRnDData.TablesProjectRnDData.PROJECT.getTableName(), getFormulaName(),EnumIntTableFields.getAllFieldNames(fldNamesObj), fldValues);
        messages.addMainForSuccess(ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.REMOVE_ATTACHMENT, new Object[]{getFormulaName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.REMOVE_ATTACHMENT, new Object[]{getFormulaName()}, getFormulaName());
    }
    public InternalMessage reactivateAttachment(Integer attachmentId) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isLocked)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ProjectsRnDEnums.FormulationErrorTrapping.ALREADY_RETIRED, new Object[]{this.getFormulaName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        EnumIntTableFields[] fldNamesObj = new EnumIntTableFields[]{TblsProjectRnDData.ProjectAttachments.REMOVED};
        Object[] fldValues = new Object[]{false};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsProjectRnDData.ProjectAttachments.PROJECT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.getFormulaName()}, "");
        sqlWhere.addConstraint(TblsProjectRnDData.ProjectAttachments.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{attachmentId}, "");
        
            RdbmsObject updateRecordInTable = Rdbms.updateTableRecordFieldsByFilter(TblsProjectRnDData.TablesProjectRnDData.PROJECT_ATTACHMENT, 
                fldNamesObj, fldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(updateRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordInTable.getErrorMessageCode(), updateRecordInTable.getErrorMessageVariables(), null);
        }
        AppProjectRnDAudit.AppProjectRnDAudit(ProjectsRnDEnums.ProjectRnDAuditEvents.REACTIVATED_ATTACHMENT, getFormulaName(), TblsProjectRnDData.TablesProjectRnDData.PROJECT.getTableName(), getFormulaName(),
        EnumIntTableFields.getAllFieldNames(fldNamesObj), fldValues);
        messages.addMainForSuccess(ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.REACTIVATE_ATTACHMENT, new Object[]{getFormulaName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.REACTIVATE_ATTACHMENT, new Object[]{getFormulaName()}, getFormulaName());
    }
*/
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
