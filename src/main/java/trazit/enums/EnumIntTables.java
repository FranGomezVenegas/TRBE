/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.enums;

/**
 *
 * @author User
 */
public interface EnumIntTables {
    String getRepositoryName(); 
    Boolean getIsProcedureInstance();
    String getTableName();   
    EnumIntTableFields[] getTableFields();
    String getSeqName();   
    String[] getPrimaryKey();  
    Object[] getForeignKey();
    String getTableComment();
    FldBusinessRules[] getTblBusinessRules();
    
        public static Integer getTblPosicInArray(EnumIntTables[] tbls, String tableName) {
        for (int i = 0; i < tbls.length; i++) {
            if (tbls[i].getTableName().equalsIgnoreCase(tableName)) {
                return i;
            }
        }
        return -1;
    }

}
