/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.enums;

import databases.Rdbms;
import java.util.HashMap;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;

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
        return getViewFieldsFromString(tblObj, flds, null);
    }
    public static EnumIntViewFields[] getViewFieldsFromString(EnumIntViews tblObj, Object flds, String alternativeProcInstanceName){
        if (flds==null || flds.toString().length()==0) return getAllFieldNamesFromDatabase(tblObj, alternativeProcInstanceName);
        if ("ALL".equalsIgnoreCase(flds.toString())) return getAllFieldNamesFromDatabase(tblObj, alternativeProcInstanceName);        
        return getViewFieldsFromString(tblObj, flds.toString().split("\\|"));
    }
    public static EnumIntViewFields[] getViewFieldsFromString(EnumIntViews tblObj, String[] flds){
        if (flds==null || flds.length==0) return tblObj.getViewFields();
        flds=LPArray.getUniquesArray(flds);
        EnumIntViewFields[] custFlds=new EnumIntViewFields[flds.length];
        EnumIntViewFields[] viewFields = tblObj.getViewFields();
        int iFld=0;
        for (String curFld: flds){
            Integer valuePosicInArray = getFldPosicInArray(viewFields, curFld);
            if (valuePosicInArray>-1)
                custFlds[iFld]=viewFields[valuePosicInArray];
            iFld++;
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
    
    public static EnumIntViewFields[] getAllFieldNamesFromDatabase(EnumIntViews tblObj, String alternativeProcInstanceName){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName="";
        if (alternativeProcInstanceName!=null)
            procInstanceName=alternativeProcInstanceName;
        else
            procInstanceName=instanceForActions.getProcedureInstance();        
        HashMap<String[], Object[][]> dbTableGetFieldDefinition = Rdbms.dbTableGetFieldDefinition(LPPlatform.buildSchemaName(procInstanceName, tblObj.getRepositoryName()), tblObj.getViewName());
        String[] fldDefinitionColName= dbTableGetFieldDefinition.keySet().iterator().next();    
        Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
        String[] tableFldsInfoColumns = LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name")));        

        tableFldsInfoColumns=LPArray.getUniquesArray(tableFldsInfoColumns);
        
        EnumIntViewFields[] custFlds=new EnumIntViewFields[tableFldsInfoColumns.length];
        EnumIntViewFields[] tableFields = tblObj.getViewFields();
        for (int i=0;i<tableFldsInfoColumns.length;i++){
            String curFld=tableFldsInfoColumns[i];
            Integer valuePosicInArray = getFldPosicInArray(tableFields, curFld);
            if (valuePosicInArray>-1)
                custFlds[i]=tableFields[valuePosicInArray]; 
            else{
                custFlds[i]=new AdhocViewFields(curFld); 
            }
        }
        return custFlds;
    }    
    
    
}
