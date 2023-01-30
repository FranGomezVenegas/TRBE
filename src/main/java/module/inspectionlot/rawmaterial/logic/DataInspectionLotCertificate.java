/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.logic;

import module.inspectionlot.rawmaterial.apis.InspLotRMAPIactions.InspLotRMAPIactionsEndpoints;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.features.Token;
import functionaljavaa.audit.LotAudit;
import module.inspectionlot.rawmaterial.logic.ModuleInspLotRMenum.DataInspLotCertificateStatuses;
import module.inspectionlot.rawmaterial.logic.ModuleInspLotRMenum.DataInspLotCertificateTrackActions;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author User
 */
public class DataInspectionLotCertificate {
    public Object[] lotCertificateRecordCreateOrUpdate(String lotName, Integer certifId, String newStatus){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String firstStatus=DataInspLotCertificateStatuses.NEW.toString();
        String[] lotFieldName=new String[]{};
        Object[] lotFieldValue=new Object[]{};
        Object[] errorDetailVariables=new Object[]{};
        Object[] diagnoses=new Object[]{};
        if (certifId==null){
            lotFieldName = LPArray.addValueToArray1D(lotFieldName, new String[]{TblsInspLotRMData.LotCertificate.LOT_NAME.getName(), TblsInspLotRMData.LotCertificate.STATUS.getName()});    
            lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, new Object[]{lotName, firstStatus});                         
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMData.TablesInspLotRMData.LOT, lotFieldName, lotFieldValue);
            if (!insertRecordInTable.getRunSuccess()){
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, diagnoses[diagnoses.length-2]);
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ModuleInspLotRMenum.DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD, errorDetailVariables);
            }                                           
        }else{
            Object[] lotExists=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), 
                    new String[]{TblsInspLotRMData.LotCertificate.LOT_NAME.getName(), TblsInspLotRMData.LotCertificate.CERTIFICATE_ID.getName()}, new Object[]{lotName, certifId});
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(lotExists[0].toString())){      
                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsInspLotRMData.LotCertificate.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
                diagnoses=Rdbms.updateRecordFieldsByFilter(TblsInspLotRMData.TablesInspLotRMData.LOT,
                    EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMData.TablesInspLotRMData.LOT, lotFieldName), lotFieldValue, sqlWhere, null);
            }else{
                lotFieldName = LPArray.addValueToArray1D(lotFieldName, new String[]{TblsInspLotRMData.LotCertificate.LOT_NAME.getName(), TblsInspLotRMData.LotCertificate.STATUS.getName()});    
                lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, new Object[]{lotName, DataInspLotCertificateStatuses.NEW.toString()});                         
                RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMData.TablesInspLotRMData.LOT, lotFieldName, lotFieldValue);
                if (!insertRecordInTable.getRunSuccess()){
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, diagnoses[diagnoses.length-2]);
                    return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ModuleInspLotRMenum.DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD, errorDetailVariables);
                }                                
            }
        }
        Object[] fieldsOnLogLot = LPArray.joinTwo1DArraysInOneOf1DString(lotFieldName, lotFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR);
        diagnoses = LPArray.addValueToArray1D(diagnoses, diagnoses[diagnoses.length-1]);
//        if (Rdbms.TBL_NO_KEY.equalsIgnoreCase(diagnoses[diagnoses.length-1].toString())){return diagnoses;}
//        if (decision!=null && decision.length()>0){
            LotAudit lotAudit = new LotAudit();            
            lotAudit.lotAuditAdd(InspLotRMAPIactionsEndpoints.LOT_TAKE_DECISION.getAuditActionName(), 
                    TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName, lotName, fieldsOnLogLot, null);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "LotCertificateNewStatus", new Object[]{lotName, newStatus, procInstanceName});
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
            if (!insertRecordInTable.getRunSuccess()){
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, insertRecordInTable.getApiMessage());
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ModuleInspLotRMenum.DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD, errorDetailVariables);
            }                                        
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "lotCertificateTrackEventRecorded", new Object[]{lotName, certifId, eventName, procInstanceName});            
        }catch(Exception e){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "lotCertificateTrackEventNotRecognized", new Object[]{lotName, certifId, eventName, procInstanceName});
        }
    }
}
