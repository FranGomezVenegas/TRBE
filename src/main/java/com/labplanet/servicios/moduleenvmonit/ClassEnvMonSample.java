/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import databases.Rdbms;
import databases.TblsData;
import databases.features.Token;
import functionaljavaa.inventory.batch.DataBatchIncubator;
import static functionaljavaa.moduleenvironmentalmonitoring.ConfigMicroorganisms.adhocMicroorganismAdd;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramSample;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramSampleAnalysis;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramSampleAnalysisResult;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.samplestructure.DataSample;
import functionaljavaa.samplestructure.DataSampleAnalysisResult;
import functionaljavaa.samplestructure.DataSampleStages;
import functionaljavaa.samplestructure.DataSampleStructureEnums;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPMath;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ResponseMessages;
/**
 *
 * @author User
 */
public class ClassEnvMonSample {

    /**
     * @return the messageDynamicData
     */
    public Object[] getMessageDynamicData() {
        return this.messageDynamicData;
    }

    /**
     * @return the rObj
     */
    public RelatedObjects getRelatedObj() {
        return this.relatedObj;
    }

    /**
     * @return the endpointExists
     */
    public Boolean getEndpointExists() {
        return this.endpointExists;
    }

    /**
     * @return the diagnostic
     */
    public Object[] getDiagnostic() {
        return this.diagnostic;
    }
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    
    public ClassEnvMonSample(HttpServletRequest request, EnvMonSampleAPI.EnvMonSampleAPIEndpoints endPoint){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
        Boolean isForTesting = procReqSession.getIsForTesting();
        String procInstanceName = procReqSession.getProcedureInstance();
        Token token=procReqSession.getToken();

        Object[] dynamicDataObjects=new Object[]{};        
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());        
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        try {
            DataProgramSampleAnalysis prgSmpAna = new DataProgramSampleAnalysis();
            DataProgramSampleAnalysisResult prgSmpAnaRes = new DataProgramSampleAnalysisResult();
            DataProgramSample prgSmp = new DataProgramSample();
            DataSample smp = new DataSample(prgSmpAna);               
            DataSampleAnalysisResult smpAnaRes = new DataSampleAnalysisResult(prgSmpAnaRes);
            Object[] actionDiagnoses = null;  
            Integer sampleId=null;
            Integer resultId = null;
            switch (endPoint){
                case LOGSAMPLE:
                    String[] fieldNames=null;
                    Object[] fieldValues=null;
                    String smpTmp=LPNulls.replaceNull(argValues[0]).toString();
                    if (smpTmp==null || smpTmp.length()==0)smpTmp=Parameter.getBusinessRuleProcedureFile(procInstanceName, "procedure", "SampleTemplate");  
                    Object smpTmpV=LPNulls.replaceNull(argValues[1]);
                    if (smpTmpV==null || smpTmpV.toString().length()==0)smpTmpV=1;
                    if (LPNulls.replaceNull(argValues[2]).toString().length()>0){
                        fieldNames=argValues[2].toString().split("\\|");
                        fieldValues=LPArray.convertStringWithDataTypeToObjectArray(argValues[3].toString().split("\\|"));
                    }                   
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString()))
                        actionDiagnoses=fieldValues;
                    else{
                        if (argValues[6]==null){
                            actionDiagnoses = prgSmp.logProgramSample(smpTmp, (Integer) smpTmpV, 
                                fieldNames, fieldValues, (String) argValues[4], (String) argValues[5]);
                        }else{
                            actionDiagnoses = prgSmp.logProgramSample(smpTmp, (Integer) smpTmpV,  
                                fieldNames, fieldValues, (String) argValues[4], (String) argValues[5], (Integer) argValues[6]);
                        }
                    }
                    //actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "arguments received", argValues);
                    dynamicDataObjects=new Object[]{actionDiagnoses[actionDiagnoses.length-1]};
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), actionDiagnoses[actionDiagnoses.length-1]);                                                
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                        sampleId=Integer.valueOf(actionDiagnoses[actionDiagnoses.length-1].toString());
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{argValues[0], procInstanceName});
                        messages.addMainForSuccess(endPoint, new Object[]{sampleId, procInstanceName, (String) argValues[5]});
                    }
                    break;
                case ENTERRESULT:                    
                case REENTERRESULT:
                case ENTER_PLATE_READING:
                case REENTER_PLATE_READING:
                    String altAuditEntry=null;
                    String altAuditClass=null;
                    if ("ENTER_PLATE_READING".equalsIgnoreCase(endPoint.getName()) || "REENTER_PLATE_READING".equalsIgnoreCase(endPoint.getName())){
                        altAuditClass="DateEnvMonitSampleEvents";
                        if ("ENTER_PLATE_READING".equalsIgnoreCase(endPoint.getName()))
                            altAuditEntry="PLATE_READING_ENTERED";
                        else
                            altAuditEntry="PLATE_READING_REENTERED";
                    }
                    resultId = (Integer) argValues[0];
                    String rawValueResult = argValues[1].toString();
                    Object[][] resultData = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                            new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()}, new Object[]{resultId}, 
                            new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.ANALYSIS.getName(), 
                                TblsData.SampleAnalysisResult.METHOD_NAME.getName(), TblsData.SampleAnalysisResult.METHOD_VERSION.getName(), TblsData.SampleAnalysisResult.PARAM_NAME.getName(), 
                                TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.RAW_VALUE.getName(), TblsData.SampleAnalysisResult.UOM.getName(), 
                                TblsData.SampleAnalysisResult.UOM_CONVERSION_MODE.getName()});                    
                    if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleStructureEnums.DataSampleAnalysisResultErrorTrapping.NOT_FOUND, new Object[]{resultId.toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
                    else{      
                        String currRawValue = (String) resultData[0][7];
                        if (currRawValue!=null && currRawValue.length()>0 && EnvMonSampleAPI.EnvMonSampleAPIEndpoints.ENTERRESULT.getName().equalsIgnoreCase(endPoint.getName())){
                            procReqSession.killIt();                            
                            if ("ENTERRESULT".equalsIgnoreCase(endPoint.getName()))
                                request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, EnvMonSampleAPI.EnvMonSampleAPIEndpoints.REENTERRESULT.getName());
                            if ("ENTER_PLATE_READING".equalsIgnoreCase(endPoint.getName())){
                                request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, EnvMonSampleAPI.EnvMonSampleAPIEndpoints.REENTER_PLATE_READING.getName());
                                altAuditEntry="PLATE_READING_REENTERED";
                            }
                            procReqSession = ProcedureRequestSession.getInstanceForActions(request, null, isForTesting);
                            if (procReqSession.getHasErrors()){
                                procReqSession.killIt();
                                actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, procReqSession.getErrorMessage(), new Object[]{resultId.toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
                                break;
                            }
                        }
                    }
                    Object[] diagn = smpAnaRes.sampleAnalysisResultEntry(resultId, rawValueResult, smp, altAuditEntry, altAuditClass);
                    actionDiagnoses=(Object[]) diagn[0];
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), resultId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                        if (diagn.length>1){
                            Object[] auditDiagn=(Object[]) diagn[1];
                            String pAuditId=(String)auditDiagn[auditDiagn.length-1];
                            smp.setParentAuditId(Integer.valueOf(pAuditId));
                        }
                        Object[][] resultInfo=new Object[0][0];
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{resultId, rawValueResult, procInstanceName});                    
                        resultInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                                new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()}, new Object[]{resultId}, new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName()});
                        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString())) sampleId=Integer.valueOf(resultInfo[0][0].toString());
                        dynamicDataObjects=new Object[]{resultInfo[0][0].toString()};
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), resultInfo[0][0]);
                    }
                    break;
/*                case PLATE_READING_NUMBER:
                    sampleId = (Integer) argValues[0];
                    rawValueResult = argValues[1].toString();
                    Object[][] sampleAnaResultInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                        new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.PARAM_NAME.getName()}, 
                        new Object[]{sampleId, "Recuento"}, 
                        new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()});
                    actionDiagnoses=null;
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAnaResultInfo[0][0].toString()))
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "No encontrado el parámetro 'Recuento' en la muestra "+sampleId.toString(), null);
                    if (sampleAnaResultInfo.length!=1)    
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Encontrado varios parámetros 'Recuento' en la muestra "+sampleId.toString()+", en este caso se debe entrar resultado por su Id y la acción ENTERRESULT.", null);
                    if (actionDiagnoses==null){    
                        resultId=Integer.valueOf(sampleAnaResultInfo[0][0].toString());
                        diagn = smpAnaRes.sampleAnalysisResultEntry(resultId, rawValueResult, smp);
                        actionDiagnoses=(Object[]) diagn[0];
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), resultId);
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                            if (diagn.length>1){
                                Object[] auditDiagn=(Object[]) diagn[1];
                                String pAuditId=(String)auditDiagn[auditDiagn.length-1];
                                smp.setParentAuditId(Integer.valueOf(pAuditId));
                            }
                            Object[][] resultInfo=new Object[0][0];
                            actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{resultId, rawValueResult, procInstanceName});                    
                            resultInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                                    new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()}, new Object[]{resultId}, new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName()});
                            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString())) sampleId=Integer.valueOf(resultInfo[0][0].toString());
                            dynamicDataObjects=new Object[]{resultInfo[0][0].toString()};
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), resultInfo[0][0]);
                        }
                    }
                    break;
*/
                case ADD_SAMPLE_MICROORGANISM: 
                case ADD_ADHOC_SAMPLE_MICROORGANISM:
                    sampleId=(Integer) argValues[0];
                    Integer numItems=1;
                    if (argValues.length>=3){
                        String numItemsStr=LPNulls.replaceNull(argValues[2]).toString();
                        if (numItemsStr.length()>0 && LPPlatform.LAB_TRUE.equalsIgnoreCase(LPMath.isNumeric(numItemsStr)[0].toString())) 
                            numItems=Integer.valueOf(numItemsStr);
                    }
                    for (String orgName: (String[]) argValues[1].toString().split("\\|")){
                        actionDiagnoses = DataProgramSample.addSampleMicroorganism((Integer) argValues[0], orgName, numItems);
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE_MICROORGANISM.getTableName(), actionDiagnoses[actionDiagnoses.length-1]);
                    }
                    if (EnvMonSampleAPI.EnvMonSampleAPIEndpoints.ADD_ADHOC_SAMPLE_MICROORGANISM.getName().equalsIgnoreCase(endPoint.getName()) && actionDiagnoses!=null &&  LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                        for (String orgName: (String[]) argValues[1].toString().split("\\|")){                        
                            adhocMicroorganismAdd(orgName);
                        }
                    }
                    dynamicDataObjects=new Object[]{argValues[1].toString().replace("\\|", ", "), sampleId, numItems};
                    if (actionDiagnoses!=null &&  LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), dynamicDataObjects);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), argValues[0]);                                                
                    break;
                case REMOVE_SAMPLE_MICROORGANISM: 
                    sampleId=(Integer) argValues[0];
                    numItems=1;
                    if (argValues.length>=3){
                        String numItemsStr=LPNulls.replaceNull(argValues[2]).toString();
                        if (numItemsStr.length()>0 && LPPlatform.LAB_TRUE.equalsIgnoreCase(LPMath.isNumeric(numItemsStr)[0].toString())) 
                            numItems=Integer.valueOf(numItemsStr);
                    }
                    for (String orgName: (String[]) argValues[1].toString().split("\\|")){
                        actionDiagnoses = DataProgramSample.removeSampleMicroorganism((Integer) argValues[0], orgName, numItems);
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE_MICROORGANISM.getTableName(), actionDiagnoses[actionDiagnoses.length-1]);
                    }
                    dynamicDataObjects=new Object[]{argValues[1].toString().replace("\\|", ", "), sampleId, numItems};
                    if (actionDiagnoses!=null &&  LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), dynamicDataObjects);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), argValues[0]);                                                
                    break;
                case EM_BATCH_INCUB_ADD_SMP:
                    String batchName = argValues[0].toString();
                    Integer batchTemplateId = (Integer) argValues[1];
                    Integer batchTemplateVersion = (Integer) argValues[2];
                    sampleId = (Integer) argValues[3];   
                    Integer positionRow=null;
                    if (argValues.length>=5 && argValues[4]!=null && argValues[4].toString().length()>0) positionRow=(Integer) argValues[4];
                    Integer positionCol=null;
                    if (argValues.length>=6 && argValues[5]!=null && argValues[5].toString().length()>0)positionCol= (Integer) argValues[5];
                    
                    Boolean positionOverride=false;
                    if (argValues.length>=7 && argValues[6]!=null && argValues[6].toString().length()>0) {
                        String positionOverrideStr=argValues[6].toString();
                        if (positionOverrideStr!=null && positionOverrideStr.length()>0) positionOverride=Boolean.valueOf(positionOverrideStr);
                    }
                    actionDiagnoses=DataBatchIncubator.batchAddSample(batchName, batchTemplateId, batchTemplateVersion
                            , sampleId, positionRow, positionCol, positionOverride);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{sampleId, batchName, procInstanceName});                                        
                    dynamicDataObjects=new Object[]{sampleId, batchName};
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), sampleId);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);
                    break;
                case EM_BATCH_INCUB_MOVE_SMP:
                    batchName = argValues[0].toString();
                    batchTemplateId = (Integer) argValues[1];
                    batchTemplateVersion = (Integer) argValues[2];
                    sampleId = (Integer) argValues[3];
                    positionRow=null;
                    if (argValues.length>=5) positionRow=(Integer) argValues[4];
                    positionCol=null;
                    if (argValues.length>=6)positionCol= (Integer) argValues[5];
                    
                    positionOverride=false;
                    if (argValues.length>=7) {
                        Object positionOverrideStr=argValues[6];
                        if (positionOverrideStr!=null && LPNulls.replaceNull(positionOverrideStr).toString().length()>0) positionOverride=Boolean.valueOf(positionOverrideStr.toString());
                    }                   
                    actionDiagnoses=DataBatchIncubator.batchMoveSample(batchName, batchTemplateId, batchTemplateVersion, sampleId, positionRow, positionCol, positionOverride);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{sampleId, batchName, procInstanceName});                                        
                    dynamicDataObjects=new Object[]{sampleId, batchName};
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), sampleId);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);
                    break;
                case EM_BATCH_INCUB_REMOVE_SMP:
                    batchName = argValues[0].toString();
                    sampleId = (Integer) argValues[3];
                    if (argValues[1]!=null && argValues[1].toString().length()>0){
                        batchTemplateId = (Integer) argValues[1];                    
                        batchTemplateVersion = (Integer) argValues[2];                                           
                        actionDiagnoses=DataBatchIncubator.batchRemoveSample(batchName, batchTemplateId, batchTemplateVersion, sampleId);
                    }else
                        actionDiagnoses=DataBatchIncubator.batchRemoveSample(batchName, sampleId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{sampleId, batchName, procInstanceName});                                        
                    dynamicDataObjects=new Object[]{sampleId, batchName};
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), sampleId);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);
                    break;
                default:
                    this.endpointExists=false;
                    Rdbms.closeRdbms(); 
                    RequestDispatcher rd = request.getRequestDispatcher(SampleAPIParams.SERVLET_API_URL);
                    rd.forward(request,null);   
            }             
            if (actionDiagnoses!=null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                DataSampleStages smpStage = new DataSampleStages();
                if (smpStage.isSampleStagesEnable())
                    smpStage.dataSampleActionAutoMoveToNext(endPoint.getName().toUpperCase(), sampleId);
                
            }           
            this.diagnostic=actionDiagnoses;
            this.relatedObj=rObj;
            this.messageDynamicData=dynamicDataObjects;
        } catch (ServletException | IOException ex) {
            Logger.getLogger(ClassEnvMonSample.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            rObj.killInstance();
        }        
    }
    
}
