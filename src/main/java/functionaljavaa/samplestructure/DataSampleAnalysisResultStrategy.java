/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import org.json.simple.JSONArray;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public interface DataSampleAnalysisResultStrategy {

    public enum DataSampleAnalysisResultStrategyBusinessRules{
        SAMPLE_ACTION_WHENUPONCONTROL_MODE("sampleActionWhenUponControlMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        SAMPLE_ACTION_WHENOOS_MODE("sampleActionWhenOOSMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|')
        
        ;
        private DataSampleAnalysisResultStrategyBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        public JSONArray getValuesList(){return this.valuesList;}
        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        
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
