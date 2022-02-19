/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.instruments;

import databases.Rdbms;
import databases.TblsCnfg;
import databases.TblsData;
import functionaljavaa.samplestructure.DataSample;
import functionaljavaa.samplestructure.DataSampleAnalysisStrategy;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
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
  public Object[] autoSampleAnalysisAdd(Integer sampleId, String[] sampleFieldName, Object[] sampleFieldValue) {
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
            String[] fieldsName = new String[]{TblsData.SampleAnalysis.FLD_ANALYSIS.getName(), TblsData.SampleAnalysis.FLD_METHOD_NAME.getName(), TblsData.SampleAnalysis.FLD_METHOD_VERSION.getName()};
            Object[] fieldsValue = new Object[]{(String) anaName1[0], (String) anaName1[1], (Integer) anaName1[2]};
            functionaljavaa.samplestructure.DataSampleAnalysis.sampleAnalysisAddtoSample(sampleId, fieldsName, fieldsValue);
            analysisAdded.append(LPArray.convertArrayToString(anaName1, ",", ""));
        }        
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "autoSampleAnalysisAdded_success", new String[]{analysisAdded.toString(), sampleId.toString(), procInstanceName});        
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
        
        Object[] diagnosis = Rdbms.existsRecord(schemaConfigName, TblsCnfg.SampleRules.TBL.getName(), 
                new String[]{TblsCnfg.SampleRules.FLD_CODE.getName(), TblsCnfg.SampleRules.FLD_CODE_VERSION.getName()}, new Object[]{template, templateVersion});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())) 
            return "ERROR: The sample_rule record for " + template + " does not exist in schema" + schemaConfigName + ". ERROR: " + diagnosis[5];
        
        String[] fieldNames = new String[1];
        Object[] fieldValues = new Object[1];
        fieldNames[0] = TblsCnfg.SampleRules.FLD_CODE.getName();
        fieldValues[0] = template;
        String[] fieldFilter = new String[]{TblsCnfg.SampleRules.FLD_CODE.getName(), TblsCnfg.SampleRules.FLD_CODE_VERSION.getName(), 
            TblsCnfg.SampleRules.FLD_STATUSES.getName(), TblsCnfg.SampleRules.FLD_DEFAULT_STATUS.getName()};
        Object[][] records = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.SampleRules.TBL.getName(), 
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
