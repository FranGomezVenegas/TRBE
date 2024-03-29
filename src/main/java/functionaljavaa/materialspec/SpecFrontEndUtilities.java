/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.materialspec;

import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsCnfg;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntTables;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class SpecFrontEndUtilities {

    private SpecFrontEndUtilities() {
        throw new IllegalStateException("Utility class");
    }

    /**
     *
     * @param code
     * @param configVersion
     * @param fieldsName
     * @param sortFields
     * @return
     */
    public static JSONObject configSpecInfo(String code, EnumIntTables tblObj, Integer configVersion, String[] fieldsName, String[] sortFields) {
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        return configSpecInfo(procReqInstance, tblObj, code, configVersion, fieldsName, sortFields);
    }

    public static JSONObject configSpecInfo(ProcedureRequestSession procReqInstance, EnumIntTables tblObj, String code, Integer configVersion, String[] fieldsName, String[] sortFields) {
        String procInstanceName = procReqInstance.getProcedureInstance();
        if (procInstanceName == null) {
            return new JSONObject();
        }
        if (fieldsName == null || fieldsName.length == 0) {
            for (TblsCnfg.Spec obj : TblsCnfg.Spec.values()) {
                fieldsName = getAllFieldNames(tblObj.getTableFields());
            }
        }
        Object[][] records = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), tblObj.getTableName(),
                new String[]{TblsCnfg.Spec.CODE.getName(), TblsCnfg.Spec.CONFIG_VERSION.getName()},
                new Object[]{code, configVersion},
                fieldsName, sortFields);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(records[0][0].toString())) {
            return new JSONObject();
        }
        JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsName, records[0]);
        Integer posicInArr = LPArray.valuePosicInArray(fieldsName, TblsCnfg.Spec.ANALYSES.getName());
        if (posicInArr > -1) {
            String[] strToArr = records[0][posicInArr].toString().split("\\|");
            jObj.put("analysis_list", LPJson.convertToJSON(strToArr));
        }
        posicInArr = LPArray.valuePosicInArray(fieldsName, TblsCnfg.Spec.VARIATION_NAMES.getName());
        if (posicInArr > -1) {
            String[] strToArr = records[0][posicInArr].toString().split("\\|");
            jObj.put("variation_names_list", LPJson.convertToJSON(strToArr));
        }
        return jObj;
    }

    /**
     *
     * @param code
     * @param configVersion
     * @param fieldsName
     * @param sortFields
     * @return
     */
    public static JSONArray configSpecLimitsInfo(String code, EnumIntTables tblObj, Integer configVersion, String specAnalysisName, String[] fieldsName, String[] sortFields) {
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        return configSpecLimitsInfo(procReqInstance, tblObj, code, configVersion, specAnalysisName, fieldsName, sortFields);
    }

    public static JSONArray configSpecLimitsInfo(ProcedureRequestSession procReqInstance, EnumIntTables tblObj,
            String code, Integer configVersion, String specAnaVariationName,String[] fieldsName, String[] sortFields) {
        String procInstanceName = procReqInstance.getProcedureInstance();
        ConfigSpecRule specRule = new ConfigSpecRule();
        if (fieldsName == null || fieldsName.length == 0) {
            for (TblsCnfg.SpecLimits obj : TblsCnfg.SpecLimits.values()) {
                String objName = obj.name();
                if (Boolean.FALSE.equals("TBL".equalsIgnoreCase(objName))) {
                    fieldsName = LPArray.addValueToArray1D(fieldsName, obj.getName());
                }
            }
        }
        String[] wFlds = new String[]{TblsCnfg.SpecLimits.CODE.getName(), TblsCnfg.SpecLimits.CONFIG_VERSION.getName()};
        Object[] wValues = new Object[]{code, configVersion};
        SqlWhere sW = new SqlWhere(tblObj, wFlds, wValues);
        if (LPNulls.replaceNull(specAnaVariationName).toString().length() > 0 && Boolean.FALSE.equals("ALL".equalsIgnoreCase(LPNulls.replaceNull(specAnaVariationName).toString()))) {
            sW.addConstraint(TblsCnfg.SpecLimits.VARIATION_NAME, SqlStatement.WHERECLAUSE_TYPES.IN, specAnaVariationName.split("\\|"), null);
        }
        EnumIntTableFields[] tableFieldsFromString = EnumIntTableFields.getTableFieldsFromString(tblObj, fieldsName);
        Object[][] records = Rdbms.getRecordFieldsByFilter(procInstanceName, procInstanceName, TblsCnfg.TablesConfig.SPEC_LIMITS, sW,
                tableFieldsFromString, sortFields, Boolean.FALSE);
        fieldsName=EnumIntTableFields.getAllFieldNames(tableFieldsFromString);
        JSONArray jArr = new JSONArray();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(records[0][0].toString())){
            return jArr;
        }
        for (Object[] curRec : records) {
            Integer posicInArr = LPArray.valuePosicInArray(fieldsName, TblsCnfg.SpecLimits.LIMIT_ID.getName());
            if (posicInArr > -1) {
                Integer limitId = Integer.valueOf(curRec[posicInArr].toString());
                specRule.specLimitsRule(limitId, null);
                if (LPArray.valuePosicInArray(fieldsName, ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_DETAILED) == -1) {
                    fieldsName = LPArray.addValueToArray1D(fieldsName, ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_DETAILED);
                }
                curRec = LPArray.addValueToArray1D(curRec, specRule.getRuleRepresentation());
            }
            jArr.add(LPJson.convertArrayRowToJSONObject(fieldsName, curRec));
        }
        return jArr;
    }

}
