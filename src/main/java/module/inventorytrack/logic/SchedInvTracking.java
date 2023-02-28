/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inventorytrack.logic;

import databases.features.Token;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class SchedInvTracking {
    public static void InvTrackingSchedProcesses(Token token, String procInstanceName){
        String moduleNameFromProcInstance = token.getModuleNameFromProcInstance(procInstanceName);
        if (!GlobalVariables.TrazitModules.INVENTORY_TRACKING.name().equalsIgnoreCase(moduleNameFromProcInstance)) return;
        return;
    }    
}
