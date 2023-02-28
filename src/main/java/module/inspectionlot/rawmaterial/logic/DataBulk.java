/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.features.Token;
import functionaljavaa.audit.LotAudit;
import functionaljavaa.materialspec.SamplingPlanEntry;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import static module.inspectionlot.rawmaterial.logic.DataInspectionLot.applySamplingPlan;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class DataBulk {
    public static InternalMessage createBulk(String lotName, String materialName){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String[] fieldName=new String[]{TblsInspLotRMData.InventoryRetain.LOT_NAME.getName(), TblsInspLotRMData.InventoryRetain.MATERIAL_NAME.getName(),
            TblsInspLotRMData.InventoryRetain.CREATED_BY.getName(), TblsInspLotRMData.InventoryRetain.CREATED_ON.getName()};
        Object[] fieldValue=new Object[]{lotName, materialName, 
            procReqSession.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN, fieldName, fieldValue);        
        return new InternalMessage(LPPlatform.LAB_TRUE, 
            InspLotRMEnums.InspLotRMAPIactionsEndpoints.NEW_LOT, new Object[]{lotName}, insertRecordInTable.getNewRowId());
    }   
    
    
    public static InternalMessage lotBulkTakeDecision(String lotName, Integer containerId, String decision, String[] fieldName, Object[] fieldValue) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        Object[][] lotInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), 
            new String[]{TblsInspLotRMData.Lot.NAME.getName()}, new Object[]{lotName}, 
            new String[]{TblsInspLotRMData.Lot.MATERIAL_NAME.getName(), TblsInspLotRMData.Lot.SPEC_CODE.getName(),
                TblsInspLotRMData.Lot.SPEC_CODE_VERSION.getName(), TblsInspLotRMData.Lot.QUANTITY.getName(),
                TblsInspLotRMData.Lot.NUM_CONTAINERS.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) 
            new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        
        InternalMessage lotContainerDecisionRecordCreateOrUpdate = lotBulkDecisionRecordCreateOrUpdate(lotName, containerId, decision);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotContainerDecisionRecordCreateOrUpdate.getDiagnostic()))
            return lotContainerDecisionRecordCreateOrUpdate;
        if (decision.toString().toLowerCase().contains("ACCEPT")){
            SamplingPlanEntry spEntry=new SamplingPlanEntry(lotInfo[0][0].toString(), lotInfo[0][1].toString(), Integer.valueOf(lotInfo[0][2].toString()), 
                Integer.valueOf(lotInfo[0][3].toString()), Integer.valueOf(lotInfo[0][4].toString()));
            if (spEntry.getHasErrors())
                return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.SAMPLEPLAN_CHECKER_ERROR, new Object[]{Arrays.toString(spEntry.getErrorsArr())});
            applySamplingPlan(lotName, lotInfo[0][0].toString(), lotInfo[0][1].toString(), Integer.valueOf(lotInfo[0][2].toString()), 
                Integer.valueOf(lotInfo[0][3].toString()), Integer.valueOf(lotInfo[0][4].toString()), 
                    null, null, spEntry, containerId);
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, 
            InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_BULK_TAKE_DECISION, new Object[]{lotName});        

        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "NotImplementedYet", null);
    }
    
    public static InternalMessage lotBulkDecisionRecordCreateOrUpdate(String lotName, Integer containerId, String decision){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String[] lotFieldName=new String[]{};
        Object[] lotFieldValue=new Object[]{};
        Object[] errorDetailVariables=new Object[]{};
        Object[] diagnoses=new Object[]{};

        if (decision!=null && decision.length()>0){
            lotFieldName = LPArray.addValueToArray1D(lotFieldName, new String[]{TblsInspLotRMData.LotDecision.DECISION.getName(), TblsInspLotRMData.LotDecision.DECISION_TAKEN_BY.getName(), TblsInspLotRMData.LotDecision.DECISION_TAKEN_ON.getName()});    
            lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, new Object[]{decision, token.getPersonName(), LPDate.getCurrentTimeStamp()});                                         
        }
        Object[] lotExists=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT_DECISION.getTableName(), 
                new String[]{TblsInspLotRMData.LotDecision.LOT_NAME.getName()}, new Object[]{lotName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(lotExists[0].toString())){      
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsInspLotRMData.LotDecision.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
            diagnoses=Rdbms.updateRecordFieldsByFilter(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK,
                EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK, lotFieldName), lotFieldValue, sqlWhere, null);
        }else{
            lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.LotDecision.LOT_NAME.getName());    
            lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, lotName);                         
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK, lotFieldName, lotFieldValue);
            if (!insertRecordInTable.getRunSuccess()){
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, insertRecordInTable.getNewRowId());
                return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD, errorDetailVariables);
            }                                
        }
        Object[] fieldsOnLogLot = LPArray.joinTwo1DArraysInOneOf1DString(lotFieldName, lotFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR);
//        diagnoses = LPArray.addValueToArray1D(diagnoses, diagnoses[diagnoses.length-1]);

//        if (Rdbms.TBL_NO_KEY.equalsIgnoreCase(diagnoses[diagnoses.length-1].toString())){return diagnoses;}
        if (decision!=null && decision.length()>0){
            LotAudit lotAudit = new LotAudit();            
            lotAudit.lotAuditAdd(InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_TAKE_DECISION.getAuditActionName(), 
                    TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName, lotName, fieldsOnLogLot, null);
            return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_TAKE_DECISION, new Object[]{lotName, decision, procInstanceName});
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, Rdbms.RdbmsSuccess.RDBMS_TABLE_FOUND, null);
    }
    
}
