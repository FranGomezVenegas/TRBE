/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.instrumentsmanagement.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.SqlWhere;
import databases.features.Token;
import functionaljavaa.investigation.Investigation;
import functionaljavaa.parameter.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import module.instrumentsmanagement.definition.TblsInstrumentsProcedure;
import module.instrumentsmanagement.definition.TblsInstrumentsData;
import org.json.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;

/**
 *
 * @author Administrator
 */
public class DataInstrumentsCorrectiveAction {

    public enum ProgramCorrectiveStatus {
        CREATED, CLOSED
    }

    public enum InstrumentsCorrectiveActionStatuses {
        STATUS_CLOSED(DataProgramCorrectiveActionBusinessRules.STATUS_CLOSED),
        STATUS_FIRST(DataProgramCorrectiveActionBusinessRules.STATUS_FIRST),;

        InstrumentsCorrectiveActionStatuses(DataProgramCorrectiveActionBusinessRules busRulName) {
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
     * @param eventId
     * @param instrumentFieldNames
     * @param instrumentFieldValues
     * @return
     */
    public static InternalMessage createNew(Integer eventId, EnumIntEndpoints endpoint, String[] instrumentFieldNames, Object[] instrumentFieldValues) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        Object[] existsRecord = Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION.getTableName(),
                new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.EVENT_ID.getName()}, new Object[]{eventId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ProgramCorrectiveActionErrorTrapping.RECORD_ALREADY_EXISTS, new Object[]{eventId, procInstanceName});
        }

        String statusFirst = InstrumentsCorrectiveActionStatuses.getStatusFirstCode();
        String[] sampleFldsToGet = new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.INSTRUMENT.getName()};
        String[] myFldName = new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.EVENT_ID.getName()};
        Object[] myFldValue = new Object[]{eventId};
        for (TblsInstrumentsProcedure.InstrumentsCorrectiveAction obj : TblsInstrumentsProcedure.InstrumentsCorrectiveAction.values()) {
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
        Integer posicInArray=LPArray.valuePosicInArray(instrumentFieldNames, TblsInstrumentsProcedure.InstrumentsCorrectiveAction.INSTRUMENT.getName());
        if (posicInArray==-1){
          posicInArray=LPArray.valuePosicInArray(instrumentFieldNames, TblsInstrumentsProcedure.InstrumentsCorrectiveAction.EVENT_ID.getName());
          if (posicInArray==-1) return new Object[]{LPPlatform.LAB_FALSE};
          eventId=Integer.valueOf(LPNulls.replaceNull(instrumentFieldValues[posicInArray].toString()));
          Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInstrumentsData.TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), 
                  new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()}, new Object[]{eventId}, 
                  new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.INSTRUMENT.getName()});
          if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())){return LPArray.array2dTo1d(sampleInfo);}
          programName=sampleInfo[0][0].toString();
        }else{programName=instrumentFieldValues[posicInArray].toString();}

        myFldValue[0]=programName; */
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInstrumentsData.TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()}, new Object[]{eventId}, sampleFldsToGet);
        for (int iFld = 0; iFld < sampleFldsToGet.length; iFld++) {
            String currFld = sampleFldsToGet[iFld];
            Integer posicInArray = LPArray.valuePosicInArray(myFldName, currFld);
            if (posicInArray == -1) {
                myFldName = LPArray.addValueToArray1D(myFldName, currFld);
                myFldValue = LPArray.addValueToArray1D(myFldValue, sampleInfo[0][iFld]);
            } else {
                myFldValue[posicInArray] = sampleInfo[0][iFld];
            }
        }
        Integer posicInArray = LPArray.valuePosicInArray(myFldName, TblsInstrumentsProcedure.InstrumentsCorrectiveAction.STATUS.getName());
        if (posicInArray == -1) {
            myFldName = LPArray.addValueToArray1D(myFldName, TblsInstrumentsProcedure.InstrumentsCorrectiveAction.STATUS.getName());
            myFldValue = LPArray.addValueToArray1D(myFldValue, statusFirst);
        } else {
            myFldValue[posicInArray] = statusFirst;
        }
        posicInArray = LPArray.valuePosicInArray(myFldName, TblsInstrumentsProcedure.InstrumentsCorrectiveAction.CREATED_BY.getName());
        if (posicInArray == -1) {
            myFldName = LPArray.addValueToArray1D(myFldName, TblsInstrumentsProcedure.InstrumentsCorrectiveAction.CREATED_BY.getName());
            myFldValue = LPArray.addValueToArray1D(myFldValue, token.getPersonName());
        } else {
            myFldValue[posicInArray] = token.getPersonName();
        }
        posicInArray = LPArray.valuePosicInArray(myFldName, TblsInstrumentsProcedure.InstrumentsCorrectiveAction.CREATED_ON.getName());
        if (posicInArray == -1) {
            myFldName = LPArray.addValueToArray1D(myFldName, TblsInstrumentsProcedure.InstrumentsCorrectiveAction.CREATED_ON.getName());
            myFldValue = LPArray.addValueToArray1D(myFldValue, LPDate.getCurrentTimeStamp());
        } else {
            myFldValue[posicInArray] = LPDate.getCurrentTimeStamp();
        }
        posicInArray = LPArray.valuePosicInArray(myFldName, TblsInstrumentsProcedure.InstrumentsCorrectiveAction.OBJECT_TYPE.getName());
        if (posicInArray == -1) {
            myFldName = LPArray.addValueToArray1D(myFldName, TblsInstrumentsProcedure.InstrumentsCorrectiveAction.OBJECT_TYPE.getName());
            myFldValue = LPArray.addValueToArray1D(myFldValue, TblsInstrumentsData.TablesInstrumentsData.INSTRUMENT_EVENT.getTableName());
        }
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION, myFldName, myFldValue);
        return new InternalMessage(LPPlatform.LAB_TRUE,
                endpoint, new Object[]{eventId}, insertRecordInTable.getNewRowId());

    }

    /**
     *
     * @param correctiveActionId
     * @return
     */
    public static InternalMessage markAsCompleted(Integer correctiveActionId) {
        return markAsCompleted(correctiveActionId, null);
    }

    public static InternalMessage markAsCompleted(Integer correctiveActionId, Integer investId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String statusClosed = InstrumentsCorrectiveActionStatuses.STATUS_CLOSED.getStatusCode();
        Object[][] correctiveActionInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION.getTableName(),
                new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.ID.getName()}, new Object[]{correctiveActionId},
                new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.STATUS.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(correctiveActionInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{correctiveActionId});
        }
        if (statusClosed.equalsIgnoreCase(correctiveActionInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ProgramCorrectiveActionErrorTrapping.ACTION_CLOSED, new Object[]{correctiveActionId});
        }
        String[] updFldName = new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.STATUS.getName()};
        Object[] updFldValue = new Object[]{statusClosed};
        if (investId != null) {
            updFldName = LPArray.addValueToArray1D(updFldName, TblsInstrumentsProcedure.InstrumentsCorrectiveAction.INVEST_ID.getName());
            updFldValue = LPArray.addValueToArray1D(updFldValue, investId);
        }
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInstrumentsProcedure.InstrumentsCorrectiveAction.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{correctiveActionId}, "");
        RdbmsObject diagnoseObj =  Rdbms.updateTableRecordFieldsByFilter(TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION,
                EnumIntTableFields.getTableFieldsFromString(TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION, updFldName), updFldValue, sqlWhere, null);
        return new InternalMessage(diagnoseObj.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, diagnoseObj.getErrorMessageCode(), diagnoseObj.getErrorMessageVariables());
    }

    public static Boolean isProgramCorrectiveActionEnable(String procInstanceName) {
        return "ENABLE".equalsIgnoreCase(Parameter.getBusinessRuleProcedureFile(procInstanceName, DataProgramCorrectiveActionBusinessRules.ACTION_MODE.getAreaName(), DataProgramCorrectiveActionBusinessRules.ACTION_MODE.getTagName()));
    }

    public static InternalMessage markAsAddedToInvestigation(Integer investId, String objectType, Object objectId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String statusClosed = InstrumentsCorrectiveActionStatuses.STATUS_CLOSED.getStatusCode();
        String objectIdClass = null;
        String fieldToFindRecord = null;
        if (TblsInstrumentsProcedure.InstrumentsCorrectiveAction.EVENT_ID.getName().equalsIgnoreCase(objectType)) {
            fieldToFindRecord = TblsInstrumentsProcedure.InstrumentsCorrectiveAction.EVENT_ID.getName();
        }
        if (fieldToFindRecord == null) {
            return new InternalMessage(LPPlatform.LAB_FALSE, Investigation.InvestigationErrorTrapping.OBJECT_TYPE_NOT_RECOGNIZED, new Object[]{objectType});
        } else {
            objectIdClass = LPDatabase.integer();
        }
        Object[][] programCorrectiveActionsToMarkAsCompleted = null;
        if (LPDatabase.integer().equalsIgnoreCase(objectIdClass)) {
            programCorrectiveActionsToMarkAsCompleted = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION.getTableName(),
                    new String[]{fieldToFindRecord, TblsInstrumentsProcedure.InstrumentsCorrectiveAction.STATUS.getName() + " " + WHERECLAUSE_TYPES.NOT_IN.getSqlClause()}, new Object[]{Integer.valueOf(objectId.toString()), statusClosed},
                    new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.ID.getName(), TblsInstrumentsProcedure.InstrumentsCorrectiveAction.INVEST_ID.getName()});
        } else {
            programCorrectiveActionsToMarkAsCompleted = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION.getTableName(),
                    new String[]{fieldToFindRecord, TblsInstrumentsProcedure.InstrumentsCorrectiveAction.STATUS.getName() + " " + WHERECLAUSE_TYPES.NOT_IN.getSqlClause()}, new Object[]{objectId.toString(), statusClosed},
                    new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.ID.getName(), TblsInstrumentsProcedure.InstrumentsCorrectiveAction.INVEST_ID.getName()});
        }
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programCorrectiveActionsToMarkAsCompleted[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{objectId.toString(), objectType});
        }
        InternalMessage diagnostic = null;
        for (Object[] curObj : programCorrectiveActionsToMarkAsCompleted) {
            if (statusClosed.equalsIgnoreCase(curObj[1].toString())) {
                diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, Investigation.InvestigationErrorTrapping.IS_CLOSED, new Object[]{investId});
            }
            InternalMessage diagn = markAsCompleted(Integer.valueOf(curObj[0].toString()), investId);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn.getDiagnostic())) {
                diagnostic = diagn;
            }
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsInstrumentsProcedure.InstrumentsCorrectiveAction.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{Integer.valueOf(curObj[0].toString())}, "");
            RdbmsObject diagnoseObj = Rdbms.updateTableRecordFieldsByFilter(TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION,
                    EnumIntTableFields.getTableFieldsFromString(TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION, new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.INVEST_ID.getName()}), new Object[]{investId}, sqlWhere, null);
            if (Boolean.FALSE.equals(diagnoseObj.getRunSuccess())) {
                diagnostic = diagn;
            }
        }
        if (diagnostic == null) {
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, new Object[]{Arrays.toString(programCorrectiveActionsToMarkAsCompleted)});
        } else {
            return diagnostic;
        }

    }
}
