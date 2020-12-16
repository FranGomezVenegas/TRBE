/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleinspectionlot;

import databases.Token;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgram;
import java.lang.reflect.InvocationTargetException;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author User
 */
public class DataInspectionLot {
    
    public Object[] createLot( String schemaPrefix, Token token, String template, Integer templateVersion, String[] fieldName, Object[] fieldValue) throws IllegalAccessException, InvocationTargetException{
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "not implemented yet", null);
    }
    
}
