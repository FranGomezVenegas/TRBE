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
    private String responsible=null;
    private String responsibleBackup=null;
    public enum Decisions{ACCEPTED, ACCEPTED_WITH_RESTRICTIONS, REJECTED}

    private InternalMessage decisionValueIsCorrect(String decision){
        try{
            Decisions.valueOf(decision);
            return new InternalMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.CORRECT, null, null);
        }catch(Exception e){
            ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
            messages.addMainForError(InstrumentsErrorTrapping.WRONG_DECISION, new Object[]{decision, Arrays.toString(Decisions.values())});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.WRONG_DECISION, new Object[]{decision, Arrays.toString(Decisions.values())}, null);
        }
    }
    private Boolean decisionAndFamilyRuleToTurnOn(String decision, String fieldName){
        if (Boolean.FALSE.equals(decision.toUpperCase().contains("ACCEPT"))) return false;
        Integer fldPosic=LPArray.valuePosicInArray(this.familyFieldNames, fieldName);
        if (fldPosic==-1) return false;
        return Boolean.valueOf(LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString());
    }
    
    private Date nextEventDate(String fieldName){
        Integer fldPosic=LPArray.valuePosicInArray(this.familyFieldNames, fieldName);
        if (fldPosic==-1) return null;
        String intervalInfo = LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString();
        if (intervalInfo==null || intervalInfo.length()==0) return null;
        String[] intvlInfoArr=intervalInfo.split("\\*");
        if (intvlInfoArr.length!=2) return null;
        return LPDate.addIntervalToGivenDate(LPDate.getCurrentDateWithNoTime(), intvlInfoArr[0], Integer.valueOf(intvlInfoArr[1]));
    }
    
    public DataInstruments(String instrName){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null); 
        String procInstanceName=procReqSession.getProcedureInstance();
        Object[][] instrInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), 
                new String[]{TblsInstrumentsData.Instruments.NAME.getName()}, new Object[]{instrName}, getAllFieldNames(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())){
            this.name=null;
            this.hasError=true;
            this.errorDetail=new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{instrName, TablesInstrumentsData.INSTRUMENTS.getTableName(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())}, instrName);
        }else{
            this.hasError=false;
            this.fieldNames=getAllFieldNames(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS.getTableFields());
            this.fieldValues=instrInfo[0];
            this.name=instrName;
            this.onLine=Boolean.valueOf(LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInstrumentsData.Instruments.ON_LINE.getName())]).toString());
            if (this.onLine==null) this.onLine=false;
            this.isLocked= Boolean.valueOf(LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInstrumentsData.Instruments.IS_LOCKED.getName())]).toString());
            if (this.isLocked==null) this.isLocked=false;
            this.isDecommissioned= Boolean.valueOf(LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInstrumentsData.Instruments.DECOMMISSIONED.getName())]).toString());
            if (this.isDecommissioned==null) this.isDecommissioned=false;
            this.lockedReason=LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInstrumentsData.Instruments.LOCKED_REASON.getName())]).toString();
            if (Boolean.FALSE.equals(this.isLocked))
                responsibleLocking();
            this.family=LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInstrumentsData.Instruments.FAMILY.getName())]).toString();
            if (this.family!=null && this.family.length()>0){
                Object[][] instrFamilyInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableName(), 
                        new String[]{TblsInstrumentsConfig.InstrumentsFamily.NAME.getName()}, new Object[]{this.family}, getAllFieldNames(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableFields()));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrFamilyInfo[0][0].toString())){
                    familyFieldNames=null;
                    familyFieldValues=null;
                }else{
                    familyFieldNames=getAllFieldNames(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableFields());
                    familyFieldValues=instrFamilyInfo[0];
                }
            }
        }
    }    
    private void responsibleLocking(){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);    
        Token token = procReqSession.getToken();
        Integer respFldPosic=LPArray.valuePosicInArray(this.fieldNames, TblsInstrumentsData.Instruments.RESPONSIBLE.getName());
        Integer resp2FldPosic=LPArray.valuePosicInArray(this.fieldNames, TblsInstrumentsData.Instruments.RESPONSIBLE_BACKUP.getName());
        if (respFldPosic>-1){
            this.responsible=LPNulls.replaceNull(this.fieldValues[respFldPosic]).toString();
            if (LPNulls.replaceNull(this.fieldValues[respFldPosic]).toString().equalsIgnoreCase(token.getUserName())){
                this.isLocked=false;            
                return;
            }
        }
        if (resp2FldPosic>-1){
            this.responsibleBackup=LPNulls.replaceNull(this.fieldValues[resp2FldPosic]).toString();
            if (LPNulls.replaceNull(this.fieldValues[resp2FldPosic]).toString().equalsIgnoreCase(token.getUserName())){
                this.isLocked=false;
                return;            
            }
        }
        if ((this.responsible==null||this.responsible.length()==0)&&(this.responsibleBackup==null||this.responsibleBackup.length()==0)){
            this.isLocked=false;
            return;
        }
        this.isLocked=true;
        this.lockedReason="user is not responsible neither responsible backup";
        
    }
    public static InternalMessage createNewInstrument(String name, String familyName, String[] fldNames, Object[] fldValues){   
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        ResponseMessages messages = procReqSession.getMessages();
        Token token = procReqSession.getToken();
        if (fldNames==null){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }
        if (familyName!=null && familyName.length()>0){
            Object[][] instrFamilyInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableName(), 
                new String[]{TblsInstrumentsConfig.InstrumentsFamily.NAME.getName()}, new Object[]{familyName}, 
                getAllFieldNames(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableFields()));
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrFamilyInfo[0][0].toString())){
                messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.FAMILY_NOT_FOUND, new Object[]{familyName});                
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.FAMILY_NOT_FOUND, new Object[]{familyName}, null);            
            }
            fldNames=LPArray.addValueToArray1D(fldNames, TblsInstrumentsData.Instruments.FAMILY.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, familyName);
        }
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsInstrumentsData.Instruments.NAME.getName(), TblsInstrumentsData.Instruments.ON_LINE.getName(),
            TblsInstrumentsData.Instruments.CREATED_ON.getName(), TblsInstrumentsData.Instruments.CREATED_BY.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{name, false, LPDate.getCurrentTimeStamp(), token.getPersonName()});
        Object[] existsRecord = Rdbms.existsRecord(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS.getTableName(), 
                new String[]{TblsInstrumentsData.Instruments.NAME.getName()}, new Object[]{name});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_EXISTS, new Object[]{name}, null);
        
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesInstrumentsData.INSTRUMENTS, fldNames, fldValues);
        if (Boolean.FALSE.equals(instCreationDiagn.getRunSuccess()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.CREATION, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.NEW_INSTRUMENT, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.NEW_INSTRUMENT, new Object[]{name}, name);
    }
    public InternalMessage updateInstrument(String[] fldNames, Object[] fldValues){
        return updateInstrument(fldNames, fldValues, null, null);
    }
    public InternalMessage assignResponsible(String[] fldNames, Object[] fldValues){
        return updateInstrument(fldNames, fldValues, "ASSIGN_RESPONSIBLE", AppInstrumentsAuditEvents.RESPONSIBLE_ASSIGNED);
    }
    public InternalMessage changeResponsible(String[] fldNames, Object[] fldValues){
        return updateInstrument(fldNames, fldValues, "CHANGE_RESPONSIBLE", AppInstrumentsAuditEvents.RESPONSIBLE_CHANGED);
    }
    public InternalMessage assignResponsibleBackup(String[] fldNames, Object[] fldValues){
        return updateInstrument(fldNames, fldValues, "ASSIGN_RESPONSIBLE_BACKUP", AppInstrumentsAuditEvents.RESPONSIBLE_BACKUP_ASSIGNED);
    }
    public InternalMessage changeResponsibleBackup(String[] fldNames, Object[] fldValues){
        return updateInstrument(fldNames, fldValues, "CHANGE_RESPONSIBLE_BACKUP", AppInstrumentsAuditEvents.RESPONSIBLE_BACKUP_CHANGED);
    }
    
    public InternalMessage updateInstrument(String[] fldNames, Object[] fldValues, String actionName, AppInstrumentsAuditEvents eventObj){
        if (Boolean.TRUE.equals(this.isDecommissioned))
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        String[] reservedFldsNotUpdatable=new String[]{TblsInstrumentsData.Instruments.NAME.getName(), TblsInstrumentsData.Instruments.ON_LINE.getName()};
        String[] reservedFldsNotUpdatableFromActions=new String[]{TblsInstrumentsData.Instruments.NAME.getName(), TblsInstrumentsData.Instruments.ON_LINE.getName()};
        if (actionName!=null)reservedFldsNotUpdatable=reservedFldsNotUpdatableFromActions;
        for (String curFld: fldNames){
            if (LPArray.valueInArray(reservedFldsNotUpdatable, curFld))
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.TRYINGUPDATE_RESERVED_FIELD, new Object[]{curFld}, null);                
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsInstrumentsData.Instruments.ON_LINE.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{true});
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInstrumentsData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{name}, "");
	Object[] instUpdateDiagn=Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENTS,
		EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        if (eventObj==null)
            eventObj=InstrumentsEnums.AppInstrumentsAuditEvents.UPDATE_INSTRUMENT;
        instrumentsAuditAdd(eventObj, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
            fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.UPDATE_INSTRUMENT, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.UPDATE_INSTRUMENT, new Object[]{name}, name);
    }
    public InternalMessage decommissionInstrument(String[] fldNames, Object[] fldValues){
        if (Boolean.TRUE.equals(this.isDecommissioned))
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        String[] reservedFldsNotUpdatable=new String[]{TblsInstrumentsData.Instruments.NAME.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(),
            TblsInstrumentsData.Instruments.LOCKED_REASON.getName(), TblsInstrumentsData.Instruments.ON_LINE.getName()};
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        for (String curFld: fldNames){
            if (LPArray.valueInArray(reservedFldsNotUpdatable, curFld))
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.TRYINGUPDATE_RESERVED_FIELD, new Object[]{curFld}, null);                
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsInstrumentsData.Instruments.ON_LINE.getName(),
            TblsInstrumentsData.Instruments.DECOMMISSIONED.getName(), TblsInstrumentsData.Instruments.DECOMMISSIONED_ON.getName(),
            TblsInstrumentsData.Instruments.DECOMMISSIONED_BY.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(),
            TblsInstrumentsData.Instruments.LOCKED_REASON.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{false, true, LPDate.getCurrentTimeStamp(), token.getPersonName(),
            true, "decommissioned"});
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInstrumentsData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{name}, "");
	Object[] instUpdateDiagn=Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENTS,
		EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.DECOMMISSION, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
            fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.DECOMMISSION_INSTRUMENT, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.DECOMMISSION_INSTRUMENT, new Object[]{name}, name);
    }
    public InternalMessage unDecommissionInstrument(String[] fldNames, Object[] fldValues){
        if (Boolean.FALSE.equals(this.isDecommissioned))
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_DECOMMISSIONED, new Object[]{this.name}, null);
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        String[] reservedFldsNotUpdatable=new String[]{TblsInstrumentsData.Instruments.NAME.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(),
            TblsInstrumentsData.Instruments.LOCKED_REASON.getName(), TblsInstrumentsData.Instruments.ON_LINE.getName()};
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        for (String curFld: fldNames){
            if (LPArray.valueInArray(reservedFldsNotUpdatable, curFld))
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.TRYINGUPDATE_RESERVED_FIELD, new Object[]{curFld}, null);                
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsInstrumentsData.Instruments.ON_LINE.getName(),
            TblsInstrumentsData.Instruments.DECOMMISSIONED.getName(), TblsInstrumentsData.Instruments.DECOMMISSIONED_ON.getName(),
            TblsInstrumentsData.Instruments.DECOMMISSIONED_BY.getName(), 
            TblsInstrumentsData.Instruments.UNDECOMMISSIONED_ON.getName(),
            TblsInstrumentsData.Instruments.UNDECOMMISSIONED_BY.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(),
            TblsInstrumentsData.Instruments.LOCKED_REASON.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{false, false, "NULL>>>LOCALDATETIME", 
            "NULL>>>STRING",
            LPDate.getCurrentTimeStamp(), token.getPersonName(),false, ""});
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInstrumentsData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{name}, "");
	Object[] instUpdateDiagn=Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENTS,
		EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.UNDECOMMISSION, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
            fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.UNDECOMMISSION_INSTRUMENT, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.UNDECOMMISSION_INSTRUMENT, new Object[]{name}, name);
    }

    public InternalMessage turnOnLine(String[] fldNames, Object[] fldValues){
        return turnOnLine(fldNames, fldValues, null);
    }
    public InternalMessage turnOnLine(String[] fldNames, Object[] fldValues, String actionName){
        if (Boolean.TRUE.equals(this.isDecommissioned))
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsInstrumentsData.Instruments.ON_LINE.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{true});
        if (Boolean.TRUE.equals(this.onLine)){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_ONLINE, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_ONLINE, new Object[]{name}, null);
        }
        if (actionName==null && this.isLocked){
            messages.addMainForError(InstrumentsErrorTrapping.IS_LOCKED, new Object[]{name, this.lockedReason});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.IS_LOCKED, new Object[]{name, this.lockedReason}, null);
        }
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInstrumentsData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{name}, "");
	Object[] instUpdateDiagn=Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENTS,
		EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.TURN_ON_LINE, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
            fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_ON_LINE, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_ON_LINE, new Object[]{name}, name);
    }
    public InternalMessage turnOffLine(String[] fldNames, Object[] fldValues){
        if (Boolean.TRUE.equals(this.isDecommissioned))
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        if (Boolean.FALSE.equals(this.onLine)){
            messages.addMainForError(InstrumentsErrorTrapping.NOT_ONLINE, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_ONLINE, new Object[]{name}, null);
        }
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsInstrumentsData.Instruments.ON_LINE.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{false});
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInstrumentsData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{name}, "");
	Object[] instUpdateDiagn=Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENTS,
		EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.TURN_OFF_LINE, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_OFF_LINE, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_OFF_LINE, new Object[]{name}, name);
    }

    public InternalMessage startCalibration(Boolean isScheduled){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        if (Boolean.TRUE.equals(this.isDecommissioned))
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.CALIBRATION.toString(), ""}, new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()});
        
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString()))){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_CALIBRATION, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_CALIBRATION, new Object[]{name}, name);
        }        
        String[] fldNames=new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsInstrumentsData.InstrumentEvent.CREATED_ON.getName(), TblsInstrumentsData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.name, AppInstrumentsAuditEvents.CALIBRATION.toString(), LPDate.getCurrentTimeStamp(), (Boolean.TRUE.equals(isScheduled)) ? GlobalVariables.TRAZIT_SCHEDULER : token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames, fldValues);
        if (Boolean.FALSE.equals(instCreationDiagn.getRunSuccess()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{name}, null);
        String insEventIdCreated=instCreationDiagn.getNewRowId().toString();
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.START_CALIBRATION, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);
        
        String variableSetName=null;
        Integer fldPosic=LPArray.valuePosicInArray(this.familyFieldNames, TblsInstrumentsConfig.InstrumentsFamily.CALIB_VARIABLES_SET.getName());
        if (fldPosic>-1) 
            variableSetName=LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString();
        if (variableSetName!=null){            
            String ownerId= token.getPersonName();            
            Integer instrEventId=Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(name, instrEventId, variableSetName, ownerId);
        }
        if (Boolean.TRUE.equals(this.onLine)){
            fldNames=new String[]{TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
            fldValues=new Object[]{true, InstrLockingReasons.UNDER_CALIBRATION_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_CALIBRATION, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_CALIBRATION, new Object[]{name}, insEventIdCreated);
    }
    public InternalMessage completeCalibration(String decision){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) return decisionValueIsCorrect;
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned!=null && Boolean.TRUE.equals(this.isDecommissioned)){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(),
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.CALIBRATION.toString(), ""}, 
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_CALIBRATION, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_CALIBRATION, new Object[]{name}, name);
        }
        String instrName=instrEventInfo[0][0].toString();
        Integer eventId=Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), eventId);                
        
        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())){
            messages.addMainForError(eventHasNotEnteredVariables.getMessageCodeObj(), eventHasNotEnteredVariables.getMessageCodeVariables());            
            return eventHasNotEnteredVariables;
        }

        String[] fldNames=new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInstrumentsData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
	Object[] instCreationDiagn=Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENT_EVENT,
		EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_CALIBRATION, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsInstrumentsData.Instruments.LAST_CALIBRATION.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        
        Date nextEventDate = nextEventDate(TblsInstrumentsConfig.InstrumentsFamily.CALIB_INTERVAL.getName());
        if (nextEventDate!=null){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsInstrumentsData.Instruments.NEXT_CALIBRATION.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, nextEventDate);
        }
        if (Boolean.FALSE.equals(this.onLine) && Boolean.TRUE.equals(decisionAndFamilyRuleToTurnOn(decision, TblsInstrumentsConfig.InstrumentsFamily.CALIB_TURN_ON_WHEN_COMPLETED.getName())) ){
            turnOnLine(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_CALIBRATION.toString());
        }else{
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_CALIBRATION.toString(), AppInstrumentsAuditEvents.COMPLETE_CALIBRATION);            
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_CALIBRATION, new Object[]{name, decision});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_CALIBRATION, new Object[]{name, decision}, name);
    }

    public InternalMessage startPrevMaint(Boolean isScheduled){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        if (Boolean.TRUE.equals(this.isDecommissioned))
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.PREVENTIVE_MAINTENANCE.toString(), ""}, new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()});
        
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString()))){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_PREV_MAINT, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_PREV_MAINT, new Object[]{name}, name);
        }        
        String[] fldNames=new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsInstrumentsData.InstrumentEvent.CREATED_ON.getName(), TblsInstrumentsData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.name, AppInstrumentsAuditEvents.PREVENTIVE_MAINTENANCE.toString(), LPDate.getCurrentTimeStamp(), (Boolean.TRUE.equals(isScheduled)) ? GlobalVariables.TRAZIT_SCHEDULER : token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames, fldValues);
        String insEventIdCreated=instCreationDiagn.getNewRowId().toString();
        if (Boolean.FALSE.equals(instCreationDiagn.getRunSuccess()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.START_PREVENTIVE_MAINTENANCE, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);

        String variableSetName=null;
        Integer fldPosic=LPArray.valuePosicInArray(this.familyFieldNames, TblsInstrumentsConfig.InstrumentsFamily.PM_VARIABLES_SET.getName());
        if (fldPosic>-1) 
            variableSetName=LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString();
        if (variableSetName!=null){
            String ownerId= token.getPersonName();
            Integer instrEventId=Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(name, instrEventId, variableSetName, ownerId);
        }
        
        if (Boolean.TRUE.equals(this.onLine)){
            fldNames=new String[]{TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
            fldValues=new Object[]{true, InstrLockingReasons.UNDER_MAINTENANCE_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_PREVENTIVE_MAINTENANCE, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_PREVENTIVE_MAINTENANCE, new Object[]{name}, insEventIdCreated);
    }
    public InternalMessage completePrevMaint(String decision){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) return decisionValueIsCorrect;
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned!=null && Boolean.TRUE.equals(this.isDecommissioned)){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.PREVENTIVE_MAINTENANCE.toString(), ""}, 
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_PREV_MAINT, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_PREV_MAINT, new Object[]{name}, name);
        }
        String instrName=instrEventInfo[0][0].toString();
        Integer eventId=Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), eventId);
        
        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())) return eventHasNotEnteredVariables;
        
        String[] fldNames=new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInstrumentsData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
	Object[] instCreationDiagn=Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENT_EVENT,
		EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_PREVENTIVE_MAINTENANCE, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsInstrumentsData.Instruments.LAST_PM.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};

        Date nextEventDate = nextEventDate(TblsInstrumentsConfig.InstrumentsFamily.PM_INTERVAL.getName());
        if (nextEventDate!=null){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsInstrumentsData.Instruments.NEXT_PM.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, nextEventDate);
        }
        if (Boolean.FALSE.equals(this.onLine)  && Boolean.TRUE.equals(decisionAndFamilyRuleToTurnOn(decision, TblsInstrumentsConfig.InstrumentsFamily.PM_TURN_ON_WHEN_COMPLETED.getName())) ){
            turnOnLine(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_PREVENTIVE_MAINTENANCE.toString());
        }else{
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_PREVENTIVE_MAINTENANCE.toString(), InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_PREVENTIVE_MAINTENANCE);            
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_PREVENTIVE_MAINTENANCE, new Object[]{name, decision});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_PREVENTIVE_MAINTENANCE, new Object[]{name, decision}, name);
    }
    
    public InternalMessage startVerification(){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        if (Boolean.TRUE.equals(this.isDecommissioned))
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.VERIFICATION.toString(), ""}, new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()});
        
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString()))){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_VERIFICATION, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_VERIFICATION, new Object[]{name}, name);
        }        
        String[] fldNames=new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsInstrumentsData.InstrumentEvent.CREATED_ON.getName(), TblsInstrumentsData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.name, AppInstrumentsAuditEvents.VERIFICATION.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesInstrumentsData.INSTRUMENT_EVENT, 
                fldNames, fldValues);
        if (Boolean.FALSE.equals(instCreationDiagn.getRunSuccess()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{name}, null);
        String insEventIdCreated=instCreationDiagn.getNewRowId().toString();
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.START_VERIFICATION, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);
        String variableSetName=null;
        Integer fldPosic=LPArray.valuePosicInArray(this.familyFieldNames, TblsInstrumentsConfig.InstrumentsFamily.VERIF_SAME_DAY_VARIABLES_SET.getName());
        if (fldPosic>-1) 
            variableSetName=LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString();
        if (variableSetName!=null){
            String ownerId= token.getPersonName();
            Integer instrEventId=Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(name, instrEventId, variableSetName, ownerId);
        }
        
        if (Boolean.TRUE.equals(this.onLine)){
            fldNames=new String[]{TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
            fldValues=new Object[]{true, InstrLockingReasons.UNDER_DAILY_VERIF_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_VERIFICATION, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_VERIFICATION, new Object[]{name}, insEventIdCreated);
    }
    public InternalMessage completeVerification(String decision){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) return decisionValueIsCorrect;
        
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned!=null && Boolean.TRUE.equals(this.isDecommissioned)){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.VERIFICATION.toString(), ""}, 
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_VERIFICATION, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_VERIFICATION, new Object[]{name}, name);
        }
        String instrName=instrEventInfo[0][0].toString();
        Integer eventId=Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), eventId);                
        
        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())) return eventHasNotEnteredVariables;

        String[] fldNames=new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInstrumentsData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
	Object[] instCreationDiagn=Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENT_EVENT,
		EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_VERIFICATION, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsInstrumentsData.Instruments.LAST_VERIF.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        if (Boolean.FALSE.equals(this.onLine)){
            turnOnLine(fldNames, fldValues);
        }else{
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_VERIFICATION.toString(), InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_VERIFICATION);            
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_VERIFICATION, new Object[]{name, decision});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_VERIFICATION, new Object[]{name, decision}, name);
    }

    public InternalMessage startSevice(){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        if (Boolean.TRUE.equals(this.isDecommissioned))
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.SERVICE.toString(), ""}, new String[]{TblsInstrumentsData.InstrumentEvent.ID.getName()});
        
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString()))){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_SERVICE, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_SERVICE, new Object[]{name}, name);
        }        
        String[] fldNames=new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsInstrumentsData.InstrumentEvent.CREATED_ON.getName(), TblsInstrumentsData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.name, AppInstrumentsAuditEvents.SERVICE.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames, fldValues);
        if (Boolean.FALSE.equals(instCreationDiagn.getRunSuccess()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{name}, null);
        String insEventIdCreated=instCreationDiagn.getNewRowId().toString();
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.START_SERVICE, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);
        String variableSetName=null;
        Integer fldPosic=LPArray.valuePosicInArray(this.familyFieldNames, TblsInstrumentsConfig.InstrumentsFamily.SERVICE_VARIABLES_SET.getName());
        if (fldPosic>-1) 
            variableSetName=LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString();
        if (variableSetName!=null){
            String ownerId= token.getPersonName();
            Integer instrEventId=Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(name, instrEventId, variableSetName, ownerId);
        }
        
        if (Boolean.TRUE.equals(this.onLine)){
            fldNames=new String[]{TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
            fldValues=new Object[]{true, InstrLockingReasons.UNDER_SERVICE_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_SERVICE, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_SERVICE, new Object[]{name}, insEventIdCreated);
    }
    public InternalMessage completeService(String decision){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) return decisionValueIsCorrect;
        
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned!=null && Boolean.TRUE.equals(this.isDecommissioned)){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.SERVICE.toString(), ""}, 
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_SERVICE, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_SERVICE, new Object[]{name}, name);
        }
        String instrName=instrEventInfo[0][0].toString();
        Integer eventId=Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), eventId);                
        
        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())) return eventHasNotEnteredVariables;

        String[] fldNames=new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInstrumentsData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
	Object[] instCreationDiagn=Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENT_EVENT,
		EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_SERVICE, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsInstrumentsData.Instruments.LAST_VERIF.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        if (Boolean.FALSE.equals(this.onLine)){
            turnOnLine(fldNames, fldValues);
        }else{
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_SERVICE.toString(), InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_SERVICE);            
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_SERVICE, new Object[]{name, decision});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_SERVICE, new Object[]{name, decision}, name);
    }
    
    public InternalMessage reopenEvent(Integer instrEventId){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        if (Boolean.TRUE.equals(this.isDecommissioned))
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
       
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.ID.getName()}, 
                new Object[]{this.name, instrEventId}, 
                new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName()});        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NOT_FOUND, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NOT_FOUND, new Object[]{name}, name);
        }
        String eventCompletedOn=LPNulls.replaceNull(instrEventInfo[0][0]).toString();
        String eventDecision=LPNulls.replaceNull(instrEventInfo[0][1]).toString();
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), instrEventId);                
        
        if (eventCompletedOn.length()==0 || eventDecision.length()==0){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId}, name);
        }

        String[] fldNames=new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName(), TblsInstrumentsData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{"NULL>>>STRING", "NULL>>>LOCALDATETIME", "NULL>>>STRING"};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInstrumentsData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{instrEventId}, "");
	Object[] instCreationDiagn=Rdbms.updateRecordFieldsByFilter(TablesInstrumentsData.INSTRUMENT_EVENT,
		EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.REOPEN_EVENT, name, TablesInstrumentsData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsInstrumentsData.Instruments.LAST_VERIF.getName(), TblsInstrumentsData.Instruments.IS_LOCKED.getName(), TblsInstrumentsData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        if (Boolean.TRUE.equals(this.onLine)){
            turnOffLine(fldNames, fldValues);
        }else{
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.REOPEN_EVENT.toString(), InstrumentsEnums.AppInstrumentsAuditEvents.REOPEN_EVENT);            
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.REOPEN_EVENT, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.REOPEN_EVENT, new Object[]{name}, name);
    }

    public Boolean getHasError() {        return hasError;    }
    public InternalMessage getErrorDetail() {        return errorDetail;    }

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

}
