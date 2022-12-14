/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 * LabPLANETJSon is a library of utilities for working with json objects
 * @author Fran Gomez
 */  

public class LPJson {
 /**
 * classVersion
 * {@value}
 */   
    String classVersion = "0.1";

    /**
     *
     * @param header
     * @param row
     * @return
     */
     static String setAlias(String value){
        if (!value.toUpperCase().contains(" AS")) return value;
        return 
            value.substring(value.toUpperCase().indexOf(" AS")+4);
    }
    public static JSONObject convertArrayRowToJSONObject(String[] header, Object[] row){
        return convertArrayRowToJSONObject(header, row, null);
    }

    public static JSONObject convertArrayRowToJSONObject(String[] header, Object[] row, String[] fieldsToExclude){
        JSONObject jObj = new JSONObject();    
        if (header.length==0){return jObj;}
        for (int iField=0; iField<header.length; iField++){ 
            if (row[iField]==null){
                jObj.put(header[iField], "");
            }else{
                if (fieldsToExclude==null || !LPArray.valueInArray(fieldsToExclude, header[iField])){
                    String clase = row[iField].getClass().toString();
                    if ( (clase.toUpperCase().equalsIgnoreCase("class java.sql.Date")) 
                        || (clase.toUpperCase().equalsIgnoreCase("class java.time.LocalDateTime"))
                        || (clase.toUpperCase().equalsIgnoreCase("class java.time.LocalDate"))
                        || (clase.toUpperCase().equalsIgnoreCase("class java.sql.Timestamp")) ){
                        jObj.put(setAlias(header[iField]), row[iField].toString());
                    }else{
                        if (row[iField].toString().toUpperCase().contains("NULL"))
                            row[iField]="null";
                        jObj.put(setAlias(header[iField]), row[iField]);
                    }
                }
            }
        }                    
        return jObj;
    }

    public static JSONArray convertToJSONArray(Object[] diagn) {
        JSONArray jMainArr=new JSONArray();
        jMainArr.addAll(Arrays.asList(diagn));
        return jMainArr;
    }

    /**
     *
     * @param diagn
     * @return
     */
    public static String convertToJSON(Object[] diagn) {
        StringBuilder jsonStr = new StringBuilder(0).append("{");
        
        for(int diagnItem = 0; diagnItem<diagn.length;diagnItem++){            
            jsonStr=jsonStr.append("diagn").append(diagnItem).append(":").append(diagn[diagnItem].toString());
        }
        jsonStr=jsonStr.append("}");
        return jsonStr.toString();
    }

    public static String convertToJSON(Object[] diagn, String labelText) {
        StringBuilder jsonStr = new StringBuilder(0).append("{");
        
        for(int diagnItem = 0; diagnItem<diagn.length;diagnItem++){            
            jsonStr=jsonStr.append(labelText).append(diagnItem).append(":").append(diagn[diagnItem].toString());
        }
        jsonStr=jsonStr.append("}");
        return jsonStr.toString();
    }
    
    /**
     *
     * @param normalArray
     * @return
     */
    public static JSONArray convertToJSON(String[] normalArray) {
        JSONArray jsonArray= new JSONArray();
        jsonArray.addAll(Arrays.asList(normalArray));
        return jsonArray;
    }

    public static Object[] convertToJsonObjectStringedObject(String value){
        return convertToJsonObjectStringedObject(value, false);
    }
    public static Object[] convertToJsonObjectStringedObject(String value, Boolean skipAsteriskSplit){
        try{
        JsonParser parser = new JsonParser();
        JsonObject asJsonObject=new JsonObject();
        if (Boolean.TRUE.equals(skipAsteriskSplit)){
            asJsonObject = parser.parse(value).getAsJsonObject();
            Object[] infoArr=new Object[]{LPPlatform.LAB_TRUE, asJsonObject};
            return infoArr;
        }
        String[] valueArr=value.split("\\*");
        if(valueArr.length==1)
            asJsonObject = parser.parse(valueArr[0]).getAsJsonObject();
        else
            // Solo cubre el escenario en el cual el json ref esta en la ??ltima posicion del valor.
            asJsonObject = parser.parse(valueArr[valueArr.length-1]).getAsJsonObject();                    
        Object[] infoArr=new Object[]{LPPlatform.LAB_TRUE, asJsonObject};
        if (valueArr.length==2) infoArr=LPArray.addValueToArray1D(infoArr, valueArr[1]);        
        return infoArr;
        }catch(JsonSyntaxException e){
           return new Object[]{LPPlatform.LAB_FALSE, e.getMessage()}; 
        }
        
    }
    
    public static JsonArray convertToJsonArrayStringedObject(String value){
        try{
            if ("TBD".equalsIgnoreCase(value)){
               JsonArray jArr = new JsonArray();
               jArr.add(value);
               return jArr;
            }
            JsonParser parser = new JsonParser();
            return parser.parse(value).getAsJsonArray();
        }catch(JsonSyntaxException e){
           
           JsonArray jArr = new JsonArray();
           jArr.add(e.getMessage());
           jArr.add(value);
           return jArr; 
        } catch (Exception ex){
           JsonArray jArr = new JsonArray();
           jArr.add(ex.getMessage());
           jArr.add(value);
           return jArr;             
        }
    }

    public static JsonObject convertToJsonObjectStringedValue(String value){
        try{
            if ("TBD".equalsIgnoreCase(value)){
               JsonObject jObj = new JsonObject();
               jObj.addProperty("TBD", value);
               return jObj;
            }
            JsonParser parser = new JsonParser();
            return parser.parse(value).getAsJsonObject();
        }catch(JsonSyntaxException e){
           JsonObject jObj = new JsonObject();
           jObj.addProperty("error", e.getMessage());
           jObj.addProperty("value", value);
           return jObj; 
        }
    }
   


    
}
