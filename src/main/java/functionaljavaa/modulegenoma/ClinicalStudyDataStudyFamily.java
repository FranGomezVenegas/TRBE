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
import databases.features.Token;
import functionaljavaa.modulegenoma.ClinicalStudyEnums.GenomaErrorTrapping;
import functionaljavaa.modulegenoma.ClinicalStudyEnums.GenomaSuccess;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import module.clinicalstudies.apis.GenomaStudyAPI.GenomaStudyAPIactionsEndPoints;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
/**
 *
 * @author User
 */
public class ClinicalStudyDataStudyFamily {
    
public InternalMessage createStudyFamily(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String familyName, String[] individuals, String[] fieldsName, Object[] fieldsValue, Boolean devMode){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
    
    InternalMessage studyOpenToChanges = ClinicalStudyDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyOpenToChanges.getDiagnostic())) return studyOpenToChanges;        
   
    String classVersionProj = "0.1";
    String[] mandatoryFields = null;
    Object[] mandatoryFieldsValue = fieldsValue;
    String[] javaDocFields = new String[0];
    Object[] javaDocValues = new Object[0];
    String javaDocLineName = "";
    DataDataIntegrity labIntChecker = new DataDataIntegrity();
    if (fieldsName==null) fieldsName=new String[0];
    if (fieldsValue==null) fieldsValue=new Object[0];

    String actionName = "Insert";
    String schemaDataName = GlobalVariables.Schemas.DATA.getName();
    schemaDataName = LPPlatform.buildSchemaName(procInstanceName, schemaDataName);    
    mandatoryFields = labIntChecker.getTableMandatoryFields(TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), actionName);
        
    if (Boolean.TRUE.equals(devMode)){
        StackTraceElement[] elementsDev = Thread.currentThread().getStackTrace();
        javaDocLineName = "CHECK sampleFieldName and sampleFieldValue match in length";
        javaDocFields = LPArray.addValueToArray1D(javaDocFields, ApiMessageReturn.JAVADOC_LINE_FLDNAME);
        javaDocValues = LPArray.addValueToArray1D(javaDocValues, javaDocLineName);
        javaDocFields = LPArray.addValueToArray1D(javaDocFields, ApiMessageReturn.JAVADOC_CLASS_FLDNAME);
        javaDocValues = LPArray.addValueToArray1D(javaDocValues, classVersionProj);
        LPPlatform.addJavaClassDoc(javaDocFields, javaDocValues, elementsDev);
    }    
    if (Boolean.FALSE.equals(devMode)){
        InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(fieldsName, fieldsValue);
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic())))
            return fieldNameValueArrayChecker;
    }    
    if (Boolean.FALSE.equals(devMode)){        
        StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);
        for (Integer inumLines=0;inumLines<mandatoryFields.length;inumLines++){
            String currField = mandatoryFields[inumLines];
            boolean contains = Arrays.asList(fieldsName).contains(currField.toLowerCase());
            if (!contains){
                if (mandatoryFieldsMissingBuilder.length()>0){mandatoryFieldsMissingBuilder.append(",");}
                
                mandatoryFieldsMissingBuilder.append(currField);
                
            }else{
                Integer valuePosic = Arrays.asList(fieldsName).indexOf(currField);
                if (fieldsValue!=null && fieldsValue.length>=valuePosic && mandatoryFieldsValue!=null && mandatoryFieldsValue.length>=inumLines) mandatoryFieldsValue[inumLines] = fieldsValue[valuePosic]; 
            }        
        }            
        if (mandatoryFieldsMissingBuilder.length()>0){
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
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyFamily.NAME.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyFamily.NAME.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, familyName);
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyFamily.NAME.getName())] = familyName;
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyFamily.STUDY.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyFamily.STUDY.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, studyName);
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyFamily.STUDY.getName())] = studyName;
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyFamily.CREATED_ON.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyFamily.CREATED_ON.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, LPDate.getCurrentTimeStamp());
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyFamily.CREATED_ON.getName())] = LPDate.getCurrentTimeStamp();
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyFamily.CREATED_BY.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyFamily.CREATED_BY.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, token.getPersonName());
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyFamily.CREATED_BY.getName())] = token.getPersonName();
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyFamily.ACTIVE.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyFamily.ACTIVE.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName()));
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyFamily.ACTIVE.getName())] = ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName());        
/*        fieldsName = LPArray.addValueToArray1D(fieldsName, GlobalVariables.Schemas.CONFIG.getName());    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplate);
        fieldsName = LPArray.addValueToArray1D(fieldsName, "config_version");    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplateVersion); 
*/
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_FAMILY, fieldsName, fieldsValue);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess()))
            for (String currIndiv: individuals)
                studyFamilyAddIndividual(endpoint, studyName, familyName, currIndiv);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())){
            ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY, familyName, 
                studyName, null, fieldsName, fieldsValue);
            return new InternalMessage(LPPlatform.LAB_TRUE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), insertRecordInTable.getNewRowId());
        }
        return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
    }    
    return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, null, null);            
}    

public InternalMessage studyFamilyActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String familyName){
    String[] fieldsName=new String[]{TblsGenomaData.StudyFamily.ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{true};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudyFamily.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{familyName}, "");
    RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_FAMILY,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_FAMILY, fieldsName), fieldsValue, sqlWhere, null);
    if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
        ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY, familyName, 
            studyName, null, fieldsName, fieldsValue);
    }
    if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }
    return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});    
}    

public InternalMessage studyFamilyDeActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String familyName){
    InternalMessage studyOpenToChanges = ClinicalStudyDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyOpenToChanges.getDiagnostic())) return studyOpenToChanges;        
    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);    
    String[] fieldsName=new String[]{TblsGenomaData.StudyFamily.ACTIVE.getName(), TblsGenomaData.StudyFamily.DEACTIVATED_BY.getName(), TblsGenomaData.StudyFamily.DEACTIVATED_ON.getName()};
    Object[] fieldsValue=new Object[]{false, instanceForActions.getToken().getPersonName(),LPDate.getCurrentTimeStamp()};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudyFamily.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    sqlWhere.addConstraint(TblsGenomaData.StudyFamily.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{familyName}, "");
    RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_FAMILY,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_FAMILY, fieldsName), fieldsValue, sqlWhere, null);
    if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
        ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY, familyName, 
            studyName, null, fieldsName, fieldsValue);
    }
    if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }
    return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});    
}   

public InternalMessage studyFamilyIndividualUpdate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String familyName, String[] fieldsName, Object[] fieldsValue){
    InternalMessage studyOpenToChanges = ClinicalStudyDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyOpenToChanges.getDiagnostic())) return studyOpenToChanges;        

    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudyFamily.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    sqlWhere.addConstraint(TblsGenomaData.StudyFamily.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{familyName}, "");
    RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_FAMILY,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_FAMILY, fieldsName), fieldsValue, sqlWhere, null);
    if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
        ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY, familyName, 
            studyName, null, fieldsName, fieldsValue);
    }
    if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }
    return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
    
} 

public InternalMessage studyFamilyAddIndividual(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String familyName, String individualIds) {
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    InternalMessage studyOpenToChanges = ClinicalStudyDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyOpenToChanges.getDiagnostic())) return studyOpenToChanges;        
    
    InternalMessage isStudyFamilyOpenToChanges = isStudyFamilyOpenToChanges(studyName, familyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyFamilyOpenToChanges.getDiagnostic())) return isStudyFamilyOpenToChanges;        

    InternalMessage projStudyIndividualToChanges=ClinicalStudyDataStudyIndividuals.isStudyIndividualOpenToChanges(studyName, Integer.valueOf(individualIds));    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyIndividualToChanges.getDiagnostic())) return projStudyIndividualToChanges;        
    
    String[] indivList=individualIds.split("\\|");
    
    InternalMessage isNumeric = LPArray.areAllNumericValues(individualIds);
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric.getDiagnostic()))
        return isNumeric;
    for (String curIndiv: indivList){
        Object[] existsRecord = Rdbms.existsRecord(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, 
                new String[]{TblsGenomaData.StudyIndividualSample.SAMPLE_ID.getName()}, 
                new Object[]{Integer.valueOf(curIndiv)}, procInstanceName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsRecord[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.STUDY_INDIVIDUAL_NOT_FOUND, new Object[]{curIndiv});
    }
        
    RdbmsObject curFamilyAndIndividualLinked = null;
    for (String curIndiv: indivList){
        curFamilyAndIndividualLinked = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_FAMILY_INDIVIDUAL,
                new String[]{TblsGenomaData.StudyFamilyIndividual.STUDY.getName(), TblsGenomaData.StudyFamilyIndividual.FAMILY_NAME.getName(),
                    TblsGenomaData.StudyFamilyIndividual.INDIVIDUAL_ID.getName(), TblsGenomaData.StudyFamilyIndividual.LINKED_ON.getName()}, 
                new Object[]{studyName, familyName, Integer.valueOf(curIndiv), LPDate.getCurrentTimeStamp()});
        if (Boolean.FALSE.equals(curFamilyAndIndividualLinked.getRunSuccess())) 
            return new InternalMessage(LPPlatform.LAB_FALSE, curFamilyAndIndividualLinked.getErrorMessageCode(), curFamilyAndIndividualLinked.getErrorMessageVariables());
        if (Boolean.TRUE.equals(curFamilyAndIndividualLinked.getRunSuccess())){
            ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY, familyName, 
                studyName, null, new String[]{TblsGenomaData.StudyFamily.UNSTRUCT_CONTENT.getName()}, 
                        new Object[]{curFamilyAndIndividualLinked.getNewRowId()});
        }
    }
        if (Boolean.FALSE.equals(curFamilyAndIndividualLinked.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, curFamilyAndIndividualLinked.getErrorMessageCode(), curFamilyAndIndividualLinked.getErrorMessageVariables());
        }else{
            return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName, familyName, individualIds});
        }       
    
}

public InternalMessage studyFamilyRemoveIndividual(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String familyName, String individualId) {
    InternalMessage studyOpenToChanges = ClinicalStudyDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyOpenToChanges.getDiagnostic())) return studyOpenToChanges;        
    
    InternalMessage isStudyFamilyOpenToChanges = isStudyFamilyOpenToChanges(studyName, familyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyFamilyOpenToChanges.getDiagnostic())) return isStudyFamilyOpenToChanges;        

    InternalMessage projStudyIndividualToChanges=ClinicalStudyDataStudyIndividuals.isStudyIndividualOpenToChanges(studyName, Integer.valueOf(individualId));    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyIndividualToChanges.getDiagnostic())) return projStudyIndividualToChanges;        

    SqlWhere where =new SqlWhere();
    where.addConstraint(TblsGenomaData.StudyFamilyIndividual.STUDY, null, new Object[]{studyName}, null);
    where.addConstraint(TblsGenomaData.StudyFamilyIndividual.FAMILY_NAME, null, new Object[]{familyName}, null);
    where.addConstraint(TblsGenomaData.StudyFamilyIndividual.INDIVIDUAL_ID, null, new Object[]{Integer.valueOf(individualId)}, null);
    RdbmsObject removeDiagn=Rdbms.removeRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_FAMILY_INDIVIDUAL, where, null); 
    if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
        ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY, familyName, 
            studyName, null, new String[]{TblsGenomaData.StudyFamily.UNSTRUCT_CONTENT.getName()}, 
                    new Object[]{removeDiagn.getErrorMessageCode()});
    }
            if (Boolean.TRUE.equals(removeDiagn.getRunSuccess()))
                return new InternalMessage(LPPlatform.LAB_TRUE, GenomaStudyAPIactionsEndPoints.STUDY_FAMILY_REMOVE_INDIVIDUAL, null);
            else
                return new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
}

public static InternalMessage isStudyFamilyOpenToChanges(String studyName, String familyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(procInstanceName,LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(),
            new String[]{TblsGenomaData.StudyFamily.STUDY.getName(), TblsGenomaData.StudyFamily.NAME.getName()}, new Object[]{studyName, familyName}, new String[]{TblsGenomaData.StudyFamily.ACTIVE.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
        return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.STUDY_FAMILY_NOT_FOUND, new Object[]{studyName, procInstanceName});
    if (Boolean.FALSE.equals(Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString())))
        return new InternalMessage(LPPlatform.LAB_FALSE,GenomaErrorTrapping.STUDY_FAMILY_ALREADY_INACTIVE, new Object[]{studyName, procInstanceName});
    return new InternalMessage(LPPlatform.LAB_TRUE, GenomaSuccess.STUDY_FAMILY_OPEN_TO_CHANGES, new Object[]{studyName, procInstanceName});
}
    
}
