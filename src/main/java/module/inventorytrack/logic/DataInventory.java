package module.inventorytrack.logic;

import module.inventorytrack.definition.InvTrackingEnums;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.features.Token;
import static module.inventorytrack.logic.AppInventoryLotAudit.InventoryLotAuditAdd;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement.UomErrorTrapping;
import java.math.BigDecimal;
import trazit.session.ResponseMessages;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.LpPlatformSuccess;
import module.inventorytrack.definition.TblsInvTrackingConfig;
import module.inventorytrack.definition.TblsInvTrackingData;
import module.inventorytrack.definition.TblsInvTrackingData.TablesInvTrackingData;
import module.inventorytrack.definition.InvTrackingEnums.InvLotStatuses;
import module.inventorytrack.definition.InvTrackingEnums.InvReferenceStockControlTypes;
import module.inventorytrack.definition.InvTrackingEnums.InventoryTrackingErrorTrapping;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
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

    /**
     * @return the lotName
     */
    public String getLotName() {
        return lotName;
    }

    /**
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }
    private final String lotName;
    private Boolean availableForUse;
    private Boolean isLocked;
    private Boolean isRetired;
    private String lockedReason;
    private String[] lotFieldNames;
    private Object[] lotFieldValues;
    private String status;
    private String statusPrevious;
    private String reference;
    private String category;
    private String[] referenceFieldNames;
    private Object[] referenceFieldValues;
    private final Boolean hasError;
    private InternalMessage errorDetail;    
    private BigDecimal currentVolume;
    private String currentVolumeUom;
    private Boolean requiresQualification;
    private Boolean isQualified;
    private String[] qualificationFieldNames;
    private Object[] qualificationFieldValues;
    
    public enum Decisions{ACCEPTED, ACCEPTED_WITH_RESTRICTIONS, REJECTED}

    
    public DataInventory(String lotName, String reference, String category){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        Object[][] invLotInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), 
                new String[]{TblsInvTrackingData.Lot.LOT_NAME.getName(), TblsInvTrackingData.Lot.REFERENCE.getName(), TblsInvTrackingData.Lot.CATEGORY.getName()}, 
                new Object[]{lotName, reference, category}, getAllFieldNames(TblsInvTrackingData.TablesInvTrackingData.LOT.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(invLotInfo[0][0].toString())){
            this.lotName=null;
            this.hasError=true;
            this.errorDetail=new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TablesInvTrackingData.LOT.getTableName(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())}, lotName);
        }else{
            this.hasError=false;
            this.lotFieldNames=getAllFieldNames(TblsInvTrackingData.TablesInvTrackingData.LOT.getTableFields());
            this.lotFieldValues=invLotInfo[0];
            this.lotName=lotName;
            this.status=LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(lotFieldNames, TblsInvTrackingData.Lot.STATUS.getName())]).toString();
            this.statusPrevious=LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(lotFieldNames, TblsInvTrackingData.Lot.STATUS_PREVIOUS.getName())]).toString();
            String value=LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(lotFieldNames, TblsInvTrackingData.Lot.VOLUME.getName())]).toString();
            if (value.length()>0)
                this.currentVolume=BigDecimal.valueOf(Double.valueOf(LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(lotFieldNames, TblsInvTrackingData.Lot.VOLUME.getName())]).toString()));
            value=LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(lotFieldNames, TblsInvTrackingData.Lot.VOLUME_UOM.getName())]).toString();
            if (value.length()>0)
                this.currentVolumeUom=LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(lotFieldNames, TblsInvTrackingData.Lot.VOLUME_UOM.getName())]).toString();
            this.lotIsAvailableForUse();
            this.isLocked= Boolean.valueOf(LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(lotFieldNames, TblsInvTrackingData.Lot.IS_LOCKED.getName())]).toString());
            if (this.isLocked==null) this.isLocked=false;
            this.isRetired= InvLotStatuses.RETIRED.toString().equalsIgnoreCase(LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(lotFieldNames, TblsInvTrackingData.Lot.STATUS.getName())]).toString());
            if (this.isRetired==null) this.isRetired=false;
            this.lockedReason=LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(lotFieldNames, TblsInvTrackingData.Lot.LOCKED_REASON.getName())]).toString();
            this.reference=LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(lotFieldNames, TblsInvTrackingData.Lot.REFERENCE.getName())]).toString();
            this.category=LPNulls.replaceNull(invLotInfo[0][LPArray.valuePosicInArray(lotFieldNames, TblsInvTrackingData.Lot.CATEGORY.getName())]).toString();
            if (this.reference!=null && this.reference.length()>0){
                Object[][] invReferenceInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableName(), 
                        new String[]{TblsInvTrackingConfig.Reference.NAME.getName(), TblsInvTrackingConfig.Reference.CATEGORY.getName()}, 
                        new Object[]{this.reference, this.category}, getAllFieldNames(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields()));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(invReferenceInfo[0][0].toString())){
                    referenceFieldNames=null;
                    referenceFieldValues=null;
                }else{
                    referenceFieldNames=getAllFieldNames(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields());
                    referenceFieldValues=invReferenceInfo[0];
                }
            }
            Integer valuePosicInArray = LPArray.valuePosicInArray(referenceFieldNames, TblsInvTrackingConfig.Reference.LOT_REQUIRES_QUALIF.getName());
            if (valuePosicInArray==-1) 
                this.requiresQualification=true;
            this.requiresQualification=Boolean.valueOf(LPNulls.replaceNull(referenceFieldValues[valuePosicInArray]).toString());
            lotIsQualified();
        }
    }    
    private void lotIsAvailableForUse(){
        this.availableForUse=InvLotStatuses.AVAILABLE_FOR_USE.toString().equalsIgnoreCase(LPNulls.replaceNull(getLotFieldValues()[LPArray.valuePosicInArray(getLotFieldNames(), TblsInvTrackingData.Lot.STATUS.getName())]).toString());
        if (this.getAvailableForUse()==null) this.availableForUse=false;
    }
    private void lotIsQualified(){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        if (!this.getRequiresQualification()) return;
        Object[][] invLotCertifInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION.getTableName(), 
                new String[]{TblsInvTrackingData.LotCertification.LOT_NAME.getName(), TblsInvTrackingData.LotCertification.CATEGORY.getName(), TblsInvTrackingData.LotCertification.REFERENCE.getName()}, 
                new Object[]{this.getLotName(), this.getCategory(), this.getReference()}, getAllFieldNames(TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(invLotCertifInfo[0][0].toString())){
            qualificationFieldNames=null;
            qualificationFieldValues=null;
            return;
        }
        qualificationFieldNames=getAllFieldNames(TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION.getTableFields());
        qualificationFieldValues=invLotCertifInfo[0];
        
        String qualifCompletedDecision = LPNulls.replaceNull(invLotCertifInfo[0][LPArray.valuePosicInArray(getQualificationFieldNames(), TblsInvTrackingData.LotCertification.COMPLETED_DECISION.getName())]).toString();
        if (qualifCompletedDecision.toUpperCase().contains("ACCEPT"))
            this.isQualified=true;
        else
            this.isQualified=false;
    }
    public static Object[] checkVolumeCoherency(DataInventory invLot, EnumIntTableFields[] invReferenceFlds, Object[] invReferenceVls, BigDecimal lotVolume, String lotVolumeUom){        
        String refUOM=null;
        String[] refAllowedUOMS=null;
        String minStockType=LPNulls.replaceNull(invReferenceVls[EnumIntTableFields.getFldPosicInArray(invReferenceFlds, TblsInvTrackingConfig.Reference.MIN_STOCK_TYPE.getName())].toString());
        String minStock=LPNulls.replaceNull(invReferenceVls[EnumIntTableFields.getFldPosicInArray(invReferenceFlds, TblsInvTrackingConfig.Reference.MIN_STOCK.getName())].toString());
        if (minStockType.equalsIgnoreCase(InvReferenceStockControlTypes.VOLUME.toString())&& LPNulls.replaceNull(minStock).toString().length()>0&&LPNulls.replaceNull(lotVolume).toString().length()==0)
            return new Object[]{new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.NO_LOT_VOLUME_SPECIFIED_AND_REQUIRED, null, null)};
        if (invLot!=null){
            refUOM=LPNulls.replaceNull(invLot.getReferenceFieldValues()[LPArray.valuePosicInArray(invLot.getReferenceFieldNames(), TblsInvTrackingConfig.Reference.MIN_STOCK_UOM.getName())].toString());
            refAllowedUOMS=LPNulls.replaceNull(invLot.getReferenceFieldValues()[LPArray.valuePosicInArray(invLot.getReferenceFieldNames(), TblsInvTrackingConfig.Reference.ALLOWED_UOMS.getName())].toString()).split("\\|");
        }else if (InvReferenceStockControlTypes.VOLUME.toString().equalsIgnoreCase(LPNulls.replaceNull(invReferenceVls[EnumIntTableFields.getFldPosicInArray(invReferenceFlds, TblsInvTrackingConfig.Reference.MIN_STOCK_TYPE.getName())].toString()))){
            refUOM=LPNulls.replaceNull(invReferenceVls[EnumIntTableFields.getFldPosicInArray(invReferenceFlds, TblsInvTrackingConfig.Reference.MIN_STOCK_UOM.getName())].toString());
            refAllowedUOMS=LPNulls.replaceNull(invReferenceVls[EnumIntTableFields.getFldPosicInArray(invReferenceFlds, TblsInvTrackingConfig.Reference.ALLOWED_UOMS.getName())].toString()).split("\\|");
        }
        if (refAllowedUOMS!=null){
            if (!(LPArray.valueInArray(refAllowedUOMS, lotVolumeUom)||refUOM.equalsIgnoreCase(lotVolumeUom)
                    ||"ALL".equalsIgnoreCase(refAllowedUOMS[0])))
                return new Object[]{new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.UOM_NOT_INTHELIST, null, null)};
            if (refUOM.equalsIgnoreCase(lotVolumeUom))
                return new Object[]{new InternalMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.CORRECT, null, null)};
            UnitsOfMeasurement uom = new UnitsOfMeasurement(lotVolume, lotVolumeUom);
            uom.convertValue(refUOM);
            if (!uom.getConvertedFine()) 
                return new Object[]{new InternalMessage(LPPlatform.LAB_FALSE, UomErrorTrapping.CONVERSION_FAILED, null, null), uom};
            String convertedQuantityUom = uom.getConvertedQuantityUom();
            uom.getConvertedQuantity();
            return new Object[]{new InternalMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.CORRECT, null, null), uom};
        }else
            return new Object[]{new InternalMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.CORRECT, null, null)};            
    }       
    public static InternalMessage createNewInventoryLot(String name, String reference, String category, BigDecimal lotVolume, String lotVolumeUom, String[] fldNames, Object[] fldValues, Integer numEntries){   
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
                messages.addMainForError(InvTrackingEnums.InventoryTrackingErrorTrapping.REFERENCE_NOT_FOUND, new Object[]{reference});                
                return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.REFERENCE_NOT_FOUND, new Object[]{reference}, null);            
            }
            fldNames=LPArray.addValueToArray1D(fldNames, TblsInvTrackingData.Lot.REFERENCE.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, reference);
            fldNames=LPArray.addValueToArray1D(fldNames, TblsInvTrackingData.Lot.CATEGORY.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, category);
        }
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsInvTrackingData.Lot.LOGGED_ON.getName(), TblsInvTrackingData.Lot.LOGGED_BY.getName(),
            TblsInvTrackingData.Lot.STATUS.getName()});
        Object[] checkVolumeCoherencyDiagn = checkVolumeCoherency(null, TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields(), referenceInfo[0], lotVolume, lotVolumeUom);
        InternalMessage checkVolumeCoherency=(InternalMessage) checkVolumeCoherencyDiagn[0];
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkVolumeCoherency.getDiagnostic()))
            return checkVolumeCoherency;
        UnitsOfMeasurement myUom = null;
        if (checkVolumeCoherencyDiagn.length>1){
            myUom=(UnitsOfMeasurement)checkVolumeCoherencyDiagn[1];
            if (!myUom.getConvertedFine())
                return new InternalMessage(LPPlatform.LAB_FALSE, myUom.getConversionErrorDetail()[0].toString(), new Object[]{name}, null);
        }        
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName(),        
        InvLotStatuses.getStatusFirstCode(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields(), referenceInfo[0])});
        for (int iNum=0;iNum<numEntries;iNum++){
            String newName=name;
            if (numEntries>1){
                newName=newName+" "+String.valueOf(iNum+1)+"/"+numEntries.toString();
            }
            Integer lotNamePosic=LPArray.valuePosicInArray(fldNames, TblsInvTrackingData.Lot.LOT_NAME.getName());
            if (lotNamePosic==-1){
                fldNames=LPArray.addValueToArray1D(fldNames, TblsInvTrackingData.Lot.LOT_NAME.getName());
                fldValues=LPArray.addValueToArray1D(fldValues, newName);                
            }else
                fldValues[lotNamePosic]=newName;
            Object[] existsRecord = Rdbms.existsRecord(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsInvTrackingData.TablesInvTrackingData.LOT.getTableName(), 
                    new String[]{TblsInvTrackingData.Lot.LOT_NAME.getName()}, new Object[]{newName});
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString()))
                return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.ALREADY_EXISTS, new Object[]{newName}, null);

            RdbmsObject invLotCreationDiagn = Rdbms.insertRecordInTable(TablesInvTrackingData.LOT, fldNames, fldValues);
            if (!invLotCreationDiagn.getRunSuccess())
                return new InternalMessage(LPPlatform.LAB_FALSE, invLotCreationDiagn.getErrorMessageCode(), new Object[]{newName}, null);
            InventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.CREATION, newName, reference, category, TablesInvTrackingData.LOT.getTableName(), newName,
                fldNames, fldValues); 
            if (myUom!=null&&myUom.getConvertedFine()&&!myUom.getOrigQuantity().equals(myUom.getConvertedQuantity())){            
                SqlWhere whereObj=new SqlWhere(TablesInvTrackingData.LOT, 
                    new String[]{TblsInvTrackingData.Lot.REFERENCE.getName(), TblsInvTrackingData.Lot.CATEGORY.getName(), TblsInvTrackingData.Lot.LOT_NAME.getName()}, 
                        new Object[]{reference, category, newName});
                String[] updateFieldNames=new String[]{TblsInvTrackingData.Lot.VOLUME.getName(), TblsInvTrackingData.Lot.VOLUME_UOM.getName()};
                Object[] updateFieldValues=new Object[]{myUom.getConvertedQuantity(), myUom.getConvertedQuantityUom()}; 
                Rdbms.updateTableRecordFieldsByFilter(TablesInvTrackingData.LOT, 
                    EnumIntTableFields.getTableFieldsFromString(TablesInvTrackingData.LOT,updateFieldNames), updateFieldValues, whereObj, null);
                updateFieldNames=new String[]{"converted_volume", "converted_volume_uom", "creation_volume", "creation_volume_uom"};
                updateFieldValues=LPArray.addValueToArray1D(updateFieldValues, new Object[]{myUom.getOrigQuantity(), myUom.getOrigQuantityUom()});
                InventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.UOM_CONVERSION_ON_CREATION, newName, reference, category, TablesInvTrackingData.LOT.getTableName(), newName,    
                    updateFieldNames, updateFieldValues); 
            }
            myUom=null;
            String reqCertification = referenceInfo[0][EnumIntTableFields.getFldPosicInArray(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields(),
                    TblsInvTrackingConfig.Reference.LOT_REQUIRES_QUALIF.getName())].toString();
            if (reqCertification==null||Boolean.valueOf(reqCertification))
                DataInventoryQualif.createInventoryLotQualif(newName, category, reference, false);
        }
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.NEW_INVENTORY_LOT, new Object[]{name, category, reference});
        
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.NEW_INVENTORY_LOT, new Object[]{name}, name);
    }
    
    private InternalMessage updateLotTransaction(String newStatus, EnumIntEndpoints actionObj,  EnumIntAuditEvents auditEventObj, String[] extraFldNames, Object[] extraFldValues, EnumIntMessages msgWhenAlreadyStatus){   
        
        if (newStatus.equalsIgnoreCase(this.getStatus()))
            return new InternalMessage(LPPlatform.LAB_FALSE, msgWhenAlreadyStatus, new Object[]{this.getLotName(), this.getStatus()}, null);
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        ResponseMessages messages = procReqSession.getMessages();
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInvTrackingData.Lot.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.getLotName()}, "");
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.REFERENCE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.getReference()}, "");
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.CATEGORY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.getCategory()}, "");
        String[] fldNames=new String[]{TblsInvTrackingData.Lot.STATUS.getName(), TblsInvTrackingData.Lot.STATUS_PREVIOUS.getName()};
        Object[] fldValues=new Object[]{newStatus, this.getStatus()};
        RdbmsObject invLotTurnAvailableDiagn = Rdbms.updateTableRecordFieldsByFilter(TablesInvTrackingData.LOT, 
                new EnumIntTableFields[]{TblsInvTrackingData.Lot.STATUS, TblsInvTrackingData.Lot.STATUS_PREVIOUS},
                fldValues, sqlWhere, null);
        if (!invLotTurnAvailableDiagn.getRunSuccess())
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotTurnAvailableDiagn.getErrorMessageCode(), new Object[]{this.getLotName()}, null);
        InventoryLotAuditAdd(auditEventObj, this.getLotName(), getReference(), getCategory(), TablesInvTrackingData.LOT.getTableName(), this.getLotName(),
            fldNames, fldValues); 
        messages.addMainForSuccess(actionObj, new Object[]{this.getLotName(), getCategory(), getReference()});
        return new InternalMessage(LPPlatform.LAB_TRUE, actionObj, new Object[]{this.getLotName()}, this.getLotName());
    }

    public InternalMessage turnAvailable(String[] fldNames, Object[] fldValues){   
        if (this.getRequiresQualification()&&!this.getIsQualified()){
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.LOT_NOTQUALIFIED_YET, new Object[]{this.getLotName()}, null);
        }
        return updateLotTransaction(InvLotStatuses.AVAILABLE_FOR_USE.toString(), InvTrackingEnums.InventoryTrackAPIactionsEndpoints.TURN_LOT_AVAILABLE,
            InvTrackingEnums.AppInventoryTrackingAuditEvents.TURN_AVAILABLE, null, null, InventoryTrackingErrorTrapping.ALREADY_AVAILABLE);
    }
    
    public InternalMessage turnUnAvailable(String[] fldNames, Object[] fldValues){   
        return updateLotTransaction(InvLotStatuses.NOT_AVAILABLEFOR_USE.toString(), InvTrackingEnums.InventoryTrackAPIactionsEndpoints.TURN_LOT_UNAVAILABLE,
            InvTrackingEnums.AppInventoryTrackingAuditEvents.TURN_UNAVAILABLE, null, null, InventoryTrackingErrorTrapping.ALREADY_AVAILABLE);
    }

    public InternalMessage updateInventoryLot(String[] fldNames, Object[] fldValues){
        return updateInventoryLot(fldNames, fldValues, null);
    }
    public InternalMessage updateInventoryLot(String[] fldNames, Object[] fldValues, String actionName){
        if (this.getIsRetired())
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.ALREADY_RETIRED, new Object[]{this.getLotName()}, null);
        String[] reservedFldsNotUpdatable=new String[]{TblsInvTrackingData.Lot.LOT_NAME.getName()};
        String[] reservedFldsNotUpdatableFromActions=new String[]{TblsInvTrackingData.Lot.LOT_NAME.getName()};
        if (actionName!=null)reservedFldsNotUpdatable=reservedFldsNotUpdatableFromActions;
        for (String curFld: fldNames){
            if (LPArray.valueInArray(reservedFldsNotUpdatable, curFld))
                return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.TRYINGUPDATE_RESERVED_FIELD, new Object[]{curFld}, null);                
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
	SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{getLotName()}, "");
        Object[] instUpdateDiagn=Rdbms.updateRecordFieldsByFilter(TablesInvTrackingData.LOT,
                EnumIntTableFields.getTableFieldsFromString(TablesInvTrackingData.LOT, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{getLotName()}, null);
        InventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.UPDATE_INVENTORY_LOT, this.getLotName(), getReference(), getCategory(), TablesInvTrackingData.LOT.getTableName(), this.getLotName(),
            fldNames, fldValues); 
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.UPDATE_LOT, new Object[]{getLotName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.UPDATE_LOT, new Object[]{getLotName()}, getLotName());
    }
    
    public InternalMessage retireInventoryLot(String[] fldNames, Object[] fldValues){
        return updateLotTransaction(InvLotStatuses.RETIRED.toString(), InvTrackingEnums.InventoryTrackAPIactionsEndpoints.RETIRE_LOT,
            InvTrackingEnums.AppInventoryTrackingAuditEvents.RETIRED, null, null, InventoryTrackingErrorTrapping.ALREADY_RETIRED);
/*
        if (this.isDecommissioned)
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.ALREADY_RETIRED, new Object[]{this.lotName}, null);
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        String[] reservedFldsNotUpdatable=new String[]{TblsInvTrackingData.Lot.LOT_NAME.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(),
            TblsAppProcData.Instruments.LOCKED_REASON.getName(), TblsAppProcData.Instruments.ON_LINE.getName()};
        if (fldNames==null || fldNames[0].length()==0){
            fldNames=new String[]{};
            fldValues=new Object[]{};
        }        
        for (String curFld: fldNames){
            if (LPArray.valueInArray(reservedFldsNotUpdatable, curFld))
                return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.TRYINGUPDATE_RESERVED_FIELD, new Object[]{curFld}, null);                
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsAppProcData.Instruments.ON_LINE.getName(),
            TblsAppProcData.Instruments.DECOMMISSIONED.getName(), TblsAppProcData.Instruments.DECOMMISSIONED_ON.getName(),
            TblsAppProcData.Instruments.DECOMMISSIONED_BY.getName(), TblsAppProcData.Instruments.IS_LOCKED.getName(),
            TblsAppProcData.Instruments.LOCKED_REASON.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{false, true, LPDate.getCurrentTimeStamp(), token.getPersonName(),
            true, "decommissioned"});
	SqlWhere sqlWhere = new SqlWhere();
        fldNames=new String[]{TblsInvTrackingData.Lot.STATUS.getName(), TblsInvTrackingData.Lot.STATUS_PREVIOUS.getName()};
        fldValues=new Object[]{InvLotStatuses.RETIRED.toString(), this.status};

        sqlWhere.addConstraint(TblsInvTrackingData.Lot.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.REFERENCE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{reference}, "");
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.CATEGORY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{category}, "");
	Object[] instUpdateDiagn=Rdbms.updateRecordFieldsByFilter(TablesAppProcData.INSTRUMENTS,
		EnumIntTableFields.getTableFieldsFromString(TablesAppProcData.INSTRUMENTS, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instUpdateDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instUpdateDiagn[instUpdateDiagn.length-1].toString(), new Object[]{lotName}, null);
        instrumentsAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.DECOMMISSION, lotName, TablesAppProcData.INSTRUMENTS.getTableName(), lotName,
            fldNames, fldValues);
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.RETIRE_LOT, new Object[]{lotName});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.RETIRE_LOT, new Object[]{lotName}, lotName);
*/
    }
    public InternalMessage unRetireInventoryLot(String[] fldNames, Object[] fldValues){
        if (!InvLotStatuses.RETIRED.toString().equalsIgnoreCase(this.status))
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.NOT_RETIRED, new Object[]{getLotName()}, null);
        return updateLotTransaction(this.getStatusPrevious(), InvTrackingEnums.InventoryTrackAPIactionsEndpoints.UNRETIRE_LOT,
            InvTrackingEnums.AppInventoryTrackingAuditEvents.UNRETIRED, null, null, InventoryTrackingErrorTrapping.NOT_RETIRED);
    }

    public InternalMessage completeQualification(String decision, Boolean turnAvailable){
        return DataInventoryQualif.completeInventoryLotQualif(this, decision, turnAvailable);
    }
    public InternalMessage reopenQualification(){
        return DataInventoryQualif.reopenInventoryLotQualif(this);
    }
    public InternalMessage consumeInvLotVolume(BigDecimal nwVolume, String nwVolumeUom){
        return DataInventoryMovements.consumeInventoryLotVolume(this, nwVolume, nwVolumeUom);
    }
    public InternalMessage addInvLotVolume(BigDecimal nwVolume, String nwVolumeUom){
        return DataInventoryMovements.addInventoryLotVolume(this, nwVolume, nwVolumeUom);
    }
    public InternalMessage adjustInvLotVolume(BigDecimal nwVolume, String nwVolumeUom){
        return DataInventoryMovements.adjustInventoryLotVolume(this, nwVolume, nwVolumeUom);
    }
    public Boolean getHasError() {        return hasError;    }
    public InternalMessage getErrorDetail() {        return errorDetail;    }
    public Boolean getIsRetired() {        return isRetired;    }
    public Boolean getAvailableForUse() {        return availableForUse;    }
    public Boolean getIsLocked() {        return isLocked;    }
    public String getLockedReason() {        return lockedReason;    }
    public String getStatus() {        return status;    }
    public String getStatusPrevious() {        return statusPrevious;    }
    public String[] getReferenceFieldNames() {        return referenceFieldNames;    }
    public Object[] getReferenceFieldValues() {        return referenceFieldValues;    }
    public Boolean getRequiresQualification() {        return requiresQualification;    }
    public Boolean getIsQualified() {        return isQualified;    }
    public String[] getQualificationFieldNames() {        return qualificationFieldNames;    }
    public Object[] getQualificationFieldValues() {        return qualificationFieldValues;    }
    public String[] getLotFieldNames() {        return lotFieldNames;    }
    public Object[] getLotFieldValues() {        return lotFieldValues;    }
    public BigDecimal getCurrentVolume() {        return currentVolume;    }
    public String getCurrentVolumeUom() {        return currentVolumeUom;    }

}