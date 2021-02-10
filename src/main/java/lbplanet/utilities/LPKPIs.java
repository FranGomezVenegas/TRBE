/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import static lbplanet.utilities.LPFrontEnd.noRecordsInTableMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class LPKPIs {

public static JSONObject getKPIs(String[] objGroupName, String[] tblCategory, String[] tblName, String[] whereFieldsNameArr, String[] whereFieldsValueArr, 
                    String[] fldToRetrieve, String[] dataGrouped){
    ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        
    JSONObject jObjMainObject=new JSONObject();
    if (objGroupName.length!=fldToRetrieve.length && objGroupName.length!=whereFieldsNameArr.length 
            && objGroupName.length!=whereFieldsValueArr.length){
        jObjMainObject.put("is_error", true);
        jObjMainObject.put("error", "KPI Definition is wrong");
    }
    for (int i=0;i<objGroupName.length;i++){
        String curgrouperName=giveMeString(objGroupName[i]); 
        String curtblCategory=giveMeString(tblCategory[i]);
        String curtblName=giveMeString(tblName[i]);
        String[] curWhereFieldsNameArr=giveMeStringArr(whereFieldsNameArr[i]);
        Object[] curWhereFieldsValueArr=giveMeObjectArr(whereFieldsValueArr[i]);                        
        String[] curFldsToRetrieveArr=giveMeStringArr(fldToRetrieve[i]);
        String curdataGrouped=giveMeString(dataGrouped[i]);

        if (curgrouperName.length()==0)curgrouperName="grouper_"+i;
        Object[][] dataInfo = new Object[][]{{}};
        if (Boolean.valueOf(curdataGrouped)){
            dataInfo = Rdbms.getGrouper(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), curtblCategory), curtblName, 
                curFldsToRetrieveArr, curWhereFieldsNameArr, curWhereFieldsValueArr, 
                null);
            curFldsToRetrieveArr=LPArray.addValueToArray1D(curFldsToRetrieveArr, "count");
        }else{
            dataInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), curtblCategory), curtblName, 
                curWhereFieldsNameArr, curWhereFieldsValueArr, curFldsToRetrieveArr);
        }
        JSONObject jObj = new JSONObject();
        JSONArray dataJSONArr = new JSONArray();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dataInfo[0][0].toString())){
            jObj= noRecordsInTableMessage();
        }else{
            for (Object[] curRec: dataInfo){
                jObj= LPJson.convertArrayRowToJSONObject(curFldsToRetrieveArr, curRec);
                dataJSONArr.add(jObj);
            }
        } 
        jObjMainObject.put(curgrouperName, dataJSONArr);
    }     
    return jObjMainObject;
}
    private static String giveMeString(String value){
        if (value==null || GlobalAPIsParams.REQUEST_PARAM_IGNORE_ARGUMENT_WORD.equalsIgnoreCase(value))
            return "";
        return value;
    }
    private static String[] giveMeStringArr(String value){
        if (value==null || GlobalAPIsParams.REQUEST_PARAM_IGNORE_ARGUMENT_WORD.equalsIgnoreCase(value))
            return new String[]{};
        return value.split("\\|");
    }    

    private static Object[] giveMeObjectArr(String value){
        if (value==null || GlobalAPIsParams.REQUEST_PARAM_IGNORE_ARGUMENT_WORD.equalsIgnoreCase(value))
            return new Object[]{};
        return LPArray.convertStringWithDataTypeToObjectArray(value.split("\\|"));
    }    
}
