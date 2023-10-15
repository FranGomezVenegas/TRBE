package trazit.procedureinstance.definition.apis;

import static com.labplanet.servicios.app.AppProcedureListAPI.LABEL_ARRAY_PROCEDURE_INSTANCES;
import static com.labplanet.servicios.app.AppProcedureListAPI.SIZE_WHEN_CONSIDERED_MOBILE;
import static com.labplanet.servicios.app.AppProcedureListAPI.procModelArray;
import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.app.TestingRegressionUAT;
import module.monitoring.definition.TblsEnvMonitConfig;
import trazit.procedureinstance.definition.logic.ReqProcDefTestingCoverageSummary;
import trazit.procedureinstance.definition.definition.ReqProcedureEnums.ReqProcedureDefinitionAPIQueriesEndpoints;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.procedureinstance.definition.definition.TblsReqs;
import trazit.procedureinstance.definition.logic.ClassReqProcedUserAndActions;
import trazit.procedureinstance.definition.logic.ClassReqProcedureQueries;
import static trazit.procedureinstance.definition.logic.ClassReqProcedureQueries.dbRowsToJsonArr;
import static trazit.procedureinstance.definition.logic.ReqProcedureFrontendMasterData.getActiveModulesJSON;

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
                        jArr.add(jObj);
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
                            wFldName, wFldVal, fieldsToRetrieveScripts, fieldsToRetrieveScripts);
                    JSONArray proceduresList = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procAndInstanceArr[0][0].toString()))){
                        for (Object[] curProc : procAndInstanceArr) {
                            JSONObject curProcObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveScripts, curProc);
                            Integer valuePosicInArray = LPArray.valuePosicInArray(fieldsToRetrieveScripts, TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName());
                            if (valuePosicInArray > -1) {                                
                                curProcObj.put("cardData", procInstanceCardDataInRequirements(curProc[valuePosicInArray].toString()));
                                curProcObj.put("definition", procInstanceDefinitionInRequirements(curProc[valuePosicInArray].toString()));
                                curProcObj.put("master_data", procInstanceMasterDataInRequirements(curProc[valuePosicInArray].toString()));
                                curProcObj.put("views", procInstanceViewsInRequirements());
                                curProcObj.put("testing", ReqProcDefTestingCoverageSummary.procInstanceTestingInfo(curProc[valuePosicInArray].toString()));
                                curProcObj.put("manuals", procInstanceManualsInRequirements(curProc[valuePosicInArray].toString()));
                                curProcObj.put("frontend_testing", procInstanceFrontendTestingInRequirements(curProc[valuePosicInArray].toString()));

                            }
                            proceduresList.add(curProcObj);

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
                            procedures.add(procInstanceDefinition);
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
                                scriptsList.add(curTestObj);
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
                    jMainObj = new JSONObject();
                    mainObjectName = "testing_coverage_summary";
                    Object[] actionDiagnosesAll = TestingRegressionUAT.procedureRepositoryMirrors(procInstanceName);
                    Object[] allMismatchesDiagn = (Object[]) actionDiagnosesAll[0];
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allMismatchesDiagn[0].toString())) {
                        jObj.put("Error_found", "Not mirrors");
                        jArr = new JSONArray();
                        Object[][] mismatchTables = (Object[][]) actionDiagnosesAll[1];
                        for (int i = 1; i < mismatchTables.length; i++) {
                            jArr.add(LPJson.convertArrayRowToJSONObject(LPArray.convertObjectArrayToStringArray(mismatchTables[0]), mismatchTables[i]));
                        }
                        JSONArray jTblColsArr = new JSONArray();
                        for (Object curCol : mismatchTables[0]) {
                            jTblColsArr.add(curCol.toString());
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
                                    scriptDetail.add(curTestObj);
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
                            proceduresList.add(curProcObj);
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
        mainObj.put("title", "Main Title Demo");
        mainObj.put("subtitle", procInstanceName);
        JSONArray fieldsArr = new JSONArray();

        fieldsArr = ClassReqProcedureQueries.dbSingleRowToJsonFldNameAndValueArr(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(),
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableFields()), new String[]{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName});
        mainObj.put("fields", fieldsArr);
        JSONArray summaryArr = new JSONArray();
        JSONObject summaryObj = new JSONObject();
        summaryObj.put("section", "Upload");
        summaryObj.put("progress", 100);
        summaryObj.put("signed", true);
        summaryObj.put("tooltip", "If URS Requirements were upload into the Procedures Definition repository");
        summaryArr.add(summaryObj);
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
        summaryArr.add(summaryObj);
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
        summaryArr.add(summaryObj);
        summaryObj = new JSONObject();
        summaryObj.put("section", "Deployed");
        summaryObj.put("progress", 90);
        summaryObj.put("signed", false);
        summaryObj.put("tooltip", "");
        summaryArr.add(summaryObj);
        mainObj.put("summary", summaryArr);
        return mainObj;
    }

    private static com.google.gson.JsonArray procInstanceViewsInRequirements() {
        return procModelArray(GlobalVariables.PROC_MANAGEMENT_SPECIAL_ROLE, SIZE_WHEN_CONSIDERED_MOBILE + 1);
    }

    private static JSONObject procInstanceDefinitionInRequirements(String procInstanceName) {
        JSONObject jMainObj = new JSONObject();
        JSONObject dbSingleRowToJsonObj = ClassReqProcedureQueries.dbSingleRowToJsonObj(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(),
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableFields()), new String[]{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName});
        jMainObj.put("procedure_info", dbSingleRowToJsonObj);
        
        String moduleName=dbSingleRowToJsonObj.get("module_name").toString();
        Integer moduleVersion=dbSingleRowToJsonObj.get("module_version").toString().length()>0?Integer.valueOf(dbSingleRowToJsonObj.get("module_version").toString()):-1;        
        
        SqlWhere wObj = new SqlWhere();
        wObj.addConstraint(TblsReqs.ModuleBusinessRules.MODULE_NAME,
                SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{moduleName}, null);        
        wObj.addConstraint(TblsReqs.ModuleBusinessRules.MODULE_VERSION,
                SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{moduleVersion}, null);        
        JSONArray dbRowsToJsonArr = new JSONArray();        
        dbRowsToJsonArr = ClassReqProcedureQueries.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.MODULE_BUSINESS_RULES,
                EnumIntTableFields.getTableFieldsFromString(TblsReqs.TablesReqs.MODULE_BUSINESS_RULES, new String[]{TblsReqs.ModuleBusinessRules.AREA.getName(),
            TblsReqs.ModuleBusinessRules.RULE_NAME.getName(), TblsReqs.ModuleBusinessRules.VALUES_LIST.getName(),
            TblsReqs.ModuleBusinessRules.PREREQUISITE.getName(), TblsReqs.ModuleBusinessRules.IS_MANDATORY.getName()}),
                wObj,
                new String[]{
                    TblsReqs.ModuleBusinessRules.AREA.getName(),
                    TblsReqs.ModuleBusinessRules.ORDER_NUMBER.getName(),
                    TblsReqs.ModuleBusinessRules.RULE_NAME.getName()
                },
                new String[]{},
                true);
        jMainObj.put("module_business_rules", dbRowsToJsonArr);
        jMainObj.put("process_accesses", ClassReqProcedureQueries.procAccessBlockInRequirements(procInstanceName));

        wObj = new SqlWhere();
        wObj.addConstraint(TblsReqs.ProcedureBusinessRules.PROC_INSTANCE_NAME,
                SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{procInstanceName}, null);
        jMainObj.put(TblsReqs.TablesReqs.PROCEDURE_RISK_ASSESSMENT.getTableName(), 
        dbRowsToJsonArr(procInstanceName, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_RISK_ASSESSMENT, TblsReqs.ProcedureRiskAssessment.values(), 
            wObj, null, null, false));

        jMainObj.put(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(), 
        dbRowsToJsonArr(procInstanceName, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, TblsReqs.ProcedureReqSolution.values(), 
            wObj, null, null, false));

        jMainObj.put(TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableName(), 
        dbRowsToJsonArr(procInstanceName, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS, TblsReqs.ProcedureUserRequirements.values(), 
            wObj, new String[]{TblsReqs.ProcedureUserRequirements.PARENT_CODE.getName(), TblsReqs.ProcedureUserRequirements.CODE.getName()}, null, false));
        
        JSONObject jViewsAccObj = new JSONObject();
        jViewsAccObj.put("roles_views", ClassReqProcedureQueries.procViewsBlockInRequirements(procInstanceName));
        jViewsAccObj.put("view_actions", ClassReqProcedureQueries.dbRowsGroupedToJsonArr(TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_ACTIONS.getViewName(),
                new String[]{TblsReqs.viewProcReqSolutionActions.WINDOW_NAME.getName(),
                    TblsReqs.viewProcReqSolutionActions.WINDOW_NAME.getName(),
                    TblsReqs.viewProcReqSolutionActions.PRETTY_EN.getName(), TblsReqs.viewProcReqSolutionActions.PRETTY_ES.getName()
                },
                new String[]{
                    TblsReqs.viewProcReqSolutionActions.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionActions.WINDOW_ACTION.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()
                },
                new Object[]{procInstanceName
                },
                new String[]{
                    TblsReqs.viewProcReqSolutionActions.ORDER_NUMBER.getName(),
                    TblsReqs.viewProcReqSolutionActions.ENTITY.getName(),
                    TblsReqs.viewProcReqSolutionActions.PRETTY_EN.getName()
                }
        ));
        jViewsAccObj.put("view_actions_en", ClassReqProcedureQueries.dbRowsGroupedToJsonArr(TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_ACTIONS.getViewName(),
                new String[]{TblsReqs.viewProcReqSolutionActions.WINDOW_LABEL_EN.getName(),
                    TblsReqs.viewProcReqSolutionActions.WINDOW_LABEL_EN.getName(),
                    TblsReqs.viewProcReqSolutionActions.PRETTY_EN.getName(), TblsReqs.viewProcReqSolutionActions.PRETTY_ES.getName()
                },
                new String[]{
                    TblsReqs.viewProcReqSolutionActions.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionActions.WINDOW_ACTION.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()
                },
                new Object[]{procInstanceName
                },
                new String[]{
                    TblsReqs.viewProcReqSolutionActions.ORDER_NUMBER.getName(),
                    TblsReqs.viewProcReqSolutionActions.ENTITY.getName(),
                    TblsReqs.viewProcReqSolutionActions.PRETTY_EN.getName()
                }
        ));
        jViewsAccObj.put("view_actions_es", ClassReqProcedureQueries.dbRowsGroupedToJsonArr(TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_ACTIONS.getViewName(),
                new String[]{TblsReqs.viewProcReqSolutionActions.WINDOW_LABEL_ES.getName(),
                    TblsReqs.viewProcReqSolutionActions.WINDOW_LABEL_ES.getName(),
                    TblsReqs.viewProcReqSolutionActions.PRETTY_EN.getName(), TblsReqs.viewProcReqSolutionActions.PRETTY_ES.getName()
                },
                new String[]{
                    TblsReqs.viewProcReqSolutionActions.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionActions.WINDOW_ACTION.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()
                },
                new Object[]{procInstanceName
                },
                new String[]{
                    TblsReqs.viewProcReqSolutionActions.ORDER_NUMBER.getName(),
                    TblsReqs.viewProcReqSolutionActions.ENTITY.getName(),
                    TblsReqs.viewProcReqSolutionActions.PRETTY_EN.getName()
                }
        ));

        jViewsAccObj.put(
                "view_sops", ClassReqProcedUserAndActions.viewsBySops(procInstanceName));

        jMainObj.put(
                "views_info", jViewsAccObj);

        jMainObj.put(
                "sops", ClassReqProcedureQueries.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableName(),
                        getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableFields()), new String[]{TblsReqs.ProcedureSopMetaData.PROC_INSTANCE_NAME.getName()
        },
                        new Object[]{procInstanceName
                        },
                        new String[]{
                            TblsReqs.ProcedureSopMetaData.SOP_NAME.getName()
                        },
                        null, true));

        JSONObject dbRowsGroupedToJsonArr = ClassReqProcedureQueries.dbRowsGroupedToJsonArr(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableName(),
                new String[]{TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.TABLE_NAME.getName(), TblsReqs.ProcedureModuleTables.DEFINITION_EN.getName(), TblsReqs.ProcedureModuleTables.DEFINITION_ES.getName(), TblsReqs.ProcedureModuleTables.IS_VIEW.getName()},
                new String[]{TblsReqs.ProcedureModuleTables.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName},
                new String[]{TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.ORDER_NUMBER.getName()});

        jMainObj.put(
                "tables", dbRowsGroupedToJsonArr);

        jMainObj.put(
                TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableFields(), ClassReqProcedureQueries.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(),
                        getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableFields()), new String[]{TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName()
        },
                        new Object[]{procInstanceName
                        },
                        new String[]{
                            TblsReqs.ProcedureReqSolution.ORDER_NUMBER.getName()
                        },
                        null, true));

        jMainObj.put(
                "frontend_proc_model", ClassReqProcedureQueries.feProcModel(procInstanceName));
        return jMainObj;
    }

    private static JSONArray procInstanceMasterDataInRequirements(String procInstanceName) {
        return ClassReqProcedureQueries.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MASTER_DATA.getTableName(),
                getAllFieldNames(TblsReqs.TablesReqs.PROC_MASTER_DATA.getTableFields()), new String[]{TblsReqs.ProcedureMasterData.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName},
                null, new String[]{TblsReqs.ProcedureMasterData.JSON_OBJ.getName()}, true);
    }

    private static JSONArray procInstanceFrontendTestingInRequirements(String procInstanceName) {
        return ClassReqProcedureQueries.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_FRONT_TESTING_WITNESS.getTableName(),
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_FRONT_TESTING_WITNESS.getTableFields()), new String[]{TblsReqs.ProcedureFrontendTestingWitness.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureFrontendTestingWitness.ACTIVE.getName()}, new Object[]{procInstanceName, true},
                new String[]{TblsReqs.ProcedureFrontendTestingWitness.ORDER_NUMBER.getName()}, new String[]{}, true);
    }

    private static JSONArray procInstanceManualsInRequirements(String procInstanceName) {
        return ClassReqProcedureQueries.dbRowsToJsonArr("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MANUALS.getTableName(),
                getAllFieldNames(TblsReqs.TablesReqs.PROC_MANUALS.getTableFields()), new String[]{TblsReqs.ProcedureManuals.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureManuals.ACTIVE.getName()}, new Object[]{procInstanceName, true},
                new String[]{TblsReqs.ProcedureManuals.ORDER_NUMBER.getName()}, new String[]{}, true);
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
                    scriptStepsList.add(curStepObj);
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

}
