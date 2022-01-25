/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.platformdefinition;

import com.labplanet.servicios.proceduredefinition.ProcedureDefinitionAPI;
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
public class PlatformDefinition {
    public enum PlatformDefinitionAPIEndpoints implements EnumIntEndpoints{
        /**
         *
         */
        CREATE_PLATFORM_INSTANCE_STRUCTURE("CREATE_PLATFORM_INSTANCE_STRUCTURE", "createPlatformInstanceStructure_success", 
                new LPAPIArguments[]{new LPAPIArguments("platformName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(ProcedureDefinitionAPI.ProcedureDefinitionpParametersEndpoints.CREATE_DATABASE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments(ProcedureDefinitionAPI.ProcedureDefinitionpParametersEndpoints.DEPLOY_SCHEMAS_AND_PROC_TBLS.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(ProcedureDefinitionAPI.ProcedureDefinitionpParametersEndpoints.CREATE_CHECKPLATFORM_PROCEDURE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                new LPAPIArguments(ProcedureDefinitionAPI.ProcedureDefinitionpParametersEndpoints.REMOVE_CHECKPLATFORM_PROCEDURE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 10)}
        )
        ;
        private PlatformDefinitionAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
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
        public String getName(){
            return this.name;
        }
        public String getSuccessMessageCode(){
            return this.successMessageCode;
        }           

        /**
         * @return the arguments
         */
        public LPAPIArguments[] getArguments() {
            return arguments;
        }     
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;

        @Override
        public JsonArray getOutputObjectTypes() {
            return EndPointsToRequirements.endpointWithNoOutputObjects;
        }
    }
    
}
