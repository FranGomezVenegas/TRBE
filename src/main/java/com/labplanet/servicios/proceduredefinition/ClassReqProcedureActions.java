package com.labplanet.servicios.proceduredefinition;

import com.labplanet.servicios.proceduredefinition.ReqProcedureEnums.ProcedureDefinitionAPIActionsEndpoints;
import com.labplanet.servicios.proceduredefinition.ReqProcedureEnums.ReqProcedureDefinitionErrorTraping;
import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import databases.RdbmsObject;
import databases.TblsReqs;
import databases.TblsReqs.TablesReqs;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.requirement.ProcedureDefinitionToInstanceUtility.procedureRolesList;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement.UomImportType;
import static functionaljavaa.unitsofmeasurement.UnitsOfMeasurement.getUomFromConfig;
import static functionaljavaa.user.UserAndRolesViews.getPersonByUser;
import java.io.IOException;
import java.util.Arrays;
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
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.LpPlatformSuccess;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author User
 */
public class ClassReqProcedureActions {
    
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;

    public static Boolean isProcInstLocked(String procName, Integer procVersion, String instanceName){
        String[] fieldsToRetrieve=new String[]{TblsReqs.ProcedureInfo.LOCKED_FOR_ACTIONS.getName()};
        Object[][] procAndInstanceArr = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(), 
        new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, 
        new Object[]{procName, procVersion, instanceName}, fieldsToRetrieve, fieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procAndInstanceArr[0][0].toString()))
            return true;
        return Boolean.TRUE.equals(Boolean.valueOf(procAndInstanceArr[0][0].toString()));
    } 
    public ClassReqProcedureActions(HttpServletRequest request, HttpServletResponse response, ProcedureDefinitionAPIActionsEndpoints endPoint){
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        String[] mandatArgs=new String[]{};
        for (LPAPIArguments curArg:endPoint.getArguments()){
            if (Boolean.TRUE.equals(curArg.getMandatory()))
                mandatArgs=LPArray.addValueToArray1D(mandatArgs, curArg.getName());
        }
        if (mandatArgs.length>0){
            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, mandatArgs);                       
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, "en", LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;          
            }             
        }
        Object[] actionDiagnoses = null;
        this.functionFound=true;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
            this.diagnostic=(Object[]) argValues[1];
            this.messageDynamicData=new Object[]{argValues[2].toString()};
            return;                        
        }            
        String procedureName=argValues[0].toString();
        Integer procedureVersion = (Integer) argValues[1];   
        String procInstanceName=argValues[2].toString();
        if (Boolean.TRUE.equals(isProcInstLocked(procedureName, procedureVersion, procInstanceName))){
            LPFrontEnd.servletReturnResponseError(request, response, 
                ReqProcedureDefinitionErrorTraping.INSTANCE_LOCKED_FOR_ACTIONS.getErrorCode(), new Object[]{procedureName, procedureVersion, procInstanceName}, "en", LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;                      
        }
        
        this.functionFound=true;
            switch (endPoint){
                case SET_PROCEDURE_BUSINESS_RULES:
                    procedureName=argValues[0].toString();
                    procedureVersion = (Integer) argValues[1];   
                    procInstanceName=argValues[2].toString();
                    String suffixName=argValues[3].toString();
                    String propName=argValues[4].toString();
                    String propValue=argValues[5].toString();
                    Parameter parm=new Parameter();
                    parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                        procInstanceName+"-"+suffixName, propName, propValue);

                    break;
                case ADD_USER:
                    procedureName=argValues[0].toString();
                    procedureVersion = (Integer) argValues[1];   
                    procInstanceName=argValues[2].toString();
                    String userName=argValues[3].toString();
                    Object[] personByUserObj = getPersonByUser(userName);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUserObj[0].toString())){
                        actionDiagnoses=personByUserObj;
                        break;
                    }
                    RdbmsObject insertDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROC_USERS, 
                    new String[]{TblsReqs.ProcedureUsers.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUsers.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureUsers.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUsers.USER_NAME.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, userName});
                    if (Boolean.TRUE.equals(insertDiagn.getRunSuccess()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());                    
                    else
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
                    functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBPersonProfiles(procedureName, procedureVersion, procInstanceName);
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
                    insertDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROC_USER_ROLES, 
                    new String[]{TblsReqs.ProcedureUserRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRoles.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, userName, roleName});
                    if (Boolean.TRUE.equals(insertDiagn.getRunSuccess()))
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());                    
                    else
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
                    functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBPersonProfiles(procedureName, procedureVersion, procInstanceName);
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
                        LPFrontEnd.servletReturnResponseError(request, response, "UOM Import Type "+importType+" not recognized", new Object[]{importType}, "", null);
                        return;
                    }   
                    actionDiagnoses=getUomFromConfig(uomName, importType);
                    break;                    
                case PROC_DEPLOY_CHECKER:
                    request.setAttribute("run_as_checker", true);
/*                    JSONObject jMainObj = new JSONObject();
                    String mainObjectName = "proc_deploy_check_summary"; 
                    procedureName=argValues[0].toString();
                    procedureVersion = (Integer) argValues[1];  
                    procInstanceName=argValues[2].toString();       
                    JSONObject jObj=new JSONObject();                

                    
                    jObj.put("Status", "Under Development");
                    jMainObj.put(mainObjectName, jObj);
                    LPFrontEnd.servletReturnSuccess(request, response, jMainObj);                    
                    return;                    */
                case DEPLOY_REQUIREMENTS:
                    procedureName=argValues[0].toString();
                    procedureVersion = (Integer) argValues[1];  
                    procInstanceName=argValues[2].toString();
                    request.setAttribute("procedureName", procedureName);
                    request.setAttribute("procInstanceName", procInstanceName);
                    request.setAttribute("endPointName", endPoint.getName());
                    //RequestDispatcher rd = request.getRequestDispatcher("/testing/platform/ProcedureDeployment");
                    RequestDispatcher rd = request.getRequestDispatcher("/ProcedureDefinitionToInstance");
                    
                    try {   
                        rd.forward(request,response);
                    } catch (ServletException | IOException ex) {
                        Logger.getLogger(LPFrontEnd.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Completed", null);
                    break;   
                case DEPLOY_REQUIREMENTS_CLONE_SPRINT:
                    procedureName=argValues[0].toString();
                    procedureVersion = (Integer) argValues[1];  
                    procInstanceName=argValues[2].toString();
                    String newProcInstanceName=argValues[3].toString();
                    Boolean continueIfNewExists=Boolean.valueOf(LPNulls.replaceNull(argValues[4]).toString());
                    String[] clonableProcInstanceFldName=new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()};
                    Object[] clonableProcInstanceFldValue=new Object[]{procedureName, procedureVersion, procInstanceName};                    
                    Object[] existsRecord = Rdbms.existsRecord(TablesReqs.PROCEDURE_INFO.getRepositoryName(), TablesReqs.PROCEDURE_INFO.getTableName(), 
                        clonableProcInstanceFldName, clonableProcInstanceFldValue);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsRecord[0].toString())){
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, null);
                        break;
                    }
                    if (Boolean.FALSE.equals(continueIfNewExists)){
                        existsRecord = Rdbms.existsRecord(TablesReqs.PROCEDURE_INFO.getRepositoryName(), TablesReqs.PROCEDURE_INFO.getTableName(), 
                            new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()},
                            new Object[]{procedureName, procedureVersion, newProcInstanceName});
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())){
                            actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.RECORD_ALREADY_EXISTS, null);
                            break;
                        }
                    }
                    String[] tblsArr=null;
                    String[] tblsWithErrorArr=null;
                    for (EnumIntTables curTbl: TablesReqs.values()){
                        Integer valuePosicInArray = null;
                        tblsArr=LPArray.addValueToArray1D(tblsArr, curTbl.getTableName());
                        if ("fe_proc_model".equalsIgnoreCase(curTbl.getTableName()) )
                            valuePosicInArray=-1;
                        String[] curTblAllFields=EnumIntTableFields.getAllFieldNames(curTbl.getTableFields());
                        Object[][] curTblInfo=QueryUtilitiesEnums.getTableData(curTbl, 
                            EnumIntTableFields.getTableFieldsFromString(curTbl, "ALL"),
                            clonableProcInstanceFldName, clonableProcInstanceFldValue, clonableProcInstanceFldName);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(curTblInfo[0][0].toString()))
                            tblsWithErrorArr=LPArray.addValueToArray1D(tblsWithErrorArr, curTbl.getTableName());
                        else{
                            valuePosicInArray = LPArray.valuePosicInArray(curTblAllFields, TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName());
                            if (valuePosicInArray==-1)
                                tblsWithErrorArr=LPArray.addValueToArray1D(tblsWithErrorArr, curTbl.getTableName());
                            else{
                                curTblInfo=LPArray.setColumnValueToArray2D(curTblInfo, valuePosicInArray, newProcInstanceName);
                                for (Object[] curTblRec: curTblInfo){
                                    RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(curTbl, 
                                            curTblAllFields, curTblRec);
                                    if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess()))
                                        tblsWithErrorArr=LPArray.addValueToArray1D(tblsWithErrorArr, curTbl.getTableName());                                    
                                }
                            }
                        }                        
                    }                    
                    if (tblsWithErrorArr!=null && tblsWithErrorArr.length>0)
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.DB_ERROR, new Object[]{Arrays.toString(tblsWithErrorArr)});
                    else
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.ALL_FINE, null);
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
