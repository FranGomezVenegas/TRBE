/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platformadmin;

import databases.TblsApp;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import trazit.enums.EnumIntEndpoints;

/**
 *
 * @author User
 */
public class PlatformAdminEnums {

    public enum PlatformAdminAPIActionsEndpoints implements EnumIntEndpoints{
        ADD_WHITE_IP("ADD_WHITE_IP", "",new LPAPIArguments[]{
            new LPAPIArguments(TblsApp.IPWhiteList.IP_VALUE1.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
            new LPAPIArguments(TblsApp.IPWhiteList.IP_VALUE2.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7 ),
            new LPAPIArguments(TblsApp.IPWhiteList.IP_VALUE3.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), false, 8 ),
            new LPAPIArguments(TblsApp.IPWhiteList.IP_VALUE4.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), false, 9 ),
            new LPAPIArguments(TblsApp.IPWhiteList.DESCRIPTION.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 10 ),        
            }, EndPointsToRequirements.endpointWithNoOutputObjects),
        ADD_BLACK_IP("ADD_BLACK_IP", "",new LPAPIArguments[]{
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE1.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE2.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7 ),
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE3.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), false, 8 ),
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE4.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), false, 9 ),
            new LPAPIArguments(TblsApp.IPBlackList.DESCRIPTION.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 10 ),        
            }, EndPointsToRequirements.endpointWithNoOutputObjects),
        ;
        private PlatformAdminAPIActionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
            this.outputObjectTypes=outputObjectTypes;            
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){                
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }  
            hm.put(request, argValues);            
            return hm;
        }        
        public String getName(){return this.name;}
        public String getSuccessMessageCode(){return this.successMessageCode;}           
        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        public LPAPIArguments[] getArguments() {return arguments;}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }
    
    public enum PlatformAdminAPIqueriesEndpoints implements EnumIntEndpoints{
        GET_API_LISTS("GET_API_LISTS", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        GET_PLATFORM_ADMIN_ALL_INFO("GET_PLATFORM_ADMIN_ALL_INFO", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        ;
        private PlatformAdminAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
            this.outputObjectTypes=outputObjectTypes;            
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){                
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }  
            hm.put(request, argValues);            
            return hm;
        }        
        public String getName(){return this.name;}
        public String getSuccessMessageCode(){return this.successMessageCode;}           
        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        public LPAPIArguments[] getArguments() {return arguments;}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }
    
}
