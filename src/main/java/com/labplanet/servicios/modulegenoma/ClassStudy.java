/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulegenoma;

import functionaljavaa.modulegenoma.GenomaDataStudy;
import functionaljavaa.modulegenoma.GenomaDataStudyFamily;
import functionaljavaa.modulegenoma.GenomaDataStudyIndividualSamples;
import functionaljavaa.modulegenoma.GenomaDataStudyIndividuals;
import functionaljavaa.modulegenoma.GenomaDataStudySamplesSet;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ClassStudy {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;

    public ClassStudy(HttpServletRequest request, GenomaStudyAPI.GenomaStudyAPIEndPoints endPoint){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        RelatedObjects rObj=RelatedObjects.getInstanceForActions();

        GenomaDataStudy prjStudy = new GenomaDataStudy();
        GenomaDataStudyIndividuals prjStudyIndividual = new GenomaDataStudyIndividuals();
        GenomaDataStudyIndividualSamples prjStudyIndividualSmp = new GenomaDataStudyIndividualSamples();
        GenomaDataStudySamplesSet prjStudySampleSet = new GenomaDataStudySamplesSet();
        GenomaDataStudyFamily prjStudyFamily = new GenomaDataStudyFamily();
        String studyName = "";
        String projectName = "";
        
        Object[] actionDiagnoses = null;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());        
        this.functionFound=true;
            switch (endPoint){
                case STUDY_NEW:
                case STUDY_UPDATE:
                    projectName = argValues[0].toString();
                    studyName = argValues[1].toString();
                    String fieldName=argValues[2].toString();
                    String fieldValue=argValues[3].toString();
                    String[] fieldNames=new String[0];
                    Object[] fieldValues=new Object[0];
                    if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");    
                    
                    if (fieldValue!=null && fieldValue.length()>0) 
                        fieldValues=TblsGenomaData.Study.convertStringWithDataTypeToObjectArray(fieldNames, fieldValue.split("\\|"));
                    if ("STUDY_NEW".equalsIgnoreCase(endPoint.getName()))
                        actionDiagnoses= prjStudy.createStudy(studyName, projectName, fieldNames, fieldValues,  false);
                    if ("STUDY_UPDATE".equalsIgnoreCase(endPoint.getName()))
                        actionDiagnoses= prjStudy.studyUpdate(studyName, fieldNames, fieldValues);
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);                
                    if (actionDiagnoses!=null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{studyName, procInstanceName});                    
                    this.messageDynamicData=new Object[]{projectName, studyName, procInstanceName};
                    break;
                case STUDY_ACTIVATE:
                case STUDY_DEACTIVATE:
                    studyName = argValues[0].toString();
                    if ("STUDY_ACTIVATE".equalsIgnoreCase(endPoint.getName()))
                        actionDiagnoses =prjStudy.studyActivate(studyName);
                    else if ("STUDY_DEACTIVATE".equalsIgnoreCase(endPoint.getName()))
                        actionDiagnoses =prjStudy.studyDeActivate(studyName);                    
                    this.messageDynamicData=new Object[]{studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);
                    break;
                case STUDY_ADD_USER:
                case STUDY_REMOVE_USER:
                case STUDY_CHANGE_USER_ROLE:
                case STUDY_USER_ACTIVATE:
                case STUDY_USER_DEACTIVATE:
                    studyName = argValues[0].toString();
                    String userName=argValues[1].toString();
                    String userRole=argValues[2].toString();
                    actionDiagnoses =prjStudy.studyUserManagement(endPoint.getName(), studyName, userName, userRole);
                    this.messageDynamicData=new Object[]{userName, studyName, userRole, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);                                    
                    break;
                case STUDY_CREATE_INDIVIDUAL:
                    studyName = argValues[0].toString();
                    String indvidualName=argValues[1].toString();
                    fieldName=argValues[2].toString();
                    fieldValue=argValues[3].toString();
                    fieldNames=new String[0];
                    fieldValues=new Object[0];
                    if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                    if (fieldValue!=null && fieldValue.length()>0) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));                                                                                
                    
                    actionDiagnoses =prjStudyIndividual.createStudyIndividual(studyName, indvidualName, fieldNames, fieldValues, false);
                    this.messageDynamicData=new Object[]{indvidualName, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);                                    
                    break;
                case STUDY_INDIVIDUAL_ACTIVATE:
                    studyName = argValues[0].toString();
                    String indvidualId=argValues[1].toString();
                    actionDiagnoses =prjStudyIndividual.studyIndividualActivate(studyName, Integer.valueOf(indvidualId));
                    this.messageDynamicData=new Object[]{indvidualId, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);                                    
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudyIndividual.TBL.getName(), TblsGenomaData.StudyIndividual.TBL.getName(), indvidualId);                                    
                    break;
                case STUDY_INDIVIDUAL_DEACTIVATE:
                    studyName = argValues[0].toString();
                    indvidualId=argValues[1].toString();
                    actionDiagnoses =prjStudyIndividual.studyIndividualDeActivate(studyName, Integer.valueOf(indvidualId));
                    this.messageDynamicData=new Object[]{indvidualId, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);                                    
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudyIndividual.TBL.getName(), TblsGenomaData.StudyIndividual.TBL.getName(), indvidualId);                                    
                    break;
                case STUDY_CREATE_INDIVIDUAL_SAMPLE:
                    studyName = argValues[0].toString();
                    indvidualId=argValues[1].toString();
                    actionDiagnoses =prjStudyIndividualSmp.createStudyIndividualSample(studyName, Integer.valueOf(indvidualId), new String[0], new Object[0], false);
                    this.messageDynamicData=new Object[]{indvidualId, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);                                    
                    break;
                case STUDY_INDIVIDUAL_SAMPLE_ACTIVATE:
                    studyName = argValues[0].toString();
                    indvidualId=argValues[1].toString();
                    String sampleIdStr=argValues[2].toString();
                    actionDiagnoses =prjStudyIndividualSmp.studyIndividualSampleActivate(studyName, Integer.valueOf(indvidualId), Integer.valueOf(sampleIdStr));
                    this.messageDynamicData=new Object[]{sampleIdStr, indvidualId, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudyIndividual.TBL.getName(), TblsGenomaData.StudyIndividual.TBL.getName(), indvidualId);
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudyIndividualSample.TBL.getName(), TblsGenomaData.StudyIndividualSample.TBL.getName(), sampleIdStr);
                    break;
                case STUDY_INDIVIDUAL_SAMPLE_DEACTIVATE:
                    studyName = argValues[0].toString();
                    indvidualId=argValues[1].toString();
                    sampleIdStr=argValues[2].toString();
                    actionDiagnoses =prjStudyIndividualSmp.studyIndividualSampleDeActivate(studyName, Integer.valueOf(indvidualId), Integer.valueOf(sampleIdStr));
                    this.messageDynamicData=new Object[]{sampleIdStr, indvidualId, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudyIndividual.TBL.getName(), TblsGenomaData.StudyIndividual.TBL.getName(), indvidualId);
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudyIndividualSample.TBL.getName(), TblsGenomaData.StudyIndividualSample.TBL.getName(), sampleIdStr);
                    break;
                case STUDY_CREATE_FAMILY:
                    studyName = argValues[0].toString();
                    String familyName=argValues[1].toString();
                    fieldName=argValues[2].toString();
                    fieldValue=argValues[3].toString();                    
                    String individualsListStr=argValues[4].toString();
                    String[] individualsList=new String[0];
                    fieldNames=new String[0];
                    fieldValues=new Object[0];
                    if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                    if (fieldValue!=null && fieldValue.length()>0) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));                                                                                                    
                    if (individualsListStr!=null && individualsListStr.length()>0) individualsList = individualsListStr.split("\\|");                    
                    actionDiagnoses =prjStudyFamily.createStudyFamily(studyName, familyName, individualsList, fieldNames, fieldValues, false);
                    this.messageDynamicData=new Object[]{familyName, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);                                    
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudyFamily.TBL.getName(), TblsGenomaData.StudyFamily.TBL.getName(), familyName);                                    
                    break;
                case STUDY_FAMILY_ACTIVATE:
                    studyName = argValues[0].toString();
                    familyName=argValues[1].toString();
                    actionDiagnoses =prjStudyFamily.studyFamilyActivate(studyName, familyName);
                    this.messageDynamicData=new Object[]{familyName, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudyFamily.TBL.getName(), TblsGenomaData.StudyFamily.TBL.getName(), familyName);                                    
                    break;
                case STUDY_FAMILY_DEACTIVATE:
                    studyName = argValues[0].toString();
                    familyName=argValues[1].toString();
                    actionDiagnoses =prjStudyFamily.studyFamilyDeActivate(studyName, familyName);
                    this.messageDynamicData=new Object[]{familyName, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudyFamily.TBL.getName(), TblsGenomaData.StudyFamily.TBL.getName(), familyName);                                    
                    break;
                case STUDY_FAMILY_ADD_INDIVIDUAL:
                    studyName = argValues[0].toString();
                    familyName=argValues[1].toString();
                    String individualIdStr = argValues[2].toString();
                    actionDiagnoses =prjStudyFamily.studyFamilyAddIndividual(studyName, familyName, individualIdStr);
                    this.messageDynamicData=new Object[]{familyName, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudyFamily.TBL.getName(), TblsGenomaData.StudyFamily.TBL.getName(), familyName);                                    
                    break;
                case STUDY_FAMILY_REMOVE_INDIVIDUAL:
                    studyName = argValues[0].toString();
                    familyName=argValues[1].toString();
                    individualIdStr = argValues[2].toString();
                    actionDiagnoses =prjStudyFamily.studyFamilyRemoveIndividual(studyName, familyName, individualIdStr);
                    this.messageDynamicData=new Object[]{familyName, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudyFamily.TBL.getName(), TblsGenomaData.StudyFamily.TBL.getName(), familyName);                                    
                    break;
                case STUDY_CREATE_SAMPLES_SET:
                    studyName = argValues[0].toString();
                    String samplesSetName=argValues[1].toString();
                    String samplesStr=argValues[2].toString();
                    fieldName=argValues[3].toString();
                    fieldValue=argValues[4].toString();
                    fieldNames=new String[0];
                    fieldValues=new Object[0];
                    String[] samples=new String[0];
                    if (samplesStr!=null && samplesStr.length()>0) samples = samplesStr.split("\\|");
                    if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                    if (fieldValue!=null && fieldValue.length()>0) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));                                                                                                    
                    actionDiagnoses =prjStudySampleSet.createStudySamplesSet(studyName, samplesSetName, samples, fieldNames, fieldValues, false);
                    this.messageDynamicData=new Object[]{samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);                                    
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudySamplesSet.TBL.getName(), TblsGenomaData.StudySamplesSet.TBL.getName(), samplesSetName);                                    
                    break;
                case STUDY_SAMPLES_SET_ACTIVATE:
                    studyName = argValues[0].toString();
                    samplesSetName=argValues[1].toString();
                    actionDiagnoses =prjStudySampleSet.studySamplesSetActivate(studyName, samplesSetName);
                    this.messageDynamicData=new Object[]{samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudySamplesSet.TBL.getName(), TblsGenomaData.StudySamplesSet.TBL.getName(), samplesSetName);                                    
                    break;
                case STUDY_SAMPLES_SET_DEACTIVATE:
                    studyName = argValues[0].toString();
                    samplesSetName=argValues[1].toString();
                    actionDiagnoses =prjStudySampleSet.studySamplesSetDeActivate(studyName, samplesSetName);
                    this.messageDynamicData=new Object[]{samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudySamplesSet.TBL.getName(), TblsGenomaData.StudySamplesSet.TBL.getName(), samplesSetName);                                    
                    break;
                case STUDY_SAMPLES_SET_ADD_SAMPLE:
                    studyName = argValues[0].toString();
                    samplesSetName=argValues[1].toString();
                    sampleIdStr = argValues[2].toString();
                    actionDiagnoses =prjStudySampleSet.studySamplesSetAddSample(studyName, samplesSetName, sampleIdStr);
                    this.messageDynamicData=new Object[]{sampleIdStr, samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudySamplesSet.TBL.getName(), TblsGenomaData.StudySamplesSet.TBL.getName(), samplesSetName);                                    
                    break;
                case STUDY_SAMPLES_SET_REMOVE_SAMPLE:
                    studyName = argValues[0].toString();
                    samplesSetName=argValues[1].toString();
                    sampleIdStr = argValues[2].toString();
                    actionDiagnoses =prjStudySampleSet.studySamplesSetRemoveSample(studyName, samplesSetName, sampleIdStr);
                    this.messageDynamicData=new Object[]{sampleIdStr, samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.Study.TBL.getName(), TblsGenomaData.Study.TBL.getName(), studyName);
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsGenomaData.StudySamplesSet.TBL.getName(), TblsGenomaData.StudySamplesSet.TBL.getName(), samplesSetName);                                    
                    break;
                default:
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
