/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.modulesample.SampleAPIParams.SampleAPIactionsEndpoints;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlWhere;
import databases.TblsData;
import databases.TblsDataAudit;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.changeofcustody.ChangeOfCustody;
import functionaljavaa.modulesample.DataModuleSampleAnalysis;
import functionaljavaa.modulesample.DataModuleSampleAnalysisResult;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.samplestructure.DataSample;
import functionaljavaa.samplestructure.DataSampleAnalysis;
import functionaljavaa.samplestructure.DataSampleAnalysisResult;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleErrorTrapping;
import functionaljavaa.samplestructure.DataSampleIncubation;
import static functionaljavaa.samplestructure.DataSampleRevisionTestingGroup.reviewSampleTestingGroup;
import functionaljavaa.samplestructure.DataSampleStages;
import functionaljavaa.samplestructure.DataSampleStructureEnums;
import functionaljavaa.samplestructure.DataSampleStructureStatuses;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ResponseMessages;
import lbplanet.utilities.LPPlatform.LpPlatformErrorTrapping;
import module.monitoring.definition.TblsEnvMonitData;
import modules.masterdata.analysis.ConfigAnalysisStructure;
import trazit.enums.ActionsClass;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassSample implements ActionsClass{

    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    private Object[] diagnostic = new Object[0];
    private InternalMessage diagnosticObj=null;
    private Boolean functionFound = false;
    private EnumIntEndpoints enumConstantByName; 
    

    public ClassSample(HttpServletRequest request, SampleAPIactionsEndpoints endPoint) {

        String[] exceptionsToSampleReviewArr = new String[]{"UNCANCELSAMPLE", "UNREVIEWSAMPLE", "SAMPLESTAGE_MOVETONEXT"};
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Boolean isForTesting = procReqSession.getIsForTesting();
        String procInstanceName = procReqSession.getProcedureInstance();

        ResponseMessages messages = procReqSession.getMessages();
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        DataModuleSampleAnalysis smpAna = new DataModuleSampleAnalysis();
        DataModuleSampleAnalysisResult moduleSmpAnaRes = new DataModuleSampleAnalysisResult();
        DataSample smp = new DataSample(smpAna);
        DataSampleAnalysisResult smpAnaRes = new DataSampleAnalysisResult(moduleSmpAnaRes);
        Integer incubationStage = null;
        Integer sampleId = null;
        InternalMessage actionDiagnosesObj = null;
        this.functionFound = true;
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
            this.diagnostic = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.messageDynamicData = new Object[]{argValues[2].toString()};
            this.relatedObj = rObj;
            rObj.killInstance();
            return;
        }
        for (LPAPIArguments currArg : endPoint.getArguments()) {
            if (GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID.equalsIgnoreCase(currArg.getName())) {
                String sampleIdStr = (String) request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID);
                if (sampleIdStr == null) {
                    sampleIdStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID);
                }
                if (sampleIdStr != null) {
                    sampleId = Integer.valueOf(sampleIdStr);
                }
            }
        }
        if (sampleId != null && !LPArray.valueInArray(exceptionsToSampleReviewArr, endPoint.getName())) {
            Object[][] sampleStatus = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(),
                    new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, new String[]{TblsData.Sample.STATUS.getName()});
            actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_ALREADY_REVIEWED, null);
            if ((sampleStatus[0][0].toString().equalsIgnoreCase(DataSampleStructureStatuses.SampleStatuses.CANCELED.getStatusCode("")))
                    || (sampleStatus[0][0].toString().equalsIgnoreCase(DataSampleStructureStatuses.SampleStatuses.REVIEWED.getStatusCode("")))) {
                this.diagnosticObj = actionDiagnosesObj;
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), actionDiagnosesObj.getNewObjectId());
                this.messageDynamicData = new Object[]{sampleId};
                this.relatedObj = rObj;
                rObj.killInstance();
                return;
            }
        }
        this.enumConstantByName=endPoint;
        this.functionFound = true;
        switch (endPoint) {
            case LOGSAMPLE:
                String sampleTemplate = argValues[0].toString();
                Integer sampleTemplateVersion = (Integer) argValues[1];
                String specName = argValues[2].toString();
                Integer specVersion = (Integer) argValues[3];
                String variationName = argValues[4].toString();
                String fieldName = argValues[5].toString();
                String fieldValue = argValues[6].toString();
                String[] fieldNames = null;
                Object[] fieldValues = null;
                //diagn = null;
                if (fieldName != null && fieldName.length() > 0) {
                    fieldNames = fieldName.split("\\|");
                }
                if (fieldValue != null && fieldValue.length() > 0) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnosesObj = (InternalMessage) fieldValues[1];
                    break;
                }
                if (fieldNames != null) {
                    Object[] checkTwoArraysSameLength = LPArray.checkTwoArraysSameLength(fieldNames, fieldValues);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkTwoArraysSameLength[0].toString())) {
                        actionDiagnosesObj=new InternalMessage(LPPlatform.LAB_FALSE, checkTwoArraysSameLength[checkTwoArraysSameLength.length - 1].toString(), null, null);
                    }
                }
                if (actionDiagnosesObj == null) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, new String[]{TblsData.Sample.SPEC_CODE.getName(), TblsData.Sample.SPEC_CODE_VERSION.getName(), TblsData.Sample.SPEC_VARIATION_NAME.getName()});
                    fieldValues = LPArray.addValueToArray1D(fieldValues, new Object[]{specName, specVersion, variationName});
                    if (argValues[7] == null || argValues[7].toString().length() == 0) {
                        actionDiagnosesObj = smp.logSample(sampleTemplate, sampleTemplateVersion, fieldNames, fieldValues);
                    } else {
                        Integer numSamplesToLog = (Integer) argValues[7];
                        actionDiagnosesObj = smp.logSample(sampleTemplate, sampleTemplateVersion, fieldNames, fieldValues, numSamplesToLog);
                    }
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), actionDiagnosesObj.getNewObjectId());
                    this.messageDynamicData = new Object[]{actionDiagnosesObj.getNewObjectId()};
                }
                messageDynamicData = actionDiagnosesObj.getMessageCodeVariables();
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), actionDiagnosesObj.getNewObjectId());
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                    sampleId = Integer.valueOf(actionDiagnosesObj.getNewObjectId().toString());
                    actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{argValues[0], procInstanceName}, sampleId);
                    messages.addMainForSuccess(endPoint, new Object[]{sampleId, procInstanceName, (String) argValues[5]});
                }                
                break;

            case RECEIVESAMPLE:
                sampleId = (Integer) argValues[0];
                actionDiagnosesObj = smp.sampleReception(sampleId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case SETSAMPLINGDATE:
                sampleId = (Integer) argValues[0];
                actionDiagnosesObj = smp.setSamplingDate(sampleId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{LPDate.getCurrentTimeStamp(), sampleId};
                break;
            case CHANGESAMPLINGDATE:
                LocalDateTime newDate = null;
                sampleId = (Integer) argValues[0];
                if (argValues[1] == null) {
                    actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, LpPlatformErrorTrapping.NEWDATETIMENULL_OR_WRONGFORMAT, new Object[]{});
                } else {
                    newDate = LPDate.stringFormatToLocalDateTime(argValues[1].toString());
                    if (newDate == null) {
                        actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, LpPlatformErrorTrapping.NEWDATETIMENULL_OR_WRONGFORMAT, new Object[]{LPNulls.replaceNull(newDate)});
                    } else {
                        actionDiagnosesObj = smp.changeSamplingDate(sampleId, newDate);
                    }
                }
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{LPNulls.replaceNull(newDate), sampleId};
                break;
            case SETSAMPLINGDATEEND:
                sampleId = (Integer) argValues[0];
                actionDiagnosesObj = smp.setSamplingDateEnd(sampleId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{LPDate.getCurrentTimeStamp(), sampleId};
                break;
            case CHANGESAMPLINGDATEEND:
                newDate = null;
                sampleId = (Integer) argValues[0];
                if (argValues[1] == null) {
                    actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, LpPlatformErrorTrapping.NEWDATETIMENULL_OR_WRONGFORMAT, new Object[]{});
                } else {
                    newDate = LPDate.stringFormatToLocalDateTime(argValues[1].toString());
                    if (newDate == null) {
                        actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, LpPlatformErrorTrapping.NEWDATETIMENULL_OR_WRONGFORMAT, new Object[]{LPNulls.replaceNull(newDate)});
                    } else {
                        actionDiagnosesObj = smp.changeSamplingDateEnd(sampleId, newDate);
                    }
                }
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{LPNulls.replaceNull(newDate), sampleId};

                break;
            case SAMPLINGCOMMENTADD:
                sampleId = (Integer) argValues[0];
                String comment = null;
                comment = argValues[1].toString();
                actionDiagnosesObj = smp.sampleReceptionCommentAdd(sampleId, comment);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case SAMPLINGCOMMENTREMOVE:
                sampleId = (Integer) argValues[0];
                actionDiagnosesObj = smp.sampleReceptionCommentRemove(sampleId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case INCUBATIONSTART:
                incubationStage = 1;
                sampleId = (Integer) argValues[0];
                String incubName = argValues[1].toString();
                BigDecimal tempReading = null;
                actionDiagnosesObj = DataSampleIncubation.setSampleStartIncubationDateTime(sampleId, incubationStage, incubName, tempReading);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case INCUBATION2START:
                incubationStage = 2;
                sampleId = (Integer) argValues[0];
                incubName = argValues[1].toString();
                tempReading = null;
                actionDiagnosesObj = DataSampleIncubation.setSampleStartIncubationDateTime(sampleId, incubationStage, incubName, tempReading);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case INCUBATIONEND:
                incubationStage = 1;
                sampleId = (Integer) argValues[0];
                incubName = argValues[1].toString();
                tempReading = null;
                actionDiagnosesObj = DataSampleIncubation.setSampleEndIncubationDateTime(sampleId, incubationStage, incubName, tempReading);
                //diagn = (Object[]) diagn[0];
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case INCUBATION2END:
                incubationStage = 2;
                sampleId = (Integer) argValues[0];
                incubName = argValues[1].toString();
                tempReading = null;
                actionDiagnosesObj = DataSampleIncubation.setSampleEndIncubationDateTime(sampleId, incubationStage, incubName, tempReading);                
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case SAMPLEANALYSISADD:
                sampleId = (Integer) argValues[0];
                String[] fieldNameArr = null;
                Object[] fieldValueArr = null;
                fieldName = argValues[1].toString();
                fieldNameArr = fieldName.split("\\|");
                fieldValue = argValues[2].toString();
                fieldValueArr = fieldValue.split("\\|");
                fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray((String[]) fieldValueArr);
                if (fieldValueArr != null && fieldValueArr.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValueArr[0].toString())) {
                    actionDiagnosesObj = (InternalMessage) fieldValueArr[1];
                    break;
                }                
                actionDiagnosesObj = DataSampleAnalysis.sampleAnalysisAddtoSample(sampleId, fieldNameArr, fieldValueArr);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = actionDiagnosesObj.getMessageCodeVariables();
                break;
            case SAMPLEANALYSISREMOVE:
                sampleId = (Integer) argValues[0];
                Integer testId = (Integer) argValues[1];
                fieldNameArr = null;
                fieldValueArr = null;
                fieldName = argValues[2].toString();
                fieldNameArr = fieldName.split("\\|");
                fieldValue = argValues[3].toString();
                fieldValueArr = fieldValue.split("\\|");
                fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray((String[]) fieldValueArr);
                if (fieldValueArr != null && fieldValueArr.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValueArr[0].toString())) {
                    actionDiagnosesObj = (InternalMessage) fieldValueArr[1];
                    break;
                }
                actionDiagnosesObj = DataSampleAnalysis.sampleAnalysisRemovetoSample(sampleId, testId, fieldNameArr, fieldValueArr);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = actionDiagnosesObj.getMessageCodeVariables();
                break;
            case REENTERRESULT:
            case ENTERRESULT:
                Integer resultId = (Integer) argValues[0];
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
                    if (currRawValue != null && currRawValue.length() > 0 && SampleAPIParams.SampleAPIactionsEndpoints.ENTERRESULT.getName().equalsIgnoreCase(endPoint.getName())) {
                        procReqSession.killIt();
                        request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, SampleAPIParams.SampleAPIactionsEndpoints.REENTERRESULT.getName());
                        procReqSession = ProcedureRequestSession.getInstanceForActions(request, null, isForTesting);
                        if (Boolean.TRUE.equals(procReqSession.getHasErrors())) {
                            procReqSession.killIt();
                            actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, 
                                    procReqSession.getErrorMessageCodeObj(), new Object[]{resultId.toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
                            break;
                        }
                    }
                }
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultEntry(resultId, rawValueResult, smp);                
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), resultId);
                this.messageDynamicData = new Object[]{resultId};
                break;
            case ENTERRESULT_PARSING:
                resultId = (Integer) argValues[0];
                byte[] fileInBytes = (byte[]) argValues[1];
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultEntryByParsing(resultId, fileInBytes, smp);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{resultId};
                break;
            case RESULT_CHANGE_UOM:
                resultId = 0;
                resultId = (Integer) argValues[0];
                String newUOM = null;
                newUOM = argValues[1].toString();
                actionDiagnosesObj = smpAnaRes.sarChangeUom(resultId, newUOM, smp);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId, newUOM};
                break;
            case ENTERRESULT_BY_ANALYSIS_NAME:
                sampleId = (Integer) argValues[0];
                String analysisName = argValues[1].toString();
                rawValueResult = argValues[2].toString();
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultEntryByAnalysisName(sampleId, analysisName, rawValueResult, smp);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId, analysisName};
                break;
            case REVIEWSAMPLE:
                sampleId = (Integer) argValues[0];
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultReview(sampleId, null, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case UNREVIEWSAMPLE:
                sampleId = (Integer) argValues[0];
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultUnReview(sampleId, null, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case REVIEWSAMPLE_TESTINGGROUP:
                sampleId = (Integer) argValues[0];
                String testingGroup = argValues[1].toString();
                actionDiagnosesObj = reviewSampleTestingGroup(sampleId, testingGroup);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId, testingGroup};
                break;
            case REVIEWTEST:
                testId = (Integer) argValues[0];
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultReview(null, testId, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{testId};
                break;
            case UNREVIEWTEST:
                testId = (Integer) argValues[0];
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultUnReview(null, testId, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), testId);
                this.messageDynamicData = new Object[]{testId};
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                    messages.addMainForSuccess(endPoint, this.messageDynamicData);
                }
                break;
            case REVIEWTEST_BY_SAMPLE_ID_AND_ANALYSIS_NAME:
                sampleId = (Integer) argValues[0];
                analysisName = argValues[1].toString();
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultReviewBySampleAndAnalysis(sampleId, analysisName);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId, analysisName};
                break;
            case REVIEWRESULT:
                resultId = (Integer) argValues[0];
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultReview(null, null, resultId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case UNREVIEWRESULT:
                resultId = (Integer) argValues[0];
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultUnReview(null, null, resultId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), resultId);
                this.messageDynamicData = new Object[]{resultId};
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                    messages.addMainForSuccess(endPoint, this.messageDynamicData);
                }
                break;
            case CANCELSAMPLE:
                sampleId = (Integer) argValues[0];
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultCancel(sampleId, null, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                    messages.addMainForSuccess(endPoint, this.messageDynamicData);
                }
                break;
            case UNCANCELSAMPLE:
                sampleId = (Integer) argValues[0];
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultUnCancel(sampleId, null, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                    messages.addMainForSuccess(endPoint, this.messageDynamicData);
                }
                break;
            case CANCELTEST:
                testId = (Integer) argValues[0];
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultCancel(null, testId, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), testId);
                this.messageDynamicData = new Object[]{sampleId};
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                    messages.addMainForSuccess(endPoint, this.messageDynamicData);
                }
                break;
            case UNCANCELTEST:
                testId = (Integer) argValues[0];
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultUnCancel(null, testId, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), testId);
                this.messageDynamicData = new Object[]{testId};
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                    messages.addMainForSuccess(endPoint, this.messageDynamicData);
                }
                break;
            case CANCELRESULT:
                resultId = (Integer) argValues[0];
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultCancel(null, null, resultId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), resultId);
                this.messageDynamicData = new Object[]{sampleId};
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                    messages.addMainForSuccess(endPoint, this.messageDynamicData);
                }
                break;
            case UNCANCELRESULT:
                resultId = (Integer) argValues[0];
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultUnCancel(null, null, resultId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), resultId);
                this.messageDynamicData = new Object[]{resultId};
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                    messages.addMainForSuccess(endPoint, this.messageDynamicData);
                }
                break;
            case TESTASSIGNMENT:
                testId = (Integer) argValues[0];
                String newAnalyst = argValues[1].toString();
                actionDiagnosesObj = DataSampleAnalysis.sampleAnalysisAssignAnalyst(testId, newAnalyst);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), testId);
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case GETSAMPLEINFO:
                sampleId = (Integer) argValues[0];
                String sampleFieldToRetrieve = argValues[1].toString();

                String[] sampleFieldToRetrieveArr = sampleFieldToRetrieve.split("\\|");
                String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());

                String[] sortFieldsNameArr = null;
                String sortFieldsName = argValues[2].toString();
                if (!((sortFieldsName == null) || (sortFieldsName.contains("undefined")))) {
                    sortFieldsNameArr = sortFieldsName.split("\\|");
                } else {
                    sortFieldsNameArr = null;
                }

                String diagnStr = Rdbms.getRecordFieldsByFilterJSON(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE.getTableName(),
                        new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, sampleFieldToRetrieveArr, sortFieldsNameArr);
                if (diagnStr.contains(LPPlatform.LAB_FALSE)) {
                    LPFrontEnd.responseError(diagnStr.split("\\|"));
                } else {
                    LPFrontEnd.servletReturnSuccess(request, null, diagnStr);
                }
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                return;
            case COC_STARTCHANGE:
                Integer objectId = (Integer) argValues[0];
                String custodianCandidate = argValues[1].toString();
                ChangeOfCustody coc = new ChangeOfCustody();
                actionDiagnosesObj = coc.cocStartChange(TblsData.TablesData.SAMPLE_COC, TblsData.SampleCoc.SAMPLE_ID, objectId, custodianCandidate);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case COC_CONFIRMCHANGE:
                sampleId = (Integer) argValues[0];
                String confirmChangeComment = argValues[1].toString();
                coc = new ChangeOfCustody();
                actionDiagnosesObj = coc.cocConfirmedChange(TblsData.TablesData.SAMPLE_COC, TblsData.SampleCoc.SAMPLE_ID, sampleId, confirmChangeComment);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case COC_ABORTCHANGE:
                sampleId = (Integer) argValues[0];
                String cancelChangeComment = argValues[1].toString();
                coc = new ChangeOfCustody();
                actionDiagnosesObj = coc.cocAbortedChange(TblsData.TablesData.SAMPLE_COC, TblsData.SampleCoc.SAMPLE_ID, sampleId, cancelChangeComment);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case LOGALIQUOT:
                sampleId = (Integer) argValues[0];
                fieldName = argValues[1].toString();
                fieldValue = argValues[2].toString();
                fieldNames = null;
                fieldValues = null;
                if (fieldName != null) {
                    fieldNames = fieldName.split("\\|");
                }
                if (fieldValue != null) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnosesObj = (InternalMessage) fieldValues[1];
                    break;
                }
                actionDiagnosesObj = smp.logSampleAliquot(sampleId,
                        // sampleTemplate, sampleTemplateVersion,
                        fieldNames, fieldValues);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ALIQ.getTableName(), actionDiagnosesObj.getNewObjectId());
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case LOGSUBALIQUOT:
                Integer aliquotId = (Integer) argValues[0];
                fieldName = argValues[1].toString();
                fieldValue = argValues[2].toString();
                fieldNames = null;
                fieldValues = null;
                if (fieldName != null) {
                    fieldNames = fieldName.split("\\|");
                }
                if (fieldValue != null) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnosesObj = (InternalMessage) fieldValues[1];
                    break;
                }
                actionDiagnosesObj = smp.logSampleSubAliquot(aliquotId,
                        // sampleTemplate, sampleTemplateVersion,
                        fieldNames, fieldValues);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ALIQ.getTableName(), sampleId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ALIQ_SUB.getTableName(), actionDiagnosesObj.getNewObjectId());
                this.messageDynamicData = new Object[]{sampleId};
                break;
            case SAMPLESTAGE_MOVETOPREVIOUS:
            case SAMPLESTAGE_MOVETONEXT:
                //Object[] actionDiagnoses=null;
                DataSampleStages smpStage = new DataSampleStages();
                if (Boolean.FALSE.equals(smpStage.isSampleStagesEnable())) {
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, null,
                            SampleAudit.SampleAuditErrorTrapping.STAGESDETECTED_BUT_SAMPLESTAGES_NOT_ENABLED.getErrorCode(), 
                            new Object[]{"Samples", procInstanceName});
                    return;
                }
                sampleId = (Integer) argValues[0];
                String sampleStage = null;
                if (argValues.length > 1 && argValues[1] != null) {
                    argValues[1].toString();
                }
                String sampleStageNext = null;
                if (argValues.length > 2 && argValues[2] != null) {
                    sampleStageNext = argValues[2].toString();
                }
                if ((sampleStage == null) || (sampleStage.equalsIgnoreCase("undefined")) || (sampleStage.length() == 0)) {
                    Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(),
                            new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId},
                            new String[]{TblsData.Sample.CURRENT_STAGE.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, null, LPArray.array2dTo1d(sampleInfo));
                        return;
                    }
                    sampleStage = sampleInfo[0][0].toString();
                }
                if (SampleAPIactionsEndpoints.SAMPLESTAGE_MOVETONEXT.getName().equalsIgnoreCase(endPoint.getName())) {
                    actionDiagnosesObj = smpStage.moveToNextStage(sampleId, sampleStage, sampleStageNext);
                }
                if (SampleAPIactionsEndpoints.SAMPLESTAGE_MOVETOPREVIOUS.getName().equalsIgnoreCase(endPoint.getName())) {
                    actionDiagnosesObj = smpStage.moveToPreviousStage(sampleId, sampleStage, sampleStageNext);
                }
                String[] sampleFieldName = new String[]{TblsData.Sample.CURRENT_STAGE.getName(), TblsData.Sample.PREVIOUS_STAGE.getName()};
                Object[] sampleFieldValue = new Object[0];
                String newSampleStage = actionDiagnosesObj == null ? "" : actionDiagnosesObj.getNewObjectId().toString();
                if (actionDiagnosesObj == null) {
                    sampleFieldValue = new Object[]{"", sampleStage};
                } else {
                    sampleFieldValue = new Object[]{newSampleStage, sampleStage};
                }
                if (actionDiagnosesObj != null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                    smpStage.dataSampleStagesTimingCapture(sampleId, sampleStage, DataSampleStages.SampleStageTimingCapturePhases.END.name());
                    smpStage.dataSampleStagesTimingCapture(sampleId, actionDiagnosesObj.getNewObjectId().toString(), DataSampleStages.SampleStageTimingCapturePhases.START.toString());
                    SqlWhere sqlWhere = new SqlWhere();
                    sqlWhere.addConstraint(TblsData.Sample.SAMPLE_ID, null, new Object[]{sampleId}, "");
                    RdbmsObject updateActionDiagnosesObj = Rdbms.updateTableRecordFieldsByFilter(TblsData.TablesData.SAMPLE,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE, sampleFieldName), sampleFieldValue, sqlWhere, null);
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(endPoint.getAuditEventObj(), TblsData.TablesData.SAMPLE.getTableName(), sampleId, sampleId, null, null, sampleFieldName, sampleFieldValue);
                    if (Boolean.TRUE.equals(updateActionDiagnosesObj.getRunSuccess())) {
                        actionDiagnosesObj=new InternalMessage(updateActionDiagnosesObj.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, updateActionDiagnosesObj.getErrorMessageCode(), updateActionDiagnosesObj.getErrorMessageVariables());
                        
                    }
                    if ("END".equalsIgnoreCase(newSampleStage)) {
                        smp.sampleReview(sampleId);
                    }
                    this.messageDynamicData = new Object[]{sampleId};
                } else {
                    this.messageDynamicData = actionDiagnosesObj == null ? new Object[]{} : new Object[]{actionDiagnosesObj.getNewObjectId().toString()};
                }
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                break;
            case SAMPLEAUDIT_SET_AUDIT_ID_REVIEWED:
                Integer auditId = (Integer) argValues[0];
                Object[][] auditInfo = QueryUtilitiesEnums.getTableData(TblsDataAudit.TablesDataAudit.SAMPLE,
                        EnumIntTableFields.getTableFieldsFromString(TblsDataAudit.TablesDataAudit.SAMPLE, new String[]{TblsDataAudit.Sample.SAMPLE_ID.getName()}),
                        new String[]{TblsDataAudit.Sample.AUDIT_ID.getName()}, new Object[]{auditId},
                        new String[]{TblsDataAudit.Sample.AUDIT_ID.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditInfo[0][0].toString())) {
                    actionDiagnosesObj = new InternalMessage(auditInfo[0][0].toString(), SampleAudit.SampleAuditErrorTrapping.AUDIT_RECORD_NOT_FOUND, new Object[]{auditId});
                    sampleId = null;
                } else {
                    actionDiagnosesObj = SampleAudit.sampleAuditSetAuditRecordAsReviewed(procInstanceName, auditId, ProcedureRequestSession.getInstanceForActions(null, null, null).getToken().getPersonName());
                    sampleId = Integer.valueOf(auditInfo[0][0].toString());
                }
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsDataAudit.TablesDataAudit.SAMPLE.getTableName(), auditId);
                this.messageDynamicData = new Object[]{auditId, sampleId};
                break;
            default:
                break;
        }
        if (actionDiagnosesObj!= null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
            DataSampleStages smpStage = new DataSampleStages();
            if (Boolean.TRUE.equals(smpStage.isSampleStagesEnable()) && sampleId != null) {
                smpStage.dataSampleActionAutoMoveToNext(endPoint.getName().toUpperCase(), sampleId);
            }
        }
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
            actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_TRUE, endPoint,messageDynamicData, sampleId);
            messages.addMainForSuccess(endPoint, messageDynamicData);                    
        }
        this.diagnostic = null;
        this.diagnosticObj=actionDiagnosesObj;
        this.relatedObj = rObj;
        rObj.killInstance();
    }

    @Override    public InternalMessage getDiagnosticObj() {        return diagnosticObj;    }

    @Override    public StringBuilder getRowArgsRows() {        return null;    }
    @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }
    @Override    public Object[] getMessageDynamicData() {        return this.messageDynamicData;    }
    @Override    public RelatedObjects getRelatedObj() {        return this.relatedObj;    }
    public Boolean getEndpointExists() {        return this.endpointExists;    }
    @Override    public Object[] getDiagnostic() {        return this.diagnostic;    }
    public Boolean getFunctionFound() {        return functionFound;    }

    @Override    public void initializeEndpoint(String actionName) {        throw new UnsupportedOperationException("Not supported yet.");}
    @Override    public void createClassEnvMonAndHandleExceptions(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {        throw new UnsupportedOperationException("Not supported yet.");}

    @Override
    public HttpServletResponse getHttpResponse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
