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
import databases.features.Token;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
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
public class GenomaDataStudyIndividualSamples {

public InternalMessage createStudyIndividualSample(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, Integer indivId, String[] fieldsName, Object[] fieldsValue, Boolean devMode){
    ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
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
        mandatoryFields = labIntChecker.getTableMandatoryFields(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), actionName);
        if (!devMode){
            InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(fieldsName, fieldsValue);
            if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic()))
                return fieldNameValueArrayChecker;
        }    
        InternalMessage diagnosesProj = null;
        if (!devMode){        
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
               fieldsValue=LPArray.addValueToArray1D(fieldsValue, token.getPersonName());
            }else
               fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividualSample.CREATED_BY.getName())] = token.getPersonName();
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividualSample.ACTIVE.getName())==-1){
               fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyIndividualSample.ACTIVE.getName());
               fieldsValue=LPArray.addValueToArray1D(fieldsValue, GenomaEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName()));
            }else
               fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividualSample.ACTIVE.getName())] = GenomaEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName());        
    /*        fieldsName = LPArray.addValueToArray1D(fieldsName, GlobalVariables.Schemas.CONFIG.getName());    
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplate);
            fieldsName = LPArray.addValueToArray1D(fieldsName, "config_version");    
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplateVersion); 
    */
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE, fieldsName, fieldsValue);            
            if (insertRecordInTable.getRunSuccess()){
                GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), 
                        insertRecordInTable.getNewRowId().toString(), 
                    studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
                return new InternalMessage(LPPlatform.LAB_TRUE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), insertRecordInTable.getNewRowId());
            }
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);            
        }    
        return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, null, null);            
     }    

    public Object[] studyIndividualSampleActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, Integer indivId, Integer sampleId){
        String[] fieldsName=new String[]{TblsGenomaData.StudyIndividualSample.ACTIVE.getName()};
        Object[] fieldsValue=new Object[]{true};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualSample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
        Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE,
            EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE, fieldsName), fieldsValue, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
            GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), sampleId.toString(), 
                studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        return diagnosesProj;      
    }    

    public Object[] studyIndividualSampleDeActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, Integer indivId, Integer sampleId){
        Object[] projStudyToChanges=GenomaDataStudy.isStudyOpenToChanges(studyName);    
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyToChanges[0].toString())) return projStudyToChanges;

        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);    
        String[] fieldsName=new String[]{TblsGenomaData.StudyIndividualSample.ACTIVE.getName(), TblsGenomaData.StudyIndividualSample.DEACTIVATED_BY.getName(), TblsGenomaData.StudyIndividualSample.DEACTIVATED_ON.getName()};
        Object[] fieldsValue=new Object[]{false, instanceForActions.getToken().getPersonName(),LPDate.getCurrentTimeStamp()};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualSample.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualSample.INDIVIDUAL_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{indivId}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualSample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
        Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE,
            EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE, fieldsName), fieldsValue, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
            GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), sampleId.toString(), 
                studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        return diagnosesProj;      
    }   

    public Object[] studyIndividualSampleUpdate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, Integer indivId, Integer sampleId, String[] fieldsName, Object[] fieldsValue){
        Object[] projStudyToChanges=GenomaDataStudy.isStudyOpenToChanges(studyName);    
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyToChanges[0].toString())) return projStudyToChanges;

        Object[] specialFieldsPresent=GenomaEnums.specialFieldsInUpdateArray(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), fieldsName);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(specialFieldsPresent[0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, specialFieldsPresent[specialFieldsPresent.length-1].toString(), null);
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualSample.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualSample.INDIVIDUAL_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{indivId}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualSample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
        Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE,
            EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE, fieldsName), fieldsValue, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
            GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), sampleId.toString(), 
                studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        return diagnosesProj;      
    } 
    
}
