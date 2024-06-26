/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.testing.config.db;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.TblsCnfg;
import databases.TblsTesting;
import functionaljavaa.inventory.InventoryGlobalVariables.DataInvRetErrorTrapping;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.materialspec.ConfigSpecRule.qualitativeRulesErrors;
import functionaljavaa.materialspec.DataSpec;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import functionaljavaa.testingscripts.LPTestingParams;
import functionaljavaa.testingscripts.LPTestingParams.TestingServletsConfig;
import functionaljavaa.testingscripts.TestingAssert;
import functionaljavaa.testingscripts.TestingAssertSummary;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import static lbplanet.utilities.LPMath.isNumeric;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform.ApiErrorTraping;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntEndpoints;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;

public class DbTestingLimitAndResult extends HttpServlet {

    public enum TestingLimitAndResult implements EnumIntEndpoints {
        DB_CONFIG_SPEC_TESTING_LIMIT_AND_RESULT("DB_CONFIG_SPEC_TESTING_LIMIT_AND_RESULT", "productionLot_newLotCreated_success",
                new LPAPIArguments[]{new LPAPIArguments("variation", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("analysis", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                    new LPAPIArguments("methodName", LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                    new LPAPIArguments("methodVersion", LPAPIArguments.ArgumentType.STRING.toString(), true, 12),
                    new LPAPIArguments("parameterName", LPAPIArguments.ArgumentType.STRING.toString(), true, 13),
                    new LPAPIArguments("resultValue", LPAPIArguments.ArgumentType.STRING.toString(), true, 14),
                    new LPAPIArguments("resultUomName", LPAPIArguments.ArgumentType.STRING.toString(), false, 15)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null);

        private TestingLimitAndResult(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name = name;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.outputObjectTypes = outputObjectTypes;
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);
        }
        @Override public String getEntity() {return "testing";}
        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getSuccessMessageCode() {
            return this.successMessageCode;
        }

        @Override
        public JsonArray getOutputObjectTypes() {
            return outputObjectTypes;
        }

        @Override
        public LPAPIArguments[] getArguments() {
            return arguments;
        }

        @Override
        public String getApiUrl() {
            return ApiUrls.TESTING_LIMIT_AND_RESULT.getUrl();
        }
        private final String name;
        private final String successMessageCode;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;

        @Override
        public String getDeveloperComment() {
            return this.devComment;
        }

        @Override
        public String getDeveloperCommentTag() {
            return this.devCommentTag;
        }
        private final String devComment;
        private final String devCommentTag;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String table1Header = TestingServletsConfig.DB_SCHEMACONFIG_SPEC_RESULTCHECK.getTablesHeaders();
        response = LPTestingOutFormat.responsePreparation(response);
        DataSpec resChkSpec = new DataSpec();
        Object[] resSpecEvaluation = null;
        InternalMessage resSpecEvaluationObj = null;
        LocalDateTime timeStarted=LPDate.getCurrentTimeStamp();
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForUAT(request, response, true, "");
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())) {
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);
            return;
        }
        Integer scriptId = Integer.valueOf(LPNulls.replaceNull(request.getAttribute(LPTestingParams.SCRIPT_ID).toString()));
        String procInstanceName = LPNulls.replaceNull(request.getAttribute(LPTestingParams.SCHEMA_PREFIX)).toString();
        String repositoryName = LPPlatform.buildSchemaName(GlobalVariables.Schemas.APP_TESTING.getName(), "");
        if (procInstanceName != null && procInstanceName.length() > 0) {
            repositoryName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
        }

        Object[][] scriptInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, repositoryName, TblsTesting.TablesTesting.SPEC_SCRIPT.getTableName(),
                new String[]{TblsTesting.SpecScript.SCRIPT_ID.getName()}, new Object[]{scriptId},
                new String[]{TblsTesting.SpecScript.SPEC_CODE.getName(), TblsTesting.SpecScript.SPEC_VERSION.getName()},new String[]{});

        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();

        String testerFileName = LPTestingParams.TestingServletsConfig.DB_SCHEMACONFIG_SPEC_RESULTCHECK.getTesterFileName();
        LPTestingOutFormat tstOut = new LPTestingOutFormat(request, LPTestingParams.TestingServletsConfig.DB_SCHEMACONFIG_SPEC_RESULTCHECK.name(), testerFileName);
        Map<String, Object> csvHeaderTags = tstOut.getCsvHeaderTags();
        String stopPhrase=null;
        StringBuilder fileContentBuilder = new StringBuilder(0);
        fileContentBuilder.append(tstOut.getHtmlStyleHeader());
        Object[][] testingContent = tstOut.getTestingContent();
        Integer currentLine = 0;
        try (PrintWriter out = response.getWriter()) {
            if (csvHeaderTags.containsKey(LPPlatform.LAB_FALSE)) {
                fileContentBuilder.append("There are missing tags in the file header: ").append(csvHeaderTags.get(LPPlatform.LAB_FALSE));
                out.println(fileContentBuilder.toString());
                return;
            }
            Integer numEvaluationArguments = tstOut.getNumEvaluationArguments();
            Integer numHeaderLines = Integer.valueOf(csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.NUM_HEADER_LINES.getTagValue().toString()).toString());

            StringBuilder fileContentTable1Builder = new StringBuilder(0);
            fileContentTable1Builder.append(LPTestingOutFormat.createTableWithHeader(table1Header, numEvaluationArguments));
            LPAPIArguments[] arguments = TestingLimitAndResult.DB_CONFIG_SPEC_TESTING_LIMIT_AND_RESULT.getArguments();
            for (Integer iLines = numHeaderLines; iLines < testingContent.length; iLines++) {
                stopPhrase=null;
                LocalDateTime timeStartedStep = LPDate.getCurrentTimeStamp();
                currentLine = iLines;
                tstAssertSummary.increaseTotalTests();
                TestingAssert tstAssert = new TestingAssert(testingContent[iLines], numEvaluationArguments, true);

                Integer lineNumCols = testingContent[0].length - 1;
                String resultValue = null;

                String schemaName = "";
                String specCode = scriptInfo[0][0].toString();
                Integer specCodeVersion = Integer.valueOf(scriptInfo[0][1].toString());
                String variation = "";
                String analysis = "";
                String methodName = "";
                Integer methodVersion = null;
                String parameterName = "";
                String resultUomName = null;
                int argIndex = 0;

                String specCodeVersionStr = null;
                String methodVersionStr = null;

                argIndex++;
                Integer dateStartPosic=tstOut.getActionNamePosic()-1;
                if (lineNumCols >= numEvaluationArguments + argIndex) {
                    variation = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][dateStartPosic + 1]);
                }
                argIndex++;
                if (lineNumCols >= numEvaluationArguments + argIndex) {
                    analysis = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][dateStartPosic + 2]);
                }
                argIndex++;
                if (lineNumCols >= numEvaluationArguments + argIndex) {
                    methodName = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][dateStartPosic + 3]);
                }
                argIndex++;
                if (lineNumCols >= numEvaluationArguments + argIndex) {
                    methodVersion = LPTestingOutFormat.csvExtractFieldValueInteger(testingContent[iLines][dateStartPosic + 4]);
                    methodVersionStr = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][dateStartPosic + 4]);
                }
                argIndex++;
                if (lineNumCols >= numEvaluationArguments + argIndex) {
                    parameterName = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][dateStartPosic + 5]);
                }
                argIndex++;
                if (lineNumCols >= numEvaluationArguments + argIndex) {
                    resultValue = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][dateStartPosic + 6]);
                }
                argIndex++;
                if (lineNumCols >= numEvaluationArguments + argIndex) {
                    resultUomName = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][dateStartPosic + 7]);
                }
                argIndex++;

                resSpecEvaluationObj = tstOut.checkMissingMandatoryParamValuesByCall(arguments, testingContent[iLines]);
                resSpecEvaluation=ApiMessageReturn.trapMessage(resSpecEvaluationObj.getDiagnostic(), resSpecEvaluationObj.getMessageCodeObj(), resSpecEvaluationObj.getMessageCodeVariables());
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluationObj.getDiagnostic())) {
                    Integer stepId = Integer.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStepIdPosic()]).toString());
                    fileContentTable1Builder.append(tstOut.publishEvalStep(request, stepId, resSpecEvaluation, new JSONArray(), tstAssert, timeStartedStep));
                } else {
                    String schemaConfigName = LPPlatform.buildSchemaName(schemaName, GlobalVariables.Schemas.CONFIG.getName());
                    String schemaDataName = LPPlatform.buildSchemaName(schemaName, GlobalVariables.Schemas.DATA.getName());
                    if (specCodeVersion == null || methodVersion == null) {
                        Object[] fldsNull = new Object[]{};
                        if (specCodeVersion == null) {
                            fldsNull = LPArray.addValueToArray1D(fldsNull, "specCodeVersion");
                        }
                        if (methodVersion == null) {
                            fldsNull = LPArray.addValueToArray1D(fldsNull, "methodVersion");
                        }
                        if ((specCodeVersion == null && specCodeVersionStr == null) || (methodVersion == null && methodVersionStr == null)) {
                            resSpecEvaluationObj = new InternalMessage(LPPlatform.LAB_FALSE, ApiErrorTraping.MANDATORY_PARAMS_MISSING, fldsNull);
                        } else {
                            resSpecEvaluationObj = new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.VALUE_NOT_NUMERIC, fldsNull);
                        }
                        resSpecEvaluation = LPArray.addValueToArray1D(resSpecEvaluation, "numeric field(s) empty");
                    } else {
                        Object[][] specLimits = Rdbms.getRecordFieldsByFilter(schemaName, schemaConfigName, TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName(),
                                new String[]{TblsCnfg.SpecLimits.CODE.getName(), TblsCnfg.SpecLimits.CONFIG_VERSION.getName(),
                                    TblsCnfg.SpecLimits.VARIATION_NAME.getName(), TblsCnfg.SpecLimits.ANALYSIS.getName(), TblsCnfg.SpecLimits.METHOD_NAME.getName(), TblsCnfg.SpecLimits.METHOD_VERSION.getName(), TblsCnfg.SpecLimits.PARAMETER.getName()},
                                new Object[]{specCode, specCodeVersion, variation, analysis, methodName, methodVersion, parameterName},
                                new String[]{TblsCnfg.SpecLimits.LIMIT_ID.getName(), TblsCnfg.SpecLimits.RULE_TYPE.getName(), TblsCnfg.SpecLimits.RULE_VARIABLES.getName(),
                                    TblsCnfg.SpecLimits.LIMIT_ID.getName(), TblsCnfg.SpecLimits.UOM.getName(), TblsCnfg.SpecLimits.UOM_CONVERSION_MODE.getName()});
                        if ((LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimits[0][0].toString()))) { //&& (!RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND.getErrorCode().equalsIgnoreCase(specLimits[0][4].toString())) ){
                            resSpecEvaluation = specLimits[0];
                            resSpecEvaluation = LPArray.addValueToArray1D(resSpecEvaluation, "Regla No encontrada ");
                        } else {
                            Integer limitId = Integer.valueOf(specLimits[0][0].toString());
                            String specUomName = specLimits[0][4].toString();
                            ConfigSpecRule specRule = new ConfigSpecRule();
                            specRule.specLimitsRule(limitId, null);
                            if (Boolean.FALSE.equals(specRule.getRuleIsQualitative()) && Boolean.FALSE.equals(specRule.getRuleIsQuantitative())) {
                                resSpecEvaluationObj = new InternalMessage(LPPlatform.LAB_FALSE, qualitativeRulesErrors.QUALITATIVE_RULE_NOT_RECOGNIZED, null);
                            }
                            if (Boolean.TRUE.equals(specRule.getRuleIsQualitative())) {
                                resSpecEvaluationObj = resChkSpec.resultCheck(resultValue, specRule.getQualitativeRule(),
                                        specRule.getQualitativeRuleValues(), specRule.getQualitativeRuleSeparator(), specRule.getQualitativeRuleListName());
                            }
                            if (Boolean.TRUE.equals(specRule.getRuleIsQuantitative())) {
                                InternalMessage isNumeric = isNumeric(resultValue, false);
                                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric.getDiagnostic())) {
                                    resSpecEvaluationObj = isNumeric;
                                } else {
                                    Boolean requiresUnitsConversion = true;
                                    BigDecimal resultConverted = null;
                                    BigDecimal resultValueBigDecimal = new BigDecimal(resultValue);
                                    UnitsOfMeasurement uom = new UnitsOfMeasurement(new BigDecimal(resultValue), resultUomName);
                                    resultUomName = LPNulls.replaceNull(resultUomName);
                                    specUomName = LPNulls.replaceNull(specUomName);
                                    if (resultUomName.length() == 0 || specUomName.length() == 0 || resultUomName.equals(specUomName)) {
                                        requiresUnitsConversion = false;
                                    }
                                    if (Boolean.TRUE.equals(requiresUnitsConversion) && specUomName != null && specUomName.length() > 0) {
                                        uom.convertValue(specUomName);
                                        if (Boolean.FALSE.equals(uom.getConvertedFine())) {
                                            resSpecEvaluationObj = new InternalMessage(LPPlatform.LAB_FALSE, DataInvRetErrorTrapping.CONVERTER_FALSE, new Object[]{limitId.toString(), "", schemaDataName});
                                        } else {
                                            resultConverted = new BigDecimal((String) uom.getConversionErrorDetail()[1]);
                                        }
                                    }
                                    if (Boolean.FALSE.equals(requiresUnitsConversion) || (Boolean.TRUE.equals(requiresUnitsConversion) && uom.getConvertedFine())) {
                                        if (Boolean.TRUE.equals(specRule.getQuantitativeHasControl())) {
                                            if (Boolean.TRUE.equals(requiresUnitsConversion)) {
                                                resSpecEvaluationObj = resChkSpec.resultCheck(resultConverted, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinControl(), specRule.getMaxControl(), specRule.getMinControlIsStrict(), specRule.getMaxControlIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                                            } else {
                                                resSpecEvaluationObj = resChkSpec.resultCheck(resultValueBigDecimal, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinControl(), specRule.getMaxControl(), specRule.getMinControlIsStrict(), specRule.getMaxControlIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                                            }
                                            resSpecEvaluation = LPArray.addValueToArray1D(resSpecEvaluation, "Regla: " + specRule.getQualitativeRuleRepresentation());
                                        } else {
                                            if (Boolean.TRUE.equals(requiresUnitsConversion)) {
                                                resSpecEvaluationObj = resChkSpec.resultCheck(resultConverted, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                                            } else {
                                                resSpecEvaluationObj = resChkSpec.resultCheck(resultValueBigDecimal, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                                            }
                                        }
                                    }
                                    resSpecEvaluation = LPArray.addValueToArray1D(resSpecEvaluation, "Regla: " + specRule.getQuantitativeRuleRepresentation());
                                }
                            }
                        }
                    }
                }
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines - numHeaderLines + 1, specCode, specCodeVersion, variation, analysis, methodName, methodVersionStr, parameterName, resultValue, resultUomName}));
                BigDecimal secondsInDateRange = LPDate.secondsInDateRange(timeStartedStep, LPDate.getCurrentTimeStamp(), true);
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(String.valueOf(secondsInDateRange)));
                if (numEvaluationArguments > 0) {
                    Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, resSpecEvaluation, 7);
                    Integer stepId = Integer.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStepIdPosic()]).toString());
                    fileContentTable1Builder.append(tstOut.publishEvalStep(request, stepId, resSpecEvaluation, new JSONArray(), tstAssert, timeStartedStep));
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate)).append(LPTestingOutFormat.ROW_END);
                    if ( tstOut.getStopSyntaxisUnmatchPosic()>-1 && Boolean.TRUE.equals(Boolean.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStopSyntaxisUnmatchPosic()]).toString())) &&
                            Boolean.FALSE.equals(TestingAssert.EvalCodes.MATCH.toString().equalsIgnoreCase(tstAssert.getEvalSyntaxisDiagnostic())) ){
                        out.println(fileContentBuilder.toString()); 
                        stopPhrase="Interrupted by evaluation not matching in step "+(iLines+1)+" of "+testingContent.length;
                        break;      
                    }                    
                }
                if (tstOut.getStopSyntaxisFalsePosic()>-1 && Boolean.TRUE.equals(Boolean.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStopSyntaxisFalsePosic()]).toString()))
                    && LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluationObj.getDiagnostic())){
                        out.println(fileContentBuilder.toString()); 
                        stopPhrase="Interrupted by evaluation returning false in step "+(iLines+1)+" of "+testingContent.length;
                    break;
                }                
            }
            fileContentTable1Builder.append(LPTestingOutFormat.TABLE_END);
//            fileContentBuilder.append(tstOut.publishEvalSummary(request, tstAssertSummary));
            fileContentBuilder.append(tstOut.publishEvalSummary(request, tstAssertSummary, stopPhrase, timeStarted)).append("<br>");

            fileContentBuilder.append(fileContentTable1Builder).append(LPTestingOutFormat.BODY_END).append(LPTestingOutFormat.HTML_END);
            out.println(fileContentBuilder.toString());
            LPTestingOutFormat.createLogFile(tstOut.getFilePathName(), fileContentBuilder.toString());
            tstAssertSummary = null;
            resChkSpec = null;
        } catch (Exception error) {
            PrintWriter out = response.getWriter();
            out.println(Arrays.toString(testingContent[currentLine]) + error.getMessage() + currentLine.toString());
            tstAssertSummary = null;
            resChkSpec = null;
        } 
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (ServletException | IOException ex) {
            Logger.getLogger(DbTestingLimitAndResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (ServletException | IOException ex) {
            Logger.getLogger(DbTestingLimitAndResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
