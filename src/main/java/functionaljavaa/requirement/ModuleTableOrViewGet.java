/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement;

import databases.Rdbms;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsDataAudit;
import databases.TblsProcedure;
import databases.TblsProcedureAudit;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMConfig;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMDataAudit;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMProcedure;
import module.inventorytrack.definition.TblsInvTrackingConfig;
import module.inventorytrack.definition.TblsInvTrackingData;
import module.inventorytrack.definition.TblsInvTrackingDataAudit;
import module.inventorytrack.definition.TblsInvTrackingProcedure;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntViews;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class ModuleTableOrViewGet {

    private Boolean found;
    private EnumIntTables tableObj;
    private EnumIntViews viewObj;
    private Boolean mirrorForTesting;
    private String errorMsg;

    public ModuleTableOrViewGet(Boolean isView, String moduleName, String curSchemaName, String tblName, String procInstanceName) {
        if (Boolean.FALSE.equals(isView)) {
            getModuleTableObj(moduleName, curSchemaName, tblName, procInstanceName);
            return;
        } else {
            getModuleViewObj(moduleName, curSchemaName, tblName);
            return;
        }
        //this.found = false;
        //this.errorMsg = "Not developed yet";
    }

    public static Boolean moduleExists(String moduleName) {
        String[] modulesListArr = new String[]{"ENVIRONMENTAL_MONITORING", "SAMPLES", "INSPECTION_LOTS_RAW_MAT",
            "GENOMIC", "INVENTORY_TRACKING"};
        return LPArray.valueInArray(modulesListArr, moduleName);
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
        Integer classesImplementingInt = -999;
        try (io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo()//.acceptPackages("com.xyz")
                .scan()) {

            ClassInfoList classesImplementing = scanResult.getClassesImplementing("trazit.enums.EnumIntTables");
            classesImplementingInt = classesImplementing.size();
        } catch (Exception e) {
            ScanResult.closeAll();
            /*            JSONArray errorJArr = new JSONArray();
            errorJArr.add("index:" + totalEndpointsVisitedInjection + audEvObjStr + "_" + evName + ":" + e.getMessage());
            LPFrontEnd.servletReturnSuccess(request, response, errorJArr);*/
            return;
        }
        ScanResult.closeAll();
    }

    public void getModuleTableObj(String moduleName, String curSchemaName, String tblName, String procInstanceName) {
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.CONFIG.getName())) {
            this.mirrorForTesting = false;
        }
        switch (moduleName) {
            case "ENVIRONMENTAL_MONITORING":
            case "SAMPLES":
                return;
            case "INSPECTION_LOTS_RAW_MAT":
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.CONFIG.getName())) {
                    try {
                        this.tableObj = TblsInspLotRMConfig.TablesInspLotRMConfig.valueOf(tblName.toUpperCase());
                        this.found = true;
                        return;
                    } catch (Exception e1) {
                        try {
                            this.tableObj = TblsCnfg.TablesConfig.valueOf(tblName.toUpperCase());
                            this.found = true;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.tableObj = null;
                        }
                    }
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.DATA_AUDIT.getName())) {
                    this.mirrorForTesting = true;
                    try {
                        this.tableObj = TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.valueOf(tblName.toUpperCase());
                        this.found = true;
                    } catch (Exception e1) {
                        try {
                            this.tableObj = TblsDataAudit.TablesDataAudit.valueOf(tblName.toUpperCase());
                            this.found = true;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.tableObj = null;
                        }
                    }
                    return;
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.DATA.getName())) {
                    this.mirrorForTesting = true;
                    try {
                        this.tableObj = TblsInspLotRMData.TablesInspLotRMData.valueOf(tblName.toUpperCase());
                        this.found = true;
                    } catch (Exception e1) {
                        try {
                            this.tableObj = TblsData.TablesData.valueOf(tblName.toUpperCase());
                            this.found = true;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.tableObj = null;
                        }
                    }
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.PROCEDURE_AUDIT.getName())) {
                    this.mirrorForTesting = true;
                    try {
                        this.tableObj = TblsProcedureAudit.TablesProcedureAudit.valueOf(tblName.toUpperCase());
                        this.found = true;
                        return;
                    } catch (Exception e1) {
                        try {
//                            this.tableObj=TblsProcedureAudit.TablesProcedure.valueOf(tblName.toUpperCase());                        
                            this.found = false;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.tableObj = null;
                        }
                    }
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.PROCEDURE.getName())) {
                    try {
                        this.tableObj = TblsInspLotRMProcedure.TablesInspLotRMProcedure.valueOf(tblName.toUpperCase());
                        this.found = true;
                        String schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                        if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                            this.mirrorForTesting = true;
                        }
                        return;
                    } catch (Exception e1) {
                        try {
                            this.tableObj = TblsProcedure.TablesProcedure.valueOf(tblName.toUpperCase());
                            this.found = true;
                            String schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                            if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                                this.mirrorForTesting = true;
                            }

                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.tableObj = null;
                        }
                    }
                }
                break;
            case "INVENTORY_TRACKING":
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.CONFIG.getName())) {
                    try {
                        this.tableObj = TblsInvTrackingConfig.TablesInvTrackingConfig.valueOf(tblName.toUpperCase());
                        this.found = true;
                        return;
                    } catch (Exception e1) {
                        try {
                            this.tableObj = TblsCnfg.TablesConfig.valueOf(tblName.toUpperCase());
                            this.found = true;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.tableObj = null;
                        }
                    }
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.DATA_AUDIT.getName())) {
                    this.mirrorForTesting = true;
                    try {
                        this.tableObj = TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.valueOf(tblName.toUpperCase());
                        this.found = true;
                    } catch (Exception e1) {
                        try {
                            this.tableObj = TblsDataAudit.TablesDataAudit.valueOf(tblName.toUpperCase());
                            this.found = true;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.tableObj = null;
                        }
                    }
                    return;
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.DATA.getName())) {
                    this.mirrorForTesting = true;
                    try {
                        this.tableObj = TblsInvTrackingData.TablesInvTrackingData.valueOf(tblName.toUpperCase());
                        this.found = true;
                    } catch (Exception e1) {
                        try {
                            this.tableObj = TblsData.TablesData.valueOf(tblName.toUpperCase());
                            this.found = true;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.tableObj = null;
                        }
                    }
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.PROCEDURE_AUDIT.getName())) {
                    this.mirrorForTesting = true;
                    try {
                        this.tableObj = TblsProcedureAudit.TablesProcedureAudit.valueOf(tblName.toUpperCase());
                        this.found = true;
                        return;
                    } catch (Exception e1) {
                        try {
//                            this.tableObj=TblsProcedureAudit.TablesProcedure.valueOf(tblName.toUpperCase());                        
                            this.found = false;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.tableObj = null;
                        }
                    }
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.PROCEDURE.getName())) {
                    try {
                        this.tableObj = TblsInvTrackingProcedure.TablesInvTrackingProcedure.valueOf(tblName.toUpperCase());
                        this.found = true;
                        String schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                        if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                            this.mirrorForTesting = true;
                        }
                        return;
                    } catch (Exception e1) {
                        try {
                            this.tableObj = TblsProcedure.TablesProcedure.valueOf(tblName.toUpperCase());
                            this.found = true;
                            String schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                            if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                                this.mirrorForTesting = true;
                            }
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.tableObj = null;
                        }
                    }
                }
                break;
            default:
        }
    }

    public void getModuleViewObj(String moduleName, String curSchemaName, String tblName) {
        getModuleViewObj(moduleName, curSchemaName, tblName, null);
    }

    public void getModuleViewObj(String moduleName, String curSchemaName, String tblName, String procInstanceName) {
        if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.CONFIG.getName())) {
            this.mirrorForTesting = false;
        }
        switch (moduleName) {
            case "ENVIRONMENTAL_MONITORING":
            case "SAMPLES":
                return;
            case "INSPECTION_LOTS_RAW_MAT":
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.CONFIG.getName())) {
                    try {
                        //this.viewObj = TblsInspLotRMConfig.TablesInspLotRMConfig.valueOf(tblName.toUpperCase());
                        this.found = true;
                        return;
                    } catch (Exception e1) {
                        try {
                            // Pero se necesita!!! this.viewObj = TblsCnfg.ViewsConfig.valueOf(tblName.toUpperCase());
                            this.found = true;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.viewObj = null;
                        }
                    }
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.DATA_AUDIT.getName())) {
                    this.mirrorForTesting = true;
                    try {
                        //this.viewObj = TblsInspLotRMDataAudit.ViewsInspLotRMDataAudit.valueOf(tblName.toUpperCase());
                        this.found = false;
                    } catch (Exception e1) {
                        try {
                            //this.viewObj = TblsDataAudit.ViewsDataAudit.valueOf(tblName.toUpperCase());
                            this.found = false;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.viewObj = null;
                        }
                    }
                    return;
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.DATA.getName())) {
                    this.mirrorForTesting = true;
                    try {
                        this.viewObj = TblsInspLotRMData.ViewsInspLotRMData.valueOf(tblName.toUpperCase());
                        this.found = true;
                    } catch (Exception e1) {
                        try {
                            this.viewObj = TblsData.ViewsData.valueOf(tblName.toUpperCase());
                            this.found = true;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.viewObj = null;
                        }
                    }
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.PROCEDURE_AUDIT.getName())) {
                    this.mirrorForTesting = true;
                    try {
                        //this.viewObj = TblsProcedureAudit.ViewsProcedureAudit.valueOf(tblName.toUpperCase());
                        this.found = false;
                        return;
                    } catch (Exception e1) {
                        try {
//                            this.viewObj=TblsProcedureAudit.TablesProcedure.valueOf(tblName.toUpperCase());                        
                            this.found = false;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.viewObj = null;
                        }
                    }
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.PROCEDURE.getName())) {
                    try {
                        //this.viewObj = TblsInspLotRMProcedure.ViewsInspLotRMProcedure.valueOf(tblName.toUpperCase());
                        this.found = false;
                        this.viewObj = TblsProcedure.ViewsProcedure.valueOf(tblName.toUpperCase());
                        this.found = true;
                        return;
                    } catch (Exception e1) {
                        try {
                            this.viewObj = TblsProcedure.ViewsProcedure.valueOf(tblName.toUpperCase());
                            this.found = true;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.viewObj = null;
                        }
                    }
                }
                break;
            case "INVENTORY_TRACKING":
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.CONFIG.getName())) {
                    try {
                        this.viewObj = TblsInvTrackingConfig.ViewsInvTrackingConfig.valueOf(tblName.toUpperCase());
                        this.found = true;
                        return;
                    } catch (Exception e1) {
                        try {
                            // pero se necesita!!! this.viewObj = TblsCnfg.ViewsConfig.valueOf(tblName.toUpperCase());
                            this.found = true;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.viewObj = null;
                        }
                    }
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.DATA_AUDIT.getName())) {
                    this.mirrorForTesting = true;
                    try {
                        //this.viewObj = TblsInvTrackingDataAudit.ViewsInvTrackingDataAudit.valueOf(tblName.toUpperCase());
                        this.found = false;
                    } catch (Exception e1) {
                        try {
                            //this.viewObj = TblsDataAudit.ViewsDataAudit.valueOf(tblName.toUpperCase());
                            this.found = false;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.viewObj = null;
                        }
                    }
                    return;
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.DATA.getName())) {
                    this.mirrorForTesting = true;
                    try {
                        this.viewObj = TblsInvTrackingData.ViewsInvTrackingData.valueOf(tblName.toUpperCase());
                        this.found = true;
                    } catch (Exception e1) {
                        try {
                            this.viewObj = TblsData.ViewsData.valueOf(tblName.toUpperCase());
                            this.found = true;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.viewObj = null;
                        }
                    }
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.PROCEDURE_AUDIT.getName())) {
                    this.mirrorForTesting = true;
                    try {
                        //this.viewObj = TblsProcedureAudit.ViewsProcedureAudit.valueOf(tblName.toUpperCase());
                        this.found = false;
                        return;
                    } catch (Exception e1) {
                        try {
//                            this.viewObj=TblsProcedureAudit.TablesProcedure.valueOf(tblName.toUpperCase());                        
                            this.found = false;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.viewObj = null;
                        }
                    }
                }
                if (curSchemaName.toLowerCase().contains(GlobalVariables.Schemas.PROCEDURE.getName())) {
                    try {
                        //this.viewObj = TblsInvTrackingProcedure.ViewsInvTrackingProcedure.valueOf(tblName.toUpperCase());
                        this.found = false;
                        this.viewObj = TblsProcedure.ViewsProcedure.valueOf(tblName.toUpperCase());
                        this.found = true;
                        String schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), tblName);
                        if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                            this.mirrorForTesting = true;
                        }
                        return;
                    } catch (Exception e1) {
                        try {
                            this.viewObj = TblsProcedure.ViewsProcedure.valueOf(tblName.toUpperCase());
                            this.found = true;
                        } catch (Exception e2) {
                            this.found = false;
                            this.errorMsg = "table not found";
                            this.viewObj = null;
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
