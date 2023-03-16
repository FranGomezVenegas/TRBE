/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import com.oreilly.servlet.MultipartRequest;
import databases.features.Token;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import functionaljavaa.testingscripts.LPTestingOutFormat.FileHeaderTags;
import functionaljavaa.testingscripts.LPTestingParams;
import functionaljavaa.testingscripts.LPTestingParams.TestingServletsConfig;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.labelling.ZPL;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author User
 */
public class TesterFromUploadFile extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)           throws ServletException, IOException {
        response = LPTestingOutFormat.responsePreparation(response);        
        try{
            String ipAddr = request.getParameter("ipAddr");
            String portNum = request.getParameter("portNum");

            String ip="192"+"."+"168"+".1"+".139";
            Integer port=3910;
            if (LPNulls.replaceNull(ipAddr).length()>0)
                ip=ipAddr;
            if (LPNulls.replaceNull(portNum).length()>0)
                port=Integer.valueOf(portNum);
            StringBuilder zplDemoLabel = ZPL.zplDemoLabel(ip, port);
            PrintWriter outZpl = response.getWriter();
            outZpl.println(zplDemoLabel);
            if (1==1) return;
            String saveDirectory="D:\\LP\\"; //TESTING_FILES_PATH;
            MultipartRequest mReq = new MultipartRequest(request, saveDirectory);
            Enumeration files = mReq.getFileNames();
            while (files.hasMoreElements()) {
                String upload = (String) files.nextElement();
                String fullFileName=mReq.getOriginalFileName(upload);
                
                String csvPathName=saveDirectory+fullFileName;
                StringBuilder fileContentBuilder = new StringBuilder(0);
                String[][] headerInfo = LPArray.convertCSVinArray(csvPathName, "=");
                HashMap<String, Object> csvHeaderTags = LPTestingOutFormat.getCSVHeaderTester(headerInfo);
                if (csvHeaderTags.containsKey(LPPlatform.LAB_FALSE)){
                    fileContentBuilder.append("There are missing tags in the file header: ").append(csvHeaderTags.get(LPPlatform.LAB_FALSE));
                    Logger.getLogger(fileContentBuilder.toString()); 
                    PrintWriter out = response.getWriter();
                    out.println(fileContentBuilder);                     
                    return;
                }                        
                String testerName = (String) csvHeaderTags.get(FileHeaderTags.TESTER_NAME.getTagValue().toString());                           
                String tokenStr = (String) csvHeaderTags.get(FileHeaderTags.TOKEN.getTagValue().toString());                           
                if (tokenStr.length()==0){
                    PrintWriter out = response.getWriter();
                    out.println("No Token"); 
                    return;
                }
                Token token=new Token(tokenStr);
                String validateToken = token.validateToken(tokenStr);
                if (!"TRUE".equalsIgnoreCase(validateToken)){
                    PrintWriter out = response.getWriter();
                    out.println("Token provided is invalid"); 
                    return;
                }
                request.setAttribute(LPTestingParams.UPLOAD_FILE_PARAM_FILE_PATH, saveDirectory+"\\");
                request.setAttribute(LPTestingParams.UPLOAD_FILE_PARAM_FILE_NAME, fullFileName);
                request.setAttribute(LPTestingParams.TESTING_SOURCE, "FILE");

                TestingServletsConfig endPoints = TestingServletsConfig.valueOf(testerName);

                switch (endPoints){
                case NODB_SCHEMACONFIG_SPECQUAL_RULEFORMAT:
                case NODB_SCHEMACONFIG_SPECQUAL_RESULTCHECK:
                case NODB_SCHEMACONFIG_SPECQUANTI_RULEFORMAT:
                case NODB_SCHEMACONFIG_SPECQUANTI_RESULTCHECK:
                    RequestDispatcher rd = request.getRequestDispatcher(endPoints.getServletUrl());
                    rd.forward(request,response);   
                    return;                       
                default:
                    Logger.getLogger("Tester name not recognized, "+testerName+". The tester cannot be completed"); 
                    return;
                }
            }
        } catch (IOException e){
            PrintWriter out = response.getWriter();
            out.println(e.getMessage()); 
            java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
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
