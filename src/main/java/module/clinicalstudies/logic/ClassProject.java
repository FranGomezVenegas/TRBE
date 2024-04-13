/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.clinicalstudies.logic;

import module.clinicalstudies.definition.TblsGenomaData;
import module.clinicalstudies.apis.GenomaProjectAPI.GenomaProjectAPIactionsEndPoints;
import functionaljavaa.modulegenoma.ClinicalStudyDataProject;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import static lbplanet.utilities.LPArray.convertStringWithDataTypeToObjectArrayInternalMessage;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import modules.masterdata.analysis.ConfigAnalysisStructure;
import trazit.enums.ActionsClass;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.definition.definition.TblsReqs;
import trazit.session.InternalMessage;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassProject  implements ActionsClass{
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    InternalMessage diagnosticObj;
    private Boolean functionFound=false;
    private EnumIntEndpoints enumConstantByName;
    
    public ClassProject(HttpServletRequest request, GenomaProjectAPIactionsEndPoints endPoint){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=procReqSession.getProcedureInstance();
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();

        ClinicalStudyDataProject prj = new ClinicalStudyDataProject();
        String projectName = "";
        this.enumConstantByName=endPoint;
        InternalMessage diagnosticObj = null;
        this.functionFound=true;
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
            this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.messageDynamicData = new Object[]{argValues[2].toString()};
            this.relatedObj = rObj;
            rObj.killInstance();
            return;
        }
        try{
            switch (endPoint){
                case PROJECT_NEW:
                case PROJECT_UPDATE:
                    projectName = argValues[0].toString();
                    String fieldName=argValues[1].toString();
                    String fieldValue=LPNulls.replaceNull(argValues[2]).toString();
                    String[] fieldNames=new String[0];
                    Object[] fieldValues=new Object[0];
                    if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                    if (fieldValue!=null && fieldValue.length()>0) fieldValues = convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"), TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA, fieldName.split("\\|"));
                    if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                        this.diagnosticObj=(InternalMessage)fieldValues[1];
                        break;
                    }else{
                        if ("PROJECT_NEW".equalsIgnoreCase(endPoint.getName())){
                            diagnosticObj= prj.createProject(endPoint, projectName, fieldNames, fieldValues,  false);
                        }
                        if ("PROJECT_UPDATE".equalsIgnoreCase(endPoint.getName()))
                            this.diagnosticObj= prj.projectUpdate(endPoint, projectName, fieldNames, fieldValues);
                        rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsGenomaData.TablesGenomaData.PROJECT.getTableName(), projectName);                
                        this.messageDynamicData=new Object[]{projectName, procInstanceName};                    
                    }
                    break;
                case PROJECT_ACTIVATE:
                case PROJECT_DEACTIVATE:
                    projectName = argValues[0].toString();
                    if ("PROJECT_ACTIVATE".equalsIgnoreCase(endPoint.getName()))
                        this.diagnosticObj =prj.projectActivate(endPoint, projectName);
                    else if ("PROJECT_DEACTIVATE".equalsIgnoreCase(endPoint.getName()))
                        this.diagnosticObj =prj.projectDeActivate(endPoint, projectName);                    
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
                    this.diagnosticObj =prj.projectUserManagement(endPoint, projectName, userName, userRole);
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
        this.relatedObj=rObj;
        rObj.killInstance();
    }catch(Exception e){
         Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
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
        return null;
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
