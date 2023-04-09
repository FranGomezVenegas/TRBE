/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import static trazit.session.ProcedureRequestSession.MANDATPRMS_MAIN_SERVLET_PROCEDURE;
import databases.Rdbms;
import databases.TblsTesting;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class TestingAPIActions extends HttpServlet {

    public enum TestingAPIActionsEndpoints implements EnumIntEndpoints {
        SCRIPT_SAVE_POINT("SCRIPT_SAVE_POINT", "scriptSaved_success",
                new LPAPIArguments[]{new LPAPIArguments("scriptId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                    new LPAPIArguments("comment", LPAPIArguments.ArgumentType.STRING.toString(), false, 7)
                }, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null);

        private TestingAPIActionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name = name;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.outputObjectTypes = outputObjectTypes;
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);
        }

        public HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex) {
            HashMap<HttpServletRequest, Object[]> hm = new HashMap<>();
            Object[] argValues = new Object[0];
            for (LPAPIArguments curArg : this.arguments) {
                argValues = LPArray.addValueToArray1D(argValues, curArg.getName() + ":" + getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }
            hm.put(request, argValues);
            return hm;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getSuccessMessageCode() {
            return this.successMessageCode;
        }

        @Override
        public JsonArray getOutputObjectTypes() {
            return outputObjectTypes;
        }

        @Override
        public String getApiUrl() {
            return ApiUrls.TESTING_ACTIONS.getUrl();
        }

        /**
         * @return the arguments
         */
        @Override
        public LPAPIArguments[] getArguments() {
            return arguments;
        }
        private final String name;
        private final String successMessageCode;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;

        @Override
        public String getDeveloperComment() {
            return this.devComment;
        }

        @Override
        public String getDeveloperCommentTag() {
            return this.devCommentTag;
        }
        private final String devComment;
        private final String devCommentTag;
    }

    public enum TestingAPIErrorTrapping implements EnumIntMessages {
        SCRIPT_NOT_FOUND("scriptNotFound", "The instrument <*1*> is not found in procedure <*2*>", "El instrumento <*1*> no se ha encontrado para el proceso <*2*>"),;

        private TestingAPIErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
            this.errorCode = errCode;
            this.defaultTextWhenNotInPropertiesFileEn = defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs = defaultTextEs;
        }

        @Override
        public String getErrorCode() {
            return this.errorCode;
        }

        @Override
        public String getDefaultTextEn() {
            return this.defaultTextWhenNotInPropertiesFileEn;
        }

        @Override
        public String getDefaultTextEs() {
            return this.defaultTextWhenNotInPropertiesFileEs;
        }

        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request = LPHttp.requestPreparation(request);
        response = LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false, true);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())) {
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);
            return;
        }
        String actionName = procReqInstance.getActionName();
        String language = procReqInstance.getLanguage();

        try (PrintWriter out = response.getWriter()) {
            TestingAPIActionsEndpoints endPoint = null;
            Object[] actionDiagnoses = null;
            try {
                endPoint = TestingAPIActionsEndpoints.valueOf(actionName.toUpperCase());
            } catch (Exception e) {
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }
            String[] argList = new String[]{};
            LPAPIArguments[] arguments = endPoint.getArguments();
            for (LPAPIArguments curArg : arguments) {
                argList = LPArray.addValueToArray1D(argList, curArg.getName());
            }
            argList = LPArray.addValueToArray1D(argList, MANDATPRMS_MAIN_SERVLET_PROCEDURE.split("\\|"));
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            switch (endPoint) {
                case SCRIPT_SAVE_POINT:
                    scriptExecutionEvidenceSave((Integer) argValues[0], LPNulls.replaceNull(argValues[1]).toString());
                    break;
            }
            if (actionDiagnoses != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses[0].toString())) {
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, actionDiagnoses);
            } else {
                RelatedObjects rObj = RelatedObjects.getInstanceForActions();
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, new Object[]{argValues[0]}, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
            }
        } catch (Exception e) {

            procReqInstance.killIt();
            String[] errObject = new String[]{e.getMessage()};
            LPFrontEnd.responseError(errObject);
        } finally {
            // release database resources
            try {
                procReqInstance.killIt();

            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void scriptExecutionEvidenceSave(Integer scriptId, String comment) {
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, false, true);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())) {
            procReqInstance.killIt();
            //LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        String[] scriptFldToRetrieve = getAllFieldNames(TblsTesting.TablesTesting.SCRIPT.getTableFields());
        //String[] scriptFldToRetrieve=new String[]{TblsTesting.Script.SCRIPT_ID.getName(), TblsTesting.Script.DATE_CREATION.getName()};
        Object[][] scriptInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT.getTableName(),
                new String[]{TblsTesting.Script.SCRIPT_ID.getName()},
                new Object[]{scriptId},
                scriptFldToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptInfo[0][0].toString())) {
            ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TestingAPIErrorTrapping.SCRIPT_NOT_FOUND, new Object[]{scriptId});
            return;
        }
        JSONObject jObj = LPJson.convertArrayRowToJSONObject(scriptFldToRetrieve, scriptInfo[0]);
        Object[][] scriptStepsInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT_STEPS.getTableName(),
                new String[]{TblsTesting.ScriptSteps.SCRIPT_ID.getName()},
                new Object[]{scriptId},
                getAllFieldNames(TblsTesting.TablesTesting.SCRIPT_STEPS.getTableFields()));
        JSONArray scriptStepsJArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptStepsInfo[0][0].toString()))) {
            for (Object[] curStep : scriptStepsInfo) {
                scriptStepsJArr.add(LPJson.convertArrayRowToJSONObject(scriptFldToRetrieve, curStep));
            }
        }
        jObj.put("steps", scriptStepsJArr);
        Object[][] scriptBusRulesInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT_BUS_RULES.getTableName(),
                new String[]{TblsTesting.ScriptBusinessRules.SCRIPT_ID.getName()},
                new Object[]{scriptId},
                getAllFieldNames(TblsTesting.TablesTesting.SCRIPT_BUS_RULES.getTableFields()));
        JSONArray scriptBusRulesJArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptBusRulesInfo[0][0].toString()))) {
            for (Object[] curBusRules : scriptBusRulesInfo) {
                scriptBusRulesJArr.add(LPJson.convertArrayRowToJSONObject(scriptFldToRetrieve, curBusRules));
            }
        }
        jObj.put("business_rules", scriptBusRulesJArr);
        String[] updFldName = new String[]{TblsTesting.ScriptSavePoint.SCRIPT_ID.getName(), TblsTesting.ScriptSavePoint.SAVED_DATE.getName(),
            TblsTesting.ScriptSavePoint.CONTENT.getName()};
        Object[] updFldValue = new Object[]{scriptId, LPDate.getCurrentTimeStamp(), jObj};
        if (comment != null && comment.length() > 0) {
            updFldName = LPArray.addValueToArray1D(updFldName, TblsTesting.ScriptSavePoint.COMMENT.getName());
            updFldValue = LPArray.addValueToArray1D(updFldValue, comment);
        }
        updFldName = LPArray.addValueToArray1D(updFldName, TblsTesting.ScriptSavePoint.DATE_EXECUTION.getName());
        updFldValue = LPArray.addValueToArray1D(updFldValue, LPDate.getCurrentTimeStamp());

        String[] scriptFldsForRecord = new String[]{TblsTesting.ScriptSavePoint.DATE_CREATION.getName(),
            TblsTesting.ScriptSavePoint.PURPOSE.getName(), TblsTesting.ScriptSavePoint.TESTER_NAME.getName(),
            TblsTesting.ScriptSavePoint.TIME_STARTED.getName(), TblsTesting.ScriptSavePoint.TIME_COMPLETED.getName(),
            TblsTesting.ScriptSavePoint.TIME_CONSUME.getName(), TblsTesting.ScriptSavePoint.RUN_SUMMARY.getName()};
        for (String curFld : scriptFldsForRecord) {
            Integer fldPosicInArray = LPArray.valuePosicInArray(scriptFldToRetrieve, curFld);
            if (fldPosicInArray > -1) {
                updFldName = LPArray.addValueToArray1D(updFldName, curFld);
                updFldValue = LPArray.addValueToArray1D(updFldValue, scriptInfo[0][fldPosicInArray]);
            }
        }
        // Rdbms.insertRecord(TblsTesting.TablesTesting.SCRIPT_SAVE_POINT, updFldName, updFldValue, null);
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (ServletException | IOException e) {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (ServletException | IOException e) {
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
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
