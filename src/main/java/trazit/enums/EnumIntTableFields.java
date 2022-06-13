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

    public static String[] getAllFieldNames(EnumIntTables tblObj){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=instanceForActions.getProcedureInstance();        
        HashMap<String[], Object[][]> dbTableGetFieldDefinition = Rdbms.dbTableGetFieldDefinition(LPPlatform.buildSchemaName(procInstanceName, tblObj.getRepositoryName()), tblObj.getTableName());
        String[] fldDefinitionColName= dbTableGetFieldDefinition.keySet().iterator().next();    
        Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
        String[] tableFldsInfoColumns = LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name")));
        return tableFldsInfoColumns;
    }    
    
    public static EnumIntTableFields[] getAllFieldNamesFromDatabase(EnumIntTables tblObj){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=instanceForActions.getProcedureInstance();        
        HashMap<String[], Object[][]> dbTableGetFieldDefinition = Rdbms.dbTableGetFieldDefinition(LPPlatform.buildSchemaName(procInstanceName, tblObj.getRepositoryName()), tblObj.getTableName());
        String[] fldDefinitionColName= dbTableGetFieldDefinition.keySet().iterator().next();    
        Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
        String[] tableFldsInfoColumns = LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name")));        

        tableFldsInfoColumns=LPArray.getUniquesArray(tableFldsInfoColumns);
        
        EnumIntTableFields[] custFlds=new EnumIntTableFields[tableFldsInfoColumns.length];
        EnumIntTableFields[] tableFields = tblObj.getTableFields();
        for (int i=0;i<tableFldsInfoColumns.length;i++){
            String curFld=tableFldsInfoColumns[i];
            Integer valuePosicInArray = getFldPosicInArray(tableFields, curFld);
            if (valuePosicInArray>-1)
                custFlds[i]=tableFields[valuePosicInArray]; 
            else{
                custFlds[i]=new AdhocTableFields(curFld); 
            }
        }
        return custFlds;
    }    
    
    public static EnumIntTableFields[] getTableFieldsFromString(EnumIntTables tblObj, Object flds){
        if (flds==null || flds.toString().length()==0) return getAllFieldNamesFromDatabase(tblObj);
        if ("ALL".equalsIgnoreCase(flds.toString())) return getAllFieldNamesFromDatabase(tblObj);        
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

