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
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPMath;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import static module.clinicalstudies.apis.GenomaStudyAPI.GenomaStudyAPIactionsEndPoints.ENTER_STUDY_OBJECT_VARIABLE_VALUE;
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
public class ClassStudy implements ActionsClass{
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    InternalMessage diagnosticObj;
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
        this.diagnosticObj = null;
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
            this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.messageDynamicData = new Object[]{argValues[2].toString()};
            this.relatedObj = rObj;
            rObj.killInstance();
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
                        diagnosticObj= prjStudy.createStudy(endPoint, studyName, projectName, fieldNames, fieldValues,  false);
                    }
                    if ("STUDY_UPDATE".equalsIgnoreCase(endPoint.getName()))
                        diagnosticObj= prjStudy.studyUpdate(endPoint, studyName, fieldNames, fieldValues);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);                
                    this.messageDynamicData=new Object[]{projectName, studyName, procInstanceName};
                    break;
                case STUDY_ACTIVATE:
                case STUDY_DEACTIVATE:
                    studyName = argValues[0].toString();
                    if ("STUDY_ACTIVATE".equalsIgnoreCase(endPoint.getName()))
                        diagnosticObj =prjStudy.studyActivate(endPoint, studyName);
                    else if ("STUDY_DEACTIVATE".equalsIgnoreCase(endPoint.getName()))
                        diagnosticObj =prjStudy.studyDeActivate(endPoint, studyName);                    
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
                    diagnosticObj =prjStudy.studyUserManagement(endPoint, studyName, userName, userRole);
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
                                diagnosticObj = (InternalMessage) fieldValues[1];
                                break;
                            }
                        }
                    }
                    diagnosticObj =prjStudyIndividual.createStudyIndividual(endPoint, studyName, indvidualName, fieldNames, fieldValues, false);
                    this.messageDynamicData=new Object[]{indvidualName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosticObj.getDiagnostic())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), diagnosticObj.getNewObjectId());
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName()+NAME_SUFFIX, indvidualName);
                    }
                    break;
                case STUDY_INDIVIDUAL_ACTIVATE:
                    studyName = argValues[0].toString();
                    String indivId=argValues[1].toString();
                    diagnosticObj =prjStudyIndividual.studyIndividualActivate(endPoint, studyName, Integer.valueOf(indivId));
                    this.messageDynamicData=new Object[]{indivId, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);                                    
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), indivId);                                    
                    break;
                case STUDY_INDIVIDUAL_DEACTIVATE:
                    studyName = argValues[0].toString();
                    indivId=argValues[1].toString();
                    diagnosticObj=LPMath.isNumeric(indivId, false);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosticObj.getDiagnostic())){
                        break;
                    }
                    else
                        diagnosticObj =prjStudyIndividual.studyIndividualDeActivate(endPoint, studyName, Integer.valueOf(indivId));
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
                    diagnosticObj = prjStudyIndividualSmp.createStudyIndividualSample(endPoint, studyName, indivIdInt, new String[0], new Object[0], false);
                    this.messageDynamicData=new Object[]{diagnosticObj.getNewObjectId(), indivId, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);                                    
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), indivId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosticObj.getDiagnostic())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), diagnosticObj.getNewObjectId());
                    }
                    break;

                case STUDY_INDIVIDUAL_SAMPLE_ACTIVATE:
                    studyName = argValues[0].toString();
                    indivId=argValues[1].toString();
                    String sampleIdStr=argValues[2].toString();
                    indivIdInt=null;
                    if (indivId.length()>0)
                        indivIdInt=Integer.valueOf(indivId);                    
                    diagnosticObj =prjStudyIndividualSmp.studyIndividualSampleActivate(endPoint, studyName, indivIdInt, Integer.valueOf(sampleIdStr));
                    this.messageDynamicData=new Object[]{sampleIdStr, indivId, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), indivId);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), sampleIdStr);
                    break;
                case STUDY_INDIVIDUAL_SAMPLE_DEACTIVATE:
                    studyName = argValues[0].toString();
                    indivId=argValues[1].toString();
                    sampleIdStr=argValues[2].toString();
                    diagnosticObj =prjStudyIndividualSmp.studyIndividualSampleDeActivate(endPoint, studyName, Integer.valueOf(indivId), Integer.valueOf(sampleIdStr));
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
                        diagnosticObj = (InternalMessage) fieldValues[1];
                        break;
                    }
                    diagnosticObj =prjStudyFamily.createStudyFamily(endPoint, studyName, familyName, individualsList, fieldNames, fieldValues, false);
                    this.messageDynamicData=new Object[]{familyName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);                                    
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosticObj.getDiagnostic())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), diagnosticObj.getNewObjectId());
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName()+NAME_SUFFIX, familyName);
                    }
                    break;
                case STUDY_FAMILY_ACTIVATE:
                    studyName = argValues[0].toString();
                    familyName=argValues[1].toString();
                    diagnosticObj =prjStudyFamily.studyFamilyActivate(endPoint, studyName, familyName);
                    this.messageDynamicData=new Object[]{familyName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), familyName);                                    
                    break;
                case STUDY_FAMILY_DEACTIVATE:
                    studyName = argValues[0].toString();
                    familyName=argValues[1].toString();
                    diagnosticObj =prjStudyFamily.studyFamilyDeActivate(endPoint, studyName, familyName);
                    this.messageDynamicData=new Object[]{familyName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), familyName);                                    
                    break;
                case STUDY_FAMILY_ADD_INDIVIDUAL:
                    studyName = argValues[0].toString();
                    familyName=argValues[1].toString();
                    String individualIdStr = argValues[2].toString();
                    diagnosticObj =prjStudyFamily.studyFamilyAddIndividual(endPoint, studyName, familyName, individualIdStr);
                    this.messageDynamicData=new Object[]{individualIdStr, familyName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), familyName);                                    
                    break;
                case STUDY_FAMILY_REMOVE_INDIVIDUAL:
                    studyName = argValues[0].toString();
                    familyName=argValues[1].toString();
                    individualIdStr = argValues[2].toString();
                    diagnosticObj =prjStudyFamily.studyFamilyRemoveIndividual(endPoint, studyName, familyName, individualIdStr);
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
                        diagnosticObj = (InternalMessage) fieldValues[1];
                        break;
                    }
                    diagnosticObj =prjStudyCohort.createStudyCohort(endPoint, studyName, cohortName, individualsList, fieldNames, fieldValues, false);
                    this.messageDynamicData=new Object[]{cohortName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);                                    
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosticObj.getDiagnostic())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), diagnosticObj.getNewObjectId());
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_COHORT.getTableName()+NAME_SUFFIX, cohortName);
                    }
                    break;
                case STUDY_COHORT_ACTIVATE:
                    studyName = argValues[0].toString();
                    cohortName=argValues[1].toString();
                    diagnosticObj =prjStudyCohort.studyCohortActivate(endPoint, studyName, cohortName);
                    this.messageDynamicData=new Object[]{cohortName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_COHORT.getTableName(), cohortName);                                    
                    break;
                case STUDY_COHORT_DEACTIVATE:
                    studyName = argValues[0].toString();
                    cohortName=argValues[1].toString();
                    diagnosticObj =prjStudyCohort.studyCohortDeActivate(endPoint, studyName, cohortName);
                    this.messageDynamicData=new Object[]{cohortName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_COHORT.getTableName(), cohortName);                                    
                    break;
                case STUDY_COHORT_ADD_INDIVIDUAL:
                    studyName = argValues[0].toString();
                    cohortName=argValues[1].toString();
                    individualIdStr = argValues[2].toString();
                    diagnosticObj =prjStudyCohort.studyCohortAddIndividual(endPoint, studyName, cohortName, individualIdStr);
                    this.messageDynamicData=new Object[]{individualIdStr, cohortName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_COHORT.getTableName(), cohortName);                                    
                    break;
                case STUDY_COHORT_REMOVE_INDIVIDUAL:
                    studyName = argValues[0].toString();
                    cohortName=argValues[1].toString();
                    individualIdStr = argValues[2].toString();
                    diagnosticObj =prjStudyCohort.studyCohortRemoveIndividual(endPoint, studyName, cohortName, individualIdStr);
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
                        diagnosticObj = ClinicalStudyDataStudyIndividualConsent.addIndividualConsent(studyName, individualId, attachUrl, briefSummary);
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosticObj.getDiagnostic())) {
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                            if (individualId != null) {
                                rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), individualId);
                            }
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_CONSENT.getTableName(), diagnosticObj.getNewObjectId());
                        }
                    //}
                    break;
                case REMOVE_INDIVIDUAL_CONSENT:
                    studyName = argValues[0].toString();
                    individualId = LPNulls.replaceNull(argValues[1]).toString().length() > 0 ? (Integer) argValues[1] : null;
                    Integer attachmentId = LPNulls.replaceNull(argValues[2]).toString().length() > 0 ? (Integer) argValues[2] : null;
                    //if (instr != null) {
                        diagnosticObj = ClinicalStudyDataStudyIndividualConsent.removeIndividualConsent(studyName, individualId, attachmentId);
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosticObj.getDiagnostic())) {
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
                        diagnosticObj = ClinicalStudyDataStudyIndividualConsent.reactivateIndividualConsent(studyName, individualId, attachmentId);
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosticObj.getDiagnostic())) {
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
                        diagnosticObj = (InternalMessage) fieldValues[1];
                        break;
                    }
                    diagnosticObj =prjStudySampleSet.createStudySamplesSet(endPoint, studyName, samplesSetName, samples, fieldNames, fieldValues, false);
                    this.messageDynamicData=new Object[]{samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);                                    
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosticObj.getDiagnostic())){
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), diagnosticObj.getNewObjectId());
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName()+NAME_SUFFIX, samplesSetName);
                    }
                    break;
                case STUDY_SAMPLES_SET_ACTIVATE:
                    studyName = argValues[0].toString();
                    samplesSetName=argValues[1].toString();
                    diagnosticObj =prjStudySampleSet.studySamplesSetActivate(endPoint, studyName, samplesSetName);
                    this.messageDynamicData=new Object[]{samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), samplesSetName);                                    
                    break;
                case STUDY_SAMPLES_SET_DEACTIVATE:
                    studyName = argValues[0].toString();
                    samplesSetName=argValues[1].toString();
                    diagnosticObj =prjStudySampleSet.studySamplesSetDeActivate(endPoint, studyName, samplesSetName);
                    this.messageDynamicData=new Object[]{samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), samplesSetName);                                    
                    break;
                case STUDY_SAMPLES_SET_ADD_SAMPLE:
                    studyName = argValues[0].toString();
                    samplesSetName=argValues[1].toString();
                    sampleIdStr = argValues[2].toString();                    
                    diagnosticObj =prjStudySampleSet.studySamplesSetAddSample(endPoint, studyName, samplesSetName, sampleIdStr);
                    this.messageDynamicData=new Object[]{sampleIdStr, samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), samplesSetName);                                    
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), sampleIdStr);                                    
                    break;
                case STUDY_SAMPLES_SET_REMOVE_SAMPLE:
                    studyName = argValues[0].toString();
                    samplesSetName=argValues[1].toString();
                    sampleIdStr = argValues[2].toString();
                    diagnosticObj =prjStudySampleSet.studySamplesSetRemoveSample(endPoint, studyName, samplesSetName, sampleIdStr);
                    this.messageDynamicData=new Object[]{sampleIdStr, samplesSetName, studyName, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), samplesSetName);                                    
                    break;
                case ADD_VARIABLE_SET_TO_STUDY_OBJECT:     
                    String variableSetName=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.VARIABLE_SET_NAME.getParamName());
                    studyName=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.STUDY_NAME.getParamName());
                    String ownerTable=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.OWNER_TABLE.getParamName());
                    String ownerId=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.OWNER_ID.getParamName());
                    diagnosticObj =ClinicalStudyDataStudyObjectsVariableValues.addVariableSetToObject(endPoint, studyName, variableSetName, ownerTable, ownerId);
                    messageDynamicData=LPArray.addValueToArray1D(messageDynamicData, new Object[]{variableSetName, ownerTable, ownerId});
                    rObj.addSimpleNode(procInstanceName,  TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET.getTableName(), variableSetName);
                    rObj.addSimpleNode(procInstanceName, ownerTable, ownerId);
                    break;   
                case ADD_VARIABLE_TO_STUDY_OBJECT:     
                    String variableName=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.VARIABLE_NAME.getParamName());
                    studyName=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.STUDY_NAME.getParamName());
                    ownerTable=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.OWNER_TABLE.getParamName());
                    ownerId=request.getParameter(GenomaProjectAPI.GenomaProjectAPIParamsList.OWNER_ID.getParamName());
                    diagnosticObj =ClinicalStudyDataStudyObjectsVariableValues.addVariableToObject(endPoint, studyName, variableName, ownerTable, ownerId, null, null);
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
                    diagnosticObj =ClinicalStudyDataStudyObjectsVariableValues.objectVariableSetValue(endPoint, studyName, ownerTable, ownerId, variableSetName, variableName, newValue);
                    messageDynamicData=LPArray.addValueToArray1D(messageDynamicData, new Object[]{newValue, variableName});
                    rObj.addSimpleNode(procInstanceName, TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES.getTableName(), variableName);
                    rObj.addSimpleNode(procInstanceName, ownerTable, ownerId);
                    break;                      
                default:
                    break;                    
            }    
            this.relatedObj=rObj;
            rObj.killInstance();
        }catch(Exception e){
             Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
             this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.EXCEPTION_RAISED, new Object[]{e.getMessage()});
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
        return null;
    }

    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
    }

    @Override
    public InternalMessage getDiagnosticObj() {
        return this.diagnosticObj;
    }
        @Override    public StringBuilder getRowArgsRows() {        return null;    }
        @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }

    
    @Override    public void initializeEndpoint(String actionName) {        throw new UnsupportedOperationException("Not supported yet.");}
    @Override    public void createClassEnvMonAndHandleExceptions(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {        throw new UnsupportedOperationException("Not supported yet.");}

    @Override
    public HttpServletResponse getHttpResponse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
