/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.logic;

import databases.Rdbms;
import databases.TblsCnfg;
import databases.TblsData;
import functionaljavaa.materialspec.SpecFrontEndUtilities;
import functionaljavaa.samplestructure.DataSample;
import functionaljavaa.samplestructure.DataSampleAnalysis;
import functionaljavaa.samplestructure.DataSampleAnalysisStrategy;
import java.util.Iterator;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(sampleFieldName, sampleFieldValue);
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic()))) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, fieldNameValueArrayChecker.getMessageCodeObj(), fieldNameValueArrayChecker.getMessageCodeVariables());
        }

        String otro = DataSampleAnalysis.DataSampleAnalyisAutoAddLevel.SPEC_VARIATION.getName();
        DataSampleAnalysis.DataSampleAnalyisAutoAddLevel autoAddAnalysisLevel = DataSampleAnalysis.DataSampleAnalyisAutoAddLevel.valueOf(otro);
        JSONArray specLimitsInfo = new JSONArray();
        Object[] specFieldsValues = new Object[]{};
        String[] specFields = new String[]{};
        switch (autoAddAnalysisLevel) {
            case SPEC_VARIATION:
                specFields = new String[]{TblsData.Sample.SPEC_CODE.getName(), TblsData.Sample.SPEC_CODE_VERSION.getName(),
                    TblsData.Sample.SPEC_VARIATION_NAME.getName()};

                String[] specMissingFields = new String[0];
                for (String curValue : specFields) {
                    Integer posicField = LPArray.valuePosicInArray(sampleFieldName, curValue);
                    if (posicField == -1) {
                        specMissingFields = LPArray.addValueToArray1D(specMissingFields, curValue);
                    } else {
                        specFieldsValues = LPArray.addValueToArray1D(specFieldsValues, sampleFieldValue[posicField]);
                    }
                }
                if (specMissingFields.length > 0) {
                    Object[][] sampleSpecInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(),
                            new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, specMissingFields);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleSpecInfo[0][0].toString())) {
                        return LPArray.array2dTo1d(sampleSpecInfo);
                    }
                }
                String[] specFlds = new String[]{
                    TblsCnfg.SpecLimits.VARIATION_NAME.getName(), TblsCnfg.SpecLimits.ANALYSIS.getName(),
                    TblsCnfg.SpecLimits.METHOD_NAME.getName(), TblsCnfg.SpecLimits.METHOD_VERSION.getName(), TblsInspLotRMConfig.SpecLimits.TESTING_GROUP.getName(),
                    TblsCnfg.SpecLimits.LIMIT_ID.getName(), TblsCnfg.SpecLimits.PARAMETER.getName()};

                specLimitsInfo = SpecFrontEndUtilities.configSpecLimitsInfo(ProcedureRequestSession.getInstanceForActions(null, null, null), TblsInspLotRMConfig.TablesInspLotRMConfig.SPEC_LIMITS, specFieldsValues[0].toString(), Integer.valueOf(specFieldsValues[1].toString()),
                        specFieldsValues[2].toString(), specFlds, new String[]{TblsInspLotRMConfig.SpecLimits.COA_ORDER.getName(), TblsInspLotRMConfig.SpecLimits.ANALYSIS.getName()});
                break;
            case SPEC:
            default:
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "autoSampleAnalysisAdd_caseNotDetected", new String[]{autoAddAnalysisLevel + " not implemented yet."});
        }
        try {
            StringBuilder analysisAdded = new StringBuilder(0);
            for (Iterator it = specLimitsInfo.iterator(); it.hasNext();) {
                JSONObject jLotInfoObj = (JSONObject) it.next();
                String[] fieldsName = new String[]{TblsData.SampleAnalysis.ANALYSIS.getName(), TblsData.SampleAnalysis.METHOD_NAME.getName(), TblsData.SampleAnalysis.METHOD_VERSION.getName(), TblsData.SampleAnalysis.TESTING_GROUP.getName()};
                Object[] fieldsValue = new Object[]{(String) jLotInfoObj.get(TblsData.SampleAnalysis.ANALYSIS.getName()),
                    (String) jLotInfoObj.get(TblsData.SampleAnalysis.METHOD_NAME.getName()), (Integer) jLotInfoObj.get(TblsData.SampleAnalysis.METHOD_VERSION.getName()),
                    (String) jLotInfoObj.get(TblsData.SampleAnalysis.TESTING_GROUP.getName())};
                Object[] sampleAnalysisAddtoSample = DataSampleAnalysis.addSampleAnalysisWithResults(sampleId, fieldsName, fieldsValue,
                        specFieldsValues[0].toString(), Integer.valueOf(specFieldsValues[1].toString()), jLotInfoObj.get(TblsCnfg.SpecLimits.PARAMETER.getName()).toString(),
                        (Integer) jLotInfoObj.get(TblsCnfg.SpecLimits.LIMIT_ID.getName()), (String) jLotInfoObj.get(TblsData.SampleAnalysis.TESTING_GROUP.getName()));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAnalysisAddtoSample[0].toString())) {
                    return sampleAnalysisAddtoSample;
                }
                analysisAdded.append(jLotInfoObj.get(TblsData.SampleAnalysis.ANALYSIS.getName()));
            }
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "autoSampleAnalysisAdded_success", new String[]{"Added analysis " + analysisAdded.toString() + " to the sample " + sampleId.toString() + " for schema " + procInstanceName});
        }catch(Exception e){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, e.getMessage(), new Object[]{e.getMessage()});        
        }
    }

        @Override
        public String specialFieldCheckSampleAnalysisAnalyst
        (String template, Integer templateVersion
        , DataSample dataSample
        
            ) {
        return "";

        }

    }
