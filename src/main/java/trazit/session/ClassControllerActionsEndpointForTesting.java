/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.session;

import org.json.JSONArray;

/**
 *
 * @author User
 */
public interface ClassControllerActionsEndpointForTesting {
    
    public Boolean getFunctionFound();
    public StringBuilder getRowArgsRows();
    public Object getFunctionDiagn();
    public JSONArray getFunctionRelatedObjects();
    
}
