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
import databases.features.Token;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.isTagValueOneOfEnableOnes;
import module.instrumentsmanagement.definition.InstrumentsEnums;
import static module.instrumentsmanagement.logic.AppInstrumentsAudit.instrumentsAuditAdd;
import static module.instrumentsmanagement.logic.DataInstrumentsEvents.addVariableSetToObject;
import static module.instrumentsmanagement.logic.DataInstrumentsEvents.eventHasNotEnteredVariables;
import module.instrumentsmanagement.definition.InstrumentsEnums.AppInstrumentsAuditEvents;
import module.instrumentsmanagement.definition.InstrumentsEnums.InstrLockingReasons;
import module.instrumentsmanagement.definition.InstrumentsEnums.InstrumentsErrorTrapping;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.util.Arrays;
import java.util.Date;
import trazit.session.ResponseMessages;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.LpPlatformSuccess;
import module.instrumentsmanagement.definition.InstrumentsEnums.InstrumentsBusinessRules;
import module.instrumentsmanagement.definition.TblsInstrumentsProcedure;
import module.inventorytrack.definition.InvTrackingEnums;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class DataInstruments {

    private final String name;
    private Boolean onLine;
    private Boolean isLocked;
    private Boolean isDecommissioned;
    private String lockedReason;
    private String[] fieldNames;
    private Object[] fieldValues;
    private String family;
    private String[] familyFieldNames;
    private Object[] familyFieldValues;
    private final Boolean hasError;
    private InternalMessage errorDetail;
    private String responsible = null;
    private String responsibleBackup = null;

    public enum Decisions {
        ACCEPTED, ACCEPTED_WITH_RESTRICTIONS, REJECTED
    }

    private InternalMessage decisionValueIsCorrect(String decision) {
        try {
            Decisions.valueOf(decision);
            return new InternalMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.CORRECT, null, null);
        } catch (Exception e) {
            ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
            messages.addMainForError(InstrumentsErrorTrapping.WRONG_DECISION, new Object[]{decision, Arrays.toString(Decisions.values())});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.WRONG_DECISION, new Object[]{decision, Arrays.toString(Decisions.values())}, null);
        }
    }

    private Boolean decisionAndFamilyRuleToTurnOn(String decision, String fieldName) {
        if (Boolean.FALSE.equals(decision.toUpperCase().contains("ACCEPT"))) {
            return false;
        }
        Integer fldPosic = LPArray.valuePosicInArray(this.familyFieldNames, fieldName);
        if (fldPosic == -1) {
            return false;
        }
        return Boolean.valueOf(LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString());
    }

    private Date nextEventDate(String fieldName) {
        Integer fldPosic = LPArray.valuePosicInArray(this.familyFieldNames, fieldName);
        if (fldPosic == -1) {
            return null;
        }
        String intervalInfo = LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString();
        if (intervalInfo == null || intervalInfo.length() == 0) {
            return null;
        }
        String[] intvlInfoArr = intervalInfo.split("\\*");
        if (intvlInfoArr.length != 2) {
            return null;
        }
        return LPDate.addIntervalToGivenDate(LPDate.getCurrentDateWithNoTime(), intvlInfoArr[0], Integer.valueOf(intvlInfoArr[1]));
    }

    public DataInstruments(String instrName) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = procReqSession.getProcedureInstance();
        Object[][] instrInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(),
                new String[]{TblsInstrumentsData.Instruments.NAME.getName()}, new Object[]{instrName}, getAllFieldNames(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())) {
            this.name = null;
            this.hasError = true;
            this.errorDetail = new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{instrName, TablesInstrumentsData.INSTRUMENTS.getTableName(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())}, instrName);
        } else {
            this.hasError = false;
            this.fieldNames = getAllFieldNames(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS.getTableFields());
            this.fieldValues = instrInfo[0];
            this.name = instrName;
            this.onLine = Boolean.valueOf(LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInstrumentsData.Instruments.ON_LINE.getName())]).toString());
            if (this.onLine == null) {
                this.onLine = false;
            }
            this.isLocked = Boolean.valueOf(LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInstrumentsData.Instruments.IS_LOCKED.getName())]).toString());
            if (this.isLocked == null) {
                this.isLocked = false;
            }
            this.isDecommissioned = Boolean.valueOf(LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInstrumentsData.Instruments.DECOMMISSIONED.getName())]).toString());
            if (this.isDecommissioned == null) {
                this.isDecommissioned = false;
            }
            this.lockedReason = LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInstrumentsData.Instruments.LOCKED_REASON.getName())]).toString();
            if (Boolean.FALSE.equals(this.isLocked)) {
                responsibleLocking();
            }
            this.family = LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInstrumentsData.Instruments.FAMILY.getName())]).toString();
            if (this.family != null && this.family.length() > 0) {
                Object[][] instrFamilyInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableName(),
                        new String[]{TblsInstrumentsConfig.InstrumentsFamily.NAME.getName()}, new Object[]{this.family}, getAllFieldNames(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableFields()));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrFamilyInfo[0][0].toString())) {
                    familyFieldNames = null;
                    familyFieldValues = null;
                } else {
                    familyFieldNames = getAllFieldNames(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableFields());
                    familyFieldValues = instrFamilyInfo[0];
                }
            }
        }
    }

    private void responsibleLocking() {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Token token = procReqSession.getToken();
        Integer respFldPosic = LPArray.valuePosicInArray(this.fieldNames, TblsInstrumentsData.Instruments.RESPONSIBLE.getName());
        Integer resp2FldPosic = LPArray.valuePosicInArray(this.fieldNames, TblsInstrumentsData.Instruments.RESPONSIBLE_BACKUP.getName());
        if (respFldPosic > -1) {
            this.responsible = LPNulls.replaceNull(this.fieldValues[respFldPosic]).toString();
            if (LPNulls.replaceNull(this.fieldValues[respFldPosic]).toString().equalsIgnoreCase(token.getUserName())) {
                this.isLocked = false;
                return;
            }
        }
        if (resp2FldPosic > -1) {
            this.responsibleBackup = LPNulls.replaceNull(this.fieldValues[resp2FldPosic]).toString();
            if (LPNulls.replaceNull(this.fieldValues[resp2FldPosic]).toString().equalsIgnoreCase(token.getUserName())) {
                this.isLocked = false;
                return;
            }
        }
        if ((this.responsible == null || this.responsible.length() == 0) && (this.responsibleBackup == null || this.responsibleBackup.length() == 0)) {
            this.isLocked = false;
            return;
        }
        this.isLocked = true;
        this.lockedReason = "user is not responsible neither responsible backup";

    }

    public static InternalMessage createNewInstrument(String name, String familyName, String[] fldNames, Object[] fldValues) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = procReqSession.getMessages();
        Token token = procReqSession.getToken();
        if (fldNames == null) {
            fldNames = new String[]{};
            fldValues = new Object[]{};
        }
        if (familyName != null && familyName.length() > 0) {
            Object[][] instrFamilyInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableName(),
                    new String[]{TblsInstrumentsConfig.InstrumentsFamily.NAME.getName()}, new Object[]{familyName},
                    getAllFieldNames(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableFields()));
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrFamilyInfo[0][0].toString())) {
                messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.FAMILY_NOT_FOUND, new Object[]{familyName});
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.FAMILY_NOT_FOUND, new Object[]{familyName}, null);
            }
            fldNames = LPArray.addValueToArray1D(fldNames, TblsInstrumentsData.Instruments.FAMILY.getName());
            fldValues = LPArray.addValueToArray1D(fldValues, familyName);
        }
        fldNames = LPArray.addValueToArray1D(fldNames, new String[]{TblsInstrumentsData.Instruments.NAME.getName(), TblsInstrumentsData.Instruments.ON_LINE.getName(),
            TblsInstrumentsData.Instruments.CREATED_ON.getName(), TblsInstrumentsData.Instruments.CREATED_BY.getName()});
        fldValues = LPArray.addValueToArray1D(fldValues, new Object[]{name, false, LPDate.getCurrentTimeStamp(), token.getPersonName()});
        Object[] existsRecord = Rdbms.existsRecord(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS.getTableName(),
                new String[]{TblsInstrumentsData.Instruments.NAME.getName()}, new Object[]{name});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_EXISTS, new Object[]{name}, null);
        }

        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesInstrumentsData.INSTRUMENTS, fldNames, fldValues);
        if (Boolean.FALSE.equals(instCreationDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{name}, null);
        }
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.CREATION, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
                fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.NEW_INSTRUMENT, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.NEW_INSTRUMENT, new Object[]{name}, name);
    }

    public InternalMessage updateInstrument(String[] fldNames, Object[] fldValues) {
        return updateInstrument(fldNames, fldValues, null, null);
    }

    public InternalMessage assignResponsible(String[] fldNames, Object[] fldValues) {
        return updateInstrument(fldNames, fldValues, "ASSIGN_RESPONSIBLE", AppInstrumentsAuditEvents.RESPONSIBLE_ASSIGNED);
    }

    public InternalMessage changeResponsible(String[] fldNames, Object[] fldValues) {
        return updateInstrument(fldNames, fldValues, "CHANGE_RESPONSIBLE", AppInstrumentsAuditEvents.RESPONSIBLE_CHANGED);
    }

    public InternalMessage assignResponsibleBackup(String[] fldNames, Object[] fldValues) {
        return updateInstrument(fldNames, fldValues, "ASSIGN_RESPONSIBLE_BACKUP", AppInstrumentsAuditEvents.RESPONSIBLE_BACKUP_ASSIGNED);
    }

    public InternalMessage changeResponsibleBackup(String[] fldNames, Object[] fldValues) {
        return updateInstrument(fldNames, fldValues, "CHANGE_RESPONSIBLE_BACKUP", AppInstrumentsAuditEvents.RESPONSIBLE_BACKUP_CHANGED);
    }

    public InternalMessage updateInstrument(String[] fldNames, Object[] fldValues, String actionName, AppInstrumentsAuditEvents eventObj) {
        if (Boolean.TRUE.equals(this.isDecommissioned)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        String[] reservedFldsNotUpdatable = new String[]{TblsInstrumentsData.Instruments.NAME.getName(), TblsInstrumentsData.Instruments.ON_LINE.getName()};
        String[] reservedFldsNotUpdatableFromActions = new String[]{TblsInstrumentsData.Instruments.NAME.getName(), TblsInstrumentsData.Instruments.ON_LINE.getName()};
        if (actionName != null) {
            reservedFldsNotUpdatable = reservedFldsNotUpdatableFromActions;
        }
        for (String curFld : fldNames) {
            if (LPArray.valueInArray(reservedFldsNotUpdatable, curFld)) {
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.TRYINGUPDATE_RESERVED_FIELD, new Object[]{curFld}, null);
            }
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames == null || fldNames[0].length() == 0) {
            fldNames = new String[]{};
            fldValues = new Object[]{};
        }
        fldNames = LPArray.addValueToArray1D(fldNames, new String[]{TblsInstrumentsData.Instruments.ON_LINE.getName()});
        fldValues = LPArray.addValueToArray1D(fldValues, new Object[]{true});
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInstrumentsData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{getName()}, "");
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENTS,
                EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length - 1].toString(), new Object[]{getName()}, null);
        }
        if (eventObj == null) {
            eventObj = InstrumentsEnums.AppInstrumentsAuditEvents.UPDATE_INSTRUMENT;
        }
        instrumentsAuditAdd(eventObj, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.UPDATE_INSTRUMENT, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.UPDATE_INSTRUMENT, new Object[]{getName()}, getName());
    }

    public InternalMessage decommissionInstrument(String[] fldNames, Object[] fldValues) {
        if (Boolean.TRUE.equals(this.isDecommissioned)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        String[] reservedFldsNotUpdatable = new String[]{TblsInstrumentsData.Instruments.NAME.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(),
            TblsInstrumentsData.Instruments.LOCKED_REASON.getName(), TblsInstrumentsData.Instruments.ON_LINE.getName()};
        if (fldNames == null || fldNames[0].length() == 0) {
            fldNames = new String[]{};
            fldValues = new Object[]{};
        }
        for (String curFld : fldNames) {
            if (LPArray.valueInArray(reservedFldsNotUpdatable, curFld)) {
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.TRYINGUPDATE_RESERVED_FIELD, new Object[]{curFld}, null);
            }
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        fldNames = LPArray.addValueToArray1D(fldNames, new String[]{TblsInstrumentsData.Instruments.ON_LINE.getName(),
            TblsInstrumentsData.Instruments.DECOMMISSIONED.getName(), TblsInstrumentsData.Instruments.DECOMMISSIONED_ON.getName(),
            TblsInstrumentsData.Instruments.DECOMMISSIONED_BY.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(),
            TblsInstrumentsData.Instruments.LOCKED_REASON.getName()});
        fldValues = LPArray.addValueToArray1D(fldValues, new Object[]{false, true, LPDate.getCurrentTimeStamp(), token.getPersonName(),
            true, "decommissioned"});
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInstrumentsData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{getName()}, "");
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENTS,
                EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length - 1].toString(), new Object[]{getName()}, null);
        }
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.DECOMMISSION, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.DECOMMISSION_INSTRUMENT, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.DECOMMISSION_INSTRUMENT, new Object[]{getName()}, getName());
    }

    public InternalMessage unDecommissionInstrument(String[] fldNames, Object[] fldValues) {
        if (Boolean.FALSE.equals(this.isDecommissioned)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        String[] reservedFldsNotUpdatable = new String[]{TblsInstrumentsData.Instruments.NAME.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(),
            TblsInstrumentsData.Instruments.LOCKED_REASON.getName(), TblsInstrumentsData.Instruments.ON_LINE.getName()};
        if (fldNames == null || fldNames[0].length() == 0) {
            fldNames = new String[]{};
            fldValues = new Object[]{};
        }
        for (String curFld : fldNames) {
            if (LPArray.valueInArray(reservedFldsNotUpdatable, curFld)) {
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.TRYINGUPDATE_RESERVED_FIELD, new Object[]{curFld}, null);
            }
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        fldNames = LPArray.addValueToArray1D(fldNames, new String[]{TblsInstrumentsData.Instruments.ON_LINE.getName(),
            TblsInstrumentsData.Instruments.DECOMMISSIONED.getName(), TblsInstrumentsData.Instruments.DECOMMISSIONED_ON.getName(),
            TblsInstrumentsData.Instruments.DECOMMISSIONED_BY.getName(),
            TblsInstrumentsData.Instruments.UNDECOMMISSIONED_ON.getName(),
            TblsInstrumentsData.Instruments.UNDECOMMISSIONED_BY.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(),
            TblsInstrumentsData.Instruments.LOCKED_REASON.getName()});
        fldValues = LPArray.addValueToArray1D(fldValues, new Object[]{false, false, "NULL>>>LOCALDATETIME",
            "NULL>>>STRING",
            LPDate.getCurrentTimeStamp(), token.getPersonName(), false, ""});
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInstrumentsData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{getName()}, "");
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENTS,
                EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length - 1].toString(), new Object[]{getName()}, null);
        }
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.UNDECOMMISSION, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.UNDECOMMISSION_INSTRUMENT, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.UNDECOMMISSION_INSTRUMENT, new Object[]{getName()}, getName());
    }

    public InternalMessage turnOnLine(String[] fldNames, Object[] fldValues) {
        return turnOnLine(fldNames, fldValues, null);
    }

    public InternalMessage turnOnLine(String[] fldNames, Object[] fldValues, String actionName) {
        if (Boolean.TRUE.equals(this.isDecommissioned)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        String procedureInstance = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getProcedureInstance();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames == null || fldNames[0].length() == 0) {
            fldNames = new String[]{};
            fldValues = new Object[]{};
        }
        fldNames = LPArray.addValueToArray1D(fldNames, new String[]{TblsInstrumentsData.Instruments.ON_LINE.getName()});
        fldValues = LPArray.addValueToArray1D(fldValues, new Object[]{true});
        if (Boolean.TRUE.equals(this.onLine)) {
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_ONLINE, new Object[]{getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_ONLINE, new Object[]{getName()}, null);
        }
        if (actionName == null && this.isLocked) {
            messages.addMainForError(InstrumentsErrorTrapping.IS_LOCKED, new Object[]{getName(), this.lockedReason});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.IS_LOCKED, new Object[]{getName(), this.lockedReason}, null);
        }
        Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procedureInstance, LPPlatform.buildSchemaName(procedureInstance, GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName() + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
                new Object[]{this.getName(), ""}, new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()});

        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString()))) {
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_EVENTS, new Object[]{getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_EVENTS, new Object[]{getName()}, getName());
        }
        
        
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInstrumentsData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{getName()}, "");
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENTS,
                EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length - 1].toString(), new Object[]{getName()}, null);
        }
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.TURN_ON_LINE, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_ON_LINE, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_ON_LINE, new Object[]{getName()}, getName());
    }

    public InternalMessage turnOffLine(String[] fldNames, Object[] fldValues) {
        if (Boolean.TRUE.equals(this.isDecommissioned)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames == null || fldNames[0].length() == 0) {
            fldNames = new String[]{};
            fldValues = new Object[]{};
        }
        if (Boolean.FALSE.equals(this.onLine)) {
            messages.addMainForError(InstrumentsErrorTrapping.NOT_ONLINE, new Object[]{getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_ONLINE, new Object[]{getName()}, null);
        }
        fldNames = LPArray.addValueToArray1D(fldNames, new String[]{TblsInstrumentsData.Instruments.ON_LINE.getName()});
        fldValues = LPArray.addValueToArray1D(fldValues, new Object[]{false});
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInstrumentsData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{getName()}, "");
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENTS,
                EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length - 1].toString(), new Object[]{getName()}, null);
        }
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.TURN_OFF_LINE, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_OFF_LINE, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_OFF_LINE, new Object[]{getName()}, getName());
    }

    public InternalMessage startCalibration(Boolean isScheduled) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isDecommissioned)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();

        Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName() + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
                new Object[]{this.getName(), AppInstrumentsAuditEvents.CALIBRATION.toString(), ""}, new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()});

        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString()))) {
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_CALIBRATION, new Object[]{getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_CALIBRATION, new Object[]{getName()}, getName());
        }
        String[] fldNames = new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsInstrumentsData.InstrumentEvent.CREATED_ON.getName(), TblsInstrumentsData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues = new Object[]{this.getName(), AppInstrumentsAuditEvents.CALIBRATION.toString(), LPDate.getCurrentTimeStamp(), (Boolean.TRUE.equals(isScheduled)) ? GlobalVariables.TRAZIT_SCHEDULER : token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames, fldValues);
        if (Boolean.FALSE.equals(instCreationDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{getName()}, null);
        }
        String insEventIdCreated = instCreationDiagn.getNewRowId().toString();
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.START_CALIBRATION, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);

        String variableSetName = null;
        Integer fldPosic = LPArray.valuePosicInArray(this.familyFieldNames, TblsInstrumentsConfig.InstrumentsFamily.CALIB_VARIABLES_SET.getName());
        if (fldPosic > -1) {
            variableSetName = LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString();
        }
        if (variableSetName != null) {
            String ownerId = token.getPersonName();
            Integer instrEventId = Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(getName(), instrEventId, variableSetName, ownerId);
        }
        if (Boolean.TRUE.equals(this.onLine)) {
            fldNames = new String[]{TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
            fldValues = new Object[]{true, InstrLockingReasons.UNDER_CALIBRATION_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_CALIBRATION, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_CALIBRATION, new Object[]{getName()}, insEventIdCreated);
    }

    public InternalMessage completeCalibration(String decision) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) {
            return decisionValueIsCorrect;
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned != null && Boolean.TRUE.equals(this.isDecommissioned)) {
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();

        Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName() + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
                new Object[]{this.getName(), AppInstrumentsAuditEvents.CALIBRATION.toString(), ""},
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.ID.getName()});

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())) {
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_CALIBRATION, new Object[]{getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_CALIBRATION, new Object[]{getName()}, getName());
        }
        String instrName = instrEventInfo[0][0].toString();
        Integer eventId = Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), eventId);

        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())) {
            messages.addMainForError(eventHasNotEnteredVariables.getMessageCodeObj(), eventHasNotEnteredVariables.getMessageCodeVariables());
            return eventHasNotEnteredVariables;
        }

        String[] fldNames = new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues = new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInstrumentsData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENT_EVENT,
                EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length - 1].toString(), new Object[]{getName()}, null);
        }
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_CALIBRATION, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);
        fldNames = new String[]{TblsInstrumentsData.Instruments.LAST_CALIBRATION.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
        fldValues = new Object[]{LPDate.getCurrentTimeStamp(), false, ""};

        Date nextEventDate = nextEventDate(TblsInstrumentsConfig.InstrumentsFamily.CALIB_INTERVAL.getName());
        if (nextEventDate != null) {
            fldNames = LPArray.addValueToArray1D(fldNames, TblsInstrumentsData.Instruments.NEXT_CALIBRATION.getName());
            fldValues = LPArray.addValueToArray1D(fldValues, nextEventDate);
        }
        if (decision.toUpperCase().contains("REJEC")) {
            createInstrumentCorrectiveAction(instrName, eventId, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_CALIBRATION);
        }
        if (Boolean.FALSE.equals(this.onLine) && Boolean.TRUE.equals(decisionAndFamilyRuleToTurnOn(decision, TblsInstrumentsConfig.InstrumentsFamily.CALIB_TURN_ON_WHEN_COMPLETED.getName()))) {
            turnOnLine(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_CALIBRATION.toString());
        } else {
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_CALIBRATION.toString(), AppInstrumentsAuditEvents.COMPLETE_CALIBRATION);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_CALIBRATION, new Object[]{getName(), decision});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_CALIBRATION, new Object[]{getName(), decision}, getName());
    }

    public static InternalMessage createInstrumentCorrectiveAction(String instrName, Integer eventId, EnumIntEndpoints endpoint) {
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String createInvCorrectiveAction = Parameter.getBusinessRuleProcedureFile(procInstanceName, InstrumentsBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_EVENT.getAreaName(), InstrumentsBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_EVENT.getTagName());
        if (Boolean.FALSE.equals(isTagValueOneOfEnableOnes(createInvCorrectiveAction))) {
            messages.addMainForError(InvTrackingEnums.InventoryTrackingErrorTrapping.DISABLED, new Object[]{});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.DISABLED, new Object[]{});
        }
        return DataInstrumentsCorrectiveAction.createNew(eventId, endpoint,
                new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.INSTRUMENT.getName(), TblsInstrumentsProcedure.InstrumentsCorrectiveAction.OBJECT_TYPE.getName()},
                new Object[]{instrName, TblsInstrumentsData.TablesInstrumentsData.INSTRUMENT_EVENT.getTableName()});
    }

    public InternalMessage startPrevMaint(Boolean isScheduled) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isDecommissioned)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();

        Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName() + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
                new Object[]{this.getName(), AppInstrumentsAuditEvents.PREVENTIVE_MAINTENANCE.toString(), ""}, new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()});

        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString()))) {
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_PREV_MAINT, new Object[]{getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_PREV_MAINT, new Object[]{getName()}, getName());
        }
        String[] fldNames = new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsInstrumentsData.InstrumentEvent.CREATED_ON.getName(), TblsInstrumentsData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues = new Object[]{this.getName(), AppInstrumentsAuditEvents.PREVENTIVE_MAINTENANCE.toString(), LPDate.getCurrentTimeStamp(), (Boolean.TRUE.equals(isScheduled)) ? GlobalVariables.TRAZIT_SCHEDULER : token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames, fldValues);
        String insEventIdCreated = instCreationDiagn.getNewRowId().toString();
        if (Boolean.FALSE.equals(instCreationDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{getName()}, null);
        }
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.START_PREVENTIVE_MAINTENANCE, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);

        String variableSetName = null;
        Integer fldPosic = LPArray.valuePosicInArray(this.familyFieldNames, TblsInstrumentsConfig.InstrumentsFamily.PM_VARIABLES_SET.getName());
        if (fldPosic > -1) {
            variableSetName = LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString();
        }
        if (variableSetName != null) {
            String ownerId = token.getPersonName();
            Integer instrEventId = Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(getName(), instrEventId, variableSetName, ownerId);
        }

        if (Boolean.TRUE.equals(this.onLine)) {
            fldNames = new String[]{TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
            fldValues = new Object[]{true, InstrLockingReasons.UNDER_MAINTENANCE_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_PREVENTIVE_MAINTENANCE, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_PREVENTIVE_MAINTENANCE, new Object[]{getName()}, insEventIdCreated);
    }

    public InternalMessage completePrevMaint(String decision) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) {
            return decisionValueIsCorrect;
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned != null && Boolean.TRUE.equals(this.isDecommissioned)) {
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();

        Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName() + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
                new Object[]{this.getName(), AppInstrumentsAuditEvents.PREVENTIVE_MAINTENANCE.toString(), ""},
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.ID.getName()});

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())) {
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_PREV_MAINT, new Object[]{getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_PREV_MAINT, new Object[]{getName()}, getName());
        }
        String instrName = instrEventInfo[0][0].toString();
        Integer eventId = Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), eventId);

        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())) {
            return eventHasNotEnteredVariables;
        }

        String[] fldNames = new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues = new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInstrumentsData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENT_EVENT,
                EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length - 1].toString(), new Object[]{getName()}, null);
        }
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_PREVENTIVE_MAINTENANCE, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);
        fldNames = new String[]{TblsInstrumentsData.Instruments.LAST_PM.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
        fldValues = new Object[]{LPDate.getCurrentTimeStamp(), false, ""};

        if (decision.toUpperCase().contains("REJEC")) {
            createInstrumentCorrectiveAction(instrName, eventId, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_PREVENTIVE_MAINTENANCE);
        }

        Date nextEventDate = nextEventDate(TblsInstrumentsConfig.InstrumentsFamily.PM_INTERVAL.getName());
        if (nextEventDate != null) {
            fldNames = LPArray.addValueToArray1D(fldNames, TblsInstrumentsData.Instruments.NEXT_PM.getName());
            fldValues = LPArray.addValueToArray1D(fldValues, nextEventDate);
        }
        if (Boolean.FALSE.equals(this.onLine) && Boolean.TRUE.equals(decisionAndFamilyRuleToTurnOn(decision, TblsInstrumentsConfig.InstrumentsFamily.PM_TURN_ON_WHEN_COMPLETED.getName()))) {
            turnOnLine(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_PREVENTIVE_MAINTENANCE.toString());
        } else {
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_PREVENTIVE_MAINTENANCE.toString(), InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_PREVENTIVE_MAINTENANCE);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_PREVENTIVE_MAINTENANCE, new Object[]{getName(), decision});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_PREVENTIVE_MAINTENANCE, new Object[]{getName(), decision}, getName());
    }

    public InternalMessage startVerification() {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isDecommissioned)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();

        Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName() + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
                new Object[]{this.getName(), AppInstrumentsAuditEvents.VERIFICATION.toString(), ""}, new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()});

        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString()))) {
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_VERIFICATION, new Object[]{getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_VERIFICATION, new Object[]{getName()}, getName());
        }
        String[] fldNames = new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsInstrumentsData.InstrumentEvent.CREATED_ON.getName(), TblsInstrumentsData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues = new Object[]{this.getName(), AppInstrumentsAuditEvents.VERIFICATION.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesInstrumentsData.INSTRUMENT_EVENT,
                fldNames, fldValues);
        if (Boolean.FALSE.equals(instCreationDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{getName()}, null);
        }
        String insEventIdCreated = instCreationDiagn.getNewRowId().toString();
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.START_VERIFICATION, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);
        String variableSetName = null;
        Integer fldPosic = LPArray.valuePosicInArray(this.familyFieldNames, TblsInstrumentsConfig.InstrumentsFamily.VERIF_SAME_DAY_VARIABLES_SET.getName());
        if (fldPosic > -1) {
            variableSetName = LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString();
        }
        if (variableSetName != null) {
            String ownerId = token.getPersonName();
            Integer instrEventId = Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(getName(), instrEventId, variableSetName, ownerId);
        }

        if (Boolean.TRUE.equals(this.onLine)) {
            fldNames = new String[]{TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
            fldValues = new Object[]{true, InstrLockingReasons.UNDER_DAILY_VERIF_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_VERIFICATION, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_VERIFICATION, new Object[]{getName()}, insEventIdCreated);
    }

    public InternalMessage completeVerification(String decision) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) {
            return decisionValueIsCorrect;
        }

        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned != null && Boolean.TRUE.equals(this.isDecommissioned)) {
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();

        Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName() + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
                new Object[]{this.getName(), AppInstrumentsAuditEvents.VERIFICATION.toString(), ""},
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.ID.getName()});

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())) {
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_VERIFICATION, new Object[]{getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_VERIFICATION, new Object[]{getName()}, getName());
        }
        String instrName = instrEventInfo[0][0].toString();
        Integer eventId = Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), eventId);

        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())) {
            return eventHasNotEnteredVariables;
        }

        String[] fldNames = new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues = new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInstrumentsData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENT_EVENT,
                EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length - 1].toString(), new Object[]{getName()}, null);
        }
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_VERIFICATION, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);
        if (decision.toUpperCase().contains("REJEC")) {
            createInstrumentCorrectiveAction(instrName, eventId, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_VERIFICATION);
        }
        fldNames = new String[]{TblsInstrumentsData.Instruments.LAST_VERIF.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
        fldValues = new Object[]{LPDate.getCurrentTimeStamp(), false, ""};
        if (Boolean.FALSE.equals(this.onLine)) {
            turnOnLine(fldNames, fldValues);
        } else {
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_VERIFICATION.toString(), InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_VERIFICATION);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_VERIFICATION, new Object[]{getName(), decision});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_VERIFICATION, new Object[]{getName(), decision}, getName());
    }

    public InternalMessage startSevice() {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isDecommissioned)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();

        Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName() + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
                new Object[]{this.getName(), AppInstrumentsAuditEvents.SERVICE.toString(), ""}, new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()});

        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString()))) {
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_SERVICE, new Object[]{getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_SERVICE, new Object[]{getName()}, getName());
        }
        String[] fldNames = new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsInstrumentsData.InstrumentEvent.CREATED_ON.getName(), TblsInstrumentsData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues = new Object[]{this.getName(), AppInstrumentsAuditEvents.SERVICE.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames, fldValues);
        if (Boolean.FALSE.equals(instCreationDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{getName()}, null);
        }
        String insEventIdCreated = instCreationDiagn.getNewRowId().toString();
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.START_SERVICE, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);
        String variableSetName = null;
        Integer fldPosic = LPArray.valuePosicInArray(this.familyFieldNames, TblsInstrumentsConfig.InstrumentsFamily.SERVICE_VARIABLES_SET.getName());
        if (fldPosic > -1) {
            variableSetName = LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString();
        }
        if (variableSetName != null) {
            String ownerId = token.getPersonName();
            Integer instrEventId = Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(getName(), instrEventId, variableSetName, ownerId);
        }

        if (Boolean.TRUE.equals(this.onLine)) {
            fldNames = new String[]{TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
            fldValues = new Object[]{true, InstrLockingReasons.UNDER_SERVICE_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_SERVICE, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_SERVICE, new Object[]{getName()}, insEventIdCreated);
    }

    public InternalMessage completeService(String decision) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) {
            return decisionValueIsCorrect;
        }

        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned != null && Boolean.TRUE.equals(this.isDecommissioned)) {
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();

        Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName() + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
                new Object[]{this.getName(), AppInstrumentsAuditEvents.SERVICE.toString(), ""},
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.ID.getName()});

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())) {
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_SERVICE, new Object[]{getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_SERVICE, new Object[]{getName()}, getName());
        }
        String instrName = instrEventInfo[0][0].toString();
        Integer eventId = Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), eventId);

        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())) {
            return eventHasNotEnteredVariables;
        }

        String[] fldNames = new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues = new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInstrumentsData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENT_EVENT,
                EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length - 1].toString(), new Object[]{getName()}, null);
        }
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_SERVICE, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);
        if (decision.toUpperCase().contains("REJEC")) {
            createInstrumentCorrectiveAction(instrName, eventId, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_SERVICE);
        }
        fldNames = new String[]{TblsInstrumentsData.Instruments.LAST_VERIF.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
        fldValues = new Object[]{LPDate.getCurrentTimeStamp(), false, ""};
        if (Boolean.FALSE.equals(this.onLine)) {
            turnOnLine(fldNames, fldValues);
        } else {
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_SERVICE.toString(), InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_SERVICE);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_SERVICE, new Object[]{getName(), decision});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_SERVICE, new Object[]{getName(), decision}, getName());
    }

    public InternalMessage reopenEvent(Integer instrEventId) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isDecommissioned)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();

        Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.ID.getName()},
                new Object[]{this.getName(), instrEventId},
                new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())) {
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NOT_FOUND, new Object[]{getName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NOT_FOUND, new Object[]{getName()}, getName());
        }
        String eventCompletedOn = LPNulls.replaceNull(instrEventInfo[0][0]).toString();
        String eventDecision = LPNulls.replaceNull(instrEventInfo[0][1]).toString();
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), instrEventId);

        if (eventCompletedOn.length() == 0 || eventDecision.length() == 0) {
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId}, getName());
        }

        String[] fldNames = new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues = new Object[]{"NULL>>>STRING", "NULL>>>LOCALDATETIME", "NULL>>>STRING"};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInstrumentsData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{instrEventId}, "");
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENT_EVENT,
                EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length - 1].toString(), new Object[]{getName()}, null);
        }
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.REOPEN_EVENT, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);
        fldNames = new String[]{TblsInstrumentsData.Instruments.LAST_VERIF.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
        fldValues = new Object[]{LPDate.getCurrentTimeStamp(), false, ""};
        if (Boolean.TRUE.equals(this.onLine)) {
            turnOffLine(fldNames, fldValues);
        } else {
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.REOPEN_EVENT.toString(), InstrumentsEnums.AppInstrumentsAuditEvents.REOPEN_EVENT);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.REOPEN_EVENT, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.REOPEN_EVENT, new Object[]{getName()}, getName());
    }

    public InternalMessage addAttachment(Integer instrEventId, String attachUrl, String briefSummary) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isDecommissioned)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (instrEventId != null) {
            Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                    new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.ID.getName()},
                    new Object[]{this.getName(), instrEventId},
                    new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())) {
                messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NOT_FOUND, new Object[]{getName()});
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NOT_FOUND, new Object[]{getName()}, getName());
            }
            RelatedObjects rObj = RelatedObjects.getInstanceForActions();
            rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), instrEventId);
            /*if (eventCompletedOn.length() > 0 || eventDecision.length() > 0) {
                messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId});
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId}, name);
            }*/
        }
        String[] fldNames = new String[]{TblsInstrumentsData.InstrAttachments.INSTRUMENT_NAME.getName(), TblsInstrumentsData.InstrAttachments.FILE_LINK.getName(), 
            TblsInstrumentsData.InstrAttachments.CREATED_ON.getName(), TblsInstrumentsData.InstrAttachments.CREATED_BY.getName()};        
        Object[] fldValues = new Object[]{this.getName(), attachUrl, LPDate.getCurrentTimeStamp(), procReqSession.getToken().getPersonName()};
        if (instrEventId != null) {
            fldNames=LPArray.addValueToArray1D(fldNames, TblsInstrumentsData.InstrAttachments.EVENT_ID.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, instrEventId);
        }
        if (briefSummary != null) {
            fldNames=LPArray.addValueToArray1D(fldNames, TblsInstrumentsData.InstrAttachments.BRIEF_SUMMARY.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, briefSummary);
        }
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInstrumentsData.TablesInstrumentsData.INSTR_ATTACHMENT, 
                fldNames, fldValues);
        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
        }
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.ADDED_ATTACHMENT, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.ADD_ATTACHMENT, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.ADD_ATTACHMENT, new Object[]{getName()}, getName());
    }
    public InternalMessage removeAttachment(Integer instrEventId, Integer attachmentId) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isDecommissioned)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (instrEventId != null) {
            Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                    new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.ID.getName()},
                    new Object[]{this.getName(), instrEventId},
                    new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())) {
                messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NOT_FOUND, new Object[]{getName()});
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NOT_FOUND, new Object[]{getName()}, getName());
            }
            RelatedObjects rObj = RelatedObjects.getInstanceForActions();
            rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), instrEventId);
        }
        EnumIntTableFields[] fldNamesObj = new EnumIntTableFields[]{TblsInstrumentsData.InstrAttachments.REMOVED};
        Object[] fldValues = new Object[]{true};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInstrumentsData.InstrAttachments.INSTRUMENT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.getName()}, "");
        sqlWhere.addConstraint(TblsInstrumentsData.InstrAttachments.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{attachmentId}, "");
        if (instrEventId != null) {
        sqlWhere.addConstraint(TblsInstrumentsData.InstrAttachments.EVENT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{instrEventId}, "");    
        }
        
        RdbmsObject updateRecordInTable = Rdbms.updateTableRecordFieldsByFilter(TblsInstrumentsData.TablesInstrumentsData.INSTR_ATTACHMENT, 
                fldNamesObj, fldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(updateRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordInTable.getErrorMessageCode(), updateRecordInTable.getErrorMessageVariables(), null);
        }
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.REMOVED_ATTACHMENT, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                EnumIntTableFields.getAllFieldNames(fldNamesObj), fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.REMOVE_ATTACHMENT, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.REMOVE_ATTACHMENT, new Object[]{getName()}, getName());
    }
    public InternalMessage reactivateAttachment(Integer instrEventId, Integer attachmentId) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (Boolean.TRUE.equals(this.isDecommissioned)) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.getName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (instrEventId != null) {
            Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                    new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.ID.getName()},
                    new Object[]{this.getName(), instrEventId},
                    new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())) {
                messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NOT_FOUND, new Object[]{getName()});
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NOT_FOUND, new Object[]{getName()}, getName());
            }
            RelatedObjects rObj = RelatedObjects.getInstanceForActions();
            rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), instrEventId);
            /*if (eventCompletedOn.length() > 0 || eventDecision.length() > 0) {
                messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId});
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId}, name);
            }*/
        }
        EnumIntTableFields[] fldNamesObj = new EnumIntTableFields[]{TblsInstrumentsData.InstrAttachments.REMOVED};
        Object[] fldValues = new Object[]{false};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInstrumentsData.InstrAttachments.INSTRUMENT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.getName()}, "");
        sqlWhere.addConstraint(TblsInstrumentsData.InstrAttachments.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{attachmentId}, "");
        if (instrEventId != null) {
        sqlWhere.addConstraint(TblsInstrumentsData.InstrAttachments.EVENT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{instrEventId}, "");    
        }
        
        RdbmsObject updateRecordInTable = Rdbms.updateTableRecordFieldsByFilter(TblsInstrumentsData.TablesInstrumentsData.INSTR_ATTACHMENT, 
                fldNamesObj, fldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(updateRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordInTable.getErrorMessageCode(), updateRecordInTable.getErrorMessageVariables(), null);
        }
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.REACTIVATED_ATTACHMENT, getName(), TablesInstrumentsData.INSTRUMENTS.getTableName(), getName(),
                EnumIntTableFields.getAllFieldNames(fldNamesObj), fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.REACTIVATE_ATTACHMENT, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.REACTIVATE_ATTACHMENT, new Object[]{getName()}, getName());
    }

    public Boolean getHasError() {
        return hasError;
    }

    public InternalMessage getErrorDetail() {
        return errorDetail;
    }

    /**
     * @return the responsible
     */
    public String getResponsible() {
        return responsible;
    }

    /**
     * @return the responsibleBackup
     */
    public String getResponsibleBackup() {
        return responsibleBackup;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

}
