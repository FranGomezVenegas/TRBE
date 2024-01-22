/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.definition;

import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPArray;
import org.json.simple.JSONArray;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public class ClassInspLotRMController {
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private Object[] functionDiagn=null;
    private JSONArray functionRelatedObjects=new JSONArray();
    private Boolean functionFound=false;
    private InternalMessage functionDiagnObj=null;
    
    public ClassInspLotRMController(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs) {
        
        Object[] argsForLogFiles=new Object[0];
        InspLotRMEnums.InspLotRMAPIactionsEndpoints endPoint = null;
        try{
            endPoint = InspLotRMEnums.InspLotRMAPIactionsEndpoints.valueOf(actionName.toUpperCase());
                    HashMap<HttpServletRequest, Object[]> hmQuery = endPoint.testingSetAttributesAndBuildArgsArray(request, testingContent, iLines);
                    HttpServletRequest query= hmQuery.keySet().iterator().next();   
                    argsForLogFiles = hmQuery.get(query);
            for (int inumArg=argsForLogFiles.length+3;inumArg<table1NumArgs;inumArg++){
                argsForLogFiles=LPArray.addValueToArray1D(argsForLogFiles, "");
            }
            this.functionFound=true;
            this.rowArgsRows=this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsForLogFiles));            
            ClassInspLotRMactions clss=new ClassInspLotRMactions(request, endPoint);
            this.functionDiagn=clss.getDiagnostic();
            this.functionDiagnObj=clss.getDiagnosticObj();
            this.functionRelatedObjects=clss.getRelatedObj().getRelatedObject();              
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
    public Object[] getFunctionDiagn() {
        return functionDiagn;
    }
    public InternalMessage getFunctionDiagnObj() {
        return functionDiagnObj;
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

                

