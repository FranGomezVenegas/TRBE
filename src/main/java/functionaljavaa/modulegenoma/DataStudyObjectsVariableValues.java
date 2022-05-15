/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import com.labplanet.servicios.modulegenoma.GenomaStudyObjectsVariablesAPI;
import com.labplanet.servicios.modulegenoma.TblsGenomaData;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import static functionaljavaa.modulegenoma.GenomaConfigVariablesQueries.getVariableSetVariablesProperties;
import static functionaljavaa.modulegenoma.GenomaDataStudy.isStudyOpenToChanges;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import static lbplanet.utilities.LPMath.isNumeric;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author User
 */
public class DataStudyObjectsVariableValues {
    
    public enum VariableTypes{LIST, INTEGER, REAL, TEXT}
    
    private static Object[][] objectFieldExtraFields(String studyName, String variableSetName, String ownerTable, String ownerId){
        Object[] fields=new Object[0];        
        switch(ownerTable){
            case "study_individual_sample":
                
                fields=LPArray.addValueToArray1D(fields, TblsGenomaData.StudyVariableValues.SAMPLE.getName());
                fields=LPArray.addValueToArray1D(fields, Integer.valueOf(ownerId));
                break;
            default:
                return new Object[0][0];
        }    
        return LPArray.array1dTo2d(fields, 2);
    }
    public static Object[] addVariableSetToObject(GenomaStudyObjectsVariablesAPI.GenomaStudyObjectsVariablesAPIEndPoints endPoint, String studyName, String variableSetName, String ownerTable, String ownerId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] diagn=new Object[0];
        Object[] isStudyOpenToChanges=isStudyOpenToChanges(studyName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())) return isStudyOpenToChanges;
        
        Object[][] variableSetContent=getVariableSetVariablesProperties(variableSetName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(variableSetContent[0]))) return variableSetContent;
        String[] fieldHeaders=new String[0];
        RdbmsObject insertRecordInTable = null;
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
                String[] fieldsName=new String[]{TblsGenomaData.StudyVariableValues.STUDY.getName(), TblsGenomaData.StudyVariableValues.OWNER_TABLE.getName(), TblsGenomaData.StudyVariableValues.OWNER_ID.getName(),
                    TblsGenomaData.StudyVariableValues.VARIABLE_SET.getName()};
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
                insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES, fieldsName, fieldsValue);            
                if (!insertRecordInTable.getRunSuccess()) return insertRecordInTable.getApiMessage();
                    GenomaDataAudit.studyAuditAdd(endPoint, TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES.getTableName(), Arrays.toString(currVar), 
                        studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(fieldsName, fieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);                
            }
        }        
        return insertRecordInTable.getApiMessage();
    }
    public static Object[] objectVariableSetValue(GenomaStudyObjectsVariablesAPI.GenomaStudyObjectsVariablesAPIEndPoints endpoint, String studyName, String ownerTable, String ownerId, String variableSetName, String variableName, String newValue){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] diagn=new Object[0];
        Object[] isStudyOpenToChanges=isStudyOpenToChanges(studyName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())) return isStudyOpenToChanges;
        
        String[] fieldsToRetrieve=new String[]{TblsGenomaData.StudyVariableValues.ID.getName(), TblsGenomaData.StudyVariableValues.NAME.getName(), TblsGenomaData.StudyVariableValues.TYPE.getName(), TblsGenomaData.StudyVariableValues.REQUIRED.getName(), 
            TblsGenomaData.StudyVariableValues.ALLOWED_VALUES.getName()};
        
        String[] fieldsName=new String[]{TblsGenomaData.StudyVariableValues.STUDY.getName(), TblsGenomaData.StudyVariableValues.OWNER_TABLE.getName(), TblsGenomaData.StudyVariableValues.OWNER_ID.getName(),
            TblsGenomaData.StudyVariableValues.VARIABLE_SET.getName(), TblsGenomaData.StudyVariableValues.NAME.getName()};
        Object[] fieldsValue=new Object[]{studyName, ownerTable, ownerId, variableSetName, variableName};
        Object[][] objectVariablePropInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES.getTableName(),
                fieldsName, fieldsValue, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(objectVariablePropInfo[0]))) return objectVariablePropInfo;
        
        if (objectVariablePropInfo.length!=1) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "Found more than one record, <*1*> for the query <*2*> on <*3*>", 
            new Object[]{objectVariablePropInfo.length, Arrays.toString(fieldsName), procInstanceName});
        
        String fieldType = objectVariablePropInfo[0][2].toString();
        if (VariableTypes.LIST.toString().equalsIgnoreCase(fieldType)){
            String[] allowedValuesArr = LPNulls.replaceNull(objectVariablePropInfo[0][4]).toString().split("\\|");
            if (!LPArray.valueInArray(allowedValuesArr, newValue)) 
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The value <*1*> is not one of the accepted values <*2*> for variable <*3*> in procedure <*4*>", 
                    new Object[]{newValue, Arrays.toString(allowedValuesArr), variableName, procInstanceName});
        }else if (VariableTypes.INTEGER.toString().equalsIgnoreCase(fieldType)){
            Object[] isNumeric = isNumeric(newValue);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric[0].toString())) 
                return isNumeric;
        }else 
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "not recognized variable type "+fieldType, null);
        String[] updFieldsName=new String[]{TblsGenomaData.StudyVariableValues.VALUE.getName()};
        Object[] updFieldsValue=new Object[]{newValue};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyVariableValues.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{Integer.valueOf(objectVariablePropInfo[0][0].toString())}, "");
        diagn=Rdbms.updateRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES,
            EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES, updFieldsName), updFieldsValue, sqlWhere, null);
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) 
            GenomaDataAudit.studyAuditAdd(endpoint, TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES.getTableName(), newValue, 
                studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(updFieldsName, updFieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);                
        
        return diagn;
        
    }
}
