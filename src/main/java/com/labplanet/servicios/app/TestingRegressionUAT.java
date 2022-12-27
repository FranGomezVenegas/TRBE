/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsTesting;
import databases.features.Token;
import functionaljavaa.businessrules.BusinessRules;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import static functionaljavaa.testingscripts.LPTestingOutFormat.rowAddFields;
import functionaljavaa.testingscripts.LPTestingParams;
import functionaljavaa.testingscripts.LPTestingParams.TestingServletsConfig;
import functionaljavaa.testingscripts.TestingBusinessRulesVisited;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import static lbplanet.utilities.LPPlatform.LAB_FALSE;
import static lbplanet.utilities.LPPlatform.LAB_TRUE;
import lbplanet.utilities.LPPlatform.LpPlatformBusinessRules;
import lbplanet.utilities.LPPlatform.LpPlatformErrorTrapping;
import lbplanet.utilities.LPPlatform.LpPlatformSuccess;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import static trazit.session.ProcedureRequestSession.isTheProcActionEnabled;
/**
 *
 * @author User
 */
public class TestingRegressionUAT extends HttpServlet {
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {        
        ProcedureRequestSession procReqInstance = null;
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);        
        StringBuilder fileContentBuilder = new StringBuilder(0);        
        String language = LPFrontEnd.setLanguage(request); 

        Integer scriptId=Integer.valueOf(LPNulls.replaceNull(request.getParameter("scriptId")));
        if (scriptId==null){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, "Argument scriptId not found in the call", null, language, null);
            return;
        }

            String saveDirectory="D:\\LP\\"; //TESTING_FILES_PATH;
            Object[][] scriptTblInfo=new Object[0][0];            
        try (PrintWriter out = response.getWriter()) {   
            String actionName=request.getParameter("actionName");
            if ("GETTESTERSLIST".equalsIgnoreCase(actionName)){
                procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, true);        
            if (procReqInstance.getHasErrors()){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
                return;
            }
                TestingServletsConfig[] endPoints = TestingServletsConfig.values();
                JSONArray jArr=new JSONArray();

                for (TestingServletsConfig curTstr: endPoints){
                    JSONObject jObj=new JSONObject();
                    jObj.put("name", curTstr.name());
                    jObj.put("servletUrl", curTstr.getServletUrl());
                    jObj.put("testerFileName", curTstr.getTesterFileName());
                    jObj.put("numTables", curTstr.getNumTables());
                    jObj.put("tablesHeaders", curTstr.getTablesHeaders());
                    jArr.add(jObj);
                }
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                return;
            }
            procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, true);
            if (procReqInstance==null){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, 
                    "Error", null, procReqInstance.getLanguage(), null);              
                return;
            }
            String sessionLang=procReqInstance.getLanguage();
            String errMsg=procReqInstance.getErrorMessage();
            if (procReqInstance.getHasErrors()){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, errMsg, null, sessionLang, null);
                return;
            }
            
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   

            Token token = new Token(finalToken);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, "Argument scriptId not found in the call", null, sessionLang, null);
                return;
            }                
            String repositoryName=LPPlatform.buildSchemaName(GlobalVariables.Schemas.APP_TESTING.getName(), "");
            String procInstanceName=request.getParameter("procInstanceName");
            if (procInstanceName==null){
/*                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, "Argument procInstanceName not found in the call", null, sessionLang);                              
                return;*/
            }else{
                repositoryName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
                fileContentBuilder=procedureRepositoryMirrorsTable(procInstanceName, scriptId);
                if (fileContentBuilder.length()>0){
                    procReqInstance.killIt();
                    out.println("Mirror stopped this testing");
                    out.println(fileContentBuilder.toString());
                    return;
                }
            }
            String[] fldsToRetrieve=new String[]{TblsTesting.Script.TESTER_NAME.getName(), TblsTesting.Script.EVAL_NUM_ARGS.getName(), TblsTesting.Script.AUDIT_IDS_TO_GET.getName(),
                TblsTesting.Script.GET_DB_ERRORS.getName(), TblsTesting.Script.GET_MSG_ERRORS.getName(), TblsTesting.Script.SAVE_EXEC_EVID_ON_SUCCESS.getName()};
            scriptTblInfo = Rdbms.getRecordFieldsByFilter(repositoryName, TblsTesting.TablesTesting.SCRIPT.getTableName(), 
                    new String[]{TblsTesting.Script.SCRIPT_ID.getName()}, new Object[]{scriptId}, 
                    fldsToRetrieve, new String[]{TblsTesting.Script.SCRIPT_ID.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptTblInfo[0][0].toString())){
                procReqInstance.killIt();                
                String msgStr=" Script "+scriptId.toString()+" Not found in procedure "+procInstanceName;
                Logger.getLogger(msgStr); 
                out.println(msgStr);
                return;
            }        
            String testerName = scriptTblInfo[0][0].toString();
            Integer numEvalArgs = 0;
            if (scriptTblInfo[0][1]!=null && scriptTblInfo[0][1].toString().length()>0) numEvalArgs=Integer.valueOf(scriptTblInfo[0][1].toString());

            request.setAttribute(LPTestingParams.UPLOAD_FILE_PARAM_FILE_PATH, saveDirectory+"\\");
            request.setAttribute(LPTestingParams.TESTING_SOURCE, "DB");
            request.setAttribute(LPTestingParams.NUM_EVAL_ARGS, numEvalArgs);
            request.setAttribute(LPTestingParams.SCRIPT_ID, scriptId);
            Integer fldPosic=LPArray.valuePosicInArray(fldsToRetrieve, TblsTesting.Script.SAVE_EXEC_EVID_ON_SUCCESS.getName());            
            if (fldPosic>-1)
                request.setAttribute(LPTestingParams.SCRIPT_EXECUTION_EVIDENCE_SAVE, scriptTblInfo[0][fldPosic]);            
            
            if (procInstanceName!=null)
                request.setAttribute(LPTestingParams.SCHEMA_PREFIX, procInstanceName);
//            request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, "eyJ1c2VyREIiOiJsYWJwbGFuZXQiLCJlU2lnbiI6ImhvbGEiLCJ1c2VyREJQYXNzd29yZCI6Imxhc2xlY2h1Z2FzIiwidXNlcl9wcm9jZWR1cmVzIjoiW2VtLWRlbW8tYSwgcHJvY2Vzcy11cywgcHJvY2Vzcy1ldSwgZ2Vub21hLTFdIiwidHlwIjoiSldUIiwiYXBwU2Vzc2lvbklkIjoiMjk4NiIsImFwcFNlc3Npb25TdGFydGVkRGF0ZSI6IlR1ZSBNYXIgMTcgMDI6Mzg6MTkgQ0VUIDIwMjAiLCJ1c2VyUm9sZSI6ImNvb3JkaW5hdG9yIiwiYWxnIjoiSFMyNTYiLCJpbnRlcm5hbFVzZXJJRCI6IjEifQ.eyJpc3MiOiJMYWJQTEFORVRkZXN0cmFuZ2lzSW5UaGVOaWdodCJ9.xiT6CxNcoFKAiE2moGhMOsxFwYjeyugdvVISjUUFv0Y");         
            TestingServletsConfig endPoints=null;
            try{
            endPoints = TestingServletsConfig.valueOf(testerName);
            }catch(Exception e){
                procReqInstance.killIt();
                String msgStr="Tester name ("+LPNulls.replaceNull(testerName)+") not recognized. The script cannot be started";
                Logger.getLogger(msgStr); 
                out.println(msgStr);
                return;
            }
            // The first endpoints block are regression testing and requires to check that the actions are enable for the process instance that it applies.
            //      This code below the cases should be considered as the checker
            // The dispatcher for both is exactly the same, there is not one for regression and another for unit testing.
            switch (endPoints){
            case DB_PLATFORM_INSTRUMENTS:
/*                Object[][] scriptStepsTblInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.TESTING.getName(), TblsTesting.TablesTesting.SCRIPT_STEPS.getTableName(), 
                        new String[]{TblsTesting.Script.SCRIPT_ID.getName(), TblsTesting.Script.ACTIVE.getName()}, new Object[]{scriptId, true}, 
                        new String[]{TblsTesting.ScriptSteps.STEP_ID.getName(), TblsTesting.ScriptSteps.ARGUMENT_01.getName()},
                        new String[]{TblsTesting.ScriptSteps.STEP_ID.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptStepsTblInfo[0][0].toString())){
                    String msgStr=" Not found ANY active step for the script "+scriptId.toString();
                    Logger.getLogger(msgStr); 
                    out.println(msgStr);
                    return;
                }        
                String[] actionsList=null;
                for (Object[] curStep: scriptStepsTblInfo){
                    Object[] theProcActionEnabled = null;
                    theProcActionEnabled = isTheProcActionEnabled(token, procInstanceName, (String) LPNulls.replaceNull(curStep[1]), bi);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(theProcActionEnabled[0].toString())){
                        actionsList=LPArray.addValueToArray1D(actionsList, "Step "+curStep[0].toString()+", Action:"+curStep[1].toString());
                        Logger.getLogger("In the script "+scriptId+" and step "+LPNulls.replaceNull(curStep[0]).toString()+"the action"+LPNulls.replaceNull(curStep[0]).toString()+" is not enabled"); 
//                        LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.REGRESSIONTESTING_ACTIONSNOTALLOWEDFORPROC.getName(), new Object[]{procInstanceName, scriptId, Arrays.toString(actionsList), this.getServletName()}, language);              
//                        return;
                    }                            
                }
                if (actionsList!=null){
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.REGRESSIONTESTING_ACTIONSNOTALLOWEDFORPROC.getName(), new Object[]{procInstanceName, scriptId, Arrays.toString(actionsList), this.getServletName()}, language);              
                    return;
                }
                
                RequestDispatcher rd = request.getRequestDispatcher(endPoints.getServletUrl());
                rd.forward(request,response);   
                return;                       */
            case DB_SCHEMADATA_GENOMA:
            case DB_SCHEMADATA_SAMPLES:
            case DB_SCHEMADATA_ENVMONIT_SAMPLES:
            case DB_SCHEMADATA_INSPECTION_LOT_RM:
                Object[][] scriptStepsTblInfo = Rdbms.getRecordFieldsByFilter(repositoryName, TblsTesting.TablesTesting.SCRIPT_STEPS.getTableName(), 
                        new String[]{TblsTesting.Script.SCRIPT_ID.getName(), TblsTesting.Script.ACTIVE.getName()}, new Object[]{scriptId, true}, 
                        new String[]{TblsTesting.ScriptSteps.STEP_ID.getName(), TblsTesting.ScriptSteps.ACTION_NAME.getName()},
                        new String[]{TblsTesting.ScriptSteps.STEP_ID.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptStepsTblInfo[0][0].toString())){
                    procReqInstance.killIt();                    
                    String msgStr=" Not found ANY active step for the script "+scriptId.toString();
                    Logger.getLogger(msgStr); 
                    out.println(msgStr);
                    return;
                }    
                if (procInstanceName!=null){
                    BusinessRules bi=new BusinessRules(procInstanceName, null);
                    BusinessRules biTesting=new BusinessRules(procInstanceName, scriptId);
                    procReqInstance.setBusinessRulesTesting(biTesting);
                    try{
                        LPTestingOutFormat.cleanLastRun(procInstanceName, scriptId);
                        LPTestingOutFormat.getIdsBefore(procInstanceName, scriptId, scriptTblInfo[0]);
                    }catch(Exception err){
                    }
                    
                    String userProceduresList=token.getUserProcedures();
                    userProceduresList=userProceduresList.replace("[", "");
                    userProceduresList=userProceduresList.replace("]", "");        
                    if (!LPArray.valueInArray(userProceduresList.split(", "), procInstanceName)){
                        procReqInstance.killIt();                        
                        out.println(Arrays.toString(ApiMessageReturn.trapMessage(LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.USER_NOTASSIGNED_TOPROCEDURE, 
                            new String[]{token.getUserName(), procInstanceName, userProceduresList})));
                        return;                    
                    }                
                    String[] actionsList=null;
                    for (Object[] curStep: scriptStepsTblInfo){
                        Object[] theProcActionEnabled = null;
                        theProcActionEnabled = isTheProcActionEnabled(token, procInstanceName, (String) LPNulls.replaceNull(curStep[1]), bi);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(theProcActionEnabled[0].toString())){
                            actionsList=LPArray.addValueToArray1D(actionsList, "Step "+curStep[0].toString()+", Action:"+curStep[1].toString());
                            Logger.getLogger("In the script "+scriptId+" and step "+LPNulls.replaceNull(curStep[0]).toString()+"the action"+LPNulls.replaceNull(curStep[1]).toString()+" is not enabled"); 
    //                        LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.REGRESSIONTESTING_ACTIONSNOTALLOWEDFORPROC.getName(), new Object[]{procInstanceName, scriptId, Arrays.toString(actionsList), this.getServletName()}, language);              
    //                        return;
                        }else{
                            TestingBusinessRulesVisited testingBusinessRulesVisitedObj = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingBusinessRulesVisitedObj();
                            if (testingBusinessRulesVisitedObj!=null)
                            testingBusinessRulesVisitedObj.AddObject(procInstanceName, "procedure", "ND", LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName()+LPNulls.replaceNull(curStep[1]).toString(), "ND");
                        }                            
                    }
                    if (actionsList!=null){
                        SqlWhere sqlWhere = new SqlWhere();
                        String[] updFldNames=new String[]{TblsTesting.Script.RUN_SUMMARY.getName()};
                        Object[] trapMessage = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.REGRESSIONTESTING_ACTIONSNOTALLOWEDFORPROC.getErrorCode(), new Object[]{procInstanceName, scriptId, Arrays.toString(actionsList)});
                        Object[] updFldValues=new Object[]{trapMessage[trapMessage.length-1]};
                        
                        sqlWhere.addConstraint(TblsTesting.Script.SCRIPT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{scriptId}, "");
                        Rdbms.updateRecordFieldsByFilter(TblsTesting.TablesTesting.SCRIPT,
                        EnumIntTableFields.getTableFieldsFromString(TblsTesting.TablesTesting.SCRIPT, updFldNames), updFldValues, sqlWhere, null);
                        procReqInstance.killIt();  
                        
                        LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.REGRESSIONTESTING_ACTIONSNOTALLOWEDFORPROC.getErrorCode(), new Object[]{procInstanceName, scriptId, Arrays.toString(actionsList), this.getServletName()}, language, null);
                        return;
                    }
                }
                RequestDispatcher rd = request.getRequestDispatcher(endPoints.getServletUrl());
                rd.forward(request,response);   
                return;                       
            case NODB_SCHEMACONFIG_SPECQUAL_RULEFORMAT:
            case NODB_SCHEMACONFIG_SPECQUAL_RESULTCHECK:
            case NODB_SCHEMACONFIG_SPECQUANTI_RULEFORMAT:
            case NODB_SCHEMACONFIG_SPECQUANTI_RESULTCHECK:
            case DB_SCHEMACONFIG_SPEC_RESULTCHECK:
            case NODB_DBACTIONS:
                rd = request.getRequestDispatcher(endPoints.getServletUrl());
                rd.forward(request,response);   
                return;                       
            default:
                String msgStr="Tester name ("+LPNulls.replaceNull(testerName)+") not recognized. The tester cannot be completed";
                Logger.getLogger(msgStr); 
                out.println(msgStr);
            }
        }
        catch(Exception e){
        }
        finally{
            procReqInstance.killIt();
            String scriptIdStr=request.getParameter("scriptId");
            String procInstanceName=request.getParameter("procInstanceName");
            //moved to a method
        /*    if (scriptTblInfo!=null && scriptIdStr.length()>0){ 
                scriptId=Integer.valueOf(LPNulls.replaceNull(scriptIdStr)); 
                if ( (procReqInstance!=null) && (!LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptTblInfo[0][0].toString())) ){
                    if (scriptTblInfo[0][2]!=null && scriptTblInfo[0][2].toString().length()>0)
                        LPTestingOutFormat.setAuditIndexValues(procInstanceName, scriptId, scriptTblInfo[0][2].toString(), "completed");

                    if (scriptTblInfo[0][3]!=null && Boolean.valueOf(scriptTblInfo[0][3].toString()))
                        LPTestingOutFormat.setDbErrorIndexValues(procInstanceName, scriptId, "completed");

                    if (scriptTblInfo[0][4]!=null && Boolean.valueOf(scriptTblInfo[0][4].toString()))
                        LPTestingOutFormat.setMessagesErrorIndexValues(procInstanceName, scriptId, "completed");
                    procReqInstance.killIt();
                }
            } */
        }
    }
    private StringBuilder procedureRepositoryMirrorsTable(String procInstanceName, Integer scriptId){
        StringBuilder fileContentBuilder = new StringBuilder(0); 
        try{
            Object[] allMismatchesDiagnAll = procedureRepositoryMirrors(procInstanceName);
            Object[] allMismatchesDiagn=(Object[]) allMismatchesDiagnAll[0];
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(allMismatchesDiagn[0].toString()))
                return fileContentBuilder;
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allMismatchesDiagn[0].toString())){
                Object[][] allMismatches= (Object[][])allMismatchesDiagnAll[1];
                fileContentBuilder.append(allMismatchesDiagn[allMismatchesDiagn.length-1]).append("<br>");
                for (int i=1;i<allMismatches.length;i++){
                    for (int iCols=0;iCols<allMismatches[0].length;iCols++){
                        fileContentBuilder.append(allMismatches[0][iCols]).append(":").append(allMismatches[i][iCols]).append("<br>");
                    }
                }
                return fileContentBuilder;
            }
            Object[][] allMismatches= (Object[][])allMismatchesDiagnAll[1];
            if (allMismatches!=null && allMismatches.length>0){
                fileContentBuilder.append(procInstanceName+" has mirror mismatches, "+allMismatches.length+", further detail below:");    

                StringBuilder htmlStyleHdr = new StringBuilder(0);
                htmlStyleHdr.append(LPTestingOutFormat.getHtmlStyleHeader(this.getServletName(), "", scriptId, procInstanceName));
                fileContentBuilder.append(htmlStyleHdr);

                //out.println(fileContentBuilder.toString());        

                StringBuilder fileContentTable1Builder = new StringBuilder(0);
                //fileContentTable1Builder.append(LPTestingOutFormat.createTableWithHeader(LPArray.convertArrayToString((String[]) mirrorCheckDiagn[1], TESTING_FILES_FIELD_SEPARATOR, ""), 0));
                for (Object[] curRow:allMismatches){
                    fileContentTable1Builder.append(LPTestingOutFormat.rowStart()).append(rowAddFields(curRow));
                    fileContentTable1Builder.append(LPTestingOutFormat.rowEnd());
                }

                fileContentTable1Builder.append(LPTestingOutFormat.tableEnd());
                fileContentBuilder.append(fileContentTable1Builder).append(LPTestingOutFormat.bodyEnd()).append(LPTestingOutFormat.htmlEnd());
                return fileContentBuilder;                
            }        
            return fileContentBuilder;         
        }catch(Exception e){
            return fileContentBuilder.append(e.getMessage());
        }
    }
    public static Object[] procedureRepositoryMirrors(String procInstanceName){
        Object[] summaryInfo=new Object[3];
        String[][] schemasToCheck=new String[][]{{GlobalVariables.Schemas.DATA.getName(), GlobalVariables.Schemas.DATA_TESTING.getName()}, 
            {GlobalVariables.Schemas.DATA_AUDIT.getName(), GlobalVariables.Schemas.DATA_AUDIT_TESTING.getName()}, 
            {GlobalVariables.Schemas.PROCEDURE.getName(), GlobalVariables.Schemas.PROCEDURE_TESTING.getName()},
            {GlobalVariables.Schemas.PROCEDURE_AUDIT.getName(), GlobalVariables.Schemas.PROCEDURE_AUDIT_TESTING.getName()}};
        
        Object[][] allMismatches=null;
        Object[] mirrorCheckDiagn =null;
        for (String[] curSchToCheck:schemasToCheck){
            Object[] tablesToCheck=Rdbms.dbSchemaAndTestingSchemaTablesAndFieldsIsMirror(procInstanceName, curSchToCheck[0]);
            mirrorCheckDiagn = Rdbms.dbSchemaAndTestingSchemaTablesAndFieldsIsMirror(procInstanceName, curSchToCheck[0], curSchToCheck[1], tablesToCheck);
            Object[][] mismatchesArr=(Object[][]) mirrorCheckDiagn[0];
            if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(mismatchesArr[0][0].toString())){
                if (allMismatches==null || (allMismatches[0].length==mismatchesArr[0].length)){
                    allMismatches=LPArray.joinTwo2DArrays(allMismatches, new String[][]{{"table_name", "field_name", "counter", "schema"}});
//                    Object[][] schemaInfoArr2D=new Object[mismatchesArr.length][1];
//                    schemaInfoArr2D=LPArray.setColumnValueToArray2D(schemaInfoArr2D, 0, curSchToCheck[0]);
                    allMismatches=LPArray.joinTwo2DArrays(allMismatches, LPArray.addColumnToArray2D(mismatchesArr, curSchToCheck[0]));
                }
            //}else{
            //    summaryInfo[0]=mirrorCheckDiagn;
            //    return LPArray.addValueToArray1D(mirrorCheckDiagn, new Object[][]{{}}); 
            }
        }
        if (allMismatches!=null && allMismatches.length>0){
            Object[] trapMessage = ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.MIRROR_MISMATCHES, null);
            summaryInfo[0]=trapMessage;
            summaryInfo[1]=allMismatches;
            return summaryInfo; //LPArray.addValueToArray1D(trapMessage, allMismatches);
        }
        Object[] trapMessage = ApiMessageReturn.trapMessage(LAB_TRUE, LpPlatformSuccess.ALL_THE_SAME, null);
        summaryInfo[0]=trapMessage;
        //summaryInfo[1]=allMismatches;
        return summaryInfo; //LPArray.addValueToArray1D(trapMessage, allMismatches);        
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        //{
        try {
            if (request==null) return;
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        //{
        try {
            if (request==null) return;
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
