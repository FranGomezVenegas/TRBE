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
        return getViewFieldsFromStringArr(tblObj, flds.toString().split("\\|"));
    }
    public static EnumIntViewFields[] getViewFieldsFromStringArr(EnumIntViews tblObj, String[] flds){
        if (flds==null || flds.length==0) return tblObj.getViewFields();
/*        EnumIntTableFields[] custFlds=null;
        for (String curFld: flds.toString.split("\\|")){
            if ()
        }*/
        return tblObj.getViewFields();
    }
    //No funciona para vistas, falta revisarlo.
    public static Integer xgetFldPosicInArray(EnumIntViews[] tblFlds, String fldName){
        for (int i=0;i<tblFlds.length;i++){
            //if (tblFlds[i].getViewFields()..equalsIgnoreCase(fldName)) return i;
        }
        return -1;
    }
    
}
