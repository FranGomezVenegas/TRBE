package trazit.procedureinstance.definition.apis;

import static com.labplanet.servicios.app.AppProcedureListAPI.LABEL_ARRAY_PROCEDURE_INSTANCES;
import static com.labplanet.servicios.app.AppProcedureListAPI.SIZE_WHEN_CONSIDERED_MOBILE;
import static com.labplanet.servicios.app.AppProcedureListAPI.procModelArray;
import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.app.TestingRegressionUAT;
import module.monitoring.definition.TblsEnvMonitConfig;
import trazit.procedureinstance.definition.definition.ReqProcedureEnums.ReqProcedureDefinitionAPIQueriesEndpoints;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsApp;
import databases.TblsCnfg;
import databases.TblsTesting;
import databases.features.Token;
import static functionaljavaa.requirement.ProcedureDefinitionQueries.*;
import functionaljavaa.user.UserProfile;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import static trazit.queries.QueryUtilities.getTableData;
import trazit.session.ProcedureRequestSession;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import static trazit.procedureinstance.definition.apis.prodDefQueriesViewDetail.getProcedureViews;
import trazit.procedureinstance.definition.definition.TblsReqs;
import trazit.procedureinstance.definition.logic.ClassReqProcedUserAndActionsForQueries;
import trazit.procedureinstance.definition.logic.ClassReqProcedureQueries;
import trazit.procedureinstance.definition.logic.ReqProcDefTestingCoverageSummary;
import static trazit.procedureinstance.definition.logic.ReqProcedureFrontendMasterData.getActiveModulesJSON;
import trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections.ReqSolutionTypes;
import trazit.queries.QueryUtilities;
import trazit.session.InternalMessage;

public class ReqProcedureDefinitionQueries extends HttpServlet {

    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME + "|" + GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN + "|" + GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    public static final String ERRORMSG_ERROR_STATUS_CODE = "Error Status Code";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @SuppressWarnings("unchecked")
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request = LPHttp.requestPreparation(request);
        response = LPHttp.responsePreparation(response);
        String language = LPFrontEnd.setLanguage(request);
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForQueries(request, response, false, true);
        procReqSession.setProcInstanceName(GlobalVariables.PROC_MANAGEMENT_SPECIAL_ROLE);
        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
            procReqSession.killIt();
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }

        String actionName = procReqSession.getActionName();
        String finalToken = procReqSession.getTokenString();

        ReqProcedureDefinitionAPIQueriesEndpoints endPoint = null;
        try {
            endPoint = ReqProcedureDefinitionAPIQueriesEndpoints.valueOf(actionName.toUpperCase());
        } catch (Exception e) {
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
            return;
        }
        try (PrintWriter out = response.getWriter()) {
            String procInstanceName = procReqSession.getProcedureInstance();
            switch (endPoint) {
                case ALL_PROCEDURES_AND_INSTANCE_LIST:
                    String[] fieldsToRetrieveScripts = EnumIntTableFields.getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableFields());
                    Object[][] list = getTableData(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM.getTableName(),
                            "", EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM.getTableFields()),
                            new String[]{TblsEnvMonitConfig.MicroOrganism.NAME.getName() + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{},
                            new String[]{TblsEnvMonitConfig.MicroOrganism.NAME.getName()});
                    JSONArray jArr = new JSONArray();
                    for (Object[] curRec : list) {
                        JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveScripts, curRec);
                        jArr.put(jObj);
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                case ONE_PROCEDURE_DEFINITION:
                case ALL_PROCEDURES_DEFINITION:
                    String curProcInstanceName = request.getParameter("procInstanceName");
                    curProcInstanceName = LPNulls.replaceNull(curProcInstanceName);
                    JSONObject jMainObj = new JSONObject();
                    String mainObjectName = "all_platform_procedures_list";
                    fieldsToRetrieveScripts = getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableFields());
                    String[] wFldName = new String[]{TblsReqs.ProcedureInfo.ACTIVE.getName(), TblsReqs.ProcedureInfo.MODULE_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL.getSqlClause()};
                    Object[] wFldVal = new Object[]{true, "APP"};
                    if ("ONE_PROCEDURE_DEFINITION".equalsIgnoreCase(endPoint.getName())&&curProcInstanceName.length() > 0&& Boolean.FALSE.equals("undefined".equalsIgnoreCase(curProcInstanceName))) {
                        wFldName = LPArray.addValueToArray1D(wFldName, TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName());
                        wFldVal = LPArray.addValueToArray1D(wFldVal, curProcInstanceName);
                    }
                    Object[][] procAndInstanceArr = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(),
                            wFldName, wFldVal, fieldsToRetrieveScripts);
                    JSONArray proceduresList = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procAndInstanceArr[0][0].toString()))){
                        for (Object[] curProc : procAndInstanceArr) {
                            JSONObject curProcObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveScripts, curProc);
                            Integer valuePosicProcNameInArray = LPArray.valuePosicInArray(fieldsToRetrieveScripts, TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName());
                            Integer valuePosicProcVersionInArray = LPArray.valuePosicInArray(fieldsToRetrieveScripts, TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName());
                            Integer valuePosicProcInstanceNameInArray = LPArray.valuePosicInArray(fieldsToRetrieveScripts, TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName());
                            if (valuePosicProcInstanceNameInArray > -1) {                                
                                curProcObj.put("cardData", procInstanceCardDataInRequirements(curProc[valuePosicProcInstanceNameInArray].toString()));
                                curProcObj.put("master_data", procInstanceMasterDataInRequirements(curProc[valuePosicProcInstanceNameInArray].toString()));
                                curProcObj.put("views", procInstanceViewsInRequirements());
                                curProcObj.put("views_design", getProcedureViews(curProc[valuePosicProcNameInArray].toString(), Integer.valueOf(curProc[valuePosicProcVersionInArray].toString()), curProc[valuePosicProcInstanceNameInArray].toString()));
                                curProcObj.put("testing", ReqProcDefTestingCoverageSummary.procInstanceTestingInfo(curProc[valuePosicProcInstanceNameInArray].toString()));
                                curProcObj.put("manuals", procInstanceManualsInRequirements(curProc[valuePosicProcInstanceNameInArray].toString()));
                                curProcObj.put("frontend_testing", procInstanceFrontendTestingInRequirements(curProc[valuePosicProcInstanceNameInArray].toString()));
                                curProcObj.put("definition", procInstanceDefinitionInRequirements(curProc[valuePosicProcInstanceNameInArray].toString()));
                                curProcObj.put("support", procInstanceSupportInRequirements(curProc[valuePosicProcInstanceNameInArray].toString()));
                            }
                            proceduresList.put(curProcObj);
                        }
                    }
                    jMainObj.put(mainObjectName, proceduresList);
                    LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
                    return;
                case ALL_USER_PROCEDURES_DEFINITION:
                    jMainObj = new JSONObject();
                    mainObjectName = "all_user_procedures_list";
                    Token token = new Token(finalToken);
                    UserProfile usProf = new UserProfile();
                    Object[] allUserProcedureInstancePrefix = usProf.getAllUserProcedurePrefix(token.getUserName());
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedureInstancePrefix[0].toString())) {
                        LPFrontEnd.servletReturnSuccess(request, response, new JSONObject());
                        return;
                    }
                    JSONArray procedures = new JSONArray();
                    for (Object curProcInst : allUserProcedureInstancePrefix) {
                        if (Boolean.FALSE.equals(GlobalVariables.PROC_MANAGEMENT_SPECIAL_ROLE.equalsIgnoreCase(curProcInst.toString()))) {
                            JSONObject procInstanceDefinition = procInstanceDefinitionInRequirements(curProcInst.toString());
                            procInstanceDefinition.put("instance_name", curProcInst);
                            procedures.put(procInstanceDefinition);
                        }
                    }
                    JSONObject proceduresListObj = new JSONObject();
                    proceduresListObj.put(LABEL_ARRAY_PROCEDURE_INSTANCES, procedures);
                    jMainObj.put(mainObjectName, proceduresListObj);
                    LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
                    return;
                case ALL_PROCEDURE_DEFINITION:
/*                case ONE_PROCEDURE_DEFINITION:
                    Rdbms.closeRdbms();
                    JSONObject mainRespDef = new JSONObject();
                    JSONObject procDef = procInstanceDefinitionInRequirements(procInstanceName);
                    mainRespDef.put("definition", procDef);
                    LPFrontEnd.servletReturnSuccess(request, response, mainRespDef);
                    return;*/
                case ENABLE_ACTIONS_AND_ROLES:
                    LPFrontEnd.servletReturnSuccess(request, response,
                            getProcBusinessRulesQueriesInfo(procInstanceName, ProcBusinessRulesQueries.PROCEDURE_ACTIONS_AND_ROLES.toString()));
                    return;
                case ALL_PROCEDURE_TESTING_SCRIPT:
                    procInstanceName = argValues[2].toString();
                    fieldsToRetrieveScripts = TblsTesting.getScriptPublicFieldNames(procInstanceName);                    
                    String repositoryName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
                    Object[] schemaExists=Rdbms.dbSchemaExists(repositoryName);
                    jMainObj = new JSONObject();
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(schemaExists[0].toString())){
                        Object[][] scriptsTblInfo = Rdbms.getRecordFieldsByFilter(repositoryName, TblsTesting.TablesTesting.SCRIPT.getTableName(),
                                new String[]{TblsTesting.Script.SCRIPT_ID.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                                new String[]{}, fieldsToRetrieveScripts, new String[]{TblsTesting.Script.SCRIPT_ID.getName()});
                        JSONObject jObj = new JSONObject();
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptsTblInfo[0][0].toString())) {
                            jObj.put("status", "Not found ANY script");
                        } else {
                            JSONArray scriptsList = new JSONArray();
                            Integer scriptIdPosic = LPArray.valuePosicInArray(fieldsToRetrieveScripts, TblsTesting.Script.SCRIPT_ID.getName());
                            for (Object[] curTest : scriptsTblInfo) {
                                JSONObject curTestObj = getScriptWithSteps(Integer.valueOf(curTest[scriptIdPosic].toString()), procInstanceName, fieldsToRetrieveScripts, curTest);
                                scriptsList.put(curTestObj);
                            }
                            jMainObj.put("scripts_list", scriptsList);
                        }
                        if (Boolean.FALSE.equals(jObj.isEmpty())) {
                            jMainObj.put("proc_testing_script_summary", jObj);
                        }
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
                    return;

                case PROC_DEPLOY_TESTING_COVERAGE_SUMMARY:
                    procInstanceName = argValues[2].toString();
                    JSONObject jObj = new JSONObject();
                    InternalMessage actionDiagnosesAll = TestingRegressionUAT.procedureRepositoryMirrors(procInstanceName);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnosesAll.getDiagnostic())) {
                        jObj.put("Error_found", "Not mirrors");
                        jArr = new JSONArray();
                        Object[][] mismatchTables = (Object[][]) actionDiagnosesAll.getNewObjectId();
                        for (int i = 1; i < mismatchTables.length; i++) {
                            jArr.put(LPJson.convertArrayRowToJSONObject(LPArray.convertObjectArrayToStringArray(mismatchTables[0]), mismatchTables[i]));
                        }
                        JSONArray jTblColsArr = new JSONArray();
                        for (Object curCol : mismatchTables[0]) {
                            jTblColsArr.put(curCol.toString());
                        }
                        jObj = new JSONObject();
                        JSONObject jerrDetObj = new JSONObject();
                        jerrDetObj.put("columns", jTblColsArr);
                        jerrDetObj.put("data", jArr);
                        jerrDetObj.put("error_en", "It is required that both data repositories are mirror");
                        jerrDetObj.put("error_es", "Es obligatorio que ambos repositoros de datos sean espejo");
                        jObj.put("not_mirror", jerrDetObj);
                        LPFrontEnd.servletReturnSuccess(request, response, jObj);
                        return;
                    }
                    jMainObj = new JSONObject();
                    mainObjectName = "all_testing_coverage_list";

                    String[] fieldsToGet = EnumIntTableFields.getAllFieldNames(TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableFields());
                    procAndInstanceArr = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getRepositoryName()), TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableName(),
                            new String[]{TblsTesting.ScriptsCoverage.COVERAGE_ID.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{},
                            fieldsToGet, new String[]{TblsTesting.ScriptsCoverage.COVERAGE_ID.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procAndInstanceArr[0][0].toString())) {
                        jMainObj.put(mainObjectName, "no data found");
                    } else {
                        proceduresList = new JSONArray();
                        for (Object[] curProc : procAndInstanceArr) {
                            JSONObject curProcObj = LPJson.convertArrayRowToJSONObject(fieldsToGet, curProc);
                            Integer valuePosicInArray = LPArray.valuePosicInArray(fieldsToGet, TblsTesting.ScriptsCoverage.ENDPOINTS_EXCLUDE_LIST.getName());
                            if (valuePosicInArray > -1) {
                                curProcObj.replace(TblsTesting.ScriptsCoverage.ENDPOINTS_EXCLUDE_LIST.getName(),
                                        LPJson.convertToJSONArray(curProc[valuePosicInArray].toString().split("\\|")));
                            }
                            valuePosicInArray = LPArray.valuePosicInArray(fieldsToGet, TblsTesting.ScriptsCoverage.SCRIPT_IDS_LIST.getName());
                            if (valuePosicInArray > -1) {
                                curProcObj.replace(TblsTesting.ScriptsCoverage.SCRIPT_IDS_LIST.getName(),
                                        LPJson.convertToJSONArray(curProc[valuePosicInArray].toString().split("\\|")));
                                JSONArray scriptDetail = new JSONArray();
                                for (String curId : curProc[valuePosicInArray].toString().split("\\|")) {
                                    JSONObject curTestObj = getScriptWithSteps(Integer.valueOf(curId), procInstanceName, null, null);
                                    scriptDetail.put(curTestObj);
                                }
                                curProcObj.put("scripts_detail", scriptDetail);
                            }
                            valuePosicInArray = LPArray.valuePosicInArray(fieldsToGet, TblsTesting.ScriptsCoverage.ENDPOINTS_COVERAGE_DETAIL.getName());
                            if (valuePosicInArray > -1) {
                                curProcObj.replace(TblsTesting.ScriptsCoverage.ENDPOINTS_COVERAGE_DETAIL.getName(),
                                        LPJson.convertToJsonObjectStringedValue(curProc[valuePosicInArray].toString()));
                            }
                            valuePosicInArray = LPArray.valuePosicInArray(fieldsToGet, TblsTesting.ScriptsCoverage.BUS_RULES_COVERAGE_DETAIL.getName());
                            if (valuePosicInArray > -1) {
                                curProcObj.replace(TblsTesting.ScriptsCoverage.BUS_RULES_COVERAGE_DETAIL.getName(),
                                        LPJson.convertToJsonObjectStringedValue(curProc[valuePosicInArray].toString()));
                            }
                            valuePosicInArray = LPArray.valuePosicInArray(fieldsToGet, TblsTesting.ScriptsCoverage.MSG_COVERAGE_DETAIL.getName());
                            if (valuePosicInArray > -1) {
                                curProcObj.replace(TblsTesting.ScriptsCoverage.MSG_COVERAGE_DETAIL.getName(),
                                        LPJson.convertToJsonObjectStringedValue(curProc[valuePosicInArray].toString()));
                            }
                            proceduresList.put(curProcObj);
                        }
                        jMainObj.put(mainObjectName, proceduresList);
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
                    return;


                case GET_ALL_ACTIVE_MODULES:
                        jMainObj = new JSONObject();
                        jMainObj.put("all_active_modules", getActiveModulesJSON(procInstanceName, null));
                        LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
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
                procReqSession.killIt();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

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

    private static JSONObject procInstanceCardDataInRequirements(String procInstanceName) {
        JSONObject mainObj = new JSONObject();
        mainObj.put("title", procInstanceName);
        mainObj.put("subtitle", procInstanceName);
        JSONArray fieldsArr = new JSONArray();
        String[] allFieldNames = getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableFields(),
            new String[]{TblsReqs.ProcedureInfo.DESCRIPTION.getName(), TblsReqs.ProcedureInfo.MODULE_SETTINGS.getName()});
        fieldsArr = QueryUtilities.dbSingleRowToJsonFldNameAndValueArr(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(),
                allFieldNames, new String[]{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName});
        mainObj.put("fields", fieldsArr);
        JSONArray summaryArr = new JSONArray();
        JSONObject summaryObj = new JSONObject();
        summaryObj.put("section", "Definition");
        summaryObj.put("progress", 100);
        summaryObj.put("signed", true);
        summaryObj.put("tooltip", "If URS Requirements were covered by solutions into the Procedures Definition repository");
        summaryArr.put(summaryObj);
        summaryObj = new JSONObject();
        summaryObj.put("section", "Testing Scripts");
        String repositoryName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
        Object[] schemaExists=Rdbms.dbSchemaExists(repositoryName);        
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(schemaExists[0].toString())){        
            JSONObject testingSummary = procInstanceSummaryTesting(procInstanceName);
            summaryObj.put("progress", testingSummary.get("execution_progress"));
            summaryObj.put("signed", false);
            summaryObj.put("tooltip", testingSummary.get("summary_phrase"));
        }else{
            summaryObj.put("progress", 0);
            summaryObj.put("signed", false);
            summaryObj.put("tooltip", "Not deployed yet");            
        }
        summaryArr.put(summaryObj);
        summaryObj = new JSONObject();
        summaryObj.put("section", "Testing Coverage");
        repositoryName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
        schemaExists=Rdbms.dbSchemaExists(repositoryName);        
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(schemaExists[0].toString())){        
            JSONObject testingCoverageSummary = procInstanceSummaryTestingCoverage(procInstanceName);
            summaryObj.put("progress", testingCoverageSummary.get("execution_progress"));
            summaryObj.put("signed", false);
            summaryObj.put("tooltip", testingCoverageSummary.get("summary_phrase"));
        }else{
            summaryObj.put("progress", 0);
            summaryObj.put("signed", false);
            summaryObj.put("tooltip", "Not deployed yet");            
        }
        summaryArr.put(summaryObj);
        summaryObj = new JSONObject();
        summaryObj.put("section", "Deployed");
        summaryObj.put("progress", 90);
        summaryObj.put("signed", false);
        summaryObj.put("tooltip", "");
        summaryArr.put(summaryObj);
        mainObj.put("summary", summaryArr);
        return mainObj;
    }

    private static com.google.gson.JsonArray procInstanceViewsInRequirements() {
        return procModelArray(GlobalVariables.PROC_MANAGEMENT_SPECIAL_ROLE, SIZE_WHEN_CONSIDERED_MOBILE + 1);
    }

    
    public static JSONObject procInstanceDefinitionInRequirements(String procInstanceName) {
        JSONObject jMainObj = new JSONObject();
        JSONObject dbSingleRowToJsonObj = ClassReqProcedureQueries.dbSingleRowToJsonObj(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(),
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableFields()), new String[]{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName});
        jMainObj.put("procedure_info", dbSingleRowToJsonObj);
        
        String moduleName=dbSingleRowToJsonObj.get("module_name").toString();
        Integer moduleVersion=dbSingleRowToJsonObj.get("module_version").toString().length()>0?Integer.valueOf(dbSingleRowToJsonObj.get("module_version").toString()):-1;        
        
        JSONArray dbRowsToJsonArr = new JSONArray();        
        dbRowsToJsonArr = QueryUtilities.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.BUSINESS_RULES_IN_SOLUTION.getViewName(),
            new String[]{TblsReqs.viewBusinessRulesInSolution.AREA.getName(),
                TblsReqs.viewBusinessRulesInSolution.RULE_NAME.getName(), TblsReqs.viewBusinessRulesInSolution.PRESENT.getName(), TblsReqs.viewBusinessRulesInSolution.REQUIREMENTS_LIST.getName(),
                TblsReqs.viewBusinessRulesInSolution.PREREQUISITE.getName(), TblsReqs.viewBusinessRulesInSolution.IS_MANDATORY.getName(),
                TblsReqs.viewBusinessRulesInSolution.VALUES_LIST.getName(), TblsReqs.viewBusinessRulesInSolution.PURPOSE_EN.getName(),
                TblsReqs.viewBusinessRulesInSolution.PURPOSE_ES.getName(), TblsReqs.viewBusinessRulesInSolution.BUSINESS_RULE_VALUE.getName()},
            new String[]{TblsReqs.ModuleBusinessRules.MODULE_NAME.getName(), TblsReqs.ModuleBusinessRules.MODULE_VERSION.getName(), TblsReqs.viewBusinessRulesInSolution.PROC_INSTANCE_NAME.getName()},
            new Object[]{moduleName, moduleVersion, procInstanceName},
            new String[]{TblsReqs.viewBusinessRulesInSolution.PRESENT.getName()+" desc",  TblsReqs.viewBusinessRulesInSolution.AREA.getName(), TblsReqs.viewBusinessRulesInSolution.RULE_NAME.getName()},
            new String[]{},true, true);
        jMainObj.put("module_in_solution_business_rules", dbRowsToJsonArr);
        dbRowsToJsonArr = new JSONArray();        
        dbRowsToJsonArr = QueryUtilities.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.ACTIONS_IN_SOLUTION.getViewName(),
                new String[]{TblsReqs.viewActionsInSolution.PRETTY_EN.getName(), TblsReqs.viewActionsInSolution.PRETTY_ES.getName(),
                    TblsReqs.viewActionsInSolution.ENTITY.getName(), TblsReqs.viewActionsInSolution.PRESENT.getName(), TblsReqs.viewActionsInSolution.REQUIREMENTS_LIST.getName(),
                    TblsReqs.viewActionsInSolution.ENDPOINT_NAME.getName(), TblsReqs.viewActionsInSolution.API_NAME.getName(), TblsReqs.viewActionsInSolution.OUTPUT_OBJECT_TYPES.getName(),
                    TblsReqs.viewActionsInSolution.PURPOSE_EN.getName(), TblsReqs.viewActionsInSolution.PURPOSE_ES.getName()},
                new String[]{TblsReqs.viewActionsInSolution.MODULE_NAME.getName(), TblsReqs.viewActionsInSolution.MODULE_VERSION.getName(), TblsReqs.viewActionsInSolution.PROC_INSTANCE_NAME.getName()},
                new Object[]{moduleName, moduleVersion, procInstanceName},
                new String[]{TblsReqs.viewActionsInSolution.PRESENT.getName()+" desc", TblsReqs.viewQueriesInSolution.ENTITY.getName(), TblsReqs.viewActionsInSolution.API_NAME.getName(), TblsReqs.viewActionsInSolution.ENDPOINT_NAME.getName()},
                new String[]{}, true, true);
        jMainObj.put("module_in_solution_actions", dbRowsToJsonArr);
        dbRowsToJsonArr = QueryUtilities.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.QUERIES_IN_SOLUTION.getViewName(),
                new String[]{TblsReqs.viewActionsInSolution.PRETTY_EN.getName(), TblsReqs.viewActionsInSolution.PRETTY_ES.getName(),
                    TblsReqs.viewQueriesInSolution.ENTITY.getName(), TblsReqs.viewQueriesInSolution.PRESENT.getName(), TblsReqs.viewQueriesInSolution.REQUIREMENTS_LIST.getName(),
                    TblsReqs.viewQueriesInSolution.ENDPOINT_NAME.getName(), TblsReqs.viewQueriesInSolution.API_NAME.getName(),
                    TblsReqs.viewActionsInSolution.ARGUMENTS_ARRAY.getName(), TblsReqs.viewActionsInSolution.OUTPUT_OBJECT_TYPES.getName(),
                    TblsReqs.viewActionsInSolution.PURPOSE_EN.getName(), TblsReqs.viewActionsInSolution.PURPOSE_ES.getName()},
                new String[]{TblsReqs.ModuleBusinessRules.MODULE_NAME.getName(), TblsReqs.ModuleBusinessRules.MODULE_VERSION.getName(), TblsReqs.viewQueriesInSolution.PROC_INSTANCE_NAME.getName()},
                new Object[]{moduleName, moduleVersion, procInstanceName},
                new String[]{TblsReqs.viewQueriesInSolution.PRESENT.getName()+" desc",  TblsReqs.viewQueriesInSolution.ENTITY.getName(), TblsReqs.viewActionsInSolution.API_NAME.getName(), TblsReqs.viewQueriesInSolution.ENDPOINT_NAME.getName()},
                new String[]{}, true, true);
/*        JSONArray dbRowsToJsonFinalArr=new JSONArray();
        for (int i=0;i<dbRowsToJsonArr.length();i++){
            JSONObject curRow = (JSONObject) dbRowsToJsonArr.get(i);     
            String curEndpointName=curRow.get("endpoint_name").toString();
            if (Boolean.FALSE.equals(LPJson.ValueInJsonArray(LPJson.convertJsonArrayToJSONArray(dbRowsToJsonFinalArr), curEndpointName))){
                JSONObject curRowObj=new JSONObject();
                curRowObj.put("endpoint_name", curEndpointName);
                dbRowsToJsonFinalArr.put(curRowObj);
            }
            //com.google.gson.JsonArray argArrayToJson = LPJson.convertToJsonArrayStringedObject(
            //    curRow.get(TblsTrazitDocTrazit.EndpointsDeclaration.ARGUMENTS_ARRAY.getName()).toString());
            //curRow.put(TblsTrazitDocTrazit.EndpointsDeclaration.ARGUMENTS_ARRAY.getName(), argArrayToJson);
            //dbRowsToJsonFinalArr.put(curRow);
        }  
        jMainObj.put("module_in_solution_queries", dbRowsToJsonFinalArr);*/
        jMainObj.put("module_in_solution_queries", dbRowsToJsonArr);
        
        dbRowsToJsonArr=QueryUtilities.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.SPECIAL_VIEWS_IN_SOLUTION.getViewName(),
            new String[]{TblsReqs.viewSpecialViewsInSolution.PRETTY_EN.getName(), TblsReqs.viewSpecialViewsInSolution.PRETTY_ES.getName(),
                TblsReqs.viewSpecialViewsInSolution.ENTITY.getName(), TblsReqs.viewSpecialViewsInSolution.PRESENT.getName(), TblsReqs.viewSpecialViewsInSolution.REQUIREMENTS_LIST.getName(),
                TblsReqs.viewSpecialViewsInSolution.VIEW_NAME.getName(),
                TblsReqs.viewSpecialViewsInSolution.PURPOSE_EN.getName(), TblsReqs.viewSpecialViewsInSolution.PURPOSE_ES.getName()},
            new String[]{TblsReqs.viewSpecialViewsInSolution.MODULE_NAME.getName(), TblsReqs.viewSpecialViewsInSolution.MODULE_VERSION.getName(), TblsReqs.viewSpecialViewsInSolution.PROC_INSTANCE_NAME.getName()},
            new Object[]{moduleName, moduleVersion, procInstanceName},
            new String[]{TblsReqs.viewSpecialViewsInSolution.PRESENT.getName()+" desc",  TblsReqs.viewSpecialViewsInSolution.ENTITY.getName(), TblsReqs.viewSpecialViewsInSolution.VIEW_NAME.getName()},
            new String[]{}, true, true);  
        jMainObj.put("module_in_solution_special_views", dbRowsToJsonArr);
        /*dbRowsToJsonFinalArr=new JSONArray();
        for (int i=0;i<dbRowsToJsonArr.length();i++){
            JSONObject curRow = (JSONObject) dbRowsToJsonArr.get(i);     
            String curEndpointName=curRow.get(TblsReqs.viewSpecialViewsInSolution.VIEW_NAME.getName()).toString();
            if (Boolean.FALSE.equals(LPJson.ValueInJsonArray(LPJson.convertJsonArrayToJSONArray(dbRowsToJsonFinalArr), curEndpointName))){
                JSONObject curRowObj=new JSONObject();
                curRowObj.put(TblsReqs.viewSpecialViewsInSolution.VIEW_NAME.getName(), curEndpointName);
                dbRowsToJsonFinalArr.put(curRowObj);
            }
            //com.google.gson.JsonArray argArrayToJson = LPJson.convertToJsonArrayStringedObject(
            //    curRow.get(TblsTrazitDocTrazit.EndpointsDeclaration.ARGUMENTS_ARRAY.getName()).toString());
            //curRow.put(TblsTrazitDocTrazit.EndpointsDeclaration.ARGUMENTS_ARRAY.getName(), argArrayToJson);
            //dbRowsToJsonFinalArr.put(curRow);
        }            
        jMainObj.put("module_in_solution_special_views", dbRowsToJsonFinalArr);*/
        jMainObj.put("procedure_tables", QueryUtilities.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableName(),
            new String[]{TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.ORDER_NUMBER.getName(),
                TblsReqs.ProcedureModuleTables.IS_MANDATORY.getName(), TblsReqs.ProcedureModuleTables.IS_VIEW.getName(), 
                TblsReqs.ProcedureModuleTables.TABLE_NAME.getName(), TblsReqs.ProcedureModuleTables.PURPOSE_EN.getName(), TblsReqs.ProcedureModuleTables.PURPOSE_ES.getName()},
            new String[]{TblsReqs.ProcedureModuleTables.PROC_INSTANCE_NAME.getName()},
            new Object[]{procInstanceName},
            new String[]{TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName()+" desc",  TblsReqs.ProcedureModuleTables.ORDER_NUMBER.getName(), TblsReqs.ProcedureModuleTables.TABLE_NAME.getName()},
            new String[]{}, true, true));

        Object[][] procAccessData=ClassReqProcedureQueries.procAccessBlockInRequirements(procInstanceName);
        for (Object[] curObj: procAccessData){
            jMainObj.put(curObj[0].toString(), curObj[1]);
        }
        
        SqlWhere wObj = new SqlWhere();
        wObj.addConstraint(TblsReqs.ProcedureBusinessRules.PROC_INSTANCE_NAME,
                SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{procInstanceName}, null);
        jMainObj.put(TblsReqs.TablesReqs.PROCEDURE_RISK_ASSESSMENT.getTableName(), 
        QueryUtilities.dbRowsToJsonArr(procInstanceName, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_RISK_ASSESSMENT, TblsReqs.ProcedureRiskAssessment.values(), 
            wObj, null, null, false));

        jMainObj.put(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(), procReqSolution(procInstanceName));
        jMainObj.put(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName()+"_tree", procReqSolutionTree(procInstanceName));
        
        jMainObj.put(TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableName(), 
        QueryUtilities.dbRowsToJsonArr(procInstanceName, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS, TblsReqs.ProcedureUserRequirements.values(), 
            wObj, new String[]{TblsReqs.ProcedureUserRequirements.PARENT_CODE.getName(), TblsReqs.ProcedureUserRequirements.CODE.getName()}, null, false));
        
        jMainObj.put(TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableName()+"_tree", procUserRequirementsTree(procInstanceName));

        JSONObject jViewsAccObj = new JSONObject();
        jViewsAccObj.put("roles_views", ClassReqProcedureQueries.procViewsBlockInRequirements(procInstanceName));
        jViewsAccObj.put("view_actions", QueryUtilities.dbRowsGroupedToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_ACTIONS.getViewName(),
                new String[]{TblsReqs.viewProcReqSolutionActions.WINDOW_NAME.getName(),TblsReqs.viewProcReqSolutionActions.WINDOW_NAME.getName(),
                    TblsReqs.viewProcReqSolutionActions.PRETTY_EN.getName(), TblsReqs.viewProcReqSolutionActions.PRETTY_ES.getName()},
                new String[]{TblsReqs.viewProcReqSolutionActions.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionActions.WINDOW_ACTION.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{procInstanceName},
                new String[]{TblsReqs.viewProcReqSolutionActions.ORDER_NUMBER.getName(), TblsReqs.viewProcReqSolutionActions.ENTITY.getName(), TblsReqs.viewProcReqSolutionActions.PRETTY_EN.getName()}));
        jViewsAccObj.put("view_actions_en", QueryUtilities.dbRowsGroupedToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_ACTIONS.getViewName(),
                new String[]{TblsReqs.viewProcReqSolutionActions.WINDOW_LABEL_EN.getName(), TblsReqs.viewProcReqSolutionActions.WINDOW_LABEL_EN.getName(),
                    TblsReqs.viewProcReqSolutionActions.PRETTY_EN.getName(), TblsReqs.viewProcReqSolutionActions.PRETTY_ES.getName()},
                new String[]{TblsReqs.viewProcReqSolutionActions.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionActions.WINDOW_ACTION.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{procInstanceName},
                new String[]{TblsReqs.viewProcReqSolutionActions.ORDER_NUMBER.getName(), TblsReqs.viewProcReqSolutionActions.ENTITY.getName(),TblsReqs.viewProcReqSolutionActions.PRETTY_EN.getName()}));
        jViewsAccObj.put("view_actions_es", QueryUtilities.dbRowsGroupedToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_ACTIONS.getViewName(),
                new String[]{TblsReqs.viewProcReqSolutionActions.WINDOW_LABEL_ES.getName(), TblsReqs.viewProcReqSolutionActions.WINDOW_LABEL_ES.getName(),
                    TblsReqs.viewProcReqSolutionActions.PRETTY_EN.getName(), TblsReqs.viewProcReqSolutionActions.PRETTY_ES.getName()},
                new String[]{TblsReqs.viewProcReqSolutionActions.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionActions.WINDOW_ACTION.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{procInstanceName},
                new String[]{TblsReqs.viewProcReqSolutionActions.ORDER_NUMBER.getName(), TblsReqs.viewProcReqSolutionActions.ENTITY.getName(), TblsReqs.viewProcReqSolutionActions.PRETTY_EN.getName()}));

        jViewsAccObj.put("view_sops", ClassReqProcedUserAndActionsForQueries.viewsBySops(procInstanceName));

        jMainObj.put("views_info", jViewsAccObj);

        jMainObj.put("sops", 
        QueryUtilities.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableName(),
            getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableFields()), new String[]{TblsReqs.ProcedureSopMetaData.PROC_INSTANCE_NAME.getName()},
            new Object[]{procInstanceName},new String[]{TblsReqs.ProcedureSopMetaData.SOP_NAME.getName()},null, true, true));

        jMainObj.put("tables", 
        QueryUtilities.dbRowsGroupedToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableName(),
                new String[]{TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.TABLE_NAME.getName(), TblsReqs.ProcedureModuleTables.PURPOSE_EN.getName(), TblsReqs.ProcedureModuleTables.PURPOSE_ES.getName(), TblsReqs.ProcedureModuleTables.IS_VIEW.getName()},
                new String[]{TblsReqs.ProcedureModuleTables.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName},
                new String[]{TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.ORDER_NUMBER.getName()}));

        jMainObj.put("frontend_proc_model", ClassReqProcedureQueries.feProcModel(procInstanceName));
        return jMainObj;
    }

    private static JSONArray procInstanceMasterDataInRequirements(String procInstanceName) {
        return QueryUtilities.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MASTER_DATA.getTableName(),
                getAllFieldNames(TblsReqs.TablesReqs.PROC_MASTER_DATA.getTableFields()), new String[]{TblsReqs.ProcedureMasterData.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName},
                null, new String[]{TblsReqs.ProcedureMasterData.JSON_OBJ.getName()}, true, true);
    }

    private static JSONArray procInstanceFrontendTestingInRequirements(String procInstanceName) {
        return QueryUtilities.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_FRONT_TESTING_WITNESS.getTableName(),
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_FRONT_TESTING_WITNESS.getTableFields()), new String[]{TblsReqs.ProcedureFrontendTestingWitness.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureFrontendTestingWitness.ACTIVE.getName()}, new Object[]{procInstanceName, true},
                new String[]{TblsReqs.ProcedureFrontendTestingWitness.ORDER_NUMBER.getName()}, new String[]{}, true, true);
    }

    private static JSONArray procInstanceManualsInRequirements(String procInstanceName) {
        return QueryUtilities.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MANUALS.getTableName(),
                getAllFieldNames(TblsReqs.TablesReqs.PROC_MANUALS.getTableFields()), new String[]{TblsReqs.ProcedureManuals.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureManuals.ACTIVE.getName()}, new Object[]{procInstanceName, true},
                new String[]{TblsReqs.ProcedureManuals.ORDER_NUMBER.getName()}, new String[]{}, true, true);       
    }
    
    private static JSONObject procInstanceSupportInRequirements(String procInstanceName){
        JSONObject supportData=new JSONObject();        
        supportData.put(TblsCnfg.TablesConfig.ZZZ_PROPERTIES_ERROR.getTableName(),QueryUtilities.dbRowsGroupedToJsonArrForParentChild(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsCnfg.TablesConfig.ZZZ_PROPERTIES_ERROR.getRepositoryName()), TblsCnfg.TablesConfig.ZZZ_PROPERTIES_ERROR.getTableName(),
                new String[]{TblsCnfg.zzzPropertiesMissing.RULE_NAME.getName(), TblsCnfg.zzzPropertiesMissing.ID.getName(), TblsCnfg.zzzPropertiesMissing.ACTION_NAME.getName(), TblsCnfg.zzzPropertiesMissing.CREATION_DATE.getName()},
                new String[]{TblsCnfg.zzzPropertiesMissing.RESOLVED.getName()}, new Object[]{false},
                new String[]{TblsCnfg.zzzPropertiesMissing.RULE_NAME.getName(), TblsCnfg.zzzPropertiesMissing.ID.getName()+" desc"}));

        supportData.put(TblsCnfg.TablesConfig.ZZZ_DB_ERROR.getTableName(), QueryUtilities.dbRowsGroupedToJsonArrForParentChild(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsCnfg.TablesConfig.ZZZ_DB_ERROR.getRepositoryName()), TblsCnfg.TablesConfig.ZZZ_DB_ERROR.getTableName(),
                new String[]{TblsCnfg.zzzDbErrorLog.ERROR_MESSAGE.getName(), TblsCnfg.zzzDbErrorLog.ID.getName(), TblsCnfg.zzzDbErrorLog.ACTION_NAME.getName(), TblsCnfg.zzzDbErrorLog.CREATION_DATE.getName(), TblsCnfg.zzzDbErrorLog.QUERY.getName(), TblsCnfg.zzzDbErrorLog.QUERY_PARAMETERS.getName()},
                new String[]{TblsCnfg.zzzDbErrorLog.RESOLVED.getName()}, new Object[]{false},
                new String[]{TblsCnfg.zzzDbErrorLog.ERROR_MESSAGE.getName(), TblsCnfg.zzzDbErrorLog.ID.getName()+" desc"}));

        supportData.put(TblsApp.TablesApp.INCIDENT.getTableName(),QueryUtilities.dbRowsGroupedToJsonArr("app", LPPlatform.buildSchemaName("app", TblsApp.TablesApp.INCIDENT.getRepositoryName()), TblsApp.TablesApp.INCIDENT.getTableName(),
                new String[]{TblsApp.Incident.CATEGORY.getName(), TblsApp.Incident.ID.getName(), TblsApp.Incident.TITLE.getName(), TblsApp.Incident.DETAIL.getName(), TblsApp.Incident.DATE_CREATION.getName(), TblsApp.Incident.STATUS.getName()},
                new String[]{TblsApp.Incident.INCIDENT_PROCEDURE.getName(), TblsApp.Incident.DATE_RESOLUTION.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, new Object[]{procInstanceName},
                new String[]{TblsApp.Incident.CATEGORY.getName(), TblsApp.Incident.ID.getName()+" desc"}));
        return supportData;
    }

    public static JSONObject getScriptWithSteps(Integer scriptId, String procInstanceName, String[] fieldsToRetrieveScripts, Object[] curTest) {
        String[] actionsList = new String[]{};

        String repositoryName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
        if (curTest == null) {
            fieldsToRetrieveScripts = TblsTesting.getScriptPublicFieldNames(procInstanceName);
            Object[][] scriptsTblInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, repositoryName, TblsTesting.TablesTesting.SCRIPT.getTableName(),
                    new String[]{TblsTesting.Script.SCRIPT_ID.getName()}, new Object[]{scriptId},
                    fieldsToRetrieveScripts, new String[]{TblsTesting.Script.SCRIPT_ID.getName()});
            curTest = scriptsTblInfo[0];
        }
        JSONObject curTestObj = new JSONObject();
        if (Boolean.TRUE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(curTest[0].toString()))) {
            return curTestObj;
        }
        curTestObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveScripts, curTest);
        String[] fieldsToRetrieveScriptSteps = EnumIntTableFields.getAllFieldNames(TblsTesting.TablesTesting.SCRIPT_STEPS, procInstanceName);
        Integer scriptIdPosic = LPArray.valuePosicInArray(fieldsToRetrieveScripts, TblsTesting.Script.SCRIPT_ID.getName());
        if (scriptIdPosic > -1) {
            Object[][] scriptStepsTblInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, repositoryName, TblsTesting.TablesTesting.SCRIPT_STEPS.getTableName(),
                    new String[]{TblsTesting.ScriptSteps.SCRIPT_ID.getName()},
                    new Object[]{Integer.valueOf(curTest[scriptIdPosic].toString())},
                    fieldsToRetrieveScriptSteps,
                    new String[]{TblsTesting.ScriptSteps.STEP_ID.getName()});
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptStepsTblInfo[0][0].toString()))) {
                JSONArray scriptStepsList = new JSONArray();
                for (Object[] curStep : scriptStepsTblInfo) {
                    JSONObject curStepObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveScriptSteps, curStep);
                    Integer actionPosic = LPArray.valuePosicInArray(fieldsToRetrieveScriptSteps, TblsTesting.ScriptSteps.ACTION_NAME.getName());
                    if (actionPosic > -1) {
                        actionsList = LPArray.addValueToArray1D(actionsList, LPNulls.replaceNull(curStep[actionPosic]).toString());
                    }
                    Integer posicId = LPArray.valuePosicInArray(fieldsToRetrieveScriptSteps, TblsTesting.ScriptSteps.EVAL_SYNTAXIS.getName());
                    String tagName = TblsTesting.ScriptSteps.EVAL_SYNTAXIS.getName() + "_icon";
                    String tagClass = TblsTesting.ScriptSteps.EVAL_SYNTAXIS.getName() + "_class";
                    if (posicId > -1) {
                        switch (curStep[posicId].toString().toUpperCase()) {
                            case "MATCH":
                                curStepObj.put(tagName, "check_circle");
                                curStepObj.put(tagClass, "green");
                                break;
                            case "UNMATCH":
                                curStepObj.put(tagName, "cancel");
                                curStepObj.put(tagClass, "red");
                                break;
                            default:
                                curStepObj.put(tagName, "help");
                                curStepObj.put(tagClass, "yellow");
                                break;
                        }
                    }
                    posicId = LPArray.valuePosicInArray(fieldsToRetrieveScriptSteps, TblsTesting.ScriptSteps.EVAL_CODE.getName());
                    tagName = TblsTesting.ScriptSteps.EVAL_CODE.getName() + "_icon";
                    tagClass = TblsTesting.ScriptSteps.EVAL_CODE.getName() + "_class";
                    if (posicId > -1) {
                        switch (curStep[posicId].toString().toUpperCase()) {
                            case "MATCH":
                                curStepObj.put(tagName, "check_circle");
                                curStepObj.put(tagClass, "green");
                                break;
                            case "UNMATCH":
                                curStepObj.put(tagName, "cancel");
                                curStepObj.put(tagClass, "red");
                                break;
                            default:
                                curStepObj.put(tagName, "help");
                                curStepObj.put(tagClass, "yellow");
                                break;
                        }
                    }
                    scriptStepsList.put(curStepObj);
                }
                curTestObj.put("steps", scriptStepsList);
            }
        }
        return curTestObj;
    }

    public static JSONObject getSpecScriptWithSteps(Integer scriptId, String procInstanceName, String[] fieldsToRetrieveScripts, Object[] curTest) {
        String[] actionsList = new String[]{};

        String repositoryName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
        if (curTest == null) {
            fieldsToRetrieveScripts = TblsTesting.getSpecScriptPublicFieldNames(procInstanceName);
            Object[][] scriptsTblInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, repositoryName, TblsTesting.TablesTesting.SPEC_SCRIPT.getTableName(),
                    new String[]{TblsTesting.SpecScript.SCRIPT_ID.getName()}, new Object[]{scriptId},
                    fieldsToRetrieveScripts, new String[]{TblsTesting.SpecScript.SCRIPT_ID.getName()});
            curTest = scriptsTblInfo[0];
        }
        JSONObject curTestObj = new JSONObject();
        if (Boolean.TRUE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(curTest[0].toString()))) {
            return curTestObj;
        }
        curTestObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveScripts, curTest);
        String[] fieldsToRetrieveScriptSteps = EnumIntTableFields.getAllFieldNames(TblsTesting.TablesTesting.SPEC_SCRIPT_STEPS, procInstanceName);
        Integer scriptIdPosic = LPArray.valuePosicInArray(fieldsToRetrieveScripts, TblsTesting.SpecScript.SCRIPT_ID.getName());
        if (scriptIdPosic > -1) {
            Object[][] scriptStepsTblInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, repositoryName, TblsTesting.TablesTesting.SPEC_SCRIPT_STEPS.getTableName(),
                    new String[]{TblsTesting.SpecScriptSteps.SCRIPT_ID.getName()},
                    new Object[]{Integer.valueOf(curTest[scriptIdPosic].toString())},
                    fieldsToRetrieveScriptSteps,
                    new String[]{TblsTesting.SpecScriptSteps.STEP_ID.getName()});
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptStepsTblInfo[0][0].toString()))) {
                JSONArray scriptStepsList = new JSONArray();
                for (Object[] curStep : scriptStepsTblInfo) {
                    JSONObject curStepObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveScriptSteps, curStep);
                    Integer analysisPosic = LPArray.valuePosicInArray(fieldsToRetrieveScriptSteps, TblsTesting.SpecScriptSteps.ANALYSIS.getName());
                    if (analysisPosic > -1) {
                        actionsList = LPArray.addValueToArray1D(actionsList, LPNulls.replaceNull(curStep[analysisPosic]).toString());
                    }
                    Integer posicId = LPArray.valuePosicInArray(fieldsToRetrieveScriptSteps, TblsTesting.SpecScriptSteps.EVAL_SYNTAXIS.getName());
                    String tagName = TblsTesting.SpecScriptSteps.EVAL_SYNTAXIS.getName() + "_icon";
                    String tagClass = TblsTesting.SpecScriptSteps.EVAL_SYNTAXIS.getName() + "_class";
                    if (posicId > -1) {
                        switch (curStep[posicId].toString().toUpperCase()) {
                            case "MATCH":
                                curStepObj.put(tagName, "check_circle");
                                curStepObj.put(tagClass, "green");
                                break;
                            case "UNMATCH":
                                curStepObj.put(tagName, "cancel");
                                curStepObj.put(tagClass, "red");
                                break;
                            default:
                                curStepObj.put(tagName, "help");
                                curStepObj.put(tagClass, "yellow");
                                break;
                        }
                    }
                    posicId = LPArray.valuePosicInArray(fieldsToRetrieveScriptSteps, TblsTesting.SpecScriptSteps.EVAL_CODE.getName());
                    tagName = TblsTesting.SpecScriptSteps.EVAL_CODE.getName() + "_icon";
                    tagClass = TblsTesting.SpecScriptSteps.EVAL_CODE.getName() + "_class";
                    if (posicId > -1) {
                        switch (curStep[posicId].toString().toUpperCase()) {
                            case "MATCH":
                                curStepObj.put(tagName, "check_circle");
                                curStepObj.put(tagClass, "green");
                                break;
                            case "UNMATCH":
                                curStepObj.put(tagName, "cancel");
                                curStepObj.put(tagClass, "red");
                                break;
                            default:
                                curStepObj.put(tagName, "help");
                                curStepObj.put(tagClass, "yellow");
                                break;
                        }
                    }
                    scriptStepsList.put(curStepObj);
                }
                curTestObj.put("steps", scriptStepsList);
            }
        }
        return curTestObj;
    }

    private static JSONObject procInstanceSummaryTesting(String procInstanceName) {
        String repositoryName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
        String[] fieldsToRetrieveScript = new String[]{TblsTesting.Script.RUN_SUMMARY.getName(), TblsTesting.Script.DATE_EXECUTION.getName()};
        //.getTableFieldsFromString(TblsTesting.TablesTesting.SCRIPT, new Strin);
        Object[][] scriptStepsTblInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, repositoryName, TblsTesting.TablesTesting.SCRIPT.getTableName(),
                new String[]{TblsTesting.Script.ACTIVE.getName()}, new Object[]{true},
                fieldsToRetrieveScript, new String[]{TblsTesting.Script.SCRIPT_ID.getName()});
        int numNotSuccess = 0;
        Date lastPerformed = null;
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptStepsTblInfo[0][0].toString())) {
            JSONObject jMain = new JSONObject();
            jMain.put("summary_phrase", "No testing scripts defined yet");
            jMain.put("execution_progress", 0);
            return jMain;
        }
        for (Object[] curTest : scriptStepsTblInfo) {
            if (Boolean.FALSE.equals(LPNulls.replaceNull(curTest[0]).toString().toUpperCase().contains("SUCCESS"))) {
                numNotSuccess++;
            }
            if (LPNulls.replaceNull(curTest[1]).toString().length() > 0) {
                if (lastPerformed == null) {
                    lastPerformed = LPDate.stringFormatToDate(LPNulls.replaceNull(curTest[1]).toString());
                } else {
                    if (LPDate.stringFormatToDate(LPNulls.replaceNull(curTest[1]).toString()).after(lastPerformed)) {
                        lastPerformed = LPDate.stringFormatToDate(LPNulls.replaceNull(curTest[1]).toString());
                    }
                }
            }
        }
        JSONObject jMain = new JSONObject();
        jMain.put("total", scriptStepsTblInfo.length);
        jMain.put("total_with_error", numNotSuccess);
        jMain.put("last_performed", lastPerformed);
        String lastPerformedStr = lastPerformed == null ? "Not performed" : lastPerformed.toString();
        String sumPhrase = " (Last performed:" + lastPerformedStr + ")";
        if (numNotSuccess == 0) {
            sumPhrase = "All tests run success" + sumPhrase;
        } else {
            sumPhrase = "Failed " + numNotSuccess + " of " + scriptStepsTblInfo.length + sumPhrase;
        }
        jMain.put("summary_phrase", sumPhrase);
        BigDecimal perctg = new BigDecimal(scriptStepsTblInfo.length).subtract(new BigDecimal(numNotSuccess));
        perctg = perctg.divide(new BigDecimal(scriptStepsTblInfo.length), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        //      .divide(new BigDecimal());
        //perctg = perctg.multiply(new BigDecimal(100));
        perctg = perctg.setScale(2, RoundingMode.UP);
        jMain.put("execution_progress", perctg);
        return jMain;
    }

    private static JSONObject procInstanceSummaryTestingCoverage(String procInstanceName) {
        String repositoryName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
        String[] fieldsToRetrieveScript = new String[]{TblsTesting.ScriptsCoverage.DATE_EXECUTION.getName(), TblsTesting.ScriptsCoverage.BUS_RULES_COVERAGE.getName(),
            TblsTesting.ScriptsCoverage.ENDPOINTS_COVERAGE.getName()};
        Object[][] scriptStepsTblInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, repositoryName, TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableName(),
                new String[]{TblsTesting.ScriptsCoverage.ACTIVE.getName()}, new Object[]{true},
                fieldsToRetrieveScript, new String[]{TblsTesting.ScriptsCoverage.DATE_EXECUTION.getName() + " desc"});
        Double totalPerc = null;
        Double endpointsCov = null;
        Double notifCov = null;
        Date lastPerformed = null;
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptStepsTblInfo[0][0].toString())) {
            JSONObject jMain = new JSONObject();
            jMain.put("summary_phrase", "No Coverage Analysis defined yet");
            jMain.put("execution_progress", 0);
            return jMain;
        }
        Object[] curTest = scriptStepsTblInfo[0];
        if (LPNulls.replaceNull(curTest[0]).toString().length() > 0) {
            if (lastPerformed == null) {
                lastPerformed = LPDate.stringFormatToDate(LPNulls.replaceNull(curTest[0]).toString());
            } else {
                if (LPDate.stringFormatToDate(LPNulls.replaceNull(curTest[0]).toString()).after(lastPerformed)) {
                    lastPerformed = LPDate.stringFormatToDate(LPNulls.replaceNull(curTest[0]).toString());
                }
            }
        }
        notifCov = LPNulls.replaceNull(curTest[1]).toString().length() == 0 ? Double.valueOf("0") : Double.valueOf(curTest[1].toString());
        endpointsCov = LPNulls.replaceNull(curTest[2]).toString().length() == 0 ? Double.valueOf("0") : Double.valueOf(curTest[2].toString());
        String lastPerformedStr = lastPerformed == null ? "Not performed" : lastPerformed.toString();
        totalPerc = ((notifCov + endpointsCov) / 2) * 100;
        JSONObject jMain = new JSONObject();
        jMain.put("last_performed", lastPerformed);
        String sumPhrase = " (Last performed:" + lastPerformedStr + ")";
        BigDecimal endpointsCovBigDec = new BigDecimal(endpointsCov);
        BigDecimal notifCovBigDec = new BigDecimal(notifCov);
        BigDecimal perctg = notifCovBigDec.add(endpointsCovBigDec);
        perctg = perctg.divide(new BigDecimal(2));
        perctg = perctg.setScale(2, RoundingMode.UP);
        endpointsCovBigDec = endpointsCovBigDec.setScale(2, RoundingMode.UP);
        notifCovBigDec = notifCovBigDec.setScale(2, RoundingMode.UP);
        jMain.put("execution_progress", perctg);
        if (perctg.equals(new BigDecimal(100))) {
            sumPhrase = "All covered" + sumPhrase;
        } else {
            sumPhrase = "Actions Coverage:" + endpointsCovBigDec + ". Notifications Coverage " + notifCovBigDec + sumPhrase;
        }
        jMain.put("summary_phrase", sumPhrase);
        return jMain;
    }
    private static JSONArray procReqSolution(String procInstanceName){        
        JSONArray reqSolJsonExtendedArr = new JSONArray();
        SqlWhere wObj = new SqlWhere();
        wObj.addConstraint(TblsReqs.ProcedureBusinessRules.PROC_INSTANCE_NAME,
                SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{procInstanceName}, null);        
        JSONArray reqSolJsonArr = QueryUtilities.dbRowsToJsonArr(procInstanceName, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, TblsReqs.ProcedureReqSolution.values(), 
            wObj, null, null, false);        
        /*JSONArray reqSolJsonArr = ClassReqProcedureQueries.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(),
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableFields()), new String[]{TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName()},
                new Object[]{procInstanceName},
                new String[]{TblsReqs.ProcedureReqSolution.ORDER_NUMBER.getName()},null, true, true);*/
        if (reqSolJsonArr.length()==1){
            JSONObject curRow=(JSONObject) reqSolJsonArr.get(0);
            if (curRow.containsKey("No Data"))
                return reqSolJsonArr;
        }
        for (int i=0;i<reqSolJsonArr.length();i++){
            JSONObject curRow=(JSONObject) reqSolJsonArr.get(i);
            reqSolJsonExtendedArr.put(solutionAddRelevantInfo(curRow));
        }   
        return reqSolJsonExtendedArr;
    }

    public static JSONObject solutionAddRelevantInfo(JSONObject curRow){
            String curType=curRow.get("type").toString();
            ReqSolutionTypes curTypeObj = ReqSolutionTypes.getByTagValue(curType);
            switch (curTypeObj){
                case BUSINESS_RULE:
                    String busRule=LPNulls.replaceNull(curRow.get("business_rule")).toString();
                    String busRuleValue=LPNulls.replaceNull(curRow.get("business_rule_value")).toString();
                    curRow.put("relevant_info_1", busRule);
                    curRow.put("relevant_info_2", busRuleValue);
                    break;
                case WINDOW:
                    String wName=LPNulls.replaceNull(curRow.get("window_name")).toString();
                    curRow.put("relevant_info_1", wName);
                    String wQuery=LPNulls.replaceNull(curRow.get("window_query")).toString();
                    curRow.put("relevant_info_2", wName);
                    break;
                case WINDOW_BUTTON:
                    String wAction=LPNulls.replaceNull(curRow.get("window_action")).toString();
                    curRow.put("relevant_info_1", wAction);
                    curRow.put("relevant_info_2", "");
                    break;
                case SPECIAL_VIEW:
                    String specialViewName=LPNulls.replaceNull(curRow.get("special_view_name")).toString();
                    curRow.put("relevant_info_1", specialViewName);
                    curRow.put("relevant_info_2", "");
                    break;
                default:
                    break;
            }

        return curRow;
    }
    public static JSONArray procReqSolutionTree(String procInstanceName){    
        JSONArray parentCodeFinalArr=new JSONArray();
        JSONArray parentCodeSolutionFinalArr=new JSONArray();
        SqlWhere parentCodeWhereObj = new SqlWhere();
        parentCodeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME,
                SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{procInstanceName}, null);
        parentCodeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.PARENT_CODE,
                SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, null);
        parentCodeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.CODE,
                SqlStatement.WHERECLAUSE_TYPES.IS_NULL, new Object[]{}, null);
        JSONArray parentCodeArr = QueryUtilities.dbRowsToJsonArr(procInstanceName, GlobalVariables.Schemas.REQUIREMENTS.getName(), 
                TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_VIEW, TblsReqs.viewProcReqAndSolutionView.values(), 
                parentCodeWhereObj, new String[]{TblsReqs.viewProcReqSolutionViews.PARENT_CODE.getName(), TblsReqs.viewProcReqSolutionViews.CODE.getName()}, null, false);        
        JSONObject curRow=(JSONObject) parentCodeArr.get(0);
        if (Boolean.TRUE.equals(curRow.containsKey("No Data"))){
            return parentCodeArr;
        }

        for (int i=0;i<parentCodeArr.length();i++){
            curRow=(JSONObject) parentCodeArr.get(i);
            if (Boolean.FALSE.equals(curRow.containsKey(TblsReqs.ProcedureUserRequirements.REQ_ID.getName())))
                return parentCodeFinalArr;

            String curReqId=curRow.get(TblsReqs.ProcedureUserRequirements.REQ_ID.getName()).toString();
            String curParentCode=curRow.get(TblsReqs.ProcedureUserRequirements.PARENT_CODE.getName()).toString();
            curRow.put("key", curReqId);
            curRow.put("label", curParentCode);
            curRow=solutionAddRelevantInfo(curRow);

            parentCodeWhereObj = new SqlWhere();
            parentCodeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME,
                    SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{procInstanceName}, null);
            parentCodeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.PARENT_CODE,
                    SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curParentCode}, null);
            parentCodeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.CODE,
                    SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, null);
            JSONArray codeArr = QueryUtilities.dbRowsToJsonArr(procInstanceName, GlobalVariables.Schemas.REQUIREMENTS.getName(), 
                    TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_VIEW, TblsReqs.viewProcReqAndSolutionView.values(), 
                    parentCodeWhereObj, new String[]{TblsReqs.ProcedureUserRequirements.PARENT_CODE.getName(), TblsReqs.ProcedureUserRequirements.CODE.getName()}, null, false);        
            
            if (curRow.containsKey("No Data"))
                curRow.put("children", new JSONArray());
            else{
                JSONArray codeFinalArr = new JSONArray();
                for (int j=0;j<codeArr.length();j++){
                    JSONObject curChildrenRow=(JSONObject) codeArr.get(j);
                     if (Boolean.FALSE.equals(curChildrenRow.containsKey("No Data"))){
                        codeFinalArr.put(solutionAddRelevantInfo(curChildrenRow));
                     }
                }                  
                curRow.put("children", codeFinalArr);
            }
            parentCodeFinalArr.put(curRow);
/*
            SqlWhere parentCodeSolutionWhereObj = new SqlWhere();
            parentCodeSolutionWhereObj.addConstraint(TblsReqs.ProcedureReqSolution.REQ_ID,
                    SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{Integer.valueOf(curReqId)}, null);          
            EnumIntTableFields[] parentCodeSolutionFldsObj = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION);
            String[] parentCodeSolutionFldsStr=EnumIntTableFields.getAllFieldNames(parentCodeSolutionFldsObj,
                        new String[]{TblsReqs.ProcedureReqSolution.JSON_MODEL.getName(), TblsReqs.ProcedureReqSolution.TWOICONS_DETAIL.getName(),
                        TblsReqs.ProcedureReqSolution.JSON_MODEL.getName(), TblsReqs.ProcedureReqSolution.SPECIAL_VIEW_JSON_MODEL.getName()});
            EnumIntTableFields[] parentCodeSolutionFldsObjFiltered = EnumIntTableFields.getTableFieldsFromString(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, parentCodeSolutionFldsStr);
            Object[][] parentSolutionInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, 
                parentCodeSolutionWhereObj, 
                parentCodeSolutionFldsObjFiltered,
                new String[]{TblsReqs.ProcedureReqSolution.ORDER_NUMBER.getName(), TblsReqs.ProcedureReqSolution.SOLUTION_ID.getName()}, Boolean.TRUE);
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(parentSolutionInfo[0][0].toString()))){
                for (Object[] curParentSolution: parentSolutionInfo){

                    parentCodeSolutionFinalArr.put(LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(parentCodeSolutionFldsObjFiltered), curParentSolution));
                }
            }
*/            
/*
            SqlWhere codeWhereObj = new SqlWhere();
            codeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME,
                    SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{procInstanceName}, null);
            codeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.PARENT_CODE,
                    SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curParentCode}, null);
            codeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.CODE,
                    SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, null);
            JSONArray codesArr = QueryUtilities.dbRowsToJsonArr(procInstanceName, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS, TblsReqs.ProcedureUserRequirements.values(), 
                    codeWhereObj, new String[]{TblsReqs.ProcedureUserRequirements.PARENT_CODE.getName(), TblsReqs.ProcedureUserRequirements.CODE.getName()}, null, false);                    
            JSONArray codesFinalArr=new JSONArray();
//            if (Boolean.FALSE.equals(codesArr.length()==1&&
//                (JSONObject)codesArr.get(0).containsKey(ClassReqProcedureQueries.NO_DATA))){
                for (int j=0;j<codesArr.length();j++){
                    JSONObject curRowDet=(JSONObject) codesArr.get(j);
                    if (curRowDet.containsKey(ClassReqProcedureQueries.NO_DATA))
                        break;
                    curReqId=curRowDet.get(TblsReqs.ProcedureUserRequirements.REQ_ID.getName()).toString();
                    String curCode=curRowDet.get(TblsReqs.ProcedureUserRequirements.CODE.getName()).toString();
                    curRowDet.put("key", curReqId);
                    curRowDet.put("label", curCode);
                    codesFinalArr.put(curRowDet);
                }
//            } 
            curRow.put("children", codesFinalArr);
            parentCodeFinalArr.put(curRow);
*/
        }   
        return parentCodeFinalArr;
    }
    
    private static JSONArray procUserRequirementsTree(String procInstanceName){    
        JSONArray parentCodeFinalArr=new JSONArray();
        SqlWhere parentCodeWhereObj = new SqlWhere();
        parentCodeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME,
                SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{procInstanceName}, null);
        parentCodeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.PARENT_CODE,
                SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, null);
        parentCodeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.CODE,
                SqlStatement.WHERECLAUSE_TYPES.IS_NULL, new Object[]{}, null);
        JSONArray parentCodeArr = QueryUtilities.dbRowsToJsonArr(procInstanceName, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS, TblsReqs.ProcedureUserRequirements.values(), 
                parentCodeWhereObj, new String[]{TblsReqs.ProcedureUserRequirements.PARENT_CODE.getName(), TblsReqs.ProcedureUserRequirements.CODE.getName()}, null, false);        


        for (int i=0;i<parentCodeArr.length();i++){
            JSONObject curRow=(JSONObject) parentCodeArr.get(i);
            if (Boolean.FALSE.equals(curRow.containsKey(TblsReqs.ProcedureUserRequirements.REQ_ID.getName())))
                return parentCodeFinalArr;

            String curReqId=curRow.get(TblsReqs.ProcedureUserRequirements.REQ_ID.getName()).toString();
            String curParentCode=curRow.get(TblsReqs.ProcedureUserRequirements.PARENT_CODE.getName()).toString();
            curRow.put("key", curReqId);
            curRow.put("label", curParentCode);
            

            SqlWhere codeWhereObj = new SqlWhere();
            codeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME,
                    SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{procInstanceName}, null);
            codeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.PARENT_CODE,
                    SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curParentCode}, null);
            codeWhereObj.addConstraint(TblsReqs.ProcedureUserRequirements.CODE,
                    SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, null);
            JSONArray codesArr = QueryUtilities.dbRowsToJsonArr(procInstanceName, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS, TblsReqs.ProcedureUserRequirements.values(), 
                    codeWhereObj, new String[]{TblsReqs.ProcedureUserRequirements.PARENT_CODE.getName(), TblsReqs.ProcedureUserRequirements.CODE.getName()}, null, false);                    
            JSONArray codesFinalArr=new JSONArray();
//            if (Boolean.FALSE.equals(codesArr.length()==1&&
//                (JSONObject)codesArr.get(0).containsKey(ClassReqProcedureQueries.NO_DATA))){
                for (int j=0;j<codesArr.length();j++){
                    JSONObject curRowDet=(JSONObject) codesArr.get(j);
                    if (curRowDet.containsKey(ClassReqProcedureQueries.NO_DATA))
                        break;
                    curReqId=curRowDet.get(TblsReqs.ProcedureUserRequirements.REQ_ID.getName()).toString();
                    String curCode=curRowDet.get(TblsReqs.ProcedureUserRequirements.CODE.getName()).toString();
                    curRowDet.put("key", curReqId);
                    curRowDet.put("label", curCode);
                    codesFinalArr.put(curRowDet);
                }
//            } 
            curRow.put("children", codesFinalArr);
            parentCodeFinalArr.put(curRow);
        }   
        return parentCodeFinalArr;
    }

}
