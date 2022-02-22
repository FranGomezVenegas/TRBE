/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleinspectionlot;

import com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.InspLotRMAPIEndpoints;
import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMData;
import databases.Rdbms;
import databases.Token;
import functionaljavaa.audit.LotAudit;
import functionaljavaa.moduleinspectionlot.ModuleInspLotRMenum.DataInspLotCertificateStatuses;
import functionaljavaa.moduleinspectionlot.ModuleInspLotRMenum.DataInspLotCertificateTrackActions;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
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
            lotFieldName = LPArray.addValueToArray1D(lotFieldName, new String[]{TblsInspLotRMData.LotCertificate.FLD_LOT_NAME.getName(), TblsInspLotRMData.LotCertificate.FLD_STATUS.getName()});    
            lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, new Object[]{lotName, firstStatus});                         
            diagnoses = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.LotCertificate.TBL.getName(), 
                lotFieldName, lotFieldValue);
            if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, diagnoses[diagnoses.length-2]);
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ModuleInspLotRMenum.DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD, errorDetailVariables);
            }                                           
        }else{
            Object[] lotExists=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.LotCertificate.TBL.getName(), 
                    new String[]{TblsInspLotRMData.LotCertificate.FLD_LOT_NAME.getName(), TblsInspLotRMData.LotCertificate.FLD_CERTIFICATE_ID.getName()}, new Object[]{lotName, certifId});
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(lotExists[0].toString())){      
                diagnoses=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.LotCertificate.TBL.getName(), 
                    lotFieldName, lotFieldValue, 
                    new String[]{TblsInspLotRMData.LotCertificate.FLD_LOT_NAME.getName()}, new Object[]{lotName});
            }else{
                lotFieldName = LPArray.addValueToArray1D(lotFieldName, new String[]{TblsInspLotRMData.LotCertificate.FLD_LOT_NAME.getName(), TblsInspLotRMData.LotCertificate.FLD_STATUS.getName()});    
                lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, new Object[]{lotName, DataInspLotCertificateStatuses.NEW.toString()});                         
                diagnoses = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.LotCertificate.TBL.getName(), 
                    lotFieldName, lotFieldValue);
                if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
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
            lotAudit.lotAuditAdd(InspLotRMAPIEndpoints.LOT_TAKE_DECISION.getAuditActionName(), 
                    TblsInspLotRMData.Lot.TBL.getName(), lotName, lotName, fieldsOnLogLot, null);
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

        String[] lotFieldName=new String[]{TblsInspLotRMData.LotCertificateTrack.FLD_LOT_NAME.getName(), TblsInspLotRMData.LotCertificateTrack.FLD_ID.getName(), TblsInspLotRMData.LotCertificateTrack.FLD_EVENT.getName(), TblsInspLotRMData.LotCertificateTrack.FLD_CREATED_BY.getName(), TblsInspLotRMData.LotCertificateTrack.FLD_CREATED_ON.getName()};
        Object[] lotFieldValue=new Object[]{lotName, certifId, eventName, token.getPersonName(), LPDate.getCurrentTimeStamp()};
        Object[] errorDetailVariables=new Object[]{};
        try{
            DataInspLotCertificateTrackActions action = DataInspLotCertificateTrackActions.valueOf(eventName);
            Object[] diagnoses = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.LotCertificateTrack.TBL.getName(), 
                lotFieldName, lotFieldValue);
            if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, diagnoses[diagnoses.length-2]);
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ModuleInspLotRMenum.DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD, errorDetailVariables);
            }                                        
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "lotCertificateTrackEventRecorded", new Object[]{lotName, certifId, eventName, procInstanceName});            
        }catch(Exception e){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "lotCertificateTrackEventNotRecognized", new Object[]{lotName, certifId, eventName, procInstanceName});
        }
    }
}
