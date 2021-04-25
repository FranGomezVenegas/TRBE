/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import com.labplanet.servicios.modulegenoma.TblsGenomaData;
import databases.DataDataIntegrity;
import databases.Rdbms;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import databases.Token;
import functionaljavaa.modulegenoma.GenomaDataAudit.ProjectAuditEvents;
import functionaljavaa.modulegenoma.GenomaDataAudit.StudyAuditEvents;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class GenomaDataStudy {
public Object[] createStudy(String studyName, String projectName, String[] fieldsName, Object[] fieldsValue, Boolean devMode){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
    
    Object[] projOpenToChanges=GenomaDataProject.isProjectOpenToChanges(projectName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;
   
    String classVersionProj = "0.1";
    String[] mandatoryFieldsProj = null;
    Object[] mandatoryFieldsValueProj = fieldsValue;
    String[] javaDocFieldsProj = new String[0];
    Object[] javaDocValuesProj = new Object[0];
    String javaDocLineNameProj = "";
    DataDataIntegrity labIntChecker = new DataDataIntegrity();
    if (fieldsName==null) fieldsName=new String[0];
    if (fieldsValue==null) fieldsValue=new Object[0];

    if (devMode){
        StackTraceElement[] elementsDev = Thread.currentThread().getStackTrace();
        javaDocLineNameProj = "BEGIN";
        javaDocFieldsProj = LPArray.addValueToArray1D(javaDocFieldsProj, LPPlatform.JAVADOC_LINE_FLDNAME);
        javaDocValuesProj = LPArray.addValueToArray1D(javaDocValuesProj, javaDocLineNameProj);
        javaDocFieldsProj = LPArray.addValueToArray1D(javaDocFieldsProj, LPPlatform.JAVADOC_CLASS_FLDNAME);
        javaDocValuesProj = LPArray.addValueToArray1D(javaDocValuesProj, classVersionProj);
        LPPlatform.addJavaClassDoc(javaDocFieldsProj, javaDocValuesProj, elementsDev);
    }    
        String actionName = "Insert";
        
        String schemaDataName = GlobalVariables.Schemas.DATA.getName();
        
        schemaDataName = LPPlatform.buildSchemaName(procInstanceName, schemaDataName);    
        
        mandatoryFieldsProj = labIntChecker.getTableMandatoryFields(TblsGenomaData.Study.TBL.getName(), actionName);
        
        
    if (devMode){
        StackTraceElement[] elementsDev = Thread.currentThread().getStackTrace();
        javaDocLineNameProj = "CHECK sampleFieldName and sampleFieldValue match in length";
        javaDocFieldsProj = LPArray.addValueToArray1D(javaDocFieldsProj, LPPlatform.JAVADOC_LINE_FLDNAME);
        javaDocValuesProj = LPArray.addValueToArray1D(javaDocValuesProj, javaDocLineNameProj);
        javaDocFieldsProj = LPArray.addValueToArray1D(javaDocFieldsProj, LPPlatform.JAVADOC_CLASS_FLDNAME);
        javaDocValuesProj = LPArray.addValueToArray1D(javaDocValuesProj, classVersionProj);
        LPPlatform.addJavaClassDoc(javaDocFieldsProj, javaDocValuesProj, elementsDev);
    }    
    if (!devMode){
        String[] diagnosesProj = LPArray.checkTwoArraysSameLength(fieldsName, fieldsValue);
        if (fieldsName.length!=fieldsValue.length){
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            diagnosesProj[0]= elements[1].getClassName() + "." + elements[1].getMethodName();
            diagnosesProj[1]= classVersionProj;
            diagnosesProj[2]= "Code Line " + (elements[1].getLineNumber());   
            diagnosesProj[3]=LPPlatform.LAB_FALSE;
            diagnosesProj[4]="ERROR:Field names and values arrays with different length";
            diagnosesProj[5]="The values in FieldName are:"+ Arrays.toString(fieldsName)+". and in FieldValue are:"+Arrays.toString(fieldsValue);
            return diagnosesProj;
        }
    }    
    if (devMode){
        StackTraceElement[] elementsDev = Thread.currentThread().getStackTrace();
        javaDocLineNameProj = "CHECK sampleFieldName and sampleFieldValue match in length";
        javaDocFieldsProj = LPArray.addValueToArray1D(javaDocFieldsProj, LPPlatform.JAVADOC_LINE_FLDNAME);
        javaDocValuesProj = LPArray.addValueToArray1D(javaDocValuesProj, javaDocLineNameProj);
        javaDocFieldsProj = LPArray.addValueToArray1D(javaDocFieldsProj, LPPlatform.JAVADOC_CLASS_FLDNAME);
        javaDocValuesProj = LPArray.addValueToArray1D(javaDocValuesProj, classVersionProj);
        LPPlatform.addJavaClassDoc(javaDocFieldsProj, javaDocValuesProj, elementsDev);
    }    
    Object[] diagnosesProj = new Object[0];
    if (!devMode){        
        if (LPArray.duplicates(fieldsName)){
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Detected any field duplicated in FieldName, the values are: <*1*>", new String[]{Arrays.toString(fieldsName)});
        }

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
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, GenomaDataProject.GenomaDataProjectErrorTrapping.NEW_PROJECT_MISSING_MANDATORY_FIELDS.getErrorCode(), new String[]{studyName, mandatoryFieldsMissingBuilder.toString(), procInstanceName});
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
                        String errorCode = "LabPLANETPlatform_SpecialFunctionReturnedEXCEPTION";
                        Object[] errorDetailVariables = new Object[0];
                        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, ex.getMessage());
                        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, errorCode, errorDetailVariables);                        
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
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.FLD_PROJECT.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Study.FLD_PROJECT.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, projectName);
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.FLD_PROJECT.getName())] = projectName;
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.FLD_NAME.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Study.FLD_NAME.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, studyName);
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.FLD_NAME.getName())] = studyName;
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.FLD_CREATED_ON.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Study.FLD_CREATED_ON.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, LPDate.getCurrentTimeStamp());
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.FLD_CREATED_ON.getName())] = LPDate.getCurrentTimeStamp();
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.FLD_CREATED_BY.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Study.FLD_CREATED_BY.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, token.getPersonName());
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.FLD_CREATED_BY.getName())] = token.getPersonName();
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.FLD_ACTIVE.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.Study.FLD_ACTIVE.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, GenomaBusinessRules.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.Study.TBL.getName()));
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.Study.FLD_ACTIVE.getName())] = GenomaBusinessRules.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.Study.TBL.getName());        
/*        fieldsName = LPArray.addValueToArray1D(fieldsName, GlobalVariables.Schemas.CONFIG.getName());    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplate);
        fieldsName = LPArray.addValueToArray1D(fieldsName, "config_version");    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplateVersion); 
*/
        
        diagnosesProj = Rdbms.insertRecordInTable(schemaDataName, TblsGenomaData.Study.TBL.getName(), fieldsName, fieldsValue);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString())){
            GenomaDataAudit.studyAuditAdd(StudyAuditEvents.NEW_STUDY.toString(), TblsGenomaData.Study.TBL.getName(), studyName, 
                studyName, projectName, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, ":"), null);
            GenomaDataAudit.projectAuditAdd(ProjectAuditEvents.STUDY_ADDED.toString(), TblsGenomaData.Project.TBL.getName(), projectName, 
                projectName, studyName, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, ":"), null);
        }
        return diagnosesProj;  
    }    
    if (devMode){
        StackTraceElement[] elementsDev = Thread.currentThread().getStackTrace();
        javaDocLineNameProj = "END";
        Integer specialFieldIndex = Arrays.asList(javaDocFieldsProj).indexOf(LPPlatform.JAVADOC_LINE_FLDNAME);
        if (specialFieldIndex==-1){
            javaDocFieldsProj = LPArray.addValueToArray1D(javaDocFieldsProj, LPPlatform.JAVADOC_LINE_FLDNAME);         javaDocValuesProj = LPArray.addValueToArray1D(javaDocValuesProj, javaDocLineNameProj);         
        }else{    
            javaDocValuesProj[specialFieldIndex] = javaDocLineNameProj;             
        }
        LPPlatform.addJavaClassDoc(javaDocFieldsProj, javaDocValuesProj, elementsDev);
    }
    return diagnosesProj; 
}    

public Object[] studyActivate(String studyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String[] fieldsName=new String[]{TblsGenomaData.Study.FLD_ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{true};
    
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.Study.TBL.getName(), 
            fieldsName, fieldsValue, new String[]{TblsGenomaData.Study.FLD_NAME.getName()}, new Object[]{studyName});
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(StudyAuditEvents.ACTIVATE_STUDY.toString(), TblsGenomaData.Study.TBL.getName(), studyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, ":"), null);
    return diagnosesProj;      
}    

public Object[] studyDeActivate(String studyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    Object[] projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;
    
    String[] fieldsName=new String[]{TblsGenomaData.Study.FLD_ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{false};
    
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.Study.TBL.getName(), 
            fieldsName, fieldsValue, new String[]{TblsGenomaData.Study.FLD_NAME.getName()}, new Object[]{studyName});
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(StudyAuditEvents.DEACTIVATE_STUDY.toString(), TblsGenomaData.Study.TBL.getName(), studyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, ":"), null);
    return diagnosesProj;      
}   

public Object[] studyUpdate(String studyName, String[] fieldsName, Object[] fieldsValue){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    Object[] projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;

    Object[] specialFieldsPresent=GenomaBusinessRules.specialFieldsInUpdateArray(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.Study.TBL.getName(), fieldsName);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(specialFieldsPresent[0].toString()))
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, specialFieldsPresent[specialFieldsPresent.length-1].toString(), null);
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.Study.TBL.getName(), 
            fieldsName, fieldsValue, new String[]{TblsGenomaData.Study.FLD_NAME.getName()}, new Object[]{studyName});
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(StudyAuditEvents.ACTIVATE_STUDY.toString(), TblsGenomaData.Study.TBL.getName(), studyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, ":"), null);
    return diagnosesProj;      
} 

public Object[] studyUserManagement(String actionName, String studyName, String userName, String userRole){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String[] fieldsName = new String[]{TblsGenomaData.StudyUsers.FLD_STUDY.getName(), TblsGenomaData.StudyUsers.FLD_PERSON.getName(), TblsGenomaData.StudyUsers.FLD_ROLES.getName()};
    Object[] fieldsValue=new Object[]{studyName, userName, userRole};
    
    Object[] projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;
    Object[] diagnosesProj = new Object[0];
    switch (actionName){
        //PROJECT_REMOVE_USER, PROJECT_CHANGE_USER_ROLE, 
        case "STUDY_ADD_USER":
            fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyUsers.FLD_ACTIVE.getName());
            fieldsValue=LPArray.addValueToArray1D(fieldsValue, GenomaBusinessRules.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.StudyUsers.TBL.getName()));            
            diagnosesProj = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.StudyUsers.TBL.getName(), 
                fieldsName, fieldsValue);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
                GenomaDataAudit.studyAuditAdd(actionName, TblsGenomaData.Study.TBL.getName(), studyName, 
                    studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, ":"), null);    
            break;
        case "STUDY_USER_ACTIVATE":
            diagnosesProj = studyUserActivate(studyName, userName, userRole);
            break;
        case "STUDY_USER_DEACTIVATE":
            diagnosesProj = studyUserDeActivate(studyName, userName, userRole);
            break;
        default:
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, actionName+" not implemented yet", null);
    }
    return diagnosesProj;      
} 

public Object[] studyUserActivate(String studyName, String userName, String userRole){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    Object[] projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;

    String[] fieldsName=new String[]{TblsGenomaData.StudyUsers.FLD_ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{true};
    
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.StudyUsers.TBL.getName(), 
            fieldsName, fieldsValue, new String[]{TblsGenomaData.StudyUsers.FLD_STUDY.getName(), TblsGenomaData.StudyUsers.FLD_PERSON.getName(), TblsGenomaData.StudyUsers.FLD_ROLES.getName()}, new Object[]{studyName, userName, userRole});
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(StudyAuditEvents.STUDY_USER_ACTIVATE.toString(), TblsGenomaData.StudyUsers.TBL.getName(), studyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, ":"), null);
    return diagnosesProj;      
}    

public Object[] studyUserDeActivate(String studyName, String userName, String userRole){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    Object[] projOpenToChanges=isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges[0].toString())) return projOpenToChanges;

    String[] fieldsName=new String[]{TblsGenomaData.StudyUsers.FLD_ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{false};
    
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.StudyUsers.TBL.getName(), 
            fieldsName, fieldsValue, new String[]{TblsGenomaData.StudyUsers.FLD_STUDY.getName(), TblsGenomaData.StudyUsers.FLD_PERSON.getName(), TblsGenomaData.StudyUsers.FLD_ROLES.getName()}, new Object[]{studyName, userName, userRole});
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(StudyAuditEvents.STUDY_USER_DEACTIVATE.toString(), TblsGenomaData.StudyUsers.TBL.getName(), studyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, ":"), null);
    return diagnosesProj;      
}    

public static Object[] isStudyOpenToChanges(String studyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.Study.TBL.getName(),
            new String[]{TblsGenomaData.Study.FLD_NAME.getName()}, new Object[]{studyName}, new String[]{TblsGenomaData.Study.FLD_ACTIVE.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The project <*1*> does not exist in procedure <*2*>", new Object[]{studyName, procInstanceName});
    if (!Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString()))
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The project <*1*> is already inactive in procedure <*2*>", new Object[]{studyName, procInstanceName});
    return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "<*1*> is open to changes in procedure <*2*>", new Object[]{studyName, procInstanceName});
}

}
