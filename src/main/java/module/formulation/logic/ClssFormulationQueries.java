/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package module.formulation.logic;

import databases.SqlStatement;
import databases.SqlStatementEnums;
import databases.SqlWhere;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import module.formulation.definition.TblsFormulationData;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.queries.QueryUtilitiesEnums;

/**
 *
 * @author User
 */
public class ClssFormulationQueries {
    public static JSONArray getFormulas(SqlWhere sW, Boolean includeIngredients){
        String[] fieldsToRetrieve = getAllFieldNames(TblsFormulationData.TablesFormulationData.FORMULA);
        Object[][] formulasInfo = QueryUtilitiesEnums.getTableData(TblsFormulationData.TablesFormulationData.FORMULA,
                EnumIntTableFields.getAllFieldNamesFromDatabase(TblsFormulationData.TablesFormulationData.FORMULA),
                sW, new String[]{TblsFormulationData.Formula.NAME.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
        JSONArray jArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(formulasInfo[0][0].toString()))) {
            for (Object[] curFormula : formulasInfo) {
                JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curFormula);
                String curProjName=curFormula[LPArray.valuePosicInArray(fieldsToRetrieve, TblsFormulationData.Formula.NAME.getName())].toString();
                if (includeIngredients){
                    jObj.put(TblsFormulationData.TablesFormulationData.FORMULA_INGREDIENTS.getTableName(), getFormulaIngredients(curProjName));
                }
                JSONObject formulaLockingDetail = formulaLockingInfo(fieldsToRetrieve, curFormula);
                if (Boolean.FALSE.equals(formulaLockingDetail.isEmpty())) {
                    jObj.put("locking_reason", formulaLockingDetail);
                }
                jArr.put(jObj);
            }
        }
        return jArr;
    }

    public static JSONArray getFormulaIngredients(String formulaName){
        String[] fieldsToRetrieve = getAllFieldNames(TblsFormulationData.TablesFormulationData.FORMULA_INGREDIENTS);
        SqlWhere sW = new SqlWhere();
        sW.addConstraint(TblsFormulationData.FormulaIngredients.FORMULA, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{formulaName}, null);

        Object[][] formulasInfo = QueryUtilitiesEnums.getTableData(TblsFormulationData.TablesFormulationData.FORMULA_INGREDIENTS,
                EnumIntTableFields.getAllFieldNamesFromDatabase(TblsFormulationData.TablesFormulationData.FORMULA_INGREDIENTS),
                sW, new String[]{TblsFormulationData.FormulaIngredients.ORDER_NUMBER.getName(), TblsFormulationData.FormulaIngredients.INGREDIENT.getName()});
        JSONArray jArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(formulasInfo[0][0].toString()))) {
            for (Object[] currInstr : formulasInfo) {
                JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstr);
                JSONObject formulaLockingDetail = formulaLockingInfo(fieldsToRetrieve, currInstr);
                if (Boolean.FALSE.equals(formulaLockingDetail.isEmpty())) {
                    jObj.put("locking_reason", formulaLockingDetail);
                }
                jArr.put(jObj);
            }
        }
        return jArr;
    }
    
    public static JSONObject formulaLockingInfo(String[] fieldsToRetrieve, Object[] currInstr) {
        JSONObject jObj = new JSONObject();

        return jObj;
        /*Integer fldPosic = LPArray.valuePosicInArray(fieldsToRetrieve, TblsFormulationData.Lot.IS_LOCKED.getName());
        if (fldPosic == -1) {
            return jObj;
        }
        if (Boolean.FALSE.equals(Boolean.TRUE.equals(Boolean.valueOf(LPNulls.replaceNull(currInstr[fldPosic]).toString())))) {
            return jObj;
        }
        fldPosic = LPArray.valuePosicInArray(fieldsToRetrieve, TblsFormulationData.Lot.LOCKED_REASON.getName());
        if (fldPosic == -1) {
            jObj.put(GlobalAPIsParams.LBL_MESSAGE_EN, "Locked");
            jObj.put(GlobalAPIsParams.LBL_MESSAGE_ES, "Bloqueado");
            return jObj;
        }
        String errorTextEn = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_API_SUCCESSMESSAGE + "InstrumentsAPIactionsEndpoints", null, LPNulls.replaceNull(currInstr[fldPosic]).toString(), DEFAULTLANGUAGE, null, true, "InstrumentsAPIactionsEndpoints");
        if (errorTextEn.length() == 0) {
            errorTextEn = LPNulls.replaceNull(currInstr[fldPosic]).toString();
        }
        String errorTextEs = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_API_SUCCESSMESSAGE + "InstrumentsAPIactionsEndpoints", null, LPNulls.replaceNull(currInstr[fldPosic]).toString(), "es", null, false, "InstrumentsAPIactionsEndpoints");
        if (errorTextEs.length() == 0) {
            errorTextEs = LPNulls.replaceNull(currInstr[fldPosic]).toString();
        }
        jObj.put(GlobalAPIsParams.LBL_MESSAGE_EN, errorTextEn);
        jObj.put(GlobalAPIsParams.LBL_MESSAGE_ES, errorTextEs);
        return jObj;*/
    }

    
}
