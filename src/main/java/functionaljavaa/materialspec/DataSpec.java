/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.materialspec;

import functionaljavaa.materialspec.ConfigSpecRule.qualitativeRules;
import functionaljavaa.materialspec.ConfigSpecRule.qualitativeRulesErrors;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleStructureSuccess;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import java.math.BigDecimal;
import java.util.Arrays;
import trazit.enums.EnumIntMessages;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;
import trazit.session.ResponseMessages;

/**
 *
 * @author Administrator
 */
public class DataSpec {
    String classVersion = "0.1";
    
    public enum ResultCheckErrorsErrorTrapping implements EnumIntMessages{ 
        STRICT_DOES_NOT_ALLOW_EQUALS("resultCheck_StrictDoesNotAllowPairOfSameValue", "", ""), 
        NULL_MANDATORY_FIELD("resultCheck_mandatoryFieldIsNull", "", ""),
        MANDATORY_FIELD_ARGUMENT_RESULT("missingMandatoryFieldResult", "", ""), 
        MANDATORY_FIELD_ARGUMENT_SPEC_RULE("missingMandatoryFieldSpecRule", "", ""),
        MANDATORY_FIELD_ARGUMENT_VALUES("missingMandatoryFieldValues", "", ""), 
        MANDATORY_FIELD_ARGUMENT_SEPARATOR("missingMandatorySeparator", "", ""),
        QUANT_OUT_MIN_CONTROL_IN_SPEC("resultCheck_quantitativeOutMinControlInSpec", "", ""),
        EVALUATION_WRONG_RULE("OUT_WRONG_RULE", "", ""),
        UNHANDLED_EXCEPTION("DataSpec_resultCheck_UnhandledException", "", ""),
        SEPARATOR_FOUND_IN_RESULT("DataSpec_resultCheck_separatorValueFoundInResult", "", ""),
        ;
        private ResultCheckErrorsErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }

    public enum ResultCheckSuccessErrorTrapping implements EnumIntMessages{ 
        QUANTITATIVE_LESS_THAN_MIN_VAL_ALLOWED("lessThanMinValAllowed", "", ""),
        QUANTITATIVE_GREATER_THAN_MAX_VAL_ALLOWED("greaterThanMaxValAllowed", "", ""),
        ;
        private ResultCheckSuccessErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
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
            errorVariables = LPArray.addValueToArray1D(errorVariables, ResultCheckErrorsErrorTrapping.MANDATORY_FIELD_ARGUMENT_RESULT);
            Object[] diagnoses =  ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.NULL_MANDATORY_FIELD, errorVariables);
            diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE);
            return diagnoses;}               
        if (specRule==null || "".equals(specRule)){
            errorVariables = LPArray.addValueToArray1D(errorVariables, ResultCheckErrorsErrorTrapping.MANDATORY_FIELD_ARGUMENT_SPEC_RULE);            
            Object[] diagnoses =  ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.NULL_MANDATORY_FIELD, errorVariables);
            diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE);
            return diagnoses;}        
        if (values==null || "".equals(values)){
            errorVariables = LPArray.addValueToArray1D(errorVariables, ResultCheckErrorsErrorTrapping.MANDATORY_FIELD_ARGUMENT_VALUES);            
            Object[] diagnoses =  ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.NULL_MANDATORY_FIELD, errorVariables);
            diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE);
            return diagnoses;}        
        
        Object[] isCorrectTheSpec = matQualit.specLimitIsCorrectQualitative( specRule, values, separator);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isCorrectTheSpec[0].toString())){
            return isCorrectTheSpec;}
        qualitativeRules qualitRule = null;
        try{
            qualitRule = qualitativeRules.valueOf(specRule.toUpperCase());
        }catch(Exception e){            
            String[] errorDetailVariables=new String[]{specRule, Arrays.toString(qualitativeRules.getAllRules())};
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, qualitativeRulesErrors.QUALITATIVE_RULE_NOT_RECOGNIZED, errorDetailVariables);             
        }

        switch (qualitRule){
            case EQUALTO: 
                if (result.equalsIgnoreCase(values)){
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUALITATIVE_IN, null);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_IN);
                    return diagnoses;                    
                }else{
                    errorVariables = new Object[]{result, values};
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUALITATIVE_OUT_EQUAL_TO, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_OUT);
                    return diagnoses;                                                           
                }                
            case NOTEQUALTO: 
                if (result.equalsIgnoreCase(values)){ 
                    errorVariables = new Object[]{result, values};
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUALITATIVE_OUT_NOT_EQUAL_TO, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_OUT);
                    return diagnoses;                                                                               
                }else{                    
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUALITATIVE_IN, null);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_IN);
                    return diagnoses;                    
                }
            case CONTAINS:                 
                if (values.toUpperCase().contains(result.toUpperCase())){
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUALITATIVE_IN, null);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_IN);
                    return diagnoses;                    
                }else{                    
                    errorVariables = new Object[]{result, values};
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUALITATIVE_OUT_CONTAINS, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_OUT);
                    return diagnoses;                         
                }
            case NOTCONTAINS:                 
                if (values.toUpperCase().contains(result.toUpperCase())){
                    errorVariables = new Object[]{result, values};
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUALITATIVE_OUT_NOT_CONTAINS, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_OUT);
                    return diagnoses;                                             
                }else{
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUALITATIVE_IN, null);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_IN);
                    return diagnoses;                    
                }
            case ISONEOF: 
                if ((separator==null) || (separator.length()==0)){
                    errorVariables = LPArray.addValueToArray1D(errorVariables, ResultCheckErrorsErrorTrapping.MANDATORY_FIELD_ARGUMENT_SEPARATOR);
                    Object[] diagnoses =  ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.NULL_MANDATORY_FIELD, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE);
                    return diagnoses;
                }else{                    
                    String[] textSpecArray = values.split(separator);
                    if (textSpecArray.length==0) textSpecArray=values.split("\\"+separator);
                    Boolean contained=result.contains("\\"+separator);
                    if (Boolean.FALSE.equals(contained)) contained=result.contains(separator);
                    if (Boolean.TRUE.equals(contained)) 
                        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.SEPARATOR_FOUND_IN_RESULT, new Object[]{separator, result});
                    if (textSpecArray.length==0 || !(values.contains("\\"+separator))) 
                        textSpecArray=LPArray.addValueToArray1D(textSpecArray, values);
                    for (Integer itextSpecArrayLen=0;itextSpecArrayLen<textSpecArray.length;itextSpecArrayLen++){
                        if (result.equalsIgnoreCase(textSpecArray[itextSpecArrayLen])){                            
                            Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUALITATIVE_IN, errorVariables);
                            diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_IN);
                            return diagnoses;                    
                        }
                    }                
                    errorVariables = new Object[]{result, String.valueOf((Integer)textSpecArray.length), values};
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUALITATIVE_OUT_IS_ONE_OF, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_OUT);
                    return diagnoses;                                                                 
                }
            case ISNOTONEOF: 
                if ((separator==null) || (separator.length()==0)){                    
                    errorVariables = LPArray.addValueToArray1D(errorVariables, ResultCheckErrorsErrorTrapping.MANDATORY_FIELD_ARGUMENT_SEPARATOR);
                    Object[] diagnoses =  ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.NULL_MANDATORY_FIELD, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE);
                    return diagnoses;
                }else{
                    Boolean contained=result.contains("\\"+separator);
                    if (Boolean.FALSE.equals(contained)) contained=result.contains(separator);
                    if (Boolean.TRUE.equals(contained)) 
                        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.SEPARATOR_FOUND_IN_RESULT, new Object[]{separator, result});                    
                    values=values.toUpperCase();
                    result=result.toUpperCase();
                    String[] textSpecArray = values.split(separator);
                    if (textSpecArray.length==0) textSpecArray=values.split("\\"+separator);
                    if (textSpecArray.length==0) textSpecArray=LPArray.addValueToArray1D(textSpecArray, values);
                    if (!LPArray.valueInArray(textSpecArray, result)){
                        errorVariables = new Object[]{result, String.valueOf((Integer)textSpecArray.length), values};                        
                        Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUALITATIVE_IN, errorVariables);
                        diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_IN);
                        return diagnoses;
                    }
                    errorVariables = new Object[]{result, String.valueOf((Integer)textSpecArray.length), values};                        
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUALITATIVE_OUT_IS_NOT_ONE_OF, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_OUT);
                    return diagnoses;                    
                }
            default:                
                String params = "Result: "+result+", Spec Rule: "+specRule+", values: "+values+", separator: "+separator+", listName: "+listName;
                errorVariables = LPArray.addValueToArray1D(errorVariables, params);
                Object[] diagnoses =  ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.UNHANDLED_EXCEPTION, errorVariables);
                diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE);
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
     * @param minValAllowed
     * @param maxValAllowed
     * @return
     */
    public Object[] resultCheck(BigDecimal result, BigDecimal minSpec, BigDecimal maxSpec, Boolean minStrict, Boolean maxStrict, BigDecimal minValAllowed, BigDecimal maxValAllowed){
        Object [] errorVariables = new Object[0];                
        ConfigSpecRule matQuant = new ConfigSpecRule();
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
        if (result==null){
            errorVariables = LPArray.addValueToArray1D(errorVariables, ResultCheckErrorsErrorTrapping.MANDATORY_FIELD_ARGUMENT_RESULT);
            Object[] diagnoses =  ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.NULL_MANDATORY_FIELD, errorVariables);
            return LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE);
        }
        Object[] isCorrectMinMaxSpec = matQuant.areStrictBoundsCorrect(minSpec, maxSpec);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isCorrectMinMaxSpec[0].toString())){
            return isCorrectMinMaxSpec;}
        int compareTo=0;
        if (minValAllowed!=null)
            compareTo = result.compareTo(minValAllowed);
        if (minValAllowed!=null && compareTo<0){
            messages.addMainForError(ResultCheckSuccessErrorTrapping.QUANTITATIVE_LESS_THAN_MIN_VAL_ALLOWED, new Object[]{result, minValAllowed});
            Object[] diagnoses =  ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ResultCheckSuccessErrorTrapping.QUANTITATIVE_LESS_THAN_MIN_VAL_ALLOWED, new Object[]{result, minValAllowed});
            return LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.QUANTITATIVE_LESS_THAN_MIN_VAL_ALLOWED);
        }
        if (maxValAllowed!=null)
            compareTo=result.compareTo(maxValAllowed);
        if (maxValAllowed!=null && compareTo>0){
            messages.addMainForError(ResultCheckSuccessErrorTrapping.QUANTITATIVE_GREATER_THAN_MAX_VAL_ALLOWED, new Object[]{result, maxValAllowed});
            Object[] diagnoses =  ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ResultCheckSuccessErrorTrapping.QUANTITATIVE_GREATER_THAN_MAX_VAL_ALLOWED, new Object[]{result, maxValAllowed});
            return LPArray.addValueToArray1D(diagnoses, ResultCheckSuccessErrorTrapping.QUANTITATIVE_GREATER_THAN_MAX_VAL_ALLOWED);
        }
                
        if (minStrict==null){minStrict=true;}
        if (maxStrict==null){maxStrict=true;}

        if (minSpec!=null){  
            int comparingMIN = minSpec.compareTo(result);
            if (minStrict){
                if ( comparingMIN>-1) {
                        errorVariables = new Object[]{result.toString(), minSpec.toString()}; 
                        Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.OUT_SPEC_MIN_STRICT, errorVariables);
                        diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.OUT_SPEC_MIN_STRICT);
                        return diagnoses;                
                }
            }else{
                if (comparingMIN>0) {
                        errorVariables = new Object[]{result.toString(), minSpec.toString()}; 
                        Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.OUT_SPEC_MIN, errorVariables);
                        diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.OUT_SPEC_MIN);
                        return diagnoses;                
                }                
            }
        }                    
        if (maxSpec!=null){  
            int comparingMAX = result.compareTo(maxSpec);
            if (maxStrict){
                if (comparingMAX>-1) {
                        errorVariables = new Object[]{result.toString(), maxSpec.toString()}; 
                        Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.OUT_SPEC_MAX_STRICT, errorVariables);
                        diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.OUT_SPEC_MAX_STRICT);
                        return diagnoses;            
                }
            }else{
                if (comparingMAX>0) {
                        errorVariables = new Object[]{result.toString(), maxSpec.toString()}; 
                        Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.OUT_SPEC_MAX, errorVariables);
                        diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.OUT_SPEC_MAX);
                        return diagnoses;            
                }
            }
        }    
        Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUANTITATIVE_IN, null);
        diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_IN);
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
     * @param minValAllowed
     * @param maxValAllowed
     * @return
     */
    public Object[] resultCheck(BigDecimal result, BigDecimal minSpec, BigDecimal maxSpec, Boolean minStrict, Boolean maxStrict, BigDecimal minControl, BigDecimal maxControl, Boolean minControlStrict, Boolean maxControlStrict, BigDecimal minValAllowed, BigDecimal maxValAllowed){
        
        Object [] errorVariables = new Object[0]; 

        if (result==null){
                errorVariables = LPArray.addValueToArray1D(errorVariables, ResultCheckErrorsErrorTrapping.MANDATORY_FIELD_ARGUMENT_RESULT);
                Object[] diagnoses =  ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ResultCheckErrorsErrorTrapping.NULL_MANDATORY_FIELD, errorVariables);
                diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE);
                return diagnoses;
        }
        
        Object[] isCorrectMinMaxSpec = this.resultCheck(result,minSpec,maxSpec, minStrict, maxStrict, minValAllowed, maxValAllowed);
        
        if (!DataSampleStructureSuccess.EVALUATION_IN.toString().equalsIgnoreCase(isCorrectMinMaxSpec[isCorrectMinMaxSpec.length-1].toString())){
            return isCorrectMinMaxSpec;
        }

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isCorrectMinMaxSpec[0].toString())){
            return isCorrectMinMaxSpec;
        }

        if (minControl!=null){
            if (minControl.equals(minSpec)) {                
                if (Boolean.FALSE.equals(minStrict) || minStrict==null){
                    errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{DataSampleStructureSuccess.OUT_SPEC_MIN, minSpec, "Min Strict  is set to false."});
                    Object[] diagnoses =  ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, ResultCheckErrorsErrorTrapping.STRICT_DOES_NOT_ALLOW_EQUALS, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE);
                    return diagnoses;
                }

                if (minStrict && minControlStrict){
                    errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{DataSampleStructureSuccess.OUT_SPEC_MIN_STRICT, minSpec, "both, min Spec & Control Strict, set to true"});
                    Object[] diagnoses =  ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, ResultCheckErrorsErrorTrapping.STRICT_DOES_NOT_ALLOW_EQUALS, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE);
                    return diagnoses;                   
                }                    
            }    
    
            if (minControlStrict==null){minControlStrict=true;}
            
            int comparingMIN = minControl.compareTo(result);
            if ( (comparingMIN>0) || (comparingMIN==0 && minControlStrict) ) {
                    errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{minSpec.toString(), " < "+result.toString()+" < ", minControl});
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, ResultCheckErrorsErrorTrapping.QUANT_OUT_MIN_CONTROL_IN_SPEC, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_UPON_CONTROL_MIN);
                    return diagnoses;                    
            }                      
        }
        if (minControl!=null){    
            int comparingMIN = result.compareTo(minControl);
            if (Boolean.TRUE.equals(minControlStrict)){
                if ( comparingMIN<1) {
                    errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{minSpec.toString(), " > "+result.toString()+" > ", minSpec.toString()});
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUANTITATIVE_IN_ALERT_MIN_STRICT, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_UPON_CONTROL_MIN);
                    return diagnoses;                                         
                }                    
            }else{
                if (comparingMIN<0) {
                    errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{minSpec.toString(), " > "+result.toString()+" > ", minSpec});
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUANTITATIVE_IN_ALERT_MIN, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_UPON_CONTROL_MAX);
                    return diagnoses;                                         
                }                                    
            }
        }

        if (maxControl!=null){            
            if ( (maxControl.equals(maxSpec)) && (!maxStrict || maxStrict==null) ) {
                errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{DataSampleStructureSuccess.OUT_SPEC_MAX, maxSpec, "max Strict is set to false."});
                Object[] diagnoses =  ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, ResultCheckErrorsErrorTrapping.STRICT_DOES_NOT_ALLOW_EQUALS, errorVariables);
                diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE);
                return diagnoses;                    
            }
            if ( (maxControl.equals(maxSpec)) && (maxStrict && maxControlStrict) ){
                errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{DataSampleStructureSuccess.OUT_SPEC_MAX_STRICT, maxSpec, "both, max Spec & Control Strict, set to true.."});
                Object[] diagnoses =  ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, ResultCheckErrorsErrorTrapping.STRICT_DOES_NOT_ALLOW_EQUALS, errorVariables);
                diagnoses = LPArray.addValueToArray1D(diagnoses, ResultCheckErrorsErrorTrapping.EVALUATION_WRONG_RULE);
                return diagnoses;                    
            }                    
        }
        if (maxControl!=null){
            int comparingMAX = result.compareTo(maxControl);
            if (Boolean.TRUE.equals(maxControlStrict)){
                if (comparingMAX>-1) {
                    errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{maxControl.toString(), " > "+result.toString()+" > ", maxSpec});
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUANTITATIVE_IN_ALERT_MAX_STRICT, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_UPON_CONTROL_MAX);
                    return diagnoses;                                         
                }                            
            }else{
                if (comparingMAX>0) {
                    errorVariables = LPArray.addValueToArray1D(errorVariables, new Object[]{maxControl.toString(), " > "+result.toString()+" > ", maxSpec});
                    Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUANTITATIVE_IN_ALERT_MAX, errorVariables);
                    diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_UPON_CONTROL_MAX);
                    return diagnoses;                                         
                }                                            
            }
        }
        Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureSuccess.QUANTITATIVE_IN, null);
        diagnoses = LPArray.addValueToArray1D(diagnoses, DataSampleStructureSuccess.EVALUATION_IN);
        return diagnoses;            
    }
}
