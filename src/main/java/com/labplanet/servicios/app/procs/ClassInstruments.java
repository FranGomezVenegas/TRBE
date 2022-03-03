/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app.procs;

import databases.Rdbms;
import databases.TblsAppProcData.TablesAppProcData;
import databases.TblsAppProcDataAudit;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.instruments.DataInstruments;
import static functionaljavaa.instruments.DataInstrumentsEvents.instrumentAuditSetAuditRecordAsReviewed;
import static functionaljavaa.instruments.DataInstrumentsEvents.objectVariableChangeValue;
import static functionaljavaa.instruments.DataInstrumentsEvents.objectVariableSetValue;
import functionaljavaa.instruments.InstrumentsEnums.InstrumentsAPIactionsEndpoints;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
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
                    String familyName=argValues[1].toString();
                    String fieldName=argValues[2].toString();
                    String fieldValue=argValues[3].toString();
                    String[] fieldNames=null;
                    Object[] fieldValues=null;
                    if (fieldName!=null) fieldNames = fieldName.split("\\|");
                    if (fieldValue!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        actionDiagnoses=DataInstruments.createNewInstrument(instrName, familyName, fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);                
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
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);                
                    break;
                case DECOMMISSION_INSTRUMENT:
                    fieldName=argValues[1].toString();
                    fieldValue=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fieldValue!=null && fieldValue.length()>0){
                        if (fieldName!=null) fieldNames = fieldName.split("\\|");
                        fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                    }
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        actionDiagnoses=instr.decommissionInstrument(fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);                
                    break;
                case UNDECOMMISSION_INSTRUMENT:
                    fieldName=argValues[1].toString();
                    fieldValue=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fieldValue!=null && fieldValue.length()>0){
                        if (fieldName!=null) fieldNames = fieldName.split("\\|");
                        fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                    }
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        actionDiagnoses=instr.unDecommissionInstrument(fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);                
                    break;
                case TURN_ON_LINE:
                    fieldName=argValues[1].toString();
                    fieldValue=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fieldValue!=null && fieldValue.length()>0){
                        if (fieldName!=null) fieldNames = fieldName.split("\\|");
                        fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                    }
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        actionDiagnoses=instr.turnOnLine(fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);                
                    break;
                case TURN_OFF_LINE:
                    fieldName=argValues[1].toString();
                    fieldValue=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fieldValue!=null && fieldValue.length()>0){
                        if (fieldName!=null) fieldNames = fieldName.split("\\|");
                        fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                    }
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=(InternalMessage) fieldValues[1];
                    else
                        instr=new DataInstruments(instrName);
                    actionDiagnoses=instr.turnOffLine(fieldNames, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);                
                    break;
                case START_CALIBRATION:
                    instr=new DataInstruments(instrName);
                    actionDiagnoses=instr.startCalibration();
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);                
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                            TablesAppProcData.INSTRUMENT_EVENT.getTableName(), actionDiagnoses.getNewObjectId());                
                    }
                    break;
                case COMPLETE_CALIBRATION:
                    instr=new DataInstruments(instrName);
                    String decision=argValues[3].toString();
                    actionDiagnoses=instr.completeCalibration(decision);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);                
                    }
                    break;
                case START_PREV_MAINT:
                    instr=new DataInstruments(instrName);
                    actionDiagnoses=instr.startPrevMaint();
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);                
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                            TablesAppProcData.INSTRUMENT_EVENT.getTableName(), actionDiagnoses.getNewObjectId());                
                    }
                    break;
                case COMPLETE_PREV_MAINT:
                    instr=new DataInstruments(instrName);
                    decision=argValues[3].toString();
                    actionDiagnoses=instr.completePrevMaint(decision);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);                
                    }
                    break;
                case START_VERIFICATION:
                    instr=new DataInstruments(instrName);
                    actionDiagnoses=instr.startVerification();
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);             
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENT_EVENT.getTableName(), 
                            TablesAppProcData.INSTRUMENT_EVENT.getTableName(), actionDiagnoses.getNewObjectId());                
                    }
                    break;
                case COMPLETE_VERIFICATION:
                    instr=new DataInstruments(instrName);
                    decision=argValues[3].toString();
                    actionDiagnoses=instr.completeVerification(decision);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);                
                    break;
                case REOPEN_EVENT:
                    instr=new DataInstruments(instrName);
                    Integer instrEventId=(Integer)argValues[1];
                    actionDiagnoses=instr.reopenEvent(instrEventId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);                
                    break;                    
                case ENTER_EVENT_RESULT:
                    instrEventId=(Integer)argValues[1];
                    String variableName=argValues[2].toString();
                    String newValue=argValues[3].toString();
                    //instr=new DataInstruments(instrName);
                    //actionDiagnoses=instr.startCalibration();
                    actionDiagnoses=objectVariableSetValue(instrName, instrEventId, variableName, newValue);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);                
                    break;
                case REENTER_EVENT_RESULT:
                    instrEventId=(Integer)argValues[1];
                    variableName=argValues[2].toString();
                    newValue=argValues[3].toString();
                    //instr=new DataInstruments(instrName);
                    //actionDiagnoses=instr.startCalibration();
                    actionDiagnoses=objectVariableChangeValue(instrName, instrEventId, variableName, newValue);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), 
                            TablesAppProcData.INSTRUMENTS.getTableName(), instrName);                
                    break;
                case INSTRUMENTAUDIT_SET_AUDIT_ID_REVIEWED:
//                    ResponseMessages message=ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
//                    message.addMainForError(TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
//                    actionDiagnoses=new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null,null);
//if (1==2){
                    String instrumentName=null;
                    Integer auditId = (Integer) argValues[0];
                    Object[][] auditInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsAppProcDataAudit.TablesAppProcDataAudit.INSTRUMENTS.getTableName(), 
                        new String[]{TblsAppProcDataAudit.Instruments.AUDIT_ID.getName()}, new Object[]{auditId}, 
                        new String[]{TblsAppProcDataAudit.Instruments.INSTRUMENT_NAME.getName()}, new String[]{TblsAppProcDataAudit.Instruments.AUDIT_ID.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditInfo[0][0].toString())){
                        actionDiagnoses=new InternalMessage(auditInfo[0][0].toString(), SampleAudit.SampleAuditErrorTrapping.AUDIT_RECORD_NOT_FOUND, new Object[]{auditId});
                        instrumentName=null;
                    }else{
                        actionDiagnoses=instrumentAuditSetAuditRecordAsReviewed(auditId, ProcedureRequestSession.getInstanceForActions(null, null, null).getToken().getPersonName());
//                    }
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsAppProcDataAudit.TablesAppProcDataAudit.INSTRUMENTS.getTableName(), TblsAppProcDataAudit.TablesAppProcDataAudit.INSTRUMENTS.getTableName(), auditId);
                    this.messageDynamicData=new Object[]{auditId, instrumentName};
                    break;
}
                default:
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, null, "endpointNotFound", null);   
                    return;
            }     
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))
            this.diagnostic=ApiMessageReturn.trapMessage(actionDiagnoses.getDiagnostic(),actionDiagnoses.getMessageCodeObj(), actionDiagnoses.getMessageCodeVariables());
        else
            this.diagnostic=ApiMessageReturn.trapMessage(actionDiagnoses.getDiagnostic(),endPoint, actionDiagnoses.getMessageCodeVariables());
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
