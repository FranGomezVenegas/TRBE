/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleinspectionlotrm;

import databases.TblsData;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import static trazit.queries.QueryUtilities.getFieldsListToRetrieve;
import static trazit.queries.QueryUtilities.getTableData;

/**
 *
 * @author User
 */
class InspLotQueries {
    private InspLotQueries() {throw new IllegalStateException("Utility class");}

    public static JSONArray configMaterialStructure(String name, String filterFieldsToRetrieve, String[] orderBy,
            Boolean includeMatCertificate, Boolean includeMatInventoryPlan, Boolean includeMatSamplingPlan){
        String[] whereFldName=new String[]{TblsInspLotRMConfig.Material.FLD_NAME.getName()};
        Object[] whereFldValue=new Object[]{name};
        Object[][] materialInfo=getTableData(GlobalVariables.Schemas.CONFIG.getName(), TblsInspLotRMConfig.Material.TBL.getName(), 
                    filterFieldsToRetrieve, TblsInspLotRMConfig.Material.getAllFieldNames(), whereFldName, whereFldValue, orderBy);        
        JSONArray jArr = new JSONArray();
        String[] fieldsToRetrieve=getFieldsListToRetrieve(filterFieldsToRetrieve, TblsInspLotRMConfig.Material.getAllFieldNames());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) return jArr;
        for (Object[] currRec: materialInfo){
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currRec);
            if (includeMatCertificate==null || includeMatCertificate) jObj.put(TblsInspLotRMConfig.MaterialCertificate.TBL.getName(), configMaterialCertificateStructure(name, null, null, new String[]{}));
            if (includeMatInventoryPlan==null || includeMatInventoryPlan) jObj.put(TblsInspLotRMConfig.MaterialInventoryPlan.TBL.getName(), configMaterialInventoryPlanStructure(name, null, null, new String[]{}));
            if (includeMatSamplingPlan==null || includeMatSamplingPlan) jObj.put(TblsInspLotRMConfig.MaterialSamplingPlan.TBL.getName(), configMaterialSamplingPlanStructure(name, null, null, new String[]{}));
            jArr.add(jObj);
        }
        return jArr;
    }
    public static JSONArray configMaterialCertificateStructure(String name, String configName, String filterFieldsToRetrieve, String[] orderBy){
        String[] whereFldName=new String[]{TblsInspLotRMConfig.MaterialCertificate.FLD_MATERIAL.getName()};
        Object[] whereFldValue=new Object[]{name};
        if (configName!=null){
            whereFldName=LPArray.addValueToArray1D(whereFldName, TblsInspLotRMConfig.MaterialCertificate.FLD_CONFIG_NAME.getName());
            whereFldValue=LPArray.addValueToArray1D(whereFldValue, configName);
        }
        Object[][] matCertifInfo=getTableData(GlobalVariables.Schemas.CONFIG.getName(), TblsInspLotRMConfig.MaterialCertificate.TBL.getName(), 
                    filterFieldsToRetrieve, TblsInspLotRMConfig.MaterialCertificate.getAllFieldNames(), whereFldName, whereFldValue, orderBy);        
        JSONArray jArr = new JSONArray();
        String[] fieldsToRetrieve=getFieldsListToRetrieve(filterFieldsToRetrieve, TblsInspLotRMConfig.MaterialCertificate.getAllFieldNames());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(matCertifInfo[0][0].toString())) return jArr;
        for (Object[] currMatCertif: matCertifInfo){
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currMatCertif);
            jArr.add(jObj);
        }
        return jArr;
    }
    public static JSONArray configMaterialInventoryPlanStructure(String name, String entryName, String filterFieldsToRetrieve, String[] orderBy){
        String[] whereFldName=new String[]{TblsInspLotRMConfig.MaterialInventoryPlan.FLD_MATERIAL.getName()};
        Object[] whereFldValue=new Object[]{name};
        if (entryName!=null){
            whereFldName=LPArray.addValueToArray1D(whereFldName, TblsInspLotRMConfig.MaterialInventoryPlan.FLD_ENTRY_NAME.getName());
            whereFldValue=LPArray.addValueToArray1D(whereFldValue, entryName);
        }
        Object[][] matCertifInfo=getTableData(GlobalVariables.Schemas.CONFIG.getName(), TblsInspLotRMConfig.MaterialInventoryPlan.TBL.getName(), 
                    filterFieldsToRetrieve, TblsInspLotRMConfig.MaterialInventoryPlan.getAllFieldNames(), whereFldName, whereFldValue, orderBy);        
        JSONArray jArr = new JSONArray();
        String[] fieldsToRetrieve=getFieldsListToRetrieve(filterFieldsToRetrieve, TblsInspLotRMConfig.MaterialInventoryPlan.getAllFieldNames());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(matCertifInfo[0][0].toString())) return jArr;
        for (Object[] currMatCertif: matCertifInfo){
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currMatCertif);
            jArr.add(jObj);
        }
        return jArr;
    }
    public static JSONArray configMaterialSamplingPlanStructure(String name, String entryName, String filterFieldsToRetrieve, String[] orderBy){
        String[] whereFldName=new String[]{TblsInspLotRMConfig.MaterialSamplingPlan.FLD_MATERIAL.getName()};
        Object[] whereFldValue=new Object[]{name};
        if (entryName!=null){
            whereFldName=LPArray.addValueToArray1D(whereFldName, TblsInspLotRMConfig.MaterialSamplingPlan.FLD_ENTRY_NAME.getName());
            whereFldValue=LPArray.addValueToArray1D(whereFldValue, entryName);
        }
        Object[][] matCertifInfo=getTableData(GlobalVariables.Schemas.CONFIG.getName(), TblsInspLotRMConfig.MaterialSamplingPlan.TBL.getName(), 
                    filterFieldsToRetrieve, TblsInspLotRMConfig.MaterialSamplingPlan.getAllFieldNames(), whereFldName, whereFldValue, orderBy);        
        JSONArray jArr = new JSONArray();
        String[] fieldsToRetrieve=getFieldsListToRetrieve(filterFieldsToRetrieve, TblsInspLotRMConfig.MaterialSamplingPlan.getAllFieldNames());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(matCertifInfo[0][0].toString())) return jArr;
        for (Object[] currMatCertif: matCertifInfo){
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currMatCertif);
            jArr.add(jObj);
        }
        return jArr;
    }
    
    public static JSONArray dataSampleStructure(String lotName, Integer sampleId, String filterFieldsToRetrieve, String[] orderBy,
            Boolean includeAnalysis, Boolean includeAnalysisResults){
        String[] whereFldName=new String[]{TblsInspLotRMData.Sample.FLD_LOT_NAME.getName()};
        Object[] whereFldValue=new Object[]{lotName};
        if ((includeAnalysis==null || includeAnalysis || includeAnalysisResults==null || includeAnalysisResults) && filterFieldsToRetrieve.length()>0 && !filterFieldsToRetrieve.contains(TblsInspLotRMData.Lot.FLD_MATERIAL_NAME.getName()))
            filterFieldsToRetrieve=filterFieldsToRetrieve + "|"+TblsInspLotRMData.Sample.FLD_SAMPLE_ID.getName();
        
        Object[][] materialInfo=getTableData(GlobalVariables.Schemas.DATA.getName(), TblsInspLotRMData.Sample.TBL.getName(), 
                    filterFieldsToRetrieve, TblsInspLotRMData.Sample.getAllFieldNames(), whereFldName, whereFldValue, orderBy);        
        JSONArray jArr = new JSONArray();
        String[] fieldsToRetrieve=getFieldsListToRetrieve(filterFieldsToRetrieve, TblsInspLotRMData.Sample.getAllFieldNames());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) return jArr;
        for (Object[] currRec: materialInfo){
            sampleId=Integer.valueOf(currRec[LPArray.valuePosicInArray(fieldsToRetrieve, TblsInspLotRMData.Sample.FLD_SAMPLE_ID.getName())].toString());
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currRec);
            if (includeAnalysis==null || includeAnalysis) jObj.put(TblsData.SampleAnalysis.TBL.getName(), dataSampleAnalysisStructure(sampleId, null, new String[]{}, includeAnalysisResults));
            jArr.add(jObj);
        }
        return jArr;
    }
    public static JSONArray dataSampleAnalysisStructure(Integer sampleId, String filterFieldsToRetrieve, String[] orderBy, 
            Boolean includeAnalysisResults){
        String[] whereFldName=new String[]{TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName()};
        Object[] whereFldValue=new Object[]{sampleId};
        Object[][] materialInfo=getTableData(GlobalVariables.Schemas.DATA.getName(), TblsData.SampleAnalysis.TBL.getName(), 
                    filterFieldsToRetrieve, TblsData.SampleAnalysis.getAllFieldNames(), whereFldName, whereFldValue, orderBy);        
        JSONArray jArr = new JSONArray();
        String[] fieldsToRetrieve=getFieldsListToRetrieve(filterFieldsToRetrieve, TblsData.SampleAnalysis.getAllFieldNames());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) return jArr;
        for (Object[] currRec: materialInfo){
            Integer testId=Integer.valueOf(currRec[LPArray.valuePosicInArray(fieldsToRetrieve, TblsData.SampleAnalysis.FLD_TEST_ID.getName())].toString());
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currRec);
            if (includeAnalysisResults==null || includeAnalysisResults) jObj.put(TblsData.SampleAnalysisResult.TBL.getName(), dataSampAnaResStructure(testId, null, new String[]{}, null));            
            jArr.add(jObj);
        }
        return jArr;
    }    
    public static JSONArray dataSampAnaResStructure(Integer testId, String filterFieldsToRetrieve, String[] orderBy, 
            Boolean includeAnalysisResults){
        String[] whereFldName=new String[]{TblsData.SampleAnalysisResult.FLD_TEST_ID.getName()};
        Object[] whereFldValue=new Object[]{testId};
        Object[][] materialInfo=getTableData(GlobalVariables.Schemas.DATA.getName(), TblsData.SampleAnalysisResult.TBL.getName(), 
                    filterFieldsToRetrieve, TblsData.SampleAnalysisResult.getAllFieldNames(), whereFldName, whereFldValue, orderBy);        
        JSONArray jArr = new JSONArray();
        String[] fieldsToRetrieve=getFieldsListToRetrieve(filterFieldsToRetrieve, TblsData.SampleAnalysisResult.getAllFieldNames());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) return jArr;
        for (Object[] currRec: materialInfo){
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currRec);
            jArr.add(jObj);
        }
        return jArr;
    }    
    
}
