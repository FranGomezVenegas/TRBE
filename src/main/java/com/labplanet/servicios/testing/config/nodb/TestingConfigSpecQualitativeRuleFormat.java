/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.testing.config.nodb;

import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPFrontEnd;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import functionaljavaa.testingscripts.LPTestingParams;
import functionaljavaa.testingscripts.LPTestingParams.TestingServletsConfig;
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
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONArray;

/**
 *
 * @author Administrator
 */
public class TestingConfigSpecQualitativeRuleFormat extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {

        String table1Header = TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUAL_RULEFORMAT.getTablesHeaders();

        response = LPTestingOutFormat.responsePreparation(response);        
        ConfigSpecRule mSpec = new ConfigSpecRule();        
        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();

        String testerFileName=LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUAL_RULEFORMAT.getTesterFileName();                         
        LPTestingOutFormat tstOut=new LPTestingOutFormat(request, LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUAL_RULEFORMAT.name(), testerFileName);
        HashMap<String, Object> csvHeaderTags=tstOut.getCsvHeaderTags();
        
        StringBuilder fileContentBuilder = new StringBuilder(0);        
        fileContentBuilder.append(tstOut.getHtmlStyleHeader());
        Object[][]  testingContent =tstOut.getTestingContent();
        String stopPhrase=null;
        
        try (PrintWriter out = response.getWriter()) {
            if (csvHeaderTags.containsKey(LPPlatform.LAB_FALSE)){
                fileContentBuilder.append("There are missing tags in the file header: ").append(csvHeaderTags.get(LPPlatform.LAB_FALSE));
                out.println(fileContentBuilder.toString()); 
                return;
            }         
            LocalDateTime timeStarted=LPDate.getCurrentTimeStamp();
            Integer numEvaluationArguments = tstOut.getNumEvaluationArguments();
            Integer numHeaderLines = Integer.valueOf(csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.NUM_HEADER_LINES.getTagValue().toString()).toString());   
            
            StringBuilder fileContentTable1Builder = new StringBuilder(0);
            fileContentTable1Builder.append(LPTestingOutFormat.createTableWithHeader(table1Header, numEvaluationArguments));
//numHeaderLines=29;
//testingContent.length            
            for ( Integer iLines =numHeaderLines;iLines<testingContent.length;iLines++){
//if (iLines==42)
//    System.out.print("h");
                LocalDateTime timeStartedStep=LPDate.getCurrentTimeStamp();
                tstAssertSummary.increaseTotalTests();
                    
                TestingAssert tstAssert = new TestingAssert(testingContent[iLines], numEvaluationArguments);
                
                if (testingContent[iLines][0]==null){tstAssertSummary.increasetotalLabPlanetBooleanUndefined();}
                if (testingContent[iLines][1]==null){tstAssertSummary.increasetotalLabPlanetErrorCodeUndefined();}

                Integer lineNumCols = testingContent[0].length-1;
                String ruleType = null;
                if (lineNumCols>=numEvaluationArguments)                               
                     ruleType = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()]);
                String specText = null;
                if (lineNumCols>=numEvaluationArguments+1)                               
                     specText = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()+1]);
                String separator = null;
                if (lineNumCols>=numEvaluationArguments+2)                               
                     separator = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()+2]);

                fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines-numHeaderLines+1, ruleType, specText, separator}));
                    
                Object[] functionEvaluation = mSpec.specLimitIsCorrectQualitative(ruleType, specText, separator);
                    
                BigDecimal SecondsInDateRange = LPDate.SecondsInDateRange(timeStartedStep, LPDate.getCurrentTimeStamp(), true);
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(String.valueOf(SecondsInDateRange)));
                
                if (numEvaluationArguments==0){                    
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(functionEvaluation)));                     
                }                                
                if (numEvaluationArguments>0){                    
                    Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, functionEvaluation);   
                    Integer stepId=Integer.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStepIdPosic()]).toString());
                    fileContentTable1Builder.append(tstOut.publishEvalStep(request, stepId, functionEvaluation, new JSONArray(), tstAssert, timeStartedStep));
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate));                        

                    if ( tstOut.getStopSyntaxisUnmatchPosic()>-1 && Boolean.TRUE.equals(Boolean.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStopSyntaxisUnmatchPosic()]).toString())) &&
                            Boolean.FALSE.equals(TestingAssert.EvalCodes.MATCH.toString().equalsIgnoreCase(tstAssert.getEvalSyntaxisDiagnostic())) ){
                        out.println(fileContentBuilder.toString()); 
                        stopPhrase="Interrupted by evaluation not matching in step "+(iLines+1)+" of "+testingContent.length;
//                        Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName.toString(), GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT.getTableName(), 
//                                new String[]{TblsTesting.Script.RUN_SUMMARY.getName()}, new Object[]{"Interrupted by evaluation not matching in step "+(iLines+1)+" of "+testingContent.length}, 
//                                new String[]{TblsTesting.Script.SCRIPT_ID.getName()}, new Object[]{6}); //testingContent[iLines][tstOut.getScriptIdPosic()]});
                        break;      
                    }
                }
                if (tstOut.getStopSyntaxisFalsePosic()>-1 && Boolean.TRUE.equals(Boolean.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStopSyntaxisFalsePosic()]).toString()))
                    && LPPlatform.LAB_FALSE.equalsIgnoreCase(functionEvaluation[0].toString())){
                        out.println(fileContentBuilder.toString()); 
                        stopPhrase="Interrupted by evaluation returning false in step "+(iLines+1)+" of "+testingContent.length;
//                        Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName.toString(), GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT.getTableName(), 
//                                new String[]{TblsTesting.Script.RUN_SUMMARY.getName()}, new Object[]{"Interrupted by evaluation returning false "+(iLines+1)+" of "+testingContent.length}, 
//                                new String[]{TblsTesting.Script.SCRIPT_ID.getName()}, new Object[]{6}); //testingContent[iLines][tstOut.getScriptIdPosic()]});
                    break;
                }                
                fileContentTable1Builder.append(LPTestingOutFormat.ROW_END);                                                
                
            }    
            fileContentTable1Builder.append(LPTestingOutFormat.TABLE_END);
            //fileContentTable1Builder.append();
            //fileContentBuilder.append(tstOut.publishEvalSummary(request, tstAssertSummary));

            fileContentBuilder.append(tstOut.publishEvalSummary(request, tstAssertSummary, stopPhrase, timeStarted)).append("<br>")
                .append(fileContentTable1Builder);
            
            fileContentBuilder.append(LPTestingOutFormat.BODY_END).append(LPTestingOutFormat.HTML_END);
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
