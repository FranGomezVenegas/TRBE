/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.enums;

import functionaljavaa.responserelatedobjects.RelatedObjects;
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
}
