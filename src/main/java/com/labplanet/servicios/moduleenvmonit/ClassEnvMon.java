/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.moduleenvmonit.EnvMonAPI.EnvMonAPIEndpoints;
import databases.Rdbms;
import databases.TblsProcedure;
import functionaljavaa.inventory.batch.DataBatchIncubator;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramSample;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.time.LocalDateTime;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class ClassEnvMon {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;

    public ClassEnvMon(HttpServletRequest request, EnvMonAPIEndpoints endPoint){
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        RelatedObjects rObj=RelatedObjects.getInstanceForActions();

        DataProgramSample prgSmp = new DataProgramSample();     
        String batchName = "";
        String incubationName = "";
        
        Object[] actionDiagnoses = null;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());        
        this.functionFound=true;
            switch (endPoint){
                case CORRECTIVE_ACTION_COMPLETE:
                    String programName=argValues[0].toString();
                    Integer correctiveActionId = (Integer) argValues[1];                    
                    actionDiagnoses = DataProgramCorrectiveAction.markAsCompleted(correctiveActionId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){                        
                        Object[][] correctiveActionInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.ProgramCorrectiveAction.TBL.getName(), 
                            new String[]{TblsProcedure.ProgramCorrectiveAction.FLD_ID.getName()}, new Object[]{correctiveActionId},
                            new String[]{TblsProcedure.ProgramCorrectiveAction.FLD_SAMPLE_ID.getName()});
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{correctiveActionId, correctiveActionInfo[0][0], procInstanceName}); 
                        this.messageDynamicData=new Object[]{correctiveActionId, correctiveActionInfo[0][0], procInstanceName};   
                    }else{
                        this.messageDynamicData=new Object[]{correctiveActionId, procInstanceName};                           
                    }                    
                    break;
                case EM_BATCH_INCUB_CREATE:    
                    batchName = argValues[0].toString();
                    Integer batchTemplateId = (Integer) argValues[1];
                    Integer batchTemplateVersion = (Integer) argValues[2];
                    String fieldName=argValues[3].toString();
                    String fieldValue=argValues[4].toString();
                    String[] fieldNames=new String[0];
                    Object[] fieldValues=new Object[0];
                    if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                    if (fieldValue!=null && fieldValue.length()>0) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));                                                                                
                    if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                        actionDiagnoses=fieldValues;
                        break;
                    }
                        actionDiagnoses= DataBatchIncubator.createBatch(batchName, batchTemplateId, batchTemplateVersion, fieldNames, fieldValues);
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName);                
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{batchName, procInstanceName});                    
                    this.messageDynamicData=new Object[]{batchName, procInstanceName};
                    break;   
                case EM_BATCH_INCUB_REMOVE:    
                    batchName = argValues[0].toString();
                    actionDiagnoses= DataBatchIncubator.removeBatch(batchName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName);                
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{batchName, procInstanceName});                    
                    this.messageDynamicData=new Object[]{batchName, procInstanceName};
                    break;   
                case EM_BATCH_ASSIGN_INCUB: 
                    batchName = argValues[0].toString();
                    incubationName = argValues[1].toString();
                    String incubStage=argValues[2].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), "incubator", incubationName);                
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName);                
                    this.messageDynamicData=new Object[]{incubationName, batchName};
                    actionDiagnoses=DataBatchIncubator.batchAssignIncubator(batchName, incubationName, incubStage);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{incubationName, batchName, procInstanceName});
                    break;
                case EM_BATCH_UPDATE_INFO: 
                    batchName = argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName);                
                    fieldName = argValues[1].toString();
                    String[] fieldsName = fieldName.split("\\|");
                    fieldValue = argValues[2].toString();
                    fieldValues= LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=fieldValues;
                    else
                        actionDiagnoses=DataBatchIncubator.batchUpdateInfo(batchName, fieldsName, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{batchName, Arrays.toString(fieldsName), Arrays.toString(fieldValues), procInstanceName});
                    this.messageDynamicData=new Object[]{incubationName, batchName};
                    break;
                case EM_BATCH_INCUB_START:
                    batchName = argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName);                
                    batchTemplateId = (Integer) argValues[1];
                    batchTemplateVersion = (Integer) argValues[2];
                    actionDiagnoses=DataBatchIncubator.batchStarted(batchName, batchTemplateId, batchTemplateVersion);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{batchName, procInstanceName});
                        this.messageDynamicData=new Object[]{incubationName, batchName};                    
                    }else{
                        if (actionDiagnoses[4]==DataBatchIncubator.IncubatorBatchErrorTrapping.INCUBATORBATCH_ALREADY_IN_PROCESS.getErrorCode())
                            this.messageDynamicData=new Object[]{actionDiagnoses[actionDiagnoses.length-2], actionDiagnoses[actionDiagnoses.length-1], procInstanceName};                                  
                        else
                            this.messageDynamicData=new Object[]{batchName, procInstanceName};
                    }
                    break;                    
                case EM_BATCH_INCUB_END:
                    batchName = argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName);                
                    batchTemplateId = (Integer) argValues[1];
                    batchTemplateVersion = (Integer) argValues[2];
                    actionDiagnoses=DataBatchIncubator.batchEnded(batchName, batchTemplateId, batchTemplateVersion);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{batchName, procInstanceName});
                    this.messageDynamicData=new Object[]{batchName, incubationName};
                    break;
                case EM_LOGSAMPLE_SCHEDULER:
                    LocalDateTime dateStart=(LocalDateTime) argValues[0];
                    LocalDateTime dateEnd=(LocalDateTime) argValues[1];
                    programName = argValues[2].toString();
                    actionDiagnoses=prgSmp.logProgramSampleScheduled(programName, dateStart, dateEnd);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{dateStart, dateEnd, programName, procInstanceName});                                        
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
