/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.moduleenvmonit.ProcedureSampleStage;
import com.labplanet.servicios.moduleenvmonit.ProcedureSampleStage.ProcedureSampleStageErrorTrapping;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import databases.TblsProcedure;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.modulesample.DataModuleSampleAnalysis;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.platform.doc.BusinessRulesToRequirements;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleStructureSuccess;
import trazit.session.ResponseMessages;
import static functionaljavaa.samplestructure.ProcedureSampleStages.procedureSampleStagesTimingEvaluateDeviation;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;

/**
 *
 * @author Administrator
 */
public class DataSampleStages {

    String labelMsgError = "Error:";

    Boolean isSampleStagesEnable = false;
    Boolean isSampleStagesTimingCaptureEnable = false;
    String isSampleStagesTimingCaptureStages = "";
    String sampleCurrentStage = "";
    String sampleNextStage = "";
    String previousStage = "";
    Integer sampleId = -999;
    Object[][] firstStageData = new Object[0][0];

    static final String LBL_SUFFIX_CHECKER = "Checker";
    static final String LBL_PREFIX_SAMPLE_STAGE = "sampleStage";

    public enum SampleStageBusinessRules implements EnumIntBusinessRules {
        SAMPLE_STAGES_FIRST("sampleStagesFirst", GlobalVariables.Schemas.DATA.getName(), null, null, '|', null, "procedure*sampleStagesMode"),
        ACTION_AUTOMOVETONEXT("sampleStagesActionAutoMoveToNext", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, "procedure*sampleStagesMode"),
        SAMPLE_STAGE_MODE("sampleStagesMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        SAMPLE_STAGE_TYPE("sampleStagesLogicType", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, "procedure*sampleStagesMode"),
        SAMPLE_STAGE_TIMING_CAPTURE_MODE("sampleStagesTimingCaptureMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, "procedure*sampleStagesMode"),
        SAMPLE_STAGE_TIMING_CAPTURE_STAGES("sampleStagesTimingCaptureStages", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, "procedure*sampleStagesMode"),
        SAMPLE_STAGE_TIMING_PROCEDURE_CONFIG_ENABLED("sampleStagesTimingProcedureConfigEnabled", GlobalVariables.Schemas.PROCEDURE.getName(), BusinessRulesToRequirements.valuesListForEnableDisable(), false, '|', null, "procedure*sampleStagesMode"),;

        private SampleStageBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator, Boolean isOpt, String preReqs) {
            this.tagName = tgName;
            this.areaName = areaNm;
            this.valuesList = valuesList;
            this.allowMultiValue = allowMulti;
            this.multiValueSeparator = separator;
            this.isOptional = isOpt;
            this.preReqsBusRules = preReqs;
        }

        @Override
        public String getTagName() {
            return this.tagName;
        }

        @Override
        public String getAreaName() {
            return this.areaName;
        }

        @Override
        public JSONArray getValuesList() {
            return this.valuesList;
        }

        @Override
        public Boolean getAllowMultiValue() {
            return this.allowMultiValue;
        }

        @Override
        public char getMultiValueSeparator() {
            return this.multiValueSeparator;
        }

        @Override
        public Boolean getIsOptional() {
            return isOptional;
        }

        @Override
        public ArrayList<String[]> getPreReqs() {
            ArrayList<String[]> d = new ArrayList<>();
            if (preReqsBusRules != null && preReqsBusRules.length() > 0) {
                String[] rulesArr = preReqsBusRules.split("\\|");
                for (String curRule : rulesArr) {
                    String[] curRuleArr = curRule.split("\\*");
                    if (curRuleArr.length == 2) {
                        d.add(curRuleArr);
                    }
                }
            }
            return d;
        }
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;
        private final Boolean isOptional;
        private final String preReqsBusRules;
    }

    public enum SampleStageTimingCapturePhases {
        START, END
    }
    public static final String SAMPLE_STAGES_MODE_ENAB_STATSES = "ENABLE|ENABLED|SI|ACTIVADO|YES";

    public enum SampleStagesTypes {
        JAVA, JAVASCRIPT
    }
    public static final String LOD_JAVASCRIPT_FORMULA = "procInstanceName-sample-stage.js";
    public static final String LOD_JAVASCRIPT_LOCAL_FORMULA = "D:\\LP\\LabPLANETAPI_20200113_beforeRefactoring\\src\\main\\resources\\JavaScript\\" + "procInstanceName-sample-stage.js";

    public DataSampleStages() {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String sampleStagesMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleStageBusinessRules.SAMPLE_STAGE_MODE.getAreaName(), SampleStageBusinessRules.SAMPLE_STAGE_MODE.getTagName(), SampleStageBusinessRules.SAMPLE_STAGE_TIMING_CAPTURE_MODE.getPreReqs(), true);
        if (Boolean.TRUE.equals(Parameter.isTagValueOneOfDisableOnes(sampleStagesMode))) {
            this.isSampleStagesEnable = false;
            return;
        }
        if (LPArray.valuePosicInArray(SAMPLE_STAGES_MODE_ENAB_STATSES.split("\\|"), sampleStagesMode) > -1) {
            this.isSampleStagesEnable = true;
        }
        String sampleStagesTimingCaptureMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleStageBusinessRules.SAMPLE_STAGE_TIMING_CAPTURE_MODE.getAreaName(), SampleStageBusinessRules.SAMPLE_STAGE_TIMING_CAPTURE_MODE.getTagName(), SampleStageBusinessRules.SAMPLE_STAGE_TIMING_CAPTURE_MODE.getPreReqs());
        if (LPArray.valuePosicInArray(SAMPLE_STAGES_MODE_ENAB_STATSES.split("\\|"), sampleStagesTimingCaptureMode) > -1) {
            this.isSampleStagesTimingCaptureEnable = true;
        }
        String sampleStagesTimingCaptureStages = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleStageBusinessRules.SAMPLE_STAGE_TIMING_CAPTURE_STAGES.getAreaName(), SampleStageBusinessRules.SAMPLE_STAGE_TIMING_CAPTURE_STAGES.getTagName(), SampleStageBusinessRules.SAMPLE_STAGE_TIMING_CAPTURE_STAGES.getPreReqs());
        if (LPArray.valuePosicInArray(SAMPLE_STAGES_MODE_ENAB_STATSES.split("\\|"), sampleStagesTimingCaptureMode) > -1) {
            this.isSampleStagesTimingCaptureStages = sampleStagesTimingCaptureStages;
        }
        String stageFirst = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleStageBusinessRules.SAMPLE_STAGES_FIRST.getAreaName(), SampleStageBusinessRules.SAMPLE_STAGES_FIRST.getTagName(), SampleStageBusinessRules.SAMPLE_STAGES_FIRST.getPreReqs());
        this.firstStageData = new Object[][]{{TblsData.Sample.CURRENT_STAGE.getName(), stageFirst}};

    }

    public Object[][] getFirstStage() {
        return this.firstStageData;
    }

    public Boolean isSampleStagesEnable() {
        return this.isSampleStagesEnable;
    }

    public Object[] moveToNextStage(Integer sampleId, String currStage, String nextStageFromPull) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] sampleAuditRevision = SampleAudit.sampleAuditRevisionPass(sampleId);

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAuditRevision[0].toString())) {
            return sampleAuditRevision;
        }
        Object[] javaScriptDiagnostic = moveStageChecker(sampleId, currStage, "Next");

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(javaScriptDiagnostic[0].toString())) {
            return javaScriptDiagnostic;
        }
        if (Boolean.FALSE.equals(javaScriptDiagnostic[0].toString().contains(LPPlatform.LAB_TRUE))) {
            return javaScriptDiagnostic;
        }

        String[] javaScriptDiagnosticArr = javaScriptDiagnostic[0].toString().split("\\|");
        if (javaScriptDiagnosticArr.length > 1) {
            String newStageProposedByChecker = javaScriptDiagnosticArr[1];
            return new Object[]{LPPlatform.LAB_TRUE, newStageProposedByChecker};
        }

        String sampleStageNextStage = Parameter.getBusinessRuleProcedureFile(procInstanceName, GlobalVariables.Schemas.DATA.getName(), LBL_PREFIX_SAMPLE_STAGE + currStage + "Next");
        if (sampleStageNextStage.length() == 0||"NULL".equalsIgnoreCase(sampleStageNextStage)) {
            return new Object[]{LPPlatform.LAB_FALSE, "Next Stage is blank for " + currStage};
        }

        String[] nextStageArr = sampleStageNextStage.split("\\|");
        if (nextStageArr.length == 1) {
            return new Object[]{LPPlatform.LAB_TRUE, sampleStageNextStage};
        }
        Integer posicInArr = LPArray.valuePosicInArray(nextStageArr, nextStageFromPull);
        if (posicInArr == -1) {
            return new Object[]{LPPlatform.LAB_FALSE, "Proposed next Stage, " + nextStageFromPull + ", is not on the list of next stages, " + Arrays.toString(nextStageArr) + " for the stage " + currStage};
        }
        return new Object[]{LPPlatform.LAB_TRUE, nextStageFromPull};
    }

    /**
     *
     * @param sampleId
     * @param currStage
     * @param previousStageFromPull
     * @return
     */
    public Object[] moveToPreviousStage(Integer sampleId, String currStage, String previousStageFromPull) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] javaScriptDiagnostic = moveStageChecker(sampleId, currStage, "Previous");
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(javaScriptDiagnostic[0].toString())) {
            return javaScriptDiagnostic;
        }

        String sampleStagePreviousStage = Parameter.getBusinessRuleProcedureFile(procInstanceName, GlobalVariables.Schemas.DATA.getName(), LBL_PREFIX_SAMPLE_STAGE + currStage + "Previous");
        if (sampleStagePreviousStage.length() == 0||"NULL".equalsIgnoreCase(sampleStagePreviousStage)) {
            return new Object[]{LPPlatform.LAB_FALSE, "Previous Stage is blank for " + currStage};
        }

        String[] previousStageArr = sampleStagePreviousStage.split("\\|");
        if (previousStageArr.length == 1) {
            return new Object[]{LPPlatform.LAB_TRUE, sampleStagePreviousStage};
        }
        Integer posicInArr = LPArray.valuePosicInArray(previousStageArr, previousStageFromPull);
        if (posicInArr == -1) {
            return new Object[]{LPPlatform.LAB_FALSE, "Proposed Previous Stage, " + previousStageFromPull + ", is not on the list of Previous stages, " + Arrays.toString(previousStageArr) + " for the stage " + currStage};
        }
        return new Object[]{LPPlatform.LAB_TRUE, previousStageFromPull};
    }

    public Object[] dataSampleActionAutoMoveToNext(String actionName, Integer sampleId) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(),
                new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId},
                new String[]{TblsData.Sample.CURRENT_STAGE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return sampleInfo;
        }
        String sampleCurrStage = sampleInfo[0][0].toString();
        String sampleStagesActionAutoMoveToNext = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleStageBusinessRules.ACTION_AUTOMOVETONEXT.getAreaName(), SampleStageBusinessRules.ACTION_AUTOMOVETONEXT.getTagName());
        if (LPArray.valuePosicInArray(sampleStagesActionAutoMoveToNext.split("\\|"), actionName) == -1) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleStructureSuccess.ACTIONNOTDECLARED_TOPERFORMAUTOMOVETONEXT, new Object[]{actionName, procInstanceName});
        }

        if ("END".equalsIgnoreCase(sampleCurrStage)) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "SAMPLE LAST STAGE", new Object[]{actionName, procInstanceName});
        }

        Object[] moveDiagn = moveToNextStage(sampleId, sampleCurrStage, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(moveDiagn[0].toString())) {
            dataSampleStagesTimingCapture(sampleId, sampleCurrStage, SampleStageTimingCapturePhases.END.toString());
            String[] sampleFieldName = new String[]{TblsData.Sample.CURRENT_STAGE.getName(), TblsData.Sample.PREVIOUS_STAGE.getName()};
            Object[] sampleFieldValue = new Object[]{moveDiagn[moveDiagn.length - 1], sampleCurrStage};
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(moveDiagn[0].toString())) {
                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsData.Sample.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
                Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE,
                        EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE, sampleFieldName), sampleFieldValue, sqlWhere, null);
                dataSampleStagesTimingCapture(sampleId, moveDiagn[moveDiagn.length - 1].toString(), SampleStageTimingCapturePhases.START.toString());
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.SAMPLESTAGE_MOVETONEXT, TblsData.TablesData.SAMPLE.getTableName(),
                        sampleId, sampleId, null, null, sampleFieldName, sampleFieldValue);
            }
            if ("END".equalsIgnoreCase(sampleCurrStage)) {
                DataModuleSampleAnalysis smpAna = new DataModuleSampleAnalysis();
                DataSample smp = new DataSample(smpAna);
                smp.sampleReview(sampleId);
            }
        }
        return moveDiagn;
    }

    private Object[] moveStageChecker(Integer sampleId, String currStage, String moveDirection) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String sampleStagesType = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleStageBusinessRules.SAMPLE_STAGE_TYPE.getAreaName(), SampleStageBusinessRules.SAMPLE_STAGE_TYPE.getTagName());
        if (SampleStagesTypes.JAVA.toString().equalsIgnoreCase(sampleStagesType)) {
            return moveStageCheckerJava(sampleId, currStage, moveDirection);
        } else {
            return moveStageCheckerJavaScript(sampleId, currStage, moveDirection);
        }
    }

    private Object[] moveStageCheckerJava(Integer sampleId, String currStage, String moveDirection) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();
        String jsonarrayf = DataSample.sampleEntireStructureData(procInstanceName, sampleId, DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS,
                DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS, null, DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS, null,
                null, null);
        String functionName = LBL_PREFIX_SAMPLE_STAGE + currStage + moveDirection + LBL_SUFFIX_CHECKER;
        ProcedureSampleStage procSampleStage = new ProcedureSampleStage();
        Method method = null;
        try {
            Class<?>[] paramTypes = {String.class, Integer.class, String.class};
            method = ProcedureSampleStage.class.getDeclaredMethod(functionName, paramTypes);
        } catch (NoSuchMethodException | SecurityException ex) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, new Object[]{ex.getMessage()});
        }
        Object specialFunctionReturn = null;
        try {
            if (method != null) {
                specialFunctionReturn = method.invoke(procSampleStage, procInstanceName, sampleId, jsonarrayf);
            }
        } catch (IllegalAccessException | NullPointerException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(DataSample.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ((specialFunctionReturn == null) || (specialFunctionReturn != null && specialFunctionReturn.toString().contains("ERROR"))) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.SPECIAL_FUNCTION_RETURNED_ERROR, new Object[]{functionName, LPNulls.replaceNull(specialFunctionReturn)});
        }
        if ((specialFunctionReturn == null) || (specialFunctionReturn != null && !specialFunctionReturn.toString().contains("TRUE"))) {
            String errorCode = LPNulls.replaceNull(specialFunctionReturn).toString().replace(LPPlatform.LAB_FALSE, "");
            String[] errorCodeArr = errorCode.split("@");
            Object[] msgVariables = null;
            if (errorCodeArr.length > 1) {
                msgVariables = new Object[]{errorCodeArr[1]};
            }
            ResponseMessages messages = instanceForActions.getMessages();
            EnumIntMessages smpStgErr = null;
            try {
                smpStgErr = ProcedureSampleStageErrorTrapping.valueOf(errorCodeArr[0].toUpperCase());
                errorCodeArr[0] = smpStgErr.getErrorCode();
                
            } catch (Exception e) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "StageChecker_"+errorCodeArr[0].toUpperCase(), new Object[]{e.getMessage()});
            }
            if (messages.getMainMessage() == null) {
                messages.addMainForError(errorCodeArr[0], msgVariables, null);
            } else {
                messages.addMinorForError(smpStgErr, msgVariables);                
                Object[] trapMessage = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, smpStgErr, msgVariables);
                trapMessage[trapMessage.length-1]=errorCodeArr[1];
                return trapMessage;
            }

            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "SpecialFunctionReturnedFALSE", new Object[]{errorCode});
        }
        return new Object[]{specialFunctionReturn};
    }

    private Object[] moveStageCheckerJavaScript(Integer sampleId, String currStage, String moveDirection) {
        try {
            String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String jsonarrayf = DataSample.sampleEntireStructureData(procInstanceName, sampleId, DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS,
                    DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS, null, DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS, null,
                    DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS, null);
            String fileName = LOD_JAVASCRIPT_FORMULA.replace(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME, procInstanceName);
            fileName = procInstanceName + "-sample-stage.js"; //"/procedure/"+
            String functionName = LBL_PREFIX_SAMPLE_STAGE + currStage + moveDirection + LBL_SUFFIX_CHECKER;
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            try {
                try {
                    engine.eval(new FileReader(fileName));
                } catch (ScriptException ex) {
                    Logger.getLogger(DataSampleStages.class.getName()).log(Level.SEVERE, null, ex);
                    return new Object[]{LPPlatform.LAB_FALSE, "FileNotFoundException", labelMsgError + ex.getMessage()};
                }
            } catch (FileNotFoundException ex) {
                try {
                    fileName = LOD_JAVASCRIPT_LOCAL_FORMULA.replace(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME, procInstanceName);
                    functionName = LBL_PREFIX_SAMPLE_STAGE + currStage + moveDirection + LBL_SUFFIX_CHECKER;
                    engine = new ScriptEngineManager().getEngineByName("nashorn");
                    engine.eval(new FileReader(fileName));
                } catch (FileNotFoundException ex2) {
                    Logger.getLogger(DataSampleStages.class.getName()).log(Level.SEVERE, null, ex2);
                    return new Object[]{LPPlatform.LAB_FALSE, "FileNotFoundException", labelMsgError + ex2.getMessage()
                        + "(tried two paths: " + "/app/" + procInstanceName + "-sample-stage.js" + " and " + LOD_JAVASCRIPT_LOCAL_FORMULA.replace(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME, procInstanceName) + ") "};
                }
            }
            Invocable invocable = (Invocable) engine;
            Object result;
            result = invocable.invokeFunction(functionName, sampleId, jsonarrayf);
            if (result.toString().equalsIgnoreCase(LPPlatform.LAB_TRUE)) {
                return new Object[]{LPPlatform.LAB_TRUE};
            }
            return new Object[]{LPPlatform.LAB_FALSE, result};
        } catch (ScriptException | NoSuchMethodException ex) {
            Logger.getLogger(DataSampleStages.class.getName()).log(Level.SEVERE, null, ex);
            return new Object[]{LPPlatform.LAB_FALSE, ex.getCause().toString(), labelMsgError + ex.getMessage()};
        }
    }

    public Object[] dataSampleStagesTimingCapture(Integer sampleId, String currStage, String phase) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        if (Boolean.FALSE.equals(this.isSampleStagesTimingCaptureEnable)) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The business rule <*1*> is not enable therefore stage change timing capture is not enabled for procedure <*2*>", new Object[]{SampleStageBusinessRules.SAMPLE_STAGE_TIMING_CAPTURE_MODE.getTagName(), procInstanceName});
        }
        if ((Boolean.FALSE.equals(("ALL".equalsIgnoreCase(this.isSampleStagesTimingCaptureStages)))) && (LPArray.valuePosicInArray(this.isSampleStagesTimingCaptureStages.split("\\|"), currStage) == -1)) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The stage <*1*> is not declared for timing capture for procedure <*2*>", new Object[]{currStage, procInstanceName});
        }
        if (SampleStageTimingCapturePhases.START.toString().equalsIgnoreCase(phase)) {
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.SAMPLE_STAGE_TIMING_CAPTURE,
                    new String[]{TblsProcedure.SampleStageTimingCapture.SAMPLE_ID.getName(), TblsProcedure.SampleStageTimingCapture.STAGE_CURRENT.getName(), TblsProcedure.SampleStageTimingCapture.STARTED_ON.getName()},
                    new Object[]{sampleId, currStage, LPDate.getCurrentTimeStamp()});
            return insertRecordInTable.getApiMessage();
        } else if (SampleStageTimingCapturePhases.END.toString().equalsIgnoreCase(phase)) {
            procedureSampleStagesTimingEvaluateDeviation(sampleId, currStage);
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsProcedure.SampleStageTimingCapture.SAMPLE_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sampleId}, "");
            sqlWhere.addConstraint(TblsProcedure.SampleStageTimingCapture.STAGE_CURRENT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{currStage}, "");
            return Rdbms.updateRecordFieldsByFilter(TblsProcedure.TablesProcedure.SAMPLE_STAGE_TIMING_CAPTURE,
                    EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.SAMPLE_STAGE_TIMING_CAPTURE, new String[]{TblsProcedure.SampleStageTimingCapture.ENDED_ON.getName()}), new Object[]{LPDate.getCurrentTimeStamp()}, sqlWhere, null);
        } else {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "The phase <*1*> is not one of the recognized by the system, <*2*>",
                    new Object[]{phase, Arrays.toString(new String[]{SampleStageTimingCapturePhases.START.toString(), SampleStageTimingCapturePhases.END.toString()})});
        }
    }

    //, JSONArray sampleData
    public String sampleStageSamplingNextChecker(String sch, Integer sampleId) {
        //var sampleStructure=JSON.parse(sampleData);
        //var samplingDate = sampleStructure.sampling_date;
        //if (samplingDate==null){
        //  return testId+" Fecha de muestreo es obligatoria para la muestra "+sampleId;}
        return LPPlatform.LAB_TRUE;
    }
}
