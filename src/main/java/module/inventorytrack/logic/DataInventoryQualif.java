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
import databases.features.Token;
import static functionaljavaa.audit.AppInventoryLotAudit.InventoryLotAuditAdd;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.inventorytrack.definition.TblsInvTrackingConfig;
import module.inventorytrack.definition.TblsInvTrackingData;
import module.inventorytrack.definition.TblsInvTrackingData.TablesInvTrackingData;
import module.inventorytrack.logic.InvTrackingEnums.InventoryTrackingErrorTrapping;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;
import trazit.session.InternalMessage;
import trazit.session.ResponseMessages;
/**
 *
 * @author Administrator
 */
public class DataInventoryQualif {

    public static InternalMessage createInventoryLotQualif(String lotName, String category, String reference, Boolean requiresConfigChecks){   
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        ResponseMessages messages = procReqSession.getMessages();
        Token token = procReqSession.getToken();
        String[] fldNames=new String[]{};
        Object[] fldValues=new Object[]{};
        Object[][] referenceInfo = null;
        if (requiresConfigChecks){
            if (reference!=null && reference.length()>0){
                referenceInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableName(), 
                    new String[]{TblsInvTrackingConfig.Reference.NAME.getName(), TblsInvTrackingConfig.Reference.CATEGORY.getName()}, new Object[]{reference, category}, 
                    getAllFieldNames(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields()));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceInfo[0][0].toString())){
                    messages.addMainForError(InvTrackingEnums.InventoryTrackingErrorTrapping.REFERENCE_NOT_FOUND, new Object[]{reference});                
                    return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.REFERENCE_NOT_FOUND, new Object[]{reference}, null);            
                }
            }
        }
            fldNames=LPArray.addValueToArray1D(fldNames, TblsInvTrackingData.LotCertification.REFERENCE.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, reference);
            fldNames=LPArray.addValueToArray1D(fldNames, TblsInvTrackingData.LotCertification.CATEGORY.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, category);
        fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsInvTrackingData.LotCertification.LOT_NAME.getName(), 
            TblsInvTrackingData.LotCertification.CREATED_ON.getName(), TblsInvTrackingData.LotCertification.CREATED_BY.getName()});
        fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{lotName, LPDate.getCurrentTimeStamp(), token.getPersonName()});        
        
        RdbmsObject invLotQualifCreationDiagn = Rdbms.insertRecordInTable(TablesInvTrackingData.LOT_CERTIFICATION, fldNames, fldValues);
        if (!invLotQualifCreationDiagn.getRunSuccess())
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotQualifCreationDiagn.getErrorMessageCode(), new Object[]{lotName}, null);
        InventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.CREATED_QUALIFICATION, lotName, reference, category, TablesInvTrackingData.LOT.getTableName(), lotName,
            fldNames, fldValues); 
            
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.NEW_INVENTORY_LOT, new Object[]{lotName}, lotName);
    }

    public static InternalMessage completeInventoryLotQualif(DataInventory invLot, String decision, Boolean turnLotAvailable){   
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        InternalMessage decisionValueIsCorrect = decisionValueIsCorrect(decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decisionValueIsCorrect.getDiagnostic())) return decisionValueIsCorrect;
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (invLot.getIsRetired()!=null && invLot.getIsRetired()){
            messages.addMainForError(InventoryTrackingErrorTrapping.ALREADY_RETIRED, new Object[]{invLot.getLotName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.ALREADY_RETIRED, new Object[]{invLot.getLotName()}, null);
        }
        Token token = ProcedureRequestSession.getInstanceForQueries(null, null, false).getToken();
        String eventIdStr=LPNulls.replaceNull(invLot.getQualificationFieldValues()[LPArray.valuePosicInArray(invLot.getQualificationFieldNames(), TblsInvTrackingData.LotCertification.ID.getName())]).toString();        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(invLot.getQualificationFieldValues()[0].toString())||eventIdStr.length()==0){
            messages.addMainForError(InvTrackingEnums.InventoryTrackingErrorTrapping.NO_PENDING_QUALIFICATION, new Object[]{invLot.getLotName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.NO_PENDING_QUALIFICATION, new Object[]{invLot.getLotName()}, invLot.getLotName());
        }        
        Integer eventId=Integer.valueOf(eventIdStr);
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesInvTrackingData.LOT_CERTIFICATION.getTableName(), eventId);                
        
/*        InternalMessage eventHasNotEnteredVariables = eventHasNotEnteredVariables(instrName, eventId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(eventHasNotEnteredVariables.getDiagnostic())){
            messages.addMainForError(eventHasNotEnteredVariables.getMessageCodeObj(), eventHasNotEnteredVariables.getMessageCodeVariables());            
            return eventHasNotEnteredVariables;
        }*/

        String[] fldNames=new String[]{TblsInvTrackingData.LotCertification.COMPLETED_DECISION.getName(), TblsInvTrackingData.LotCertification.COMPLETED_ON.getName(), TblsInvTrackingData.LotCertification.COMPLETED_BY.getName()};
        Object[] fldValues=new Object[]{decision, LPDate.getCurrentTimeStamp(), token.getPersonName()};
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInvTrackingData.LotCertification.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
	Object[] instCreationDiagn=Rdbms.updateRecordFieldsByFilter(TablesInvTrackingData.LOT_CERTIFICATION,
		EnumIntTableFields.getTableFieldsFromString(TablesInvTrackingData.LOT_CERTIFICATION, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{invLot.getLotName()}, null);
        InventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_QUALIFICATION, 
                invLot.getLotName(), invLot.getReference(), invLot.getCategory(), TablesInvTrackingData.LOT.getTableName(),
                invLot.getLotName(), fldNames, fldValues);         
        fldNames=new String[]{TblsInvTrackingData.Lot.IS_LOCKED.getName(), TblsInvTrackingData.Lot.LOCKED_REASON.getName()};
        fldValues=new Object[]{false, ""};
        invLot.updateInventoryLot(fldNames, fldValues, InvTrackingEnums.AppInventoryTrackingAuditEvents.UNLOCK_LOT_ONCE_QUALIFIED.toString());            
        InventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.UNLOCK_LOT_ONCE_QUALIFIED, 
                invLot.getLotName(), invLot.getReference(), invLot.getCategory(), TablesInvTrackingData.LOT.getTableName(),
                invLot.getLotName(), fldNames, fldValues);         
        if (turnLotAvailable!=null&&turnLotAvailable&&decision.toUpperCase().contains("ACCEPT"))
            invLot.turnAvailable(null, null);
//        if (!invLot.availableForUse && decisionAndFamilyRuleToTurnOn(decision, TblsAppProcConfig.InstrumentsFamily.CALIB_TURN_ON_WHEN_COMPLETED.getName())){
//            turnAvailable(fldNames, fldValues); //, InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_CALIBRATION.toString());
//        }else{
//            updateInventoryLot(fldNames, fldValues, InvTrackingEnums.AppInventoryTrackingAuditEvents.COMPLETE_QUALIFICATION.toString());            
//        }
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.COMPLETE_QUALIFICATION, new Object[]{invLot.getLotName(), decision});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.COMPLETE_QUALIFICATION, new Object[]{invLot.getLotName(), decision}, invLot.getLotName());        
    }
    
    public static InternalMessage reopenInventoryLotQualif(DataInventory invLot){
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        String eventIdStr=LPNulls.replaceNull(invLot.getQualificationFieldValues()[LPArray.valuePosicInArray(invLot.getQualificationFieldNames(), TblsInvTrackingData.LotCertification.ID.getName())]).toString();        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(invLot.getQualificationFieldValues()[0].toString())||eventIdStr.length()==0){
            messages.addMainForError(InvTrackingEnums.InventoryTrackingErrorTrapping.NO_PENDING_QUALIFICATION, new Object[]{invLot.getLotName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.NO_PENDING_QUALIFICATION, new Object[]{invLot.getLotName()}, invLot.getLotName());
        }  
        String qualifDecision=LPNulls.replaceNull(invLot.getQualificationFieldValues()[LPArray.valuePosicInArray(invLot.getQualificationFieldNames(), TblsInvTrackingData.LotCertification.COMPLETED_DECISION.getName())]).toString();        
        if (qualifDecision.length()==0)
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.QUALIFICATION_NOT_CLOSED, new Object[]{invLot.getLotName()}, invLot.getLotName());
        Integer eventId=Integer.valueOf(eventIdStr);
        String[] fldNames=new String[]{TblsInvTrackingData.LotCertification.COMPLETED_DECISION.getName(), TblsInvTrackingData.LotCertification.COMPLETED_BY.getName()};
            //TblsInvTrackingData.LotCertification.COMPLETED_ON.getName(), 
        Object[] fldValues=new Object[]{"", ""}; //"null", 
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsInvTrackingData.LotCertification.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{eventId}, "");
	Object[] instCreationDiagn=Rdbms.updateRecordFieldsByFilter(TablesInvTrackingData.LOT_CERTIFICATION,
		EnumIntTableFields.getTableFieldsFromString(TablesInvTrackingData.LOT_CERTIFICATION, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{invLot.getLotName()}, null);
        InventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.REOPEN_QUALIFICATION, 
                invLot.getLotName(), invLot.getReference(), invLot.getCategory(), TablesInvTrackingData.LOT.getTableName(),
                invLot.getLotName(), fldNames, fldValues);         
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.REOPEN_QUALIFICATION, new Object[]{invLot.getLotName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.REOPEN_QUALIFICATION, new Object[]{invLot.getLotName()}, invLot.getLotName());        
        
    }
    private static InternalMessage decisionValueIsCorrect(String decision){
        try{
            DataInventory.Decisions.valueOf(decision);
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.CORRECT, null, null);
        }catch(Exception e){
            ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getMessages();
            messages.addMainForError(InventoryTrackingErrorTrapping.WRONG_DECISION, new Object[]{decision, Arrays.toString(DataInventory.Decisions.values())});
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.WRONG_DECISION, new Object[]{decision, Arrays.toString(DataInventory.Decisions.values())}, null);
        }
    }
}
