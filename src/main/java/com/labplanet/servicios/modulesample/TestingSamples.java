/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import com.labplanet.servicios.app.TestingRegressionUAT;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import functionaljavaa.testingscripts.LPTestingParams;
import functionaljavaa.testingscripts.LPTestingParams.TestingServletsConfig;
import functionaljavaa.testingscripts.TestingAssert;
import functionaljavaa.testingscripts.TestingAssertSummary;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;

/**
 *
 * @author User
 */
public class TestingSamples extends HttpServlet {

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
        
        String table1Header = TestingServletsConfig.DB_SCHEMADATA_SAMPLES.getTablesHeaders();
        Integer table1NumArgs=13;
        
        Object[] functionEvaluation=new Object[0];
        JSONArray functionRelatedObjects=new JSONArray();

        response = LPTestingOutFormat.responsePreparation(response);        
        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();

        String testerFileName=LPTestingParams.TestingServletsConfig.DB_SCHEMADATA_ENVMONIT_SAMPLES.getTesterFileName();                         
        LPTestingOutFormat tstOut=new LPTestingOutFormat(request, LPTestingParams.TestingServletsConfig.DB_SCHEMADATA_ENVMONIT_SAMPLES.name(), testerFileName);
        HashMap<String, Object> csvHeaderTags=tstOut.getCsvHeaderTags();
        
        StringBuilder fileContentBuilder = new StringBuilder(0);        
        fileContentBuilder.append(tstOut.getHtmlStyleHeader());
        Object[][]  testingContent =tstOut.getTestingContent();
        testingContent=LPArray.addColumnToArray2D(testingContent, new JSONArray());
        
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
            for ( Integer iLines =numHeaderLines;iLines<testingContent.length;iLines++){
                LocalDateTime timeStartedStep=LPDate.getCurrentTimeStamp();
                tstAssertSummary.increaseTotalTests();                    
                TestingAssert tstAssert = new TestingAssert(testingContent[iLines], numEvaluationArguments);                
                
                Object actionName = LPNulls.replaceNull(testingContent[iLines][5]).toString();
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                    new Object[]{iLines-numHeaderLines+1, "actionName"+":"+LPNulls.replaceNull(testingContent[iLines][5]).toString()}));                     

                ClassSampleController clssController=new ClassSampleController(request, actionName.toString(), testingContent, iLines, table1NumArgs);
                if (clssController.getFunctionFound()){
                    functionRelatedObjects=clssController.getFunctionRelatedObjects();
                    functionEvaluation=(Object[]) clssController.getFunctionDiagn();
                    testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                    fileContentTable1Builder.append(clssController.getRowArgsRows());
                }                
                
                if (testingContent[iLines][0]==null){tstAssertSummary.increasetotalLabPlanetBooleanUndefined();}
                if (testingContent[iLines][1]==null){tstAssertSummary.increasetotalLabPlanetErrorCodeUndefined();}
                                    
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                    new Object[]{
                    (LPNulls.replaceNull(testingContent[iLines][2]).toString().length()>0) ? "Yes" : "No",
                    (LPNulls.replaceNull(testingContent[iLines][3]).toString().length()>0) ? "Yes" : "No",
                    (LPNulls.replaceNull(testingContent[iLines][4]).toString().length()>0) ? "Yes" : "No",
                }));                                     

                if (numEvaluationArguments==0){                    
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(functionEvaluation)));                     
                }                                
                if (numEvaluationArguments>0){                    
                    Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, functionEvaluation);   
                    Integer stepId=Integer.valueOf(testingContent[iLines][tstOut.getStepIdPosic()].toString());
                    fileContentTable1Builder.append(tstOut.publishEvalStep(request, stepId, functionEvaluation, functionRelatedObjects, tstAssert, timeStartedStep));
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate));                        
                }
                fileContentTable1Builder.append(LPTestingOutFormat.rowEnd());                                                
            }    
            fileContentTable1Builder.append(LPTestingOutFormat.tableEnd());
            fileContentTable1Builder.append(LPTestingOutFormat.businessRulesTable());            
            fileContentBuilder.append(tstOut.publishEvalSummary(request, tstAssertSummary));
            fileContentBuilder.append(fileContentTable1Builder).append(LPTestingOutFormat.bodyEnd()).append(LPTestingOutFormat.htmlEnd());

            out.println(fileContentBuilder.toString());            
            LPTestingOutFormat.createLogFile(tstOut.getFilePathName(), fileContentBuilder.toString());
            tstAssertSummary=null; 
        }
        catch(IOException error){
            //tstAssertSummary=null; 
            String exceptionMessage = error.getMessage();     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null);                    
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        try {
            processRequest(request, response);
        } catch (ServletException | IOException ex) {
            Logger.getLogger(TestingRegressionUAT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        try {
            processRequest(request, response);
        } catch (ServletException | IOException ex) {
            Logger.getLogger(TestingRegressionUAT.class.getName()).log(Level.SEVERE, null, ex);
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
