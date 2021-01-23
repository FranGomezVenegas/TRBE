/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import databases.DataDataIntegrity;
import databases.Rdbms;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class DataProgram {

    /**
     *
     */
    public DataProgram() {
        super();
    }

    /**
     *
     */
    public enum SchemaDataTableProgram{

        /**
         *
         */
        TABLE_NAME("program", "CREATE TABLE app.app_session (#FLDS) WITH (" +
                "    OIDS = FALSE" +
                ")" +
                "TABLESPACE pg_default;" +
                "" +
                "ALTER TABLE app.app_session" +
                "    OWNER to labplanet;")
        ,

        /**
         *
         */
        FIELD_SESSION_ID("session_id", "integer NOT NULL DEFAULT nextval('app.app_session_session_id_seq1'::regclass)")
        ,

        /**
         *
         */
        FIELD_DATA_STARTED("data_started", "date")
        ,

        /**
         *
         */
        FIELD_PERSON("person", "character varying COLLATE pg_catalog.\"default\"")
        ,

        /**
         *
         */
        FIELD_ROLE_NAME("role_name", "character varying COLLATE pg_catalog.\"default\"");
        
        private SchemaDataTableProgram(String dbObjName, String dbObjType){
            this.dbObjName=dbObjName;
            this.dbObjType=dbObjType;
        }

        /**
         *
         * @return
         */
        public String getName(){
            return this.dbObjName;
        }

        /**
         *
         * @return
         */
        public String[] getDbFieldDefinition(){
            return new String[]{this.dbObjName, this.dbObjType};
        }
        private final String dbObjName;             
        private final String dbObjType;             
        
    }
    /**
     *
     * @param sampleTemplate
     * @param sampleTemplateVersion
     * @param sampleFieldName
     * @param sampleFieldValue
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public Object[] createProgramDev(String sampleTemplate, Integer sampleTemplateVersion, String[] sampleFieldName, Object[] sampleFieldValue) throws IllegalAccessException, InvocationTargetException{
        return DataProgram.this.createProgram(sampleTemplate, sampleTemplateVersion, sampleFieldName, sampleFieldValue, true);
    }

    /**
     *
     * @param template
     * @param templateVersion
     * @param fieldName
     * @param fieldValue
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public Object[] createProgram(String template, Integer templateVersion, String[] fieldName, Object[] fieldValue) throws IllegalAccessException, InvocationTargetException{
        return DataProgram.this.createProgram(template, templateVersion, fieldName, fieldValue, false);
    }

Object[] createProgram(String projectTemplate, Integer projectTemplateVersion, String[] sampleFieldName, Object[] sampleFieldValue, Boolean devMode) throws IllegalAccessException, InvocationTargetException{
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String classVersionProj = "0.1";
    String[] mandatoryFieldsProj = null;
    Object[] mandatoryFieldsValueProj = null;
    String[] javaDocFieldsProj = new String[0];
    Object[] javaDocValuesProj = new Object[0];
    String javaDocLineNameProj = "";
    DataDataIntegrity labIntChecker = new DataDataIntegrity();

    if (devMode){
        StackTraceElement[] elementsDev = Thread.currentThread().getStackTrace();
        javaDocLineNameProj = "BEGIN";
        javaDocFieldsProj = LPArray.addValueToArray1D(javaDocFieldsProj, LPPlatform.JAVADOC_LINE_FLDNAME);
        javaDocValuesProj = LPArray.addValueToArray1D(javaDocValuesProj, javaDocLineNameProj);
        javaDocFieldsProj = LPArray.addValueToArray1D(javaDocFieldsProj, LPPlatform.JAVADOC_CLASS_FLDNAME);
        javaDocValuesProj = LPArray.addValueToArray1D(javaDocValuesProj, classVersionProj);
        LPPlatform.addJavaClassDoc(javaDocFieldsProj, javaDocValuesProj, elementsDev);
    }    
        String tableName = "project";
        String actionName = "Insert";
        
        String schemaDataName = GlobalVariables.Schemas.DATA.getName();
        String schemaConfigName = GlobalVariables.Schemas.CONFIG.getName();
        
        schemaDataName = LPPlatform.buildSchemaName(procInstanceName, schemaDataName);    
        schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, schemaConfigName); 
        
        mandatoryFieldsProj = labIntChecker.getTableMandatoryFields(schemaDataName, tableName, actionName);
        
        
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
        String[] diagnosesProj = LPArray.checkTwoArraysSameLength(sampleFieldName, sampleFieldValue);
        if (sampleFieldName.length!=sampleFieldValue.length){
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            diagnosesProj[0]= elements[1].getClassName() + "." + elements[1].getMethodName();
            diagnosesProj[1]= classVersionProj;
            diagnosesProj[2]= "Code Line " + (elements[1].getLineNumber());   
            diagnosesProj[3]=LPPlatform.LAB_FALSE;
            diagnosesProj[4]="ERROR:Field names and values arrays with different length";
            diagnosesProj[5]="The values in FieldName are:"+ Arrays.toString(sampleFieldName)+". and in FieldValue are:"+Arrays.toString(sampleFieldValue);
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
        if (LPArray.duplicates(sampleFieldName)){
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            diagnosesProj[0]= elements[1].getClassName() + "." + elements[1].getMethodName();
            diagnosesProj[1]= classVersionProj;
            diagnosesProj[2]= "Code Line " + (elements[1].getLineNumber());
            diagnosesProj[3]=LPPlatform.LAB_FALSE;
            diagnosesProj[4]="ERROR:Fields duplicated";
            diagnosesProj[5]="Detected any field duplicated in FieldName, the values are:"+(char) 10 + Arrays.toString(sampleFieldName);
            return diagnosesProj;
        }

        // spec is not mandatory but when any of the fields involved is added to the parameters 
        //  then it turns mandatory all the fields required for linking this entity.
        Integer fieldIndexSpecCode = Arrays.asList(sampleFieldName).indexOf("spec_code");
        Integer fieldIndexSpecCodeVersion = Arrays.asList(sampleFieldName).indexOf("spec_code_version");
        Integer fieldIndexSpecVariationName = Arrays.asList(sampleFieldName).indexOf("spec_variation_name");
        if ((fieldIndexSpecCode!=-1) || (fieldIndexSpecCodeVersion!=-1) || (fieldIndexSpecVariationName!=-1)){
            mandatoryFieldsProj = LPArray.addValueToArray1D(mandatoryFieldsProj, "spec_code");
            mandatoryFieldsProj = LPArray.addValueToArray1D(mandatoryFieldsProj, "spec_code_version");
            mandatoryFieldsProj = LPArray.addValueToArray1D(mandatoryFieldsProj, "spec_variation_name");
        }
        mandatoryFieldsValueProj = new Object[mandatoryFieldsProj.length];
        StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);
        for (Integer inumLines=0;inumLines<mandatoryFieldsProj.length;inumLines++){
            String currField = mandatoryFieldsProj[inumLines];
            boolean contains = Arrays.asList(sampleFieldName).contains(currField.toLowerCase());
            if (!contains){
                if (mandatoryFieldsMissingBuilder.length()>0){mandatoryFieldsMissingBuilder.append(",");}
                
                mandatoryFieldsMissingBuilder.append(currField);
                
            }else{
                Integer valuePosic = Arrays.asList(sampleFieldName).indexOf(currField);
                mandatoryFieldsValueProj[inumLines] = sampleFieldValue[valuePosic]; 
            }        
        }            
        if (mandatoryFieldsMissingBuilder.length()>0){
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Mandatory fields not found: <*1*>", new Object[]{mandatoryFieldsMissingBuilder.toString()});
        }        
        Object[] diagnosis = Rdbms.existsRecord(schemaConfigName, tableName, new String[]{GlobalVariables.Schemas.CONFIG.getName(),"config_version"}, new Object[]{projectTemplate, projectTemplateVersion});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosis[0].toString())) return diagnosis;

        String[] specialFields = labIntChecker.getStructureSpecialFields(schemaDataName, "projectStructure");
        String[] specialFieldsFunction = labIntChecker.getStructureSpecialFieldsFunction(schemaDataName, "projectStructure");
        
        Integer specialFieldIndex = -1;
        for (Integer inumLines=0;inumLines<sampleFieldName.length;inumLines++){
            String currField = tableName+"." + sampleFieldName[inumLines];
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
        sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, GlobalVariables.Schemas.CONFIG.getName());    
        sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, projectTemplate);
        sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, "config_version");    
        sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, projectTemplateVersion); 

        diagnosesProj = Rdbms.insertRecordInTable(schemaDataName, tableName, sampleFieldName, sampleFieldValue);

        //smpAudit.sampleAuditAdd(rdbm, procInstanceName, auditActionName, "sample", Integer.parseInt(diagnosesProj[6]), Integer.parseInt(diagnosesProj[6]), null, null, fieldsOnLogSample, userName, userRole);

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

}

