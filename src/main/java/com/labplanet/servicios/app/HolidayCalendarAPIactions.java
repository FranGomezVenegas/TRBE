/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import static com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.MANDATORY_PARAMS_MAIN_SERVLET_PROCEDURE;
import databases.TblsApp;
import functionaljavaa.holidayscalendar.HolidaysCalendar;
import functionaljavaa.holidayscalendar.HolidaysCalendarEnums.CalendarAPIactionsEndpoints;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
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
import trazit.session.ProcedureRequestSession;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
/**
 *
 * @author User
 */
public class HolidayCalendarAPIactions extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    

    public enum ParamsList{INCIDENT_ID("incidentId"),INCIDENT_TITLE("incidentTitle"),INCIDENT_DETAIL("incidentDetail"),
        NOTE("note"),NEW_STATUS("newStatus"),
        ;
        private ParamsList(String requestName){
            this.requestName=requestName;
        } 
        public String getParamName(){
            return this.requestName;
        }        
        private final String requestName;
    }
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

        try (PrintWriter out = response.getWriter()) {

            CalendarAPIactionsEndpoints endPoint = null;
            InternalMessage actionDiagnoses = null;
            try{
                endPoint = CalendarAPIactionsEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language);              
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
            Integer incId=null;
            switch (endPoint){
                case NEW_CALENDAR:
                    if (procReqInstance.getToken()==null)
                        procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false, true);
                    
                    actionDiagnoses = HolidaysCalendar.createNewCalendar(argValues[0].toString(), null, null);
                    //String incIdStr=actionDiagnoses[actionDiagnoses.length-1].toString();
                    //if (incIdStr!=null && incIdStr.length()>0) incId=Integer.valueOf(incIdStr);
                    break;
                case ADD_DATE_TO_CALENDAR:
                    actionDiagnoses = HolidaysCalendar.addDateToCalendar(argValues[0].toString(), (Date) argValues[1], (String) argValues[2], null, null);
                    break;
                case DELETE_DATE_FROM_GIVEN_CALENDAR:    
                    actionDiagnoses = HolidaysCalendar.deleteCalendarDate(argValues[0].toString(), (Integer) argValues[1]);
                    break;
            }    
            String diagnostic=actionDiagnoses.getDiagnostic();

            if (diagnostic!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic)){  
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, actionDiagnoses.getMessageCodeObj().getErrorCode(), actionDiagnoses.getMessageCodeVariables());   
            }else{

                RelatedObjects rObj=RelatedObjects.getInstanceForActions();
                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.INCIDENT.getTableName(), "incident", incId);                
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, new Object[]{incId}, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
                
/*
                RelatedObjects rObj=RelatedObjects.getInstanceForActions();
                
                //rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TablesAppProcData.INSTRUMENTS.getTableName(), "instruments", instrName);                
                //JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(endPoint.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), new Object[]{instrName}, rObj.getRelatedObject());
                JSONObject dataSampleJSONMsg = new JSONObject();
                rObj.killInstance();
                LPFrontEnd.responseJSONDiagnosticLPTrue(request, response, dataSampleJSONMsg);*/
            }           
/*            
            if (actionDiagnoses!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses[0].toString())){  
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, actionDiagnoses);   
            }else{
                RelatedObjects rObj=RelatedObjects.getInstanceForActions();
                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.INCIDENT.getTableName(), "incident", incId);                
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(endPoint.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), new Object[]{incId}, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
            }           */
        }catch(Exception e){   
            // Rdbms.closeRdbms();                   
            procReqInstance.killIt();
            String[] errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
        } finally {
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
