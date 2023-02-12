/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inventorytrack.logic;

import databases.SqlStatement;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import module.inventorytrack.definition.TblsInvTrackingConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.FrontendMasterData;
import trazit.queries.QueryUtilitiesEnums;

/**
 *
 * @author User
 */
public class InvTrackingFrontendMasterData implements FrontendMasterData{

    @Override
    public JSONObject getMasterDataJsonObject(String alternativeProcInstanceName) {
        JSONObject jSummaryObj=new JSONObject();
        
        String[] fieldsToRetrieve = getAllFieldNames(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_CATEGORY, alternativeProcInstanceName);
        if (!LPArray.valueInArray(fieldsToRetrieve, TblsInvTrackingConfig.Category.NAME.getName()))
            fieldsToRetrieve=LPArray.addValueToArray1D(fieldsToRetrieve, TblsInvTrackingConfig.Category.NAME.getName());
        Object[][] categoryInfo = QueryUtilitiesEnums.getTableData(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_CATEGORY,
                EnumIntTableFields.getAllFieldNamesFromDatabase(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_CATEGORY, alternativeProcInstanceName),
                new String[]{TblsInvTrackingConfig.Category.NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{},
                new String[]{TblsInvTrackingConfig.Category.NAME.getName()}, alternativeProcInstanceName);
        JSONArray jSummaryArr = new JSONArray();
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(categoryInfo[0][0].toString())){
            for (Object[] currCategory: categoryInfo){
                JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currCategory);
                
                String[] fieldsToRetrieveLvl2 = getAllFieldNames(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE, alternativeProcInstanceName);
                String curCategory=currCategory[LPArray.valuePosicInArray(fieldsToRetrieve, TblsInvTrackingConfig.Category.NAME.getName())].toString();
                Object[][] catReferencesInfo=QueryUtilitiesEnums.getTableData(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE,
                    EnumIntTableFields.getAllFieldNamesFromDatabase(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE, alternativeProcInstanceName),
                    new String[]{TblsInvTrackingConfig.Reference.CATEGORY.getName()}, new Object[]{curCategory}, 
                    new String[]{TblsInvTrackingConfig.Reference.NAME.getName()}, alternativeProcInstanceName);
                JSONArray jArrLvl2 = new JSONArray();
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(catReferencesInfo[0][0].toString())){
                    //Object[] childJObj=new Object[]{null, null, "No child", "", "", "", null, "", "", null, null};
                    //for (int iChild=childJObj.length;iChild<fieldsToRetrieve.length;iChild++)
                    //    childJObj=LPArray.addValueToArray1D(childJObj, "");                            
                    //JSONObject jObjLvl2=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, childJObj); 
                    JSONObject jObjLvl2=new JSONObject();
                    jArrLvl2.add(jObjLvl2);
                }else{
                    for (Object[] curRowLvl2: catReferencesInfo){
                        JSONObject jObjLvl2=LPJson.convertArrayRowToJSONObject(fieldsToRetrieveLvl2, curRowLvl2);  
                        jArrLvl2.add(jObjLvl2);
                    }
                }
                jObj.put(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableName(), jArrLvl2);
                jSummaryArr.add(jObj);
            }
        }                
        jSummaryObj.put("category_and_references", jSummaryArr);
        return jSummaryObj;
    }
    
}
