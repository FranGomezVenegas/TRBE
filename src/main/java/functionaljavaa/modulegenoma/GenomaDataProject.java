/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import com.labplanet.servicios.modulegenoma.GenomaProjectAPI;
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
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
/**
 *
 * @author User
 */
public class GenomaDataProject {
    
    public enum GenomaDataProjectErrorTrapping implements EnumIntMessages{ 
        PROJECT_ALREADY_EXISTS("ProjectAlreadyExists", "", ""),
        MISSING_MANDATORY_FIELDS("MissingMandatoryFields", "", ""),
        NEW_PROJECT_MISSING_MANDATORY_FIELDS("NewProjectMissingMandatoryFields", "", ""),
        ;
        private GenomaDataProjectErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    
public InternalMessage createProject(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName, String[] fieldsName, Object[] fieldsValue, Boolean devMode){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

    String classVersionProj = "0.1";
    String[] mandatoryFieldsProj = null;
    Object[] mandatoryFieldsValueProj = fieldsValue;
    String[] javaDocFieldsProj = new String[0];
    Object[] javaDocValuesProj = new Object[0];
    String javaDocLineNameProj = "";
    DataDataIntegrity labIntChecker = new DataDataIntegrity();
    if (fieldsName==null) fieldsName=new String[0];
    if (fieldsValue==null) fieldsValue=new Object[0];

    String tableName = "project";
    String actionName = "Insert";
    String schemaDataName = GlobalVariables.Schemas.DATA.getName();
    schemaDataName = LPPlatform.buildSchemaName(procInstanceName, schemaDataName);    
    mandatoryFieldsProj = labIntChecker.getTableMandatoryFields(tableName, actionName);
        
    if (Boolean.FALSE.equals(devMode)){
        InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(fieldsName, fieldsValue);
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic()))
            return fieldNameValueArrayChecker;
    }    
    Object[] diagnosesProj = new Object[0];
    if (Boolean.FALSE.equals(devMode)){        
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
            return new InternalMessage(LPPlatform.LAB_FALSE, GenomaDataProjectErrorTrapping.NEW_PROJECT_MISSING_MANDATORY_FIELDS, new String[]{projectName, mandatoryFieldsMissingBuilder.toString(), procInstanceName});
        }        
        Object[] diagnosis = Rdbms.existsRecord(
                LPPlatform.buildSchemaName(procInstanceName, TblsGenomaData.TablesGenomaData.PROJECT.getRepositoryName())                
                , TblsGenomaData.TablesGenomaData.PROJECT.getTableName(), 
            new String[]{TblsGenomaData.Project.NAME.getName()}, new Object[]{projectName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())){	
            return new InternalMessage(LPPlatform.LAB_FALSE, GenomaDataProjectErrorTrapping.PROJECT_ALREADY_EXISTS, new Object[]{projectName, procInstanceName});
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
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.NAME.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Project.NAME.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, projectName);
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.NAME.getName())] = projectName;
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.CREATED_ON.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Project.CREATED_ON.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, LPDate.getCurrentTimeStamp());
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.CREATED_ON.getName())] = LPDate.getCurrentTimeStamp();
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.CREATED_BY.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Project.CREATED_BY.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, token.getPersonName());
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.CREATED_BY.getName())] = token.getPersonName();
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.ACTIVE.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Project.ACTIVE.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, GenomaEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.PROJECT.getTableName()));
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Project.ACTIVE.getName())] = GenomaEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.PROJECT.getTableName());        
/*        fieldsName = LPArray.addValueToArray1D(fieldsName, GlobalVariables.Schemas.CONFIG.getName());    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplate);
        fieldsName = LPArray.addValueToArray1D(fieldsName, "config_version");    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplateVersion); 
*/
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.PROJECT, fieldsName, fieldsValue);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())){
            GenomaDataAudit.projectAuditAdd(endpoint, tableName, projectName, 
                projectName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
            return new InternalMessage(LPPlatform.LAB_TRUE, GenomaSuccess.PROJECT_CREATED, insertRecordInTable.getErrorMessageVariables(), insertRecordInTable.getNewRowId());
        }
        return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);            
    }    
    return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, null, null);            
}    

public Object[] projectActivate(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    Object[] projOpenToChanges=isProjectOpenToChanges(projectName);    
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;

    String[] fieldsName=new String[]{TblsGenomaData.Project.ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{true};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.Project.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{projectName}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.PROJECT,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.PROJECT, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.projectAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.PROJECT.getTableName(), projectName, 
            projectName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
}    

public Object[] projectDeActivate(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    
    Object[] projOpenToChanges=isProjectOpenToChanges(projectName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;
    
    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);    
    String[] fieldsName=new String[]{TblsGenomaData.Project.ACTIVE.getName(), TblsGenomaData.Project.DEACTIVATED_BY.getName(), TblsGenomaData.Project.DEACTIVATED_ON.getName()};
    Object[] fieldsValue=new Object[]{false, instanceForActions.getToken().getPersonName(),LPDate.getCurrentTimeStamp()};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.Project.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{projectName}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.PROJECT,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.PROJECT, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.projectAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.PROJECT.getTableName(), projectName, 
            projectName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
}   

public Object[] projectUpdate(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName, String[] fieldsName, Object[] fieldsValue){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    Object[] projOpenToChanges=isProjectOpenToChanges(projectName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;

    Object[] specialFieldsPresent=GenomaEnums.specialFieldsInUpdateArray(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.PROJECT.getTableName(), fieldsName);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(specialFieldsPresent[0].toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, specialFieldsPresent[specialFieldsPresent.length-1].toString(), null);
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.Project.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{projectName}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.PROJECT,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.PROJECT, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.projectAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.PROJECT.getTableName(), projectName, 
            projectName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
} 

public Object[] projectUserManagement(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName, String userName, String userRole){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    String[] fieldsName = new String[]{TblsGenomaData.ProjectUsers.PROJECT.getName(), TblsGenomaData.ProjectUsers.PERSON.getName(), TblsGenomaData.ProjectUsers.ROLES.getName()};
    Object[] fieldsValue=new Object[]{projectName, userName, userRole};
    
    Object[] projOpenToChanges=isProjectOpenToChanges(projectName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;
    Object[] diagnosesProj = null;
    switch (endpoint){
        case PROJECT_ADD_USER:
            fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.ProjectUsers.ACTIVE.getName());
            fieldsValue=LPArray.addValueToArray1D(fieldsValue, GenomaEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.PROJECT_USERS.getTableName()));            
            RdbmsObject insertRecordInTable=Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.PROJECT_USERS, fieldsName, fieldsValue);
            diagnosesProj=insertRecordInTable.getApiMessage();
            if (!insertRecordInTable.getRunSuccess()) return insertRecordInTable.getApiMessage();
                GenomaDataAudit.projectAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.PROJECT.getTableName(), projectName, 
                    projectName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);    
            break;
            
        case PROJECT_REMOVE_USER:
            fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.ProjectUsers.ACTIVE.getName());
            fieldsValue=LPArray.addValueToArray1D(fieldsValue, GenomaEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.PROJECT_USERS.getTableName()));            
            SqlWhere sqlWhere=new SqlWhere();
            sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PROJECT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{projectName}, null);
            sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, null);
            RdbmsObject removeRecordInTable=Rdbms.removeRecordInTable(TblsGenomaData.TablesGenomaData.PROJECT_USERS, 
                sqlWhere, null);
            diagnosesProj=removeRecordInTable.getApiMessage();
            if (!removeRecordInTable.getRunSuccess()) return removeRecordInTable.getApiMessage();
                GenomaDataAudit.projectAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.PROJECT.getTableName(), projectName, 
                    projectName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);    
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
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, endpoint.toString()+" not implemented yet", null);
    }
    return diagnosesProj;      
} 
public Object[] projectUserActivate(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName, String userName, String userRole){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String[] fieldsName=new String[]{TblsGenomaData.ProjectUsers.ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{true};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PROJECT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{projectName}, "");
    sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, "");
    if (LPNulls.replaceNull(userRole).length()>0)
        sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.ROLES, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userRole}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.PROJECT_USERS,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.PROJECT_USERS, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.projectAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.PROJECT_USERS.getTableName(), projectName, 
            projectName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
}    

public Object[] projectUserDeActivate(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName, String userName, String userRole){
    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
    String procInstanceName=instanceForActions.getProcedureInstance();
    Object[] projOpenToChanges=isProjectOpenToChanges(projectName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;
    String[] fieldsName=new String[]{TblsGenomaData.ProjectUsers.ACTIVE.getName(), TblsGenomaData.ProjectUsers.DEACTIVATED_BY.getName(), TblsGenomaData.ProjectUsers.DEACTIVATED_ON.getName()};
    Object[] fieldsValue=new Object[]{false, instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PROJECT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{projectName}, "");
    sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, "");
    sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.ROLES, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userRole}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.PROJECT_USERS,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.PROJECT_USERS, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.projectAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.PROJECT_USERS.getTableName(), projectName, 
            projectName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
}    

public static Object[] isProjectOpenToChanges(String projectName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
    Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.PROJECT.getTableName(),
        new String[]{TblsGenomaData.Project.NAME.getName()}, new Object[]{projectName}, new String[]{TblsGenomaData.Project.ACTIVE.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The project <*1*> does not exist in procedure <*2*>", new Object[]{projectName, procInstanceName});
    if (!Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The project <*1*> is already inactive in procedure <*2*>", new Object[]{projectName, procInstanceName});
    return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "<*1*> is open to changes in procedure <*2*>", new Object[]{projectName, procInstanceName});
}

public static InternalMessage isProjectOpenToChanges2(String projectName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
    Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.PROJECT.getTableName(),
        new String[]{TblsGenomaData.Project.NAME.getName()}, new Object[]{projectName}, new String[]{TblsGenomaData.Project.ACTIVE.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
        return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.PROJECT_NOT_FOUND, new Object[]{projectName, procInstanceName});
    if (!Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString()))
        return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.PROJECT_ALREADY_INACTIVE, new Object[]{projectName, procInstanceName});
    return new InternalMessage(LPPlatform.LAB_TRUE, GenomaSuccess.PROJECT_OPEN_TO_CHANGES, new Object[]{projectName, procInstanceName});
}


public Object[] projectUserChangeRole(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints endpoint, String projectName, String userName, String userRole){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String[] fieldsName=new String[]{TblsGenomaData.ProjectUsers.ROLES.getName()};
    Object[] fieldsValue=new Object[]{userRole};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PROJECT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{projectName}, "");
    sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, "");
//    sqlWhere.addConstraint(TblsGenomaData.ProjectUsers.ROLES, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userRole}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.PROJECT_USERS,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.PROJECT_USERS, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.projectAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.PROJECT_USERS.getTableName(), projectName, 
            projectName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
}    

}
