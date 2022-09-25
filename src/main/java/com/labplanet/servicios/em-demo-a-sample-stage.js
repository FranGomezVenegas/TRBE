var sampleStageSamplingNextChecker = function(sampleId, sampleData) {
    var sampleStructure=JSON.parse(sampleData);
    var samplingDate = sampleStructure.sampling_date;
//	var testId=sampleStructure.sample_analysis[0].analysis	
    if (samplingDate===null){
        return " Fecha de muestreo es obligatoria para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
};

var sampleStageIncubationPreviousChecker = function(sampleId, sampleData) {
    var sampleStructure=JSON.parse(sampleData);
    var incubationPassed = sampleStructure.incubation_passed;
    var incubation2Passed = sampleStructure.incubation2_passed;
    if (incubationPassed!==true){
        return " Pendiente 1a Incubacion para la muestra "+sampleId;}
    if (incubation2Passed!==true){
        return " Pendiente 2a Incubacion para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
};

var sampleStageIncubationNextChecker = function(sampleId, sampleData) {
    var sampleStructure=JSON.parse(sampleData);
    var incubationPassed = sampleStructure.incubation_passed;
    var incubation2Passed = sampleStructure.incubation2_passed;
    if (incubationPassed!==true){
        return " Pendiente 1a Incubacion para la muestra "+sampleId;}
    if (incubation2Passed!==true){
        return " Pendiente 2a Incubacion para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
};

var sampleStagePlateReadingPreviousChecker = function(sampleId, sampleData) {
    // val = val * 2;
    return "LABPLANET_TRUE";
};

var sampleStagePlateReadingNextChecker = function(sampleId, sampleData) {
    // val = val * 2;
    return "LABPLANET_TRUE";
};

var sampleStageMicroorganismIdentificationNextChecker = function(sampleId, sampleData) {
    // val = val * 2;
    return "LABPLANET_TRUE";
};

