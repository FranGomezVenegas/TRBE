/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulegenoma;

import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import databases.Rdbms;
import functionaljavaa.batch.incubator.DataBatchIncubator;
import functionaljavaa.batch.incubator.DataBatchIncubatorStructured;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import static functionaljavaa.batch.incubator.DataBatchIncubatorStructured.BATCHCONTENTSEPARATORSTRUCTUREDBATCH;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class ProjectAPIFrontend extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        String language = LPFrontEnd.setLanguage(request); 

        try (PrintWriter out = response.getWriter()) {

        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, GenomaProjectAPI.MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
            return;          
        }             
        String schemaPrefix = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX);            
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);

       // Token token = new Token(finalToken);

        if (!LPFrontEnd.servletStablishDBConection(request, response, false))return;

        switch (actionName.toUpperCase()){
        case "API_ENDPOINT_ACTIVE_BATCH_LIST": 
            Rdbms.stablishDBConection();
            String[] fieldsToRetrieve=new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName(), TblsEnvMonitData.IncubBatch.FLD_TYPE.getName()
                , TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_VERSION.getName()
                , TblsEnvMonitData.IncubBatch.FLD_INCUBATION_INCUBATOR.getName(), TblsEnvMonitData.IncubBatch.FLD_UNSTRUCT_CONTENT.getName()
                , TblsEnvMonitData.IncubBatch.FLD_INCUBATION_START.getName(), TblsEnvMonitData.IncubBatch.FLD_INCUBATION_END.getName()
                , TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_ROWS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_COLS.getName()
                , TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_POSITIONS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName()
                , TblsEnvMonitData.IncubBatch.FLD_STRUCT_ROWS_NAME.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_COLS_NAME.getName() 
                , TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
            Object[][] activeBatchesList=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                    new String[]{TblsEnvMonitData.IncubBatch.FLD_ACTIVE.getName()}, new Object[]{true}, 
                    fieldsToRetrieve, new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()});
            JSONArray jArr = new JSONArray();
            for (Object[] currBatch: activeBatchesList){
                JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currBatch);
                
                if (DataBatchIncubator.BatchIncubatorType.UNSTRUCTURED.toString().equalsIgnoreCase(currBatch[1].toString())){
                    String unstructuredContent=LPNulls.replaceNull((String)currBatch[5]);
                    if (unstructuredContent!=null && unstructuredContent.length()>0){ 
                        String fieldsSeparator="\\*";
                        String[] fieldsTag = new String[]{"sample_id", "incubation_moment"};
                        String[] samplesArr = unstructuredContent.split("\\|");
                        JSONArray jbatchSamplesArr = new JSONArray();
                        for (String currSample: samplesArr){
                            String[] currSampleArr=currSample.split(fieldsSeparator);
                            JSONObject jReadingsObj=LPJson.convertArrayRowToJSONObject(fieldsTag, currSampleArr);
                            jbatchSamplesArr.add(jReadingsObj);
                        }
                        jObj.put("SAMPLES_ARRAY", jbatchSamplesArr);
                        jObj.put("NUM_SAMPLES", samplesArr.length);                    
                    }
                }else if (DataBatchIncubator.BatchIncubatorType.STRUCTURED.toString().equalsIgnoreCase(currBatch[1].toString())){
                    Integer totalRows=(Integer)currBatch[8];
                    Integer totalCols=(Integer)currBatch[9];
                    String[] rowsName=currBatch[12].toString().split(DataBatchIncubatorStructured.BATCHCONTENTSEPARATORSTRUCTUREDBATCH);
                    String[] colsName=currBatch[13].toString().split(DataBatchIncubatorStructured.BATCHCONTENTSEPARATORSTRUCTUREDBATCH);
                    String[] batchContent1D=currBatch[14].toString().split(BATCHCONTENTSEPARATORSTRUCTUREDBATCH);
                    String[][] batchContent2D=LPArray.array1dTo2d(batchContent1D, totalCols);
                    
                    JSONArray jbatchSamplesArr = new JSONArray();
                    for (int x=0;x<totalRows;x++){
                        for (int y=0;y<totalCols;y++){
                            JSONObject posicObj=new JSONObject();
                            posicObj.put("x", x+1);
                            posicObj.put("y", y+1);
                            posicObj.put("posic name", rowsName[x]+colsName[y]);
                            posicObj.put("content", batchContent2D[x][y]);
                            jbatchSamplesArr.add(posicObj);
                        }
                    }
                    jObj.put("SAMPLES_ARRAY", jbatchSamplesArr);
                }else jObj.put("NUM_SAMPLES", 0);
                
                jArr.add(jObj);
            }
            Rdbms.closeRdbms();  
            LPFrontEnd.servletReturnSuccess(request, response, jArr);
            break;        
        default:      
            Rdbms.closeRdbms(); 
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);                                                                  
    }
    }catch(Exception e){      
        String exceptionMessage =e.getMessage();
        if (exceptionMessage==null){exceptionMessage="null exception";}
        response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
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
