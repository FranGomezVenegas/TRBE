/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPArray;
import trazit.enums.ActionsClass;
import trazit.session.InternalMessage;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassSampleController implements ActionsClass{
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private InternalMessage diagnosticObj=null;
    private RelatedObjects functionRelatedObjects=null;
    private Boolean functionFound=false;
    private EnumIntEndpoints enumConstantByName;
    
    public ClassSampleController(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {        
        Object[] argsForLogFiles=new Object[0];
        SampleAPIParams.SampleAPIactionsEndpoints endPoint = null;
        try{
            endPoint = SampleAPIParams.SampleAPIactionsEndpoints.valueOf(actionName.toUpperCase());            
            HashMap<HttpServletRequest, Object[]> hmQuery = endPoint.testingSetAttributesAndBuildArgsArray(request, testingContent, iLines);
            HttpServletRequest query= hmQuery.keySet().iterator().next();   
            argsForLogFiles = hmQuery.get(query);                    
            for (int inumArg=argsForLogFiles.length+3;inumArg<table1NumArgs;inumArg++){
                argsForLogFiles=LPArray.addValueToArray1D(argsForLogFiles, "");
            }
            this.functionFound=true;
            this.enumConstantByName=endPoint;
            this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsForLogFiles));
            ClassSample clss=new ClassSample(request, endPoint);
            this.diagnosticObj=clss.getDiagnosticObj();
            this.functionRelatedObjects=clss.getRelatedObj();
        }catch(Exception e){
            this.functionFound=false;
        }
    }

    public StringBuilder getRowArgsRows() {        return rowArgsRows;    }
    @Override    public InternalMessage getDiagnosticObj() {        return diagnosticObj;    }
    @Override    public RelatedObjects getRelatedObj() {        return functionRelatedObjects;    }
    @Override    public Object[] getDiagnostic() {        return null;    }
    @Override    public Object[] getMessageDynamicData() {        return diagnosticObj.getMessageCodeVariables();    }
    public Boolean getFunctionFound() {        return functionFound;    }    
    @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }
    
}
