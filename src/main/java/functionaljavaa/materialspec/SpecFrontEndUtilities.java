/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.materialspec;

import databases.Rdbms;
import databases.TblsCnfg;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class SpecFrontEndUtilities {
    private SpecFrontEndUtilities() {    throw new IllegalStateException("Utility class");  }
    /**
     *
     * @param code
     * @param configVersion
     * @param fieldsName
     * @param sortFields
     * @return
     */
    public static JSONObject configSpecInfo(String code, Integer configVersion, String[] fieldsName, String[] sortFields){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    if (fieldsName==null || fieldsName.length==0){
    for (TblsCnfg.Spec obj: TblsCnfg.Spec.values()){
        fieldsName=TblsCnfg.Spec.getAllFieldNames();
      }      
    }
    Object[][] records=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_CONFIG), TblsCnfg.Spec.TBL.getName(), 
            new String[]{TblsCnfg.Spec.FLD_CODE.getName(), TblsCnfg.Spec.FLD_CONFIG_VERSION.getName()}, 
            new Object[]{code, configVersion}, 
            fieldsName, sortFields);
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(records[0][0].toString()))
        return new JSONObject();
    JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsName, records[0]);
    Integer posicInArr=LPArray.valuePosicInArray(fieldsName, TblsCnfg.Spec.FLD_ANALYSES.getName());
    if (posicInArr>-1){
      String[] strToArr=records[0][posicInArr].toString().split("\\|");
        jObj.put("analysis_list", LPJson.convertToJSON(strToArr));
    }
    posicInArr=LPArray.valuePosicInArray(fieldsName, TblsCnfg.Spec.FLD_VARIATION_NAMES.getName());
    if (posicInArr>-1){
      String[] strToArr=records[0][posicInArr].toString().split("\\|");
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
    public static JSONArray configSpecLimitsInfo(String code, Integer configVersion, String[] fieldsName, String[] sortFields){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    ConfigSpecRule specRule = new ConfigSpecRule();
    if (fieldsName==null || fieldsName.length==0){
      for (TblsCnfg.SpecLimits obj: TblsCnfg.SpecLimits.values()){
          String objName = obj.name();
          if (!"TBL".equalsIgnoreCase(objName))
            fieldsName=LPArray.addValueToArray1D(fieldsName, obj.getName());
      }      
    }
    Object[][] records=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_CONFIG), TblsCnfg.SpecLimits.TBL.getName(), 
            new String[]{TblsCnfg.SpecLimits.FLD_CODE.getName(), TblsCnfg.SpecLimits.FLD_CONFIG_VERSION.getName()}, 
            new Object[]{code, configVersion}, 
            fieldsName, sortFields);
    JSONArray jArr = new JSONArray();
    for (Object[] curRec: records){
      Integer posicInArr=LPArray.valuePosicInArray(fieldsName, TblsCnfg.SpecLimits.FLD_LIMIT_ID.getName());
      if (posicInArr>-1){
        Integer limitId = (Integer) curRec[posicInArr];
        specRule.specLimitsRule(limitId, null);
        if (LPArray.valuePosicInArray(fieldsName, ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_DETAILED)==-1)
          fieldsName=LPArray.addValueToArray1D(fieldsName,ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_DETAILED);
        curRec=LPArray.addValueToArray1D(curRec, specRule.getRuleRepresentation());
      }    
      jArr.add(LPJson.convertArrayRowToJSONObject(fieldsName, curRec));
    }
    return jArr;
  }
  
}
