/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inventorytrack.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsAppProcConfig;
import databases.TblsAppProcData;
import databases.TblsAppProcData.TablesAppProcData;
import databases.features.Token;
import static functionaljavaa.audit.AppInstrumentsAudit.instrumentsAuditAdd;
import static functionaljavaa.audit.AppInventoryLotAudit.InventoryLotAuditAdd;
import static functionaljavaa.instruments.DataInstrumentsEvents.addVariableSetToObject;
import static functionaljavaa.instruments.DataInstrumentsEvents.eventHasNotEnteredVariables;
import functionaljavaa.instruments.InstrumentsEnums.AppInstrumentsAuditEvents;
import functionaljavaa.instruments.InstrumentsEnums.InstrLockingReasons;
import functionaljavaa.instruments.InstrumentsEnums.InstrumentsErrorTrapping;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import trazit.session.ResponseMessages;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.LpPlatformSuccess;
import module.inventorytrack.definition.TblsInvTrackingConfig;
import module.inventorytrack.definition.TblsInvTrackingData;
import module.inventorytrack.definition.TblsInvTrackingData.TablesInvTrackingData;
import module.inventorytrack.logic.InvTrackingEnums.InvLotStatuses;
import module.inventorytrack.logic.InvTrackingEnums.InvReferenceStockControlTypes;
import module.inventorytrack.logic.InvTrackingEnums.InventoryTrackErrorTrapping;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */

public class DataInventory {
    private final String lotName;
    private Boolean availableForUse;
    private Boolean isLocked;
    private Boolean isDecommissioned;
    private String lockedReason;
    private String[] fieldNames;
    private Object[] fieldValues;
    private String status;
    private String reference;
    private String category;
    private String[] referenceFieldNames;
    private Object[] referenceFieldValues;
    private final Boolean hasError;
    private InternalMessage errorDetail;
    
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
        Integer fldPosic=LPArray.valuePosicInArray(this.referenceFieldNames, fieldName);
        if (fldPosic==-1) return false;
        return Boolean.valueOf(LPNulls.replaceNull(this.referenceFieldValues[fldPosic]).toString());
    }
    
    private Date nextEventDate(String fieldName){
        Integer fldPosic=LPArray.valuePosicInArray(this.referenceFieldNames, fieldName);
        if (fldPosic==-1) return null;
        String intervalInfo = LPNulls.replaceNull(this.referenceFieldValues[fldPosic]).toString();
        if (intervalInfo==null || intervalInfo.length()==0) return null;
        String[] intvlInfoArr=intervalInfo.split("\\*");
        if (intvlInfoArr.length!=2) return null;
        return LPDate.addIntervalToGivenDate(LPDate.getCurrentDateWithNoTime(), intvlInfoArr[0], Integer.valueOf(intvlInfoArr[1]));
    }
    
    public DataInventory(String lotName, String reference, String category){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        Object[][] invLotInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), 
                new String[]{TblsInvTrackingData.Lot.LOT_ID.getName(), TblsInvTrackingData.Lot.REFERENCE.getName(), TblsInvTrackingData.Lot.CATEGORY.getName()}, 
                new Object[]{lotName, reference, category}, getAllFieldNames(TblsInvTrackingData.TablesInvTrackingData.LOT.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(invLotInfo[0][0].toString())){
            this.lotName=null;
            this.hasError=true;
            this.errorDetail=new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TablesInvTrackingData.LOT.getTableName(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())}, lotName);
        }else{
            this.hasError=false;
            this.fieldNames=getAllFieldNames(TblsInvTrackingData.TablesInvTrackingData.LOT.getTableFields());
            this.fieldValues=invLotInfo[0];
            this.lotName=lotName;
            this.status=LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInvTrackingData.Lot.STATUS.getName())]).toString();
            this.availableForUse=InvLotStatuses.AVAILABLE_FOR_USE.toString().equalsIgnoreCase(LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInvTrackingData.Lot.STATUS.getName())]).toString());
            if (this.availableForUse==null) this.availableForUse=false;
            this.isLocked= Boolean.valueOf(LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInvTrackingData.Lot.IS_LOCKED.getName())]).toString());
            if (this.isLocked==null) this.isLocked=false;
            this.isDecommissioned= InvLotStatuses.RETIRED.toString().equalsIgnoreCase(LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInvTrackingData.Lot.STATUS.getName())]).toString());
            if (this.isDecommissioned==null) this.isDecommissioned=false;
            this.lockedReason=LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInvTrackingData.Lot.LOCKED_REASON.getName())]).toString();
            this.reference=LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInvTrackingData.Lot.REFERENCE.getName())]).toString();
            this.category=LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(fieldNames, TblsInvTrackingData.Lot.CATEGORY.getName())]).toString();
            if (this.reference!=null && this.reference.length()>0){
                Object[][] invReferenceInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableName(), 
                        new String[]{TblsInvTrackingConfig.Reference.NAME.getName()}, new Object[]{this.reference}, getAllFieldNames(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields()));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(invReferenceInfo[0][0].toString())){
                    referenceFieldNames=null;
                    referenceFieldValues=null;
                }else{
                    referenceFieldNames=getAllFieldNames(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields());
                    referenceFieldValues=invReferenceInfo[0];
                }
            }
        }
    }    
    public static InternalMessage checkVolumeCoherency(EnumIntTableFields[] invReferenceFlds, Object[] invReferenceVls, BigDecimal lotVolume, String lotVolumeUom){
        if (InvReferenceStockControlTypes.VOLUME.toString().equalsIgnoreCase(LPNulls.replaceNull(invReferenceVls[EnumIntTableFields.getFldPosicInArray(invReferenceFlds, TblsInvTrackingConfig.Reference.MIN_STOCK_TYPE.getName())].toString()))){
            if (LPNulls.replaceNull(lotVolume).toString().length()==0)
                return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackErrorTrapping.NO_LOT_VOLUME_SPECIFIED_AND_REQUIRED, null, null);
            String[] refAllowedUOMS=LPNulls.replaceNull(invReferenceVls[EnumIntTableFields.getFldPosicInArray(invReferenceFlds, TblsInvTrackingConfig.Reference.ALLOWED_UOMS.getName())].toString()).split("\\|");
            String refUom=LPNulls.replaceNull(invReferenceVls[EnumIntTableFields.getFldPosicInArray(invReferenceFlds, TblsInvTrackingConfig.Reference.MIN_STOCK_UOM.getName())].toString());
            if (!LPArray.valueInArray(refAllowedUOMS, lotVolume)||!refUom.equalsIgnoreCase(lotVolumeUom))
                return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackErrorTrapping.UOM_NOT_INTHELIST, null, null);            
            return new InternalMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.CORRECT, null, null);
        }else
            return new InternalMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.CORRECT, null, null);
            
    }       
    public static InternalMessage createNewInventoryLot(String name, String reference, String category, BigDecimal lotVolume, String lotVolumeUom, String[] fldNames, Object[] fldValues){   
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        ResponseMessages messages = procReqSession.getMessages();
        Token token = procReqSession.getToken();
        if (fldNames==null){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }
        Object[][] referenceInfo = null;
        if (reference!=null && reference.length()>0){
            referenceInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableName(), 
                new String[]{TblsInvTrackingConfig.Reference.NAME.getName(), TblsInvTrackingConfig.Reference.CATEGORY.getName()}, new Object[]{reference, category}, 
                getAllFieldNames(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields()));
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceInfo[0][0].toString())){
                messages.addMainForError(InvTrackingEnums.InventoryTrackErrorTrapping.REFERENCE_NOT_FOUND, new Object[]{reference});                
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.FAMILY_NOT_FOUND, new Object[]{reference}, null);            
            }
            fldNames=LPArray.addValueToArray1D(fldNames, TblsInvTrackingData.Lot.REFERENCE.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, reference);
            fldNames=LPArray.addValueToArray1D(fldNames, TblsInvTrackingData.Lot.CATEGORY.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, category);
        }
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsInvTrackingData.Lot.LOT_ID.getName(), 
            TblsInvTrackingData.Lot.LOGGED_ON.getName(), TblsInvTrackingData.Lot.LOGGED_BY.getName(),
            TblsInvTrackingData.Lot.STATUS.getName()});
        InternalMessage checkVolumeCoherency = checkVolumeCoherency(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields(), referenceInfo[0], lotVolume, lotVolumeUom);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkVolumeCoherency.getDiagnostic()))
            return checkVolumeCoherency;
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{name, LPDate.getCurrentTimeStamp(), token.getPersonName(),        
        InvLotStatuses.getStatusFirstCode(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields(), referenceInfo[0])});
        Object[] existsRecord = Rdbms.existsRecord(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsInvTrackingData.TablesInvTrackingData.LOT.getTableName(), 
                new String[]{TblsInvTrackingData.Lot.LOT_ID.getName()}, new Object[]{name});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_EXISTS, new Object[]{name}, null);
        
        RdbmsObject invLotCreationDiagn = Rdbms.insertRecordInTable(TablesInvTrackingData.LOT, fldNames, fldValues);
        if (!invLotCreationDiagn.getRunSuccess())
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotCreationDiagn.getErrorMessageCode(), new Object[]{name}, null);
        InventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.CREATION, name, reference, category, TablesInvTrackingData.LOT.getTableName(), name,
            fldNames, fldValues); 
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.NEW_INVENTORY_LOT, new Object[]{name, category, reference});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.NEW_INVENTORY_LOT, new Object[]{name}, name);
    }
    
    public static InternalMessage turnAvailable(String name, String reference, String category, String[] fldNames, Object[] fldValues){   
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        ResponseMessages messages = procReqSession.getMessages();
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInvTrackingData.Lot.LOT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{name}, "");
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.REFERENCE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{reference}, "");
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.CATEGORY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{category}, "");
        RdbmsObject invLotTurnAvailableDiagn = Rdbms.updateTableRecordFieldsByFilter(TablesInvTrackingData.LOT, 
                new EnumIntTableFields[]{TblsInvTrackingData.Lot.STATUS},
                new Object[]{InvLotStatuses.AVAILABLE_FOR_USE.toString()}, sqlWhere, null);
        if (!invLotTurnAvailableDiagn.getRunSuccess())
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotTurnAvailableDiagn.getErrorMessageCode(), new Object[]{name}, null);
        InventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.TURN_AVAILABLE, name, reference, category, TablesInvTrackingData.LOT.getTableName(), name,
            fldNames, fldValues); 
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.TURN_LOT_AVAILABLE, new Object[]{name, category, reference});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.TURN_LOT_AVAILABLE, new Object[]{name}, name);
    }
    
    public static InternalMessage turnUnAvailable(String name, String reference, String category, String[] fldNames, Object[] fldValues){   
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        ResponseMessages messages = procReqSession.getMessages();
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInvTrackingData.Lot.LOT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{name}, "");
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.REFERENCE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{reference}, "");
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.CATEGORY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{category}, "");
        RdbmsObject invLotTurnAvailableDiagn = Rdbms.updateTableRecordFieldsByFilter(TablesInvTrackingData.LOT, 
                new EnumIntTableFields[]{TblsInvTrackingData.Lot.STATUS},
                new Object[]{InvLotStatuses.NOT_AVAILABLEFOR_USE.toString()}, sqlWhere, null);
        if (!invLotTurnAvailableDiagn.getRunSuccess())
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotTurnAvailableDiagn.getErrorMessageCode(), new Object[]{name}, null);
        InventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.TURN_UNAVAILABLE, name, reference, category, TablesInvTrackingData.LOT.getTableName(), name,
            fldNames, fldValues); 
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.TURN_LOT_UNAVAILABLE, new Object[]{name, category, reference});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.TURN_LOT_UNAVAILABLE, new Object[]{name}, name);
    }

    public InternalMessage updateInstrument(String[] fldNames, Object[] fldValues){
        return updateInstrument(fldNames, fldValues, null);
    }
    public InternalMessage updateInstrument(String[] fldNames, Object[] fldValues, String actionName){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName}, null);
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
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsAppProcData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
	Object[] instUpdateDiagn=Rdbms.updateRecordFieldsByFilter(TablesAppProcData.INSTRUMENTS,
		EnumIntTableFields.getTableFieldsFromString(TablesAppProcData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{lotName}, null);
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.UPDATE_INSTRUMENT, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
            fldNames, fldValues);
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.UPDATE_INSTRUMENT, new Object[]{lotName});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.UPDATE_INSTRUMENT, new Object[]{lotName}, lotName);
    }
    public InternalMessage decommissionInstrument(String[] fldNames, Object[] fldValues){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName}, null);
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
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsAppProcData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
	Object[] instUpdateDiagn=Rdbms.updateRecordFieldsByFilter(TablesAppProcData.INSTRUMENTS,
		EnumIntTableFields.getTableFieldsFromString(TablesAppProcData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{lotName}, null);
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.DECOMMISSION, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
            fldNames, fldValues);
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.DECOMMISSION_INSTRUMENT, new Object[]{lotName});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.DECOMMISSION_INSTRUMENT, new Object[]{lotName}, lotName);
    }
    public InternalMessage unDecommissionInstrument(String[] fldNames, Object[] fldValues){
        if (!this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_DECOMMISSIONED, new Object[]{this.lotName}, null);
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
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsAppProcData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
	Object[] instUpdateDiagn=Rdbms.updateRecordFieldsByFilter(TablesAppProcData.INSTRUMENTS,
		EnumIntTableFields.getTableFieldsFromString(TablesAppProcData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{lotName}, null);
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.UNDECOMMISSION, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
            fldNames, fldValues);
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.UNDECOMMISSION_INSTRUMENT, new Object[]{lotName});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.UNDECOMMISSION_INSTRUMENT, new Object[]{lotName}, lotName);
    }

    public InternalMessage turnOnLine(String[] fldNames, Object[] fldValues){
        return turnOnLine(fldNames, fldValues, null);
    }
    public InternalMessage turnOnLine(String[] fldNames, Object[] fldValues, String actionName){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName}, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.ON_LINE.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{true});
        if (this.availableForUse){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_ONLINE, new Object[]{lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_ONLINE, new Object[]{lotName}, null);
        }
        if (actionName==null && this.isLocked){
            messages.addMainForError(InstrumentsErrorTrapping.IS_LOCKED, new Object[]{lotName, this.lockedReason});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.IS_LOCKED, new Object[]{lotName, this.lockedReason}, null);
        }
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsAppProcData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
	Object[] instUpdateDiagn=Rdbms.updateRecordFieldsByFilter(TablesAppProcData.INSTRUMENTS,
		EnumIntTableFields.getTableFieldsFromString(TablesAppProcData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{lotName}, null);
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.TURN_AVAILABLE, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
            fldNames, fldValues);
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.TURN_ON_LINE, new Object[]{lotName});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.TURN_ON_LINE, new Object[]{lotName}, lotName);
    }
    public InternalMessage turnOffLine(String[] fldNames, Object[] fldValues){
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName}, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        if (!this.availableForUse){
            messages.addMainForError(InstrumentsErrorTrapping.NOT_ONLINE, new Object[]{lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_ONLINE, new Object[]{lotName}, null);
        }
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.ON_LINE.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{false});
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsAppProcData.Instruments.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
	Object[] instUpdateDiagn=Rdbms.updateRecordFieldsByFilter(TablesAppProcData.INSTRUMENTS,
		EnumIntTableFields.getTableFieldsFromString(TablesAppProcData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{lotName}, null);
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.TURN_UNAVAILABLE, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
                        fldNames, fldValues);
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.TURN_OFF_LINE, new Object[]{lotName});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.TURN_OFF_LINE, new Object[]{lotName}, lotName);
    }

    public InternalMessage startCalibration(){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.lotName, AppInstrumentsAuditEvents.CALIBRATION.toString(), ""}, new String[]{TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InvTrackingEnums.InventoryTrackErrorTrapping.ALREADY_HAS_PENDING_CALIBRATION, new Object[]{lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackErrorTrapping.ALREADY_HAS_PENDING_CALIBRATION, new Object[]{lotName}, lotName);
        }        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsAppProcData.InstrumentEvent.CREATED_ON.getName(), TblsAppProcData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.lotName, AppInstrumentsAuditEvents.CALIBRATION.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesAppProcData.INSTRUMENT_EVENT, fldNames, fldValues);
        if (!instCreationDiagn.getRunSuccess())
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{lotName}, null);
        String insEventIdCreated=instCreationDiagn.getNewRowId().toString();
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.START_CALIBRATION, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
                        fldNames, fldValues);
        
        String variableSetName=null;
        Integer fldPosic=LPArray.valuePosicInArray(this.referenceFieldNames, TblsAppProcConfig.InstrumentsFamily.CALIB_VARIABLES_SET.getName());
        if (fldPosic>-1) 
            variableSetName=LPNulls.replaceNull(this.referenceFieldValues[fldPosic]).toString();
        if (variableSetName!=null){
            String ownerId= token.getPersonName();
            Integer instrEventId=Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(lotName, instrEventId, variableSetName, ownerId);
        }
        if (this.availableForUse){
            fldNames=new String[]{TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
            fldValues=new Object[]{true, InstrLockingReasons.UNDER_CALIBRATION_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.START_CALIBRATION, new Object[]{lotName});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.START_CALIBRATION, new Object[]{lotName}, insEventIdCreated);
    }
    public InternalMessage completeCalibration(String decision){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) return decisionValueIsCorrect;
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned!=null && this.isDecommissioned){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENT_EVENT.getTableName(),
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.lotName, AppInstrumentsAuditEvents.CALIBRATION.toString(), ""}, 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InvTrackingEnums.InventoryTrackErrorTrapping.NO_PENDING_CALIBRATION, new Object[]{lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackErrorTrapping.NO_PENDING_CALIBRATION, new Object[]{lotName}, lotName);
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

        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsAppProcData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
	Object[] instCreationDiagn=Rdbms.updateRecordFieldsByFilter(TablesAppProcData.INSTRUMENT_EVENT,
		EnumIntTableFields.getTableFieldsFromString(TablesAppProcData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{lotName}, null);
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_CALIBRATION, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsAppProcData.Instruments.LAST_CALIBRATION.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        
        Date nextEventDate = nextEventDate(TblsAppProcConfig.InstrumentsFamily.CALIB_INTERVAL.getName());
        if (nextEventDate!=null){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsAppProcData.Instruments.NEXT_CALIBRATION.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, nextEventDate);
        }
        if (!this.availableForUse && decisionAndFamilyRuleToTurnOn(decision, TblsAppProcConfig.InstrumentsFamily.CALIB_TURN_ON_WHEN_COMPLETED.getName())){
            turnOnLine(fldNames, fldValues, InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_CALIBRATION.toString());
        }else{
            updateInstrument(fldNames, fldValues, InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_CALIBRATION.toString());            
        }
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.COMPLETE_CALIBRATION, new Object[]{lotName, decision});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.COMPLETE_CALIBRATION, new Object[]{lotName, decision}, lotName);
    }

    public InternalMessage startPrevMaint(){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.lotName, AppInstrumentsAuditEvents.PREVENTIVE_MAINTENANCE.toString(), ""}, new String[]{TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InvTrackingEnums.InventoryTrackErrorTrapping.ALREADY_HAS_PENDING_PREV_MAINT, new Object[]{lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackErrorTrapping.ALREADY_HAS_PENDING_PREV_MAINT, new Object[]{lotName}, lotName);
        }        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsAppProcData.InstrumentEvent.CREATED_ON.getName(), TblsAppProcData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.lotName, AppInstrumentsAuditEvents.PREVENTIVE_MAINTENANCE.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesAppProcData.INSTRUMENT_EVENT, fldNames, fldValues);
        String insEventIdCreated=instCreationDiagn.getNewRowId().toString();
        if (!instCreationDiagn.getRunSuccess())
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{lotName}, null);
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.START_PREVENTIVE_MAINTENANCE, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
                        fldNames, fldValues);

        String variableSetName=null;
        Integer fldPosic=LPArray.valuePosicInArray(this.referenceFieldNames, TblsAppProcConfig.InstrumentsFamily.PM_VARIABLES_SET.getName());
        if (fldPosic>-1) 
            variableSetName=LPNulls.replaceNull(this.referenceFieldValues[fldPosic]).toString();
        if (variableSetName!=null){
            String ownerId= token.getPersonName();
            Integer instrEventId=Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(lotName, instrEventId, variableSetName, ownerId);
        }
        
        if (this.availableForUse){
            fldNames=new String[]{TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
            fldValues=new Object[]{true, InstrLockingReasons.UNDER_MAINTENANCE_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.START_PREVENTIVE_MAINTENANCE, new Object[]{lotName});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.START_PREVENTIVE_MAINTENANCE, new Object[]{lotName}, insEventIdCreated);
    }
    public InternalMessage completePrevMaint(String decision){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) return decisionValueIsCorrect;
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned!=null && this.isDecommissioned){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.lotName, AppInstrumentsAuditEvents.PREVENTIVE_MAINTENANCE.toString(), ""}, 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InvTrackingEnums.InventoryTrackErrorTrapping.NO_PENDING_PREV_MAINT, new Object[]{lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackErrorTrapping.NO_PENDING_PREV_MAINT, new Object[]{lotName}, lotName);
        }
        String instrName=instrEventInfo[0][0].toString();
        Integer eventId=Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), eventId);
        
        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())) return eventHasNotEnteredVariables;
        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsAppProcData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
	Object[] instCreationDiagn=Rdbms.updateRecordFieldsByFilter(TablesAppProcData.INSTRUMENT_EVENT,
		EnumIntTableFields.getTableFieldsFromString(TablesAppProcData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{lotName}, null);
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_PREVENTIVE_MAINTENANCE, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsAppProcData.Instruments.LAST_PM.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};

        Date nextEventDate = nextEventDate(TblsAppProcConfig.InstrumentsFamily.PM_INTERVAL.getName());
        if (nextEventDate!=null){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsAppProcData.Instruments.NEXT_PM.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, nextEventDate);
        }
        if (!this.availableForUse  && decisionAndFamilyRuleToTurnOn(decision, TblsAppProcConfig.InstrumentsFamily.PM_TURN_ON_WHEN_COMPLETED.getName())){
            turnOnLine(fldNames, fldValues, InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_PREVENTIVE_MAINTENANCE.toString());
        }else{
            updateInstrument(fldNames, fldValues, InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_PREVENTIVE_MAINTENANCE.toString());            
        }
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.COMPLETE_PREVENTIVE_MAINTENANCE, new Object[]{lotName, decision});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.COMPLETE_PREVENTIVE_MAINTENANCE, new Object[]{lotName, decision}, lotName);
    }
    
    public InternalMessage startVerification(){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.lotName, AppInstrumentsAuditEvents.VERIFICATION.toString(), ""}, new String[]{TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InvTrackingEnums.InventoryTrackErrorTrapping.ALREADY_HAS_PENDING_VERIFICATION, new Object[]{lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackErrorTrapping.ALREADY_HAS_PENDING_VERIFICATION, new Object[]{lotName}, lotName);
        }        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsAppProcData.InstrumentEvent.CREATED_ON.getName(), TblsAppProcData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.lotName, AppInstrumentsAuditEvents.VERIFICATION.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesAppProcData.INSTRUMENT_EVENT, 
                fldNames, fldValues);
        if (!instCreationDiagn.getRunSuccess())
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{lotName}, null);
        String insEventIdCreated=instCreationDiagn.getNewRowId().toString();
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.START_VERIFICATION, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
                        fldNames, fldValues);
        String variableSetName=null;
        Integer fldPosic=LPArray.valuePosicInArray(this.referenceFieldNames, TblsAppProcConfig.InstrumentsFamily.VERIF_SAME_DAY_VARIABLES_SET.getName());
        if (fldPosic>-1) 
            variableSetName=LPNulls.replaceNull(this.referenceFieldValues[fldPosic]).toString();
        if (variableSetName!=null){
            String ownerId= token.getPersonName();
            Integer instrEventId=Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(lotName, instrEventId, variableSetName, ownerId);
        }
        
        if (this.availableForUse){
            fldNames=new String[]{TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
            fldValues=new Object[]{true, InstrLockingReasons.UNDER_DAILY_VERIF_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.START_VERIFICATION, new Object[]{lotName});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.START_VERIFICATION, new Object[]{lotName}, insEventIdCreated);
    }
    public InternalMessage completeVerification(String decision){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) return decisionValueIsCorrect;
        
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned!=null && this.isDecommissioned){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.lotName, AppInstrumentsAuditEvents.VERIFICATION.toString(), ""}, 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InvTrackingEnums.InventoryTrackErrorTrapping.NO_PENDING_VERIFICATION, new Object[]{lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackErrorTrapping.NO_PENDING_VERIFICATION, new Object[]{lotName}, lotName);
        }
        String instrName=instrEventInfo[0][0].toString();
        Integer eventId=Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), eventId);                
        
        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())) return eventHasNotEnteredVariables;

        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsAppProcData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
	Object[] instCreationDiagn=Rdbms.updateRecordFieldsByFilter(TablesAppProcData.INSTRUMENT_EVENT,
		EnumIntTableFields.getTableFieldsFromString(TablesAppProcData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{lotName}, null);
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_VERIFICATION, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsAppProcData.Instruments.LAST_VERIF.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        if (!this.availableForUse){
            turnOnLine(fldNames, fldValues);
        }else{
            updateInstrument(fldNames, fldValues, InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_VERIFICATION.toString());            
        }
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.COMPLETE_VERIFICATION, new Object[]{lotName, decision});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.COMPLETE_VERIFICATION, new Object[]{lotName, decision}, lotName);
    }

    public InternalMessage startSevice(){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName}, null);        
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.lotName, AppInstrumentsAuditEvents.SERVICE.toString(), ""}, new String[]{TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InvTrackingEnums.InventoryTrackErrorTrapping.ALREADY_HAS_PENDING_SERVICE, new Object[]{lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackErrorTrapping.ALREADY_HAS_PENDING_SERVICE, new Object[]{lotName}, lotName);
        }        
        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
            TblsAppProcData.InstrumentEvent.CREATED_ON.getName(), TblsAppProcData.InstrumentEvent.CREATED_BY.getName()};
        Object[] fldValues=new Object[]{this.lotName, AppInstrumentsAuditEvents.SERVICE.toString(), LPDate.getCurrentTimeStamp(), token.getPersonName()};
        RdbmsObject instCreationDiagn = Rdbms.insertRecordInTable(TablesAppProcData.INSTRUMENT_EVENT, fldNames, fldValues);
        if (!instCreationDiagn.getRunSuccess())
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn.getErrorMessageCode(), new Object[]{lotName}, null);
        String insEventIdCreated=instCreationDiagn.getNewRowId().toString();
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.START_SERVICE, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
                        fldNames, fldValues);
        String variableSetName=null;
        Integer fldPosic=LPArray.valuePosicInArray(this.referenceFieldNames, TblsAppProcConfig.InstrumentsFamily.SERVICE_VARIABLES_SET.getName());
        if (fldPosic>-1) 
            variableSetName=LPNulls.replaceNull(this.referenceFieldValues[fldPosic]).toString();
        if (variableSetName!=null){
            String ownerId= token.getPersonName();
            Integer instrEventId=Integer.valueOf(instCreationDiagn.getNewRowId().toString());
            addVariableSetToObject(lotName, instrEventId, variableSetName, ownerId);
        }
        
        if (this.availableForUse){
            fldNames=new String[]{TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
            fldValues=new Object[]{true, InstrLockingReasons.UNDER_SERVICE_EVENT.getPropertyName()};
            turnOffLine(fldNames, fldValues);
        }
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.START_SERVICE, new Object[]{lotName});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.START_SERVICE, new Object[]{lotName}, insEventIdCreated);
    }
    public InternalMessage completeService(String decision){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) return decisionValueIsCorrect;
        
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (this.isDecommissioned!=null && this.isDecommissioned){
            messages.addMainForError(InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.EVENT_TYPE.getName(),
                    TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                new Object[]{this.lotName, AppInstrumentsAuditEvents.SERVICE.toString(), ""}, 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.ID.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InvTrackingEnums.InventoryTrackErrorTrapping.NO_PENDING_SERVICE, new Object[]{lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackErrorTrapping.NO_PENDING_SERVICE, new Object[]{lotName}, lotName);
        }
        String instrName=instrEventInfo[0][0].toString();
        Integer eventId=Integer.valueOf(instrEventInfo[0][1].toString());
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), eventId);                
        
        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())) return eventHasNotEnteredVariables;

        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsAppProcData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
	Object[] instCreationDiagn=Rdbms.updateRecordFieldsByFilter(TablesAppProcData.INSTRUMENT_EVENT,
		EnumIntTableFields.getTableFieldsFromString(TablesAppProcData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{lotName}, null);
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_SERVICE, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsAppProcData.Instruments.LAST_VERIF.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        if (!this.availableForUse){
            turnOnLine(fldNames, fldValues);
        }else{
            updateInstrument(fldNames, fldValues, InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_SERVICE.toString());            
        }
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.COMPLETE_SERVICE, new Object[]{lotName, decision});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.COMPLETE_SERVICE, new Object[]{lotName, decision}, lotName);
    }
    
    public InternalMessage reopenEvent(Integer instrEventId){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{this.lotName}, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        
        Object[][] instrEventInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.ID.getName()}, 
                new Object[]{this.lotName, instrEventId}, 
                new String[]{TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_DECISION.getName()});        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())){
            messages.addMainForError(InvTrackingEnums.InventoryTrackErrorTrapping.NOT_FOUND, new Object[]{lotName});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackErrorTrapping.NOT_FOUND, new Object[]{lotName}, lotName);
        }
        String eventCompletedOn=LPNulls.replaceNull(instrEventInfo[0][0]).toString();
        String eventDecision=LPNulls.replaceNull(instrEventInfo[0][1]).toString();
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), instrEventId);                
        
        if (eventCompletedOn.length()==0 || eventDecision.length()==0){
            messages.addMainForError(InvTrackingEnums.InventoryTrackErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId}, lotName);
        }

        String[] fldNames=new String[]{TblsAppProcData.InstrumentEvent.COMPLETED_DECISION.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_ON.getName(), TblsAppProcData.InstrumentEvent.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{"NULL>>>STRING", "NULL>>>LOCALDATETIME", "NULL>>>STRING"};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsAppProcData.InstrumentEvent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{instrEventId}, "");
	Object[] instCreationDiagn=Rdbms.updateRecordFieldsByFilter(TablesAppProcData.INSTRUMENT_EVENT,
		EnumIntTableFields.getTableFieldsFromString(TablesAppProcData.INSTRUMENT_EVENT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{lotName}, null);
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.REOPEN_EVENT, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
                        fldNames, fldValues);        
        fldNames=new String[]{TblsAppProcData.Instruments.LAST_VERIF.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(), TblsAppProcData.Instruments.LOCKED_REASON.getName()};
        fldValues=new Object[]{LPDate.getCurrentTimeStamp(),false, ""};
        if (this.availableForUse){
            turnOffLine(fldNames, fldValues);
        }else{
            updateInstrument(fldNames, fldValues, InvTrackingEnums.AppInventoryTrackingAuditEvents.REOPEN_EVENT.toString());            
        }
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.REOPEN_EVENT, new Object[]{lotName});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.REOPEN_EVENT, new Object[]{lotName}, lotName);
    }

    public Boolean getHasError() {        return hasError;    }
    public InternalMessage getErrorDetail() {        return errorDetail;    }

}
