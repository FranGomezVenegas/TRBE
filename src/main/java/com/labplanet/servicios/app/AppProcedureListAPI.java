/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsProcedure;
import trazit.procedureinstance.definition.definition.TblsReqs;
import databases.features.Token;
import functionaljavaa.businessrules.ActionsControl;
import functionaljavaa.businessrules.BusinessRules;
import module.monitoring.logic.ConfigMasterData;
import functionaljavaa.user.UserProfile;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import functionaljavaa.sop.UserSop;
import static functionaljavaa.sop.UserSop.isProcedureSopEnable;
import static functionaljavaa.user.UserProfile.getProcedureUsers;
import static functionaljavaa.user.UserProfile.getProcedureUsersAndRolesList;
import lbplanet.utilities.LPNulls;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.definition.logic.ReqProcedureFrontendMasterData;

/**
 *
 * @author Administrator
 */
public class AppProcedureListAPI extends HttpServlet {

    public enum elementType {
        SIMPLE, ICON_BUTTON, TWOICONS
    }

    private enum iconPosition {
        UP, DOWN
    }
    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN + "|" + GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    public static final String LABEL_ARRAY_PROCEDURES = "procedures";
    public static final String LABEL_ARRAY_PROCEDURE_INSTANCES = "procedure_instances";
    public static final String LABEL_ARRAY_PROC_EVENTS = "definition";
    public static final String LABEL_ARRAY_PROC_EVENTS_ICONS_UP = "icons_up";
    public static final String LABEL_ARRAY_PROC_EVENTS_ICONS_DOWN = "icons_down";
    public static final String LABEL_ARRAY_PROC_EVENTS_ERROR = "definition_returned_error";
    public static final String LABEL_ARRAY_SOPS = "sops";
    public static final String LABEL_ARRAY_SOP_LIST = "sop_list";
    public static final String LABEL_SOPS_PASSED = "sops_passed";
    public static final String LABEL_SOP_TOTAL = "sop_total";
    public static final String LABEL_SOP_NAME = "sop_name";
    public static final String LABEL_ICONS = "icons";
    public static final String LABEL_SOP_CERTIFICATION = "SopCertification";
    public static final String LBL_VAL_SOP_CERTIF_DISABLE = "Disabled";
    public static final String LABEL_SOP_TOTAL_COMPLETED = "sop_total_completed";
    public static final String LABEL_SOP_TOTAL_NOT_COMPLETED = "sop_total_not_completed";
    public static final String LABEL_ARRAY_SOP_LIST_INFO = "sop_list_info";
    public static final String LABEL_SOP_TOTAL_NO_SOPS = "There are no SOPS for this form";
    public static final String LABEL_PROC_SCHEMA = "procInstanceName";
    public static final String FIELD_NAME_SOP = "sop";

    public static final String PROC_FLD_NAME = TblsProcedure.ProcedureInfo.NAME.getName()
            + "|" + TblsProcedure.ProcedureInfo.VERSION.getName() + "|label_en|label_es" + "|" + TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName()
            + "|" + TblsProcedure.ProcedureInfo.PROCEDURE_HASH_CODE.getName();
    public static final String PROC_EVENT_FLD_NAME = "name|lp_frontend_page_name|label_en|label_es|type|mode|esign_required|sop|order_number|position|icon_name";
    public static final String PROC_NEW_EVENT_FLD_NAME = "lp_frontend_page_filter|lp_frontend_page_name|label_en|label_es|type|mode|esign_required|sop|order_number|position|icon_name|icon_name_when_not_certified";
    public static final String PROC_EVENT_ICONS_UP_FLD_NAME = "name|lp_frontend_page_name|label_en|label_es|icon_name|type|mode|esign_required|sop|position";
    public static final String PROC_EVENT_ICONS_DOWN_FLD_NAME = "name|lp_frontend_page_name|label_en|label_es|icon_name|type|mode|esign_required|sop|position";
    public static final Integer SIZE_WHEN_CONSIDERED_MOBILE = 960;

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
        LPFrontEnd.servletReturnSuccess(request, response, procedureListInfo(request, response));
    }

    public static JSONObject procedureListInfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            String language = LPFrontEnd.setLanguage(request);

            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return new JSONObject();
            }
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            if (finalToken == null || finalToken.length() == 0) {
                finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
            }
            Token token = new Token(finalToken);

            Integer sizeValue = SIZE_WHEN_CONSIDERED_MOBILE + 1;
            String sizeValueStr = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SIZE_VALUE);
            if (sizeValueStr != null && sizeValueStr.length() > 0) {
                sizeValue = Integer.valueOf(sizeValueStr);
            }

            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
                return new JSONObject();
            }

            UserProfile usProf = new UserProfile();
            Object[] allUserProcedurePrefix = usProf.getAllUserProcedurePrefix(token.getUserName());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0].toString())) {
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, allUserProcedurePrefix);
                return new JSONObject();
            }
            String[] procFldNameArray = PROC_FLD_NAME.split("\\|");

            JSONArray procedures = new JSONArray();

            for (Object curProc : allUserProcedurePrefix) {
                try {
                    if (Boolean.FALSE.equals(GlobalVariables.PROC_MANAGEMENT_SPECIAL_ROLE.equalsIgnoreCase(curProc.toString()))) {
                        JSONObject procedure = new JSONObject();
                        String schemaNameProcedure = LPPlatform.buildSchemaName(curProc.toString(), GlobalVariables.Schemas.PROCEDURE.getName());

                        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
                            return new JSONObject();
                        }

                        Object[][] procInfo = Rdbms.getRecordFieldsByFilter(schemaNameProcedure, schemaNameProcedure, TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(),
                                new String[]{TblsProcedure.ProcedureInfo.NAME.getName() + WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, null,
                                PROC_FLD_NAME.split("\\|"));
                        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfo[0][0].toString()))) {
                            procedure = LPJson.convertArrayRowToJSONObject(procFldNameArray, procInfo[0]);

                            Object[][] rulesInfo = Rdbms.getRecordFieldsByFilter(curProc.toString(), LPPlatform.buildSchemaName(curProc.toString(), GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(),
                                    new String[]{TblsProcedure.ProcedureBusinessRules.AREA.getName(), TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause()},
                                    new Object[]{UserSop.UserSopBusinessRules.USERSOP_MODE.getAreaName(), UserSop.UserSopBusinessRules.USERSOP_MODE.getTagName()+ "|" +UserSop.UserSopBusinessRules.WINDOWOPENABLE_WHENNOTSOPCERTIFIED.getTagName()},
                                    new String[]{TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName(), TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()});
                            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(rulesInfo[0][0].toString()))) {
                                for (Object[] curRule : rulesInfo) {
                                    procedure.put(curRule[0].toString(), curRule[1].toString());
                                }
                            }
                            procedure.put(LABEL_PROC_SCHEMA, curProc);

                            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
                                return new JSONObject();
                            }

                            procedure.put("new_" + LABEL_ARRAY_PROC_EVENTS, newProcedureDefinition(token, curProc,
                                    Boolean.valueOf(LPNulls.replaceNull(request.getParameter(GlobalAPIsParams.REQUEST_PARAM_IS_TESTING)))));
                            procedure.put(LABEL_ARRAY_PROC_EVENTS_ICONS_UP, procedureIconsUp(token, curProc,
                                    Boolean.valueOf(LPNulls.replaceNull(request.getParameter(GlobalAPIsParams.REQUEST_PARAM_IS_TESTING)))));
                            procedure.put(LABEL_ARRAY_PROC_EVENTS_ICONS_DOWN, procedureIconsDown(token, curProc,
                                    Boolean.valueOf(LPNulls.replaceNull(request.getParameter(GlobalAPIsParams.REQUEST_PARAM_IS_TESTING)))));
                        }
                        procedure.put("actions_with_esign", ActionsControl.procActionsWithESign(curProc.toString()));
                        procedure.put("actions_with_confirm_user", ActionsControl.procActionsWithConfirmUser(curProc.toString()));
                        procedure.put("actions_with_justification_phrase", ActionsControl.procActionsWithJustifReason(curProc.toString()));
                        procedure.put("actions_with_action_confirm", ActionsControl.procActionsWithActionConfirm(curProc.toString()));
                        procedure.put("audit_sign_mode", ActionsControl.auditSignMode(curProc.toString()));
                        String includeProcModelInfo = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_PROC_MODEL_INFO);
                        if (includeProcModelInfo != null && Boolean.valueOf(includeProcModelInfo)) {
                            procedure.put("procModel", procModel(curProc.toString(), sizeValue, token.getUserRole()));
                        }
                        procedure.put("master_data", getMasterData(token, curProc.toString()));
                        procedures.add(procedure);                                            
                    }
                } catch (Exception e) {
                    JSONObject procWithError = new JSONObject();
                    procWithError.put(LABEL_ARRAY_PROCEDURES, "Error in procedure " + curProc.toString() + ". Error: " + e.getMessage());
//                    procedures.put(procWithError);
                    //proceduresList.put(LABEL_ARRAY_PROCEDURES, procedures);
                    //return proceduresList;
                }

            }
            JSONObject proceduresList = new JSONObject();
            proceduresList.put(LABEL_ARRAY_PROCEDURES, procedures);
            return proceduresList;
        } catch (NumberFormatException e) {
            JSONObject proceduresList = new JSONObject();
            proceduresList.put(LABEL_ARRAY_PROCEDURES, e.getMessage());
            return proceduresList;
        }
    }

    public static JsonObject xprocModel(String procInstanceName, Integer sizeValue) {
        try {
            JsonObject jArr = new JsonObject();
            Object[][] ruleValue = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_FE_MODEL.getTableName(),
                    new String[]{TblsReqs.ProcedureFEModel.PROCEDURE_NAME.getName(), SqlStatement.WHERECLAUSE_TYPES.OR.getSqlClause() + " " + TblsReqs.ProcedureFEModel.PROC_INSTANCE_NAME.getName()},
                    new Object[]{procInstanceName, procInstanceName},
                    new String[]{TblsReqs.ProcedureFEModel.MODEL_JSON.getName(), TblsReqs.ProcedureFEModel.MODEL_JSON_MOBILE.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ruleValue[0][0].toString())) {
                return jArr;
            }
            if (sizeValue <= SIZE_WHEN_CONSIDERED_MOBILE && ruleValue[0][1] != null && ruleValue[0][1].toString().length() > 0) {
                return JsonParser.parseString(ruleValue[0][1].toString()).getAsJsonObject();
            }
            return JsonParser.parseString(ruleValue[0][0].toString()).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            return new JsonObject();
        }
    }
    public static JsonObject procModel(String procInstanceName, Integer sizeValue, String roleName) {
        try {
            
            JsonObject jObj = new JsonObject();
            if ("platform-settings".equalsIgnoreCase(procInstanceName)||"app".equalsIgnoreCase(procInstanceName)){
                Object[][] ruleValue = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_FE_MODEL.getTableName(),
                        new String[]{TblsReqs.ProcedureFEModel.PROCEDURE_NAME.getName(), SqlStatement.WHERECLAUSE_TYPES.OR.getSqlClause() + " " + TblsReqs.ProcedureFEModel.PROC_INSTANCE_NAME.getName()},
                        new Object[]{procInstanceName, procInstanceName},
                        new String[]{TblsReqs.ProcedureFEModel.MODEL_JSON.getName(), TblsReqs.ProcedureFEModel.MODEL_JSON_MOBILE.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ruleValue[0][0].toString())) {
                    return jObj;
                }
                if (sizeValue <= SIZE_WHEN_CONSIDERED_MOBILE && ruleValue[0][1] != null && ruleValue[0][1].toString().length() > 0) {
                    return JsonParser.parseString(ruleValue[0][1].toString()).getAsJsonObject();
                }
                return JsonParser.parseString(ruleValue[0][0].toString()).getAsJsonObject();
            }
            Object[][] procInfoModSettings = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(),
                    new String[]{TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName()},
                    new Object[]{procInstanceName},
                    new String[]{TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName(), TblsProcedure.ProcedureInfo.MODULE_SETTINGS.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfoModSettings[0][0].toString())) {
                return jObj;
            }
            jObj.add("ModuleSettings", JsonParser.parseString(procInfoModSettings[0][1].toString()).getAsJsonObject());
            Object[][] procViewModel = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableName(),
                    new String[]{TblsProcedure.ProcedureViews.ROLE_NAME.getName()},
                    new Object[]{roleName},
                    new String[]{TblsProcedure.ProcedureViews.NAME.getName(), TblsProcedure.ProcedureViews.JSON_MODEL.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procViewModel[0][0].toString())) {
                return jObj;
            }
            for (Object[] curRow: procViewModel){
                if (LPNulls.replaceNull(curRow[1]).toString().length()>0)
                    jObj.add(curRow[0].toString(), JsonParser.parseString(curRow[1].toString()).getAsJsonObject());
            }
            return jObj;
        } catch (JsonSyntaxException e) {
            return new JsonObject();
        }
    }

    public static com.google.gson.JsonArray procModelArray(String procInstanceName, Integer sizeValue) {
        try {
            Object[][] ruleValue = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_FE_MODEL.getTableName(),
                    new String[]{TblsReqs.ProcedureFEModel.PROCEDURE_NAME.getName(), SqlStatement.WHERECLAUSE_TYPES.OR.getSqlClause() + " " + TblsReqs.ProcedureFEModel.PROC_INSTANCE_NAME.getName()},
                    new Object[]{procInstanceName, procInstanceName},
                    new String[]{TblsReqs.ProcedureFEModel.MODEL_JSON.getName(), TblsReqs.ProcedureFEModel.MODEL_JSON_MOBILE.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ruleValue[0][0].toString())) {
                return null;
            }
            if (sizeValue <= SIZE_WHEN_CONSIDERED_MOBILE && ruleValue[0][1] != null && ruleValue[0][1].toString().length() > 0) {
                return JsonParser.parseString(ruleValue[0][1].toString()).getAsJsonArray();
            }
            return JsonParser.parseString(ruleValue[0][0].toString()).getAsJsonArray();
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static JSONObject getMasterData(Token token, String procInstanceName) {
        JSONObject jObj = new JSONObject();
        try {
            if (GlobalVariables.PROC_MANAGEMENT_SPECIAL_ROLE.equalsIgnoreCase(procInstanceName)){
                ReqProcedureFrontendMasterData procMngrMasterData= new ReqProcedureFrontendMasterData();
                jObj=procMngrMasterData.getMasterDataJsonObject(procInstanceName);
                return jObj;
            }
                    
            String moduleNameFromProcInstance = token.getModuleNameFromProcInstance(procInstanceName);
            if ("notFound".equalsIgnoreCase(moduleNameFromProcInstance)) {
                return jObj;
            }
            if (GlobalVariables.TrazitModules.MONITORING.name().equalsIgnoreCase(moduleNameFromProcInstance)) {
                BusinessRules bi = new BusinessRules(procInstanceName, null);
                jObj = ConfigMasterData.getMasterData(procInstanceName, bi);
            }else{
                try {
                    GlobalVariables.TrazitModules moduleDefinition = GlobalVariables.TrazitModules.valueOf(moduleNameFromProcInstance);
                    // Create an instance of the class
                    if (moduleDefinition.getModuleMasterDataClass()!=null)
                        jObj=moduleDefinition.getModuleMasterDataClass().getDeclaredConstructor().newInstance().getMasterDataJsonObject(procInstanceName);                        
                } catch (Exception e) {
                    e.printStackTrace();
                    jObj.put(procInstanceName, "no master data logic defined, this procedure has assigned the module "+moduleNameFromProcInstance);
                    // Handle the exception appropriately
                }            
            }        
            JSONArray usArr = new JSONArray();
            if (Boolean.FALSE.equals(GlobalVariables.PROC_MANAGEMENT_SPECIAL_ROLE.equalsIgnoreCase(token.getUserRole()))){
                for (Object[] curRow : getProcedureUsersAndRolesList(procInstanceName, null)) {
                    JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(new String[]{"user_name", TblsProcedure.PersonProfile.ROLE_NAME.getName()}, curRow);
                    usArr.add(convertArrayRowToJSONObject);
                }
            }
            jObj.put("users_and_roles", usArr);

            usArr = new JSONArray();
            if (Boolean.FALSE.equals(GlobalVariables.PROC_MANAGEMENT_SPECIAL_ROLE.equalsIgnoreCase(token.getUserRole()))){
                for (Object curRow : getProcedureUsers(procInstanceName, null)) {
                    JSONObject newRow = new JSONObject();
                    newRow.put("user", curRow);
                    usArr.add(newRow);
                }
            }
            jObj.put("users", usArr);
            return jObj;
        } catch (Exception e) {
            jObj.put("error", e.getMessage());
            return jObj;
        }
    }


    /**
     *
     * @param internalUserID the personName
     * @param curProc procedure Prefix
     * @param procedure procedureInfo (json object)
     * @param procEventJson procedureEvents (json object)
     * @param procEventFldNameArray SOP field names
     * @param procEvent1 not sure
     * @param isForTesting
     * @return the SOPs linked to the procedure Event (to confirm)
     */
    public static JSONObject procEventSops(String internalUserID, String curProc, JSONObject procedure, JSONObject procEventJson, String[] procEventFldNameArray, Object[] procEvent1, Boolean isForTesting) {
        try {
            Object[][] notCompletedUserSOP = null;
            Object[] notCompletedUserSOP1D = null;

            UserSop userSop = new UserSop();

            Object[] procedureSopEnable = isProcedureSopEnable(curProc);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureSopEnable[0].toString())) {
                procedure.put(LABEL_SOP_CERTIFICATION, LBL_VAL_SOP_CERTIF_DISABLE);
            } else {
                notCompletedUserSOP = userSop.getNotCompletedUserSOP(internalUserID, curProc, new String[]{LABEL_SOP_NAME}, isForTesting);
                if (notCompletedUserSOP == null || notCompletedUserSOP.length==0) {
                    procEventJson.put(LABEL_SOPS_PASSED, true);
                    JSONObject procEventSopDetail = new JSONObject();
                    procEventSopDetail.put(LABEL_SOP_TOTAL, null);
                    procEventSopDetail.put(LABEL_SOP_TOTAL_COMPLETED, null);
                    procEventSopDetail.put(LABEL_SOP_TOTAL_NOT_COMPLETED, null);
                    procEventSopDetail.put(LABEL_ARRAY_SOP_LIST, null);
                    return procEventSopDetail;
                }
                notCompletedUserSOP1D = LPArray.array2dTo1d(notCompletedUserSOP);
            }
            JSONObject procEventSopDetail = new JSONObject();
            String procEventSops = null;
            Integer sopFieldposic = LPArray.valuePosicInArray(procEventFldNameArray, FIELD_NAME_SOP);
            if (sopFieldposic > -1) {
                procEventSops = (String) procEvent1[sopFieldposic];
            }

            Boolean userHasNotCompletedSOP = false;
            if ((procEventSops == null) || ("".equals(procEventSops))) {
                procEventJson.put(LABEL_SOPS_PASSED, true);
                if ((procEventSops == null)) {
                    procEventJson.put(LABEL_SOPS_PASSED, true);
                }
                procEventSopDetail.put(LABEL_ARRAY_SOP_LIST, new JSONArray());
                procEventSopDetail.put(LABEL_ARRAY_SOP_LIST_INFO, LABEL_SOP_TOTAL_NO_SOPS);
                procEventSopDetail.put(LABEL_SOP_TOTAL, 0);
                procEventSopDetail.put(LABEL_SOP_TOTAL_COMPLETED, 0);
                procEventSopDetail.put(LABEL_SOP_TOTAL_NOT_COMPLETED, 0);
            } else {
                Object[] procEventSopsArr = procEventSops.split("\\|");
                StringBuilder sopListStrBuilder = new StringBuilder(0);
                Integer sopTotalNotCompleted = 0;
                Integer sopTotalCompleted = 0;
                JSONArray procEventSopSummary = new JSONArray();
                for (Object curProcEvSop : procEventSopsArr) {
                    JSONObject procEventSopDetailJson = new JSONObject();
                    procEventSopDetailJson.put(LABEL_SOP_NAME, curProcEvSop);
                    if (LPArray.valuePosicInArray(notCompletedUserSOP1D, curProcEvSop) == -1) {
                        sopTotalCompleted++;
                        procEventSopDetailJson.put(LABEL_SOP_TOTAL_COMPLETED, true);
                    } else {
                        sopTotalNotCompleted++;
                        sopListStrBuilder.append(curProcEvSop.toString()).append("*NO, ");
                        userHasNotCompletedSOP = true;
                        procEventSopDetailJson.put(LABEL_SOP_TOTAL_COMPLETED, false);
                    }
                    procEventSopSummary.add(procEventSopDetailJson);
                }
                procEventJson.put(LABEL_SOPS_PASSED, !userHasNotCompletedSOP);
                procEventSopDetail.put(LABEL_SOP_TOTAL, procEventSopsArr.length);
                procEventSopDetail.put(LABEL_SOP_TOTAL_COMPLETED, sopTotalCompleted);
                procEventSopDetail.put(LABEL_SOP_TOTAL_NOT_COMPLETED, sopTotalNotCompleted);
                procEventSopDetail.put(LABEL_ARRAY_SOP_LIST, procEventSopSummary);
            }
            return procEventSopDetail;
        } catch (Exception e) {
            JSONObject proceduresList = new JSONObject();
            procEventJson.put(LABEL_SOPS_PASSED, true);
            proceduresList.put(LABEL_ARRAY_PROCEDURES, e.getMessage());
            return proceduresList;
        }
    }

    private static JSONArray procedureIconsDown(Token token, Object curProc, Boolean isForTesting) {
        try {
            String rolName = token.getUserRole();
            String[] procEventFldNameIconsDownArray = PROC_EVENT_ICONS_DOWN_FLD_NAME.split("\\|");
            String schemaNameProcedure = LPPlatform.buildSchemaName(curProc.toString(), GlobalVariables.Schemas.PROCEDURE.getName());
            Object[][] procEventIconsDown = Rdbms.getRecordFieldsByFilter(curProc.toString(), schemaNameProcedure, TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableName(),
                    new String[]{TblsProcedure.ProcedureViews.ROLE_NAME.getName(), TblsProcedure.ProcedureViews.POSITION.getName(), TblsProcedure.ProcedureViews.TYPE.getName()}, new String[]{rolName, iconPosition.DOWN.toString().toLowerCase(), elementType.ICON_BUTTON.toString().toLowerCase().replace("_", "-")},
                    procEventFldNameIconsDownArray, new String[]{TblsProcedure.ProcedureViews.ORDER_NUMBER.getName()});
            JSONObject procedure = new JSONObject();
            JSONArray procEventsIconsDown = new JSONArray();
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventIconsDown[0][0].toString()))) {
                for (Object[] procEvent1 : procEventIconsDown) {
                    JSONObject procEventJson = new JSONObject();
                    procEventJson = LPJson.convertArrayRowToJSONObject(procEventFldNameIconsDownArray, procEvent1);

                    JSONObject procEventSopDetail = procEventSops(token.getPersonName(), curProc.toString(), procedure, procEventJson, procEventFldNameIconsDownArray, procEvent1, isForTesting);

                    procEventJson.put(LABEL_ARRAY_SOPS, procEventSopDetail);
                    procEventsIconsDown.add(procEventJson);
                }
            }
            return procEventsIconsDown;
        } catch (Exception e) {
            JSONArray proceduresList = new JSONArray();
            proceduresList.add("Error:" + e.getMessage());
            return proceduresList;
        }
    }

    private static JSONArray procedureIconsUp(Token token, Object curProc, Boolean isForTesting) {
        try {
            String rolName = token.getUserRole();
            String[] procEventFldNameIconsUpArray = PROC_EVENT_ICONS_UP_FLD_NAME.split("\\|");
            String schemaNameProcedure = LPPlatform.buildSchemaName(curProc.toString(), GlobalVariables.Schemas.PROCEDURE.getName());
            Object[][] procEventIconsUp = Rdbms.getRecordFieldsByFilter(curProc.toString(), schemaNameProcedure, TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableName(),
                    new String[]{TblsProcedure.ProcedureViews.ROLE_NAME.getName(), TblsProcedure.ProcedureViews.POSITION.getName(), TblsProcedure.ProcedureViews.TYPE.getName()}, new String[]{rolName, iconPosition.UP.toString().toLowerCase(), elementType.ICON_BUTTON.toString().toLowerCase().replace("_", "-")},
                    procEventFldNameIconsUpArray, new String[]{TblsProcedure.ProcedureViews.ORDER_NUMBER.getName()});
            JSONObject procedure = new JSONObject();
            JSONArray procEventsIconsUp = new JSONArray();
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventIconsUp[0][0].toString()))) {
                for (Object[] procEvent1 : procEventIconsUp) {
                    JSONObject procEventJson = new JSONObject();
                    procEventJson = LPJson.convertArrayRowToJSONObject(procEventFldNameIconsUpArray, procEvent1);

                    JSONObject procEventSopDetail = procEventSops(token.getPersonName(), curProc.toString(), procedure, procEventJson, procEventFldNameIconsUpArray, procEvent1, isForTesting);

                    procEventJson.put(LABEL_ARRAY_SOPS, procEventSopDetail);
                    procEventsIconsUp.add(procEventJson);
                }
            }
            return procEventsIconsUp;
        } catch (Exception e) {
            JSONArray proceduresList = new JSONArray();
            proceduresList.add("Error:" + e.getMessage());
            return proceduresList;
        }
    }

    public static JSONArray newProcedureDefinition(Token token, Object curProc, Boolean isForTesting) {
        JSONArray procEvents = new JSONArray();
        JSONObject procedure = new JSONObject();
        String rolName = token.getUserRole();
        String[] procEventFldNameArray = PROC_NEW_EVENT_FLD_NAME.split("\\|");
        String schemaNameProcedure = LPPlatform.buildSchemaName(curProc.toString(), GlobalVariables.Schemas.PROCEDURE.getName());
        String[] excludedAttributesForOtherItem = new String[]{TblsProcedure.ProcedureViews.ORDER_NUMBER.getName(),
            TblsProcedure.ProcedureViews.TYPE.getName(), TblsProcedure.ProcedureViews.POSITION.getName()};
        String[] excludedAttributesForParentIconGroupItem = new String[]{TblsProcedure.ProcedureViews.ORDER_NUMBER.getName(),
            TblsProcedure.ProcedureViews.TYPE.getName(), TblsProcedure.ProcedureViews.POSITION.getName(),
            TblsProcedure.ProcedureViews.MODE.getName(), TblsProcedure.ProcedureViews.SOP.getName(), TblsProcedure.ProcedureViews.ESIGN_REQUIRED.getName()};
        String[] excludedAttributesForIconGroupItem = new String[]{TblsProcedure.ProcedureViews.ORDER_NUMBER.getName(),
            TblsProcedure.ProcedureViews.TYPE.getName(), TblsProcedure.ProcedureViews.LP_FRONTEND_PAGE_NAME.getName(),
            TblsProcedure.ProcedureViews.POSITION.getName()
        };
        Object[][] procEvent = Rdbms.getRecordFieldsByFilter(curProc.toString(), schemaNameProcedure, TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableName(),
                new String[]{TblsProcedure.ProcedureViews.ROLE_NAME.getName(), TblsProcedure.ProcedureViews.TYPE.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause()},
                new String[]{rolName, elementType.SIMPLE.toString().toLowerCase().replace("_", "-") + "|" + elementType.TWOICONS.toString().toLowerCase()},
                procEventFldNameArray, new String[]{TblsProcedure.ProcedureViews.ORDER_NUMBER.getName(), TblsProcedure.ProcedureViews.TYPE.getName(), TblsProcedure.ProcedureViews.POSITION.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procEvent[0][0].toString())) {
            JSONObject procEventJson = new JSONObject();
            procEventJson.put("Error on get procedure_events records", procEvent[0][procEvent.length - 1].toString());
            procedure.put(LABEL_ARRAY_PROC_EVENTS_ERROR, procEventJson);
            procedure.put(LABEL_ARRAY_PROC_EVENTS, new JSONArray());
        }

        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procEvent[0][0].toString()))) {
            JSONObject procEventJson = new JSONObject();
            JSONArray childs = new JSONArray();
            procEventFldNameArray = PROC_NEW_EVENT_FLD_NAME.replace(TblsProcedure.ProcedureViews.LP_FRONTEND_PAGE_FILTER.getName(), TblsProcedure.ProcedureViews.NAME.getName()).split("\\|");
            for (Object[] procEvent1 : procEvent) {
                String curProcEventType = procEvent1[LPArray.valuePosicInArray(procEventFldNameArray, TblsProcedure.ProcedureViews.TYPE.getName())].toString();
                if (Boolean.FALSE.equals(curProcEventType.equalsIgnoreCase(elementType.TWOICONS.toString().toLowerCase()))) {
                    if (Boolean.FALSE.equals(childs.isEmpty())) {
                        procEventJson.put(LABEL_ICONS, childs);
                        procEvents.add(procEventJson);
                        procEventJson = new JSONObject();
                    }
                    procEventJson = LPJson.convertArrayRowToJSONObject(procEventFldNameArray, procEvent1, excludedAttributesForOtherItem);
                    JSONObject procEventSopDetail = procEventSops(token.getPersonName(), curProc.toString(), procedure, procEventJson, procEventFldNameArray, procEvent1, isForTesting);
                    procEventJson.put(LABEL_ARRAY_SOPS, procEventSopDetail);
                    childs = new JSONArray();
                    procEvents.add(procEventJson);
                    procEventJson = new JSONObject();
                }
                if (curProcEventType.equalsIgnoreCase(elementType.TWOICONS.toString().toLowerCase())) {
                    String curProcEventPosition = procEvent1[LPArray.valuePosicInArray(procEventFldNameArray, TblsProcedure.ProcedureViews.POSITION.getName())].toString();
                    if ("0".equalsIgnoreCase(curProcEventPosition)) {
                        if (Boolean.FALSE.equals(childs.isEmpty())) {
                            procEventJson.put(LABEL_ICONS, childs);
                            procEvents.add(procEventJson);
                            procEventJson = new JSONObject();
                        }
                        procEventJson = LPJson.convertArrayRowToJSONObject(procEventFldNameArray, procEvent1, excludedAttributesForParentIconGroupItem);
                        JSONObject procEventSopDetail = procEventSops(token.getPersonName(), curProc.toString(), procedure, procEventJson, procEventFldNameArray, procEvent1, isForTesting);
                        procEventJson.put(LABEL_ARRAY_SOPS, procEventSopDetail);
                        childs = new JSONArray();
                    } else {
                        JSONObject procEventJson2 = new JSONObject();
                        procEventJson2 = LPJson.convertArrayRowToJSONObject(procEventFldNameArray, procEvent1, excludedAttributesForIconGroupItem);
                        JSONObject procEventSopDetail2 = new JSONObject();
                        procEventSopDetail2 = procEventSops(token.getPersonName(), curProc.toString(), procedure, procEventJson2, procEventFldNameArray, procEvent1, isForTesting);
                        procEventJson2.put(LABEL_ARRAY_SOPS, procEventSopDetail2);
                        childs.add(procEventJson2);
                        procEventJson2 = new JSONObject();
                    }
                }
            }
            if (Boolean.FALSE.equals(childs.isEmpty())) {
                procEventJson.put(LABEL_ICONS, childs);
            }
            if (Boolean.FALSE.equals(procEventJson.isEmpty())) {
                procEvents.add(procEventJson);
            }
        }
        return procEvents;
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
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
