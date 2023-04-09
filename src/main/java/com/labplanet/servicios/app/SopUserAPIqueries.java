/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import static com.labplanet.servicios.app.AppProcedureListAPI.procedureListInfo;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsCnfg;
import databases.TblsData;
import databases.features.Token;
import static functionaljavaa.certification.FrontendCertifObjsUtilities.certifObjCertifModeOwnUserAction;
import static functionaljavaa.certification.FrontendCertifObjsUtilities.certifObjCertifReviewerPendingSign;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import functionaljavaa.user.UserProfile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import functionaljavaa.sop.UserSop;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import lbplanet.utilities.LPAPIArguments;
import static lbplanet.utilities.LPDatabase.FIELDS_NAMES_PROCEDURE_NAME;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntViewFields;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class SopUserAPIqueries extends HttpServlet {

    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME + "|" + GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN + "|" + GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

    /**
     *
     */
    public static final String ERRORMSG_ERROR_STATUS_CODE = "Error Status Code";

    /**
     *
     */
    public static final String ERRORMSG_MANDATORY_PARAMS_MISSING = "API Error Message: There are mandatory params for this API method not being passed";

    /**
     *
     */
    public static final String FIELDNAME_SOP_ID = "sop_id";

    /**
     *
     */
    public static final String FIELDNAME_SOP_NAME = "sop_name";

    /**
     *
     */
    public static final String JSON_TAG_NAME = "name";

    /**
     *
     */
    public static final String JSON_TAG_LABEL_EN = "label_en";

    /**
     *
     */
    public static final String JSON_TAG_LABEL_ES = "label_es";

    /**
     *
     */
    public static final String JSON_TAG_WINDOWS_URL = "window_url";

    /**
     *
     */
    public static final String JSON_TAG_MODE = "mode";

    /**
     *
     */
    public static final String JSON_TAG_BRANCH_LEVEL = "branch_level";

    /**
     *
     */
    public static final String JSON_TAG_TYPE = "type";

    /**
     *
     */
    public static final String JSON_TAG_BADGE = "badge";

    /**
     *
     */
    public static final String JSON_TAG_DEFINITION = "definition";

    /**
     *
     */
    public static final String JSON_TAG_VERSION = "version";

    /**
     *
     */
    public static final String JSON_TAG_SCHEMA_PREFIX = "procInstanceName";

    /**
     *
     */
    public static final String JSON_TAG_VALUE_TYPE_TREE_LIST = "tree-list";

    /**
     *
     */
    public static final String JSON_TAG_VALUE_BRANCH_LEVEL_LEVEL_1 = "level1";

    /**
     *
     */
    public static final String JSON_TAG_VALUE_WINDOWS_URL_HOME = "Modulo1/home.js";

    public enum SopUserAPIqueriesEndpoints implements EnumIntEndpoints {
        ALL_MY_SOPS("ALL_MY_SOPS", "", new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6)},
                EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        MY_PENDING_SOPS("MY_PENDING_SOPS", "", new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6)},
                EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        PROCEDURE_SOPS("PROCEDURE_SOPS", "", new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6)},
                EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        SOP_TREE_LIST_ELEMENT("SOP_TREE_LIST_ELEMENT", "", new LPAPIArguments[]{},
                EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        ALL_IN_ONE("ALL_IN_ONE", "", new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6)},
                EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),;

        private SopUserAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
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
        public LPAPIArguments[] getArguments() {
            return arguments;
        }

        @Override
        public String getApiUrl() {
            return GlobalVariables.ApiUrls.SOPS_QUERIES.getUrl();
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

        String language = LPFrontEnd.setLanguage(request);

        try (PrintWriter out = response.getWriter()) {

            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }
            String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);

            SopUserAPIqueriesEndpoints endPoint = null;
            try {
                endPoint = SopUserAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
            } catch (Exception e) {
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }
            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
                return;
            }

            switch (endPoint) {
                case ALL_MY_SOPS:
                    LPFrontEnd.servletReturnSuccess(request, response, AllMySops(request, response));
                    return;
                case MY_PENDING_SOPS:
                    LPFrontEnd.servletReturnSuccess(request, response, MyPendingSops(request, response));
                    return;
                case PROCEDURE_SOPS:
                    LPFrontEnd.servletReturnSuccess(request, response, ProceduresSops(request, response));
                    return;
                case SOP_TREE_LIST_ELEMENT:
                    LPFrontEnd.servletReturnSuccess(request, response, SopTreeListElements(request, response));
                    return;
                case ALL_IN_ONE:
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("procedures_list", procedureListInfo(request, response));
                    jsonObj.put("all_my_sops", SopUserAPIqueries.AllMySops(request, response));
                    jsonObj.put("my_pending_sops", SopUserAPIqueries.MyPendingSops(request, response));
                    jsonObj.put("procedures_sops", SopUserAPIqueries.ProceduresSops(request, response));
                    jsonObj.put("sop_tree_list_element", SopUserAPIqueries.SopTreeListElements(request, response));
                    LPFrontEnd.servletReturnSuccess(request, response, jsonObj);
                    return;
                default:
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            }
        } catch (Exception e) {
            String errMessage = e.getMessage();
            String[] errObject = new String[0];
            errObject = LPArray.addValueToArray1D(errObject, ERRORMSG_ERROR_STATUS_CODE + ": " + HttpServletResponse.SC_BAD_REQUEST);
            errObject = LPArray.addValueToArray1D(errObject, "This call raised one unhandled exception. Error:" + errMessage);
            LPFrontEnd.responseError(errObject);

        } finally {
            try {
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static JSONObject AllMyPendingSignSops(HttpServletRequest request, HttpServletResponse response) {
        try {
            String language = LPFrontEnd.setLanguage(request);
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            if (finalToken == null || finalToken.length() == 0) {
                finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
            }
            Token token = new Token(finalToken);

            SopUserAPIqueriesEndpoints endPoint = SopUserAPIqueriesEndpoints.ALL_MY_SOPS;
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
                return new JSONObject();
            }

            UserProfile usProf = new UserProfile();
            String[] allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])) {
                LPFrontEnd.responseError(allUserProcedurePrefix);
                Rdbms.closeRdbms();
                return new JSONObject();
            }
            String[] fieldsToRetrieve = new String[]{FIELDNAME_SOP_ID, FIELDNAME_SOP_NAME};

            String sopFieldsToRetrieve = null;
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString()))) {
                sopFieldsToRetrieve = argValues[0].toString();
            }

            if (sopFieldsToRetrieve != null && sopFieldsToRetrieve.length() > 0) {
                String[] sopFieldsToRetrieveArr = sopFieldsToRetrieve.split("\\|");
                for (String fv : sopFieldsToRetrieveArr) {
                    fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fv);
                }
            } else {
                fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsData.ViewUserAndMetaDataSopView.values());
            }

            Integer procedureFldPosic = LPArray.valuePosicInArray(fieldsToRetrieve, TblsData.ViewUserAndMetaDataSopView.PROCEDURE.getName());
            if (procedureFldPosic == -1) {
                fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, TblsData.ViewUserAndMetaDataSopView.PROCEDURE.getName());
            }
            Object[][] userSops = UserSop.getUserProfileFieldValues(
                    new String[]{TblsData.ViewUserAndMetaDataSopView.REVIEWER_ID.getName(), TblsData.ViewUserAndMetaDataSopView.PENDING_REVIEW.getName()}, new Object[]{token.getPersonName(), true}, fieldsToRetrieve, allUserProcedurePrefix,
                    Boolean.valueOf(LPNulls.replaceNull(request.getParameter(GlobalAPIsParams.REQUEST_PARAM_IS_TESTING))));
            if (userSops == null) {
                return new JSONObject();
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(userSops[0][0]).toString())) {
                LPFrontEnd.responseError(allUserProcedurePrefix);
                Rdbms.closeRdbms();
                return new JSONObject();
            }
            JSONArray mySops = new JSONArray();
            JSONObject mySopsList = new JSONObject();
            JSONArray mySopsListArr = new JSONArray();

            for (Object[] curSop : userSops) {
                JSONObject sop = new JSONObject();
                procedureFldPosic = LPArray.valuePosicInArray(fieldsToRetrieve, TblsData.ViewUserAndMetaDataSopView.PROCEDURE.getName());
                if (procedureFldPosic > -1) {
                    curSop[procedureFldPosic] = curSop[procedureFldPosic].toString().replace("-config", "").replace("\"", "");
                }
                sop = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curSop);

                sop.put(GlobalAPIsParams.REQUEST_PARAM_CERTIF_OBJECTS_LEVEL, certifObjCertifReviewerPendingSign(fieldsToRetrieve, curSop));
                mySops.add(sop);
            }
            mySopsList.put("num_objects", mySops.size());
            mySopsList.put("objects", mySops);
            return mySopsList;
            //mySopsListArr.add(mySopsList);        
            //return mySopsListArr;
        } catch (Exception e) {
            JSONObject proceduresList = new JSONObject();
            JSONArray mySops = new JSONArray();
            proceduresList.put("num_objects", 0);
            proceduresList.put("objects", mySops);
            return proceduresList;
        }
    }

    public static JSONArray AllMySops(HttpServletRequest request, HttpServletResponse response) {
        try {
            String language = LPFrontEnd.setLanguage(request);
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            if (finalToken == null || finalToken.length() == 0) {
                finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
            }
            Token token = new Token(finalToken);

            SopUserAPIqueriesEndpoints endPoint = SopUserAPIqueriesEndpoints.ALL_MY_SOPS;
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
                return new JSONArray();
            }

            UserProfile usProf = new UserProfile();
            String[] allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])) {
                LPFrontEnd.responseError(allUserProcedurePrefix);
                Rdbms.closeRdbms();
                return new JSONArray();
            }
            String[] fieldsToRetrieve = new String[]{FIELDNAME_SOP_ID, FIELDNAME_SOP_NAME};

            String sopFieldsToRetrieve = null;
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString()))) {
                sopFieldsToRetrieve = argValues[0].toString();
            }

            if (sopFieldsToRetrieve != null && sopFieldsToRetrieve.length() > 0) {
                String[] sopFieldsToRetrieveArr = sopFieldsToRetrieve.split("\\|");
                for (String fv : sopFieldsToRetrieveArr) {
                    fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fv);
                }
            } else {
                fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsData.ViewUserAndMetaDataSopView.values());
            }

            Integer procedureFldPosic = LPArray.valuePosicInArray(fieldsToRetrieve, TblsData.ViewUserAndMetaDataSopView.PROCEDURE.getName());
            if (procedureFldPosic == -1) {
                fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, TblsData.ViewUserAndMetaDataSopView.PROCEDURE.getName());
            }
            Object[][] userSops = UserSop.getUserProfileFieldValues(
                    new String[]{TblsData.ViewUserAndMetaDataSopView.USER_ID.getName()}, new Object[]{token.getPersonName()}, fieldsToRetrieve, allUserProcedurePrefix,
                    Boolean.valueOf(LPNulls.replaceNull(request.getParameter(GlobalAPIsParams.REQUEST_PARAM_IS_TESTING))));
            if (userSops == null) {
                return new JSONArray();
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(userSops[0][0]).toString())) {
                LPFrontEnd.responseError(allUserProcedurePrefix);
                Rdbms.closeRdbms();
                return new JSONArray();
            }
            JSONArray mySops = new JSONArray();
            JSONObject mySopsList = new JSONObject();
            JSONArray mySopsListArr = new JSONArray();

            for (Object[] curSop : userSops) {
                JSONObject sop = new JSONObject();
                procedureFldPosic = LPArray.valuePosicInArray(fieldsToRetrieve, TblsData.ViewUserAndMetaDataSopView.PROCEDURE.getName());
                if (procedureFldPosic > -1) {
                    curSop[procedureFldPosic] = curSop[procedureFldPosic].toString().replace("-config", "").replace("\"", "");
                }
                sop = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curSop);

                sop.put(GlobalAPIsParams.REQUEST_PARAM_CERTIF_OBJECTS_LEVEL, certifObjCertifModeOwnUserAction(fieldsToRetrieve, curSop));
                mySops.add(sop);
            }
            mySopsList.put("my_sops", mySops);
            mySopsListArr.add(mySopsList);
            return mySopsListArr;
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static JSONArray MyPendingSops(HttpServletRequest request, HttpServletResponse response) {
        try {
            String language = LPFrontEnd.setLanguage(request);
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            if (finalToken == null || finalToken.length() == 0) {
                finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
            }
            Token token = new Token(finalToken);
            if (finalToken == null || finalToken.length() == 0) {
                finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
            }

            SopUserAPIqueriesEndpoints endPoint = SopUserAPIqueriesEndpoints.MY_PENDING_SOPS;
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
                return new JSONArray();
            }

            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
                return new JSONArray();
            }
            UserProfile usProf = new UserProfile();

            usProf = new UserProfile();
            String[] allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])) {
                LPFrontEnd.responseError(allUserProcedurePrefix);
                Rdbms.closeRdbms();
                return new JSONArray();
            }
            String[] fieldsToRetrieve = new String[]{FIELDNAME_SOP_ID, FIELDNAME_SOP_NAME};
            String sopFieldsToRetrieve = argValues[0].toString();
            if (sopFieldsToRetrieve != null && sopFieldsToRetrieve.length() > 0) {
                String[] sopFieldsToRetrieveArr = sopFieldsToRetrieve.split("\\|");
                for (String fv : sopFieldsToRetrieveArr) {
                    fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fv);
                }
            } else {
                fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsData.ViewUserAndMetaDataSopView.values());
            }

            JSONArray myPendingSopsByProc = new JSONArray();
            UserSop userSop = new UserSop();
            for (String currProc : allUserProcedurePrefix) {

                Object[][] userProcSops = userSop.getNotCompletedUserSOP(token.getPersonName(), currProc, fieldsToRetrieve,
                        Boolean.valueOf(LPNulls.replaceNull(request.getParameter(GlobalAPIsParams.REQUEST_PARAM_IS_TESTING))));
                if (userProcSops != null && userProcSops.length > 0) {
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(userProcSops[0]))) {
                        LPFrontEnd.responseError(userProcSops);
                        Rdbms.closeRdbms();
                        return new JSONArray();
                    }
                    JSONArray mySops = new JSONArray();
                    JSONObject mySopsList = new JSONObject();

                    for (Object[] userProcSop : userProcSops) {
                        JSONObject sop = new JSONObject();
                        sop = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, userProcSop);
                        sop.put(GlobalAPIsParams.REQUEST_PARAM_CERTIF_OBJECTS_LEVEL, certifObjCertifModeOwnUserAction(fieldsToRetrieve, userProcSop));
                        mySopsList.put("pending_sops", mySops);
                        mySopsList.put(FIELDS_NAMES_PROCEDURE_NAME, currProc);
                        mySops.add(sop);
                    }
                    myPendingSopsByProc.add(mySopsList);
                }
            }
            return myPendingSopsByProc;
        } catch (Exception e) {
            return new JSONArray();
            
        }
    }

    public static JSONArray ProceduresSops(HttpServletRequest request, HttpServletResponse response) {
        try {
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            if (finalToken == null || finalToken.length() == 0) {
                finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
            }
            Token token = new Token(finalToken);
            if (finalToken == null || finalToken.length() == 0) {
                finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
            }

            SopUserAPIqueriesEndpoints endPoint = SopUserAPIqueriesEndpoints.PROCEDURE_SOPS;
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
                return new JSONArray();
            }

            UserProfile usProf = new UserProfile();
            String[] allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])) {
                LPFrontEnd.responseError(allUserProcedurePrefix);
                Rdbms.closeRdbms();
                return new JSONArray();
            }
            String[] fieldsToRetrieve = new String[]{FIELDNAME_SOP_ID, FIELDNAME_SOP_NAME};
            String sopFieldsToRetrieve = argValues[0].toString();
            if (sopFieldsToRetrieve != null && sopFieldsToRetrieve.length() > 0 && Boolean.FALSE.equals(sopFieldsToRetrieve.toUpperCase().contains("LABPLANET_FALSE"))) {
                for (String fv : EnumIntViewFields.getAllFieldNames(TblsData.ViewUserAndMetaDataSopView.values())) {
                    fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fv);
                }
            }
            JSONArray myPendingSopsByProc = new JSONArray();
            for (String currProc : allUserProcedurePrefix) {
                Object[][] procSops = Rdbms.getRecordFieldsByFilter(currProc + "-config", TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(),
                        new String[]{TblsCnfg.SopMetaData.SOP_ID.getName() + WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, null, fieldsToRetrieve);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(procSops[0]))) {
                    LPFrontEnd.responseError(procSops);
                    Rdbms.closeRdbms();
                    return new JSONArray();
                }
                JSONArray mySops = new JSONArray();
                JSONObject mySopsList = new JSONObject();
                if ((procSops.length > 0)
                        && (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procSops[0][0].toString())))) {
                    for (Object[] procSop : procSops) {
                        JSONObject sop = new JSONObject();
                        sop = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, procSop);
                        sop.put(GlobalAPIsParams.REQUEST_PARAM_CERTIF_OBJECTS_LEVEL, certifObjCertifModeOwnUserAction(fieldsToRetrieve, procSop));
                        mySops.add(sop);
                    }
                }
                mySopsList.put("procedure_sops", mySops);
                mySopsList.put(FIELDS_NAMES_PROCEDURE_NAME, currProc);
                myPendingSopsByProc.add(mySopsList);
            }
            return myPendingSopsByProc;
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static JSONArray SopTreeListElements(HttpServletRequest request, HttpServletResponse response) {
        try {
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            if (finalToken == null || finalToken.length() == 0) {
                finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
            }
            Token token = new Token(finalToken);
            if (finalToken == null || finalToken.length() == 0) {
                finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
            }

            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
                return new JSONArray();
            }

            UserProfile usProf = new UserProfile();
            String[] allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
            if (allUserProcedurePrefix == null) {
                return new JSONArray();
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])) {
                LPFrontEnd.responseError(allUserProcedurePrefix);
                Rdbms.closeRdbms();
                return new JSONArray();
            }
            Integer numPendingSOPs = 0;
            String[] fieldsToRetrieve = new String[]{FIELDNAME_SOP_ID};
            for (String curProc : allUserProcedurePrefix) {
                UserSop userSop = new UserSop();
                Object[][] userProcSops = userSop.getNotCompletedUserSOP(token.getPersonName(), curProc, fieldsToRetrieve,
                        Boolean.valueOf(LPNulls.replaceNull(request.getParameter(GlobalAPIsParams.REQUEST_PARAM_IS_TESTING))));
                if (userProcSops == null) {
                    return new JSONArray();
                }
                if ((userProcSops.length > 0)
                        && (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(userProcSops[0][0].toString())))) {
                    numPendingSOPs = numPendingSOPs + userProcSops.length;
                }
            }
            JSONArray sopOptions = new JSONArray();

            JSONObject sopOption = new JSONObject();
            sopOption.put(JSON_TAG_NAME, "AllMySOPs");
            sopOption.put(JSON_TAG_LABEL_EN, "All my SOPs");
            sopOption.put(JSON_TAG_LABEL_ES, "Todos Mis PNTs");
            sopOption.put(JSON_TAG_WINDOWS_URL, JSON_TAG_VALUE_WINDOWS_URL_HOME);
            sopOption.put(JSON_TAG_MODE, "edit");
            sopOption.put(JSON_TAG_BRANCH_LEVEL, JSON_TAG_VALUE_BRANCH_LEVEL_LEVEL_1);
            sopOption.put(JSON_TAG_TYPE, JSON_TAG_VALUE_TYPE_TREE_LIST);
            sopOptions.add(sopOption);

            sopOption = new JSONObject();
            sopOption.put(JSON_TAG_NAME, "MyPendingSOPs");
            sopOption.put(JSON_TAG_LABEL_EN, "My Pending SOPs");
            sopOption.put(JSON_TAG_LABEL_ES, "Mis PNT Pendientes");
            sopOption.put(JSON_TAG_WINDOWS_URL, JSON_TAG_VALUE_WINDOWS_URL_HOME);
            sopOption.put(JSON_TAG_MODE, "edit");
            sopOption.put(JSON_TAG_BRANCH_LEVEL, JSON_TAG_VALUE_BRANCH_LEVEL_LEVEL_1);
            sopOption.put(JSON_TAG_BADGE, numPendingSOPs);
            sopOption.put(JSON_TAG_TYPE, JSON_TAG_VALUE_TYPE_TREE_LIST);
            sopOptions.add(sopOption);

            sopOption = new JSONObject();
            sopOption.put(JSON_TAG_NAME, "ProcSOPs");
            sopOption.put(JSON_TAG_LABEL_EN, "Procedure SOPs");
            sopOption.put(JSON_TAG_LABEL_ES, "PNTs del proceso");
            sopOption.put(JSON_TAG_WINDOWS_URL, JSON_TAG_VALUE_WINDOWS_URL_HOME);
            sopOption.put(JSON_TAG_MODE, "edit");
            sopOption.put(JSON_TAG_BRANCH_LEVEL, JSON_TAG_VALUE_BRANCH_LEVEL_LEVEL_1);
            sopOption.put(JSON_TAG_TYPE, JSON_TAG_VALUE_TYPE_TREE_LIST);
            sopOptions.add(sopOption);

            JSONObject sopElement = new JSONObject();
            sopElement.put(JSON_TAG_DEFINITION, sopOptions);
            sopElement.put(JSON_TAG_NAME, "SOP");
            sopElement.put(JSON_TAG_VERSION, "1");
            sopElement.put(JSON_TAG_LABEL_EN, "SOPs");
            sopElement.put(JSON_TAG_LABEL_ES, "P.N.T.");
            sopElement.put(JSON_TAG_SCHEMA_PREFIX, "process-us");

            JSONArray arrFinal = new JSONArray();
            arrFinal.add(sopElement);
            return arrFinal;
        } catch (Exception e) {
            return new JSONArray();
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
            Logger.getLogger(e.getMessage());
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
