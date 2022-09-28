/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.materialspec;

import databases.Rdbms;
import databases.TblsCnfg;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.PatternSyntaxException;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntMessages;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author Administrator
 */
public class ConfigSpecRule {

    public enum qualitativeRulesErrors implements EnumIntMessages{
        RULE_ARG_IS_MANDATORY("specLimits_ruleMandatoryArgumentNull", "", ""), 
        TEXT_SPEC_ARG_IS_MANDATORY("specLimits_textSpecMandatoryArgumentNull", "", ""), 
        QUALITATIVE_RULE_NOT_RECOGNIZED("specLimits_qualitativeRuleNotRecognized", "", ""),
        SEPARATOR_ARG_IS_MANDATORY("specLimits_separatorMandatoryArgumentNull", "", ""), 
        ;
        private qualitativeRulesErrors(String errCode, String defaultTextEn, String defaultTextEs){
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
    enum qualitativeRules{EQUALTO("EQUALTO","specLimits_equalTo_Successfully"), NOTEQUALTO ("NOTEQUALTO", "specLimits_notEqualTo_Successfully"), 
        CONTAINS ("CONTAINS", "specLimits_contains_Successfully"), NOTCONTAINS ("NOTCONTAINS", "specLimits_notContains_Successfully"), 
        ISONEOF ("ISONEOF", "specLimits_isOneOf_Successfully"), ISNOTONEOF ("ISNOTONEOF", "specLimits_isNotOneOf_Successfully");
        private qualitativeRules(String ruleName, String successCode){
            this.ruleName=ruleName;
            this.successCode=successCode;
        }    
        public String getRuleName(){
            return this.ruleName;
        }
        public String getSuccessCode(){
            return this.successCode;
        }
        public static String[] getAllRules(){
            String[] tableFields=new String[0];
            for (qualitativeRules obj: qualitativeRules.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.getRuleName());
                }
            }           
            return tableFields;
        }   
        private final String ruleName;
        private final String successCode;
    }
    String specArgumentsSeparator = "*";
    enum quantitativeVariables{MINSPEC, MINSPECSTRICT, MINCONTROLSTRICT, MINCONTROL, MAXCONTROL, MAXCONTROLSTRICT, MAXSPEC, MAXSPECSTRICT}
    
    enum quantitativeRulesErrors implements EnumIntMessages{
        MIN_OR_MAX_MANDATORY("MinAndMaxSpecOneOfBothMandatory", "", ""),
        MINSPEC_MAXSPEC_MINSPEC_GREATEROREQUALTO_MAXSPEC("specLimits_quantitativeMinSpecMaxSpec_MinSpecGreaterOrEqualToMaxSpec", "", ""),
        MINCONTROLPRESENT_MINSPECMANDATORY("specLimits_MinControlPresent_MinSpecMandatory", "", ""),
        MAXCONTROLPRESENT_MAXSPECMANDATORY("specLimits_MaxControlPresent_MaxSpecMandatory", "", ""),
        
        MINCONTROL_GREATEROREQUALTO_MAXCONTROL("specLimits_minControlGreaterOrEqualToMaxControl", "", ""),
        MINCONTROL_GREATEROREQUALTO_MAXSPEC("specLimits_minControlGreaterOrEqualToMaxSpec", "", ""),
        MINCONTROL_LESSTHANOREQUALTO_MINSPEC("specLimits_minControlLessThanOrEqualToMinSpec", "", ""),
        MAXCONTROL_GREATEREQUALTO_MINSPEC("specLimits_maxControlGreaterThanOrEqualToMinSpec", "", ""),
        MINCONTROL_GREATEREQUALTO_MINSPEC("specLimits_minControlGreaterThanOrEqualToMinSpec", "", ""),
        MAXCONTROL_GREATEROREQUALTO_MAXSPEC("specLimits_MaxControlGreaterThanOrEqualToMaxSpec", "", ""),
        MINCONTROL_MAXCONTROL_NOTLOGIC("specLimits_MinControlAndMaxControlOutOfLogicControl", "", ""),
        SPEC_RECORD_ALREADY_EXISTS("specRecord_AlreadyExists", "", ""),
        
        ;
        private quantitativeRulesErrors(String errCode, String defaultTextEn, String defaultTextEs){
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
    enum quantitativeRules{
        MESSAGE_CODE_QUANT_MINSPEC_MAXSPEC_SUCCESS("specLimits_quantitativeMinSpecMaxSpec_Successfully"),
        MIN_SPEC_SUCCESS("specLimits_quantitativeMinSpecSuccessfully"), 
        MAX_SPEC_SUCCESS("specLimits_quantitativeMaxSpecSuccessfully"), 
        MINSPEC_MINCONTROL_SUCCESS("specLimits_quantitativeMinSpecMinControlSuccessfully"),
        MINSPEC_MINCONTROL_MAXCONTROL_MAXSPEC_SUCCESS("specLimits_quantitativeMinSpecMinControlMaxControlMaxSpec_Successfully"),
        MINSPEC_MINCONTROL_MAXSPEC_SUCCESS("specLimits_quantitativeMinSpecMinControlMaxSpec_Successfully"),
        MINSPEC_MAXCONTROL_MAXSPEC_SUCCESS("specLimits_quantitativeMinSpecMaxControlMaxSpec_Successfully"),
        ;
        private quantitativeRules(String successCode){
            this.successCode=successCode;
        }    
        public String getSuccessCode(){
            return this.successCode;
        }
        public static String[] getAllRules(){
            String[] tableFields=new String[0];
            for (qualitativeRules obj: qualitativeRules.values()){
                String objName = obj.name();
                if (!"TBL".equalsIgnoreCase(objName)){
                    tableFields=LPArray.addValueToArray1D(tableFields, obj.toString());
                }
            }           
            return tableFields;
        }   
        private final String successCode;
    }
    public static final String JSON_TAG_NAME_SPEC_RULE_DETAILED = "spec_rule_with_detail";

    public static final String JSON_TAG_NAME_SPEC_RULE_INFO = "spec_rule_info";    
    String classVersion = "0.1";
    
    private Boolean ruleIsQuantitative=false;
    private Boolean ruleIsQualitative=false;
    
    private BigDecimal minSpec=null;
    private Boolean minSpecIsStrict=null;
    private BigDecimal maxSpec=null;
    private Boolean maxSpecIsStrict=null;
    private BigDecimal minControl=null;
    private Boolean minControlIsStrict=null;
    private BigDecimal maxControl=null;
    private Boolean maxControlIsStrict=null;

    private BigDecimal minValAllowed=null;
    private BigDecimal maxValAllowed=null;
    
    
    private Boolean quantitativeHasControl=false;  
    private String ruleRepresentation=null;
    private String quantitativeRuleRepresentation=null;        
    private String qualitativeRuleRepresentation=null;
    
    private String quantitativeRuleValues="";
    private String qualitativeRule="";
    private String qualitativeRuleValues="";
    private String qualitativeRuleSeparator=null;
    private String qualitativeRuleListName=null;

    public static final String SPEC_WORD_FOR_UPON_CONTROL="CONTROL";
    public static final String SPEC_WORD_FOR_OOS="OUT";
    public static final String SPEC_WORD_FOR_INSPEC="IN";
    public enum QuantSymbols{
        MIN("<= R"), MIN_STRICT("< R"), MAX("R <="), MAX_STRICT("R <")
        ;
        private QuantSymbols(String c){
            this.symbol=c;
        }    
        public String getSymbol(){
            return this.symbol;
        }        
        private final String symbol;        
        
    }

    /**
     *
     * @param rule
     * @param textSpec
     * @param separator
     * @return
     */
    public Object[] specLimitIsCorrectQualitative(String rule, String textSpec, String separator){        
        Object[]  errorDetailVariables= new Object[0];
        
        if ((rule==null) || (rule.length()==0)){
           return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, qualitativeRulesErrors.RULE_ARG_IS_MANDATORY, LPArray.addValueToArray1D(errorDetailVariables, ""));}
        if ((textSpec==null) || (textSpec.length()==0)){
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, "");          
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, qualitativeRulesErrors.TEXT_SPEC_ARG_IS_MANDATORY, LPArray.addValueToArray1D(errorDetailVariables, ""));}
        qualitativeRules qualitRule = null;
        try{
            qualitRule = qualitativeRules.valueOf(rule.toUpperCase());
        }catch(Exception e){
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, rule);          
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, Arrays.toString(qualitativeRules.getAllRules()));          
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, qualitativeRulesErrors.QUALITATIVE_RULE_NOT_RECOGNIZED, errorDetailVariables);             
        }

        switch (qualitRule){
            case EQUALTO:  
            case NOTEQUALTO: 
            case CONTAINS: 
            case NOTCONTAINS: 
                this.qualitativeRuleValues=rule+specArgumentsSeparator+textSpec+specArgumentsSeparator;
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, qualitRule.getSuccessCode(), errorDetailVariables);                                          
            case ISONEOF: 
                if ((separator==null) || (separator.length()==0)){
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, rule.toUpperCase());          
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, "");          
                    return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, qualitativeRulesErrors.SEPARATOR_ARG_IS_MANDATORY, errorDetailVariables);}                       
                else{
                    String[] textSpecArray = null;
                    try{
                        textSpecArray = textSpec.split(separator);
                    }catch(PatternSyntaxException e){
                        textSpecArray = textSpec.split("\\"+separator);
                    }
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, textSpecArray.length);          
                    this.qualitativeRuleValues=rule+specArgumentsSeparator+textSpec+specArgumentsSeparator+separator;
                    return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, qualitRule.getSuccessCode(), errorDetailVariables);}                       
            case ISNOTONEOF: 
                if ((separator==null) || (separator.length()==0)){
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, rule.toUpperCase());          
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, "");          
                    return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, qualitativeRulesErrors.SEPARATOR_ARG_IS_MANDATORY, errorDetailVariables);}    
                else{
                    String[] textSpecArray =null;
                    try{
                        textSpecArray = textSpec.split(separator);
                    }catch(PatternSyntaxException e){
                        textSpecArray = textSpec.split("\\"+separator);
                    }
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, textSpecArray.length);          
                    this.qualitativeRuleValues=rule+specArgumentsSeparator+textSpec+specArgumentsSeparator+separator;
                    return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, qualitRule.getSuccessCode(), errorDetailVariables);}                          
            default: 
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, rule);          
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, Arrays.toString(qualitativeRules.getAllRules()));          
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, qualitativeRulesErrors.QUALITATIVE_RULE_NOT_RECOGNIZED, errorDetailVariables);    
        }
        
        
    }
/**
 * This method verify that the parameters provided to build one quantitative spec limit apply just one range are coherent accordingly to the different options:<br>
 * Basically when both are not null then cannot be the same value even min cannot be greater than max.
 * @param minSpec Float - The minimum value
 * @param maxSpec Float - The maximum value
 * Bundle parameters:
 *          config-specLimits_MinAndMaxSpecBothMandatory, specLimits_quantitativeMinSpecSuccessfully, specLimits_quantitativeMaxSpecSuccessfully<br>
 *          quantitativeRules.MESSAGE_CODE_QUANT_MINSPEC_MAXSPEC_SUCCESS.getSuccessCode(), specLimits_quantitativeMinSpecMaxSpec_MinSpecGreaterOrEqualToMaxSpec
 * @return Object[] position 0 is a boolean to determine if the arguments are correct, when set to false then position 1 provides detail about the deficiency 
 */
    public Object[] specLimitIsCorrectQuantitative(Float minSpec, Float maxSpec){
        Object[]  errorDetailVariables= new Object[0];                
        if ((minSpec==null) && (maxSpec==null)){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MIN_OR_MAX_MANDATORY, errorDetailVariables);}                                               
        if ((minSpec!=null) && (maxSpec==null)){
            this.quantitativeRuleValues=quantitativeVariables.MINSPECSTRICT.toString()+minSpec.toString();
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MIN_SPEC_SUCCESS.getSuccessCode(), errorDetailVariables);}                                    
        if ((minSpec==null) && (maxSpec!=null)){
            this.quantitativeRuleValues=quantitativeVariables.MAXSPECSTRICT.toString()+maxSpec.toString();
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MAX_SPEC_SUCCESS.getSuccessCode(), errorDetailVariables);
        }
        if (minSpec<maxSpec){  
            this.quantitativeRuleValues=quantitativeVariables.MINSPECSTRICT.toString()+minSpec.toString()+specArgumentsSeparator+quantitativeVariables.MAXSPECSTRICT.toString()+maxSpec.toString();
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MESSAGE_CODE_QUANT_MINSPEC_MAXSPEC_SUCCESS.getSuccessCode(), errorDetailVariables);
        }
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, LPNulls.replaceNull(minSpec).toString());        
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, LPNulls.replaceNull(maxSpec).toString());        
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MINSPEC_MAXSPEC_MINSPEC_GREATEROREQUALTO_MAXSPEC, errorDetailVariables);                                    
    }

/**
 * This method verify that the parameters provided to build one quantitative spec limit apply just one range are coherent accordingly to the different options:<br>
 * Basically when both are not null then cannot be the same value even min cannot be greater than max.
 * @param minSpec BigDecimal - The minimum value
 * @param maxSpec BigDecimal - The maximum value
 * Bundle parameters:
 *          config-specLimits_MinAndMaxSpecBothMandatory, specLimits_quantitativeMinSpecSuccessfully, specLimits_quantitativeMaxSpecSuccessfully<br>
 *          specLimits_quantitativeMinSpecMaxSpec_Successfully, specLimits_quantitativeMinSpecMaxSpec_MinSpecGreaterOrEqualToMaxSpec
 * @return Object[] position 0 is a boolean to determine if the arguments are correct, when set to false then position 1 provides detail about the deficiency 
 */
    public Object[] areStrictBoundsCorrect(BigDecimal minSpec, BigDecimal maxSpec){
        Object[]  errorDetailVariables= new Object[0];        
        if ((minSpec==null) && (maxSpec==null)){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MIN_OR_MAX_MANDATORY, errorDetailVariables);}                                               
        if ((minSpec!=null) && (maxSpec==null)){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MIN_SPEC_SUCCESS.getSuccessCode(), errorDetailVariables);}                                    
        if ((minSpec==null) && (maxSpec!=null)){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MAX_SPEC_SUCCESS.getSuccessCode(), errorDetailVariables);}                                           
        int comparsion = minSpec.compareTo(maxSpec);
        if (comparsion<0){
           return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MESSAGE_CODE_QUANT_MINSPEC_MAXSPEC_SUCCESS.getSuccessCode(), errorDetailVariables);}                                    
        
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, LPNulls.replaceNull(minSpec).toString());        
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, LPNulls.replaceNull(maxSpec).toString());
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MINSPEC_MAXSPEC_MINSPEC_GREATEROREQUALTO_MAXSPEC, errorDetailVariables);                                    
    }

    public Object[] areBoundsCorrect(BigDecimal minSpec, BigDecimal maxSpec, Boolean isStrict){
        Object[]  errorDetailVariables= new Object[0];        
        if ((minSpec==null) && (maxSpec==null)){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MIN_OR_MAX_MANDATORY, errorDetailVariables);}                                               
        if ((minSpec!=null) && (maxSpec==null)){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MIN_SPEC_SUCCESS.getSuccessCode(), errorDetailVariables);}                                    
        if ((minSpec==null) && (maxSpec!=null)){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MAX_SPEC_SUCCESS.getSuccessCode(), errorDetailVariables);}                                           
        int comparsion = minSpec.compareTo(maxSpec);
        if ((comparsion<0) || (isStrict && comparsion<1)){
           return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MESSAGE_CODE_QUANT_MINSPEC_MAXSPEC_SUCCESS.getSuccessCode(), errorDetailVariables);}                                    
        
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, LPNulls.replaceNull(minSpec).toString());        
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, LPNulls.replaceNull(maxSpec).toString());
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MINSPEC_MAXSPEC_MINSPEC_GREATEROREQUALTO_MAXSPEC, errorDetailVariables);                                    
    }
    
/**
 * This method verify that the parameters provided to build one quantitative spec limit apply one double level range are coherent accordingly to the different options:<br>
 * Basically when both peers, min-max, are not null then cannot be the same value even min cannot be greater than max. At the same time
 * The control range should be included or part of the spec range that should be broader.
 * @param minSpec Float - The minimum value
 * @param maxSpec Float - The maximum value
 * @param minControl1 Float - The minimum control
 * @param maxControl1 Float - The maximum control
 * Bundle parameters:
 *          config-specLimits_quantitativeMinSpecMaxSpec_Successfully, specLimits_MinControlPresent_MinSpecMandatory, specLimits_MaxControlPresent_MaxSpecMandatory<br>
 *          specLimits_minControlGreaterOrEqualToMaxControl, specLimits_minControlGreaterOrEqualToMaxSpec, specLimits_MaxControlLessThanOrEqualToMinSpec <br>
 *          specLimits_MinControlLessThanOrEqualToMinSpec, specLimits_quantitativeMinSpecMinControlMaxSpec_Successfully, specLimits_MaxControlGreaterThanOrEqualToMaxSpec <br>
 *          specLimits_quantitativeMinSpecMinControlMaxControlMaxSpec_Successfully, specLimits_MinControlAndMaxControlOutOfLogicControl
 * @return Object[] position 0 is a boolean to determine if the arguments are correct, when set to false then position 1 provides detail about the deficiency 
 */    
    public Object[] specLimitIsCorrectQuantitative(Float minSpec, Float maxSpec, Float minControl1, Float maxControl1){
        Object[]  errorDetailVariables= new Object[0];        
        Object[] isCorrectMinMaxSpec = this.specLimitIsCorrectQuantitative(minSpec, maxSpec);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isCorrectMinMaxSpec[0].toString())){
            return isCorrectMinMaxSpec;}
        String currSpecLimitVariables=this.quantitativeRuleValues;
        
        if ((minControl1==null) && (maxControl1==null)){            
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MESSAGE_CODE_QUANT_MINSPEC_MAXSPEC_SUCCESS.getSuccessCode(), errorDetailVariables);}                                            
        if ((minControl1!=null) && (minSpec==null)){
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, minControl1.toString());        
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MINCONTROLPRESENT_MINSPECMANDATORY, errorDetailVariables);}                                           
        if ((maxControl1!=null) && (maxSpec==null)){            
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, maxControl1.toString());      
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MAXCONTROLPRESENT_MAXSPECMANDATORY, errorDetailVariables);}                                    
        if (((minControl1!=null) && (maxControl1!=null)) && (minControl1>=maxControl1)){
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, minControl1.toString());        
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, maxControl1.toString());    
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MINCONTROL_GREATEROREQUALTO_MAXCONTROL, errorDetailVariables);}                                    
        if (((minControl1!=null) && (maxSpec!=null)) && (minControl1>=maxSpec)){
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, minControl1.toString());        
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, maxSpec.toString());    
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MINCONTROL_GREATEROREQUALTO_MAXSPEC, errorDetailVariables);}                      
        if (((maxControl1!=null) && (minSpec!=null)) && (maxControl1<=minSpec)){
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, maxControl1.toString());        
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, minSpec.toString());    
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MAXCONTROL_GREATEREQUALTO_MINSPEC, errorDetailVariables);}                      
        if (minControl1!=null){                        
            if (minControl1.compareTo(minSpec)<=0){
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, LPNulls.replaceNull(minControl1).toString());        
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, LPNulls.replaceNull(minSpec).toString());    
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MINCONTROL_GREATEREQUALTO_MINSPEC, errorDetailVariables);                                      
            }else{
                if (maxControl1==null){
                    this.quantitativeRuleValues=currSpecLimitVariables+specArgumentsSeparator+quantitativeVariables.MINCONTROLSTRICT.toString()+minControl1.toString()
                            +quantitativeVariables.MINSPECSTRICT.toString()+minSpec.toString();
                    return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MINSPEC_MINCONTROL_MAXSPEC_SUCCESS.getSuccessCode(), errorDetailVariables);   
                }
            }
        }                      
        if ((maxControl1!=null)){
            if (maxControl1.compareTo(maxSpec)>=0){
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, LPNulls.replaceNull(maxControl1).toString());        
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, LPNulls.replaceNull(maxSpec).toString()); 
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MAXCONTROL_GREATEROREQUALTO_MAXSPEC, errorDetailVariables);                                   
            }else{
                this.quantitativeRuleValues=currSpecLimitVariables+specArgumentsSeparator+quantitativeVariables.MAXCONTROLSTRICT.toString()+maxControl1.toString();
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MINSPEC_MINCONTROL_MAXCONTROL_MAXSPEC_SUCCESS.getSuccessCode(), errorDetailVariables);        
            }    
        }
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, LPNulls.replaceNull(minControl1).toString());        
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, LPNulls.replaceNull(maxControl1).toString()); 
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MINCONTROL_MAXCONTROL_NOTLOGIC, errorDetailVariables);              
    }    
/**
 * This method verify that the parameters provided to build one quantitative spec limit apply one double level range are coherent accordingly to the different options:<br>
 * Basically when both peers, min-max, are not null then cannot be the same value even min cannot be greater than max. At the same time
 * The control range should be included or part of the spec range that should be broader.
 * @param minSpec Float - The minimum value
 * @param maxSpec Float - The maximum value
 * @param minControl1 Float - The minimum control
 * @param maxControl1 Float - The maximum control
 * Bundle parameters:
 *          config-specLimits_quantitativeMinSpecMaxSpec_Successfully, specLimits_MinControlPresent_MinSpecMandatory, specLimits_MaxControlPresent_MaxSpecMandatory<br>
 *          specLimits_minControlGreaterOrEqualToMaxControl, specLimits_minControlGreaterOrEqualToMaxSpec, specLimits_MaxControlLessThanOrEqualToMinSpec <br>
 *          specLimits_MinControlLessThanOrEqualToMinSpec, specLimits_quantitativeMinSpecMinControlMaxSpec_Successfully, specLimits_MaxControlGreaterThanOrEqualToMaxSpec <br>
 *          specLimits_quantitativeMinSpecMinControlMaxControlMaxSpec_Successfully, specLimits_MinControlAndMaxControlOutOfLogicControl
 * @return Object[] position 0 is a boolean to determine if the arguments are correct, when set to false then position 1 provides detail about the deficiency 
 */    
    public Object[] specLimitIsCorrectQuantitative(BigDecimal minSpec, BigDecimal maxSpec, BigDecimal minControl1, BigDecimal maxControl1){
        return specLimitIsCorrectQuantitative(minSpec, false, maxSpec,false, minControl1, false, maxControl1, false);
    }
    
    public Object[] specLimitIsCorrectQuantitative(BigDecimal minSpec, Boolean minSpecIsStrict, BigDecimal maxSpec, Boolean maxSpecIsStrict, BigDecimal minControl1, Boolean minControl1IsStrict, BigDecimal maxControl1, Boolean maxControl1IsStrict){
        Object[]  errorDetailVariables= new Object[0];               
        Object[] isCorrectMinMaxSpec = this.areStrictBoundsCorrect(minSpec, maxSpec);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isCorrectMinMaxSpec[0].toString())) return isCorrectMinMaxSpec;
        if ((minControl1!=null) && (minSpec==null)){
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, minControl1.toString());        
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MINCONTROLPRESENT_MINSPECMANDATORY, errorDetailVariables);
        }
        if ((maxControl1!=null) && (maxSpec==null)){            
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, maxControl1.toString());      
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MAXCONTROLPRESENT_MAXSPECMANDATORY, errorDetailVariables);
        }            

        if (minControl1==null && maxControl1==null)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MESSAGE_CODE_QUANT_MINSPEC_MAXSPEC_SUCCESS.getSuccessCode(), errorDetailVariables);
        Object[] checkBoundDiagn = this.areStrictBoundsCorrect(minControl1, maxControl1);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkBoundDiagn[0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MINCONTROL_GREATEROREQUALTO_MAXCONTROL, errorDetailVariables);
        if (minSpec!=null && minControl1!=null){
            checkBoundDiagn = this.areStrictBoundsCorrect(minSpec, minControl1);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkBoundDiagn[0].toString()))
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MINCONTROL_LESSTHANOREQUALTO_MINSPEC, errorDetailVariables);
            if (maxControl1==null && maxSpec==null)
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MINSPEC_MINCONTROL_SUCCESS.getSuccessCode(), errorDetailVariables);
                
        }
        if (maxSpec!=null && maxControl1!=null){
            checkBoundDiagn = this.areStrictBoundsCorrect(maxControl1, maxSpec);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkBoundDiagn[0].toString()))
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MAXCONTROL_GREATEROREQUALTO_MAXSPEC, errorDetailVariables);
        }
        if (maxSpec!=null && minControl1!=null){
            checkBoundDiagn = this.areStrictBoundsCorrect(minControl1, maxSpec);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkBoundDiagn[0].toString()))
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MINCONTROL_GREATEROREQUALTO_MAXSPEC, errorDetailVariables);
        }
        if (minSpec!=null && maxControl1!=null){
            checkBoundDiagn = this.areStrictBoundsCorrect(minSpec, maxControl1);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkBoundDiagn[0].toString()))
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MAXCONTROL_GREATEREQUALTO_MINSPEC, errorDetailVariables);        
        }
        if (minSpec!=null && minControl1!=null && maxControl1!=null && maxSpec!=null)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MINSPEC_MINCONTROL_MAXCONTROL_MAXSPEC_SUCCESS.getSuccessCode(), errorDetailVariables);
        if (minSpec!=null && minControl1!=null && maxControl1==null && maxSpec!=null)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MINSPEC_MINCONTROL_MAXSPEC_SUCCESS.getSuccessCode(), errorDetailVariables);
        if (minSpec!=null && minControl1==null && maxControl1!=null && maxSpec!=null)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, quantitativeRules.MINSPEC_MAXCONTROL_MAXSPEC_SUCCESS.getSuccessCode(), errorDetailVariables);
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.MINCONTROL_MAXCONTROL_NOTLOGIC, errorDetailVariables);
    }    

    /**
     *
     * @param limitId
     * @param language
     * @return
     */
    public Object[] specLimitsRule(Integer limitId, String language){
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        String procInstanceName=procReqInstance.getProcedureInstance();
        Object[] errorDetailVariables= new Object[0];      
      StringBuilder ruleBuilder = new StringBuilder(0);
      Object[][] specDef=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName(), 
            new String[]{TblsCnfg.SpecLimits.LIMIT_ID.getName()}, new Object[]{limitId}, 
            new String[]{TblsCnfg.SpecLimits.RULE_TYPE.getName(), TblsCnfg.SpecLimits.RULE_VARIABLES.getName(),
              TblsCnfg.SpecLimits.MIN_VAL_ALLOWED.getName(), TblsCnfg.SpecLimits.MAX_VAL_ALLOWED.getName()  });
      if (LPPlatform.LAB_FALSE.equalsIgnoreCase(specDef[0][0].toString())) return LPArray.array2dTo1d(specDef);
      String ruleType = specDef[0][0].toString();
      String ruleVariables = specDef[0][1].toString();
      switch (ruleType.toLowerCase()) {
        case "qualitative":
          this.ruleIsQualitative=true;
          String[] qualitSpecTestingArray = ruleVariables.split("\\"+specArgumentsSeparator);
          this.qualitativeRule = qualitSpecTestingArray[0];
          if (qualitSpecTestingArray.length>=2)
            this.qualitativeRuleValues = qualitSpecTestingArray[1];
          if (qualitSpecTestingArray.length == 3) {
              this.qualitativeRuleSeparator  = qualitSpecTestingArray[2];
          }
          this.qualitativeRuleListName= null;       
          this.qualitativeRuleRepresentation=ruleType+" "+ruleVariables;
          this.ruleRepresentation=this.qualitativeRuleRepresentation;
          break;
        case "quantitative":
            this.ruleIsQuantitative=true;
            if (specDef[0][2]!=null && specDef[0][2].toString().length()>0) 
                this.minValAllowed=BigDecimal.valueOf(Double.valueOf(specDef[0][2].toString()));
            if (specDef[0][3]!=null && specDef[0][3].toString().length()>0) 
                this.maxValAllowed=BigDecimal.valueOf(Double.valueOf(specDef[0][3].toString()));
                
            String[] quantiSpecTestingArray = ruleVariables.split("\\"+specArgumentsSeparator);
            for (Integer iField=0; iField<quantiSpecTestingArray.length;iField++){
              String curParam = quantiSpecTestingArray[iField];

              if (curParam.toUpperCase().contains(quantitativeVariables.MINSPECSTRICT.toString())){
                      curParam = curParam.replace(quantitativeVariables.MINSPECSTRICT.toString(), "");       
                      this.minSpec = BigDecimal.valueOf(Double.valueOf(curParam));   
                      this.minSpecIsStrict=true;
              }        
              if (curParam.toUpperCase().contains(quantitativeVariables.MINSPEC.toString())){
                      curParam = curParam.replace(quantitativeVariables.MINSPEC.toString(), "");    
                      //Long curParamLong=Long.valueOf(-2.5); 
                      this.minSpec = BigDecimal.valueOf(Double.valueOf(curParam)); 
                      this.minSpecIsStrict=false;
              }        
              if (curParam.toUpperCase().contains(quantitativeVariables.MINCONTROLSTRICT.toString())){
                      curParam = curParam.replace(quantitativeVariables.MINCONTROLSTRICT.toString(), "");
                      this.minControl = BigDecimal.valueOf(Double.valueOf(curParam)); 
                      this.minControlIsStrict=true; 
                      this.quantitativeHasControl=true;
              }        
              if (curParam.toUpperCase().contains(quantitativeVariables.MINCONTROL.toString())){
                    curParam = curParam.replace(quantitativeVariables.MINCONTROL.toString(), "");          
                    this.minControl = BigDecimal.valueOf(Double.valueOf(curParam)); 
                    this.minControlIsStrict=false; 
                    this.quantitativeHasControl=true;
              }        
              if (curParam.toUpperCase().contains(quantitativeVariables.MAXCONTROLSTRICT.toString())){
                      curParam = curParam.replace(quantitativeVariables.MAXCONTROLSTRICT.toString(), "");       
                      this.maxControl = BigDecimal.valueOf(Double.valueOf(curParam));     
                      this.maxControlIsStrict=true; 
                      this.quantitativeHasControl=true;
              }        
              if (curParam.toUpperCase().contains(quantitativeVariables.MAXCONTROL.toString())){
                      curParam = curParam.replace(quantitativeVariables.MAXCONTROL.toString(), "");          
                      this.maxControl = BigDecimal.valueOf(Double.valueOf(curParam)); 
                      this.maxControlIsStrict=false; 
                      this.quantitativeHasControl=true;
              }        
              if (curParam.toUpperCase().contains("MAXSPECSTRICT")){
                      curParam = curParam.replace("MAXSPECSTRICT", "");       
                      this.maxSpec = BigDecimal.valueOf(Double.valueOf(curParam));    
                      this.maxSpecIsStrict=true;
              }        
              if (curParam.toUpperCase().contains("MAXSPEC")){
                      curParam = curParam.replace("MAXSPEC", "");              
                      this.maxSpec =BigDecimal.valueOf(Double.valueOf(curParam)); 
                      this.maxSpecIsStrict=false;
              }        
          }   
          StringBuilder ruleRepr = new StringBuilder(0);
          if (this.minSpec!=null){
            if (this.minSpecIsStrict)ruleRepr.append("<");
            ruleRepr.append(this.minSpec);
          }
          if (this.minControl!=null){
            ruleRepr.append(" ");
            if (this.minControlIsStrict)ruleRepr.append("<");
            ruleRepr.append(this.minControl);
          }

          ruleRepr.append(" R ");

          if (this.maxControl!=null){
            ruleRepr.append(" ");
            if (this.maxControlIsStrict)ruleRepr.append(">");
            ruleRepr.append(this.maxControl);
          }
          if (this.maxSpec!=null){
            ruleRepr.append(" ");            
            if (this.maxSpecIsStrict)ruleRepr.append(">");
            ruleRepr.append(this.maxSpec);
          }
          this.quantitativeRuleRepresentation=ruleRepr.toString();
          this.ruleRepresentation=this.quantitativeRuleRepresentation;
          break;
        default:
          errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, new Object[]{limitId.toString(), LPPlatform.buildSchemaName(GlobalVariables.Schemas.CONFIG.getName(), procInstanceName), ruleType});
          return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResult_SpecRuleNotImplemented", errorDetailVariables);        
      }
      return new Object[]{LPPlatform.LAB_TRUE, ruleBuilder.toString()};
    }

    /**
     *
     * @param sampleSpecCode
     * @param sampleSpecCodeVersion
     * @param sampleSpecVariationName
     * @param analysis
     * @param methodName
     * @param methodVersion
     * @param paramName
     * @param fieldsToRetrieve
     * @return
     */
    public static Object[][] getSpecLimitLimitIdFromSpecVariables(String sampleSpecCode, Integer sampleSpecCodeVersion,
                String sampleSpecVariationName, String analysis, String methodName, Integer methodVersion, String paramName, String[] fieldsToRetrieve){

        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        return Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName(), 
                new String[]{TblsCnfg.SpecLimits.CODE.getName(), TblsCnfg.SpecLimits.CONFIG_VERSION.getName(), TblsCnfg.SpecLimits.VARIATION_NAME.getName(), 
                    TblsCnfg.SpecLimits.ANALYSIS.getName(), TblsCnfg.SpecLimits.METHOD_NAME.getName(), TblsCnfg.SpecLimits.METHOD_VERSION.getName(), 
                    TblsCnfg.SpecLimits.PARAMETER.getName()}, new Object[]{sampleSpecCode, sampleSpecCodeVersion, sampleSpecVariationName, 
                      analysis, methodName, methodVersion, paramName}, 
                fieldsToRetrieve);
//                new String[]{TblsCnfg.SpecLimits.LIMIT_ID.getName(), TblsCnfg.SpecLimits.RULE_TYPE.getName(), TblsCnfg.SpecLimits.RULE_VARIABLES.getName(), TblsCnfg.SpecLimits.LIMIT_ID.getName(), 
//                    TblsCnfg.SpecLimits.UOM.getName(), TblsCnfg.SpecLimits.UOM_CONVERSION_MODE.getName()});         
    }
  /**
   * @return the qualitativeRule
   */
  public String getQualitativeRule() {
    return qualitativeRule;
  }

  /**
   * @return the qualitativeRuleValues
   */
  public String getQualitativeRuleValues() {
    return qualitativeRuleValues;
  }
  public String getQuantitativeRuleValues() {
    return quantitativeRuleValues;
  }
  

  /**
   * @return the ruleIsQuantitative
   */
  public Boolean getRuleIsQuantitative() {
    return ruleIsQuantitative;
  }

  /**
   * @return the ruleIsQualitative
   */
  public Boolean getRuleIsQualitative() {
    return ruleIsQualitative;
  }

  /**
   * @return the minSpec
   */
  public BigDecimal getMinSpec() {
    return minSpec;
  }

  /**
   * @return the minSpecIsStrict
   */
  public Boolean getMinSpecIsStrict() {
    return minSpecIsStrict;
  }

  /**
   * @return the maxSpec
   */
  public BigDecimal getMaxSpec() {
    return maxSpec;
  }

  /**
   * @return the maxSpecIsStrict
   */
  public Boolean getMaxSpecIsStrict() {
    return maxSpecIsStrict;
  }

  /**
   * @return the minControl
   */
  public BigDecimal getMinControl() {
    return minControl;
  }

  /**
   * @return the minControlIsStrict
   */
  public Boolean getMinControlIsStrict() {
    return minControlIsStrict;
  }

  /**
   * @return the minValAllowed
   */
  public BigDecimal getMinValAllowed() {
    return minValAllowed;
  }
  
  /**
   * @return the maxValAllowed
   */
  public BigDecimal getMaxValAllowed() {
    return maxValAllowed;
  }

  /**
   * @return the maxControl
   */
  public BigDecimal getMaxControl() {
    return maxControl;
  }

  /**
   * @return the maxControlIsStrict
   */
  public Boolean getMaxControlIsStrict() {
    return maxControlIsStrict;
  }

  /**
   * @return the quantitativeHasControl
   */
  public Boolean getQuantitativeHasControl() {
    return quantitativeHasControl;
  }
  /**
   * @return the ruleRepresentation independently of being quanti or auqlitative (Use the Quant or Qual get when concrete is a need)
   */
  public String getRuleRepresentation() {
    return ruleRepresentation;
  }
  public Object[][] getRuleData(){
        if (ruleIsQuantitative){
            Object[][] quantiRuleInfo = new Object[9][2];
            quantiRuleInfo[0]=new Object[]{"minSpec", minSpec};
            quantiRuleInfo[1]=new Object[]{"minSpecIsStrict", minSpecIsStrict};
            quantiRuleInfo[2]=new Object[]{"minControl", minControl};
            quantiRuleInfo[3]=new Object[]{"minControlIsStrict", minControlIsStrict};
            quantiRuleInfo[4]=new Object[]{"maxControl", maxControl};
            quantiRuleInfo[5]=new Object[]{"maxControlIsStrict", maxControlIsStrict};
            quantiRuleInfo[6]=new Object[]{"maxSpec", maxSpec};
            quantiRuleInfo[7]=new Object[]{"maxSpecIsStrict", maxSpecIsStrict};
            quantiRuleInfo[8]=new Object[]{"ruleRepresentation", ruleRepresentation};
            return quantiRuleInfo;
        }
        if (ruleIsQualitative){
            Object[][] qualitRuleInfo = new Object[5][2];
            qualitRuleInfo[0]=new Object[]{"qualitativeRule", qualitativeRule};
            qualitRuleInfo[1]=new Object[]{"qualitativeRuleValues", qualitativeRuleValues};
            qualitRuleInfo[2]=new Object[]{"qualitativeRuleSeparator", qualitativeRuleSeparator};
            qualitRuleInfo[3]=new Object[]{"qualitativeRuleListName", qualitativeRuleListName};
            qualitRuleInfo[4]=new Object[]{"ruleRepresentation", ruleRepresentation};
            return qualitRuleInfo;
        }   
        return new Object[][]{};
  }
  /**
   * @return the quantitativeRuleRepresentation
   */
  public String getQuantitativeRuleRepresentation() {
    return quantitativeRuleRepresentation;
  }

  /**
   * @return the qualitativeRuleRepresentation
   */
  public String getQualitativeRuleRepresentation() {
    return qualitativeRuleRepresentation;
  }
  /**
   * @return the qualitativeRuleSeparator
   */
  public String getQualitativeRuleSeparator() {
    return qualitativeRuleSeparator;
  }

  /**
   * @return the qualitativeRuleListName
   */
  public String getQualitativeRuleListName() {
    return qualitativeRuleListName;
  }
    
} 
