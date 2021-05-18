/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import functionaljavaa.parameter.Parameter;
import functionaljavaa.samplestructure.DataSampleAnalysisResultStrategy;
import lbplanet.utilities.LPArray;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class DataProgramSampleAnalysisResult implements DataSampleAnalysisResultStrategy{

    /**
     *
     * @param resultId
     * @param sampleFieldName
     * @param sampleFieldValue
     * @param sarFieldName
     * @param sarFieldValue
     * @return
     */
    @Override
  public Object[] sarControlAction(Integer resultId, String[] sampleFieldName, Object[] sampleFieldValue, String[] sarFieldName, Object[] sarFieldValue) {
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    String sampleActionWhenUponControlMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultStrategyBusinessRules.SAMPLE_ACTION_WHENUPONCONTROL_MODE.getAreaName(), DataSampleAnalysisResultStrategyBusinessRules.SAMPLE_ACTION_WHENUPONCONTROL_MODE.getTagName());
    if (LPArray.valuePosicInArray(SAMPLEACTIONWHENUPONCONTROLMODEENABLINGSTATUSES.split("\\|"), sampleActionWhenUponControlMode)==-1)
        return new Object[0];
    return DataProgramCorrectiveAction.createNew(resultId, sampleFieldName, sampleFieldValue,sarFieldName, sarFieldValue);
  }

    /**
     *
     * @param resultId
     * @param sampleFieldName
     * @param sampleFieldValue
     * @param sarFieldName
     * @param sarFieldValue
     * @return
     */
    @Override
  public Object[] sarOOSAction(Integer resultId, String[] sampleFieldName, Object[] sampleFieldValue, String[] sarFieldName, Object[] sarFieldValue) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

      String sampleActionWhenOOSMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultStrategy.DataSampleAnalysisResultStrategyBusinessRules.SAMPLE_ACTION_WHENOOS_MODE.getAreaName(), DataSampleAnalysisResultStrategyBusinessRules.SAMPLE_ACTION_WHENOOS_MODE.getTagName());
      if (LPArray.valuePosicInArray(SAMPLEACTIONWHENUPONOOSMODEENABLINGSTATUSES.split("\\|"), sampleActionWhenOOSMode)==-1)
          return new Object[0];
      return DataProgramCorrectiveAction.createNew(resultId, sampleFieldName, sampleFieldValue,sarFieldName, sarFieldValue);
  }
}
