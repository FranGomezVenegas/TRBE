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
import static functionaljavaa.modulegenoma.ClinicalStudyDataStudy.isStudyOpenToChanges;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.LPPlatform;
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
public class ClinicalStudyDataStudyIndividualSamples {

    public InternalMessage createStudyIndividualSample(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, Integer indivId, String[] fieldsName, Object[] fieldsValue, Boolean devMode){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        InternalMessage projOpenToChanges=isStudyOpenToChanges(studyName);    
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
            messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
            return projOpenToChanges;
        }

        String[] mandatoryFields = null;
        Object[] mandatoryFieldsValue = fieldsValue;
        DataDataIntegrity labIntChecker = new DataDataIntegrity();
        if (fieldsName==null) fieldsName=new String[0];
        if (fieldsValue==null) fieldsValue=new Object[0];

        String actionName = "Insert";
        mandatoryFields = labIntChecker.getTableMandatoryFields(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), actionName);
        if (Boolean.FALSE.equals(devMode)){
            InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(fieldsName, fieldsValue);
            if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic()))){
                messages.addMainForError(fieldNameValueArrayChecker.getMessageCodeObj(), fieldNameValueArrayChecker.getMessageCodeVariables());
                return fieldNameValueArrayChecker;
            }
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
                messages.addMainForError(ClinicalStudyEnums.GenomaErrorTrapping.NEW_PROJECT_MISSING_MANDATORY_FIELDS, new String[]{studyName, mandatoryFieldsMissingBuilder.toString()});
                return new InternalMessage(LPPlatform.LAB_FALSE, ClinicalStudyEnums.GenomaErrorTrapping.NEW_PROJECT_MISSING_MANDATORY_FIELDS, new String[]{studyName, mandatoryFieldsMissingBuilder.toString(), instanceForActions.getProcedureInstance()});
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
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividualSample.INDIVIDUAL_ID.getName())==-1){
               fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyIndividualSample.INDIVIDUAL_ID.getName());
               fieldsValue=LPArray.addValueToArray1D(fieldsValue, indivId);
            }else
               fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividualSample.INDIVIDUAL_ID.getName())] = indivId;
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividualSample.STUDY.getName())==-1){
               fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyIndividualSample.STUDY.getName());
               fieldsValue=LPArray.addValueToArray1D(fieldsValue, studyName);
            }else
               fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividualSample.STUDY.getName())] = studyName;
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividualSample.CREATED_ON.getName())==-1){
               fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyIndividualSample.CREATED_ON.getName());
               fieldsValue=LPArray.addValueToArray1D(fieldsValue, LPDate.getCurrentTimeStamp());
            }else
               fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividualSample.CREATED_ON.getName())] = LPDate.getCurrentTimeStamp();
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividualSample.CREATED_BY.getName())==-1){
               fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyIndividualSample.CREATED_BY.getName());
               fieldsValue=LPArray.addValueToArray1D(fieldsValue, instanceForActions.getToken().getPersonName());
            }else
               fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividualSample.CREATED_BY.getName())] = instanceForActions.getToken().getPersonName();
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividualSample.ACTIVE.getName())==-1){
               fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyIndividualSample.ACTIVE.getName());
               fieldsValue=LPArray.addValueToArray1D(fieldsValue, ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName()));
            }else
               fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividualSample.ACTIVE.getName())] = ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName());        
    /*        fieldsName = LPArray.addValueToArray1D(fieldsName, GlobalVariables.Schemas.CONFIG.getName());    
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplate);
            fieldsName = LPArray.addValueToArray1D(fieldsName, "config_version");    
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplateVersion); 
    */
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE, fieldsName, fieldsValue);            
            if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())){
                messages.addMinorForSuccess(endpoint, fieldsValue);
                ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE, 
                        insertRecordInTable.getNewRowId().toString(), 
                    studyName, null, fieldsName, fieldsValue);
                return new InternalMessage(LPPlatform.LAB_TRUE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());
            }
            messages.addMainForError(insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
        }    
        messages.addMainForError(TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, null);
        return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, null, null);            
     }    

    public InternalMessage studyIndividualSampleActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, Integer indivId, Integer sampleId){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        InternalMessage projOpenToChanges=isStudyOpenToChanges(studyName);    
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
            messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
            return projOpenToChanges;
        }
        String[] fieldsName=new String[]{TblsGenomaData.StudyIndividualSample.ACTIVE.getName()};
        Object[] fieldsValue=new Object[]{true};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualSample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
        RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE,
            EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE, fieldsName), fieldsValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
            ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE, sampleId.toString(), 
                studyName, null, fieldsName, fieldsValue);
        }
        if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
            messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        }
        messages.addMinorForSuccess(endpoint, new Object[]{studyName});
        return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
    }    

    public InternalMessage studyIndividualSampleDeActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, Integer indivId, Integer sampleId){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        InternalMessage projOpenToChanges=isStudyOpenToChanges(studyName);    
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
            messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
            return projOpenToChanges;
        }
        String[] fieldsName=new String[]{TblsGenomaData.StudyIndividualSample.ACTIVE.getName(), TblsGenomaData.StudyIndividualSample.DEACTIVATED_BY.getName(), TblsGenomaData.StudyIndividualSample.DEACTIVATED_ON.getName()};
        Object[] fieldsValue=new Object[]{false, instanceForActions.getToken().getPersonName(),LPDate.getCurrentTimeStamp()};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualSample.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualSample.INDIVIDUAL_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{indivId}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualSample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
        RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE,
            EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE, fieldsName), fieldsValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
            ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE, sampleId.toString(), 
                studyName, null, fieldsName, fieldsValue);
        }
        if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
            messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        }
        messages.addMinorForSuccess(endpoint, new Object[]{studyName});
        return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
    }   

    public InternalMessage studyIndividualSampleUpdate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, Integer indivId, Integer sampleId, String[] fieldsName, Object[] fieldsValue){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        InternalMessage projOpenToChanges=isStudyOpenToChanges(studyName);    
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
            messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
            return projOpenToChanges;
        }

        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualSample.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualSample.INDIVIDUAL_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{indivId}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualSample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
        RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE,
            EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE, fieldsName), fieldsValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
            ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE, sampleId.toString(), 
                studyName, null, fieldsName, fieldsValue);
        }
        if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
            messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        }
        messages.addMinorForSuccess(endpoint, new Object[]{studyName});
        return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
    } 
    
}
