/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.proceduredefinition;

import static com.labplanet.servicios.app.AppProcedureListAPI.LABEL_ARRAY_PROCEDURE_INSTANCES;
import static com.labplanet.servicios.app.AppProcedureListAPI.PROC_FLD_NAME;
import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.app.TestingRegressionUAT;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import com.labplanet.servicios.proceduredefinition.ReqProcedureEnums.ReqProcedureDefinitionAPIQueriesEndpoints;
import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsReqs;
import databases.TblsTesting;
import databases.features.Token;
import static functionaljavaa.requirement.ProcedureDefinitionQueries.*;
import functionaljavaa.user.UserProfile;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import static trazit.queries.QueryUtilities.getTableData;
import trazit.session.ProcedureRequestSession;

public class ReqProcedureDefinitionQueries extends HttpServlet {
    
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    public static final String ERRORMSG_ERROR_STATUS_CODE="Error Status Code";
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
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);
        String language = LPFrontEnd.setLanguage(request); 

        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;          
            }            
            ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(request, response, false);
            
            String actionName = procReqSession.getActionName();
            String finalToken = procReqSession.getTokenString();
            
            ReqProcedureDefinitionAPIQueriesEndpoints endPoint = null;
            try{
                endPoint = ReqProcedureDefinitionAPIQueriesEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;                   
            }
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}          
        try (PrintWriter out = response.getWriter()) {
            String procInstanceName = procReqSession.getProcedureInstance();
            switch (endPoint){
            case ALL_PROCEDURES_AND_INSTANCE_LIST:
                String[] fieldsToRetrieveScripts=EnumIntTableFields.getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableFields());
                Object[][] list=getTableData(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM.getTableName(), 
                    "", EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM.getTableFields()), 
                    new String[]{TblsEnvMonitConfig.MicroOrganism.NAME.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{}, 
                    new String[]{TblsEnvMonitConfig.MicroOrganism.NAME.getName()});     
                JSONArray jArr=new JSONArray();
                for (Object[] curRec: list){
                    JSONObject jObj= LPJson.convertArrayRowToJSONObject(fieldsToRetrieveScripts, curRec);
                    jArr.add(jObj);
                }   
                LPFrontEnd.servletReturnSuccess(request, response, jArr );
                return;
            case ALL_PROCEDURES_DEFINITION:
                JSONObject jMainObj = new JSONObject();
                String mainObjectName="all_platform_procedures_list";                                 
                fieldsToRetrieveScripts=new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName(), 
                    TblsReqs.ProcedureInfo.LOCKED_FOR_ACTIONS.getName(), TblsReqs.ProcedureInfo.NAVIGATION_ICON_NAME.getName()};
                Object[][] procAndInstanceArr = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(), 
                    new String[]{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, 
                    new Object[]{}, 
                    fieldsToRetrieveScripts,
                    fieldsToRetrieveScripts);
                String curProcName="";
                JSONArray proceduresList = new JSONArray();
                for (Object[] curProc: procAndInstanceArr){
                    JSONObject curProcObj= LPJson.convertArrayRowToJSONObject(fieldsToRetrieveScripts, curProc);
                    Integer valuePosicInArray = LPArray.valuePosicInArray(fieldsToRetrieveScripts, TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName());
                    if (valuePosicInArray>-1){
                        JSONObject procInstanceDefinition = procInstanceDefinitionInRequirements(curProc[valuePosicInArray].toString());
                        curProcObj.put("definition", procInstanceDefinition);
                    }
                    proceduresList.add(curProcObj);
                    
                }
                jMainObj.put(mainObjectName, proceduresList);
                LPFrontEnd.servletReturnSuccess(request, response, jMainObj);                                    
                return;                 
            case ALL_USER_PROCEDURES_DEFINITION:
                jMainObj = new JSONObject();
                mainObjectName="all_user_procedures_list";                 
                JSONObject jsonObj = new JSONObject();
                Token token = new Token(finalToken);
                String rolName = token.getUserRole();
                UserProfile usProf = new UserProfile();
                Object[] allUserProcedureInstancePrefix = usProf.getAllUserProcedurePrefix(token.getUserName());
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedureInstancePrefix[0].toString())){
                    LPFrontEnd.servletReturnSuccess(request, response, new JSONObject());
                    return;                                                
                }
                String[] procFldNameArray = PROC_FLD_NAME.split("\\|");
                JSONArray procedures = new JSONArray();     
                for (Object curProcInst: allUserProcedureInstancePrefix){
                    if (!"proc_management".equalsIgnoreCase(curProcInst.toString())){
                        JSONObject procedure = new JSONObject();
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
            case ONE_PROCEDURE_DEFINITION:
                JSONObject schemaContentObj = new JSONObject(); 

                Rdbms.closeRdbms();  
                JSONObject mainRespDef= new JSONObject();
                mainRespDef.put("definition", procInstanceDefinitionInRequirements(procInstanceName));
                LPFrontEnd.servletReturnSuccess(request, response, mainRespDef);
                return;                                
            case ENABLE_ACTIONS_AND_ROLES:  
                LPFrontEnd.servletReturnSuccess(request, response, 
                        getProcBusinessRulesQueriesInfo(procInstanceName, ProcBusinessRulesQueries.PROCEDURE_ACTIONS_AND_ROLES.toString()));
                return;
            case ALL_PROCEDURE_TESTING_SCRIPT:
                procInstanceName=argValues[2].toString();
                fieldsToRetrieveScripts=TblsTesting.getScriptPublicFieldNames(procInstanceName);
                String repositoryName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
                Object[][] scriptsTblInfo = Rdbms.getRecordFieldsByFilter(repositoryName, TblsTesting.TablesTesting.SCRIPT.getTableName(), 
                    new String[]{TblsTesting.Script.SCRIPT_ID.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, 
                    new String[]{}, fieldsToRetrieveScripts, new String[]{TblsTesting.Script.SCRIPT_ID.getName()});
                jMainObj = new JSONObject();
                JSONObject jObj = new JSONObject();                   
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptsTblInfo[0][0].toString())){                    
                    jObj.put("status", "Not found ANY script");
                    //return;
                }else{    
                    JSONArray scriptsList = new JSONArray();
                    Integer scriptIdPosic=LPArray.valuePosicInArray(fieldsToRetrieveScripts, TblsTesting.Script.SCRIPT_ID.getName());
                    for (Object[] curTest: scriptsTblInfo){
                        JSONObject curTestObj=getScriptWithSteps(Integer.valueOf(curTest[scriptIdPosic].toString()), procInstanceName, fieldsToRetrieveScripts, curTest);
/*                        JSONObject curTestObj= LPJson.convertArrayRowToJSONObject(fieldsToRetrieveScripts, curTest);
                        Integer scriptIdPosic=LPArray.valuePosicInArray(fieldsToRetrieveScripts, TblsTesting.Script.SCRIPT_ID.getName());
                        if (scriptIdPosic>-1){
                            Object[][] scriptStepsTblInfo = Rdbms.getRecordFieldsByFilter(repositoryName, TblsTesting.TablesTesting.SCRIPT_STEPS.getTableName(), 
                                new String[]{TblsTesting.ScriptSteps.SCRIPT_ID.getName()}, 
                                new Object[]{Integer.valueOf(curTest[scriptIdPosic].toString())},
                                fieldsToRetrieveScriptSteps,
                                new String[]{TblsTesting.ScriptSteps.STEP_ID.getName()});
                            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptStepsTblInfo[0][0].toString())){                    
                                JSONArray scriptStepsList = new JSONArray();
                                for (Object[] curStep: scriptStepsTblInfo){
                                    JSONObject curStepObj= LPJson.convertArrayRowToJSONObject(fieldsToRetrieveScriptSteps, curStep);
                                    Integer actionPosic=LPArray.valuePosicInArray(fieldsToRetrieveScriptSteps, TblsTesting.ScriptSteps.ARGUMENT_01.getName());
                                    if (actionPosic>-1)
                                        actionsList=LPArray.addValueToArray1D(actionsList, LPNulls.replaceNull(curStep[actionPosic]).toString());
                                    scriptStepsList.add(curStepObj);
                                }
                                curTestObj.put("steps", scriptStepsList);
                            }
                        }
*/                        
                        scriptsList.add(curTestObj);                        
                    }
                    //actionsList=LPArray.getUniquesArray(actionsList);
                    
                    jMainObj.put("scripts_list", scriptsList);
                }
                if (!jObj.isEmpty())
                    jMainObj.put("proc_testing_script_summary", jObj);                
//                if (jMainObj.isEmpty())
//                    jMainObj.put("proc_testing_script_summary", "");
                LPFrontEnd.servletReturnSuccess(request, response, jMainObj);                  
                return;

            case PROC_DEPLOY_TESTING_COVERAGE_SUMMARY:
                String procedureName = argValues[0].toString();
                Integer procedureVersion = (Integer) argValues[1];  
                procInstanceName=argValues[2].toString();       
                jObj = new JSONObject();                
                jMainObj = new JSONObject();
                mainObjectName="testing_coverage_summary"; 
                Object[] actionDiagnosesAll = TestingRegressionUAT.procedureRepositoryMirrors(procInstanceName);
                Object[] allMismatchesDiagn=(Object[]) actionDiagnosesAll[0];
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allMismatchesDiagn[0].toString())){
                    Object[][] allMismatches= (Object[][])actionDiagnosesAll[1];
                    jObj.put("Error_found", "Not mirrors");
                    jArr=new JSONArray();                                        
                    Object[] fldNamesObj=(Object[]) actionDiagnosesAll[1];
                    Object[][] mismatchTables=(Object[][])actionDiagnosesAll[1];
                    for (int i=1;i<mismatchTables.length;i++){
                        jArr.add(LPJson.convertArrayRowToJSONObject(LPArray.convertObjectArrayToStringArray((Object[]) mismatchTables[0]), (Object[]) mismatchTables[i]));
                    }                    
                    JSONArray jTblColsArr=new JSONArray();
                    for (Object curCol: (Object[]) mismatchTables[0]){
                        jTblColsArr.add(curCol.toString());
                    }
                    jObj=new JSONObject();
                    JSONObject jerrDetObj=new JSONObject();
                    jerrDetObj.put("columns", jTblColsArr);
                    jerrDetObj.put("data", jArr);
                    jerrDetObj.put("error_en", "It is required that both data repositories are mirror");
                    jerrDetObj.put("error_es", "Es obligatorio que ambos repositoros de datos sean espejo");
                    jObj.put("not_mirror", jerrDetObj);
                    LPFrontEnd.servletReturnSuccess(request, response, jObj);                    
                    return;
                }
                jMainObj = new JSONObject();
                mainObjectName="all_testing_coverage_list";                                 
                
                String[] fieldsToGet = EnumIntTableFields.getAllFieldNames(TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableFields());
                procAndInstanceArr = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getRepositoryName()), TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableName(), 
                       new String[]{TblsTesting.ScriptsCoverage.COVERAGE_ID.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{}, 
                       fieldsToGet, new String[]{TblsTesting.ScriptsCoverage.COVERAGE_ID.getName()});                
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procAndInstanceArr[0][0].toString()))
                    jMainObj.put(mainObjectName, "no data found");
                else{    
                    curProcName="";
                    proceduresList = new JSONArray();
                    for (Object[] curProc: procAndInstanceArr){
                        JSONObject curProcObj= LPJson.convertArrayRowToJSONObject(fieldsToGet, curProc);
                        Integer valuePosicInArray = LPArray.valuePosicInArray(fieldsToGet, TblsTesting.ScriptsCoverage.ENDPOINTS_EXCLUDE_LIST.getName());
                        if (valuePosicInArray>-1){
                            curProcObj.replace(TblsTesting.ScriptsCoverage.ENDPOINTS_EXCLUDE_LIST.getName(), 
                                LPJson.convertToJSONArray(curProc[valuePosicInArray].toString().split("\\|")));
                        }
                        valuePosicInArray = LPArray.valuePosicInArray(fieldsToGet, TblsTesting.ScriptsCoverage.SCRIPT_IDS_LIST.getName());
                        if (valuePosicInArray>-1){
                            curProcObj.replace(TblsTesting.ScriptsCoverage.SCRIPT_IDS_LIST.getName(), 
                                LPJson.convertToJSONArray(curProc[valuePosicInArray].toString().split("\\|")));
                            JSONArray scriptDetail=new JSONArray();
                            for (String curId: curProc[valuePosicInArray].toString().split("\\|")){
                                JSONObject curTestObj=getScriptWithSteps(Integer.valueOf(curId), procInstanceName, null, null);
                                scriptDetail.add(curTestObj);
                            }
                            curProcObj.put("scripts_detail", scriptDetail);
                        }
                        valuePosicInArray = LPArray.valuePosicInArray(fieldsToGet, TblsTesting.ScriptsCoverage.ENDPOINTS_COVERAGE_DETAIL.getName());
                        if (valuePosicInArray>-1){                       
                            curProcObj.replace(TblsTesting.ScriptsCoverage.ENDPOINTS_COVERAGE_DETAIL.getName(), 
                                LPJson.convertToJsonObjectStringedValue(curProc[valuePosicInArray].toString()));
                        }
                        valuePosicInArray = LPArray.valuePosicInArray(fieldsToGet, TblsTesting.ScriptsCoverage.BUS_RULES_COVERAGE_DETAIL.getName());
                        if (valuePosicInArray>-1){                       
                            curProcObj.replace(TblsTesting.ScriptsCoverage.BUS_RULES_COVERAGE_DETAIL.getName(), 
                                LPJson.convertToJsonObjectStringedValue(curProc[valuePosicInArray].toString()));
                        }
                        valuePosicInArray = LPArray.valuePosicInArray(fieldsToGet, TblsTesting.ScriptsCoverage.MSG_COVERAGE_DETAIL.getName());
                        if (valuePosicInArray>-1){                       
                            curProcObj.replace(TblsTesting.ScriptsCoverage.MSG_COVERAGE_DETAIL.getName(), 
                                LPJson.convertToJsonObjectStringedValue(curProc[valuePosicInArray].toString()));
                        }
                        proceduresList.add(curProcObj);                    
                    }
                    jMainObj.put(mainObjectName, proceduresList);
                }
                LPFrontEnd.servletReturnSuccess(request, response, jMainObj);                                    
                return;                 
                 
            default:                
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            }
        }catch(Exception e){
            String errMessage = e.getMessage();
            String[] errObject = new String[0];
            errObject = LPArray.addValueToArray1D(errObject, ERRORMSG_ERROR_STATUS_CODE+": "+HttpServletResponse.SC_BAD_REQUEST);
            errObject = LPArray.addValueToArray1D(errObject, "This call raised one unhandled exception. Error:"+errMessage);     
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);    
        } finally {
            procReqSession.killIt();
            try {
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
         try {
        processRequest(request, response);
         }catch(ServletException|IOException e){Logger.getLogger(e.getMessage());}
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

    private static JSONObject procInstanceDefinitionInRequirements(String procInstanceName){
        JSONArray mainArr = new JSONArray(); 
        JSONObject jMainObj=new JSONObject();            
        String[] sectionsArr=new String[]{ProcBusinessRulesQueries.PROCEDURE_MAIN_INFO.toString(), ProcBusinessRulesQueries.PROCEDURE_ACTIONS_AND_ROLES.toString(),
            ProcBusinessRulesQueries.PROCEDURE_SAMPLE_AUDIT_LEVEL.toString(),
            ProcBusinessRulesQueries.PROCEDURE_USER_SOP_CERTIFICATION_LEVEL.toString(), ProcBusinessRulesQueries.PROGRAM_CORRECTIVE_ACTION.toString(),
            ProcBusinessRulesQueries.CHANGE_OF_CUSTODY.toString(), ProcBusinessRulesQueries.SAMPLE_STAGES_TIMING_CAPTURE.toString(),
            ProcBusinessRulesQueries.SAMPLE_INCUBATION.toString(),ProcBusinessRulesQueries.PROCEDURE_ALL_PROC_USERS_ROLES.toString(),
            ProcBusinessRulesQueries.PROCEDURE_SAMPLE_STAGES.toString(),ProcBusinessRulesQueries.PROCEDURE_ENCRYPTION_TABLES_AND_FIELDS.toString()};
        //for (String currSection: sectionsArr)
        //    mainArr.add(getProcBusinessRulesQueriesInfo(procInstanceName, currSection));
        jMainObj.put("procedure_info", ClassReqProcedureQueries.dbSingleRowToJsonObj(procInstanceName, TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(), 
            getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableFields()), new String[]{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName}));

        jMainObj.put("business_rules", ClassReqProcedureQueries.dbRowsGroupedToJsonArr(procInstanceName, TblsReqs.TablesReqs.PROC_BUS_RULES.getTableName(), 
            new String[]{TblsReqs.ProcedureBusinessRules.CATEGORY.getName(), 
                TblsReqs.ProcedureBusinessRules.RULE_NAME.getName(), TblsReqs.ProcedureBusinessRules.RULE_VALUE.getName(),
                TblsReqs.ProcedureBusinessRules.EXPLANATION.getName(), TblsReqs.ProcedureBusinessRules.VALUES_ALLOWED.getName()},
            new String[]{TblsReqs.ProcedureBusinessRules.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureBusinessRules.CATEGORY.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.NOT_IN.getSqlClause(), SqlStatement.WHERECLAUSE_TYPES.OR.getSqlClause()+" "+TblsReqs.ProcedureBusinessRules.CATEGORY.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
            new Object[]{procInstanceName, "ACCESS"},
            new String[]{TblsReqs.ProcedureBusinessRules.CATEGORY.getName(), TblsReqs.ProcedureBusinessRules.ORDER_NUMBER.getName(), TblsReqs.ProcedureBusinessRules.RULE_NAME.getName()}));

        jMainObj.put("process_accesses", ClassReqProcedureQueries.procAccessBlockInRequirements(procInstanceName));

        jMainObj.put("sops", ClassReqProcedureQueries.dbRowsToJsonArr(procInstanceName, TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableName(), 
            getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableFields()), new String[]{TblsReqs.ProcedureSopMetaData.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName},
            new String[]{TblsReqs.ProcedureSopMetaData.SOP_NAME.getName()}, null));

        jMainObj.put("tables", ClassReqProcedureQueries.dbRowsGroupedToJsonArr(procInstanceName, TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableName(), 
            new String[]{TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.TABLE_NAME.getName()}, 
            new String[]{TblsReqs.ProcedureModuleTables.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName},
            new String[]{TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName()}));

        jMainObj.put("user_requirements", ClassReqProcedureQueries.dbRowsToJsonArr(procInstanceName, TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableName(), 
            getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableFields()), new String[]{TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName},
            new String[]{TblsReqs.ProcedureUserRequirements.ORDER_NUMBER.getName()}, null));

        jMainObj.put("user_requirements_events", ClassReqProcedureQueries.dbRowsToJsonArr(procInstanceName, TblsReqs.TablesReqs.PROCEDURE_USER_REQS_EVENTS.getTableName(), 
            getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_USER_REQS_EVENTS.getTableFields()), new String[]{TblsReqs.ProcedureUserRequirementsEvents.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName},
            new String[]{TblsReqs.ProcedureUserRequirementsEvents.ORDER_NUMBER.getName()}, null));
        jMainObj.put("master_data", ClassReqProcedureQueries.dbRowsToJsonArr(procInstanceName, TblsReqs.TablesReqs.PROC_MASTER_DATA.getTableName(), 
            getAllFieldNames(TblsReqs.TablesReqs.PROC_MASTER_DATA.getTableFields()), new String[]{TblsReqs.ProcedureMasterData.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName},
            null, new String[]{TblsReqs.ProcedureMasterData.JSON_OBJ.getName()}));

        jMainObj.put("frontend_proc_model", ClassReqProcedureQueries.feProcModel(procInstanceName));
        return jMainObj;
    }
    
    private JSONObject getScriptWithSteps(Integer scriptId, String procInstanceName, String[] fieldsToRetrieveScripts, Object[] curTest){
        JSONObject curTestObj=new JSONObject();
        String[] actionsList=new String[]{};

        String repositoryName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
        if (curTest==null){
            fieldsToRetrieveScripts=TblsTesting.getScriptPublicFieldNames(procInstanceName);
            String[] fieldsToRetrieveScriptSteps=  EnumIntTableFields.getAllFieldNames(TblsTesting.TablesTesting.SCRIPT_STEPS);  
            Object[][] scriptsTblInfo = Rdbms.getRecordFieldsByFilter(repositoryName, TblsTesting.TablesTesting.SCRIPT.getTableName(), 
                new String[]{TblsTesting.Script.SCRIPT_ID.getName()}, new Object[]{scriptId}, 
                fieldsToRetrieveScripts, new String[]{TblsTesting.Script.SCRIPT_ID.getName()});
            curTest=scriptsTblInfo[0];
        }
        curTestObj= LPJson.convertArrayRowToJSONObject(fieldsToRetrieveScripts, curTest);
        String[] fieldsToRetrieveScriptSteps=  EnumIntTableFields.getAllFieldNames(TblsTesting.TablesTesting.SCRIPT_STEPS);              
        Integer scriptIdPosic=LPArray.valuePosicInArray(fieldsToRetrieveScripts, TblsTesting.Script.SCRIPT_ID.getName());
        if (scriptIdPosic>-1){
            Object[][] scriptStepsTblInfo = Rdbms.getRecordFieldsByFilter(repositoryName, TblsTesting.TablesTesting.SCRIPT_STEPS.getTableName(), 
                new String[]{TblsTesting.ScriptSteps.SCRIPT_ID.getName()}, 
                new Object[]{Integer.valueOf(curTest[scriptIdPosic].toString())},
                fieldsToRetrieveScriptSteps,
                new String[]{TblsTesting.ScriptSteps.STEP_ID.getName()});
            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptStepsTblInfo[0][0].toString())){                    
                JSONArray scriptStepsList = new JSONArray();
                for (Object[] curStep: scriptStepsTblInfo){
                    JSONObject curStepObj= LPJson.convertArrayRowToJSONObject(fieldsToRetrieveScriptSteps, curStep);
                    Integer actionPosic=LPArray.valuePosicInArray(fieldsToRetrieveScriptSteps, TblsTesting.ScriptSteps.ACTION_NAME.getName());
                    if (actionPosic>-1)
                        actionsList=LPArray.addValueToArray1D(actionsList, LPNulls.replaceNull(curStep[actionPosic]).toString());
                    scriptStepsList.add(curStepObj);
                }
                curTestObj.put("steps", scriptStepsList);
            }
        }
        return curTestObj;
    }
}
