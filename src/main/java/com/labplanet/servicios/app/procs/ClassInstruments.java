/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app.procs;

import databases.TblsAppProcData;
import functionaljavaa.instruments.DataInstruments;
import functionaljavaa.instruments.InstrumentsEnums.InstrumentsAPIactionsEndpoints;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ClassInstruments {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;

    public ClassInstruments(HttpServletRequest request, InstrumentsAPIactionsEndpoints endPoint){
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        InternalMessage actionDiagnoses = null;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());        
        String instrName=argValues[0].toString();
        DataInstruments instr=new DataInstruments(instrName);
        this.functionFound=true;
            switch (endPoint){
                case NEW_INSTRUMENT:
                    String fieldName=argValues[1].toString();
                    String fieldValue=argValues[2].toString();
                    String[] fieldNames=null;
                    Object[] fieldValues=null;
                    if (fieldName!=null) fieldNames = fieldName.split("\\|");
                    if (fieldValue!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        actionDiagnoses=DataInstruments.createNewInstrument(instrName, fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                            TblsAppProcData.Instruments.TBL.getName(), instrName);                
                    break;
                case UPDATE_INSTRUMENT:
                    fieldName=argValues[1].toString();
                    fieldValue=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fieldName!=null) fieldNames = fieldName.split("\\|");
                    if (fieldValue!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        actionDiagnoses=instr.updateInstrument(fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                            TblsAppProcData.Instruments.TBL.getName(), instrName);                
                    break;
                case DECOMMISSION_INSTRUMENT:
                    fieldName=argValues[1].toString();
                    fieldValue=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fieldName!=null) fieldNames = fieldName.split("\\|");
                    if (fieldValue!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        actionDiagnoses=instr.decommissionInstrument(fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                            TblsAppProcData.Instruments.TBL.getName(), instrName);                
                    break;
                case UNDECOMMISSION_INSTRUMENT:
                    fieldName=argValues[1].toString();
                    fieldValue=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fieldName!=null) fieldNames = fieldName.split("\\|");
                    if (fieldValue!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        actionDiagnoses=instr.unDecommissionInstrument(fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                            TblsAppProcData.Instruments.TBL.getName(), instrName);                
                    break;
                case TURN_ON_LINE:
                    fieldName=argValues[1].toString();
                    fieldValue=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fieldName!=null) fieldNames = fieldName.split("\\|");
                    if (fieldValue!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        actionDiagnoses=instr.turnOnLine(fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                            TblsAppProcData.Instruments.TBL.getName(), instrName);                
                    break;
                case TURN_OFF_LINE:
                    fieldName=argValues[1].toString();
                    fieldValue=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fieldName!=null) fieldNames = fieldName.split("\\|");
                    if (fieldValue!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        instr=new DataInstruments(instrName);
                    actionDiagnoses=instr.turnOffLine(fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                            TblsAppProcData.Instruments.TBL.getName(), instrName);                
                    break;
                case START_CALIBRATION:
                    instr=new DataInstruments(instrName);
                    actionDiagnoses=instr.startCalibration();
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                            TblsAppProcData.Instruments.TBL.getName(), instrName);                
                    break;
                case COMPLETE_CALIBRATION:
                    instr=new DataInstruments(instrName);
                    String decision=argValues[3].toString();
                    actionDiagnoses=instr.completeCalibration(decision);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                            TblsAppProcData.Instruments.TBL.getName(), instrName);                
                    break;
                case START_PREV_MAINT:
                    instr=new DataInstruments(instrName);
                    actionDiagnoses=instr.startPrevMaint();
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                            TblsAppProcData.Instruments.TBL.getName(), instrName);                
                    break;
                case COMPLETE_PREV_MAINT:
                    instr=new DataInstruments(instrName);
                    decision=argValues[3].toString();
                    actionDiagnoses=instr.completePrevMaint(decision);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                            TblsAppProcData.Instruments.TBL.getName(), instrName);                
                    break;
                case START_VERIFICATION:
                    instr=new DataInstruments(instrName);
                    actionDiagnoses=instr.startVerification();
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                            TblsAppProcData.Instruments.TBL.getName(), instrName);                
                    break;
                case COMPLETE_VERIFICATION:
                    instr=new DataInstruments(instrName);
                    decision=argValues[3].toString();
                    actionDiagnoses=instr.completeVerification(decision);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsAppProcData.Instruments.TBL.getName(), 
                            TblsAppProcData.Instruments.TBL.getName(), instrName);                
                    break;
                default:
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, null, "endpointNotFound", null);   
                    return;
            }     
        this.diagnostic=LPPlatform.trapMessage(actionDiagnoses.getDiagnostic(),actionDiagnoses.getMessageCode(), actionDiagnoses.getMessageCodeVariables());
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
