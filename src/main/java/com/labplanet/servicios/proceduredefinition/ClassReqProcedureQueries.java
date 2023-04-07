/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.proceduredefinition;

import com.google.gson.JsonParser;
import databases.Rdbms;
import databases.SqlStatement;
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
public class ClassReqProcedureQueries {    
    
    static JSONObject procAccessBlockInRequirements(String procInstanceName){
        String[] fldsArr=new String[]{TblsReqs.ProcedureUsers.USER_NAME.getName()};
        Object[][] procUsers = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_USERS.getTableName(), 
            new String[]{TblsReqs.ProcedureUsers.PROC_INSTANCE_NAME.getName()}, 
            new Object[]{procInstanceName}, fldsArr);
        JSONObject jBlockObj = new JSONObject();
        JSONArray jBlockArr = new JSONArray(); 
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procUsers[0][0].toString())){
            for (Object[] curRow: procUsers){
                jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsArr, curRow));
            }
            jBlockObj.put("users", jBlockArr);
        }
        fldsArr=new String[]{TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()};
        Object[][] procUserRoles = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_USER_ROLES.getTableName(), 
            new String[]{TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName()}, 
            new Object[]{procInstanceName}, fldsArr);
        jBlockArr = new JSONArray(); 
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserRoles[0][0].toString())){
            for (Object[] curRow: procUserRoles){
                jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsArr, curRow));
            }
            jBlockObj.put("user_role", jBlockArr);
        }
        fldsArr=new String[]{TblsReqs.ProcedureRoles.ROLE_NAME.getName()};
        String[] roleActionsFldsArr=new String[]{TblsReqs.ProcedureUserRequirements.NAME.getName()};
    
        Object[][] procRoles = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_ROLES.getTableName(), 
            new String[]{TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName()}, 
            new Object[]{procInstanceName}, fldsArr);
        jBlockArr = new JSONArray(); 
        JSONObject jRolesActions=new JSONObject();
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procRoles[0][0].toString())){
            for (Object[] curRow: procRoles){
                jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsArr, curRow));
                JSONArray jRoleActionsjArr = new JSONArray(); 
                Object[][] roleActions = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableName(), 
                    new String[]{TblsReqs.ProcedureUserRequirements.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirements.ROLES.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.LIKE.getSqlClause()}, 
                    new Object[]{procInstanceName, "%"+curRow[0]+"%"}, roleActionsFldsArr);
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(roleActions[0][0].toString())){
                    for (Object[] curRolAction: roleActions){
                        jRoleActionsjArr.add(curRolAction[0]);
                    }
                }
                jRolesActions.put(curRow[0], jRoleActionsjArr);
            }
            jBlockObj.put("roles", jBlockArr);
            jBlockObj.put("roles_actions", jRolesActions);
        }
        return jBlockObj;
    }
    static JSONArray feProcModel(String procInstanceName){
        JSONArray jArr = new JSONArray();   
        Object[][] ruleValue = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_FE_MODEL.getTableName(), 
                new String[]{TblsReqs.ProcedureFEModel.PROC_INSTANCE_NAME.getName()},
                new Object[]{procInstanceName}, 
                new String[]{TblsReqs.ProcedureFEModel.MODEL_JSON.getName(), TblsReqs.ProcedureFEModel.MODEL_JSON_MOBILE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ruleValue[0][0].toString())){
            JSONObject jObj= new JSONObject();
            jObj.put("No Data", "No Data");
            jArr.add(jObj);
        }else{
            JSONObject jObj= new JSONObject();
            jObj.put("laptop_mode", JsonParser.parseString(ruleValue[0][0].toString()).getAsJsonObject());
            jArr.add(jObj);
            jObj= new JSONObject();
            if (ruleValue[0][1]==null || ruleValue[0][1].toString().length()==0)
                jObj.put("mobile_mode", "no mobile version");
            else
                jObj.put("mobile_mode", JsonParser.parseString(ruleValue[0][1].toString()).getAsJsonObject());
            jArr.add(jObj);
            
        }  
        return jArr;        
    }
    static JSONObject dbSingleRowToJsonObj(String procInstanceName, String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue){
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), tblName, 
            whereFldName, whereFldValue, fldsToGet);        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procTblRows[0][0].toString())){
            JSONObject jObj= new JSONObject();
            jObj.put("No Data", "No Data");
            return jObj;
        }else{       
            JSONObject jObj=new JSONObject();
            jObj=LPJson.convertArrayRowToJSONObject(fldsToGet, procTblRows[0]);                    
            return jObj;
        }
    }
    static JSONArray dbSingleRowToJsonFldNameAndValueArr(String procInstanceName, String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue){
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), tblName, 
            whereFldName, whereFldValue, fldsToGet);        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procTblRows[0][0].toString())){
            JSONObject jObj= new JSONObject();
            jObj.put("No Data", "No Data");
            JSONArray jArr= new JSONArray();
            jArr.add(jObj);
            return jArr;
        }else{       
            JSONArray jArr= new JSONArray();
            jArr=LPJson.convertArrayRowToJSONFieldNameAndValueObject(fldsToGet, procTblRows[0], null);                    
            return jArr;
        }
    }
    
    
    static JSONArray dbRowsToJsonArr(String procInstanceName, String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue, String[] sortFlds, String[] jsonFlds){
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter(procInstanceName, tblName, 
            whereFldName, whereFldValue, fldsToGet, sortFlds);
        JSONArray jBlockArr = new JSONArray(); 
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procTblRows[0][0].toString())){
            JSONObject jObj= new JSONObject();
            jObj.put("No Data", "No Data");
            jBlockArr.add(jObj);
        }else{ 
            try{
            for (Object[] curRow: procTblRows){
                if (jsonFlds==null)
                    jBlockArr.add(LPJson.convertArrayRowToJSONObject(fldsToGet, curRow));
                else{
                    JSONObject jObj = new JSONObject();
                    jObj=(LPJson.convertArrayRowToJSONObject(fldsToGet, curRow, jsonFlds));                    
                    for (String curJsonFld: jsonFlds)
                        jObj.put(TblsReqs.ProcedureMasterData.JSON_OBJ.getName(), JsonParser.parseString(curRow[LPArray.valuePosicInArray(fldsToGet, curJsonFld)].toString()).getAsJsonObject());
                    jBlockArr.add(jObj);
                }
            }
            }catch(Exception e){                
                jBlockArr.add("Errors trying to get the master data records info. "+e.getMessage());
                return jBlockArr;        
            }
        }        
        return jBlockArr;        
    }

    static JSONObject dbRowsGroupedToJsonArr(String tblName, String[] fldsToGet, String[] whereFldName, Object[] whereFldValue, String[] sortFlds){
        Object[][] procTblRows = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), tblName, 
            whereFldName, whereFldValue, fldsToGet, sortFlds);
        JSONObject jBlockObj = new JSONObject();        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procTblRows[0][0].toString())){
            jBlockObj.put("No Data", "No Data");
            return jBlockObj;
        }else{
            String curSchema="";
            JSONArray jSchemaArr=new JSONArray();
            for (Object[] curRow: procTblRows){
                if (!curSchema.equalsIgnoreCase(LPNulls.replaceNull(curRow[0]).toString())){
                    if (!jSchemaArr.isEmpty()){
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
            }
            if (!jSchemaArr.isEmpty()){            
                if (curSchema.length()==0) curSchema="-";                
                jBlockObj.put(curSchema, jSchemaArr);
            }

        }
        return jBlockObj;
    }
    
}
