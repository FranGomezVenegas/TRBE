/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulegenoma;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import functionaljavaa.modulegenoma.GenomaDataAudit.DataGenomaProjectAuditEvents;
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
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntEndpoints;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class GenomaProjectAPI extends HttpServlet {
    
    public enum  GenomaProjectAPIParamsList{
        PROJECT_NAME("projectName"), STUDY_NAME("studyName"), INDIVIDUAL_NAME("individualName"), INDIVIDUAL_ID("individualId"), INDIVIDUALS_LIST("individualsList"), 
        FIELDS_NAMES("fieldsNames"), FIELDS_VALUES("fieldsValues"), SAMPLE_ID("sampleId"), SAMPLES_LIST("samplesList"), FAMILY_NAME("familyName"), 
        SAMPLES_SET_NAME("samplesSetName"), USER_NAME("userName"), USER_ROLE("userRole"), 
        VARIABLE_NAME("variableName"), VARIABLE_SET_NAME("variableSetName"), NEW_VALUE("newValue"),
        OWNER_TABLE("ownerTable"), OWNER_ID("ownerId");
        
        private GenomaProjectAPIParamsList(String name){
            this.paramName=name;
        } 
        String getParamName(){
            return this.paramName;
        }
        private final String paramName;
    }
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
       
    public enum GenomaProjectAPIEndPoints implements EnumIntEndpoints{
        PROJECT_NEW("PROJECT_NEW", "newProjectCreated_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.PROJECT_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_NAMES.getParamName(), LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_VALUES.getParamName(), LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8)}, null, DataGenomaProjectAuditEvents.NEW_PROJECT),
        PROJECT_ACTIVATE("PROJECT_ACTIVATE", "projectActivated_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.PROJECT_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, null, DataGenomaProjectAuditEvents.ACTIVATE_PROJECT),
        PROJECT_DEACTIVATE("PROJECT_DEACTIVATE", "projectDeactivated_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.PROJECT_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, null, DataGenomaProjectAuditEvents.DEACTIVATE_PROJECT),
        PROJECT_UPDATE("PROJECT_UPDATE", "projectUpdated_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.PROJECT_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_NAMES.getParamName(), LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_VALUES.getParamName(), LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8)}, null, DataGenomaProjectAuditEvents.UPDATED_PROJECT),
        PROJECT_ADD_USER("PROJECT_ADD_USER", "userAddedToProject_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.PROJECT_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_ROLE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, null, DataGenomaProjectAuditEvents.PROJECT_ADD_USER),
        PROJECT_REMOVE_USER("PROJECT_REMOVE_USER", "userRemovedToProject_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.PROJECT_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_ROLE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, null, DataGenomaProjectAuditEvents.PROJECT_REMOVE_USER),
        PROJECT_CHANGE_USER_ROLE("PROJECT_CHANGE_USER_ROLE", "projectUserRoleChanged_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.PROJECT_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_ROLE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, null, DataGenomaProjectAuditEvents.PROJECT_CHANGE_USER_ROLE),
        PROJECT_USER_ACTIVATE("PROJECT_USER_ACTIVATE", "userProjectActivated_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.PROJECT_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_ROLE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 8)}, null, DataGenomaProjectAuditEvents.PROJECT_USER_ACTIVATE),
        PROJECT_USER_DEACTIVATE("PROJECT_USER_DEACTIVATE", "userProjectDeactivated_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.PROJECT_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_ROLE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, null, DataGenomaProjectAuditEvents.PROJECT_USER_DEACTIVATE),
/*        STUDY_NEW("STUDY_NEW", "newStudyCreated_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.PROJECT_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_NAMES.getParamName(), LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_VALUES.getParamName(), LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 9)}, null, DataGenomaProjectAuditEvents.STUDY_ADDED),*/
        ;
        private GenomaProjectAPIEndPoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, EnumIntAuditEvents actionEventObj){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums; 
            this.outputObjectTypes=outputObjectTypes;            
            this.actionEventObj=actionEventObj;
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
        public EnumIntAuditEvents getAuditEventObj() {return actionEventObj;}
        
        private final String name;
        private final String successMessageCode;    
        private final  LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;   
        private final EnumIntAuditEvents actionEventObj;
    }
    
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

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        
        String[] errObject = new String[]{"Servlet Genoma ProjectAPI at " + request.getServletPath()};   

        GenomaProjectAPIEndPoints endPoint = null;
        try{
            endPoint = GenomaProjectAPIEndPoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;                   
        }
        Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }                
        //Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        
        String schemaConfigName = LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName());    
        Rdbms.setTransactionId(schemaConfigName);
        try (PrintWriter out = response.getWriter()) {
            ClassProject clss=new ClassProject(request, endPoint);
            Object[] diagnostic=clss.getDiagnostic();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clss.getMessageDynamicData());   
            }else{
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, clss.getMessageDynamicData(), clss.getRelatedObj().getRelatedObject());                
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
            }   
        }catch(Exception e){   
            response.setStatus(401);
            errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
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
