package module.methodvalidation.logic;

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
import module.methodvalidation.definition.MethodValidationEnums.MethodValidationAPIactionsEndpoints;
import module.methodvalidation.definition.TblsMethodValidationData;
import module.methodvalidation.definition.TblsMethodValidationData.TablesMethodValidationData;
import module.projectrnd.definition.ProjectsRnDEnums;
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
public class DataMethodValidation {

    /**
     * @return the formulaName
     */
    public String getParameterName() {
        return parameterName;
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
    private final String parameterName;
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

    public DataMethodValidation(String parameterName, String analyticalParameter) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = "";        
        Object[][] projectRnDinfo = Rdbms.getRecordFieldsByFilter(procInstanceName,  LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getTableName(),
                new String[]{TblsMethodValidationData.ValidationMethodParams.NAME.getName()},
                new Object[]{projectName}, getAllFieldNames(TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projectRnDinfo[0][0].toString())) {
            this.parameterName = null;
            this.hasError = true;
            this.errorDetail = new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{projectName, TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getTableName(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())}, projectName);
        } else {
            this.hasError = false;
            this.formulaFieldNames = getAllFieldNames(TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getTableFields());
            this.formulaFieldValues = projectRnDinfo[0];
            this.parameterName = LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsMethodValidationData.ValidationMethodParams.NAME.getName())]).toString();
            this.isLocked = Boolean.valueOf(LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsMethodValidationData.ValidationMethodParams.IS_LOCKED.getName())]).toString());
            if (this.isLocked == null) {
                this.isLocked = false;
            }
            this.lockedReason = LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsMethodValidationData.ValidationMethodParams.LOCKED_REASON.getName())]).toString();
        }
    }

    public static InternalMessage createNewParameter(String parameterName, String analyticalParameter, String projectName, String[] fldNames, Object[] fldValues) {
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = procReqSession.getMessages();
        Token token = procReqSession.getToken();
        if (fldNames == null) {
            fldNames = new String[]{};
            fldValues = new Object[]{};
        }
        Object[][] referenceInfo = null;
        
        fldNames = LPArray.addValueToArray1D(fldNames, TblsMethodValidationData.ValidationMethodParams.NAME.getName());
        fldValues = LPArray.addValueToArray1D(fldValues, parameterName);
        fldNames = LPArray.addValueToArray1D(fldNames, TblsMethodValidationData.ValidationMethodParams.ANALYTICAL_PARAMETER.getName());
        fldValues = LPArray.addValueToArray1D(fldValues, analyticalParameter);

        fldNames = LPArray.addValueToArray1D(fldNames, new String[]{TblsMethodValidationData.ValidationMethodParams.CREATED_ON.getName(), TblsMethodValidationData.ValidationMethodParams.CREATED_BY.getName()});
        fldValues = LPArray.addValueToArray1D(fldValues, new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()});

        if (projectName!=null){
            fldNames = LPArray.addValueToArray1D(fldNames, TblsMethodValidationData.ValidationMethodParams.PROJECT.getName());
            fldValues = LPArray.addValueToArray1D(fldValues, projectName);
        }
        
        RdbmsObject invLotCreationDiagn = Rdbms.insertRecordInTable(TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS, fldNames, fldValues);
        if (Boolean.FALSE.equals(invLotCreationDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotCreationDiagn.getErrorMessageCode(), new Object[]{parameterName}, null);
        }
        MethodValidationAudit.MethodValidationAudit(ProjectsRnDEnums.ProjectRnDAuditEvents.PROJECT_CREATION, parameterName, TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getTableName(), parameterName,
                fldNames, fldValues, projectName);        

        Integer fldPosic=LPArray.valuePosicInArray(fldNames, TblsMethodValidationData.ValidationMethodParams.NUM_SAMPLES.getName());

        DataMethValSample MethSmp= new DataMethValSample();
        MethSmp.logParameterSample(parameterName, null, null, 
            (fldPosic==-1)? 0:Integer.valueOf(fldValues[fldPosic].toString()));

        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getTableName(), parameterName);
        messages.addMainForSuccess(MethodValidationAPIactionsEndpoints.NEW_PARAMETER, new Object[]{parameterName});
        return new InternalMessage(LPPlatform.LAB_TRUE, MethodValidationAPIactionsEndpoints.NEW_PARAMETER, new Object[]{parameterName}, parameterName);
    }

    private InternalMessage updateLotTransaction(EnumIntEndpoints actionObj, EnumIntAuditEvents auditEventObj, String[] extraFldNames, Object[] extraFldValues) {

        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = procReqSession.getMessages();
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsMethodValidationData.ValidationMethodParams.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.getParameterName()}, "");        
        RdbmsObject invLotTurnAvailableDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS,
                EnumIntTableFields.getTableFieldsFromString(TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS, extraFldNames),
                extraFldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(invLotTurnAvailableDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotTurnAvailableDiagn.getErrorMessageCode(), new Object[]{this.getParameterName()}, null);
        }
        MethodValidationAudit.MethodValidationAudit(auditEventObj, parameterName, TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getTableName(), parameterName,
                extraFldNames, extraFldValues, this.getProjectName());        
        messages.addMainForSuccess(actionObj, new Object[]{this.getParameterName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, actionObj, new Object[]{this.getParameterName()}, this.getParameterName());
    }
/*
    public InternalMessage turnAvailable(String[] fldNames, Object[] fldValues) {
        if (Boolean.TRUE.equals(this.getRequiresQualification()) && Boolean.FALSE.equals(this.getIsQualified())){
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationErrorTrapping.PROJECT_NOTQUALIFIED_YET, new Object[]{this.getParameterName()}, null);
        }
        
        return updateLotTransaction(InvLotStatuses.AVAILABLE_FOR_USE.toString(), ProjectsRnDEnums.MethodValidationAPIactionsEndpoints.TURN_LOT_AVAILABLE,
                ProjectsRnDEnums.ProjectRnDAuditEvents.TURN_AVAILABLE, null, null, FormulationErrorTrapping.ALREADY_AVAILABLE);
    }

    public InternalMessage turnUnAvailable(String[] fldNames, Object[] fldValues) {
        if (Boolean.TRUE.equals(this.getRequiresQualification()) && Boolean.FALSE.equals(this.getIsQualified())){
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationErrorTrapping.PROJECT_NOTQUALIFIED_YET, new Object[]{this.getParameterName()}, null);
        }
        return updateLotTransaction(InvLotStatuses.NOT_AVAILABLEFOR_USE.toString(), ProjectsRnDEnums.MethodValidationAPIactionsEndpoints.TURN_LOT_UNAVAILABLE,
                ProjectsRnDEnums.ProjectRnDAuditEvents.TURN_UNAVAILABLE, null, null, FormulationErrorTrapping.ALREADY_UNAVAILABLE);
    }
*/
/*    
    public InternalMessage updateFormula(String[] fldNames, Object[] fldValues) {
        if (Boolean.TRUE.equals(this.getIsRetired())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, FormulationErrorTrapping.ALREADY_RETIRED, new Object[]{this.getParameterName()}, null);
        }
        String[] reservedFldsNotUpdatable = new String[]{TblsMethodValidationData.ValidationMethodParams.NAME.getName()};
        String[] reservedFldsNotUpdatableFromActions = new String[]{TblsMethodValidationData.ValidationMethodParams.NAME.getName()};
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
        sqlWhere.addConstraint(TblsMethodValidationData.ValidationMethodParams.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{getParameterName()}, "");
        RdbmsObject instUpdateDiagn = Rdbms.updateTableRecordFieldsByFilter(TablesMethodValidationData.PROJECT,
                EnumIntTableFields.getTableFieldsFromString(TablesMethodValidationData.PROJECT, fldNames), fldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(instUpdateDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn.getErrorMessageCode(), instUpdateDiagn.getErrorMessageVariables());
        }
        AppProjectRnDAudit(ProjectsRnDEnums.ProjectRnDAuditEvents.UPDATE_PROJECT, this.getParameterName(), TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getTableName(), this.getParameterName(),
                fldNames, fldValues);
        messages.addMainForSuccess(ProjectsRnDEnums.MethodValidationAPIactionsEndpoints.UPDATE_FORMULA, new Object[]{getParameterName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, ProjectsRnDEnums.MethodValidationAPIactionsEndpoints.UPDATE_FORMULA, new Object[]{getParameterName()}, getParameterName());
    }

    public InternalMessage closeFormula(String[] fldNames, Object[] fldValues) {
        return updateLotTransaction(ProjectsRnDEnums.MethodValidationAPIactionsEndpoints.CLOSE_FORMULA,
    ProjectsRnDEnums.ProjectRnDAuditEvents.CLOSED_FORMULA, new String[]{TblsMethodValidationData.ValidationMethodParams.OPEN.getName()}, new Object[]{false});
    }

    public InternalMessage addAttachment(String attachUrl, String briefSummary) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isLocked)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ProjectsRnDEnums.FormulationErrorTrapping.ALREADY_RETIRED, new Object[]{this.getParameterName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        String[] fldNames = new String[]{TblsMethodValidationData.ProjectAttachments.PROJECT_NAME.getName(), TblsMethodValidationData.ProjectAttachments.FILE_LINK.getName(), 
            TblsMethodValidationData.ProjectAttachments.CREATED_ON.getName(), TblsMethodValidationData.ProjectAttachments.CREATED_BY.getName()};        
        Object[] fldValues = new Object[]{this.getParameterName(), attachUrl, LPDate.getCurrentTimeStamp(), procReqSession.getToken().getPersonName()};
        if (briefSummary != null) {
            fldNames=LPArray.addValueToArray1D(fldNames, TblsMethodValidationData.ProjectAttachments.BRIEF_SUMMARY.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, briefSummary);
        }
        RdbmsObject insertRecordInTable = Rdbms.insertRecord(TblsMethodValidationData.TablesMethodValidationData.PROJECT_ATTACHMENT, 
                fldNames, fldValues, procReqSession.getProcedureInstance());
        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
        }
        AppProjectRnDAudit.AppProjectRnDAudit(ProjectsRnDEnums.ProjectRnDAuditEvents.ADDED_ATTACHMENT, getParameterName(), 
                TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getTableName(), getParameterName(), fldNames, fldValues);
        messages.addMainForSuccess(ProjectsRnDEnums.MethodValidationAPIactionsEndpoints.ADD_ATTACHMENT, new Object[]{getParameterName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, ProjectsRnDEnums.MethodValidationAPIactionsEndpoints.ADD_ATTACHMENT, new Object[]{getParameterName()}, getParameterName());
    }
    public InternalMessage removeAttachment(Integer attachmentId) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isLocked)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ProjectsRnDEnums.FormulationErrorTrapping.ALREADY_RETIRED, new Object[]{this.getParameterName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        EnumIntTableFields[] fldNamesObj = new EnumIntTableFields[]{TblsMethodValidationData.ProjectAttachments.REMOVED};
        Object[] fldValues = new Object[]{true};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsMethodValidationData.ProjectAttachments.PROJECT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.getParameterName()}, "");
        sqlWhere.addConstraint(TblsMethodValidationData.ProjectAttachments.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{attachmentId}, "");
        
        RdbmsObject updateRecordInTable = Rdbms.updateTableRecordFieldsByFilter(TblsMethodValidationData.TablesMethodValidationData.PROJECT_ATTACHMENT, 
                fldNamesObj, fldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(updateRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordInTable.getErrorMessageCode(), updateRecordInTable.getErrorMessageVariables(), null);
        }
        AppProjectRnDAudit.AppProjectRnDAudit(ProjectsRnDEnums.ProjectRnDAuditEvents.REMOVED_ATTACHMENT, getParameterName(), 
                TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getTableName(), getParameterName(),EnumIntTableFields.getAllFieldNames(fldNamesObj), fldValues);
        messages.addMainForSuccess(ProjectsRnDEnums.MethodValidationAPIactionsEndpoints.REMOVE_ATTACHMENT, new Object[]{getParameterName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, ProjectsRnDEnums.MethodValidationAPIactionsEndpoints.REMOVE_ATTACHMENT, new Object[]{getParameterName()}, getParameterName());
    }
    public InternalMessage reactivateAttachment(Integer attachmentId) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isLocked)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ProjectsRnDEnums.FormulationErrorTrapping.ALREADY_RETIRED, new Object[]{this.getParameterName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        EnumIntTableFields[] fldNamesObj = new EnumIntTableFields[]{TblsMethodValidationData.ProjectAttachments.REMOVED};
        Object[] fldValues = new Object[]{false};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsMethodValidationData.ProjectAttachments.PROJECT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.getParameterName()}, "");
        sqlWhere.addConstraint(TblsMethodValidationData.ProjectAttachments.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{attachmentId}, "");
        
            RdbmsObject updateRecordInTable = Rdbms.updateTableRecordFieldsByFilter(TblsMethodValidationData.TablesMethodValidationData.PROJECT_ATTACHMENT, 
                fldNamesObj, fldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(updateRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordInTable.getErrorMessageCode(), updateRecordInTable.getErrorMessageVariables(), null);
        }
        AppProjectRnDAudit.AppProjectRnDAudit(ProjectsRnDEnums.ProjectRnDAuditEvents.REACTIVATED_ATTACHMENT, getParameterName(), TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getTableName(), getParameterName(),
        EnumIntTableFields.getAllFieldNames(fldNamesObj), fldValues);
        messages.addMainForSuccess(ProjectsRnDEnums.MethodValidationAPIactionsEndpoints.REACTIVATE_ATTACHMENT, new Object[]{getParameterName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, ProjectsRnDEnums.MethodValidationAPIactionsEndpoints.REACTIVATE_ATTACHMENT, new Object[]{getParameterName()}, getParameterName());
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
