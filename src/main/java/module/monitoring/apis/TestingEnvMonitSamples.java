/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.apis;

import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.app.TestingRegressionUAT;
import trazit.procedureinstance.definition.definition.ReqProcedureEnums.ProcedureDefinitionAPIActionsEndpoints;
import functionaljavaa.parameter.Parameter;
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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.TrazitModules;
import trazit.session.ActionsServletCommons;
import trazit.session.InternalMessage;
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
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        LPHttp.moduleActionsSingleAPIforTestScripts(request, response, GlobalVariables.TrazitModules.MONITORING, this.getClass().getSimpleName());
    }
            
    protected void processRequest1(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String table1Header = TestingServletsConfig.MONITORING.getTablesHeaders();
        Integer table1NumArgs=13;
        LocalDateTime timeStarted=LPDate.getCurrentTimeStamp();
        JSONArray functionRelatedObjects=new JSONArray();        
        InternalMessage functionDiagnosticObj=null;        
        Integer scriptId=Integer.valueOf(LPNulls.replaceNull(request.getParameter("scriptId")));

        response = LPTestingOutFormat.responsePreparation(response);        
        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();

        String testerFileName=LPTestingParams.TestingServletsConfig.MONITORING.getTesterFileName();                         
        LPTestingOutFormat tstOut=new LPTestingOutFormat(request, LPTestingParams.TestingServletsConfig.MONITORING.name(), testerFileName);
        Map<String, Object> csvHeaderTags=tstOut.getCsvHeaderTags();
        
        StringBuilder fileContentBuilder = new StringBuilder(0);        

        fileContentBuilder.append(tstOut.getHtmlStyleHeader());
        Object[][]  testingContent =tstOut.getTestingContent();
        testingContent=LPArray.addColumnToArray2D(testingContent, new JSONArray());
        
        String stopPhrase=null;
        
        try (PrintWriter out = response.getWriter()) {
            ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
           
            String procInstanceName=instanceForActions.getProcedureInstance();
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
                LPTestingParams.handleAlternativeToken(tstOut, iLines);
                
                tstAssertSummary.increaseTotalTests();                    
                TestingAssert tstAssert = new TestingAssert(testingContent[iLines], numEvaluationArguments, false);                

                Object actionName = LPNulls.replaceNull(testingContent[iLines][5]).toString();
                request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, actionName);
                instanceForActions.setActionNameForTesting(scriptId, iLines, actionName.toString());                
                if (tstOut.getAuditReasonPosic()!=-1)
                    request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE, LPNulls.replaceNull(testingContent[iLines][tstOut.getAuditReasonPosic()]).toString());

                fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                    new Object[]{iLines-numHeaderLines+1, LPNulls.replaceNull(testingContent[iLines][5]).toString()})); //print actionName   
                ActionsServletCommons clss=null;
                if (actionName.toString().equalsIgnoreCase(ProcedureDefinitionAPIActionsEndpoints.SET_PROCEDURE_BUSINESS_RULES.getName())){
                    procInstanceName=LPNulls.replaceNull(testingContent[iLines][6]).toString();
                    fileContentTable1Builder.append(procInstanceName);                    
                    String suffixName=LPNulls.replaceNull(testingContent[iLines][7]).toString();
                    fileContentTable1Builder.append(suffixName);                    
                    String propName=LPNulls.replaceNull(testingContent[iLines][8]).toString();
                    fileContentTable1Builder.append(propName);                    
                    String propValue=LPNulls.replaceNull(testingContent[iLines][9]).toString();
                    fileContentTable1Builder.append(propValue);                    
                    Parameter parm=new Parameter();
                    String diagn=parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                        procInstanceName+"-"+suffixName, propName, propValue);
                    functionRelatedObjects=new JSONArray();                      
                    if (diagn.toUpperCase().contains("CREATED"))
                        functionDiagnosticObj=new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.PROPERTY_CREATED, new Object[]{diagn});
                    else
                        functionDiagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.PROPERTY_NOT_CREATED, new Object[]{diagn});
                    testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                    
                }else{  
                    InternalMessage confirmDialogVerifObj = tstOut.passConfirmDialogValidation(request, tstOut, actionName.toString(), testingContent[iLines]);                    
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(confirmDialogVerifObj.getDiagnostic())){
                        functionRelatedObjects=new JSONArray();
                    }else{                        
                        clss=new ActionsServletCommons(request, TrazitModules.MONITORING.getActionsEndpointPair(), actionName.toString());
                        if (clss.getEndpointFound()){
                            functionRelatedObjects = clss.getRelatedObj().getRelatedObject();                            
                            testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                            fileContentTable1Builder.append(clss.getRowArgsRows());
                        }else{
                            testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                            fileContentTable1Builder.append(clss.getRowArgsRows());                                     
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
                BigDecimal secondsInDateRange = LPDate.secondsInDateRange(timeStartedStep, LPDate.getCurrentTimeStamp(), true);
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(String.valueOf(secondsInDateRange)));
                if (numEvaluationArguments==0){                    
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(clss.getActionClassRun().getDiagnostic())));                     
                }                                
                if (numEvaluationArguments>0){                    
                    Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, clss.getActionClassRun().getDiagnostic(), 4);   
                        
                    Integer stepId=Integer.valueOf(testingContent[iLines][tstOut.getStepIdPosic()].toString());
                    fileContentTable1Builder.append(tstOut.publishEvalStep(request, stepId, clss.getActionClassRun().getDiagnostic(), functionRelatedObjects, tstAssert, timeStartedStep));                    
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate));                        
                    if ( tstOut.getStopSyntaxisUnmatchPosic()>-1 && Boolean.TRUE.equals(Boolean.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStopSyntaxisUnmatchPosic()]).toString())) &&
                            Boolean.FALSE.equals(TestingAssert.EvalCodes.MATCH.toString().equalsIgnoreCase(tstAssert.getEvalSyntaxisDiagnostic())) ){
                        out.println(fileContentBuilder.toString()); 
                        stopPhrase="Interrupted by evaluation not matching in step "+(iLines+1)+" of "+testingContent.length;
                        break;      
                    }
                }
                if (tstOut.getStopSyntaxisFalsePosic()>-1 && Boolean.TRUE.equals(Boolean.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStopSyntaxisFalsePosic()]).toString()))
                    && LPPlatform.LAB_FALSE.equalsIgnoreCase(clss.getDiagnosticObj().getDiagnostic())){
                        out.println(fileContentBuilder.toString()); 
                        stopPhrase="Interrupted by evaluation returning false in step "+(iLines+1)+" of "+testingContent.length;
//                        Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName.toString(), GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT.getTableName(), 
//                                new String[]{TblsTesting.Script.RUN_SUMMARY.getName()}, new Object[]{"Interrupted by evaluation returning false "+(iLines+1)+" of "+testingContent.length}, 
//                                new String[]{TblsTesting.Script.SCRIPT_ID.getName()}, new Object[]{6}); //testingContent[iLines][tstOut.getScriptIdPosic()]});
                    break;
                }
                fileContentTable1Builder.append(LPTestingOutFormat.ROW_END);   
                instanceForActions.auditActionsKill();
            }    
            fileContentTable1Builder.append(LPTestingOutFormat.TABLE_END);
            fileContentTable1Builder.append(LPTestingOutFormat.businessRulesTable());
            fileContentBuilder.append(tstOut.publishEvalSummary(request, tstAssertSummary, stopPhrase, timeStarted)).append("<br>");
            fileContentBuilder.append(fileContentTable1Builder).append(LPTestingOutFormat.BODY_END).append(LPTestingOutFormat.HTML_END);

            out.println(fileContentBuilder.toString());            
            tstAssertSummary=null; 
        }
        catch(IOException error){
            tstAssertSummary=null; 
            String exceptionMessage = error.getMessage();     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);                    
        } finally {    
            // release database resources
            try {
                ProcedureRequestSession.getInstanceForActions(request, response, Boolean.TRUE).killIt();
                   
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }               
    }   
/*
    protected void processRequest2(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String table1Header = TestingServletsConfig.MONITORING.getTablesHeaders();
        Integer table1NumArgs=13;
        LocalDateTime timeStarted=LPDate.getCurrentTimeStamp();
        Object[] functionEvaluation=new Object[0];
        JSONArray functionRelatedObjects=new JSONArray();        
        Integer scriptId=Integer.valueOf(LPNulls.replaceNull(request.getParameter("scriptId")));

        response = LPTestingOutFormat.responsePreparation(response);        
        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();

        String testerFileName=LPTestingParams.TestingServletsConfig.MONITORING.getTesterFileName();                         
        LPTestingOutFormat tstOut=new LPTestingOutFormat(request, LPTestingParams.TestingServletsConfig.MONITORING.name(), testerFileName);
        Map<String, Object> csvHeaderTags=tstOut.getCsvHeaderTags();
        
        StringBuilder fileContentBuilder = new StringBuilder(0);        

        fileContentBuilder.append(tstOut.getHtmlStyleHeader());
        Object[][]  testingContent =tstOut.getTestingContent();
        testingContent=LPArray.addColumnToArray2D(testingContent, new JSONArray());
        
        String stopPhrase=null;
        
        try (PrintWriter out = response.getWriter()) {
            ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
           
            String procInstanceName=instanceForActions.getProcedureInstance();
//            String brName="sampleReviewer_canBeAnyTestingGroupReviewer";
//            String ruleValue=Parameter.getBusinessRuleProcedureFile(procInstanceName, "procedure", brName);
//            out.println("ruleValue using Parameter.getBusinessRuleProcedureFile = "+ruleValue);
//            if (1==1) return;
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
                LPTestingParams.handleAlternativeToken(tstOut, iLines);
                
                tstAssertSummary.increaseTotalTests();                    
                TestingAssert tstAssert = new TestingAssert(testingContent[iLines], numEvaluationArguments, false);                

                Object actionName = LPNulls.replaceNull(testingContent[iLines][5]).toString();
                request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, actionName);
                instanceForActions.setActionNameForTesting(scriptId, iLines, actionName.toString());                
                if (tstOut.getAuditReasonPosic()!=-1)
                    request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE, LPNulls.replaceNull(testingContent[iLines][tstOut.getAuditReasonPosic()]).toString());

                fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                    new Object[]{iLines-numHeaderLines+1, LPNulls.replaceNull(testingContent[iLines][5]).toString()})); //print actionName                                    
                if (actionName.toString().equalsIgnoreCase(ProcedureDefinitionAPIActionsEndpoints.SET_PROCEDURE_BUSINESS_RULES.getName())){
                    procInstanceName=LPNulls.replaceNull(testingContent[iLines][6]).toString();
                    fileContentTable1Builder.append(procInstanceName);                    
                    String suffixName=LPNulls.replaceNull(testingContent[iLines][7]).toString();
                    fileContentTable1Builder.append(suffixName);                    
                    String propName=LPNulls.replaceNull(testingContent[iLines][8]).toString();
                    fileContentTable1Builder.append(propName);                    
                    String propValue=LPNulls.replaceNull(testingContent[iLines][9]).toString();
                    fileContentTable1Builder.append(propValue);                    
                    Parameter parm=new Parameter();
                    String diagn=parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                        procInstanceName+"-"+suffixName, propName, propValue);
                    functionRelatedObjects=new JSONArray();                      
                    if (diagn.toUpperCase().contains("CREATED"))
                        functionEvaluation=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.PROPERTY_CREATED, new Object[]{diagn});
                    else
                        functionEvaluation=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.PROPERTY_NOT_CREATED, new Object[]{diagn});
                    testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                    
                }else{  
                    InternalMessage confirmDialogVerif = tstOut.passConfirmDialogValidation(request, tstOut, actionName.toString(), testingContent[iLines]);
                    functionEvaluation=confirmDialogVerif;
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(confirmDialogVerif.getDiagnostic())){
                        functionRelatedObjects=new JSONArray();
                    }else{                        
                        ClassEnvMonSampleController clssEnvMonSampleController=new ClassEnvMonSampleController(request, actionName.toString(), testingContent, iLines, table1NumArgs, tstOut.getAuditReasonPosic());
                        if (Boolean.TRUE.equals(clssEnvMonSampleController.getFunctionFound())){
                            functionRelatedObjects=clssEnvMonSampleController.getFunctionRelatedObjects();
                            functionEvaluation=(Object[]) clssEnvMonSampleController.getFunctionDiagn();
                            testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                            fileContentTable1Builder.append(clssEnvMonSampleController.getRowArgsRows());
                            clssEnvMonSampleController=null;
                        }else{
                            ClassEnvMonSampleFrontendController clssEnvMonSampleFrontendController=new ClassEnvMonSampleFrontendController(request, response, actionName.toString(), testingContent, iLines, table1NumArgs);
                            if (Boolean.TRUE.equals(clssEnvMonSampleFrontendController.getFunctionFound())){
                                functionRelatedObjects=clssEnvMonSampleFrontendController.getFunctionRelatedObjects();
                                functionEvaluation=(Object[]) clssEnvMonSampleFrontendController.getFunctionDiagn();
                                testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                                fileContentTable1Builder.append(clssEnvMonSampleFrontendController.getRowArgsRows());    
                                clssEnvMonSampleFrontendController=null;
                            }else{
                                ClassEnvMonController clssEnvMonController=new ClassEnvMonController(request, actionName.toString(), testingContent, iLines, table1NumArgs);
                                if (Boolean.TRUE.equals(clssEnvMonController.getFunctionFound())){
                                    functionRelatedObjects=clssEnvMonController.getFunctionRelatedObjects();
                                    functionEvaluation=(Object[]) clssEnvMonController.getFunctionDiagn();
                                    testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                                    fileContentTable1Builder.append(clssEnvMonController.getRowArgsRows());
                                    clssEnvMonController=null;
                                }else{                    
                                    ClassEnvMonIncubatorController clssEnvMonIncubController=new ClassEnvMonIncubatorController(request, actionName.toString(), testingContent, iLines, table1NumArgs);
                                    if (Boolean.TRUE.equals(clssEnvMonIncubController.getFunctionFound())){
                                        functionRelatedObjects=clssEnvMonIncubController.getFunctionRelatedObjects();
                                        functionEvaluation=(Object[]) clssEnvMonIncubController.getFunctionDiagn();
                                        testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                                        fileContentTable1Builder.append(clssEnvMonIncubController.getRowArgsRows());  
                                        clssEnvMonIncubController=null;
                                    }else{                            
                                        ClassEnvMonQueriesController clssEnvMonQueriesController=new ClassEnvMonQueriesController(request, response, actionName.toString(), testingContent, iLines, table1NumArgs);
                                        if (Boolean.TRUE.equals(clssEnvMonQueriesController.getFunctionFound())){
                                            functionRelatedObjects=clssEnvMonQueriesController.getFunctionRelatedObjects();
                                            functionEvaluation=(Object[]) clssEnvMonQueriesController.getFunctionDiagn();
                                            testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                                            fileContentTable1Builder.append(clssEnvMonQueriesController.getRowArgsRows());     
                                            clssEnvMonQueriesController=null;
                                        }else{
                                            ClassSampleController clssSampleController=new ClassSampleController(request, actionName.toString(), testingContent, iLines, table1NumArgs);
                                            if (Boolean.TRUE.equals(clssSampleController.getFunctionFound())){
                                                functionRelatedObjects=clssSampleController.getFunctionRelatedObjects();
                                                functionEvaluation=(Object[]) clssSampleController.getFunctionDiagn();
                                                testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                                                fileContentTable1Builder.append(clssSampleController.getRowArgsRows());  
                                                clssSampleController=null;
                                            }else{
                                                ClassInvestigationController clssInvestigationController=new ClassInvestigationController(request, actionName.toString(), testingContent, iLines, table1NumArgs);
                                                if (Boolean.TRUE.equals(clssInvestigationController.getFunctionFound())){
                                                    functionRelatedObjects=clssInvestigationController.getFunctionRelatedObjects();
                                                    functionEvaluation=(Object[]) clssInvestigationController.getFunctionDiagn();
                                                    testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                                                    fileContentTable1Builder.append(clssInvestigationController.getRowArgsRows()); 
                                                    clssInvestigationController=null;
                                                }else{
                                                    ClassEnvMonProdLotController clsEnvMonProdLot=new ClassEnvMonProdLotController(request, actionName.toString(), testingContent, iLines, table1NumArgs, tstOut.getAuditReasonPosic());
                                                    if (Boolean.TRUE.equals(clsEnvMonProdLot.getFunctionFound())){
                                                        functionRelatedObjects=clsEnvMonProdLot.getFunctionRelatedObjects();
                                                        functionEvaluation=(Object[]) clsEnvMonProdLot.getFunctionDiagn();
                                                        testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                                                        fileContentTable1Builder.append(clsEnvMonProdLot.getRowArgsRows());  
                                                        clsEnvMonProdLot=null;
                                                    }else{
                                                        functionEvaluation=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName});
                                                        testingContent[iLines][testingContent[0].length-1]=functionRelatedObjects;
                                                        fileContentTable1Builder.append(clssSampleController.getRowArgsRows());         
                                                    }
                                                    clsEnvMonProdLot=null;                                            
                                                }
                                                clssInvestigationController=null;
                                            }
                                            clssSampleController=null;
                                        }
                                        clssEnvMonQueriesController=null;
                                    }
                                    clssEnvMonIncubController=null;
                                }
                                clssEnvMonSampleFrontendController=null;
                            }
                            clssEnvMonSampleController=null;
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
                BigDecimal secondsInDateRange = LPDate.secondsInDateRange(timeStartedStep, LPDate.getCurrentTimeStamp(), true);
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(String.valueOf(secondsInDateRange)));
                if (numEvaluationArguments==0){                    
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(functionEvaluation)));                     
                }                                
                if (numEvaluationArguments>0){                    
                    Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, functionEvaluation, 4);   
                        
                    Integer stepId=Integer.valueOf(testingContent[iLines][tstOut.getStepIdPosic()].toString());
                    fileContentTable1Builder.append(tstOut.publishEvalStep(request, stepId, functionEvaluation, functionRelatedObjects, tstAssert, timeStartedStep));                    
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate));                        
                    if ( tstOut.getStopSyntaxisUnmatchPosic()>-1 && Boolean.TRUE.equals(Boolean.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStopSyntaxisUnmatchPosic()]).toString())) &&
                            Boolean.FALSE.equals(TestingAssert.EvalCodes.MATCH.toString().equalsIgnoreCase(tstAssert.getEvalSyntaxisDiagnostic())) ){
                        out.println(fileContentBuilder.toString()); 
                        stopPhrase="Interrupted by evaluation not matching in step "+(iLines+1)+" of "+testingContent.length;
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
                instanceForActions.auditActionsKill();
            }    
            fileContentTable1Builder.append(LPTestingOutFormat.TABLE_END);
            fileContentTable1Builder.append(LPTestingOutFormat.businessRulesTable());
            fileContentBuilder.append(tstOut.publishEvalSummary(request, tstAssertSummary, stopPhrase, timeStarted)).append("<br>");
            fileContentBuilder.append(fileContentTable1Builder).append(LPTestingOutFormat.BODY_END).append(LPTestingOutFormat.HTML_END);

            out.println(fileContentBuilder.toString());            
            tstAssertSummary=null; 
        }
        catch(IOException error){
            tstAssertSummary=null; 
            String exceptionMessage = error.getMessage();     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);                    
        } finally {    
            // release database resources
            try {
                ProcedureRequestSession.getInstanceForActions(request, response, Boolean.TRUE).killIt();
                   
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }               
    }   
*/
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
