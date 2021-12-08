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
import databases.Token;
import static functionaljavaa.audit.AppInstrumentsAudit.instrumentsAuditAdd;
import functionaljavaa.instruments.InstrumentsEnums.InstrumentEvents;
import functionaljavaa.instruments.InstrumentsEnums.InstrumentsErrorTrapping;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import trazit.session.ResponseMessages;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */

public class DataInstruments {
    private String name;
    private Boolean onLine;
    private Boolean isLocked;
    private Boolean isDecommissioned;
    private String lockedReason;
    private String[] fieldNames;
    private Object[] fieldValues;
    private String family;
    private String[] familyFieldNames;
    private Object[] familyFieldValues;
    
    
    
    public DataInstruments(String instrName){
        Object[][] instrInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                new String[]{TblsAppProcData.Instruments.FLD_NAME.getName()}, new Object[]{instrName}, TblsAppProcData.Instruments.getAllFieldNames());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())){
            this.name=null;
        }else{
            this.fieldNames=TblsAppProcData.Instruments.getAllFieldNames();
            this.fieldValues=instrInfo[0];
            this.name=instrName;
            this.onLine=Boolean.valueOf(LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsAppProcData.Instruments.FLD_ON_LINE.getName())]).toString());
            this.isLocked= Boolean.valueOf(LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsAppProcData.Instruments.FLD_IS_LOCKED.getName())]).toString());
            this.isDecommissioned= Boolean.valueOf(LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsAppProcData.Instruments.FLD_DECOMMISSIONED.getName())]).toString());
            this.lockedReason=LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsAppProcData.Instruments.FLD_LOCKED_REASON.getName())]).toString();
            this.family=LPNulls.replaceNull(instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsAppProcData.Instruments.FLD_FAMILY.getName())]).toString();
            if (this.family!=null && this.family.length()>0){
                Object[][] instrFamilyInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_CONFIG.getName(), TblsAppProcConfig.InstrumentsFamily.TBL.getName(), 
                        new String[]{TblsAppProcConfig.InstrumentsFamily.FLD_NAME.getName()}, new Object[]{this.family}, TblsAppProcConfig.InstrumentsFamily.getAllFieldNames());
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrFamilyInfo[0][0].toString())){
                    familyFieldNames=null;
                    familyFieldValues=null;
                }else{
                    familyFieldNames=TblsAppProcConfig.InstrumentsFamily.getAllFieldNames();
                    familyFieldValues=instrFamilyInfo[0];
                }
            }
        }
    }    
    
    public static InternalMessage createNewInstrument(String name, String[] fldNames, Object[] fldValues){   
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        if (fldNames==null){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.FLD_NAME.getName(), TblsAppProcData.Instruments.FLD_ON_LINE.getName(),
            TblsAppProcData.Instruments.FLD_CREATED_ON.getName(), TblsAppProcData.Instruments.FLD_CREATED_BY.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{name, false, LPDate.getCurrentTimeStamp(), token.getPersonName()});
        Object[] instCreationDiagn = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.CREATION.toString(), name, TblsAppProcData.Instruments.TBL.getName(), name,
                        fldNames, fldValues);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        messages.addMainForSuccess("configInstruments", InstrumentsEnums.InstrumentsAPIactionsEndpoints.NEW_INSTRUMENT.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.NEW_INSTRUMENT.getSuccessMessageCode(), new Object[]{name}, name);
    }
    public InternalMessage updateInstrument(String[] fldNames, Object[] fldValues){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED.getErrorCode(), new Object[]{this.name}, null);
        String[] reservedFldsNotUpdatable=new String[]{TblsAppProcData.Instruments.FLD_NAME.getName(), TblsAppProcData.Instruments.FLD_IS_LOCKED.getName(),
            TblsAppProcData.Instruments.FLD_LOCKED_REASON.getName(), TblsAppProcData.Instruments.FLD_ON_LINE.getName()};
        for (String curFld: fldNames){
            if (LPArray.valueInArray(reservedFldsNotUpdatable, curFld))
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.TRYINGUPDATE_RESERVED_FIELD.getErrorCode(), new Object[]{curFld}, null);                
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.FLD_ON_LINE.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{true});
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                fldNames, fldValues, new String[]{TblsAppProcData.Instruments.FLD_NAME.getName()},new Object[]{name});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.UPDATE_INSTRUMENT.toString(), name, TblsAppProcData.Instruments.TBL.getName(), name,
            fldNames, fldValues);
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.UPDATE_INSTRUMENT.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.UPDATE_INSTRUMENT.getSuccessMessageCode(), new Object[]{name}, name);
    }
    public InternalMessage decommissionInstrument(String[] fldNames, Object[] fldValues){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED.getErrorCode(), new Object[]{this.name}, null);
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        String[] reservedFldsNotUpdatable=new String[]{TblsAppProcData.Instruments.FLD_NAME.getName(), TblsAppProcData.Instruments.FLD_IS_LOCKED.getName(),
            TblsAppProcData.Instruments.FLD_LOCKED_REASON.getName(), TblsAppProcData.Instruments.FLD_ON_LINE.getName()};
        for (String curFld: fldNames){
            if (LPArray.valueInArray(reservedFldsNotUpdatable, curFld))
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.TRYINGUPDATE_RESERVED_FIELD.getErrorCode(), new Object[]{curFld}, null);                
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.FLD_ON_LINE.getName(),
            TblsAppProcData.Instruments.FLD_DECOMMISSIONED.getName(), TblsAppProcData.Instruments.FLD_DECOMMISSIONED_ON.getName(),
            TblsAppProcData.Instruments.FLD_DECOMMISSIONED_BY.getName(), TblsAppProcData.Instruments.FLD_IS_LOCKED.getName(),
            TblsAppProcData.Instruments.FLD_LOCKED_REASON.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{false, true, LPDate.getCurrentTimeStamp(), token.getPersonName(),
            true, "decommissioned"});
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                fldNames, fldValues, new String[]{TblsAppProcData.Instruments.FLD_NAME.getName()},new Object[]{name});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.DECOMMISSION.toString(), name, TblsAppProcData.Instruments.TBL.getName(), name,
            fldNames, fldValues);
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.DECOMMISSION_INSTRUMENT.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.DECOMMISSION_INSTRUMENT.getSuccessMessageCode(), new Object[]{name}, name);
    }
    public InternalMessage unDecommissionInstrument(String[] fldNames, Object[] fldValues){
        if (!this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_DECOMMISSIONED.getErrorCode(), new Object[]{this.name}, null);
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        String[] reservedFldsNotUpdatable=new String[]{TblsAppProcData.Instruments.FLD_NAME.getName(), TblsAppProcData.Instruments.FLD_IS_LOCKED.getName(),
            TblsAppProcData.Instruments.FLD_LOCKED_REASON.getName(), TblsAppProcData.Instruments.FLD_ON_LINE.getName()};
        for (String curFld: fldNames){
            if (LPArray.valueInArray(reservedFldsNotUpdatable, curFld))
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.TRYINGUPDATE_RESERVED_FIELD.getErrorCode(), new Object[]{curFld}, null);                
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.FLD_ON_LINE.getName(),
            TblsAppProcData.Instruments.FLD_DECOMMISSIONED.getName(), TblsAppProcData.Instruments.FLD_DECOMMISSIONED_ON.getName(),
            TblsAppProcData.Instruments.FLD_DECOMMISSIONED_BY.getName(), 
            TblsAppProcData.Instruments.FLD_UNDECOMMISSIONED_ON.getName(),
            TblsAppProcData.Instruments.FLD_UNDECOMMISSIONED_BY.getName(), TblsAppProcData.Instruments.FLD_IS_LOCKED.getName(),
            TblsAppProcData.Instruments.FLD_LOCKED_REASON.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{false, false, "NULL>>>LOCALDATETIME", 
            "NULL>>>STRING",
            LPDate.getCurrentTimeStamp(), token.getPersonName(),false, ""});
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                fldNames, fldValues, new String[]{TblsAppProcData.Instruments.FLD_NAME.getName()},new Object[]{name});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.UNDECOMMISSION.toString(), name, TblsAppProcData.Instruments.TBL.getName(), name,
            fldNames, fldValues);
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.UNDECOMMISSION_INSTRUMENT.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.UNDECOMMISSION_INSTRUMENT.getSuccessMessageCode(), new Object[]{name}, name);
    }

    
    public InternalMessage turnOnLine(String[] fldNames, Object[] fldValues){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED.getErrorCode(), new Object[]{this.name}, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.FLD_ON_LINE.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{true});
        if (this.onLine){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_ONLINE.getErrorCode(), new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_ONLINE.getErrorCode(), new Object[]{name}, null);
        }
        if (this.isLocked){
            messages.addMainForError(InstrumentsErrorTrapping.IS_LOCKED.getErrorCode(), new Object[]{name, this.lockedReason});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.IS_LOCKED.getErrorCode(), new Object[]{name, this.lockedReason}, null);
        }
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                fldNames, fldValues, new String[]{TblsAppProcData.Instruments.FLD_NAME.getName()},new Object[]{name});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.TURN_ON_LINE.toString(), name, TblsAppProcData.Instruments.TBL.getName(), name,
            fldNames, fldValues);
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_ON_LINE.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_ON_LINE.getSuccessMessageCode(), new Object[]{name}, name);
    }
    public InternalMessage turnOffLine(String[] fldNames, Object[] fldValues){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED.getErrorCode(), new Object[]{this.name}, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        if (!this.onLine){
            messages.addMainForError(InstrumentsErrorTrapping.NOT_ONLINE.getErrorCode(), new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_ONLINE.getErrorCode(), new Object[]{name}, null);
        }
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.FLD_ON_LINE.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{false});
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                fldNames, fldValues, new String[]{TblsAppProcData.Instruments.FLD_NAME.getName()},new Object[]{name});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.TURN_OFF_LINE.toString(), name, TblsAppProcData.Instruments.TBL.getName(), name,
                        fldNames, fldValues);
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_OFF_LINE.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_OFF_LINE.getSuccessMessageCode(), new Object[]{name}, name);
    }

    public InternalMessage startCalibration(){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED.getErrorCode(), new Object[]{this.name}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), 
                new String[]{TblsAppProcData.InstrumentEvent.FLD_INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.FLD_EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.FLD_COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, InstrumentEvents.CALIBRATION.toString(), ""}, new String[]{TblsAppProcData.InstrumentEvent.FLD_ID.getName()});
        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_CALIBRATION.getErrorCode(), new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_CALIBRATION.getErrorCode(), new Object[]{name}, name);
        }        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.FLD_INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.FLD_EVENT_TYPE.getName(),
            TblsAppProcData.InstrumentEvent.FLD_CREATED_ON.getName(), TblsAppProcData.InstrumentEvent.FLD_CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.name, InstrumentEvents.CALIBRATION.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        Object[] instCreationDiagn = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), 
                fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.START_CALIBRATION.toString(), name, TblsAppProcData.Instruments.TBL.getName(), name,
                        fldNames, fldValues);
        
        if (this.onLine){
            fldNames=new String[]{TblsAppProcData.Instruments.FLD_IS_LOCKED.getName(), TblsAppProcData.Instruments.FLD_LOCKED_REASON.getName()};
            fldValues=new Object[]{true, "Under calibration event"};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_CALIBRATION.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_CALIBRATION.getSuccessMessageCode(), new Object[]{name}, name);
    }
    public InternalMessage completeCalibration(){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED.getErrorCode(), new Object[]{this.name}, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), 
                new String[]{TblsAppProcData.InstrumentEvent.FLD_INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.FLD_EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.FLD_COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, InstrumentEvents.CALIBRATION.toString(), ""}, new String[]{TblsAppProcData.InstrumentEvent.FLD_ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_CALIBRATION.getErrorCode(), new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_CALIBRATION.getErrorCode(), new Object[]{name}, name);
        }
        Integer eventId=Integer.valueOf(instrEventInfo[0][0].toString());
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), eventId);                
        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.FLD_COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.FLD_COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()};
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), 
                fldNames, fldValues, 
                new String[]{TblsAppProcData.InstrumentEvent.FLD_ID.getName()}, new Object[]{eventId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.COMPLETE_CALIBRATION.toString(), name, TblsAppProcData.Instruments.TBL.getName(), name,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsAppProcData.Instruments.FLD_LAST_CALIBRATION.getName(), TblsAppProcData.Instruments.FLD_IS_LOCKED.getName(), TblsAppProcData.Instruments.FLD_LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        if (!this.onLine){
            turnOnLine(fldNames, fldValues);
        }else{
            updateInstrument(fldNames, fldValues);            
        }
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_CALIBRATION.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_CALIBRATION.getSuccessMessageCode(), new Object[]{name}, name);
    }

    public InternalMessage startPrevMaint(){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED.getErrorCode(), new Object[]{this.name}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), 
                new String[]{TblsAppProcData.InstrumentEvent.FLD_INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.FLD_EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.FLD_COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, InstrumentEvents.PREVENTIVE_MAINTENANCE.toString(), ""}, new String[]{TblsAppProcData.InstrumentEvent.FLD_ID.getName()});
        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_PREV_MAINT.getErrorCode(), new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_PREV_MAINT.getErrorCode(), new Object[]{name}, name);
        }        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.FLD_INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.FLD_EVENT_TYPE.getName(),
            TblsAppProcData.InstrumentEvent.FLD_CREATED_ON.getName(), TblsAppProcData.InstrumentEvent.FLD_CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.name, InstrumentEvents.PREVENTIVE_MAINTENANCE.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        Object[] instCreationDiagn = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), 
                fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.START_PREVENTIVE_MAINTENANCE.toString(), name, TblsAppProcData.Instruments.TBL.getName(), name,
                        fldNames, fldValues);
        
        if (this.onLine){
            fldNames=new String[]{TblsAppProcData.Instruments.FLD_IS_LOCKED.getName(), TblsAppProcData.Instruments.FLD_LOCKED_REASON.getName()};
            fldValues=new Object[]{true, "Under preventive maintenance event"};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_PREV_MAINT.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_PREV_MAINT.getSuccessMessageCode(), new Object[]{name}, name);
    }
    public InternalMessage completePrevMaint(){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED.getErrorCode(), new Object[]{this.name}, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), 
                new String[]{TblsAppProcData.InstrumentEvent.FLD_INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.FLD_EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.FLD_COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, InstrumentEvents.PREVENTIVE_MAINTENANCE.toString(), ""}, new String[]{TblsAppProcData.InstrumentEvent.FLD_ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_PREV_MAINT.getErrorCode(), new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_PREV_MAINT.getErrorCode(), new Object[]{name}, name);
        }
        Integer eventId=Integer.valueOf(instrEventInfo[0][0].toString());
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), eventId);                
        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.FLD_COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.FLD_COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()};
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), 
                fldNames, fldValues, 
                new String[]{TblsAppProcData.InstrumentEvent.FLD_ID.getName()}, new Object[]{eventId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.COMPLETE_PREVENTIVE_MAINTENANCE.toString(), name, TblsAppProcData.Instruments.TBL.getName(), name,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsAppProcData.Instruments.FLD_LAST_PM.getName(), TblsAppProcData.Instruments.FLD_IS_LOCKED.getName(), TblsAppProcData.Instruments.FLD_LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        if (!this.onLine){
            turnOnLine(fldNames, fldValues);
        }else{
            updateInstrument(fldNames, fldValues);            
        }
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_PREV_MAINT.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_PREV_MAINT.getSuccessMessageCode(), new Object[]{name}, name);
    }
    
    public InternalMessage startVerification(){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED.getErrorCode(), new Object[]{this.name}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), 
                new String[]{TblsAppProcData.InstrumentEvent.FLD_INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.FLD_EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.FLD_COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, InstrumentEvents.VERIFICATION.toString(), ""}, new String[]{TblsAppProcData.InstrumentEvent.FLD_ID.getName()});
        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_VERIFICATION.getErrorCode(), new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_HAS_PENDING_VERIFICATION.getErrorCode(), new Object[]{name}, name);
        }        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.FLD_INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.FLD_EVENT_TYPE.getName(),
            TblsAppProcData.InstrumentEvent.FLD_CREATED_ON.getName(), TblsAppProcData.InstrumentEvent.FLD_CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.name, InstrumentEvents.VERIFICATION.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        Object[] instCreationDiagn = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), 
                fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.START_VERIFICATION.toString(), name, TblsAppProcData.Instruments.TBL.getName(), name,
                        fldNames, fldValues);
        
        if (this.onLine){
            fldNames=new String[]{TblsAppProcData.Instruments.FLD_IS_LOCKED.getName(), TblsAppProcData.Instruments.FLD_LOCKED_REASON.getName()};
            fldValues=new Object[]{true, "Under verification event"};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_VERIFICATION.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_VERIFICATION.getSuccessMessageCode(), new Object[]{name}, name);
    }
    public InternalMessage completeVerification(){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED.getErrorCode(), new Object[]{this.name}, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), 
                new String[]{TblsAppProcData.InstrumentEvent.FLD_INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.FLD_EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.FLD_COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.name, InstrumentEvents.VERIFICATION.toString(), ""}, new String[]{TblsAppProcData.InstrumentEvent.FLD_ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_VERIFICATION.getErrorCode(), new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NO_PENDING_VERIFICATION.getErrorCode(), new Object[]{name}, name);
        }
        Integer eventId=Integer.valueOf(instrEventInfo[0][0].toString());
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), eventId);                
        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.FLD_COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.FLD_COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()};
        Object[] instCreationDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.InstrumentEvent.TBL.getName(), 
                fldNames, fldValues, 
                new String[]{TblsAppProcData.InstrumentEvent.FLD_ID.getName()}, new Object[]{eventId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.COMPLETE_VERIFICATION.toString(), name, TblsAppProcData.Instruments.TBL.getName(), name,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsAppProcData.Instruments.FLD_LAST_VERIF.getName(), TblsAppProcData.Instruments.FLD_IS_LOCKED.getName(), TblsAppProcData.Instruments.FLD_LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        if (!this.onLine){
            turnOnLine(fldNames, fldValues);
        }else{
            updateInstrument(fldNames, fldValues);            
        }
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_CALIBRATION.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_CALIBRATION.getSuccessMessageCode(), new Object[]{name}, name);
    }
    
}