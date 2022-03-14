/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.instruments;

import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsAppProcConfig;
import databases.TblsAppProcData;
import databases.TblsAppProcData.TablesAppProcData;
import databases.TblsAppProcDataAudit;
import static functionaljavaa.audit.AppInstrumentsAudit.instrumentsAuditAdd;
import functionaljavaa.instruments.InstrumentsEnums.InstrEventsErrorTrapping;
import functionaljavaa.instruments.InstrumentsEnums.InstrumentsAPIactionsEndpoints;
import functionaljavaa.instruments.InstrumentsEnums.InstrumentsBusinessRules;
import functionaljavaa.instruments.InstrumentsEnums.InstrumentsErrorTrapping;
import functionaljavaa.moduleenvironmentalmonitoring.DataStudyObjectsVariableValues;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.isTagValueOneOfEnableOnes;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import static lbplanet.utilities.LPMath.isNumeric;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.session.ResponseMessages;

/**
 *
 * @author User
 */
public class DataInstrumentsEvents {

public static Object[][] getVariableSetVariablesProperties(String variableSetName){
    String appProcInstance=GlobalVariables.Schemas.APP_PROC_CONFIG.getName();

    Object[][] variableSetInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.CONFIG.getName()), TblsAppProcConfig.TablesAppProcConfig.VARIABLES_SET.getTableName(), 
        new String[]{TblsAppProcConfig.VariablesSet.NAME.getName()}, new Object[]{variableSetName}, 
        new String[]{TblsAppProcConfig.VariablesSet.VARIABLES_LIST.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variableSetInfo[0][0].toString())) {
        return variableSetInfo;
    }
    String variableSetContent = LPNulls.replaceNull(variableSetInfo[0][0]).toString();
    String[] fieldsToRetrieve=new String[]{TblsAppProcConfig.Variables.PARAM_NAME.getName(), TblsAppProcConfig.Variables.PARAM_TYPE.getName(), TblsAppProcConfig.Variables.REQUIRED.getName(), 
        TblsAppProcConfig.Variables.ALLOWED_VALUES.getName()};
    Object[][] variablesProperties2D= Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.CONFIG.getName()), TblsAppProcConfig.TablesAppProcConfig.VARIABLES.getTableName(), 
        new String[]{TblsAppProcConfig.Variables.PARAM_NAME.getName()+" IN"}, new Object[]{variableSetContent}, 
         fieldsToRetrieve);
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variablesProperties2D[0][0].toString())) {
        return variablesProperties2D;
    }
    Object[] variablesProperties1D=LPArray.array2dTo1d(variablesProperties2D);
    variablesProperties1D=LPArray.addValueToArray1D(fieldsToRetrieve, variablesProperties1D);
    return LPArray.array1dTo2d(variablesProperties1D, fieldsToRetrieve.length);
}
    
public static Object[] isEventOpenToChanges(Integer insEventId){
    String appProcInstance=GlobalVariables.Schemas.APP_PROC_DATA.getName();
        Object[][] eventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENT_EVENT.getTableName(),
            new String[]{TblsAppProcData.InstrumentEvent.ID.getName()}, 
            new Object[]{insEventId}, 
            new String[]{TblsAppProcData.InstrumentEvent.COMPLETED_BY.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventInfo[0][0].toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_FOUND, new Object[]{insEventId, appProcInstance});
    if (LPNulls.replaceNull(eventInfo[0][0]).toString().length()>0)
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES, new Object[]{insEventId, appProcInstance});
    return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "<*1*> is open to changes in procedure <*2*>", new Object[]{insEventId, appProcInstance});
}
    
    public static Object[] addVariableSetToObject(String instrName, Integer instrEventId, String variableSetName, String ownerId){
        String appProcInstance=GlobalVariables.Schemas.APP_PROC_DATA.getName();
        Object[] diagn=new Object[0];
        Object[] isStudyOpenToChanges=isEventOpenToChanges(instrEventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())) return isStudyOpenToChanges;
        
        Object[][] variableSetContent=getVariableSetVariablesProperties(variableSetName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variableSetContent[0][0].toString())) return variableSetContent[0];
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
                String[] fieldsName=new String[]{TblsAppProcData.InstrEventVariableValues.INSTRUMENT.getName(), TblsAppProcData.InstrEventVariableValues.EVENT_ID.getName(), TblsAppProcData.InstrEventVariableValues.OWNER_ID.getName(),
                    TblsAppProcData.InstrEventVariableValues.VARIABLE_SET.getName()};
                fieldsName=LPArray.addValueToArray1D(fieldsName, fieldHeaders);
                Object[] fieldsValue=new Object[]{instrName, instrEventId, ownerId, variableSetName};
                fieldsValue=LPArray.addValueToArray1D(fieldsValue, fieldVarProperties);
/*                Object[][] extraFields=objectFieldExtraFields(insEventId, variableSetName, ownerTable, ownerId);
                if (extraFields!=null && extraFields.length>0){
                    for (Object[] curFld: extraFields){
                        fieldsName=LPArray.addValueToArray1D(fieldsName, curFld[0].toString());
                        fieldsValue=LPArray.addValueToArray1D(fieldsValue, curFld[1]);
                    }
                }*/
                diagn=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTR_EVENT_VARIABLE_VALUES.getTableName(), 
                    fieldsName, fieldsValue);            
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) 
                    instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.START_CALIBRATION, instrName, TablesAppProcData.INSTRUMENTS.getTableName(), instrEventId.toString(),
                        fieldsName, fieldsValue);
            }
        }        
        return diagn; //LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "not implemented yet!", null);
    }
    public static InternalMessage objectVariableSetValue(String instrName, Integer instrEventId, String variableName, String newValue){
        String appProcInstance=GlobalVariables.Schemas.APP_PROC_DATA.getName();
        Object[] diagn=new Object[0];
        Object[] isStudyOpenToChanges=isEventOpenToChanges(instrEventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())) 
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES, null,null);
        
        String[] fieldsToRetrieve=new String[]{TblsAppProcData.InstrEventVariableValues.ID.getName(), 
            TblsAppProcData.InstrEventVariableValues.PARAM_NAME.getName(), TblsAppProcData.InstrEventVariableValues.PARAM_TYPE.getName(), 
            TblsAppProcData.InstrEventVariableValues.REQUIRED.getName(), 
            TblsAppProcData.InstrEventVariableValues.ALLOWED_VALUES.getName(), TblsAppProcData.InstrEventVariableValues.VALUE.getName()};
        
        String[] fieldsName=new String[]{TblsAppProcData.InstrEventVariableValues.EVENT_ID.getName(),
            TblsAppProcData.InstrEventVariableValues.PARAM_NAME.getName()};
        Object[] fieldsValue=new Object[]{instrEventId, variableName};
        Object[][] objectVariablePropInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTR_EVENT_VARIABLE_VALUES.getTableName(),
                fieldsName, fieldsValue, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectVariablePropInfo[0][0].toString())){
            Object[][] instEvVariables=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTR_EVENT_VARIABLE_VALUES.getTableName(),
                    new String[]{TblsAppProcData.InstrEventVariableValues.EVENT_ID.getName()}, new Object[]{instrEventId}, fieldsToRetrieve);            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instEvVariables[0][0].toString()))
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.VARIABLE_NOT_EXISTS_EVENT_WITHNOVARIABLES, null);
            else{
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.VARIABLE_NOT_EXISTS, 
                new Object[]{Arrays.toString(LPArray.getColumnFromArray2D(instEvVariables, 1))});
            }
        }
        if (objectVariablePropInfo.length!=1) return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.MORE_THAN_ONE_VARIABLE, 
            new Object[]{objectVariablePropInfo.length, Arrays.toString(fieldsName), appProcInstance});
        String currentValue = LPNulls.replaceNull(objectVariablePropInfo[0][5]).toString();
        if (currentValue.length()>0){
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.USE_REENTER_WHEN_PARAM_ALREADY_HAS_VALUE, 
            new Object[]{});            
        }
        String fieldType = objectVariablePropInfo[0][2].toString();        
        if (DataStudyObjectsVariableValues.VariableTypes.LIST.toString().equalsIgnoreCase(fieldType)){
            String[] allowedValuesArr = LPNulls.replaceNull(objectVariablePropInfo[0][4]).toString().split("\\|");
            if (!LPArray.valueInArray(allowedValuesArr, newValue)) 
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.MORE_THAN_ONE_VARIABLE, 
                    new Object[]{newValue, Arrays.toString(allowedValuesArr), variableName, appProcInstance});
        }else if (DataStudyObjectsVariableValues.VariableTypes.REAL.toString().equalsIgnoreCase(fieldType)){
            Object[] isNumeric = isNumeric(newValue);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric[0].toString())) 
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.NOT_NUMERIC_VALUE,null, null);
        }else if (DataStudyObjectsVariableValues.VariableTypes.INTEGER.toString().equalsIgnoreCase(fieldType)){
            Object[] isNumeric = isNumeric(newValue);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric[0].toString())) 
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.NOT_NUMERIC_VALUE,null, null);
        }else if (DataStudyObjectsVariableValues.VariableTypes.TEXT.toString().equalsIgnoreCase(fieldType)){
        }else 
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.VARIABLE_TYPE_NOT_RECOGNIZED, new Object[]{fieldType}, null);
        String[] updFieldsName=new String[]{TblsAppProcData.InstrEventVariableValues.VALUE.getName()};
        Object[] updFieldsValue=new Object[]{newValue};
        diagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTR_EVENT_VARIABLE_VALUES.getTableName(), 
            updFieldsName, updFieldsValue, new String[]{TblsAppProcData.InstrEventVariableValues.ID.getName()}, new Object[]{Integer.valueOf(objectVariablePropInfo[0][0].toString())});            
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) 
            instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.VALUE_ENTERED, instrName, TablesAppProcData.INSTRUMENTS.getTableName(), instrEventId.toString(),
                updFieldsName, updFieldsValue);
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.ENTER_EVENT_RESULT, new Object[]{instrName, instrEventId, variableName, newValue}, null);        
    }

    public static InternalMessage objectVariableChangeValue(String instrName, Integer instrEventId, String variableName, String newValue){
        String appProcInstance=GlobalVariables.Schemas.APP_PROC_DATA.getName();
        Object[] diagn=new Object[0];
        Object[] isStudyOpenToChanges=isEventOpenToChanges(instrEventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())) 
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES, null,null);
        
        String[] fieldsToRetrieve=new String[]{TblsAppProcData.InstrEventVariableValues.ID.getName(), TblsAppProcData.InstrEventVariableValues.PARAM_NAME.getName(), TblsAppProcData.InstrEventVariableValues.PARAM_TYPE.getName(), TblsAppProcData.InstrEventVariableValues.REQUIRED.getName(), 
            TblsAppProcData.InstrEventVariableValues.ALLOWED_VALUES.getName(), TblsAppProcData.InstrEventVariableValues.VALUE.getName()};
        
        String[] fieldsName=new String[]{TblsAppProcData.InstrEventVariableValues.EVENT_ID.getName(),
            TblsAppProcData.InstrEventVariableValues.PARAM_NAME.getName()};
        Object[] fieldsValue=new Object[]{instrEventId, variableName};
        Object[][] objectVariablePropInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTR_EVENT_VARIABLE_VALUES.getTableName(),
                fieldsName, fieldsValue, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectVariablePropInfo[0][0].toString())){
            Object[][] instEvVariables=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTR_EVENT_VARIABLE_VALUES.getTableName(),
                    new String[]{TblsAppProcData.InstrEventVariableValues.EVENT_ID.getName()}, new Object[]{instrEventId}, fieldsToRetrieve);            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instEvVariables[0][0].toString()))
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.VARIABLE_NOT_EXISTS_EVENT_WITHNOVARIABLES, null);
            else{
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.VARIABLE_NOT_EXISTS, 
                new Object[]{Arrays.toString(LPArray.getColumnFromArray2D(instEvVariables, 1))});
            }
        }
        if (objectVariablePropInfo.length!=1) return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.MORE_THAN_ONE_VARIABLE, 
            new Object[]{objectVariablePropInfo.length, Arrays.toString(fieldsName), appProcInstance});
        String currentValue = LPNulls.replaceNull(objectVariablePropInfo[0][5]).toString();
        if (currentValue.length()==0){
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.USE_ENTER_WHEN_PARAM_HAS_NO_VALUE, 
            new Object[]{});            
        }
        if (currentValue.toString().equalsIgnoreCase(newValue.toString())){
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.SAME_RESULT_VALUE, 
            new Object[]{variableName, appProcInstance, newValue});                        
        }
        String fieldType = objectVariablePropInfo[0][2].toString();        
        if (DataStudyObjectsVariableValues.VariableTypes.LIST.toString().equalsIgnoreCase(fieldType)){
            String[] allowedValuesArr = LPNulls.replaceNull(objectVariablePropInfo[0][4]).toString().split("\\|");
            if (!LPArray.valueInArray(allowedValuesArr, newValue)) 
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.MORE_THAN_ONE_VARIABLE, 
                    new Object[]{newValue, Arrays.toString(allowedValuesArr), variableName, appProcInstance});
        }else if (DataStudyObjectsVariableValues.VariableTypes.REAL.toString().equalsIgnoreCase(fieldType)){
            Object[] isNumeric = isNumeric(newValue);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric[0].toString())) 
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.NOT_NUMERIC_VALUE,null, null);
        }else if (DataStudyObjectsVariableValues.VariableTypes.INTEGER.toString().equalsIgnoreCase(fieldType)){
            Object[] isNumeric = isNumeric(newValue);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric[0].toString())) 
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.NOT_NUMERIC_VALUE,null, null);
        }else if (DataStudyObjectsVariableValues.VariableTypes.TEXT.toString().equalsIgnoreCase(fieldType)){
        }else 
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.VARIABLE_TYPE_NOT_RECOGNIZED, new Object[]{fieldType}, null);
        String[] updFieldsName=new String[]{TblsAppProcData.InstrEventVariableValues.VALUE.getName()};
        Object[] updFieldsValue=new Object[]{newValue};
        diagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTR_EVENT_VARIABLE_VALUES.getTableName(), 
            updFieldsName, updFieldsValue, new String[]{TblsAppProcData.InstrEventVariableValues.ID.getName()}, new Object[]{Integer.valueOf(objectVariablePropInfo[0][0].toString())});            
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) 
            instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.VALUE_REENTERED, instrName, TablesAppProcData.INSTRUMENTS.getTableName(), instrEventId.toString(),
                updFieldsName, updFieldsValue);
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.ENTER_EVENT_RESULT, new Object[]{instrName, instrEventId, variableName, newValue}, null);        
    }
    public static InternalMessage eventHasNotEnteredVariables(String instrName, Integer instrEventId){
        String appProcInstance=GlobalVariables.Schemas.APP_PROC_DATA.getName();
        Object[] isStudyOpenToChanges=isEventOpenToChanges(instrEventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())){ 
            ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
            messages.addMainForError(InstrEventsErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES, new Object[]{instrEventId});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES, new Object[]{instrEventId},null);
        }
        
        Object[][] diagn = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTR_EVENT_VARIABLE_VALUES.getTableName(), 
            new String[]{TblsAppProcData.InstrEventVariableValues.INSTRUMENT.getName(), 
                TblsAppProcData.InstrEventVariableValues.EVENT_ID.getName(), TblsAppProcData.InstrEventVariableValues.REQUIRED.getName(), TblsAppProcData.InstrEventVariableValues.VALUE.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
            new Object[]{instrName, instrEventId, "Y"}, new String[]{TblsAppProcData.InstrEventVariableValues.ID.getName()});            
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0][0].toString())) 
            return new InternalMessage(LPPlatform.LAB_TRUE, InstrEventsErrorTrapping.EVENT_NOTHING_PENDING, null,null);
        else{
            ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
            messages.addMainForError(InstrEventsErrorTrapping.EVENT_HAS_PENDING_RESULTS, new Object[]{diagn.length});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.EVENT_HAS_PENDING_RESULTS, new Object[]{diagn.length},null);
        }        
    }
    public static InternalMessage instrumentAuditSetAuditRecordAsReviewed(Integer auditId, String personName){
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
        String appProcInstance=GlobalVariables.Schemas.APP_PROC_DATA_AUDIT.getName();
        String auditReviewMode = Parameter.getBusinessRuleProcedureFile(appProcInstance, InstrumentsBusinessRules.REVISION_MODE.getAreaName(), InstrumentsBusinessRules.REVISION_MODE.getTagName());  
        if (!isTagValueOneOfEnableOnes(auditReviewMode)){
                messages.addMainForError(InstrumentsErrorTrapping.DISABLED, new Object[]{});
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.DISABLED, new Object[]{});                        
        }
        String auditAuthorCanBeReviewerMode = Parameter.getBusinessRuleProcedureFile(appProcInstance, InstrumentsBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getAreaName(), InstrumentsBusinessRules.AUTHOR_CAN_REVIEW_AUDIT_TOO.getTagName());  
        Object[][] auditInfo=QueryUtilitiesEnums.getTableData(TblsAppProcDataAudit.TablesAppProcDataAudit.INSTRUMENTS, 
            EnumIntTableFields.getTableFieldsFromString(TblsAppProcDataAudit.TablesAppProcDataAudit.INSTRUMENTS, new String[]{TblsAppProcDataAudit.Instruments.PERSON.getName(), TblsAppProcDataAudit.Instruments.REVIEWED.getName()}),
            new String[]{TblsAppProcDataAudit.Instruments.AUDIT_ID.getName()}, new Object[]{auditId}, 
            new String[]{TblsAppProcDataAudit.Instruments.AUDIT_ID.getName()});
        if (!isTagValueOneOfEnableOnes(auditAuthorCanBeReviewerMode)){//(!"TRUE".equalsIgnoreCase(auditAuthorCanBeReviewerMode)){            
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(auditInfo[0][0].toString())){ 
                messages.addMainForError(InstrumentsErrorTrapping.DISABLED, new Object[]{});
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.DISABLED, new Object[]{});            
            }
            if (personName.equalsIgnoreCase(auditInfo[0][0].toString())){
                messages.addMainForError(InstrumentsErrorTrapping.AUTHOR_CANNOT_BE_REVIEWER, new Object[]{});
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.AUTHOR_CANNOT_BE_REVIEWER, new Object[]{});                
            }
        }
        if (Boolean.valueOf(auditInfo[0][1].toString())){
            messages.addMainForError(InstrumentsErrorTrapping.AUDIT_RECORD_ALREADY_REVIEWED, new Object[]{auditId});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.AUDIT_RECORD_ALREADY_REVIEWED, new Object[]{auditId});
        }
        Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(appProcInstance, TblsAppProcDataAudit.TablesAppProcDataAudit.INSTRUMENTS.getTableName(), 
            new String[]{TblsAppProcDataAudit.Instruments.REVIEWED.getName(), TblsAppProcDataAudit.Instruments.REVIEWED_BY.getName(), TblsAppProcDataAudit.Instruments.REVIEWED_ON.getName()},
            new Object[]{true, personName, LPDate.getCurrentTimeStamp()}, 
            new String[]{TblsAppProcDataAudit.Instruments.AUDIT_ID.getName()}, new Object[]{auditId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(updateRecordFieldsByFilter[0].toString()))
            return new InternalMessage(updateRecordFieldsByFilter[0].toString(), InstrumentsAPIactionsEndpoints.INSTRUMENTAUDIT_SET_AUDIT_ID_REVIEWED, new Object[]{auditId});
        else
            return new InternalMessage(updateRecordFieldsByFilter[0].toString(), InstrumentsErrorTrapping.AUDIT_RECORD_NOT_FOUND, new Object[]{auditId});
    }    
    
}
