/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app.procs;

import static com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.MANDATORY_PARAMS_MAIN_SERVLET_PROCEDURE;
import databases.TblsAppProcConfig;
import functionaljavaa.instruments.ConfigInstruments;
import functionaljavaa.instruments.InstrumentsEnums.InstrumentsAPIactionsEndpoints;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

import org.json.simple.JSONObject;
import trazit.session.InternalMessage;
/**
 *
 * @author User
 */
public class InstrumentsAPIactions extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);     

        
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false, true);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        InternalMessage actionDiagnoses=null;

        InstrumentsAPIactionsEndpoints endPoint = null;
        try{
            endPoint = InstrumentsAPIactionsEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
            return;                   
        }
        JsonObject jsonObject=null;
        String[] argList=new String[]{};
        LPAPIArguments[] arguments = endPoint.getArguments();
        for (LPAPIArguments curArg: arguments){
            argList=LPArray.addValueToArray1D(argList, curArg.getName());
        }
        argList=LPArray.addValueToArray1D(argList, MANDATORY_PARAMS_MAIN_SERVLET_PROCEDURE.split("\\|"));
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());  
        String instrName=argValues[0].toString();
        ConfigInstruments instr=new ConfigInstruments(instrName);
        try (PrintWriter out = response.getWriter()) {
            switch (endPoint){
                case NEW_INSTRUMENT:
                    String fieldName=argValues[1].toString();
                    String fieldValue=argValues[2].toString();
                    String[] fieldNames=null;
                    Object[] fieldValues=null;
                    if (fieldName!=null) fieldNames = fieldName.split("\\|");
                    if (fieldValue!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    actionDiagnoses=ConfigInstruments.createNewInstrument(instrName, fieldNames, fieldValues);
                    break;
                case TURN_ON_LINE:
                    fieldName=argValues[1].toString();
                    fieldValue=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fieldName!=null) fieldNames = fieldName.split("\\|");
                    if (fieldValue!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    actionDiagnoses=instr.turnOnLine(fieldNames, fieldValues);
                    break;
                case TURN_OFF_LINE:
                    fieldName=argValues[1].toString();
                    fieldValue=argValues[2].toString();
                    fieldNames=null;
                    fieldValues=null;
                    if (fieldName!=null) fieldNames = fieldName.split("\\|");
                    if (fieldValue!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    instr=new ConfigInstruments(instrName);
                    actionDiagnoses=instr.turnOffLine(fieldNames, fieldValues);
                    break;
                case START_CALIBRATION:
                    instr=new ConfigInstruments(instrName);
                    actionDiagnoses=instr.startCalibration();
                    break;
                case COMPLETE_CALIBRATION:
                    instr=new ConfigInstruments(instrName);
                    actionDiagnoses=instr.completeCalibration();
                    break;
/*                case CONFIRM_INCIDENT:
                    incId=(Integer) argValues[0];
                    AppIncident inc=new AppIncident(incId);
                    actionDiagnoses = inc.confirmIncident(incId, argValues[1].toString());
                    break;
                case ADD_NOTE_INCIDENT:
                    incId=(Integer) argValues[0];
                    inc=new AppIncident(incId);
                    String newNote=argValues[2].toString();
                    actionDiagnoses = inc.addNoteIncident(incId, argValues[1].toString(), newNote);
                    break;                    
                case CLOSE_INCIDENT:
                    incId=(Integer) argValues[0];
                    inc=new AppIncident(incId);
                    actionDiagnoses = inc.closeIncident(incId, argValues[1].toString());
                    break;                    
                case REOPEN_INCIDENT:
                    incId=(Integer) argValues[0];
                    inc=new AppIncident(incId);
                    actionDiagnoses = inc.reopenIncident(incId, argValues[1].toString());
                    break;    */      
                default:
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, "endpointNotFound", null);   
                    return;
            }    
            
            if (actionDiagnoses!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())){  
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, null, null);   
            }else{
                RelatedObjects rObj=RelatedObjects.getInstanceForActions();
                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsAppProcConfig.Instruments.TBL.getName(), "instruments", instrName);                
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), new Object[]{instrName}, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
            }           
        }catch(Exception e){   
            // Rdbms.closeRdbms();                   
            procReqInstance.killIt();
            String[] errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
        } finally {
            instr=null;
            // release database resources
            try {           
                procReqInstance.killIt();
                // Rdbms.closeRdbms();   
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
        } catch (IOException ex) {
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

/*        JSONArray hdrJArr=new JSONArray();
        Enumeration headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            System.out.println(headerName + " = " + request.getHeader(headerName));
            JSONObject jObj=new JSONObject();
            jObj.put(headerName, request.getHeader(headerName));
            hdrJArr.add(jObj);
        }
        

        System.out.println("\n\nParameters");

        JSONArray paramJArr=new JSONArray();
        Enumeration params = request.getParameterNames();
        String theBody="";
        while(params.hasMoreElements()){
            String paramName = (String)params.nextElement();
            System.out.println(paramName + " = " + request.getParameter(paramName));
            JSONObject jObj=new JSONObject();
            jObj.put(paramName, request.getParameter(paramName));
            paramJArr.add(jObj);
            if (paramName.length()>0 && request.getParameter(paramName).length()==0)
                theBody=paramName;
        }
        JsonObject jsonObject=(JsonObject) objToJsonObj[1];
        String jsonObjType = jsonObject.get("object_type").getAsString();
        if (!objectType.toUpperCase().contains(jsonObjType.toUpperCase())){
            this.diagnostic=new Object[]{LPPlatform.LAB_FALSE, "objectType in record and objectType in the JsonObject mismatch"};
            return;
        }
*/
        //HttpResponse.BodyHandler<String> ofString = HttpResponse.BodyHandlers.ofString();
        //String ofStringStr=ofString.toString();
/*                    String requestArgValue=request.getParameter("reduxState");
        String firstNamev=request.getParameter("firstName");
        String dataV=request.getParameter("data");
        String dbNameV=request.getParameter("dbName");
        Enumeration<String> attributeNames = request.getAttributeNames();
        Enumeration<String> headerNames = request.getHeaderNames();
        Enumeration<String> parameterNames = request.getParameterNames();
        requestArgValue=LPNulls.replaceNull(request.getAttribute("reduxState")).toString();
        firstNamev=LPNulls.replaceNull(request.getAttribute("firstName")).toString();
        dataV=LPNulls.replaceNull(request.getAttribute("data")).toString();
        //dbNameV=LPNulls.replaceNull(request.getAttribute("reduxState")).toString();
  */      
//        Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\A");
//        String  s.hasNext() ? s.next() : ""; 
        
        try {
            processRequest(request, response);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return;
        
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
