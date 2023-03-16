/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import databases.features.Token;
import functionaljavaa.instruments.incubator.ConfigIncubator;
import functionaljavaa.instruments.incubator.DataIncubatorNoteBook;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;

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

    public ClassEnvMonIncubator(HttpServletRequest request, EnvMonIncubatorAPIactions.EnvMonIncubatorAPIactionsEndpoints endPoint){
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        Object[] actionDiagnoses = null;
        this.functionFound=true;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
            this.diagnostic=(Object[]) argValues[1];
            this.messageDynamicData=new Object[]{argValues[2].toString()};
            return;                        
        }         
        
        String instrName="";
        BigDecimal temperature=null;
        switch (endPoint){
                case EM_INCUBATOR_NEW:
                    instrName=argValues[0].toString();
                    String incubStage=argValues[1].toString();
                    BigDecimal minTemp=BigDecimal.valueOf(Double.valueOf(LPNulls.replaceNull(argValues[2]).toString()));
                    BigDecimal maxTemp=BigDecimal.valueOf(Double.valueOf(LPNulls.replaceNull(argValues[3]).toString()));             
                    String fieldName=argValues[4].toString();
                    String fieldValue=argValues[5].toString();
                    String[] fieldNames=new String[0];
                    Object[] fieldValues=new Object[0];
                    if (fieldName!=null && fieldName.length()>0 && !"undefined".equalsIgnoreCase(fieldName)) fieldNames = fieldName.split("\\|");                                            
                    if (fieldValue!=null && fieldValue.length()>0 && !"undefined".equalsIgnoreCase(fieldValue)) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));                                                                                
                    if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                        actionDiagnoses=fieldValues;
                        break;
                    }                    
                    actionDiagnoses=ConfigIncubator.newIncubator(instrName, incubStage, minTemp, maxTemp, fieldNames, fieldValues);
                    rObj.addSimpleNode(GlobalVariables.Schemas.CONFIG.getName(), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), instrName);                
                    this.messageDynamicData=new Object[]{instrName};
                    break;            
                case EM_INCUBATOR_ACTIVATE:
                    instrName=argValues[0].toString();               
                    actionDiagnoses=ConfigIncubator.activateIncubator(instrName, token.getPersonName());
                    rObj.addSimpleNode(GlobalVariables.Schemas.CONFIG.getName(), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), instrName);                
                    this.messageDynamicData=new Object[]{instrName};
                    break;
                case EM_INCUBATOR_DEACTIVATE:
                    instrName=argValues[0].toString();
                    actionDiagnoses=ConfigIncubator.deactivateIncubator(instrName, token.getPersonName());                    
                    rObj.addSimpleNode(GlobalVariables.Schemas.CONFIG.getName(), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), instrName);                
                    this.messageDynamicData=new Object[]{instrName};
                    break;
                case EM_INCUBATOR_ADD_TEMP_READING:
                    instrName=argValues[0].toString();
                    temperature=(BigDecimal) argValues[1];
                    actionDiagnoses=DataIncubatorNoteBook.newTemperatureReading(instrName, token.getPersonName(),temperature);                    
                    rObj.addSimpleNode(GlobalVariables.Schemas.CONFIG.getName(), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), instrName);                
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK.getTableName(), actionDiagnoses[actionDiagnoses.length-1]);                
                    this.messageDynamicData=new Object[]{temperature, instrName};
                    break;      
        }
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses[0].toString()))
            this.diagnostic=ApiMessageReturn.trapMessage(
                    actionDiagnoses[0].toString(),
                    actionDiagnoses[actionDiagnoses.length-1].toString(), this.messageDynamicData);
        else
            this.diagnostic=ApiMessageReturn.trapMessage(actionDiagnoses[0].toString(),endPoint, new Object[]{instrName, temperature});
        
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
