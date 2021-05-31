package com.labplanet.servicios.proceduredefinition;

import databases.Rdbms;
import databases.TblsReqs;
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
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
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
        
        Object[] actionDiagnoses = null;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());        
        this.functionFound=true;
            switch (endPoint){
                case ADD_USER:
                    String procedureName=argValues[0].toString();
                    Integer procedureVersion = (Integer) argValues[1];   
                    String procInstanceName=argValues[2].toString();
                    String userName=argValues[3].toString();
                    Object[] personByUserObj = getPersonByUser(userName);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUserObj[0].toString())){
                        actionDiagnoses=personByUserObj;
                        break;
                    }
                    actionDiagnoses=Rdbms.insertRecordInTable(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureUsers.TBL.getName(), 
                            new String[]{TblsReqs.ProcedureUsers.FLD_PROCEDURE_NAME.getName(), TblsReqs.ProcedureUsers.FLD_PROCEDURE_VERSION.getName(),
                                TblsReqs.ProcedureUsers.FLD_SCHEMA_PREFIX.getName(), TblsReqs.ProcedureUsers.FLD_USER_NAME.getName()}, 
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
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The role <*1*> does not exist in procedure <*2*> and version <*3*>", new Object[]{roleName, procedureName, procedureVersion});
                        break;
                    }
                    actionDiagnoses=Rdbms.insertRecordInTable(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureUserRole.TBL.getName(), 
                            new String[]{TblsReqs.ProcedureUserRole.FLD_PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRole.FLD_PROCEDURE_VERSION.getName(),
                                TblsReqs.ProcedureUserRole.FLD_SCHEMA_PREFIX.getName(), TblsReqs.ProcedureUserRole.FLD_USER_NAME.getName(), TblsReqs.ProcedureUserRole.FLD_ROLE_NAME.getName()}, 
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
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "UOM Import Type "+importType+" not recognized", null);
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
                    actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "Completed", null);
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
