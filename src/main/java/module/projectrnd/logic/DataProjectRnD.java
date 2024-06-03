package module.projectrnd.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.features.Token;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import trazit.session.ResponseMessages;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.methodvalidation.definition.MethodValidationEnums;
import module.methodvalidation.definition.TblsMethodValidationData;
import module.methodvalidation.logic.DataMethValSample;
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
     * @return the projectName
     */
    public String getFormulaName() {
        return projectName;
    }

    /**
     * @return the reference
     */

    /**
     * @return the category
     */
    private final String projectName;
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
        String procInstanceName = procReqSession.getProcedureInstance();
        Object[][] projectRnDinfo = null;
        projectRnDinfo = Rdbms.getRecordFieldsByFilter(procInstanceName,  LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsProjectRnDData.TablesProjectRnDData.PROJECT.getTableName(),
                new String[]{TblsProjectRnDData.Project.NAME.getName()},
                new Object[]{projectName}, getAllFieldNames(TblsProjectRnDData.TablesProjectRnDData.PROJECT.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projectRnDinfo[0][0].toString())) {
            this.projectName = null;
            this.hasError = true;
            this.errorDetail = new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{projectName, TablesProjectRnDData.PROJECT.getTableName(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())}, projectName);
        } else {
            this.hasError = false;
            this.formulaFieldNames = getAllFieldNames(TblsProjectRnDData.TablesProjectRnDData.PROJECT.getTableFields());
            this.formulaFieldValues = projectRnDinfo[0];
            this.projectName = LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsProjectRnDData.Project.NAME.getName())]).toString();
            this.isLocked = Boolean.valueOf(LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsProjectRnDData.Project.IS_LOCKED.getName())]).toString());
            if (this.isLocked == null) {
                this.isLocked = false;
            }
            this.lockedReason = LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsProjectRnDData.Project.LOCKED_REASON.getName())]).toString();
        }
    }

    public static InternalMessage createNewProject(String projectName, String[] fldNames, Object[] fldValues) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);

        Object[] existMethod = Rdbms.existsRecord(TablesProjectRnDData.PROJECT, 
                new String[]{TblsProjectRnDData.Project.NAME.getName()}, new Object[]{projectName}, procReqSession.getProcedureInstance());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existMethod[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ProjectsRnDEnums.ProjectRnDErrorTrapping.ALREADY_EXISTS, new Object[]{projectName}, projectName);
        }

        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        
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

    
    public static InternalMessage createNewAnalyticalSequence(String analyticalSequenceName, String analyticalParameter, String projectName, String[] fldNames, Object[] fldValues, Integer numSamplesToLog) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[] existMethod = Rdbms.existsRecord(TablesProjectRnDData.METHOD_DEVELOPMENT_SEQUENCE, 
                new String[]{TblsProjectRnDData.MethodDevelopmentSequence.NAME.getName(), TblsProjectRnDData.MethodDevelopmentSequence.PROJECT.getName()}, 
                new Object[]{analyticalSequenceName, projectName}, procReqSession.getProcedureInstance());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existMethod[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ProjectsRnDEnums.ProjectRnDErrorTrapping.ALREADY_EXISTS, new Object[]{analyticalSequenceName, projectName}, analyticalSequenceName);
        }
        fldNames = LPArray.addValueToArray1D(fldNames, new String[]{TblsProjectRnDData.MethodDevelopmentSequence.NAME.getName(), TblsProjectRnDData.MethodDevelopmentSequence.PROJECT.getName()});
        fldValues = LPArray.addValueToArray1D(fldValues, new Object[]{analyticalSequenceName, projectName});
        
        if (analyticalParameter!=null&&analyticalParameter.length()>0&&LPArray.valuePosicInArray(fldNames, TblsProjectRnDData.MethodDevelopmentSequence.ANALYTICAL_PARAMETER.getName())==-1){
            fldNames = LPArray.addValueToArray1D(fldNames, TblsProjectRnDData.MethodDevelopmentSequence.ANALYTICAL_PARAMETER.getName());
            fldValues = LPArray.addValueToArray1D(fldValues, analyticalParameter);            
        }
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        ResponseMessages messages = procReqSession.getMessages();
        
        RdbmsObject invLotCreationDiagn = Rdbms.insertRecordInTable(TablesProjectRnDData.METHOD_DEVELOPMENT_SEQUENCE, fldNames, fldValues);
        if (Boolean.FALSE.equals(invLotCreationDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotCreationDiagn.getErrorMessageCode(), new Object[]{projectName}, null);
        }
        AppProjectRnDAudit(ProjectsRnDEnums.ProjectRnDAuditEvents.PROJECT_CREATION, projectName, TablesProjectRnDData.METHOD_DEVELOPMENT_SEQUENCE.getTableName(), analyticalSequenceName,
                fldNames, fldValues);        

        if (analyticalParameter!=null&&analyticalParameter.length()>0){
            if (numSamplesToLog==null){
                Integer fldPosic=LPArray.valuePosicInArray(fldNames, TblsMethodValidationData.ValidationMethodParams.NUM_SAMPLES.getName());
                numSamplesToLog=(fldPosic==-1)? null:Integer.valueOf(fldValues[fldPosic].toString());
            }
            DataMethValSample MethSmp= new DataMethValSample();
            MethSmp.logAnalyticalParameterSamplelogParameterSample(projectName, analyticalSequenceName, analyticalParameter, null, null, 
                numSamplesToLog);
        }
        
        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesProjectRnDData.PROJECT.getTableName(), projectName);
        messages.addMainForSuccess(ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.NEW_ANALYTICAL_SEQUENCE, new Object[]{analyticalSequenceName});
        return new InternalMessage(LPPlatform.LAB_TRUE, ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints.NEW_ANALYTICAL_SEQUENCE, new Object[]{analyticalSequenceName}, projectName);
        
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
    public static InternalMessage updateTestAttribute(Integer testId, String attrName, String attrValue) {
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = procReqSession.getMessages();
        Token token = procReqSession.getToken();
        Integer fldPosicInArray = EnumIntTableFields.getFldPosicInArray(TblsMethodValidationData.TablesMethodValidationData.SAMPLE_ANALYSIS.getTableFields(), attrName);
        if (fldPosicInArray==-1)
            return new InternalMessage(LPPlatform.LAB_FALSE, MethodValidationEnums.ProjectRnDErrorTrapping.ATTRIBUTE_NOT_FOUND, new Object[]{attrName}, null);
        String[] fldNames = new String[]{attrName};
        
        EnumIntTableFields tableFieldDefinition = TblsMethodValidationData.TablesMethodValidationData.SAMPLE_ANALYSIS.getTableFields()[fldPosicInArray];
        Object[] fldValues = new Object[]{};
        if (LPDatabase.integer().equalsIgnoreCase(tableFieldDefinition.getFieldType())){
            fldValues=new Object[]{Integer.valueOf(attrValue)};
        }else{
            fldValues=new Object[]{attrValue};
        }
                
        SqlWhere wObj=new SqlWhere();
        wObj.addConstraint(TblsMethodValidationData.SampleAnalysis.TEST_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{testId}, null);
        RdbmsObject invLotCreationDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsMethodValidationData.TablesMethodValidationData.SAMPLE_ANALYSIS, 
            EnumIntTableFields.getTableFieldsFromString(TblsMethodValidationData.TablesMethodValidationData.SAMPLE_ANALYSIS, fldNames), fldValues, wObj, procReqSession.getProcedureInstance());
        if (Boolean.FALSE.equals(invLotCreationDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotCreationDiagn.getErrorMessageCode(), new Object[]{attrName.replace("_", " ")}, null);
        }
/*        AppProjectRnDAudit(ProjectsRnDEnums.ProjectRnDAuditEvents.TEST_ATTRIBUTE_UPDATE, attrName, TblsMethodValidationData.TablesMethodValidationData.SAMPLE_ANALYSIS.getTableName(), testId.toString(),
            fldNames, fldValues, this.projectName);        */

        return new InternalMessage(LPPlatform.LAB_TRUE, MethodValidationEnums.MethodValidationAPIactionsEndpoints.NEW_PARAMETER, new Object[]{attrName.replace("_", " ")}, testId);
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
