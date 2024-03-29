/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.testing.config;

import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import functionaljavaa.testingscripts.TestingAssert;
import functionaljavaa.testingscripts.TestingAssertSummary;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import trazit.session.ProcedureRequestSession;


/**
 *
 * @author Fran
 */
public class TestingUOMFamilyTable extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {
        response = LPTestingOutFormat.responsePreparation(response);        
        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForUAT(request, response, true, "");
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }

        String csvFileName = "uom_familyTable.txt"; 
                            
        String csvPathName = LPTestingOutFormat.TESTING_FILES_PATH+csvFileName; 
        String csvFileSeparator=LPTestingOutFormat.TESTING_FILES_FIELD_SEPARATOR;
        Object[][] csvFileContent = LPArray.convertCSVinArray(csvPathName, csvFileSeparator); 

        StringBuilder fileContentBuilder = new StringBuilder(0);
        fileContentBuilder.append(LPTestingOutFormat.getHtmlStyleHeader(this.getClass().getSimpleName(), csvFileName, null, null));
        
        
        try (PrintWriter out = response.getWriter()) {
            Map<String, Object> csvHeaderTags = LPTestingOutFormat.getCSVHeader(LPArray.convertCSVinArray(csvPathName, "="));
            if (csvHeaderTags.containsKey(LPPlatform.LAB_FALSE)){
                fileContentBuilder.append("There are missing tags in the file header: ").append(csvHeaderTags.get(LPPlatform.LAB_FALSE));
                out.println(fileContentBuilder.toString()); 
                return;
            }            
            
            Integer numEvaluationArguments = Integer.valueOf(csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.NUM_EVALUATION_ARGUMENTS.getTagValue().toString()).toString());   
            Integer numHeaderLines = Integer.valueOf(csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.NUM_HEADER_LINES.getTagValue().toString()).toString());   
            String table1Header = csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.TABLE_NAME.getTagValue().toString()+"1").toString();               
             StringBuilder fileContentTable1Builder = new StringBuilder(0);
            fileContentTable1Builder.append(LPTestingOutFormat.createTableWithHeader(table1Header, numEvaluationArguments));
               
            for (Integer iLines=numHeaderLines;iLines<csvFileContent.length;iLines++){
                tstAssertSummary.increaseTotalTests();
                TestingAssert tstAssert = new TestingAssert(csvFileContent[iLines], numEvaluationArguments, false);

                Integer lineNumCols = csvFileContent[0].length-1;
                String familyName = null;
                if (lineNumCols>=numEvaluationArguments+1)                
                    familyName = LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+1]);
                String[] fieldsToRetrieve = new String[0];
                if (lineNumCols>=numEvaluationArguments+2)                
                    fieldsToRetrieve = LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+2]);
                
                UnitsOfMeasurement uom = new UnitsOfMeasurement(null, null);

                Object[][] tableGet = uom.getAllUnitsPerFamily(familyName, fieldsToRetrieve);    
                fileContentTable1Builder.append(LPTestingOutFormat.tableStart(""));
                for (int iRows=0;iRows<tableGet.length;iRows++){
                   fileContentTable1Builder.append(LPTestingOutFormat.ROW_START); 
                   fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(String.valueOf(iRows)));     
                   Boolean continueLoop=true;
                   for (int iColumns=0;iColumns<fieldsToRetrieve.length && Boolean.TRUE.equals(continueLoop);iColumns++){
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(tableGet[0][0].toString())) {
                             fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(String.valueOf(tableGet[0][3])))
                                     .append(LPTestingOutFormat.rowAddField(String.valueOf(tableGet[0][5])));
                             continueLoop=false;
                        }else{                       
                           fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(String.valueOf(tableGet[iRows][iColumns])));     
                        }
                   }    
                   fileContentTable1Builder.append(LPTestingOutFormat.ROW_END); 
                   
                }    
                fileContentTable1Builder.append(LPTestingOutFormat.TABLE_END);
                
                if (numEvaluationArguments>0){                    
                    Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, new Object[0], 4);
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate));                        
                }
                fileContentTable1Builder.append(LPTestingOutFormat.ROW_END);                        
            }   
            tstAssertSummary.notifyResults();
            Rdbms.closeRdbms();
            fileContentTable1Builder.append(LPTestingOutFormat.TABLE_END);
            fileContentBuilder.append(fileContentTable1Builder);          
            
            if (numEvaluationArguments>0){
                String fileContentSummary = LPTestingOutFormat.createSummaryTable(tstAssertSummary, numEvaluationArguments, null, null);
                fileContentBuilder.append(fileContentSummary);
            }
            fileContentBuilder.append(LPTestingOutFormat.BODY_END).append(LPTestingOutFormat.HTML_END);
            out.println(fileContentBuilder.toString());            
            LPTestingOutFormat.createLogFile(csvPathName, fileContentBuilder.toString());
        }
        catch(IOException error){
            Rdbms.closeRdbms();
            String exceptionMessage = error.getMessage();     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);                    
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
