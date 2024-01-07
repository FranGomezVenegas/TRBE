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
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.LPPlatform;
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
public class GenomaDataStudyFamily {
    
public InternalMessage createStudyFamily(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String familyName, String[] individuals, String[] fieldsName, Object[] fieldsValue, Boolean devMode){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
    
    InternalMessage studyOpenToChanges = GenomaDataStudy.isStudyOpenToChanges2(studyName);    
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
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, GenomaEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName()));
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyFamily.ACTIVE.getName())] = GenomaEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName());        
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
            GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), familyName, 
                studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
            return new InternalMessage(LPPlatform.LAB_TRUE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), insertRecordInTable.getNewRowId());
        }
        return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
    }    
    return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, null, null);            
}    

public Object[] studyFamilyActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String familyName){
    String[] fieldsName=new String[]{TblsGenomaData.StudyFamily.ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{true};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudyFamily.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{familyName}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_FAMILY,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_FAMILY, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), familyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
}    

public Object[] studyFamilyDeActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String familyName){
    Object[] projStudyToChanges=GenomaDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyToChanges[0].toString())) return projStudyToChanges;
    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);    
    String[] fieldsName=new String[]{TblsGenomaData.StudyFamily.ACTIVE.getName(), TblsGenomaData.StudyFamily.DEACTIVATED_BY.getName(), TblsGenomaData.StudyFamily.DEACTIVATED_ON.getName()};
    Object[] fieldsValue=new Object[]{false, instanceForActions.getToken().getPersonName(),LPDate.getCurrentTimeStamp()};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudyFamily.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    sqlWhere.addConstraint(TblsGenomaData.StudyFamily.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{familyName}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_FAMILY,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_FAMILY, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), familyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
}   

public Object[] studyFamilyIndividualUpdate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String familyName, String[] fieldsName, Object[] fieldsValue){
    Object[] projStudyToChanges=GenomaDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyToChanges[0].toString())) return projStudyToChanges;

    Object[] specialFieldsPresent=GenomaEnums.specialFieldsInUpdateArray(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), fieldsName);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(specialFieldsPresent[0].toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, specialFieldsPresent[specialFieldsPresent.length-1].toString(), null);
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudyFamily.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    sqlWhere.addConstraint(TblsGenomaData.StudyFamily.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{familyName}, "");
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_FAMILY,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_FAMILY, fieldsName), fieldsValue, sqlWhere, null);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), familyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
} 

public Object[] studyFamilyAddIndividual(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String familyName, String individualId) {
    Object[] projStudyToChanges=GenomaDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyToChanges[0].toString())) return projStudyToChanges;
    
    Object[] isStudyFamilyOpenToChanges=isStudyFamilyOpenToChanges(studyName, familyName);
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyFamilyOpenToChanges[0].toString())) return isStudyFamilyOpenToChanges;

    Object[] projStudyIndividualToChanges=GenomaDataStudyIndividuals.isStudyIndividualOpenToChanges(studyName, Integer.valueOf(individualId));    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyIndividualToChanges[0].toString())) return projStudyIndividualToChanges;   
    
    String[] indivList=individualId.split("\\|");
    RdbmsObject curFamilyAndIndividualLinked = null;
    for (String curIndiv: indivList){
        curFamilyAndIndividualLinked = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_FAMILY_INDIVIDUAL,
                new String[]{TblsGenomaData.StudyFamilyIndividual.STUDY.getName(), TblsGenomaData.StudyFamilyIndividual.FAMILY_NAME.getName(),
                    TblsGenomaData.StudyFamilyIndividual.INDIVIDUAL_ID.getName(), TblsGenomaData.StudyFamilyIndividual.LINKED_ON.getName()}, 
                new Object[]{studyName, familyName, Integer.valueOf(curIndiv), LPDate.getCurrentTimeStamp()});
        if (Boolean.FALSE.equals(curFamilyAndIndividualLinked.getRunSuccess())) return curFamilyAndIndividualLinked.getApiMessage();
        if (Boolean.TRUE.equals(curFamilyAndIndividualLinked.getRunSuccess())){
            GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), familyName, 
                studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(new String[]{TblsGenomaData.StudyFamily.UNSTRUCT_CONTENT.getName()}, 
                        new Object[]{curFamilyAndIndividualLinked.getNewRowId()}, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        }
    }
    return curFamilyAndIndividualLinked.getApiMessage();
}

public Object[] studyFamilyRemoveIndividual(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String familyName, String individualId) {
    Object[] projStudyToChanges=GenomaDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyToChanges[0].toString())) return projStudyToChanges;
    
    Object[] isStudyFamilyOpenToChanges=isStudyFamilyOpenToChanges(studyName, familyName);
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyFamilyOpenToChanges[0].toString())) return isStudyFamilyOpenToChanges;

    Object[] projStudyIndividualToChanges=GenomaDataStudyIndividuals.isStudyIndividualOpenToChanges(studyName, Integer.valueOf(individualId));    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyIndividualToChanges[0].toString())) return projStudyIndividualToChanges;   

    SqlWhere where =new SqlWhere();
    where.addConstraint(TblsGenomaData.StudyFamilyIndividual.STUDY, null, new Object[]{studyName}, null);
    where.addConstraint(TblsGenomaData.StudyFamilyIndividual.FAMILY_NAME, null, new Object[]{familyName}, null);
    where.addConstraint(TblsGenomaData.StudyFamilyIndividual.INDIVIDUAL_ID, null, new Object[]{Integer.valueOf(individualId)}, null);
    RdbmsObject removeDiagn=Rdbms.removeRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_FAMILY_INDIVIDUAL, where, null); 
    if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
        GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), familyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(new String[]{TblsGenomaData.StudyFamily.UNSTRUCT_CONTENT.getName()}, 
                    new Object[]{removeDiagn.getErrorMessageCode()}, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    }
            if (Boolean.TRUE.equals(removeDiagn.getRunSuccess()))
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE,"", null);
            else
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
}

public static Object[] isStudyFamilyOpenToChanges(String studyName, String familyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(procInstanceName,LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(),
            new String[]{TblsGenomaData.StudyFamily.STUDY.getName(), TblsGenomaData.StudyFamily.NAME.getName()}, new Object[]{studyName, familyName}, new String[]{TblsGenomaData.StudyFamily.ACTIVE.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The study family <*1*> does not exist in procedure <*2*>", new Object[]{studyName, procInstanceName});
    if (Boolean.FALSE.equals(Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString())))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The study family <*1*> is already inactive in procedure <*2*>", new Object[]{studyName, procInstanceName});
    return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "<*1*> is open to changes in procedure <*2*>", new Object[]{studyName, procInstanceName});
}
    
}
