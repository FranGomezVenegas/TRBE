/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import databases.TblsCnfgAudit;
import lbplanet.utilities.LPPlatform;
import static trazit.enums.deployrepository.DeployTables.createTableScript;

/**
 *
 * @author User
 */
public class TblsEnvMonitConfigAudit {
    public static final String getTableCreationScriptFromConfigAuditTableEnvMonit(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "ANALYSIS": return createTableScript(TblsCnfgAudit.TablesCfgAudit.ANALYSIS, schemaNamePrefix);
            case "SPEC": return createTableScript(TblsCnfgAudit.TablesCfgAudit.SPEC, schemaNamePrefix);
            default: return "TABLE "+tableName+" NOT IN ENVMONIT_TBLSCNFGAUDITENVMONIT"+LPPlatform.LAB_FALSE;            
        }        
    }
    
}
