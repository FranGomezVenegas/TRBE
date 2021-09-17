/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsData;
import javax.sql.rowset.CachedRowSet;
import static lbplanet.utilities.LPFrontEnd.noRecordsInTableMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public final class LPKPIs {
    private LPKPIs() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}

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
    public static JSONObject getRecoveryRate(String[] fldToRetrieve, String[] whereFieldsNameArr, String[] whereFieldsValueArr,
        Boolean showAbsence, Boolean showPresence, Boolean showIN, Boolean showOUT, Integer numDecPlaces){
        String subQryAlias="vw";
        if (numDecPlaces==null) numDecPlaces=2;
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        JSONObject jObjMainObject=new JSONObject();
        String grouperFldsStr=LPArray.convertArrayToString(fldToRetrieve, ",", "");
        grouperFldsStr=grouperFldsStr.substring(0, grouperFldsStr.length()-1);
        String alias="row_total";
        String qry="select "+grouperFldsStr+", "+alias;
        String subQry="select "+grouperFldsStr;
        
        Object[] wherefldsValues=new Object[]{};
        subQry=subQry+", COUNT(sample_id) FILTER (WHERE raw_value_num is not null) as "+alias;
        fldToRetrieve=LPArray.addValueToArray1D(fldToRetrieve, alias);
        if (showAbsence){
            alias="absence";
            qry=qry+","+alias+", round(100*("+subQryAlias+"."+alias+"::numeric/row_total::numeric),"+numDecPlaces.toString()+") as "+alias+"_perc";
            subQry=subQry+", COUNT(sample_id) FILTER (WHERE raw_value_num =0) as "+alias;
            fldToRetrieve=LPArray.addValueToArray1D(fldToRetrieve, alias);
            fldToRetrieve=LPArray.addValueToArray1D(fldToRetrieve, alias+"_perc");
        }
        if (showPresence){
            alias="presence";
            qry=qry+","+alias+", round(100*("+subQryAlias+"."+alias+"::numeric/row_total::numeric),"+numDecPlaces.toString()+") as "+alias+"_perc";
            subQry=subQry+", COUNT(sample_id) FILTER (WHERE raw_value_num >0) as "+alias;
            fldToRetrieve=LPArray.addValueToArray1D(fldToRetrieve, alias);
            fldToRetrieve=LPArray.addValueToArray1D(fldToRetrieve, alias+"_perc");
        }
        if (showIN){
            alias="in_range";
            qry=qry+","+alias+", round(100*("+subQryAlias+"."+alias+"::numeric/row_total::numeric),"+numDecPlaces.toString()+") as "+alias+"_perc";
            subQry=subQry+", COUNT(sample_id) FILTER (WHERE upper(spec_eval) not like 'OUT_SPEC%' and upper(spec_eval) not Like 'OUTOFSPEC%') as "+alias;
            fldToRetrieve=LPArray.addValueToArray1D(fldToRetrieve, alias);
            fldToRetrieve=LPArray.addValueToArray1D(fldToRetrieve, alias+"_perc");
        }
        if (showOUT){
            alias="out_range";
            qry=qry+","+alias+", round(100*("+subQryAlias+"."+alias+"::numeric/row_total::numeric),"+numDecPlaces.toString()+") as "+alias+"_perc";
            subQry=subQry+", COUNT(sample_id) FILTER (WHERE upper(spec_eval) like 'OUT_SPEC%' OR upper(spec_eval) like 'OUTOFSPEC%') as "+alias;
            fldToRetrieve=LPArray.addValueToArray1D(fldToRetrieve, alias);
            fldToRetrieve=LPArray.addValueToArray1D(fldToRetrieve, alias+"_perc");
        }
        subQry=subQry+" from "+LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())+"."+TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName();
        subQry=subQry+" where raw_value_num is not null"; 
        if (whereFieldsNameArr!=null && whereFieldsNameArr[0].length()>0){
            Object[] buildWhereClause = SqlStatement.buildWhereClause(whereFieldsNameArr, LPArray.convertStringWithDataTypeToObjectArray(whereFieldsValueArr));            
            subQry=subQry+" and "+buildWhereClause[0];
            wherefldsValues=(Object[]) buildWhereClause[1];
        }
        subQry=subQry+" group by "+grouperFldsStr;
        qry=qry+" from("+subQry+") "+subQryAlias; 
        CachedRowSet prepRdQuery = Rdbms.prepRdQuery(qry, wherefldsValues);
        Object[][] dataInfo = Rdbms.resultSetToArray(prepRdQuery, fldToRetrieve);            
    
        JSONObject jObj = new JSONObject();
        JSONArray dataJSONArr = new JSONArray();
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dataInfo[0][0].toString())){
        jObj= noRecordsInTableMessage();
    }else{
        for (Object[] curRec: dataInfo){
            jObj= LPJson.convertArrayRowToJSONObject(fldToRetrieve, curRec);
            dataJSONArr.add(jObj);
        }
        jObjMainObject.put("data", dataJSONArr);
        dataJSONArr=new JSONArray();
        for (Object curFld: fldToRetrieve){
            jObj= new JSONObject();
            jObj.put("name", curFld);
            dataJSONArr.add(jObj);
        }
    } 
    //jObjMainObject.put("data", dataJSONArr);
    jObjMainObject.put("columns_data", dataJSONArr);
    
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
