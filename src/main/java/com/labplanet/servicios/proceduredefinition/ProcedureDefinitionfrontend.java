/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.proceduredefinition;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsReqs;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.requirement.ProcedureDefinitionQueries.*;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
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
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
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
        ALL_PROCEDURE_DEFINITION("ALL_PROCEDURE_DEFINITION", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        ENABLE_ACTIONS_AND_ROLES("ENABLE_ACTIONS_AND_ROLES", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
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
    
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
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
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                return;          
            }            
            
            String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            
            ProcedureDefinitionAPIfrontendEndpoints endPoint = null;
            try{
                endPoint = ProcedureDefinitionAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
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

            JSONArray mainArr = new JSONArray(); 
            JSONObject jMainObj=new JSONObject();            
            switch (endPoint){
            case ALL_PROCEDURE_DEFINITION:
                JSONObject schemaContentObj = new JSONObject(); 
                String[] sectionsArr=new String[]{ProcBusinessRulesQueries.PROCEDURE_MAIN_INFO.toString(), ProcBusinessRulesQueries.PROCEDURE_ACTIONS_AND_ROLES.toString(),
                    ProcBusinessRulesQueries.PROCEDURE_SAMPLE_AUDIT_LEVEL.toString(),
                    ProcBusinessRulesQueries.PROCEDURE_USER_SOP_CERTIFICATION_LEVEL.toString(), ProcBusinessRulesQueries.PROGRAM_CORRECTIVE_ACTION.toString(),
                    ProcBusinessRulesQueries.CHANGE_OF_CUSTODY.toString(), ProcBusinessRulesQueries.SAMPLE_STAGES_TIMING_CAPTURE.toString(),
                    ProcBusinessRulesQueries.SAMPLE_INCUBATION.toString(),ProcBusinessRulesQueries.PROCEDURE_ALL_PROC_USERS_ROLES.toString(),
                    ProcBusinessRulesQueries.PROCEDURE_SAMPLE_STAGES.toString(),ProcBusinessRulesQueries.PROCEDURE_ENCRYPTION_TABLES_AND_FIELDS.toString()};
                //for (String currSection: sectionsArr)
                //    mainArr.add(getProcBusinessRulesQueriesInfo(procInstanceName, currSection));
                jMainObj.put("procedure_info", ClassProcedureQueries.dbSingleRowToJsonObj(procInstanceName, TblsReqs.ProcedureInfo.TBL.getName(), 
                    TblsReqs.ProcedureInfo.getAllFieldNames(), new String[]{TblsReqs.ProcedureInfo.FLD_PROCEDURE_NAME.getName()}, new Object[]{procInstanceName}));

                jMainObj.put("business_rules", ClassProcedureQueries.dbRowsGroupedToJsonArr(procInstanceName, TblsReqs.ProcedureBusinessRules.TBL.getName(), 
                    new String[]{TblsReqs.ProcedureBusinessRules.FLD_CATEGORY.getName(), 
                        TblsReqs.ProcedureBusinessRules.FLD_RULE_NAME.getName(), TblsReqs.ProcedureBusinessRules.FLD_RULE_VALUE.getName(),
                        TblsReqs.ProcedureBusinessRules.FLD_EXPLANATION.getName(), TblsReqs.ProcedureBusinessRules.FLD_VALUES_ALLOWED.getName()},
                    new String[]{TblsReqs.ProcedureBusinessRules.FLD_PROCEDURE_NAME.getName(), TblsReqs.ProcedureBusinessRules.FLD_CATEGORY.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.NOT_IN.getSqlClause(), SqlStatement.WHERECLAUSE_TYPES.OR.getSqlClause()+" "+TblsReqs.ProcedureBusinessRules.FLD_CATEGORY.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                    new Object[]{procInstanceName, "ACCESS"},
                    new String[]{TblsReqs.ProcedureBusinessRules.FLD_CATEGORY.getName(), TblsReqs.ProcedureBusinessRules.FLD_ORDER_NUMBER.getName(), TblsReqs.ProcedureBusinessRules.FLD_RULE_NAME.getName()}));
                
                jMainObj.put("process_accesses", ClassProcedureQueries.procAccessBlock(procInstanceName));

                jMainObj.put("sops", ClassProcedureQueries.dbRowsToJsonArr(procInstanceName, TblsReqs.ProcedureSopMetaData.TBL.getName(), 
                    TblsReqs.ProcedureSopMetaData.getAllFieldNames(), new String[]{TblsReqs.ProcedureSopMetaData.FLD_PROCEDURE_NAME.getName()}, new Object[]{procInstanceName},
                    new String[]{TblsReqs.ProcedureSopMetaData.FLD_SOP_NAME.getName()}, null));

                jMainObj.put("tables", ClassProcedureQueries.dbRowsGroupedToJsonArr(procInstanceName, TblsReqs.ProcedureModuleTablesAndFields.TBL.getName(), 
                    new String[]{TblsReqs.ProcedureModuleTablesAndFields.FLD_SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTablesAndFields.FLD_TABLE_NAME.getName()}, 
                    new String[]{TblsReqs.ProcedureModuleTablesAndFields.FLD_PROCEDURE_NAME.getName()}, new Object[]{procInstanceName},
                    new String[]{TblsReqs.ProcedureModuleTablesAndFields.FLD_SCHEMA_NAME.getName()}));

                jMainObj.put("user_requirements", ClassProcedureQueries.dbRowsToJsonArr(procInstanceName, TblsReqs.ProcedureUserRequirements.TBL.getName(), 
                    TblsReqs.ProcedureUserRequirements.getAllFieldNames(), new String[]{TblsReqs.ProcedureUserRequirements.FLD_PROCEDURE_NAME.getName()}, new Object[]{procInstanceName},
                    new String[]{TblsReqs.ProcedureUserRequirements.FLD_ORDER_NUMBER.getName()}, null));
                
                jMainObj.put("user_requirements_events", ClassProcedureQueries.dbRowsToJsonArr(procInstanceName, TblsReqs.ProcedureUserRequirementsEvents.TBL.getName(), 
                    TblsReqs.ProcedureUserRequirementsEvents.getAllFieldNames(), new String[]{TblsReqs.ProcedureUserRequirementsEvents.FLD_PROCEDURE_NAME.getName()}, new Object[]{procInstanceName},
                    new String[]{TblsReqs.ProcedureUserRequirementsEvents.FLD_ORDER_NUMBER.getName()}, null));

                jMainObj.put("master_data", ClassProcedureQueries.dbRowsToJsonArr(procInstanceName, TblsReqs.ProcedureMasterData.TBL.getName(), 
                    TblsReqs.ProcedureMasterData.getAllFieldNames(), new String[]{TblsReqs.ProcedureMasterData.FLD_PROCEDURE_NAME.getName()}, new Object[]{procInstanceName},
                    null, new String[]{TblsReqs.ProcedureMasterData.FLD_JSON_OBJ.getName()}));

                jMainObj.put("frontend_proc_model", ClassProcedureQueries.feProcModel(procInstanceName));
                Rdbms.closeRdbms();  
                JSONObject mainRespDef= new JSONObject();
                mainRespDef.put("definition", jMainObj);
                LPFrontEnd.servletReturnSuccess(request, response, mainRespDef);
                return;                                
            case ENABLE_ACTIONS_AND_ROLES:  
                LPFrontEnd.servletReturnSuccess(request, response, 
                        getProcBusinessRulesQueriesInfo(procInstanceName, ProcBusinessRulesQueries.PROCEDURE_ACTIONS_AND_ROLES.toString()));
                return;
            default:                
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
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

}
