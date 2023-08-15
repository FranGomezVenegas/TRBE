/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement;
import java.math.BigDecimal;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
import trazit.session.ApiMessageReturn;

/**
 * LPMath is a library for adding extra maths to the standard ones.
 *
 * @author Fran Gomez
 * @version 0.1
 */
public class LPMath {

    private LPMath() {
        throw new IllegalStateException("Utility class");
    }

    /**
     *
     * Calc the nth root. Common application on sampling plans.
     *
     * @param n int - The nth root
     * @param a double - for this value
     * @param p double
     * @return double. returns 0 if A = 0, returns -1 in case of error.
     */
    public static double nthroot(int n, double a, double p) {
        if (a < 0) {
            return -1;
        } else if (a == 0) {
            return 0;
        }
        double xPrev = a;
        double x = a / n;  // starting "guessed" value...
        while (Math.abs(x - xPrev) > p) {
            xPrev = x;
            x = ((n - 1.0) * x + a / Math.pow(x, n - 1.0)) / n;
        }
        return x;
    }

    /**
     *
     * @param procInstanceName
     * @param volume
     * @param volumeUOM
     * @param volumeObjectId
     * @param portion
     * @param portionUOM
     * @param portionObjectId
     * @return
     */
    public static Object[] extractPortion(String procInstanceName, BigDecimal volume, String volumeUOM, Integer volumeObjectId, BigDecimal portion, String portionUOM, Integer portionObjectId) {
        volumeUOM = volumeUOM == null ? "" : volumeUOM;
        Object[] errorDetailVariables = new Object[0];

        if (volume == null) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, "");
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, "");
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, procInstanceName);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.VOLUME_CANNOTBE_NULL, errorDetailVariables);
        }
        UnitsOfMeasurement uom = new UnitsOfMeasurement(portion, portionUOM);
        if (portionUOM == null ? volumeUOM != null : !portionUOM.equals(volumeUOM)) {
            uom.convertValue(volumeUOM);
            portion = uom.getConvertedQuantity();
        }

        if (portion.compareTo(BigDecimal.ZERO) < 1) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, portion.toString());
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, portionObjectId.toString());
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, procInstanceName);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.PORTION_NOT_ZERO_OR_NEGATIVE, errorDetailVariables);
        }
        volume = volume.add(portion.negate());
        if (volume.compareTo(BigDecimal.ZERO) < 0) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, "aliquot  " + volumeObjectId.toString());
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, volume.toString());
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, "subaliquoting");
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, portion.toString());
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, procInstanceName);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.VOLUME_NOT_ZERO_OR_NEGATIVE, errorDetailVariables);
        }
        String conclusionMsg = "It is possible to extract ";
        if (volumeUOM.equalsIgnoreCase(portionUOM)) {
            conclusionMsg = conclusionMsg + portion.toString() + " from " + volume.toString() + " expressed in " + volumeUOM;
        } else {
            conclusionMsg = conclusionMsg + portion.toString() + " of " + portionUOM + " from " + volume.toString() + " of " + volumeUOM;
        }
        return new Object[]{LPPlatform.LAB_TRUE, conclusionMsg, portion};
    }

    public static Object[] isNumeric(String strNum) {
        if (strNum == null || strNum.length() == 0) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.VALUE_EMPTY, null);
        }

        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            if (strNum.contains(",")) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.COMMA_IS_DECIMAL_SEPARATOR, null);
            }
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.VALUE_NOT_NUMERIC, null);
        }

        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "isNumeric", null);
    }

} // end class
