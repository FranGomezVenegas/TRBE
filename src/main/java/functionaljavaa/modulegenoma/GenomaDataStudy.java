/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import com.labplanet.servicios.modulegenoma.GenomaStudyAPI;
import com.labplanet.servicios.modulegenoma.TblsGenomaData;
import databases.DataDataIntegrity;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import databases.features.Token;
import functionaljavaa.modulegenoma.GenomaEnums.GenomaErrorTrapping;
import functionaljavaa.modulegenoma.GenomaEnums.GenomaSuccess;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
/**
 *
 * @author User
 */
public class GenomaDataStudy {
public InternalMessage createStudy(GenomaStudyAPI.GenomaStudyAPIEndPoints endpoint, String studyName, String projectName, String[] fieldsName, Object[] fieldsValue, Boolean devMode){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
    
    InternalMessage projectOpenToChanges = GenomaDataProject.isProjectOpenToChanges2(projectName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projectOpenToChanges.getDiagnostic().toString())) return projectOpenToChanges;
   
    String classVersionProj = "0.1";
    String[] mandatoryFieldsProj = null;
    Object[] mandatoryFieldsValueProj = fieldsValue;
    String[] javaDocFieldsProj = new String[0];
    Object[] javaDocValuesProj = new Object[0];
    String javaDocLineNameProj = "";
    DataDataIntegrity labIntChecker = new DataDataIntegrity();
    if (fieldsName==null) fieldsName=new String[0];
    if (fieldsValue==null) fieldsValue=new Object[0];

    String actionName = "Insert";
    String schemaDataName = GlobalVariables.Schemas.DATA.getName();
    schemaDataName = LPPlatform.buildSchemaName(procInstanceName, schemaDataName);    
    mandatoryFieldsProj = labIntChecker.getTableMandatoryFields(TblsGenomaData.TablesGenomaData.STUDY.getTableName(), actionName);
    if (!devMode){
            InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(fieldsName, fieldsValue);
            if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic()))
                return fieldNameValueArrayChecker;
    }    
    Object[] diagnosesProj = new Object[0];
    if (!devMode){        
        StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);
        for (Integer inumLines=0;inumLines<mandatoryFieldsProj.length;inumLines++){
            String currField = mandatoryFieldsProj[inumLines];
            boolean contains = Arrays.asList(fieldsName).contains(currField.toLowerCase());
            if (!contains){
                if (mandatoryFieldsMissingBuilder.length()>0){mandatoryFieldsMissingBuilder.append(",");}
                
                mandatoryFieldsMissingBuilder.append(currField);
                
            }else{
                Integer valuePosic = Arrays.asList(fieldsName).indexOf(currField);
                if (fieldsValue!=null && fieldsValue.length>=valuePosic && mandatoryFieldsValueProj!=null && mandatoryFieldsValueProj.length>=inumLines) mandatoryFieldsValueProj[inumLines] = fieldsValue[valuePosic]; 
            }        
        }            
        if (mandatoryFieldsMissingBuilder.length()>0){
            return new InternalMessage(LPPlatform.LAB_FALSE, GenomaDataProject.GenomaDataProjectErrorTrapping.NEW_PROJECT_MISSING_MANDATORY_FIELDS, new String[]{studyName, mandatoryFieldsMissingBuilder.toString(), procInstanceName});
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
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, GenomaEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName()));
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.ACTIVE.getName())] = GenomaEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName());        
/*        fieldsName = LPArray.addValueToArray1D(fieldsName, GlobalVariables.Schemas.CONFIG.getName());    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplate);
        fieldsName = LPArray.addValueToArray1D(fieldsName, "config_version");    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplateVersion); 
*/
        
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY, fieldsName, fieldsValue);
        if (insertRecordInTable.getRunSuccess()){
            GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName, 
                studyName, projectName, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
            GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.PROJECT.getTableName(), projectName, 
                projectName, studyName, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
                return new InternalMessage(LPPlatform.LAB_TRUE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), insertRecordInTable.getNewRowId());
            }
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);            
        }    
        return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, null, null);            
}    

public Object[] studyActivate(GenomaStudyAPI.GenomaStudyAPIEndPoints endpoint, String studyName){
    String[] fieldsName=new String[]{TblsGenomaData.Study.ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{true};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.Study.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
}    

public Object[] studyDeActivate(GenomaStudyAPI.GenomaStudyAPIEndPoints endpoint, String studyName){
    Object[] projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;
    String[] fieldsName=new String[]{TblsGenomaData.Study.ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{false};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.Study.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
}   

public Object[] studyUpdate(GenomaStudyAPI.GenomaStudyAPIEndPoints endpoint, String studyName, String[] fieldsName, Object[] fieldsValue){
    Object[] projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;
    Object[] specialFieldsPresent=GenomaEnums.specialFieldsInUpdateArray(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), fieldsName);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(specialFieldsPresent[0].toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, specialFieldsPresent[specialFieldsPresent.length-1].toString(), null);
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.Study.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
} 

public Object[] studyUserManagement(GenomaStudyAPI.GenomaStudyAPIEndPoints endpoint, String studyName, String userName, String userRole){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String[] fieldsName = new String[]{TblsGenomaData.StudyUsers.STUDY.getName(), TblsGenomaData.StudyUsers.PERSON.getName(), TblsGenomaData.StudyUsers.ROLES.getName()};
    Object[] fieldsValue=new Object[]{studyName, userName, userRole};
    
    Object[] projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;
    Object[] diagnosesProj = new Object[0];
    switch (endpoint){
        //PROJECT_REMOVE_USER, PROJECT_CHANGE_USER_ROLE, 
        case STUDY_ADD_USER:
            fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyUsers.ACTIVE.getName());
            fieldsValue=LPArray.addValueToArray1D(fieldsValue, GenomaEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_USERS.getTableName()));            
            RdbmsObject insDiagnosesProj = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_USERS, fieldsName, fieldsValue);
            diagnosesProj=insDiagnosesProj.getApiMessage();
            if (insDiagnosesProj.getRunSuccess())
                GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName, 
                    studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);    
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
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, endpoint.toString()+" not implemented yet", null);
    }
    return diagnosesProj;      
} 

public Object[] studyUserActivate(GenomaStudyAPI.GenomaStudyAPIEndPoints endpoint, String studyName, String userName, String userRole){
    Object[] projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;
    String[] fieldsName=new String[]{TblsGenomaData.StudyUsers.ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{true};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudyUsers.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    sqlWhere.addConstraint(TblsGenomaData.StudyUsers.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, "");
    if (LPNulls.replaceNull(userRole).length()>0)
        sqlWhere.addConstraint(TblsGenomaData.StudyUsers.ROLES, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userRole}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_USERS,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_USERS, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_USERS.getTableName(), studyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
}    

public Object[] studyUserDeActivate(GenomaStudyAPI.GenomaStudyAPIEndPoints endpoint, String studyName, String userName, String userRole){
    Object[] projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;

    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);    
    String[] fieldsName=new String[]{TblsGenomaData.StudyUsers.ACTIVE.getName(), TblsGenomaData.StudyUsers.DEACTIVATED_BY.getName(), TblsGenomaData.StudyUsers.DEACTIVATED_ON.getName()};
    Object[] fieldsValue=new Object[]{false, instanceForActions.getToken().getPersonName(),LPDate.getCurrentTimeStamp()};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudyUsers.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    sqlWhere.addConstraint(TblsGenomaData.StudyUsers.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, "");
    sqlWhere.addConstraint(TblsGenomaData.StudyUsers.ROLES, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userRole}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_USERS,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_USERS, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_USERS.getTableName(), studyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
}    

public static Object[] isStudyOpenToChanges(String studyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY.getTableName(),
            new String[]{TblsGenomaData.Study.NAME.getName()}, new Object[]{studyName}, new String[]{TblsGenomaData.Study.ACTIVE.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The project <*1*> does not exist in procedure <*2*>", new Object[]{studyName, procInstanceName});
    if (!Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The project <*1*> is already inactive in procedure <*2*>", new Object[]{studyName, procInstanceName});
    return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "<*1*> is open to changes in procedure <*2*>", new Object[]{studyName, procInstanceName});
}
public static InternalMessage isStudyOpenToChanges2(String studyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY.getTableName(),
            new String[]{TblsGenomaData.Study.NAME.getName()}, new Object[]{studyName}, new String[]{TblsGenomaData.Study.ACTIVE.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
        return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.STUDY_NOT_FOUND, new Object[]{studyName, procInstanceName});
    if (!Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString()))
        return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.STUDY_ALREADY_INACTIVE, new Object[]{studyName, procInstanceName});
    return new InternalMessage(LPPlatform.LAB_TRUE, GenomaSuccess.STUDY_OPEN_TO_CHANGES, new Object[]{studyName, procInstanceName});
}


public Object[] studyUserChangeRole(GenomaStudyAPI.GenomaStudyAPIEndPoints endpoint, String studyName, String userName, String userRole){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String[] fieldsName=new String[]{TblsGenomaData.StudyUsers.ROLES.getName()};
    Object[] fieldsValue=new Object[]{userRole};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudyUsers.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    sqlWhere.addConstraint(TblsGenomaData.StudyUsers.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, "");
//    sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.ROLES, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userRole}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_USERS,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_USERS, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_USERS.getTableName(), studyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
}    

}
