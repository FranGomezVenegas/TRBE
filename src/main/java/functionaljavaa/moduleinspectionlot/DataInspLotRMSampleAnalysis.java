/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleinspectionlot;

import databases.Rdbms;
import databases.TblsCnfg;
import databases.TblsData;
import functionaljavaa.samplestructure.DataSample;
import functionaljavaa.samplestructure.DataSampleAnalysis;
import functionaljavaa.samplestructure.DataSampleAnalysisStrategy;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
/**
 *
 * @author User
 */
public class DataInspLotRMSampleAnalysis implements DataSampleAnalysisStrategy {

    @Override
    public Object[] autoSampleAnalysisAdd(Integer sampleId, String[] sampleFieldName, Object[] sampleFieldValue) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(sampleFieldName, sampleFieldValue);
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, fieldNameValueArrayChecker.getMessageCodeObj(), fieldNameValueArrayChecker.getMessageCodeVariables());

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
                    Object[][] sampleSpecInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                            new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, specMissingFields);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleSpecInfo[0][0].toString())){return LPArray.array2dTo1d(sampleSpecInfo);}
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
                anaName=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName(), 
                        specWhereFieldName, specWhereFieldValue, 
                        new String[]{TblsCnfg.SpecLimits.ANALYSIS.getName(), TblsCnfg.SpecLimits.METHOD_NAME.getName(), TblsCnfg.SpecLimits.METHOD_VERSION.getName(), TblsCnfg.SpecLimits.TESTING_GROUP.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(anaName[0][0].toString())){return LPArray.array2dTo1d(anaName);}
                
                break;
            case SPEC:
            default:
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "autoSampleAnalysisAdd_caseNotDetected", new String[]{autoAddAnalysisLevel+" not implemented yet."});
        }

        StringBuilder analysisAdded = new StringBuilder();
        for (Object[] anaName1 : anaName) {
            String[] fieldsName = new String[]{TblsData.SampleAnalysis.ANALYSIS.getName(), TblsData.SampleAnalysis.METHOD_NAME.getName(), TblsData.SampleAnalysis.METHOD_VERSION.getName(), TblsData.SampleAnalysis.TESTING_GROUP.getName()};
            Object[] fieldsValue = new Object[]{(String) anaName1[0], (String) anaName1[1], (Integer) anaName1[2], (String) anaName1[3]};
            DataSampleAnalysis.sampleAnalysisAddtoSample(sampleId, fieldsName, fieldsValue);
            analysisAdded.append(LPArray.convertArrayToString(anaName1, ",", ""));
        }        
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "autoSampleAnalysisAdded_success", new String[]{"Added analysis "+analysisAdded.toString()+" to the sample "+sampleId.toString()+" for schema "+procInstanceName});        
    }

    @Override
    public String specialFieldCheckSampleAnalysisAnalyst(String template, Integer templateVersion, DataSample dataSample) {
        return "";
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
