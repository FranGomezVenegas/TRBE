/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.definition;

import com.labplanet.servicios.moduleenvmonit.EnvMonAPI.EnvMonAPIactionsEndpoints;
import module.monitoring.definition.TblsEnvMonitData;
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
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import modules.masterdata.analysis.ConfigAnalysisStructure;
import trazit.enums.ActionsClass;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassEnvMon implements ActionsClass{
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private InternalMessage diagnosticObj=null;
    private Boolean functionFound=false;
    private EnumIntEndpoints enumConstantByName;
    
    public ClassEnvMon(HttpServletRequest request, EnvMonAPIactionsEndpoints endPoint){
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        RelatedObjects rObj=RelatedObjects.getInstanceForActions();

        DataProgramSample prgSmp = new DataProgramSample();     
        String batchName = "";
        String incubationName = "";
                
        InternalMessage actionDiagnosesObj = null;
        this.functionFound=true;
        this.enumConstantByName=endPoint;
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
            this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.messageDynamicData = new Object[]{argValues[2].toString()};
            this.relatedObj = rObj;
            rObj.killInstance();
            return;
        }      
        
            switch (endPoint){
                case CORRECTIVE_ACTION_COMPLETE:
                    String programName=argValues[0].toString();
                    Integer correctiveActionId = (Integer) argValues[1];                    
                    actionDiagnosesObj = DataProgramCorrectiveAction.markAsCompleted(correctiveActionId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())){                        
                        Object[][] correctiveActionInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), 
                            new String[]{TblsProcedure.ProgramCorrectiveAction.ID.getName()}, new Object[]{correctiveActionId},
                            new String[]{TblsProcedure.ProgramCorrectiveAction.SAMPLE_ID.getName()});
                        actionDiagnosesObj=new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{correctiveActionId, correctiveActionInfo[0][0], procInstanceName}); 
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
                    if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                        actionDiagnosesObj = (InternalMessage) fieldValues[1];
                        break;
                    }
                    if (incubStage.length()>0){
                        fieldNames=LPArray.addValueToArray1D(fieldNames, TblsEnvMonitData.IncubBatch.INCUB_STAGE.getName());
                        fieldValues=LPArray.addValueToArray1D(fieldValues, incubStage);
                    }
                        actionDiagnosesObj= DataBatchIncubator.createBatch(batchName, batchTemplateId, batchTemplateVersion, fieldNames, fieldValues);
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);                
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic()))
                        actionDiagnosesObj=new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{batchName, procInstanceName});                    
                    this.messageDynamicData=new Object[]{batchName, procInstanceName};
                    break;   
                case EM_BATCH_INCUB_REMOVE:
                    batchName = argValues[0].toString();
                    actionDiagnosesObj= DataBatchIncubator.removeBatch(batchName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);                
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic()))
                        actionDiagnosesObj=new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{batchName, procInstanceName});                    
                    this.messageDynamicData=new Object[]{batchName, procInstanceName};
                    break;   
                case EM_BATCH_ASSIGN_INCUB: 
                    batchName = argValues[0].toString();
                    incubationName = argValues[1].toString();
                    incubStage=argValues[2].toString(); 
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), "incubator", incubationName);  // TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName()               
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);                
                    this.messageDynamicData=new Object[]{batchName, incubationName};
                    actionDiagnosesObj=DataBatchIncubator.batchAssignIncubator(batchName, incubationName, incubStage);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic()))
                        actionDiagnosesObj=new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{incubationName, batchName, procInstanceName});
                    break;
                case EM_BATCH_UPDATE_INFO: 
                    batchName = argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);                
                    fieldName = argValues[1].toString();
                    String[] fieldsName = fieldName.split("\\|");
                    fieldValue = argValues[2].toString();
                    fieldValues= LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                        actionDiagnosesObj = (InternalMessage) fieldValues[1];
                        break;
                    }else
                        actionDiagnosesObj=DataBatchIncubator.batchUpdateInfo(batchName, fieldsName, fieldValues);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic()))
                        actionDiagnosesObj=new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{batchName, Arrays.toString(fieldsName), Arrays.toString(fieldValues), procInstanceName});
                    this.messageDynamicData=new Object[]{incubationName, batchName};
                    break;
                case EM_BATCH_INCUB_START:
                    batchName = argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);                
                    batchTemplateId = (Integer) argValues[1];
                    batchTemplateVersion = (Integer) argValues[2];
                    actionDiagnosesObj=DataBatchIncubator.batchStarted(batchName, batchTemplateId, batchTemplateVersion);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())){
                        actionDiagnosesObj=new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{batchName, procInstanceName});
                        this.messageDynamicData=new Object[]{incubationName, batchName};                    
                    }else{
                        if (actionDiagnosesObj.getMessageCodeObj()==DataBatchIncubator.IncubatorBatchErrorTrapping.INCUBATORBATCH_ALREADY_IN_PROCESS)
                            this.messageDynamicData=actionDiagnosesObj.getMessageCodeVariables();
                        else
                            this.messageDynamicData=new Object[]{batchName, procInstanceName};
                    }
                    break;                    
                case EM_BATCH_INCUB_END:
                    batchName = argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);                
                    batchTemplateId = (Integer) argValues[1];
                    batchTemplateVersion = (Integer) argValues[2];
                    actionDiagnosesObj=DataBatchIncubator.batchEnded(batchName, batchTemplateId, batchTemplateVersion);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic()))
                        actionDiagnosesObj=new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{batchName, procInstanceName});
                    this.messageDynamicData=new Object[]{batchName, incubationName};
                    break;
                case EM_LOGSAMPLE_SCHEDULER:                    
                    LocalDateTime dateStart=LPDate.stringFormatToLocalDateTime(argValues[0].toString());
                    LocalDateTime dateEnd=LPDate.stringFormatToLocalDateTime(argValues[1].toString());
                    programName = argValues[2].toString();
                    actionDiagnosesObj=prgSmp.logProgramSampleScheduled(programName, dateStart, dateEnd);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic()))
                        actionDiagnosesObj=new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{dateStart, dateEnd, programName, procInstanceName});                                        
                    this.messageDynamicData=new Object[]{};
                    break;
                default:      
                    Rdbms.closeRdbms(); 
                    request.getRequestDispatcher(SampleAPIParams.SERVLET_FRONTEND_URL);                    
            }    
        //this.diagnostic=null;
        this.diagnosticObj=actionDiagnosesObj;
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
    @Override    public StringBuilder getRowArgsRows() {        return null;    }

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
        return null;
    }
    public InternalMessage getDiagnosticObj() {
        return diagnosticObj;
    }

    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
    }
    @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }

    @Override    public void initializeEndpoint(String actionName) {        throw new UnsupportedOperationException("Not supported yet.");}
    @Override    public void createClassEnvMonAndHandleExceptions(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {        throw new UnsupportedOperationException("Not supported yet.");}

    @Override
    public HttpServletResponse getHttpResponse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
