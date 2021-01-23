/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.TblsData;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.samplestructure.DataSample;
import functionaljavaa.samplestructure.DataSampleAnalysis;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class SampleAPIlogic {
    public static JSONObject performAction  (HttpServletRequest request, DataSample smp, SampleAPIParams.SampleAPIEndpoints endPoint, RelatedObjects rObj)  {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
    
        //case TESTASSIGNMENT:
        String objectIdStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TEST_ID);
        int testId = Integer.parseInt(objectIdStr);     
                    String newAnalyst = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_NEW_ANALYST);
        Object[] dataSample = DataSampleAnalysis.sampleAnalysisAssignAnalyst(testId, newAnalyst, smp);
        Object sampleId=null;
                   rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);                            
        Object[] messageDynamicData = new Object[]{sampleId}; 
                    //break;                               
        JSONObject dataSampleJSONMsg=new JSONObject();

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dataSample[0].toString())){  
        }else{
            dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue("this.getClass().getSimpleName()", 
                    endPoint.getSuccessMessageCode(), messageDynamicData, rObj.getRelatedObject());
        }
        rObj.killInstance();
        return dataSampleJSONMsg;
    }   
}
