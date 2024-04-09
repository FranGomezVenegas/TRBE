/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.clinicalstudies.logic;

import module.clinicalstudies.apis.GenomaProjectAPI;
import module.clinicalstudies.apis.GenomaStudyAPI;
import module.clinicalstudies.definition.TblsGenomaConfig;
import module.clinicalstudies.definition.TblsGenomaData;
import functionaljavaa.modulegenoma.ClinicalStudyDataStudyObjectsVariableValues;
import functionaljavaa.modulegenoma.ClinicalStudyDataStudy;
import functionaljavaa.modulegenoma.ClinicalStudyDataStudyCohort;
import functionaljavaa.modulegenoma.ClinicalStudyDataStudyFamily;
import functionaljavaa.modulegenoma.ClinicalStudyDataStudyIndividualConsent;
import functionaljavaa.modulegenoma.ClinicalStudyDataStudyIndividualSamples;
import functionaljavaa.modulegenoma.ClinicalStudyDataStudyIndividuals;
import functionaljavaa.modulegenoma.ClinicalStudyDataStudySamplesSet;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPMath;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import static module.clinicalstudies.apis.GenomaStudyAPI.GenomaStudyAPIactionsEndPoints.ENTER_STUDY_OBJECT_VARIABLE_VALUE;
import trazit.enums.ActionsClass;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassStudy implements ActionsClass{
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    InternalMessage actionDiagnosesObj;
    private Boolean functionFound=false;
    private EnumIntEndpoints enumConstantByName;
    
static final String NAME_SUFFIX="_name";
    public ClassStudy(HttpServletRequest request, GenomaStudyAPI.GenomaStudyAPIactionsEndPoints endPoint){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=procReqSession.getProcedureInstance();

        RelatedObjects rObj=RelatedObjects.getInstanceForActions();

        ClinicalStudyDataStudy prjStudy = new ClinicalStudyDataStudy();
        ClinicalStudyDataStudyIndividuals prjStudyIndividual = new ClinicalStudyDataStudyIndividuals();
        ClinicalStudyDataStudyIndividualSamples prjStudyIndividualSmp = new ClinicalStudyDataStudyIndividualSamples();
        ClinicalStudyDataStudySamplesSet prjStudySampleSet = new ClinicalStudyDataStudySamplesSet();
        ClinicalStudyDataStudyFamily prjStudyFamily = new ClinicalStudyDataStudyFamily();
        ClinicalStudyDataStudyCohort prjStudyCohort = new ClinicalStudyDataStudyCohort();
        this.enumConstantByName=endPoint;
        String studyName = "";
        String projectName = "";
        try{
        Object[] actionDiagnoses = null;
        this.actionDiagnosesObj = null;
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            this.functionFound=true;
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
                //procReqSession.killIt();
                this.diagnostic=(Object[]) argValues[1];
                this.messageDynamicData=new Object[]{argValues[2].toString()};
                return;                        
            }            
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
                        //fieldValues=TblsGenomaData.Study.convertStringWithDataTypeToObjectArray(fieldNames, fieldValue.split("\\|"));
                        fieldValues=LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    if ("STUDY_NEW".equalsIgnoreCase(endPoint.getName())){
                        actionDiagnosesObj= prjStudy.createStudy(endPoint, studyName, projectName, fieldNames, fieldValues,  false);
                        actionDiagnoses=ApiMessageReturn.trapMessage(actionDiagnosesObj.getDiagnostic(), actionDiagnosesObj.getMessageCodeObj(), actionDiagnosesObj.getMessageCodeVariables());
                    }
                    if ("STUDY_UPDATE".equalsIgnoreCase(endPoint.getName()))
                        actionDiagnosesObj= prjStudy.studyUpdate(endPoint, studyName, fieldNames, fieldValues);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);                
                    if (actionDiagnoses!=null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{studyName, procInstanceName});                    
                    this.messageDynamicData=new Object[]{projectName, studyName, procInstanceName};
                    break;
                case STUDY_ACTIVATE:
                case STUDY_DEACTIVATE:
                    studyName = argValues[0].toString();
                    if ("STUDY_ACTIVATE".equalsIgnoreCase(endPoint.getName()))
                        actionDiagnosesObj =prjStudy.studyActivate(endPoint, studyName);
                    else if ("STUDY_DEACTIVATE".equalsIgnoreCase(endPoint.getName()))
                        actionDiagnosesObj =prjStudy.studyDeActivate(endPoint, studyName);                    
                    this.messageDynamicData=new Object[]{studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    break;
                case STUDY_ADD_USER:
                case STUDY_REMOVE_USER:
                case STUDY_CHANGE_USER_ROLE:
                case STUDY_USER_ACTIVATE:
                case STUDY_USER_DEACTIVATE:
                    studyName = argValues[0].toString();
                    String userName=argValues[1].toString();
                    String userRole=argValues[2].toString();
                    actionDiagnosesObj =prjStudy.studyUserManagement(endPoint, studyName, userName, userRole);
                    this.messageDynamicData=new Object[]{userName, studyName, userRole, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_USERS.getTableName(), userName);
                    break;
                case STUDY_CREATE_INDIVIDUAL:
                    studyName = argValues[0].toString();
                    String indvidualName=argValues[1].toString();
                    fieldName=argValues[2].toString();
                    fieldValue=argValues[3].toString();
                    fieldNames=new String[0];
                    fieldValues=new Object[0];
                    if (fieldName!=null && fieldName.length()>0){
                        fieldNames = fieldName.split("\\|");
                        if (fieldValue!=null && fieldValue.length()>0){
                            fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));                                                                                                
                            if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                                actionDiagnoses=fieldValues;
                                break;
                            }
                        }
                    }
                    actionDiagnosesObj =prjStudyIndividual.createStudyIndividual(endPoint, studyName, indvidualName, fieldNames, fieldValues, false);
                    this.messageDynamicData=new Object[]{indvidualName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), actionDiagnoses[actionDiagnoses.length-1]);
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName()+NAME_SUFFIX, indvidualName);
                    }
                    break;
                case STUDY_INDIVIDUAL_ACTIVATE:
                    studyName = argValues[0].toString();
                    String indivId=argValues[1].toString();
                    actionDiagnosesObj =prjStudyIndividual.studyIndividualActivate(endPoint, studyName, Integer.valueOf(indivId));
                    this.messageDynamicData=new Object[]{indivId, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);                                    
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), indivId);                                    
                    break;
                case STUDY_INDIVIDUAL_DEACTIVATE:
                    studyName = argValues[0].toString();
                    indivId=argValues[1].toString();
                    Object[] isNumeric=LPMath.isNumeric(indivId);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric[0].toString())) 
                        actionDiagnoses=isNumeric;
                    else
                        actionDiagnosesObj =prjStudyIndividual.studyIndividualDeActivate(endPoint, studyName, Integer.valueOf(indivId));
                    this.messageDynamicData=new Object[]{indivId, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);                                    
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), indivId);                                    
                    break;
                case STUDY_CREATE_INDIVIDUAL_SAMPLE:
                    studyName = argValues[0].toString();
                    indivId=argValues[1].toString();
                    Integer indivIdInt=null;
                    if (indivId.length()>0)
                        indivIdInt=Integer.valueOf(indivId);
                    actionDiagnosesObj = prjStudyIndividualSmp.createStudyIndividualSample(endPoint, studyName, indivIdInt, new String[0], new Object[0], false);
                    this.messageDynamicData=new Object[]{actionDiagnosesObj.getNewObjectId(), indivId, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);                                    
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), indivId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), actionDiagnosesObj.getNewObjectId());
                    }
                    actionDiagnoses=ApiMessageReturn.trapMessage(actionDiagnosesObj.getDiagnostic(), actionDiagnosesObj.getMessageCodeObj(), actionDiagnosesObj.getMessageCodeVariables());
                    break;

                case STUDY_INDIVIDUAL_SAMPLE_ACTIVATE:
                    studyName = argValues[0].toString();
                    indivId=argValues[1].toString();
                    String sampleIdStr=argValues[2].toString();
                    indivIdInt=null;
                    if (indivId.length()>0)
                        indivIdInt=Integer.valueOf(indivId);                    
                    actionDiagnosesObj =prjStudyIndividualSmp.studyIndividualSampleActivate(endPoint, studyName, indivIdInt, Integer.valueOf(sampleIdStr));
                    this.messageDynamicData=new Object[]{sampleIdStr, indivId, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), indivId);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), sampleIdStr);
                    break;
                case STUDY_INDIVIDUAL_SAMPLE_DEACTIVATE:
                    studyName = argValues[0].toString();
                    indivId=argValues[1].toString();
                    sampleIdStr=argValues[2].toString();
                    actionDiagnosesObj =prjStudyIndividualSmp.studyIndividualSampleDeActivate(endPoint, studyName, Integer.valueOf(indivId), Integer.valueOf(sampleIdStr));
                    this.messageDynamicData=new Object[]{sampleIdStr, indivId, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), indivId);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), sampleIdStr);
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
                    if (fieldValues!=null&&fieldValues.length>0&& LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                        actionDiagnoses=fieldValues;
                        break;
                    }
                    actionDiagnosesObj =prjStudyFamily.createStudyFamily(endPoint, studyName, familyName, individualsList, fieldNames, fieldValues, false);
                    actionDiagnoses=ApiMessageReturn.trapMessage(actionDiagnosesObj.getDiagnostic(), actionDiagnosesObj.getMessageCodeObj(), actionDiagnosesObj.getMessageCodeVariables());
                    this.messageDynamicData=new Object[]{familyName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);                                    
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), actionDiagnosesObj.getNewObjectId());
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName()+NAME_SUFFIX, familyName);
                    }
                    break;
                case STUDY_FAMILY_ACTIVATE:
                    studyName = argValues[0].toString();
                    familyName=argValues[1].toString();
                    actionDiagnosesObj =prjStudyFamily.studyFamilyActivate(endPoint, studyName, familyName);
                    this.messageDynamicData=new Object[]{familyName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), familyName);                                    
                    break;
                case STUDY_FAMILY_DEACTIVATE:
                    studyName = argValues[0].toString();
                    familyName=argValues[1].toString();
                    actionDiagnosesObj =prjStudyFamily.studyFamilyDeActivate(endPoint, studyName, familyName);
                    this.messageDynamicData=new Object[]{familyName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), familyName);                                    
                    break;
                case STUDY_FAMILY_ADD_INDIVIDUAL:
                    studyName = argValues[0].toString();
                    familyName=argValues[1].toString();
                    String individualIdStr = argValues[2].toString();
                    actionDiagnosesObj =prjStudyFamily.studyFamilyAddIndividual(endPoint, studyName, familyName, individualIdStr);
                    this.messageDynamicData=new Object[]{individualIdStr, familyName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), familyName);                                    
                    break;
                case STUDY_FAMILY_REMOVE_INDIVIDUAL:
                    studyName = argValues[0].toString();
                    familyName=argValues[1].toString();
                    individualIdStr = argValues[2].toString();
                    actionDiagnosesObj =prjStudyFamily.studyFamilyRemoveIndividual(endPoint, studyName, familyName, individualIdStr);
                    this.messageDynamicData=new Object[]{individualIdStr, familyName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), familyName);                                    
                    break;
                case STUDY_CREATE_COHORT:
                    studyName = argValues[0].toString();
                    String cohortName=argValues[1].toString();
                    fieldName=argValues[2].toString();
                    fieldValue=argValues[3].toString();                    
                    individualsListStr=argValues[4].toString();
                    individualsList=new String[0];
                    fieldNames=new String[0];
                    fieldValues=new Object[0];
                    if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                    if (fieldValue!=null && fieldValue.length()>0) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));                                                                                                    
                    if (individualsListStr!=null && individualsListStr.length()>0) individualsList = individualsListStr.split("\\|");                    
                    if (fieldValues!=null&&fieldValues.length>0&& LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                        actionDiagnoses=fieldValues;
                        break;
                    }
                    actionDiagnosesObj =prjStudyCohort.createStudyCohort(endPoint, studyName, cohortName, individualsList, fieldNames, fieldValues, false);
                    actionDiagnoses=ApiMessageReturn.trapMessage(actionDiagnosesObj.getDiagnostic(), actionDiagnosesObj.getMessageCodeObj(), actionDiagnosesObj.getMessageCodeVariables());
                    this.messageDynamicData=new Object[]{cohortName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);                                    
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), actionDiagnosesObj.getNewObjectId());
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_COHORT.getTableName()+NAME_SUFFIX, cohortName);
                    }
                    break;
                case STUDY_COHORT_ACTIVATE:
                    studyName = argValues[0].toString();
                    cohortName=argValues[1].toString();
                    actionDiagnosesObj =prjStudyCohort.studyCohortActivate(endPoint, studyName, cohortName);
                    this.messageDynamicData=new Object[]{cohortName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_COHORT.getTableName(), cohortName);                                    
                    break;
                case STUDY_COHORT_DEACTIVATE:
                    studyName = argValues[0].toString();
                    cohortName=argValues[1].toString();
                    actionDiagnosesObj =prjStudyCohort.studyCohortDeActivate(endPoint, studyName, cohortName);
                    this.messageDynamicData=new Object[]{cohortName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_COHORT.getTableName(), cohortName);                                    
                    break;
                case STUDY_COHORT_ADD_INDIVIDUAL:
                    studyName = argValues[0].toString();
                    cohortName=argValues[1].toString();
                    individualIdStr = argValues[2].toString();
                    actionDiagnosesObj =prjStudyCohort.studyCohortAddIndividual(endPoint, studyName, cohortName, individualIdStr);
                    this.messageDynamicData=new Object[]{individualIdStr, cohortName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_COHORT.getTableName(), cohortName);                                    
                    break;
                case STUDY_COHORT_REMOVE_INDIVIDUAL:
                    studyName = argValues[0].toString();
                    cohortName=argValues[1].toString();
                    individualIdStr = argValues[2].toString();
                    actionDiagnosesObj =prjStudyCohort.studyCohortRemoveIndividual(endPoint, studyName, cohortName, individualIdStr);
                    this.messageDynamicData=new Object[]{individualIdStr, cohortName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_COHORT.getTableName(), cohortName);                                    
                    break;
                case ADD_INDIVIDUAL_CONSENT:
                    studyName = argValues[0].toString();
                    Integer individualId = LPNulls.replaceNull(argValues[1]).toString().length() > 0 ? (Integer) argValues[1] : null;
                    String attachUrl = argValues[2].toString();
                    String briefSummary = argValues[3].toString();
                    //if (instr != null) {
                        actionDiagnosesObj = ClinicalStudyDataStudyIndividualConsent.addIndividualConsent(studyName, individualId, attachUrl, briefSummary);
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                            if (individualId != null) {
                                rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), individualId);
                            }
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_CONSENT.getTableName(), actionDiagnosesObj.getNewObjectId());
                        }
                    //}
                    break;
                case REMOVE_INDIVIDUAL_CONSENT:
                    studyName = argValues[0].toString();
                    individualId = LPNulls.replaceNull(argValues[1]).toString().length() > 0 ? (Integer) argValues[1] : null;
                    Integer attachmentId = LPNulls.replaceNull(argValues[2]).toString().length() > 0 ? (Integer) argValues[2] : null;
                    //if (instr != null) {
                        actionDiagnosesObj = ClinicalStudyDataStudyIndividualConsent.removeIndividualConsent(studyName, individualId, attachmentId);
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                            if (individualId != null) {
                                rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), individualId);
                            }
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_CONSENT.getTableName(), attachmentId);
                        }
                    //}
                    break;
                case REACTIVATE_INDIVIDUAL_CONSENT:
                    studyName = argValues[0].toString();
                    individualId = LPNulls.replaceNull(argValues[1]).toString().length() > 0 ? (Integer) argValues[1] : null;
                    attachmentId = LPNulls.replaceNull(argValues[2]).toString().length() > 0 ? (Integer) argValues[2] : null;
                    //if (instr != null) {
                        actionDiagnosesObj = ClinicalStudyDataStudyIndividualConsent.reactivateIndividualConsent(studyName, individualId, attachmentId);
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnosesObj.getDiagnostic())) {
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                            if (individualId != null) {
                                rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), individualId);
                            }
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_CONSENT.getTableName(), attachmentId);
                        }
                    //}
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
                    if (fieldValues!=null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                        actionDiagnoses=fieldValues;
                        break;
                    }
                    actionDiagnosesObj =prjStudySampleSet.createStudySamplesSet(endPoint, studyName, samplesSetName, samples, fieldNames, fieldValues, false);
                    actionDiagnoses=ApiMessageReturn.trapMessage(actionDiagnosesObj.getDiagnostic(), actionDiagnosesObj.getMessageCodeObj(), actionDiagnosesObj.getMessageCodeVariables());
                    this.messageDynamicData=new Object[]{samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);                                    
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), actionDiagnosesObj.getNewObjectId());
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName()+NAME_SUFFIX, samplesSetName);
                    }
                    break;
                case STUDY_SAMPLES_SET_ACTIVATE:
                    studyName = argValues[0].toString();
                    samplesSetName=argValues[1].toString();
                    actionDiagnosesObj =prjStudySampleSet.studySamplesSetActivate(endPoint, studyName, samplesSetName);
                    this.messageDynamicData=new Object[]{samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), samplesSetName);                                    
                    break;
                case STUDY_SAMPLES_SET_DEACTIVATE:
                    studyName = argValues[0].toString();
                    samplesSetName=argValues[1].toString();
                    actionDiagnosesObj =prjStudySampleSet.studySamplesSetDeActivate(endPoint, studyName, samplesSetName);
                    this.messageDynamicData=new Object[]{samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), samplesSetName);                                    
                    break;
                case STUDY_SAMPLES_SET_ADD_SAMPLE:
                    studyName = argValues[0].toString();
                    samplesSetName=argValues[1].toString();
                    sampleIdStr = argValues[2].toString();                    
                    actionDiagnosesObj =prjStudySampleSet.studySamplesSetAddSample(endPoint, studyName, samplesSetName, sampleIdStr);
                    this.messageDynamicData=new Object[]{sampleIdStr, samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), samplesSetName);                                    
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), sampleIdStr);                                    
                    break;
                case STUDY_SAMPLES_SET_REMOVE_SAMPLE:
                    studyName = argValues[0].toString();
                    samplesSetName=argValues[1].toString();
                    sampleIdStr = argValues[2].toString();
                    actionDiagnosesObj =prjStudySampleSet.studySamplesSetRemoveSample(endPoint, studyName, samplesSetName, sampleIdStr);
                    this.messageDynamicData=new Object[]{sampleIdStr, samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), samplesSetName);                                    
                    break;
                case ADD_VARIABLE_SET_TO_STUDY_OBJECT:     
                    String variableSetName=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.VARIABLE_SET_NAME.getParamName());
                    studyName=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.STUDY_NAME.getParamName());
                    String ownerTable=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.OWNER_TABLE.getParamName());
                    String ownerId=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.OWNER_ID.getParamName());
                    actionDiagnosesObj =ClinicalStudyDataStudyObjectsVariableValues.addVariableSetToObject(endPoint, studyName, variableSetName, ownerTable, ownerId);
                    actionDiagnoses=ApiMessageReturn.trapMessage(actionDiagnosesObj.getDiagnostic(), actionDiagnosesObj.getMessageCodeObj(), actionDiagnosesObj.getMessageCodeVariables());
                    messageDynamicData=LPArray.addValueToArray1D(messageDynamicData, new Object[]{variableSetName, ownerTable, ownerId});
                    rObj.addSimpleNode(procInstanceName,  TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET.getTableName(), variableSetName);
                    rObj.addSimpleNode(procInstanceName, ownerTable, ownerId);
                    break;   
                case ADD_VARIABLE_TO_STUDY_OBJECT:     
                    String variableName=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.VARIABLE_NAME.getParamName());
                    studyName=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.STUDY_NAME.getParamName());
                    ownerTable=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.OWNER_TABLE.getParamName());
                    ownerId=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.OWNER_ID.getParamName());
                    actionDiagnosesObj =ClinicalStudyDataStudyObjectsVariableValues.addVariableToObject(endPoint, studyName, variableName, ownerTable, ownerId, null, null);
                    actionDiagnoses=ApiMessageReturn.trapMessage(actionDiagnosesObj.getDiagnostic(), actionDiagnosesObj.getMessageCodeObj(), actionDiagnosesObj.getMessageCodeVariables());
                    messageDynamicData=LPArray.addValueToArray1D(messageDynamicData, new Object[]{variableName, ownerTable, ownerId});
                    rObj.addSimpleNode(procInstanceName,  TblsGenomaConfig.TablesGenomaConfig.VARIABLES.getTableName(), variableName);
                    rObj.addSimpleNode(procInstanceName, ownerTable, ownerId);
                    break;                       
                case ENTER_STUDY_OBJECT_VARIABLE_VALUE:     
                case REENTER_STUDY_OBJECT_VARIABLE_VALUE:     
                    variableSetName=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.VARIABLE_SET_NAME.getParamName());
                    studyName=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.STUDY_NAME.getParamName());
                    ownerTable=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.OWNER_TABLE.getParamName());
                    ownerId=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.OWNER_ID.getParamName());
                    variableName=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.VARIABLE_NAME.getParamName());
                    String newValue=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.NEW_VALUE.getParamName());
                    actionDiagnosesObj =ClinicalStudyDataStudyObjectsVariableValues.objectVariableSetValue(endPoint, studyName, ownerTable, ownerId, variableSetName, variableName, newValue);
                    messageDynamicData=LPArray.addValueToArray1D(messageDynamicData, new Object[]{newValue, variableName});
                    rObj.addSimpleNode(procInstanceName, TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES.getTableName(), variableName);
                    rObj.addSimpleNode(procInstanceName, ownerTable, ownerId);
                    break;                      
                default:
                    break;                    
            }    
            if (actionDiagnoses!=null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, this.messageDynamicData);                                
            this.diagnostic=actionDiagnoses;
            this.relatedObj=rObj;
            rObj.killInstance();
        }catch(Exception e){
             Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
             this.actionDiagnosesObj=new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.EXCEPTION_RAISED, new Object[]{e.getMessage()});
        } finally {
            // release database resources
            try {           
                procReqSession.killIt();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }                      
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

    @Override
    public InternalMessage getDiagnosticObj() {
        return this.actionDiagnosesObj;
    }
        @Override    public StringBuilder getRowArgsRows() {        return null;    }
        @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }

}
