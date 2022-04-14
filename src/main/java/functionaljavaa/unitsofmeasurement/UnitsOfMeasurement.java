package functionaljavaa.unitsofmeasurement;

import databases.Rdbms;
import databases.TblsCnfg;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import java.math.BigDecimal;
import java.util.Arrays;
import trazit.enums.EnumIntMessages;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;
/**
 * functionality where units of measurements are involved
 * @author Fran Gomez
 * @version 0.1
 */
public class UnitsOfMeasurement {
    public enum UomImportType{INDIV, FAMIL}
    /**
     * @return the origQuantity
     */
    public BigDecimal getOrigQuantity() {
        return origQuantity;
    }

    /**
     * @return the origQuantityUom
     */
    public String getOrigQuantityUom() {
        return origQuantityUom;
    }
    /**
     * @return the convertedQuantity
     */
    public BigDecimal getConvertedQuantity() {
        return convertedQuantity;
    }

    /**
     * @return the convertedQuantityUom
     */
    public String getConvertedQuantityUom() {
        return convertedQuantityUom;
    }

    /**
     * @return the convertedFine
     */
    public Boolean getConvertedFine() {
        return convertedFine;
    }

    /**
     * @return the conversionErrorDetail
     */
    public Object[] getConversionErrorDetail() {
        return conversionErrorDetail;
    }

    /**
     * @return the conversionDetail
     */
    public Object[] getConversionDetail() {
        return conversionDetail;
    }
    private final BigDecimal origQuantity;
    private String origQuantityUom;
    private BigDecimal convertedQuantity;
    private String convertedQuantityUom;
    private Boolean convertedFine;
    private Object[] conversionErrorDetail;
    private Object[] conversionDetail;
    /**
     *
     */
    public enum UomErrorTrapping implements EnumIntMessages{ 
        CURRENT_UNITS_NOT_DEFINED("UnitsOfMeasurement_currentUnitsNotDefined", "unit <*1*> not defined in procedure <*2*>", "Unidad de medida <*1*> no definida en proceso <*2*>"),
        NEW_UNITS_NOT_DEFINED("UnitsOfMeasurement_newUnitsNotDefined","",""),
        SAME_VALUE_NOT_CONVERTED("UnitsOfMeasurement_sameValueNotConverted","",""),
        FAMILY_FIELD_NOT_IN_QUERY("UnitsOfMeasurement_methodError_familyFieldNotAddedToTheQuery","",""),
        ;
        private UomErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
    public static final String MESSAGE_TRAPPING_CONVERTED_SUCCESS="UnitsOfMeasurement_convertedSuccesfully";
    public static final String MESSAGE_LABELS_VALUE_CONVERTED="valueToConvert: ";
    public static final String MESSAGE_LABELS_CURRENT_UNIT="currentUnit: ";
    public static final String MESSAGE_LABELS_NEW_UNIT="newUnit: ";
    /**
     *
     * @param origQuant
     * @param origQuantUom
     */
    public UnitsOfMeasurement(BigDecimal origQuant, String origQuantUom){
        this.origQuantity=origQuant;
        this.origQuantityUom=origQuantUom;
    }
/**
 * Convert one value expressed in currentUnit to be expressed in newUnit.Notes:
  Units not assigned to any measurement family are not compatible so not convertible.Both units should belong to the same measurement family.
 * @param currentUnit String - The units as expressed the value before be converted
 * @param newUnit String - the units for the value once converted
 * @return Object[] - position 0 - boolean to know if converted (true) or not (false)
 *                  - position 1 - the new value once converted
 *                  - position 2 - the new units when converted or the current units when not converted
 *                  - position 3 - conclusion in a wording mode to provide further detail about what the method applied for this particular case
 */
    public Object[] twoUnitsInSameFamily(String currentUnit, String newUnit){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=procReqSession.getProcedureInstance();
        Object[] conversion = new Object[6];
        if (currentUnit==null){
            conversion = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, UomErrorTrapping.CURRENT_UNITS_NOT_DEFINED,
                        new Object[]{procInstanceName,  MESSAGE_LABELS_VALUE_CONVERTED+this.getOrigQuantity()+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(currentUnit)+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
            return conversion;
        }
        if (newUnit==null){
            conversion = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, UomErrorTrapping.NEW_UNITS_NOT_DEFINED,
                        new Object[]{procInstanceName,  MESSAGE_LABELS_VALUE_CONVERTED+this.getOrigQuantity()+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(currentUnit)+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
            return conversion;
        }
        if (newUnit.equals(currentUnit)){
            conversion = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, UomErrorTrapping.SAME_VALUE_NOT_CONVERTED,
                        new Object[]{procInstanceName,  MESSAGE_LABELS_VALUE_CONVERTED+this.getOrigQuantity()+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(currentUnit)+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
            conversion = LPArray.addValueToArray1D(conversion, this.getOrigQuantity());
            return conversion;
        }
        String tableName = TblsCnfg.TablesConfig.UOM.getTableName();
        String familyFieldNameDataBase = TblsCnfg.UnitsOfMeasurement.MEASUREMENT_FAMILY.getName();
        String schemaName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());

        String[] fieldsToGet = new String[]{TblsCnfg.UnitsOfMeasurement.NAME.getName(), familyFieldNameDataBase, TblsCnfg.UnitsOfMeasurement.IS_BASE.getName(),
            TblsCnfg.UnitsOfMeasurement.FACTOR_VALUE.getName(), TblsCnfg.UnitsOfMeasurement.OFFSET_VALUE.getName()};
        Object[][] currentUnitInfo = Rdbms.getRecordFieldsByFilter(schemaName, tableName,
                 new String[]{TblsCnfg.UnitsOfMeasurement.NAME.getName()},  new Object[]{currentUnit}, fieldsToGet );
        Object[][] newUnitInfo = Rdbms.getRecordFieldsByFilter(schemaName, tableName,
                 new String[]{TblsCnfg.UnitsOfMeasurement.NAME.getName()},  new Object[]{newUnit}, fieldsToGet);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(currentUnitInfo[0][0].toString())){
            return currentUnitInfo[0];
        }
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newUnitInfo[0][0].toString())){
            return newUnitInfo[0];
        }
        Integer currentUnitFamilyFieldPosic = Arrays.asList(fieldsToGet).indexOf(familyFieldNameDataBase);
        Integer newUnitFamilyFieldPosic = Arrays.asList(fieldsToGet).indexOf(familyFieldNameDataBase);
        if ((currentUnitFamilyFieldPosic==-1) || (newUnitFamilyFieldPosic==-1) ){
            conversion = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, UomErrorTrapping.FAMILY_FIELD_NOT_IN_QUERY,
                        new Object[]{familyFieldNameDataBase, Arrays.toString(fieldsToGet), procInstanceName,  MESSAGE_LABELS_VALUE_CONVERTED+this.getOrigQuantity()+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(currentUnit)+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
            return conversion;
        }
        if (!currentUnitInfo[0][currentUnitFamilyFieldPosic].toString().equalsIgnoreCase(newUnitInfo[0][currentUnitFamilyFieldPosic].toString())){
            conversion = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, UomErrorTrapping.FAMILY_FIELD_NOT_IN_QUERY,
                        new Object[]{currentUnit , currentUnitInfo[0][currentUnitFamilyFieldPosic].toString(),
                            newUnit, newUnitInfo[0][currentUnitFamilyFieldPosic].toString(),
                            procInstanceName,  MESSAGE_LABELS_VALUE_CONVERTED+this.getOrigQuantity()+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(currentUnit)+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
            return conversion;
        }
        conversion[0]=LPPlatform.LAB_TRUE;
        return conversion;
    }

    /**
     *
     * @param newUnit
     */
    public void convertValue(String newUnit){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=procReqSession.getProcedureInstance();
        Object[] unitsCompatible = twoUnitsInSameFamily(this.origQuantityUom, newUnit);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(unitsCompatible[0].toString())){
            this.convertedFine=false;
            this.conversionErrorDetail=unitsCompatible;
            return;
        }
        Object[] conversion = new Object[6];
        String tableName = TblsCnfg.TablesConfig.UOM.getTableName();
        String familyFieldNameDataBase = TblsCnfg.UnitsOfMeasurement.MEASUREMENT_FAMILY.getName();
        BigDecimal valueConverted = this.getOrigQuantity();

        String schemaName = GlobalVariables.Schemas.CONFIG.getName();
        schemaName = LPPlatform.buildSchemaName(procInstanceName, schemaName);

        String[] fieldsToGet = new String[]{TblsCnfg.UnitsOfMeasurement.NAME.getName(), familyFieldNameDataBase, TblsCnfg.UnitsOfMeasurement.IS_BASE.getName(),
            TblsCnfg.UnitsOfMeasurement.FACTOR_VALUE.getName(), TblsCnfg.UnitsOfMeasurement.OFFSET_VALUE.getName()};
        Object[][] currentUnitInfo = Rdbms.getRecordFieldsByFilter(schemaName, tableName,
                 new String[]{TblsCnfg.UnitsOfMeasurement.NAME.getName()},  new Object[]{this.getOrigQuantityUom()}, fieldsToGet );
        Object[][] newUnitInfo = Rdbms.getRecordFieldsByFilter(schemaName, tableName,
                 new String[]{TblsCnfg.UnitsOfMeasurement.NAME.getName()},  new Object[]{newUnit}, fieldsToGet);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(currentUnitInfo[0][0].toString())){
            this.convertedFine=false;
            this.conversionErrorDetail=currentUnitInfo;
            return;
        }
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newUnitInfo[0][0].toString())){
            this.convertedFine=false;
            this.conversionErrorDetail=newUnitInfo;
            return;
        }

        Integer currentUnitFamilyFieldPosic = Arrays.asList(fieldsToGet).indexOf(familyFieldNameDataBase);
        Integer newUnitFamilyFieldPosic = Arrays.asList(fieldsToGet).indexOf(familyFieldNameDataBase);
        if ((currentUnitFamilyFieldPosic==-1) || (newUnitFamilyFieldPosic==-1) ){
            conversion = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, UomErrorTrapping.FAMILY_FIELD_NOT_IN_QUERY,
                        new Object[]{familyFieldNameDataBase, Arrays.toString(fieldsToGet), procInstanceName,  MESSAGE_LABELS_VALUE_CONVERTED+this.getOrigQuantity()+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(this.getOrigQuantityUom())+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
            this.convertedFine=false;
            this.conversionErrorDetail=conversion;
            return;
        }
        if (!currentUnitInfo[0][currentUnitFamilyFieldPosic].toString().equalsIgnoreCase(newUnitInfo[0][currentUnitFamilyFieldPosic].toString())){
            conversion = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, UomErrorTrapping.FAMILY_FIELD_NOT_IN_QUERY,
                        new Object[]{this.getOrigQuantityUom(), currentUnitInfo[0][currentUnitFamilyFieldPosic].toString(),
                            newUnit, newUnitInfo[0][currentUnitFamilyFieldPosic].toString(),
                            procInstanceName,  MESSAGE_LABELS_VALUE_CONVERTED+this.getOrigQuantity()+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(this.getOrigQuantityUom())+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
            this.convertedFine=false;
            this.conversionErrorDetail=conversion;
            return;
        }
        BigDecimal currentUnitFactor = new BigDecimal(currentUnitInfo[0][3].toString());
        BigDecimal newUnitFactor = new BigDecimal(newUnitInfo[0][3].toString());
        BigDecimal currentUnitOffset = new BigDecimal(currentUnitInfo[0][4].toString());
        BigDecimal newUnitOffset = new BigDecimal(newUnitInfo[0][4].toString());

        newUnitFactor=newUnitFactor.divide(currentUnitFactor);
        valueConverted=valueConverted.multiply(newUnitFactor);
        newUnitOffset=newUnitOffset.add(currentUnitOffset.negate());
        valueConverted=valueConverted.add(newUnitOffset);

        conversion = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, MESSAGE_TRAPPING_CONVERTED_SUCCESS,
                        new Object[]{this.getOrigQuantityUom(), newUnitInfo, this.getOrigQuantity(), valueConverted, procInstanceName,
                             MESSAGE_LABELS_VALUE_CONVERTED+this.getOrigQuantity()+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(this.getOrigQuantityUom())+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
        conversion = LPArray.addValueToArray1D(conversion, valueConverted);
        conversion = LPArray.addValueToArray1D(conversion, newUnit);
        this.convertedFine=true;
        this.convertedQuantity=valueConverted;
        this.convertedQuantityUom=newUnit;
        this.conversionErrorDetail=null;
        this.conversionDetail=conversion;
    }
/**
 * Get all units of measurement from the same given family from one specific schema/procedure and getting as many fields
 * as the ones specified in fieldsToRetrieve.
 * @param family - String - The given family for the uom to be got.
 * @param fieldsToRetrieve String[] - Fields from the table that should be added to the array returned.
 * @return Object[][] - a dynamic table containing the fields specified in fieldsToRetrieve, position[0][3] set to FALSE when cannot buuilt.
 */
    public Object[][] getAllUnitsPerFamily(String family, String[] fieldsToRetrieve ){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=procReqSession.getProcedureInstance();
        String tableName = TblsCnfg.TablesConfig.UOM.getTableName();
        String schemaName = GlobalVariables.Schemas.CONFIG.getName();
        schemaName = LPPlatform.buildSchemaName(procInstanceName, schemaName);
        if (family==null){
            Object[] conversion = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, UomErrorTrapping.FAMILY_FIELD_NOT_IN_QUERY,
                                    new Object[]{procInstanceName});
            return LPArray.array1dTo2d(conversion, conversion.length);
        }

        Object[][] unitsList = Rdbms.getRecordFieldsByFilter(schemaName, tableName,
                 new String[]{TblsCnfg.UnitsOfMeasurement.MEASUREMENT_FAMILY.getName()},  new Object[]{family}, fieldsToRetrieve,
                 new String[]{TblsCnfg.UnitsOfMeasurement.FACTOR_VALUE.getName(), TblsCnfg.UnitsOfMeasurement.OFFSET_VALUE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(unitsList[0][0].toString())) return unitsList;

        return unitsList;
    }

    /**
     *
     * @param family
     * @return
     */
    public String getFamilyBaseUnitName(String family){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=procReqSession.getProcedureInstance();
        String tableName = TblsCnfg.TablesConfig.UOM.getTableName();
        String schemaName = GlobalVariables.Schemas.CONFIG.getName();
        schemaName = LPPlatform.buildSchemaName(procInstanceName, schemaName);

        Object[][] unitsList = Rdbms.getRecordFieldsByFilter(schemaName, tableName,
                 new String[]{TblsCnfg.UnitsOfMeasurement.MEASUREMENT_FAMILY.getName(), TblsCnfg.UnitsOfMeasurement.IS_BASE.getName()},  new Object[]{family, true}, new String[]{TblsCnfg.UnitsOfMeasurement.NAME.getName()});
        return unitsList[0][0].toString();
    }
    
    public static Object[] getUomFromConfig(String uomName, String importType){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=procReqSession.getProcedureInstance();    
        String[] whereFieldNames = new String[0];
        String[] whereFieldValues = new String[]{uomName};
        if (UomImportType.INDIV.toString().equalsIgnoreCase(importType)){
            whereFieldNames=new String[]{TblsCnfg.UnitsOfMeasurement.NAME.getName()};
        }
        if (UomImportType.FAMIL.toString().equalsIgnoreCase(importType)){
            whereFieldNames=new String[]{TblsCnfg.UnitsOfMeasurement.MEASUREMENT_FAMILY.getName()};
        }
        return Rdbms.insertRecordInTableFromTable(true,getAllFieldNames(TblsCnfg.TablesConfig.UOM.getTableFields()),
                GlobalVariables.Schemas.CONFIG.getName(), TblsCnfg.TablesConfig.UOM.getTableName(), 
                whereFieldNames, whereFieldValues,
                LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.UOM.getTableName(), getAllFieldNames(TblsCnfg.TablesConfig.UOM.getTableFields()));
    }
}
