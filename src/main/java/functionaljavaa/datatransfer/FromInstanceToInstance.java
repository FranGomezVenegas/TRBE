/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.datatransfer;

import databases.Rdbms;
import databases.Rdbms.RdbmsSuccess;
import databases.RdbmsObject;
import databases.SqlStatement;
import java.util.HashMap;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTables;
import trazit.session.ApiMessageReturn;

/**
 *
 * @author User
 */
public class FromInstanceToInstance {
    
    public static Object[] tableContent(EnumIntTables tblObj, String sourceDB, String destinationDB){
        Rdbms.closeRdbms();
        Rdbms.stablishDBConection(sourceDB);
        HashMap<String[], Object[][]> dbTableGetFieldDefinition = Rdbms.dbTableGetFieldDefinition(tblObj.getRepositoryName(), tblObj.getTableName());
        String[] fldDefinitionColName= dbTableGetFieldDefinition.keySet().iterator().next();    
        Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
        String[] tableFldsInfoColumns = LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name")));
        
        Object[][] recordsInSourceDB = Rdbms.getRecordFieldsByFilter(tblObj.getRepositoryName(), tblObj.getTableName(),
                new String[]{tableFldsInfoColumns[0]+" "+SqlStatement.WHERECLAUSE_TYPES.NOT_IN.getSqlClause()}, 
                new Object[]{"<<<>>>"}, tableFldsInfoColumns);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(recordsInSourceDB[0][0].toString())) 
            return recordsInSourceDB[0];
        Rdbms.closeRdbms();
        Rdbms.stablishDBConection(destinationDB);
        int numRecsTransferred=0;
        for (Object[] curRow: recordsInSourceDB){            
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(tblObj, tableFldsInfoColumns, curRow);
            if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) 
                break;
            numRecsTransferred++;
        }
        String diagn=LPPlatform.LAB_FALSE;
        if (numRecsTransferred==recordsInSourceDB.length) diagn=LPPlatform.LAB_TRUE;
        return ApiMessageReturn.trapMessage(diagn, RdbmsSuccess.TRANSFERRED_RECORDS_BETWEEN_INSTANCES, new Object[]{numRecsTransferred, recordsInSourceDB.length});
    }
    
}
