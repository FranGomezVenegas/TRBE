/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.instrumentsmanagement.logic;

import lbplanet.utilities.LPPlatform;
import module.instrumentsmanagement.definition.InstrumentsEnums.InstrEventsErrorTrapping;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public class ConfigInstrumentsFamily {

public static InternalMessage configUpdateInstrumentFamily(String instr, String[] fieldNames, Object[] fieldValues){
    return new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.EVENT_NOTHING_PENDING, null, null);
}
    
}
