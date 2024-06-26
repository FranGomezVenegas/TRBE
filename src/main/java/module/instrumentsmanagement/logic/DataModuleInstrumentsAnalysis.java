/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.instrumentsmanagement.logic;

import databases.TblsData;
import functionaljavaa.samplestructure.DataSample;
import functionaljavaa.samplestructure.DataSampleAnalysisStrategy;
import functionaljavaa.samplestructure.DataSampleStructureEnums;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
/**
 *
 * @author Administrator
 */
public class DataModuleInstrumentsAnalysis implements DataSampleAnalysisStrategy{
    String[] mandatoryFields = null;
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
        Object[][] anaName =new Object[2][3];
                anaName[0][0] = "pH";
                anaName[0][1] = "pH method";
                anaName[0][2] = 1;
                anaName[1][0] = "LOD";
                anaName[1][1] = "LOD Method";
                anaName[1][2] = 1;                    
        StringBuilder analysisAdded = new StringBuilder(0);
        for (Object[] anaName1 : anaName) {
            String[] fieldsName = new String[]{TblsData.SampleAnalysis.ANALYSIS.getName(), TblsData.SampleAnalysis.METHOD_NAME.getName(), TblsData.SampleAnalysis.METHOD_VERSION.getName()};
            Object[] fieldsValue = new Object[]{(String) anaName1[0], (String) anaName1[1], (Integer) anaName1[2]};
            functionaljavaa.samplestructure.DataSampleAnalysis.sampleAnalysisAddtoSample(sampleId, fieldsName, fieldsValue);
            analysisAdded.append(LPArray.convertArrayToString(anaName1, ",", ""));
        }        
        return new InternalMessage(LPPlatform.LAB_TRUE, DataSampleStructureEnums.DataSampleStructureSuccess.AUTOSAMPLEANALYSIS_ADDED_SUCCESS, new String[]{"Added analysis " + analysisAdded.toString() + " to the sample " + sampleId.toString() + " for schema " + procInstanceName});
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
    public InternalMessage specialFieldCheckSampleAnalysisAnalyst(String template, Integer templateVersion, DataSample dataSample) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String schemaConfigName = GlobalVariables.Schemas.CONFIG.getName();
        schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, schemaConfigName);
        return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
/*if (1 == 1) 
            return"ERROR: specialFieldCheckSampleAnalysisAnalyst not implemented yet.";
        
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
    }

  */
    }
    @Override
    public InternalMessage calcsPostEnterResult(Integer resultId, Integer testId, Integer sampleId, DataSample dataSample) {
        return new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, null);
    }
    
}
