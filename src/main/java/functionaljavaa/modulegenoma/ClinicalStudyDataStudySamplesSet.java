/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import module.clinicalstudies.apis.GenomaStudyAPI;
import module.clinicalstudies.definition.TblsGenomaData;
import static functionaljavaa.modulegenoma.ClinicalStudyUtilities.*;
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
import lbplanet.utilities.LPMath;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
/**
 *
 * @author User
 */
public class ClinicalStudyDataStudySamplesSet {
public InternalMessage createStudySamplesSet(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String sampleSetName, String[] samples, String[] fieldsName, Object[] fieldsValue, Boolean devMode){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
    
    InternalMessage studyOpenToChanges = ClinicalStudyDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyOpenToChanges.getDiagnostic())) return studyOpenToChanges;        
   
    String[] mandatoryFields = null;
    Object[] mandatoryFieldsValue = fieldsValue;
    DataDataIntegrity labIntChecker = new DataDataIntegrity();
    if (fieldsName==null) fieldsName=new String[0];
    if (fieldsValue==null) fieldsValue=new Object[0];

    String actionName = "Insert";       
    mandatoryFields = labIntChecker.getTableMandatoryFields(TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), actionName);
        
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
            if (Boolean.FALSE.equals(contains)){
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
           fieldsValue=LPArray.addValueToArray1D(fieldsValue, ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName()));
        }else
           fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudySamplesSet.ACTIVE.getName())] = ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName());        
/*        fieldsName = LPArray.addValueToArray1D(fieldsName, GlobalVariables.Schemas.CONFIG.getName());    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplate);
        fieldsName = LPArray.addValueToArray1D(fieldsName, "config_version");    
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplateVersion); 
*/
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET, fieldsName, fieldsValue);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess()))
            for (String currSample: samples)
                studySamplesSetAddSample(endpoint, studyName, sampleSetName, currSample);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())){
            ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET, sampleSetName, 
                studyName, null, fieldsName, fieldsValue);
            return new InternalMessage(LPPlatform.LAB_TRUE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), insertRecordInTable.getNewRowId());
        }
        return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
    }    
    return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, null, null);            
}    

public InternalMessage studySamplesSetActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String sampleSetName){
    String[] fieldsName=new String[]{TblsGenomaData.StudySamplesSet.ACTIVE.getName()};
    Object[] fieldsValue=new Object[]{true};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudySamplesSet.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleSetName}, "");
    RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET, fieldsName), fieldsValue, sqlWhere, null);
    if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess()))
        ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET, sampleSetName, 
            studyName, null, fieldsName, fieldsValue);
    if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }
    return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
}    

public InternalMessage studySamplesSetDeActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String sampleSetName){
    InternalMessage studyOpenToChanges = ClinicalStudyDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyOpenToChanges.getDiagnostic())) return studyOpenToChanges;        
    
    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);    
    String[] fieldsName=new String[]{TblsGenomaData.StudySamplesSet.ACTIVE.getName(), TblsGenomaData.StudySamplesSet.DEACTIVATED_BY.getName(), TblsGenomaData.StudySamplesSet.DEACTIVATED_ON.getName()};
    Object[] fieldsValue=new Object[]{false, instanceForActions.getToken().getPersonName(),LPDate.getCurrentTimeStamp()};
    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudySamplesSet.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    sqlWhere.addConstraint(TblsGenomaData.StudySamplesSet.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleSetName}, "");
    RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET, fieldsName), fieldsValue, sqlWhere, null);
    if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess()))
        ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET, sampleSetName, 
            studyName, null, fieldsName, fieldsValue);
    if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }
    return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
}   

public InternalMessage studySamplesSetUpdate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String sampleSetName, String[] fieldsName, Object[] fieldsValue){
    InternalMessage studyOpenToChanges = ClinicalStudyDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyOpenToChanges.getDiagnostic())) return studyOpenToChanges;        

    SqlWhere sqlWhere = new SqlWhere();
    sqlWhere.addConstraint(TblsGenomaData.StudySamplesSet.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
    sqlWhere.addConstraint(TblsGenomaData.StudySamplesSet.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleSetName}, "");
    RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET,
        EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET, fieldsName), fieldsValue, sqlWhere, null);
    if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess()))
        ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET, sampleSetName, 
            studyName, null, fieldsName, fieldsValue);
    if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
        return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
    }
    return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
} 

public InternalMessage studySamplesSetAddSample(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String sampleSetName, String sampleId) {
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    InternalMessage studyOpenToChanges = ClinicalStudyDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyOpenToChanges.getDiagnostic())) return studyOpenToChanges;        
    
    InternalMessage isNumeric = LPMath.isNumeric(sampleId, Boolean.FALSE);
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric.getDiagnostic()))
        return isNumeric;

    Object[] existsRecord = Rdbms.existsRecord(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE, 
            new String[]{TblsGenomaData.StudyIndividualSample.SAMPLE_ID.getName()}, 
            new Object[]{Integer.valueOf(sampleId)}, procInstanceName);
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsRecord[0].toString()))
        return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.STUDY_INDIVIDUAL_SAMPLE_NOT_FOUND, new Object[]{sampleId});
    
    InternalMessage updateSamplesSetSamples=addObjectToUnstructuredField(endpoint, TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET, 
            new String[]{TblsGenomaData.StudySamplesSet.STUDY.getName(), TblsGenomaData.StudySamplesSet.NAME.getName()}, new Object[]{studyName, sampleSetName}, 
            TblsGenomaData.StudySamplesSet.UNSTRUCT_CONTENT.getName(), sampleId, sampleId);  
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateSamplesSetSamples.getDiagnostic())) {
        return updateSamplesSetSamples;
    }
    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(updateSamplesSetSamples.getDiagnostic()))) {
        ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET, sampleSetName, 
            studyName, null, new String[]{TblsGenomaData.StudySamplesSet.UNSTRUCT_CONTENT.getName()}, 
            updateSamplesSetSamples.getMessageCodeVariables());
    }
    return updateSamplesSetSamples;
}

public InternalMessage studySamplesSetRemoveSample(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String sampleSetName, String sampleId) {
    InternalMessage studyOpenToChanges = ClinicalStudyDataStudy.isStudyOpenToChanges(studyName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyOpenToChanges.getDiagnostic())) return studyOpenToChanges;        

    InternalMessage updateSamplesSetSamples=removeObjectToUnstructuredField(endpoint, TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET, 
            new String[]{TblsGenomaData.StudySamplesSet.STUDY.getName(), TblsGenomaData.StudySamplesSet.NAME.getName()}, new Object[]{studyName, sampleSetName}, 
            TblsGenomaData.StudySamplesSet.UNSTRUCT_CONTENT.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), sampleId, sampleId);  
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateSamplesSetSamples.getDiagnostic())) {
        return updateSamplesSetSamples;
    }
    
    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(updateSamplesSetSamples.getDiagnostic()))) {
        ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET, sampleSetName, 
            studyName, null, new String[]{TblsGenomaData.StudySamplesSet.UNSTRUCT_CONTENT.getName()}, 
            updateSamplesSetSamples.getMessageCodeVariables());
    }
    return updateSamplesSetSamples;
}

public static InternalMessage isStudySamplesSetOpenToChanges(String studyName, String sampleSet){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(),
            new String[]{TblsGenomaData.StudySamplesSet.STUDY.getName(), TblsGenomaData.StudySamplesSet.NAME.getName()}, new Object[]{studyName, sampleSet}, new String[]{TblsGenomaData.StudySamplesSet.ACTIVE.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
        return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.STUDY_SAMPLES_SET_NOT_FOUND, new Object[]{studyName, procInstanceName});
    if (Boolean.FALSE.equals(Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString())))
        return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.STUDY_SAMPLES_SET_ALREADY_INACTIVE, new Object[]{studyName, procInstanceName});
    return new InternalMessage(LPPlatform.LAB_TRUE, GenomaSuccess.STUDY_SAMPLES_SET_OPEN_TO_CHANGES, new Object[]{studyName, procInstanceName});
}
    
}
