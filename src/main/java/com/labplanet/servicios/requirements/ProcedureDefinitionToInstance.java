/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.requirements;

import com.labplanet.servicios.ResponseSuccess;
import com.labplanet.servicios.proceduredefinition.ReqProcedureEnums.ProcedureDefinitionAPIActionsEndpoints;
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
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Administrator
 */
public class ProcedureDefinitionToInstance extends HttpServlet {

    private static final Boolean  CREATE_REPOSITORIES_AND_PROC_TBLS=false;
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
        Object endPointName = request.getAttribute("endPointName");
        Object runAsCheckerAttrValue = LPNulls.replaceNull(request.getAttribute("run_as_checker"));
        ProcedureDefinitionAPIActionsEndpoints endPoint = ProcedureDefinitionAPIActionsEndpoints.valueOf(endPointName.toString());
        LPAPIArguments[] arguments = endPoint.getArguments();
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, arguments);                
        String procName = argValues[0].toString();
        Integer procVersion= (Integer) argValues[1];
        String procInstanceName=argValues[2].toString();
        String dbName=argValues[3].toString();
        String moduleName=null;
        
        if (!Boolean.valueOf(runAsCheckerAttrValue.toString()))
            moduleName=argValues[4].toString();
        
        JSONObject mainObj=new JSONObject();
        JSONObject sectionsSettingJobj=new JSONObject();
        JSONObject sectionsDetailObj=new JSONObject();
        JSONArray sectionsDetailCheckerArr=new JSONArray();
        JSONObject procInstanceInfo=new JSONObject();
        procInstanceInfo.put("Process Name", procName);
        procInstanceInfo.put("Process Version", procVersion);
        procInstanceInfo.put("Instance Name", procInstanceName);        
        procInstanceInfo.put("dbName", dbName);        
        mainObj.put("Procedure Instance Info", procInstanceInfo);
        Boolean runSection=false;
        Integer iSection=0;
        try (PrintWriter out = response.getWriter()) {
            if (Boolean.valueOf(runAsCheckerAttrValue.toString())){
                JSONObject createDBProcedureInfo = ProcDeployCheckerLogic.createModuleSchemasAndBaseTables(procInstanceName);
                createDBProcedureInfo.put("section_name", "CREATE_REPOSITORIES_AND_PROC_TBLS");
                createDBProcedureInfo.put("section_label_en", "Repositories and Base Procedure Tables creation");
                createDBProcedureInfo.put("section_label_es", "Creación de Repositorios y Tablas Base del Proceso");
                iSection++;
                createDBProcedureInfo.put("index", iSection);
                sectionsDetailCheckerArr.add(createDBProcedureInfo);
            }else{
                runSection=Boolean.valueOf(argValues[5].toString()) || CREATE_REPOSITORIES_AND_PROC_TBLS;
                sectionsSettingJobj.put("1) CREATE_REPOSITORIES_AND_PROC_TBLS", runSection);
                if (runSection){            
                    JSONObject createDBProcedureInfo = DbObjects.createModuleSchemasAndBaseTables(procInstanceName);
                    sectionsDetailObj.put("CREATE_REPOSITORIES_AND_PROC_TBLS", createDBProcedureInfo);
                }               
            }
            if (Boolean.valueOf(runAsCheckerAttrValue.toString())){
                JSONObject createDBProcedureInfo = ProcDeployCheckerLogic.createDBProcedureInfo(procName, procVersion, procInstanceName);
                createDBProcedureInfo.put("section_name", "PROCDEPL_PROCEDURE_INFO");
                createDBProcedureInfo.put("section_label_en", "Procedure Info section creation");
                createDBProcedureInfo.put("section_label_es", "Creación de sección Procedure Info");
                iSection++;
                createDBProcedureInfo.put("index", iSection);
                sectionsDetailCheckerArr.add(createDBProcedureInfo);
            }else{                
                runSection=Boolean.valueOf(argValues[6].toString()) || PROCDEPL_PROCEDURE_INFO;
                sectionsSettingJobj.put("2) PROCDEPL_PROCEDURE_INFO", runSection);
                if (runSection){
                    JSONObject createDBProcedureInfo = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBProcedureInfo(procName, procVersion, procInstanceName);
                    sectionsDetailObj.put("PROCDEPL_PROCEDURE_INFO", createDBProcedureInfo);
                }   
            }
            if (Boolean.valueOf(runAsCheckerAttrValue.toString())){
                JSONObject createDBProcedureUserRoles = ProcDeployCheckerLogic.createDBPersonProfiles(procName, procVersion, procInstanceName);
                createDBProcedureUserRoles.put("section_name", "PROCDEPL_PROCEDURE_USER_ROLES");
                createDBProcedureUserRoles.put("section_label_en", "Procedure User and Roles section creation");
                createDBProcedureUserRoles.put("section_label_es", "Creación de sección Usuarios y Roles");
                iSection++;
                createDBProcedureUserRoles.put("index", iSection);
                sectionsDetailCheckerArr.add(createDBProcedureUserRoles);
            }else{                
                runSection=Boolean.valueOf(argValues[7].toString()) || PROCDEPL_PROCEDURE_USER_ROLES;
                sectionsSettingJobj.put("3) PROCDEPL_PROCEDURE_USER_ROLES", runSection);
                if (runSection){
                    JSONObject createDBProcedureUserRoles = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBPersonProfiles(procName, procVersion, procInstanceName);
                    sectionsDetailObj.put("PROCDEPL_PROCEDURE_USER_ROLES", createDBProcedureUserRoles);
                } 
            }
            if (Boolean.valueOf(runAsCheckerAttrValue.toString())){
                JSONObject createDBSopMetaDataAndUserSop = ProcDeployCheckerLogic.createDBSopMetaDataAndUserSop(procName, procVersion, procInstanceName);
                createDBSopMetaDataAndUserSop.put("section_name", "PROCDEPL_PROCEDURE_SOP_META_DATA");
                createDBSopMetaDataAndUserSop.put("section_label_en", "Procedure SOPs section");
                createDBSopMetaDataAndUserSop.put("section_label_es", "Sección de PNTs");
                iSection++;
                createDBSopMetaDataAndUserSop.put("index", iSection);
                sectionsDetailCheckerArr.add(createDBSopMetaDataAndUserSop);
            }else{
                runSection=Boolean.valueOf(argValues[8].toString()) || PROCDEPL_PROCEDURE_SOP_META_DATA;
                sectionsSettingJobj.put("4) PROCDEPL_PROCEDURE_SOP_META_DATA", runSection);
                if (runSection){
                    JSONObject createDBSopMetaDataAndUserSop = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBSopMetaDataAndUserSop(procName, procVersion, procInstanceName);
                    sectionsDetailObj.put("PROCDEPL_PROCEDURE_SOP_META_DATA", createDBSopMetaDataAndUserSop);
                } 
            }
            if (Boolean.valueOf(runAsCheckerAttrValue.toString())){
                JSONObject createDBProcedureUserRoles = ProcDeployCheckerLogic.addProcedureSOPtoUsers(procName, procVersion, procInstanceName);
                createDBProcedureUserRoles.put("section_name", "PROCDEPL_ASIGN_PROC_SOPS_TO_USERS");
                createDBProcedureUserRoles.put("section_label_en", "Procedure assignment of SOPs to Users");
                createDBProcedureUserRoles.put("section_label_es", "Sección de asignación de PNTs a Usuarios");
                iSection++;
                createDBProcedureUserRoles.put("index", iSection);
                sectionsDetailCheckerArr.add(createDBProcedureUserRoles);
            }else{
                runSection=Boolean.valueOf(argValues[9].toString()) || PROCDEPL_ASIGN_PROC_SOPS_TO_USERS;
                sectionsSettingJobj.put("5) PROCDEPL_ASIGN_PROC_SOPS_TO_USERS", runSection);
                if (runSection){
                    JSONObject createDBProcedureUserRoles = functionaljavaa.requirement.ProcedureDefinitionToInstance.addProcedureSOPtoUsers(procName, procVersion, procInstanceName);
                    sectionsDetailObj.put("PROCDEPL_ASIGN_PROC_SOPS_TO_USERS", createDBProcedureUserRoles);
                } 
            }
            if (Boolean.valueOf(runAsCheckerAttrValue.toString())){
                JSONObject createDBProcedureEvents = ProcDeployCheckerLogic.createDBProcedureEvents(procName, procVersion, procInstanceName);
                createDBProcedureEvents.put("section_name", "PROCDEPL_PROCEDURE_EVENTS");
                createDBProcedureEvents.put("section_label_en", "Procedure Events section creation");
                createDBProcedureEvents.put("section_label_es", "Sección de Procedure Events");
                iSection++;
                createDBProcedureEvents.put("index", iSection);
                sectionsDetailCheckerArr.add(createDBProcedureEvents);
            }else{
                runSection=Boolean.valueOf(argValues[10].toString()) || PROCDEPL_PROCEDURE_EVENTS;
                sectionsSettingJobj.put("6) PROCDEPL_PROCEDURE_EVENTS", runSection);
                if (runSection){
                    JSONArray jArr=new JSONArray();
                    JSONObject createDBProcedureEvents = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBProcedureEvents(procName, procVersion, procInstanceName);
                    jArr.add(createDBProcedureEvents);
                    JSONObject createdDBProcedureActions = functionaljavaa.requirement.ProcedureDefinitionToInstance.createdDBProcedureActions(procName,  procVersion, procInstanceName);
                    jArr.add(createdDBProcedureActions);
                    sectionsDetailObj.put("PROCDEPL_PROCEDURE_EVENTS", jArr);
                }
            }
            if (Boolean.valueOf(runAsCheckerAttrValue.toString())){
                JSONObject createPropBusinessRules = ProcDeployCheckerLogic.createBusinessRules(procName, procVersion, procInstanceName);
                createPropBusinessRules.put("section_name", "PROCDEPL_BUSINESS_RULES");
                createPropBusinessRules.put("section_label_en", "Procedure Business Rules section creation");
                createPropBusinessRules.put("section_label_es", "Sección de Procedure Business Rules");
                iSection++;
                createPropBusinessRules.put("index", iSection);
                sectionsDetailCheckerArr.add(createPropBusinessRules);
            }else{
                runSection=Boolean.valueOf(argValues[11].toString()) || PROCDEPL_BUSINESS_RULES_PROPTS_FILS;
                sectionsSettingJobj.put("7) PROCDEPL_BUSINESS_RULES_PROPTS_FILS", runSection);
                if (runSection){
                    JSONArray createPropBusinessRules = functionaljavaa.requirement.ProcedureDefinitionToInstance.createBusinessRules(procName, procVersion, procInstanceName);
                    sectionsDetailObj.put("PROCDEPL_BUSINESS_RULES_PROPTS_FILS", createPropBusinessRules);
                }
            }
            if (Boolean.valueOf(runAsCheckerAttrValue.toString())){
                JSONObject createDBModuleTablesAndFields = ProcDeployCheckerLogic.createDBModuleTablesAndFields(procName, procVersion, procInstanceName, moduleName);
                createDBModuleTablesAndFields.put("section_name", "PROCDEPL_MODULE_TABLES_AND_FIELDS");
                createDBModuleTablesAndFields.put("section_label_en", "Module Tables and Fields section creation");
                createDBModuleTablesAndFields.put("section_label_es", "Sección de Creación de Tablas y Campos del módulo");
                iSection++;
                createDBModuleTablesAndFields.put("index", iSection);
                sectionsDetailCheckerArr.add(createDBModuleTablesAndFields);
            }else{
                runSection=Boolean.valueOf(argValues[12].toString()) || PROCDEPL_MODULE_TABLES_AND_FIELDS;
                sectionsSettingJobj.put("8) PROCDEPL_MODULE_TABLES_AND_FIELDS", runSection);
                if (runSection){
                    JSONObject createDBModuleTablesAndFields = functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBModuleTablesAndFields(procName, procVersion, procInstanceName, moduleName);
                    sectionsDetailObj.put("PROCDEPL_MODULE_TABLES_AND_FIELDS", createDBModuleTablesAndFields);
                }
            }
            if (Boolean.valueOf(runAsCheckerAttrValue.toString())){
                JSONObject createDBModuleTablesAndFields = ProcDeployCheckerLogic.deployMasterData(procName, procVersion, procInstanceName, moduleName);
                createDBModuleTablesAndFields.put("section_name", "PROCDEPL_MASTER_DATA");
                createDBModuleTablesAndFields.put("section_label_en", "Master Data section creation");
                createDBModuleTablesAndFields.put("section_label_es", "Sección de Creación de Data Maestra");
                iSection++;
                createDBModuleTablesAndFields.put("index", iSection);
                sectionsDetailCheckerArr.add(createDBModuleTablesAndFields);
            }else{
                runSection=Boolean.valueOf(argValues[13].toString()) || PROCDEPL_MASTER_DATA;
                sectionsSettingJobj.put("9) PROCDEPL_MASTER_DATA", runSection);
                if (runSection){
                    JSONObject createDBModuleTablesAndFields = functionaljavaa.requirement.ProcedureDefinitionToInstance.deployMasterData(procName, procVersion, procInstanceName, moduleName);
                    sectionsDetailObj.put("PROCDEPL_MASTER_DATA", createDBModuleTablesAndFields);
                }
            }
            if (Boolean.valueOf(runAsCheckerAttrValue.toString())){
                JSONObject dataRepositoriesAreMirror = ProcDeployCheckerLogic.dataRepositoriesAreMirror(procInstanceName, null);
                dataRepositoriesAreMirror.put("section_name", "DATA_REPOSITORIES_MIRROR");
                dataRepositoriesAreMirror.put("section_label_en", "Procedure Data Repositories are Mirror checker");
                dataRepositoriesAreMirror.put("section_label_es", "Sección para Comprobar que los Repositorios de Datos son Espejo");
                iSection++;
                dataRepositoriesAreMirror.put("index", iSection);
                sectionsDetailCheckerArr.add(dataRepositoriesAreMirror);
            }
            
            mainObj.put("actions_to_perform_settings", sectionsSettingJobj);
            if (Boolean.valueOf(runAsCheckerAttrValue.toString()))
                mainObj.put("sections_log", sectionsDetailCheckerArr);
            else
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
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
