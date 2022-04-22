/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulegenoma;

import databases.TblsCnfg;
import databases.TblsData;
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
public class xTablesDeployment extends HttpServlet {

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
/*            
            String tblCreateScript=createTableScript(TblsCnfg.TablesConfig.SOP_META_DATA, schemaNamePrefix, false, true);
            //Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});
            out.println("<p>Table "+TblsCnfg.TablesConfig.SOP_META_DATA.getTableName()+" created.</p>");
            
            tblCreateScript=createTableScript(TblsData.TablesData.USER_SOP, schemaNamePrefix, false, true);
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsData.TablesData.USER_SOP.getTableName()+" created.</p>");

//            tblCreateScript=TblsData.ViewUserAndMetaDataSopView.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
//            out.println("<p>Table "+TblsData.ViewUserAndMetaDataSopView.TBL.getName()+" created.</p>");

            tblCreateScript=TblsGenomaData.Project.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.TablesGenomaData.PROJECT.getTableName()+" created.</p>");
            
            tblCreateScript=TblsGenomaData.ProjectUsers.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.TablesGenomaData.PROJECT_USERS.getTableName()+" created.</p>");

            tblCreateScript=TblsGenomaData.Study.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.TablesGenomaData.STUDY.getTableName()+" created.</p>");

            tblCreateScript=TblsGenomaData.StudyUsers.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.TablesGenomaData.STUDY_USERS.getTableName()+" created.</p>");

            tblCreateScript=TblsGenomaData.StudyIndividual.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName()+" created.</p>");

            tblCreateScript=TblsGenomaData.StudyIndividualSample.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName()+" created.</p>");
            
            tblCreateScript=TblsGenomaData.StudySamplesSet.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName()+" created.</p>");

            tblCreateScript=TblsGenomaData.StudyFamily.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName()+" created.</p>");

            tblCreateScript=TblsGenomaData.StudyVariableValues.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES.getTableName()+" created.</p>");

            tblCreateScript=TblsGenomaData.studyObjectsFiles.createTableScript(schemaNamePrefix, new String[]{""});
           // Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaData.TablesGenomaData.STUDY_OBJECTS_FILES.getTableName()+" created.</p>");
            
            tblCreateScript=TblsGenomaConfig.Variables.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table config."+TblsGenomaConfig.TablesGenomaConfig.VARIABLES.getTableName()+" created.</p>");

            tblCreateScript=TblsGenomaConfig.VariablesSet.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table config."+TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET.getTableName()+" created.</p>");

            tblCreateScript=createTableScript(TblsDataAudit.TablesDataAudit.SESSION, schemaNamePrefix, false, true);
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table data-audit."+TblsDataAudit.TablesDataAudit.SESSION.getTableName()+" created.</p>");
            
            tblCreateScript=TblsGenomaDataAudit.Project.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaDataAudit.TablesGenomaDataAudit.PROJECT.getTableName()+" created.</p>");

            tblCreateScript=TblsGenomaDataAudit.Study.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            out.println("<p>Table "+TblsGenomaDataAudit.TablesGenomaDataAudit.STUDY.getTableName()+" created.</p>");
*/
            
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
