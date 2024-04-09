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
import trazit.session.InternalMessage;
/**
 *
 * @author User
 */
public final class SampleAPIlogic {
    private SampleAPIlogic() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static JSONObject performAction  (HttpServletRequest request, DataSample smp, SampleAPIParams.SampleAPIactionsEndpoints endPoint, RelatedObjects rObj)  {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
    
        //case TESTASSIGNMENT:
        String objectIdStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TEST_ID);
        int testId = Integer.parseInt(objectIdStr);     
                    String newAnalyst = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_NEW_ANALYST);
        InternalMessage dataSample = DataSampleAnalysis.sampleAnalysisAssignAnalyst(testId, newAnalyst);
        Object sampleId=null;
                   rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);                            
        Object[] messageDynamicData = new Object[]{sampleId}; 
        JSONObject dataSampleJSONMsg=new JSONObject();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(dataSample.getDiagnostic()))){  
            dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, messageDynamicData, rObj.getRelatedObject());
        }
        rObj.killInstance();
        return dataSampleJSONMsg;
    }   
}
