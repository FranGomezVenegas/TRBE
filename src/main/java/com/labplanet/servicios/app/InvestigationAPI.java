/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import databases.TblsProcedure;
import functionaljavaa.investigation.ClassInvestigation;
import functionaljavaa.platform.doc.EndPointsToRequirements;
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
import trazit.enums.EnumIntEndpoints;
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
    public enum InvestigationAPIEndpoints implements EnumIntEndpoints{
        /**
         *
         */
        NEW_INVESTIGATION("NEW_INVESTIGATION", "investigationCreated_success",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 7 ),
                new LPAPIArguments(ParamsList.OBJECTS_TO_ADD.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.PROCEDURE.getName())
                .add("table", TblsProcedure.TablesProcedure.INVESTIGATION.getTableName()).build()).build()),
        CLOSE_INVESTIGATION("CLOSE_INVESTIGATION", "investigationClosed_success",  
            new LPAPIArguments[]{ new LPAPIArguments(ParamsList.INVESTIGATION_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.PROCEDURE.getName())
                .add("table", TblsProcedure.TablesProcedure.INVESTIGATION.getTableName()).build()).build()),
        INVESTIGATION_CAPA_DECISION("INVESTIGATION_CAPA_DECISION", "investigationDescisionTaken_success",  
            new LPAPIArguments[]{ new LPAPIArguments(ParamsList.INVESTIGATION_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(ParamsList.CAPA_REQUIRED.getParamName(), LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 7 ),
                new LPAPIArguments(ParamsList.CAPA_FIELD_NAME.getParamName(), LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8 ),
                new LPAPIArguments(ParamsList.CAPA_FIELD_VALUE.getParamName(), LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9 ),
                new LPAPIArguments(ParamsList.CLOSE_INVESTIGATION.getParamName(), LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 10 ),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.PROCEDURE.getName())
                .add("table", TblsProcedure.TablesProcedure.INVESTIGATION.getTableName()).build()).build()),
        ADD_INVEST_OBJECTS("ADD_INVEST_OBJECTS", "investObjectsAdded_success",  
            new LPAPIArguments[]{ new LPAPIArguments(ParamsList.INVESTIGATION_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(ParamsList.OBJECTS_TO_ADD.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.PROCEDURE.getName())
                .add("table", TblsProcedure.TablesProcedure.INVESTIGATION.getTableName()).build()).build()),
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

    
    public enum InvestigationAPIfrontendEndpoints implements EnumIntEndpoints{
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
        String[] errObject = new String[]{"Servlet programAPI at " + request.getServletPath()};   
        
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false, false);
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
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
                ClassInvestigation clssInv=new ClassInvestigation(request, endPoint);
                if (clssInv.getEndpointExists()){
                    Object[] diagnostic=clssInv.getDiagnostic();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
        /*                Rdbms.rollbackWithSavePoint();
                        if (!con.getAutoCommit()){
                            con.rollback();
                            con.setAutoCommit(true);}                */     
                        String errorCode =diagnostic[4].toString();
                        Object[] msgVariables=clssInv.getMessageDynamicData();
                        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, errorCode, msgVariables);               
    //                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, diagnostic);   
                    }else{
                        JSONObject dataSampleJSONMsg =new JSONObject();
                        if (endPoint!=null)
                            dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), clssInv.getMessageDynamicData(), clssInv.getRelatedObj().getRelatedObject());                

                        LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
                    }            
                }else{
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language);              
                    return;                                       
                }
        }catch(Exception e){   
 /*           try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(sampleAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
*/            
            procReqInstance.killIt();
            errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
        } finally {
            // release database resources
            try {
//                con.close();
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
