/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulegenoma;

import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsData.TablesData;
import databases.TblsDataAudit;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static trazit.enums.deployrepository.DeployTables.createTableScript;

/**
 *
 * @author User
 */
public class TablesDeployment extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            String schemaNamePrefix="genoma-1";
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TablesDeployment</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TablesDeployment at " + request.getContextPath() + "</h1>");
            
            String tblCreateScript2=TblsCnfg.SopMetaData.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});
            out.println("<p>Table "+TblsCnfg.SopMetaData.TBL.getName()+" created.</p>");
            
            String tblCreateScript=createTableScript(TablesData.USER_SOP, schemaNamePrefix);
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsData.TablesData.USER_SOP.getTableName()+" created.</p>");

            tblCreateScript=TblsData.ViewUserAndMetaDataSopView.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsData.ViewUserAndMetaDataSopView.TBL.getName()+" created.</p>");

            tblCreateScript=TblsGenomaData.Project.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.Project.TBL.getName()+" created.</p>");
            
            tblCreateScript=TblsGenomaData.ProjectUsers.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.ProjectUsers.TBL.getName()+" created.</p>");

            tblCreateScript=TblsGenomaData.Study.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.Study.TBL.getName()+" created.</p>");

            tblCreateScript=TblsGenomaData.StudyUsers.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.StudyUsers.TBL.getName()+" created.</p>");

            tblCreateScript=TblsGenomaData.StudyIndividual.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.StudyIndividual.TBL.getName()+" created.</p>");

            tblCreateScript=TblsGenomaData.StudyIndividualSample.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.StudyIndividualSample.TBL.getName()+" created.</p>");
            
            tblCreateScript=TblsGenomaData.StudySamplesSet.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.StudySamplesSet.TBL.getName()+" created.</p>");

            tblCreateScript=TblsGenomaData.StudyFamily.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.StudyFamily.TBL.getName()+" created.</p>");

            tblCreateScript=TblsGenomaData.StudyVariableValues.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.StudyVariableValues.TBL.getName()+" created.</p>");

            tblCreateScript=TblsGenomaData.studyObjectsFiles.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.studyObjectsFiles.TBL.getName()+" created.</p>");
            
            tblCreateScript=TblsGenomaConfig.Variables.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table config."+TblsGenomaConfig.Variables.TBL.getName()+" created.</p>");

            tblCreateScript=TblsGenomaConfig.VariablesSet.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table config."+TblsGenomaConfig.VariablesSet.TBL.getName()+" created.</p>");

            tblCreateScript=createTableScript(TblsDataAudit.TablesDataAudit.SESSION, schemaNamePrefix);
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table data-audit."+TblsDataAudit.TablesDataAudit.SESSION.getTableName()+" created.</p>");
            
            tblCreateScript=TblsGenomaDataAudit.Project.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaDataAudit.Project.TBL.getName()+" created.</p>");

            tblCreateScript=TblsGenomaDataAudit.Study.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaDataAudit.Study.TBL.getName()+" created.</p>");

            
            out.println("</body>");
            out.println("</html>");

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (IOException | ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
        } catch (IOException | ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
