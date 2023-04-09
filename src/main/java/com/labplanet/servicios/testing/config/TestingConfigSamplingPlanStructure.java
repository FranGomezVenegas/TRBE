/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.testing.config;

import lbplanet.utilities.LPNulls;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import databases.Rdbms;
import functionaljavaa.materialspec.ConfigSamplingPlanForSpec;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class TestingConfigSamplingPlanStructure extends HttpServlet {

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
        response = LPTestingOutFormat.responsePreparation(response);        
        try (PrintWriter out = response.getWriter()) {
            ConfigSamplingPlanForSpec smpPlan = new ConfigSamplingPlanForSpec();

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForUAT(request, response, true, "");        
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);
            return;
        }
            
        Object[] exRec =  Rdbms.existsRecord(GlobalVariables.Schemas.APP.getName(), "users", new String[]{"user_name"}, new Object[]{"labplanet"});
        out.println("Exists record? " + Arrays.toString(exRec));
        
        String csvFileName = "dbActions.txt"; 

        String csvPathName = LPTestingOutFormat.TESTING_FILES_PATH+csvFileName; 
        String csvFileSeparator=LPTestingOutFormat.TESTING_FILES_FIELD_SEPARATOR;
        
        Object[][] csvFileContent = LPArray.convertCSVinArray(csvPathName, csvFileSeparator); 
        
        out.println("Reading file: " + csvFileContent[0][0].toString());
        
            Integer numTesting = 1;
            Integer inumTesting = 0;
            Object[][] configSamplingPlanTestingArray = new Object[numTesting][6];
            String userName="1"; 

            if (inumTesting<numTesting){
                String[] fieldName= new String[0];
                Object[] fieldValue=new Object[0];
                String procInstanceName="oil-pl1";
                String actionName="NEWSAMPLINGDETAIL";
                String samplingPlan = "84";
                fieldName = LPArray.addValueToArray1D(fieldName, "analysis");
                fieldValue = LPArray.addValueToArray1D(fieldValue, "pH");
                fieldName = LPArray.addValueToArray1D(fieldName, "method_name");
                fieldValue = LPArray.addValueToArray1D(fieldValue, "pH method");
                fieldName = LPArray.addValueToArray1D(fieldName, "method_version");
                fieldValue = LPArray.addValueToArray1D(fieldValue, 2);
                configSamplingPlanTestingArray[inumTesting][0]=procInstanceName;
                configSamplingPlanTestingArray[inumTesting][1]=samplingPlan;
                configSamplingPlanTestingArray[inumTesting][2]=userName;
                configSamplingPlanTestingArray[inumTesting][3]=fieldName;
                configSamplingPlanTestingArray[inumTesting][4]=fieldValue;
                configSamplingPlanTestingArray[inumTesting][5]=actionName;
                inumTesting++;
            }
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TestingConfigSamplingPlanStructure</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>ssssss Servlet TestingConfigSamplingPlanStructure at " + request.getContextPath() + "</h1>");
            out.println("<table>");
            out.println("<th>Test#</th><th>Schema Prefix</th><th>Function Being Tested</th><th>Field Name</th><th>Field Value</th><th>Evaluation</th>");           
            
            for (Integer i=0;i<configSamplingPlanTestingArray.length;i++){
out.println(Arrays.toString(configSamplingPlanTestingArray));                
                out.println("<tr>");
                String[] fieldName=null;    
                Object[] fieldValue=null;
                String procInstanceName=null;
                userName=null;                
                String actionName=null;
                Object[] dataSample = null;

                if (configSamplingPlanTestingArray[i][0]!=null){procInstanceName = configSamplingPlanTestingArray[i][0].toString();}
                if (configSamplingPlanTestingArray[i][5]!=null){actionName = configSamplingPlanTestingArray[i][5].toString();}
                    
                out.println(LPTestingOutFormat.FIELD_START+i+LPTestingOutFormat.FIELD_START+LPTestingOutFormat.FIELD_END+procInstanceName+LPTestingOutFormat.FIELD_START+LPTestingOutFormat.FIELD_END+actionName+LPTestingOutFormat.FIELD_START+LPTestingOutFormat.FIELD_END+Arrays.toString(fieldName)+LPTestingOutFormat.FIELD_START+LPTestingOutFormat.FIELD_END+"<b>"+Arrays.toString(fieldValue)+"</b>"+LPTestingOutFormat.FIELD_END);

                switch (LPNulls.replaceNull(actionName).toUpperCase()){
                    case "NEWSAMPLINGDETAIL":
                        if (configSamplingPlanTestingArray[i][1]!=null){procInstanceName = configSamplingPlanTestingArray[i][0].toString();}
                        if (configSamplingPlanTestingArray[i][1]!=null){userName = configSamplingPlanTestingArray[i][2].toString();}
                        if (configSamplingPlanTestingArray[i][3]!=null){fieldName = (String[]) configSamplingPlanTestingArray[i][3];}              
                        if (configSamplingPlanTestingArray[i][4]!=null){fieldValue = (Object[]) configSamplingPlanTestingArray[i][4];}                         
                        dataSample = smpPlan.newSamplingPlanDetailRecord(fieldName, fieldValue);
                        break;
                    default:                
                        break;
                }
                if (dataSample!=null){
                    out.println("<td>"+dataSample[0].toString()+". "+dataSample[1].toString()+". "+dataSample[2].toString()+". "+dataSample[3].toString()+". "+dataSample[4].toString()+". "+dataSample[5].toString()+"</td>");}
                out.println("</tr>");
            }
            out.println("</table>");        
            out.println("</body>");
            out.println("</html>");
            Rdbms.closeRdbms();
        }   catch (IOException ex) {
                Logger.getLogger(TestingConfigSamplingPlanStructure.class.getName()).log(Level.SEVERE, null, ex);                           
            String exceptionMessage = ex.getMessage();     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);                    
        } finally {
            // release database resources
            try {
                   
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }       }

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
