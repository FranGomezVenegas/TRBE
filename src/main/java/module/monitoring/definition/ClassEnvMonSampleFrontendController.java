/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.definition;

import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import org.json.simple.JSONArray;
import trazit.enums.ActionsClass;
import trazit.session.InternalMessage;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassEnvMonSampleFrontendController implements ActionsClass{
    
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private InternalMessage diagnosticObj=null;
    private RelatedObjects functionRelatedObjects=null;
    private Boolean functionFound=false;    
    private EnumIntEndpoints enumConstantByName;
    
    public ClassEnvMonSampleFrontendController(HttpServletRequest request, HttpServletResponse response, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {
        
        Object[] argsForLogFiles=new Object[0];
        ClassEnvMonSampleFrontend.EnvMonSampleAPIqueriesEndpoints endPoint = null;
        try{
            endPoint = ClassEnvMonSampleFrontend.EnvMonSampleAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
            if (testingContent!=null){
                Map<HttpServletRequest, Object[]> hmQuery = endPoint.testingSetAttributesAndBuildArgsArray(request, testingContent, iLines);
                HttpServletRequest query= hmQuery.keySet().iterator().next();   
                argsForLogFiles = hmQuery.get(query);
                for (int inumArg=argsForLogFiles.length+3;inumArg<table1NumArgs;inumArg++){
                    argsForLogFiles=LPArray.addValueToArray1D(argsForLogFiles, "");
                }
            }
            this.enumConstantByName=endPoint;
            this.functionFound=true;
            this.rowArgsRows=this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsForLogFiles));
            ClassEnvMonSampleFrontend clss=new ClassEnvMonSampleFrontend(request, endPoint);
            if (Boolean.TRUE.equals(clss.getIsSuccess()))
                this.diagnosticObj=new InternalMessage(LPPlatform.LAB_TRUE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.QUERIES_HAVE_NO_MSG_CODE, null);
            else
                this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE,  TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.QUERIES_HAVE_NO_MSG_CODE, null);
            if (clss.getResponseSuccessJArr()!=null && (Boolean.FALSE.equals(clss.getResponseSuccessJArr().isEmpty())) ){
                LPFrontEnd.servletReturnSuccess(request, response, clss.getResponseSuccessJArr());
            }else{
                LPFrontEnd.servletReturnSuccess(request, response, clss.getResponseSuccessJObj());
            }
            
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    public StringBuilder getRowArgsRows() {        return rowArgsRows;    }
    public InternalMessage getFunctionDiagn() {        return diagnosticObj;    }
    public JSONArray getFunctionRelatedObjects() {        return functionRelatedObjects.getRelatedObject();    }
    public Boolean getFunctionFound() {        return functionFound;    }
    @Override    public InternalMessage getDiagnosticObj() {        return diagnosticObj;    }
    @Override    public RelatedObjects getRelatedObj() {        return functionRelatedObjects;    }
    @Override    public Object[] getDiagnostic() {        return null;    }
    @Override    public Object[] getMessageDynamicData() {        return diagnosticObj.getMessageCodeVariables();}    
    @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }
    
}