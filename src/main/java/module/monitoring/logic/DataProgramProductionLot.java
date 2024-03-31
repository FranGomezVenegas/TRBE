/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.logic;

import module.monitoring.definition.TblsEnvMonitData;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.features.Token;
import lbplanet.utilities.LPArray;
import java.util.Arrays;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
/**
 *
 * @author Administrator
 */
public class DataProgramProductionLot{
    
    public enum ProductionLotErrorTrapping implements EnumIntMessages{ 
        PRODUCTIONLOT_ALREADY_EXIST("productionLotAlreadyExist", "One production lot called <*1*> already exist in procedure <*2*>", "Un lote de producción con el nombre <*1*> ya existe en el proceso <*2*>"),
        PRODUCTIONLOT_ALREADY_ACTIVE("productionLotAlreadyActive", "", ""),
        PRODUCTIONLOT_FIELD_NOT_FOUND("productionLot_fieldNotFound", "", ""),
        PRODUCTIONLOT_ALREADY_CLOSED("productionLot_alreadyClosed", "", ""),
        ;
        private ProductionLotErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    private static InternalMessage isProLotOpen(String lotName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] batchExists=Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableName(), 
            new String[]{TblsEnvMonitData.ProductionLot.LOT_NAME.getName(), TblsEnvMonitData.ProductionLot.ACTIVE.getName()}, 
            new Object[]{lotName, true});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(batchExists[0].toString()))
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, new Object[]{lotName, procInstanceName});
        batchExists=Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableName(), 
            new String[]{TblsEnvMonitData.ProductionLot.LOT_NAME.getName()}, new Object[]{lotName});   
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchExists[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, ProductionLotErrorTrapping.PRODUCTIONLOT_FIELD_NOT_FOUND, new Object[]{lotName, procInstanceName});
        else
            return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, new Object[]{lotName});
    }
    /**
     *
     * @param lotName
     * @param fieldName
     * @param fieldValue
     * @return
     */    
    public static InternalMessage newProgramProductionLot(String lotName, String[] fieldName, Object[] fieldValue) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        String[] tblFlds=new String[0];
        Object[] batchExists=Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableName(), 
                new String[]{TblsEnvMonitData.ProductionLot.LOT_NAME.getName()}, new Object[]{lotName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(batchExists[0].toString())){
            return new InternalMessage(LPPlatform.LAB_FALSE, ProductionLotErrorTrapping.PRODUCTIONLOT_ALREADY_EXIST, new Object[]{lotName, procInstanceName});
            //return LPArray.addValueToArray1D(trapMessage, new Object[]{lotName, procInstanceName});
        }
        
        for (TblsEnvMonitData.ProductionLot obj: TblsEnvMonitData.ProductionLot.values()){
          tblFlds=LPArray.addValueToArray1D(tblFlds, obj.getName());
        }        
        if (fieldName==null)fieldName=new String[0];
        for (String curFld: fieldName){
          if (curFld.length()>0 && LPArray.valuePosicInArray(tblFlds, curFld)==-1)return new InternalMessage(LPPlatform.LAB_FALSE, 
                  ProductionLotErrorTrapping.PRODUCTIONLOT_FIELD_NOT_FOUND, new Object[]{curFld, lotName, Arrays.toString(fieldName), Arrays.toString(fieldValue), procInstanceName});
        }
        fieldName=LPArray.addValueToArray1D(fieldName, TblsEnvMonitData.ProductionLot.LOT_NAME.getName());
        fieldValue=LPArray.addValueToArray1D(fieldValue, lotName);
        Integer posicInArr=LPArray.valuePosicInArray(fieldName, TblsEnvMonitData.ProductionLot.CREATED_BY.getName());
        if (posicInArr==-1){
          fieldName=LPArray.addValueToArray1D(fieldName, TblsEnvMonitData.ProductionLot.CREATED_BY.getName());
          fieldValue=LPArray.addValueToArray1D(fieldValue, token.getPersonName());
        }else{fieldValue[posicInArr]=token.getPersonName();}
        posicInArr=LPArray.valuePosicInArray(fieldName, TblsEnvMonitData.ProductionLot.CREATED_ON.getName());
        if (posicInArr==-1){
          fieldName=LPArray.addValueToArray1D(fieldName, TblsEnvMonitData.ProductionLot.CREATED_ON.getName());
          fieldValue=LPArray.addValueToArray1D(fieldValue, LPDate.getCurrentTimeStamp());
        }else{fieldValue[posicInArr]=LPDate.getCurrentTimeStamp();}
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT, fieldName, fieldValue);
        return new InternalMessage(LPPlatform.LAB_TRUE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), insertRecordInTable.getNewRowId());
    }

    /**
     *
     * @param lotName
     * @return
     */
    public static InternalMessage activateProgramProductionLot(String lotName){
        InternalMessage proLotOpen = isProLotOpen(lotName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(proLotOpen.getDiagnostic()))
            return proLotOpen;
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] batchExistsAndActive=Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableName(), 
            new String[]{TblsEnvMonitData.ProductionLot.LOT_NAME.getName(), TblsEnvMonitData.ProductionLot.ACTIVE.getName()}, 
            new Object[]{lotName, true});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(batchExistsAndActive[0].toString()))   
            return new InternalMessage(LPPlatform.LAB_FALSE, ProductionLotErrorTrapping.PRODUCTIONLOT_ALREADY_ACTIVE, new Object[]{lotName, procInstanceName});
        String[] fieldName=new String[]{TblsEnvMonitData.ProductionLot.ACTIVE.getName()};
        Object[] fieldValue=new Object[]{true};
        return updateProgramProductionLot(lotName, fieldName, fieldValue);
    }    

    /**
     *
     * @param lotName
     * @return
     */
    public static InternalMessage deactivateProgramProductionLot(String lotName) {
        InternalMessage proLotOpen = isProLotOpen(lotName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(proLotOpen.getDiagnostic()))
            return proLotOpen;
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);                
        String personName = instanceForActions.getToken().getPersonName();
        String[] fieldName=new String[]{TblsEnvMonitData.ProductionLot.ACTIVE.getName(), TblsEnvMonitData.ProductionLot.CLOSED_ON.getName(),
        TblsEnvMonitData.ProductionLot.CLOSED_BY.getName()};
        Object[] fieldValue=new Object[]{false, LPDate.getCurrentTimeStamp(), personName};
        return updateProgramProductionLot(lotName, fieldName, fieldValue);
    }        
    private static InternalMessage updateProgramProductionLot(String lotName, String[] fieldName, Object[] fieldValue){
        InternalMessage proLotOpen = isProLotOpen(lotName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(proLotOpen.getDiagnostic()))
            return proLotOpen;
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsEnvMonitData.ProductionLot.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, "");
        RdbmsObject updateRecordFieldsByFilter = Rdbms.updateTableRecordFieldsByFilter(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT,
                EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT, fieldName), fieldValue, sqlWhere, null);
        return new InternalMessage(updateRecordFieldsByFilter.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, updateRecordFieldsByFilter.getErrorMessageCode(), updateRecordFieldsByFilter.getErrorMessageVariables(), null);
    }
}
