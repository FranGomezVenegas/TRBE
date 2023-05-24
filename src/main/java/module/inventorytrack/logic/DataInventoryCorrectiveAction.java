/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inventorytrack.logic;

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
import module.inventorytrack.definition.InvTrackingEnums;
import module.inventorytrack.definition.TblsInvTrackingData;
import module.inventorytrack.definition.TblsInvTrackingProcedure;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
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
public class DataInventoryCorrectiveAction {

    public enum ProgramCorrectiveStatus {
        CREATED, CLOSED
    }

    public enum InventoryCorrectiveActionStatuses {
        STATUS_CLOSED(DataProgramCorrectiveActionBusinessRules.STATUS_CLOSED),
        STATUS_FIRST(DataProgramCorrectiveActionBusinessRules.STATUS_FIRST),;

        InventoryCorrectiveActionStatuses(DataProgramCorrectiveActionBusinessRules busRulName) {
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
     * @param certifId
     * @param invTrackingFieldNames
     * @param invTrackingFieldValues
     * @return
     */
    public static InternalMessage createNew(Integer certifId, String[] invTrackingFieldNames, Object[] invTrackingFieldValues) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        Object[] existsRecord = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION.getTableName(),
                new String[]{TblsInvTrackingProcedure.InventoryCorrectiveAction.CERTIF_ID.getName()}, new Object[]{certifId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, ProgramCorrectiveActionErrorTrapping.RECORD_ALREADY_EXISTS, new Object[]{certifId, procInstanceName});
        }

        String statusFirst = InventoryCorrectiveActionStatuses.getStatusFirstCode();
        String[] sampleFldsToGet = new String[]{TblsInvTrackingProcedure.InventoryCorrectiveAction.LOT_NAME.getName(),
            TblsInvTrackingProcedure.InventoryCorrectiveAction.REFERENCE.getName(), TblsInvTrackingProcedure.InventoryCorrectiveAction.CATEGORY.getName()};
        String[] myFldName = new String[]{TblsInvTrackingProcedure.InventoryCorrectiveAction.CERTIF_ID.getName()};
        Object[] myFldValue = new Object[]{certifId};
        for (TblsInvTrackingProcedure.InventoryCorrectiveAction obj : TblsInvTrackingProcedure.InventoryCorrectiveAction.values()) {
            if (Boolean.FALSE.equals("TBL".equalsIgnoreCase(obj.name()))) {
                Integer posicInArray = LPArray.valuePosicInArray(invTrackingFieldNames, obj.getName());
                if (posicInArray > -1) {
                    myFldName = LPArray.addValueToArray1D(myFldName, obj.getName());
                    myFldValue = LPArray.addValueToArray1D(myFldValue, invTrackingFieldValues[posicInArray]);
                }
            }
        }
        /*        String programName="";
        Integer posicInArray=LPArray.valuePosicInArray(invTrackingFieldNames, TblsInvTrackingProcedure.InventoryCorrectiveAction.LOT_NAME.getName());
        if (posicInArray==-1){
          posicInArray=LPArray.valuePosicInArray(invTrackingFieldNames, TblsInvTrackingProcedure.InventoryCorrectiveAction.CERTIF_ID.getName());
          if (posicInArray==-1)  
                return new InternalMessage{LPPlatform.LAB_FALSE, null, null};
          certifId=Integer.valueOf(LPNulls.replaceNull(invTrackingFieldValues[posicInArray].toString()));
          Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION.getTableName(), 
                  new String[]{TblsInvTrackingData.LotCertification.CERTIF_ID.getName()}, new Object[]{certifId}, 
                  new String[]{TblsInvTrackingProcedure.InventoryCorrectiveAction.LOT_NAME.getName()});
          if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())){
              return new InternalMessage(LPPlatform.LAB_FALSE, LPArray.array2dTo1d(sampleInfo));
          }
          programName=sampleInfo[0][0].toString();
        }else{programName=invTrackingFieldValues[posicInArray].toString();}

        myFldValue[0]=programName;*/
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION.getTableName(),
                new String[]{TblsInvTrackingData.LotCertification.CERTIF_ID.getName()}, new Object[]{certifId}, sampleFldsToGet);
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
        Integer posicInArray = LPArray.valuePosicInArray(myFldName, TblsInvTrackingProcedure.InventoryCorrectiveAction.STATUS.getName());
        if (posicInArray == -1) {
            myFldName = LPArray.addValueToArray1D(myFldName, TblsInvTrackingProcedure.InventoryCorrectiveAction.STATUS.getName());
            myFldValue = LPArray.addValueToArray1D(myFldValue, statusFirst);
        } else {
            myFldValue[posicInArray] = statusFirst;
        }
        posicInArray = LPArray.valuePosicInArray(myFldName, TblsInvTrackingProcedure.InventoryCorrectiveAction.CREATED_BY.getName());
        if (posicInArray == -1) {
            myFldName = LPArray.addValueToArray1D(myFldName, TblsInvTrackingProcedure.InventoryCorrectiveAction.CREATED_BY.getName());
            myFldValue = LPArray.addValueToArray1D(myFldValue, token.getPersonName());
        } else {
            myFldValue[posicInArray] = token.getPersonName();
        }
        posicInArray = LPArray.valuePosicInArray(myFldName, TblsInvTrackingProcedure.InventoryCorrectiveAction.CREATED_ON.getName());
        if (posicInArray == -1) {
            myFldName = LPArray.addValueToArray1D(myFldName, TblsInvTrackingProcedure.InventoryCorrectiveAction.CREATED_ON.getName());
            myFldValue = LPArray.addValueToArray1D(myFldValue, LPDate.getCurrentTimeStamp());
        } else {
            myFldValue[posicInArray] = LPDate.getCurrentTimeStamp();
        }
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION, myFldName, myFldValue);
        return new InternalMessage(LPPlatform.LAB_TRUE,
                InvTrackingEnums.InventoryTrackAPIactionsEndpoints.COMPLETE_QUALIFICATION, new Object[]{certifId}, insertRecordInTable.getNewRowId());

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

        String statusClosed = InventoryCorrectiveActionStatuses.STATUS_CLOSED.getStatusCode();
        Object[][] correctiveActionInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION.getTableName(),
                new String[]{TblsInvTrackingProcedure.InventoryCorrectiveAction.ID.getName()}, new Object[]{correctiveActionId},
                new String[]{TblsInvTrackingProcedure.InventoryCorrectiveAction.STATUS.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(correctiveActionInfo[0][0].toString())) {
            return correctiveActionInfo[0];
        }
        if (statusClosed.equalsIgnoreCase(correctiveActionInfo[0][0].toString())) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ProgramCorrectiveActionErrorTrapping.ACTION_CLOSED, new Object[]{correctiveActionId});
        }
        String[] updFldName = new String[]{TblsInvTrackingProcedure.InventoryCorrectiveAction.STATUS.getName()};
        Object[] updFldValue = new Object[]{statusClosed};
        if (investId != null) {
            updFldName = LPArray.addValueToArray1D(updFldName, TblsInvTrackingProcedure.InventoryCorrectiveAction.INVEST_ID.getName());
            updFldValue = LPArray.addValueToArray1D(updFldValue, investId);
        }
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInvTrackingProcedure.InventoryCorrectiveAction.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{correctiveActionId}, "");
        return Rdbms.updateRecordFieldsByFilter(TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION,
                EnumIntTableFields.getTableFieldsFromString(TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION, updFldName), updFldValue, sqlWhere, null);
    }

    public static Boolean isProgramCorrectiveActionEnable(String procInstanceName) {
        return "ENABLE".equalsIgnoreCase(Parameter.getBusinessRuleProcedureFile(procInstanceName, DataProgramCorrectiveActionBusinessRules.ACTION_MODE.getAreaName(), DataProgramCorrectiveActionBusinessRules.ACTION_MODE.getTagName()));
    }

    public static Object[] markAsAddedToInvestigation(Integer investId, String objectType, Object objectId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String statusClosed = InventoryCorrectiveActionStatuses.STATUS_CLOSED.getStatusCode();
        String objectIdClass = null;
        String fieldToFindRecord = null;
        if (TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION.getTableName().equalsIgnoreCase(objectType)) {
            fieldToFindRecord = TblsInvTrackingProcedure.InventoryCorrectiveAction.CERTIF_ID.getName();
        }
        if (fieldToFindRecord == null) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "Object Type <*1*> not recognized", new Object[]{objectType});
        } else {
            objectIdClass = LPDatabase.integer();
        }
        Object[][] programCorrectiveActionsToMarkAsCompleted = null;
        if (LPDatabase.integer().equalsIgnoreCase(objectIdClass)) {
            programCorrectiveActionsToMarkAsCompleted = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION.getTableName(),
                    new String[]{fieldToFindRecord, TblsInvTrackingProcedure.InventoryCorrectiveAction.STATUS.getName() + " " + WHERECLAUSE_TYPES.NOT_IN.getSqlClause()}, new Object[]{Integer.valueOf(objectId.toString()), statusClosed},
                    new String[]{TblsInvTrackingProcedure.InventoryCorrectiveAction.ID.getName(), TblsInvTrackingProcedure.InventoryCorrectiveAction.INVEST_ID.getName()});
        } else {
            programCorrectiveActionsToMarkAsCompleted = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION.getTableName(),
                    new String[]{fieldToFindRecord, TblsInvTrackingProcedure.InventoryCorrectiveAction.STATUS.getName() + " " + WHERECLAUSE_TYPES.NOT_IN.getSqlClause()}, new Object[]{objectId.toString(), statusClosed},
                    new String[]{TblsInvTrackingProcedure.InventoryCorrectiveAction.ID.getName(), TblsInvTrackingProcedure.InventoryCorrectiveAction.INVEST_ID.getName()});
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
            sqlWhere.addConstraint(TblsInvTrackingProcedure.InventoryCorrectiveAction.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{Integer.valueOf(curObj[0].toString())}, "");
            diagn = Rdbms.updateRecordFieldsByFilter(TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION,
                    EnumIntTableFields.getTableFieldsFromString(TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION, new String[]{TblsInvTrackingProcedure.InventoryCorrectiveAction.INVEST_ID.getName()}), new Object[]{investId}, sqlWhere, null);
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
