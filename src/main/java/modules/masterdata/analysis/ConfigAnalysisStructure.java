/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules.masterdata.analysis;

import lbplanet.utilities.LPNulls;
import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import databases.Rdbms.RdbmsSuccess;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import databases.TblsCnfg;
import databases.TblsCnfgAudit;
import functionaljavaa.audit.ConfigTablesAudit;
import functionaljavaa.audit.ConfigTablesAudit.ConfigAnalysisAuditEvents;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPParadigm.ParadigmErrorTrapping;
import lbplanet.utilities.LPPlatform.LpPlatformErrorTrapping;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import modules.masterdata.analysis.MasterDataAnalysisEnums;
import modules.masterdata.analysis.MasterDataAnalysisEnums.MasterDataAnalysisErrorTrapping;
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
public class ConfigAnalysisStructure {



    private static final String DIAGNOSES_SUCCESS = "SUCCESS";
    private static final String DIAGNOSES_ERROR = "ERROR";
    
    Boolean approvedForUse=true;
    Boolean isCurrentlyActive=true;
    String code;
    Integer configVersion;
    EnumIntTableFields[] fldsToGetObj = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsCnfg.TablesConfig.ANALYSIS);
    Object[] lotInfo;
    private InternalMessage lockedDueToApprovedForUse(){
        return new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisErrorTrapping.LOCKED_DUE_TO_APPROVED_FOR_USE, new Object[]{code});
    }

    public ConfigAnalysisStructure(String code, Integer codeVersion) {      
        
        this.code=code;
        this.configVersion=codeVersion;

        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsCnfg.Analysis.CODE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{code}, "");
        sqlWhere.addConstraint(TblsCnfg.Analysis.CONFIG_VERSION, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{configVersion}, "");


        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
//        EnumIntTableFields[] fldsToGetObj = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsCnfg.TablesConfig.ANALYSIS);
        Object[][] lotInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsCnfg.TablesConfig.ANALYSIS,
            sqlWhere, this.fldsToGetObj, null, false);
        this.approvedForUse=false;
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) return;
        
        this.lotInfo=lotInfo[0];
        Integer appForUseFldPosic = EnumIntTableFields.getFldPosicInArray(this.fldsToGetObj, TblsCnfg.Analysis.APPROVED_FOR_USE.getName());
        if (appForUseFldPosic>-1)
            this.approvedForUse=Boolean.valueOf(LPNulls.replaceNull(lotInfo[0][appForUseFldPosic]).toString());

        this.lotInfo=lotInfo[0];
        Integer isCurrentlyActiveFldPosic = EnumIntTableFields.getFldPosicInArray(this.fldsToGetObj, TblsCnfg.Analysis.ACTIVE.getName());
        if (isCurrentlyActiveFldPosic>-1)
            this.isCurrentlyActive=Boolean.valueOf(LPNulls.replaceNull(lotInfo[0][isCurrentlyActiveFldPosic]).toString());

    } 
    public enum ConfigAnalysisErrorTrapping implements EnumIntMessages {
        ERROR_INSERTING_SAMPLE_RECORD("errorInsertingSampleRecord", "", ""),
        MISSING_MANDATORY_FIELDS("MissingMandatoryFields", "MissingMandatoryFields <*1*>", ""),
        MISSING_CONFIG_CODE("MissingConfigCode", "", ""),
        LOCKED_DUE_TO_APPROVED_FOR_USE("analysisLockedDueToApprovedForUse", "MissingMandatoryFields <*1*>", ""),
        ANALYSIS_IN_USE_BY_SPECS("analysisInUseBySpecs", "MissingMandatoryFields <*1*>", ""),
        ANALYSIS_ALREADY_DEACTIVATED("analysisAlreadyDeactivated", "MissingMandatoryFields <*1*>", "")
        ;
        private ConfigAnalysisErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
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
        return new String[]{//TblsCnfg.AnalysisMethodParams.FLD_ANALYSIS.getName(),
            //    TblsCnfg.AnalysisMethodParams.METHOD_NAME.getName(), TblsCnfg.AnalysisMethodParams.METHOD_VERSION.getName(),
            TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName(), TblsCnfg.AnalysisMethodParams.PARAM_TYPE.getName()};
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
        String schemaName = GlobalVariables.Schemas.CONFIG.getName();
        schemaName = LPPlatform.buildSchemaName(procInstanceName, schemaName);
        if (1 == 1) {
            return "ERROR";
        }
        return DIAGNOSES_SUCCESS;
    }

    /**
     *
     * @param procInstanceName
     * @param specCode
     * @param mandatoryFields
     * @param specCodeVersion
     * @param mandatoryFieldValue
     * @return
     */
    public String specialFieldCheckSpecLimitsAnalysis(String procInstanceName, String specCode, Integer specCodeVersion, String[] mandatoryFields, Object[] mandatoryFieldValue) {
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());

        Integer specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(TblsCnfg.AnalysisMethodParams.ANALYSIS.getName());
        String analysis = (String) mandatoryFieldValue[specialFieldIndex];
        if (analysis.length() == 0) {
            return "ERROR: The parameter analysis cannot be null";
        }

        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(TblsCnfg.AnalysisMethodParams.METHOD_NAME.getName());
        String methodName = (String) mandatoryFieldValue[specialFieldIndex];
        if (methodName.length() == 0) {
            return "ERROR: The parameter method_name cannot be null";
        }

        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(TblsCnfg.AnalysisMethodParams.METHOD_VERSION.getName());
        Integer methodVersion = (Integer) mandatoryFieldValue[specialFieldIndex];
        if (methodVersion == null) {
            return "ERROR: The parameter method_version cannot be null";
        }

        String[] fieldNames = new String[]{TblsCnfg.AnalysisMethod.ANALYSIS.getName(), TblsCnfg.AnalysisMethod.METHOD_NAME.getName()};
        Object[] fieldValues = new Object[]{analysis, methodName};

        Object[] diagnosis = Rdbms.existsRecord(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.ANALYSIS_METHOD.getTableName(), fieldNames, fieldValues);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())) {
            return DIAGNOSES_SUCCESS;
        } else {
            diagnosis = Rdbms.existsRecord(procInstanceName, schemaConfigName, TblsCnfg.AnalysisMethodParams.ANALYSIS.getName(),
                    new String[]{"code"}, new Object[]{analysis});
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())) {
                return "ERROR: The analysis " + analysis + " exists but the method " + methodName + " with version " + methodVersion + " was not found in the schema " + procInstanceName;
            } else {
                return "ERROR: The analysis " + analysis + " is not found in the schema " + procInstanceName;
            }
        }
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
     * @param code
     * @param configVersion
     * @param specFieldName
     * @param specFieldValue
     * @return
     */
    public InternalMessage analysisUpdate(String code, Integer configVersion, String[] specFieldName, Object[] specFieldValue) {

        if (this.approvedForUse)
            return lockedDueToApprovedForUse();
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        Object[] errorDetailVariables = new Object[0];

        Object[] diagnoses = Rdbms.existsRecord(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.ANALYSIS.getTableName(),
                new String[]{TblsCnfg.Analysis.CODE.getName(), TblsCnfg.Analysis.CONFIG_VERSION.getName()}, new Object[]{code, configVersion});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{code, configVersion, procInstanceName});
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
                    Logger.getLogger(ConfigAnalysisStructure.class.getName()).log(Level.SEVERE, null, ex);
                }
                Object[] parameters = new Object[3];
                parameters[0] = schemaConfigName;
                parameters[1] = currFieldValue;
                parameters[2] = code;
                Object specialFunctionReturn = DIAGNOSES_ERROR;
                try {
                    if (method != null) {
                        specialFunctionReturn = method.invoke(this, parameters);
                    }
                } catch (IllegalAccessException | NullPointerException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(ConfigAnalysisStructure.class.getName()).log(Level.SEVERE, null, ex);
                }
                if ((specialFunctionReturn.toString().contains(DIAGNOSES_ERROR))) {
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, currField);
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, aMethod);
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, LPNulls.replaceNull(specialFunctionReturn));
                    return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.UNHANDLED_EXCEPTION_IN_CODE, errorDetailVariables);
                }
            }
        }
        try {
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsCnfg.Analysis.CODE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{code}, "");
            sqlWhere.addConstraint(TblsCnfg.Analysis.CONFIG_VERSION, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{configVersion}, "");
            RdbmsObject updateLog = Rdbms.updateTableRecordFieldsByFilter(TblsCnfg.TablesConfig.ANALYSIS,
                    EnumIntTableFields.getTableFieldsFromString(TblsCnfg.TablesConfig.ANALYSIS, specFieldName), specFieldValue, sqlWhere, null);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                ConfigTablesAudit.analysisAuditAdd(ConfigAnalysisAuditEvents.ANALYSIS_UPDATE, TblsCnfgAudit.TablesCfgAudit.ANALYSIS, code,
                        code, configVersion, specFieldName, specFieldValue, null);
            }
            return new InternalMessage(updateLog.getSqlStatement(), updateLog.getErrorMessageCode(), updateLog.getErrorMessageVariables(), updateLog.getNewRowId());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ConfigAnalysisStructure.class.getName()).log(Level.SEVERE, null, ex);
        }
        String params = "ProcInstanceName: " + procInstanceName + "code" + code + "configVersion" + configVersion.toString()
                + "specFieldName" + Arrays.toString(specFieldName) + "specFieldValue" + Arrays.toString(specFieldValue);
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, params);
        return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.UNHANDLED_EXCEPTION_IN_CODE, errorDetailVariables);
    }

    /**
     *
     * @param code
     * @param configVersion
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public InternalMessage analysisNew(String code, Integer configVersion, String[] fieldName, Object[] fieldValue, String instanceName) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();
        Object[] mandatoryFieldValue = new String[0];
        StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);
        String[] errorDetailVariables = new String[0];
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());

        String[] mandatoryFields = getSpecMandatoryFields();

        Object[] checkTwoArraysSameLength = LPArray.checkTwoArraysSameLength(fieldName, fieldValue);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkTwoArraysSameLength[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, checkTwoArraysSameLength[0].toString(), null, null);
        }

        if (LPArray.duplicates(fieldName)) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, Arrays.toString(fieldName));
            return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.FIELDS_DUPLICATED, errorDetailVariables);
        }
        for (Integer inumLines = 0; inumLines < mandatoryFields.length; inumLines++) {
            String currField = mandatoryFields[inumLines];
            boolean contains = Arrays.asList(fieldName).contains(currField.toLowerCase());
            if (Boolean.FALSE.equals(contains)) {
                if (mandatoryFieldsMissingBuilder.length() > 0) {
                    mandatoryFieldsMissingBuilder.append(",");
                }

                mandatoryFieldsMissingBuilder.append(currField);
            } else {
                Object currFieldValue = fieldValue[Arrays.asList(fieldName).indexOf(currField.toLowerCase())];
                mandatoryFieldValue = LPArray.addValueToArray1D(mandatoryFieldValue, currFieldValue);
            }
        }
        if (mandatoryFieldsMissingBuilder.length() > 0) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, mandatoryFieldsMissingBuilder.toString());
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, procInstanceName);
            return new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, errorDetailVariables);
        }

        String[] specialFields = getSpecialFields();
        String[] specialFieldsFunction = getSpecialFieldsFunction();
        for (Integer inumLines = 0; inumLines < fieldName.length; inumLines++) {
            String currField = "analysis." + fieldName[inumLines];
            String currFieldValue = fieldValue[inumLines].toString();
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
                    Logger.getLogger(ConfigAnalysisStructure.class.getName()).log(Level.SEVERE, null, ex);
                }
                String[] parameters = new String[3];
                parameters[0] = schemaConfigName;
                parameters[1] = currFieldValue;
                parameters[2] = code;
                if (method != null) {
                    try {
                        specialFunctionReturn = method.invoke(this, (Object[]) parameters);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(ConfigAnalysisStructure.class.getName()).log(Level.SEVERE, null, ex);
                        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, currField);
                        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, aMethod);
                        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, specialFunctionReturn.toString());
                        return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.SPECIAL_FUNCTION_RETURNED_ERROR, errorDetailVariables);
                    }
                }
                if (specialFunctionReturn.toString().contains(DIAGNOSES_ERROR)) {
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, currField);
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, aMethod);
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, specialFunctionReturn.toString());
                    return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.SPECIAL_FUNCTION_RETURNED_ERROR, errorDetailVariables);
                }
            }
        }
        Object[] diagnoses = Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, schemaConfigName), TblsCnfg.TablesConfig.ANALYSIS.getTableName(),
                new String[]{TblsCnfg.Analysis.CODE.getName(), TblsCnfg.Analysis.CONFIG_VERSION.getName()},
                new Object[]{code, configVersion});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, code);
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, configVersion.toString());
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, schemaConfigName);
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsSuccess.RDBMS_RECORD_FOUND, errorDetailVariables);
        }
        try {
            fieldName = LPArray.addValueToArray1D(fieldName, TblsCnfg.Analysis.CODE.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, code);
            fieldName = LPArray.addValueToArray1D(fieldName, TblsCnfg.Analysis.CONFIG_VERSION.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, configVersion);
            if (Boolean.FALSE.equals(LPArray.valueInArray(fieldName, TblsCnfg.Analysis.CREATED_BY.getName()))){
                fieldName = LPArray.addValueToArray1D(fieldName, TblsCnfg.Analysis.CREATED_BY.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, instanceForActions.getToken().getPersonName());
                fieldName = LPArray.addValueToArray1D(fieldName, TblsCnfg.Analysis.CREATED_ON.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, LPDate.getCurrentTimeStamp());
            }
            if (Boolean.FALSE.equals(LPArray.valueInArray(fieldName, TblsCnfg.Analysis.ACTIVE.getName()))){
                fieldName = LPArray.addValueToArray1D(fieldName, TblsCnfg.Analysis.ACTIVE.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, false);
            }
            RdbmsObject diagnObj = Rdbms.insertRecord(TblsCnfg.TablesConfig.ANALYSIS, fieldName, fieldValue, instanceName);
            if (Boolean.TRUE.equals(diagnObj.getRunSuccess())) {
                ConfigTablesAudit.analysisAuditAdd(ConfigAnalysisAuditEvents.ANALYSIS_NEW, TblsCnfgAudit.TablesCfgAudit.ANALYSIS, code,
                        code, configVersion, fieldName, fieldValue, null);
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, code);
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, schemaConfigName);
                return new InternalMessage(LPPlatform.LAB_TRUE, MasterDataAnalysisEnums.MasterDataAnalysisActionsEndpoints.ANALYSIS_NEW, new Object[]{code});
            } else {
                return new InternalMessage(LPPlatform.LAB_FALSE, diagnObj.getErrorMessageCode(), diagnObj.getErrorMessageVariables());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ConfigAnalysisStructure.class.getName()).log(Level.SEVERE, null, ex);
        }
        String params = "procInstanceName: " + procInstanceName + "specFieldName: " + Arrays.toString(fieldName) + "specFieldValue: " + Arrays.toString(fieldValue);
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, params);
        return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.UNHANDLED_EXCEPTION_IN_CODE, errorDetailVariables);
    }

    /**
     * @param procInstanceName
     * @param specCode
     * @return
     */
    /*    public Object[] specVariationGetNamesList( String procInstanceName, String specCode){

        String schemaName = GlobalVariables.Schemas.CONFIG.getName();
        StringBuilder variationListBuilder = new StringBuilder(0);
        String errorCode ="";
        
        schemaName = LPPlatform.buildSchemaName(procInstanceName, schemaName);
        
        Object[][] variationListArray = Rdbms.getRecordFieldsByFilter(schemaName, TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS.getTableName(), 
                new String[]{TblsCnfg.AnalysisMethodParams.CODE.getName()}, new Object[]{specCode}, 
                new String[]{TblsCnfg.AnalysisMethodParams.VARIATION_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variationListArray[0][0].toString())){            
            return LPArray.array2dTo1d(variationListArray);
        }else{
            for (int i=0;i<=variationListArray.length;i++){
                 if (variationListBuilder.length()>0){variationListBuilder.append("|");}
                 variationListBuilder.append(variationListArray[i][0].toString());
             }
            errorCode = "specVariationGetNamesList_successfully";
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, errorCode, new String[]{variationListBuilder.toString()});            
        }
    }
     */
    public InternalMessage analysisDeactivate(){
        if (Boolean.FALSE.equals(this.isCurrentlyActive))
            return new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisErrorTrapping.ANALYSIS_ALREADY_DEACTIVATED, new Object[]{code});                
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();

        String[] fieldName=new String[]{TblsCnfg.Analysis.ACTIVE.getName(), TblsCnfg.Analysis.INACTIVATED_BY.getName(), TblsCnfg.Analysis.INACTIVATED_ON.getName()};
        Object[] fieldValue=new Object[]{false, instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        
        
        Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsCnfg.TablesConfig.SPEC_LIMITS.getRepositoryName()), TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())){
            Object[] existsRecord = Rdbms.existsRecord(TblsCnfg.TablesConfig.SPEC_LIMITS, 
                    new String[]{TblsCnfg.SpecLimits.ANALYSIS.getName()}, new Object[]{this.code}, procInstanceName);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())){
                return new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisErrorTrapping.ANALYSIS_IN_USE_BY_SPECS, new Object[]{code});                
            }
        }
        
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsCnfg.Analysis.CODE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.code}, "");
        sqlWhere.addConstraint(TblsCnfg.Analysis.CONFIG_VERSION, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{this.configVersion}, "");
        RdbmsObject diagnObj = Rdbms.updateTableRecordFieldsByFilter(TblsCnfg.TablesConfig.ANALYSIS,
                EnumIntTableFields.getTableFieldsFromString(TblsCnfg.TablesConfig.ANALYSIS, fieldName), fieldValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnObj.getRunSuccess())) {
            ConfigTablesAudit.analysisAuditAdd(ConfigAnalysisAuditEvents.ANALYSIS_DEACTIVATED, TblsCnfgAudit.TablesCfgAudit.ANALYSIS, code,
                    code, configVersion, fieldName, fieldValue, null);
            if (this.approvedForUse){
                fieldName=new String[]{TblsCnfg.Analysis.APPROVED_FOR_USE.getName(), TblsCnfg.Analysis.APPROVED_BY.getName(), TblsCnfg.Analysis.APPROVED_ON.getName()};
                fieldValue=new Object[]{false, "NULL>>>STRING", "NULL>>>DATETIME"};
                diagnObj = Rdbms.updateTableRecordFieldsByFilter(TblsCnfg.TablesConfig.ANALYSIS,
                        EnumIntTableFields.getTableFieldsFromString(TblsCnfg.TablesConfig.ANALYSIS, fieldName), fieldValue, sqlWhere, null);
                ConfigTablesAudit.analysisAuditAdd(ConfigAnalysisAuditEvents.UNAPPROVED_FOR_USE, TblsCnfgAudit.TablesCfgAudit.ANALYSIS, code,
                        code, configVersion, fieldName, fieldValue, null);
            }
            return new InternalMessage(LPPlatform.LAB_TRUE, MasterDataAnalysisEnums.MasterDataAnalysisActionsEndpoints.ANALYSIS_DEACTIVATE, new Object[]{code});
        } else {
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnObj.getErrorMessageCode(), diagnObj.getErrorMessageVariables());
        }
    }
    public InternalMessage analysisReactivate(){
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String schemaName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        String[] fieldName=new String[]{TblsCnfg.Analysis.ACTIVE.getName(), TblsCnfg.Analysis.INACTIVATED_BY.getName(), TblsCnfg.Analysis.INACTIVATED_ON.getName()};
        Object[] fieldValue=new Object[]{true, "NULL>>>STRING", "NULL>>>DATE"};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsCnfg.Analysis.CODE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{code}, "");
        sqlWhere.addConstraint(TblsCnfg.Analysis.CONFIG_VERSION, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{configVersion}, "");
        RdbmsObject diagnObj = Rdbms.updateTableRecordFieldsByFilter(TblsCnfg.TablesConfig.ANALYSIS,
                EnumIntTableFields.getTableFieldsFromString(TblsCnfg.TablesConfig.ANALYSIS, fieldName), fieldValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnObj.getRunSuccess())) {
            ConfigTablesAudit.analysisAuditAdd(ConfigAnalysisAuditEvents.ANALYSIS_REACTIVATED, TblsCnfgAudit.TablesCfgAudit.ANALYSIS, code,
                    code, configVersion, fieldName, fieldValue, null);
            return new InternalMessage(LPPlatform.LAB_TRUE, MasterDataAnalysisEnums.MasterDataAnalysisActionsEndpoints.ANALYSIS_REACTIVATE, new Object[]{code});
        } else {
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnObj.getErrorMessageCode(), diagnObj.getErrorMessageVariables());
        }
    }
    public InternalMessage analysisApproveForUse(){
        if (this.approvedForUse){
            return lockedDueToApprovedForUse();
        }
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();
        String schemaName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        String[] fieldName=new String[]{TblsCnfg.Analysis.APPROVED_FOR_USE.getName(), TblsCnfg.Analysis.APPROVED_BY.getName(), TblsCnfg.Analysis.APPROVED_ON.getName()};
        Object[] fieldValue=new Object[]{true, instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp()};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsCnfg.Analysis.CODE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{code}, "");
        sqlWhere.addConstraint(TblsCnfg.Analysis.CONFIG_VERSION, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{configVersion}, "");
        RdbmsObject diagnObj = Rdbms.updateTableRecordFieldsByFilter(TblsCnfg.TablesConfig.ANALYSIS,
                EnumIntTableFields.getTableFieldsFromString(TblsCnfg.TablesConfig.ANALYSIS, fieldName), fieldValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnObj.getRunSuccess())) {
            ConfigTablesAudit.analysisAuditAdd(ConfigAnalysisAuditEvents.ANALYSIS_REACTIVATED, TblsCnfgAudit.TablesCfgAudit.ANALYSIS, code,
                    code, configVersion, fieldName, fieldValue, null);
            return new InternalMessage(LPPlatform.LAB_TRUE, MasterDataAnalysisEnums.MasterDataAnalysisActionsEndpoints.ANALYSIS_REACTIVATE, new Object[]{code});
        } else {
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnObj.getErrorMessageCode(), diagnObj.getErrorMessageVariables());
        }
    }

    public InternalMessage analysisAddMethod(String analysisCode, Integer analysisCodeVersion, String methodName, String expiryIntervalInfo){
        if (this.approvedForUse)
            return lockedDueToApprovedForUse();
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String schemaName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        Object[] diagnoses = Rdbms.existsRecord(procInstanceName, schemaName, TblsCnfg.TablesConfig.METHODS.getTableName(), 
            new String[]{TblsCnfg.Methods.CODE.getName()}, new Object[]{methodName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, MasterDataAnalysisErrorTrapping.METHOD_NOT_FOUND, new Object[]{methodName});
        }
        String[] fieldName=new String[]{TblsCnfg.AnalysisMethod.ANALYSIS.getName(), TblsCnfg.AnalysisMethod.METHOD_NAME.getName(), TblsCnfg.AnalysisMethod.EXPIRY_INTERVAL_INFO.getName()};
        Object[] fieldValue=new Object[]{analysisCode, methodName, expiryIntervalInfo};
                RdbmsObject diagnObj = Rdbms.insertRecordInTable(TblsCnfg.TablesConfig.ANALYSIS_METHOD, 
                fieldName,fieldValue);
        if (Boolean.TRUE.equals(diagnObj.getRunSuccess())) {
            ConfigTablesAudit.analysisAuditAdd(ConfigAnalysisAuditEvents.ANALYSIS_METHOD_ADDED, TblsCnfgAudit.TablesCfgAudit.ANALYSIS, analysisCode,
                    analysisCode, analysisCodeVersion, fieldName, fieldValue, null);
            return new InternalMessage(LPPlatform.LAB_TRUE, MasterDataAnalysisEnums.MasterDataAnalysisActionsEndpoints.ANALYSIS_ADD_METHOD, new Object[]{methodName, analysisCode});
        } else {
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnObj.getErrorMessageCode(), diagnObj.getErrorMessageVariables());
        }
    }
    public InternalMessage analysisRemoveMethod(String analysisCode, Integer analysisCodeVersion, String methodName){
        if (this.approvedForUse)
            return lockedDueToApprovedForUse();
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String schemaName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        Object[] diagnoses = Rdbms.existsRecord(procInstanceName, schemaName, TblsCnfg.TablesConfig.ANALYSIS_METHOD.getTableName(), 
            new String[]{TblsCnfg.AnalysisMethod.ANALYSIS.getName(), TblsCnfg.AnalysisMethod.METHOD_NAME.getName()}, new Object[]{analysisCode, methodName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, MasterDataAnalysisErrorTrapping.ANALYSIS_METHOD_NOT_FOUND, new Object[]{methodName, analysisCode});
        }
        String[] fieldName=new String[]{TblsCnfg.AnalysisMethod.ANALYSIS.getName(), TblsCnfg.AnalysisMethod.METHOD_NAME.getName()};
        Object[] fieldValue=new Object[]{analysisCode, methodName};

        SqlWhere whereObj=new SqlWhere(TblsCnfg.TablesConfig.ANALYSIS_METHOD, fieldName, fieldValue);
        RdbmsObject diagnObj = Rdbms.removeRecordInTable(TblsCnfg.TablesConfig.ANALYSIS_METHOD, whereObj, procInstanceName);
        if (Boolean.TRUE.equals(diagnObj.getRunSuccess())) {
            ConfigTablesAudit.analysisAuditAdd(ConfigAnalysisAuditEvents.ANALYSIS_METHOD_DELETE, TblsCnfgAudit.TablesCfgAudit.ANALYSIS, analysisCode,
                    analysisCode, analysisCodeVersion, fieldName, fieldValue, null);
            return new InternalMessage(LPPlatform.LAB_TRUE, MasterDataAnalysisEnums.MasterDataAnalysisActionsEndpoints.ANALYSIS_REMOVE_METHOD, new Object[]{methodName, analysisCode});
        } else {
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnObj.getErrorMessageCode(), diagnObj.getErrorMessageVariables());
        }
    }
    public InternalMessage analysisMethodParamsNew(String analysisCode, Integer analysisCodeVersion, String methodName, String[] fieldName, Object[] fieldValue, Boolean createMethodIfNotFound) {
        if (this.approvedForUse)
            return lockedDueToApprovedForUse();
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();
        Object[] mandatoryFieldValue = new String[0];
        StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);

        
        Object[] errorDetailVariables = new Object[0];

        String schemaName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        String[] mandatoryFields = getSpecLimitsMandatoryFields();
        
        Object[] checkTwoArraysSameLength = LPArray.checkTwoArraysSameLength(fieldName, fieldValue);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkTwoArraysSameLength[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, checkTwoArraysSameLength[0].toString(), null, null);
        }

        String[] whereFields = new String[]{TblsCnfg.Analysis.CODE.getName()};
        Object[] whereFieldsValue = new Object[]{analysisCode};
        Object[] diagnoses = Rdbms.existsRecord(procInstanceName, schemaName, TblsCnfg.TablesConfig.ANALYSIS.getTableName(), whereFields, whereFieldsValue);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, MasterDataAnalysisErrorTrapping.ANALYSIS_NOT_FOUND, new Object[]{analysisCode});
        }
        if (LPArray.duplicates(fieldName)) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, Arrays.toString(fieldName));
            return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.FIELDS_DUPLICATED, errorDetailVariables);
        }

        Integer methodVersion = 1;
        if (LPArray.valueInArray(fieldName, "method_version")) {
            methodVersion = Integer.valueOf(fieldValue[LPArray.valuePosicInArray(fieldName, "method_version")].toString());
        }
        for (Integer inumLines = 0; inumLines < mandatoryFields.length; inumLines++) {
            String currField = mandatoryFields[inumLines];
            boolean contains = Arrays.asList(fieldName).contains(currField.toLowerCase());
            if (Boolean.FALSE.equals(contains)) {
                if (mandatoryFieldsMissingBuilder.length() > 0) {
                    mandatoryFieldsMissingBuilder.append(",");
                }

                mandatoryFieldsMissingBuilder.append(currField);
            } else {
                Object currFieldValue = fieldValue[Arrays.asList(fieldName).indexOf(currField.toLowerCase())];
                mandatoryFieldValue = LPArray.addValueToArray1D(mandatoryFieldValue, currFieldValue);
            }
        }
        if (mandatoryFieldsMissingBuilder.length() > 0) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, mandatoryFieldsMissingBuilder.toString());
            return new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, errorDetailVariables);
        }

        String[] specialFields = getSpecialFields();
        String[] specialFieldsFunction = getSpecialFieldsFunction();
        for (Integer inumLines = 0; inumLines < fieldName.length; inumLines++) {
            String currField = "analysis_method." + fieldName[inumLines];
            boolean contains = Arrays.asList(specialFields).contains(currField);
            if (contains) {
                Integer specialFieldIndex = Arrays.asList(specialFields).indexOf(currField);
                String aMethod = specialFieldsFunction[specialFieldIndex];
                Method method = null;
                try {
                    Class<?>[] paramTypes = {String.class, String.class, Integer.class, String[].class, Object[].class};
                    method = getClass().getDeclaredMethod(aMethod, paramTypes);
                } catch (NoSuchMethodException | SecurityException ex) {
                    Logger.getLogger(ConfigAnalysisStructure.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    Object specialFunctionReturn = DIAGNOSES_ERROR;
                    if (method != null) {
                        try {
                            specialFunctionReturn = method.invoke(this, schemaName, analysisCode, analysisCodeVersion, fieldName, fieldValue);
                        } catch (IllegalAccessException | IllegalArgumentException ex) {
                            Logger.getLogger(ConfigAnalysisStructure.class.getName()).log(Level.SEVERE, null, ex);
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
                    return new InternalMessage(LPPlatform.LAB_FALSE, LpPlatformErrorTrapping.SPECIALFUNCTION_CAUSEDEXCEPTION, errorDetailVariables);
                }
            }
        }
        whereFields = new String[]{TblsCnfg.AnalysisMethod.METHOD_NAME.getName()};
        whereFieldsValue = new Object[]{methodName};
        diagnoses = Rdbms.existsRecord(procInstanceName, schemaName, TblsCnfg.TablesConfig.ANALYSIS_METHOD.getTableName(), whereFields, whereFieldsValue);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())) {
            if (createMethodIfNotFound){
                String[] anaMethFldName = new String[]{TblsCnfg.AnalysisMethod.ANALYSIS.getName(), TblsCnfg.AnalysisMethod.METHOD_NAME.getName(), 
                    TblsCnfg.AnalysisMethod.CREATED_BY.getName(), TblsCnfg.AnalysisMethod.CREATED_ON.getName()};
                Integer createdByFldPosic=LPArray.valuePosicInArray(fieldName, TblsCnfg.AnalysisMethod.CREATED_BY.getName());
                String createdBy=instanceForActions.getToken().getPersonName();
                if (createdByFldPosic>-1)
                    createdBy=fieldValue[createdByFldPosic].toString();
                Object[] anaMethFldValue = new Object[]{analysisCode, methodName, createdBy, LPDate.getCurrentTimeStamp()};
                RdbmsObject diagnObj = Rdbms.insertRecord(TblsCnfg.TablesConfig.ANALYSIS_METHOD, anaMethFldName, anaMethFldValue, procInstanceName); 
                if (Boolean.TRUE.equals(diagnObj.getRunSuccess())) {
                    ConfigTablesAudit.analysisAuditAdd(ConfigAnalysisAuditEvents.ANALYSIS_METHOD_ADDED, TblsCnfgAudit.TablesCfgAudit.ANALYSIS, analysisCode,
                            analysisCode, analysisCodeVersion, anaMethFldName, anaMethFldValue, null);
                }            
            }else{
                return new InternalMessage(LPPlatform.LAB_FALSE, MasterDataAnalysisErrorTrapping.ANALYSIS_METHOD_NOT_FOUND, new Object[]{methodName, analysisCode});
            }            
        }
        try {
            fieldName = LPArray.addValueToArray1D(fieldName, TblsCnfg.AnalysisMethodParams.ANALYSIS.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, analysisCode);
            fieldName = LPArray.addValueToArray1D(fieldName, TblsCnfg.AnalysisMethodParams.METHOD_NAME.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, methodName);
            fieldName = LPArray.addValueToArray1D(fieldName, TblsCnfg.AnalysisMethodParams.METHOD_VERSION.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, methodVersion);
            
            RdbmsObject diagnObj = Rdbms.insertRecord(TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS, fieldName, fieldValue, procInstanceName);
            if (Boolean.TRUE.equals(diagnObj.getRunSuccess())) {
                ConfigTablesAudit.analysisAuditAdd(ConfigAnalysisAuditEvents.ANALYSIS_METHOD_PARAM_NEW, TblsCnfgAudit.TablesCfgAudit.ANALYSIS, analysisCode,
                        analysisCode, analysisCodeVersion, fieldName, fieldValue, null);
                String paramName = fieldValue[LPArray.valuePosicInArray(fieldName, TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName())].toString();
                return new InternalMessage(LPPlatform.LAB_TRUE, MasterDataAnalysisEnums.MasterDataAnalysisActionsEndpoints.ANALYSIS_ADD_PARAM, new Object[]{paramName, analysisCode, methodName});
            } else {
                return new InternalMessage(LPPlatform.LAB_FALSE, diagnObj.getErrorMessageCode(), diagnObj.getErrorMessageVariables());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ConfigAnalysisStructure.class.getName()).log(Level.SEVERE, null, ex);
        }
        String params = "procInstanceName: " + procInstanceName + "fieldName: " + Arrays.toString(fieldName) + "fieldValue: " + Arrays.toString(fieldValue);
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, params);
        return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.UNHANDLED_EXCEPTION_IN_CODE, errorDetailVariables, null);        
    }
}
