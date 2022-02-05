/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

/**
 *
 * @author User
 */
public class EmdemoaSampleStage {
//    , JSONArray sampleData
public String sampleStageSamplingNextChecker(String sch, Integer sampleId) {
    //var sampleStructure=JSON.parse(sampleData);
    //var samplingDate = sampleStructure.sampling_date;
    //if (samplingDate==null){
      //  return testId+" Fecha de muestreo es obligatoria para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
}
public String sampleStagePlateReadingNextChecker(String sch, Integer sampleId) {
    //var sampleStructure=JSON.parse(sampleData);
    //var samplingDate = sampleStructure.sampling_date;
    //if (samplingDate==null){
      //  return testId+" Fecha de muestreo es obligatoria para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
}
/*
var sampleStageIncubationPreviousChecker = function(sampleId, sampleData) {
    sampleStructure=JSON.parse(sampleData);
    var incubationPassed = sampleStructure.incubation_passed;
    var incubation2Passed = sampleStructure.incubation2_passed;
    if (incubationPassed!=true){
        return " Pendiente 1a Incubacion para la muestra "+sampleId;}
    if (incubation2Passed!=true){
        return " Pendiente 2a Incubacion para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
};

var sampleStageIncubationNextChecker = function(sampleId, sampleData) {
    sampleStructure=JSON.parse(sampleData);
    var incubationPassed = sampleStructure.incubation_passed;
    var incubation2Passed = sampleStructure.incubation2_passed;
    if (incubationPassed!=true){
        return " Pendiente 1a Incubacion para la muestra "+sampleId;}
    if (incubation2Passed!=true){
        return " Pendiente 2a Incubacion para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
};

var sampleStagePlateReadingPreviousChecker = function(sampleId, sampleData) {
    // val = val * 2;
    return "LABPLANET_TRUE";
    if (sampleId==143){ return "LABPLANET_TRUE";}
    return sampleId+"LABPLANET_FALSE sampleID should be 143 and is "+sampleId;
};

var sampleStagePlateReadingNextChecker = function(sampleId, sampleData) {
    // val = val * 2;
    return "LABPLANET_TRUE";
    var smpStatus = "";//sampleData.sample_analysis[0].test_id;
    if (sampleId==143){ return "LABPLANET_TRUE";}
    return smpStatus+"LABPLANET_FALSE sampleID should be 143 and is "+sampleId;
};

var sampleStageMicroorganismIdentificationNextChecker = function(sampleId, sampleData) {
    // val = val * 2;
    return "LABPLANET_TRUE";
    var smpStatus = "";//sampleData.sample_analysis[0].test_id;
    if (sampleId==143){ return "LABPLANET_TRUE";}
    return smpStatus+"LABPLANET_FALSE sampleID should be 143 and is "+sampleId;
};  */  
}
