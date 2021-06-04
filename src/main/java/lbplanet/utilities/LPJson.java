/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
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
        JSONObject jObj = new JSONObject();    
        if (header.length==0){return jObj;}
        for (int iField=0; iField<header.length; iField++){     
            if (row[iField]==null){
                jObj.put(header[iField], "");
            }else{
                String clase = row[iField].getClass().toString();
                if ( (clase.toUpperCase().equalsIgnoreCase("class java.sql.Date")) || (clase.toUpperCase().equalsIgnoreCase("class java.sql.Timestamp")) ){
                    jObj.put(setAlias(header[iField]), row[iField].toString());
                }else{
                    jObj.put(setAlias(header[iField]), row[iField]);
                }
            }
        }                    
        return jObj;
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
        try{
        JsonParser parser = new JsonParser();
        return new Object[]{LPPlatform.LAB_TRUE, parser.parse(value).getAsJsonObject()};
        }catch(Exception e){
           return new Object[]{LPPlatform.LAB_FALSE, e.getMessage()}; 
        }
        
    }
    
    public static JsonArray convertToJsonArrayStringedObject(String value){
        JsonParser parser = new JsonParser();
        return parser.parse(value).getAsJsonArray();
    }

   


    
}
