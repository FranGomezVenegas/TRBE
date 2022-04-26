/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.instruments;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.TblsAppProcConfig;
import databases.TblsAppProcData;
import databases.TblsAppProcData.TablesAppProcData;
import databases.features.Token;
import static functionaljavaa.audit.AppInstrumentsAudit.instrumentsAuditAdd;
import static functionaljavaa.instruments.DataInstrumentsEvents.addVariableSetToObject;
import static functionaljavaa.instruments.DataInstrumentsEvents.eventHasNotEnteredVariables;
import functionaljavaa.instruments.InstrumentsEnums.AppInstrumentsAuditEvents;
import functionaljavaa.instruments.InstrumentsEnums.InstrLockingReasons;
import functionaljavaa.instruments.InstrumentsEnums.InstrumentsErrorTrapping;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.util.Arrays;
import java.util.Date;
import trazit.session.ResponseMessages;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.LpPlatformSuccess;
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
        if (!decision.toUpperCase().contains("ACCEPT")) return false;
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
        Object[][] instrInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                new String[]{TblsAppProcData.Instruments.NAME.getName()}, new Object[]{instrName}, getAllFieldNames(TblsAppProcData.TablesAppProcData.INSTRUMENTS.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())){
            this.name=null;
        }else{
            this.fieldNames=getAllFieldNames(TblsAppProcData.TablesAppProcData.INSTRUMENTS.getTableFields());
            this.fieldValues=instrInfo[0];
            this.name=instrName;
            this.onLine=Boolean.valueOf(LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsAppProcData.Instruments.ON_LINE.getName())]).toString());
            if (this.onLine==null) this.onLine=false;
            this.isLocked= Boolean.valueOf(LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsAppProcData.Instruments.IS_LOCKED.getName())]).toString());
            if (this.isLocked==null) this.isLocked=false;
            this.isDecommissioned= Boolean.valueOf(LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsAppProcData.Instruments.DECOMMISSIONED.getName())]).toString());
            if (this.isDecommissioned==null) this.isDecommissioned=false;
            this.lockedReason=LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsAppProcData.Instruments.LOCKED_REASON.getName())]).toString();
            this.family=LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsAppProcData.Instruments.FAMILY.getName())]).toString();
            if (this.family!=null && this.family.length()>0){
                Object[][] instrFamilyInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_CONFIG.getName(), TblsAppProcConfig.TablesAppProcConfig.INSTRUMENTS_FAMILY.getTableName(), 
                        new String[]{TblsAppProcConfig.InstrumentsFamily.NAME.getName()}, new Object[]{this.family}, getAllFieldNames(TblsAppProcConfig.TablesAppProcConfig.INSTRUMENTS_FAMILY.getTableFields()));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrFamilyInfo[0][0].toString())){
                    familyFieldNames=null;
                    familyFieldValues=null;
                }else{
                    familyFieldNames=getAllFieldNames(TblsAppProcConfig.TablesAppProcConfig.INSTRUMENTS_FAMILY.getTableFields());
                    familyFieldValues=instrFamilyInfo[0];
                }
            }
        }
    }    
    
    public static InternalMessage createNewInstrument(String name, String familyName, String[] fldNames, Object[] fldValues){   
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        if (fldNames==null){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }
        if (familyName!=null && familyName.length()>0){
            Object[][] instrFamilyInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_CONFIG.getName(), TblsAppProcConfig.TablesAppProcConfig.INSTRUMENTS_FAMILY.getTableName(), 
                new String[]{TblsAppProcConfig.InstrumentsFamily.NAME.getName()}, new Object[]{familyName}, 
                getAllFieldNames(TblsAppProcConfig.TablesAppProcConfig.INSTRUMENTS_FAMILY.getTableFields()));
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrFamilyInfo[0][0].toString())){
                messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.FAMILY_NOT_FOUND, new Object[]{familyName});                
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.FAMILY_NOT_FOUND, new Object[]{familyName}, null);            
            }
            fldNames=LPArray.addValueToArray1D(fldNames, TblsAppProcData.Instruments.FAMILY.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, familyName);
        }
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.NAME.getName(), TblsAppProcData.Instruments.ON_LINE.getName(),
            TblsAppProcData.Instruments.CREATED_ON.getName(), TblsAppProcData.Instruments.CREATED_BY.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{name, false, LPDate.getCurrentTimeStamp(), token.getPersonName()});
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesAppProcData.INSTRUMENTS, fldNames, fldValues);
        if (!instCreationDiagn.getRunSuccess())
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.CREATION, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.NEW_INSTRUMENT, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.NEW_INSTRUMENT, new Object[]{name}, name);
    }
    public InternalMessage updateInstrument(String[] fldNames, Object[] fldValues){
        return updateInstrument(fldNames, fldValues, null);
    }
    public InternalMessage updateInstrument(String[] fldNames, Object[] fldValues, String actionName){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        String[] reservedFldsNotUpdatable=new String[]{TblsAppProcData.Instruments.NAME.getName(), TblsAppProcData.Instruments.ON_LINE.getName()};
        String[] reservedFldsNotUpdatableFromActions=new String[]{TblsAppProcData.Instruments.NAME.getName(), TblsAppProcData.Instruments.ON_LINE.getName()};
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
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.ON_LINE.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{true});
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                fldNames, fldValues, new String[]{TblsAppProcData.Instruments.NAME.getName()},new Object[]{name});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.UPDATE_INSTRUMENT, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
            fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.UPDATE_INSTRUMENT, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.UPDATE_INSTRUMENT, new Object[]{name}, name);
    }
    public InternalMessage decommissionInstrument(String[] fldNames, Object[] fldValues){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        String[] reservedFldsNotUpdatable=new String[]{TblsAppProcData.Instruments.NAME.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(),
            TblsAppProcData.Instruments.LOCKED_REASON.getName(), TblsAppProcData.Instruments.ON_LINE.getName()};
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        for (String curFld: fldNames){
            if (LPArray.valueInArray(reservedFldsNotUpdatable, curFld))
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.TRYINGUPDATE_RESERVED_FIELD, new Object[]{curFld}, null);                
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.ON_LINE.getName(),
            TblsAppProcData.Instruments.DECOMMISSIONED.getName(), TblsAppProcData.Instruments.DECOMMISSIONED_ON.getName(),
            TblsAppProcData.Instruments.DECOMMISSIONED_BY.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(),
            TblsAppProcData.Instruments.LOCKED_REASON.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{false, true, LPDate.getCurrentTimeStamp(), token.getPersonName(),
            true, "decommissioned"});
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                fldNames, fldValues, new String[]{TblsAppProcData.Instruments.NAME.getName()},new Object[]{name});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.DECOMMISSION, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
            fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.DECOMMISSION_INSTRUMENT, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.DECOMMISSION_INSTRUMENT, new Object[]{name}, name);
    }
    public InternalMessage unDecommissionInstrument(String[] fldNames, Object[] fldValues){
        if (!this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_DECOMMISSIONED, new Object[]{this.name}, null);
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        String[] reservedFldsNotUpdatable=new String[]{TblsAppProcData.Instruments.NAME.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(),
            TblsAppProcData.Instruments.LOCKED_REASON.getName(), TblsAppProcData.Instruments.ON_LINE.getName()};
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        for (String curFld: fldNames){
            if (LPArray.valueInArray(reservedFldsNotUpdatable, curFld))
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.TRYINGUPDATE_RESERVED_FIELD, new Object[]{curFld}, null);                
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.ON_LINE.getName(),
            TblsAppProcData.Instruments.DECOMMISSIONED.getName(), TblsAppProcData.Instruments.DECOMMISSIONED_ON.getName(),
            TblsAppProcData.Instruments.DECOMMISSIONED_BY.getName(), 
            TblsAppProcData.Instruments.UNDECOMMISSIONED_ON.getName(),
            TblsAppProcData.Instruments.UNDECOMMISSIONED_BY.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(),
            TblsAppProcData.Instruments.LOCKED_REASON.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{false, false, "NULL>>>LOCALDATETIME", 
            "NULL>>>STRING",
            LPDate.getCurrentTimeStamp(), token.getPersonName(),false, ""});
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                fldNames, fldValues, new String[]{TblsAppProcData.Instruments.NAME.getName()},new Object[]{name});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.UNDECOMMISSION, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
            fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.UNDECOMMISSION_INSTRUMENT, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.UNDECOMMISSION_INSTRUMENT, new Object[]{name}, name);
    }

    public InternalMessage turnOnLine(String[] fldNames, Object[] fldValues){
        return turnOnLine(fldNames, fldValues, null);
    }
    public InternalMessage turnOnLine(String[] fldNames, Object[] fldValues, String actionName){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.ON_LINE.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{true});
        if (this.onLine){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_ONLINE, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_ONLINE, new Object[]{name}, null);
        }
        if (actionName==null && this.isLocked){
            messages.addMainForError(InstrumentsErrorTrapping.IS_LOCKED, new Object[]{name, this.lockedReason});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.IS_LOCKED, new Object[]{name, this.lockedReason}, null);
        }
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                fldNames, fldValues, new String[]{TblsAppProcData.Instruments.NAME.getName()},new Object[]{name});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.TURN_ON_LINE, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
            fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_ON_LINE, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_ON_LINE, new Object[]{name}, name);
    }
    public InternalMessage turnOffLine(String[] fldNames, Object[] fldValues){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        if (!this.onLine){
            messages.addMainForError(InstrumentsErrorTrapping.NOT_ONLINE, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_ONLINE, new Object[]{name}, null);
        }
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.ON_LINE.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{false});
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                fldNames, fldValues, new String[]{TblsAppProcData.Instruments.NAME.getName()},new Object[]{name});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.TURN_OFF_LINE, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_OFF_LINE, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_OFF_LINE, new Object[]{name}, name);
    }

    public InternalMessage startCalibration(){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.CALIBRATION.toString(), ""}, new String[]{TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_CALIBRATION, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_CALIBRATION, new Object[]{name}, name);
        }        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsAppProcData.InstrumentEvent.CREATED_ON.getName(), TblsAppProcData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.name, AppInstrumentsAuditEvents.CALIBRATION.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesAppProcData.INSTRUMENT_EVENT, fldNames, fldValues);
        if (!instCreationDiagn.getRunSuccess())
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{name}, null);
        String insEventIdCreated=instCreationDiagn.getNewRowId().toString();
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.START_CALIBRATION, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);
        
        String variableSetName=null;
        Integer fldPosic=LPArray.valuePosicInArray(this.familyFieldNames, TblsAppProcConfig.InstrumentsFamily.CALIB_VARIABLES_SET.getName());
        if (fldPosic>-1) 
            variableSetName=LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString();
        if (variableSetName!=null){
            String ownerId= token.getPersonName();
            Integer instrEventId=Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(name, instrEventId, variableSetName, ownerId);
        }
        if (this.onLine){
            fldNames=new String[]{TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
            fldValues=new Object[]{true, InstrLockingReasons.UNDER_CALIBRATION_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_CALIBRATION, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_CALIBRATION, new Object[]{name}, insEventIdCreated);
    }
    public InternalMessage completeCalibration(String decision){
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) return decisionValueIsCorrect;
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned!=null && this.isDecommissioned){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(),
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.CALIBRATION.toString(), ""}, 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_CALIBRATION, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_CALIBRATION, new Object[]{name}, name);
        }
        String instrName=instrEventInfo[0][0].toString();
        Integer eventId=Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), eventId);                
        
        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())){
            messages.addMainForError(eventHasNotEnteredVariables.getMessageCodeObj(), eventHasNotEnteredVariables.getMessageCodeVariables());            
            return eventHasNotEnteredVariables;
        }

        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.DECISION.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                fldNames, fldValues, 
                new String[]{TblsAppProcData.InstrumentEvent.ID.getName()}, new Object[]{eventId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_CALIBRATION, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsAppProcData.Instruments.LAST_CALIBRATION.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        
        Date nextEventDate = nextEventDate(TblsAppProcConfig.InstrumentsFamily.CALIB_INTERVAL.getName());
        if (nextEventDate!=null){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsAppProcData.Instruments.NEXT_CALIBRATION.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, nextEventDate);
        }
        if (!this.onLine && decisionAndFamilyRuleToTurnOn(decision, TblsAppProcConfig.InstrumentsFamily.CALIB_TURN_ON_WHEN_COMPLETED.getName())){
            turnOnLine(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_CALIBRATION.toString());
        }else{
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_CALIBRATION.toString());            
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_CALIBRATION, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_CALIBRATION, new Object[]{name}, name);
    }

    public InternalMessage startPrevMaint(){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.PREVENTIVE_MAINTENANCE.toString(), ""}, new String[]{TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_PREV_MAINT, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_PREV_MAINT, new Object[]{name}, name);
        }        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsAppProcData.InstrumentEvent.CREATED_ON.getName(), TblsAppProcData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.name, AppInstrumentsAuditEvents.PREVENTIVE_MAINTENANCE.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesAppProcData.INSTRUMENT_EVENT, fldNames, fldValues);
        String insEventIdCreated=instCreationDiagn.getNewRowId().toString();
        if (!instCreationDiagn.getRunSuccess())
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.START_PREVENTIVE_MAINTENANCE, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);

        String variableSetName=null;
        Integer fldPosic=LPArray.valuePosicInArray(this.familyFieldNames, TblsAppProcConfig.InstrumentsFamily.PM_VARIABLES_SET.getName());
        if (fldPosic>-1) 
            variableSetName=LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString();
        if (variableSetName!=null){
            String ownerId= token.getPersonName();
            Integer instrEventId=Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(name, instrEventId, variableSetName, ownerId);
        }
        
        if (this.onLine){
            fldNames=new String[]{TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
            fldValues=new Object[]{true, InstrLockingReasons.UNDER_MAINTENANCE_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_PREV_MAINT, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_PREV_MAINT, new Object[]{name}, insEventIdCreated);
    }
    public InternalMessage completePrevMaint(String decision){
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) return decisionValueIsCorrect;
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned!=null && this.isDecommissioned){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.PREVENTIVE_MAINTENANCE.toString(), ""}, 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_PREV_MAINT, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_PREV_MAINT, new Object[]{name}, name);
        }
        String instrName=instrEventInfo[0][0].toString();
        Integer eventId=Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), eventId);
        
        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())) return eventHasNotEnteredVariables;
        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.DECISION.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                fldNames, fldValues, 
                new String[]{TblsAppProcData.InstrumentEvent.ID.getName()}, new Object[]{eventId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_PREVENTIVE_MAINTENANCE, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsAppProcData.Instruments.LAST_PM.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};

        Date nextEventDate = nextEventDate(TblsAppProcConfig.InstrumentsFamily.PM_INTERVAL.getName());
        if (nextEventDate!=null){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsAppProcData.Instruments.NEXT_PM.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, nextEventDate);
        }
        if (!this.onLine  && decisionAndFamilyRuleToTurnOn(decision, TblsAppProcConfig.InstrumentsFamily.PM_TURN_ON_WHEN_COMPLETED.getName())){
            turnOnLine(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_PREVENTIVE_MAINTENANCE.toString());
        }else{
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_PREVENTIVE_MAINTENANCE.toString());            
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_PREV_MAINT, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_PREV_MAINT, new Object[]{name}, name);
    }
    
    public InternalMessage startVerification(){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.VERIFICATION.toString(), ""}, new String[]{TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_VERIFICATION, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_VERIFICATION, new Object[]{name}, name);
        }        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsAppProcData.InstrumentEvent.CREATED_ON.getName(), TblsAppProcData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.name, AppInstrumentsAuditEvents.VERIFICATION.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesAppProcData.INSTRUMENT_EVENT, 
                fldNames, fldValues);
        if (!instCreationDiagn.getRunSuccess())
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{name}, null);
        String insEventIdCreated=instCreationDiagn.getNewRowId().toString();
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.START_VERIFICATION, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);
        String variableSetName=null;
        Integer fldPosic=LPArray.valuePosicInArray(this.familyFieldNames, TblsAppProcConfig.InstrumentsFamily.VERIF_SAME_DAY_VARIABLES_SET.getName());
        if (fldPosic>-1) 
            variableSetName=LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString();
        if (variableSetName!=null){
            String ownerId= token.getPersonName();
            Integer instrEventId=Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(name, instrEventId, variableSetName, ownerId);
        }
        
        if (this.onLine){
            fldNames=new String[]{TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
            fldValues=new Object[]{true, InstrLockingReasons.UNDER_DAILY_VERIF_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_VERIFICATION, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_VERIFICATION, new Object[]{name}, insEventIdCreated);
    }
    public InternalMessage completeVerification(String decision){
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) return decisionValueIsCorrect;
        
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned!=null && this.isDecommissioned){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.VERIFICATION.toString(), ""}, 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_VERIFICATION, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_VERIFICATION, new Object[]{name}, name);
        }
        String instrName=instrEventInfo[0][0].toString();
        Integer eventId=Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), eventId);                
        
        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())) return eventHasNotEnteredVariables;

        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.DECISION.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                fldNames, fldValues, 
                new String[]{TblsAppProcData.InstrumentEvent.ID.getName()}, new Object[]{eventId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_VERIFICATION, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsAppProcData.Instruments.LAST_VERIF.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        if (!this.onLine){
            turnOnLine(fldNames, fldValues);
        }else{
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_VERIFICATION.toString());            
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_VERIFICATION, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_VERIFICATION, new Object[]{name}, name);
    }

    public InternalMessage startSevice(){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.SERVICE.toString(), ""}, new String[]{TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_SERVICE, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_SERVICE, new Object[]{name}, name);
        }        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsAppProcData.InstrumentEvent.CREATED_ON.getName(), TblsAppProcData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.name, AppInstrumentsAuditEvents.SERVICE.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesAppProcData.INSTRUMENT_EVENT, fldNames, fldValues);
        if (!instCreationDiagn.getRunSuccess())
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{name}, null);
        String insEventIdCreated=instCreationDiagn.getNewRowId().toString();
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.START_SERVICE, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);
        String variableSetName=null;
        Integer fldPosic=LPArray.valuePosicInArray(this.familyFieldNames, TblsAppProcConfig.InstrumentsFamily.SERVICE_VARIABLES_SET.getName());
        if (fldPosic>-1) 
            variableSetName=LPNulls.replaceNull(this.familyFieldValues[fldPosic]).toString();
        if (variableSetName!=null){
            String ownerId= token.getPersonName();
            Integer instrEventId=Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(name, instrEventId, variableSetName, ownerId);
        }
        
        if (this.onLine){
            fldNames=new String[]{TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
            fldValues=new Object[]{true, InstrLockingReasons.UNDER_SERVICE_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_SERVICE, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_SERVICE, new Object[]{name}, insEventIdCreated);
    }
    public InternalMessage completeService(String decision){
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) return decisionValueIsCorrect;
        
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned!=null && this.isDecommissioned){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, AppInstrumentsAuditEvents.SERVICE.toString(), ""}, 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_SERVICE, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_SERVICE, new Object[]{name}, name);
        }
        String instrName=instrEventInfo[0][0].toString();
        Integer eventId=Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), eventId);                
        
        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())) return eventHasNotEnteredVariables;

        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.DECISION.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                fldNames, fldValues, 
                new String[]{TblsAppProcData.InstrumentEvent.ID.getName()}, new Object[]{eventId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_SERVICE, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsAppProcData.Instruments.LAST_VERIF.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        if (!this.onLine){
            turnOnLine(fldNames, fldValues);
        }else{
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.COMPLETE_SERVICE.toString());            
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_SERVICE, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_SERVICE, new Object[]{name}, name);
    }
    
    public InternalMessage reopenEvent(Integer instrEventId){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.name}, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.ID.getName()}, 
                new Object[]{this.name, instrEventId}, 
                new String[]{TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.DECISION.getName()});        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NOT_FOUND, new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NOT_FOUND, new Object[]{name}, name);
        }
        String eventCompletedOn=LPNulls.replaceNull(instrEventInfo[0][0]).toString();
        String eventDecision=LPNulls.replaceNull(instrEventInfo[0][1]).toString();
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), instrEventId);                
        
        if (eventCompletedOn.length()==0 || eventDecision.length()==0){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId}, name);
        }

        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.DECISION.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{"NULL>>>STRING", "NULL>>>LOCALDATETIME", "NULL>>>STRING"};
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                fldNames, fldValues, 
                new String[]{TblsAppProcData.InstrumentEvent.ID.getName()}, new Object[]{instrEventId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.REOPEN_EVENT, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsAppProcData.Instruments.LAST_VERIF.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        if (this.onLine){
            turnOffLine(fldNames, fldValues);
        }else{
            updateInstrument(fldNames, fldValues, InstrumentsEnums.AppInstrumentsAuditEvents.REOPEN_EVENT.toString());            
        }
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.REOPEN_EVENT, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.REOPEN_EVENT, new Object[]{name}, name);
    }

}
