/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.instrumentsmanagement.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import module.instrumentsmanagement.definition.TblsInstrumentsConfig;
import module.instrumentsmanagement.definition.TblsInstrumentsData;
import module.instrumentsmanagement.definition.TblsInstrumentsData.TablesInstrumentsData;
import module.instrumentsmanagement.definition.TblsInstrumentsDataAudit;
import module.instrumentsmanagement.definition.TblsInstrumentsDataAudit.TablesInstrumentsDataAudit;
import module.instrumentsmanagement.definition.InstrumentsEnums;
import static module.instrumentsmanagement.logic.AppInstrumentsAudit.instrumentsAuditAdd;
import module.instrumentsmanagement.definition.InstrumentsEnums.InstrEventsErrorTrapping;
import module.instrumentsmanagement.definition.InstrumentsEnums.InstrumentsAPIactionsEndpoints;
import module.instrumentsmanagement.definition.InstrumentsEnums.InstrumentsBusinessRules;
import module.instrumentsmanagement.definition.InstrumentsEnums.InstrumentsErrorTrapping;
import functionaljavaa.modulegenoma.DataStudyObjectsVariableValues;
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
    ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
    String appProcInstance=LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName());

    Object[][] variableSetInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.CONFIG.getName()), TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES_SET.getTableName(), 
        new String[]{TblsInstrumentsConfig.VariablesSet.NAME.getName()}, new Object[]{variableSetName}, 
        new String[]{TblsInstrumentsConfig.VariablesSet.VARIABLES_LIST.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variableSetInfo[0][0].toString())) {
        return variableSetInfo;
    }
    String variableSetContent = LPNulls.replaceNull(variableSetInfo[0][0]).toString();
    String[] fieldsToRetrieve=new String[]{TblsInstrumentsConfig.Variables.PARAM_NAME.getName(), TblsInstrumentsConfig.Variables.PARAM_TYPE.getName(), TblsInstrumentsConfig.Variables.REQUIRED.getName(), 
        TblsInstrumentsConfig.Variables.ALLOWED_VALUES.getName()};
    Object[][] variablesProperties2D= Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.CONFIG.getName()), TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES.getTableName(), 
        new String[]{TblsInstrumentsConfig.Variables.PARAM_NAME.getName()+" IN"}, new Object[]{variableSetContent}, 
         fieldsToRetrieve);
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variablesProperties2D[0][0].toString())) {
        return variablesProperties2D;
    }
    Object[] variablesProperties1D=LPArray.array2dTo1d(variablesProperties2D);
    variablesProperties1D=LPArray.addValueToArray1D(fieldsToRetrieve, variablesProperties1D);
    return LPArray.array1dTo2d(variablesProperties1D, fieldsToRetrieve.length);
}
    
public static Object[] isEventOpenToChanges(Integer insEventId){
    ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
    String appProcInstance=LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName());
        Object[][] eventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
            new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()}, 
            new Object[]{insEventId}, 
            new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_BY.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventInfo[0][0].toString()))
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_FOUND, new Object[]{insEventId, appProcInstance});
    if (LPNulls.replaceNull(eventInfo[0][0]).toString().length()>0)
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES, new Object[]{insEventId, appProcInstance});
    return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "<*1*> is open to changes in procedure <*2*>", new Object[]{insEventId, appProcInstance});
}
    
    public static Object[] addVariableSetToObject(String instrName, Integer instrEventId, String variableSetName, String ownerId){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        String appProcInstance=LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName());
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
                String[] fieldsName=new String[]{TblsInstrumentsData.InstrEventVariableValues.INSTRUMENT.getName(), TblsInstrumentsData.InstrEventVariableValues.EVENT_ID.getName(), TblsInstrumentsData.InstrEventVariableValues.OWNER_ID.getName(),
                    TblsInstrumentsData.InstrEventVariableValues.VARIABLE_SET.getName()};
                fieldsName=LPArray.addValueToArray1D(fieldsName, fieldHeaders);
                Object[] fieldsValue=new Object[]{instrName, instrEventId, ownerId, variableSetName};
                fieldsValue=LPArray.addValueToArray1D(fieldsValue, fieldVarProperties);
                RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES, fieldsName, fieldsValue);            
                    if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())){
                    instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.START_CALIBRATION, instrName, TablesInstrumentsData.INSTRUMENTS.getTableName(), instrEventId.toString(),
                            fieldsName, fieldsValue);
                    }
            }
        }        
        return diagn;
    }
    public static InternalMessage objectVariableSetValue(String instrName, Integer instrEventId, String variableName, String newValue){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        String appProcInstance=LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName());
        Object[] isStudyOpenToChanges=isEventOpenToChanges(instrEventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())) 
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES, null,null);
        
        String[] fieldsToRetrieve=new String[]{TblsInstrumentsData.InstrEventVariableValues.ID.getName(), 
            TblsInstrumentsData.InstrEventVariableValues.PARAM_NAME.getName(), TblsInstrumentsData.InstrEventVariableValues.PARAM_TYPE.getName(), 
            TblsInstrumentsData.InstrEventVariableValues.REQUIRED.getName(), 
            TblsInstrumentsData.InstrEventVariableValues.ALLOWED_VALUES.getName(), TblsInstrumentsData.InstrEventVariableValues.VALUE.getName()};
        
        String[] fieldsName=new String[]{TblsInstrumentsData.InstrEventVariableValues.EVENT_ID.getName(),
            TblsInstrumentsData.InstrEventVariableValues.PARAM_NAME.getName()};
        Object[] fieldsValue=new Object[]{instrEventId, variableName};
        Object[][] objectVariablePropInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES.getTableName(),
                fieldsName, fieldsValue, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectVariablePropInfo[0][0].toString())){
            Object[][] instEvVariables=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES.getTableName(),
                    new String[]{TblsInstrumentsData.InstrEventVariableValues.EVENT_ID.getName()}, new Object[]{instrEventId}, fieldsToRetrieve);            
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
        String[] updFieldsName=new String[]{TblsInstrumentsData.InstrEventVariableValues.VALUE.getName()};
        Object[] updFieldsValue=new Object[]{newValue};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInstrumentsData.InstrEventVariableValues.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{Integer.valueOf(objectVariablePropInfo[0][0].toString())}, "");
	Object[] diagnostic=Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES,
		EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES, updFieldsName), updFieldsValue, sqlWhere, null);
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) 
            instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.VALUE_ENTERED, instrName, TablesInstrumentsData.INSTRUMENTS.getTableName(), instrEventId.toString(),
                updFieldsName, updFieldsValue);
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.ENTER_EVENT_RESULT, new Object[]{instrName, instrEventId, variableName, newValue}, null);        
    }

    public static InternalMessage objectVariableChangeValue(String instrName, Integer instrEventId, String variableName, String newValue){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        String appProcInstance=LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName());
        Object[] diagn=new Object[0];
        Object[] isStudyOpenToChanges=isEventOpenToChanges(instrEventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())) 
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES, null,null);
        
        String[] fieldsToRetrieve=new String[]{TblsInstrumentsData.InstrEventVariableValues.ID.getName(), TblsInstrumentsData.InstrEventVariableValues.PARAM_NAME.getName(), TblsInstrumentsData.InstrEventVariableValues.PARAM_TYPE.getName(), TblsInstrumentsData.InstrEventVariableValues.REQUIRED.getName(), 
            TblsInstrumentsData.InstrEventVariableValues.ALLOWED_VALUES.getName(), TblsInstrumentsData.InstrEventVariableValues.VALUE.getName()};
        
        String[] fieldsName=new String[]{TblsInstrumentsData.InstrEventVariableValues.EVENT_ID.getName(),
            TblsInstrumentsData.InstrEventVariableValues.PARAM_NAME.getName()};
        Object[] fieldsValue=new Object[]{instrEventId, variableName};
        Object[][] objectVariablePropInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES.getTableName(),
                fieldsName, fieldsValue, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectVariablePropInfo[0][0].toString())){
            Object[][] instEvVariables=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES.getTableName(),
                    new String[]{TblsInstrumentsData.InstrEventVariableValues.EVENT_ID.getName()}, new Object[]{instrEventId}, fieldsToRetrieve);            
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
        if (currentValue.equalsIgnoreCase(newValue)){
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
        String[] updFieldsName=new String[]{TblsInstrumentsData.InstrEventVariableValues.VALUE.getName()};
        Object[] updFieldsValue=new Object[]{newValue};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInstrumentsData.InstrEventVariableValues.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{Integer.valueOf(objectVariablePropInfo[0][0].toString())}, "");
	Object[] diagnostic=Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES,
		EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES, updFieldsName), updFieldsValue, sqlWhere, null);
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) 
            instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.VALUE_REENTERED, instrName, TablesInstrumentsData.INSTRUMENTS.getTableName(), instrEventId.toString(),
                updFieldsName, updFieldsValue);
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.ENTER_EVENT_RESULT, new Object[]{instrName, instrEventId, variableName, newValue}, null);        
    }
    public static InternalMessage eventHasNotEnteredVariables(String instrName, Integer instrEventId){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        String appProcInstance=LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName());
        Object[] isStudyOpenToChanges=isEventOpenToChanges(instrEventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())){ 
            ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
            messages.addMainForError(InstrEventsErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES, new Object[]{instrEventId});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES, new Object[]{instrEventId},null);
        }
        
        Object[][] diagn = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES.getTableName(), 
            new String[]{TblsInstrumentsData.InstrEventVariableValues.INSTRUMENT.getName(), 
                TblsInstrumentsData.InstrEventVariableValues.EVENT_ID.getName(), TblsInstrumentsData.InstrEventVariableValues.REQUIRED.getName(), TblsInstrumentsData.InstrEventVariableValues.VALUE.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
            new Object[]{instrName, instrEventId, "Y"}, new String[]{TblsInstrumentsData.InstrEventVariableValues.ID.getName()});            
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
        Object[][] auditInfo=QueryUtilitiesEnums.getTableData(TblsInstrumentsDataAudit.TablesInstrumentsDataAudit.INSTRUMENTS, 
            EnumIntTableFields.getTableFieldsFromString(TblsInstrumentsDataAudit.TablesInstrumentsDataAudit.INSTRUMENTS, new String[]{TblsInstrumentsDataAudit.Instruments.PERSON.getName(), TblsInstrumentsDataAudit.Instruments.REVIEWED.getName()}),
            new String[]{TblsInstrumentsDataAudit.Instruments.AUDIT_ID.getName()}, new Object[]{auditId}, 
            new String[]{TblsInstrumentsDataAudit.Instruments.AUDIT_ID.getName()});
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
        String[] updFieldsName = new String[]{TblsInstrumentsDataAudit.Instruments.REVIEWED.getName(), TblsInstrumentsDataAudit.Instruments.REVIEWED_BY.getName(), TblsInstrumentsDataAudit.Instruments.REVIEWED_ON.getName()};
        Object[] updFieldsValue = new Object[]{true, personName, LPDate.getCurrentTimeStamp()};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInstrumentsDataAudit.Instruments.AUDIT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{auditId}, "");
	Object[] updateRecordFieldsByFilter=Rdbms.updateRecordFieldsByFilter(TablesInstrumentsDataAudit.INSTRUMENTS,
		EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsDataAudit.INSTRUMENTS, updFieldsName), updFieldsValue, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(updateRecordFieldsByFilter[0].toString()))
            return new InternalMessage(updateRecordFieldsByFilter[0].toString(), InstrumentsAPIactionsEndpoints.INSTRUMENTAUDIT_SET_AUDIT_ID_REVIEWED, new Object[]{auditId});
        else
            return new InternalMessage(updateRecordFieldsByFilter[0].toString(), InstrumentsErrorTrapping.AUDIT_RECORD_NOT_FOUND, new Object[]{auditId});
    }    
    
}
