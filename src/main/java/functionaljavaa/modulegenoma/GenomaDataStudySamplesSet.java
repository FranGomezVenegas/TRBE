/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import com.labplanet.servicios.modulegenoma.TblsGenomaData;
import static functionaljavaa.modulegenoma.GenomaUtilities.*;
import databases.DataDataIntegrity;
import databases.Rdbms;
import databases.features.Token;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author User
 */
public class GenomaDataStudySamplesSet {
public Object[] createStudySamplesSet( String studyName, String sampleSetName, String[] samples, String[] fieldsName, Object[] fieldsValue, Boolean devMode){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
    
    Object[] projStudyToChanges=GenomaDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyToChanges[0].toString())) return projStudyToChanges;
   
    String classVersionProj = "0.1";
    String[] mandatoryFields = null;
    Object[] mandatoryFieldsValue = fieldsValue;
    String[] javaDocFields = new String[0];
    Object[] javaDocValues = new Object[0];
    String javaDocLineName = "";
    DataDataIntegrity labIntChecker = new DataDataIntegrity();
    if (fieldsName==null) fieldsName=new String[0];
    if (fieldsValue==null) fieldsValue=new Object[0];

    if (devMode){
        StackTraceElement[] elementsDev = Thread.currentThread().getStackTrace();
        javaDocLineName = "BEGIN";
        javaDocFields = LPArray.addValueToArray1D(javaDocFields, ApiMessageReturn.JAVADOC_LINE_FLDNAME);
        javaDocValues = LPArray.addValueToArray1D(javaDocValues, javaDocLineName);
        javaDocFields = LPArray.addValueToArray1D(javaDocFields, ApiMessageReturn.JAVADOC_CLASS_FLDNAME);
        javaDocValues = LPArray.addValueToArray1D(javaDocValues, classVersionProj);
        LPPlatform.addJavaClassDoc(javaDocFields, javaDocValues, elementsDev);
    }    
        String actionName = "Insert";
        
        String schemaDataName = GlobalVariables.Schemas.DATA.getName();
        
        schemaDataName = LPPlatform.buildSchemaName(procInstanceName, schemaDataName);    
        
        mandatoryFields = labIntChecker.getTableMandatoryFields(TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), actionName);
        
        
    if (devMode){
        StackTraceElement[] elementsDev = Thread.currentThread().getStackTrace();
        javaDocLineName = "CHECK sampleFieldName and sampleFieldValue match in length";
        javaDocFields = LPArray.addValueToArray1D(javaDocFields, ApiMessageReturn.JAVADOC_LINE_FLDNAME);
        javaDocValues = LPArray.addValueToArray1D(javaDocValues, javaDocLineName);
        javaDocFields = LPArray.addValueToArray1D(javaDocFields, ApiMessageReturn.JAVADOC_CLASS_FLDNAME);
        javaDocValues = LPArray.addValueToArray1D(javaDocValues, classVersionProj);
        LPPlatform.addJavaClassDoc(javaDocFields, javaDocValues, elementsDev);
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
        javaDocLineName = "CHECK sampleFieldName and sampleFieldValue match in length";
        javaDocFields = LPArray.addValueToArray1D(javaDocFields, ApiMessageReturn.JAVADOC_LINE_FLDNAME);
        javaDocValues = LPArray.addValueToArray1D(javaDocValues, javaDocLineName);
        javaDocFields = LPArray.addValueToArray1D(javaDocFields, ApiMessageReturn.JAVADOC_CLASS_FLDNAME);
        javaDocValues = LPArray.addValueToArray1D(javaDocValues, classVersionProj);
        LPPlatform.addJavaClassDoc(javaDocFields, javaDocValues, elementsDev);
    }    
    Object[] diagnosesProj = new Object[0];
    if (!devMode){        
        if (LPArray.duplicates(fieldsName)){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "Detected any field duplicated in FieldName, the values are: <*1*>", new String[]{Arrays.toString(fieldsName)});
        }

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
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, GenomaDataProject.GenomaDataProjectErrorTrapping.NEW_PROJECT_MISSING_MANDATORY_FIELDS, new String[]{studyName, mandatoryFieldsMissingBuilder.toString(), procInstanceName});
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
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudySamplesSet.NAME.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudySamplesSet.NAME.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, sampleSetName);
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudySamplesSet.NAME.getName())] = sampleSetName;
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudySamplesSet.STUDY.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudySamplesSet.STUDY.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, studyName);
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudySamplesSet.STUDY.getName())] = studyName;
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudySamplesSet.CREATED_ON.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudySamplesSet.CREATED_ON.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, LPDate.getCurrentTimeStamp());
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudySamplesSet.CREATED_ON.getName())] = LPDate.getCurrentTimeStamp();
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudySamplesSet.CREATED_BY.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudySamplesSet.CREATED_BY.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, token.getPersonName());
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudySamplesSet.CREATED_BY.getName())] = token.getPersonName();
        if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudySamplesSet.ACTIVE.getName())==-1){
           fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudySamplesSet.ACTIVE.getName());
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, GenomaBusinessRules.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName()));
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudySamplesSet.ACTIVE.getName())] = GenomaBusinessRules.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName());        
/*        fieldsName = LPArray.addValueToArray1D(fieldsName, GlobalVariables.Schemas.CONFIG.getName());    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplate);
        fieldsName = LPArray.addValueToArray1D(fieldsName, "config_version");    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplateVersion); 
*/
        
        diagnosesProj = Rdbms.insertRecordInTable(schemaDataName, TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), fieldsName, fieldsValue);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
            for (String currSample: samples)
                studySamplesSetAddSample(studyName, sampleSetName, currSample);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
            GenomaDataAudit.studyAuditAdd(GenomaDataAudit.DataGenomaStudyAuditEvents.NEW_STUDY_SAMPLES_SET.toString(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), sampleSetName, 
                studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        return diagnosesProj;  
    }    
    if (devMode){
        StackTraceElement[] elementsDev = Thread.currentThread().getStackTrace();
        javaDocLineName = "END";
        Integer specialFieldIndex = Arrays.asList(javaDocFields).indexOf(ApiMessageReturn.JAVADOC_LINE_FLDNAME);
        if (specialFieldIndex==-1){
            javaDocFields = LPArray.addValueToArray1D(javaDocFields, ApiMessageReturn.JAVADOC_LINE_FLDNAME);         javaDocValues = LPArray.addValueToArray1D(javaDocValues, javaDocLineName);         
        }else{    
            javaDocValues[specialFieldIndex] = javaDocLineName;             
        }
        LPPlatform.addJavaClassDoc(javaDocFields, javaDocValues, elementsDev);
    }
    return diagnosesProj; 
}    

public Object[] studySamplesSetActivate( String studyName, String sampleSetName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String[] fieldsName=new String[]{TblsGenomaData.StudySamplesSet.ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{true};
    
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), 
            fieldsName, fieldsValue, new String[]{TblsGenomaData.StudySamplesSet.NAME.getName()}, new Object[]{sampleSetName});
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(GenomaDataAudit.DataGenomaStudyAuditEvents.ACTIVATE_STUDY_SAMPLES_SET.toString(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), sampleSetName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
}    

public Object[] studySamplesSetDeActivate(String studyName, String sampleSetName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    Object[] projStudyToChanges=GenomaDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyToChanges[0].toString())) return projStudyToChanges;
    
    String[] fieldsName=new String[]{TblsGenomaData.StudySamplesSet.ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{false};
    
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), 
            fieldsName, fieldsValue, 
            new String[]{TblsGenomaData.StudySamplesSet.STUDY.getName(), TblsGenomaData.StudySamplesSet.NAME.getName()}, new Object[]{studyName, sampleSetName});
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(GenomaDataAudit.DataGenomaStudyAuditEvents.DEACTIVATE_STUDY_SAMPLES_SET.toString(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), sampleSetName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
}   

public Object[] studySamplesSetUpdate( String studyName, String sampleSetName, String[] fieldsName, Object[] fieldsValue){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    Object[] projStudyToChanges=GenomaDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyToChanges[0].toString())) return projStudyToChanges;

    Object[] specialFieldsPresent=GenomaBusinessRules.specialFieldsInUpdateArray(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), fieldsName);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(specialFieldsPresent[0].toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, specialFieldsPresent[specialFieldsPresent.length-1].toString(), null);
    Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), 
            fieldsName, fieldsValue, 
            new String[]{TblsGenomaData.StudySamplesSet.STUDY.getName(), TblsGenomaData.StudySamplesSet.NAME.getName()}, new Object[]{studyName, sampleSetName});
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
        GenomaDataAudit.studyAuditAdd(GenomaDataAudit.DataGenomaStudyAuditEvents.UPDATE_STUDY_SAMPLES_SET.toString(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), sampleSetName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    return diagnosesProj;      
} 

public Object[] studySamplesSetAddSample(String studyName, String sampleSetName, String sampleId) {
    Object[] isStudySamplesSetOpenToChanges=isStudySamplesSetOpenToChanges(studyName, sampleSetName);
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudySamplesSetOpenToChanges[0].toString())) return isStudySamplesSetOpenToChanges;
    
    Object[] updateSamplesSetSamples=addObjectToUnstructuredField(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), 
            new String[]{TblsGenomaData.StudySamplesSet.STUDY.getName(), TblsGenomaData.StudySamplesSet.NAME.getName()}, new Object[]{studyName, sampleSetName}, 
            TblsGenomaData.StudySamplesSet.UNSTRUCT_CONTENT.getName(), sampleId, sampleId);  
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateSamplesSetSamples[0].toString())) {
        return updateSamplesSetSamples;
    }
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateSamplesSetSamples[0].toString())) {
        GenomaDataAudit.studyAuditAdd(GenomaDataAudit.DataGenomaStudyAuditEvents.STUDY_SAMPLES_SET_ADDED_SAMPLE.toString(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), sampleSetName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(new String[]{TblsGenomaData.StudySamplesSet.UNSTRUCT_CONTENT.getName()}, new Object[]{updateSamplesSetSamples[updateSamplesSetSamples.length-1]}, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    }
    return updateSamplesSetSamples;
}

public Object[] studySamplesSetRemoveSample(String studyName, String sampleSetName, String sampleId) {
    Object[] isStudySamplesSetOpenToChanges=isStudySamplesSetOpenToChanges(studyName, sampleSetName);
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudySamplesSetOpenToChanges[0].toString())) return isStudySamplesSetOpenToChanges;

    Object[] updateSamplesSetSamples=removeObjectToUnstructuredField(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), 
            new String[]{TblsGenomaData.StudySamplesSet.STUDY.getName(), TblsGenomaData.StudySamplesSet.NAME.getName()}, new Object[]{studyName, sampleSetName}, 
            TblsGenomaData.StudySamplesSet.UNSTRUCT_CONTENT.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), sampleId, sampleId);  
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateSamplesSetSamples[0].toString())) {
        return updateSamplesSetSamples;
    }
    
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateSamplesSetSamples[0].toString())) {
        GenomaDataAudit.studyAuditAdd(GenomaDataAudit.DataGenomaStudyAuditEvents.STUDY_SAMPLES_SET_REMOVED_SAMPLE.toString(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), sampleSetName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(new String[]{TblsGenomaData.StudySamplesSet.UNSTRUCT_CONTENT.getName()}, new Object[]{updateSamplesSetSamples[updateSamplesSetSamples.length-1]}, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    }
    return updateSamplesSetSamples;
}

public static Object[] isStudySamplesSetOpenToChanges(String studyName, String familyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(),
            new String[]{TblsGenomaData.StudySamplesSet.STUDY.getName(), TblsGenomaData.StudySamplesSet.NAME.getName()}, new Object[]{studyName, familyName}, new String[]{TblsGenomaData.StudySamplesSet.ACTIVE.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The study family <*1*> does not exist in procedure <*2*>", new Object[]{studyName, procInstanceName});
    if (!Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The study family <*1*> is already inactive in procedure <*2*>", new Object[]{studyName, procInstanceName});
    return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "<*1*> is open to changes in procedure <*2*>", new Object[]{studyName, procInstanceName});
}
    
}
