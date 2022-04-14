/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.proceduredefinition;

import static com.labplanet.servicios.app.AppProcedureListAPI.LABEL_ARRAY_PROCEDURES;
import static com.labplanet.servicios.app.AppProcedureListAPI.PROC_FLD_NAME;
import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.app.TestingRegressionUAT;
import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsReqs;
import databases.Token;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.requirement.ProcedureDefinitionQueries.*;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import functionaljavaa.user.UserProfile;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ProcedureDefinitionfrontend extends HttpServlet {
    public enum ProcedureDefinitionAPIfrontendEndpoints implements EnumIntEndpoints{
        /**
         *
         */
        ALL_PROCEDURES_DEFINITION("ALL_PROCEDURES_DEFINITION", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        ALL_PROCEDURE_DEFINITION("ALL_PROCEDURE_DEFINITION", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        ONE_PROCEDURE_DEFINITION("ONE_PROCEDURE_DEFINITION", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        ENABLE_ACTIONS_AND_ROLES("ENABLE_ACTIONS_AND_ROLES", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        PROC_DEPLOY_CHECKER("PROC_DEPLOY_CHECKER", "deployRequirements_success", 
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionAPI.ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(ProcedureDefinitionAPI.ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments(ProcedureDefinitionAPI.ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments(ProcedureDefinitionAPI.ProcedureDefinitionpParametersEndpoints.DB_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects),
        ;
        private ProcedureDefinitionAPIfrontendEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
            this.outputObjectTypes=outputObjectTypes;            
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){                
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }  
            hm.put(request, argValues);            
            return hm;
        }        
        public String getName(){return this.name;}
        public String getSuccessMessageCode(){return this.successMessageCode;}           
        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        public LPAPIArguments[] getArguments() {return arguments;}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }
    
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
            
            String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            
            ProcedureDefinitionAPIfrontendEndpoints endPoint = null;
            try{
                endPoint = ProcedureDefinitionAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;                   
            }
            ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(request, response, false);
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}          
        try (PrintWriter out = response.getWriter()) {
//            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}   

            String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME);   
//BOOOORRRRRAAAAAAAAAAAAAAAR ************************************
            if (!"em-demo-a".equalsIgnoreCase(procInstanceName))
                procInstanceName="proc-deploy";
//BOOOORRRRRAAAAAAAAAAAAAAAR ************************************

            switch (endPoint){
            case ALL_PROCEDURES_DEFINITION:
                JSONObject jsonObj = new JSONObject();
                
                Token token = new Token(finalToken);

                String rolName = token.getUserRole();
                UserProfile usProf = new UserProfile();
                Object[] allUserProcedurePrefix = usProf.getAllUserProcedurePrefix(token.getUserName());
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0].toString())){
                    LPFrontEnd.servletReturnSuccess(request, response, new JSONObject());
                    return;                                                
                }
                String[] procFldNameArray = PROC_FLD_NAME.split("\\|");

                JSONArray procedures = new JSONArray();     
                for (Object curProc: allUserProcedurePrefix){
                    JSONObject procedure = new JSONObject();
                    //procedure.put("definition", procedureDefinition(curProc.toString()));
                    procedures.add(procedureDefinition(curProc.toString()));
                }
                
                JSONObject proceduresList = new JSONObject();
                proceduresList.put(LABEL_ARRAY_PROCEDURES, procedures);
                jsonObj.put("procedures_list", proceduresList);
                //return proceduresList;                
                LPFrontEnd.servletReturnSuccess(request, response, jsonObj);
                return;                                                
            case ALL_PROCEDURE_DEFINITION:
            case ONE_PROCEDURE_DEFINITION:
                JSONObject schemaContentObj = new JSONObject(); 

                Rdbms.closeRdbms();  
                JSONObject mainRespDef= new JSONObject();
                mainRespDef.put("definition", procedureDefinition(procInstanceName));
                LPFrontEnd.servletReturnSuccess(request, response, mainRespDef);
                return;                                
            case ENABLE_ACTIONS_AND_ROLES:  
                LPFrontEnd.servletReturnSuccess(request, response, 
                        getProcBusinessRulesQueriesInfo(procInstanceName, ProcBusinessRulesQueries.PROCEDURE_ACTIONS_AND_ROLES.toString()));
                return;
            case PROC_DEPLOY_CHECKER:
                String procedureName=argValues[0].toString();
                Integer procedureVersion = (Integer) argValues[1];  
                procInstanceName=argValues[2].toString();       
                Object[] actionDiagnosesAll = TestingRegressionUAT.procedureRepositoryMirrors(procInstanceName);
                Rdbms.closeRdbms();                    
                JSONObject jObj=new JSONObject();
                String[] actionDiagnoses=(String[]) actionDiagnosesAll[0];
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                    jObj.put("All mirrored", "Success");
                    LPFrontEnd.servletReturnSuccess(request, response, jObj);
                }else{
                    if (actionDiagnosesAll.length==17){
                        jObj.put("errors found", "");
                        LPFrontEnd.servletReturnSuccess(request, response, jObj);
                    }else{                             
                        JSONArray jArr=new JSONArray();
                        Object[] fldNamesObj=(Object[]) actionDiagnosesAll[1];
                        String[] fldNames=new String[fldNamesObj.length];
                        for (int i=0;i<fldNamesObj.length;i++){
                            fldNames[i]=fldNamesObj[i].toString();
                        }                           
                        Object[] mismatchTables=(Object[])actionDiagnosesAll[2];
                        for (int i=8;i<mismatchTables.length;i++){
                            jArr.add(LPJson.convertArrayRowToJSONObject(fldNames, (Object[]) mismatchTables[i]));

                        }
                        jObj=new JSONObject();
                        jObj.put("fields expected to be mirrored", jArr);
                        LPFrontEnd.servletReturnSuccess(request, response, jObj);
                    }
                }
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

    private static JSONObject procedureDefinition(String procInstanceName){
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
        jMainObj.put("procedure_info", ClassProcedureQueries.dbSingleRowToJsonObj(procInstanceName, TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(), 
            getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableFields()), new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName()}, new Object[]{procInstanceName}));

        jMainObj.put("business_rules", ClassProcedureQueries.dbRowsGroupedToJsonArr(procInstanceName, TblsReqs.TablesReqs.PROC_BUS_RULES.getTableName(), 
            new String[]{TblsReqs.ProcedureBusinessRules.CATEGORY.getName(), 
                TblsReqs.ProcedureBusinessRules.RULE_NAME.getName(), TblsReqs.ProcedureBusinessRules.RULE_VALUE.getName(),
                TblsReqs.ProcedureBusinessRules.EXPLANATION.getName(), TblsReqs.ProcedureBusinessRules.VALUES_ALLOWED.getName()},
            new String[]{TblsReqs.ProcedureBusinessRules.PROCEDURE_NAME.getName(), TblsReqs.ProcedureBusinessRules.CATEGORY.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.NOT_IN.getSqlClause(), SqlStatement.WHERECLAUSE_TYPES.OR.getSqlClause()+" "+TblsReqs.ProcedureBusinessRules.CATEGORY.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
            new Object[]{procInstanceName, "ACCESS"},
            new String[]{TblsReqs.ProcedureBusinessRules.CATEGORY.getName(), TblsReqs.ProcedureBusinessRules.ORDER_NUMBER.getName(), TblsReqs.ProcedureBusinessRules.RULE_NAME.getName()}));

        jMainObj.put("process_accesses", ClassProcedureQueries.procAccessBlock(procInstanceName));

        jMainObj.put("sops", ClassProcedureQueries.dbRowsToJsonArr(procInstanceName, TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableName(), 
            getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableFields()), new String[]{TblsReqs.ProcedureSopMetaData.PROCEDURE_NAME.getName()}, new Object[]{procInstanceName},
            new String[]{TblsReqs.ProcedureSopMetaData.SOP_NAME.getName()}, null));

        jMainObj.put("tables", ClassProcedureQueries.dbRowsGroupedToJsonArr(procInstanceName, TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableName(), 
            new String[]{TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.TABLE_NAME.getName()}, 
            new String[]{TblsReqs.ProcedureModuleTables.PROCEDURE_NAME.getName()}, new Object[]{procInstanceName},
            new String[]{TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName()}));

        jMainObj.put("user_requirements", ClassProcedureQueries.dbRowsToJsonArr(procInstanceName, TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableName(), 
            getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableFields()), new String[]{TblsReqs.ProcedureUserRequirements.PROCEDURE_NAME.getName()}, new Object[]{procInstanceName},
            new String[]{TblsReqs.ProcedureUserRequirements.ORDER_NUMBER.getName()}, null));

        jMainObj.put("user_requirements_events", ClassProcedureQueries.dbRowsToJsonArr(procInstanceName, TblsReqs.TablesReqs.PROCEDURE_USER_REQS_EVENTS.getTableName(), 
            getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_USER_REQS_EVENTS.getTableFields()), new String[]{TblsReqs.ProcedureUserRequirementsEvents.PROCEDURE_NAME.getName()}, new Object[]{procInstanceName},
            new String[]{TblsReqs.ProcedureUserRequirementsEvents.ORDER_NUMBER.getName()}, null));

        jMainObj.put("master_data", ClassProcedureQueries.dbRowsToJsonArr(procInstanceName, TblsReqs.TablesReqs.PROC_MASTER_DATA.getTableName(), 
            getAllFieldNames(TblsReqs.TablesReqs.PROC_MASTER_DATA.getTableFields()), new String[]{TblsReqs.ProcedureMasterData.PROCEDURE_NAME.getName()}, new Object[]{procInstanceName},
            null, new String[]{TblsReqs.ProcedureMasterData.JSON_OBJ.getName()}));

        jMainObj.put("frontend_proc_model", ClassProcedureQueries.feProcModel(procInstanceName));
        return jMainObj;
    }
}
