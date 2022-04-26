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
    EnumIntTableFields getTableField();
    String getFieldMask();  
    String getFieldComment(); 
    FldBusinessRules[] getFldBusinessRules();

    public static String[] getAllFieldNames(EnumIntViewFields[] tblFlds){
    //    if (1==1) return new String[]{};
        String[] flds=new String[]{};
        for (EnumIntViewFields curFld: tblFlds){
            flds=LPArray.addValueToArray1D(flds, curFld.getName());
        }
        return flds;
    }
    public static EnumIntViewFields[] getViewFieldsFromString(EnumIntViews tblObj, Object flds){
        if (flds==null || flds.toString().length()==0) return tblObj.getViewFields();
        if ("ALL".equalsIgnoreCase(flds.toString())) return tblObj.getViewFields();
        return getViewFieldsFromString(tblObj, flds.toString().split("\\|"));
    }
    public static EnumIntViewFields[] getViewFieldsFromString(EnumIntViews tblObj, String[] flds){
        if (flds==null || flds.length==0) return tblObj.getViewFields();
        flds=LPArray.getUniquesArray(flds);
        EnumIntViewFields[] custFlds=new EnumIntViewFields[flds.length];
        EnumIntViewFields[] ViewFields = tblObj.getViewFields();
        for (EnumIntViewFields curFld: ViewFields){
            Integer valuePosicInArray = LPArray.valuePosicInArray(flds, curFld.getName());
            if (valuePosicInArray>-1)
                custFlds[valuePosicInArray]=curFld;
        }
        return custFlds;
    }
    //No funciona para vistas, falta revisarlo.
    public static Integer getFldPosicInArray(EnumIntViewFields[] vwFlds, String fldName){
        for (int i=0;i<vwFlds.length;i++){
            if (vwFlds[i].getName().equalsIgnoreCase(fldName)) return i;
        }
        return -1;
    }
    
}
