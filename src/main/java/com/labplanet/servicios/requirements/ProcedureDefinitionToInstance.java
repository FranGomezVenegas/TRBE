/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.requirements;

import com.labplanet.servicios.ResponseSuccess;
import com.labplanet.servicios.proceduredefinition.ProcedureDefinitionAPI;
import com.labplanet.servicios.proceduredefinition.ProcedureDefinitionAPI.ProcedureDefinitionAPIEndpoints;
import databases.DbObjects;
import lbplanet.utilities.LPFrontEnd;
import databases.Rdbms;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}   
        ProcedureDefinitionAPI.ProcedureDefinitionAPIEndpoints endPoint = ProcedureDefinitionAPIEndpoints.DEPLOY_REQUIREMENTS;
        LPAPIArguments[] arguments = endPoint.getArguments();
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, arguments);                
        String procName = argValues[0].toString();
        Integer procVersion= (Integer) argValues[1];
        String procInstanceName=argValues[2].toString();
        String dbName=argValues[3].toString();
        String moduleName=argValues[4].toString();
        String[][] businessVariablesHeader = new String[][]{{"Business Rule", "Value"}                 
                            , {"Process Name", procName}, {"Process Version", procVersion.toString()}, {"Instance", procInstanceName}
                            , {"CREATE_SCHEMAS_AND_PROC_TBLS", argValues[4].toString()}
                            , {"PROC_DEPLOY_PROCEDURE_INFO", argValues[5].toString()}
                            , {"PROC_DEPLOY_PROCEDURE_USER_ROLES", argValues[6].toString()}
                            , {"PROC_DEPLOY_PROCEDURE_SOP_META_DATA", argValues[7].toString()}    
                            , {"PROC_DEPLOY_PROC_EVENTS", argValues[8].toString()}                
                            , {"PROC_DEPLOY_ASSIGN_PROCEDURE_SOPS_TO_USERS", argValues[9].toString()}                
                            , {"PROC_DEPLOY_BUSINESS_RULES_PROPERTIES", argValues[10].toString()}
                            , {"PROC_DEPLOY_TABLES_AND_FIELDS", argValues[11].toString()}
                            , {"PROC_DEPLOY_MASTER_DATA", argValues[12].toString()}
                    };
        JSONObject mainObj=new JSONObject();
        JSONObject sectionsSettingJobj=new JSONObject();
        JSONObject sectionsDetailObj=new JSONObject();
        JSONObject procInstanceInfo=new JSONObject();
        procInstanceInfo.put("Process Name", procName);
        procInstanceInfo.put("Process Version", procVersion);
        procInstanceInfo.put("Instance Name", procInstanceName);        
        procInstanceInfo.put("dbName", dbName);        
        mainObj.put("Procedure Instance Info", procInstanceInfo);
        
        try (PrintWriter out = response.getWriter()) {
            Boolean runSection=Boolean.valueOf(argValues[5].toString()) || CREATE_SCHEMAS_AND_PROC_TBLS;
            sectionsSettingJobj.put("1) CREATE_SCHEMAS_AND_PROC_TBLS", runSection);
            if (runSection){            
                JSONObject createDBProcedureInfo = DbObjects.createModuleSchemasAndBaseTables(procInstanceName, null);
                sectionsDetailObj.put("CREATE_SCHEMAS_AND_PROC_TBLS", createDBProcedureInfo);
            }               
            runSection=Boolean.valueOf(argValues[6].toString()) || PROCDEPL_PROCEDURE_INFO;
            sectionsSettingJobj.put("2) PROCDEPL_PROCEDURE_INFO", runSection);
            if (runSection){
                JSONObject createDBProcedureInfo = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBProcedureInfo(procName, procVersion, procInstanceName);
                sectionsDetailObj.put("PROCDEPL_PROCEDURE_INFO", createDBProcedureInfo);
            }   
            runSection=Boolean.valueOf(argValues[7].toString()) || PROCDEPL_PROCEDURE_USER_ROLES;
            sectionsSettingJobj.put("3) PROCDEPL_PROCEDURE_USER_ROLES", runSection);
            if (runSection){
                JSONObject createDBProcedureUserRoles = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBPersonProfiles(procName, procVersion, procInstanceName);
                sectionsDetailObj.put("PROCDEPL_PROCEDURE_USER_ROLES", createDBProcedureUserRoles);
            }            
            runSection=Boolean.valueOf(argValues[8].toString()) || PROCDEPL_PROCEDURE_SOP_META_DATA;
            sectionsSettingJobj.put("4) PROCDEPL_PROCEDURE_SOP_META_DATA", runSection);
            if (runSection){
                JSONObject createDBSopMetaDataAndUserSop = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBSopMetaDataAndUserSop(procName, procVersion, procInstanceName);
                sectionsDetailObj.put("PROCDEPL_PROCEDURE_SOP_META_DATA", createDBSopMetaDataAndUserSop);
            }       
            runSection=Boolean.valueOf(argValues[9].toString()) || PROCDEPL_ASIGN_PROC_SOPS_TO_USERS;
            sectionsSettingJobj.put("5) PROCDEPL_ASIGN_PROC_SOPS_TO_USERS", runSection);
            if (runSection){
                JSONObject createDBProcedureUserRoles = functionaljavaa.requirement.ProcedureDefinitionToInstance.addProcedureSOPtoUsers(procName, procVersion, procInstanceName);
                sectionsDetailObj.put("PROCDEPL_ASIGN_PROC_SOPS_TO_USERS", createDBProcedureUserRoles);
            }  
            runSection=Boolean.valueOf(argValues[10].toString()) || PROCDEPL_PROCEDURE_EVENTS;
            sectionsSettingJobj.put("6) PROCDEPL_PROCEDURE_EVENTS", runSection);
            if (runSection){
                JSONObject createDBProcedureEvents = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBProcedureEvents(procName, procVersion, procInstanceName);
                sectionsDetailObj.put("PROCDEPL_PROCEDURE_EVENTS", createDBProcedureEvents);
            }
            runSection=Boolean.valueOf(argValues[11].toString()) || PROCDEPL_BUSINESS_RULES_PROPTS_FILS;
            sectionsSettingJobj.put("7) PROCDEPL_BUSINESS_RULES_PROPTS_FILS", runSection);
            if (runSection){
                JSONArray createPropBusinessRules = functionaljavaa.requirement.ProcedureDefinitionToInstance.createBusinessRules(procName, procVersion, procInstanceName);
                sectionsDetailObj.put("PROCDEPL_BUSINESS_RULES_PROPTS_FILS", createPropBusinessRules);
            }
            runSection=Boolean.valueOf(argValues[12].toString()) || PROCDEPL_MODULE_TABLES_AND_FIELDS;
            sectionsSettingJobj.put("8) PROCDEPL_MODULE_TABLES_AND_FIELDS", runSection);
            if (runSection){
                JSONObject createDBModuleTablesAndFields = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBModuleTablesAndFields(procName, procVersion, procInstanceName, moduleName);
                sectionsDetailObj.put("PROCDEPL_MODULE_TABLES_AND_FIELDS", createDBModuleTablesAndFields);
            }
            runSection=Boolean.valueOf(argValues[13].toString()) || PROCDEPL_MASTER_DATA;
            sectionsSettingJobj.put("9) PROCDEPL_MASTER_DATA", runSection);
            if (runSection){
                JSONObject createDBModuleTablesAndFields = functionaljavaa.requirement.ProcedureDefinitionToInstance.deployMasterData(procName, procVersion, procInstanceName);
                sectionsDetailObj.put("PROCDEPL_MASTER_DATA", createDBModuleTablesAndFields);
            }
            mainObj.put("endpoint_call_settings", sectionsSettingJobj);
            mainObj.put("sections_log", sectionsDetailObj);
            Rdbms.closeRdbms();
            LPFrontEnd.servletReturnSuccess(request, response, mainObj);
        }catch(Exception e){
            Logger.getLogger(ResponseSuccess.class.getName()).log(Level.SEVERE, null, e.getMessage());
        }finally{
            Rdbms.closeRdbms();
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
