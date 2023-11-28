/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import module.monitoring.definition.ClassEnvMonSampleFrontend.EnvMonSampleAPIqueriesEndpoints;
import com.labplanet.servicios.moduleenvmonit.EnvMonAPI;
import com.labplanet.servicios.modulesample.SampleAPIParams.SampleAPIqueriesEndpoints;
import databases.Rdbms;
import databases.TblsTesting;
import databases.features.Token;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import static functionaljavaa.testingscripts.LPTestingOutFormat.rowAddFields;
import functionaljavaa.testingscripts.LPTestingParams;
import functionaljavaa.testingscripts.LPTestingParams.TestingServletsConfig;
import java.io.IOException;
import java.io.PrintWriter;
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
import lbplanet.utilities.LPMath;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import static lbplanet.utilities.LPPlatform.LAB_FALSE;
import static lbplanet.utilities.LPPlatform.LAB_TRUE;
import lbplanet.utilities.LPPlatform.LpPlatformErrorTrapping;
import lbplanet.utilities.LPPlatform.LpPlatformSuccess;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import static trazit.globalvariables.GlobalVariables.DEFAULTLANGUAGE;
import trazit.session.ApiMessageReturn;

/**
 *
 * @author User
 */
public class TestingSpecUAT extends HttpServlet {

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
        ProcedureRequestSession procReqInstance = null;
        request = LPHttp.requestPreparation(request);
        response = LPHttp.responsePreparation(response);
        StringBuilder fileContentBuilder = new StringBuilder(0);
        String language = LPFrontEnd.setLanguage(request);

        String scriptIdStr = LPNulls.replaceNull(request.getParameter("scriptId"));
        if (scriptIdStr.length() == 0) {
            LPFrontEnd.servletReturnResponseError(request, response, "Argument scriptId not found in the call", null, language, null);
            return;
        }
        Object[] isScriptNumeric = LPMath.isNumeric(scriptIdStr);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isScriptNumeric[0].toString())) {
            LPFrontEnd.servletReturnResponseError(request, response, "Argument scriptId not numeric", null, language, null);
            return;
        }
        
        Integer scriptId = Integer.valueOf(scriptIdStr);
        String procInstanceName = request.getParameter("procInstanceName");
        String isProcManagementStr = LPNulls.replaceNull(request.getParameter("procManagement"));
        Boolean isProcManagement = Boolean.TRUE.equals(Boolean.valueOf(LPNulls.replaceNull(isProcManagementStr)));
        if (Boolean.TRUE.equals(isProcManagement)) {
            request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME, procInstanceName);
        }
//                        if (Boolean.TRUE.equals(GlobalVariables.PROC_MANAGEMENT_SPECIAL_ROLE.equals(procReqInstance.getProcedureInstance())))
//                            procReqInstance.setProcInstanceName(procInstanceName);
//                        else{

        String saveDirectory = "D:\\LP\\";
        Object[][] scriptTblInfo = new Object[0][0];
        try (PrintWriter out = response.getWriter()) {
            String actionName = request.getParameter("actionName");
            if ("GETTESTERSLIST".equalsIgnoreCase(actionName)) {
                procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, true);
                if (Boolean.TRUE.equals(procReqInstance.getHasErrors())) {
                    procReqInstance.killIt();
                    LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);
                    return;
                }
                TestingServletsConfig[] endPoints = TestingServletsConfig.values();
                JSONArray jArr = new JSONArray();

                for (TestingServletsConfig curTstr : endPoints) {
                    JSONObject jObj = new JSONObject();
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
            if (procReqInstance == null) {
                LPFrontEnd.servletReturnResponseError(request, response,
                        "Error", null, DEFAULTLANGUAGE, null);
                return;
            }
            String sessionLang = procReqInstance.getLanguage();
            String errMsg = procReqInstance.getErrorMessage();
            if (Boolean.TRUE.equals(procReqInstance.getHasErrors())) {
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, errMsg, null, sessionLang, null);
                return;
            }

            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);

            Token token = new Token(finalToken);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())) {
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, "Argument scriptId not found in the call", null, sessionLang, null);
                return;
            }
            String repositoryName = LPPlatform.buildSchemaName(GlobalVariables.Schemas.APP_TESTING.getName(), "");
            procInstanceName = request.getParameter("procInstanceName");
            if (procInstanceName != null) {
                repositoryName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
                fileContentBuilder = procedureRepositoryMirrorsTable(procInstanceName, scriptId);
                if (fileContentBuilder.length() > 0) {
                    procReqInstance.killIt();
                    out.println("Mirror stopped this testing");
                    out.println(fileContentBuilder.toString());
                    return;
                }
            }
            String[] fldsToRetrieve = new String[]{TblsTesting.SpecScript.TESTER_NAME.getName(), TblsTesting.SpecScript.EVAL_NUM_ARGS.getName(), TblsTesting.SpecScript.AUDIT_IDS_TO_GET.getName(),
                TblsTesting.SpecScript.GET_DB_ERRORS.getName(), TblsTesting.SpecScript.GET_MSG_ERRORS.getName(), TblsTesting.SpecScript.SAVE_EXEC_EVID_ON_SUCCESS.getName()};
            scriptTblInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, repositoryName, TblsTesting.TablesTesting.SPEC_SCRIPT.getTableName(),
                    new String[]{TblsTesting.SpecScript.SCRIPT_ID.getName()}, new Object[]{scriptId},
                    fldsToRetrieve, new String[]{TblsTesting.SpecScript.SCRIPT_ID.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptTblInfo[0][0].toString())) {
                procReqInstance.killIt();
                String msgStr = " Script " + scriptId.toString() + " Not found in procedure " + procInstanceName;
                Logger.getLogger(msgStr);
                out.println(msgStr);
                return;
            }
            String testerName = "DB_SCHEMACONFIG_SPEC_RESULTCHECK";
            Integer numEvalArgs = 0;
            if (scriptTblInfo[0][1] != null && scriptTblInfo[0][1].toString().length() > 0) {
                numEvalArgs = Integer.valueOf(scriptTblInfo[0][1].toString());
            }

            request.setAttribute(LPTestingParams.UPLOAD_FILE_PARAM_FILE_PATH, saveDirectory + "\\");
            request.setAttribute(LPTestingParams.TESTING_SOURCE, "DB");
            request.setAttribute(LPTestingParams.NUM_EVAL_ARGS, numEvalArgs);
            request.setAttribute(LPTestingParams.SCRIPT_ID, scriptId);
            Integer fldPosic = LPArray.valuePosicInArray(fldsToRetrieve, TblsTesting.SpecScript.SAVE_EXEC_EVID_ON_SUCCESS.getName());
            if (fldPosic > -1) {
                request.setAttribute(LPTestingParams.SCRIPT_EXECUTION_EVIDENCE_SAVE, scriptTblInfo[0][fldPosic]);
            }

            if (procInstanceName != null) {
                request.setAttribute(LPTestingParams.SCHEMA_PREFIX, procInstanceName);
            }
            TestingServletsConfig endPoints = null;
            endPoints = TestingServletsConfig.valueOf(testerName);
            // The first endpoints block are regression testing and requires to check that the actions are enable for the process instance that it applies.
            //      This code below the cases should be considered as the checker
            // The dispatcher for both is exactly the same, there is not one for regression and another for unit testing.
            RequestDispatcher rd = request.getRequestDispatcher(endPoints.getServletUrl());
            rd.forward(request, response);
                    return;
        } catch (Exception e) {
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), null, language, null);
        } finally {
            if (procReqInstance != null) {
                procReqInstance.killIt();
            }
        }
    }

    private StringBuilder procedureRepositoryMirrorsTable(String procInstanceName, Integer scriptId) {
        StringBuilder fileContentBuilder = new StringBuilder(0);
        try {
            Object[] allMismatchesDiagnAll = procedureRepositoryMirrors(procInstanceName);
            Object[] allMismatchesDiagn = (Object[]) allMismatchesDiagnAll[0];
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(allMismatchesDiagn[0].toString())) {
                return fileContentBuilder;
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allMismatchesDiagn[0].toString())) {
                Object[][] allMismatches = (Object[][]) allMismatchesDiagnAll[1];
                fileContentBuilder.append(allMismatchesDiagn[allMismatchesDiagn.length - 1]).append("<br>");
                for (int i = 1; i < allMismatches.length; i++) {
                    for (int iCols = 0; iCols < allMismatches[0].length; iCols++) {
                        fileContentBuilder.append(allMismatches[0][iCols]).append(":").append(allMismatches[i][iCols]).append("<br>");
                    }
                }
                return fileContentBuilder;
            }
            Object[][] allMismatches = (Object[][]) allMismatchesDiagnAll[1];
            if (allMismatches != null && allMismatches.length > 0) {
                fileContentBuilder.append(procInstanceName).append(" has mirror mismatches, ").append(allMismatches.length).append(", further detail below:");

                StringBuilder htmlStyleHdr = new StringBuilder(0);
                htmlStyleHdr.append(LPTestingOutFormat.getHtmlStyleHeader(this.getServletName(), "", scriptId, procInstanceName));
                fileContentBuilder.append(htmlStyleHdr);

                StringBuilder fileContentTable1Builder = new StringBuilder(0);
                for (Object[] curRow : allMismatches) {
                    fileContentTable1Builder.append(LPTestingOutFormat.ROW_START).append(rowAddFields(curRow));
                    fileContentTable1Builder.append(LPTestingOutFormat.ROW_END);
                }

                fileContentTable1Builder.append(LPTestingOutFormat.TABLE_END);
                fileContentBuilder.append(fileContentTable1Builder).append(LPTestingOutFormat.BODY_END).append(LPTestingOutFormat.HTML_END);
                return fileContentBuilder;
            }
            return fileContentBuilder;
        } catch (Exception e) {
            return fileContentBuilder.append(e.getMessage());
        }
    }

    public static Object[] procedureRepositoryMirrors(String procInstanceName) {
        Object[] summaryInfo = new Object[3];
        String[][] schemasToCheck = new String[][]{{GlobalVariables.Schemas.DATA.getName(), GlobalVariables.Schemas.DATA_TESTING.getName()},
        {GlobalVariables.Schemas.DATA_AUDIT.getName(), GlobalVariables.Schemas.DATA_AUDIT_TESTING.getName()},
        {GlobalVariables.Schemas.PROCEDURE.getName(), GlobalVariables.Schemas.PROCEDURE_TESTING.getName()},
        {GlobalVariables.Schemas.PROCEDURE_AUDIT.getName(), GlobalVariables.Schemas.PROCEDURE_AUDIT_TESTING.getName()}};

        Object[][] allMismatches = null;
        Object[] mirrorCheckDiagn = null;
        for (String[] curSchToCheck : schemasToCheck) {
            Object[] tablesToCheck = Rdbms.dbSchemaAndTestingSchemaTablesAndFieldsIsMirror(procInstanceName, curSchToCheck[0]);
            mirrorCheckDiagn = Rdbms.dbSchemaAndTestingSchemaTablesAndFieldsIsMirror(procInstanceName, curSchToCheck[0], curSchToCheck[1], tablesToCheck);
            Object[][] mismatchesArr = (Object[][]) mirrorCheckDiagn[0];
            if ((Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(mismatchesArr[0][0].toString())))
                    && (allMismatches == null || (allMismatches[0].length == mismatchesArr[0].length))) {
                allMismatches = LPArray.joinTwo2DArrays(allMismatches, new String[][]{{"table_name", GlobalAPIsParams.LBL_FIELD_NAME, "counter", "schema"}});
                allMismatches = LPArray.joinTwo2DArrays(allMismatches, LPArray.addColumnToArray2D(mismatchesArr, curSchToCheck[0]));
            }
        }
        if (allMismatches != null && allMismatches.length > 0) {
            Object[] trapMessage = ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.MIRROR_MISMATCHES, null);
            summaryInfo[0] = trapMessage;
            summaryInfo[1] = allMismatches;
            return summaryInfo; //LPArray.addValueToArray1D(trapMessage, allMismatches);
        }
        Object[] trapMessage = ApiMessageReturn.trapMessage(LAB_TRUE, LpPlatformSuccess.ALL_THE_SAME, null);
        summaryInfo[0] = trapMessage;
        //summaryInfo[1]=allMismatches;
        return summaryInfo; //LPArray.addValueToArray1D(trapMessage, allMismatches);        
    }

    public static Boolean actionIsOneQuery(String actionName) {
        Boolean found = false;
        if (Boolean.FALSE.equals(found)) {
            for (EnumIntEndpoints curEnvMonQ : EnvMonAPI.EnvMonQueriesAPIEndpoints.values()) {
                if (actionName.equalsIgnoreCase(curEnvMonQ.getName())) {
                    return true;
                }
            }
        }
        if (Boolean.FALSE.equals(found)) {
            for (EnumIntEndpoints curEnvMonQ : EnvMonSampleAPIqueriesEndpoints.values()) {
                if (actionName.equalsIgnoreCase(curEnvMonQ.getName())) {
                    return true;
                }
            }
        }
        if (Boolean.FALSE.equals(found)) {
            for (EnumIntEndpoints curEnvMonQ : SampleAPIqueriesEndpoints.values()) {
                if (actionName.equalsIgnoreCase(curEnvMonQ.getName())) {
                    return true;
                }
            }
        }

        return found;
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
            if (request == null) {
                return;
            }
            processRequest(request, response);
        } catch (ServletException | IOException ex) {
            Logger.getLogger(TestingSpecUAT.class.getName()).log(Level.SEVERE, null, ex);
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
            if (request == null) {
                return;
            }
            processRequest(request, response);
        } catch (ServletException | IOException ex) {
            Logger.getLogger(TestingSpecUAT.class.getName()).log(Level.SEVERE, null, ex);
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
