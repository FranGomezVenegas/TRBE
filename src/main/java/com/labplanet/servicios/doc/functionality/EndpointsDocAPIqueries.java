/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.doc.functionality;

import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsTrazitDocTrazit;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class EndpointsDocAPIqueries extends HttpServlet {

    public enum EndpointsDocAPIqueriesEndpoints implements EnumIntEndpoints {
        GET_DOC_ENDPOINTS("GET_DOC_ENDPOINTS", "", new LPAPIArguments[]{
            new LPAPIArguments("apiName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments("endpointName", LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
            new LPAPIArguments("groupedByAPI", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 8)},
                EndPointsToRequirements.endpointWithNoOutputObjects,
                null, null);

        private EndpointsDocAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name = name;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.outputObjectTypes = outputObjectTypes;
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);
        }
        @Override        public String getName() {            return this.name;        }
        @Override        public String getSuccessMessageCode() {            return this.successMessageCode;        }
        @Override        public JsonArray getOutputObjectTypes() {            return outputObjectTypes;        }
        @Override        public LPAPIArguments[] getArguments() {            return arguments;        }
        @Override        public String getApiUrl() {            return ApiUrls.DOC_ENDPOINTS_QUERIES.getUrl();        }
        private final String name;
        private final String successMessageCode;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
        @Override        public String getDeveloperComment() {            return this.devComment;        }
        @Override        public String getDeveloperCommentTag() {            return this.devCommentTag;        }
        private final String devComment;
        private final String devCommentTag;
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
        request = LPHttp.requestPreparation(request);
        response = LPHttp.responsePreparation(response);
        String[] endpointDeclarationAllFieldNames = EnumIntTableFields.getAllFieldNames(TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION.getTableFields());
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForDocumentation(request, response);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())) {
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);
            return;
        }
        String actionName = procReqInstance.getActionName();
        String language = procReqInstance.getLanguage();

        try (PrintWriter out = response.getWriter()) {
            EndpointsDocAPIqueriesEndpoints endPoint = null;
            try {
                endPoint = EndpointsDocAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
            } catch (Exception e) {
                //procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }

            Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }

            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());

            String apiName = argValues[0].toString();
            String endpointName = argValues[1].toString();
            Boolean groupedByAPI = Boolean.valueOf(LPNulls.replaceNull(argValues[2]).toString());
            String[] whereFldName = new String[]{TblsTrazitDocTrazit.EndpointsDeclaration.API_NAME.getName(), TblsTrazitDocTrazit.EndpointsDeclaration.DISABLED.getName()};
            Object[] whereFldValue = new Object[]{apiName, false};
            if ("ALL".equalsIgnoreCase(apiName)) {
                whereFldName[0] = whereFldName[0] + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause();
                whereFldValue[0] = "ZZZ";
            }
            if (endpointName.length() > 0) {
                whereFldName = LPArray.addValueToArray1D(whereFldName, TblsTrazitDocTrazit.EndpointsDeclaration.ENDPOINT_NAME.getName());
                whereFldValue = LPArray.addValueToArray1D(whereFldValue, endpointName);
            }
            ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
            String dbTrazitModules = prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
            Rdbms.getRdbms().startRdbms(dbTrazitModules);
            Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION.getTableName(),
                    whereFldName, whereFldValue, endpointDeclarationAllFieldNames,
                    new String[]{TblsTrazitDocTrazit.EndpointsDeclaration.API_NAME.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())) {
                out.println(Arrays.toString(reqEndpointInfo[0]));
                return;
            }
            JSONArray jMainArr = new JSONArray();
            JSONArray jApiArr = new JSONArray();
            JSONObject jApiObj = new JSONObject();
            if (Boolean.TRUE.equals(groupedByAPI)) {
                String curApiName = "";
                for (Object[] currEndpoint : reqEndpointInfo) {
                    if (!curApiName.equalsIgnoreCase(LPNulls.replaceNull(currEndpoint[LPArray.valuePosicInArray(endpointDeclarationAllFieldNames, TblsTrazitDocTrazit.EndpointsDeclaration.API_NAME.getName())]).toString())) {
                        curApiName = LPNulls.replaceNull(currEndpoint[LPArray.valuePosicInArray(endpointDeclarationAllFieldNames, TblsTrazitDocTrazit.EndpointsDeclaration.API_NAME.getName())]).toString();
                        if (!jApiArr.isEmpty()) {
                            jApiObj.put("endpoints", jApiArr);
                            jMainArr.add(jApiObj);
                        }
                        jApiArr = new JSONArray();
                        jApiObj = new JSONObject();
                        jApiObj.put("apiName", curApiName);
                    }

                    JSONObject jObj = LPJson.convertArrayRowToJSONObject(endpointDeclarationAllFieldNames,
                            currEndpoint, new String[]{TblsTrazitDocTrazit.EndpointsDeclaration.ARGUMENTS_ARRAY.getName(), TblsTrazitDocTrazit.EndpointsDeclaration.OUTPUT_OBJECT_TYPES.getName()});
                    com.google.gson.JsonArray argArrayToJson = LPJson.convertToJsonArrayStringedObject(
                            currEndpoint[LPArray.valuePosicInArray(endpointDeclarationAllFieldNames, TblsTrazitDocTrazit.EndpointsDeclaration.ARGUMENTS_ARRAY.getName())].toString());
                    jObj.put(TblsTrazitDocTrazit.EndpointsDeclaration.ARGUMENTS_ARRAY.getName(), argArrayToJson);
                    com.google.gson.JsonArray argOutputToJson = LPJson.convertToJsonArrayStringedObject(
                            currEndpoint[LPArray.valuePosicInArray(endpointDeclarationAllFieldNames, TblsTrazitDocTrazit.EndpointsDeclaration.OUTPUT_OBJECT_TYPES.getName())].toString());
                    jObj.put(TblsTrazitDocTrazit.EndpointsDeclaration.OUTPUT_OBJECT_TYPES.getName(), argOutputToJson);
                    jApiArr.add(jObj);
                }
                if (!jApiArr.isEmpty()) {
                    jApiObj.put("endpoints", jApiArr);
                    jMainArr.add(jApiObj);
                }

            } else {
                for (Object[] currEndpoint : reqEndpointInfo) {
                    JSONObject jObj = LPJson.convertArrayRowToJSONObject(endpointDeclarationAllFieldNames,
                            currEndpoint, new String[]{TblsTrazitDocTrazit.EndpointsDeclaration.ARGUMENTS_ARRAY.getName(), TblsTrazitDocTrazit.EndpointsDeclaration.OUTPUT_OBJECT_TYPES.getName()});
                    com.google.gson.JsonArray argArrayToJson = LPJson.convertToJsonArrayStringedObject(
                            currEndpoint[LPArray.valuePosicInArray(endpointDeclarationAllFieldNames, TblsTrazitDocTrazit.EndpointsDeclaration.ARGUMENTS_ARRAY.getName())].toString());
                    jObj.put(TblsTrazitDocTrazit.EndpointsDeclaration.ARGUMENTS_ARRAY.getName(), argArrayToJson);
                    com.google.gson.JsonArray argOutputToJson = LPJson.convertToJsonArrayStringedObject(
                            currEndpoint[LPArray.valuePosicInArray(endpointDeclarationAllFieldNames, TblsTrazitDocTrazit.EndpointsDeclaration.OUTPUT_OBJECT_TYPES.getName())].toString());
                    jObj.put(TblsTrazitDocTrazit.EndpointsDeclaration.OUTPUT_OBJECT_TYPES.getName(), argOutputToJson);
                    jMainArr.add(jObj);
                }
            }
            Rdbms.closeRdbms();
            LPFrontEnd.servletReturnSuccess(request, response, jMainArr);
        } catch (Exception e) {
            procReqInstance.killIt();
        } finally {
            procReqInstance.killIt();
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
