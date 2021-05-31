/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

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
public class EnvMonIncubationAPI extends HttpServlet {

    public enum EnvMonIncubationAPIEndpoints{
        /**
         *
         */
        EM_INCUBATION_ACTIVATE("EM_INCUBATION_ACTIVATE", "incubator_activate_success", 
            new LPAPIArguments[]{new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_INCUBATOR_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}),
        EM_INCUBATION_DEACTIVATE("EM_INCUBATION_DEACTIVATE", "incubator_deactivate_success", 
            new LPAPIArguments[]{new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_INCUBATOR_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}),
        EM_INCUBATION_ADD_TEMP_READING("EM_INCUBATION_ADD_TEMP_READING", "incubator_add_temp_reading_success", 
            new LPAPIArguments[]{new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_INCUBATOR_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_INCUBATOR_TEMPERATURE, LPAPIArguments.ArgumentType.BIGDECIMAL.toString(), true, 7)}),
        ;
        private EnvMonIncubationAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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

    
  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();

        String[] errObject = new String[]{"Servlet programAPI at " + request.getServletPath()};   

/*        Connection con = Rdbms.createTransactionWithSavePoint();        
      
        try {
            con.rollback();
            con.setAutoCommit(true);    
        } catch (SQLException ex) {
            Logger.getLogger(EnvMonAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
                    
//        try {
//            con.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(sampleAPI.class.getName()).log(Level.SEVERE, null, ex);
//        }
*/
        try (PrintWriter out = response.getWriter()) {

            EnvMonIncubationAPIEndpoints endPoint = null;
            try{
                endPoint = EnvMonIncubationAPIEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);
                return;
            }                
            ClassEnvMonIncubator clss=new ClassEnvMonIncubator(request, endPoint);
            Object[] diagnostic=clss.getDiagnostic();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
/*                Rdbms.rollbackWithSavePoint();
                if (!con.getAutoCommit()){
                    con.rollback();
                    con.setAutoCommit(true);}                */
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, diagnostic);   
            }else{
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), clss.getMessageDynamicData(), clss.getRelatedObj().getRelatedObject());                
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
            }               
/*            
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            Object[] messageDynamicData=new Object[]{};
            RelatedObjects rObj=RelatedObjects.getInstanceForActions();
            
            String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());    
            Rdbms.setTransactionId(schemaConfigName);        
            switch (endPoint){
                case EM_INCUBATION_ACTIVATE:
                    String instrName=argValues[0].toString();               
                    actionDiagnoses=ConfigIncubator.activateIncubator(procInstanceName, instrName, token.getPersonName());
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), "instrument_incubator", instrName);                
                    this.messageDynamicData=new Object[]{instrName};
                    break;
                case EM_INCUBATION_DEACTIVATE:
                    instrName=argValues[0].toString();
                    actionDiagnoses=ConfigIncubator.deactivateIncubator(procInstanceName, instrName, token.getPersonName());
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), "instrument_incubator", instrName);                
                    this.messageDynamicData=new Object[]{instrName};
                    break;
                case EM_INCUBATION_ADD_TEMP_READING:
                    instrName=argValues[0].toString();
                    String temperature=argValues[1].toString();
                    actionDiagnoses=DataIncubatorNoteBook.newTemperatureReading(procInstanceName, instrName, token.getPersonName(), new BigDecimal(temperature));                    
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), "instrument_incubator", instrName);                
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.TBL.getName(), "instrument_incubator_notebook", actionDiagnoses[actionDiagnoses.length-1]);                
                    this.messageDynamicData=new Object[]{temperature, instrName};
                    break;                    
                default:      
                    Rdbms.closeRdbms(); 
                    RequestDispatcher rd = request.getRequestDispatcher(SampleAPIParams.SERVLET_FRONTEND_URL);
                    rd.forward(request,response);  
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses[0].toString())){  
//                Rdbms.rollbackWithSavePoint();
//                if (!con.getAutoCommit()){
//                    con.rollback();
//                    con.setAutoCommit(true);}                
                rObj.killInstance();
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, actionDiagnoses);   
            }else{
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), messageDynamicData, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                
            } 
*/            
        }catch(Exception e){  
            procReqInstance.killIt();
            errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, errMsg);
        } finally {
            // release database resources
            try {                
                //con.close();
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
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
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
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
