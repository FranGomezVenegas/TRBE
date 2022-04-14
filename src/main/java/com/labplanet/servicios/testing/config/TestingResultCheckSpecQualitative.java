/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.testing.config;

import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPFrontEnd;
import functionaljavaa.materialspec.DataSpec;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import functionaljavaa.testingscripts.LPTestingParams;
import functionaljavaa.testingscripts.LPTestingParams.TestingServletsConfig;
import functionaljavaa.testingscripts.TestingAssert;
import functionaljavaa.testingscripts.TestingAssertSummary;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPAPIArguments;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class TestingResultCheckSpecQualitative extends HttpServlet {
    public enum TestingResultCheckSpecQualitativeArgs{
        DB_CONFIG_SPEC_TESTING_LIMIT_AND_RESULT("DB_CONFIG_SPEC_TESTING_LIMIT_AND_RESULT", "productionLot_newLotCreated_success",
                new LPAPIArguments[]{new LPAPIArguments("schemaName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments("specCode", LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments("specCodeVersion", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments("variation", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                new LPAPIArguments("analysis", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                new LPAPIArguments("methodName", LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                new LPAPIArguments("methodVersion", LPAPIArguments.ArgumentType.STRING.toString(), true, 12),
                new LPAPIArguments("parameterName", LPAPIArguments.ArgumentType.STRING.toString(), true, 13),
                new LPAPIArguments("resultValue", LPAPIArguments.ArgumentType.STRING.toString(), true, 14),
                new LPAPIArguments("resultUomName", LPAPIArguments.ArgumentType.STRING.toString(), false, 15),
        } ),                
        ;
        private TestingResultCheckSpecQualitativeArgs(String name, String successMessageCode, LPAPIArguments[] argums){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums; 
        } 
        public String getName(){
            return this.name;
        }
        public String getSuccessMessageCode(){
            return this.successMessageCode;
        }           

        /**
         * @return the arguments
         */
        public LPAPIArguments[] getArguments() {
            return arguments;
        }     
        private final String name;
        private final String successMessageCode;       
        private  LPAPIArguments[] arguments;
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
        String table1Header = TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUAL_RESULTCHECK.getTablesHeaders();
        response = LPTestingOutFormat.responsePreparation(response);        
        DataSpec resChkSpec = new DataSpec();   

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForUAT(request, response, true, "");
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();


        String testerFileName=LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUAL_RESULTCHECK.getTesterFileName();                         
        LPTestingOutFormat tstOut=new LPTestingOutFormat(request, LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUAL_RESULTCHECK.name(), testerFileName);
        HashMap<String, Object> csvHeaderTags=tstOut.getCsvHeaderTags();

        StringBuilder fileContentBuilder = new StringBuilder(0);        
        fileContentBuilder.append(tstOut.getHtmlStyleHeader());
        Object[][]  testingContent =tstOut.getTestingContent();
Integer currentLine=0;
        
/*        String csvPathName =(String) request.getAttribute(LPTestingParams.UPLOAD_FILE_PARAM_FILE_PATH);
        String csvFileName =(String) request.getAttribute(LPTestingParams.UPLOAD_FILE_PARAM_FILE_NAME);
        if ("".equals(csvPathName) || csvPathName==null){
            csvFileName = LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUAL_RESULTCHECK.getTesterFileName();                         
            csvPathName = LPTestingOutFormat.TESTING_FILES_PATH; }
        csvPathName = csvPathName+csvFileName; 
        String csvFileSeparator=LPTestingOutFormat.TESTING_FILES_FIELD_SEPARATOR;
        
        Object[][] csvFileContent = LPArray.convertCSVinArray(csvPathName, csvFileSeparator); 

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
            LPAPIArguments[] arguments = TestingResultCheckSpecQualitativeArgs.DB_CONFIG_SPEC_TESTING_LIMIT_AND_RESULT.getArguments();
            
/*            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(csvFileContent[0][0].toString())){
                fileContentBuilder.append("Problem trying to extract file content for file "+csvPathName+csvFileContent[0][1].toString()); 
                out.println(fileContentBuilder.toString());
                return;            
            }
            HashMap<String, Object> csvHeaderTags = LPTestingOutFormat.getCSVHeader(LPArray.convertCSVinArray(csvPathName, LPTestingOutFormat.FileHeaderTags.SEPARATOR.toString()));
            if (csvHeaderTags.containsKey(LPPlatform.LAB_FALSE)){
                fileContentBuilder.append(LPTestingOutFormat.ERROR_TRAPPING_FILEHEADER_MISSING_TAGS).append(csvHeaderTags.get(LPPlatform.LAB_FALSE));
                out.println(fileContentBuilder.toString()); 
                return;
            }                        
            Integer numEvaluationArguments = Integer.valueOf(csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.NUM_EVALUATION_ARGUMENTS.getTagValue().toString()).toString());   
            Integer numHeaderLines = Integer.valueOf(csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.NUM_HEADER_LINES.getTagValue().toString()).toString());   
            String table1Header = csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.TABLE_NAME.getTagValue().toString()+"1").toString();     
            StringBuilder fileContentTable1Builder = new StringBuilder(0);
            fileContentTable1Builder.append(LPTestingOutFormat.createTableWithHeader(table1Header, numEvaluationArguments));       
*/
//testingContent.length
//numHeaderLines=1;
            for (Integer iLines=numHeaderLines;iLines<testingContent.length;iLines++){
                tstAssertSummary.increaseTotalTests();
                TestingAssert tstAssert = new TestingAssert(testingContent[iLines], numEvaluationArguments);                
                
                Integer lineNumCols = testingContent[0].length-1;
                String result = null;
                String ruleType = null;
                String values = null;
                String separator = null;
                String listName = null;
                
                if (lineNumCols>=numEvaluationArguments) result = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()]);
                if (lineNumCols>=numEvaluationArguments+1) ruleType = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()+1]);
                if (lineNumCols>=numEvaluationArguments+2) values = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()+2]);
                if (lineNumCols>=numEvaluationArguments+3) separator = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()+3]);
                if (lineNumCols>=numEvaluationArguments+4) listName = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()+4]);

                fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines-numHeaderLines+1, result, ruleType, values, separator, listName}));                    
                Object[] resSpecEvaluation = resChkSpec.resultCheck(result, ruleType, values, separator, listName);
                if (numEvaluationArguments<=0){                    
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(resSpecEvaluation)));
                }else{
                    Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, resSpecEvaluation);
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate));
                }                
                fileContentTable1Builder.append(LPTestingOutFormat.rowEnd());
            }                          
            tstAssertSummary.notifyResults();
            fileContentTable1Builder.append(LPTestingOutFormat.tableEnd());
            fileContentTable1Builder.append(tstOut.getFilePathName());
            fileContentBuilder.append(LPTestingOutFormat.createSummaryTable(tstAssertSummary, numEvaluationArguments, null, null)).append(fileContentTable1Builder)
                    .append(LPTestingOutFormat.bodyEnd()).append(LPTestingOutFormat.htmlEnd());
            out.println(fileContentBuilder.toString());            
            LPTestingOutFormat.createLogFile(tstOut.getFilePathName(), fileContentBuilder.toString());
        }
        catch(IOException error){
            PrintWriter out = response.getWriter();
            out.println(error.getMessage());
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
