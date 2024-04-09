/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.session;

import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import modules.masterdata.analysis.ConfigAnalysisStructure;
import org.json.simple.JSONObject;
import trazit.enums.ActionsClass;
import trazit.enums.ActionsEndpointPair;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;

/**
 *
 * @author User
 */
public class ActionsServletCommons {

    public static void publishResult(HttpServletRequest request, HttpServletResponse response, ProcedureRequestSession procReqInstance, EnumIntEndpoints endPoint, Object[] diagnostic, InternalMessage diagnosticObj, Object[] messageDynamicData, RelatedObjects relatedObj) {
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        String lotName = argValues[0].toString();
        if (diagnosticObj != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosticObj.getDiagnostic())) {
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnosticObj.getMessageCodeObj(), diagnosticObj.getMessageCodeVariables());
            return;
        } else if (diagnosticObj == null && diagnostic != null && diagnostic.length > 0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) {
            LPFrontEnd.responseError(diagnostic);
            return;
        } else {
            JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, messageDynamicData, relatedObj.getRelatedObject());
            LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
            return;
        }
        /*
        if (diagnostic != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) {
        procReqInstance.killIt();
        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnosticObj.getMessageCodeObj(), diagnosticObj.getMessageCodeVariables());
        return;
        } else {
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsInvTrackingData.TablesInvTrackingData.LOT.getTableName(), lotName);
        JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, new Object[]{lotName}, rObj.getRelatedObject());
        rObj.killInstance();
        procReqInstance.killIt();
        LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
        return;
        }
         */
    }
    
    Boolean endpointFound;
    EnumIntEndpoints enumConstantByName;
    private InternalMessage diagnosticObj;
    private Object[] messageVariables;
    ActionsClass actionClass;
    private EnumIntMessages errorCodeObj;
    private StringBuilder rowArgsRows=new StringBuilder(0);
    private RelatedObjects relatedObj;
    public ActionsServletCommons(HttpServletRequest request, ActionsEndpointPair[] endpointsArr, String actionName){
        endpointFound=false;
        String className="";
        for (ActionsEndpointPair curActionsEndpointPair: endpointsArr){
            EnumIntEndpoints endpoint = getEnumConstantByName(curActionsEndpointPair.getEndpoint(), actionName);
            this.enumConstantByName=endpoint;
            if (endpoint!=null){
                try {
                    endpointFound=true;                    
                    Class<?> clazz = Class.forName(curActionsEndpointPair.getAction());    
                    className=clazz.getSimpleName();
                    Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endpoint.getArguments());
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
                        this.messageVariables = new Object[]{argValues[2].toString()};
                        return;
                    }                    
                    Constructor<?> constructor = clazz.getConstructor(HttpServletRequest.class, endpoint.getClass());
                    actionClass = (ActionsClass) constructor.newInstance(request, endpoint);
                    rowArgsRows=actionClass.getRowArgsRows();
                    relatedObj=actionClass.getRelatedObj();
                    return;
                } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    endpointFound=false;
                    Logger.getLogger(ActionsServletCommons.class.getName()).log(Level.SEVERE, null, ex);
                    this.errorCodeObj=LPPlatform.ApiErrorTraping.EXCEPTION_RAISED;
                    this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.EXCEPTION_RAISED, new Object[]{ex.getMessage()});
                    this.messageVariables=new Object[]{actionName+": "+className+"-"+ex.getMessage()};
                    rowArgsRows=null;
                    relatedObj=null;
                    return;
                }
            }
        }
        this.messageVariables=new Object[]{actionName};
        this.errorCodeObj=LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND;
        this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND, this.getMessageVariables());
    }
    
    public ActionsServletCommons(HttpServletRequest request, ActionsEndpointPair[] endpointsArr, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic){
        endpointFound=false;
        String className="";
        for (ActionsEndpointPair curActionsEndpointPair: endpointsArr){
            EnumIntEndpoints endpoint = getEnumConstantByName(curActionsEndpointPair.getEndpoint(), actionName);
            this.enumConstantByName=endpoint;
            if (endpoint!=null){
                try {
                    endpointFound=true;                    
                    Class<?> clazz = Class.forName(curActionsEndpointPair.getAction());    
                    className=clazz.getSimpleName();
                    /*Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endpoint.getArguments());
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
                        this.messageVariables = new Object[]{argValues[2].toString()};
                        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
                        this.relatedObj=rObj;
                        rObj.killInstance();
                        return;
                    }*/                    
                    Constructor<?> constructor = clazz.getConstructor(HttpServletRequest.class, String.class, Object[][].class, Integer.class, Integer.class, Integer.class);
                    actionClass = (ActionsClass) constructor.newInstance(request, actionName, testingContent, iLines, table1NumArgs, auditReasonPosic);
                    rowArgsRows=actionClass.getRowArgsRows();
                    this.diagnosticObj=actionClass.getDiagnosticObj();
                    relatedObj=actionClass.getRelatedObj();
                    return;
                } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    endpointFound=false;
                    Logger.getLogger(ActionsServletCommons.class.getName()).log(Level.SEVERE, null, ex);
                    this.errorCodeObj=LPPlatform.ApiErrorTraping.EXCEPTION_RAISED;
                    this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.EXCEPTION_RAISED, new Object[]{ex.getMessage()});
                    this.messageVariables=new Object[]{actionName+": "+className+"-"+ex.getMessage()};
                    rowArgsRows=null;
                    relatedObj=null;
                    return;
                }
            }
        }
        this.messageVariables=new Object[]{actionName};
        this.errorCodeObj=LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND;
        this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND, this.getMessageVariables());
    }

    public Boolean getEndpointFound(){        
        return endpointFound;
    }    
    
    public static <E extends Enum<E>> E getEnumConstantByName(String enumClassName, String constantName) {
    try {
        Class<E> enumClass = (Class<E>) Class.forName(enumClassName);
        return Enum.valueOf(enumClass, constantName);
    } catch (ClassNotFoundException | IllegalArgumentException e) {
        // Handle the case where the enum class doesn't exist        
        e.printStackTrace();
    }
        // Handle the case where the enum constant doesn't exist
    return null;
    }

    public EnumIntEndpoints getEndpointObj(){
        return enumConstantByName;
    }
    public ActionsClass getActionClassRun(){
        return actionClass;
    }

    /**
     * @return the diagnosticObj
     */
    public InternalMessage getDiagnosticObj() {
        return diagnosticObj;
    }

    /**
     * @return the messageVariables
     */
    public Object[] getMessageVariables() {
        return messageVariables;
    }

    /**
     * @return the errorCodeObj
     */
    public EnumIntMessages getErrorCodeObj() {
        return errorCodeObj;
    }
    public StringBuilder getRowArgsRows() {
        return rowArgsRows;
    }
    public RelatedObjects getRelatedObj() {
        return relatedObj;
    }
    
}
