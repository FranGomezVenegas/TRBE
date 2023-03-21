/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.enums;

import org.json.simple.JSONArray;

public interface EnumIntQueriesEndpoints {
    Boolean getFunctionFound(); 
    JSONArray getFunctionRelatedObjects();
    Object getFunctionDiagn();   
    StringBuilder getRowArgsRows();
    Boolean getIsSuccess(); 
    EnumIntQueriesObj getQueryRunObj();
}
