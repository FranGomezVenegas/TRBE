let sampleStageSamplingNextChecker = function(sampleId, sampleData) {
    let sampleStructure=JSON.parse(sampleData);
    let samplingDate = sampleStructure.sampling_date;
    if (samplingDate===null){
        return "stagesCheckerSamplingDataIsMandatory"+"@"+sampleId;} 
    let reqsTrackingSamplingEnd = sampleStructure.requires_tracking_sampling_end;
    if (reqsTrackingSamplingEnd===null || reqsTrackingSamplingEnd===false)
        return "LABPLANET_TRUE";
    let samplingDateEnd = sampleStructure.sampling_date_end;
    if (samplingDateEnd===null){
        return "stagesCheckerSamplingDateEndIsMandatory"+"@"+sampleId;} 
    return "LABPLANET_TRUE";
};

let sampleStageIncubationPreviousChecker = function(sampleId, sampleData) {
    let sampleStructure=JSON.parse(sampleData);
    let incubationPassed = sampleStructure.incubation_passed;
    let incubation2Passed = sampleStructure.incubation2_passed;
    if (incubationPassed!==true){
        return "stagesCheckerPendingFirstIncubation"+"@"+sampleId;} 
    if (incubation2Passed!==true){
        return "stagesCheckerPendingsecondIncubation"+"@"+sampleId;} 
    return "LABPLANET_TRUE";
};

let sampleStageIncubationNextChecker = function(sampleId, sampleData) {
    let sampleStructure=JSON.parse(sampleData);
    let incubationPassed = sampleStructure.incubation_passed;
    let incubation2Passed = sampleStructure.incubation2_passed;
    if (!incubationPassed){
        return "stagesCheckerPendingFirstIncubation"+"@"+sampleId;}
    if (!incubation2Passed){
        return "stagesCheckerPendingSecondIncubation"+"@"+sampleId;} 
    return "LABPLANET_TRUE";
};

let sampleStagePlateReadingPreviousChecker = function(sampleId, sampleData) {
    return "LABPLANET_TRUE";
};

let sampleStagePlateReadingNextChecker = function(schema, sampleId, sampleData) {
    return "LABPLANET_TRUE";
};

let sampleStageMicroorganismIdentificationNextChecker = function(sampleId, sampleData) {
    return "LABPLANET_TRUE";
};

