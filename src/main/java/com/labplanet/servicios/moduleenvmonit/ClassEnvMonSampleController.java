/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import databases.Token;
import functionaljavaa.audit.AuditAndUserValidation;
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
public class ClassEnvMonSampleController {
    
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private Object[] functionDiagn=new Object[0];
    private JSONArray functionRelatedObjects=new JSONArray();
    private Boolean functionFound=false;
    
    public ClassEnvMonSampleController(HttpServletRequest request, Token token, String schemaPrefix, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, AuditAndUserValidation auditAndUsrValid) {
        
        Object[] argsForLogFiles=new Object[0];
        EnvMonSampleAPI.EnvMonSampleAPIEndpoints endPoint = null;
        try{
            endPoint = EnvMonSampleAPI.EnvMonSampleAPIEndpoints.valueOf(actionName.toUpperCase());
                    HashMap<HttpServletRequest, Object[]> hmQuery = endPoint.testingSetAttributesAndBuildArgsArray(request, testingContent, iLines);
                    HttpServletRequest query= hmQuery.keySet().iterator().next();   
                    argsForLogFiles = hmQuery.get(query);
            for (int inumArg=argsForLogFiles.length+4;inumArg<table1NumArgs;inumArg++){
                argsForLogFiles=LPArray.addValueToArray1D(argsForLogFiles, "");
            }
            this.functionFound=true;
            this.rowArgsRows=this.rowArgsRows.append(LPTestingOutFormat.rowAddFields(argsForLogFiles));
            ClassEnvMonSample clss=new ClassEnvMonSample(request, token, schemaPrefix, endPoint, auditAndUsrValid);
            this.functionDiagn=clss.getDiagnostic();
            this.functionRelatedObjects=clss.getRelatedObj().getRelatedObject();  
        } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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

                

