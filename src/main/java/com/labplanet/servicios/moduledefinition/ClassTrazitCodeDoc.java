package com.labplanet.servicios.moduledefinition;

import com.labplanet.servicios.moduledefinition.ModuleDefinitionAPI.ModuleDefinitionAPIEndpoints;
import functionaljavaa.platform.doc.AuditEventsToRequirements;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import functionaljavaa.platform.doc.ErrorMessageCodesToRequirements;
import functionaljavaa.platform.doc.BusinessRulesToRequirements;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPPlatform;
import trazit.session.ApiMessageReturn;

/**
 *
 * @author User
 */
public class ClassTrazitCodeDoc {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;

    public ClassTrazitCodeDoc(HttpServletRequest request, HttpServletResponse response, ModuleDefinitionAPIEndpoints endPoint){
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();

        Object[] actionDiagnoses = null;
        this.functionFound=true;
            switch (endPoint){
                case DOC_API_ENDPOINTS_IN_DB:         
                    EndPointsToRequirements eToReq=new EndPointsToRequirements(request, response);
                    break;
                case DOC_API_ERROR_MESSAGE_CODES_IN_DB:
                    ErrorMessageCodesToRequirements msgToReq=new ErrorMessageCodesToRequirements(request, response);
                    break;
                case DOC_API_BUSINESS_RULES_IN_DB:
                    new BusinessRulesToRequirements(request, response);
                    break;
                case DOC_API_AUDIT_EVENTS_IN_DB:
                    AuditEventsToRequirements evToReq=new AuditEventsToRequirements(request, response);
                    break;

            }    
        if (actionDiagnoses!=null)
            this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{actionDiagnoses[0]});
        else
            this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, null);
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
