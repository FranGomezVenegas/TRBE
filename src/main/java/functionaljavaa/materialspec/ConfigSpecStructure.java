/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.materialspec;

import lbplanet.utilities.LPNulls;
import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import databases.TblsCnfg;
import functionaljavaa.audit.ConfigTablesAudit;
import functionaljavaa.audit.ConfigTablesAudit.ConfigSpecAuditEvents;
import functionaljavaa.materialspec.ConfigSpecRule.quantitativeRulesErrors;
import functionaljavaa.materialspec.ConfigSpecRule.quantitativeVariables;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPParadigm.ParadigmErrorTrapping;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;

import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;

/**
 * The specification is considered one structure belonging to the material
 * definition.<br>
 * This class contains all the required to verify that anything related to this
 * structure will be properly defined accordingly
 *
 * @version 0.1
 * @author Fran Gomez
 */
public class ConfigSpecStructure {

    String classVersion = "Class Version=0.1";
    private static final String DIAGNOSES_SUCCESS = "SUCCESS";
    private static final String DIAGNOSES_ERROR = "ERROR";

    private static final String ERROR_TRAPING_ARG_VALUE_LBL_ERROR = "ERROR: ";

    public enum ConfigSpecErrorTrapping implements EnumIntMessages {
        SAMPLE_NOT_FOUND("SampleNotFound", "", ""),
        ERROR_INSERTING_SAMPLE_RECORD("errorInsertingSampleRecord", "", ""),
        MISSING_MANDATORY_FIELDS("MissingMandatoryFields", "MissingMandatoryFields <*1*>", ""),
        MISSING_CONFIG_CODE("MissingConfigCode", "", ""),;

        private ConfigSpecErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
            this.errorCode = errCode;
            this.defaultTextWhenNotInPropertiesFileEn = defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs = defaultTextEs;
        }

        @Override
        public String getErrorCode() {
            return this.errorCode;
        }

        @Override
        public String getDefaultTextEn() {
            return this.defaultTextWhenNotInPropertiesFileEn;
        }

        @Override
        public String getDefaultTextEs() {
            return this.defaultTextWhenNotInPropertiesFileEs;
        }

        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }

    private static String[] getSpecialFields() {
        String[] mySpecialFields = new String[6];

        mySpecialFields[0] = "spec.analyses";
        mySpecialFields[1] = "spec.variation_nameszzz";
        mySpecialFields[2] = "spec_limits.variation_name";
        mySpecialFields[3] = "spec_limits.analysis";
        mySpecialFields[4] = "spec_limits.rule_type";

        return mySpecialFields;
    }

    private static String[] getSpecialFieldsFunction() {
        String[] mySpecialFields = new String[6];

        mySpecialFields[0] = "specialFieldCheckSpecAnalyses";
        mySpecialFields[1] = "specialFieldCheckSpecVariationNames";
        mySpecialFields[2] = "specialFieldCheckSpecLimitsVariationName";
        mySpecialFields[3] = "specialFieldCheckSpecLimitsAnalysis";
        mySpecialFields[4] = "specialFieldCheckSpecLimitsRuleType";

        return mySpecialFields;
    }

    private static String[] getSpecMandatoryFields() {
        return new String[]{};
    }

    private String[] getSpecLimitsMandatoryFields() {
        String[] myMandatoryFields = new String[7];
        myMandatoryFields[0] = TblsCnfg.SpecLimits.VARIATION_NAME.getName();
        myMandatoryFields[1] = TblsCnfg.SpecLimits.ANALYSIS.getName();
        myMandatoryFields[2] = TblsCnfg.SpecLimits.METHOD_NAME.getName();
        myMandatoryFields[3] = TblsCnfg.SpecLimits.METHOD_VERSION.getName();
        myMandatoryFields[4] = TblsCnfg.SpecLimits.PARAMETER.getName();
        myMandatoryFields[5] = TblsCnfg.SpecLimits.RULE_TYPE.getName();
        myMandatoryFields[6] = TblsCnfg.SpecLimits.RULE_VARIABLES.getName();
        return myMandatoryFields;
    }

    /**
     *
     * @param parameters
     * @return
     */
    public Object specialFieldCheckSpecAnalyses(Object[] parameters) {
        if (1 == 1) {
            return DIAGNOSES_SUCCESS;
        }
        String[] mandatoryFields = new String[1];
        Object[] mandatoryFieldValue = new String[0];

        String myDiagnoses = "";
        String procInstanceName = parameters[0].toString();
        String variationNames = parameters[1].toString();

        Integer specialFieldIndex = Arrays.asList(mandatoryFields).indexOf("code");
        String specCode = (String) mandatoryFieldValue[specialFieldIndex];

        StringBuilder variationNameExistBuilder = new StringBuilder(0);

        if (1 == 1) {
            myDiagnoses = "SUCCESS, but not implemented yet";
            return myDiagnoses;
        }

        InternalMessage variationNameDiagnosticArray = specVariationGetNamesList(procInstanceName, specCode);
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(variationNameDiagnosticArray.getDiagnostic()))) {
            return DIAGNOSES_SUCCESS;
        } else {
            String[] currVariationNameArray = variationNameDiagnosticArray.getMessageCodeVariables()[0].toString().split("\\|", -1);
            for (String currVariation : currVariationNameArray) {
                if (Boolean.FALSE.equals(variationNames.contains(currVariation))) {
                    if (variationNameExistBuilder.length() > 0) {
                        variationNameExistBuilder.append(",");
                    }

                    variationNameExistBuilder.append(currVariation);
                }
            }
        }
        if (variationNameExistBuilder.length() > 0) {
            return "ERROR: Those variations (" + variationNameExistBuilder.toString() + ") are part of the spec " + specCode + " and cannot be removed from the variations name by this method";
        } else {
            return DIAGNOSES_SUCCESS;
        }

    }

    /**
     *
     * @param parameters
     * @return
     */
    public Object specialFieldCheckSpecVariationNames(Object[] parameters) {
        String[] mandatoryFields = new String[1];
        Object[] mandatoryFieldValue = new String[0];

        String procInstanceName = LPNulls.replaceNull(parameters[0]).toString();
        String variationNames = LPNulls.replaceNull(parameters[1]).toString();

        Integer specialFieldIndex = Arrays.asList(mandatoryFields).indexOf("code");
        String specCode = (String) mandatoryFieldValue[specialFieldIndex];

        StringBuilder variationNameExistBuilder = new StringBuilder(0);

        InternalMessage variationNameDiagnosticArray = specVariationGetNamesList(procInstanceName, specCode);
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(variationNameDiagnosticArray.getDiagnostic()))) {
            return DIAGNOSES_SUCCESS;
        } else {
            String[] currVariationNameArray = variationNameDiagnosticArray.getMessageCodeVariables()[0].toString().split("\\|", -1);
            for (String currVariation : currVariationNameArray) {
                if (Boolean.FALSE.equals(variationNames.contains(currVariation))) {
                    if (variationNameExistBuilder.length() > 0) {
                        variationNameExistBuilder.append(" , ");
                    }

                    variationNameExistBuilder.append(currVariation);
                }
            }
        }
        if (variationNameExistBuilder.length() > 0) {
            return "ERROR: Those variations (" + variationNameExistBuilder.toString() + ") are part of the spec " + specCode + " and cannot be removed from the variations name by this method";
        } else {
            return DIAGNOSES_SUCCESS;
        }
    }

    /**
     *
     * @param procInstanceName
     * @param specCode
     * @param specCodeVersion
     * @param mandatoryFieldValue
     * @param mandatoryFields
     * @return
     */
    public String specialFieldCheckSpecLimitsVariationName(String procInstanceName, String specCode, Integer specCodeVersion, String[] mandatoryFields, Object[] mandatoryFieldValue) {
        String analysesMissing = "";
        String myDiagnoses = "";
        String specVariations = "";
        String schemaName = GlobalVariables.Schemas.CONFIG.getName();
        schemaName = LPPlatform.buildSchemaName(procInstanceName, schemaName);

        Integer specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(TblsCnfg.SpecLimits.VARIATION_NAME.getName());
        String varationName = (String) mandatoryFieldValue[specialFieldIndex];

        Object[][] recordFieldsByFilter = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaName, TblsCnfg.TablesConfig.SPEC.getTableName(),
                new String[]{TblsCnfg.Spec.CODE.getName(), TblsCnfg.Spec.CONFIG_VERSION.getName()},
                new Object[]{specCode, specCodeVersion},
                new String[]{TblsCnfg.Spec.VARIATION_NAMES.getName(), TblsCnfg.Spec.CODE.getName(), TblsCnfg.Spec.CONFIG_VERSION.getName(), TblsCnfg.Spec.CODE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(recordFieldsByFilter[0][0].toString())) {
            myDiagnoses = ERROR_TRAPING_ARG_VALUE_LBL_ERROR + recordFieldsByFilter[0][3];
            return myDiagnoses;
        }

        specVariations = recordFieldsByFilter[0][0].toString();
        String[] strArray = specVariations.split("\\|", -1);

        if (Arrays.asList(strArray).indexOf(varationName) == -1) {
            myDiagnoses = "ERROR: The variation " + varationName + " is not one of the variations (" + specVariations.replace("|", ", ") + ") on spec " + specCode + "  in the schema " + procInstanceName + ". Missed analysis=" + analysesMissing;
        } else {
            myDiagnoses = DIAGNOSES_SUCCESS;
        }
        return myDiagnoses;
    }

    /**
     *
     * @param procInstanceName
     * @param specCode
     * @param specCodeVersion
     * @param mandatoryFields
     * @param mandatoryFieldValue
     * @return
     */
    public String specialFieldCheckSpecLimitsAnalysis(String procInstanceName, String specCode, Integer specCodeVersion, String[] mandatoryFields, Object[] mandatoryFieldValue) {
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());

        Integer specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(TblsCnfg.SpecLimits.ANALYSIS.getName());
        String analysis = (String) mandatoryFieldValue[specialFieldIndex];
        if (analysis.length() == 0) {
            return "ERROR: The parameter analysis cannot be null";
        }

        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(TblsCnfg.SpecLimits.METHOD_NAME.getName());
        String methodName = (String) mandatoryFieldValue[specialFieldIndex];
        if (methodName.length() == 0) {
            return "ERROR: The parameter method_name cannot be null";
        }

        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(TblsCnfg.SpecLimits.METHOD_VERSION.getName());
        Integer methodVersion = (Integer) mandatoryFieldValue[specialFieldIndex];
        if (methodVersion == null) {
            return "ERROR: The parameter method_version cannot be null";
        }

        String[] fieldNames = new String[]{TblsCnfg.AnalysisMethod.ANALYSIS.getName(), TblsCnfg.AnalysisMethod.METHOD_NAME.getName(), TblsCnfg.AnalysisMethod.METHOD_VERSION.getName()};
        Object[] fieldValues = new Object[]{analysis, methodName, methodVersion};

        Object[] diagnosis = Rdbms.existsRecord(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.ANALYSIS_METHOD.getTableName(), fieldNames, fieldValues);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())) {
            return DIAGNOSES_SUCCESS;
        } else {
            diagnosis = Rdbms.existsRecord(procInstanceName,  schemaConfigName, TblsCnfg.SpecLimits.ANALYSIS.getName(),
                    new String[]{"code"}, new Object[]{analysis});
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())) {
                return "ERROR: The analysis " + analysis + " exists but the method " + methodName + " with version " + methodVersion + " was not found in the schema " + procInstanceName;
            } else {
                return "ERROR: The analysis " + analysis + " is not found in the schema " + procInstanceName;
            }
        }
    }

    /**
     * bm
     *
     * @return
     */
    public String specialFieldCheckSpecLimitsRuleType(String procInstanceName, String specCode, Integer specCodeVersion, String[] mandatoryFields, Object[] mandatoryFieldValue) {
        Integer specialFieldIndex = Arrays.asList(mandatoryFields).indexOf("rule_type");
        String ruleType = (String) mandatoryFieldValue[specialFieldIndex];

        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf("rule_variables");
        String ruleVariables = (String) mandatoryFieldValue[specialFieldIndex];

        String myDiagnoses = "";

        String[] ruleVariablesArr = ruleVariables.split("\\*");
        switch (ruleType.toUpperCase()) {
            case "QUALITATIVE":
                if (ruleVariablesArr.length != 3 && ruleVariablesArr.length != 2) {
                    myDiagnoses = "ERROR: Qualitative rule type requires 2 or 3 parameters and the string (" + ruleVariables + ") contains " + ruleVariablesArr.length + " parameters";
                    return myDiagnoses;
                }
                ConfigSpecRule qualSpec = new ConfigSpecRule();
                Object[] isCorrect = null;
                if (ruleVariablesArr.length == 2) {
                    isCorrect = qualSpec.specLimitIsCorrectQualitative(ruleVariablesArr[0], ruleVariablesArr[1], null);
                } else {
                    isCorrect = qualSpec.specLimitIsCorrectQualitative(ruleVariablesArr[0], ruleVariablesArr[1], ruleVariablesArr[2]);
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isCorrect[0].toString())) {
                    myDiagnoses = DIAGNOSES_SUCCESS;
                } else {
                    myDiagnoses = ERROR_TRAPING_ARG_VALUE_LBL_ERROR + isCorrect[1];
                }
                break;
            case "QUANTITATIVE":
                Float minSpec = null;
                Float maxSpec = null;
                Float minControl = null;
                Float maxControl = null;
                for (String ruleVar : ruleVariablesArr) {
                    if (ruleVar.contains(quantitativeVariables.MINSPECSTRICT.toString())) {
                        ruleVar = ruleVar.replace(quantitativeVariables.MINSPECSTRICT.toString(), "");
                        minSpec = Float.parseFloat(ruleVar);
                    }
                    if (ruleVar.contains(quantitativeVariables.MAXSPECSTRICT.toString())) {
                        ruleVar = ruleVar.replace(quantitativeVariables.MAXSPECSTRICT.toString(), "");
                        maxSpec = Float.parseFloat(ruleVar);
                    }
                    if (ruleVar.contains(quantitativeVariables.MINCONTROLSTRICT.toString())) {
                        ruleVar = ruleVar.replace(quantitativeVariables.MINCONTROLSTRICT.toString(), "");
                        minControl = Float.parseFloat(ruleVar);
                    }
                    if (ruleVar.contains(quantitativeVariables.MAXCONTROLSTRICT.toString())) {
                        ruleVar = ruleVar.replace(quantitativeVariables.MAXCONTROLSTRICT.toString(), "");
                        maxControl = Float.parseFloat(ruleVar);
                    }
                    if (ruleVar.contains(quantitativeVariables.MINSPEC.toString())) {
                        ruleVar = ruleVar.replace(quantitativeVariables.MINSPEC.toString(), "");
                        minSpec = Float.parseFloat(ruleVar);
                    }
                    if (ruleVar.contains(quantitativeVariables.MAXSPEC.toString())) {
                        ruleVar = ruleVar.replace(quantitativeVariables.MAXSPEC.toString(), "");
                        maxSpec = Float.parseFloat(ruleVar);
                    }
                    if (ruleVar.contains(quantitativeVariables.MINCONTROL.toString())) {
                        ruleVar = ruleVar.replace(quantitativeVariables.MINCONTROL.toString(), "");
                        minControl = Float.parseFloat(ruleVar);
                    }
                    if (ruleVar.contains(quantitativeVariables.MAXCONTROL.toString())) {
                        ruleVar = ruleVar.replace(quantitativeVariables.MAXCONTROL.toString(), "");
                        maxControl = Float.parseFloat(ruleVar);
                    }
                }
                ConfigSpecRule quantSpec2 = new ConfigSpecRule();
                isCorrect = quantSpec2.specLimitIsCorrectQuantitative(minSpec, maxSpec, minControl, maxControl);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isCorrect[0].toString())) {
                    myDiagnoses = DIAGNOSES_SUCCESS;
                } else {
                    myDiagnoses = ERROR_TRAPING_ARG_VALUE_LBL_ERROR + isCorrect[1];
                }
                break;
            default:
                myDiagnoses = "ERROR: The rule type " + ruleType + " is not recognized";
                break;
        }
        return myDiagnoses;
    }

    /**
     *
     * @return
     */
    public Object[] zspecRemove() {
        //String procInstanceName, String code. Estos son candidatos a argumentos, no esta implementado aun, no borrar.
        return new Object[6];
    }

    /**
     *
     * @param specCode
     * @param specCodeVersion
     * @param specFieldName
     * @param specFieldValue
     * @return
     */
    public InternalMessage specUpdate(String specCode, Integer specCodeVersion, String[] specFieldName, Object[] specFieldValue) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        Object[] errorDetailVariables = new Object[0];

        Object[] diagnoses = Rdbms.existsRecord(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.SPEC.getTableName(),
                new String[]{TblsCnfg.Spec.CODE.getName(), TblsCnfg.Spec.CONFIG_VERSION.getName()}, new Object[]{specCode, specCodeVersion});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, "Spec <*1*> or version <*2*> not found in procedure <*3*>", new Object[]{specCode, specCodeVersion, procInstanceName}, null);
        }

        String[] specialFields = getSpecialFields();
        String[] specialFieldsFunction = getSpecialFieldsFunction();

        for (Integer inumLines = 0; inumLines < specFieldName.length; inumLines++) {
            String currField = "spec." + specFieldName[inumLines];
            String currFieldValue = specFieldValue[inumLines].toString();
            boolean contains = Arrays.asList(specialFields).contains(currField);
            if (contains) {
                Integer specialFieldIndex = Arrays.asList(specialFields).indexOf(currField);
                String aMethod = specialFieldsFunction[specialFieldIndex];
                Method method = null;
                try {
                    Class<?>[] paramTypes = {Object[].class};
                    method = getClass().getDeclaredMethod(aMethod, paramTypes);
                } catch (NoSuchMethodException | SecurityException ex) {
                    Logger.getLogger(ConfigSpecStructure.class.getName()).log(Level.SEVERE, null, ex);
                }
                Object[] parameters = new Object[3];
                parameters[0] = schemaConfigName;
                parameters[1] = currFieldValue;
                parameters[2] = specCode;
                Object specialFunctionReturn = DIAGNOSES_ERROR;
                try {
                    if (method != null) {
                        specialFunctionReturn = method.invoke(this, parameters);
                    }
                } catch (IllegalAccessException | NullPointerException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(ConfigSpecStructure.class.getName()).log(Level.SEVERE, null, ex);
                }
                if ((specialFunctionReturn.toString().contains(DIAGNOSES_ERROR))) {
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, currField);
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, aMethod);
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, LPNulls.replaceNull(specialFunctionReturn));
                    return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.UNHANDLED_EXCEPTION_IN_CODE, errorDetailVariables, null);
                }
            }
        }
        try {
            Object[] whereFieldValues = new Object[0];
            whereFieldValues = LPArray.addValueToArray1D(whereFieldValues, specCode);
            whereFieldValues = LPArray.addValueToArray1D(whereFieldValues, specCodeVersion);

            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsCnfg.Spec.CODE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{specCode}, "");
            sqlWhere.addConstraint(TblsCnfg.Spec.CONFIG_VERSION, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{specCodeVersion}, "");
            diagnoses = Rdbms.updateRecordFieldsByFilter(TblsCnfg.TablesConfig.SPEC,
                    EnumIntTableFields.getTableFieldsFromString(TblsCnfg.TablesConfig.SPEC, specFieldName),
                    specFieldValue, sqlWhere, null);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                ConfigTablesAudit.specAuditAdd(ConfigSpecAuditEvents.SPEC_UPDATE, TblsCnfg.TablesConfig.SPEC, specCode,
                        specCode, specCodeVersion, specFieldName, specFieldValue, null);
                String[] specRulesFldNames = new String[]{TblsCnfg.SpecRules.CODE.getName(), TblsCnfg.SpecRules.CONFIG_VERSION.getName(),
                    TblsCnfg.SpecRules.ALLOW_OTHER_ANALYSIS.getName(), TblsCnfg.SpecRules.ALLOW_MULTI_SPEC.getName()};
                Object[] specRulesFldValues = new Object[]{specCode, specCodeVersion, false, false};
                RdbmsObject insertDiagn = Rdbms.insertRecordInTable(TblsCnfg.TablesConfig.SPEC_RULES,
                        specRulesFldNames, specRulesFldValues);
                if (Boolean.TRUE.equals(insertDiagn.getRunSuccess())) {
                    ConfigTablesAudit.specAuditAdd(ConfigSpecAuditEvents.SPEC_UPDATE, TblsCnfg.TablesConfig.SPEC_RULES, specCode,
                            specCode, specCodeVersion, specRulesFldNames, specRulesFldValues, null);
                    return new InternalMessage(LPPlatform.LAB_TRUE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
                } else {
                    return new InternalMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
                }

            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ConfigSpecStructure.class.getName()).log(Level.SEVERE, null, ex);
        }
        String params = "ProcInstanceName: " + procInstanceName + "specCode" + specCode + "specCodeVersion" + specCodeVersion.toString()
                + "specFieldName" + Arrays.toString(specFieldName) + "specFieldValue" + Arrays.toString(specFieldValue);
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, params);
        return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.UNHANDLED_EXCEPTION_IN_CODE, errorDetailVariables, null);
    }

    /**
     *
     * @param specCode
     * @param specCodeVersion
     * @param specFieldName
     * @param specFieldValue
     * @param specRulesFieldName
     * @param specRulesFieldValue
     * @return
     */
    public InternalMessage specNew(String specCode, Integer specCodeVersion, String[] specFieldName, Object[] specFieldValue, String[] specRulesFieldName, Object[] specRulesFieldValue) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] mandatoryFieldValue = new String[0];
        StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);

        String errorCode = "";
        String[] errorDetailVariables = new String[0];

        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());

        String[] mandatoryFields = getSpecMandatoryFields();

        Object[] checkTwoArraysSameLength = LPArray.checkTwoArraysSameLength(specFieldName, specFieldValue);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkTwoArraysSameLength[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, checkTwoArraysSameLength[checkTwoArraysSameLength.length - 1].toString(), null, null);
        }

        if (LPArray.duplicates(specFieldName)) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, Arrays.toString(specFieldName));
            return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.FIELDS_DUPLICATED, errorDetailVariables, null);
        }

        for (Integer inumLines = 0; inumLines < mandatoryFields.length; inumLines++) {
            String currField = mandatoryFields[inumLines];
            boolean contains = Arrays.asList(specFieldName).contains(currField.toLowerCase());
            if (Boolean.FALSE.equals(contains)) {
                if (mandatoryFieldsMissingBuilder.length() > 0) {
                    mandatoryFieldsMissingBuilder.append(",");
                }

                mandatoryFieldsMissingBuilder.append(currField);
            } else {
                Object currFieldValue = specFieldValue[Arrays.asList(specFieldName).indexOf(currField.toLowerCase())];
                mandatoryFieldValue = LPArray.addValueToArray1D(mandatoryFieldValue, currFieldValue);
            }
        }
        if (mandatoryFieldsMissingBuilder.length() > 0) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, mandatoryFieldsMissingBuilder.toString());
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, procInstanceName);
            return new InternalMessage(LPPlatform.LAB_FALSE, ConfigSpecErrorTrapping.MISSING_MANDATORY_FIELDS, errorDetailVariables, null);
        }

        String[] specialFields = getSpecialFields();
        String[] specialFieldsFunction = getSpecialFieldsFunction();
        for (Integer inumLines = 0; inumLines < specFieldName.length; inumLines++) {
            String currField = "spec." + specFieldName[inumLines];
            String currFieldValue = specFieldValue[inumLines].toString();
            boolean contains = Arrays.asList(specialFields).contains(currField);
            if (contains) {
                Integer specialFieldIndex = Arrays.asList(specialFields).indexOf(currField);
                String aMethod = specialFieldsFunction[specialFieldIndex];
                Method method = null;
                Object specialFunctionReturn = DIAGNOSES_ERROR;
                try {
                    Class<?>[] paramTypes = {Object[].class};
                    method = this.getClass().getDeclaredMethod(aMethod, paramTypes);
                } catch (NoSuchMethodException | SecurityException ex) {
                    Logger.getLogger(ConfigSpecStructure.class.getName()).log(Level.SEVERE, null, ex);
                }
                Object[] parameters = new Object[3];
                parameters[0] = schemaConfigName;
                parameters[1] = currFieldValue;
                parameters[2] = specCode;
                method = null;
                specialFunctionReturn = DIAGNOSES_SUCCESS;
                if (method != null) {
                    try {
                        specialFunctionReturn = method.invoke(this, parameters);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(ConfigSpecStructure.class.getName()).log(Level.SEVERE, null, ex);
                        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, currField);
                        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, aMethod);
                        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, specialFunctionReturn.toString());
                        return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.SPECIAL_FUNCTION_RETURNED_ERROR, errorDetailVariables, null);
                    }
                }
                if (specialFunctionReturn.toString().contains(DIAGNOSES_ERROR)) {
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, currField);
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, aMethod);
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, specialFunctionReturn.toString());
                    return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.SPECIAL_FUNCTION_RETURNED_ERROR, errorDetailVariables, null);
                }
            }
        }
        Object[] diagnoses = Rdbms.existsRecord(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.SPEC.getTableName(),
                new String[]{TblsCnfg.Spec.CODE.getName(), TblsCnfg.Spec.CONFIG_VERSION.getName()},
                new Object[]{specCode, specCodeVersion});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
            diagnoses = Rdbms.existsRecord(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.SPEC_RULES.getTableName(),
                    new String[]{TblsCnfg.SpecRules.CODE.getName(), TblsCnfg.SpecRules.CONFIG_VERSION.getName()},
                    new Object[]{specCode, specCodeVersion});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())) {
                String[] specRulesFldNames = new String[]{TblsCnfg.SpecRules.CODE.getName(), TblsCnfg.SpecRules.CONFIG_VERSION.getName(),
                    TblsCnfg.SpecRules.ALLOW_OTHER_ANALYSIS.getName(), TblsCnfg.SpecRules.ALLOW_MULTI_SPEC.getName()};
                Object[] specRulesFldValues = new Object[]{specCode, specCodeVersion, false, false};
                if (specRulesFieldName != null) {
                    for (int i = 0; i < specRulesFieldName.length; i++) {
                        if (LPArray.valueInArray(specRulesFldNames, specRulesFieldName[i])) {
                            specRulesFldValues[LPArray.valuePosicInArray(specRulesFldNames, specRulesFieldName[i])] = specRulesFieldValue[i];
                        } else {
                            LPArray.addValueToArray1D(specRulesFldNames, specRulesFieldName[i]);
                            LPArray.addValueToArray1D(specRulesFldValues, specRulesFieldValue[i]);
                        }
                    }
                }
                RdbmsObject insertDiagn = Rdbms.insertRecordInTable(TblsCnfg.TablesConfig.SPEC_RULES,
                        specRulesFldNames, specRulesFldValues);
                if (Boolean.TRUE.equals(insertDiagn.getRunSuccess())) {
                    ConfigTablesAudit.specAuditAdd(ConfigSpecAuditEvents.SPEC_NEW, TblsCnfg.TablesConfig.SPEC_RULES, specCode,
                            specCode, specCodeVersion, specRulesFldNames, specRulesFldValues, null);
                    new InternalMessage(LPPlatform.LAB_TRUE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
                } else {
                    new InternalMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
                }
            }
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, specCode);
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, specCodeVersion.toString());
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, schemaConfigName);
            return new InternalMessage(LPPlatform.LAB_FALSE, quantitativeRulesErrors.SPEC_RECORD_ALREADY_EXISTS, errorDetailVariables, null);
        }
        try {
            if (Boolean.FALSE.equals(LPArray.valueInArray(specFieldName, TblsCnfg.SpecLimits.CODE.getName()))) {
                specFieldName = LPArray.addValueToArray1D(specFieldName, TblsCnfg.SpecLimits.CODE.getName());
                specFieldValue = LPArray.addValueToArray1D(specFieldValue, specCode);
            }
            if (Boolean.FALSE.equals(LPArray.valueInArray(specFieldName, TblsCnfg.SpecLimits.CONFIG_VERSION.getName()))) {
                specFieldName = LPArray.addValueToArray1D(specFieldName, TblsCnfg.SpecLimits.CONFIG_VERSION.getName());
                specFieldValue = LPArray.addValueToArray1D(specFieldValue, specCodeVersion);
            }
            RdbmsObject insertDiagn = Rdbms.insertRecordInTable(TblsCnfg.TablesConfig.SPEC, specFieldName, specFieldValue);
            if (Boolean.FALSE.equals(insertDiagn.getRunSuccess())) {
                return new InternalMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables(), null);
            } else {
                ConfigTablesAudit.specAuditAdd(ConfigSpecAuditEvents.SPEC_NEW, TblsCnfg.TablesConfig.SPEC, specCode,
                        specCode, specCodeVersion, specFieldName, specFieldValue, null);
                String[] specRulesFldNames = new String[]{TblsCnfg.SpecRules.CODE.getName(), TblsCnfg.SpecRules.CONFIG_VERSION.getName(),
                    TblsCnfg.SpecRules.ALLOW_OTHER_ANALYSIS.getName(), TblsCnfg.SpecRules.ALLOW_MULTI_SPEC.getName()};
                Object[] specRulesFldValues = new Object[]{specCode, specCodeVersion, false, false};
                if (specRulesFieldName != null) {
                    for (int i = 0; i < specRulesFieldName.length; i++) {
                        if (LPArray.valueInArray(specRulesFldNames, specRulesFieldName[i])) {
                            specRulesFldValues[LPArray.valuePosicInArray(specRulesFldNames, specRulesFieldName[i])] = specRulesFieldValue[i];
                        } else {
                            LPArray.addValueToArray1D(specRulesFldNames, specRulesFieldName[i]);
                            LPArray.addValueToArray1D(specRulesFldValues, specRulesFieldValue[i]);
                        }
                    }
                }
                insertDiagn = Rdbms.insertRecordInTable(TblsCnfg.TablesConfig.SPEC_RULES,
                        specRulesFldNames, specRulesFldValues);
                if (Boolean.FALSE.equals(insertDiagn.getRunSuccess())) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
                }

                ConfigTablesAudit.specAuditAdd(ConfigSpecAuditEvents.SPEC_NEW, TblsCnfg.TablesConfig.SPEC_RULES, specCode,
                        specCode, specCodeVersion, specRulesFldNames, specRulesFldValues, null);
            }
            errorCode = "specRecord_createdSuccessfully";
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, specCode);
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, schemaConfigName);
            return new InternalMessage(LPPlatform.LAB_TRUE, errorCode, errorDetailVariables, null);

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ConfigSpecStructure.class.getName()).log(Level.SEVERE, null, ex);
        }
        String params = "procInstanceName: " + procInstanceName + "specFieldName: " + Arrays.toString(specFieldName) + "specFieldValue: " + Arrays.toString(specFieldValue);
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, params);
        return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.UNHANDLED_EXCEPTION_IN_CODE, errorDetailVariables);
    }

    /**
     * @param procInstanceName
     * @param specCode
     * @return
     */
    public InternalMessage updateSpecRules(String specCode, Integer specCodeVersion, String[] specFieldName, Object[] specFieldValue) {
        return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
    }
    public InternalMessage specVariationGetNamesList(String procInstanceName, String specCode) {

        String schemaName = GlobalVariables.Schemas.CONFIG.getName();
        StringBuilder variationListBuilder = new StringBuilder(0);
        String errorCode = "";

        schemaName = LPPlatform.buildSchemaName(procInstanceName, schemaName);

        Object[][] variationListArray = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaName, TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName(),
                new String[]{TblsCnfg.SpecLimits.CODE.getName()}, new Object[]{specCode},
                new String[]{TblsCnfg.SpecLimits.VARIATION_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variationListArray[0][0].toString())) {
            errorCode = "specVariationGetNamesList_error";
            return new InternalMessage(LPPlatform.LAB_FALSE, errorCode, new Object[]{variationListArray[variationListArray.length - 1]}, null);
        } else {
            for (int i = 0; i <= variationListArray.length; i++) {
                if (variationListBuilder.length() > 0) {
                    variationListBuilder.append("|");
                }
                variationListBuilder.append(variationListArray[i][0].toString());
            }
            errorCode = "specVariationGetNamesList_successfully";
            return new InternalMessage(LPPlatform.LAB_TRUE, errorCode, new String[]{variationListBuilder.toString()}, null);
        }

    }

    /**
     *
     * @param specCode
     * @param specCodeVersion
     * @param specFieldName
     * @param specFieldValue
     * @return
     */
    public InternalMessage specLimitNew(String specCode, Integer specCodeVersion, String[] specFieldName, Object[] specFieldValue) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] mandatoryFieldValue = new String[0];
        StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);
        Object[] errorDetailVariables = new Object[0];

        String schemaName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        String[] mandatoryFields = getSpecLimitsMandatoryFields();

        Object[] checkTwoArraysSameLength = LPArray.checkTwoArraysSameLength(specFieldName, specFieldValue);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkTwoArraysSameLength[0].toString())) {
            return new InternalMessage(checkTwoArraysSameLength[0].toString(), TrazitUtilitiesErrorTrapping.ARRAYS_DIFFERENT_SIZE, new Object[]{checkTwoArraysSameLength[checkTwoArraysSameLength.length - 1].toString()}, null);
        }

        if (LPArray.duplicates(specFieldName)) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, Arrays.toString(specFieldName));
            return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.FIELDS_DUPLICATED, errorDetailVariables);
        }
        Integer fieldIndex = Arrays.asList(specFieldName).indexOf(TblsCnfg.AnalysisMethod.ANALYSIS.getName());
        String analysis = (String) specFieldValue[fieldIndex];
        String[] keyFieldNames = new String[]{TblsCnfg.SpecLimits.CODE.getName(), TblsCnfg.SpecLimits.CONFIG_VERSION.getName(), TblsCnfg.SpecLimits.ANALYSIS.getName()};
        Object[] keyFieldValues = new Object[]{specCode, specCodeVersion, analysis};
        keyFieldNames = LPArray.addValueToArray1D(keyFieldNames, TblsCnfg.SpecLimits.VARIATION_NAME.getName());
        fieldIndex = Arrays.asList(specFieldName).indexOf(TblsCnfg.SpecLimits.VARIATION_NAME.getName());
        keyFieldValues = LPArray.addValueToArray1D(keyFieldValues, specFieldValue[fieldIndex]);

        Integer fieldIndexMethodName = Arrays.asList(specFieldName).indexOf(TblsCnfg.AnalysisMethod.METHOD_NAME.getName());
        Integer fieldIndexMethodVersion = Arrays.asList(specFieldName).indexOf(TblsCnfg.AnalysisMethod.METHOD_VERSION.getName());
        String methodName = "";
        Integer methodVersion = -1;
        if (fieldIndexMethodName > -1) {
            methodName = (String) specFieldValue[fieldIndexMethodName];
            keyFieldNames = LPArray.addValueToArray1D(keyFieldNames, TblsCnfg.SpecLimits.METHOD_NAME.getName());
            keyFieldValues = LPArray.addValueToArray1D(keyFieldValues, methodName);
        }
        if (fieldIndexMethodVersion > -1) {
            methodVersion = (Integer) specFieldValue[fieldIndexMethodVersion];
            keyFieldNames = LPArray.addValueToArray1D(keyFieldNames, TblsCnfg.SpecLimits.METHOD_VERSION.getName());
            keyFieldValues = LPArray.addValueToArray1D(keyFieldValues, methodVersion);
        }

        Object[][] analysisMethods = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaName, TblsCnfg.TablesConfig.ANALYSIS_METHOD.getTableName(),
                new String[]{TblsCnfg.AnalysisMethod.ANALYSIS.getName()}, new Object[]{analysis},
                new String[]{TblsCnfg.AnalysisMethod.METHOD_NAME.getName(), TblsCnfg.AnalysisMethod.METHOD_VERSION.getName()},
                new String[]{"1"}, true);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisMethods[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, "analysis <*1*> not found", new Object[]{analysis}, null);
        }
        if (analysisMethods.length > 1) {
            Integer methodArrPosic=LPArray.valuePosicInArray(LPArray.getColumnFromArray2D(analysisMethods, 0), methodName);
            if (methodArrPosic==-1) {
                return new InternalMessage(LPPlatform.LAB_FALSE, "analysis <*1*> not found", new Object[]{analysis}, null);
            }
            methodName = (String) analysisMethods[methodArrPosic][0];
            methodVersion = (Integer) analysisMethods[methodArrPosic][1];            
        } else {
            methodName = (String) analysisMethods[0][0];
            methodVersion = (Integer) analysisMethods[0][1];
        }
        if (fieldIndexMethodName > -1) {
            specFieldValue[fieldIndexMethodName] = methodName;
        } else {
            specFieldName = LPArray.addValueToArray1D(specFieldName, TblsCnfg.AnalysisMethod.METHOD_NAME.getName());
            specFieldValue = LPArray.addValueToArray1D(specFieldValue, methodName);
        }
        if (fieldIndexMethodVersion > -1) {
            specFieldValue[fieldIndexMethodVersion] = methodVersion;
        } else {
            specFieldName = LPArray.addValueToArray1D(specFieldName, TblsCnfg.AnalysisMethod.METHOD_VERSION.getName());
            specFieldValue = LPArray.addValueToArray1D(specFieldValue, methodVersion);
        }

        Object[] existsRecord = Rdbms.existsRecord(TblsCnfg.TablesConfig.SPEC_LIMITS, keyFieldNames, keyFieldValues, procInstanceName);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())) {            
            return new InternalMessage(LPPlatform.LAB_TRUE, quantitativeRulesErrors.SPEC_RECORD_ALREADY_EXISTS, new Object[]{TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName(), keyFieldNames, keyFieldValues, procInstanceName}, null);
        }
        for (Integer inumLines = 0; inumLines < mandatoryFields.length; inumLines++) {
            String currField = mandatoryFields[inumLines];
            boolean contains = Arrays.asList(specFieldName).contains(currField.toLowerCase());
            if (Boolean.FALSE.equals(contains)) {
                if (mandatoryFieldsMissingBuilder.length() > 0) {
                    mandatoryFieldsMissingBuilder.append(",");
                }

                mandatoryFieldsMissingBuilder.append(currField);
            } else {
                Object currFieldValue = specFieldValue[Arrays.asList(specFieldName).indexOf(currField.toLowerCase())];
                mandatoryFieldValue = LPArray.addValueToArray1D(mandatoryFieldValue, currFieldValue);
            }
        }
        Object[] diagnoses = Rdbms.existsRecord(procInstanceName, schemaName, TblsCnfg.TablesConfig.SPEC.getTableName(),
                new String[]{TblsCnfg.Spec.CODE.getName(), TblsCnfg.Spec.CONFIG_VERSION.getName()},
                new Object[]{specCode, specCodeVersion});
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_TABLE_NOT_FOUND, new Object[]{TblsCnfg.TablesConfig.SPEC.getTableName(), specCode, specCodeVersion, procInstanceName}, null);
        }

        if (mandatoryFieldsMissingBuilder.length() > 0) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, mandatoryFieldsMissingBuilder.toString());
            return new InternalMessage(LPPlatform.LAB_FALSE, ConfigSpecErrorTrapping.MISSING_MANDATORY_FIELDS, errorDetailVariables);
        }

/*        String[] specialFields = getSpecialFields();
        String[] specialFieldsFunction = getSpecialFieldsFunction();
        for (Integer inumLines = 0; inumLines < specFieldName.length; inumLines++) {
            String currField = "spec_limits." + specFieldName[inumLines];
            boolean contains = Arrays.asList(specialFields).contains(currField);
            if (contains) {
                Integer specialFieldIndex = Arrays.asList(specialFields).indexOf(currField);
                String aMethod = specialFieldsFunction[specialFieldIndex];
                Method method = null;
                try {
                    Class<?>[] paramTypes = {String.class, String.class, Integer.class, String[].class, Object[].class};
                    method = getClass().getDeclaredMethod(aMethod, paramTypes);
                } catch (NoSuchMethodException | SecurityException ex) {
                    Logger.getLogger(ConfigSpecStructure.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    Object specialFunctionReturn = DIAGNOSES_ERROR;
                    if (method != null) {
                        try {
                            specialFunctionReturn = method.invoke(this, schemaName, specCode, specCodeVersion, specFieldName, specFieldValue);
                        } catch (IllegalAccessException | IllegalArgumentException ex) {
                            Logger.getLogger(ConfigSpecStructure.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (specialFunctionReturn.toString().contains(DIAGNOSES_ERROR)) {
                        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, specialFunctionReturn.toString());
                        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, currField);
                        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, aMethod);
                        return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.SPECIAL_FUNCTION_RETURNED_ERROR, errorDetailVariables);
                    }
                } catch (InvocationTargetException ite) {
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, aMethod);
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, ite.getMessage());
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, "Spec Limits");
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, schemaName);
                    return new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.SPECIALFUNCTION_CAUSEDEXCEPTION, errorDetailVariables);
                }
            }
        } */
        String[] whereFields = new String[]{TblsCnfg.AnalysisMethod.ANALYSIS.getName(), TblsCnfg.AnalysisMethod.METHOD_NAME.getName(), TblsCnfg.AnalysisMethod.METHOD_VERSION.getName()};
        Object[] whereFieldsValue = new Object[]{analysis, methodName, methodVersion};
        diagnoses = Rdbms.existsRecord(procInstanceName, schemaName, TblsCnfg.TablesConfig.ANALYSIS_METHOD.getTableName(), whereFields, whereFieldsValue);
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString()))) {
            Object[] whereFieldsAndValues = LPArray.joinTwo1DArraysInOneOf1DString(diagnoses, whereFieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR);
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, TblsCnfg.TablesConfig.ANALYSIS_METHOD.getTableName());
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, Arrays.toString(whereFieldsAndValues));
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, schemaName);
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, errorDetailVariables);
        } else {
            fieldIndex = Arrays.asList(specFieldName).indexOf(TblsCnfg.SpecLimits.PARAMETER.getName());
            String parameter = (String) specFieldValue[fieldIndex];
            whereFields = new String[]{TblsCnfg.SpecLimits.ANALYSIS.getName(), TblsCnfg.SpecLimits.METHOD_NAME.getName(), TblsCnfg.SpecLimits.METHOD_VERSION.getName(), "param_name"};
            whereFieldsValue = new Object[]{analysis, methodName, methodVersion, parameter};
            diagnoses = Rdbms.existsRecord(procInstanceName, schemaName, TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS.getTableName(), whereFields, whereFieldsValue);
            if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString()))) {
                Object[] whereFieldsAndValues = LPArray.joinTwo1DArraysInOneOf1DString(diagnoses, whereFieldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR);
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS.getTableName());
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, Arrays.toString(whereFieldsAndValues));
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, schemaName);
                return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{"The parameter " + parameter + " was not found even though the method " + methodName + " in its version " + methodVersion.toString() + " in the analysis " + analysis + " exists in the schema " + schemaName + "......... " + diagnoses[5].toString()}, null);
            }
        }
        try {
            specFieldName = LPArray.addValueToArray1D(specFieldName, TblsCnfg.SpecLimits.CODE.getName());
            specFieldValue = LPArray.addValueToArray1D(specFieldValue, specCode);
            specFieldName = LPArray.addValueToArray1D(specFieldName, TblsCnfg.SpecLimits.CONFIG_VERSION.getName());
            specFieldValue = LPArray.addValueToArray1D(specFieldValue, specCodeVersion);
            RdbmsObject insertDiagn = Rdbms.insertRecordInTable(TblsCnfg.TablesConfig.SPEC_LIMITS, specFieldName, specFieldValue); //, schemaName);
            if (Boolean.TRUE.equals(insertDiagn.getRunSuccess())) {
                ConfigTablesAudit.specAuditAdd(ConfigSpecAuditEvents.SPEC_LIMIT_NEW, TblsCnfg.TablesConfig.SPEC_LIMITS, specCode,
                        specCode, specCodeVersion, specFieldName, specFieldValue, null);
                return new InternalMessage(LPPlatform.LAB_TRUE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
            } else {
                return new InternalMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
            }

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ConfigSpecStructure.class.getName()).log(Level.SEVERE, null, ex);
        }
        String params = "procInstanceName: " + procInstanceName + "specFieldName: " + Arrays.toString(specFieldName) + "specFieldValue: " + Arrays.toString(specFieldValue);
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, params);
        return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.UNHANDLED_EXCEPTION_IN_CODE, errorDetailVariables, null);
    }
}
