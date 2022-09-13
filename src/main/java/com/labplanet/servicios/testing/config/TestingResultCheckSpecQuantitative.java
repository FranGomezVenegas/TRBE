package com.labplanet.servicios.testing.config;

import functionaljavaa.materialspec.ConfigSpecRule.QuantSymbols;
import functionaljavaa.materialspec.DataSpec;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPPlatform;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import functionaljavaa.testingscripts.LPTestingParams;
import lbplanet.utilities.LPFrontEnd;
import functionaljavaa.testingscripts.TestingAssert;
import functionaljavaa.testingscripts.TestingAssertSummary;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
/**
 *
 * @author Administrator
 */
public class TestingResultCheckSpecQuantitative extends HttpServlet {
    public static String getprettyValue(Object value, Boolean strictMode, String bound){
        //if (1==1) return "hola";
        if (value==null || value.toString().length()==0) return "";
        String prettyVal=LPNulls.replaceNull(value).toString();
        if (strictMode)
            if ("MIN".equalsIgnoreCase(bound))                
                prettyVal=prettyVal+" "+QuantSymbols.MIN_STRICT.getSymbol();
            else
                prettyVal=QuantSymbols.MAX_STRICT.getSymbol()+" "+prettyVal;
        else
            if ("MIN".equalsIgnoreCase(bound))                
                prettyVal=prettyVal+" "+QuantSymbols.MIN.getSymbol();
            else
                prettyVal=QuantSymbols.MAX.getSymbol()+" "+prettyVal;
        return prettyVal;
    }    

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {        

        
        String[] tableHeaders = LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUANTI_RESULTCHECK.getTablesHeaders().split("\\|");
        String table1Header=tableHeaders[0];
        String table2Header=tableHeaders[1];

        response = LPTestingOutFormat.responsePreparation(response);        
        DataSpec resChkSpec = new DataSpec();   
        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();

        String testerFileName=LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUANTI_RESULTCHECK.getTesterFileName();                         
        LPTestingOutFormat tstOut=new LPTestingOutFormat(request, LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUANTI_RESULTCHECK.name(), testerFileName);
        HashMap<String, Object> csvHeaderTags=tstOut.getCsvHeaderTags();

        StringBuilder fileContentBuilder = new StringBuilder(0);        
        fileContentBuilder.append(tstOut.getHtmlStyleHeader());
        Object[][]  testingContent =tstOut.getTestingContent();
        String stopPhrase=null;

/*        response = LPTestingOutFormat.responsePreparation(response);        
        DataSpec resChkSpec = new DataSpec();   
        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForUAT(request, response, true, "");
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }

        String csvPathName =(String) request.getAttribute(LPTestingParams.UPLOAD_FILE_PARAM_FILE_PATH);
        String csvFileName =(String) request.getAttribute(LPTestingParams.UPLOAD_FILE_PARAM_FILE_NAME);
        if ("".equals(csvPathName) || csvPathName==null){
            csvFileName = LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUANTI_RESULTCHECK.getTesterFileName();                         
            csvPathName = LPTestingOutFormat.TESTING_FILES_PATH; }
        csvPathName = csvPathName+csvFileName; 
        String csvFileSeparator=LPTestingOutFormat.TESTING_FILES_FIELD_SEPARATOR;
        
        Object[][] testingContent = LPArray.convertCSVinArray(csvPathName, csvFileSeparator); 
        StringBuilder fileContentBuilder = new StringBuilder(0);
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
        
/*            
            fileContentBuilder.append(LPTestingOutFormat.getHtmlStyleHeader(this.getClass().getSimpleName(), csvFileName));
            HashMap<String, Object> csvHeaderTags = LPTestingOutFormat.getCSVHeader(LPArray.convertCSVinArray(csvPathName, "="));
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
            fileContentTable2Builder.append(LPTestingOutFormat.createTableWithHeader(table2Header, numEvaluationArguments));
            LocalDateTime timeStarted=LPDate.getCurrentTimeStamp();
//testingContent.length
            for (Integer iLines=numHeaderLines;iLines<testingContent.length;iLines++){
                tstAssertSummary.increaseTotalTests();
                LocalDateTime timeStartedStep=LPDate.getCurrentTimeStamp();
                TestingAssert tstAssert = new TestingAssert(testingContent[iLines], numEvaluationArguments);
                
                Integer lineNumCols = testingContent[0].length-1;
                BigDecimal result = null;
                if (lineNumCols>=numEvaluationArguments)
                    {result = LPTestingOutFormat.csvExtractFieldValueBigDecimal(testingContent[iLines][tstOut.getActionNamePosic()]);}
                BigDecimal minSpec = null;
                if (lineNumCols>=numEvaluationArguments+1)
                    {minSpec = LPTestingOutFormat.csvExtractFieldValueBigDecimal(testingContent[iLines][tstOut.getActionNamePosic()+1]);}
                Boolean minStrict = null;
                if (lineNumCols>=numEvaluationArguments+2)
                    {minStrict = LPTestingOutFormat.csvExtractFieldValueBoolean(testingContent[iLines][tstOut.getActionNamePosic()+2]);}
                BigDecimal maxSpec = null;
                if (lineNumCols>=numEvaluationArguments+3)
                    {maxSpec = LPTestingOutFormat.csvExtractFieldValueBigDecimal(testingContent[iLines][tstOut.getActionNamePosic()+3]);}
                Boolean maxStrict = null;
                if (lineNumCols>=numEvaluationArguments+4)
                    { maxStrict = LPTestingOutFormat.csvExtractFieldValueBoolean(testingContent[iLines][tstOut.getActionNamePosic()+4]);}
                BigDecimal minControl = null;
                if (lineNumCols>=numEvaluationArguments+5)
                    { minControl = LPTestingOutFormat.csvExtractFieldValueBigDecimal(testingContent[iLines][tstOut.getActionNamePosic()+5]);}
                Boolean minControlStrict = null;
                if (lineNumCols>=numEvaluationArguments+6)
                    { minControlStrict = LPTestingOutFormat.csvExtractFieldValueBoolean(testingContent[iLines][tstOut.getActionNamePosic()+6]);}
                BigDecimal maxControl = null;
                if (lineNumCols>=numEvaluationArguments+7)
                    {maxControl = LPTestingOutFormat.csvExtractFieldValueBigDecimal(testingContent[iLines][tstOut.getActionNamePosic()+7]);}
                Boolean maxControlStrict = null;
                if (lineNumCols>=numEvaluationArguments+8)
                    {maxControlStrict = LPTestingOutFormat.csvExtractFieldValueBoolean(testingContent[iLines][tstOut.getActionNamePosic()+8]);}
                    
                Object[] resSpecEvaluation = new Object[0];
                if (minControl==null){
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines-numHeaderLines+1, result, getprettyValue(minSpec, minStrict, "MIN"), getprettyValue(maxSpec, maxStrict, "MAX")}));
                    resSpecEvaluation = resChkSpec.resultCheck(result, minSpec, maxSpec, minStrict, maxStrict, null, null);
                }else{                   
                    fileContentTable2Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines, getprettyValue(minSpec, minStrict, "MIN"), 
                                getprettyValue(minControl, minControlStrict, "MIN"), result, getprettyValue(maxControl, maxControlStrict, "MAX"), getprettyValue(maxSpec, maxStrict, "MAX")}));
                    resSpecEvaluation = resChkSpec.resultCheck(
                            result, minSpec, maxSpec, minStrict, maxStrict, minControl, maxControl, minControlStrict, maxControlStrict, null, null);
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
                        fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate)).append(LPTestingOutFormat.rowEnd());
                    }else{
                        fileContentTable2Builder.append(LPTestingOutFormat.rowAddFields(evaluate)).append(LPTestingOutFormat.rowEnd());
                    }
                }
            }       
            tstAssertSummary.notifyResults();
            fileContentTable1Builder.append(LPTestingOutFormat.tableEnd());
            fileContentTable2Builder.append(LPTestingOutFormat.tableEnd());
            
            fileContentBuilder.append(tstOut.publishEvalSummary(request, tstAssertSummary, stopPhrase, timeStarted)).append("<br>")
                .append(fileContentTable1Builder).append(fileContentTable2Builder.toString()).append(LPTestingOutFormat.bodyEnd()).append(LPTestingOutFormat.htmlEnd());
            
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
            
            fileContentBuilder.append(tstOut.publishEvalSummary(request, tstAssertSummary, stopPhrase, timeStarted)).append("<br>");
            
            String fileContentSummary = LPTestingOutFormat.createSummaryTable(tstAssertSummary, numEvaluationArguments, summaryPhrase, null);
            
            fileContentBuilder.append(fileContentSummary).append(fileContentTable1Builder.toString()).append(fileContentTable2Builder.toString());
*/            
            out.println(fileContentBuilder.toString());            
            LPTestingOutFormat.createLogFile(tstOut.getFilePathName(), fileContentBuilder.toString());
            tstAssertSummary=null; resChkSpec=null;
        }
        catch(Exception error){
            PrintWriter out = response.getWriter();
            out.println(error.getMessage());
            tstAssertSummary=null; resChkSpec=null;
            String exceptionMessage = error.getMessage();     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);                    
        } finally {
            // release database resources
            try {
                // Rdbms.closeRdbms();   
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }       }

    
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

