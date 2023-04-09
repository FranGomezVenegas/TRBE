package functionaljavaa.requirement;

import com.labplanet.servicios.app.AppProcedureListAPI.elementType;
import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.app.TestingRegressionUAT;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig.TablesEnvMonitConfig;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfigAudit.TablesEnvMonitConfigAudit;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData.TablesEnvMonitData;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData.ViewsEnvMonData;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitDataAudit.TablesEnvMonitDataAudit;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitProcedure.TablesEnvMonitProcedure;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import static databases.Rdbms.insertRecordInTableFromTable;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.SqlWhere;
import databases.TblsApp;
import databases.TblsAppConfig;
import databases.TblsCnfg;
import databases.TblsCnfg.TablesConfig;
import databases.TblsCnfgAudit.TablesCfgAudit;
import databases.TblsData.TablesData;
import databases.TblsData.ViewsData;
import databases.TblsDataAudit.TablesDataAudit;
import databases.TblsProcedure;
import databases.TblsProcedure.TablesProcedure;
import databases.TblsProcedure.ViewsProcedure;
import databases.TblsProcedureAudit;
import databases.TblsProcedureAudit.TablesProcedureAudit;
import databases.TblsProcedureConfig.TablesProcedureConfig;
import databases.TblsReqs;
import databases.features.DbEncryption;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.requirement.masterdata.ClassMasterData;
import java.util.Arrays;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform.LpPlatformBusinessRules;
import module.instrumentsmanagement.definition.TblsInstrumentsConfig.TablesInstrumentsConfig;
import module.instrumentsmanagement.definition.TblsInstrumentsData.TablesInstrumentsData;
import module.instrumentsmanagement.definition.TblsInstrumentsData.ViewsInstrumentsData;
import module.instrumentsmanagement.definition.TblsInstrumentsDataAudit.TablesInstrumentsDataAudit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntViews;
import static trazit.enums.deployrepository.DeployTables.createTableScript;
import trazit.globalvariables.GlobalVariables;

public class ProcedureDefinitionToInstance {

    public static EnumIntViews[] getModuleViewObj(String getModuleViewObj, String tblName) {
        return null;
    }

    public static final String[] ProcedureAuditSchema_TablesWithNoTestingClone = new String[]{TblsProcedureAudit.TablesProcedureAudit.PROC_HASH_CODES.getTableName()};
    public static final String[] ProcedureSchema_TablesWithNoTestingClone = new String[]{TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(), TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableName(),
        TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(), TblsProcedure.ViewProcUserAndRoles.TBL.getName(), TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName()};

    private ProcedureDefinitionToInstance() {
        throw new IllegalStateException("Utility class");
    }

    public enum JsonTags {
        NO("No"), YES("Yes"), ERROR("Error"), USERS("Users"), NUM_RECORDS_IN_DEFINITION("Num Records in definition");

        private JsonTags(String tgVal) {
            this.tagValue = tgVal;
        }

        public String getTagValue() {
            return this.tagValue;
        }
        private final String tagValue;
    }
    public static final String SCHEMA_AUTHORIZATION_ROLE = "labplanet";
    public static final String FLDSTORETR_REQS_PROCINFOSRC = TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName() + "|" + TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName() + "|" + TblsReqs.ProcedureInfo.LABEL_EN.getName() + "|" + TblsReqs.ProcedureInfo.LABEL_ES.getName() + "|" + TblsReqs.ProcedureInfo.PROCEDURE_HASH_CODE.getName();
    public static final String FLDSTORETR_PROCEDURE_INFO_SOURCE = TblsProcedure.ProcedureInfo.NAME.getName() + "|" + TblsProcedure.ProcedureInfo.VERSION.getName() + "|" + TblsProcedure.ProcedureInfo.LABEL_EN.getName() + "|" + TblsProcedure.ProcedureInfo.LABEL_ES.getName() + "|" + TblsProcedure.ProcedureInfo.PROCEDURE_HASH_CODE.getName();
    public static final String FLDSTORETR_PROCEDURE_USR_ROLE_SRC = "user_name|role_name";
    public static final String FLDSTORETR_PROCEDURE_USR_ROLE_SRT = "user_name";
    public static final String FIELDS_TO_INSERT_APP_USER_PROCESS = TblsApp.UserProcess.USER_NAME.getName() + "|" + TblsApp.UserProcess.PROC_NAME.getName() + "|" + TblsApp.UserProcess.ACTIVE.getName();
    public static final String FLDSTO_INSERT_PROC_USR_ROLE_DEST = "person_name|role_name|active";
    public static final String FLDSTO_RETRIEVE_PROC_SOPMTDATA_SRC = "sop_id|sop_name|sop_version|sop_revision|current_status|expires|has_child|file_link|brief_summary";
    public static final String FLDSTO_RETRIEVE_PROC_SOPMTDATA_SRT = "sop_id";
    public static final String FLDSTO_INSERT_PROC_SOPMTDATA_DEST = "person_name|role_name|active";
    public static final String FLDSTO_RETRIEVE_PROC_EVENT_DEST = "name|role_name|sop";

    public static final JSONObject createDBProcedureInfo(String procedure, Integer procVersion, String procInstanceName) {
        JSONObject jsonErrorObj = new JSONObject();
        JSONObject jsonObj = new JSONObject();
        String schemaNameDestinationProc = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName());
        Object[][] procInfoRecordsSource = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(),
                new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName},
                FLDSTORETR_REQS_PROCINFOSRC.split("\\|"));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfoRecordsSource[0][0].toString())) {
            jsonErrorObj.put("Record in requirements", "Not exists");
            jsonErrorObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procInfoRecordsSource[0]));
        } else {
            jsonErrorObj.put("Record in requirements", "Found");
            for (Object[] curRow : procInfoRecordsSource) {
                Object[][] procInfoRecordsDestination = Rdbms.getRecordFieldsByFilter(schemaNameDestinationProc, TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(),
                        new String[]{TblsProcedure.ProcedureInfo.NAME.getName(), TblsProcedure.ProcedureInfo.VERSION.getName()}, new Object[]{procedure, procVersion},
                        FLDSTORETR_PROCEDURE_INFO_SOURCE.split("\\|"));
                if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfoRecordsDestination[0][0].toString()))) {
                    jsonErrorObj.put("Record in the new instance", "Already exists");
                } else {
                    jsonErrorObj.put("Record in new instance", "Not exists");
                    String[] fldName = FLDSTORETR_PROCEDURE_INFO_SOURCE.split("\\|");
                    Object[] fldValue = curRow;
                    if (Boolean.FALSE.equals(LPArray.valueInArray(fldName, TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName()))) {
                        fldName = LPArray.addValueToArray1D(fldName, TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName());
                        fldValue = LPArray.addValueToArray1D(fldValue, procInstanceName);
                    }
                    RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_INFO, fldName, fldValue); //, schemaNameDestinationProc
                    jsonObj = new JSONObject();
                    if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
                        jsonObj.put("Record inserted in the new instance?", true);
                        return jsonObj;
                    } else {
                        jsonObj.put("Record inserted in the new instance?", false);
                        jsonObj.put("error_detail", jsonErrorObj);
                        return jsonObj;
                    }
                }
            }
        }
        jsonObj.put("Record inserted in the instance?", false);
        jsonObj.put("error_detail", jsonErrorObj);
        return jsonObj;
    }

    private static final JSONObject createProcEventParent(String procedure, Integer procVersion, String procInstanceName, String role, String[] procEventFldNamesToGet, Object[] values) {
        JSONObject jsonObj = new JSONObject();
        String type = values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.TYPE.getName())].toString();
        String position = values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.POSITION.getName())].toString();
        if (Boolean.FALSE.equals(elementType.TWOICONS.toString().equalsIgnoreCase(type)) || Boolean.FALSE.equals("1".equalsIgnoreCase(position))) {
            return jsonObj;
        }
        values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.POSITION.getName())] = "0";
        values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.NAME.getName())] = "PARENT_" + values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.NAME.getName())].toString();
        values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.ESIGN_REQUIRED.getName())] = Boolean.valueOf(values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.ESIGN_REQUIRED.getName())].toString());
        values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.USERCONFIRM_REQUIRED.getName())] = Boolean.valueOf(values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.USERCONFIRM_REQUIRED.getName())].toString());
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS, procEventFldNamesToGet, values);
        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
            jsonObj.put("insert error log",
                    insertRecordInTable.getErrorMessageCode() + " " + Arrays.toString(insertRecordInTable.getErrorMessageVariables()));
        } else {
            jsonObj.put("parent_added", "success");
        }
        return jsonObj;
    }

    public static final JSONObject createDBProcedureEvents(String procedure, Integer procVersion, String procInstanceName) {
        SqlWhere sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureEvents.NAME, WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, "");
        RdbmsObject removeRecordInTable = Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS, sw, procInstanceName);
        Object[] insertRecordInTableFromTable = insertRecordInTableFromTable(true,
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_USER_REQS_EVENTS.getTableFields()),
                GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS_EVENTS.getTableName(),
                new String[]{TblsReqs.ProcedureUserRequirementsEvents.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirementsEvents.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUserRequirementsEvents.PROC_INSTANCE_NAME.getName()},
                new Object[]{procedure, procVersion, procInstanceName},
                LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()),
                TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableName(), getAllFieldNames(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableFields()));
        JSONObject jsonObj = new JSONObject();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTableFromTable[0].toString())) {
            jsonObj.put("error_cloning_from_requirements_to_procedure", Arrays.toString(insertRecordInTableFromTable));
            return jsonObj;
        }
        jsonObj.put("success_cloning_from_requirements_to_procedure", insertRecordInTableFromTable[insertRecordInTableFromTable.length - 2] + ":" + insertRecordInTableFromTable[insertRecordInTableFromTable.length - 1]);
//        jsonObj.put("Diagnostic from createDBProcedureEvents", insertRecordInTableFromTable[0].toString());
        String[] procEventFldNamesToGet = getAllFieldNames(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableFields());
        Object[][] procEventRows = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableName(),
                //new String[]{TblsProcedure.ProcedureEvents.ROLE_NAME.getName(), WHERECLAUSE_TYPES.OR.getSqlClause()+" "+TblsProcedure.ProcedureEvents.ROLE_NAME.getName()+" "+WHERECLAUSE_TYPES.LIKE}, 
                //new Object[]{"ALL", "%|%"}, 
                new String[]{TblsProcedure.ProcedureEvents.ROLE_NAME.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{},
                procEventFldNamesToGet);
        JSONArray multiRolejArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventRows[0][0].toString()))) {
            Object[][] procRoles = new Object[][]{{}};
            Object[][] procRolesAllRoles = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_ROLES.getTableName(),
                    new String[]{TblsReqs.ProcedureRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRoles.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName()},
                    new Object[]{procedure, procVersion, procInstanceName},
                    new String[]{TblsReqs.ProcedureRoles.ROLE_NAME.getName()});

            for (Object[] curProcEvent : procEventRows) {
                JSONObject multiRolCurEvent = new JSONObject();
                multiRolCurEvent.put("event_name",
                        curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.NAME.getName())]);
                String multiRolesLog = "";
                if ("ALL".equalsIgnoreCase(curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.ROLE_NAME.getName())].toString())) {
                    procRoles = procRolesAllRoles;
                    multiRolesLog = multiRolesLog + " as for all roles, trying addition for " + LPArray.convertArrayToString(LPArray.getColumnFromArray2D(procRoles, 0), ", ", "", true);
                } else {
                    procRoles = LPArray.array1dTo2d(curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.ROLE_NAME.getName())].toString().split("\\|"), 1);
                    multiRolesLog = multiRolesLog + " as for multiple roles, trying addition for " + LPArray.convertArrayToString(LPArray.getColumnFromArray2D(procRoles, 0), ", ", "", true);
                }
                multiRolCurEvent.put("multirole_type", multiRolesLog);
                for (int i = 0; i < procRoles.length; i++) {
                    if (i == 0) {
                        SqlWhere sqlWhere = new SqlWhere();
                        sqlWhere.addConstraint(TblsProcedure.ProcedureEvents.ROLE_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.ROLE_NAME.getName())].toString()}, "");
                        sqlWhere.addConstraint(TblsProcedure.ProcedureEvents.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.NAME.getName())]}, "");
                        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS,
                                EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS, new String[]{TblsProcedure.ProcedureEvents.ROLE_NAME.getName()}), new Object[]{procRoles[0][0].toString()}, sqlWhere, procInstanceName);
                        multiRolCurEvent.put("updated?", !LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString()));
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())) {
                            multiRolCurEvent.put("update error log", Arrays.toString(diagnoses));
                        } else {
                            JSONObject createProcEventParent = createProcEventParent(procedure, procVersion, procInstanceName, procRoles[i][0].toString(), procEventFldNamesToGet, curProcEvent);
                            multiRolCurEvent.put("adding_parent", createProcEventParent);
                        }
                    } else {

                        curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.ROLE_NAME.getName())] = procRoles[i][0].toString();
                        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS, procEventFldNamesToGet, curProcEvent);
                        multiRolCurEvent.put("inserted?", insertRecordInTable.getRunSuccess());
                        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
                            multiRolCurEvent.put("insert error log",
                                    insertRecordInTable.getErrorMessageCode() + " " + Arrays.toString(insertRecordInTable.getErrorMessageVariables()));
                        } else {
                            JSONObject createProcEventParent = createProcEventParent(procedure, procVersion, procInstanceName, procRoles[i][0].toString(), procEventFldNamesToGet, curProcEvent);
                            multiRolCurEvent.put("adding_parent", createProcEventParent);
                        }
                    }
                }
                if (procRoles.length > 1) {
                    multiRolejArr.add(multiRolCurEvent);
                }
            }
        }
        if (Boolean.FALSE.equals(multiRolejArr.isEmpty())) {
            jsonObj.put("multiroles_addition_log", multiRolejArr);
        }
        return jsonObj;
    }

    public static final JSONObject createdDBProcedureActions(String procedure, Integer procVersion, String procInstanceName) {
        JSONObject jMainObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        SqlWhere sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName()}, "");
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getAreaName()}, "");
        RdbmsObject removeRecordInTable = Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, procInstanceName);
        sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.LIKE, new Object[]{"%" + LPPlatform.LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName().toLowerCase() + "%"}, "");
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getAreaName()}, "");
        removeRecordInTable = Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, procInstanceName);
        sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.LIKE, new Object[]{"%" + LPPlatform.LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName().toLowerCase() + "%"}, "");
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.ESIGN_REQUIRED.getAreaName()}, "");
        removeRecordInTable = Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, procInstanceName);
        sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.LIKE, new Object[]{"%" + LPPlatform.LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getTagName().toLowerCase() + "%"}, "");
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getAreaName()}, "");
        removeRecordInTable = Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, procInstanceName);
        sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.LIKE, new Object[]{"%" + LPPlatform.LpPlatformBusinessRules.ACTIONCONFIRM_REQUIRED.getTagName().toLowerCase() + "%"}, "");
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.ACTIONCONFIRM_REQUIRED.getAreaName()}, "");
        removeRecordInTable = Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, procInstanceName);
        sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.LIKE, new Object[]{"%" + LPPlatform.LpPlatformBusinessRules.AUDIT_JUSTIF_REASON_REQUIRED.getTagName().toLowerCase() + "%"}, "");
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.AUDIT_JUSTIF_REASON_REQUIRED.getAreaName()}, "");
        removeRecordInTable = Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, procInstanceName);
        sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.LIKE, new Object[]{"%" + LPPlatform.LpPlatformBusinessRules.AUDITREASON_PHRASE.getTagName().toLowerCase() + "%"}, "");
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.AUDITREASON_PHRASE.getAreaName()}, "");
        removeRecordInTable = Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, procInstanceName);
        Object[][] procUsrReqs = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableName(),
                new String[]{TblsReqs.ProcedureUserRequirements.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirements.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME.getName()},
                new Object[]{procedure, procVersion, procInstanceName},
                new String[]{TblsReqs.ProcedureUserRequirements.WINDOW_ACTION.getName(), TblsReqs.ProcedureUserRequirements.ROLES.getName(),
                    TblsReqs.ProcedureUserRequirements.CONFIRM_DIALOG.getName(), TblsReqs.ProcedureUserRequirements.CONFIRM_DIALOG_DETAIL.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procUsrReqs[0][0].toString())) {
            JSONObject jObj = new JSONObject();
            jObj.put("error", "no entries in " + TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableName() + " table");
            jArr.add(jObj);
        } else {
            String allProcActionsInOne = LPArray.convertArrayToString(LPArray.getColumnFromArray2D(procUsrReqs, 0), "|", "", true);
            allProcActionsInOne = allProcActionsInOne.replace("||", "|");
            String[] fldNames = new String[]{TblsProcedure.ProcedureBusinessRules.DISABLED.getName(), TblsProcedure.ProcedureBusinessRules.AREA.getName(),
                TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName(), TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()};
            Object[] fldValues = new Object[]{false, LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getAreaName(),
                LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName(), allProcActionsInOne};
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, fldNames, fldValues);
            JSONObject jObj = new JSONObject();
            if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
                jObj.put(LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName(), allProcActionsInOne + " " + "Added");
            } else {
                jObj.put(LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName(), allProcActionsInOne + " " + "error adding" + insertRecordInTable.getErrorMessageCode());
            }
            jArr.add(jObj);
            String esigns = "";
            String verifUsers = "";
            String actionConfirm = "";
            String justifReason = "";
            for (Object[] curAction : procUsrReqs) {
                String actionName = LPNulls.replaceNull(curAction[0]).toString();
                String[] allRoles = LPNulls.replaceNull(curAction[1]).toString().split("\\|");
                if (actionName.trim().length() > 0) {
                    for (String curRole : allRoles) {
                        fldValues = new Object[]{false, LPPlatform.LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getAreaName(),
                            LPPlatform.LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName() + curAction[0].toString(), curRole};
                        insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, fldNames, fldValues);
                        jObj = new JSONObject();
                        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
                            jObj.put(LPPlatform.LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName() + curAction[0].toString(), curRole + " " + "Added");
                        } else {
                            jObj.put(LPPlatform.LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName() + curAction[0].toString(), curRole + " " + "error adding" + insertRecordInTable.getErrorMessageCode());
                        }
                        jArr.add(jObj);
                    }
                    String[] confirmDialog = LPNulls.replaceNull(curAction[2]).toString().split("\\|");
                    if (confirmDialog.length > 0) {
                        if (Arrays.toString(confirmDialog).toLowerCase().contains("esign")) {
                            if (esigns.length() > 0) {
                                esigns = esigns + "|";
                            }
                            esigns = esigns + actionName;
                        }
                        if (Arrays.toString(confirmDialog).toLowerCase().contains("user")) {
                            if (verifUsers.length() > 0) {
                                verifUsers = verifUsers + "|";
                            }
                            verifUsers = verifUsers + actionName;
                        }
                        if (Arrays.toString(confirmDialog).toLowerCase().contains("confirm")) {
                            if (actionConfirm.length() > 0) {
                                actionConfirm = actionConfirm + "|";
                            }
                            actionConfirm = actionConfirm + actionName;
                        }
                        if (Arrays.toString(confirmDialog).toLowerCase().contains("justif")) {
                            if (justifReason.length() > 0) {
                                justifReason = justifReason + "|";
                            }
                            justifReason = justifReason + actionName;
                        }
                    }
                    String[] confirmDialogDetail = LPNulls.replaceNull(curAction[3]).toString().split("\\|");
                    String confirmDialogPhrase = curAction[3].toString();
                    if (confirmDialogPhrase.length() > 0) {
                        fldValues = new Object[]{false, LPPlatform.LpPlatformBusinessRules.AUDITREASON_PHRASE.getAreaName(),
                            actionName + LPPlatform.LpPlatformBusinessRules.AUDITREASON_PHRASE.getTagName(), confirmDialogPhrase};
                        insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, fldNames, fldValues);
                        jObj = new JSONObject();
                        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
                            jObj.put(actionName + LPPlatform.LpPlatformBusinessRules.AUDITREASON_PHRASE.getTagName(), confirmDialogPhrase + " " + "Added");
                        } else {
                            jObj.put(actionName + LPPlatform.LpPlatformBusinessRules.AUDITREASON_PHRASE.getTagName(), confirmDialogPhrase + " " + "error adding" + insertRecordInTable.getErrorMessageCode());
                        }
                        jArr.add(jObj);
                    }
                }
            }
            if (esigns.length() > 0) {
                fldValues = new Object[]{false, LPPlatform.LpPlatformBusinessRules.ESIGN_REQUIRED.getAreaName(),
                    LPPlatform.LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName(), esigns};
                insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, fldNames, fldValues);
                jObj = new JSONObject();
                if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
                    jObj.put(LPPlatform.LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName(), esigns + " " + "Added");
                } else {
                    jObj.put(LPPlatform.LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName(), esigns + " " + "error adding" + insertRecordInTable.getErrorMessageCode());
                }
                jArr.add(jObj);
            }
            if (verifUsers.length() > 0) {
                fldValues = new Object[]{false, LPPlatform.LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getAreaName(),
                    LPPlatform.LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getTagName(), verifUsers};
                insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, fldNames, fldValues);
                jObj = new JSONObject();
                if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
                    jObj.put(LPPlatform.LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getTagName(), verifUsers + " " + "Added");
                } else {
                    jObj.put(LPPlatform.LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getTagName(), verifUsers + " " + "error adding" + insertRecordInTable.getErrorMessageCode());
                }
                jArr.add(jObj);
            }
            if (actionConfirm.length() > 0) {
                fldValues = new Object[]{false, LPPlatform.LpPlatformBusinessRules.ACTIONCONFIRM_REQUIRED.getAreaName(),
                    LPPlatform.LpPlatformBusinessRules.ACTIONCONFIRM_REQUIRED.getTagName(), actionConfirm};
                insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, fldNames, fldValues);
                jObj = new JSONObject();
                if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
                    jObj.put(LPPlatform.LpPlatformBusinessRules.ACTIONCONFIRM_REQUIRED.getTagName(), actionConfirm + " " + "Added");
                } else {
                    jObj.put(LPPlatform.LpPlatformBusinessRules.ACTIONCONFIRM_REQUIRED.getTagName(), actionConfirm + " " + "error adding" + insertRecordInTable.getErrorMessageCode());
                }
                jArr.add(jObj);
            }
            if (justifReason.length() > 0) {
                fldValues = new Object[]{false, LPPlatform.LpPlatformBusinessRules.AUDIT_JUSTIF_REASON_REQUIRED.getAreaName(),
                    LPPlatform.LpPlatformBusinessRules.AUDIT_JUSTIF_REASON_REQUIRED.getTagName(), justifReason};
                insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, fldNames, fldValues);
                jObj = new JSONObject();
                if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
                    jObj.put(LPPlatform.LpPlatformBusinessRules.AUDIT_JUSTIF_REASON_REQUIRED.getTagName(), justifReason + " " + "Added");
                } else {
                    jObj.put(LPPlatform.LpPlatformBusinessRules.AUDIT_JUSTIF_REASON_REQUIRED.getTagName(), justifReason + " " + "error adding" + insertRecordInTable.getErrorMessageCode());
                }
                jArr.add(jObj);
            }
//          procedureActionsAndRoles(procInstanceName, ProcedureDefinitionQueries.ProcBusinessRulesQueries bsnRuleQry, mainObj);
        }
        jMainObj.put("createdDBProcedureActions", jArr);
        return jMainObj;
    }

    /**
     *
     * @param procedure
     * @param procVersion
     * @param procInstanceName
     * @return
     */
    public static final JSONObject createDBPersonProfiles(String procedure, Integer procVersion, String procInstanceName) {
        String defaultMail = "info@trazit.net";
        String pasEsingn = "trazit4ever";
        Object[] encryptValue = DbEncryption.encryptValue(pasEsingn);
        String pasEncrypted = encryptValue[encryptValue.length - 1].toString();
        String fakeEsingn = "firmademo";
        encryptValue = DbEncryption.encryptValue(fakeEsingn);
        String fakeEsingnEncrypted = encryptValue[encryptValue.length - 1].toString();

        JSONObject jsonObj = new JSONObject();
        String schemaNameDestinationProcedure = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName());
        Object[][] procUserRolesRecordsSource = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_USER_ROLES.getTableName(),
                new String[]{TblsReqs.ProcedureUserRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRoles.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName},
                FLDSTORETR_PROCEDURE_USR_ROLE_SRC.split("\\|"), FLDSTORETR_PROCEDURE_USR_ROLE_SRT.split("\\|"));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserRolesRecordsSource[0][0].toString())) {
            jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procUserRolesRecordsSource[0]));
            return jsonObj;
        }
        for (Object[] curRow : procUserRolesRecordsSource) {
            Object curUserName = curRow[LPArray.valuePosicInArray(FLDSTORETR_PROCEDURE_USR_ROLE_SRC.split("\\|"), TblsReqs.ProcedureUserRoles.USER_NAME.getName())];
            Object curRoleName = curRow[LPArray.valuePosicInArray(FLDSTORETR_PROCEDURE_USR_ROLE_SRC.split("\\|"), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName())];
            JSONArray jsArr = new JSONArray();
            JSONObject jsUserRoleObj = new JSONObject();
            jsUserRoleObj.put("User", curUserName);
            jsUserRoleObj.put("Role", curRoleName);

            Object[][] existsAppUser = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(),
                    new String[]{TblsApp.Users.USER_NAME.getName()}, new Object[]{curUserName.toString()}, new String[]{TblsApp.Users.PERSON_NAME.getName()});
            String diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())) ? JsonTags.NO.getTagValue() : JsonTags.YES.getTagValue();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())) {
                String personId = curUserName + "z";

                RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsAppConfig.TablesAppConfig.PERSON,
                        new String[]{TblsAppConfig.Person.PERSON_ID.getName(), TblsAppConfig.Person.FIRST_NAME.getName(),
                            TblsAppConfig.Person.LAST_NAME.getName(), TblsAppConfig.Person.PHOTO.getName()},
                        new Object[]{personId, "I'm a user demo", "for demos ", "https://hasta-pronto.ru/wp-content/uploads/2014/09/chibcha.jpg"});
                insertRecordInTable = Rdbms.insertRecordInTable(TblsApp.TablesApp.USERS,
                        new String[]{TblsApp.Users.USER_NAME.getName(), TblsApp.Users.EMAIL.getName(), TblsApp.Users.ESIGN.getName(),
                            TblsApp.Users.PASSWORD.getName(), TblsApp.Users.PERSON_NAME.getName()},
                        new Object[]{curUserName, defaultMail, fakeEsingnEncrypted, pasEncrypted, personId});
                existsAppUser = LPArray.array1dTo2d(insertRecordInTable.getApiMessage(), 1);
                diagnosesForLog = diagnosesForLog + " trying to create, log for creation=" + insertRecordInTable.getApiMessage()[0].toString();
//                insertRecordInTable=Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USER_PROCESS.getTableName(), 
//                    new String[]{TblsApp.UserProcess.USER_NAME.getName(), TblsApp.UserProcess.PROC_NAME.getName(), TblsApp.UserProcess.ACTIVE.getName()}, 
//                    new Object[]{curUserName, fakeProcName, true});

                // Place to create the user
            }
            jsUserRoleObj.put("User exists in the app after running this logic?", LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesForLog));
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(existsAppUser[0])))) {
                Object[] existsAppUserProcess = Rdbms.existsRecord(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USER_PROCESS.getTableName(),
                        new String[]{TblsApp.UserProcess.USER_NAME.getName(), TblsApp.UserProcess.PROC_NAME.getName()}, new Object[]{curUserName.toString(), procInstanceName});
                jsonObj.put("User was added to the Process at the App level?", LPPlatform.LAB_TRUE.equalsIgnoreCase(existsAppUserProcess[0].toString()));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUserProcess[0].toString())) {
                    RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsApp.TablesApp.USER_PROCESS,
                            FIELDS_TO_INSERT_APP_USER_PROCESS.split("\\|"), new Object[]{curUserName.toString(), procInstanceName, true});
                    jsonObj.put("Added the User to the Process at the App level by running this utility?", insertRecordInTable.getApiMessage()[0].toString());
                }
            }
            Object curPersonName = existsAppUser[0][0];
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString()))) {
                RdbmsObject insertRecord = Rdbms.insertRecord(TblsProcedure.TablesProcedure.PERSON_PROFILE,
                        FLDSTO_INSERT_PROC_USR_ROLE_DEST.split("\\|"), new Object[]{curPersonName.toString(), curRoleName.toString(), true}, schemaNameDestinationProcedure);
                jsonObj.put("User Role inserted in the instance?", insertRecord.getApiMessage()[0].toString());
            }
            jsArr.add(jsUserRoleObj);
            jsonObj.put("User " + curUserName + " & Role " + curRoleName, jsArr);
        }
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procUserRolesRecordsSource.length);
        return jsonObj;
    }

    /**
     *
     * @param procedure
     * @param procVersion
     * @param procInstanceName
     * @return
     */
    public static final JSONObject createDBSopMetaDataAndUserSop(String procedure, Integer procVersion, String procInstanceName) {
        JSONObject jsonObj = new JSONObject();
        String schemaNameDestination = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());

        Object[][] procSopMetaDataRecordsSource = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableName(),
                new String[]{TblsReqs.ProcedureSopMetaData.PROCEDURE_NAME.getName(), TblsReqs.ProcedureSopMetaData.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureSopMetaData.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName},
                FLDSTO_RETRIEVE_PROC_SOPMTDATA_SRC.split("\\|"), FLDSTO_RETRIEVE_PROC_SOPMTDATA_SRT.split("\\|"));

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procSopMetaDataRecordsSource[0][0].toString())) {
            jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procSopMetaDataRecordsSource));
            return jsonObj;
        }
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procSopMetaDataRecordsSource.length);
        for (Object[] curSopMetaData : procSopMetaDataRecordsSource) {
            Object curSopId = curSopMetaData[LPArray.valuePosicInArray(FLDSTO_RETRIEVE_PROC_SOPMTDATA_SRC.split("\\|"), TblsCnfg.SopMetaData.SOP_ID.getName())];
            Object curSopName = curSopMetaData[LPArray.valuePosicInArray(FLDSTO_RETRIEVE_PROC_SOPMTDATA_SRC.split("\\|"), TblsCnfg.SopMetaData.SOP_NAME.getName())];
            JSONArray jsArr = new JSONArray();
            JSONObject jsUserRoleObj = new JSONObject();
            jsUserRoleObj.put("SOP Id", curSopId);
            jsUserRoleObj.put("SOP Name", curSopName);

            Object[][] existsAppUser = Rdbms.getRecordFieldsByFilter(schemaNameDestination, TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(),
                    new String[]{TblsCnfg.SopMetaData.SOP_NAME.getName()}, new Object[]{curSopName.toString()}, new String[]{TblsCnfg.SopMetaData.SOP_NAME.getName()});
            String diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())) ? JsonTags.NO.getTagValue() : JsonTags.YES.getTagValue();
            jsUserRoleObj.put("SOP exists in the procedure?", diagnosesForLog);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())) {
                RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsCnfg.TablesConfig.SOP_META_DATA,
                        FLDSTO_RETRIEVE_PROC_SOPMTDATA_SRC.split("\\|"), curSopMetaData);
                diagnosesForLog = (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) ? JsonTags.NO.getTagValue() : JsonTags.YES.getTagValue();
                jsonObj.put("SOP inserted in the instance?", diagnosesForLog);
                //if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTable[0].toString())){}
            }
            jsArr.add(jsUserRoleObj);
            jsonObj.put("SOP Id " + curSopId + " & SOP Name " + curSopName, jsArr);
        }
        return jsonObj;
    }

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

            Object[][] procModuleTablesAndFieldsSource = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableName(),
                    new String[]{TblsReqs.ProcedureModuleTables.ACTIVE.getName(), TblsReqs.ProcedureModuleTables.PROCEDURE_NAME.getName(), TblsReqs.ProcedureModuleTables.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureModuleTables.PROC_INSTANCE_NAME.getName()},
                    new Object[]{true, procedure, procVersion, procInstanceName},
                    getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()),
                    new String[]{TblsReqs.ProcedureModuleTables.IS_VIEW.getName(), TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.ORDER_NUMBER.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procModuleTablesAndFieldsSource[0][0].toString())) {
                jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procModuleTablesAndFieldsSource[0]));
                return jsonObj;
            }
            jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procModuleTablesAndFieldsSource.length);
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
                curTblJsonObj.put("table_name", curTableName);
                curTblJsonObj.put("repository_name", curSchemaName);
                curTblJsonObj.put("fields_name", curFieldName);
                curTblJsonObj.put("order_index", orderObj);
                Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
                Object[] dbTableTestingExists = dbTableExists;
                String schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
                if (Boolean.FALSE.equals(schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) ) {
                    dbTableTestingExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, schemaForTesting), curTableName);
                }
                String diagn = "";
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString()) && LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableTestingExists[0].toString())) {
                    curTblJsonObj.put(GlobalAPIsParams.LBL_DIAGNOSTIC, "table already exists in this repository");
                } else {
                    diagn = "table NOT exists in this repository";
                    String tblCreateScript = null;
                    String tblCreateScriptTesting = null;
                    switch (moduleName.toUpperCase()) {
                        case "ENVIRONMENTAL_MONITORING":
                            Boolean cont = true;
                            try {
                                switch (curSchemaName.toLowerCase()) {
                                    case "config":
                                    try {
                                        tblCreateScript = createTableScript(TablesEnvMonitConfig.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    } catch (Exception e) {
                                        tblCreateScript = createTableScript(TablesConfig.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    }
                                    break;
                                    case "config-audit":
                                    try {
                                        tblCreateScript = createTableScript(TablesEnvMonitConfigAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    } catch (Exception e) {
                                        tblCreateScript = createTableScript(TablesCfgAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    }
                                    break;
                                    case "data":
                                        if (curIsView == null || Boolean.FALSE.equals(Boolean.valueOf(curIsView))) {
                                            try {
                                                tblCreateScript = createTableScript(TablesEnvMonitData.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                                tblCreateScriptTesting = createTableScript(TablesEnvMonitData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                            } catch (Exception e) {
                                                tblCreateScript = createTableScript(TablesData.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                                tblCreateScriptTesting = createTableScript(TablesData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                            }
                                        } else {
                                            try {
                                                tblCreateScript = EnumIntViews.getViewScriptCreation(TblsEnvMonitData.ViewsEnvMonData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                                tblCreateScriptTesting = EnumIntViews.getViewScriptCreation(TblsEnvMonitData.ViewsEnvMonData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                            } catch (Exception e) {
                                                tblCreateScript = EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                                tblCreateScriptTesting = EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                            }

                                            /*                                        try{
                                            tblCreateScript=EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                            tblCreateScriptTesting=EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                            if (tblCreateScript.length()==0){
                                                tblCreateScript = ViewsEnvMonData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                                tblCreateScriptTesting = createTableScript(TablesEnvMonitData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                            }
                                        }catch(Exception e){
                                            tblCreateScript = ViewsData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                        }    */
                                        }
                                        break;
                                    case "data-audit":
                                    try {
                                        tblCreateScript = createTableScript(TablesEnvMonitDataAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        tblCreateScriptTesting = createTableScript(TablesEnvMonitDataAudit.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                    } catch (Exception e) {
                                        tblCreateScript = createTableScript(TablesDataAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        tblCreateScriptTesting = createTableScript(TablesDataAudit.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                    }
                                    break;
                                    case "procedure":
                                        if (curIsView == null || Boolean.FALSE.equals(Boolean.valueOf(curIsView)) ){
                                            try {
                                                tblCreateScript = createTableScript(TablesEnvMonitProcedure.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                                if (Boolean.FALSE.equals(schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName)))) {
                                                    tblCreateScriptTesting = createTableScript(TablesEnvMonitProcedure.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                                }
                                            } catch (Exception e) {
                                                tblCreateScript = createTableScript(TablesProcedure.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                                if (Boolean.FALSE.equals(schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName)))) {
                                                    tblCreateScriptTesting = createTableScript(TablesProcedure.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                                }
                                            }
                                        } else {
                                            try {
                                                tblCreateScript = EnumIntViews.getViewScriptCreation(ViewsProcedure.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                                tblCreateScriptTesting = EnumIntViews.getViewScriptCreation(ViewsProcedure.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                                if (tblCreateScript.length() == 0) {
                                                    tblCreateScript = ViewsEnvMonData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                                    tblCreateScriptTesting = createTableScript(TablesEnvMonitData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                                }
                                            } catch (Exception e) {
                                                tblCreateScript = ViewsData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                            }
                                        }
                                        break;
                                    case "procedure-config":
                                        if (curIsView == null || Boolean.FALSE.equals(Boolean.valueOf(curIsView))) {
                                            tblCreateScript = createTableScript(TablesProcedureConfig.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                            if (Boolean.FALSE.equals(schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) ) {
                                                tblCreateScriptTesting = createTableScript(TablesProcedureConfig.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                            }
                                        } else {
                                            //Commented out due to it does not exist any view yet.
/*                                        try{
                                            tblCreateScript=EnumIntViews.getViewScriptCreation(ViewsProcedureConfig.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                            tblCreateScriptTesting=EnumIntViews.getViewScriptCreation(ViewsProcedure.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                            if (tblCreateScript.length()==0){
                                                tblCreateScript = ViewsEnvMonData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                                tblCreateScriptTesting = createTableScript(TablesEnvMonitData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                            }
                                        }catch(Exception e){
                                            tblCreateScript = ViewsData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                        }                                        */
                                        }
                                        break;
                                    case "procedure-audit":
                                    try {
                                        tblCreateScript = createTableScript(TablesProcedureAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        if (Boolean.FALSE.equals(schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) ){
                                            tblCreateScriptTesting = createTableScript(TablesProcedureAudit.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                        }
                                    } catch (Exception e) {
                                    }
                                    break;
                                    default:
                                        cont = false;
                                        curTblJsonObj.put("unexpected_error", "repository " + curSchemaName + " not recognized");
                                }
                            } catch (Exception e) {
                                cont = false;
                                curTblJsonObj.put("unexpected_error", e.getMessage());
                            }
                            if (Boolean.TRUE.equals(cont)){
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

                                schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
                                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName)) && tblCreateScriptTesting != null) {
                                    curTblJsonObj.put("requires_testing_clone", true);
                                    Object[] prepUpQueryTesting = Rdbms.prepUpQueryWithDiagn(curSchemaName, curTableName, tblCreateScriptTesting, new Object[]{});
                                    scriptLog.put("2) script_testing", tblCreateScriptTesting);

                                    if (!(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE) || tblCreateScript.toLowerCase().startsWith("view")) && !tblCreateScriptTesting.toLowerCase().contains("already")) {
                                        scriptLog.put("2) creator_diagn_testing", prepUpQuery[prepUpQueryTesting.length - 1]);
                                    }
                                } else {
                                    curTblJsonObj.put("requires_testing_clone", false);
                                }

                                if (prepUpQuery[prepUpQuery.length - 1].toString().toLowerCase().contains("error")) {
                                    errorsOnlyObj.put(curSchemaName + "." + curTableName, scriptLog);
                                }
                                curTblJsonObj.put("scripts_detail", scriptLog);
                            }
                            /*                    if (GlobalVariables.Schemas.CONFIG.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitConfig.getTableCreationScriptFromConfigTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.CONFIG_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitConfigAudit.getTableCreationScriptFromConfigAuditTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.DATA.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitData.getTableCreationScriptFromDataTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.DATA_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitDataAudit.getTableCreationScriptFromDataAuditTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.PROCEDURE.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitProcedure.getTableCreationScriptFromDataProcedureTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                             */ break;
                        case "INSTRUMENTS":
                            cont = true;
                            try {
                                switch (curSchemaName.toLowerCase()) {
                                    case "config":
                                    try {
                                        tblCreateScript = createTableScript(TablesInstrumentsConfig.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    } catch (Exception e) {
                                        tblCreateScript = createTableScript(TablesConfig.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    }
                                    break;
                                    case "config-audit":
                                    try {
                                        tblCreateScript = "TablesAppProcConfigAudit collection not exists";
                                        //tblCreateScript = createTableScript(TablesInstrumentsConfigAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    } catch (Exception e) {
                                        tblCreateScript = createTableScript(TablesCfgAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    }
                                    break;
                                    case "data":
                                        if (curIsView == null || !Boolean.valueOf(curIsView)) {
                                            try {
                                                tblCreateScript = createTableScript(TablesInstrumentsData.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                                tblCreateScriptTesting = createTableScript(TablesInstrumentsData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                            } catch (Exception e) {
                                                tblCreateScript = createTableScript(TablesData.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                                tblCreateScriptTesting = createTableScript(TablesData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                            }
                                        } else {
                                            try {
                                                tblCreateScript = EnumIntViews.getViewScriptCreation(ViewsInstrumentsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                                tblCreateScriptTesting = EnumIntViews.getViewScriptCreation(ViewsInstrumentsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                                if (tblCreateScript.length() == 0) {
                                                    tblCreateScript = EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                                    tblCreateScriptTesting = EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                                }
                                            } catch (Exception e) {
                                                tblCreateScript = EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                                tblCreateScriptTesting = EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                            }
                                        }
                                        break;
                                    case "data-audit":
                                    try {
                                        tblCreateScript = createTableScript(TablesInstrumentsDataAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        tblCreateScriptTesting = createTableScript(TablesInstrumentsDataAudit.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                    } catch (Exception e) {
                                        tblCreateScript = createTableScript(TablesDataAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        tblCreateScriptTesting = createTableScript(TablesDataAudit.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                    }
                                    break;
                                    case "procedure":
                                        if (curIsView == null || !Boolean.valueOf(curIsView)) {
                                            try {
                                                tblCreateScript = "TablesInstrumentsProcedure collection not exists";
                                                //tblCreateScript = createTableScript(TablesInstrumentsProcedure.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                                                    tblCreateScript = "TablesInstrumentsProcedure collection not exists";
                                                }
                                                //    tblCreateScriptTesting = createTableScript(TablesInstrumentsProcedure.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                            } catch (Exception e) {
                                                tblCreateScript = createTableScript(TablesProcedure.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                                                    tblCreateScriptTesting = createTableScript(TablesProcedure.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                                }
                                            }
                                        } else {
                                            try {
                                                tblCreateScript = EnumIntViews.getViewScriptCreation(ViewsProcedure.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                                tblCreateScriptTesting = EnumIntViews.getViewScriptCreation(ViewsProcedure.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                                if (tblCreateScript.length() == 0) {
//                                                tblCreateScript = ViewsInstrumentsData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
//                                                tblCreateScriptTesting = createTableScript(ViewsProcedure.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                                }
                                            } catch (Exception e) {
                                                tblCreateScript = "";//ViewsData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                            }
                                        }
                                        break;
                                    case "procedure-audit":
                                    try {
                                        tblCreateScript = createTableScript(TablesProcedureAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                                            tblCreateScriptTesting = createTableScript(TablesProcedureAudit.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                        }
                                    } catch (Exception e) {
                                    }
                                    break;
                                    default:
                                        cont = false;
                                        curTblJsonObj.put("unexpected_error", "repository " + curSchemaName + " not recognized");
                                }
                            } catch (Exception e) {
                                cont = false;
                                curTblJsonObj.put("unexpected_error", e.getMessage());
                            }
                            if (Boolean.TRUE.equals(cont)){
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

                                schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
                                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName)) && tblCreateScriptTesting != null) {
                                    curTblJsonObj.put("requires_testing_clone", true);
                                    Object[] prepUpQueryTesting = Rdbms.prepUpQueryWithDiagn(curSchemaName, curTableName, tblCreateScriptTesting, new Object[]{});
                                    scriptLog.put("2) script_testing", tblCreateScriptTesting);

                                    if (!(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE) || tblCreateScript.toLowerCase().startsWith("view")) && !tblCreateScriptTesting.toLowerCase().contains("already")) {
                                        scriptLog.put("2) creator_diagn_testing", prepUpQuery[prepUpQueryTesting.length - 1]);
                                    }
                                } else {
                                    curTblJsonObj.put("requires_testing_clone", false);
                                }

                                if (prepUpQuery[prepUpQuery.length - 1].toString().toLowerCase().contains("error")) {
                                    errorsOnlyObj.put(curSchemaName + "." + curTableName, scriptLog);
                                }
                                curTblJsonObj.put("scripts_detail", scriptLog);
                            }
                            /*                    if (GlobalVariables.Schemas.CONFIG.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitConfig.getTableCreationScriptFromConfigTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.CONFIG_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitConfigAudit.getTableCreationScriptFromConfigAuditTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.DATA.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitData.getTableCreationScriptFromDataTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.DATA_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitDataAudit.getTableCreationScriptFromDataAuditTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.PROCEDURE.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitProcedure.getTableCreationScriptFromDataProcedureTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                             */ break;
                        case "SAMPLES":
                            cont = true;
                            try {
                                switch (curSchemaName.toLowerCase()) {
                                    case "config":
                                        tblCreateScript = createTableScript(TablesConfig.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        break;
                                    case "config-audit":
                                        tblCreateScript = createTableScript(TablesCfgAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        break;
                                    case "data":
                                        if (curIsView == null || !Boolean.valueOf(curIsView)) {
                                            tblCreateScript = createTableScript(TablesData.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                            tblCreateScriptTesting = createTableScript(TablesData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                        } else {
                                            //tblCreateScript = ViewsData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                            try {
                                                tblCreateScript = EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                                tblCreateScriptTesting = EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                                if (tblCreateScript.length() == 0) {
                                                    tblCreateScript = ViewsEnvMonData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                                    tblCreateScriptTesting = createTableScript(TablesEnvMonitData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                                }
                                            } catch (Exception e) {
                                                tblCreateScript = ViewsData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                            }
                                        }
                                        break;
                                    case "data-audit":
                                        tblCreateScript = createTableScript(TablesDataAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        tblCreateScriptTesting = createTableScript(TablesDataAudit.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                        break;
                                    case "procedure":
                                        if (curIsView == null || !Boolean.valueOf(curIsView)) {
                                            tblCreateScript = createTableScript(TablesProcedure.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                            if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                                                tblCreateScriptTesting = createTableScript(TablesProcedure.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                            }
                                        } else {
                                            try {
                                                tblCreateScript = EnumIntViews.getViewScriptCreation(ViewsProcedure.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                                tblCreateScriptTesting = EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                                if (tblCreateScript.length() == 0) {
                                                    tblCreateScript = ViewsEnvMonData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                                    tblCreateScriptTesting = createTableScript(TablesProcedure.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                                }
                                            } catch (Exception e) {
                                                tblCreateScript = ViewsData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                            }
                                        }
                                        break;
                                    case "procedure-audit":
                                    try {
                                        tblCreateScript = createTableScript(TablesProcedureAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))) {
                                            tblCreateScriptTesting = createTableScript(TablesProcedureAudit.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                        }
                                    } catch (Exception e) {
                                    }
                                    break;
                                    default:
                                        cont = false;
                                        curTblJsonObj.put("unexpected_error", "repository " + curSchemaName + " not recognized");
                                }
                            } catch (Exception e) {
                                cont = false;
                                curTblJsonObj.put("unexpected_error", e.getMessage());
                            }
                            if (Boolean.TRUE.equals(cont)){
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

                                schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
                                if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName)) && tblCreateScriptTesting != null) {
                                    curTblJsonObj.put("requires_testing_clone", true);
                                    Object[] prepUpQueryTesting = Rdbms.prepUpQueryWithDiagn(curSchemaName, curTableName, tblCreateScriptTesting, new Object[]{});
                                    scriptLog.put("2) script_testing", tblCreateScriptTesting);
                                    if (!tblCreateScriptTesting.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE) && !tblCreateScriptTesting.toLowerCase().contains("already")) {
                                        scriptLog.put("2) creator_diagn_testing", prepUpQuery[prepUpQueryTesting.length - 1]);
                                    }
                                } else {
                                    curTblJsonObj.put("requires_testing_clone", false);
                                }

                                if (prepUpQuery[prepUpQuery.length - 1].toString().toLowerCase().contains("error")) {
                                    errorsOnlyObj.put(curSchemaName + "." + curTableName, scriptLog);
                                }
                                curTblJsonObj.put("scripts_detail", scriptLog);
                            }
                            /*                    if (GlobalVariables.Schemas.CONFIG.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitConfig.getTableCreationScriptFromConfigTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.CONFIG_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitConfigAudit.getTableCreationScriptFromConfigAuditTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.DATA.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitData.getTableCreationScriptFromDataTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.DATA_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitDataAudit.getTableCreationScriptFromDataAuditTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.PROCEDURE.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitProcedure.getTableCreationScriptFromDataProcedureTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                             */ break;

                        case "INSPECTION_LOT_RAW_MATERIAL":
                            /*                    if (GlobalVariables.Schemas.CONFIG.getName().equalsIgnoreCase(curSchemaName.toString())){
                            Object[] tableExists=dbTableExists(procInstanceName+"-"+GlobalVariables.Schemas.CONFIG.getName(), curTableName.toString());
                            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(tableExists[0].toString()))
                                tableCreationScriptTable=TblsInspLotRMConfig.getTableUpdateScriptFromConfigTableInspLotRM(curTableName.toString(), procInstanceName+"-"+GlobalVariables.Schemas.CONFIG.getName(), curFieldName.toString().split("\\|"));
                            else
                                tableCreationScriptTable = TblsInspLotRMConfig.getTableCreationScriptFromConfigTableInspLotRM(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        }
    //                    if (GlobalVariables.Schemas.CONFIG_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
    //                        tableCreationScriptFromCnfgTable = TblsInspLotRMCnfgAduit.getTableCreationScriptFromCnfgTable(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.DATA.getName().equalsIgnoreCase(curSchemaName.toString())){
                            tableCreationScriptTable = TblsInspLotRMData.getTableCreationScriptFromDataTableInspLotRM(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        }
                        if (GlobalVariables.Schemas.DATA_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsInspLotRMDataAudit.getTableCreationScriptFromDataAuditTableInspLotRM(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.PROCEDURE.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsInspLotRMProcedure.getTableCreationScriptFromDataProcedureTableInspLotRM(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));                  
                             */ break;
                        case "GENOME":
                            break;
                        case "INVENTORY_TRACK":
                            ModuleTableOrViewGet tblDiagn = new ModuleTableOrViewGet(Boolean.valueOf(curIsView), moduleName, curSchemaName, curTableName.toUpperCase());
                            if (curIsView == null || !Boolean.valueOf(curIsView)) {
                                //EnumIntTables moduleTableObj = getModuleTableObj(moduleName, curSchemaName, curTableName.toUpperCase());
                                if (Boolean.FALSE.equals(tblDiagn.getFound())) {
                                    curTblJsonObj.put("error", tblDiagn.getErrorMsg());
                                    //curTblJsonObj.put("error", tableCreationScriptTable);
                                } else {
                                    tblCreateScript = createTableScript(tblDiagn.getTableObj(), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    if (Boolean.TRUE.equals(tblDiagn.getMirrorForTesting())) {
                                        tblCreateScriptTesting = createTableScript(tblDiagn.getTableObj(), schemaForTesting, false, true);
                                    }
                                }
                            } else {
                                tblCreateScript = EnumIntViews.getViewScriptCreation(ViewsProcedure.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                tblCreateScriptTesting = EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                            }
                            if (tblCreateScript.length() > 0) {
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
                                    schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
                                    curTblJsonObj.put("requires_testing_clone", true);
                                    Object[] prepUpQueryTesting = Rdbms.prepUpQueryWithDiagn(curSchemaName, curTableName, tblCreateScriptTesting, new Object[]{});
                                    scriptLog.put("2) script_testing", tblCreateScriptTesting);

                                    if (!(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE) || tblCreateScript.toLowerCase().startsWith("view")) && !tblCreateScriptTesting.toLowerCase().contains("already")) {
                                        scriptLog.put("2) creator_diagn_testing", prepUpQuery[prepUpQueryTesting.length - 1]);
                                    }
                                } else {
                                    curTblJsonObj.put("requires_testing_clone", false);
                                }

                                if (prepUpQuery[prepUpQuery.length - 1].toString().toLowerCase().contains("error")) {
                                    errorsOnlyObj.put(curSchemaName + "." + curTableName, scriptLog);
                                }
                                curTblJsonObj.put("scripts_detail", scriptLog);
                            }
                            break;
                        /*                        switch (curSchemaName.toLowerCase()){                            
                        case "config":
                            
                            tblCreateScript = createTableScript(TablesConfig.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                            break;                        
                        break;*/
                        default:
                            tableCreationScriptTable = "The module " + moduleName + " is not recognized";
                            curTblJsonObj.put("error", tableCreationScriptTable);
                            break;
                    }
                }
                jsonObj.put(curSchemaName + "-" + curTableName, curTblJsonObj);
            }
            return jsonObj;
        } catch (Exception e) {
            JSONObject jErr = new JSONObject();
            jErr.put("log_before_error", jsonObj);
            jErr.put("error", e.getMessage());
        }
        return jsonObj;
    }

    /*
    public static final  JSONObject createDBModuleTablesAndFieldsNEW(String procedure,  Integer procVersion, String procInstanceName, String moduleName){
        JSONObject jsonObj = new JSONObject();
        
         Object[][] procModuleTablesAndFieldsSource = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableName(), 
                new String[]{TblsReqs.ProcedureModuleTables.ACTIVE.getName(), TblsReqs.ProcedureModuleTables.PROCEDURE_NAME.getName(), TblsReqs.ProcedureModuleTables.PROCEDURE_VERSION.getName(),TblsReqs.ProcedureModuleTables.PROC_INSTANCE_NAME.getName()}, 
                new Object[]{true, procedure, procVersion, procInstanceName}, 
                getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), 
                new String[]{TblsReqs.ProcedureModuleTables.IS_VIEW.getName(), TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.ORDER_NUMBER.getName()});        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procModuleTablesAndFieldsSource[0][0].toString())){
          jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procModuleTablesAndFieldsSource[0]));
          return jsonObj;
        }
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procModuleTablesAndFieldsSource.length);  
        JSONObject errorsOnlyObj=new JSONObject();
        Integer orderObj=0;
        for (Object[] curModuleTablesAndFields: procModuleTablesAndFieldsSource){
            JSONObject curTblJsonObj=new JSONObject();
            orderObj++;
            String tableCreationScriptTable = "";
            String curSchemaName = LPNulls.replaceNull(curModuleTablesAndFields[LPArray.valuePosicInArray(getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName())]).toString();
            String curTableName = LPNulls.replaceNull(curModuleTablesAndFields[LPArray.valuePosicInArray(getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), TblsReqs.ProcedureModuleTables.TABLE_NAME.getName())]).toString();
            String curFieldName = LPNulls.replaceNull(curModuleTablesAndFields[LPArray.valuePosicInArray(getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), TblsReqs.ProcedureModuleTables.FIELD_NAME.getName())]).toString();
            String curIsView = LPNulls.replaceNull(curModuleTablesAndFields[LPArray.valuePosicInArray(getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), TblsReqs.ProcedureModuleTables.IS_VIEW.getName())]).toString();
            curTblJsonObj.put("table_name", curTableName);
            curTblJsonObj.put("repository_name", curSchemaName);
            curTblJsonObj.put("fields_name", curFieldName);
            curTblJsonObj.put("order_index", orderObj);            
            Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
            Object[] dbTableTestingExists=dbTableExists;
            String schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
            if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName)))
                dbTableTestingExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, schemaForTesting), curTableName);
            String diagn="";
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString()) && LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableTestingExists[0].toString()))
                curTblJsonObj.put(GlobalAPIsParams.LBL_DIAGNOSTIC, "table already exists in this repository");
            else{
                diagn ="table NOT exists in this repository";
                String tblCreateScript =null;
                String tblCreateScriptTesting=null;

                if (!moduleExists(moduleName.toUpperCase()))
                    tableCreationScriptTable="The module "+moduleName+" is not recognized";
                else{
                    Boolean cont=true;
                    EnumIntTables[] moduleTableObjs =null;
                    EnumIntViews[] moduleViewObjs =null;
                    if (curIsView==null || !Boolean.valueOf(curIsView)){
                        moduleTableObjs = getModuleTableObj(moduleName, curSchemaName, curTableName);
                    }else{
                        moduleViewObjs = getModuleViewObj(moduleName, curSchemaName, curTableName);
                    }
                    
                    try{
                        switch (curSchemaName.toLowerCase()){                            
                            case "config":
                                try{
                                    tblCreateScript = createTableScript(TablesEnvMonitConfig.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                }catch(Exception e){                                        
                                    tblCreateScript = createTableScript(TablesConfig.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                }
                                break;
                            case "config-audit":
                                try{
                                    tblCreateScript = createTableScript(TablesEnvMonitConfigAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                }catch(Exception e){
                                    tblCreateScript = createTableScript(TablesCfgAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                }
                                break;
                            case "data":
                                if (curIsView==null || !Boolean.valueOf(curIsView)){
                                    try{
                                        EnumIntTables dataModuleTblObj=getModuleTableObj(moduleName.toUpperCase(), curTableName.toUpperCase());
                                        tblCreateScript = createTableScript(TablesEnvMonitData.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        tblCreateScriptTesting = createTableScript(TablesEnvMonitData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                    }catch(Exception e){
                                        tblCreateScript = createTableScript(TablesData.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        tblCreateScriptTesting = createTableScript(TablesData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                    }
                                }else{
                                    try{
                                        EnumIntViews dataModuleTblObj=getModuleViewObj(moduleName.toUpperCase(), curTableName.toUpperCase());
                                        tblCreateScript=EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                        tblCreateScriptTesting=EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                        if (tblCreateScript.length()==0){
                                            tblCreateScript = ViewsEnvMonData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                            tblCreateScriptTesting = createTableScript(TablesEnvMonitData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                        }
                                    }catch(Exception e){
                                        tblCreateScript = ViewsData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                    }                                        
                                }
                                break;
                            case "data-audit":
                                try{
                                    tblCreateScript = createTableScript(TablesEnvMonitDataAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    tblCreateScriptTesting = createTableScript(TablesEnvMonitDataAudit.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                }catch(Exception e){
                                    tblCreateScript = createTableScript(TablesDataAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    tblCreateScriptTesting = createTableScript(TablesDataAudit.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                }
                                break;
                            case "procedure":
                                if (curIsView==null || !Boolean.valueOf(curIsView)){
                                    try{
                                        tblCreateScript = createTableScript(TablesEnvMonitProcedure.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName)))
                                            tblCreateScriptTesting = createTableScript(TablesEnvMonitProcedure.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                    }catch(Exception e){
                                        tblCreateScript = createTableScript(TablesProcedure.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName)))
                                            tblCreateScriptTesting = createTableScript(TablesProcedure.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                    }
                                }else{
                                    try{
                                        tblCreateScript=EnumIntViews.getViewScriptCreation(ViewsProcedure.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                        tblCreateScriptTesting=EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                        if (tblCreateScript.length()==0){
                                            tblCreateScript = ViewsEnvMonData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                            tblCreateScriptTesting = createTableScript(TablesEnvMonitData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                        }
                                    }catch(Exception e){
                                        tblCreateScript = ViewsData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                    }                                        
                                }
                                break;
                            case "procedure-audit":
                                try{
                                    tblCreateScript = createTableScript(TablesProcedureAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName)))
                                        tblCreateScriptTesting = createTableScript(TablesProcedureAudit.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                }catch(Exception e){
                                }
                                break;
                            default:
                                cont=false;
                                curTblJsonObj.put("unexpected_error", "repository "+curSchemaName+" not recognized");
                        }                    
                    }catch(Exception e){
                        cont=false;
                        curTblJsonObj.put("unexpected_error", e.getMessage());
                    }
                    if (cont){
                        Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curSchemaName, curTableName, tblCreateScript, new Object[]{});
                        if ("-999".equalsIgnoreCase(prepUpQuery[0].toString()))
                            diagn=diagn+" and not created, "+prepUpQuery[prepUpQuery.length-1];                                
                        else
                            diagn=diagn+" and created";
                        curTblJsonObj.put(GlobalAPIsParams.LBL_DIAGNOSTIC, diagn);

                        JSONObject scriptLog=new JSONObject();
                        if (!(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE)||tblCreateScript.toLowerCase().startsWith("view")) && !tblCreateScript.toLowerCase().contains("already"))
                            scriptLog.put("1) creator_diagn", prepUpQuery[prepUpQuery.length-1]);
                        scriptLog.put("1) script", tblCreateScript);                            

                        schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
                        if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName)) && tblCreateScriptTesting!=null){
                            curTblJsonObj.put("requires_testing_clone", true);
                            Object[] prepUpQueryTesting = Rdbms.prepUpQueryWithDiagn(curSchemaName, curTableName, tblCreateScriptTesting, new Object[]{});
                            scriptLog.put("2) script_testing", tblCreateScriptTesting);

                            if (!(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE)||tblCreateScript.toLowerCase().startsWith("view")) && !tblCreateScriptTesting.toLowerCase().contains("already"))
                            scriptLog.put("2) creator_diagn_testing", prepUpQuery[prepUpQueryTesting.length-1]);
                        }else
                            curTblJsonObj.put("requires_testing_clone", false);

                        if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains("error"))
                            errorsOnlyObj.put(curSchemaName+"."+curTableName, scriptLog);
                        curTblJsonObj.put("scripts_detail", scriptLog);                                
                    }                    
                }
                switch (moduleName.toUpperCase()){
                    case "ENVIRONMENTAL_MONITORING":       

                        /*                    if (GlobalVariables.Schemas.CONFIG.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitConfig.getTableCreationScriptFromConfigTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.CONFIG_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitConfigAudit.getTableCreationScriptFromConfigAuditTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.DATA.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitData.getTableCreationScriptFromDataTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.DATA_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitDataAudit.getTableCreationScriptFromDataAuditTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.PROCEDURE.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitProcedure.getTableCreationScriptFromDataProcedureTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        * /    
                        break;

                    case "SAMPLES":       
                        cont=true;
                        try{
                            switch (curSchemaName.toLowerCase()){                            
                                case "config":
                                    tblCreateScript = createTableScript(TablesConfig.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    break;
                                case "config-audit":
                                    tblCreateScript = createTableScript(TablesCfgAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    break;
                                case "data":
                                    if (curIsView==null || !Boolean.valueOf(curIsView)){
                                        tblCreateScript = createTableScript(TablesData.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        tblCreateScriptTesting = createTableScript(TablesData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                    }else{
                                        //tblCreateScript = ViewsData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                        try{
                                            tblCreateScript=EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                            tblCreateScriptTesting=EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                            if (tblCreateScript.length()==0){
                                                tblCreateScript = ViewsEnvMonData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                                tblCreateScriptTesting = createTableScript(TablesEnvMonitData.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                            }
                                        }catch(Exception e){
                                            tblCreateScript = ViewsData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                        }                                        
                                    }
                                    break;
                                case "data-audit":
                                    tblCreateScript = createTableScript(TablesDataAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                    tblCreateScriptTesting = createTableScript(TablesDataAudit.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                    break;
                                case "procedure":
                                    if (curIsView==null || !Boolean.valueOf(curIsView)){
                                        tblCreateScript = createTableScript(TablesProcedure.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName)))
                                            tblCreateScriptTesting = createTableScript(TablesProcedure.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                    }else{
                                        try{
                                            tblCreateScript=EnumIntViews.getViewScriptCreation(ViewsProcedure.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, false);
                                            tblCreateScriptTesting=EnumIntViews.getViewScriptCreation(ViewsData.valueOf(curTableName.toUpperCase()), procInstanceName, false, true, true);
                                            if (tblCreateScript.length()==0){
                                                tblCreateScript = ViewsEnvMonData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                                tblCreateScriptTesting = createTableScript(TablesProcedure.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                            }
                                        }catch(Exception e){
                                            tblCreateScript = ViewsData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                        }                                                                                
                                    }
                                    break;
                                case "procedure-audit":
                                    try{
                                        tblCreateScript = createTableScript(TablesProcedureAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName), false, true);
                                        if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName)))
                                            tblCreateScriptTesting = createTableScript(TablesProcedureAudit.valueOf(curTableName.toUpperCase()), schemaForTesting, false, true);
                                    }catch(Exception e){
                                    }
                                    break;
                                default:
                                    cont=false;
                                    curTblJsonObj.put("unexpected_error", "repository "+curSchemaName+" not recognized");
                            }                    
                        }catch(Exception e){
                            cont=false;
                            curTblJsonObj.put("unexpected_error", e.getMessage());
                        }
                        if (cont){
                            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(curSchemaName, curTableName, tblCreateScript, new Object[]{});
                            if ("-999".equalsIgnoreCase(prepUpQuery[0].toString()))
                                diagn=diagn+" and not created, "+prepUpQuery[prepUpQuery.length-1];                                
                            else
                                diagn=diagn+" and created";
                            curTblJsonObj.put(GlobalAPIsParams.LBL_DIAGNOSTIC, diagn);
                            
                            JSONObject scriptLog=new JSONObject();
                            if (!(tblCreateScript.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE)||tblCreateScript.toLowerCase().startsWith("view")) && !tblCreateScript.toLowerCase().contains("already"))
                                scriptLog.put("1) creator_diagn", prepUpQuery[prepUpQuery.length-1]);
                            scriptLog.put("1) script", tblCreateScript);                            
                            
                            schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
                            if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName)) && tblCreateScriptTesting!=null){
                                curTblJsonObj.put("requires_testing_clone", true);
                                Object[] prepUpQueryTesting = Rdbms.prepUpQueryWithDiagn(curSchemaName, curTableName, tblCreateScriptTesting, new Object[]{});
                                scriptLog.put("2) script_testing", tblCreateScriptTesting);
                                if (!tblCreateScriptTesting.toLowerCase().startsWith(GlobalAPIsParams.LBL_TABLE) && !tblCreateScriptTesting.toLowerCase().contains("already"))
                                scriptLog.put("2) creator_diagn_testing", prepUpQuery[prepUpQueryTesting.length-1]);
                            }else
                                curTblJsonObj.put("requires_testing_clone", false);

                            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains("error"))
                                errorsOnlyObj.put(curSchemaName+"."+curTableName, scriptLog);
                            curTblJsonObj.put("scripts_detail", scriptLog);                                
                        }
                        /*                    if (GlobalVariables.Schemas.CONFIG.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitConfig.getTableCreationScriptFromConfigTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.CONFIG_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitConfigAudit.getTableCreationScriptFromConfigAuditTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.DATA.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitData.getTableCreationScriptFromDataTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.DATA_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitDataAudit.getTableCreationScriptFromDataAuditTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.PROCEDURE.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitProcedure.getTableCreationScriptFromDataProcedureTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                      * /  
                        break;

                    case "INSPECTION_LOT_RAW_MATERIAL":
    /*                    if (GlobalVariables.Schemas.CONFIG.getName().equalsIgnoreCase(curSchemaName.toString())){
                            Object[] tableExists=dbTableExists(procInstanceName+"-"+GlobalVariables.Schemas.CONFIG.getName(), curTableName.toString());
                            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(tableExists[0].toString()))
                                tableCreationScriptTable=TblsInspLotRMConfig.getTableUpdateScriptFromConfigTableInspLotRM(curTableName.toString(), procInstanceName+"-"+GlobalVariables.Schemas.CONFIG.getName(), curFieldName.toString().split("\\|"));
                            else
                                tableCreationScriptTable = TblsInspLotRMConfig.getTableCreationScriptFromConfigTableInspLotRM(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        }
    //                    if (GlobalVariables.Schemas.CONFIG_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
    //                        tableCreationScriptFromCnfgTable = TblsInspLotRMCnfgAduit.getTableCreationScriptFromCnfgTable(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.DATA.getName().equalsIgnoreCase(curSchemaName.toString())){
                            tableCreationScriptTable = TblsInspLotRMData.getTableCreationScriptFromDataTableInspLotRM(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        }
                        if (GlobalVariables.Schemas.DATA_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsInspLotRMDataAudit.getTableCreationScriptFromDataAuditTableInspLotRM(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.PROCEDURE.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsInspLotRMProcedure.getTableCreationScriptFromDataProcedureTableInspLotRM(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));                  
    * /                    break;
                    case "GENOME":
                        break;
                    default: 
                        tableCreationScriptTable="The module "+moduleName+" is not recognized";
                        break;
                }
            }
        jsonObj.put(curSchemaName+"-"+curTableName, curTblJsonObj);
        }
            
        return jsonObj;
    }
     */
    /**
     *
     * @param procedure
     * @param procVersion
     * @param procInstanceName
     * @return
     */
    public static final JSONObject addProcedureSOPtoUsers(String procedure, Integer procVersion, String procInstanceName) {
        JSONObject jsonObj = new JSONObject();
        String schemaNameDestinationProc = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName());
        Object[][] procEventSopsRecordsSource = Rdbms.getRecordFieldsByFilter(schemaNameDestinationProc, TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableName(),
                new String[]{TblsProcedure.ProcedureEvents.SOP.getName() + WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{""},
                FLDSTO_RETRIEVE_PROC_EVENT_DEST.split("\\|"), new String[]{"sop"});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventSopsRecordsSource[0][0].toString())) {
            jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procEventSopsRecordsSource));
            return jsonObj;
        }
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procEventSopsRecordsSource.length);

        String[] existingSopRole = new String[0];
        for (Object[] curProcEventSops : procEventSopsRecordsSource) {
            Object curProcEventName = curProcEventSops[LPArray.valuePosicInArray(FLDSTO_RETRIEVE_PROC_EVENT_DEST.split("\\|"), TblsProcedure.ProcedureEvents.NAME.getName())];
            Object curSops = curProcEventSops[LPArray.valuePosicInArray(FLDSTO_RETRIEVE_PROC_EVENT_DEST.split("\\|"), TblsProcedure.ProcedureEvents.SOP.getName())];
            Object curRoleName = curProcEventSops[LPArray.valuePosicInArray(FLDSTO_RETRIEVE_PROC_EVENT_DEST.split("\\|"), TblsProcedure.ProcedureEvents.ROLE_NAME.getName())];
            JSONArray jsArr = new JSONArray();
            JSONObject jsUserRoleObj = new JSONObject();
            jsUserRoleObj.put("Procedure Event", curProcEventName);
            jsUserRoleObj.put("SOP Name", curSops);
            jsUserRoleObj.put("Role Name", curRoleName);

            String[] curSopsArr = curSops.toString().split("\\|");
            String[] curRoleNameArr = curRoleName.toString().split("\\|");
            JSONArray jsEventArr = new JSONArray();
            for (String sopFromArr : curSopsArr) {
                JSONArray jsSopRoleArr = new JSONArray();
                for (String roleFromArr : curRoleNameArr) {

                    JSONObject jsSopRoleObj = new JSONObject();

                    String sopRoleValue = sopFromArr + "*" + roleFromArr;
                    Integer sopRolePosic = LPArray.valuePosicInArray(existingSopRole, sopRoleValue);
                    String diagnosesForLog = (sopRolePosic == -1) ? JsonTags.NO.getTagValue() : JsonTags.YES.getTagValue();
                    jsSopRoleObj.put("SOP " + sopFromArr + " exists for role " + roleFromArr + " ?", diagnosesForLog);
                    if (sopRolePosic == -1) {
                        Object[][] procedureAddSopToUsersByRole = ProcedureDefinitionToInstanceUtility.procedureAddSopToUsersByRole(procedure, procVersion, procInstanceName,
                                roleFromArr, sopFromArr, null, null);
                    }
                    jsSopRoleArr.add(jsSopRoleObj);
                    existingSopRole = LPArray.addValueToArray1D(existingSopRole, sopRoleValue);
                }
                jsEventArr.add(jsSopRoleArr);
                jsUserRoleObj.put("Event SOPs Log", jsEventArr);
            }
            jsArr.add(jsUserRoleObj);
            jsonObj.put("Procedure Event " + curProcEventName + " & SOP Name " + curSops + " & Role Name " + curRoleName, jsArr);
        }
        return jsonObj;
    }

    /**
     *
     * @param schemaNamePrefix - Procedure Instance where it applies
     * @return
     */
    public static final JSONObject createDBProcessSchemas(String schemaNamePrefix) {
        JSONObject jsonObj = new JSONObject();

        String[] schemaNames = new String[]{GlobalVariables.Schemas.CONFIG.getName(), GlobalVariables.Schemas.CONFIG_AUDIT.getName(), GlobalVariables.Schemas.DATA.getName(), GlobalVariables.Schemas.DATA_AUDIT.getName(), GlobalVariables.Schemas.PROCEDURE_AUDIT.getName(), GlobalVariables.Schemas.PROCEDURE.getName()};
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), schemaNames.length);
        for (String fn : schemaNames) {
            JSONArray jsSchemaArr = new JSONArray();
            String configSchemaName = schemaNamePrefix + "-" + fn;
            jsSchemaArr.add(configSchemaName);

            configSchemaName = LPPlatform.buildSchemaName(configSchemaName, fn);
            String configSchemaScript = "CREATE SCHEMA " + configSchemaName + "  AUTHORIZATION " + SCHEMA_AUTHORIZATION_ROLE + ";"
                    + " GRANT ALL ON SCHEMA " + configSchemaName + " TO " + SCHEMA_AUTHORIZATION_ROLE + ";";
            Rdbms.prepUpQuery(configSchemaScript, new Object[]{});

            // La idea es no permitir ejecutar prepUpQuery directamente, por eso es privada y no publica.            
            //Integer prepUpQuery = Rdbms.prepUpQuery(configSchemaScript, new Object[0]);
            //String diagnosesForLog = (prepUpQuery==-1) ? JSON_LABEL_FOR_NO : JSON_LABEL_FOR_YES;
            //jsonObj.put("Schema Created?", diagnosesForLog);
            jsonObj.put(configSchemaName, jsSchemaArr);
        }
        return jsonObj;
    }

    /**
     *
     * @param schemaNamePrefix - Procedure Instance where it applies
     * @param tableName
     * @param fieldsName
     * @return
     */
    public static final JSONObject createDBProcessTables(String schemaNamePrefix, String tableName, String[] fieldsName) {
        return new JSONObject();
    }

    public static final JSONArray createBusinessRules(String procedure, Integer procVersion, String instanceName) {
        try {

            String[] fieldsRequired = new String[]{TblsReqs.ProcedureBusinessRules.FILE_SUFFIX.getName(), TblsReqs.ProcedureBusinessRules.RULE_NAME.getName(), TblsReqs.ProcedureBusinessRules.RULE_VALUE.getName()};
            String diagnObjName = GlobalAPIsParams.LBL_DIAGNOSTIC;
            String[] fildsToGet = new String[]{TblsReqs.ProcedureBusinessRules.FILE_SUFFIX.getName(), TblsReqs.ProcedureBusinessRules.RULE_NAME.getName(),
                TblsReqs.ProcedureBusinessRules.RULE_VALUE.getName()};
            for (String curFldReq : fieldsRequired) {
                if (!LPArray.valueInArray(fildsToGet, curFldReq)) {
                    LPArray.addValueToArray1D(fildsToGet, curFldReq);
                }
            }
            JSONArray jsonArr = new JSONArray();
            JSONObject jsonObj = new JSONObject();
            Object[][] procBusRules = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_BUS_RULES.getTableName(),
                    new String[]{TblsReqs.ProcedureBusinessRules.PROCEDURE_NAME.getName(), TblsReqs.ProcedureBusinessRules.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureBusinessRules.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureBusinessRules.ACTIVE.getName()},
                    new Object[]{procedure, procVersion, instanceName, true},
                    fildsToGet, new String[]{});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procBusRules[0][0].toString())) {
                jsonObj.put(JsonTags.ERROR.getTagValue(), RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND.getErrorCode());
                jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), 0);
                jsonArr.add(jsonObj);
                //return jsonArr;
            } else {
                jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procBusRules.length);

                jsonArr.add(jsonObj);
                // Object[] procBusRulesFiles = LPArray.getColumnFromArray2D(procBusRules, LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureBusinessRules.FILE_SUFFIX.getName()));
                /*        String[] filesNames=LPArray.getUniquesArray(procBusRulesFiles);
                for (String curFile: filesNames){
                    parm.createPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                            instanceName+"-"+curFile);  
                }        */
                for (Object[] curprocBusRules : procBusRules) {
                    RdbmsObject diagn = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE,
                            new String[]{TblsProcedure.ProcedureBusinessRules.AREA.getName(), TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName(), TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()},
                            curprocBusRules);
                    curprocBusRules = LPArray.addValueToArray1D(curprocBusRules, diagn.getApiMessage());
                    JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(fildsToGet, curprocBusRules);
                    //Object curProcEventName = curProcEventSops[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), TblsProcedure.ProcedureEvents.PROCEDURE_NAME.getName())];
                    jsonArr.add(convertArrayRowToJSONObject);
                }
            }
            //Build procedureActions and actionEnabled properties
            fildsToGet = new String[]{TblsReqs.ProcedureUserRequirements.WINDOW_ACTION.getName(), TblsReqs.ProcedureUserRequirements.ROLES.getName(), TblsReqs.ProcedureUserRequirements.CONFIRM_DIALOG.getName(), TblsReqs.ProcedureUserRequirements.CONFIRM_DIALOG_DETAIL.getName()};
            Object[][] procActionsEnabledBusRules = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableName(),
                    new String[]{TblsReqs.ProcedureUserRequirements.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirements.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUserRequirements.ACTIVE.getName(), TblsReqs.ProcedureUserRequirements.IN_SYSTEM.getName(), TblsReqs.ProcedureUserRequirements.IN_SCOPE.getName(), TblsReqs.ProcedureUserRequirements.WINDOW_ELEMENT_TYPE.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.LIKE},
                    new Object[]{procedure, procVersion, instanceName, true, true, true, "WindowAction%"},
                    fildsToGet, new String[]{});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procActionsEnabledBusRules[0][0].toString())) {
                jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procActionsEnabledBusRules[0]));
                jsonArr.add(jsonObj);
                return jsonArr;
            }
            String allEnabledActions = "";
            String allEsigReq = "";
            String allUserConfirmReq = "";
            for (Object[] curProcActionEnabled : procActionsEnabledBusRules) {
                String curAction = LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.WINDOW_ACTION.getName())]).toString();
                if (curAction.length() > 0) {
                    curAction = curAction.trim();
                    String confirmDialog = LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.CONFIRM_DIALOG.getName())]).toString();
                    String confirDialogDetail = LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.CONFIRM_DIALOG_DETAIL.getName())]).toString();
                    if ("user_esign".equalsIgnoreCase(confirmDialog)) {
                        allEsigReq = allEsigReq + "|" + curAction;
                    }
                    //                if ("true".equalsIgnoreCase(userConfirmReq)) allUserConfirmReq=allUserConfirmReq+"|"+curAction;

                    RdbmsObject diagn = Rdbms.insertRecord(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE,
                            new String[]{TblsProcedure.ProcedureBusinessRules.AREA.getName(), TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName(), TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()},
                            new Object[]{GlobalVariables.Schemas.PROCEDURE.getName(), LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName() + curAction, LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.ROLES.getName())]).toString()}, instanceName);
                    curProcActionEnabled = LPArray.addValueToArray1D(curProcActionEnabled, diagn.getApiMessage());
                    JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(fildsToGet, curProcActionEnabled);
                    //Object curProcEventName = curProcEventSops[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), TblsProcedure.ProcedureEvents.PROCEDURE_NAME.getName())];
                    jsonArr.add(convertArrayRowToJSONObject);
                    if (allEnabledActions.length() > 0) {
                        allEnabledActions = allEnabledActions + "|";
                    }
                    allEnabledActions = allEnabledActions + LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.WINDOW_ACTION.getName())]).toString();
                }
            }
            Parameter parm = new Parameter();
            RdbmsObject addProcBusinessRule = parm.addProcBusinessRule(LpPlatformBusinessRules.PROCEDURE_ACTIONS.getAreaName(),
                    LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName(), allEnabledActions);

            addProcBusinessRule = parm.addProcBusinessRule(LpPlatformBusinessRules.ESIGN_REQUIRED.getAreaName(),
                    LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName(), allEsigReq);
            addProcBusinessRule = parm.addProcBusinessRule(LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getAreaName(),
                    LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getTagName(), allUserConfirmReq);
            return jsonArr;
        } catch (Exception e) {
            JSONArray jsonArr = new JSONArray();
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("error_exception", e.getMessage());
            return jsonArr;
        }
    }

    public static final JSONObject deployMasterData(String procedure, Integer procVersion, String instanceName, String moduleName) {
        JSONObject jsonObjSummary = new JSONObject();
        Object[] allMismatchesDiagnAll = TestingRegressionUAT.procedureRepositoryMirrors(instanceName);
        Object[] allMismatchesDiagn = (Object[]) allMismatchesDiagnAll[0];
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allMismatchesDiagn[0].toString())) {
            Object[][] allMismatches = (Object[][]) allMismatchesDiagnAll[1];
            JSONArray jArr = new JSONArray();
            for (int i = 1; i < allMismatches.length; i++) {
                jArr.add(LPJson.convertArrayRowToJSONObject(LPArray.convertObjectArrayToStringArray(allMismatches[0]), allMismatches[i]));
            }
            jsonObjSummary.put("error_not_mirror_tables", jArr);
            return jsonObjSummary;
        }
        try {
            JSONArray jsonArr = new JSONArray();
            JSONObject jsonObj = new JSONObject();
            Object[][] procMasterDataObjs = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MASTER_DATA.getTableName(),
                    new String[]{TblsReqs.ProcedureMasterData.PROCEDURE_NAME.getName(), TblsReqs.ProcedureMasterData.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureMasterData.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureMasterData.ACTIVE.getName()},
                    new Object[]{procedure, procVersion, instanceName, true},
                    new String[]{TblsReqs.ProcedureMasterData.OBJECT_TYPE.getName(), TblsReqs.ProcedureMasterData.JSON_OBJ.getName()},
                    new String[]{TblsReqs.ProcedureMasterData.ORDER_NUMBER.getName()});
            JSONArray jsonRowArr = new JSONArray();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procMasterDataObjs[0][0].toString())) {
                jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procMasterDataObjs[0]));
                jsonArr.add(jsonObj);
            } else {
                jsonArr.add(jsonObj);
                for (Object[] curRow : procMasterDataObjs) {
                    try {
                        ClassMasterData clssMD = new ClassMasterData(instanceName, curRow[0].toString(), curRow[1].toString(), moduleName);
                        JSONObject jsonRowObj = new JSONObject();
                        jsonRowObj.put(curRow[0], clssMD.getDiagnostic()[clssMD.getDiagnostic().length - 1]);
                        jsonRowArr.add(jsonRowObj);
                    } catch (Exception e) {
                        JSONObject jsonRowObj = new JSONObject();
                        jsonRowObj.put("error_in_" + curRow[0].toString(), e.getMessage() + Arrays.toString(curRow));
                        jsonRowArr.add(jsonRowObj);
                        jsonObjSummary.put("summary_with_errors", jsonRowArr);
                        return jsonObjSummary;
                    }
                }
            }
            jsonObjSummary.put("summary", jsonRowArr);
            return jsonObjSummary;
        } catch (Exception e) {
            jsonObjSummary.put("error", e.getMessage());
            return jsonObjSummary;
        }
    }

}
