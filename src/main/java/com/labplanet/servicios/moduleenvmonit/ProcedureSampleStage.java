/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import static lbplanet.utilities.LPJson.convertToJsonObjectStringedObject;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author User
 */
public class ProcedureSampleStage {  
    public String sampleStageSamplingNextChecker(String procInstanceName, Integer sampleId, String sampleData) {   
        Object[] objToJsonObj = convertToJsonObjectStringedObject(sampleData);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objToJsonObj[0].toString()))
           return LPPlatform.LAB_FALSE;
        JsonObject sampleStructure=(JsonObject) objToJsonObj[1];
        if (sampleStructure.get("sampling_date").isJsonNull())
            return LPPlatform.LAB_FALSE+" Fecha de muestreo es obligatoria para la muestra "+sampleId;
        String samplingDate=sampleStructure.get("sampling_date").toString();
        if (samplingDate==null || "null".equalsIgnoreCase(samplingDate)) {
            return LPPlatform.LAB_FALSE+" Fecha de muestreo es obligatoria para la muestra "+sampleId;}
        return LPPlatform.LAB_TRUE;
    }  

    public String sampleStageIncubationPreviousChecker(String procInstanceName, Integer sampleId, String sampleData) {   
        Object[] objToJsonObj = convertToJsonObjectStringedObject(sampleData);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objToJsonObj[0].toString()))
           return LPPlatform.LAB_FALSE;
        JsonObject sampleStructure=(JsonObject) objToJsonObj[1];
        Boolean incubationPassed=sampleStructure.get("incubation_passed").getAsBoolean();
        Boolean incubation2Passed=sampleStructure.get("incubation2_passed").getAsBoolean();
        if (!incubationPassed){
            return " Pendiente 1a Incubacion para la muestra "+sampleId;}
        if (!incubation2Passed){
            return " Pendiente 2a Incubacion para la muestra "+sampleId;}
        return LPPlatform.LAB_TRUE;
    }  

    public String sampleStageIncubationNextChecker(String procInstanceName, Integer sampleId, String sampleData) {   
        Object[] objToJsonObj = convertToJsonObjectStringedObject(sampleData);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objToJsonObj[0].toString()))
           return LPPlatform.LAB_FALSE;
        JsonObject sampleStructure=(JsonObject) objToJsonObj[1];
        Boolean incubationPassed=sampleStructure.get("incubation_passed").getAsBoolean();
        Boolean incubation2Passed=sampleStructure.get("incubation2_passed").getAsBoolean();
        if (!incubationPassed){
            return " Pendiente 1a Incubacion para la muestra "+sampleId;}
        if (!incubation2Passed){
            return " Pendiente 2a Incubacion para la muestra "+sampleId;}
        return LPPlatform.LAB_TRUE;
    }  
    public String sampleStagePlateReadingPreviousChecker(String procInstanceName, Integer sampleId, String sampleData) {   
        return LPPlatform.LAB_TRUE;
    }
    public String sampleStagePlateReadingNextChecker(String procInstanceName, Integer sampleId, String sampleData) { 
        try{
            Object[] objToJsonObj = convertToJsonObjectStringedObject(sampleData);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objToJsonObj[0].toString()))
               return LPPlatform.LAB_FALSE+"Info not parse-able";
            JsonObject sampleStructure=(JsonObject) objToJsonObj[1];
            if (sampleStructure.get("sample_analysis").isJsonNull())
                return LPPlatform.LAB_FALSE+"sample_analysis value not found";
            JsonArray smpAna=sampleStructure.getAsJsonArray("sample_analysis");
            JsonElement jGet = smpAna.get(0);        
            JsonObject asJsonObject = jGet.getAsJsonObject();
            if (asJsonObject.getAsJsonArray("sample_analysis_result").isJsonNull())
                return LPPlatform.LAB_FALSE+"sample_analysis_result value not found";
            JsonArray asJsonArray = asJsonObject.getAsJsonArray("sample_analysis_result"); //
            jGet = asJsonArray.get(0);        
            asJsonObject = jGet.getAsJsonObject();

            String rawValue="";
            if (asJsonObject.get("raw_value").isJsonNull())
                return LPPlatform.LAB_FALSE+"raw value not entered yet";
            else
                rawValue=asJsonObject.get("raw_value").getAsString();

            String paramName="";
            if (asJsonObject.get("param_name").isJsonNull())
                return LPPlatform.LAB_FALSE+"Parameter name is empty";
            else
                paramName=asJsonObject.get("param_name").getAsString();
            
            //String paramName=asJsonObject.get("param_name").getAsString();
            if ("Recuento".equals(paramName)){ 
                if ("0".equals(rawValue)) return LPPlatform.LAB_TRUE+"|END";
                else return LPPlatform.LAB_TRUE;
            }        
            return LPPlatform.LAB_FALSE+"You win! This logic is not handled";
        }catch(Exception e){
            return LPPlatform.LAB_FALSE+e.getMessage();
        }
    }
    public String sampleStageMicroorganismIdentificationPreviousChecker(String procInstanceName, Integer sampleId, String sampleData) {   
        return LPPlatform.LAB_TRUE;
    }
    public String sampleStageMicroorganismIdentificationNextChecker(String procInstanceName, Integer sampleId, String sampleData) {   
        return LPPlatform.LAB_TRUE+""; // No falla pero habría que ver si queremos añadir reglas de negocio para hacer que la identificación sea obligatorio o no
    }    
    public String sampleStageENDPreviousChecker(String procInstanceName, Integer sampleId, String sampleData) {   
        return LPPlatform.LAB_TRUE;
    }    
}

