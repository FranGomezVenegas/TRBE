/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.logic;

import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.features.Token;
import module.inspectionlot.rawmaterial.definition.LotAudit;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.DataInspLotCertificateStatuses;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.DataInspLotCertificateTrackActions;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.InspectionLotRMAuditEvents;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
/**
 *
 * @author User
 */
public class DataInspectionLotCertificate {
    public InternalMessage lotCertificateRecordCreateOrUpdate(String lotName, Integer certifId, String newStatus){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String firstStatus=DataInspLotCertificateStatuses.NEW.toString();
        String[] lotFieldName=new String[]{};
        Object[] lotFieldValue=new Object[]{};
        Object[] errorDetailVariables=new Object[]{};
        Object[] diagnoses=new Object[]{};
        EnumIntAuditEvents auditEvObj=null;
        if (certifId==null){
            lotFieldName = LPArray.addValueToArray1D(lotFieldName, new String[]{TblsInspLotRMData.LotCertificate.LOT_NAME.getName(), TblsInspLotRMData.LotCertificate.STATUS.getName()});    
            lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, new Object[]{lotName, firstStatus});                         
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMData.TablesInspLotRMData.LOT, lotFieldName, lotFieldValue);
            if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())){
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, diagnoses[diagnoses.length-2]);
                return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD,  errorDetailVariables); 
            }                                           
        }else{
            Object[] lotExists=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), 
                    new String[]{TblsInspLotRMData.LotCertificate.LOT_NAME.getName(), TblsInspLotRMData.LotCertificate.CERTIFICATE_ID.getName()}, new Object[]{lotName, certifId});
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(lotExists[0].toString())){      
                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsInspLotRMData.LotCertificate.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
                RdbmsObject updateRecordFieldsByFilter = Rdbms.updateTableRecordFieldsByFilter(TblsInspLotRMData.TablesInspLotRMData.LOT,
                        EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMData.TablesInspLotRMData.LOT, lotFieldName), lotFieldValue, sqlWhere, null);
                if (Boolean.FALSE.equals(updateRecordFieldsByFilter.getRunSuccess()))
                    return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordFieldsByFilter.getErrorMessageCode(),  updateRecordFieldsByFilter.getErrorMessageVariables()); 
                auditEvObj=InspectionLotRMAuditEvents.LOT_CERTIFICATE_UPDATED;
            }else{
                lotFieldName = LPArray.addValueToArray1D(lotFieldName, new String[]{TblsInspLotRMData.LotCertificate.LOT_NAME.getName(), TblsInspLotRMData.LotCertificate.STATUS.getName()});    
                lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, new Object[]{lotName, DataInspLotCertificateStatuses.NEW.toString()});                         
                RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMData.TablesInspLotRMData.LOT, lotFieldName, lotFieldValue);
                if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())){
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, diagnoses[diagnoses.length-2]);
                    return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD, errorDetailVariables);
                }  
                auditEvObj=InspectionLotRMAuditEvents.LOT_CERTIFICATE_INSERTED;
            }
        }
        diagnoses = LPArray.addValueToArray1D(diagnoses, diagnoses[diagnoses.length-1]);
            LotAudit lotAudit = new LotAudit();            
            lotAudit.lotAuditAdd(auditEvObj, 
                    TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName, lotName, lotFieldName, lotFieldValue);
            return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD, new Object[]{lotName, newStatus, procInstanceName});
//        }
//        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "", null);
    }
    public Object[] lotCertificatePrint(String lotName, Integer certifId){
        return lotCertificateTrackEvent(lotName, certifId, DataInspLotCertificateTrackActions.PRINT.toString());
    }
    
    private Object[] lotCertificateTrackEvent(String lotName, Integer certifId, String eventName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        String[] lotFieldName=new String[]{TblsInspLotRMData.LotCertificateTrack.LOT_NAME.getName(), TblsInspLotRMData.LotCertificateTrack.ID.getName(), TblsInspLotRMData.LotCertificateTrack.EVENT.getName(), TblsInspLotRMData.LotCertificateTrack.CREATED_BY.getName(), TblsInspLotRMData.LotCertificateTrack.CREATED_ON.getName()};
        Object[] lotFieldValue=new Object[]{lotName, certifId, eventName, token.getPersonName(), LPDate.getCurrentTimeStamp()};
        Object[] errorDetailVariables=new Object[]{};
        try{
            DataInspLotCertificateTrackActions action = DataInspLotCertificateTrackActions.valueOf(eventName);
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMData.TablesInspLotRMData.LOT_CERTIFICATE_TRACK, lotFieldName, lotFieldValue);
            if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())){
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, insertRecordInTable.getApiMessage());
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD, errorDetailVariables);
            }                                        
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "lotCertificateTrackEventRecorded", new Object[]{lotName, certifId, eventName, procInstanceName});            
        }catch(Exception e){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "lotCertificateTrackEventNotRecognized", new Object[]{lotName, certifId, eventName, procInstanceName});
        }
    }
}
