package module.projectrnd.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.features.Token;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.io.File;
import trazit.session.ResponseMessages;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFilesTools;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPaws;
import module.projectrnd.definition.DailyEntryEnums;
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
public class DataDailyEntry {

    /**
     * @return the RdDailyEntryName
     */
    public String getFormulaName() {
        return dailyEntryName;
    }

    /**
     * @return the reference
     */

    /**
     * @return the category
     */
    private final String dailyEntryName;
    private Boolean isLocked;
    private Boolean isRetired;
    private String lockedReason;
    private String[] dailyEntryFieldNames;
    private Object[] dailyEntryFieldValues;
    private Boolean hasError;
    private InternalMessage errorDetail;
//    private String[] ingredientsFieldNames;
//    private Object[] ingredientsFieldValues;

    public DataDailyEntry(String dailyEntryName) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = procReqSession.getProcedureInstance();
        Object[][] projectRnDinfo = null;
        projectRnDinfo = Rdbms.getRecordFieldsByFilter(procInstanceName,  LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsProjectRnDData.TablesProjectRnDData.RD_DAILY_ENTRY.getTableName(),
                new String[]{TblsProjectRnDData.RdDailyEntry.NAME.getName()},
                new Object[]{dailyEntryName}, getAllFieldNames(TblsProjectRnDData.TablesProjectRnDData.RD_DAILY_ENTRY.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projectRnDinfo[0][0].toString())) {
            this.dailyEntryName = null;
            this.hasError = true;
            this.errorDetail = new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{dailyEntryName, TablesProjectRnDData.RD_DAILY_ENTRY.getTableName(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())}, dailyEntryName);
        } else {
            this.hasError = false;
            this.dailyEntryFieldNames = getAllFieldNames(TblsProjectRnDData.TablesProjectRnDData.RD_DAILY_ENTRY.getTableFields());
            this.dailyEntryFieldValues = projectRnDinfo[0];
            this.dailyEntryName = LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(dailyEntryFieldNames, TblsProjectRnDData.RdDailyEntry.NAME.getName())]).toString();
            this.isLocked = Boolean.valueOf(LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(dailyEntryFieldNames, TblsProjectRnDData.RdDailyEntry.IS_LOCKED.getName())]).toString());
            if (this.isLocked == null) {
                this.isLocked = false;
            }
            this.lockedReason = LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(dailyEntryFieldNames, TblsProjectRnDData.Project.LOCKED_REASON.getName())]).toString();
        }
    }

    public static InternalMessage createNewDailyEntry(String dailyEntryName, String projectName, String[] fldNames, Object[] fldValues) {
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = procReqSession.getMessages();
        Token token = procReqSession.getToken();
        if (fldNames == null) {
            fldNames = new String[]{};
            fldValues = new Object[]{};
        }
        Object[][] referenceInfo = null;
        
        fldNames = LPArray.addValueToArray1D(fldNames, TblsProjectRnDData.RdDailyEntry.NAME.getName());
        fldValues = LPArray.addValueToArray1D(fldValues, dailyEntryName);

        fldNames = LPArray.addValueToArray1D(fldNames, TblsProjectRnDData.RdDailyEntry.PROJECT.getName());
        fldValues = LPArray.addValueToArray1D(fldValues, projectName);

        fldNames = LPArray.addValueToArray1D(fldNames, new String[]{TblsProjectRnDData.RdDailyEntry.CREATED_ON.getName(), TblsProjectRnDData.Project.CREATED_BY.getName()});
        fldValues = LPArray.addValueToArray1D(fldValues, new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()});

        RdbmsObject invLotCreationDiagn = Rdbms.insertRecordInTable(TablesProjectRnDData.RD_DAILY_ENTRY, fldNames, fldValues);
        if (Boolean.FALSE.equals(invLotCreationDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotCreationDiagn.getErrorMessageCode(), new Object[]{projectName}, null);
        }
        AppProjectRnDAudit(DailyEntryEnums.DailyEntryAuditEvents.DAILY_ENTRY_CREATION, projectName, TablesProjectRnDData.RD_DAILY_ENTRY.getTableName(), dailyEntryName,
                fldNames, fldValues);        
        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesProjectRnDData.PROJECT.getTableName(), projectName);
        messages.addMainForSuccess(DailyEntryEnums.DailyEntryAPIactionsEndpoints.NEW_DAILY_ENTRY, new Object[]{dailyEntryName, projectName});
        return new InternalMessage(LPPlatform.LAB_TRUE, DailyEntryEnums.DailyEntryAPIactionsEndpoints.NEW_DAILY_ENTRY, new Object[]{dailyEntryName, projectName}, dailyEntryName);
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

    public InternalMessage addPicture(String projectName, byte[] picture, String[] fldNames, Object[] fldValues){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = procReqSession.getProcedureInstance();
        LPaws aws=new LPaws(null);
        if (aws.getHasError()){
            return aws.getErrorDetail();
        }
        String uploadKey = "projectName"+projectName+"_dailyentry"+this.dailyEntryName+"_"+this.hashCode()+".jpeg";
        //String pdfPath = "D:/LP/Interfaces/HPLC_VALIDACIONES_FRAN_382.pdf";
        
        File pictureFile=LPFilesTools.byteArrToFile(projectName, picture);             
        aws.uploadFile(uploadKey, pictureFile);
        
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsProjectRnDData.ProjectAttachments.DAILY_ENTRY_NAME.getName(), TblsProjectRnDData.ProjectAttachments.PROJECT_NAME.getName(), TblsProjectRnDData.ProjectAttachments.AWS_FILE.getName(),
            TblsProjectRnDData.ProjectAttachments.CREATED_BY.getName(), TblsProjectRnDData.ProjectAttachments.CREATED_ON.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{this.dailyEntryName, projectName, uploadKey, procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()});
        RdbmsObject insertRecordInTable = Rdbms.insertRecord(TblsProjectRnDData.TablesProjectRnDData.PROJECT_ATTACHMENT, 
                fldNames, fldValues, procReqSession.getProcedureInstance());
        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
        }
        
        
        return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE,null);
    }

    public InternalMessage addAwsAttachment(String projectName, byte[] file, String[] fldNames, Object[] fldValues){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = procReqSession.getProcedureInstance();
        LPaws aws=new LPaws(null);
        if (aws.getHasError()){
            return aws.getErrorDetail();
        }
        String uploadKey = "projectName"+projectName+"_dailyentry"+this.dailyEntryName+"_"+this.hashCode();
        File pictureFile=LPFilesTools.byteArrToFile(uploadKey, file);             
        aws.uploadFile(uploadKey, pictureFile);
        
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsProjectRnDData.ProjectAttachments.DAILY_ENTRY_NAME.getName(), TblsProjectRnDData.ProjectAttachments.PROJECT_NAME.getName(), TblsProjectRnDData.ProjectAttachments.AWS_FILE.getName(),
            TblsProjectRnDData.ProjectAttachments.ORIGINAL_FILE_NAME.getName(), TblsProjectRnDData.ProjectAttachments.CREATED_BY.getName(), TblsProjectRnDData.ProjectAttachments.CREATED_ON.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{this.dailyEntryName, projectName, uploadKey, 
            uploadKey, procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()});
        RdbmsObject insertRecordInTable = Rdbms.insertRecord(TblsProjectRnDData.TablesProjectRnDData.PROJECT_ATTACHMENT, 
                fldNames, fldValues, procReqSession.getProcedureInstance());
        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE,null);
    }

    public InternalMessage addUrlAttachment(String projectName, String urlLink, String[] fldNames, Object[] fldValues){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = procReqSession.getProcedureInstance();
        
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsProjectRnDData.ProjectAttachments.DAILY_ENTRY_NAME.getName(), TblsProjectRnDData.ProjectAttachments.PROJECT_NAME.getName(), 
            TblsProjectRnDData.ProjectAttachments.FILE_LINK.getName(), TblsProjectRnDData.ProjectAttachments.CREATED_BY.getName(), TblsProjectRnDData.ProjectAttachments.CREATED_ON.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{this.dailyEntryName, projectName,  
            urlLink, procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()});
        RdbmsObject insertRecordInTable = Rdbms.insertRecord(TblsProjectRnDData.TablesProjectRnDData.PROJECT_ATTACHMENT, 
                fldNames, fldValues, procReqSession.getProcedureInstance());
        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE,null);
    }    
    
    public InternalMessage addNote(String projectName, String note, String[] fldNames, Object[] fldValues){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = procReqSession.getProcedureInstance();

        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsProjectRnDData.ProjectNotes.DAILY_ENTRY_NAME.getName(), TblsProjectRnDData.ProjectNotes.PROJECT_NAME.getName(), TblsProjectRnDData.ProjectNotes.NOTES.getName(),
            TblsProjectRnDData.ProjectNotes.CREATED_BY.getName(), TblsProjectRnDData.ProjectNotes.CREATED_ON.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{this.dailyEntryName, projectName, note,
            procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()});
        RdbmsObject insertRecordInTable = Rdbms.insertRecord(TblsProjectRnDData.TablesProjectRnDData.PROJECT_NOTES, 
                fldNames, fldValues, procReqSession.getProcedureInstance());
        if (insertRecordInTable.getRunSuccess()){
            return new InternalMessage(LPPlatform.LAB_TRUE,DailyEntryEnums.DailyEntryAPIactionsEndpoints.DAILY_ENTRY_ADDNOTE, new Object[]{this.dailyEntryName}, null);
        }else{
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);            
        }
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

/*    public String[] getQualificationFieldNames() {
        return ingredientsFieldNames;
    }

    public Object[] getQualificationFieldValues() {
        return ingredientsFieldValues;
    }
*/
    public String[] getLotFieldNames() {
        return dailyEntryFieldNames;
    }

    public Object[] getLotFieldValues() {
        return dailyEntryFieldValues;
    }

}
