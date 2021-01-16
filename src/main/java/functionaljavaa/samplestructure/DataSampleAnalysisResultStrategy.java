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
public interface DataSampleAnalysisResultStrategy {

    /**
     *
     */
    String SAMPLEACTIONWHENUPONCONTROLMODEENABLINGSTATUSES="ENABLE";

    /**
     *
     */
    String SAMPLEACTIONWHENUPONOOSMODEENABLINGSTATUSES="ENABLE";

    /**
     *
     * @param resultId
     * @param sampleFieldName
     * @param sampleFieldValue
     * @param sarFieldName
     * @param sarFieldValue
     * @return
     */
    public abstract Object[] sarControlAction(Integer resultId, String[] sampleFieldName, Object[] sampleFieldValue, String[] sarFieldName, Object[] sarFieldValue);

    /**
     *
     * @param resultId
     * @param sampleFieldName
     * @param sampleFieldValue
     * @param sarFieldName
     * @param sarFieldValue
     * @return
     */
    public abstract Object[] sarOOSAction(Integer resultId, String[] sampleFieldName, Object[] sampleFieldValue, String[] sarFieldName, Object[] sarFieldValue);
}
