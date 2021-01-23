/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import lbplanet.utilities.LPPlatform;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class DataSampleUtilities {
    private DataSampleUtilities(){    throw new IllegalStateException("Utility class");}    
    
    /**
     *
     * @return
     */
    public static Object[] getSchemaSampleStatusList(){      
        return getSchemaSampleStatusList("en");
    }

    /**
     *
     * @param language
     * @return
     */
    public static Object[] getSchemaSampleStatusList(String language){      
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String stList = "";
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        if (language==null){language="en";}
       switch (language){
           case "en":
               stList = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), "sample_statuses_label_en"); 
               break;
           case "es":
               stList = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), "sample_statuses_label_es"); 
               break;
           default:
               stList = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), "sample_statuses"); 
               break;
       }        
        return LPTestingOutFormat.csvExtractFieldValueStringArr(stList);
    }
    
}
