/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import databases.Rdbms;
import lbplanet.utilities.LPArray;
import java.util.Arrays;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author Administrator
 */
public class DataProgramProductionLot{
    
    public enum ProductionLotErrorTrapping{ 
        PRODUCTIONLOT_ALREADY_EXIST("incubatorBatchExist", "One production lot called <*1*> already exist in procedure <*2*>", "Un lote de producción con el nombre <*1*> ya existe en el proceso <*2*>"),
        PRODUCTIONLOT_FIELD_NOT_FOUND("productionLot_fieldNotFound", "", ""),
        ;
        private ProductionLotErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        public String getErrorCode(){return this.errorCode;}
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    
    /**
     *
     * @param schemaPrefix
   * @param lotName
     * @param fieldName
     * @param fieldValue
     * @param personName
     * @param userRole
     * @param appSessionId
     * @return
     */    
    public static Object[] newProgramProductionLot(String schemaPrefix, String lotName, String[] fieldName, Object[] fieldValue, String personName, String userRole, Integer appSessionId) {
        String[] tblFlds=new String[0];
        Object[] batchExists=Rdbms.existsRecord(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.ProductionLot.TBL.getName(), 
                new String[]{TblsEnvMonitData.ProductionLot.FLD_LOT_NAME.getName()}, new Object[]{lotName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(batchExists[0].toString())){
            Object[] trapMessage = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ProductionLotErrorTrapping.PRODUCTIONLOT_ALREADY_EXIST.getErrorCode(), new Object[]{lotName, schemaPrefix});
            return LPArray.addValueToArray1D(trapMessage, new Object[]{lotName, schemaPrefix});
        }
        
        for (TblsEnvMonitData.ProductionLot obj: TblsEnvMonitData.ProductionLot.values()){
          tblFlds=LPArray.addValueToArray1D(tblFlds, obj.getName());
        }        
        if (fieldName==null)fieldName=new String[0];
        for (String curFld: fieldName){
          if (curFld.length()>0 && LPArray.valuePosicInArray(tblFlds, curFld)==-1)return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, 
                  ProductionLotErrorTrapping.PRODUCTIONLOT_FIELD_NOT_FOUND.getErrorCode(), new Object[]{curFld, lotName, Arrays.toString(fieldName), Arrays.toString(fieldValue), schemaPrefix});
        }
        fieldName=LPArray.addValueToArray1D(fieldName, TblsEnvMonitData.ProductionLot.FLD_LOT_NAME.getName());
        fieldValue=LPArray.addValueToArray1D(fieldValue, lotName);
        Integer posicInArr=LPArray.valuePosicInArray(fieldName, TblsEnvMonitData.ProductionLot.FLD_CREATED_BY.getName());
        if (posicInArr==-1){
          fieldName=LPArray.addValueToArray1D(fieldName, TblsEnvMonitData.ProductionLot.FLD_CREATED_BY.getName());
          fieldValue=LPArray.addValueToArray1D(fieldValue, personName);
        }else{fieldValue[posicInArr]=personName;}
        posicInArr=LPArray.valuePosicInArray(fieldName, TblsEnvMonitData.ProductionLot.FLD_CREATED_ON.getName());
        if (posicInArr==-1){
          fieldName=LPArray.addValueToArray1D(fieldName, TblsEnvMonitData.ProductionLot.FLD_CREATED_ON.getName());
          fieldValue=LPArray.addValueToArray1D(fieldValue, LPDate.getCurrentTimeStamp());
        }else{fieldValue[posicInArr]=LPDate.getCurrentTimeStamp();}
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.ProductionLot.TBL.getName(), fieldName, fieldValue);
    }

    /**
     *
     * @param schemaPrefix
     * @param lotName
     * @param personName
     * @param userRole
     * @param appSessionId
     * @return
     */
    public static Object[] activateProgramProductionLot(String schemaPrefix, String lotName, String personName, String userRole, Integer appSessionId) {
      String[] fieldName=new String[]{TblsEnvMonitData.ProductionLot.FLD_ACTIVE.getName()};
      Object[] fieldValue=new Object[]{true};
      return updateProgramProductionLot(schemaPrefix, lotName, fieldName, fieldValue, personName, userRole, appSessionId);
    }    

    /**
     *
     * @param schemaPrefix
     * @param lotName
     * @param personName
     * @param userRole
     * @param appSessionId
     * @return
     */
    public static Object[] deactivateProgramProductionLot(String schemaPrefix, String lotName, String personName, String userRole, Integer appSessionId) {
      String[] fieldName=new String[]{TblsEnvMonitData.ProductionLot.FLD_ACTIVE.getName()};
      Object[] fieldValue=new Object[]{false};
      return updateProgramProductionLot(schemaPrefix, lotName, fieldName, fieldValue, personName, userRole, appSessionId);
    }        
    private static Object[] updateProgramProductionLot(String schemaPrefix, String lotName, String[] fieldName, Object[] fieldValue, String personName, String userRole, Integer appSessionId) {
      return Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.ProductionLot.TBL.getName(), 
              fieldName, fieldValue, new String[]{TblsEnvMonitData.ProductionLot.FLD_LOT_NAME.getName()}, new Object[]{lotName});                 
    }
}
