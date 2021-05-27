/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.app.TestingRegressionUAT;
import com.labplanet.servicios.modulesample.ClassSampleController;
import databases.Rdbms;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import functionaljavaa.testingscripts.LPTestingParams;
import functionaljavaa.testingscripts.LPTestingParams.TestingServletsConfig;
import functionaljavaa.testingscripts.TestingAssert;
import functionaljavaa.testingscripts.TestingAssertSummary;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class TestingEnvMonitSamples extends HttpServlet {

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
        String table1Header = TestingServletsConfig.DB_SCHEMADATA_ENVMONIT_SAMPLES.getTablesHeaders();
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
        
        String stopPhrase=null;
        
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
                tstAssertSummary.increaseTotalTests();                    
                TestingAssert tstAssert = new TestingAssert(testingContent[iLines], numEvaluationArguments);                

                Object actionName = LPNulls.replaceNull(testingContent[iLines][5]).toString();
                request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, actionName);
                if (tstOut.getAuditReasonPosic()!=-1)
                    request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE, LPNulls.replaceNull(testingContent[iLines][tstOut.getAuditReasonPosic()]).toString());

                fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                    new Object[]{iLines-numHeaderLines+1, "actionName"+":"+LPNulls.replaceNull(testingContent[iLines][5]).toString()}));                     

                ClassEnvMonSampleController clssEnvMonSampleController=new ClassEnvMonSampleController(request, actionName.toString(), testingContent, iLines, table1NumArgs, tstOut.getAuditReasonPosic());
                if (clssEnvMonSampleController.getFunctionFound()){
                    functionRelatedObjects=clssEnvMonSampleController.getFunctionRelatedObjects();
                    functionEvaluation=(Object[]) clssEnvMonSampleController.getFunctionDiagn();
                    testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                    fileContentTable1Builder.append(clssEnvMonSampleController.getRowArgsRows());
                }else{
                    ClassEnvMonSampleFrontendController clssEnvMonSampleFrontendController=new ClassEnvMonSampleFrontendController(request, actionName.toString(), testingContent, iLines, table1NumArgs);
                    if (clssEnvMonSampleFrontendController.getFunctionFound()){
                        functionRelatedObjects=clssEnvMonSampleFrontendController.getFunctionRelatedObjects();
                        functionEvaluation=(Object[]) clssEnvMonSampleFrontendController.getFunctionDiagn();
                        testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                        fileContentTable1Builder.append(clssEnvMonSampleFrontendController.getRowArgsRows());
                    }else{
                        ClassEnvMonController clssEnvMonController=new ClassEnvMonController(request, actionName.toString(), testingContent, iLines, table1NumArgs);
                        if (clssEnvMonController.getFunctionFound()){
                            functionRelatedObjects=clssEnvMonController.getFunctionRelatedObjects();
                            functionEvaluation=(Object[]) clssEnvMonController.getFunctionDiagn();
                            testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                            fileContentTable1Builder.append(clssEnvMonController.getRowArgsRows());
                        }else{                    
                            ClassEnvMonIncubatorController clssEnvMonIncubController=new ClassEnvMonIncubatorController(request, actionName.toString(), testingContent, iLines, table1NumArgs);
                            if (clssEnvMonIncubController.getFunctionFound()){
                                functionRelatedObjects=clssEnvMonIncubController.getFunctionRelatedObjects();
                                functionEvaluation=(Object[]) clssEnvMonIncubController.getFunctionDiagn();
                                testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                                fileContentTable1Builder.append(clssEnvMonIncubController.getRowArgsRows());                
                            }else{                            
                                ClassEnvMonQueriesController clssEnvMonQueriesController=new ClassEnvMonQueriesController(request, actionName.toString(), testingContent, iLines, table1NumArgs);
                                if (clssEnvMonQueriesController.getFunctionFound()){
                                    functionRelatedObjects=clssEnvMonQueriesController.getFunctionRelatedObjects();
                                    functionEvaluation=(Object[]) clssEnvMonQueriesController.getFunctionDiagn();
                                    testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                                    fileContentTable1Builder.append(clssEnvMonQueriesController.getRowArgsRows());                
                                }else{
                                    ClassSampleController clssSampleController=new ClassSampleController(request, actionName.toString(), testingContent, iLines, table1NumArgs);
                                    if (clssSampleController.getFunctionFound()){
                                        functionRelatedObjects=clssSampleController.getFunctionRelatedObjects();
                                        functionEvaluation=(Object[]) clssSampleController.getFunctionDiagn();
                                        testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                                        fileContentTable1Builder.append(clssSampleController.getRowArgsRows());                
                                    }else{
                                        functionEvaluation=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Endpoint <*1*> not found", new Object[]{actionName});
                                        testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                                        fileContentTable1Builder.append(clssSampleController.getRowArgsRows());         
                                    }
                                }
                            }
                        }
                    }
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
                    fileContentTable1Builder.append(tstOut.publishEvalStep(request, stepId, functionEvaluation, functionRelatedObjects, tstAssert));
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate));                        
                    if ( tstOut.getStopSyntaxisUnmatchPosic()>-1 && Boolean.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStopSyntaxisUnmatchPosic()]).toString()) &&
                            !TestingAssert.EvalCodes.MATCH.toString().equalsIgnoreCase(tstAssert.getEvalSyntaxisDiagnostic()) ){
                        out.println(fileContentBuilder.toString()); 
                        stopPhrase="Interrupted by evaluation not matching in step "+(iLines+1)+" of "+testingContent.length;
//                        Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix.toString(), GlobalVariables.Schemas.TESTING.getName()), TblsTesting.Script.TBL.getName(), 
//                                new String[]{TblsTesting.Script.FLD_RUN_SUMMARY.getName()}, new Object[]{"Interrupted by evaluation not matching in step "+(iLines+1)+" of "+testingContent.length}, 
//                                new String[]{TblsTesting.Script.FLD_SCRIPT_ID.getName()}, new Object[]{6}); //testingContent[iLines][tstOut.getScriptIdPosic()]});
                        break;      
                    }
                }
                if (tstOut.getStopSyntaxisFalsePosic()>-1 && Boolean.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStopSyntaxisFalsePosic()]).toString())
                    && LPPlatform.LAB_FALSE.equalsIgnoreCase(functionEvaluation[0].toString())){
                        out.println(fileContentBuilder.toString()); 
                        stopPhrase="Interrupted by evaluation returning false in step "+(iLines+1)+" of "+testingContent.length;
//                        Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix.toString(), GlobalVariables.Schemas.TESTING.getName()), TblsTesting.Script.TBL.getName(), 
//                                new String[]{TblsTesting.Script.FLD_RUN_SUMMARY.getName()}, new Object[]{"Interrupted by evaluation returning false "+(iLines+1)+" of "+testingContent.length}, 
//                                new String[]{TblsTesting.Script.FLD_SCRIPT_ID.getName()}, new Object[]{6}); //testingContent[iLines][tstOut.getScriptIdPosic()]});
                    break;
                }
                fileContentTable1Builder.append(LPTestingOutFormat.rowEnd());                                                
            }    
            fileContentTable1Builder.append(LPTestingOutFormat.tableEnd());
            fileContentBuilder.append(tstOut.publishEvalSummary(request, tstAssertSummary, stopPhrase)).append("<br>");
            fileContentBuilder.append(fileContentTable1Builder).append(LPTestingOutFormat.bodyEnd()).append(LPTestingOutFormat.htmlEnd());

            out.println(fileContentBuilder.toString());            
            LPTestingOutFormat.createLogFile(tstOut.getFilePathName(), fileContentBuilder.toString());
            tstAssertSummary=null; 
        }
        catch(IOException error){
            tstAssertSummary=null; 
            String exceptionMessage = error.getMessage();     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null);                    
        } finally {
            // release database resources
            try {
                ProcedureRequestSession.getInstanceForActions(request, response, Boolean.TRUE).killIt();
                Rdbms.closeRdbms();   
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
