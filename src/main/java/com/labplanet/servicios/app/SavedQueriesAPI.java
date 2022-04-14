/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import databases.TblsData;
import databases.Token;
import functionaljavaa.businessrules.BusinessRules;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.savedqueries.SaveQueries;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class SavedQueriesAPI extends HttpServlet {  
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

    public enum SavedQueriesAPIEndpoints implements EnumIntEndpoints{
        CREATE_SAVED_QUERY("CREATE_SAVED_QUERY", "savedQueriesCreated_success", 
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments("definition", LPAPIArguments.ArgumentType.STRING.toString(), false, 7 ),
               new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 9 )}),
        UPDATE_SAVED_QUERY("UPDATE_SAVED_QUERY", "savedQueriesUpdated_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DB_USERNAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DB_PSSWD, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),
/*        CREATE("CREATE", "savedQueriesCreated_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DB_USERNAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DB_PSSWD, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}),*/

        ;      
        private SavedQueriesAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;            
        } 

        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+contentLine[lineIndex][curArg.getTestingArgPosic()]);
                request.setAttribute(curArg.getName(), contentLine[lineIndex][curArg.getTestingArgPosic()]);
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
        private final  LPAPIArguments[] arguments;

        public JsonArray getOutputObjectTypes() {
            return EndPointsToRequirements.endpointWithNoOutputObjects;
        }

    }

    public enum SavedQueriesAPIfrontendEndpoints implements EnumIntEndpoints{
        /**
         *
         */
        ALL_SAVED_QUERIES("ALL_SAVED_QUERIES", "",new LPAPIArguments[]{}),
        //INVESTIGATION_RESULTS_PENDING_DECISION("INVESTIGATION_RESULTS_PENDING_DECISION", "",new LPAPIArguments[]{}),
        //INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION("INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION", "",new LPAPIArguments[]{new LPAPIArguments(ParamsList.INVESTIGATION_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),}),
        ;
        private SavedQueriesAPIfrontendEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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

        public JsonArray getOutputObjectTypes() {
            return EndPointsToRequirements.endpointWithNoOutputObjects;
        }
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
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     */
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);        
        
        String language = LPFrontEnd.setLanguage(request); 
        String[] errObject = new String[]{"Servlet InvestigationAPI at " + request.getServletPath()};   

        String[] mandatoryParams = new String[]{""};
        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;          
        }             
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME); 
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode(), null, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;                             
        }
        mandatoryParams = null;                        
        BusinessRules bi=new BusinessRules(procInstanceName, null);
        Object[] procActionRequiresUserConfirmation = LPPlatform.procActionRequiresUserConfirmation(procInstanceName, actionName, bi);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresUserConfirmation[0].toString())){     
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK);    
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK);    
        }
        Object[] procActionRequiresEsignConfirmation = LPPlatform.procActionRequiresEsignConfirmation(procInstanceName, actionName, bi);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresEsignConfirmation[0].toString())){                                                      
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_ESIGN_TO_CHECK);    
        }        
        if (mandatoryParams!=null){
            areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, mandatoryParams);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;                  
            }     
        }
        if ( (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresUserConfirmation[0].toString())) &&     
             (!LPFrontEnd.servletUserToVerify(request, response, token.getUserName(), token.getUsrPw())) ){return;}

        if ( (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresEsignConfirmation[0].toString())) &&    
             (!LPFrontEnd.servletEsignToVerify(request, response, token.geteSign())) ){return;}
        
        if (mandatoryParams!=null){
            areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, mandatoryParams);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                       LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
               return;                   
            }     
        }
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;} 
        try (PrintWriter out = response.getWriter()) {
            Object[] actionEnabled = LPPlatform.procActionEnabled(procInstanceName, token, actionName, bi);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, actionEnabled);
                return ;               
            }            
            actionEnabled = LPPlatform.procUserRoleActionEnabled(procInstanceName, token.getUserRole(), actionName, bi);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){       
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, actionEnabled);
                return ;                           
            }  
            SavedQueriesAPIEndpoints endPoint = null;
            Object[] actionDiagnoses = null;
            try{
                endPoint = SavedQueriesAPIEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                return;                   
            }
            areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }                
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());  
            Integer svqQryId=null;
            switch (endPoint){
                case CREATE_SAVED_QUERY:                    
                    Object[] fieldValueArr=LPArray.convertStringWithDataTypeToObjectArray(argValues[3].toString().split(("\\|")));
                    if (fieldValueArr!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValueArr[0].toString()))
                        actionDiagnoses=fieldValueArr;
                    else
                        actionDiagnoses = SaveQueries.newSavedQuery(argValues[0].toString(), argValues[1].toString(), argValues[2].toString().split(("\\|")), fieldValueArr);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                        String savedQueryIdStr=actionDiagnoses[actionDiagnoses.length-1].toString();
                        if (savedQueryIdStr!=null && savedQueryIdStr.length()>0) svqQryId=Integer.valueOf(savedQueryIdStr);
                    }
                    break;
                case UPDATE_SAVED_QUERY:
                    //actionDiagnoses = Investigation.addInvestObjects(token, procInstanceName, Integer.valueOf(argValues[0].toString()), argValues[1].toString(), null);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                        String savedQueryIdStr=actionDiagnoses[actionDiagnoses.length-1].toString();
                        if (savedQueryIdStr!=null && savedQueryIdStr.length()>0) svqQryId=Integer.valueOf(savedQueryIdStr);
                    }
                    break;
            }    
            if (actionDiagnoses!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses[0].toString())){  
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, actionDiagnoses);   
            }else{
                RelatedObjects rObj=RelatedObjects.getInstanceForActions();
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAVED_QUERIES.getTableName(), svqQryId);                
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, new Object[]{svqQryId}, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
            }           
        }catch(Exception e){   
            // Rdbms.closeRdbms();                   
            errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
        } finally {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
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
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
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