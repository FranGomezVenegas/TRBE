/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public interface DataSampleAnalysisResultStrategy {

    public enum DataSampleAnalysisResultStrategyBusinessRules  implements EnumIntBusinessRules{
        SAMPLE_ACTION_WHENUPONCONTROL_MODE("sampleActionWhenUponControlMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        SAMPLE_ACTION_WHENOOS_MODE("sampleActionWhenOOSMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null)
        
        ;
        private DataSampleAnalysisResultStrategyBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator
        , Boolean isOpt, ArrayList<String[]> preReqs){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
            this.isOptional=isOpt;
            this.preReqs=preReqs;
        }       
        @Override        public String getTagName(){return this.tagName;}
        @Override        public String getAreaName(){return this.areaName;}
        @Override        public JSONArray getValuesList(){return this.valuesList;}
        @Override        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        @Override        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        @Override        public Boolean getIsOptional() {return isOptional;}
        @Override        public ArrayList<String[]> getPreReqs() {return this.preReqs;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        
        private final Boolean isOptional;
        private final ArrayList<String[]> preReqs;
    }
    /**
     *
     */
    String SAMPLEACTIONWHENUPONCONTROLMODEENABLINGSTATUSES="ENABLE|ENABLED|YES|SI";

    /**
     *
     */
    String SAMPLEACTIONWHENUPONOOSMODEENABLINGSTATUSES="ENABLE|ENABLED|YES|SI";

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
