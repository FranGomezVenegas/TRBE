/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_NUM_DAYS;
import databases.Rdbms;
import databases.SqlStatement;
import functionaljavaa.instruments.incubator.DataIncubatorNoteBook;
import functionaljavaa.platform.doc.EndPointsToRequirements;
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
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;
import trazit.queries.QueryUtilitiesEnums;
/**
 *
 * @author User
 */
public class EnvMonIncubatorAPIqueries extends HttpServlet {

    public enum EnvMonIncubatorAPIqueriesEndpoints implements EnumIntEndpoints{
        GET_INCUBATOR_TEMP_READINGS("GET_INCUBATOR_TEMP_READINGS", "", 
                new LPAPIArguments[]{new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_INCUBATOR_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_INCUBATOR_NUM_POINTS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),}, null),
        GET_INCUBATORS_LIST("GET_INCUBATORS_LIST", "", 
                new LPAPIArguments[]{new LPAPIArguments("incubStage", LPAPIArguments.ArgumentType.STRING.toString(), false, 6)}, null),
        GET_INCUBATORS_LIST_BY_STAGE("GET_INCUBATORS_LIST_BY_STAGE", "", 
                new LPAPIArguments[]{new LPAPIArguments("incubStage", LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, null),
        GET_INCUBATORS_DEACTIVATED_LAST_N_DAYS("GET_INCUBATORS_DEACTIVATED_LAST_N_DAYS","",new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_NUM_DAYS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects),
        ;
        private EnvMonIncubatorAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
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
        @Override        public String getApiUrl(){return GlobalVariables.ApiUrls.ENVMON_INCUBATOR_QUERIES.getUrl();}
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
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
    String actionName=procReqInstance.getActionName();
    String language=procReqInstance.getLanguage();
    

    try (PrintWriter out = response.getWriter()) {

        EnvMonIncubatorAPIqueriesEndpoints endPoint = null;
        try{
            endPoint = EnvMonIncubatorAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
            return;                   
        }
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                   
        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response)))return;
        switch (endPoint){
            case GET_INCUBATORS_LIST_BY_STAGE: 
                //String[] fieldsToRetrieve=new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.STAGE.getName()};

            case GET_INCUBATORS_LIST: 
                String[] fieldsToRetrieve = EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableFields());                    
//                String[] fieldsToRetrieve=new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.STAGE.getName()};
                String[] fieldsToRetrieveReadings=new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.ID.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.EVENT_TYPE.getName(),
                            TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_BY.getName(),
                            TblsEnvMonitData.InstrIncubatorNoteBook.TEMPERATURE.getName()};     
                EnumIntTableFields[] fieldsToRetrieveObj=EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, fieldsToRetrieve);
                Object[][] incubatorsList=QueryUtilitiesEnums.getTableData(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
                    fieldsToRetrieveObj,
                    new String[]{TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()}, new Object[]{true}, 
                    new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()});
                JSONArray jArr = new JSONArray();
                for (Object[] currInstrument: incubatorsList){
                    JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrument);
                    Object[][] instrReadings=DataIncubatorNoteBook.getLastTemperatureReading( currInstrument[EnumIntTableFields.getFldPosicInArray(fieldsToRetrieveObj, TblsEnvMonitConfig.InstrIncubator.NAME.getName())].toString(), 5);                    
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
            case GET_INCUBATOR_TEMP_READINGS:
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
            case GET_INCUBATORS_DEACTIVATED_LAST_N_DAYS:
                    String numDays = LPNulls.replaceNull(argValues[0]).toString();
                    if (numDays.length()==0) numDays=String.valueOf(7);
                    int numDaysInt=0-Integer.valueOf(numDays);
                    fieldsToRetrieve = EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableFields());
                    Object[][] prodLotsDeactivatedLastDays=QueryUtilitiesEnums.getTableData(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
                        EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, "ALL"),
                        new String[]{TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName(), TblsEnvMonitConfig.InstrIncubator.LAST_DEACTIVATION_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.GREATER_THAN.getSqlClause()}, 
                        new Object[]{false, LPDate.addDays(LPDate.getCurrentDateWithNoTime(), numDaysInt)}, 
                        new String[]{TblsEnvMonitConfig.InstrIncubator.LAST_DEACTIVATION_ON.getName()+" desc"});
                    jArr = new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(prodLotsDeactivatedLastDays[0][0].toString())){
                        for (Object[] currIncident: prodLotsDeactivatedLastDays){
                            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();  
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);        
                    return;
            default:      
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());                                                                  
        }
    }catch(Exception e){      
        procReqInstance.killIt();
        String exceptionMessage =e.getMessage();
        if (exceptionMessage==null){exceptionMessage="null exception";}
        response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
        LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);      
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
