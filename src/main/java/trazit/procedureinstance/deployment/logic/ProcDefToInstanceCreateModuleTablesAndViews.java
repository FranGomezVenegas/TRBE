package trazit.procedureinstance.deployment.logic;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntViews;
import static trazit.enums.deployrepository.DeployTables.createTableScript;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.definition.definition.TblsReqs;

/**
 *
 * @author User
 */
public class ProcDefToInstanceCreateModuleTablesAndViews {

    /**
     *
     * @param procedure
     * @param procVersion
     * @param procInstanceName
     * @param moduleName
     * @return
     */
    public static final JSONObject createDBModuleTablesAndFields(String procedure, Integer procVersion, String procInstanceName, String moduleName) {
        JSONObject jsonObj = new JSONObject();
        try {
            Object[][] procModuleTablesAndFieldsSource = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableName(), new String[]{TblsReqs.ProcedureModuleTables.ACTIVE.getName(), TblsReqs.ProcedureModuleTables.PROCEDURE_NAME.getName(), TblsReqs.ProcedureModuleTables.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureModuleTables.PROC_INSTANCE_NAME.getName()}, new Object[]{true, procedure, procVersion, procInstanceName}, getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), new String[]{TblsReqs.ProcedureModuleTables.IS_VIEW.getName(), TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.ORDER_NUMBER.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procModuleTablesAndFieldsSource[0][0].toString())) {
                jsonObj.put(ProcedureDefinitionToInstanceSections.JsonTags.ERROR.getTagValueEn(), LPJson.convertToJSON(procModuleTablesAndFieldsSource[0]));
                return jsonObj;
            }
            jsonObj.put(ProcedureDefinitionToInstanceSections.JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValueEn(), procModuleTablesAndFieldsSource.length);
            JSONObject errorsOnlyObj = new JSONObject();
            Integer orderObj = 0;
            for (Object[] curModuleTablesAndFields : procModuleTablesAndFieldsSource) {
                JSONObject curTblJsonObj = new JSONObject();
                orderObj++;
                String tableCreationScriptTable = "";
                String curSchemaName = LPNulls.replaceNull(curModuleTablesAndFields[LPArray.valuePosicInArray(getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName())]).toString();
                String curTableName = LPNulls.replaceNull(curModuleTablesAndFields[LPArray.valuePosicInArray(getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), TblsReqs.ProcedureModuleTables.TABLE_NAME.getName())]).toString();
                String curFieldName = LPNulls.replaceNull(curModuleTablesAndFields[LPArray.valuePosicInArray(getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), TblsReqs.ProcedureModuleTables.FIELD_NAME.getName())]).toString();
                String curIsView = LPNulls.replaceNull(curModuleTablesAndFields[LPArray.valuePosicInArray(getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), TblsReqs.ProcedureModuleTables.IS_VIEW.getName())]).toString();
                String fieldsToExclude = LPNulls.replaceNull(curModuleTablesAndFields[LPArray.valuePosicInArray(getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), TblsReqs.ProcedureModuleTables.FIELDS_TO_EXCLUDE.getName())]).toString();
                curTblJsonObj.put("table_name", curTableName);
                curTblJsonObj.put("repository_name", curSchemaName);
                curTblJsonObj.put("fields_name", curFieldName);
                curTblJsonObj.put("order_index", orderObj);
                Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
                Object[] dbTableTestingExists = dbTableExists;
                String schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
                if (Boolean.FALSE.equals(schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName)))) {
                    dbTableTestingExists = Rdbms.dbTableExists(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, schemaForTesting), curTableName);
                }
                String diagn = "";
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString()) && LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableTestingExists[0].toString())) {
                    curTblJsonObj.put(GlobalAPIsParams.LBL_DIAGNOSTIC, "table already exists in this repository");
                } else {
                    diagn = "table NOT exists in this repository";
                    String tblCreateScript = null;
                    String tblCreateScriptTesting = null;
                    GlobalVariables.TrazitModules moduleObj = GlobalVariables.TrazitModules.valueOf(moduleName);
                    ModuleTableOrViewGet tblDiagn = new ModuleTableOrViewGet(Boolean.valueOf(curIsView), moduleName, curSchemaName, curTableName.toUpperCase(), procInstanceName);
                    if (curIsView == null || !Boolean.valueOf(curIsView)) {
                        //EnumIntTables moduleTableObj = getModuleTableObj(moduleName, curSchemaName, curTableName.toUpperCase());
                        if (Boolean.FALSE.equals(tblDiagn.getFound())) {
                            curTblJsonObj.put(GlobalAPIsParams.LBL_ERROR, tblDiagn.getErrorMsg());
                            //curTblJsonObj.put(GlobalAPIsParams.LBL_ERROR, tableCreationScriptTable);
                        } else {
                            tblCreateScript = createTableScript(tblDiagn.getTableObj(), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true, fieldsToExclude);
                            if (Boolean.TRUE.equals(tblDiagn.getMirrorForTesting())) {
                                tblCreateScriptTesting = createTableScript(tblDiagn.getTableObj(), schemaForTesting, false, true, fieldsToExclude);
                            }
                        }
                    } else {
                        if (Boolean.FALSE.equals(tblDiagn.getFound())) {
                            curTblJsonObj.put(GlobalAPIsParams.LBL_ERROR, tblDiagn.getErrorMsg());
                            //curTblJsonObj.put(GlobalAPIsParams.LBL_ERROR, tableCreationScriptTable);
                        } else {
                            tblCreateScript = EnumIntViews.getViewScriptCreation(tblDiagn.getViewObj(), procInstanceName, false, true, false, fieldsToExclude);
                            if (Boolean.TRUE.equals(tblDiagn.getMirrorForTesting())) {
                                tblCreateScriptTesting = EnumIntViews.getViewScriptCreation(tblDiagn.getViewObj(), procInstanceName, false, true, true, fieldsToExclude);
                            }
                        }
                    }
                    if (tblCreateScript!=null&&tblCreateScript.length() > 0) {
                        Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curSchemaName, curTableName, tblCreateScript, new Object[]{});
                        if ("-999".equalsIgnoreCase(prepUpQuery[0].toString())) {
                            diagn = diagn + " and not created, " + prepUpQuery[prepUpQuery.length - 1];
                        } else {
                            diagn = diagn + " and created";
                        }
                        curTblJsonObj.put(GlobalAPIsParams.LBL_DIAGNOSTIC, diagn);
                        JSONObject scriptLog = new JSONObject();
                        if (!(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE) || tblCreateScript.toLowerCase().startsWith("view")) && !tblCreateScript.toLowerCase().contains("already")) {
                            scriptLog.put("1) creator_diagn", prepUpQuery[prepUpQuery.length - 1]);
                        }
                        scriptLog.put("1) script", tblCreateScript);
                        if (Boolean.TRUE.equals(tblDiagn.getMirrorForTesting())) {
                            schemaForTesting = Rdbms.suffixForTesting(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
                            curTblJsonObj.put("requires_testing_clone", true);
                            Object[] prepUpQueryTesting = Rdbms.prepUpQueryWithDiagn(curSchemaName, curTableName, tblCreateScriptTesting, new Object[]{});
                            scriptLog.put("2) script_testing", tblCreateScriptTesting);
                            if (!(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE) || tblCreateScript.toLowerCase().startsWith("view")) && !tblCreateScriptTesting.toLowerCase().contains("already")) {
                                scriptLog.put("2) creator_diagn_testing", prepUpQuery[prepUpQueryTesting.length - 1]);
                            }
                        } else {
                            curTblJsonObj.put("requires_testing_clone", false);
                        }
                        if (prepUpQuery[prepUpQuery.length - 1].toString().toLowerCase().contains(GlobalAPIsParams.LBL_ERROR)) {
                            errorsOnlyObj.put(curSchemaName + "." + curTableName, scriptLog);
                        }
                        curTblJsonObj.put("scripts_detail", scriptLog);
                    }
                }
                jsonObj.put(curSchemaName + "-" + curTableName, curTblJsonObj);
            }
            return jsonObj;
        } catch (Exception e) {
            JSONObject jErr = new JSONObject();
            jErr.put("log_before_error", jsonObj);
            jErr.put(GlobalAPIsParams.LBL_ERROR, e.getMessage());
        }
        return jsonObj;
    }
    
}
