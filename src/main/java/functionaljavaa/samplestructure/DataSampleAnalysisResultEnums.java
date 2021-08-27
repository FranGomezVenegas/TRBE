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
 * @author User
 */
public class DataSampleAnalysisResultEnums {
    
    public enum DataSampleAnalysisResultBusinessRules{        
        STATUS_FIRST("sampleAnalysisResult_statusFirst", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_ENTERED("sampleAnalysisResult_statusEntered", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_REENTERED("sampleAnalysisResult_statusReEntered", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_REVIEWED("sampleAnalysisResult_statusReviewed", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_CANCELED("sampleAnalysisResult_statusCanceled", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_SPEC_EVAL_NOSPEC("sampleAnalysisResult_statusSpecEvalNoSpec", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_EVAL_NOSPECPARAMLIMIT("sampleAnalysisResult_statusSpecEvalNoSpecParamLimit", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
         
        ;
        private DataSampleAnalysisResultBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
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
    
}
