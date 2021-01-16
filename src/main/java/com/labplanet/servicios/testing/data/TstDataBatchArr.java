/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.testing.data;

import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import databases.Rdbms;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Administrator
 */
public class TstDataBatchArr extends HttpServlet {

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
        String csvFileName = "tstDataBatchArray.txt"; 
        StringBuilder fileContentBuilder = new StringBuilder(0);
        String csvPathName = LPTestingOutFormat.TESTING_FILES_PATH+csvFileName; 
        String csvFileSeparator=LPTestingOutFormat.TESTING_FILES_FIELD_SEPARATOR;

        if (Rdbms.getRdbms().startRdbms()==null){
            fileContentBuilder.append(LPTestingOutFormat.MSG_DB_CON_ERROR);
            LPTestingOutFormat.createLogFile(csvPathName, fileContentBuilder.toString());
            return;
        }           
  
        try (PrintWriter out = response.getWriter()) {
            
            Object[][] dataSample2D = new Object[1][6];
            
            String[][] configSpecTestingArray = LPArray.convertCSVinArray(csvPathName, csvFileSeparator);                        
            fileContentBuilder.append(LPTestingOutFormat.getHtmlStyleHeader(this.getServletName(), csvFileName));
            
            for (Integer j=0;j<configSpecTestingArray[0].length;j++){                
                fileContentBuilder.append(LPTestingOutFormat.headerAddFields(configSpecTestingArray[0]));
            }            

            for (Integer i=1;i<configSpecTestingArray.length;i++){
                out.println("Line "+i.toString());
                fileContentBuilder.append("<tr>");
                String schemaPrefix=null;
                String tableName=null;
                String[] fieldName=null;                    
                String[] fieldValue=null;
                String[] setFieldName=null;                    
                String[] setFieldValue=null;
                String[] orderBy=null;                    
                String[] groupBy=null;
                String[] fieldsToRetrieve=null;   
                String actionName="";                     
                Object[] dataSample2Din1D = new Object[0];

                if (configSpecTestingArray[i][1]!=null){actionName = LPNulls.replaceNull(configSpecTestingArray[i][1]);}
                if (configSpecTestingArray[i][2]!=null){schemaPrefix = LPNulls.replaceNull(configSpecTestingArray[i][2]);}
                if (configSpecTestingArray[i][3]!=null){tableName = LPNulls.replaceNull(configSpecTestingArray[i][3]);}
                if (configSpecTestingArray[i][4]!=null){fieldName = configSpecTestingArray[i][4].split("\\|");}else{fieldName = new String[0];}              
                if (configSpecTestingArray[i][5]!=null){fieldValue = configSpecTestingArray[i][5].split("\\|");}else{fieldValue = new String[0];}
                if (configSpecTestingArray[i][6]!=null){fieldsToRetrieve = configSpecTestingArray[i][6].split("\\|");}else{fieldsToRetrieve = new String[0];}                  
                if (configSpecTestingArray[i][7]!=null){setFieldName = configSpecTestingArray[i][7].split("\\|");}else{setFieldName = new String[0];}              
                if (configSpecTestingArray[i][8]!=null){setFieldValue = configSpecTestingArray[i][8].split("\\|");}else{setFieldValue = new String[0];}
                if (configSpecTestingArray[i][9]!=null){orderBy = configSpecTestingArray[i][9].split("\\|");}else{orderBy = new String[0];}
                if (configSpecTestingArray[i][10]!=null){groupBy = configSpecTestingArray[i][10].split("\\|");}else{groupBy = new String[0];}
                
                Object[] fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue);
                if ( (fieldName!=null) && (fieldValue!=null) ){
                    //whereFieldsNameArr=labArr.addValueToArray1D(whereFieldsNameArr, whereFieldsName.split("\\|"));
                    //whereFieldsValueArr = labArr.addValueToArray1D(whereFieldsValueArr, labArr.convertStringWithDataTypeToObjectArray(whereFieldsValue.split("\\|")));                                                              
                }                         
                Object[] rowContent = new Object[]{i, actionName, schemaPrefix, tableName, Arrays.toString(fieldName), "<b>"+Arrays.toString(fieldValue)+"</b>"
                        ,Arrays.toString(fieldsToRetrieve), Arrays.toString(setFieldName), "<b>"+Arrays.toString(setFieldValue)+"</b>", Arrays.toString(orderBy)
                        , Arrays.toString(groupBy)};
                
                Object[] setFieldValues = LPArray.convertStringWithDataTypeToObjectArray(setFieldValue);
                //fileContentBuilder.append(LPTestingOutFormat.convertArrayInHtmlTable(LPArray.array1dTo2d(rowContent, rowContent.length)));
                
                Object[] allFunctionsBeingTested = new Object[0];                
                allFunctionsBeingTested = LPArray.addValueToArray1D(allFunctionsBeingTested, "CREATEBATCHARRAY");
                allFunctionsBeingTested = LPArray.addValueToArray1D(allFunctionsBeingTested, "INSERT");
                allFunctionsBeingTested = LPArray.addValueToArray1D(allFunctionsBeingTested, "GETRECORDFIELDSBYFILTER");
                allFunctionsBeingTested = LPArray.addValueToArray1D(allFunctionsBeingTested, "UPDATE");
                
                switch (actionName.toUpperCase()){
                    case "CREATEBATCHARRAY":   
                        //batch.dbCreateBatchArray();
                        Object[] exRec =  Rdbms.existsRecord(schemaPrefix, tableName, fieldName, fieldValues);
                        dataSample2D = LPArray.array1dTo2d(exRec, exRec.length);
                        break;
                    case "INSERT":                    
                        Object[] insRec = Rdbms.insertRecordInTable(schemaPrefix, tableName, fieldName, fieldValues);  
                        dataSample2D = LPArray.array1dTo2d(insRec, insRec.length);
                        break;
                    case "GETRECORDFIELDSBYFILTER":              
                        if (orderBy.length>0){
                            dataSample2D = Rdbms.getRecordFieldsByFilter(schemaPrefix, tableName, fieldName, fieldValues, fieldsToRetrieve, orderBy);
                        }else{
                            dataSample2D = Rdbms.getRecordFieldsByFilter(schemaPrefix, tableName, fieldName, fieldValues, fieldsToRetrieve);
                        }
                        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(dataSample2D[0][0].toString())){
                            dataSample2Din1D =  LPArray.array2dTo1d(dataSample2D);
                        }    
                        break;
                    case "UPDATE":                    
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
               if (dataSample2D[0].length==0){rowContent=LPArray.addValueToArray1D(rowContent, "No content in the array dataSample2D returned for function "+actionName);
               rowContent=LPArray.addValueToArray1D(rowContent, dataSample2D);}
/*               if (dataSample2D[0].length>0){fileContent = fileContent + "<td>"+dataSample2D[0][0].toString();}
                if (dataSample2D[0].length>1){fileContent = fileContent + ". "+LPNulls.replaceNull((String) dataSample2D[0][1]);}
                if (dataSample2D[0].length>2){fileContent = fileContent + ". "+LPNulls.replaceNull((String) dataSample2D[0][2]);}
                if (dataSample2D[0].length>3){fileContent = fileContent + ". "+LPNulls.replaceNull((String) dataSample2D[0][3]);}
                if (dataSample2D[0].length>4){fileContent = fileContent + ". "+LPNulls.replaceNull((String) dataSample2D[0][4]);}                
                if (dataSample2D[0].length>5){fileContent = fileContent + ". "+LPNulls.replaceNull((String) dataSample2D[0][5]);} */
                if ( ("GETRECORDFIELDSBYFILTER".equalsIgnoreCase(actionName)) && (!LPPlatform.LAB_FALSE.equalsIgnoreCase(dataSample2D[0][0].toString())) ){
                    rowContent=LPArray.addValueToArray1D(rowContent, Arrays.toString(dataSample2Din1D));
                    //fileContent = fileContent + LPTestingOutFormat.fieldEnd()+LPTestingOutFormat.fieldStart()+Arrays.toString(dataSample2Din1D);
                }
                fileContentBuilder.append(LPTestingOutFormat.convertArrayInHtmlTable(LPArray.array1dTo2d(rowContent, rowContent.length)));
                //fileContent = fileContent + LPTestingOutFormat.fieldEnd();
                //fileContent = fileContent +LPTestingOutFormat.rowEnd();
            }
            fileContentBuilder.append(LPTestingOutFormat.tableEnd());        
            out.println(fileContentBuilder.toString());

            csvPathName = csvPathName.replace(".txt", ".html");
            LPTestingOutFormat.createLogFile(csvPathName, fileContentBuilder.toString());
            Rdbms.closeRdbms();
            }   catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);   
                fileContentBuilder.append("</table>");    
                Logger.getLogger(fileContentBuilder.toString());
                Rdbms.closeRdbms();
            String exceptionMessage = ex.getMessage();     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null);                    
        } finally {
            // release database resources
            try {
                Rdbms.closeRdbms();   
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
