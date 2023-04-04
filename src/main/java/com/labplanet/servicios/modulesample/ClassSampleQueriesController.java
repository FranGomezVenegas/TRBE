/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntQueriesEndpoints;
import trazit.enums.EnumIntQueriesObj;
import trazit.session.ApiMessageReturn;

/**
 *
 * @author User
 */
public class ClassSampleQueriesController implements EnumIntQueriesEndpoints{
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private Object[] functionDiagn=new Object[0];
    private JSONArray functionRelatedObjects=new JSONArray();
    private Boolean functionFound=false;
    private Boolean isSuccess=false;
    ClassSampleQueries clssObj=null;
    public ClassSampleQueriesController(HttpServletRequest request, HttpServletResponse response, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs) {
        Object[] argsForLogFiles=new Object[0];
        SampleAPIParams.SampleAPIqueriesEndpoints endPoint = null;
        try{
            endPoint = SampleAPIParams.SampleAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
            this.functionFound=true;
            if (table1NumArgs!=null){
                HashMap<HttpServletRequest, Object[]> hmQuery = endPoint.testingSetAttributesAndBuildArgsArray(request, testingContent, iLines);
                HttpServletRequest query= hmQuery.keySet().iterator().next();   
                argsForLogFiles = hmQuery.get(query);
                for (int inumArg=argsForLogFiles.length+3;inumArg<table1NumArgs;inumArg++){
                    argsForLogFiles=LPArray.addValueToArray1D(argsForLogFiles, "");
                }
                this.rowArgsRows=this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsForLogFiles));
            }
            ClassSampleQueries clss=new ClassSampleQueries(request, response, endPoint);
            this.clssObj=clss;
            this.functionDiagn=clss.getDiagnostic();
            this.functionRelatedObjects=clss.getRelatedObj().getRelatedObject();   
            
            if (Boolean.TRUE.equals(clss.getIsSuccess())){
                if (clss.getResponseSuccessJArr()!=null&&Boolean.FALSE.equals(clss.getResponseSuccessJArr().isEmpty()))
                    LPFrontEnd.servletReturnSuccess(request, response, clss.getResponseSuccessJArr());
                if (clss.getResponseSuccessJObj()!=null&&Boolean.FALSE.equals(clss.getResponseSuccessJObj().isEmpty()))
                    LPFrontEnd.servletReturnSuccess(request, response, clss.getResponseSuccessJObj());
            
                this.functionDiagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "queriesHaveNoMsgCode", null);
            }else
                this.functionDiagn=clss.getDiagnostic();
        } catch (Exception ex) {            
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override    public StringBuilder getRowArgsRows() {
        return rowArgsRows;
    }
    @Override    public Object getFunctionDiagn() {
        return functionDiagn;
    }
    @Override    public JSONArray getFunctionRelatedObjects() {
        return functionRelatedObjects;
    }
    @Override    public Boolean getFunctionFound() {
        return functionFound;
    }
    @Override    public Boolean getIsSuccess() {
        return this.isSuccess;
    }

    @Override
    public EnumIntQueriesObj getQueryRunObj() {
        return this.clssObj;
    }
}

                

