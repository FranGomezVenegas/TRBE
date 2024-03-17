
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.clinicalstudies.logic;

import module.clinicalstudies.apis.ClinicalStudyAPIqueries;
import module.clinicalstudies.definition.TblsGenomaConfig;
import org.json.simple.JSONObject;
import trazit.enums.FrontendMasterData;

public class ClinicalStudiesFrontendMasterData implements FrontendMasterData{

    @Override
    public JSONObject getMasterDataJsonObject(String alternativeProcInstanceName) {
        JSONObject jSummaryObj=new JSONObject();        
        jSummaryObj.put(TblsGenomaConfig.TablesGenomaConfig.VARIABLES.getTableName(), ClinicalStudyAPIqueries.variablesList(alternativeProcInstanceName));
        jSummaryObj.put(TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET.getTableName(), ClinicalStudyAPIqueries.variableSetList(alternativeProcInstanceName));
        return jSummaryObj;
    }
    
}
    

