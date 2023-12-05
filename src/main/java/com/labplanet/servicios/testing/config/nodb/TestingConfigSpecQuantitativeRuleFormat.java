/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.testing.config.nodb;

import static com.labplanet.servicios.testing.config.TestingResultCheckSpecQuantitative.getprettyValue;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPFrontEnd;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import functionaljavaa.testingscripts.LPTestingParams;
import functionaljavaa.testingscripts.TestingAssert;
import functionaljavaa.testingscripts.TestingAssertSummary;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPMath;

/**
 *
 * @author Administrator
 */
public class TestingConfigSpecQuantitativeRuleFormat extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException exception not handled
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String[] tableHeaders = LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUANTI_RULEFORMAT.getTablesHeaders().split("\\|");
        String table1Header = tableHeaders[0];
        String table2Header = tableHeaders[1];

        response = LPTestingOutFormat.responsePreparation(response);
        ConfigSpecRule mSpec = new ConfigSpecRule();
        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();

        String testerFileName = LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUANTI_RULEFORMAT.getTesterFileName();
        LPTestingOutFormat tstOut = new LPTestingOutFormat(request, LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUANTI_RULEFORMAT.name(), testerFileName);
        Map<String, Object> csvHeaderTags = tstOut.getCsvHeaderTags();

        StringBuilder fileContentBuilder = new StringBuilder(0);
        fileContentBuilder.append(tstOut.getHtmlStyleHeader());
        Object[][] testingContent = tstOut.getTestingContent();
        String stopPhrase = null;
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

            StringBuilder fileContentTable2Builder = new StringBuilder(0);
            LocalDateTime timeStarted = LPDate.getCurrentTimeStamp();
            fileContentTable2Builder.append(LPTestingOutFormat.createTableWithHeader(table2Header, numEvaluationArguments));

            for (Integer iLines = numHeaderLines; iLines < testingContent.length; iLines++) {
                tstAssertSummary.increaseTotalTests();
                LocalDateTime timeStartedStep = LPDate.getCurrentTimeStamp();
                TestingAssert tstAssert = new TestingAssert(testingContent[iLines], numEvaluationArguments, false);

                if (testingContent[iLines][0] == null) {
                    tstAssertSummary.increasetotalLabPlanetBooleanUndefined();
                }
                if (testingContent[iLines][1] == null) {
                    tstAssertSummary.increasetotalLabPlanetErrorCodeUndefined();
                }
                Object[] resSpecEvaluation = new Object[0];
                BigDecimal minSpec = null;
                BigDecimal minControl = null;
                BigDecimal maxControl = null;
                BigDecimal maxSpec = null;

                Boolean hasErrors = false;
                for (int i = 0; i < 4; i++) {
                    if (testingContent[iLines][tstOut.getActionNamePosic() + i].toString().length() > 0) {
                        Object[] isNumeric = LPMath.isNumeric(testingContent[iLines][tstOut.getActionNamePosic() + i].toString());
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric[0].toString())) {
                            resSpecEvaluation = isNumeric;
                            hasErrors = true;
                        }
                    }
                }
                Integer lineNumCols = testingContent[0].length - 1;
                if (lineNumCols >= numEvaluationArguments) {
                    {
                        minSpec = LPTestingOutFormat.csvExtractFieldValueBigDecimal(testingContent[iLines][tstOut.getActionNamePosic()]);
                    }
                }
                if (lineNumCols >= numEvaluationArguments + 1) {
                    minControl = LPTestingOutFormat.csvExtractFieldValueBigDecimal(testingContent[iLines][tstOut.getActionNamePosic() + 1]);
                }
                if (lineNumCols >= numEvaluationArguments + 2) {
                    maxControl = LPTestingOutFormat.csvExtractFieldValueBigDecimal(testingContent[iLines][tstOut.getActionNamePosic() + 2]);
                }
                if (lineNumCols >= numEvaluationArguments + 3) {
                    maxSpec = LPTestingOutFormat.csvExtractFieldValueBigDecimal(testingContent[iLines][tstOut.getActionNamePosic() + 3]);
                }
                if (hasErrors) {
                    if (minControl == null && maxControl == null) {
                        fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines - numHeaderLines + 1, getprettyValue(minSpec, false, "MIN"), getprettyValue(maxSpec, false, "MAX")}));
                    } else {
                        fileContentTable2Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines - numHeaderLines + 1, getprettyValue(minSpec, false, "MIN"), getprettyValue(minControl, false, "MIN"), getprettyValue(maxControl, false, "MAX"), getprettyValue(maxSpec, false, "MAX")}));
                    }
                } else {
                    if (minControl == null && maxControl == null) {
                        fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines - numHeaderLines + 1, getprettyValue(minSpec, false, "MIN"), getprettyValue(maxSpec, false, "MAX")}));
                        resSpecEvaluation = mSpec.specLimitIsCorrectQuantitative(minSpec, maxSpec, minControl, maxControl);
                    } else {
                        fileContentTable2Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines - numHeaderLines + 1, getprettyValue(minSpec, false, "MIN"), getprettyValue(minControl, false, "MIN"), getprettyValue(maxControl, false, "MAX"), getprettyValue(maxSpec, false, "MAX")}));
                        resSpecEvaluation = mSpec.specLimitIsCorrectQuantitative(minSpec, maxSpec, minControl, maxControl);
                    }
                }
                BigDecimal secondsInDateRange = LPDate.secondsInDateRange(timeStartedStep, LPDate.getCurrentTimeStamp(), true);
                if (numEvaluationArguments == 0) {
                    if (minControl == null && maxControl == null) {
                        fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(String.valueOf(secondsInDateRange)));
                        fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(resSpecEvaluation)));
                    } else {
                        fileContentTable2Builder.append(LPTestingOutFormat.rowAddField(String.valueOf(secondsInDateRange)));
                        fileContentTable2Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(resSpecEvaluation)));
                    }
                }
                if (numEvaluationArguments > 0) {
                    Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, resSpecEvaluation, 4);
                    if (minControl == null && maxControl == null) {
                        fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(String.valueOf(secondsInDateRange)));
                        fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate));
                        fileContentTable1Builder.append(LPTestingOutFormat.ROW_END);
                    } else {
                        fileContentTable2Builder.append(LPTestingOutFormat.rowAddField(String.valueOf(secondsInDateRange)));
                        fileContentTable2Builder.append(LPTestingOutFormat.rowAddFields(evaluate));
                        fileContentTable2Builder.append(LPTestingOutFormat.ROW_END);
                    }
                }
            }
            tstAssertSummary.notifyResults();
            fileContentTable1Builder.append(LPTestingOutFormat.TABLE_END);
            fileContentTable2Builder.append(LPTestingOutFormat.TABLE_END);

            fileContentBuilder.append(tstOut.publishEvalSummary(request, tstAssertSummary, stopPhrase, timeStarted)).append("<br>")
                    .append(fileContentTable1Builder).append(fileContentTable2Builder);

            fileContentBuilder.append(LPTestingOutFormat.BODY_END).append(LPTestingOutFormat.HTML_END);
            out.println(fileContentBuilder.toString());
            LPTestingOutFormat.createLogFile(tstOut.getFilePathName(), fileContentBuilder.toString());
            tstAssertSummary = null;
            mSpec = null;
        } catch (IOException error) {
            tstAssertSummary = null;
            mSpec = null;
            String exceptionMessage = error.getMessage();
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);
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
        } catch (ServletException | IOException e) {
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
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
        } catch (ServletException | IOException e) {
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
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
