/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package module.methodvalidation.definition;

import functionaljavaa.samplestructure.DataSample;
import org.json.simple.JSONObject;

public interface MethodParamsHandler {    
    JSONObject paramDataForQuery(String curProjName, String paramName, String sequenceName, String analyticalParameter, String procInstanceName);
    
    void calcParamResults(Integer resultId, Integer testId, Integer sampleId, DataSample dataSample, String analyticalParameter, String parameterName, String sequenceName, String project);
    
}
