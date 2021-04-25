/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.requirements;

import com.labplanet.servicios.proceduredefinition.ProcedureDefinitionAPI;
import com.labplanet.servicios.proceduredefinition.ProcedureDefinitionAPI.ProcedureDefinitionAPIEndpoints;
import databases.DbObjects;
import lbplanet.utilities.LPFrontEnd;
import databases.Rdbms;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Administrator
 */
public class ProcedureDefinitionToInstance extends HttpServlet {

    private static final Boolean  CREATE_SCHEMAS_AND_PROC_TBLS=false;
    private static final Boolean  PROCDEPL_PROCEDURE_INFO=false;
    private static final Boolean  PROCDEPL_PROCEDURE_USER_ROLES=false;
    private static final Boolean  PROCDEPL_PROCEDURE_SOP_META_DATA=false;
    private static final Boolean  PROCDEPL_ASIGN_PROC_SOPS_TO_USERS=false;
    private static final Boolean  PROCDEPL_PROCEDURE_EVENTS=false;
    private static final Boolean  PROCDEPL_BUSINESS_RULES_PROPTS_FILS=false;
    private static final Boolean  PROCDEPL_MODULE_TABLES_AND_FIELDS=false;
    private static final Boolean  PROCDEPL_MASTER_DATA=false;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response=LPTestingOutFormat.responsePreparation(response);
        String fileContent = LPTestingOutFormat.getHtmlStyleHeader(this.getClass().getSimpleName(), "No File");
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}   
        ProcedureDefinitionAPI.ProcedureDefinitionAPIEndpoints endPoint = ProcedureDefinitionAPIEndpoints.DEPLOY_REQUIREMENTS;
        LPAPIArguments[] arguments = endPoint.getArguments();
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, arguments);                
        String procName = argValues[0].toString(); //request.getParameter("procedureName"); //"process-us";         
        Integer procVersion= (Integer) argValues[1];
        String schemaPrefix=argValues[2].toString(); //request.getParameter("schemaPrefix"); //"process-us";
        String moduleName=argValues[3].toString();
        String[][] businessVariablesHeader = new String[][]{{"Business Rule", "Value"}                 
                            , {"Process Name", procName}, {"Process Version", procVersion.toString()}, {"Instance", schemaPrefix}
                            , {"CREATE_SCHEMAS_AND_PROC_TBLS", CREATE_SCHEMAS_AND_PROC_TBLS.toString()}
                            , {"PROC_DEPLOY_PROCEDURE_INFO", PROCDEPL_PROCEDURE_INFO.toString()}
                            , {"PROC_DEPLOY_PROCEDURE_USER_ROLES", PROCDEPL_PROCEDURE_USER_ROLES.toString()}
                            , {"PROC_DEPLOY_PROCEDURE_SOP_META_DATA", PROCDEPL_PROCEDURE_SOP_META_DATA.toString()}    
                            , {"PROC_DEPLOY_ASSIGN_PROCEDURE_SOPS_TO_USERS", PROCDEPL_ASIGN_PROC_SOPS_TO_USERS.toString()}                
//                            , {"PROC_DISPLAY_PROC_INSTANCE_SOPS", PROC_DISPLAY_PROC_INSTANCE_SOPS.toString()}                
//                            , {"PROC_DEPLOYMENT_ENTIRE_PROCEDURE", PROC_DEPLOYMENT_ENTIRE_PROCEDURE.toString()}
//                            , {"PROC_DEPLOYMENT_CREATE_MISSING_PROC_EVENT_SOPS", PROC_DEPLOYMENT_CREATE_MISSING_PROC_EVENT_SOPS.toString()}
//                            , {"PROC_DEPLOYMENT_ASSIGN_USER_SOPS", PROC_DEPLOYMENT_ASSIGN_USER_SOPS.toString()}
                    };
        
        fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(businessVariablesHeader); 
        try (PrintWriter out = response.getWriter()) {
            if (Boolean.valueOf(argValues[4].toString()) || CREATE_SCHEMAS_AND_PROC_TBLS){
                JSONObject createDBProcedureInfo = DbObjects.createModuleSchemasAndBaseTables(schemaPrefix, null);
                String[][] createDBProcedureInfoTbl = new String[][]{{"Log for PROC_DEPLOY_PROCEDURE_INFO"},{createDBProcedureInfo.toJSONString()}};  
                fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(createDBProcedureInfoTbl);
            }   
            if (Boolean.valueOf(argValues[5].toString()) || PROCDEPL_PROCEDURE_INFO){
                JSONObject createDBProcedureInfo = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBProcedureInfo(procName, procVersion, schemaPrefix);
                String[][] createDBProcedureInfoTbl = new String[][]{{"Log for PROC_DEPLOY_PROCEDURE_INFO"},{createDBProcedureInfo.toJSONString()}};  
                fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(createDBProcedureInfoTbl);
            }   
            if (Boolean.valueOf(argValues[6].toString()) || PROCDEPL_PROCEDURE_USER_ROLES){
                JSONObject createDBProcedureUserRoles = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBPersonProfiles(procName, procVersion, schemaPrefix);
                String[][] createDBProcedureUserRolesTbl = new String[][]{{"Log for PROC_DEPLOY_PROCEDURE_USER_ROLES"},{createDBProcedureUserRoles.toJSONString()}};  
                fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(createDBProcedureUserRolesTbl);
            }            
            if (Boolean.valueOf(argValues[7].toString()) || PROCDEPL_PROCEDURE_SOP_META_DATA){
                JSONObject createDBProcedureUserRoles = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBSopMetaDataAndUserSop(procName, procVersion, schemaPrefix);
                String[][] createDBProcedureUserRolesTbl = new String[][]{{"Log for PROC_DEPLOY_PROCEDURE_SOP_META_DATA"},{createDBProcedureUserRoles.toJSONString()}};  
                fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(createDBProcedureUserRolesTbl);
            }       
            if (Boolean.valueOf(argValues[8].toString()) || PROCDEPL_ASIGN_PROC_SOPS_TO_USERS){
                JSONObject createDBProcedureUserRoles = functionaljavaa.requirement.ProcedureDefinitionToInstance.addProcedureSOPtoUsers(procName, procVersion, schemaPrefix);
                String[][] createDBProcedureUserRolesTbl = new String[][]{{"Log for PROC_DEPLOY_ASSIGN_PROCEDURE_SOPS_TO_USERS"},{createDBProcedureUserRoles.toJSONString()}};  
                fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(createDBProcedureUserRolesTbl);
            }  
            if (Boolean.valueOf(argValues[9].toString()) || PROCDEPL_PROCEDURE_EVENTS){
                JSONObject createDBProcedureEvents = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBProcedureEvents(procName, procVersion, schemaPrefix);
                String[][] createDBProcedureEventsDiagnostic = new String[][]{{"Log for PROC_DEPLOY_PROCEDURE_EVENTS"},{createDBProcedureEvents.toJSONString()}};  
                fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(createDBProcedureEventsDiagnostic);                
            }
            if (Boolean.valueOf(argValues[10].toString()) || PROCDEPL_BUSINESS_RULES_PROPTS_FILS){
                JSONArray createPropBusinessRules = functionaljavaa.requirement.ProcedureDefinitionToInstance.createPropBusinessRules(procName, procVersion, schemaPrefix);
                String[][] createPropBusinessRulesTbl = new String[][]{{"Log for PROC_DEPLOY_PROCEDURE_SOP_META_DATA"},{createPropBusinessRules.toJSONString()}};  
                fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(createPropBusinessRulesTbl);                
            }
            if (Boolean.valueOf(argValues[11].toString()) || PROCDEPL_MODULE_TABLES_AND_FIELDS){
                JSONObject createDBModuleTablesAndFields = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBModuleTablesAndFields(procName, procVersion, schemaPrefix, moduleName);
                String[][] createDBModuleTablesAndFieldsDiagnostic = new String[][]{{"Log for PROC_DEPLOY_MODULE_TABLES_AND_FIELDS"},{createDBModuleTablesAndFields.toJSONString()}};  
                fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(createDBModuleTablesAndFieldsDiagnostic);                
            }
            if (Boolean.valueOf(argValues[12].toString()) || PROCDEPL_MASTER_DATA){
                JSONObject createDBModuleTablesAndFields = functionaljavaa.requirement.ProcedureDefinitionToInstance.deployMasterData(procName, procVersion, schemaPrefix);
                String[][] createDBModuleTablesAndFieldsDiagnostic = new String[][]{{"Log for PROCDEPL_MASTER_DATA"},{createDBModuleTablesAndFields.toJSONString()}};  
                fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(createDBModuleTablesAndFieldsDiagnostic);                
            }
            
            
            fileContent=fileContent+LPTestingOutFormat.bodyEnd()+LPTestingOutFormat.htmlEnd();
            out.println(fileContent);            
        }
        Rdbms.closeRdbms();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
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
