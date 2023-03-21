let sampleStageSamplingNextChecker = function(sampleId, sampleData) {
    let sampleStructure=JSON.parse(sampleData);
    let samplingDate = sampleStructure.sampling_date;
    if (samplingDate===null){
        return " Fecha de muestreo es obligatoria para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
};

let sampleStageIncubationPreviousChecker = function(sampleId, sampleData) {
    let sampleStructure=JSON.parse(sampleData);
    let incubationPassed = sampleStructure.incubation_passed;
    let incubation2Passed = sampleStructure.incubation2_passed;
    if (incubationPassed!==true){
        return " Pendiente 1a Incubacion para la muestra "+sampleId;}
    if (incubation2Passed!==true){
        return " Pendiente 2a Incubacion para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
};

let sampleStageIncubationNextChecker = function(sampleId, sampleData) {
    let sampleStructure=JSON.parse(sampleData);
    let incubationPassed = sampleStructure.incubation_passed;
    let incubation2Passed = sampleStructure.incubation2_passed;
    if (incubationPassed!==true){
        return " Pendiente 1a Incubacion para la muestra "+sampleId;}
    if (incubation2Passed!==true){
        return " Pendiente 2a Incubacion para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
};

let sampleStagePlateReadingPreviousChecker = function(sampleId, sampleData) {
    return "LABPLANET_TRUE";
};

let sampleStagePlateReadingNextChecker = function(sampleId, sampleData) {
    return "LABPLANET_TRUE";
};

let sampleStageMicroorganismIdentificationNextChecker = function(sampleId, sampleData) {
    return "LABPLANET_TRUE";
};

