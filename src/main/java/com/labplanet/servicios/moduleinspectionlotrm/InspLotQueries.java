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
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilities;
import static trazit.queries.QueryUtilities.getFieldsListToRetrieve;
import static trazit.queries.QueryUtilities.getTableData;

/**
 *
 * @author User
 */
public final class InspLotQueries {
    private InspLotQueries() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    public static JSONArray configMaterialStructure(String name, String filterFieldsToRetrieve, String[] orderBy,
            Boolean includeMatCertificate, Boolean includeMatInventoryPlan, Boolean includeMatSamplingPlan){
        String[] whereFldName=new String[]{TblsInspLotRMConfig.Material.NAME.getName()};
        Object[] whereFldValue=new Object[]{name};
        Object[][] materialInfo=QueryUtilities.getTableData(GlobalVariables.Schemas.CONFIG.getName(), TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL.getTableName(), 
                    filterFieldsToRetrieve, EnumIntTableFields.getAllFieldNames(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL.getTableFields()), whereFldName, whereFldValue, orderBy);        
        JSONArray jArr = new JSONArray();
        String[] fieldsToRetrieve=getFieldsListToRetrieve(filterFieldsToRetrieve, EnumIntTableFields.getAllFieldNames(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) return jArr;
        for (Object[] currRec: materialInfo){
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currRec);
            if (includeMatCertificate==null || includeMatCertificate) jObj.put(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL_CERTIFICATE.getTableName(), configMaterialCertificateStructure(name, null, null, new String[]{}));
            if (includeMatInventoryPlan==null || includeMatInventoryPlan) jObj.put(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL_INVENTORY_PLAN.getTableName(), configMaterialInventoryPlanStructure(name, null, null, new String[]{}));
            if (includeMatSamplingPlan==null || includeMatSamplingPlan) jObj.put(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL_SAMPLING_PLAN.getTableName(), configMaterialSamplingPlanStructure(name, null, null, new String[]{}));
            jArr.add(jObj);
        }
        return jArr;
    }
    public static JSONArray configMaterialCertificateStructure(String name, String configName, String filterFieldsToRetrieve, String[] orderBy){
        String[] whereFldName=new String[]{TblsInspLotRMConfig.MaterialCertificate.MATERIAL.getName()};
        Object[] whereFldValue=new Object[]{name};
        if (configName!=null){
            whereFldName=LPArray.addValueToArray1D(whereFldName, TblsInspLotRMConfig.MaterialCertificate.CONFIG_NAME.getName());
            whereFldValue=LPArray.addValueToArray1D(whereFldValue, configName);
        }
        Object[][] matCertifInfo=getTableData(GlobalVariables.Schemas.CONFIG.getName(), TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL_CERTIFICATE.getTableName(), 
                    filterFieldsToRetrieve, EnumIntTableFields.getAllFieldNames(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL_CERTIFICATE.getTableFields()), whereFldName, whereFldValue, orderBy);        
        JSONArray jArr = new JSONArray();
        String[] fieldsToRetrieve=getFieldsListToRetrieve(filterFieldsToRetrieve, EnumIntTableFields.getAllFieldNames(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL_CERTIFICATE.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(matCertifInfo[0][0].toString())) return jArr;
        for (Object[] currMatCertif: matCertifInfo){
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currMatCertif);
            jArr.add(jObj);
        }
        return jArr;
    }
    public static JSONArray configMaterialInventoryPlanStructure(String name, String entryName, String filterFieldsToRetrieve, String[] orderBy){
        String[] whereFldName=new String[]{TblsInspLotRMConfig.MaterialInventoryPlan.MATERIAL.getName()};
        Object[] whereFldValue=new Object[]{name};
        if (entryName!=null){
            whereFldName=LPArray.addValueToArray1D(whereFldName, TblsInspLotRMConfig.MaterialInventoryPlan.ENTRY_NAME.getName());
            whereFldValue=LPArray.addValueToArray1D(whereFldValue, entryName);
        }
        Object[][] matCertifInfo=getTableData(GlobalVariables.Schemas.CONFIG.getName(), TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL_INVENTORY_PLAN.getTableName(), 
                    filterFieldsToRetrieve, EnumIntTableFields.getAllFieldNames(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL_INVENTORY_PLAN.getTableFields()), whereFldName, whereFldValue, orderBy);        
        JSONArray jArr = new JSONArray();
        String[] fieldsToRetrieve=getFieldsListToRetrieve(filterFieldsToRetrieve, EnumIntTableFields.getAllFieldNames(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL_INVENTORY_PLAN.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(matCertifInfo[0][0].toString())) return jArr;
        for (Object[] currMatCertif: matCertifInfo){
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currMatCertif);
            jArr.add(jObj);
        }
        return jArr;
    }
    public static JSONArray configMaterialSamplingPlanStructure(String name, String entryName, String filterFieldsToRetrieve, String[] orderBy){
        String[] whereFldName=new String[]{TblsInspLotRMConfig.MaterialSamplingPlan.MATERIAL.getName()};
        Object[] whereFldValue=new Object[]{name};
        if (entryName!=null){
            whereFldName=LPArray.addValueToArray1D(whereFldName, TblsInspLotRMConfig.MaterialSamplingPlan.ENTRY_NAME.getName());
            whereFldValue=LPArray.addValueToArray1D(whereFldValue, entryName);
        }
        Object[][] matCertifInfo=getTableData(GlobalVariables.Schemas.CONFIG.getName(), TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL_SAMPLING_PLAN.getTableName(), 
                    filterFieldsToRetrieve, EnumIntTableFields.getAllFieldNames(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL_SAMPLING_PLAN.getTableFields()), whereFldName, whereFldValue, orderBy);        
        JSONArray jArr = new JSONArray();
        String[] fieldsToRetrieve=getFieldsListToRetrieve(filterFieldsToRetrieve, EnumIntTableFields.getAllFieldNames(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL_SAMPLING_PLAN.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(matCertifInfo[0][0].toString())) return jArr;
        for (Object[] currMatCertif: matCertifInfo){
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currMatCertif);
            jArr.add(jObj);
        }
        return jArr;
    }
    
    public static JSONArray dataSampleStructure(String lotName, Integer sampleId, String filterFieldsToRetrieve, String[] orderBy,
            Boolean includeAnalysis, Boolean includeAnalysisResults){
        String[] whereFldName=new String[]{TblsInspLotRMData.Sample.LOT_NAME.getName()};
        Object[] whereFldValue=new Object[]{lotName};
        if ((includeAnalysis==null || includeAnalysis || includeAnalysisResults==null || includeAnalysisResults) && filterFieldsToRetrieve.length()>0 && !filterFieldsToRetrieve.contains(TblsInspLotRMData.Lot.MATERIAL_NAME.getName()))
            filterFieldsToRetrieve=filterFieldsToRetrieve + "|"+TblsInspLotRMData.Sample.SAMPLE_ID.getName();
        
        Object[][] materialInfo=getTableData(GlobalVariables.Schemas.DATA.getName(), TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableName(), 
                    filterFieldsToRetrieve, EnumIntTableFields.getAllFieldNames(TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableFields()), whereFldName, whereFldValue, orderBy);        
        JSONArray jArr = new JSONArray();
        String[] fieldsToRetrieve=getFieldsListToRetrieve(filterFieldsToRetrieve, EnumIntTableFields.getAllFieldNames(TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) return jArr;
        for (Object[] currRec: materialInfo){
            if (sampleId==null){
                sampleId=Integer.valueOf(currRec[LPArray.valuePosicInArray(fieldsToRetrieve, TblsInspLotRMData.Sample.SAMPLE_ID.getName())].toString());
            }
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currRec);
            if (includeAnalysis==null || includeAnalysis) jObj.put(TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), dataSampleAnalysisStructure(sampleId, null, new String[]{}, includeAnalysisResults));
            jArr.add(jObj);
        }
        return jArr;
    }
    public static JSONArray dataSampleAnalysisStructure(Integer sampleId, String filterFieldsToRetrieve, String[] orderBy, 
            Boolean includeAnalysisResults){
        String[] whereFldName=new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName()};
        Object[] whereFldValue=new Object[]{sampleId};
        Object[][] materialInfo=getTableData(GlobalVariables.Schemas.DATA.getName(), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                    filterFieldsToRetrieve, getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS.getTableFields()), whereFldName, whereFldValue, orderBy);        
        JSONArray jArr = new JSONArray();
        String[] fieldsToRetrieve=getFieldsListToRetrieve(filterFieldsToRetrieve, getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) return jArr;
        for (Object[] currRec: materialInfo){
            Integer testId=Integer.valueOf(currRec[LPArray.valuePosicInArray(fieldsToRetrieve, TblsData.SampleAnalysis.TEST_ID.getName())].toString());
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currRec);
            if (includeAnalysisResults==null || includeAnalysisResults) jObj.put(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), dataSampAnaResStructure(testId, null, new String[]{}, null));            
            jArr.add(jObj);
        }
        return jArr;
    }    
    public static JSONArray dataSampAnaResStructure(Integer testId, String filterFieldsToRetrieve, String[] orderBy, 
            Boolean includeAnalysisResults){
        String[] whereFldName=new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()};
        Object[] whereFldValue=new Object[]{testId};
        Object[][] materialInfo=getTableData(GlobalVariables.Schemas.DATA.getName(), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                    filterFieldsToRetrieve, getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableFields()), whereFldName, whereFldValue, orderBy);        
        JSONArray jArr = new JSONArray();
        String[] fieldsToRetrieve=getFieldsListToRetrieve(filterFieldsToRetrieve, getAllFieldNames(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) return jArr;
        for (Object[] currRec: materialInfo){
            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currRec);
            jArr.add(jObj);
        }
        return jArr;
    }    
    
}
