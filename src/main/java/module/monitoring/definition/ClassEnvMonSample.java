/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.definition;

import com.labplanet.servicios.app.GlobalAPIsParams;
import module.monitoring.definition.TblsEnvMonitData;
import com.labplanet.servicios.moduleenvmonit.EnvMonSampleAPI;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import databases.Rdbms;
import databases.TblsData;
import functionaljavaa.inventory.batch.DataBatchIncubator;
import module.monitoring.logic.DataProgramSample;
import module.monitoring.logic.DataProgramSampleAnalysis;
import module.monitoring.logic.DataProgramSampleAnalysisResult;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.samplestructure.DataSample;
import functionaljavaa.samplestructure.DataSampleAnalysisResult;
import functionaljavaa.samplestructure.DataSampleStages;
import functionaljavaa.samplestructure.DataSampleStructureEnums;
import java.io.IOException;
import java.math.BigDecimal;
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
import static module.monitoring.logic.ConfigMicroorganisms.adhocMicroorganismAdd;
import trazit.enums.ActionsClass;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ResponseMessages;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassEnvMonSample implements ActionsClass{

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
    public InternalMessage getDiagnosticObj() {
        return diagnosticObj;
    }    
    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    private Object[] diagnostic = new Object[0];
    private InternalMessage diagnosticObj=null;
    private Boolean isSuccess = false;
    private Object[] responseError = null;
    private Boolean functionFound = false;
    private EnumIntEndpoints enumConstantByName;
    
    public ClassEnvMonSample(HttpServletRequest request, EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints endPoint) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
        Boolean isForTesting = procReqSession.getIsForTesting();
        String procInstanceName = procReqSession.getProcedureInstance();
        this.enumConstantByName=endPoint;
        Object[] dynamicDataObjects = new Object[]{};
        InternalMessage actionDiagnosesObj = null;
        this.functionFound = true;
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
            this.isSuccess = false;
            this.diagnostic = (Object[]) argValues[1];
            this.responseError = this.diagnostic;
            this.messageDynamicData = new Object[]{argValues[2].toString()};
            return;
        }
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        try {
            DataProgramSampleAnalysis prgSmpAna = new DataProgramSampleAnalysis();
            DataProgramSampleAnalysisResult prgSmpAnaRes = new DataProgramSampleAnalysisResult();
            DataProgramSample prgSmp = new DataProgramSample();
            DataSample smp = new DataSample(prgSmpAna);
            DataSampleAnalysisResult smpAnaRes = new DataSampleAnalysisResult(prgSmpAnaRes);
            Integer sampleId = null;
            Integer resultId = null;
            switch (endPoint) {
                case LOGSAMPLE:
                    String[] fieldNames = null;
                    Object[] fieldValues = null;
                    String smpTmp = LPNulls.replaceNull(argValues[0]).toString();
                    Object smpTmpV = LPNulls.replaceNull(argValues[1]);
                    if (smpTmpV == null || smpTmpV.toString().length() == 0) {
                        smpTmpV = 1;
                    }
                    if (LPNulls.replaceNull(argValues[2]).toString().length() > 0) {
                        fieldNames = argValues[2].toString().split("\\|");                        
                        fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(argValues[3].toString().split("\\|"),
                        TblsEnvMonitData.TablesEnvMonitData.SAMPLE, fieldNames);
                        if (fieldValues != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                            actionDiagnosesObj = (InternalMessage) fieldValues[1];
                        }

                    }
                    if (fieldNames != null) {
                        Object[] checkTwoArraysSameLength = LPArray.checkTwoArraysSameLength(fieldNames, fieldValues);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkTwoArraysSameLength[0].toString())) {
                            actionDiagnosesObj=new InternalMessage(LPPlatform.LAB_FALSE, checkTwoArraysSameLength[checkTwoArraysSameLength.length - 1].toString(), null, null);
                        }
                    }
                    if (actionDiagnosesObj == null) {
                        if (LPNulls.replaceNull(argValues[6]).toString().length() == 0) {
                            actionDiagnosesObj = prgSmp.logProgramSample(smpTmp, (Integer) smpTmpV,
                                    fieldNames, fieldValues, (String) argValues[4], (String) argValues[5]);
                        } else {
                            actionDiagnosesObj = prgSmp.logProgramSample(smpTmp, (Integer) smpTmpV,
                                    fieldNames, fieldValues, (String) argValues[4], (String) argValues[5], (Integer) argValues[6]);
                        }
                    }
                    dynamicDataObjects = actionDiagnosesObj.getMessageCodeVariables();
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), actionDiagnosesObj.getNewObjectId());
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                        sampleId = Integer.valueOf(actionDiagnosesObj.getNewObjectId().toString());
                        actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{argValues[0], procInstanceName}, sampleId);
                        messages.addMainForSuccess(endPoint, new Object[]{sampleId, procInstanceName, (String) argValues[5]});
                    }
                    break;
                case ENTERRESULT:
                case REENTERRESULT:
                case ENTER_PLATE_READING:
                case REENTER_PLATE_READING:
                    String altAuditEntry = null;
                    String altAuditClass = null;
                    if (EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints.ENTER_PLATE_READING.getName().equalsIgnoreCase(endPoint.getName()) || EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints.REENTER_PLATE_READING.getName().equalsIgnoreCase(endPoint.getName())) {
                        altAuditClass = "DateEnvMonitSampleEvents";
                        if (EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints.ENTER_PLATE_READING.getName().equalsIgnoreCase(endPoint.getName())) {
                            altAuditEntry = "PLATE_READING_ENTERED";
                        } else {
                            altAuditEntry = "PLATE_READING_REENTERED";
                        }
                    }
                    resultId = (Integer) argValues[0];
                    String rawValueResult = argValues[1].toString();
                    Object[][] resultData = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                            new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()}, new Object[]{resultId},
                            new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.ANALYSIS.getName(),
                                TblsData.SampleAnalysisResult.METHOD_NAME.getName(), TblsData.SampleAnalysisResult.METHOD_VERSION.getName(), TblsData.SampleAnalysisResult.PARAM_NAME.getName(),
                                TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.RAW_VALUE.getName(), TblsData.SampleAnalysisResult.UOM.getName(),
                                TblsData.SampleAnalysisResult.UOM_CONVERSION_MODE.getName()});
                    if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) {
                        actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, DataSampleStructureEnums.DataSampleAnalysisResultErrorTrapping.NOT_FOUND, new Object[]{resultId.toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
                    } else {
                        String currRawValue = (String) resultData[0][7];
                        if (currRawValue != null && currRawValue.length() > 0 && EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints.ENTERRESULT.getName().equalsIgnoreCase(endPoint.getName())) {
                            procReqSession.killIt();
                            if ("ENTERRESULT".equalsIgnoreCase(endPoint.getName())) {
                                request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints.REENTERRESULT.getName());
                            }
                            if ("ENTER_PLATE_READING".equalsIgnoreCase(endPoint.getName())) {
                                request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints.REENTER_PLATE_READING.getName());
                                altAuditEntry = "PLATE_READING_REENTERED";
                            }
                            procReqSession = ProcedureRequestSession.getInstanceForActions(request, null, isForTesting);
                            if (Boolean.TRUE.equals(procReqSession.getHasErrors())) {
                                procReqSession.killIt();
                                actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, procReqSession.getErrorMessageCodeObj(), new Object[]{resultId.toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
                                break;
                            }
                        }
                    }
                    actionDiagnosesObj = smpAnaRes.sampleAnalysisResultEntry(resultId, rawValueResult, smp, altAuditEntry, altAuditClass);                    
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), resultId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                        messages.addMainForSuccess(endPoint, new Object[]{resultId, procInstanceName});                        
                        Object[][] resultInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                        new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()}, new Object[]{resultId}, new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName()});
                        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString()))) {
                            sampleId = Integer.valueOf(resultInfo[0][0].toString());
                        }
                        dynamicDataObjects = new Object[]{resultInfo[0][0].toString()};
                        messages.addMainForSuccess(endPoint, new Object[]{resultInfo[0][0], procInstanceName});
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), resultInfo[0][0]);
                    }
                    break;

                case ENTER_PLATE_READING_SECONDENTRY:
                case REENTER_PLATE_READING_SECONDENTRY:
                    altAuditEntry = null;
                    altAuditClass = null;
                    if ("ENTER_PLATE_READING_SECONDENTRY".equalsIgnoreCase(endPoint.getName()) || "REENTER_PLATE_READING_SECONDENTRY".equalsIgnoreCase(endPoint.getName())) {
                        altAuditClass = "DateEnvMonitSampleEvents";
                        if ("ENTER_PLATE_READING".equalsIgnoreCase(endPoint.getName())) {
                            altAuditEntry = "PLATE_READING_ENTERED_SECONDENTRY";
                        } else {
                            altAuditEntry = "PLATE_READING_REENTERED_SECONDENTRY";
                        }
                    }
                    resultId = (Integer) argValues[0];
                    rawValueResult = argValues[1].toString();
                    resultData = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY.getTableName(),
                            new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()}, new Object[]{resultId},
                            new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.ANALYSIS.getName(),
                                TblsData.SampleAnalysisResult.METHOD_NAME.getName(), TblsData.SampleAnalysisResult.METHOD_VERSION.getName(), TblsData.SampleAnalysisResult.PARAM_NAME.getName(),
                                TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.RAW_VALUE.getName(), TblsData.SampleAnalysisResult.UOM.getName(),
                                TblsData.SampleAnalysisResult.UOM_CONVERSION_MODE.getName()});
                    if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) {
                        actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, DataSampleStructureEnums.DataSampleAnalysisResultErrorTrapping.NOT_FOUND, new Object[]{resultId.toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
                    } else {
                        String currRawValue = (String) resultData[0][7];
                        if (currRawValue != null && currRawValue.length() > 0 && EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints.ENTERRESULT.getName().equalsIgnoreCase(endPoint.getName())) {
                            procReqSession.killIt();
                            if ("ENTERRESULT".equalsIgnoreCase(endPoint.getName())) {
                                request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints.REENTERRESULT.getName());
                            }
                            if ("ENTER_PLATE_READING_SECONDENTRY".equalsIgnoreCase(endPoint.getName())) {
                                request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints.REENTER_PLATE_READING.getName());
                                altAuditEntry = "PLATE_READING_REENTERED_SECONDENTRY";
                            }
                            procReqSession = ProcedureRequestSession.getInstanceForActions(request, null, isForTesting);
                            if (Boolean.TRUE.equals(procReqSession.getHasErrors())) {
                                procReqSession.killIt();
                                actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, procReqSession.getErrorMessageCodeObj(), new Object[]{resultId.toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
                                break;
                            }
                        }
                    }
                    actionDiagnosesObj = smpAnaRes.sampleAnalysisResultEntrySecondEntry(resultId, rawValueResult, smp, altAuditEntry, altAuditClass);                    
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY.getTableName(), resultId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                        messages.addMainForSuccess(endPoint, new Object[]{resultId, procInstanceName});
                        Object[][] resultInfo = new Object[0][0];
                        resultInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT_SECONDENTRY.getTableName(),
                                new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()}, new Object[]{resultId}, new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName()});
                        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString()))) {
                            sampleId = Integer.valueOf(resultInfo[0][0].toString());
                        }
                        dynamicDataObjects = new Object[]{resultInfo[0][0].toString()};
                        messages.addMainForSuccess(endPoint, new Object[]{resultInfo[0][0], procInstanceName});
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), resultInfo[0][0]);
                    }
                    break;
                case ADD_SAMPLE_MICROORGANISM:
                case ADD_ADHOC_SAMPLE_MICROORGANISM:
                    sampleId = (Integer) argValues[0];
                    Integer numItems = 1;
                    if (argValues.length >= 3) {
                        String numItemsStr = LPNulls.replaceNull(argValues[2]).toString();
                        if (numItemsStr.length() > 0 && LPPlatform.LAB_TRUE.equalsIgnoreCase(LPMath.isNumeric(numItemsStr)[0].toString())) {
                            numItems = Integer.valueOf(numItemsStr);
                        }
                    }
                    for (String orgName : argValues[1].toString().split("\\|")) {
                        actionDiagnosesObj = DataProgramSample.addSampleMicroorganism((Integer) argValues[0], orgName, numItems);
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE_MICROORGANISM.getTableName(), actionDiagnosesObj.getNewObjectId());
                    }
                    if (EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints.ADD_ADHOC_SAMPLE_MICROORGANISM.getName().equalsIgnoreCase(endPoint.getName()) && actionDiagnosesObj != null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                        for (String orgName : argValues[1].toString().split("\\|")) {
                            adhocMicroorganismAdd(orgName);
                        }
                    }
                    dynamicDataObjects = new Object[]{argValues[1].toString().replace("\\|", ", "), sampleId, numItems};
                    if (actionDiagnosesObj != null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                        actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_TRUE, endPoint, dynamicDataObjects);
                    }
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), argValues[0]);
                    break;
                case REMOVE_SAMPLE_MICROORGANISM:
                    sampleId = (Integer) argValues[0];
                    numItems = 1;
                    if (argValues.length >= 3) {
                        String numItemsStr = LPNulls.replaceNull(argValues[2]).toString();
                        if (numItemsStr.length() > 0 && LPPlatform.LAB_TRUE.equalsIgnoreCase(LPMath.isNumeric(numItemsStr)[0].toString())) {
                            numItems = Integer.valueOf(numItemsStr);
                        }
                    }
                    for (String orgName : argValues[1].toString().split("\\|")) {
                        actionDiagnosesObj = DataProgramSample.removeSampleMicroorganism((Integer) argValues[0], orgName, numItems);
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE_MICROORGANISM.getTableName(), actionDiagnosesObj.getNewObjectId());
                    }
                    dynamicDataObjects = new Object[]{argValues[1].toString().replace("\\|", ", "), sampleId, numItems};
                    if (actionDiagnosesObj != null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                        actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_TRUE, endPoint, dynamicDataObjects);
                    }
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), argValues[0]);
                    break;
                case EM_BATCH_INCUB_ADD_SMP:
                    String batchName = argValues[0].toString();
                    Integer batchTemplateId = (Integer) argValues[1];
                    Integer batchTemplateVersion = (Integer) argValues[2];
                    sampleId = (Integer) argValues[3];
                    Integer positionRow = null;
                    if (argValues.length >= 5 && argValues[4] != null && argValues[4].toString().length() > 0) {
                        positionRow = (Integer) argValues[4];
                    }
                    Integer positionCol = null;
                    if (argValues.length >= 6 && argValues[5] != null && argValues[5].toString().length() > 0) {
                        positionCol = (Integer) argValues[5];
                    }

                    Boolean positionOverride = false;
                    if (argValues.length >= 7 && argValues[6] != null && argValues[6].toString().length() > 0) {
                        String positionOverrideStr = argValues[6].toString();
                        if (positionOverrideStr != null && positionOverrideStr.length() > 0) {
                            positionOverride = Boolean.valueOf(positionOverrideStr);
                        }
                    }
                    actionDiagnosesObj = DataBatchIncubator.batchAddSample(batchName, batchTemplateId, batchTemplateVersion,
                             sampleId, positionRow, positionCol, positionOverride);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                        actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{sampleId, batchName, procInstanceName});
                    }
                    dynamicDataObjects = new Object[]{sampleId, batchName};
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), sampleId);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);
                    break;
                case EM_BATCH_INCUB_MOVE_SMP:
                    batchName = argValues[0].toString();
                    batchTemplateId = (Integer) argValues[1];
                    batchTemplateVersion = (Integer) argValues[2];
                    sampleId = (Integer) argValues[3];
                    positionRow = null;
                    if (argValues.length >= 5) {
                        positionRow = (Integer) argValues[4];
                    }
                    positionCol = null;
                    if (argValues.length >= 6) {
                        positionCol = (Integer) argValues[5];
                    }

                    positionOverride = false;
                    if (argValues.length >= 7) {
                        Object positionOverrideStr = argValues[6];
                        if (positionOverrideStr != null && LPNulls.replaceNull(positionOverrideStr).toString().length() > 0) {
                            positionOverride = Boolean.valueOf(positionOverrideStr.toString());
                        }
                    }
                    actionDiagnosesObj = DataBatchIncubator.batchMoveSample(batchName, batchTemplateId, batchTemplateVersion, sampleId, positionRow, positionCol, positionOverride);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                        actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{sampleId, batchName, procInstanceName});
                    }
                    dynamicDataObjects = new Object[]{sampleId, batchName};
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), sampleId);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);
                    break;
                case EM_BATCH_INCUB_REMOVE_SMP:
                    batchName = argValues[0].toString();
                    sampleId = (Integer) argValues[3];
                    if (argValues[1] != null && argValues[1].toString().length() > 0) {
                        batchTemplateId = (Integer) argValues[1];
                        batchTemplateVersion = (Integer) argValues[2];
                        actionDiagnosesObj = DataBatchIncubator.batchRemoveSample(batchName, batchTemplateId, batchTemplateVersion, sampleId);
                    } else {
                        actionDiagnosesObj = DataBatchIncubator.batchRemoveSample(batchName, sampleId);
                    }
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                        actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{sampleId, batchName, procInstanceName});
                    }
                    dynamicDataObjects = new Object[]{sampleId, batchName};
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), sampleId);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName(), batchName);
                    break;
                case ASSIGN_SAMPLE_CULTURE_MEDIA:
                    sampleId = (Integer) argValues[0];
                    String externalProcInstanceName = LPNulls.replaceNull(argValues[1]).toString();
                    String category = LPNulls.replaceNull(argValues[2]).toString();
                    String reference = LPNulls.replaceNull(argValues[3]).toString();
                    String referenceLot = LPNulls.replaceNull(argValues[4]).toString();
                    String useOpenReferenceLot = LPNulls.replaceNull(argValues[5]).toString();

                    numItems = 1;
                    actionDiagnosesObj = DataProgramSample.assignCultureMedia(sampleId, referenceLot, reference, category,
                            new BigDecimal(numItems.toString()), null, externalProcInstanceName, Boolean.valueOf(useOpenReferenceLot));
                    dynamicDataObjects = actionDiagnosesObj.getMessageCodeVariables();
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()),
                            TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), sampleId);
                    if (actionDiagnosesObj != null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                        actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_TRUE, endPoint, dynamicDataObjects);
                    } else {
                        actionDiagnosesObj = new InternalMessage(actionDiagnosesObj.getDiagnostic(), actionDiagnosesObj.getMessageCodeObj(), actionDiagnosesObj.getMessageCodeVariables());
                    }

                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), sampleId);
                    break;

                default:
                    this.endpointExists = false;
                    Rdbms.closeRdbms();
                    RequestDispatcher rd = request.getRequestDispatcher(SampleAPIParams.SERVLET_API_URL);
                    rd.forward(request, null);
            }
            if (actionDiagnosesObj != null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                DataSampleStages smpStage = new DataSampleStages();
                if (Boolean.TRUE.equals(smpStage.isSampleStagesEnable())) {
                    smpStage.dataSampleActionAutoMoveToNext(endPoint.getName().toUpperCase(), sampleId);
                }

            }            
            this.diagnosticObj=actionDiagnosesObj;
            this.relatedObj = rObj;
            this.messageDynamicData = dynamicDataObjects;
        } catch (IOException | ServletException ex) {
            Logger.getLogger(ClassEnvMonSample.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            rObj.killInstance();
        }
    }
    @Override    public StringBuilder getRowArgsRows() {        return null;    }
    @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }
}
