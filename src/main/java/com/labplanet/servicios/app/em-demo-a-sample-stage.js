var sampleStageSamplingNextChecker = function(sampleId, sampleData) {
    var sampleStructure=JSON.parse(sampleData);
    var samplingDate = sampleStructure.sampling_date;
//	var testId=sampleStructure.sample_analysis[0].analysis	
    if (samplingDate===null){
        return "stagesCheckerSamplingDataIsMandatory"+"@"+sampleId;} //" Fecha de muestreo es obligatoria para la muestra "+sampleId;}    
    var reqsTrackingSamplingEnd = sampleStructure.requires_tracking_sampling_end;
    if (reqsTrackingSamplingEnd===null || reqsTrackingSamplingEnd===false)
        return "LABPLANET_TRUE";
    var samplingDateEnd = sampleStructure.sampling_date_end;
    if (samplingDateEnd===null){
        return "stagesCheckerSamplingDateEndIsMandatory"+"@"+sampleId;} //" Fecha de muestreo es obligatoria para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
};

var sampleStageIncubationPreviousChecker = function(sampleId, sampleData) {
    var sampleStructure=JSON.parse(sampleData);
    var incubationPassed = sampleStructure.incubation_passed;
    var incubation2Passed = sampleStructure.incubation2_passed;
    if (incubationPassed!==true){
        return "stagesCheckerPendingFirstIncubation"+"@"+sampleId;} // Pendiente 1a Incubacion para la muestra "+sampleId;}
    if (incubation2Passed!==true){
        return "stagesCheckerPendingsecondIncubation"+"@"+sampleId;} //" Pendiente 2a Incubacion para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
};

var sampleStageIncubationNextChecker = function(sampleId, sampleData) {
    var sampleStructure=JSON.parse(sampleData);
    var incubationPassed = sampleStructure.incubation_passed;
    var incubation2Passed = sampleStructure.incubation2_passed;
    if (!incubationPassed){
        return "stagesCheckerPendingFirstIncubation"+"@"+sampleId;} //" Pendiente 1a Incubacion para la muestra "+sampleId;}
    if (!incubation2Passed){
        return "stagesCheckerPendingSecondIncubation"+"@"+sampleId;} //" Pendiente 2a Incubacion para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
};

var sampleStagePlateReadingPreviousChecker = function(sampleId, sampleData) {
    return "LABPLANET_TRUE";
};

var sampleStagePlateReadingNextChecker = function(schema, sampleId, sampleData) {
    var s=schema;
    var val = sampleId * 2;
    return "LABPLANET_TRUE";
};

var sampleStageMicroorganismIdentificationNextChecker = function(sampleId, sampleData) {
    return "LABPLANET_TRUE";
};

