/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author User
 */
public class ProcedureSampleStage {  
    public String sampleStageSamplingNextChecker(Integer sampleId, String sampleData) {   
        JsonObject sampleStructure = LPJson.convertToJsonObjectStringedObject(sampleData);
        String samplingDate=sampleStructure.get("sampling_date").getAsString();
        if (samplingDate==null){
            return " Fecha de muestreo es obligatoria para la muestra "+sampleId;}
        return LPPlatform.LAB_TRUE;
    }  

    public String sampleStageIncubationPreviousChecker(Integer sampleId, String sampleData) {   
        JsonObject sampleStructure = LPJson.convertToJsonObjectStringedObject(sampleData);
        Boolean incubationPassed=sampleStructure.get("incubation_passed").getAsBoolean();
        Boolean incubation2Passed=sampleStructure.get("incubation2_passed").getAsBoolean();
        if (!incubationPassed){
            return " Pendiente 1a Incubacion para la muestra "+sampleId;}
        if (!incubation2Passed){
            return " Pendiente 2a Incubacion para la muestra "+sampleId;}
        return LPPlatform.LAB_TRUE;
    }  

    public String sampleStageIncubationNextChecker(Integer sampleId, String sampleData) {   
        JsonObject sampleStructure = LPJson.convertToJsonObjectStringedObject(sampleData);
        Boolean incubationPassed=sampleStructure.get("incubation_passed").getAsBoolean();
        Boolean incubation2Passed=sampleStructure.get("incubation2_passed").getAsBoolean();
        if (!incubationPassed){
            return " Pendiente 1a Incubacion para la muestra "+sampleId;}
        if (!incubation2Passed){
            return " Pendiente 2a Incubacion para la muestra "+sampleId;}
        return LPPlatform.LAB_TRUE;
    }  
    public String sampleStagePlateReadingPreviousChecker(Integer sampleId, String sampleData) {   
        return LPPlatform.LAB_TRUE;
    }
    public String sampleStagePlateReadingNextChecker(Integer sampleId, String sampleData) {   
        JsonObject sampleStructure = LPJson.convertToJsonObjectStringedObject(sampleData);
        JsonArray smpAna=sampleStructure.getAsJsonArray("sample_analysis");
        JsonElement jGet = smpAna.get(0);        
        JsonObject asJsonObject = jGet.getAsJsonObject();
        JsonArray asJsonArray = asJsonObject.getAsJsonArray("sample_analysis_result"); //
        jGet = asJsonArray.get(0);        
        asJsonObject = jGet.getAsJsonObject();
        String rawValue=asJsonObject.get("raw_value").getAsString();
        String paramName=asJsonObject.get("param_name").getAsString();
        if ("Recuento".equals(paramName)){ 
            if ("0".equals(rawValue)) return LPPlatform.LAB_TRUE+"|END";
            else return LPPlatform.LAB_TRUE;
        }
        return LPPlatform.LAB_FALSE;
    }
    public String sampleStageMicroorganismIdentificationPreviousChecker(Integer sampleId, String sampleData) {   
        return LPPlatform.LAB_TRUE;
    }
    public String sampleStageMicroorganismIdentificationNextChecker(Integer sampleId, String sampleData) {   
        return LPPlatform.LAB_FALSE;
    }    
    public String sampleStageENDPreviousChecker(Integer sampleId, String sampleData) {   
        return LPPlatform.LAB_TRUE;
    }    
}

