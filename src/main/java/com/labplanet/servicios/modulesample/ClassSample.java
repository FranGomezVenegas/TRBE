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
import databases.Token;
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
import functionaljavaa.samplestructure.DataSampleStructureStatuses;
import java.math.BigDecimal;
import java.sql.Date;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
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
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        String schemaDataName="";
        String language="";
        DataModuleSampleAnalysis smpAna = new DataModuleSampleAnalysis();
        DataModuleSampleAnalysisResult moduleSmpAnaRes = new DataModuleSampleAnalysisResult();
        DataSample smp = new DataSample(smpAna);
        DataSampleAnalysisResult smpAnaRes = new DataSampleAnalysisResult(moduleSmpAnaRes);
        
        Integer incubationStage=null;
        Integer sampleId = null;
        Object[] diagn = null;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());  
        for (LPAPIArguments currArg: endPoint.getArguments()){
            if (GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID.equalsIgnoreCase(currArg.getName())){
                sampleId = Integer.valueOf(request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID.toString()));
            }
        }
        if (sampleId!=null || LPArray.valueInArray(exceptionsToSampleReviewArr, endPoint.getName())){
            Object[][] sampleStatus=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), 
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, new String[]{TblsData.Sample.FLD_STATUS.getName()});
            diagn=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_ALREADY_REVIEWED.getErrorCode(), null);
            if ( (sampleStatus[0][0].toString().equalsIgnoreCase(DataSampleStructureStatuses.SampleStatuses.CANCELED.getStatusCode(""))) ||
                 (sampleStatus[0][0].toString().equalsIgnoreCase(DataSampleStructureStatuses.SampleStatuses.REVIEWED.getStatusCode(""))) ){               
                this.diagnostic=diagn;
//                Object[] dynamicDataObjects = new Object[]{sampleId};
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), diagn[diagn.length-1]);
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
                
//                String numSmplsToLogStr = argValues[7].toString();
//                Integer numSamplesToLog=(Integer) argValues[7];
                
                if (argValues[6]==null){
                    diagn = smp.logSample(sampleTemplate, sampleTemplateVersion, fieldNames, fieldValues);
                }else{
                    Integer numSamplesToLog=(Integer) argValues[6];
                    diagn = smp.logSample(sampleTemplate, sampleTemplateVersion, fieldNames, fieldValues, numSamplesToLog);
                }
                Object[] dynamicDataObjects = new Object[]{diagn[diagn.length-1]};
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), diagn[diagn.length-1]);
                this.messageDynamicData=new Object[]{diagn[diagn.length-1]};
                break;
            case RECEIVESAMPLE:
                sampleId = (Integer) argValues[0];
                diagn = smp.sampleReception(sampleId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case SETSAMPLINGDATE:
                sampleId = (Integer) argValues[0];
                diagn = smp.setSamplingDate(sampleId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{LPDate.getCurrentTimeStamp(), sampleId};
                break;
            case CHANGESAMPLINGDATE:
                sampleId = (Integer) argValues[0];
                Date newDate=(Date) argValues[1];
                diagn = smp.changeSamplingDate(sampleId, newDate);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case SAMPLINGCOMMENTADD:
                sampleId = (Integer) argValues[0];
                String comment=null;
                comment = argValues[1].toString();
                diagn = smp.sampleReceptionCommentAdd(sampleId, comment);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case SAMPLINGCOMMENTREMOVE:
                sampleId = (Integer) argValues[0];
                diagn = smp.sampleReceptionCommentRemove(sampleId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case INCUBATIONSTART:
                incubationStage=1;
                sampleId = (Integer) argValues[0];
                String incubName=argValues[1].toString();
                BigDecimal tempReading=null;
                diagn = DataSampleIncubation.setSampleStartIncubationDateTime(sampleId, incubationStage, incubName, tempReading);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case INCUBATION2START:
                incubationStage=2;
                sampleId = (Integer) argValues[0];
                incubName=argValues[1].toString();
                tempReading=null;
                diagn = DataSampleIncubation.setSampleStartIncubationDateTime(sampleId, incubationStage, incubName, tempReading);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case INCUBATIONEND:
                incubationStage=1;
                sampleId = (Integer) argValues[0];
                incubName= argValues[1].toString();
                tempReading=null;
                diagn = DataSampleIncubation.setSampleEndIncubationDateTime(sampleId, incubationStage, incubName, tempReading);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case INCUBATION2END:
                incubationStage=2;
                sampleId = (Integer) argValues[0];
                incubName= argValues[1].toString();
                tempReading=null;
                diagn = DataSampleIncubation.setSampleEndIncubationDateTime(sampleId, incubationStage, incubName, tempReading);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
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
                diagn = DataSampleAnalysis.sampleAnalysisAddtoSample(sampleId, fieldNameArr, fieldValueArr, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case ENTERRESULT:
                Integer resultId = (Integer) argValues[0];
                String rawValueResult = argValues[1].toString();
                diagn = smpAnaRes.sampleAnalysisResultEntry(resultId, rawValueResult, smp);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysisResult.TBL.getName(), TblsData.SampleAnalysisResult.TBL.getName(), resultId);
                this.messageDynamicData=new Object[]{resultId};
                break;
            case ENTERRESULT_BY_ANALYSIS_NAME:
                sampleId = (Integer) argValues[0];
                String analysisName = argValues[1].toString();
                rawValueResult = argValues[2].toString();
                diagn = smpAnaRes.sampleAnalysisResultEntryByAnalysisName(sampleId, analysisName, rawValueResult, smp);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId, analysisName};
                break;
            case REVIEWSAMPLE:
                sampleId = (Integer) argValues[0];
                diagn = smpAnaRes.sampleAnalysisResultReview(sampleId, null, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case REVIEWSAMPLE_TESTINGGROUP:
                sampleId = (Integer) argValues[0];
                String testingGroup = argValues[1].toString();
                diagn = reviewSampleTestingGroup(sampleId, testingGroup);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId, testingGroup};
                break;
            case REVIEWTEST:
                Integer testId = (Integer) argValues[0];
                diagn = smpAnaRes.sampleAnalysisResultReview(null, testId, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{testId};
                break;
            case REVIEWTEST_BY_SAMPLE_ID_AND_ANALYSIS_NAME:
                sampleId = (Integer) argValues[0];
                analysisName = argValues[1].toString();
                diagn = smpAnaRes.sampleAnalysisResultReviewBySampleAndAnalysis(sampleId, analysisName);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
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
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case CANCELSAMPLE:
                sampleId = (Integer) argValues[0];
                diagn = smpAnaRes.sampleAnalysisResultCancel(sampleId, null, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case CANCELTEST:
                testId = (Integer) argValues[0];
                diagn = smpAnaRes.sampleAnalysisResultCancel(null, testId, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysis.TBL.getName(), TblsData.SampleAnalysis.TBL.getName(), testId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case CANCELRESULT:
                resultId = (Integer) argValues[0];
                diagn = smpAnaRes.sampleAnalysisResultCancel(null, null, resultId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysisResult.TBL.getName(), TblsData.SampleAnalysisResult.TBL.getName(), resultId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case UNREVIEWRESULT:   // No break then will take the same logic than the next one
            case UNCANCELSAMPLE:
                sampleId = (Integer) argValues[0];
                diagn = smpAnaRes.sampleAnalysisResultUnCancel(sampleId, null, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case UNREVIEWTEST:
                testId = (Integer) argValues[0];
                diagn = smpAnaRes.sampleAnalysisResultUnReview(null, testId, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysis.TBL.getName(), TblsData.SampleAnalysis.TBL.getName(), testId);
                this.messageDynamicData=new Object[]{testId};
                break;
            case UNCANCELTEST:
                testId = (Integer) argValues[0];
                diagn = smpAnaRes.sampleAnalysisResultUnCancel(null, testId, null);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysis.TBL.getName(), TblsData.SampleAnalysis.TBL.getName(), testId);
                this.messageDynamicData=new Object[]{testId};
                break;
            case UNCANCELRESULT:
                resultId = (Integer) argValues[0];
                diagn = smpAnaRes.sampleAnalysisResultUnCancel(null, null, resultId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysisResult.TBL.getName(), TblsData.SampleAnalysisResult.TBL.getName(), resultId);
                this.messageDynamicData=new Object[]{resultId};
                break;
            case TESTASSIGNMENT:
                testId = (Integer) argValues[0];
                String newAnalyst = argValues[1].toString();
                diagn = DataSampleAnalysis.sampleAnalysisAssignAnalyst(testId, newAnalyst, smp);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysis.TBL.getName(), TblsData.SampleAnalysis.TBL.getName(), testId);
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
                
                String diagnStr = Rdbms.getRecordFieldsByFilterJSON(schemaDataName, TblsData.Sample.TBL.getName(),
                        new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, sampleFieldToRetrieveArr, sortFieldsNameArr);
                if (diagnStr.contains(LPPlatform.LAB_FALSE)){
                    LPFrontEnd.responseError(diagnStr.split("\\|"), language, procInstanceName);
                }else{
                    LPFrontEnd.servletReturnSuccess(request, null, diagnStr);
                }
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                return;
            case COC_STARTCHANGE:
                Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
                objectId = (Integer) argValues[0];
                String custodianCandidate = argValues[1].toString();
                ChangeOfCustody coc = new ChangeOfCustody();
                Integer appSessionId=null;
                if (token.getAppSessionId()!=null){appSessionId=Integer.valueOf(token.getAppSessionId());}
                diagn = coc.cocStartChange(TblsData.Sample.TBL.getName(), TblsData.Sample.FLD_SAMPLE_ID.getName(), objectId, custodianCandidate);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case COC_CONFIRMCHANGE:
                sampleId = (Integer) argValues[0];
                String confirmChangeComment = argValues[1].toString();
                coc =  new ChangeOfCustody();
                diagn = coc.cocConfirmedChange(TblsData.Sample.TBL.getName(), TblsData.Sample.FLD_SAMPLE_ID.getName(), sampleId, confirmChangeComment);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case COC_ABORTCHANGE:
                sampleId = (Integer) argValues[0];
                String cancelChangeComment = argValues[1].toString();
                coc =  new ChangeOfCustody();
                diagn = coc.cocAbortedChange(TblsData.Sample.TBL.getName(), TblsData.Sample.FLD_SAMPLE_ID.getName(), sampleId, cancelChangeComment);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
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
                diagn = smp.logSampleAliquot(sampleId,
                        // sampleTemplate, sampleTemplateVersion,
                        fieldNames, fieldValues);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
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
                diagn = smp.logSampleSubAliquot(aliquotId,
                        // sampleTemplate, sampleTemplateVersion,
                        fieldNames, fieldValues);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                this.messageDynamicData=new Object[]{sampleId};
                break;
            case SAMPLESTAGE_MOVETOPREVIOUS:
            case SAMPLESTAGE_MOVETONEXT:
                DataSampleStages smpStage=new DataSampleStages();
                if (!smpStage.isSampleStagesEnable()){
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, null,
                            LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "STAGES_FUNCTIONALITY_NOT_ENABLE", new Object[]{"Samples", procInstanceName}));
                    return;
                }
                sampleId = (Integer) argValues[0];
                String sampleStage = null;
                if (argValues.length>1 && argValues[1]!=null) argValues[1].toString();
                String sampleStageNext=null;
                if (argValues.length>2 && argValues[2]!=null)sampleStageNext = argValues[2].toString();
                if ((sampleStage==null) || (sampleStage.equalsIgnoreCase("undefined")) || (sampleStage.length()==0)){
                    Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(),
                            new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                            new String[]{TblsData.Sample.FLD_CURRENT_STAGE.getName()});
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
                String[] sampleFieldName=new String[]{TblsData.Sample.FLD_CURRENT_STAGE.getName(), TblsData.Sample.FLD_PREVIOUS_STAGE.getName()};
                Object[] sampleFieldValue=new Object[0];
                if (diagn==null)
                    sampleFieldValue=new Object[]{"", sampleStage};                    
                else                    
                    sampleFieldValue=new Object[]{diagn[diagn.length-1], sampleStage};
                if (diagn!=null && LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString())){
                    smpStage.dataSampleStagesTimingCapture(sampleId, sampleStage, DataSampleStages.SampleStageTimingCapturePhases.END.name());                                                         
                    smpStage.dataSampleStagesTimingCapture(sampleId, diagn[diagn.length-1].toString(), DataSampleStages.SampleStageTimingCapturePhases.START.toString());
                    diagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(),
                            sampleFieldName, 
                            sampleFieldValue,
                            new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
                    String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR);
                    SampleAudit smpAudit = new SampleAudit();
                    smpAudit.sampleAuditAdd(endPoint.getName(), TblsData.Sample.TBL.getName(), sampleId, sampleId, null, null, fieldsForAudit, null);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString()))
                        diagn=LPPlatform.trapMessage(diagn[0].toString(), endPoint.getSuccessMessageCode(), new Object[]{sampleId});
                    this.messageDynamicData=new Object[]{sampleId};                    
                }else
                    this.messageDynamicData=new Object[]{diagn[diagn.length-1].toString()};
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), TblsData.Sample.TBL.getName(), sampleId);
                //this.messageDynamicData=new Object[]{};
                break;
            case SAMPLEAUDIT_SET_AUDIT_ID_REVIEWED:
                Integer auditId = (Integer) argValues[0];
                Object[][] auditInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), TblsDataAudit.Sample.TBL.getName(), 
                    new String[]{TblsDataAudit.Sample.FLD_AUDIT_ID.getName()}, new Object[]{auditId}, 
                    new String[]{TblsDataAudit.Sample.FLD_SAMPLE_ID.getName()}, new String[]{TblsDataAudit.Sample.FLD_AUDIT_ID.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditInfo[0][0].toString())){
                    diagn=LPPlatform.trapMessage(auditInfo[0][0].toString(), SampleAudit.SampleAuditErrorTrapping.AUDIT_RECORD_NOT_FOUND.getErrorCode(), new Object[]{auditId});
                    sampleId=null;
                }else{
                    diagn=SampleAudit.sampleAuditSetAuditRecordAsReviewed(procInstanceName, auditId, ProcedureRequestSession.getInstanceForActions(null, null, null).getToken().getPersonName());
                    sampleId=Integer.valueOf(auditInfo[0][0].toString());
                }
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsDataAudit.Sample.TBL.getName(), TblsDataAudit.Sample.TBL.getName(), auditId);
                this.messageDynamicData=new Object[]{auditId, sampleId};
                break;
            default:
                break;                
        }
        if (diagn!=null &&  LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString())){
            DataSampleStages smpStage = new DataSampleStages();
            if (sampleId!=null)
                smpStage.dataSampleActionAutoMoveToNext(endPoint.getName().toUpperCase(), sampleId);
        }
        this.diagnostic=diagn;
        this.relatedObj=rObj;
        rObj.killInstance();
    }
    
}
