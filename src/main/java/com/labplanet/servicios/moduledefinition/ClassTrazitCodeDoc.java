package com.labplanet.servicios.moduledefinition;

import com.labplanet.servicios.moduledefinition.ModuleDefinitionAPI.ModuleDefinitionAPIactionsEndpoints;
import functionaljavaa.platform.doc.AuditEventsToRequirements;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import functionaljavaa.platform.doc.ErrorMessageCodesToRequirements;
import functionaljavaa.platform.doc.BusinessRulesToRequirements;
import functionaljavaa.platform.doc.DevObjectsInModules;
import functionaljavaa.platform.doc.EndpointsWithNoJsonModel;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
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

    public ClassTrazitCodeDoc(HttpServletRequest request, HttpServletResponse response, ModuleDefinitionAPIactionsEndpoints endPoint){
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();

        Object[] actionDiagnoses = null;
        this.functionFound=true;
            switch (endPoint){
                case DOC_API_ENDPOINTS_IN_DB:         
                    EndPointsToRequirements endpointToReq=new EndPointsToRequirements(request, response);
                    LPFrontEnd.servletReturnSuccess(request, response, endpointToReq.getSummaryInfo());
                    break;
                case DOC_API_ERROR_MESSAGE_CODES_IN_DB:
                    ErrorMessageCodesToRequirements msgToReq=new ErrorMessageCodesToRequirements(request, response);
                    LPFrontEnd.servletReturnSuccess(request, response, msgToReq.getSummaryInfo());
                    break;
                case DOC_API_BUSINESS_RULES_IN_DB:
                    BusinessRulesToRequirements busRulToReq=new BusinessRulesToRequirements(request, response);
                    LPFrontEnd.servletReturnSuccess(request, response, busRulToReq.getSummaryInfo());
                    break;
                case DOC_API_AUDIT_EVENTS_IN_DB:
                    AuditEventsToRequirements auditEvToReq=new AuditEventsToRequirements(request, response);
                    LPFrontEnd.servletReturnSuccess(request, response, auditEvToReq.getSummaryInfo());
                    break;
                case DOC_OBJECTS_NOT_IN_MODULES:
                    DevObjectsInModules objsInModules=new DevObjectsInModules(request, response);
                    LPFrontEnd.servletReturnSuccess(request, response, objsInModules.getSummaryInfo());
                    break;      
                case DOC_ENDPOINTS_WITH_NO_JSON_MODEL:
                    EndpointsWithNoJsonModel endpointsNoJsonModel=new EndpointsWithNoJsonModel(request, response);
                    LPFrontEnd.servletReturnSuccess(request, response, endpointsNoJsonModel.getSummaryInfo());
                    break;
                    
                case DOC_API_ALL_IN_ONE:
                    JSONObject mainObj=new JSONObject();
                    request.setAttribute("summaryOnly", true);
                    auditEvToReq=new AuditEventsToRequirements(request, response);
                    mainObj.put("audit_events_summary", auditEvToReq.getSummaryInfo());

                    busRulToReq=new BusinessRulesToRequirements(request, response);
                    mainObj.put("business_rules_summary", busRulToReq.getSummaryInfo());

                    msgToReq=new ErrorMessageCodesToRequirements(request, response);
                    mainObj.put("error_msgcodes_summary", msgToReq.getSummaryInfo());
                    
                    endpointToReq=new EndPointsToRequirements(request, response);
                    mainObj.put("endpoints_summary", endpointToReq.getSummaryInfo());

                    LPFrontEnd.servletReturnSuccess(request, response, mainObj);
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
