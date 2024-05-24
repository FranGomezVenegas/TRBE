package trazit.procedureinstance.deployment.logic;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.labplanet.servicios.app.AppProcedureListAPI.elementType;
import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.app.TestingRegressionUAT;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import static databases.Rdbms.insertRecordInTableFromTable;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.SqlWhere;
import databases.TblsApp;
import databases.TblsAppConfig;
import databases.TblsProcedure;
import databases.TblsProcedureAudit;
import trazit.procedureinstance.definition.definition.TblsReqs;
import databases.features.DbEncryption;
import functionaljavaa.requirement.masterdata.ClassMasterData;
import java.util.Arrays;
import lbplanet.utilities.LPNulls;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntViews;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;

public class ProcedureDefinitionToInstanceSections {

    public static EnumIntViews[] getModuleViewObj(String getModuleViewObj, String tblName) {
        return null;
    }

    public static final String[] ProcedureAuditSchema_TablesWithNoTestingClone = new String[]{TblsProcedureAudit.TablesProcedureAudit.PROC_HASH_CODES.getTableName()};
    public static final String[] ProcedureSchema_TablesWithNoTestingClone = new String[]{TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(), TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableName(),
        TblsProcedure.TablesProcedure.AUDIT_HIGHLIGHT_FIELDS.getTableName(), 
        TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName(), TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS_MASTER_DATA.getTableName(), TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(), TblsProcedure.ViewProcUserAndRoles.TBL.getName(), 
        TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), TblsProcedure.TablesProcedure.PROCEDURE_EVENTS_HELP_CONTENT.getTableName()};

    private ProcedureDefinitionToInstanceSections() {
        throw new IllegalStateException("Utility class");
    }

    public enum ReqSolutionTypes {
        WINDOW("Window"), WINDOW_BUTTON("Window Button"), TABLE_ROW_BUTTON("Table Row Button"), BUSINESS_RULE("Business Rule"), SPECIAL_VIEW("Special View")
        ;
        private ReqSolutionTypes(String tgVal) {
            this.tagValue = tgVal;
        }
        public String getTagValue() {
            return this.tagValue;
        }
        private final String tagValue;
            public static ReqSolutionTypes getByTagValue(String tagValue) {
        for (ReqSolutionTypes type : ReqSolutionTypes.values()) {
            if (type.getTagValue().equals(tagValue)) {
                return type;
            }
        }
        // If no matching enum is found, you can return null or throw an exception as per your requirement.
        return null;
    }
    }

        public enum ReqWindowContentType {
        WINDOW("Window"), WINDOW_BUTTON("Window Button"), TABLE_ROW_BUTTON("Table Row Button"), BUSINESS_RULE("Business Rule"), SPECIAL_VIEW("Special View")
        ;
        private ReqWindowContentType(String tgVal) {
            this.tagValue = tgVal;
        }
        public String getTagValue() {
            return this.tagValue;
        }
        public static String getAllTagValues() {
            return "('"+String.join("', '", Arrays.stream(ReqWindowContentType.values())
                                           .map(ReqWindowContentType::getTagValue)
                                           .toArray(String[]::new))+"')";
        }
        private final String tagValue;
        public static ReqSolutionTypes getByTagValue(String tagValue) {
            for (ReqSolutionTypes type : ReqSolutionTypes.values()) {
                if (type.getTagValue().equals(tagValue)) {
                    return type;
                }
            }
            // If no matching enum is found, you can return null or throw an exception as per your requirement.
            return null;
        }
    }

    public enum JsonTags {
        NO("No", "No"), YES("Yes", "Si"), ERROR("Error", "Error"), USERS("Users", "Usuarios"), NUM_RECORDS_IN_DEFINITION("Num Records in Definition Area", "Número de registros en área de Definición");

        private JsonTags(String tgValEn, String tgValEs) {
            this.tagValueEn = tgValEn;
            this.tagValueEs = tgValEs;
        }

        public String getTagValueEn() {
            return this.tagValueEn;
        }
        public String getTagValueEs() {
            return this.tagValueEs;
        }
        private final String tagValueEn;
        private final String tagValueEs;
    }
    public static final String SCHEMA_AUTHORIZATION_ROLE = "labplanet";
    public static final String FLDSTORETR_REQS_PROCINFOSRC = TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName() + "|" + TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName() + "|" + TblsReqs.ProcedureInfo.LABEL_EN.getName() + "|" + TblsReqs.ProcedureInfo.LABEL_ES.getName() + "|" + TblsReqs.ProcedureInfo.PROCEDURE_HASH_CODE.getName() + "|" + TblsReqs.ProcedureInfo.MODULE_NAME.getName()+ "|" + TblsReqs.ProcedureInfo.MODULE_SETTINGS.getName();
    public static final String FLDSTORETR_PROCEDURE_INFO_SOURCE = TblsProcedure.ProcedureInfo.NAME.getName() + "|" + TblsProcedure.ProcedureInfo.VERSION.getName() + "|" + TblsProcedure.ProcedureInfo.LABEL_EN.getName() + "|" + TblsProcedure.ProcedureInfo.LABEL_ES.getName() + "|" + TblsProcedure.ProcedureInfo.PROCEDURE_HASH_CODE.getName() + "|" + TblsProcedure.ProcedureInfo.MODULE_NAME.getName()+ "|" + TblsReqs.ProcedureInfo.MODULE_SETTINGS.getName();
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
        Object[][] procInfoRecordsSource = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(),
                new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName},
                FLDSTORETR_REQS_PROCINFOSRC.split("\\|"));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfoRecordsSource[0][0].toString())) {
            jsonErrorObj.put("Record in requirements", "Not exists");
            jsonErrorObj.put(JsonTags.ERROR.getTagValueEn(), LPJson.convertToJSON(procInfoRecordsSource[0]));
            return jsonErrorObj;
        } else {
            jsonErrorObj.put("Record in requirements", "Found");
        }
        for (Object[] curRow : procInfoRecordsSource) {
            Object[][] procInfoRecordsDestination = Rdbms.getRecordFieldsByFilter("", schemaNameDestinationProc, TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(),
                    new String[]{TblsProcedure.ProcedureInfo.NAME.getName(), TblsProcedure.ProcedureInfo.VERSION.getName()}, new Object[]{procedure, procVersion},
                    FLDSTORETR_PROCEDURE_INFO_SOURCE.split("\\|"));
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfoRecordsDestination[0][0].toString()))) {
                jsonErrorObj.put("Record in the new instance", "Already exists");
            } else {
                jsonErrorObj.put("Record in new instance", "did not exist previously");
                String[] fldName = FLDSTORETR_PROCEDURE_INFO_SOURCE.split("\\|");
                Object[] fldValue = curRow;
                if (Boolean.FALSE.equals(LPArray.valueInArray(fldName, TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName()))) {
                    fldName = LPArray.addValueToArray1D(fldName, TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName());
                    fldValue = LPArray.addValueToArray1D(fldValue, procInstanceName);
                }
                RdbmsObject insertRecordInTable = Rdbms.insertRecord(TblsProcedure.TablesProcedure.PROCEDURE_INFO,
                    fldName, fldValue, procInstanceName); //, schemaNameDestinationProc
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
        jsonObj.put("Record inserted in the instance?", false);
        jsonObj.put("error_detail", jsonErrorObj);
        return jsonObj;
    }

    private static final JSONObject createProcEventParent(String procedure, Integer procVersion, String procInstanceName, String role, String[] procEventFldNamesToGet, Object[] values) {
        JSONObject jsonObj = new JSONObject();
        String type = values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.TYPE.getName())].toString();
        String position = values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.POSITION.getName())].toString();
        if (Boolean.FALSE.equals(elementType.TWOICONS.toString().equalsIgnoreCase(type)) || Boolean.FALSE.equals("1".equalsIgnoreCase(position))) {
            return jsonObj;
        }
        values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.POSITION.getName())] = "0";
        values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.NAME.getName())] = "PARENT_" + values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.NAME.getName())].toString();
        values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.ESIGN_REQUIRED.getName())] = Boolean.valueOf(values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.ESIGN_REQUIRED.getName())].toString());
        values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.USERCONFIRM_REQUIRED.getName())] = Boolean.valueOf(values[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.USERCONFIRM_REQUIRED.getName())].toString());
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, procEventFldNamesToGet, values);
        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
            jsonObj.put("insert error log",
                    insertRecordInTable.getErrorMessageCode() + " " + Arrays.toString(insertRecordInTable.getErrorMessageVariables()));
        } else {
            jsonObj.put("parent_added", "success");
        }
        return jsonObj;
    }

    public static final JSONObject xcreateDBProcedureViews(String procedure, Integer procVersion, String procInstanceName) {
        SqlWhere sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureViews.NAME, WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, "");
        Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, sw, procInstanceName);
        
        Object[] insertRecordInTableFromTable = insertRecordInTableFromTable(true,
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableFields(), new String[]{TblsReqs.ProcedureReqSolution.NAME.getName()}),
                GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(),
                new String[]{TblsReqs.ProcedureReqSolution.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolution.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.TYPE.getName(), TblsReqs.ProcedureReqSolution.ACTIVE.getName(), TblsReqs.ProcedureReqSolution.IN_SYSTEM.getName(), TblsReqs.ProcedureReqSolution.IN_SCOPE.getName()},
                new Object[]{procedure, procVersion, procInstanceName, ReqSolutionTypes.WINDOW.getTagValue(), true, true, true},
                LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()),
                TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableName(), getAllFieldNames(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableFields(), new String[]{TblsProcedure.ProcedureViews.NAME.getName()}),
                null, null, new String[][]{{TblsReqs.ProcedureReqSolution.WINDOW_NAME.getName(), TblsProcedure.ProcedureViews.NAME.getName()}});
        JSONObject jsonObj = new JSONObject();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTableFromTable[0].toString())) {
            jsonObj.put("error_cloning_from_requirements_to_procedure", Arrays.toString(insertRecordInTableFromTable));
            return jsonObj;
        }
        jsonObj.put("success_cloning_from_requirements_to_procedure", insertRecordInTableFromTable[insertRecordInTableFromTable.length - 2] + ":" + insertRecordInTableFromTable[insertRecordInTableFromTable.length - 1]);
//        jsonObj.put("Diagnostic from createDBProcedureEvents", insertRecordInTableFromTable[0].toString());
        String[] procEventFldNamesToGet = getAllFieldNames(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableFields());
        Object[][] procEventRows = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableName(),
                //new String[]{TblsProcedure.ProcedureEvents.ROLE_NAME.getName(), WHERECLAUSE_TYPES.OR.getSqlClause()+" "+TblsProcedure.ProcedureEvents.ROLE_NAME.getName()+" "+WHERECLAUSE_TYPES.LIKE}, 
                //new Object[]{"ALL", "%|%"}, 
                new String[]{TblsProcedure.ProcedureViews.ROLE_NAME.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{},
                procEventFldNamesToGet);
        JSONArray multiRolejArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventRows[0][0].toString()))) {
            Object[][] procRoles = new Object[][]{{}};
            Object[][] procRolesAllRoles = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_ROLES.getTableName(),
                    new String[]{TblsReqs.ProcedureRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRoles.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName()},
                    new Object[]{procedure, procVersion, procInstanceName},
                    new String[]{TblsReqs.ProcedureRoles.ROLE_NAME.getName()});

            for (Object[] curProcEvent : procEventRows) {
                JSONObject multiRolCurEvent = new JSONObject();
                multiRolCurEvent.put("event_name",
                        curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.NAME.getName())]);
                String multiRolesLog = "";
                if ("ALL".equalsIgnoreCase(curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.ROLE_NAME.getName())].toString())) {
                    procRoles = procRolesAllRoles;
                    multiRolesLog = multiRolesLog + " as for all roles, trying addition for " + LPArray.convertArrayToString(LPArray.getColumnFromArray2D(procRoles, 0), ", ", "", true);
                } else {
                    procRoles = LPArray.array1dTo2d(curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.ROLE_NAME.getName())].toString().split("\\|"), 1);
                    multiRolesLog = multiRolesLog + " as for multiple roles, trying addition for " + LPArray.convertArrayToString(LPArray.getColumnFromArray2D(procRoles, 0), ", ", "", true);
                }
                multiRolCurEvent.put("multirole_type", multiRolesLog);
                for (int i = 0; i < procRoles.length; i++) {
                    if (i == 0) {
                        SqlWhere sqlWhere = new SqlWhere();
                        sqlWhere.addConstraint(TblsProcedure.ProcedureViews.ROLE_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.ROLE_NAME.getName())].toString()}, "");
                        sqlWhere.addConstraint(TblsProcedure.ProcedureViews.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.NAME.getName())]}, "");
                        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS,
                                EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, new String[]{TblsProcedure.ProcedureViews.ROLE_NAME.getName()}), new Object[]{procRoles[0][0].toString()}, sqlWhere, procInstanceName);
                        multiRolCurEvent.put("updated?", !LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString()));
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())) {
                            multiRolCurEvent.put("update error log", Arrays.toString(diagnoses));
                        } else {
                            JSONObject createProcEventParent = createProcEventParent(procedure, procVersion, procInstanceName, procRoles[i][0].toString(), procEventFldNamesToGet, curProcEvent);
                            multiRolCurEvent.put("adding_parent", createProcEventParent);
                        }
                    } else {

                        curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureViews.ROLE_NAME.getName())] = procRoles[i][0].toString();
                        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, procEventFldNamesToGet, curProcEvent);
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
                    multiRolejArr.put(multiRolCurEvent);
                }
            }
        }
        if (Boolean.FALSE.equals(multiRolejArr.isEmpty())) {
            jsonObj.put("multiroles_addition_log", multiRolejArr);
        }
        return jsonObj;
    }
    
    
    public static final JSONObject createdDBProcedureActions(String procedure, Integer procVersion, String procInstanceName) {
        Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, procInstanceName + "-procedure", TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName());
        return (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString()))? 
            createdDBProcedureActionsInBusRules(procedure, procVersion, procInstanceName):
            createdDBProcedureActionsInTable(procedure, procVersion, procInstanceName);
    }
    private static final JSONObject createdDBProcedureActionsInBusRules(String procedure, Integer procVersion, String procInstanceName) {
        JSONObject jMainObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        SqlWhere sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName()}, "");
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getAreaName()}, "");
        Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, procInstanceName);
        sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.LIKE, new Object[]{"%" + LPPlatform.LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName().toLowerCase() + "%"}, "");
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getAreaName()}, "");
        Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, procInstanceName);
        sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.LIKE, new Object[]{"%" + LPPlatform.LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName().toLowerCase() + "%"}, "");
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.ESIGN_REQUIRED.getAreaName()}, "");
        Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, procInstanceName);
        sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.LIKE, new Object[]{"%" + LPPlatform.LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getTagName().toLowerCase() + "%"}, "");
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getAreaName()}, "");
        Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, procInstanceName);
        sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.LIKE, new Object[]{"%" + LPPlatform.LpPlatformBusinessRules.ACTIONCONFIRM_REQUIRED.getTagName().toLowerCase() + "%"}, "");
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.ACTIONCONFIRM_REQUIRED.getAreaName()}, "");
        Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, procInstanceName);
        sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.LIKE, new Object[]{"%" + LPPlatform.LpPlatformBusinessRules.AUDIT_JUSTIF_REASON_REQUIRED.getTagName().toLowerCase() + "%"}, "");
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.AUDIT_JUSTIF_REASON_REQUIRED.getAreaName()}, "");
        Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, procInstanceName);
        sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.LIKE, new Object[]{"%" + LPPlatform.LpPlatformBusinessRules.AUDITREASON_PHRASE.getTagName().toLowerCase() + "%"}, "");
        sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.AUDITREASON_PHRASE.getAreaName()}, "");
        Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, procInstanceName);
        Object[][] procUsrReqs = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableName(),
                new String[]{TblsReqs.ProcedureReqSolution.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolution.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName()},
                new Object[]{procedure, procVersion, procInstanceName},
                new String[]{TblsReqs.ProcedureReqSolution.WINDOW_ACTION.getName(), TblsReqs.ProcedureReqSolution.ROLES.getName(),
                    TblsReqs.ProcedureReqSolution.CONFIRM_DIALOG.getName(), TblsReqs.ProcedureReqSolution.CONFIRM_DIALOG_DETAIL.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procUsrReqs[0][0].toString())) {
            JSONObject jObj = new JSONObject();
            jObj.put(GlobalAPIsParams.LBL_ERROR, "no entries in " + TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableName() + " table");
            jArr.put(jObj);
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
            jArr.put(jObj);
            StringBuilder esigns = new StringBuilder(0);
            StringBuilder verifUsers = new StringBuilder(0);
            StringBuilder actionConfirm = new StringBuilder(0);
            StringBuilder justifReason = new StringBuilder(0);
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
                        jArr.put(jObj);
                    }
                    String[] confirmDialog = LPNulls.replaceNull(curAction[2]).toString().split("\\|");
                    if (confirmDialog.length > 0) {
                        if (Arrays.toString(confirmDialog).toLowerCase().contains("esign")) {
                            if (esigns.length() > 0) {
                                esigns.append("|");
                            }
                            esigns.append(actionName);
                        }
                        if (Arrays.toString(confirmDialog).toLowerCase().contains("user")) {
                            if (verifUsers.length() > 0) {
                                verifUsers.append("|");
                            }
                            verifUsers.append(actionName);
                        }
                        if (Arrays.toString(confirmDialog).toLowerCase().contains("confirm")) {
                            if (actionConfirm.length() > 0) {
                                actionConfirm.append("|");
                            }
                            actionConfirm.append(actionName);
                        }
                        if (Arrays.toString(confirmDialog).toLowerCase().contains("justif")) {
                            if (justifReason.length() > 0) {
                                justifReason.append("|");
                            }
                            justifReason.append(actionName);
                        }
                    }
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
                        jArr.put(jObj);
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
                jArr.put(jObj);
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
                jArr.put(jObj);
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
                jArr.put(jObj);
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
                jArr.put(jObj);
            }
//          procedureActionsAndRoles(procInstanceName, ProcedureDefinitionQueries.ProcBusinessRulesQueries bsnRuleQry, mainObj);
        }
        jMainObj.put("createdDBProcedureActions", jArr);
        return jMainObj;
    }
    private static final JSONObject createdDBProcedureActionsInTable(String procedure, Integer procVersion, String procInstanceName) {
        JSONObject jMainObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        SqlWhere sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureActions.ACTION_NAME, WHERECLAUSE_TYPES.NOT_EQUAL, new Object[]{"<<>>"}, "");
        Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS, sw, procInstanceName);
        sw = new SqlWhere();
        String[] fldsToGet=new String[]{TblsReqs.ProcedureReqSolution.WINDOW_ACTION.getName(), TblsReqs.ProcedureReqSolution.ROLES.getName(),
                    TblsReqs.ProcedureReqSolution.CONFIRM_DIALOG.getName(), TblsReqs.ProcedureReqSolution.CONFIRM_DIALOG_DETAIL.getName(),
                    TblsReqs.ProcedureReqSolution.EXTRA_ACTIONS.getName()};
        Object[][] procUsrReqs = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(),
                new String[]{TblsReqs.ProcedureReqSolution.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolution.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName()
                        , TblsReqs.ProcedureReqSolution.TYPE.getName(), TblsReqs.ProcedureReqSolution.ACTIVE.getName(), TblsReqs.ProcedureReqSolution.IN_SYSTEM.getName(), TblsReqs.ProcedureReqSolution.IN_SCOPE.getName(), TblsReqs.ProcedureReqSolution.QUERY_FOR_BUTTON.getName()},
                new Object[]{procedure, procVersion, procInstanceName, ReqSolutionTypes.WINDOW_BUTTON.getTagValue(), true, true, true, false},
                fldsToGet);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procUsrReqs[0][0].toString())) {
            JSONObject jObj = new JSONObject();
            jObj.put(GlobalAPIsParams.LBL_ERROR, "no entries in " + TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableName() + " table");
            jArr.put(jObj);
        } else {
            Integer windowActionFldPosic = LPArray.valuePosicInArray(fldsToGet, TblsReqs.ProcedureReqSolution.WINDOW_ACTION.getName());
            Integer extraColFldPosic = LPArray.valuePosicInArray(fldsToGet, TblsReqs.ProcedureReqSolution.EXTRA_ACTIONS.getName());
            for (Object[] curAction : procUsrReqs) {
                String actionName = LPNulls.replaceNull(curAction[0]).toString();
                jArr.put(createProcActionRecord(fldsToGet, curAction, procInstanceName));
                if (extraColFldPosic>-1){
                    if (LPNulls.replaceNull(curAction[extraColFldPosic]).toString().length()>0){
                        for (String curExtraAction:curAction[extraColFldPosic].toString().split("\\|")){
                            curAction[windowActionFldPosic]=curExtraAction;
                            jArr.put(createProcActionRecord(fldsToGet, curAction, procInstanceName));
                        }
                    }
                }
            }
        }
        jMainObj.put("createdDBProcedureActions", jArr);
        return jMainObj;
    }
    
    private static JSONObject createProcActionRecord(String[] fldsToGet, Object[] curAction, String procInstanceName){
        String[] fldNames=null;
        Object[] fldValues=null;                
        Integer windowActionFldPosic = LPArray.valuePosicInArray(fldsToGet, TblsReqs.ProcedureReqSolution.WINDOW_ACTION.getName());
        Integer rolesFldPosic = LPArray.valuePosicInArray(fldsToGet, TblsReqs.ProcedureReqSolution.ROLES.getName());
        Integer confirmFldPosic = LPArray.valuePosicInArray(fldsToGet, TblsReqs.ProcedureReqSolution.CONFIRM_DIALOG.getName());
        Integer confirmDetailFldPosic = LPArray.valuePosicInArray(fldsToGet, TblsReqs.ProcedureReqSolution.CONFIRM_DIALOG_DETAIL.getName());
        String actionName = LPNulls.replaceNull(curAction[windowActionFldPosic]).toString();
        if (actionName.length()==0){
            return new JSONObject();
        }
        String allRolesStr = LPNulls.replaceNull(curAction[rolesFldPosic]).toString();

        fldNames=LPArray.addValueToArray1D(fldNames, TblsProcedure.ProcedureActions.ACTION_NAME.getName());
        fldValues=LPArray.addValueToArray1D(fldValues, actionName);
        fldNames=LPArray.addValueToArray1D(fldNames, TblsProcedure.ProcedureActions.ROLES_NAME.getName());
        fldValues=LPArray.addValueToArray1D(fldValues, allRolesStr);

        String[] confirmDialog = LPNulls.replaceNull(curAction[confirmFldPosic]).toString().split("\\|");
        if (confirmDialog.length > 0) {
            Boolean fieldAdded=false;
            if (Arrays.toString(confirmDialog).toLowerCase().contains("esign")) {
                fldNames=LPArray.addValueToArray1D(fldNames, TblsProcedure.ProcedureActions.ESIGN_REQUIRED.getName());
                fieldAdded=true;
            }
            if (Arrays.toString(confirmDialog).toLowerCase().contains("user")) {
                fldNames=LPArray.addValueToArray1D(fldNames, TblsProcedure.ProcedureActions.USER_CREDENTIAL_REQUIRED.getName());
                fieldAdded=true;
            }
            if (Arrays.toString(confirmDialog).toLowerCase().contains("confirm")) {
                fldNames=LPArray.addValueToArray1D(fldNames, TblsProcedure.ProcedureActions.ARE_YOU_SURE_REQUIRED.getName());
                fieldAdded=true;
            }
            if (Arrays.toString(confirmDialog).toLowerCase().contains("justif")) {
                fldNames=LPArray.addValueToArray1D(fldNames, TblsProcedure.ProcedureActions.JUSTIF_REASON_REQUIRED.getName());
                fieldAdded=true;
            }
            if (fieldAdded){
                fldValues=LPArray.addValueToArray1D(fldValues, true);                    
            }
        }
        String confirmDialogPhrase = curAction[confirmDetailFldPosic].toString();
        if (confirmDialogPhrase.length() > 0) {
            fldNames=LPArray.addValueToArray1D(fldNames, TblsProcedure.ProcedureActions.AUDIT_REASON_TYPE.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, confirmDialogPhrase);
        }                
        Object[] existsRecord = Rdbms.existsRecord(TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS, 
                new String[]{TblsProcedure.ProcedureActions.ACTION_NAME.getName()}, new Object[]{actionName}, procInstanceName);
        JSONObject jObj = new JSONObject();
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())){
            jObj.put("detail", actionName+". Action already present");
            return jObj;
        }
        RdbmsObject insertRecordInTable = Rdbms.insertRecord(TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS, fldNames, fldValues, procInstanceName);
        if (Boolean.TRUE.equals(insertRecordInTable.getRunSuccess())) {
            jObj.put(TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName()+"_"+ curAction[windowActionFldPosic].toString(), curAction[windowActionFldPosic].toString()+" Added");
        } else {
            jObj.put(TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS.getTableName()+"_"+ curAction[windowActionFldPosic].toString(), "error adding" + insertRecordInTable.getErrorMessageCode());
        }
        jObj.put("detail",LPJson.convertArrayRowToJSONObject(fldNames, fldValues));     
        return jObj;
    }

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
        Object[][] procUserRolesRecordsSource = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_USER_ROLES.getTableName(),
                new String[]{TblsReqs.ProcedureUserRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRoles.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName},
                FLDSTORETR_PROCEDURE_USR_ROLE_SRC.split("\\|"), FLDSTORETR_PROCEDURE_USR_ROLE_SRT.split("\\|"));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserRolesRecordsSource[0][0].toString())) {
            jsonObj.put(JsonTags.ERROR.getTagValueEn(), LPJson.convertToJSON(procUserRolesRecordsSource[0]));
            return jsonObj;
        }
        for (Object[] curRow : procUserRolesRecordsSource) {
            Object curUserName = curRow[LPArray.valuePosicInArray(FLDSTORETR_PROCEDURE_USR_ROLE_SRC.split("\\|"), TblsReqs.ProcedureUserRoles.USER_NAME.getName())];
            Object curRoleName = curRow[LPArray.valuePosicInArray(FLDSTORETR_PROCEDURE_USR_ROLE_SRC.split("\\|"), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName())];
            JSONArray jsArr = new JSONArray();
            JSONObject jsUserRoleObj = new JSONObject();
            jsUserRoleObj.put("User", curUserName);
            jsUserRoleObj.put("Role", curRoleName);
            String persEncrypted = "";
            Object[][] existsAppUser = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(),
                    new String[]{TblsApp.Users.USER_NAME.getName()}, new Object[]{curUserName.toString()}, new String[]{TblsApp.Users.PERSON_NAME.getName()});
            String diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())) ? JsonTags.NO.getTagValueEn() : JsonTags.YES.getTagValueEn();
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString()))) {
                persEncrypted=existsAppUser[0][0].toString();
            }else{
                //Object[] encryptPers=DbEncryption.encryptValue(curUserName + "z");        
                persEncrypted = String.valueOf(curUserName.hashCode());//encryptPers[encryptPers.length-1].toString();                
            }
                RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsAppConfig.TablesAppConfig.PERSON,
                        new String[]{TblsAppConfig.Person.PERSON_ID.getName(), TblsAppConfig.Person.FIRST_NAME.getName(),
                            TblsAppConfig.Person.LAST_NAME.getName(), TblsAppConfig.Person.PHOTO.getName()},
                        new Object[]{persEncrypted, "I'm " + curUserName, "for " + curRoleName, "https://hasta-pronto.ru/wp-content/uploads/2014/09/chibcha.jpg"});
                insertRecordInTable = Rdbms.insertRecordInTable(TblsApp.TablesApp.USERS,
                        new String[]{TblsApp.Users.USER_NAME.getName(), TblsApp.Users.EMAIL.getName(), TblsApp.Users.ESIGN.getName(),
                            TblsApp.Users.PASSWORD.getName(), TblsApp.Users.PERSON_NAME.getName()},
                        new Object[]{curUserName.toString().toLowerCase(), defaultMail, fakeEsingnEncrypted, pasEncrypted, persEncrypted});
                existsAppUser = LPArray.array1dTo2d(insertRecordInTable.getApiMessage(), 1);
                diagnosesForLog = diagnosesForLog + " trying to create, log for creation=" + insertRecordInTable.getApiMessage()[0].toString();
//                insertRecordInTable=Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USER_PROCESS.getTableName(), 
//                    new String[]{TblsApp.UserProcess.USER_NAME.getName(), TblsApp.UserProcess.PROC_NAME.getName(), TblsApp.UserProcess.ACTIVE.getName()}, 
//                    new Object[]{curUserName, fakeProcName, true});

                // Place to create the user
                existsAppUser[0][0]=persEncrypted;
            jsUserRoleObj.put("User exists in the app after running this logic?", LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesForLog));
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(existsAppUser[0])))) {
                Object[] existsAppUserProcess = Rdbms.existsRecord("", GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USER_PROCESS.getTableName(),
                        new String[]{TblsApp.UserProcess.USER_NAME.getName(), TblsApp.UserProcess.PROC_NAME.getName()}, new Object[]{curUserName.toString(), procInstanceName});
                jsonObj.put("User was added to the Process at the App level?", LPPlatform.LAB_TRUE.equalsIgnoreCase(existsAppUserProcess[0].toString()));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUserProcess[0].toString())) {
                    insertRecordInTable = Rdbms.insertRecordInTable(TblsApp.TablesApp.USER_PROCESS,
                            FIELDS_TO_INSERT_APP_USER_PROCESS.split("\\|"), new Object[]{curUserName.toString(), procInstanceName, true});
                    jsonObj.put("Added the User to the Process at the App level by running this utility?", insertRecordInTable.getApiMessage()[0].toString());
                }
            }
            Object curPersonName = existsAppUser[0][0];
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString()))) {
                RdbmsObject insertRecord = Rdbms.insertRecord(TblsProcedure.TablesProcedure.PERSON_PROFILE,
                        FLDSTO_INSERT_PROC_USR_ROLE_DEST.split("\\|"), new Object[]{curPersonName.toString().toLowerCase(), curRoleName.toString().toLowerCase(), true}, schemaNameDestinationProcedure);
                jsonObj.put("User Role inserted in the instance?", insertRecord.getApiMessage()[0].toString());
            }
            jsArr.put(jsUserRoleObj);
            jsonObj.put("User " + curUserName + " & Role " + curRoleName, jsArr);
        }
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValueEn(), procUserRolesRecordsSource.length);
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
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValueEn(), schemaNames.length);
        for (String fn : schemaNames) {
            JSONArray jsSchemaArr = new JSONArray();
            String configSchemaName = schemaNamePrefix + "-" + fn;
            jsSchemaArr.put(configSchemaName);

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
            SqlWhere sw = new SqlWhere();
            sw.addConstraint(TblsProcedure.ProcedureBusinessRules.RULE_NAME, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName()}, "");
            sw.addConstraint(TblsProcedure.ProcedureBusinessRules.AREA, WHERECLAUSE_TYPES.EQUAL, new Object[]{LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getAreaName()}, "");
            Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, sw, instanceName);

            String[] fieldsRequired = new String[]{TblsReqs.ProcedureBusinessRules.FILE_SUFFIX.getName(), TblsReqs.ProcedureBusinessRules.RULE_NAME.getName(), TblsReqs.ProcedureBusinessRules.RULE_VALUE.getName()};
            String[] fildsToGet = new String[]{TblsReqs.ProcedureBusinessRules.FILE_SUFFIX.getName(), TblsReqs.ProcedureBusinessRules.RULE_NAME.getName(),
                TblsReqs.ProcedureBusinessRules.RULE_VALUE.getName()};
            for (String curFldReq : fieldsRequired) {
                if (!LPArray.valueInArray(fildsToGet, curFldReq)) {
                    LPArray.addValueToArray1D(fildsToGet, curFldReq);
                }
            }
            JSONArray jsonArr = new JSONArray();
            JSONObject jsonObj = new JSONObject();
/*            Object[][] procBusRules = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_BUS_RULES.getTableName(),
                    new String[]{TblsReqs.ProcedureBusinessRules.PROCEDURE_NAME.getName(), TblsReqs.ProcedureBusinessRules.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureBusinessRules.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureBusinessRules.ACTIVE.getName()},
                    new Object[]{procedure, procVersion, instanceName, true},
                    fildsToGet, new String[]{});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procBusRules[0][0].toString())) {
                jsonObj.put(JsonTags.ERROR.getTagValue(), RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND.getErrorCode());
                jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), 0);
                jsonArr.put(jsonObj);
                //return jsonArr;
            } else {
                jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procBusRules.length);

                jsonArr.put(jsonObj);
                for (Object[] curprocBusRules : procBusRules) {
                    RdbmsObject diagn = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE,
                            new String[]{TblsProcedure.ProcedureBusinessRules.AREA.getName(), TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName(), TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()},
                            curprocBusRules);
                    curprocBusRules = LPArray.addValueToArray1D(curprocBusRules, diagn.getApiMessage());
                    JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(fildsToGet, curprocBusRules);
                    //Object curProcEventName = curProcEventSops[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), TblsProcedure.ProcedureEvents.PROCEDURE_NAME.getName())];
                    jsonArr.put(convertArrayRowToJSONObject);
                }
            }*/
            //Build procedureActions and actionEnabled properties
            fildsToGet = new String[]{TblsReqs.ProcedureReqSolution.BUSINESS_RULE.getName(), TblsReqs.ProcedureReqSolution.BUSINESS_RULE_VALUE.getName()};
            Object[][] procActionsEnabledBusRules = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(),
                    new String[]{TblsReqs.ProcedureReqSolution.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolution.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.ACTIVE.getName(), TblsReqs.ProcedureReqSolution.IN_SYSTEM.getName(), TblsReqs.ProcedureReqSolution.IN_SCOPE.getName(), TblsReqs.ProcedureReqSolution.TYPE.getName()},
                    new Object[]{procedure, procVersion, instanceName, true, true, true, ReqSolutionTypes.BUSINESS_RULE.getTagValue()},
                    fildsToGet, new String[]{});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procActionsEnabledBusRules[0][0].toString())) {
                jsonObj.put(JsonTags.ERROR.getTagValueEn(), LPJson.convertToJSON(procActionsEnabledBusRules[0]));
                jsonArr.put(jsonObj);
                return jsonArr;
            }
            fildsToGet = new String[]{TblsReqs.ProcedureReqSolution.BUSINESS_RULE.getName(), TblsReqs.ProcedureReqSolution.BUSINESS_RULE_VALUE.getName()};
            for (Object[] curProcActionEnabled : procActionsEnabledBusRules) {
                RdbmsObject diagn = Rdbms.insertRecord(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE,
                        new String[]{TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName(), TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName(), TblsProcedure.ProcedureBusinessRules.AREA.getName()},
                        LPArray.addValueToArray1D(curProcActionEnabled, "procedure"), instanceName);
                JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(fildsToGet, curProcActionEnabled);
                jsonArr.put(convertArrayRowToJSONObject);                
            }
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
        InternalMessage allMismatchesDiagnAll = TestingRegressionUAT.procedureRepositoryMirrors(instanceName);        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allMismatchesDiagnAll.getDiagnostic())) {
            Object[][] allMismatches = (Object[][]) allMismatchesDiagnAll.getNewObjectId();
            JSONArray jArr = new JSONArray();
            for (int i = 1; i < allMismatches.length; i++) {
                jArr.put(LPJson.convertArrayRowToJSONObject(LPArray.convertObjectArrayToStringArray(allMismatches[0]), allMismatches[i]));
            }
            jsonObjSummary.put("error_not_mirror_tables", jArr);
            return jsonObjSummary;
        }
        try {
            JSONArray jsonArr = new JSONArray();
            JSONObject jsonObj = new JSONObject();
            Object[][] procMasterDataObjs = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MASTER_DATA.getTableName(),
                    new String[]{TblsReqs.ProcedureMasterData.PROCEDURE_NAME.getName(), TblsReqs.ProcedureMasterData.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureMasterData.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureMasterData.ACTIVE.getName()},
                    new Object[]{procedure, procVersion, instanceName, true},
                    new String[]{TblsReqs.ProcedureMasterData.OBJECT_TYPE.getName(), TblsReqs.ProcedureMasterData.JSON_OBJ.getName()},
                    new String[]{TblsReqs.ProcedureMasterData.ORDER_NUMBER.getName()});
            JSONArray jsonRowArr = new JSONArray();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procMasterDataObjs[0][0].toString())) {
                jsonObj.put(JsonTags.ERROR.getTagValueEn(), LPJson.convertToJSON(procMasterDataObjs[0]));
                jsonArr.put(jsonObj);
            } else {
                jsonArr.put(jsonObj);
                for (Object[] curRow : procMasterDataObjs) {
                    try {
                        ClassMasterData clssMD = new ClassMasterData(instanceName, curRow[0].toString(), curRow[1].toString(), moduleName);
                        JSONObject jsonRowObj = new JSONObject();
                        jsonRowObj.put(curRow[0], clssMD.getjMainLogArr());
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(clssMD.getDiagnostic().getDiagnostic())) {
                            jsonRowObj.put("error_detail", clssMD.getDiagnostic().getMessageCodeObj().getErrorCode());
                        }
                        jsonRowArr.put(jsonRowObj);
                    } catch (Exception e) {
                        JSONObject jsonRowObj = new JSONObject();
                        jsonRowObj.put("error_in_" + curRow[0].toString(), e.getMessage() + Arrays.toString(curRow));
                        jsonRowArr.put(jsonRowObj);
                        jsonObjSummary.put("summary_with_errors", jsonRowArr);
                        return jsonObjSummary;
                    }
                }
            }
            jsonObjSummary.put("summary", jsonRowArr);
            return jsonObjSummary;
        } catch (Exception e) {
            jsonObjSummary.put(GlobalAPIsParams.LBL_ERROR, e.getMessage());
            return jsonObjSummary;
        }
    }
    public static final JSONArray xcreateDBProcedureViewsJson(String procedure, Integer procVersion, String procInstanceName) {
        JSONArray mainLog=new JSONArray();
        Object[][] procViewsArr = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_WINDOWS.getViewName(),
            new String[]{TblsReqs.viewProcReqSolutionViews.PROCEDURE_NAME.getName(), TblsReqs.viewProcReqSolutionViews.PROCEDURE_VERSION.getName(), TblsReqs.viewProcReqSolutionViews.PROC_INSTANCE_NAME.getName(), 
                TblsReqs.viewProcReqSolutionViews.ACTIVE.getName(), TblsReqs.viewProcReqSolutionViews.TYPE.getName()},
            new Object[]{procedure, procVersion, procInstanceName, true, ProcedureDefinitionToInstanceSections.ReqSolutionTypes.WINDOW.getTagValue()},
            new String[]{TblsReqs.viewProcReqSolutionViews.WINDOW_NAME.getName(), TblsReqs.viewProcReqSolutionViews.PARENT_CODE.getName(), TblsReqs.viewProcReqSolutionViews.WINDOW_QUERY.getName(),
            }); //TblsReqs.viewProcReqSolutionViews.JSON_MODEL.getName()});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procViewsArr[0][0].toString())){
            JSONObject curViewLog=new JSONObject();
            curViewLog.put("error", "cannot get the data");
            curViewLog.put("error_detail", Arrays.toString(procViewsArr[0]));
            return mainLog;
        }
        for (Object[] curView: procViewsArr){      
            JSONObject curViewLog=new JSONObject();
            String curViewName=curView[0].toString();
            String curParentCode=curView[1].toString();
            String curViewQuery=curView[2].toString();
            curViewLog.put("name", curViewName);
            curViewLog.put("query", curViewQuery);
            //JsonObject mainViewDef = JsonParser.parseString(curView[3].toString()).getAsJsonObject();
            JsonArray jObjModel = JsonParser.parseString(curView[3].toString()).getAsJsonArray();
            //JSONObject mainViewDefObj=new JSONObject();
            
            RdbmsObject updateTableRecordFieldsByFilter=Rdbms.insertRecord(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, 
                    new String[]{TblsProcedure.ProcedureViews.NAME.getName(), TblsProcedure.ProcedureViews.ROLE_NAME.getName(), TblsProcedure.ProcedureViews.JSON_MODEL.getName()}, 
                    new Object[]{"hola", "hola", jObjModel}, procInstanceName);

/*            if (mainViewDef.has("actions")){
                mainViewDef.remove("actions");
            }
            JSONArray actionsArr=new JSONArray();
            
            mainViewDefObj.put("test", "test");
            //mainViewDef.addProperty("actions", actionsArr);
            SqlWhere whereObj=new SqlWhere();
            whereObj.addConstraint(TblsProcedure.ProcedureViews.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curViewName}, null);
            RdbmsObject updateTableRecordFieldsByFilter = Rdbms.updateTableRecordFieldsByFilter(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, 
                    new EnumIntTableFields[]{TblsProcedure.ProcedureViews.JSON_MODEL}, 
                    new Object[]{jObjModel}, whereObj, procInstanceName);*/
                if (Boolean.TRUE.equals(updateTableRecordFieldsByFilter.getRunSuccess())) {
                    curViewLog.put("diagnostic", "success");
                } else {
                    curViewLog.put("diagnostic", "error");
                    curViewLog.put("error_detail", updateTableRecordFieldsByFilter.getErrorMessageCode() 
                            + " " + Arrays.toString(updateTableRecordFieldsByFilter.getErrorMessageVariables()));                    
                }            
            //curViewLog.put("model", mainViewDef);
            mainLog.put(curView);
        }
        return mainLog;
    }

}
