/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platformadmin;

import databases.Token;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.session.ResponseMessages;

/**
 *
 * @author User
 */
public class AdminActions {
    public static InternalMessage addWhiteIp(String val1, String val2, String val3, String val4, String description){   
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        messages.addMainForError(TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
        Object ipId = null;
        return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, new Object[]{}, ipId);
        
    }
/*        
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
        Object[] instCreationDiagn = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instCreationDiagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, instCreationDiagn[instCreationDiagn.length-1].toString(), new Object[]{name}, null);
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.CREATION, name, TablesAppProcData.INSTRUMENTS.getTableName(), name,
                        fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.NEW_INSTRUMENT, new Object[]{name});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.NEW_INSTRUMENT, new Object[]{name}, name);
    }
*/
}
    

