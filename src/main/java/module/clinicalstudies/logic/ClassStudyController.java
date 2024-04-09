/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.clinicalstudies.logic;

import functionaljavaa.responserelatedobjects.RelatedObjects;
import module.clinicalstudies.apis.GenomaStudyAPI.GenomaStudyAPIactionsEndPoints;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPArray;
import trazit.enums.ActionsClass;
import trazit.session.InternalMessage;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassStudyController implements ActionsClass {

    private StringBuilder rowArgsRows = new StringBuilder(0);
    private Object[] argsWithNamesAndValues;
    private InternalMessage diagnosticObj=null;
    private RelatedObjects functionRelatedObjects=null;
    private Boolean functionFound = false;
    private EnumIntEndpoints enumConstantByName;
    public ClassStudyController(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {
        Object[] argsForLogFiles = new Object[0];
        GenomaStudyAPIactionsEndPoints endPoint = null;
        try {
            endPoint = GenomaStudyAPIactionsEndPoints.valueOf(actionName.toUpperCase());
            HashMap<HttpServletRequest, Object[]> hmQuery = endPoint.testingSetAttributesAndBuildArgsArray(request, testingContent, iLines);
            HttpServletRequest query = hmQuery.keySet().iterator().next();
            argsForLogFiles = hmQuery.get(query);
            argsWithNamesAndValues = argsForLogFiles;

            for (int inumArg = argsForLogFiles.length + 3; inumArg < table1NumArgs; inumArg++) {
                argsForLogFiles = LPArray.addValueToArray1D(argsForLogFiles, "");
            }
            this.functionFound = true;
            this.enumConstantByName=endPoint;
            this.rowArgsRows = this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsForLogFiles));
            ClassStudy clss = new ClassStudy(request, endPoint);
            this.diagnosticObj = clss.getDiagnosticObj();
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
