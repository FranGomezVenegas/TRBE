/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.enums;

import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public interface ActionsClass {
    public Object[] getDiagnostic();
    public InternalMessage getDiagnosticObj();
    public RelatedObjects getRelatedObj();
    public Object[] getMessageDynamicData();
    public StringBuilder getRowArgsRows();    
    public EnumIntEndpoints getEndpointObj();
    public HttpServletResponse getHttpResponse();
    
    public void initializeEndpoint(String actionName);
    
    public void createClassEnvMonAndHandleExceptions(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic);
}
