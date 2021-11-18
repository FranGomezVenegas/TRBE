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
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */

public class ConfigInstruments {
    private String name;
    private Boolean onLine;
    private String[] fieldNames;
    private Object[] fieldValues;
    
    
    public ConfigInstruments(String instrName){
        Object[][] instrInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_CONFIG.getName(), TblsAppProcConfig.Instruments.TBL.getName(), 
                new String[]{TblsAppProcConfig.Instruments.FLD_NAME.getName()}, new Object[]{instrName}, TblsAppProcConfig.Instruments.getAllFieldNames());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())){
            this.name=null;
        }else{
            this.fieldNames=TblsAppProcConfig.Instruments.getAllFieldNames();
            this.fieldValues=instrInfo[0];
            this.name=instrName;
            this.onLine=(Boolean) instrInfo[0][LPArray.valuePosicInArray(fieldNames, TblsAppProcConfig.Instruments.FLD_ON_LINE.getName())];
        }
    }    
    
    public static InternalMessage createNewInstrument(String name, String[] fldNames, Object[] fldValues){      
        if (fldNames==null){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcConfig.Instruments.FLD_NAME.getName(), TblsAppProcConfig.Instruments.FLD_ON_LINE.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{name, false});
        Object[] instCreationDiagn = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP_PROC_CONFIG.getName(), TblsAppProcConfig.Instruments.TBL.getName(), 
                fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.CREATION.toString(), name, TblsAppProcConfig.Instruments.TBL.getName(), name,
                        fldNames, fldValues);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        messages.addMainForSuccess("configInstruments", InstrumentsEnums.InstrumentsAPIactionsEndpoints.NEW_INSTRUMENT.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.NEW_INSTRUMENT.getSuccessMessageCode(), new Object[]{name}, name);
    }
    public InternalMessage turnOnLine(String[] fldNames, Object[] fldValues){
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcConfig.Instruments.FLD_ON_LINE.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{true});
        if (this.onLine){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_ONLINE.getErrorCode(), new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_ONLINE.getErrorCode(), new Object[]{name}, null);
        }
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_CONFIG.getName(), TblsAppProcConfig.Instruments.TBL.getName(), 
                fldNames, fldValues, new String[]{TblsAppProcConfig.Instruments.FLD_NAME.getName()},new Object[]{name});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.TURN_ON_LINE.toString(), name, TblsAppProcConfig.Instruments.TBL.getName(), name,
            fldNames, fldValues);
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_ON_LINE.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_ON_LINE.getSuccessMessageCode(), new Object[]{name}, name);
    }
    public InternalMessage turnOffLine(String[] fldNames, Object[] fldValues){
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        if (!this.onLine){
            messages.addMainForError(InstrumentsErrorTrapping.NOT_ONLINE.getErrorCode(), new Object[]{name});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_ONLINE.getErrorCode(), new Object[]{name}, null);
        }
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcConfig.Instruments.FLD_ON_LINE.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{false});
        Object[] instUpdateDiagn = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP_PROC_CONFIG.getName(), TblsAppProcConfig.Instruments.TBL.getName(), 
                fldNames, fldValues, new String[]{TblsAppProcConfig.Instruments.FLD_NAME.getName()},new Object[]{name});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.TURN_OFF_LINE.toString(), name, TblsAppProcConfig.Instruments.TBL.getName(), name,
                        fldNames, fldValues);
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_OFF_LINE.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.TURN_OFF_LINE.getSuccessMessageCode(), new Object[]{name}, name);
    }
    public InternalMessage startCalibration(){
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
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.START_CALIBRATION.toString(), name, TblsAppProcConfig.Instruments.TBL.getName(), name,
                        fldNames, fldValues);

        Object[] instrumentOnline = isInstrumentOnline(this.name);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(instrumentOnline[0].toString())){
            fldNames=new String[]{TblsAppProcConfig.Instruments.FLD_IS_LOCKED.getName(), TblsAppProcConfig.Instruments.FLD_LOCKED_REASON.getName()};
            fldValues=new Object[]{true, "Under calibration event"};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_CALIBRATION.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.START_CALIBRATION.getSuccessMessageCode(), new Object[]{name}, name);
    }
    public InternalMessage completeCalibration(){
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
        instrumentsAuditAdd(InstrumentsEnums.InstrumentEvents.COMPLETE_CALIBRATION.toString(), name, TblsAppProcConfig.Instruments.TBL.getName(), name,
                        fldNames, fldValues);

        Object[] instrumentOnline = isInstrumentOnline(this.name);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentOnline[0].toString())){
            fldNames=new String[]{TblsAppProcConfig.Instruments.FLD_IS_LOCKED.getName(), TblsAppProcConfig.Instruments.FLD_LOCKED_REASON.getName()};
            fldValues=new Object[]{false, ""};
            turnOnLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(this.getClass().getSimpleName(), InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_CALIBRATION.getSuccessMessageCode(), new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.COMPLETE_CALIBRATION.getSuccessMessageCode(), new Object[]{name}, name);
    }
    private Object[] isInstrumentOnline(String instrName){
        if (!this.onLine) return  LPPlatform.trapMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_ONLINE.getErrorCode(), new Object[]{instrName});
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "on-line", new Object[]{instrName});
    }
    
}
