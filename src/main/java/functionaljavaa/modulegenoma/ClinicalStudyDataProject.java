/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import module.clinicalstudies.apis.GenomaProjectAPI;
import module.clinicalstudies.definition.TblsGenomaData;
import databases.DataDataIntegrity;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import databases.features.Token;
import functionaljavaa.modulegenoma.ClinicalStudyEnums.GenomaErrorTrapping;
import functionaljavaa.modulegenoma.ClinicalStudyEnums.GenomaSuccess;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ResponseMessages;

/**
 *
 * @author User
 */
public class ClinicalStudyDataProject {

    public InternalMessage createProject(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName, String[] fieldsName, Object[] fieldsValue, Boolean devMode) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        String[] mandatoryFieldsProj = null;
        Object[] mandatoryFieldsValueProj = fieldsValue;
        DataDataIntegrity labIntChecker = new DataDataIntegrity();
        if (fieldsName == null) {
            fieldsName = new String[0];
        }
        if (fieldsValue == null) {
            fieldsValue = new Object[0];
        }

        String tableName = "project";
        String actionName = "Insert";
        String schemaDataName = GlobalVariables.Schemas.DATA.getName();
        schemaDataName = LPPlatform.buildSchemaName(procInstanceName, schemaDataName);
        mandatoryFieldsProj = labIntChecker.getTableMandatoryFields(tableName, actionName);

        if (Boolean.FALSE.equals(devMode)) {
            InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(fieldsName, fieldsValue);
            if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic()))) {
                return fieldNameValueArrayChecker;
            }
        }
        if (Boolean.FALSE.equals(devMode)) {
            StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);
            for (Integer inumLines = 0; inumLines < mandatoryFieldsProj.length; inumLines++) {
                String currField = mandatoryFieldsProj[inumLines];
                boolean contains = Arrays.asList(fieldsName).contains(currField.toLowerCase());
                if (Boolean.FALSE.equals(contains)) {
                    if (mandatoryFieldsMissingBuilder.length() > 0) {
                        mandatoryFieldsMissingBuilder.append(",");
                    }

                    mandatoryFieldsMissingBuilder.append(currField);

                } else {
                    Integer valuePosic = Arrays.asList(fieldsName).indexOf(currField);
                    if (fieldsValue != null && fieldsValue.length >= valuePosic && mandatoryFieldsValueProj != null && mandatoryFieldsValueProj.length >= inumLines) {
                        mandatoryFieldsValueProj[inumLines] = fieldsValue[valuePosic];
                    }
                }
            }
            if (mandatoryFieldsMissingBuilder.length() > 0) {
                messages.addMainForError(ClinicalStudyEnums.GenomaErrorTrapping.NEW_PROJECT_MISSING_MANDATORY_FIELDS, new String[]{projectName, mandatoryFieldsMissingBuilder.toString(), procInstanceName});
                return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.NEW_PROJECT_MISSING_MANDATORY_FIELDS, new String[]{projectName, mandatoryFieldsMissingBuilder.toString(), procInstanceName});
            }
            Object[] diagnosis = Rdbms.existsRecord(procInstanceName,
                    LPPlatform.buildSchemaName(procInstanceName, TblsGenomaData.TablesGenomaData.PROJECT.getRepositoryName()),
                     TblsGenomaData.TablesGenomaData.PROJECT.getTableName(),
                    new String[]{TblsGenomaData.Project.NAME.getName()}, new Object[]{projectName});
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())) {
                messages.addMainForError(GenomaErrorTrapping.PROJECT_ALREADY_EXISTS, new Object[]{projectName, procInstanceName});
                return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.PROJECT_ALREADY_EXISTS, new Object[]{projectName, procInstanceName});
            }

            /*        String[] specialFields = labIntChecker.getStructureSpecialFields(schemaDataName, "projectStructure", actionName);
        String[] specialFieldsFunction = labIntChecker.getStructureSpecialFieldsFunction(schemaDataName, "projectStructure", actionName);
        
        String specialFieldsCheck = "";
        Integer specialFieldIndex = -1;
        for (Integer inumLines=0;inumLines<fieldsName.length;inumLines++){
            String currField = tableName+"." + fieldsName[inumLines];
            String currFieldValue = fieldsValue[inumLines].toString();
            boolean contains = Arrays.asList(specialFields).contains(currField);
            if (contains){                    
                    specialFieldIndex = Arrays.asList(specialFields).indexOf(currField);
                    String aMethod = specialFieldsFunction[specialFieldIndex];
                    Method method = null;
                    try {
                        Class<?>[] paramTypes = {Rdbms.class, String[].class, String.class, String.class, Integer.class};
                        method = getClass().getDeclaredMethod(aMethod, paramTypes);
                    } catch (NoSuchMethodException | SecurityException ex) {
                        Object[] errorDetailVariables = new Object[0];
                        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, ex.getMessage());
                        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, errorDetailVariables);                        
                    }
                    Object specialFunctionReturn = method.invoke(this, null, procInstanceName, projectTemplate, projectTemplateVersion);      
                    if (specialFunctionReturn.toString().contains("ERROR")){
                        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
                        diagnosesProj[0]= elements[1].getClassName() + "." + elements[1].getMethodName();
                        diagnosesProj[1]= classVersionProj;
                        diagnosesProj[2]= "Code Line " + (elements[1].getLineNumber());
                        diagnosesProj[3]=LPPlatform.LAB_FALSE;
                        diagnosesProj[4]=specialFunctionReturn.toString();
                        diagnosesProj[5]="The field " + currField + " is considered special and its checker (" + aMethod + ") returned the Error above";
                        return diagnosesProj;                            
                    }                
                    
            }
        }
             */
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.NAME.getName()) == -1) {
                fieldsName = LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Project.NAME.getName());
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectName);
            } else {
                fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.NAME.getName())] = projectName;
            }
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.CREATED_ON.getName()) == -1) {
                fieldsName = LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Project.CREATED_ON.getName());
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, LPDate.getCurrentTimeStamp());
            } else {
                fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.CREATED_ON.getName())] = LPDate.getCurrentTimeStamp();
            }
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.CREATED_BY.getName()) == -1) {
                fieldsName = LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Project.CREATED_BY.getName());
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, token.getPersonName());
            } else {
                fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.CREATED_BY.getName())] = token.getPersonName();
            }
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.ACTIVE.getName()) == -1) {
                fieldsName = LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Project.ACTIVE.getName());
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.PROJECT.getTableName()));
            } else {
                fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.ACTIVE.getName())] = ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.PROJECT.getTableName());
            }
            /*        fieldsName = LPArray.addValueToArray1D(fieldsName, GlobalVariables.Schemas.CONFIG.getName());    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplate);
        fieldsName = LPArray.addValueToArray1D(fieldsName, "config_version");    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplateVersion); 
             */
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.PROJECT, fieldsName, fieldsValue);
            if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
                messages.addMinorForSuccess(endpoint, new Object[]{projectName}); 
                ClinicalStudyDataAudit.projectAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.PROJECT, projectName,
                        projectName, null, fieldsName, fieldsValue);
                return new InternalMessage(LPPlatform.LAB_TRUE, GenomaSuccess.PROJECT_CREATED, insertRecordInTable.getErrorMessageVariables(), insertRecordInTable.getNewRowId());
            }
            messages.addMainForError(insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
        }
        messages.addMainForError(TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, null);
        return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, null, null);
    }

    public InternalMessage projectActivate(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        InternalMessage projOpenToChanges = isProjectOpenToChanges2(projectName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
            messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
            return projOpenToChanges;
        }
        String[] fieldsName = new String[]{TblsGenomaData.Project.ACTIVE.getName()};
        Object[] fieldsValue = new Object[]{true};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.Project.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{projectName}, "");
        RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.PROJECT,
                EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.PROJECT, fieldsName), fieldsValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
            messages.addMinorForSuccess(endpoint, new Object[]{projectName});
            ClinicalStudyDataAudit.projectAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.PROJECT, projectName,
                    projectName, null, fieldsName, fieldsValue);
        }
        messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }

    public InternalMessage projectDeActivate(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        InternalMessage projOpenToChanges = isProjectOpenToChanges2(projectName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
            messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
            return projOpenToChanges;
        }
        String[] fieldsName = new String[]{TblsGenomaData.Project.ACTIVE.getName(), TblsGenomaData.Project.DEACTIVATED_BY.getName(), TblsGenomaData.Project.DEACTIVATED_ON.getName()};
        Object[] fieldsValue = new Object[]{false, instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.Project.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{projectName}, "");
        RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.PROJECT,
                EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.PROJECT, fieldsName), fieldsValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
            messages.addMinorForSuccess(endpoint, new Object[]{projectName});
            ClinicalStudyDataAudit.projectAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.PROJECT, projectName,
                    projectName, null, fieldsName, fieldsValue);
        }
        messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }

    public InternalMessage projectUpdate(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName, String[] fieldsName, Object[] fieldsValue) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        InternalMessage projOpenToChanges = isProjectOpenToChanges2(projectName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
            messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
            return projOpenToChanges;
        }
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.Project.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{projectName}, "");
        RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.PROJECT,
                EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.PROJECT, fieldsName), fieldsValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
            messages.addMinorForSuccess(endpoint, new Object[]{projectName});
            ClinicalStudyDataAudit.projectAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.PROJECT, projectName,
                    projectName, null, fieldsName, fieldsValue);
        }
        messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }

    public InternalMessage projectUserManagement(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName, String userName, String userRole) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        String[] fieldsName = new String[]{TblsGenomaData.ProjectUsers.PROJECT.getName(), TblsGenomaData.ProjectUsers.PERSON.getName(), TblsGenomaData.ProjectUsers.ROLES.getName()};
        Object[] fieldsValue = new Object[]{projectName, userName, userRole};

        InternalMessage projOpenToChanges = isProjectOpenToChanges2(projectName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
            messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
            return projOpenToChanges;
        }
        InternalMessage diagnosesProj = null;
        switch (endpoint) {
            case PROJECT_ADD_USER:
                fieldsName = LPArray.addValueToArray1D(fieldsName, TblsGenomaData.ProjectUsers.ACTIVE.getName());
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.PROJECT_USERS.getTableName()));
                RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.PROJECT_USERS, fieldsName, fieldsValue);
                
                if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
                    messages.addMainForError(insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());
                    return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());
                }
                messages.addMainForSuccess(endpoint, insertRecordInTable.getErrorMessageVariables());
                ClinicalStudyDataAudit.projectAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.PROJECT, projectName,
                        projectName, null, fieldsName, fieldsValue);
                break;

            case PROJECT_REMOVE_USER:
                fieldsName = LPArray.addValueToArray1D(fieldsName, TblsGenomaData.ProjectUsers.ACTIVE.getName());
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.PROJECT_USERS.getTableName()));
                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PROJECT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{projectName}, null);
                sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, null);
                RdbmsObject removeRecordInTable = Rdbms.removeRecordInTable(TblsGenomaData.TablesGenomaData.PROJECT_USERS,
                        sqlWhere, null);
                messages.addMainForSuccess(endpoint, removeRecordInTable.getErrorMessageVariables());
                if (Boolean.FALSE.equals(removeRecordInTable.getRunSuccess())){
                    messages.addMainForError(removeRecordInTable.getErrorMessageCode(), removeRecordInTable.getErrorMessageVariables());
                    return new InternalMessage(LPPlatform.LAB_FALSE, removeRecordInTable.getErrorMessageCode(), removeRecordInTable.getErrorMessageVariables());
                }
                ClinicalStudyDataAudit.projectAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.PROJECT, projectName,
                        projectName, null, fieldsName, fieldsValue);
                break;
            case PROJECT_USER_ACTIVATE:
                diagnosesProj = projectUserActivate(endpoint, projectName, userName, userRole);
                break;
            case PROJECT_USER_DEACTIVATE:
                diagnosesProj = projectUserDeActivate(endpoint, projectName, userName, userRole);
                break;
            case PROJECT_CHANGE_USER_ROLE:
                diagnosesProj = projectUserChangeRole(endpoint, projectName, userName, userRole);
                break;
            default:
                messages.addMainForError(TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
                return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
        }
        return diagnosesProj;
    }

    public InternalMessage projectUserActivate(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName, String userName, String userRole) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        InternalMessage projOpenToChanges = isProjectOpenToChanges2(projectName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
            messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
            return projOpenToChanges;
        }
        String[] fieldsName = new String[]{TblsGenomaData.ProjectUsers.ACTIVE.getName()};
        Object[] fieldsValue = new Object[]{true};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PROJECT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{projectName}, "");
        sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, "");
        if (LPNulls.replaceNull(userRole).length() > 0) {
            sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.ROLES, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userRole}, "");
        }
        RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.PROJECT_USERS,
                EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.PROJECT_USERS, fieldsName), fieldsValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
            messages.addMinorForSuccess(endpoint, new Object[]{projectName, userName, userRole});
            ClinicalStudyDataAudit.projectAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.PROJECT_USERS, projectName,
                    projectName, null, fieldsName, fieldsValue);
        }
        messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }

    public InternalMessage projectUserDeActivate(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName, String userName, String userRole) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        InternalMessage projOpenToChanges = isProjectOpenToChanges2(projectName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
            messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
            return projOpenToChanges;
        }
        String[] fieldsName = new String[]{TblsGenomaData.ProjectUsers.ACTIVE.getName(), TblsGenomaData.ProjectUsers.DEACTIVATED_BY.getName(), TblsGenomaData.ProjectUsers.DEACTIVATED_ON.getName()};
        Object[] fieldsValue = new Object[]{false, instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PROJECT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{projectName}, "");
        sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, "");
        sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.ROLES, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userRole}, "");
        RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.PROJECT_USERS,
                EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.PROJECT_USERS, fieldsName), fieldsValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
            messages.addMinorForSuccess(endpoint, new Object[]{projectName, userName, userRole});
            ClinicalStudyDataAudit.projectAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.PROJECT_USERS, projectName,
                    projectName, null, fieldsName, fieldsValue);
        }
        messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }

    public static Object[] isProjectOpenToChanges(String projectName) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.PROJECT.getTableName(),
                new String[]{TblsGenomaData.Project.NAME.getName()}, new Object[]{projectName}, new String[]{TblsGenomaData.Project.ACTIVE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The project <*1*> does not exist in procedure <*2*>", new Object[]{projectName, procInstanceName});
        }
        if (Boolean.FALSE.equals(Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString()))) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The project <*1*> is already inactive in procedure <*2*>", new Object[]{projectName, procInstanceName});
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "<*1*> is open to changes in procedure <*2*>", new Object[]{projectName, procInstanceName});
    }

    public static InternalMessage isProjectOpenToChanges2(String projectName) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.PROJECT.getTableName(),
                new String[]{TblsGenomaData.Project.NAME.getName()}, new Object[]{projectName}, new String[]{TblsGenomaData.Project.ACTIVE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()) &&
            (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) ){
                return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.PROJECT_NOT_FOUND, new Object[]{projectName, procInstanceName});            
        }
        if (Boolean.FALSE.equals(Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString()))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.PROJECT_ALREADY_INACTIVE, new Object[]{projectName, procInstanceName});
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, GenomaSuccess.PROJECT_OPEN_TO_CHANGES, new Object[]{projectName, procInstanceName});
    }

    public InternalMessage projectUserChangeRole(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName, String userName, String userRole) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        String[] fieldsName = new String[]{TblsGenomaData.ProjectUsers.ROLES.getName()};
        Object[] fieldsValue = new Object[]{userRole};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PROJECT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{projectName}, "");
        sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, "");
        RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.PROJECT_USERS,
                EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.PROJECT_USERS, fieldsName), fieldsValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
            messages.addMinorForSuccess(endpoint, new Object[]{projectName, userName, userRole});
            ClinicalStudyDataAudit.projectAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.PROJECT_USERS, projectName,
                    projectName, null, fieldsName, fieldsValue);
            return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{projectName, userName, userRole});
        }
        messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }

}
