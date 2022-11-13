/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.investigation;

import com.labplanet.servicios.app.InvestigationAPI;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPArray;
import org.json.simple.JSONArray;

/**
 *
 * @author User
 */
public class ClassInvestigationController {
    
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private Object[] functionDiagn=new Object[0];
    private JSONArray functionRelatedObjects=new JSONArray();
    private Boolean functionFound=false;
    
    public ClassInvestigationController(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs) {
        
        Object[] argsForLogFiles=new Object[0];
        InvestigationAPI.InvestigationAPIactionsEndpoints endPoint = null;
        try{
//            request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, actionName);
/*                AuditAndUserValidation auditAndUsrValid=AuditAndUserValidation.getInstanceForActions(request, null, "en");
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditAndUsrValid.getCheckUserValidationPassesDiag()[0].toString())){
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, null, auditAndUsrValid.getCheckUserValidationPassesDiag());              
                    auditAndUsrValid.killInstance();
                    return;          
                }                  
*/            
            endPoint = InvestigationAPI.InvestigationAPIactionsEndpoints.valueOf(actionName.toUpperCase());
            HashMap<HttpServletRequest, Object[]> hmQuery = 
                    endPoint.testingSetAttributesAndBuildArgsArray(request, testingContent, iLines);
            HttpServletRequest query= hmQuery.keySet().iterator().next();   
            argsForLogFiles = hmQuery.get(query);
            for (int inumArg=argsForLogFiles.length+3;inumArg<table1NumArgs;inumArg++){
                argsForLogFiles=LPArray.addValueToArray1D(argsForLogFiles, "");
            }
            this.functionFound=true;
            this.rowArgsRows=this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsForLogFiles));
            ClassInvestigation clss=new ClassInvestigation(request, endPoint);
            this.functionDiagn=clss.getDiagnostic();
            this.functionRelatedObjects=clss.getRelatedObj().getRelatedObject();  
//            auditAndUsrValid.killInstance();
        } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StringBuilder getRowArgsRows() {        return rowArgsRows;    }

    public Object getFunctionDiagn() {        return functionDiagn;    }

    public JSONArray getFunctionRelatedObjects() {        return functionRelatedObjects;    }

    public Boolean getFunctionFound() {        return functionFound;    }
}
