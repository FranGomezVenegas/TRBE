/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.requirements;

import com.labplanet.servicios.proceduredefinition.ProcedureDefinitionAPI;
import com.labplanet.servicios.proceduredefinition.ProcedureDefinitionAPI.ProcedureDefinitionAPIEndpoints;
import databases.DbObjects;
import databases.Rdbms;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPFrontEnd;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class FrontendDefinition extends HttpServlet {
    Boolean CREATE_FILES=false;
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
        response=LPTestingOutFormat.responsePreparation(response);
        String fileContent = LPTestingOutFormat.getHtmlStyleHeader(this.getClass().getSimpleName(), "No File");
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}   
        ProcedureDefinitionAPI.ProcedureDefinitionAPIEndpoints endPoint = ProcedureDefinitionAPIEndpoints.DEPLOY_FRONTEND;
        LPAPIArguments[] arguments = endPoint.getArguments();
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, arguments);                
        String procName = argValues[0].toString(); //request.getParameter("procedureName"); //"process-us";         
        Integer procVersion= (Integer) argValues[1];
        String schemaPrefix=argValues[2].toString(); //request.getParameter("schemaPrefix"); //"process-us";
        String moduleName=argValues[3].toString();
        String[][] businessVariablesHeader = new String[][]{{"Business Rule", "Value"}                 
                            , {"Process Name", procName}, {"Process Version", procVersion.toString()}, {"Instance", schemaPrefix}
                            , {"CREATE_SCHEMAS_AND_PROC_TBLS", CREATE_FILES.toString()}
//                            , {"PROC_DEPLOY_PROCEDURE_INFO", PROCDEPL_PROCEDURE_INFO.toString()}
//                            , {"PROC_DEPLOY_PROCEDURE_USER_ROLES", PROCDEPL_PROCEDURE_USER_ROLES.toString()}
//                            , {"PROC_DEPLOY_PROCEDURE_SOP_META_DATA", PROCDEPL_PROCEDURE_SOP_META_DATA.toString()}    
//                            , {"PROC_DEPLOY_ASSIGN_PROCEDURE_SOPS_TO_USERS", PROCDEPL_ASIGN_PROC_SOPS_TO_USERS.toString()}                
//                            , {"PROC_DISPLAY_PROC_INSTANCE_SOPS", PROC_DISPLAY_PROC_INSTANCE_SOPS.toString()}                
//                            , {"PROC_DEPLOYMENT_ENTIRE_PROCEDURE", PROC_DEPLOYMENT_ENTIRE_PROCEDURE.toString()}
//                            , {"PROC_DEPLOYMENT_CREATE_MISSING_PROC_EVENT_SOPS", PROC_DEPLOYMENT_CREATE_MISSING_PROC_EVENT_SOPS.toString()}
//                            , {"PROC_DEPLOYMENT_ASSIGN_USER_SOPS", PROC_DEPLOYMENT_ASSIGN_USER_SOPS.toString()}
                    };
        
        fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(businessVariablesHeader); 
        try (PrintWriter out = response.getWriter()) {
            if (Boolean.valueOf(argValues[4].toString()) || CREATE_FILES){
                JSONObject createFiles = DbObjects.createModuleSchemasAndBaseTables(schemaPrefix, null);
                String[][] createDBProcedureInfoTbl = new String[][]{{"Log for createFiles"},{createFiles.toJSONString()}};  
                fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(createDBProcedureInfoTbl);
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
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
