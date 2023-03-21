let sampleStageSamplingNextChecker = function(sampleId, sampleData) {
    let sampleStructure=JSON.parse(sampleData);
    let samplingDate = sampleStructure.sampling_date;
//	let testId=sampleStructure.sample_analysis[0].analysis	
    if (samplingDate===null){
        return "stagesCheckerSamplingDataIsMandatory"+"@"+sampleId;} //" Fecha de muestreo es obligatoria para la muestra "+sampleId;}    
    let reqsTrackingSamplingEnd = sampleStructure.requires_tracking_sampling_end;
    if (reqsTrackingSamplingEnd===null || reqsTrackingSamplingEnd===false)
        return "LABPLANET_TRUE";
    let samplingDateEnd = sampleStructure.sampling_date_end;
    if (samplingDateEnd===null){
        return "stagesCheckerSamplingDateEndIsMandatory"+"@"+sampleId;} //" Fecha de muestreo es obligatoria para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
};

let sampleStageIncubationPreviousChecker = function(sampleId, sampleData) {
    let sampleStructure=JSON.parse(sampleData);
    let incubationPassed = sampleStructure.incubation_passed;
    let incubation2Passed = sampleStructure.incubation2_passed;
    if (incubationPassed!==true){
        return "stagesCheckerPendingFirstIncubation"+"@"+sampleId;} // Pendiente 1a Incubacion para la muestra "+sampleId;}
    if (incubation2Passed!==true){
        return "stagesCheckerPendingsecondIncubation"+"@"+sampleId;} //" Pendiente 2a Incubacion para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
};

let sampleStageIncubationNextChecker = function(sampleId, sampleData) {
    let sampleStructure=JSON.parse(sampleData);
    let incubationPassed = sampleStructure.incubation_passed;
    let incubation2Passed = sampleStructure.incubation2_passed;
    if (!incubationPassed){
        return "stagesCheckerPendingFirstIncubation"+"@"+sampleId;} //" Pendiente 1a Incubacion para la muestra "+sampleId;}
    if (!incubation2Passed){
        return "stagesCheckerPendingSecondIncubation"+"@"+sampleId;} //" Pendiente 2a Incubacion para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
};

let sampleStagePlateReadingPreviousChecker = function(sampleId, sampleData) {
    return "LABPLANET_TRUE";
};

let sampleStagePlateReadingNextChecker = function(schema, sampleId, sampleData) {
    let s=schema;
    let val = sampleId * 2;
    return "LABPLANET_TRUE";
};

let sampleStageMicroorganismIdentificationNextChecker = function(sampleId, sampleData) {
    return "LABPLANET_TRUE";
};

