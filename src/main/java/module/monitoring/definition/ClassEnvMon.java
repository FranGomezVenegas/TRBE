/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.definition;

import module.monitoring.definition.TblsEnvMonitData;
import com.labplanet.servicios.moduleenvmonit.EnvMonAPI.EnvMonAPIactionsEndpoints;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import databases.Rdbms;
import databases.TblsProcedure;
import functionaljavaa.inventory.batch.DataBatchIncubator;
import module.monitoring.logic.DataProgramCorrectiveAction;
import module.monitoring.logic.DataProgramSample;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.time.LocalDateTime;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;

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

    public ClassEnvMon(HttpServletRequest request, EnvMonAPIactionsEndpoints endPoint){
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        RelatedObjects rObj=RelatedObjects.getInstanceForActions();

        DataProgramSample prgSmp = new DataProgramSample();     
        String batchName = "";
        String incubationName = "";
        
        Object[] actionDiagnoses = null;
        this.functionFound=true;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
            this.diagnostic=(Object[]) argValues[1];
            this.messageDynamicData=new Object[]{argValues[2].toString()};
            return;                        
        }         
        
            switch (endPoint){
                case CORRECTIVE_ACTION_COMPLETE:
                    String programName=argValues[0].toString();
                    Integer correctiveActionId = (Integer) argValues[1];                    
                    actionDiagnoses = DataProgramCorrectiveAction.markAsCompleted(correctiveActionId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){                        
                        Object[][] correctiveActionInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), 
                            new String[]{TblsProcedure.ProgramCorrectiveAction.ID.getName()}, new Object[]{correctiveActionId},
                            new String[]{TblsProcedure.ProgramCorrectiveAction.SAMPLE_ID.getName()});
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{correctiveActionId, correctiveActionInfo[0][0], procInstanceName}); 
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
                    String incubStage=LPNulls.replaceNull(argValues[5]).toString();
                    String[] fieldNames=new String[0];
                    Object[] fieldValues=new Object[0];
                    if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                    if (fieldValue!=null && fieldValue.length()>0) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));                                                                                
                    if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                        actionDiagnoses=fieldValues;
                        break;
                    }
                    if (incubStage.length()>0){
                        fieldNames=LPArray.addValueToArray1D(fieldNames, TblsEnvMonitData.IncubBatch.INCUB_STAGE.getName());
                        fieldValues=LPArray.addValueToArray1D(fieldValues, incubStage);
                    }
                        actionDiagnoses= DataBatchIncubator.createBatch(batchName, batchTemplateId, batchTemplateVersion, fieldNames, fieldValues);
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);                
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{batchName, procInstanceName});                    
                    this.messageDynamicData=new Object[]{batchName, procInstanceName};
                    break;   
                case EM_BATCH_INCUB_REMOVE:
                    batchName = argValues[0].toString();
                    actionDiagnoses= DataBatchIncubator.removeBatch(batchName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);                
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{batchName, procInstanceName});                    
                    this.messageDynamicData=new Object[]{batchName, procInstanceName};
                    break;   
                case EM_BATCH_ASSIGN_INCUB: 
                    batchName = argValues[0].toString();
                    incubationName = argValues[1].toString();
                    incubStage=argValues[2].toString(); 
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), "incubator", incubationName);  // TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName()               
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);                
                    this.messageDynamicData=new Object[]{batchName, incubationName};
                    actionDiagnoses=DataBatchIncubator.batchAssignIncubator(batchName, incubationName, incubStage);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{incubationName, batchName, procInstanceName});
                    break;
                case EM_BATCH_UPDATE_INFO: 
                    batchName = argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);                
                    fieldName = argValues[1].toString();
                    String[] fieldsName = fieldName.split("\\|");
                    fieldValue = argValues[2].toString();
                    fieldValues= LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=fieldValues;
                    else
                        actionDiagnoses=DataBatchIncubator.batchUpdateInfo(batchName, fieldsName, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{batchName, Arrays.toString(fieldsName), Arrays.toString(fieldValues), procInstanceName});
                    this.messageDynamicData=new Object[]{incubationName, batchName};
                    break;
                case EM_BATCH_INCUB_START:
                    batchName = argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);                
                    batchTemplateId = (Integer) argValues[1];
                    batchTemplateVersion = (Integer) argValues[2];
                    actionDiagnoses=DataBatchIncubator.batchStarted(batchName, batchTemplateId, batchTemplateVersion);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{batchName, procInstanceName});
                        this.messageDynamicData=new Object[]{incubationName, batchName};                    
                    }else{
                        if (actionDiagnoses[4]==DataBatchIncubator.IncubatorBatchErrorTrapping.INCUBATORBATCH_ALREADY_IN_PROCESS)
                            this.messageDynamicData=new Object[]{actionDiagnoses[actionDiagnoses.length-2], actionDiagnoses[actionDiagnoses.length-1], procInstanceName};                                  
                        else
                            this.messageDynamicData=new Object[]{batchName, procInstanceName};
                    }
                    break;                    
                case EM_BATCH_INCUB_END:
                    batchName = argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);                
                    batchTemplateId = (Integer) argValues[1];
                    batchTemplateVersion = (Integer) argValues[2];
                    actionDiagnoses=DataBatchIncubator.batchEnded(batchName, batchTemplateId, batchTemplateVersion);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{batchName, procInstanceName});
                    this.messageDynamicData=new Object[]{batchName, incubationName};
                    break;
                case EM_LOGSAMPLE_SCHEDULER:                    
                    LocalDateTime dateStart=LPDate.stringFormatToLocalDateTime(argValues[0].toString());
                    LocalDateTime dateEnd=LPDate.stringFormatToLocalDateTime(argValues[1].toString());
                    programName = argValues[2].toString();
                    actionDiagnoses=prgSmp.logProgramSampleScheduled(programName, dateStart, dateEnd);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{dateStart, dateEnd, programName, procInstanceName});                                        
                    this.messageDynamicData=new Object[]{};
                    break;
                default:      
                    Rdbms.closeRdbms(); 
                    request.getRequestDispatcher(SampleAPIParams.SERVLET_FRONTEND_URL);                    
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
