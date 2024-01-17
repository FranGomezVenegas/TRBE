/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules.masterdata.analysis;

import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.materialspec.DataSpec;
import functionaljavaa.samplestructure.DataSampleAnalysis;
import functionaljavaa.samplestructure.DataSampleAnalysisResultStrategy;
import functionaljavaa.samplestructure.DataSampleStructureStatuses;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPMath;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class AnalysisCalculations {
    Boolean resultCalculated=false;
    BigDecimal calculatedResultValue;
    Integer calcResultId;
    Integer calcResultSpecLimitId;
    String currResultStatus;
    String[] fieldsName=null;
    Object[] fieldsValue=null;
    Integer resultId;
    Integer testId;
    Integer sampleId;
    String alternativeAuditEntry;
    String alternativeAuditClass;
    String newResultStatus;
    public enum AnalysisCalculationsCollection {
        LOD, SULFATES, SULFURIC_ASHES, MIN, MAX, AVERAGE, STD_DEV
    };

    
    
    public AnalysisCalculations(Integer resultId, Integer testId, Integer sampleId, String[] resultFlds, Object[] resultVls,
            DataSampleAnalysisResultStrategy sar, String alternativeAuditEntry, String alternativeAuditClass) {
        Integer calcLinkedFldPosicInArray = LPArray.valuePosicInArray(resultFlds, TblsData.SampleAnalysisResult.CALC_LINKED.getName());
        if (calcLinkedFldPosicInArray == -1) {
            return;
        }
        this.sampleId=sampleId;
        this.testId=testId;
        this.resultId=resultId;
        this.alternativeAuditEntry=alternativeAuditEntry;
        this.alternativeAuditClass=alternativeAuditClass;

        String calcInfo=resultVls[calcLinkedFldPosicInArray].toString();
        String[] calcInfoArr=calcInfo.split("\\|");
        String calcName=calcInfoArr[0];
        String paramName=null;
        if (calcInfoArr.length>1)
            paramName=calcInfoArr[1];
        if (calcName.length()==0) return;
        AnalysisCalculationsCollection selCalcName = null;
        try{
            selCalcName = AnalysisCalculationsCollection.valueOf(calcName);
        }catch(Exception e){
            return;
        }
        switch (selCalcName) {
            case LOD:
                calcLossOnDrying();
                break;
            case SULFATES:
                calcSulfase();
                break;
            case SULFURIC_ASHES:
                calcSulfuricAshes();
                break;
            case MIN:
                calcMin(paramName);
                break;
            case MAX:
                calcMax(paramName);
                break;
            case AVERAGE:
                calcAverage(paramName);
                break;
            case STD_DEV:
                calcStdDev(paramName);
                break;
            default:
                break;
        }
        if (Boolean.FALSE.equals(this.resultCalculated)) return;
        this.getCalcResultInfo(testId);
        if (this.calcResultId==null) return;
        this.setCalcResultStatus();
        if (calcResultSpecLimitId != null)

            this.checkCalcSpec(sar);
        this.updateCalcResult();
    }

    public final void calcLossOnDrying() {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String[] resultFieldsArr = new String[]{TblsData.SampleAnalysisResult.PARAM_NAME.getName(), TblsData.SampleAnalysisResult.RAW_VALUE.getName(),
            TblsData.SampleAnalysisResult.PARAM_TYPE.getName(), TblsData.SampleAnalysisResult.RESULT_ID.getName(),
            TblsData.SampleAnalysisResult.LIMIT_ID.getName(), TblsData.SampleAnalysisResult.STATUS.getName()};
        Object[][] resultData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{testId},
                resultFieldsArr);
        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) {
            return;
        }

        String wBefore = null;
        String wAfter = null;
        for (Object[] curRes : resultData) {
            if ((curRes[0].toString().toUpperCase().contains("BEFORE")) || (curRes[0].toString().toUpperCase().contains("WET"))
                    || (curRes[0].toString().toUpperCase().contains("ANTES")) || (curRes[0].toString().toUpperCase().contains("MOJADO"))) {
                wBefore = LPNulls.replaceNull(curRes[1]).toString();
            }
            if ((curRes[0].toString().toUpperCase().contains("AFTER")) || (curRes[0].toString().toUpperCase().contains("DRY"))
                    || (curRes[0].toString().toUpperCase().contains("SECO")) || (curRes[0].toString().toUpperCase().contains("DESPUES"))) {
                wAfter = LPNulls.replaceNull(curRes[1]).toString();
            }
            if (curRes[2].toString().toUpperCase().contains("CALC")) {
                calcResultId = Integer.valueOf(LPNulls.replaceNull(curRes[3]).toString());
                calcResultSpecLimitId = LPNulls.replaceNull(curRes[4]).toString().length() == 0 ? null : Integer.valueOf(LPNulls.replaceNull(curRes[4]).toString());
                currResultStatus = LPNulls.replaceNull(curRes[5]).toString();
            }
        }
        if (LPNulls.replaceNull(wBefore).length() > 0 && LPNulls.replaceNull(wAfter).length() > 0 && LPNulls.replaceNull(calcResultId).toString().length() > 0) {
            BigDecimal initialWeight = BigDecimal.valueOf(Double.parseDouble(wBefore));
            BigDecimal finalWeight = BigDecimal.valueOf(Double.parseDouble(wAfter));
            BigDecimal loss = initialWeight.subtract(finalWeight);
            this.calculatedResultValue =loss.divide(finalWeight, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            this.resultCalculated=true;
        }
    }

    public final void calcSulfase() {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String[] resultFieldsArr = new String[]{TblsData.SampleAnalysisResult.PARAM_NAME.getName(), TblsData.SampleAnalysisResult.RAW_VALUE.getName(),
            TblsData.SampleAnalysisResult.PARAM_TYPE.getName(), TblsData.SampleAnalysisResult.RESULT_ID.getName(),
            TblsData.SampleAnalysisResult.LIMIT_ID.getName(), TblsData.SampleAnalysisResult.STATUS.getName()};
        Object[][] resultData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{testId},
                resultFieldsArr);
        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) {
            return;
        }

        String volume = null;
        String factor = null;
        String weight = null;
        Integer calcResultId = null;
        Integer calcResultSpecLimitId = null;
        String currResultStatus = null;
        for (Object[] curRes : resultData) {
            if (curRes[0].toString().toUpperCase().contains("VOLUME")) {
                volume = LPNulls.replaceNull(curRes[1]).toString();
            }
            if (curRes[0].toString().toUpperCase().contains("FACTOR")) {
                factor = LPNulls.replaceNull(curRes[1]).toString();
            }
            if (curRes[0].toString().toUpperCase().contains("WEIGHT")||curRes[0].toString().toUpperCase().contains("PESO")) {
                weight = LPNulls.replaceNull(curRes[1]).toString();
            }
            if (curRes[2].toString().toUpperCase().contains("CALC")) {
                calcResultId = Integer.valueOf(LPNulls.replaceNull(curRes[3]).toString());
                calcResultSpecLimitId = LPNulls.replaceNull(curRes[4]).toString().length() == 0 ? null : Integer.valueOf(LPNulls.replaceNull(curRes[4]).toString());
                currResultStatus = LPNulls.replaceNull(curRes[5]).toString();
            }
        }
        if (LPNulls.replaceNull(volume).toString().length() > 0 && LPNulls.replaceNull(factor).toString().length() > 0 && LPNulls.replaceNull(calcResultId).toString().length() > 0) {
            BigDecimal volumeNum = BigDecimal.valueOf(Double.valueOf(volume));
            BigDecimal factorNum = BigDecimal.valueOf(Double.valueOf(factor));
            BigDecimal weightNum = BigDecimal.valueOf(Double.valueOf(weight));
            BigDecimal valueCalc = volumeNum.multiply(BigDecimal.valueOf(0.032)).multiply(factorNum);
            this.calculatedResultValue =valueCalc.divide(weightNum, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            this.resultCalculated=true;
            return;
        }
    }

    public final void calcSulfuricAshes() {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String[] resultFieldsArr = new String[]{TblsData.SampleAnalysisResult.PARAM_NAME.getName(), TblsData.SampleAnalysisResult.RAW_VALUE.getName(),
            TblsData.SampleAnalysisResult.PARAM_TYPE.getName(), TblsData.SampleAnalysisResult.RESULT_ID.getName(),
            TblsData.SampleAnalysisResult.LIMIT_ID.getName(), TblsData.SampleAnalysisResult.STATUS.getName()};
        Object[][] resultData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{testId},
                resultFieldsArr);
        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) {
            return;
        }

        String after = null;
        String empty = null;
        String weight = null;
        Integer calcResultId = null;
        Integer calcResultSpecLimitId = null;
        String currResultStatus = null;
        for (Object[] curRes : resultData) {
            if (curRes[0].toString().toUpperCase().contains("AFTER")||curRes[0].toString().toUpperCase().contains("DESPU")) {
                after = LPNulls.replaceNull(curRes[1]).toString();
            }
            if (curRes[0].toString().toUpperCase().contains("ANTES")||curRes[0].toString().toUpperCase().contains("EMPTY")||curRes[0].toString().toUpperCase().contains("VACIO")) {
                empty = LPNulls.replaceNull(curRes[1]).toString();
            }
            if (curRes[0].toString().toUpperCase().contains("WEIGHT")||curRes[0].toString().toUpperCase().contains("PESO")) {
                weight = LPNulls.replaceNull(curRes[1]).toString();
            }
            if (curRes[2].toString().toUpperCase().contains("CALC")) {
                calcResultId = Integer.valueOf(LPNulls.replaceNull(curRes[3]).toString());
                calcResultSpecLimitId = LPNulls.replaceNull(curRes[4]).toString().length() == 0 ? null : Integer.valueOf(LPNulls.replaceNull(curRes[4]).toString());
                currResultStatus = LPNulls.replaceNull(curRes[5]).toString();
            }
        }
        if (LPNulls.replaceNull(after).toString().length() > 0 && LPNulls.replaceNull(empty).toString().length() > 0 && LPNulls.replaceNull(calcResultId).toString().length() > 0) {
            BigDecimal afterNum = BigDecimal.valueOf(Double.valueOf(after));
            BigDecimal emptyNum = BigDecimal.valueOf(Double.valueOf(empty));
            BigDecimal weightNum = BigDecimal.valueOf(Double.valueOf(weight));
            BigDecimal valueCalc = afterNum.subtract(emptyNum);
            this.calculatedResultValue = valueCalc.divide(weightNum, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            this.resultCalculated=true;
            return;
        }
    }

    public final void calcMin(String paramName) {
        
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();
        SqlWhere wObj=new SqlWhere(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{testId});
//        wObj.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL, new Object[]{resultId}, null);
        wObj.addConstraint(TblsData.SampleAnalysisResult.RAW_VALUE, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, null);
        wObj.addConstraint(TblsData.SampleAnalysisResult.PARAM_TYPE, SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL, new Object[]{"CALC"}, null);
        if (paramName!=null)
            wObj.addConstraint(TblsData.SampleAnalysisResult.PARAM_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{paramName}, null);
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        EnumIntTableFields[] resultFieldsArr = new EnumIntTableFields[]{TblsData.SampleAnalysisResult.PARAM_NAME, TblsData.SampleAnalysisResult.RAW_VALUE,
            TblsData.SampleAnalysisResult.PARAM_TYPE, TblsData.SampleAnalysisResult.RESULT_ID,
            TblsData.SampleAnalysisResult.LIMIT_ID, TblsData.SampleAnalysisResult.STATUS};
        Object[][] resultData = Rdbms.getRecordFieldsByFilter(null, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
            wObj,resultFieldsArr, new String[]{TblsData.SampleAnalysisResult.RAW_VALUE.getName()}, false);
        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) {
            this.resultCalculated=false;
            return;
        }
        this.resultCalculated=true;
        this.calculatedResultValue=BigDecimal.valueOf(Double.parseDouble(resultData[0][1].toString()));
    }

    public final void calcMax(String paramName) {
        
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();
        SqlWhere wObj=new SqlWhere(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{testId});
//        wObj.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL, new Object[]{resultId}, null);
        wObj.addConstraint(TblsData.SampleAnalysisResult.RAW_VALUE, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, null);
        wObj.addConstraint(TblsData.SampleAnalysisResult.PARAM_TYPE, SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL, new Object[]{"CALC"}, null);
        if (paramName!=null)
            wObj.addConstraint(TblsData.SampleAnalysisResult.PARAM_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{paramName}, null);
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        EnumIntTableFields[] resultFieldsArr = new EnumIntTableFields[]{TblsData.SampleAnalysisResult.PARAM_NAME, TblsData.SampleAnalysisResult.RAW_VALUE,
            TblsData.SampleAnalysisResult.PARAM_TYPE, TblsData.SampleAnalysisResult.RESULT_ID,
            TblsData.SampleAnalysisResult.LIMIT_ID, TblsData.SampleAnalysisResult.STATUS};
        Object[][] resultData = Rdbms.getRecordFieldsByFilter(null, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
            wObj,resultFieldsArr, new String[]{TblsData.SampleAnalysisResult.RAW_VALUE.getName()+ " desc"}, false);
        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) {
            this.resultCalculated=false;
            return;
        }
        this.resultCalculated=true;
        this.calculatedResultValue=BigDecimal.valueOf(Double.parseDouble(resultData[0][1].toString()));
    }                
    
    public final void calcStdDev(String paramName) {
        
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();
        SqlWhere wObj=new SqlWhere(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{testId});
//        wObj.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL, new Object[]{resultId}, null);
        wObj.addConstraint(TblsData.SampleAnalysisResult.RAW_VALUE, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, null);
        wObj.addConstraint(TblsData.SampleAnalysisResult.PARAM_TYPE, SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL, new Object[]{"CALC"}, null);
        if (paramName!=null)
            wObj.addConstraint(TblsData.SampleAnalysisResult.PARAM_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{paramName}, null);
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        EnumIntTableFields[] resultFieldsArr = new EnumIntTableFields[]{TblsData.SampleAnalysisResult.PARAM_NAME, TblsData.SampleAnalysisResult.RAW_VALUE,
            TblsData.SampleAnalysisResult.PARAM_TYPE, TblsData.SampleAnalysisResult.RESULT_ID,
            TblsData.SampleAnalysisResult.LIMIT_ID, TblsData.SampleAnalysisResult.STATUS};
        Object[][] resultData = Rdbms.getRecordFieldsByFilter(null, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
            wObj,resultFieldsArr, new String[]{TblsData.SampleAnalysisResult.RAW_VALUE.getName()+ " desc"}, false);
        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) {
            this.resultCalculated=false;
            return;
        }
        this.resultCalculated=true;
        Double[] numArray=new Double[]{};
        for (Object[] curRes: resultData){
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(LPMath.isNumeric(curRes[1].toString())[0].toString())){
                numArray=LPArray.addValueToArray1D(numArray, Double.valueOf(curRes[1].toString()));
                
            }
        }        
        this.calculatedResultValue=BigDecimal.valueOf(LPMath.calculateSD(numArray));
    }                

    public final void calcAverage(String paramName) {
        
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();
        SqlWhere wObj=new SqlWhere(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{testId});
        wObj.addConstraint(TblsData.SampleAnalysisResult.RAW_VALUE, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, null);
        wObj.addConstraint(TblsData.SampleAnalysisResult.PARAM_TYPE, SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL, new Object[]{"CALC"}, null);
        if (paramName!=null)
            wObj.addConstraint(TblsData.SampleAnalysisResult.PARAM_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{paramName}, null);
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        EnumIntTableFields[] resultFieldsArr = new EnumIntTableFields[]{TblsData.SampleAnalysisResult.PARAM_NAME, TblsData.SampleAnalysisResult.RAW_VALUE,
            TblsData.SampleAnalysisResult.PARAM_TYPE, TblsData.SampleAnalysisResult.RESULT_ID,
            TblsData.SampleAnalysisResult.LIMIT_ID, TblsData.SampleAnalysisResult.STATUS};
        Object[][] resultData = Rdbms.getRecordFieldsByFilter(null, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
            wObj,resultFieldsArr, new String[]{TblsData.SampleAnalysisResult.RAW_VALUE.getName()+ " desc"}, false);
        if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) {
            this.resultCalculated=false;
            return;
        }
        this.resultCalculated=true;
        Integer totalNums=0;
        Double total=Double.valueOf("0");
        for (Object[] curRes: resultData){
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(LPMath.isNumeric(curRes[1].toString())[0].toString())){
                total=total+Double.valueOf(curRes[1].toString());
                totalNums=totalNums+1;
            }
        }
        if (totalNums==0){
            this.calculatedResultValue=null;
        }else{
            this.calculatedResultValue=BigDecimal.valueOf(total/totalNums);
        }
    }                

    public final void getCalcResultInfo(Integer testId){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());

        this.calcResultId = null;
        this.calcResultSpecLimitId = null;
        this.currResultStatus = null; 

        SqlWhere wObj=new SqlWhere(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{testId});
        wObj.addConstraint(TblsData.SampleAnalysisResult.PARAM_TYPE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{"CALC"}, null);
        EnumIntTableFields[] resultFieldsArr = new EnumIntTableFields[]{TblsData.SampleAnalysisResult.PARAM_NAME, TblsData.SampleAnalysisResult.RAW_VALUE,
            TblsData.SampleAnalysisResult.PARAM_TYPE, TblsData.SampleAnalysisResult.RESULT_ID,
            TblsData.SampleAnalysisResult.LIMIT_ID, TblsData.SampleAnalysisResult.STATUS};
        Object[][] calcResultData = Rdbms.getRecordFieldsByFilter(null, schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
            wObj,resultFieldsArr, new String[]{TblsData.SampleAnalysisResult.RAW_VALUE.getName()}, false);
        if (LPPlatform.LAB_FALSE.equals(calcResultData[0][0].toString())) {
            return;
        }
            if (calcResultData[0][2].toString().toUpperCase().contains("CALC")) {
                calcResultId = Integer.valueOf(LPNulls.replaceNull(calcResultData[0][3]).toString());
                calcResultSpecLimitId = LPNulls.replaceNull(calcResultData[0][4]).toString().length() == 0 ? null : Integer.valueOf(LPNulls.replaceNull(calcResultData[0][4]).toString());
                currResultStatus = LPNulls.replaceNull(calcResultData[0][5]).toString();
            }

    } 
    public final void setCalcResultStatus(){
        this.fieldsName = LPArray.addValueToArray1D(this.fieldsName, TblsData.SampleAnalysisResult.STATUS_PREVIOUS.getName());
        this.fieldsValue = LPArray.addValueToArray1D(this.fieldsValue, this.currResultStatus);
        this.newResultStatus = currResultStatus;
        String resultStatusDefault = DataSampleStructureStatuses.SampleAnalysisResultStatuses.getStatusFirstCode();
        String resultStatusCanceled = DataSampleStructureStatuses.SampleAnalysisResultStatuses.CANCELED.getStatusCode("");
        String resultStatusReviewed = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REVIEWED.getStatusCode("");
        String resultStatusEntered = DataSampleStructureStatuses.SampleAnalysisResultStatuses.ENTERED.getStatusCode("");
        String resultStatusReEntered = DataSampleStructureStatuses.SampleAnalysisResultStatuses.REENTERED.getStatusCode("");
        if (resultStatusReviewed.equalsIgnoreCase(currResultStatus) || resultStatusCanceled.equalsIgnoreCase(currResultStatus)) {
            return;
        }
        if (currResultStatus == null) {
            this.newResultStatus = resultStatusDefault;
        }
        if ((newResultStatus.equalsIgnoreCase(DataSampleStructureStatuses.SampleAnalysisResultStatuses.BLANK.getStatusCode(""))) || (newResultStatus.equalsIgnoreCase(resultStatusDefault))) {
            this.newResultStatus = resultStatusEntered;
        } else {
            this.newResultStatus = resultStatusReEntered;
        }
        this.fieldsName = LPArray.addValueToArray1D(this.fieldsName, TblsData.SampleAnalysisResult.STATUS.getName());
        this.fieldsValue = LPArray.addValueToArray1D(this.fieldsValue, this.newResultStatus);
    }
    public final void checkCalcSpec(DataSampleAnalysisResultStrategy sar){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = instanceForActions.getProcedureInstance();
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
            
        DataSpec resChkSpec = new DataSpec();
        Object[] resSpecEvaluation = null;
        ConfigSpecRule specRule = new ConfigSpecRule();
        specRule.specLimitsRule(calcResultSpecLimitId, null);
        String specEval = "";
        if (Boolean.TRUE.equals(specRule.getRuleIsQualitative())) {
            resSpecEvaluation = resChkSpec.resultCheck((String) calculatedResultValue.toString(), specRule.getQualitativeRule(),
                    specRule.getQualitativeRuleValues(), specRule.getQualitativeRuleSeparator(), specRule.getQualitativeRuleListName());
            EnumIntMessages checkMsgCode = (EnumIntMessages) resSpecEvaluation[resSpecEvaluation.length - 1];
            specEval = checkMsgCode.getErrorCode();

            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation[0].toString())) {
                return;
            }
            fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.SPEC_EVAL_DETAIL.getName(),
                TblsData.SampleAnalysisResult.ENTERED_BY.getName(), TblsData.SampleAnalysisResult.ENTERED_ON.getName(), TblsData.SampleAnalysisResult.STATUS.getName()});
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEval, resSpecEvaluation[resSpecEvaluation.length - 2],
                instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp(), newResultStatus});
            if (calcResultSpecLimitId == null || Boolean.FALSE.equals(Objects.equals(calcResultSpecLimitId, calcResultSpecLimitId))) {
                fieldsName = LPArray.addValueToArray1D(fieldsName, TblsData.SampleAnalysisResult.LIMIT_ID.getName());
                fieldsValue = LPArray.addValueToArray1D(fieldsValue, calcResultSpecLimitId);
            }
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{resultId}, "");
            Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                        resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                Object[][] sampleData = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE.getTableName(),
                        new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId},
                        new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()});
                if (LPPlatform.LAB_FALSE.equals(sampleData[0][0].toString())) {
                    return;
                }
                String[] sampleFieldName = new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE_VERSION.getName()};
                String sampleConfigCode = (String) sampleData[0][1];
                Integer sampleConfigCodeVersion = Integer.valueOf(LPNulls.replaceNull(sampleData[0][2]).toString());

                Object[] sampleFieldValue = new Object[]{sampleId, sampleConfigCode, sampleConfigCodeVersion};

                if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_UPON_CONTROL)) {
                    sar.sarControlAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                }
                if ((resSpecEvaluation[resSpecEvaluation.length - 1]).toString().contains(ConfigSpecRule.SPEC_WORD_FOR_OOS)) {
                    sar.sarOOSAction(resultId, sampleFieldName, sampleFieldValue, fieldsName, fieldsValue);
                }
            }
            return;
        }
        if (calcResultSpecLimitId == null || Boolean.FALSE.equals(Objects.equals(calcResultSpecLimitId, calcResultSpecLimitId))) {
            fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{TblsData.SampleAnalysisResult.SPEC_EVAL.getName(), TblsData.SampleAnalysisResult.SPEC_EVAL_DETAIL.getName(), TblsData.SampleAnalysisResult.LIMIT_ID.getName()});
            fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{specEval, resSpecEvaluation[resSpecEvaluation.length - 2], calcResultSpecLimitId});
        }

    }
            
    public final void updateCalcResult(){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        fieldsName = LPArray.addValueToArray1D(fieldsName, new String[]{
            TblsData.SampleAnalysisResult.RAW_VALUE.getName(), TblsData.SampleAnalysisResult.PRETTY_VALUE.getName(),
            TblsData.SampleAnalysisResult.ENTERED_BY.getName(), TblsData.SampleAnalysisResult.ENTERED_ON.getName()});
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, new Object[]{this.calculatedResultValue, this.calculatedResultValue,
            instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp()});
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsData.SampleAnalysisResult.RESULT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{calcResultId}, "");
        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, fieldsName), fieldsValue, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
            SampleAudit smpAudit = new SampleAudit();
            smpAudit.sampleAuditAdd(SampleAudit.DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED_CALC, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                    resultId, sampleId, testId, resultId, fieldsName, fieldsValue, alternativeAuditEntry, alternativeAuditClass);
        }
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
            DataSampleAnalysis.sampleAnalysisEvaluateStatus(sampleId, testId);
        }
    }
    
}
