/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
/**
 *
 * @author User
 */
public class LPAPIArguments {
    
    public enum ArgumentType{STRING, INTEGER, BIGDECIMAL, STRINGARR, STRINGOFOBJECTS, DATE, BOOLEAN, BOOLEANARR}
    private final String name;
    private String type=ArgumentType.STRING.toString();
    private final Boolean mandatory;
    private final Integer testingArgPosic;

    public LPAPIArguments(String nme, String tpe, Boolean mandatry, Integer tstArg){
        this.name=nme;
        if (tpe!=null) this.type=tpe;
        this.mandatory=mandatry;
        this.testingArgPosic=tstArg;
    }
    public LPAPIArguments(String nme){
        this.name=nme;
        this.type=ArgumentType.STRING.toString();
        this.mandatory=true;
        this.testingArgPosic=-1;
    }
    public LPAPIArguments(String nme, String tpe){
        this.name=nme;
        if (tpe!=null) this.type=tpe;
        this.mandatory=true;
        this.testingArgPosic=-1;
    }

    
    public LPAPIArguments(String nme, String tpe, Boolean mandatry){
        this.name=nme;
        this.type=tpe;
        this.mandatory=mandatry;
        this.testingArgPosic=-1;
    }
        
            
    public static Object[] buildAPIArgsumentsArgsValues(HttpServletRequest request, LPAPIArguments[] argsDef){
        if (argsDef==null) return new Object[0];
        Object[] returnArgsDef=new Object[0];
        for (LPAPIArguments currArg: argsDef){
            String requestArgValue=(String) request.getAttribute(currArg.getName());
                if (requestArgValue==null) requestArgValue=LPNulls.replaceNull(request.getParameter(currArg.getName())).toString();
            try{
                ArgumentType argType=ArgumentType.valueOf(currArg.getType().toUpperCase());                
                switch (argType){
                    case STRING:
                        returnArgsDef=LPArray.addValueToArray1D(returnArgsDef, requestArgValue);
                        break;
                    case INTEGER:
                        Integer valueConverted=null;
                        if (requestArgValue.length()>0) valueConverted = Integer.parseInt(requestArgValue);
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
        return returnArgsDef;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the mandatory
     */
    public Boolean getMandatory() {
        return mandatory;
    }

    /**
     * @return the testingArgPosic
     */
    public Integer getTestingArgPosic() {
        return testingArgPosic;
    }
    
}
