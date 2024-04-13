/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.definition;

import com.labplanet.servicios.moduleenvmonit.EnvMonAPI;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.util.HashMap;
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
import trazit.enums.EnumIntEndpoints;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ClassEnvMonQueriesController implements ActionsClass {
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private InternalMessage diagnosticObj=null;
    private RelatedObjects functionRelatedObjects=null;
    private Boolean functionFound=false;    
    private EnumIntEndpoints enumConstantByName;
    private HttpServletResponse response;
    
    public ClassEnvMonQueriesController(HttpServletRequest request, HttpServletResponse response, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {
        this.response=response;
        initializeEndpoint(actionName);
        if (Boolean.FALSE.equals(this.functionFound)){
            this.functionFound=false;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, "");
            return;
        }
        createClassEnvMonAndHandleExceptions(request, actionName, testingContent, iLines, table1NumArgs, auditReasonPosic);
    }
    @Override public void initializeEndpoint(String actionName) {
        try {
            this.enumConstantByName = EnvMonAPI.EnvMonQueriesAPIEndpoints.valueOf(actionName.toUpperCase());
            this.functionFound = true;
        } catch (Exception ex) {
            this.functionFound = false;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override    public void createClassEnvMonAndHandleExceptions(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {     
        ClassEnvMonQueries clss = null;
        try {            
            EnvMonAPI.EnvMonQueriesAPIEndpoints endPoint = EnvMonAPI.EnvMonQueriesAPIEndpoints.valueOf(actionName.toUpperCase());
            ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, null);
            if (Boolean.TRUE.equals(procReqInstance.getIsForTesting())){            
                HashMap<HttpServletRequest, Object[]> hmQuery = endPoint.testingSetAttributesAndBuildArgsArray(request, testingContent, iLines);
                HttpServletRequest query= hmQuery.keySet().iterator().next();   
                Object[] argsForLogFiles = hmQuery.get(query);
                for (int inumArg=argsForLogFiles.length+3;inumArg<table1NumArgs;inumArg++){
                    argsForLogFiles=LPArray.addValueToArray1D(argsForLogFiles, "");
                }    
                this.rowArgsRows=this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsForLogFiles));
            }
            clss = new ClassEnvMonQueries(request, endPoint, null);            
            this.functionRelatedObjects = clss.getRelatedObj();
            if (Boolean.TRUE.equals(clss.getIsSuccess()))
                this.diagnosticObj=new InternalMessage(LPPlatform.LAB_TRUE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.QUERIES_HAVE_NO_MSG_CODE, null);
            else
                this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE,  TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.QUERIES_HAVE_NO_MSG_CODE, null);
            this.functionRelatedObjects=clss.getRelatedObj();
            procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, null);
            if (Boolean.FALSE.equals(procReqInstance.getIsForTesting())){
                if (clss.getResponseSuccessJArr()!=null && (Boolean.FALSE.equals(clss.getResponseSuccessJArr().isEmpty())) ){
                    LPFrontEnd.servletReturnSuccess(request, this.response, clss.getResponseSuccessJArr());
                }else{
                    LPFrontEnd.servletReturnSuccess(request, this.response, clss.getResponseSuccessJObj());
                }
            }            
        } catch (Exception ex) {
            this.functionRelatedObjects = RelatedObjects.getInstanceForActions();
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

    @Override
    public HttpServletResponse getHttpResponse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

                

