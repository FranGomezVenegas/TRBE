/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
import trazit.enums.ActionsClass;
import trazit.session.InternalMessage;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassSampleQueriesController implements ActionsClass{
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private InternalMessage diagnosticObj=null;
    private RelatedObjects functionRelatedObjects=null;
    private Boolean functionFound=false;
    private Boolean isSuccess=false;
    private Object[] argsWithNamesAndValues;
    ClassSampleQueries clssObj=null;
    private EnumIntEndpoints enumConstantByName;
    
    public ClassSampleQueriesController(HttpServletRequest request, HttpServletResponse response, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {        
        SampleAPIParams.SampleAPIqueriesEndpoints endPoint = null;
        try{
            endPoint = SampleAPIParams.SampleAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
            this.functionFound=true;
            this.enumConstantByName=endPoint;
            if (table1NumArgs!=null){
                HashMap<HttpServletRequest, Object[]> hmQuery = endPoint.testingSetAttributesAndBuildArgsArray(request, testingContent, iLines);
                HttpServletRequest query= hmQuery.keySet().iterator().next();   
                argsWithNamesAndValues = hmQuery.get(query);
                for (int inumArg=argsWithNamesAndValues.length+3;inumArg<table1NumArgs;inumArg++){
                    argsWithNamesAndValues=LPArray.addValueToArray1D(argsWithNamesAndValues, "");
                }
                this.rowArgsRows=this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsWithNamesAndValues));
            }
            ClassSampleQueries clss=new ClassSampleQueries(request, response, endPoint);
            this.clssObj=clss;
            this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, TrazitUtilitiesErrorTrapping.QUERIES_HAVE_NO_MSG_CODE, null);
            this.functionRelatedObjects=clss.getRelatedObj();
            
/*            if (Boolean.TRUE.equals(clss.getIsSuccess())){
                if (clss.getResponseSuccessJArr()!=null)//&&Boolean.FALSE.equals(clss.getResponseSuccessJArr().isEmpty()))
                    LPFrontEnd.servletReturnSuccess(request, response, clss.getResponseSuccessJArr());
                if (clss.getResponseSuccessJObj()!=null)//&&Boolean.FALSE.equals(clss.getResponseSuccessJObj().isEmpty()))
                    LPFrontEnd.servletReturnSuccess(request, response, clss.getResponseSuccessJObj());
            
                this.functionDiagn=
            }else
                this.functionDiagn=clss.getDiagnostic();*/
        } catch (Exception ex) {            
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    public StringBuilder getRowArgsRows() {        return rowArgsRows;    }
    @Override    public InternalMessage getDiagnosticObj() {        return diagnosticObj;    }
    @Override    public RelatedObjects getRelatedObj() {        return functionRelatedObjects;    }
    public Boolean getFunctionFound() {        return functionFound;    }
    @Override    public Object[] getDiagnostic() {        return null;    }
    @Override    public Object[] getMessageDynamicData() {        return diagnosticObj.getMessageCodeVariables();    }
    public Object[] getArgsWithNamesAndValues() {return argsWithNamesAndValues;}
    @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }
}

                

