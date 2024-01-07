/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.clinicalstudies.logic;

import module.clinicalstudies.apis.GenomaStudyAPI.GenomaStudyAPIactionsEndPoints;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPArray;
import org.json.simple.JSONArray;
import trazit.session.ClassControllerActionsEndpointForTesting;

/**
 *
 * @author User
 */
public class ClassStudyController implements ClassControllerActionsEndpointForTesting {

    private StringBuilder rowArgsRows = new StringBuilder(0);
    private Object[] argsWithNamesAndValues;
    private Object[] functionDiagn = new Object[0];
    private JSONArray functionRelatedObjects = new JSONArray();
    private Boolean functionFound = false;

    public ClassStudyController(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs) {
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
            this.rowArgsRows = this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsForLogFiles));
            ClassStudy clss = new ClassStudy(request, endPoint);
            this.functionDiagn = clss.getDiagnostic();
            this.functionRelatedObjects = clss.getRelatedObj().getRelatedObject();
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
    public Object[] getArgsWithNamesAndValues() {return argsWithNamesAndValues;}
}
