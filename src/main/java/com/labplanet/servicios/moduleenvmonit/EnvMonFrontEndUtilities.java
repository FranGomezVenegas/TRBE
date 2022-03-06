/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import databases.Rdbms;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
class EnvMonFrontEndUtilities {
  private EnvMonFrontEndUtilities() {
    throw new IllegalStateException("Utility class");
  }

    /**
     *
     * @param procInstanceName Procedure prefix
     * @param programConfigId program id
     * @param programVersion program version
     * @param fieldsName fields to retrieve
     * @param sortFields fields for sorting
     * @return ConfigProgram info (field values) for a given program-version
     */
    public static Object[][] configProgramInfo(String procInstanceName, String programConfigId, Integer programVersion, String[] fieldsName, String[] sortFields){
    if (fieldsName==null || fieldsName.length==0){
      for (TblsEnvMonitConfig.Program obj: TblsEnvMonitConfig.Program.values()){
          String objName = obj.name();
          if (!"TBL".equalsIgnoreCase(objName))
            fieldsName=LPArray.addValueToArray1D(fieldsName, obj.getName());
      }      
    }
    return Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM.getTableName(), 
            new String[]{TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_VERSION.getName()}, 
            new Object[]{programConfigId, programVersion}, 
            fieldsName, sortFields);
  }

    /**
     *
     * @param procInstanceName Procedure prefix
     * @param programConfigId program id
     * @param fieldsName fields to retrieve
     * @param sortFields fields for sorting
     * @return the configProgramLocation info (field values) for a given program-version
     */
    public static Object[][] configProgramLocationInfo(String procInstanceName, String programConfigId, String[] fieldsName, String[] sortFields){
    if (fieldsName==null || fieldsName.length==0){
      for (TblsEnvMonitConfig.ProgramCalendarDate obj: TblsEnvMonitConfig.ProgramCalendarDate.values()){
          String objName = obj.name();
          if (!"TBL".equalsIgnoreCase(objName))
            fieldsName=LPArray.addValueToArray1D(fieldsName, obj.getName());
      }      
    }
    return Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_CALENDAR_DATE.getTableName(), 
            new String[]{TblsEnvMonitConfig.ProgramCalendarDate.PROGRAM_ID.getName()}, 
            new Object[]{programConfigId,}, 
            fieldsName, sortFields);
  }

    /**
     *
     * @param procInstanceName Procedure prefix
     * @param programName program name
     * @param fieldsName fields to retrieve
     * @param sortFields fields for sorting
     * @return dataProgram info (field values) for a given program
     */
    public static JSONObject dataProgramInfo(String procInstanceName, String programName, String[] fieldsName, String[] sortFields){
    if (fieldsName==null || fieldsName.length==0){
      for (TblsEnvMonitData.Program obj: TblsEnvMonitData.Program.values()){
          String objName = obj.name();
          if (!"TBL".equalsIgnoreCase(objName))
            fieldsName=LPArray.addValueToArray1D(fieldsName, obj.getName());
      }      
    }
    Object[][] records=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitData.Program.TBL.getName(), 
            new String[]{TblsEnvMonitData.Program.FLD_NAME.getName()}, 
            new Object[]{programName}, 
            fieldsName, sortFields);
    return LPJson.convertArrayRowToJSONObject(fieldsName, records[0]);
  }

    /**
     *
     * @param procInstanceName Procedure Prefix
     * @param programName program name
     * @param fieldsName fields to retrieve
     * @param sortFields fields for sorting
     * @return dataProgramLocation info for a given program
     */
    public static JSONArray dataProgramLocationInfo(String procInstanceName, String programName, String[] fieldsName, String[] sortFields){
    if (fieldsName==null || fieldsName.length==0)
        fieldsName=TblsEnvMonitData.ProgramLocation.getAllFieldNames();
    Object[][] records=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitData.ProgramLocation.TBL.getName(), 
            new String[]{TblsEnvMonitData.ProgramLocation.FLD_PROGRAM_NAME.getName()}, 
            new Object[]{programName,}, 
            fieldsName, sortFields);
    JSONArray jArr = new JSONArray();
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(records[0][0].toString())){
        for (Object[] curRec: records)
            jArr.add(LPJson.convertArrayRowToJSONObject(fieldsName, curRec));    
    }
    return jArr;
  }
  
}
