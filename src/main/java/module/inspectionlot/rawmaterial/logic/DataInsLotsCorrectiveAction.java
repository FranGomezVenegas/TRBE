/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.SqlWhere;
import databases.features.Token;
import functionaljavaa.parameter.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMProcedure;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;

/**
 *
 * @author Administrator
 */
public class DataInsLotsCorrectiveAction {

    public enum ProgramCorrectiveStatus {
        CREATED, CLOSED
    }

    public enum LotsCorrectiveActionStatuses {
        STATUS_CLOSED(DataProgramCorrectiveActionBusinessRules.STATUS_CLOSED),
        STATUS_FIRST(DataProgramCorrectiveActionBusinessRules.STATUS_FIRST),;

        LotsCorrectiveActionStatuses(DataProgramCorrectiveActionBusinessRules busRulName) {
            this.busRulName = busRulName;
        }

        public static String getStatusFirstCode() {
            return "CREATED";
        }

        public String getStatusCode() {
            return "CLOSED";
        }
        private final DataProgramCorrectiveActionBusinessRules busRulName;
    }

    public enum DataProgramCorrectiveActionBusinessRules implements EnumIntBusinessRules {
        STATUS_CLOSED("programCorrectiveAction_statusClosed", GlobalVariables.Schemas.DATA.getName(), null, null, '|', null, null),
        STATUS_FIRST("programCorrectiveAction_statusFirst", GlobalVariables.Schemas.DATA.getName(), null, null, '|', null, null),
        ACTION_MODE("programCorrectiveActionMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        STILLOPEN_NOTIFMODE("programCorrectiveActionNotifModeStillInProgress", GlobalVariables.Schemas.PROCEDURE.getName(),
                null, false, '|', null, null), //(JSONArray) Json.createArrayBuilder().add("locking").add("warning").add("silent").build()
        ;

        private DataProgramCorrectiveActionBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator,
                Boolean isOpt, ArrayList<String[]> preReqs) {
            this.tagName = tgName;
            this.areaName = areaNm;
            this.valuesList = valuesList;
            this.allowMultiValue = allowMulti;
            this.multiValueSeparator = separator;
            this.isOptional = isOpt;
            this.preReqs = preReqs;
        }

        @Override
        public String getTagName() {
            return this.tagName;
        }

        @Override
        public String getAreaName() {
            return this.areaName;
        }

        @Override
        public JSONArray getValuesList() {
            return this.valuesList;
        }

        @Override
        public Boolean getAllowMultiValue() {
            return this.allowMultiValue;
        }

        @Override
        public char getMultiValueSeparator() {
            return this.multiValueSeparator;
        }

        @Override
        public Boolean getIsOptional() {
            return isOptional;
        }

        @Override
        public ArrayList<String[]> getPreReqs() {
            return this.preReqs;
        }

        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;
        private final Boolean isOptional;
        private final ArrayList<String[]> preReqs;
    }

    public enum ProgramCorrectiveActionErrorTrapping implements EnumIntMessages {
        RECORD_ALREADY_EXISTS("programCorrectiveActionRecord_AlreadyExists", "", ""),
        ACTION_CLOSED("DataProgramCorrectiveAction_actionClosed", "The action <*1*> is already closed, no action can be performed.", "La acción <*1*> está cerrada y no admite cambios."),;

        private ProgramCorrectiveActionErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
            this.errorCode = errCode;
            this.defaultTextWhenNotInPropertiesFileEn = defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs = defaultTextEs;
        }

        @Override
        public String getErrorCode() {
            return this.errorCode;
        }

        @Override
        public String getDefaultTextEn() {
            return this.defaultTextWhenNotInPropertiesFileEn;
        }

        @Override
        public String getDefaultTextEs() {
            return this.defaultTextWhenNotInPropertiesFileEs;
        }

        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }

    /**
     *
     * @param bulkId
     * @param instrumentFieldNames
     * @param instrumentFieldValues
     * @return
     */
    public static InternalMessage createNew(String lotName, Integer bulkId, EnumIntEndpoints endpoint, String[] instrumentFieldNames, Object[] instrumentFieldValues, String objectType) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String[] keyFldsN = new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.LOT_NAME.getName()};
        Object[] keyFldsV = new Object[]{lotName};
        if (bulkId != null) {
            keyFldsN = LPArray.addValueToArray1D(keyFldsN, TblsInspLotRMProcedure.LotsCorrectiveAction.BULK_ID.getName());
            keyFldsV = LPArray.addValueToArray1D(keyFldsV, bulkId);
        }
        Object[] existsRecord = Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION.getTableName(),
                keyFldsN, keyFldsV);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ProgramCorrectiveActionErrorTrapping.RECORD_ALREADY_EXISTS, new Object[]{bulkId, procInstanceName});
        }

        String statusFirst = LotsCorrectiveActionStatuses.getStatusFirstCode();
        String[] sampleFldsToGet = new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.LOT_NAME.getName()};
        String[] myFldName = new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.LOT_NAME.getName()};
        Object[] myFldValue = new Object[]{""};
        for (TblsInspLotRMProcedure.LotsCorrectiveAction obj : TblsInspLotRMProcedure.LotsCorrectiveAction.values()) {
            if (Boolean.FALSE.equals("TBL".equalsIgnoreCase(obj.name()))) {
                Integer posicInArray = LPArray.valuePosicInArray(instrumentFieldNames, obj.getName());
                if (posicInArray > -1) {
                    myFldName = LPArray.addValueToArray1D(myFldName, obj.getName());
                    myFldValue = LPArray.addValueToArray1D(myFldValue, instrumentFieldValues[posicInArray]);
                }
            }
        }
        /*        
        String programName="";
        Integer posicInArray=LPArray.valuePosicInArray(instrumentFieldNames, TblsInspLotRMProcedure.LotsCorrectiveAction.INSTRUMENT.getName());
        if (posicInArray==-1){
          posicInArray=LPArray.valuePosicInArray(instrumentFieldNames, TblsInspLotRMProcedure.LotsCorrectiveAction.EVENT_ID.getName());
          if (posicInArray==-1) return new Object[]{LPPlatform.LAB_FALSE};
          eventId=Integer.valueOf(LPNulls.replaceNull(instrumentFieldValues[posicInArray].toString()));
          Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInstrumentsData.TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), 
                  new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()}, new Object[]{eventId}, 
                  new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.INSTRUMENT.getName()});
          if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())){return LPArray.array2dTo1d(sampleInfo);}
          programName=sampleInfo[0][0].toString();
        }else{programName=instrumentFieldValues[posicInArray].toString();}

        myFldValue[0]=programName; */
 /*        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInstrumentsData.TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()}, new Object[]{bulkId}, sampleFldsToGet);
        for (int iFld=0;iFld<sampleFldsToGet.length;iFld++){
          String currFld=sampleFldsToGet[iFld];
            Integer posicInArray = LPArray.valuePosicInArray(myFldName, currFld);
          if (posicInArray==-1){
            myFldName=LPArray.addValueToArray1D(myFldName, currFld);
            myFldValue=LPArray.addValueToArray1D(myFldValue, sampleInfo[0][iFld]);      
          }else{myFldValue[posicInArray]=sampleInfo[0][iFld];}      
        }*/
        Integer posicInArray = LPArray.valuePosicInArray(myFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.STATUS.getName());
        if (posicInArray == -1) {
            myFldName = LPArray.addValueToArray1D(myFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.STATUS.getName());
            myFldValue = LPArray.addValueToArray1D(myFldValue, statusFirst);
        } else {
            myFldValue[posicInArray] = statusFirst;
        }
        posicInArray = LPArray.valuePosicInArray(myFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.CREATED_BY.getName());
        if (posicInArray == -1) {
            myFldName = LPArray.addValueToArray1D(myFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.CREATED_BY.getName());
            myFldValue = LPArray.addValueToArray1D(myFldValue, token.getPersonName());
        } else {
            myFldValue[posicInArray] = token.getPersonName();
        }
        posicInArray = LPArray.valuePosicInArray(myFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.CREATED_ON.getName());
        if (posicInArray == -1) {
            myFldName = LPArray.addValueToArray1D(myFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.CREATED_ON.getName());
            myFldValue = LPArray.addValueToArray1D(myFldValue, LPDate.getCurrentTimeStamp());
        } else {
            myFldValue[posicInArray] = LPDate.getCurrentTimeStamp();
        }
        myFldName = LPArray.addValueToArray1D(myFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.OBJECT_TYPE.getName());
        myFldValue = LPArray.addValueToArray1D(myFldValue, objectType);
        
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION, myFldName, myFldValue);
        return new InternalMessage(LPPlatform.LAB_TRUE,
                endpoint, new Object[]{bulkId}, insertRecordInTable.getNewRowId());

    }

    public static InternalMessage createNew(Integer bulkId, EnumIntEndpoints endpoint, String[] instrumentFieldNames, Object[] instrumentFieldValues, String objectType) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        Object[] existsRecord = Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION.getTableName(),
                new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.BULK_ID.getName()}, new Object[]{bulkId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ProgramCorrectiveActionErrorTrapping.RECORD_ALREADY_EXISTS, new Object[]{bulkId, procInstanceName});
        }

        String statusFirst = LotsCorrectiveActionStatuses.getStatusFirstCode();
        //String[] sampleFldsToGet= new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.LOT_NAME.getName()};
        String[] myFldName = new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.BULK_ID.getName()};
        Object[] myFldValue = new Object[]{bulkId};
        for (TblsInspLotRMProcedure.LotsCorrectiveAction obj : TblsInspLotRMProcedure.LotsCorrectiveAction.values()) {
            if (Boolean.FALSE.equals("TBL".equalsIgnoreCase(obj.name()))) {
                Integer posicInArray = LPArray.valuePosicInArray(instrumentFieldNames, obj.getName());
                if (posicInArray > -1) {
                    myFldName = LPArray.addValueToArray1D(myFldName, obj.getName());
                    myFldValue = LPArray.addValueToArray1D(myFldValue, instrumentFieldValues[posicInArray]);
                }
            }
        }
        /*        
        String programName="";
        Integer posicInArray=LPArray.valuePosicInArray(instrumentFieldNames, TblsInspLotRMProcedure.LotsCorrectiveAction.INSTRUMENT.getName());
        if (posicInArray==-1){
          posicInArray=LPArray.valuePosicInArray(instrumentFieldNames, TblsInspLotRMProcedure.LotsCorrectiveAction.EVENT_ID.getName());
          if (posicInArray==-1) return new Object[]{LPPlatform.LAB_FALSE};
          eventId=Integer.valueOf(LPNulls.replaceNull(instrumentFieldValues[posicInArray].toString()));
          Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInstrumentsData.TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), 
                  new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()}, new Object[]{eventId}, 
                  new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.INSTRUMENT.getName()});
          if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())){return LPArray.array2dTo1d(sampleInfo);}
          programName=sampleInfo[0][0].toString();
        }else{programName=instrumentFieldValues[posicInArray].toString();}

        myFldValue[0]=programName; */
 /*
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInstrumentsData.TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()}, new Object[]{eventId}, sampleFldsToGet);
        for (int iFld=0;iFld<sampleFldsToGet.length;iFld++){
          String currFld=sampleFldsToGet[iFld];
            Integer posicInArray = LPArray.valuePosicInArray(myFldName, currFld);
          if (posicInArray==-1){
            myFldName=LPArray.addValueToArray1D(myFldName, currFld);
            myFldValue=LPArray.addValueToArray1D(myFldValue, sampleInfo[0][iFld]);      
          }else{myFldValue[posicInArray]=sampleInfo[0][iFld];}      
        }*/
        Integer posicInArray = LPArray.valuePosicInArray(myFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.STATUS.getName());
        if (posicInArray == -1) {
            myFldName = LPArray.addValueToArray1D(myFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.STATUS.getName());
            myFldValue = LPArray.addValueToArray1D(myFldValue, statusFirst);
        } else {
            myFldValue[posicInArray] = statusFirst;
        }
        posicInArray = LPArray.valuePosicInArray(myFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.CREATED_BY.getName());
        if (posicInArray == -1) {
            myFldName = LPArray.addValueToArray1D(myFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.CREATED_BY.getName());
            myFldValue = LPArray.addValueToArray1D(myFldValue, token.getPersonName());
        } else {
            myFldValue[posicInArray] = token.getPersonName();
        }
        posicInArray = LPArray.valuePosicInArray(myFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.CREATED_ON.getName());
        if (posicInArray == -1) {
            myFldName = LPArray.addValueToArray1D(myFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.CREATED_ON.getName());
            myFldValue = LPArray.addValueToArray1D(myFldValue, LPDate.getCurrentTimeStamp());
        } else {
            myFldValue[posicInArray] = LPDate.getCurrentTimeStamp();
        }
        myFldName = LPArray.addValueToArray1D(myFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.OBJECT_TYPE.getName());
        myFldValue = LPArray.addValueToArray1D(myFldValue, objectType);

        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION, myFldName, myFldValue);
        return new InternalMessage(LPPlatform.LAB_TRUE,
                endpoint, new Object[]{bulkId}, insertRecordInTable.getNewRowId());

    }

    /**
     *
     * @param correctiveActionId
     * @return
     */
    public static Object[] markAsCompleted(Integer correctiveActionId) {
        return markAsCompleted(correctiveActionId, null);
    }

    public static Object[] markAsCompleted(Integer correctiveActionId, Integer investId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String statusClosed = LotsCorrectiveActionStatuses.STATUS_CLOSED.getStatusCode();
        Object[][] correctiveActionInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION.getTableName(),
                new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.ID.getName()}, new Object[]{correctiveActionId},
                new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.STATUS.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(correctiveActionInfo[0][0].toString())) {
            return correctiveActionInfo[0];
        }
        if (statusClosed.equalsIgnoreCase(correctiveActionInfo[0][0].toString())) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ProgramCorrectiveActionErrorTrapping.ACTION_CLOSED, new Object[]{correctiveActionId});
        }
        String[] updFldName = new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.STATUS.getName()};
        Object[] updFldValue = new Object[]{statusClosed};
        if (investId != null) {
            updFldName = LPArray.addValueToArray1D(updFldName, TblsInspLotRMProcedure.LotsCorrectiveAction.INVEST_ID.getName());
            updFldValue = LPArray.addValueToArray1D(updFldValue, investId);
        }
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInspLotRMProcedure.LotsCorrectiveAction.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{correctiveActionId}, "");
        return Rdbms.updateRecordFieldsByFilter(TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION,
                EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION, updFldName), updFldValue, sqlWhere, null);
    }

    public static Boolean isProgramCorrectiveActionEnable(String procInstanceName) {
        return "ENABLE".equalsIgnoreCase(Parameter.getBusinessRuleProcedureFile(procInstanceName, DataProgramCorrectiveActionBusinessRules.ACTION_MODE.getAreaName(), DataProgramCorrectiveActionBusinessRules.ACTION_MODE.getTagName()));
    }

    public static Object[] markAsAddedToInvestigation(Integer investId, String objectType, Object objectId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String statusClosed = LotsCorrectiveActionStatuses.STATUS_CLOSED.getStatusCode();
        String objectIdClass = null;
        
        String fieldToFindRecord = null;
        if (TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName().equalsIgnoreCase(objectType)) {
            fieldToFindRecord = TblsInspLotRMProcedure.LotsCorrectiveAction.LOT_NAME.getName();
        }
        if (TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName().equalsIgnoreCase(objectType)) {
            fieldToFindRecord = TblsInspLotRMProcedure.LotsCorrectiveAction.BULK_ID.getName();
            objectIdClass = LPDatabase.integer();
        }
        if (fieldToFindRecord == null) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "Object Type <*1*> not recognized", new Object[]{objectType});
        } else {
            objectIdClass = LPDatabase.integer();
        }
        Object[][] programCorrectiveActionsToMarkAsCompleted = null;
        if (objectIdClass!=null&&LPDatabase.integer().equalsIgnoreCase(objectIdClass)) {
            programCorrectiveActionsToMarkAsCompleted = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION.getTableName(),
                    new String[]{fieldToFindRecord, TblsInspLotRMProcedure.LotsCorrectiveAction.STATUS.getName() + " " + WHERECLAUSE_TYPES.NOT_IN.getSqlClause()}, 
                    new Object[]{Integer.valueOf(objectId.toString()), statusClosed},
                    new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.ID.getName(), TblsInspLotRMProcedure.LotsCorrectiveAction.INVEST_ID.getName()});
        } else {
            programCorrectiveActionsToMarkAsCompleted = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION.getTableName(),
                    new String[]{fieldToFindRecord, TblsInspLotRMProcedure.LotsCorrectiveAction.STATUS.getName() + " " + WHERECLAUSE_TYPES.NOT_IN.getSqlClause()}, new Object[]{objectId.toString(), statusClosed},
                    new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.ID.getName(), TblsInspLotRMProcedure.LotsCorrectiveAction.INVEST_ID.getName()});
        }
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programCorrectiveActionsToMarkAsCompleted[0][0].toString())) {
            return LPArray.array2dTo1d(programCorrectiveActionsToMarkAsCompleted);
        }
        Object[] diagnostic = null;
        for (Object[] curObj : programCorrectiveActionsToMarkAsCompleted) {
            if (statusClosed.equalsIgnoreCase(curObj[1].toString())) {
                diagnostic = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "<*1*> is closed, cannot be added to the investigation", new Object[]{investId});
            }
            Object[] diagn = markAsCompleted(Integer.valueOf(curObj[0].toString()), investId);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) {
                diagnostic = diagn;
            }
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsInspLotRMProcedure.LotsCorrectiveAction.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{Integer.valueOf(curObj[0].toString())}, "");
            diagn = Rdbms.updateRecordFieldsByFilter(TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION,
                    EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION, new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.INVEST_ID.getName()}), new Object[]{investId}, sqlWhere, null);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) {
                diagnostic = diagn;
            }
        }
        if (diagnostic == null) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "allMarkedAsAdded <*1*>", new Object[]{Arrays.toString(programCorrectiveActionsToMarkAsCompleted)});
        } else {
            return diagnostic;
        }

    }
}
