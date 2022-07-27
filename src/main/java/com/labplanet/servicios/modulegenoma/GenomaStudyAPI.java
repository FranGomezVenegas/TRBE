/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulegenoma;

import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.modulegenoma.GenomaProjectAPI.GenomaProjectAPIParamsList;
import functionaljavaa.modulegenoma.GenomaDataAudit.DataGenomaStudyAuditEvents;
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
import trazit.session.ProcedureRequestSession;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class GenomaStudyAPI extends HttpServlet {

    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
            
    public enum GenomaStudyAPIEndPoints  implements EnumIntEndpoints{
        STUDY_NEW("STUDY_NEW", "newStudyCreated_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.PROJECT_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_NAMES.getParamName(), LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_VALUES.getParamName(), LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9)}, null, DataGenomaStudyAuditEvents.NEW_STUDY),
        STUDY_ACTIVATE("STUDY_ACTIVATE", "studyActivated_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, null, DataGenomaStudyAuditEvents.ACTIVATE_STUDY),
        STUDY_DEACTIVATE("STUDY_DEACTIVATE", "studyDeactivated_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}, null, DataGenomaStudyAuditEvents.DEACTIVATE_STUDY),
        STUDY_UPDATE("STUDY_UPDATE", "studyUpdated_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.PROJECT_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_NAMES.getParamName(), LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_VALUES.getParamName(), LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 9)}, null, DataGenomaStudyAuditEvents.UPDATE_STUDY),
        STUDY_ADD_USER("STUDY_ADD_USER", "userAddedToStudy_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_ROLE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, null, DataGenomaStudyAuditEvents.STUDY_ADD_USER),
        STUDY_REMOVE_USER("STUDY_REMOVE_USER", "userRemovedToStudy_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_ROLE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, null, DataGenomaStudyAuditEvents.STUDY_REMOVE_USER),
        STUDY_CHANGE_USER_ROLE("STUDY_CHANGE_USER_ROLE", "userStudyChangedRole_sucess", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_ROLE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, null, DataGenomaStudyAuditEvents.STUDY_CHANGE_USER_ROLE),
        STUDY_USER_ACTIVATE("STUDY_USER_ACTIVATE", "userStudyActivated_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_ROLE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 8)}, null, DataGenomaStudyAuditEvents.STUDY_USER_ACTIVATE),
        STUDY_USER_DEACTIVATE("STUDY_USER_DEACTIVATE", "userStudyDeactivated_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.USER_ROLE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, null, DataGenomaStudyAuditEvents.STUDY_USER_DEACTIVATE),
        STUDY_CREATE_INDIVIDUAL("STUDY_CREATE_INDIVIDUAL", "studyAddInvidividual_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.INDIVIDUAL_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_NAMES.getParamName(), LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_VALUES.getParamName(), LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),}, null, DataGenomaStudyAuditEvents.NEW_STUDY_INDIVIDUAL),
        STUDY_INDIVIDUAL_ACTIVATE("STUDY_INDIVIDUAL_ACTIVATE", "studyActivateInvidividual_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.INDIVIDUAL_ID.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, null, DataGenomaStudyAuditEvents.ACTIVATE_STUDY_INDIVIDUAL),
        STUDY_INDIVIDUAL_DEACTIVATE("STUDY_INDIVIDUAL_DEACTIVATE", "studyDeactivateInvidividual_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.INDIVIDUAL_ID.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, null, DataGenomaStudyAuditEvents.DEACTIVATE_STUDY_INDIVIDUAL),
        STUDY_CREATE_INDIVIDUAL_SAMPLE("STUDY_CREATE_INDIVIDUAL_SAMPLE", "studyAddInvidividualSample_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.INDIVIDUAL_ID.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, null, DataGenomaStudyAuditEvents.ACTIVATE_STUDY_INDIVIDUAL_SAMPLE),
        STUDY_INDIVIDUAL_SAMPLE_ACTIVATE("STUDY_INDIVIDUAL_SAMPLE_ACTIVATE", "studyActivateInvidividualSample_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.SAMPLE_ID.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.INDIVIDUAL_ID.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 8)}, null, DataGenomaStudyAuditEvents.ACTIVATE_STUDY_INDIVIDUAL_SAMPLE),
        STUDY_INDIVIDUAL_SAMPLE_DEACTIVATE("STUDY_INDIVIDUAL_SAMPLE_DEACTIVATE", "studyDeactivateInvidividualSample_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.INDIVIDUAL_ID.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.SAMPLE_ID.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, null, DataGenomaStudyAuditEvents.DEACTIVATE_STUDY_INDIVIDUAL_SAMPLE),
        STUDY_CREATE_FAMILY("STUDY_CREATE_FAMILY", "studyAddFamily_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.FAMILY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_NAMES.getParamName(), LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_VALUES.getParamName(), LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),
                new LPAPIArguments(GenomaProjectAPIParamsList.INDIVIDUALS_LIST.getParamName(), LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10)}, null, DataGenomaStudyAuditEvents.NEW_STUDY_FAMILY),
        STUDY_FAMILY_ACTIVATE("STUDY_FAMILY_ACTIVATE", "studyActivateFamily_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.FAMILY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, null, DataGenomaStudyAuditEvents.ACTIVATE_STUDY_FAMILY),
        STUDY_FAMILY_DEACTIVATE("STUDY_FAMILY_DEACTIVATE", "studyDeactivateFamily_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.FAMILY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, null, DataGenomaStudyAuditEvents.DEACTIVATE_STUDY_FAMILY),
        STUDY_FAMILY_ADD_INDIVIDUAL("STUDY_FAMILY_ADD_INDIVIDUAL", "studyFamilyAddIndividual_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.FAMILY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.INDIVIDUALS_LIST.getParamName(), LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_NAMES.getParamName(), LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_VALUES.getParamName(), LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 10)}, null, DataGenomaStudyAuditEvents.STUDY_FAMILY_ADDED_INDIVIDUAL),
        STUDY_FAMILY_REMOVE_INDIVIDUAL("STUDY_FAMILY_REMOVE_INDIVIDUAL", "studyFamilyRemoveIndividual_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.FAMILY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.INDIVIDUAL_ID.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),}, null, DataGenomaStudyAuditEvents.STUDY_FAMILY_REMOVED_INDIVIDUAL),
        STUDY_CREATE_SAMPLES_SET("STUDY_CREATE_SAMPLES_SET", "studyAddSampleSet_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.SAMPLES_SET_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.SAMPLES_LIST.getParamName(), LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_NAMES.getParamName(), LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                new LPAPIArguments(GenomaProjectAPIParamsList.FIELDS_VALUES.getParamName(), LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 10)}, null, DataGenomaStudyAuditEvents.NEW_STUDY_SAMPLES_SET),
        STUDY_SAMPLES_SET_ACTIVATE("STUDY_SAMPLES_SET_ACTIVATE", "studyActivateSampleSet_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.SAMPLES_SET_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, null, DataGenomaStudyAuditEvents.ACTIVATE_STUDY_SAMPLES_SET),
        STUDY_SAMPLES_SET_DEACTIVATE("STUDY_SAMPLES_SET_DEACTIVATE", "studyDeactivateSampleSet_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.SAMPLES_SET_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, null, DataGenomaStudyAuditEvents.DEACTIVATE_STUDY_SAMPLES_SET),
        STUDY_SAMPLES_SET_ADD_SAMPLE("STUDY_SAMPLES_SET_ADD_SAMPLE", "studySamplesSetAddSample_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.SAMPLES_SET_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.SAMPLE_ID.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, null, DataGenomaStudyAuditEvents.STUDY_SAMPLES_SET_ADDED_SAMPLE),
        STUDY_SAMPLES_SET_REMOVE_SAMPLE("STUDY_SAMPLES_SET_REMOVE_SAMPLE", "studySamplesSetRemoveSample_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),                
                new LPAPIArguments(GenomaProjectAPIParamsList.SAMPLES_SET_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.SAMPLE_ID.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, null, DataGenomaStudyAuditEvents.STUDY_SAMPLES_SET_REMOVED_SAMPLE),
        ADD_VARIABLE_SET_TO_STUDY_OBJECT("ADD_VARIABLE_SET_TO_STUDY_OBJECT", "variablesSetAdded_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GenomaProjectAPIParamsList.VARIABLE_SET_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.OWNER_TABLE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments(GenomaProjectAPIParamsList.OWNER_ID.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)}, null, null),
        ADD_VARIABLE_TO_STUDY_OBJECT("ADD_VARIABLE_TO_STUDY_OBJECT", "variableAdded_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GenomaProjectAPIParamsList.VARIABLE_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.OWNER_TABLE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments(GenomaProjectAPIParamsList.OWNER_ID.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)}, null, null),
        STUDY_OBJECT_SET_VARIABLE_VALUE("STUDY_OBJECT_SET_VARIABLE_VALUE", "variableValueEntered_success", 
                new LPAPIArguments[]{new LPAPIArguments(GenomaProjectAPIParamsList.STUDY_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GenomaProjectAPIParamsList.VARIABLE_SET_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GenomaProjectAPIParamsList.OWNER_TABLE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments(GenomaProjectAPIParamsList.OWNER_ID.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                new LPAPIArguments(GenomaProjectAPIParamsList.VARIABLE_NAME.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                new LPAPIArguments(GenomaProjectAPIParamsList.NEW_VALUE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 11)}, null, null),        
/*
          STUDY_SAMPLES_SET_ADD_SAMPLE("STUDY_SAMPLE_SET_ADD_SAMPLE", "studyName|samplesSetName|sampleId"), 
        STUDY_SAMPLES_SET_REMOVE_SAMPLE("STUDY_SAMPLE_SET_REMOVE_SAMPLE", "studyName|samplesSetName|sampleId"),
*/        
        ;
        private GenomaStudyAPIEndPoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, EnumIntAuditEvents actionEventObj){
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
/*        private GenomaStudyAPIEndPoints(String name, String successMessageCode, LPAPIArguments[] argums){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
        } 
        public HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){                
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }  
            hm.put(request, argValues);            
            return hm;
        }        
        @Override        public String getName(){            return this.name;        }
        @Override        public String getSuccessMessageCode(){            return this.successMessageCode;        }           
        @Override        public LPAPIArguments[] getArguments() {            return arguments;        }     
        @Override        public JsonArray getOutputObjectTypes() {            return EndPointsToRequirements.endpointWithNoOutputObjects;        }
        private final String name;
        private final String successMessageCode;  
        private final  LPAPIArguments[] arguments;
    }
    */
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
        GenomaStudyAPIEndPoints endPoint = null;
        try{
            endPoint = GenomaStudyAPIEndPoints.valueOf(actionName.toUpperCase());
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
        try (PrintWriter out = response.getWriter()) {
            ClassStudy clss=new ClassStudy(request, endPoint);
            Object[] diagnostic=clss.getDiagnostic();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clss.getMessageDynamicData());   
            }else{
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, clss.getMessageDynamicData(), clss.getRelatedObj().getRelatedObject());                
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
            }   
        }catch(Exception e){   
            response.setStatus(401);
            procReqInstance.killIt();
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
