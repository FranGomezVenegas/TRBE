/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.logic;

import com.labplanet.servicios.modulesample.ClassSampleQueries;
import databases.TblsCnfg;
import org.json.simple.JSONObject;
import trazit.enums.FrontendMasterData;

/**
 *
 * @author User
 */
public class InspLotRawMaterialMasterData implements FrontendMasterData{

    @Override
    public JSONObject getMasterDataJsonObject(String alternativeProcInstanceName) {
        JSONObject jSummaryObj=new JSONObject();        
        jSummaryObj.put(TblsCnfg.TablesConfig.METHODS.getTableName(), ClassSampleQueries.configMethodsList(alternativeProcInstanceName));
        jSummaryObj.put(TblsCnfg.TablesConfig.ANALYSIS_METHOD.getTableName(), ClassSampleQueries.configAnalysisList(alternativeProcInstanceName));
        return jSummaryObj;
    }
    
}
