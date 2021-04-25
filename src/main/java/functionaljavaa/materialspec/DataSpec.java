/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.materialspec;

import functionaljavaa.materialspec.ConfigSpecRule.qualitativeRules;
import functionaljavaa.materialspec.ConfigSpecRule.qualitativeRulesErrors;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 *
 * @author Administrator
 */
public class DataSpec {
    String classVersion = "0.1";
    
    public enum ResultCheckErrorsErrorTrapping{ 
        STRICT_DOES_NOT_ALLOW_EQUALS("resultCheck_StrictDoesNotAllowPairOfSameValue", "", ""), 
        NULL_MANDATORY_FIELD("resultCheck_mandatoryFieldIsNull", "", ""),
        MANDATORY_FIELD_ARGUMENT_RESULT("Result", "", ""), 
        MANDATORY_FIELD_ARGUMENT_SPEC_RULE("specRule", "", ""),
        MANDATORY_FIELD_ARGUMENT_VALUES("values", "", ""), 
        MANDATORY_FIELD_ARGUMENT_SEPARATOR("Separator", "", ""),
        QUANT_OUT_MIN_CONTROL_IN_SPEC("resultCheck_quantitativeOutMinControlInSpec", "", ""),
        EVALUATION_WRONG_RULE("OUT_WRONG_RULE", "", ""),
        UNHANDLED_EXCEPTION("DataSpec_resultCheck_UnhandledException", "", ""),
        ;
        private ResultCheckErrorsErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        public String getErrorCode(){return this.errorCode;}
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    public enum ResultCheckSuccessErrorTrapping{ 
        QUANT_OUT_MIN_CONTROL_IN_SPEC("resultCheck_quantitativeOutMinControlInSpec", "", ""),
        EVALUATION_IN("IN", "", ""), 
        QUANTITATIVE_IN("DataSpec_resultCheck_quantitativeIN", "", ""), QUALITATIVE_IN("DataSpec_resultCheck_qualitativeIN", "", ""),
        QUANTITATIVE_IN_SPEC_BUT_OUT_MAX_CONTROL("resultCheck_quantitativeOutMaxControlInSpec", "", ""),
        QUANTITATIVE_IN_SPEC_BUT_OUT_MIN_CONTROL("resultCheck_quantitativeOutMinControlInSpec", "", ""),
        EVALUATION_OUT("OUT", "", ""),
        QUALITATIVE_OUT_EQUAL_TO("DataSpec_resultCheck_qualitativeEqualToOUT", "", ""), 
        QUALITATIVE_OUT_NOT_EQUAL_TO("DataSpec_resultCheck_qualitativeNotEqualToOUT", "", ""),
        QUALITATIVE_OUT_CONTAINS("DataSpec_resultCheck_qualitativeContainsOUT", "", ""), 
        QUALITATIVE_OUT_NOT_CONTAINS("DataSpec_resultCheck_qualitativeNotContainsOUT", "", ""),
        QUALITATIVE_OUT_IS_ONE_OF("DataSpec_resultCheck_qualitativeIsOneOfOUT", "", ""), 
        QUALITATIVE_OUT_IS_NOT_ONE_OF("DataSpec_resultCheck_qualitativeIsNotOneOfOUT", "", ""),
        QUANTITATIVE_OUT_SPEC_BY_MIN_STRICT("resultCheck_quantitativeOutSpecByMinStrict", "", ""), 
        QUANTITATIVE_OUT_SPEC_BY_MIN("resultCheck_quantitativeOutSpecByMin", "", ""),
        QUANTITATIVE_OUT_SPEC_BY_MAX_STRICT("resultCheck_quantitativeOutSpecByMaxStrict", "", ""), 
        QUANTITATIVE_OUT_SPEC_BY_MAX("resultCheck_quantitativeOutSpecByMax", "", ""),
        OUT_SPEC_MIN("OUT_SPEC_MIN", "", ""), OUT_SPEC_MAX("OUT_SPEC_MAX", "", ""),
        QUANTITATIVE_OUT_ARGUMENT_MIN_SPEC_MIN_CONTROL("Min Spec and Min Control", "", ""), 
        QUANTITATIVE_OUT_ARGUMENT_MAX_SPEC_MAX_CONTROL("Max Spec and Control", "", ""),                
        EVALUATION_UPON_CONTROL_MIN("UPON_CONTROL_MIN", "", ""), 
        EVALUATION_UPON_CONTROL_MAX("UPON_CONTROL_MAX", "", ""),
        ;
        private ResultCheckSuccessErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        public String getErrorCode(){return this.errorCode;}
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
                

    
   
/**
 * There are some behaviors for interpret what the spec limits means.<br>
 * Case 1: The most restrictive one is the one where the spec is the mechanism to decide which are the analysis that should be added to the samples
 * and it means that it is expected that the sample contains all the analysis defined in its spec, nothing more and nothing less (SPEC_LIMIT_DEFINITION).<BR>
 * Case 2: Other analysis than the ones added to the spec are not allowed but not all of them should be present at the spec limit level due to many reasons like
 * , for example, simply there are no ranges defined yet for those analysis or even they are just analysis to reinforce the result or testing for internal purposes
 * (ANALYSES_SPEC_LIST).<br>
 * Case 3. Analysis has not to be declared in any level of the spec, let any analysis be added to the sample (OPEN)<br>
 * Then the three cases that this method cover are: OPEN|ANALYSES_SPEC_LIST|SPEC_LIMIT_DEFINITION 
 * LABPLANET_TRUE means the result can be checked against the rule even when the check returns one Out result
 * LABPLANET_FALSE means the evaluation cannot be performed due to any deficiency<br>
 * @return 
 */
    public Object[] specAllowSampleAnalysisAddition(){
        Object[] diagnoses = new Object[2];
        diagnoses[0]=false;
        return diagnoses;
    }

    /**
     *
     * @param result
     * @param specRule
     * @param values
     * @param separator
     * @param listName
     * @return
     */
    public Object[] resultCheck(String result, String specRule, String values, String separator, String listName){
        ConfigSpecRule matQualit = new ConfigSpecRule();
        Object [] errorVariables = new Object[0];        

        String errorCode = "";        
        if (result==null || "".equals(result)){
            errorVariables = LPArray.addValueToArray1D(errorVariables, ResultCheckErrorsErrorTrapping.MANDATORY_FIELD_ARGUMENT_RESULT.getErrorCode());
            Object[] diagnoses =  LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.NULL_MANDATORY_FIELD.getErrorCode(), errorVariables);
            diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE.getErrorCode());
            return diagnoses;}               
        if (specRule==null || "".equals(specRule)){
            errorVariables = LPArray.addValueToArray1D(errorVariables, ResultCheckErrorsErrorTrapping.MANDATORY_FIELD_ARGUMENT_SPEC_RULE.getErrorCode());            
            Object[] diagnoses =  LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.NULL_MANDATORY_FIELD.getErrorCode(), errorVariables);
            diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE.getErrorCode());
            return diagnoses;}        
        if (values==null || "".equals(values)){
            errorVariables = LPArray.addValueToArray1D(errorVariables, ResultCheckErrorsErrorTrapping.MANDATORY_FIELD_ARGUMENT_VALUES.getErrorCode());            
            Object[] diagnoses =  LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.NULL_MANDATORY_FIELD.getErrorCode(), errorVariables);
            diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE.getErrorCode());
            return diagnoses;}        
        
        Object[] isCorrectTheSpec = matQualit.specLimitIsCorrectQualitative( specRule, values, separator);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isCorrectTheSpec[0].toString())){
            return isCorrectTheSpec;}
        qualitativeRules qualitRule = null;
        try{
            qualitRule = qualitativeRules.valueOf(specRule.toUpperCase());
        }catch(Exception e){            
            String[] errorDetailVariables=new String[]{specRule, Arrays.toString(qualitativeRules.getAllRules())};
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, qualitativeRulesErrors.QUALITATIVE_RULE_NOT_RECOGNIZED.getErrorCode(), errorDetailVariables);             
        }

        switch (qualitRule){
            case EQUALTO: 
                if (result.equalsIgnoreCase(values)){
                    errorCode = ResultCheckSuccessErrorTrapping.QUALITATIVE_IN.getErrorCode();
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, errorCode, null);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_IN.getErrorCode());
                    return diagnoses;                    
                }else{
                    errorVariables = new Object[]{result, values};
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUALITATIVE_OUT_EQUAL_TO.getErrorCode(), errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_OUT.getErrorCode());
                    return diagnoses;                                                           
                }                
            case NOTEQUALTO: 
                if (result.equalsIgnoreCase(values)){ 
                    errorVariables = new Object[]{result, values};
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUALITATIVE_OUT_NOT_EQUAL_TO.getErrorCode(), errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_OUT.getErrorCode());
                    return diagnoses;                                                                               
                }else{                    
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUALITATIVE_IN.getErrorCode(), null);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_IN.getErrorCode());
                    return diagnoses;                    
                }
            case CONTAINS:                 
                if (values.toUpperCase().contains(result.toUpperCase())){
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUALITATIVE_IN.getErrorCode(), null);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_IN.getErrorCode());
                    return diagnoses;                    
                }else{                    
                    errorVariables = new Object[]{result, values};
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUALITATIVE_OUT_CONTAINS.getErrorCode(), errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_OUT.getErrorCode());
                    return diagnoses;                         
                }
            case NOTCONTAINS:                 
                if (values.toUpperCase().contains(result.toUpperCase())){
                    errorVariables = new Object[]{result, values};
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUALITATIVE_OUT_NOT_CONTAINS.getErrorCode(), errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_OUT.getErrorCode());
                    return diagnoses;                                             
                }else{
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUALITATIVE_IN.getErrorCode(), null);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_IN.getErrorCode());
                    return diagnoses;                    
                }
            case ISONEOF: 
                if ((separator==null) || (separator.length()==0)){
                    errorVariables = LPArray.addValueToArray1D(errorVariables, ResultCheckErrorsErrorTrapping.MANDATORY_FIELD_ARGUMENT_SEPARATOR.getErrorCode());
                    Object[] diagnoses =  LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.NULL_MANDATORY_FIELD.getErrorCode(), errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE.getErrorCode());
                    return diagnoses;
                }else{
                    String[] textSpecArray = values.split(separator);
                    for (Integer itextSpecArrayLen=0;itextSpecArrayLen<textSpecArray.length;itextSpecArrayLen++){
                        if (result.equalsIgnoreCase(textSpecArray[itextSpecArrayLen])){                            
                            Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUALITATIVE_IN.getErrorCode(), null);
                            diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_IN.getErrorCode());
                            return diagnoses;                    
                        }
                    }                
                    errorVariables = new Object[]{result, String.valueOf((Integer)textSpecArray.length+1), values};
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUALITATIVE_OUT_IS_ONE_OF.getErrorCode(), errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_OUT.getErrorCode());
                    return diagnoses;                                                                 
                }
            case ISNOTONEOF: 
                if ((separator==null) || (separator.length()==0)){                    
                    errorVariables = LPArray.addValueToArray1D(errorVariables, ResultCheckErrorsErrorTrapping.MANDATORY_FIELD_ARGUMENT_SEPARATOR.getErrorCode());
                    Object[] diagnoses =  LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.NULL_MANDATORY_FIELD.getErrorCode(), errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE.getErrorCode());
                    return diagnoses;
                }else{
                    String[] textSpecArray = values.split(separator);
                    if (!LPArray.valueInArray(textSpecArray, result)){
                        errorVariables = new Object[]{result, String.valueOf((Integer)textSpecArray.length+1), values};                        
                        Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUALITATIVE_OUT_IS_NOT_ONE_OF.getErrorCode(), errorVariables);
                        diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_OUT.getErrorCode());
                        return diagnoses;
                    }
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUALITATIVE_IN.getErrorCode(), null);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_IN.getErrorCode());
                    return diagnoses;                    
                }
            default:                
                String params = "Result: "+result+", Spec Rule: "+specRule+", values: "+values+", separator: "+separator+", listName: "+listName;
                errorVariables = LPArray.addValueToArray1D(errorVariables, params);
                Object[] diagnoses =  LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.UNHANDLED_EXCEPTION.getErrorCode(), errorVariables);
                diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE.getErrorCode());
                return diagnoses;               
        }
    }
    /**
     *
     * @param result
     * @param minSpec
     * @param maxSpec
     * @param minStrict
     * @param maxStrict
     * @return
     */
    public Object[] resultCheck(BigDecimal result, BigDecimal minSpec, BigDecimal maxSpec, Boolean minStrict, Boolean maxStrict){
        
        ConfigSpecRule matQuant = new ConfigSpecRule();

        Object [] errorVariables = new Object[0];        
        
        if (result==null){
            String errorCode = ResultCheckErrorsErrorTrapping.NULL_MANDATORY_FIELD.getErrorCode();
            errorVariables = LPArray.addValueToArray1D(errorVariables, ResultCheckErrorsErrorTrapping.MANDATORY_FIELD_ARGUMENT_RESULT.getErrorCode());
            Object[] diagnoses =  LPPlatform.trapMessage(LPPlatform.LAB_FALSE, errorCode, errorVariables);
            diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE.getErrorCode());
            return diagnoses;
        }
        Object[] isCorrectMinMaxSpec = matQuant.specLimitIsCorrectQuantitative(minSpec, maxSpec);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isCorrectMinMaxSpec[0].toString())){
            return isCorrectMinMaxSpec;}
                
        if (minStrict==null){minStrict=true;}
        if (maxStrict==null){maxStrict=true;}

        if (minSpec!=null){  
            int comparingMIN = minSpec.compareTo(result);
            if (minStrict){
                if ( comparingMIN>-1) {
                        errorVariables = new Object[]{result.toString(), minSpec.toString()}; 
                        Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUANTITATIVE_OUT_SPEC_BY_MIN_STRICT.getErrorCode(), errorVariables);
                        diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.OUT_SPEC_MIN.getErrorCode());
                        return diagnoses;                
                }
            }else{
                if (comparingMIN>0) {
                        errorVariables = new Object[]{result.toString(), minSpec.toString()}; 
                        Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUANTITATIVE_OUT_SPEC_BY_MIN.getErrorCode(), errorVariables);
                        diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.OUT_SPEC_MIN.getErrorCode());
                        return diagnoses;                
                }                
            }
        }                    
        if (maxSpec!=null){  
            int comparingMAX = result.compareTo(maxSpec);
            if (maxStrict){
                if (comparingMAX>-1) {
                        errorVariables = new Object[]{result.toString(), maxSpec.toString()}; 
                        Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUANTITATIVE_OUT_SPEC_BY_MAX_STRICT.getErrorCode(), errorVariables);
                        diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.OUT_SPEC_MAX.getErrorCode());
                        return diagnoses;            
                }
            }else{
                if (comparingMAX>0) {
                        errorVariables = new Object[]{result.toString(), maxSpec.toString()}; 
                        Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUANTITATIVE_OUT_SPEC_BY_MAX.getErrorCode(), errorVariables);
                        diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.OUT_SPEC_MAX.getErrorCode());
                        return diagnoses;            
                }
            }
        }    
        String errorCode = ResultCheckSuccessErrorTrapping.QUANTITATIVE_IN.getErrorCode();
        Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, errorCode, null);
        diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_IN.getErrorCode());
        return diagnoses;        
    }
    
    /**
     *
     * @param result
     * @param minSpec
     * @param maxSpec
     * @param minStrict
     * @param maxStrict
     * @param minControl
     * @param maxControl
     * @param minControlStrict
     * @param maxControlStrict
     * @return
     */
    public Object[] resultCheck(BigDecimal result, BigDecimal minSpec, BigDecimal maxSpec, Boolean minStrict, Boolean maxStrict, BigDecimal minControl, BigDecimal maxControl, Boolean minControlStrict, Boolean maxControlStrict){
        
        Object [] errorVariables = new Object[0]; 

        if (result==null){
                errorVariables = LPArray.addValueToArray1D(errorVariables, ResultCheckErrorsErrorTrapping.MANDATORY_FIELD_ARGUMENT_RESULT.getErrorCode());
                Object[] diagnoses =  LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.NULL_MANDATORY_FIELD.getErrorCode(), errorVariables);
                diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE.getErrorCode());
                return diagnoses;
        }
        
        Object[] isCorrectMinMaxSpec = this.resultCheck(result,minSpec,maxSpec, minStrict, maxStrict);
        
        if (!"IN".equalsIgnoreCase(isCorrectMinMaxSpec[isCorrectMinMaxSpec.length-1].toString())){
            return isCorrectMinMaxSpec;
        }

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isCorrectMinMaxSpec[0].toString())){
            return isCorrectMinMaxSpec;
        }

        if (minControl!=null){
            if (minControl.equals(minSpec)) {                
                if (!minStrict || minStrict==null){
                    errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{ResultCheckSuccessErrorTrapping.QUANTITATIVE_OUT_ARGUMENT_MIN_SPEC_MIN_CONTROL.getErrorCode(), minSpec, "Min Strict  is set to false."});
                    Object[] diagnoses =  LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckErrorsErrorTrapping.STRICT_DOES_NOT_ALLOW_EQUALS.getErrorCode(), errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE.getErrorCode());
                    return diagnoses;
                }

                if (minStrict && minControlStrict){
                    errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{ResultCheckSuccessErrorTrapping.QUANTITATIVE_OUT_ARGUMENT_MIN_SPEC_MIN_CONTROL.getErrorCode(), minSpec, "both, min Spec & Control Strict, set to true"});
                    Object[] diagnoses =  LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckErrorsErrorTrapping.STRICT_DOES_NOT_ALLOW_EQUALS.getErrorCode(), errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE.getErrorCode());
                    return diagnoses;                   
                }                    
            }    
    
            if (minControlStrict==null){minControlStrict=true;}
            
            int comparingMIN = minControl.compareTo(result);
            if ( (comparingMIN==1) || (comparingMIN==0 && minControlStrict) ) {
                    errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{minSpec.toString(), " < "+result.toString()+" < ", minControl});
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckErrorsErrorTrapping.QUANT_OUT_MIN_CONTROL_IN_SPEC.getErrorCode(), errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_UPON_CONTROL_MIN.getErrorCode());
                    return diagnoses;                    
            }                      
        }
        if (minControl!=null){    
            int comparingMIN = result.compareTo(minControl);
            if (minControlStrict){
                if ( comparingMIN<1) {
                    errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{minSpec.toString(), " > "+result.toString()+" > ", minSpec.toString()});
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUANT_OUT_MIN_CONTROL_IN_SPEC.getErrorCode(), errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_UPON_CONTROL_MIN.getErrorCode());
                    return diagnoses;                                         
                }                    
            }else{
                if (comparingMIN<0) {
                    errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{minSpec.toString(), " > "+result.toString()+" > ", minSpec});
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUANTITATIVE_IN_SPEC_BUT_OUT_MAX_CONTROL.getErrorCode(), errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_UPON_CONTROL_MAX.getErrorCode());
                    return diagnoses;                                         
                }                                    
            }
        }

        if (maxControl!=null){            
            if ( (maxControl.equals(maxSpec)) && (!maxStrict || maxStrict==null) ) {
                errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{ResultCheckSuccessErrorTrapping.QUANTITATIVE_OUT_ARGUMENT_MAX_SPEC_MAX_CONTROL.getErrorCode(), maxSpec, "max Strict is set to false."});
                Object[] diagnoses =  LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckErrorsErrorTrapping.STRICT_DOES_NOT_ALLOW_EQUALS.getErrorCode(), errorVariables);
                diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE.getErrorCode());
                return diagnoses;                    
            }
            if ( (maxControl.equals(maxSpec)) && (maxStrict && maxControlStrict) ){
                errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{ResultCheckSuccessErrorTrapping.QUANTITATIVE_OUT_ARGUMENT_MAX_SPEC_MAX_CONTROL.getErrorCode(), maxSpec, "both, max Spec & Control Strict, set to true.."});
                Object[] diagnoses =  LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckErrorsErrorTrapping.STRICT_DOES_NOT_ALLOW_EQUALS.getErrorCode(), errorVariables);
                diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE.getErrorCode());
                return diagnoses;                    
            }                    
        }
        if (maxControl!=null){
            int comparingMAX = result.compareTo(maxControl);
            if (maxControlStrict){
                if (comparingMAX>-1) {
                    errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{maxControl.toString(), " > "+result.toString()+" > ", maxSpec});
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUANTITATIVE_IN_SPEC_BUT_OUT_MAX_CONTROL.getErrorCode(), errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_UPON_CONTROL_MAX.getErrorCode());
                    return diagnoses;                                         
                }                            
            }else{
                if (comparingMAX>0) {
                    errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{maxControl.toString(), " > "+result.toString()+" > ", maxSpec});
                    Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUANTITATIVE_IN_SPEC_BUT_OUT_MAX_CONTROL.getErrorCode(), errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_UPON_CONTROL_MAX.getErrorCode());
                    return diagnoses;                                         
                }                                            
            }
        }
        Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ResultCheckSuccessErrorTrapping.QUANTITATIVE_IN.getErrorCode(), null);
        diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.EVALUATION_IN.getErrorCode());
        return diagnoses;            
    }
}
