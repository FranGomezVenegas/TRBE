
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.clinicalstudies.logic;

import org.json.simple.JSONObject;
import trazit.enums.FrontendMasterData;

/**
 *
 * @author User
 */
public class ClinicalStudiesFrontendMasterData implements FrontendMasterData{

    @Override
    public JSONObject getMasterDataJsonObject(String alternativeProcInstanceName) {
        JSONObject jSummaryObj=new JSONObject();        
        //jSummaryObj.put(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableName(), InstrumentsAPIqueries.instrumentFamiliesList(alternativeProcInstanceName));
        //jSummaryObj.put(TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES.getTableName(), InstrumentsAPIqueries.instrumentVariablesList(alternativeProcInstanceName));
        //jSummaryObj.put(TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES_SET.getTableName(), InstrumentsAPIqueries.instrumentVariablesSetList(alternativeProcInstanceName));
        //jSummaryObj.put(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS.getTableName(), InstrumentsAPIqueries.instrumentsList(alternativeProcInstanceName));
        return jSummaryObj;
    }
    
}
    

