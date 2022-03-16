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
public interface EnumIntTableFields {
    String getName();   
    String getFieldType();   
    String getFieldMask();  
    ReferenceFld getReferenceTable();     
    String getFieldComment(); 
    FldBusinessRules[] getFldBusinessRules();

    public static String[] getAllFieldNames(EnumIntTableFields[] tblFlds){
        String[] flds=new String[]{};
        for (EnumIntTableFields curFld: tblFlds){
            flds=LPArray.addValueToArray1D(flds, curFld.getName());
        }
        return flds;
    }
    
    public static EnumIntTableFields[] getTableFieldsFromString(EnumIntTables tblObj, Object flds){
        if (flds==null || flds.toString().length()==0) return tblObj.getTableFields();
        if ("ALL".equalsIgnoreCase(flds.toString())) return tblObj.getTableFields();        
        return getTableFieldsFromString(tblObj, flds.toString().split("\\|"));
    }
    public static EnumIntTableFields[] getTableFieldsFromString(EnumIntTables tblObj, String[] flds){
        if (flds==null || flds.length==0) return tblObj.getTableFields();
        flds=LPArray.getUniquesArray(flds);
        EnumIntTableFields[] custFlds=new EnumIntTableFields[flds.length];
        EnumIntTableFields[] tableFields = tblObj.getTableFields();
        for (EnumIntTableFields curFld: tableFields){
            Integer valuePosicInArray = LPArray.valuePosicInArray(flds, curFld.getName());
            if (valuePosicInArray>-1)
                custFlds[valuePosicInArray]=curFld;
        }
        return custFlds;
    }
    public static Integer getFldPosicInArray(EnumIntTableFields[] tblFlds, String fldName){
        for (int i=0;i<tblFlds.length;i++){
            if (tblFlds[i].getName().equalsIgnoreCase(fldName)) return i;
        }
        return -1;
    }

}

