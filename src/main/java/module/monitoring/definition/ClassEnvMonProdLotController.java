/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.definition;

import com.labplanet.servicios.moduleenvmonit.EnvMonProdLotAPI.EnvMonProdLotAPIactionsEndpoints;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import trazit.enums.ActionsClass;
import trazit.session.InternalMessage;
import trazit.enums.EnumIntEndpoints;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
/**
 *
 * @author User
 */
public class ClassEnvMonProdLotController implements ActionsClass{
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private InternalMessage diagnosticObj=null;
    private RelatedObjects functionRelatedObjects=null;
    private Boolean functionFound=false;
    private EnumIntEndpoints enumConstantByName;
    private HttpServletResponse response;
    
    public ClassEnvMonProdLotController(HttpServletRequest request, HttpServletResponse response, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {
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
            this.enumConstantByName = EnvMonProdLotAPIactionsEndpoints.valueOf(actionName.toUpperCase());
            this.functionFound = true;
        } catch (Exception ex) {
            this.functionFound = false;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override public void createClassEnvMonAndHandleExceptions(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {
        ClassEnvMonProdLot clss = null;
        try {            
            EnvMonProdLotAPIactionsEndpoints endPoint = EnvMonProdLotAPIactionsEndpoints.valueOf(actionName.toUpperCase());
            ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, null);
            if (Boolean.TRUE.equals(procReqInstance.getIsForTesting())){
                HashMap<HttpServletRequest, Object[]> hmQuery = endPoint.testingSetAttributesAndBuildArgsArray(request, testingContent, iLines, auditReasonPosic);
                HttpServletRequest query= hmQuery.keySet().iterator().next();   
                Object[] argsForLogFiles = hmQuery.get(query);
                for (int inumArg=argsForLogFiles.length+3;inumArg<table1NumArgs;inumArg++){
                    argsForLogFiles=LPArray.addValueToArray1D(argsForLogFiles, "");
                }
                this.rowArgsRows=this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsForLogFiles));
            }
            this.functionFound=true;
            this.enumConstantByName=endPoint;
            
                        
            clss = new ClassEnvMonProdLot(request, endPoint);
            this.diagnosticObj = clss.getDiagnosticObj();
            this.functionRelatedObjects = clss.getRelatedObj();
        } catch (Exception ex) {
            this.functionRelatedObjects = RelatedObjects.getInstanceForActions();
            if (clss != null && clss.getDiagnosticObj() != null) {
                this.diagnosticObj = clss.getDiagnosticObj();
            } else {
                this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.EXCEPTION_RAISED, new Object[]{ex.getMessage()});
            }            
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }    

    public StringBuilder getRowArgsRows() {        return rowArgsRows;    }
    @Override    public InternalMessage getDiagnosticObj() {        return diagnosticObj;    }
    @Override    public RelatedObjects getRelatedObj() {        return functionRelatedObjects;    }
    @Override    public Object[] getDiagnostic() {        return null;    }
    @Override    public Object[] getMessageDynamicData() {        return diagnosticObj.getMessageCodeVariables();    }
    public Boolean getFunctionFound() {        return functionFound;    }
    @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }

    @Override
    public HttpServletResponse getHttpResponse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

                

