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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPDate;


/**
 *
 * @author Administrator
 */
public class TestingConfigSpecQuantitativeRuleFormat extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException exception not handled
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {

        String[] tableHeaders = LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUANTI_RULEFORMAT.getTablesHeaders().split("\\|");
        String table1Header=tableHeaders[0];
        String table2Header=tableHeaders[1];
        
        response = LPTestingOutFormat.responsePreparation(response);        
        ConfigSpecRule mSpec = new ConfigSpecRule();        
        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();

        String testerFileName=LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUANTI_RULEFORMAT.getTesterFileName();                         
        LPTestingOutFormat tstOut=new LPTestingOutFormat(request, LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUANTI_RULEFORMAT.name(), testerFileName);
        HashMap<String, Object> csvHeaderTags=tstOut.getCsvHeaderTags();

        StringBuilder fileContentBuilder = new StringBuilder(0);        
        fileContentBuilder.append(tstOut.getHtmlStyleHeader());
        Object[][]  testingContent =tstOut.getTestingContent();
        String stopPhrase=null;
        
/*
        String csvPathName =(String) request.getAttribute(LPTestingParams.UPLOAD_FILE_PARAM_FILE_PATH);
        String csvFileName =(String) request.getAttribute(LPTestingParams.UPLOAD_FILE_PARAM_FILE_NAME);
        if ("".equals(csvPathName) || csvPathName==null){
            csvFileName = LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUANTI_RULEFORMAT.getTesterFileName();                         
            csvPathName = LPTestingOutFormat.TESTING_FILES_PATH; }
        csvPathName = csvPathName+csvFileName; 
        String csvFileSeparator=LPTestingOutFormat.TESTING_FILES_FIELD_SEPARATOR;
        
        Object[][] testingContent = LPArray.convertCSVinArray(csvPathName, csvFileSeparator); 
        StringBuilder fileContentBuilder = new StringBuilder(0);
        fileContentBuilder.append(LPTestingOutFormat.getHtmlStyleHeader(this.getClass().getSimpleName(), csvFileName));                
*/
        try (PrintWriter out = response.getWriter()) {
            if (csvHeaderTags.containsKey(LPPlatform.LAB_FALSE)){
                fileContentBuilder.append("There are missing tags in the file header: ").append(csvHeaderTags.get(LPPlatform.LAB_FALSE));
                out.println(fileContentBuilder.toString()); 
                return;
            }            
            Integer numEvaluationArguments = tstOut.getNumEvaluationArguments();
            Integer numHeaderLines = Integer.valueOf(csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.NUM_HEADER_LINES.getTagValue().toString()).toString());   
            
            StringBuilder fileContentTable1Builder = new StringBuilder(0);
            fileContentTable1Builder.append(LPTestingOutFormat.createTableWithHeader(table1Header, numEvaluationArguments));
            
/*            HashMap<String, Object> csvHeaderTags = LPTestingOutFormat.getCSVHeader(LPArray.convertCSVinArray(csvPathName, "="));
            if (csvHeaderTags.containsKey(LPPlatform.LAB_FALSE)){
                fileContentBuilder.append("There are missing tags in the file header: ").append(csvHeaderTags.get(LPPlatform.LAB_FALSE));
                out.println(fileContentBuilder.toString()); 
                return;
            }            
            
            Integer numEvaluationArguments = Integer.valueOf(csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.NUM_EVALUATION_ARGUMENTS.getTagValue().toString()).toString());   
            Integer numHeaderLines = Integer.valueOf(csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.NUM_HEADER_LINES.getTagValue().toString()).toString());   
*/            
//            String table1Header = csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.TABLE_NAME.getTagValue().toString()+"1").toString();               
//            StringBuilder fileContentTable1Builder = new StringBuilder(0);
//            fileContentTable1Builder.append(LPTestingOutFormat.createTableWithHeader(table1Header, numEvaluationArguments));

//            String table2Header = csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.TABLE_NAME.getTagValue().toString()+"2").toString();            
            StringBuilder fileContentTable2Builder = new StringBuilder(0);
            LocalDateTime timeStarted=LPDate.getCurrentTimeStamp();
            fileContentTable2Builder.append(LPTestingOutFormat.createTableWithHeader(table2Header, numEvaluationArguments));


//testingContent.length    
//Integer totall=5;
//numHeaderLines=27;
            for (Integer iLines=numHeaderLines;iLines<testingContent.length;iLines++){
                tstAssertSummary.increaseTotalTests();
                LocalDateTime timeStartedStep=LPDate.getCurrentTimeStamp();
//out.println(iLines.toString());
                TestingAssert tstAssert = new TestingAssert(testingContent[iLines], numEvaluationArguments);
                
                if (testingContent[iLines][0]==null){tstAssertSummary.increasetotalLabPlanetBooleanUndefined();}
                if (testingContent[iLines][1]==null){tstAssertSummary.increasetotalLabPlanetErrorCodeUndefined();}

                Integer lineNumCols = testingContent[0].length-1;
                BigDecimal minSpec = null;
                if (lineNumCols>=numEvaluationArguments)
                    {minSpec = LPTestingOutFormat.csvExtractFieldValueBigDecimal(testingContent[iLines][tstOut.getActionNamePosic()]);}
                BigDecimal minControl = null;
                if (lineNumCols>=numEvaluationArguments+1)
                    {minControl = LPTestingOutFormat.csvExtractFieldValueBigDecimal(testingContent[iLines][tstOut.getActionNamePosic()+1]);}
                BigDecimal maxControl = null;
                if (lineNumCols>=numEvaluationArguments+2)
                    {maxControl = LPTestingOutFormat.csvExtractFieldValueBigDecimal(testingContent[iLines][tstOut.getActionNamePosic()+2]);}
                BigDecimal maxSpec = null;
                if (lineNumCols>=numEvaluationArguments+3)
                    {maxSpec = LPTestingOutFormat.csvExtractFieldValueBigDecimal(testingContent[iLines][tstOut.getActionNamePosic()+3]);}
                    
                Object[] resSpecEvaluation = new Object[0];                
                if (minControl==null){
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines-numHeaderLines+1, getprettyValue(minSpec, false, "MIN"), getprettyValue(maxSpec, false, "MAX")}));
                    resSpecEvaluation = mSpec.specLimitIsCorrectQuantitative(minSpec,maxSpec, minControl, maxControl);
                }else{
                    fileContentTable2Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines-numHeaderLines+1, getprettyValue(minSpec, false, "MIN"), getprettyValue(minControl, false, "MIN"), getprettyValue(maxControl, false, "MAX"), getprettyValue(maxSpec, false, "MAX")}));
                    resSpecEvaluation = mSpec.specLimitIsCorrectQuantitative(minSpec,maxSpec, minControl, maxControl);
                }        
                BigDecimal SecondsInDateRange = LPDate.SecondsInDateRange(timeStartedStep, LPDate.getCurrentTimeStamp(), true);
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(String.valueOf(SecondsInDateRange)));
                
                if (numEvaluationArguments==0){                    
                    if (minControl==null){
                        fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(resSpecEvaluation)));                     
                    }else{
                        fileContentTable2Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(resSpecEvaluation)));                     
                    }
                }                                
                if (numEvaluationArguments>0){                    
                    Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, resSpecEvaluation);
                    if (minControl==null){
                        fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate));                        
                        fileContentTable1Builder.append(LPTestingOutFormat.rowEnd());                                                
                    }else{
                        fileContentTable2Builder.append(LPTestingOutFormat.rowAddFields(evaluate));                        
                        fileContentTable2Builder.append(LPTestingOutFormat.rowEnd());                                                
                    }
                }
            }    
            tstAssertSummary.notifyResults();
            fileContentTable1Builder.append(LPTestingOutFormat.tableEnd());                                                
            fileContentTable2Builder.append(LPTestingOutFormat.tableEnd());       
            
            fileContentBuilder.append(tstOut.publishEvalSummary(request, tstAssertSummary, stopPhrase, timeStarted)).append("<br>")
                .append(fileContentTable1Builder).append(fileContentTable2Builder);
            
/*            String summaryPhrase ="";
            String scriptIdStr=request.getParameter("scriptId");                       
            Integer scriptId=Integer.valueOf(LPNulls.replaceNull(scriptIdStr));             
            if (numEvaluationArguments==0) summaryPhrase="COMPLETED ALL STEPS";
            else{
                if (tstAssertSummary.getTotalSyntaxisMatch()==testingContent.length){
                    summaryPhrase="COMPLETED SUCCESSFULLY";
                    String savePoint= LPNulls.replaceNull(request.getAttribute(LPTestingParams.SCRIPT_EXECUTION_EVIDENCE_SAVE)).toString();
                    if (savePoint==null || savePoint.length()==0)
                        savePoint= LPNulls.replaceNull(request.getParameter(LPTestingParams.SCRIPT_EXECUTION_EVIDENCE_SAVE)).toString();
                    if (Boolean.valueOf(savePoint))
                        scriptExecutionEvidenceSave(scriptId, summaryPhrase);
                }else{
                    summaryPhrase="COMPLETED WITH UNEXPECTED RESULTS. ";
                    if (tstAssertSummary.getTotalSyntaxisUnMatch()>0) summaryPhrase=summaryPhrase+"Unmatched="+tstAssertSummary.getTotalSyntaxisUnMatch()+". ";
                    if (tstAssertSummary.getTotalSyntaxisUndefined()>0) summaryPhrase=summaryPhrase+"Undefined="+tstAssertSummary.getTotalSyntaxisUndefined()+". ";
                }
            }
            
            if (numEvaluationArguments>0){
                String fileContentSummary = LPTestingOutFormat.createSummaryTable(tstAssertSummary, numEvaluationArguments, summaryPhrase, null);
                fileContentBuilder.append(fileContentSummary).append(fileContentTable1Builder).append(fileContentTable2Builder);
            }
*/            
            fileContentBuilder.append(LPTestingOutFormat.bodyEnd()).append(LPTestingOutFormat.htmlEnd());
            out.println(fileContentBuilder.toString());            
            LPTestingOutFormat.createLogFile(tstOut.getFilePathName(), fileContentBuilder.toString());
            tstAssertSummary=null; mSpec=null;
        }
        catch(IOException error){
            tstAssertSummary=null; mSpec=null;
            String exceptionMessage = error.getMessage();     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);                    
        } finally {
            // release database resources
            try {
                // Rdbms.closeRdbms();   
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
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
