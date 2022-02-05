/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.proceduredefinition;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import databases.Rdbms;
import databases.TblsReqs;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class ClassProcedureQueries {    
    
    static JSONObject procAccessBlock(String procInstanceName){
        String[] fldsArr=new String[]{TblsReqs.ProcedureUsers.FLD_USER_NAME.getName()};
        Object[][] procUsers = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureUsers.TBL.getName(), 
            new String[]{TblsReqs.ProcedureUsers.FLD_PROCEDURE_NAME.getName()}, 
            new Object[]{procInstanceName}, fldsArr);
        JSONObject jBlockObj = new JSONObject();
        JSONArray jBlockArr = new JSONArray(); 
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procUsers[0][0].toString())){
            for (Object[] curRow: procUsers){
                jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsArr, curRow));
            }
            jBlockObj.put("users", jBlockArr);
        }
        fldsArr=new String[]{TblsReqs.ProcedureUserRole.FLD_USER_NAME.getName(), TblsReqs.ProcedureUserRole.FLD_ROLE_NAME.getName()};
        Object[][] procUserRoles = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureUserRole.TBL.getName(), 
            new String[]{TblsReqs.ProcedureUserRole.FLD_PROCEDURE_NAME.getName()}, 
            new Object[]{procInstanceName}, fldsArr);
        jBlockArr = new JSONArray(); 
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserRoles[0][0].toString())){
            for (Object[] curRow: procUserRoles){
                jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsArr, curRow));
            }
            jBlockObj.put("user_role", jBlockArr);
        }
        fldsArr=new String[]{TblsReqs.ProcedureRoles.FLD_ROLE_NAME.getName()};
        Object[][] procRoles = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureRoles.TBL.getName(), 
            new String[]{TblsReqs.ProcedureRoles.FLD_PROCEDURE_NAME.getName()}, 
            new Object[]{procInstanceName}, fldsArr);
        jBlockArr = new JSONArray(); 
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procRoles[0][0].toString())){
            for (Object[] curRow: procRoles){
                jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsArr, curRow));
            }
            jBlockObj.put("roles", jBlockArr);
        }
        return jBlockObj;
    }
    static JsonObject feProcModel(String procInstanceName){
        JsonObject jArr = new JsonObject();   
        Object[][] ruleValue = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureFrontEndProcModel.TBL.getName(), 
                new String[]{TblsReqs.ProcedureFrontEndProcModel.FLD_PROCEDURE_NAME.getName()},
                new Object[]{procInstanceName}, 
                new String[]{TblsReqs.ProcedureFrontEndProcModel.FLD_MODEL_JSON.getName()});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(ruleValue[0][0].toString())){
            JsonParser parser = new JsonParser();
            return parser.parse(ruleValue[0][0].toString()).getAsJsonObject();
        }  
        return jArr;        
    }
    static JSONObject dbSingleRowToJsonObj(String procInstanceName, String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue){
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), tblName, 
            whereFldName, whereFldValue, fldsToGet);
        String blockLabel="procedure_info";
        JSONObject jBlockObj=new JSONObject();
        JSONObject jObj=new JSONObject();
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procTblRows[0][0].toString()))
            jObj=LPJson.convertArrayRowToJSONObject(fldsToGet, procTblRows[0]);                    
        return jObj;
    }

    static JSONArray dbRowsToJsonArr(String procInstanceName, String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue, String[] sortFlds, String[] jsonFlds){
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), tblName, 
            whereFldName, whereFldValue, fldsToGet, sortFlds);
        JSONArray jBlockArr = new JSONArray(); 
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procTblRows[0][0].toString())){
            for (Object[] curRow: procTblRows){
                if (jsonFlds==null)
                    jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsToGet, curRow));
                else{
                    JSONObject jObj = new JSONObject();
                    jObj=(LPJson.convertArrayRowToJSONObject(fldsToGet, curRow, jsonFlds));
                    JsonParser parser = new JsonParser();
                    for (String curJsonFld: jsonFlds)
                        jObj.put(TblsReqs.ProcedureMasterData.FLD_JSON_OBJ.getName(), parser.parse(curRow[LPArray.valuePosicInArray(fldsToGet, curJsonFld)].toString()).getAsJsonObject());
                    jBlockArr.add(jObj);
                }
            }
        }        
        return jBlockArr;        
    }

    static JSONObject dbRowsGroupedToJsonArr(String procInstanceName, String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue, String[] sortFlds){
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), tblName, 
            whereFldName, whereFldValue, fldsToGet, sortFlds);
        JSONArray jBlockArr = new JSONArray(); 
        JSONObject jBlockObj = new JSONObject();        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procTblRows[0][0].toString())){
            String curSchema="";
            JSONObject jSchemaObj=new JSONObject();
            JSONArray jSchemaArr=new JSONArray();
            for (Object[] curRow: procTblRows){
                if (!curSchema.equalsIgnoreCase(LPNulls.replaceNull(curRow[0]).toString())){
                    if (jSchemaArr.size()>0){
                        if (curSchema.length()==0) curSchema="-";
                        jBlockObj.put(curSchema, jSchemaArr);
                    }
                    jSchemaArr=new JSONArray();
                    if (fldsToGet.length==2)
                        jSchemaArr.add(LPNulls.replaceNull(curRow[1]).toString());
                    else{
                        JSONObject jObj = new JSONObject();
                        for (int i=1;i<fldsToGet.length;i++){
                            jObj.put(fldsToGet[i], curRow[i]);
                        }
                        jSchemaArr.add(jObj);
                    }
                    curSchema=curRow[0].toString();
                }else{
                    if (fldsToGet.length==2)
                        jSchemaArr.add(LPNulls.replaceNull(curRow[1]).toString());
                    else{
                        JSONObject jObj = new JSONObject();
                        for (int i=1;i<fldsToGet.length;i++){
                            jObj.put(fldsToGet[i], curRow[i]);
                        }
                        jSchemaArr.add(jObj);
                    }
                }
                //jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsArr, curRow));
            }
            if (jSchemaArr.size()>0){
                if (curSchema.length()==0) curSchema="-";                
                jBlockObj.put(curSchema, jSchemaArr);
            }

        }
        return jBlockObj;
    }
    
}
