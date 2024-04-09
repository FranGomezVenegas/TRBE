/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.definition;

import com.labplanet.servicios.moduleenvmonit.EnvMonSampleAPI;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPArray;
import org.json.simple.JSONArray;
import trazit.enums.ActionsClass;
import trazit.enums.EnumIntEndpoints;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public class ClassEnvMonSampleController implements ActionsClass{
    
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private InternalMessage diagnosticObj=null;
    private RelatedObjects functionRelatedObjects=null;
    private Boolean functionFound=false;
    private EnumIntEndpoints enumConstantByName;
    public ClassEnvMonSampleController(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {
        
        Object[] argsForLogFiles=new Object[0];
        EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints endPoint = null;
        try{
//            request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, actionName);
/*                AuditAndUserValidation auditAndUsrValid=AuditAndUserValidation.getInstanceForActions(request, null, DEFAULTLANGUAGE);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditAndUsrValid.getCheckUserValidationPassesDiag()[0].toString())){
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, null, auditAndUsrValid.getCheckUserValidationPassesDiag());              
                    auditAndUsrValid.killInstance();
                    return;          
                }                  
*/            
            endPoint = EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints.valueOf(actionName.toUpperCase());
                    HashMap<HttpServletRequest, Object[]> hmQuery = endPoint.testingSetAttributesAndBuildArgsArray(request, testingContent, iLines, auditReasonPosic);
                    HttpServletRequest query= hmQuery.keySet().iterator().next();   
                    argsForLogFiles = hmQuery.get(query);
            for (int inumArg=argsForLogFiles.length+3;inumArg<table1NumArgs;inumArg++){
                argsForLogFiles=LPArray.addValueToArray1D(argsForLogFiles, "");
            }
            this.functionFound=true;
            this.enumConstantByName=endPoint;
            this.rowArgsRows=this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsForLogFiles));
            ClassEnvMonSample clss=new ClassEnvMonSample(request, endPoint);            
            this.diagnosticObj=clss.getDiagnosticObj();
            this.functionRelatedObjects=clss.getRelatedObj();
            //this.functionRelatedObjects=clss.getRelatedObj().getRelatedObject();  
//            auditAndUsrValid.killInstance();
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
    @Override    public Object[] getMessageDynamicData() {        return diagnosticObj.getMessageCodeVariables();    }
    @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }
}

                

