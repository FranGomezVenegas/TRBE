/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.enums;

/**
 *
 * @author User
 */
public interface EnumIntViews {
    String getRepositoryName(); 
    Boolean getIsProcedureInstance();
    String getViewCreatecript();
    String getViewName();   
    EnumIntViewFields[] getViewFields();
    String getViewComment();
    FldBusinessRules[] getTblBusinessRules();
}
