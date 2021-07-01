/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import databases.TblsCnfgAudit;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author User
 */
public class TblsEnvMonitConfigAudit {
    public static final String getTableCreationScriptFromConfigAuditTableEnvMonit(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "ANALYSIS": return TblsCnfgAudit.Analysis.createTableScript(schemaNamePrefix, fields);
            case "SPEC": return TblsCnfgAudit.Spec.createTableScript(schemaNamePrefix, fields);
            default: return "TABLE "+tableName+" NOT IN ENVMONIT_TBLSCNFGAUDITENVMONIT"+LPPlatform.LAB_FALSE;            
        }        
    }
    
}
