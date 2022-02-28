/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import databases.Rdbms;
import databases.TblsCnfg;
import databases.TblsData;
import functionaljavaa.samplestructure.DataSample;
import functionaljavaa.samplestructure.DataSampleAnalysis;
import functionaljavaa.samplestructure.DataSampleAnalysisStrategy;
import functionaljavaa.samplestructure.DataSampleRevisionTestingGroup;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author Administrator
 */
public class DataProgramSampleAnalysis implements DataSampleAnalysisStrategy {
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
    public Object[] autoSampleAnalysisAdd(Integer sampleId, String[] sampleFieldName, Object[] sampleFieldValue) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(sampleFieldName, sampleFieldValue);
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker[0].toString())) return fieldNameValueArrayChecker;

        Object[][] anaName = new Object[0][0];
        String otro = DataSampleAnalysis.DataSampleAnalyisAutoAddLevel.SPEC_VARIATION.getName();
        DataSampleAnalysis.DataSampleAnalyisAutoAddLevel autoAddAnalysisLevel = DataSampleAnalysis.DataSampleAnalyisAutoAddLevel.valueOf(otro);
        switch (autoAddAnalysisLevel){     
            case SPEC_VARIATION:
                Object[][] specFields = new Object[][]{{TblsData.Sample.FLD_SPEC_CODE.getName(), "", TblsCnfg.SpecLimits.FLD_CODE.getName()}, 
                    {TblsData.Sample.FLD_SPEC_CODE_VERSION.getName(), "", TblsCnfg.SpecLimits.FLD_CONFIG_VERSION.getName()}, 
                    {TblsData.Sample.FLD_SPEC_VARIATION_NAME.getName(), "", TblsCnfg.SpecLimits.FLD_VARIATION_NAME.getName()}};
                String[] specMissingFields = new String[0];
                for (Object[] curValue: specFields){
                    Integer posicField = LPArray.valuePosicInArray(sampleFieldName, curValue[0].toString());
                    if (posicField == -1){specMissingFields = LPArray.addValueToArray1D(specMissingFields, curValue[0].toString()); curValue[1] = specMissingFields.length;
                    }else{curValue[1] = sampleFieldValue[posicField];}                
                }
                if (specMissingFields.length>0){
                    Object[][] sampleSpecInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                            new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, specMissingFields);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleSpecInfo[0][0].toString())){return LPArray.array2dTo1d(sampleSpecInfo);}
//                      for (String specMissingField : specMissingFields) {
                        // Pasar de sampleSpecInfo a specFields estando los datos en  specMissingFields
//                      }
                }
                String[] specWhereFieldName=LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(specFields, 2));
                Object[] specWhereFieldValue=LPArray.getColumnFromArray2D(specFields, 1);

                Integer posicField = LPArray.valuePosicInArray(sampleFieldName, TblsData.Sample.FLD_SPEC_ANALYSIS_VARIATION.getName());
                if (posicField > -1){
                    Object analysisVariation= sampleFieldValue[posicField];
                    String[] analysisVariationArr=analysisVariation.toString().split("\\-");
                    if (analysisVariationArr.length==2){
                        specWhereFieldName=LPArray.addValueToArray1D(specWhereFieldName, TblsCnfg.SpecLimits.FLD_ANALYSIS.getName());
                        specWhereFieldName=LPArray.addValueToArray1D(specWhereFieldName, TblsCnfg.SpecLimits.FLD_METHOD_NAME.getName());
                        specWhereFieldValue=LPArray.addValueToArray1D(specWhereFieldValue, analysisVariationArr[0]);
                        specWhereFieldValue=LPArray.addValueToArray1D(specWhereFieldValue, analysisVariationArr[1]);                    
                    }
                }                 
                anaName=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName(), 
                        specWhereFieldName, specWhereFieldValue, 
                        new String[]{TblsCnfg.SpecLimits.FLD_ANALYSIS.getName(), TblsCnfg.SpecLimits.FLD_METHOD_NAME.getName(), TblsCnfg.SpecLimits.FLD_METHOD_VERSION.getName(), TblsCnfg.SpecLimits.FLD_TESTING_GROUP.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(anaName[0][0].toString())){return LPArray.array2dTo1d(anaName);}
                
                break;
            case SPEC:
            default:
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "autoSampleAnalysisAdd_caseNotDetected", new String[]{autoAddAnalysisLevel+" not implemented yet."});
        }
        StringBuilder analysisAdded = new StringBuilder();
        for (Object[] anaName1 : anaName) {
            String[] fieldsName = new String[]{TblsData.SampleAnalysis.FLD_ANALYSIS.getName(), TblsData.SampleAnalysis.FLD_METHOD_NAME.getName(), TblsData.SampleAnalysis.FLD_METHOD_VERSION.getName()};
            Object[] fieldsValue = new Object[]{(String) anaName1[0], (String) anaName1[1], (Integer) anaName1[2]};
            Object[] isReviewByTestingGroupEnable=LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleRevisionTestingGroup.DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP.getTagName());
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString())){
                fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysis.FLD_TESTING_GROUP.getName());
                fieldsValue= LPArray.addValueToArray1D(fieldsValue, (String) anaName1[3]);
            }
            
            DataSampleAnalysis.sampleAnalysisAddtoSample(sampleId, fieldsName, fieldsValue);
            analysisAdded.append(LPArray.convertArrayToString(anaName1, ",", ""));
        }        
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "autoSampleAnalysisAdded_success", new String[]{"Added analysis "+analysisAdded.toString()+" to the sample "+sampleId.toString()+" for schema "+procInstanceName});        
    }
    /**
     *
     * @param template
     * @param templateVersion
   * @param dataSample
     * @param preAuditId
     * @return
     */
  @Override
    public String specialFieldCheckSampleAnalysisAnalyst(String template, Integer templateVersion, DataSample dataSample) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String schemaConfigName = GlobalVariables.Schemas.CONFIG.getName();
        schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, schemaConfigName);
if (1 == 1) 
            return"ERROR: specialFieldCheckSampleAnalysisAnalyst not implemented yet.";
        
        Integer specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(TblsData.SampleAnalysis.FLD_STATUS.getName());
        String status = mandatoryFieldsValue[specialFieldIndex].toString();
        if (status.length() == 0) return "ERROR: The parameter status cannot be null";
        
        Object[] diagnosis = Rdbms.existsRecord(schemaConfigName, TblsCnfg.TablesConfig.SAMPLE_RULES.getTableName(), 
                new String[]{TblsCnfg.SampleRules.FLD_CODE.getName(), TblsCnfg.SampleRules.FLD_CODE_VERSION.getName()}, new Object[]{template, templateVersion});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())) 
            return "ERROR: The sample_rule record for " + template + " does not exist in schema" + schemaConfigName + ". ERROR: " + diagnosis[5];
        
        String[] fieldNames = new String[1];
        Object[] fieldValues = new Object[1];
        fieldNames[0] = TblsCnfg.SampleRules.FLD_CODE.getName();
        fieldValues[0] = template;
        String[] fieldFilter = new String[]{TblsCnfg.SampleRules.FLD_CODE.getName(), TblsCnfg.SampleRules.FLD_CODE_VERSION.getName(), 
            TblsCnfg.SampleRules.FLD_STATUSES.getName(), TblsCnfg.SampleRules.FLD_DEFAULT_STATUS.getName()};
        Object[][] records = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.TablesConfig.SAMPLE_RULES.getTableName(), 
                fieldNames, fieldValues, fieldFilter);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(records[0][0].toString())) 
            return "ERROR: Problem on getting sample rules for " + template + " exists but the rule record is missing in the schema " + schemaConfigName;
        String statuses = records[0][2].toString();
        if (LPArray.valueInArray(statuses.split("\\|", -1), status)) {
            return DataSample.DIAGNOSES_SUCCESS;
        } else {
            return "ERROR: The status " + status + " is not of one the defined status (" + statuses + " for the template " + template + " exists but the rule record is missing in the schema " + schemaConfigName;
        }
    }


  
}
