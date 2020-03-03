/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.testing.platform;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import databases.Rdbms;
import functionaljavaa.testingscripts.TestingAssert;
import functionaljavaa.testingscripts.TestingAssertSummary;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Administrator
 */
public class DBActions extends HttpServlet {
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     */
        public static final String ENDPOINTS_BEING_TESTED_EXISTSRECORD="EXISTSRECORD";

    /**
     *
     */
    public static final String ENDPOINTS_BEING_TESTED_INSERT="INSERT";

    /**
     *
     */
    public static final String ENDPOINTS_BEING_TESTED_GETRECORDFIELDSBYFILTER="GETRECORDFIELDSBYFILTER";

    /**
     *
     */
    public static final String ENDPOINTS_BEING_TESTED_UPDATE="UPDATE";

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        String language = LPFrontEnd.setLanguage(request); 

        Object[][] dataSample2D = new Object[1][6];
        Object[] dataSample2Din1D = new Object[0];
        
        String csvFileName = "dbActions.txt"; 
        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();

        String csvPathName = LPTestingOutFormat.TESTING_FILES_PATH+csvFileName; 
        String csvFileSeparator=LPTestingOutFormat.TESTING_FILES_FIELD_SEPARATOR;
        
        Object[][] csvFileContent = LPArray.convertCSVinArray(csvPathName, csvFileSeparator); 
        StringBuilder fileContentBuilder = new StringBuilder();
        fileContentBuilder.append(LPTestingOutFormat.getHtmlStyleHeader(this.getClass().getSimpleName(), csvFileName));
                
        try (PrintWriter out = response.getWriter()) {
     
            HashMap<String, Object> csvHeaderTags = LPTestingOutFormat.getCSVHeader(LPArray.convertCSVinArray(csvPathName, "="));
            if (csvHeaderTags.containsKey(LPPlatform.LAB_FALSE)){
                fileContentBuilder.append("There are missing tags in the file header: ").append(csvHeaderTags.get(LPPlatform.LAB_FALSE));
                out.println(fileContentBuilder.toString()); 
                return;
            }            
            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}   
        
                
            Integer numEvaluationArguments = Integer.valueOf(csvHeaderTags.get(LPTestingOutFormat.FILEHEADER_NUM_EVALUATION_ARGUMENTS).toString());   
            Integer numHeaderLines = Integer.valueOf(csvHeaderTags.get(LPTestingOutFormat.FILEHEADER_NUM_HEADER_LINES_TAG_NAME).toString());   
            String table1Header = csvHeaderTags.get(LPTestingOutFormat.FILEHEADER_TABLE_NAME_TAG_NAME+"1").toString();               
            StringBuilder fileContentTable1Builder = new StringBuilder();
            fileContentTable1Builder.append(LPTestingOutFormat.createTableWithHeader(table1Header, numEvaluationArguments));
            
            for (Integer iLines=numHeaderLines;iLines<csvFileContent.length;iLines++){
                tstAssertSummary.increaseTotalTests();
                TestingAssert tstAssert = new TestingAssert(csvFileContent[iLines], numEvaluationArguments);
                
                Integer lineNumCols = csvFileContent[0].length-1;
                String actionName = null;
                if (lineNumCols>=numEvaluationArguments)
                    {actionName=LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments]);}
                String schemaPrefix = null;
                if (lineNumCols>=numEvaluationArguments+1)
                    schemaPrefix = LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+1]);
                String tableName = null;
                if (lineNumCols>=numEvaluationArguments+2)
                    tableName = LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+2]);
                String[] fieldName = null;
                if (lineNumCols>=numEvaluationArguments+3)                
                    fieldName = LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+3]);
                String[] fieldValue = null;
                if (lineNumCols>=numEvaluationArguments+4)
                     fieldValue = LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+4]);
                String[] fieldsToRetrieve = null;
                if (lineNumCols>=numEvaluationArguments+5)
                     fieldsToRetrieve = LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+5]);
                String[] setFieldName = null;
                if (lineNumCols>=numEvaluationArguments+6)
                     setFieldName = LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+6]);
                String[] setFieldValue = null;
                if (lineNumCols>=numEvaluationArguments+7)
                    {setFieldValue=LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+7]);}
                String[] orderBy = null;
                if (lineNumCols>=numEvaluationArguments+8)
                    {orderBy=LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+8]);}
                String[] groupBy = null;
                if (lineNumCols>=numEvaluationArguments+9)
                    {groupBy=LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+9]);}
                
                Object[] fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue);
                if ( (fieldName!=null) && (fieldValue!=null) ){
                    for (int iFields=0; iFields<fieldName.length; iFields++){
                        if (LPPlatform.isEncryptedField(schemaPrefix, "sample", fieldName[iFields])){                
                            HashMap<String, String> hm = LPPlatform.encryptEncryptableFieldsAddBoth(fieldName[iFields], fieldValues[iFields].toString());
                            fieldName[iFields]= hm.keySet().iterator().next();    
                            if ( hm.get(fieldName[iFields]).length()!=fieldValues[iFields].toString().length()){
                                String newWhereFieldValues = hm.get(fieldName[iFields]);
                                fieldValues[iFields]=newWhereFieldValues;
                            }
                        }
                    }                                    
                }     
                Object[] setFieldValues = null;
                if (setFieldValue!=null){
                    setFieldValues = LPArray.convertStringWithDataTypeToObjectArray(setFieldValue);}
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines-numHeaderLines+1, actionName, schemaPrefix, tableName,
                    Arrays.toString(fieldName), Arrays.toString(fieldValue), Arrays.toString(fieldsToRetrieve), 
                    Arrays.toString(setFieldName), Arrays.toString(setFieldValue), Arrays.toString(orderBy), Arrays.toString(groupBy)}));
                
                Object[] allFunctionsBeingTested = new Object[0];                
                allFunctionsBeingTested = LPArray.addValueToArray1D(allFunctionsBeingTested, ENDPOINTS_BEING_TESTED_EXISTSRECORD);
                allFunctionsBeingTested = LPArray.addValueToArray1D(allFunctionsBeingTested, ENDPOINTS_BEING_TESTED_INSERT);
                allFunctionsBeingTested = LPArray.addValueToArray1D(allFunctionsBeingTested, ENDPOINTS_BEING_TESTED_GETRECORDFIELDSBYFILTER);
                allFunctionsBeingTested = LPArray.addValueToArray1D(allFunctionsBeingTested, ENDPOINTS_BEING_TESTED_UPDATE);

                switch (actionName.toUpperCase()){
                    case ENDPOINTS_BEING_TESTED_EXISTSRECORD:   
                        Object[] exRec =  Rdbms.existsRecord(schemaPrefix, tableName, fieldName, fieldValues);
                        dataSample2D = LPArray.array1dTo2d(exRec, exRec.length);
                        break;
                    case ENDPOINTS_BEING_TESTED_INSERT:                    
                        Object[] insRec = Rdbms.insertRecordInTable(schemaPrefix, tableName, fieldName, fieldValues);  
                        dataSample2D = LPArray.array1dTo2d(insRec, insRec.length);
                        break;
                    case ENDPOINTS_BEING_TESTED_GETRECORDFIELDSBYFILTER:              
                        if (orderBy!=null && orderBy.length>0){
                            dataSample2D = Rdbms.getRecordFieldsByFilter(schemaPrefix, tableName, fieldName, fieldValues, fieldsToRetrieve, orderBy);
                        }else{
                            dataSample2D = Rdbms.getRecordFieldsByFilter(schemaPrefix, tableName, fieldName, fieldValues, fieldsToRetrieve);
                        }
                        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(dataSample2D[0][0].toString())){
                            dataSample2Din1D =  LPArray.array2dTo1d(dataSample2D);
                        }    
                        break;
                    case ENDPOINTS_BEING_TESTED_UPDATE:                    
                        Object[] updRec = Rdbms.updateRecordFieldsByFilter(schemaPrefix, tableName, setFieldName, setFieldValues, fieldName, fieldValues);  
                        dataSample2D = LPArray.array1dTo2d(updRec, updRec.length);
                        break;                        
                    default:
                        String errorCode = "ERROR: FUNCTION NOT RECOGNIZED";
                        Object[] errorDetail = new Object [1];
                        errorDetail[0]="The function <*1*> is not one of the declared ones therefore nothing can be performed for it. Functions are: <*2*>";
                        errorDetail = LPArray.addValueToArray1D(errorDetail, actionName);
                        errorDetail = LPArray.addValueToArray1D(errorDetail, Arrays.toString(allFunctionsBeingTested));
                        Object[] trapErrorMessage = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, errorCode, errorDetail);            
                        dataSample2D = LPArray.array1dTo2d(trapErrorMessage, trapErrorMessage.length);
                        break;
                }                    
                if (dataSample2D[0].length==0){
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddField("No content in the array dataSample2D returned for function"));
                }else{
                    if ( (ENDPOINTS_BEING_TESTED_GETRECORDFIELDSBYFILTER.equalsIgnoreCase(actionName)) && (!LPPlatform.LAB_FALSE.equalsIgnoreCase(dataSample2D[0][0].toString())) ){
                        if (numEvaluationArguments==0){                    
                            fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(dataSample2Din1D)));                     
                        }
                        if (numEvaluationArguments>0){                    
                            Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, dataSample2Din1D);
                            fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate));                        
                        }
                    }
                    if ( (!ENDPOINTS_BEING_TESTED_GETRECORDFIELDSBYFILTER.equalsIgnoreCase(actionName)) && (!LPPlatform.LAB_FALSE.equalsIgnoreCase(dataSample2D[0][0].toString())) ){                        
                        StringBuilder dataSampleFldOutBuilder = new StringBuilder();
                        for (int iFields=0; iFields<dataSample2D[0].length;iFields++){
                            dataSampleFldOutBuilder.append(LPNulls.replaceNull((String) dataSample2D[0][iFields])).append(". ");
                        }
                        if (numEvaluationArguments==0){                    
                            fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(dataSampleFldOutBuilder.toString()));
                        }
                        if (numEvaluationArguments>0){                    
                            Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, new Object[]{dataSampleFldOutBuilder});
                            fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate));                        
                        }
                    }
                }
                fileContentTable1Builder.append(LPTestingOutFormat.rowEnd());                                                
            }                          
            tstAssertSummary.notifyResults();
            fileContentTable1Builder.append(LPTestingOutFormat.tableEnd());
            String fileContentSummary = LPTestingOutFormat.createSummaryTable(tstAssertSummary);
            fileContentTable1Builder.append(fileContentSummary);
            fileContentTable1Builder.append(LPTestingOutFormat.bodyEnd()).append(LPTestingOutFormat.htmlEnd());
            out.println(fileContentTable1Builder.toString());            
            LPTestingOutFormat.createLogFile(csvPathName, fileContentTable1Builder.toString());
            Rdbms.closeRdbms();
            tstAssertSummary=null; 
        }
        catch(IOException error){
            Rdbms.closeRdbms();
            tstAssertSummary=null; 
            String exceptionMessage = error.getMessage();     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null);                    
        } finally {
            // release database resources
            try {
                Rdbms.closeRdbms();   
            } catch (Exception ignore) {
            }
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
        }catch(IOException e){
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
        }catch(IOException e){
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
