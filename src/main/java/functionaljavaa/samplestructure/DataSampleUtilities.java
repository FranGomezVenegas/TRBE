/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import lbplanet.utilities.LPPlatform;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleBusinessRules;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import static trazit.globalvariables.GlobalVariables.DEFAULTLANGUAGE;
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
        return getSchemaSampleStatusList(DEFAULTLANGUAGE);
    }

    /**
     *
     * @param language
     * @return
     */
    public static Object[] getSchemaSampleStatusList(String language){  
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        String procInstanceName=procReqInstance.getProcedureInstance();
        String stList = "";
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        if (language==null){language=DEFAULTLANGUAGE;}
       switch (language){
           case DEFAULTLANGUAGE:
               stList = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.STATUSES_LABEL_EN.getAreaName(), DataSampleBusinessRules.STATUSES_LABEL_EN.getTagName(), true); 
               if (stList.length()==0) stList=DataSample.SAMPLE_STATUSES_LABEL_EN_WHEN_NO_PROPERTY;
               break;
           case "es":
               stList = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.STATUSES_LABEL_ES.getAreaName(), DataSampleBusinessRules.STATUSES_LABEL_ES.getTagName(), true); 
               if (stList.length()==0) stList=DataSample.SAMPLE_STATUSES_LABEL_ES_WHEN_NO_PROPERTY;
               break;
           default:
               stList = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.STATUSES.getAreaName(), DataSampleBusinessRules.STATUSES.getTagName()); 
               if (stList.length()==0) stList=DataSample.SAMPLE_STATUSES_WHEN_NO_PROPERTY;
               break;
       }        
        return LPTestingOutFormat.csvExtractFieldValueStringArr(stList);
    }
    
}
