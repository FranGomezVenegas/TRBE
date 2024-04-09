/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulesample;

import functionaljavaa.parameter.Parameter;
import functionaljavaa.samplestructure.DataSampleAnalysisResultStrategy;
import lbplanet.utilities.LPArray;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class DataModuleSampleAnalysisResult implements DataSampleAnalysisResultStrategy{
    
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
  public InternalMessage sarControlAction(Integer resultId, String[] sampleFieldName, Object[] sampleFieldValue, String[] sarFieldName, Object[] sarFieldValue) {
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    String sampleActionWhenUponControlMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultStrategyBusinessRules.SAMPLE_ACTION_WHENUPONCONTROL_MODE.getAreaName(), DataSampleAnalysisResultStrategyBusinessRules.SAMPLE_ACTION_WHENUPONCONTROL_MODE.getTagName());
    if (LPArray.valuePosicInArray(SAMPLEACTIONWHENUPONCONTROLMODEENABLINGSTATUSES.split("\\|"), sampleActionWhenUponControlMode)==-1)
        return null;
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
  public InternalMessage sarOOSAction(Integer resultId, String[] sampleFieldName, Object[] sampleFieldValue, String[] sarFieldName, Object[] sarFieldValue) {
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

      String sampleActionWhenOOSMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleAnalysisResultStrategy.DataSampleAnalysisResultStrategyBusinessRules.SAMPLE_ACTION_WHENOOS_MODE.getAreaName(), DataSampleAnalysisResultStrategyBusinessRules.SAMPLE_ACTION_WHENOOS_MODE.getTagName());
      if (LPArray.valuePosicInArray(SAMPLEACTIONWHENUPONOOSMODEENABLINGSTATUSES.split("\\|"), sampleActionWhenOOSMode)==-1)
          return null;
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.      
  }




}
