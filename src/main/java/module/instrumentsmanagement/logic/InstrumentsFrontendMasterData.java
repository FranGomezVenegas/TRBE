/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.instrumentsmanagement.logic;

import module.instrumentsmanagement.apis.InstrumentsAPIqueries;
import module.instrumentsmanagement.definition.TblsInstrumentsConfig.TablesInstrumentsConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.FrontendMasterData;

/**
 *
 * @author User
 */
public class InstrumentsFrontendMasterData implements FrontendMasterData{

    @Override
    public JSONObject getMasterDataJsonObject(String alternativeProcInstanceName) {
        JSONObject jSummaryObj=new JSONObject();        
        JSONArray instrumentFamiliesList = InstrumentsAPIqueries.instrumentFamiliesList(alternativeProcInstanceName);
        jSummaryObj.put(TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableName(), instrumentFamiliesList);
        return jSummaryObj;
    }
    
}
