/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

/**
 *
 * @author Administrator
 */
public interface DataSampleAnalysisStrategy {

    /**
     *
     * @param sampleId
     * @param sampleFieldName
     * @param sampleFieldValue
     * @return
     */
    public abstract Object[] autoSampleAnalysisAdd(Integer sampleId, String[] sampleFieldName, Object[] sampleFieldValue);

    /**
     *
     * @param template
     * @param templateVersion
     * @param dataSample
     * @return
     */
    public abstract String specialFieldCheckSampleAnalysisAnalyst(String template, Integer templateVersion, DataSample dataSample);
  
}
