/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import module.clinicalstudies.apis.GenomaStudyAPI;
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
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ResponseMessages;
/**
 *
 * @author User
 */
public class ClinicalStudyDataStudy {
public InternalMessage createStudy(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String projectName, String[] fieldsName, Object[] fieldsValue, Boolean devMode){
    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
    String procInstanceName=instanceForActions.getProcedureInstance();
    Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
    ResponseMessages messages = instanceForActions.getMessages();    
    
    InternalMessage projectOpenToChanges = ClinicalStudyDataProject.isProjectOpenToChanges2(projectName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projectOpenToChanges.getDiagnostic())) return projectOpenToChanges;
   
    String[] mandatoryFieldsProj = null;
    Object[] mandatoryFieldsValueProj = fieldsValue;
    DataDataIntegrity labIntChecker = new DataDataIntegrity();
    if (fieldsName==null) fieldsName=new String[0];
    if (fieldsValue==null) fieldsValue=new Object[0];

    String actionName = "Insert";
    mandatoryFieldsProj = labIntChecker.getTableMandatoryFields(TblsGenomaData.TablesGenomaData.STUDY.getTableName(), actionName);
    if (Boolean.FALSE.equals(devMode)){
            InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(fieldsName, fieldsValue);
            messages.addMainForError(fieldNameValueArrayChecker.getMessageCodeObj(), fieldNameValueArrayChecker.getMessageCodeVariables());
            if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic()))){
                messages.addMainForError(fieldNameValueArrayChecker.getMessageCodeObj(), fieldNameValueArrayChecker.getMessageCodeVariables());
                return fieldNameValueArrayChecker;
            }            
    }    
    if (Boolean.FALSE.equals(devMode)){        
        StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);
        for (Integer inumLines=0;inumLines<mandatoryFieldsProj.length;inumLines++){
            String currField = mandatoryFieldsProj[inumLines];
            boolean contains = Arrays.asList(fieldsName).contains(currField.toLowerCase());
            if (Boolean.FALSE.equals(contains)){
                
                if (mandatoryFieldsMissingBuilder.length()>0){mandatoryFieldsMissingBuilder.append(",");}
                
                mandatoryFieldsMissingBuilder.append(currField);
                
            }else{
                Integer valuePosic = Arrays.asList(fieldsName).indexOf(currField);
                if (fieldsValue!=null && fieldsValue.length>=valuePosic && mandatoryFieldsValueProj!=null && mandatoryFieldsValueProj.length>=inumLines) mandatoryFieldsValueProj[inumLines] = fieldsValue[valuePosic]; 
            }        
        }            
        if (mandatoryFieldsMissingBuilder.length()>0){
            messages.addMainForError(ClinicalStudyEnums.GenomaErrorTrapping.NEW_PROJECT_MISSING_MANDATORY_FIELDS, new String[]{studyName, mandatoryFieldsMissingBuilder.toString(), procInstanceName});
            return new InternalMessage(LPPlatform.LAB_FALSE, ClinicalStudyEnums.GenomaErrorTrapping.NEW_PROJECT_MISSING_MANDATORY_FIELDS, new String[]{studyName, mandatoryFieldsMissingBuilder.toString(), procInstanceName});
        }        
/*        Object[] diagnosis = Rdbms.existsRecord(schemaConfigName, tableName, new String[]{GlobalVariables.Schemas.CONFIG.getName(),"config_version"}, new Object[]{projectTemplate, projectTemplateVersion});
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())){	
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            diagnosesProj[0]= elements[1].getClassName() + "." + elements[1].getMethodName();
            diagnosesProj[1]= classVersionProj;
            diagnosesProj[2]= "Code Line " + (elements[1].getLineNumber());
            diagnosesProj[3]=LPPlatform.LAB_FALSE;
            diagnosesProj[4]="ERROR:Sample Config Code NOT FOUND";
            diagnosesProj[5]="The sample config code "+projectTemplate+" in its version "+projectTemplateVersion+" was not found in the schema "+schemaConfigName+". Detail:"+diagnosis[5];
            return diagnosesProj;
        }
*/
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
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.PROJECT.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Study.PROJECT.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, projectName);
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.PROJECT.getName())] = projectName;
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.NAME.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Study.NAME.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, studyName);
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.NAME.getName())] = studyName;
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.CREATED_ON.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Study.CREATED_ON.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, LPDate.getCurrentTimeStamp());
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.CREATED_ON.getName())] = LPDate.getCurrentTimeStamp();
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.CREATED_BY.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Study.CREATED_BY.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, token.getPersonName());
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.CREATED_BY.getName())] = token.getPersonName();
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.ACTIVE.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Study.ACTIVE.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName()));
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.ACTIVE.getName())] = ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName());        
/*        fieldsName = LPArray.addValueToArray1D(fieldsName, GlobalVariables.Schemas.CONFIG.getName());    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplate);
        fieldsName = LPArray.addValueToArray1D(fieldsName, "config_version");    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplateVersion); 
*/
        
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY, fieldsName, fieldsValue);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())){
            messages.addMinorForSuccess(endpoint, fieldsValue);
            ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY, studyName, 
                studyName, projectName, fieldsName, fieldsValue);
            ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.PROJECT, projectName, 
                projectName, studyName, fieldsName, fieldsValue);
                return new InternalMessage(LPPlatform.LAB_TRUE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), insertRecordInTable.getNewRowId());
            }
            messages.addMainForError(insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);            
        } 
        messages.addMainForError(TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, null);
        return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, null, null);            
}    

public InternalMessage studyActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName){
    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
    ResponseMessages messages = instanceForActions.getMessages();    
    String[] fieldsName=new String[]{TblsGenomaData.Study.ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{true};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.Study.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY, fieldsName), fieldsValue, sqlWhere, null);
    if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
        messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }
    messages.addMinorForSuccess(endpoint, new Object[]{studyName});
    return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
}    

public InternalMessage studyDeActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName){
    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
    ResponseMessages messages = instanceForActions.getMessages();    
    InternalMessage projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
        messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
        return projOpenToChanges;
    }
    String[] fieldsName=new String[]{TblsGenomaData.Study.ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{false};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.Study.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY, fieldsName), fieldsValue, sqlWhere, null);
    if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
        messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }
    messages.addMinorForSuccess(endpoint, new Object[]{studyName});
    return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
}   

public InternalMessage studyUpdate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String[] fieldsName, Object[] fieldsValue){
    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
    ResponseMessages messages = instanceForActions.getMessages();    
    InternalMessage projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
        messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
        return projOpenToChanges;
    }
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.Study.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY, fieldsName), fieldsValue, sqlWhere, null);
    if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
        messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }
    messages.addMinorForSuccess(endpoint, new Object[]{studyName});
    return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
} 

public InternalMessage studyUserManagement(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String userName, String userRole){
    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
    ResponseMessages messages = instanceForActions.getMessages();    
    String[] fieldsName = new String[]{TblsGenomaData.StudyUsers.STUDY.getName(), TblsGenomaData.StudyUsers.PERSON.getName(), TblsGenomaData.StudyUsers.ROLES.getName()};
    Object[] fieldsValue=new Object[]{studyName, userName, userRole};
    
    InternalMessage projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())) return projOpenToChanges;
    InternalMessage diagnosesProj = null;
    switch (endpoint){
        //PROJECT_REMOVE_USER, PROJECT_CHANGE_USER_ROLE, 
        case STUDY_ADD_USER:
            fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyUsers.ACTIVE.getName());
            fieldsValue=LPArray.addValueToArray1D(fieldsValue, ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_USERS.getTableName()));            
            RdbmsObject diagnosesProjRdbms = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_USERS, fieldsName, fieldsValue);            
            if (Boolean.TRUE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj.getDiagnostic())))
                ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY, studyName, 
                    studyName, null, fieldsName, fieldsValue);    
            if (Boolean.FALSE.equals(diagnosesProjRdbms.getRunSuccess())) {
                diagnosesProj=new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProjRdbms.getErrorMessageCode(), diagnosesProjRdbms.getErrorMessageVariables());
            }else{
                diagnosesProj=new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName, userName, userRole});
            }            
            break;
        case STUDY_USER_ACTIVATE:
            diagnosesProj = studyUserActivate(endpoint, studyName, userName, userRole);
            break;
        case STUDY_USER_DEACTIVATE:
            diagnosesProj = studyUserDeActivate(endpoint, studyName, userName, userRole);
            break;
        case STUDY_CHANGE_USER_ROLE: 
            diagnosesProj = studyUserChangeRole(endpoint, studyName, userName, userRole);
            break;            
        default:
            return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
    }
    return diagnosesProj;      
} 

public InternalMessage studyUserActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String userName, String userRole){
    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
    ResponseMessages messages = instanceForActions.getMessages();    
    InternalMessage projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
        messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
        return projOpenToChanges;
    }
    String[] fieldsName=new String[]{TblsGenomaData.StudyUsers.ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{true};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudyUsers.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    sqlWhere.addConstraint(TblsGenomaData.StudyUsers.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, "");
    if (LPNulls.replaceNull(userRole).length()>0)
        sqlWhere.addConstraint(TblsGenomaData.StudyUsers.ROLES, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userRole}, "");
    RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_USERS,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_USERS, fieldsName), fieldsValue, sqlWhere, null);
    if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
        messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }
    messages.addMinorForSuccess(endpoint, new Object[]{studyName});
    return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName, userName, userRole});
}    

public InternalMessage studyUserDeActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String userName, String userRole){
    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
    ResponseMessages messages = instanceForActions.getMessages();    
    InternalMessage projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
        messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
        return projOpenToChanges;
    }

    String[] fieldsName=new String[]{TblsGenomaData.StudyUsers.ACTIVE.getName(), TblsGenomaData.StudyUsers.DEACTIVATED_BY.getName(), TblsGenomaData.StudyUsers.DEACTIVATED_ON.getName()};
    Object[] fieldsValue=new Object[]{false, instanceForActions.getToken().getPersonName(),LPDate.getCurrentTimeStamp()};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudyUsers.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    sqlWhere.addConstraint(TblsGenomaData.StudyUsers.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, "");
    sqlWhere.addConstraint(TblsGenomaData.StudyUsers.ROLES, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userRole}, "");
    RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_USERS,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_USERS, fieldsName), fieldsValue, sqlWhere, null);
    if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
        messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }
    messages.addMinorForSuccess(endpoint, new Object[]{studyName});
    return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName, userName, userRole});
}    

/*public static InternalMessage isStudyOpenToChanges(String studyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(procInstanceName,LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY.getTableName(),
            new String[]{TblsGenomaData.Study.NAME.getName()}, new Object[]{studyName}, new String[]{TblsGenomaData.Study.ACTIVE.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
        return new InternalMessage(LPPlatform.LAB_FALSE, "The project <*1*> does not exist in procedure <*2*>", new Object[]{studyName, procInstanceName});
    if (Boolean.FALSE.equals(Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString())))
        return new InternalMessage(LPPlatform.LAB_FALSE, "The project <*1*> is already inactive in procedure <*2*>", new Object[]{studyName, procInstanceName});
    return new InternalMessage(LPPlatform.LAB_TRUE, "<*1*> is open to changes in procedure <*2*>", new Object[]{studyName, procInstanceName});
}
*/
public static InternalMessage isStudyOpenToChanges(String studyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(procInstanceName,LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY.getTableName(),
            new String[]{TblsGenomaData.Study.NAME.getName()}, new Object[]{studyName}, new String[]{TblsGenomaData.Study.ACTIVE.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
        return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.STUDY_NOT_FOUND, new Object[]{studyName, procInstanceName});
    if (Boolean.FALSE.equals(Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString())))
        return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.STUDY_ALREADY_INACTIVE, new Object[]{studyName, procInstanceName});
    return new InternalMessage(LPPlatform.LAB_TRUE, GenomaSuccess.STUDY_OPEN_TO_CHANGES, new Object[]{studyName, procInstanceName});
}


public InternalMessage studyUserChangeRole(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String userName, String userRole){
    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
    ResponseMessages messages = instanceForActions.getMessages();    
    String[] fieldsName=new String[]{TblsGenomaData.StudyUsers.ROLES.getName()};
    Object[] fieldsValue=new Object[]{userRole};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudyUsers.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    sqlWhere.addConstraint(TblsGenomaData.StudyUsers.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, "");
    RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_USERS,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_USERS, fieldsName), fieldsValue, sqlWhere, null);
    if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
        ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_USERS, studyName, 
            studyName, null, fieldsName, fieldsValue);
        messages.addMinorForSuccess(endpoint, new Object[]{studyName, userName, userRole});
        return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName, userName, userRole});    
    }else{
        messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        return new InternalMessage(LPPlatform.LAB_FALSE, endpoint, new Object[]{studyName, userName, userRole});  
    }
}    

}
