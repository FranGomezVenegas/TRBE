/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import com.labplanet.servicios.app.GlobalAPIsParams;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import functionaljavaa.testingscripts.LPTestingParams;
import functionaljavaa.testingscripts.TestingAssert;
import functionaljavaa.testingscripts.TestingAssertSummary;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import org.json.simple.JSONArray;
import trazit.enums.ActionsEndpointPair;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables.TrazitModules;
import trazit.procedureinstance.definition.definition.ReqProcedureEnums;
import trazit.session.ActionsServletCommons;
import static trazit.session.ActionsServletCommons.publishResult;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class LPHttp {

    private LPHttp() {
        throw new IllegalStateException("Utility class");
    }

    /**
     *
     * @param request
     * @return
     */
    public static HttpServletRequest requestPreparation(HttpServletRequest request) {
        try {
            request.setCharacterEncoding(LPPlatform.LAB_ENCODER_UTF8);
            return request;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LPHttp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return request;
    }

    /**
     *
     * @param response
     * @return
     */
    public static HttpServletResponse responsePreparation(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding(LPPlatform.LAB_ENCODER_UTF8);

        response.setHeader("CORS_ORIGIN_ALLOW_ALL", "True");
        response.setHeader("CORS_ALLOW_CREDENTIALS", "true");                 //False
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "True");
        response.setHeader("Set-Cookie", "key=value; HttpOnly; SameSite=strict");
        addSameSiteCookieAttribute(response);
        return response;
    }

    private static void addSameSiteCookieAttribute(HttpServletResponse response) {
        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        boolean firstHeader = true;
        for (String header : headers) { // there can be multiple Set-Cookie attributes
            if (firstHeader) {
                response.setHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=Strict"));
                firstHeader = false;
                continue;
            }
            response.addHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=Strict"));
        }
    }

    /**
     *
     * @param request
     * @param paramNames
     * @return
     */
    public static Object[] areMandatoryParamsInApiRequest(HttpServletRequest request, String[] paramNames) {
        Object[] diagnoses = null;
        StringBuilder paramsNotPresent = new StringBuilder(0);
        if ((paramNames != null) && (paramNames.length > 1 || (paramNames.length == 1 && (!"".equals(paramNames[0]))))) {
            for (String curParam : paramNames) {
                Boolean notPresent = false;
                String curParamValue = request.getParameter(curParam);
                if (curParamValue == null) {
                    curParamValue = LPNulls.replaceNull(request.getAttribute(curParam)).toString();
                }
                if (curParamValue == null) {
                    notPresent = true;
                }
                if ("undefined".equals(curParamValue)) {
                    notPresent = true;
                }
                if ("".equals(curParamValue)) {
                    notPresent = true;
                }
                if (Boolean.TRUE.equals(notPresent)) {
                    paramsNotPresent.append(curParam).append(", ");
                }
            }
        }
        if (paramsNotPresent.length() > 0) {
            diagnoses = LPArray.addValueToArray1D(diagnoses, LPPlatform.LAB_FALSE);
            diagnoses = LPArray.addValueToArray1D(diagnoses, paramsNotPresent);
            return diagnoses;
        } else {
            return new Object[]{LPPlatform.LAB_TRUE};
        }
    }

    /**
     *
     * @param request
     * @param paramNames
     * @return
     */
    public static Object[] areEndPointMandatoryParamsInApiRequest(HttpServletRequest request, LPAPIArguments[] paramNames) {
        Object[] diagnoses = null;
        StringBuilder paramsNotPresent = new StringBuilder(0);
        if ((paramNames != null) && (paramNames.length > 1 || (paramNames.length == 1 && (!"".equals(paramNames[0].toString()))))) {
            for (LPAPIArguments curParam : paramNames) {
                Boolean notPresent = false;
                String curParamValue = request.getParameter(curParam.getName());
                if (curParamValue == null) {
                    notPresent = true;
                }
                if (Boolean.TRUE.equals(curParam.getMandatory())) {
                    if ("undefined".equals(curParamValue)) {
                        notPresent = true;
                    }
                    if ("".equals(curParamValue)) {
                        notPresent = true;
                    }

                    if (Boolean.TRUE.equals(notPresent)) {
                        paramsNotPresent.append(curParam.getName()).append(", ");
                    }
                }
                if (curParam.getSpecialCheck() != null && curParamValue!=null && curParamValue.length() > 0) {
                    String checkerController = LPAPIArgumentsSpecialChecks.checkerController(curParam, curParamValue);
                    if (checkerController != null) {
                        diagnoses = LPArray.addValueToArray1D(diagnoses, LPPlatform.LAB_FALSE);
                        diagnoses = LPArray.addValueToArray1D(diagnoses, checkerController);
                        return diagnoses;
                    }
                }

            }
        }
        if (paramsNotPresent.length() > 0) {
            diagnoses = LPArray.addValueToArray1D(diagnoses, LPPlatform.LAB_FALSE);
            diagnoses = LPArray.addValueToArray1D(diagnoses, paramsNotPresent);
            return diagnoses;
        } else {
            return new Object[]{LPPlatform.LAB_TRUE};
        }
    }

    /**
     *
     */
    public static void sendResponseMissingMandatories() {
        // Not implemented yet
    }
    
    public static String toSnakeCase(String camelCaseStr) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return camelCaseStr.replaceAll(regex, replacement).toLowerCase();
    }
    
    public static void moduleActionsSingleAPI(HttpServletRequest request, HttpServletResponse response, ActionsEndpointPair[] actionEndpointArr, String thisServletName){
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);     
        
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false, false);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            if (procReqInstance.getErrorMessageCodeObj()!=null)
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, procReqInstance.getErrorMessageCodeObj(), procReqInstance.getErrorMessageVariables());                   
            else
                LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), thisServletName}, procReqInstance.getLanguage(), null);                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        EnumIntEndpoints endPoint = null;
        try (PrintWriter out = response.getWriter()) {
            
            ActionsServletCommons clss=new ActionsServletCommons(request, actionEndpointArr, actionName);
            if (clss.getEndpointFound()){
                publishResult(request, response, procReqInstance, clss.getEndpointObj(), 
                    clss.getActionClassRun().getDiagnostic(), clss.getActionClassRun().getDiagnosticObj(), 
                    clss.getActionClassRun().getMessageDynamicData(), 
                    clss.getActionClassRun().getRelatedObj());
            }else{
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, clss.getErrorCodeObj(), clss.getMessageVariables());
                return;                
            }
        }catch(Exception e){  
            Logger.getLogger(thisServletName).log(Level.SEVERE, null, e);
        } finally {
            // release database resources
            try {           
                procReqInstance.killIt();
            } catch (Exception ex) {
                Logger.getLogger(thisServletName).log(Level.SEVERE, null, ex);
            }
        }          
        
    }


    public static void moduleActionsSingleAPIforTestScripts(HttpServletRequest request, HttpServletResponse response, TrazitModules moduleDef, String className)  {
        String table1Header = moduleDef.getTablesHeaders();
        Integer table1NumArgs=13;
        LocalDateTime timeStarted=LPDate.getCurrentTimeStamp();
        JSONArray functionRelatedObjects=new JSONArray();        
        InternalMessage functionDiagnosticObj=null;        
        Integer scriptId=Integer.valueOf(LPNulls.replaceNull(request.getParameter("scriptId")));

        response = LPTestingOutFormat.responsePreparation(response);        
        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();

        String testerFileName=className;                         
        LPTestingOutFormat tstOut=new LPTestingOutFormat(request, className, testerFileName);
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
                if (actionName.toString().equalsIgnoreCase(ReqProcedureEnums.ProcedureDefinitionAPIActionsEndpoints.SET_PROCEDURE_BUSINESS_RULES.getName())){
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
                        clss=new ActionsServletCommons(request, moduleDef.getActionsEndpointPairForTesting(), actionName.toString(), testingContent, iLines, table1NumArgs, tstOut.getAuditReasonPosic());
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
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(clss.getDiagnosticObj().getMessageCodeObj().getErrorCode()));
                }                                
                if (numEvaluationArguments>0){                    
                    Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, clss.getDiagnosticObj(), 4);   
                        
                    Integer stepId=Integer.valueOf(testingContent[iLines][tstOut.getStepIdPosic()].toString());
                    fileContentTable1Builder.append(tstOut.publishEvalStep(request, stepId, clss.getDiagnosticObj(), functionRelatedObjects, tstAssert, timeStartedStep));                    
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
        }               
    }   

}
