/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import functionaljavaa.analysis.ConfigAnalysisStructure.ConfigAnalysisErrorTrapping;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author User
 */
public class LPAPIArguments {
    
    public enum ArgumentType{STRING, INTEGER, BIGDECIMAL, STRINGARR, STRINGOFOBJECTS, DATE, DATETIME, BOOLEAN, BOOLEANARR}
    private final String name;
    private String type=ArgumentType.STRING.toString();
    private final Boolean mandatory;
    private final Integer testingArgPosic;
    private final String argComment;
    private final String argCommentTag;

    public LPAPIArguments(String nme, String tpe, Boolean mandatory, Integer tstArg, String developerComment, String developerTag){
        this.name=nme;
        if (tpe!=null) this.type=tpe;
        this.mandatory=mandatory;
        this.testingArgPosic=tstArg;
        this.argComment=developerComment;
        this.argCommentTag=developerTag;
    }

    public LPAPIArguments(String nme, String tpe, Boolean mandatory, Integer tstArg){
        this.name=nme;
        if (tpe!=null) this.type=tpe;
        this.mandatory=mandatory;
        this.testingArgPosic=tstArg;
        this.argComment="";
        this.argCommentTag="";        
    }
    public LPAPIArguments(String nme){
        this.name=nme;
        this.type=ArgumentType.STRING.toString();
        this.mandatory=true;
        this.testingArgPosic=-1;
        this.argComment="";
        this.argCommentTag="";        
    }
    public LPAPIArguments(String nme, String tpe){
        this.name=nme;
        if (tpe!=null) this.type=tpe;
        this.mandatory=true;
        this.testingArgPosic=-1;
        this.argComment="";
        this.argCommentTag="";        
    }   
    public LPAPIArguments(String nme, String tpe, Boolean mandatry){
        this.name=nme;
        this.type=tpe;
        this.mandatory=mandatry;
        this.testingArgPosic=-1;
        this.argComment="";
        this.argCommentTag="";        
    }
        
            
    public static Object[] buildAPIArgsumentsArgsValues(HttpServletRequest request, LPAPIArguments[] argsDef){
        if (argsDef==null) return new Object[0];
        Object[] returnArgsDef=new Object[0];
        for (LPAPIArguments currArg: argsDef){
            String requestArgValue=(String) request.getAttribute(currArg.getName());
            if (requestArgValue==null) requestArgValue=LPNulls.replaceNull(request.getParameter(currArg.getName()));
            if (LPNulls.replaceNull(requestArgValue).length()==0){
                if (currArg.getMandatory())
                    return new Object[]{LPPlatform.LAB_FALSE, ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{currArg.getName()}), currArg.getName()};
                else
                    returnArgsDef=LPArray.addValueToArray1D(returnArgsDef, "");
            }else{
                requestArgValue=specialTagFilter(requestArgValue);
                try{
                    ArgumentType argType=ArgumentType.valueOf(currArg.getType().toUpperCase());                
                    switch (argType){
                        case STRING:
                            returnArgsDef=LPArray.addValueToArray1D(returnArgsDef, requestArgValue);
                            break;
                        case INTEGER:
                            Integer valueConverted=null;
                            try{
                                if (Boolean.FALSE.equals("UNDEFINED".equalsIgnoreCase(requestArgValue))&&requestArgValue.length()>0) valueConverted = Integer.parseInt(requestArgValue);
                            }catch(NumberFormatException e){
                                return new Object[]{LPPlatform.LAB_FALSE, ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.VALUE_NOT_NUMERIC, new Object[]{requestArgValue}), requestArgValue};
                            }
                            returnArgsDef=LPArray.addValueToArray1D(returnArgsDef, valueConverted);
                            break;
                        case BIGDECIMAL:
                            BigDecimal valueConvertedBigDec=null;
                            if (requestArgValue.length()>0) valueConvertedBigDec = new BigDecimal(requestArgValue);
                            returnArgsDef=LPArray.addValueToArray1D(returnArgsDef, valueConvertedBigDec);
                            break;
                        case DATE:     
                            Date valueConvertedDate=null;
                            if (requestArgValue.length()>0) valueConvertedDate=Date.valueOf(requestArgValue);
                            returnArgsDef=LPArray.addValueToArray1D(returnArgsDef, valueConvertedDate);
                            break;
                        case DATETIME:     
                            returnArgsDef=LPArray.addValueToArray1D(returnArgsDef, LPDate.stringFormatToLocalDateTime(requestArgValue));    
                            break;
                        case BOOLEAN:     
                            Boolean valueConvertedBoolean=null;
                            if (requestArgValue.length()>0 && ("TRUE".equalsIgnoreCase(requestArgValue)||"FALSE".equalsIgnoreCase(requestArgValue) )) valueConvertedBoolean=Boolean.valueOf(requestArgValue);
                            returnArgsDef=LPArray.addValueToArray1D(returnArgsDef, valueConvertedBoolean);
                            break;
                        case STRINGARR:
                            //String[] valueConvertedStrArr = requestArgValue.split("\\|");
                            returnArgsDef=LPArray.addValueToArray1D(returnArgsDef, requestArgValue);
                            break;                        
                        case STRINGOFOBJECTS:
                            //Object[] valueConvertedTopObjectArr = LPArray.convertStringWithDataTypeToObjectArray(requestArgValue.split("\\|"));   
                            returnArgsDef=LPArray.addValueToArray1D(returnArgsDef, requestArgValue);
                            break;                                                
                        default:
                            returnArgsDef=LPArray.addValueToArray1D(returnArgsDef, requestArgValue);
                            break;                
                    }   
                }catch(NumberFormatException e){
                        returnArgsDef=LPArray.addValueToArray1D(returnArgsDef, requestArgValue);
                        break;                                
                }
            }
        }
        return returnArgsDef;
    }

    public static String specialTagFilter(String value){
        String tagName="{TZ_DATE}";
        if (value.contains(tagName)) value=value.replace(tagName, LPDate.getCurrentTimeStamp().toString());
        return value;
    }
    public String getName() {        return name;    }
    public String getType() {        return type;    }
    public Boolean getMandatory() {        return mandatory;    }
    public Integer getTestingArgPosic() {        return testingArgPosic;    }
    public String getDevComment() {        return this.argComment;    }
    public String getDevCommentTags() {        return this.argCommentTag;    }
    
}
