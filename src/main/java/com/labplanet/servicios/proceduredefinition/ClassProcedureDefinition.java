package com.labplanet.servicios.proceduredefinition;

import databases.Rdbms;
import databases.TblsReqs;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.requirement.ProcedureDefinitionToInstanceUtility.procedureRolesList;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement.UomImportType;
import static functionaljavaa.unitsofmeasurement.UnitsOfMeasurement.getUomFromConfig;
import static functionaljavaa.user.UserAndRolesViews.getPersonByUser;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author User
 */
public class ClassProcedureDefinition {
    
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;

    public ClassProcedureDefinition(HttpServletRequest request, HttpServletResponse response, ProcedureDefinitionAPI.ProcedureDefinitionAPIEndpoints endPoint){
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        String[] mandatArgs=new String[]{};
        for (LPAPIArguments curArg:endPoint.getArguments()){
            if (curArg.getMandatory())
                mandatArgs=LPArray.addValueToArray1D(mandatArgs, curArg.getName());
        }
        if (mandatArgs.length>0){
            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, mandatArgs);                       
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, "en");              
                return;          
            }             
        }
        Object[] actionDiagnoses = null;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());        
        this.functionFound=true;
            switch (endPoint){
                case SET_PROCEDURE_BUSINESS_RULES:
                    String procInstanceName=argValues[0].toString();
                    String suffixName=argValues[1].toString();
                    String propName=argValues[2].toString();
                    String propValue=argValues[3].toString();
                    Parameter parm=new Parameter();
//                    parm.createPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
//                    procInstanceName+"-"+suffixName);  
                    String diagn=parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                        procInstanceName+"-"+suffixName, propName, propValue);

                    break;
                case ADD_USER:
                    String procedureName=argValues[0].toString();
                    Integer procedureVersion = (Integer) argValues[1];   
                    procInstanceName=argValues[2].toString();
                    String userName=argValues[3].toString();
                    Object[] personByUserObj = getPersonByUser(userName);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUserObj[0].toString())){
                        actionDiagnoses=personByUserObj;
                        break;
                    }
                    actionDiagnoses=Rdbms.insertRecordInTable(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_USERS.getTableName(), 
                            new String[]{TblsReqs.ProcedureUsers.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUsers.PROCEDURE_VERSION.getName(),
                                TblsReqs.ProcedureUsers.SCHEMA_PREFIX.getName(), TblsReqs.ProcedureUsers.USER_NAME.getName()}, 
                            new Object[]{procedureName, procedureVersion, procInstanceName, userName});
                    JSONObject createDBProcedureUsers = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBPersonProfiles(procedureName, procedureVersion, procInstanceName);
                    break;
                case ADD_ROLE_TO_USER:
                    procedureName=argValues[0].toString();
                    procedureVersion = (Integer) argValues[1];   
                    procInstanceName=argValues[2].toString();
                    String roleName=argValues[3].toString();
                    userName=argValues[4].toString();
                    personByUserObj = getPersonByUser(userName);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUserObj[0].toString())){
                        actionDiagnoses=personByUserObj;
                        break;
                    }
                    Object[] procedureRolesList = procedureRolesList(procedureName, procedureVersion);    
                    if (!LPArray.valueInArray(procedureRolesList, roleName)){
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The role <*1*> does not exist in procedure <*2*> and version <*3*>", new Object[]{roleName, procedureName, procedureVersion});
                        break;
                    }
                    actionDiagnoses=Rdbms.insertRecordInTable(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_USER_ROLES.getTableName(), 
                            new String[]{TblsReqs.ProcedureUserRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRoles.PROCEDURE_VERSION.getName(),
                                TblsReqs.ProcedureUserRoles.SCHEMA_PREFIX.getName(), TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()}, 
                            new Object[]{procedureName, procedureVersion, procInstanceName, userName, roleName});
                    JSONObject createDBProcedureUserRoles = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBPersonProfiles(procedureName, procedureVersion, procInstanceName);
                    break;
                case GET_UOM:
                    procedureName=argValues[0].toString();
                    procedureVersion = (Integer) argValues[1];   
                    procInstanceName=argValues[2].toString();
                    String uomName=argValues[3].toString();
                    String importType=argValues[4].toString();
                    UomImportType impTypeEnum=null;
                    try{
                        impTypeEnum = UomImportType.valueOf(importType.toUpperCase());
                    }catch(Exception e){
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "UOM Import Type "+importType+" not recognized", null);
                        LPFrontEnd.servletReturnResponseError(request, response, "UOM Import Type "+importType+" not recognized", new Object[]{importType}, "");                                      
                        return;
                    }   
                    actionDiagnoses=getUomFromConfig(uomName, importType);
                    break;                    
                case DEPLOY_REQUIREMENTS:
                    procedureName=argValues[0].toString();
                    procedureVersion = (Integer) argValues[1];  
                    procInstanceName=argValues[2].toString();
                    request.setAttribute("procedureName", procedureName);
                    request.setAttribute("procInstanceName", procInstanceName);
                    
                    //RequestDispatcher rd = request.getRequestDispatcher("/testing/platform/ProcedureDeployment");
                    RequestDispatcher rd = request.getRequestDispatcher("/ProcedureDefinitionToInstance");
                    
                    try {   
                        rd.forward(request,response);
                    } catch (ServletException | IOException ex) {
                        Logger.getLogger(LPFrontEnd.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Completed", null);
                    break;                    

                    //String procName = request.getParameter("procedureName"); //"process-us"; 
                    //String procInstanceName=request.getParameter("procInstanceName"); //"process-us";

                    
//                    JSONObject createDBProcedureUserRoles = functionaljavaa.requirement.ProcedureDefinitionToInstance.addProcedureSOPtoUsers(procName, procVersion, procInstanceName);
                     
/*                    String programName=argValues[0].toString();
                    Integer correctiveActionId = (Integer) argValues[1];                    
                    actionDiagnoses = DataProgramCorrectiveAction.markAsCompleted(procInstanceName, token, correctiveActionId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){                        
                        Object[][] correctiveActionInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsEnvMonitProcedure.ProgramCorrectiveAction.TBL.getName(), 
                            new String[]{TblsEnvMonitProcedure.ProgramCorrectiveAction.FLD_ID.getName()}, new Object[]{correctiveActionId},
                            new String[]{TblsEnvMonitProcedure.ProgramCorrectiveAction.FLD_SAMPLE_ID.getName()});
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{correctiveActionId, correctiveActionInfo[0][0], procInstanceName}); 
                        this.messageDynamicData=new Object[]{correctiveActionId, correctiveActionInfo[0][0], procInstanceName};   
                    }else{
                        this.messageDynamicData=new Object[]{correctiveActionId, procInstanceName};                           
                    }                    
                    break;
*/
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
