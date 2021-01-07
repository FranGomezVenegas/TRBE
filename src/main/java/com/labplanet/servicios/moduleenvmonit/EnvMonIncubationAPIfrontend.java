/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import functionaljavaa.instruments.incubator.DataIncubatorNoteBook;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class EnvMonIncubationAPIfrontend extends HttpServlet {

    public enum EnvMonIncubationAPIfrontendEndpoints{
        INCUBATOR_TEMP_READINGS("INCUBATOR_TEMP_READINGS", "", 
                new LPAPIArguments[]{new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_INCUBATOR_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_INCUBATOR_NUM_POINTS, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),}),
        INCUBATORS_LIST("INCUBATORS_LIST", "", 
                new LPAPIArguments[]{}),
        ;
        private EnvMonIncubationAPIfrontendEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){                
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }  
            hm.put(request, argValues);            
            return hm;
        }        
        public String getName(){
            return this.name;
        }
        public String getSuccessMessageCode(){
            return this.successMessageCode;
        }           

        /**
         * @return the arguments
         */
        public LPAPIArguments[] getArguments() {
            return arguments;
        }     
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
    }    
    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN;
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

    Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
        LPFrontEnd.servletReturnResponseError(request, response, 
            LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
        return;          
    }             
    String schemaPrefix = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX);            
    String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
    EnvMonIncubationAPIfrontendEndpoints endPoint = null;
    try{
        endPoint = EnvMonIncubationAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
    }catch(Exception e){
        LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);              
        return;                   
    }
    Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                   
    if (!LPFrontEnd.servletStablishDBConection(request, response, false))return;
        
    switch (endPoint){
        case INCUBATORS_LIST: 
            String[] fieldsToRetrieve=new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()};
            String[] fieldsToRetrieveReadings=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_ID.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                        TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(),
                        TblsEnvMonitData.InstrIncubatorNoteBook.FLD_TEMPERATURE.getName()};     
            Rdbms.stablishDBConection();
            Object[][] incubatorsList=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                    new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, new Object[]{true}, 
                    fieldsToRetrieve, new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()});
            JSONArray jArr = new JSONArray();
            for (Object[] currInstrument: incubatorsList){
                JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrument);
                Object[][] instrReadings=DataIncubatorNoteBook.getLastTemperatureReading(schemaPrefix, currInstrument[0].toString(), 5);                    
                JSONArray jReadingsArr = new JSONArray();
                for (Object[] curReading: instrReadings){
                    JSONObject jReadingsObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieveReadings, curReading);
                    jReadingsArr.add(jReadingsObj);
                }
                jObj.put("LAST_READINGS", jReadingsArr);
                jArr.add(jObj);
            }
            Rdbms.closeRdbms();  
            LPFrontEnd.servletReturnSuccess(request, response, jArr);
            break;
        case INCUBATOR_TEMP_READINGS:
            String instrName=argValues[0].toString();
            String numPoints=argValues[1].toString();
            Integer numPointsInt=null;
            fieldsToRetrieve=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.FLD_ID.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_EVENT_TYPE.getName(),
                        TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_ON.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.FLD_CREATED_BY.getName(),
                        TblsEnvMonitData.InstrIncubatorNoteBook.FLD_TEMPERATURE.getName()};            
            if (numPoints!=null && numPoints.length()>0) numPointsInt=Integer.valueOf(numPoints);                    
            Object[][] instrReadings=DataIncubatorNoteBook.getLastTemperatureReading(schemaPrefix, instrName, numPointsInt);                    
            Rdbms.closeRdbms();  
            jArr = new JSONArray();
            for (Object[] currReading: instrReadings){
                JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currReading);
                jArr.add(jObj);
            }
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
