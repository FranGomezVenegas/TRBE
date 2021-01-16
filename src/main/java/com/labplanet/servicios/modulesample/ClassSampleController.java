/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPArray;
import org.json.simple.JSONArray;

/**
 *
 * @author User
 */
public class ClassSampleController {
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private Object[] functionDiagn=new Object[0];
    private JSONArray functionRelatedObjects=new JSONArray();
    private Boolean functionFound=false;

    public ClassSampleController(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs) {
        
        Object[] argsForLogFiles=new Object[0];
        SampleAPIParams.SampleAPIEndpoints endPoint = null;
                try{
                    endPoint = SampleAPIParams.SampleAPIEndpoints.valueOf(actionName.toUpperCase());
                    HashMap<HttpServletRequest, Object[]> hmQuery = endPoint.testingSetAttributesAndBuildArgsArray(request, testingContent, iLines);
                    HttpServletRequest query= hmQuery.keySet().iterator().next();   
                    argsForLogFiles = hmQuery.get(query);                    
/*                    endPoint = SampleAPIParams.SampleAPIEndpoints.valueOf(actionName.toString().toUpperCase());
                    switch (endPoint){
                        case LOGSAMPLE:
                            request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_TEMPLATE, LPTestingOutFormat.getAttributeValue(testingContent[iLines][6], testingContent) );
                            request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_TEMPLATE_VERSION, LPTestingOutFormat.getAttributeValue(testingContent[iLines][7], testingContent) );
                            request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPTestingOutFormat.getAttributeValue(testingContent[iLines][8], testingContent) );
                            request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPTestingOutFormat.getAttributeValue(testingContent[iLines][9], testingContent) );                    
                            request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_NUM_SAMPLES_TO_LOG, LPTestingOutFormat.getAttributeValue(testingContent[iLines][12], testingContent));   
                            argsForLogFiles=new Object[]{GlobalAPIsParams.REQUEST_PARAM_SAMPLE_TEMPLATE+":"+LPNulls.replaceNull(testingContent[iLines][6]).toString(), GlobalAPIsParams.REQUEST_PARAM_SAMPLE_TEMPLATE_VERSION+":"+LPNulls.replaceNull(testingContent[iLines][7]).toString(), 
                                GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME+":"+LPNulls.replaceNull(testingContent[iLines][8]).toString(), GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE+":"+LPNulls.replaceNull(testingContent[iLines][9]).toString(), 
                                GlobalAPIsParams.REQUEST_PARAM_NUM_SAMPLES_TO_LOG+":"+LPNulls.replaceNull(testingContent[iLines][10]).toString()    
                            };
                            break;
                        case SETSAMPLINGDATE:
                            request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPTestingOutFormat.getAttributeValue(testingContent[iLines][6], testingContent) );
                            argsForLogFiles=new Object[]{GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID+":"+LPNulls.replaceNull(testingContent[iLines][6]).toString()};
                            break;
                    } 
*/                    
                    for (int inumArg=argsForLogFiles.length+4;inumArg<table1NumArgs;inumArg++){
                        argsForLogFiles=LPArray.addValueToArray1D(argsForLogFiles, "");
                    }
                    this.functionFound=true;
                    this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsForLogFiles));
                    ClassSample clss=new ClassSample(request, endPoint);
                    this.functionDiagn=clss.getDiagnostic();
                    this.functionRelatedObjects=clss.getRelatedObj().getRelatedObject();  
                }catch(Exception e){
                    //Object[] diagnostic=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()});                    
                }    

    }

    /**
     * @return the rowArgsRows
     */
    public StringBuilder getRowArgsRows() {
        return rowArgsRows;
    }

    /**
     * @return the functionDiagn
     */
    public Object getFunctionDiagn() {
        return functionDiagn;
    }

    /**
     * @return the functionRelatedObjects
     */
    public JSONArray getFunctionRelatedObjects() {
        return functionRelatedObjects;
    }

    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
    }    
    
}
