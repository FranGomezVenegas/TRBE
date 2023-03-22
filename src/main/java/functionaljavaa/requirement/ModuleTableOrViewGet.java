/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement;

import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsDataAudit;
import lbplanet.utilities.LPArray;
import module.inventorytrack.definition.TblsInvTrackingConfig;
import module.inventorytrack.definition.TblsInvTrackingData;
import module.inventorytrack.definition.TblsInvTrackingDataAudit;
import trazit.enums.EnumIntTables;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class ModuleTableOrViewGet {
    private Boolean found;
    private EnumIntTables tableObj;
    private Boolean mirrorForTesting;
    private String errorMsg;
    
    public ModuleTableOrViewGet(Boolean isView, String moduleName, String curSchemaName, String tblName){
        if (Boolean.FALSE.equals(isView)){
            getModuleTableObj(moduleName, curSchemaName, tblName);
            return;
        }
        this.found=false;
        this.errorMsg="Not developed yet";
    }
    
    public static Boolean moduleExists(String moduleName){
        String[] modulesListArr=new String[]{"ENVIRONMENTAL_MONITORING","SAMPLES", "INSPECTION_LOT_RAW_MATERIAL",
            "GENOMIC"};
        return LPArray.valueInArray(modulesListArr, moduleName);
    }
    public void getModuleTableObj(String moduleName, String curSchemaName, String tblName){
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.CONFIG.getName()))
            this.mirrorForTesting=false;            
        switch (moduleName){
            case "ENVIRONMENTAL_MONITORING":            
            case "SAMPLES":
                return;
            case "INVENTORY_TRACK":
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.CONFIG.getName())){
                    try{                        
                        this.tableObj=TblsInvTrackingConfig.TablesInvTrackingConfig.valueOf(tblName.toUpperCase());                        
                        this.found=true;
                        return;
                    }catch(Exception e1){
                        try{                        
                            this.tableObj=TblsCnfg.TablesConfig.valueOf(tblName.toUpperCase());                        
                            this.found=true;
                        }catch(Exception e2){
                            this.found=false;
                            this.errorMsg="table not found";
                            this.tableObj=null;
                        }
                    }
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.DATA_AUDIT.getName())){
                    this.mirrorForTesting=true;
                    try{
                        this.tableObj=TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.valueOf(tblName.toUpperCase());                        
                        this.found=true;
                        return;
                    }catch(Exception e1){
                        try{
                            this.tableObj=TblsDataAudit.TablesDataAudit.valueOf(tblName.toUpperCase());                        
                            this.found=true;
                        }catch(Exception e2){
                            this.found=false;
                            this.errorMsg="table not found";
                            this.tableObj=null;
                        }
                    }
                    return;
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.DATA.getName())){
                    this.mirrorForTesting=true;
                    try{
                        this.tableObj=TblsInvTrackingData.TablesInvTrackingData.valueOf(tblName.toUpperCase());                        
                        this.found=true;
                    }catch(Exception e1){
                        try{
                            this.tableObj=TblsData.TablesData.valueOf(tblName.toUpperCase());                        
                            this.found=true;
                        }catch(Exception e2){
                            this.found=false;
                            this.errorMsg="table not found";
                            this.tableObj=null;
                        }
                    }
                }
                break;
            default:
        }
    }    

    /**
     * @return the found
     */
    public Boolean getFound() {
        return found;
    }

    /**
     * @return the tableObj
     */
    public EnumIntTables getTableObj() {
        return tableObj;
    }

    /**
     * @return the mirrorForTesting
     */
    public Boolean getMirrorForTesting() {
        return mirrorForTesting;
    }

    /**
     * @return the errorMsg
     */
    public String getErrorMsg() {
        return errorMsg;
    }
    
}
