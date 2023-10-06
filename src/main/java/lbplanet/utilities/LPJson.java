/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.labplanet.servicios.app.GlobalAPIsParams;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LPJson {

    /**
     * classVersion {@value}
     */
    String classVersion = "0.1";

    /**
     *
     * @param header
     * @param row
     * @return
     */
    static String setAlias(String value) {
        if (Boolean.FALSE.equals(value.toUpperCase().contains(" AS"))) {
            return value;
        }
        return value.substring(value.toUpperCase().indexOf(" AS") + 4);
    }

    public static JSONObject convertArrayRowToJSONObject(String[] header, Object[] row) {
        return convertArrayRowToJSONObject(header, row, null);
    }

    public static JSONArray convertArrayRowToJSONFieldNameAndValueObject(String[] header, Object[] row, String[] fieldsToExclude) {
        JSONArray jArr = new JSONArray();
        if (header.length == 0) {
            return jArr;
        }
        for (int iField = 0; iField < header.length; iField++) {
            JSONObject jObj = new JSONObject();
            if (row[iField] == null) {
                jObj.put("field_name", header[iField]);
                jObj.put("field_value", "");
            } else {
                if (fieldsToExclude == null || !LPArray.valueInArray(fieldsToExclude, header[iField])) {
                    String clase = row[iField].getClass().toString();
                    if ((clase.toUpperCase().contains("DATE"))|| (clase.toUpperCase().contains("TIME"))){
                        jObj.put(setAlias(header[iField]), row[iField].toString());
                    } else {
                        if (row[iField].toString().toUpperCase().contains("NULL")) {
                            row[iField] = "null";
                        }
                        jObj.put("field_name", setAlias(header[iField]));
                        jObj.put("field_value", row[iField]);
                    }
                }
            }
            jArr.add(jObj);
        }
        return jArr;
    }

    public static JSONObject convertArrayRowToJSONObject(String[] header, Object[] row, String[] fieldsToExclude) {
        JSONObject jObj = new JSONObject();
        if (header.length == 0) {
            return jObj;
        }
        for (int iField = 0; iField < header.length; iField++) {
            if (row[iField] == null) {
                jObj.put(header[iField], "");
            }else if (row[iField].toString().toUpperCase().contains("NULL>>>")){
                jObj.put(setAlias(header[iField]), null);
            } else {
                if (fieldsToExclude == null || !LPArray.valueInArray(fieldsToExclude, header[iField])) {
                    String clase = row[iField].getClass().toString();
                    if ((clase.toUpperCase().contains("DATE"))|| (clase.toUpperCase().contains("TIME"))){                        
                        jObj.put(setAlias(header[iField]), row[iField].toString());
                    }else if ((clase.toUpperCase().contains("CLASS [B"))){    
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode jsonNode = objectMapper.convertValue(row[iField], JsonNode.class);
                            String jsonString = objectMapper.writeValueAsString(jsonNode);
                            jObj.put(setAlias(header[iField]), jsonString);
                        } catch (JsonProcessingException ex) {
                            Logger.getLogger(LPJson.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        if ("NULL".equalsIgnoreCase(row[iField].toString())) {
                            row[iField] = "null";
                        }
                        jObj.put(setAlias(header[iField]), row[iField]);
                    }
                }
            }
        }
        return jObj;
    }

    public static JSONArray convertToJSONArray(Object[] diagn) {
        JSONArray jMainArr = new JSONArray();
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

        for (int diagnItem = 0; diagnItem < diagn.length; diagnItem++) {
            jsonStr = jsonStr.append("diagn").append(diagnItem).append(":").append(diagn[diagnItem].toString());
        }
        jsonStr = jsonStr.append("}");
        return jsonStr.toString();
    }

    public static String convertToJSON(Object[] diagn, String labelText) {
        StringBuilder jsonStr = new StringBuilder(0).append("{");

        for (int diagnItem = 0; diagnItem < diagn.length; diagnItem++) {
            jsonStr = jsonStr.append(labelText).append(diagnItem).append(":").append(diagn[diagnItem].toString());
        }
        jsonStr = jsonStr.append("}");
        return jsonStr.toString();
    }

    /**
     *
     * @param normalArray
     * @return
     */
    public static JSONArray convertToJSON(String[] normalArray) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(Arrays.asList(normalArray));
        return jsonArray;
    }

    public static JSONArray convertArrayJsonToJSON(JsonArray jsonArr) {
        JSONArray jsonArray = new JSONArray();
        jsonArr.forEach(jsonElement -> {
            jsonArray.add(jsonElement);
        });
        return jsonArray;
    }
    public static JsonArray convertJsonArrayToJSONArray(JSONArray jsonArray) {
    JsonArray jsonArrayResult = new JsonArray();

    for (int i = 0; i < jsonArray.size(); i++) {
        try {
            // Convert each element to a JsonElement using Gson
            JsonElement jsonElement = JsonParser.parseString(jsonArray.get(i).toString());
            
            // Add the JsonElement to the result JsonArray
            jsonArrayResult.add(jsonElement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        return jsonArrayResult;
    }
    public static Object[] convertToJsonObjectStringedObject(String value) {
        return convertToJsonObjectStringedObject(value, false);
    }

    public static Object[] convertToJsonObjectStringedObject(String value, Boolean skipAsteriskSplit) {
        try {
            JsonObject asJsonObject = new JsonObject();
            if (Boolean.TRUE.equals(skipAsteriskSplit)) {
                asJsonObject = JsonParser.parseString(value).getAsJsonObject();
                return new Object[]{LPPlatform.LAB_TRUE, asJsonObject};
            }
            String[] valueArr = value.split("\\*");
            if (valueArr.length == 1) {
                asJsonObject = JsonParser.parseString(valueArr[0]).getAsJsonObject();
            } else // Solo cubre el escenario en el cual el json ref esta en la última posicion del valor.
            {
                asJsonObject = JsonParser.parseString(valueArr[valueArr.length - 1]).getAsJsonObject();
            }
            Object[] infoArr = new Object[]{LPPlatform.LAB_TRUE, asJsonObject};
            if (valueArr.length == 2) {
                infoArr = LPArray.addValueToArray1D(infoArr, valueArr[1]);
            }
            return infoArr;
        } catch (JsonSyntaxException e) {
            return new Object[]{LPPlatform.LAB_FALSE, e.getMessage()};
        }

    }

    public static JsonArray convertToJsonArrayStringedObject(String value) {
        try {
            if (LPNulls.replaceNull(value).length() == 0) {
                JsonArray jArr = new JsonArray();
                return jArr;
            }
            if ("TBD".equalsIgnoreCase(value)) {
                JsonArray jArr = new JsonArray();
                jArr.add(value);
                return jArr;
            }
            return JsonParser.parseString(value).getAsJsonArray();
        } catch (JsonSyntaxException e) {

            JsonArray jArr = new JsonArray();
            jArr.add(e.getMessage());
            jArr.add(value);
            return jArr;
        } catch (Exception ex) {
            JsonArray jArr = new JsonArray();
            jArr.add(ex.getMessage());
            jArr.add(value);
            return jArr;
        }
    }

    public static JsonObject convertToJsonObjectStringedValue(String value) {
        try {
            if ("TBD".equalsIgnoreCase(value)) {
                JsonObject jObj = new JsonObject();
                jObj.addProperty("TBD", value);
                return jObj;
            }
            return JsonParser.parseString(value).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            JsonObject jObj = new JsonObject();
            jObj.addProperty(GlobalAPIsParams.LBL_ERROR, e.getMessage());
            jObj.addProperty("value", value);
            return jObj;
        }
    }

    public static JsonObject sortJsonObjectContent(JsonObject objectToSort) {
        TreeMap<String, JsonElement> sortedProcEndPoints = new TreeMap<>();
        for (Map.Entry<String, JsonElement> entry : objectToSort.entrySet()) {
            sortedProcEndPoints.put(entry.getKey(), entry.getValue());
            objectToSort.remove(entry.getKey());
        }
        for (Map.Entry<String, JsonElement> entry : sortedProcEndPoints.entrySet()) {
            objectToSort.add(entry.getKey(), entry.getValue());
        }
        return objectToSort;
    }

    /*    
    public static JSONArray  sortJsonArrayContent(JSONArray objectToSort) {
List<JsonElement> list = objectToSort.toList();

// sort the list
Collections.sort(list, new Comparator<JsonElement>() {
    @Override
    public int compare(JsonElement o1, JsonElement o2) {
        // compare the values of the two JsonElements
        // return -1 if o1 is less than o2, 0 if they are equal, and 1 if o1 is greater than o2
        // for example, to sort by a string property:
        // return o1.getAsJsonObject().get("propertyName").getAsString().compareTo(o2.getAsJsonObject().get("propertyName").getAsString());
        return 0; // replace this line with your own comparison logic
    }
});

// convert the sorted list back to a JSONArray
JSONArray sortedJsonArray = new JSONArray();
for (JsonElement element : list) {
    sortedJsonArray.put(element);
}        
    }    
     */

    public static boolean ValueInJsonArray(JsonArray  jArr, String valueToFind) {
        JsonParser parser = new JsonParser();
        boolean containsDemo = false;
        for (JsonElement element : jArr) {
            if (parser.parse(element.toString()).getAsString().equals(valueToFind)) {
                containsDemo = true;
                break;
            }
        }
        return containsDemo;
    }
/*    
    public static Object[] filterJArrByProperty2(JSONArray arr, String filterPropName, String filterPropValue, String propToGet){
        Object[] newArr=new Object[]{};
        
        arr.forEach(curEntry -> {
            JSONObject entry = (JSONObject) curEntry;
            
            String stage = entry.get(filterPropName).toString();
            Object id = (Object) entry.get(propToGet);

            if (filterPropValue.equalsIgnoreCase(stage)) {
                newArr=LPArray.addValueToArray1D(newArr, id);
                //System.out.println("ID with stage X: " + id);
            }
        };  
        return newArr;
        );
    }
*/        
    public static Object[] filterJArrByProperty(JSONArray arr, String filterPropName, String filterPropValue, String propToGet) {
        Object[] newArr = new Object[]{}; // Initialize the array to the same length as the original array
        
        for (int i = 0; i < arr.size(); i++) {
            JSONObject entry = (JSONObject) arr.get(i);
            
            String stage = entry.get(filterPropName).toString();
            Object id = entry.get(propToGet);

            if (filterPropValue.equalsIgnoreCase(stage)) {
                newArr=LPArray.addValueToArray1D(newArr, id);
                //newArr[i] = id;
            }
        }
        
        return newArr;    
    }
}
