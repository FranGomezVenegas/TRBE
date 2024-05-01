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
import java.time.LocalDateTime;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import module.instrumentsmanagement.definition.InstrumentsEnums;
import module.inventorytrack.definition.InvTrackingEnums;
import trazit.enums.EnumIntTableFields;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.session.ResponseMessages;
import module.inventorytrack.definition.TblsInvTrackingConfig;
import module.inventorytrack.definition.TblsInvTrackingConfigAudit;

/**
 *
 * @author User
 */
public class ConfigInvTracking {

    public static InternalMessage configUpdateReference(String reference, String category, String[] fieldNames, Object[] fieldValues) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = procReqSession.getMessages();
        SqlWhere whereObj = new SqlWhere();
        Integer fldPosic=EnumIntTableFields.getFldPosicInArray(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields(), TblsInvTrackingConfig.Reference.HASHCODE.getName());
        if ( fldPosic> -1) {
            LocalDateTime currentTimeStamp = LPDate.getCurrentTimeStamp();
            int hashCode = currentTimeStamp.hashCode();
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInvTrackingConfig.Reference.HASHCODE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, String.valueOf(hashCode));
        }        
        whereObj.addConstraint(TblsInvTrackingConfig.Reference.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{reference}, null);
        whereObj.addConstraint(TblsInvTrackingConfig.Reference.CATEGORY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{category}, null);
        RdbmsObject updateTableRecordFieldsByFilter = Rdbms.updateTableRecordFieldsByFilter(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE,
                EnumIntTableFields.getTableFieldsFromString(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE, fieldNames),
                fieldValues, whereObj, null);
        if (updateTableRecordFieldsByFilter.getRunSuccess()) {
            AppInventoryLotAudit.inventoryLotConfigAuditAdd(InvTrackingEnums.AppConfigInventoryTrackingAuditEvents.REFERENCE_UPDATED, TblsInvTrackingConfigAudit.TablesInvTrackingConfigAudit.REFERENCE, reference,
                    category, fieldNames, fieldValues);
            messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.CONFIG_UPDATE_REFERENCE, new Object[]{reference});
            return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrEventsErrorTrapping.EVENT_NOTHING_PENDING, null, null);
        } else {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateTableRecordFieldsByFilter.getErrorMessageCode(), updateTableRecordFieldsByFilter.getErrorMessageVariables(), null);
        }
    }

    
}
