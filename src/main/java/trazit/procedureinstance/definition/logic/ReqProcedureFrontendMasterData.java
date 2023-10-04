/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.definition.logic;

import databases.Rdbms;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.enums.FrontendMasterData;
import trazit.procedureinstance.definition.definition.TblsReqs;

/**
 *
 * @author User
 */
public class ReqProcedureFrontendMasterData implements FrontendMasterData{

    @Override
    public JSONObject getMasterDataJsonObject(String alternativeProcInstanceName) {
        JSONObject jSummaryObj=new JSONObject();        
        jSummaryObj.put(TblsReqs.TablesReqs.MODULES.getTableName(), getActiveModules(alternativeProcInstanceName));
        return jSummaryObj;
    }
    
    public static JSONArray getActiveModules(String procInstanceName){
        String[] fieldsToGet = EnumIntTableFields.getAllFieldNames(TblsReqs.TablesReqs.MODULES.getTableFields());
        Object[][] procAndInstanceArr = Rdbms.getRecordFieldsByFilter( "", 
                TblsReqs.TablesReqs.MODULES.getRepositoryName(), TblsReqs.TablesReqs.MODULES.getTableName(),
                new String[]{TblsReqs.Modules.ACTIVE.getName()}, 
                new Object[]{true},
                fieldsToGet, new String[]{TblsReqs.Modules.MODULE_NAME.getName()});
        JSONArray proceduresList = new JSONArray();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procAndInstanceArr[0][0].toString())) {
            return proceduresList;
        } else {
            for (Object[] curProc : procAndInstanceArr) {
                JSONObject curProcObj = LPJson.convertArrayRowToJSONObject(fieldsToGet, curProc);
                proceduresList.add(curProcObj);                            
            }
            return proceduresList;

        }
    }
    
}
