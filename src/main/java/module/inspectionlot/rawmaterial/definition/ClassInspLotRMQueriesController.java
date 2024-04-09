/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.definition;

import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import trazit.enums.ActionsClass;
import trazit.session.InternalMessage;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassInspLotRMQueriesController  implements ActionsClass{
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private InternalMessage diagnosticObj=null;
    private RelatedObjects functionRelatedObjects=null;
    private Object[] argsWithNamesAndValues;
    private Boolean functionFound=false;
    private Boolean isSuccess=false;
    ClassInspLotRMQueries clss=null;
    private EnumIntEndpoints enumConstantByName;
    public ClassInspLotRMQueriesController(HttpServletRequest request, HttpServletResponse response, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {
        InspLotRMEnums.InspLotRMQueriesAPIEndpoints endPoint = null;
        try{
            endPoint = InspLotRMEnums.InspLotRMQueriesAPIEndpoints.valueOf(actionName.toUpperCase());
            this.enumConstantByName=endPoint;
            this.functionFound=true;
            if (table1NumArgs!=null){
                HashMap<HttpServletRequest, Object[]> hmQuery = endPoint.testingSetAttributesAndBuildArgsArray(request, testingContent, iLines);
                HttpServletRequest query= hmQuery.keySet().iterator().next();   
                argsWithNamesAndValues = hmQuery.get(query);
                for (int inumArg=argsWithNamesAndValues.length+3;inumArg<table1NumArgs;inumArg++){
                    argsWithNamesAndValues=LPArray.addValueToArray1D(argsWithNamesAndValues, "");
                }
                this.rowArgsRows=this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsWithNamesAndValues));
            }
            ClassInspLotRMQueries clss=new ClassInspLotRMQueries(request, response, endPoint);
            this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.QUERIES_HAVE_NO_MSG_CODE, null);
            this.functionRelatedObjects=clss.getRelatedObj();
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

                

