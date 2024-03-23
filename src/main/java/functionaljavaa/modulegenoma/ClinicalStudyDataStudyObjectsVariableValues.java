package functionaljavaa.modulegenoma;

import module.clinicalstudies.apis.GenomaStudyAPI;
import module.clinicalstudies.definition.TblsGenomaConfig;
import module.clinicalstudies.definition.TblsGenomaData;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import static functionaljavaa.modulegenoma.ClinicalStudyConfigVariablesQueries.getVariableProperties;
import static functionaljavaa.modulegenoma.ClinicalStudyConfigVariablesQueries.getVariableSetVariablesProperties;
import static functionaljavaa.modulegenoma.ClinicalStudyDataStudy.isStudyOpenToChanges;
import functionaljavaa.modulegenoma.ClinicalStudyEnums.GenomaErrorTrapping;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import static lbplanet.utilities.LPMath.isNumeric;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.instrumentsmanagement.definition.InstrumentsEnums;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ResponseMessages;

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
            case "study_family":
                fields = LPArray.addValueToArray1D(fields, TblsGenomaData.StudyVariableValues.FAMILY.getName());
                fields = LPArray.addValueToArray1D(fields, ownerId);
                break;
            case "study_cohort":
                fields = LPArray.addValueToArray1D(fields, TblsGenomaData.StudyVariableValues.COHORT.getName());
                fields = LPArray.addValueToArray1D(fields, ownerId);
                break;
            case "study_samples_set":
                fields = LPArray.addValueToArray1D(fields, TblsGenomaData.StudyVariableValues.SAMPLES_SET.getName());
                fields = LPArray.addValueToArray1D(fields, ownerId);
                break;
            case "study_individual":
                fields = LPArray.addValueToArray1D(fields, TblsGenomaData.StudyVariableValues.INDIVIDUAL.getName());
                fields = LPArray.addValueToArray1D(fields, Integer.valueOf(ownerId));
                break;
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
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        InternalMessage studyOpenToChanges = ClinicalStudyDataStudy.isStudyOpenToChanges(studyName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyOpenToChanges.getDiagnostic())) {
            return studyOpenToChanges;
        }
        Object[][] variableSetContent = getVariableSetVariablesProperties(variableSetName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(variableSetContent[0]))) {
            messages.addMainForError(ClinicalStudyEnums.GenomaErrorTrapping.VARIABLES_SET_NOT_FOUND, new Object[]{variableSetName, instanceForActions.getProcedureInstance()});
            return new InternalMessage(LPPlatform.LAB_FALSE, ClinicalStudyEnums.GenomaErrorTrapping.VARIABLES_SET_NOT_FOUND, null);
        }        
        RdbmsObject insertRecordInTable = null;
        String[] extraFieldsName = new String[]{TblsGenomaData.StudyVariableValues.VARIABLE_SET.getName()};            
        Object[] extraFieldsValue = new Object[]{variableSetName};
        InternalMessage addVariableToObject = null;
        for (int curVariable = 1; curVariable < variableSetContent.length; curVariable++) {
            if ("LIST".equalsIgnoreCase(variableSetContent[curVariable][LPArray.valuePosicInArray(variableSetContent[0],TblsGenomaData.StudyVariableValues.PARAM_TYPE.getName())].toString())){
                extraFieldsName=LPArray.addValueToArray1D(extraFieldsName, TblsGenomaData.StudyVariableValues.ALLOWED_VALUES.getName());
                extraFieldsValue=LPArray.addValueToArray1D(extraFieldsValue, variableSetContent[curVariable][LPArray.valuePosicInArray(variableSetContent[0],TblsGenomaData.StudyVariableValues.ALLOWED_VALUES.getName())].toString());    
            }
            String variableName=variableSetContent[curVariable][LPArray.valuePosicInArray(variableSetContent[0],TblsGenomaData.StudyVariableValues.NAME.getName())].toString();
            addVariableToObject = addVariableToObject(endPoint, studyName, variableName, ownerTable, ownerId, extraFieldsName, extraFieldsValue);
        }    
        return addVariableToObject; //new InternalMessage(LPPlatform.LAB_TRUE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), variableSetName);
/*            String[] fieldHeaders = new String[0];
            for (Object currVar1 : variableSetContent[0]) {
                fieldHeaders = LPArray.addValueToArray1D(fieldHeaders, currVar1.toString());
            }
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
                messages.addMinorForSuccess(endPoint, new Object[]{studyName});
                ClinicalStudyDataAudit.studyAuditAdd(endPoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES, Arrays.toString(variableSetContent[i]),
                        studyName, null, fieldsName, fieldsValue);
            } else {
                messages.addMainForError(insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());
                return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
            }
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), variableSetName);
*/
    }

    public static InternalMessage addVariableToObject(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endPoint, String studyName, String variableName, String ownerTable, String ownerId, String[] extraFieldNames, Object[] extraFieldValues) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        InternalMessage studyOpenToChanges = ClinicalStudyDataStudy.isStudyOpenToChanges(studyName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyOpenToChanges.getDiagnostic())) {
            return studyOpenToChanges;
        }

        Object[] existsRecord = Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsGenomaConfig.TablesGenomaConfig.VARIABLES.getRepositoryName()), TblsGenomaConfig.TablesGenomaConfig.VARIABLES.getTableName(),
                new String[]{TblsGenomaConfig.Variables.NAME.getName()}, new Object[]{variableName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsRecord[0].toString())) {
            messages.addMainForError(InstrumentsEnums.InstrEventsErrorTrapping.VARIABLE_NOT_EXISTS, new Object[]{variableName, procInstanceName});
            return new InternalMessage(LPPlatform.LAB_FALSE, ClinicalStudyEnums.GenomaErrorTrapping.VARIABLE_NOT_FOUND, null);
        }
        String[] fieldsName = new String[]{TblsGenomaData.StudyVariableValues.STUDY.getName(), TblsGenomaData.StudyVariableValues.OWNER_TABLE.getName(), TblsGenomaData.StudyVariableValues.OWNER_ID.getName(),
            };
        Object[] fieldsValue = new Object[]{studyName, ownerTable, ownerId};
        Object[][] variableInfo = getVariableProperties(variableName);
        for (int iVarFlds=0;iVarFlds<variableInfo[0].length;iVarFlds++){
            String curFldName=variableInfo[0][iVarFlds].toString();
            if (Boolean.FALSE.equals(TblsGenomaConfig.Variables.ALLOWED_VALUES.getName().equalsIgnoreCase(curFldName))||
                    (Boolean.TRUE.equals(TblsGenomaConfig.Variables.ALLOWED_VALUES.getName().equalsIgnoreCase(curFldName))&&
                    Boolean.TRUE.equals("LIST".equalsIgnoreCase(variableInfo[1][LPArray.valuePosicInArray(variableInfo[0],TblsGenomaConfig.Variables.PARAM_TYPE.getName())].toString()))                    
                    )
                ){
                fieldsName = LPArray.addValueToArray1D(fieldsName, curFldName);
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, variableInfo[1][iVarFlds]);
            }            
        }
        Object[][] objectFieldExtraFields = objectFieldExtraFields(studyName, variableName, ownerTable, ownerId);
        if (objectFieldExtraFields != null && objectFieldExtraFields.length > 0) {
            for (Object[] curFld : objectFieldExtraFields) {
                if (Boolean.FALSE.equals(LPArray.valueInArray(fieldsName, curFld[0].toString()))){
                    fieldsName = LPArray.addValueToArray1D(fieldsName, curFld[0].toString());
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, curFld[1]);
                }
            }
        }        
        if (extraFieldNames != null && extraFieldNames.length > 0) {
            for (int iVarFlds=0;iVarFlds<extraFieldNames.length;iVarFlds++){
                if (Boolean.FALSE.equals(LPArray.valueInArray(fieldsName, extraFieldNames[iVarFlds].toString()))){
                    fieldsName = LPArray.addValueToArray1D(fieldsName, extraFieldNames[iVarFlds].toString());
                    fieldsValue = LPArray.addValueToArray1D(fieldsValue, extraFieldValues[iVarFlds]);
                }            
            }
        }
        if (Boolean.FALSE.equals(LPArray.valueInArray(fieldsName, TblsGenomaConfig.Variables.NAME.getName()))){
            fieldsName = LPArray.addValueToArray1D(fieldsName, TblsGenomaConfig.Variables.NAME.getName());
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, variableName);
        }
        existsRecord = Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES.getRepositoryName()), TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES.getTableName(),
            new String[]{TblsGenomaData.StudyVariableValues.STUDY.getName(), TblsGenomaData.StudyVariableValues.OWNER_TABLE.getName(), TblsGenomaData.StudyVariableValues.OWNER_ID.getName(), TblsGenomaData.StudyVariableValues.NAME.getName()}, 
            new Object[]{studyName, ownerTable, ownerId, variableName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())) {
            messages.addMainForError(GenomaErrorTrapping.VARIABLE_ALREADY_EXISTS, new Object[]{variableName, ownerId, ownerTable, studyName});
            return new InternalMessage(LPPlatform.LAB_FALSE, GenomaErrorTrapping.VARIABLE_ALREADY_EXISTS, new Object[]{variableName, ownerId, ownerTable, studyName});
        }        
        fieldsName =  LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyVariableValues.CREATED_ON.getName());
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, LPDate.getCurrentTimeStamp());
        fieldsName = LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyVariableValues.CREATED_BY.getName());
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, instanceForActions.getToken().getPersonName());
        fieldsName = LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyVariableValues.ACTIVE.getName());
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, true);

        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES, fieldsName, fieldsValue);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
            ClinicalStudyDataAudit.studyAuditAdd(endPoint.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES, variableName,
                    studyName, null, fieldsName, fieldsValue);
            messages.addMinorForSuccess(endPoint, new Object[]{studyName});
            return new InternalMessage(LPPlatform.LAB_TRUE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), insertRecordInTable.getNewRowId());
        }
        messages.addMainForError(insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());
        return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
    }

    public static InternalMessage objectVariableSetValue(GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endpoint, String studyName, String ownerTable, String ownerId, String variableSetName, String variableName, String newValue) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();    
        String procInstanceName = instanceForActions.getProcedureInstance();
        InternalMessage projOpenToChanges=isStudyOpenToChanges(studyName);    
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projOpenToChanges.getDiagnostic())){
            messages.addMainForError(projOpenToChanges.getMessageCodeObj(), projOpenToChanges.getMessageCodeVariables());
            return projOpenToChanges;
        }

        String[] fieldsToRetrieve = new String[]{TblsGenomaData.StudyVariableValues.ID.getName(), TblsGenomaData.StudyVariableValues.NAME.getName(), TblsGenomaData.StudyVariableValues.PARAM_TYPE.getName(), TblsGenomaData.StudyVariableValues.REQUIRED.getName(),
            TblsGenomaData.StudyVariableValues.ALLOWED_VALUES.getName()};

        String[] fieldsName = new String[]{TblsGenomaData.StudyVariableValues.STUDY.getName(), TblsGenomaData.StudyVariableValues.OWNER_TABLE.getName(), TblsGenomaData.StudyVariableValues.OWNER_ID.getName(),
            TblsGenomaData.StudyVariableValues.NAME.getName()};
        Object[] fieldsValue = new Object[]{studyName, ownerTable, ownerId, variableName};
        if (variableSetName!=null){
            fieldsName=LPArray.addValueToArray1D(fieldsName, TblsGenomaData.StudyVariableValues.VARIABLE_SET.getName());
            fieldsValue=LPArray.addValueToArray1D(fieldsValue, variableSetName);
        }
        Object[][] objectVariablePropInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES.getTableName(),
                fieldsName, fieldsValue, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectVariablePropInfo[0][0].toString())) {
            messages.addMainForError(InstrumentsEnums.InstrEventsErrorTrapping.VARIABLE_NOT_EXISTS, new Object[]{variableSetName, variableName, procInstanceName});
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
                    messages.addMainForError(InstrumentsEnums.InstrEventsErrorTrapping.VARIABLE_VALUE_NOTONEOFTHEEXPECTED,
                            new Object[]{newValue, Arrays.toString(allowedValuesArr), variableName, procInstanceName});
                    return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrEventsErrorTrapping.VARIABLE_VALUE_NOTONEOFTHEEXPECTED,
                            new Object[]{newValue, Arrays.toString(allowedValuesArr), variableName, procInstanceName});
                }
                break;
            case REAL:
            case INTEGER:
                Object[] isNumeric = isNumeric(newValue);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric[0].toString())) {
                    messages.addMainForError(InstrumentsEnums.InstrEventsErrorTrapping.NOT_NUMERIC_VALUE, null);
                    return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrEventsErrorTrapping.NOT_NUMERIC_VALUE, null, null);
                }
                break;
            case TEXT:
                break;
            default:
                messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.VARIABLE_TYPE_NOT_RECOGNIZED, new Object[]{fieldType});
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.VARIABLE_TYPE_NOT_RECOGNIZED, new Object[]{fieldType}, null);
        }
        String[] updFieldsName = new String[]{TblsGenomaData.StudyVariableValues.VALUE.getName()};
        Object[] updFieldsValue = new Object[]{newValue};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyVariableValues.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{Integer.valueOf(objectVariablePropInfo[0][0].toString())}, "");
        RdbmsObject diagnosesProj = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES,
                EnumIntTableFields.getTableFieldsFromString(TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES, updFieldsName), updFieldsValue, sqlWhere, null);
        if (Boolean.FALSE.equals(diagnosesProj.getRunSuccess())) {
            messages.addMainForError(diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnosesProj.getErrorMessageCode(), diagnosesProj.getErrorMessageVariables());
        }
        messages.addMinorForSuccess(endpoint, new Object[]{studyName});
        return new InternalMessage(LPPlatform.LAB_TRUE, endpoint, new Object[]{studyName});
    }
}
