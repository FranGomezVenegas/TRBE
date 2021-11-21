/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import databases.Rdbms;
import databases.TblsReqs;
import databases.TblsTesting;
import databases.Token;
import functionaljavaa.businessrules.BusinessRules;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import static functionaljavaa.testingscripts.LPTestingOutFormat.TESTING_FILES_FIELD_SEPARATOR;
import static functionaljavaa.testingscripts.LPTestingOutFormat.rowAddFields;
import functionaljavaa.testingscripts.LPTestingParams;
import functionaljavaa.testingscripts.LPTestingParams.TestingServletsConfig;
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
import static lbplanet.utilities.LPPlatform.trapMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
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
            LPFrontEnd.servletReturnResponseError(request, response, "Argument scriptId not found in the call", null, language);                              
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
                LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
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
                LPFrontEnd.servletReturnResponseError(request, response, 
                    "Error", null, procReqInstance.getLanguage());              
                return;
            }
            String sessionLang=procReqInstance.getLanguage();
            String errMsg=procReqInstance.getErrorMessage();
            if (procReqInstance.getHasErrors()){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, errMsg, null, sessionLang);              
                return;
            }
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   

            Token token = new Token(finalToken);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, "Argument scriptId not found in the call", null, sessionLang);                              
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
                fileContentBuilder=procedureRepositoryMirrors(procInstanceName, scriptId);
                if (fileContentBuilder.length()>0){
                    out.println(fileContentBuilder.toString());
                    return;
                }
            }

            scriptTblInfo = Rdbms.getRecordFieldsByFilter(repositoryName, TblsTesting.Script.TBL.getName(), 
                    new String[]{TblsTesting.Script.FLD_SCRIPT_ID.getName()}, new Object[]{scriptId}, 
                    new String[]{TblsTesting.Script.FLD_TESTER_NAME.getName(), TblsTesting.Script.FLD_EVAL_NUM_ARGS.getName(), TblsTesting.Script.FLD_AUDIT_IDS_TO_GET.getName(),
                                    TblsTesting.Script.FLD_GET_DB_ERRORS.getName(), TblsTesting.Script.FLD_GET_MSG_ERRORS.getName()},
                    new String[]{TblsTesting.Script.FLD_SCRIPT_ID.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptTblInfo[0][0].toString())){
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
            if (procInstanceName!=null)
                request.setAttribute(LPTestingParams.SCHEMA_PREFIX, procInstanceName);
//            request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, "eyJ1c2VyREIiOiJsYWJwbGFuZXQiLCJlU2lnbiI6ImhvbGEiLCJ1c2VyREJQYXNzd29yZCI6Imxhc2xlY2h1Z2FzIiwidXNlcl9wcm9jZWR1cmVzIjoiW2VtLWRlbW8tYSwgcHJvY2Vzcy11cywgcHJvY2Vzcy1ldSwgZ2Vub21hLTFdIiwidHlwIjoiSldUIiwiYXBwU2Vzc2lvbklkIjoiMjk4NiIsImFwcFNlc3Npb25TdGFydGVkRGF0ZSI6IlR1ZSBNYXIgMTcgMDI6Mzg6MTkgQ0VUIDIwMjAiLCJ1c2VyUm9sZSI6ImNvb3JkaW5hdG9yIiwiYWxnIjoiSFMyNTYiLCJpbnRlcm5hbFVzZXJJRCI6IjEifQ.eyJpc3MiOiJMYWJQTEFORVRkZXN0cmFuZ2lzSW5UaGVOaWdodCJ9.xiT6CxNcoFKAiE2moGhMOsxFwYjeyugdvVISjUUFv0Y");         
            TestingServletsConfig endPoints=null;
            try{
            endPoints = TestingServletsConfig.valueOf(testerName);
            }catch(Exception e){
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
/*                Object[][] scriptStepsTblInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.TESTING.getName(), TblsTesting.ScriptSteps.TBL.getName(), 
                        new String[]{TblsTesting.Script.FLD_SCRIPT_ID.getName(), TblsTesting.Script.FLD_ACTIVE.getName()}, new Object[]{scriptId, true}, 
                        new String[]{TblsTesting.ScriptSteps.FLD_STEP_ID.getName(), TblsTesting.ScriptSteps.FLD_ARGUMENT_01.getName()},
                        new String[]{TblsTesting.ScriptSteps.FLD_STEP_ID.getName()});
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
            case DB_SCHEMADATA_ENVMONIT_SAMPLES:
            case DB_SCHEMADATA_INSPECTION_LOT_RM:
                Object[][] scriptStepsTblInfo = Rdbms.getRecordFieldsByFilter(repositoryName, TblsTesting.ScriptSteps.TBL.getName(), 
                        new String[]{TblsTesting.Script.FLD_SCRIPT_ID.getName(), TblsTesting.Script.FLD_ACTIVE.getName()}, new Object[]{scriptId, true}, 
                        new String[]{TblsTesting.ScriptSteps.FLD_STEP_ID.getName(), TblsTesting.ScriptSteps.FLD_ARGUMENT_01.getName()},
                        new String[]{TblsTesting.ScriptSteps.FLD_STEP_ID.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptStepsTblInfo[0][0].toString())){
                    String msgStr=" Not found ANY active step for the script "+scriptId.toString();
                    Logger.getLogger(msgStr); 
                    out.println(msgStr);
                    return;
                }    
                if (procInstanceName!=null){
                    BusinessRules bi=new BusinessRules(procInstanceName, null);
                    BusinessRules biTesting=new BusinessRules(procInstanceName, scriptId);
                    procReqInstance.setBusinessRulesTesting(biTesting);
                    LPTestingOutFormat.cleanLastRun(procInstanceName, scriptId);
                    LPTestingOutFormat.getIdsBefore(procInstanceName, scriptId, scriptTblInfo[0]);
                    
                    String userProceduresList=token.getUserProcedures();
                    userProceduresList=userProceduresList.replace("[", "");
                    userProceduresList=userProceduresList.replace("]", "");        
                    if (!LPArray.valueInArray(userProceduresList.split(", "), procInstanceName)){
                        out.println(Arrays.toString(trapMessage(LAB_FALSE, LPPlatform.LpPlatformErrorTrapping.USER_NOTASSIGNED_TOPROCEDURE.getErrorCode(), 
                            new String[]{token.getUserName(), procInstanceName, userProceduresList})));
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
        finally{
            String scriptIdStr=request.getParameter("scriptId");
            String procInstanceName=request.getParameter("procInstanceName");
            if (scriptTblInfo.length==0 || scriptIdStr==null) return;
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
        }
    }

    private StringBuilder procedureRepositoryMirrors(String procInstanceName, Integer scriptId){
        StringBuilder fileContentBuilder = new StringBuilder(0);        
        String[][] schemasToCheck=new String[][]{{GlobalVariables.Schemas.DATA.getName(), GlobalVariables.Schemas.DATA_TESTING.getName()}, 
            {GlobalVariables.Schemas.DATA_AUDIT.getName(), GlobalVariables.Schemas.DATA_AUDIT_TESTING.getName()}, 
            {GlobalVariables.Schemas.PROCEDURE.getName(), GlobalVariables.Schemas.PROCEDURE_TESTING.getName()},
            {GlobalVariables.Schemas.PROCEDURE_AUDIT.getName(), GlobalVariables.Schemas.PROCEDURE_AUDIT_TESTING.getName()}};

        Object[][] allMismatches=null;
        Object[] mirrorCheckDiagn =null;
        for (String[] curSchToCheck:schemasToCheck){
            Object[][] tablesToCheckQry=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureModuleTablesAndFields.TBL.getName(), 
                    new String[]{TblsReqs.ProcedureModuleTablesAndFields.FLD_PROCEDURE_NAME.getName(), TblsReqs.ProcedureModuleTablesAndFields.FLD_SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTablesAndFields.FLD_ACTIVE.getName()},
                    new Object[]{procInstanceName, curSchToCheck[0], true}, 
                    new String[]{TblsReqs.ProcedureModuleTablesAndFields.FLD_TABLE_NAME.getName()});
            Object[] tablesToCheck=new String[]{"sample"};
            tablesToCheck=LPArray.getColumnFromArray2D(tablesToCheckQry, 0);

//        if (schemaName.contains(GlobalVariables.Schemas.PROCEDURE.getName())){
//            if (!LPArray.valueInArray(ProcedureDefinitionToInstance.ProcedureSchema_TablesWithNoTestingClone, tableName)) 


            mirrorCheckDiagn = Rdbms.dbSchemaAndTestingSchemaTablesAndFieldsIsMirror(procInstanceName, curSchToCheck[0], curSchToCheck[1], tablesToCheck);
            Object[][] mismatchesArr=(Object[][]) mirrorCheckDiagn[0];
            if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(mismatchesArr[0][0].toString()))
                if (allMismatches==null || (allMismatches[0].length==mismatchesArr[0].length)){
                    allMismatches=LPArray.joinTwo2DArrays(allMismatches, new Object[][]{{"schema family",curSchToCheck[0].toString(), ""}});
                    allMismatches=LPArray.joinTwo2DArrays(allMismatches, mismatchesArr);
                }
        }
        if (allMismatches!=null && allMismatches.length>0){
            fileContentBuilder.append(procInstanceName+" has mirror mismatches, "+allMismatches.length+", further detail below:");    

            StringBuilder htmlStyleHdr = new StringBuilder(0);
            htmlStyleHdr.append(LPTestingOutFormat.getHtmlStyleHeader(this.getServletName(), "", scriptId, procInstanceName));
            fileContentBuilder.append(htmlStyleHdr);

            //out.println(fileContentBuilder.toString());        

            StringBuilder fileContentTable1Builder = new StringBuilder(0);
            fileContentTable1Builder.append(LPTestingOutFormat.createTableWithHeader(LPArray.convertArrayToString((String[]) mirrorCheckDiagn[1], TESTING_FILES_FIELD_SEPARATOR, ""), 0));
            for (Object[] curRow:allMismatches){
                fileContentTable1Builder.append(LPTestingOutFormat.rowStart()).append(rowAddFields(curRow));
                fileContentTable1Builder.append(LPTestingOutFormat.rowEnd());
            }

            fileContentTable1Builder.append(LPTestingOutFormat.tableEnd());
            fileContentBuilder.append(fileContentTable1Builder).append(LPTestingOutFormat.bodyEnd()).append(LPTestingOutFormat.htmlEnd());
            return fileContentBuilder;                
        }        
        return fileContentBuilder; 
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
