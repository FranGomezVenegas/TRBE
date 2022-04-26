/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.modulesample.SampleAPIParams.SampleAPIEndpoints;
import databases.Rdbms;
import databases.TblsData;
import databases.TblsDataAudit;
import databases.features.Token;
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
import trazit.session.ApiMessageReturn;
import trazit.session.ResponseMessages;
import lbplanet.utilities.LPPlatform.LpPlatformErrorTrapping;
/**
 *
 * @author User
 */
public class ClassSample {
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
    public Boolean getFunctionFound() {
        return functionFound;
    }    
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;
    
    public ClassSample(HttpServletRequest request, SampleAPIEndpoints endPoint){
        
        String[] exceptionsToSampleReviewArr=new String[]{"UNCANCELSAMPLE", "UNREVIEWSAMPLE"};
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Boolean isForTesting = procReqSession.getIsForTesting();
        String procInstanceName=procReqSession.getProcedureInstance();
        if (procInstanceName==null){
            this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "procInstanceNameIsNull", null);
            return;
        }
        ResponseMessages messages = procReqSession.getMessages();
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        String schemaDataName="";
        String language="";
        DataModuleSampleAnalysis smpAna = new DataModuleSampleAnalysis();
        DataModuleSampleAnalysisResult moduleSmpAnaRes = new DataModuleSampleAnalysisResult();
        DataSample smp = new DataSample(smpAna);
        DataSampleAnalysisResult smpAnaRes = new DataSampleAnalysisResult(moduleSmpAnaRes);
        try{
            Integer incubationStage=null;
            Integer sampleId = null;
            Object[] diagn = null;
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());  
            for (LPAPIArguments currArg: endPoint.getArguments()){
                if (GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID.equalsIgnoreCase(currArg.getName())){
                    String sampleIdStr = (String) request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID.toString());
                    if (sampleIdStr==null)
                        sampleIdStr = (String) request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID.toString());
                    if (sampleIdStr!=null) 
                        sampleId = Integer.valueOf(sampleIdStr);
                }
            }
            if (sampleId!=null && !LPArray.valueInArray(exceptionsToSampleReviewArr, endPoint.getName())){
                Object[][] sampleStatus=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                    new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, new String[]{TblsData.Sample.STATUS.getName()});
                diagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_ALREADY_REVIEWED, null);
                if ( (sampleStatus[0][0].toString().equalsIgnoreCase(DataSampleStructureStatuses.SampleStatuses.CANCELED.getStatusCode(""))) ||
                     (sampleStatus[0][0].toString().equalsIgnoreCase(DataSampleStructureStatuses.SampleStatuses.REVIEWED.getStatusCode(""))) ){               
                    this.diagnostic=diagn;
    //                Object[] dynamicDataObjects = new Object[]{sampleId};
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), diagn[diagn.length-1]);
                    this.messageDynamicData=new Object[]{sampleId};
                    this.relatedObj=rObj;
                    rObj.killInstance();
                    return;
                }
            }
            this.functionFound=true;
            switch (endPoint){
                case LOGSAMPLE:
                    String sampleTemplate= argValues[0].toString();
                    Integer sampleTemplateVersion = (Integer) argValues[1];
                    String fieldName=argValues[2].toString();
                    String fieldValue=argValues[3].toString();
                    String[] fieldNames=null;
                    Object[] fieldValues=null;
                    if (fieldName!=null) fieldNames = fieldName.split("\\|");
                    if (fieldValue!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                        diagn=fieldValues;
                        break;
                    }

    //                String numSmplsToLogStr = argValues[7].toString();
    //                Integer numSamplesToLog=(Integer) argValues[7];

                    if (argValues[6]==null){
                        diagn = smp.logSample(sampleTemplate, sampleTemplateVersion, fieldNames, fieldValues);
                    }else{
                        Integer numSamplesToLog=(Integer) argValues[6];
                        diagn = smp.logSample(sampleTemplate, sampleTemplateVersion, fieldNames, fieldValues, numSamplesToLog);
                    }
                    Object[] dynamicDataObjects = new Object[]{diagn[diagn.length-1]};
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), diagn[diagn.length-1]);
                    this.messageDynamicData=new Object[]{diagn[diagn.length-1]};
                    break;
                case RECEIVESAMPLE:
                    sampleId = (Integer) argValues[0];
                    diagn = smp.sampleReception(sampleId);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case SETSAMPLINGDATE:
                    sampleId = (Integer) argValues[0];
                    diagn = smp.setSamplingDate(sampleId);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{LPDate.getCurrentTimeStamp(), sampleId};
                    break;
                case CHANGESAMPLINGDATE:
                    sampleId = (Integer) argValues[0];
                    LocalDateTime newDate=(LocalDateTime) argValues[1];
                    if (newDate==null)
                        diagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LpPlatformErrorTrapping.NEWDATETIMENULL_OR_WRONGFORMAT, new Object[]{LPNulls.replaceNull(newDate)});
                    else
                        diagn = smp.changeSamplingDate(sampleId, newDate);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{LPNulls.replaceNull(newDate), sampleId};
                    break;
                case SETSAMPLINGDATEEND:
                    sampleId = (Integer) argValues[0];
                    diagn = smp.setSamplingDateEnd(sampleId);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{LPDate.getCurrentTimeStamp(), sampleId};
                    break;
                case CHANGESAMPLINGDATEEND:
                    sampleId = (Integer) argValues[0];
                    newDate=(LocalDateTime) argValues[1];
                    if (newDate==null)
                        diagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LpPlatformErrorTrapping.NEWDATETIMENULL_OR_WRONGFORMAT, new Object[]{LPNulls.replaceNull(newDate)});
                    else
                        diagn = smp.changeSamplingDateEnd(sampleId, newDate);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{LPNulls.replaceNull(newDate), sampleId};
                    break;
                case SAMPLINGCOMMENTADD:
                    sampleId = (Integer) argValues[0];
                    String comment=null;
                    comment = argValues[1].toString();
                    diagn = smp.sampleReceptionCommentAdd(sampleId, comment);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case SAMPLINGCOMMENTREMOVE:
                    sampleId = (Integer) argValues[0];
                    diagn = smp.sampleReceptionCommentRemove(sampleId);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case INCUBATIONSTART:
                    incubationStage=1;
                    sampleId = (Integer) argValues[0];
                    String incubName=argValues[1].toString();
                    BigDecimal tempReading=null;
                    diagn = DataSampleIncubation.setSampleStartIncubationDateTime(sampleId, incubationStage, incubName, tempReading);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case INCUBATION2START:
                    incubationStage=2;
                    sampleId = (Integer) argValues[0];
                    incubName=argValues[1].toString();
                    tempReading=null;
                    diagn = DataSampleIncubation.setSampleStartIncubationDateTime(sampleId, incubationStage, incubName, tempReading);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case INCUBATIONEND:
                    incubationStage=1;
                    sampleId = (Integer) argValues[0];
                    incubName= argValues[1].toString();
                    tempReading=null;
                    diagn = DataSampleIncubation.setSampleEndIncubationDateTime(sampleId, incubationStage, incubName, tempReading);
                    diagn=(Object[])diagn[0];
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case INCUBATION2END:
                    incubationStage=2;
                    sampleId = (Integer) argValues[0];
                    incubName= argValues[1].toString();
                    tempReading=null;
                    diagn = DataSampleIncubation.setSampleEndIncubationDateTime(sampleId, incubationStage, incubName, tempReading);
                    diagn=(Object[])diagn[0];
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case SAMPLEANALYSISADD:
                    sampleId = (Integer) argValues[0];
                    String[] fieldNameArr = null;
                    Object[] fieldValueArr = null;
                    fieldName = argValues[1].toString();
                    fieldNameArr =fieldName.split("\\|");
                    fieldValue = argValues[2].toString();
                    fieldValueArr = fieldValue.split("\\|");
                    fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray((String[]) fieldValueArr);
                    if (fieldValueArr!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValueArr[0].toString())){
                        diagn=fieldValueArr;
                        break;
                    }
                    diagn = DataSampleAnalysis.sampleAnalysisAddtoSample(sampleId, fieldNameArr, fieldValueArr);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case REENTERRESULT:
                case ENTERRESULT:
                    Integer resultId = (Integer) argValues[0];
                    String rawValueResult = argValues[1].toString();
                        Object[][] resultData = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                                new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()}, new Object[]{resultId}, 
                                new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.ANALYSIS.getName(), 
                                    TblsData.SampleAnalysisResult.METHOD_NAME.getName(), TblsData.SampleAnalysisResult.METHOD_VERSION.getName(), TblsData.SampleAnalysisResult.PARAM_NAME.getName(), 
                                    TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.RAW_VALUE.getName(), TblsData.SampleAnalysisResult.UOM.getName(), 
                                    TblsData.SampleAnalysisResult.UOM_CONVERSION_MODE.getName()});
                        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString()))
                            diagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleStructureEnums.DataSampleAnalysisResultErrorTrapping.NOT_FOUND, new Object[]{resultId.toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
                        else{            
                            String currRawValue = (String) resultData[0][7];
                            if (currRawValue!=null && currRawValue.length()>0 && SampleAPIParams.SampleAPIEndpoints.ENTERRESULT.getName().equalsIgnoreCase(endPoint.getName())){
                                procReqSession.killIt();
                                request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, SampleAPIParams.SampleAPIEndpoints.REENTERRESULT.getName());
                                procReqSession = ProcedureRequestSession.getInstanceForActions(request, null, isForTesting);
                                if (procReqSession.getHasErrors()){
                                    procReqSession.killIt();
                                    diagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, procReqSession.getErrorMessage(), new Object[]{resultId.toString(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())});
                                    break;
                                }
                            }
                        }                
                    Object[] actionDiagnoses = smpAnaRes.sampleAnalysisResultEntry(resultId, rawValueResult, smp);
                    diagn=(Object[]) actionDiagnoses[0];  
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), resultId);
                    this.messageDynamicData=new Object[]{resultId};
                    break;
                case RESULT_CHANGE_UOM:
                    resultId = 0;
                    resultId = (Integer) argValues[0];
                    String newUOM = null;
                    newUOM = argValues[1].toString();
                    diagn = smpAnaRes.sarChangeUom(resultId, newUOM, smp);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId, newUOM};
                    break;
                case ENTERRESULT_BY_ANALYSIS_NAME:
                    sampleId = (Integer) argValues[0];
                    String analysisName = argValues[1].toString();
                    rawValueResult = argValues[2].toString();
                    diagn = smpAnaRes.sampleAnalysisResultEntryByAnalysisName(sampleId, analysisName, rawValueResult, smp);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId, analysisName};
                    break;
                case REVIEWSAMPLE:
                    sampleId = (Integer) argValues[0];
                    diagn = smpAnaRes.sampleAnalysisResultReview(sampleId, null, null);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case REVIEWSAMPLE_TESTINGGROUP:
                    sampleId = (Integer) argValues[0];
                    String testingGroup = argValues[1].toString();
                    diagn = reviewSampleTestingGroup(sampleId, testingGroup);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId, testingGroup};
                    break;
                case REVIEWTEST:
                    Integer testId = (Integer) argValues[0];
                    diagn = smpAnaRes.sampleAnalysisResultReview(null, testId, null);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{testId};
                    break;
                case REVIEWTEST_BY_SAMPLE_ID_AND_ANALYSIS_NAME:
                    sampleId = (Integer) argValues[0];
                    analysisName = argValues[1].toString();
                    diagn = smpAnaRes.sampleAnalysisResultReviewBySampleAndAnalysis(sampleId, analysisName);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId, analysisName};
                    break;
                case REVIEWRESULT:
                    Integer objectId = (Integer) argValues[0];
                    String objectLevel = argValues[1].toString();
                    testId = null; 
                    resultId = null;
                    if (objectLevel.equalsIgnoreCase(GlobalAPIsParams.REQUEST_PARAM_OBJECT_LEVEL_SAMPLE)){sampleId = objectId;}
                    if (objectLevel.equalsIgnoreCase(GlobalAPIsParams.REQUEST_PARAM_OBJECT_LEVEL_TEST)){testId = objectId;}
                    if (objectLevel.equalsIgnoreCase(GlobalAPIsParams.REQUEST_PARAM_OBJECT_LEVEL_RESULT)){resultId = objectId;}
                    //diagn=smp.sampleReview(procInstanceName, token.getPersonName(), token.getUserRole(), sampleId, Integer.parseInt(token.getAppSessionId()));
                    diagn = smpAnaRes.sampleAnalysisResultReview(sampleId, testId, resultId);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case CANCELSAMPLE:
                    sampleId = (Integer) argValues[0];
                    diagn = smpAnaRes.sampleAnalysisResultCancel(sampleId, null, null);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString()))
                        messages.addMainForSuccess(endPoint, this.messageDynamicData);
                    break;
                case CANCELTEST:
                    testId = (Integer) argValues[0];
                    diagn = smpAnaRes.sampleAnalysisResultCancel(null, testId, null);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), testId);
                    this.messageDynamicData=new Object[]{sampleId};
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString()))
                        messages.addMainForSuccess(endPoint, this.messageDynamicData);
                    break;
                case CANCELRESULT:
                    resultId = (Integer) argValues[0];
                    diagn = smpAnaRes.sampleAnalysisResultCancel(null, null, resultId);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), resultId);
                    this.messageDynamicData=new Object[]{sampleId};
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString()))
                        messages.addMainForSuccess(endPoint, this.messageDynamicData);
                    break;
                case UNCANCELSAMPLE:
                    sampleId = (Integer) argValues[0];
                    diagn = smpAnaRes.sampleAnalysisResultUnCancel(sampleId, null, null);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString()))
                        messages.addMainForSuccess(endPoint, this.messageDynamicData);
                    break;
                case UNREVIEWTEST:
                    testId = (Integer) argValues[0];
                    diagn = smpAnaRes.sampleAnalysisResultUnReview(null, testId, null);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), testId);
                    this.messageDynamicData=new Object[]{testId};
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString()))
                        messages.addMainForSuccess(endPoint, this.messageDynamicData);
                    break;
                case UNCANCELTEST:
                    testId = (Integer) argValues[0];
                    diagn = smpAnaRes.sampleAnalysisResultUnCancel(null, testId, null);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), testId);
                    this.messageDynamicData=new Object[]{testId};
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString()))
                        messages.addMainForSuccess(endPoint, this.messageDynamicData);
                    break;
                case UNCANCELRESULT:
                    resultId = (Integer) argValues[0];
                    diagn = smpAnaRes.sampleAnalysisResultUnCancel(null, null, resultId);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), resultId);
                    this.messageDynamicData=new Object[]{resultId};
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString()))
                        messages.addMainForSuccess(endPoint, this.messageDynamicData);
                    break;
                case TESTASSIGNMENT:
                    testId = (Integer) argValues[0];
                    String newAnalyst = argValues[1].toString();
                    diagn = DataSampleAnalysis.sampleAnalysisAssignAnalyst(testId, newAnalyst, smp);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), testId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case GETSAMPLEINFO:
                    sampleId = (Integer) argValues[0];
                    String sampleFieldToRetrieve = argValues[1].toString();

                    String[] sampleFieldToRetrieveArr =sampleFieldToRetrieve.split("\\|");
                    schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());

                    String[] sortFieldsNameArr = null;
                    String sortFieldsName = argValues[2].toString();
                    if (! ((sortFieldsName==null) || (sortFieldsName.contains("undefined"))) ) {
                        sortFieldsNameArr = sortFieldsName.split("\\|");
                    }else{   sortFieldsNameArr=null;}

                    String diagnStr = Rdbms.getRecordFieldsByFilterJSON(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(),
                            new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, sampleFieldToRetrieveArr, sortFieldsNameArr);
                    if (diagnStr.contains(LPPlatform.LAB_FALSE)){
                        LPFrontEnd.responseError(diagnStr.split("\\|"), language, procInstanceName);
                    }else{
                        LPFrontEnd.servletReturnSuccess(request, null, diagnStr);
                    }
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    return;
                case COC_STARTCHANGE:
                    Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
                    objectId = (Integer) argValues[0];
                    String custodianCandidate = argValues[1].toString();
                    ChangeOfCustody coc = new ChangeOfCustody();
                    Integer appSessionId=null;
                    if (token.getAppSessionId()!=null){appSessionId=Integer.valueOf(token.getAppSessionId());}
                    diagn = coc.cocStartChange(TblsData.TablesData.SAMPLE.getTableName(), TblsData.Sample.SAMPLE_ID.getName(), objectId, custodianCandidate);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case COC_CONFIRMCHANGE:
                    sampleId = (Integer) argValues[0];
                    String confirmChangeComment = argValues[1].toString();
                    coc =  new ChangeOfCustody();
                    diagn = coc.cocConfirmedChange(TblsData.TablesData.SAMPLE.getTableName(), TblsData.Sample.SAMPLE_ID.getName(), sampleId, confirmChangeComment);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case COC_ABORTCHANGE:
                    sampleId = (Integer) argValues[0];
                    String cancelChangeComment = argValues[1].toString();
                    coc =  new ChangeOfCustody();
                    diagn = coc.cocAbortedChange(TblsData.TablesData.SAMPLE.getTableName(), TblsData.Sample.SAMPLE_ID.getName(), sampleId, cancelChangeComment);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case LOGALIQUOT:
                    sampleId = (Integer) argValues[0];
                    fieldName=argValues[1].toString();
                    fieldValue=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fieldName!=null) fieldNames = fieldName.split("\\|");
                    if (fieldValue!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                        diagn=fieldValues;
                        break;
                    }
                    diagn = smp.logSampleAliquot(sampleId,
                            // sampleTemplate, sampleTemplateVersion,
                            fieldNames, fieldValues);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case LOGSUBALIQUOT:
                    Integer aliquotId = (Integer) argValues[0];
                    fieldName=argValues[1].toString();
                    fieldValue=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fieldName!=null) fieldNames =  fieldName.split("\\|");
                    if (fieldValue!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                        diagn=fieldValues;
                        break;
                    }
                    diagn = smp.logSampleSubAliquot(aliquotId,
                            // sampleTemplate, sampleTemplateVersion,
                            fieldNames, fieldValues);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    this.messageDynamicData=new Object[]{sampleId};
                    break;
                case SAMPLESTAGE_MOVETOPREVIOUS:
                case SAMPLESTAGE_MOVETONEXT:
                    DataSampleStages smpStage=new DataSampleStages();
                    if (!smpStage.isSampleStagesEnable()){
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, null,
                                ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "STAGES_FUNCTIONALITY_NOT_ENABLE", new Object[]{"Samples", procInstanceName}));
                        return;
                    }
                    sampleId = (Integer) argValues[0];
                    String sampleStage = null;
                    if (argValues.length>1 && argValues[1]!=null) argValues[1].toString();
                    String sampleStageNext=null;
                    if (argValues.length>2 && argValues[2]!=null)sampleStageNext = argValues[2].toString();
                    if ((sampleStage==null) || (sampleStage.equalsIgnoreCase("undefined")) || (sampleStage.length()==0)){
                        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(),
                                new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                                new String[]{TblsData.Sample.CURRENT_STAGE.getName()});
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())){
                            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, null, LPArray.array2dTo1d(sampleInfo));
                            return;
                        }
                        sampleStage=sampleInfo[0][0].toString();
                    }
                    if (SampleAPIEndpoints.SAMPLESTAGE_MOVETONEXT.getName().equalsIgnoreCase(endPoint.getName()))
                        diagn=smpStage.moveToNextStage(sampleId, sampleStage, sampleStageNext);
                    if (SampleAPIEndpoints.SAMPLESTAGE_MOVETOPREVIOUS.getName().equalsIgnoreCase(endPoint.getName()))
                        diagn=smpStage.moveToPreviousStage(sampleId, sampleStage, sampleStageNext);
                    String[] sampleFieldName=new String[]{TblsData.Sample.CURRENT_STAGE.getName(), TblsData.Sample.PREVIOUS_STAGE.getName()};
                    Object[] sampleFieldValue=new Object[0];
                    String newSampleStage=diagn[diagn.length-1].toString();
                    if (diagn==null)
                        sampleFieldValue=new Object[]{"", sampleStage};                    
                    else                    
                        sampleFieldValue=new Object[]{newSampleStage, sampleStage};
                    if (diagn!=null && LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString())){
                        smpStage.dataSampleStagesTimingCapture(sampleId, sampleStage, DataSampleStages.SampleStageTimingCapturePhases.END.name());                                                         
                        smpStage.dataSampleStagesTimingCapture(sampleId, diagn[diagn.length-1].toString(), DataSampleStages.SampleStageTimingCapturePhases.START.toString());
                        diagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(),
                                sampleFieldName, 
                                sampleFieldValue,
                                new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId});
                        SampleAudit smpAudit = new SampleAudit();
                        smpAudit.sampleAuditAdd(endPoint.getAuditEventObj(), TblsData.TablesData.SAMPLE.getTableName(), sampleId, sampleId, null, null, sampleFieldName, sampleFieldValue);
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString()))
                            diagn=ApiMessageReturn.trapMessage(diagn[0].toString(), endPoint, new Object[]{sampleId});
                        if ("END".equalsIgnoreCase(newSampleStage))
                            smp.sampleReview(sampleId);                                        
                        this.messageDynamicData=new Object[]{sampleId};                    
                    }else
                        this.messageDynamicData=new Object[]{diagn[diagn.length-1].toString()};
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                    //this.messageDynamicData=new Object[]{};
                    break;
                case SAMPLEAUDIT_SET_AUDIT_ID_REVIEWED:
                    Integer auditId = (Integer) argValues[0];
                    Object[][] auditInfo=QueryUtilitiesEnums.getTableData(TblsDataAudit.TablesDataAudit.SAMPLE, 
                        EnumIntTableFields.getTableFieldsFromString(TblsDataAudit.TablesDataAudit.SAMPLE, new String[]{TblsDataAudit.Sample.SAMPLE_ID.getName()}),
                        new String[]{TblsDataAudit.Sample.AUDIT_ID.getName()}, new Object[]{auditId}, 
                        new String[]{TblsDataAudit.Sample.AUDIT_ID.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditInfo[0][0].toString())){
                        diagn=ApiMessageReturn.trapMessage(auditInfo[0][0].toString(), SampleAudit.SampleAuditErrorTrapping.AUDIT_RECORD_NOT_FOUND, new Object[]{auditId});
                        sampleId=null;
                    }else{
                        diagn=SampleAudit.sampleAuditSetAuditRecordAsReviewed(procInstanceName, auditId, ProcedureRequestSession.getInstanceForActions(null, null, null).getToken().getPersonName());
                        sampleId=Integer.valueOf(auditInfo[0][0].toString());
                    }
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsDataAudit.TablesDataAudit.SAMPLE.getTableName(), auditId);
                    this.messageDynamicData=new Object[]{auditId, sampleId};
                    break;
                default:
                    break;                
            }
            if (diagn!=null &&  LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString())){
                DataSampleStages smpStage = new DataSampleStages();
                if (smpStage.isSampleStagesEnable() && sampleId!=null)
                    smpStage.dataSampleActionAutoMoveToNext(endPoint.getName().toUpperCase(), sampleId);
            }
            if (diagn!=null &&  LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString()))
                diagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{argValues[0], procInstanceName});                    
            
            this.diagnostic=diagn;
            this.relatedObj=rObj;
            rObj.killInstance();
        }
        finally{
/*            smpAna = null;
            moduleSmpAnaRes = null;
            smp = null;
            smpAnaRes = null;    */
        }
        }
}
    

