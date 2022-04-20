/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import databases.Rdbms;
import databases.features.Token;
import lbplanet.utilities.LPArray;
import java.util.Arrays;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntMessages;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author Administrator
 */
public class DataProgramProductionLot{
    
    public enum ProductionLotErrorTrapping implements EnumIntMessages{ 
        PRODUCTIONLOT_ALREADY_EXIST("productionLotAlreadyExist", "One production lot called <*1*> already exist in procedure <*2*>", "Un lote de producción con el nombre <*1*> ya existe en el proceso <*2*>"),
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
    private static Object[] isProLotOpen(String lotName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] batchExists=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableName(), 
            new String[]{TblsEnvMonitData.ProductionLot.LOT_NAME.getName(), TblsEnvMonitData.ProductionLot.ACTIVE.getName()}, new Object[]{lotName, false});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(batchExists[0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ProductionLotErrorTrapping.PRODUCTIONLOT_ALREADY_CLOSED, new Object[]{lotName, procInstanceName});
        batchExists=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableName(), 
            new String[]{TblsEnvMonitData.ProductionLot.LOT_NAME.getName()}, new Object[]{lotName});   
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchExists[0].toString()))
            return batchExists;
        else
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "productLotIsOpen", new Object[]{lotName});
    }
    /**
     *
     * @param lotName
     * @param fieldName
     * @param fieldValue
     * @return
     */    
    public static Object[] newProgramProductionLot(String lotName, String[] fieldName, Object[] fieldValue) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        String[] tblFlds=new String[0];
        Object[] batchExists=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableName(), 
                new String[]{TblsEnvMonitData.ProductionLot.LOT_NAME.getName()}, new Object[]{lotName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(batchExists[0].toString())){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ProductionLotErrorTrapping.PRODUCTIONLOT_ALREADY_EXIST, new Object[]{lotName, procInstanceName});
            //return LPArray.addValueToArray1D(trapMessage, new Object[]{lotName, procInstanceName});
        }
        
        for (TblsEnvMonitData.ProductionLot obj: TblsEnvMonitData.ProductionLot.values()){
          tblFlds=LPArray.addValueToArray1D(tblFlds, obj.getName());
        }        
        if (fieldName==null)fieldName=new String[0];
        for (String curFld: fieldName){
          if (curFld.length()>0 && LPArray.valuePosicInArray(tblFlds, curFld)==-1)return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, 
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
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableName(), fieldName, fieldValue);
    }

    /**
     *
     * @param lotName
     * @return
     */
    public static Object[] activateProgramProductionLot(String lotName){
        Object[] proLotOpen = isProLotOpen(lotName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(proLotOpen[0].toString()))
            return proLotOpen;
        String[] fieldName=new String[]{TblsEnvMonitData.ProductionLot.ACTIVE.getName()};
        Object[] fieldValue=new Object[]{true};
        return updateProgramProductionLot(lotName, fieldName, fieldValue);
    }    

    /**
     *
     * @param lotName
     * @return
     */
    public static Object[] deactivateProgramProductionLot(String lotName) {
        Object[] proLotOpen = isProLotOpen(lotName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(proLotOpen[0].toString()))
            return proLotOpen;
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);                
        String personName = instanceForActions.getToken().getPersonName();
        String[] fieldName=new String[]{TblsEnvMonitData.ProductionLot.ACTIVE.getName(), TblsEnvMonitData.ProductionLot.CLOSED_ON.getName(),
        TblsEnvMonitData.ProductionLot.CLOSED_BY.getName()};
        Object[] fieldValue=new Object[]{false, LPDate.getCurrentTimeStamp(), personName};
        return updateProgramProductionLot(lotName, fieldName, fieldValue);
    }        
    private static Object[] updateProgramProductionLot(String lotName, String[] fieldName, Object[] fieldValue){
        Object[] proLotOpen = isProLotOpen(lotName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(proLotOpen[0].toString()))
            return proLotOpen;
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        return Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableName(), 
              fieldName, fieldValue, new String[]{TblsEnvMonitData.ProductionLot.LOT_NAME.getName()}, new Object[]{lotName});                 
    }
}
