/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app.platformadmin;

import databases.TblsApp;
import databases.TblsAppProcData.TablesAppProcData;
import functionaljavaa.platformadmin.AdminActions;
import functionaljavaa.platformadmin.PlatformAdminEnums.PlatformAdminAPIActionsEndpoints;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.ApiErrorTraping;
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
    private Boolean functionFound=false;

    public ClassPlatformAdmin(HttpServletRequest request, PlatformAdminAPIActionsEndpoints endPoint){
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        InternalMessage actionDiagnoses = null;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());        
        this.functionFound=true;
            switch (endPoint){
                case ADD_WHITE_IP:
                    String ipVal1=LPNulls.replaceNull(argValues[0].toString());
                    String ipVal2=LPNulls.replaceNull(argValues[1].toString());
                    String ipVal3=LPNulls.replaceNull(argValues[2].toString());
                    String ipVal4=LPNulls.replaceNull(argValues[3].toString());
                    String description=LPNulls.replaceNull(argValues[4].toString());
                    String[] fieldNames=new String[]{TblsApp.IPWhiteList.ACTIVE.getName()};
                    Object[] fieldValues=new Object[]{true};
                    InternalMessage addWhiteIp = AdminActions.addWhiteIp(LPNulls.replaceNull(argValues[0].toString()), LPNulls.replaceNull(argValues[1].toString()), 
                        LPNulls.replaceNull(argValues[2].toString()), LPNulls.replaceNull(argValues[3].toString()), LPNulls.replaceNull(argValues[4].toString()));
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))                        
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(),TablesAppProcData.INSTRUMENTS.getTableName(), null);                
                    break;

                default:
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, null, ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND, null);   
                    return;
            }     
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()))
            this.diagnostic=ApiMessageReturn.trapMessage(actionDiagnoses.getDiagnostic(),actionDiagnoses.getMessageCodeObj(), actionDiagnoses.getMessageCodeVariables());
        else
            this.diagnostic=ApiMessageReturn.trapMessage(actionDiagnoses.getDiagnostic(),endPoint, actionDiagnoses.getMessageCodeVariables());
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
