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
import trazit.enums.EnumIntMessages;

/**
 *
 * @author User
 */
public class ProcedureSampleStage {  
    
   public enum ProcedureSampleStageErrorTrapping implements EnumIntMessages{ 
        NOTPARSEABLE("EnvMonSampleStageChecker_NotParseable", "", ""),
        SAMPLEANALYSIS_NOTFOUND("EnvMonSampleStageChecker_SampleAnalysisNotFound", "sample_analysis value not found", ""),
        SAMPLEANALYSISRESULT_NOTFOUND("EnvMonSampleStageChecker_SampleAnalysisResultNotFound", "sample_analysis_result value not found", ""),
        SAMPLING_PREV_FIRSTSTAGE("EnvMonSampleStageChecker_SamplingPrevious_InFirstStage", "Sampling is the first stage, has no previous.", ""),
        SAMPLINGDATE_MANDATORY("EnvMonSampleStageChecker_SamplingNext_stagesCheckerSamplingDateIsMandatory", "", ""),
        SAMPLINGDATEEND_MANDATORY("EnvMonSampleStageChecker_SamplingNext_stagesCheckerSamplingDateEndIsMandatory", "", ""),
        FIRSTINCUB_MANDATORY("EnvMonSampleStageChecker_IncubationNext_stagesCheckerPendingFirstIncubation", "", ""),
        SECONDINCUB_MANDATORY("EnvMonSampleStageChecker_IncubationNext_stagesCheckerPendingSecondIncubation", "", ""),
        INCUB_INPROGRESS("EnvMonSampleStageChecker_IncubationNext_stagesCheckerIncubationInProgress", "", ""),
        ALREADY_LASTSTAGE("EnvMonSampleStageChecker_EndNext_InLastStage", "END is the last stage, has no next one.", ""),
        SAMPLEWITHNORESULT("EnvMonSampleStageChecker_stagesCheckerSampleWithNoResult", "", ""),
        SAMPLEWITHNOSECONDENTRYRESULT("EnvMonSampleStageChecker_stagesCheckerSampleWithNoSecondEntryResult", "", ""),
        PARAMNAMEEMPTY("EnvMonSampleStageChecker_stagesParamNameEmpty", "", ""),
        YOUWIN("EnvMonSampleStageChecker_YoWin", "You win! This logic is not handled", "")
        
        ;
        
        private ProcedureSampleStageErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
       
    
    public String sampleStageSamplingPreviousChecker(String procInstanceName, Integer sampleId, String sampleData) {   
        return LPPlatform.LAB_FALSE+"SAMPLING_PREV_FIRSTSTAGE";
    }
    public String sampleStageSamplingNextChecker(String procInstanceName, Integer sampleId, String sampleData) {   
        Object[] objToJsonObj = convertToJsonObjectStringedObject(sampleData, true);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objToJsonObj[0].toString()))
           return LPPlatform.LAB_FALSE;
        JsonObject sampleStructure=(JsonObject) objToJsonObj[1];
        if (sampleStructure.get("sampling_date").isJsonNull())
            return LPPlatform.LAB_FALSE+"SAMPLINGDATE_MANDATORY"+"@"+sampleId; //" Fecha de muestreo es obligatoria para la muestra "+sampleId;
        String samplingDate=sampleStructure.get("sampling_date").toString();
        if (samplingDate==null || "null".equalsIgnoreCase(samplingDate)) {
            return LPPlatform.LAB_FALSE+"SAMPLINGDATE_MANDATORY"+"@"+sampleId;} // Fecha de muestreo es obligatoria para la muestra "+sampleId;}
        String reqsTrackingSamplingEnd=sampleStructure.get("requires_tracking_sampling_end").toString();        
        if (reqsTrackingSamplingEnd==null || !Boolean.valueOf(reqsTrackingSamplingEnd))
            return "LABPLANET_TRUE";
        String samplingDateEnd=sampleStructure.get("sampling_date_end").toString();        
        if (samplingDateEnd==null || "null".equalsIgnoreCase(samplingDateEnd)) {
            return LPPlatform.LAB_FALSE+"SAMPLINGDATEEND_MANDATORY"+"@"+sampleId;} // Fecha de muestreo es obligatoria para la muestra "+sampleId;}
        return LPPlatform.LAB_TRUE;
    }  

    public String sampleStageIncubationPreviousChecker(String procInstanceName, Integer sampleId, String sampleData) { 
        if (1==1)
            return LPPlatform.LAB_TRUE;
        Object[] objToJsonObj = convertToJsonObjectStringedObject(sampleData, true);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objToJsonObj[0].toString()))
           return LPPlatform.LAB_FALSE;
        JsonObject sampleStructure=(JsonObject) objToJsonObj[1];
        if (sampleStructure.get("incubation_passed").isJsonNull())
            return "FIRSTINCUB_MANDATORY"+"@"+sampleId; //" Pendiente 1a Incubacion para la muestra "+sampleId;}
        if (sampleStructure.get("incubation2_passed").isJsonNull())
            return "SECONDINCUB_MANDATORY"+"@"+sampleId; //" Pendiente 2a Incubacion para la muestra "+sampleId;}        
        boolean incubationStartIsNull = sampleStructure.get("incubation_start").isJsonNull();
        boolean incubation2StartIsNull = sampleStructure.get("incubation2_start").isJsonNull();
        String incubationPassedStr=sampleStructure.get("incubation_passed").getAsString();
        Boolean incubationPassed=Boolean.valueOf(incubationPassedStr);
        String incubation2PassedStr=sampleStructure.get("incubation2_passed").getAsString();
        Boolean incubation2Passed=Boolean.valueOf(incubation2PassedStr);
        if ((Boolean.FALSE.equals(incubationStartIsNull)) && (Boolean.FALSE.equals(incubationPassed)))
            return "INCUB_INPROGRESS"+"@"+sampleId;
        if ((Boolean.FALSE.equals(incubation2StartIsNull)) && (Boolean.FALSE.equals(incubation2Passed)))
            return "INCUB_INPROGRESS"+"@"+sampleId;
        if (Boolean.FALSE.equals(incubationPassed)){
            return "FIRSTINCUB_MANDATORY"+"@"+sampleId;} 
        if (Boolean.FALSE.equals(incubation2Passed)){
            return "SECONDINCUB_MANDATORY"+"@"+sampleId;} //" Pendiente 2a Incubacion para la muestra "+sampleId;}
        return LPPlatform.LAB_TRUE;
    }  

    public String sampleStageIncubationNextChecker(String procInstanceName, Integer sampleId, String sampleData) {   
        Object[] objToJsonObj = convertToJsonObjectStringedObject(sampleData, true);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objToJsonObj[0].toString()))
           return LPPlatform.LAB_FALSE;
        JsonObject sampleStructure=(JsonObject) objToJsonObj[1];
        if (sampleStructure.get("incubation_passed").isJsonNull())
            return "FIRSTINCUB_MANDATORY"+"@"+sampleId; //" Pendiente 1a Incubacion para la muestra "+sampleId;}
        if (sampleStructure.get("incubation2_passed").isJsonNull())
            return "SECONDINCUB_MANDATORY"+"@"+sampleId; //" Pendiente 2a Incubacion para la muestra "+sampleId;}
        String incubationPassedStr=sampleStructure.get("incubation_passed").getAsString();
        Boolean incubationPassed=Boolean.valueOf(incubationPassedStr);
        String incubation2PassedStr=sampleStructure.get("incubation2_passed").getAsString();
        Boolean incubation2Passed=Boolean.valueOf(incubation2PassedStr);
        if (Boolean.FALSE.equals(incubationPassed)){
            return "FIRSTINCUB_MANDATORY"+"@"+sampleId;} //" Pendiente 1a Incubacion para la muestra "+sampleId;}
        if (Boolean.FALSE.equals(incubation2Passed)){
            return "SECONDINCUB_MANDATORY"+"@"+sampleId;} //" Pendiente 2a Incubacion para la muestra "+sampleId;}
        return LPPlatform.LAB_TRUE;
    }  
    public String sampleStagePlateReadingPreviousChecker(String procInstanceName, Integer sampleId, String sampleData) {   
        return LPPlatform.LAB_TRUE;
    }
    public String sampleStagePlateReadingSecondEntryPreviousChecker(String procInstanceName, Integer sampleId, String sampleData) {   
        return LPPlatform.LAB_TRUE;
    }    
    public String sampleStagePlateReadingNextCheckerWithNoSecondEntry(String procInstanceName, Integer sampleId, String sampleData) { 
        try{
            Object[] objToJsonObj = convertToJsonObjectStringedObject(sampleData, true);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objToJsonObj[0].toString()))
               return LPPlatform.LAB_FALSE+"NOTPARSEABLE";
            JsonObject sampleStructure=(JsonObject) objToJsonObj[1];
            if (sampleStructure.get("sample_analysis").isJsonNull())
                return LPPlatform.LAB_FALSE+"SAMPLEANALYSIS_NOTFOUND";
            JsonArray smpAna=sampleStructure.getAsJsonArray("sample_analysis");
            JsonElement jGet = smpAna.get(0);        
            JsonObject asJsonObject = jGet.getAsJsonObject();
            if (asJsonObject.getAsJsonArray("sample_analysis_result").isJsonNull())
                return LPPlatform.LAB_FALSE+"SAMPLEANALYSISRESULT_NOTFOUND";
            JsonArray asJsonArray = asJsonObject.getAsJsonArray("sample_analysis_result"); //
            jGet = asJsonArray.get(0);        
            asJsonObject = jGet.getAsJsonObject();

            String rawValue="";
            if (asJsonObject.get("raw_value").isJsonNull())
                return LPPlatform.LAB_FALSE+"SAMPLEWITHNORESULT"+"@"+sampleId; //"raw value not entered yet";
            else
                rawValue=asJsonObject.get("raw_value").getAsString();

            String paramName="";
            if (asJsonObject.get("param_name").isJsonNull())
                return LPPlatform.LAB_FALSE+"PARAMNAMEEMPTY"+"@"+sampleId; //+"Parameter name is empty";
            else
                paramName=asJsonObject.get("param_name").getAsString();
            
            //String paramName=asJsonObject.get("param_name").getAsString();
            if ("Recuento".equals(paramName)){ 
                if ("0".equals(rawValue)) return LPPlatform.LAB_TRUE+"|END";
                else return LPPlatform.LAB_TRUE;
            }        
            return LPPlatform.LAB_FALSE+"YOUWIN";
        }catch(Exception e){
            return LPPlatform.LAB_FALSE+e.getMessage();
        }
    }

    public String sampleStageRevisionPreviousChecker(String procInstanceName, Integer sampleId, String sampleData) { 
        try{
                return LPPlatform.LAB_TRUE;
        }catch(Exception e){
            return LPPlatform.LAB_FALSE+e.getMessage();
        }        
    }

    public String sampleStageRevisionNextChecker(String procInstanceName, Integer sampleId, String sampleData) { 
        try{
                return LPPlatform.LAB_TRUE;
        }catch(Exception e){
            return LPPlatform.LAB_FALSE+e.getMessage();
        }        
    }
    public String sampleStagePlateReadingNextChecker(String procInstanceName, Integer sampleId, String sampleData) { 
        try{
            Object[] objToJsonObj = convertToJsonObjectStringedObject(sampleData, true);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objToJsonObj[0].toString()))
               return LPPlatform.LAB_FALSE+"NOTPARSEABLE";
            JsonObject sampleStructure=(JsonObject) objToJsonObj[1];
            if (sampleStructure.get("sample_analysis").isJsonNull())
                return LPPlatform.LAB_FALSE+"SAMPLEANALYSIS_NOTFOUND";
            JsonArray smpAna=sampleStructure.getAsJsonArray("sample_analysis");
            JsonElement jGet = smpAna.get(0);        
            JsonObject asJsonObject = jGet.getAsJsonObject();
            if (asJsonObject.getAsJsonArray("sample_analysis_result").isJsonNull())
                return LPPlatform.LAB_FALSE+"SAMPLEANALYSISRESULT_NOTFOUND";
            JsonArray asJsonArray = asJsonObject.getAsJsonArray("sample_analysis_result"); //
            jGet = asJsonArray.get(0);        
            asJsonObject = jGet.getAsJsonObject();

            if (asJsonObject.get("raw_value").isJsonNull())
                return LPPlatform.LAB_FALSE+"SAMPLEWITHNORESULT"+"@"+sampleId; 

            String paramName="";
            if (asJsonObject.get("param_name").isJsonNull())
                return LPPlatform.LAB_FALSE+"PARAMNAMEEMPTY"+"@"+sampleId; 
            else
                paramName=asJsonObject.get("param_name").getAsString();
            
            if (paramName.toUpperCase().contains("COUNT")||paramName.toUpperCase().contains("CUENT")
                    ||paramName.toUpperCase().equalsIgnoreCase("Recuento")||paramName.toUpperCase().contains("RESULT")){ 
                return LPPlatform.LAB_TRUE;
            }        
            return LPPlatform.LAB_FALSE+"YOUWIN";
        }catch(Exception e){
            return LPPlatform.LAB_FALSE+e.getMessage();
        }
    }
    public String sampleStagePlateReadingSecondEntryNextChecker(String procInstanceName, Integer sampleId, String sampleData) { 
        try{
            Object[] objToJsonObj = convertToJsonObjectStringedObject(sampleData, true);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objToJsonObj[0].toString()))
               return LPPlatform.LAB_FALSE+"NOTPARSEABLE";
            JsonObject sampleStructure=(JsonObject) objToJsonObj[1];
            if (sampleStructure.get("sample_analysis").isJsonNull())
                return LPPlatform.LAB_FALSE+"SAMPLEANALYSIS_NOTFOUND";
            JsonArray smpAna=sampleStructure.getAsJsonArray("sample_analysis");
            JsonElement jGet = smpAna.get(0);
            JsonObject asJsonObject = jGet.getAsJsonObject();
            if (asJsonObject.getAsJsonArray("sample_analysis_result").isJsonNull())
                return LPPlatform.LAB_FALSE+"SAMPLEANALYSISRESULT_NOTFOUND";
            JsonArray asJsonArray = asJsonObject.getAsJsonArray("sample_analysis_result"); //
            jGet = asJsonArray.get(0);
            asJsonObject = jGet.getAsJsonObject();

            String rawValue="";
            if (asJsonObject.get("sar2_"+"raw_value").isJsonNull())
                return LPPlatform.LAB_FALSE+"SAMPLEWITHNOSECONDENTRYRESULT"+"@"+sampleId;
            else
                rawValue=asJsonObject.get("sar2_"+"raw_value").getAsString();

            String paramName="";
            if (asJsonObject.get("sar2_"+"param_name").isJsonNull())
                return LPPlatform.LAB_FALSE+"PARAMNAMEEMPTY"+"@"+sampleId; //+"Parameter name is empty";
            else
                paramName=asJsonObject.get("sar2_"+"param_name").getAsString();
            
            //String paramName=asJsonObject.get("param_name").getAsString();
            if ("Recuento".equals(paramName)){ 
                //if ("0".equals(rawValue)) return LPPlatform.LAB_TRUE+"|END";
                //else 
                    return LPPlatform.LAB_TRUE;
            }        
            return LPPlatform.LAB_FALSE+"YOUWIN";
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
    public String sampleStageENDNextChecker(String procInstanceName, Integer sampleId, String sampleData) {   
        return LPPlatform.LAB_FALSE+"ALREADYLASTSTAGE";
    }
}

