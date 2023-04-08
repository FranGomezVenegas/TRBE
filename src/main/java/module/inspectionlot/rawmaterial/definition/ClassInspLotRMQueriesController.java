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
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntQueriesEndpoints;
import trazit.enums.EnumIntQueriesObj;
import trazit.session.ApiMessageReturn;

/**
 *
 * @author User
 */
public class ClassInspLotRMQueriesController implements EnumIntQueriesEndpoints{
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private Object[] functionDiagn=new Object[0];
    private JSONArray functionRelatedObjects=new JSONArray();
    private Boolean functionFound=false;
    private Boolean isSuccess=false;
    ClassInspLotRMQueries clss=null;
    public ClassInspLotRMQueriesController(HttpServletRequest request, HttpServletResponse response, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs) {
    
        Object[] argsForLogFiles=new Object[0];
        InspLotRMEnums.InspLotRMQueriesAPIEndpoints endPoint = null;
        try{
            endPoint = InspLotRMEnums.InspLotRMQueriesAPIEndpoints.valueOf(actionName.toUpperCase());
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
            ClassInspLotRMQueries clss=new ClassInspLotRMQueries(request, response, endPoint);
            this.functionDiagn=clss.getDiagnostic();
            if (Boolean.TRUE.equals(clss.getIsSuccess()))
                this.functionDiagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "queriesHaveNoMsgCode", null);
            else
                this.functionDiagn=clss.getDiagnostic(); 
            
            this.functionRelatedObjects=clss.getRelatedObj().getRelatedObject();               
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
        return this.clss;
    }
}

                

