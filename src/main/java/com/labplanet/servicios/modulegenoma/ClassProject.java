/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulegenoma;

import com.labplanet.servicios.modulegenoma.GenomaProjectAPI.GenomaProjectAPIEndPoints;
import functionaljavaa.modulegenoma.GenomaDataProject;
import functionaljavaa.modulegenoma.GenomaDataStudy;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public class ClassProject {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;

    public ClassProject(HttpServletRequest request, GenomaProjectAPIEndPoints endPoint){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();

        GenomaDataProject prj = new GenomaDataProject();
        GenomaDataStudy prjStudy = new GenomaDataStudy();
        String projectName = "";
        
        Object[] actionDiagnoses = null;
        InternalMessage actionDiagnosesObj = null;
        this.functionFound=true;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
            //procReqSession.killIt();
            this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, 
                    argValues[1].toString(), new Object[]{argValues[2].toString()});
            this.messageDynamicData=new Object[]{argValues[2].toString()};
            return;                        
        }        
            switch (endPoint){
                case PROJECT_NEW:
                case PROJECT_UPDATE:
                    projectName = argValues[0].toString();
                    String fieldName=argValues[1].toString();
                    String fieldValue=LPNulls.replaceNull(argValues[2]).toString();
                    String[] fieldNames=new String[0];
                    Object[] fieldValues=new Object[0];
                    if (fieldName!=null && fieldName.length()>0 && fieldValue.length()>0){
                        fieldNames = fieldName.split("\\|");                                            
                        if (fieldValue!=null && fieldValue.length()>0) 
                            fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));                                                                                
                        if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                            actionDiagnoses=fieldValues;
                            break;
                        }
                    }
                    if ("PROJECT_NEW".equalsIgnoreCase(endPoint.getName())){
                        actionDiagnosesObj= prj.createProject(endPoint, projectName, fieldNames, fieldValues,  false);
                        actionDiagnoses=ApiMessageReturn.trapMessage(actionDiagnosesObj.getDiagnostic(), actionDiagnosesObj.getMessageCodeObj(), actionDiagnosesObj.getMessageCodeVariables());
                    }
                    if ("PROJECT_UPDATE".equalsIgnoreCase(endPoint.getName()))
                        actionDiagnoses= prj.projectUpdate(endPoint, projectName, fieldNames, fieldValues);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.PROJECT.getTableName(), projectName);                
                    this.messageDynamicData=new Object[]{projectName, procInstanceName};
                    break;
                case PROJECT_ACTIVATE:
                case PROJECT_DEACTIVATE:
                    projectName = argValues[0].toString();
                    if ("PROJECT_ACTIVATE".equalsIgnoreCase(endPoint.getName()))
                        actionDiagnoses =prj.projectActivate(endPoint, projectName);
                    else if ("PROJECT_DEACTIVATE".equalsIgnoreCase(endPoint.getName()))
                        actionDiagnoses =prj.projectDeActivate(endPoint, projectName);                    
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.PROJECT.getTableName(), projectName);                
                    break;
                case PROJECT_ADD_USER:
                case PROJECT_REMOVE_USER:
                case PROJECT_CHANGE_USER_ROLE:
                case PROJECT_USER_ACTIVATE:
                case PROJECT_USER_DEACTIVATE:
                    projectName = argValues[0].toString();
                    String userName=argValues[1].toString();
                    String userRole=argValues[2].toString();
                    actionDiagnoses =prj.projectUserManagement(endPoint, projectName, userName, userRole);
                    this.messageDynamicData=new Object[]{userName, projectName, userName, userRole, procInstanceName};
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.PROJECT.getTableName(), projectName);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.PROJECT_USERS.getTableName(), userName);
                    break;
/*                case STUDY_NEW:
                    projectName = argValues[0].toString();
                    String studyName = argValues[1].toString();
                    fieldName=argValues[2].toString();
                    fieldValue=argValues[3].toString();
                    fieldNames=new String[0];
                    fieldValues=new Object[0];
                    if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");    
                    
                    if (fieldValue!=null && fieldValue.length()>0) 
                        //fieldValues=TblsGenomaData.Study.convertStringWithDataTypeToObjectArray(fieldNames, fieldValue.split("\\|"));
                        fieldValues=LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    actionDiagnoses= prjStudy.createStudy(endPoint, studyName, projectName, fieldNames, fieldValues,  false);
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.STUDY.getTableName(), studyName);                
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{studyName, procInstanceName});                    
                    this.messageDynamicData=new Object[]{projectName, studyName, procInstanceName};
                    break;*/
                default:
                    break;
            }    
        if (actionDiagnoses!=null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
            actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, this.messageDynamicData);                    
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
