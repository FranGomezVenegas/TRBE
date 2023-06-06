/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.instrumentsmanagement.logic;

import module.instrumentsmanagement.apis.InstrumentsAPIqueries;
import module.instrumentsmanagement.definition.TblsInstrumentsConfig.TablesInstrumentsConfig;
import module.instrumentsmanagement.definition.TblsInstrumentsData.TablesInstrumentsData;
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
        jSummaryObj.put(TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableName(), InstrumentsAPIqueries.instrumentFamiliesList(alternativeProcInstanceName));
        jSummaryObj.put(TablesInstrumentsConfig.VARIABLES.getTableName(), InstrumentsAPIqueries.instrumentVariablesList(alternativeProcInstanceName));
        jSummaryObj.put(TablesInstrumentsConfig.VARIABLES_SET.getTableName(), InstrumentsAPIqueries.instrumentVariablesSetList(alternativeProcInstanceName));
        jSummaryObj.put(TablesInstrumentsData.INSTRUMENTS.getTableName(), InstrumentsAPIqueries.instrumentsList(alternativeProcInstanceName));
        return jSummaryObj;
    }
    
}
