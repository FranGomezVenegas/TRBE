/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import com.labplanet.servicios.ClassPath;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static lbplanet.utilities.LPHttp.moduleActionsSingleAPI;
import trazit.enums.ActionsEndpointPair;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class SampleAPI extends HttpServlet {
    
    String propertyFileName = "";    


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        propertyFileName = ClassPath.getInstanceForActions().getConfigXmlPath(); //context.getInitParameter("jsfiles");
    }    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response){        
        ActionsEndpointPair[] actionEndpointArr = GlobalVariables.TrazitModules.SAMPLES_MANAGEMENT.getActionsEndpointPair(); //implements ActionsClass
        moduleActionsSingleAPI(request, response, actionEndpointArr, this.getServletName());
    }
    

    // <editor-fold defaultstate="collapsed" desc="HttpServlLPFrontEnd on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  {
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