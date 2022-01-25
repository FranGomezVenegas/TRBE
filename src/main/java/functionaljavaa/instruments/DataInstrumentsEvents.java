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
import static functionaljavaa.audit.AppInstrumentsAudit.instrumentsAuditAdd;
import functionaljavaa.instruments.InstrumentsEnums.InstrEventsErrorTrapping;
import functionaljavaa.instruments.InstrumentsEnums.InstrumentsErrorTrapping;
import functionaljavaa.moduleenvironmentalmonitoring.DataStudyObjectsVariableValues;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import static lbplanet.utilities.LPMath.isNumeric;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
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

    Object[][] variableSetInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.CONFIG.getName()), TblsAppProcConfig.VariablesSet.TBL.getName(), 
        new String[]{TblsAppProcConfig.VariablesSet.FLD_NAME.getName()}, new Object[]{variableSetName}, 
        new String[]{TblsAppProcConfig.VariablesSet.FLD_VARIABLES_LIST.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variableSetInfo[0][0].toString())) {
        return variableSetInfo;
    }
    String variableSetContent = LPNulls.replaceNull(variableSetInfo[0][0]).toString();
    String[] fieldsToRetrieve=new String[]{TblsAppProcConfig.Variables.FLD_PARAM_NAME.getName(), TblsAppProcConfig.Variables.FLD_PARAM_TYPE.getName(), TblsAppProcConfig.Variables.FLD_REQUIRED.getName(), 
        TblsAppProcConfig.Variables.FLD_ALLOWED_VALUES.getName()};
    Object[][] variablesProperties2D= Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.CONFIG.getName()), TblsAppProcConfig.Variables.TBL.getName(), 
        new String[]{TblsAppProcConfig.Variables.FLD_PARAM_NAME.getName()+" IN"}, new Object[]{variableSetContent}, 
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
        Object[][] eventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TblsAppProcData.InstrumentEvent.TBL.getName(),
            new String[]{TblsAppProcData.InstrumentEvent.FLD_ID.getName()}, 
            new Object[]{insEventId}, 
            new String[]{TblsAppProcData.InstrumentEvent.FLD_COMPLETED_BY.getName()});
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventInfo[0][0].toString()))
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_FOUND.getErrorCode(), new Object[]{insEventId, appProcInstance});
    if (LPNulls.replaceNull(eventInfo[0][0]).toString().length()>0)
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES.getErrorCode(), new Object[]{insEventId, appProcInstance});
    return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "<*1*> is open to changes in procedure <*2*>", new Object[]{insEventId, appProcInstance});
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
                String[] fieldsName=new String[]{TblsAppProcData.InstrEventVariableValues.FLD_INSTRUMENT.getName(), TblsAppProcData.InstrEventVariableValues.FLD_EVENT_ID.getName(), TblsAppProcData.InstrEventVariableValues.FLD_OWNER_ID.getName(),
                    TblsAppProcData.InstrEventVariableValues.FLD_VARIABLE_SET.getName()};
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
                diagn=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TblsAppProcData.InstrEventVariableValues.TBL.getName(), 
                    fieldsName, fieldsValue);            
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) 
                    instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.START_CALIBRATION.toString(), instrName, TblsAppProcData.Instruments.TBL.getName(), instrEventId.toString(),
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
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES.getErrorCode(), null,null);
        
        String[] fieldsToRetrieve=new String[]{TblsAppProcData.InstrEventVariableValues.FLD_ID.getName(), TblsAppProcData.InstrEventVariableValues.FLD_PARAM_NAME.getName(), TblsAppProcData.InstrEventVariableValues.FLD_PARAM_TYPE.getName(), TblsAppProcData.InstrEventVariableValues.FLD_REQUIRED.getName(), 
            TblsAppProcData.InstrEventVariableValues.FLD_ALLOWED_VALUES.getName()};
        
        String[] fieldsName=new String[]{TblsAppProcData.InstrEventVariableValues.FLD_EVENT_ID.getName(),
            TblsAppProcData.InstrEventVariableValues.FLD_PARAM_NAME.getName()};
        Object[] fieldsValue=new Object[]{instrEventId, variableName};
        Object[][] objectVariablePropInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TblsAppProcData.InstrEventVariableValues.TBL.getName(),
                fieldsName, fieldsValue, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objectVariablePropInfo[0][0].toString())){
            Object[][] instEvVariables=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TblsAppProcData.InstrEventVariableValues.TBL.getName(),
                    new String[]{TblsAppProcData.InstrEventVariableValues.FLD_EVENT_ID.getName()}, new Object[]{instrEventId}, fieldsToRetrieve);            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instEvVariables[0][0].toString()))
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.VARIABLE_NOT_EXISTS_EVENT_WITHNOVARIABLES.getErrorCode(), null);
            else{
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.VARIABLE_NOT_EXISTS.getErrorCode(), 
                new Object[]{Arrays.toString(LPArray.getColumnFromArray2D(instEvVariables, 1))});
            }
        }
        if (objectVariablePropInfo.length!=1) return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.MORE_THAN_ONE_VARIABLE.getErrorCode(), 
            new Object[]{objectVariablePropInfo.length, Arrays.toString(fieldsName), appProcInstance});
        
        String fieldType = objectVariablePropInfo[0][2].toString();        
        if (DataStudyObjectsVariableValues.VariableTypes.LIST.toString().equalsIgnoreCase(fieldType)){
            String[] allowedValuesArr = LPNulls.replaceNull(objectVariablePropInfo[0][4]).toString().split("\\|");
            if (!LPArray.valueInArray(allowedValuesArr, newValue)) 
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.MORE_THAN_ONE_VARIABLE.getErrorCode(), 
                    new Object[]{newValue, Arrays.toString(allowedValuesArr), variableName, appProcInstance});
        }else if (DataStudyObjectsVariableValues.VariableTypes.REAL.toString().equalsIgnoreCase(fieldType)){
            Object[] isNumeric = isNumeric(newValue);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric[0].toString())) 
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.NOT_NUMERIC_VALUE.getErrorCode(),null, null);
        }else if (DataStudyObjectsVariableValues.VariableTypes.INTEGER.toString().equalsIgnoreCase(fieldType)){
            Object[] isNumeric = isNumeric(newValue);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric[0].toString())) 
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.NOT_NUMERIC_VALUE.getErrorCode(),null, null);
        }else if (DataStudyObjectsVariableValues.VariableTypes.TEXT.toString().equalsIgnoreCase(fieldType)){
        }else 
            return new InternalMessage(LPPlatform.LAB_FALSE, "not recognized variable type "+fieldType, null, null);
        String[] updFieldsName=new String[]{TblsAppProcData.InstrEventVariableValues.FLD_VALUE.getName()};
        Object[] updFieldsValue=new Object[]{newValue};
        diagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TblsAppProcData.InstrEventVariableValues.TBL.getName(), 
            updFieldsName, updFieldsValue, new String[]{TblsAppProcData.InstrEventVariableValues.FLD_ID.getName()}, new Object[]{Integer.valueOf(objectVariablePropInfo[0][0].toString())});            
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) 
            instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.VALUE_ENTERED.toString(), instrName, TblsAppProcData.Instruments.TBL.getName(), instrEventId.toString(),
                updFieldsName, updFieldsValue);
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.ENTER_EVENT_RESULT.getSuccessMessageCode(), new Object[]{instrName, instrEventId, variableName, newValue}, null);        
    }

    public static InternalMessage eventHasNotEnteredVariables(String instrName, Integer instrEventId){
        String appProcInstance=GlobalVariables.Schemas.APP_PROC_DATA.getName();        
        Object[] isStudyOpenToChanges=isEventOpenToChanges(instrEventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isStudyOpenToChanges[0].toString())){ 
            ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
            messages.addMainForError(InstrEventsErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES.getErrorCode(), new Object[]{instrEventId});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.EVENT_NOT_OPEN_FOR_CHANGES.getErrorCode(), new Object[]{instrEventId},null);
        }
        
        Object[][] diagn = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(appProcInstance, GlobalVariables.Schemas.DATA.getName()), TblsAppProcData.InstrEventVariableValues.TBL.getName(), 
            new String[]{TblsAppProcData.InstrEventVariableValues.FLD_INSTRUMENT.getName(), 
                TblsAppProcData.InstrEventVariableValues.FLD_EVENT_ID.getName(), TblsAppProcData.InstrEventVariableValues.FLD_REQUIRED.getName(), TblsAppProcData.InstrEventVariableValues.FLD_VALUE.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
            new Object[]{instrName, instrEventId, "Y"}, new String[]{TblsAppProcData.InstrEventVariableValues.FLD_ID.getName()});            
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0][0].toString())) 
            return new InternalMessage(LPPlatform.LAB_TRUE, InstrEventsErrorTrapping.EVENT_NOTHING_PENDING.getErrorCode(), null,null);
        else{
            ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
            messages.addMainForError(InstrEventsErrorTrapping.EVENT_HAS_PENDING_RESULTS.getErrorCode(), new Object[]{diagn.length});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.EVENT_HAS_PENDING_RESULTS.getErrorCode(), new Object[]{diagn.length},null);
        }        
    }
    
}
