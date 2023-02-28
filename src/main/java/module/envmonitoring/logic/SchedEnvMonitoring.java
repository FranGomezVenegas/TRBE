/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.envmonitoring.logic;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig.TablesEnvMonitConfig;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.features.Token;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramSample;
import lbplanet.utilities.LPDate;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;

public class SchedEnvMonitoring {
    public static void EnvMonitSchedProcesses(Token token, String procInstanceName){
        String moduleNameFromProcInstance = token.getModuleNameFromProcInstance(procInstanceName);
        if (!GlobalVariables.TrazitModules.ENVIRONMENTAL_MONITORING.name().equalsIgnoreCase(moduleNameFromProcInstance)) return;
        logNextEventWhenExpiredOrClose(token, procInstanceName);
    }
    public static void logNextEventWhenExpiredOrClose(Token token, String procInstanceName){  
        DataProgramSample prgSmp = new DataProgramSample();  
        SqlWhere sW=new SqlWhere();
        sW.addConstraint(TblsEnvMonitConfig.Program.NAME, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);        
        EnumIntTableFields[] fieldsToRetrieve = EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM, 
            TblsEnvMonitConfig.Program.NAME);
        Object[][] tableData = QueryUtilitiesEnums.getTableData(TablesEnvMonitConfig.PROGRAM,
                fieldsToRetrieve,
                sW, null, procInstanceName);                
        for (Object[] curRow: tableData){
            prgSmp.logProgramSampleScheduled(curRow[0].toString(), LPDate.dateStringFormatToLocalDateTime(LPDate.getCurrentDateWithNoTime().toString()), 
                LPDate.dateStringFormatToLocalDateTime(LPDate.addIntervalToGivenDate(LPDate.getCurrentDateWithNoTime(), "DAYS", 1).toString()));
        }
        prgSmp=null;
    }
    
}