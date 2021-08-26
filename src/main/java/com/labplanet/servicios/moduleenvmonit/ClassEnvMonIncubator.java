/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import databases.Token;
import functionaljavaa.instruments.incubator.ConfigIncubator;
import functionaljavaa.instruments.incubator.DataIncubatorNoteBook;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class ClassEnvMonIncubator {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;

    public ClassEnvMonIncubator(HttpServletRequest request, EnvMonIncubationAPI.EnvMonIncubationAPIEndpoints endPoint){
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        Object[] actionDiagnoses = null;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());    
        String instrName="";
        BigDecimal temperature=null;
        this.functionFound=true;
        switch (endPoint){
                case EM_INCUBATION_ACTIVATE:
                    instrName=argValues[0].toString();               
                    actionDiagnoses=ConfigIncubator.activateIncubator(instrName, token.getPersonName());
                    rObj.addSimpleNode(GlobalVariables.Schemas.CONFIG.getName(), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), instrName);                
                    this.messageDynamicData=new Object[]{instrName};
                    break;
                case EM_INCUBATION_DEACTIVATE:
                    instrName=argValues[0].toString();
                    actionDiagnoses=ConfigIncubator.deactivateIncubator(instrName, token.getPersonName());
                    rObj.addSimpleNode(GlobalVariables.Schemas.CONFIG.getName(), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), instrName);                
                    this.messageDynamicData=new Object[]{instrName};
                    break;
                case EM_INCUBATION_ADD_TEMP_READING:
                    instrName=argValues[0].toString();
                    temperature=(BigDecimal) argValues[1];
                    actionDiagnoses=DataIncubatorNoteBook.newTemperatureReading(instrName, token.getPersonName(),temperature);                    
                    rObj.addSimpleNode(GlobalVariables.Schemas.CONFIG.getName(), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), instrName);                
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), "instrument_incubator_notebook", actionDiagnoses[actionDiagnoses.length-1]);                
                    this.messageDynamicData=new Object[]{temperature, instrName};
                    break;      
        }
        if (actionDiagnoses!=null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
            actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{instrName, temperature});
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
