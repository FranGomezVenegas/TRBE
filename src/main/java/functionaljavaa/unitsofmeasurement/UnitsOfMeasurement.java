/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.unitsofmeasurement;

import databases.Rdbms;
import databases.TblsCnfg;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import java.math.BigDecimal;
import java.util.Arrays;
import trazit.globalvariables.GlobalVariables;
/**
 * functionality where units of measurements are involved
 * @author Fran Gomez
 * @version 0.1
 */
public class UnitsOfMeasurement {
    String classVersion = "0.1";
    
    /**
     *
     */
    public static final String ERROR_TRAPPING_CURRENT_UNITS_NOT_DEFINED="UnitsOfMeasurement_currentUnitsNotDefined";

    /**
     *
     */
    public static final String ERROR_TRAPPING_NEW_UNITS_NOT_DEFINED="UnitsOfMeasurement_newUnitsNotDefined";

    /**
     *
     */
    public static final String ERROR_TRAPPING_SAME_VALUE_NOT_CONVERTED="UnitsOfMeasurement_sameValueNotConverted";

    /**
     *
     */
    public static final String ERROR_TRAPPING_FAMILY_FIELD_NOT_IN_QUERY="UnitsOfMeasurement_methodError_familyFieldNotAddedToTheQuery";

    /**
     *
     */
    public static final String MESSAGE_TRAPPING_CONVERTED_SUCCESS="UnitsOfMeasurement_convertedSuccesfully";
    
    /**
     *
     */
    public static final String MESSAGE_LABELS_VALUE_CONVERTED="valueToConvert: ";

    /**
     *
     */
    public static final String MESSAGE_LABELS_CURRENT_UNIT="currentUnit: ";

    /**
     *
     */
    public static final String MESSAGE_LABELS_NEW_UNIT="newUnit: ";
    /**
     *
     */
    public UnitsOfMeasurement(){
        // Not implemented yet
    }
/**
 * Convert one value expressed in currentUnit to be expressed in newUnit. Notes: 
 *  Units not assigned to any measurement family are not compatible so not convertible.
 *  Both units should belong to the same measurement family.
 * @param procInstanceName String - The schema/procedure where both uom should be present. 
 * @param valueToConvert Float - The value to be converted
 * @param currentUnit String - The units as expressed the value before be converted
 * @param newUnit String - the units for the value once converted
 * @return Object[] - position 0 - boolean to know if converted (true) or not (false)
 *                  - position 1 - the new value once converted
 *                  - position 2 - the new units when converted or the current units when not converted
 *                  - position 3 - conclusion in a wording mode to provide further detail about what the method applied for this particular case
 */    
    public Object[] twoUnitsInSameFamily( String procInstanceName, BigDecimal valueToConvert, String currentUnit, String newUnit){
        Object[] conversion = new Object[6];        
        if (currentUnit==null){
            conversion = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_CURRENT_UNITS_NOT_DEFINED,
                        new Object[]{procInstanceName,  MESSAGE_LABELS_VALUE_CONVERTED+valueToConvert+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(currentUnit)+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
            return conversion;
        }
        if (newUnit==null){
            conversion = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_NEW_UNITS_NOT_DEFINED,
                        new Object[]{procInstanceName,  MESSAGE_LABELS_VALUE_CONVERTED+valueToConvert+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(currentUnit)+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
            return conversion;
        }        
        if (newUnit.equals(currentUnit)){
            conversion = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_SAME_VALUE_NOT_CONVERTED,
                        new Object[]{procInstanceName,  MESSAGE_LABELS_VALUE_CONVERTED+valueToConvert+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(currentUnit)+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
            conversion = LPArray.addValueToArray1D(conversion, valueToConvert);
            return conversion;
        }            
        String tableName = TblsCnfg.UnitsOfMeasurement.TBL.getName();
        String familyFieldNameDataBase = TblsCnfg.UnitsOfMeasurement.FLD_MEASUREMENT_FAMILY.getName();                
        String schemaName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
             
        String[] fieldsToGet = new String[]{TblsCnfg.UnitsOfMeasurement.FLD_NAME.getName(), familyFieldNameDataBase, TblsCnfg.UnitsOfMeasurement.FLD_IS_BASE.getName(), 
            TblsCnfg.UnitsOfMeasurement.FLD_FACTOR_VALUE.getName(), TblsCnfg.UnitsOfMeasurement.FLD_OFFSET_VALUE.getName()};
        Object[][] currentUnitInfo = Rdbms.getRecordFieldsByFilter(schemaName, tableName, 
                 new String[]{TblsCnfg.UnitsOfMeasurement.FLD_NAME.getName()},  new Object[]{currentUnit}, fieldsToGet );
        Object[][] newUnitInfo = Rdbms.getRecordFieldsByFilter(schemaName, tableName, 
                 new String[]{TblsCnfg.UnitsOfMeasurement.FLD_NAME.getName()},  new Object[]{newUnit}, fieldsToGet);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(currentUnitInfo[0][0].toString())){
            return conversion;            
        }        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newUnitInfo[0][0].toString())){
            return conversion;            
        }
        Integer currentUnitFamilyFieldPosic = Arrays.asList(fieldsToGet).indexOf(familyFieldNameDataBase);         
        Integer newUnitFamilyFieldPosic = Arrays.asList(fieldsToGet).indexOf(familyFieldNameDataBase);                
        if ((currentUnitFamilyFieldPosic==-1) || (newUnitFamilyFieldPosic==-1) ){
            conversion = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_FAMILY_FIELD_NOT_IN_QUERY,
                        new Object[]{familyFieldNameDataBase, Arrays.toString(fieldsToGet), procInstanceName,  MESSAGE_LABELS_VALUE_CONVERTED+valueToConvert+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(currentUnit)+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
            return conversion;            
        }                        
        if (!currentUnitInfo[0][currentUnitFamilyFieldPosic].toString().equalsIgnoreCase(newUnitInfo[0][currentUnitFamilyFieldPosic].toString())){
            conversion = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_FAMILY_FIELD_NOT_IN_QUERY,
                        new Object[]{currentUnit , currentUnitInfo[0][currentUnitFamilyFieldPosic].toString(), 
                            newUnit, newUnitInfo[0][currentUnitFamilyFieldPosic].toString(), 
                            procInstanceName,  MESSAGE_LABELS_VALUE_CONVERTED+valueToConvert+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(currentUnit)+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
            return conversion; 
        }
        conversion[0]=LPPlatform.LAB_TRUE;
        return conversion;        
    }
    
    /**
     *
     * @param procInstanceName
     * @param valueToConvert
     * @param currentUnit
     * @param newUnit
     * @return
     */
    public Object[] convertValue( String procInstanceName, BigDecimal valueToConvert, String currentUnit, String newUnit){
        
        Object[] unitsCompatible = twoUnitsInSameFamily(procInstanceName, valueToConvert, currentUnit, newUnit);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(unitsCompatible[0].toString())){
            return unitsCompatible;}
        
        Object[] conversion = new Object[6];
        String tableName = TblsCnfg.UnitsOfMeasurement.TBL.getName();
        String familyFieldNameDataBase = TblsCnfg.UnitsOfMeasurement.FLD_MEASUREMENT_FAMILY.getName();
        BigDecimal valueConverted = valueToConvert;
        
        String schemaName = GlobalVariables.Schemas.CONFIG.getName();
        schemaName = LPPlatform.buildSchemaName(procInstanceName, schemaName);
             
        String[] fieldsToGet = new String[]{TblsCnfg.UnitsOfMeasurement.FLD_NAME.getName(), familyFieldNameDataBase, TblsCnfg.UnitsOfMeasurement.FLD_IS_BASE.getName(), 
            TblsCnfg.UnitsOfMeasurement.FLD_FACTOR_VALUE.getName(), TblsCnfg.UnitsOfMeasurement.FLD_OFFSET_VALUE.getName()};
        Object[][] currentUnitInfo = Rdbms.getRecordFieldsByFilter(schemaName, tableName, 
                 new String[]{TblsCnfg.UnitsOfMeasurement.FLD_NAME.getName()},  new Object[]{currentUnit}, fieldsToGet );
        Object[][] newUnitInfo = Rdbms.getRecordFieldsByFilter(schemaName, tableName, 
                 new String[]{TblsCnfg.UnitsOfMeasurement.FLD_NAME.getName()},  new Object[]{newUnit}, fieldsToGet);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(currentUnitInfo[0][0].toString())){
            return conversion;            
        }        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newUnitInfo[0][0].toString())){
            return conversion;            
        }
        
        Integer currentUnitFamilyFieldPosic = Arrays.asList(fieldsToGet).indexOf(familyFieldNameDataBase);         
        Integer newUnitFamilyFieldPosic = Arrays.asList(fieldsToGet).indexOf(familyFieldNameDataBase);                
        if ((currentUnitFamilyFieldPosic==-1) || (newUnitFamilyFieldPosic==-1) ){
            conversion = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_FAMILY_FIELD_NOT_IN_QUERY,
                        new Object[]{familyFieldNameDataBase, Arrays.toString(fieldsToGet), procInstanceName,  MESSAGE_LABELS_VALUE_CONVERTED+valueToConvert+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(currentUnit)+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
            return conversion;            
        }                        
        if (!currentUnitInfo[0][currentUnitFamilyFieldPosic].toString().equalsIgnoreCase(newUnitInfo[0][currentUnitFamilyFieldPosic].toString())){
            conversion = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_FAMILY_FIELD_NOT_IN_QUERY,
                        new Object[]{currentUnit , currentUnitInfo[0][currentUnitFamilyFieldPosic].toString(), 
                            newUnit, newUnitInfo[0][currentUnitFamilyFieldPosic].toString(), 
                            procInstanceName,  MESSAGE_LABELS_VALUE_CONVERTED+valueToConvert+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(currentUnit)+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
            return conversion; 
        }
        BigDecimal currentUnitFactor = new BigDecimal(currentUnitInfo[0][3].toString());
        BigDecimal newUnitFactor = new BigDecimal(newUnitInfo[0][3].toString());
        BigDecimal currentUnitOffset = new BigDecimal(currentUnitInfo[0][4].toString());
        BigDecimal newUnitOffset = new BigDecimal(newUnitInfo[0][4].toString());

        newUnitFactor=newUnitFactor.divide(currentUnitFactor);
        valueConverted=valueConverted.multiply(newUnitFactor);
        newUnitOffset=newUnitOffset.add(currentUnitOffset.negate());
        valueConverted=valueConverted.add(newUnitOffset);
        
        conversion = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, MESSAGE_TRAPPING_CONVERTED_SUCCESS,
                        new Object[]{currentUnit , newUnitInfo, valueToConvert, valueConverted, procInstanceName,
                             MESSAGE_LABELS_VALUE_CONVERTED+valueToConvert+", "+MESSAGE_LABELS_CURRENT_UNIT+LPNulls.replaceNull(currentUnit)+", "+MESSAGE_LABELS_NEW_UNIT+LPNulls.replaceNull(newUnit)});
        conversion = LPArray.addValueToArray1D(conversion, valueConverted);
        conversion = LPArray.addValueToArray1D(conversion, newUnit);
        return conversion;
    }
/**
 * Get all units of measurement from the same given family from one specific schema/procedure and getting as many fields
 * as the ones specified in fieldsToRetrieve.
 * @param procInstanceName - String - ProcedureName  
 * @param family - String - The given family for the uom to be got.
 * @param fieldsToRetrieve String[] - Fields from the table that should be added to the array returned.
 * @return Object[][] - a dynamic table containing the fields specified in fieldsToRetrieve, position[0][3] set to FALSE when cannot buuilt.
 */
    public Object[][] getAllUnitsPerFamily( String procInstanceName, String family, String[] fieldsToRetrieve ){
       
        String tableName = TblsCnfg.UnitsOfMeasurement.TBL.getName();        
        String schemaName = GlobalVariables.Schemas.CONFIG.getName();
        schemaName = LPPlatform.buildSchemaName(procInstanceName, schemaName);
        if (family==null){
            Object[] conversion = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_FAMILY_FIELD_NOT_IN_QUERY,
                                    new Object[]{procInstanceName});
            return LPArray.array1dTo2d(conversion, conversion.length);
        }
      
        Object[][] unitsList = Rdbms.getRecordFieldsByFilter(schemaName, tableName, 
                 new String[]{TblsCnfg.UnitsOfMeasurement.FLD_MEASUREMENT_FAMILY.getName()},  new Object[]{family}, fieldsToRetrieve, 
                 new String[]{TblsCnfg.UnitsOfMeasurement.FLD_FACTOR_VALUE.getName(), TblsCnfg.UnitsOfMeasurement.FLD_OFFSET_VALUE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(unitsList[0][0].toString())) return unitsList;            
        
        return unitsList;        
    }

    /**
     *
     * @param procInstanceName
     * @param family
     * @return
     */
    public String getFamilyBaseUnitName( String procInstanceName, String family){
        String tableName = TblsCnfg.UnitsOfMeasurement.TBL.getName();                
        String schemaName = GlobalVariables.Schemas.CONFIG.getName();
        schemaName = LPPlatform.buildSchemaName(procInstanceName, schemaName);
       
        Object[][] unitsList = Rdbms.getRecordFieldsByFilter(schemaName, tableName, 
                 new String[]{TblsCnfg.UnitsOfMeasurement.FLD_MEASUREMENT_FAMILY.getName(), TblsCnfg.UnitsOfMeasurement.FLD_IS_BASE.getName()},  new Object[]{family, true}, new String[]{TblsCnfg.UnitsOfMeasurement.FLD_NAME.getName()});        
        return unitsList[0][0].toString();            

    }
    
}
