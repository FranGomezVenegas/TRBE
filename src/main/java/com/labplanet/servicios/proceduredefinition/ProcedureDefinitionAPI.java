/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.proceduredefinition;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ProcedureDefinitionAPI extends HttpServlet {

    public enum ProcedureDefinitionpParametersEndpoints{
        /**
         *
         */
        PROCEDURE_NAME("procedureName"), PROCEDURE_VERSION("procedureVersion"), PROC_INSTANCENAME("procInstanceName"), MODULE_NAME("moduleName"), MODULE_VERSION("moduleVersion"), USER_NAME("userName"), ROLE_NAME("roleName"),
        UOM_NAME("uomName"),UOM_IMPORT_TYPE("importType"),
        CREATE_DATABASE("createDatabase"), CREATE_FILES("createFiles"), MAIN_PATH("mainPath"), 
        NEW_FILE_CAMEL_LOWER("newFileCamelLower"), NEW_FILE_CAMEL("newFileCamel"),
        NEW_FILE_PROC_NAME("newFileProcName"), NEW_FILE_ALIAS_UNDERSCORE("newAliasUnderscore"),
        CREATE_CHECKPLATFORM_PROCEDURE("createCheckPlatformProcedure"),
        REMOVE_CHECKPLATFORM_PROCEDURE("removeCheckPlatformProcedure"),
        DEPLOY_SCHEMAS_AND_PROC_TBLS("deploySchemasAndProcTbls"), DEPLOY_PROC_INFO("deployProcInfo"), DEPLOY_PROC_USER_ROLES("deployProcUserRoles"),
        DEPLOY_PROC_SOP_META_DATA("deployProcSopMetaData"), DEPLOY_PROC_SOPS_TO_USERS("deployProcSopsToUsers"), DEPLOY_PROC_EVENTS("deployProcEvents"),
        DEPLOY_PROC_BUSINESS_RULES_PROP_FILES("deployProcBusinessRulesPropFiles"), DEPLOY_MODULE_TABLES_AND_FIELDS("deployModuleTablesAndFields"),
        DEPLOY_PROC_MASTER_DATA("deployMasterData"),
        ;
        private ProcedureDefinitionpParametersEndpoints(String name){
            this.name=name;
        } 
        public String getName(){
            return this.name;
        }
        private final String name;
    }
    
    public enum ProcedureDefinitionAPIEndpoints{
        /**
         *
         */
        DEPLOY_REQUIREMENTS("DEPLOY_REQUIREMENTS", "deployRequirements_success", 
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.MODULE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_SCHEMAS_AND_PROC_TBLS.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 10),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_PROC_INFO.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_PROC_USER_ROLES.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 12),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_PROC_SOP_META_DATA.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 13),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_PROC_SOPS_TO_USERS.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 14),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_PROC_EVENTS.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 15),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_PROC_BUSINESS_RULES_PROP_FILES.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 16),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_MODULE_TABLES_AND_FIELDS.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 17),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.DEPLOY_PROC_MASTER_DATA.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 17)}),
        DEPLOY_FRONTEND("DEPLOY_FRONTEND", "deployFrontend_success", 
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.MODULE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.MODULE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 10),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.MAIN_PATH.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.NEW_FILE_CAMEL_LOWER.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 12),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.NEW_FILE_CAMEL.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 13),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.NEW_FILE_PROC_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 14),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.NEW_FILE_ALIAS_UNDERSCORE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 15),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.CREATE_FILES.getName(), LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 16)}),
        ADD_USER("ADD_USER", "addUserToProcedure_success",
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.USER_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9)}),
        ADD_ROLE_TO_USER("ADD_ROLE_TO_USER", "addRoleToUser_success", 
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.ROLE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.USER_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10)}),
        GET_UOM("GET_UOM", "addRoleToUser_success", 
                new LPAPIArguments[]{new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROCEDURE_VERSION.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.PROC_INSTANCENAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.UOM_NAME.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                new LPAPIArguments(ProcedureDefinitionpParametersEndpoints.UOM_IMPORT_TYPE.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10)}),
        ;
        private ProcedureDefinitionAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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
    }
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }
        String language = LPFrontEnd.setLanguage(request); 
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        
        String[] errObject = new String[]{"Servlet programAPI at " + request.getServletPath()};   

        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
            return;          
        }             
/*
//        String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME);            
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.ApiErrorTraping.INVALID_TOKEN.getName(), null, language);              
                return;                             
        }
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}
*/        
        ProcedureDefinitionAPIEndpoints endPoint = null;        
        try{
            endPoint = ProcedureDefinitionAPIEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
            return;                   
        }
        try (PrintWriter out = response.getWriter()) {
            ClassProcedureDefinition clss=new ClassProcedureDefinition(request, response, endPoint);
            Object[] diagnostic=clss.getDiagnostic();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
/*                Rdbms.rollbackWithSavePoint();
                if (!con.getAutoCommit()){
                    con.rollback();
                    con.setAutoCommit(true);}                */
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clss.getMessageDynamicData());   
            }else{
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), clss.getMessageDynamicData(), clss.getRelatedObj().getRelatedObject());                
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
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
            // Rdbms.closeRdbms();                   
            errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
        } finally {
            // release database resources
            try {
                procReqInstance.killIt();
                //con.close();
                // Rdbms.closeRdbms();   
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }      }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
         try {
        processRequest(request, response);
         }catch(ServletException|IOException e){Logger.getLogger(e.getMessage());}
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

