/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import module.monitoring.definition.TblsEnvMonitConfig;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.queries.QueryUtilitiesEnums;
/**
 *
 * @author Administrator
 */
public class EnvMonFrontEndUtilities {
  private EnvMonFrontEndUtilities() {
    throw new IllegalStateException("Utility class");
  }

    /**
     *
     * @param programConfigId program id
     * @param programVersion program version
     * @param fieldsName fields to retrieve
     * @param sortFields fields for sorting
     * @return ConfigProgram info (field values) for a given program-version
     */
    public static Object[][] configProgramInfo(String programConfigId, Integer programVersion, String[] fieldsName, String[] sortFields){
    if (fieldsName==null || fieldsName.length==0){
      for (TblsEnvMonitConfig.Program obj: TblsEnvMonitConfig.Program.values()){
          String objName = obj.name();
          if (Boolean.FALSE.equals("TBL".equalsIgnoreCase(objName)))
            fieldsName=LPArray.addValueToArray1D(fieldsName, obj.getName());
      }      
    }
    return QueryUtilitiesEnums.getTableData(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM, 
        EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM, fieldsName),
        new String[]{TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_VERSION.getName()}, 
        new Object[]{programConfigId, programVersion}, sortFields);
  }

    /**
     *
     * @param procInstanceName Procedure prefix
     * @param programConfigId program id
     * @param fieldsName fields to retrieve
     * @param sortFields fields for sorting
     * @return the configProgramLocation info (field values) for a given program-version
     */
    public static Object[][] configProgramLocationInfo(String programConfigId, String[] fieldsName, String[] sortFields){
    if (fieldsName==null || fieldsName.length==0){
      for (TblsEnvMonitConfig.ProgramCalendarDate obj: TblsEnvMonitConfig.ProgramCalendarDate.values()){
          String objName = obj.name();
          if (Boolean.FALSE.equals("TBL".equalsIgnoreCase(objName)))
            fieldsName=LPArray.addValueToArray1D(fieldsName, obj.getName());
      }      
    }
    return QueryUtilitiesEnums.getTableData(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_CALENDAR_DATE, 
        EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_CALENDAR_DATE, fieldsName),
        new String[]{TblsEnvMonitConfig.ProgramCalendarDate.PROGRAM_CONFIG_ID.getName()}, 
        new Object[]{programConfigId,}, 
        sortFields);
  }

    /**
     *
     * @param programName program name
     * @param fieldsName fields to retrieve
     * @param sortFields fields for sorting
     * @return dataProgram info (field values) for a given program
     */
    public static JSONObject dataProgramInfo(String programName, String[] fieldsName, String[] sortFields){
    if (fieldsName==null || fieldsName.length==0){
      for (TblsEnvMonitConfig.Program obj: TblsEnvMonitConfig.Program.values()){
          String objName = obj.name();
          if (Boolean.FALSE.equals("TBL".equalsIgnoreCase(objName)))
            fieldsName=LPArray.addValueToArray1D(fieldsName, obj.getName());
      }      
    }
    Object[][] records=QueryUtilitiesEnums.getTableData(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM, 
        EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM, fieldsName),
        new String[]{TblsEnvMonitConfig.Program.NAME.getName()}, 
        new Object[]{programName}, sortFields);
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(records[0][0].toString())) return new JSONObject();
    return LPJson.convertArrayRowToJSONObject(fieldsName, records[0]);
  }

    /**
     *
     * @param programName program name
     * @param fieldsName fields to retrieve
     * @param sortFields fields for sorting
     * @return dataProgramLocation info for a given program
     */
    public static JSONArray dataProgramLocationInfo(String programName, String[] fieldsName, String[] sortFields){
    if (fieldsName==null || fieldsName.length==0)
        fieldsName=EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getTableFields());
    Object[][] records=QueryUtilitiesEnums.getTableData(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION, 
        EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION, fieldsName),
        new String[]{TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName()}, 
        new Object[]{programName,}, 
        sortFields);
    JSONArray jArr = new JSONArray();
    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(records[0][0].toString()))){
        for (Object[] curRec: records)
            jArr.put(LPJson.convertArrayRowToJSONObject(fieldsName, curRec));    
    }
    return jArr;
  }
  
}
