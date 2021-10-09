/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import databases.TblsApp;
import databases.TblsProcedure;
import functionaljavaa.investigation.Investigation;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class InvestigationAPI extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    
    static final String COMMON_PARAMS="investigationId|note";
    public enum InvestigationAPIEndpoints{
        /**
         *
         */
        NEW_INVESTIGATION("NEW_INVESTIGATION", "investigationCreated_success",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 7 ),
                new LPAPIArguments(ParamsList.OBJECTS_TO_ADD.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.PROCEDURE.getName())
                .add("table", TblsProcedure.Investigation.TBL.getName()).build()).build()),
        CLOSE_INVESTIGATION("CLOSE_INVESTIGATION", "investigationClosed_success",  
            new LPAPIArguments[]{ new LPAPIArguments(ParamsList.INVESTIGATION_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.PROCEDURE.getName())
                .add("table", TblsProcedure.Investigation.TBL.getName()).build()).build()),
        INVESTIGATION_CAPA_DECISION("INVESTIGATION_CAPA_DECISION", "investigationDescisionTaken_success",  
            new LPAPIArguments[]{ new LPAPIArguments(ParamsList.INVESTIGATION_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(ParamsList.CAPA_REQUIRED.getParamName(), LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 7 ),
                new LPAPIArguments(ParamsList.CAPA_FIELD_NAME.getParamName(), LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8 ),
                new LPAPIArguments(ParamsList.CAPA_FIELD_VALUE.getParamName(), LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9 ),
                new LPAPIArguments(ParamsList.CLOSE_INVESTIGATION.getParamName(), LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 10 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.PROCEDURE.getName())
                .add("table", TblsProcedure.Investigation.TBL.getName()).build()).build()),
        ADD_INVEST_OBJECTS("ADD_INVEST_OBJECTS", "investObjectsAdded_success",  
            new LPAPIArguments[]{ new LPAPIArguments(ParamsList.INVESTIGATION_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(ParamsList.OBJECTS_TO_ADD.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.PROCEDURE.getName())
                .add("table", TblsProcedure.Investigation.TBL.getName()).build()).build()),
/*        ADD_NOTE_INVESTIGATION("ADD_NOTE_INVESTIGATION", "investigationNoteAdded_success",  
            new LPAPIArguments[]{ new LPAPIArguments(ParamsList.INVESTIGATION_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(ParamsList.NOTE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
            new LPAPIArguments(ParamsList.NEW_STATUS.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 7)}),*/
        ;
        private InvestigationAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;
            this.outputObjectTypes=outputObjectTypes;                        
        } 
        public String getName(){return this.name;}
        public String getSuccessMessageCode(){return this.successMessageCode;}           
        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        public LPAPIArguments[] getArguments() {return arguments;}
        private final String name;
        private final String successMessageCode;       
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }

    
    public enum InvestigationAPIfrontendEndpoints{
        /**
         *
         */
        OPEN_INVESTIGATIONS("OPEN_INVESTIGATIONS", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects ),
        INVESTIGATION_RESULTS_PENDING_DECISION("INVESTIGATION_RESULTS_PENDING_DECISION", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects ),
        INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION("INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION", "",new LPAPIArguments[]{new LPAPIArguments(ParamsList.INVESTIGATION_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects),
        ;
        private InvestigationAPIfrontendEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
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
        public String getName(){return this.name;}
        public String getSuccessMessageCode(){return this.successMessageCode;}           
        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        public LPAPIArguments[] getArguments() {return arguments;}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }

    public enum ParamsList{INVESTIGATION_ID("investigationId"), OBJECTS_TO_ADD("objectsToAdd"),    
        CAPA_REQUIRED("capaRequired"), CAPA_FIELD_NAME("capaFieldName"), CAPA_FIELD_VALUE("capaFieldValue"), CLOSE_INVESTIGATION("closeInvestigation")
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
            InvestigationAPIEndpoints endPoint = null;
            Object[] actionDiagnoses = null;
        
            try{
                endPoint = InvestigationAPIEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());  
            Integer incId=null;
            switch (endPoint){
                case NEW_INVESTIGATION:                    
                    actionDiagnoses = Investigation.newInvestigation(argValues[0].toString().split(("\\|")), LPArray.convertStringWithDataTypeToObjectArray(argValues[1].toString().split(("\\|"))), argValues[2].toString());
                    String investigationIdStr="";
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        investigationIdStr=actionDiagnoses[actionDiagnoses.length-1].toString();
                    if (investigationIdStr!=null && investigationIdStr.length()>0) incId=Integer.valueOf(investigationIdStr);
                    break;
                case ADD_INVEST_OBJECTS:
                    actionDiagnoses = Investigation.addInvestObjects(Integer.valueOf(argValues[0].toString()), argValues[1].toString(), null);
                    investigationIdStr=argValues[0].toString();
                    if (investigationIdStr!=null && investigationIdStr.length()>0) incId=Integer.valueOf(investigationIdStr);
                    break;
                case CLOSE_INVESTIGATION:
                    actionDiagnoses = Investigation.closeInvestigation(Integer.valueOf(argValues[0].toString()));
                    investigationIdStr=argValues[0].toString();
                    if (investigationIdStr!=null && investigationIdStr.length()>0) incId=Integer.valueOf(investigationIdStr);
                    break;
                case INVESTIGATION_CAPA_DECISION:
                    String[] capaFldName=null;
                    String[] capaFldValue=null;
                    if (argValues[1]==null) LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{ParamsList.CAPA_REQUIRED.getParamName()}, language);
                            
                    if (argValues[2]!=null && argValues[2].toString().length()>0) capaFldName=argValues[2].toString().split("\\|");
                    if (argValues[3]!=null && argValues[3].toString().length()>0) capaFldValue=argValues[3].toString().split("\\|");
                    actionDiagnoses = Investigation.capaDecision(Integer.valueOf(argValues[0].toString()), 
                            Boolean.valueOf(argValues[1].toString()), capaFldName, capaFldValue);
                    investigationIdStr=argValues[0].toString();
                    if (investigationIdStr!=null && investigationIdStr.length()>0) incId=Integer.valueOf(investigationIdStr);
                    break;
            }    
            if (actionDiagnoses!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses[0].toString())){  
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, actionDiagnoses);   
            }else{
                RelatedObjects rObj=RelatedObjects.getInstanceForActions();
                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsApp.Incident.TBL.getName(), "investigation", incId);                
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), new Object[]{incId}, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
            }           
        }catch(Exception e){   
            procReqInstance.killIt();            
            // Rdbms.closeRdbms();                   
            String[] errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
        } finally {
            procReqInstance.killIt();
            // release database resources
            try {                
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
        try {
            processRequest(request, response);
        } catch (IOException ex) {
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
