/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import databases.Rdbms;
import databases.TblsData;
import databases.Token;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.sop.UserSop;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import org.json.simple.JSONObject;
/**
 *
 * @author Administrator
 */
public class SopUserAPI extends HttpServlet {

    public enum SopUserAPIEndpoints{
        /**
         *
         */
        SOP_MARK_AS_COMPLETED("SOP_MARK_AS_COMPLETED", "sopName", "", "appSop_markAsCompleted_success",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6 )}),
        ;
        private SopUserAPIEndpoints(String name, String mandatoryParams, String optionalParams, String successMessageCode, LPAPIArguments[] argums){
            this.name=name;
            this.mandatoryParams=mandatoryParams;
            this.optionalParams=optionalParams;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;
        } 
        public String getName(){
            return this.name;
        }
        public String getMandatoryParams(){
            return this.mandatoryParams;
        }
        public String getSuccessMessageCode(){
            return this.successMessageCode;
        }           
        private String[] getEndpointDefinition(){
            return new String[]{this.name, this.mandatoryParams, this.optionalParams, this.successMessageCode};
        }
       /**
         * @return the arguments
         */
        public LPAPIArguments[] getArguments() {
            return arguments;
        }     
        private final String name;
        private final String mandatoryParams; 
        private final String optionalParams; 
        private final String successMessageCode;  
        public  LPAPIArguments[] arguments;
    }
    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        String language = LPFrontEnd.setLanguage(request); 

        String[] mandatoryParams = new String[]{""};
        Object[] areMandatoryParamsInResponse = LPHttp.areAPIMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
            return;          
        }                 
        String schemaPrefix = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX);  
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.API_ERRORTRAPING_INVALID_TOKEN, null, language);              
                return;                                
        }
        mandatoryParams = null;                        
        Object[] procActionRequiresUserConfirmation = LPPlatform.procActionRequiresUserConfirmation(schemaPrefix, actionName);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresUserConfirmation[0].toString())){     
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK);    
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK);    
        }
        Object[] procActionRequiresEsignConfirmation = LPPlatform.procActionRequiresEsignConfirmation(schemaPrefix, actionName);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresEsignConfirmation[0].toString())){                                                      
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_ESIGN_TO_CHECK);    
        }        
        if (mandatoryParams!=null){
            areMandatoryParamsInResponse = LPHttp.areAPIMandatoryParamsInApiRequest(request, mandatoryParams);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                return;                  
            }     
        }
        if ( (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresUserConfirmation[0].toString())) &&     
             (!LPFrontEnd.servletUserToVerify(request, response, token.getUserName(), token.getUsrPw())) ){return;}

        if ( (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresEsignConfirmation[0].toString())) &&    
             (!LPFrontEnd.servletEsignToVerify(request, response, token.geteSign())) ){return;}
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}     

            SopUserAPIEndpoints endPoint = null;
            try{
                endPoint = SopUserAPIEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());     
            Object[] messageDynamicData=new Object[]{};
/*       Connection con = Rdbms.createTransactionWithSavePoint();    
        try {
            con.rollback();
            con.setAutoCommit(true);    
        } catch (SQLException ex) {
            Logger.getLogger(SopUserAPI.class.getName()).log(Level.SEVERE, null, ex);
        }        
*/
        RelatedObjects rObj=RelatedObjects.getInstance();
        Object[] userSopDiagnostic=new Object[0];
        try (PrintWriter out = response.getWriter()) {        
            switch (endPoint){
            case SOP_MARK_AS_COMPLETED:
                String sopName = argValues[0].toString();
                String userName = token.getUserName();
                userSopDiagnostic=UserSop.userSopMarkedAsCompletedByUser(schemaPrefix, userName, sopName);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userSopDiagnostic[0].toString())){  
                    messageDynamicData=new Object[]{sopName, userName, schemaPrefix};
                }else{
                    messageDynamicData=new Object[]{sopName};                
                }
                rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsData.UserSop.TBL.getName(), TblsData.UserSop.TBL.getName(), sopName);
                break;
            default:                
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);              
                return;                                          
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userSopDiagnostic[0].toString())){  
/*                Rdbms.rollbackWithSavePoint();
                if (!con.getAutoCommit()){
                    con.rollback();
                    con.setAutoCommit(true);}                */
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, userSopDiagnostic[4].toString(), messageDynamicData);                
                //LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, userSopDiagnositc);   
            }else{                
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), messageDynamicData, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);     
                
            }                 
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_EXCEPTION_RAISED, new Object[]{e.getMessage(), this.getServletName()}, language);                   
        } finally {
            // release database resources
            try {
                //con.close();
                Rdbms.closeRdbms();   
            } catch (Exception ignore) {
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