/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

/**
 *
 * @author User
 */
public class LPAPIArgumentsSpecialChecks {

    public enum specialCheckersList {
        NONEGATIVEVALUE    }

    public static String checkerController(LPAPIArguments arg, Object value) {
        if (arg.getSpecialCheck() == null) {
            return null;
        }
        switch (arg.getSpecialCheck()) {
            case NONEGATIVEVALUE:
                return notNegativeValue(arg, value);
            default:
                return "Argument special checker " + arg.getSpecialCheck() + " not one of the recognized.";
        }
    }

    public static String notNegativeValue(LPAPIArguments arg, Object value) {
        if (arg.getSpecialCheck() == null) {
            return null;
        }
        Object[] numeric = LPMath.isNumeric(LPNulls.replaceNull(value).toString());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(numeric[0].toString())) {
            return numeric[numeric.length - 1].toString();
        }
        if (Integer.valueOf(value.toString())<0) return "value "+value.toString()+" is negative and it is not allowed";
        return null;
    }
}
