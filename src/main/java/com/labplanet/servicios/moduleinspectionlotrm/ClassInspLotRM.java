/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleinspectionlotrm;

//import com.labplanet.servicios.moduleenvmonit.*;
import com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.InspLotRMAPIEndpoints;
import databases.Rdbms;
import databases.TblsProcedure;
import databases.Token;
import functionaljavaa.audit.AuditAndUserValidation;
import functionaljavaa.batch.incubator.DataBatchIncubator;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramSample;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.time.LocalDateTime;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author User
 */
public class ClassInspLotRM {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstance();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;

    public ClassInspLotRM(HttpServletRequest request, Token token, String schemaPrefix, InspLotRMAPIEndpoints endPoint, AuditAndUserValidation auditAndUsrValid){
        RelatedObjects rObj=RelatedObjects.getInstance();

        DataProgramSample prgSmp = new DataProgramSample();     
        
        Object[] actionDiagnoses = null;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());        
        this.functionFound=true;
            switch (endPoint){
                case NEW_LOT:
                    LocalDateTime dateStart=(LocalDateTime) argValues[0];
                    LocalDateTime dateEnd=(LocalDateTime) argValues[1];
                    String programName = argValues[2].toString();
                    actionDiagnoses=prgSmp.logProgramSampleScheduled(schemaPrefix, token, programName, dateStart, dateEnd);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{dateStart, dateEnd, programName, schemaPrefix});                                        
                    this.messageDynamicData=new Object[]{};
                    break;
            }    
        this.diagnostic=actionDiagnoses;
        this.relatedObj=rObj;
        rObj.killInstance();
    }
    
    /**
     * @return the messageDynamicData
     */
    public Object[] getMessageDynamicData() {
        return messageDynamicData;
    }

    /**
     * @return the relatedObj
     */
    public RelatedObjects getRelatedObj() {
        return relatedObj;
    }

    /**
     * @return the endpointExists
     */
    public Boolean getEndpointExists() {
        return endpointExists;
    }

    /**
     * @return the diagnostic
     */
    public Object[] getDiagnostic() {
        return diagnostic;
    }

    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
    }
    
}
