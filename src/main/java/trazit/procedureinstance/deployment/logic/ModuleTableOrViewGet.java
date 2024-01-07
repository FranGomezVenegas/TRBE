/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.deployment.logic;

import databases.Rdbms;
import databases.TblsCnfg;
import databases.TblsCnfgAudit;
import databases.TblsData;
import databases.TblsDataAudit;
import databases.TblsProcedure;
import databases.TblsProcedureAudit;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntViews;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.TrazitModules;

/**
 *
 * @author User
 */
public final class ModuleTableOrViewGet {

    private Boolean found;
    private EnumIntTables tableObj;
    private EnumIntViews viewObj;
    private Boolean mirrorForTesting;
    private String errorMsg;

    public ModuleTableOrViewGet(Boolean isView, String moduleName, String curSchemaName, String tblName, String procInstanceName) {
        if (Boolean.FALSE.equals(isView)) {
            getModuleTableObj(moduleName, curSchemaName, tblName, procInstanceName);
        } else {
            getModuleViewObj(moduleName, curSchemaName, tblName, procInstanceName);
        }
    }

    public enum ModulesTablesDefinition {
        //INVENTORY_TRACKING(),
        //INSPECTION_LOTS_RAW_MAT(),
        //ENVIRONMENTAL_MONITORING(),
        //GENOMIC(),
        //SAMPLES(),
        BASE("TablesConfig", "", "TablesData", "TablesDataAudit", "TablesProcedure", "TablesProcedure");

        private ModulesTablesDefinition(String c, String ca, String d, String da, String pr,
                String pra) {
            this.config = c;
            this.configAudit = ca;
            this.data = d;
            this.dataAudit = da;
            this.procedure = pr;
            this.procedureAudit = pra;
        }
        private final String config;
        private final String configAudit;
        private final String data;
        private final String dataAudit;
        private final String procedure;
        private final String procedureAudit;

        public String getName() {
            return this.config;
        }

        public String getFieldType() {
            return this.configAudit;
        }

        public String getFieldMask() {
            return this.data;
        }

        public String getReferenceTable() {
            return this.dataAudit;
        }

        public String getFieldComment() {
            return this.procedure;
        }

        public String getFldBusinessRules() {
            return this.procedureAudit;
        }
    }

    public void getModuleTableObjNew(String moduleName, String curSchemaName, String tblName) {
        try (io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo()//.acceptPackages("com.xyz")
                .scan()) {

            ClassInfoList classesImplementing = scanResult.getClassesImplementing("trazit.enums.EnumIntTables");
        } catch (Exception e) {
            ScanResult.closeAll();
            return;
        }
        ScanResult.closeAll();
    }

    public void getModuleTableObj(String moduleName, String curSchemaName, String tblName, String procInstanceName) {
        String tblNotFound="table not found";
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.CONFIG.getName())) {
            this.mirrorForTesting = false;
        }
        TrazitModules moduleObj = TrazitModules.valueOf(moduleName);
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.CONFIG_AUDIT.getName())) {
            this.mirrorForTesting = true;     
            Integer tblPosic=-1;
            if (moduleObj.getConfigAuditVws()!=null)
                tblPosic=EnumIntTables.getTblPosicInArray(moduleObj.getConfigAuditTbls(), tblName.toUpperCase());
            if (tblPosic>-1){
                this.tableObj = moduleObj.getConfigAuditTbls()[tblPosic];
                this.found = true;
            }else{
                try {
                    this.tableObj = TblsCnfgAudit.TablesCfgAudit.valueOf(tblName.toUpperCase());
                    this.found = true;
                } catch (Exception e2) {
                    this.found = false;
                    this.errorMsg = tblNotFound;
                    this.tableObj = null;
                }
            }
            if (this.found){
                String schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                    this.mirrorForTesting = true;
                }                                
            }
            return;
        }
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.CONFIG.getName())) {
            this.mirrorForTesting = true;     
            Integer tblPosic=-1;
            if (moduleObj.getConfigTbls()!=null)
                tblPosic=EnumIntTables.getTblPosicInArray(moduleObj.getConfigTbls(), tblName.toUpperCase());
            if (tblPosic>-1){
                this.tableObj = moduleObj.getConfigTbls()[tblPosic];
                this.found = true;
            }else{
                try {
                    this.tableObj = TblsCnfg.TablesConfig.valueOf(tblName.toUpperCase());
                    this.found = true;
                } catch (Exception e2) {
                    this.found = false;
                    this.errorMsg = tblNotFound;
                    this.tableObj = null;
                }
            }
            if (this.found){
                String schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                    this.mirrorForTesting = true;
                }                                
            }
            return;
        }
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.DATA_AUDIT.getName())) {
            this.mirrorForTesting = true;     
            Integer tblPosic=-1;
            if (moduleObj.getDataAuditTbls()!=null)
                tblPosic=EnumIntTables.getTblPosicInArray(moduleObj.getDataAuditTbls(), tblName.toUpperCase());
            if (tblPosic>-1){
                this.tableObj = moduleObj.getDataAuditTbls()[tblPosic];
                this.found = true;
            }else{
                try {
                    this.tableObj = TblsDataAudit.TablesDataAudit.valueOf(tblName.toUpperCase());
                    this.found = true;
                } catch (Exception e2) {
                    this.found = false;
                    this.errorMsg = tblNotFound;
                    this.tableObj = null;
                }
            }
            if (this.found){
                String schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                    this.mirrorForTesting = true;
                }                                
            }
            return;
        }
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.DATA.getName())) {
            this.mirrorForTesting = true;     
            Integer tblPosic=-1;
            if (moduleObj.getDataTbls()!=null)
                tblPosic=EnumIntTables.getTblPosicInArray(moduleObj.getDataTbls(), tblName.toUpperCase());
            if (tblPosic>-1){
                this.tableObj = moduleObj.getDataTbls()[tblPosic];
                this.found = true;
            }else{
                try {
                    this.tableObj = TblsData.TablesData.valueOf(tblName.toUpperCase());
                    this.found = true;
                } catch (Exception e2) {
                    this.found = false;
                    this.errorMsg = tblNotFound;
                    this.tableObj = null;
                }
            }
            if (this.found){
                String schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                    this.mirrorForTesting = true;
                }                                
            }
            return;
        }
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.PROCEDURE_AUDIT.getName())) {
            this.mirrorForTesting = true;     
            Integer tblPosic=-1;
            if (moduleObj.getProcedureAuditTbls()!=null)
                tblPosic=EnumIntTables.getTblPosicInArray(moduleObj.getProcedureAuditTbls(), tblName.toUpperCase());
            if (tblPosic>-1){
                this.tableObj = moduleObj.getProcedureAuditTbls()[tblPosic];
                this.found = true;
            }else{
                try {
                    this.tableObj = TblsProcedureAudit.TablesProcedureAudit.valueOf(tblName.toUpperCase());
                    this.found = true;
                } catch (Exception e2) {
                    this.found = false;
                    this.errorMsg = tblNotFound;
                    this.tableObj = null;
                }
            }
            if (this.found){
                String schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                    this.mirrorForTesting = true;
                }                                
            }
            return;
        }
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.PROCEDURE.getName())) {
            this.mirrorForTesting = true;     
            Integer tblPosic=-1;
            if (moduleObj.getProcedureTbls()!=null)
                tblPosic=EnumIntTables.getTblPosicInArray(moduleObj.getProcedureTbls(), tblName.toUpperCase());
            if (tblPosic>-1){
                this.tableObj = moduleObj.getProcedureTbls()[tblPosic];
                this.found = true;
            }else{
                try {
                    this.tableObj = TblsProcedure.TablesProcedure.valueOf(tblName.toUpperCase());
                    this.found = true;
                } catch (Exception e2) {
                    this.found = false;
                    this.errorMsg = tblNotFound;
                    this.tableObj = null;
                }
            }
            if (this.found){
                String schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                    this.mirrorForTesting = true;
                }                                
            }
            return;
        }
    }

    public void getModuleViewObj(String moduleName, String curSchemaName, String tblName) {
        getModuleViewObj(moduleName, curSchemaName, tblName, null);
    }

    public void getModuleViewObj(String moduleName, String curSchemaName, String tblName, String procInstanceName) {
        String viewNotFound="View not found";
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.CONFIG.getName())) {
            this.mirrorForTesting = false;
        }
        TrazitModules moduleObj = TrazitModules.valueOf(moduleName);
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.CONFIG_AUDIT.getName())) {
            this.mirrorForTesting = true;     
            Integer tblPosic=-1;
            if (moduleObj.getConfigAuditVws()!=null)
                tblPosic=EnumIntViews.getViewPosicInArray(moduleObj.getConfigAuditVws(), tblName.toUpperCase());
            if (tblPosic>-1){
                this.viewObj = moduleObj.getConfigAuditVws()[tblPosic];
                this.found = true;
            }else{
                try {
                    this.viewObj = null; //TblsCnfgAudit.ViewsCfgAudit.valueOf(tblName.toUpperCase());
                    this.found = true;
                } catch (Exception e2) {
                    this.found = false;
                    this.errorMsg = viewNotFound;
                    this.viewObj = null;
                }
            }
            if (this.found){
                String schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                    this.mirrorForTesting = true;
                }                                
            }
            return;
        }
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.CONFIG.getName())) {
            this.mirrorForTesting = true;     
            Integer tblPosic=-1;
            if (moduleObj.getConfigVws()!=null)
                tblPosic=EnumIntViews.getViewPosicInArray(moduleObj.getConfigVws(), tblName.toUpperCase());
            if (tblPosic>-1){
                this.viewObj = moduleObj.getConfigVws()[tblPosic];
                this.found = true;
            }else{
                try {
                    this.viewObj = TblsCnfg.ViewsConfig.valueOf(tblName.toUpperCase());
                    this.found = true;
                } catch (Exception e2) {
                    this.found = false;
                    this.errorMsg = viewNotFound;
                    this.viewObj = null;
                }
            }
            if (this.found){
                String schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                    this.mirrorForTesting = true;
                }                                
            }
            return;
        }
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.DATA_AUDIT.getName())) {
            this.mirrorForTesting = true;     
            Integer tblPosic=-1;
            if (moduleObj.getDataAuditVws()!=null)
                tblPosic=EnumIntViews.getViewPosicInArray(moduleObj.getDataAuditVws(), tblName.toUpperCase());
            if (tblPosic>-1){
                this.viewObj = moduleObj.getDataAuditVws()[tblPosic];
                this.found = true;
            }else{
                try {
                    this.viewObj = null; // TblsDataAudit.ViewsDataAudit.valueOf(tblName.toUpperCase());
                    this.found = true;
                } catch (Exception e2) {
                    this.found = false;
                    this.errorMsg = viewNotFound;
                    this.viewObj = null;
                }
            }
            if (this.found){
                String schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                    this.mirrorForTesting = true;
                }                                
            }
            return;
        }
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.DATA.getName())) {
            this.mirrorForTesting = true;     
            Integer tblPosic=-1;
            if (moduleObj.getDataVws()!=null)
                tblPosic=EnumIntViews.getViewPosicInArray(moduleObj.getDataVws(), tblName.toUpperCase());
            if (tblPosic>-1){
                this.viewObj = moduleObj.getDataVws()[tblPosic];
                this.found = true;
            }else{
                try {
                    this.viewObj = TblsData.ViewsData.valueOf(tblName.toUpperCase());
                    this.found = true;
                } catch (Exception e2) {
                    this.found = false;
                    this.errorMsg = viewNotFound;
                    this.viewObj = null;
                }
            }
            if (this.found){
                String schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                    this.mirrorForTesting = true;
                }                                
            }
            return;
        }
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.PROCEDURE_AUDIT.getName())) {
            this.mirrorForTesting = true;     
            Integer tblPosic=-1;
            if (moduleObj.getProcedureAuditVws()!=null)
                tblPosic=EnumIntViews.getViewPosicInArray(moduleObj.getProcedureAuditVws(), tblName.toUpperCase());
            if (tblPosic>-1){
                this.viewObj = moduleObj.getProcedureAuditVws()[tblPosic];
                this.found = true;
            }else{
                try {
                    this.viewObj = null; //TblsProcedureAudit.ViewsProcedureAudit.valueOf(tblName.toUpperCase());
                    this.found = true;
                } catch (Exception e2) {
                    this.found = false;
                    this.errorMsg = viewNotFound;
                    this.viewObj = null;
                }
            }
            if (this.found){
                String schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                    this.mirrorForTesting = true;
                }                                
            }
            return;
        }
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.PROCEDURE.getName())) {
            this.mirrorForTesting = true;     
            Integer tblPosic=-1;
            if (moduleObj.getProcedureVws()!=null)
                tblPosic=EnumIntViews.getViewPosicInArray(moduleObj.getProcedureVws(), tblName.toUpperCase());
            if (tblPosic>-1){
                this.viewObj = moduleObj.getProcedureVws()[tblPosic];
                this.found = true;
            }else{
                try {
                    this.viewObj = TblsProcedure.ViewsProcedure.valueOf(tblName.toUpperCase());
                    this.found = true;
                } catch (Exception e2) {
                    this.found = false;
                    this.errorMsg = viewNotFound;
                    this.viewObj = null;
                }
            }
            if (this.found){
                String schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                    this.mirrorForTesting = true;
                }                                
            }
            return;
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

    public EnumIntViews getViewObj() {
        return viewObj;
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
