/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import functionaljavaa.instruments.incubator.DataIncubatorNoteBook;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.queries.QueryUtilitiesEnums;
/**
 *
 * @author User
 */
public class EnvMonIncubationAPIfrontend extends HttpServlet {

    public enum EnvMonIncubationAPIfrontendEndpoints implements EnumIntEndpoints{
        INCUBATOR_TEMP_READINGS("INCUBATOR_TEMP_READINGS", "", 
                new LPAPIArguments[]{new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_INCUBATOR_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_INCUBATOR_NUM_POINTS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),}, null),
        INCUBATORS_LIST("INCUBATORS_LIST", "", 
                new LPAPIArguments[]{new LPAPIArguments("incubStage", LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, null),
        ;
        private EnvMonIncubationAPIfrontendEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums; 
            this.outputObjectTypes=outputObjectTypes;            
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
        @Override        public String getName(){return this.name;}
        @Override        public String getSuccessMessageCode(){return this.successMessageCode;}           
        @Override        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        @Override        public LPAPIArguments[] getArguments() {return arguments;}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }    
    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
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

    ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }
    String actionName=procReqInstance.getActionName();
    String language=procReqInstance.getLanguage();
    String procInstanceName = procReqInstance.getProcedureInstance();
    

    try (PrintWriter out = response.getWriter()) {

        EnvMonIncubationAPIfrontendEndpoints endPoint = null;
        try{
            endPoint = EnvMonIncubationAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language);              
            return;                   
        }
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                   
        if (!LPFrontEnd.servletStablishDBConection(request, response))return;
        switch (endPoint){
            case INCUBATORS_LIST: 
                String[] fieldsToRetrieve=new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.STAGE.getName()};
                String[] fieldsToRetrieveReadings=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.ID.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.EVENT_TYPE.getName(),
                            TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_BY.getName(),
                            TblsEnvMonitData.InstrIncubatorNoteBook.TEMPERATURE.getName()};     
                Object[][] incubatorsList=QueryUtilitiesEnums.getTableData(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
                    EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, LPArray.convertArrayToString(fieldsToRetrieve, "\\|", "")),
                    new String[]{TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()}, new Object[]{true}, 
                    new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()});
                JSONArray jArr = new JSONArray();
                for (Object[] currInstrument: incubatorsList){
                    JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrument);
                    Object[][] instrReadings=DataIncubatorNoteBook.getLastTemperatureReading( currInstrument[0].toString(), 5);                    
                    JSONArray jReadingsArr = new JSONArray();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrReadings[0][0].toString()))
                        jObj.put("LAST_READINGS", "No readings");
                    else{
                        for (Object[] curReading: instrReadings)
                            jReadingsArr.add(LPJson.convertArrayRowToJSONObject(fieldsToRetrieveReadings, curReading));                    
                        jObj.put("LAST_READINGS", jReadingsArr);
                    }                    
                    jArr.add(jObj);
                }
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                break;
            case INCUBATOR_TEMP_READINGS:
                String instrName=argValues[0].toString();
                String numPoints=LPNulls.replaceNull(argValues[1]).toString();
                Integer numPointsInt=null;
                fieldsToRetrieve=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.ID.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.EVENT_TYPE.getName(),
                            TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_BY.getName(),
                            TblsEnvMonitData.InstrIncubatorNoteBook.TEMPERATURE.getName()};            
                if (numPoints!=null && numPoints.length()>0) numPointsInt=Integer.valueOf(numPoints);                    
                Object[][] instrReadings=DataIncubatorNoteBook.getLastTemperatureReading(instrName, numPointsInt);                    
                jArr = new JSONArray();
                for (Object[] currReading: instrReadings)
                    jArr.add(LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currReading));                
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                break;
            default:      
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language);                                                                  
        }
    }catch(Exception e){      
        procReqInstance.killIt();
        String exceptionMessage =e.getMessage();
        if (exceptionMessage==null){exceptionMessage="null exception";}
        response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
        LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null);      
    } finally {
        // release database resources
        try {
            procReqInstance.killIt();
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
