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
import functionaljavaa.modulegenoma.GenomaEnums.GenomaErrorTrapping;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author User
 */
public class GenomaDataStudyIndividuals {
    public Object[] createStudyIndividual(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String indivName, String[] fieldsName, Object[] fieldsValue, Boolean devMode){
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
        if (!devMode){
            Object[] diagnosesProj = LPArray.checkTwoArraysSameLength(fieldsName, fieldsValue);
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
               fieldsValue=LPArray.addValueToArray1D(fieldsValue, GenomaEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName()));
            }else
               fieldsValue[LPArray.valuePosicInArray(fieldsName, TblsGenomaData.StudyIndividual.ACTIVE.getName())] = GenomaEnums.activateOnCreation(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName());        
    /*        fieldsName = LPArray.addValueToArray1D(fieldsName, GlobalVariables.Schemas.CONFIG.getName());    
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplate);
            fieldsName = LPArray.addValueToArray1D(fieldsName, "config_version");    
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, projectTemplateVersion); 
    */
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, fieldsName, fieldsValue);
            diagnosesProj = insertRecordInTable.getApiMessage();
            if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())){
                GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), indivName, 
                    studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
                diagnosesProj=LPArray.addValueToArray1D(diagnosesProj, insertRecordInTable.getNewRowId());
            }
            return diagnosesProj;  
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
        return diagnosesProj; 
    }    

    public Object[] studyIndividualActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, Integer indivId){
        String[] fieldsName=new String[]{TblsGenomaData.StudyIndividual.ACTIVE.getName()};
        Object[] fieldsValue=new Object[]{true};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividual.INDIVIDUAL_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{indivId}, "");
        Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL,
            EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, fieldsName), fieldsValue, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
            GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), indivId.toString(), 
                studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        return diagnosesProj;      
    }    

    public Object[] studyIndividualDeActivate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, Integer indivId){
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividual.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividual.INDIVIDUAL_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{indivId}, "");

        Object[] projStudyToChanges=GenomaDataStudy.isStudyOpenToChanges(studyName);    
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyToChanges[0].toString())) return projStudyToChanges;

        Object[][] studyIndivInfo = QueryUtilitiesEnums.getTableData(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL,
                EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, TblsGenomaData.StudyIndividual.ACTIVE.getName()), sqlWhere, null);
        if ("false".equalsIgnoreCase(studyIndivInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.STUDY_INDIVIDUAL_ALREADY_DEACTIVATED, new Object[]{studyName, indivId});
        
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);    
        String[] fieldsName=new String[]{TblsGenomaData.StudyIndividual.ACTIVE.getName(), TblsGenomaData.StudyIndividual.DEACTIVATED_BY.getName(), TblsGenomaData.StudyIndividual.DEACTIVATED_ON.getName()};
        Object[] fieldsValue=new Object[]{false, instanceForActions.getToken().getPersonName(),LPDate.getCurrentTimeStamp()};
        Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL,
            EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, fieldsName), fieldsValue, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
            GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), indivId.toString(), 
                studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        return diagnosesProj;      
    }   

    public Object[] studyIndividualUpdate(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, Integer indivId, String[] fieldsName, Object[] fieldsValue){
        Object[] projStudyToChanges=GenomaDataStudy.isStudyOpenToChanges(studyName);    
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyToChanges[0].toString())) return projStudyToChanges;

        Object[] specialFieldsPresent=GenomaEnums.specialFieldsInUpdateArray(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), fieldsName);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(specialFieldsPresent[0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, specialFieldsPresent[specialFieldsPresent.length-1].toString(), null);
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividual.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividual.INDIVIDUAL_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{indivId}, "");
        Object[] diagnosesProj = Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL,
            EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL, fieldsName), fieldsValue, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesProj[0].toString()))
            GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), studyName, 
                studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
        return diagnosesProj;      
    } 
public static Object[] isStudyIndividualOpenToChanges(String studyName, Integer individualId){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(),
        new String[]{TblsGenomaData.StudyIndividual.STUDY.getName(), TblsGenomaData.StudyIndividual.INDIVIDUAL_ID.getName()}, new Object[]{studyName, individualId}, new String[]{TblsGenomaData.StudyIndividual.ACTIVE.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.STUDY_INDIVIDUAL_NOT_FOUND, new Object[]{studyName, procInstanceName});
    if (!Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][0]).toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.STUDY_INDIVIDUAL_ALREADY_DEACTIVATED, new Object[]{studyName, procInstanceName});
    return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "<*1*> is open to changes in procedure <*2*>", new Object[]{studyName, procInstanceName});
}    
}
