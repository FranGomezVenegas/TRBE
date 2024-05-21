/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.projectrnd.logic;

import com.labplanet.servicios.modulesample.ClassSampleQueries;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsCnfg;
import module.projectrnd.definition.TblsProjectRnDConfig;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.enums.FrontendMasterData;
import trazit.queries.QueryUtilities;

/**
 *
 * @author User
 */
public class ProjectRnDMasterData implements FrontendMasterData{

    @Override
    public JSONObject getMasterDataJsonObject(String alternativeProcInstanceName) {
        JSONObject jSummaryObj=new JSONObject();        
        jSummaryObj.put(TblsCnfg.TablesConfig.METHODS.getTableName(), ClassSampleQueries.configMethodsList(alternativeProcInstanceName));
        jSummaryObj.put(TblsCnfg.TablesConfig.ANALYSIS_METHOD.getTableName(), ClassSampleQueries.configAnalysisList(alternativeProcInstanceName));

        SqlWhere wObj=new SqlWhere();
        wObj.addConstraint(TblsProjectRnDConfig.Ingredients.ACTIVE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{true}, null);
        
        jSummaryObj.put(TblsProjectRnDConfig.TablesProjectRnDConfig.INGREDIENTS.getTableName(),
                QueryUtilities.dbRowsToJsonArr(alternativeProcInstanceName, alternativeProcInstanceName, TblsProjectRnDConfig.TablesProjectRnDConfig.INGREDIENTS, 
                EnumIntTableFields.getTableFieldsFromString(TblsProjectRnDConfig.TablesProjectRnDConfig.INGREDIENTS, "ALL"), 
                wObj, new String[]{TblsProjectRnDConfig.Ingredients.NAME.getName()}, null, true));

        return jSummaryObj;
    }
    
}
