/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement.masterdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.labplanet.servicios.app.GlobalAPIsParams;
import module.monitoring.definition.TblsEnvMonitConfig;
import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import databases.RdbmsObject;
import module.instrumentsmanagement.definition.TblsInstrumentsConfig;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsProcedureConfig;
import modules.masterdata.analysis.ConfigAnalysisStructure;
import functionaljavaa.certification.AnalysisMethodCertif;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.materialspec.ConfigSpecStructure;
import functionaljavaa.parameter.Parameter;
import trazit.procedureinstance.deployment.logic.ModuleTableOrViewGet;
import functionaljavaa.responserelatedobjects.RelatedObjects.RelatedObjectsElementNames;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement;
import static functionaljavaa.unitsofmeasurement.UnitsOfMeasurement.getUomFromConfig;
import functionaljavaa.user.UserProfile;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import static lbplanet.utilities.LPJson.convertToJsonObjectStringedObject;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.instrumentsmanagement.definition.TblsInstrumentsData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public class ClassMasterData {

    private Boolean objectTypeExists = true;
    private InternalMessage diagnostic = null;
    private String globalDiagn = LPPlatform.LAB_TRUE;
    private JSONObject jMainLogArr;

    public enum MasterDataObjectTypes {
        MD_METHODS(new EnumIntTables[]{TblsCnfg.TablesConfig.METHODS}),
        MD_ANALYSIS_PARAMS(new EnumIntTables[]{TblsCnfg.TablesConfig.METHODS, TblsCnfg.TablesConfig.ANALYSIS, TblsCnfg.TablesConfig.ANALYSIS_METHOD, TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS}),
        MD_SPECS(new EnumIntTables[]{TblsCnfg.TablesConfig.SPEC}),
        MD_SPEC_RULES(new EnumIntTables[]{TblsCnfg.TablesConfig.SPEC_RULES}),
        MD_SPEC_LIMITS(new EnumIntTables[]{TblsCnfg.TablesConfig.SPEC_LIMITS}),
        MD_INCUBATORS(new EnumIntTables[]{TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR}),
        MD_INCUB_BATCHES(new EnumIntTables[]{TblsEnvMonitConfig.TablesEnvMonitConfig.INCUB_BATCH}),
        MD_MICROORGANISMS(new EnumIntTables[]{TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM, TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM_ADHOC}),
        MD_STAGES(new EnumIntTables[]{}),
        MD_STAGES_TIMING_INTERVAL(new EnumIntTables[]{TblsProcedureConfig.TablesProcedureConfig.STAGE_TIMING_INTERVAL}),
        MD_PROGRAMS(new EnumIntTables[]{TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM}),
        MD_PROGRAM_LOCATIONS(new EnumIntTables[]{TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION}),
        MD_SAMPLES(new EnumIntTables[]{TblsCnfg.TablesConfig.SAMPLE}),
        MD_SAMPLE_RULES(new EnumIntTables[]{TblsCnfg.TablesConfig.SAMPLE_RULES}),
        MD_PERSONAL_AREAS(new EnumIntTables[]{}),
        MD_INSTRUMENTS_FAMILIES(new EnumIntTables[]{TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY}),
        MD_INSTRUMENTS(new EnumIntTables[]{TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS}),
        MD_VARIABLES(new EnumIntTables[]{TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES}),
        MD_VARIABLES_SET(new EnumIntTables[]{TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES_SET}),
        MD_UOM(new EnumIntTables[]{TblsCnfg.TablesConfig.UOM}),;

        private MasterDataObjectTypes(EnumIntTables[] tblsObj) {
            this.tblsObj = tblsObj;
        }

        public EnumIntTables[] getInvolvedTables() {
            return this.tblsObj;
        }
        private final EnumIntTables[] tblsObj;
    }

    public ClassMasterData(String instanceName, String objectType, String jsonObj, String moduleName) {
        String userCreator = "PROCEDURE_DEPLOYMENT";
        String start = "START";
        Object[] objToJsonObj = convertToJsonObjectStringedObject(jsonObj, true);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objToJsonObj[0].toString())) {
            this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, objToJsonObj[1].toString() + ".Object: <*1*>", new Object[]{jsonObj}, null);
            calculateDiagnostic(new JSONArray());
            return;
        }
        JsonObject jsonObject = (JsonObject) objToJsonObj[1];
        try{
        JSONArray jLogArr = new JSONArray();
        if (jsonObject.has("parsing_type") && "SIMPLE_TABLE".equalsIgnoreCase(jsonObject.get("parsing_type").getAsString())) {
            JsonArray asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
            for (JsonElement jO : asJsonArray) {
                JSONObject jLog = new JSONObject();
                if (Boolean.FALSE.equals(jsonObject.has(RelatedObjectsElementNames.OBJECT_TYPE.toString().toLowerCase()))) {
                    this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE,
                            "object_type property not found in this json model which is required for 'simple_table' parsing_type", null, null);
                }
                String tableName = jsonObject.get(RelatedObjectsElementNames.OBJECT_TYPE.toString().toLowerCase()).getAsString();
                ModuleTableOrViewGet tblDiagn = new ModuleTableOrViewGet(Boolean.FALSE, moduleName, GlobalVariables.Schemas.CONFIG.getName(), tableName, instanceName);
                if (Boolean.FALSE.equals(tblDiagn.getFound())) {
                    this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, tblDiagn.getErrorMsg(), null, null);
                } else {
                    Object[] fldsInfo = getFldsNamesAndValues(tblDiagn.getTableObj(), jO);
                    if (fldsInfo.length == 3) {
                        this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, fldsInfo[2].toString(), null, null);
                    } else {
                        String[] fldsName = (String[]) fldsInfo[0];
                        Object[] fldsValue = (Object[]) fldsInfo[1];

                        if (EnumIntTableFields.getFldPosicInArray(tblDiagn.getTableObj().getTableFields(), TblsEnvMonitConfig.InstrIncubator.CREATED_BY.getName()) > -1) {
                            fldsName = LPArray.addValueToArray1D(fldsName, new String[]{TblsEnvMonitConfig.InstrIncubator.CREATED_BY.getName(), TblsEnvMonitConfig.InstrIncubator.CREATED_ON.getName()});
                            fldsValue = LPArray.addValueToArray1D(fldsValue, new Object[]{userCreator, LPDate.getCurrentTimeStamp()});
                        }
                        RdbmsObject insertRecord = Rdbms.insertRecord(tblDiagn.getTableObj(), fldsName, fldsValue, instanceName);
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(globalDiagn) && Boolean.FALSE.equals(insertRecord.getRunSuccess())) {
                            this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables(), insertRecord.getNewRowId());
                            globalDiagn = this.diagnostic.getDiagnostic();
                            jLog.put(GlobalAPIsParams.LBL_DIAGNOSTIC, this.diagnostic.getDiagnostic());
                            jLogArr.add(jLog);
                            calculateDiagnostic(jLogArr);
                            return;
                        }
                    }
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(globalDiagn)) {
                    this.diagnostic = new InternalMessage(globalDiagn, "created with success", new Object[]{tableName}, null);
                }
                jLog.put(GlobalAPIsParams.LBL_DIAGNOSTIC, this.diagnostic.getDiagnostic());
                jLogArr.add(jLog);
            }
            calculateDiagnostic(jLogArr);
        } else {

            MasterDataObjectTypes endPoint = null;
            try {
                endPoint = MasterDataObjectTypes.valueOf(objectType.toUpperCase());
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, ex.getMessage(), null, null);
                this.globalDiagn = LPPlatform.LAB_FALSE;
                this.objectTypeExists = false;
                calculateDiagnostic(new JSONArray());
                return;
            }

            String jsonObjType = jsonObject.get(RelatedObjectsElementNames.OBJECT_TYPE.toString().toLowerCase()).getAsString();
            if (Boolean.FALSE.equals(objectType.toUpperCase().contains(jsonObjType.toUpperCase()))) {
                this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, "objectType in record and objectType in the JsonObject mismatch", null, null);
                this.globalDiagn = LPPlatform.LAB_FALSE;
                calculateDiagnostic(new JSONArray());
                return;
            }
            if (endPoint.getInvolvedTables() != null && endPoint.getInvolvedTables().length > 0) {
                for (EnumIntTables curTbl : endPoint.getInvolvedTables()) {
                    Object[] dbTableExists = Rdbms.dbTableExists(instanceName, LPPlatform.buildSchemaName(instanceName, curTbl.getRepositoryName()), curTbl.getTableName());
                    if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString()))) {
                        this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_TABLE_NOT_FOUND, new Object[]{curTbl.getTableName()}, null);
                        this.globalDiagn = LPPlatform.LAB_FALSE;
                        calculateDiagnostic(new JSONArray());
                        return;
                    }
                }
            }

            switch (endPoint) {
                case MD_METHODS:
                    JsonArray asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    for (JsonElement jO : asJsonArray) {
                        String diagn = "";
                        String methodName = jO.getAsJsonObject().get(TblsCnfg.Methods.CODE.getName()).getAsString();
                        Integer methodVersion = -9;
                        if (jO.getAsJsonObject().has(GlobalAPIsParams.LBL_USERS_ASSIGNMENT)) {
                            methodVersion = jO.getAsJsonObject().get(TblsCnfg.Methods.CONFIG_VERSION.getName()).getAsInt();
                        } else {
                            methodVersion = 1;
                        }
                        JSONObject jLog = new JSONObject();
                        jLog.put(TblsCnfg.Methods.CODE.getName(), methodName);
                        if (jO.getAsJsonObject().has(GlobalAPIsParams.LBL_USERS_ASSIGNMENT)) {
                            InternalMessage userCertificationEnabled = AnalysisMethodCertif.isUserCertificationEnabled();
                            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled.getDiagnostic())) {
                                globalDiagn = userCertificationEnabled.getDiagnostic();
                                diagn = userCertificationEnabled.getMessageCodeObj().getErrorCode();
                            } else {
                                String userNameStr = jO.getAsJsonObject().get(GlobalAPIsParams.LBL_USERS_ASSIGNMENT).getAsString();
                                String[] usersArr = null;
                                if ("ALL".equalsIgnoreCase(userNameStr)) {
                                    Object[] procedureUsers = UserProfile.getProcedureUsers(instanceName, null);
                                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureUsers[0].toString()))) {
                                        this.globalDiagn = LPPlatform.LAB_FALSE;

                                        usersArr = LPArray.convertObjectArrayToStringArray(procedureUsers);
                                    }
                                } else {
                                    usersArr = userNameStr.split("\\|");
                                }
                                JSONArray jUserAssignLogArr = new JSONArray();
                                for (String curUser : usersArr) {
                                    JSONObject jUserAssignLog = new JSONObject();
                                    jUserAssignLog.put(TblsCnfg.Methods.CODE.getName(), methodName);
                                    jUserAssignLog.put(TblsData.ViewUserAndAnalysisMethodCertificationView.USER_NAME.getName(), curUser);
                                    InternalMessage newRecord = AnalysisMethodCertif.newRecord(methodName, methodVersion, curUser);
                                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newRecord.getDiagnostic())) {
                                        globalDiagn = newRecord.getDiagnostic();
                                    }
                                    jUserAssignLog.put(GlobalAPIsParams.LBL_DIAGNOSTIC, newRecord.getNewObjectId());
                                    jUserAssignLogArr.add(jUserAssignLog);
                                }
                                jLog.put("users_assignment_detail", jUserAssignLogArr);
                            }
                        }
                        jLog.put(GlobalAPIsParams.LBL_DIAGNOSTIC, diagn);
                        jLogArr.add(jLog);
                    }
                    calculateDiagnostic(jLogArr);
                    break;
                case MD_ANALYSIS_PARAMS:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    
                    for (JsonElement jO : asJsonArray) {
                        String methodName = jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.METHOD_NAME.getName()).getAsString();
                        String[] fldNames = new String[]{TblsCnfg.Methods.CODE.getName(), TblsCnfg.Methods.CONFIG_VERSION.getName(),
                            TblsCnfg.Methods.CREATED_ON.getName(), TblsCnfg.Methods.CREATED_BY.getName()};
                        Object[] fldValues = new Object[]{methodName, 1, LPDate.getCurrentTimeStamp(), userCreator};

                        Object[] existMethod = Rdbms.existsRecord(TblsCnfg.TablesConfig.METHODS, fldNames, fldValues, instanceName);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existMethod[0].toString())) {
                            Rdbms.insertRecord(TblsCnfg.TablesConfig.METHODS, fldNames, fldValues, instanceName);
                        }
                        String analysisCode=jO.getAsJsonObject().get(TblsCnfg.Analysis.CODE.getName()).getAsString();
                        ConfigAnalysisStructure cAna = new ConfigAnalysisStructure(analysisCode, null);
                        fldNames = new String[]{TblsCnfg.Analysis.ACTIVE.getName(), TblsCnfg.Analysis.CREATED_ON.getName(), TblsCnfg.Analysis.CREATED_BY.getName()};
                        fldValues = new Object[]{true, LPDate.getCurrentTimeStamp(), userCreator};
                        Object[] existAnalysis = Rdbms.existsRecord(TblsCnfg.TablesConfig.ANALYSIS, new String[]{TblsCnfg.Analysis.CODE.getName()}, 
                                new Object[]{analysisCode}, instanceName);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existAnalysis[0].toString())) {
                            this.diagnostic = cAna.analysisNew(jO.getAsJsonObject().get(TblsCnfg.Analysis.CODE.getName()).getAsString(), 1, fldNames, fldValues, instanceName);
                        }

                        fldNames = new String[]{TblsCnfg.AnalysisMethod.CREATED_ON.getName(), TblsCnfg.AnalysisMethod.CREATED_BY.getName(),
                            TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName(), TblsCnfg.AnalysisMethodParams.PARAM_TYPE.getName(),
                            TblsCnfg.AnalysisMethodParams.MANDATORY.getName(), TblsCnfg.AnalysisMethodParams.NUM_REPLICAS.getName()};
                        fldValues = new Object[]{LPDate.getCurrentTimeStamp(), userCreator,
                            jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.PARAM_TYPE.getName()).getAsString(),
                            jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.MANDATORY.getName()).getAsBoolean(), jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.NUM_REPLICAS.getName()).getAsInt()};

                        if (jO.getAsJsonObject().has(TblsCnfg.AnalysisMethodParams.UOM.getName())) {
                            fldNames = LPArray.addValueToArray1D(fldNames, TblsCnfg.AnalysisMethodParams.UOM.getName());
                            fldValues = LPArray.addValueToArray1D(fldValues, jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.UOM.getName()).getAsString());
                        }

                        this.diagnostic = cAna.analysisMethodParamsNew(jO.getAsJsonObject().get(TblsCnfg.Analysis.CODE.getName()).getAsString(), 1, jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.METHOD_NAME.getName()).getAsString(), fldNames, fldValues, true);
                        //if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnostic[0].toString())) return;
                        if (jO.getAsJsonObject().has(TblsCnfg.AnalysisMethodParams.UOM.getName())) {
                            String uom = jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.UOM.getName()).getAsString();
                            String currFld = "Tipo Importación";
                            if (jO.getAsJsonObject().has(currFld) && uom.length() > 0) {
                                getUomFromConfig(uom, jO.getAsJsonObject().get(currFld).getAsString());
                            }
                        }
                    }
                    this.diagnostic = new InternalMessage(LPPlatform.LAB_TRUE, "Inserted " + asJsonArray.size() + " new analysis params", null, null);
                    break;
                case MD_SPECS:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    ConfigSpecStructure cSpec = new ConfigSpecStructure();
                    for (JsonElement jO : asJsonArray) {
                        String[] specFieldName = new String[]{TblsCnfg.Spec.CREATED_ON.getName(), TblsCnfg.Spec.CREATED_BY.getName(),
                            TblsCnfg.Spec.CODE.getName(), TblsCnfg.Spec.VARIATION_NAMES.getName(), TblsCnfg.Spec.CATEGORY.getName(),
                            TblsCnfg.Spec.ANALYSES.getName()};
                        Object[] specFldValues = new Object[]{LPDate.getCurrentTimeStamp(), userCreator,
                            jO.getAsJsonObject().get(TblsCnfg.Spec.CODE.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.Spec.VARIATION_NAMES.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.Spec.CATEGORY.getName()).getAsString(),
                            jO.getAsJsonObject().get(TblsCnfg.Spec.ANALYSES.getName()).getAsString()};
                        String[] specRulesFieldName = new String[]{TblsCnfg.SpecRules.ALLOW_OTHER_ANALYSIS.getName(), TblsCnfg.SpecRules.ALLOW_MULTI_SPEC.getName()};
                        Object[] specRulesFldValues = new Object[]{jO.getAsJsonObject().get(TblsCnfg.SpecRules.ALLOW_OTHER_ANALYSIS.getName()).getAsBoolean(), jO.getAsJsonObject().get(TblsCnfg.SpecRules.ALLOW_MULTI_SPEC.getName()).getAsBoolean()};
                        this.diagnostic = cSpec.specNew(jO.getAsJsonObject().get(TblsCnfg.Spec.CODE.getName()).getAsString(), 1,
                                specFieldName, specFldValues, specRulesFieldName, specRulesFldValues);

                    }
                    this.diagnostic = new InternalMessage(LPPlatform.LAB_TRUE, "Inserted " + asJsonArray.size() + " new specs", null, null);
                    break;
                case MD_SPEC_LIMITS:
                    globalDiagn = LPPlatform.LAB_TRUE;
                    String ruleValues = null;
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    cSpec = new ConfigSpecStructure();
                    for (JsonElement jO : asJsonArray) {
                        JSONObject jLog = new JSONObject();
                        jLog.put(TblsCnfg.SpecLimits.ANALYSIS.getName(), jO.getAsJsonObject().get(TblsCnfg.SpecLimits.ANALYSIS.getName()).getAsString());
                        String[] fieldName = new String[]{//TblsCnfg.SpecLimits.CREATED_ON.getName(), TblsCnfg.SpecLimits.CREATED_BY.getName(),
                            TblsCnfg.SpecLimits.VARIATION_NAME.getName(),
                            TblsCnfg.SpecLimits.ANALYSIS.getName(), TblsCnfg.SpecLimits.METHOD_NAME.getName(),
                            TblsCnfg.SpecLimits.PARAMETER.getName(), TblsCnfg.SpecLimits.RULE_TYPE.getName()};
                        Object[] fieldValue = new Object[]{//LPDate.getCurrentTimeStamp(), userCreator,
                            jO.getAsJsonObject().get(TblsCnfg.SpecLimits.VARIATION_NAME.getName()).getAsString(), //jO.getAsJsonObject().get(TblsCnfg.SpecLimits.TESTING_GROUP.getName()).getAsString(), 
                            jO.getAsJsonObject().get(TblsCnfg.SpecLimits.ANALYSIS.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SpecLimits.METHOD_NAME.getName()).getAsString(),
                            jO.getAsJsonObject().get(TblsCnfg.SpecLimits.PARAMETER.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SpecLimits.RULE_TYPE.getName()).getAsString()};
                        String[] fldsToAdd = new String[]{TblsCnfg.SpecLimits.MIN_VAL_ALLOWED.getName(), TblsCnfg.SpecLimits.MIN_VAL_FOR_UNDETERMINED.getName(),
                            TblsCnfg.SpecLimits.MAX_VAL_ALLOWED.getName(), TblsCnfg.SpecLimits.MAX_VAL_FOR_UNDETERMINED.getName(), TblsCnfg.SpecLimits.TESTING_GROUP.getName()};
                        for (String curFldName : fldsToAdd) {
                            if (jO.getAsJsonObject().has(curFldName)) {
                                fieldName = LPArray.addValueToArray1D(fieldName, curFldName);
                                if (TblsCnfg.SpecLimits.TESTING_GROUP.getName().equalsIgnoreCase(curFldName)) {
                                    fieldValue = LPArray.addValueToArray1D(fieldValue, jO.getAsJsonObject().get(curFldName).getAsString());
                                } else {
                                    fieldValue = LPArray.addValueToArray1D(fieldValue, jO.getAsJsonObject().get(curFldName).getAsFloat());
                                }
                            }
                        }
                        String ruleType = jO.getAsJsonObject().get(TblsCnfg.SpecLimits.RULE_TYPE.getName()).getAsString();
                        InternalMessage resSpecEvaluation = null;
                        ConfigSpecRule mSpec = new ConfigSpecRule();
                        String curFldName = "";
                        if ("quantitative".equalsIgnoreCase(ruleType)) {
                            curFldName = "MIN Acción";
                            Float minSpec = null;
                            if (jO.getAsJsonObject().has(curFldName)) {
                                minSpec = jO.getAsJsonObject().get(curFldName).getAsFloat();
                            }
                            curFldName = "MIN Alerta";
                            Float minControl = null;
                            if (jO.getAsJsonObject().has(curFldName)) {
                                minControl = jO.getAsJsonObject().get(curFldName).getAsFloat();
                            }
                            curFldName = "MAX Alerta";
                            Float maxControl = null;
                            if (jO.getAsJsonObject().has(curFldName)) {
                                maxControl = jO.getAsJsonObject().get(curFldName).getAsFloat();
                            }
                            curFldName = "MAX Acción";
                            Float maxSpec = null;
                            if (jO.getAsJsonObject().has(curFldName)) {
                                maxSpec = jO.getAsJsonObject().get(curFldName).getAsFloat();
                            }
                            resSpecEvaluation = mSpec.specLimitIsCorrectQuantitative(minSpec, maxSpec, minControl, maxControl);
                            ruleValues = mSpec.getQuantitativeRuleValues();
                        } else if ("qualitative".equalsIgnoreCase(ruleType)) {
                            curFldName = "rule";
                            String rule = null;
                            if (jO.getAsJsonObject().has(curFldName)) {
                                rule = jO.getAsJsonObject().get(curFldName).getAsString();
                            }
                            curFldName = "value";
                            String value = null;
                            if (jO.getAsJsonObject().has(curFldName)) {
                                value = jO.getAsJsonObject().get(curFldName).getAsString();
                            }
                            curFldName = "separator";
                            String separator = null;
                            if (jO.getAsJsonObject().has(curFldName)) {
                                separator = jO.getAsJsonObject().get(curFldName).getAsString();
                            }
                            resSpecEvaluation = mSpec.specLimitIsCorrectQualitative(rule, value, separator);
                            ruleValues = mSpec.getQualitativeRuleValues();
                        }
                        if (Boolean.FALSE.equals(LPArray.valueInArray(fieldName, TblsCnfg.SpecLimits.UOM.getName()))) {
                            Object[][] paramUOM = Rdbms.getRecordFieldsByFilter(instanceName, LPPlatform.buildSchemaName(instanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS.getTableName(),
                                    new String[]{TblsCnfg.AnalysisMethodParams.ANALYSIS.getName(), TblsCnfg.AnalysisMethodParams.METHOD_NAME.getName(),
                                        TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName()},
                                    new Object[]{jO.getAsJsonObject().get(TblsCnfg.SpecLimits.ANALYSIS.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SpecLimits.METHOD_NAME.getName()).getAsString(),
                                        jO.getAsJsonObject().get(TblsCnfg.SpecLimits.PARAMETER.getName()).getAsString()},
                                    new String[]{TblsCnfg.AnalysisMethodParams.UOM.getName(), TblsCnfg.AnalysisMethodParams.UOM_CONVERSION_MODE.getName()});
                            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(paramUOM[0][0].toString())))
                                    && (paramUOM[0][0] != null)) {
                                fieldName = LPArray.addValueToArray1D(fieldName, TblsCnfg.SpecLimits.UOM.getName());
                                fieldValue = LPArray.addValueToArray1D(fieldValue, LPNulls.replaceNull(paramUOM[0][0].toString()));
                                fieldName = LPArray.addValueToArray1D(fieldName, TblsCnfg.SpecLimits.UOM_CONVERSION_MODE.getName());
                                fieldValue = LPArray.addValueToArray1D(fieldValue, LPNulls.replaceNull(paramUOM[0][1].toString()));
                            }
                        }
                        InternalMessage diagn = resSpecEvaluation;
                        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation.getDiagnostic()))) {
                            curFldName = TblsCnfg.SpecLimits.RULE_VARIABLES.getName();
                            fieldName = LPArray.addValueToArray1D(fieldName, curFldName);
                            fieldValue = LPArray.addValueToArray1D(fieldValue, ruleValues);
                            this.diagnostic = cSpec.specLimitNew(jO.getAsJsonObject().get(TblsCnfg.SpecLimits.CODE.getName()).getAsString(), 1, fieldName, fieldValue);
                            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnostic.getDiagnostic())) {
                                globalDiagn = diagn.getDiagnostic();
                            }
                        }else{
                            jLog.put(GlobalAPIsParams.LBL_DIAGNOSTIC, resSpecEvaluation.getDiagnostic());
                        }
                        jLog.put(GlobalAPIsParams.LBL_DIAGNOSTIC, this.diagnostic.getDiagnostic());
                        jLogArr.add(jLog);
                    }
                    calculateDiagnostic(jLogArr);
                    break;
                case MD_INCUBATORS:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    for (JsonElement jO : asJsonArray) {
                        JSONObject jLog = new JSONObject();
                        Object[] fldsInfo = getFldsNamesAndValues(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, jO);
                        if (fldsInfo.length == 3) {
                            this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, fldsInfo[2].toString(), null, null);
                        } else {
                            String[] fldsName = (String[]) fldsInfo[0];
                            Object[] fldsValue = (Object[]) fldsInfo[1];
                            fldsName = LPArray.addValueToArray1D(fldsName, new String[]{TblsEnvMonitConfig.InstrIncubator.CREATED_BY.getName(), TblsEnvMonitConfig.InstrIncubator.CREATED_ON.getName()});
                            fldsValue = LPArray.addValueToArray1D(fldsValue, new Object[]{userCreator, LPDate.getCurrentTimeStamp()});
                            RdbmsObject insertRecord = Rdbms.insertRecord(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR,
                                    fldsName, fldsValue, instanceName);
                            this.diagnostic = new InternalMessage(insertRecord.getSqlStatement(), insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables(), insertRecord.getNewRowId());
                        }
                        jLog.put(GlobalAPIsParams.LBL_DIAGNOSTIC, this.diagnostic.getDiagnostic());
                        jLogArr.add(jLog);
                    }
                    calculateDiagnostic(jLogArr);
                    /*                        
                    RdbmsObject insertRecord = Rdbms.insertRecord(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
                    new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.DESCRIPTION.getName()
                            , TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName(), TblsEnvMonitConfig.InstrIncubator.STAGE.getName(),
                        TblsEnvMonitConfig.InstrIncubator.CREATED_ON.getName(), TblsEnvMonitConfig.InstrIncubator.CREATED_BY.getName()},
                    new Object[]{jO.getAsJsonObject().get(TblsEnvMonitConfig.InstrIncubator.NAME.getName()).getAsString(), jO.getAsJsonObject().get(TblsEnvMonitConfig.InstrIncubator.DESCRIPTION.getName()).getAsString(), 
                        jO.getAsJsonObject().get(TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()).getAsBoolean(), jO.getAsJsonObject().get(TblsEnvMonitConfig.InstrIncubator.STAGE.getName()).getAsBoolean(), LPDate.getCurrentTimeStamp(), userCreator},
                    instanceName);
                        this.diagnostic=insertRecord.getApiMessage();
                        if (Boolean.FALSE.equals(insertRecord.getRunSuccess())) return;
                    }                    
                    this.diagnostic=new InternalMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new incubators", null);
                     */
                    break;
                case MD_INCUB_BATCHES:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    for (JsonElement jO : asJsonArray) {
                        JSONObject jLog = new JSONObject();
                        Object[] fldsInfo = getFldsNamesAndValues(TblsEnvMonitConfig.TablesEnvMonitConfig.INCUB_BATCH, jO);
                        if (fldsInfo.length == 3) {
                            this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, fldsInfo[2].toString(), null, null);
                        } else {
                            String[] fldsName = (String[]) fldsInfo[0];
                            Object[] fldsValue = (Object[]) fldsInfo[1];
                            fldsName = LPArray.addValueToArray1D(fldsName, new String[]{TblsEnvMonitConfig.IncubBatch.CREATED_BY.getName(), TblsEnvMonitConfig.IncubBatch.CREATED_ON.getName()});
                            fldsValue = LPArray.addValueToArray1D(fldsValue, new Object[]{userCreator, LPDate.getCurrentTimeStamp()});
                            RdbmsObject insertRecord = Rdbms.insertRecord(TblsEnvMonitConfig.TablesEnvMonitConfig.INCUB_BATCH,
                                    fldsName, fldsValue, instanceName);
                            this.diagnostic = new InternalMessage(insertRecord.getSqlStatement(), insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables(), insertRecord.getNewRowId());
                        }
                        jLog.put(GlobalAPIsParams.LBL_DIAGNOSTIC, this.diagnostic.getDiagnostic());
                        jLogArr.add(jLog);
                    }
                    calculateDiagnostic(jLogArr);
                    break;
                case MD_MICROORGANISMS:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    for (JsonElement jO : asJsonArray) {
                        RdbmsObject insertRecord = Rdbms.insertRecord(TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM,
                                new String[]{TblsEnvMonitConfig.MicroOrganism.NAME.getName()},
                                new Object[]{jO.getAsJsonObject().get(TblsEnvMonitConfig.MicroOrganism.NAME.getName()).getAsString()}, instanceName);
                        this.diagnostic = new InternalMessage(insertRecord.getSqlStatement(), insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables(), insertRecord.getNewRowId());
                        if (Boolean.FALSE.equals(insertRecord.getRunSuccess())) {
                            return;
                        }
                    }
                    this.diagnostic = new InternalMessage(LPPlatform.LAB_TRUE, "Inserted " + asJsonArray.size() + " new microorganisms", null, null);
                    break;
                case MD_SAMPLES:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    for (JsonElement jO : asJsonArray) {
                        RdbmsObject insertRecord = Rdbms.insertRecord(TblsCnfg.TablesConfig.SAMPLE,
                                new String[]{TblsCnfg.Sample.CODE.getName(), TblsCnfg.Sample.CODE_VERSION.getName(), TblsCnfg.Sample.CREATED_ON.getName(), TblsCnfg.Sample.CREATED_BY.getName()},
                                new Object[]{jO.getAsJsonObject().get(TblsCnfg.Sample.CODE.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.Sample.CODE_VERSION.getName()).getAsInt(), LPDate.getCurrentTimeStamp(), userCreator},
                                instanceName);
                        this.diagnostic = new InternalMessage(insertRecord.getSqlStatement(), insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables(), insertRecord.getNewRowId());
                        if (Boolean.FALSE.equals(insertRecord.getRunSuccess())) {
                            break;
                        }
                    }
                    this.diagnostic = new InternalMessage(LPPlatform.LAB_TRUE, "Inserted " + asJsonArray.size() + " new samples", null, null);
                    break;
                case MD_SAMPLE_RULES:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    for (JsonElement jO : asJsonArray) {
                        RdbmsObject insertRecord = Rdbms.insertRecord(TblsCnfg.TablesConfig.SAMPLE_RULES,
                                new String[]{TblsCnfg.SampleRules.CODE.getName(), TblsCnfg.SampleRules.CODE_VERSION.getName(),
                                    TblsCnfg.SampleRules.ANALYST_ASSIGNMENT_MODE.getName(), TblsCnfg.SampleRules.TEST_ANALYST_REQUIRED.getName(),
                                    TblsCnfg.SampleRules.CREATED_ON.getName(), TblsCnfg.SampleRules.CREATED_BY.getName()},
                                new Object[]{jO.getAsJsonObject().get(TblsCnfg.SampleRules.CODE.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SampleRules.CODE_VERSION.getName()).getAsInt(),
                                    jO.getAsJsonObject().get(TblsCnfg.SampleRules.ANALYST_ASSIGNMENT_MODE.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SampleRules.TEST_ANALYST_REQUIRED.getName()).getAsBoolean(),
                                    LPDate.getCurrentTimeStamp(), userCreator},
                                instanceName);
                        this.diagnostic = new InternalMessage(insertRecord.getSqlStatement(), insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables(), insertRecord.getNewRowId());
                    }
                    this.diagnostic = new InternalMessage(LPPlatform.LAB_TRUE, "Inserted " + asJsonArray.size() + " new sample rules", null, null);
                    break;
                case MD_PROGRAMS:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    for (JsonElement jO : asJsonArray) {
                        Object[] fldsInfo = getFldsNamesAndValues(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM, jO);
                        if (fldsInfo.length == 3) {
                            this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, fldsInfo[2].toString(), null, null);
                        } else {
                            String[] fldsName = (String[]) fldsInfo[0];
                            Object[] fldsValue = (Object[]) fldsInfo[1];
                            fldsName = LPArray.addValueToArray1D(fldsName, new String[]{TblsEnvMonitConfig.Program.CREATED_BY.getName(), TblsEnvMonitConfig.Program.CREATED_ON.getName()});
                            fldsValue = LPArray.addValueToArray1D(fldsValue, new Object[]{userCreator, LPDate.getCurrentTimeStamp()});
                            RdbmsObject insertRecord = Rdbms.insertRecord(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM,
                                    fldsName, fldsValue, instanceName);
                            this.diagnostic = new InternalMessage(insertRecord.getSqlStatement(), insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables(), insertRecord.getNewRowId());
                        }
                        /*                        
                        String[] fldName=new String[]{TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_VERSION.getName(),
                            TblsEnvMonitConfig.Program.SPEC_CODE.getName(),
                            TblsEnvMonitConfig.Program.CREATED_BY.getName(), TblsEnvMonitConfig.Program.CREATED_ON.getName()};
                        Object[] fldValue=new Object[]{jO.getAsJsonObject().get(TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName()).getAsInt(), 1,
                            jO.getAsJsonObject().get(TblsEnvMonitConfig.Program.SPEC_CODE.getName()).getAsString(),
                            userCreator, LPDate.getCurrentTimeStamp()};
                        String[] allFieldNames = EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM.getTableFields());
                        for (String curFld:allFieldNames){
                            
                            if (Boolean.FALSE.equals(LPArray.valueInArray(fldName, curFld) && jO.getAsJsonObject().has(curFld))){
                                fldName=LPArray.addValueToArray1D(fldName, curFld);
                                if (TblsEnvMonitConfig.Program.SAMPLE_CONFIG_CODE_VERSION.getName().equalsIgnoreCase(curFld))
                                    fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsInt());
                                else if (TblsEnvMonitConfig.Program.SPEC_CONFIG_VERSION.getName().equalsIgnoreCase(curFld))
                                    fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsInt());
                                else
                                    fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsString());
                            }
                        }
                        if (Boolean.FALSE.equals(LPArray.valueInArray(fldName, TblsEnvMonitConfig.Program.SAMPLE_CONFIG_CODE_VERSION.getName()))){
                            fldName=LPArray.addValueToArray1D(fldName, TblsEnvMonitConfig.Program.SAMPLE_CONFIG_CODE_VERSION.getName());
                            fldValue=LPArray.addValueToArray1D(fldValue, 1);                            
                        }
                        if (Boolean.FALSE.equals(LPArray.valueInArray(fldName, TblsEnvMonitConfig.Program.SPEC_CONFIG_VERSION.getName()))){
                            fldName=LPArray.addValueToArray1D(fldName, TblsEnvMonitConfig.Program.SPEC_CONFIG_VERSION.getName());
                            fldValue=LPArray.addValueToArray1D(fldValue, 1);                            
                        }
                        RdbmsObject insertRecord = Rdbms.insertRecord(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM, 
                            fldName, fldValue, instanceName);
                        this.diagnostic=insertRecord.getApiMessage();
                        if (Boolean.FALSE.equals(insertRecord.getRunSuccess())) return;                        */
                    }

                    //this.diagnostic=new InternalMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new program", null);
                    break;
                case MD_PROGRAM_LOCATIONS:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    for (JsonElement jO : asJsonArray) {
                        Object[] fldsInfo = getFldsNamesAndValues(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION, jO);
                        if (fldsInfo.length == 3) {
                            this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, fldsInfo[2].toString(), null, null);
                        } else {
                            RdbmsObject insertRecord = Rdbms.insertRecord(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION,
                                    (String[]) fldsInfo[0], (Object[]) fldsInfo[1], instanceName);
                            this.diagnostic = new InternalMessage(insertRecord.getSqlStatement(), insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables(), insertRecord.getNewRowId());
                        }
                        /*                        
                        String[] fldName=new String[]{TblsEnvMonitConfig.ProgramLocation.PROGRAM_CONFIG_ID.getName(), 
                            TblsEnvMonitConfig.ProgramLocation.PROGRAM_CONFIG_VERSION.getName(), TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName()};
                        //    TblsEnvMonitConfig.ProgramLocation.CREATED_BY.getName(), TblsEnvMonitConfig.ProgramLocation.CREATED_ON.getName()};
                        Object[] fldValue=new Object[]{jO.getAsJsonObject().get(TblsEnvMonitConfig.ProgramLocation.PROGRAM_CONFIG_ID.getName()).getAsInt(),
                            jO.getAsJsonObject().get(TblsEnvMonitConfig.ProgramLocation.PROGRAM_CONFIG_VERSION.getName()).getAsInt(), jO.getAsJsonObject().get(TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName()).getAsString()};
//                            userCreator, LPDate.getCurrentTimeStamp()};
                        String[] allFieldNames = EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getTableFields());
                        for (String curFld:allFieldNames){
                            if (Boolean.FALSE.equals(LPArray.valueInArray(fldName, curFld) && jO.getAsJsonObject().has(curFld))){
                                fldName=LPArray.addValueToArray1D(fldName, curFld);
                                if (TblsEnvMonitConfig.ProgramLocation.REQUIRES_PERSON_ANA.getName().equalsIgnoreCase(curFld))
                                    fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsBoolean());
                                else if (TblsEnvMonitConfig.ProgramLocation.SPEC_CODE_VERSION.getName().equalsIgnoreCase(curFld))
                                    fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsInt());
                                else
                                    fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsString());
                            }
                        } 
                        if (Boolean.FALSE.equals(LPArray.valueInArray(fldName, TblsEnvMonitConfig.ProgramLocation.SPEC_CODE_VERSION.getName()))){
                            fldName=LPArray.addValueToArray1D(fldName, TblsEnvMonitConfig.ProgramLocation.SPEC_CODE_VERSION.getName());
                            fldValue=LPArray.addValueToArray1D(fldValue, 1);                            
                        }
                        RdbmsObject insertRecord = Rdbms.insertRecord(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION, 
                            fldName, fldValue, instanceName);
                        this.diagnostic=insertRecord.getApiMessage();
                        if (Boolean.FALSE.equals(insertRecord.getRunSuccess()) ) return;
                         */
                    }
                    //this.diagnostic=new InternalMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new program locations", null);
                    break;
                case MD_STAGES:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    StringBuilder allStages = new StringBuilder(0);
                    StringBuilder stageWithAutoMoveToNext = new StringBuilder(0);
                    StringBuilder stageWithTimingCapture = new StringBuilder(0);
                    String curFldName = "";
                    String firstStage = "";
                    Parameter parm = new Parameter();
                    RdbmsObject addProcBusinessRule = null;
                    for (JsonElement jO : asJsonArray) {
                        curFldName = "NAME";
                        if (jO.getAsJsonObject().has(curFldName)) {
                            String curStage = jO.getAsJsonObject().get(curFldName).getAsString();
                            allStages.append("|").append(curStage);
                            curFldName = "LET AUTO-NEXT STAGE?";
                            if (jO.getAsJsonObject().has(curFldName) && "YES".equalsIgnoreCase(jO.getAsJsonObject().get(curFldName).getAsString())) {
                                stageWithAutoMoveToNext.append("|").append(curStage);
                            }
                            curFldName = "TIMING CAPTURE?";
                            if (jO.getAsJsonObject().has(curFldName) && "YES".equalsIgnoreCase(jO.getAsJsonObject().get(curFldName).getAsString())) {
                                stageWithTimingCapture.append("|").append(curStage);
                            }
                            curFldName = "PREVIOUS STAGES";
                            if (firstStage.length() == 0 && start.equalsIgnoreCase(jO.getAsJsonObject().get(curFldName).getAsString())) {
                                firstStage = curStage;
                            }
                            if (jO.getAsJsonObject().has(curFldName) && Boolean.FALSE.equals(start.equalsIgnoreCase(jO.getAsJsonObject().get(curFldName).getAsString()))) {
                                addProcBusinessRule = parm.addProcBusinessRule(GlobalVariables.Schemas.DATA.getName(),
                                        "sampleStage" + curStage + "Previous", jO.getAsJsonObject().get(curFldName).getAsString(), instanceName);
                                if (Boolean.FALSE.equals(addProcBusinessRule.getRunSuccess())){
                                    this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, "Error inserting " + "sampleStage" + curStage + "Previous", addProcBusinessRule.getErrorMessageVariables(), null);
                                }
                            }
                            
                            curFldName = "NEXT STAGES";
                            if (jO.getAsJsonObject().has(curFldName) && Boolean.FALSE.equals(start.equalsIgnoreCase(jO.getAsJsonObject().get(curFldName).getAsString()))) {
                                addProcBusinessRule = parm.addProcBusinessRule(GlobalVariables.Schemas.DATA.getName(),
                                        "sampleStage" + curStage + "Next", jO.getAsJsonObject().get(curFldName).getAsString(), instanceName);
                                if (Boolean.FALSE.equals(addProcBusinessRule.getRunSuccess())){
                                    this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, "Error inserting " + "sampleStage" + curStage + "Next", addProcBusinessRule.getErrorMessageVariables(), null);
                                }
                            }
                        }
                        this.diagnostic = new InternalMessage(LPPlatform.LAB_TRUE, "Inserted " + asJsonArray.size() + " new stages", null, null);
                    }
                    parm.addProcBusinessRule(GlobalVariables.Schemas.DATA.getName(), "sampleStagesList_en", allStages + "|END", instanceName);
                    parm.addProcBusinessRule(GlobalVariables.Schemas.DATA.getName(), "sampleStagesList_es", allStages + "|FIN", instanceName);
                    parm.addProcBusinessRule(GlobalVariables.Schemas.DATA.getName(), "sampleStagesFirst", firstStage, instanceName);
                    parm.addProcBusinessRule(GlobalVariables.Schemas.DATA.getName(), "sampleStagesFirst_en", firstStage, instanceName);
                    parm.addProcBusinessRule(GlobalVariables.Schemas.DATA.getName(), "sampleStagesFirst_es", firstStage, instanceName);
                    parm.addProcBusinessRule(GlobalVariables.Schemas.DATA.getName(), "sampleStagesFirst_es", firstStage, instanceName);
                    parm.addProcBusinessRule(GlobalVariables.Schemas.PROCEDURE.getName(), "sampleStagesTimingCaptureStages", stageWithTimingCapture.toString(), instanceName);
                    parm.addProcBusinessRule(GlobalVariables.Schemas.PROCEDURE.getName(), "sampleStagesActionAutoMoveToNext", stageWithAutoMoveToNext.toString(), instanceName);
                    parm.addProcBusinessRule(GlobalVariables.Schemas.PROCEDURE.getName(), "sampleStagesMode", "ENABLED", instanceName);

                    break;
                case MD_STAGES_TIMING_INTERVAL:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    for (JsonElement jO : asJsonArray) {
                        JSONObject jLog = new JSONObject();
                        Object[] fldsInfo = getFldsNamesAndValues(TblsProcedureConfig.TablesProcedureConfig.STAGE_TIMING_INTERVAL, jO);
                        if (fldsInfo.length == 3) {
                            this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, fldsInfo[2].toString(), null, null);
                        } else {
                            String[] fldsName = (String[]) fldsInfo[0];
                            Object[] fldsValue = (Object[]) fldsInfo[1];
                            RdbmsObject insertRecord = Rdbms.insertRecord(TblsProcedureConfig.TablesProcedureConfig.STAGE_TIMING_INTERVAL,
                                    fldsName, fldsValue, instanceName);
                            this.diagnostic = new InternalMessage(insertRecord.getSqlStatement(), insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables(), insertRecord.getNewRowId());
                        }
                        jLog.put(GlobalAPIsParams.LBL_DIAGNOSTIC, this.diagnostic.getDiagnostic());
                        jLogArr.add(jLog);
                    }
                    calculateDiagnostic(jLogArr);
                    break;
                case MD_INSTRUMENTS:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    for (JsonElement jO : asJsonArray) {
                        Object[] fldsInfo = getFldsNamesAndValues(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS, jO);
                        if (fldsInfo.length == 3) {
                            this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, fldsInfo[2].toString(), null, null);
                        } else {
                            RdbmsObject insertRecord = Rdbms.insertRecord(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS,
                                    (String[]) fldsInfo[0], (Object[]) fldsInfo[1], instanceName);
                            this.diagnostic = new InternalMessage(insertRecord.getSqlStatement(), insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables(), insertRecord.getNewRowId());
                        }
                    }
                    this.diagnostic = new InternalMessage(LPPlatform.LAB_TRUE, "Inserted " + asJsonArray.size() + " new " + endPoint.name().toLowerCase(), null, null);
                    break;
                case MD_VARIABLES:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    for (JsonElement jO : asJsonArray) {
                        Object[] fldsInfo = getFldsNamesAndValues(TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES, jO);
                        if (fldsInfo.length == 3) {
                            this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, fldsInfo[2].toString(), null, null);
                        } else {
                            RdbmsObject insertRecord = Rdbms.insertRecord(TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES,
                                    (String[]) fldsInfo[0], (Object[]) fldsInfo[1], instanceName);
                            this.diagnostic = new InternalMessage(insertRecord.getSqlStatement(), insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables(), insertRecord.getNewRowId());
                        }
                    }
                    this.diagnostic = new InternalMessage(LPPlatform.LAB_TRUE, "Inserted " + asJsonArray.size() + " new " + endPoint.name().toLowerCase(), null, null);
                    break;
                case MD_VARIABLES_SET:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    for (JsonElement jO : asJsonArray) {
                        Object[] fldsInfo = getFldsNamesAndValues(TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES_SET, jO);
                        if (fldsInfo.length == 3) {
                            this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, fldsInfo[2].toString(), null, null);
                        } else {
                            RdbmsObject insertRecord = Rdbms.insertRecord(TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES_SET,
                                    (String[]) fldsInfo[0], (Object[]) fldsInfo[1], instanceName);
                            this.diagnostic = new InternalMessage(insertRecord.getSqlStatement(), insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables(), insertRecord.getNewRowId());
                        }
                    }
                    this.diagnostic = new InternalMessage(LPPlatform.LAB_TRUE, "Inserted " + asJsonArray.size() + " new " + endPoint.name().toLowerCase(), null, null);
                    break;
                case MD_UOM:
                    asJsonArray = jsonObject.get(GlobalAPIsParams.LBL_VALUES).getAsJsonArray();
                    for (JsonElement jO : asJsonArray) {
                        String uomName = jO.getAsJsonObject().get("name").getAsString();
                        String importType = UnitsOfMeasurement.UomImportType.INDIV.toString();
                        if (jO.getAsJsonObject().has("import_all_family") && jO.getAsJsonObject().get("import_all_family").getAsBoolean()) {
                            UnitsOfMeasurement.UomImportType.FAMIL.toString();
                        }
                        this.diagnostic = new InternalMessage(LPPlatform.LAB_TRUE, "Inserted " + asJsonArray.size() + " new " + endPoint.name().toLowerCase(), null, null);
                    }
                    break;
                default:
                    this.diagnostic = new InternalMessage(LPPlatform.LAB_FALSE, "mdParserNotFound", new Object[]{endPoint.name()}, null);
                    break;
            }
        }
        }catch(Exception e){
            String log = e.getMessage();
        }
    }

    /**
     * @return the endpointExists
     */
    public Boolean getObjectTypeExists() {
        return objectTypeExists;
    }

    /**
     * @return the diagnostic
     */
    public InternalMessage getDiagnostic() {
        return diagnostic;
    }

    private Object[] getFldsNamesAndValues(EnumIntTables tbl, JsonElement jO) {
        String[] fldNames = new String[0];
        Object[] fldValues = new Object[0];
        String curFldName = "";
        try {
            for (EnumIntTableFields curFld : tbl.getTableFields()) {
                curFldName = curFld.getName();
                if (jO.getAsJsonObject().has(curFldName)) {
                    switch (EnumIntTableFields.getFldType(curFld).toUpperCase()) {
                        case "STRING":
                            fldNames = LPArray.addValueToArray1D(fldNames, curFld.getName());
                            fldValues = LPArray.addValueToArray1D(fldValues, jO.getAsJsonObject().get(curFld.getName()).getAsString());
                            break;
                        case "BOOLEAN":
                            fldNames = LPArray.addValueToArray1D(fldNames, curFld.getName());
                            fldValues = LPArray.addValueToArray1D(fldValues, jO.getAsJsonObject().get(curFld.getName()).getAsBoolean());
                            break;
                        case "DATE":
                            fldNames = LPArray.addValueToArray1D(fldNames, curFld.getName());
                            fldValues = LPArray.addValueToArray1D(fldValues, LPDate.stringFormatToDate(jO.getAsJsonObject().get(curFld.getName()).getAsString()));
                            break;
                        case "INTEGER":
                            fldNames = LPArray.addValueToArray1D(fldNames, curFld.getName());
                            fldValues = LPArray.addValueToArray1D(fldValues, jO.getAsJsonObject().get(curFld.getName()).getAsInt());
                            break;
                        case "REAL":
                            fldNames = LPArray.addValueToArray1D(fldNames, curFld.getName());
                            fldValues = LPArray.addValueToArray1D(fldValues, jO.getAsJsonObject().get(curFld.getName()).getAsFloat());
                            break;
                        default:
                            String error = curFld.getFieldType() + " type not recognized for field " + curFld.getName();
                            return new Object[]{fldNames, fldValues, error};
                    }
                }
                //String[] fieldsToRetrieve = getAllFieldNames(tbl);
                //for (String curFldName: getAllFieldNames(tbl)){            
                //    jO.getAsJsonObject().get(curFldName).getAsString();
                //}                
            }
            return new Object[]{fldNames, fldValues};
        } catch (Exception e) {
            return new Object[]{fldNames, fldValues, curFldName + " " + e.getMessage()};
        }
    }

    private void calculateDiagnostic(JSONArray jLogArr) {
        JSONObject jMainLogArr = new JSONObject();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(globalDiagn)) {
            jMainLogArr.put("global_diagnostic", "Errors found");
        } else {
            jMainLogArr.put("global_diagnostic", "success");
        }
        jMainLogArr.put("detail", jLogArr);
        this.jMainLogArr = jMainLogArr;
        //this.diagnostic = new InternalMessage(globalDiagn, globalDiagn, new Object[]{}, null);
    }

    /**
     * @return the jMainLogArr
     */
    public JSONObject getjMainLogArr() {
        return jMainLogArr;
    }
}
