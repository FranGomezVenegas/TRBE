/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.session;

import databases.features.Token;
import module.instrumentsmanagement.logic.SchedInstruments;
import static trazit.session.ProcReqSessionAutomatisms.markAsExpiredTheExpiredObjects;

/**
 *
 * @author User
 */
public class SchedProcedures {
    
    public static void schedProcesses(Token token, String procedureInstance){
        markAsExpiredTheExpiredObjects(procedureInstance);
        if (token!=null&&procedureInstance!=null)
            SchedInstruments.InstrumentsSchedProcesses(token, procedureInstance);
        
    }
    
}
