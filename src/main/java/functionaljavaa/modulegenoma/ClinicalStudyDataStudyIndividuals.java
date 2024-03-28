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
import static functionaljavaa.modulegenoma.ClinicalStudyDataStudy.isStudyOpenToChanges;
import functionaljavaa.modulegenoma.ClinicalStudyEnums.GenomaErrorTrapping;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ResponseMessages;
/**
 *
 * @author User
 */
public class ClinicalStudyDataStudyIndividuals {
    public InternalMessage createStudyIndividual(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String indivName, String[] fieldsName, Object[] fieldsValue, Boolean devMode){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        InternalMessage projOpenToChanges=isStudyOpenToChanges(studyName);    
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
            messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
            return projOpenToChanges;
        }

        String classVersionProj = "0.1";
        String[] mandatoryFields = null;
        Object[] mandatoryFieldsValue = fieldsValue;
        String[] javaDocFields = new String[0];
        Object[] javaDocValues = new Object[0];
        String javaDocLineName = "";
        DataDataIntegrity labIntChecker = new DataDataIntegrity();
        if (fieldsName==null) fieldsName=new String[0];
        if (fieldsValue==null) fieldsValue=new Object[0];

        if (Boolean.TRUE.equals(devMode)){
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

            mandatoryFields = labIntChecker.getTableMandatoryFields(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), actionName);


        if (Boolean.TRUE.equals(devMode)){
            StackTraceElement[] elementsDev = Thread.currentThread().getStackTrace();
            javaDocLineName = "CHECK sampleFieldName and sampleFieldValue match in length";
            javaDocFields = LPArray.addValueToArray1D(javaDocFields, ApiMessageReturn.JAVADOC_LINE_FLDNAME);
            javaDocValues = LPArray.addValueToArray1D(javaDocValues, javaDocLineName);
            javaDocFields = LPArray.addValueToArray1D(javaDocFields, ApiMessageReturn.JAVADOC_CLASS_FLDNAME);
            javaDocValues = LPArray.addValueToArray1D(javaDocValues, classVersionProj);
            LPPlatform.addJavaClassDoc(javaDocFields, javaDocValues, elementsDev);
        }    
        Object[] checkTwoArraysSameLength = LPArray.checkTwoArraysSameLength(fieldsName, fieldsValue);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkTwoArraysSameLength[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ARRAYS_DIFFERENT_SIZE, new Object[]{checkTwoArraysSameLength[checkTwoArraysSameLength.length - 1].toString()}, null);
        }
        if (Boolean.TRUE.equals(devMode)){
            StackTraceElement[] elementsDev = Thread.currentThread().getStackTrace();
            javaDocLineName = "CHECK sampleFieldName and sampleFieldValue match in length";
            javaDocFields = LPArray.addValueToArray1D(javaDocFields, ApiMessageReturn.JAVADOC_LINE_FLDNAME);
            javaDocValues = LPArray.addValueToArray1D(javaDocValues, javaDocLineName);
            javaDocFields = LPArray.addValueToArray1D(javaDocFields, ApiMessageReturn.JAVADOC_CLASS_FLDNAME);
            javaDocValues = LPArray.addValueToArray1D(javaDocValues, classVersionProj);
            LPPlatform.addJavaClassDoc(javaDocFields, javaDocValues, elementsDev);
        }    
        Object[] diagnosesProj = new Object[0];
        if (Boolean.FALSE.equals(devMode)){
            if (LPArray.duplicates(fieldsName)) {
                messages.addMainForError(TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.FIELDS_DUPLICATED, fieldsName);
                return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.FIELDS_DUPLICATED, fieldsName);
            }

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
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividual.INDIV_NAME.getName())==-1){
               fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyIndividual.INDIV_NAME.getName());
               fieldsValue=LPArray.addValueToArray1D(fieldsValue, indivName);
            }else
               fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividual.INDIV_NAME.getName())] = indivName;
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividual.STUDY.getName())==-1){
               fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyIndividual.STUDY.getName());
               fieldsValue=LPArray.addValueToArray1D(fieldsValue, studyName);
            }else
               fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividual.STUDY.getName())] = studyName;
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividual.CREATED_ON.getName())==-1){
               fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyIndividual.CREATED_ON.getName());
               fieldsValue=LPArray.addValueToArray1D(fieldsValue, LPDate.getCurrentTimeStamp());
            }else
               fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividual.CREATED_ON.getName())] = LPDate.getCurrentTimeStamp();
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividual.CREATED_BY.getName())==-1){
               fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyIndividual.CREATED_BY.getName());
               fieldsValue=LPArray.addValueToArray1D(fieldsValue, token.getPersonName());
            }else
               fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividual.CREATED_BY.getName())] = token.getPersonName();
            if (LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividual.ACTIVE.getName())==-1){
               fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyIndividual.ACTIVE.getName());
               fieldsValue=LPArray.addValueToArray1D(fieldsValue, ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName()));
            }else
               fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividual.ACTIVE.getName())] = ClinicalStudyEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName());        
    /*        fieldsName = LPArray.addValueToArray1D(fieldsName, GlobalVariables.Schemas.CONFIG.getName());    
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplate);
            fieldsName = LPArray.addValueToArray1D(fieldsName, "config_version");    
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplateVersion); 
    */
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, fieldsName, fieldsValue);
            diagnosesProj = insertRecordInTable.getApiMessage();
            if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())){
                ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, indivName, 
                    studyName, null, fieldsName, fieldsValue);
                diagnosesProj=LPArray.addValueToArray1D(diagnosesProj, insertRecordInTable.getNewRowId());
            }
            if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
                messages.addMainForError(insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());
                return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());
            }else{
                messages.addMinorForSuccess(endpoint, new Object[]{studyName, indivName});
                return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName, indivName});
            }              
        }    
        if (Boolean.TRUE.equals(devMode)){
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
        messages.addMinorForSuccess(endpoint, new Object[]{studyName, indivName});
        return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName, indivName});
    }    

    public InternalMessage studyIndividualActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, Integer indivId){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        String[] fieldsName=new String[]{TblsGenomaData.StudyIndividual.ACTIVE.getName()};
        Object[] fieldsValue=new Object[]{true};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividual.INDIVIDUAL_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{indivId}, "");
        RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL,
            EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, fieldsName), fieldsValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
            ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, indivId.toString(), 
                studyName, null, fieldsName, fieldsValue);
        }
        if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
            messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        }
        messages.addMinorForSuccess(endpoint, new Object[]{studyName});
        return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
    }    

    public InternalMessage studyIndividualDeActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, Integer indivId){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividual.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividual.INDIVIDUAL_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{indivId}, "");

        InternalMessage projOpenToChanges=isStudyOpenToChanges(studyName);    
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())) return projOpenToChanges;

        Object[][] studyIndivInfo = QueryUtilitiesEnums.getTableData(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL,
                EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, TblsGenomaData.StudyIndividual.ACTIVE.getName()), sqlWhere, null);
        if ("false".equalsIgnoreCase(studyIndivInfo[0][0].toString())){
            messages.addMainForError(Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{studyName, indivId});
            return new InternalMessage(LPPlatform.LAB_FALSE, endpoint, new Object[]{studyName, indivId});
        }
        String[] fieldsName=new String[]{TblsGenomaData.StudyIndividual.ACTIVE.getName(), TblsGenomaData.StudyIndividual.DEACTIVATED_BY.getName(), TblsGenomaData.StudyIndividual.DEACTIVATED_ON.getName()};
        Object[] fieldsValue=new Object[]{false, instanceForActions.getToken().getPersonName(),LPDate.getCurrentTimeStamp()};
        RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL,
            EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, fieldsName), fieldsValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
            ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, indivId.toString(), 
                studyName, null, fieldsName, fieldsValue);
        }
        if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
            messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        }
        messages.addMinorForSuccess(endpoint, new Object[]{studyName});
        return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
    }   

    public InternalMessage studyIndividualUpdate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, Integer indivId, String[] fieldsName, Object[] fieldsValue){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        InternalMessage projOpenToChanges=isStudyOpenToChanges(studyName);    
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
            messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
            return projOpenToChanges;
        }

        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividual.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividual.INDIVIDUAL_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{indivId}, "");
        RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL,
            EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, fieldsName), fieldsValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnosesProj.getRunSuccess())) {
            ClinicalStudyDataAudit.studyAuditAdd(endpoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, studyName, 
                studyName, null, fieldsName, fieldsValue);
        }
        if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
            messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        }
        messages.addMinorForSuccess(endpoint, new Object[]{studyName});
        return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
    } 
    
    public static InternalMessage isStudyIndividualOpenToChanges(String studyName, Integer individualId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(procInstanceName,LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(),
            new String[]{TblsGenomaData.StudyIndividual.STUDY.getName(), TblsGenomaData.StudyIndividual.INDIVIDUAL_ID.getName()}, new Object[]{studyName, individualId}, new String[]{TblsGenomaData.StudyIndividual.ACTIVE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.STUDY_INDIVIDUAL_NOT_FOUND, new Object[]{studyName, procInstanceName});
        if (Boolean.FALSE.equals(Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString())))
            return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.STUDY_INDIVIDUAL_ALREADY_DEACTIVATED, new Object[]{studyName, procInstanceName});
        return new InternalMessage(LPPlatform.LAB_TRUE, ClinicalStudyEnums.GenomaSuccess.STUDY_INDIVIDUAL_OPEN_TO_CHANGES, new Object[]{studyName, individualId, procInstanceName});
    }    
}
