/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.queries;

import databases.Rdbms;
import java.util.Map;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntTables;

/**
 *
 * @author User
 */
public class QueriesDataMining {
    
    public static JSONArray buildDynamicQuery(Map<String, String[]> params, String procInstanceName, Map<EnumIntTables, String> tbls, String[] fieldsToRetrieve){
        String query="";
        Object[] paramValues=null;
        Object[][] recordByDirectQuery = Rdbms.getRecordByDirectQuery(query, paramValues, fieldsToRetrieve);
        return QueryUtilities.convertArray2DtoJArr(recordByDirectQuery, fieldsToRetrieve, null, null);
    }
}
