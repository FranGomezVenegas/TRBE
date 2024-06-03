/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.clinicalstudies.apis;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static module.clinicalstudies.apis.GenomaConfigVariableAPI.GenomaVariableAPIactionsEndpoints.VARIABLE_SET_ADD_VARIABLE;
import module.clinicalstudies.apis.GenomaProjectAPI.GenomaProjectAPIParamsList;
import functionaljavaa.modulegenoma.ClinicalStudyConfigVariables;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.clinicalstudies.definition.TblsGenomaConfig;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntEndpoints;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import trazit.session.InternalMessage;
/**
 *
 * @author User
 */
public class GenomaConfigVariableAPI extends HttpServlet {

    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
            
    public enum GenomaVariableAPIactionsEndpoints implements EnumIntEndpoints {
          VARIABLE_SET_ADD_VARIABLE("VARIABLE_SET_ADD_VARIABLE", "variableSetName|variableName", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects
                  , null, null, TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET.getTableName()),
          VARIABLE_SET_REMOVE_VARIABLE("VARIABLE_SET_REMOVE_VARIABLE", "variableSetName|variableName", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects
                  , null, null, TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET.getTableName())
          ;
        private GenomaVariableAPIactionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag
        , String entity) {
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
            this.outputObjectTypes=outputObjectTypes;            
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);
            this.entityName=entity;
        } 
          @Override                public String getName(){return this.name;}
          @Override                public String getSuccessMessageCode(){return this.successMessageCode;}           
          @Override                public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
          @Override                public LPAPIArguments[] getArguments() {return arguments;}
          @Override                public String getApiUrl(){return ApiUrls.GENOMA_VARIABLE_ACTIONS.getUrl();}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
          @Override         public String getDeveloperComment() { return this.devComment;}
          @Override        public String getDeveloperCommentTag() {            return this.devCommentTag;        }
        private final String devComment;
        private final String devCommentTag;
        private final String entityName;
        @Override        public String getEntity() {return entityName;}
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
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        String[] errObject = new String[]{"Servlet Genoma VariableAPI at " + request.getServletPath()};   
        try (PrintWriter out = response.getWriter()) {
            InternalMessage actionDiagnoseObj = null;
                    GenomaVariableAPIactionsEndpoints endPoint = null;
                    try{
                        endPoint = GenomaVariableAPIactionsEndpoints.valueOf(actionName.toUpperCase());
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
            
            switch (endPoint){
                case VARIABLE_SET_ADD_VARIABLE:
                case VARIABLE_SET_REMOVE_VARIABLE:     
                    String variableSetName=request.getParameter(GenomaProjectAPIParamsList.VARIABLE_SET_NAME.getParamName());
                    String variableName=request.getParameter(GenomaProjectAPIParamsList.VARIABLE_NAME.getParamName());
                    if (VARIABLE_SET_ADD_VARIABLE.equals(endPoint))
                        actionDiagnoseObj =ClinicalStudyConfigVariables.variableSetAddVariable(variableSetName, variableName);
                    else if ("VARIABLE_SET_REMOVE_VARIABLE".equalsIgnoreCase(actionName))
                        actionDiagnoseObj =ClinicalStudyConfigVariables.variableSetRemoveVariable(variableSetName, variableName);
                    else
                        LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                    break;       
                default:      
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                    return;                    
            }    
            if (actionDiagnoseObj!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoseObj.getDiagnostic())){  
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response,  actionDiagnoseObj.getMessageCodeObj(), actionDiagnoseObj.getMessageCodeVariables());   
            }else{              
                LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, actionDiagnoseObj.getMessageCodeVariables(), new JSONArray());                
            }            
        }catch(Exception e){   
            response.setStatus(401);
            errObject = new String[]{e.getMessage()};
            LPFrontEnd.responseError(errObject);
        } finally {
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
