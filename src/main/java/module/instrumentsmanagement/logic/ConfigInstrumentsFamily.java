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
import java.time.LocalDateTime;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import module.instrumentsmanagement.definition.InstrumentsEnums;
import module.instrumentsmanagement.definition.InstrumentsEnums.InstrEventsErrorTrapping;
import module.instrumentsmanagement.definition.TblsInstrumentsConfig;
import module.instrumentsmanagement.definition.TblsInstrumentsConfigAudit;
import trazit.enums.EnumIntTableFields;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.session.ResponseMessages;

/**
 *
 * @author User
 */
public class ConfigInstrumentsFamily {

    public static InternalMessage configUpdateInstrumentFamily(String family, String[] fieldNames, Object[] fieldValues) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = procReqSession.getMessages();
        SqlWhere whereObj = new SqlWhere();
        if (EnumIntTableFields.getFldPosicInArray(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableFields(), TblsInstrumentsConfig.InstrumentsFamily.HASHCODE.getName()) > -1) {
            LocalDateTime currentTimeStamp = LPDate.getCurrentTimeStamp();
            int hashCode = currentTimeStamp.hashCode();
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsConfig.InstrumentsFamily.HASHCODE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, hashCode);
        }        
        whereObj.addConstraint(TblsInstrumentsConfig.InstrumentsFamily.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{family}, null);
        RdbmsObject updateTableRecordFieldsByFilter = Rdbms.updateTableRecordFieldsByFilter(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY,
                EnumIntTableFields.getTableFieldsFromString(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY, fieldNames),
                fieldValues, whereObj, null);
        if (updateTableRecordFieldsByFilter.getRunSuccess()) {
            AppInstrumentsAudit.instrumentsConfigAuditAdd(InstrumentsEnums.AppConfigInstrumentsAuditEvents.INSTRUMENT_FAMILY_UPDATED, TblsInstrumentsConfigAudit.TablesInstrumentsConfigAudit.INSTRUMENTS_FAMILY, family,
                    fieldNames, fieldValues);
            messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.CONFIG_UPDATE_INSTRUMENT_FAMILY, new Object[]{family});
            return new InternalMessage(LPPlatform.LAB_TRUE, InstrEventsErrorTrapping.EVENT_NOTHING_PENDING, null, null);
        } else {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateTableRecordFieldsByFilter.getErrorMessageCode(), updateTableRecordFieldsByFilter.getErrorMessageVariables(), null);
        }
    }

}
