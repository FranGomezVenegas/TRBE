package module.inventorytrack.logic;

import module.inventorytrack.definition.InvTrackingEnums;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement;
import java.math.BigDecimal;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.inventorytrack.definition.TblsInvTrackingConfig;
import module.inventorytrack.definition.TblsInvTrackingData;
import module.inventorytrack.definition.TblsInvTrackingData.TablesInvTrackingData;
import module.inventorytrack.definition.InvTrackingEnums.InventoryTrackingErrorTrapping;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.session.InternalMessage;
import trazit.session.ResponseMessages;
import static module.inventorytrack.logic.AppInventoryLotAudit.inventoryLotAuditAdd;

public class DataInventoryMovements {

    private DataInventoryMovements() {
        throw new IllegalStateException("Utility class");
    }

    public static InternalMessage adjustInventoryLotQuantity(DataInventory invLot, BigDecimal newVolume, String newVolumeUom) {
        InternalMessage availableForMovements = isAvailableForMovements(invLot, newVolume, newVolumeUom);
        if ((invLot.getCurrentVolume().equals(newVolume)) && (invLot.getCurrentVolumeUom().equals(newVolumeUom))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.QUANTITY_IS_ALREADY_THIS, new Object[]{newVolume, newVolumeUom, invLot.getLotName()}, null);
        }

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(availableForMovements.getDiagnostic())) {
            return availableForMovements;
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        UnitsOfMeasurement myUom = null;
        Object[] checkVolumeCoherencyDiagn = DataInventory.checkVolumeCoherency(invLot, TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields(), invLot.getReferenceFieldValues(), newVolume, newVolumeUom);
        InternalMessage checkVolumeCoherency = (InternalMessage) checkVolumeCoherencyDiagn[0];
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkVolumeCoherency.getDiagnostic())) {
            return checkVolumeCoherency;
        }
        if (checkVolumeCoherencyDiagn.length > 1) {
            myUom = (UnitsOfMeasurement) checkVolumeCoherencyDiagn[1];
            if (Boolean.FALSE.equals(myUom.getConvertedFine())) {
                return new InternalMessage(LPPlatform.LAB_FALSE, myUom.getConversionErrorDetail()[0].toString(), new Object[]{invLot.getLotName()}, null);
            }
        }
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{invLot.getLotName()}, "");
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.REFERENCE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{invLot.getReference()}, "");
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.CATEGORY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{invLot.getCategory()}, "");        
        Object[] fldValues = new Object[]{newVolume, newVolumeUom};
        RdbmsObject invLotTurnAvailableDiagn = Rdbms.updateTableRecordFieldsByFilter(TablesInvTrackingData.LOT,
                new EnumIntTableFields[]{TblsInvTrackingData.Lot.QUANTITY, TblsInvTrackingData.Lot.QUANTITY_UOM},
                fldValues, sqlWhere, null);
        String[] fldNames = new String[]{"new_" + TblsInvTrackingData.Lot.QUANTITY.getName(), "new_" + TblsInvTrackingData.Lot.QUANTITY_UOM.getName(),
            "previous_" + TblsInvTrackingData.Lot.QUANTITY.getName(), "previous_" + TblsInvTrackingData.Lot.QUANTITY_UOM.getName()};
        fldValues = LPArray.addValueToArray1D(fldValues,
                new Object[]{LPNulls.replaceNull(invLot.getLotFieldValues()[LPArray.valuePosicInArray(invLot.getLotFieldNames(), TblsInvTrackingData.Lot.QUANTITY.getName())]).toString(),
                    LPNulls.replaceNull(invLot.getLotFieldValues()[LPArray.valuePosicInArray(invLot.getLotFieldNames(), TblsInvTrackingData.Lot.QUANTITY_UOM.getName())]).toString()});
        if (Boolean.FALSE.equals(invLotTurnAvailableDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotTurnAvailableDiagn.getErrorMessageCode(), new Object[]{invLot.getLotName()}, null);
        }
        inventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.LOT_QUANTITY_ADJUSTED, invLot.getLotName(), invLot.getReference(), invLot.getCategory(), TablesInvTrackingData.LOT.getTableName(), invLot.getLotName(),
                fldNames, fldValues);

        if (checkVolumeCoherencyDiagn.length > 1) {
            myUom = (UnitsOfMeasurement) checkVolumeCoherencyDiagn[1];
            if (Boolean.FALSE.equals(myUom.getConvertedFine())) {
                return new InternalMessage(LPPlatform.LAB_FALSE, myUom.getConversionErrorDetail()[0].toString(), new Object[]{invLot.getLotName()}, null);
            }
        }
        if (myUom != null && myUom.getConvertedFine() && !myUom.getOrigQuantity().equals(myUom.getConvertedQuantity())) {
            SqlWhere whereObj = new SqlWhere(TablesInvTrackingData.LOT,
                    new String[]{TblsInvTrackingData.Lot.REFERENCE.getName(), TblsInvTrackingData.Lot.CATEGORY.getName(), TblsInvTrackingData.Lot.LOT_NAME.getName()},
                    new Object[]{invLot.getReference(), invLot.getCategory(), invLot.getLotName()});
            String[] updateFieldNames = new String[]{TblsInvTrackingData.Lot.QUANTITY.getName(), TblsInvTrackingData.Lot.QUANTITY_UOM.getName()};
            Object[] updateFieldValues = new Object[]{myUom.getConvertedQuantity(), myUom.getConvertedQuantityUom()};
            Rdbms.updateTableRecordFieldsByFilter(TablesInvTrackingData.LOT,
                    EnumIntTableFields.getTableFieldsFromString(TablesInvTrackingData.LOT, updateFieldNames), updateFieldValues, whereObj, null);
            updateFieldNames = new String[]{"converted_volume", "converted_volume_uom", "creation_volume", "creation_volume_uom"};
            updateFieldValues = LPArray.addValueToArray1D(updateFieldValues, new Object[]{myUom.getOrigQuantity(), myUom.getOrigQuantityUom()});
            inventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.LOT_QUANTITY_ADJUSTED, invLot.getLotName(), invLot.getReference(), invLot.getCategory(), TablesInvTrackingData.LOT.getTableName(), invLot.getLotName(),
                    updateFieldNames, updateFieldValues);
        }
        if (myUom != null) {
            myUom = null;
        }
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.ADJUST_INV_LOT_QUANTITY, new Object[]{newVolume, newVolumeUom, invLot.getLotName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.ADJUST_INV_LOT_QUANTITY, new Object[]{newVolume, newVolumeUom, invLot.getLotName()}, invLot.getLotName());
    }

    public static InternalMessage consumeInventoryLotQuantity(DataInventory invLot, BigDecimal newVolume, String newVolumeUom, String externalProcInstanceName) {
        Boolean requiredConversion = false;
        BigDecimal reducedVolume = null;
        UnitsOfMeasurement myUom = null;
        InternalMessage availableForMovements = isAvailableForMovements(invLot, newVolume, newVolumeUom);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(availableForMovements.getDiagnostic())) {
            return availableForMovements;
        }
        if (invLot.getCurrentVolume() == null) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.INV_LOT_HAS_NO_QUANTITY_SET, new Object[]{invLot.getLotName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();

        if (newVolumeUom == null || invLot.getCurrentVolumeUom().equalsIgnoreCase(newVolumeUom)) {
            reducedVolume = invLot.getCurrentVolume().subtract(newVolume);
        } else {
            requiredConversion = true;
            Object[] checkVolumeCoherencyDiagn = DataInventory.checkVolumeCoherency(invLot, TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields(), invLot.getReferenceFieldValues(), newVolume, newVolumeUom);
            InternalMessage checkVolumeCoherency = (InternalMessage) checkVolumeCoherencyDiagn[0];
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkVolumeCoherency.getDiagnostic())) {
                return checkVolumeCoherency;
            }
            if (checkVolumeCoherencyDiagn.length > 1) {
                myUom = (UnitsOfMeasurement) checkVolumeCoherencyDiagn[1];
                if (Boolean.FALSE.equals(myUom.getConvertedFine())) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, myUom.getConversionErrorDetail()[0].toString(), new Object[]{invLot.getLotName()}, null);
                }
            }
            if (myUom != null) {
                reducedVolume = invLot.getCurrentVolume().subtract(myUom.getConvertedQuantity());
            }
        }

        if (BigDecimal.ZERO.compareTo(reducedVolume) > 0) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InvTrackingEnums.InventoryTrackingErrorTrapping.INV_LOT_HAS_NOT_ENOUGH_QUANTITY, new Object[]{invLot.getLotName(), invLot.getCurrentVolume(), newVolume, newVolumeUom}, invLot.getLotName());
        }

        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{invLot.getLotName()}, "");
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.REFERENCE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{invLot.getReference()}, "");
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.CATEGORY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{invLot.getCategory()}, "");

        String[] fldNames = new String[]{TblsInvTrackingData.Lot.QUANTITY.getName()};
        Object[] fldValues = new Object[]{reducedVolume};
        RdbmsObject invLotTurnAvailableDiagn = Rdbms.updateTableRecordFieldsByFilter(TablesInvTrackingData.LOT,
                new EnumIntTableFields[]{TblsInvTrackingData.Lot.QUANTITY},
                fldValues, sqlWhere, externalProcInstanceName);
        fldNames = new String[]{"new_" + TblsInvTrackingData.Lot.QUANTITY.getName(), "new_" + TblsInvTrackingData.Lot.QUANTITY_UOM.getName(),
            "previous_" + TblsInvTrackingData.Lot.QUANTITY.getName(), "previous_" + TblsInvTrackingData.Lot.QUANTITY_UOM.getName()};
        fldValues = LPArray.addValueToArray1D(fldValues,
                new Object[]{newVolumeUom, LPNulls.replaceNull(invLot.getLotFieldValues()[LPArray.valuePosicInArray(invLot.getLotFieldNames(), TblsInvTrackingData.Lot.QUANTITY.getName())]).toString(),
                    LPNulls.replaceNull(invLot.getLotFieldValues()[LPArray.valuePosicInArray(invLot.getLotFieldNames(), TblsInvTrackingData.Lot.QUANTITY_UOM.getName())]).toString()});
        fldNames = LPArray.addValueToArray1D(fldNames, "operation_en");
        fldValues = LPArray.addValueToArray1D(fldValues, "The new quantity is " + reducedVolume + " as reducing " + newVolume + " " + newVolumeUom + " to the current volume " + invLot.getCurrentVolume() + " " + invLot.getCurrentVolumeUom());
        fldNames = LPArray.addValueToArray1D(fldNames, "operation_es");
        fldValues = LPArray.addValueToArray1D(fldValues, "La nueva cantidad es " + reducedVolume + " al reducir " + newVolume + " " + newVolumeUom + " del volume actual " + invLot.getCurrentVolume() + " " + invLot.getCurrentVolumeUom());
        if (Boolean.TRUE.equals(requiredConversion)) {
            fldNames = LPArray.addValueToArray1D(fldNames, "required_conversion");
            fldValues = LPArray.addValueToArray1D(fldValues, true);
            fldNames = LPArray.addValueToArray1D(fldNames, "conversion_en");
            if (myUom != null) {
                fldValues = LPArray.addValueToArray1D(fldValues, "The value entered by the user was " + newVolume + " " + newVolumeUom + " and this lot is expressed in " + invLot.getCurrentVolumeUom() + " therefore was converted to " + myUom.getConvertedQuantity() + " " + myUom.getConvertedQuantityUom());
            }
            fldNames = LPArray.addValueToArray1D(fldNames, "conversion_es");
            if (myUom != null) {
                fldValues = LPArray.addValueToArray1D(fldValues, "The valor entrado por el usuario fue " + newVolume + " " + newVolumeUom + " y este lot está expresado en " + invLot.getCurrentVolumeUom() + " por lo tanto se convirtió a " + myUom.getConvertedQuantity() + " " + myUom.getConvertedQuantityUom());
            }
        }
        if (Boolean.FALSE.equals(invLotTurnAvailableDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotTurnAvailableDiagn.getErrorMessageCode(), new Object[]{invLot.getLotName()}, null);
        }
        inventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.LOT_QUANTITY_CONSUMED, invLot.getLotName(), invLot.getReference(), invLot.getCategory(), TablesInvTrackingData.LOT.getTableName(), invLot.getLotName(),
                fldNames, fldValues, externalProcInstanceName);
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.CONSUME_INV_LOT_QUANTITY, new Object[]{newVolume, newVolumeUom, invLot.getLotName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.CONSUME_INV_LOT_QUANTITY, new Object[]{newVolume, newVolumeUom}, invLot.getLotName());
    }

    public static InternalMessage addInventoryLotQuantity(DataInventory invLot, BigDecimal newVolume, String newVolumeUom) {
        Boolean requiredConversion = false;
        BigDecimal increasedVolume = null;
        UnitsOfMeasurement myUom = null;
        InternalMessage availableForMovements = isAvailableForMovements(invLot, newVolume, newVolumeUom);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(availableForMovements.getDiagnostic())) {
            return availableForMovements;
        }
        if (invLot.getCurrentVolume() == null) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.INV_LOT_HAS_NO_QUANTITY_SET, new Object[]{invLot.getLotName()}, null);
        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();

        if (invLot.getCurrentVolumeUom().equalsIgnoreCase(newVolumeUom)) {
            increasedVolume = invLot.getCurrentVolume().add(newVolume);
        } else {
            requiredConversion = true;
            Object[] checkVolumeCoherencyDiagn = DataInventory.checkVolumeCoherency(invLot, TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields(), invLot.getReferenceFieldValues(), newVolume, newVolumeUom);
            InternalMessage checkVolumeCoherency = (InternalMessage) checkVolumeCoherencyDiagn[0];
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkVolumeCoherency.getDiagnostic())) {
                return checkVolumeCoherency;
            }
            if (checkVolumeCoherencyDiagn.length > 1) {
                myUom = (UnitsOfMeasurement) checkVolumeCoherencyDiagn[1];
                if (Boolean.FALSE.equals(myUom.getConvertedFine())) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, myUom.getConversionErrorDetail()[0].toString(), new Object[]{invLot.getLotName()}, null);
                }
            }
            if (myUom != null) {
                increasedVolume = invLot.getCurrentVolume().add(myUom.getConvertedQuantity());
            }
        }
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{invLot.getLotName()}, "");
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.REFERENCE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{invLot.getReference()}, "");
        sqlWhere.addConstraint(TblsInvTrackingData.Lot.CATEGORY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{invLot.getCategory()}, "");

        String[] fldNames = new String[]{TblsInvTrackingData.Lot.QUANTITY.getName()};
        Object[] fldValues = new Object[]{increasedVolume};
        RdbmsObject invLotTurnAvailableDiagn = Rdbms.updateTableRecordFieldsByFilter(TablesInvTrackingData.LOT,
                new EnumIntTableFields[]{TblsInvTrackingData.Lot.QUANTITY},
                fldValues, sqlWhere, null);
        fldNames = new String[]{"new_" + TblsInvTrackingData.Lot.QUANTITY.getName(), "new_" + TblsInvTrackingData.Lot.QUANTITY_UOM.getName(),
            "previous_" + TblsInvTrackingData.Lot.QUANTITY.getName(), "previous_" + TblsInvTrackingData.Lot.QUANTITY_UOM.getName()};
        fldValues = LPArray.addValueToArray1D(fldValues,
                new Object[]{newVolumeUom, LPNulls.replaceNull(invLot.getLotFieldValues()[LPArray.valuePosicInArray(invLot.getLotFieldNames(), TblsInvTrackingData.Lot.QUANTITY.getName())]).toString(),
                    LPNulls.replaceNull(invLot.getLotFieldValues()[LPArray.valuePosicInArray(invLot.getLotFieldNames(), TblsInvTrackingData.Lot.QUANTITY_UOM.getName())]).toString()});
        fldNames = LPArray.addValueToArray1D(fldNames, "operation_en");
        fldValues = LPArray.addValueToArray1D(fldValues, "The new quantity is " + increasedVolume + " as adding " + newVolume + " " + newVolumeUom + " to the current volume " + invLot.getCurrentVolume() + " " + invLot.getCurrentVolumeUom());
        fldNames = LPArray.addValueToArray1D(fldNames, "operation_es");
        fldValues = LPArray.addValueToArray1D(fldValues, "La nueva cantidad es " + increasedVolume + " al añadir " + newVolume + " " + newVolumeUom + " del volume actual " + invLot.getCurrentVolume() + " " + invLot.getCurrentVolumeUom());
        if (Boolean.TRUE.equals(requiredConversion)) {
            fldNames = LPArray.addValueToArray1D(fldNames, "required_conversion");
            fldValues = LPArray.addValueToArray1D(fldValues, true);
            fldNames = LPArray.addValueToArray1D(fldNames, "conversion_en");
            if (myUom != null) {
                fldValues = LPArray.addValueToArray1D(fldValues, "The value entered by the user was " + newVolume + " " + newVolumeUom + " and this lot is expressed in " + invLot.getCurrentVolumeUom() + " therefore was converted to " + myUom.getConvertedQuantity() + " " + myUom.getConvertedQuantityUom());
            }
            fldNames = LPArray.addValueToArray1D(fldNames, "conversion_es");
            if (myUom != null) {
                fldValues = LPArray.addValueToArray1D(fldValues, "The valor entrado por el usuario fue " + newVolume + " " + newVolumeUom + " y este lot está expresado en " + invLot.getCurrentVolumeUom() + " por lo tanto se convirtió a " + myUom.getConvertedQuantity() + " " + myUom.getConvertedQuantityUom());
            }
        }
        if (Boolean.FALSE.equals(invLotTurnAvailableDiagn.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, invLotTurnAvailableDiagn.getErrorMessageCode(), new Object[]{invLot.getLotName()}, null);
        }
        inventoryLotAuditAdd(InvTrackingEnums.AppInventoryTrackingAuditEvents.LOT_QUANTITY_ADDITION, invLot.getLotName(), invLot.getReference(), invLot.getCategory(), TablesInvTrackingData.LOT.getTableName(), invLot.getLotName(),
                fldNames, fldValues);
        messages.addMainForSuccess(InvTrackingEnums.InventoryTrackAPIactionsEndpoints.ADD_INV_LOT_QUANTITY, new Object[]{newVolume, newVolumeUom, invLot.getLotName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.ADD_INV_LOT_QUANTITY, new Object[]{newVolume, newVolumeUom, invLot.getLotName()}, invLot.getLotName());
    }

    private static InternalMessage isAvailableForMovements(DataInventory invLot, BigDecimal newVolume, String newVolumeUom) {

        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (Boolean.FALSE.equals(invLot.getAvailableForUse())) {
            messages.addMainForError(InventoryTrackingErrorTrapping.NOT_AVAILABLE, new Object[]{invLot.getLotName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.NOT_AVAILABLE, new Object[]{invLot.getLotName()}, null);
        }
        if (invLot.getCurrentVolume() == null) {
            messages.addMainForError(InventoryTrackingErrorTrapping.INV_LOT_HAS_NO_QUANTITY_SET, new Object[]{invLot.getLotName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.INV_LOT_HAS_NO_QUANTITY_SET, new Object[]{invLot.getLotName()}, null);
        }
        if (invLot.getIsRetired() != null && invLot.getIsRetired()) {
            messages.addMainForError(InventoryTrackingErrorTrapping.ALREADY_RETIRED, new Object[]{invLot.getLotName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.ALREADY_RETIRED, new Object[]{invLot.getLotName()}, null);
        }
        if (invLot.getIsLocked() != null && invLot.getIsLocked()) {
            messages.addMainForError(InventoryTrackingErrorTrapping.IS_LOCKED, new Object[]{invLot.getLotName()});
            return new InternalMessage(LPPlatform.LAB_FALSE, InventoryTrackingErrorTrapping.IS_LOCKED, new Object[]{invLot.getLotName()}, null);
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, InvTrackingEnums.InventoryTrackAPIactionsEndpoints.ADD_INV_LOT_QUANTITY, new Object[]{invLot.getLotName(), newVolume, newVolumeUom}, invLot.getLotName());
    }
}
