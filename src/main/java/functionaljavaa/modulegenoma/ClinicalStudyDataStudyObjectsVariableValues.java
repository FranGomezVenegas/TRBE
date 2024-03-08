/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import module.clinicalstudies.apis.GenomaStudyAPI;
import module.clinicalstudies.definition.TblsGenomaConfig;
import module.clinicalstudies.definition.TblsGenomaData;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import static functionaljavaa.modulegenoma.ClinicalStudyConfigVariablesQueries.getVariableSetVariablesProperties;
import static functionaljavaa.modulegenoma.ClinicalStudyDataStudy.isStudyOpenToChanges;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import static lbplanet.utilities.LPMath.isNumeric;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.instrumentsmanagement.definition.InstrumentsEnums;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public class ClinicalStudyDataStudyObjectsVariableValues {

    public enum VariableTypes {
        LIST, INTEGER, REAL, TEXT, FILE
    }

    private static Object[][] objectFieldExtraFields(String studyName, String variableSetName, String ownerTable, String ownerId) {
        Object[] fields = new Object[0];
        switch (ownerTable) {
            case "study_individual_sample":

                fields = LPArray.addValueToArray1D(fields, TblsGenomaData.StudyVariableValues.SAMPLE.getName());
                fields = LPArray.addValueToArray1D(fields, Integer.valueOf(ownerId));
                break;
            default:
                return new Object[0][0];
        }
        return LPArray.array1dTo2d(fields, 2);
    }

    public static InternalMessage addVariableSetToObject(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endPoint, String studyName, String variableSetName, String ownerTable, String ownerId) {
        InternalMessage studyOpenToChanges = ClinicalStudyDataStudy.isStudyOpenToChanges(studyName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyOpenToChanges.getDiagnostic())) {
            return studyOpenToChanges;
        }

        Object[][] variableSetContent = getVariableSetVariablesProperties(variableSetName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(variableSetContent[0]))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ClinicalStudyEnums.GenomaErrorTrapping.VARIABLES_SET_NOT_FOUND, null);
        }
        RdbmsObject insertRecordInTable = null;
        //for (Object[] currVar: variableSetContent){
        for (int i = 1; i < variableSetContent.length; i++) {
            String[] fieldHeaders = new String[0];
//            if (fieldHeaders.length==0){
            for (Object currVar1 : variableSetContent[0]) {
                fieldHeaders = LPArray.addValueToArray1D(fieldHeaders, currVar1.toString());
            }
//            }else{
            Object[] fieldVarProperties = new Object[0];
            for (Object currVar1 : variableSetContent[i]) {
                fieldVarProperties = LPArray.addValueToArray1D(fieldVarProperties, currVar1);
            }
            String[] fieldsName = new String[]{TblsGenomaData.StudyVariableValues.STUDY.getName(), TblsGenomaData.StudyVariableValues.OWNER_TABLE.getName(), TblsGenomaData.StudyVariableValues.OWNER_ID.getName(),
                TblsGenomaData.StudyVariableValues.VARIABLE_SET.getName()};
            fieldsName = LPArray.addValueToArray1D(fieldsName, fieldHeaders);
            Object[] fieldsValue = new Object[]{studyName, ownerTable, ownerId, variableSetName};
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, fieldVarProperties);
            Object[][] extraFields = objectFieldExtraFields(studyName, variableSetName, ownerTable, ownerId);
            if (extraFields != null && extraFields.length > 0) {
                for (Object[] curFld : extraFields) {
                    fieldsName = LPArray.addValueToArray1D(fieldsName, curFld[0].toString());
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, curFld[1]);
                }
            }
            insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES, fieldsName, fieldsValue);
            if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
                ClinicalStudyDataAudit.studyAuditAdd(endPoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES, Arrays.toString(variableSetContent[i]),
                        studyName, null, fieldsName, fieldsValue);
            } else {
                return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
            }
//            }            
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), variableSetName);
        //return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, null, null);            
    }

    public static InternalMessage addVariableToObject(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endPoint, String studyName, String variableName, String ownerTable, String ownerId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        InternalMessage studyOpenToChanges = ClinicalStudyDataStudy.isStudyOpenToChanges(studyName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyOpenToChanges.getDiagnostic())) {
            return studyOpenToChanges;
        }

        Object[] existsRecord = Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsGenomaConfig.TablesGenomaConfig.VARIABLES.getRepositoryName()), TblsGenomaConfig.TablesGenomaConfig.VARIABLES.getTableName(),
                new String[]{TblsGenomaConfig.Variables.NAME.getName()}, new Object[]{variableName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsRecord[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ClinicalStudyEnums.GenomaErrorTrapping.VARIABLE_NOT_FOUND, null);
        }
        String[] fieldsName = new String[]{TblsGenomaData.StudyVariableValues.STUDY.getName(), TblsGenomaData.StudyVariableValues.OWNER_TABLE.getName(), TblsGenomaData.StudyVariableValues.OWNER_ID.getName(),
            TblsGenomaData.StudyVariableValues.NAME.getName()};
        Object[] fieldsValue = new Object[]{studyName, ownerTable, ownerId, variableName};
        Object[][] extraFields = objectFieldExtraFields(studyName, variableName, ownerTable, ownerId);
        if (extraFields != null && extraFields.length > 0) {
            for (Object[] curFld : extraFields) {
                fieldsName = LPArray.addValueToArray1D(fieldsName, curFld[0].toString());
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, curFld[1]);
            }
        }
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES, fieldsName, fieldsValue);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
            ClinicalStudyDataAudit.studyAuditAdd(endPoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES, variableName,
                    studyName, null, fieldsName, fieldsValue);
            return new InternalMessage(LPPlatform.LAB_TRUE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), insertRecordInTable.getNewRowId());
        }
        return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
    }

    public static InternalMessage objectVariableSetValue(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String ownerTable, String ownerId, String variableSetName, String variableName, String newValue) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] diagn = new Object[0];
        InternalMessage isStudyOpenToChanges = isStudyOpenToChanges(studyName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges.getDiagnostic())) {
            return isStudyOpenToChanges;
        }

        String[] fieldsToRetrieve = new String[]{TblsGenomaData.StudyVariableValues.ID.getName(), TblsGenomaData.StudyVariableValues.NAME.getName(), TblsGenomaData.StudyVariableValues.PARAM_TYPE.getName(), TblsGenomaData.StudyVariableValues.REQUIRED.getName(),
            TblsGenomaData.StudyVariableValues.ALLOWED_VALUES.getName()};

        String[] fieldsName = new String[]{TblsGenomaData.StudyVariableValues.STUDY.getName(), TblsGenomaData.StudyVariableValues.OWNER_TABLE.getName(), TblsGenomaData.StudyVariableValues.OWNER_ID.getName(),
            TblsGenomaData.StudyVariableValues.VARIABLE_SET.getName(), TblsGenomaData.StudyVariableValues.NAME.getName()};
        Object[] fieldsValue = new Object[]{studyName, ownerTable, ownerId, variableSetName, variableName};
        Object[][] objectVariablePropInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES.getTableName(),
                fieldsName, fieldsValue, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(objectVariablePropInfo[0]))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrEventsErrorTrapping.VARIABLE_NOT_EXISTS,
                    new Object[]{variableSetName, variableName, procInstanceName});
        }

        if (objectVariablePropInfo.length != 1) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrEventsErrorTrapping.MORE_THAN_ONE_VARIABLE,
                    new Object[]{objectVariablePropInfo.length, Arrays.toString(fieldsName), procInstanceName});
        }

        String fieldType = objectVariablePropInfo[0][2].toString().toUpperCase();
        switch (ClinicalStudyDataStudyObjectsVariableValues.VariableTypes.valueOf(fieldType)) {
            case LIST:
                String[] allowedValuesArr = LPNulls.replaceNull(objectVariablePropInfo[0][4]).toString().split("\\|");
                if (Boolean.FALSE.equals(LPArray.valueInArray(allowedValuesArr, newValue))) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrEventsErrorTrapping.VARIABLE_VALUE_NOTONEOFTHEEXPECTED,
                            new Object[]{newValue, Arrays.toString(allowedValuesArr), variableName, procInstanceName});
                }
                break;
            case REAL:
            case INTEGER:
                Object[] isNumeric = isNumeric(newValue);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric[0].toString())) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrEventsErrorTrapping.NOT_NUMERIC_VALUE, null, null);
                }
                break;
            case TEXT:
                break;
            default:
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.VARIABLE_TYPE_NOT_RECOGNIZED, new Object[]{fieldType}, null);
        }
        String[] updFieldsName = new String[]{TblsGenomaData.StudyVariableValues.VALUE.getName()};
        Object[] updFieldsValue = new Object[]{newValue};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyVariableValues.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{Integer.valueOf(objectVariablePropInfo[0][0].toString())}, "");
        RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES,
                EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES, updFieldsName), updFieldsValue, sqlWhere, null);
        if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
    }
}
