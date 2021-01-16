/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import com.labplanet.servicios.modulegenoma.TblsGenomaData;
import databases.Rdbms;
import static functionaljavaa.modulegenoma.GenomaConfigVariablesQueries.getVariableSetVariablesProperties;
import functionaljavaa.modulegenoma.GenomaDataAudit;
import static functionaljavaa.modulegenoma.GenomaDataStudy.isStudyOpenToChanges;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPMath;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class DataStudyObjectsVariableValues {
    
    public enum VariableTypes{CATEGORICAL, INTEGER}
    
    private static Object[][] objectFieldExtraFields(String studyName, String variableSetName, String ownerTable, String ownerId){
        Object[] fields=new Object[0];        
        switch(ownerTable){
            case "study_individual_sample":
                
                fields=LPArray.addValueToArray1D(fields, TblsGenomaData.StudyVariableValues.FLD_SAMPLE.getName());
                fields=LPArray.addValueToArray1D(fields, Integer.valueOf(ownerId));
                break;
            default:
                return new Object[0][0];
        }    
        return LPArray.array1dTo2d(fields, 2);
    }
    public static Object[] addVariableSetToObject(String studyName, String variableSetName, String ownerTable, String ownerId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] diagn=new Object[0];
        Object[] isStudyOpenToChanges=isStudyOpenToChanges(studyName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())) return isStudyOpenToChanges;
        
        Object[][] variableSetContent=getVariableSetVariablesProperties(variableSetName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(variableSetContent[0]))) return variableSetContent;
        String[] fieldHeaders=new String[0];
        for (Object[] currVar: variableSetContent){
            if (fieldHeaders.length==0){
                for (Object currVar1 : currVar) {
                    fieldHeaders = LPArray.addValueToArray1D(fieldHeaders, currVar1.toString());                
                }
            }else{
                Object[] fieldVarProperties=new Object[0];
                for (Object currVar1 : currVar) {
                    fieldVarProperties = LPArray.addValueToArray1D(fieldVarProperties, currVar1);                
                }
                String[] fieldsName=new String[]{TblsGenomaData.StudyVariableValues.FLD_STUDY.getName(), TblsGenomaData.StudyVariableValues.FLD_OWNER_TABLE.getName(), TblsGenomaData.StudyVariableValues.FLD_OWNER_ID.getName(),
                    TblsGenomaData.StudyVariableValues.FLD_VARIABLE_SET.getName()};
                fieldsName=LPArray.addValueToArray1D(fieldsName, fieldHeaders);
                Object[] fieldsValue=new Object[]{studyName, ownerTable, ownerId, variableSetName};
                fieldsValue=LPArray.addValueToArray1D(fieldsValue, fieldVarProperties);
                Object[][] extraFields=objectFieldExtraFields(studyName, variableSetName, ownerTable, ownerId);
                if (extraFields!=null && extraFields.length>0){
                    for (Object[] curFld: extraFields){
                        fieldsName=LPArray.addValueToArray1D(fieldsName, curFld[0].toString());
                        fieldsValue=LPArray.addValueToArray1D(fieldsValue, curFld[1]);
                    }
                }
                diagn=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsGenomaData.StudyVariableValues.TBL.getName(), 
                    fieldsName, fieldsValue);            
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) 
                    GenomaDataAudit.studyAuditAdd(GenomaDataAudit.StudyAuditEvents.ADD_VARIABLE_SET_TO_STUDY_OBJECT.toString(), TblsGenomaData.StudyVariableValues.TBL.getName(), Arrays.toString(currVar), 
                        studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, ":"), null);                
            }
        }        
        return diagn; //LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "not implemented yet!", null);
    }
    public static Object[] objectVariableSetValue(String studyName, String ownerTable, String ownerId, String variableSetName, String variableName, String newValue){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] diagn=new Object[0];
        Object[] isStudyOpenToChanges=isStudyOpenToChanges(studyName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())) return isStudyOpenToChanges;
        
        String[] fieldsToRetrieve=new String[]{TblsGenomaData.StudyVariableValues.FLD_ID.getName(), TblsGenomaData.StudyVariableValues.FLD_NAME.getName(), TblsGenomaData.StudyVariableValues.FLD_TYPE.getName(), TblsGenomaData.StudyVariableValues.FLD_REQUIRED.getName(), 
            TblsGenomaData.StudyVariableValues.FLD_ALLOWED_VALUES.getName()};
        
        String[] fieldsName=new String[]{TblsGenomaData.StudyVariableValues.FLD_STUDY.getName(), TblsGenomaData.StudyVariableValues.FLD_OWNER_TABLE.getName(), TblsGenomaData.StudyVariableValues.FLD_OWNER_ID.getName(),
            TblsGenomaData.StudyVariableValues.FLD_VARIABLE_SET.getName(), TblsGenomaData.StudyVariableValues.FLD_NAME.getName()};
        Object[] fieldsValue=new Object[]{studyName, ownerTable, ownerId, variableSetName, variableName};
        Object[][] objectVariablePropInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsGenomaData.StudyVariableValues.TBL.getName(),
                fieldsName, fieldsValue, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(objectVariablePropInfo[0]))) return objectVariablePropInfo;
        
        if (objectVariablePropInfo.length!=1) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Found more than one record, <*1*> for the query <*2*> on <*3*>", 
            new Object[]{objectVariablePropInfo.length, Arrays.toString(fieldsName), procInstanceName});
        
        String fieldType = objectVariablePropInfo[0][2].toString();
        if (VariableTypes.CATEGORICAL.toString().equalsIgnoreCase(fieldType)){
            String[] allowedValuesArr = LPNulls.replaceNull(objectVariablePropInfo[0][4]).toString().split("\\|");
            if (!LPArray.valueInArray(allowedValuesArr, newValue)) 
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The value <*1*> is not one of the accepted values <*2*> for variable <*3*> in procedure <*4*>", 
                    new Object[]{newValue, Arrays.toString(allowedValuesArr), variableName, procInstanceName});
        }else if (VariableTypes.INTEGER.toString().equalsIgnoreCase(fieldType)){
            if (!LPMath.isNumeric(newValue))return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The value <*1*> is not numeric for variable <*2*> in procedure <*3*>", 
                    new Object[]{newValue, variableName, procInstanceName});
        }else 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "not recognized variable type "+fieldType, null);
        String[] updFieldsName=new String[]{TblsGenomaData.StudyVariableValues.FLD_VALUE.getName()};
        Object[] updFieldsValue=new Object[]{newValue};
        diagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), TblsGenomaData.StudyVariableValues.TBL.getName(), 
            updFieldsName, updFieldsValue, new String[]{TblsGenomaData.StudyVariableValues.FLD_ID.getName()}, new Object[]{Integer.valueOf(objectVariablePropInfo[0][0].toString())});            
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) 
            GenomaDataAudit.studyAuditAdd(GenomaDataAudit.StudyAuditEvents.STUDY_OBJECT_SET_VARIABLE_VALUE.toString(), TblsGenomaData.StudyVariableValues.TBL.getName(), newValue, 
                studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(updFieldsName, updFieldsValue, ":"), null);                
        
        return diagn;
        
    }
}
