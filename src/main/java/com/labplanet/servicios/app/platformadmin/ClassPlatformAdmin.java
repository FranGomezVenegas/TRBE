/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app.platformadmin;

import databases.TblsApp;
import functionaljavaa.platformadmin.AdminActions;
import functionaljavaa.platformadmin.PlatformAdminEnums.PlatformAdminAPIActionsEndpoints;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.user.UserAndRolesViews;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.ApiErrorTraping;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ClassPlatformAdmin {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    InternalMessage actionDiagnosesObj=null;    
    private Boolean functionFound=false;

    public ClassPlatformAdmin(HttpServletRequest request, PlatformAdminAPIActionsEndpoints endPoint){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();

        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        InternalMessage actionDiagnoses = null;
        this.functionFound=true;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments()); 
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
            String language = instanceForActions.getLanguage();
            this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, 
                    (EnumIntMessages)argValues[1] , new Object[]{argValues[2].toString()});
            this.messageDynamicData=new Object[]{argValues[2].toString()};
            return;                        
        }         
            switch (endPoint){
                case ADD_WHITE_IP:
                    String ipVal1=LPNulls.replaceNull(argValues[0]).toString();
                    String ipVal2=LPNulls.replaceNull(argValues[1]).toString();
                    String ipVal3=LPNulls.replaceNull(argValues[2]).toString();
                    String ipVal4=LPNulls.replaceNull(argValues[3]).toString();
                    String description=LPNulls.replaceNull(argValues[4]).toString();
                    String[] fieldNames=new String[]{TblsApp.IPWhiteList.ACTIVE.getName()};
                    Object[] fieldValues=new Object[]{true};
                    actionDiagnoses=AdminActions.addWhiteIp(ipVal1, ipVal2, ipVal3, ipVal4, description);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(),TblsApp.TablesApp.IP_WHITE_LIST.getTableName(), actionDiagnoses.getNewObjectId());
                    break;
                case ADD_BLACK_IP:
                    ipVal1=LPNulls.replaceNull(argValues[0]).toString();
                    ipVal2=LPNulls.replaceNull(argValues[1]).toString();
                    ipVal3=LPNulls.replaceNull(argValues[2]).toString();
                    ipVal4=LPNulls.replaceNull(argValues[3]).toString();
                    description=LPNulls.replaceNull(argValues[4]).toString();
                    fieldNames=new String[]{TblsApp.IPWhiteList.ACTIVE.getName()};
                    fieldValues=new Object[]{true};
                    actionDiagnoses=AdminActions.addBlackIp(ipVal1, ipVal2, ipVal3, ipVal4, description);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), actionDiagnoses.getNewObjectId());
                    break;
                case ACTIVATE_WHITE_IP:
                    String id=LPNulls.replaceNull(argValues[0]).toString();
                    actionDiagnoses=AdminActions.activateWhiteIp(Integer.valueOf(id));
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_WHITE_LIST.getTableName(), id);
                    break;
                case DEACTIVATE_WHITE_IP:
                    id=LPNulls.replaceNull(argValues[0]).toString();
                    actionDiagnoses=AdminActions.deActivateWhiteIp(Integer.valueOf(id));
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_WHITE_LIST.getTableName(), id);
                    break;
                case UPDATE_WHITE_IP:
                    Integer idInt=Integer.valueOf(LPNulls.replaceNull(argValues[0]).toString());
                    ipVal1=LPNulls.replaceNull(argValues[1]).toString();
                    ipVal2=LPNulls.replaceNull(argValues[2]).toString();
                    ipVal3=LPNulls.replaceNull(argValues[3]).toString();
                    ipVal4=LPNulls.replaceNull(argValues[4]).toString();
                    description=LPNulls.replaceNull(argValues[5]).toString();
                    actionDiagnoses=AdminActions.updateWhiteIp(idInt, ipVal1, ipVal2, ipVal3, ipVal4, description);                               if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))    
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))    
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), idInt);
                    break;
                case REMOVE_WHITE_IP:
                    id=LPNulls.replaceNull(argValues[0]).toString();
                    actionDiagnoses=AdminActions.removeWhiteIp(Integer.valueOf(id));
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_WHITE_LIST.getTableName(), id);
                    break;
                case ACTIVATE_BLACK_IP:
                    id=LPNulls.replaceNull(argValues[0]).toString();
                    actionDiagnoses=AdminActions.activateBlackIp(Integer.valueOf(id));
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), id);
                    break;
                case DEACTIVATE_BLACK_IP:
                    id=LPNulls.replaceNull(argValues[0]).toString();
                    actionDiagnoses=AdminActions.deActivateBlackIp(Integer.valueOf(id));
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), id);
                    break;
                case REMOVE_BLACK_IP:
                    id=LPNulls.replaceNull(argValues[0]).toString();
                    actionDiagnoses=AdminActions.removeBlackIp(Integer.valueOf(id));
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))    
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), id);
                    break;
                case UPDATE_BLACK_IP:
                    idInt=Integer.valueOf(LPNulls.replaceNull(argValues[0]).toString());
                    ipVal1=LPNulls.replaceNull(argValues[1]).toString();
                    ipVal2=LPNulls.replaceNull(argValues[2]).toString();
                    ipVal3=LPNulls.replaceNull(argValues[3]).toString();
                    ipVal4=LPNulls.replaceNull(argValues[4]).toString();
                    description=LPNulls.replaceNull(argValues[5]).toString();
                    actionDiagnoses=AdminActions.updateBlackIp(idInt, ipVal1, ipVal2, ipVal3, ipVal4, description);                               if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))    
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), idInt);
                    break;
                case UPDATE_USER_SHIFT:
                    String newShift =LPNulls.replaceNull(argValues[0]).toString();
                    String userName =LPNulls.replaceNull(argValues[1]).toString();
                    actionDiagnoses=UserAndRolesViews.updateUserShift(newShift, userName);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()) && userName.length()>0)    
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), userName);
/*                    JSONObject jsonObj = new JSONObject();
                    rObj=RelatedObjects.getInstanceForActions();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), instanceForActions.getToken().getUserName());
                    jsonObj = new JSONObject();
                    jsonObj = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, new Object[0], rObj.getRelatedObject());                
                    jsonObj.put(AuthenticationAPIParams.RESPONSE_JSON_TAG_FINAL_TOKEN, instanceForActions.getToken());
                    rObj.killInstance();
                    LPFrontEnd.servletReturnSuccess(request, null, jsonObj);*/
                    break;

                default:
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, null, ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND, null);   
                    return;
            }     
        this.diagnostic=ApiMessageReturn.trapMessage(actionDiagnoses.getDiagnostic(),endPoint, actionDiagnoses.getMessageCodeVariables());
        this.relatedObj=rObj;
        this.actionDiagnosesObj=actionDiagnoses;
        //rObj.killInstance();
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
    public InternalMessage getDiagnosticObj() {
        return this.actionDiagnosesObj;
    }
    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
    }
    
}
