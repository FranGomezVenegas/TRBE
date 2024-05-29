/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.methodvalidation.logic;

import databases.Rdbms;
import databases.TblsCnfg;
import databases.TblsData;
import functionaljavaa.samplestructure.DataSample;
import functionaljavaa.samplestructure.DataSampleAnalysis;
import functionaljavaa.samplestructure.DataSampleAnalysisStrategy;
import functionaljavaa.samplestructure.DataSampleStructureEnums;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import module.methodvalidation.definition.TblsMethodValidationData;
import module.monitoring.logic.EnvMonEnums;
import module.projectrnd.definition.TblsProjectRnDConfig;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
/**
 *
 * @author Administrator
 */
public class DataParameterSampleAnalysis implements DataSampleAnalysisStrategy {
     String[] mandatoryFields = null;

    /**
     *
     */
    Object[] mandatoryFieldsValue = null;
    /**
     *
     * @param sampleId
     * @param sampleFieldName
     * @param sampleFieldValue
     * @return
     */
    @Override
    public InternalMessage autoSampleAnalysisAdd(Integer sampleId, String[] sampleFieldName, Object[] sampleFieldValue) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        Object[][] anaName = new Object[0][0];
        StringBuilder analysisAdded = new StringBuilder();
        
//        Integer posicField = LPArray.valuePosicInArray(sampleFieldName, TblsMethodValidationData.Sample.PARAMETER_NAME.getName());
        Integer posicField = LPArray.valuePosicInArray(sampleFieldName, TblsMethodValidationData.Sample.ANALYTICAL_PARAMETER.getName());
        if (posicField!=-1){
            String analyticalParameter=sampleFieldValue[posicField].toString();
            Object[][] methodInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsCnfg.TablesConfig.METHODS.getRepositoryName()), 
                TblsCnfg.TablesConfig.METHODS.getTableName(),
            new String[]{TblsProjectRnDConfig.Methods.CODE.getName()}, new Object[]{analyticalParameter}, 
            new String[]{TblsProjectRnDConfig.Methods.ADD_ANALYSIS_ON_LOG.getName(), TblsProjectRnDConfig.Methods.ANALYSIS_LIST.getName(),
                TblsProjectRnDConfig.Methods.NUM_SAMPLES.getName()}, true);            
  /*          String parameterName=sampleFieldValue[posicField].toString();
            Object[][] analyticalParameterInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getRepositoryName()), TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getTableName(),
                new String[]{TblsMethodValidationData.ValidationMethodParams.NAME.getName()}, 
                new Object[]{parameterName}, 
                new String[]{TblsMethodValidationData.ValidationMethodParams.ADD_ANALYSIS_ON_LOG.getName(), TblsMethodValidationData.ValidationMethodParams.ANALYSIS_LIST.getName(), 
                    TblsMethodValidationData.ValidationMethodParams.ANALYTICAL_PARAMETER.getName()});*/
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(methodInfo[0][0].toString()))
               return new InternalMessage(LPPlatform.LAB_FALSE, EnvMonEnums.EnvMonitErrorTrapping.LOGSAMPLE_PROGRAM_OR_LOCATION_NOTFOUND, new Object[]{analyticalParameter});
            if (Boolean.FALSE.equals(Boolean.valueOf(LPNulls.replaceNull(methodInfo[0][0].toString())))){
                return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleStructureEnums.DataSampleStructureSuccess.AUTOSAMPLEANALYSIS_ADDED_SUCCESS, new String[]{"Added analysis " + analysisAdded.toString() + " to the sample " + sampleId.toString() + " for schema " + procInstanceName});
            }
            String analysisList=methodInfo[0][1].toString();
           // String analyticalParameter=LPNulls.replaceNull(methodInfo[0][2]).toString();
            for (String curAnalysis : analysisList.split("\\|")) {
                String[] fieldsName = new String[]{TblsData.SampleAnalysis.ANALYSIS.getName(), TblsData.SampleAnalysis.METHOD_NAME.getName(), TblsData.SampleAnalysis.METHOD_VERSION.getName()};
                Object[] fieldsValue = new Object[]{(String) curAnalysis, analyticalParameter, 1};

                InternalMessage sampleAnalysisAddtoSample = DataSampleAnalysis.sampleAnalysisAddtoSample(sampleId, fieldsName, fieldsValue);
                
                analysisAdded.append(LPArray.convertArrayToString(analyticalParameter.split("\\|"), ",", ""));
            }        
        }        
/*        
        InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(sampleFieldName, sampleFieldValue);
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic())))
            return new InternalMessage(LPPlatform.LAB_FALSE, fieldNameValueArrayChecker.getMessageCodeObj(), fieldNameValueArrayChecker.getMessageCodeVariables());

        Object[][] anaName = new Object[0][0];
        String otro = DataSampleAnalysis.DataSampleAnalyisAutoAddLevel.SPEC_VARIATION.getName();
        DataSampleAnalysis.DataSampleAnalyisAutoAddLevel autoAddAnalysisLevel = DataSampleAnalysis.DataSampleAnalyisAutoAddLevel.valueOf(otro);
        switch (autoAddAnalysisLevel){     
            case SPEC_VARIATION:
                Object[][] specFields = new Object[][]{{TblsData.Sample.SPEC_CODE.getName(), "", TblsCnfg.SpecLimits.CODE.getName()}, 
                    {TblsData.Sample.SPEC_CODE_VERSION.getName(), "", TblsCnfg.SpecLimits.CONFIG_VERSION.getName()}, 
                    {TblsData.Sample.SPEC_VARIATION_NAME.getName(), "", TblsCnfg.SpecLimits.VARIATION_NAME.getName()}};
                String[] specMissingFields = new String[0];
                for (Object[] curValue: specFields){
                    Integer posicField = LPArray.valuePosicInArray(sampleFieldName, curValue[0].toString());
                    if (posicField == -1){specMissingFields = LPArray.addValueToArray1D(specMissingFields, curValue[0].toString()); curValue[1] = specMissingFields.length;
                    }else{curValue[1] = sampleFieldValue[posicField];}                
                }
                if (specMissingFields.length>0){
                    Object[][] sampleSpecInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                            new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, specMissingFields);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleSpecInfo[0][0].toString())){
                        return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{sampleId});
                    }
//                      for (String specMissingField : specMissingFields) {
                        // Pasar de sampleSpecInfo a specFields estando los datos en  specMissingFields
//                      }
                }
                String[] specWhereFieldName=LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(specFields, 2));
                Object[] specWhereFieldValue=LPArray.getColumnFromArray2D(specFields, 1);

                Integer posicField = LPArray.valuePosicInArray(sampleFieldName, TblsData.Sample.SPEC_ANALYSIS_VARIATION.getName());
                if (posicField > -1){
                    Object analysisVariation= sampleFieldValue[posicField];
                    String[] analysisVariationArr=analysisVariation.toString().split("\\-");
                    if (analysisVariationArr.length==2){
                        specWhereFieldName=LPArray.addValueToArray1D(specWhereFieldName, TblsCnfg.SpecLimits.ANALYSIS.getName());
                        specWhereFieldName=LPArray.addValueToArray1D(specWhereFieldName, TblsCnfg.SpecLimits.METHOD_NAME.getName());
                        specWhereFieldValue=LPArray.addValueToArray1D(specWhereFieldValue, analysisVariationArr[0]);
                        specWhereFieldValue=LPArray.addValueToArray1D(specWhereFieldValue, analysisVariationArr[1]);                    
                    }
                }                 
                anaName=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName(), 
                        specWhereFieldName, specWhereFieldValue, 
                        new String[]{TblsCnfg.SpecLimits.ANALYSIS.getName(), TblsCnfg.SpecLimits.METHOD_NAME.getName(), TblsCnfg.SpecLimits.METHOD_VERSION.getName(), TblsCnfg.SpecLimits.TESTING_GROUP.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(anaName[0][0].toString())){
                    return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{sampleId});
                }                
                break;
            case SPEC:
            default:
                return new InternalMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.AUTOSAMPLEANALYSISADD_CASE_NOT_DETECTED, new String[]{autoAddAnalysisLevel + " not implemented yet."});
        }
        
        StringBuilder analysisAdded = new StringBuilder();
        for (Object[] anaName1 : anaName) {
            String[] fieldsName = new String[]{TblsData.SampleAnalysis.ANALYSIS.getName(), TblsData.SampleAnalysis.METHOD_NAME.getName(), TblsData.SampleAnalysis.METHOD_VERSION.getName()};
            Object[] fieldsValue = new Object[]{(String) anaName1[0], (String) anaName1[1], (Integer) anaName1[2]};
            Object[] isReviewByTestingGroupEnable=LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleRevisionTestingGroup.DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP.getTagName());
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString())){
                fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysis.TESTING_GROUP.getName());
                fieldsValue= LPArray.addValueToArray1D(fieldsValue, anaName1[3].toString());
            }
            
            DataSampleAnalysis.sampleAnalysisAddtoSample(sampleId, fieldsName, fieldsValue);
            analysisAdded.append(LPArray.convertArrayToString(anaName1, ",", ""));
        }        
*/        
        return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleStructureEnums.DataSampleStructureSuccess.AUTOSAMPLEANALYSIS_ADDED_SUCCESS, new String[]{"Added analysis " + analysisAdded.toString() + " to the sample " + sampleId.toString() + " for schema " + procInstanceName});
    }
    /**
     *
     * @param template
     * @param templateVersion
   * @param dataSample
     * @return
     */
    @Override
    public InternalMessage specialFieldCheckSampleAnalysisAnalyst(String template, Integer templateVersion, DataSample dataSample) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String schemaConfigName = GlobalVariables.Schemas.CONFIG.getName();
        schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, schemaConfigName);
        return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
/*        
        Integer specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(TblsData.SampleAnalysis.STATUS.getName());
        String status = mandatoryFieldsValue[specialFieldIndex].toString();
        if (status.length() == 0) return "ERROR: The parameter status cannot be null";
        
        Object[] diagnosis = Rdbms.existsRecord(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.SAMPLE_RULES.getTableName(), 
                new String[]{TblsCnfg.SampleRules.CODE.getName(), TblsCnfg.SampleRules.CODE_VERSION.getName()}, new Object[]{template, templateVersion});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())) 
            return "ERROR: The sample_rule record for " + template + " does not exist in schema" + schemaConfigName + ". ERROR: " + diagnosis[5];
        
        String[] fieldNames = new String[1];
        Object[] fieldValues = new Object[1];
        fieldNames[0] = TblsCnfg.SampleRules.CODE.getName();
        fieldValues[0] = template;
        String[] fieldFilter = new String[]{TblsCnfg.SampleRules.CODE.getName(), TblsCnfg.SampleRules.CODE_VERSION.getName(), 
            TblsCnfg.SampleRules.STATUSES.getName(), TblsCnfg.SampleRules.DEFAULT_STATUS.getName()};
        Object[][] records = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.SAMPLE_RULES.getTableName(), 
                fieldNames, fieldValues, fieldFilter);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(records[0][0].toString())) 
            return "ERROR: Problem on getting sample rules for " + template + " exists but the rule record is missing in the schema " + schemaConfigName;
        String statuses = records[0][2].toString();
        if (LPArray.valueInArray(statuses.split("\\|", -1), status)) {
            return DataSample.DIAGNOSES_SUCCESS;
        } else {
            return "ERROR: The status " + status + " is not of one the defined status (" + statuses + " for the template " + template + " exists but the rule record is missing in the schema " + schemaConfigName;
        }
*/
    }
    @Override
    public InternalMessage calcsPostEnterResult(Integer resultId, Integer testId, Integer sampleId, DataSample dataSample) {
        return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
    }


  
}
