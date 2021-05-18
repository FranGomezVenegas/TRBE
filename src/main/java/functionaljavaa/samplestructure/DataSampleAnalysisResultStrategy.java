/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public interface DataSampleAnalysisResultStrategy {

    public enum DataSampleAnalysisResultStrategyBusinessRules{
        SAMPLE_ACTION_WHENUPONCONTROL_MODE("sampleActionWhenUponControlMode", GlobalVariables.Schemas.PROCEDURE.getName()),
        SAMPLE_ACTION_WHENOOS_MODE("sampleActionWhenOOSMode", GlobalVariables.Schemas.PROCEDURE.getName())
        
        ;
        private DataSampleAnalysisResultStrategyBusinessRules(String tgName, String areaNm){
            this.tagName=tgName;
            this.areaName=areaNm;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        
        private final String tagName;
        private final String areaName;
    }
    
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
