/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.definition;

import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.session.ApiMessageReturn;

/**
 *
 * @author User
 */
public class ClassEnvMonSampleFrontendController {
    
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private Object[] functionDiagn=new Object[0];
    private JSONArray functionRelatedObjects=new JSONArray();
    private Boolean functionFound=false;    
    
    public ClassEnvMonSampleFrontendController(HttpServletRequest request, HttpServletResponse response, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs) {
        
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
            this.functionFound=true;
            this.rowArgsRows=this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsForLogFiles));
            ClassEnvMonSampleFrontend clss=new ClassEnvMonSampleFrontend(request, endPoint);
            if (Boolean.TRUE.equals(clss.getIsSuccess()))
                this.functionDiagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "queriesHaveNoMsgCode", null);
            else
                this.functionDiagn=clss.getDiagnostic(); //LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "queriesHaveNoMsgCode", null);
            //this.functionDiagn=clss.getDiagnostic();
            this.functionRelatedObjects=clss.getRelatedObj().getRelatedObject(); 
            if (clss.getResponseSuccessJArr()!=null && (Boolean.FALSE.equals(clss.getResponseSuccessJArr().isEmpty())) ){
                LPFrontEnd.servletReturnSuccess(request, response, clss.getResponseSuccessJArr());
            }else{
                LPFrontEnd.servletReturnSuccess(request, response, clss.getResponseSuccessJObj());
            }
            
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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

                

