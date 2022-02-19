/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.datatransfer;

import databases.Rdbms;
import databases.SqlStatement;
import java.util.HashMap;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;

/**
 *
 * @author User
 */
public class FromInstanceToInstance {
    
    public static Object[] tableContent(String schemaName, String tableName, String sourceDB, String destinationDB){
        Rdbms.closeRdbms();
        Rdbms.stablishDBConection(sourceDB);
        HashMap<String[], Object[][]> dbTableGetFieldDefinition = Rdbms.dbTableGetFieldDefinition(schemaName, tableName);
        String[] fldDefinitionColName= dbTableGetFieldDefinition.keySet().iterator().next();    
        Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
        String[] tableFldsInfoColumns = LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name")));
        
        Object[][] recordsInSourceDB = Rdbms.getRecordFieldsByFilter(schemaName, tableName, 
                new String[]{tableFldsInfoColumns[0]+" "+SqlStatement.WHERECLAUSE_TYPES.NOT_IN.getSqlClause()}, 
                new Object[]{"<<<>>>"}, tableFldsInfoColumns);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(recordsInSourceDB[0][0].toString())) 
            return recordsInSourceDB[0];
        Rdbms.closeRdbms();
        Rdbms.stablishDBConection(destinationDB);
        int numRecsTransferred=0;
        for (Object[] curRow: recordsInSourceDB){            
            Object[] insertRecordInTable = Rdbms.insertRecordInTable(schemaName, tableName, tableFldsInfoColumns, curRow);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTable[0].toString())) 
                break;
            numRecsTransferred++;
        }
        String diagn=LPPlatform.LAB_FALSE;
        if (numRecsTransferred==recordsInSourceDB.length) diagn=LPPlatform.LAB_TRUE;
        return ApiMessageReturn.trapMessage(diagn, "Moved "+numRecsTransferred+" records of "+recordsInSourceDB.length, null, GlobalVariables.Languages.EN.getName());
    }
    
}
