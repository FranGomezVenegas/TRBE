/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.enums;

import lbplanet.utilities.LPArray;

/**
 *
 * @author User
 */
public interface EnumIntViewFields {
    String getName();   
    String getViewAliasName();  
    String getFieldMask();  
    String getFieldComment(); 
    FldBusinessRules[] getFldBusinessRules();

    public static String[] getAllFieldNames(EnumIntViewFields[] tblFlds){
        String[] flds=new String[]{};
        for (EnumIntViewFields curFld: tblFlds){
            flds=LPArray.addValueToArray1D(flds, curFld.getName());
        }
        return flds;
    }

}
